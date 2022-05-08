package me.evlad.advancementsmenu.utils;

import java.util.HashMap;
import java.util.List;

import org.bukkit.advancement.Advancement;

public class AdvancementType extends HashMap<String, AdvancementInfo> {
	public boolean isRoot;
	public boolean isTab;
	public AdvancementType parent;
	public List<AdvancementType> childrens;
	public String namespace;
	public String path;
	public String name;
	public AdvancementInfo info;
	public Advancement advancement;

	public AdvancementType(Advancement advancement) {
		this.advancement = advancement;
		this.info = new AdvancementInfo(advancement);
		this.namespace = advancement.getKey().getNamespace();
		this.path = advancement.getKey().getKey();

		String[] splitPath = path.split("/");

		this.name = splitPath[0];
		if (name.equals("recipes"))
			return;
		this.isRoot = splitPath[1].equals("root");
		this.isTab = splitPath.length == 2 && isRoot;
	}
}
