package com.zetra.econsig.helper.texto;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.CodedValues;

/**
 * This class contains helper methods for dealing with
 * Date objects.
 */

public final class DateHelper {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DateHelper.class);

    public static Date getSystemDatetime() {
        return Calendar.getInstance().getTime();
    }

    public static Date getSystemDate() {
        return clearHourTime(Calendar.getInstance().getTime());
    }

    public static Date getDate(int year, int month, int day, int hour, int minute) {
        // returns a Date with the specified time elements
        final Calendar cal = new GregorianCalendar(year, intToCalendarMonth(month), day, hour, minute);
        return cal.getTime();
    }

    public static Date getDate(int year, int month, int day) {
        // returns a Date with the specified time elements,
        // with the hour and minutes both set to 0 (midnight)
        final Calendar cal = new GregorianCalendar(year, intToCalendarMonth(month), day);
        return cal.getTime();
    }

    public static Date addMonths(Date target, int months) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(target);
        cal.add(Calendar.MONTH, months);
        return cal.getTime();
    }

    public static Date addDays(Date target, int days) {
        // returns a Date that is the sum of the target Date
        // and the specified number of days;
        // to subtract days from the target Date, the days
        // argument should be negative
        final long msPerDay = 1000 * 60 * 60 * 24;
        final long msTarget = target.getTime();
        final long msSum = msTarget + (msPerDay * days);
        final Date result = new Date();
        result.setTime(msSum);
        return result;
    }

    public static Date addHours(Date target, int hours) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(target);
        cal.add(Calendar.HOUR, hours);
        return cal.getTime();
    }

    public static Date addSeconds(Date target, int seconds) {
       final long msPerSeconds = 1000;
       final long msTarget = target.getTime();
       final long msSum = msTarget + (msPerSeconds * seconds);
       final Date result = new Date();
       result.setTime(msSum);
       return result;
   }

    public static int minDiff(Date date) {
        // returns the difference, in minutes, between the Date argument
        // and current date
        final Date now = Calendar.getInstance().getTime();
        final long msPermin = 1000 * 60;
        final long diff = (now.getTime() / msPermin) - (date.getTime() / msPermin);
        final Long convertLong = diff;
        return convertLong.intValue();
    }

    public static int dayDiff(Date date) {
        // returns the difference, in days, between the Date argument
        // and current date
        final Date now = Calendar.getInstance().getTime();
        return dayDiff(now, date);
    }

    /**
     * Retorna a diferença em dias entre duas datas. Se first > second o valor retornado
     * será posítivo. Se first < second, o valor será negativo.
     * @param first
     * @param second
     * @return
     */
    public static int dayDiff(Date first, Date second) {
        // returns the difference, in days, between the first
        // and second Date arguments
        final long msPerDay = 1000 * 60 * 60 * 24;
        final long diff = (clearHourTime(first).getTime() / msPerDay) - (clearHourTime(second).getTime() / msPerDay);
        final Long convertLong = diff;
        return convertLong.intValue();
    }

    public static int monthDiff(Date biggest, Date smallest) {
        final Calendar cal1 = Calendar.getInstance();
        final Calendar cal2 = Calendar.getInstance();

        cal1.setTime(biggest);
        cal2.setTime(smallest);

        return ((cal1.get(Calendar.YEAR) * 12) + cal1.get(Calendar.MONTH)) -
                ((cal2.get(Calendar.YEAR) * 12) + cal2.get(Calendar.MONTH));
    }

    public static int yearDiff(Date date1, Date date2) {
        final Calendar cal1 = Calendar.getInstance();
        final Calendar cal2 = Calendar.getInstance();

        cal1.setTime(date1);
        cal2.setTime(date2);

        int years = -1;
        while (!cal1.after(cal2)) {
            cal1.add(Calendar.YEAR, 1);
            years++;
        }

        return years;
    }

    public static Date clearHourTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal = DateHelper.clearHourTime(cal);
        return cal.getTime();
    }

    /**
     * retorna apenas as horas e minutos do objeto Date
     * @param date
     * @return
     */
    public static Date clearData(Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 0);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.YEAR, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    public static Calendar clearHourTime(Calendar date) {
        final Calendar cal = (Calendar) date.clone();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    /**
     * retorna apenas mês e dia do objeto Date
     * @param date
     * @return
     */
    public static Date clearYearHourTime(Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.YEAR, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static int getYear(Date date) {
        final Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    public static int getMonth(Date date) {
        final Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        final int calendarMonth = cal.get(Calendar.MONTH);
        return calendarMonthToInt(calendarMonth);
    }

    public static int getDay(Date date) {
        final Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    public static int getHour(Date date) {
        final Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    public static int getMinute(Date date) {
        final Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal.get(Calendar.MINUTE);
    }

    public static Date getEndOfDay(Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE,      cal.getMaximum(Calendar.MINUTE));
        cal.set(Calendar.SECOND,      cal.getMaximum(Calendar.SECOND));
        // MILLISECOND should be zero because MySQL limitation of date field.
        // If MILLISECOND get its maximum, date in MySQL will be set to next day.
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private static int calendarMonthToInt(int calendarMonth) {
        return switch (calendarMonth) {
            case Calendar.JANUARY -> 1;
            case Calendar.FEBRUARY -> 2;
            case Calendar.MARCH -> 3;
            case Calendar.APRIL -> 4;
            case Calendar.MAY -> 5;
            case Calendar.JUNE -> 6;
            case Calendar.JULY -> 7;
            case Calendar.AUGUST -> 8;
            case Calendar.SEPTEMBER -> 9;
            case Calendar.OCTOBER -> 10;
            case Calendar.NOVEMBER -> 11;
            case Calendar.DECEMBER -> 12;
            default -> 1;
        };
    }

    public static String format(Date date, String pattern) {
        // returns a String representation of the date argument,
        // formatted according to the pattern argument, which
        // has the same syntax as the argument of the SimpleDateFormat
        // class1E
        if (date == null) {
            return "";
        }

        final SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(date);
    }

    /**
     * Reformata uma data com o padrao de entrada 'patternIn' para o padrao de
     * @param date
     * @param patternIn
     * @param patternOut
     * @return - XSS : Seguro pois durante o processamento da entrada ela é convertida para tipo Date
     * @throws ParseException
     *
     * Date and Time Pattern Examples -> Result
     * "yyyy.MM.dd G 'at' HH:mm:ss z" 2001.07.04 AD at 12:08:56 PDT
     * "EEE, MMM d, ''yy"             Wed, Jul 4, '01
     * "h:mm a"                       12:08 PM
     * "hh 'o''clock' a, zzzz"        12 o'clock PM, Pacific Daylight Time
     * "K:mm a, z"                    0:08 PM, PDT
     * "yyyyy.MMMMM.dd GGG hh:mm aaa" 02001.July.04 AD 12:08 PM
     * "EEE, d MMM yyyy HH:mm:ss Z"   Wed, 4 Jul 2001 12:08:56 -0700
     * "yyMMddHHmmssZ"                010704120856-0700
     * "dd/MM/yyyy HH:mm:ss"          01/01/1970 19:13:20
     * "dd/MM/yyyy"                   01/01/1970
     */
    public static String reformat(String date, String patternIn, String patternOut) throws ParseException {
        return format(parse(date, patternIn), patternOut);
    }

    /**
     * Transforma uma string no padrao especificado em um Date
     */
    public static Date parse(String date, String pattern) throws ParseException {
        final SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return (formatter.parse(date));
    }

    public static Date parseExceptionSafe(String date, String pattern) {
        try {
            return parse(date, pattern);
        } catch (final ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            return null;
        }
    }

    /**
     * Verifica se a string está no padrão informado por parâmetro.
     * @param date
     * @param pattern
     * @return
     */
    public static boolean verifyPattern(String date, String pattern) {
        try {
            if (date != null) {
                parse(date, pattern);
                return true;
            } else {
                return false;
            }
        } catch (final ParseException ex) {
            return false;
        }
    }

    private static int intToCalendarMonth(int month) {
        return switch (month) {
            case 1 -> Calendar.JANUARY;
            case 2 -> Calendar.FEBRUARY;
            case 3 -> Calendar.MARCH;
            case 4 -> Calendar.APRIL;
            case 5 -> Calendar.MAY;
            case 6 -> Calendar.JUNE;
            case 7 -> Calendar.JULY;
            case 8 -> Calendar.AUGUST;
            case 9 -> Calendar.SEPTEMBER;
            case 10 -> Calendar.OCTOBER;
            case 11 -> Calendar.NOVEMBER;
            case 12 -> Calendar.DECEMBER;
            default -> Calendar.JANUARY;
        };
    }

    public static String toDateString(Date date) {
        // Retorna um String com a data no formato 'dd/mm/aaaa'
        return format(date, LocaleHelper.getDatePattern());
    }

    public static String toDateTimeString(Date date) {
        // Retorna um String com a data no formato 'dd/mm/aaaa hh:mm:ss'
        return format(date, LocaleHelper.getDateTimePattern());
    }

    /**
     * Retorna um String com a data no formato 'mmm/aaaa', ou 'nn/aaaa' caso a periodicidade da folha
     * não seja mensal, e 'nn' é o número do período
     * @param date
     * @return
     */
    public static String toPeriodMesExtensoString(Date date) {
        if (!PeriodoHelper.folhaMensal(AcessoSistema.getAcessoUsuarioSistema())) {
            return toPeriodString(date);
        } else {
            return format(date, "MMM/yyyy");
        }
    }

    /**
     * Retorna um String com a data no formato 'mm/aaaa', ou 'nn/aaaa' caso a periodicidade da folha
     * não seja mensal, e 'nn' é o número do período
     * @param date
     * @return
     */
    public static String toPeriodString(Date date) {
        if (date != null) {
            if (!PeriodoHelper.folhaMensal(AcessoSistema.getAcessoUsuarioSistema())) {
                final int dia = getDay(date);
                final int mes = getMonth(date);
                final int ano = getYear(date);
                int numPeriodo = mes;

                final String periodicidade = PeriodoHelper.getPeriodicidadeFolha(AcessoSistema.getAcessoUsuarioSistema());
                if (CodedValues.PERIODICIDADE_FOLHA_QUINZENAL.equals(periodicidade) || CodedValues.PERIODICIDADE_FOLHA_QUATORZENAL.equals(periodicidade)) {
                    // (M*2 + D - 2)
                    numPeriodo = ((mes * 2) + dia) - 2;
                } else if (CodedValues.PERIODICIDADE_FOLHA_SEMANAL.equals(periodicidade)) {
                    // (M*4 + D - 4)
                    numPeriodo = ((mes * 4) + dia) - 4;
                }

                return String.format("%02d", numPeriodo) + "/" + ano;
            } else {
                return format(date, "MM/yyyy");
            }
        } else {
            return "";
        }
    }

    /**
     * Retorna uma Data referente ao período, de acordo com a
     * periodicidade da folha. Se mensal, a data terá a parte
     * do dia setado para "01". Se quinzenal, manterá a parte
     * de dia, fazendo apenas a conversão para java.sql.Date.
     * @param date
     * @return
     */
    public static java.sql.Date toPeriodDate(Date date) {
        if (!PeriodoHelper.folhaMensal(AcessoSistema.getAcessoUsuarioSistema())) {
            return toSQLDate(date);
        } else {
            final Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            return toSQLDate(cal.getTime());
        }
    }

    /**
     * Retorna uma Data referente ao período, de acordo com a
     * periodicidade da folha. Se mensal, utiliza a máscara de
     * mês/ano, e a parte do dia será "01". Se quinzenal, utiliza
     * máscara de dia/mês/ano.
     * @param dateString
     * @return
     * @throws ParseException
     */
    public static java.sql.Date parsePeriodString(String dateString) throws ParseException {
        if (!PeriodoHelper.folhaMensal(AcessoSistema.getAcessoUsuarioSistema())) {
            final String[] partes = (dateString != null ? dateString.split("/") : null);
            if ((partes == null) || (partes.length != 2) || !TextHelper.isNum(partes[0]) || !TextHelper.isNum(partes[1])) {
                throw new RuntimeException(ApplicationResourcesHelper.getMessage("mensagem.folha.formato.periodo.quinzenal", AcessoSistema.getAcessoUsuarioSistema()));
            } else {
                final int numPeriodo = Integer.parseInt(partes[0]);
                final int ano = Integer.parseInt(partes[1]);
                final String mesDia = PeriodoHelper.converterNumPeriodoParaMesDia(numPeriodo, AcessoSistema.getAcessoUsuarioSistema());
                final String[] partesMesDia = mesDia.split("-");
                final int mes = Integer.parseInt(partesMesDia[0]);
                final int dia = Integer.parseInt(partesMesDia[1]);
                if ((mes < 1) || (mes > 12)) {
                    throw new RuntimeException(ApplicationResourcesHelper.getMessage("mensagem.erro.quinzena.invalida", AcessoSistema.getAcessoUsuarioSistema()));
                }
                return toSQLDate(getDate(ano, mes, dia));
            }
        } else {
            return toPeriodDate(parse(dateString, "MM/yyyy"));
        }
    }

    @Deprecated
    public static int dateDiff(String sdate1, String sdate2, String fmt, TimeZone tz, String calculo) {
        final SimpleDateFormat df = new SimpleDateFormat(fmt);

        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df.parse(sdate1);
            date2 = df.parse(sdate2);
        } catch (final ParseException pe) {
            throw new RuntimeException(pe);
        }

        Calendar cal1 = null;
        Calendar cal2 = null;

        if (tz == null) {
            cal1 = Calendar.getInstance();
            cal2 = Calendar.getInstance();
        } else {
            cal1 = Calendar.getInstance(tz);
            cal2 = Calendar.getInstance(tz);
        }

        // different date might have different offset
        cal1.setTime(date1);
        final long ldate1 = date1.getTime() + cal1.get(Calendar.ZONE_OFFSET) + cal1.get(Calendar.DST_OFFSET);

        cal2.setTime(date2);
        final long ldate2 = date2.getTime() + cal2.get(Calendar.ZONE_OFFSET) + cal2.get(Calendar.DST_OFFSET);

        // Use integer calculation, truncate the decimals
        final int hr1 = (int) (ldate1 / 3600000); //60*60*1000
        final int hr2 = (int) (ldate2 / 3600000);

        final int days1 = hr1 / 24;
        final int days2 = hr2 / 24;

        if (calculo == null) {
            calculo = "";
        }
        final int hourDiff = (int) (ldate2 - ldate1);
        if ("HORAS".equals(calculo)) {
            return hourDiff;
        }
        final int dateDiff = days2 - days1;
        if ("DIAS".equals(calculo)) {
            return dateDiff;
        }
        final int weekOffset = (cal2.get(Calendar.DAY_OF_WEEK) - cal1.get(Calendar.DAY_OF_WEEK)) < 0 ? 1 : 0;
        final int weekDiff = (dateDiff / 7) + weekOffset;
        if ("SEMANAS".equals(calculo)) {
            return weekDiff;
        }
        final int yearDiff = cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR);
        if ("ANOS".equals(calculo)) {
            return yearDiff;
        }
        final int monthDiff = ((yearDiff * 12) + cal2.get(Calendar.MONTH)) - cal1.get(Calendar.MONTH);
        if ("MESES".equals(calculo)) {
            final int offset = cal2.get(Calendar.DAY_OF_MONTH) - cal1.get(Calendar.DAY_OF_MONTH);
            return (offset > 0 ? monthDiff + 1 : monthDiff);
            //return monthDiff;
        } else if ("PRAZO".equals(calculo)) {
            final int prazo = monthDiff + 1;
            return (prazo > 0 ? prazo : 0);
        }

        throw new IllegalArgumentException(ApplicationResourcesHelper.getMessage("mensagem.erro.forma.calculo", (AcessoSistema) null, calculo));
    }

    public static java.util.Date getNextDayOfWeek(Date date, int dayOfWeek) {
        // Retorna o próximo dia, após à data passada, que seja Seg/Ter/Qua/Qui/Sex/Sab
        // ou Dom de acordo com o parâmetro dayOfWeek
        final Calendar cal = Calendar.getInstance();
        if (date != null) {
            cal.setTime(date);
        }
        cal.add(Calendar.DATE, 1);

        if(dayOfWeek > 7) {
            dayOfWeek = 7;
        }

        while (cal.get(Calendar.DAY_OF_WEEK) != dayOfWeek) {
            cal.add(Calendar.DATE, 1);
        }

        return cal.getTime();
    }

    public static java.util.Date getLastDayOfWeek(Date date, int dayOfWeek) {
        // Retorna o último dia, anterior à data passada, que seja Seg/Ter/Qua/Qui/Sex/Sab
        // ou Dom de acordo com o parâmetro dayOfWeek
        final Calendar cal = Calendar.getInstance();
        if (date != null) {
            cal.setTime(date);
        }
        cal.add(Calendar.DATE, -1);
        while (cal.get(Calendar.DAY_OF_WEEK) != dayOfWeek) {
            cal.add(Calendar.DATE, -1);
        }

        return cal.getTime();
    }

    /**
     * Metodo para adicionar ou subtrair um valor de uma data.
     * @param data - data que se deseja alterar.
     * @param tipo - qual valor vai ser alterado, dia, mes ou ano.
     * @param tempo - intervalo a ser modificado.
     * @return
     */
    public static Date dateAdd(Date data, String tipo, int tempo) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        if ("MES".equalsIgnoreCase(tipo)) {
            cal.add(Calendar.MONTH, tempo);
        } else if ("DIA".equalsIgnoreCase(tipo)) {
            cal.add(Calendar.DAY_OF_MONTH, tempo);
        } else if ("ANO".equalsIgnoreCase(tipo)) {
            cal.add(Calendar.YEAR, tempo);
        }
        return cal.getTime();
    }

    public static String getMonthName(Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.getDisplayName(Calendar.MONTH, Calendar.LONG, LocaleHelper.getLocaleObject());
    }

    public static String getWeekDayName(Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, LocaleHelper.getLocaleObject());
    }

    public static String getWeekDayName(int diaDaSemana) {
        final Map<String, Integer> weekNames = Calendar.getInstance().getDisplayNames(Calendar.DAY_OF_WEEK, Calendar.LONG, LocaleHelper.getLocaleObject());
        if (weekNames.containsValue(diaDaSemana)) {
            final Set<String> keySet = weekNames.keySet();

            for (final String week : keySet) {
                final Integer weekInt = weekNames.get(week);

                if (weekInt.intValue() == diaDaSemana) {
                    return week;
                }
            }
        }
        return null;
    }

    /**
     * Retorna a idade de acordo com a data de nascimento informada.
     *
     * This Method is unit tested properly for very different cases ,
     * taking care of Leap Year days difference in a year,
     * and date cases month and Year boundary cases (12/31/1980, 01/01/1980 etc).
     * @param dateOfBirth Data de nascimento
     * @return Retorna idade
     */
    public static int getAge(Date dateOfBirth) {
        return getAge(dateOfBirth, null);
    }

    public static int getAge(Date dateOfBirth, Date dateOfCalculation) {
        final Calendar today = Calendar.getInstance();
        if (dateOfCalculation != null) {
            today.setTime(dateOfCalculation);
        }

        final Calendar birthDate = Calendar.getInstance();
        birthDate.setTime(dateOfBirth);

        if (birthDate.after(today)) {
            throw new IllegalArgumentException(ApplicationResourcesHelper.getMessage("mensagem.erro.data.nascimento.data.futura", (AcessoSistema) null));
        }

        int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

        // If birth date is greater than todays date (after 2 days adjustment of leap year) then decrement age one year
        if ((((birthDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR)) > 3) ||
                (birthDate.get(Calendar.MONTH) > today.get(Calendar.MONTH))) || ((birthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH)) &&
                (birthDate.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH)))) {
            age--;

            // If birth date and todays date are of same month and birth day of month is greater than todays day of month then decrement age
        }

        return age;
    }

    /**
     * This method returns a sql.Date version of the util.Date arg.
     * @param inDate
     * @return
     */
    public static final java.sql.Date toSQLDate(java.util.Date inDate) {
        return new java.sql.Date(clearHourTime(inDate).getTime());
    }

    /**
     * Transforma a data informada em uma string no padrão ISO8601
     * @param d
     * @return
     */
    public static final String toISOString(Date d){
    	// Infelizmente não podemos usar os métodos adicionados no Java 8
    	final TimeZone tz = TimeZone.getTimeZone("UTC");
    	final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
    	df.setTimeZone(tz);
    	return df.format(d);
    }

    /**
     * Transforma a data informada em uma string no padrão ISO8601 com fuso horário local
     * @param d
     * @return
     */
    public static final String toISOStringWithLocalTimeZone(Date d){
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX"); // O "XXX" indica o fuso horário local
        return df.format(d);
    }

    /**
     * Retorna a menor das duas datas, Null Safe
     * @param first
     * @param second
     * @return
     */
    public static final java.util.Date leastDate(java.util.Date first, java.util.Date second) {
        if ((first != null) && ((second == null) || first.before(second))) {
            return first;
        } else if ((second != null) && ((first == null) || second.before(first))) {
            return second;
        } else {
            // Equal or booth Null
            return first;
        }
    }

    /**
     * Muda o ANO de um calendário tratando o mês de fevereiro
     * quanto ao dia 28 ou 29.
     * @param cal
     * @param year
     * @return
     */
    public static Calendar setYearOfCalendar(Calendar cal, int year) {
        // Caso seja dia 29 volta para o dia 28
        if ((cal.get(Calendar.MONTH) == Calendar.FEBRUARY) && ((cal.get(Calendar.DAY_OF_MONTH) == 28) || (cal.get(Calendar.DAY_OF_MONTH) == 29))) {
            if (cal.get(Calendar.DAY_OF_MONTH) == 29) {
                cal.set(Calendar.DAY_OF_MONTH, 28);
            }

            cal.set(Calendar.YEAR, year);

            // Caso dia 28 não seja o ultimo dia do mês, seta para dia 29
            if (cal.get(Calendar.DAY_OF_MONTH) < cal.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                cal.set(Calendar.DAY_OF_MONTH, 29);
            } else if (cal.get(Calendar.DAY_OF_MONTH) > cal.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                cal.set(Calendar.DAY_OF_MONTH, 28);
                cal.set(Calendar.MONTH, Calendar.FEBRUARY);
            }
        } else {
            cal.set(Calendar.YEAR, year);
        }
        return cal;
    }

    /**
     * Transforma o objeto de entrada em um Date, seja ele um Date ou um String.
     * Sendo String poderá estar no formato americano, ou no formato do Locale do sistema.
     * @param input
     * @return
     */
    public static Date objectToDate(Object input) {
        try {
            if (!TextHelper.isNull(input)) {
                if (input instanceof Date) {
                    return (Date) input;
                } else if (input instanceof final LocalDateTime adeDataLDT) {
                    final Instant instant = adeDataLDT.atZone(ZoneId.systemDefault()).toInstant();
                    return Date.from(instant);
                } else if (input instanceof String) {
                    if (verifyPattern(input.toString(), "yyyy-MM-dd")) {
                        return parse(input.toString(), "yyyy-MM-dd");
                    } else if (verifyPattern(input.toString(), LocaleHelper.getDatePattern())) {
                        return parse(input.toString(), LocaleHelper.getDatePattern());
                    } else if (verifyPattern(input.toString(), "yyyy-MM-dd HH:mm:ss")) {
                        return parse(input.toString(), "yyyy-MM-dd HH:mm:ss");
                    } else if (verifyPattern(input.toString(), "yyyy-MM-dd'T'HH:mm:ss")) {
                        return parse(input.toString(), "yyyy-MM-dd'T'HH:mm:ss");
                    }
                }
            }
        } catch (final ParseException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * Retorna a faixa etária do beneficiário de acordo com a data de nascimento informada.
     *
     * @param dateOfBirth Data de nascimento
     * @return Retorna faixa etária
     */
    public static String getFaixaEtariaBeneficios(Date dateOfBirth) {
        final int idade = getAge(dateOfBirth, null);
        if ((idade >= 0) && (idade <=18)) {
            return "0 - 18";
        } else if ((idade >= 19) && (idade <=23)) {
            return "19 - 23";
        } else if ((idade >= 24) && (idade <=28)) {
            return "24 - 28";
        } else if ((idade >= 29) && (idade <=33)) {
            return "29 - 33";
        } else if ((idade >= 34) && (idade <=38)) {
            return "34 - 38";
        } else if ((idade >= 39) && (idade <=43)) {
            return "39 - 43";
        } else if ((idade >= 44) && (idade <=48)) {
            return "44 - 48";
        } else if ((idade >= 49) && (idade <=53)) {
            return "49 - 53";
        } else if ((idade >= 54) && (idade <=58)) {
            return "54 - 58";
        } else if ((idade >= 59) && (idade <=999)) {
            return "59 - 999";
        }
        return ApplicationResourcesHelper.getMessage("mensagem.info.datehelper.idade.nao.disponivel", (AcessoSistema) null);
    }

    /**
     * Retorna o primeiro e o ultimo dia do mês e ano chamado (MM/yyyy)
     *
     * @param mesAno
     * @return Retorna uma lista com duas datas sendo o primeiro e o último dia do mês, respectivamente
     */
    public static List<Date> getPrimeiroUltimoDiaDoMes(String mesAno) {
        Date primeiroDiaMes = null;
        Date ultimoDiaMes = null;

        if (!TextHelper.isNull(mesAno)) {
            try {
                final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                primeiroDiaMes = sdf.parse(DateHelper.reformat(mesAno, "MM/yyyy", "yyyy-MM-dd 00:00:00"));

                final String mesAnoStr = DateHelper.reformat(mesAno, "MM/yyyy", "yyyy-MM-dd 23:59:59");

                final Calendar calendar = Calendar.getInstance();
                calendar.setTime(sdf.parse(mesAnoStr));
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

                ultimoDiaMes = calendar.getTime();

            } catch (final ParseException e) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.periodo.parse.invalido", (AcessoSistema) null), e);
            }
        }

        final List<Date> retorno = new ArrayList<>();
        retorno.add(primeiroDiaMes);
        retorno.add(ultimoDiaMes);

        return retorno;
    }


    public static String segundosParaHoraMinutoSegundo(int segundos) {

        final int horas = segundos / 3600;
        segundos = (segundos - (horas * 3600));
        final int minutos =  segundos / 60;
        segundos =  segundos - (minutos * 60);

        final StringBuilder builder = new StringBuilder();

        if (horas != 0) {
            builder.append(horas).append(" ").append(ApplicationResourcesHelper.getMessage("rotulo.horas", AcessoSistema.getAcessoUsuarioSistema()));
        }
        if (minutos != 0) {
            if (!builder.toString().isEmpty()) {
                builder.append(", ");
            }
            builder.append(minutos).append(" ").append(ApplicationResourcesHelper.getMessage("rotulo.minutos", AcessoSistema.getAcessoUsuarioSistema()));
        }
        if (segundos != 0) {
            if (!builder.toString().isEmpty()) {
                builder.append(" ").append(ApplicationResourcesHelper.getMessage("rotulo.e", AcessoSistema.getAcessoUsuarioSistema())).append(" ");
            }
            builder.append(segundos).append(" ").append(ApplicationResourcesHelper.getMessage("rotulo.segundos", AcessoSistema.getAcessoUsuarioSistema()));
        }

        return builder.toString();

    }

    public static Date addMinutes(Date date, int minutes) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }

    public static java.util.Date getLastDayOfMonth(Date date) {
        // Retorna o último dia do Mẽs de acordo com a data passada.
        final Calendar cal = Calendar.getInstance();
        if (date != null) {
            cal.setTime(date);
        } else {
        	cal.setTime(getSystemDate());
        }
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);

        return cal.getTime();
    }
}
