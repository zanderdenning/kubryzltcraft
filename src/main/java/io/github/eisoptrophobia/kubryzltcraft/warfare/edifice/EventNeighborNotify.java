package io.github.eisoptrophobia.kubryzltcraft.warfare.edifice;

import io.github.eisoptrophobia.kubryzltcraft.Kubryzltcraft;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kubryzltcraft.MOD_ID)
public class EventNeighborNotify {

	@SubscribeEvent
	public static void onNeighborNotify(BlockEvent.NeighborNotifyEvent event) {
		if (!event.getWorld().isClientSide()) {
			EdificeUtils.updateEdificesAround(((World) event.getWorld()), event.getPos());
		}
	}
}