package cz.kuba1428.coincraftcore.coincraftcore.commands;

import cz.kuba1428.coincraftcore.coincraftcore.CoincraftCore;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class PublicResources implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        FileConfiguration config = CoincraftCore.getPlugin(CoincraftCore.class).getConfig();
        Economy economy = CoincraftCore.getEconomy();
        DecimalFormat format = new DecimalFormat("0.00");
        sender.sendMessage(ChatColor.GREEN + "Aktuální veřejné finanční prostředky činí: " + format.format(config.getDouble("economy.current_central_money")) + "$");
        Player player = (Player) sender;

        // Create the clickable message
        TextComponent message = new TextComponent("Click me!");
        message.setColor(ChatColor.GREEN.asBungee());
        message.setBold(true);
        message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/say hi"));

        // Set the hover text
        TextComponent hoverText = new TextComponent("This is the hover text!");
        hoverText.setColor(ChatColor.YELLOW.asBungee());
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{hoverText}));

        // Send the clickable message to the player
        player.spigot().sendMessage(message);
        return true;
    }
}
