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

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.OnChangedComponent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.In;
import org.terasology.entityNetwork.Network;
import org.terasology.entityNetwork.NetworkNode;
import org.terasology.entityNetwork.components.EntityNetworkComponent;
import org.terasology.entityNetwork.systems.EntityNetworkManager;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.manualLabor.components.HeatBlockNetworkComponent;
import org.terasology.manualLabor.components.HeatSourceComponent;
import org.terasology.manualLabor.components.HeatedComponent;
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

    public static float getAverageHeat(EntityRef entityRef, EntityNetworkManager entityNetworkManager) {
        int heatSources = 0;
        int heatSinks = 0;
        for (NetworkNode workstationNode : entityNetworkManager.getNodesForEntity(entityRef)) {
            if (workstationNode.getNetworkId().equals(HeatBlockNetworkComponent.NETWORK_ID)) {
                for (Network network : entityNetworkManager.getNetworks(workstationNode)) {
                    // loop through each of the nodes on this network to see how many are heat sources
                    for (NetworkNode siblingNode : entityNetworkManager.getNetworkNodes(network)) {
                        EntityRef siblingEntity = entityNetworkManager.getEntityForNode(siblingNode);
                        if (siblingEntity.hasComponent(HeatSourceComponent.class)) {
                            heatSources++;
                        }
                        if (siblingEntity.hasComponent(HeatedComponent.class)) {
                            heatSinks++;
                        }
                    }
                }
            }
        }

        return (float) (heatSources / (heatSinks + 1));
    }

    public static float getTotalHeat(EntityRef entityRef, EntityNetworkManager entityNetworkManager) {
        int heatSources = 0;
        for (NetworkNode workstationNode : entityNetworkManager.getNodesForEntity(entityRef)) {
            if (workstationNode.getNetworkId().equals(HeatBlockNetworkComponent.NETWORK_ID)) {
                for (Network network : entityNetworkManager.getNetworks(workstationNode)) {
                    // loop through each of the nodes on this network to see how many are heat sources
                    for (NetworkNode siblingNode : entityNetworkManager.getNetworkNodes(network)) {
                        EntityRef siblingEntity = entityNetworkManager.getEntityForNode(siblingNode);
                        if (siblingEntity.hasComponent(HeatSourceComponent.class)) {
                            heatSources++;
                        }
                    }
                }
            }
        }

        return (float) heatSources;
    }
}
