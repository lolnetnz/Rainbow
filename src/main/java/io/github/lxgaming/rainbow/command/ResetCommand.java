/*
 * Copyright 2019 Alex Thomson
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

package io.github.lxgaming.rainbow.command;

import io.github.lxgaming.rainbow.manager.RainbowManager;
import io.github.lxgaming.rainbow.util.Toolbox;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Arrays;
import java.util.List;

public class ResetCommand extends AbstractCommand {
    
    public ResetCommand() {
        addAlias("reset");
        setPermission("rainbow.command.reset");
    }
    
    @Override
    public CommandResult execute(CommandSource commandSource, List<String> arguments) {
        if (!(commandSource instanceof Player)) {
            commandSource.sendMessage(Text.of(Toolbox.getTextPrefix(), TextColors.RED, "This command cannot be run by Console"));
            return CommandResult.empty();
        }
        
        Player player = (Player) commandSource;
        RainbowManager.getTracking().computeIfPresent(player.getUniqueId(), (key, value) -> {
            Arrays.fill(value, 0.0F);
            return value;
        });
        
        player.sendMessage(Text.of(Toolbox.getTextPrefix(), TextColors.GREEN, "Rainbow Armor reset"));
        return CommandResult.success();
    }
}