<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
  <market_descriptions response_code="OK">
    <market id="282" name="Innings 1 to 5th top - {$competitor1} total" groups="all|score|4.5_innings">
      <outcomes>
        <outcome id="13" name="under {total}"/>
        <outcome id="12" name="over {total}"/>
      </outcomes>
      <specifiers>
        <specifier name="total" type="decimal"/>
      </specifiers>
    </market>
    <market id="701" name="Any player to score {milestone}" groups="all">
      <outcomes>
        <outcome id="74" name="yes"/>
        <outcome id="76" name="no"/>
      </outcomes>
      <specifiers>
        <specifier name="milestone" type="integer"/>
        <specifier name="maxovers" type="integer"/>
      </specifiers>
    </market>
    <market id="625" name="{!mapnr} map - player with most deaths (incl. overtime)" groups="all|map_incl_ot|player" includes_outcomes_of_type="sr:player">
    <specifiers>
      <specifier name="mapnr" type="integer"/>
    </specifiers>
  </market>
  <market id="683" name="Top batter" groups="all" includes_outcomes_of_type="sr:player">
    <specifiers>
      <specifier name="maxovers" type="integer"/>
      <specifier name="type" type="string"/>
    </specifiers>
  </market>
  <market id="337" name="{!mapnr} map knife round - winner" groups="all|round">
  <outcomes>
    <outcome id="4" name="{$competitor1}"/>
    <outcome id="5" name="{$competitor2}"/>
  </outcomes>
  <specifiers>
    <specifier name="mapnr" type="integer"/>
  </specifiers>
</market>
<market id="1170" name="Holes {from} to {to} - head2head (1x2)" groups="all">
  <outcomes>
    <outcome id="1966" name="{%competitor1}"/>
    <outcome id="1967" name="draw"/>
    <outcome id="1968" name="{%competitor2}"/>
  </outcomes>
  <specifiers>
    <specifier name="from" type="integer"/>
    <specifier name="to" type="integer"/>
    <specifier name="competitor1" type="string"/>
    <specifier name="competitor2" type="string"/>
  </specifiers>
  <attributes>
    <attribute name="is_golf_match_play_market" description="This market is applicable to Golf match play"/>
  </attributes>
</market>
<market id="340" name="Winner (incl. super over)" groups="all|score|incl_so">
  <outcomes>
    <outcome id="4" name="{$competitor1}"/>
    <outcome id="5" name="{$competitor2}"/>
  </outcomes>
</market>
<market id="1084" name="Exact strikes of {%player} {!appearancenr} time at bat" groups="all">
<outcomes>
  <outcome id="1999" name="0"/>
  <outcome id="2000" name="1"/>
  <outcome id="2001" name="2"/>
  <outcome id="2002" name="3"/>
</outcomes>
<specifiers>
  <specifier name="appearancenr" type="integer"/>
  <specifier name="player" type="string"/>
