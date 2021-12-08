// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.manualLabor.ui;

import org.joml.Vector2i;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.module.inventory.systems.InventoryManager;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.machines.ExtendedInventoryManager;
import org.terasology.nui.Canvas;
import org.terasology.nui.CoreWidget;
import org.terasology.nui.LayoutConfig;
import org.terasology.nui.widgets.UILabel;
import org.terasology.substanceMatters.components.MaterialCompositionComponent;
import org.terasology.workstation.process.inventory.InventoryOutputProcessPartCommonSystem;
import org.terasology.workstation.ui.WorkstationUI;

public class MaterialCompositionWidget extends CoreWidget implements WorkstationUI {
    private EntityRef station;

    @LayoutConfig
    private UILabel content;

    @Override
    public void initializeWorkstation(EntityRef entity) {
        station = entity;
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawWidget(content);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        StringBuilder result = new StringBuilder();
        if (station.exists()) {
            for (EntityRef entity : ExtendedInventoryManager.iterateItems(
                    CoreRegistry.get(InventoryManager.class),
                    station,
                    true,
                    InventoryOutputProcessPartCommonSystem.WORKSTATIONOUTPUTCATEGORY)) {
                MaterialCompositionComponent component = entity.getComponent(MaterialCompositionComponent.class);
                if (component != null) {
                    result.append(component.toDisplayString());
                }
            }
        }
        content.setText(result.toString());
    }

    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i sizeHint) {
        return content.getPreferredContentSize(canvas, sizeHint);
    }
}
