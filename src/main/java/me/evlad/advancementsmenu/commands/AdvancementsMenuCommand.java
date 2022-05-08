package me.evlad.advancementsmenu.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.common.collect.Iterators;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import me.evlad.advancementsmenu.AdvancementsMenu;
import me.evlad.advancementsmenu.utils.AdvancementTabs;
import me.evlad.advancementsmenu.utils.AdvancementType;
import dev.triumphteam.gui.builder.item.ItemBuilder;

import org.bukkit.advancement.AdvancementProgress;

import net.kyori.adventure.text.Component;

public class AdvancementsMenuCommand implements CommandExecutor {
	private final AdvancementsMenu plugin;
	private final Logger logger;

	public AdvancementsMenuCommand(AdvancementsMenu plugin) {
			this.plugin = plugin;
			this.logger = plugin.getLogger();
	}

	public void log(String msg) {
		logger.log(Level.INFO, msg);
  }

	List<AdvancementType> advancementList;

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
		AdvancementTabs advancementTabs = new AdvancementTabs();

		if (sender instanceof Player) {
			Player player = (Player) sender;

			Gui menu = Gui.gui()
			.title(Component.text("Succès"))
			.rows(3)
			.disableAllInteractions()
			.create();
			
			for (int i = 0; i < advancementTabs.tabs.size(); i++) {
				AdvancementType tab = advancementTabs.tabs.get(i);

				String title = tab.info.getTitle();

				GuiItem item = ItemBuilder
				.from(Material.STONE)
				.name(Component.text(title))
				.asGuiItem(event -> {
					openTabAdvancement(player, tab);
				});
				menu.setItem(i*2, item);
			}

			menu.getFiller().fill(ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).asGuiItem());
			menu.open(player);

			return true;
		} else {
			return false;
		}
	}

	private void openTabAdvancement(Player player, AdvancementType tab) {
		Gui menu = Gui.gui()
			.title(Component.text(tab.info.getTitle()))
			.rows(6)
			.disableAllInteractions()
			.create();

		for (int i = 0; i < tab.childrens.size(); i++) {
			AdvancementType children = tab.childrens.get(i);

			String title = children.info.getTitle();
			String description = children.info.getDescription(true, 20, "\n");
			String[] splitDesc = description.split("\n");
			List<Component> descriptionLore = new ArrayList<>();

			for (int j = 0; j < splitDesc.length; j++) {
				descriptionLore.add(Component.text(splitDesc[j]));
			}

			if (title == null || description == null)
				continue;

			log(children.info.getFrameType());

			AdvancementProgress progress = player.getAdvancementProgress(children.advancement);
			GuiItem item = ItemBuilder
				.from(progress.isDone() ? Material.LIME_CONCRETE : Material.RED_CONCRETE)
				.name(Component.text(title))
				.lore(descriptionLore)
				.asGuiItem();

			menu.addItem(item);
		}
		menu.getFiller().fill(ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).asGuiItem());

		menu.open(player);
	}
}
