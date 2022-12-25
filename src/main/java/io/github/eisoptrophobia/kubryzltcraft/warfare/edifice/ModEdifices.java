package io.github.eisoptrophobia.kubryzltcraft.warfare.edifice;

import io.github.eisoptrophobia.kubryzltcraft.Kubryzltcraft;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public class ModEdifices {
	
	public static final DeferredRegister<Edifice> EDIFICES = DeferredRegister.create(Kubryzltcraft.EDIFICE_REGISTRY, Kubryzltcraft.MOD_ID);
	
	public static final RegistryObject<Edifice> NULL = EDIFICES.register("null", EdificeNull::new);
	
	public static final RegistryObject<Edifice> RADAR = EDIFICES.register("radar", EdificeRadar::new);
	
	public static void register(IEventBus bus) {
		EDIFICES.register(bus);
	}
}