package io.github.eisoptrophobia.kubryzltcraft;

import io.github.eisoptrophobia.kubryzltcraft.block.ModBlocks;
import io.github.eisoptrophobia.kubryzltcraft.block.entity.ModTileEntities;
import io.github.eisoptrophobia.kubryzltcraft.client.ClientSetup;
import io.github.eisoptrophobia.kubryzltcraft.data.CapabilityKubryzltcraftTeamMember;
import io.github.eisoptrophobia.kubryzltcraft.item.ModItems;
import io.github.eisoptrophobia.kubryzltcraft.warfare.ModTerritoryManagers;
import io.github.eisoptrophobia.kubryzltcraft.warfare.TerritoryManager;
import io.github.eisoptrophobia.kubryzltcraft.warfare.edifice.ModEdifices;
import io.github.eisoptrophobia.kubryzltcraft.warfare.team.KubryzltcraftTeamManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Kubryzltcraft.MOD_ID)
public class Kubryzltcraft {
	
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MOD_ID = "kubryzltcraft";
	
	public Kubryzltcraft() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigCommon.CONFIG);
		ConfigCommon.load(ConfigCommon.CONFIG, FMLPaths.CONFIGDIR.get().resolve(MOD_ID + "-" +ModConfig.Type.COMMON.extension() + ".toml"));
		
		MinecraftForge.EVENT_BUS.register(this);
		
		ModTerritoryManagers.TERRITORY_MANAGERS.makeRegistry("territory_manager", RegistryBuilder::new);
		ModEdifices.EDIFICES.makeRegistry("edifice", RegistryBuilder::new);
		
		ModItems.ITEMS.register(eventBus);
		ModBlocks.BLOCKS.register(eventBus);
		ModTileEntities.TILE_ENTITIES.register(eventBus);
		ModTerritoryManagers.TERRITORY_MANAGERS.register(eventBus);
		ModEdifices.EDIFICES.register(eventBus);
		
		for (ConfigCommon.TeamsConfig.TeamConfig team : ConfigCommon.getTeams()) {
			KubryzltcraftTeamManager.register(team.id, team.prefix, team.getKubryzltTexture());
		}
		
		eventBus.addListener(this::setup);
		eventBus.addListener(ClientSetup::setup);
	}
	
	private void setup(FMLCommonSetupEvent event) {
		CapabilityKubryzltcraftTeamMember.register();
		
		ResourceLocation territoryManagerResourceLocation = ResourceLocation.tryParse(ConfigCommon.TERRITORY_MANAGER.get());
		for (RegistryObject<TerritoryManager> o : ModTerritoryManagers.TERRITORY_MANAGERS.getEntries()) {
			if (o.getId().equals(territoryManagerResourceLocation)) {
				TerritoryManager.setManager(o.get());
			}
		}
		
		TerritoryManager.getManager().init();
	}
}