package io.github.eisoptrophobia.kubryzltcraft.block.entity;

import io.github.eisoptrophobia.kubryzltcraft.data.tags.KubryzltcraftTags;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityEdificeCore extends TileEntity {
	
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
				return true;
			}
			
			@Nonnull
			@Override
			public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
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
}