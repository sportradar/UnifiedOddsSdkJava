/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.sportradar.unifiedodds.sdk.conn.ApiSimulator.ApiStubDelay.toBeDelayedBy;
import static com.sportradar.unifiedodds.sdk.conn.ProducerId.LIVE_ODDS;
import static com.sportradar.unifiedodds.sdk.conn.SapiProducers.buildActiveProducer;
import static com.sportradar.unifiedodds.sdk.conn.SapiSports.allSports;
import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.sportradar.uf.custombet.datamodel.CapiCalculationResponse;
import com.sportradar.uf.custombet.datamodel.CapiFilteredCalculationResponse;
import com.sportradar.uf.custombet.datamodel.CapiResponse;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.utils.Urn;
import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;
import org.apache.http.HttpStatus;

@SuppressWarnings(
    { "ClassFanOutComplexity", "ClassDataAbstractionCoupling", "MultipleStringLiterals", "MagicNumber" }
)
public class ApiSimulator {

    public static final String XML_DECLARATION =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";
    public static final String UNIFIED_XML_NAMESPACE = "http://schemas.sportradar.com/sportsapi/v1/unified";
    private final Consumer<MappingBuilder> stubRegistrar;

    public ApiSimulator(WireMockRule wireMockRule) {
        this.stubRegistrar = wireMockRule::stubFor;
    }

    public ApiSimulator(WireMock wireMock) {
        this.stubRegistrar = wireMock::register;
    }

    public void activateOnlyLiveProducer() {
        Producers producers = new Producers();
        producers.setResponseCode(ResponseCode.OK);
        producers.getProducer().add(buildActiveProducer(LIVE_ODDS));

        register(
            get(urlPathEqualTo("/v1/descriptions/producers.xml"))
                .willReturn(WireMock.ok(JaxbContexts.SportsApi.marshall(producers)))
        );
    }

    public void activateProducer(ProducerId producer) {
        Producers producers = new Producers();
        producers.setResponseCode(ResponseCode.OK);
        producers.getProducer().add(buildActiveProducer(producer));

        register(
            get(urlPathEqualTo("/v1/descriptions/producers.xml"))
                .willReturn(WireMock.ok(JaxbContexts.SportsApi.marshall(producers)))
        );
    }

    public void activateProducer(ProducerId producer, String producerApiUrl) {
        Producers producers = new Producers();
        producers.setResponseCode(ResponseCode.OK);
        producers.getProducer().add(buildActiveProducer(producer, producerApiUrl));

        register(
            get(urlPathEqualTo("/v1/descriptions/producers.xml"))
                .willReturn(WireMock.ok(JaxbContexts.SportsApi.marshall(producers)))
        );
    }

    public void defineBookmaker() {
        register(
            get(urlPathEqualTo("/v1/users/whoami.xml"))
                .willReturn(
                    WireMock.ok(
                        XML_DECLARATION +
                        "<bookmaker_details response_code=\"OK\" " +
                        "expire_at=\"2025-07-26T17:44:24Z\" " +
                        "bookmaker_id=\"1\" " +
                        "virtual_host=\"/virtualhost\"/>"
                    )
                )
        );
    }

    public void stubWhoAmIWithEmptyResponseBody() {
        register(get(urlPathEqualTo("/v1/users/whoami.xml")).willReturn(WireMock.ok()));
    }

    public void stubEmptyScheduleForNext3Days(Locale language) {
        val emptySchedule =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
            "<schedule generated_at=\"2025-03-06T17:27:34+00:00\" />";
        for (int i = 0; i <= 3; i++) {
            register(
                get(
                    urlPathMatching(
                        format(
                            "/v1/sports/%s/schedules/%s/schedule.xml",
                            language.toString(),
                            LocalDate.now().plusDays(i).format(DateTimeFormatter.ISO_DATE)
                        )
                    )
                )
                    .willReturn(WireMock.ok(emptySchedule))
            );
        }
    }

