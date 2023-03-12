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
package org.iot.services.interfaces.mioty.bssci.api;

import org.iot.services.interfaces.mioty.bssci.utils.EUI64;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * The attach operation is initiated by the Base Station after receiving an over the air attachment request from an End Point.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Att extends Api {
    /** End Point EUI64. */
    private EUI64 epEui;
    /** Unix UTC time of reception, 64 bit, ns resolution. */
    private long rxTime;
    /** End Point attachment counter. */
    private int attachCnt;
    /** rssi. */
    private double rssi;
    /** SNR value. */
    private double snr;
    /** 4 Byte End Point nonce. */
    private int[] nonce;
    /** 4 Byte End Point signature. */
    private int[] sign;
    /** End Point short address, assigned by the Base Station. */
    private int shAddr;
    /** True if End Point uses dual channel mode. */
    private boolean dualChan;
    /** True if End Point uses UL repetition. */
    private boolean repetition;
    /** True if End Point uses wide carrier offset. */
    private boolean wideCarrOff;
    /** True if End Point uses long DL interblock distance. */
    private boolean longBlkDist;

}
