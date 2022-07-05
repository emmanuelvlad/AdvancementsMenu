package me.evlad.advancementsmenu.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

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
			}).collect(Collectors.toList());

			return true;
		}).collect(Collectors.toList());
	}

	public List<AdvancementType> getTabs() {
		return tabs.stream().filter((tab) -> {
			String[][] requirements = tab.info.getRequirements();
			if (requirements == null)
				return false;

			boolean isImpossible = false;

			for (String [] parent : requirements) {
				for (String requirement: parent) {
					isImpossible = requirement.equals("impossible");
				}
			}

			return !isImpossible;
		}).collect(Collectors.toList());
	}

	public List<AdvancementType> getAllTabs() {
		return tabs;
	}
}
