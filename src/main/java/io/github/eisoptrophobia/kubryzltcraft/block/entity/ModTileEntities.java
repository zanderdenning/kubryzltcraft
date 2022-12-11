package io.github.eisoptrophobia.kubryzltcraft.block.entity;

import io.github.eisoptrophobia.kubryzltcraft.Kubryzltcraft;
import io.github.eisoptrophobia.kubryzltcraft.block.BlockKubryzlt;
import io.github.eisoptrophobia.kubryzltcraft.block.ModBlocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntities {
	
	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Kubryzltcraft.MOD_ID);
	
	public static final RegistryObject<TileEntityType<TileEntityKubryzlt>> KUBRYZLT = TILE_ENTITIES.register("kubryzlt",
			() -> TileEntityType.Builder.of(TileEntityKubryzlt::new, ModBlocks.KUBRYZLT.get()).build(null)
	);
	
	public static final RegistryObject<TileEntityType<TileEntityEdificeCore>> EDIFICE_CORE = TILE_ENTITIES.register("edifice_core",
			() -> TileEntityType.Builder.of(TileEntityEdificeCore::new, ModBlocks.EDIFICE_CORE.get()).build(null)
	);
	
	public static void register(IEventBus bus) {
		TILE_ENTITIES.register(bus);
	}
}