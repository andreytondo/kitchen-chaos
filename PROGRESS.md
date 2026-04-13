# Kitchen Chaos — Development Progress

> Living document. Update the relevant section after each work session.
> Cross-reference the GDD (`Kitchen_Chaos_GDD.md`) for full design specs.

---

## Current Sprint: Sprint 1 — Foundation
**Goal:** Core movement, combat feel, one enemy, one room (test scaffold).

---

## Status Legend
- `[x]` Done
- `[~]` Partially done / needs polish
- `[ ]` Not started

---

## Sprint 1 — Foundation (Weeks 1–4)

### Player
- [x] Movement — WASD + Arrow keys
- [x] Dash/dodge — Shift, 0.2 s active, 1.0 s cooldown, 3.5× speed
- [~] Attack — Space key, range-based melee, 0.5 s cooldown
  - Attack direction uses `facingDirection` (set by movement)
  - Mouse aiming not yet implemented (needed for directional attacks and future ranged weapons)
- [x] Health system — `Health` component with damage, heal, death
- [x] World boundary clamping

### Enemies
- [x] `TomatoEnemy` — melee chaser, 100 HP
- [x] `MeleeChaseAI` — moves toward target, attacks on contact
- [x] `RangedKiteAI` — implemented, not yet used by any enemy

### Core Systems
- [x] Component architecture — `Attack`, `Health`, `Dash`, `BasicMovement`, `PlayerInput`
- [x] Interface contracts — `Renderable`, `Updatable`, `EnemyBehavior`, `HasHealth`, `HasPosition`, `Movement`
- [x] AABB collision resolution — player↔enemy and enemy↔enemy push-apart
- [x] Health bar rendering — above each entity via `ShapeRenderer`
- [x] `Constants.java` — all balance values centralized
- [x] `GameRenderer` — shared `SpriteBatch` + `ShapeRenderer`

### Sprint 1 — Known Issues / Remaining Work
- [ ] **Enemy world boundary clamping** — enemies can be pushed off-screen; mirror `clampToWorld()` from `Player`
- [ ] **Mouse aiming** — `facingDirection` should point toward mouse cursor; required for directional melee and all future ranged weapons
- [ ] **Game-over / restart screen** — death sets player inactive but nothing happens; needs a `GameOverScreen`
- [ ] **Start / main menu screen** — no entry screen yet
- [x] **Window title** — set to `"Kitchen Chaos"` in `Lwjgl3Launcher`
- [x] **`AssetManager`** — textures loaded once in `KitchenChaos`, injected into entities via constructor; `Assets.java` holds all path constants

---

## Sprint 2 — Core Systems (Weeks 5–8)

### Room & World Structure
- [ ] `Room` class — replaces hardcoded GameScreen setup; holds a list of entities + room state
- [ ] Room transitions / doors — enter a door to load the next room
- [ ] Procedural floor generation — connect rooms via door graph (BSP or preset room templates)
- [ ] Enemy spawner — define spawn lists per room type; decouple from GameScreen

### Combat Expansion
- [ ] Weapon system — `Weapon` interface / base class; player holds a current weapon
- [ ] Weapon: Frigideira (frying pan) — high damage melee, arc sweep, knockback
- [ ] Weapon: Faca de Chef (chef knife) — fast melee, 2 hits/s
- [ ] Projectile system — pooled projectile entities with velocity + lifetime

### Enemy Variety (6 types total, 1 done)
- [x] Tomate Raivoso — MeleeChaseAI, fast, low HP
- [ ] Frango Zumbi — high HP tank, slow, line attack
- [ ] Brócolis Atirador — stationary, spread projectile pattern (uses RangedKiteAI)
- [ ] Cebola Explosiva — chaser, explodes on contact
- [ ] Cogumelo Clonador — summons copies of nearby enemies
- [ ] Camarão Veloz — swarm type, minimal HP, very fast

### Economy
- [ ] Coin (Pimenta de Ouro) drop on enemy death
- [ ] Coin pickup / counter
- [ ] Shop room — spend coins on items/upgrades

