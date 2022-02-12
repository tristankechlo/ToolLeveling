package com.tristankechlo.toolleveling.config.values;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.tristankechlo.toolleveling.ToolLeveling;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RegistryConfig<T> extends AbstractConfigValue<ImmutableList<T>> {

	private final ImmutableList<T> defaultValues;
	private ImmutableList<T> values;
	private List<String> rawValues = new ArrayList<>();
	private final Type type = new TypeToken<List<String>>() {}.getType();
	private final Gson GSON = new Gson();
	private final Registry<T> registry;

	public RegistryConfig(String identifier, Registry<T> registry, List<T> defaultValues) {
		super(identifier);
		if (registry == null) {
			throw new NullPointerException("registry of the config value can't be null");
		}
		if (defaultValues == null) {
			throw new NullPointerException("defaultValues of the config value can't be null");
		}
		this.registry = registry;
		this.defaultValues = ImmutableList.copyOf(defaultValues);

		for (T arg : defaultValues) {
			rawValues.add(registry.getId(arg).toString());
		}
		values = ImmutableList.copyOf(defaultValues);
	}

	@Override
	public void setToDefault() {
		values = ImmutableList.copyOf(defaultValues);
	}

	@Override
	public ImmutableList<T> getValue() {
		return values;
	}

	@Override
	public void serialize(JsonObject jsonObject) {
		List<String> tempValues = getValue().stream().map((element) -> {
			return registry.getId(element).toString();
		}).collect(Collectors.toList());
		JsonElement jsonElement = GSON.toJsonTree(tempValues, type);
		jsonObject.add(getIdentifier(), jsonElement);
	}

	@Override
	public void deserialize(JsonObject jsonObject) {
		try {
			JsonElement jsonElement = jsonObject.get(getIdentifier());
			if (jsonElement == null) {
				this.setToDefault();
				ToolLeveling.LOGGER.warn(
						"Error while loading the config value for " + getIdentifier() + ", using defaultvalues instead");
				return;
			}
			rawValues = GSON.fromJson(jsonElement, type);
			if (rawValues == null) {
				this.setToDefault();
				ToolLeveling.LOGGER.warn(
						"Error while loading the config value for " + getIdentifier() + ", using defaultvalues instead");
				return;
			}
			List<T> tempValues = new ArrayList<>();
			List<String> modids = new ArrayList<>();
			for (String element : rawValues) {
				Identifier loc = Identifier.tryParse(element);
				if (loc != null && registry.containsId(loc)) {
					tempValues.add(registry.get(loc));
				} else {
					String modid = getModidFromWildcard(element);
					if (modid != null) {
						ToolLeveling.LOGGER
								.info("Found wildcard for mod: '" + modid + "' in '" + getIdentifier() + "'");
						modids.add(modid);
					}
				}
			}
			addAllWildcards(tempValues, modids, registry);
			values = ImmutableList.copyOf(tempValues);
		} catch (Exception e) {
			values = ImmutableList.copyOf(defaultValues);
			ToolLeveling.LOGGER
					.warn("Error while loading the config value " + getIdentifier() + ", using defaultvalue instead");
		}
	}

	private static <T> void addAllWildcards(List<T> tempValues, List<String> modids, final Registry<T> registry) {
		if (modids.isEmpty()) {
			return;
		}
		registry.stream().filter((element) -> {
			return modids.contains(registry.getId(element).getNamespace());
		}).forEach((element) -> tempValues.add(element));
	}

	private static String getModidFromWildcard(String element) {
		if (element.contains(":")) {
			String[] splitted = element.split(":");
			if (splitted[1].equals("*") && FabricLoader.getInstance().isModLoaded(splitted[0])) {
				return splitted[0];
			}
		}
		return null;
	}

}
