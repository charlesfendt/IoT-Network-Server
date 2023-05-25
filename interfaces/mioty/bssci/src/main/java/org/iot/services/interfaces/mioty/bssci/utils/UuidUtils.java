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

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

/**
 * Utility class for UUID handling.
 *
 * @author FendtC
 */
public final class UuidUtils {

    /** size of the byte array associated to the UUID. */
    private static final int UUID_SIZE = 16;

    /** random for the UID generation. */
    private static final Random random = new SecureRandom();

    /**
     * Hidden constructor.
     */
    private UuidUtils() {
        super();
    }

    /**
     * Method to convert a UUID to a byte array.
     *
     * @param uuidParam
     *                  The UUID to convert.
     * @return The converted representation of the UUID in bytes.
     */
    public static byte[] convertUuidToBytes(final UUID uuidParam) {
        final var uuid = uuidParam == null ? UUID.randomUUID() : uuidParam;
        final var bb = ByteBuffer.wrap(new byte[UuidUtils.UUID_SIZE]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    /**
     * Method to convert a UUID to a byte array.
     *
     * @param uuidParam
     *                  The UUID to convert.
     * @return The converted representation of the UUID in integers.
     */
    public static int[] convertUuidToInts(final UUID uuidParam) {
        final var bb = UuidUtils.convertUuidToBytes(uuidParam);
        final var len = bb.length;
        final var result = new int[len];
        for (var index = 0; index < len; index++) {
            result[index] = bb[index];
        }
        return result;
    }

    /**
     * Method to convert a byte array to a UUID.
     *
     * @param bytesParam
     *                   the byte array to convert.
     * @return the associated UUID
     */
    public static UUID convertBytesToUuid(final byte[] bytesParam) {
        if (bytesParam == null) {
            return null;
        }

        byte[] bytes;
        final var length = bytesParam.length;
        if (length < UuidUtils.UUID_SIZE) {
            bytes = new byte[UuidUtils.UUID_SIZE];
            System.arraycopy(bytesParam, 0, bytes, 0, length);
        } else {
            bytes = bytesParam;
        }
        final var byteBuffer = ByteBuffer.wrap(bytes);
        final var high = byteBuffer.getLong();
        final var low = byteBuffer.getLong();
        return new UUID(high, low);
    }

    /**
     * method to fill a byte array with random data.
     *
     * @param bytes
     *              the byte array to fill.
     */
    public static void random(final byte[] bytes) {
        if (bytes != null) {
            UuidUtils.random.nextBytes(bytes);
        }
    }
}
