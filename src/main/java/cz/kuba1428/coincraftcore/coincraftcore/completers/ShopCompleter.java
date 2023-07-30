package cz.kuba1428.coincraftcore.coincraftcore.completers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ShopCompleter implements TabCompleter {

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> completion = new ArrayList<>();
        if (args.length > 3) {
            return completion;
        } else if (args.length > 2) {
            completion.add("<cena>");
            return completion;
        } else if (args.length > 1) {
            completion.add("<počet ks>");
            return completion;
        } else if (args.length > 0) {
            completion.add("prodej");
            completion.add("výkup");
            return completion;
        } else {
            return null;
        }
    }
}
