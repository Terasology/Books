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
    private Binding<TextureRegion> interiorLeft = new DefaultBinding<>(Assets.getTextureRegion("Books:book#interiorLeft").get());
    private Binding<TextureRegion> interiorRight = new DefaultBinding<>(Assets.getTextureRegion("Books:book#interiorRight").get());
    private Binding<TextureRegion> exteriorLeft = new DefaultBinding<>(Assets.getTextureRegion("Books:book#exteriorLeft").get());
    private Binding<TextureRegion> exteriorRight = new DefaultBinding<>(Assets.getTextureRegion("Books:book#exteriorRight").get());
    private Binding<TextureRegion> pageLeft = new DefaultBinding<>(Assets.getTextureRegion("Books:book#pageLeft").get());
    private Binding<TextureRegion> pageRight = new DefaultBinding<>(Assets.getTextureRegion("Books:book#pageRight").get());

    private Binding<State> state = new DefaultBinding<>(State.PAGES);

    private Rect2i left = Rect2i.createFromMinAndSize(0, 0, 206, 200);
    private Rect2i right = Rect2i.createFromMinAndSize(206, 0, 206, 200);

    private Binding<Color> tint = new DefaultBinding<>(Color.WHITE);

    public UIBook() {
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (getState().equals(State.CLOSED_RIGHT)) {
            canvas.drawTexture(exteriorRight.get(), right, tint.get());
        } else if (getState().equals(State.CLOSED_LEFT)) {
            canvas.drawTexture(exteriorLeft.get(), left, tint.get());
        } else {
            canvas.drawTexture(interiorLeft.get(), left, tint.get());
            canvas.drawTexture(interiorRight.get(), right, tint.get());

            if (getState().equals(State.OPEN_RIGHT)) {
                canvas.drawTexture(pageRight.get(), right);
            } else if (getState().equals(State.OPEN_LEFT)) {
                canvas.drawTexture(pageLeft.get(), left);
            } else {
                canvas.drawTexture(pageLeft.get(), left);
                canvas.drawTexture(pageRight.get(), right);
            }
        }
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
        this.state.set(state);
    }

    public State getState() {
        return state.get();
    }

    public void bindState(Binding<State> stateBinding) {
        this.state = stateBinding;
    }
}


enum State {
    CLOSED_LEFT,
    OPEN_LEFT,
    PAGES,
    OPEN_RIGHT,
    CLOSED_RIGHT
}
