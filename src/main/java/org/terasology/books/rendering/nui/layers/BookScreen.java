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

import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.books.logic.BookComponent;
import org.terasology.books.logic.EditBooksComponent;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.prefab.PrefabManager;
import org.terasology.logic.clipboard.ClipboardManager;
import org.terasology.logic.common.DisplayNameComponent;
import org.terasology.logic.inventory.InventoryUtils;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.registry.In;
import org.terasology.rendering.assets.texture.TextureRegion;
import org.terasology.rendering.nui.BaseInteractionScreen;
import org.terasology.rendering.nui.Color;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.rendering.nui.WidgetUtil;
import org.terasology.rendering.nui.databinding.Binding;
import org.terasology.rendering.nui.databinding.DefaultBinding;
import org.terasology.rendering.nui.widgets.UIButton;
import org.terasology.rendering.nui.widgets.UIImage;
import org.terasology.rendering.nui.widgets.UILabel;
import org.terasology.rendering.nui.widgets.UIText;
import org.terasology.utilities.Assets;

import java.util.ArrayList;
import java.util.List;

/**
 * A Screen class that displays a book.
 */
public class BookScreen extends BaseInteractionScreen {
    private static final String STATUS_EDITING = "Editing";
    private static final String STATUS_READING = "Reading";
    private static final String STATUS_READ_ONLY = "Read-only";

    private final Logger logger = LoggerFactory.getLogger(BookScreen.class);

    @In
    private NUIManager nuiManager;
    @In
    private LocalPlayer localPlayer;
    @In
    private ClipboardManager clipboardManager;
    @In
    private PrefabManager prefabManager;
    @In
    private EntityManager entityManager;

    private BookComponent book;
    private EntityRef bookEntity;
    private List<String> pages;
    private String status;

    private UIImage coverLeft;
    private UIImage coverRight;
    private UIImage pageLeft;
    private UIImage pageRight;
    private UIText textLeft;
    private UIText textRight;
    private UIButton arrowForward;
    private UIButton arrowBackward;

    private UIButton copy;
    private UIButton save;
    private UIButton cancel;
    private UIButton deleteLeft;
    private UIButton deleteRight;
    private UIButton addPage;
    private UILabel statusText;
    private UILabel title;

    private Binding<TextureRegion> coverBackL = new DefaultBinding<>(Assets.getTextureRegion("Books:book#interiorLeft").get());
    private Binding<TextureRegion> coverBackR = new DefaultBinding<>(Assets.getTextureRegion("Books:book#interiorRight").get());
    private Binding<TextureRegion> coverFrontL = new DefaultBinding<>(Assets.getTextureRegion("Books:book#exteriorLeft").get());
    private Binding<TextureRegion> coverFrontR = new DefaultBinding<>(Assets.getTextureRegion("Books:book#exteriorRight").get());
    private Binding<TextureRegion> pageL = new DefaultBinding<>(Assets.getTextureRegion("Books:book#pageLeft").get());
    private Binding<TextureRegion> pageR = new DefaultBinding<>(Assets.getTextureRegion("Books:book#pageRight").get());
    private Binding<TextureRegion> blank = new DefaultBinding<>(Assets.getTextureRegion("Books:blank").get());

    private Binding<Integer> index;

    public BookScreen() {
    }

