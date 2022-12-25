package io.github.eisoptrophobia.kubryzltcraft;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber(modid = Kubryzltcraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigCommon {
	
	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static ForgeConfigSpec CONFIG;
	
	public static ForgeConfigSpec.ConfigValue<String> TERRITORY_MANAGER;
	public static ForgeConfigSpec.ConfigValue<Integer> DEFAULT_MORALE;
	public static ForgeConfigSpec.ConfigValue<String> TIME_ZONE;
	public static ForgeConfigSpec.ConfigValue<Integer> DAY_TICK_HOUR;
	public static ForgeConfigSpec.ConfigValue<Integer> EDIFICE_UPDATE_RADIUS;
	
	static {
		BUILDER.push("general");
		TERRITORY_MANAGER = BUILDER.define("territoryManager", "kubryzltcraft:null");
		DEFAULT_MORALE = BUILDER.define("defaultMorale", 1000);
		TIME_ZONE = BUILDER.define("timeZone", "America/Los_Angeles");
		DAY_TICK_HOUR = BUILDER.define("dayTickHour", 20);
		EDIFICE_UPDATE_RADIUS = BUILDER.define("edificeUpdateRadius", 15);
		BUILDER.pop();
	}
	
	private static TeamsConfig TEAMS;
	
	static {
		BUILDER.comment("Team config")
				.define("teams", new ArrayList<>());
	}
	
	static {
		CONFIG = BUILDER.build();
	}
	
	public static class TeamsConfig {
		
		public List<TeamConfig> teams;
		
		public static class TeamConfig {
			
			public String id;
			public String prefix;
			public String kubryzltTexture;
			
			public ResourceLocation getKubryzltTexture() {
				return ResourceLocation.tryParse(kubryzltTexture);
			}
		}
	}
	
	public static List<TeamsConfig.TeamConfig> getTeams() {
		return TEAMS.teams == null ? Collections.emptyList() : TEAMS.teams;
	}
	
	public static void setup(CommentedConfig config) {
		TEAMS = new ObjectConverter().toObject(config, TeamsConfig::new);
	}
	
	public static void load(ForgeConfigSpec spec, Path path) {
		CommentedFileConfig config = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
		config.load();
		spec.setConfig(config);
		setup(config);
	}
	
	@SubscribeEvent
	public static void onConfigReloading(ModConfig.Reloading event) {
		ModConfig config = event.getConfig();
		if (config.getModId().equals(Kubryzltcraft.MOD_ID)) {
			setup(config.getConfigData());
		}
	}
}