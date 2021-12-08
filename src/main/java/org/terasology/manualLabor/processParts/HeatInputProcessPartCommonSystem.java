// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.manualLabor.processParts;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.In;
import org.terasology.engine.utilities.Assets;
import org.terasology.entityNetwork.systems.EntityNetworkManager;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.machines.ui.OverlapLayout;
import org.terasology.manualLabor.components.HeatedComponent;
import org.terasology.manualLabor.systems.HeatAuthoritySystem;
import org.terasology.nui.widgets.UIImage;
import org.terasology.workstation.process.ProcessPartDescription;
import org.terasology.workstation.processPart.ProcessEntityFinishExecutionEvent;
import org.terasology.workstation.processPart.ProcessEntityIsInvalidToStartEvent;
import org.terasology.workstation.processPart.ProcessEntityStartExecutionEvent;
import org.terasology.workstation.processPart.metadata.ProcessEntityGetInputDescriptionEvent;


@RegisterSystem
public class HeatInputProcessPartCommonSystem extends BaseComponentSystem {
    @In
    EntityNetworkManager entityNetworkManager;

    ///// Processing

    @ReceiveEvent
    public void validateToStartExecution(ProcessEntityIsInvalidToStartEvent event, EntityRef processEntity,
                                         HeatInputComponent heatInputComponent) {

        float averageHeat = HeatAuthoritySystem.getAverageHeat(event.getWorkstation(), entityNetworkManager);

        // compare to the average heat sources in the network
        if (averageHeat == 0) {
            event.consume();
        }
    }

    @ReceiveEvent
    public void startExecution(ProcessEntityStartExecutionEvent event, EntityRef processEntity,
                               HeatInputComponent heatInputComponent) {
        if (event.getWorkstation().hasComponent(HeatedComponent.class)) {
            event.getWorkstation().removeComponent(HeatedComponent.class);
        }
    }

    @ReceiveEvent
    public void finishExecution(ProcessEntityFinishExecutionEvent event, EntityRef entityRef,
                                HeatInputComponent heatInputComponent) {
        if (event.getWorkstation().hasComponent(HeatedComponent.class)) {
            event.getWorkstation().removeComponent(HeatedComponent.class);
        }
    }

    ///// Metadata

    @ReceiveEvent
    public void getInputDescriptions(ProcessEntityGetInputDescriptionEvent event, EntityRef processEntity,
                                     HeatInputComponent heatInputComponent) {
        String description = "Heat";
        UIImage image = new UIImage(Assets.getTextureRegion("ManualLabor:Manuallabor#Heat").get());
        OverlapLayout layout = new OverlapLayout();
        layout.addWidget(image);
        layout.setTooltip(description);
        layout.setTooltipDelay(0);
        event.addInputDescription(new ProcessPartDescription(null, description, layout));
    }
}
