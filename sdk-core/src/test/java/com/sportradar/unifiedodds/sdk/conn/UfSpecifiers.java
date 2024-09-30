/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.sportradar.utils.Urn;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;

public final class UfSpecifiers {

    public static String join(UfSpecifier... specifiers) {
        if (specifiers == null) {
            throw new IllegalStateException("Specifiers cannot be null");
        }
        return Arrays.stream(specifiers).map(UfSpecifier::getKeyValue).collect(Collectors.joining("|"));
    }

    public static final class UfMapNrSpecifier extends UfSpecifier<Integer> {

        private UfMapNrSpecifier(int value) {
            super("mapnr", value);
        }

        public static UfMapNrSpecifier mapNr(int value) {
            return new UfMapNrSpecifier(value);
        }
    }

    public static final class UfMinuteSpecifier extends UfSpecifier<Integer> {

        private UfMinuteSpecifier(int value) {
            super("minute", value);
        }

        public static UfMinuteSpecifier minute(int value) {
            return new UfMinuteSpecifier(value);
        }
    }

    public static final class UfInningNrSpecifier extends UfSpecifier<Integer> {

        private UfInningNrSpecifier(int value) {
            super("inningnr", value);
        }

        public static UfInningNrSpecifier inningNr(int value) {
            return new UfInningNrSpecifier(value);
        }
    }

    public static final class UfRunNrSpecifier extends UfSpecifier<Integer> {

        private UfRunNrSpecifier(int value) {
            super("runnr", value);
        }

        public static UfRunNrSpecifier runNr(int value) {
            return new UfRunNrSpecifier(value);
        }
    }

    public static final class UfSetNrSpecifier extends UfSpecifier<Integer> {

        private UfSetNrSpecifier(int value) {
            super("setnr", value);
        }

        public static UfSetNrSpecifier setNr(int value) {
            return new UfSetNrSpecifier(value);
        }
    }

    public static final class UfBreakNrSpecifier extends UfSpecifier<Integer> {

        private UfBreakNrSpecifier(Integer value) {
            super("breaknr", value);
        }

        public static UfBreakNrSpecifier breakNr(int value) {
            return new UfBreakNrSpecifier(value);
        }
    }

    public static final class UfHandicapSpecifier extends UfSpecifier<Double> {

        private UfHandicapSpecifier(double value) {
            super("hcp", value);
        }

        public static UfHandicapSpecifier handicap(double value) {
            return new UfHandicapSpecifier(value);
        }
    }

    public static final class UfHoleNrSpecifier extends UfSpecifier<Integer> {

        private UfHoleNrSpecifier(Integer value) {
            super("holenr", value);
        }

        public static UfHoleNrSpecifier holeNr(int value) {
            return new UfHoleNrSpecifier(value);
        }
    }

    public static final class UfCompetitorSpecifier extends UfSpecifier<Urn> {

        private UfCompetitorSpecifier(Urn urn) {
            super("competitor", urn);
        }

        public static UfCompetitorSpecifier competitor(Urn urn) {
            return new UfCompetitorSpecifier(urn);
        }
    }

    public static final class UfPlayerSpecifier extends UfSpecifier<Urn> {

        private UfPlayerSpecifier(Urn urn) {
            super("player", urn);
        }

        public static UfPlayerSpecifier player(Urn urn) {
            return new UfPlayerSpecifier(urn);
        }
    }

    public static final class UfAppearanceNrSpecifier extends UfSpecifier<Integer> {

        private UfAppearanceNrSpecifier(int value) {
            super("appearancenr", value);
        }

        public static UfAppearanceNrSpecifier appearanceNr(int value) {
            return new UfAppearanceNrSpecifier(value);
        }
    }

    public static final class UfMatchDaySpecifier extends UfSpecifier<Integer> {

        private UfMatchDaySpecifier(int value) {
            super("matchday", value);
        }

        public static UfMatchDaySpecifier matchDay(int value) {
            return new UfMatchDaySpecifier(value);
        }
    }

    @AllArgsConstructor
    public abstract static class UfSpecifier<T> {

        private final String key;

        @Getter
        private final T value;

        public String getKeyValue() {
            return key + "=" + value;
        }

        @Override
        public String toString() {
            return getKeyValue();
        }
    }
}
