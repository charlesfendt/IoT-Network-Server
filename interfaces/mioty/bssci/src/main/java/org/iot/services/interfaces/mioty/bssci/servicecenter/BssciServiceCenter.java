/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.iot.services.interfaces.mioty.bssci.servicecenter;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;

import org.apache.commons.lang3.Validate;
import org.iot.services.interfaces.mioty.bssci.api.Api;
import org.iot.services.interfaces.mioty.bssci.utils.EUI64;

import lombok.Getter;

/**
 * Mockup for the BSSCI server.
 *
 * @author FendtC
 *
 */
public class BssciServiceCenter {
    /** The server socket. */
    private SSLServerSocket serverSock;
    /** The server EUI64. */
    @Getter
    private EUI64 id;
    /** The identifier for the server. */
    @Getter
    private String identifier;

    /** Message handler. */
    private Optional<IBssciEventHandler<Void>> onConnectHandler;
    /** Message handler. */
    private Optional<IBssciEventHandler<Void>> onDisconnectHandler;
    /** Message handler. */
    private Optional<IBssciEventHandler<Api>> onIncommingHandler;
    /** Message handler. */
    private Optional<IBssciEventHandler<Api>> onOutgoingHandler;
    /** Message handler. */
    private Optional<IBssciEventHandler<Exception>> onErrorHandler;

    /** List of connected clients. */
    private final Set<BssciServiceCenterClient> clients = ConcurrentHashMap.newKeySet();

    /**
     * Method to start the test server.
     *
     * @param sc
     *              the SSL/TLS context.
     * @param port
     *              TCP port to use.
     * @param eui
     *              EUI64 of the server.
     * @param ident
     *              Identity of the server.
     * @throws IOException
     *                     Any I/O error.
     */
    public void startServer(final SSLContext sc, final int port, final EUI64 eui, final String ident)
            throws IOException {
        Validate.matchesPattern(ident, "\\w{8}", "%s must be a 8 byte String", ident); //$NON-NLS-1$ //$NON-NLS-2$
        this.id = eui;
        this.identifier = ident;

        this.serverSock = (SSLServerSocket) sc.getServerSocketFactory().createServerSocket(port);
        this.serverSock.setWantClientAuth(true);
        this.waitForClient();
    }

    /**
     * Wait method.
     */
    private void waitForClient() {
        new Thread(() -> {
            try {
                while (true) {
                    this.clients.add(new BssciServiceCenterClient(this, this.serverSock.accept()));
                }
            } catch (final Exception e) {
                this.onErrorHandler.ifPresent(h -> h.handle(null, e));
            }
            try {
                this.close();
            } catch (final IOException e) {
                this.onErrorHandler.ifPresent(h -> h.handle(null, e));
            }
        }).start();
    }

    /**
     * Event handler.
     *
     * @param handler
     *                handler.
     * @return the service center.
     */
    public BssciServiceCenter onDisonnect(final IBssciEventHandler<Void> handler) {
        this.onDisconnectHandler = Optional.of(handler);
        return this;
    }

    /**
     * Event handler.
     *
     * @param client
     *               the client connection.
     */
    protected void disconnect(final BssciServiceCenterClient client) {
        this.clients.remove(client);
        this.onDisconnectHandler.ifPresent(h -> h.handle(client, null));
    }

    /**
     * Event handler.
     *
     * @param handler
     *                handler.
     * @return the service center.
     */
    public BssciServiceCenter onConnect(final IBssciEventHandler<Void> handler) {
        this.onConnectHandler = Optional.of(handler);
        return this;
    }

    /**
     * Event handler.
     *
     * @param client
     *               the client connection.
     */
    protected void connected(final BssciServiceCenterClient client) {
        this.onConnectHandler.ifPresent(h -> h.handle(client, null));
    }

    /**
     * Event handler.
     *
     * @param handler
     *                handler.
     * @return the service center.
     */
    public BssciServiceCenter onIncomming(final IBssciEventHandler<Api> handler) {
        this.onIncommingHandler = Optional.of(handler);
        return this;
    }

    /**
     * Event handler.
     *
     * @param handler
     *                handler.
     * @return the service center.
     */
    public BssciServiceCenter onOutgoing(final IBssciEventHandler<Api> handler) {
        this.onOutgoingHandler = Optional.of(handler);
        return this;
    }

    /**
     * Event handler.
     *
     * @param client
     *               Client associated to the connection.
     * @param apiObj
     *               Object received.
     *
     */
    protected void received(final BssciServiceCenterClient client, final Api apiObj) {
        this.onIncommingHandler.ifPresent(h -> h.handle(client, apiObj));
    }

    /**
     * Event handler.
     *
     * @param client
     *               Client associated to the connection.
     * @param apiObj
     *               Object received.
     *
     */
    protected void sending(final BssciServiceCenterClient client, final Api apiObj) {
        this.onOutgoingHandler.ifPresent(h -> h.handle(client, apiObj));
    }

    /**
     * Event handler.
     *
     * @param handler
     *                handler.
     * @return the service center.
     */
    public BssciServiceCenter onError(final IBssciEventHandler<Exception> handler) {
        this.onErrorHandler = Optional.of(handler);
        return this;
    }

    /**
     * Event handler.
     *
     * @param client
     *               Client associated to the connection.
     * @param e
     *               error received.
     *
     */
    protected void error(final BssciServiceCenterClient client, final Exception e) {
        this.onErrorHandler.ifPresent(h -> h.handle(client, e));
    }

    /**
     * Method to send a message to all clients.
     *
     * @param apiObj
     *               Message to send.
     * @return The server instance.
     */
    public BssciServiceCenter sendToAll(final Api apiObj) {
        this.clients.stream().forEach(c -> c.send(apiObj));
        return this;
    }

    /**
     * Method to close the server instance and therefore all client connections.
     *
     * @throws IOException
     *                     Any I/O error.
     */
    public void close() throws IOException {
        this.clients.stream().forEach(BssciServiceCenterClient::disconnect);
        if (this.serverSock != null) {
            this.serverSock.close();
        }
        this.serverSock = null;
    }
}
