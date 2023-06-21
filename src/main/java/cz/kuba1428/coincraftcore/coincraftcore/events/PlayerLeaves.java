package cz.kuba1428.coincraftcore.coincraftcore.events;

import cz.kuba1428.coincraftcore.coincraftcore.Coincraftcore;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class PlayerLeaves implements Listener {
    @EventHandler
    public void OnPlayerLeft(PlayerQuitEvent event){
        UpdatePlayerBallance(event.getPlayer());
    }
    public void UpdatePlayerBallance(Player player){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);
            Statement stmnt = connection.createStatement();
            String statement = "UPDATE " + config.getString("database.prefix") + "users SET money = " + economy.getBalance(player) + " WHERE nick='" + player.getName() + "'";
            stmnt.executeUpdate(statement);
        }catch (SQLException e){
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    String url = "jdbc:mysql://" + config.getString("database.host") + ":" + config.getString("database.port") + "/" + config.getString("database.database");
    static Coincraftcore plugin = Coincraftcore.getPlugin(Coincraftcore.class);
    static FileConfiguration config = plugin.getConfig();
    Economy economy = Coincraftcore.getEconomy();
    static  String user = config.getString("database.user");
    static  String password = config.getString("database.password");
}