    public void stubEmptyMarketList(Locale language) {
        stub(new MarketDescriptions(), format("/v1/descriptions/%s/markets.xml.*", language.toString()));
    }

    public ApiSimulator stubMarketListContaining(DescMarket market, Locale language) {
        val descriptions = new MarketDescriptions();
        descriptions.getMarket().add(market);
        stub(descriptions, format("/v1/descriptions/%s/markets.xml.*", language.toString()));
        return this;
    }

    public ApiSimulator stubMarketListContaining(
        DescMarket market,
        Locale language,
        HeaderEquality headerEquality,
        HeaderEquality... additionalHeaderEqualities
    ) {
        val descriptions = new MarketDescriptions();
        descriptions.getMarket().add(market);

        val mapping = get(urlPathMatching(format("/v1/descriptions/%s/markets.xml.*", language.toString())));

        Stream
            .concat(Stream.of(headerEquality), Arrays.stream(additionalHeaderEqualities))
            .forEach(eq -> mapping.withHeader(eq.name, eq.valuePattern));

        register(mapping.willReturn(ok(JaxbContexts.SportsApi.marshall(descriptions))));
        return this;
    }

    public void stubVariantListContaining(DescVariant variantDescription, Locale language) {
        val descriptions = new VariantDescriptions();
        descriptions.getVariant().add(variantDescription);
        stub(descriptions, format("/v1/descriptions/%s/variants.xml.*", language.toString()));
    }

    public void stubEmptyVariantList(Locale language) {
        val descriptions = new VariantDescriptions();
        stub(descriptions, format("/v1/descriptions/%s/variants.xml.*", language.toString()));
    }

    public void stubSingleVariantMarket(DescMarket market, Locale language) {
        val descriptions = new MarketDescriptions();
        descriptions.getMarket().add(market);
        stub(
            descriptions,
            format(
                "/v1/descriptions/%s/markets/%s/variants/%s?.*",
                language,
                market.getId(),
                market.getVariant()
            )
        );
    }

    public void stubSingleVariantMarket(
        int httpStatus,
        int marketId,
        Identifiable variantId,
        Locale language
    ) {
        stub(
            httpStatus,
            format(
                "/v1/descriptions/%s/markets/%s/variants/%s.*",
                language.getLanguage(),
                marketId,
                variantId.id()
            )
        );
    }

    public void stubSingleVariantMarket(
        int httpStatus,
        int marketId,
        Identifiable variantId,
        Locale language,
        MarketDescriptions descriptions
    ) {
        stub(
            httpStatus,
            format(
                "/v1/descriptions/%s/markets/%s/variants/%s.*",
                language.getLanguage(),
                marketId,
                variantId.id()
            ),
            descriptions
        );
    }

    public void stubMatchSummary(Locale language, SapiMatchSummaryEndpoint summary) {
        String id = summary.getSportEvent().getId();
        val summaryElement = new JAXBElement<>(
            new QName(UNIFIED_XML_NAMESPACE, "match_summary"),
            SapiMatchSummaryEndpoint.class,
            summary
        );
        stub(
            HttpStatus.SC_OK,
            format("/v1/sports/%s/sport_events/%s/summary.xml", language.getLanguage(), id),
            summaryElement
        );
    }

    public void stubMatchSummary(Locale language, SapiMatchSummaryEndpoint summary, ApiStubDelay delay) {
        String id = summary.getSportEvent().getId();
        val summaryElement = new JAXBElement<>(
            new QName(UNIFIED_XML_NAMESPACE, "match_summary"),
            SapiMatchSummaryEndpoint.class,
            summary
        );

        register(
            get(
                urlPathMatching(
                    format("/v1/sports/%s/sport_events/%s/summary.xml", language.getLanguage(), id)
                )
            )
                .willReturn(
                    ok(JaxbContexts.SportsApi.marshall(summaryElement))
                        .withFixedDelay((int) delay.delay.toMillis())
                )
        );
    }

