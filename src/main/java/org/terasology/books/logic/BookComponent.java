// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.books.logic;

import com.google.common.collect.Lists;
import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.network.FieldReplicateType;
import org.terasology.engine.network.Replicate;
import org.terasology.nui.Color;

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

