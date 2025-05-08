/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static java.util.Locale.ENGLISH;
import static java.util.stream.Collectors.toList;

import com.sportradar.uf.sportsapi.datamodel.SapiSport;
import com.sportradar.uf.sportsapi.datamodel.SapiSportsEndpoint;
import com.sportradar.utils.Urn;
import com.sportradar.utils.domain.names.LanguageHolder;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Function;
import lombok.val;

@SuppressWarnings("MethodLength")
public class SapiSports {

    public static final String WORLD_LOTTERY_SPORT_ID = "sr:sport:108";
    private static final String[][] SPORTS_ENGLISH = new String[][] {
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
        { "sr:sport:207", "Cyclo-Cross" },
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
        { WORLD_LOTTERY_SPORT_ID, "World Lottery" },
        { "sr:sport:86", "Wrestling" },
    };

    private static final String[][] SPORTS_CHINESE = new String[][] {
        { "sr:sport:143", "七球桌球" },
        { "sr:sport:192", "航空竞速" },
        { "sr:sport:43", "登山" },
        { "sr:sport:74", "水上项目" },
        { "sr:sport:75", "射箭" },
        { "sr:sport:95", "花样游泳" },
        { "sr:sport:36", "田径" },
        { "sr:sport:13", "澳洲足球" },
        { "sr:sport:31", "羽毛球" },
        { "sr:sport:15", "曲棍球" },
        { "sr:sport:3", "棒球" },
        { "sr:sport:2", "篮球" },
        { "sr:sport:155", "3对3篮球" },
        { "sr:sport:130", "巴斯克回力球" },
        { "sr:sport:157", "沙滩手球" },
        { "sr:sport:60", "沙滩足球" },
        { "sr:sport:98", "沙滩网球" },
        { "sr:sport:34", "沙滩排球" },
        { "sr:sport:44", "滑雪和射击" },
        { "sr:sport:41", "自行车" },
        { "sr:sport:45", "雪橇" },
        { "sr:sport:32", "保龄球" },
        { "sr:sport:10", "拳击" },
        { "sr:sport:204", "Breaking" },
        { "sr:sport:92", "皮划艇激流回旋" },
        { "sr:sport:72", "独木舟" },
        { "sr:sport:33", "国际象棋" },
        { "sr:sport:205", "Cornhole" },
        { "sr:sport:21", "板球" },
        { "sr:sport:46", "越野赛" },
        { "sr:sport:28", "冰壶" },
        { "sr:sport:17", "自行车赛" },
        { "sr:sport:91", "自行车越野赛" },
        { "sr:sport:179", "BMX自行车Freesty" },
        { "sr:sport:180", "BMX自行车竞速" },
        { "sr:sport:141", "单车球" },
        { "sr:sport:88", "山地自行车赛" },
        { "sr:sport:97", "场地自行车赛" },
        { "sr:sport:207", "Cyclo-Cross" },
        { "sr:sport:22", "飞镖" },
        { "sr:sport:177", "星际争霸" },
        { "sr:sport:96", "跳水" },
        { "sr:sport:147", "直线加速赛" },
        { "sr:sport:42", "德国房车赛" },
        { "sr:sport:107", "电子竞技" },
        { "sr:sport:153", "电子篮球" },
        { "sr:sport:197", "电子板球" },
        { "sr:sport:195", "电子冰球" },
        { "sr:sport:193", "耐力竞速" },
        { "sr:sport:76", "马术" },
        { "sr:sport:137", "电子足球" },
        { "sr:sport:164", "电子竞技-Apex英雄" },
        { "sr:sport:158", "电竞传说对决" },
        { "sr:sport:162", "电竞神器" },
        { "sr:sport:175", "电子竞技 自走棋" },
        { "sr:sport:168", "电子竞技矿星之争" },
        { "sr:sport:118", "使命召唤" },
        { "sr:sport:171", "电子竞技万智牌" },
        { "sr:sport:133", "电竞皇室战争" },
        { "sr:sport:109", "反恐精英:全球攻势" },
        { "sr:sport:123", "穿越火线" },
        { "sr:sport:111", "刀塔2" },
        { "sr:sport:173", "电子竞技Dota霸业" },
        { "sr:sport:154", "电竞龙珠战士Z" },
        { "sr:sport:176", "电子竞技 格斗游戏" },
        { "sr:sport:170", "电子竞技 ，堡垒之夜" },
        { "sr:sport:132", "电竞战争机器" },
        { "sr:sport:124", "光环" },
        { "sr:sport:113", "炉石传说" },
        { "sr:sport:114", "风暴英雄" },
        { "sr:sport:134", "电竞王者荣耀" },
        { "sr:sport:110", "英雄联盟" },
        { "sr:sport:167", "电子竞技-麦登橄榄球" },
        { "sr:sport:178", "电子竞技 赛车运动" },
        { "sr:sport:121", "守望先锋" },
        { "sr:sport:161", "电竞枪火游侠" },
        { "sr:sport:140", "电竞绝地求生" },
        { "sr:sport:166", "电子竞技-实况足球" },
        { "sr:sport:139", "电竞雷神之锤" },
        { "sr:sport:125", "彩虹六号" },
        { "sr:sport:128", "火箭联盟" },
        { "sr:sport:119", "神之浩劫" },
        { "sr:sport:160", "电竞大格斗" },
        { "sr:sport:112", "星际争霸" },
        { "sr:sport:127", "街头霸王5" },
        { "sr:sport:174", "电子竞技 团队战略" },
        { "sr:sport:156", "电竞铁拳" },
        { "sr:sport:159", "电竞绝地要塞2" },
        { "sr:sport:120", "虚荣" },
        { "sr:sport:194", "电竞无畏契约" },
        { "sr:sport:122", "魔兽争霸3" },
        { "sr:sport:199", "电竞英雄联盟" },
        { "sr:sport:115", "坦克世界" },
        { "sr:sport:152", "电竞魔兽世界" },
        { "sr:sport:196", "电子网球" },
        { "sr:sport:198", "电子排球" },
        { "sr:sport:77", "击剑" },
        { "sr:sport:24", "草地曲棍球" },
        { "sr:sport:102", "花样滑冰" },
        { "sr:sport:53", "棒球" },
        { "sr:sport:172", "钓鱼" },
        { "sr:sport:7", "地板球" },
        { "sr:sport:16", "美式橄榄球" },
        { "sr:sport:40", "一级方程式" },
        { "sr:sport:189", "F2二级方程式" },
        { "sr:sport:142", "E级方程式" },
        { "sr:sport:103", "自由滑雪" },
        { "sr:sport:29", "五人制足球" },
        { "sr:sport:135", "盖尔式足球" },
        { "sr:sport:136", "盖尔式板棍球" },
        { "sr:sport:27", "爱尔兰式足球" },
        { "sr:sport:9", "高尔夫" },
        { "sr:sport:58", "赛狗" },
        { "sr:sport:78", "体操" },
        { "sr:sport:6", "手球" },
        { "sr:sport:55", "赛马" },
        { "sr:sport:73", "马篮球" },
        { "sr:sport:4", "冰上曲棍球" },
        { "sr:sport:163", "室内足球" },
        { "sr:sport:165", "印地赛车" },
        { "sr:sport:129", "印地赛车" },
        { "sr:sport:57", "曲棍球" },
        { "sr:sport:79", "柔道" },
        { "sr:sport:138", "卡巴迪" },
        { "sr:sport:181", "空手道" },
        { "sr:sport:39", "长曲棍球" },
        { "sr:sport:51", "平底雪橇" },
        { "sr:sport:182", "游泳马拉松" },
        { "sr:sport:117", "综合格斗" },
        { "sr:sport:80", "现代五项运动" },
        { "sr:sport:149", "改装赛车" },
        { "sr:sport:68", "世界摩托车锦标赛-Moto2组" },
        { "sr:sport:69", "世界摩托车锦标赛-Moto3组" },
        { "sr:sport:144", "摩托车越野赛" },
        { "sr:sport:67", "世界摩托车锦标赛" },
        { "sr:sport:11", "赛车" },
        { "sr:sport:190", "摩托车竞速" },
        { "sr:sport:202", "Muay Thai" },
        { "sr:sport:185", "Nascar房车世界卡车" },
        { "sr:sport:70", "Nasc杯系列赛" },
        { "sr:sport:186", "Nascar无限系列赛" },
        { "sr:sport:35", "无板篮球" },
        { "sr:sport:187", "直线加速赛" },
        { "sr:sport:47", "高山越野滑雪" },
        { "sr:sport:150", "越野车赛" },
        { "sr:sport:30", "奧运会" },
        { "sr:sport:87", "青年奥林匹克运动会" },
        { "sr:sport:71", "桨" },
        { "sr:sport:61", "芬兰棒球" },
        { "sr:sport:169", "法式滚球" },
        { "sr:sport:206", "Pickleball" },
        { "sr:sport:116", "马球" },
        { "sr:sport:25", "台球" },
        { "sr:sport:201", "Racquetball" },
        { "sr:sport:101", "汽车拉力赛" },
        { "sr:sport:93", "艺术体操" },
        { "sr:sport:89", "马术" },
        { "sr:sport:38", "冰上曲棍球" },
        { "sr:sport:64", "赛艇" },
        { "sr:sport:12", "英式橄榄球" },
        { "sr:sport:59", "联盟式橄榄球" },
        { "sr:sport:81", "帆船赛" },
        { "sr:sport:56", "瑞士摔跤" },
        { "sr:sport:126", "藤球" },
        { "sr:sport:82", "射击" },
        { "sr:sport:105", "短道速滑" },
        { "sr:sport:183", "滑板" },
        { "sr:sport:104", "俯式冰橇赛" },
        { "sr:sport:48", "高山滑雪" },
        { "sr:sport:19", "桌球" },
        { "sr:sport:49", "单板滑雪" },
        { "sr:sport:65", "自由式单板滑雪" },
        { "sr:sport:66", "单板滑雪平行" },
        { "sr:sport:1", "足球" },
        { "sr:sport:106", "足球神话" },
        { "sr:sport:203", "Soccer Specials" },
        { "sr:sport:54", "垒球" },
        { "sr:sport:18", "特技比赛" },
        { "sr:sport:146", "快艇比赛" },
        { "sr:sport:50", "速度滑雪" },
        { "sr:sport:131", "沙地摩托车" },
        { "sr:sport:184", "体育攀岩" },
        { "sr:sport:145", "短程泥路赛车" },
        { "sr:sport:37", "壁球" },
        { "sr:sport:191", "改装车比赛" },
        { "sr:sport:62", "街头曲棍球" },
        { "sr:sport:99", "相扑" },
        { "sr:sport:100", "超级摩托车" },
        { "sr:sport:90", "冲浪" },
        { "sr:sport:52", "游泳赛" },
        { "sr:sport:200", "T-Basket" },
        { "sr:sport:20", "乒乓球" },
        { "sr:sport:83", "跆拳道" },
        { "sr:sport:5", "网球" },
        { "sr:sport:188", "巡回赛车" },
        { "sr:sport:94", "蹦床体操" },
        { "sr:sport:84", "铁人三项" },
        { "sr:sport:8", "马车赛" },
        { "sr:sport:151", "卡车拖拉机牵引赛" },
        { "sr:sport:23", "排球" },
        { "sr:sport:26", "水球" },
        { "sr:sport:85", "举重" },
        { "sr:sport:14", "高山滑雪" },
        { "sr:sport:63", "世界锦标赛" },
        { WORLD_LOTTERY_SPORT_ID, "世界彩票协会" },
        { "sr:sport:86", "摔跤" },
    };

