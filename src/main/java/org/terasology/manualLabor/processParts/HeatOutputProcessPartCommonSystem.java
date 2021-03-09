// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.manualLabor.processParts;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.utilities.Assets;
import org.terasology.machines.ui.OverlapLayout;
import org.terasology.manualLabor.components.HeatSourceComponent;
import org.terasology.nui.widgets.UIImage;
import org.terasology.workstation.process.ProcessPartDescription;
import org.terasology.workstation.processPart.ProcessEntityFinishExecutionEvent;
import org.terasology.workstation.processPart.ProcessEntityGetDurationEvent;
import org.terasology.workstation.processPart.ProcessEntityIsInvalidToStartEvent;
import org.terasology.workstation.processPart.ProcessEntityStartExecutionEvent;
import org.terasology.workstation.processPart.metadata.ProcessEntityGetOutputDescriptionEvent;

@RegisterSystem
public class HeatOutputProcessPartCommonSystem extends BaseComponentSystem {

    ///// Processing

    @ReceiveEvent
    public void validateToStartExecution(ProcessEntityIsInvalidToStartEvent event, EntityRef processEntity,
                                         HeatOutputComponent heatOutputComponent) {
    }

    @ReceiveEvent
    public void startExecution(ProcessEntityStartExecutionEvent event, EntityRef processEntity,
                               HeatOutputComponent heatOutputComponent) {
        if (!event.getWorkstation().hasComponent(HeatSourceComponent.class)) {
            event.getWorkstation().addComponent(new HeatSourceComponent());
        }

    }

    @ReceiveEvent
    public void getDuration(ProcessEntityGetDurationEvent event, EntityRef processEntity,
                            HeatOutputComponent heatOutputComponent) {
        event.add(heatOutputComponent.burnTime / 1000f);
    }

    @ReceiveEvent
    public void finishExecution(ProcessEntityFinishExecutionEvent event, EntityRef entityRef,
                                HeatOutputComponent heatOutputComponent) {
        if (event.getWorkstation().hasComponent(HeatSourceComponent.class)) {
            event.getWorkstation().removeComponent(HeatSourceComponent.class);
        }
    }

    @ReceiveEvent
    public void getOutputDescriptions(ProcessEntityGetOutputDescriptionEvent event, EntityRef processEntity,
                                      HeatOutputComponent heatOutputComponent) {
        String description = "Heat";
        UIImage image = new UIImage(Assets.getTextureRegion("ManualLabor:Manuallabor#Heat").get());
        OverlapLayout layout = new OverlapLayout();
        layout.addWidget(image);
        layout.setTooltip(description);
        layout.setTooltipDelay(0);
        event.addOutputDescription(new ProcessPartDescription(null, description, layout));


    }
}
