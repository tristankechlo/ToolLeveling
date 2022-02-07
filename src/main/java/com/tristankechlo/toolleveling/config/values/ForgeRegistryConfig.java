package com.tristankechlo.toolleveling.config.values;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.tristankechlo.toolleveling.ToolLeveling;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class ForgeRegistryConfig<T extends IForgeRegistryEntry<T>> extends AbstractConfigValue<ImmutableList<T>> {

	private final ImmutableList<T> defaultValues;
	private ImmutableList<T> values;
	private List<String> rawValues = new ArrayList<>();
	private final Type type = new TypeToken<List<String>>() {}.getType();
	private final Gson GSON = new Gson();
	private final IForgeRegistry<T> registry;

	public ForgeRegistryConfig(String identifier, IForgeRegistry<T> registry, List<T> defaultValues) {
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
			rawValues.add(arg.getRegistryName().toString());
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
		JsonElement jsonElement = GSON.toJsonTree(rawValues, type);
		jsonObject.add(getIdentifier(), jsonElement);
	}

	@Override
	public void deserialize(JsonObject jsonObject) {
		try {
			rawValues = GSON.fromJson(jsonObject.get(getIdentifier()), type);
			if (rawValues == null) {
				rawValues = new ArrayList<>();
			}
			List<T> tempValues = new ArrayList<>();
			for (String element : rawValues) {
				ResourceLocation loc = new ResourceLocation(element);
				if (registry.containsKey(loc)) {
					tempValues.add(registry.getValue(loc));
				}
			}
			values = ImmutableList.copyOf(tempValues);
		} catch (Exception e) {
			values = ImmutableList.copyOf(defaultValues);
			ToolLeveling.LOGGER
					.warn("Error while loading the config value " + getIdentifier() + ", using defaultvalue instead");
		}
	}

}
