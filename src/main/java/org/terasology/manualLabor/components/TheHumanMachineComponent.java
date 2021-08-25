// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.manualLabor.components;

import org.terasology.engine.entitySystem.Owns;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.Replicate;
import org.terasology.gestalt.entitysystem.component.Component;

public class TheHumanMachineComponent implements Component<TheHumanMachineComponent> {
    @Owns
    @Replicate
    public EntityRef machineEntity;

    @Override
    public void copyFrom(TheHumanMachineComponent other) {
        this.machineEntity = other.machineEntity;
    }
}
