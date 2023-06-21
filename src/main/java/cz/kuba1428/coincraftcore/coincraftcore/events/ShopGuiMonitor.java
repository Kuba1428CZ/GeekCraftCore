package cz.kuba1428.coincraftcore.coincraftcore.events;

import cz.kuba1428.coincraftcore.coincraftcore.Coincraftcore;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Barrel;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;

import static org.bukkit.Bukkit.createInventory;
import static org.bukkit.Bukkit.getLogger;

public class ShopGuiMonitor implements Listener {
    @EventHandler
    public static void onShop(PlayerInteractEvent event) throws IOException, ClassNotFoundException {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            if (event.getClickedBlock().getType().toString().contains("SIGN")){
                Sign sign = (Sign) event.getClickedBlock().getState();
                Coincraftcore plugin = Coincraftcore.getPlugin(Coincraftcore.class);
                PersistentDataContainer data = sign.getPersistentDataContainer();
                if (data.has(newKey("isshop"), PersistentDataType.INTEGER)){
                    if (data.get(newKey("isshop"), PersistentDataType.INTEGER).equals(1) ){
                        FileConfiguration config = plugin.getConfig();
                        String user = config.getString("database.user");
                        String password = config.getString("database.password");
                        String url = "jdbc:mysql://" + config.getString("database.host") + ":" + config.getString("database.port") + "/" + config.getString("database.database");
                        String owner = null;

                        String storage_loc_encoded = null;
                        String itemstack = null;
                        String shop_type = null;
                        double price = 0;
                        Integer count = null;
                        Integer locked = null;



                        try {
                            Class.forName("com.mysql.cj.jdbc.Driver");
                            Connection connection = DriverManager.getConnection(url, user, password);
                            Statement statement = connection.createStatement();
                            ResultSet resultSet = statement.executeQuery("SELECT owner, shop_location, storage_location_encoded, itemstack, shop_type, price, count, locked, items_in_storage FROM "+config.getString("database.prefix")+"shops WHERE shop_location='"+ event.getClickedBlock().getLocation().toString() +"'");
                            ResultSet rs = resultSet;
                            if (resultSet.next() == false) {
                                event.getPlayer().sendMessage(ChatColor.DARK_RED + "Obchod nelze otevřít z důvodu poškozeného nebo neexistujicího databázového záznamu. Prosím kontaktuj Technický Team serveru");
                                return;
                            }
                            else
                            {
                                owner = rs.getString("owner");
                                storage_loc_encoded = rs.getString("storage_location_encoded");
                                itemstack = rs.getString("itemstack");
                                shop_type = rs.getString("shop_type");
                                price = rs.getInt("price");
                                count = rs.getInt("count");
                                locked = rs.getInt("locked");



                            }
                            statement.close();
                        }catch (Exception e){
                            getLogger().warning("Nastaly problémy při připojování k databázi: " + e);
                        }
                        Location storagelocation = getLoc(storage_loc_encoded);
                        Inventory inven = null;
                        byte[] itemSerialized = Base64.getDecoder().decode(itemstack);
                        ByteArrayInputStream in = new ByteArrayInputStream(itemSerialized);
                        BukkitObjectInputStream is = new BukkitObjectInputStream(in);
                        ItemStack tradingitem = (ItemStack) is.readObject();
                        if (storagelocation.getBlock().getType().equals(Material.BARREL)){
                            Barrel bar = (Barrel) storagelocation.getBlock().getState();
                            inven = bar.getInventory();
                        }else{
                            Chest chst = (Chest) storagelocation.getBlock().getState();
                            inven = chst.getInventory();
                        }
                        Integer items_in_storage = getAmount(inven, tradingitem);
                        Economy economy = Coincraftcore.getEconomy();
                        if (owner.equals(event.getPlayer().getName())){
                            Inventory adminui = createInventory(event.getPlayer(), 9, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Administrace obchodu");

                            ArrayList<String> lore = new ArrayList<>();

                            if (locked.equals(0)){
                                lore.add(ChatColor.WHITE + "Kliknutím znemožníš ostatním");
                                lore.add(ChatColor.WHITE + "hráčům používat tvůj obchod");
                                adminui.setItem(0, newItem("&c&lUzamknout obchod", Material.LIME_DYE, lore));
                            }else {
                                lore.add(ChatColor.WHITE + "Kliknutím umožníš ostatním");
                                lore.add(ChatColor.WHITE + "hráčům používat tvůj obchod");
                                adminui.setItem(0, newItem("&a&lOdemknout obchod", Material.RED_DYE, lore));
                            }
                            lore = new ArrayList<>();
                            adminui.setItem(1, newItem("&e&lZměnit cenu itemu", Material.SUNFLOWER, lore));
                            adminui.setItem(2, newItem("&e&lZměnit počet itemů", Material.PUMPKIN_SEEDS, lore));
                            lore.add(ChatColor.WHITE + "Umožňuje přepnout mezi");
                            lore.add(ChatColor.WHITE +"prodejem a výkupem");
                            lore.add("");
                            if (shop_type.equals("prodej")){
                                lore.add(ChatColor.translateAlternateColorCodes('&',"&fKlikni pro změnu na: &avýkup"));

                            }else {
                                lore.add(ChatColor.translateAlternateColorCodes('&',"&fKlikni pro změnu na: &aprodej"));
                            }


                            adminui.setItem(3, newItem("&e&lZměnit typ obchodu", Material.ITEM_FRAME, lore));
                            ArrayList<String> infolore = new ArrayList<>();
                            infolore.add(ChatColor.translateAlternateColorCodes('&',"&aMajitel: &f" + owner));
                            if (shop_type.toLowerCase().equals("prodej")){
                                infolore.add(ChatColor.translateAlternateColorCodes('&',"&aItemů k dispozici: &f" + items_in_storage.toString()));
                            }else{
                                items_in_storage = getFreeSpace(inven, tradingitem);
                                infolore.add(ChatColor.translateAlternateColorCodes('&',"&aMísta k dispozici: &f" + items_in_storage.toString()));
                            }
                            adminui.setItem(8, newItem("&e&lInfo o obchodu", Material.NAME_TAG , infolore));


                            event.getPlayer().openInventory(adminui);
                        }else{
                            Inventory userui = createInventory(event.getPlayer(), 9, ChatColor.DARK_GREEN + "" + ChatColor.BOLD + shop_type.substring(0,1).toUpperCase() + shop_type.substring(1).toLowerCase() + " itemů");
                            ArrayList<String> infolore = new ArrayList<>();
                            infolore.add(ChatColor.translateAlternateColorCodes('&',"&aMajitel: &f" + owner));
                            if (shop_type.toLowerCase().equals("prodej")){
                                infolore.add(ChatColor.translateAlternateColorCodes('&',"&aItemů k dispozici: &f" + items_in_storage.toString()));
                                ArrayList<String> buyone = new ArrayList<>();
                                ArrayList<String> buyeight = new ArrayList<>();
                                if (count <= items_in_storage){
                                    userui.setItem(0, newItem("&aKoupit &2" + count.toString() + "ks&a za &2" + price + "$&a", Material.LIME_WOOL, buyone));
                                }else {
                                    //Integer counteight = count * 8;
                                    buyone.add(ChatColor.RED + "Nedostatek itemů ve skladišti");
                                    userui.setItem(0, newItem("&aKoupit &2" + count.toString() + "ks&a za &2" + price + "$&a", Material.LIME_STAINED_GLASS, buyone));

                                }
                                Integer counteight = count * 8;
                                double priceeight = price * 8;
                                if (counteight <= items_in_storage){
                                    userui.setItem(1, newItem("&aKoupit &2" + counteight.toString() + "ks&a za &2" + priceeight + "$", Material.LIME_WOOL, buyeight));
                                }else {
                                    //Integer counteight = count * 8;
                                    buyeight.add(ChatColor.RED + "Nedostatek itemů ve skladišti");
                                    userui.setItem(1, newItem("&aKoupit &2" + counteight.toString() + "ks&a za &2" + priceeight + "$", Material.LIME_STAINED_GLASS, buyeight));
                                }
                            }else{
                                ArrayList<String> buyone = new ArrayList<>();
                                ArrayList<String> buyeight = new ArrayList<>();
                                items_in_storage = getFreeSpace(inven, tradingitem);
                                infolore.add(ChatColor.translateAlternateColorCodes('&',"&aMísta k dispozici: &f" + items_in_storage.toString()));
                                if (count <= items_in_storage){
                                    userui.setItem(0, newItem("&aProdat &2" + count.toString() + "ks&a za &2" + price + "$&a", Material.YELLOW_WOOL, buyone));
                                }else {
                                    //Integer counteight = count * 8;
                                    buyone.add(ChatColor.RED + "Nedostatek místa ve skladišti");
                                    userui.setItem(0, newItem("&aProdat &2" + count.toString() + "ks&a za &2" + price + "$&a", Material.YELLOW_STAINED_GLASS, buyone));

                                }
                                Integer counteight = count * 8;
                                double priceeight = price * 8;
                                if (counteight <= items_in_storage){
                                    userui.setItem(1, newItem("&aProdat &2" + counteight.toString() + "ks&a za &2" + priceeight + "$", Material.YELLOW_WOOL, buyeight));
                                }else {
                                    //Integer counteight = count * 8;
                                    buyeight.add(ChatColor.RED + "Nedostatek místa ve skladišti");
                                    userui.setItem(1, newItem("&aProdat &2" + counteight.toString() + "ks&a za &2" + priceeight + "$", Material.YELLOW_STAINED_GLASS, buyeight));
                                }
                            }
                            userui.setItem(8, newItem("&e&lInfo o obchodu", Material.NAME_TAG , infolore));
                            userui.setItem(4, tradingitem);
                            event.getPlayer().openInventory(userui);
                        }
                    }

                }
            }
        }
    }

