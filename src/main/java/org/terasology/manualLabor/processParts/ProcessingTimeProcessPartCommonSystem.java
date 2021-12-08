// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.manualLabor.processParts;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.utilities.Assets;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.machines.ui.OverlapLayout;
import org.terasology.nui.widgets.UIImage;
import org.terasology.workstation.process.ProcessPartDescription;
import org.terasology.workstation.processPart.ProcessEntityGetDurationEvent;
import org.terasology.workstation.processPart.metadata.ProcessEntityGetInputDescriptionEvent;

@RegisterSystem
public class ProcessingTimeProcessPartCommonSystem extends BaseComponentSystem {
    @ReceiveEvent
    public void getDuration(ProcessEntityGetDurationEvent event, EntityRef processEntity,
                            ProcessingTimeComponent processingTimeComponent) {
        event.add(processingTimeComponent.duration / 1000f);
    }

    @ReceiveEvent
    public void getInputDescriptions(ProcessEntityGetInputDescriptionEvent event, EntityRef processEntity,
                                     ProcessingTimeComponent processingTimeComponent) {
        String time = String.valueOf(processingTimeComponent.duration / 1000);
        UIImage image = new UIImage(Assets.getTextureRegion("ManualLabor:Manuallabor#Time").get());
        OverlapLayout layout = new OverlapLayout();
        layout.addWidget(image);
        layout.setTooltip(time + " sec");
        layout.setTooltipDelay(0);
        event.addInputDescription(new ProcessPartDescription(null, time + " sec", layout));

    }
}

