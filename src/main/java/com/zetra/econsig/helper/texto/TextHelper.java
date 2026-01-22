package com.zetra.econsig.helper.texto;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.math.NumberUtils;
import org.json.simple.JSONArray;
import org.jsoup.Jsoup;
import org.owasp.encoder.Encode;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: TextHelper</p>
 * <p>Description: Auxilio na formatação de texto.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TextHelper {

    public static String[] split(String texto, String regex) {
        boolean endsWith = false;
        if (texto == null) {
            texto = "";
        } else if (texto.endsWith(regex)) {
            endsWith = true;
            if (" ".equals(regex)) {
                texto += "x";
            } else {
                texto += " ";
            }
        }
        if (".".equals(regex)) {
            regex = "\\" + regex;
        }
        final String[] resultado = texto.split(regex);
        if (endsWith && (resultado.length > 0)) {
            resultado[resultado.length - 1] = "";
        }
        return resultado;
    }

    /**
     * Cria uma string para ser usada dentro de um in (...) do SQL
     * Em outras palavras: coloca os ítens dentro de '' e separa por vírgula
     * @param array
     * @return
     */
    public static String sqlJoin(List<String> list) {
        return sqlJoin(list.toArray(new String[]{""}));
    }

    /**
     * Cria uma string para ser usada dentro de um in (...) do SQL
     * Em outras palavras: coloca os ítens dentro de '' e separa por vírgula
     * @param array
     * @return
     */
    public static String sqlJoin(String[] array) {
        final StringBuilder result = new StringBuilder();
        final int len = array.length;
        for (int i = 0; i < len; i++) {
            if (array[i] != null) {
                result.append("'").append(array[i].toString()).append("'");
            }
            if ((i + 1) < len) {
                result.append(",");
            }
        }
        return result.toString();
    }

    public static String join(Object[] array, String separator) {
        return join(Arrays.asList(array), separator);
    }

    public static String join(Collection<?> list, String separator) {
        final Iterator<?> it = list.iterator();
        final StringBuilder result = new StringBuilder();
        while (it.hasNext()) {
            result.append(it.next());
            if (it.hasNext()) {
                result.append(separator);
            }
        }
        return result.toString();
    }

    public static String joinWithEscapeSql(List<String> list, String separator) {
        final Iterator<String> it = list.iterator();
        final StringBuilder result = new StringBuilder();
        while (it.hasNext()) {
            result.append(escapeSql(it.next()));
            if (it.hasNext()) {
                result.append(separator);
            }
        }
        return result.toString();
    }

    public static String format(String input, String pattern) {
        final StringBuilder text = new StringBuilder(dropSeparator(input));
        for (int i = 0; i < pattern.length(); i++) {
            switch (pattern.charAt(i)) {
                case '.':
                    text.insert(i, '.');
                    break;
                case '-':
                    text.insert(i, '-');
                    break;
                case '/':
                    text.insert(i, '/');
                    break;
                case ',':
                    text.insert(i, ',');
                    break;
                case '(':
                    text.insert(i, '(');
                    break;
                case ')':
                    text.insert(i, ')');
                    break;
                default:
                    ;
            }
        }
        return text.toString();
    }

    public static String dropSeparator(String input) {
        final StringBuilder text = new StringBuilder(input);
        for (int i = 0; i < text.length(); i++) {
            if ((text.charAt(i) == '.') || (text.charAt(i) == '-') || (text.charAt(i) == '/') || (text.charAt(i) == ',') || (text.charAt(i) == '(') || (text.charAt(i) == ')')) {
                text.deleteCharAt(i);
                i--;
            }
        }
        return text.toString();
    }

    public static String dropBlankSpace(String input) {
        return dropSeparator(input, ' ');
    }

    public static String dropSeparator(String input, char separador) {
        final StringBuilder text = new StringBuilder(input);
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == separador) {
                text.deleteCharAt(i);
                i--;
            }
        }
        return text.toString();
    }

    public static String ltrim(String texto) {
        final int tamanho = texto.length();
        int inicio = 0;

        while ((inicio < tamanho) && (texto.charAt(inicio) == ' ')) {
            inicio++;
        }
        return (inicio < tamanho) ? texto.substring(inicio, tamanho) : "";
    }

    public static String rtrim(String texto) {
        int tamanho = texto.length();

        while ((tamanho > 0) && (texto.charAt(tamanho - 1) == ' ')) {
            tamanho--;
        }
        return (tamanho > 0) ? texto.substring(0, tamanho) : "";
    }

    public static String removeAccent(String message) {
        final String temp = AsciiUtils.removeAccent(message);
        return temp.replaceAll("[^\\p{ASCII}]", "");
    }

    /**
     * Método usado para upload de arquivos para correção de nomes em encodings diversos
     * @param message = texto a ser tratado
     * @return
     * @throws IOException
     */
    public static String removeAccentCharsetArbitrario(String message) throws IOException {
        final String textoUtf = new String(message.getBytes(), "UTF-8");
        return AsciiUtils.removeAccent(textoUtf).replaceAll("[^\\p{ASCII}]", "");
    }

    public static String formataMensagem(String mensagem, String complemento, int tamanho, boolean alinhaEsquerda) {
        return formataMensagem(mensagem, complemento, tamanho, alinhaEsquerda, false);
    }

    public static String formataMensagem(String mensagem, String complemento, int tamanho, boolean alinhaEsquerda, boolean limitaTam) {
        final StringBuilder resultado = new StringBuilder(mensagem);
        if (limitaTam && (resultado.length() > tamanho)) {
            resultado.setLength(tamanho);
        }
        if (!alinhaEsquerda) {
            resultado.reverse();
        }
        while (resultado.length() < tamanho) {
            resultado.append(complemento);
        }
        if (!alinhaEsquerda) {
            resultado.reverse();
        }
        return resultado.toString();
    }

    public static int parseIntErrorSafe(Object number, int defaultValue) {
        if (!isNull(number)) {
            try {
                return Integer.parseInt(number.toString().trim());
            } catch (final NumberFormatException ex) {
            }
        }
        return defaultValue;
    }

    public static double parseDoubleErrorSafe(Object number, double defaultValue) {
        if (isDecimalNum(number)) {
            try {
                return Double.parseDouble(number.toString().trim());
            } catch (final NumberFormatException ex) {
            }
        }
        return defaultValue;
    }

    public static boolean isNum(Object text) {
        if (!isNull(text)) {
            try {
                Long.parseLong(text.toString().trim());
                return true;
            } catch (final NumberFormatException ex) {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean isDecimalNum(Object text) {
        if (!isNull(text)) {
            try {
                Double.parseDouble(text.toString().trim());
                return true;
            } catch (final NumberFormatException ex) {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean isNull(Object text) {
        if (text == null) {
            return true;
        } else if (text instanceof final String textString) {
            return textString.isBlank();
        } else if (text instanceof final Object[] textArray) {
            if (textArray.length == 0) {
                return true;
            } else {
                boolean allNull = true;
                for (final Object elementArray : textArray) {
                    allNull &= isNull(elementArray);
                }
                return allNull;
            }
        } else {
            return text.toString().isBlank();
        }
    }

    public static Object coalesce(Object input, Object defaultValue) {
        return (isNull(input)) ? defaultValue : input;
    }

    @SuppressWarnings("unchecked")
	public static List<String> objectToStringList(Object value) {
        if (value != null) {
            if (value instanceof Collection<?>) {
            	return new ArrayList<>((Collection<String>) value);
            } else if (value instanceof String[]) {
                return Arrays.asList((String[]) value);
            } else if (!value.toString().isBlank()) {
                return Arrays.asList(value.toString().trim());
            }
        }
        return null;
    }

    public static boolean cpfOk(String cpf) {
        if (LocaleHelper.BRASIL.equals(LocaleHelper.getLocale())) {
            // Verifica se pelo menos tem o tamanho correto
            // Verifica se tem somente dígitos
            if ((cpf.length() < 11) || !NumberUtils.isDigits(cpf)) {
                return false;
            }
            // Verificação dos dois digitos finais em relação ao número completo
            final int c1 = Integer.parseInt(cpf.substring(9, 10)); //10o caracter = primeiro dígito verificador
            final int c2 = Integer.parseInt(cpf.substring(10, 11)); //11o caracter = segundo dígito verificador

            final int[] dvs = calculaDvCpf(cpf);
            final int dv1 = dvs[0];
            final int dv2 = dvs[1];

            if ((c1 != dv1) || (c2 != dv2)) {
                return false;
            }
        } else if (LocaleHelper.MEXICO.equals(LocaleHelper.getLocale())) {
            if (cpf.length() < 13) {
                return false;
            }

            final Pattern p = Pattern.compile("\\D{4}\\d{6}\\w{3}");
            final Matcher m = p.matcher(cpf);
            if (m.find()) {
                final String sequencia = m.group();
                return (sequencia.equals(cpf));
            } else {
                return false;
            }
        } else if (LocaleHelper.INDIA.equals(LocaleHelper.getLocale())) {
            if (cpf.length() < 10) {
                return false;
            }

            final Pattern p = Pattern.compile("[A-Z]{5}\\d{4}[A-Z]{1}");
            final Matcher m = p.matcher(cpf);
            if (m.find()) {
                final String sequencia = m.group();
                return (sequencia.equals(cpf));
            } else {
                return false;
            }
        } else if (LocaleHelper.INGLATERRA.equals(LocaleHelper.getLocale())) {
            if (cpf.length() < 9) {
                return false;
            }

            final Pattern p = Pattern.compile("[A-Z]{2}\\d{6}[A-Z]{1}");
            final Matcher m = p.matcher(cpf);
            if (m.find()) {
                final String sequencia = m.group();
                return (sequencia.equals(cpf));
            } else {
                return false;
            }
        }

        return true;
    }

    public static int[] calculaDvCpf(String cpf) {
        // Cálculo do primeiro dígito verificador
        int soma = 0;
        int parcela = 0;
        int fator = 0;

        for (int i = 1; i <= 9; i++) {
            fator = i + 1;

            parcela = fator * Integer.parseInt(cpf.substring(9 - i, (9 - i) + 1));
            soma = soma + parcela;
        }

        int dv1 = (soma % 11);
        dv1 = 11 - dv1;

        if (dv1 > 9) {
            dv1 = 0;
        }

        // Cálculo do segundo dígito verificador
        soma = 0;
        parcela = 0;
        fator = 0;

        for (int i = 1; i <= 10; i++) {
            fator = i + 1;

            parcela = fator * (((i == 1) && (cpf.length() != 11)) ? dv1 : Integer.parseInt(cpf.substring(10 - i, (10 - i) + 1)));
            soma = soma + parcela;
        }

        int dv2 = (soma % 11);
        dv2 = 11 - dv2;

        if (dv2 > 9) {
            dv2 = 0;
        }

        return new int[] { dv1, dv2 };
    }

    /**
     * Formata o texto para poder ser usado em comparação:
     * - colocando o texto em maiúsculas;
     * - removendo todos os caracteres que não sejam alfa-numéricos;
     * - e removendo os zeros no início.
     * @param texto
     * @return Texto formatado.
     */
    public static String formataParaComparacao(String texto) {
        String result = texto;
        if (!TextHelper.isNull(texto)) {
            result = result.toUpperCase();
            result = result.replaceAll("\\W", "").replace("_", "");
            while (result.indexOf("0") == 0) {
                result = result.substring(1, result.length());
            }
        }
        return result;
    }

    /**
     * Introduz o caracter de escape (\) antes dos caracteres \,' e " para evitar Sql Injection.
     * Na verdade deveria se usar sql parametrizado (PreparedStatement), mas o autor original nao
     * fez isto e vai dar muito trabalho para mudar tudo :)
     * @author Luis Henriques
     * @param str
     * @return
     */
    public static String escapeSql(Object str) {
        if (str == null) {
            return null;
        }
        if (str instanceof String) {
            return str.toString().replaceAll("([\'\"\\\\])", "\\\\$1");
        }
        return str.toString();
    }

    /**
     * Verifica se String representa um decimal (considerando ',' e '.' para o locale
     * português e inglês, a menos que estejam no final da string)
     * @param valorEntrada
     * @return
     */
    public static boolean isNotNumeric(String valorEntrada) {
        if (valorEntrada == null) {
            return true;
        }

        final Pattern notNumber = Pattern.compile("[^0-9\\+\\.,-]|"
                                          + "(([0-9&&[\\.,]]?)(\\+$|\\.$|,$|-$))|"
                                          + "((([0-9]{4,})?)(([\\.,](([0-9]{4,})|([0-9]{1,2}))[\\.,])+)([0-9]+))|"
                                          + "(([0-9]+)[,]([0-9]+)[,]([0-9]{4,}|[0-9]{1,2}$))|"
                                          + "(([0-9]+)[.]([0-9]+)[.]([0-9]{4,}|[0-9]{1,2}$))|"
                                          + "(([0-9]{4,}+)([,]+)(([0-9]{3}+))([\\.]+)([0-9]+))|"
                                          + "(([0-9]{4,}+)([\\.]+)(([0-9]{3}+))([,]+)([0-9]+))");
        final Matcher m = notNumber.matcher(valorEntrada);
        return (m.find());
    }

    /**
     * Verifica se o endereço de e-mail é válido
     * @param email e-mail a ser validado.
     * @return True caso o email seja valido.
     */
    public static boolean isEmailValid(String email) {
        if (email == null) {
            return false;
        }
        final Pattern p = Pattern.compile("^[\\w!#$%&'*+\\/=?^`{|}~-]+(\\.[\\w!#$%&'*+\\/=?^`{|}~-]+)*@(([\\w-]+\\.)+[A-Za-z]{2,}|\\[\\d{1,3}(\\.\\d{1,3}){3}\\])$");
        final Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * Transforma valor em branco em nulo.
     * @param text
     * @return
     */
    public static String blankToNullValue(Object text) {
        return !TextHelper.isNull(text) ? text.toString() : null;
    }

    /**
     * Realiza um operador de agregação sobre linhas onde as chaves "groupKey" são idênticas
     * concatenando os valores representados pelas chaves "concatKey".
     * @param values    : Valores de entrada
     * @param groupKey  : Chave dos atributos para agrupamento
     * @param concatKey : Chave dos atributos para concatenação
     * @param separator : Separador da concatenação
     * @param sort      : True se deve ordenar os valores de entrada
     * @param distinct  : True se deve concatenar apenas valores distintos
     * @return
     */
    public static List<TransferObject> groupConcat(List<TransferObject> values, final String[] groupKey, final String[] concatKey, String separator, boolean sort, boolean distinct) {
        if ((values == null) || (values.size() == 0)) {
            return values;
        }

        if (sort) {
            // Ordena a lista de entrada
            Collections.sort(values, (t1, t2) -> {
                for (final String element : groupKey) {
                    final int compareResult = ((Comparable<Object>) t1.getAttribute(element)).compareTo(t2.getAttribute(element));
                    if (compareResult != 0) {
                        return compareResult;
                    }
                }
                return 0;
            });
        }

        // Coleção dos valores concatenados: Se devem ser distintos cria um Set ao invés de um List
        final Collection<Object>[] concatValues = (distinct ? new TreeSet[concatKey.length] : new ArrayList[concatKey.length]);
        // Lista de resultado do operador groupConcat
        final List<TransferObject> result = new ArrayList<>();
        // Variáveis temporárias para armazenarem a referencia a ultima iteração
        String oldMatchKey = null;
        TransferObject lastRow = null;

        for (final TransferObject row : values) {
            String matchKey = "";
            for (final String element : groupKey) {
                matchKey += row.getAttribute(element) + "|";
            }

            if ((oldMatchKey != null) && !oldMatchKey.equals(matchKey)) {
                // Gera uma saida
                result.add(createResult(concatKey, concatValues, separator, sort, lastRow));
            }

            if ((oldMatchKey == null) || !oldMatchKey.equals(matchKey)) {
                // Limpa os valores concatenados
                for (int i = 0; i < concatKey.length; i++) {
                    concatValues[i] = (distinct ? new TreeSet<>() : new ArrayList<>());
                }
            }

            for (int i = 0; i < concatKey.length; i++) {
                if (!isNull(row.getAttribute(concatKey[i]))) {
                    concatValues[i].add(row.getAttribute(concatKey[i]));
                }
            }

            oldMatchKey = matchKey;
            lastRow = row;
        }

        // Gera a última saida
        result.add(createResult(concatKey, concatValues, separator, sort, lastRow));

        return result;
    }

    /**
     * valida se valor passado com parâmetro atende à máscara dada. reproduz validação javascript de scripts_2810.js
     * @param valor - valor confrontado com a máscara
     * @param mascara - máscara a ser validade
     * @return
     */
    public static boolean validaMascara(String valor, String mascara) {
        int tamReal = 0;
        if (mascara.charAt(0) == '#') {
            //tamReal = mascara.substring(2, mascara.length()).length();
            tamReal = Integer.parseInt(mascara.substring(2, mascara.length()));
        } else {
            tamReal = mascara.length();
        }

        if (valor.length() >= tamReal) {
            valor = valor.substring(0, tamReal);
        }

        final int tamValor = valor.length();

        String valourAux = null;
        int pularChar = -1;
        for (int i = 0; i < tamValor; i++) {
            if (i <= pularChar) {
                continue;
            }

            final char cUp = Character.toUpperCase(valor.charAt(i));

            char maskChar = 0;
            if (mascara.charAt(0) == '#') {
                maskChar = mascara.charAt(1);
            } else {
                maskChar = mascara.charAt(i);
            }

            Pattern pattern = null;
            Matcher matcher = null;
            switch (maskChar) {
                case 'D':
                    pattern = Pattern.compile("\\d");
                    matcher = pattern.matcher(String.valueOf(cUp));
                    if (!matcher.find()) {
                        return false;
                    }
                    break;
                case 'N':
                    pattern = Pattern.compile("\\d");
                    matcher = pattern.matcher(String.valueOf(cUp));
                    if (!matcher.find() && ((cUp != '.') && (cUp != ','))) {
                        return false;
                    }

                    if ((cUp == ',') && ((valor.indexOf(",") > -1) || (i == 0))) {
                        return false;
                    }

                    if ((cUp == '.') && (i == 0)) {

                        return false;
                    }

                    if (mascara.charAt(0) != '#') {
                        for (int j = i; j < tamValor; j++) {
                            if ((mascara.charAt(j) != 'N') || (j == (tamValor - 1))) {
                                if (j == (tamValor - 1)) {
                                    valourAux = valor.substring(i);
                                } else {
                                    valourAux = valor.substring(i, j);
                                }
                                pularChar = j - 1;
                                break;
                            }
                        }
                    } else {
                        valourAux = valor;
                    }

                    if (isNotNumeric(valourAux)) {
                        return false;
                    }

                    break;
                case 'F':
                    pattern = Pattern.compile("\\d");
                    matcher = pattern.matcher(String.valueOf(cUp));
                    if (!matcher.find() && (cUp != ',') && (cUp != '-') && (cUp != '.')) {

                        return false;
                    }
                    if (((i == 0) ? (cUp == ',') : (cUp == '-'))) {

                        return false;
                    }

                    if (mascara.charAt(0) != '#') {
                        for (int j = i; j < tamValor; j++) {
                            if ((mascara.charAt(j) != 'F') || (j == (tamValor - 1))) {
                                if (j == (tamValor - 1)) {
                                    valourAux = valor.substring(i);
                                } else {
                                    valourAux = valor.substring(i, j);
                                }
                                pularChar = j - 1;
                                break;
                            }
                        }
                    } else {
                        valourAux = valor;
                    }

                    if (isNotNumeric(valourAux)) {
                        return false;
                    }

                    break;
                case 'M': // MENSAGEM - FAQ
                    if ((cUp == '\'') || (cUp == '\"') || (cUp == '<') || (cUp == '>')) {

                        return false;
                    }
                    break;
                case 'C':
                    pattern = Pattern.compile("[A-Z]");
                    matcher = pattern.matcher(String.valueOf(cUp));
                    if (!matcher.find()) {
                        return false;
                    }
                    break;
                case 'A':
                    pattern = Pattern.compile("[A-Z0-9]");
                    matcher = pattern.matcher(String.valueOf(cUp));
                    if (!matcher.find()) {
                        return false;
                    }
                    break;
                case 'L': // Login
                    pattern = Pattern.compile("[A-Z0-9]");
                    matcher = pattern.matcher(String.valueOf(cUp));
                    if (!matcher.find() && (cUp != '/') && (cUp != '_') && (cUp != '-') && (cUp != '.')) {
                        return false;
                    }
                    break;
                case 'T':
                    pattern = Pattern.compile("\\d");
                    matcher = pattern.matcher(String.valueOf(cUp));
                    if ((!matcher.find()) && (cUp != '-') && (cUp != ',') && (cUp != ' ')) {

                        return false;
                    }
                    if (((i != 0) ? (cUp == '-') : (cUp == ' '))) {

                        return false;
                    }
                    break;
                case '-':
                    if (cUp != '-') {

                        return false;
                    }
                    break;
                case '.':
                    if (cUp != '.') {

                        return false;
                    }
                    break;
                case ',':
                    if (cUp != ',') {

                        return false;
                    }
                    break;
                case '/':
                    if (cUp != '/') {

                        return false;
                    }
                    break;
                default:
                    break;
            }
        }
        return true;

    }

    public static String aplicarMascara(String valor, String mascara) throws ZetraException {
        if (!validaMascara(valor, mascara)) {
            throw new ZetraException("mensagem.erro.valor.invalido.mascara.aplicada", (AcessoSistema) null);
        }

        int tamReal = 0;
        String retorno = null;
        if (mascara.charAt(0) == '#') {
            tamReal = Integer.parseInt(mascara.substring(2, mascara.length()));
        } else {
            tamReal = mascara.length();
        }

        if (valor.length() >= tamReal) {
            retorno = valor.substring(0, tamReal);
        } else {
            retorno = valor;
        }

        return retorno;
    }

    private static TransferObject createResult(String[] concatKey, Collection<Object>[] concatValues, String separator, boolean sort, TransferObject lastRow) {
        final TransferObject newRow = new CustomTransferObject(lastRow);
        for (int i = 0; i < concatKey.length; i++) {
            if (!concatValues[i].isEmpty()) {
                final Object[] values = concatValues[i].toArray();
                if (sort) {
                    Arrays.sort(values);
                }
                newRow.setAttribute(concatKey[i], join(values, separator));
            }
        }
        return newRow;
    }

    public static String StringArrayToJSONArray(String[] arrayString) {
        if (arrayString == null) {
            return "[]";
        }

        final StringWriter out = new StringWriter();
        final JSONArray list = new JSONArray();

        Collections.addAll(list, arrayString);

        try {
            list.writeJSONString(out);
        } catch (final IOException e) {
            return "[]";
        }
        return out.toString();
    }

    // Métodos para codificar e decodificar strings no padrão base64
    public static String encode64(String string) {
        return Base64.getEncoder().encodeToString(string.getBytes());
    }

    public static String decode64(String string) {
        return new String(Base64.getDecoder().decode(string));
    }

    // codifica para base 64 um stream de bytes
    public static String encode64Binary(byte[] binaryData) {
        return binaryData != null ? Base64.getEncoder().encodeToString(binaryData) : null;
    }

    public static String md5(String input) {
        try {
            final java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            final byte[] array = md.digest(input.getBytes("UTF-8"));
            final StringBuilder sb = new StringBuilder();
            for (final byte element : array) {
                sb.append(String.format("%02x", element));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            return null;
        }
    }

    /**
     * Retorna true se objeto for de algum dos tipos String, StringBuilder ou StringBuilder, caso contrário retorna o objeto original.
     * @param object
     * @return
     */
    private static boolean isLikeString(Object object) {
        if ((object != null) && ((object.getClass() == String.class) || (object.getClass() == StringBuilder.class) || (object.getClass() == StringBuffer.class))) {
            return true;
        }
        return false;
    }

    /**
     * Retorna String codificada se objeto for de algum dos tipos String, StringBuilder ou StringBuilder, caso contrário retorna toString do objeto original.
     * @param object
     * @return object original ou String
     */
    public static String forHtml(Object object) {
        if (isLikeString(object)) {
            return Encode.forHtml(java.util.Objects.toString(object));
        }
        return java.util.Objects.toString(object, null);
    }

    /**
     * Retorna String codificada se objeto for de algum dos tipos String, StringBuilder ou StringBuilder, caso contrário retorna toString do objeto original.
     * @param object
     * @return object original ou String
     */
    public static String forHtmlAttribute(Object object) {
        if (isLikeString(object)) {
            return Encode.forHtmlAttribute(java.util.Objects.toString(object));
        }
        return java.util.Objects.toString(object, null);
    }

    /**
     * Retorna String codificada se objeto for de algum dos tipos String, StringBuilder ou StringBuilder, caso contrário retorna toString do objeto original.
     * @param object
     * @return object original ou String
     */
    public static String forJavaScriptSource(Object object) {
        if (isLikeString(object)) {
            return Encode.forJavaScriptSource(java.util.Objects.toString(object));
        }
        return java.util.Objects.toString(object, null);
    }

    /**
     * Retorna String codificada se objeto for de algum dos tipos String, StringBuilder ou StringBuilder, caso contrário retorna toString do objeto original.
     * @param object
     * @return object original ou String
     */
    public static String forHtmlContent(Object object) {
        if (isLikeString(object)) {
            return Encode.forHtmlContent(java.util.Objects.toString(object));
        }
        return java.util.Objects.toString(object, null);
    }

    /**
     * Retorna String codificada se objeto for de algum dos tipos String, StringBuilder ou StringBuilder, caso contrário retorna toString do objeto original.
     * @param object
     * @return object original ou String
     */
    public static String forJavaScript(Object object) {
        if (isLikeString(object)) {
            return Encode.forJavaScript(java.util.Objects.toString(object));
        }
        return java.util.Objects.toString(object, null);
    }

    /**
     * Retorna String codificada se objeto for de algum dos tipos String, StringBuilder ou StringBuilder, caso contrário retorna toString do objeto original.
     * @param object
     * @return object original ou String
     */
    public static String forJavaScriptAttribute(Object object) {
        if (isLikeString(object)) {
            return Encode.forJavaScriptAttribute(java.util.Objects.toString(object));
        }
        return java.util.Objects.toString(object, null);
    }

    /**
     * Retorna String codificada se objeto for de algum dos tipos String, StringBuilder ou StringBuilder, caso contrário retorna toString do objeto original.
     * @param object
     * @return object original ou String
     */
    public static String forUriComponent(Object object) {
        if (isLikeString(object)) {
            return Encode.forUriComponent(java.util.Objects.toString(object));
        }
        return java.util.Objects.toString(object, null);
    }

    /**
     * Retorna String codificada se objeto for de algum dos tipos String, StringBuilder ou StringBuilder, caso contrário retorna toString do objeto original.
     * @param object
     * @return object original ou String
     */
    public static String forJavaScriptBlock(Object object) {
        if (isLikeString(object)) {
            return Encode.forJavaScriptBlock(java.util.Objects.toString(object));
        }
        return java.util.Objects.toString(object, null);
    }

    /**
     * Segurança XSS: Para string contendo <BR> e <B></B>, executa o forHtmlContent mantendo estas tags
     * @param input
     * @return String
     */
    public static String forHtmlContentComTags(String input) {
        // Estas tags não podem estar presentes na entrada ou seria tentativa de ataque
        // Retorna as tags aceitas
        input = input.replace("#econsig:ordm#", "");
        input = input.replace("#econsig:ordf#", "");
        input = input.replace("#econsig:nbsp#", "");
        input = input.replace("#econsig:br#", "");
        input = input.replace("#econsig:b#", "");
        input = input.replace("#econsig:/b#", "");
        input = input.replace("#econsig:sup#", "");
        input = input.replace("#econsig:/sup#", "");
        input = input.replace("#econsig:scn#", "");
        input = input.replace("#econsig:/sc#", "");
        input = input.replace("#econsig:fcns#", "");
        input = input.replace("#econsig:fcca#", "");
        input = input.replace("#econsig:/font#", "");
        input = input.replace("#econsig:i#", "");
        input = input.replace("#econsig:/i#", "");
        input = input.replace("#econsig:strong#", "");
        input = input.replace("#econsig:/strong#", "");
        input = input.replace("#econsig:scns#","");

        // Substitui tags aceitas
        input = input.replaceAll("(?i)&ordm;", "#econsig:ordm#");
        input = input.replaceAll("(?i)&ordf;", "#econsig:ordf#");
        input = input.replaceAll("(?i)&nbsp;", "#econsig:nbsp#");
        input = input.replaceAll("(?i)<br/{0,1}>", "#econsig:br#");
        input = input.replaceAll("(?i)<b>", "#econsig:b#");
        input = input.replaceAll("(?i)</b>", "#econsig:/b#");
        input = input.replaceAll("(?i)<sup>", "#econsig:sup#");
        input = input.replaceAll("(?i)</sup>", "#econsig:/sup#");
        input = input.replaceAll("(?i)<span class=\"normal\">", "#econsig:scn#");
        input = input.replaceAll("(?i)</span>", "#econsig:/sc#");
        input = input.replaceAll("(?i)<font class=\"novaSenha\">", "#econsig:fcns#");
        input = input.replaceAll("(?i)<font class=\"codigoAutorizacao\">", "#econsig:fcca#");
        input = input.replaceAll("(?i)</font>", "#econsig:/font#");
        input = input.replaceAll("(?i)<i>", "#econsig:i#");
        input = input.replaceAll("(?i)</i>", "#econsig:/i#");
        input = input.replaceAll("(?i)<strong>", "#econsig:strong#");
        input = input.replaceAll("(?i)</strong>", "#econsig:/strong#");
        input = input.replaceAll("(?i)<font class=\"erro\">", "#econsig:fcrr#");
        input = input.replaceAll("(?i)<span style=\"font-size:0.9rem\">", "#econsig:scns#");

        // Aplica o escape das tags não aceitas
        input = TextHelper.forHtmlContent(input);

        // Retorna as tags aceitas
        input = input.replace("#econsig:ordm#", "&ordm;");
        input = input.replace("#econsig:ordf#", "&ordf;");
        input = input.replace("#econsig:nbsp#", "&nbsp;");
        input = input.replace("#econsig:br#", "<br>");
        input = input.replace("#econsig:b#", "<b>");
        input = input.replace("#econsig:/b#", "</b>");
        input = input.replace("#econsig:sup#", "<sup>");
        input = input.replace("#econsig:/sup#", "</sup>");
        input = input.replace("#econsig:scn#", "<span class=\"normal\">");
        input = input.replace("#econsig:/sc#", "</span>");
        input = input.replace("#econsig:fcns#", "<font class=\"novaSenha\">");
        input = input.replace("#econsig:fcca#", "<font class=\"codigoAutorizacao\">");
        input = input.replace("#econsig:/font#", "</font>");
        input = input.replace("#econsig:i#", "<i>");
        input = input.replace("#econsig:/i#", "</i>");
        input = input.replace("#econsig:strong#", "<strong>");
        input = input.replace("#econsig:/strong#", "</strong>");
        input = input.replace("#econsig:fcrr#", "<font class=\"erro\">");
        input = input.replace("#econsig:scns#", "<span style=\"font-size:0.9rem\">");

        return TextHelper.forHtmlTagsEspecificas(input);
    }

    public static String removeTagsHtml(String input) {
        return Jsoup.parse(input).text();
    }

    public static String forHtmlContentComTags(Object object) {
        if (isLikeString(object)) {
            return forHtmlContentComTags(java.util.Objects.toString(object));
        }
        return java.util.Objects.toString(object, null);
    }

    public static String formataNomeUsuario(String nome, AcessoSistema responsavel) {

        String retorno = nome;

        /*
         * 0 - Exibir nome completo (default)
         * 1 - Exibir apenas o primeiro e segundo nome
         * 2 - Exibir apenas o primeiro e último nome
         * 3 - Exibir apenas as iniciais, seguida de ponto, de cada parte do nome
         * 4 - Exibir apenas o primeiro nome e as iniciais das demais partes
         */

        if (!ParamSist.paramEquals(CodedValues.TPC_MASCARA_NOME_LOGIN, "0", responsavel)) {

            final String[] partesNome = nome.replaceAll("\\p{Punct}", "").replaceAll("\\p{Blank}{2,}", " ").split(" ");

            if (ParamSist.paramEquals(CodedValues.TPC_MASCARA_NOME_LOGIN, "1", responsavel)) {
                if (partesNome.length > 1) {
                    retorno = partesNome[0] + " " + partesNome[1];
                }
            } else if (ParamSist.paramEquals(CodedValues.TPC_MASCARA_NOME_LOGIN, "2", responsavel)) {
                if (partesNome.length > 1) {
                    retorno = partesNome[0] + " " + partesNome[partesNome.length - 1];
                }
            } else if (ParamSist.paramEquals(CodedValues.TPC_MASCARA_NOME_LOGIN, "3", responsavel)) {
                retorno = "";
                for (final String element : partesNome) {
                    retorno += element.substring(0, 1) + ". ";
                }
            } else if (ParamSist.paramEquals(CodedValues.TPC_MASCARA_NOME_LOGIN, "4", responsavel) && (partesNome.length > 1)) {
                retorno = partesNome[0] + " ";
                for (int i = 1; i < partesNome.length; i++) {
                    retorno += partesNome[i].substring(0, 1) + ". ";
                }
            }
        }

        return retorno;
    }

    /**
     * Retorna iniciais concatenadas de um nome passado por parâmetro.
     * @param nome
     * @param qtdeIniciais Quantide de iniciais que serão retornadas (0: retorna todas as iniciais).
     * @return
     */
    public static String retornaIniciaisNome(String nome, Integer qtdeIniciais) {
        String retorno = "";

        if (!TextHelper.isNull(nome)) {
            final String[] partesNome = nome.replaceAll("\\p{Punct}", "").replaceAll("\\p{Blank}{2,}", " ").split(" ");
            for (final String element : partesNome) {
                retorno += element.substring(0, 1);
                if ((qtdeIniciais > 0) && (retorno.length() >= qtdeIniciais)) {
                    break;
                }
            }
        }

        return retorno.toUpperCase();
    }

    public static String replaceString(String value, String oldValue, String newValue) {
        return value.replace(oldValue, newValue);
    }

    /**
     * Retorna uma String formatada com somente a primeira letra da palavra em UpperCase
     * @param entrada
     * @return
     */
    public static String capitailizeFirstLetter(String entrada) {
        final StringBuilder saida = new StringBuilder();
        entrada = entrada.toLowerCase();

        char ch = ' ';
        for (int i = 0; i < entrada.length(); i++) {
            if ((ch == ' ') && (entrada.charAt(i) != ' ')) {
                saida.append(Character.toUpperCase(entrada.charAt(i)));
            } else {
                saida.append(entrada.charAt(i));
            }
            ch = entrada.charAt(i);
        }

        return saida.toString().trim();
    }

    /**
     * Método que esconde os 6 digitos finais de um cpf já formatado
     * @param cpf
     * @return
     */
    public static String escondeCpf(String cpf) {
        if (LocaleHelper.BRASIL.equals(LocaleHelper.getLocale()) && (cpf.length() == 14)) {
            String aux = cpf.substring(0, 6);
            cpf = cpf.replaceAll("[0-9]", "\\*");
            aux += cpf.substring(6, cpf.length());
            return aux;
        } else {
            return cpf;
        }
    }

    /**
     * Método que esconde os caracteres de um email
     * @param email
     * @return
     */
    public static String escondeEmail(String email) {
        if (isNull(email)) {
            return "";
        }
        final List<String> parts = Arrays.asList(email.split("@"));
        final String user = parts.get(0).substring(0, Math.min(4, parts.get(0).length()));
        final String domain = parts.get(1).substring(Math.max(parts.get(1).length() - 4, 0));
        return user + "******@******" + domain;
    }

    /**
     * Método que esconde os digitos iniciais de um telefone, retornando somente os 4 últimos dígitos
     * @param email
     * @return
     */
    public static String escondeTelefone(String telefone) {
        if (isNull(telefone)) {
            return "";
        }
        return "******" + telefone.substring(telefone.length() - Math.min(4, telefone.length()));
    }

    /**
     * Método que substitui tags específicas por links fixos
     */
    public static String forHtmlTagsEspecificas(String input) {
        if(input.contains("#linkPesquisarConsignacao:")) {
            input = input.replace("#linkPesquisarConsignacao:", "<a class=\"textLinkInfo\"href=\"#no-back\" onclick=\"postData('../v3/consultarConsignacao?acao=pesquisarConsignacao&RSE_CODIGO=");
            input = input.replace("#linkPesquisarConsignacao1", "&ADE_NUMERO_LIST=");
            input = input.replace("#linkPesquisarConsignacao2", "&skip_consultar_consignacao=true')\">");
            input = input.replace("#linkPesquisarConsignacao3", "</a>");
        }
        return input;
    }

    public static Boolean forValidationArquivo(String input) {

        return !input.toLowerCase().contains("validacao_");
    }

    public static String formataTextoLongoListagem(String entrada, String complemento, int tamanho) {
        if(!TextHelper.isNull(entrada) && !TextHelper.isNull(complemento) && !TextHelper.isNull(tamanho)) {
            tamanho = entrada.indexOf(" ") > tamanho ? entrada.indexOf(" ") : tamanho;
            if(tamanho < entrada.length()){
                entrada = entrada.substring(0,entrada.lastIndexOf(" ", tamanho)) + complemento;
            }
        }
        return entrada;
    }

    public static String formatarTelefone(String telefone) {
        if (isNull(telefone)) {
            return telefone;
        }

        telefone = telefone.replaceAll("\\D", "");
        if (telefone.length() == 8) {
            return telefone.substring(0, 4) + "-" + telefone.substring(4);
        } else if (telefone.length() == 10) {
            return "(" + telefone.substring(0, 2) + ") " + telefone.substring(2, 6) + "-" + telefone.substring(6);
        } else if (telefone.length() == 11) {
            return "(" + telefone.substring(0, 2) + ") " + telefone.substring(2, 7) + "-" + telefone.substring(7);
        } else if (telefone.length() == 12) {
            return "(" + telefone.substring(0, 3) + ") " + telefone.substring(3, 7) + "-" + telefone.substring(7);
        }

        return telefone;
    }

    public static String generateRandomString(int max) {
        final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

        Random random = new Random();
        return IntStream.range(0, max)
                .mapToObj(i -> String.valueOf(CHARACTERS.charAt(random.nextInt(CHARACTERS.length()))))
                .collect(Collectors.joining());
    }
}
