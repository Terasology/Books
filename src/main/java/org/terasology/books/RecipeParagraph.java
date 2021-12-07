// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.books;

import org.joml.Vector2i;
import org.terasology.books.logic.BookRecipeComponent;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.logic.common.DisplayNameComponent;
import org.terasology.engine.logic.inventory.ItemComponent;
import org.terasology.module.inventory.ui.ItemIcon;
import org.terasology.engine.rendering.nui.widgets.browser.data.ParagraphData;
import org.terasology.engine.rendering.nui.widgets.browser.data.basic.flow.ContainerRenderSpace;
import org.terasology.engine.rendering.nui.widgets.browser.ui.ParagraphRenderable;
import org.terasology.engine.rendering.nui.widgets.browser.ui.style.ParagraphRenderStyle;
import org.terasology.engine.utilities.Assets;
import org.terasology.engine.world.block.Block;
import org.terasology.joml.geom.Rectanglei;
import org.terasology.nui.Canvas;
import org.terasology.nui.HorizontalAlign;

import java.util.List;

/**
 * Used to display recipes alongside text in a book by putting it in a paragraph.
 */
public class RecipeParagraph implements ParagraphData, ParagraphRenderable {
    private int indentAbove = 5;
    private int indentBelow = 5;
    private int ingredientSpacing = 3;
    private int resultSpacing = 30;

    private int iconSize = 64;
    private ItemIcon[] ingredientIcons;
    private ItemIcon resultIcon;


    /**
     * Creates a new RecipeParagraph using data from a {@link BookRecipeComponent}.
     *
     * @param blockIngredients Blocks used in this recipe, if any.
     * @param itemIngredients Items used in this recipe, if any.
     * @param blockResult The result of the recipe, if it is a block. If it is an item, this should be left empty.
     * @param itemResult The result of the recipe, if it is an item. If it is a block, this should be left empty.
     * @param resultCount The amount of blocks/items produced by this recipe.
     */
    public RecipeParagraph(int blockIngredients, List<Block> blockIngredientsList, List<Prefab> itemIngredients, Block blockResult,
                           Prefab itemResult, int resultCount) {
        ingredientIcons = new ItemIcon[blockIngredients];
        for (int i = 0; i < ingredientIcons.length; i++) {
            ItemIcon itemIcon = new ItemIcon();
            if (i < blockIngredientsList.size()) {
                initializeForBlock(itemIcon, blockIngredientsList.get(i));
            } else {
                initializeForItem(itemIcon, itemIngredients.get(i - blockIngredientsList.size()));
            }
            ingredientIcons[i] = itemIcon;
        }
        resultIcon = new ItemIcon();
        if (blockResult != null) {
            initializeForBlock(resultIcon, blockResult);
        } else {
            initializeForItem(resultIcon, itemResult);
        }
        resultIcon.setQuantity(resultCount);
    }

    /**
     * Gets the render style for this paragraph.
     * <br>
     * Note that this paragraph's horizontal alignment will always be centered.
     *
     * @return This paragraph's render style.
     */
    @Override
    public ParagraphRenderStyle getParagraphRenderStyle() {
        return new ParagraphRenderStyle() {
            @Override
            public HorizontalAlign getHorizontalAlignment() {
                return HorizontalAlign.CENTER;
            }
        };
    }

    /**
     * Gets the renderable paragraph for this paragraph, a.k.a. this object.
     *
     * @return this object.
     */
    @Override
    public ParagraphRenderable getParagraphContents() {
        return this;
    }


    /**
     * Stores an item's icon into an ItemIcon
     *
     * @param itemIcon The icon to store the item into.
     * @param itemIngredient The item to get the icon of.
     */
    private void initializeForItem(ItemIcon itemIcon, Prefab itemIngredient) {
        ItemComponent item = itemIngredient.getComponent(ItemComponent.class);
        DisplayNameComponent displayName = itemIngredient.getComponent(DisplayNameComponent.class);
        itemIcon.setIcon(item.icon);
        if (displayName != null) {
            itemIcon.setTooltip(displayName.name);
        }
    }

    /**
     * Stores a block's model into an ItemIcon.
     *
     * @param itemIcon The icon to store the block into.
     * @param blockIngredient The block to get the model of.
     */
    private void initializeForBlock(ItemIcon itemIcon, Block blockIngredient) {
        itemIcon.setMesh(blockIngredient.getMesh());
        itemIcon.setMeshTexture(Assets.getTexture("engine:terrain").get());
        itemIcon.setTooltip(blockIngredient.getDisplayName());
    }

    /**
     * Renders the paragraph.
     * <br>
     * The ingredients are drawn in a strip from left to right, followed by the result.
     */
    @Override
    public void renderContents(Canvas canvas, Vector2i startPos, ContainerRenderSpace containerRenderSpace, int leftIndent, int rightIndent,
                               ParagraphRenderStyle defaultStyle, HorizontalAlign horizontalAlign, HyperlinkRegister hyperlinkRegister) {
        int ingredientsCount = ingredientIcons.length;
        int drawingWidth =
                ingredientsCount * iconSize + (ingredientsCount - 1) * ingredientSpacing + resultSpacing + iconSize;
        int x = startPos.x + horizontalAlign.getOffset(drawingWidth,
                containerRenderSpace.getWidthForVerticalPosition(startPos.y));
        int y = startPos.y + indentAbove;
        for (int i = 0; i < ingredientIcons.length; i++) {
            canvas.drawWidget(ingredientIcons[i], new Rectanglei(x, y).setSize(iconSize, iconSize));
            x += iconSize + ingredientSpacing;
        }
        x -= ingredientSpacing;
        x += resultSpacing;
        canvas.drawWidget(resultIcon, new Rectanglei(x, y).setSize(iconSize, iconSize));
    }

    /**
     * Gets the preferred height of the paragraph.
     *
     * @param defaultStyle Not used
     * @param yStart Not used
     * @param containerRenderSpace Not used
     * @param sideIndents Not used
     *
     * @return The y dimension of this paragraph's preferred size.
     */
    @Override
    public int getPreferredContentsHeight(ParagraphRenderStyle defaultStyle, int yStart, ContainerRenderSpace containerRenderSpace, int sideIndents) {
        return getPreferredSize().y;
    }

    /**
     * Gets the minimum width of this paragraph.
     *
     * @param defaultStyle Not used
     *
     * @return The x dimension of this paragraph's preferred size.
     */
    @Override
    public int getContentsMinWidth(ParagraphRenderStyle defaultStyle) {
        return getPreferredSize().x;
    }

    /**
     * Calculates the amount of space needed to display the recipe.
     *
     * @return A Vector2i whose x component is the width and y component is the height.
     */
    private Vector2i getPreferredSize() {
        int x = 0;
        int y = 0;

        int ingredientsCount = ingredientIcons.length;
        x += ingredientsCount * iconSize + (ingredientsCount - 1) * ingredientSpacing;
        x += resultSpacing + iconSize;

        y += iconSize;

        y += indentAbove + indentBelow;
        return new Vector2i(x, y);
    }
}
