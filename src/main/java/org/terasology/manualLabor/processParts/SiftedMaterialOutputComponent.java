// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.manualLabor.processParts;

import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Creates an material item containing the materials that it is composed of based on the original input items.
 * The item will appear like the largest amount of substance.
 */
public class SiftedMaterialOutputComponent implements Component<SiftedMaterialOutputComponent> {
    public String item;
    public String smallItem;
    public float smallItemAmount = 2.5f;
    public float minimumSiftableAmount = 1f;

    @Override
    public void copyFrom(SiftedMaterialOutputComponent other) {
        this.item = other.item;
        this.smallItem = other.smallItem;
        this.smallItemAmount = other.smallItemAmount;
        this.minimumSiftableAmount = other.minimumSiftableAmount;
    }
}
