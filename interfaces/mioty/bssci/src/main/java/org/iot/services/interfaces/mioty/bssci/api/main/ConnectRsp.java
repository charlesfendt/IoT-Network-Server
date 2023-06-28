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
 * BSSCI object.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ConnectRsp extends Api {
    /** Requested protocol version, “major.minor.patch”. */
    private String version;
    /** Service Center EUI64. */
    private EUI64 scEui;
    /** Vendor of the Service Center, optional. */
    private String vendor;
    /** Model of the Service Center, optional. */
    private String model;
    /** Name of the Service Center, optional. */
    private String name;
    /** Software version, optional. */
    private String swVersion;
    /** True if a previous session is resumed. */
    private boolean snResume;
    /** True if a previous session is resumed. */
    @Setter
    private byte[] snScUuid;

    /**
     * Constructor with arguments.
     *
     * @param scEui
     *                 Service Center EUI64.
     * @param snResume
     *                 True if a previous session is resumed.
     */
    public ConnectRsp(final EUI64 scEui, final boolean snResume) {
        this();
        this.scEui = scEui;
        this.snResume = snResume;
    }

    @Override
    public Api createResponse() {
        return new ConnectCmp();
    }

}
