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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.books.logic.BookComponent;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.rendering.assets.texture.Texture;
import org.terasology.rendering.assets.texture.TextureRegion;
import org.terasology.rendering.nui.BaseInteractionScreen;
import org.terasology.rendering.nui.Color;
import org.terasology.rendering.nui.WidgetUtil;
import org.terasology.rendering.nui.databinding.Binding;
import org.terasology.rendering.nui.databinding.DefaultBinding;
import org.terasology.rendering.nui.widgets.UIImage;
import org.terasology.rendering.nui.widgets.UILabel;
import org.terasology.rendering.nui.widgets.UIText;
import org.terasology.utilities.Assets;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookScreen extends BaseInteractionScreen {
    private final Logger logger = LoggerFactory.getLogger(BookScreen.class);

    private BookComponent book;
    private List<String> pages;

    private UIImage coverLeft;
    private UIImage coverRight;
    private UIImage pageLeft;
    private UIImage pageRight;
    private UILabel textLeft;
    private UILabel textRight;

    private Binding<TextureRegion> coverBackL = new DefaultBinding<>(Assets.getTextureRegion("Books:book#interiorLeft").get());
    private Binding<TextureRegion> coverBackR = new DefaultBinding<>(Assets.getTextureRegion("Books:book#interiorRight").get());
    private Binding<TextureRegion> coverFrontL = new DefaultBinding<>(Assets.getTextureRegion("Books:book#exteriorLeft").get());
    private Binding<TextureRegion> coverFrontR = new DefaultBinding<>(Assets.getTextureRegion("Books:book#exteriorRight").get());
    private Binding<TextureRegion> pageL = new DefaultBinding<>(Assets.getTextureRegion("Books:book#pageLeft").get());
    private Binding<TextureRegion> pageR = new DefaultBinding<>(Assets.getTextureRegion("Books:book#pageRight").get());
    private Binding<TextureRegion> blank = new DefaultBinding<>(Assets.getTextureRegion("Books:blank").get());

    private Binding<Integer> index;
    private Binding<State> state;

    public BookScreen() {
    }

    @Override
    public void initialise() {
        index = new Binding<Integer>() {
            private Integer index = -1;

            @Override
            public Integer get() {
                return index;
            }

            @Override
            public void set(Integer value) {
                if ((value >= -1) && (value <= pages.size()))
                    index = value;
                else if (index < -1)
                    index = -1;
                else if (index > pages.size())
                    index = pages.size();
            }
        };

        state = new Binding<State>() {
            @Override
            public State get() {
                int i = index.get();

                if (i == -1) {
                    return State.CLOSED_RIGHT;
                } if (i == 0) {
                    return State.OPEN_RIGHT;
                } if (i == pages.size() - 1) {
                    return State.OPEN_LEFT;
                } if (i == pages.size())
                    return State.CLOSED_LEFT;
                return State.PAGES;
            }

            @Override
            public void set(State value) {

            }
        };

        coverLeft = find("coverLeft", UIImage.class);
        coverRight = find("coverRight", UIImage.class);
        pageLeft = find("pageLeft", UIImage.class);
        pageRight = find("pageRight", UIImage.class);
        textLeft = find("textLeft", UILabel.class);
        textRight = find("textRight", UILabel.class);


        WidgetUtil.trySubscribe(this, "forward", button -> {
            forward();
            updatePage();
        });

        WidgetUtil.trySubscribe(this, "backward", button -> {
            index.set(index.get() - 1);
            backward();
            updatePage();
        });
    }

    @Override
    protected void initializeWithInteractionTarget(EntityRef interactionTarget) {
        book = interactionTarget.getComponent(BookComponent.class);
        setTint(book.tint);

        pages = book.pages;

        textLeft.bindText(new Binding<String>() {
            @Override
            public String get() {
                if(getState().equals(State.OPEN_LEFT))
                    return pages.get(index.get());
                if(getState().equals(State.PAGES)) {
                    if(index.get() % 2 == 1)
                        return pages.get(index.get());
                    else
                        return pages.get(index.get()-1);
                }

                return "";
            }

            @Override
            public void set(String value) {

            }
        });

        textRight.bindText(new Binding<String>() {
            @Override
            public String get() {
                if(getState().equals(State.OPEN_RIGHT))
                    return pages.get(index.get());
                if(getState().equals(State.PAGES)) {
                    if(index.get() % 2 == 0)
                        return pages.get(index.get());
                    else
                        return pages.get(index.get()+1);
                }

                return "";
            }

            @Override
            public void set(String value) {
            }
        });

        updatePage();
    }

    public void setTint(Color color) {
        coverLeft.setTint(color);
        coverRight.setTint(color);
    }

    private void updatePage() {
        pageLeft.bindTexture(blank);
        pageRight.bindTexture(blank);

        if (getState().equals(State.CLOSED_RIGHT)) {
            coverRight.bindTexture(coverFrontR);
            coverLeft.bindTexture(blank);
        } else if (getState().equals(State.CLOSED_LEFT)) {
            coverRight.bindTexture(blank);
            coverLeft.bindTexture(coverFrontL);
        } else {
            coverLeft.bindTexture(coverBackL);
            coverRight.bindTexture(coverBackR);

            if (getState().equals(State.OPEN_RIGHT)) {
                pageRight.bindTexture(pageR);
            } else if (getState().equals(State.OPEN_LEFT)) {
                pageLeft.bindTexture(pageL);
            } else {
                pageLeft.bindTexture(pageL);
                pageRight.bindTexture(pageR);
            }
        }
    }

    private State getState() {
        return state.get();
    }

    private void forward() {
        index.set(index.get()+1);

        if(getState().equals(State.PAGES))
            index.set(index.get()+1);
    }

    private void backward() {
        index.set(index.get()-1);

        if(getState().equals(State.PAGES))
            index.set(index.get()-1);
    }
}

enum State {
    CLOSED_LEFT,
    OPEN_LEFT,
    PAGES,
    OPEN_RIGHT,
    CLOSED_RIGHT
}