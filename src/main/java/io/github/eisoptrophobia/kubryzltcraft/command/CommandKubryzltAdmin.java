package io.github.eisoptrophobia.kubryzltcraft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.eisoptrophobia.kubryzltcraft.warfare.Territory;
import io.github.eisoptrophobia.kubryzltcraft.warfare.TerritoryManager;
import io.github.eisoptrophobia.kubryzltcraft.warfare.team.KubryzltcraftTeam;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

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
				);
		commandDispatcher.register(command);
	}
}