package net.astropvp.kitreset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

/** EssentialsX kits (/kit apollo, nebula, galaxy ...): the cooldown is the user's kit timestamp (timestamps.kits.<kit>). */
final class EssentialsKits implements AstroKitReset.KitSystem {

    private static Essentials ess() {
        return (Essentials)Bukkit.getPluginManager().getPlugin("Essentials");
    }

    static UUID findUser(String name) {
        User user = ess().getOfflineUser(name);
        return user == null ? null : user.getBase().getUniqueId();
    }

    private static List<String> kitNames() {
        List<String> names = new ArrayList<String>();
        ConfigurationSection kits = ess().getKits().getKits();
        if (kits != null) {
            names.addAll(kits.getKeys(false));
        }
        return names;
    }

    /** every user Essentials knows (online and offline), or just these */
    private static void forUsers(Collection<UUID> players, java.util.function.Consumer<User> action) {
        Collection<UUID> uuids = players != null ? players : ess().getUserMap().getAllUniqueUsers();
        for (UUID uuid : uuids) {
            User user = ess().getUser(uuid);
            if (user != null) {
                action.accept(user);
            }
        }
    }

    @Override
    public List<AstroKitReset.Kit> kits() {
        List<AstroKitReset.Kit> kits = new ArrayList<AstroKitReset.Kit>();
        for (String name : kitNames()) {
            kits.add(new AstroKitReset.Kit() {
                @Override
                public String name() {
                    return name;
                }

                @Override
                public void reset(Collection<UUID> players) {
                    forUsers(players, user -> user.setKitTimestamp(name.toLowerCase(Locale.ENGLISH), 0L));
                }
            });
        }
        return kits;
    }

    @Override
    public void resetAll(Collection<UUID> players) {
        List<String> names = kitNames();
        forUsers(players, user -> {
            for (String name : names) {
                user.setKitTimestamp(name.toLowerCase(Locale.ENGLISH), 0L);
            }
        });
    }
}
