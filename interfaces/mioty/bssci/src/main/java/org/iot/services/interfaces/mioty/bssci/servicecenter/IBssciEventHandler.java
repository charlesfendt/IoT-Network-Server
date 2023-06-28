/**
 *
 */
package org.iot.services.interfaces.mioty.bssci.servicecenter;

/**
 * Event handler definition.
 *
 * @author FendtC
 * @param <T>
 *            Event type.
 */
@FunctionalInterface
public interface IBssciEventHandler<T> {

    /**
     * Handling method.
     *
     * @param client
     *               Client associated to the event.
     * @param event
     *               event to handle
     */
    void handle(BssciServiceCenterClient client, T event);
}