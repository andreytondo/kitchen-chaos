package io.github.andreytondo.component;

public enum WeaponType {
    FRYING_PAN  ("Frigideira",    35f, 220f, 0.35f, true,  1, 0f),
    CHEF_KNIFE  ("Faca de Chef",  15f, 190f, 0.15f, true,  1, 0f),
    LADLE       ("Concha",        20f,   0f, 0.50f, false, 1, 0f),
    SALT_SHAKER ("Saleiro",       12f,   0f, 0.60f, false, 5, 28f),
    GIANT_FORK  ("Garfo Gigante", 55f, 250f, 0.70f, true,  1, 0f);

    public final String  displayName;
    public final float   damage;
    public final float   meleeRange;
    public final float   cooldown;
    public final boolean isMelee;
    public final int     projectileCount;
    public final float   spreadDegrees;

    WeaponType(String name, float damage, float meleeRange, float cooldown,
               boolean isMelee, int projectileCount, float spreadDegrees) {
        this.displayName     = name;
        this.damage          = damage;
        this.meleeRange      = meleeRange;
        this.cooldown        = cooldown;
        this.isMelee         = isMelee;
        this.projectileCount = projectileCount;
        this.spreadDegrees   = spreadDegrees;
    }

    public WeaponType next() {
        WeaponType[] v = values();
        return v[(ordinal() + 1) % v.length];
    }

    public WeaponType prev() {
        WeaponType[] v = values();
        return v[(ordinal() + v.length - 1) % v.length];
    }
}
