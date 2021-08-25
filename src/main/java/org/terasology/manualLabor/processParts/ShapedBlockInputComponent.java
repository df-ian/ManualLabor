// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.manualLabor.processParts;

import org.terasology.gestalt.entitysystem.component.Component;

public class ShapedBlockInputComponent implements Component<ShapedBlockInputComponent> {
    public String shape;
    public int amount = 1;

    @Override
    public void copyFrom(ShapedBlockInputComponent other) {
        this.shape = other.shape;
        this.amount = other.amount;
    }
}
