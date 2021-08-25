// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.manualLabor.processParts;

import org.terasology.gestalt.entitysystem.component.Component;

public class ToolItemOutputComponent implements Component<ToolItemOutputComponent> {
    public String item;

    @Override
    public void copyFrom(ToolItemOutputComponent other) {
        this.item = other.item;
    }
}
