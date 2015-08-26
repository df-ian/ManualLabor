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

import org.terasology.entityNetwork.Network;
import org.terasology.entityNetwork.NetworkNode;
import org.terasology.entityNetwork.components.EntityNetworkComponent;
import org.terasology.entityNetwork.systems.EntityNetworkManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.entitySystem.entity.lifecycleEvents.OnChangedComponent;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.manualLabor.components.HeatedComponent;
import org.terasology.registry.In;
import org.terasology.workstation.event.WorkstationStateChanged;

@RegisterSystem(RegisterMode.AUTHORITY)
public class HeatAuthoritySystem extends BaseComponentSystem {
    @In
    EntityNetworkManager entityNetworkManager;

    @ReceiveEvent
    public void onHeatedChanged(OnChangedComponent event, EntityRef entity,
                                HeatedComponent heatedComponent,
                                EntityNetworkComponent entityNetworkComponent) {
        notifyConnectedWorkstations(entity);
    }

    @ReceiveEvent
    public void onHeatedActivated(OnActivatedComponent event, EntityRef entity,
                                  HeatedComponent heatedComponent,
                                  EntityNetworkComponent entityNetworkComponent) {
        notifyConnectedWorkstations(entity);
    }

    private void notifyConnectedWorkstations(EntityRef entityRef) {
        for (NetworkNode networkNode : entityNetworkManager.getNodesForEntity(entityRef)) {
            for (Network network : entityNetworkManager.getNetworks(networkNode)) {
                for (NetworkNode connectedNode : entityNetworkManager.getNetworkNodes(network)) {
                    EntityRef connectedEntityRef = entityNetworkManager.getEntityForNode(connectedNode);
                    if (!connectedEntityRef.equals(entityRef)) {
                        connectedEntityRef.send(new WorkstationStateChanged());
                    }
                }
            }
        }
    }
}
