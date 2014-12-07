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

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.terasology.asset.Assets;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.prefab.PrefabManager;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.journal.DiscoveredNewJournalEntry;
import org.terasology.journal.JournalManager;
import org.terasology.journal.StaticJournalChapterHandler;
import org.terasology.journal.part.TextJournalPart;
import org.terasology.journal.part.TitleJournalPart;
import org.terasology.logic.players.event.OnPlayerSpawnedEvent;
import org.terasology.manualLabor.components.ManualLaborSubstanceDescriptionComponent;
import org.terasology.manualLabor.components.ToolModificationDescription;
import org.terasology.registry.In;
import org.terasology.rendering.nui.HorizontalAlign;
import org.terasology.substanceMatters.components.SubstanceComponent;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.block.BlockManager;

import java.util.List;

@RegisterSystem
public class JournalIntegration extends BaseComponentSystem {
    @In
    JournalManager journalManager;
    @In
    EntityManager entityManager;
    @In
    PrefabManager prefabManager;
    @In
    BlockManager blockManager;
    @In
    BlockEntityRegistry blockEntityRegistry;

    private String chapterId = "ManualLabor";

    @Override
    public void initialise() {
        super.initialise();

        StaticJournalChapterHandler chapterHandler = new StaticJournalChapterHandler();

        List<JournalManager.JournalEntryPart> introduction = Lists.newArrayList(
                new TitleJournalPart("Manual Labor"),
                new TextJournalPart("To Get started, get a "),
                new TitleJournalPart("Tools"),
                new ItemIconJournalPart("ManualLabor:Hammer", HorizontalAlign.LEFT),
                new TextJournalPart("Hammers are primarily a digging tool to dig through rock."),
                new TextJournalPart("When used as a crafting tool, they can flatten materials and can smash ores into smaller chunks that can then be smelted into metal."),
                new ItemIconJournalPart("ManualLabor:Pickaxe", HorizontalAlign.LEFT),
                new TextJournalPart("Pickaxes are a digging tool specializing in minerals."),
                new ItemIconJournalPart("ManualLabor:Shovel", HorizontalAlign.LEFT),
                new TextJournalPart("Shovels are a digging tool specializing in dirt."),
                new ItemIconJournalPart("ManualLabor:Axe", HorizontalAlign.LEFT),
                new TextJournalPart("Axes are a digging tool specializing in wood."),
                new ItemIconJournalPart("ManualLabor:MetalFile", HorizontalAlign.LEFT),
                new TextJournalPart("Metal files are used to sharpen and shape materials."),
                new ItemIconJournalPart("ManualLabor:Pliers", HorizontalAlign.LEFT),
                new TextJournalPart("Pliers are used for bending materials."),
                new ItemIconJournalPart("ManualLabor:Saw", HorizontalAlign.LEFT),
                new TextJournalPart("Saws are used for cutting materials."),
                new ItemIconJournalPart("ManualLabor:Screwdriver", HorizontalAlign.LEFT),
                new TextJournalPart("Screwdrivers are used for fastening materials together."),
                new ItemIconJournalPart("ManualLabor:Wrench", HorizontalAlign.LEFT),
                new TextJournalPart("Wrenches are used for working with machines.")
        );

        // add substances
        for (Prefab substance : prefabManager.listPrefabs(SubstanceComponent.class)) {
            SubstanceComponent substanceComponent = substance.getComponent(SubstanceComponent.class);
            ManualLaborSubstanceDescriptionComponent substanceDescriptionComponent = substance.getComponent(ManualLaborSubstanceDescriptionComponent.class);

            if (substanceDescriptionComponent != null) {
                // try and get a block of this substance
                introduction.add(new TitleJournalPart(substanceComponent.name));
                introduction.add(new ItemIconJournalPart(substanceDescriptionComponent.defaultItemTexture + "." + substance.getURI().toSimpleString(), HorizontalAlign.LEFT));
                if (!substanceComponent.description.isEmpty()) {
                    introduction.add(new TextJournalPart(substanceComponent.description));
                }
                if (!substanceDescriptionComponent.description.isEmpty()) {
                    introduction.add(new TextJournalPart(substanceDescriptionComponent.description));
                }

                for (ToolModificationDescription toolModificationDescription : Iterables.filter(substance.iterateComponents(), ToolModificationDescription.class)) {
                    introduction.add(new TextJournalPart(" - " + toolModificationDescription.getDescription()));
                }
            }
        }

        chapterHandler.registerJournalEntry("introduction", introduction);

        journalManager.registerJournalChapter(chapterId, Assets.getTexture("ManualLabor", "ManualLaborIcon"), "Manual Labor", chapterHandler);
    }


    @ReceiveEvent
    public void playerSpawned(OnPlayerSpawnedEvent event, EntityRef player) {
        player.send(new DiscoveredNewJournalEntry(chapterId, "introduction"));
    }
}

