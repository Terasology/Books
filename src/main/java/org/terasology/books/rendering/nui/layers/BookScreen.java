/*
 * Copyright 2016 MovingBlocks
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

import org.terasology.books.logic.BookComponent;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.rendering.nui.BaseInteractionScreen;
import org.terasology.rendering.nui.WidgetUtil;

public class BookScreen extends BaseInteractionScreen {
    private BookComponent book;
    private UIBook gui;

    public BookScreen() {

    }

    @Override
    public void initialise() {
        gui = find("book", UIBook.class);

    }

    @Override
    protected void initializeWithInteractionTarget(EntityRef interactionTarget) {
        gui.setTint(interactionTarget.getComponent(BookComponent.class).tint);

        WidgetUtil.trySubscribe(this, "forward", button -> {
            State state = gui.getState();

            if (state.equals(State.CLOSED_LEFT)) {
                gui.setState(State.OPEN_LEFT);
            } else if (state.equals(State.OPEN_LEFT)) {
                gui.setState(State.PAGES);
            } else if (state.equals(State.PAGES)) {
                gui.setState(State.OPEN_RIGHT);
            } else if (state.equals(State.OPEN_RIGHT)) {
                gui.setState(State.CLOSED_RIGHT);
            }
        });

        WidgetUtil.trySubscribe(this, "backward", button -> {
            State state = gui.getState();

            if (state.equals(State.CLOSED_RIGHT)) {
                gui.setState(State.OPEN_RIGHT);
            } else if (state.equals(State.OPEN_RIGHT)) {
                gui.setState(State.PAGES);
            } else if (state.equals(State.PAGES)) {
                gui.setState(State.OPEN_LEFT);
            } else if (state.equals(State.OPEN_LEFT)) {
                gui.setState(State.CLOSED_LEFT);
            }
        });
    }
}
