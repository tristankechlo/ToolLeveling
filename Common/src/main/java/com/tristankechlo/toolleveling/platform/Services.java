package com.tristankechlo.toolleveling.platform;

import com.tristankechlo.toolleveling.ToolLeveling;

import java.util.ServiceLoader;

public final class Services {

    public static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        ToolLeveling.LOGGER.info("Loaded {} for service {}", loadedService.getClass().getSimpleName(), clazz.getSimpleName());
        return loadedService;
    }

}
