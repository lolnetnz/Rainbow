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

package io.github.lxgaming.rainbow.configuration;

import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import io.github.lxgaming.rainbow.Rainbow;

import java.io.IOException;
import java.nio.file.Path;

public class Configuration {
    
    private ConfigurationLoader<CommentedConfigurationNode> configurationLoader;
    private ObjectMapper<Config>.BoundInstance objectMapper;
    private CommentedConfigurationNode configurationNode;
    private Config config;
    
    public Configuration(Path path) {
        try {
            this.configurationLoader = HoconConfigurationLoader.builder().setPath(path).build();
            this.objectMapper = ObjectMapper.forClass(Config.class).bindToNew();
        } catch (Exception ex) {
            Rainbow.getInstance().getLogger().error("Encountered an error initializing {}", getClass().getSimpleName(), ex);
        }
    }
    
    public void loadConfiguration() {
        try {
            configurationNode = getConfigurationLoader().load(ConfigurationOptions.defaults());
            config = getObjectMapper().populate(getConfigurationNode());
            Rainbow.getInstance().getLogger().info("Successfully loaded configuration file.");
        } catch (IOException | ObjectMappingException | RuntimeException ex) {
            configurationNode = getConfigurationLoader().createEmptyNode(ConfigurationOptions.defaults());
            Rainbow.getInstance().getLogger().error("Encountered an error processing {}::loadConfiguration", getClass().getSimpleName(), ex);
        }
    }
    
    public void saveConfiguration() {
        try {
            getObjectMapper().serialize(getConfigurationNode());
            getConfigurationLoader().save(getConfigurationNode());
            Rainbow.getInstance().getLogger().info("Successfully saved configuration file.");
        } catch (IOException | ObjectMappingException | RuntimeException ex) {
            Rainbow.getInstance().getLogger().error("Encountered an error processing {}::saveConfiguration", getClass().getSimpleName(), ex);
        }
    }
    
    private ConfigurationLoader<CommentedConfigurationNode> getConfigurationLoader() {
        return configurationLoader;
    }
    
    private ObjectMapper<Config>.BoundInstance getObjectMapper() {
        return objectMapper;
    }
    
    private CommentedConfigurationNode getConfigurationNode() {
        return configurationNode;
    }
    
    public Config getConfig() {
        return config;
    }
}