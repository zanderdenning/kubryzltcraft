package io.github.eisoptrophobia.kubryzltcraft.block.entity.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class ContainerEdificeCore extends Container {
	
	private static final int containerSize = 2;
	
	private final TileEntity tileEntity;
	private final PlayerEntity player;
	private final IItemHandler playerInventory;
	private final IInventory container;
	
	public ContainerEdificeCore(int windowId, World world, BlockPos pos, PlayerInventory inventory, PlayerEntity player) {
		super(ModContainers.EDIFICE_CORE.get(), windowId);
		this.tileEntity = world.getBlockEntity(pos);
		this.player = player;
		this.playerInventory = new InvWrapper(inventory);
		this.container = inventory;
		
		makeInventorySlots();
		
		if (tileEntity != null) {
			tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
				addSlot(new SlotItemHandler(handler, 0, 98, 35));
				addSlot(new SlotItemHandler(handler, 1, 134, 35));
			});
		}
	}
	
	private void makeInventorySlots() {
		addSlotRow(8, 142, 0, 18);
		addSlotRow(8, 84, 9, 18);
		addSlotRow(8, 102, 18, 18);
		addSlotRow(8, 120, 27, 18);
	}
	
	private void addSlotRow(int x, int y, int index, int dx) {
		for (int i = 0; i < 9; i ++) {
			addSlot(new SlotItemHandler(playerInventory, index + i, x + i * dx, y));
		}
	}
	
	@Override
	public ItemStack quickMoveStack(PlayerEntity player, int index) {
		Slot slot = slots.get(index);
		if (slot == null || !slot.hasItem()) {
			return ItemStack.EMPTY;
		}
		ItemStack sourceStack = slot.getItem();
		ItemStack stack = sourceStack.copy();
		if (index < 36) {
			if (!moveItemStackTo(sourceStack, 36, 36 + containerSize, false)) {
				return ItemStack.EMPTY;
			}
		}
		else if (index < 36 + containerSize) {
			if (!moveItemStackTo(sourceStack, 0, 36, false)) {
				return ItemStack.EMPTY;
			}
		}
		else {
			return ItemStack.EMPTY;
		}
		if (sourceStack.getCount() == 0) {
			slot.set(ItemStack.EMPTY);
		}
		else {
			slot.setChanged();
		}
		slot.onTake(player, sourceStack);
		return stack;
	}
	
	@Override
	public boolean stillValid(PlayerEntity playerEntity) {
		return container.stillValid(playerEntity);
	}
}