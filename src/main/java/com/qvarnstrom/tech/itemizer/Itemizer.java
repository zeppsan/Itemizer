package com.qvarnstrom.tech.itemizer;

import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class Itemizer extends JavaPlugin {

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        Lore LoreObject = new Lore(getConfig());
        Name ItemName = new Name(getConfig());

        // Lores
        this.getCommand("addlore").setExecutor(LoreObject);
        this.getCommand("removelore").setExecutor(LoreObject);
        this.getCommand("editlore").setExecutor(LoreObject);
        this.getCommand("getlore").setExecutor(LoreObject);

        this.getCommand("addname").setExecutor(ItemName);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public FileConfiguration getConfiguration(){
        return getConfig();
    }
}
