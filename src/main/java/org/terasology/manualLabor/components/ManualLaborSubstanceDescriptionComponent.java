// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.manualLabor.components;

import org.terasology.gestalt.entitysystem.component.Component;

public class ManualLaborSubstanceDescriptionComponent implements Component<ManualLaborSubstanceDescriptionComponent> {
    public String description = "";
    public String defaultItemTexture = "ManualLabor:Nugget";

    @Override
    public void copyFrom(ManualLaborSubstanceDescriptionComponent other) {
        this.description = other.description;
        this.defaultItemTexture = other.defaultItemTexture;
    }
}
