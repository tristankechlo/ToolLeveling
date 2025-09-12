package com.tristankechlo.toolleveling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;

public class ToolLeveling {

    public static final String MOD_ID = "toolleveling";
    public static final String MOD_NAME = "Tool Leveling";
    public static final String TABLE = "tool_leveling_table";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }

}
