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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.terasology.asset.Assets;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.journal.DiscoveredNewJournalEntry;
import org.terasology.journal.JournalManager;
import org.terasology.journal.StaticJournalChapterHandler;
import org.terasology.journal.part.TextJournalPart;
import org.terasology.journal.part.TitleJournalPart;
import org.terasology.logic.characters.CharacterComponent;
import org.terasology.logic.inventory.events.InventorySlotChangedEvent;
import org.terasology.logic.players.event.OnPlayerSpawnedEvent;
import org.terasology.registry.In;

import java.util.Arrays;
import java.util.List;

@RegisterSystem
public class JournalIntegration extends BaseComponentSystem {
    @In
    JournalManager journalManager;

    private String chapterId = "ManualLabor";
    private Multimap<String, String> dependencyMap = HashMultimap.create();

    @Override
    public void initialise() {
        super.initialise();

        StaticJournalChapterHandler chapterHandler = new StaticJournalChapterHandler();

        List<JournalManager.JournalEntryPart> introduction = Arrays.asList(
                new TitleJournalPart("Manual Labor"),
                new TextJournalPart("")
        );
        chapterHandler.registerJournalEntry("introduction", introduction);


        List<JournalManager.JournalEntryPart> overview = Arrays.asList(
                new TitleJournalPart("Smelting"),
                new TextJournalPart("Using a hearth,  you can heat ore to melting point to get metal nuggets.")

        );
        chapterHandler.registerJournalEntry("overview", overview);
        dependencyMap.put("overview", "introduction");

        journalManager.registerJournalChapter(chapterId, Assets.getTexture("ManualLabor", "ManualIcon"), "Manual Labor", chapterHandler);
    }


    private void discoveredEntry(EntityRef character, String entryId) {
        for (String dependentOn : dependencyMap.get(entryId)) {
            if (!journalManager.hasEntry(character, chapterId, dependentOn)) {
                discoveredEntry(character, dependentOn);
            }
        }
        if (!journalManager.hasEntry(character, chapterId, entryId)) {
            character.send(new DiscoveredNewJournalEntry(chapterId, entryId));
        }
    }


    @ReceiveEvent
    public void playerSpawned(OnPlayerSpawnedEvent event, EntityRef player) {
        player.send(new DiscoveredNewJournalEntry(chapterId, "introduction"));
    }

    @ReceiveEvent(components = {CharacterComponent.class})
    public void playerPickedUpItem(InventorySlotChangedEvent event, EntityRef character) {
    }
}

