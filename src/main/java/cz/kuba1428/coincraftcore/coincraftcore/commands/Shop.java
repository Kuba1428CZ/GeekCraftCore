package cz.kuba1428.coincraftcore.coincraftcore.commands;

import cz.kuba1428.coincraftcore.coincraftcore.CoincraftCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Barrel;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;

import static org.bukkit.Bukkit.getLogger;
              /* _/_/_/    _/_/_/
              _/        _/
             _/  _/_/  _/
            _/    _/  _/
             _/_/_/    _/_/_/*/


public class Shop implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String expression, @NotNull String[] args) {
        if (sender instanceof Player) {
            sender.sendMessage(ChatColor.RED + "Toto může jen hráč");
            return true;
        }

        if (args.length != 3) {
            sender.sendMessage(ChatColor.RED + "Špatně zadané údaje! Správné použití:" + "\n" + ChatColor.WHITE + "/shop <prodej/výkup> <počet ks> <cena>");
            return true;
        }
        Player player = (Player) sender;
        if (!(((player.getInventory().getItemInOffHand().getType() == Material.BONE && player.getInventory().getItemInOffHand().getItemMeta().hasEnchant(Enchantment.DURABILITY)) || (player.getItemInHand().getType() == Material.BONE && player.getItemInHand().getItemMeta().hasEnchant(Enchantment.DURABILITY))))) {
            sender.sendMessage(ChatColor.RED + "V jedné ruce musíš mít pravítko a v druhé item se kterým chceš obchodovat.");
            return true;
        }
        if (!(player.getItemInHand().getType() != Material.AIR && player.getInventory().getItemInOffHand().getType() != Material.AIR)) {
            sender.sendMessage(ChatColor.RED + "V jedné ruce musíš mít pravítko a v druhé item se kterým chceš obchodovat.");
            return true;
        }
        ArrayList<ItemStack> items = new ArrayList<>();
        if (player.getItemInHand().getType() == Material.BONE && player.getItemInHand().getItemMeta().hasEnchant(Enchantment.DURABILITY)) {
            items.add(player.getItemInHand());
            items.add(player.getInventory().getItemInOffHand());
        } else {
            items.add(player.getInventory().getItemInOffHand());
            items.add(player.getItemInHand());
        }
        CoincraftCore plugin = CoincraftCore.getPlugin(CoincraftCore.class);
        ItemMeta meta = items.get(0).getItemMeta();

