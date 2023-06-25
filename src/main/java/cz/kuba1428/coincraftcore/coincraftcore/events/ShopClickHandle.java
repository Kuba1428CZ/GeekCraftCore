package cz.kuba1428.coincraftcore.coincraftcore.events;

import cz.kuba1428.coincraftcore.coincraftcore.CoincraftCore;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Barrel;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
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

import static cz.kuba1428.coincraftcore.coincraftcore.events.ShopGuiMonitor.newItem;


public class ShopClickHandle implements Listener {
    @EventHandler
    public static void onGuiClick(InventoryClickEvent event) throws SQLException, IOException, ClassNotFoundException {
        Player player = (Player) event.getWhoClicked();
        if (event.getCurrentItem() != null) {
            if (event.getView().getTitle().contains("Administrace obchodu") || event.getView().getTitle().contains("Prodej itemů") || event.getView().getTitle().contains("Výkup itemů")) {
                FileConfiguration config = plugin.getConfig();
                event.setCancelled(true);
                Location blockLocation = event.getWhoClicked().getTargetBlock(null, 10).getLocation();
                Location storage_location;
                String storage_location_encoded = null;
                String shop_type = null;
                String owner = null;
                Integer id = null;
                double price = 0;
                Integer count = null;
                Integer items_in_storage = null;
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection connection = DriverManager.getConnection(url, user, password);
                    Statement stmnt = connection.createStatement();


                    ResultSet rs = stmnt.executeQuery("SELECT storage_location_encoded, shop_type, owner, id, price, count, items_in_storage FROM " + config.getString("database.prefix") + "shops WHERE shop_location='" + blockLocation.toString() + "' AND server='" + config.getString("server.id") + "'");
                    while (rs.next()) {
                        //String firstName = rs.getString("first_name");
                        storage_location_encoded = rs.getString("storage_location_encoded");
                        price = rs.getInt("price");
                        count = rs.getInt("count");
                        shop_type = rs.getString("shop_type");
                        id = rs.getInt("id");
                        owner = rs.getString("owner");
                        items_in_storage = rs.getInt("items_in_storage");

                        byte[] locSerialized = Base64.getDecoder().decode(storage_location_encoded);
                        ByteArrayInputStream in = new ByteArrayInputStream(locSerialized);
                        BukkitObjectInputStream is = new BukkitObjectInputStream(in);
                        storage_location = (Location) is.readObject();

                    }
                } catch (SQLException e) {
                    plugin.getLogger().warning(e.toString());
                } catch (ClassNotFoundException | IOException e) {
                    throw new RuntimeException(e);
                }
                Economy economy = CoincraftCore.getEconomy();


                if (event.getView().getTitle().contains("Administrace obchodu")) {
                    Inventory inv = event.getInventory();
                    if (event.getSlot() == 2) {
                        PersistentDataContainer pdata = event.getWhoClicked().getPersistentDataContainer();
                        pdata.set(new NamespacedKey(plugin, "shopcountidedit"), PersistentDataType.STRING, id.toString());
                        event.getWhoClicked().sendMessage(ChatColor.GREEN + "Napiš nový počet kusů");
                        event.getWhoClicked().closeInventory();
                    }
                    if (event.getSlot() == 1) {
                        PersistentDataContainer pdata = event.getWhoClicked().getPersistentDataContainer();
                        pdata.set(new NamespacedKey(plugin, "shoppriceidedit"), PersistentDataType.STRING, id.toString());
                        event.getWhoClicked().sendMessage(ChatColor.GREEN + "Napiš novou cenu");
                        event.getWhoClicked().closeInventory();

                    }
                    if (event.getSlot() == 0) {
                        ItemStack item = inv.getItem(0);
                        String loc = event.getWhoClicked().getTargetBlock(null, 5).getLocation().toString();
                        ArrayList<String> lore = new ArrayList<>();
                        if (item.getType().equals(Material.RED_DYE)) {
                            try {
                                Class.forName("com.mysql.cj.jdbc.Driver");
                                Connection connection = DriverManager.getConnection(url, user, password);
                                Statement stmnt = connection.createStatement();
                                stmnt.executeUpdate("UPDATE " + config.getString("database.prefix") + "shops SET locked = 0 WHERE shop_location='" + loc + "'");
                                lore.add(ChatColor.WHITE + "Kliknutím znemožníš ostatním");
                                lore.add(ChatColor.WHITE + "hráčům používat tvůj obchod");
                                inv.setItem(0, newItem("&c&lUzamknout obchod", Material.LIME_DYE, lore));
                            } catch (SQLException e) {
                                plugin.getLogger().warning(e.toString());
                            }
                        } else {
                            try {
                                Class.forName("com.mysql.cj.jdbc.Driver");
                                Connection connection = DriverManager.getConnection(url, user, password);
                                Statement stmnt = connection.createStatement();
                                stmnt.executeUpdate("UPDATE " + config.getString("database.prefix") + "shops SET locked = 1 WHERE shop_location='" + loc + "'");
                                lore.add(ChatColor.WHITE + "Kliknutím umožníš ostatním");
                                lore.add(ChatColor.WHITE + "hráčům používat tvůj obchod");
                                inv.setItem(0, newItem("&a&lOdemknout obchod", Material.RED_DYE, lore));
                            } catch (SQLException e) {
                                plugin.getLogger().warning(e.toString());
                            }

                        }
                    }
                    if (event.getSlot() == 3) {
                        ItemStack item = inv.getItem(3);
                        String loc = event.getWhoClicked().getTargetBlock(null, 5).getLocation().toString();
                        ArrayList<String> lore = new ArrayList<>();
                        lore.add(ChatColor.WHITE + "Umožňuje přepnout mezi");
                        lore.add(ChatColor.WHITE + "prodejem a výkupem");
                        lore.add("");
                        if (shop_type.equals("prodej")) {
                            try {
                                Class.forName("com.mysql.cj.jdbc.Driver");
                                Connection connection = DriverManager.getConnection(url, user, password);
                                Statement stmnt = connection.createStatement();
                                stmnt.executeUpdate("UPDATE " + config.getString("database.prefix") + "shops SET shop_type = 'výkup' WHERE shop_location='" + loc + "'");
                                lore.add(ChatColor.translateAlternateColorCodes('&', "&fKlikni pro změnu na: &aprodej"));
                            } catch (SQLException e) {
                                plugin.getLogger().warning(e.toString());
                            }
                        } else {
                            try {
                                Class.forName("com.mysql.cj.jdbc.Driver");
                                Connection connection = DriverManager.getConnection(url, user, password);
                                Statement stmnt = connection.createStatement();
                                stmnt.executeUpdate("UPDATE " + config.getString("database.prefix") + "shops SET shop_type = 'prodej' WHERE shop_location='" + loc + "'");
                                lore.add(ChatColor.translateAlternateColorCodes('&', "&fKlikni pro změnu na: &avýkup"));

                            } catch (SQLException e) {
                                plugin.getLogger().warning(e.toString());
                            }

                        }
                        inv.setItem(3, newItem("&e&lZměnit typ obchodu", Material.ITEM_FRAME, lore));

                    }
                }
                if (event.getView().getTitle().contains("Prodej itemů")) {

                    //event.getWhoClicked().sendMessage(count.toString() + " " + price + " " + items_in_storage.toString() );
                    double cost = price;
                    int itm_count = count;
                    if (event.getSlot() == 1) {
                        cost = cost * 8;
                        itm_count = itm_count * 8;
                    }
                    if (economy.getBalance(player) >= cost) {
                        byte[] itemSerialized = Base64.getDecoder().decode(storage_location_encoded);
                        ByteArrayInputStream in = new ByteArrayInputStream(itemSerialized);
                        BukkitObjectInputStream is = new BukkitObjectInputStream(in);
                        Location storage_loc = (Location) is.readObject();
                        Material block_material = storage_loc.getBlock().getType();
                        Inventory storage_inventory = null;
                        if (block_material.equals(Material.CHEST)) {
                            Chest chst = (Chest) storage_loc.getBlock().getState();
                            storage_inventory = chst.getInventory();
                        } else {
                            Barrel chst = (Barrel) storage_loc.getBlock().getState();
                            storage_inventory = chst.getInventory();
                        }
                        if (getAmount(storage_inventory, event.getInventory().getItem(4)) >= itm_count) {
                            if (getFreeSpaceInPlayerInventory(player.getInventory(), event.getInventory().getItem(4)) >= itm_count) {

                                try {
                                    Class.forName("com.mysql.cj.jdbc.Driver");
                                    Connection connection = DriverManager.getConnection(url, user, password);
                                    Statement stmnt = connection.createStatement();
                                    stmnt.executeUpdate("UPDATE " + config.getString("database.prefix") + "shops SET items_in_storage='" + getAmount(storage_inventory, event.getInventory().getItem(4)) + "' WHERE id='" + id + "'");
                                    ItemStack itm_with_amount = event.getInventory().getItem(4).clone();
                                    itm_with_amount.setAmount(itm_count);
                                    storage_inventory.removeItem(itm_with_amount);
                                    player.getInventory().addItem(itm_with_amount);
                                    economy.withdrawPlayer(player, cost);
                                    economy.depositPlayer(owner, cost - cost * config.getDouble("economy.shop_tax"));
                                    double old_value = config.getDouble("economy.current_central_money");
                                    Double new_value = old_value + cost * config.getDouble("economy.shop_tax");
                                    config.set("economy.current_central_money", new_value);
                                    if (plugin.getServer().getPlayer(owner).isOnline()) {
                                        plugin.getServer().getPlayer(owner).sendMessage(ChatColor.GREEN + "Hráč " + player.getName() + " si od tebe koupil " + itm_count + "x " + itm_with_amount.getType().toString().toLowerCase());
                                    }
                                    player.sendMessage(ChatColor.GREEN + "Koupil jsi " + itm_count + "x " + itm_with_amount.getType().toString().toLowerCase() + "od hráče " + owner);
                                    plugin.saveConfig();
                                } catch (SQLException e) {
                                    plugin.getLogger().warning(e.toString());
                                }

                            } else {
                                player.sendMessage(ChatColor.RED + "Nemáš dost místa v inventáři");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "V obchodě není dost zboží");
                        }

                    } else {
                        event.getWhoClicked().sendMessage(ChatColor.RED + "Nemáš dostatek peněz.");
                    }


                }
                if (event.getView().getTitle().contains("Výkup itemů")) {
                    event.getWhoClicked().sendMessage("Výkup" + event.getSlot());
                    double cost = price;
                    int itm_count = count;
                    if (event.getSlot() == 1) {
                        cost = cost * 8;
                        itm_count = itm_count * 8;
                    }
                    if (economy.getBalance(player) >= cost) {
                        byte[] itemSerialized = Base64.getDecoder().decode(storage_location_encoded);
                        ByteArrayInputStream in = new ByteArrayInputStream(itemSerialized);
                        BukkitObjectInputStream is = new BukkitObjectInputStream(in);
                        Location storage_loc = (Location) is.readObject();
                        Material block_material = storage_loc.getBlock().getType();
                        Inventory storage_inventory = null;
                        if (block_material.equals(Material.CHEST)) {
                            Chest chst = (Chest) storage_loc.getBlock().getState();
                            storage_inventory = chst.getInventory();
                        } else {
                            Barrel chst = (Barrel) storage_loc.getBlock().getState();
                            storage_inventory = chst.getInventory();
                        }
                        if (getAmount(player.getInventory(), event.getInventory().getItem(4)) >= itm_count) {
                            if (getFreeSpace(storage_inventory, event.getInventory().getItem(4)) >= itm_count) {

                                try {
                                    Class.forName("com.mysql.cj.jdbc.Driver");
                                    Connection connection = DriverManager.getConnection(url, user, password);
                                    Statement stmnt = connection.createStatement();
                                    stmnt.executeUpdate("UPDATE " + config.getString("database.prefix") + "shops SET items_in_storage='" + getAmount(storage_inventory, event.getInventory().getItem(4)) + "' WHERE id='" + id + "'");
                                    ItemStack itm_with_amount = event.getInventory().getItem(4).clone();
                                    itm_with_amount.setAmount(itm_count);
                                    player.getInventory().removeItem(itm_with_amount);
                                    storage_inventory.addItem(itm_with_amount);
                                    economy.withdrawPlayer(owner, cost);
                                    economy.depositPlayer(player, cost - cost * config.getDouble("economy.shop_tax"));
                                    double old_value = config.getDouble("economy.current_central_money");
                                    Double new_value = old_value + cost * config.getDouble("economy.shop_tax");
                                    config.set("economy.current_central_money", new_value);
                                    if (plugin.getServer().getPlayer(owner).isOnline()) {
                                        plugin.getServer().getPlayer(owner).sendMessage(ChatColor.GREEN + "Hráč " + player.getName() + " ti prodal " + itm_count + "x " + itm_with_amount.getType().toString().toLowerCase());
                                    }
                                    player.sendMessage(ChatColor.GREEN + "Prodal jsi " + itm_count + "x " + itm_with_amount.getType().toString().toLowerCase() + " hráči " + owner);
                                    plugin.saveConfig();
                                } catch (SQLException e) {
                                    plugin.getLogger().warning(e.toString());
                                }

                            } else {
                                player.sendMessage(ChatColor.RED + "V obchodě není dost místa");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Nemáš dost itemů");
                        }

                    } else {
                        event.getWhoClicked().sendMessage(ChatColor.RED + "Majitel obchodu nemá dostatek peněz.");
                    }


                }
            }

        }

    }

