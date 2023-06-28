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

/**
 * The DL data result operation is initiated by the Base Station after queued DL data has either been sent or discarded.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DlDataRes extends Api {
    /** End Point EUI64 */
    private EUI64 epEui;
    /** Queue ID. */
    private long queId;
    /** Unix UTC time of transmission, 64 bit, ns resolution */
    private long txTime;
    /** End Point packet counter of the scheduled data */
    private int packetCnt;
    /** “sent”, “expired”, “invalid”, ... */
    private String result;

    /**
     * Constructor with parameters.
     *
     * @param epEui
     *                  End Point EUI64.
     * @param packetCnt
     *                  End Point packet counter, only if result is “sent”.
     * @param result
     *                  “sent”, “expired”, “invalid”.
     */
    public DlDataRes(final EUI64 epEui, final int packetCnt, final String result) {
        super();
        this.epEui = epEui;
        this.packetCnt = packetCnt;
        this.result = result;
    }

    @Override
    public Api createResponse() {
        return new DlDataResRsp();
    }

}
