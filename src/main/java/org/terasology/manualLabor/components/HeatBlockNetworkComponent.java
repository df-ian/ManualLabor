// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.manualLabor.components;

import org.terasology.engine.world.block.ForceBlockActive;
import org.terasology.entityNetwork.components.BlockLocationNetworkNodeComponent;
import org.terasology.gestalt.entitysystem.component.Component;

@ForceBlockActive
public class HeatBlockNetworkComponent extends BlockLocationNetworkNodeComponent implements Component<BlockLocationNetworkNodeComponent> {
    public static final String NETWORK_ID = "ManualLabor:Heat";

    public HeatBlockNetworkComponent() {
        networkId = NETWORK_ID;
    }
}
