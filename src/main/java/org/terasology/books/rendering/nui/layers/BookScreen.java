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
import org.terasology.rendering.nui.databinding.Binding;
import org.terasology.rendering.nui.databinding.DefaultBinding;
import org.terasology.rendering.nui.widgets.UIText;

public class BookScreen extends BaseInteractionScreen {
    private BookComponent book;
    private UIBook gui;
    private UIText text;

    private Binding<Integer> index;
    private Binding<State> state;

    public BookScreen() {
    }

    @Override
    public void initialise() {
        index = new Binding<Integer>() {
            private Integer index = -2;

            @Override
            public Integer get() {
                return index;
            }

            @Override
            public void set(Integer value) {
                if ((value >= -2) && (value <= book.pages.size()))
                    index = value;
                else if (index < -2)
                    index = -2;
                else if (index > book.pages.size())
                    index = book.pages.size();
            }
        };

        state = new Binding<State>() {
            @Override
            public State get() {
                int i = index.get();

                if (i == -2)
                    return State.CLOSED_RIGHT;
                if (i == -1)
                    return State.OPEN_RIGHT;
                if (i == book.pages.size()-1)
                    return State.OPEN_LEFT;
                if (i == book.pages.size())
                    return State.CLOSED_LEFT;
                return State.PAGES;
            }

            @Override
            public void set(State value) {

            }
        };


        gui = find("book", UIBook.class);

        WidgetUtil.trySubscribe(this, "forward", button -> { setPage(index.get() + 1); });

        WidgetUtil.trySubscribe(this, "backward", button -> { setPage(index.get() - 1); });
    }

    @Override
    protected void initializeWithInteractionTarget(EntityRef interactionTarget) {
        book = interactionTarget.getComponent(BookComponent.class);
        gui.setTint(book.tint);
        gui.bindState(state);
    }

    public void setPage(int page) {
        index.set(page);

    }
}
