package com.udipoc.api.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Random;

public class MathUtils {

    public static String generateRandomNumber() {
        return StringUtils.leftPad(String.valueOf(new Random().nextInt(999999999)), 10, "0");
    }
}
