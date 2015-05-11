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
import org.terasology.journal.BrowserJournalChapterHandler;
import org.terasology.journal.DefaultDocumentData;
import org.terasology.journal.DiscoveredNewJournalEntry;
import org.terasology.journal.JournalManager;
import org.terasology.logic.inventory.ItemComponent;
import org.terasology.logic.players.event.OnPlayerSpawnedEvent;
import org.terasology.manualLabor.components.ManualLaborSubstanceDescriptionComponent;
import org.terasology.manualLabor.components.ToolModificationDescription;
import org.terasology.registry.In;
import org.terasology.rendering.assets.texture.TextureRegion;
import org.terasology.rendering.nui.HorizontalAlign;
import org.terasology.rendering.nui.widgets.browser.data.ImageParagraphData;
import org.terasology.rendering.nui.widgets.browser.data.ParagraphData;
import org.terasology.rendering.nui.widgets.browser.data.basic.FlowParagraphData;
import org.terasology.rendering.nui.widgets.browser.data.basic.HTMLLikeParser;
import org.terasology.rendering.nui.widgets.browser.ui.style.DocumentRenderStyle;
import org.terasology.rendering.nui.widgets.browser.ui.style.ParagraphRenderStyle;
import org.terasology.substanceMatters.components.SubstanceComponent;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.block.BlockManager;

import java.util.List;

@RegisterSystem
public class JournalIntegration extends BaseComponentSystem {
    private static final int IMAGE_INDENT_RIGHT = 3;

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

    private DocumentRenderStyle imageInsetRenderStyle =new DocumentRenderStyle() {
        @Override
        public Integer getDocumentIndentTop() {
            return 0;
        }

        @Override
        public Integer getDocumentIndentBottom() {
            return 3;
        }

        @Override
        public Integer getDocumentIndentLeft() {
            return 0;
        }

        @Override
        public Integer getDocumentIndentRight() {
            return IMAGE_INDENT_RIGHT;
        }
    };

    private ParagraphRenderStyle centerRenderStyle = new ParagraphRenderStyle() {
        @Override
        public HorizontalAlign getHorizontalAlignment() {
            return HorizontalAlign.CENTER;
        }
    };

