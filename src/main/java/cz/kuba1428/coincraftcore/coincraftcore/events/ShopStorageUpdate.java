package cz.kuba1428.coincraftcore.coincraftcore.events;

import cz.kuba1428.coincraftcore.coincraftcore.Coincraftcore;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Barrel;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Base64;

public class ShopStorageUpdate implements Listener {
    @EventHandler
    public static void onGUIClose(InventoryCloseEvent event) throws SQLException, IOException, ClassNotFoundException {
        Inventory inv = event.getInventory();
        if (inv.getHolder() instanceof Chest || inv.getHolder() instanceof Barrel){
            Location loc = ((Container) inv.getHolder()).getLocation();
            try {
                Connection conn = DriverManager.getConnection(url, user, password);
                Statement stmt = conn.createStatement();
                String sql = "SELECT * FROM " + config.getString("database.prefix") + "shops WHERE storage_location = '" + loc.toString() + "'";
                ResultSet rs = stmt.executeQuery(sql);
                Boolean exists = false;
                while (rs.next()){
                    Integer id = rs.getInt("id");
                    byte[] itemSerialized = Base64.getDecoder().decode(rs.getString("itemstack"));
                    ByteArrayInputStream in = new ByteArrayInputStream(itemSerialized);
                    BukkitObjectInputStream is = new BukkitObjectInputStream(in);
                    ItemStack stack = (ItemStack) is.readObject();
                    Connection conn2 = DriverManager.getConnection(url, user, password);
                    Statement stmt2 = conn2.createStatement();

                    sql = "UPDATE "  + config.getString("database.prefix") + "shops SET items_in_storage = " + getAmount(event.getInventory(), stack) + " WHERE id = '" + id + "'";
                    stmt2.executeUpdate(sql);
                    exists = true;
                }
                if (exists){
                    event.getPlayer().sendMessage(ChatColor.GREEN + "Byl aktualizován počet itemů ve skladišti.");
                }
            }catch (SQLException e){
                plugin.getLogger().warning(e.toString());
            }
        }
    }
    static Coincraftcore plugin = Coincraftcore.getPlugin(Coincraftcore.class);
    static FileConfiguration config = plugin.getConfig();
    static  String user = config.getString("database.user");
    static  String password = config.getString("database.password");
    static String url = "jdbc:mysql://" + config.getString("database.host") + ":" + config.getString("database.port") + "/" + config.getString("database.database");

    public static Integer getAmount(Inventory inventory, ItemStack item) {

        ItemStack[] items = inventory.getContents();
        Integer count = 0;
        for (ItemStack itm : items) {
            if(itm != null) {
                ItemStack itemone = item.clone();
                ItemStack itmone = itm.clone();
                itmone.setAmount(1);
                itemone.setAmount(1);
                if (itemone.equals(itmone) ){

                    count += itm.getAmount();
                }
            }

        }
        return count;
    }
}
