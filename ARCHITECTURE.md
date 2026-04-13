# Kitchen Chaos — Code Architecture

> Reference document for the component-based entity system and project structure.
> Update this when new systems, components, or patterns are introduced.

---

## Package Structure

```
io.github.andreytondo/
├── KitchenChaos.java          # LibGDX Game entry point; owns the AssetManager
├── screen/                    # Game screens (one per game state)
│   └── GameScreen.java        # Temporary test scaffold — will be replaced
├── entity/                    # Game objects (player, enemies, etc.)
│   ├── BaseEntity.java
│   ├── BaseActor.java
│   ├── Player.java
│   └── TomatoEnemy.java
├── component/                 # Reusable behaviors attached to entities
│   ├── BasicMovement.java
│   ├── PlayerInput.java
│   ├── Dash.java
│   ├── Attack.java
│   ├── Health.java
│   ├── MeleeChaseAI.java
│   └── RangedKiteAI.java
├── contract/                  # Interfaces (capabilities)
│   ├── Renderable.java
│   ├── Movement.java
│   ├── Updatable.java
│   ├── EnemyBehavior.java
│   ├── HasHealth.java
│   └── HasPosition.java
└── utils/
    ├── Assets.java            # String constants for all asset paths
    ├── Constants.java
    └── GameRenderer.java
```

---

## Design Philosophy

The project uses **composition over inheritance**. Entities are thin shells that hold
component instances. Behavior lives in the components, not in the entity class itself.
Interfaces define capabilities; entities declare which capabilities they expose.

```
Entity = data (position, size) + references to components
Component = a single isolated behavior (movement, health, input, AI...)
Interface = a contract declaring the entity has a certain capability
```

---

## Contracts (Interfaces)

### `HasPosition`
```java
Vector2 getPosition();
float getX();
float getY();
```
Any object with a position in world space. Used by `Attack` to measure distance
between attacker and target without needing a full `BaseActor` reference.

---

### `HasHealth`
```java
void takeDamage(float amount);   // default: delegates to getHealth()
boolean isDead();                // default: delegates to getHealth()
Health getHealth();              // abstract
```
Default methods forward to `getHealth()`, so implementing classes only need to
expose their `Health` component.

---

### `Updatable`
```java
void update(float delta);
```
Anything that participates in the game loop. `GameScreen` calls `update(delta)` on
all active `Updatable` entities each frame.

---

### `Renderable`
```java
void render(GameRenderer renderer);
```
Anything that draws itself. Separate from `Updatable` — not every game object
necessarily renders (e.g. a trigger zone is `Updatable` but not `Renderable`).

---

### `Movement`
```java
void move(Vector2 position, Vector2 direction, float speed, float delta);
```
Low-level movement strategy. Currently only `BasicMovement` implements this.
Designed to allow future alternatives (physics-based, grid-snapped, etc.).

---

### `EnemyBehavior`
```java
void execute(BaseActor target, BaseActor self, float delta);
```
AI strategy interface. Each enemy holds one `EnemyBehavior` and calls
`behavior.execute(target, self, delta)` from its own `update()`. Swapping the
behavior instance changes the AI without touching the entity class.

---

## Entity Hierarchy

```
BaseEntity          (position, width, height, active flag)
    └── BaseActor   (+ speed, facingDirection, BasicMovement, Health)
            ├── Player      (+ PlayerInput, Dash, Attack, Texture)
            └── TomatoEnemy (+ EnemyBehavior, Attack, Texture)
```

### `BaseEntity`
- Holds the `Vector2 position`, `width`, `height`, `active` flag
- Implements `HasPosition`
- Provides `getX()` / `getY()` convenience accessors
- `active = false` signals the entity should be skipped by the game loop

### `BaseActor`
- Extends `BaseEntity`, implements `Updatable` and `HasHealth`
- Holds `speed`, `facingDirection`, a `BasicMovement` component, and a `Health` component
- `move(direction, delta)` normalizes the direction into `facingDirection`, then
  delegates to the `Movement` component
- Overload `move(direction, effectiveSpeed, delta)` lets callers pass a modified
  speed (e.g. dash multiplier applied before calling)

### `Player`
- Owns: `PlayerInput`, `Dash`, `Attack`, `Texture`
- `update(delta)`:
  1. Check death → set inactive
  2. Poll input for dash/attack presses
  3. Tick `Dash` and `Attack` timers
  4. Read movement direction from `PlayerInput`
  5. Call `move()` with `dash.applyTo(speed)` as the effective speed
  6. Clamp position to world bounds
- `tryAttack(BaseActor target)` — public; called by `GameScreen` per enemy

### `TomatoEnemy`
- Owns: `EnemyBehavior` (currently `MeleeChaseAI`), `Attack`, `Texture`
- `update(delta)`:
  1. Check death → set inactive
  2. Delegate entirely to `behavior.execute(target, self, delta)`
- The behavior owns the `Attack` instance and manages its own cooldown timer

---

## Components

### `BasicMovement`
```
position += normalize(direction) * speed * delta
```
No-ops if direction is null or zero. The only movement implementation currently.

---

### `Health`
| Method | Behavior |
|---|---|
| `takeDamage(amount)` | `currentHealth = max(0, current - amount)` |
| `heal(amount)` | `currentHealth = min(max, current + amount)` |
| `isDead()` | `currentHealth <= 0` |
| `getPercent()` | `currentHealth / maxHealth` — used for health bar rendering |

