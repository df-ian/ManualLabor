// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.manualLabor.processParts;

import org.terasology.gestalt.entitysystem.component.Component;

public class ProcessingTimeComponent implements Component<ProcessingTimeComponent> {
    public long duration;

    @Override
    public void copyFrom(ProcessingTimeComponent other) {
        this.duration = other.duration;
    }
}