        PersistentDataContainer data = meta.getPersistentDataContainer();
        if (!(data.has(new NamespacedKey(plugin, "SignX"), PersistentDataType.INTEGER) && data.has(new NamespacedKey(plugin, "StorageX"), PersistentDataType.INTEGER))) {
            sender.sendMessage(ChatColor.RED + "Musíš nejdříve pravítkem označit cedulku a úložiště (truhla nebo barel)");
            return true;
        }
        Location SignLocation = new Location(Bukkit.getServer().getWorld(data.get(new NamespacedKey(plugin, "SignWorld"), PersistentDataType.STRING)), data.get(new NamespacedKey(plugin, "SignX"), PersistentDataType.INTEGER), data.get(new NamespacedKey(plugin, "SignY"), PersistentDataType.INTEGER), data.get(new NamespacedKey(plugin, "SignZ"), PersistentDataType.INTEGER));
        Location StorageLocation = new Location(Bukkit.getServer().getWorld(data.get(new NamespacedKey(plugin, "StorageWorld"), PersistentDataType.STRING)), data.get(new NamespacedKey(plugin, "StorageX"), PersistentDataType.INTEGER), data.get(new NamespacedKey(plugin, "StorageY"), PersistentDataType.INTEGER), data.get(new NamespacedKey(plugin, "StorageZ"), PersistentDataType.INTEGER));
        Sign sign = (Sign) SignLocation.getBlock().getState();
        PersistentDataContainer signdata = sign.getPersistentDataContainer();
        if (!(signdata.has(newKey("isshop"), PersistentDataType.INTEGER))) {
            sender.sendMessage(ChatColor.RED + "Na tomto místě již obchod je!");
            return true;
        }
        if (!(SignLocation.getBlock().getType().toString().contains("SIGN") && (StorageLocation.getBlock().getType() == Material.CHEST || StorageLocation.getBlock().getType() == Material.BARREL))) {
            sender.sendMessage(ChatColor.RED + "Úložiště nebo cedulka byla zbořena nebo je nedostupná! Označte úložiště a cedulku znova.");
            return true;
        }
        if (!(plugin.isInteger(args[1]) && plugin.isInteger(args[2]))) {
            sender.sendMessage(ChatColor.RED + "Špatně zadané údaje! Správné použití:" + "\n" + ChatColor.WHITE + "/shop <prodej/výkup> <počet ks> <cena>");
            return true;
        }
        FileConfiguration config = plugin.getConfig();
        String user = config.getString("database.user");
        String password = config.getString("database.password");
        String url = "jdbc:mysql://" + config.getString("database.host") + ":" + config.getString("database.port") + "/" + config.getString("database.database");
        Inventory storage = null;
        PersistentDataContainer storagedata = null;
        if (StorageLocation.getBlock().getType() == Material.CHEST) {
            Chest chest = (Chest) StorageLocation.getBlock().getState();
            storage = chest.getInventory();
            storagedata = chest.getPersistentDataContainer();
        } else {
            Barrel barrel = (Barrel) StorageLocation.getBlock().getState();
            storage = barrel.getInventory();
            storagedata = barrel.getPersistentDataContainer();
        }
        boolean success = false;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);
            Statement statement = connection.createStatement();
            ItemStack clone = items.get(1).clone();
            clone.setAmount(1);

            String encodedItem = encodeItemstack(clone);
            statement.executeUpdate("INSERT INTO " + config.getString("database.prefix") + "shops" + " (owner, server, shop_location, itemstack, material, shop_type, price, items_in_storage, count, storage_location, shop_location_encoded, storage_location_encoded) VALUES " +
                    "('" + player.getName() + "','" + config.getString("server.id") + "'," + '"' + data.get(newKey("SignLocation"), PersistentDataType.STRING) + '"' + ",'"
                    + encodedItem + "','" + items.get(1).getType() + "','" + args[0].toLowerCase() + "','" + args[2] + "','" + getAmount(storage, items.get(1)) + "','" + args[1] + "','"
                    + data.get(newKey("StorageLocation"), PersistentDataType.STRING) + "','" + data.get(newKey("SignLocationEncoded"), PersistentDataType.STRING) + "','" + data.get(newKey("StorageLocationEncoded"), PersistentDataType.STRING) + "')");
            statement.close();

            sign.setLine(0, player.getDisplayName());
            sign.setLine(1, args[0]);
            sign.setLine(2, items.get(1).getType().toString().toLowerCase());
            sign.setLine(3, args[2] + "$");
            signdata.set(newKey("isshop"), PersistentDataType.INTEGER, 1);
            signdata.set(newKey("shopowner"), PersistentDataType.STRING, player.getName());
            storagedata.set(newKey("shopowner"), PersistentDataType.STRING, player.getName());
            storagedata.set(newKey("isstorage"), PersistentDataType.INTEGER, 1);
            sign.update();
            if (StorageLocation.getBlock().getType() == Material.CHEST) {
                Chest chest = (Chest) StorageLocation.getBlock().getState();
                chest.update();
            } else {
                Barrel barrel = (Barrel) StorageLocation.getBlock().getState();
                barrel.update();
            }
            sender.sendMessage(ChatColor.GREEN + "Obchod vytvořen");
        } catch (Exception e) {
            getLogger().warning("Nastaly problémy při připojování k databázi: " + e);
        }

        return true;
    }

    public static int getAmount(Inventory inventory, ItemStack item) {

        ItemStack[] items = inventory.getContents();
        int count = 0;
        for (ItemStack itm : items) {
            ItemStack itemone = item.clone();
            if (itm != null) {
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

    public static NamespacedKey newKey(String name) {
        return new NamespacedKey(CoincraftCore.getPlugin(CoincraftCore.class), name);
    }

    public static String encodeItemstack(ItemStack itmstck) throws IOException {
        ByteArrayOutputStream io = new ByteArrayOutputStream();
        BukkitObjectOutputStream os = new BukkitObjectOutputStream(io);
        os.writeObject(itmstck);
        os.flush();
        byte[] serializedObject = io.toByteArray();
        return Base64.getEncoder().encodeToString(serializedObject);
    }

}
