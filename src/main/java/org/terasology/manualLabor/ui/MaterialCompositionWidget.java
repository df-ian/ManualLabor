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
package org.terasology.manualLabor.ui;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.inventory.InventoryManager;
import org.terasology.machines.ExtendedInventoryManager;
import org.terasology.math.geom.Vector2i;
import org.terasology.registry.CoreRegistry;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.CoreWidget;
import org.terasology.rendering.nui.LayoutConfig;
import org.terasology.rendering.nui.widgets.UILabel;
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

        String result = "";
        if (station.exists()) {
            for (EntityRef entity : ExtendedInventoryManager.iterateItems(
                    CoreRegistry.get(InventoryManager.class),
                    station,
                    true,
                    InventoryOutputProcessPartCommonSystem.WORKSTATIONOUTPUTCATEGORY)) {
                MaterialCompositionComponent component = entity.getComponent(MaterialCompositionComponent.class);
                if (component != null) {
                    result += component.toDisplayString();
                }
            }
        }
        content.setText(result);
    }

    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i sizeHint) {
        return content.getPreferredContentSize(canvas, sizeHint);
    }
}
