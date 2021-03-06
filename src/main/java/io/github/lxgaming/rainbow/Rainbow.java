/*
 * Copyright 2018 Alex Thomson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.lxgaming.rainbow;

import com.google.inject.Inject;
import io.github.lxgaming.rainbow.command.RainbowCommand;
import io.github.lxgaming.rainbow.configuration.Config;
import io.github.lxgaming.rainbow.configuration.Configuration;
import io.github.lxgaming.rainbow.data.RainbowData;
import io.github.lxgaming.rainbow.data.RainbowDataBuilder;
import io.github.lxgaming.rainbow.data.RainbowImmutableData;
import io.github.lxgaming.rainbow.listener.PlayerListener;
import io.github.lxgaming.rainbow.manager.CommandManager;
import io.github.lxgaming.rainbow.util.Reference;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameRegistryEvent;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.nio.file.Path;
import java.util.Optional;

@Plugin(
        id = Reference.ID,
        name = Reference.NAME,
        version = Reference.VERSION,
        description = Reference.DESCRIPTION,
        authors = {Reference.AUTHORS},
        url = Reference.WEBSITE
)
public class Rainbow {
    
    private static Rainbow instance;
    
    @Inject
    private PluginContainer pluginContainer;
    
    @Inject
    private Logger logger;
    
    @Inject
    @DefaultConfig(sharedRoot = true)
    private Path path;
    
    private Configuration configuration;
    
    @Listener
    public void onGameConstruction(GameConstructionEvent event) {
        instance = this;
        configuration = new Configuration(getPath());
    }
    
    @Listener
    public void onGamePreInitialization(GamePreInitializationEvent event) {
    }
    
    @Listener
    public void onGameInitialization(GameInitializationEvent event) {
        CommandManager.registerCommand(RainbowCommand.class);
        Sponge.getEventManager().registerListeners(getInstance(), new PlayerListener());
    }
    
    @Listener
    public void onGamePostInitialization(GamePostInitializationEvent event) {
        reloadConfiguration();
    }
    
    @Listener
    public void onKeyRegistration(GameRegistryEvent.Register<Key<?>> event) {
        event.register(RainbowData.UNIQUE_ID_KEY);
        event.register(RainbowData.HUE_KEY);
    }
    
    @Listener
    public void onDataRegistration(GameRegistryEvent.Register<DataRegistration<?, ?>> event) {
        DataRegistration.builder()
                .dataClass(RainbowData.class)
                .immutableClass(RainbowImmutableData.class)
                .builder(new RainbowDataBuilder())
                .name("Rainbow Data")
                .id("rainbow")
                .build();
    }
    
    public boolean reloadConfiguration() {
        getConfiguration().loadConfiguration();
        getConfiguration().saveConfiguration();
        return getConfig().isPresent();
    }
    
    public void debugMessage(String format, Object... arguments) {
        if (getConfig().map(Config::isDebug).orElse(false)) {
            getLogger().info(format, arguments);
        }
    }
    
    public static Rainbow getInstance() {
        return instance;
    }
    
    public PluginContainer getPluginContainer() {
        return pluginContainer;
    }
    
    public Logger getLogger() {
        return logger;
    }
    
    public Path getPath() {
        return path;
    }
    
    public Configuration getConfiguration() {
        return configuration;
    }
    
    public Optional<Config> getConfig() {
        if (getConfiguration() != null) {
            return Optional.ofNullable(getConfiguration().getConfig());
        }
        
        return Optional.empty();
    }
}