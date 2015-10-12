/**
 * Copyright 2007-2015, Kaazing Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaazing.gateway.security.auth;

import java.io.IOException;
import java.net.InetAddress;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.kaazing.gateway.server.spi.security.InetAddressCallback;

public class InetAddressCallbackHandler implements CallbackHandler {
    private final InetAddress inetAddress;

    public InetAddressCallbackHandler(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            if (callback instanceof InetAddressCallback) {
                ((InetAddressCallback) callback).setInetAddress(inetAddress);
            } else {
                throw new UnsupportedCallbackException(callback);
            }
        }
    }
}
