package me.evlad.advancementsmenu.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.LocaleUtils;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import me.evlad.advancementsmenu.AdvancementsMenu;
import me.evlad.advancementsmenu.utils.AdvancementTabs;
import me.evlad.advancementsmenu.utils.AdvancementType;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.ScrollingGui;

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
		if (split.length > 0) {
			String subcommand = split[0];
			switch(subcommand) {
				case "reload": {
					if (sender.hasPermission("advancementsmenu.*")) {
						return reload();
					}
				}
				case "open": {
					if (split.length >= 2 && sender.hasPermission("advancementsmenu.open.player")) {
						return openMenu(plugin.getServer().getPlayer(split[1]));
					}
	
				}
			}
		}

		return openMenu((Player)sender);
	}

	private boolean reload() {
		plugin.reloadConfig();
		return true;
	}

	private boolean openMenu(Player player) {
		FileConfiguration config = plugin.getConfig();

		if (player == null) {
			return true;
		}

		AdvancementTabs advancementTabs = new AdvancementTabs();
		boolean spaceBetween = true;

		Gui menu = Gui.gui()
		.title(Component.text(config.getString("gui.title")))
		.rows(3)
		.disableAllInteractions()
		.create();

		List<AdvancementType> tabs = advancementTabs.getTabs();
		
		for (int i = 0; i < tabs.size(); i++) {
			AdvancementType tab = tabs.get(i);

			String title = tab.info.getTitle();
			String[] descriptionArray = tab.info.getDescriptionArray(20);

			if (title == null || descriptionArray.length < 0)
				continue;
			
			ItemStack displayedItem = tab.info.getItem() == null
				? new ItemStack(Material.STONE)
				: tab.info.getItem();
			List<Component> descriptionLore = new ArrayList<>();

			for (int j = 0; j < descriptionArray.length; j++) {
				descriptionLore.add(
					Component.text(config.getString("gui.advancement-description").replaceAll("\\[description\\]", descriptionArray[j]))
				);
			}

			GuiItem item = ItemBuilder
			.from(displayedItem)
			.flags(ItemFlag.HIDE_ATTRIBUTES)
			.name(Component.text(config.getString("gui.advancement-title").replaceAll("\\[title\\]", title)))
			.lore(descriptionLore)
			.asGuiItem(event -> {
				openTabAdvancement(player, tab);
			});
			menu.setItem(i*(spaceBetween ? 2 : 1), item);
		}

		menu.getFiller().fill(ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).asGuiItem());
		menu.open(player);
		return true;
	}

	private void openTabAdvancement(Player player, AdvancementType tab) {
		FileConfiguration config = plugin.getConfig();

		ScrollingGui menu = Gui.scrolling()
			.title(Component.text(tab.info.getTitle()))
			.rows(6)
			.disableAllInteractions()
			.pageSize(45)
			.create();

		for (int i = 0; i < tab.childrens.size(); i++) {
			AdvancementType children = tab.childrens.get(i);

			String title = children.info.getTitle();
			String[] descriptionArray = children.info.getDescriptionArray(20);

			if (title == null || descriptionArray.length < 0)
				continue;

			List<Component> descriptionLore = new ArrayList<>();

			for (int j = 0; j < descriptionArray.length; j++) {
				descriptionLore.add(
					Component.text(config.getString("gui.advancement-description").replaceAll("\\[description\\]", descriptionArray[j]))
				);
			}

			AdvancementProgress progress = children.getPlayerProgress(player);
			boolean isDone = progress.isDone();
			ItemStack progressItem = children.info.getItem();
			int requirementFinished = 0;
			String[][] requirements = children.info.getRequirements();
			for (String [] requirement : requirements) {
				boolean requirementIsDone = false;

				for (String requirementName : requirement) {
					if (progress.getDateAwarded(requirementName) != null)
						requirementIsDone = true;
				}

				if (requirementIsDone)
					requirementFinished++;
			}

			Collection<String> awardedCriteria = progress.getAwardedCriteria();
			Date latestDateAwarded = new Date(0);
	
			for (String criterion : awardedCriteria) {
				@Nullable Date dateAwarded = progress.getDateAwarded(criterion);
				if (dateAwarded == null)
					continue;

				if (dateAwarded.after(latestDateAwarded)) {
					latestDateAwarded = dateAwarded;
				}
			}

			if (isDone) {
				String formattedDate = new SimpleDateFormat(
					config.getString("misc.date-formatting"),
					LocaleUtils.toLocale(config.getString("misc.locale")))
				.format(latestDateAwarded);
				// String formattedDate = new DateFormat(config.getString("misc.date-formatting")).format(latestDateAwarded);

				descriptionLore.add(Component.text(""));
				descriptionLore.add(Component.text(config.getString("gui.done").replaceAll("\\[date\\]", formattedDate)));
			} else if (requirements.length > 1) {
				descriptionLore.add(Component.text(""));
				descriptionLore.add(Component.text(requirementFinished + "/" + requirements.length));
			}
			GuiItem item = ItemBuilder
				.from(isDone || progressItem == null ? new ItemStack(Material.LIME_STAINED_GLASS_PANE) : progressItem)
				.name(Component.text(config.getString("gui.advancement-title").replaceAll("\\[title\\]", title)))
				.glow(isDone)
				.flags(ItemFlag.HIDE_ATTRIBUTES)
				.lore(descriptionLore)
				.asGuiItem();

			menu.addItem(item);
		}

		menu.getFiller().fillBetweenPoints(6, 1, 6, 9, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).asGuiItem());

		// Back
		GuiItem backItem = ItemBuilder.from(Material.BARRIER)
			.name(Component.text(config.getString("gui.back")))
			.asGuiItem(event -> openMenu(player));
		menu.setItem(6, 1, backItem);
		
		// Previous
		GuiItem previousItem = ItemBuilder.from(Material.PAPER)
			.name(Component.text(config.getString("gui.previous")))
			.asGuiItem(event -> menu.previous());
		menu.setItem(6, 3, previousItem);

		// Next
		GuiItem nextItem = ItemBuilder.from(Material.PAPER)
			.name(Component.text(config.getString("gui.next")))
			.asGuiItem(event -> menu.next());
		menu.setItem(6, 7, nextItem);

		menu.open(player);
	}
}
