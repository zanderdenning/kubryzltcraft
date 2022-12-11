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
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ArgumentTerritory implements ArgumentType<Territory> {
	
	public static final DynamicCommandExceptionType ERROR_INVALID_VALUE = new DynamicCommandExceptionType(o -> new TranslationTextComponent("kubryzltcraft.argument.territory.invalid", o));
	
	private ArgumentTerritory() {}
	
	public static ArgumentTerritory territory() {
		return new ArgumentTerritory();
	}
	
	public static Territory getTerritory(CommandContext<CommandSource> context, String arg) {
		return context.getArgument(arg, Territory.class);
	}
	
	@Override
	public Territory parse(StringReader reader) throws CommandSyntaxException {
		String s = reader.readUnquotedString();
		Territory territory = TerritoryManager.getManager().getTerritoryById(s);
		if (territory == null) {
			throw ERROR_INVALID_VALUE.create(s);
		}
		else {
			return territory;
		}
	}
	
	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder suggestionsBuilder) {
		return ISuggestionProvider.suggest(TerritoryManager.getManager().getTerritories().stream().map(Territory::getId).sorted().collect(Collectors.toList()), suggestionsBuilder);
	}
}