    @Override
    public void preBegin() {
        BrowserJournalChapterHandler chapterHandler = new BrowserJournalChapterHandler();

        List<ParagraphData> introduction = Lists.newArrayList();
        addTitle(introduction, "Manual Labor");
        addText(introduction, "To get started, get a stick and a stone.  Sticks are found in branches of trees.  You must dig for stone, they are hiding under the dirt layer of the ground.  " +
                "After finding these ingredients, craft them into a crude hammer.  This hammer will help you dig down and find more ores to help create better tools with.<l>" +
                "Also, you can craft a stone tool with one block of stone. This will help you cut wood and other things.");
        addTitle(introduction, "Tools");

        addItemWithDescription(introduction, "ManualLabor:SledgeHammer", "Sledge hammers are primarily a digging tool to dig through rock.<l>" +
                "When used as a crafting tool, they can smash ores into small chunks that can then be smelted into metal.");

        addItemWithDescription(introduction, "ManualLabor:Pickaxe", "Pickaxes are a digging tool specializing in minerals.");
        addItemWithDescription(introduction, "ManualLabor:Shovel", "Shovels are a digging tool specializing in dirt.");
        addItemWithDescription(introduction, "ManualLabor:Axe", "Axes are a digging tool specializing in wood.");
        addItemWithDescription(introduction, "ManualLabor:Mallet", "Mallets are used for assembling various items and tools.  Also, they can be used to grind chunks of material into dust.");
        addItemWithDescription(introduction, "ManualLabor:MetalFile", "Metal files are used to sharpen and shape materials.");
        addItemWithDescription(introduction, "ManualLabor:Pliers", "Pliers are used for bending materials.");
        addItemWithDescription(introduction, "ManualLabor:Saw", "Saws are used for cutting materials.");
        addItemWithDescription(introduction, "ManualLabor:Screwdriver", "Screwdrivers are used for fastening materials together.");
        addItemWithDescription(introduction, "ManualLabor:Wrench", "Wrenches are used for working with machines.");

        addTitle(introduction, "Blocks");
        addText(introduction, "Assembly tables are crafted with a block of wood, this is the basis for further crafting.");
        addText(introduction, "Tool assembly tables help craft tools from components. You can craft one from an existing assembly table.");
        addText(introduction, "The hearth is for melting down crushed ores to make metals for crafting. " +
                "You can craft one from an existing fireplace with some additional stone.");
        addText(introduction, "Fireplaces heat up hearths to high temperatures so that metals can be melted. You can craft one from a bunch of stone blocks.");
        addText(introduction, "Sifters can separate out impurities from crushed ores using water.");

        // add substances
        for (Prefab substance : prefabManager.listPrefabs(SubstanceComponent.class)) {
            SubstanceComponent substanceComponent = substance.getComponent(SubstanceComponent.class);
            ManualLaborSubstanceDescriptionComponent substanceDescriptionComponent = substance.getComponent(ManualLaborSubstanceDescriptionComponent.class);

            if (substanceDescriptionComponent != null) {
                // try and get a block of this substance
                addTitle(introduction, substanceComponent.name);

                StringBuilder description = new StringBuilder();
                if (!substanceComponent.description.isEmpty()) {
                    description.append(HTMLLikeParser.encodeHTMLLike(substanceComponent.description));
                    description.append("<l>");
                }
                if (!substanceDescriptionComponent.description.isEmpty()) {
                    description.append(HTMLLikeParser.encodeHTMLLike(substanceDescriptionComponent.description));
                    description.append("<l>");
                }

                for (ToolModificationDescription toolModificationDescription : Iterables.filter(substance.iterateComponents(), ToolModificationDescription.class)) {
                    description.append(HTMLLikeParser.encodeHTMLLike(" - " + toolModificationDescription.getDescription()));
                    description.append("<l>");
                }

                addItemWithDescription(introduction, substanceDescriptionComponent.defaultItemTexture + "." + substance.getURI().toSimpleString(),
                        description.toString());
            }
        }

        addTitle(introduction, "(Scroll to the top for more instructions)");

        chapterHandler.registerJournalEntry("introduction", introduction);

        journalManager.registerJournalChapter(chapterId, Assets.getTexture("ManualLabor", "ManualLaborIcon"), "Manual Labor", chapterHandler);
    }

    private void addItemWithDescription(List<ParagraphData> introduction, String itemUri, String itemDescription) {
        DefaultDocumentData imageDocumentData = new DefaultDocumentData(imageInsetRenderStyle);
        TextureRegion textureRegion = getTextureRegion(itemUri);
        imageDocumentData.addParagraph(new ImageParagraphData(null, textureRegion));

        FlowParagraphData flowParagraphData = new FlowParagraphData(null, imageDocumentData, textureRegion.getWidth() + IMAGE_INDENT_RIGHT, true);
        flowParagraphData.append(HTMLLikeParser.parseHTMLLike(itemDescription));
        introduction.add(flowParagraphData);
    }

    private TextureRegion getTextureRegion(String itemUri) {
        EntityRef item = entityManager.create(itemUri);
        ItemComponent itemComponent = item.getComponent(ItemComponent.class);
        TextureRegion texture = itemComponent.icon;
        item.destroy();
        return texture;
    }

    private void addText(List<ParagraphData> introduction, String text) {
        introduction.add(HTMLLikeParser.parseHTMLLikeParagraph(null, text));
    }

    private void addTitle(List<ParagraphData> introduction, String title) {
        introduction.add(HTMLLikeParser.parseHTMLLikeParagraph(centerRenderStyle, "<f engine:title>" + title + "</f>"));
    }


    @ReceiveEvent
    public void playerSpawned(OnPlayerSpawnedEvent event, EntityRef player) {
        player.send(new DiscoveredNewJournalEntry(chapterId, "introduction"));
    }
}

