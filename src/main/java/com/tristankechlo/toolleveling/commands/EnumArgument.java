package com.tristankechlo.toolleveling.commands;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.TranslatableText;

/**
 * copied from Forge <b>net.minecraftforge.server.command.EnumArgument</b><br>
 * and made some small changes to work with fabric
 */
public final class EnumArgument<T extends Enum<T>> implements ArgumentType<T> {

	private static final Dynamic2CommandExceptionType INVALID_ENUM = new Dynamic2CommandExceptionType(
			(found, constants) -> new TranslatableText("commands.arguments.enum.invalid", found, constants));
	private final Class<T> enumClass;

	public static <R extends Enum<R>> EnumArgument<R> enumArgument(Class<R> enumClass) {
		return new EnumArgument<>(enumClass);
	}

	private EnumArgument(final Class<T> enumClass) {
		this.enumClass = enumClass;
	}

	@Override
	public T parse(final StringReader reader) throws CommandSyntaxException {
		String name = reader.readUnquotedString();
		try {
			return Enum.valueOf(enumClass, name);
		} catch (IllegalArgumentException e) {
			throw INVALID_ENUM.createWithContext(reader, name, Arrays.toString(enumClass.getEnumConstants()));
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context,
			final SuggestionsBuilder builder) {
		return suggest(Stream.of(enumClass.getEnumConstants()).map(Object::toString), builder);
	}

	@Override
	public Collection<String> getExamples() {
		return Stream.of(enumClass.getEnumConstants()).map(Object::toString).collect(Collectors.toList());
	}

	private static CompletableFuture<Suggestions> suggest(Stream<String> stream, SuggestionsBuilder builder) {
		String s = builder.getRemaining().toLowerCase(Locale.ROOT);
		stream.filter((string) -> {
			return matchesSubStr(s, string.toLowerCase(Locale.ROOT));
		}).forEach(builder::suggest);
		return builder.buildFuture();
	}

	private static boolean matchesSubStr(String string1, String string2) {
		for (int i = 0; !string2.startsWith(string1, i); ++i) {
			i = string2.indexOf(95, i);
			if (i < 0) {
				return false;
			}
		}
		return true;
	}

	public static class Serializer implements ArgumentSerializer<EnumArgument<?>> {

		@Override
		public void toPacket(EnumArgument<?> argument, PacketByteBuf buffer) {
			buffer.writeString(argument.enumClass.getName());
		}

		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public EnumArgument<?> fromPacket(PacketByteBuf buffer) {
			try {
				String name = buffer.readString();
				return new EnumArgument(Class.forName(name));
			} catch (ClassNotFoundException e) {
				return null;
			}
		}

		@Override
		public void toJson(EnumArgument<?> argument, JsonObject json) {
			json.addProperty("enum", argument.enumClass.getName());
		}

	}

}
