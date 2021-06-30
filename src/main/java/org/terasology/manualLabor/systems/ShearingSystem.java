// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.manualLabor.systems;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.core.Time;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.characters.events.AttackEvent;
import org.terasology.engine.logic.delay.DelayManager;
import org.terasology.engine.logic.delay.PeriodicActionTriggeredEvent;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.assets.material.Material;
import org.terasology.engine.rendering.assets.skeletalmesh.SkeletalMesh;
import org.terasology.engine.rendering.logic.SkeletalMeshComponent;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.gestalt.assets.management.AssetManager;
import org.terasology.manualLabor.components.ShearableComponent;

import java.util.Optional;

@RegisterSystem
public class ShearingSystem extends BaseComponentSystem {
    public static final String SHEARING_ITEM = "ManualLabor:CrudeShears";
    public static final int HAIR_REGROWTH_TIME = 3 * 60 * 1000; // 3 minutes in ms
    public static final String HAIR_REGROWTH_ACTION_ID = "ManualLabor:HairRegrowthAction";
    public static final String SHEARED_SHEEP_MESH = "WildAnimals:shearedSheep";
    public static final String SHEARED_SHEEP_MATERIAL = "WildAnimals:shearedSheepSkin";
    public static final String SHEEP_MESH = "WildAnimals:sheep";
    public static final String SHEEP_MATERIAL = "WildAnimals:sheepSkin";
    private static final Logger logger = LoggerFactory.getLogger(ShearingSystem.class);

    @In
    protected Time time;

    @In
    private AssetManager assetManager;

    @In
    private DelayManager delayManager;

    @ReceiveEvent(components = {ShearableComponent.class})
    public void onAttack(AttackEvent event, EntityRef entityRef) {
        ShearableComponent component = entityRef.getComponent(ShearableComponent.class);
        EntityRef heldItem = event.getDirectCause();
        Prefab parentPrefab = heldItem.getParentPrefab();
        if (parentPrefab != null && !component.sheared && parentPrefab.getUrn().equals(new ResourceUrn(SHEARING_ITEM))) {
            component.sheared = true;
            component.lastShearingTimestamp = time.getGameTimeInMs();
            delayManager.addPeriodicAction(entityRef, HAIR_REGROWTH_ACTION_ID, 0, HAIR_REGROWTH_TIME / 20);
            switchPrefab(entityRef, SHEARED_SHEEP_MESH, SHEARED_SHEEP_MATERIAL);
        }
    }

    @ReceiveEvent
    public void onPeriodicActionTriggered(PeriodicActionTriggeredEvent event, EntityRef entity) {
        if (event.getActionId().equals(HAIR_REGROWTH_ACTION_ID)) {
            ShearableComponent shearableComponent = entity.getComponent(ShearableComponent.class);
            if (shearableComponent.sheared && (time.getGameTimeInMs() - shearableComponent.lastShearingTimestamp) > HAIR_REGROWTH_TIME) {
                shearableComponent.sheared = false;
                delayManager.cancelPeriodicAction(entity, HAIR_REGROWTH_ACTION_ID);
                switchPrefab(entity, SHEEP_MESH, SHEEP_MATERIAL);
            }
        }
    }

    private void switchPrefab(EntityRef entity, String meshURI, String materialURI) {
        SkeletalMeshComponent skeletalMeshComponent = entity.getComponent(SkeletalMeshComponent.class);
        if (skeletalMeshComponent != null) {
            Optional<SkeletalMesh> skeletalMesh = assetManager.getAsset(meshURI,
                    SkeletalMesh.class);
            Optional<Material> shearedMaterial = assetManager.getAsset(materialURI,
                    Material.class);
            if (skeletalMesh.isPresent() && shearedMaterial.isPresent()) {
                skeletalMeshComponent.mesh = skeletalMesh.get();
                skeletalMeshComponent.material = shearedMaterial.get();
            }
            entity.saveComponent(skeletalMeshComponent);
        }
    }
}
