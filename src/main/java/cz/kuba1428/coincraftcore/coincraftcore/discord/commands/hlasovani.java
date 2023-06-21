package cz.kuba1428.coincraftcore.coincraftcore.discord.commands;

import cz.kuba1428.coincraftcore.coincraftcore.Coincraftcore;
import cz.kuba1428.coincraftcore.coincraftcore.other.hlasovaniStorage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import net.dv8tion.jda.api.entities.MessageEmbed.ImageInfo;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class hlasovani extends ListenerAdapter implements hlasovaniStorage {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getName().equals("hlasovani")){
            EmbedBuilder eb = new EmbedBuilder();
            // Convert the LocalDateTime to a Unix timestamp
            LocalDateTime pragueDateTime = LocalDateTime.now(ZoneId.of("Europe/Prague"));
            // Add five minutes to the current time
            LocalDateTime modifiedDateTime = pragueDateTime.plusSeconds(10);
            // Convert the modified LocalDateTime to a Unix timestamp
            long unixTimestamp = modifiedDateTime.toEpochSecond(ZoneOffset.UTC);
                eb.setTitle("Hlasování! \uD83D\uDCE2");
                eb.addField("O čem hlasujeme?", Objects.requireNonNull(event.getOption("popis")).getAsString(), false);
                eb.addField("Hlasování končí:", "<t:" + Instant.now().plusSeconds(10).getEpochSecond() + ":R>", false);
                eb.setColor(new Color(15, 118, 187));
            event.reply("Startuji hlasování").setEphemeral(true).queue();
            event.getChannel().sendMessageEmbeds(eb.build())
                        .addActionRow(
                                Button.success("voteyes",  "Hlasovat pro")
                                        .withEmoji(Emoji.fromFormatted("<:GeekDoporucuje:797579487947915284>")),
                                Button.danger("voteno", "Hlasovat proti")
                                        .withEmoji(Emoji.fromFormatted("<:GeekDislike:797579488253968426>"))

                        )
                        .queue((message) -> {
                            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
                            eb.setFooter("id: " + message.getId());
                            message.editMessageEmbeds(eb.build()).queue();
                            ArrayList<Integer> args = new ArrayList<>();
                            args.add(0);
                            args.add(5);
                            hlasovaniData.put(message.getId(), args);
                            plugin.getLogger().info(String.valueOf(hlasovaniData.size()));
                            executor.schedule(() -> {
                                EmbedBuilder eb2 = new EmbedBuilder();
                                eb2.setTitle("Hlasování skončilo");
                                eb2.setDescription(Objects.requireNonNull(event.getOption("popis")).getAsString() +  "\n**Výsledek Hlasování:**");

                                eb2.setColor(new Color(15, 118, 187));
                                message.editMessageComponents().queue();

                                message.editMessageEmbeds(eb2.build()).queue();

                            }, 10 , TimeUnit.SECONDS);
                        });


        }


    }
    static Coincraftcore plugin = Coincraftcore.getPlugin(Coincraftcore.class);

}
