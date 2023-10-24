/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.utils.domain.names;

import static java.util.Arrays.asList;

import java.util.Random;
import lombok.val;

public class Names {

    public static final Random RANDOM = new Random();

    private Names() {}

    public static String any() {
        val listOfNames = asList(
            "Anapa Mars",
            "Daidalos Shyama",
            "Aonghus Hut-Heru",
            "Brünhild Inti",
            "Saam Eos",
            "Bébinn Poseidon",
            "Daireann Indrani",
            "Onuphrius Bedivere",
            "Agaue Iris",
            "Radha Wōdanaz"
        );
        int enoughElementsToSatisfyTypicalTest = listOfNames.size();
        return listOfNames.get(RANDOM.nextInt(enoughElementsToSatisfyTypicalTest));
    }

    public static String anyEnglish() {
        return "English " + any();
    }

    public static String anyFrench() {
        return "French " + any();
    }
}
