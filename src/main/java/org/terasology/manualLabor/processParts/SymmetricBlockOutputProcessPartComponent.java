// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.manualLabor.processParts;

import org.terasology.engine.world.block.family.BlockFamily;
import org.terasology.gestalt.entitysystem.component.Component;

public class SymmetricBlockOutputProcessPartComponent implements Component<SymmetricBlockOutputProcessPartComponent> {
    public BlockFamily blockFamily;

    @Override
    public void copyFrom(SymmetricBlockOutputProcessPartComponent other) {
        this.blockFamily = other.blockFamily;
    }
}
