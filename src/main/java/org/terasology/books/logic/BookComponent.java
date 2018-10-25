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

package org.terasology.books.logic;

import com.google.common.collect.Lists;
import org.terasology.entitySystem.Component;
import org.terasology.network.FieldReplicateType;
import org.terasology.network.Replicate;
import org.terasology.rendering.nui.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Indicates that an item is a book, allowing it to be opened and read.
 */
public class BookComponent implements Component {
    public enum BookType {
        Written,
        Picture
    }

    /** The color used for the front and back cover of the book in the GUI. */
    public Color tint = Color.WHITE;
    public BookType type = BookType.Written;

    /** If this value is true, the book will not be editable. */
    @Replicate
    public boolean readOnly;
    /** The name of the book displayed on the front cover in the GUI. */
    @Replicate
    public String title;

    /** The list of pages in this book. Its length must be even or thing will explode. */
    @Replicate(FieldReplicateType.OWNER_TO_SERVER)
    public List<String> pages = new ArrayList<>(Lists.newArrayList("", ""));
}

