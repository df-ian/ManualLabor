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
package org.terasology.manualLabor.processParts;

import com.google.common.collect.Lists;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.inventory.InventoryUtils;
import org.terasology.manualLabor.components.ToolItemOutputUsedItemsComponent;
import org.terasology.manualLabor.events.ModifyToolCreationEvent;
import org.terasology.registry.CoreRegistry;
import org.terasology.substanceMatters.SubstanceMattersUtil;
import org.terasology.substanceMatters.components.MaterialCompositionComponent;
import org.terasology.substanceMatters.components.MaterialItemComponent;
import org.terasology.tintOverlay.TintOverlayIconComponent;
import org.terasology.workstation.process.inventory.InventoryInputProcessPartSlotAmountsComponent;
import org.terasology.workstation.process.inventory.InventoryOutputComponent;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ToolItemOutputComponent extends InventoryOutputComponent {
    public String item;

    @Override
    public boolean validateBeforeStart(EntityRef instigator, EntityRef workstation, EntityRef processEntity) {
        boolean isValid = super.validateBeforeStart(instigator, workstation, processEntity);
        if (isValid) {
            ToolItemOutputUsedItemsComponent toolItemOutputUsedItemsComponent = new ToolItemOutputUsedItemsComponent();
            InventoryInputProcessPartSlotAmountsComponent slotAmountsComponent = processEntity.getComponent(InventoryInputProcessPartSlotAmountsComponent.class);
            for (Map.Entry<Integer, Integer> slotAmount : slotAmountsComponent.slotAmounts.entrySet()) {
                EntityRef itemInSlot = InventoryUtils.getItemAt(workstation, slotAmount.getKey());
                MaterialItemComponent materialItemComponent = itemInSlot.getComponent(MaterialItemComponent.class);
                MaterialCompositionComponent materialCompositionComponent = itemInSlot.getComponent(MaterialCompositionComponent.class);
                if (materialItemComponent != null && materialCompositionComponent != null) {
                    toolItemOutputUsedItemsComponent.iconSubstanceMap.put(materialItemComponent.icon, materialCompositionComponent.getPrimarySubstance());
                }
            }

            processEntity.addComponent(toolItemOutputUsedItemsComponent);
        }

        return isValid;
    }

    @Override
    protected Set<EntityRef> createOutputItems(EntityRef processEntity) {
        EntityManager entityManager = CoreRegistry.get(EntityManager.class);

        Set<EntityRef> result = new HashSet<>();
        EntityRef toolEntity = entityManager.create(item);

        // add the composition of this tool
        MaterialCompositionComponent materialCompositionComponent = processEntity.getComponent(MaterialCompositionComponent.class);
        if (materialCompositionComponent != null) {
            toolEntity.addComponent(materialCompositionComponent);
        }

        // use the items used to tweak the icon
        ToolItemOutputUsedItemsComponent toolItemOutputUsedItemsComponent = processEntity.getComponent(ToolItemOutputUsedItemsComponent.class);
        if (toolItemOutputUsedItemsComponent != null) {
            tweakToolEntityIcon(toolEntity, toolItemOutputUsedItemsComponent);
        }

        // allow tweaking of this tool based on its attributes
        toolEntity.send(new ModifyToolCreationEvent());

        result.add(toolEntity);
        return result;
    }


    /**
     * Mimic any tint overlay items with an input item
     */
    public void tweakToolEntityIcon(EntityRef toolEntity, ToolItemOutputUsedItemsComponent toolItemOutputUsedItemsComponent) {
        TintOverlayIconComponent tintOverlayIconComponent = toolEntity.getComponent(TintOverlayIconComponent.class);
        List<TintOverlayIconComponent.TintParameter> remainingTintParameters = Lists.newArrayList(tintOverlayIconComponent.texture.values());

        // check all the items for an icon match in the tint overlay
        for (Map.Entry<String, String> iconSubstanceItem : toolItemOutputUsedItemsComponent.iconSubstanceMap.entrySet()) {
            TintOverlayIconComponent.TintParameter tintParameter = tintOverlayIconComponent.getTintParameterForIcon(iconSubstanceItem.getKey());
            if (tintParameter != null) {
                // change the appearance of this overlay (dont change the offset)
                SubstanceMattersUtil.setTintParametersFromSubstance(iconSubstanceItem.getValue(), tintParameter);
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
}