    public void stubMatchSummary(
        Locale language,
        SapiMatchSummaryEndpoint summary,
        HeaderEquality headerEquality,
        HeaderEquality... additionalHeaderEqualities
    ) {
        String id = summary.getSportEvent().getId();
        val summaryElement = new JAXBElement<>(
            new QName(UNIFIED_XML_NAMESPACE, "match_summary"),
            SapiMatchSummaryEndpoint.class,
            summary
        );

        val mapping = get(
            urlPathMatching(format("/v1/sports/%s/sport_events/%s/summary.xml", language.getLanguage(), id))
        );

        Stream
            .concat(Stream.of(headerEquality), Arrays.stream(additionalHeaderEqualities))
            .forEach(eq -> mapping.withHeader(eq.name, eq.valuePattern));
        register(mapping.willReturn(ok(JaxbContexts.SportsApi.marshall(summaryElement))));
    }

    public void stubMatchSummaryNotFound(Locale language, Urn id) {
        stub(
            HttpStatus.SC_NOT_FOUND,
            format("/v1/sports/%s/sport_events/%s/summary.xml", language.getLanguage(), id),
            sapiNotFoundResponse()
        );
    }

    public void stubMatchSummaryNotFound(
        Locale language,
        Urn id,
        HeaderEquality headerEquality,
        HeaderEquality... additionalHeaderEqualities
    ) {
        val mapping = get(
            urlPathMatching(format("/v1/sports/%s/sport_events/%s/summary.xml", language.getLanguage(), id))
        );

        Stream
            .concat(Stream.of(headerEquality), Arrays.stream(additionalHeaderEqualities))
            .forEach(eq -> mapping.withHeader(eq.name, eq.valuePattern));

        register(
            mapping.willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_NOT_FOUND)
                    .withBody(JaxbContexts.SportsApi.marshall(sapiNotFoundResponse()))
            )
        );
    }

    public void stubCompetitorProfileNotFound(Locale language, Urn id) {
        stub(
            HttpStatus.SC_NOT_FOUND,
            format("/v1/sports/%s/competitors/%s/profile.xml", language.getLanguage(), id),
            sapiNotFoundResponse()
        );
    }

    private JAXBElement<Response> sapiNotFoundResponse() {
        val sapiResponse = new Response();
        sapiResponse.setResponseCode(ResponseCode.NOT_FOUND);
        return new JAXBElement<>(new QName(UNIFIED_XML_NAMESPACE, "response"), Response.class, sapiResponse);
    }

    public void stubSeasonSummary(Locale language, SapiTournamentInfoEndpoint tournamentInfo) {
        String id = tournamentInfo.getSeason().getId();
        stubTournamentInfo(language, tournamentInfo, id);
    }

    public void stubTournamentSummary(Locale language, SapiTournamentInfoEndpoint tournamentInfo) {
        String id = tournamentInfo.getTournament().getId();
        stubTournamentInfo(language, tournamentInfo, id);
    }

    private void stubTournamentInfo(Locale language, SapiTournamentInfoEndpoint tournamentInfo, String id) {
        val summaryElement = new JAXBElement<>(
            new QName(UNIFIED_XML_NAMESPACE, "tournament_info"),
            SapiTournamentInfoEndpoint.class,
            tournamentInfo
        );
        stub(
            HttpStatus.SC_OK,
            format("/v1/sports/%s/sport_events/%s/summary.xml", language.getLanguage(), id),
            summaryElement
        );
    }

    public void stubRaceSummary(Locale language, SapiStageSummaryEndpoint stageSummary) {
        String id = stageSummary.getSportEvent().getId();
        stubRaceSummary(language, stageSummary, id);
    }

    private void stubRaceSummary(Locale language, SapiStageSummaryEndpoint stageSummary, String id) {
        val summaryElement = new JAXBElement<>(
            new QName(UNIFIED_XML_NAMESPACE, "race_summary"),
            SapiStageSummaryEndpoint.class,
            stageSummary
        );
        stub(
            HttpStatus.SC_OK,
            format("/v1/sports/%s/sport_events/%s/summary.xml", language.getLanguage(), id),
            summaryElement
        );
    }

    public void stubAllSports(Locale language) {
        val allSportsJaxb = new JAXBElement<>(
            new QName(UNIFIED_XML_NAMESPACE, "sports"),
            SapiSportsEndpoint.class,
            allSports(language)
        );
        stub(allSportsJaxb, format("/v1/sports/%s/sports.xml", language.getLanguage()));
    }

    public void stubAllSports(Locale language, SapiSport... sport) {
        val allSports = new SapiSportsEndpoint();
        val allSportsJaxb = new JAXBElement<>(
            new QName(UNIFIED_XML_NAMESPACE, "sports"),
            SapiSportsEndpoint.class,
            allSports
        );
        allSports.getSport().addAll(asList(sport));
        stub(allSportsJaxb, format("/v1/sports/%s/sports.xml", language.getLanguage()));
    }

    public void stubAllSports(Locale language, ApiStubDelay delay) {
        val allSportsJaxb = new JAXBElement<>(
            new QName(UNIFIED_XML_NAMESPACE, "sports"),
            SapiSportsEndpoint.class,
            allSports()
        );
        register(
            get(urlPathMatching(format("/v1/sports/%s/sports.xml", language.getLanguage())))
                .willReturn(
                    ok(JaxbContexts.SportsApi.marshall(allSportsJaxb))
                        .withFixedDelay((int) delay.delay.toMillis())
                )
        );
    }

    public void stubAllSportsWithEmptyErrorResponse(Locale language) {
        register(
            get(urlPathMatching(format("/v1/sports/%s/sports.xml", language.getLanguage())))
                .willReturn(badRequest().withBody("<response/>"))
        );
    }

    public void stubEmptyAllTournaments(Locale language) {
        val allTournaments = new SapiTournamentsEndpoint();
        val allTournamentsJaxb = new JAXBElement<>(
            new QName(UNIFIED_XML_NAMESPACE, "tournaments"),
            SapiTournamentsEndpoint.class,
            allTournaments
        );
        stub(allTournamentsJaxb, format("/v1/sports/%s/tournaments.xml", language.getLanguage()));
    }

    public void stubAllTournaments(Locale language, SapiTournamentExtended tournament) {
        val allTournaments = new SapiTournamentsEndpoint();
        allTournaments.getTournament().add(tournament);
        val allTournamentsJaxb = new JAXBElement<>(
            new QName(UNIFIED_XML_NAMESPACE, "tournaments"),
            SapiTournamentsEndpoint.class,
            allTournaments
        );
        stub(allTournamentsJaxb, format("/v1/sports/%s/tournaments.xml", language.getLanguage()));
    }

    public void stubAllTournaments(
        Locale language,
        SapiTournamentExtended tournament,
        HeaderEquality headerEquality
    ) {
        val allTournaments = new SapiTournamentsEndpoint();
        allTournaments.getTournament().add(tournament);
        val allTournamentsJaxb = new JAXBElement<>(
            new QName(UNIFIED_XML_NAMESPACE, "tournaments"),
            SapiTournamentsEndpoint.class,
            allTournaments
        );
        register(
            get(urlPathMatching(format("/v1/sports/%s/tournaments.xml", language.getLanguage())))
                .withHeader(headerEquality.name, headerEquality.valuePattern)
                .willReturn(ok(JaxbContexts.SportsApi.marshall(allTournamentsJaxb)))
        );
    }

    public void stubPostRequest(String url, HeaderEquality headerEquality) {
        register(post(url).withHeader(headerEquality.name, headerEquality.valuePattern));
    }

    public void stubPostRequest(String url, HeaderEquality headerEquality, ApiStubDelay delay) {
        register(
            post(url)
                .withHeader(headerEquality.name, headerEquality.valuePattern)
                .willReturn(aResponse().withFixedDelay((int) delay.delay.toMillis()))
        );
    }

    public void stubFailedPostRequest(String url, HeaderEquality headerEquality) {
        register(
            post(url).withHeader(headerEquality.name, headerEquality.valuePattern).willReturn(badRequest())
        );
    }

    public void stubPutRequest(String url, HeaderEquality headerEquality) {
        register(put(url).withHeader(headerEquality.name, headerEquality.valuePattern));
    }

    public void stubPutRequest(String url, HeaderEquality headerEquality, ApiStubDelay delay) {
        register(
            put(url)
                .withHeader(headerEquality.name, headerEquality.valuePattern)
                .willReturn(aResponse().withFixedDelay((int) delay.delay.toMillis()))
        );
    }

    public void stubFailedPutRequest(String url, HeaderEquality headerEquality) {
        register(
            put(url).withHeader(headerEquality.name, headerEquality.valuePattern).willReturn(badRequest())
        );
    }

    public void stubDeleteRequest(String url, HeaderEquality headerEquality) {
        register(delete(url).withHeader(headerEquality.name, headerEquality.valuePattern));
    }

    public void stubDeleteRequest(String url, HeaderEquality headerEquality, ApiStubDelay delay) {
        register(
            delete(url)
                .withHeader(headerEquality.name, headerEquality.valuePattern)
                .willReturn(aResponse().withFixedDelay((int) delay.delay.toMillis()))
        );
    }

    public void stubFailedDeleteRequest(String url, HeaderEquality headerEquality) {
        register(
            delete(url).withHeader(headerEquality.name, headerEquality.valuePattern).willReturn(badRequest())
        );
    }

    public void stubAllTournamentsWithBadRequestErrorResponse(
        Locale language,
        HeaderEquality headerEquality
    ) {
        register(
            get(urlPathMatching(format("/v1/sports/%s/tournaments.xml", language.getLanguage())))
                .withHeader(headerEquality.name, headerEquality.valuePattern)
                .willReturn(badRequest().withBody("<response/>"))
        );
    }

    public void stubApiGetRequest(String url, HeaderEquality headerEquality, ApiStubDelay delay) {
        register(
            get(urlPathMatching(url))
                .withHeader(headerEquality.name, headerEquality.valuePattern)
                .willReturn(aResponse().withFixedDelay((int) delay.delay.toMillis()))
        );
    }

    private void stub(int httpStatus, String path) {
        register(get(urlPathMatching(path)).willReturn(WireMock.status(httpStatus)));
    }

    private void stub(int httpStatus, String path, Object descriptions) {
        register(
            get(urlPathMatching(path))
                .willReturn(
                    WireMock
                        .aResponse()
                        .withStatus(httpStatus)
                        .withBody(JaxbContexts.SportsApi.marshall(descriptions))
                )
        );
    }

    private void stub(Object descriptions, String path) {
        register(
            get(urlPathMatching(path)).willReturn(WireMock.ok(JaxbContexts.SportsApi.marshall(descriptions)))
        );
    }

    public void noMarketVariants() {
        register(
            get(urlPathEqualTo("/v1/descriptions/en/variants.xml"))
                .willReturn(
                    WireMock.ok(
                        XML_DECLARATION +
                        "  <variant_descriptions response_code=\"OK\">" +
                        "</variant_descriptions>"
                    )
                )
        );
    }

    public void returnNotFoundForCustomBetAvailableSelections() {
        CapiResponse response = notFoundResponse(
            "no available selections for a path " + "/v1/custombet/[^/]+/available_selections"
        );
        register(
            get(urlPathMatching("/v1/custombet/[^/]+/available_selections"))
                .willReturn(
                    WireMock.aResponse().withBody(toXml(response)).withStatus(HttpStatus.SC_NOT_FOUND)
                )
        );
    }

    public void returnNotFoundForCustomBetCalculate() {
        CapiResponse response = notFoundResponse(
            "no available selections for a path " + "/v1/custombet/calculate"
        );
        register(
            post(urlPathMatching("/v1/custombet/calculate"))
                .willReturn(
                    WireMock.aResponse().withBody(toXml(response)).withStatus(HttpStatus.SC_NOT_FOUND)
                )
        );
    }

    public void returnNotFoundForCustomBetCalculateFilter() {
        CapiResponse response = notFoundResponse(
            "no available selections for a path " + "/v1/custombet/calculate-filter"
        );
        register(
            post(urlPathMatching("/v1/custombet/calculate-filter"))
                .willReturn(
                    WireMock.aResponse().withBody(toXml(response)).withStatus(HttpStatus.SC_NOT_FOUND)
                )
        );
    }

    public void stubCustomBetCalculate(CapiCalculationResponse response) {
        register(
            post(urlPathMatching("/v1/custombet/calculate"))
                .willReturn(
                    WireMock
                        .aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withBody(JaxbContexts.CustomBetApi.marshall(response))
                )
        );
    }

    public void stubCustomBetCalculate(CapiCalculationResponse response, BodyCondition requestBodyCondition) {
        register(
            post(urlPathMatching("/v1/custombet/calculate"))
                .withRequestBody(equalTo(JaxbContexts.CustomBetApi.marshall(requestBodyCondition.value)))
                .willReturn(
                    WireMock
                        .aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withBody(JaxbContexts.CustomBetApi.marshall(response))
                )
        );
    }

    public void stubCustomBetCalculateFilter(CapiFilteredCalculationResponse response) {
        register(
            post(urlPathMatching("/v1/custombet/calculate-filter"))
                .willReturn(
                    WireMock
                        .aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withBody(JaxbContexts.CustomBetApi.marshall(response))
                )
        );
    }

    private static CapiResponse notFoundResponse(String message) {
        CapiResponse capiNotFoundResponse = new CapiResponse();
        capiNotFoundResponse.setResponseCode("NOT_FOUND");
        capiNotFoundResponse.setMessage(message);
        return capiNotFoundResponse;
    }

    private void register(MappingBuilder mappingBuilder) {
        stubRegistrar.accept(mappingBuilder);
    }

    @SneakyThrows
    private String toXml(Object content) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            JAXB.marshal(content, bos);
            return new String(bos.toByteArray(), Charsets.UTF_8);
        }
    }

    public void stubCompetitorProfile(
        Locale aLanguage,
        SapiCompetitorProfileEndpoint profile,
        SapiCompetitorProfileEndpoint... profiles
    ) {
        val allProfiles = ImmutableList
            .<SapiCompetitorProfileEndpoint>builder()
            .add(profile)
            .addAll(ImmutableList.copyOf(profiles))
            .build();

        for (SapiCompetitorProfileEndpoint p : allProfiles) {
            JAXBElement<SapiCompetitorProfileEndpoint> profileJaxb = new JAXBElement<>(
                new QName(UNIFIED_XML_NAMESPACE, "competitor_profile"),
                SapiCompetitorProfileEndpoint.class,
                p
            );
            stub(
                profileJaxb,
                format(
                    "/v1/sports/%s/competitors/%s/profile.xml",
                    aLanguage.getLanguage(),
                    p.getCompetitor().getId()
                )
            );
        }
    }

    public void stubCompetitorProfile(Locale aLanguage, SapiSimpleTeamProfileEndpoint profile) {
        JAXBElement<SapiSimpleTeamProfileEndpoint> profileJaxb = new JAXBElement<>(
            new QName(UNIFIED_XML_NAMESPACE, "simpleteam_profile"),
            SapiSimpleTeamProfileEndpoint.class,
            profile
        );
        stub(
            profileJaxb,
            format(
                "/v1/sports/%s/competitors/%s/profile.xml",
                aLanguage.getLanguage(),
                profile.getCompetitor().getId()
            )
        );
    }

    public void stubPlayerProfile(Locale aLanguage, SapiPlayerExtended player) {
        SapiPlayerProfileEndpoint p = new SapiPlayerProfileEndpoint();
        p.setPlayer(player);
        JAXBElement<SapiPlayerProfileEndpoint> profileJaxb = new JAXBElement<>(
            new QName(UNIFIED_XML_NAMESPACE, "player_profile"),
            SapiPlayerProfileEndpoint.class,
            p
        );
        stub(
            profileJaxb,
            format("/v1/sports/%s/players/%s/profile.xml", aLanguage.getLanguage(), player.getId())
        );
    }

    public void stubSportCategories(Locale langA, Sport sport, SapiCategory category) {
        Urn sportUrn = sport.getUrn();
        val categories = createCategories(sportUrn, category, sport.name());
        val categoriesJaxb = new JAXBElement<>(
            new QName(UNIFIED_XML_NAMESPACE, "sport_categories"),
            SapiSportCategoriesEndpoint.class,
            categories
        );

        stub(categoriesJaxb, format("/v1/sports/%s/sports/%s/categories.xml", langA.getLanguage(), sportUrn));
    }

    public void stubSportCategories(Locale langA, SapiSport sport, SapiCategory category) {
        Urn sportUrn = Urn.parse(sport.getId());
        val categories = createCategories(sportUrn, category, sport.getName());
        val categoriesJaxb = new JAXBElement<>(
            new QName(UNIFIED_XML_NAMESPACE, "sport_categories"),
            SapiSportCategoriesEndpoint.class,
            categories
        );

        stub(categoriesJaxb, format("/v1/sports/%s/sports/%s/categories.xml", langA.getLanguage(), sportUrn));
    }

    private static SapiSportCategoriesEndpoint createCategories(
        Urn sportUrn,
        SapiCategory category,
        String sportName
    ) {
        val categories = new SapiSportCategoriesEndpoint();
        val sapiSport = new SapiSport();
        sapiSport.setId(sportUrn.toString());
        sapiSport.setName(sportName);

        categories.setSport(sapiSport);
        categories.setCategories(new SapiCategories());
        categories.getCategories().getCategory().add(category);
        return categories;
    }

    public void stubFixtureChanges(
        Locale aLanguage,
        SapiFixtureChangesEndpoint fixtureChanges,
        ApiStubQueryParameter... queryParameters
    ) {
        JAXBElement<SapiFixtureChangesEndpoint> changesJaxb = new JAXBElement<>(
            new QName(UNIFIED_XML_NAMESPACE, "fixture_changes"),
            SapiFixtureChangesEndpoint.class,
            fixtureChanges
        );
        val path = format("/v1/sports/%s/fixtures/changes.xml", aLanguage.getLanguage());
        MappingBuilder mappingBuilder = get(urlPathMatching(path));
        appendQueryParameters(queryParameters, mappingBuilder);
        register(mappingBuilder.willReturn(WireMock.ok(JaxbContexts.SportsApi.marshall(changesJaxb))));
    }

    public void stubSportEventFixtures(
        String matchId,
        Locale aLanguage,
        SapiFixturesEndpoint fixtures,
        ApiStubDelay delay
    ) {
        JAXBElement<SapiFixturesEndpoint> changesJaxb = new JAXBElement<>(
            new QName(UNIFIED_XML_NAMESPACE, "fixtures_fixture"),
            SapiFixturesEndpoint.class,
            fixtures
        );
        val path = format("/v1/sports/%s/sport_events/%s/fixture.xml", aLanguage.getLanguage(), matchId);
        MappingBuilder mappingBuilder = get(urlPathMatching(path));
        register(
            mappingBuilder.willReturn(
                WireMock
                    .ok(JaxbContexts.SportsApi.marshall(changesJaxb))
                    .withFixedDelay((int) delay.delay.toMillis())
            )
        );
    }

    public void stubSportEventFixtures(String matchId, Locale aLanguage, SapiFixturesEndpoint fixtures) {
        stubSportEventFixtures(matchId, aLanguage, fixtures, toBeDelayedBy(0, SECONDS));
    }

    public void stubResultChanges(
        Locale aLanguage,
        SapiResultChangesEndpoint resultChanges,
        ApiStubQueryParameter... queryParameters
    ) {
        JAXBElement<SapiResultChangesEndpoint> changesJaxb = new JAXBElement<>(
            new QName(UNIFIED_XML_NAMESPACE, "result_changes"),
            SapiResultChangesEndpoint.class,
            resultChanges
        );
        val path = format("/v1/sports/%s/results/changes.xml", aLanguage.getLanguage());
        MappingBuilder mappingBuilder = get(urlPathMatching(path));
        appendQueryParameters(queryParameters, mappingBuilder);
        register(mappingBuilder.willReturn(WireMock.ok(JaxbContexts.SportsApi.marshall(changesJaxb))));
    }

    private static void appendQueryParameters(
        ApiStubQueryParameter[] queryParameters,
        MappingBuilder mappingBuilder
    ) {
        if (queryParameters != null) {
            for (ApiStubQueryParameter queryParameter : queryParameters) {
                mappingBuilder.withQueryParam(
                    queryParameter.getKey().get(),
                    equalTo(queryParameter.getValue())
                );
            }
        }
    }

    public void stubEventOddsRecovery(
        String id,
        HeaderEquality headerEquality,
        HeaderEquality... additionalHeaderEqualities
    ) {
        val mapping = post(urlPathMatching(format(".*/odds/events/%s/initiate_request", id)));

        Stream
            .concat(Stream.of(headerEquality), Arrays.stream(additionalHeaderEqualities))
            .forEach(eq -> mapping.withHeader(eq.name, eq.valuePattern));
        register(mapping.willReturn(ok("OK")));
    }

    public void stubEventStatefulRecovery(
        String id,
        HeaderEquality headerEquality,
        HeaderEquality... additionalHeaderEqualities
    ) {
        val mapping = post(urlPathMatching(format(".*/stateful_messages/events/%s/initiate_request", id)));

        Stream
            .concat(Stream.of(headerEquality), Arrays.stream(additionalHeaderEqualities))
            .forEach(eq -> mapping.withHeader(eq.name, eq.valuePattern));
        register(mapping.willReturn(ok("OK")));
    }

    @Value
    @SuppressWarnings("VisibilityModifier")
    public static class ApiStubQueryParameter {

        QueryParameterName key;
        String value;

        public static ApiStubQueryParameter queryParameter(QueryParameterName key, String value) {
            return new ApiStubQueryParameter(key, value);
        }

        public interface QueryParameterName {
            String get();
        }

        public enum FixtureChangesQueryParameter implements QueryParameterName {
            AFTER_DATE_TIME("afterDateTime"),
            SPORT_ID("sportId");

            private final String key;

            FixtureChangesQueryParameter(String key) {
                this.key = key;
            }

            @Override
            public String get() {
                return key;
            }
        }
    }

    public static class ApiStubDelay {

        private final Duration delay;

        private ApiStubDelay(Duration delay) {
            this.delay = delay;
        }

        public static ApiStubDelay toBeDelayedBy(int delay, TemporalUnit unit) {
            return new ApiStubDelay(Duration.of(delay, unit));
        }
    }

    public static class HeaderEquality {

        private final String name;
        private final StringValuePattern valuePattern;

        public HeaderEquality(String name, StringValuePattern valuePattern) {
            this.name = name;
            this.valuePattern = valuePattern;
        }

        public static HeaderEquality forHeader(String name, String value) {
            return new HeaderEquality(name, equalTo(value));
        }

        public static HeaderEquality forHeaderWithAnyValue(String name) {
            return new HeaderEquality(name, matching("^.+$"));
        }
    }

    public static class BodyCondition {

        private final Object value;

        public BodyCondition(Object value) {
            this.value = value;
        }

        public static BodyCondition forRequestBody(Object value) {
            return new BodyCondition(value);
        }
    }
}
