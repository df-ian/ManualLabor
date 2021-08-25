// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.manualLabor.components;

import org.terasology.gestalt.entitysystem.component.Component;

/**
 * This component can be attached to entities to allow them to be sheared.
 */
public class ShearableComponent implements Component<ShearableComponent> {

    /**
     * Stores the last time the shearing took place. Default -1 for no previous shearing event.
     */
    public long lastShearingTimestamp = -1;

    /**
     * Stores the current state of shearing i.e. whether sheared or not.
     */
    public boolean isSheared;

    /**
     * URI of item to be dropped in event of shearing
     */
    public String dropItemURI;

    @Override
    public void copyFrom(ShearableComponent other) {
        this.lastShearingTimestamp = other.lastShearingTimestamp;
        this.isSheared = other.isSheared;
        this.dropItemURI = other.dropItemURI;
    }
}
