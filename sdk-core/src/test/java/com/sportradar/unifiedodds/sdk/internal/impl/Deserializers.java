/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl;

import com.sportradar.uf.custombet.datamodel.CapiResponse;
import com.sportradar.uf.sportsapi.datamodel.DescMarket;
import javax.xml.bind.JAXBContext;
import lombok.SneakyThrows;
import lombok.val;

public class Deserializers {

    @SneakyThrows
    public static Deserializer sportsApiDeserializer() {
        val sportsApiPackageNameTakenFromOneClassFromIt = DescMarket.class.getPackage().getName();
        return new DeserializerImpl(JAXBContext.newInstance(sportsApiPackageNameTakenFromOneClassFromIt));
    }

    @SneakyThrows
    public static Deserializer customBetApiDeserializer() {
        val customBetPackageNameTakenFromOneClassFromIt = CapiResponse.class.getPackage().getName();
        return new DeserializerImpl(JAXBContext.newInstance(customBetPackageNameTakenFromOneClassFromIt));
    }
}
