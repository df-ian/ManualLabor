// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.manualLabor.components;

import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Add this to a substance so that when a tool is created, its base damage is increased based on how much of the substance is present
 */
public class IncreaseToolDamageComponent implements Component<IncreaseToolDamageComponent>, ToolModificationDescription {
    public float increasePerSubstanceAmount;

    @Override
    public String getDescription() {
        String upDown = increasePerSubstanceAmount < 0f ? "Decreases" : "Increases";
        return String.format(upDown + " tool damage by %.1f for every 100 units of material", Math.abs(increasePerSubstanceAmount * 100f));
    }

    @Override
    public void copyFrom(IncreaseToolDamageComponent other) {
        this.increasePerSubstanceAmount = other.increasePerSubstanceAmount;
    }
}
