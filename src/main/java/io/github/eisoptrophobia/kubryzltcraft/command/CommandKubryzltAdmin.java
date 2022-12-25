package io.github.eisoptrophobia.kubryzltcraft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.eisoptrophobia.kubryzltcraft.data.WorldSavedDataKubryzltcraftMap;
import io.github.eisoptrophobia.kubryzltcraft.warfare.Territory;
import io.github.eisoptrophobia.kubryzltcraft.warfare.TerritoryManager;
import io.github.eisoptrophobia.kubryzltcraft.warfare.team.KubryzltcraftTeam;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.UUID;
import java.util.stream.Collectors;

public class CommandKubryzltAdmin {

	public static void register(CommandDispatcher<CommandSource> commandDispatcher) {
		LiteralArgumentBuilder<CommandSource> command = Commands.literal("kubryzltadmin")
				.requires((commandSource -> commandSource.hasPermission(3)))
				.then(Commands.literal("convert")
					.then(Commands.argument("territory", ArgumentTerritory.territory())
						.then(Commands.argument("team", ArgumentKubryzltcraftTeam.kubryzltcraftTeam())
							.executes(context -> {
								Territory territory = ArgumentTerritory.getTerritory(context, "territory");
								KubryzltcraftTeam team = ArgumentKubryzltcraftTeam.getKubryzltcraftTeam(context, "team");
								TerritoryManager.getManager().convert(territory, team);
								return 1;
							})
						)
					)
				)
				.then(Commands.literal("morale")
					.then(Commands.literal("get")
						.then(Commands.argument("territory", ArgumentTerritory.territory())
							.executes(context -> {
								Territory territory = ArgumentTerritory.getTerritory(context, "territory");
								context.getSource().sendSuccess(new TranslationTextComponent("commands.kubryzltcraft.kubryzltadminmoraleget.success", territory.getId(), "" + territory.getMoraleServer()), false);
								return 1;
							})
						)
					)
					.then(Commands.literal("set")
						.then(Commands.argument("territory", ArgumentTerritory.territory())
							.then(Commands.argument("morale", IntegerArgumentType.integer(0))
								.executes(context -> {
									Territory territory = ArgumentTerritory.getTerritory(context, "territory");
									int morale = IntegerArgumentType.getInteger(context, "morale");
									territory.setMorale(morale);
									context.getSource().sendSuccess(new TranslationTextComponent("commands.kubryzltcraft.kubryzltadminmoraleset.success", territory.getId(), morale), false);
									return 1;
								})
							)
						)
					)
				).then(Commands.literal("edifice")
					.then(Commands.literal("list")
						.then(Commands.argument("territory", ArgumentTerritory.territory())
							.executes(context -> {
								Territory territory = ArgumentTerritory.getTerritory(context, "territory");
								String out = WorldSavedDataKubryzltcraftMap.readKubryzltEdifices(ServerLifecycleHooks.getCurrentServer().overworld(), territory.getId()).stream().map(UUID::toString).collect(Collectors.joining(", "));
								context.getSource().sendSuccess(new StringTextComponent(out), false);
								return 1;
							})
						)
					)
				);
		commandDispatcher.register(command);
	}
}