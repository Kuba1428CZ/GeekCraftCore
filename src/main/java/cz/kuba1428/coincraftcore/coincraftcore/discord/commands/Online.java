package cz.kuba1428.coincraftcore.coincraftcore.discord.commands;

import cz.kuba1428.coincraftcore.coincraftcore.CoincraftCore;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Collection;

public class Online extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getName().equals("online")) {
            Collection<? extends Player> players = plugin.getServer().getOnlinePlayers();
            EmbedBuilder eb = new EmbedBuilder();
            if (players.size() > 0) {
                StringBuilder playerlist = new StringBuilder("```");
                for (Player player : players) {
                    playerlist.append("• ").append(player.getName()).append("\n");
                }
                playerlist.append("```");
                eb.setTitle("Seznam online hráčů");
                eb.setDescription("Počet: " + players.size() + "\n" + playerlist);
                eb.setColor(new Color(15, 118, 187));
                event.replyEmbeds(eb.build()).queue();
            } else {
                eb.setTitle("❌ Na serveru v tuto chvíli nikdo není");
                eb.setDescription("Čekáme na tebe a tvé kámoše ;)");
                eb.setColor(Color.RED);
                event.replyEmbeds(eb.build()).queue();
            }

        }


    }

    static CoincraftCore plugin = CoincraftCore.getPlugin(CoincraftCore.class);

}
