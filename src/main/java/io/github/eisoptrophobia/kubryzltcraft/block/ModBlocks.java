package io.github.eisoptrophobia.kubryzltcraft.block;

import io.github.eisoptrophobia.kubryzltcraft.Kubryzltcraft;
import io.github.eisoptrophobia.kubryzltcraft.item.ModItems;
import io.github.eisoptrophobia.kubryzltcraft.item.creative.ItemGroupKubryzltcraft;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ModBlocks {
	
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Kubryzltcraft.MOD_ID);
	
	public static final RegistryObject<Block> KUBRYZLT = registerBlock("kubryzlt", BlockKubryzlt::new);
	
	public static final RegistryObject<Block> EDIFICE_CORE = registerBlock("edifice_core", BlockEdificeCore::new);
	
	public static void register(IEventBus bus) {
		BLOCKS.register(bus);
	}
	
	public static RegistryObject<Block> registerBlock(String name, Supplier<Block> supplier) {
		RegistryObject<Block> block = BLOCKS.register(name, supplier);
		ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(ItemGroupKubryzltcraft.INSTANCE)));
		return block;
	}
}