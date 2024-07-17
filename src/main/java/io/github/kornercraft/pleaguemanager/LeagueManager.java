package io.github.kornercraft.pleaguemanager;

import co.aikar.commands.BukkitCommandManager;
import io.github.kornercraft.pleaguemanager.commands.*;
import io.github.kornercraft.pleaguemanager.commands.timers.*;
import io.github.kornercraft.pleaguemanager.configs.*;
import io.github.kornercraft.pleaguemanager.managers.*;
import lombok.Getter;
import lombok.Setter;
import net.luckperms.api.LuckPerms;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

@Setter
@Getter
public class LeagueManager extends JavaPlugin {
  private ConfigManager messagesFile = new ConfigManager(this, "");
  private YamlConfiguration config;
  private LuckPerms luckPermsAPI = null;
  private UtilManager utilManager;
  private GUIManager guiManager;
  private ListenerManager listenerManager;
  @Getter
  private static LeagueManager instance;

  @Override
  public void onEnable() {
    instance = this;
    getServer().getScheduler().cancelTasks(getInstance());
    setupMessages();
    setupConfig();
    setupLuckPermsAPI();
    setupManagers();
    setupCommands();
    setupListeners();

    getUtilManager().getLogger().sendBanner();
    getUtilManager().getLogger().info("Successfully enabled!");
  }

  @Override
  public void onDisable() {
    if (listenerManager != null) {
      getListenerManager().unregisterListeners();
    }
    getServer().getScheduler().cancelTasks(getInstance());
  }

  private void setupConfig() {
    Config.setup(this);
    config = Config.getConfig("config.yml");
  }

  private void setupLuckPermsAPI() {
    RegisteredServiceProvider<LuckPerms> provider = getServer().getServicesManager().getRegistration(LuckPerms.class);
    if (provider != null) {
      luckPermsAPI = provider.getProvider();
    } else {
      getLogger().warning("LuckPerms not found! Disabling plugin due to missing dependency.");
      getServer().getPluginManager().disablePlugin(this);
    }
  }

  private void setupManagers() {
    utilManager = new UtilManager(this);
    guiManager = new GUIManager();
    listenerManager = new ListenerManager(this, utilManager, guiManager);
  }

  public void setupMessages() {
    getMessagesFile().createNewFile("messages.yml", "Loading messages.yml", "LeagueManager Messages");
    loadMessages();
  }

  private void loadMessages() {
    Lang.setFile(getMessagesFile().getConfig("messages.yml"));

    for (Lang value : Lang.values())
      getMessagesFile().getConfig("messages.yml").addDefault(value.getPath(), value.getDefault());

    getMessagesFile().getConfig("messages.yml").options().copyDefaults(true);
    getMessagesFile().saveConfig("messages.yml");
  }

  private void setupCommands() {
    BukkitCommandManager commandManager = new BukkitCommandManager(this);

    commandManager.enableUnstableAPI("help");
    commandManager.registerCommand(new LMCommand(getUtilManager(), this));
    commandManager.registerCommand(new RostersCommand(getUtilManager(), getGuiManager()));
    commandManager.registerCommand(new MigrateCommand(getUtilManager()));
    commandManager.registerCommand(new StatisticsCommand(getUtilManager()));
    commandManager.registerCommand(new ResultCommand(this, getUtilManager()));
    commandManager.registerCommand(new TimerCommand(this, getUtilManager()));
    commandManager.registerCommand(new OXECommand(this, getUtilManager()));
    commandManager.registerCommand(new TXFCommand(this, getUtilManager()));
  }

  private void setupListeners() {
    if (listenerManager.isRegistered()) {
      listenerManager.unregisterListeners();
    }
    listenerManager.registerListeners();
  }
}
