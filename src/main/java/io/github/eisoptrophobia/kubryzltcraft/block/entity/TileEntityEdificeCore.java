package io.github.eisoptrophobia.kubryzltcraft.block.entity;

import com.mojang.datafixers.util.Pair;
import io.github.eisoptrophobia.kubryzltcraft.data.tags.KubryzltcraftTags;
import io.github.eisoptrophobia.kubryzltcraft.warfare.edifice.Edifice;
import io.github.eisoptrophobia.kubryzltcraft.warfare.edifice.EdificeUtils;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class TileEntityEdificeCore extends TileEntity implements ITickableTileEntity {
	
	private final ItemStackHandler itemHandler = createItemHandler();
	private final LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.of(() -> itemHandler);
	
	public TileEntityEdificeCore() {
		super(ModTileEntities.EDIFICE_CORE.get());
	}
	
	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		itemHandler.deserializeNBT(nbt.getCompound("inventory"));
		super.load(state, nbt);
	}
	
	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		nbt.put("inventory", itemHandler.serializeNBT());
		return super.save(nbt);
	}
	
	private ItemStackHandler createItemHandler() {
		return new ItemStackHandler(2) {
			@Override
			protected void onContentsChanged(int slot) {
				setChanged();
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
				Pair<EdificeUtils.Validity, Map<Item, Pair<Integer, List<Pair<Integer, int[]>>>>> missing = EdificeUtils.getMissingBlocks(level, edifice, worldPosition, Rotation.NONE);
				if (missing.getFirst() != EdificeUtils.Validity.INCOMPLETE) {
					return true;
				}
				return missing.getSecond().containsKey(stack.getItem());
			}
			
			public boolean isItemValid(int slot, @Nonnull ItemStack stack, Pair<EdificeUtils.Validity, Map<Item, Pair<Integer, List<Pair<Integer, int[]>>>>> missing) {
				if (slot == 1) {
					if (missing.getFirst() != EdificeUtils.Validity.INCOMPLETE) {
						return false;
					}
					return missing.getSecond().containsKey(stack.getItem());
				}
				return isItemValid(slot, stack);
			}
			
			@Nonnull
			@Override
			public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
				Edifice edifice = EdificeUtils.getEdificeByBlueprint(lazyItemHandler.orElse(itemHandler).getStackInSlot(0).getItem());
				if (slot == 1 && edifice != null) {
					Pair<EdificeUtils.Validity, Map<Item, Pair<Integer, List<Pair<Integer, int[]>>>>> missing = EdificeUtils.getMissingBlocks(level, edifice, worldPosition, Rotation.NONE);
					if (isItemValid(slot, stack, missing)) {
						int count = missing.getSecond().get(stack.getItem()).getFirst();
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
		IItemHandler handler = lazyItemHandler.orElse(itemHandler);
		Edifice edifice = EdificeUtils.getEdificeByBlueprint(handler.getStackInSlot(0).getItem());
		ItemStack stack = handler.getStackInSlot(1);
		if (edifice != null && !stack.isEmpty()) {
			Pair<EdificeUtils.Validity, Map<Item, Pair<Integer, List<Pair<Integer, int[]>>>>> missing = EdificeUtils.getMissingBlocks(level, edifice, worldPosition, Rotation.NONE);
			if (missing.getFirst() == EdificeUtils.Validity.INCOMPLETE) {
				Map<Item, Pair<Integer, List<Pair<Integer, int[]>>>> itemMap = missing.getSecond();
				if (itemMap.containsKey(stack.getItem())) {
					Pair<Integer, int[]> blockData = itemMap.get(stack.getItem()).getSecond().get(0);
					int[] position = blockData.getSecond();
					BlockPos newPos = worldPosition.offset(position[0], position[1], position[2]).offset(edifice.getOffset());
					level.setBlock(newPos, EdificeUtils.getEdificeStructureData(edifice).getPalette().get(blockData.getFirst()), Constants.BlockFlags.DEFAULT);
					stack.shrink(1);
				}
			}
		}
	}
}