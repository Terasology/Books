// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
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
