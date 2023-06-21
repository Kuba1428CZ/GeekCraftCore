package cz.kuba1428.coincraftcore.coincraftcore.discord.commands;

import cz.kuba1428.coincraftcore.coincraftcore.Coincraftcore;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.jetbrains.annotations.NotNull;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.json.simple.JSONObject;

import java.awt.*;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Objects;

public class shops extends ListenerAdapter {




    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event)  {
        if (event.getName().equals("shoplist")){
            Coincraftcore plugin = Coincraftcore.getPlugin(Coincraftcore.class);
            FileConfiguration config = plugin.getConfig();
            // Define API endpoint URL with required parameters
            OptionMapping typ = event.getOption("typ");
            String args = "&sc1=shop_type&sv1=" + typ.getAsString();
            if (event.getOption("item") != null){
                args += "&sc2=material&sv2=" + Objects.requireNonNull(event.getOption("item")).getAsString().toUpperCase().replace(' ', '_');
            }
            if (event.getOption("majitel") != null){
                args += "&sc3=owner&sv3=" + event.getOption("majitel").getAsString().toUpperCase().replace(' ', '_');

            }
            String endpointUrl = "https://mc.geekboy.cz/api/getshops.php?columns=id,material,shop_location_encoded,owner,price,count,items_in_storage&sort=material&limit=10" + args;
            try {
                // Create URL object for the page you want to get content from
                URL url = new URL(endpointUrl);
                // Create HttpURLConnection object to send HTTP request
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // Set request method and other properties
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                // Get input stream from connection to read response
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                // Read response line by line and append to StringBuilder object
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                EmbedBuilder eb = new EmbedBuilder();
                if (!response.toString().equals("No data found")){
                    eb.setTitle("Obchody odpovídající hledání:");
                    eb.setColor(new Color(15, 118, 187));
                    Gson gson = new Gson();
                    JsonArray jsonArray = gson.fromJson(response.toString(), JsonArray.class);
                    for (JsonElement element : jsonArray) {
                        JsonObject jsonObject = element.getAsJsonObject();
                        String material = jsonObject.get("material").getAsString();
                        int price = jsonObject.get("price").getAsInt();
                        int count = jsonObject.get("count").getAsInt();
                        byte[] itemSerialized = Base64.getDecoder().decode(jsonObject.get("shop_location_encoded").getAsString());
                        ByteArrayInputStream inn = new ByteArrayInputStream(itemSerialized);
                        BukkitObjectInputStream is = new BukkitObjectInputStream(inn);
                        Location location = (Location) is.readObject();
                        String owner = jsonObject.get("owner").getAsString();
                        int items_in_storage = jsonObject.get("items_in_storage").getAsInt();
                        eb.addField(material.replace('_', ' '), "💵 Cena: `" + price + "$ za " + count + "ks`\n🔗 Majitel: `" + owner + "`\n📦 Kusů na skladě: `" + items_in_storage + "`\n[zobrazit na mapě](https://map.majnr.cz/?worldname="+ location.getWorld().getName() +"&mapname=surface&zoom=7&x="+ location.getX()+"&y=" + location.getY() +"&z="+ location.getZ()+")", true);

                    }
                    if (jsonArray.size() < 10){
                        event.replyEmbeds(eb.build())
                                .addActionRow(
                                        Button.primary("previous", "◄")
                                                .asDisabled(),
                                        Button.secondary("pagecount", "Strana 1")
                                                .asDisabled(),
                                        Button.primary("next", "►")
                                                .asDisabled()
                                )
                                .queue();
                    }else{
                        JSONObject nextbuttonjson = new JSONObject();
                        nextbuttonjson.put("p", "1");
                        nextbuttonjson.put("a", args);
                        event.replyEmbeds(eb.build())
                                .addActionRow(
                                        Button.primary("previous", "◄")
                                                .asDisabled(),
                                        Button.secondary("pagecount", "Strana 1")
                                                .asDisabled(),
                                        Button.primary( "nsl" + nextbuttonjson.toJSONString(), "►")

                                )
                                .queue();
                    }
                }else{
                    eb.setTitle("⚠️ žádné obchody");
                    eb.setDescription("nebyly nalezeny žádné obchody s těmito parametry");
                    eb.setColor(Color.YELLOW);
                    event.replyEmbeds(eb.build())
                            .addActionRow(
                                    Button.primary("previous", "◄")
                                            .asDisabled(),
                                    Button.secondary("pagecount", "Strana 1")
                                            .asDisabled(),
                                    Button.primary("next", "►")
                                            .asDisabled()
                            )
                            .queue();
                }



            } catch (Exception e) {
                e.printStackTrace();
            }



        }
    }
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getComponentId().contains("nsl") || event.getComponentId().contains("psl")){
            Gson gson = new Gson();
            String jsonstring;
            int page;
            JsonObject nextbuttonjsongrabbed;
            if (event.getComponentId().contains("psl")){
                jsonstring = event.getComponentId().replace("psl", "");
                nextbuttonjsongrabbed = gson.fromJson( jsonstring, JsonObject.class);
                page = nextbuttonjsongrabbed.get("p").getAsInt() - 1;

            }else {
                jsonstring = event.getComponentId().replace("nsl", "");
                nextbuttonjsongrabbed = gson.fromJson( jsonstring, JsonObject.class);
                page = nextbuttonjsongrabbed.get("p").getAsInt() + 1;
            }
            String args = nextbuttonjsongrabbed.get("a").getAsString();
            Coincraftcore plugin = Coincraftcore.getPlugin(Coincraftcore.class);
            FileConfiguration config = plugin.getConfig();
            // Define API endpoint URL with required parameters
            String endpointUrl = "https://mc.geekboy.cz/api/getshops.php?columns=id,material,shop_location_encoded,owner,price,count,items_in_storage&sort=material&limit=10" + args  + "&page=" + page;
            try {
                // Create URL object for the page you want to get content from
                URL url = new URL(endpointUrl);
                // Create HttpURLConnection object to send HTTP request
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // Set request method and other properties
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                // Get input stream from connection to read response
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                // Read response line by line and append to StringBuilder object
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                plugin.getLogger().info(response.toString());
                EmbedBuilder eb = new EmbedBuilder();
                JSONObject buttonjson = new JSONObject();
                buttonjson.put("p", page);
                buttonjson.put("a", args);
                if (!response.toString().equals("No data found")){
                    eb.setTitle("Obchody odpovídající hledání:");
                    eb.setColor(new Color(15, 118, 187));
                    JsonArray jsonArray = gson.fromJson(response.toString(), JsonArray.class);
                    for (JsonElement element : jsonArray) {
                        JsonObject jsonObject = element.getAsJsonObject();
                        String material = jsonObject.get("material").getAsString();
                        int price = jsonObject.get("price").getAsInt();
                        int count = jsonObject.get("count").getAsInt();
                        byte[] itemSerialized = Base64.getDecoder().decode(jsonObject.get("shop_location_encoded").getAsString());
                        ByteArrayInputStream inn = new ByteArrayInputStream(itemSerialized);
                        BukkitObjectInputStream is = new BukkitObjectInputStream(inn);
                        Location location = (Location) is.readObject();
                        String owner = jsonObject.get("owner").getAsString();
                        int items_in_storage = jsonObject.get("items_in_storage").getAsInt();
                        eb.addField(material.replace('_', ' '), "💵 Cena: `" + price + "$ za " + count + "ks`\n🔗 Majitel: `" + owner + "`\n📦 Kusů na skladě: `" + items_in_storage + "`\n[zobrazit na mapě](https://map.majnr.cz/?worldname="+ location.getWorld().getName() +"&mapname=surface&zoom=7&x="+ location.getX()+"&y=" + location.getY() +"&z="+ location.getZ()+")", true);
                    }
                    Button previousbutton = Button.primary("psl" + buttonjson.toJSONString(), "◄");
                    if (page == 1){
                        previousbutton = Button.primary("psl" + buttonjson.toJSONString(), "◄").asDisabled();
                    }
                    if (jsonArray.size() < 10){
                        event.editMessage("")
                                .setEmbeds(eb.build())
                                .setActionRow(
                                        previousbutton,
                                        Button.secondary("pagecount", "Strana " + page)
                                                .asDisabled(),
                                        Button.primary("next", "►")
                                                .asDisabled()
                                )
                                .queue();
                    }else{

                        event.editMessage("")
                                .setActionRow(
                                        previousbutton,
                                        Button.secondary("pagecount", "Strana " + page)
                                                .asDisabled(),
                                        Button.primary( "nsl" + buttonjson.toJSONString(), "►")

                                )
                                .setEmbeds(eb.build())
                                .queue();
                    }
                }else{
                    eb.setTitle("⚠️ žádné obchody");
                    eb.setDescription("nebyly nalezeny žádné obchody s těmito parametry");
                    eb.setColor(Color.YELLOW);
                    event.editMessage("")
                            .setEmbeds(eb.build())
                            .setActionRow(
                                    Button.primary("psl" + buttonjson.toJSONString(), "◄"),
                                    Button.secondary("pagecount", "Strana " + page)
                                            .asDisabled(),
                                    Button.primary("next", "►")
                                            .asDisabled()
                            )
                            .queue();
                }



            } catch (Exception e) {
                e.printStackTrace();
            }






        }
    }

}
