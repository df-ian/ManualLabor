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

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.registry.CoreRegistry;
import org.terasology.substanceMatters.components.MaterialCompositionComponent;
import org.terasology.substanceMatters.processParts.MaterialItemOutputComponent;
import org.terasology.workstation.process.inventory.InventoryOutputComponent;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Creates an material item containing the materials that it is composed of based on the original input items.  The item will appear like the largest amount of substance.
 */
public class SiftedMaterialOutputComponent extends InventoryOutputComponent {
    public String item;
    public String smallItem;
    public float smallItemAmount = 2.5f;
    public float minimumSiftableAmount = 1f;

    @Override
    protected Set<EntityRef> createOutputItems(EntityRef processEntity) {
        EntityManager entityManager = CoreRegistry.get(EntityManager.class);

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
            if (materialAmount.getValue() >= minimumSiftableAmount && splittableSubstances.size() < 2) {
                splittableSubstances.put(materialAmount.getKey(), materialAmount.getValue());
            }
        }

        if (splittableSubstances.size() != 2) {
            // just output a single item if there is nothing to split.
            EntityRef newItem = entityManager.create(item);
            newItem.addComponent(materialComposition);
            MaterialItemOutputComponent.setDisplayName(newItem, materialComposition);
            result.add(newItem);
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
                EntityRef newItem;
                if (splittableSubstance.getValue() <= smallItemAmount) {
                    newItem = entityManager.create(smallItem);
                } else {
                    newItem = entityManager.create(item);
                }
                MaterialCompositionComponent newComposition = extraSubstances.copy();
                newComposition.addSubstance(splittableSubstance.getKey(), splittableSubstance.getValue());

                newItem.addComponent(newComposition);
                MaterialItemOutputComponent.setDisplayName(newItem, newComposition);
                result.add(newItem);
            }
        }

        return result;
    }
}
