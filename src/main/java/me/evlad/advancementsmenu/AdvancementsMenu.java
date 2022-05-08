package me.evlad.advancementsmenu;

import org.bukkit.plugin.java.JavaPlugin;

import me.evlad.advancementsmenu.commands.AdvancementsMenuCommand;

public final class AdvancementsMenu extends JavaPlugin {
	@Override
	public void onEnable() {
		getCommand("advancements").setExecutor(new AdvancementsMenuCommand(this));
	}

	@Override
	public void onDisable() {
	}
}