### Boss
- [ ] Chef Tomate Bravão (Floor 1 boss) — spawns mini-tomatoes, throws seeds

### Recipe / Ingredient System
- [ ] Ingredient item entity — collectible pickup
- [ ] Player ingredient inventory (holds up to 2)
- [ ] Combination system — map `(ingredientA, ingredientB)` → effect
- [ ] 8 recipes from GDD (Rastilho de Fogo, Aura Repelente, Lâmina Ácida, etc.)

---

## Sprint 3 — Content (Weeks 9–14)

### World
- [ ] Floor 2: Câmara Fria — new enemy set, Frango Congelado boss
- [ ] Floor 3: Salão Principal — new enemy set, Maestro dos Garfos boss
- [ ] Room types: Despensa, Loja do Tempero, Copa, Sala Misteriosa, Sala do Boss

### Remaining Weapons
- [ ] Concha (ladle) — ranged, throws hot soup projectile
- [ ] Saleiro (salt shaker) — shotgun spread of micro-projectiles
- [ ] Balde d'Água (water bucket) — slow projectile, roots enemy
- [ ] Garfo Gigante (giant fork) — slow heavy melee, pierces through enemies

### Passive Items
- [ ] Item pickup entity
- [ ] Inventory / item effect application
- [ ] 7+ items from GDD (Avental Reforçado, Faca Serrilhada, Tênis de Borracha, etc.)

### Art & Animation
- [ ] Animation system — load sprite sheets from Aseprite exports
- [ ] Player walk / idle / attack / dash animations
- [ ] Enemy animations per type
- [ ] Visual effects: hit flash, death pop, projectile trail

### Audio
- [ ] Sound effect integration (`AssetManager` for audio)
- [ ] SFX: attack hit, dash, enemy death, coin pickup, door open
- [ ] Background music — jazz / chaotic kitchen theme

### UI / HUD
- [ ] Heart display (6 hearts = 12 HP, half-heart increments)
- [ ] Weapon icon + ammo/cooldown indicator
- [ ] Coin counter
- [ ] Room map / floor map overlay
- [ ] Pause menu (ESC/P)
- [ ] Main menu screen

---

## Sprint 4 — Polish (Weeks 15–18)

### Content
- [ ] Floor 4: Cozinha do Chef — elite enemies, Chef Fantasma Final (3-phase boss)

### Meta-Progression
- [ ] Cookbook — unlocked recipes persist between runs
- [ ] Bestiário — enemies seen are logged permanently
- [ ] Difficulty mode: Inferno — unlocked after first clear
- [ ] Cosmetic chef skins — unlocked by achievements

### Game Feel (Juice)
- [ ] Screen shake on heavy hits
- [ ] Hit-stop (brief freeze frame on impactful hits)
- [ ] Particles: coin burst, blood/sauce splatter, dust on dash
- [ ] Camera zoom / pulse on boss entry

### Extras (if time allows)
- [ ] High score board (local)
- [ ] Achievement system
- [ ] Co-op local (2 players)

---

## Architecture Debts to Resolve Before Sprint 2

These are not bugs, but structural gaps that will block Sprint 2 work if not addressed:

| Debt | Why it matters |
|---|---|
| `GameScreen` holds `List<TomatoEnemy>` directly | Cannot mix enemy types; needs `List<Enemy>` or `List<BaseActor>` |
| ~~No `AssetManager`~~ | **Resolved** — `KitchenChaos` owns `AssetManager`; textures injected via constructor |
| No entity factory | Spawning new enemy types requires editing the screen class |
| `PlayerInput.isAttackPressed()` uses Space | Should use mouse left-click; Space is a temporary placeholder |
| No room abstraction | Everything in one screen; prerequisite for procedural generation |

---

## Notes

_Use this section for session notes, decisions, or anything that doesn't fit above._

- `GameScreen.java` is a temporary test scaffold and will be replaced entirely when the room system is built.
- `RangedKiteAI` is ready to use — wire it to `BroccoliEnemy` in Sprint 2.
- GDD specifies 1.5 s dash cooldown; current implementation uses 1.0 s — decide which to keep.
