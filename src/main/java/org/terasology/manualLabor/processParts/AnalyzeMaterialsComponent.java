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
import org.terasology.logic.inventory.InventoryManager;
import org.terasology.manualLabor.components.AnalyzedMaterialComponent;
import org.terasology.registry.CoreRegistry;
import org.terasology.substanceMatters.components.MaterialCompositionComponent;
import org.terasology.workstation.process.ProcessPart;
import org.terasology.workstation.process.WorkstationInventoryUtils;
import org.terasology.workstation.process.inventory.InventoryInputComponent;
import org.terasology.workstation.process.inventory.InventoryOutputComponent;
import org.terasology.workstation.process.inventory.ValidateInventoryItem;

public class AnalyzeMaterialsComponent implements Component, ProcessPart, ValidateInventoryItem {

    @Override
    public boolean isResponsibleForSlot(EntityRef workstation, int slotNo) {
        return WorkstationInventoryUtils.getAssignedInputSlots(workstation, InventoryInputComponent.WORKSTATIONINPUTCATEGORY).contains(slotNo)
                || WorkstationInventoryUtils.getAssignedOutputSlots(workstation, InventoryOutputComponent.WORKSTATIONOUTPUTCATEGORY).contains(slotNo);
    }

    @Override
    public boolean isValid(EntityRef workstation, int slotNo, EntityRef instigator, EntityRef item) {
        if (WorkstationInventoryUtils.getAssignedInputSlots(workstation, InventoryInputComponent.WORKSTATIONINPUTCATEGORY).contains(slotNo)) {
            return item.hasComponent(MaterialCompositionComponent.class) && !item.hasComponent(AnalyzedMaterialComponent.class);
        } else if (WorkstationInventoryUtils.getAssignedOutputSlots(workstation, InventoryOutputComponent.WORKSTATIONOUTPUTCATEGORY).contains(slotNo)) {
            return item.hasComponent(AnalyzedMaterialComponent.class);
        }

        return false;
    }

    @Override
    public boolean validateBeforeStart(EntityRef instigator, EntityRef workstation, EntityRef processEntity) {
        InventoryManager inventoryManager = CoreRegistry.get(InventoryManager.class);
        for (int inputSlot : WorkstationInventoryUtils.getAssignedInputSlots(workstation, InventoryInputComponent.WORKSTATIONINPUTCATEGORY)) {
            EntityRef inputItem = inventoryManager.getItemInSlot(workstation, inputSlot);
            if (inputItem.exists()) {
                return true;
            }
        }
        return false;
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
        InventoryManager inventoryManager = CoreRegistry.get(InventoryManager.class);

        for (int inputSlot : WorkstationInventoryUtils.getAssignedInputSlots(workstation, InventoryInputComponent.WORKSTATIONINPUTCATEGORY)) {
            EntityRef inputItem = inventoryManager.getItemInSlot(workstation, inputSlot);
            if (inputItem.exists()) {
                if (!inputItem.hasComponent(AnalyzedMaterialComponent.class)) {
                    inputItem.addComponent(new AnalyzedMaterialComponent());
                }
                inventoryManager.moveItemToSlots(instigator, workstation, inputSlot, workstation, WorkstationInventoryUtils.getAssignedOutputSlots(workstation, InventoryOutputComponent.WORKSTATIONOUTPUTCATEGORY));
            }
        }
    }
}
