// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.manualLabor.systems;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.prefab.PrefabManager;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.In;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.manualLabor.components.AnalyzedMaterialComponent;
import org.terasology.module.inventory.ui.GetItemTooltip;
import org.terasology.nui.widgets.TooltipLine;
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
