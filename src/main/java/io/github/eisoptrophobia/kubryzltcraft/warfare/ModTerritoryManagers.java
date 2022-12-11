package io.github.eisoptrophobia.kubryzltcraft.warfare;

import io.github.eisoptrophobia.kubryzltcraft.Kubryzltcraft;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public class ModTerritoryManagers {
	
	public static final DeferredRegister<TerritoryManager> TERRITORY_MANAGERS = DeferredRegister.create(TerritoryManager.class, Kubryzltcraft.MOD_ID);
	
	public static final RegistryObject<TerritoryManager> NULL = TERRITORY_MANAGERS.register("null", TerritoryManagerNull::new);
	public static final RegistryObject<TerritoryManager> TEST = TERRITORY_MANAGERS.register("test", TerritoryManagerTest::new);
	
	public static void register(IEventBus bus) {
		TERRITORY_MANAGERS.register(bus);
	}
}