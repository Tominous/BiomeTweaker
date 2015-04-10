package me.superckl.biometweaker.client.gui;

import java.util.ArrayList;
import java.util.List;

import me.superckl.biometweaker.common.reference.ModData;
import net.minecraft.client.gui.GuiScreen;
import cpw.mods.fml.client.config.ConfigGuiType;
import cpw.mods.fml.client.config.DummyConfigElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;

public class GuiScreenConfig extends GuiConfig{

	public GuiScreenConfig(final GuiScreen parentScreen) {
		super(parentScreen, GuiScreenConfig.getConfigElements(), ModData.MOD_ID, true, false, "BiomeTweaker Config", "100% success rate every other time!");
	}

	private static List<IConfigElement> getConfigElements(){
		final List<IConfigElement> list = new ArrayList<IConfigElement>();
		final DummyConfigElement dummy = new DummyConfigElement<Boolean>("Reload Scripts", false, ConfigGuiType.BOOLEAN, "biometweaker.cfg.reload");
		dummy.setConfigEntryClass(ReloadScriptsConfigEntry.class);
		list.add(dummy);
		return list;
	}

}