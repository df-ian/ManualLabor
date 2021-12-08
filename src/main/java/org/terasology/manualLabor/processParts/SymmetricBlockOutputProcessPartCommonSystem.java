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

import com.google.common.collect.Sets;
import org.terasology.engine.entitySystem.entity.EntityBuilder;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.block.BlockUri;
import org.terasology.engine.world.block.items.BlockItemComponent;
import org.terasology.engine.world.block.items.BlockItemFactory;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.module.inventory.systems.InventoryManager;
import org.terasology.module.inventory.systems.InventoryUtils;
import org.terasology.workstation.process.WorkstationInventoryUtils;
import org.terasology.workstation.process.inventory.InventoryInputProcessPartSlotAmountsComponent;
import org.terasology.workstation.process.inventory.InventoryOutputItemsComponent;
import org.terasology.workstation.process.inventory.InventoryOutputProcessPartCommonSystem;
import org.terasology.workstation.process.inventory.InventoryProcessPartUtils;
import org.terasology.workstation.processPart.ProcessEntityFinishExecutionEvent;
import org.terasology.workstation.processPart.ProcessEntityIsInvalidEvent;
import org.terasology.workstation.processPart.ProcessEntityIsInvalidToStartEvent;
import org.terasology.workstation.processPart.inventory.ProcessEntityIsInvalidForInventoryItemEvent;
import org.terasology.workstation.processPart.metadata.ProcessEntityGetOutputDescriptionEvent;

import java.util.Map;
import java.util.Set;

@RegisterSystem
public class SymmetricBlockOutputProcessPartCommonSystem extends BaseComponentSystem {
    @In
    InventoryManager inventoryManager;
    @In
    BlockManager blockManager;
    @In
    EntityManager entityManager;

    @ReceiveEvent
    public void validateProcess(ProcessEntityIsInvalidEvent event, EntityRef processEntity,
                                SymmetricBlockOutputComponent symmetricBlockOutputComponent) {

        Set<EntityRef> items = null;
        try {
            items = createOutputItems(null, symmetricBlockOutputComponent, false);
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
    public void validateToStartExecution(ProcessEntityIsInvalidToStartEvent event, EntityRef processEntity,
                                         SymmetricBlockOutputComponent symmetricBlockOutputComponent) {
        SymmetricBlockOutputProcessPartComponent symmetricBlockOutputProcessPartComponent = null;
        InventoryInputProcessPartSlotAmountsComponent slotAmountsComponent = processEntity.getComponent(InventoryInputProcessPartSlotAmountsComponent.class);
        for (Map.Entry<Integer, Integer> slotAmount : slotAmountsComponent.slotAmounts.entrySet()) {
            EntityRef itemInSlot = InventoryUtils.getItemAt(event.getWorkstation(), slotAmount.getKey());
            BlockItemComponent blockItemComponent = itemInSlot.getComponent(BlockItemComponent.class);
            if (blockItemComponent != null) {
                BlockUri sourceBlockUri = blockItemComponent.blockFamily.getURI();
                BlockUri newBlockUri = new BlockUri(new ResourceUrn(sourceBlockUri.getShapelessUri().toString()), new ResourceUrn(symmetricBlockOutputComponent.shape));
                symmetricBlockOutputProcessPartComponent = new SymmetricBlockOutputProcessPartComponent();
                symmetricBlockOutputProcessPartComponent.blockFamily = blockManager.getBlockFamily(newBlockUri);
                processEntity.addComponent(symmetricBlockOutputProcessPartComponent);
                break;
            }
        }

        Set<EntityRef> outputItems = createOutputItems(symmetricBlockOutputProcessPartComponent, symmetricBlockOutputComponent, false);


        if (symmetricBlockOutputProcessPartComponent == null
                || !InventoryProcessPartUtils.canGiveItemsTo(event.getWorkstation(), outputItems, InventoryOutputProcessPartCommonSystem.WORKSTATIONOUTPUTCATEGORY)) {
            event.consume();
        }
    }

    @ReceiveEvent
    public void finish(ProcessEntityFinishExecutionEvent event, EntityRef processEntity,
                       SymmetricBlockOutputComponent symmetricBlockOutputComponent,
                       SymmetricBlockOutputProcessPartComponent symmetricBlockOutputProcessPartComponent) {
        Set<EntityRef> outputItems = createOutputItems(symmetricBlockOutputProcessPartComponent, symmetricBlockOutputComponent, true);
        // allow other systems to post process these items
        processEntity.addComponent(new InventoryOutputItemsComponent(outputItems));
        for (EntityRef outputItem : outputItems) {
            if (!inventoryManager.giveItem(event.getWorkstation(), event.getInstigator(), outputItem, WorkstationInventoryUtils.getAssignedOutputSlots(event.getWorkstation(), InventoryOutputProcessPartCommonSystem.WORKSTATIONOUTPUTCATEGORY))) {
                outputItem.destroy();
            }
        }
    }

    @ReceiveEvent
    public void isValidInventoryItem(ProcessEntityIsInvalidForInventoryItemEvent event, EntityRef processEntity,
                                     SymmetricBlockOutputComponent symmetricBlockOutputComponent) {
        // only allow the workstation to put items in the output
        if (WorkstationInventoryUtils.getAssignedOutputSlots(event.getWorkstation(), InventoryOutputProcessPartCommonSystem.WORKSTATIONOUTPUTCATEGORY).contains(event.getSlotNo())
                && !event.getInstigator().equals(event.getWorkstation())) {
            event.consume();
        }
    }

    @ReceiveEvent
    public void getOutputDescriptions(ProcessEntityGetOutputDescriptionEvent event, EntityRef processEntity,
                                      SymmetricBlockOutputComponent symmetricBlockOutputComponent) {
        Set<EntityRef> items = createOutputItems(null, symmetricBlockOutputComponent, true);
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

    protected Set<EntityRef> createOutputItems(SymmetricBlockOutputProcessPartComponent symmetricBlockOutputProcessPartComponent, SymmetricBlockOutputComponent symmetricBlockOutputComponent, boolean createPersistentEntities) {
        BlockItemFactory blockFactory = new BlockItemFactory(entityManager);
        Set<EntityRef> output = Sets.newHashSet();
        if (symmetricBlockOutputProcessPartComponent != null) {
            EntityBuilder entityBuilder = blockFactory.newBuilder(symmetricBlockOutputProcessPartComponent.blockFamily, symmetricBlockOutputComponent.amount);
            entityBuilder.setPersistent(createPersistentEntities);
            output.add(entityBuilder.build());
        } else {
            BlockUri newBlockUri = new BlockUri(new ResourceUrn("ManualLabor:TempBlock"), new ResourceUrn(symmetricBlockOutputComponent.shape));
            EntityBuilder entityBuilder = blockFactory.newBuilder(blockManager.getBlockFamily(newBlockUri), symmetricBlockOutputComponent.amount);
            entityBuilder.setPersistent(createPersistentEntities);
            output.add(entityBuilder.build());
        }
        return output;
    }
}
