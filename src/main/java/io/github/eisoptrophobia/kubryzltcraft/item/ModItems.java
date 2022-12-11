package io.github.eisoptrophobia.kubryzltcraft.item;

import io.github.eisoptrophobia.kubryzltcraft.Kubryzltcraft;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
	
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Kubryzltcraft.MOD_ID);
	
	public static void register(IEventBus bus) {
		ITEMS.register(bus);
	}
}