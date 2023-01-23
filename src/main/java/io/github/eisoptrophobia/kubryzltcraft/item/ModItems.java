package io.github.eisoptrophobia.kubryzltcraft.item;

import io.github.eisoptrophobia.kubryzltcraft.Kubryzltcraft;
import io.github.eisoptrophobia.kubryzltcraft.item.creative.ItemGroupKubryzltcraft;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
	
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Kubryzltcraft.MOD_ID);
	
	public static final RegistryObject<Item> BLUEPRINT_BLANK = ITEMS.register("blueprint_blank", () -> new Item(new Item.Properties().tab(ItemGroupKubryzltcraft.INSTANCE)));
	
	public static final RegistryObject<Item> BLUEPRINT_RADAR = ITEMS.register("blueprint_radar", () -> new Item(new Item.Properties().tab(ItemGroupKubryzltcraft.INSTANCE)));
	
	public static final RegistryObject<Item> KUBRYZLT_KEY = ITEMS.register("kubryzlt_key", () -> new Item(new Item.Properties().tab(ItemGroupKubryzltcraft.INSTANCE).rarity(Rarity.RARE)));
	
	public static void register(IEventBus bus) {
		ITEMS.register(bus);
	}
}