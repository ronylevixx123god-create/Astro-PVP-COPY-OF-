package me.cosmicpvp.CosmicGlobalKits.struct.mkits.sets;

/** The ability armor sets of the Mastery Kits, in place of CosmicArmorSets' AbilityArmorSet (see {@link MasteryArmorSet}). */
public enum MasterySet {
    GHOST(new GhostAbilitySet()),
    NECROMANCER(new NecromancerAbilitySet()),
    DEATH_KNIGHT(new DeathKnightAbilitySet());

    private final MasteryArmorSet armorSetInfo;

    MasterySet(MasteryArmorSet armorSetInfo) {
        this.armorSetInfo = armorSetInfo;
    }

    public MasteryArmorSet getArmorSetInfo() {
        return this.armorSetInfo;
    }
}
