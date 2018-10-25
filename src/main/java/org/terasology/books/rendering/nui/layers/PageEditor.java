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
package org.terasology.books.rendering.nui.layers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.registry.In;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.rendering.nui.widgets.UIButton;
import org.terasology.rendering.nui.widgets.UIText;

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
                if (BookScreen.getState().equals(State.OPEN_RIGHT)) {
                    BookScreen.pages.set(BookScreen.index.get(), pageText.getText());
                } else if (BookScreen.getState().equals(State.PAGES)) {
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