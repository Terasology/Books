// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.books.rendering.nui.layers;

import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.books.DefaultDocumentData;
import org.terasology.books.RecipeParagraph;
import org.terasology.books.logic.BookComponent;
import org.terasology.books.logic.BookRecipeComponent;
import org.terasology.books.logic.EditBooksComponent;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.entitySystem.prefab.PrefabManager;
import org.terasology.engine.logic.common.DisplayNameComponent;
import org.terasology.engine.logic.players.LocalPlayer;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.nui.BaseInteractionScreen;
import org.terasology.engine.rendering.nui.NUIManager;
import org.terasology.engine.rendering.nui.widgets.browser.data.DocumentData;
import org.terasology.engine.rendering.nui.widgets.browser.data.ParagraphData;
import org.terasology.engine.rendering.nui.widgets.browser.data.basic.HTMLLikeParser;
import org.terasology.engine.rendering.nui.widgets.browser.ui.BrowserWidget;
import org.terasology.engine.utilities.Assets;
import org.terasology.module.inventory.systems.InventoryUtils;
import org.terasology.nui.Color;
import org.terasology.nui.UITextureRegion;
import org.terasology.nui.WidgetUtil;
import org.terasology.nui.databinding.Binding;
import org.terasology.nui.databinding.DefaultBinding;
import org.terasology.nui.widgets.UIButton;
import org.terasology.nui.widgets.UIImage;
import org.terasology.nui.widgets.UILabel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

/**
 * A Screen class that displays a book. The book is optionally editable, has pages which can be switched and there is a title.
 */
public class BookScreen extends BaseInteractionScreen {
    private static final Logger logger = LoggerFactory.getLogger(BookScreen.class);
    /* Local List of pages that the bookComponent contains */
    static List<String> pages;
    /* Boolean to see which edit button was clicked (left or right) */
    static boolean leftPageEditing = true;
    /* Index for finding which page number is opened */
    static Binding<Integer> index;

    private static BookComponent book;
    private static EntityRef bookEntity;
    private static UIImage coverLeft;
    private static UIImage coverRight;
    private static UIImage pageLeft;
    private static UIImage pageRight;
    private static BrowserWidget textLeft;
    private static BrowserWidget textRight;
    private static UIButton arrowForward;
    private static UIButton arrowBackward;
    private static UILabel title;
    private static final Binding<UITextureRegion> coverBackL = new DefaultBinding<>(Assets.getTextureRegion("Books:book#interiorLeft").get());
    private static final Binding<UITextureRegion> coverBackR = new DefaultBinding<>(Assets.getTextureRegion("Books:book#interiorRight").get());
    private static final Binding<UITextureRegion> coverFrontL = new DefaultBinding<>(Assets.getTextureRegion("Books:book#exteriorLeft").get());
    private static final Binding<UITextureRegion> coverFrontR = new DefaultBinding<>(Assets.getTextureRegion("Books:book#exteriorRight").get());
    private static final Binding<UITextureRegion> pageL = new DefaultBinding<>(Assets.getTextureRegion("Books:book#pageLeft").get());
    private static final Binding<UITextureRegion> pageR = new DefaultBinding<>(Assets.getTextureRegion("Books:book#pageRight").get());
    private static final Binding<UITextureRegion> blank = new DefaultBinding<>(Assets.getTextureRegion("Books:blank").get());

    private static final String STATUS_EDITING = "Editing";
    private static final String STATUS_READING = "Reading";
    private static final String STATUS_READ_ONLY = "Read-only";


    @In
    private static PrefabManager prefabManager;
    @In
    private NUIManager nuiManager;
    @In
    private LocalPlayer localPlayer;

    private String status;
    private UIButton save;
    private UIButton editLeft;
    private UIButton editRight;
    private UIButton cancel;
    private UIButton deleteLeft;
    private UIButton deleteRight;
    private UIButton addPage;
    private UILabel statusText;

    public BookScreen() {
    }

    /**
     * Returns a HTMLLike document for a given string
     */
    private static DocumentData createDocument(String text) {
        DefaultDocumentData page = new DefaultDocumentData(null);
        page.addParagraphs(createParagraphs(text));
        return page;
    }

