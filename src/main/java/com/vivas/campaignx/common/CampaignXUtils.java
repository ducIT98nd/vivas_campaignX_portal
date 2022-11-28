package com.vivas.campaignx.common;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.*;
import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StringUtils;

public final class CampaignXUtils {

    private final Logger logger = LogManager.getLogger(this.getClass().getName());

    private static CampaignXUtils INSTANCE = null;
    public static final Locale VIETNAM_LOCALE = new Locale("vi", "VN");

    private CampaignXUtils() {
        if (INSTANCE != null) {
            throw new RuntimeException("Already initial.");
        }
    }

    public static CampaignXUtils getInstance() {
        if (INSTANCE == null) {
            synchronized (CampaignXUtils.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CampaignXUtils();
                }
            }
        }
        return INSTANCE;
    }

    private boolean isValidTextInput(String date, String pattern) {
        if (!StringUtils.hasText(date) || !StringUtils.hasText(pattern)) {
            return false;
        }
        return true;
    }

    private DecimalFormat getDefaultVietNamNumberFormat() {
        DecimalFormat vnNumberDecimalFormat = (DecimalFormat) NumberFormat
                .getNumberInstance(VIETNAM_LOCALE);
        vnNumberDecimalFormat.applyLocalizedPattern(AppUtils.CURRENCY_FORMAT_VN);
        vnNumberDecimalFormat.setRoundingMode(RoundingMode.UP);
        vnNumberDecimalFormat.setParseBigDecimal(false);
        return vnNumberDecimalFormat;
    }

    public String formatDefaultVnNumber(BigDecimal numberValue) {
        if (numberValue == null) {
            return null;
        }
        DecimalFormat vnNumberFormat = this.getDefaultVietNamNumberFormat();
        return vnNumberFormat.format(numberValue);
    }

    private Boolean isNumber(String str) {
        try {
            BigInteger i1 = new BigInteger(str);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public String defaultLikeQueryEscape(String input) {
        if (StringUtils.isEmpty(input)) {
            return input;
        }
//        List<String> specialCharacters = Arrays.asList("\\", "^", "$", "{", "}", "[", "]", "(", ")", ".", "*", "+", "?",
//                "|", "<", ">", "-", "&", "%");
        List<String> specialCharactersWildcard = Arrays.asList("\\", "_", "%");
        return Arrays.stream(input.split("")).map((c) -> {
            if (specialCharactersWildcard.contains(c))
                return "\\" + c;
            else
                return c;
        }).collect(Collectors.joining());
    }

    public String formatStringForQuery(String value) {
        String queryStr;
        if (StringUtils.isEmpty(value)) {
            return value;
        }
        String vnCharacterStr = VNCharacterUtils.removeAccent(value);
        queryStr = CampaignXUtils.getInstance().defaultLikeQueryEscape(vnCharacterStr);
        return queryStr;
    }
}
