// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.books.logic;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.inventory.events.DropItemEvent;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.world.block.entity.CreateBlockDropsEvent;
import org.terasology.inventory.logic.InventoryComponent;
import org.terasology.inventory.logic.events.BeforeItemPutInInventory;
import org.terasology.math.geom.Vector3f;

import java.util.List;

/**
 * Simple system to allow bookcases to filter out non-books and drop contents when destroyed. This is a good simple
 * example of a contrast with chests (that drop as a single item including its contents), and for showing a simple way
 * to filter for specific objects.
 * <p>
 * Setting the RegisterMode to ALWAYS lets the filtering trigger on a non-authority client ie. regular multiplayer
 * clients.
 */
@RegisterSystem(RegisterMode.ALWAYS)
public class BookcaseSystem extends BaseComponentSystem {

    /**
     * Check that only books can be put into a bookcase.
     *
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
     * On destruction of a bookcase go through its inventory and drop all items on the ground. This
     *
     * @param event the triggering event (something caused block drops - the bookcase was destroyed)
     * @param entity the bookcase's entity, has the target inventory to go through
     */
    @ReceiveEvent(components = {BookcaseComponent.class})
    public void onDestroyBookCase(CreateBlockDropsEvent event, EntityRef entity) {
        if (entity.hasComponent(InventoryComponent.class)) {
            Vector3f location = entity.getComponent(LocationComponent.class).getWorldPosition();
            InventoryComponent inventory = entity.getComponent(InventoryComponent.class);
            List<EntityRef> items = inventory.itemSlots;
            for (EntityRef item : items) {
                if (item.hasComponent(BookComponent.class)) {
                    item.send(new DropItemEvent(location));
                }
            }
            items.clear();
        }
    }
}
