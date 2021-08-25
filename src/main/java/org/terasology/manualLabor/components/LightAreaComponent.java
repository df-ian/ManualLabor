// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.manualLabor.components;

import com.google.common.collect.Sets;
import org.terasology.engine.entitySystem.Owns;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.world.block.ForceBlockActive;
import org.terasology.gestalt.entitysystem.component.Component;

import java.util.Set;

@ForceBlockActive
public class LightAreaComponent implements Component<LightAreaComponent> {
    @Owns
    public Set<EntityRef> lights = Sets.newHashSet();
    public String lightPrefab;

    @Override
    public void copyFrom(LightAreaComponent other) {
        this.lights = Sets.newHashSet(other.lights);
        this.lightPrefab = other.lightPrefab;
    }
}
