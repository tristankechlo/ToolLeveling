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
				Identifier loc = new Identifier(element);
				if (registry.containsId(loc)) {
					tempValues.add(registry.get(loc));
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
