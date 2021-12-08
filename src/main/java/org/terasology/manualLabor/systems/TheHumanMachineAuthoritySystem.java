/*
 * Copyright 2014 MovingBlocks
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

import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.characters.CharacterComponent;
import org.terasology.engine.logic.players.event.OnPlayerSpawnedEvent;
import org.terasology.engine.registry.In;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.manualLabor.components.TheHumanMachineComponent;

@RegisterSystem(RegisterMode.AUTHORITY)
public class TheHumanMachineAuthoritySystem extends BaseComponentSystem {
    @In
    EntityManager entityManager;

    @ReceiveEvent(components = CharacterComponent.class)
    public void onPlayerSpawn(OnPlayerSpawnedEvent event, EntityRef player) {
        addTheHumanMachine(player);
    }

    private TheHumanMachineComponent addTheHumanMachine(EntityRef player) {
        EntityRef machineEntity = entityManager.create("TheHumanMachine");
        TheHumanMachineComponent theHumanAutomaticProcessingComponent = new TheHumanMachineComponent();
        theHumanAutomaticProcessingComponent.machineEntity = machineEntity;
        player.addComponent(theHumanAutomaticProcessingComponent);
        return theHumanAutomaticProcessingComponent;
    }
}
