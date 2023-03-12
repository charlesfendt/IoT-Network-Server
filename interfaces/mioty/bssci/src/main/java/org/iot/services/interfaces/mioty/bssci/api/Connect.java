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
import lombok.Setter;

/**
 * BSSCI object.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Connect extends Api {
    /** Protocol version, “major.minor.patch”, current “1.0.0”. */
    private String version;
    /** Base Station EUI64. */
    private EUI64 bsEui;

    /** the gateway vendor. */
    @Setter
    private String vendor;
    /** the gateway model. */
    @Setter
    private String model;
    /** Gateway name. */
    @Setter
    private String name;
    /** Software version. */
    @Setter
    private String swVersion;

    /** True if Base Station is bidirectional. */
    private boolean bidi;
    /** Geographic location [Latitude, Longitude, Altitude], optional. */
    private float[] geoLocation;
    /** UUID of the Base station session. */
    @Setter
    private int[] snBsUuid;
    /** Minimum required known Base Station operation ID to resume previous session, optional. */
    private Integer snBsOpId;
    /** Maximum known Service Center operation ID to resume previous session, optional. */
    private Integer snScOpId;

    /**
     * Constructor with arguments.
     *
     * @param version
     *                  Service Center EUI64.
     * @param bsEui
     *                  Base Station EUI64.
     * @param swVersion
     *                  Software version, optional.
     * @param bidi
     *                  True if Base Station is bidirectional.
     */
    public Connect(final String version, final EUI64 bsEui, final String swVersion, final boolean bidi) {
        this(version, bsEui, swVersion, bidi, null, null, null);
    }

    /**
     * Constructor with arguments.
     *
     * @param version
     *                    Service Center EUI64.
     * @param bsEui
     *                    Base Station EUI64.
     * @param swVersion
     *                    Software version, optional.
     * @param bidi
     *                    True if Base Station is bidirectional.
     * @param geoLocation
     *                    Geographic location [Latitude, Longitude, Altitude], optional.
     * @param snBsOpId
     *                    Minimum required known Base Station operation ID to resume previous session, optional.
     * @param snScOpId
     *                    Maximum known Service Center operation ID to resume previous session, optional.
     */
    public Connect(final String version, final EUI64 bsEui, final String swVersion, final boolean bidi,
            final float[] geoLocation, final Integer snBsOpId, final Integer snScOpId) {
        this.version = version;
        this.bsEui = bsEui;
        this.swVersion = swVersion;
        this.bidi = bidi;
        this.geoLocation = geoLocation;
        this.snBsOpId = snBsOpId;
        this.snScOpId = snScOpId;
    }

}
