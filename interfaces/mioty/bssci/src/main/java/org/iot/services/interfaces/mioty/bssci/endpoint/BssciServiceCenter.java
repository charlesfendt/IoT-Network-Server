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
package org.iot.services.interfaces.mioty.bssci.endpoint;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;

import org.apache.commons.lang3.Validate;
import org.iot.services.interfaces.mioty.bssci.api.Api;
import org.iot.services.interfaces.mioty.bssci.api.Connect;
import org.iot.services.interfaces.mioty.bssci.api.ConnectCmp;
import org.iot.services.interfaces.mioty.bssci.api.ConnectRsp;
import org.iot.services.interfaces.mioty.bssci.utils.EUI64;
import org.iot.services.interfaces.mioty.bssci.utils.UuidUtils;

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
    private EUI64 id;
    /** The identifier for the server. */
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
    private void disconnect(final BssciServiceCenterClient client) {
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
    private void connected(final BssciServiceCenterClient client) {
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
    private void received(final BssciServiceCenterClient client, final Api apiObj) {
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
    private void sending(final BssciServiceCenterClient client, final Api apiObj) {
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
    private void error(final BssciServiceCenterClient client, final Exception e) {
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

    public static class BssciServiceCenterClient {
        private final BssciServiceCenter scs;
        private EUI64 eui;
        private final AtomicInteger opid;
        private Socket sock;
        private final InputStream in;
        private final OutputStream out;

        private BssciServiceCenterClient(final BssciServiceCenter scs, final Socket sock) throws Exception {
            this.scs = scs;
            this.opid = new AtomicInteger(-1);
            this.sock = sock;
            this.in = sock.getInputStream();
            this.out = sock.getOutputStream();

            // start receiver in separate thread
            new Thread(new Receiver()).start();
        }

        public BssciServiceCenterClient send(final Api apiObj) {
            return this.send(apiObj, this.opid.getAndDecrement());
        }

        public BssciServiceCenterClient respond(final Api responseMsg, final Api receivedMsg) {
            return this.send(responseMsg, receivedMsg.getOpId());
        }

        public EUI64 getEui() {
            return this.eui;
        }

        private BssciServiceCenterClient send(final Api apiObj, final int opId) {
            this.scs.sending(this, apiObj);
            apiObj.setOpId(opId);
            try {
                final var payload = apiObj.toMsgPack();
                // create a 4 byte payload length, in little endian!
                final var payloadsize = this.reverse(java.nio.ByteBuffer.allocate(4).putInt(payload.length).array());

                // it has to be 'this' static name, I do not know why...
                final var size = 8 + 4 + payload.length;// Identifier(8Byte) + payload size(4Byte) + payload(variable)
                final var bytesdd = java.nio.ByteBuffer.allocate(size)
                        .put(this.scs.identifier.getBytes(StandardCharsets.US_ASCII)).put(payloadsize).put(payload)
                        .array();

                this.out.write(bytesdd);
            } catch (final Exception e) {
                this.scs.error(this, e);
            }
            return this;
        }

        public void disconnect() {
            try {
                if (this.sock != null) {
                    this.sock.close();
                    this.scs.disconnect(this);
                    this.in.close();
                    this.out.close();
                    this.sock = null;
                }
            } catch (final IOException e) {
                this.scs.error(this, e);
            }
        }

        private class Receiver implements Runnable {
            @Override
            public void run() {
                try {
                    while (this.read()) {
                        // do nothing
                    }
                } catch (final Exception e) {
                    BssciServiceCenterClient.this.scs.error(BssciServiceCenterClient.this, e);
                }
                BssciServiceCenterClient.this.disconnect();
            }

            private boolean read() throws IOException {
                final var header = BssciServiceCenterClient.this.in.readNBytes(8);
                if (header.length < 8) {
                    // socket closed on buffersize == 0
                    if (header.length > 0) {
                        BssciServiceCenterClient.this.scs.error(BssciServiceCenterClient.this,
                                new IllegalArgumentException("Invalid byte flow!")); //$NON-NLS-1$
                    }
                    return false;
                }
                // read payload size, 4 Bytes little endian!
                final var payloadSize = java.nio.ByteBuffer
                        .wrap(BssciServiceCenterClient.this.reverse(BssciServiceCenterClient.this.in.readNBytes(4)))
                        .getInt();
                this.handle(Api.fromMsgPack(BssciServiceCenterClient.this.in.readNBytes(payloadSize)));
                return true;
            }

            private void handle(final Api apiObj) {
                // connect messages
                if (apiObj instanceof Connect) {
                    BssciServiceCenterClient.this.eui = ((Connect) apiObj).getBsEui();
                    final var conRsp = new ConnectRsp(BssciServiceCenterClient.this.scs.id, false);
                    conRsp.setSnScUuid(UuidUtils.convertUuidToBytes(null));
                    BssciServiceCenterClient.this.send(conRsp, 0);
                } else if (apiObj instanceof ConnectCmp) {
                    BssciServiceCenterClient.this.scs.connected(BssciServiceCenterClient.this);
                } else {
                    BssciServiceCenterClient.this.scs.received(BssciServiceCenterClient.this, apiObj);
                }
            }
        }

        private byte[] reverse(final byte[] bytes) {
            for (int i = 0, j = bytes.length - 1; i < j; i++, j--) {
                final var tmp = bytes[i];
                bytes[i] = bytes[j];
                bytes[j] = tmp;
            }
            return bytes;
        }
    }

    @FunctionalInterface
    public interface IBssciEventHandler<T> {
        void handle(BssciServiceCenterClient client, T event);
    }

}
