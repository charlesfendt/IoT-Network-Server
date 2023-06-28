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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

import org.iot.services.interfaces.mioty.bssci.api.Api;
import org.iot.services.interfaces.mioty.bssci.api.main.Connect;
import org.iot.services.interfaces.mioty.bssci.api.main.ConnectCmp;
import org.iot.services.interfaces.mioty.bssci.api.main.ConnectRsp;
import org.iot.services.interfaces.mioty.bssci.utils.BytesUtils;
import org.iot.services.interfaces.mioty.bssci.utils.EUI64;
import org.iot.services.interfaces.mioty.bssci.utils.UuidUtils;

import lombok.Getter;

/**
 * Client for the BsciServiceCenter implementation.
 *
 * @author FendtC
 */
public class ServiceCenterConnection {
    private final BssciServiceCenter scs;
    @Getter
    private EUI64 eui;
    private final AtomicInteger opid;
    private Socket sock;
    private final InputStream in;
    private final OutputStream out;

    protected ServiceCenterConnection(final BssciServiceCenter scs, final Socket sock) throws Exception {
        this.scs = scs;
        this.opid = new AtomicInteger(-1);
        this.sock = sock;
        this.in = sock.getInputStream();
        this.out = sock.getOutputStream();

        // start receiver in separate thread
        new Thread(new Receiver()).start();
    }

    public ServiceCenterConnection send(final Api apiObj) {
        return this.send(apiObj, this.opid.getAndDecrement());
    }

    public ServiceCenterConnection respond(final Api responseMsg, final Api receivedMsg) {
        return this.send(responseMsg, receivedMsg.getOpId());
    }

    protected ServiceCenterConnection send(final Api apiObj, final int opId) {
        this.scs.sending(this, apiObj);
        apiObj.setOpId(opId);
        try {
            final var payload = apiObj.toMsgPack();
            // create a 4 byte payload length, in little endian!
            final var payloadsize = BytesUtils.reverse(java.nio.ByteBuffer.allocate(4).putInt(payload.length).array());

            // it has to be 'this' static name, I do not know why...
            final var size = 8 + 4 + payload.length;// Identifier(8Byte) + payload size(4Byte) + payload(variable)
            final var bytesdd = java.nio.ByteBuffer.allocate(size)
                    .put(this.scs.getIdentifier().getBytes(StandardCharsets.US_ASCII)).put(payloadsize).put(payload)
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
                ServiceCenterConnection.this.scs.error(ServiceCenterConnection.this, e);
            }
            ServiceCenterConnection.this.disconnect();
        }

        private boolean read() throws IOException {
            final var header = ServiceCenterConnection.this.in.readNBytes(8);
            if (header.length < 8) {
                // socket closed on buffersize == 0
                if (header.length > 0) {
                    ServiceCenterConnection.this.scs.error(ServiceCenterConnection.this,
                            new IllegalArgumentException("Invalid byte flow!")); //$NON-NLS-1$
                }
                return false;
            }
            // read payload size, 4 Bytes little endian!
            final var payloadSize = java.nio.ByteBuffer
                    .wrap(BytesUtils.reverse(ServiceCenterConnection.this.in.readNBytes(4))).getInt();
            this.handle(Api.fromMsgPack(ServiceCenterConnection.this.in.readNBytes(payloadSize)));
            return true;
        }

        private void handle(final Api apiObj) {
            // connect messages
            if (apiObj instanceof Connect) {
                ServiceCenterConnection.this.eui = ((Connect) apiObj).getBsEui();
                final var conRsp = new ConnectRsp(ServiceCenterConnection.this.scs.getId(), false);
                conRsp.setSnScUuid(UuidUtils.convertUuidToBytes(null));
                ServiceCenterConnection.this.send(conRsp, 0);
            } else if (apiObj instanceof ConnectCmp) {
                ServiceCenterConnection.this.scs.connected(ServiceCenterConnection.this);
            } else {
                ServiceCenterConnection.this.scs.received(ServiceCenterConnection.this, apiObj);
            }
        }
    }

}
