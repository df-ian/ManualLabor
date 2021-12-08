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

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.terasology.engine.entitySystem.entity.EntityBuilder;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.module.inventory.systems.InventoryManager;
import org.terasology.substanceMatters.components.MaterialCompositionComponent;
import org.terasology.substanceMatters.processParts.TransferSubstancesProcessPartCommonSystem;
import org.terasology.workstation.process.WorkstationInventoryUtils;
import org.terasology.workstation.process.inventory.InventoryOutputItemsComponent;
import org.terasology.workstation.process.inventory.InventoryOutputProcessPartCommonSystem;
import org.terasology.workstation.process.inventory.InventoryProcessPartUtils;
import org.terasology.workstation.processPart.ProcessEntityFinishExecutionEvent;
import org.terasology.workstation.processPart.ProcessEntityIsInvalidEvent;
import org.terasology.workstation.processPart.ProcessEntityIsInvalidToStartEvent;
import org.terasology.workstation.processPart.inventory.ProcessEntityIsInvalidForInventoryItemEvent;
import org.terasology.workstation.processPart.metadata.ProcessEntityGetOutputDescriptionEvent;
import org.terasology.workstation.system.WorkstationRegistry;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RegisterSystem
public class SiftedMaterialOutputProcessPartCommonSystem extends BaseComponentSystem {
    @In
    InventoryManager inventoryManager;
    @In
    WorkstationRegistry workstationRegistry;
    @In
    BlockManager blockManager;
    @In
    EntityManager entityManager;

    @ReceiveEvent
    public void validateProcess(ProcessEntityIsInvalidEvent event, EntityRef processEntity,
                                SiftedMaterialOutputComponent siftedMaterialOutputComponent) {

        Set<EntityRef> items = createOutputItems(siftedMaterialOutputComponent, processEntity);
        try {
            if (items.size() == 0) {
                event.addError("No output items specified in " + this.getClass().getSimpleName());
            }
        } catch (Exception ex) {
            event.addError("Could not create output items in " + this.getClass().getSimpleName());
        } finally {
            if (items != null) {
                for (EntityRef outputItem : items) {
                    outputItem.destroy();
                }
            }
        }
    }

    @ReceiveEvent
    public void validateToExecute(ProcessEntityIsInvalidToStartEvent event, EntityRef processEntity,
                                  SiftedMaterialOutputComponent siftedMaterialOutputComponent) {
        Set<EntityRef> outputItems = createOutputItems(siftedMaterialOutputComponent, processEntity);
        if (!InventoryProcessPartUtils.canGiveItemsTo(event.getWorkstation(), outputItems,
                InventoryOutputProcessPartCommonSystem.WORKSTATIONOUTPUTCATEGORY)) {
            event.consume();
        }
    }

    @ReceiveEvent
    public void finish(ProcessEntityFinishExecutionEvent event, EntityRef processEntity,
                       SiftedMaterialOutputComponent siftedMaterialOutputComponent) {
        Set<EntityRef> outputItems = createOutputItems(siftedMaterialOutputComponent, processEntity);
        // allow other systems to post process these items
        processEntity.addComponent(new InventoryOutputItemsComponent(outputItems));
        for (EntityRef outputItem : outputItems) {
            if (!inventoryManager.giveItem(event.getInstigator(), event.getInstigator(), outputItem,
                    WorkstationInventoryUtils.getAssignedOutputSlots(event.getWorkstation(),
                            InventoryOutputProcessPartCommonSystem.WORKSTATIONOUTPUTCATEGORY))) {
                outputItem.destroy();
            }
        }
    }

    @ReceiveEvent
    public void isValidInventoryItem(ProcessEntityIsInvalidForInventoryItemEvent event, EntityRef processEntity,
                                     SiftedMaterialOutputComponent siftedMaterialOutputComponent) {
        // only allow the workstation to put items in the output
        if (WorkstationInventoryUtils.getAssignedOutputSlots(event.getWorkstation(),
                InventoryOutputProcessPartCommonSystem.WORKSTATIONOUTPUTCATEGORY).contains(event.getSlotNo())
                && !event.getInstigator().equals(event.getWorkstation())) {
            event.consume();
        }
    }

    @ReceiveEvent
    public void getOutputDescriptions(ProcessEntityGetOutputDescriptionEvent event, EntityRef processEntity,
                                      SiftedMaterialOutputComponent siftedMaterialOutputComponent) {
        Set<EntityRef> items = Sets.newLinkedHashSet();
        items.add(entityManager.create(siftedMaterialOutputComponent.item));
        items.add(entityManager.create(siftedMaterialOutputComponent.smallItem));
        try {
            for (EntityRef item : items) {
                event.addOutputDescription(InventoryProcessPartUtils.createProcessPartDescription(item));
            }
        } finally {
            for (EntityRef outputItem : items) {
                outputItem.destroy();
            }
        }
    }


    private Set<EntityRef> createOutputItems(SiftedMaterialOutputComponent siftedMaterialOutputComponent, EntityRef processEntity) {
        Set<EntityRef> result = Sets.newHashSet();

        // grab the material composition from the process entity
        MaterialCompositionComponent materialComposition = processEntity.getComponent(MaterialCompositionComponent.class);
        if (materialComposition == null) {
            materialComposition = new MaterialCompositionComponent();
        }

        List<Map.Entry<String, Float>> materialAmounts = materialComposition.getSortedByAmountDesc();

        // get the top two substances that are splittable
        Map<String, Float> splittableSubstances = Maps.newHashMap();
        for (Map.Entry<String, Float> materialAmount : materialAmounts) {
            if (materialAmount.getValue() >= siftedMaterialOutputComponent.minimumSiftableAmount && splittableSubstances.size() < 2) {
                splittableSubstances.put(materialAmount.getKey(), materialAmount.getValue());
            }
        }

        if (splittableSubstances.size() != 2) {
            // just output a single item if there is nothing to split.
            EntityBuilder newItem = entityManager.newBuilder(siftedMaterialOutputComponent.item);
            newItem.setPersistent(processEntity.isPersistent());
            newItem.addComponent(materialComposition);
            TransferSubstancesProcessPartCommonSystem.setDisplayName(newItem, materialComposition);
            result.add(newItem.build());
        } else {
            // grab the substances not going to be split out so that they can be distributed equally
            MaterialCompositionComponent extraSubstances = new MaterialCompositionComponent();
            for (Map.Entry<String, Float> materialAmount : materialAmounts) {
                if (!splittableSubstances.containsKey(materialAmount.getKey())) {
                    extraSubstances.addSubstance(materialAmount.getKey(), materialAmount.getValue() / 2);
                }
            }

            // create the new items
            for (Map.Entry<String, Float> splittableSubstance : splittableSubstances.entrySet()) {
                EntityBuilder newItem;
                if (splittableSubstance.getValue() <= siftedMaterialOutputComponent.smallItemAmount) {
                    newItem = entityManager.newBuilder(siftedMaterialOutputComponent.smallItem);
                } else {
                    newItem = entityManager.newBuilder(siftedMaterialOutputComponent.item);
                }
                newItem.setPersistent(processEntity.isPersistent());
                MaterialCompositionComponent newComposition = extraSubstances.copy();
                newComposition.addSubstance(splittableSubstance.getKey(), splittableSubstance.getValue());

                newItem.addComponent(newComposition);
                TransferSubstancesProcessPartCommonSystem.setDisplayName(newItem, newComposition);
                result.add(newItem.build());
            }
        }

        return result;
    }
}
