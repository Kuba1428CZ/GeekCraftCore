package cz.kuba1428.coincraftcore.coincraftcore.completers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class dmsgcompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completion = new ArrayList<>();
        completion.add("<zpráva>");
        return completion;

    }
}
