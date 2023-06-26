package cz.kuba1428.coincraftcore.coincraftcore.events;

import cz.kuba1428.coincraftcore.coincraftcore.CoincraftCore;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.io.BukkitObjectInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;

public class ShopBreak implements Listener {
    @EventHandler
    public static void onBlockBreak(BlockBreakEvent event) throws SQLException {
        Block block = event.getBlock();
        PersistentDataContainer persistentDataContainer;
        if (block.getType().equals(Material.CHEST) || block.getType().equals(Material.BARREL)) {

            try {
                Connection conn = DriverManager.getConnection(url, user, password);
                Statement stmt = conn.createStatement();
                String sql = "SELECT * FROM " + config.getString("database.prefix") + "shops WHERE storage_location = '" + event.getBlock().getLocation().toString() + "'";
                ResultSet rs = stmt.executeQuery(sql);
                boolean exist = false;
                ArrayList<Block> blocks = new ArrayList<>();
                while (rs.next()) {
                    exist = true;

                    String owner = rs.getString("owner");
                    if (owner.equals(event.getPlayer().getName())) {
                        byte[] itemSerialized = Base64.getDecoder().decode(rs.getString("shop_location_encoded"));
                        ByteArrayInputStream in = new ByteArrayInputStream(itemSerialized);
                        BukkitObjectInputStream is = new BukkitObjectInputStream(in);
                        Location shop_loc = (Location) is.readObject();
                        Block sign = shop_loc.getBlock();
                        blocks.add(sign);


                    } else {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(ChatColor.RED + "Né všechny obchody přidružené k tomuto úložšti patří tobě!");
                        return;
                    }

                }

                if (exist) {
                    for (Block blck : blocks) {
                        Sign sgn = (Sign) blck.getState();
                        sgn.getPersistentDataContainer().remove(newKey("isshop"));
                        sgn.setLine(0, "");
                        sgn.setLine(1, "");
                        sgn.setLine(2, "");
                        sgn.setLine(3, "");
                        sgn.update();
                    }
                    event.getPlayer().sendMessage(ChatColor.YELLOW + "Obchody přidružené k tomuto úložišti byli zrušeny");
                    sql = "DELETE FROM " + config.getString("database.prefix") + "shops WHERE storage_location = '" + event.getBlock().getLocation().toString() + "'";
                    stmt.executeUpdate(sql);
                }
                rs.close();

            } catch (SQLException | IOException | ClassNotFoundException e) {
                plugin.getLogger().warning(e.toString());
            }

        } else if (event.getBlock().getType().toString().contains("SIGN")) {
            try {
                Connection conn = DriverManager.getConnection(url, user, password);
                Statement stmt = conn.createStatement();
                String sql = "SELECT * FROM " + config.getString("database.prefix") + "shops WHERE shop_location = '" + event.getBlock().getLocation().toString() + "'";
                ResultSet rs = stmt.executeQuery(sql);

                ArrayList<String> signloc = new ArrayList<>();
                while (rs.next()) {


                    String owner = rs.getString("owner");
                    if (owner.equals(event.getPlayer().getName())) {
                        event.getPlayer().sendMessage(ChatColor.YELLOW + "Obchod zrušen");
                        signloc.add(rs.getString("shop_location"));

                    } else {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(ChatColor.RED + "Tento obchod nepatří tobě.");
                        return;
                    }


                }
                for (String str : signloc) {
                    Connection conn2 = DriverManager.getConnection(url, user, password);
                    Statement stmt2 = conn2.createStatement();
                    String sql2 = "DELETE FROM " + config.getString("database.prefix") + "shops WHERE shop_location = '" + str + "'";
                    stmt2.executeUpdate(sql2);
                }


            } catch (SQLException e) {
                plugin.getLogger().warning(e.toString());
            }
        }

    }

    public static NamespacedKey newKey(String name) {
        return new NamespacedKey(CoincraftCore.getPlugin(CoincraftCore.class), name);
    }

    static CoincraftCore plugin = CoincraftCore.getPlugin(CoincraftCore.class);
    static FileConfiguration config = plugin.getConfig();
    static String user = config.getString("database.user");
    static String password = config.getString("database.password");
    static String url = "jdbc:mysql://" + config.getString("database.host") + ":" + config.getString("database.port") + "/" + config.getString("database.database");

}



