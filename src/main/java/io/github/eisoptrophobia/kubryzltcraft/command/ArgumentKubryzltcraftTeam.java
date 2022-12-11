package io.github.eisoptrophobia.kubryzltcraft.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.eisoptrophobia.kubryzltcraft.warfare.Territory;
import io.github.eisoptrophobia.kubryzltcraft.warfare.TerritoryManager;
import io.github.eisoptrophobia.kubryzltcraft.warfare.team.KubryzltcraftTeam;
import io.github.eisoptrophobia.kubryzltcraft.warfare.team.KubryzltcraftTeamManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ArgumentKubryzltcraftTeam implements ArgumentType<KubryzltcraftTeam> {
	
	public static final DynamicCommandExceptionType ERROR_INVALID_VALUE = new DynamicCommandExceptionType(o -> new TranslationTextComponent("kubryzltcraft.argument.kubryzltcraft_team.invalid", o));
	
	private ArgumentKubryzltcraftTeam() {}
	
	public static ArgumentKubryzltcraftTeam kubryzltcraftTeam() {
		return new ArgumentKubryzltcraftTeam();
	}
	
	public static KubryzltcraftTeam getKubryzltcraftTeam(CommandContext<CommandSource> context, String arg) {
		return context.getArgument(arg, KubryzltcraftTeam.class);
	}
	
	@Override
	public KubryzltcraftTeam parse(StringReader reader) throws CommandSyntaxException {
		String s = reader.readUnquotedString();
		KubryzltcraftTeam team = KubryzltcraftTeamManager.getTeamById(s);
		if (team == null) {
			throw ERROR_INVALID_VALUE.create(s);
		}
		else {
			return team;
		}
	}
	
	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder suggestionsBuilder) {
		return ISuggestionProvider.suggest(KubryzltcraftTeamManager.getTeams().stream().map(KubryzltcraftTeam::getId).sorted().collect(Collectors.toList()), suggestionsBuilder);
	}
}