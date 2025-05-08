/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static com.sportradar.unifiedodds.sdk.conn.SapiSports.WORLD_LOTTERY_SPORT_ID;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static java.util.Locale.CHINESE;
import static java.util.Locale.ENGLISH;
import static java.util.stream.Collectors.toMap;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.sportradar.uf.sportsapi.datamodel.SapiCategory;
import com.sportradar.utils.Urn;
import com.sportradar.utils.domain.names.LanguageHolder;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("MultipleStringLiterals")
public class SapiCategories {

    public static final String WORLD_LOTTERY_USA_CATEGORY_ID = "sr:category:1039";
    private static final Map<String, Map<Locale, List<SapiCategory>>> CATEGORIES_FOR_SPORTS = ImmutableMap
        .<String, Map<Locale, List<SapiCategory>>>builder()
        .put(
            "sr:sport:48",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:140", "Ski Jumping"),
                    sapiCategory("sr:category:547", "Ski Jumping Women")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:140", "跳台滑雪"),
                    sapiCategory("sr:category:547", "女子跳台滑雪")
                )
            )
        )
        .put(
            "sr:sport:161",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:1768", "Other"),
                    sapiCategory("sr:category:2008", "Hi-Rez Paladins")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:1768", "其他"),
                    sapiCategory("sr:category:2008", "Hi-Rez 枪火游侠")
                )
            )
        )
        .put("sr:sport:66", ImmutableMap.of(ENGLISH, ImmutableList.of(), CHINESE, ImmutableList.of()))
        .put(
            "sr:sport:166",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:1825", "Other"),
                    sapiCategory("sr:category:1826", "WESG")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:1825", "其他"),
                    sapiCategory("sr:category:1826", "WESG")
                )
            )
        )
        .put(
            "sr:sport:176",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:1968", "Other"),
                    sapiCategory("sr:category:2172", "Mortal Kombat")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:1968", "其他"),
                    sapiCategory("sr:category:2172", "Mortal Kombat")
                )
            )
        )
        .put(
            "sr:sport:83",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:2037", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:2037", "国际"))
            )
        )
        .put("sr:sport:167", ImmutableMap.of(ENGLISH, ImmutableList.of(), CHINESE, ImmutableList.of()))
        .put(
            "sr:sport:2",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:15", "USA", "USA"),
                    sapiCategory("sr:category:103", "International")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:15", "美国", "USA"),
                    sapiCategory("sr:category:103", "国际")
                )
            )
        )
        .put(
            "sr:sport:44",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:141", "Biathlon"),
                    sapiCategory("sr:category:142", "Biathlon Women")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:141", "男子冬季两项"),
                    sapiCategory("sr:category:142", "女子冬季两项")
                )
            )
        )
        .put(
            "sr:sport:82",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:991", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:991", "国际"))
            )
        )
        .put("sr:sport:145", ImmutableMap.of(ENGLISH, ImmutableList.of(), CHINESE, ImmutableList.of()))
        .put(
            "sr:sport:95",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:2039", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:2039", "国际"))
            )
        )
        .put(
            "sr:sport:37",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:371", "International"),
                    sapiCategory("sr:category:494", "England")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:371", "国际"),
                    sapiCategory("sr:category:494", "英格兰")
                )
            )
        )
        .put(
            "sr:sport:143",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:2094", "Geek Events"),
                    sapiCategory("sr:category:2228", "Chinese Taipei")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:2094", "Geek Events"),
                    sapiCategory("sr:category:2228", "Chinese Taipei")
                )
            )
        )
        .put("sr:sport:63", ImmutableMap.of(ENGLISH, ImmutableList.of(), CHINESE, ImmutableList.of()))
        .put(
            "sr:sport:157",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:1731", "International"),
                    sapiCategory("sr:category:2251", "Russia")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:1731", "国际"),
                    sapiCategory("sr:category:2251", "Russia")
                )
            )
        )
        .put(
            "sr:sport:47",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:147", "Nordic Combined"),
                    sapiCategory("sr:category:1558", "Nordic Combined, Women")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:147", "北欧越野滑雪"),
                    sapiCategory("sr:category:1558", "女子全能")
                )
            )
        )
        .put(
            "sr:sport:96",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:987", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:987", "International"))
            )
        )
        .put(
            "sr:sport:200",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:2403", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:2403", "International"))
            )
        )
        .put(
            "sr:sport:67",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:2049", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:2049", "国际"))
            )
        )
        .put(
            "sr:sport:73",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:830", "France")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:830", "法国"))
            )
        )
        .put(
            "sr:sport:182",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:2032", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:2032", "国际"))
            )
        )
        .put(
            "sr:sport:164",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:2446", "Electronic Arts")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:2446", "Electronic Arts"))
            )
        )
        .put(
            "sr:sport:99",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:863", "Sumo.Japan")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:863", "Sumo.Japan"))
            )
        )
        .put(
            "sr:sport:135",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:1517", "Ireland")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:1517", "爱尔兰"))
            )
        )
        .put(
            "sr:sport:14",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:774", "Specials"),
                    sapiCategory("sr:category:2351", "Drivers")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:774", "特别项目"),
                    sapiCategory("sr:category:2351", "Drivers")
                )
            )
        )
        .put(
            "sr:sport:112",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:1052", "Other"),
                    sapiCategory("sr:category:1356", "KeSPA SC2")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:1052", "其他"),
                    sapiCategory("sr:category:1356", "韩国电子竞技协会 星际争霸2")
                )
            )
        )
        .put(
            "sr:sport:56",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:566", "Switzerland")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:566", "瑞士"))
            )
        )
        .put(
            "sr:sport:32",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:266", "International"),
                    sapiCategory("sr:category:945", "Australia", "AUS")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:266", "国际"),
                    sapiCategory("sr:category:945", "澳大利亚", "AUS")
                )
            )
        )
        .put(
            "sr:sport:136",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:1518", "Ireland")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:1518", "爱尔兰"))
            )
        )
        .put(
            "sr:sport:80",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:2033", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:2033", "国际"))
            )
        )
        .put(
            "sr:sport:15",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:75", "Sweden", "SWE"),
                    sapiCategory("sr:category:120", "International")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:75", "瑞典", "SWE"),
                    sapiCategory("sr:category:120", "国际")
                )
            )
        )
        .put(
            "sr:sport:189",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:464", "GP2 Series"),
                    sapiCategory("sr:category:2111", "International")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:464", "GP2锦标赛"),
                    sapiCategory("sr:category:2111", "International")
                )
            )
        )
        .put(
            "sr:sport:3",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:16", "USA", "USA"),
                    sapiCategory("sr:category:210", "Germany", "DEU")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:16", "美国", "USA"),
                    sapiCategory("sr:category:210", "德国", "DEU")
                )
            )
        )
        .put(
            "sr:sport:98",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:854", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:854", "International"))
            )
        )
        .put(
            WORLD_LOTTERY_SPORT_ID,
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory(WORLD_LOTTERY_USA_CATEGORY_ID, "USA"),
                    sapiCategory("sr:category:1040", "UK")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory(WORLD_LOTTERY_USA_CATEGORY_ID, "美国"),
                    sapiCategory("sr:category:1040", "英国")
                )
            )
        )
        .put(
            "sr:sport:104",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:358", "Skeleton Men"),
                    sapiCategory("sr:category:359", "Skeleton Women")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:358", "男子俯式雪橇"),
                    sapiCategory("sr:category:359", "女子俯式雪橇")
                )
            )
        )
        .put(
            "sr:sport:115",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:1055", "Other")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:1055", "Other"))
            )
        )
        .put(
            "sr:sport:70",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:2051", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:2051", "国际"))
            )
        )
        .put(
            "sr:sport:190",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:2079", "Moto2"),
                    sapiCategory("sr:category:498", "MotoGP")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:2079", "Moto2"),
                    sapiCategory("sr:category:498", "世界摩托车大奖赛")
                )
            )
        )
        .put(
            "sr:sport:10",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:27", "International"),
                    sapiCategory("sr:category:1028", "WBO")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:27", "国际"),
                    sapiCategory("sr:category:1028", "世界拳击组织")
                )
            )
        )
        .put(
            "sr:sport:171",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:2058", "MTG")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:2058", "MTG"))
            )
        )
        .put(
            "sr:sport:33",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:268", "International"),
                    sapiCategory("sr:category:330", "Germany", "DEU")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:268", "International"),
                    sapiCategory("sr:category:330", "德国", "DEU")
                )
            )
        )
        .put(
            "sr:sport:69",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:2048", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:2048", "国际"))
            )
        )
        .put(
            "sr:sport:195",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:2261", "Cyber Live Arena"),
                    sapiCategory("sr:category:2270", "Other")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:2261", "Cyber Live Arena"),
                    sapiCategory("sr:category:2270", "其他")
                )
            )
        )
        .put(
            "sr:sport:118",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:1466", "MLG CoD"),
                    sapiCategory("sr:category:1112", "Activision BO3")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:1466", "北美电竞联盟 使命召唤"),
                    sapiCategory("sr:category:1112", "Activision 三局两胜")
                )
            )
        )
        .put("sr:sport:196", ImmutableMap.of(ENGLISH, ImmutableList.of(), CHINESE, ImmutableList.of()))
        .put(
            "sr:sport:120",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:1363", "Vainglory"),
                    sapiCategory("sr:category:1824", "WESG")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:1363", "虚荣"),
                    sapiCategory("sr:category:1824", "WESG")
                )
            )
        )
        .put(
            "sr:sport:169",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:1886", "France")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:1886", "法国"))
            )
        )
        .put(
            "sr:sport:36",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:295", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:295", "国际"))
            )
        )
        .put(
            "sr:sport:43",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:118", "Alpine"),
                    sapiCategory("sr:category:119", "Alpine Women")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:118", "男子高山滑雪"),
                    sapiCategory("sr:category:119", "女子高山滑雪")
                )
            )
        )
        .put(
            "sr:sport:129",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:453", "Indycar"),
                    sapiCategory("sr:category:1812", "Indy Lights")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:453", "印地赛车"),
                    sapiCategory("sr:category:1812", "印地赛车")
                )
            )
        )
        .put(
            "sr:sport:181",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:2031", "International"),
                    sapiCategory("sr:category:2097", "Kata")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:2031", "国际"),
                    sapiCategory("sr:category:2097", "Kata")
                )
            )
        )
        .put(
            "sr:sport:198",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:2292", "Liga Pro")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:2292", "Liga Pro"))
            )
        )
        .put("sr:sport:149", ImmutableMap.of(ENGLISH, ImmutableList.of(), CHINESE, ImmutableList.of()))
        .put(
            "sr:sport:127",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:1456", "Other"),
                    sapiCategory("sr:category:1540", "ESL SFV")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:1456", "其他"),
                    sapiCategory("sr:category:1540", "ESL SFV")
                )
            )
        )
        .put(
            "sr:sport:187",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:2053", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:2053", "国际"))
            )
        )
        .put(
            "sr:sport:81",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:1495", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:1495", "国际"))
            )
        )
        .put(
            "sr:sport:35",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:294", "Australia"),
                    sapiCategory("sr:category:326", "International")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:294", "Australia"),
                    sapiCategory("sr:category:326", "International")
                )
            )
        )
        .put(
            "sr:sport:49",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:314", "Snowboard"),
                    sapiCategory("sr:category:315", "Snowboard Women")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:314", "男子单板滑雪"),
                    sapiCategory("sr:category:315", "女子单板滑雪")
                )
            )
        )
        .put(
            "sr:sport:31",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:259", "International"),
                    sapiCategory("sr:category:427", "Denmark", "DNK")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:259", "国际"),
                    sapiCategory("sr:category:427", "丹麦", "DNK")
                )
            )
        )
        .put(
            "sr:sport:111",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:1126", "Dreamhack Dota2"),
                    sapiCategory("sr:category:2409", "EPL")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:1126", "Dreamhack 刀塔2"),
                    sapiCategory("sr:category:2409", "EPL")
                )
            )
        )
        .put(
            "sr:sport:186",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:2052", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:2052", "国际"))
            )
        )
        .put(
            "sr:sport:142",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:1008", "Formula E"),
                    sapiCategory("sr:category:1697", "Championship")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:1008", "方程式E"),
                    sapiCategory("sr:category:1697", "冠军")
                )
            )
        )
        .put(
            "sr:sport:9",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:28", "Men"),
                    sapiCategory("sr:category:29", "Women")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:28", "男子"),
                    sapiCategory("sr:category:29", "女子")
                )
            )
        )
        .put(
            "sr:sport:121",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:1440", "Blizzard OW"),
                    sapiCategory("sr:category:1364", "Other")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:1440", "暴雪娱乐 守望先锋"),
                    sapiCategory("sr:category:1364", "其他")
                )
            )
        )
        .put(
            "sr:sport:134",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:1946", "Tencent"),
                    sapiCategory("sr:category:1515", "Other")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:1946", "腾讯 王者荣耀"),
                    sapiCategory("sr:category:1515", "其他")
                )
            )
        )
        .put(
            "sr:sport:114",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:1054", "Other"),
                    sapiCategory("sr:category:1369", "Dreamhack")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:1054", "Other"),
                    sapiCategory("sr:category:1369", "Dreamhack")
                )
            )
        )
        .put(
            "sr:sport:162",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:1770", "Other")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:1770", "其他"))
            )
        )
        .put(
            "sr:sport:12",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:83", "Rugby League"),
                    sapiCategory("sr:category:82", "Rugby Union")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:83", "联盟式橄榄"),
                    sapiCategory("sr:category:82", "联合式橄榄球")
                )
            )
        )
        .put(
            "sr:sport:75",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:1038", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:1038", "International"))
            )
        )
        .put(
            "sr:sport:203",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:2443", "7vs7")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:2443", "7vs7"))
            )
        )
        .put(
            "sr:sport:72",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:929", "Canoe Slalom"),
                    sapiCategory("sr:category:930", "Kayak Slalom")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:929", "独木舟激流回旋"),
                    sapiCategory("sr:category:930", "皮划艇激流回旋")
                )
            )
        )
        .put("sr:sport:65", ImmutableMap.of(ENGLISH, ImmutableList.of(), CHINESE, ImmutableList.of()))
        .put(
            "sr:sport:106",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:1002", "Weekly matches"),
                    sapiCategory("sr:category:1688", "World Cup")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:1002", "周赛"),
                    sapiCategory("sr:category:1688", "世界杯")
                )
            )
        )
        .put(
            "sr:sport:18",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:203", "Music"),
                    sapiCategory("sr:category:204", "Politics")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:203", "音乐"),
                    sapiCategory("sr:category:204", "政治")
                )
            )
        )
        .put(
            "sr:sport:8",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:1018", "Nordic")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:1018", "北欧"))
            )
        )
        .put(
            "sr:sport:60",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:577", "International"),
                    sapiCategory("sr:category:578", "Russia", "RUS")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:577", "国际"),
                    sapiCategory("sr:category:578", "俄罗斯", "RUS")
                )
            )
        )
        .put(
            "sr:sport:140",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:1531", "Other"),
                    sapiCategory("sr:category:1649", "ESL PU Battlegrounds")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:1531", "其他"),
                    sapiCategory("sr:category:1649", "ESL 绝地求生")
                )
            )
        )
        .put(
            "sr:sport:11",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:308", "A1"),
                    sapiCategory("sr:category:338", "Races")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:308", "A1联赛"),
                    sapiCategory("sr:category:338", "车赛")
                )
            )
        )
        .put(
            "sr:sport:194",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:2160", "ESPN"),
                    sapiCategory("sr:category:2190", "Solari")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:2160", "ESPN"),
                    sapiCategory("sr:category:2190", "Solari")
                )
            )
        )
        .put(
            "sr:sport:202",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:2434", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:2434", "International"))
            )
        )
        .put(
            "sr:sport:30",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:215", "Olympics"),
                    sapiCategory("sr:category:216", "Track")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:215", "奥运"),
                    sapiCategory("sr:category:216", "径赛")
                )
            )
        )
        .put(
            "sr:sport:201",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:2429", "Canada")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:2429", "Canada"))
            )
        )
        .put(
            "sr:sport:58",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:569", "Greyhound"),
                    sapiCategory("sr:category:926", "Virtual Dog Racing")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:569", "赛狗"),
                    sapiCategory("sr:category:926", "虚拟赛狗")
                )
            )
        )
        .put(
            "sr:sport:79",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:990", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:990", "International"))
            )
        )
        .put(
            "sr:sport:90",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:841", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:841", "International"))
            )
        )
        .put(
            "sr:sport:180",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:2027", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:2027", "国际"))
            )
        )
        .put(
            "sr:sport:193",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:979", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:979", "世界电子竞技锦标赛"))
            )
        )
        .put(
            "sr:sport:25",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:161", "International"),
                    sapiCategory("sr:category:501", "Canada")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:161", "国际"),
                    sapiCategory("sr:category:501", "加拿大")
                )
            )
        )
        .put(
            "sr:sport:116",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:1068", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:1068", "国际"))
            )
        )
        .put(
            "sr:sport:158",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:1854", "Garena Vietnam"),
                    sapiCategory("sr:category:2250", "Garena Thailand")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:1854", "Garena 越南"),
                    sapiCategory("sr:category:2250", "Garena Thailand")
                )
            )
        )
        .put(
            "sr:sport:199",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:2348", "Riot")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:2348", "Riot"))
            )
        )
        .put("sr:sport:53", ImmutableMap.of(ENGLISH, ImmutableList.of(), CHINESE, ImmutableList.of()))
        .put(
            "sr:sport:174",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:1966", "Other"),
                    sapiCategory("sr:category:2593", "Riot")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:1966", "其他"),
                    sapiCategory("sr:category:2593", "Riot")
                )
            )
        )
        .put(
            "sr:sport:13",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:87", "Australia", "AUS"),
                    sapiCategory("sr:category:243", "International")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:87", "澳大利亚", "AUS"),
                    sapiCategory("sr:category:243", "国际")
                )
            )
        )
        .put(
            "sr:sport:107",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:995", "League of Legends"),
                    sapiCategory("sr:category:996", "Starcraft2")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:995", "League of Legends"),
                    sapiCategory("sr:category:996", "Starcraft2")
                )
            )
        )
        .put("sr:sport:150", ImmutableMap.of(ENGLISH, ImmutableList.of(), CHINESE, ImmutableList.of()))
        .put(
            "sr:sport:128",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:1457", "Other"),
                    sapiCategory("sr:category:1755", "Psyonix")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:1457", "其他"),
                    sapiCategory("sr:category:1755", "Psyonix")
                )
            )
        )
        .put(
            "sr:sport:57",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:568", "International"),
                    sapiCategory("sr:category:713", "Czech")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:568", "International"),
                    sapiCategory("sr:category:713", "Czech")
                )
            )
        )
        .put(
            "sr:sport:34",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:290", "International"),
                    sapiCategory("sr:category:757", "Turkiye", "TUR")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:290", "国际"),
                    sapiCategory("sr:category:757", "土耳其", "TUR")
                )
            )
        )
        .put(
            "sr:sport:155",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:1713", "USA", "USA"),
                    sapiCategory("sr:category:1715", "International")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:1713", "美国", "USA"),
                    sapiCategory("sr:category:1715", "国际")
                )
            )
        )
        .put(
            "sr:sport:168",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:1884", "Other")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:1884", "其他"))
            )
        )
        .put(
            "sr:sport:27",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:175", "Matches"),
                    sapiCategory("sr:category:419", "Football")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:175", "Matches"),
                    sapiCategory("sr:category:419", "Football")
                )
            )
        )
        .put(
            "sr:sport:52",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:504", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:504", "国际"))
            )
        )
        .put(
            "sr:sport:97",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:2029", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:2029", "国际"))
            )
        )
        .put(
            "sr:sport:125",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:1414", "Other"),
                    sapiCategory("sr:category:1763", "ESL")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:1414", "其他"),
                    sapiCategory("sr:category:1763", "ESL")
                )
            )
        )
        .put("sr:sport:91", ImmutableMap.of(ENGLISH, ImmutableList.of(), CHINESE, ImmutableList.of()))
        .put(
            "sr:sport:22",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:104", "International"),
                    sapiCategory("sr:category:261", "England", "ENG")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:104", "国际"),
                    sapiCategory("sr:category:261", "英格兰", "ENG")
                )
            )
        )
        .put("sr:sport:74", ImmutableMap.of(ENGLISH, ImmutableList.of(), CHINESE, ImmutableList.of()))
        .put(
            "sr:sport:152",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:1700", "Other"),
                    sapiCategory("sr:category:1850", "Blizzard")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:1700", "其他"),
                    sapiCategory("sr:category:1850", "暴雪")
                )
            )
        )
        .put(
            "sr:sport:133",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:1738", "Other")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:1738", "其他"))
            )
        )
        .put(
            "sr:sport:117",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:1089", "UFC"),
                    sapiCategory("sr:category:1090", "Bellator")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:1089", "终极格斗"),
                    sapiCategory("sr:category:1090", "Bellator MMA")
                )
            )
        )
        .put("sr:sport:59", ImmutableMap.of(ENGLISH, ImmutableList.of(), CHINESE, ImmutableList.of()))
        .put(
            "sr:sport:100",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:2057", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:2057", "国际"))
            )
        )
        .put(
            "sr:sport:156",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:1729", "Other")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:1729", "其他"))
            )
        )
        .put(
            "sr:sport:26",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:172", "Italy", "ITA"),
                    sapiCategory("sr:category:164", "International")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:172", "意大利", "ITA"),
                    sapiCategory("sr:category:164", "国际")
                )
            )
        )
        .put(
            "sr:sport:7",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:63", "Sweden", "SWE"),
                    sapiCategory("sr:category:70", "Finland", "FIN")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:63", "瑞典", "SWE"),
                    sapiCategory("sr:category:70", "芬兰", "FIN")
                )
            )
        )
        .put(
            "sr:sport:6",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:73", "International"),
                    sapiCategory("sr:category:53", "Germany", "DEU")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:73", "国际"),
                    sapiCategory("sr:category:53", "德国", "DEU")
                )
            )
        )
        .put("sr:sport:151", ImmutableMap.of(ENGLISH, ImmutableList.of(), CHINESE, ImmutableList.of()))
        .put(
            "sr:sport:62",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:708", "Czech Republic"),
                    sapiCategory("sr:category:709", "Slovakia")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:708", "Czech Republic"),
                    sapiCategory("sr:category:709", "Slovakia")
                )
            )
        )
        .put(
            "sr:sport:101",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:84", "Rally"),
                    sapiCategory("sr:category:982", "Rallycross")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:84", "拉力赛"),
                    sapiCategory("sr:category:982", "汽车拉力赛")
                )
            )
        )
        .put(
            "sr:sport:21",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:497", "India", "IND"),
                    sapiCategory("sr:category:848", "Pakistan", "PAK")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:497", "印度", "IND"),
                    sapiCategory("sr:category:848", "巴基斯坦", "PAK")
                )
            )
        )
        .put(
            "sr:sport:1",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:4", "International"),
                    sapiCategory("sr:category:393", "International Clubs"),
                    sapiCategory("sr:category:32", "Spain", "ESP")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:4", "International"),
                    sapiCategory("sr:category:393", "国际俱乐部"),
                    sapiCategory("sr:category:32", "西班牙", "ESP")
                )
            )
        )
        .put(
            "sr:sport:113",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:1053", "Other"),
                    sapiCategory("sr:category:1498", "StarLadder")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:1053", "其他"),
                    sapiCategory("sr:category:1498", "StarLadder")
                )
            )
        )
        .put(
            "sr:sport:88",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:2028", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:2028", "国际"))
            )
        )
        .put(
            "sr:sport:175",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:1967", "Other")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:1967", "其他"))
            )
        )
        .put(
            "sr:sport:204",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:2447", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:2447", "International"))
            )
        )
        .put(
            "sr:sport:39",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:450", "USA", "USA"),
                    sapiCategory("sr:category:448", "International")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:450", "美国", "USA"),
                    sapiCategory("sr:category:448", "国际长曲棍球")
                )
            )
        )
        .put(
            "sr:sport:159",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:1767", "Other")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:1767", "其他"))
            )
        )
        .put(
            "sr:sport:207",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:2571", "International"),
                    sapiCategory("sr:category:2573", "Belgium", "BEL")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:2571", "International"),
                    sapiCategory("sr:category:2573", "Belgium", "BEL")
                )
            )
        )
        .put(
            "sr:sport:160",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:1766", "Other"),
                    sapiCategory("sr:category:1930", "CEO Gaming")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:1766", "其他"),
                    sapiCategory("sr:category:1930", "CEO游戏 任天堂明星大乱斗")
                )
            )
        )
        .put(
            "sr:sport:139",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:1532", "Other"),
                    sapiCategory("sr:category:2110", "Bethesda")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:1532", "其他"),
                    sapiCategory("sr:category:2110", "Bethesda")
                )
            )
        )
        .put(
            "sr:sport:178",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:1986", "Formula 1"),
                    sapiCategory("sr:category:2010", "MotoGP")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:1986", "F1一级方程式"),
                    sapiCategory("sr:category:2010", "MotoGP")
                )
            )
        )
        .put(
            "sr:sport:126",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:1418", "Malaysia", "MYS"),
                    sapiCategory("sr:category:1432", "Thailand", "THA")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:1418", "马来西亚", "MYS"),
                    sapiCategory("sr:category:1432", "泰国", "THA")
                )
            )
        )
        .put(
            "sr:sport:172",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:1932", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:1932", "国际"))
            )
        )
        .put(
            "sr:sport:77",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:2030", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:2030", "国际"))
            )
        )
        .put(
            "sr:sport:55",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:559", "Gallop"),
                    sapiCategory("sr:category:560", "Trotting")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:559", "加鲁普"),
                    sapiCategory("sr:category:560", "轻快步")
                )
            )
        )
        .put("sr:sport:130", ImmutableMap.of(ENGLISH, ImmutableList.of(), CHINESE, ImmutableList.of()))
        .put(
            "sr:sport:184",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:2036", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:2036", "国际"))
            )
        )
        .put(
            "sr:sport:54",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:528", "International"),
                    sapiCategory("sr:category:562", "USA", "USA")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:528", "国际"),
                    sapiCategory("sr:category:562", "美国", "USA")
                )
            )
        )
        .put(
            "sr:sport:165",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:2045", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:2045", "国际"))
            )
        )
        .put(
            "sr:sport:78",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:992", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:992", "International"))
            )
        )
        .put("sr:sport:177", ImmutableMap.of(ENGLISH, ImmutableList.of(), CHINESE, ImmutableList.of()))
        .put(
            "sr:sport:40",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:36", "Formula 1"),
                    sapiCategory("sr:category:1026", "GP Austria")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:36", "世界一级方程式锦标赛"),
                    sapiCategory("sr:category:1026", "奥地利大奖赛")
                )
            )
        )
        .put(
            "sr:sport:87",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:833", "Ice Hockey")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:833", "Ice Hockey"))
            )
        )
        .put(
            "sr:sport:124",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:1411", "Other")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:1411", "其它"))
            )
        )
        .put(
            "sr:sport:28",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:178", "International"),
                    sapiCategory("sr:category:431", "Curling Men")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:178", "国际"),
                    sapiCategory("sr:category:431", "男子冰壶")
                )
            )
        )
        .put(
            "sr:sport:109",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:2185", "Playzone"),
                    sapiCategory("sr:category:2460", "YaLLa Esports")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:2185", "Playzone"),
                    sapiCategory("sr:category:2460", "YaLLa Esports")
                )
            )
        )
        .put("sr:sport:197", ImmutableMap.of(ENGLISH, ImmutableList.of(), CHINESE, ImmutableList.of()))
        .put(
            "sr:sport:46",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:143", "Cross-country"),
                    sapiCategory("sr:category:144", "Cross-country Women")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:143", "男子越野滑雪"),
                    sapiCategory("sr:category:144", "女子越野滑雪")
                )
            )
        )
        .put(
            "sr:sport:137",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:2265", "GT Sports League"),
                    sapiCategory("sr:category:2440", "eAdriatic League")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:2265", "GT Sports League"),
                    sapiCategory("sr:category:2440", "eAdriatic League")
                )
            )
        )
        .put(
            "sr:sport:85",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:993", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:993", "International"))
            )
        )
        .put(
            "sr:sport:102",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:355", "Figure Skating Men"),
                    sapiCategory("sr:category:356", "Figure Skating Mixed")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:355", "男子花样滑冰"),
                    sapiCategory("sr:category:356", "双人花样滑冰")
                )
            )
        )
        .put(
            "sr:sport:71",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:789", "International"),
                    sapiCategory("sr:category:2396", "Brazil")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:789", "国际"),
                    sapiCategory("sr:category:2396", "Brazil")
                )
            )
        )
        .put(
            "sr:sport:185",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:2050", "Not in use")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:2050", "国际"))
            )
        )
        .put(
            "sr:sport:86",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:850", "International"),
                    sapiCategory("sr:category:2062", "Freestyle")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:850", "International"),
                    sapiCategory("sr:category:2062", "Freestyle")
                )
            )
        )
        .put(
            "sr:sport:173",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:1965", "Other")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:1965", "其他"))
            )
        )
        .put(
            "sr:sport:179",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:2026", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:2026", "国际"))
            )
        )
        .put("sr:sport:41", ImmutableMap.of(ENGLISH, ImmutableList.of(), CHINESE, ImmutableList.of()))
        .put(
            "sr:sport:19",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:55", "International"),
                    sapiCategory("sr:category:193", "England", "ENG")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:55", "国际"),
                    sapiCategory("sr:category:193", "英格兰", "ENG")
                )
            )
        )
        .put(
            "sr:sport:183",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:2035", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:2035", "国际"))
            )
        )
        .put(
            "sr:sport:188",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:198", "DTM"),
                    sapiCategory("sr:category:422", "Supercars")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:198", "德国房车大师赛"),
                    sapiCategory("sr:category:422", "超级跑车赛")
                )
            )
        )
        .put(
            "sr:sport:147",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:1718", "NHRA")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:1718", "国际"))
            )
        )
        .put(
            "sr:sport:170",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:1905", "Other"),
                    sapiCategory("sr:category:1933", "Epic Games")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:1905", "其他"),
                    sapiCategory("sr:category:1933", "Epic游戏 堡垒之夜")
                )
            )
        )
        .put(
            "sr:sport:16",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:93", "Canada", "CAN"),
                    sapiCategory("sr:category:43", "USA", "USA")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:93", "加拿大", "CAN"),
                    sapiCategory("sr:category:43", "美国", "USA")
                )
            )
        )
        .put(
            "sr:sport:76",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:1693", "Jumping"),
                    sapiCategory("sr:category:1694", "Dressage")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:1693", "场地障碍赛"),
                    sapiCategory("sr:category:1694", "盛装舞步")
                )
            )
        )
        .put(
            "sr:sport:205",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:2455", "International"),
                    sapiCategory("sr:category:2603", "USA", "USA")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:2455", "International"),
                    sapiCategory("sr:category:2603", "USA", "USA")
                )
            )
        )
        .put(
            "sr:sport:105",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:362", "Short Track Speed Skating"),
                    sapiCategory("sr:category:363", "Short Track Speed Skating Women")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:362", "男子短道速滑"),
                    sapiCategory("sr:category:363", "女子短道速滑")
                )
            )
        )
        .put(
            "sr:sport:103",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:360", "Freestyle"),
                    sapiCategory("sr:category:361", "Freestyle Women")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:360", "男子自由滑雪"),
                    sapiCategory("sr:category:361", "女子自由滑雪")
                )
            )
        )
        .put(
            "sr:sport:191",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:150", "Nascar"),
                    sapiCategory("sr:category:2485", "Turismo Carretera")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:150", "纳斯卡赛车锦标赛"),
                    sapiCategory("sr:category:2485", "Turismo Carretera")
                )
            )
        )
        .put(
            "sr:sport:94",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:2040", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:2040", "国际"))
            )
        )
        .put("sr:sport:206", ImmutableMap.of(ENGLISH, ImmutableList.of(), CHINESE, ImmutableList.of()))
        .put(
            "sr:sport:153",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:1703", "Other"),
                    sapiCategory("sr:category:1827", "NBA")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:1703", "其他"),
                    sapiCategory("sr:category:1827", "美职篮")
                )
            )
        )
        .put(
            "sr:sport:45",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:316", "Bobsleigh"),
                    sapiCategory("sr:category:319", "Bobsleigh Women")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:316", "男子有舵雪橇"),
                    sapiCategory("sr:category:319", "女子有舵雪橇")
                )
            )
        )
        .put("sr:sport:146", ImmutableMap.of(ENGLISH, ImmutableList.of(), CHINESE, ImmutableList.of()))
        .put(
            "sr:sport:154",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:1712", "Other")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:1712", "其他"))
            )
        )
        .put(
            "sr:sport:192",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:418", "Red Bull Air race")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:418", "红牛特技飞行赛"))
            )
        )
        .put(
            "sr:sport:123",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:1410", "Other")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:1410", "其它"))
            )
        )
        .put(
            "sr:sport:17",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:96", "International"),
                    sapiCategory("sr:category:470", "Germany", "DEU")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:96", "国际"),
                    sapiCategory("sr:category:470", "德国", "DEU")
                )
            )
        )
        .put(
            "sr:sport:119",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:1359", "Other"),
                    sapiCategory("sr:category:1960", "Hi-Rez")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:1359", "其他"),
                    sapiCategory("sr:category:1960", "Hi-Rez 神之浩劫")
                )
            )
        )
        .put(
            "sr:sport:64",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:756", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:756", "国际"))
            )
        )
        .put("sr:sport:92", ImmutableMap.of(ENGLISH, ImmutableList.of(), CHINESE, ImmutableList.of()))
        .put("sr:sport:89", ImmutableMap.of(ENGLISH, ImmutableList.of(), CHINESE, ImmutableList.of()))
        .put(
            "sr:sport:4",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:56", "International"),
                    sapiCategory("sr:category:54", "Switzerland", "CHE")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:56", "国际"),
                    sapiCategory("sr:category:54", "瑞士", "CHE")
                )
            )
        )
        .put(
            "sr:sport:42",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:2042", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:2042", "国际"))
            )
        )
        .put(
            "sr:sport:141",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:1654", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:1654", "国际"))
            )
        )
        .put(
            "sr:sport:24",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:166", "International"),
                    sapiCategory("sr:category:271", "Germany", "DEU")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:166", "国际"),
                    sapiCategory("sr:category:271", "德国", "DEU")
                )
            )
        )
        .put(
            "sr:sport:144",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:459", "Motocross"),
                    sapiCategory("sr:category:2112", "International")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:459", "摩托越野赛"),
                    sapiCategory("sr:category:2112", "International")
                )
            )
        )
        .put(
            "sr:sport:23",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:136", "International"),
                    sapiCategory("sr:category:128", "Austria", "AUT")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:136", "国际"),
                    sapiCategory("sr:category:128", "奥地利", "AUT")
                )
            )
        )
        .put(
            "sr:sport:84",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:2038", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:2038", "国际"))
            )
        )
        .put(
            "sr:sport:93",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:2034", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:2034", "国际"))
            )
        )
        .put(
            "sr:sport:68",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:2047", "International")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:2047", "国际"))
            )
        )
        .put(
            "sr:sport:163",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:1780", "International"),
                    sapiCategory("sr:category:2493", "NIU")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:1780", "国际"),
                    sapiCategory("sr:category:2493", "NIU")
                )
            )
        )
        .put(
            "sr:sport:122",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:1377", "Other"),
                    sapiCategory("sr:category:1862", "Warcraft3Korea")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:1377", "其它"),
                    sapiCategory("sr:category:1862", "Warcraft3Korea")
                )
            )
        )
        .put(
            "sr:sport:20",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:88", "International"),
                    sapiCategory("sr:category:1017", "Czech Republic", "CZE")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:88", "国际"),
                    sapiCategory("sr:category:1017", "捷克", "CZE")
                )
            )
        )
        .put(
            "sr:sport:61",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:597", "Finland", "FIN")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:597", "芬兰", "FIN"))
            )
        )
        .put(
            "sr:sport:138",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:1530", "India", "IND"),
                    sapiCategory("sr:category:1719", "International")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:1530", "印度", "IND"),
                    sapiCategory("sr:category:1719", "国际")
                )
            )
        )
        .put(
            "sr:sport:131",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:2056", "Sweden", "SWE"),
                    sapiCategory("sr:category:2055", "Poland", "POL")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:2056", "瑞典", "SWE"),
                    sapiCategory("sr:category:2055", "波兰", "POL")
                )
            )
        )
        .put(
            "sr:sport:5",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:74", "Billie Jean King Cup"),
                    sapiCategory("sr:category:6", "WTA"),
                    sapiCategory("sr:category:3", "ATP")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:74", "Billie Jean King Cup"),
                    sapiCategory("sr:category:6", "WTA"),
                    sapiCategory("sr:category:3", "ATP")
                )
            )
        )
        .put(
            "sr:sport:51",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:317", "Luge"),
                    sapiCategory("sr:category:321", "Luge Women")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:317", "男子无舵雪橇"),
                    sapiCategory("sr:category:321", "女子无舵雪橇")
                )
            )
        )
        .put(
            "sr:sport:132",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(sapiCategory("sr:category:1658", "Other")),
                CHINESE,
                ImmutableList.of(sapiCategory("sr:category:1658", "其他"))
            )
        )
        .put(
            "sr:sport:29",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:183", "Spain", "ESP"),
                    sapiCategory("sr:category:185", "Italy", "ITA")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:183", "西班牙", "ESP"),
                    sapiCategory("sr:category:185", "意大利", "ITA")
                )
            )
        )
        .put(
            "sr:sport:50",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:318", "Speed Skating"),
                    sapiCategory("sr:category:320", "Speed Skating Women")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:318", "男子速度滑冰"),
                    sapiCategory("sr:category:320", "女子速度滑冰")
                )
            )
        )
        .put(
            "sr:sport:38",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:445", "Portugal"),
                    sapiCategory("sr:category:444", "Spain")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:445", "葡萄牙"),
                    sapiCategory("sr:category:444", "西班牙")
                )
            )
        )
        .put(
            "sr:sport:110",
            ImmutableMap.of(
                ENGLISH,
                ImmutableList.of(
                    sapiCategory("sr:category:1046", "Riot LoL"),
                    sapiCategory("sr:category:1644", "Fortuna LoL")
                ),
                CHINESE,
                ImmutableList.of(
                    sapiCategory("sr:category:1046", "拳头 英雄联盟"),
                    sapiCategory("sr:category:1644", "Fortuna 英雄联盟")
                )
            )
        )
        .build();

    public static Map<Urn, List<SapiCategory>> allCategories(LanguageHolder language) {
        return CATEGORIES_FOR_SPORTS
            .entrySet()
            .stream()
            .collect(toMap(e -> Urn.parse(e.getKey()), e -> e.getValue().get(language.get())));
    }

    public static SportAwareSapiCategory getSapiCategory(Urn categoryId, LanguageHolder language) {
        return allCategories(language)
            .entrySet()
            .stream()
            .flatMap(sportIdAndCategories ->
                sportIdAndCategories
                    .getValue()
                    .stream()
                    .map(category -> new SportAwareSapiCategory(category, sportIdAndCategories.getKey()))
            )
            .filter(category -> category.getCategory().getId().equals(categoryId.toString()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Category not found " + categoryId));
    }

    public static SapiCategory international() {
        return sapiCategory("sr:category:4", "International");
    }

    public static SapiCategory nascar() {
        return sapiCategory("sr:category:6", "NASCAR");
    }

    public static SapiCategory usa() {
        SapiCategory category = new SapiCategory();
        category.setId("sr:category:37");
        category.setName("USA");
        category.setCountryCode("USA");
        return category;
    }

    public static SapiCategory norway() {
        return sapiCategory("sr:category:4", "International");
    }

    public static SapiCategory england() {
        SapiCategory category = new SapiCategory();
        category.setId("sr:category:1");
        category.setName("England");
        category.setCountryCode("ENG");
        return category;
    }

    public static SapiCategory formula1() {
        return sapiCategory("sr:category:36", "Formula 1");
    }

    public static SapiCategory men() {
        return sapiCategory("sr:category:28", "Men");
    }

    public static SapiCategory atp() {
        return getSapiCategory(Urn.parse("sr:category:3"), in(ENGLISH)).getCategory();
    }

    public static SapiCategory atp(LanguageHolder language) {
        return getSapiCategory(Urn.parse("sr:category:3"), language).getCategory();
    }

    private static SapiCategory sapiCategory(String id, String name) {
        SapiCategory category = new SapiCategory();
        category.setId(id);
        category.setName(name);
        return category;
    }

    @NotNull
    private static SapiCategory sapiCategory(String id, String name, String countryCode) {
        SapiCategory category = new SapiCategory();
        category.setId(id);
        category.setName(name);
        category.setCountryCode(countryCode);
        return category;
    }

    public static SapiCategory virtualFootball() {
        return sapiCategory("sr:category:1111", "Virtual Football");
    }

    @Value
    public static final class SportAwareSapiCategory {

        private final SapiCategory category;
        private final Urn sportId;
    }
}
