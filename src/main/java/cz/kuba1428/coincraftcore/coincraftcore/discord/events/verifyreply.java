package cz.kuba1428.coincraftcore.coincraftcore.discord.events;

import cz.kuba1428.coincraftcore.coincraftcore.Coincraftcore;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;


import java.awt.*;
import java.util.*;
import java.sql.*;
import java.util.List;

public class verifyreply extends ListenerAdapter {
    FileConfiguration config = plugin.getConfig();
    @Override
    public void onModalInteraction(@NotNull  ModalInteractionEvent event) {
        if (event.getModalId().equals("verify")) {
            String code = Objects.requireNonNull(event.getValue("code")).getAsString();
            boolean exist = false;
            boolean asigned = false;
            String discordid = Objects.requireNonNull(event.getMember()).getId();
            String nick = null;
            try {
                Connection connection = DriverManager.getConnection(url, user, password);
                Class.forName("com.mysql.cj.jdbc.Driver");
                Statement stmnt = connection.createStatement();
                ResultSet rs = stmnt.executeQuery("SELECT * FROM " + config.getString("database.prefix") + "users WHERE verify_code ='" + code + "'");
                while (rs.next()){

                    exist = true;
                    nick = rs.getString("nick");
                    ResultSet rs2 = stmnt.executeQuery("SELECT * FROM " + config.getString("database.prefix") + "users WHERE discord=" + discordid);
                    while (rs2.next()){
                        asigned = true;
                    }
                }

            }catch (SQLException ignored){} catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            EmbedBuilder eb = new EmbedBuilder();
            if (exist){
                if (!asigned){

                    try {
                        List<Role> rolelist = event.getMember().getRoles();
                        Guild guild = event.getGuild();
                        String rank = "default";
                        assert guild != null;
                        if (rolelist.contains(guild.getRoleById(Objects.requireNonNull(config.getString("discord.roles.elita"))))){
                            rank = "elita";
                        } else if (rolelist.contains(guild.getRoleById(Objects.requireNonNull(config.getString("discord.roles.moderator"))))) {
                            rank = "moderator";
                        } else if (rolelist.contains(guild.getRoleById(Objects.requireNonNull(config.getString("discord.roles.technik"))))) {
                            rank = "techik";
                        } else if (rolelist.contains(guild.getRoleById(Objects.requireNonNull(config.getString("discord.roles.ultimate"))))) {
                            rank = "ultimate";
                        } else if (rolelist.contains(guild.getRoleById(Objects.requireNonNull(config.getString("discord.roles.premium"))))) {
                            rank = "premium";
                        } else if (rolelist.contains(guild.getRoleById(Objects.requireNonNull(config.getString("discord.roles.lite"))))) {
                            rank = "lite";
                        } else if (rolelist.contains(guild.getRoleById(Objects.requireNonNull(config.getString("discord.roles.booster"))))) {
                            rank = "booster";
                        }
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection connection = DriverManager.getConnection(url, user, password);
                        Statement stmnt = connection.createStatement();
                        stmnt.executeUpdate("UPDATE " + config.getString("database.prefix") + "users SET discord='" + discordid + "', verify_code=null , allow_access=1, rank='" + rank +"'  WHERE verify_code='" + code + "'");
                        eb.setColor(new Color(15, 118, 187));
                        eb.setTitle("Úspěšně propojeno");
                        eb.setDescription("Užij si hraní na našem serveru :)");
                        assert nick != null;
                        eb.addField("🏷️ Nick", nick, false);
                        eb.addField("🌐 Adresa serveru", "mc.geekboy.cz", false);
                        eb.addField("♾️ Verze", "Forge 1.16.5", false);
                        eb.addField("📦 Modpack", "https://mc.geekboy.cz/modpack", false);

                        event.replyEmbeds(eb.build()).setEphemeral(true).queue();
                    } catch (SQLException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }


                }else{
                    eb.setColor(Color.RED);
                    eb.setTitle("❌ Tvůj discord je již přidružen k mc účtu");
                    eb.setDescription("Použij /unlink aby jsi odpojil tvůj discord od mc účtu");
                    event.replyEmbeds(eb.build()).setEphemeral(true).queue();
                }
            }else{
                eb.setColor(Color.RED);
                eb.setTitle("❌ Verifikační kód neexistuje");
                event.replyEmbeds(eb.build()).setEphemeral(true).queue();

            }
            event.reply("Thanks for your request!").setEphemeral(true).queue();
        }
    }
    static Coincraftcore plugin = Coincraftcore.getPlugin(Coincraftcore.class);
    String user = config.getString("database.user");
    String password = config.getString("database.password");
    String url = "jdbc:mysql://" + config.getString("database.host") + ":" + config.getString("database.port") + "/" + config.getString("database.database");

}
