// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.manualLabor.systems;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.core.Time;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.characters.events.AttackEvent;
import org.terasology.engine.logic.delay.DelayManager;
import org.terasology.engine.logic.delay.PeriodicActionTriggeredEvent;
import org.terasology.engine.logic.inventory.ItemComponent;
import org.terasology.engine.registry.In;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.manualLabor.components.ShearableComponent;
import org.terasology.wildAnimals.event.AnimalSpawnEvent;

import java.util.ArrayList;

@RegisterSystem
public class ShearingSystem extends BaseComponentSystem {
    public static final int HITS_TO_SHEAR = 5;
    public static final ArrayList<ItemComponent> SHEARING_ITEMS = null; //TODO yet to decide
    public static final int HAIR_REGROWTH_TIME = 3*60*1000; // In ms
    public static final String HAIR_REGROWTH_ACTION_ID = "ManualLabor:HairRegrowthAction";
    private static final Logger logger = LoggerFactory.getLogger(ShearingSystem.class);

    @In
    protected Time time;

    @In
    private DelayManager delayManager;

    @In
    private EntityManager entityManager;

    @ReceiveEvent
    public void onAnimalSpawn(AnimalSpawnEvent event, EntityRef entityRef) {
        if (entityRef.getParentPrefab().getUrn().equals(new ResourceUrn("WildAnimals:Sheep"))) {
            entityRef.addComponent(new ShearableComponent());
        }
    }

    @ReceiveEvent(components = {ShearableComponent.class})
    public void onAttack(AttackEvent event, EntityRef entityRef) {
        ShearableComponent component = entityRef.getComponent(ShearableComponent.class);
        if (!component.sheared) {
            int hits = component.hits;
            if (hits < HITS_TO_SHEAR) {
                component.hits++;
            } else {
                component.hits = 0;
                component.sheared = true;
                component.lastShearingTimestamp = time.getGameTimeInMs();
                delayManager.addPeriodicAction(entityRef, HAIR_REGROWTH_ACTION_ID, 0, HAIR_REGROWTH_TIME/20);
                //TODO switch model
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
                    //TODO switch model
                }
            }
        }
    }
}
