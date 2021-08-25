// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.manualLabor.components;

import org.terasology.gestalt.entitysystem.component.Component;

public class BurnableSubstanceComponent implements Component<BurnableSubstanceComponent> {
    public int burnTimePerSubstanceAmount;

    @Override
    public void copyFrom(BurnableSubstanceComponent other) {
        this.burnTimePerSubstanceAmount = other.burnTimePerSubstanceAmount;
    }
}
