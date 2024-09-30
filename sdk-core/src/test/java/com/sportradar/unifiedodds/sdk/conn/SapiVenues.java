/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.sportradar.uf.sportsapi.datamodel.SapiCourse;
import com.sportradar.uf.sportsapi.datamodel.SapiHole;
import com.sportradar.uf.sportsapi.datamodel.SapiVenue;

@SuppressWarnings("MagicNumber")
public class SapiVenues {

    public static SapiVenue munichFootballArena() {
        SapiVenue venue = new SapiVenue();
        venue.setId("sr:venue:574");
        venue.setName("Munich Football Arena");
        venue.setCapacity(75000);
        venue.setCityName("Munich");
        venue.setCountryName("Germany");
        venue.setCountryCode("DEU");
        venue.setMapCoordinates("48.218777,11.624748");
        return venue;
    }

    public static SapiVenue minuteMaidParkHouston() {
        SapiVenue venue = new SapiVenue();
        venue.setId("sr:venue:8103");
        venue.setName("Minute Maid Park");
        venue.setCapacity(41000);
        venue.setCityName("Houston");
        venue.setCountryName("USA");
        venue.setCountryCode("USA");
        venue.setMapCoordinates("29.7577058,-95.35453989999999");
        venue.setState("TX");
        return venue;
    }

    public static class FullyPopulatedSapiVenue {

        public static final String URN = "sr:venue:1234";

        public static SapiVenue getVenue() {
            SapiVenue venue = new SapiVenue();
            venue.setId(URN);
            venue.setName("Allianz Arena");
            venue.setCityName("Munich");
            venue.setCountryCode("DEU");
            venue.setCountryName("GermanyVenu");
            venue.setCapacity(75000);
            venue.setMapCoordinates("48.218775, 11.624753");
            venue.setState("Bavaria");
            venue.getCourse().add(getCourse());
            return venue;
        }

        private static SapiCourse getCourse() {
            SapiCourse course = new SapiCourse();
            course.setId("sr:venue:4522");
            course.setName("Malaga Course 4");
            course.getHole().add(getHole());
            return course;
        }

        private static SapiHole getHole() {
            SapiHole hole = new SapiHole();
            hole.setNumber(1);
            hole.setPar(4);
            return hole;
        }
    }
}
