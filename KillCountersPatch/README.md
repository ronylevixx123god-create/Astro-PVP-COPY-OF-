# AstroKillCounters 1.0, patched

`AstroKillCounters-1.0.jar` here is `Ruins/lib/AstroKillCounters-1.0 (6).jar` (there is no source for it) with one class
replaced: `me.kingsavaqe.killcounters.listeners.DeathEventListener`, rebuilt from its bytecode in
`src/`. The only change is that a kill made with an empty hand (AIR) is skipped; the original passed it to `NBTItem`,
which threw `NullPointerException: ItemStack can't be null/Air!` on every such player kill.

Rebuild (Java 8):

    javac -source 8 -target 8 -cp "server.jar;AstroKillCounters-1.0.jar;item-nbt-api-plugin-2.7.1.jar" -d out src/me/kingsavaqe/killcounters/listeners/DeathEventListener.java

then put `out/me/kingsavaqe/killcounters/listeners/DeathEventListener.class` into a copy of the original jar.

The plugins that compile against AstroKillCounters keep using the jar in their own `lib/` (the API is unchanged).
