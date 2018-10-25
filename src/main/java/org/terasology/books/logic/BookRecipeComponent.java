/*
 * Copyright 2017 MovingBlocks
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

package org.terasology.books.logic;

import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.world.block.Block;

import java.util.ArrayList;
import java.util.List;

/**
 * Used in a prefab to represent a recipe. The prefab can then be inserted in a book using a &ltrecipe&gt tag inside a page as such:
 * <br>
 * <code>&ltrecipe module:prefab&gt</code>
 */
public class BookRecipeComponent implements Component {
	/** The number of ingredients used in this recipe. */
    public int blockIngredients;
    /** Blocks used in this recipe, if any. */
    public List<Block> blockIngredientsList;
    /** Items used in this recipe, if any. */
    public List<Prefab> itemIngredients = new ArrayList<>();
    /** The result of the recipe, if it is a block. If it is an item, this should be left empty. */
    public Block blockResult = null;
    /** The result of the recipe, if it is an item. If it is a block, this should be left empty. */
    public Prefab itemResult;
    /** The amount of blocks/items produced by this recipe. */
    public int resultCount;
}

