package cz.kuba1428.coincraftcore.coincraftcore.events;

import cz.kuba1428.coincraftcore.coincraftcore.CoincraftCore;
import cz.kuba1428.coincraftcore.coincraftcore.managers.DbManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.io.BukkitObjectInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;

public class EditShopParameter implements Listener {
    static CoincraftCore plugin = CoincraftCore.getPlugin(CoincraftCore.class);
    FileConfiguration config = plugin.getConfig();
    String user = config.getString("database.user");
    String password = config.getString("database.password");
    String url = "jdbc:mysql://" + config.getString("database.host") + ":" + config.getString("database.port") + "/" + config.getString("database.database");


    @EventHandler
    public void onPlayerSendMessage(AsyncPlayerChatEvent event) {
        String msg = event.getMessage();
        Player player = event.getPlayer();
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        if (pdc.has(new NamespacedKey(plugin, "shoppriceidedit"), PersistentDataType.STRING)) {
            String id = pdc.get(new NamespacedKey(plugin, "shoppriceidedit"), PersistentDataType.STRING);
            event.setCancelled(true);
            pdc.remove(new NamespacedKey(plugin, "shoppriceidedit"));
            try {
                Double.parseDouble(msg);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Špatný formát čísla. Ruším akci");
                return;
            }
            player.sendMessage("dddd");
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");

                DbManager.ExecuteUpdate("UPDATE " + config.getString("database.prefix") + "shops  SET price=" + msg + " WHERE id=" + id);
                ResultSet rs = DbManager.ExecuteQuery("SELECT * FROM " + config.getString("database.prefix") + "shops WHERE id=" + id);

                while (rs.next()) {
                    String shop_loc_en = rs.getString("shop_location_encoded");
                    byte[] itemSerialized = Base64.getDecoder().decode(shop_loc_en);
                    ByteArrayInputStream in = new ByteArrayInputStream(itemSerialized);
                    BukkitObjectInputStream is = new BukkitObjectInputStream(in);
                    Location shop_loc = (Location) is.readObject();
                    Block block = shop_loc.getBlock();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Sign sign = (Sign) block.getLocation().getBlock().getState();
                            sign.setLine(3, msg + "$");
                            sign.update();
                        }
                    }.runTask(CoincraftCore.getPlugin(CoincraftCore.class));
                }
                player.sendMessage(ChatColor.GREEN + "Cena upravena.");
            } catch (SQLException e) {
                plugin.getLogger().warning(e.toString());
            } catch (ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (pdc.has(new NamespacedKey(plugin, "shopcountidedit"), PersistentDataType.STRING)) {
            event.setCancelled(true);
            String id = pdc.get(new NamespacedKey(plugin, "shopcountidedit"), PersistentDataType.STRING);
            pdc.remove(new NamespacedKey(plugin, "shopcountidedit"));

            try {
                Integer count = Integer.parseInt(msg);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Špatný formát čísla. Ruším akci");
                return;
            }
            player.sendMessage("aaa");

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection connection = DriverManager.getConnection(url, user, password);
                Statement stmnt = connection.createStatement();
                int count = Integer.parseInt(msg);
                String statement = "UPDATE " + config.getString("database.prefix") + "shops SET count=" + count + " WHERE id=" + id;
                stmnt.executeUpdate(statement);
                player.sendMessage(ChatColor.GREEN + "Počet kusů upraven.");
            } catch (SQLException e) {
                plugin.getLogger().warning(e.toString());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }


        }
    }


}
