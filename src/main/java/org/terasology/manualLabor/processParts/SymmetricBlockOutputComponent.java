// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.manualLabor.processParts;

import org.terasology.gestalt.entitysystem.component.Component;

public class SymmetricBlockOutputComponent implements Component<SymmetricBlockOutputComponent> {
    public String shape;
    public int amount;

    @Override
    public void copyFrom(SymmetricBlockOutputComponent other) {
        this.shape = other.shape;
        this.amount = other.amount;
    }
}
