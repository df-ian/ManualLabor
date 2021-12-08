/*
 * Copyright 2016 MovingBlocks
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

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.In;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.manualLabor.components.AnalyzedMaterialComponent;
import org.terasology.module.inventory.systems.InventoryManager;
import org.terasology.substanceMatters.components.MaterialCompositionComponent;
import org.terasology.workstation.process.WorkstationInventoryUtils;
import org.terasology.workstation.process.inventory.InventoryInputProcessPartCommonSystem;
import org.terasology.workstation.process.inventory.InventoryOutputProcessPartCommonSystem;
import org.terasology.workstation.processPart.ProcessEntityFinishExecutionEvent;
import org.terasology.workstation.processPart.ProcessEntityIsInvalidToStartEvent;
import org.terasology.workstation.processPart.inventory.ProcessEntityIsInvalidForInventoryItemEvent;

@RegisterSystem
public class AnalyzeMaterialsProcessPartCommonSystem extends BaseComponentSystem {
    @In
    InventoryManager inventoryManager;

    ///// Processing

    @ReceiveEvent
    public void validateToStartExecution(ProcessEntityIsInvalidToStartEvent event, EntityRef processEntity,
                                         AnalyzeMaterialsComponent analyzeMaterialsComponent) {
        for (int inputSlot : WorkstationInventoryUtils.getAssignedInputSlots(event.getWorkstation(),
                InventoryInputProcessPartCommonSystem.WORKSTATIONINPUTCATEGORY)) {
            EntityRef inputItem = inventoryManager.getItemInSlot(event.getWorkstation(), inputSlot);
            if (inputItem.exists()) {
                return;
            }
        }
        event.consume();
    }

    @ReceiveEvent
    public void finishExecution(ProcessEntityFinishExecutionEvent event, EntityRef entityRef,
                                AnalyzeMaterialsComponent analyzeMaterialsComponent) {
        for (int inputSlot : WorkstationInventoryUtils.getAssignedInputSlots(event.getWorkstation(),
                InventoryInputProcessPartCommonSystem.WORKSTATIONINPUTCATEGORY)) {
            EntityRef inputItem = inventoryManager.getItemInSlot(event.getWorkstation(), inputSlot);
            if (inputItem.exists()) {
                if (!inputItem.hasComponent(AnalyzedMaterialComponent.class)) {
                    inputItem.addComponent(new AnalyzedMaterialComponent());
                }
                inventoryManager.moveItemToSlots(event.getInstigator(), event.getWorkstation(), inputSlot, event.getWorkstation(),
                        WorkstationInventoryUtils.getAssignedOutputSlots(event.getWorkstation(),
                                InventoryOutputProcessPartCommonSystem.WORKSTATIONOUTPUTCATEGORY));
            }
        }
    }

    ///// Inventory

    @ReceiveEvent
    public void isValidInventoryItem(ProcessEntityIsInvalidForInventoryItemEvent event, EntityRef processEntity,
                                     AnalyzeMaterialsComponent analyzeMaterialsComponent) {
        if (WorkstationInventoryUtils.getAssignedInputSlots(event.getWorkstation(),
                InventoryInputProcessPartCommonSystem.WORKSTATIONINPUTCATEGORY).contains(event.getSlotNo())
                || WorkstationInventoryUtils.getAssignedOutputSlots(event.getWorkstation(),
                InventoryOutputProcessPartCommonSystem.WORKSTATIONOUTPUTCATEGORY).contains(event.getSlotNo())) {
            // let analyzed items go to the output,  and un-analyzed go to the input
            if (WorkstationInventoryUtils.getAssignedInputSlots(event.getWorkstation(),
                    InventoryInputProcessPartCommonSystem.WORKSTATIONINPUTCATEGORY).contains(event.getSlotNo())) {
                if (event.getItem().hasComponent(MaterialCompositionComponent.class)
                        && !event.getItem().hasComponent(AnalyzedMaterialComponent.class)) {
                    return;
                }
            } else if (WorkstationInventoryUtils.getAssignedOutputSlots(event.getWorkstation(),
                    InventoryOutputProcessPartCommonSystem.WORKSTATIONOUTPUTCATEGORY).contains(event.getSlotNo())) {
                if (event.getItem().hasComponent(AnalyzedMaterialComponent.class)) {
                    return;
                }
            }

            event.consume();
        }
    }
}
