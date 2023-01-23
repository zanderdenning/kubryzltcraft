package io.github.eisoptrophobia.kubryzltcraft.block;

import io.github.eisoptrophobia.kubryzltcraft.block.entity.ModTileEntities;
import io.github.eisoptrophobia.kubryzltcraft.block.entity.TileEntityEdificeCore;
import io.github.eisoptrophobia.kubryzltcraft.data.capability.kubryzltcraftteammember.CapabilityKubryzltcraftTeamMember;
import io.github.eisoptrophobia.kubryzltcraft.item.ModItems;
import io.github.eisoptrophobia.kubryzltcraft.warfare.Territory;
import io.github.eisoptrophobia.kubryzltcraft.warfare.TerritoryManager;
import io.github.eisoptrophobia.kubryzltcraft.warfare.team.KubryzltcraftTeam;
import io.github.eisoptrophobia.kubryzltcraft.warfare.team.KubryzltcraftTeamManager;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockKubryzlt extends Block {
	
	public BlockKubryzlt() {
		super(AbstractBlock.Properties.of(Material.METAL).strength(-1.0F, 3600000.0F).lightLevel(s -> 15));
	}
	
	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
		if (player.getItemInHand(hand).getItem() == ModItems.KUBRYZLT_KEY.get()) {
			if (!world.isClientSide()) {
				if (!player.isCrouching()) {
					Territory territory = TerritoryManager.getManager().getTerritoryByBlockPos(world, pos);
					player.getCapability(CapabilityKubryzltcraftTeamMember.INSTANCE).ifPresent(handler -> {
						KubryzltcraftTeam team = handler.getTeam();
						if (team != KubryzltcraftTeamManager.getTerritoryTeamServer(territory)) {
							TerritoryManager.getManager().convert(territory, team);
							player.getItemInHand(hand).shrink(1);
						}
					});
				}
			}
		}
		return ActionResultType.SUCCESS;
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