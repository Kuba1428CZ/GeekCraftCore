package cz.kuba1428.coincraftcore.coincraftcore.discord.commands;

import cz.kuba1428.coincraftcore.coincraftcore.Coincraftcore;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.Configuration;
import java.awt.*;
import java.sql.*;

public class player extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getName().equals("player")){
            OptionMapping nick = event.getOption("nick");
            EmbedBuilder eb = new EmbedBuilder();
            OfflinePlayer player = plugin.getServer().getOfflinePlayer(nick.getAsString());
            Boolean exists = false;
            Long discordid = null;
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection connection = DriverManager.getConnection(url, user, password);
                Statement stmnt = connection.createStatement();
                ResultSet rs = stmnt.executeQuery("SELECT * FROM " + config.getString("database.prefix") + "users WHERE nick='" + nick.getAsString() + "'");
                while (rs.next()){


                    exists = true;
                    if ( rs.getLong("discord") == 0   ) {
                        discordid = null;
                    }else{
                        discordid =  rs.getLong("discord");

                    }
                }

            }catch (SQLException ignored){} catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            if (!exists){
                eb.setTitle("Hráč nenalezen");
                eb.setDescription("❌ Hráč s tímto jménem nehraje na našem serveru");
                eb.setColor(Color.RED);

            }else{
                eb.setTitle("Informace o hráči " + nick.getAsString());
                eb.setThumbnail("https://api.mineatar.io/face/" + nick.getAsString() + ".png");
                eb.setColor(new Color(15, 118, 187));
                if (player.isOnline()){
                    eb.addField("🟢 Status", "`online`", false);
                }else {
                    eb.addField("🔴 Status", "`offline`", false);
                }
                Economy economy = Coincraftcore.getEconomy();
                eb.addField("💰 Peníze", "`" + String.format("%,.2f", economy.getBalance(player)) +"$`", false);

                if (discordid == null){
                    eb.addField("⚠️ Nedokončená registrace", "`Hráč nedokončil registraci a nemůže hrát na serveru`", false);
                }else{
                    eb.addField("🔗 Discord", "<@" + discordid + ">", false);

                }

            }

            event.replyEmbeds(eb.build()).queue();

        }
    }
    static Coincraftcore plugin = Coincraftcore.getPlugin(Coincraftcore.class);
    FileConfiguration config = plugin.getConfig();
    String user = config.getString("database.user");
    String password = config.getString("database.password");
    String url = "jdbc:mysql://" + config.getString("database.host") + ":" + config.getString("database.port") + "/" + config.getString("database.database");
}
