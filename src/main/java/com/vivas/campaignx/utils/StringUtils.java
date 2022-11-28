package com.vivas.campaignx.utils;

import java.util.Objects;

public final class StringUtils {

    private static volatile StringUtils instance;

    public static StringUtils getInstance() {
        if (Objects.isNull(instance)) {
            synchronized ((StringUtils.class)) {
                if (Objects.isNull(instance)) {
                    instance = new StringUtils();
                }
            }
        }
        return instance;
    }
}
