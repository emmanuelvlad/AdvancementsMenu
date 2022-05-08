package me.evlad.advancementsmenu.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;

public class AdvancementTabs {
	public final List<AdvancementType> tabs;
	private final List<AdvancementType> all = new ArrayList<>();

	public AdvancementTabs() {
		Iterator<Advancement> advIt = Bukkit.advancementIterator();

		advIt.forEachRemaining(advancement -> {
			AdvancementType type = new AdvancementType(advancement);

			all.add(type);
		});

		Stream<AdvancementType> allStream = all.stream();

		this.tabs = allStream.filter((type) -> {
			if (!type.isTab)
				return false;

			Stream<AdvancementType> allStream2 = all.stream();
			
			type.childrens = allStream2.filter((type2) -> {
				return type.namespace.equals(type2.namespace) && type2.name.equals(type.name) && !type2.path.equals(type.path);
			}).toList();
			
			allStream2.close();

			return true;
		}).toList();

		allStream.close();
	}
}
