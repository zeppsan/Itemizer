package com.qvarnstrom.tech.itemizer;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Lore implements CommandExecutor {

    private FileConfiguration config;

    public Lore(FileConfiguration config) {
        this.config = config;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            print(sender, this.config.getString("not-player"));
        }

        // It's a player
        Player player = (Player) sender;

        if(player.getInventory().getItemInMainHand().getItemMeta() == null){
            print(sender, this.config.getString("not-holding-item"));
            return false;
        }

        switch (label){

                // Adds a lore to the item. At the end.
            case "addlore":
                if(player.hasPermission("itemizer.addlore"))
                    return addLore(player, args);
                return false;

                // Edits a lore at a specific index
            case "editlore":
                if(player.hasPermission("itemizer.editlore"))
                    return editLore(player, args);
                return false;

                // Removes all lore.
            case "removelore":
                if(player.hasPermission("itemizer.removelore"))
                    return removeLore(player, args);
                return false;
            case "getlore":
                if(player.hasPermission("itemizer.getlore"))
                    return getLore(player, args);
                return false;
        }

        return false;

    }

    private boolean getLore(Player player, String[] args) {
        ItemStack item = player.getInventory().getItemInMainHand();

        if(item.getItemMeta().hasLore()){
            printConfigHeader(player, item);

            printIndexes(player, item);
            return true;
        }
        player.sendMessage("This item does not have any lore...");
        return false;
    }

    private void printIndexes(Player player, ItemStack item) {
        List<String> lores = item.getItemMeta().getLore();
        for(int i = 0; i < lores.size(); i++){
            print(player, this.config.getString("index-print").replace("{INDEX}", Integer.toString(i)).replace("{LORE}", lores.get(i)));
        }
    }

    private boolean removeLore(Player player, String[] args) {
        ItemStack item = player.getInventory().getItemInMainHand();

        if(!item.hasItemMeta() || args.length != 1){
            if(args.length == 0)
                print(player, this.config.getString("invalid-arguments"));
            else
                print(player, this.config.getString("no-lore-found"));
            return false;
        }

        ItemMeta meta = item.getItemMeta();

        if(args[0].equalsIgnoreCase("all")){
            meta.setLore(null);
            print(player, this.config.getString("removed-all-lore"));
        } else {
            int loreToRemove;
            try {
                loreToRemove = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                print(player, this.config.getString("invalid-index"));
                return false;
            }

            if(meta.hasLore()){
                if(loreToRemove < meta.getLore().size() && loreToRemove >= 0){
                    List<String> temp = meta.getLore();
                    temp.remove(loreToRemove);
                    meta.setLore(temp);
                    print(player, this.config.getString("removed-lore"));
                } else {
                    print(player, this.config.getString("invalid-index"));
                    return false;
                }
            }
        }
        item.setItemMeta(meta);
        player.getInventory().setItemInMainHand(item);
        return true;
    }

    private boolean editLore(Player player, String[] args) {
        ItemStack item = player.getInventory().getItemInMainHand();

        if(!item.hasItemMeta()){
            print(player, this.config.getString("no-lore-found"));
            return false;
        }

        ItemMeta meta = item.getItemMeta();

        // If there is not enough arguments
        if(args.length < 2){
            print(player, this.config.getString("invalid-arguments"));
            return false;
        }

        if(!meta.hasLore()){
            print(player, this.config.getString("no-lore-found"));
            return false;
        }

        int loreToEdit;

        try {
            loreToEdit = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            print(player, this.config.getString("invalid-arguments"));
            return false;
        }
        if(loreToEdit > meta.getLore().size() - 1){
            print(player, this.config.getString("invalid-arguments"));
            return false;
        }

        int loreAdded = 0;

        // Now we know that the index is valid, and the second argument is an index.
        // Get the lores
        List<String> lores = meta.getLore();

        lores.remove(loreToEdit);
        lores.add(loreToEdit, getParsedLore(args).substring(2));

        // Set the lore.
        meta.setLore(lores);
        item.setItemMeta(meta);
        player.getInventory().setItemInMainHand(item);
        return true;
    }

    private boolean addLore(Player player, String[] args) {
        // Returns the menu to the player
        if(args.length == 0)
            return printInfo(player);

        // User item
        ItemStack item = player.getInventory().getItemInMainHand();
        String lore = getParsedLore(args);

        ItemMeta meta = item.getItemMeta();
        List<String> lores = meta.getLore();

        if(lores == null)
            lores = new ArrayList<String>();

        lores.add(lore);

        meta.setLore(lores);
        item.setItemMeta(meta);

        // Items is now modified
        player.getInventory().setItemInMainHand(item);
        print(player, this.config.getString("added-lore"));
        return true;
    }

    private String getParsedLore(String[] args){
        String lore = "";

        // Concat all the args to a string
        for(int i = 0; i < args.length; i++){
            lore = lore.concat(args[i]);
            lore = lore.concat(" ");
        }
        return ChatColor.translateAlternateColorCodes('&', lore);
    }

    private boolean printInfo(Player sender) {
        return false;
    }

    private void print(CommandSender sender, String message){
        String prefix = this.config.getString("prefix");
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
        return;
    }

    private void printConfigHeader(CommandSender sender, ItemStack item){
        if(item.getItemMeta().hasDisplayName())
            print(sender, this.config.getString("configuration-header").replace("{ITEM}", item.getItemMeta().getDisplayName()));
        else
            print(sender, this.config.getString("configuration-header").replace("{ITEM}", item.getType().name()));
    }
}
