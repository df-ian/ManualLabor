// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.manualLabor.processParts;

import org.terasology.gestalt.entitysystem.component.Component;

public class HeatOutputComponent implements Component<HeatOutputComponent> {
    public long burnTime;

    @Override
    public void copyFrom(HeatOutputComponent other) {
        this.burnTime = other.burnTime;
    }
}
