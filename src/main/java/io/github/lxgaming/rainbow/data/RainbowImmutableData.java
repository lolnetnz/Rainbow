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

package io.github.lxgaming.rainbow.data;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.value.immutable.ImmutableBoundedValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

import java.util.UUID;

public class RainbowImmutableData extends AbstractImmutableData<RainbowImmutableData, RainbowData> {
    
    private final UUID uniqueId;
    private final float hue;
    
    RainbowImmutableData(UUID uniqueId, float hue) {
        this.uniqueId = uniqueId;
        this.hue = hue;
        registerGetters();
    }
    
    @Override
    protected void registerGetters() {
        registerFieldGetter(RainbowData.UNIQUE_ID_KEY, this::getUniqueId);
        registerKeyValue(RainbowData.UNIQUE_ID_KEY, this::uniqueId);
        
        registerFieldGetter(RainbowData.HUE_KEY, this::getHue);
        registerKeyValue(RainbowData.HUE_KEY, this::hue);
    }
    
    @Override
    public RainbowData asMutable() {
        return new RainbowData(getUniqueId(), getHue());
    }
    
    @Override
    public int getContentVersion() {
        return 1;
    }
    
    @Override
    public DataContainer toContainer() {
        DataContainer dataContainer = super.toContainer();
        dataContainer.set(RainbowData.UNIQUE_ID_KEY, getUniqueId());
        dataContainer.set(RainbowData.HUE_KEY, getHue());
        return dataContainer;
    }
    
    public UUID getUniqueId() {
        return uniqueId;
    }
    
    private ImmutableValue<UUID> uniqueId() {
        return Sponge.getRegistry().getValueFactory().createValue(RainbowData.UNIQUE_ID_KEY, getUniqueId()).asImmutable();
    }
    
    public float getHue() {
        return hue;
    }
    
    private ImmutableBoundedValue<Float> hue() {
        return Sponge.getRegistry().getValueFactory().createBoundedValueBuilder(RainbowData.HUE_KEY)
                .actualValue(getHue())
                .defaultValue(0.0F)
                .maximum(360.0F)
                .minimum(0.0F)
                .build().asImmutable();
    }
}