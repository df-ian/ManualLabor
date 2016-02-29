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

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.registry.CoreRegistry;
import org.terasology.workstation.process.inventory.InventoryInputComponent;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.family.SymmetricFamily;
import org.terasology.world.block.items.BlockItemComponent;
import org.terasology.world.block.items.BlockItemFactory;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class SymmetricBlockInputComponent extends InventoryInputComponent {
    private static final Logger logger = LoggerFactory.getLogger(SymmetricBlockInputComponent.class);

    @Override
    protected Map<Predicate<EntityRef>, Integer> getInputItems() {
        Map<Predicate<EntityRef>, Integer> output = Maps.newHashMap();
        for (EntityRef item : createItems()) {
            output.put(new Predicate<EntityRef>() {
                @Override
                public boolean apply(@Nullable EntityRef input) {
                    BlockItemComponent blockItemComponent = input.getComponent(BlockItemComponent.class);
                    if (blockItemComponent != null) {
                        return blockItemComponent.blockFamily instanceof SymmetricFamily && !blockItemComponent.blockFamily.getURI().getShapeUrn().isPresent();
                    }
                    return false;
                }
            }, 1);
        }
        return output;
    }

    @Override
    protected Set<EntityRef> createItems() {
        Set<EntityRef> output = Sets.newHashSet();
        BlockItemFactory blockFactory = new BlockItemFactory(CoreRegistry.get(EntityManager.class));
        output.add(blockFactory.newInstance(CoreRegistry.get(BlockManager.class).getBlockFamily("ManualLabor:TempBlock"), 1));
        return output;
    }
}
