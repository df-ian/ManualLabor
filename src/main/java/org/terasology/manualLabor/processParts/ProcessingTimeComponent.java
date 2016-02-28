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

import com.google.common.collect.Lists;
import org.terasology.utilities.Assets;
import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.machines.ui.OverlapLayout;
import org.terasology.manualLabor.events.ModifyProcessingTimeEvent;
import org.terasology.rendering.nui.widgets.UIImage;
import org.terasology.workstation.process.DescribeProcess;
import org.terasology.workstation.process.ProcessPart;
import org.terasology.workstation.process.ProcessPartDescription;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
    public Collection<ProcessPartDescription> getOutputDescriptions() {
        return Collections.emptyList();
    }

    @Override
    public Collection<ProcessPartDescription> getInputDescriptions() {
        List<ProcessPartDescription> descriptions = Lists.newLinkedList();
        String time = String.valueOf(duration / 1000);
        UIImage image = new UIImage(Assets.getTextureRegion("ManualLabor:Manuallabor#Time").get());
        OverlapLayout layout = new OverlapLayout();
        layout.addWidget(image);
        layout.setTooltip(time + " sec");
        layout.setTooltipDelay(0);
        descriptions.add(new ProcessPartDescription(null, time + " sec", layout));
        return descriptions;
    }
}
