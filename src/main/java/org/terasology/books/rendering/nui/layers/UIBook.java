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

import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2i;
import org.terasology.rendering.assets.texture.TextureRegion;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.Color;
import org.terasology.rendering.nui.CoreWidget;
import org.terasology.rendering.nui.databinding.Binding;
import org.terasology.rendering.nui.databinding.DefaultBinding;
import org.terasology.utilities.Assets;


public class UIBook extends CoreWidget {
    private TextureRegion interiorLeft = Assets.getTextureRegion("Books:book#interiorLeft").get();
    private TextureRegion interiorRight = Assets.getTextureRegion("Books:book#interiorRight").get();
    private TextureRegion exteriorLeft = Assets.getTextureRegion("Books:book#exteriorLeft").get();
    private TextureRegion exteriorRight = Assets.getTextureRegion("Books:book#exteriorRight").get();
    private TextureRegion pageLeft = Assets.getTextureRegion("Books:book#pageLeft").get();
    private TextureRegion pageRight = Assets.getTextureRegion("Books:book#pageRight").get();

    private State state = State.CLOSED_LEFT;

    private Rect2i left = Rect2i.createFromMinAndSize(0, 0, 206, 200);
    private Rect2i right = Rect2i.createFromMinAndSize(206, 0, 206, 200);

    private Binding<Color> tint = new DefaultBinding<>(Color.WHITE);

    public UIBook() {
    }

    public UIBook(String id) {
        super(id);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (state.equals(State.CLOSED_RIGHT)) {
            canvas.drawTexture(exteriorRight, right, tint.get());
            return;
        }

        if (state.equals(State.CLOSED_LEFT)) {
            canvas.drawTexture(exteriorLeft, left, tint.get());
            return;
        }

        canvas.drawTexture(interiorLeft, left, tint.get());
        canvas.drawTexture(interiorRight, right, tint.get());

        if (state.equals(State.OPEN_RIGHT)) {
            canvas.drawTexture(pageRight, right);
            return;
        }
        if (state.equals(State.OPEN_LEFT)) {
            canvas.drawTexture(pageLeft, left);
            return;
        }

        canvas.drawTexture(pageLeft);
        canvas.drawTexture(pageRight, right);


    }

    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i sizeHint) {
        return new Vector2i(412, 200);
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

    public State getState() {
        return state;
    }
}


enum State {
    CLOSED_LEFT,
    OPEN_LEFT,
    PAGES,
    OPEN_RIGHT,
    CLOSED_RIGHT
}