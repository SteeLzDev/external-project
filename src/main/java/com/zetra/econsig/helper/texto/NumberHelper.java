package com.zetra.econsig.helper.texto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * <p>Title: NumberHelper</p>
 * <p>Description: auxiliar para tratamento de valores numéricos.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class NumberHelper {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(NumberHelper.class);

    // FORMAT
    /**
     * Método format
     * @param number
     * @param pattern
     * @param minFractionDigits
     * @param maxFractionDigits
     * @param groupingUsed
     * @return - XSS : Seguro pois a entrada que é processada e retornada é um double.
     */
    public static String format(double number, String pattern, int minFractionDigits, int maxFractionDigits, boolean groupingUsed) {
        if (pattern.equals(LocaleHelper.INDIA)) {
            return rupeeFormat(number, minFractionDigits, maxFractionDigits);
        } else {
            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.of(pattern));
            // Set whether or not grouping will be used in this format.
            formatter.setGroupingUsed(groupingUsed);
            formatter.setMinimumFractionDigits(minFractionDigits);
            formatter.setMaximumFractionDigits(maxFractionDigits);
            formatter.setRoundingMode(RoundingMode.HALF_UP);
            return formatter.format(number);
        }
    }

    /**
     * Método format
     * @param number
     * @param pattern
     * @param minFractionDigits
     * @param maxFractionDigits
     * @return - XSS : Seguro pois a entrada que é processada e retornada é um double.
     */
    public static String format(double number, String pattern, int minFractionDigits, int maxFractionDigits) {
        return format(number, pattern, minFractionDigits, maxFractionDigits, false);
    }

    /**
     * Método format
     * @param number
     * @param pattern
     * @param groupingUsed
     * @return - XSS : Seguro pois a entrada que é processada e retornada é um double.
     */
    public static String format(double number, String pattern, boolean groupingUsed) {
        return format(number, pattern, 2, 2, groupingUsed);
    }

    /**
     * Método format
     * @param number
     * @param pattern
     * @return - XSS : Seguro pois a entrada que é processada e retornada é um double.
     */
    public static String format(double number, String pattern) {
        return format(number, pattern, 2, 2, false);
    }

    // REFORMAT
    /**
     * Método reformat
     * @param number
     * @param patternIn
     * @param patternOut
     * @param minFractionDigits
     * @param maxFractionDigits
     * @param groupingUsed
     * @return - XSS : Seguro pois usa parse(number, patternIn) que retorna double.
     * @throws ParseException
     */
    public static String reformat(String number, String patternIn, String patternOut, int minFractionDigits, int maxFractionDigits, boolean groupingUsed) throws ParseException {
        return format(parse(number, patternIn), patternOut, minFractionDigits, maxFractionDigits, groupingUsed);
    }

    /**
     * Método reformat
     * @param number
     * @param patternIn
     * @param patternOut
     * @param minFractionDigits
     * @param maxFractionDigits
     * @return - XSS : Seguro pois usa parse(number, patternIn) que retorna double.
     * @throws ParseException
     */
    public static String reformat(String number, String patternIn, String patternOut, int minFractionDigits, int maxFractionDigits) throws ParseException {
        return format(parse(number, patternIn), patternOut, minFractionDigits, maxFractionDigits);
    }

    /**
     * Método reformat
     * @param number
     * @param patternIn
     * @param patternOut
     * @param groupingUsed
     * @return - XSS : Seguro pois usa parse(number, patternIn) que retorna double.
     * @throws ParseException
     */
    public static String reformat(String number, String patternIn, String patternOut, boolean groupingUsed) throws ParseException {
        return format(parse(number, patternIn), patternOut, groupingUsed);
    }

    /**
     * Método reformat
     * @param number
     * @param patternIn
     * @param patternOut
     * @return - XSS : Seguro pois usa parse(number, patternIn) que retorna double.
     * @throws ParseException
     */
    public static String reformat(String number, String patternIn, String patternOut) throws ParseException {
        return format(parse(number, patternIn), patternOut);
    }

    // PARSE
    /**
     * Método parse
     * @param number
     * @param pattern
     * @return - XSS : seguro pois retorna double
     * @throws ParseException
     */
    public static double parse(String number, String pattern) throws ParseException {
        if (pattern.equals(LocaleHelper.INDIA) && number != null) {
            // Removes grouping separator
            number = number.replaceAll(",", "");
            // Use US default format
            pattern = "en";
        }

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.of(pattern));
        return formatter.parse(number).doubleValue();
    }

    /**
     * Formata o número de acordo com o padrão passado.
     * OBS: O PADRÃO NÃO É O LOCALE
     *
     * Exemplo:
     * ("#,###; -#,###") = 1.234, -1.234
     * ("R$ #,###.##; -R$ #,###.##") = R$ 1.234,56, -R$ 1.234,56
     *
     * @param number Número a ser formatado.
     * @param pattern Padrão que será aplicado ao número. Padrão deve ser informado para número positivo e negativo ("#,###; -#,###").
     * @return Retorna número formatado. XSS - Seguro pois entrada que é processada para ser exibida é um double.
     */
    public static String formata(double number, String pattern) {
        DecimalFormat  formatador  = new DecimalFormat();
        formatador.applyPattern(pattern);
        return formatador.format(number);
    }

    /**
     * Transforma uma String em um BigDecimal. A String pode estar tanto
     * no formato PT como EN, a única restrição é que o número deve possuir
     * as casas decimais.
     * @param number : o número a ser formatado
     * @return um Bigdecimal que represeta o numero
     */
    public static BigDecimal parseDecimal(String text) {
        try {
            String number = TextHelper.dropSeparator(text);
            while (number.length() < 20) {
                number = "0" + number;
            }
            number = TextHelper.format(number, "##################.##");
            BigDecimal bd = new BigDecimal(number);
            if (text.charAt(0) == '-') {
                bd = bd.negate();
            }
            return bd;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return null;
        }
    }

    /**
     * Retorna um valor randomico de acordo com o seed passado e o número máximo esperado.
     *
     * @param max Valor máximo esperado
     * @param seed
     * @return Um valor randomico
     */
    public static int getRandomNumber(int max, int seed) throws NoSuchAlgorithmException, NoSuchProviderException {
        return getRandomNumber(0, max, seed);
    }

    /**
     * Retorna um valor randomico de acordo com o seed passado e o número máximo esperado.
     *
     * @param max Valor máximo esperado
     * @param seed
     * @return Um valor randomico
     */
    public static int getRandomNumber(int min, int max, int seed) throws NoSuchAlgorithmException, NoSuchProviderException {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG","SUN");
        if (seed > 0) {
            random.setSeed(seed);
        }

        return random.nextInt(max - min + 1) + min;
    }

    /**
     * Retorna a língua padrão do ambiente.
     * OBS: Para evitar situações aonde o import para o LocaleHelper não existisse e isto causasse um erro, foi realizado este método.
     * Isto possibilita que possamos fazer um search&replace sem (teoricamente) nenhum impacto no funcionamento do sistema.
     * @return
     */
    public static String getLang() {
        return LocaleHelper.getLanguage();
    }

    /**
     * Transforma um object em BigDecimal seja ele um BigDecimal, um Double ou uma String
     * @param input
     * @return
     */
    public static BigDecimal objectToBigDecimal(Object input) {
        if (!TextHelper.isNull(input)) {
            if (input instanceof BigDecimal) {
                return (BigDecimal) input;
            } else if (input instanceof Double) {
                return BigDecimal.valueOf((Double) input);
            } else if (input instanceof String) {
                return parseDecimal(input.toString());
            }
        }
        return null;
    }

    /**
     * Transforma um object em Integer seja ele um BigDecimal, um Double ou uma String
     * @param input
     * @return
     */
    public static Integer objectToInteger(Object input) {
        if (!TextHelper.isNull(input)) {
            if (input instanceof BigDecimal) {
                return ((BigDecimal) input).intValue();
            } else if (input instanceof Double) {
                return ((Double) input).intValue();
            } else if (input instanceof BigInteger) {
                return ((BigInteger) input).intValue();
            } else if (input instanceof Integer) {
                return (Integer) input;
            } else if (input instanceof String && TextHelper.isNum(input)) {
                return Integer.valueOf(input.toString());
            }
        }
        return null;
    }

    /**
     * Formata números para o Locale INDIA (LocaleHelper.INDIA), evitando
     * o uso da biblioteca icu4j.
     * @param number
     * @param minFractionDigits
     * @param maxFractionDigits
     * @return
     */
    private static String rupeeFormat(double number, int minFractionDigits, int maxFractionDigits) {
        // Format number input as US format
        final String numberInEnglish = format(number, "en", minFractionDigits, maxFractionDigits, false);
        // Splits by US fractional delimiter
        final String[] values = numberInEnglish.split("\\.");

        final String whole = values[0];
        final char lastDigit = whole.charAt(whole.length() - 1);

        StringBuilder result = new StringBuilder();
        int len = whole.length() - 1;
        int nDigits = 0;

        for (int i = len - 1; i >= 0; i--) {
            result.append(whole.charAt(i));
            nDigits++;
            if (((nDigits % 2) == 0) && (i > 0)) {
                result.append(",");
            }
        }
        result.reverse().append(lastDigit);

        if (values.length > 1) {
            result.append(".").append(values[1]);
        }
        return result.toString();
    }
}
