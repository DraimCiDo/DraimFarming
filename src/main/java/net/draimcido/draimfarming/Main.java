package net.draimcido.draimfarming;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import net.draimcido.draimfarming.managers.CropManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.draimcido.draimfarming.commands.PluginCommand;
import net.draimcido.draimfarming.config.ConfigUtil;
import net.draimcido.draimfarming.config.MainConfig;
import net.draimcido.draimfarming.helper.LibraryLoader;
import net.draimcido.draimfarming.integrations.papi.PlaceholderManager;
import net.draimcido.draimfarming.utils.AdventureUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Main extends JavaPlugin {

    public static BukkitAudiences adventure;
    public static Main plugin;
    public static ProtocolManager protocolManager;

    private PlaceholderManager placeholderManager;
    private CropManager cropManager;
    private PluginCommand pluginCommand;

    @Override
    public void onLoad(){
        plugin = this;
        LibraryLoader.load("dev.dejvokep","boosted-yaml","1.3","https://repo.maven.apache.org/maven2/");
        LibraryLoader.load("commons-io","commons-io","2.11.0","https://repo.maven.apache.org/maven2/");
    }

    @Override
    public void onEnable() {

        adventure = BukkitAudiences.create(plugin);
        protocolManager = ProtocolLibrary.getProtocolManager();
        AdventureUtil.consoleMessage("[DraimFarming] Запущен на <white>" + Bukkit.getVersion());

        if (Bukkit.getPluginManager().getPlugin("ItemsAdder") != null) {
            MainConfig.customPlugin = "itemsadder";
            MainConfig.OraxenHook = false;
            AdventureUtil.consoleMessage("[DraimFarming] Кастомизация предметов используется из <#BA55D3><u>ItemsAdder");
        }
        else if (Bukkit.getPluginManager().getPlugin("Oraxen") != null) {
            MainConfig.customPlugin = "oraxen";
            MainConfig.OraxenHook = true;
            AdventureUtil.consoleMessage("[DraimFarming] Кастомизация предметов используется из <#6495ED><u>Oraxen");
        }
        else {
            AdventureUtil.consoleMessage("<red>[DraimFarming] Вам нужно установить ItemsAdder либо Oraxen для работы плагина");
            Bukkit.getPluginManager().disablePlugin(Main.plugin);
            return;
        }

        ConfigUtil.reloadConfigs();

        if (MainConfig.cropMode) AdventureUtil.consoleMessage("[DraimFarming] Режим урожая: Tripwire");
        else AdventureUtil.consoleMessage("[DraimFarming] Режим урожая: ItemFrame");

        this.pluginCommand = new PluginCommand();
        Objects.requireNonNull(Bukkit.getPluginCommand("draimfarming")).setExecutor(pluginCommand);
        Objects.requireNonNull(Bukkit.getPluginCommand("draimfarming")).setTabCompleter(pluginCommand);

        this.cropManager = new CropManager();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            this.placeholderManager = new PlaceholderManager();
        }

        AdventureUtil.consoleMessage("[DraimFarming] Плагин успешно запущен!");
    }

    @Override
    public void onDisable() {
        if (adventure != null) {
            adventure.close();
        }
        if (this.placeholderManager != null) {
            this.placeholderManager.unload();
        }
        if (this.cropManager != null) {
            this.cropManager.unload();
        }
    }

    public PlaceholderManager getPlaceholderManager() {
        return placeholderManager;
    }

    public boolean hasPapi() {
        return placeholderManager != null;
    }

    public CropManager getCropManager() {
        return cropManager;
    }
}