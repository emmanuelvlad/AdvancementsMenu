package me.evlad.advancementsmenu.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;

public class AdvancementTabs {
	private final List<AdvancementType> tabs;
	private final List<AdvancementType> all = new ArrayList<>();

	public AdvancementTabs() {
		Iterator<Advancement> advIt = Bukkit.advancementIterator();

		advIt.forEachRemaining(advancement -> {
			AdvancementType type = new AdvancementType(advancement);

			all.add(type);
		});

		this.tabs = all.stream().filter((type) -> {
			if (!type.isTab)
				return false;
			
			type.childrens = all.stream().filter((type2) -> {
				return type.namespace.equals(type2.namespace) && type2.name.equals(type.name) && !type2.path.equals(type.path);
			}).toList();

			return true;
		}).toList();
	}

	public List<AdvancementType> getTabs() {
		return tabs.stream().filter((tab) -> {
			String[][] requirements = tab.info.getRequirements();
			boolean isImpossible = false;

			for (String [] parent : requirements) {
				for (String requirement: parent) {
					isImpossible = requirement.equals("impossible");
				}
			}

			return !isImpossible;
		}).toList();
	}

	public List<AdvancementType> getAllTabs() {
		return tabs;
	}
}
