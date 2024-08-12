/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.ci;

import static org.assertj.core.api.Assertions.assertThat;

import com.sportradar.uf.sportsapi.datamodel.SapiHole;
import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class HoleCiTest {

    @Nested
    public class WhenConstructedFromDto {

        @Test
        public void preserveHoleNumber() {
            final int holeNumber = 5;
            SapiHole dtoHole = new SapiHole();
            dtoHole.setNumber(holeNumber);

            final val hole = new HoleCi(dtoHole);

            assertThat(hole.getNumber()).isEqualTo(holeNumber);
        }

        @Test
        public void preserveHolePar() {
            final int par = 4;
            SapiHole dtoHole = new SapiHole();
            dtoHole.setPar(par);

            final val hole = new HoleCi(dtoHole);

            assertThat(hole.getPar()).isEqualTo(par);
        }
    }

    @Nested
    public class WhenReImportedAfterExporting {

        public static final int ANY = 0;

        @Test
        public void preserveHoleNumber() throws Exception {
            final int holeNumber = 5;
            SapiHole dtoHole = new SapiHole();
            dtoHole.setNumber(holeNumber);
            final val hole = new HoleCi(dtoHole);

            HoleCi reImportedHole = HoleCis.exportSerializeAndUseConstructorToReimport(hole);

            assertThat(reImportedHole.getNumber()).isEqualTo(holeNumber);
        }

        @Test
        public void preserveHolePar() throws Exception {
            final int par = 4;
            SapiHole dtoHole = new SapiHole();
            dtoHole.setPar(par);
            final val hole = new HoleCi(dtoHole);

            HoleCi reImportedHole = HoleCis.exportSerializeAndUseConstructorToReimport(hole);

            assertThat(reImportedHole.getPar()).isEqualTo(par);
        }
    }
}
