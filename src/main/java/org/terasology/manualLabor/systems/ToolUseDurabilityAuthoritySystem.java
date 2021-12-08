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

import org.terasology.durability.events.ReduceDurabilityEvent;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.machines.events.RequirementUsedEvent;
import org.terasology.workstation.process.inventory.InventoryInputProcessPartSlotAmountsComponent;

@RegisterSystem(RegisterMode.AUTHORITY)
public class ToolUseDurabilityAuthoritySystem extends BaseComponentSystem {
    @ReceiveEvent
    public void onToolUse(RequirementUsedEvent event, EntityRef entity) {
        InventoryInputProcessPartSlotAmountsComponent slotAmounts = event.getProcessEntity()
                .getComponent(InventoryInputProcessPartSlotAmountsComponent.class);
        if (slotAmounts != null) {
            int totalAmount = 0;
            for (Integer itemAmount : slotAmounts.slotAmounts.values()) {
                totalAmount += itemAmount;
            }
            entity.send(new ReduceDurabilityEvent(totalAmount));
        } else {
            entity.send(new ReduceDurabilityEvent(1));
        }
    }
}
