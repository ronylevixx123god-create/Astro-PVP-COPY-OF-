# CosmicFactionBoosters 1.0, patched

`CosmicFactionBoosters.jar` here is Astro's original `CosmicFactionBoosters.jar` (the copy in every `*/lib/` folder of the
original files, md5 f17b979c...; there is no source for it) with two changes and nothing else:

1. **plugin.yml** gets `softdepend: [mcMMO, Factions, CosmicPvPUtils, Arkkit, FactionsKore]`. The plugin only hooks
   mcMMO when mcMMO is already enabled, and without a load order it started first (so mcMMO boosters did nothing)
   and took `/booster` away from FactionsKore. Loading after them, `/booster` stays FactionsKore's and the
   original active-booster list is `/boosters` (also `/boost`, `/boostertimer`).
2. Three classes rebuilt from their decompiled bytecode in `src/`: `listeners/mcMMOListener`,
   `commands/CommandCheckBooster` and `tasks/BoosterTask`. They called `Faction.isNormal()` / `Faction.sendMessage()`
   as class methods; in the installed Factions `Faction` is an interface, so they threw `IncompatibleClassChangeError`
   (mcMMO XP was never boosted, `/boosters` failed, an expiring booster threw). Rebuilt against the installed jars the
   only difference is `invokevirtual` -> `invokeinterface` for those calls.

Commands (OP only, as in the original):

    /givefacbooster <EXP|MCMMO> <player> <multiplier> <seconds>    (alias /gfb; type in capitals, multiplier >= 1.1)

The item is a "Faction mcMMO Booster" / "Faction XP Booster" emerald; right-click activates it for the faction.

Rebuild (Java 8): compile the three files against server.jar, Factions.jar, mcMMO.jar, CosmicPvPUtils.jar, Arkkit.jar and
the original jar, and put the classes and this plugin.yml into a copy of the original jar.
