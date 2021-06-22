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
import org.terasology.engine.logic.characters.CharacterHeldItemComponent;
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
        EntityRef player = event.getInstigator();
        CharacterHeldItemComponent characterHeldItemComponent = player.getComponent(CharacterHeldItemComponent.class);
        EntityRef heldItem = characterHeldItemComponent.selectedItem;
        Prefab parentPrefab = heldItem.getParentPrefab();
        if (parentPrefab != null) {
            if (!component.sheared && parentPrefab.getUrn().equals(new ResourceUrn(SHEARING_ITEM))) {
                component.sheared = true;
                component.lastShearingTimestamp = time.getGameTimeInMs();
                delayManager.addPeriodicAction(entityRef, HAIR_REGROWTH_ACTION_ID, 0, HAIR_REGROWTH_TIME / 20);
                SkeletalMeshComponent skeletalMeshComponent = entityRef.getComponent(SkeletalMeshComponent.class);
                Optional<SkeletalMesh> skeletalMesh = assetManager.getAsset("WildAnimals:shearedSheep",
                        SkeletalMesh.class);
                skeletalMesh.ifPresent(mesh -> skeletalMeshComponent.mesh = mesh);
                Optional<Material> shearedMaterial = assetManager.getAsset("WildAnimals:shearedSheepSkin",
                        Material.class);
                shearedMaterial.ifPresent(texture -> skeletalMeshComponent.material = shearedMaterial.get());
                entityRef.saveComponent(skeletalMeshComponent);
            }
        }
    }

    @ReceiveEvent
    public void onPeriodicActionTriggered(PeriodicActionTriggeredEvent event, EntityRef entity) {
        if (event.getActionId().equals(HAIR_REGROWTH_ACTION_ID)) {
            ShearableComponent shearableComponent = entity.getComponent(ShearableComponent.class);
            if (shearableComponent.sheared) {
                if ((time.getGameTimeInMs() - shearableComponent.lastShearingTimestamp) > HAIR_REGROWTH_TIME) {
                    shearableComponent.sheared = false;
                    delayManager.cancelPeriodicAction(entity, HAIR_REGROWTH_ACTION_ID);
                    SkeletalMeshComponent skeletalMeshComponent = entity.getComponent(SkeletalMeshComponent.class);
                    Optional<SkeletalMesh> skeletalMesh = assetManager.getAsset("WildAnimals:sheep",
                            SkeletalMesh.class);
                    skeletalMesh.ifPresent(mesh -> skeletalMeshComponent.mesh = mesh);
                    Optional<Material> sheepMaterial = assetManager.getAsset("WildAnimals:sheepSkin", Material.class);
                    sheepMaterial.ifPresent(texture -> skeletalMeshComponent.material = sheepMaterial.get());
                    entity.saveComponent(skeletalMeshComponent);
                }
            }
        }
    }
}