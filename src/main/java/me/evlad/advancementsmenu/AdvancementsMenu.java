package me.evlad.advancementsmenu;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

import me.evlad.advancementsmenu.commands.AdvancementsMenuCommand;

public final class AdvancementsMenu extends JavaPlugin {
	@Override
	public void onEnable() {
		// Plugin startup logic
		getLogger().log(Level.INFO, "Hello tout tle monde");

		getCommand("success").setExecutor(new AdvancementsMenuCommand(this));
	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic
	}
}
