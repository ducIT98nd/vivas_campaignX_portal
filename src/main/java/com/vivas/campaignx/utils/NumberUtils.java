package com.vivas.campaignx.utils;

import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

public final class NumberUtils {
    public static final DecimalFormat vnCurrencyNF = ((DecimalFormat) NumberFormat.getNumberInstance(new Locale("vi", "VN")));


    private static volatile NumberUtils instance;

    public static NumberUtils getInstance() {
        if (Objects.isNull(instance)) {
            synchronized ((NumberUtils.class)) {
                if (Objects.isNull(instance)) {
                    instance = new NumberUtils();
                }
            }
        }
        return instance;
    }

    public static String convertBigDecimalToCurrency(BigDecimal value) {
        if (ObjectUtils.isEmpty(value)) {
            return "0";
        }
        return vnCurrencyNF.format(value);
    }
}
