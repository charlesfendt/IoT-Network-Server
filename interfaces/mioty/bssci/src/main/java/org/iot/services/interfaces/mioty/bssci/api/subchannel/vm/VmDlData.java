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
package org.iot.services.interfaces.mioty.bssci.api.subchannel.vm;

import org.iot.services.interfaces.mioty.bssci.api.Api;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * The UL data operation is initiated by the Base Station after receiving uplink data from an End Point. Telegrams carrying control data exclusively are
 * considered as empty data.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class VmDlData extends Api {
    /** MAC-Type of the variable MAC. */
    private int macType;
    /** n Byte End Point user data U-MPDU starting with first byte after MAC-Type. */
    private int[] userData;

    /** Transceiver time of transmission, center of first subpacket, 64 bit, ns resolution. */
    private long trxTime;
    /** Unix UTC time of transmission, center of first subpacket, 64 bit, ns resolution. */
    private long sysTime;

    /** Frequency offset from center between primary and secondary channel in Hz . */
    private double freqOff;

    /** Reception signal level. */
    private double ulRrssi;
    /** Reception noise level. */
    private double ulSsnr;

    /** Carrier offset range, 5 or 1. */
    private int carrOffRange;
    /** Carrier spacing step size Bc, 0 = narrow, 1 = standard, 2 = wide. */
    private int carrSpace;
    /** Header and payload CRC, crc[0] = header CRC, crc[1] = payload CRC. */
    private int[] ulCrc;

    /** Transmission start time indicator, 21 to 16383, optional, default 128. */
    private Integer tsi;
    /** True to enable sync burst. */
    private boolean syncBurst;
    /** True to enable dual channel. */
    private boolean dualChan;
    /** True to enable core frame repetition. */
    private boolean repetition;
    /** True to enable long block distance. */
    private boolean longBlkDist;

    @Override
    public Api createResponse() {
        return new VmDlDataRsp();
    }

    /**
     * Subpackets object.
     *
     * @author FendtC
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class VmUlDataSubpackets {
        /** Subpacket signal to noise ratio in dB. */
        private double[] snr;
        /** Subpacket signal strength in dBm. */
        private double[] rssi;
        /** Subpacket frequencies in Hz. */
        private double[] frequency;
        /** Subpacket phases in degree +-180, optional. */
        private double[] phase;
    }
}
