// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.manualLabor.components;

import org.terasology.engine.entitySystem.Component;

public class ShearableComponent implements Component {
    public long lastShearingTimestamp = -1;
    public boolean sheared;
}
