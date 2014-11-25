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

import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.manualLabor.events.ModifyToolCreationEvent;
import org.terasology.registry.CoreRegistry;
import org.terasology.substanceMatters.components.MaterialCompositionComponent;
import org.terasology.workstation.process.inventory.InventoryInputComponent;
import org.terasology.workstation.process.inventory.InventoryOutputComponent;

import java.util.HashSet;
import java.util.Set;

public class ToolItemOutputComponent extends InventoryOutputComponent {
    public String item;

    @Override
    protected Set<EntityRef> createOutputItems(EntityRef processEntity) {
        EntityManager entityManager = CoreRegistry.get(EntityManager.class);

        Set<EntityRef> result = new HashSet<>();
        EntityRef toolEntity = entityManager.create(item);


        InventoryInputComponent.InventoryInputProcessPartItemsComponent inputItemsContainer = processEntity.getComponent(InventoryInputComponent.InventoryInputProcessPartItemsComponent.class);
        if (inputItemsContainer != null) {
            // add the composition of this tool
            toolEntity.addComponent(new MaterialCompositionComponent(inputItemsContainer.items));

            toolEntity.send(new ModifyToolCreationEvent(inputItemsContainer.items));
        }

        result.add(toolEntity);
        return result;
    }

}
