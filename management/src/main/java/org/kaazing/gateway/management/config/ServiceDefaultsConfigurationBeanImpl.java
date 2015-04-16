/**
 * Copyright (c) 2007-2014 Kaazing Corporation. All rights reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.kaazing.gateway.management.config;

import static java.util.Arrays.asList;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONObject;
import org.kaazing.gateway.management.gateway.GatewayManagementBean;
import org.kaazing.gateway.server.context.ServiceDefaultsContext;
import org.kaazing.gateway.service.AcceptOptionsContext;
import org.kaazing.gateway.util.Utils;

public class ServiceDefaultsConfigurationBeanImpl implements ServiceDefaultsConfigurationBean {

    private static final AtomicInteger serviceDefaultsIds = new AtomicInteger(0);
    private final ServiceDefaultsContext serviceDefaultsContext;
    private final GatewayManagementBean gatewayBean;
    private final int id;

    public ServiceDefaultsConfigurationBeanImpl(ServiceDefaultsContext serviceDefaultsContext, GatewayManagementBean
            gatewayBean) {
        this.serviceDefaultsContext = serviceDefaultsContext;
        this.gatewayBean = gatewayBean;
        this.id = serviceDefaultsIds.incrementAndGet();
    }

    @Override
    public GatewayManagementBean getGatewayManagementBean() {
        return gatewayBean;
    }

    @Override
    public int getId() {
        return id;
    }

    // Note: the following is copied and modified from ServiceConfigurationBeanImpl
    @Override
    public String getAcceptOptions() {
        JSONObject jsonOptions = new JSONObject();
        JSONObject jsonObj;

        AcceptOptionsContext context = serviceDefaultsContext.getAcceptOptionsContext();
        try {
            if (context != null) {
                Map<String, Object> acceptOptions = context.asOptionsMap();
                Map<String, String> binds = context.getBinds();
                if ((binds != null) && !binds.isEmpty()) {
                    jsonObj = new JSONObject();
                    for (String key : binds.keySet()) {
                        jsonObj.put(key, binds.get(key));
                    }
                    jsonOptions.put("binds", jsonObj);
                }

                String[] sslCiphers = (String[]) acceptOptions.get("ssl.ciphers");
                if (sslCiphers != null && sslCiphers.length > 0) {
                    jsonOptions.put("ssl.ciphers", Utils.asCommaSeparatedString(asList(sslCiphers)));
                }

                boolean isSslEncryptionEnabled = (Boolean) acceptOptions.get("ssl.encryption");
                jsonOptions.put("ssl.encryption",
                        isSslEncryptionEnabled ? "enabled" : "disabled");

                boolean wantClientAuth = (Boolean) acceptOptions.get("ssl.wantClientAuth");
                boolean needClientAuth = (Boolean) acceptOptions.get("ssl.needClientAuth");
                if (needClientAuth) {
                    jsonOptions.put("ssl.verify-client", "required");
                } else if (wantClientAuth) {
                    jsonOptions.put("ssl.verify-client", "optional");
                } else {
                    jsonOptions.put("ssl.verify-client", "none");
                }

                jsonOptions.put("ws.maximum.message.size", acceptOptions.get("ws.maximum.message.size"));

                Integer httpKeepAlive = (Integer) acceptOptions.get("http.keepalive.timeout");
                if (httpKeepAlive != null) {
                    jsonOptions.put("http.keepalive.timeout", httpKeepAlive);
                }

                URI pipeTransport = (URI) acceptOptions.get("pipe.transport");
                if (pipeTransport != null) {
                    jsonOptions.put("pipe.transport", pipeTransport.toString());
                }

                URI tcpTransport = (URI) acceptOptions.get("tcp.transport");
                if (tcpTransport != null) {
                    jsonOptions.put("tcp.transport", tcpTransport.toString());
                }

                URI sslTransport = (URI) acceptOptions.get("ssl.transport");
                if (sslTransport != null) {
                    jsonOptions.put("ssl.transport", sslTransport.toString());
                }

                URI httpTransport = (URI) acceptOptions.get("http.transport");
                if (httpTransport != null) {
                    jsonOptions.put("http.transport", httpTransport.toString());
                }

                long tcpMaxOutboundRate = (Long) acceptOptions.get("tcp.maximum.outbound.rate");
                jsonOptions.put("tcp.maximum.outbound.rate", tcpMaxOutboundRate);
            }
        } catch (Exception ex) {
            // This is only for JSON exceptions, but there should be no way to
            // hit this.
        }

        return jsonOptions.toString();
    }

    @Override
    public String getMimeMappings() {
        JSONObject jsonObj = new JSONObject();

        try {
            Map<String, String> mimeMappings = serviceDefaultsContext.getMimeMappings();

            if (mimeMappings != null) {
                for (String extension : mimeMappings.keySet()) {
                    jsonObj.put(extension, mimeMappings.get(extension));
                }
            }
        } catch (Exception ex) {
            // This is only for JSON exceptions, but there should be no way to
            // hit this.
        }

        return jsonObj.toString();
    }
}
