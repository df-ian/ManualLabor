/*
 * Copyright 2015 MovingBlocks
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

import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.prefab.PrefabManager;
import org.terasology.logic.inventory.InventoryUtils;
import org.terasology.manualLabor.components.MetalSubstanceComponent;
import org.terasology.registry.CoreRegistry;
import org.terasology.substanceMatters.components.MaterialCompositionComponent;
import org.terasology.substanceMatters.components.MaterialItemComponent;
import org.terasology.workstation.process.ProcessPart;
import org.terasology.workstation.process.inventory.InventoryInputProcessPartSlotAmountsComponent;

import java.util.Map;

public class MetalSubstanceRequirementComponent implements Component, ProcessPart {
    @Override
    public boolean validateBeforeStart(EntityRef instigator, EntityRef workstation, EntityRef processEntity) {
        InventoryInputProcessPartSlotAmountsComponent slotAmountsComponent = processEntity.getComponent(InventoryInputProcessPartSlotAmountsComponent.class);
        for (Map.Entry<Integer, Integer> slotAmount : slotAmountsComponent.slotAmounts.entrySet()) {
            EntityRef itemInSlot = InventoryUtils.getItemAt(workstation, slotAmount.getKey());
            MaterialItemComponent materialItemComponent = itemInSlot.getComponent(MaterialItemComponent.class);
            MaterialCompositionComponent materialCompositionComponent = itemInSlot.getComponent(MaterialCompositionComponent.class);
            if (materialItemComponent != null && materialCompositionComponent != null) {
                String primarySubstanceUri = materialCompositionComponent.getPrimarySubstance();
                Prefab primarySubstance = CoreRegistry.get(PrefabManager.class).getPrefab(primarySubstanceUri);
                if (!primarySubstance.hasComponent(MetalSubstanceComponent.class)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public long getDuration(EntityRef instigator, EntityRef workstation, EntityRef processEntity) {
        return 0;
    }

    @Override
    public void executeStart(EntityRef instigator, EntityRef workstation, EntityRef processEntity) {

    }

    @Override
    public void executeEnd(EntityRef instigator, EntityRef workstation, EntityRef processEntity) {

    }
}
