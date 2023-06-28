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
package org.iot.services.interfaces.mioty.bssci.basestation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.lang3.Validate;
import org.iot.services.interfaces.mioty.bssci.api.Api;
import org.iot.services.interfaces.mioty.bssci.api.main.Connect;
import org.iot.services.interfaces.mioty.bssci.api.main.ConnectRsp;
import org.iot.services.interfaces.mioty.bssci.api.main.Error;
import org.iot.services.interfaces.mioty.bssci.utils.BytesUtils;
import org.iot.services.interfaces.mioty.bssci.utils.EUI64;
import org.iot.services.interfaces.mioty.bssci.utils.UuidUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.Setter;

/**
 * Generic mioty BaseStation implementation.
 *
 * @author FendtC
 */
public class BssciBaseStation {

    /** Logger of the class. */
    private static final Logger LOG = LoggerFactory.getLogger(BssciBaseStation.class);

    /** Delay before any error notification. */
    private static final int ERROR_DELAY = 25;

    /** The generator for the operation ID. */
    private final AtomicInteger opid;
    /** EUI64 of the base station. */
    private final EUI64 id;
    /** Identifier of the gateway. */
    private final String identifier;
    /** the connection socket. */
    private Socket sock;
    /** Input stream of the socket. */
    private InputStream in = InputStream.nullInputStream();
    /** Output stream socket. */
    private OutputStream out = OutputStream.nullOutputStream();

    /** Event handler. */
    private Optional<IBaseStationEventHandler<ConnectionResult>> onConnectHandler;
    /** Event handler. */
    private Optional<IBaseStationEventHandler<Void>> onDisconnectHandler;
    /** Event handler. */
    private Optional<IBaseStationEventHandler<Api>> onIncommingHandler;
    /** Event handler. */
    private Optional<IBaseStationEventHandler<Api>> onOutgoingHandler;
    /** Event handler. */
    private Optional<IBaseStationEventHandler<Exception>> onErrorHandler;

    /** The debug handler to use. */
    private final IDebugLogger debug;

    /** Gateway info to be pushed to the server. */
    @Getter
    @Setter
    private String vendor; // the gateway vendor
    /** Gateway info to be pushed to the server. */
    @Getter
    @Setter
    private String model; // the gateway model
    /** Gateway info to be pushed to the server. */
    @Getter
    @Setter
    private String name; // Gateway name
    /** Gateway info to be pushed to the server. */
    @Getter
    @Setter
    private String swVersion; // Software version

    /**
     * Constructor with minimal information.
     *
     * @param eui
     *                   the Gateway EUI64.
     * @param identifier
     *                   the gateway identifier.
     */
    public BssciBaseStation(final EUI64 eui, final String identifier) {
        this(eui, identifier, null);
    }

    /**
     * Constructor with minimal information.
     *
     * @param eui
     *                   the Gateway EUI64.
     * @param identifier
     *                   the gateway identifier.
     * @param debug
     *                   The optional debug interface.
     */
    public BssciBaseStation(final EUI64 eui, final String identifier, final IDebugLogger debug) {
        super();
        Validate.matchesPattern(identifier, "\\w{8}", "%s must be a 8 byte String", identifier); //$NON-NLS-1$ //$NON-NLS-2$
        this.id = eui;
        this.identifier = identifier;

        if (debug == null) {
            this.debug = new IDebugLogger() {
                // do nothing
            };
        } else {
            this.debug = debug;
        }
        this.opid = new AtomicInteger(1);
        this.onDisconnectHandler = Optional.empty();
        this.onConnectHandler = Optional.empty();
        this.onIncommingHandler = Optional.empty();
        this.onErrorHandler = Optional.empty();
    }

    public void connect(final SSLContext sc, final URI uri, final IBaseStationEventHandler<ConnectionResult> handler) {
        this.onConnectHandler = Optional.of(handler);
        new Thread(() -> this.run(sc.getSocketFactory(), uri)).start();
    }

    public void connect(final SSLSocketFactory socketFactoy, final URI uri,
            final IBaseStationEventHandler<ConnectionResult> handler) {
        this.onConnectHandler = Optional.of(handler);
        new Thread(() -> this.run(socketFactoy, uri)).start();
    }

    private void run(final SSLSocketFactory socketFactoy, final URI uri) {
        this.opid.set(0);
        try {
            this.sock = socketFactoy.createSocket(uri.getHost(), uri.getPort());
            this.in = this.sock.getInputStream();
            this.out = this.sock.getOutputStream();
            // start receiver in separate thread
            new Thread(this::processStream).start();
            // start bssci handshake
            final var con = new Connect("1.0.0", this.id, this.swVersion, true); //$NON-NLS-1$
            con.setVendor(this.vendor);
            con.setModel(this.model);
            con.setName(this.name);
            con.setSnBsUuid(UuidUtils.convertUuidToInts(null));
            this.send(con);
        } catch (final Exception e) {
            BssciBaseStation.notify(this.onConnectHandler, new ConnectionResult(false, e));
        }
    }

