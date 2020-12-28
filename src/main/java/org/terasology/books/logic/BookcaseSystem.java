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

import org.joml.Vector3f;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.inventory.events.BeforeItemPutInInventory;
import org.terasology.logic.inventory.InventoryComponent;
import org.terasology.logic.inventory.events.DropItemEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.world.block.entity.CreateBlockDropsEvent;

import java.util.List;

/**
 * Simple system to allow bookcases to filter out non-books and drop contents when destroyed.
 * This is a good simple example of a contrast with chests (that drop as a single item including its contents),
 * and for showing a simple way to filter for specific objects.
 *
 * Setting the RegisterMode to ALWAYS lets the filtering trigger on a non-authority client ie. regular multiplayer clients.
 */
@RegisterSystem(RegisterMode.ALWAYS)
public class BookcaseSystem extends BaseComponentSystem {

    /**
     * Check that only books can be put into a bookcase.
     * @param event the triggering event (somebody tried to put something into the bookcase)
     * @param entity the object that is being submitted as an potential entry to the bookcase's inventory
     */
    @ReceiveEvent(components = {BookcaseComponent.class})
    public void filterBook(BeforeItemPutInInventory event, EntityRef entity) {
        if (!event.getItem().hasComponent(BookComponent.class)) {
            event.consume();
        }
    }

    /**
     * On destruction of a bookcase go through its inventory and drop all items on the ground.
     * This
     * @param event the triggering event (something caused block drops - the bookcase was destroyed)
     * @param entity the bookcase's entity, has the target inventory to go through
     */
    @ReceiveEvent(components = {BookcaseComponent.class, InventoryComponent.class, LocationComponent.class})
    public void onDestroyBookCase(CreateBlockDropsEvent event, LocationComponent location, InventoryComponent inventory, EntityRef entity) {
        Vector3f pos = location.getWorldPosition(new Vector3f());
        List<EntityRef> items = inventory.itemSlots;
        for (EntityRef item : items) {
            if (item.hasComponent(BookComponent.class)) {
                item.send(new DropItemEvent(pos));
            }
        }
        items.clear();
    }
}
