// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.books.rendering.nui.layers;

import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.nui.CoreScreenLayer;
import org.terasology.engine.rendering.nui.NUIManager;
import org.terasology.nui.widgets.UIButton;
import org.terasology.nui.widgets.UIText;

/**
 * UI for editing pages.
 */
public class PageEditor extends CoreScreenLayer {

    @In
    private NUIManager nuiManager;

    private UIText pageText;
    private UIButton save;
    private UIButton exit;

    /**
     * Initializes the UI by attatching events to the save and exit buttons.
     */
    @Override
    public void initialise() {
        pageText = find("pageText", UIText.class);
        save = find("save", UIButton.class);
        exit = find("exit", UIButton.class);

        save.subscribe(button -> {
            if (BookScreen.leftPageEditing) {
                BookScreen.pages.set(BookScreen.index.get(), pageText.getText());
                BookScreen.updatePage();
                nuiManager.closeScreen(this);
            } else {
                if (BookScreen.getState().equals(BookScreen.State.OPEN_RIGHT)) {
                    BookScreen.pages.set(BookScreen.index.get(), pageText.getText());
                } else if (BookScreen.getState().equals(BookScreen.State.PAGES)) {
                    BookScreen.pages.set(BookScreen.index.get() + 1, pageText.getText());
                }
                BookScreen.updatePage();
                nuiManager.closeScreen(this);
            }
        });
        exit.subscribe(button -> nuiManager.closeScreen(this));
    }

    /**
     * Initializes the text in the UI to the current text of the page being edited.
     */
    @Override
    public void onOpened() {
        if (BookScreen.leftPageEditing) {
            pageText.setText(BookScreen.getTextLeft());
        } else {
            pageText.setText(BookScreen.getTextRight());
        }
    }
}
