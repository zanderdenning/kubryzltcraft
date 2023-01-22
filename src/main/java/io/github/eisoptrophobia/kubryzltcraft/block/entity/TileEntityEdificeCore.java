package io.github.eisoptrophobia.kubryzltcraft.block.entity;

import io.github.eisoptrophobia.kubryzltcraft.Kubryzltcraft;
import io.github.eisoptrophobia.kubryzltcraft.data.WorldSavedDataKubryzltcraftMap;
import io.github.eisoptrophobia.kubryzltcraft.data.tags.KubryzltcraftTags;
import io.github.eisoptrophobia.kubryzltcraft.warfare.edifice.Edifice;
import io.github.eisoptrophobia.kubryzltcraft.warfare.edifice.EdificeUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TileEntityEdificeCore extends TileEntity implements ITickableTileEntity {
	
	private final ItemStackHandler itemHandler = createItemHandler();
	private final LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.of(() -> itemHandler);
	private UUID uuid;
	private Rotation rotation = Rotation.NONE;
	
	public TileEntityEdificeCore() {
		super(ModTileEntities.EDIFICE_CORE.get());
		uuid = UUID.randomUUID();
		EdificeUtils.updateEdifice(level, worldPosition);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		itemHandler.deserializeNBT(nbt.getCompound("inventory"));
		uuid = nbt.getUUID("uuid");
		EdificeUtils.updateEdifice(level, worldPosition);
		super.load(state, nbt);
	}
	
	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		nbt.put("inventory", itemHandler.serializeNBT());
		nbt.putUUID("uuid", uuid);
		return super.save(nbt);
	}
	
	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = super.getUpdateTag();
		return save(nbt);
	}
	
	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT tag) {
		load(state, tag);
	}
	
	@Nullable
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(worldPosition, 1, getUpdateTag());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		handleUpdateTag(level.getBlockState(pkt.getPos()), pkt.getTag());
	}
	
	private ItemStackHandler createItemHandler() {
		return new ItemStackHandler(2) {
			@Override
			protected void onContentsChanged(int slot) {
				setChanged();
				EdificeUtils.updateEdifice(level, worldPosition);
			}
			
			@Override
			public int getSlotLimit(int slot) {
				if (slot == 0) {
					return 1;
				}
				return 64;
			}
			
			@Override
			public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
				if (slot == 0) {
					return stack.getItem().is(KubryzltcraftTags.Items.BLUEPRINT);
				}
				Edifice edifice = EdificeUtils.getEdificeByBlueprint(lazyItemHandler.orElse(itemHandler).getStackInSlot(0).getItem());
				if (edifice == null) {
					return false;
				}
				EdificeUtils.StatusData missing = EdificeUtils.getMissingBlocksServer(level, edifice, worldPosition);
				if (missing.getValidity() != EdificeUtils.Validity.INCOMPLETE) {
					return true;
				}
				return missing.getMissingBlocks().containsKey(stack.getItem());
			}
			
			public boolean isItemValid(int slot, @Nonnull ItemStack stack, EdificeUtils.StatusData missing) {
				if (slot == 1) {
					if (missing.getValidity() != EdificeUtils.Validity.INCOMPLETE) {
						return false;
					}
					return missing.getMissingBlocks().containsKey(stack.getItem());
				}
				return isItemValid(slot, stack);
			}
			
			@Nonnull
			@Override
			public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
				Edifice edifice = EdificeUtils.getEdificeByBlueprint(lazyItemHandler.orElse(itemHandler).getStackInSlot(0).getItem());
				if (slot == 1 && edifice != null) {
					EdificeUtils.StatusData missing;
					if (level.isClientSide) {
						missing = EdificeUtils.getMissingBlocks(level, edifice, worldPosition, rotation);
					}
					else {
						missing = EdificeUtils.getMissingBlocksServer(level, edifice, worldPosition);
					}
					if (isItemValid(slot, stack, missing)) {
						int count = missing.getMissingBlocks().get(stack.getItem()).size();
						ItemStack reducedStack = stack.copy();
						reducedStack.setCount(count);
						ItemStack out = super.insertItem(slot, reducedStack, simulate);
						ItemStack remainder = stack.copy();
						remainder.setCount(out.getCount() + stack.getCount() - count);
						return remainder;
					}
					return stack;
				}
				if (!isItemValid(slot, stack)) {
					return stack;
				}
				return super.insertItem(slot, stack, simulate);
			}
		};
	}
	
	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return lazyItemHandler.cast();
		}
		return super.getCapability(cap, side);
	}
	
	@Override
	public void tick() {
		if (level.isClientSide) {
			return;
		}
		IItemHandler handler = lazyItemHandler.orElse(itemHandler);
		Edifice edifice = EdificeUtils.getEdificeByBlueprint(handler.getStackInSlot(0).getItem());
		ItemStack stack = handler.getStackInSlot(1);
		if (edifice != null && !stack.isEmpty()) {
			EdificeUtils.StatusData missing = EdificeUtils.getMissingBlocksServer(level, edifice, worldPosition);
			if (missing.getValidity() == EdificeUtils.Validity.INCOMPLETE) {
				Map<Item, List<EdificeUtils.StatusData.BlockData>> itemMap = missing.getMissingBlocks();
				if (itemMap.containsKey(stack.getItem())) {
					List<EdificeUtils.StatusData.BlockData> blockDatas = itemMap.get(stack.getItem());
					EdificeUtils.StatusData.BlockData blockData = blockDatas.get(0);
					int[] position = blockData.getPos();
					BlockPos newPos = worldPosition.offset(position[0], position[1], position[2]).offset(edifice.getOffset());
					level.setBlock(newPos, EdificeUtils.getEdificeStructureData(edifice).getPalette().get(blockData.getPaletteIndex()), Constants.BlockFlags.DEFAULT);
					stack.shrink(1);
					blockDatas.remove(0);
					if (blockDatas.size() == 0) {
						itemMap.remove(stack.getItem());
					}
					WorldSavedDataKubryzltcraftMap.writeEdificeStatus(ServerLifecycleHooks.getCurrentServer().overworld(), uuid, missing);
				}
			}
		}
	}
	
	public UUID getUuid() {
		return uuid;
	}
	
	public Rotation getRotation() {
		return rotation;
	}
}