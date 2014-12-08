/*
 * Copyright 2014 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.manualLabor.processParts;

import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.manualLabor.components.HeatedComponent;
import org.terasology.workstation.process.DescribeProcess;
import org.terasology.workstation.process.ProcessPart;
import org.terasology.workstation.process.ProcessPartDescription;

public class HeatOutputComponent implements Component, ProcessPart, DescribeProcess {
    public long burnTime;

    @Override
    public boolean validateBeforeStart(EntityRef instigator, EntityRef workstation, EntityRef processEntity) {
        return true;
    }

    @Override
    public long getDuration(EntityRef instigator, EntityRef workstation, EntityRef processEntity) {
        return burnTime;
    }

    @Override
    public void executeStart(EntityRef instigator, EntityRef workstation, EntityRef processEntity) {
        if (!workstation.hasComponent(HeatedComponent.class)) {
            workstation.addComponent(new HeatedComponent());
        }
    }

    @Override
    public void executeEnd(EntityRef instigator, EntityRef workstation, EntityRef processEntity) {
        if (workstation.hasComponent(HeatedComponent.class)) {
            workstation.removeComponent(HeatedComponent.class);
        }
    }

    @Override
    public ProcessPartDescription getOutputDescription() {
        return new ProcessPartDescription((burnTime / 1000) + " seconds of heat");
    }

    @Override
    public ProcessPartDescription getInputDescription() {
        return null;
    }

    @Override
    public int getComplexity() {
        return 0;
    }
}
