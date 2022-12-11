package io.github.eisoptrophobia.kubryzltcraft.block;

import io.github.eisoptrophobia.kubryzltcraft.block.entity.ModTileEntities;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class BlockKubryzlt extends Block {
	
	public BlockKubryzlt() {
		super(AbstractBlock.Properties.of(Material.METAL).strength(-1.0F, 3600000.0F).lightLevel(s -> 15));
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return ModTileEntities.KUBRYZLT.get().create();
	}
	
	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}
}