    /**
     * This method initializes the class by setting the widget-related fields. Also adds the required listeners.
     */
    @Override
    public void initialise() {
        // index will always be that of the left page when State.PAGES
        index = new Binding<Integer>() {
            private Integer index = -1;

            @Override
            public Integer get() {
                return index;
            }

            @Override
            public void set(Integer value) {
                if ((value >= -1) && (value <= pages.size())) {
                    index = value;
                } else if (index < -1) {
                    index = -1;
                } else if (index > pages.size()) {
                    index = pages.size();
                }
            }
        };

        title = find("title", UILabel.class);
        coverLeft = find("coverLeft", UIImage.class);
        coverRight = find("coverRight", UIImage.class);
        pageLeft = find("pageLeft", UIImage.class);
        pageRight = find("pageRight", UIImage.class);
        textLeft = find("textLeft", UIText.class);
        textRight = find("textRight", UIText.class);
        arrowForward = find("forward", UIButton.class);
        arrowBackward = find("backward", UIButton.class);

        save = find("save", UIButton.class);
        cancel = find("cancel", UIButton.class);
        copy = find("copy", UIButton.class);
        deleteLeft = find("deleteLeft", UIButton.class);
        addPage = find("addPage", UIButton.class);
        deleteRight = find("deleteRight", UIButton.class);
        statusText = find("status", UILabel.class);

        WidgetUtil.trySubscribe(this, "forward", button -> {
            updateEdits();
            forward();
            updateEditingControls();
            updatePage();
        });

        WidgetUtil.trySubscribe(this, "backward", button -> {
            updateEdits();
            backward();
            updateEditingControls();
            updatePage();
        });

        cancel.subscribe(button -> nuiManager.closeScreen(this));

        save.subscribe(button -> {
            updateEdits();
            if (pages != null) {
                book.pages = new ArrayList<String>(pages);
            }
            bookEntity.saveComponent(book);
            nuiManager.closeScreen(this);
        });

        deleteLeft.subscribe(button -> {
            updateEdits();
            if (getState().equals(State.PAGES)) {
                pages.remove(index.get() - 1);
                pages.remove(index.get() - 1);
            } else if (getState().equals(State.OPEN_LEFT)) {
                pages.remove(index.get() - 1);
                pages.remove(index.get() - 1);
            }
            index.set(Math.max(index.get() - 2, 0));
            updateEditingControls();
            updatePage();
        });

        deleteRight.subscribe(button -> {
            updateEdits();
            if (getState().equals(State.PAGES)) {
                pages.remove(index.get() + 1);
                pages.remove(index.get() + 1);
            } else if (getState().equals(State.OPEN_RIGHT)) {
                pages.remove(index.get().intValue());
                pages.remove(index.get().intValue());
            }
            updateEditingControls();
            updatePage();
        });

        addPage.subscribe(button -> {
            updateEdits();
            if (getState().equals(State.OPEN_RIGHT)) {
                pages.add(index.get(), "");
                pages.add(index.get(), "");
            } else if (getState().equals(State.OPEN_LEFT)) {
                pages.add("");
                pages.add("");
            } else if (getState().equals(State.PAGES)) {
                pages.add(index.get() + 1, "");
                pages.add(index.get() + 1, "");
            }
            updateEditingControls();
            updatePage();
        });

        copy.subscribe(button -> {
            updateEdits();
            clipboardManager.setClipboardContents(buildPrefab());
        });
    }

    /**
     * Sets the index to the default value. This ensures the book will open on the default page.
     */
    @Override
    public void onClosed() {
        super.onClosed();
        index.set(-1);
    }

    @Override
    protected void initializeWithInteractionTarget(EntityRef interactionTarget) {
        bookEntity = interactionTarget;
        book = interactionTarget.getComponent(BookComponent.class);
        setTint(book.tint);

        pages = new ArrayList<>(book.pages);

        initEditingControls();
        updateEditingControls();
        updatePage();
    }

    private void initEditingControls() {
        boolean editBook = false;
        setEditable(false);

        //check for item in inventory
        EntityRef character = localPlayer.getCharacterEntity();
        for (int i = 0; i < InventoryUtils.getSlotCount(character); i++) {
            if (InventoryUtils.getItemAt(character, i).hasComponent(EditBooksComponent.class)) {
                editBook = true;
            }
        }
        if (editBook && !book.readOnly) {
            status = STATUS_EDITING;
        } else if (editBook && book.readOnly) {
            status = STATUS_READ_ONLY;
        } else {
            status = STATUS_READING;
        }
    }

    /**
     * Sets the tint ({@link Color}) of the book's cover.
     * @param color The color which the book cover should have.
     */
    public void setTint(Color color) {
        coverLeft.setTint(color);
        coverRight.setTint(color);
    }

    private void setEditable(boolean editable) {
        save.setVisible(editable);
        copy.setVisible(editable);
        cancel.setVisible(editable);
        deleteLeft.setVisible(editable);
        addPage.setVisible(editable);
        deleteRight.setVisible(editable);

        textRight.setReadOnly(!editable);
        textLeft.setReadOnly(!editable);
    }

    private State getState() {
        int i = index.get();

        if (i == -1) {
            return State.CLOSED_RIGHT;
        }
        if (i == 0) {
            return State.OPEN_RIGHT;
        }
        if (i == pages.size() - 1) {
            return State.OPEN_LEFT;
        }
        if (i == pages.size()) {
            return State.CLOSED_LEFT;
        }
        return State.PAGES;
    }

    private String getTextLeft() {
        if (getState().equals(State.OPEN_LEFT)) {
            return pages.get(index.get());
        }
        if (getState().equals(State.PAGES)) {
            return pages.get(index.get());
        }

        return "";
    }

