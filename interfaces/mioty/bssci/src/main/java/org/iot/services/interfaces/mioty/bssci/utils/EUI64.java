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

import java.io.IOException;
import java.math.BigInteger;

import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * EUI64 definition that is used as device UID.
 *
 * @author FendtC
 */
@JsonSerialize(using = EUI64.EUI64Serializer.class)
@JsonDeserialize(using = EUI64.EUI64Deserializer.class)
public final class EUI64 {

    /**
     * Method to create a random EUI64.
     *
     * @return The new random EUI64.
     */
    public static EUI64 random() {
        final var bytes = new byte[8];
        UuidUtils.random(bytes);
        return new EUI64(new BigInteger(bytes).abs());
    }

    /**
     * Method to create an EUI64 based on its bytes representation.
     *
     * @param bytes
     *              EUI64 as bytes.
     * @return EUI64 object.
     */
    public static EUI64 fix(final byte[] bytes) {
        Validate.isTrue(bytes.length == 8, "EUI64 needs to be a 8-Byte value, is: %d", bytes.length); //$NON-NLS-1$
        return new EUI64(new BigInteger(bytes));
    }

    /**
     * Method to create an EUI64 based on its hex-string representation.
     *
     * @param val
     *            EUI64 as hex-string.
     * @return EUI64 object.
     */
    public static EUI64 fromHexString(final String val) {
        Validate.matchesPattern(val, "[a-fA-F0-9]{16}", "EUI64 needs to be a 16-character HEX value, is: %s", val); //$NON-NLS-1$ //$NON-NLS-2$
        return new EUI64(new BigInteger(val, 16));
    }

    @Override
    public String toString() {
        return String.format("%016X", this.value); //$NON-NLS-1$
    }

    /** The EUI64 a big integer. */
    BigInteger value;

    /**
     * Constructor of the EUI64.
     *
     * @param v
     *          The big-integer representation to use.
     */
    private EUI64(final BigInteger v) {
        this.value = v;
    }

    /**
     * Method to retrieve the underlying big-integer representation of the object.
     *
     * @return the underlying big-integer representation of the object.
     */
    public BigInteger asBI() {
        return this.value;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof EUI64) {
            return this.value.equals(((EUI64) obj).value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    /**
     * MsgPack serializer class.
     *
     * @author FendtC
     */
    @SuppressWarnings("serial")
    public static class EUI64Serializer extends StdSerializer<EUI64> {
        /**
         * Default constructor.
         */
        public EUI64Serializer() {
            super(EUI64.class);
        }

        /**
         * Constructor with concrete class.
         *
         * @param t
         *          concrete class.
         */
        public EUI64Serializer(final Class<EUI64> t) {
            super(t);
        }

        @Override
        public void serialize(final EUI64 value, final JsonGenerator gen, final SerializerProvider provider)
                throws IOException {
            gen.writeNumber(value.asBI());
        }
    }

    /**
     * MsgPack deserializer class.
     *
     * @author FendtC
     */
    @SuppressWarnings("serial")
    public static class EUI64Deserializer extends StdDeserializer<EUI64> {
        /**
         * Default constructor.
         */
        public EUI64Deserializer() {
            super(EUI64.class);
        }

        /**
         * Constructor with concrete class.
         *
         * @param t
         *          concrete class.
         */
        public EUI64Deserializer(final Class<EUI64> t) {
            super(t);
        }

        @Override
        public EUI64 deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            return new EUI64(p.getBigIntegerValue());
        }
    }

}
