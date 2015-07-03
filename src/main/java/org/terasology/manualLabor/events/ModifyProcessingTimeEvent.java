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
package org.terasology.manualLabor.events;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.AbstractValueModifiableEvent;

public class ModifyProcessingTimeEvent extends AbstractValueModifiableEvent {
    private EntityRef instigator;
    private EntityRef workstation;
    private EntityRef processEntity;

    public ModifyProcessingTimeEvent(float baseValue, EntityRef instigator, EntityRef workstation, EntityRef processEntity) {
        super(baseValue);
        this.instigator = instigator;
        this.workstation = workstation;
        this.processEntity = processEntity;
    }

    public EntityRef getInstigator() {
        return instigator;
    }

    public EntityRef getWorkstation() {
        return workstation;
    }

    public EntityRef getProcessEntity() {
        return processEntity;
    }
}
