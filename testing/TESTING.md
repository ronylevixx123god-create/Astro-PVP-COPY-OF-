# Testing the restored plugins

How the fixes in this repo were tested, and how to repeat the tests. Nothing in `testing/` is part of the
Maven build or of the server; it only holds test code.

- `probes/` — small test plugins ("probes"). Each one runs a scenario on a test server, logs `PROBE ...`
  lines and stops the server.
- `linkcheck/LinkCheck3.java` — read-only check that every class/field/method a plugin uses exists in
  WineSpigot + the other plugins (no plugin code is run).
- `compat-tests/` — two tiny standalone checks used for the Arkkit and CosmicPvPUtils compatibility fixes.

## 1. Rules for testing

- **Never test on the live server.** Use a separate, throw-away server folder.
- Accept the Minecraft EULA yourself in that test folder (`eula.txt`); nothing here does it for you.
- Back up the test server folder before a run and restore it afterwards (`cp -r devserver devserver-backup`).
- Report **startup checks** (plugin loads/enables) separately from **gameplay checks** (what a player can do).

## 2. Test server

| Server jar | When |
|---|---|
| `.lib/WineSpigot-V1.9.15.jar` | The real server jar (1.8.8). Needs the WineSpigot license. Use it for anything with crate keys or slot tickets (see limitations). |
| `AstroFactions/lib/taco.jar` (TacoSpigot 1.8.8) | Runs without a license. Used for all tests so far. |

Java 8. Example folder setup:

```
devserver/
  server.jar            (one of the jars above)
  eula.txt              (eula=true — set it yourself)
  server.properties     (online-mode=false for test players, except the HotFix test: online-mode=true)
  plugins/              (the plugins under test + their dependencies, see SERVER-SETUP.md)
```

