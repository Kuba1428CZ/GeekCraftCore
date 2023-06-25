package cz.kuba1428.coincraftcore.coincraftcore.misc;

import cz.kuba1428.coincraftcore.coincraftcore.CoincraftCore;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class UpdateBalance {
    public void string(String name) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);
            Statement stmnt = connection.createStatement();
            String statement = "UPDATE " + config.getString("database.prefix") + "users SET money = " + economy.getBalance(name);
            stmnt.executeUpdate(statement);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void UpdatePlayerBallance(Player player) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);
            Statement stmnt = connection.createStatement();
            String statement = "UPDATE " + config.getString("database.prefix") + "users SET money = " + economy.getBalance(player) + " WHERE nick='" + player.getName() + "'";
            stmnt.executeUpdate(statement);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    String url = "jdbc:mysql://" + config.getString("database.host") + ":" + config.getString("database.port") + "/" + config.getString("database.database");
    static CoincraftCore plugin = CoincraftCore.getPlugin(CoincraftCore.class);
    static FileConfiguration config = plugin.getConfig();
    Economy economy = CoincraftCore.getEconomy();
    static String user = config.getString("database.user");
    static String password = config.getString("database.password");
}



