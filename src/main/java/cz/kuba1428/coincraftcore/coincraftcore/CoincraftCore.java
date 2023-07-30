package cz.kuba1428.coincraftcore.coincraftcore;

import cz.kuba1428.coincraftcore.coincraftcore.commands.PublicResources;
import cz.kuba1428.coincraftcore.coincraftcore.commands.Shop;
import cz.kuba1428.coincraftcore.coincraftcore.commands.pozemek;
import cz.kuba1428.coincraftcore.coincraftcore.completers.PozemekCompleter;
import cz.kuba1428.coincraftcore.coincraftcore.completers.ShopCompleter;
import cz.kuba1428.coincraftcore.coincraftcore.discord.commands.Online;
import cz.kuba1428.coincraftcore.coincraftcore.discord.commands.Player;
import cz.kuba1428.coincraftcore.coincraftcore.discord.commands.Shops;
import cz.kuba1428.coincraftcore.coincraftcore.discord.commands.Verify;
import cz.kuba1428.coincraftcore.coincraftcore.discord.commands.Voting;
import cz.kuba1428.coincraftcore.coincraftcore.discord.events.VerifyReply;
import cz.kuba1428.coincraftcore.coincraftcore.events.EditShopParameter;
import cz.kuba1428.coincraftcore.coincraftcore.events.LogikaPravitka;
import cz.kuba1428.coincraftcore.coincraftcore.events.PlayerJoinSetup;
import cz.kuba1428.coincraftcore.coincraftcore.events.PlayerLeaves;
import cz.kuba1428.coincraftcore.coincraftcore.events.PravitkoCancelBreak;
import cz.kuba1428.coincraftcore.coincraftcore.events.ShopBreak;
import cz.kuba1428.coincraftcore.coincraftcore.events.ShopClickHandle;
import cz.kuba1428.coincraftcore.coincraftcore.events.ShopGuiMonitor;
import cz.kuba1428.coincraftcore.coincraftcore.events.ShopStorageUpdate;
import cz.kuba1428.coincraftcore.coincraftcore.managers.DbManager;
import cz.kuba1428.coincraftcore.coincraftcore.recipes.RulerCrafting;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Objects;
import java.util.logging.Logger;


public final class CoincraftCore extends JavaPlugin {
    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;

    @Override
    public void onEnable() {
        if (getConfig().getBoolean("discord.enabled")) {
            try {
                JDA bot = JDABuilder.createDefault(getConfig().getString("discord.token"))
                        .setStatus(OnlineStatus.ONLINE)
                        .setActivity(Activity.watching("tě ve spánku >:)"))
                        .addEventListeners(new Player())
                        .addEventListeners(new Verify())
                        .addEventListeners(new Online())
                        .addEventListeners(new VerifyReply())
                        .addEventListeners(new Shops())
                        .addEventListeners(new Voting())
                        .build().awaitReady();
                Guild botguild = bot.getGuildById(Objects.requireNonNull(getConfig().getString("discord.guild")));
                if (botguild != null) {
                    botguild.upsertCommand("player", "zobrazí informace o hráči")
                            .addOption(OptionType.STRING, "nick", "nick hrace", true)
                            .queue();
                    botguild.upsertCommand("hlasovani", "spustí hlasování pouze pro hráče serveru")
                            .addOption(OptionType.STRING, "popis", "popis toho o čem se hlasuje", true)
                            .addOption(OptionType.INTEGER, "trvani", "Doba trvání ankety (v minutách)", true)
                            .queue();
                    botguild.upsertCommand("verify", "propojí mc účet s discordem")
                            .queue();
                    botguild.upsertCommand("online", "zobrazí online hráče")
                            .queue();
                    botguild.upsertCommand("shoplist", "zobrazí shopy na serveru")
                            .addOptions(
                                    new OptionData(OptionType.STRING, "typ", "specifikovat typ obchodu", true)
                                            .addChoice("Prodej", "prodej")
                                            .addChoice("Výkup", "výkup")
                            )
                            .addOption(OptionType.STRING, "item", "spicifikovat item", false)
                            .addOption(OptionType.STRING, "majitel", "specifikovat majitele obchodu", false)
                            .queue();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        RulerCrafting.init();
        if (!setupEconomy()) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        saveDefaultConfig();
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new ShopStorageUpdate(), this);
        pm.registerEvents(new PravitkoCancelBreak(), this);
        pm.registerEvents(new ShopBreak(), this);
        pm.registerEvents(new PlayerLeaves(), this);
        pm.registerEvents(new ShopClickHandle(), this);
        pm.registerEvents(new ShopGuiMonitor(), this);
        pm.registerEvents(new LogikaPravitka(), this);
        pm.registerEvents(new PlayerJoinSetup(), this);
        pm.registerEvents(new EditShopParameter(), this);
        Objects.requireNonNull(getCommand("obchod")).setExecutor(new Shop());
        Objects.requireNonNull(getCommand("verejne-prostredky")).setExecutor(new PublicResources());
        Objects.requireNonNull(getCommand("obchod")).setTabCompleter(new ShopCompleter());
        Objects.requireNonNull(getCommand("pozemek")).setExecutor(new pozemek());
        Objects.requireNonNull(getCommand("pozemek")).setTabCompleter(new PozemekCompleter());
        FileConfiguration config = this.getConfig();
            try {

            DbManager.ExecuteUpdate("create table IF NOT EXISTS `" + config.getString("database.prefix") + "users` (\n" +
                    "  `id` int unsigned not null auto_increment primary key,\n" +
                    "  `nick` VARCHAR(255) not null UNIQUE,\n" +
                    "  `money` INT not null default 0,\n" +
                    "  `allow_access` INT not null default 0,\n" +
                    "  `discord` BIGINT default null UNIQUE,\n" +
                    "  `verify_code` INT default null UNIQUE,\n" +
                    "  `rank` VARCHAR(255) default null\n" +
                    ");");
            DbManager.ExecuteUpdate("create table if not exists `" + config.getString("database.prefix") + "shops` (\n" +
                    "  `id` int unsigned not null auto_increment primary key,\n" +
                    "  `owner` VARCHAR(255) not null,\n" +
                    "  `server` varchar(255) not null,\n" +
                    "  `shop_location` TEXT not null,\n" +
                    "  `shop_location_encoded` TEXT not null,\n" +
                    "  `storage_location` TEXT not null,\n" +
                    "  `storage_location_encoded` TEXT not null,\n" +
                    "  `itemstack` TEXT not null,\n" +
                    "  `material` varchar(255) null,\n" +
                    "  `shop_type` varchar(255) not null,\n" +
                    "  `price` INT not null,\n" +
                    "  `count` INTEGER not null,\n" +
                    "  `locked` INTEGER not null default 0,\n" +
                    "  `items_in_storage` INTEGER not null default 0,\n" +
                    "  `nickname` VARCHAR(255)\n" +

                    ")");
                DbManager.ExecuteUpdate("create table if not exists `" + config.getString("database.prefix") + "warez_allowed` (\n" +
                    "  `id` int unsigned not null auto_increment primary key,\n" +
                    "  `nick` VARCHAR(255) not null\n" +
                    ")");
        } catch (Exception e) {
            getLogger().warning("Nastaly problémy při připojování k databázi: " + e);
        }
        getLogger().info("CoinCraftCore sucessfully enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    public static Economy getEconomy() {
        return econ;
    }


}
