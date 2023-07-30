package cz.kuba1428.coincraftcore.coincraftcore.events;

import cz.kuba1428.coincraftcore.coincraftcore.CoincraftCore;
import cz.kuba1428.coincraftcore.coincraftcore.managers.DbManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaves implements Listener {
    @EventHandler
    public void OnPlayerLeft(PlayerQuitEvent event) {
        UpdatePlayerBalance(event.getPlayer());
    }

    public void UpdatePlayerBalance(Player player) {

            DbManager.ExecuteUpdate(    "UPDATE " + config.getString("database.prefix") + "users SET money = " + economy.getBalance(player) + " WHERE nick='" + player.getName() + "'");


    }


    static CoincraftCore plugin = CoincraftCore.getPlugin(CoincraftCore.class);
    static FileConfiguration config = plugin.getConfig();
    Economy economy = CoincraftCore.getEconomy();

}
