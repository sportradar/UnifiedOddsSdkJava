/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static java.util.stream.Collectors.toList;

import com.sportradar.uf.sportsapi.datamodel.SapiSport;
import com.sportradar.uf.sportsapi.datamodel.SapiSportsEndpoint;
import java.util.Arrays;
import lombok.val;

@SuppressWarnings("MethodLength")
public class SapiSports {

    public static SapiSportsEndpoint allSports() {
        val sports = new String[][] {
            { "sr:sport:143", "7BallRun" },
            { "sr:sport:192", "Air Racing" },
            { "sr:sport:43", "Alpine Skiing" },
            { "sr:sport:74", "Aquatics" },
            { "sr:sport:75", "Archery" },
            { "sr:sport:95", "Artistic Swimming" },
            { "sr:sport:36", "Athletics" },
            { "sr:sport:13", "Aussie Rules" },
            { "sr:sport:31", "Badminton" },
            { "sr:sport:15", "Bandy" },
            { "sr:sport:3", "Baseball" },
            { "sr:sport:2", "Basketball" },
            { "sr:sport:155", "Basketball 3x3" },
            { "sr:sport:130", "Basque Pelota" },
            { "sr:sport:157", "Beach Handball" },
            { "sr:sport:60", "Beach Soccer" },
            { "sr:sport:98", "Beach Tennis" },
            { "sr:sport:34", "Beach Volley" },
            { "sr:sport:44", "Biathlon" },
            { "sr:sport:41", "Bikes" },
            { "sr:sport:45", "Bobsleigh" },
            { "sr:sport:32", "Bowls" },
            { "sr:sport:10", "Boxing" },
            { "sr:sport:204", "Breaking" },
            { "sr:sport:92", "Canoe slalom" },
            { "sr:sport:72", "Canoeing" },
            { "sr:sport:33", "Chess" },
            { "sr:sport:205", "Cornhole" },
            { "sr:sport:21", "Cricket" },
            { "sr:sport:46", "Cross-Country" },
            { "sr:sport:28", "Curling" },
            { "sr:sport:17", "Cycling" },
            { "sr:sport:91", "BMX racing" },
            { "sr:sport:179", "Cycling BMX Freestyle" },
            { "sr:sport:180", "Cycling BMX Racing" },
            { "sr:sport:141", "Cycling Cycle Ball" },
            { "sr:sport:88", "Mountain Bike" },
            { "sr:sport:97", "Track cycling" },
            { "sr:sport:22", "Darts" },
            { "sr:sport:177", "DEPRECATED sc" },
            { "sr:sport:96", "Diving" },
            { "sr:sport:147", "Drag Racing" },
            { "sr:sport:42", "DTM" },
            { "sr:sport:107", "eSport" },
            { "sr:sport:153", "eBasketball" },
            { "sr:sport:197", "eCricket" },
            { "sr:sport:195", "eIce Hockey" },
            { "sr:sport:193", "Endurance Racing" },
            { "sr:sport:76", "Equestrian" },
            { "sr:sport:137", "eSoccer" },
            { "sr:sport:164", "ESport Apex Legends" },
            { "sr:sport:158", "ESport Arena of Valor" },
            { "sr:sport:162", "ESport Artifact" },
            { "sr:sport:175", "Esport Auto Chess" },
            { "sr:sport:168", "ESport Brawl Stars" },
            { "sr:sport:118", "ESport Call of Duty" },
            { "sr:sport:171", "ESport MTG" },
            { "sr:sport:133", "ESport Clash Royale" },
            { "sr:sport:109", "ESport Counter-Strike" },
            { "sr:sport:123", "ESport Crossfire" },
            { "sr:sport:111", "ESport Dota" },
            { "sr:sport:173", "Esport Dota Underlords" },
            { "sr:sport:154", "ESport Dragon Ball FighterZ" },
            { "sr:sport:176", "Esport Fighting Games" },
            { "sr:sport:170", "ESport Fortnite" },
            { "sr:sport:132", "ESport Gears of War" },
            { "sr:sport:124", "ESport Halo" },
            { "sr:sport:113", "ESport Hearthstone" },
            { "sr:sport:114", "ESport Heroes of the Storm" },
            { "sr:sport:134", "ESport King of Glory" },
            { "sr:sport:110", "ESport League of Legends" },
            { "sr:sport:167", "ESport Madden NFL" },
            { "sr:sport:178", "ESport Motorsport" },
            { "sr:sport:121", "ESport Overwatch" },
            { "sr:sport:161", "ESport Paladins" },
            { "sr:sport:140", "ESport PlayerUnknowns Battlegrounds" },
            { "sr:sport:166", "ESport Pro Evolution Soccer" },
            { "sr:sport:139", "ESport Quake" },
            { "sr:sport:125", "ESport Rainbow Six" },
            { "sr:sport:128", "ESport Rocket League" },
            { "sr:sport:119", "ESport Smite" },
            { "sr:sport:160", "ESport SSBM" },
            { "sr:sport:112", "ESport StarCraft" },
            { "sr:sport:127", "ESport Street Fighter V" },
            { "sr:sport:174", "Esport Teamfight Tactics" },
            { "sr:sport:156", "ESport Tekken" },
            { "sr:sport:159", "ESport TF2" },
            { "sr:sport:120", "ESport Vainglory" },
            { "sr:sport:194", "ESport Valorant" },
            { "sr:sport:122", "ESport WarCraft III" },
            { "sr:sport:199", "ESport Wild Rift" },
            { "sr:sport:115", "ESport World of Tanks" },
            { "sr:sport:152", "ESport World of Warcraft" },
            { "sr:sport:196", "eTennis" },
            { "sr:sport:198", "eVolleyball" },
            { "sr:sport:77", "Fencing" },
            { "sr:sport:24", "Field hockey" },
            { "sr:sport:102", "Figure Skating" },
            { "sr:sport:53", "Finnish Baseball" },
            { "sr:sport:172", "Fishing" },
            { "sr:sport:7", "Floorball" },
            { "sr:sport:16", "American Football" },
            { "sr:sport:40", "Formula 1" },
            { "sr:sport:189", "Formula 2" },
            { "sr:sport:142", "Formula E" },
            { "sr:sport:103", "Freestyle Skiing" },
            { "sr:sport:29", "Futsal" },
            { "sr:sport:135", "Gaelic Football" },
            { "sr:sport:136", "Gaelic Hurling" },
            { "sr:sport:27", "Gaelic sports" },
            { "sr:sport:9", "Golf" },
            { "sr:sport:58", "Greyhound" },
            { "sr:sport:78", "Gymnastics" },
            { "sr:sport:6", "Handball" },
            { "sr:sport:55", "Horse racing" },
            { "sr:sport:73", "Horseball" },
            { "sr:sport:4", "Ice Hockey" },
            { "sr:sport:163", "Indoor Soccer" },
            { "sr:sport:165", "Indy Lights" },
            { "sr:sport:129", "Indy Racing" },
            { "sr:sport:57", "Inline Hockey" },
            { "sr:sport:79", "Judo" },
            { "sr:sport:138", "Kabaddi" },
            { "sr:sport:181", "Karate" },
            { "sr:sport:39", "Lacrosse" },
            { "sr:sport:51", "Luge" },
            { "sr:sport:182", "Marathon Swimming" },
            { "sr:sport:117", "MMA" },
            { "sr:sport:80", "Modern Pentathlon" },
            { "sr:sport:149", "Modified Racing" },
            { "sr:sport:68", "Moto2" },
            { "sr:sport:69", "Moto3" },
            { "sr:sport:144", "Motocross" },
            { "sr:sport:67", "MotoGP" },
            { "sr:sport:11", "Motorsport" },
            { "sr:sport:190", "Motorcycle Racing" },
            { "sr:sport:202", "Muay Thai" },
            { "sr:sport:185", "Nascar Camping World Truck" },
            { "sr:sport:70", "Nascar Cup Series" },
            { "sr:sport:186", "Nascar Xfinity Series" },
            { "sr:sport:35", "Netball" },
            { "sr:sport:187", "NHRA" },
            { "sr:sport:47", "Nordic Combined" },
            { "sr:sport:150", "Off Road" },
            { "sr:sport:30", "Olympics" },
            { "sr:sport:87", "Olympics Youth" },
            { "sr:sport:71", "Padel" },
            { "sr:sport:61", "Pesapallo" },
            { "sr:sport:169", "Petanque" },
            { "sr:sport:206", "Pickleball" },
            { "sr:sport:116", "Polo" },
            { "sr:sport:25", "Pool" },
            { "sr:sport:201", "Racquetball" },
            { "sr:sport:101", "Rally" },
            { "sr:sport:93", "Rhythmic gymnastics" },
            { "sr:sport:89", "Riding" },
            { "sr:sport:38", "Rink Hockey" },
            { "sr:sport:64", "Rowing" },
            { "sr:sport:12", "Rugby" },
            { "sr:sport:59", "Rugby League" },
            { "sr:sport:81", "Sailing" },
            { "sr:sport:56", "Schwingen" },
            { "sr:sport:126", "Sepak Takraw" },
            { "sr:sport:82", "Shooting" },
            { "sr:sport:105", "Short Track" },
            { "sr:sport:183", "Skateboarding" },
            { "sr:sport:104", "Skeleton" },
            { "sr:sport:48", "Ski Jumping" },
            { "sr:sport:19", "Snooker" },
            { "sr:sport:49", "Snowboard" },
            { "sr:sport:65", "Freestyle" },
            { "sr:sport:66", "Snowboardcross/Parallel" },
            { "sr:sport:1", "Soccer" },
            { "sr:sport:106", "Soccer Mythical" },
            { "sr:sport:203", "Soccer Specials" },
            { "sr:sport:54", "Softball" },
            { "sr:sport:18", "Specials" },
            { "sr:sport:146", "Speed Boat Racing" },
            { "sr:sport:50", "Speed Skating" },
            { "sr:sport:131", "Speedway" },
            { "sr:sport:184", "Sport Climbing" },
            { "sr:sport:145", "Sprint Car Racing" },
            { "sr:sport:37", "Squash" },
            { "sr:sport:191", "Stock Car Racing" },
            { "sr:sport:62", "Streethockey" },
            { "sr:sport:99", "Sumo" },
            { "sr:sport:100", "Superbike" },
            { "sr:sport:90", "Surfing" },
            { "sr:sport:52", "Swimming" },
            { "sr:sport:200", "T-Basket" },
            { "sr:sport:20", "Table Tennis" },
            { "sr:sport:83", "Taekwondo" },
            { "sr:sport:5", "Tennis" },
            { "sr:sport:188", "Touring Car Racing" },
            { "sr:sport:94", "Trampoline Gymnastics" },
            { "sr:sport:84", "Triathlon" },
            { "sr:sport:8", "Trotting" },
            { "sr:sport:151", "Truck &amp; Tractor Pulling" },
            { "sr:sport:23", "Volleyball" },
            { "sr:sport:26", "Waterpolo" },
            { "sr:sport:85", "Weightlifting" },
            { "sr:sport:14", "Winter Sports" },
            { "sr:sport:63", "World Championship" },
            { "sr:sport:108", "World Lottery" },
            { "sr:sport:86", "Wrestling" },
        };
        val allSports = Arrays
            .stream(sports)
            .map(sport -> {
                SapiSport sapiSport = new SapiSport();
                sapiSport.setId(sport[0]);
                sapiSport.setName(sport[1]);
                return sapiSport;
            })
            .collect(toList());
        val result = new SapiSportsEndpoint();
        result.getSport().addAll(allSports);
        return result;
    }

    public static SapiSport soccer() {
        SapiSport soccer = new SapiSport();
        soccer.setId("sr:sport:1");
        soccer.setName("Soccer");
        return soccer;
    }

    public static SapiSport stockCarRacing() {
        SapiSport stockCarRacing = new SapiSport();
        stockCarRacing.setId("sr:sport:191");
        stockCarRacing.setName("Stock Car Racing");
        return stockCarRacing;
    }

    public static SapiSport formula1() {
        SapiSport formula1 = new SapiSport();
        formula1.setId("sr:sport:40");
        formula1.setName("Formula 1");
        return formula1;
    }
}