---

### `Dash`
State machine with two timers:

```
READY ──[tryActivate()]──> ACTIVE (activeTimer = duration)
ACTIVE ──[timer expires]──> COOLDOWN (cooldownTimer = cooldown)
COOLDOWN ──[timer expires]──> READY
```

- `applyTo(baseSpeed)` returns `baseSpeed * multiplier` while active, else `baseSpeed`
- `isActive()` — used externally to grant i-frames during the active window

---

### `Attack`
- Holds `damage`, `range`, `cooldown`, and a `cooldownTimer`
- `tryAttack(target, self)`:
  1. Bail if `cooldownTimer > 0`
  2. Measure `target.getPosition().dst(self.getPosition())`
  3. Bail if `dist > range`
  4. Call `target.takeDamage(damage)`, reset `cooldownTimer`
- Used by both `Player` (via `Player.tryAttack()`) and enemies (via AI components)
- `update(delta)` must be called each frame to tick the cooldown timer down

---

### `PlayerInput`
Thin wrapper around `Gdx.input` calls. Returns normalized values, not raw keys.

| Method | Keys |
|---|---|
| `getMovementDirection()` | WASD + Arrow keys → `Vector2` |
| `isDashPressed()` | `isKeyJustPressed` SHIFT_LEFT / SHIFT_RIGHT |
| `isAttackPressed()` | `isKeyPressed` SPACE |

Currently keyboard-only. Mouse aiming support (for `facingDirection` and ranged
weapons) has not been added yet.

---

### `MeleeChaseAI`
Each frame:
1. Tick attack cooldown (`attack.update(delta)`)
2. Compute normalized direction from self → target
3. Try to attack target (if in range and ready)
4. Move toward target

The enemy always moves, even while attacking — behavior by design for a fast chaser.

---

### `RangedKiteAI`
Each frame:
1. Tick attack cooldown
2. Measure distance to target
3. If `dist < preferredDistance` → move **away** from target
4. If `dist > preferredDistance + 50` → move **toward** target
5. Try to attack

Not yet used by any enemy class. Intended for `BroccoliEnemy` or similar ranged types.

---

## Utilities

### `Constants`
All game balance values in one place. Values are expressed as fractions of world
dimensions where applicable so they scale consistently.

```java
WORLD_WIDTH / HEIGHT       // 1920 x 1080
PLAYER_SIZE                // WORLD_WIDTH * 0.083  ≈ 160 px
PLAYER_SPEED               // WORLD_WIDTH * 0.172  ≈ 330 px/s
PLAYER_HEALTH              // 1000
PLAYER_DASH_DURATION       // 0.2 s
PLAYER_DASH_COOLDOWN       // 1.0 s
PLAYER_DASH_MULTIPLIER     // 3.5x
PLAYER_ATTACK_DAMAGE       // 25
PLAYER_ATTACK_RANGE        // PLAYER_SIZE * 2 ≈ 320 px
PLAYER_ATTACK_COOLDOWN     // 0.5 s
TOMATO_SIZE                // WORLD_WIDTH * 0.075 ≈ 144 px
TOMATO_SPEED               // same as player
TOMATO_HEALTH              // 100
```

---

### `Assets`
String constants for every asset path (`PLAYER`, `TOMATO`, …). All `AssetManager`
load/get calls reference these constants — no magic strings anywhere else.

---

### `GameRenderer`
Holds and exposes a `SpriteBatch` and a `ShapeRenderer`. Single instance passed
into every `render()` call. Centralizes resource ownership and `dispose()`.

---

## Adding a New Enemy Type (Pattern)

1. Add an `Assets.YOUR_ENEMY = "yourenemy.png"` constant to `Assets.java`
2. Load it in `KitchenChaos.create()`: `assets.load(Assets.YOUR_ENEMY, Texture.class)`
3. Create `YourEnemy extends BaseActor implements Renderable` with a `Texture` constructor param
4. In the constructor, instantiate an `Attack` and pass it to a behavior:
   ```java
   Attack attack = new Attack(damage, range, cooldown);
   this.behavior = new MeleeChaseAI(attack);   // or RangedKiteAI
   ```
5. `update(delta)`: death check → `behavior.execute(target, this, delta)`
6. `render(renderer)`: draw texture
7. Add balance constants to `Constants.java`
8. Pass the retrieved texture from `GameScreen` when constructing the enemy

---

## Adding a New AI Behavior (Pattern)

1. Create `YourAI implements EnemyBehavior`
2. Implement `execute(BaseActor target, BaseActor self, float delta)`
3. The AI owns its own `Attack` instance and must call `attack.update(delta)` itself
4. Inject via constructor into whichever enemy uses it

---

## Assets

```
assets/
├── player.png          # Player sprite
├── player.aseprite     # Aseprite source (animations not loaded yet)
├── tomato.png          # Tomato enemy sprite
├── tomato.aseprite
└── ui/
    ├── uiskin.json / .png / .atlas   # LibGDX Scene2D UI skin
    └── font*.fnt                     # Bitmap fonts
```

Textures are loaded once in `KitchenChaos.create()` via `AssetManager`, then
retrieved by `GameScreen` and injected into entity constructors as plain
`Texture` references. Entities do not own or dispose their textures.
`KitchenChaos.dispose()` disposes the `AssetManager`, which frees all textures.
