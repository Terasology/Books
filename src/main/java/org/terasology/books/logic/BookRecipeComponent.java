// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.books.logic;

import com.google.common.collect.Lists;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.world.block.Block;
import org.terasology.gestalt.entitysystem.component.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Used in a prefab to represent a recipe. The prefab can then be inserted in a book using a <recipe> tag inside a page as such:
 * <br>
 * <code><recipe module:prefab></code>
 */
public class BookRecipeComponent implements Component<BookRecipeComponent> {
    /**
     * The number of ingredients used in this recipe.
     */
    public int blockIngredients;
    /**
     * Blocks used in this recipe, if any.
     */
    public List<Block> blockIngredientsList = new ArrayList<>();
    
    /**
     * Items used in this recipe, if any.
     */
    public List<Prefab> itemIngredients = new ArrayList<>();
    /**
     * The result of the recipe, if it is a block. If it is an item, this should be left empty.
     */
    public Block blockResult = null;
    /**
     * The result of the recipe, if it is an item. If it is a block, this should be left empty.
     */
    public Prefab itemResult;
    /**
     * The amount of blocks/items produced by this recipe.
     */
    public int resultCount;

    @Override
    public void copyFrom(BookRecipeComponent other) {
        this.blockIngredients = other.blockIngredients;
        this.blockIngredientsList = Lists.newArrayList(other.blockIngredientsList);
        this.itemIngredients = Lists.newArrayList(other.itemIngredients);
        this.blockResult = other.blockResult;
        this.itemResult = other.itemResult;
        this.resultCount = other.resultCount;
    }
}

