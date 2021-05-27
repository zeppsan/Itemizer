package com.qvarnstrom.tech.itemizer;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;


public final class Name implements CommandExecutor {

    private FileConfiguration config;

    public Name(FileConfiguration config) {
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
            case "addname":
                if(player.hasPermission("itemizer.addname"))
                    return addName(player, args);
                return false;
        }


        return false;
    }

    private boolean addName(Player player, String[] args) {
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        String name = getParsedName(args);
        meta.setDisplayName(name);
        item.setItemMeta(meta);

        // Items is now modified
        player.getInventory().setItemInMainHand(item);
        print(player, this.config.getString("changed-name"));
        return true;
    }

    private void print(CommandSender sender, String message){
        String prefix = this.config.getString("prefix");
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
        return;
    }
    private String getParsedName(String[] args){
        String name = "";

        // Concat all the args to a string
        for(int i = 0; i < args.length; i++){
            name = name.concat(args[i]);
            name = name.concat(" ");
        }
        return ChatColor.translateAlternateColorCodes('&', name);
    }
}

