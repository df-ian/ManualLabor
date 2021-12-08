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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.terasology.engine.entitySystem.entity.EntityBuilder;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.manualLabor.events.ModifyToolCreationEvent;
import org.terasology.module.inventory.systems.InventoryManager;
import org.terasology.substanceMatters.SubstanceMattersUtil;
import org.terasology.substanceMatters.components.MaterialCompositionComponent;
import org.terasology.substanceMatters.components.MaterialItemComponent;
import org.terasology.tintOverlay.TintOverlayIconComponent;
import org.terasology.workstation.process.WorkstationInventoryUtils;
import org.terasology.workstation.process.inventory.InventoryInputItemsComponent;
import org.terasology.workstation.process.inventory.InventoryOutputItemsComponent;
import org.terasology.workstation.process.inventory.InventoryOutputProcessPartCommonSystem;
import org.terasology.workstation.process.inventory.InventoryProcessPartUtils;
import org.terasology.workstation.processPart.ProcessEntityFinishExecutionEvent;
import org.terasology.workstation.processPart.ProcessEntityIsInvalidEvent;
import org.terasology.workstation.processPart.ProcessEntityIsInvalidToStartEvent;
import org.terasology.workstation.processPart.inventory.ProcessEntityIsInvalidForInventoryItemEvent;
import org.terasology.workstation.processPart.metadata.ProcessEntityGetOutputDescriptionEvent;
import org.terasology.workstation.system.WorkstationRegistry;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RegisterSystem
public class ToolItemOutputProcessPartCommonSystem extends BaseComponentSystem {
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
                                ToolItemOutputComponent toolItemOutputComponent) {

        Set<EntityRef> items = null;
        try {
            items = createOutputItems(processEntity, toolItemOutputComponent);
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
                                         ToolItemOutputComponent toolItemOutputComponent) {
        Set<EntityRef> outputItems = createOutputItems(processEntity, toolItemOutputComponent);
        if (!InventoryProcessPartUtils.canGiveItemsTo(event.getWorkstation(), outputItems,
                InventoryOutputProcessPartCommonSystem.WORKSTATIONOUTPUTCATEGORY)) {
            event.consume();
        }
    }

    @ReceiveEvent
    public void finish(ProcessEntityFinishExecutionEvent event, EntityRef processEntity,
                       ToolItemOutputComponent toolItemOutputComponent) {
        Set<EntityRef> outputItems = createOutputItems(processEntity, toolItemOutputComponent);
        // allow other systems to post process these items
        processEntity.addComponent(new InventoryOutputItemsComponent(outputItems));
        for (EntityRef outputItem : outputItems) {
            if (!inventoryManager.giveItem(event.getWorkstation(), event.getInstigator(), outputItem,
                    WorkstationInventoryUtils.getAssignedOutputSlots(event.getWorkstation(),
                            InventoryOutputProcessPartCommonSystem.WORKSTATIONOUTPUTCATEGORY))) {
                outputItem.destroy();
            }
        }
    }

    @ReceiveEvent
    public void isValidInventoryItem(ProcessEntityIsInvalidForInventoryItemEvent event, EntityRef processEntity,
                                     ToolItemOutputComponent toolItemOutputComponent) {
        // only allow the workstation to put items in the output
        if (WorkstationInventoryUtils.getAssignedOutputSlots(event.getWorkstation(),
                InventoryOutputProcessPartCommonSystem.WORKSTATIONOUTPUTCATEGORY).contains(event.getSlotNo())
                && !event.getInstigator().equals(event.getWorkstation())) {
            event.consume();
        }
    }

    @ReceiveEvent
    public void getOutputDescriptions(ProcessEntityGetOutputDescriptionEvent event, EntityRef processEntity,
                                      ToolItemOutputComponent toolItemOutputComponent) {
        Set<EntityRef> items = createOutputItems(processEntity, toolItemOutputComponent);
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

    private Set<EntityRef> createOutputItems(EntityRef processEntity, ToolItemOutputComponent toolItemOutputComponent) {

        Set<EntityRef> result = new HashSet<>();
        EntityBuilder entityBuilder = entityManager.newBuilder(toolItemOutputComponent.item);
        entityBuilder.setPersistent(processEntity.isPersistent());

        // add the composition of this tool
        MaterialCompositionComponent materialCompositionComponent = processEntity.getComponent(MaterialCompositionComponent.class);
        if (materialCompositionComponent != null) {
            entityBuilder.addComponent(materialCompositionComponent);
        }

        EntityRef toolEntity = entityBuilder.build();

        // use the items used to tweak the icon
        Map<String, String> iconSubstanceMap = Maps.newHashMap();
        InventoryInputItemsComponent inventoryInputItemsComponent = processEntity.getComponent(InventoryInputItemsComponent.class);
        if (inventoryInputItemsComponent != null) {
            for (EntityRef itemInSlot : inventoryInputItemsComponent.items) {
                MaterialItemComponent materialItemComponent = itemInSlot.getComponent(MaterialItemComponent.class);
                MaterialCompositionComponent itemMaterialCompositionComponent = itemInSlot.getComponent(MaterialCompositionComponent.class);
                if (materialItemComponent != null && itemMaterialCompositionComponent != null) {
                    iconSubstanceMap.put(materialItemComponent.icon, itemMaterialCompositionComponent.getPrimarySubstance());
                }
            }
            tweakToolEntityIcon(toolEntity, iconSubstanceMap);
        }

        // allow tweaking of this tool based on its attributes
        toolEntity.send(new ModifyToolCreationEvent());

        result.add(toolEntity);
        return result;
    }


    /**
     * Mimic any tint overlay items with an input item
     */
    public void tweakToolEntityIcon(EntityRef toolEntity, Map<String, String> iconSubstanceMap) {
        TintOverlayIconComponent tintOverlayIconComponent = toolEntity.getComponent(TintOverlayIconComponent.class);
        List<TintOverlayIconComponent.TintParameter> remainingTintParameters =
                Lists.newArrayList(tintOverlayIconComponent.texture.values());

        // check all the items for an icon match in the tint overlay
        for (Map.Entry<String, String> iconSubstanceItem : iconSubstanceMap.entrySet()) {
            TintOverlayIconComponent.TintParameter tintParameter =
                    tintOverlayIconComponent.getTintParameterForIcon(iconSubstanceItem.getKey());
            if (tintParameter != null) {
                // change the appearance of this overlay (dont change the offset)
                SubstanceMattersUtil.setTintParametersFromSubstance(iconSubstanceItem.getValue(), tintParameter);
                remainingTintParameters.remove(tintParameter);
            }
        }

        // if we did not match up this overlay item to an item, and the hue was previously set, use the overall substance to tint
        for (TintOverlayIconComponent.TintParameter tintParameter : remainingTintParameters) {
            MaterialCompositionComponent materialCompositionComponent = toolEntity.getComponent(MaterialCompositionComponent.class);
            if (tintParameter.hue != null && materialCompositionComponent != null) {
                SubstanceMattersUtil.setTintParametersFromSubstance(materialCompositionComponent.getPrimarySubstance(), tintParameter);
            }
        }

        toolEntity.saveComponent(tintOverlayIconComponent);
    }
}
