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
 * The DL RX status operation is initiated by the Base Station after receiving a DL RX status response control segment from an End Point.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DlRxStat extends Api {
    /** End Point EUI64. */
    private EUI64 epEui;
    /** Unix UTC time of reception, 64 bit, ns resolution. */
    private long rxTime;
    /** End Point DL reception signal level. */
    private double dlRxSignalLevel;
    /** End Point DL reception noise level. */
    private double dlRxNoiseLevel;

    @Override
    public Api createResponse() {
        return new DlRxStatRsp();
    }
}
