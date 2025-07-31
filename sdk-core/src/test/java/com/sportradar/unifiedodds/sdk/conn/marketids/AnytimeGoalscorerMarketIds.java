/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn.marketids;

import static com.sportradar.unifiedodds.sdk.conn.SapiPlayerProfiles.*;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.Germany2024Uefa.*;

import com.google.common.collect.ImmutableList;
import java.util.List;

public class AnytimeGoalscorerMarketIds {

    public static final int ANYTIME_GOALSCORER_MARKET_ID = 40;

    public static final String NO_GOAL_OUTCOME_ID = "1716";

    public static final List<String> PLAYER_OUTCOME_IDS = ImmutableList.of(
        santiagoAriasNaranjo().getId(),
        caulyOliveiraSouza().getId(),
        marcosFelipeDeFreitasMonteiro().getId(),
        rodrigoNestorBertalia().getId()
    );

    public static final List<String> UEFA_2024_GERMANY_VS_SCOTLAND_PLAYER_OUTCOME_IDS = ImmutableList.of(
        NEUER_MANUEL_PLAYER_ID,
        RUDIGER_ANTONIO_PLAYER_ID,
        RAUM_DAVID_PLAYER_ID,
        TAH_JONATHAN_PLAYER_ID,
        GROSS_PASCAL_PLAYER_ID,
        KIMMICH_JOSHUA_PLAYER_ID
    );
}
