/*
 * Copyright 2016 MovingBlocks
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
package org.terasology.manualLabor.processParts;

import com.google.common.collect.Lists;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.machines.ui.OverlapLayout;
import org.terasology.rendering.nui.widgets.UIImage;
import org.terasology.utilities.Assets;
import org.terasology.workstation.process.ProcessPartDescription;
import org.terasology.workstation.processPart.ProcessEntityGetDurationEvent;
import org.terasology.workstation.processPart.metadata.ProcessEntityGetInputDescriptionEvent;

import java.util.List;

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
        List<ProcessPartDescription> descriptions = Lists.newLinkedList();
        String time = String.valueOf(processingTimeComponent.duration / 1000);
        UIImage image = new UIImage(Assets.getTextureRegion("ManualLabor:Manuallabor#Time").get());
        OverlapLayout layout = new OverlapLayout();
        layout.addWidget(image);
        layout.setTooltip(time + " sec");
        layout.setTooltipDelay(0);
        event.addInputDescription(new ProcessPartDescription(null, time + " sec", layout));

    }
}

