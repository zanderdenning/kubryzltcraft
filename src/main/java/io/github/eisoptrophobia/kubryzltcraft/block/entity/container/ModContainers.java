package io.github.eisoptrophobia.kubryzltcraft.block.entity.container;

import io.github.eisoptrophobia.kubryzltcraft.Kubryzltcraft;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainers {

	public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, Kubryzltcraft.MOD_ID);
	
	public static final RegistryObject<ContainerType<ContainerEdificeCore>> EDIFICE_CORE = CONTAINERS.register("edifice_core",
			() -> IForgeContainerType.create(((windowId, inv, data) -> {
				BlockPos pos = data.readBlockPos();
				World world = inv.player.level;
				return new ContainerEdificeCore(windowId, world, pos, inv, inv.player);
			}))
	);
	
	public static void register(IEventBus bus) {
		CONTAINERS.register(bus);
	}
}