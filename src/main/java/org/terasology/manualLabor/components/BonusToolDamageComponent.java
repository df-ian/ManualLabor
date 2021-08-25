// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.manualLabor.components;

import org.terasology.gestalt.entitysystem.component.Component;

public class BonusToolDamageComponent implements Component<BonusToolDamageComponent> {
    public float baseDamage;

    public boolean hasValue() {
        return baseDamage > 0;
    }

    @Override
    public void copyFrom(BonusToolDamageComponent other) {
        this.baseDamage = other.baseDamage;
    }
}
