package io.github.eisoptrophobia.kubryzltcraft;

import io.github.eisoptrophobia.kubryzltcraft.block.ModBlocks;
import io.github.eisoptrophobia.kubryzltcraft.block.entity.ModTileEntities;
import io.github.eisoptrophobia.kubryzltcraft.block.entity.container.ModContainers;
import io.github.eisoptrophobia.kubryzltcraft.client.ClientSetup;
import io.github.eisoptrophobia.kubryzltcraft.data.capability.kubryzltcraftteammember.CapabilityKubryzltcraftTeamMember;
import io.github.eisoptrophobia.kubryzltcraft.item.ModItems;
import io.github.eisoptrophobia.kubryzltcraft.network.Network;
import io.github.eisoptrophobia.kubryzltcraft.server.ServerSetup;
import io.github.eisoptrophobia.kubryzltcraft.warfare.ModTerritoryManagers;
import io.github.eisoptrophobia.kubryzltcraft.warfare.TerritoryManager;
import io.github.eisoptrophobia.kubryzltcraft.warfare.edifice.Edifice;
import io.github.eisoptrophobia.kubryzltcraft.warfare.edifice.EdificeUtils;
import io.github.eisoptrophobia.kubryzltcraft.warfare.edifice.ModEdifices;
import io.github.eisoptrophobia.kubryzltcraft.warfare.team.KubryzltcraftTeamManager;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

@Mod(Kubryzltcraft.MOD_ID)
public class Kubryzltcraft {
	
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MOD_ID = "kubryzltcraft";
	
	public static final IForgeRegistry<Edifice> EDIFICE_REGISTRY = new RegistryBuilder<Edifice>().setType(Edifice.class).setName(new ResourceLocation(MOD_ID, "edifice")).create();
	public static final IForgeRegistry<TerritoryManager> TERRITORY_MANAGER_REGISTRY = new RegistryBuilder<TerritoryManager>().setType(TerritoryManager.class).setName(new ResourceLocation(MOD_ID, "territory_manager")).create();
	
	public Kubryzltcraft() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigCommon.CONFIG);
		ConfigCommon.load(ConfigCommon.CONFIG, FMLPaths.CONFIGDIR.get().resolve(MOD_ID + "-" +ModConfig.Type.COMMON.extension() + ".toml"));
		
		MinecraftForge.EVENT_BUS.register(this);
		
		ModItems.register(eventBus);
		ModBlocks.register(eventBus);
		ModTileEntities.register(eventBus);
		ModContainers.register(eventBus);
		ModTerritoryManagers.register(eventBus);
		ModEdifices.register(eventBus);
		
		for (ConfigCommon.TeamsConfig.TeamConfig team : ConfigCommon.getTeams()) {
			KubryzltcraftTeamManager.register(team.id, team.prefix, team.getKubryzltTexture());
		}
		
		eventBus.addListener(this::setup);
		eventBus.addListener(ClientSetup::setup);
		eventBus.addListener(ServerSetup::setup);
	}
	
	private void setup(FMLCommonSetupEvent event) {
		Network.init();
		
		EdificeUtils.initRegistry();
		
		CapabilityKubryzltcraftTeamMember.register();
		
		ResourceLocation territoryManagerResourceLocation = ResourceLocation.tryParse(ConfigCommon.TERRITORY_MANAGER.get());
		for (Map.Entry<RegistryKey<TerritoryManager>, TerritoryManager> e : TERRITORY_MANAGER_REGISTRY.getEntries()) {
			if (e.getKey().location().equals(territoryManagerResourceLocation)) {
				TerritoryManager.setManager(e.getValue());
			}
		}
		
		TerritoryManager.getManager().init();
	}
}