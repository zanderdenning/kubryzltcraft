package io.github.eisoptrophobia.kubryzltcraft.block;

import io.github.eisoptrophobia.kubryzltcraft.block.entity.ModTileEntities;
import io.github.eisoptrophobia.kubryzltcraft.block.entity.TileEntityEdificeCore;
import io.github.eisoptrophobia.kubryzltcraft.block.entity.container.ContainerEdificeCore;
import io.github.eisoptrophobia.kubryzltcraft.data.WorldSavedDataKubryzltcraftMap;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.UUID;

public class BlockEdificeCore extends Block {
	
	public BlockEdificeCore() {
		super(AbstractBlock.Properties.of((new Material.Builder(MaterialColor.METAL)).nonSolid().build(), MaterialColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL));
	}
	
	@Override
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState blockState2, boolean bool) {
		if (!state.is(blockState2.getBlock())) {
			TileEntity tileEntity = world.getBlockEntity(pos);
			if (tileEntity instanceof TileEntityEdificeCore) {
				ServerWorld serverWorld = ServerLifecycleHooks.getCurrentServer().overworld();
				UUID uuid = ((TileEntityEdificeCore) tileEntity).getUuid();
				WorldSavedDataKubryzltcraftMap.cleanKubryzltEdifices(serverWorld, uuid);
				WorldSavedDataKubryzltcraftMap.deleteEdificeLocationStatus(serverWorld, uuid);
				tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
					NonNullList<ItemStack> items = NonNullList.create();
					for (int i = 0; i < handler.getSlots(); i++) {
						ItemStack item = handler.getStackInSlot(i);
						if (!item.equals(ItemStack.EMPTY)) {
							items.add(item);
						}
					}
					InventoryHelper.dropContents(world, pos, items);
					world.updateNeighbourForOutputSignal(pos, this);
				});
			}
		}
		super.onRemove(state, world, pos, blockState2, bool);
	}
	
	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
		if (!world.isClientSide()) {
			TileEntity tileEntity = world.getBlockEntity(pos);
			if (!player.isCrouching()) {
				if (tileEntity instanceof TileEntityEdificeCore) {
					INamedContainerProvider containerProvider = createContainerProvider(world, pos);
					NetworkHooks.openGui(((ServerPlayerEntity) player), containerProvider, tileEntity.getBlockPos());
				}
			}
		}
		return ActionResultType.SUCCESS;
	}
	
	private INamedContainerProvider createContainerProvider(World world, BlockPos pos) {
		return new INamedContainerProvider() {
			@Override
			public ITextComponent getDisplayName() {
				return new TranslationTextComponent("screen.kubryzltcraft.edifice_core");
			}
			
			@Nullable
			@Override
			public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity playerEntity) {
				return new ContainerEdificeCore(id, world, pos, playerInventory, playerEntity);
			}
		};
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return ModTileEntities.EDIFICE_CORE.get().create();
	}
}