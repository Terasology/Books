/*
 * Copyright 2017 MovingBlocks
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
package org.terasology.books;

import org.terasology.engine.rendering.nui.widgets.browser.data.DocumentData;
import org.terasology.engine.rendering.nui.widgets.browser.data.ParagraphData;
import org.terasology.engine.rendering.nui.widgets.browser.ui.style.DocumentRenderStyle;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Holds the data for a page of a book, consisting of a list of paragraphs.
 */
public class DefaultDocumentData implements DocumentData {
    private DocumentRenderStyle documentRenderStyle;
    private List<ParagraphData> paragraphs = new LinkedList<>();

    /**
     * Creates a new instance with the specified render style.
     * 
     * @param documentRenderStyle The render style to use.
     */
    public DefaultDocumentData(DocumentRenderStyle documentRenderStyle) {
        this.documentRenderStyle = documentRenderStyle;
    }

    /**
     * Gets the render style of this page.
     * 
     * @return The render style.
     */
    @Override
    public DocumentRenderStyle getDocumentRenderStyle() {
        return documentRenderStyle;
    }

    /**
     * Gets the paragraphs on this page.
     * 
     * @return an unmodifiable list of the paragraphs on this page.
     */
    @Override
    public Collection<ParagraphData> getParagraphs() {
        return Collections.unmodifiableList(paragraphs);
    }

    /**
     * Adds another paragraph to this page.
     * 
     * @param paragraph The paragraph to add.
     */
    public void addParagraph(ParagraphData paragraph) {
        paragraphs.add(paragraph);
    }

    /**
     * Adds several paragraphs to this page.
     * 
     * @param paragraphsToAdd A collection of paragraphs to add.
     */
    public void addParagraphs(Collection<ParagraphData> paragraphsToAdd) {
        if (paragraphsToAdd != null) {
            paragraphs.addAll(paragraphsToAdd);
        }
    }
}