    static CoincraftCore plugin = CoincraftCore.getPlugin(CoincraftCore.class);
    static FileConfiguration config = plugin.getConfig();
    static String user = config.getString("database.user");
    static String password = config.getString("database.password");
    static String url = "jdbc:mysql://" + config.getString("database.host") + ":" + config.getString("database.port") + "/" + config.getString("database.database");

    public static Integer getAmount(Inventory inventory, ItemStack item) {

        ItemStack[] items = inventory.getContents();
        int count = 0;
        for (ItemStack itm : items) {
            if (itm != null) {
                ItemStack itemone = item.clone();
                ItemStack itmone = itm.clone();
                itmone.setAmount(1);
                itemone.setAmount(1);
                if (itemone.equals(itmone)) {

                    count += itm.getAmount();
                }
            }

        }
        return count;
    }

    public static Integer getFreeSpaceInPlayerInventory(Inventory inventory, ItemStack item) {

        ItemStack[] items = inventory.getContents();
        int count = 0;
        int lp = 0;
        for (ItemStack itm : items) {

            if (lp < 36) {
                lp = 1 + lp;
                if (itm != null) {
                    ItemStack itemone = item.clone();
                    ItemStack itmone = itm.clone();
                    itmone.setAmount(1);
                    itemone.setAmount(1);
                    if (itemone.equals(itmone)) {

                        count += item.getMaxStackSize() - itm.getAmount();
                    }
                } else {
                    count += item.getMaxStackSize();
                }
            }


        }
        return count;
    }

    public static Integer getFreeSpace(Inventory inventory, ItemStack item) {

        ItemStack[] items = inventory.getContents();
        int count = 0;
        for (ItemStack itm : items) {
            if (itm != null) {
                ItemStack itemone = item.clone();
                ItemStack itmone = itm.clone();
                itmone.setAmount(1);
                itemone.setAmount(1);
                if (itemone.equals(itmone)) {

                    count += item.getMaxStackSize() - itm.getAmount();
                }
            } else {
                count += item.getMaxStackSize();
            }

        }
        return count;
    }


}