    /**
     * Converts the Text into TextParagraphs and RecipeParagraphs
     */
    private static Collection<ParagraphData> createParagraphs(String text) {
        Collection<ParagraphData> paragraphs = new ArrayList<ParagraphData>();
        while (text.length() > 0) {
            if (text.contains("<recipe")) {
                int i = text.indexOf("<recipe");
                paragraphs.add(createTextParagraph(text.substring(0, i)));
                text = text.substring(i);
                i = text.indexOf(">");
                // Capture the text following the "<recipe" tag and remove spaces
                String recipePrefabName = text.substring("<recipe".length(), i).replaceAll("\\s", "");
                paragraphs.add(createRecipeParagraph(recipePrefabName));
                text = text.substring(i + 1);
            } else {
                paragraphs.add(createTextParagraph(text));
                text = "";
            }
        }
        return paragraphs;
    }

    private static ParagraphData createTextParagraph(String text) {
        return HTMLLikeParser.parseHTMLLikeParagraph(null, "<c 198>" + text.replace("\n", "<l>") + "</c>");
    }

    private static RecipeParagraph createRecipeParagraph(String prefabName) {
        Prefab recipePrefab = prefabManager.getPrefab(prefabName);
        BookRecipeComponent bookRecipeComponent = recipePrefab.getComponent(BookRecipeComponent.class);
        return new RecipeParagraph(bookRecipeComponent.blockIngredients, bookRecipeComponent.blockIngredientsList, bookRecipeComponent.itemIngredients,
                bookRecipeComponent.blockResult, bookRecipeComponent.itemResult, bookRecipeComponent.resultCount);
    }

    static State getState() {
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

    static String getTextLeft() {
        if (getState().equals(State.OPEN_LEFT)) {
            return pages.get(index.get());
        }
        if (getState().equals(State.PAGES)) {
            return pages.get(index.get());
        }

        return "";
    }

    static String getTextRight() {
        if (getState().equals(State.OPEN_RIGHT)) {
            return pages.get(index.get());
        }
        if (getState().equals(State.PAGES)) {
            return pages.get(index.get() + 1);
        }

        return "";
    }

    static void updatePage() {
        pageLeft.bindTexture(blank);
        pageRight.bindTexture(blank);
        arrowForward.setVisible(true);
        arrowBackward.setVisible(true);
        title.setText("");

        textLeft.navigateTo(createDocument(getTextLeft()));
        textRight.navigateTo(createDocument(getTextRight()));

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
        textLeft = find("textLeft", BrowserWidget.class);
        textRight = find("textRight", BrowserWidget.class);
        arrowForward = find("forward", UIButton.class);
        arrowBackward = find("backward", UIButton.class);

        editLeft = find("editLeft", UIButton.class);
        editRight = find("editRight", UIButton.class);
        cancel = find("cancel", UIButton.class);
        save = find("save", UIButton.class);
        deleteLeft = find("deleteLeft", UIButton.class);
        addPage = find("addPage", UIButton.class);
        deleteRight = find("deleteRight", UIButton.class);
        statusText = find("status", UILabel.class);

        WidgetUtil.trySubscribe(this, "forward", button -> {
            forward();
            updateEditingControls();
            updatePage();
        });

        WidgetUtil.trySubscribe(this, "backward", button -> {
            backward();
            updateEditingControls();
            updatePage();
        });

        cancel.subscribe(button -> nuiManager.closeScreen(this));

        editLeft.subscribe(button -> {
            leftPageEditing = true;
            nuiManager.pushScreen("Books:pageEditor", PageEditor.class);
        });

        editRight.subscribe(button -> {
            leftPageEditing = false;
            nuiManager.pushScreen("Books:pageEditor", PageEditor.class);
        });

        deleteLeft.subscribe(button -> {
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

        save.subscribe(button -> {
            book.pages = pages;
            bookEntity.addOrSaveComponent(book);
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
     *
     * @param color The color which the book cover should have.
     */
    private void setTint(Color color) {
        coverLeft.setTint(color);
        coverRight.setTint(color);
    }

    private void setEditable(boolean editable) {
        if (editable) {
            if (getState().equals(State.OPEN_RIGHT)) {
                editRight.setVisible(true);
                editLeft.setVisible(false);
            } else if (getState().equals(State.OPEN_LEFT)) {
                editLeft.setVisible(true);
                editRight.setVisible(false);
            } else if (getState().equals(State.PAGES)) {
                editLeft.setVisible(true);
                editRight.setVisible(true);
            } else {
                editLeft.setVisible(false);
                editRight.setVisible(false);
            }
        } else {
            editLeft.setVisible(false);
            editRight.setVisible(false);
        }
        save.setVisible(editable);
        cancel.setVisible(editable);
        deleteLeft.setVisible(editable);
        addPage.setVisible(editable);
        deleteRight.setVisible(editable);

    }

    private void updateEditingControls() {
        if (status.equals(STATUS_EDITING)) {
            setEditable(true);
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
