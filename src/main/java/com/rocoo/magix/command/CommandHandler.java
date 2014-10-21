package com.rocoo.magix.command;

import com.rocoo.magix.Magix;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CommandHandler implements CommandExecutor, Listener {

    private Magix magix;
    private char[] token;

    private HashMap<String, ItemStack> playerToItemStack = new HashMap<>();

    public CommandHandler(Magix magix) {
        this.magix = magix;
        magix.getServer().getPluginManager().registerEvents(this, magix);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sendHelp(sender);
            return true;
        }

        if (args.length == 1) {
            String cmd = args[0];
            if (cmd.equalsIgnoreCase("create")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    ItemStack stack = player.getItemInHand();

                    if (stack.getType() != Material.MAP) {
                        player.sendMessage(ChatColor.RED + "You need to hold a map in your hand!");
                    } else {
                        ItemMeta meta = stack.getItemMeta();
                        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Wand");
                        meta.setLore(Arrays.asList("default"));
                        stack.setItemMeta(meta);
                        render(Bukkit.getMap(stack.getDurability()));
                        player.sendMessage(ChatColor.AQUA + "Successfully created your wand!");
                    }

                } else {
                    sendErrorPlayerMessage(sender);
                }
            } else if (cmd.equalsIgnoreCase("help")) {
                sendHelp(sender);
            } else if (cmd.equalsIgnoreCase("edit")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    ItemStack stack = player.getItemInHand();

                    if (stack.getType() != Material.MAP) {
                        player.sendMessage(ChatColor.RED + "You need to hold a map in your hand!");
                    } else {
                        giveBook(player);
                    }
                } else {
                    sendErrorPlayerMessage(sender);
                }
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "Unknown command! Type: '/magix help' to see a list of available commands.");
                return true;
            }
        }

        return true;
    }

    private void sendErrorPlayerMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Only players can use this command!");
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "===== Help Menu =====");
        sender.sendMessage(ChatColor.YELLOW + "/wand create" + ChatColor.GOLD + " - Creates your wand and assign the default script to it");
        sender.sendMessage(ChatColor.YELLOW + "/wand edit" + ChatColor.GOLD + " - Gives you a book so you can edit the script");
        sender.sendMessage(ChatColor.YELLOW + "/wand help" + ChatColor.GOLD + " - Displays this message");
    }

    private static boolean initialisedImageEngine = false;
    private static BufferedImage image;

    private void render(MapView view) {
        try {
            view.getRenderers().clear();

            initImage();

            view.addRenderer(new MapRenderer() {
                @Override
                public void render(MapView view, MapCanvas mapCanvas, Player player) {
                    mapCanvas.drawImage(0, 0, image);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void initImage() throws IOException {
        if (!initialisedImageEngine) {
            byte[] imageData = this.magix.getImageHandler().getImageByToken(this.token);
            image = ImageIO.read(new ByteArrayInputStream(imageData));

            this.magix.getEngine().addScript("defaultImageGetter", String.valueOf(this.token));
            this.magix.getEngine().execute("defaultImageGetter", null);
            initialisedImageEngine = true;
        }
    }

    private void giveBook(Player player) {
        this.playerToItemStack.put(player.getName(), player.getItemInHand());

        ItemStack book = new ItemStack(Material.BOOK_AND_QUILL);
        ItemMeta meta = book.getItemMeta();
        meta.setDisplayName("Right click to edit the script, Sign it in order for the changes to take effect");
        book.setItemMeta(meta);

        player.setItemInHand(book);
    }

    @EventHandler
    public void onEditBook(PlayerEditBookEvent event) {
        if (event.isSigning()) {
            ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
            book.setItemMeta(event.getNewBookMeta());
            check(event.getPlayer(), book);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        check(event.getPlayer(), event.getPlayer().getItemInHand());
    }

    private void check(Player player, ItemStack book) {
        if (this.playerToItemStack.containsKey(player.getName())) {

            ItemStack wand = null;

            if (book.getItemMeta() instanceof BookMeta) {
                BookMeta meta = (BookMeta) book.getItemMeta();
                List<String> code = meta.getPages();

                wand = this.playerToItemStack.get(player.getName());

                ItemMeta itemMeta = wand.getItemMeta();
                itemMeta.setLore(code);
                wand.setItemMeta(itemMeta);
            }

            player.getPlayer().setItemInHand(wand);
            this.playerToItemStack.remove(player.getName());
        }
    }

    @EventHandler
    public void pluginDisable(PluginDisableEvent event) {
        if (event.getPlugin().getName().equalsIgnoreCase("Magix")) {
            for (String name : this.playerToItemStack.keySet()) {
                Player player = Bukkit.getPlayerExact(name);

                if (player == null)
                    continue;

                check(player, player.getItemInHand());
            }
        }
    }

    public void setImage(char[] token) {
        try {
            this.token = token;
            initialisedImageEngine = false;
        } catch (Exception e) {
            throw new RuntimeException("Failed to refresh the Image Token!", e);
        }
    }
}
