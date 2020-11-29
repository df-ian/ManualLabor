// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.manualLabor.particles;

import org.joml.Vector3f;
import org.terasology.entitySystem.entity.EntityBuilder;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.lifecycleEvents.BeforeDeactivateComponent;
import org.terasology.entitySystem.entity.lifecycleEvents.OnAddedComponent;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.location.LocationComponent;
import org.terasology.particles.components.ParticleEmitterComponent;
import org.terasology.registry.In;
import org.terasology.world.block.BlockComponent;

@RegisterSystem(RegisterMode.ALWAYS)
public class CampfireParticleSystem extends BaseComponentSystem {

    @In
    EntityManager entityManager;

    @ReceiveEvent(components = {BlockComponent.class})
    public void onActivate(OnAddedComponent event, EntityRef entity, LocationComponent location,
                           CampfireComponent campfire) {
        Vector3f loc = location.getWorldPosition(new Vector3f());
        campfire.flameEmitter = createEmitter(entity, loc, "ManualLabor:flames");
        loc.add(0, 1, 0);
        campfire.smokeEmitter = createEmitter(entity, loc, "ManualLabor:smoke");
        entity.saveComponent(campfire);
    }

    @ReceiveEvent
    public void onDestroy(BeforeDeactivateComponent event, EntityRef entity, CampfireComponent campfire) {
        campfire.smokeEmitter.destroy();
        campfire.flameEmitter.destroy();
        campfire.lightEmitter.destroy();
    }

    private EntityRef createEmitter(EntityRef entity, Vector3f location, String prefabName) {
        EntityBuilder builder = entityManager.newBuilder(prefabName);
        builder.updateComponent(LocationComponent.class, l -> {
            l.setWorldPosition(new Vector3f(location));
            return l;
        });
        builder.updateComponent(ParticleEmitterComponent.class, emitter -> {
            emitter.ownerEntity = entity;
            return emitter;
        });
        builder.setOwner(entity);
        return builder.build();
    }
}
