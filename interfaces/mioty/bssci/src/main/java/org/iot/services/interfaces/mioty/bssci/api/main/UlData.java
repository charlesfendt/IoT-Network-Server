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
package org.iot.services.interfaces.mioty.bssci.api.main;

import org.iot.services.interfaces.mioty.bssci.api.Api;
import org.iot.services.interfaces.mioty.bssci.utils.EUI64;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The UL data operation is initiated by the Base Station after receiving uplink data from an End Point. Telegrams carrying control data exclusively are
 * considered as empty data.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UlData extends Api {
    /** End Point EUI64. */
    private EUI64 epEui;
    /** Unix UTC time of reception, 64 bit, ns resolution. */
    private long rxTime;
    /** Duration or the reception, center of first subpacket to center of last subpacket in ns, optional. */
    @Setter
    private long rxDuration;
    /** End Point packet counter. */
    private int packetCnt;
    /** Reception signal level. */
    private double rssi;
    /** Reception noise level. */
    private double snr;
    /** n Byte End Point user data, might be empty. */
    private int[] userData;
    /** User data format identifier, 8 bit, optional. */
    private Integer format;
    /** True if End Point downlink window is opened. */
    private boolean dlOpen;
    /** True if End Point expects a response in the DL window, requires dlOpen. */
    @Setter
    private boolean responseExp;
    /** True if End Point acknowledges the reception of a DL transmission in the last DL window (packetCnt - 1). */
    @Setter
    private boolean dlAck;

    /**
     * Constructor with arguments.
     *
     * @param epEui
     *                  the end-point EUI64.
     * @param rxTime
     *                  the reception time.
     * @param packetCnt
     *                  End Point packet counter.
     * @param rssi
     *                  The RSSI
     * @param snr
     *                  The SNR
     * @param userData
     *                  n Byte End Point user data, might be empty.
     * @param dlOpen
     *                  True if End Point downlink window is opened.
     */
    public UlData(final EUI64 epEui, final long rxTime, final int packetCnt, final double rssi, final double snr,
            final int[] userData, final boolean dlOpen) {
        this(epEui, rxTime, packetCnt, rssi, snr, userData, dlOpen, null);
    }

    /**
     * Constructor with arguments.
     *
     * @param epEui
     *                  the end-point EUI64.
     * @param rxTime
     *                  the reception time.
     * @param packetCnt
     *                  End Point packet counter.
     * @param rssi
     *                  The RSSI
     * @param snr
     *                  The SNR
     * @param userData
     *                  n Byte End Point user data, might be empty.
     * @param dlOpen
     *                  True if End Point downlink window is opened.
     * @param format
     *                  The optional MPF byte.
     */
    public UlData(final EUI64 epEui, final long rxTime, final int packetCnt, final double rssi, final double snr,
            final int[] userData, final boolean dlOpen, final Integer format) {
        super();
        this.epEui = epEui;
        this.rxTime = rxTime;
        this.packetCnt = packetCnt;
        this.rssi = rssi;
        this.snr = snr;
        this.userData = userData;
        this.dlOpen = dlOpen;
        this.format = format;
    }

    @Override
    public Api createResponse() {
        return new UlDataRsp();
    }
}
