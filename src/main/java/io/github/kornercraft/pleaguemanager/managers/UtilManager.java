package io.github.kornercraft.pleaguemanager.managers;

import io.github.kornercraft.pleaguemanager.utils.Logger;
import io.github.kornercraft.pleaguemanager.utils.Helper;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Getter
public class UtilManager {
  private final Plugin plugin;
  private Logger logger;
  private Helper helper;
  @Setter private boolean fcEnabled = true;
  @Getter private boolean debug = true;

  public UtilManager(final Plugin plugin) {
    this.plugin = plugin;
    this.logger = new Logger(plugin);
    this.helper = new Helper(plugin);
  }

  public void reload() {
    this.logger = new Logger(plugin);
    this.helper = new Helper(plugin);
  }

  public String color(final String string) {
    return ChatColor.translateAlternateColorCodes('&', string);
  }

  public boolean isTaskQueued(final Integer taskId) {
    if (taskId != null) return getPlugin().getServer().getScheduler().isQueued(taskId);
    else return false;
  }

  public static String formatTime(int time) {
    return LocalTime.MIDNIGHT.plus(Duration.ofSeconds(time)).format(DateTimeFormatter.ofPattern("mm:ss"));
  }
}