    private String getTextRight() {
        if (getState().equals(State.OPEN_RIGHT)) {
            return pages.get(index.get());
        }
        if (getState().equals(State.PAGES)) {
            return pages.get(index.get() + 1);
        }

        return "";
    }

    /**
     * Updates local array with any edits made to the pages by the player
     */
    private void updateEdits() {
        if (getState().equals(State.OPEN_RIGHT)) {
            pages.set(index.get(), textRight.getText());
        } else if (getState().equals(State.OPEN_LEFT)) {
            pages.set(index.get(), textLeft.getText());
        } else if (getState().equals(State.PAGES)) {
            pages.set(index.get(), textLeft.getText());
            pages.set(index.get() + 1, textRight.getText());
        }
    }

    private void updateEditingControls() {
        if (status.equals(STATUS_EDITING)) {
            setEditable(true);
            if (getState().equals(State.OPEN_RIGHT)) {
                textLeft.setReadOnly(true);
            } else if (getState().equals(State.OPEN_LEFT)) {
                textRight.setReadOnly(true);
            }
            if (getState().equals(State.OPEN_RIGHT)) {
                deleteLeft.setVisible(false);
            } else if (getState().equals(State.OPEN_LEFT)) {
                deleteRight.setVisible(false);
            } else if (getState().equals(State.CLOSED_LEFT) || getState().equals(State.CLOSED_RIGHT)) {
                setEditable(false);
            }
            if (pages.size() <= 2) {
                deleteRight.setVisible(false);
                deleteLeft.setVisible(false);
            }
        } else {
            setEditable(false);
        }

        if (getState().equals(State.CLOSED_RIGHT)) {
            statusText.setText(status);
        } else {
            statusText.setText("");
        }
    }

    private void updatePage() {
        pageLeft.bindTexture(blank);
        pageRight.bindTexture(blank);
        arrowForward.setVisible(true);
        arrowBackward.setVisible(true);
        title.setText("");

        textRight.setText(getTextRight());
        textLeft.setText(getTextLeft());

        if (getState().equals(State.CLOSED_RIGHT)) {
            coverRight.bindTexture(coverFrontR);
            coverLeft.bindTexture(blank);
            arrowBackward.setVisible(false);
            if (book.title != null) {
                title.setText(book.title);
            } else if (bookEntity.getComponent(DisplayNameComponent.class) != null) {
                DisplayNameComponent displayNameComponent = bookEntity.getComponent(DisplayNameComponent.class);
                String name = displayNameComponent.name;
                if (name != null) {
                    title.setText(name);
                } else {
                    // Books:book -> book
                    String prefabName = bookEntity.getParentPrefab().getName().split(":")[1];
                    String capitalisedName = Character.toUpperCase(prefabName.charAt(0)) + prefabName.substring(1);
                    title.setText(capitalisedName);
                }
            }
        } else if (getState().equals(State.CLOSED_LEFT)) {
            coverRight.bindTexture(blank);
            coverLeft.bindTexture(coverFrontL);
            arrowForward.setVisible(false);
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

    private String buildPrefab() {
        Joiner joiner = Joiner.on("\",\n                   \"");
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("    \"parent\": \"engine:iconItem\",\n");
        sb.append("    \"Item\": {\n");
        sb.append("        \"icon\": \"engine:items#brownBook\",\n");
        sb.append("        \"usage\": \"NONE\"\n");
        sb.append("    },\n");
        sb.append("    \"DisplayName\": {\n");
        sb.append("        \"name\": \"Book\"\n");
        sb.append("    },\n");
        sb.append("    \"Book\": {\n");
        sb.append("        \"pages\": [\"");
        sb.append(joiner.join(pages)).append("\"]\n");
        sb.append("    },\n");
        sb.append("    \"InteractionTarget\": {},\n");
        sb.append("    \"InteractionScreen\": {\n");
        sb.append("        \"screen\": \"Books:BookScreen\"\n");
        sb.append("    }\n");
        sb.append("}\n");

        return sb.toString();
    }

    private void forward() {
        if (getState().equals(State.PAGES)) {
            index.set(index.get() + 2);
        } else {
            index.set(index.get() + 1);
        }
    }

    private void backward() {
        index.set(index.get() - 1);

        if (getState().equals(State.PAGES)) {
            index.set(index.get() - 1);
        }
    }
}

/**
 * A simple enumeration containing different states the book can be in.
 */
enum State {
    CLOSED_LEFT,
    OPEN_LEFT,
    PAGES,
    OPEN_RIGHT,
    CLOSED_RIGHT
}
