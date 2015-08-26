/*
 * Copyright 2014 MovingBlocks
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

import org.terasology.asset.Assets;
import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.manualLabor.events.ModifyProcessingTimeEvent;
import org.terasology.manualLabor.ui.OverlapLayout;
import org.terasology.rendering.nui.widgets.UIImage;
import org.terasology.workstation.process.DescribeProcess;
import org.terasology.workstation.process.ProcessPart;
import org.terasology.workstation.process.ProcessPartDescription;

public class ProcessingTimeComponent implements Component, ProcessPart, DescribeProcess {
    public long duration;

    @Override
    public boolean validateBeforeStart(EntityRef instigator, EntityRef workstation, EntityRef processEntity) {
        return true;
    }

    @Override
    public long getDuration(EntityRef instigator, EntityRef workstation, EntityRef processEntity) {
        ModifyProcessingTimeEvent processingTimeEvent = new ModifyProcessingTimeEvent(duration, instigator, workstation, processEntity);
        instigator.send(processingTimeEvent);
        workstation.send(processingTimeEvent);
        processEntity.send(processingTimeEvent);
        return ((Float) processingTimeEvent.getResultValue()).longValue();
    }

    @Override
    public void executeStart(EntityRef instigator, EntityRef workstation, EntityRef processEntity) {

    }

    @Override
    public void executeEnd(EntityRef instigator, EntityRef workstation, EntityRef processEntity) {

    }

    @Override
    public ProcessPartDescription getOutputDescription() {
        return null;
    }

    @Override
    public ProcessPartDescription getInputDescription() {
        String time = String.valueOf(duration / 1000);
        UIImage image = new UIImage(Assets.getTextureRegion("ManualLabor:Manuallabor#Time").get());
        OverlapLayout layout = new OverlapLayout();
        layout.addWidget(image);
        layout.setTooltip(time + " sec");
        layout.setTooltipDelay(0);

        return new ProcessPartDescription(time + " sec", layout);
    }

    @Override
    public int getComplexity() {
        return 0;
    }
}
