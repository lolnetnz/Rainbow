/*
 * Copyright 2018 lolnet.co.nz
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

package nz.co.lolnet.rainbow.managers;

import nz.co.lolnet.rainbow.Rainbow;
import nz.co.lolnet.rainbow.configuration.Config;
import nz.co.lolnet.rainbow.data.RainbowData;
import nz.co.lolnet.rainbow.util.HSLColor;
import nz.co.lolnet.rainbow.util.Toolbox;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Color;

import java.util.Optional;
import java.util.Set;

public final class RainbowManager {
    
    private static final Color DEFAULT_COLOR = Color.ofRgb(10511680);
    private static final Set<ItemType> VALID_ITEM_TYPES = Toolbox.newHashSet(ItemTypes.LEATHER_HELMET, ItemTypes.LEATHER_CHESTPLATE, ItemTypes.LEATHER_LEGGINGS, ItemTypes.LEATHER_BOOTS);
    
    public static ItemStack createItem(Player player, ItemType itemType) {
        ItemStack itemStack = ItemStack.of(itemType, 1);
        itemStack.offer(new RainbowData(player.getUniqueId()));
        return itemStack;
    }
    
    public static void updateItem(Player player, ItemStack itemStack) {
        if (!getValidItemTypes().contains(itemStack.getType())) {
            return;
        }
        
        Optional<RainbowData> rainbowData = itemStack.get(RainbowData.class);
        if (!rainbowData.isPresent()) {
            return;
        }
        
        if (!player.getUniqueId().equals(rainbowData.get().getUniqueId())) {
            resetItem(itemStack);
        }
        
        Optional<Float> rainbowSpeed = getRainbowSpeed(player);
        if (rainbowSpeed.isPresent() && rainbowSpeed.get() > 0.0F) {
            Color color = itemStack.get(Keys.COLOR).orElse(getDefaultColor());
            HSLColor hslColor = HSLColor.of(color.asJavaColor());
            itemStack.offer(Keys.COLOR, Color.of(hslColor.getIncremented(rainbowSpeed.get())));
        }
    }
    
    public static void resetItem(ItemStack itemStack) {
        if (!getValidItemTypes().contains(itemStack.getType())) {
            return;
        }
        
        itemStack.offer(Keys.COLOR, getDefaultColor());
    }
    
    private static Optional<Float> getRainbowSpeed(Player player) {
        Optional<String> option = Toolbox.getOptionFromSubject(player, "rainbow-speed");
        if (option.isPresent()) {
            Optional<Float> rainbowSpeed = option.flatMap(Toolbox::parseFloat);
            if (rainbowSpeed.isPresent()) {
                return rainbowSpeed;
            }
            
            Rainbow.getInstance().getLogger().warn("Failed to parse rainbow speed for {} ({})", player.getName(), player.getUniqueId());
        }
        
        return Rainbow.getInstance().getConfig().map(Config::getDefaultRainbowSpeed);
    }
    
    public static Color getDefaultColor() {
        return DEFAULT_COLOR;
    }
    
    public static Set<ItemType> getValidItemTypes() {
        return VALID_ITEM_TYPES;
    }
}