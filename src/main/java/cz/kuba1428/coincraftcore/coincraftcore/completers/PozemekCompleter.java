package cz.kuba1428.coincraftcore.coincraftcore.completers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PozemekCompleter implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        switch (args.length){
            case 1:
                completions.add("create");
                completions.add("delete");
                completions.add("list");
                completions.add("member");
                break;
            case 2:
                switch (args[0]){
                    case "create":
                    case "delete":
                    case "member":
                        completions.add("<jmeno pozemku>");
                        break;
                }
                break;
            case 3:
                if (args[0].equals("member")){
                    completions.add("add");
                    completions.add("remove");

                }
                break;
            case 4:
                switch (args[2]){
                    case "add":
                    case "delete":
                        completions.add("<hrac>");
                }
                break;
            case 5:
                if (args[2].equals("add")){
                    completions.add("majitel");
                    completions.add("člen");
                }
                break;
        }


        return completions;
    }
}
