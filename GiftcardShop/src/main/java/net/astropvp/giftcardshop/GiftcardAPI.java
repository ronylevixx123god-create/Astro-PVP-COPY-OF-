package net.astropvp.giftcardshop;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.cosmicpvp.cosmicutils.utils.NumberUtils;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.astropvp.giftcardshop.data.AddGiftcards;
import net.astropvp.giftcardshop.utils.Clickable;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class GiftcardAPI {

    public static void createGiftCard(Player player, double amount) throws IOException, UnirestException, ParseException {
        createGiftCard(player, amount, null);
    }

    /** Creates the giftcard through the Tebex plugin API; the result (or the reason it failed) also goes to requester. */
    public static void createGiftCard(Player player, double amount, CommandSender requester) {
        String secret = getSecret();
        if (secret.isEmpty()) {
            String error = "&c&l(!)&c Giftcard not created: no Tebex key. Set &ftebex-secret&c in plugins/AstroGiftCardPlugin/config.yml.";
            notify(player, requester, error);
            System.out.println("[AstroGiftcards] Could not generate a " + amount + " Giftcard for " + player.getName() + ": tebex-secret is empty in config.yml");
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(GiftcardShopPlugin.getInstance(), ()-> {
            player.sendMessage(CC.translate("&a&l(!)&a Generating a &f$" + NumberUtils.formatMoney(amount) +  " &aGiftcard for you..."));

            int status;
            String body;
            try {
                String note = player.getName() + " Generated Giftcard";
                URL url = new URL("https://plugin.tebex.io/gift-cards?amount=" + amount + "&note=" + URLEncoder.encode(note, "UTF-8"));
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(15000);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("X-Tebex-Secret", secret);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);
                try (OutputStream out = connection.getOutputStream()) {
                    out.write(("{\"amount\":" + amount + ",\"note\":\"" + note.replace("\"", "") + "\"}").getBytes(StandardCharsets.UTF_8));
                }
                status = connection.getResponseCode();
                body = read(status >= 400 ? connection.getErrorStream() : connection.getInputStream());
            } catch (IOException e) {
                notify(player, requester, "&c&l(!)&c Giftcard not created: could not reach Tebex (" + e.getClass().getSimpleName() + ": " + e.getMessage() + ").");
                System.out.println("[AstroGiftcards] Tebex request failed for " + player.getName() + ": " + e);
                return;
            }

            if (status < 200 || status >= 300 || !body.contains("\"code\":")) {
                String reason = status == 401 || status == 403 ? "the Tebex key was refused" : "Tebex answered HTTP " + status;
                String message = jsonValue(body, "error_message");
                notify(player, requester, "&c&l(!)&c Giftcard not created: " + reason + " (HTTP " + status + ")" + (message.isEmpty() ? "" : ": &f" + message) + "&c.");
                System.out.println("[AstroGiftcards] Tebex refused a " + amount + " Giftcard for " + player.getName() + ": HTTP " + status + " " + body);
                return;
            }

            String code = body;

            code = code.split("\"code\":")[1];

            code = code.split(",")[0].replace("\"", "");

            code = java.util.Arrays.toString(code.split("(?<=\\G.{4})"));

            code = code.replace("[", "").replace("]", "").replace(",", "");

            String codeAndAmount = code+":"+amount;

            AddGiftcards.addNewGiftcard(player.getUniqueId(), codeAndAmount);
            Clickable giftcardClickable = new Clickable("");
            giftcardClickable.add(CC.translate("&a&l(!)&a A giftcard has been added to your &f/giftcards&a."), CC.translate("&a/giftcards"), "/giftcards");
            giftcardClickable.sendToPlayer(player);
            player.sendMessage(CC.translate("&7* The code is &f" + code));
            if (requester != null)
                requester.sendMessage(CC.Green + "Created Giftcard for " + player.getName() + " with amount: " + amount);
            System.out.println("[AstroGiftcards] Generated a " + amount + " Giftcard for " + player.getName());
        });
    }

    public static String getSecret() {
        String secret = GiftcardShopPlugin.getInstance().getConfig().getString("tebex-secret");
        return secret == null ? "" : secret.trim();
    }

    private static void notify(Player player, CommandSender requester, String message) {
        if (requester != null) {
            requester.sendMessage(CC.translate(message));
        } else {
            // automatic rewards (KOTH, events): the reason is in the console, the winner only learns it failed
            player.sendMessage(CC.translate("&c&l(!)&c Your giftcard could not be generated right now, please contact staff."));
        }
    }

    private static String read(InputStream in) throws IOException {
        if (in == null) return "";
        try (InputStream input = in) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int n;
            while ((n = input.read(buffer)) != -1) out.write(buffer, 0, n);
            return new String(out.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    private static String jsonValue(String json, String key) {
        int i = json.indexOf("\"" + key + "\":\"");
        if (i < 0) return "";
        int start = i + key.length() + 4;
        int end = json.indexOf('"', start);
        return end < 0 ? "" : json.substring(start, end);
    }
}
