package cz.kuba1428.coincraftcore.coincraftcore.commands;

import com.sk89q.worldguard.protection.managers.storage.StorageException;
import cz.kuba1428.coincraftcore.coincraftcore.utils.GlobalVars;
import cz.kuba1428.coincraftcore.coincraftcore.utils.PravitkoUtils;
import cz.kuba1428.coincraftcore.coincraftcore.managers.WgManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class pozemek implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
            if (args[0].equals("member")){
                if (WgManager.doesExist(args[1])){
                    assert player != null;
                    if (WgManager.isOwner(player, args[1])){
                        switch (args[2]){
                            case "add":
                                if (args[3] != null && args[4] != null){
                                    Player member = Bukkit.getPlayer(args[3]);
                                    switch (args[4]){
                                        case "majitel":
                                            try {
                                                assert member != null;
                                                player.sendMessage(ChatColor.GREEN + "Přidal jsi hráče jako majitele do pozemku");
                                                WgManager.addOwner(player, member, args[1]);
                                            } catch (StorageException e) {
                                                throw new RuntimeException(e);
                                            }
                                            break;
                                        case "člen":
                                            try {
                                                assert member != null;
                                                player.sendMessage(ChatColor.GREEN + "Přidal jsi hráče jako majitele do pozemku");
                                                WgManager.addMember(player, member, args[1]);
                                            } catch (StorageException e) {
                                                throw new RuntimeException(e);
                                            }
                                    }
                                }
                                break;

                            default:
                                break;
                        }
                    }else{
                        player.sendMessage(ChatColor.RED + "Nejsi majitelem pozemku.");
                    }

                }else {
                    assert player != null;
                    player.sendMessage(ChatColor.RED + "Pozemek neexistuje.");
                }
            }
            if (args[0].equals("remove")){
                if (args[1] != null){
                    if (WgManager.doesExist(args[1])){
                        assert player != null;
                        if (WgManager.isOwner(player, args[1])){
                            try {
                                WgManager.deleteRegion(player , args[1]);
                                player.sendMessage("Pozemek odstraněn");
                            } catch (StorageException e) {
                                player.sendMessage("Něco se nepovedlo při mazázání pozemku");
                                throw new RuntimeException(e);
                            }
                        }else{
                            player.sendMessage(ChatColor.RED + "Nejsi majitelem pozemku.");
                        }

                    }else {
                        assert player != null;
                        player.sendMessage(ChatColor.RED + "Pozemek neexistuje.");
                    }

                }else{
                    assert player != null;
                    player.sendMessage(ChatColor.RED + "Musíš zadat jméno pozemku");
                }
            }
            if (args[0].equals("create")){
                if (args[1] != null){
                    assert player != null;
                    if (PravitkoUtils.isPravitko(player.getItemInHand())){
                        HashMap<String, Object> data = PravitkoUtils.getData(player.getItemInHand());
                        if (data.get("FirstX") != null && data.get("SecondX") != null){
                            int blockCount = countBlocks((Integer) data.get("FirstX"), (Integer) data.get("FirstY"), (Integer) data.get("FirstZ"), (Integer) data.get("SecondX"), (Integer) data.get("SecondY"), (Integer) data.get("SecondZ"));
                            if (WgManager.isBlockInRegion((Integer) data.get("FirstX"), (Integer) data.get("FirstY"), (Integer) data.get("FirstZ"), (String) data.get("FirstWorld")) || WgManager.isBlockInRegion((Integer) data.get("SecondX"), (Integer) data.get("SecondY"), (Integer) data.get("SecondZ"), (String) data.get("SecondWorld"))){
                                sender.sendMessage(ChatColor.RED + "Na tomto místě již je pozemek");
                            }else{
                                if (!WgManager.doesExist(args[1])){
                                    double cost = GlobalVars.config.getDouble("regions.cost_per_block") * blockCount;
                                    if (args.length == 3){
                                        if (cost < GlobalVars.economy.getBalance(player)){
                                            try {
                                                WgManager.createRegion(player ,args[1], (Integer) data.get("FirstX"), (Integer) data.get("FirstY"), (Integer) data.get("FirstZ"), (Integer) data.get("SecondX"), (Integer) data.get("SecondY"), (Integer) data.get("SecondZ"));
                                                WgManager.addOwner(player, player, args[1]);
                                                GlobalVars.economy.withdrawPlayer(player, cost);
                                                GlobalVars.config.set("economy.current_central_money", GlobalVars.config.getDouble("economy.current_central_money" + cost));
                                                player.sendMessage(ChatColor.GREEN + "");
                                            } catch (StorageException e) {
                                                player.sendMessage("Nastal problém při vytváření pozemku");
                                                throw new RuntimeException(e);
                                            }
                                        }else{
                                            sender.sendMessage(ChatColor.RED + "Nemáš dost peněz.");
                                        }
                                    }else{
                                        TextComponent line1 = new TextComponent(ChatColor.YELLOW + "■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
                                        TextComponent line2 = new TextComponent(ChatColor.AQUA + "Chystáš se vytvořit nový pozemek");
                                        TextComponent line3 = new TextComponent(ChatColor.AQUA + "Bude velký " + ChatColor.DARK_AQUA + blockCount + " blocků");
                                        TextComponent line4 = new TextComponent(ChatColor.AQUA + "Bude tě to stát " + ChatColor.DARK_AQUA  + cost + "$");
                                        TextComponent line5 = new TextComponent(ChatColor.YELLOW + "■■■■■■■■■■■■■■■■■■■■");
                                        TextComponent continueText = new TextComponent(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "[pokračovat]");
                                        TextComponent line6 = new TextComponent(ChatColor.YELLOW + "■■■■");

                                        // Create the hover event for the "pokračovat" text
                                        String hoverText = ChatColor.GREEN + "vytvořit pozemek";
                                        continueText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverText).create()));

                                        // Create the click event for the "pokračovat" text
                                        String cmd = "/pozemek create " + args[1] + " confirm";
                                        continueText.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));

                                        // Combine the TextComponents into one multi-line TextComponent
                                        TextComponent message = new TextComponent("\n"); // Newline to separate lines
                                        message.addExtra(line1);
                                        message.addExtra("\n"); // Newline to separate lines
                                        message.addExtra(line2);
                                        message.addExtra("\n"); // Newline to separate lines
                                        message.addExtra(line3);
                                        message.addExtra("\n"); // Newline to separate lines
                                        message.addExtra(line4);
                                        message.addExtra("\n");
                                        message.addExtra(line5);
                                        message.addExtra(continueText);
                                        message.addExtra(line6);

                                        // Send the multi-line message to the sender
                                        player.spigot().sendMessage(message);
                                    }
                                }else{
                                    sender.sendMessage(ChatColor.RED + "Pozemek s tímto názvem již existuje");
                                }
                            }
                        }else{
                            sender.sendMessage("count: " + data.size());
                            sender.sendMessage(ChatColor.RED + "Musíš mít vybrané dva body pravítkem");
                        }
                    }else{
                        sender.sendMessage(ChatColor.RED + "V ruce musíš mít pravítko");
                    }
                }else{
                    sender.sendMessage(ChatColor.RED + "Pozemek musí mít název.");
                }
            }
        }else{
            sender.sendMessage(ChatColor.RED + "Toto může jen hráč");
        }

        return true;
    }
    public int countBlocks(int x1, int y1, int z1, int x2, int y2, int z2){
        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);
        int minZ = Math.min(z1, z2);
        int maxZ = Math.max(z1, z2);
        int width = maxX - minX + 1;
        int height = maxY - minY + 1;
        int length = maxZ - minZ + 1;

        return width * height * length;
    }


}
