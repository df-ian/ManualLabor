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
package org.terasology.manualLabor.systems;

import org.terasology.durability.components.DurabilityComponent;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.utilities.Assets;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.manualLabor.components.BonusToolDamageComponent;
import org.terasology.manualLabor.components.IncreaseToolDamageComponent;
import org.terasology.manualLabor.components.IncreaseToolDurabilityComponent;
import org.terasology.manualLabor.components.MultiplyToolDurabilityComponent;
import org.terasology.manualLabor.events.ModifyToolCreationEvent;
import org.terasology.substanceMatters.components.MaterialCompositionComponent;

import java.util.Map;

@RegisterSystem
public class ModifyToolCreationSystem extends BaseComponentSystem {

    /**
     * Modify the original durability based on the substances used in its creation
     */
    @ReceiveEvent
    public void onModifyToolDurability(ModifyToolCreationEvent event, EntityRef toolEntity, DurabilityComponent durabilityComponent) {
        MaterialCompositionComponent materialCompositionComponent = toolEntity.getComponent(MaterialCompositionComponent.class);

        if (materialCompositionComponent != null) {
            // increase the tool's base durability
            for (Map.Entry<String, Float> substance : materialCompositionComponent.contents.entrySet()) {
                Prefab substancePrefab = Assets.getPrefab(substance.getKey()).get();

                IncreaseToolDurabilityComponent substanceIncrease = substancePrefab.getComponent(IncreaseToolDurabilityComponent.class);
                if (substanceIncrease != null) {
                    durabilityComponent.maxDurability += substanceIncrease.increasePerSubstanceAmount * substance.getValue();
                }
            }

            // multiply the durability
            for (Map.Entry<String, Float> substance : materialCompositionComponent.contents.entrySet()) {
                Prefab substancePrefab = Assets.getPrefab(substance.getKey()).get();

                MultiplyToolDurabilityComponent substanceMultiply = substancePrefab.getComponent(MultiplyToolDurabilityComponent.class);
                if (substanceMultiply != null) {
                    durabilityComponent.maxDurability *= Math.pow(substanceMultiply.multiplyPerSubstanceAmount, substance.getValue());
                }
            }

            // ensure the tool's initial durability is reset
            durabilityComponent.durability = durabilityComponent.maxDurability;

            toolEntity.saveComponent(durabilityComponent);


            // add the tool's bonus damage
            BonusToolDamageComponent bonusToolDamageComponent = new BonusToolDamageComponent();
            for (Map.Entry<String, Float> substance : materialCompositionComponent.contents.entrySet()) {
                Prefab substancePrefab = Assets.getPrefab(substance.getKey()).get();

                IncreaseToolDamageComponent substanceIncrease = substancePrefab.getComponent(IncreaseToolDamageComponent.class);
                if (substanceIncrease != null) {
                    bonusToolDamageComponent.baseDamage = substanceIncrease.increasePerSubstanceAmount * substance.getValue();
                }
            }
            if (bonusToolDamageComponent.hasValue()) {
                toolEntity.addComponent(bonusToolDamageComponent);
            }
        }
    }
}
