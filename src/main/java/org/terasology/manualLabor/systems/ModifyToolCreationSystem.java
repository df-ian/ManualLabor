/*
 * Copyright 2014 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.manualLabor.systems;

import com.google.common.collect.Lists;
import org.terasology.asset.Assets;
import org.terasology.durability.components.DurabilityComponent;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.manualLabor.components.IncreaseToolDurabilityComponent;
import org.terasology.manualLabor.components.MultiplyToolDurabilityComponent;
import org.terasology.manualLabor.events.ModifyToolCreationEvent;
import org.terasology.substanceMatters.SubstanceMattersUtil;
import org.terasology.substanceMatters.components.MaterialCompositionComponent;
import org.terasology.substanceMatters.components.MaterialItemComponent;
import org.terasology.tintOverlay.TintOverlayIconComponent;

import java.util.List;
import java.util.Map;

@RegisterSystem
public class ModifyToolCreationSystem extends BaseComponentSystem {

    /**
     * Mimic any tint overlay items with an input item
     */
    @ReceiveEvent
    public void onToolHasTintOverlayIcon(ModifyToolCreationEvent event, EntityRef toolEntity, TintOverlayIconComponent tintOverlayIconComponent) {
        List<TintOverlayIconComponent.TintParameter> remainingTintParameters = Lists.newArrayList(tintOverlayIconComponent.texture.values());

        // check all the items for an icon match in the tint overlay
        for (EntityRef inputItem : event.getInputItems()) {
            MaterialItemComponent inputItemMaterialItemComponent = inputItem.getComponent(MaterialItemComponent.class);
            MaterialCompositionComponent inputItemMaterialCompositionComponent = inputItem.getComponent(MaterialCompositionComponent.class);

            TintOverlayIconComponent.TintParameter tintParameter = tintOverlayIconComponent.getTintParameterForIcon(inputItemMaterialItemComponent.icon);
            if (tintParameter != null) {
                // change the appearance of this overlay (dont change the offset)
                String substance = inputItemMaterialCompositionComponent.getPrimarySubstance();
                SubstanceMattersUtil.setTintParametersFromSubstance(substance, tintParameter);
                remainingTintParameters.remove(tintParameter);
            }
        }

        // if we did not match up this overlay item to an item, and the hue was previously set, use the overall substance to tint
        for (TintOverlayIconComponent.TintParameter tintParameter : remainingTintParameters) {
            if (tintParameter.hue != null) {
                SubstanceMattersUtil.setTintParametersFromSubstance(toolEntity.getComponent(MaterialCompositionComponent.class).getPrimarySubstance(), tintParameter);
            }
        }

        toolEntity.saveComponent(tintOverlayIconComponent);
    }

    /**
     * Modify the original durability based on the substances used in its creation
     */
    @ReceiveEvent
    public void onModifyToolDurability(ModifyToolCreationEvent event, EntityRef toolEntity, DurabilityComponent durabilityComponent) {
        MaterialCompositionComponent materialCompositionComponent = toolEntity.getComponent(MaterialCompositionComponent.class);

        if (materialCompositionComponent != null) {
            // increase the tool's base durability
            for (Map.Entry<String, Float> substance : materialCompositionComponent.contents.entrySet()) {
                Prefab substancePrefab = Assets.getPrefab(substance.getKey());

                IncreaseToolDurabilityComponent substanceIncrease = substancePrefab.getComponent(IncreaseToolDurabilityComponent.class);
                if (substanceIncrease != null) {
                    durabilityComponent.maxDurability += substanceIncrease.increasePerSubstanceAmount * substance.getValue();
                }
            }

            // multiply the durability
            for (Map.Entry<String, Float> substance : materialCompositionComponent.contents.entrySet()) {
                Prefab substancePrefab = Assets.getPrefab(substance.getKey());

                MultiplyToolDurabilityComponent substanceMultiply = substancePrefab.getComponent(MultiplyToolDurabilityComponent.class);
                if (substanceMultiply != null) {
                    durabilityComponent.maxDurability *= Math.pow(substanceMultiply.multiplyPerSubstanceAmount, substance.getValue());
                }
            }

            // ensure the tool's initial durability is reset
            durabilityComponent.durability = durabilityComponent.maxDurability;

            toolEntity.saveComponent(durabilityComponent);
        }
    }
}