Start/stop: `java -Xmx1G -Dfile.encoding=UTF-8 -jar server.jar nogui < /dev/null > run.log 2>&1`
(the probe stops the server by itself; wrap it in `timeout 300` so a failed probe can't hang).

### Test players

There is no Minecraft client. Probes create real server-side players (`EntityPlayer`) whose network
connection only queues packets, and register them as online (`PlayerList.players` plus the private UUID and
name maps `j` and `playersByName`). Commands, menus and events run through the real plugin code.

## 3. Building a probe

From the repo root, after `mvn -o -DskipTests package` (paths use `;` on Windows, `:` elsewhere):

```
P=testing/probes/<ProbeName>
javac -source 8 -target 8 -encoding UTF-8 -cp "<server.jar>;<extra jars>" -d out/<ProbeName> $P/src/devtest/<ProbeName>.java
jar cf <ProbeName>.jar -C $P plugin.yml -C out/<ProbeName> devtest
```

Then copy `<ProbeName>.jar` into the test server's `plugins/` together with the plugins it tests.
Only one probe at a time (each one stops the server). Read the results with `grep PROBE run.log`.

| Probe | Extra jars to compile against | Plugins it needs on the test server |
|---|---|---|
| FactionsGuardProbe, FactionsRulesProbe | `AstroFactions/target/Factions.jar` | Factions, CosmicPvPUtils, StorageUtil (shaded), WorldEdit, WorldGuard, ProtocolLib |
| KitsLPProbe | `Kits/target/Kits.jar`, `Core/lib/LuckPerms-Bukkit-5.4.30.jar` | LuckPerms, CosmicGlobalKits and its dependencies |
| CustomSkullsProbe | `CustomBlocks/target/CustomBlocks.jar`, `Utils/target/CosmicPvPUtils.jar`, `AstroCrateKeys/target/AstroCrateKeys.jar`, `Slotbot/target/Slotbot-0.1.jar` | CustomSkullBlocks and its dependencies (Vault, Multiverse-Core, ParticleLIB, CosmicMysteryChest, CosmicSkyCustomItems, Arkkit, AstroSpawners, a FactionLogs, AstroCrateKeys, AstroLootFilter, AstroLoottables, AstroSlotBot, Factions, ProtocolLib, WorldEdit, WorldGuard) |
| all other probes | none (they reach plugins through reflection) | see the `depend`/`softdepend` in the probe's `plugin.yml` |

## 4. The probes

| Probe | Tests | Recorded result |
|---|---|---|
| **HotFixProbe** | HotFix auto-OP: XaitX (owner UUID in HotFix's `auto-op-uuids`) and an unrelated player join. | XaitX OP level 4 about 2 s after joining; the other player not OP. Needs `online-mode=true` and ProtocolLib. |
| **DuelMenuProbe** | CosmicDuel `/team` menus open with the bundled MF-GUI. | Menus open. |
| **KitsLPProbe** | Kits unlocks stored as LuckPerms permissions; phase set in `plugins/KitsLPProbe/phase.txt` (grant, then restart and check). | Grants persist across a restart. |
| **CommonsProbe** | Commons, NebulaShop and AstroRuins load; `/nebula shop` opens; Commons blocks taking items. | NebulaShop works; AstroRuins loads with Commons, ProtocolLib and CCVerify (+BuycraftX). AstroRuins needs a `world_ruins` world. |
| **SlotGiveProbe** | `/slot give`, `giveheroic`, `enable`, `disable` need `slotbot.admin` (OP); console rewards still work; `/slot` still opens. | All 8 checks pass. |
| **SlotProbe** | Slot bot: console gives tickets, `/slot` menu, insert tickets, spin, rewards. | Ticket insertion needs WineSpigot (see limitations). |
| **FactionsGuardProbe** | Factions version guards on 1.8.8 (particles, portals, titles, materials) and claim protection with 1.8 block names. | Guards hold; no newer-API errors. |
| **FactionsRulesProbe** | Claim rules: outsiders may only use chests, trapped chests and enchanting tables; only allies can be granted more; old saved grants ignored; one-time ally reset. Prints `ROW who|what|result` lines — run on the old and new jar and compare. | All rules pass. |
| **CustomSkullsProbe** | CustomSkullBlocks crate-key opener, slot bot opener (loot filter applied) and mob grinder key button. | Blocked on TacoSpigot (crate key items need WineSpigot). |
| **AcfProbe** | AstroCore / AstroLootFilter load with the bundled ACF and register their commands; slot bot reward delivery. | AstroLootFilter loads; AstroCore needs the database to finish enabling. |
| **BadgeProbe** | AstroCore's chat badges with AstroDiscordSync not installed (must not crash). | Also covered by the 5 unit tests in `Core` (`DiscordSyncBadgesTest`). |
| **BahProbe** | BAH management as XaitX vs. another player (`/bah additem`, `forcestart`, `/bah`, `/bahloot`). | Needs the database (AstroCore) for a full run. |

## 5. Known limitations of TacoSpigot

- No `CraftItemStack.getHandle()`: crate key items (AstroCrateKeys) and slot ticket insertion fail, so
  AstroSpawners, CustomSkullBlocks and the slot bot spin must be tested on WineSpigot.
- Its Gson has no `setLenient`: use `StorageUtil-0.1-shaded.jar`, not `StorageUtil.jar`.

## 6. Link check

`linkcheck/LinkCheck3.java` reads the class files of one or more plugin jars and checks every outside
reference against a simulated server classpath. It never runs plugin code.

```
javac -d out/linkcheck testing/linkcheck/LinkCheck3.java
java -cp out/linkcheck LinkCheck3 plugins.txt classpath.txt report.txt
```

- `plugins.txt`: one line per plugin to check, `Name|module|<path to the plugin jar>`.
  Only classes compiled from the module's own source (`<module>/target/classes`) are checked.
- `classpath.txt`: one jar per line, **WineSpigot first**, then every plugin jar that would be installed
  (built jars + third-party jars from SERVER-SETUP.md). Use Windows paths (`C:/...`) on Windows.

Problem types: `MISSING_CLASS`, `MISSING_MEMBER` (class exists, field/method doesn't), `UNVERIFIABLE`
(the check couldn't load a class it needed to decide). Last results: CustomSkullBlocks 6 problems before
its fix, 0 after.

## 7. Compatibility checks

`compat-tests/CompatTest.java` (Arkkit's spawner stack-size compatibility) and `compat-tests/BananaDura.java`
(CosmicPvPUtils durability helper) are plain `main` programs; compile them against the server jar and the
plugin jar they test and run them with `java`.