    /**
     * Method to set the disconnect handler.
     *
     * @param handler
     *                new handler.
     * @return the BaseStation instance.
     */
    public BssciBaseStation onDisonnect(final IBaseStationEventHandler<Void> handler) {
        this.onDisconnectHandler = Optional.of(handler);
        return this;
    }

    /**
     * Method to set the incoming handler.
     *
     * @param handler
     *                new handler.
     * @return the BaseStation instance.
     */
    public BssciBaseStation onIncoming(final IBaseStationEventHandler<Api> handler) {
        this.onIncommingHandler = Optional.of(handler);
        return this;
    }

    /**
     * Method to set the outgoing handler.
     *
     * @param handler
     *                new handler.
     * @return the BaseStation instance.
     */
    public BssciBaseStation onOutgoing(final IBaseStationEventHandler<Api> handler) {
        this.onOutgoingHandler = Optional.of(handler);
        return this;
    }

    /**
     * Method to set the error handler.
     *
     * @param handler
     *                new handler.
     * @return the BaseStation instance.
     */
    public BssciBaseStation onError(final IBaseStationEventHandler<Exception> handler) {
        this.onErrorHandler = Optional.of(handler);
        return this;
    }

    public BssciBaseStation send(final Api apiObj) {
        return this.send(apiObj, this.opid.getAndIncrement());
    }

    public BssciBaseStation respond(final Api responseMsg, final Api receivedMsg) {
        return this.send(responseMsg, receivedMsg.getOpId());
    }

    private BssciBaseStation send(final Api apiObj, final int opId) {
        BssciBaseStation.notify(this.onOutgoingHandler, apiObj);
        apiObj.setOpId(opId);
        try {
            final var payload = apiObj.toMsgPack();
            // create a 4 byte payload length, in little endian!
            final var payloadsize = BytesUtils.reverse(ByteBuffer.allocate(4).putInt(payload.length).array());

            // it has to be 'this' static name, I do not know why...
            final var size = 8 + 4 + payload.length;// Identifier(8Byte) + payload size(4Byte) + payload(variable)
            final var bytesdd = ByteBuffer.allocate(size).put(this.identifier.getBytes(StandardCharsets.US_ASCII))
                    .put(payloadsize).put(payload).array();

            this.debug.logOutgoing(apiObj, bytesdd);
            this.out.write(bytesdd);
        } catch (final Exception e) {
            BssciBaseStation.notify(this.onErrorHandler, e);
        }
        return this;
    }

    public void disconnect() {
        try {
            if (this.sock != null) {
                this.sock.close();
                BssciBaseStation.notify(this.onDisconnectHandler, null);
                this.in.close();
                this.out.close();
                this.out = OutputStream.nullOutputStream();
                this.in = InputStream.nullInputStream();
                this.sock = null;
            }
        } catch (final IOException e) {
            BssciBaseStation.notify(this.onErrorHandler, e);
        }
        this.opid.set(0);
    }

    private void processStream() {
        try {
            var loop = true;
            while (loop) {
                final var header = this.in.readNBytes(8);
                if (header.length < 8) {
                    // socket closed on buffer content too small
                    if (header.length > 0) {
                        BssciBaseStation.notify(this.onErrorHandler,
                                new IllegalArgumentException("Invalid byte flow!")); //$NON-NLS-1$
                    }
                    loop = false;
                } else {
                    // read payload size, 4 Bytes little endian!
                    final var payloadSize = ByteBuffer.wrap(BytesUtils.reverse(this.in.readNBytes(4))).getInt();
                    final var payload = this.in.readNBytes(payloadSize);
                    try {
                        final var incoming = Api.fromMsgPack(payload);
                        this.debug.logIncoming(incoming, payload);
                        this.handle(incoming);
                    } catch (final Exception ex) {
                        this.debug.logIncoming(null, payload);
                        BssciBaseStation.LOG.error("Wrong incoming data {}", BytesUtils.bytesToHex(payload)); //$NON-NLS-1$
                        try {
                            Thread.sleep(BssciBaseStation.ERROR_DELAY);
                        } catch (final InterruptedException exc) {
                            BssciBaseStation.LOG.warn("Cannt wait before error notification", exc); //$NON-NLS-1$
                            Thread.currentThread().interrupt();
                        }
                        this.send(new Error(1, "unsupported operation"), Api.extractOpId(payload)); //$NON-NLS-1$
                    }
                }
            }
        } catch (final Exception e) {
            BssciBaseStation.notify(this.onErrorHandler, e);
        }
        this.disconnect();
    }

    private void handle(final Api apiObj) {
        if (apiObj != null) {
            final var response = apiObj.createResponse();
            if (response != null) {
                this.respond(response, apiObj);
            }
            if (apiObj instanceof ConnectRsp) {
                BssciBaseStation.notify(this.onConnectHandler, new ConnectionResult(true, null));
            } else {
                BssciBaseStation.notify(this.onIncommingHandler, apiObj);
            }
        }
    }

    private static <T> void notify(final Optional<IBaseStationEventHandler<T>> handler, final T event) {
        try {
            handler.ifPresent(h -> h.handle(event));
        } catch (final Exception ex) {
            BssciBaseStation.LOG.error("errr in the handling of the BSSCI event", ex); //$NON-NLS-1$
        }
    }
}
