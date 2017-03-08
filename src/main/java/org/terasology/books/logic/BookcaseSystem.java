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

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.inventory.events.BeforeItemPutInInventory;
import org.terasology.logic.inventory.InventoryComponent;
import org.terasology.logic.inventory.events.DropItemEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.world.block.entity.CreateBlockDropsEvent;

import java.util.List;

@RegisterSystem(RegisterMode.AUTHORITY)
public class BookcaseSystem extends BaseComponentSystem {

    /**
     * Check that only books can be put into a bookcase.
     * @param event
     * @param entity
     */
    @ReceiveEvent
    public void filterBook(BeforeItemPutInInventory event, EntityRef entity, BookcaseComponent bookcaseComponent) {
        if (!event.getItem().hasComponent(BookComponent.class)) {
            event.consume();
        }
    }

    @ReceiveEvent
    public void onDestroyBookCase(CreateBlockDropsEvent event, EntityRef entity, BookcaseComponent bookcaseComponent) {
        if (entity.hasComponent(InventoryComponent.class)) {
            Vector3f location = entity.getComponent(LocationComponent.class).getWorldPosition();
            InventoryComponent inventory = entity.getComponent(InventoryComponent.class);
            List<EntityRef> items = inventory.itemSlots;
            for (EntityRef item: items) {
                if (item.hasComponent(BookComponent.class)) {
                    item.send(new DropItemEvent(location));
                }
            }
            items.clear();
        }
    }
}
