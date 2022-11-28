package com.vivas.campaignx.utils;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class DateTimeUtils {

    private static volatile DateTimeUtils instance;

    public static DateTimeUtils getInstance() {
        if (Objects.isNull(instance)) {
            synchronized ((DateTimeUtils.class)) {
                if (Objects.isNull(instance)) {
                    instance = new DateTimeUtils();
                }
            }
        }
        return instance;
    }

    public static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss");


}
