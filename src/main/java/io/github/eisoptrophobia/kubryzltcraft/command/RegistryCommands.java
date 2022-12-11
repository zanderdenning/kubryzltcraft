package io.github.eisoptrophobia.kubryzltcraft.command;

import com.mojang.brigadier.CommandDispatcher;
import io.github.eisoptrophobia.kubryzltcraft.Kubryzltcraft;
import net.minecraft.command.CommandSource;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kubryzltcraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RegistryCommands {
	
	@SubscribeEvent
	public static void onRegisterCommands(RegisterCommandsEvent event) {
		CommandDispatcher<CommandSource> commandDispatcher = event.getDispatcher();
		CommandKubryzltAdmin.register(commandDispatcher);
	}
}