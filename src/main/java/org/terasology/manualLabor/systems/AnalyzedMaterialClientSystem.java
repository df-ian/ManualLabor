// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.manualLabor.systems;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.prefab.PrefabManager;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.manualLabor.components.AnalyzedMaterialComponent;
import org.terasology.nui.widgets.TooltipLine;
import org.terasology.registry.In;
import org.terasology.rendering.nui.layers.ingame.inventory.GetItemTooltip;
import org.terasology.substanceMatters.components.MaterialCompositionComponent;

@RegisterSystem(RegisterMode.CLIENT)
public class AnalyzedMaterialClientSystem extends BaseComponentSystem {
    @In
    PrefabManager prefabManager;

    @ReceiveEvent
    public void getDurabilityItemTooltip(GetItemTooltip event, EntityRef entity,
                                         AnalyzedMaterialComponent analyzedMaterialComponent,
                                         MaterialCompositionComponent materialCompositionComponent) {
        event.getTooltipLines().add(new TooltipLine(materialCompositionComponent.toDisplayString()));
    }
}
