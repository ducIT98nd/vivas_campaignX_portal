package com.vivas.campaignx.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Stream;

public class AppUtils {
    protected static final Logger logger = LogManager.getLogger(AppUtils.class);

    public static final int successCode = 1;
    public static final int errorCode = 0;
    public static final int successCodeCampaignCreated = 5;

    private static final String alpha = "abcdefghijklmnopqrstuvwxyz"; // a-z
    private static final String alphaUpperCase = alpha.toUpperCase(); // A-Z
    private static final String digits = "0123456789"; // 0-9
    private static final String specials = "~=+%^*/()[]{}/!@#$?|";
    private static final String ALPHA_NUMERIC = alpha + alphaUpperCase + digits;
    private static final String ALL = alpha + alphaUpperCase + digits + specials;
    public static final String CURRENCY_FORMAT_VN = "###.###,##";
    private static Random generator = new Random();

    public static final String DEFAULT_DATE_FORMAT_PATTERN = "dd/MM/yyyy";
    public static final String DEFAULT_DATE_TIME_FORMAT_PATTERN = "dd/MM/yyyy hh:mm:ss";
    public static final String DEFAULT_DATE_TIME_FORMAT_PATTERN_1 = "yyyy/MM/dd HH:mm:ss";
    
    public static String randomAlphaNumeric(int numberOfCharactor) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numberOfCharactor; i++) {
            int number = randomNumber(0, ALPHA_NUMERIC.length() - 1);
            char ch = ALPHA_NUMERIC.charAt(number);
            sb.append(ch);
        }
        return sb.toString();
    }
    
    public static int randomNumber(int min, int max) {
        return generator.nextInt((max - min) + 1) + min;
    }
    
    public static Throwable getrootcause(Exception e) {
        Optional<Throwable> rootCause =
                Stream.iterate(e, Throwable::getCause).filter(element -> element.getCause() == null).findFirst();
        return rootCause.orElse(null);
    }
    
    public static boolean isStringNullOrEmpty(String input) {
        return input == null || input.isEmpty();
    }
    
    public static String ObjectToJsonResponse(Object object) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(object);
        } catch (Exception ex) {
            logger.error("Có lỗi xảy ra khi parse json: ", ex);
        }
        return "{code:0, messsage:\"Có lỗi xảy ra, vui lòng liên hệ quản trị viên!\"}";
    }

    public static <T> T jsonToObject(String json, Class<T> objectClass) throws AppException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return (T) objectMapper.readValue(json, objectClass);
        } catch (Exception ex) {
            logger.error("error", ex);
            throw new AppException("Có lỗi xảy ra khi xử lý json");
        }
    }

    public static String objectToJson(Object object) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(object);
        } catch (Exception ex) {
            logger.error("Có lỗi xảy ra khi parse json: ", ex);
        }
        return "{code:500, messsage:\"Không thể kết nối tới máy chủ do hệ thống đang bận. Vui lòng thử lại!\"}";
    }

    public static Date convertStringToDate(String time, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }
    
    public static String convertDateToString(Date date, String pattern) {
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        String strDate = dateFormat.format(date);
        return strDate;
    }

    public static String getLocalDateTimeByPattern() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return (dtf.format(now));
    }

    public static String formatDefaultDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT_PATTERN);
        String strDate = dateFormat.format(date);
        return strDate;
    }
    
    public static String formatDefaultDatetime(Date date) {
        DateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT_PATTERN);
        String strDate = dateFormat.format(date);
        return strDate;
    }

    public static String formatDefaultDatetime1(Date date) {
        DateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT_PATTERN_1);
        String strDate = dateFormat.format(date);
        return strDate;
    }

    public static String saveFile(MultipartFile file, String pathFile) {
        String fileName = "target_group_" + randomAlphaNumeric(8) + getFileExtension(file.getOriginalFilename());
        Path path = Paths.get(pathFile + fileName);
        try {
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.error("Error while save file", e);
        }
        String fileDownloadUri = path.toString();
        return fileDownloadUri;
    }

    public static String getFileExtension(String file) {
        String extension = "";
        try {
            extension = file.substring(file.lastIndexOf("."));
        } catch (Exception e) {
            extension = "";
        }
        return extension;
    }

    public static boolean deleteFtpFile(FTPClient ftpClient, String fileToDelete) {
        boolean deleted = false;
        try {
            deleted = ftpClient.deleteFile(fileToDelete);
            if (deleted) {
                logger.info("FTP: The file was deleted successfully.");
            } else {
                logger.info("FTP: Could not delete the file.");
            }
        } catch (IOException ex) {
            logger.info("FTP: Oh no, there was an error: " + ex.getMessage());
        }
        return deleted;
    }

    /**
     * Compare a == b
     *
     * @param a
     * @param b
     * @return
     */
    public static boolean equal(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) == 0;
    }

    /**
     * compare a > b
     *
     * @param a
     * @param b
     * @return a.compareTo(b) = 1
     */
    public static boolean greaterThan(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) > 0;
    }

    public static BigDecimal divide(BigDecimal a, BigDecimal b) {
        MathContext mc = new MathContext(8);
        return a.divide(b, mc).setScale(2, RoundingMode.FLOOR);
    }

    public static String dateToString(Date date, String format) {
        DateFormat df = new SimpleDateFormat(format);
        String todayAsString = df.format(date);
        return todayAsString;
    }

    public static String formatStringDate(String date, String fromFormat, String toFormat) {
        SimpleDateFormat dateFormat1 = new SimpleDateFormat(fromFormat);
        SimpleDateFormat dateFormat2 = new SimpleDateFormat(toFormat);
        String result = null;
        try {
            java.util.Date tmp = dateFormat1.parse(date.trim());
            result = dateFormat2.format(tmp);
        } catch (Exception e) {
            logger.error("error: ", e);
        }
        return result;
    }

    public static Long readFileExcel(String path) {
        logger.info("start read file: " + path);
        Set<String> setValid = new HashSet<String>();
        logger.info("start read file: " + path);
        long start = System.currentTimeMillis();
        try {
            ReadableWorkbook wb = new ReadableWorkbook(new File(path));
            Sheet sheet = wb.getFirstSheet();
            try (Stream<Row> rows = sheet.openStream()) {
                rows.forEach(r -> {
                    String msisdn1 = r.getCellRawValue(0).orElse(null);
                    String msisdn2 = r.getCellRawValue(1).orElse(null);
                    String msisdn3 = r.getCellRawValue(2).orElse(null);
                    String msisdn4 = r.getCellRawValue(3).orElse(null);
                    String msisdn5 = r.getCellRawValue(4).orElse(null);
                    String msisdn6 = r.getCellRawValue(5).orElse(null);
                    String msisdn7 = r.getCellRawValue(6).orElse(null);
                    String msisdn8 = r.getCellRawValue(7).orElse(null);
                    String msisdn9 = r.getCellRawValue(8).orElse(null);
                    String msisdn10 = r.getCellRawValue(9).orElse(null);

                    if(msisdn1 != null && msisdn1.length() > 0) {
                        setValid.add(msisdn1);
                    }
                    if(msisdn2 != null && msisdn2.length() > 0) {
                        setValid.add(msisdn2);
                    }
                    if(msisdn3 != null && msisdn3.length() > 0) {
                        setValid.add(msisdn3);
                    }
                    if(msisdn4 != null && msisdn4.length() > 0) {
                        setValid.add(msisdn4);
                    }
                    if(msisdn5 != null && msisdn5.length() > 0) {
                        setValid.add(msisdn5);
                    }
                    if(msisdn6 != null && msisdn6.length() > 0) {
                        setValid.add(msisdn6);
                    }
                    if(msisdn7 != null && msisdn7.length() > 0) {
                        setValid.add(msisdn7);
                    }
                    if(msisdn8 != null && msisdn8.length() > 0) {
                        setValid.add(msisdn8);
                    }
                    if(msisdn9 != null && msisdn9.length() > 0) {
                        setValid.add(msisdn9);
                    }
                    if(msisdn10 != null && msisdn10.length() > 0) {
                        setValid.add(msisdn10);
                    }
                });
            }finally {
                wb.close();
            }

        }catch(Exception e) {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();
        logger.info("end read file: " + path);
        logger.info("time: " + (end - start) + " ms");
        return (long)setValid.size();
    }


    public static Set<String> readFileExcel1(String path) {
        Set<String> setValid = new HashSet<String>();
        logger.info("start read file: " + path);
        long start = System.currentTimeMillis();
        try {
            ReadableWorkbook wb = new ReadableWorkbook(new File(path));
            Sheet sheet = wb.getFirstSheet();
            try (Stream<Row> rows = sheet.openStream()) {
                rows.forEach(r -> {
                    String msisdn1 = r.getCellRawValue(0).orElse(null);
                    String msisdn2 = r.getCellRawValue(1).orElse(null);
                    String msisdn3 = r.getCellRawValue(2).orElse(null);
                    String msisdn4 = r.getCellRawValue(3).orElse(null);
                    String msisdn5 = r.getCellRawValue(4).orElse(null);
                    String msisdn6 = r.getCellRawValue(5).orElse(null);
                    String msisdn7 = r.getCellRawValue(6).orElse(null);
                    String msisdn8 = r.getCellRawValue(7).orElse(null);
                    String msisdn9 = r.getCellRawValue(8).orElse(null);
                    String msisdn10 = r.getCellRawValue(9).orElse(null);

                    if (msisdn1 != null && msisdn1.length() > 0) {
                        setValid.add(msisdn1);
                    }
                    if (msisdn2 != null && msisdn2.length() > 0) {
                        setValid.add(msisdn2);
                    }
                    if (msisdn3 != null && msisdn3.length() > 0) {
                        setValid.add(msisdn3);
                    }
                    if (msisdn4 != null && msisdn4.length() > 0) {
                        setValid.add(msisdn4);
                    }
                    if (msisdn5 != null && msisdn5.length() > 0) {
                        setValid.add(msisdn5);
                    }
                    if (msisdn6 != null && msisdn6.length() > 0) {
                        setValid.add(msisdn6);
                    }
                    if (msisdn7 != null && msisdn7.length() > 0) {
                        setValid.add(msisdn7);
                    }
                    if (msisdn8 != null && msisdn8.length() > 0) {
                        setValid.add(msisdn8);
                    }
                    if (msisdn9 != null && msisdn9.length() > 0) {
                        setValid.add(msisdn9);
                    }
                    if (msisdn10 != null && msisdn10.length() > 0) {
                        setValid.add(msisdn10);
                    }

                });
            } finally {
                wb.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();
        logger.info("end read file: " + path);
        logger.info("time: " + (end - start) + " ms");
        return setValid;
    }


    public static Long readFileCSV(String path) {
        logger.info("start read file: " + path);
        long start = System.currentTimeMillis();
        Set<String> setValid = new HashSet<String>();
        try (Reader inputReader = new InputStreamReader(new FileInputStream(new File(path)), "UTF-8")) {
            logger.info("readfile");
            CsvParserSettings settings = new CsvParserSettings();
            settings.getFormat().setDelimiter(',');
            settings.getFormat().setLineSeparator("\n");
            CsvParser parser = new CsvParser(settings);
            List<String[]> parsedRows = parser.parseAll(inputReader);
            parsedRows.stream().forEach(f -> {
                if (f != null) {
                    if (f[0] != null && f[0].trim().length() > 0) {
                        String msisdn = f[0];
                        setValid.add(msisdn);
                    }
                }
            });
        } catch (Exception e) {
            logger.error("Error while read file", e);
        }
        long end = System.currentTimeMillis();
        logger.info("end read file: " + path);
        logger.info("time: " + (end - start) + " ms");
        return (long)setValid.size();
    }

    public static Set<String> readFileCSVToSet(String path) {
        logger.info("start read file: " + path);
        long start = System.currentTimeMillis();
        Set<String> setValid = new HashSet<String>();
        try (Reader inputReader = new InputStreamReader(new FileInputStream(new File(path)), "UTF-8")) {
            logger.info("readfile");
            CsvParserSettings settings = new CsvParserSettings();
            settings.getFormat().setDelimiter(',');
            settings.getFormat().setLineSeparator("\n");
            CsvParser parser = new CsvParser(settings);
            List<String[]> parsedRows = parser.parseAll(inputReader);
            parsedRows.stream().forEach(f -> {
                if (f != null) {
                    if (f[0] != null && f[0].trim().length() > 0) {
                            String msisdn = f[0];
                            setValid.add(msisdn);
                    }
                }
            });
        } catch (Exception e) {
            logger.error("Error while read file", e);
        }
        long end = System.currentTimeMillis();
        logger.info("end read file: " + path);
        logger.info("time: " + (end - start) + " ms");
        return setValid;
    }
}
