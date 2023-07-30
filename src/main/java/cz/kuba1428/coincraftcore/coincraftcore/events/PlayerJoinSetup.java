package cz.kuba1428.coincraftcore.coincraftcore.events;

import cz.kuba1428.coincraftcore.coincraftcore.CoincraftCore;
import cz.kuba1428.coincraftcore.coincraftcore.managers.DbManager;
import cz.kuba1428.coincraftcore.coincraftcore.utils.GlobalVars;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.UUID;

public class PlayerJoinSetup implements Listener {

    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent event) {
        Economy economy = GlobalVars.economy;
        DbManager.ExecuteUpdate("INSERT IGNORE INTO " + config.getString("database.prefix") + "users (nick, money) VALUES ('" + event.getPlayer().getName() + "'," + economy.getBalance(event.getPlayer().getName()) + ")");
        UpdatePlayerBalance(event.getPlayer());
        try {

            ResultSet rs = DbManager.ExecuteQuery("SELECT discord, allow_access, rank FROM " + config.getString("database.prefix") + "users WHERE nick='" + event.getPlayer().getName() + "'");
            while (rs.next()) {
                Random rand = new Random();
                int randominteger = rand.nextInt(900000) + 100000;
                String kickmsg = "\n&9&lGeek&f&lCraft"
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


                if (rs.getLong("discord") == 0) {
                    event.getPlayer().kickPlayer(ChatColor.translateAlternateColorCodes('&', kickmsg));
                    event.setJoinMessage("");
                    DbManager.ExecuteUpdate(                        "UPDATE " + config.getString("database.prefix") + "users SET verify_code = " + randominteger + " WHERE nick='" + event.getPlayer().getName() + "'");
                } else if (rs.getInt("allow_access") == 0) {
                    event.getPlayer().kickPlayer(ChatColor.translateAlternateColorCodes('&', kickmsg));
                    event.setJoinMessage("");
                    DbManager.ExecuteUpdate("UPDATE " + config.getString("database.prefix") + "users SET verify_code = " + randominteger + " WHERE nick='" + event.getPlayer().getName() + "'");

                } else {
                    LuckPerms lp = LuckPermsProvider.get();
                    Player player = event.getPlayer();
                    UUID uuid = player.getUniqueId();
                    User user = lp.getUserManager().getUser(uuid);
                    assert user != null;
                    user.data().clear(NodeType.INHERITANCE::matches);
                    user.data().add(InheritanceNode.builder(rs.getString("rank")).build());
                    lp.getUserManager().saveUser(user);
                }
            }

        } catch (SQLException e) {
            event.getPlayer().kickPlayer("Chyba databáze.");
            throw new RuntimeException(e);

        }

    }

    static Economy economy = CoincraftCore.getEconomy();

    public static void UpdatePlayerBalance(Player player) {
        DbManager.ExecuteUpdate("UPDATE " + config.getString("database.prefix") + "users SET money = " + economy.getBalance(player) + " WHERE nick='" + player.getName() + "'");

    }

    static FileConfiguration config = GlobalVars.config;


}
