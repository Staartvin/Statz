package me.staartvin.statz.gui.inventory.buttons;

import org.bukkit.event.inventory.InventoryClickEvent;

public interface ButtonListener {

    /**
     * The {@link InventoryClickEvent} that should be called and
     * executed when the associated {@link GUIButton} is clicked.
     *
     * @param event The event that should be called and executed.
     */
    void onClick(InventoryClickEvent event);

}
