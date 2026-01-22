package com.zetra.econsig.report.format;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.TimeZone;

import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;

import net.sf.jasperreports.engine.util.FormatFactory;

/**
 * <p> Title: DefaultFormatFactory</p>
 * <p> Description: Fábrica dos formatadores dos dados dos relatório.</p>
 * <p> Copyright: Copyright (c) 2006 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class DefaultFormatFactory implements FormatFactory {

    @Override
    public DateFormat createDateFormat(String arg0, Locale arg1, TimeZone arg2) {
        // TODO fazer os outros patterns de formato de data
        if (TextHelper.isNull(arg0) || arg0.equals(LocaleHelper.getDateTimePattern())) {
            return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, LocaleHelper.getLocaleObject());
        } else if (arg0.equals("HH:mm:ss")) {
            return DateFormat.getTimeInstance(DateFormat.MEDIUM, LocaleHelper.getLocaleObject());
        } else {
            return DateFormat.getDateInstance(DateFormat.MEDIUM, LocaleHelper.getLocaleObject());
        }
    }

    @Override
    public NumberFormat createNumberFormat(String arg0, Locale arg1) {
        // TODO fazer os outros patterns de formato de número (percentual, cientifico, etc)
        // Em todos os relatórios os patterns estão escritos no formato en-US, por isto os separadores estão fixos
        String decimalSep = ".";
        String groupSep = ",";
        if (TextHelper.isNull(arg0) || arg0.indexOf(decimalSep) != -1) {
            NumberFormat nf = NumberFormat.getNumberInstance(LocaleHelper.getLocaleObject());
            if (!TextHelper.isNull(arg0)) {
                String[] partes = TextHelper.split(arg0, decimalSep);
                int pos = 0;
                if (partes.length > 1) {
                    nf.setMinimumIntegerDigits(partes[0].replaceAll("#", "").replaceAll(groupSep, "").length());
                    pos++;
                }
                nf.setMinimumFractionDigits(partes[pos].length());
                nf.setMaximumFractionDigits(partes[pos].length());
                nf.setGroupingUsed(arg0.indexOf(groupSep) != -1);
            }
            return nf;
        } else {
            NumberFormat nf = NumberFormat.getIntegerInstance(LocaleHelper.getLocaleObject());
            if (!TextHelper.isNull(arg0)) {
                nf.setGroupingUsed(arg0.indexOf(groupSep) != -1);
            } else {
                nf.setGroupingUsed(true);
            }
            return nf;
        }
    }

}