</specifiers>
</market>
<market id="447" name="{!periodnr} period - {$competitor1} total" groups="all|score|period">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="periodnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="1075" name="{!inningnr} inning - {$competitor2} exact home runs" groups="all">
<outcomes>
<outcome id="1996" name="0"/>
<outcome id="1997" name="1"/>
<outcome id="1998" name="2+"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="1066" name="{!drivenr} drive - {%competitor} result" groups="all|drive">
<outcomes>
<outcome id="1992" name="punt"/>
<outcome id="1993" name="touchdown"/>
<outcome id="1994" name="field goal attempt"/>
<outcome id="1995" name="other"/>
</outcomes>
<specifiers>
<specifier name="drivenr" type="integer"/>
<specifier name="competitor" type="string"/>
</specifiers>
</market>
<market id="1102" name="Top {winners} (teams)" groups="all" includes_outcomes_of_type="sr:competitor" outcome_type="competitor">
<specifiers>
<specifier name="winners" type="integer" description="number of winners"/>
</specifiers>
</market>
<market id="1008" name="{%competitor} total strokes" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="competitor" type="string"/>
<specifier name="total" type="decimal"/>
</specifiers>
<attributes>
<attribute name="is_golf_stroke_play_market" description="This market is applicable to Golf stroke play"/>
</attributes>
</market>
<market id="69" name="1st half - {$competitor1} total" groups="all|score|1st_half">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="124" name="Penalty shootout - {!penaltynr} penalty scored" groups="all|score|pen_so">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="penaltynr" type="integer"/>
</specifiers>
</market>
<market id="868" name="Place" groups="all" includes_outcomes_of_type="sr:competitor">
<specifiers>
<specifier name="pos" type="integer"/>
</specifiers>
</market>
<market id="131" name="Penalty shootout - odd/even" groups="all|score|pen_so">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
</market>
<market id="350" name="{!inningnr} innings - {$competitor2} total at {!dismissalnr} dismissal" groups="all|score|innings">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="dismissalnr" type="integer"/>
<specifier name="total" type="decimal"/>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="1094" name="{!retirementnr} to retire" groups="all" includes_outcomes_of_type="sr:competitor" outcome_type="competitor">
<specifiers>
<specifier name="retirementnr" type="integer" description="retirement"/>
</specifiers>
</market>
<market id="1140" name="{!inningnr} innings - {%player1} or {%player2} dismissal method" groups="all">
<outcomes>
<outcome id="1806" name="fielder catch"/>
<outcome id="1807" name="bowled"/>
<outcome id="1808" name="keeper catch"/>
<outcome id="1809" name="lbw"/>
<outcome id="1810" name="run out"/>
<outcome id="1811" name="stumped"/>
<outcome id="1812" name="other"/>
</outcomes>
</market>
<market id="385" name="{$competitor1} total 180s" groups="all|regular_play|180s">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="62" name="1st half - {!goalnr} goal" groups="all|score|1st_half">
<outcomes>
<outcome id="6" name="{$competitor1}"/>
<outcome id="7" name="none"/>
<outcome id="8" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="goalnr" type="integer"/>
</specifiers>
</market>
<market id="351" name="{!inningnr} innings overs 0 to {overnr} - 1x2" groups="all|score|x_overs">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="overnr" type="integer"/>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="612" name="Draw no bet (incl. overtime)" groups="all|score|incl_ot">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
</market>
<market id="302" name="{!quarternr} quarter - draw no bet" groups="all|score|quarter">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="quarternr" type="integer"/>
</specifiers>
</market>
<market id="556" name="{!mapnr} map - {!xth} dragon" groups="all|map|structures">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
<specifier name="xth" type="integer"/>
</specifiers>
</market>
<market id="110" name="5 minutes - total from {from} to {to}" groups="all|score|5_min">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="33" name="{$competitor1} win to nil" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="970" name="Total sixes spread" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
</market>
<market id="700" name="{!inningnr} innings - any player to score {milestone}" groups="all">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="milestone" type="integer"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="673" name="{!inningnr} innings - {$competitor2} exact runs" groups="all">
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="114" name="Overtime - which team wins the rest" groups="all|score|ot">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="score" type="string" description="current score in match"/>
</specifiers>
</market>
<market id="621" name="{!mapnr} map - odd/even rounds (incl. overtime)" groups="all|score|map_incl_ot">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
</specifiers>
</market>
<market id="31" name="{$competitor1} clean sheet" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="12" name="{$competitor1} no bet" groups="all|score|regular_play">
<outcomes>
<outcome id="776" name="draw"/>
<outcome id="778" name="{$competitor2}"/>
</outcomes>
</market>
<market id="1011" name="{%competitor} total birdies" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="competitor" type="string"/>
<specifier name="total" type="decimal"/>
</specifiers>
<attributes>
<attribute name="is_golf_stroke_play_market" description="This market is applicable to Golf stroke play"/>
</attributes>
</market>
<market id="129" name="Penalty shootout - {$competitor2} total" groups="all|score|pen_so">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="322" name="{!setnr} set end {endnr} - winner" groups="all|score|end">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="endnr" type="integer"/>
</specifiers>
</market>
<market id="792" name="Player disposals" groups="all|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="323" name="{!setnr} set end {endnr} - total" groups="all|score|end">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="endnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="234" name="Highest scoring quarter" groups="all|score|regular_play">
<outcomes>
<outcome id="920" name="1st quarter"/>
<outcome id="921" name="2nd quarter"/>
<outcome id="922" name="3rd quarter"/>
<outcome id="923" name="4th quarter"/>
<outcome id="924" name="equal"/>
</outcomes>
</market>
<market id="176" name="1st half - corner handicap" groups="all|1st_half|corners">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="486" name="1st half - try handicap" groups="all|1st_half|tries">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="376" name="{!setnr} set leg {legnr} - total darts" groups="all|score|leg">
<outcomes>
<outcome id="1039" name="over {total}"/>
<outcome id="1040" name="under {total}"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="legnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="984" name="{!inningnr} innings - {$competitor1} total {upsnr} ups spread" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="upsnr" type="integer"/>
</specifiers>
</market>
<market id="309" name="{!setnr} set - point handicap" groups="all|score|set">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="584" name="5 minutes - {$competitor2} total corners from {from} to {to}" groups="all|5_min|corners">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="27" name="{$competitor1} odd/even" groups="all|score|regular_play">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
</market>
<market id="727" name="{!mapnr} map - total aegis" groups="all|map|structures">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="1103" name="Head2head (teams)" groups="all" includes_outcomes_of_type="sr:competitor" outcome_type="competitor">
<specifiers>
<specifier name="id" type="string"/>
</specifiers>
</market>
<market id="1121" name="{$competitor1} total home runs (incl. extra innings)" groups="all|incl_ei|home_run">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="1120" name="Total home runs (incl. extra innings)" groups="all|incl_ei|home_run">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="83" name="2nd half - 1x2" groups="all|score|2nd_half">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
</market>
<market id="610" name="1x2 (incl. overtime)" groups="all|score|incl_ot">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
</market>
<market id="791" name="Player handballs" groups="all|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="631" name="{!mapnr} map round {roundnr} - bomb planted" groups="all|round|bomb">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
<specifier name="roundnr" type="integer"/>
</specifiers>
</market>
<market id="95" name="2nd half - both teams to score" groups="all|score|2nd_half">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="980" name="{$competitor1} joy of six spread" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
<specifiers>
<specifier name="player1" type="string"/>
<specifier name="player2" type="string"/>
<specifier name="player3" type="string"/>
</specifiers>
</market>
<market id="887" name="1 minute - total penalties awarded from {from} to {to}" groups="all|rapid_market|penalties">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="from" type="string"/>
<specifier name="to" type="string"/>
</specifiers>
</market>
<market id="411" name="US spread (incl. overtime and penalties)" groups="all|score|incl_ot_and_pen">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="820" name="Halftime/fulltime &amp; exact goals" groups="all|regular_play|combo">
<outcomes>
<outcome id="1854" name="draw/draw &amp; 0"/>
<outcome id="1855" name="{$competitor1}/{$competitor1} &amp; 1"/>
<outcome id="1856" name="draw/{$competitor1} &amp; 1"/>
<outcome id="1857" name="draw/{$competitor2} &amp; 1"/>
<outcome id="1858" name="{$competitor2}/{$competitor2} &amp; 1"/>
<outcome id="1859" name="{$competitor1}/{$competitor1} &amp; 2"/>
<outcome id="1860" name="{$competitor1}/draw &amp; 2"/>
<outcome id="1861" name="draw/{$competitor1} &amp; 2"/>
<outcome id="1862" name="draw/draw &amp; 2"/>
<outcome id="1863" name="draw/{$competitor2} &amp; 2"/>
<outcome id="1864" name="{$competitor2}/draw &amp; 2"/>
<outcome id="1865" name="{$competitor2}/{$competitor2} &amp; 2"/>
<outcome id="1866" name="{$competitor1}/{$competitor1} &amp; 3"/>
<outcome id="1867" name="{$competitor1}/{$competitor2} &amp; 3"/>
<outcome id="1868" name="draw/{$competitor1} &amp; 3"/>
<outcome id="1869" name="draw/{$competitor2} &amp; 3"/>
<outcome id="1870" name="{$competitor2}/{$competitor1} &amp; 3"/>
<outcome id="1871" name="{$competitor2}/{$competitor2} &amp; 3"/>
<outcome id="1872" name="{$competitor1}/{$competitor1} &amp; 4"/>
<outcome id="1873" name="{$competitor1}/draw &amp; 4"/>
<outcome id="1874" name="{$competitor1}/{$competitor2} &amp; 4"/>
<outcome id="1875" name="draw/{$competitor1} &amp; 4"/>
<outcome id="1876" name="draw/draw &amp; 4"/>
<outcome id="1877" name="draw/{$competitor2} &amp; 4"/>
<outcome id="1878" name="{$competitor2}/{$competitor1} &amp; 4"/>
<outcome id="1879" name="{$competitor2}/draw &amp; 4"/>
<outcome id="1880" name="{$competitor2}/{$competitor2} &amp; 4"/>
<outcome id="1881" name="{$competitor1}/{$competitor1} &amp; 5+"/>
<outcome id="1882" name="{$competitor1}/draw &amp; 5+"/>
<outcome id="1883" name="{$competitor1}/{$competitor2} &amp; 5+"/>
<outcome id="1884" name="draw/{$competitor1} &amp; 5+"/>
<outcome id="1885" name="draw/draw &amp; 5+"/>
<outcome id="1886" name="draw/{$competitor2} &amp; 5+"/>
<outcome id="1887" name="{$competitor2}/{$competitor1} &amp; 5+"/>
<outcome id="1888" name="{$competitor2}/draw &amp; 5+"/>
<outcome id="1889" name="{$competitor2}/{$competitor2} &amp; 5+"/>
</outcomes>
</market>
<market id="125" name="Penalty shootout - {!goalnr} goal" groups="all|score|pen_so">
<outcomes>
<outcome id="6" name="{$competitor1}"/>
<outcome id="7" name="none"/>
<outcome id="8" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="goalnr" type="integer"/>
</specifiers>
</market>
<market id="906" name="Championship free text market" groups="all" includes_outcomes_of_type="pre:outcometext">
<specifiers>
<specifier name="variant" type="variable_text"/>
<specifier name="version" type="string"/>
</specifiers>
</market>
<market id="757" name="{!quarternr} quarter - {$competitor2} total" groups="all|score|quarter">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="quarternr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="227" name="{$competitor1} total (incl. overtime)" groups="all|score|incl_ot">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="164" name="Last corner" groups="all|regular_play|corners">
<outcomes>
<outcome id="6" name="{$competitor1}"/>
<outcome id="7" name="none"/>
<outcome id="8" name="{$competitor2}"/>
</outcomes>
</market>
<market id="430" name="Correct score [{score}]" groups="all|score|regular_play">
<outcomes>
<outcome id="1538" name="0:0"/>
<outcome id="1539" name="1:0"/>
<outcome id="1540" name="2:0"/>
<outcome id="1541" name="3:0"/>
<outcome id="1542" name="4:0"/>
<outcome id="1543" name="5:0"/>
<outcome id="1544" name="6:0"/>
<outcome id="1545" name="7:0"/>
<outcome id="1546" name="8:0"/>
<outcome id="1547" name="0:1"/>
<outcome id="1548" name="1:1"/>
<outcome id="1549" name="2:1"/>
<outcome id="1550" name="3:1"/>
<outcome id="1551" name="4:1"/>
<outcome id="1552" name="5:1"/>
<outcome id="1553" name="6:1"/>
<outcome id="1554" name="7:1"/>
<outcome id="1555" name="0:2"/>
<outcome id="1556" name="1:2"/>
<outcome id="1557" name="2:2"/>
<outcome id="1558" name="3:2"/>
<outcome id="1559" name="4:2"/>
<outcome id="1560" name="5:2"/>
<outcome id="1561" name="6:2"/>
<outcome id="1562" name="0:3"/>
<outcome id="1563" name="1:3"/>
<outcome id="1564" name="2:3"/>
<outcome id="1565" name="3:3"/>
<outcome id="1566" name="4:3"/>
<outcome id="1567" name="5:3"/>
<outcome id="1568" name="0:4"/>
<outcome id="1569" name="1:4"/>
<outcome id="1570" name="2:4"/>
<outcome id="1571" name="3:4"/>
<outcome id="1572" name="4:4"/>
<outcome id="1573" name="0:5"/>
<outcome id="1574" name="1:5"/>
<outcome id="1575" name="2:5"/>
<outcome id="1576" name="3:5"/>
<outcome id="1577" name="0:6"/>
<outcome id="1578" name="1:6"/>
<outcome id="1579" name="2:6"/>
<outcome id="1580" name="0:7"/>
<outcome id="1581" name="1:7"/>
<outcome id="1582" name="0:8"/>
</outcomes>
<specifiers>
<specifier name="score" type="string" description="current score in match"/>
</specifiers>
<attributes>
<attribute name="is_flex_score" description="Outcomes should be adjusted according to score specifier"/>
</attributes>
</market>
<market id="841" name="{!inningnr} innings - {$competitor1} total spread at {!dismissalnr} dismissal" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="dismissalnr" type="integer"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
<attributes>
<attribute name="is_spread_market" description="This is a spread-market and special client-side spread rules need to be followed for odds-changes and bet-settlements"/>
</attributes>
</market>
<market id="367" name="Total legs" groups="all|score|regular_play">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="528" name="{!setnr} set - total" groups="all|score|set">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="165" name="Corner handicap" groups="all|regular_play|corners">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="1146" name="{!inningnr} innings - race to {runs} runs" groups="all">
<outcomes>
<outcome id="2023" name="{%player1}"/>
<outcome id="2024" name="none"/>
<outcome id="2025" name="{%player2}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="runs" type="integer"/>
<specifier name="player1" type="string"/>
<specifier name="player2" type="string"/>
</specifiers>
</market>
<market id="150" name="1st half - {!bookingnr} booking" groups="all|1st_half|bookings">
<outcomes>
<outcome id="6" name="{$competitor1}"/>
<outcome id="7" name="none"/>
<outcome id="8" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="bookingnr" type="integer"/>
</specifiers>
</market>
<market id="756" name="{!quarternr} quarter - {$competitor1} total" groups="all|score|quarter">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="quarternr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="654" name="Total run outs" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="475" name="Try draw no bet" groups="all|regular_play|tries">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
</market>
<market id="914" name="{%player} total passing yards (incl. overtime)" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="player" type="string"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="711" name="{!inningnr} innings - 1x2" groups="all">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="655" name="Total extras" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="613" name="{!quarternr} quarter - draw no bet (incl. overtime)" groups="all|score|quarter_incl_ot">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="quarternr" type="integer"/>
</specifiers>
</market>
<market id="858" name="{$competitor2} or over {total}" groups="all|regular_play|combo">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="632" name="{!mapnr} map round {roundnr} - bomb defused" groups="all|round|bomb">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
<specifier name="roundnr" type="integer"/>
</specifiers>
</market>
<market id="476" name="Try handicap {hcp}" groups="all|regular_play|tries">
<outcomes>
<outcome id="1711" name="{$competitor1} ({hcp})"/>
<outcome id="1712" name="draw ({hcp})"/>
<outcome id="1713" name="{$competitor2} ({hcp})"/>
</outcomes>
<specifiers>
<specifier name="hcp" type="string"/>
</specifiers>
</market>
<market id="895" name="{!setnr} set tiebreak - total points" groups="all|score|tiebreak">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="206" name="{!setnr} set - will there be a tiebreak" groups="all|score|set">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
</specifiers>
</market>
<market id="34" name="{$competitor2} win to nil" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="1056" name="{!setnr} set - winner &amp; total" groups="all|combo|set">
<outcomes>
<outcome id="973" name="{$competitor1} &amp; over {total}"/>
<outcome id="974" name="{$competitor2} &amp; over {total}"/>
<outcome id="975" name="{$competitor1} &amp; under {total}"/>
<outcome id="976" name="{$competitor2} &amp; under {total}"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="1168" name="Hole {holenr} - head2head (1x2)" groups="all">
<outcomes>
<outcome id="1966" name="{%competitor1}"/>
<outcome id="1967" name="draw"/>
<outcome id="1968" name="{%competitor2}"/>
</outcomes>
<specifiers>
<specifier name="holenr" type="integer"/>
<specifier name="competitor1" type="string"/>
<specifier name="competitor2" type="string"/>
</specifiers>
<attributes>
<attribute name="is_golf_match_play_market" description="This market is applicable to Golf match play"/>
</attributes>
</market>
<market id="784" name="Batter runs (incl. extra innings)" groups="all|incl_ei|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="1024" name="Hole {holenr} - 2 ball (handicap)" groups="all">
<outcomes>
<outcome id="1969" name="{%competitor1} ({+hcp})"/>
<outcome id="1970" name="{%competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="holenr" type="integer"/>
<specifier name="hcp" type="decimal"/>
<specifier name="competitor1" type="string"/>
<specifier name="competitor2" type="string"/>
</specifiers>
<attributes>
<attribute name="is_golf_stroke_play_market" description="This market is applicable to Golf stroke play"/>
</attributes>
</market>
<market id="5" name="Winning method" groups="all|score|cup_tie">
<outcomes>
<outcome id="14" name="{$competitor1} regular time"/>
<outcome id="15" name="{$competitor2} regular time"/>
<outcome id="16" name="{$competitor1} overtime"/>
<outcome id="17" name="{$competitor2} overtime"/>
<outcome id="18" name="{$competitor1} penalties"/>
<outcome id="19" name="{$competitor2} penalties"/>
</outcomes>
</market>
<market id="774" name="Player 3-point field goals (incl. overtime)" groups="all|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="819" name="Halftime/fulltime &amp; 1st half total" groups="all|regular_play|combo">
<outcomes>
<outcome id="1836" name="{$competitor1}/{$competitor1} &amp; under {total}"/>
<outcome id="1837" name="{$competitor1}/draw &amp; under {total}"/>
<outcome id="1838" name="{$competitor1}/{$competitor2} &amp; under {total}"/>
<outcome id="1839" name="draw/{$competitor1} &amp; under {total}"/>
<outcome id="1840" name="draw/draw &amp; under {total}"/>
<outcome id="1841" name="draw/{$competitor2} &amp; under {total}"/>
<outcome id="1842" name="{$competitor2}/{$competitor1} &amp; under {total}"/>
<outcome id="1843" name="{$competitor2}/draw &amp; under {total}"/>
<outcome id="1844" name="{$competitor2}/{$competitor2} &amp; under {total}"/>
<outcome id="1845" name="{$competitor1}/{$competitor1} &amp; over {total}"/>
<outcome id="1846" name="{$competitor1}/draw &amp; over {total}"/>
<outcome id="1847" name="{$competitor1}/{$competitor2} &amp; over {total}"/>
<outcome id="1848" name="draw/{$competitor1} &amp; over {total}"/>
<outcome id="1849" name="draw/draw &amp; over {total}"/>
<outcome id="1850" name="draw/{$competitor2} &amp; over {total}"/>
<outcome id="1851" name="{$competitor2}/{$competitor1} &amp; over {total}"/>
<outcome id="1852" name="{$competitor2}/draw &amp; over {total}"/>
<outcome id="1853" name="{$competitor2}/{$competitor2} &amp; over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="130" name="Penalty shootout - exact goals" groups="all|score|pen_so">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="1002" name="{!inningnr} innings - {%player} {upsnr} ups spread" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="upsnr" type="integer"/>
<specifier name="player" type="string"/>
</specifiers>
</market>
<market id="593" name="10 minutes - sending off from {from} to {to}" groups="all|10_min|bookings">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="288" name="{!inningnr} inning - total" groups="all|score|inning">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="876" name="{!inningnr} innings - {$competitor2} total at {!dismissalnr} dismissal" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="dismissalnr" type="integer"/>
<specifier name="total" type="decimal"/>
<specifier name="inningnr" type="integer"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="454" name="{!periodnr} period - {$competitor1} clean sheet" groups="all|score|period">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="periodnr" type="integer"/>
</specifiers>
</market>
<market id="1161" name="Head2head (1x2)" groups="all">
<outcomes>
<outcome id="1966" name="{%competitor1}"/>
<outcome id="1967" name="draw"/>
<outcome id="1968" name="{%competitor2}"/>
</outcomes>
<specifiers>
<specifier name="competitor1" type="string"/>
<specifier name="competitor2" type="string"/>
</specifiers>
<attributes>
<attribute name="is_golf_match_play_market" description="This market is applicable to Golf match play"/>
</attributes>
</market>
<market id="303" name="{!quarternr} quarter - handicap" groups="all|score|quarter">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="quarternr" type="integer"/>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="266" name="{!runnr} run (incl. extra innings)" groups="all|score|incl_ei">
<outcomes>
<outcome id="6" name="{$competitor1}"/>
<outcome id="7" name="none"/>
<outcome id="8" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="runnr" type="integer"/>
</specifiers>
</market>
<market id="207" name="{!setnr} set - correct score" groups="all|score|set">
<outcomes>
<outcome id="865" name="6:0"/>
<outcome id="866" name="6:1"/>
<outcome id="867" name="6:2"/>
<outcome id="868" name="6:3"/>
<outcome id="869" name="6:4"/>
<outcome id="870" name="7:5"/>
<outcome id="871" name="7:6"/>
<outcome id="872" name="0:6"/>
<outcome id="873" name="1:6"/>
<outcome id="874" name="2:6"/>
<outcome id="875" name="3:6"/>
<outcome id="876" name="4:6"/>
<outcome id="877" name="5:7"/>
<outcome id="878" name="6:7"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
</specifiers>
</market>
<market id="192" name="{$competitor1} to win a set" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="1176" name="1st quarter handicap &amp; 1st quarter total" groups="all|combo|incl_ot">
<outcomes>
<outcome id="2035" name="{$competitor1} ({+hcp}) &amp; over {total}"/>
<outcome id="2036" name="{$competitor1} ({+hcp}) &amp; under {total}"/>
<outcome id="2037" name="{$competitor2} ({-hcp}) &amp; over {total}"/>
<outcome id="2038" name="{$competitor2} ({-hcp}) &amp; under {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="421" name="{$competitor1} clean sheet (incl. overtime and penalties)" groups="all|score|incl_ot_and_pen">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="260" name="{$competitor1} total (incl. extra innings)" groups="all|score|incl_ei">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="312" name="{!setnr} set - {!pointnr} point" groups="all|score|set">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="pointnr" type="integer"/>
</specifiers>
</market>
<market id="1076" name="{!inningnr} inning - total pitches" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="28" name="{$competitor2} odd/even" groups="all|score|regular_play">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
</market>
<market id="829" name="Bonus ball equals any regular ball" groups="all">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="783" name="Batter runs batted in (incl. extra innings)" groups="all|incl_ei|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="746" name="{!inningnr} inning - handicap" groups="all|score|inning">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="68" name="1st half - total" groups="all|score|1st_half">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="557" name="{!mapnr} map - {!xth} baron" groups="all|map|structures">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
<specifier name="xth" type="integer"/>
</specifiers>
</market>
<market id="1044" name="{!inningnr} inning - {$competitor1} total hits" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="221" name="{!scorenr} score (incl. overtime)" groups="all">
<outcomes>
<outcome id="6" name="{$competitor1}"/>
<outcome id="7" name="none"/>
<outcome id="8" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="scorenr" type="integer"/>
</specifiers>
</market>
<market id="999" name="{%player} total player performance spread" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
<specifiers>
<specifier name="player" type="string"/>
</specifiers>
</market>
<market id="96" name="2nd half - {$competitor1} clean sheet" groups="all|score|2nd_half">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="1027" name="Holes {from} to {to} - 3 ball" groups="all" includes_outcomes_of_type="sr:competitor" outcome_type="competitor">
<specifiers>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
<attributes>
<attribute name="is_golf_stroke_play_market" description="This market is applicable to Golf stroke play"/>
</attributes>
</market>
<market id="316" name="{!setnr} set - draw no bet" groups="all|score|set">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
</specifiers>
</market>
<market id="690" name="{!inningnr} innings - {$competitor1} odd/even" groups="all">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="682" name="Total in the highest scoring over" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="638" name="{!inningnr} innings - {%player} total" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="player" type="string"/>
<specifier name="total" type="decimal"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="602" name="1st half - race to {cornernr} corners" groups="all|1st_half|corners">
<outcomes>
<outcome id="6" name="{$competitor1}"/>
<outcome id="7" name="none"/>
<outcome id="8" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="cornernr" type="integer"/>
</specifiers>
</market>
<market id="1063" name="{!drivenr} drive play {playnr} - {%competitor} total yards gained" groups="all|drive">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="drivenr" type="integer"/>
<specifier name="playnr" type="integer"/>
<specifier name="competitor" type="string"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="674" name="{!inningnr} innings - {$competitor1} top batter" groups="all" includes_outcomes_of_type="sr:player">
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="maxovers" type="integer"/>
<specifier name="type" type="string"/>
</specifiers>
</market>
<market id="489" name="1st half - {$competitor2} total tries" groups="all|1st_half|tries">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="412" name="Total (incl. overtime and penalties)" groups="all|score|incl_ot_and_pen">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="502" name="{!framenr} frame - odd/even" groups="all|score|frame">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
<specifiers>
<specifier name="framenr" type="integer"/>
</specifiers>
</market>
<market id="909" name="{!touchdownnr} touchdown scorer (incl. overtime)" groups="all|scorers|incl_ot" includes_outcomes_of_type="sr:player" outcome_type="player">
<outcomes>
<outcome id="1949" name="{$competitor1} d/st player"/>
<outcome id="1950" name="{$competitor1} other player"/>
<outcome id="1951" name="{$competitor2} d/st player"/>
<outcome id="1952" name="{$competitor2} other player"/>
</outcomes>
<specifiers>
<specifier name="touchdownnr" type="integer"/>
<specifier name="version" type="string"/>
</specifiers>
</market>
<market id="482" name="1st half - try 1x2" groups="all|1st_half|tries">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
</market>
<market id="148" name="{$competitor2} sending off" groups="all|regular_play|bookings">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="1049" name="Result of {!pitchnr} pitch" groups="all|rapid_market|pitch">
<outcomes>
<outcome id="1978" name="strike"/>
<outcome id="1979" name="ball"/>
<outcome id="1980" name="hit"/>
<outcome id="1981" name="other"/>
</outcomes>
<specifiers>
<specifier name="pitchnr" type="integer"/>
</specifiers>
</market>
<market id="157" name="1st half - {$competitor2} exact bookings" groups="all|1st_half|bookings">
<outcomes>
<outcome id="54" name="0"/>
<outcome id="56" name="1"/>
<outcome id="58" name="2"/>
<outcome id="60" name="3+"/>
</outcomes>
</market>
<market id="565" name="15 minutes - corner 1x2 from {from} to {to}" groups="all|15_min|corners">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="1093" name="Fastest lap" groups="all" includes_outcomes_of_type="sr:competitor" outcome_type="competitor"/>
<market id="281" name="Innings 1 to 5th top - total" groups="all|score|4.5_innings">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="611" name="{!quarternr} quarter - 1x2 (incl. overtime)" groups="all|score|quarter_incl_ot">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="quarternr" type="integer"/>
</specifiers>
</market>
<market id="956" name="{!timeoutnr} timeout" groups="all">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="timeoutnr" type="integer"/>
</specifiers>
</market>
<market id="431" name="Correct score" groups="all|score|regular_play">
<outcomes>
<outcome id="1584" name="0:0"/>
<outcome id="1585" name="1:0"/>
<outcome id="1586" name="2:0"/>
<outcome id="1587" name="3:0"/>
<outcome id="1588" name="4:0"/>
<outcome id="1589" name="5:0"/>
<outcome id="1590" name="0:1"/>
<outcome id="1591" name="1:1"/>
<outcome id="1592" name="2:1"/>
<outcome id="1593" name="3:1"/>
<outcome id="1594" name="4:1"/>
<outcome id="1595" name="5:1"/>
<outcome id="1596" name="0:2"/>
<outcome id="1597" name="1:2"/>
<outcome id="1598" name="2:2"/>
<outcome id="1599" name="3:2"/>
<outcome id="1600" name="4:2"/>
<outcome id="1601" name="5:2"/>
<outcome id="1602" name="0:3"/>
<outcome id="1603" name="1:3"/>
<outcome id="1604" name="2:3"/>
<outcome id="1605" name="3:3"/>
<outcome id="1606" name="4:3"/>
<outcome id="1607" name="5:3"/>
<outcome id="1608" name="0:4"/>
<outcome id="1609" name="1:4"/>
<outcome id="1610" name="2:4"/>
<outcome id="1611" name="3:4"/>
<outcome id="1612" name="4:4"/>
<outcome id="1613" name="5:4"/>
<outcome id="1614" name="0:5"/>
<outcome id="1615" name="1:5"/>
<outcome id="1616" name="2:5"/>
<outcome id="1617" name="3:5"/>
<outcome id="1618" name="4:5"/>
<outcome id="1619" name="5:5"/>
<outcome id="1620" name="other"/>
</outcomes>
</market>
<market id="330" name="{!mapnr} map - winner (incl. overtime)" groups="all|score|map_incl_ot">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
</specifiers>
</market>
<market id="582" name="5 minutes - total corners from {from} to {to}" groups="all|5_min|corners">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="747" name="{!inningnr} inning - {$competitor1} total" groups="all|score|inning">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="193" name="{$competitor2} to win a set" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="402" name="1st half - correct score [{score}]" groups="all|score|1st_half">
<outcomes>
<outcome id="1058" name="0:0"/>
<outcome id="1059" name="0:1"/>
<outcome id="1060" name="0:2"/>
<outcome id="1061" name="0:3"/>
<outcome id="1062" name="0:4"/>
<outcome id="1063" name="0:5"/>
<outcome id="1064" name="0:6"/>
<outcome id="1065" name="0:7"/>
<outcome id="1066" name="0:8"/>
<outcome id="1067" name="0:9"/>
<outcome id="1068" name="0:10"/>
<outcome id="1069" name="0:11"/>
<outcome id="1070" name="0:12"/>
<outcome id="1071" name="0:13"/>
<outcome id="1072" name="0:14"/>
<outcome id="1073" name="0:15"/>
<outcome id="1074" name="0:16"/>
<outcome id="1075" name="0:17"/>
<outcome id="1076" name="0:18"/>
<outcome id="1077" name="0:19"/>
<outcome id="1078" name="1:0"/>
<outcome id="1079" name="1:1"/>
<outcome id="1080" name="1:2"/>
<outcome id="1081" name="1:3"/>
<outcome id="1082" name="1:4"/>
<outcome id="1083" name="1:5"/>
<outcome id="1084" name="1:6"/>
<outcome id="1085" name="1:7"/>
<outcome id="1086" name="1:8"/>
<outcome id="1087" name="1:9"/>
<outcome id="1088" name="1:10"/>
<outcome id="1089" name="1:11"/>
<outcome id="1090" name="1:12"/>
<outcome id="1091" name="1:13"/>
<outcome id="1092" name="1:14"/>
<outcome id="1093" name="1:15"/>
<outcome id="1094" name="1:16"/>
<outcome id="1095" name="1:17"/>
<outcome id="1096" name="1:18"/>
<outcome id="1097" name="1:19"/>
<outcome id="1098" name="2:0"/>
<outcome id="1099" name="2:1"/>
<outcome id="1100" name="2:2"/>
<outcome id="1101" name="2:3"/>
<outcome id="1102" name="2:4"/>
<outcome id="1103" name="2:5"/>
<outcome id="1104" name="2:6"/>
<outcome id="1105" name="2:7"/>
<outcome id="1106" name="2:8"/>
<outcome id="1107" name="2:9"/>
<outcome id="1108" name="2:10"/>
<outcome id="1109" name="2:11"/>
<outcome id="1110" name="2:12"/>
<outcome id="1111" name="2:13"/>
<outcome id="1112" name="2:14"/>
<outcome id="1113" name="2:15"/>
<outcome id="1114" name="2:16"/>
<outcome id="1115" name="2:17"/>
<outcome id="1116" name="2:18"/>
<outcome id="1117" name="2:19"/>
<outcome id="1118" name="3:0"/>
<outcome id="1119" name="3:1"/>
<outcome id="1120" name="3:2"/>
<outcome id="1121" name="3:3"/>
<outcome id="1122" name="3:4"/>
<outcome id="1123" name="3:5"/>
<outcome id="1124" name="3:6"/>
<outcome id="1125" name="3:7"/>
<outcome id="1126" name="3:8"/>
<outcome id="1127" name="3:9"/>
<outcome id="1128" name="3:10"/>
<outcome id="1129" name="3:11"/>
<outcome id="1130" name="3:12"/>
<outcome id="1131" name="3:13"/>
<outcome id="1132" name="3:14"/>
<outcome id="1133" name="3:15"/>
<outcome id="1134" name="3:16"/>
<outcome id="1135" name="3:17"/>
<outcome id="1136" name="3:18"/>
<outcome id="1137" name="3:19"/>
<outcome id="1138" name="4:0"/>
<outcome id="1139" name="4:1"/>
<outcome id="1140" name="4:2"/>
<outcome id="1141" name="4:3"/>
<outcome id="1142" name="4:4"/>
<outcome id="1143" name="4:5"/>
<outcome id="1144" name="4:6"/>
<outcome id="1145" name="4:7"/>
<outcome id="1146" name="4:8"/>
<outcome id="1147" name="4:9"/>
<outcome id="1148" name="4:10"/>
<outcome id="1149" name="4:11"/>
<outcome id="1150" name="4:12"/>
<outcome id="1151" name="4:13"/>
<outcome id="1152" name="4:14"/>
<outcome id="1153" name="4:15"/>
<outcome id="1154" name="4:16"/>
<outcome id="1155" name="4:17"/>
<outcome id="1156" name="4:18"/>
<outcome id="1157" name="4:19"/>
<outcome id="1158" name="5:0"/>
<outcome id="1159" name="5:1"/>
<outcome id="1160" name="5:2"/>
<outcome id="1161" name="5:3"/>
<outcome id="1162" name="5:4"/>
<outcome id="1163" name="5:5"/>
<outcome id="1164" name="5:6"/>
<outcome id="1165" name="5:7"/>
<outcome id="1166" name="5:8"/>
<outcome id="1167" name="5:9"/>
<outcome id="1168" name="5:10"/>
<outcome id="1169" name="5:11"/>
<outcome id="1170" name="5:12"/>
<outcome id="1171" name="5:13"/>
<outcome id="1172" name="5:14"/>
<outcome id="1173" name="5:15"/>
<outcome id="1174" name="5:16"/>
<outcome id="1175" name="5:17"/>
<outcome id="1176" name="5:18"/>
<outcome id="1177" name="5:19"/>
<outcome id="1178" name="6:0"/>
<outcome id="1179" name="6:1"/>
<outcome id="1180" name="6:2"/>
<outcome id="1181" name="6:3"/>
<outcome id="1182" name="6:4"/>
<outcome id="1183" name="6:5"/>
<outcome id="1184" name="6:6"/>
<outcome id="1185" name="6:7"/>
<outcome id="1186" name="6:8"/>
<outcome id="1187" name="6:9"/>
<outcome id="1188" name="6:10"/>
<outcome id="1189" name="6:11"/>
<outcome id="1190" name="6:12"/>
<outcome id="1191" name="6:13"/>
<outcome id="1192" name="6:14"/>
<outcome id="1193" name="6:15"/>
<outcome id="1194" name="6:16"/>
<outcome id="1195" name="6:17"/>
<outcome id="1196" name="6:18"/>
<outcome id="1197" name="6:19"/>
<outcome id="1198" name="7:0"/>
<outcome id="1199" name="7:1"/>
<outcome id="1200" name="7:2"/>
<outcome id="1201" name="7:3"/>
<outcome id="1202" name="7:4"/>
<outcome id="1203" name="7:5"/>
<outcome id="1204" name="7:6"/>
<outcome id="1205" name="7:7"/>
<outcome id="1206" name="7:8"/>
<outcome id="1207" name="7:9"/>
<outcome id="1208" name="7:10"/>
<outcome id="1209" name="7:11"/>
<outcome id="1210" name="7:12"/>
<outcome id="1211" name="7:13"/>
<outcome id="1212" name="7:14"/>
<outcome id="1213" name="7:15"/>
<outcome id="1214" name="7:16"/>
<outcome id="1215" name="7:17"/>
<outcome id="1216" name="7:18"/>
<outcome id="1217" name="7:19"/>
<outcome id="1218" name="8:0"/>
<outcome id="1219" name="8:1"/>
<outcome id="1220" name="8:2"/>
<outcome id="1221" name="8:3"/>
<outcome id="1222" name="8:4"/>
<outcome id="1223" name="8:5"/>
<outcome id="1224" name="8:6"/>
<outcome id="1225" name="8:7"/>
<outcome id="1226" name="8:8"/>
<outcome id="1227" name="8:9"/>
<outcome id="1228" name="8:10"/>
<outcome id="1229" name="8:11"/>
<outcome id="1230" name="8:12"/>
<outcome id="1231" name="8:13"/>
<outcome id="1232" name="8:14"/>
<outcome id="1233" name="8:15"/>
<outcome id="1234" name="8:16"/>
<outcome id="1235" name="8:17"/>
<outcome id="1236" name="8:18"/>
<outcome id="1237" name="8:19"/>
<outcome id="1238" name="9:0"/>
<outcome id="1239" name="9:1"/>
<outcome id="1240" name="9:2"/>
<outcome id="1241" name="9:3"/>
<outcome id="1242" name="9:4"/>
<outcome id="1243" name="9:5"/>
<outcome id="1244" name="9:6"/>
<outcome id="1245" name="9:7"/>
<outcome id="1246" name="9:8"/>
<outcome id="1247" name="9:9"/>
<outcome id="1248" name="9:10"/>
<outcome id="1249" name="9:11"/>
<outcome id="1250" name="9:12"/>
<outcome id="1251" name="9:13"/>
<outcome id="1252" name="9:14"/>
<outcome id="1253" name="9:15"/>
<outcome id="1254" name="9:16"/>
<outcome id="1255" name="9:17"/>
<outcome id="1256" name="9:18"/>
<outcome id="1257" name="9:19"/>
<outcome id="1258" name="10:0"/>
<outcome id="1259" name="10:1"/>
<outcome id="1260" name="10:2"/>
<outcome id="1261" name="10:3"/>
<outcome id="1262" name="10:4"/>
<outcome id="1263" name="10:5"/>
<outcome id="1264" name="10:6"/>
<outcome id="1265" name="10:7"/>
<outcome id="1266" name="10:8"/>
<outcome id="1267" name="10:9"/>
<outcome id="1268" name="10:10"/>
<outcome id="1269" name="10:11"/>
<outcome id="1270" name="10:12"/>
<outcome id="1271" name="10:13"/>
<outcome id="1272" name="10:14"/>
<outcome id="1273" name="10:15"/>
<outcome id="1274" name="10:16"/>
<outcome id="1275" name="10:17"/>
<outcome id="1276" name="10:18"/>
<outcome id="1277" name="10:19"/>
<outcome id="1278" name="11:0"/>
<outcome id="1279" name="11:1"/>
<outcome id="1280" name="11:2"/>
<outcome id="1281" name="11:3"/>
<outcome id="1282" name="11:4"/>
<outcome id="1283" name="11:5"/>
<outcome id="1284" name="11:6"/>
<outcome id="1285" name="11:7"/>
<outcome id="1286" name="11:8"/>
<outcome id="1287" name="11:9"/>
<outcome id="1288" name="11:10"/>
<outcome id="1289" name="11:11"/>
<outcome id="1290" name="11:12"/>
<outcome id="1291" name="11:13"/>
<outcome id="1292" name="11:14"/>
<outcome id="1293" name="11:15"/>
<outcome id="1294" name="11:16"/>
<outcome id="1295" name="11:17"/>
<outcome id="1296" name="11:18"/>
<outcome id="1297" name="11:19"/>
<outcome id="1298" name="12:0"/>
<outcome id="1299" name="12:1"/>
<outcome id="1300" name="12:2"/>
<outcome id="1301" name="12:3"/>
<outcome id="1302" name="12:4"/>
<outcome id="1303" name="12:5"/>
<outcome id="1304" name="12:6"/>
<outcome id="1305" name="12:7"/>
<outcome id="1306" name="12:8"/>
<outcome id="1307" name="12:9"/>
<outcome id="1308" name="12:10"/>
<outcome id="1309" name="12:11"/>
<outcome id="1310" name="12:12"/>
<outcome id="1311" name="12:13"/>
<outcome id="1312" name="12:14"/>
<outcome id="1313" name="12:15"/>
<outcome id="1314" name="12:16"/>
<outcome id="1315" name="12:17"/>
<outcome id="1316" name="12:18"/>
<outcome id="1317" name="12:19"/>
<outcome id="1318" name="13:0"/>
<outcome id="1319" name="13:1"/>
<outcome id="1320" name="13:2"/>
<outcome id="1321" name="13:3"/>
<outcome id="1322" name="13:4"/>
<outcome id="1323" name="13:5"/>
<outcome id="1324" name="13:6"/>
<outcome id="1325" name="13:7"/>
<outcome id="1326" name="13:8"/>
<outcome id="1327" name="13:9"/>
<outcome id="1328" name="13:10"/>
<outcome id="1329" name="13:11"/>
<outcome id="1330" name="13:12"/>
<outcome id="1331" name="13:13"/>
<outcome id="1332" name="13:14"/>
<outcome id="1333" name="13:15"/>
<outcome id="1334" name="13:16"/>
<outcome id="1335" name="13:17"/>
<outcome id="1336" name="13:18"/>
<outcome id="1337" name="13:19"/>
<outcome id="1338" name="14:0"/>
<outcome id="1339" name="14:1"/>
<outcome id="1340" name="14:2"/>
<outcome id="1341" name="14:3"/>
<outcome id="1342" name="14:4"/>
<outcome id="1343" name="14:5"/>
<outcome id="1344" name="14:6"/>
<outcome id="1345" name="14:7"/>
<outcome id="1346" name="14:8"/>
<outcome id="1347" name="14:9"/>
<outcome id="1348" name="14:10"/>
<outcome id="1349" name="14:11"/>
<outcome id="1350" name="14:12"/>
<outcome id="1351" name="14:13"/>
<outcome id="1352" name="14:14"/>
<outcome id="1353" name="14:15"/>
<outcome id="1354" name="14:16"/>
<outcome id="1355" name="14:17"/>
<outcome id="1356" name="14:18"/>
<outcome id="1357" name="14:19"/>
<outcome id="1358" name="15:0"/>
<outcome id="1359" name="15:1"/>
<outcome id="1360" name="15:2"/>
<outcome id="1361" name="15:3"/>
<outcome id="1362" name="15:4"/>
<outcome id="1363" name="15:5"/>
<outcome id="1364" name="15:6"/>
<outcome id="1365" name="15:7"/>
<outcome id="1366" name="15:8"/>
<outcome id="1367" name="15:9"/>
<outcome id="1368" name="15:10"/>
<outcome id="1369" name="15:11"/>
<outcome id="1370" name="15:12"/>
<outcome id="1371" name="15:13"/>
<outcome id="1372" name="15:14"/>
<outcome id="1373" name="15:15"/>
<outcome id="1374" name="15:16"/>
<outcome id="1375" name="15:17"/>
<outcome id="1376" name="15:18"/>
<outcome id="1377" name="15:19"/>
<outcome id="1378" name="16:0"/>
<outcome id="1379" name="16:1"/>
<outcome id="1380" name="16:2"/>
<outcome id="1381" name="16:3"/>
<outcome id="1382" name="16:4"/>
<outcome id="1383" name="16:5"/>
<outcome id="1384" name="16:6"/>
<outcome id="1385" name="16:7"/>
<outcome id="1386" name="16:8"/>
<outcome id="1387" name="16:9"/>
<outcome id="1388" name="16:10"/>
<outcome id="1389" name="16:11"/>
<outcome id="1390" name="16:12"/>
<outcome id="1391" name="16:13"/>
<outcome id="1392" name="16:14"/>
<outcome id="1393" name="16:15"/>
<outcome id="1394" name="16:16"/>
<outcome id="1395" name="16:17"/>
<outcome id="1396" name="16:18"/>
<outcome id="1397" name="16:19"/>
<outcome id="1398" name="17:0"/>
<outcome id="1399" name="17:1"/>
<outcome id="1400" name="17:2"/>
<outcome id="1401" name="17:3"/>
<outcome id="1402" name="17:4"/>
<outcome id="1403" name="17:5"/>
<outcome id="1404" name="17:6"/>
<outcome id="1405" name="17:7"/>
<outcome id="1406" name="17:8"/>
<outcome id="1407" name="17:9"/>
<outcome id="1408" name="17:10"/>
<outcome id="1409" name="17:11"/>
<outcome id="1410" name="17:12"/>
<outcome id="1411" name="17:13"/>
<outcome id="1412" name="17:14"/>
<outcome id="1413" name="17:15"/>
<outcome id="1414" name="17:16"/>
<outcome id="1415" name="17:17"/>
<outcome id="1416" name="17:18"/>
<outcome id="1417" name="17:19"/>
<outcome id="1418" name="18:0"/>
<outcome id="1419" name="18:1"/>
<outcome id="1420" name="18:2"/>
<outcome id="1421" name="18:3"/>
<outcome id="1422" name="18:4"/>
<outcome id="1423" name="18:5"/>
<outcome id="1424" name="18:6"/>
<outcome id="1425" name="18:7"/>
<outcome id="1426" name="18:8"/>
<outcome id="1427" name="18:9"/>
<outcome id="1428" name="18:10"/>
<outcome id="1429" name="18:11"/>
<outcome id="1430" name="18:12"/>
<outcome id="1431" name="18:13"/>
<outcome id="1432" name="18:14"/>
<outcome id="1433" name="18:15"/>
<outcome id="1434" name="18:16"/>
<outcome id="1435" name="18:17"/>
<outcome id="1436" name="18:18"/>
<outcome id="1437" name="18:19"/>
<outcome id="1438" name="19:0"/>
<outcome id="1439" name="19:1"/>
<outcome id="1440" name="19:2"/>
<outcome id="1441" name="19:3"/>
<outcome id="1442" name="19:4"/>
<outcome id="1443" name="19:5"/>
<outcome id="1444" name="19:6"/>
<outcome id="1445" name="19:7"/>
<outcome id="1446" name="19:8"/>
<outcome id="1447" name="19:9"/>
<outcome id="1448" name="19:10"/>
<outcome id="1449" name="19:11"/>
<outcome id="1450" name="19:12"/>
<outcome id="1451" name="19:13"/>
<outcome id="1452" name="19:14"/>
<outcome id="1453" name="19:15"/>
<outcome id="1454" name="19:16"/>
<outcome id="1455" name="19:17"/>
<outcome id="1456" name="19:18"/>
<outcome id="1457" name="19:19"/>
</outcomes>
<specifiers>
<specifier name="score" type="string" description="current score in match"/>
</specifiers>
<attributes>
<attribute name="is_flex_score" description="Outcomes should be adjusted according to score specifier"/>
</attributes>
</market>
<market id="274" name="Innings 1 to 5 - 1x2" groups="all|score|5_innings">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
</market>
<market id="284" name="Innings {from} to {to} - total" groups="all|score|3_innings">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="467" name="{!scorenr} scoring play" groups="all|score|regular_play">
<outcomes>
<outcome id="1640" name="{$competitor1} with try"/>
<outcome id="1641" name="{$competitor1} with penalty"/>
<outcome id="1642" name="{$competitor1} with drop goal"/>
<outcome id="1643" name="{$competitor2} with try"/>
<outcome id="1644" name="{$competitor2} with penalty"/>
<outcome id="1645" name="{$competitor2} with drop goal"/>
</outcomes>
<specifiers>
<specifier name="scorenr" type="integer"/>
</specifiers>
</market>
<market id="178" name="1st half - {$competitor1} total corners" groups="all|1st_half|corners">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="449" name="{!periodnr} period - exact goals" groups="all|score|period">
<specifiers>
<specifier name="periodnr" type="integer"/>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="709" name="{%player} total player performance" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="player" type="string"/>
<specifier name="total" type="decimal"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="294" name="2nd half - draw no bet (incl. overtime)" groups="all|score|combo|incl_ot|2nd_half_incl_ot">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
</market>
<market id="1012" name="{%competitor} total pars" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="competitor" type="string"/>
<specifier name="total" type="decimal"/>
</specifiers>
<attributes>
<attribute name="is_golf_stroke_play_market" description="This market is applicable to Golf stroke play"/>
</attributes>
</market>
<market id="257" name="US Spread (incl. extra innings)" groups="all|score|incl_ei">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="691" name="{!inningnr} innings - {$competitor2} odd/even" groups="all">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="708" name="{!inningnr} innings - {%player} total dismissals" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="player" type="string"/>
<specifier name="total" type="decimal"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="893" name="Last goalscorer" groups="all|regular_play|scorers" includes_outcomes_of_type="sr:player">
<specifiers>
<specifier name="variant" type="variable_text"/>
<specifier name="version" type="string"/>
</specifiers>
</market>
<market id="604" name="{!penaltynr} penalty scored" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="penaltynr" type="integer"/>
</specifiers>
</market>
<market id="620" name="{!mapnr} map - race to {roundnr} rounds" groups="all|score|map">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
<specifier name="roundnr" type="integer"/>
</specifiers>
</market>
<market id="295" name="2nd half - odd/even (incl. overtime)" groups="all|score|combo|incl_ot|2nd_half_incl_ot">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
</market>
<market id="1034" name="Holes {from} to {to} - 3 ball most pars" groups="all" includes_outcomes_of_type="sr:competitor" outcome_type="competitor">
<specifiers>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
<attributes>
<attribute name="is_golf_stroke_play_market" description="This market is applicable to Golf stroke play"/>
</attributes>
</market>
<market id="344" name="{!inningnr} innings - {!dismissalnr} dismissal method (limited)" groups="all|dismissal|innings">
<outcomes>
<outcome id="1037" name="caught"/>
<outcome id="1038" name="not caught"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="dismissalnr" type="integer"/>
</specifiers>
</market>
<market id="630" name="{!mapnr} map round {roundnr} - {$competitor2} total kills" groups="all|kills|round">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
<specifier name="roundnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="75" name="1st half - both teams to score" groups="all|score|1st_half">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="574" name="10 minutes - corner handicap from {from} to {to}" groups="all|10_min|corners">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="hcp" type="decimal"/>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="509" name="{!framenr} frame - break 50+" groups="all|frame|break">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="framenr" type="integer"/>
</specifiers>
</market>
<market id="809" name="{$event} - qualify to play offs" groups="all|cup_group" includes_outcomes_of_type="sr:competitor"/>
<market id="840" name="{!inningnr} innings - {$competitor2} total spread" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
<attributes>
<attribute name="is_spread_market" description="This is a spread-market and special client-side spread rules need to be followed for odds-changes and bet-settlements"/>
</attributes>
</market>
<market id="639" name="Total fours" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="781" name="Batter hits (incl. extra innings)" groups="all|incl_ei|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="859" name="{$competitor2} or under {total}" groups="all|regular_play|combo">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="646" name="Team with highest score at {!dismissalnr} dismissal" groups="all">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="dismissalnr" type="integer"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="6" name="Which team kicks off" groups="all|misc">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
</market>
<market id="894" name="{!setnr} set tiebreak - correct score" groups="all|score|tiebreak">
<outcomes>
<outcome id="1923" name="7:0"/>
<outcome id="1924" name="7:1"/>
<outcome id="1925" name="7:2"/>
<outcome id="1926" name="7:3"/>
<outcome id="1927" name="7:4"/>
<outcome id="1928" name="7:5"/>
<outcome id="1929" name="other {$competitor1} win"/>
<outcome id="1930" name="0:7"/>
<outcome id="1931" name="1:7"/>
<outcome id="1932" name="2:7"/>
<outcome id="1933" name="3:7"/>
<outcome id="1934" name="4:7"/>
<outcome id="1935" name="5:7"/>
<outcome id="1936" name="other {$competitor2} win"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
</specifiers>
</market>
<market id="718" name="{!inningnr} innings - {!dismissalnr} dismissal method (extended)" groups="all">
<outcomes>
<outcome id="1806" name="fielder catch"/>
<outcome id="1807" name="bowled"/>
<outcome id="1808" name="keeper catch"/>
<outcome id="1809" name="lbw"/>
<outcome id="1810" name="run out"/>
<outcome id="1811" name="stumped"/>
<outcome id="1812" name="other"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="dismissalnr" type="integer"/>
</specifiers>
</market>
<market id="139" name="Total bookings" groups="all|regular_play|bookings">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="357" name="{!inningnr} innings over {overnr} - {$competitor2} total" groups="all|score|over">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="overnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="765" name="Player receiving yards (incl. overtime)" groups="all|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="1052" name="Result of {!hitnr} hit" groups="all|rapid_market|hit">
<outcomes>
<outcome id="1982" name="single"/>
<outcome id="1983" name="double"/>
<outcome id="1984" name="triple"/>
<outcome id="1985" name="home run"/>
<outcome id="1986" name="no further hit"/>
</outcomes>
<specifiers>
<specifier name="hitnr" type="integer"/>
</specifiers>
</market>
<market id="247" name="{!gamenr} game - total points" groups="all|score|game">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="gamenr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="1074" name="{!inningnr} inning - {$competitor1} exact home runs" groups="all">
<outcomes>
<outcome id="1996" name="0"/>
<outcome id="1997" name="1"/>
<outcome id="1998" name="2+"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="503" name="{!framenr} frame - race to {pointnr} points" groups="all|score|frame">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="framenr" type="integer"/>
<specifier name="pointnr" type="integer"/>
</specifiers>
</market>
<market id="645" name="{!inningnr} innings over {overnr} - 1x2" groups="all">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="overnr" type="integer"/>
</specifiers>
</market>
<market id="285" name="Innings {from} to {to} - {$competitor1} total" groups="all|score|3_innings">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="48" name="{$competitor1} to win both halves" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="19" name="{$competitor1} total" groups="all|score|regular_play">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="13" name="{$competitor2} no bet" groups="all|score|regular_play">
<outcomes>
<outcome id="780" name="{$competitor1}"/>
<outcome id="782" name="draw"/>
</outcomes>
</market>
<market id="663" name="{!inningnr} innings overs 0 to {overnr} - {$competitor1} total dismissals" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="overnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="758" name="{!quarternr} quarter - winning margin" groups="all|score|quarter">
<specifiers>
<specifier name="quarternr" type="integer"/>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="520" name="{!gamenr} game - {!pointnr} point" groups="all|score|game">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="gamenr" type="integer"/>
<specifier name="pointnr" type="integer"/>
</specifiers>
</market>
<market id="439" name="{$competitor1} to score in all periods" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="241" name="Exact games" groups="all|score|regular_play">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="199" name="Correct score" groups="all|score|regular_play">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="900" name="{$competitor1} {!c1goalnr} goalscorer" groups="all|regular_play|scorers" includes_outcomes_of_type="sr:player">
<outcomes>
<outcome id="1943" name="{$competitor1} no goal"/>
</outcomes>
<specifiers>
<specifier name="c1goalnr" type="integer"/>
</specifiers>
</market>
<market id="603" name="{!goalnr} goal - strike zone" groups="all|misc">
<outcomes>
<outcome id="1797" name="high left"/>
<outcome id="1798" name="low left"/>
<outcome id="1799" name="high center"/>
<outcome id="1800" name="low center"/>
<outcome id="1801" name="high right"/>
<outcome id="1802" name="low right"/>
</outcomes>
<specifiers>
<specifier name="goalnr" type="integer"/>
</specifiers>
</market>
<market id="358" name="1st over - total" groups="all|score|over">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="213" name="{!setnr} set game {gamenr} - odd/even points" groups="all|score|game">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="gamenr" type="integer"/>
</specifiers>
</market>
<market id="810" name="{$event} group {group} - top {pos} exact order" groups="all|cup_group">
<specifiers>
<specifier name="group" type="string"/>
<specifier name="pos" type="integer"/>
</specifiers>
</market>
<market id="877" name="{!inningnr} innings - {$competitor1} total" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="total" type="decimal"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="801" name="{$event} matchday {matchday} - total home team wins" groups="all|matchday">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="matchday" type="integer"/>
</specifiers>
</market>
<market id="537" name="3-ball" groups="all" includes_outcomes_of_type="sr:competitor">
<specifiers>
<specifier name="id" type="string"/>
</specifiers>
</market>
<market id="1065" name="{!drivenr} drive play {playnr} - {%competitor} sack" groups="all|drive">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="drivenr" type="integer"/>
<specifier name="playnr" type="integer"/>
<specifier name="competitor" type="string"/>
</specifiers>
</market>
<market id="763" name="Player rushing touchdowns (incl. overtime)" groups="all|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="1149" name="{!overnr} over - {$competitor2} total ({ballcount}-ball overs)" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="overnr" type="integer"/>
<specifier name="ballcount" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="1125" name="{!inningnr} innings - {%player1} to score {milestone} &amp; {%player2} over {total} dismissals" groups="all">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="player1" type="string"/>
<specifier name="player2" type="string"/>
<specifier name="milestone" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="1062" name="{!drivenr} drive play {playnr} - {%competitor} pass completion" groups="all|drive">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="drivenr" type="integer"/>
<specifier name="playnr" type="integer"/>
<specifier name="competitor" type="string"/>
</specifiers>
</market>
<market id="866" name="Win" groups="all" includes_outcomes_of_type="sr:competitor"/>
<market id="812" name="{$event} - to reach the final" groups="all|cup_ko" includes_outcomes_of_type="sr:competitor"/>
<market id="965" name="{$competitor1} to lead by {points}" groups="all">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="points" type="integer"/>
</specifiers>
</market>
<market id="879" name="{$competitor2} to win" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="393" name="{!setnr} set leg {legnr} - {$competitor2} to score a 180" groups="all|leg|180s">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="legnr" type="integer"/>
</specifiers>
</market>
<market id="848" name="{$event} - top {pos} exact order" groups="all|cup_group">
<specifiers>
<specifier name="pos" type="integer"/>
</specifiers>
</market>
<market id="923" name="{%player} total rebounds (incl. overtime)" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="player" type="string"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="737" name="Maximum consecutive runs by either team" groups="all|score|regular_play">
<outcomes>
<outcome id="961" name="0"/>
<outcome id="962" name="1"/>
<outcome id="963" name="2"/>
<outcome id="964" name="3"/>
<outcome id="965" name="4"/>
<outcome id="966" name="5+"/>
</outcomes>
</market>
<market id="391" name="{!setnr} set leg {legnr} - any player to score a 180" groups="all|leg|180s">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="legnr" type="integer"/>
</specifiers>
</market>
<market id="766" name="Player receiving touchdowns (incl. overtime)" groups="all|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="837" name="{$competitor1}/{$competitor2} supremacy" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
<specifiers>
<specifier name="maxovers" type="integer"/>
</specifiers>
<attributes>
<attribute name="is_spread_market" description="This is a spread-market and special client-side spread rules need to be followed for odds-changes and bet-settlements"/>
</attributes>
</market>
<market id="1163" name="{%competitor} total holes won" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="competitor" type="string"/>
<specifier name="total" type="decimal"/>
</specifiers>
<attributes>
<attribute name="is_golf_match_play_market" description="This market is applicable to Golf match play"/>
</attributes>
</market>
<market id="838" name="{$competitor2}/{$competitor1} supremacy" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
<specifiers>
<specifier name="maxovers" type="integer"/>
</specifiers>
<attributes>
<attribute name="is_spread_market" description="This is a spread-market and special client-side spread rules need to be followed for odds-changes and bet-settlements"/>
</attributes>
</market>
<market id="179" name="1st half - {$competitor2} total corners" groups="all|1st_half|corners">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="1035" name="Holes {from} to {to} - 3 ball most bogeys" groups="all" includes_outcomes_of_type="sr:competitor" outcome_type="competitor">
<specifiers>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
<attributes>
<attribute name="is_golf_stroke_play_market" description="This market is applicable to Golf stroke play"/>
</attributes>
</market>
<market id="973" name="{$competitor2} 10/3 sixes head2head spread" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
</market>
<market id="927" name="{%player} total batter runs + runs batted in (incl. extra innings)" groups="all|incl_ei">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="player" type="string"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="849" name="Any team winning margin (incl. overtime)" groups="all|score|incl_ot">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="136" name="Booking 1x2" groups="all|regular_play|bookings">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
</market>
<market id="962" name="{$competitor1} total maximum consecutive points" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="1148" name="{!overnr} over - {$competitor1} total ({ballcount}-ball overs)" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="overnr" type="integer"/>
<specifier name="ballcount" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="171" name="{$competitor2} corner range" groups="all|regular_play|corners">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="1177" name="1st half 1x2 &amp; 1st half total" groups="all|combo|incl_ot">
<outcomes>
<outcome id="794" name="{$competitor1} &amp; under {total}"/>
<outcome id="796" name="{$competitor1} &amp; over {total}"/>
<outcome id="798" name="draw &amp; under {total}"/>
<outcome id="800" name="draw &amp; over {total}"/>
<outcome id="802" name="{$competitor2} &amp; under {total}"/>
<outcome id="804" name="{$competitor2} &amp; over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="1032" name="Holes {from} to {to} - 2 ball (1x2) most bogeys" groups="all">
<outcomes>
<outcome id="1966" name="{%competitor1}"/>
<outcome id="1967" name="draw"/>
<outcome id="1968" name="{%competitor2}"/>
</outcomes>
<specifiers>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
<specifier name="competitor1" type="string"/>
<specifier name="competitor2" type="string"/>
</specifiers>
<attributes>
<attribute name="is_golf_stroke_play_market" description="This market is applicable to Golf stroke play"/>
</attributes>
</market>
<market id="514" name="{!framenr} frame - {!xth} colour potted" groups="all|score|frame">
<outcomes>
<outcome id="1672" name="yellow"/>
<outcome id="1673" name="green"/>
<outcome id="1674" name="brown"/>
<outcome id="1675" name="blue"/>
<outcome id="1676" name="pink"/>
<outcome id="1677" name="black"/>
</outcomes>
<specifiers>
<specifier name="framenr" type="integer"/>
<specifier name="xth" type="integer"/>
</specifiers>
</market>
<market id="547" name="Double chance &amp; total" groups="all|regular_play|combo">
<outcomes>
<outcome id="1724" name="{$competitor1}/draw &amp; under {total}"/>
<outcome id="1725" name="{$competitor1}/{$competitor2} &amp; under {total}"/>
<outcome id="1726" name="draw/{$competitor2} &amp; under {total}"/>
<outcome id="1727" name="{$competitor1}/draw &amp; over {total}"/>
<outcome id="1728" name="{$competitor1}/{$competitor2} &amp; over {total}"/>
<outcome id="1729" name="draw/{$competitor2} &amp; over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="384" name="Total 180s" groups="all|regular_play|180s">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="1029" name="Holes {from} to {to} - 2 ball (handicap)" groups="all">
<outcomes>
<outcome id="1969" name="{%competitor1} ({+hcp})"/>
<outcome id="1970" name="{%competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
<specifier name="hcp" type="decimal"/>
<specifier name="competitor1" type="string"/>
<specifier name="competitor2" type="string"/>
</specifiers>
<attributes>
<attribute name="is_golf_stroke_play_market" description="This market is applicable to Golf stroke play"/>
</attributes>
</market>
<market id="1020" name="Total 2+ over par" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
<attributes>
<attribute name="is_golf_stroke_play_market" description="This market is applicable to Golf stroke play"/>
</attributes>
</market>
<market id="546" name="Double chance &amp; both teams to score" groups="all|regular_play|combo">
<outcomes>
<outcome id="1718" name="{$competitor1}/draw &amp; yes"/>
<outcome id="1719" name="{$competitor1}/draw &amp; no"/>
<outcome id="1720" name="{$competitor1}/{$competitor2} &amp; yes"/>
<outcome id="1721" name="{$competitor1}/{$competitor2} &amp; no"/>
<outcome id="1722" name="draw/{$competitor2} &amp; yes"/>
<outcome id="1723" name="draw/{$competitor2} &amp; no"/>
</outcomes>
</market>
<market id="1097" name="Grid position range of the winner" groups="all">
<outcomes>
<outcome id="2017" name="1-2"/>
<outcome id="2018" name="3-4"/>
<outcome id="2019" name="5-6"/>
<outcome id="2020" name="7-8"/>
<outcome id="2021" name="9-10"/>
<outcome id="2022" name="11+"/>
</outcomes>
</market>
<market id="405" name="Race to {goalnr} goals (incl. overtime)" groups="all|score|incl_ot">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="goalnr" type="integer"/>
</specifiers>
</market>
<market id="143" name="{$competitor1} exact bookings" groups="all|regular_play|bookings">
<outcomes>
<outcome id="730" name="0-1"/>
<outcome id="732" name="2"/>
<outcome id="734" name="3"/>
<outcome id="736" name="4+"/>
</outcomes>
</market>
<market id="20" name="{$competitor2} total" groups="all|score|regular_play">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="660" name="{!inningnr} innings - {$competitor1} total sixes" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="total" type="decimal"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="802" name="{$event} matchday {matchday} - total draws" groups="all|matchday">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="matchday" type="integer"/>
</specifiers>
</market>
<market id="267" name="Race to {runnr} runs (incl. extra innings)" groups="all|score|incl_ei">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="runnr" type="integer"/>
</specifiers>
</market>
<market id="41" name="Correct score [{score}]" groups="all|score|regular_play">
<outcomes>
<outcome id="110" name="0:0"/>
<outcome id="114" name="1:0"/>
<outcome id="116" name="2:0"/>
<outcome id="118" name="3:0"/>
<outcome id="120" name="4:0"/>
<outcome id="122" name="5:0"/>
<outcome id="124" name="6:0"/>
<outcome id="126" name="0:1"/>
<outcome id="128" name="1:1"/>
<outcome id="130" name="2:1"/>
<outcome id="132" name="3:1"/>
<outcome id="134" name="4:1"/>
<outcome id="136" name="5:1"/>
<outcome id="138" name="0:2"/>
<outcome id="140" name="1:2"/>
<outcome id="142" name="2:2"/>
<outcome id="144" name="3:2"/>
<outcome id="146" name="4:2"/>
<outcome id="148" name="0:3"/>
<outcome id="150" name="1:3"/>
<outcome id="152" name="2:3"/>
<outcome id="154" name="3:3"/>
<outcome id="156" name="0:4"/>
<outcome id="158" name="1:4"/>
<outcome id="160" name="2:4"/>
<outcome id="162" name="0:5"/>
<outcome id="164" name="1:5"/>
<outcome id="166" name="0:6"/>
</outcomes>
<specifiers>
<specifier name="score" type="string" description="current score in match"/>
</specifiers>
<attributes>
<attribute name="is_flex_score" description="Outcomes should be adjusted according to score specifier"/>
</attributes>
</market>
<market id="1162" name="Hole handicap" groups="all">
<outcomes>
<outcome id="1969" name="{%competitor1} ({+hcp})"/>
<outcome id="1970" name="{%competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="competitor1" type="string"/>
<specifier name="competitor2" type="string"/>
<specifier name="hcp" type="decimal"/>
</specifiers>
<attributes>
<attribute name="is_golf_match_play_market" description="This market is applicable to Golf match play"/>
</attributes>
</market>
<market id="420" name="Which team to score (incl. overtime and penalties)" groups="all|score|incl_ot_and_pen">
<outcomes>
<outcome id="1475" name="both"/>
<outcome id="1476" name="only {$competitor1}"/>
<outcome id="1477" name="only {$competitor2}"/>
</outcomes>
</market>
<market id="117" name="Overtime - handicap" groups="all|score|ot">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="773" name="Player steals (incl. overtime)" groups="all|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="446" name="{!periodnr} period - total" groups="all|score|period">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="periodnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="413" name="US total (incl. overtime and penalties)" groups="all|score|incl_ot_and_pen">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="680" name="Most extras" groups="all">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="652" name="{!inningnr} innings over {overnr} - {$competitor1} boundary" groups="all">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="overnr" type="integer"/>
</specifiers>
</market>
<market id="748" name="{!inningnr} inning - {$competitor2} total" groups="all|score|inning">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="734" name="{!mapnr} map 10 minutes - total kills from {from} to {to}" groups="all|10_min|kills">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="1019" name="Total bogeys" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
<attributes>
<attribute name="is_golf_stroke_play_market" description="This market is applicable to Golf stroke play"/>
</attributes>
</market>
<market id="1139" name="{!inningnr} innings over {overnr} - {!deliverynr} delivery {$competitor2} to be a wicket" groups="all">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="overnr" type="integer"/>
<specifier name="deliverynr" type="integer"/>
</specifiers>
</market>
<market id="728" name="{!mapnr} map - total towers" groups="all|map|structures">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="398" name="{!mapnr} map - {!xth} barracks" groups="all|map|structures">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
<specifier name="xth" type="integer"/>
</specifiers>
</market>
<market id="40" name="Anytime goalscorer" groups="all|regular_play|scorers" includes_outcomes_of_type="sr:player">
<outcomes>
<outcome id="1716" name="no goal"/>
</outcomes>
<specifiers>
<specifier name="type" type="string"/>
</specifiers>
</market>
<market id="219" name="Winner (incl. overtime)" groups="all|score|incl_ot">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
</market>
<market id="255" name="Winning margin (incl. extra innings)" groups="all|score|incl_ei">
<outcomes>
<outcome id="630" name="{$competitor1} by 1"/>
<outcome id="632" name="{$competitor1} by 2"/>
<outcome id="634" name="{$competitor1} by 3+"/>
<outcome id="636" name="{$competitor2} by 1"/>
<outcome id="638" name="{$competitor2} by 2"/>
<outcome id="640" name="{$competitor2} by 3+"/>
</outcomes>
</market>
<market id="392" name="{!setnr} set leg {legnr} - {$competitor1} to score a 180" groups="all|leg|180s">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="legnr" type="integer"/>
</specifiers>
</market>
<market id="899" name="Player to score 3+" groups="all|regular_play|scorers" includes_outcomes_of_type="sr:player"/>
<market id="495" name="Odd/even frames" groups="all|score|regular_play">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
</market>
<market id="331" name="{!mapnr} map - round handicap (incl. overtime)" groups="all|score|map_incl_ot">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="377" name="{!setnr} set leg {legnr} - highest scoring player on {!visitnr} visits" groups="all|score|visit">
<outcomes>
<outcome id="1041" name="{$competitor1}"/>
<outcome id="1042" name="{$competitor2}"/>
<outcome id="1043" name="draw"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="legnr" type="integer"/>
<specifier name="visitnr" type="integer"/>
</specifiers>
</market>
<market id="1169" name="Hole {holenr} - draw no bet" groups="all">
<outcomes>
<outcome id="2033" name="{%competitor1}"/>
<outcome id="2034" name="{%competitor2}"/>
</outcomes>
<specifiers>
<specifier name="holenr" type="integer"/>
<specifier name="competitor1" type="string"/>
<specifier name="competitor2" type="string"/>
</specifiers>
<attributes>
<attribute name="is_golf_match_play_market" description="This market is applicable to Golf match play"/>
</attributes>
</market>
<market id="313" name="{!setnr} set - race to {pointnr} points" groups="all|score|set">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="pointnr" type="integer"/>
</specifiers>
</market>
<market id="830" name="Bonus ball range" groups="all">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="49" name="{$competitor2} to win both halves" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="123" name="Penalty shootout - winner" groups="all|score|pen_so">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
</market>
<market id="55" name="1st/2nd half both teams to score" groups="all|score|regular_play">
<outcomes>
<outcome id="806" name="no/no"/>
<outcome id="808" name="yes/no"/>
<outcome id="810" name="yes/yes"/>
<outcome id="812" name="no/yes"/>
</outcomes>
</market>
<market id="865" name="{$competitor2} or any clean sheet" groups="all|regular_play|combo">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="618" name="1st half - {$competitor1} odd/even" groups="all|score|1st_half">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
</market>
<market id="97" name="2nd half - {$competitor2} clean sheet" groups="all|score|2nd_half">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="662" name="{!inningnr} innings - {%player} to score {milestone}" groups="all">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="player" type="string"/>
<specifier name="milestone" type="integer"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="934" name="Total sacks (incl. overtime)" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="739" name="When will the {!runnr} run be scored (incl. extra innings)" groups="all|score|incl_ei">
<outcomes>
<outcome id="1826" name="{!inningnr} inning"/>
<outcome id="1828" name="{!(inningnr+1)} inning"/>
<outcome id="1829" name="{!(inningnr+2)} inning"/>
<outcome id="1830" name="other inning or no run"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="runnr" type="integer"/>
</specifiers>
</market>
<market id="248" name="{!gamenr} game - odd/even" groups="all|score|game">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
<specifiers>
<specifier name="gamenr" type="integer"/>
</specifiers>
</market>
<market id="103" name="15 minutes - {!goalnr} goal from {from} to {to}" groups="all|score|15_min">
<outcomes>
<outcome id="6" name="{$competitor1}"/>
<outcome id="7" name="none"/>
<outcome id="8" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="goalnr" type="integer"/>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="994" name="{!inningnr} innings {!overnr} over - total runs spread" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
<specifiers>
<specifier name="overnr" type="integer"/>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="1086" name="Total speed of {!pitchnr} pitch" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="pitchnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="74" name="1st half - odd/even" groups="all|score|1st_half">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
</market>
<market id="846" name="{!inningnr} innings over {overnr} - {$competitor1} total spread" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="overnr" type="integer"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
<attributes>
<attribute name="is_spread_market" description="This is a spread-market and special client-side spread rules need to be followed for odds-changes and bet-settlements"/>
</attributes>
</market>
<market id="619" name="1st half - {$competitor2} odd/even" groups="all|score|1st_half">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
</market>
<market id="776" name="Player shots (incl. overtime)" groups="all|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="684" name="Top bowler" groups="all" includes_outcomes_of_type="sr:player">
<specifiers>
<specifier name="maxovers" type="integer"/>
<specifier name="type" type="string"/>
</specifiers>
</market>
<market id="18" name="Total" groups="all|score|regular_play">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="889" name="Anytime goalscorer &amp; correct score" groups="all|regular_play|combo">
<outcomes>
<outcome id="1898" name="{%player} &amp; 1:0"/>
<outcome id="1899" name="{%player} &amp; 2:0"/>
<outcome id="1900" name="{%player} &amp; 3:0"/>
<outcome id="1901" name="{%player} &amp; 4:0"/>
<outcome id="1902" name="{%player} &amp; 2:1"/>
<outcome id="1903" name="{%player} &amp; 3:1"/>
<outcome id="1904" name="{%player} &amp; 4:1"/>
<outcome id="1905" name="{%player} &amp; 3:2"/>
<outcome id="1906" name="{%player} &amp; 4:2"/>
<outcome id="1907" name="{%player} &amp; 4:3"/>
<outcome id="1908" name="{%player} &amp; 0:1"/>
<outcome id="1909" name="{%player} &amp; 0:2"/>
<outcome id="1910" name="{%player} &amp; 0:3"/>
<outcome id="1911" name="{%player} &amp; 0:4"/>
<outcome id="1912" name="{%player} &amp; 1:2"/>
<outcome id="1913" name="{%player} &amp; 1:3"/>
<outcome id="1914" name="{%player} &amp; 1:4"/>
<outcome id="1915" name="{%player} &amp; 2:3"/>
<outcome id="1916" name="{%player} &amp; 2:4"/>
<outcome id="1917" name="{%player} &amp; 3:4"/>
<outcome id="1918" name="{%player} &amp; 1:1"/>
<outcome id="1919" name="{%player} &amp; 2:2"/>
<outcome id="1920" name="{%player} &amp; 3:3"/>
<outcome id="1921" name="{%player} &amp; 4:4"/>
<outcome id="1922" name="other"/>
</outcomes>
<specifiers>
<specifier name="player" type="string"/>
</specifiers>
</market>
<market id="1058" name="{!scorenr} scoring type" groups="all|score|regular_play">
<specifiers>
<specifier name="scorenr" type="integer"/>
<specifier name="variant" type="string"/>
</specifiers>
</market>
<market id="225" name="Total (incl. overtime)" groups="all|score|incl_ot">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="1067" name="Winner &amp; total (incl. extra innings)" groups="all|incl_ei">
<outcomes>
<outcome id="973" name="{$competitor1} &amp; over {total}"/>
<outcome id="974" name="{$competitor2} &amp; over {total}"/>
<outcome id="975" name="{$competitor1} &amp; under {total}"/>
<outcome id="976" name="{$competitor2} &amp; under {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="1053" name="Result of {%player} {!appearancenr} time at bat" groups="all|player|rapid_market">
<outcomes>
<outcome id="1987" name="on base"/>
<outcome id="1988" name="out"/>
<outcome id="1989" name="home run"/>
</outcomes>
<specifiers>
<specifier name="appearancenr" type="integer"/>
<specifier name="player" type="string"/>
</specifiers>
</market>
<market id="501" name="{!framenr} frame - total points" groups="all|score|frame">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="framenr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="269" name="{$competitor1} to bat in 9th inning" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="804" name="{$event} - winner" groups="all|league|cup" includes_outcomes_of_type="sr:competitor"/>
<market id="527" name="{!setnr} set - handicap" groups="all|score|set">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="390" name="{!setnr} set - {$competitor2} total 180s" groups="all|set|180s">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="832" name="Standard bet" groups="all">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="831" name="Draw sum total (incl. bonus ball)" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="1109" name="{!lapnr} lap - fastest lap" groups="all" includes_outcomes_of_type="sr:competitor" outcome_type="competitor">
<specifiers>
<specifier name="lapnr" type="integer" description="lap number"/>
</specifiers>
</market>
<market id="126" name="Penalty shootout - winning margin" groups="all|score|pen_so">
<outcomes>
<outcome id="630" name="{$competitor1} by 1"/>
<outcome id="632" name="{$competitor1} by 2"/>
<outcome id="634" name="{$competitor1} by 3+"/>
<outcome id="636" name="{$competitor2} by 1"/>
<outcome id="638" name="{$competitor2} by 2"/>
<outcome id="640" name="{$competitor2} by 3+"/>
</outcomes>
</market>
<market id="650" name="{!inningnr} innings - {$competitor2} total dismissals" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="total" type="decimal"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="651" name="{!inningnr} innings - {%player} dismissal method" groups="all">
<outcomes>
<outcome id="1806" name="fielder catch"/>
<outcome id="1807" name="bowled"/>
<outcome id="1808" name="keeper catch"/>
<outcome id="1809" name="lbw"/>
<outcome id="1810" name="run out"/>
<outcome id="1811" name="stumped"/>
<outcome id="1812" name="other"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="player" type="string"/>
</specifiers>
</market>
<market id="1072" name="{!inningnr} inning - most hits" groups="all">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="108" name="5 minutes - 1x2 from {from} to {to}" groups="all|score|5_min">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="343" name="{!inningnr} innings - {!dismissalnr} dismissal method" groups="all|dismissal|innings">
<outcomes>
<outcome id="1031" name="caught"/>
<outcome id="1032" name="bowled"/>
<outcome id="1033" name="lbw"/>
<outcome id="1034" name="run out"/>
<outcome id="1035" name="stumped"/>
<outcome id="1036" name="others"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="dismissalnr" type="integer"/>
</specifiers>
</market>
<market id="526" name="Will there be a 5th set" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="88" name="2nd half - handicap" groups="all|score|2nd_half">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="226" name="US total (incl. overtime)" groups="all|score|incl_ot">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="681" name="Most run outs" groups="all">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="707" name="{!inningnr} innings - {$competitor2} total ducks" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="total" type="decimal"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="1054" name="{%player} to strike out {!appearancenr} time at bat" groups="all|player|rapid_market">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="appearancenr" type="integer"/>
<specifier name="player" type="string"/>
</specifiers>
</market>
<market id="789" name="Player kicks" groups="all|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="1050" name="Hit on {!pitchnr} pitch" groups="all|rapid_market|pitch">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="pitchnr" type="integer"/>
</specifiers>
</market>
<market id="1036" name="{!quarternr} quarter - handicap {hcp}" groups="all">
<outcomes>
<outcome id="1711" name="{$competitor1} ({hcp})"/>
<outcome id="1712" name="draw ({hcp})"/>
<outcome id="1713" name="{$competitor2} ({hcp})"/>
</outcomes>
<specifiers>
<specifier name="quarternr" type="integer"/>
<specifier name="hcp" type="string"/>
</specifiers>
</market>
<market id="474" name="Try double chance" groups="all|regular_play|tries">
<outcomes>
<outcome id="9" name="{$competitor1} or draw"/>
<outcome id="10" name="{$competitor1} or {$competitor2}"/>
<outcome id="11" name="draw or {$competitor2}"/>
</outcomes>
</market>
<market id="1001" name="{!inningnr} innings overs {overnrX} x {overnrY} - {$competitor2} multi run spread" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="overnrX" type="integer"/>
<specifier name="overnrY" type="integer"/>
</specifiers>
</market>
<market id="163" name="{!cornernr} corner" groups="all|regular_play|corners">
<outcomes>
<outcome id="6" name="{$competitor1}"/>
<outcome id="7" name="none"/>
<outcome id="8" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="cornernr" type="integer"/>
</specifiers>
</market>
<market id="713" name="Most catches" groups="all">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="365" name="Set handicap {hcp}" groups="all|score|regular_play">
<outcomes>
<outcome id="1711" name="{$competitor1} ({hcp})"/>
<outcome id="1712" name="draw ({hcp})"/>
<outcome id="1713" name="{$competitor2} ({hcp})"/>
</outcomes>
<specifiers>
<specifier name="hcp" type="string"/>
</specifiers>
</market>
<market id="1010" name="{%competitor} total eagles" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="competitor" type="string"/>
<specifier name="total" type="decimal"/>
</specifiers>
<attributes>
<attribute name="is_golf_stroke_play_market" description="This market is applicable to Golf stroke play"/>
</attributes>
</market>
<market id="300" name="Race to {pointnr} points" groups="all|score|regular_play">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="pointnr" type="integer"/>
</specifiers>
</market>
<market id="119" name="Overtime 1st half - 1x2" groups="all|score|ot_1st_half">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
</market>
<market id="70" name="1st half - {$competitor2} total" groups="all|score|1st_half">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="577" name="10 minutes - {$competitor2} total corners from {from} to {to}" groups="all|10_min|corners">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="370" name="{!setnr} set - which player wins the rest" groups="all|score|set">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="score" type="string"/>
</specifiers>
</market>
<market id="373" name="{!setnr} set - odd/even legs" groups="all|score|set">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
</specifiers>
</market>
<market id="1130" name="{!inningnr} innings - {%player1} + {%player2} + {%player3} total" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="player1" type="string"/>
<specifier name="player2" type="string"/>
<specifier name="player3" type="string"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="905" name="1st half - next score" groups="all|score|1st_half">
<outcomes>
<outcome id="6" name="{$competitor1}"/>
<outcome id="7" name="none"/>
<outcome id="8" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="score" type="string" description="current score in match"/>
</specifiers>
</market>
<market id="803" name="{$event} matchday {matchday} - total away team wins" groups="all|matchday">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="matchday" type="integer"/>
</specifiers>
</market>
<market id="761" name="Player passing touchdowns (incl. overtime)" groups="all|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="1037" name="Race to {goals} goals" groups="all">
<outcomes>
<outcome id="6" name="{$competitor1}"/>
<outcome id="7" name="none"/>
<outcome id="8" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="goals" type="integer"/>
</specifiers>
</market>
<market id="634" name="Team with highest scoring half" groups="all|score|regular_play">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
</market>
<market id="1081" name="{!inningnr} inning - {$competitor1} to record a double or triple play" groups="all">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="917" name="{%player} total carries (incl. overtime)" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="player" type="string"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="90" name="2nd half - total" groups="all|score|2nd_half">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="1151" name="Overs 0 to {overnr} - {$competitor2} total ({ballcount}-ball overs)" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="overnr" type="integer"/>
<specifier name="ballcount" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="741" name="Both teams over {total} (incl. extra innings)" groups="all|score|incl_ei">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="246" name="{!gamenr} game - point handicap" groups="all|score|game">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="gamenr" type="integer"/>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="235" name="{!quarternr} quarter - 1x2" groups="all|score|quarter">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="quarternr" type="integer"/>
</specifiers>
</market>
<market id="440" name="{$competitor2} to score in all periods" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="433" name="{$competitor1} highest scoring period" groups="all|score|regular_play">
<outcomes>
<outcome id="1621" name="1st period"/>
<outcome id="1622" name="2nd period"/>
<outcome id="1623" name="3rd period"/>
<outcome id="1624" name="equal"/>
</outcomes>
</market>
<market id="839" name="{!inningnr} innings - {$competitor1} total spread" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
<attributes>
<attribute name="is_spread_market" description="This is a spread-market and special client-side spread rules need to be followed for odds-changes and bet-settlements"/>
</attributes>
</market>
<market id="755" name="{!quarternr} quarter - point range" groups="all|score|quarter">
<specifiers>
<specifier name="quarternr" type="integer"/>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="53" name="{$competitor1} highest scoring half" groups="all|score|regular_play">
<outcomes>
<outcome id="436" name="1st half"/>
<outcome id="438" name="2nd half"/>
<outcome id="440" name="equal"/>
</outcomes>
</market>
<market id="151" name="1st half - total booking points" groups="all|1st_half|bookings">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="911" name="Will the fight go the distance" groups="all">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="190" name="{$competitor1} total games" groups="all|score|regular_play">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="301" name="{!quarternr} quarter - winning margin" groups="all|score|quarter">
<outcomes>
<outcome id="1002" name="{$competitor1} by 3+"/>
<outcome id="1003" name="{$competitor2} by 3+"/>
<outcome id="1004" name="other"/>
</outcomes>
<specifiers>
<specifier name="quarternr" type="integer"/>
</specifiers>
</market>
<market id="1160" name="Draw no bet" groups="all">
<outcomes>
<outcome id="2033" name="{%competitor1}"/>
<outcome id="2034" name="{%competitor2}"/>
</outcomes>
<specifiers>
<specifier name="competitor1" type="string"/>
<specifier name="competitor2" type="string"/>
</specifiers>
<attributes>
<attribute name="is_golf_match_play_market" description="This market is applicable to Golf match play"/>
</attributes>
</market>
<market id="992" name="Total wides spread" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
</market>
<market id="749" name="{!inningnr} inning - {$competitor1} to score" groups="all|score|inning">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="886" name="1 minute - total offsides from {from} to {to}" groups="all|rapid_market|offsides">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="from" type="string"/>
<specifier name="to" type="string"/>
</specifiers>
</market>
<market id="335" name="{!mapnr} map - will there be overtime" groups="all|score|map">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
</specifiers>
</market>
<market id="325" name="{!setnr} set end {endnr} - odd/even" groups="all|score|end">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="endnr" type="integer"/>
</specifiers>
</market>
<market id="1070" name="{!inningnr} inning - most strikes" groups="all">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="633" name="{!mapnr} map round {roundnr} - player with {!killnr} kill" groups="all|round|player" includes_outcomes_of_type="sr:player">
<specifiers>
<specifier name="mapnr" type="integer"/>
<specifier name="roundnr" type="integer"/>
<specifier name="killnr" type="integer"/>
</specifiers>
</market>
<market id="643" name="{!inningnr} innings - {%player} total fours" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="player" type="string"/>
<specifier name="total" type="decimal"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="898" name="Player to score 2+" groups="all|regular_play|scorers" includes_outcomes_of_type="sr:player"/>
<market id="432" name="Highest scoring period" groups="all|score|regular_play">
<outcomes>
<outcome id="1621" name="1st period"/>
<outcome id="1622" name="2nd period"/>
<outcome id="1623" name="3rd period"/>
<outcome id="1624" name="equal"/>
</outcomes>
</market>
<market id="916" name="{%player} total passing touchdowns (incl. overtime)" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="player" type="string"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="1064" name="{!drivenr} drive play {playnr} - {%competitor} new first down" groups="all|drive">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="drivenr" type="integer"/>
<specifier name="playnr" type="integer"/>
<specifier name="competitor" type="string"/>
</specifiers>
</market>
<market id="1137" name="{!inningnr} innings over {overnr} - {!deliverynr} delivery {$competitor2} exact runs" groups="all">
<outcomes>
<outcome id="2026" name="0"/>
<outcome id="2027" name="1"/>
<outcome id="2028" name="2"/>
<outcome id="2029" name="3"/>
<outcome id="2030" name="4"/>
<outcome id="2031" name="6"/>
<outcome id="2032" name="other"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="overnr" type="integer"/>
<specifier name="deliverynr" type="integer"/>
</specifiers>
</market>
<market id="324" name="{!setnr} set end {endnr} - exact points" groups="all|score|end">
<outcomes>
<outcome id="1005" name="0"/>
<outcome id="1006" name="1"/>
<outcome id="1007" name="2"/>
<outcome id="1008" name="3"/>
<outcome id="1009" name="4"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="endnr" type="integer"/>
</specifiers>
</market>
<market id="162" name="Corner 1x2" groups="all|regular_play|corners">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
</market>
<market id="599" name="5 minutes - {$competitor1} sending off from {from} to {to}" groups="all|5_min|bookings">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="1047" name="Innings 1 to 5 - {$competitor1} total hits" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="318" name="{!setnr} set - {$competitor1} total" groups="all|score|set">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="426" name="{!periodnr} period 1x2 &amp; winner (incl. overtime and penalties)" groups="all|combo|incl_ot_and_pen">
<outcomes>
<outcome id="1482" name="{$competitor1} &amp; {$competitor1}"/>
<outcome id="1483" name="draw &amp; {$competitor1}"/>
<outcome id="1484" name="{$competitor2} &amp; {$competitor1}"/>
<outcome id="1485" name="{$competitor1} &amp; {$competitor2}"/>
<outcome id="1486" name="draw &amp; {$competitor2}"/>
<outcome id="1487" name="{$competitor2} &amp; {$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="periodnr" type="integer"/>
</specifiers>
</market>
<market id="170" name="{$competitor1} corner range" groups="all|regular_play|corners">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="606" name="{!inningnr} innings - {$competitor1} total" groups="all|score|innings">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="685" name="Player of the match" groups="all" includes_outcomes_of_type="sr:player">
<specifiers>
<specifier name="type" type="string"/>
</specifiers>
</market>
<market id="46" name="Halftime/fulltime correct score" groups="all|score|regular_play">
<outcomes>
<outcome id="326" name="0:0 0:0"/>
<outcome id="328" name="0:0 0:1"/>
<outcome id="330" name="0:0 0:2"/>
<outcome id="332" name="0:0 0:3"/>
<outcome id="334" name="0:0 1:0"/>
<outcome id="336" name="0:0 1:1"/>
<outcome id="338" name="0:0 1:2"/>
<outcome id="340" name="0:0 2:0"/>
<outcome id="342" name="0:0 2:1"/>
<outcome id="344" name="0:0 3:0"/>
<outcome id="346" name="0:0 4+"/>
<outcome id="348" name="0:1 0:1"/>
<outcome id="350" name="0:1 0:2"/>
<outcome id="352" name="0:1 0:3"/>
<outcome id="354" name="0:1 1:1"/>
<outcome id="356" name="0:1 1:2"/>
<outcome id="358" name="0:1 2:1"/>
<outcome id="360" name="0:1 4+"/>
<outcome id="362" name="0:2 0:2"/>
<outcome id="364" name="0:2 0:3"/>
<outcome id="366" name="0:2 1:2"/>
<outcome id="368" name="0:2 4+"/>
<outcome id="370" name="0:3 0:3"/>
<outcome id="372" name="0:3 4+"/>
<outcome id="374" name="1:0 1:0"/>
<outcome id="376" name="1:0 1:1"/>
<outcome id="378" name="1:0 1:2"/>
<outcome id="380" name="1:0 2:0"/>
<outcome id="382" name="1:0 2:1"/>
<outcome id="384" name="1:0 3:0"/>
<outcome id="386" name="1:0 4+"/>
<outcome id="388" name="1:1 1:1"/>
<outcome id="390" name="1:1 1:2"/>
<outcome id="392" name="1:1 2:1"/>
<outcome id="394" name="1:1 4+"/>
<outcome id="396" name="1:2 1:2"/>
<outcome id="398" name="1:2 4+"/>
<outcome id="400" name="2:0 2:0"/>
<outcome id="402" name="2:0 2:1"/>
<outcome id="404" name="2:0 3:0"/>
<outcome id="406" name="2:0 4+"/>
<outcome id="408" name="2:1 2:1"/>
<outcome id="410" name="2:1 4+"/>
<outcome id="412" name="3:0 3:0"/>
<outcome id="414" name="3:0 4+"/>
<outcome id="416" name="4+ 4+"/>
</outcomes>
</market>
<market id="861" name="Draw or both teams to score" groups="all|regular_play|combo">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="1152" name="{%player} total rushing touchdowns (incl. overtime)" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="player" type="string"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="463" name="Overtime - double chance" groups="all|score|ot">
<outcomes>
<outcome id="9" name="{$competitor1} or draw"/>
<outcome id="10" name="{$competitor1} or {$competitor2}"/>
<outcome id="11" name="draw or {$competitor2}"/>
</outcomes>
</market>
<market id="116" name="Overtime - total" groups="all|score|ot">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="653" name="{!inningnr} innings over {overnr} - {$competitor2} boundary" groups="all">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="overnr" type="integer"/>
</specifiers>
</market>
<market id="926" name="{%player} total bases (incl. extra innings)" groups="all|incl_ei">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="player" type="string"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="328" name="Total maps" groups="all|score|regular_play">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="228" name="{$competitor2} total (incl. overtime)" groups="all|score|incl_ot">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="541" name="Double chance (match) &amp; 2nd half both teams score" groups="all|regular_play|combo">
<outcomes>
<outcome id="1718" name="{$competitor1}/draw &amp; yes"/>
<outcome id="1719" name="{$competitor1}/draw &amp; no"/>
<outcome id="1720" name="{$competitor1}/{$competitor2} &amp; yes"/>
<outcome id="1721" name="{$competitor1}/{$competitor2} &amp; no"/>
<outcome id="1722" name="draw/{$competitor2} &amp; yes"/>
<outcome id="1723" name="draw/{$competitor2} &amp; no"/>
</outcomes>
</market>
<market id="699" name="Team with top bowler" groups="all">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
</market>
<market id="925" name="{%player} total pitcher strikeouts (incl. extra innings)" groups="all|incl_ei">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="player" type="string"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="644" name="{!inningnr} innings - {%player} total sixes" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="player" type="string"/>
<specifier name="total" type="decimal"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="664" name="{!inningnr} innings overs 0 to {overnr} - {$competitor2} total dismissals" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="overnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="1085" name="Exact balls of {%player} {!appearancenr} time at bat" groups="all">
<outcomes>
<outcome id="1005" name="0"/>
<outcome id="1006" name="1"/>
<outcome id="1007" name="2"/>
<outcome id="1008" name="3"/>
<outcome id="1009" name="4"/>
</outcomes>
<specifiers>
<specifier name="appearancenr" type="integer"/>
<specifier name="player" type="string"/>
</specifiers>
</market>
<market id="874" name="Show" groups="all" includes_outcomes_of_type="sr:competitor">
<specifiers>
<specifier name="pos" type="integer"/>
</specifiers>
</market>
<market id="102" name="15 minutes - 1x2 from {from} to {to}" groups="all|score|15_min">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="329" name="Correct score (in maps)" groups="all|score|regular_play">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="1095" name="{!stopnr} pit stop" groups="all" includes_outcomes_of_type="sr:competitor" outcome_type="competitor">
<specifiers>
<specifier name="stopnr" type="integer" description="pit stop number"/>
</specifiers>
</market>
<market id="452" name="{!periodnr} period - both teams to score" groups="all|score|period">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="periodnr" type="integer"/>
</specifiers>
</market>
<market id="972" name="{$competitor1} 10/3 sixes head2head spread" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
</market>
<market id="297" name="Total (over-exact-under)" groups="all|score|regular_play">
<outcomes>
<outcome id="939" name="under {total}"/>
<outcome id="940" name="exact {total}"/>
<outcome id="941" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="768" name="Player points (incl. overtime)" groups="all|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="145" name="Booking point range" groups="all|regular_play|bookings">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="491" name="Which player wins the rest of the match" groups="all|score|regular_play">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="score" type="string" description="current score in match"/>
</specifiers>
</market>
<market id="84" name="2nd half - {!goalnr} goal" groups="all|score|2nd_half">
<outcomes>
<outcome id="6" name="{$competitor1}"/>
<outcome id="7" name="none"/>
<outcome id="8" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="goalnr" type="integer"/>
</specifiers>
</market>
<market id="1128" name="{!inningnr} innings - {%player1} &amp; {%player2} to score {milestone} &amp; {%player3} over {total} dismissals" groups="all">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="player1" type="string"/>
<specifier name="player2" type="string"/>
<specifier name="player3" type="string"/>
<specifier name="milestone" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="672" name="{!inningnr} innings - {$competitor1} exact runs" groups="all">
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="888" name="Anytime goalscorer &amp; 1x2" groups="all|regular_play|combo">
<outcomes>
<outcome id="1894" name="{%player} &amp; {$competitor1}"/>
<outcome id="1895" name="{%player} &amp; draw"/>
<outcome id="1896" name="{%player} &amp; {$competitor2}"/>
<outcome id="1897" name="other"/>
</outcomes>
<specifiers>
<specifier name="player" type="string"/>
</specifiers>
</market>
<market id="605" name="{!inningnr} innings - total" groups="all|score|innings">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="290" name="Winning margin (incl. overtime)" groups="all|score|incl_ot">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="549" name="{$competitor1} multigoals" groups="all|score|regular_play">
<outcomes>
<outcome id="1746" name="1-2"/>
<outcome id="1747" name="1-3"/>
<outcome id="1748" name="2-3"/>
<outcome id="1749" name="4+"/>
<outcome id="1805" name="no goal"/>
</outcomes>
</market>
<market id="790" name="Player marks" groups="all|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="569" name="15 minutes - {$competitor1} total corners from {from} to {to}" groups="all|15_min|corners">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="118" name="Overtime - correct score [{score}]" groups="all|score|ot">
<outcomes>
<outcome id="442" name="0:0"/>
<outcome id="444" name="1:0"/>
<outcome id="446" name="2:0"/>
<outcome id="448" name="3:0"/>
<outcome id="450" name="0:1"/>
<outcome id="452" name="1:1"/>
<outcome id="454" name="2:1"/>
<outcome id="456" name="0:2"/>
<outcome id="458" name="1:2"/>
<outcome id="460" name="0:3"/>
</outcomes>
<specifiers>
<specifier name="score" type="string" description="current score in match"/>
</specifiers>
<attributes>
<attribute name="is_flex_score" description="Outcomes should be adjusted according to score specifier"/>
</attributes>
</market>
<market id="974" name="{!inningnr} innings - {$competitor1} total sixes spread" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="548" name="Multigoals" groups="all|score|regular_play">
<outcomes>
<outcome id="1730" name="1-2"/>
<outcome id="1731" name="1-3"/>
<outcome id="1732" name="1-4"/>
<outcome id="1733" name="1-5"/>
<outcome id="1734" name="1-6"/>
<outcome id="1735" name="2-3"/>
<outcome id="1736" name="2-4"/>
<outcome id="1737" name="2-5"/>
<outcome id="1738" name="2-6"/>
<outcome id="1739" name="3-4"/>
<outcome id="1740" name="3-5"/>
<outcome id="1741" name="3-6"/>
<outcome id="1742" name="4-5"/>
<outcome id="1743" name="4-6"/>
<outcome id="1744" name="5-6"/>
<outcome id="1745" name="7+"/>
<outcome id="1804" name="no goal"/>
</outcomes>
</market>
<market id="483" name="1st half - try double chance" groups="all|1st_half|tries">
<outcomes>
<outcome id="9" name="{$competitor1} or draw"/>
<outcome id="10" name="{$competitor1} or {$competitor2}"/>
<outcome id="11" name="draw or {$competitor2}"/>
</outcomes>
</market>
<market id="817" name="{!inningnr} innings - {$competitor2} {!dismissalnr} dismissal method (extended)" groups="all">
<outcomes>
<outcome id="1806" name="fielder catch"/>
<outcome id="1807" name="bowled"/>
<outcome id="1808" name="keeper catch"/>
<outcome id="1809" name="lbw"/>
<outcome id="1810" name="run out"/>
<outcome id="1811" name="stumped"/>
<outcome id="1812" name="other"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="dismissalnr" type="integer"/>
</specifiers>
</market>
<market id="597" name="5 minutes - total bookings from {from} to {to}" groups="all|5_min|bookings">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="180" name="1st half - {$competitor1} exact corners" groups="all|1st_half|corners">
<outcomes>
<outcome id="730" name="0-1"/>
<outcome id="732" name="2"/>
<outcome id="734" name="3"/>
<outcome id="736" name="4+"/>
</outcomes>
</market>
<market id="860" name="{$competitor1} or both teams to score" groups="all|regular_play|combo">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="740" name="Total scoreless innings" groups="all|score|regular_play">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="534" name="Championship free text market" groups="all" includes_outcomes_of_type="pre:outcometext">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="1100" name="Total overtakings" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="967" name="Any team to lead by {points}" groups="all">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="points" type="integer"/>
</specifiers>
</market>
<market id="782" name="Batter home runs (incl. extra innings)" groups="all|incl_ei|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="371" name="{!setnr} set - leg handicap" groups="all|score|set">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="897" name="Overtime - correct score" groups="all|score|ot">
<outcomes>
<outcome id="462" name="0:0"/>
<outcome id="464" name="1:1"/>
<outcome id="466" name="2:2"/>
<outcome id="468" name="1:0"/>
<outcome id="470" name="2:0"/>
<outcome id="472" name="2:1"/>
<outcome id="474" name="0:1"/>
<outcome id="476" name="0:2"/>
<outcome id="478" name="1:2"/>
<outcome id="480" name="other"/>
</outcomes>
</market>
<market id="1141" name="{!inningnr} innings over {overnr} - {%player1} &amp; {%player2} to hit a boundary" groups="all">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="overnr" type="integer"/>
<specifier name="player1" type="string"/>
<specifier name="player2" type="string"/>
</specifiers>
</market>
<market id="818" name="Halftime/fulltime &amp; total" groups="all|regular_play|combo">
<outcomes>
<outcome id="1836" name="{$competitor1}/{$competitor1} &amp; under {total}"/>
<outcome id="1837" name="{$competitor1}/draw &amp; under {total}"/>
<outcome id="1838" name="{$competitor1}/{$competitor2} &amp; under {total}"/>
<outcome id="1839" name="draw/{$competitor1} &amp; under {total}"/>
<outcome id="1840" name="draw/draw &amp; under {total}"/>
<outcome id="1841" name="draw/{$competitor2} &amp; under {total}"/>
<outcome id="1842" name="{$competitor2}/{$competitor1} &amp; under {total}"/>
<outcome id="1843" name="{$competitor2}/draw &amp; under {total}"/>
<outcome id="1844" name="{$competitor2}/{$competitor2} &amp; under {total}"/>
<outcome id="1845" name="{$competitor1}/{$competitor1} &amp; over {total}"/>
<outcome id="1846" name="{$competitor1}/draw &amp; over {total}"/>
<outcome id="1847" name="{$competitor1}/{$competitor2} &amp; over {total}"/>
<outcome id="1848" name="draw/{$competitor1} &amp; over {total}"/>
<outcome id="1849" name="draw/draw &amp; over {total}"/>
<outcome id="1850" name="draw/{$competitor2} &amp; over {total}"/>
<outcome id="1851" name="{$competitor2}/{$competitor1} &amp; over {total}"/>
<outcome id="1852" name="{$competitor2}/draw &amp; over {total}"/>
<outcome id="1853" name="{$competitor2}/{$competitor2} &amp; over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="853" name="{$competitor2} to win exactly 2 sets" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="73" name="1st half - {$competitor2} exact goals" groups="all|score|1st_half">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="181" name="1st half - {$competitor2} exact corners" groups="all|1st_half|corners">
<outcomes>
<outcome id="730" name="0-1"/>
<outcome id="732" name="2"/>
<outcome id="734" name="3"/>
<outcome id="736" name="4+"/>
</outcomes>
</market>
<market id="554" name="{!mapnr} map - kill draw no bet" groups="all|kills|map">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
</specifiers>
</market>
<market id="380" name="{!setnr} set leg {legnr} - checkout colour" groups="all|leg|checkout">
<outcomes>
<outcome id="1049" name="red"/>
<outcome id="1050" name="green"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="legnr" type="integer"/>
</specifiers>
</market>
<market id="419" name="Odd/even (incl. overtime and penalties)" groups="all|score|incl_ot_and_pen">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
</market>
<market id="8" name="{!goalnr} goal" groups="all|score|regular_play">
<outcomes>
<outcome id="6" name="{$competitor1}"/>
<outcome id="7" name="none"/>
<outcome id="8" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="goalnr" type="integer"/>
</specifiers>
</market>
<market id="1026" name="Hole {holenr} - {%competitor} under par" groups="all">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="holenr" type="integer"/>
<specifier name="competitor" type="string"/>
</specifiers>
<attributes>
<attribute name="is_golf_stroke_play_market" description="This market is applicable to Golf stroke play"/>
</attributes>
</market>
<market id="775" name="Player goals (incl. overtime)" groups="all|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="555" name="{!mapnr} map - kill handicap" groups="all|kills|map">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="98" name="2nd half - correct score" groups="all|score|2nd_half">
<outcomes>
<outcome id="546" name="0:0"/>
<outcome id="548" name="0:1"/>
<outcome id="550" name="0:2"/>
<outcome id="552" name="1:0"/>
<outcome id="554" name="1:1"/>
<outcome id="556" name="1:2"/>
<outcome id="558" name="2:0"/>
<outcome id="560" name="2:1"/>
<outcome id="562" name="2:2"/>
<outcome id="564" name="other"/>
</outcomes>
</market>
<market id="220" name="Will there be overtime" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="208" name="{!setnr} set - race to {games} games" groups="all|score|set">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="games" type="integer"/>
</specifiers>
</market>
<market id="91" name="2nd half - {$competitor1} total" groups="all|score|2nd_half">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="259" name="US Total (incl. extra innings)" groups="all|score|incl_ei">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="455" name="{!periodnr} period - {$competitor2} clean sheet" groups="all|score|period">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="periodnr" type="integer"/>
</specifiers>
</market>
<market id="315" name="{!setnr} set - 1x2" groups="all|score|set">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
</specifiers>
</market>
<market id="963" name="{$competitor2} total maximum consecutive points" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="268" name="Will there be an extra inning" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="1092" name="Winner of group" groups="all" includes_outcomes_of_type="sr:competitor" outcome_type="competitor">
<specifiers>
<specifier name="id" type="string"/>
</specifiers>
</market>
<market id="444" name="{!periodnr} period - {!goalnr} goal" groups="all|score|period">
<outcomes>
<outcome id="6" name="{$competitor1}"/>
<outcome id="7" name="none"/>
<outcome id="8" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="periodnr" type="integer"/>
<specifier name="goalnr" type="integer"/>
</specifiers>
</market>
<market id="875" name="{!inningnr} innings - {$competitor1} total at {!dismissalnr} dismissal" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="dismissalnr" type="integer"/>
<specifier name="total" type="decimal"/>
<specifier name="inningnr" type="integer"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="480" name="{$competitor2} total tries" groups="all|regular_play|tries">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="671" name="{!inningnr} innings - {$competitor2} total in the highest scoring over" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="total" type="decimal"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="137" name="{!bookingnr} booking" groups="all|regular_play|bookings">
<outcomes>
<outcome id="6" name="{$competitor1}"/>
<outcome id="7" name="none"/>
<outcome id="8" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="bookingnr" type="integer"/>
</specifiers>
</market>
<market id="191" name="{$competitor2} total games" groups="all|score|regular_play">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="287" name="{!inningnr} inning - 1x2" groups="all|score|inning">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="473" name="Try 1x2" groups="all|regular_play|tries">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
</market>
<market id="1142" name="{!inningnr} innings over {overnr} - {%player1} &amp; {%player2} over {total}" groups="all">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="overnr" type="integer"/>
<specifier name="player1" type="string"/>
<specifier name="player2" type="string"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="1057" name="{!quarternr} quarter - race to {pointnr} points" groups="all|score|quarter">
<outcomes>
<outcome id="6" name="{$competitor1}"/>
<outcome id="7" name="none"/>
<outcome id="8" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="quarternr" type="integer"/>
<specifier name="pointnr" type="integer"/>
</specifiers>
</market>
<market id="352" name="{!inningnr} innings overs 0 to {overnr} - {$competitor1} total" groups="all|score|x_overs">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="overnr" type="integer"/>
<specifier name="total" type="decimal"/>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="1143" name="{!inningnr} innings over {overnr} - {$competitor1} to score a boundary four &amp; a six" groups="all">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="overnr" type="integer"/>
</specifiers>
</market>
<market id="996" name="Total run outs spread" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
</market>
<market id="847" name="{!inningnr} innings over {overnr} - {$competitor2} total spread" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="overnr" type="integer"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
<attributes>
<attribute name="is_spread_market" description="This is a spread-market and special client-side spread rules need to be followed for odds-changes and bet-settlements"/>
</attributes>
</market>
<market id="754" name="{!quarternr} quarter - which team wins the rest" groups="all|score|quarter">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="quarternr" type="integer"/>
<specifier name="score" type="string" description="current score"/>
</specifiers>
</market>
<market id="726" name="{!mapnr} map - total kills" groups="all|kills|map">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="563" name="Race to {pointnr} points" groups="all|score|regular_play">
<outcomes>
<outcome id="6" name="{$competitor1}"/>
<outcome id="7" name="none"/>
<outcome id="8" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="pointnr" type="integer"/>
</specifiers>
</market>
<market id="47" name="Halftime/fulltime" groups="all|score|regular_play">
<outcomes>
<outcome id="418" name="{$competitor1}/{$competitor1}"/>
<outcome id="420" name="{$competitor1}/draw"/>
<outcome id="422" name="{$competitor1}/{$competitor2}"/>
<outcome id="424" name="draw/{$competitor1}"/>
<outcome id="426" name="draw/draw"/>
<outcome id="428" name="draw/{$competitor2}"/>
<outcome id="430" name="{$competitor2}/{$competitor1}"/>
<outcome id="432" name="{$competitor2}/draw"/>
<outcome id="434" name="{$competitor2}/{$competitor2}"/>
</outcomes>
</market>
<market id="568" name="15 minutes - total corners from {from} to {to}" groups="all|15_min|corners">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="540" name="Double chance (match) &amp; 1st half both teams score" groups="all|regular_play|combo">
<outcomes>
<outcome id="1718" name="{$competitor1}/draw &amp; yes"/>
<outcome id="1719" name="{$competitor1}/draw &amp; no"/>
<outcome id="1720" name="{$competitor1}/{$competitor2} &amp; yes"/>
<outcome id="1721" name="{$competitor1}/{$competitor2} &amp; no"/>
<outcome id="1722" name="draw/{$competitor2} &amp; yes"/>
<outcome id="1723" name="draw/{$competitor2} &amp; no"/>
</outcomes>
</market>
<market id="507" name="{!framenr} frame - {$competitor1} break 100+" groups="all|frame|break">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="framenr" type="integer"/>
</specifiers>
</market>
<market id="173" name="1st half - corner 1x2" groups="all|1st_half|corners">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
</market>
<market id="7" name="Which team wins the rest of the match" groups="all|score|regular_play">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="score" type="string" description="current score in match"/>
</specifiers>
</market>
<market id="918" name="{%player} total rushing yards (incl. overtime)" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="player" type="string"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="1018" name="Total pars" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
<attributes>
<attribute name="is_golf_stroke_play_market" description="This market is applicable to Golf stroke play"/>
</attributes>
</market>
<market id="1131" name="Both teams to score {milestone}" groups="all">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="milestone" type="integer"/>
</specifiers>
</market>
<market id="488" name="1st half - {$competitor1} total tries" groups="all|1st_half|tries">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="678" name="{!inningnr} innings - {$competitor1} last player standing" groups="all" includes_outcomes_of_type="sr:player">
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="583" name="5 minutes - {$competitor1} total corners from {from} to {to}" groups="all|5_min|corners">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="245" name="{!gamenr} game - winner" groups="all|score|game">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="gamenr" type="integer"/>
</specifiers>
</market>
<market id="1017" name="Total birdies" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
<attributes>
<attribute name="is_golf_stroke_play_market" description="This market is applicable to Golf stroke play"/>
</attributes>
</market>
<market id="445" name="{!periodnr} period - handicap {hcp}" groups="all|score|period">
<outcomes>
<outcome id="1711" name="{$competitor1} ({hcp})"/>
<outcome id="1712" name="draw ({hcp})"/>
<outcome id="1713" name="{$competitor2} ({hcp})"/>
</outcomes>
<specifiers>
<specifier name="periodnr" type="integer"/>
<specifier name="hcp" type="string"/>
</specifiers>
</market>
<market id="904" name="Next scoring type (incl. overtime)" groups="all|score|incl_ot">
<outcomes>
<outcome id="1945" name="touchdown"/>
<outcome id="1946" name="field goal"/>
<outcome id="1947" name="safety"/>
<outcome id="1948" name="none"/>
</outcomes>
<specifiers>
<specifier name="score" type="string" description="current score in match"/>
</specifiers>
</market>
<market id="261" name="{$competitor2} total (incl. extra innings)" groups="all|score|incl_ei">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="36" name="Total &amp; both teams to score" groups="all|regular_play|combo">
<outcomes>
<outcome id="90" name="over {total} &amp; yes"/>
<outcome id="92" name="under {total} &amp; yes"/>
<outcome id="94" name="over {total} &amp; no"/>
<outcome id="96" name="under {total} &amp; no"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="101" name="When will the {!goalnr} goal be scored (10 min interval)" groups="all|score|regular_play">
<outcomes>
<outcome id="598" name="1-10"/>
<outcome id="600" name="11-20"/>
<outcome id="602" name="21-30"/>
<outcome id="604" name="31-40"/>
<outcome id="606" name="41-50"/>
<outcome id="608" name="51-60"/>
<outcome id="610" name="61-70"/>
<outcome id="612" name="71-80"/>
<outcome id="614" name="81-90"/>
<outcome id="616" name="none"/>
</outcomes>
<specifiers>
<specifier name="goalnr" type="integer"/>
</specifiers>
</market>
<market id="272" name="Highest scoring inning" groups="all|score|regular_play">
<outcomes>
<outcome id="951" name="1st inning"/>
<outcome id="952" name="2nd inning"/>
<outcome id="953" name="3rd inning"/>
<outcome id="954" name="4th inning"/>
<outcome id="955" name="5th inning"/>
<outcome id="956" name="6th inning"/>
<outcome id="957" name="7th inning"/>
<outcome id="958" name="8th inning"/>
<outcome id="959" name="9th inning"/>
<outcome id="960" name="equal"/>
</outcomes>
</market>
<market id="982" name="Total {upsnr} ups spread" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
<specifiers>
<specifier name="upsnr" type="integer"/>
</specifiers>
</market>
<market id="198" name="Odd/even games" groups="all|score|regular_play">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
</market>
<market id="310" name="{!setnr} set - total points" groups="all|score|set">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="378" name="{!setnr} set leg {legnr} - point range on {!visitnr} visit" groups="all|score|visit">
<outcomes>
<outcome id="1044" name="0-89"/>
<outcome id="1045" name="90-100"/>
<outcome id="1046" name="101+"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="legnr" type="integer"/>
<specifier name="visitnr" type="integer"/>
</specifiers>
</market>
<market id="513" name="{!framenr} frame - player to pot {!xth} ball" groups="all|score|frame">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="framenr" type="integer"/>
<specifier name="xth" type="integer"/>
</specifiers>
</market>
<market id="460" name="{!periodnr} period - handicap" groups="all|score|period">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="periodnr" type="integer"/>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="262" name="Total (over-exact-under) (incl. extra innings)" groups="all|score|incl_ei">
<outcomes>
<outcome id="939" name="under {total}"/>
<outcome id="940" name="exact {total}"/>
<outcome id="941" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="338" name="{!mapnr} map round {roundnr} - winner" groups="all|round">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
<specifier name="roundnr" type="integer"/>
</specifiers>
</market>
<market id="1171" name="Holes {from} to {to} - draw no bet" groups="all">
<outcomes>
<outcome id="2033" name="{%competitor1}"/>
<outcome id="2034" name="{%competitor2}"/>
</outcomes>
<specifiers>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
<specifier name="competitor1" type="string"/>
<specifier name="competitor2" type="string"/>
</specifiers>
<attributes>
<attribute name="is_golf_match_play_market" description="This market is applicable to Golf match play"/>
</attributes>
</market>
<market id="510" name="{!framenr} frame - {$competitor1} break 50+" groups="all|frame|break">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="framenr" type="integer"/>
</specifiers>
</market>
<market id="496" name="Race to {framenr} frames" groups="all|score|regular_play">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="framenr" type="integer"/>
</specifiers>
</market>
<market id="201" name="Double result (1st set/match)" groups="all|score|regular_play">
<outcomes>
<outcome id="861" name="{$competitor1}/{$competitor1}"/>
<outcome id="862" name="{$competitor2}/{$competitor1}"/>
<outcome id="863" name="{$competitor1}/{$competitor2}"/>
<outcome id="864" name="{$competitor2}/{$competitor2}"/>
</outcomes>
</market>
<market id="1178" name="1st half handicap &amp; 1st half total" groups="all|combo|incl_ot">
<outcomes>
<outcome id="2035" name="{$competitor1} ({+hcp}) &amp; over {total}"/>
<outcome id="2036" name="{$competitor1} ({+hcp}) &amp; under {total}"/>
<outcome id="2037" name="{$competitor2} ({-hcp}) &amp; over {total}"/>
<outcome id="2038" name="{$competitor2} ({-hcp}) &amp; under {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="706" name="{!inningnr} innings - {$competitor1} total ducks" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="total" type="decimal"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="497" name="Frames {fromframe} to {toframe} - {frames} consecutive frames winner" groups="all|score|x_frames">
<outcomes>
<outcome id="6" name="{$competitor1}"/>
<outcome id="7" name="none"/>
<outcome id="8" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="fromframe" type="integer"/>
<specifier name="toframe" type="integer"/>
<specifier name="frames" type="integer"/>
</specifiers>
</market>
<market id="764" name="Player rushing yards (incl. overtime)" groups="all|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="25" name="Goal range" groups="all|score|regular_play">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="26" name="Odd/even" groups="all|score|regular_play">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
</market>
<market id="56" name="{$competitor1} to score in both halves" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="218" name="{!setnr} set game {gamenr} - {!pointnr} point" groups="all|score|points">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="gamenr" type="integer"/>
<specifier name="pointnr" type="integer"/>
</specifiers>
</market>
<market id="279" name="Innings 1 to 5th top - 1x2" groups="all|score|4.5_innings">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
</market>
<market id="128" name="Penalty shootout - {$competitor1} total" groups="all|score|pen_so">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="1129" name="{!inningnr} innings - {%player1} + {%player2} total" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="player1" type="string"/>
<specifier name="player2" type="string"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="45" name="Correct score" groups="all|score|regular_play">
<outcomes>
<outcome id="274" name="0:0"/>
<outcome id="276" name="1:0"/>
<outcome id="278" name="2:0"/>
<outcome id="280" name="3:0"/>
<outcome id="282" name="4:0"/>
<outcome id="284" name="0:1"/>
<outcome id="286" name="1:1"/>
<outcome id="288" name="2:1"/>
<outcome id="290" name="3:1"/>
<outcome id="292" name="4:1"/>
<outcome id="294" name="0:2"/>
<outcome id="296" name="1:2"/>
<outcome id="298" name="2:2"/>
<outcome id="300" name="3:2"/>
<outcome id="302" name="4:2"/>
<outcome id="304" name="0:3"/>
<outcome id="306" name="1:3"/>
<outcome id="308" name="2:3"/>
<outcome id="310" name="3:3"/>
<outcome id="312" name="4:3"/>
<outcome id="314" name="0:4"/>
<outcome id="316" name="1:4"/>
<outcome id="318" name="2:4"/>
<outcome id="320" name="3:4"/>
<outcome id="322" name="4:4"/>
<outcome id="324" name="other"/>
</outcomes>
</market>
<market id="1123" name="{!inningnr} inning - Winner" groups="all|inning">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="907" name="Short term free text market" groups="all" includes_outcomes_of_type="pre:outcometext">
<specifiers>
<specifier name="variant" type="variable_text"/>
<specifier name="version" type="string"/>
</specifiers>
</market>
<market id="591" name="10 minutes - booking 1x2 from {from} to {to}" groups="all|10_min|bookings">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="346" name="{!inningnr} innings - {$competitor2} run range" groups="all|score|innings">
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="254" name="Handicap {hcp} (incl. extra innings)" groups="all|score|incl_ei">
<outcomes>
<outcome id="1711" name="{$competitor1} ({hcp})"/>
<outcome id="1712" name="draw ({hcp})"/>
<outcome id="1713" name="{$competitor2} ({hcp})"/>
</outcomes>
<specifiers>
<specifier name="hcp" type="string"/>
</specifiers>
</market>
<market id="1045" name="{!inningnr} inning - {$competitor2} total hits" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="273" name="Exact runs in highest scoring inning" groups="all|score|regular_play">
<outcomes>
<outcome id="961" name="0"/>
<outcome id="962" name="1"/>
<outcome id="963" name="2"/>
<outcome id="964" name="3"/>
<outcome id="965" name="4"/>
<outcome id="966" name="5+"/>
</outcomes>
</market>
<market id="418" name="{$competitor2} exact goals (incl. overtime and penalties)" groups="all|score|incl_ot_and_pen">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="743" name="Innings {from} to {to} - 1x2" groups="all|score|3_innings">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="932" name="Total field goals made (incl. overtime)" groups="all|incl_ot">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="750" name="{!inningnr} inning - {$competitor2} to score" groups="all|score|inning">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="576" name="10 minutes - {$competitor1} total corners from {from} to {to}" groups="all|10_min|corners">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="81" name="1st half - correct score" groups="all|score|1st_half">
<outcomes>
<outcome id="462" name="0:0"/>
<outcome id="464" name="1:1"/>
<outcome id="466" name="2:2"/>
<outcome id="468" name="1:0"/>
<outcome id="470" name="2:0"/>
<outcome id="472" name="2:1"/>
<outcome id="474" name="0:1"/>
<outcome id="476" name="0:2"/>
<outcome id="478" name="1:2"/>
<outcome id="480" name="other"/>
</outcomes>
</market>
<market id="142" name="Exact bookings" groups="all|regular_play|bookings">
<outcomes>
<outcome id="710" name="0-3"/>
<outcome id="712" name="4"/>
<outcome id="714" name="5"/>
<outcome id="716" name="6"/>
<outcome id="718" name="7"/>
<outcome id="720" name="8"/>
<outcome id="722" name="9"/>
<outcome id="724" name="10"/>
<outcome id="726" name="11"/>
<outcome id="728" name="12+"/>
</outcomes>
</market>
<market id="665" name="{!daynr} day session {sessionnr} - total" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="daynr" type="integer"/>
<specifier name="sessionnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="253" name="Which team wins the rest of the match (incl. extra innings)" groups="all|score|incl_ei">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="score" type="string" description="current score in match"/>
</specifiers>
</market>
<market id="404" name="{!goalnr} goal (incl. overtime)" groups="all|score|incl_ot">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="goalnr" type="integer"/>
</specifiers>
</market>
<market id="61" name="1st half - which team wins the rest" groups="all|score|1st_half">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="score" type="string" description="current score in match"/>
</specifiers>
</market>
<market id="692" name="{!inningnr} innings - {$competitor1} to finish with a boundary" groups="all">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="735" name="{!mapnr} map - player with highest creep score" groups="all|map|player" includes_outcomes_of_type="sr:player">
<specifiers>
<specifier name="mapnr" type="integer"/>
</specifiers>
</market>
<market id="657" name="{!inningnr} innings - {!dismissalnr} batter out" groups="all">
<outcomes>
<outcome id="1821" name="{%player1}"/>
<outcome id="1822" name="{%player2}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="dismissalnr" type="integer"/>
<specifier name="player1" type="string"/>
<specifier name="player2" type="string"/>
</specifiers>
</market>
<market id="796" name="Player runs" groups="all|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="417" name="{$competitor1} exact goals (incl. overtime and penalties)" groups="all|score|incl_ot_and_pen">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="356" name="{!inningnr} innings over {overnr} - {$competitor1} total" groups="all|score|over">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="overnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="535" name="Short term free text market" groups="all" includes_outcomes_of_type="pre:outcometext">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="127" name="Penalty shootout - total" groups="all|score|pen_so">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="989" name="{!inningnr} innings overs 0 to {overnr} - {$competitor2} supremacy spread" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
<specifiers>
<specifier name="overnr" type="integer"/>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="1042" name="{$competitor2} total hits (incl. extra innings)" groups="all|incl_ei">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="172" name="Odd/even corners" groups="all|regular_play|corners">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
</market>
<market id="379" name="{!setnr} set leg {legnr} - checkout score {score}+" groups="all|leg|checkout">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="legnr" type="integer"/>
<specifier name="score" type="integer"/>
</specifiers>
</market>
<market id="656" name="{!inningnr} innings - {!dismissalnr} partnership 1x2" groups="all">
<outcomes>
<outcome id="1818" name="{%player1}"/>
<outcome id="1819" name="draw"/>
<outcome id="1820" name="{%player2}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="dismissalnr" type="integer"/>
<specifier name="player1" type="string"/>
<specifier name="player2" type="string"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="670" name="{!inningnr} innings - {$competitor1} total in the highest scoring over" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="total" type="decimal"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="590" name="15 minutes - {$competitor2} sending off from {from} to {to}" groups="all|15_min|bookings">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="580" name="5 minutes - {!cornernr} corner from {from} to {to}" groups="all|5_min|corners">
<outcomes>
<outcome id="6" name="{$competitor1}"/>
<outcome id="7" name="none"/>
<outcome id="8" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="cornernr" type="integer"/>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="677" name="{!inningnr} innings - {$competitor2} top bowler" groups="all" includes_outcomes_of_type="sr:player">
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="maxovers" type="integer"/>
<specifier name="type" type="string"/>
</specifiers>
</market>
<market id="10" name="Double chance" groups="all|score|regular_play">
<outcomes>
<outcome id="9" name="{$competitor1} or draw"/>
<outcome id="10" name="{$competitor1} or {$competitor2}"/>
<outcome id="11" name="draw or {$competitor2}"/>
</outcomes>
</market>
<market id="697" name="Total dismissals" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="1028" name="Holes {from} to {to} - 2 ball (1x2)" groups="all">
<outcomes>
<outcome id="1966" name="{%competitor1}"/>
<outcome id="1967" name="draw"/>
<outcome id="1968" name="{%competitor2}"/>
</outcomes>
<specifiers>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
<specifier name="competitor1" type="string"/>
<specifier name="competitor2" type="string"/>
</specifiers>
<attributes>
<attribute name="is_golf_stroke_play_market" description="This market is applicable to Golf stroke play"/>
</attributes>
</market>
<market id="543" name="2nd half - 1x2 &amp; both teams to score" groups="all|combo|2nd_half">
<outcomes>
<outcome id="78" name="{$competitor1} &amp; yes"/>
<outcome id="80" name="{$competitor1} &amp; no"/>
<outcome id="82" name="draw &amp; yes"/>
<outcome id="84" name="draw &amp; no"/>
<outcome id="86" name="{$competitor2} &amp; yes"/>
<outcome id="88" name="{$competitor2} &amp; no"/>
</outcomes>
</market>
<market id="1107" name="Laps {from} to {to} - fastest lap" groups="all" includes_outcomes_of_type="sr:competitor" outcome_type="competitor">
<specifiers>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="617" name="{$competitor2} odd/even (incl. overtime)" groups="all|score|incl_ot">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
</market>
<market id="696" name="Total wides" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="712" name="Most keeper catches" groups="all">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="465" name="Overtime - {$competitor1} no bet" groups="all|score|ot">
<outcomes>
<outcome id="776" name="draw"/>
<outcome id="778" name="{$competitor2}"/>
</outcomes>
</market>
<market id="998" name="Man of the match player performance spread" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
</market>
<market id="1016" name="Total eagles" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
<attributes>
<attribute name="is_golf_stroke_play_market" description="This market is applicable to Golf stroke play"/>
</attributes>
</market>
<market id="732" name="{!mapnr} map - race to {xth} net worth" groups="all|map|progress">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
<specifier name="xth" type="integer"/>
</specifiers>
</market>
<market id="16" name="Handicap" groups="all|score|regular_play">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="624" name="{!mapnr} map - player with most kills (incl. overtime)" groups="all|map_incl_ot|player" includes_outcomes_of_type="sr:player">
<specifiers>
<specifier name="mapnr" type="integer"/>
</specifiers>
</market>
<market id="1043" name="{!inningnr} inning - total hits" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="714" name="Most stumpings" groups="all">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="913" name="Winner &amp; round range" groups="all">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="154" name="1st half - {$competitor2} total bookings" groups="all|1st_half|bookings">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="168" name="{$competitor2} total corners" groups="all|regular_play|corners">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="975" name="{!inningnr} innings - {$competitor2} total sixes spread" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="854" name="{$competitor1} or over {total}" groups="all|regular_play|combo">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="816" name="{!inningnr} innings - {$competitor1} {!dismissalnr} dismissal method (extended)" groups="all">
<outcomes>
<outcome id="1806" name="fielder catch"/>
<outcome id="1807" name="bowled"/>
<outcome id="1808" name="keeper catch"/>
<outcome id="1809" name="lbw"/>
<outcome id="1810" name="run out"/>
<outcome id="1811" name="stumped"/>
<outcome id="1812" name="other"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="dismissalnr" type="integer"/>
</specifiers>
</market>
<market id="1098" name="{%competitor} total overtakings" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="competitor" type="string"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="579" name="5 minutes - corner 1x2 from {from} to {to}" groups="all|5_min|corners">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="140" name="{$competitor1} total bookings" groups="all|regular_play|bookings">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="484" name="1st half - try draw no bet" groups="all|1st_half|tries">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
</market>
<market id="289" name="Which team wins the jump ball" groups="all|misc">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
</market>
<market id="1048" name="Innings 1 to 5 - {$competitor2} total hits" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="742" name="First {innings} innings 1x2 &amp; 1x2 (incl. extra innings)" groups="all|combo|incl_ei">
<outcomes>
<outcome id="1529" name="{$competitor1} &amp; {$competitor1}"/>
<outcome id="1530" name="draw &amp; {$competitor1}"/>
<outcome id="1531" name="{$competitor2} &amp; {$competitor1}"/>
<outcome id="1532" name="{$competitor1} &amp; draw"/>
<outcome id="1533" name="draw &amp; draw"/>
<outcome id="1534" name="{$competitor2} &amp; draw"/>
<outcome id="1535" name="{$competitor1} &amp; {$competitor2}"/>
<outcome id="1536" name="draw &amp; {$competitor2}"/>
<outcome id="1537" name="{$competitor2} &amp; {$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="innings" type="integer"/>
</specifiers>
</market>
<market id="38" name="{!goalnr} goalscorer" groups="all|regular_play|scorers" includes_outcomes_of_type="sr:player">
<outcomes>
<outcome id="1716" name="no goal"/>
</outcomes>
<specifiers>
<specifier name="goalnr" type="integer"/>
<specifier name="type" type="string"/>
</specifiers>
</market>
<market id="1073" name="{!inningnr} inning - exact home runs" groups="all">
<outcomes>
<outcome id="1996" name="0"/>
<outcome id="1997" name="1"/>
<outcome id="1998" name="2+"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="669" name="{!inningnr} innings - {$competitor2} total run outs" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="total" type="decimal"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="169" name="Corner range" groups="all|regular_play|corners">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="745" name="Innings {from} to {to} - odd/even" groups="all|score|3_innings">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
<specifiers>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="733" name="{!mapnr} map - race to level {xth}" groups="all|map|progress">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
<specifier name="xth" type="integer"/>
</specifiers>
</market>
<market id="236" name="{!quarternr} quarter - total" groups="all|score|quarter">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="quarternr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="833" name="Standard bet (incl. bonus ball in same drum)" groups="all">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="270" name="Team to win more innings" groups="all|score|regular_play">
<outcomes>
<outcome id="948" name="{$competitor1}"/>
<outcome id="949" name="draw"/>
<outcome id="950" name="{$competitor2}"/>
</outcomes>
</market>
<market id="566" name="15 minutes - {!cornernr} corner from {from} to {to}" groups="all|15_min|corners">
<outcomes>
<outcome id="6" name="{$competitor1}"/>
<outcome id="7" name="none"/>
<outcome id="8" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="cornernr" type="integer"/>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="326" name="Map handicap {hcp}" groups="all|score|regular_play">
<outcomes>
<outcome id="1711" name="{$competitor1} ({hcp})"/>
<outcome id="1712" name="draw ({hcp})"/>
<outcome id="1713" name="{$competitor2} ({hcp})"/>
</outcomes>
<specifiers>
<specifier name="hcp" type="string"/>
</specifiers>
</market>
<market id="216" name="{!setnr} set game {gamenr} - race to {pointnr} points" groups="all|score|points">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="gamenr" type="integer"/>
<specifier name="pointnr" type="integer"/>
</specifiers>
</market>
<market id="425" name="1x2 &amp; both teams to score (incl. overtime and penalties)" groups="all|combo|incl_ot_and_pen">
<outcomes>
<outcome id="1706" name="{$competitor1} &amp; yes"/>
<outcome id="1708" name="{$competitor1} &amp; no"/>
<outcome id="1709" name="{$competitor2} &amp; yes"/>
<outcome id="1710" name="{$competitor2} &amp; no"/>
</outcomes>
</market>
<market id="762" name="Player carries (incl. overtime)" groups="all|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="29" name="Both teams to score" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="844" name="{!inningnr} innings overs 0 to {overnr} - {$competitor1} total spread" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="overnr" type="integer"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
<attributes>
<attribute name="is_spread_market" description="This is a spread-market and special client-side spread rules need to be followed for odds-changes and bet-settlements"/>
</attributes>
</market>
<market id="271" name="Team with highest scoring inning" groups="all|score|regular_play">
<outcomes>
<outcome id="948" name="{$competitor1}"/>
<outcome id="949" name="draw"/>
<outcome id="950" name="{$competitor2}"/>
</outcomes>
</market>
<market id="2" name="To qualify" groups="all|score|cup_tie">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
</market>
<market id="345" name="{!inningnr} innings - {$competitor1} run range" groups="all|score|innings">
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="1089" name="Any team scoring type {!pointnr} point (incl. overtime)" groups="all">
<outcomes>
<outcome id="2014" name="1 point score"/>
<outcome id="2015" name="2 point score"/>
<outcome id="2016" name="3 point score"/>
</outcomes>
<specifiers>
<specifier name="pointnr" type="integer"/>
</specifiers>
</market>
<market id="1165" name="Total holes played" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
<attributes>
<attribute name="is_golf_match_play_market" description="This market is applicable to Golf match play"/>
</attributes>
</market>
<market id="976" name="Total fours spread" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
</market>
<market id="596" name="5 minutes - booking 1x2 from {from} to {to}" groups="all|5_min|bookings">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="694" name="Which team wins the coin toss" groups="all">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
</market>
<market id="237" name="Point handicap" groups="all|score|regular_play">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="787" name="Batter total bases (incl. extra innings)" groups="all|incl_ei|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="977" name="Multi fours spread" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
</market>
<market id="72" name="1st half - {$competitor1} exact goals" groups="all|score|1st_half">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="873" name="Trio" groups="all">
<outcomes>
<outcome id="1893" name="others"/>
</outcomes>
</market>
<market id="723" name="Will there be a rampage" groups="all|regular_play|kills">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="995" name="Multi flyer spread" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
</market>
<market id="35" name="1x2 &amp; both teams to score" groups="all|regular_play|combo">
<outcomes>
<outcome id="78" name="{$competitor1} &amp; yes"/>
<outcome id="80" name="{$competitor1} &amp; no"/>
<outcome id="82" name="draw &amp; yes"/>
<outcome id="84" name="draw &amp; no"/>
<outcome id="86" name="{$competitor2} &amp; yes"/>
<outcome id="88" name="{$competitor2} &amp; no"/>
</outcomes>
</market>
<market id="481" name="Odd/even tries" groups="all|regular_play|tries">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
</market>
<market id="1051" name="Home run on {!pitchnr} pitch" groups="all|rapid_market|pitch">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="pitchnr" type="integer"/>
</specifiers>
</market>
<market id="698" name="Team with top batter" groups="all">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="368" name="Odd/even sets" groups="all|score|regular_play">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
</market>
<market id="515" name="{!framenr} frame - player to pot last ball" groups="all|score|frame">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="framenr" type="integer"/>
</specifiers>
</market>
<market id="135" name="Penalty shootout - correct score" groups="all|score|pen_so">
<outcomes>
<outcome id="1767" name="0:1"/>
<outcome id="1768" name="0:2"/>
<outcome id="1769" name="0:3"/>
<outcome id="1770" name="1:0"/>
<outcome id="1771" name="1:2"/>
<outcome id="1772" name="1:3"/>
<outcome id="1773" name="1:4"/>
<outcome id="1774" name="2:0"/>
<outcome id="1775" name="2:1"/>
<outcome id="1776" name="2:3"/>
<outcome id="1777" name="2:4"/>
<outcome id="1778" name="3:0"/>
<outcome id="1779" name="3:1"/>
<outcome id="1780" name="3:2"/>
<outcome id="1781" name="3:4"/>
<outcome id="1782" name="3:5"/>
<outcome id="1783" name="4:1"/>
<outcome id="1784" name="4:2"/>
<outcome id="1785" name="4:3"/>
<outcome id="1786" name="4:5"/>
<outcome id="1787" name="5:3"/>
<outcome id="1788" name="5:4"/>
<outcome id="1789" name="other"/>
</outcomes>
</market>
<market id="589" name="15 minutes - {$competitor1} sending off from {from} to {to}" groups="all|15_min|bookings">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="78" name="1st half - 1x2 &amp; both teams to score" groups="all|combo|1st_half">
<outcomes>
<outcome id="78" name="{$competitor1} &amp; yes"/>
<outcome id="80" name="{$competitor1} &amp; no"/>
<outcome id="82" name="draw &amp; yes"/>
<outcome id="84" name="draw &amp; no"/>
<outcome id="86" name="{$competitor2} &amp; yes"/>
<outcome id="88" name="{$competitor2} &amp; no"/>
</outcomes>
</market>
<market id="881" name="Any team to win" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="160" name="1st half - {$competitor1} sending off" groups="all|1st_half|bookings">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="182" name="1st half - corner range" groups="all|1st_half|corners">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="969" name="Player to score a point" groups="all|regular_play|scorers" includes_outcomes_of_type="sr:player" outcome_type="player">
<specifiers>
<specifier name="version" type="string"/>
</specifiers>
</market>
<market id="457" name="{!periodnr} period - last team to score" groups="all|score|period">
<outcomes>
<outcome id="6" name="{$competitor1}"/>
<outcome id="7" name="none"/>
<outcome id="8" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="periodnr" type="integer"/>
</specifiers>
</market>
<market id="571" name="15 minutes - odd/even corners from {from} to {to}" groups="all|15_min|corners">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
<specifiers>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="388" name="{!setnr} set - total 180s" groups="all|set|180s">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="1" name="1x2" groups="all|score|regular_play">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
</market>
<market id="80" name="1st half - correct score [{score}]" groups="all|score|1st_half">
<outcomes>
<outcome id="442" name="0:0"/>
<outcome id="444" name="1:0"/>
<outcome id="446" name="2:0"/>
<outcome id="448" name="3:0"/>
<outcome id="450" name="0:1"/>
<outcome id="452" name="1:1"/>
<outcome id="454" name="2:1"/>
<outcome id="456" name="0:2"/>
<outcome id="458" name="1:2"/>
<outcome id="460" name="0:3"/>
</outcomes>
<specifiers>
<specifier name="score" type="string" description="current score in match"/>
</specifiers>
<attributes>
<attribute name="is_flex_score" description="Outcomes should be adjusted according to score specifier"/>
</attributes>
</market>
<market id="1135" name="Which team has the highest 1st partnership &amp; 1st over &amp; overs 0 to {overnr}" groups="all">
<outcomes>
<outcome id="6" name="{$competitor1}"/>
<outcome id="7" name="none"/>
<outcome id="8" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="overnr" type="integer"/>
</specifiers>
</market>
<market id="532" name="Correct score (in sets)" groups="all|score|regular_play">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="752" name="{!mapnr} map - {!xth} shrine" groups="all|map|structures">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
<specifier name="xth" type="integer"/>
</specifiers>
</market>
<market id="354" name="{!inningnr} innings overs 0 to {overnr} - {$competitor1} run range" groups="all|score|x_overs">
<specifiers>
<specifier name="overnr" type="integer"/>
<specifier name="variant" type="variable_text"/>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="649" name="{!inningnr} innings - {$competitor1} total dismissals" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="total" type="decimal"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="826" name="Bonus ball total" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="152" name="1st half - total bookings" groups="all|1st_half|bookings">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="334" name="{!mapnr} map - 1x2" groups="all|score|map">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
</specifiers>
</market>
<market id="498" name="Frames 1 to {framenr} - 1x2" groups="all|score|x_frames">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="framenr" type="integer"/>
</specifiers>
</market>
<market id="966" name="{$competitor2} to lead by {points}" groups="all">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="points" type="integer"/>
</specifiers>
</market>
<market id="500" name="{!framenr} frame - point handicap" groups="all|score|frame">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="framenr" type="integer"/>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="912" name="Winner &amp; exact rounds" groups="all">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="57" name="{$competitor2} to score in both halves" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="188" name="Set handicap" groups="all|score|regular_play">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="508" name="{!framenr} frame - {$competitor2} break 100+" groups="all|frame|break">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="framenr" type="integer"/>
</specifiers>
</market>
<market id="263" name="Run range (incl. extra innings)" groups="all|score|incl_ei">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="196" name="Exact sets" groups="all|score|regular_play">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="30" name="Which team to score" groups="all|score|regular_play">
<outcomes>
<outcome id="784" name="none"/>
<outcome id="788" name="only {$competitor1}"/>
<outcome id="790" name="only {$competitor2}"/>
<outcome id="792" name="both teams"/>
</outcomes>
</market>
<market id="427" name="Correct score (incl. overtime and penalties)" groups="all|score|incl_ot_and_pen">
<outcomes>
<outcome id="1488" name="1:0"/>
<outcome id="1489" name="2:0"/>
<outcome id="1490" name="3:0"/>
<outcome id="1491" name="4:0"/>
<outcome id="1492" name="5:0"/>
<outcome id="1493" name="0:1"/>
<outcome id="1494" name="2:1"/>
<outcome id="1495" name="3:1"/>
<outcome id="1496" name="4:1"/>
<outcome id="1497" name="5:1"/>
<outcome id="1498" name="0:2"/>
<outcome id="1499" name="1:2"/>
<outcome id="1500" name="3:2"/>
<outcome id="1501" name="4:2"/>
<outcome id="1502" name="5:2"/>
<outcome id="1503" name="0:3"/>
<outcome id="1504" name="1:3"/>
<outcome id="1505" name="2:3"/>
<outcome id="1506" name="4:3"/>
<outcome id="1507" name="5:3"/>
<outcome id="1508" name="0:4"/>
<outcome id="1509" name="1:4"/>
<outcome id="1510" name="2:4"/>
<outcome id="1511" name="3:4"/>
<outcome id="1512" name="5:4"/>
<outcome id="1513" name="0:5"/>
<outcome id="1514" name="1:5"/>
<outcome id="1515" name="2:5"/>
<outcome id="1516" name="3:5"/>
<outcome id="1517" name="4:5"/>
<outcome id="1518" name="other"/>
</outcomes>
</market>
<market id="492" name="Will there be a deciding frame" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="824" name="Bonus ball odd/even" groups="all">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
</market>
<market id="1087" name="Exact home runs (incl. extra innings)" groups="all|incl_ei">
<outcomes>
<outcome id="961" name="0"/>
<outcome id="962" name="1"/>
<outcome id="963" name="2"/>
<outcome id="964" name="3"/>
<outcome id="965" name="4"/>
<outcome id="966" name="5+"/>
</outcomes>
</market>
<market id="533" name="Frames 1 to {framenr} - correct score" groups="all|score|x_frames">
<specifiers>
<specifier name="framenr" type="integer"/>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="552" name="1st half - multigoals" groups="all|score|1st_half">
<outcomes>
<outcome id="1746" name="1-2"/>
<outcome id="1747" name="1-3"/>
<outcome id="1748" name="2-3"/>
<outcome id="1749" name="4+"/>
<outcome id="1805" name="no goal"/>
</outcomes>
</market>
<market id="896" name="{!scorenr} scoring type" groups="all|score|regular_play">
<outcomes>
<outcome id="1937" name="shot"/>
<outcome id="1938" name="header"/>
<outcome id="1939" name="own goal"/>
<outcome id="1940" name="penalty"/>
<outcome id="1941" name="free kick"/>
<outcome id="1942" name="none"/>
</outcomes>
<specifiers>
<specifier name="scorenr" type="integer"/>
</specifiers>
</market>
<market id="542" name="1st half - double chance &amp; both teams to score" groups="all|combo|1st_half">
<outcomes>
<outcome id="1718" name="{$competitor1}/draw &amp; yes"/>
<outcome id="1719" name="{$competitor1}/draw &amp; no"/>
<outcome id="1720" name="{$competitor1}/{$competitor2} &amp; yes"/>
<outcome id="1721" name="{$competitor1}/{$competitor2} &amp; no"/>
<outcome id="1722" name="draw/{$competitor2} &amp; yes"/>
<outcome id="1723" name="draw/{$competitor2} &amp; no"/>
</outcomes>
</market>
<market id="1088" name="Scoring type {!pointnr} point (incl. overtime)" groups="all">
<outcomes>
<outcome id="2008" name="{$competitor1} 1 point score"/>
<outcome id="2009" name="{$competitor1} 2 point score"/>
<outcome id="2010" name="{$competitor1} 3 point score"/>
<outcome id="2011" name="{$competitor2} 1 point score"/>
<outcome id="2012" name="{$competitor2} 2 point score"/>
<outcome id="2013" name="{$competitor2} 3 point score"/>
</outcomes>
<specifiers>
<specifier name="pointnr" type="integer"/>
</specifiers>
</market>
<market id="399" name="{$competitor1} goal range" groups="all|score|regular_play">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="1173" name="Winner (including OT) &amp; Total (including OT)" groups="all">
<outcomes>
<outcome id="973" name="{$competitor1} &amp; over {total}"/>
<outcome id="974" name="{$competitor2} &amp; over {total}"/>
<outcome id="975" name="{$competitor1} &amp; under {total}"/>
<outcome id="976" name="{$competitor2} &amp; under {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="23" name="{$competitor1} exact goals" groups="all|score|regular_play">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="490" name="1st half - odd/even tries" groups="all|1st_half|tries">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
</market>
<market id="961" name="{!freethrownr} free throw scored" groups="all">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="freethrownr" type="integer"/>
</specifiers>
</market>
<market id="485" name="1st half - try handicap {hcp}" groups="all|1st_half|tries">
<outcomes>
<outcome id="1711" name="{$competitor1} ({hcp})"/>
<outcome id="1712" name="draw ({hcp})"/>
<outcome id="1713" name="{$competitor2} ({hcp})"/>
</outcomes>
<specifiers>
<specifier name="hcp" type="string"/>
</specifiers>
</market>
<market id="37" name="1x2 &amp; total" groups="all|regular_play|combo|incl_ot">
<outcomes>
<outcome id="794" name="{$competitor1} &amp; under {total}"/>
<outcome id="796" name="{$competitor1} &amp; over {total}"/>
<outcome id="798" name="draw &amp; under {total}"/>
<outcome id="800" name="draw &amp; over {total}"/>
<outcome id="802" name="{$competitor2} &amp; under {total}"/>
<outcome id="804" name="{$competitor2} &amp; over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="834" name="Standard bet (incl. bonus ball in additional drum)" groups="all">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="855" name="{$competitor1} or under {total}" groups="all|regular_play|combo">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="788" name="Batter runs + runs batted in (incl. extra innings)" groups="all|incl_ei|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="229" name="Odd/even (incl. overtime)" groups="all|score|incl_ot">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
</market>
<market id="806" name="{$event} - bottom {pos} finish" groups="all|league" includes_outcomes_of_type="sr:competitor">
<specifiers>
<specifier name="pos" type="integer"/>
</specifiers>
</market>
<market id="959" name="{!drivenr} drive - result" groups="all">
<outcomes>
<outcome id="1961" name="field goal attempt"/>
<outcome id="1962" name="offensive touchdown"/>
<outcome id="1963" name="punt"/>
<outcome id="1964" name="turnover"/>
<outcome id="1965" name="safety"/>
</outcomes>
<specifiers>
<specifier name="drivenr" type="integer"/>
</specifiers>
</market>
<market id="1059" name="{$competitor1} {!c1scorenr} scoring type" groups="all|score|regular_play">
<specifiers>
<specifier name="c1scorenr" type="integer"/>
<specifier name="variant" type="string"/>
</specifiers>
</market>
<market id="363" name="{!inningnr} innings over {overnr} - {!deliverynr} delivery {$competitor2} total" groups="all|score|delivery">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="overnr" type="integer"/>
<specifier name="deliverynr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="209" name="{!setnr} set - {!gamenrX} and {!gamenrY} game winner" groups="all|score|game">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="gamenrX" type="integer"/>
<specifier name="gamenrY" type="integer"/>
</specifiers>
</market>
<market id="769" name="Player field goals (incl. overtime)" groups="all|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="1101" name="Winner (teams)" groups="all" includes_outcomes_of_type="sr:competitor" outcome_type="competitor"/>
<market id="882" name="{%player} to score (incl. overtime)" groups="all|scorers|incl_ot">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="player" type="string"/>
</specifiers>
</market>
<market id="100" name="When will the {!goalnr} goal be scored (15 min interval)" groups="all|score|regular_play">
<outcomes>
<outcome id="584" name="1-15"/>
<outcome id="586" name="16-30"/>
<outcome id="588" name="31-45"/>
<outcome id="590" name="46-60"/>
<outcome id="592" name="61-75"/>
<outcome id="594" name="76-90"/>
<outcome id="596" name="none"/>
</outcomes>
<specifiers>
<specifier name="goalnr" type="integer"/>
</specifiers>
</market>
<market id="648" name="Most sixes" groups="all">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="341" name="Will there be a super over" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="133" name="Penalty shootout - {$competitor2} odd/even" groups="all|score|pen_so">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
</market>
<market id="505" name="{!framenr} frame - highest break" groups="all|frame|break">
<outcomes>
<outcome id="1667" name="0-49"/>
<outcome id="1668" name="50-99"/>
<outcome id="1669" name="100-119"/>
<outcome id="1670" name="120-146"/>
<outcome id="1671" name="147+"/>
</outcomes>
<specifiers>
<specifier name="framenr" type="integer"/>
</specifiers>
</market>
<market id="407" name="{!goalnr} goal (incl. overtime and penalties)" groups="all|score|incl_ot_and_pen">
<outcomes>
<outcome id="6" name="{$competitor1}"/>
<outcome id="7" name="none"/>
<outcome id="8" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="goalnr" type="integer"/>
</specifiers>
</market>
<market id="920" name="{%player} total receptions (incl. overtime)" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="player" type="string"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="203" name="{!setnr} set - game handicap" groups="all|score|set">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="336" name="{!mapnr} map - correct score (in rounds)" groups="all">
<specifiers>
<specifier name="mapnr" type="integer"/>
</specifiers>
</market>
<market id="751" name="{!inningnr} inning - both teams to score" groups="all|score|inning">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="298" name="Point range" groups="all|score|regular_play">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="1108" name="{!lapnr} lap - total retirements" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="lapnr" type="integer" description="lap number"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="795" name="Player run meters" groups="all|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="668" name="{!inningnr} innings - {$competitor1} total run outs" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="total" type="decimal"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="960" name="{!quarternr} quarter - last point" groups="all">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="quarternr" type="integer"/>
</specifiers>
</market>
<market id="798" name="{$event} matchday {matchday} - away teams total" groups="all|matchday">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="matchday" type="integer"/>
</specifiers>
</market>
<market id="51" name="{$competitor2} to win either half" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="1122" name="{$competitor2} total home runs (incl. extra innings)" groups="all|incl_ei|home_run">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="659" name="{!inningnr} innings - {$competitor2} total fours" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="total" type="decimal"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="372" name="{!setnr} set - total legs" groups="all|score|set">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="435" name="{$competitor1} to win all periods" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="478" name="Total tries" groups="all|regular_play|tries">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="472" name="1st half - race to {pointnr} points" groups="all|score|1st_half">
<outcomes>
<outcome id="6" name="{$competitor1}"/>
<outcome id="7" name="none"/>
<outcome id="8" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="pointnr" type="integer"/>
</specifiers>
</market>
<market id="462" name="{!periodnr} period - odd/even" groups="all|score|period">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
<specifiers>
<specifier name="periodnr" type="integer"/>
</specifiers>
</market>
<market id="1080" name="{!inningnr} inning - {$competitor2} exact strikeouts" groups="all">
<outcomes>
<outcome id="1999" name="0"/>
<outcome id="2000" name="1"/>
<outcome id="2001" name="2"/>
<outcome id="2002" name="3"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="616" name="{$competitor1} odd/even (incl. overtime)" groups="all|score|incl_ot">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
</market>
<market id="981" name="{$competitor2} joy of six spread" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
<specifiers>
<specifier name="player1" type="string"/>
<specifier name="player2" type="string"/>
<specifier name="player3" type="string"/>
</specifiers>
</market>
<market id="863" name="{$competitor1} or any clean sheet" groups="all|regular_play|combo">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="434" name="{$competitor2} highest scoring period" groups="all|score|regular_play">
<outcomes>
<outcome id="1621" name="1st period"/>
<outcome id="1622" name="2nd period"/>
<outcome id="1623" name="3rd period"/>
<outcome id="1624" name="equal"/>
</outcomes>
</market>
<market id="251" name="Winner (incl. extra innings)" groups="all|score|incl_ei">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
</market>
<market id="230" name="Race to {pointnr} points (incl. overtime)" groups="all|score|incl_ot">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="pointnr" type="integer"/>
</specifiers>
</market>
<market id="1005" name="3 ball" groups="all" includes_outcomes_of_type="sr:competitor" outcome_type="competitor">
<attributes>
<attribute name="is_golf_stroke_play_market" description="This market is applicable to Golf stroke play"/>
</attributes>
</market>
<market id="805" name="{$event} - top {pos} finish" groups="all|league" includes_outcomes_of_type="sr:competitor">
<specifiers>
<specifier name="pos" type="integer"/>
</specifiers>
</market>
<market id="327" name="Map handicap" groups="all|score|regular_play">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="627" name="{!mapnr} map round {roundnr} - kill handicap" groups="all|kills|round">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
<specifier name="roundnr" type="integer"/>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="453" name="{!periodnr} period - which team to score" groups="all|score|period">
<outcomes>
<outcome id="784" name="none"/>
<outcome id="788" name="only {$competitor1}"/>
<outcome id="790" name="only {$competitor2}"/>
<outcome id="792" name="both teams"/>
</outcomes>
<specifiers>
<specifier name="periodnr" type="integer"/>
</specifiers>
</market>
<market id="778" name="Player passes (incl. overtime)" groups="all|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="451" name="{!periodnr} period - {$competitor2} exact goals" groups="all|score|period">
<specifiers>
<specifier name="periodnr" type="integer"/>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="256" name="Handicap (incl. extra innings)" groups="all|score|incl_ei">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="189" name="Total games" groups="all|score|regular_play">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="92" name="2nd half - {$competitor2} total" groups="all|score|2nd_half">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="161" name="1st half - {$competitor2} sending off" groups="all|1st_half|bookings">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="777" name="Player shots on goal (incl. overtime)" groups="all|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="545" name="2nd half - double chance &amp; both teams to score" groups="all|combo|2nd_half">
<outcomes>
<outcome id="1718" name="{$competitor1}/draw &amp; yes"/>
<outcome id="1719" name="{$competitor1}/draw &amp; no"/>
<outcome id="1720" name="{$competitor1}/{$competitor2} &amp; yes"/>
<outcome id="1721" name="{$competitor1}/{$competitor2} &amp; no"/>
<outcome id="1722" name="draw/{$competitor2} &amp; yes"/>
<outcome id="1723" name="draw/{$competitor2} &amp; no"/>
</outcomes>
</market>
<market id="725" name="{!mapnr} map - duration" groups="all|map|progress">
<outcomes>
<outcome id="1831" name="00:00 - {(minute-1)}:59"/>
<outcome id="1832" name="{minute}:00+"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
<specifier name="minute" type="integer"/>
</specifiers>
</market>
<market id="1031" name="Holes {from} to {to} - 2 ball (1x2) most pars" groups="all">
<outcomes>
<outcome id="1966" name="{%competitor1}"/>
<outcome id="1967" name="draw"/>
<outcome id="1968" name="{%competitor2}"/>
</outcomes>
<specifiers>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
<specifier name="competitor1" type="string"/>
<specifier name="competitor2" type="string"/>
</specifiers>
<attributes>
<attribute name="is_golf_stroke_play_market" description="This market is applicable to Golf stroke play"/>
</attributes>
</market>
<market id="890" name="{!goalnr} goalscorer &amp; correct score" groups="all|regular_play|combo">
<outcomes>
<outcome id="1898" name="{%player} &amp; 1:0"/>
<outcome id="1899" name="{%player} &amp; 2:0"/>
<outcome id="1900" name="{%player} &amp; 3:0"/>
<outcome id="1901" name="{%player} &amp; 4:0"/>
<outcome id="1902" name="{%player} &amp; 2:1"/>
<outcome id="1903" name="{%player} &amp; 3:1"/>
<outcome id="1904" name="{%player} &amp; 4:1"/>
<outcome id="1905" name="{%player} &amp; 3:2"/>
<outcome id="1906" name="{%player} &amp; 4:2"/>
<outcome id="1907" name="{%player} &amp; 4:3"/>
<outcome id="1908" name="{%player} &amp; 0:1"/>
<outcome id="1909" name="{%player} &amp; 0:2"/>
<outcome id="1910" name="{%player} &amp; 0:3"/>
<outcome id="1911" name="{%player} &amp; 0:4"/>
<outcome id="1912" name="{%player} &amp; 1:2"/>
<outcome id="1913" name="{%player} &amp; 1:3"/>
<outcome id="1914" name="{%player} &amp; 1:4"/>
<outcome id="1915" name="{%player} &amp; 2:3"/>
<outcome id="1916" name="{%player} &amp; 2:4"/>
<outcome id="1917" name="{%player} &amp; 3:4"/>
<outcome id="1918" name="{%player} &amp; 1:1"/>
<outcome id="1919" name="{%player} &amp; 2:2"/>
<outcome id="1920" name="{%player} &amp; 3:3"/>
<outcome id="1921" name="{%player} &amp; 4:4"/>
<outcome id="1922" name="other"/>
</outcomes>
<specifiers>
<specifier name="goalnr" type="integer"/>
<specifier name="player" type="string"/>
</specifiers>
</market>
<market id="797" name="{$event} matchday {matchday} - home teams total" groups="all|matchday">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="matchday" type="integer"/>
</specifiers>
</market>
<market id="679" name="{!inningnr} innings - {$competitor2} last player standing" groups="all" includes_outcomes_of_type="sr:player">
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="52" name="Highest scoring half" groups="all|score|regular_play">
<outcomes>
<outcome id="436" name="1st half"/>
<outcome id="438" name="2nd half"/>
<outcome id="440" name="equal"/>
</outcomes>
</market>
<market id="642" name="{!inningnr} innings over {overnr} - {$competitor2} dismissal" groups="all">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="overnr" type="integer"/>
</specifiers>
</market>
<market id="891" name="{!goalnr} goalscorer &amp; 1x2" groups="all|regular_play|combo">
<outcomes>
<outcome id="1894" name="{%player} &amp; {$competitor1}"/>
<outcome id="1895" name="{%player} &amp; draw"/>
<outcome id="1896" name="{%player} &amp; {$competitor2}"/>
<outcome id="1897" name="other"/>
</outcomes>
<specifiers>
<specifier name="goalnr" type="integer"/>
<specifier name="player" type="string"/>
</specifiers>
</market>
<market id="676" name="{!inningnr} innings - {$competitor1} top bowler" groups="all" includes_outcomes_of_type="sr:player">
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="maxovers" type="integer"/>
<specifier name="type" type="string"/>
</specifiers>
</market>
<market id="635" name="1st half - goal range" groups="all|score|1st_half">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="933" name="Total turnovers (incl. overtime)" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="424" name="1x2 &amp; total (incl. overtime and penalties)" groups="all|combo|incl_ot_and_pen">
<outcomes>
<outcome id="794" name="{$competitor1} &amp; under {total}"/>
<outcome id="796" name="{$competitor1} &amp; over {total}"/>
<outcome id="798" name="draw &amp; under {total}"/>
<outcome id="800" name="draw &amp; over {total}"/>
<outcome id="802" name="{$competitor2} &amp; under {total}"/>
<outcome id="804" name="{$competitor2} &amp; over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="353" name="{!inningnr} innings overs 0 to {overnr} - {$competitor2} total" groups="all|score|x_overs">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="overnr" type="integer"/>
<specifier name="total" type="decimal"/>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="396" name="{!mapnr} map - {!xth} aegis" groups="all|map|structures">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
<specifier name="xth" type="integer"/>
</specifiers>
</market>
<market id="588" name="15 minutes - sending off from {from} to {to}" groups="all|15_min|bookings">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="538" name="Head2head (1x2)" groups="all" includes_outcomes_of_type="sr:competitor">
<outcomes>
<outcome id="1717" name="draw"/>
</outcomes>
<specifiers>
<specifier name="id" type="string"/>
</specifiers>
</market>
<market id="406" name="Winner (incl. overtime and penalties)" groups="all|score|incl_ot_and_pen">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
</market>
<market id="382" name="{!xth} player to score a 180" groups="all|regular_play|180s">
<outcomes>
<outcome id="6" name="{$competitor1}"/>
<outcome id="7" name="none"/>
<outcome id="8" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="xth" type="integer"/>
</specifiers>
</market>
<market id="1150" name="Overs 0 to {overnr} - {$competitor1} total ({ballcount}-ball overs)" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="overnr" type="integer"/>
<specifier name="ballcount" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="815" name="{!mapnr} map - assist draw no bet" groups="all">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
</specifiers>
</market>
<market id="471" name="1st half - point range" groups="all|score|1st_half">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="578" name="10 minutes - odd/even corners from {from} to {to}" groups="all|10_min|corners">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
<specifiers>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="559" name="Free text market" groups="all" includes_outcomes_of_type="pre:outcometext">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="570" name="15 minutes - {$competitor2} total corners from {from} to {to}" groups="all|15_min|corners">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="147" name="{$competitor1} sending off" groups="all|regular_play|bookings">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="753" name="{!quarternr} quarter - double chance" groups="all|score|quarter">
<outcomes>
<outcome id="9" name="{$competitor1} or draw"/>
<outcome id="10" name="{$competitor1} or {$competitor2}"/>
<outcome id="11" name="draw or {$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="quarternr" type="integer"/>
</specifiers>
</market>
<market id="715" name="{!inningnr} innings over {overnr} - {$competitor1} run range" groups="all">
<specifiers>
<specifier name="overnr" type="integer"/>
<specifier name="variant" type="variable_text"/>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="770" name="Player assists (incl. overtime)" groups="all|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="499" name="{!framenr} frame - winner" groups="all|score|frame">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="framenr" type="integer"/>
</specifiers>
</market>
<market id="442" name="All periods under {total}" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="85" name="2nd half - double chance" groups="all|score|2nd_half">
<outcomes>
<outcome id="9" name="{$competitor1} or draw"/>
<outcome id="10" name="{$competitor1} or {$competitor2}"/>
<outcome id="11" name="draw or {$competitor2}"/>
</outcomes>
</market>
<market id="1159" name="Winner" groups="all" includes_outcomes_of_type="sr:competitor" outcome_type="competitor">
<specifiers>
<specifier name="version" type="string"/>
</specifiers>
<attributes>
<attribute name="is_golf_stroke_play_market" description="This market is applicable to Golf stroke play"/>
</attributes>
</market>
<market id="862" name="{$competitor2} or both teams to score" groups="all|regular_play|combo">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="175" name="1st half - last corner" groups="all|1st_half|corners">
<outcomes>
<outcome id="6" name="{$competitor1}"/>
<outcome id="7" name="none"/>
<outcome id="8" name="{$competitor2}"/>
</outcomes>
</market>
<market id="919" name="{%player} total receiving yards (incl. overtime)" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="player" type="string"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="924" name="{%player} total 3-point field goals (incl. overtime)" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="player" type="string"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="416" name="Exact goals (incl. overtime and penalties)" groups="all|score|incl_ot_and_pen">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="443" name="{!periodnr} period - 1x2" groups="all|score|period">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="periodnr" type="integer"/>
</specifiers>
</market>
<market id="910" name="Winning method" groups="all">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="217" name="{!setnr} set game {gamenr} - first {pointnr} points winner" groups="all|score|points">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="gamenr" type="integer"/>
<specifier name="pointnr" type="integer"/>
</specifiers>
</market>
<market id="551" name="Multiscores" groups="all|score|regular_play">
<outcomes>
<outcome id="1750" name="1:0, 2:0 or 3:0"/>
<outcome id="1751" name="0:1, 0:2 or 0:3"/>
<outcome id="1752" name="4:0, 5:0 or 6:0"/>
<outcome id="1753" name="0:4, 0:5 or 0:6"/>
<outcome id="1754" name="2:1, 3:1 or 4:1"/>
<outcome id="1755" name="1:2, 1:3 or 1:4"/>
<outcome id="1756" name="3:2, 4:2, 4:3 or 5:1"/>
<outcome id="1757" name="2:3, 2:4, 3:4 or 1:5"/>
<outcome id="1758" name="other homewin"/>
<outcome id="1759" name="other awaywin"/>
<outcome id="1803" name="draw"/>
</outcomes>
</market>
<market id="215" name="{!setnr} set game {gamenr} - correct score or break" groups="all|score|game">
<outcomes>
<outcome id="894" name="{%server} to 0"/>
<outcome id="895" name="{%server} to 15"/>
<outcome id="896" name="{%server} to 30"/>
<outcome id="897" name="{%server} to 40"/>
<outcome id="898" name="break"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="gamenr" type="integer"/>
<specifier name="server" type="string"/>
</specifiers>
</market>
<market id="153" name="1st half - {$competitor1} total bookings" groups="all|1st_half|bookings">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="823" name="Draw sum range" groups="all">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="58" name="Both halves over {total}" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="238" name="Total points" groups="all|score|regular_play">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="1071" name="{!inningnr} inning - most balls" groups="all">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="397" name="{!mapnr} map - {!xth} tower" groups="all|map|structures">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
<specifier name="xth" type="integer"/>
</specifiers>
</market>
<market id="15" name="Winning margin" groups="all|score|regular_play">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="1079" name="{!inningnr} inning - {$competitor1} exact strikeouts" groups="all">
<outcomes>
<outcome id="1999" name="0"/>
<outcome id="2000" name="1"/>
<outcome id="2001" name="2"/>
<outcome id="2002" name="3"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="317" name="{!setnr} set - handicap {hcp}" groups="all|score|set">
<outcomes>
<outcome id="1711" name="{$competitor1} ({hcp})"/>
<outcome id="1712" name="draw ({hcp})"/>
<outcome id="1713" name="{$competitor2} ({hcp})"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="hcp" type="string"/>
</specifiers>
</market>
<market id="550" name="{$competitor2} multigoals" groups="all|score|regular_play">
<outcomes>
<outcome id="1746" name="1-2"/>
<outcome id="1747" name="1-3"/>
<outcome id="1748" name="2-3"/>
<outcome id="1749" name="4+"/>
<outcome id="1805" name="no goal"/>
</outcomes>
</market>
<market id="348" name="{!inningnr} innings - run range in the highest scoring over" groups="all|score|innings">
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="134" name="Penalty shootout - winner &amp; total" groups="all|combo|pen_so">
<outcomes>
<outcome id="656" name="{$competitor1} &amp; under {total}"/>
<outcome id="658" name="{$competitor2} &amp; under {total}"/>
<outcome id="660" name="{$competitor1} &amp; over {total}"/>
<outcome id="662" name="{$competitor2} &amp; over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="63" name="1st half - double chance" groups="all|score|1st_half">
<outcomes>
<outcome id="9" name="{$competitor1} or draw"/>
<outcome id="10" name="{$competitor1} or {$competitor2}"/>
<outcome id="11" name="draw or {$competitor2}"/>
</outcomes>
</market>
<market id="607" name="{!inningnr} innings - {$competitor2} total" groups="all|score|innings">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="845" name="{!inningnr} innings overs 0 to {overnr} - {$competitor2} total spread" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="overnr" type="integer"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
<attributes>
<attribute name="is_spread_market" description="This is a spread-market and special client-side spread rules need to be followed for odds-changes and bet-settlements"/>
</attributes>
</market>
<market id="210" name="{!setnr} set game {gamenr} - winner" groups="all|score|game">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="gamenr" type="integer"/>
</specifiers>
</market>
<market id="1136" name="{!inningnr} innings over {overnr} - {!deliverynr} delivery {$competitor1} exact runs" groups="all">
<outcomes>
<outcome id="2026" name="0"/>
<outcome id="2027" name="1"/>
<outcome id="2028" name="2"/>
<outcome id="2029" name="3"/>
<outcome id="2030" name="4"/>
<outcome id="2031" name="6"/>
<outcome id="2032" name="other"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="overnr" type="integer"/>
<specifier name="deliverynr" type="integer"/>
</specifiers>
</market>
<market id="666" name="{!inningnr} innings - {$competitor1} total extras" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="total" type="decimal"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="883" name="1 minute - total from {from} to {to}" groups="all|score|rapid_market">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="from" type="string"/>
<specifier name="to" type="string"/>
</specifiers>
</market>
<market id="852" name="{$competitor1} to win exactly 2 sets" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="9" name="Last goal" groups="all|score|regular_play">
<outcomes>
<outcome id="6" name="{$competitor1}"/>
<outcome id="7" name="none"/>
<outcome id="8" name="{$competitor2}"/>
</outcomes>
</market>
<market id="450" name="{!periodnr} period - {$competitor1} exact goals" groups="all|score|period">
<specifiers>
<specifier name="periodnr" type="integer"/>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="280" name="Innings 1 to 5th top - handicap" groups="all|score|4.5_innings">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="86" name="2nd half - draw no bet" groups="all|score|2nd_half">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
</market>
<market id="381" name="Most 180s" groups="all|regular_play|180s">
<outcomes>
<outcome id="1041" name="{$competitor1}"/>
<outcome id="1042" name="{$competitor2}"/>
<outcome id="1043" name="draw"/>
</outcomes>
</market>
<market id="880" name="{$competitor1} to win" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="389" name="{!setnr} set - {$competitor1} total 180s" groups="all|set|180s">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="738" name="When will the match be decided" groups="all|score|regular_play">
<outcomes>
<outcome id="1823" name="top of 9th inning"/>
<outcome id="1824" name="bottom of 9th inning"/>
<outcome id="1825" name="any extra inning"/>
</outcomes>
</market>
<market id="1023" name="Hole {holenr} - 2 ball (1x2)" groups="all">
<outcomes>
<outcome id="1966" name="{%competitor1}"/>
<outcome id="1967" name="draw"/>
<outcome id="1968" name="{%competitor2}"/>
</outcomes>
<specifiers>
<specifier name="holenr" type="integer"/>
<specifier name="competitor1" type="string"/>
<specifier name="competitor2" type="string"/>
</specifiers>
<attributes>
<attribute name="is_golf_stroke_play_market" description="This market is applicable to Golf stroke play"/>
</attributes>
</market>
<market id="109" name="5 minutes - {!goalnr} goal from {from} to {to}" groups="all|score|5_min">
<outcomes>
<outcome id="6" name="{$competitor1}"/>
<outcome id="7" name="none"/>
<outcome id="8" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="goalnr" type="integer"/>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="704" name="{!inningnr} innings - {$competitor1} total wides bowled" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="total" type="decimal"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="202" name="{!setnr} set - winner" groups="all|score|set">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
</specifiers>
</market>
<market id="464" name="Overtime - draw no bet" groups="all|score|ot">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
</market>
<market id="573" name="10 minutes - {!cornernr} corner from {from} to {to}" groups="all|10_min|corners">
<outcomes>
<outcome id="6" name="{$competitor1}"/>
<outcome id="7" name="none"/>
<outcome id="8" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="cornernr" type="integer"/>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="120" name="Overtime 1st half - handicap" groups="all|score|ot_1st_half">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="64" name="1st half - draw no bet" groups="all|score|1st_half">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
</market>
<market id="144" name="{$competitor2} exact bookings" groups="all|regular_play|bookings">
<outcomes>
<outcome id="730" name="0-1"/>
<outcome id="732" name="2"/>
<outcome id="734" name="3"/>
<outcome id="736" name="4+"/>
</outcomes>
</market>
<market id="516" name="{!framenr} frame - last points scored" groups="all|score|frame">
<outcomes>
<outcome id="1678" name="red"/>
<outcome id="1679" name="yellow"/>
<outcome id="1680" name="green"/>
<outcome id="1681" name="brown"/>
<outcome id="1682" name="blue"/>
<outcome id="1683" name="pink"/>
<outcome id="1684" name="black"/>
<outcome id="1685" name="foul"/>
</outcomes>
<specifiers>
<specifier name="framenr" type="integer"/>
</specifiers>
</market>
<market id="658" name="{!inningnr} innings - {$competitor1} total fours" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="total" type="decimal"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="731" name="{!mapnr} map - race to {xth} kills" groups="all|kills|map">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
<specifier name="xth" type="integer"/>
</specifiers>
</market>
<market id="1099" name="Total finishers" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="54" name="{$competitor2} highest scoring half" groups="all|score|regular_play">
<outcomes>
<outcome id="436" name="1st half"/>
<outcome id="438" name="2nd half"/>
<outcome id="440" name="equal"/>
</outcomes>
</market>
<market id="626" name="{!mapnr} map round {roundnr} - total kills" groups="all|kills|round">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
<specifier name="roundnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="224" name="US spread (incl. overtime)" groups="all|score|incl_ot">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="71" name="1st half - exact goals" groups="all|score|1st_half">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="375" name="{!setnr} set leg {legnr} - winner" groups="all|score|leg">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="legnr" type="integer"/>
</specifiers>
</market>
<market id="687" name="Bowler head2head (1x2)" groups="all">
<outcomes>
<outcome id="1818" name="{%player1}"/>
<outcome id="1819" name="draw"/>
<outcome id="1820" name="{%player2}"/>
</outcomes>
<specifiers>
<specifier name="player1" type="string"/>
<specifier name="player2" type="string"/>
</specifiers>
</market>
<market id="693" name="{!inningnr} innings - {$competitor2} to finish with a boundary" groups="all">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="1158" name="2 ball" groups="all">
<outcomes>
<outcome id="2033" name="{%competitor1}"/>
<outcome id="2034" name="{%competitor2}"/>
</outcomes>
<specifiers>
<specifier name="competitor1" type="string"/>
<specifier name="competitor2" type="string"/>
</specifiers>
<attributes>
<attribute name="is_golf_stroke_play_market" description="This market is applicable to Golf stroke play"/>
</attributes>
</market>
<market id="598" name="5 minutes - sending off from {from} to {to}" groups="all|5_min|bookings">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="1144" name="{!inningnr} innings over {overnr} - {$competitor2} to score a boundary four &amp; a six" groups="all">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="overnr" type="integer"/>
</specifiers>
</market>
<market id="107" name="10 minutes - total from {from} to {to}" groups="all|score|10_min">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="530" name="1st half - {!scorenr} score" groups="all">
<outcomes>
<outcome id="6" name="{$competitor1}"/>
<outcome id="7" name="none"/>
<outcome id="8" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="scorenr" type="integer"/>
</specifiers>
</market>
<market id="183" name="1st half - odd/even corners" groups="all|1st_half|corners">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
</market>
<market id="780" name="Player tackles (incl. overtime)" groups="all|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="232" name="2nd half - total (incl. overtime)" groups="all|score|combo|incl_ot|2nd_half_incl_ot">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="401" name="Correct score [{score}]" groups="all|score|regular_play">
<outcomes>
<outcome id="1058" name="0:0"/>
<outcome id="1059" name="0:1"/>
<outcome id="1060" name="0:2"/>
<outcome id="1061" name="0:3"/>
<outcome id="1062" name="0:4"/>
<outcome id="1063" name="0:5"/>
<outcome id="1064" name="0:6"/>
<outcome id="1065" name="0:7"/>
<outcome id="1066" name="0:8"/>
<outcome id="1067" name="0:9"/>
<outcome id="1068" name="0:10"/>
<outcome id="1069" name="0:11"/>
<outcome id="1070" name="0:12"/>
<outcome id="1071" name="0:13"/>
<outcome id="1072" name="0:14"/>
<outcome id="1073" name="0:15"/>
<outcome id="1074" name="0:16"/>
<outcome id="1075" name="0:17"/>
<outcome id="1076" name="0:18"/>
<outcome id="1077" name="0:19"/>
<outcome id="1078" name="1:0"/>
<outcome id="1079" name="1:1"/>
<outcome id="1080" name="1:2"/>
<outcome id="1081" name="1:3"/>
<outcome id="1082" name="1:4"/>
<outcome id="1083" name="1:5"/>
<outcome id="1084" name="1:6"/>
<outcome id="1085" name="1:7"/>
<outcome id="1086" name="1:8"/>
<outcome id="1087" name="1:9"/>
<outcome id="1088" name="1:10"/>
<outcome id="1089" name="1:11"/>
<outcome id="1090" name="1:12"/>
<outcome id="1091" name="1:13"/>
<outcome id="1092" name="1:14"/>
<outcome id="1093" name="1:15"/>
<outcome id="1094" name="1:16"/>
<outcome id="1095" name="1:17"/>
<outcome id="1096" name="1:18"/>
<outcome id="1097" name="1:19"/>
<outcome id="1098" name="2:0"/>
<outcome id="1099" name="2:1"/>
<outcome id="1100" name="2:2"/>
<outcome id="1101" name="2:3"/>
<outcome id="1102" name="2:4"/>
<outcome id="1103" name="2:5"/>
<outcome id="1104" name="2:6"/>
<outcome id="1105" name="2:7"/>
<outcome id="1106" name="2:8"/>
<outcome id="1107" name="2:9"/>
<outcome id="1108" name="2:10"/>
<outcome id="1109" name="2:11"/>
<outcome id="1110" name="2:12"/>
<outcome id="1111" name="2:13"/>
<outcome id="1112" name="2:14"/>
<outcome id="1113" name="2:15"/>
<outcome id="1114" name="2:16"/>
<outcome id="1115" name="2:17"/>
<outcome id="1116" name="2:18"/>
<outcome id="1117" name="2:19"/>
<outcome id="1118" name="3:0"/>
<outcome id="1119" name="3:1"/>
<outcome id="1120" name="3:2"/>
<outcome id="1121" name="3:3"/>
<outcome id="1122" name="3:4"/>
<outcome id="1123" name="3:5"/>
<outcome id="1124" name="3:6"/>
<outcome id="1125" name="3:7"/>
<outcome id="1126" name="3:8"/>
<outcome id="1127" name="3:9"/>
<outcome id="1128" name="3:10"/>
<outcome id="1129" name="3:11"/>
<outcome id="1130" name="3:12"/>
<outcome id="1131" name="3:13"/>
<outcome id="1132" name="3:14"/>
<outcome id="1133" name="3:15"/>
<outcome id="1134" name="3:16"/>
<outcome id="1135" name="3:17"/>
<outcome id="1136" name="3:18"/>
<outcome id="1137" name="3:19"/>
<outcome id="1138" name="4:0"/>
<outcome id="1139" name="4:1"/>
<outcome id="1140" name="4:2"/>
<outcome id="1141" name="4:3"/>
<outcome id="1142" name="4:4"/>
<outcome id="1143" name="4:5"/>
<outcome id="1144" name="4:6"/>
<outcome id="1145" name="4:7"/>
<outcome id="1146" name="4:8"/>
<outcome id="1147" name="4:9"/>
<outcome id="1148" name="4:10"/>
<outcome id="1149" name="4:11"/>
<outcome id="1150" name="4:12"/>
<outcome id="1151" name="4:13"/>
<outcome id="1152" name="4:14"/>
<outcome id="1153" name="4:15"/>
<outcome id="1154" name="4:16"/>
<outcome id="1155" name="4:17"/>
<outcome id="1156" name="4:18"/>
<outcome id="1157" name="4:19"/>
<outcome id="1158" name="5:0"/>
<outcome id="1159" name="5:1"/>
<outcome id="1160" name="5:2"/>
<outcome id="1161" name="5:3"/>
<outcome id="1162" name="5:4"/>
<outcome id="1163" name="5:5"/>
<outcome id="1164" name="5:6"/>
<outcome id="1165" name="5:7"/>
<outcome id="1166" name="5:8"/>
<outcome id="1167" name="5:9"/>
<outcome id="1168" name="5:10"/>
<outcome id="1169" name="5:11"/>
<outcome id="1170" name="5:12"/>
<outcome id="1171" name="5:13"/>
<outcome id="1172" name="5:14"/>
<outcome id="1173" name="5:15"/>
<outcome id="1174" name="5:16"/>
<outcome id="1175" name="5:17"/>
<outcome id="1176" name="5:18"/>
<outcome id="1177" name="5:19"/>
<outcome id="1178" name="6:0"/>
<outcome id="1179" name="6:1"/>
<outcome id="1180" name="6:2"/>
<outcome id="1181" name="6:3"/>
<outcome id="1182" name="6:4"/>
<outcome id="1183" name="6:5"/>
<outcome id="1184" name="6:6"/>
<outcome id="1185" name="6:7"/>
<outcome id="1186" name="6:8"/>
<outcome id="1187" name="6:9"/>
<outcome id="1188" name="6:10"/>
<outcome id="1189" name="6:11"/>
<outcome id="1190" name="6:12"/>
<outcome id="1191" name="6:13"/>
<outcome id="1192" name="6:14"/>
<outcome id="1193" name="6:15"/>
<outcome id="1194" name="6:16"/>
<outcome id="1195" name="6:17"/>
<outcome id="1196" name="6:18"/>
<outcome id="1197" name="6:19"/>
<outcome id="1198" name="7:0"/>
<outcome id="1199" name="7:1"/>
<outcome id="1200" name="7:2"/>
<outcome id="1201" name="7:3"/>
<outcome id="1202" name="7:4"/>
<outcome id="1203" name="7:5"/>
<outcome id="1204" name="7:6"/>
<outcome id="1205" name="7:7"/>
<outcome id="1206" name="7:8"/>
<outcome id="1207" name="7:9"/>
<outcome id="1208" name="7:10"/>
<outcome id="1209" name="7:11"/>
<outcome id="1210" name="7:12"/>
<outcome id="1211" name="7:13"/>
<outcome id="1212" name="7:14"/>
<outcome id="1213" name="7:15"/>
<outcome id="1214" name="7:16"/>
<outcome id="1215" name="7:17"/>
<outcome id="1216" name="7:18"/>
<outcome id="1217" name="7:19"/>
<outcome id="1218" name="8:0"/>
<outcome id="1219" name="8:1"/>
<outcome id="1220" name="8:2"/>
<outcome id="1221" name="8:3"/>
<outcome id="1222" name="8:4"/>
<outcome id="1223" name="8:5"/>
<outcome id="1224" name="8:6"/>
<outcome id="1225" name="8:7"/>
<outcome id="1226" name="8:8"/>
<outcome id="1227" name="8:9"/>
<outcome id="1228" name="8:10"/>
<outcome id="1229" name="8:11"/>
<outcome id="1230" name="8:12"/>
<outcome id="1231" name="8:13"/>
<outcome id="1232" name="8:14"/>
<outcome id="1233" name="8:15"/>
<outcome id="1234" name="8:16"/>
<outcome id="1235" name="8:17"/>
<outcome id="1236" name="8:18"/>
<outcome id="1237" name="8:19"/>
<outcome id="1238" name="9:0"/>
<outcome id="1239" name="9:1"/>
<outcome id="1240" name="9:2"/>
<outcome id="1241" name="9:3"/>
<outcome id="1242" name="9:4"/>
<outcome id="1243" name="9:5"/>
<outcome id="1244" name="9:6"/>
<outcome id="1245" name="9:7"/>
<outcome id="1246" name="9:8"/>
<outcome id="1247" name="9:9"/>
<outcome id="1248" name="9:10"/>
<outcome id="1249" name="9:11"/>
<outcome id="1250" name="9:12"/>
<outcome id="1251" name="9:13"/>
<outcome id="1252" name="9:14"/>
<outcome id="1253" name="9:15"/>
<outcome id="1254" name="9:16"/>
<outcome id="1255" name="9:17"/>
<outcome id="1256" name="9:18"/>
<outcome id="1257" name="9:19"/>
<outcome id="1258" name="10:0"/>
<outcome id="1259" name="10:1"/>
<outcome id="1260" name="10:2"/>
<outcome id="1261" name="10:3"/>
<outcome id="1262" name="10:4"/>
<outcome id="1263" name="10:5"/>
<outcome id="1264" name="10:6"/>
<outcome id="1265" name="10:7"/>
<outcome id="1266" name="10:8"/>
<outcome id="1267" name="10:9"/>
<outcome id="1268" name="10:10"/>
<outcome id="1269" name="10:11"/>
<outcome id="1270" name="10:12"/>
<outcome id="1271" name="10:13"/>
<outcome id="1272" name="10:14"/>
<outcome id="1273" name="10:15"/>
<outcome id="1274" name="10:16"/>
<outcome id="1275" name="10:17"/>
<outcome id="1276" name="10:18"/>
<outcome id="1277" name="10:19"/>
<outcome id="1278" name="11:0"/>
<outcome id="1279" name="11:1"/>
<outcome id="1280" name="11:2"/>
<outcome id="1281" name="11:3"/>
<outcome id="1282" name="11:4"/>
<outcome id="1283" name="11:5"/>
<outcome id="1284" name="11:6"/>
<outcome id="1285" name="11:7"/>
<outcome id="1286" name="11:8"/>
<outcome id="1287" name="11:9"/>
<outcome id="1288" name="11:10"/>
<outcome id="1289" name="11:11"/>
<outcome id="1290" name="11:12"/>
<outcome id="1291" name="11:13"/>
<outcome id="1292" name="11:14"/>
<outcome id="1293" name="11:15"/>
<outcome id="1294" name="11:16"/>
<outcome id="1295" name="11:17"/>
<outcome id="1296" name="11:18"/>
<outcome id="1297" name="11:19"/>
<outcome id="1298" name="12:0"/>
<outcome id="1299" name="12:1"/>
<outcome id="1300" name="12:2"/>
<outcome id="1301" name="12:3"/>
<outcome id="1302" name="12:4"/>
<outcome id="1303" name="12:5"/>
<outcome id="1304" name="12:6"/>
<outcome id="1305" name="12:7"/>
<outcome id="1306" name="12:8"/>
<outcome id="1307" name="12:9"/>
<outcome id="1308" name="12:10"/>
<outcome id="1309" name="12:11"/>
<outcome id="1310" name="12:12"/>
<outcome id="1311" name="12:13"/>
<outcome id="1312" name="12:14"/>
<outcome id="1313" name="12:15"/>
<outcome id="1314" name="12:16"/>
<outcome id="1315" name="12:17"/>
<outcome id="1316" name="12:18"/>
<outcome id="1317" name="12:19"/>
<outcome id="1318" name="13:0"/>
<outcome id="1319" name="13:1"/>
<outcome id="1320" name="13:2"/>
<outcome id="1321" name="13:3"/>
<outcome id="1322" name="13:4"/>
<outcome id="1323" name="13:5"/>
<outcome id="1324" name="13:6"/>
<outcome id="1325" name="13:7"/>
<outcome id="1326" name="13:8"/>
<outcome id="1327" name="13:9"/>
<outcome id="1328" name="13:10"/>
<outcome id="1329" name="13:11"/>
<outcome id="1330" name="13:12"/>
<outcome id="1331" name="13:13"/>
<outcome id="1332" name="13:14"/>
<outcome id="1333" name="13:15"/>
<outcome id="1334" name="13:16"/>
<outcome id="1335" name="13:17"/>
<outcome id="1336" name="13:18"/>
<outcome id="1337" name="13:19"/>
<outcome id="1338" name="14:0"/>
<outcome id="1339" name="14:1"/>
<outcome id="1340" name="14:2"/>
<outcome id="1341" name="14:3"/>
<outcome id="1342" name="14:4"/>
<outcome id="1343" name="14:5"/>
<outcome id="1344" name="14:6"/>
<outcome id="1345" name="14:7"/>
<outcome id="1346" name="14:8"/>
<outcome id="1347" name="14:9"/>
<outcome id="1348" name="14:10"/>
<outcome id="1349" name="14:11"/>
<outcome id="1350" name="14:12"/>
<outcome id="1351" name="14:13"/>
<outcome id="1352" name="14:14"/>
<outcome id="1353" name="14:15"/>
<outcome id="1354" name="14:16"/>
<outcome id="1355" name="14:17"/>
<outcome id="1356" name="14:18"/>
<outcome id="1357" name="14:19"/>
<outcome id="1358" name="15:0"/>
<outcome id="1359" name="15:1"/>
<outcome id="1360" name="15:2"/>
<outcome id="1361" name="15:3"/>
<outcome id="1362" name="15:4"/>
<outcome id="1363" name="15:5"/>
<outcome id="1364" name="15:6"/>
<outcome id="1365" name="15:7"/>
<outcome id="1366" name="15:8"/>
<outcome id="1367" name="15:9"/>
<outcome id="1368" name="15:10"/>
<outcome id="1369" name="15:11"/>
<outcome id="1370" name="15:12"/>
<outcome id="1371" name="15:13"/>
<outcome id="1372" name="15:14"/>
<outcome id="1373" name="15:15"/>
<outcome id="1374" name="15:16"/>
<outcome id="1375" name="15:17"/>
<outcome id="1376" name="15:18"/>
<outcome id="1377" name="15:19"/>
<outcome id="1378" name="16:0"/>
<outcome id="1379" name="16:1"/>
<outcome id="1380" name="16:2"/>
<outcome id="1381" name="16:3"/>
<outcome id="1382" name="16:4"/>
<outcome id="1383" name="16:5"/>
<outcome id="1384" name="16:6"/>
<outcome id="1385" name="16:7"/>
<outcome id="1386" name="16:8"/>
<outcome id="1387" name="16:9"/>
<outcome id="1388" name="16:10"/>
<outcome id="1389" name="16:11"/>
<outcome id="1390" name="16:12"/>
<outcome id="1391" name="16:13"/>
<outcome id="1392" name="16:14"/>
<outcome id="1393" name="16:15"/>
<outcome id="1394" name="16:16"/>
<outcome id="1395" name="16:17"/>
<outcome id="1396" name="16:18"/>
<outcome id="1397" name="16:19"/>
<outcome id="1398" name="17:0"/>
<outcome id="1399" name="17:1"/>
<outcome id="1400" name="17:2"/>
<outcome id="1401" name="17:3"/>
<outcome id="1402" name="17:4"/>
<outcome id="1403" name="17:5"/>
<outcome id="1404" name="17:6"/>
<outcome id="1405" name="17:7"/>
<outcome id="1406" name="17:8"/>
<outcome id="1407" name="17:9"/>
<outcome id="1408" name="17:10"/>
<outcome id="1409" name="17:11"/>
<outcome id="1410" name="17:12"/>
<outcome id="1411" name="17:13"/>
<outcome id="1412" name="17:14"/>
<outcome id="1413" name="17:15"/>
<outcome id="1414" name="17:16"/>
<outcome id="1415" name="17:17"/>
<outcome id="1416" name="17:18"/>
<outcome id="1417" name="17:19"/>
<outcome id="1418" name="18:0"/>
<outcome id="1419" name="18:1"/>
<outcome id="1420" name="18:2"/>
<outcome id="1421" name="18:3"/>
<outcome id="1422" name="18:4"/>
<outcome id="1423" name="18:5"/>
<outcome id="1424" name="18:6"/>
<outcome id="1425" name="18:7"/>
<outcome id="1426" name="18:8"/>
<outcome id="1427" name="18:9"/>
<outcome id="1428" name="18:10"/>
<outcome id="1429" name="18:11"/>
<outcome id="1430" name="18:12"/>
<outcome id="1431" name="18:13"/>
<outcome id="1432" name="18:14"/>
<outcome id="1433" name="18:15"/>
<outcome id="1434" name="18:16"/>
<outcome id="1435" name="18:17"/>
<outcome id="1436" name="18:18"/>
<outcome id="1437" name="18:19"/>
<outcome id="1438" name="19:0"/>
<outcome id="1439" name="19:1"/>
<outcome id="1440" name="19:2"/>
<outcome id="1441" name="19:3"/>
<outcome id="1442" name="19:4"/>
<outcome id="1443" name="19:5"/>
<outcome id="1444" name="19:6"/>
<outcome id="1445" name="19:7"/>
<outcome id="1446" name="19:8"/>
<outcome id="1447" name="19:9"/>
<outcome id="1448" name="19:10"/>
<outcome id="1449" name="19:11"/>
<outcome id="1450" name="19:12"/>
<outcome id="1451" name="19:13"/>
<outcome id="1452" name="19:14"/>
<outcome id="1453" name="19:15"/>
<outcome id="1454" name="19:16"/>
<outcome id="1455" name="19:17"/>
<outcome id="1456" name="19:18"/>
<outcome id="1457" name="19:19"/>
</outcomes>
<specifiers>
<specifier name="score" type="string" description="current score in match"/>
</specifiers>
<attributes>
<attribute name="is_flex_score" description="Outcomes should be adjusted according to score specifier"/>
</attributes>
</market>
<market id="997" name="Launch spread" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
</market>
<market id="794" name="Player tries" groups="all|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="902" name="Next scoring play" groups="all|score|regular_play">
<outcomes>
<outcome id="1640" name="{$competitor1} with try"/>
<outcome id="1641" name="{$competitor1} with penalty"/>
<outcome id="1642" name="{$competitor1} with drop goal"/>
<outcome id="1643" name="{$competitor2} with try"/>
<outcome id="1644" name="{$competitor2} with penalty"/>
<outcome id="1645" name="{$competitor2} with drop goal"/>
</outcomes>
<specifiers>
<specifier name="score" type="string" description="current score in match"/>
</specifiers>
</market>
<market id="265" name="1x2 &amp; total (incl. extra innings)" groups="all|combo|incl_ei">
<outcomes>
<outcome id="794" name="{$competitor1} &amp; under {total}"/>
<outcome id="796" name="{$competitor1} &amp; over {total}"/>
<outcome id="798" name="draw &amp; under {total}"/>
<outcome id="800" name="draw &amp; over {total}"/>
<outcome id="802" name="{$competitor2} &amp; under {total}"/>
<outcome id="804" name="{$competitor2} &amp; over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="544" name="2nd half - 1x2 &amp; total" groups="all|combo|2nd_half">
<outcomes>
<outcome id="794" name="{$competitor1} &amp; under {total}"/>
<outcome id="796" name="{$competitor1} &amp; over {total}"/>
<outcome id="798" name="draw &amp; under {total}"/>
<outcome id="800" name="draw &amp; over {total}"/>
<outcome id="802" name="{$competitor2} &amp; under {total}"/>
<outcome id="804" name="{$competitor2} &amp; over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="121" name="Overtime 1st half - correct score [{score}]" groups="all|score|ot_1st_half">
<outcomes>
<outcome id="618" name="0:0"/>
<outcome id="620" name="1:0"/>
<outcome id="622" name="2:0"/>
<outcome id="624" name="0:1"/>
<outcome id="626" name="1:1"/>
<outcome id="628" name="0:2"/>
</outcomes>
<specifiers>
<specifier name="score" type="string" description="current score in match"/>
</specifiers>
<attributes>
<attribute name="is_flex_score" description="Outcomes should be adjusted according to score specifier"/>
</attributes>
</market>
<market id="355" name="{!inningnr} innings overs 0 to {overnr} - {$competitor2} run range" groups="all|score|x_overs">
<specifiers>
<specifier name="overnr" type="integer"/>
<specifier name="variant" type="variable_text"/>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="1077" name="{!inningnr} inning - {$competitor1} total pitches" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="1132" name="{!inningsnr} innings  over {overnr} - deliveries {deliverynr1} &amp; {deliverynr2} {$competitor1} over {total}" groups="all">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="overnr" type="integer"/>
<specifier name="deliverynr1" type="integer"/>
<specifier name="deliverynr2" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="112" name="Overtime &amp; goal" groups="all|combo|ot">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="979" name="{!inningnr} innings - {$competitor2} total fours spread" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="205" name="{!setnr} set - odd/even games" groups="all|score|set">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
</specifiers>
</market>
<market id="1134" name="{!inningnr} innings - {%player1} &amp; {%player2} dismissal method" groups="all">
<outcomes>
<outcome id="1806" name="fielder catch"/>
<outcome id="1807" name="bowled"/>
<outcome id="1808" name="keeper catch"/>
<outcome id="1809" name="lbw"/>
<outcome id="1810" name="run out"/>
<outcome id="1811" name="stumped"/>
<outcome id="1812" name="other"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="player1" type="string"/>
<specifier name="player2" type="string"/>
</specifiers>
</market>
<market id="600" name="5 minutes - {$competitor2} sending off from {from} to {to}" groups="all|5_min|bookings">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="311" name="{!setnr} set - odd/even" groups="all|score|set">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
</specifiers>
</market>
<market id="553" name="2nd half - multigoals" groups="all|score|2nd_half">
<outcomes>
<outcome id="1746" name="1-2"/>
<outcome id="1747" name="1-3"/>
<outcome id="1748" name="2-3"/>
<outcome id="1749" name="4+"/>
<outcome id="1805" name="no goal"/>
</outcomes>
</market>
<market id="857" name="Draw or under {total}" groups="all|regular_play|combo">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="395" name="{!mapnr} map - winner" groups="all|map">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
</specifiers>
</market>
<market id="884" name="1 minute - total corners from {from} to {to}" groups="all|corners|rapid_market">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="from" type="string"/>
<specifier name="to" type="string"/>
</specifiers>
</market>
<market id="512" name="{!framenr} frame - will there be a foul committed" groups="all|score|frame">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="framenr" type="integer"/>
</specifiers>
</market>
<market id="414" name="{$competitor1} total (incl. overtime and penalties)" groups="all|score|incl_ot_and_pen">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="66" name="1st half - handicap" groups="all|score|1st_half">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="807" name="{$event} - head2head" groups="all|league" includes_outcomes_of_type="sr:competitor">
<specifiers>
<specifier name="competitor1" type="string"/>
<specifier name="competitor2" type="string"/>
</specifiers>
</market>
<market id="1069" name="Any team win to nil (incl. extra innings)" groups="all|incl_ei">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="184" name="{!goalnr} goal &amp; 1x2" groups="all|regular_play|combo">
<outcomes>
<outcome id="814" name="{$competitor1} goal &amp; {$competitor1}"/>
<outcome id="816" name="{$competitor1} goal &amp; draw"/>
<outcome id="818" name="{$competitor1} goal &amp; {$competitor2}"/>
<outcome id="820" name="{$competitor2} goal &amp; {$competitor1}"/>
<outcome id="822" name="{$competitor2} goal &amp; draw"/>
<outcome id="824" name="{$competitor2} goal &amp; {$competitor2}"/>
<outcome id="826" name="no goal"/>
</outcomes>
<specifiers>
<specifier name="goalnr" type="integer"/>
</specifiers>
</market>
<market id="204" name="{!setnr} set - total games" groups="all|score|set">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="835" name="{$competitor1} windex" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
<specifiers>
<specifier name="maxovers" type="integer"/>
</specifiers>
<attributes>
<attribute name="is_spread_market" description="This is a spread-market and special client-side spread rules need to be followed for odds-changes and bet-settlements"/>
</attributes>
</market>
<market id="1078" name="{!inningnr} inning - {$competitor2} total pitches" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="971" name="Multi sixes spread" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
</market>
<market id="32" name="{$competitor2} clean sheet" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="275" name="Innings 1 to 5 - handicap" groups="all|score|5_innings">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="156" name="1st half - {$competitor1} exact bookings" groups="all|1st_half|bookings">
<outcomes>
<outcome id="54" name="0"/>
<outcome id="56" name="1"/>
<outcome id="58" name="2"/>
<outcome id="60" name="3+"/>
</outcomes>
</market>
<market id="105" name="10 minutes - 1x2 from {from} to {to}" groups="all|score|10_min">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="362" name="{!inningnr} innings over {overnr} - {!deliverynr} delivery {$competitor1} total" groups="all|score|delivery">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="overnr" type="integer"/>
<specifier name="deliverynr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="438" name="{$competitor2} to win any period" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="506" name="{!framenr} frame - break 100+" groups="all|frame|break">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="framenr" type="integer"/>
</specifiers>
</market>
<market id="1096" name="Total laps at {!stopnr} pit stop" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="stopnr" type="integer"/>
</specifiers>
</market>
<market id="1006" name="2 ball (1x2)" groups="all">
<outcomes>
<outcome id="1966" name="{%competitor1}"/>
<outcome id="1967" name="draw"/>
<outcome id="1968" name="{%competitor2}"/>
</outcomes>
<specifiers>
<specifier name="competitor1" type="string"/>
<specifier name="competitor2" type="string"/>
</specifiers>
<attributes>
<attribute name="is_golf_stroke_play_market" description="This market is applicable to Golf stroke play"/>
</attributes>
</market>
<market id="525" name="Will there be a 4th set" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="374" name="{!setnr} set - correct score (in legs)" groups="all|score|set">
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="661" name="{!inningnr} innings - {$competitor2} total sixes" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="total" type="decimal"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="760" name="Player pass completions (incl. overtime)" groups="all|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="850" name="{$competitor1} to win exactly 1 set" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="305" name="{!quarternr} quarter - race to {pointnr} points" groups="all|score|quarter">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="quarternr" type="integer"/>
<specifier name="pointnr" type="integer"/>
</specifiers>
</market>
<market id="214" name="{!setnr} set game {gamenr} - correct score" groups="all|score|game">
<outcomes>
<outcome id="886" name="{$competitor1} to 0"/>
<outcome id="887" name="{$competitor1} to 15"/>
<outcome id="888" name="{$competitor1} to 30"/>
<outcome id="889" name="{$competitor1} to 40"/>
<outcome id="890" name="{$competitor2} to 0"/>
<outcome id="891" name="{$competitor2} to 15"/>
<outcome id="892" name="{$competitor2} to 30"/>
<outcome id="893" name="{$competitor2} to 40"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="gamenr" type="integer"/>
</specifiers>
</market>
<market id="1166" name="Will there be a tie" groups="all">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<attributes>
<attribute name="is_golf_match_play_market" description="This market is applicable to Golf match play"/>
</attributes>
</market>
<market id="1041" name="{$competitor1} total hits (incl. extra innings)" groups="all|incl_ei">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="113" name="Overtime - 1x2" groups="all|score|ot">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
</market>
<market id="1007" name="2 ball (handicap)" groups="all">
<outcomes>
<outcome id="1969" name="{%competitor1} ({+hcp})"/>
<outcome id="1970" name="{%competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="competitor1" type="string"/>
<specifier name="competitor2" type="string"/>
<specifier name="hcp" type="decimal"/>
</specifiers>
<attributes>
<attribute name="is_golf_stroke_play_market" description="This market is applicable to Golf stroke play"/>
</attributes>
</market>
<market id="686" name="Batter head2head (1x2)" groups="all">
<outcomes>
<outcome id="1818" name="{%player1}"/>
<outcome id="1819" name="draw"/>
<outcome id="1820" name="{%player2}"/>
</outcomes>
<specifiers>
<specifier name="player1" type="string"/>
<specifier name="player2" type="string"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="870" name="Exacta" groups="all">
<outcomes>
<outcome id="1893" name="others"/>
</outcomes>
</market>
<market id="724" name="Will there be an ultra kill" groups="all|regular_play|kills">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="11" name="Draw no bet" groups="all|score|regular_play">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
</market>
<market id="386" name="{$competitor2} total 180s" groups="all|regular_play|180s">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="159" name="1st half - sending off" groups="all|1st_half|bookings">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="536" name="Free text multiwinner market" groups="all" includes_outcomes_of_type="pre:outcometext">
<specifiers>
<specifier name="variant" type="variable_text"/>
<specifier name="winners" type="integer" description="number of winners"/>
</specifiers>
</market>
<market id="250" name="{!gamenr} game - race to {pointnr} points" groups="all|score|game">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="gamenr" type="integer"/>
<specifier name="pointnr" type="integer"/>
</specifiers>
</market>
<market id="581" name="5 minutes - corner handicap from {from} to {to}" groups="all|5_min|corners">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="hcp" type="decimal"/>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="211" name="{!setnr} set game {gamenr} - exact points" groups="all|score|game">
<outcomes>
<outcome id="882" name="4"/>
<outcome id="883" name="5"/>
<outcome id="884" name="6"/>
<outcome id="885" name="7+"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="gamenr" type="integer"/>
</specifiers>
</market>
<market id="39" name="Last goalscorer" groups="all|regular_play|scorers" includes_outcomes_of_type="sr:player">
<outcomes>
<outcome id="1716" name="no goal"/>
</outcomes>
<specifiers>
<specifier name="type" type="string"/>
</specifiers>
</market>
<market id="799" name="{$event} matchday {matchday} - total" groups="all|matchday">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="matchday" type="integer"/>
</specifiers>
</market>
<market id="141" name="{$competitor2} total bookings" groups="all|regular_play|bookings">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="459" name="{!periodnr} period - draw no bet" groups="all|score|period">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="periodnr" type="integer"/>
</specifiers>
</market>
<market id="978" name="{!inningnr} innings - {$competitor1} total fours spread" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="76" name="1st half - {$competitor1} clean sheet" groups="all|score|1st_half">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="964" name="Any team total maximum consecutive points" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="252" name="1x2 (incl. extra innings)" groups="all">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<attributes>
<attribute name="deprecated" description="This market is no longer sent by any producer"/>
</attributes>
</market>
<market id="892" name="{!goalnr} goalscorer" groups="all|regular_play|scorers" includes_outcomes_of_type="sr:player">
<specifiers>
<specifier name="variant" type="variable_text"/>
<specifier name="goalnr" type="integer"/>
<specifier name="version" type="string"/>
</specifiers>
</market>
<market id="494" name="Total frames" groups="all|score|regular_play">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="539" name="Head2head" groups="all" includes_outcomes_of_type="sr:competitor">
<specifiers>
<specifier name="id" type="string"/>
</specifiers>
</market>
<market id="793" name="Player fantasy points" groups="all|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="276" name="Innings 1 to 5 - total" groups="all|score|5_innings">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="174" name="1st half - {!cornernr} corner" groups="all|1st_half|corners">
<outcomes>
<outcome id="6" name="{$competitor1}"/>
<outcome id="7" name="none"/>
<outcome id="8" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="cornernr" type="integer"/>
</specifiers>
</market>
<market id="710" name="Which team wins the coin toss and the match" groups="all">
<outcomes>
<outcome id="1813" name="{$competitor1}"/>
<outcome id="1814" name="neither"/>
<outcome id="1815" name="{$competitor2}"/>
</outcomes>
</market>
<market id="333" name="{!mapnr} map - {!xth} kill" groups="all|kills|map">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
<specifier name="xth" type="integer"/>
</specifiers>
</market>
<market id="843" name="{!inningnr} innings - {%player} total spread" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="player" type="string"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
<attributes>
<attribute name="is_spread_market" description="This is a spread-market and special client-side spread rules need to be followed for odds-changes and bet-settlements"/>
</attributes>
</market>
<market id="332" name="{!mapnr} map - total rounds (incl. overtime)" groups="all|score|map_incl_ot">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="14" name="Handicap {hcp}" groups="all|score|regular_play">
<outcomes>
<outcome id="1711" name="{$competitor1} ({hcp})"/>
<outcome id="1712" name="draw ({hcp})"/>
<outcome id="1713" name="{$competitor2} ({hcp})"/>
</outcomes>
<specifiers>
<specifier name="hcp" type="string"/>
</specifiers>
</market>
<market id="60" name="1st half - 1x2" groups="all|score|1st_half">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
</market>
<market id="872" name="Trifecta" groups="all">
<outcomes>
<outcome id="1893" name="others"/>
</outcomes>
</market>
<market id="636" name="1st half - {$competitor1} goal range" groups="all|score|1st_half">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="595" name="10 minutes - {$competitor2} sending off from {from} to {to}" groups="all|10_min|bookings">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="87" name="2nd half - handicap {hcp}" groups="all|score|2nd_half">
<outcomes>
<outcome id="1711" name="{$competitor1} ({hcp})"/>
<outcome id="1712" name="draw ({hcp})"/>
<outcome id="1713" name="{$competitor2} ({hcp})"/>
</outcomes>
<specifiers>
<specifier name="hcp" type="string"/>
</specifiers>
</market>
<market id="928" name="{%player} total earned runs (incl. extra innings)" groups="all|incl_ei">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="player" type="string"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="149" name="1st half - booking 1x2" groups="all|1st_half|bookings">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
</market>
<market id="864" name="Draw or any clean sheet" groups="all|regular_play|combo">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="827" name="Bonus ball sum total" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="629" name="{!mapnr} map round {roundnr} - {$competitor1} total kills" groups="all|kills|round">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
<specifier name="roundnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="993" name="Multi wides spread" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
</market>
<market id="166" name="Total corners" groups="all|regular_play|corners">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="767" name="Player receptions (incl. overtime)" groups="all|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="903" name="Next score (incl. overtime)" groups="all|score|incl_ot">
<outcomes>
<outcome id="6" name="{$competitor1}"/>
<outcome id="7" name="none"/>
<outcome id="8" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="score" type="string" description="current score in match"/>
</specifiers>
</market>
<market id="1030" name="Holes {from} to {to} - 2 ball (1x2) most birdies" groups="all">
<outcomes>
<outcome id="1966" name="{%competitor1}"/>
<outcome id="1967" name="draw"/>
<outcome id="1968" name="{%competitor2}"/>
</outcomes>
<specifiers>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
<specifier name="competitor1" type="string"/>
<specifier name="competitor2" type="string"/>
</specifiers>
<attributes>
<attribute name="is_golf_stroke_play_market" description="This market is applicable to Golf stroke play"/>
</attributes>
</market>
<market id="186" name="Winner" groups="all|score|regular_play">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
</market>
<market id="410" name="Handicap (incl. overtime and penalties)" groups="all|score|incl_ot_and_pen">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="529" name="{!periodnr} period - double chance" groups="all|score|period">
<outcomes>
<outcome id="9" name="{$competitor1} or draw"/>
<outcome id="10" name="{$competitor1} or {$competitor2}"/>
<outcome id="11" name="draw or {$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="periodnr" type="integer"/>
</specifiers>
</market>
<market id="702" name="Top batter total" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="1127" name="{!inningnr} innings - {%player1} to score {milestone1} &amp; {%player2} to score {milestone2}" groups="all">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="player1" type="string"/>
<specifier name="player2" type="string"/>
<specifier name="milestone1" type="integer"/>
<specifier name="milestone2" type="integer"/>
</specifiers>
</market>
<market id="730" name="{!mapnr} map - tower handicap" groups="all|map|structures">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="409" name="Winning margin (incl. overtime and penalties)" groups="all|score|incl_ot_and_pen">
<outcomes>
<outcome id="630" name="{$competitor1} by 1"/>
<outcome id="632" name="{$competitor1} by 2"/>
<outcome id="634" name="{$competitor1} by 3+"/>
<outcome id="636" name="{$competitor2} by 1"/>
<outcome id="638" name="{$competitor2} by 2"/>
<outcome id="640" name="{$competitor2} by 3+"/>
</outcomes>
</market>
<market id="985" name="{!inningnr} innings - {$competitor2} total {upsnr} ups spread" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="upsnr" type="integer"/>
</specifiers>
</market>
<market id="59" name="Both halves under {total}" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="359" name="{!inningnr} innings over {overnr} - {$competitor1} odd/even" groups="all|score|over">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="overnr" type="integer"/>
</specifiers>
</market>
<market id="1110" name="Winner" groups="all" includes_outcomes_of_type="sr:competitor" outcome_type="competitor"/>
<market id="814" name="{!mapnr} map - player with most deaths" groups="all" includes_outcomes_of_type="sr:player">
<specifiers>
<specifier name="mapnr" type="integer"/>
</specifiers>
</market>
<market id="3" name="Which team will win the final" groups="all|score|cup_tie">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
</market>
<market id="729" name="{!mapnr} map - aegis handicap" groups="all|map|structures">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="908" name="Free text multiwinner market" groups="all" includes_outcomes_of_type="pre:outcometext">
<specifiers>
<specifier name="variant" type="variable_text"/>
<specifier name="winners" type="integer" description="number of winners"/>
<specifier name="version" type="string"/>
</specifiers>
</market>
<market id="836" name="{$competitor2} windex" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
<specifiers>
<specifier name="maxovers" type="integer"/>
</specifiers>
<attributes>
<attribute name="is_spread_market" description="This is a spread-market and special client-side spread rules need to be followed for odds-changes and bet-settlements"/>
</attributes>
</market>
<market id="458" name="{!periodnr} period - which team wins the rest" groups="all|score|period">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="periodnr" type="integer"/>
<specifier name="score" type="string"/>
</specifiers>
</market>
<market id="1033" name="Holes {from} to {to} - 3 ball most birdies" groups="all" includes_outcomes_of_type="sr:competitor" outcome_type="competitor">
<specifiers>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
<attributes>
<attribute name="is_golf_stroke_play_market" description="This market is applicable to Golf stroke play"/>
</attributes>
</market>
<market id="558" name="{!mapnr} map - {!xth} inhibitor" groups="all|map|structures">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
<specifier name="xth" type="integer"/>
</specifiers>
</market>
<market id="466" name="Overtime - {$competitor2} no bet" groups="all|score|ot">
<outcomes>
<outcome id="780" name="{$competitor1}"/>
<outcome id="782" name="draw"/>
</outcomes>
</market>
<market id="1061" name="{!drivenr} drive play {playnr} - {%competitor} play type" groups="all|drive">
<outcomes>
<outcome id="1990" name="rush"/>
<outcome id="1991" name="pass"/>
</outcomes>
<specifiers>
<specifier name="drivenr" type="integer"/>
<specifier name="playnr" type="integer"/>
<specifier name="competitor" type="string"/>
</specifiers>
</market>
<market id="278" name="Innings 1 to 5 - {$competitor2} total" groups="all|score|5_innings">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="212" name="{!setnr} set game {gamenr} - to deuce" groups="all|score|game">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="gamenr" type="integer"/>
</specifiers>
</market>
<market id="94" name="2nd half - odd/even" groups="all|score|2nd_half">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
</market>
<market id="608" name="{!inningnr} innings - odd/even" groups="all|score|innings">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="349" name="{!inningnr} innings - {$competitor1} total at {!dismissalnr} dismissal" groups="all|score|innings">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="dismissalnr" type="integer"/>
<specifier name="total" type="decimal"/>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="1119" name="Race to {pointnr} points" groups="all">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="pointnr" type="integer"/>
</specifiers>
</market>
<market id="821" name="Draw sum odd/even" groups="all">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
</market>
<market id="511" name="{!framenr} frame - {$competitor2} break 50+" groups="all|frame|break">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="framenr" type="integer"/>
</specifiers>
</market>
<market id="400" name="{$competitor2} goal range" groups="all|score|regular_play">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="167" name="{$competitor1} total corners" groups="all|regular_play|corners">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="695" name="Total ducks" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="615" name="{!quarternr} quarter - total (incl. overtime)" groups="all|score|quarter_incl_ot">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="quarternr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="1175" name="1st quarter 1x2 &amp; 1st quarter total" groups="all|combo|incl_ot">
<outcomes>
<outcome id="794" name="{$competitor1} &amp; under {total}"/>
<outcome id="796" name="{$competitor1} &amp; over {total}"/>
<outcome id="798" name="draw &amp; under {total}"/>
<outcome id="800" name="draw &amp; over {total}"/>
<outcome id="802" name="{$competitor2} &amp; under {total}"/>
<outcome id="804" name="{$competitor2} &amp; over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="759" name="Player passing yards (incl. overtime)" groups="all|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="828" name="Bonus ball single digit" groups="all">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="231" name="2nd half - handicap (incl. overtime)" groups="all|score|combo|incl_ot|2nd_half_incl_ot">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="628" name="{!mapnr} map round {roundnr} - {!killnr} kill" groups="all|kills|round">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
<specifier name="roundnr" type="integer"/>
<specifier name="killnr" type="integer"/>
</specifiers>
</market>
<market id="158" name="1st half - booking point range" groups="all|1st_half|bookings">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="138" name="Total booking points" groups="all|regular_play|bookings">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="901" name="{$competitor2} {!c2goalnr} goalscorer" groups="all|regular_play|scorers" includes_outcomes_of_type="sr:player">
<outcomes>
<outcome id="1944" name="{$competitor2} no goal"/>
</outcomes>
<specifiers>
<specifier name="c2goalnr" type="integer"/>
</specifiers>
</market>
<market id="609" name="Team with highest scoring quarter" groups="all|score|regular_play">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
</market>
<market id="623" name="{!mapnr} map - will there be an ace (incl. overtime)" groups="all|map_incl_ot|kills">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
</specifiers>
</market>
<market id="856" name="Draw or over {total}" groups="all|regular_play|combo">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="456" name="{!periodnr} period - correct score" groups="all|score|period">
<outcomes>
<outcome id="1630" name="0:0"/>
<outcome id="1631" name="1:0"/>
<outcome id="1632" name="2:0"/>
<outcome id="1633" name="0:1"/>
<outcome id="1634" name="1:1"/>
<outcome id="1635" name="2:1"/>
<outcome id="1636" name="0:2"/>
<outcome id="1637" name="1:2"/>
<outcome id="1638" name="2:2"/>
<outcome id="1639" name="other"/>
</outcomes>
<specifiers>
<specifier name="periodnr" type="integer"/>
</specifiers>
</market>
<market id="940" name="{!fieldgoalnr} field goal made (incl. overtime)" groups="all">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="fieldgoalnr" type="integer"/>
</specifiers>
</market>
<market id="586" name="15 minutes - booking 1x2 from {from} to {to}" groups="all|15_min|bookings">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="194" name="Any set to nil" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="808" name="{$event} group {group} - group winner" groups="all|cup_group" includes_outcomes_of_type="sr:competitor">
<specifiers>
<specifier name="group" type="string"/>
</specifiers>
</market>
<market id="277" name="Innings 1 to 5 - {$competitor1} total" groups="all|score|5_innings">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="77" name="1st half - {$competitor2} clean sheet" groups="all|score|1st_half">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="65" name="1st half - handicap {hcp}" groups="all|score|1st_half">
<outcomes>
<outcome id="1711" name="{$competitor1} ({hcp})"/>
<outcome id="1712" name="draw ({hcp})"/>
<outcome id="1713" name="{$competitor2} ({hcp})"/>
</outcomes>
<specifiers>
<specifier name="hcp" type="string"/>
</specifiers>
</market>
<market id="988" name="{!inningnr} innings overs 0 to {overnr} - {$competitor1} supremacy spread" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
<specifiers>
<specifier name="overnr" type="integer"/>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="736" name="{!mapnr} map 1st half - winner" groups="all|score|map_1st_half">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
</specifiers>
</market>
<market id="366" name="Leg handicap" groups="all|score|regular_play">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="885" name="1 minute - total bookings from {from} to {to}" groups="all|bookings|rapid_market">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="from" type="string"/>
<specifier name="to" type="string"/>
</specifiers>
</market>
<market id="675" name="{!inningnr} innings - {$competitor2} top batter" groups="all" includes_outcomes_of_type="sr:player">
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="maxovers" type="integer"/>
<specifier name="type" type="string"/>
</specifiers>
</market>
<market id="429" name="{!periodnr} period 1x2 &amp; 1x2" groups="all|regular_play|combo">
<outcomes>
<outcome id="1529" name="{$competitor1} &amp; {$competitor1}"/>
<outcome id="1530" name="draw &amp; {$competitor1}"/>
<outcome id="1531" name="{$competitor2} &amp; {$competitor1}"/>
<outcome id="1532" name="{$competitor1} &amp; draw"/>
<outcome id="1533" name="draw &amp; draw"/>
<outcome id="1534" name="{$competitor2} &amp; draw"/>
<outcome id="1535" name="{$competitor1} &amp; {$competitor2}"/>
<outcome id="1536" name="draw &amp; {$competitor2}"/>
<outcome id="1537" name="{$competitor2} &amp; {$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="periodnr" type="integer"/>
</specifiers>
</market>
<market id="304" name="{!quarternr} quarter - odd/even" groups="all|score|quarter">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
<specifiers>
<specifier name="quarternr" type="integer"/>
</specifiers>
</market>
<market id="1091" name="Top {winners}" groups="all" includes_outcomes_of_type="sr:competitor" outcome_type="competitor">
<specifiers>
<specifier name="winners" type="integer" description="number of winners"/>
</specifiers>
</market>
<market id="387" name="{!setnr} set - most 180s" groups="all|set|180s">
<outcomes>
<outcome id="1041" name="{$competitor1}"/>
<outcome id="1042" name="{$competitor2}"/>
<outcome id="1043" name="draw"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
</specifiers>
</market>
<market id="383" name="180s handicap" groups="all|regular_play|180s">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="306" name="How many sets will be decided by extra points" groups="all|score|regular_play">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="1025" name="Hole {holenr} - any player under par" groups="all">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="holenr" type="integer"/>
</specifiers>
<attributes>
<attribute name="is_golf_stroke_play_market" description="This market is applicable to Golf stroke play"/>
</attributes>
</market>
<market id="50" name="{$competitor1} to win either half" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="1133" name="{!inningsnr} innings  over {overnr} - deliveries {deliverynr1} &amp; {deliverynr2} {$competitor2} over {total}" groups="all">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="overnr" type="integer"/>
<specifier name="deliverynr1" type="integer"/>
<specifier name="deliverynr2" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="292" name="Winner &amp; total (incl. overtime)" groups="all|combo|incl_ot">
<outcomes>
<outcome id="973" name="{$competitor1} &amp; over {total}"/>
<outcome id="974" name="{$competitor2} &amp; over {total}"/>
<outcome id="975" name="{$competitor1} &amp; under {total}"/>
<outcome id="976" name="{$competitor2} &amp; under {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="1060" name="{$competitor2} {!c2scorenr} scoring type" groups="all|score|regular_play">
<specifiers>
<specifier name="c2scorenr" type="integer"/>
<specifier name="variant" type="string"/>
</specifiers>
</market>
<market id="1147" name="{%player} to score {milestone} &amp; take a wicket &amp; a catch" groups="all">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="player" type="string"/>
<specifier name="milestone" type="integer"/>
</specifiers>
</market>
<market id="931" name="Total touchdowns (incl. overtime)" groups="all|incl_ot">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="667" name="{!inningnr} innings - {$competitor2} total extras" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="total" type="decimal"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="921" name="{%player} total points (incl. overtime)" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="player" type="string"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="79" name="1st half - 1x2 &amp; total" groups="all|combo|1st_half">
<outcomes>
<outcome id="794" name="{$competitor1} &amp; under {total}"/>
<outcome id="796" name="{$competitor1} &amp; over {total}"/>
<outcome id="798" name="draw &amp; under {total}"/>
<outcome id="800" name="draw &amp; over {total}"/>
<outcome id="802" name="{$competitor2} &amp; under {total}"/>
<outcome id="804" name="{$competitor2} &amp; over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="703" name="Rabbit total" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="689" name="Keeper head2head (1x2)" groups="all">
<outcomes>
<outcome id="1818" name="{%player1}"/>
<outcome id="1819" name="draw"/>
<outcome id="1820" name="{%player2}"/>
</outcomes>
<specifiers>
<specifier name="player1" type="string"/>
<specifier name="player2" type="string"/>
</specifiers>
</market>
<market id="319" name="{!setnr} set - {$competitor2} total" groups="all|score|set">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="setnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="504" name="{!framenr} frame - player with highest break" groups="all|frame|break">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="framenr" type="integer"/>
</specifiers>
</market>
<market id="688" name="All-rounder head2head (1x2)" groups="all">
<outcomes>
<outcome id="1818" name="{%player1}"/>
<outcome id="1819" name="draw"/>
<outcome id="1820" name="{%player2}"/>
</outcomes>
<specifiers>
<specifier name="player1" type="string"/>
<specifier name="player2" type="string"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="1145" name="{!inningnr} innings - {%player1} or {%player2} to survive a review" groups="all">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="player1" type="string"/>
<specifier name="player2" type="string"/>
</specifiers>
</market>
<market id="1040" name="Total hits (incl. extra innings)" groups="all|incl_ei">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="800" name="{$event} matchday {matchday} - most goals home or away teams" groups="all|matchday">
<outcomes>
<outcome id="1833" name="home teams"/>
<outcome id="1834" name="draw"/>
<outcome id="1835" name="away teams"/>
</outcomes>
<specifiers>
<specifier name="matchday" type="integer"/>
</specifiers>
</market>
<market id="222" name="{!scorenr} scoring type (incl. overtime)" groups="all">
<outcomes>
<outcome id="901" name="touchdown"/>
<outcome id="902" name="fieldgoal"/>
<outcome id="903" name="other"/>
<outcome id="904" name="none"/>
</outcomes>
<specifiers>
<specifier name="scorenr" type="integer"/>
</specifiers>
</market>
<market id="825" name="Bonus ball sum odd/even" groups="all">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
</market>
<market id="104" name="15 minutes - total from {from} to {to}" groups="all|score|15_min">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="415" name="{$competitor2} total (incl. overtime and penalties)" groups="all|score|incl_ot_and_pen">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="637" name="1st half - {$competitor2} goal range" groups="all|score|1st_half">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="1055" name="Winner &amp; total" groups="all|regular_play|combo">
<outcomes>
<outcome id="973" name="{$competitor1} &amp; over {total}"/>
<outcome id="974" name="{$competitor2} &amp; over {total}"/>
<outcome id="975" name="{$competitor1} &amp; under {total}"/>
<outcome id="976" name="{$competitor2} &amp; under {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="647" name="Most fours" groups="all">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="122" name="Will there be a penalty shootout" groups="all|score|incl_ot">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="360" name="{!inningnr} innings over {overnr} - {$competitor2} odd/even" groups="all|score|over">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="overnr" type="integer"/>
</specifiers>
</market>
<market id="239" name="How many games will be decided by extra points" groups="all|score|regular_play">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="1046" name="Innings 1 to 5 - total hits" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="21" name="Exact goals" groups="all|score|regular_play">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="575" name="10 minutes - total corners from {from} to {to}" groups="all|10_min|corners">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="479" name="{$competitor1} total tries" groups="all|regular_play|tries">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="871" name="Quinella" groups="all">
<outcomes>
<outcome id="1893" name="others"/>
</outcomes>
</market>
<market id="622" name="{!mapnr} map - total headshots (incl. overtime)" groups="all|map_incl_ot|kills">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="mapnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="592" name="10 minutes - total bookings from {from} to {to}" groups="all|10_min|bookings">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="594" name="10 minutes - {$competitor1} sending off from {from} to {to}" groups="all|10_min|bookings">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="314" name="Total sets" groups="all|score|regular_play">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="470" name="1st half - winning margin" groups="all|score|1st_half">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="1174" name="Handicap (including OT) &amp; Total (including OT)" groups="all|combo|incl_ot">
<outcomes>
<outcome id="2035" name="{$competitor1} ({+hcp}) &amp; over {total}"/>
<outcome id="2036" name="{$competitor1} ({+hcp}) &amp; under {total}"/>
<outcome id="2037" name="{$competitor2} ({-hcp}) &amp; over {total}"/>
<outcome id="2038" name="{$competitor2} ({-hcp}) &amp; under {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="813" name="{!mapnr} map - player with most kills" groups="all" includes_outcomes_of_type="sr:player">
<specifiers>
<specifier name="mapnr" type="integer"/>
</specifiers>
</market>
<market id="878" name="{!inningnr} innings - {$competitor2} total" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="total" type="decimal"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="922" name="{%player} total assists (incl. overtime)" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="player" type="string"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="1014" name="{%competitor} total 2+ over par" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="competitor" type="string"/>
<specifier name="total" type="decimal"/>
</specifiers>
<attributes>
<attribute name="is_golf_stroke_play_market" description="This market is applicable to Golf stroke play"/>
</attributes>
</market>
<market id="106" name="10 minutes - {!goalnr} goal from {from} to {to}" groups="all|score|10_min">
<outcomes>
<outcome id="6" name="{$competitor1}"/>
<outcome id="7" name="none"/>
<outcome id="8" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="goalnr" type="integer"/>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="585" name="5 minutes - odd/even corners from {from} to {to}" groups="all|5_min|corners">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
<specifiers>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="487" name="1st half - total tries" groups="all|1st_half|tries">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="915" name="{%player} total pass completions (incl. overtime)" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="player" type="string"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="132" name="Penalty shootout - {$competitor1} odd/even" groups="all|score|pen_so">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
</market>
<market id="264" name="Odd/even (incl. extra innings)" groups="all|score|incl_ei">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
</market>
<market id="347" name="{!inningnr} innings - team with highest scoring over" groups="all|score|innings">
<outcomes>
<outcome id="948" name="{$competitor1}"/>
<outcome id="949" name="draw"/>
<outcome id="950" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="744" name="Innings {from} to {to} - handicap" groups="all|score|3_innings">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="4" name="Which team will win the 3rd place final" groups="all|score|cup_tie">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
</market>
<market id="717" name="Batter head2head (handicap)" groups="all">
<outcomes>
<outcome id="1816" name="{%player1} ({+hcp})"/>
<outcome id="1817" name="{%player2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="player1" type="string"/>
<specifier name="player2" type="string"/>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="437" name="{$competitor1} to win any period" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="587" name="15 minutes - total bookings from {from} to {to}" groups="all|15_min|bookings">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="155" name="1st half - exact bookings" groups="all|1st_half|bookings">
<outcomes>
<outcome id="1760" name="0"/>
<outcome id="1761" name="1"/>
<outcome id="1762" name="2"/>
<outcome id="1763" name="3"/>
<outcome id="1764" name="4"/>
<outcome id="1765" name="5"/>
<outcome id="1766" name="6+"/>
</outcomes>
</market>
<market id="1106" name="{%team} total overtakings" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="team" type="string"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="408" name="Handicap {hcp} (incl. overtime and penalties)" groups="all|score|incl_ot_and_pen">
<outcomes>
<outcome id="1711" name="{$competitor1} ({hcp})"/>
<outcome id="1712" name="draw ({hcp})"/>
<outcome id="1713" name="{$competitor2} ({hcp})"/>
</outcomes>
<specifiers>
<specifier name="hcp" type="string"/>
</specifiers>
</market>
<market id="291" name="{!pointnr} point (incl. overtime)" groups="all|score|incl_ot">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="pointnr" type="integer"/>
</specifiers>
</market>
<market id="641" name="{!inningnr} innings over {overnr} - {$competitor1} dismissal" groups="all">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="overnr" type="integer"/>
</specifiers>
</market>
<market id="146" name="Sending off" groups="all|regular_play|bookings">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="258" name="Total (incl. extra innings)" groups="all|score|incl_ei">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="1068" name="Will there be a grand slam (incl. extra innings)" groups="all|incl_ei">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="286" name="Innings {from} to {to} - {$competitor2} total" groups="all|score|3_innings">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="851" name="{$competitor2} to win exactly 1 set" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="1105" name="{!stopnr} pit stop (teams)" groups="all" includes_outcomes_of_type="sr:competitor" outcome_type="competitor">
<specifiers>
<specifier name="stopnr" type="integer" description="pit stop number"/>
</specifiers>
</market>
<market id="448" name="{!periodnr} period - {$competitor2} total" groups="all|score|period">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="periodnr" type="integer"/>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="567" name="15 minutes - corner handicap from {from} to {to}" groups="all|15_min|corners">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="hcp" type="decimal"/>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="423" name="{!goalnr} goal &amp; winner (incl. overtime and penalties)" groups="all|combo|incl_ot_and_pen">
<outcomes>
<outcome id="1478" name="{$competitor1} goal &amp; {$competitor1}"/>
<outcome id="1479" name="{$competitor1} goal &amp; {$competitor2}"/>
<outcome id="1480" name="{$competitor2} goal &amp; {$competitor1}"/>
<outcome id="1481" name="{$competitor2} goal &amp; {$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="goalnr" type="integer"/>
</specifiers>
</market>
<market id="436" name="{$competitor2} to win all periods" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="422" name="{$competitor2} clean sheet (incl. overtime and penalties)" groups="all|score|incl_ot_and_pen">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="1013" name="{%competitor} total bogeys" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="competitor" type="string"/>
<specifier name="total" type="decimal"/>
</specifiers>
<attributes>
<attribute name="is_golf_stroke_play_market" description="This market is applicable to Golf stroke play"/>
</attributes>
</market>
<market id="1083" name="Pitch range of {%player} {!appearancenr} time at bat" groups="all">
<outcomes>
<outcome id="2005" name="1-3"/>
<outcome id="2006" name="4-5"/>
<outcome id="2007" name="6+"/>
</outcomes>
<specifiers>
<specifier name="appearancenr" type="integer"/>
<specifier name="player" type="string"/>
</specifiers>
</market>
<market id="1082" name="{!inningnr} inning - {$competitor2} to record a double or triple play" groups="all">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="115" name="Overtime - {!goalnr} goal" groups="all|score|ot">
<outcomes>
<outcome id="6" name="{$competitor1}"/>
<outcome id="7" name="none"/>
<outcome id="8" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="goalnr" type="integer"/>
</specifiers>
</market>
<market id="785" name="Pitcher strikeouts (incl. extra innings)" groups="all|incl_ei|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="1022" name="Hole {holenr} - 3 ball" groups="all" includes_outcomes_of_type="sr:competitor" outcome_type="competitor">
<specifiers>
<specifier name="holenr" type="integer"/>
</specifiers>
<attributes>
<attribute name="is_golf_stroke_play_market" description="This market is applicable to Golf stroke play"/>
</attributes>
</market>
<market id="601" name="Race to {cornernr} corners" groups="all|regular_play|corners">
<outcomes>
<outcome id="6" name="{$competitor1}"/>
<outcome id="7" name="none"/>
<outcome id="8" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="cornernr" type="integer"/>
</specifiers>
</market>
<market id="441" name="All periods over {total}" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="822" name="Draw sum total" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="1000" name="{!inningnr} innings overs {overnrX} x {overnrY} - {$competitor1} multi run spread" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="overnrX" type="integer"/>
<specifier name="overnrY" type="integer"/>
</specifiers>
</market>
<market id="772" name="Player rebounds (incl. overtime)" groups="all|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="1104" name="{!retirementnr} to retire (teams)" groups="all" includes_outcomes_of_type="sr:competitor" outcome_type="competitor">
<specifiers>
<specifier name="retirementnr" type="integer" description="retirement"/>
</specifiers>
</market>
<market id="342" name="Will there be a tie" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="493" name="Frame handicap" groups="all|score|regular_play">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="24" name="{$competitor2} exact goals" groups="all|score|regular_play">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="477" name="Try handicap" groups="all|regular_play|tries">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="614" name="{!quarternr} quarter - handicap (incl. overtime)" groups="all|score|quarter_incl_ot">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="quarternr" type="integer"/>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="93" name="2nd half - exact goals" groups="all|score|2nd_half">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="771" name="Player blocks (incl. overtime)" groups="all|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="779" name="Player touches" groups="all|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="177" name="1st half - total corners" groups="all|1st_half|corners">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="195" name="Will there be a tiebreak" groups="all|score|regular_play">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
</market>
<market id="968" name="Anytime goalscorer" groups="all|regular_play|scorers" includes_outcomes_of_type="sr:player" outcome_type="player">
<specifiers>
<specifier name="version" type="string"/>
</specifiers>
</market>
<market id="283" name="Innings 1 to 5th top - {$competitor2} total" groups="all|score|4.5_innings">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
</specifiers>
</market>
<market id="1124" name="Innings 1 to 5 - Winner" groups="all|5_innings">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
</market>
<market id="640" name="Total sixes" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="total" type="decimal"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="1138" name="{!inningnr} innings over {overnr} - {!deliverynr} delivery {$competitor1} to be a wicket" groups="all">
<outcomes>
<outcome id="74" name="yes"/>
<outcome id="76" name="no"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="overnr" type="integer"/>
<specifier name="deliverynr" type="integer"/>
</specifiers>
</market>
<market id="716" name="{!inningnr} innings over {overnr} - {$competitor2} run range" groups="all">
<specifiers>
<specifier name="overnr" type="integer"/>
<specifier name="variant" type="variable_text"/>
<specifier name="inningnr" type="integer"/>
</specifiers>
</market>
<market id="1021" name="{%competitor} odd/even strokes" groups="all">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
<specifiers>
<specifier name="competitor" type="string"/>
</specifiers>
<attributes>
<attribute name="is_golf_stroke_play_market" description="This market is applicable to Golf stroke play"/>
</attributes>
</market>
<market id="705" name="{!inningnr} innings - {$competitor2} total wides bowled" groups="all">
<outcomes>
<outcome id="13" name="under {total}"/>
<outcome id="12" name="over {total}"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="total" type="decimal"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
</market>
<market id="842" name="{!inningnr} innings - {$competitor2} total spread at {!dismissalnr} dismissal" groups="all">
<outcomes>
<outcome id="1890" name="sell"/>
<outcome id="1891" name="buy"/>
<outcome id="1892" name="mid"/>
</outcomes>
<specifiers>
<specifier name="inningnr" type="integer"/>
<specifier name="dismissalnr" type="integer"/>
<specifier name="maxovers" type="integer"/>
</specifiers>
<attributes>
<attribute name="is_spread_market" description="This is a spread-market and special client-side spread rules need to be followed for odds-changes and bet-settlements"/>
</attributes>
</market>
<market id="786" name="Pitcher earned runs (incl. extra innings)" groups="all|incl_ei|player_props">
<specifiers>
<specifier name="variant" type="variable_text"/>
</specifiers>
</market>
<market id="187" name="Game handicap" groups="all|score|regular_play">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="1090" name="{!quarternr} quarter - {!pointnr} point" groups="all">
<outcomes>
<outcome id="4" name="{$competitor1}"/>
<outcome id="5" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="quarternr" type="integer"/>
<specifier name="pointnr" type="integer"/>
</specifiers>
</market>
<market id="293" name="2nd half - 1x2 (incl. overtime)" groups="all|score|combo|incl_ot|2nd_half_incl_ot">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
</market>
<market id="223" name="Handicap (incl. overtime)" groups="all|score|incl_ot">
<outcomes>
<outcome id="1714" name="{$competitor1} ({+hcp})"/>
<outcome id="1715" name="{$competitor2} ({-hcp})"/>
</outcomes>
<specifiers>
<specifier name="hcp" type="decimal"/>
</specifiers>
</market>
<market id="572" name="10 minutes - corner 1x2 from {from} to {to}" groups="all|10_min|corners">
<outcomes>
<outcome id="1" name="{$competitor1}"/>
<outcome id="2" name="draw"/>
<outcome id="3" name="{$competitor2}"/>
</outcomes>
<specifiers>
<specifier name="from" type="integer"/>
<specifier name="to" type="integer"/>
</specifiers>
</market>
<market id="361" name="1st over - odd/even" groups="all|score|over">
<outcomes>
<outcome id="70" name="odd"/>
<outcome id="72" name="even"/>
</outcomes>
</market>
</market_descriptions>