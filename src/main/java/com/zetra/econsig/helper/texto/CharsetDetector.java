package com.zetra.econsig.helper.texto;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.tika.parser.txt.CharsetMatch;

import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: CharsetDetector</p>
 * <p>Description: Permite detectar a codificação de um arquivo ou string.</p>
 * <p>A codificação é o conjunto de caracteres usados. Ex.: ASCII, ISO-8859-1, UTF8</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class CharsetDetector {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CharsetDetector.class);

    /**
     * Detecta a codificação de um arquivo.
     * @param fileName arquivo
     * @return String com o nome do content-type (ex.: utf8)
     * @throws IOException
     */
    public static String detect(String fileName) throws IOException {
        if (fileName.toLowerCase().endsWith(".zip") || fileName.toLowerCase().endsWith(".jrxml")
                || fileName.toLowerCase().endsWith(".xml")) {
            return null;
        }

        org.apache.tika.parser.txt.CharsetDetector tika = new org.apache.tika.parser.txt.CharsetDetector(limiteCharset());
        tika.setText(new BufferedInputStream(new FileInputStream(new File(fileName))));
        CharsetMatch charsetTika = tika.detect();

        String encoding = null;
        if (charsetTika != null) {
            encoding = charsetTika.getName();
        }

        if (encoding != null) {
            LOG.debug("Detected encoding = '" + encoding + "' for file '" + fileName + "'");
        } else {
            LOG.warn("No encoding detected for file '" + fileName + "'");
        }

        if (encoding != null && encoding.contains("IBM")) {
            encoding = "UTF-8";
        }

        return encoding;
    }

    /**
     * Detecta a codificação de uma string.
     * @param string
     * @return
     * @throws IOException
     */
    public static String detectString(String string) throws IOException {
        if (string == null) {
            return null;
        }

        org.apache.tika.parser.txt.CharsetDetector tika = new org.apache.tika.parser.txt.CharsetDetector(limiteCharset());
        tika.setText(string.getBytes());
        CharsetMatch charsetTika = tika.detect();

        String encoding = null;
        if (charsetTika != null) {
            encoding = charsetTika.getName();
        }

        if (encoding == null) {
            LOG.warn("No encoding detected for string '" + string + "'");
        }

        if (encoding != null && encoding.contains("IBM")) {
            encoding = "UTF-8";
        }

        return encoding;
    }

    /**
     * Retorna valor de esforço para a CharsetDetector
     */
    private static int limiteCharset() {

        String paramEsforcoCharset = (String) ParamSist.getInstance().getParam(CodedValues.TPC_DEFINE_ESFORCO_CONVERSAO_CHARSET, AcessoSistema.getAcessoUsuarioSistema());
        int valorLimite = 16536;

        if (!TextHelper.isNull(paramEsforcoCharset)) {
            valorLimite = Integer.parseInt(paramEsforcoCharset) < 16536 ? 16536 : Integer.parseInt(paramEsforcoCharset);
        }
        return valorLimite;
    }
}