/*
 * Copyright 2014 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.manualLabor.processParts;

import com.google.common.collect.Lists;
import org.terasology.asset.Assets;
import org.terasology.entityNetwork.Network;
import org.terasology.entityNetwork.NetworkNode;
import org.terasology.entityNetwork.systems.EntityNetworkManager;
import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.manualLabor.components.HeatBlockNetworkComponent;
import org.terasology.manualLabor.components.HeatedComponent;
import org.terasology.manualLabor.ui.OverlapLayout;
import org.terasology.registry.CoreRegistry;
import org.terasology.rendering.nui.widgets.UIImage;
import org.terasology.workstation.process.DescribeProcess;
import org.terasology.workstation.process.ProcessPart;
import org.terasology.workstation.process.ProcessPartDescription;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class HeatInputComponent implements Component, ProcessPart, DescribeProcess {

    @Override
    public boolean validateBeforeStart(EntityRef instigator, EntityRef workstation, EntityRef processEntity) {
        int heatSources = 0;
        EntityNetworkManager entityNetworkManager = CoreRegistry.get(EntityNetworkManager.class);
        // loop through all nodes this workstation is part of
        for (NetworkNode workstationNode : entityNetworkManager.getNodesForEntity(workstation)) {
            if (workstationNode.getNetworkId().equals(HeatBlockNetworkComponent.NETWORK_ID)) {
                for (Network network : entityNetworkManager.getNetworks(workstationNode)) {
                    // loop through each of the nodes on this network to see how many are heat sources
                    for (NetworkNode siblingNode : entityNetworkManager.getNetworkNodes(network)) {
                        EntityRef siblingEntity = entityNetworkManager.getEntityForNode(siblingNode);
                        if (siblingEntity.hasComponent(HeatedComponent.class)) {
                            heatSources++;
                        }
                    }
                }
            }
        }

        return heatSources > 0;
    }

    @Override
    public long getDuration(EntityRef instigator, EntityRef workstation, EntityRef processEntity) {
        return 0;
    }

    @Override
    public void executeStart(EntityRef instigator, EntityRef workstation, EntityRef processEntity) {
    }

    @Override
    public void executeEnd(EntityRef instigator, EntityRef workstation, EntityRef processEntity) {
    }

    @Override
    public Collection<ProcessPartDescription> getOutputDescriptions() {
        return Collections.emptyList();
    }

    @Override
    public Collection<ProcessPartDescription> getInputDescriptions() {
        List<ProcessPartDescription> descriptions = Lists.newLinkedList();
        String description = "Heat";
        UIImage image = new UIImage(Assets.getTextureRegion("ManualLabor:Manuallabor#Heat").get());
        OverlapLayout layout = new OverlapLayout();
        layout.addWidget(image);
        layout.setTooltip(description);
        layout.setTooltipDelay(0);
        descriptions.add(new ProcessPartDescription(null, description, layout));
        return descriptions;
    }
}
