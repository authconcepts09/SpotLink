package com.thistech.spotlink.client;

/*
 * “The contents of this file are subject to the SpotLink Public License,
 * version 1.0 (the “License”); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.thistech.com/spotlink/spl.
 *
 * Software distributed under the License is distributed on an “AS IS”
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.  See
 * the License for the specific language governing rights and limitations
 * under the License.
 *
 * The Original Code is SpotLink Server Code, release date February 14, 2011
 * The Initial Developer of the Original Code is This Technology, LLC.
 * Copyright (C) 2010-2011, This Technology, LLC
 * All Rights Reserved.
 */

import javax.annotation.Resource;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

import java.util.Properties;

public abstract class AbstractClientFactory<T> {

    @Resource(name = "com.thistech.spotlink.JAXBDataBinding")
    private JAXBDataBinding dataBinding;

    @Resource(name = "com.thistech.spotlink.Properties")
    private Properties properties = null;

    private final Class clazz;
    private final String wsdl;

    protected AbstractClientFactory(Class clazz, String wsdl) {
        this.clazz = clazz;
        this.wsdl = wsdl;
    }

    @SuppressWarnings("unchecked")
    public T create(String address) {
        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        factoryBean.setServiceClass(clazz);
        factoryBean.setWsdlLocation(wsdl);
        factoryBean.setDataBinding(dataBinding);
        factoryBean.setAddress(address);
        Object service = factoryBean.create();

        if (this.properties.containsKey("cxf.service.timeout")) {
            Client client = ClientProxy.getClient(service);
            if (client != null) {
                HTTPConduit conduit = (HTTPConduit) client.getConduit();
                long timeout = Long.parseLong(this.properties.getProperty("cxf.service.timeout"));
                HTTPClientPolicy policy = new HTTPClientPolicy();
                policy.setConnectionTimeout(timeout);
                policy.setReceiveTimeout(timeout);
                conduit.setClient(policy);
            }
        }

        return (T) service;
    }
}
