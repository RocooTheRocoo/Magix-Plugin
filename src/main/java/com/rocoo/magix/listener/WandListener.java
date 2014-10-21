package com.rocoo.magix.listener;

import com.rocoo.magix.Magix;
import com.rocoo.magix.ScriptTank;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class WandListener implements Listener {

    private Magix magix;

    public WandListener(Magix magix) {
        this.magix = magix;
    }

    @EventHandler
    public void handleClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        ItemStack stack = player.getItemInHand();
        ItemMeta meta = stack.getItemMeta();

        if (stack.getType() != Material.MAP)
            return;

        if (!meta.getDisplayName().equalsIgnoreCase(ChatColor.LIGHT_PURPLE + "Wand"))
            return;

        if (!player.hasPermission("magix.use")) {
            player.sendMessage(ChatColor.RED + "You don't have permission!");
            return;
        }

        if (meta.getLore() == null || meta.getLore().isEmpty()) {
            player.sendMessage(ChatColor.RED + "This wand is corrupted! Please type: '/wand create' to fix it!");
            return;
        }

        ScriptTank engine = this.magix.getEngine();
        String code = meta.getLore().get(0);
        for (int i = 1; i < meta.getLore().size(); i++) {
            code += ("\n" + meta.getLore().get(i));
        }

        try {
            if (code.equalsIgnoreCase("default")) {
                engine.execute("default_", player);
            } else {
                engine.addScript(player.getName(), code);
                engine.execute(player.getName(), player);
            }
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "Something went wrong! Error: " + e.getCause().getLocalizedMessage());
        }
    }
}
