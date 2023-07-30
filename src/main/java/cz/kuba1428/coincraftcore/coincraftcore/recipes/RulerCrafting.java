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

public class RulerCrafting {
    public static ItemStack ruler_stack;

    public static void init() {
        createRuler();
    }

    private static void createRuler() {
        ItemStack stick = new ItemStack(Material.BONE);
        ItemMeta stick_meta = stick.getItemMeta();
        assert stick_meta != null;
        stick_meta.setDisplayName(ChatColor.YELLOW + "Pravítko");
        stick_meta.addEnchant(Enchantment.DURABILITY, 69, true);
        stick_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.WHITE + "S tímto nástrojem můžeš dělat hodně věcí.");
        lore.add(ChatColor.WHITE + "Jak ho použít najdeš na naší wiki");
        lore.add("");
        lore.add(ChatColor.DARK_PURPLE + "První pozice " + ChatColor.LIGHT_PURPLE + "(-,-,-)");
        lore.add(ChatColor.DARK_PURPLE + "Druhá pozice " + ChatColor.LIGHT_PURPLE + "(-,-,-)");
        stick_meta.setLore(lore);
        stick.setItemMeta(stick_meta);
        ruler_stack = stick;
        ShapelessRecipe ruler_recipe = new ShapelessRecipe(NamespacedKey.minecraft("pravitko_shapeless"), ruler_stack);
        ruler_recipe.addIngredient(Material.BONE);
        ruler_recipe.addIngredient(Material.LAPIS_LAZULI);
        Bukkit.addRecipe(ruler_recipe);
    }
}
