/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.manualLabor.systems;

import com.google.common.collect.Sets;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.terasology.engine.entitySystem.entity.EntityBuilder;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.OnAddedComponent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.health.BeforeDestroyEvent;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.block.BlockComponent;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.manualLabor.components.LightAreaComponent;

import java.util.Set;

@RegisterSystem(RegisterMode.AUTHORITY)
public class CampFireAuthoritySystem extends BaseComponentSystem {
    @In
    EntityManager entityManager;

    @ReceiveEvent
    public void addLightArea(OnAddedComponent event, EntityRef entity, LightAreaComponent lightAreaComponent,
                             LocationComponent location, BlockComponent block) {
        Set<Vector3f> lightRelativePositions = Sets.newHashSet();
        lightRelativePositions.add(new Vector3f(0, 0, 0));

        for (Vector3f relativePosition : lightRelativePositions) {
            EntityBuilder renderedEntityBuilder = entityManager.newBuilder(lightAreaComponent.lightPrefab);
            renderedEntityBuilder.setOwner(entity);
            LocationComponent locationComponent = new LocationComponent();
            Vector3f worldPosition = new Vector3f(block.getPosition(new Vector3i()));
            worldPosition.add(relativePosition);
            locationComponent.setWorldPosition(worldPosition);
            renderedEntityBuilder.addComponent(locationComponent);
            EntityRef newLightEntity = renderedEntityBuilder.build();

            lightAreaComponent.lights.add(newLightEntity);
        }
        entity.saveComponent(lightAreaComponent);
    }

    @ReceiveEvent
    public void destroyLightArea(BeforeDestroyEvent event, EntityRef entityRef,
                                 LightAreaComponent lightAreaComponent, BlockComponent block) {
        for (EntityRef light : lightAreaComponent.lights) {
            light.destroy();
        }
    }
}
