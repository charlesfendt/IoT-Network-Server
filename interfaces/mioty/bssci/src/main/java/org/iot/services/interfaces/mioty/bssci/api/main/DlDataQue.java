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
 * The DL data queue operation is initiated by the Service Center to schedule downlink data at the Base Station for an End Point. This might be done either
 * within the interval between an uplink message and the according downlink window for direct responses or a priory for predefined downlink data. Counter
 * dependent downlink data (i.e. due to application encryption) can only be transmitted in a downlink window with the matching counter. If user data is
 * empty, a pure acknowledgement downlink is queued.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DlDataQue extends Api {
    /** End Point EUI64. */
    private EUI64 epEui;
    /** Queue ID. */
    private long queId;
    /** True if userData is counter dependent (encryption etc.). */
    private boolean cntDepend;
    /**
     * End Point packet counter, must match RX packet counter if cntDepend is set to true, else used as reference only.
     */
    private int[] packetCnt;
    /** n Byte End Point user data, might be empty. */
    private byte[][] userData;
    /** User data format identifier, 8 bit, optional. */
    private Byte format;
    /** Priority. */
    private float prio;
    /** True if End Point response is expected, optional. */
    private Boolean responseExp;
    /** True for priority End Point response, optional. */
    private Boolean responsePrio;
    /** True to request further End Point DL window, optional. */
    private Boolean dlWindReq;
    /** True to send downlink only if End Point expects a response, optional. */
    @Setter
    private Boolean expOnly;

    /**
     * Constructor with arguments.
     *
     * @param epEui
     *                  End Point EUI64.
     * @param packetCnt
     *                  End Point packet counter for which the according userData entry is valid, omitted if cntDepend is false.
     * @param userData
     *                  n Byte End Point user data for each of the m packet counters, single user data entry if cntDepend is false.
     * @param cntDepend
     *                  True if userData is counter dependent.
     */
    public DlDataQue(final EUI64 epEui, final int[] packetCnt, final byte[][] userData, final boolean cntDepend) {
        this(epEui, packetCnt, userData, cntDepend, null, null, null, null);
    }

    /**
     * Constructor with arguments.
     *
     * @param epEui
     *                     End Point EUI64.
     * @param packetCnt
     *                     End Point packet counter for which the according userData entry is valid, omitted if cntDepend is false.
     * @param userData
     *                     n Byte End Point user data for each of the m packet counters, single user data entry if cntDepend is false.
     * @param cntDepend
     *                     True if userData is counter dependent.
     * @param format
     *                     User data format identifier, 8 bit, optional
     * @param responseExp
     *                     True to request End Point response, optional.
     * @param responsePrio
     *                     True to request priority End Point response, optional.
     * @param dlWindReq
     *                     True to request further End Point DL window, optional.
     */
    public DlDataQue(final EUI64 epEui, final int[] packetCnt, final byte[][] userData, final boolean cntDepend,
            final Byte format, final Boolean responseExp, final Boolean responsePrio, final Boolean dlWindReq) {
        this();
        this.epEui = epEui;
        this.packetCnt = packetCnt;
        this.userData = userData;
        this.cntDepend = cntDepend;
        this.format = format;
        this.responseExp = responseExp;
        this.responsePrio = responsePrio;
        this.dlWindReq = dlWindReq;
    }

    @Override
    public Api createResponse() {
        return new DlDataQueRsp();
    }

}
