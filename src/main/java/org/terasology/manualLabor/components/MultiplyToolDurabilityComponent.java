// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.manualLabor.components;

import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Add this to a substance so that when a tool is created, its durability is multiplied based on how much of the substance is present.  It stacks multiplicatively
 */
public class MultiplyToolDurabilityComponent implements Component<MultiplyToolDurabilityComponent>, ToolModificationDescription {
    public float multiplyPerSubstanceAmount = 1f;

    @Override
    public String getDescription() {
        String upDown = multiplyPerSubstanceAmount < 1f ? "Decreases" : "Increases";

        return String.format(upDown + " tool durability by %.1f%% for every 10 units of material", Math.abs((Math.pow(multiplyPerSubstanceAmount, 10) - 1f) * 100));
    }

    @Override
    public void copyFrom(MultiplyToolDurabilityComponent other) {
        this.multiplyPerSubstanceAmount = other.multiplyPerSubstanceAmount;
    }
}