    public static NamespacedKey newKey(String name){
        return new NamespacedKey(Coincraftcore.getPlugin(Coincraftcore.class), name);
    }
    public static ItemStack newItem(String name, Material material, ArrayList<String> lore){
        ItemStack itemstack = new ItemStack(material);
        ItemMeta itemMeta = itemstack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        itemMeta.setLore(lore);
        itemstack.setItemMeta(itemMeta);
        return itemstack;
    }
    public static Location getLoc(String byt) throws IOException, ClassNotFoundException {
        byte[] itemSerialized = Base64.getDecoder().decode(byt);
        ByteArrayInputStream in = new ByteArrayInputStream(itemSerialized);
        BukkitObjectInputStream is = new BukkitObjectInputStream(in);
        Location loc = (Location) is.readObject();
        return loc;
    }
    public static Integer getAmount(Inventory inventory, ItemStack item)
    {

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
    public static Integer getFreeSpace(Inventory inventory, ItemStack item)
    {

        ItemStack[] items = inventory.getContents();
        Integer count = 0;
        for (ItemStack itm : items) {
            if(itm != null) {
                ItemStack itemone = item.clone();
                ItemStack itmone = itm.clone();
                itmone.setAmount(1);
                itemone.setAmount(1);
                if (itemone.equals(itmone) ){

                    count += item.getMaxStackSize() - itm.getAmount();
                }
            }else {
                count += item.getMaxStackSize();
            }

        }
        return count;
    }
}

