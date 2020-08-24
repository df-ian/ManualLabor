// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.manualLabor.systems;

import org.terasology.entityNetwork.systems.EntityNetworkManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.manualLabor.components.HeatBlockNetworkComponent;
import org.terasology.manualLabor.components.HeatSourceComponent;
import org.terasology.nui.widgets.TooltipLine;
import org.terasology.registry.In;
import org.terasology.rendering.nui.layers.ingame.inventory.GetItemTooltip;

@RegisterSystem(RegisterMode.CLIENT)
public class HeatClientSystem extends BaseComponentSystem {
    @In
    EntityNetworkManager entityNetworkManager;

    @ReceiveEvent
    public void getItemTooltip(GetItemTooltip event, EntityRef entityRef, HeatBlockNetworkComponent heatBlockNetworkComponent) {
        if (entityRef.hasComponent(HeatSourceComponent.class)) {
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
