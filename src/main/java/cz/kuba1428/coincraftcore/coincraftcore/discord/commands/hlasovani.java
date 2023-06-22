package cz.kuba1428.coincraftcore.coincraftcore.discord.commands;

import cz.kuba1428.coincraftcore.coincraftcore.Coincraftcore;
import cz.kuba1428.coincraftcore.coincraftcore.other.hlasovaniStorage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class hlasovani extends ListenerAdapter implements hlasovaniStorage {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getName().equals("hlasovani")){
            EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("Hlasování! \uD83D\uDCE2");
                eb.addField("O čem hlasujeme?", Objects.requireNonNull(event.getOption("popis")).getAsString(), false);
                eb.addField("Hlasování končí:", "<t:" + Instant.now().plusSeconds(Objects.requireNonNull(event.getOption("trvani")).getAsLong() * 60).getEpochSecond() + ":R>", false);
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
                            args.add(0);
                            hlasovaniData.put(message.getId(), args);
                            hlasujici.put(message.getId(), new ArrayList<>());
                            plugin.getLogger().info(String.valueOf(hlasovaniData.size()));
                            executor.schedule(() -> {
                                EmbedBuilder eb2 = new EmbedBuilder();
                                eb2.setTitle("Hlasování skončilo");
                                ArrayList<Integer> hlasy = hlasovaniData.get(message.getId());
                                String ansi = "```ansi\n" +
                                        "\n" +
                                        "\u001B[0;2m\u001B[1;2m\u001B[1;32m\u001B[0;32m\u001B[0;37m\u001B[0;32m\u001B[0;47m\u001B[0;31m\u001B[1;31m\u001B[0m\u001B[0;31m\u001B[0;47m\u001B[0m\u001B[0;32m\u001B[0;47m\u001B[0m\u001B[0;32m\u001B[0m\u001B[0;37m\u001B[0m\u001B[0;32m\u001B[0m\u001B[1;32m\u001B[0m\u001B[0m\u001B[0m\u001B[2;32m\u001B[1;32mPro: \u001B[1;37m" + hlasy.get(0) + "\n" +

                                        "\u001B[1;31mProti: \u001B[1;37m" +hlasy.get(1)+"\u001B[0m\u001B[1;31m\u001B[0m\u001B[1;37m\u001B[0m\u001B[1;32m\u001B[0m\u001B[2;32m\u001B[0m\n" +
                                        "\n" +
                                        "```";
                                eb2.setDescription("\n" + Objects.requireNonNull(event.getOption("popis")).getAsString() +  "\n\n**Výsledek Hlasování:**" + ansi);

                                eb2.setColor(new Color(15, 118, 187));
                                message.editMessageComponents().queue();

                                message.editMessageEmbeds(eb2.build()).queue();

                            }, Objects.requireNonNull(event.getOption("trvani")).getAsInt() , TimeUnit.MINUTES);
                        });


        }


    }
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (Objects.equals(event.getButton().getId(), "voteyes")){

            try {
                if(isMember(event.getUser().getId())){
                    if (!hlasujici.get(event.getMessageId()).contains(event.getUser().getId())){
                        ArrayList<Integer> votearray = hlasovaniStorage.hlasovaniData.get(event.getMessageId());
                        votearray.set(0, votearray.get(0) + 1);
                        ArrayList<String> hlasuji = hlasujici.get(event.getMessageId());
                        hlasuji.add(event.getUser().getId());
                        event.reply("Tvůj hlas byl zaznamenán").setEphemeral(true).queue();
                    }else{
                        event.reply("Ani to nezkoušej >:O každý jen jednoho hlasu je hoden!").setEphemeral(true).queue();
                    }

                }else {
                    event.reply("Toto hlasování se týká pouze hráčů GeekCraftu. Sorka :c").setEphemeral(true).queue();
                }
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        } else if (event.getComponentId().equals("voteno")) {
            try {
                if(isMember(event.getUser().getId())){
                    if (!hlasujici.get(event.getMessageId()).contains(event.getUser().getId())){
                        ArrayList<Integer> votearray = hlasovaniStorage.hlasovaniData.get(event.getMessageId());
                        votearray.set(1, votearray.get(1) + 1);
                        ArrayList<String> hlasuji = hlasujici.get(event.getMessageId());
                        hlasuji.add(event.getUser().getId());
                        event.reply("Tvůj hlas byl zaznamenán").setEphemeral(true).queue();
                    }else{
                        event.reply("Ani to nezkoušej >:O každý jen jednoho hlasu je hoden!").setEphemeral(true).queue();
                    }

                }else {
                    event.reply("Toto hlasování se týká pouze hráčů GeekCraftu. Sorka :c").setEphemeral(true).queue();
                }
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Boolean isMember(String id) throws SQLException, ClassNotFoundException {

        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(url, user, password);
        Statement stmnt = connection.createStatement();
        ResultSet rs = stmnt.executeQuery("SELECT discord FROM " + config.getString("database.prefix") + "users WHERE discord=" + id);
        Boolean bol = rs.next();
        connection.close();
        return bol;
    }
    static Coincraftcore plugin = Coincraftcore.getPlugin(Coincraftcore.class);
    FileConfiguration config = plugin.getConfig();
    String user = config.getString("database.user");
    String password = config.getString("database.password");
    String url = "jdbc:mysql://" + config.getString("database.host") + ":" + config.getString("database.port") + "/" + config.getString("database.database");


}