    public static SapiSport getSapiSport(Urn sportId, LanguageHolder language) {
        return allSports(language)
            .getSport()
            .stream()
            .filter(sport -> sport.getId().equals(sportId.toString()))
            .findFirst()
            .map(toFreshCopy())
            .orElseThrow(() ->
                new IllegalArgumentException("Sport not found [" + sportId + ", " + language + "]")
            );
    }

    private static Function<SapiSport, SapiSport> toFreshCopy() {
        return sport -> {
            SapiSport sapiSport = new SapiSport();
            sapiSport.setId(sport.getId());
            sapiSport.setName(sport.getName());
            return sapiSport;
        };
    }

    public static SapiSportsEndpoint allSports() {
        return allSports(ENGLISH);
    }

    public static SapiSportsEndpoint allSports(LanguageHolder languageHolder) {
        return allSports(languageHolder.get());
    }

    public static SapiSportsEndpoint allSports(Locale language) {
        val values = language.equals(Locale.CHINESE) ? SPORTS_CHINESE : SPORTS_ENGLISH;
        val allSports = Arrays
            .stream(values)
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
        return soccer(in(ENGLISH));
    }

    public static SapiSport soccer(LanguageHolder language) {
        return getSapiSport(Urn.parse("sr:sport:1"), language);
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

    public static SapiSport golf() {
        SapiSport soccer = new SapiSport();
        soccer.setId("sr:sport:9");
        soccer.setName("Golf");
        return soccer;
    }

    public static SapiSport tennis() {
        SapiSport formula1 = new SapiSport();
        formula1.setId("sr:sport:5");
        formula1.setName("Tennis");
        return formula1;
    }

    public static SapiSport baseball() {
        SapiSport formula1 = new SapiSport();
        formula1.setId("sr:sport:3");
        formula1.setName("Baseball");
        return formula1;
    }
}
