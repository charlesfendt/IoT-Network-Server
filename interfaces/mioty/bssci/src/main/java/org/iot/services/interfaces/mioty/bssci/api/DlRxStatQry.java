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

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The DL RX status query operation is initiated by the Service Center to schedule a DL RX status query control segment for the next downlink transmission
 * of the Base Station to an End Point.
 */
@Getter
@AllArgsConstructor
public class DlRxStatQry extends Api {
    /** End-point EUI. */
    private final EUI64 epEui;

    /**
     * Default constructor.
     */
    protected DlRxStatQry() {
        this(null);
    }

    @Override
    public Api createResponse() {
        return new DlRxStatQryRsp();
    }

}
