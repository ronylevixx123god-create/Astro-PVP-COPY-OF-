package me.astropvp.hotfixplugin.commands;


import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.cosmicpvp.cosmicutils.utils.CC;
import de.tr7zw.nbtapi.NBTItem;
import me.astropvp.hotfixplugin.utils.TimeUtils;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

@Command("hasbeenduped")
public class HasBeenDupedCommand extends CommandBase {

    @Default
    public void hasBeenDuped(Player player) {
        if(!player.hasPermission("admin.admin.admin")) return;

        ItemStack itemStack = player.getInventory().getItemInHand();
        if(itemStack == null || itemStack.getType() == Material.AIR) return;

        NBTItem nbtItem = new NBTItem(itemStack);
        if(!nbtItem.hasKey("dupedBy")) {
            player.sendMessage(CC.translate("&cItem hasn't been duped by anyone"));
        } else {
            player.sendMessage(CC.translate("&aItem has been duped by " + nbtItem.getString("dupedBy")));
            sendWebHook(nbtItem.getString("dupedBy"), nbtItem.getLong("dupedAt"), CC.strip(itemStack.getItemMeta().getDisplayName()));
        }
        sendItemId(player, itemStack);
    }

    // Arkkit ItemID: the item's IID and whether Arkkit flagged it as a duplicate (same wording as /iid)
    private void sendItemId(Player player, ItemStack itemStack) {
        Plugin arkkit = Bukkit.getPluginManager().getPlugin("Arkkit");
        if (arkkit == null) return;
        try {
            Class<?> itemId = Class.forName("org.arkhamnetwork.Arkkit.patches.ItemID", true, arkkit.getClass().getClassLoader());
            long iid = (long) itemId.getMethod("getItemID", ItemStack.class).invoke(null, itemStack);
            if (iid == -1L) return;
            boolean blacklisted = (boolean) itemId.getMethod("isItemBlacklisted", ItemStack.class).invoke(null, itemStack);
            player.sendMessage(ChatColor.GOLD + "The IID of the item in your hand is: " + ChatColor.WHITE + iid);
            if (blacklisted) {
                player.sendMessage(ChatColor.RED + "IID " + iid + " is " + ChatColor.BOLD + "BLACKLISTED!");
            } else {
                player.sendMessage(ChatColor.GREEN + "IID " + iid + " is " + ChatColor.BOLD + "NOT" + ChatColor.GREEN + " blacklisted.");
            }
        } catch (ReflectiveOperationException | ClassCastException ignored) {
        }
    }


    public void sendWebHook(String playerName, long dupedAt, String itemName) {
        String webhookUrl = me.astropvp.hotfixplugin.HotFixPlugin.getInstance().getConfig().getString("discord-webhooks.dupe-check", "");
        if (webhookUrl.isEmpty()) {
            return;
        }
        WebhookClientBuilder builder = new WebhookClientBuilder(webhookUrl); // or id, token

        builder.setThreadFactory((job) -> {
            Thread thread = new Thread(job);
            thread.setName("Duped Item");
            thread.setDaemon(true);
            return thread;
        });

        builder.setWait(true);

        WebhookClient client = builder.build();


        WebhookEmbed embed = new WebhookEmbedBuilder()
                .setColor(0X610000)
                .setTitle(new WebhookEmbed.EmbedTitle("**Duped Item - " + itemName + "**", null))
                .setFooter(new WebhookEmbed.EmbedFooter("Duped | " + TimeUtils.formatTimeMillis(dupedAt), null))
                .setDescription("**Player:** `" + playerName + "`\n" + "**Item:** `" + itemName + "`\n")
                .build();

        WebhookMessageBuilder idBuilder = new WebhookMessageBuilder();
        idBuilder.setUsername("Duped Item");
        idBuilder.setAvatarUrl("https://cdn.discordapp.com/avatars/1202888199903641640/cad565e0a6bcd051ce0fd36b70c1f023.webp");
        idBuilder.addEmbeds(embed);

        client.send(idBuilder.build());
    }

}
