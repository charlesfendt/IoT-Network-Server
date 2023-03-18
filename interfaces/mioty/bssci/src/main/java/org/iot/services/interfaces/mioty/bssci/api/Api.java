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

import java.io.IOException;

import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;

/**
 * BSSCI object.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "command")
@JsonSubTypes({ //
        @JsonSubTypes.Type(value = Connect.class, name = "con"), //
        @JsonSubTypes.Type(value = ConnectRsp.class, name = "conRsp"), //
        @JsonSubTypes.Type(value = ConnectCmp.class, name = "conCmp"), //
        @JsonSubTypes.Type(value = Ping.class, name = "ping"), //
        @JsonSubTypes.Type(value = PingRsp.class, name = "pingRsp"), //
        @JsonSubTypes.Type(value = PingCmp.class, name = "pingCmp"), //
        @JsonSubTypes.Type(value = Status.class, name = "status"), //
        @JsonSubTypes.Type(value = StatusRsp.class, name = "statusRsp"), //
        @JsonSubTypes.Type(value = StatusCmp.class, name = "statusCmp"), //
        @JsonSubTypes.Type(value = Att.class, name = "att"), //
        @JsonSubTypes.Type(value = AttRsp.class, name = "attRsp"), //
        @JsonSubTypes.Type(value = AttCmp.class, name = "attCmp"), //
        @JsonSubTypes.Type(value = Det.class, name = "det"), //
        @JsonSubTypes.Type(value = DetRsp.class, name = "detRsp"), //
        @JsonSubTypes.Type(value = DetCmp.class, name = "detCmp"), //
        @JsonSubTypes.Type(value = AttPrp.class, name = "attPrp"), //
        @JsonSubTypes.Type(value = AttPrpRsp.class, name = "attPrpRsp"), //
        @JsonSubTypes.Type(value = AttPrpCmp.class, name = "attPrpCmp"), //
        @JsonSubTypes.Type(value = DetPrp.class, name = "detPrp"), //
        @JsonSubTypes.Type(value = DetPrpRsp.class, name = "detPrpRsp"), //
        @JsonSubTypes.Type(value = DetPrpCmp.class, name = "detPrpCmp"), //
        @JsonSubTypes.Type(value = UlData.class, name = "ulData"), //
        @JsonSubTypes.Type(value = UlDataRsp.class, name = "ulDataRsp"), //
        @JsonSubTypes.Type(value = UlDataCmp.class, name = "ulDataCmp"), //
        @JsonSubTypes.Type(value = DlDataQue.class, name = "dlDataQue"), //
        @JsonSubTypes.Type(value = DlDataQueRsp.class, name = "dlDataQueRsp"), //
        @JsonSubTypes.Type(value = DlDataQueCmp.class, name = "dlDataQueCmp"), //
        @JsonSubTypes.Type(value = DlDataRev.class, name = "dlDataRev"), //
        @JsonSubTypes.Type(value = DlDataRevRsp.class, name = "dlDataRevRsp"), //
        @JsonSubTypes.Type(value = DlDataRevCmp.class, name = "dlDataRevCmp"), //
        @JsonSubTypes.Type(value = DlDataRes.class, name = "dlDataRes"), //
        @JsonSubTypes.Type(value = DlDataResRsp.class, name = "dlDataResRsp"), //
        @JsonSubTypes.Type(value = DlDataResCmp.class, name = "dlDataRevCmp"), //
        @JsonSubTypes.Type(value = DlRxStat.class, name = "dlRxStat"), //
        @JsonSubTypes.Type(value = DlRxStatRsp.class, name = "dlRxStatRsp"), //
        @JsonSubTypes.Type(value = DlRxStatCmp.class, name = "dlRxStatCmp"), //
        @JsonSubTypes.Type(value = DlRxStatQry.class, name = "dlRxStatQry"), //
        @JsonSubTypes.Type(value = DlRxStatQryRsp.class, name = "dlRxStatQryRsp"), //
        @JsonSubTypes.Type(value = DlRxStatQryCmp.class, name = "dlRxStatQryCmp"), //
        @JsonSubTypes.Type(value = Error.class, name = "error"), //
        @JsonSubTypes.Type(value = ErrorAck.class, name = "errorAck") })
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public abstract class Api {

    /** Logger of the class. */
    private static final Logger LOG = LoggerFactory.getLogger(Api.class);

    /** The mapper for MsgPack objects. */
    private static final ObjectMapper MAPPER_MSGPACK = new ObjectMapper(new MessagePackFactory());
    /** The mapper for JSON objects. */
    private static final ObjectMapper MAPPER_JSON = new ObjectMapper();

    /** The operation ID. */
    private int opId = 0;

    /**
     * Default constructor.
     */
    @JsonCreator
    public Api() {
        super();
    }

    /**
     * Method to generate the MsgPack representation of the object.
     *
     * @return the raw MsgPack object.
     * @throws JsonProcessingException
     *                                 Any serialization error.
     */
    public byte[] toMsgPack() throws JsonProcessingException {
        return Api.MAPPER_MSGPACK.writeValueAsBytes(this);
    }

    /**
     * Method to generate the JSON representation of the object.
     *
     * @return the JSON object as string.
     */
    public String toJson() {
        try {
            return Api.MAPPER_JSON.writeValueAsString(this);
        } catch (final JsonProcessingException e) {
            return "error"; //$NON-NLS-1$
        }
    }

    /**
     * Method to create a POJO based on the given MsgPack raw message.
     *
     * @param bytes
     *              the MsgPack to deserialize.
     * @return The POJO associated to the MsgPack
     * @throws IOException
     *                     Any I/O error.
     */
    public static Api fromMsgPack(final byte[] bytes) throws IOException {
        return Api.MAPPER_MSGPACK.readValue(bytes, Api.class);
    }

    /**
     * Method to extract the operation ID from the raw message.
     *
     * @param bytes
     *              the MsgPack raw message to parse.
     * @return The operation ID from the message.
     */
    public static int extractOpId(final byte[] bytes) {
        try {
            final var tree = Api.MAPPER_MSGPACK.readTree(bytes);
            if (tree.has("opId")) { //$NON-NLS-1$
                final var opId = tree.get("opId"); //$NON-NLS-1$
                return opId.asInt();
            }
        } catch (final Exception ex) {
            Api.LOG.error("cannot parse message", ex); //$NON-NLS-1$
        }
        return 0;
    }

    /**
     * Setter method.
     *
     * @param opId
     *             The new operation ID.
     * @return the current object, after modification.
     */
    public Api setOpId(final int opId) {
        this.opId = opId;
        return this;
    }

    @Override
    public String toString() {
        return this.toJson();
    }

    /**
     * Method to create the response object to the current request.
     *
     * @return the response object or NULL if the request don't need any answer.
     */
    public Api createResponse() {
        return null;
    }

}
