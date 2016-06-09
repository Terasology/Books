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
package org.terasology.books.rendering.gui.windows;

import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2i;
import org.terasology.rendering.assets.texture.Texture;
import org.terasology.rendering.assets.texture.TextureRegion;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.Color;
import org.terasology.rendering.nui.CoreWidget;
import org.terasology.rendering.nui.LayoutConfig;
import org.terasology.rendering.nui.databinding.Binding;
import org.terasology.rendering.nui.databinding.DefaultBinding;
import org.terasology.utilities.Assets;

/**
 * Created by Jared Johnson on 6/7/2016.
 */
public class UIBook extends CoreWidget {
    private TextureRegion interior_left = Assets.getTextureRegion("Books:book#interiorLeft").get();
    private TextureRegion interior_right = Assets.getTextureRegion("Books:book#interiorRight").get();
    private TextureRegion exterior_left = Assets.getTextureRegion("Books:book#exteriorLeft").get();
    private TextureRegion exterior_right = Assets.getTextureRegion("Books:book#exteriorRight").get();
    private TextureRegion page_left = Assets.getTextureRegion("Books:book#pageLeft").get();
    private TextureRegion page_right = Assets.getTextureRegion("Books:book#pageRight").get();

    private State state = State.CLOSED_LEFT;

    private Rect2i left = Rect2i.createFromMinAndSize(0,0,206,200);
    private Rect2i right = Rect2i.createFromMinAndSize(206,0,206,200);

    private Binding<Color> tint = new DefaultBinding<>(Color.WHITE);

    public UIBook() {
    }

    public UIBook(String id) {
        super(id);
    }


    @Override
    public void onDraw(Canvas canvas) {
        if(state.equals(State.CLOSED_RIGHT)) {
            canvas.drawTexture(exterior_right, right, tint.get());
            return;
        }

        if(state.equals(State.CLOSED_LEFT)) {
            canvas.drawTexture(exterior_left, left, tint.get());
            return;
        }

        canvas.drawTexture(interior_left, left, tint.get());
        canvas.drawTexture(interior_right, right, tint.get());

        if(state.equals(State.OPEN_RIGHT)) {
            canvas.drawTexture(page_right, right);
            return;
        }
        if(state.equals(State.OPEN_LEFT)) {
            canvas.drawTexture(page_left, left);
            return;
        }

        canvas.drawTexture(page_left);
        canvas.drawTexture(page_right, right);


    }

    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i sizeHint) {
        return new Vector2i(412,200);
    }

    public Color getTint() {
        return tint.get();
    }

    public void setTint(Color color) {
        this.tint.set(color);
    }

    public void bindTint(Binding<Color> binding) {
        this.tint = binding;
    }

    public void setState(State state) {
        this.state = state;
    }

    public State getState() { return state; }
}

enum State {
    CLOSED_LEFT,
    OPEN_LEFT,
    PAGES,
    OPEN_RIGHT,
    CLOSED_RIGHT
}