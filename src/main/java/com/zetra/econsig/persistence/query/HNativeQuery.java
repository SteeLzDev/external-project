package com.zetra.econsig.persistence.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Session;
import org.hibernate.dialect.Dialect;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.type.BasicTypeReference;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.Pair;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.dialect.CustomDialect;
import com.zetra.econsig.persistence.dialect.CustomSqlFunction;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: HNativeQuery</p>
 * <p>Description: Classe base para queries que não podem ser adaptadas para HQL.
 *                 ATENÇÃO! Nestas queries não podem ser usadas funções proprietárias
 *                 dos bancos de dados, apenas as definidas pelo ANSI ou registrados em
 *                 hibernate dialects. Estas devem ser escritas em letra minúscula devido a
 *                 retrição do Hibernate. Não utilizar a função cast para recuperar substring
 *                 ex.: cast(valor as char(1)) devido a um bug no dialect do MySQL</p>
 * <p>Copyright: Copyright (c) 2008-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Leonel Martins
 */
public abstract class HNativeQuery extends HQuery {

    public abstract void setCriterios(TransferObject criterio);

    @SuppressWarnings("rawtypes")
    private final Map<String, BasicTypeReference> fieldsType = new HashMap<>();

    /**
     * Adiciona o campo ao mapa de conversão de tipos de campos.
     * Para que este método seja utilizado o getFields deve retornar
     * o mesmo nome dos campos na query.
     * @param fieldName
     * @param fieldType
     */
    @SuppressWarnings("rawtypes")
    protected final void addFieldType(String fieldName, BasicTypeReference fieldType) {
        fieldsType.put(fieldName, fieldType);
    }

