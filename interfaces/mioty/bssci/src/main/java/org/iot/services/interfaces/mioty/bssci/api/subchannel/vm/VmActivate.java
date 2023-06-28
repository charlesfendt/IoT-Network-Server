/**
 *
 */
package org.iot.services.interfaces.mioty.bssci.api.subchannel.vm;

import org.iot.services.interfaces.mioty.bssci.api.Api;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * BSSCI object for the Sub-Channel VM.
 *
 * @author FendtC
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class VmActivate extends Api {
    /** The MAC-Type to activate. */
    private int macType;

    @Override
    public Api createResponse() {
        return new VmActivateRsp();
    }
}
