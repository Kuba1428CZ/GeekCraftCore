package cz.kuba1428.coincraftcore.coincraftcore.recipes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;


import java.util.ArrayList;

public class pravitkocrafting{
    public static ItemStack pravitkostack;
    public static void init(){createPravitko();}
    private static void createPravitko() {
        ItemStack stick = new ItemStack(Material.BONE);
        ItemMeta stickmeta = stick.getItemMeta();
        assert stickmeta != null;
        stickmeta.setDisplayName(ChatColor.YELLOW + "Pravítko");
        stickmeta.addEnchant(Enchantment.DURABILITY, 69, true);
        stickmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.WHITE + "S tímto nástrojem můžeš dělat hodně věcí.");
        lore.add(ChatColor.WHITE + "Jak ho použít najdeš na naší wiki");
        lore.add("");
        lore.add(ChatColor.DARK_PURPLE + "První pozice " + ChatColor.LIGHT_PURPLE + "(-,-,-)");
        lore.add(ChatColor.DARK_PURPLE + "Druhá pozice " + ChatColor.LIGHT_PURPLE + "(-,-,-)");
        stickmeta.setLore(lore);
        stick.setItemMeta(stickmeta);
        pravitkostack = stick;
        ShapelessRecipe pravitkorecipe = new ShapelessRecipe(NamespacedKey.minecraft("pravitko_shapeless"), pravitkostack);
        pravitkorecipe.addIngredient(Material.BONE);
        Bukkit.addRecipe(pravitkorecipe);
    }
}
