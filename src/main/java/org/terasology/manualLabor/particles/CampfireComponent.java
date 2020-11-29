// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.manualLabor.particles;

import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.Owns;
import org.terasology.entitySystem.entity.EntityRef;

public class CampfireComponent implements Component {
    @Owns
    EntityRef flameEmitter = EntityRef.NULL;
    @Owns
    EntityRef smokeEmitter = EntityRef.NULL;
    @Owns
    EntityRef lightEmitter = EntityRef.NULL;

}
