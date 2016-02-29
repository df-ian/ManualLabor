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
package org.terasology.irlCorp.processParts;

import com.google.common.collect.Sets;
import org.terasology.assets.ResourceUrn;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.inventory.InventoryUtils;
import org.terasology.registry.CoreRegistry;
import org.terasology.workstation.process.inventory.InventoryInputProcessPartSlotAmountsComponent;
import org.terasology.workstation.process.inventory.InventoryOutputComponent;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.BlockUri;
import org.terasology.world.block.items.BlockItemComponent;
import org.terasology.world.block.items.BlockItemFactory;

import java.util.Map;
import java.util.Set;

public class SymmetricBlockOutputComponent extends InventoryOutputComponent {
    public String shape;
    public int amount;

    @Override
    public boolean validateBeforeStart(EntityRef instigator, EntityRef workstation, EntityRef processEntity) {
        boolean isValid = false;
        InventoryInputProcessPartSlotAmountsComponent slotAmountsComponent = processEntity.getComponent(InventoryInputProcessPartSlotAmountsComponent.class);
        for (Map.Entry<Integer, Integer> slotAmount : slotAmountsComponent.slotAmounts.entrySet()) {
            EntityRef itemInSlot = InventoryUtils.getItemAt(workstation, slotAmount.getKey());
            BlockItemComponent blockItemComponent = itemInSlot.getComponent(BlockItemComponent.class);
            if (blockItemComponent != null) {
                BlockUri sourceBlockUri = blockItemComponent.blockFamily.getURI();
                BlockUri newBlockUri = new BlockUri(new ResourceUrn(sourceBlockUri.getShapelessUri().toString()), new ResourceUrn(shape));
                SymmetricBlockTransformationComponent symmetricBlockTransformationComponent = new SymmetricBlockTransformationComponent();
                symmetricBlockTransformationComponent.blockFamily = CoreRegistry.get(BlockManager.class).getBlockFamily(newBlockUri);
                processEntity.addComponent(symmetricBlockTransformationComponent);
                isValid = true;
                break;
            }
        }
        return isValid && super.validateBeforeStart(instigator, workstation, processEntity);
    }

    @Override
    protected Set<EntityRef> createOutputItems(EntityRef processEntity) {
        Set<EntityRef> output = Sets.newHashSet();
        SymmetricBlockTransformationComponent symmetricBlockTransformationComponent = processEntity.getComponent(SymmetricBlockTransformationComponent.class);
        if (symmetricBlockTransformationComponent != null) {
            BlockItemFactory blockFactory = new BlockItemFactory(CoreRegistry.get(EntityManager.class));
            output.add(blockFactory.newInstance(symmetricBlockTransformationComponent.blockFamily, amount));
        } else {
            BlockUri newBlockUri = new BlockUri(new ResourceUrn("ManualLabor:TempBlock"), new ResourceUrn(shape));
            BlockItemFactory blockFactory = new BlockItemFactory(CoreRegistry.get(EntityManager.class));
            output.add(blockFactory.newInstance(CoreRegistry.get(BlockManager.class).getBlockFamily(newBlockUri), amount));
        }
        return output;
    }
}
