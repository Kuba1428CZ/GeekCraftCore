package cz.kuba1428.coincraftcore.coincraftcore.utils;

import cz.kuba1428.coincraftcore.coincraftcore.CoincraftCore;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public interface GlobalVars {
    CoincraftCore plugin = JavaPlugin.getPlugin(CoincraftCore.class);
    FileConfiguration config = plugin.getConfig();
    Economy economy = CoincraftCore.getEconomy();

}
