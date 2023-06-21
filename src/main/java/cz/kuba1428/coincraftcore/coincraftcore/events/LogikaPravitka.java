package cz.kuba1428.coincraftcore.coincraftcore.events;

import cz.kuba1428.coincraftcore.coincraftcore.Coincraftcore;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

public class LogikaPravitka implements Listener {
    @EventHandler
    public static void onPlayerInteract(PlayerInteractEvent event) throws IOException {
        Action action = event.getAction();
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();

        Block block = event.getClickedBlock();
        Coincraftcore plugin = Coincraftcore.getPlugin(Coincraftcore.class);
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.WHITE + "S tímto nástrojem můžeš dělat hodně věcí.");
        lore.add(ChatColor.WHITE + "Jak ho použít najdeš na naší wiki");
        lore.add("");
        if (item.containsEnchantment(Enchantment.DURABILITY) && item.getType() == Material.BONE){
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer data = meta.getPersistentDataContainer();
        if (!(event.getAction().toString().contains("AIR"))){
            if (block.getType().toString().contains("SIGN") ){
                if(!(data.has(new NamespacedKey(plugin, "SignX"), PersistentDataType.INTEGER))) {
                    data.set(new NamespacedKey(plugin, "SignX"), PersistentDataType.INTEGER, block.getX());
                    data.set(new NamespacedKey(plugin, "SignY"), PersistentDataType.INTEGER, block.getY());
                    data.set(new NamespacedKey(plugin, "SignZ"), PersistentDataType.INTEGER, block.getZ());
                    data.set(new NamespacedKey(plugin, "SignLocation"), PersistentDataType.STRING, block.getLocation().toString());
                    data.set(new NamespacedKey(plugin, "SignWorld"), PersistentDataType.STRING, block.getWorld().getName());
                    data.set(new NamespacedKey(plugin, "SignLocationEncoded"), PersistentDataType.STRING, encodeLocation(block.getLocation()));
                    item.setItemMeta(meta);
                    player.sendMessage(ChatColor.GREEN + "Pozice cedulky nastavena " + ChatColor.DARK_GREEN + "(" + ChatColor.GREEN + block.getX() + ChatColor.DARK_GREEN + "," + ChatColor.GREEN + block.getY() + ChatColor.DARK_GREEN + "," + ChatColor.GREEN + block.getZ() + ChatColor.DARK_GREEN + ")");
                }else{
                    if (!(data.get(new NamespacedKey(plugin, "SignX"), PersistentDataType.INTEGER) == block.getX() && data.get(new NamespacedKey(plugin, "SignY"), PersistentDataType.INTEGER) == block.getY() && data.get(new NamespacedKey(plugin, "SignZ"), PersistentDataType.INTEGER) == block.getZ() && data.get(new NamespacedKey(plugin, "SignWorld"), PersistentDataType.STRING) == block.getWorld().getName())){
                        data.set(new NamespacedKey(plugin, "SignX"), PersistentDataType.INTEGER, block.getX());
                        data.set(new NamespacedKey(plugin, "SignY"), PersistentDataType.INTEGER, block.getY());
                        data.set(new NamespacedKey(plugin, "SignZ"), PersistentDataType.INTEGER, block.getZ());
                        data.set(new NamespacedKey(plugin, "SignWorld"), PersistentDataType.STRING, block.getWorld().getName());
                        data.set(new NamespacedKey(plugin, "SignLocation"), PersistentDataType.STRING, block.getLocation().toString());
                        data.set(new NamespacedKey(plugin, "SignLocationEncoded"), PersistentDataType.STRING, encodeLocation(block.getLocation()));


                        item.setItemMeta(meta);
                        player.sendMessage(ChatColor.GREEN + "Pozice cedulky nastavena " + ChatColor.DARK_GREEN + "(" + ChatColor.GREEN + block.getX() + ChatColor.DARK_GREEN + "," + ChatColor.GREEN + block.getY() + ChatColor.DARK_GREEN + "," + ChatColor.GREEN + block.getZ() + ChatColor.DARK_GREEN + ")");

                    }
                }
            }else if(block.getType() == Material.CHEST || block.getType() == Material.BARREL){
                if(!(data.has(new NamespacedKey(plugin, "StorageX"), PersistentDataType.INTEGER))) {
                    data.set(new NamespacedKey(plugin, "StorageX"), PersistentDataType.INTEGER, block.getX());
                    data.set(new NamespacedKey(plugin, "StorageY"), PersistentDataType.INTEGER, block.getY());
                    data.set(new NamespacedKey(plugin, "StorageZ"), PersistentDataType.INTEGER, block.getZ());
                    data.set(new NamespacedKey(plugin, "StorageWorld"), PersistentDataType.STRING, block.getWorld().getName());
                    data.set(new NamespacedKey(plugin, "StorageLocationEncoded"), PersistentDataType.STRING, encodeLocation(block.getLocation()));
                    data.set(new NamespacedKey(plugin, "StorageLocation"), PersistentDataType.STRING, block.getLocation().toString());

                    item.setItemMeta(meta);
                    player.sendMessage(ChatColor.GREEN + "Pozice skladiště nastavena " + ChatColor.DARK_GREEN + "(" + ChatColor.GREEN + block.getX() + ChatColor.DARK_GREEN + "," + ChatColor.GREEN + block.getY() + ChatColor.DARK_GREEN + "," + ChatColor.GREEN + block.getZ() + ChatColor.DARK_GREEN + ")");
                }else{
                    if (!(data.get(new NamespacedKey(plugin, "StorageX"), PersistentDataType.INTEGER) == block.getX() && data.get(new NamespacedKey(plugin, "StorageY"), PersistentDataType.INTEGER) == block.getY() && data.get(new NamespacedKey(plugin, "StorageZ"), PersistentDataType.INTEGER) == block.getZ() && data.get(new NamespacedKey(plugin, "StorageWorld"), PersistentDataType.STRING) == block.getWorld().getName())){
                        data.set(new NamespacedKey(plugin, "StorageX"), PersistentDataType.INTEGER, block.getX());
                        data.set(new NamespacedKey(plugin, "StorageY"), PersistentDataType.INTEGER, block.getY());
                        data.set(new NamespacedKey(plugin, "StorageZ"), PersistentDataType.INTEGER, block.getZ());
                        data.set(new NamespacedKey(plugin, "StorageWorld"), PersistentDataType.STRING, block.getWorld().getName());
                        data.set(new NamespacedKey(plugin, "StorageLocation"), PersistentDataType.STRING, block.getLocation().toString());
                        data.set(new NamespacedKey(plugin, "StorageLocationEncoded"), PersistentDataType.STRING, encodeLocation(block.getLocation()));

                        item.setItemMeta(meta);
                        player.sendMessage(ChatColor.GREEN + "Pozice skladiště nastavena " + ChatColor.DARK_GREEN + "(" + ChatColor.GREEN + block.getX() + ChatColor.DARK_GREEN + "," + ChatColor.GREEN + block.getY() + ChatColor.DARK_GREEN + "," + ChatColor.GREEN + block.getZ() + ChatColor.DARK_GREEN + ")");

                    }
                }
            }else{
            if (action == Action.LEFT_CLICK_BLOCK){
                if(!(data.has(new NamespacedKey(plugin, "FirstX"), PersistentDataType.INTEGER))) {
                    data.set(new NamespacedKey(plugin, "FirstX"), PersistentDataType.INTEGER, block.getX());
                    data.set(new NamespacedKey(plugin, "FirstY"), PersistentDataType.INTEGER, block.getY());
                    data.set(new NamespacedKey(plugin, "FirstZ"), PersistentDataType.INTEGER, block.getZ());
                    data.set(new NamespacedKey(plugin, "FirstWorld"), PersistentDataType.STRING, block.getWorld().getName());
                    item.setItemMeta(meta);
                    player.sendMessage(ChatColor.GREEN + "První pozice nastavena " + ChatColor.DARK_GREEN + "(" + ChatColor.GREEN + block.getX() + ChatColor.DARK_GREEN + "," + ChatColor.GREEN + block.getY() + ChatColor.DARK_GREEN + "," + ChatColor.GREEN + block.getZ() + ChatColor.DARK_GREEN + ")");
                }else{
                    if (!(data.get(new NamespacedKey(plugin, "FirstX"), PersistentDataType.INTEGER) == block.getX() && data.get(new NamespacedKey(plugin, "FirstY"), PersistentDataType.INTEGER) == block.getY() && data.get(new NamespacedKey(plugin, "FirstZ"), PersistentDataType.INTEGER) == block.getZ() && data.get(new NamespacedKey(plugin, "FirstWorld"), PersistentDataType.STRING) == block.getWorld().getName())){
                        data.set(new NamespacedKey(plugin, "FirstX"), PersistentDataType.INTEGER, block.getX());
                        data.set(new NamespacedKey(plugin, "FirstY"), PersistentDataType.INTEGER, block.getY());
                        data.set(new NamespacedKey(plugin, "FirstZ"), PersistentDataType.INTEGER, block.getZ());
                        data.set(new NamespacedKey(plugin, "FirstWorld"), PersistentDataType.STRING, block.getWorld().getName());

                        item.setItemMeta(meta);
                        player.sendMessage(ChatColor.GREEN + "První pozice nastavena " + ChatColor.DARK_GREEN + "(" + ChatColor.GREEN + block.getX() + ChatColor.DARK_GREEN + "," + ChatColor.GREEN + block.getY() + ChatColor.DARK_GREEN + "," + ChatColor.GREEN + block.getZ() + ChatColor.DARK_GREEN + ")");

                    }
                }
            }
            if (action == Action.RIGHT_CLICK_BLOCK){
                if(!(data.has(new NamespacedKey(plugin, "SecondX"), PersistentDataType.INTEGER))) {
                    data.set(new NamespacedKey(plugin, "SecondX"), PersistentDataType.INTEGER, block.getX());
                    data.set(new NamespacedKey(plugin, "SecondY"), PersistentDataType.INTEGER, block.getY());
                    data.set(new NamespacedKey(plugin, "SecondZ"), PersistentDataType.INTEGER, block.getZ());
                    data.set(new NamespacedKey(plugin, "SecondWorld"), PersistentDataType.STRING, block.getWorld().getName());
                    item.setItemMeta(meta);
                    player.sendMessage(ChatColor.GREEN + "Druhá pozice nastavena " + ChatColor.DARK_GREEN + "(" + ChatColor.GREEN + block.getX() + ChatColor.DARK_GREEN + "," + ChatColor.GREEN + block.getY() + ChatColor.DARK_GREEN + "," + ChatColor.GREEN + block.getZ() + ChatColor.DARK_GREEN + ")");
                }else{
                    if (!(data.get(new NamespacedKey(plugin, "SecondX"), PersistentDataType.INTEGER) == block.getX() && data.get(new NamespacedKey(plugin, "SecondY"), PersistentDataType.INTEGER) == block.getY() && data.get(new NamespacedKey(plugin, "SecondZ"), PersistentDataType.INTEGER) == block.getZ() && data.get(new NamespacedKey(plugin, "SecondWorld"), PersistentDataType.STRING) == block.getWorld().getName())){
                        data.set(new NamespacedKey(plugin, "SecondX"), PersistentDataType.INTEGER, block.getX());
                        data.set(new NamespacedKey(plugin, "SecondY"), PersistentDataType.INTEGER, block.getY());
                        data.set(new NamespacedKey(plugin, "SecondZ"), PersistentDataType.INTEGER, block.getZ());
                        data.set(new NamespacedKey(plugin, "SecondWorld"), PersistentDataType.STRING, block.getWorld().getName());
                        item.setItemMeta(meta);
                        player.sendMessage(ChatColor.GREEN + "Druhá pozice nastavena " + ChatColor.DARK_GREEN + "(" + ChatColor.GREEN + block.getX() + ChatColor.DARK_GREEN + "," + ChatColor.GREEN + block.getY() + ChatColor.DARK_GREEN + "," + ChatColor.GREEN + block.getZ() + ChatColor.DARK_GREEN + ")");
                    }
                }

            }}
            if (data.has(new NamespacedKey(plugin, "FirstX"), PersistentDataType.INTEGER)){
                lore.add(ChatColor.DARK_PURPLE + "První pozice " + ChatColor.LIGHT_PURPLE + "("+ data.get(new NamespacedKey(plugin, "FirstX"), PersistentDataType.INTEGER) +","+data.get(new NamespacedKey(plugin, "FirstY"), PersistentDataType.INTEGER)+","+data.get(new NamespacedKey(plugin, "FirstZ"), PersistentDataType.INTEGER)+")");
            }else{
                lore.add(ChatColor.DARK_PURPLE + "První pozice " + ChatColor.LIGHT_PURPLE + "(-,-,-)");
            }
            if (data.has(new NamespacedKey(plugin, "SecondX"), PersistentDataType.INTEGER)){
                lore.add(ChatColor.DARK_PURPLE + "Druhá pozice " + ChatColor.LIGHT_PURPLE + "("+ data.get(new NamespacedKey(plugin, "SecondX"), PersistentDataType.INTEGER) +","+data.get(new NamespacedKey(plugin, "SecondY"), PersistentDataType.INTEGER)+","+data.get(new NamespacedKey(plugin, "SecondZ"), PersistentDataType.INTEGER)+")");
            }else{
                lore.add(ChatColor.DARK_PURPLE + "Druhá pozice " + ChatColor.LIGHT_PURPLE + "(-,-,-)");
            }
            if (data.has(new NamespacedKey(plugin, "StorageX"), PersistentDataType.INTEGER)) {
                lore.add(ChatColor.DARK_PURPLE + "Pozice úložiště " + ChatColor.LIGHT_PURPLE + "(" + data.get(new NamespacedKey(plugin, "StorageX"), PersistentDataType.INTEGER) + "," + data.get(new NamespacedKey(plugin, "StorageY"), PersistentDataType.INTEGER) + "," + data.get(new NamespacedKey(plugin, "StorageZ"), PersistentDataType.INTEGER) + ")");
            }
            if (data.has(new NamespacedKey(plugin, "SignX"), PersistentDataType.INTEGER)){
                lore.add(ChatColor.DARK_PURPLE + "Pozice cedulky " + ChatColor.LIGHT_PURPLE + "("+ data.get(new NamespacedKey(plugin, "SignX"), PersistentDataType.INTEGER) +","+data.get(new NamespacedKey(plugin, "SignY"), PersistentDataType.INTEGER)+","+data.get(new NamespacedKey(plugin, "SignZ"), PersistentDataType.INTEGER)+")");
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }}
    }
    public static String encodeLocation(Location itmstck) throws IOException {
        ByteArrayOutputStream io = new ByteArrayOutputStream();
        BukkitObjectOutputStream os = new BukkitObjectOutputStream(io);
        os.writeObject(itmstck);
        os.flush();
        byte[] serializedObject = io.toByteArray();
        String encodedItem = Base64.getEncoder().encodeToString(serializedObject);
        return encodedItem;
    }
}
