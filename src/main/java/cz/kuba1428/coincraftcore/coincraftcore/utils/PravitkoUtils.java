package cz.kuba1428.coincraftcore.coincraftcore.utils;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Objects;

public class PravitkoUtils {
    public static Boolean isPravitko(ItemStack item){
        return item.getType() == Material.BONE && Objects.requireNonNull(item.getItemMeta()).hasEnchant(Enchantment.DURABILITY);
    }
    public static HashMap<String, Object> getData(ItemStack item){
        HashMap<String, Object> hashmap = new HashMap<>();
        PersistentDataContainer data = Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer();
        if ((data.has(key("StorageX"), PersistentDataType.INTEGER))) {
            hashmap.put("StorageX", data.get(key("StorageX"), PersistentDataType.INTEGER));
            hashmap.put("StorageY", data.get(key("StorageY"), PersistentDataType.INTEGER));
            hashmap.put("StorageZ", data.get(key("StorageZ"), PersistentDataType.INTEGER));
            hashmap.put("StorageWorld", data.get(key("StorageWorld"), PersistentDataType.STRING));
            hashmap.put("StorageLocation", data.get(key("StorageLocation"), PersistentDataType.STRING));
            hashmap.put("StorageLocationEncoded", data.get(key("StorageLocationEncoded"), PersistentDataType.STRING));
        }
        if ((data.has(key("SignX"), PersistentDataType.INTEGER))) {
            hashmap.put("SignX", data.get(key("SignX"), PersistentDataType.INTEGER));
            hashmap.put("SignY", data.get(key("SignY"), PersistentDataType.INTEGER));
            hashmap.put("SignZ", data.get(key("SignZ"), PersistentDataType.INTEGER));
            hashmap.put("SignWorld", data.get(key("SignWorld"), PersistentDataType.STRING));
            hashmap.put("SignLocation", data.get(key("SignLocation"), PersistentDataType.STRING));
            hashmap.put("SignLocationEncoded", data.get(key("SignLocationEncoded"), PersistentDataType.STRING));
        }
        if ((data.has(key("FirstX"), PersistentDataType.INTEGER))) {
            hashmap.put("FirstX", data.get(key("FirstX"), PersistentDataType.INTEGER));
            hashmap.put("FirstY", data.get(key("FirstY"), PersistentDataType.INTEGER));
            hashmap.put("FirstZ", data.get(key("FirstZ"), PersistentDataType.INTEGER));
            hashmap.put("FirstWorld", data.get(key("FirstWorld"), PersistentDataType.STRING));

        }
        if ((data.has(key("SecondX"), PersistentDataType.INTEGER))) {
            hashmap.put("SecondX", data.get(key("SecondX"), PersistentDataType.INTEGER));
            hashmap.put("SecondY", data.get(key("SecondY"), PersistentDataType.INTEGER));
            hashmap.put("SecondZ", data.get(key("SecondZ"), PersistentDataType.INTEGER));
            hashmap.put("SecondWorld", data.get(key("SecondWorld"), PersistentDataType.STRING));

        }
        return hashmap;
    }
    public static  NamespacedKey key(String name){
        return new NamespacedKey(GlobalVars.plugin, name);
    }
}
