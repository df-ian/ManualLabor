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

import org.terasology.math.Rect2i;
import org.terasology.math.Vector2i;
import org.terasology.rendering.assets.texture.TextureRegion;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.HorizontalAlign;
import org.terasology.rendering.nui.widgets.browser.data.ParagraphData;
import org.terasology.rendering.nui.widgets.browser.data.basic.flow.ContainerRenderSpace;
import org.terasology.rendering.nui.widgets.browser.ui.ParagraphRenderable;
import org.terasology.rendering.nui.widgets.browser.ui.style.ParagraphRenderStyle;

public class ImageParagraphData implements ParagraphData, ParagraphRenderable {
    private ParagraphRenderStyle paragraphRenderStyle;
    private TextureRegion textureRegion;
    private int width;
    private int height;

    public ImageParagraphData(ParagraphRenderStyle paragraphRenderStyle, TextureRegion textureRegion) {
        this(paragraphRenderStyle, textureRegion, textureRegion.getWidth(), textureRegion.getHeight());
    }

    public ImageParagraphData(ParagraphRenderStyle paragraphRenderStyle, TextureRegion textureRegion,
                              int width, int height) {
        this.paragraphRenderStyle = paragraphRenderStyle;
        this.textureRegion = textureRegion;
        this.width = width;
        this.height = height;
    }

    @Override
    public ParagraphRenderStyle getParagraphRenderStyle() {
        return paragraphRenderStyle;
    }

    @Override
    public ParagraphRenderable getParagraphContents() {
        return this;
    }

    @Override
    public void renderContents(Canvas canvas, Vector2i startPos, ContainerRenderSpace containerRenderSpace, int leftIndent, int rightIndent, ParagraphRenderStyle defaultStyle, HorizontalAlign horizontalAlign, HyperlinkRegister hyperlinkRegister) {
        int availableWidth = containerRenderSpace.getWidthForVerticalPosition(startPos.y);
        if (availableWidth - leftIndent - rightIndent >= width) {
            if (horizontalAlign == HorizontalAlign.LEFT || horizontalAlign == HorizontalAlign.CENTER) {
                availableWidth -= leftIndent;
            }
            if (horizontalAlign == HorizontalAlign.RIGHT || horizontalAlign == HorizontalAlign.CENTER) {
                availableWidth -= rightIndent;
            }

            int alignOffset = horizontalAlign.getOffset(width, availableWidth);
            canvas.drawTexture(textureRegion, Rect2i.createFromMinAndSize(alignOffset + containerRenderSpace.getAdvanceForVerticalPosition(startPos.y), startPos.y, width, height));
        } else {
            float ratio = 1f * height / width;
            int resultHeight = Math.round((availableWidth - leftIndent - rightIndent) * ratio);
            canvas.drawTexture(textureRegion, Rect2i.createFromMinAndSize(containerRenderSpace.getAdvanceForVerticalPosition(startPos.y), startPos.y, availableWidth, resultHeight));
        }
    }

    @Override
    public int getPreferredContentsHeight(ParagraphRenderStyle defaultStyle, int yStart, ContainerRenderSpace containerRenderSpace, int sideIndents) {
        int usableWidth = containerRenderSpace.getWidthForVerticalPosition(yStart) - sideIndents;
        if (usableWidth >= width) {
            return height;
        } else {
            float ratio = 1f * height / width;
            return Math.round(usableWidth * ratio);
        }
    }

    @Override
    public int getContentsMinWidth(ParagraphRenderStyle defaultStyle) {
        return width;
    }
}
