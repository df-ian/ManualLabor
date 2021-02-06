// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.manualLabor.systems;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.health.event.BeforeDamagedEvent;
import org.terasology.manualLabor.components.BonusToolDamageComponent;
import org.terasology.nui.widgets.TooltipLine;
import org.terasology.rendering.nui.layers.ingame.inventory.GetItemTooltip;
import org.terasology.world.block.BlockComponent;
import org.terasology.world.block.entity.damage.BlockDamageModifierComponent;
import org.terasology.world.block.family.BlockFamily;

@RegisterSystem(RegisterMode.AUTHORITY)
public class BonusToolDamageSystem extends BaseComponentSystem {
    @ReceiveEvent(netFilter = RegisterMode.AUTHORITY)
    public void beforeDamage(BeforeDamagedEvent event, EntityRef entity, BlockComponent blockComponent) {
        if (event.getDamageType() != null) {
            BlockDamageModifierComponent blockDamage = event.getDamageType().getComponent(BlockDamageModifierComponent.class);
            BonusToolDamageComponent bonusToolDamageComponent = event.getDirectCause().getComponent(BonusToolDamageComponent.class);
            if (blockDamage != null && bonusToolDamageComponent != null) {
                BlockFamily blockFamily = blockComponent.block.getBlockFamily();
                for (String category : blockFamily.getCategories()) {
                    if (blockDamage.materialDamageMultiplier.containsKey(category)) {
                        event.add(bonusToolDamageComponent.baseDamage);
                    }
                }
            }
        }
    }


    @ReceiveEvent
    public void getDurabilityItemTooltip(GetItemTooltip event, EntityRef entity, BonusToolDamageComponent bonusToolDamageComponent) {
        event.getTooltipLines().add(new TooltipLine("Bonus Damage: " + bonusToolDamageComponent.baseDamage));
    }
}
