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
 * The attach propagate operation is initiated by the Service Center to propagate an End Point attachment to the Base Station. The attachment information
 * can either be acquired via an over the air attachment at another Base Station or in the form of an offline preattachment of an End Point (as required
 * for unidirectional End Points).
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AttPrp extends Api {
    /** End Point EUI64. */
    private EUI64 epEui;
    /** True if End Point is bidirectional. */
    private boolean bidi;
    /** 16 Byte End Point network session key. */
    private byte[] nwkSnKey;
    /** End Point short address, assigned by the Base Station. */
    private int shAddr;
    /** Last known End Point packet counter. */
    private int lastPacketCnt;
    /** True if End Point uses dual channel mode. */
    private boolean dualChan;
    /** True if End Point uses UL repetition. */
    private boolean repetition;
    /** True if End Point uses wide carrier offset. */
    private boolean wideCarrOff;
    /** True if End Point uses long DL interblock distance. */
    private boolean longBlkDist;

    @Override
    public Api createResponse() {
        return new AttPrpRsp();
    }

}
