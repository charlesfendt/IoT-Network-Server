/**
 *
 */
package org.iot.services.interfaces.mioty.bssci.api.subchannel.vm;

import org.iot.services.interfaces.mioty.bssci.api.Api;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * BSSCI object for the Sub-Channel VM.
 *
 * @author FendtC
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VmDeactivateRsp extends Api {

    @Override
    public Api createResponse() {
        return new VmDeactivateCmp();
    }
}
