// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.manualLabor.components;

import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Add this to a substance so that when a tool is created, its base durability is increased based on how much of the substance is present
 */
public class IncreaseToolDurabilityComponent implements Component<IncreaseToolDurabilityComponent>, ToolModificationDescription {
    public float increasePerSubstanceAmount;

    @Override
    public String getDescription() {
        String upDown = increasePerSubstanceAmount < 0f ? "Decreases" : "Increases";
        return String.format(upDown + " tool durability by %.1f for every unit of material", Math.abs(increasePerSubstanceAmount));
    }

    @Override
    public void copyFrom(IncreaseToolDurabilityComponent other) {
        this.increasePerSubstanceAmount = other.increasePerSubstanceAmount;
    }
}
