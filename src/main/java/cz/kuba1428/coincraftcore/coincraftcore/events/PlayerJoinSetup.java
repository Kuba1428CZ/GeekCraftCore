package cz.kuba1428.coincraftcore.coincraftcore.events;
import cz.kuba1428.coincraftcore.coincraftcore.Coincraftcore;
import cz.kuba1428.coincraftcore.coincraftcore.misc.UpdateBalance;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class PlayerJoinSetup implements Listener {

    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent event){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);
            Statement stmnt = connection.createStatement();
            Economy economy = Coincraftcore.getEconomy();
            String statement = "INSERT IGNORE INTO " + config.getString("database.prefix") + "users (nick, money) VALUES ('"+ event.getPlayer().getName() +"'," + economy.getBalance(event.getPlayer().getName()) +")";
            stmnt.executeUpdate(statement);
            UpdatePlayerBallance(event.getPlayer());
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");

                statement = "SELECT discord, allow_access, rank FROM " + config.getString("database.prefix") + "users WHERE nick='" + event.getPlayer().getName() + "'";
                ResultSet rs = stmnt.executeQuery(statement);
                while (rs.next()){
                    Random rand = new Random();
                    Integer randominteger = rand.nextInt(900000) + 100000;
                    String kickmsg = "&9&lGeek&f&lCraft"
                            + "\n"
                            + "\n&6&lTENTO SERVER JE POUZE PRO ČLENY"
                            + "\n&6&lKOMUNITNÍHO DISCORDU YOUTUBERA GEEKBOY"
                            + "\n"
                            + "\n&bJsi členem?"
                            + "\n&fNa discordu použij příkaz /verify a zadej verifikační kód níže"
                            + "\n"
                            + "\n"
                            + "\n&b&lTvůj verifikační kód:"
                            + "\n&e" + randominteger;


                    if (rs.getLong("discord") == 0){
                        event.getPlayer().kickPlayer(ChatColor.translateAlternateColorCodes('&', kickmsg));
                        event.setJoinMessage("");
                        statement = "UPDATE " + config.getString("database.prefix") + "users SET verify_code = " + randominteger + " WHERE nick='" + event.getPlayer().getName() + "'";
                        stmnt = connection.createStatement();
                        stmnt.executeUpdate(statement);
                    } else if (rs.getInt("allow_access") == 0) {
                        event.getPlayer().kickPlayer(ChatColor.translateAlternateColorCodes('&', kickmsg));
                        event.setJoinMessage("");
                        statement = "UPDATE " + config.getString("database.prefix") + "users SET verify_code = " + randominteger + " WHERE nick='" + event.getPlayer().getName() + "'";
                        stmnt = connection.createStatement();
                        stmnt.executeUpdate(statement);
                    }else {
                        LuckPerms lp = LuckPermsProvider.get();
                        Player player = event.getPlayer();
                        UUID uuid = player.getUniqueId();
                        User user = lp.getUserManager().getUser(uuid);
                        user.data().clear(NodeType.INHERITANCE::matches);
                        InheritanceNode node = InheritanceNode.builder(rs.getString("rank")).value(true).build();
                        DataMutateResult result = user.data().add(node);
                        lp.getUserManager().saveUser(user);
                        plugin.getLogger().info("aaaa" + rs.getString("rank"));
                    }
                }
            }catch (SQLException e){
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }

        static Economy economy = Coincraftcore.getEconomy();
    public static void UpdatePlayerBallance(Player player){
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
    static Coincraftcore plugin = Coincraftcore.getPlugin(Coincraftcore.class);
    static FileConfiguration config = plugin.getConfig();
    static String user = config.getString("database.user");
    static String password = config.getString("database.password");
    static String url = "jdbc:mysql://" + config.getString("database.host") + ":" + config.getString("database.port") + "/" + config.getString("database.database");


}
