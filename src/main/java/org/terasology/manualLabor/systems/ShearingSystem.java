// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.manualLabor.systems;

import com.google.common.collect.Lists;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.audio.StaticSound;
import org.terasology.engine.audio.events.PlaySoundEvent;
import org.terasology.engine.core.Time;
import org.terasology.engine.entitySystem.entity.EntityBuilder;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.EventPriority;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.characters.events.AttackEvent;
import org.terasology.engine.logic.delay.DelayManager;
import org.terasology.engine.logic.delay.PeriodicActionTriggeredEvent;
import org.terasology.engine.logic.inventory.events.DropItemEvent;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.assets.material.Material;
import org.terasology.engine.rendering.assets.skeletalmesh.SkeletalMesh;
import org.terasology.engine.rendering.logic.SkeletalMeshComponent;
import org.terasology.engine.utilities.random.FastRandom;
import org.terasology.engine.utilities.random.Random;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.gestalt.assets.management.AssetManager;
import org.terasology.manualLabor.components.ShearableComponent;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This system is handling the logic for shearing. It currently is implemented mainly for sheep.
 * The event of shearing starts a sheep shearing cycle that first switches the model to a
 * sheared state and switches it back after 3 minutes to the non-sheared state.
 */
@RegisterSystem
public class ShearingSystem extends BaseComponentSystem {
    public static final String SHEARING_ITEM = "ManualLabor:CrudeShears";
    public static final int HAIR_REGROWTH_TIME = 3 * 60 * 1000; // 3 minutes in ms
    public static final String HAIR_REGROWTH_ACTION_ID = "ManualLabor:HairRegrowthAction";

    public static final String SHEARED_SHEEP_MESH = "WildAnimals:shearedSheep";
    public static final String SHEARED_SHEEP_MATERIAL = "WildAnimals:shearedSheepSkin";
    public static final String SHEEP_MESH = "WildAnimals:sheep";
    public static final String SHEEP_MATERIAL = "WildAnimals:sheepSkin";

    public static final List<String> SOUND_ASSETS = Lists.newArrayList("ManualLabor:Shearing1", "ManualLabor:Shearing2");
    public static final List<StaticSound> SHEARING_SOUNDS = Lists.newArrayList();
    public static final String PARTICLE_EFFECT = "ManualLabor:sheepShearingParticleEffect";

    private static final Logger logger = LoggerFactory.getLogger(ShearingSystem.class);
    private Random random = new FastRandom();

    @In
    protected Time time;

    @In
    private EntityManager entityManager;

    @In
    AssetManager assetManager;

    @In
    private DelayManager delayManager;

    @Override
    public void initialise() {
        super.initialise();
        SHEARING_SOUNDS.addAll(SOUND_ASSETS.stream()
                .map(urn -> assetManager.getAsset(urn, StaticSound.class))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList())
        );
    }

    /**
     * Checks pre-conditions for shearing (e.g. the right tool) and initiates the shearing cycle.
     *
     * @param entityRef Entity being sheared
     */
    @ReceiveEvent(components = {ShearableComponent.class}, priority = EventPriority.PRIORITY_HIGH)
    public void onShearing(AttackEvent event, EntityRef entityRef) {
        ShearableComponent component = entityRef.getComponent(ShearableComponent.class);
        EntityRef heldItem = event.getDirectCause();
        Prefab parentPrefab = heldItem.getParentPrefab();
        if (parentPrefab != null && !component.isSheared && parentPrefab.getUrn().equals(new ResourceUrn(SHEARING_ITEM))) {
            component.isSheared = true;
            component.lastShearingTimestamp = time.getGameTimeInMs();
            delayManager.addPeriodicAction(entityRef, HAIR_REGROWTH_ACTION_ID, 0, HAIR_REGROWTH_TIME / 20);
            switchPrefab(entityRef, SHEARED_SHEEP_MESH, SHEARED_SHEEP_MATERIAL);

            EntityBuilder dropEntity = entityManager.newBuilder(component.dropItemURI);
            Vector3f worldPosition = entityRef.getComponent(LocationComponent.class).getWorldPosition(new Vector3f());
            dropEntity.build().send(new DropItemEvent(worldPosition));

            EntityBuilder entityBuilder = entityManager.newBuilder(PARTICLE_EFFECT);
            LocationComponent locationComponent = entityBuilder.getComponent(LocationComponent.class);
            locationComponent.setWorldPosition(worldPosition);
            entityBuilder.build();

            entityRef.send(new PlaySoundEvent(random.nextItem(SHEARING_SOUNDS), 1));
            event.consume();
        }
    }

    /**
     * Periodically checks if enough time has elapsed for hair regrowth and executes events to regrow.
     *
     * @param entity Entity to regrow hair
     */
    @ReceiveEvent
    public void onPeriodicActionTriggered(PeriodicActionTriggeredEvent event, EntityRef entity) {
        if (event.getActionId().equals(HAIR_REGROWTH_ACTION_ID)) {
            ShearableComponent shearableComponent = entity.getComponent(ShearableComponent.class);
            if (shearableComponent.isSheared && (time.getGameTimeInMs() - shearableComponent.lastShearingTimestamp) > HAIR_REGROWTH_TIME) {
                shearableComponent.isSheared = false;
                delayManager.cancelPeriodicAction(entity, HAIR_REGROWTH_ACTION_ID);
                switchPrefab(entity, SHEEP_MESH, SHEEP_MATERIAL);
            }
        }
    }

    /**
     * Switches the model for the provided entity
     *
     * @param entity entity whose model is to be switched
     * @param meshURI mesh URI for the new model
     * @param materialURI material URI for the new model
     */
    void switchPrefab(EntityRef entity, String meshURI, String materialURI) {
        SkeletalMeshComponent skeletalMeshComponent = entity.getComponent(SkeletalMeshComponent.class);
        if (skeletalMeshComponent != null) {
            Optional<SkeletalMesh> skeletalMesh = assetManager.getAsset(meshURI,
                    SkeletalMesh.class);
            Optional<Material> shearedMaterial = assetManager.getAsset(materialURI,
                    Material.class);
            if (skeletalMesh.isPresent() && shearedMaterial.isPresent()) {
                skeletalMeshComponent.mesh = skeletalMesh.get();
                skeletalMeshComponent.material = shearedMaterial.get();
            } else {
                logger.warn("Model switch fail due to invalid assets");
            }
            entity.saveComponent(skeletalMeshComponent);
        }
    }
}
