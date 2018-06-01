/*
 * Copyright 2018 MovingBlocks
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

import org.terasology.entityNetwork.systems.EntityNetworkManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.EventPriority;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.input.ButtonState;
import org.terasology.input.binds.inventory.InventoryButton;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.manualLabor.components.HeatBlockNetworkComponent;
import org.terasology.manualLabor.components.HeatSourceComponent;
import org.terasology.manualLabor.components.TheHumanMachineComponent;
import org.terasology.mechanicalPower.components.MechanicalPowerProducerComponent;
import org.terasology.network.ClientComponent;
import org.terasology.registry.In;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.rendering.nui.layers.ingame.inventory.GetItemTooltip;
import org.terasology.rendering.nui.widgets.TooltipLine;

@RegisterSystem(RegisterMode.CLIENT)
public class HeatClientSystem extends BaseComponentSystem {
    @In
    EntityNetworkManager entityNetworkManager;

    @ReceiveEvent
    public void getItemTooltip(GetItemTooltip event, EntityRef entityRef, HeatBlockNetworkComponent heatBlockNetworkComponent) {
        if( entityRef.hasComponent(HeatSourceComponent.class)) {
            event.getTooltipLines().add(new TooltipLine("Producing: Heat"));
        } else {
            float averageHeat = HeatAuthoritySystem.getAverageHeat(entityRef, entityNetworkManager);
            if (averageHeat > 0) {
                event.getTooltipLines().add(new TooltipLine("Hot"));
            } else {
                event.getTooltipLines().add(new TooltipLine("Cold"));
            }
        }
    }
}