    @SuppressWarnings({ "unchecked", "deprecation" })
    @Override
    protected Query<Object[]> instanciarQuery(Session session, String corpo) {
        final Query<Object[]> query = session.createNativeQuery(preProcessQuery(session, corpo));
        if (maxResults != null) {
            query.setMaxResults(maxResults);
        }
        if (firstResult != null) {
            query.setFirstResult(firstResult);
        }
        setQueryString(query);
        return query;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> executarDTO(Session session, Class<T> dtoClass) throws HQueryException {
        final Query<?> query = preparar(session);
        final boolean customTO = ((dtoClass == null) || dtoClass.equals(TransferObject.class) || dtoClass.equals(CustomTransferObject.class));
        final String[] fields = getFields();
        if (fields == null) {
            throw new HQueryException("mensagem.erro.query.metodo.get.fields.nao.implementado", (AcessoSistema) null);
        }
        setReturnTypes((NativeQuery<?>) query);

        final List<T> result = new ArrayList<>();

        try {
            final List<?> resultList = query.list();
            TransferObject row = null;
            Object[] values = null;
            int i = 0;
            for (final Object next : resultList) {
                if (next instanceof final Object[] nextArray) {
                    values = nextArray;
                } else {
                    values = new Object[] { next };
                }
                if (customTO) {
                    row = new CustomTransferObject();
                } else {
                    row = (TransferObject) dtoClass.getDeclaredConstructor().newInstance();
                }
                i = 0;
                for (final String field : fields) {
                    values[i] = (values[i] instanceof String) ? values[i].toString().trim() : values[i];
                    row.setAttribute(field, values[i]);
                    i++;
                }
                result.add((T) row);
            }
        } catch (final Exception ex) {
            throw new HQueryException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }

        return result;
    }

    private void setReturnTypes(NativeQuery<?> query) {
        // Se há declaração de tipos na query, utiliza o método "addScalar" para
        // adicionar os campos a serem retornados com os tipos requeridos. Os
        // campos que não tem tipo declarado também devem ser adicionados.
        if (fieldsType.size() > 0) {
            for (final String field : getFields()) {
                if (fieldsType.containsKey(field)) {
                    query.addScalar(Columns.getColumnName(field), fieldsType.get(field));
                } else {
                    query.addScalar(Columns.getColumnName(field));
                }
            }
        }
    }

    /**
     * Pré-Processa querys nativas:
     * 1) Aplicação dos operadores customizados do Hibernate.
     * 2) Limitação de 30 caracteres de objetos no Oracle.
     * @param session
     * @param corpo
     * @return
     */
    private String preProcessQuery(Session session, String corpo) {
        final Dialect dialect = ((SessionFactoryImpl) session.getSessionFactory()).getJdbcServices().getDialect();

        if (dialect instanceof final CustomDialect customDialect) {
            for (final CustomSqlFunction function : customDialect.getCustomFunctions()) {
                corpo = renderNativeFunction(function, corpo);
            }
        }

        return DBHelper.applyTableNameRestriction(corpo);
    }

    /**
     * Substitui na query o operador pela função nativa do banco de dados
     * do dialeto atual.
     * @param function
     * @param corpo
     * @return
     */
    @SuppressWarnings("java:S3776")
    private String renderNativeFunction(CustomSqlFunction function, String corpo) {
        final String functionName = function.getName();
        final String regex = "[\\s,\\(]" + functionName + "[\\s\\(]";
        final Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(corpo);
        int initPosFunction = 0;

        while (matcher.find()) {
            initPosFunction = matcher.start() + 1;
            final int abreParentesis = corpo.indexOf("(", initPosFunction);

            final Pair<List<String>, Integer> arguments = extractParametersValues(abreParentesis, corpo);
            final List<String> parameters = arguments.first;

            // método render espera que as funções cast e extract tenham dois parâmetros:
            if (functionName.equals("cast") || functionName.equals("extract")) {
                String param = parameters.get(0);
                if (functionName.equals("cast")) {
                    final int asIndex = param.lastIndexOf(" as ");
                    final String [] splited = new String [2];
                    splited[0] = param.substring(0, asIndex);
                    splited[1] = param.substring(asIndex + 4, param.length());
                    parameters.remove(0);

                    if (splited[1].equalsIgnoreCase("VARCHAR")) {
                        continue;
                    } else if (splited[1].equalsIgnoreCase("signed integer")) {
                        // "signed integer" não é um tipo reconhecido pelo hibernate
                        splited[1] = "integer";
                    }
                    Collections.addAll(parameters, splited);
                } else if (functionName.equals("extract")) {
                    final int fromIndex = param.lastIndexOf(" from ");
                    final String [] splited = new String [2];
                    splited[0] = param.substring(0, fromIndex);
                    splited[1] = param.substring(fromIndex + 6, param.length());
                    param = param.replaceAll("[\\s]+from[\\s]+", ",");

                    Collections.addAll(parameters, splited);
                }
            }

            final StringBuilder sqlAppender = new StringBuilder();
            final String template = function.getTemplate();
            final String regexTemplate = "\\?(\\d+)";
            final Pattern patternTemplate = Pattern.compile(regexTemplate);
            final Matcher matcherTemplate = patternTemplate.matcher(template);
            while (matcherTemplate.find()) {
                final int index = Integer.valueOf(matcherTemplate.group().substring(1)) - 1;
                matcherTemplate.appendReplacement(sqlAppender, parameters.get(index));
            }
            matcherTemplate.appendTail(sqlAppender);
            String nativeFunc = sqlAppender.toString();
            if (functionName.equals("cast")) {
                /* ao passar uma função como parâmetro do cast para transformar seu resultado para char, o método render
                 * substitui para um cast para char(1), o que é um erro. Optou-se então por substituir para char(255)
                 */
                nativeFunc = nativeFunc.replaceAll("[\\s]+char\\(1\\)"," char(255)");
            }

            corpo = corpo.substring(0, initPosFunction)
                  + nativeFunc
                  + corpo.substring(arguments.second.intValue() + 1, corpo.length());

            // Recompila a expressão para o novo corpo
            matcher = pattern.matcher(corpo);
            // Define a região a continuar a pesquisa
            if (nativeFunc.indexOf("(") >= 0) {
                matcher.region(initPosFunction + nativeFunc.indexOf("("), corpo.length() - 1);
            } else {
                matcher.region(initPosFunction + nativeFunc.length(), corpo.length() - 1);
            }
        }

        return corpo;
    }

    /**
     *
     * @param initFunction
     * @param corpo
     * @return
     */
    @SuppressWarnings("java:S3776")
    private Pair<List<String>, Integer> extractParametersValues(int initFunction, String corpo) {
        int countParentesis = -1;
        int initArgument = -1;
        final List<String> parameters = new ArrayList<>();
        Integer endOfFunction = -1;

        for (int i = initFunction; i < corpo.length(); i++) {
            if (corpo.charAt(i) == '(') {
                if (countParentesis < 0) {
                    countParentesis = 1;
                } else {
                    countParentesis++;
                }
            } else if (corpo.charAt(i) == ')') {
                countParentesis--;

                if ((countParentesis == 0) && (initArgument >= 0)) {
                    parameters.add(corpo.substring(initArgument, i));
                }

            } else if (corpo.charAt(i) == ',') {
                if (countParentesis == 1) {
                    parameters.add(corpo.substring(initArgument, i));
                    initArgument = -1;
                }

            } else if (initArgument < 0) {
                initArgument = i;
            }
            if (countParentesis == 0) {
                endOfFunction = i;
                break;
            }
        }

        return Pair.of(parameters, endOfFunction);
    }
}