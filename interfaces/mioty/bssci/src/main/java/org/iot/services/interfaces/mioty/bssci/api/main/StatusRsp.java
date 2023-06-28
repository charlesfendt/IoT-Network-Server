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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * BSSCI object.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class StatusRsp extends Api {
    /** Status code, using POSIX error numbers, 0 for “ok”. */
    private int code;
    /** Status message. */
    private String message;
    /** Unix UTC system time, 64 bit, ns resolution. */
    private long time;
    /** Fraction of TX time, sliding window over one hour. */
    private float dutyCycle;
    /** Geographic location [Latitude, Longitude, Altitude], optional. */
    private float[] geoLocation;
    /** System uptime in seconds, optional. */
    private Long uptime;
    /** System temperature in degree Celsius, optional. */
    private Double temp;
    /** CPU utilization, normalized to 1.0 for all cores, optional. */
    private Double cpuLoad;
    /** Memory utilization, normalized to 1.0, optional. */
    private Double memLoad;

    /**
     * Constructor with arguments.
     *
     * @param status
     *               The current gateway status.
     */
    public StatusRsp(final EnumStatus status) {
        this(status == EnumStatus.OK ? 0 : 1, status == EnumStatus.OK ? "" : "genric error", //$NON-NLS-1$ //$NON-NLS-2$
                System.currentTimeMillis() * 1000L, 0.1F, null, null, null, null, null);
    }

    /**
     * Constructor with arguments.
     *
     * @param status
     *                    The current gateway status.
     * @param geoLocation
     *                    Geographic location [Latitude, Longitude, Altitude], optional.
     * @param uptime
     *                    System uptime in seconds, optional.
     * @param temp
     *                    System temperature in degree Celsius, optional.
     * @param cpuLoad
     *                    CPU utilization, normalized to 1.0 for all cores, optional.
     * @param memLoad
     *                    Memory utilization, normalized to 1.0, optional.
     */
    public StatusRsp(final EnumStatus status, final float[] geoLocation, final Long uptime, final Double temp,
            final Double cpuLoad, final Double memLoad) {
        this(status == EnumStatus.OK ? 0 : 1, status == EnumStatus.OK ? "" : "genric error", //$NON-NLS-1$ //$NON-NLS-2$
                System.currentTimeMillis() * 1000L, 0.1F, geoLocation, uptime, temp, cpuLoad, memLoad);
    }

    @Override
    public Api createResponse() {
        return new StatusCmp();
    }

    /**
     * Enumeration for the status.
     *
     * @author FendtC
     */
    public enum EnumStatus {
        /** status: Ok. */
        OK,
        /** status: Error. */
        ERROR;
    }
}
