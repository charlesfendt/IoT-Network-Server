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
package org.iot.services.interfaces.mioty.bssci.utils;

import java.nio.charset.StandardCharsets;

/**
 * Utility class.
 *
 * @author FendtC
 */
public final class BytesUtils {

    /** Helper for hex-string conversion. */
    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII); //$NON-NLS-1$

    /**
     * hidden constructor.
     */
    private BytesUtils() {
        super();
    }

    /**
     * Method to perform an hex dump.
     *
     * @param bytes
     *              Bytes to dump.
     * @return the corresponding hex-string.
     */
    public static String bytesToHex(final byte[] bytes) {
        final var hexChars = new byte[bytes.length * 2];
        for (var j = 0; j < bytes.length; j++) {
            final var v = bytes[j] & 0xFF;
            hexChars[j * 2] = BytesUtils.HEX_ARRAY[v >>> 4];
            hexChars[(j * 2) + 1] = BytesUtils.HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }

    /**
     * Method to reverse a byte array.
     *
     * @param bytes
     *              Bytes to reverse.
     * @return A new array with a reversed content.
     */
    public static byte[] reverse(final byte[] bytes) {
        for (int i = 0, j = bytes.length - 1; i < j; i++, j--) {
            final var tmp = bytes[i];
            bytes[i] = bytes[j];
            bytes[j] = tmp;
        }
        return bytes;
    }

}
