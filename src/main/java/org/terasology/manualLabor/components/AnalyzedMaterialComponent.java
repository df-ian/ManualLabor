// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.manualLabor.components;

import org.terasology.engine.network.Replicate;
import org.terasology.gestalt.entitysystem.component.Component;

public class AnalyzedMaterialComponent implements Component<AnalyzedMaterialComponent> {
    @Replicate
    public boolean IsAnalyzed = true;

    @Override
    public void copyFrom(AnalyzedMaterialComponent other) {
        this.IsAnalyzed = other.IsAnalyzed;
    }
}
