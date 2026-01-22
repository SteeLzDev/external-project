package com.zetra.econsig.persistence.query;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.persistence.dao.DAOFactory;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: HQuery</p>
 * <p>Description: Classe base para construções de query usando a linguagem HQL do Hibernate</p>
 * <p>Copyright: Copyright (c) 2008-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1104")
public abstract class HQuery {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(HQuery.class);

    protected boolean indexHintEnabled = true;

    private String queryString;
    private Set<String> paramExpasion = new HashSet<>();

	public Integer maxResults = null;
	public Integer firstResult = null;

	protected abstract Query<Object[]> preparar(Session session) throws HQueryException;

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(Query<Object[]> query) {
        queryString = query.getQueryString();
    }

    private Query<Object[]> prepararQuery(Session session) throws HQueryException {
        /**
         * Inclui no SQL final, um comentário com o nome completo da classe Java responsável
         * pela implementação do HQL. É necessário para que o interceptor que inclui hints de
         * índices funcione corretamente.
         * @see com.zetra.econsig.persistence.interceptor.IndexHintInterceptor
         */
        final Query<Object[]> query = preparar(session);

        if (indexHintEnabled) {
            query.setComment(this.getClass().getName());
        }
        return query;
    }

    /**
     * lista de campos a retornar na consulta. Deve-se respeitar a
     * ordem definada na query SQL
     * @return
     */
    @SuppressWarnings("java:S1168")
    protected String[] getFields() {
        return null;
    }

	@SuppressWarnings({ "unchecked", "deprecation" })
    protected Query<Object[]> instanciarQuery(Session session, String corpo) {
		final Query<Object[]> query = session.createQuery(corpo);
		if (maxResults != null) {
			query.setMaxResults(maxResults);
        }
		if (firstResult != null) {
			query.setFirstResult(firstResult);
        }
        setQueryString(query);
		return query;
	}

    /**
     * Retorna um Iterator para que não se necessite trazer o resultado inteiro para
     * a memória.
     * @param session
     * @return
     * @throws HQueryException
     */
    public Iterator<Object[]> executarIterator(Session session) throws HQueryException {
        final Query<Object[]> query = prepararQuery(session);
        return query.stream().iterator();
    }

    /**
     * Resultado da consulta retornado como um mapeamento onde
     * a chave é a primeira coluna de resultado e o valor é a
     * segunda coluna.
     * @return
     * @throws HQueryException
     */
	@SuppressWarnings("rawtypes")
    public Map executarMapa() throws HQueryException {
		final Session session = SessionUtil.getSession();
		try {
			return executarMapa(session);
		} finally {
            SessionUtil.closeSession(session);
		}
	}

	@SuppressWarnings("rawtypes")
    public Map executarMapa(Session session) throws HQueryException {
		final Query<Object[]> query = prepararQuery(session);
		final HashMap<Object, Object> resultado = new HashMap<>();
		Object[] item = null;

        final Iterator<Object[]> iterator = query.stream().iterator();
		while (iterator.hasNext()) {
			item = iterator.next();
			resultado.put((item[0] instanceof String) ? item[0].toString().trim() : item[0],
                          (item[1] instanceof String) ? item[1].toString().trim() : item[1]);
		}

		return resultado;
	}

    /**
     * Resultado da consulta retornado como uma lista de valores
     * únicos. Para consultas que retornam apenas uma coluna.
     * @return
     * @throws HQueryException
     */
    public <T> List<T> executarLista() throws HQueryException {
        final Session session = SessionUtil.getSession();
        try {
            return executarLista(session);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> executarLista(Session session) throws HQueryException {
        final Query<T> query = (Query<T>) prepararQuery(session);
        return query.list();
    }

    /**
     * Resultado da pesquisa retornado como uma lista de Transfer Objects
     * para consultas que retornam mais de uma coluna.
     * @return
     * @throws HQueryException
     */
	public List<TransferObject> executarDTO() throws HQueryException {
		return executarDTO(null);
	}

	public <T> List<T> executarDTO(Class<T> dtoClass) throws HQueryException {
		final Session session = SessionUtil.getSession();
		try {
			return executarDTO(session, dtoClass);
		} finally {
            SessionUtil.closeSession(session);
		}
	}

	@SuppressWarnings({ "java:S3776", "unchecked" })
    public <T> List<T> executarDTO(Session session, Class<T> dtoClass) throws HQueryException {
		final Query<Object[]> query = prepararQuery(session);
		final boolean customTO = ((dtoClass == null) || dtoClass.equals(TransferObject.class) || dtoClass.equals(CustomTransferObject.class));
		final String[] fields = getFields();
		if (fields == null) {
			throw new HQueryException("mensagem.erro.query.metodo.get.fields.nao.implementado", (AcessoSistema) null);
		}

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
                try {
                    for (final String field : fields) {
                        values[i] = (values[i] instanceof String) ? values[i].toString().trim() : values[i];
                        row.setAttribute(field, values[i]);
                        i++;
                    }
                } catch (final ArrayIndexOutOfBoundsException e) {
                    LOG.error(e.getMessage(), e);
                }
                result.add((T) row);
            }
        } catch (final Exception ex) {
            throw new HQueryException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }

	    return result;
	}

    /**
     * Utilizado para execução de contagem de número de linhas de uma
     * consulta a base de dados.
     * @return
     * @throws HQueryException
     */
    public int executarContador() throws HQueryException {
        return executarContador(null);
    }

    public int executarContador(Session session) throws HQueryException {
        List<?> resultado = null;
        if (null == session) {
            resultado = executarLista();
        } else {
            resultado = executarLista(session);
        }
        if ((resultado != null) && (resultado.size() == 1) && (resultado.get(0) != null)) {
            return Integer.parseInt(resultado.get(0).toString());
        }
        return -1;
    }

    /**
     * Utilizado para execução de somatório de campos numéricos numa
     * consulta de banco de dados.
     * @return
     * @throws HQueryException
     */
    public BigDecimal executarSomatorio() throws HQueryException {
        final List<?> resultado = executarLista();
        if ((resultado != null) && (resultado.size() == 1) && (resultado.get(0) != null)) {
            return ((BigDecimal) resultado.get(0));
        }
        return null;
    }

    public BigDecimal executarSomatorio(BigDecimal vlrDefault) throws HQueryException {
        final BigDecimal somatorio = executarSomatorio();
        return (somatorio == null ? vlrDefault : somatorio);
    }

    /**
     * Retorna o predicado a ser concatenado na query relativo a cláusula nomeada
     * para o campo informado pelo parâmetro "nomeParametro" e de acordo com o tipo
     * de dados do objeto "valor".
     * @param nomeParametro
     * @param valor
     * @return
     */
    protected String criaClausulaNomeada(String nomeParametro, Object valor) {
        return criaClausulaNomeada(null, nomeParametro, valor);
    }

    /**
     * Retorna o predicado a ser concatenado na query relativo a cláusula nomeada
     * para o campo informado pelo parâmetro "nomeParametro" e de acordo com o tipo
     * de dados do objeto "valor". Trata o like usando a funcao like_ci_ai se nomeColuna for diferente de nulo.
     * @param nomeColuna
     * @param nomeParametro
     * @param valor
     * @return
     */
    @SuppressWarnings("java:S3776")
    protected String criaClausulaNomeada(String nomeColuna, String nomeParametro, Object valor) {
        String retorno = "";

        if ((valor instanceof Collection) || (valor instanceof Object[])) {
            Collection<?> valores = null;
            if (valor instanceof Collection) {
                valores = (Collection<?>) valor;
            } else {
                valores = Arrays.asList((Object[]) valor);
            }
            final boolean hasNotKey = valores.contains(CodedValues.NOT_EQUAL_KEY);
            final int qtdParamLista = valores.size() - (hasNotKey ? 1 : 0);
            if (qtdParamLista > 1) {
                if (enableMultiValueParamExpansion(qtdParamLista)) {
                    retorno = (hasNotKey ? " NOT " : " ") + "IN (" + expandirClausulaLista(nomeParametro, qtdParamLista) + ")";
                } else {
                    retorno = (hasNotKey ? " NOT " : " ") + "IN (:" + nomeParametro + ")";
                }
            } else {
                retorno = (hasNotKey ? " !" : " ") + "= :" + nomeParametro;
            }

        } else if (valor.equals(CodedValues.IS_NULL_KEY) || valor.equals(CodedValues.IS_NOT_NULL_KEY)) {
            retorno = ((valor.equals(CodedValues.IS_NULL_KEY)) ? " IS NULL " : " IS NOT NULL ");

        } else if ((valor.toString().indexOf(CodedValues.LIKE_MULTIPLO) != -1) ||
            (valor.toString().indexOf(CodedValues.LIKE_UNICO) != -1)) {
            if (nomeColuna != null) {
                if (DAOFactory.isOracle()) {
                    retorno = " like_ci_ai(" + nomeColuna + ", :" + nomeParametro + ") ";
                } else {
                    retorno = " " + nomeColuna + " like :" + nomeParametro + " ";
                }
            } else {
                retorno = " like :" + nomeParametro;
            }
        } else if (valor.toString().indexOf(CodedValues.NOT_EQUAL_KEY) != -1) {
            retorno = " " + (nomeColuna == null ? "" : nomeColuna) + " != :" + nomeParametro + " ";
        } else {
            retorno = " " + (nomeColuna == null ? "" : nomeColuna) + " = :" + nomeParametro + " ";
        }

        return retorno;
    }

    private String expandirClausulaLista(String nomeParametro, int tamanhoLista) {
        paramExpasion.add(nomeParametro);

        final StringBuilder query = new StringBuilder();
        for (int i = 0; i < tamanhoLista; i++) {
            if (i > 0) {
                query.append(",");
            }
            query.append(":").append(nomeParametro).append("_").append(i);
        }
        return query.toString();
    }

    /**
     * Define o valor para a cláusula nomeada de acordo com o tipo do
     * objeto valor informado por parâmetro.
     * @param nomeParametro
     * @param valor
     * @param query
     */
    @SuppressWarnings("java:S3776")
    protected void defineValorClausulaNomeada(String nomeParametro, Object valor, Query<Object[]> query) {
        if ((valor instanceof Collection) || (valor instanceof Object[])) {
            Collection<?> valores = null;
            if (valor instanceof Collection) {
                valores = (Collection<?>) valor;
            } else {
                valores = Arrays.asList((Object[]) valor);
            }
            if (valores != null) {
                if (!valores.isEmpty() && valores.contains(CodedValues.NOT_EQUAL_KEY)) {
                    final List<?> novosValores = new ArrayList<>(valores);
                    novosValores.remove(CodedValues.NOT_EQUAL_KEY);
                    valores = novosValores;
                }
                if (valores.size() > 1) {
                    if (enableMultiValueParamExpansion(valores.size()) && paramExpasion.contains(nomeParametro)) {
                        Iterator<?> itValores = valores.iterator();
                        for (int i = 0; i < valores.size(); i++) {
                            query.setParameter(nomeParametro + "_" + i, itValores.next());
                        }
                    } else {
                        query.setParameterList(nomeParametro, valores, Object.class);
                    }
                } else if (valores.size() == 1) {
                    query.setParameter(nomeParametro, valores.iterator().next());
                } else if (DAOFactory.isOracle()) {
                    // No oracle não posso usar Vazio como cláusula porque é considerado nulo
                    query.setParameter(nomeParametro, " ");
                } else {
                    query.setParameter(nomeParametro, "");
                }
            }

        } else if (valor.equals(CodedValues.IS_NULL_KEY) || valor.equals(CodedValues.IS_NOT_NULL_KEY)) {
            // Não faz nada, pois esta cláusula não tem atributos

        } else if (valor instanceof String valorAsString) {
            valorAsString = TextHelper.escapeSql(valor);
            String multiplo = "%";
            String unico = "_";
            if (DAOFactory.isOracle()) {
                multiplo = ".*";
                unico = ".";
            }

            if ((valorAsString.indexOf(CodedValues.LIKE_MULTIPLO) != -1) ||
                (valorAsString.indexOf(CodedValues.LIKE_UNICO) != -1)) {
                if (DAOFactory.isOracle()) {
                    if(!((String)valor).startsWith(CodedValues.LIKE_MULTIPLO)){
                        valorAsString = "^" + valorAsString;
                    }
                    if(!((String)valor).endsWith(CodedValues.LIKE_MULTIPLO)){
                        valorAsString = valorAsString + "$";
                    }
                }
                valorAsString = valorAsString.replaceAll(CodedValues.LIKE_MULTIPLO, multiplo);
                valorAsString = valorAsString.replaceAll(CodedValues.LIKE_UNICO, unico);

            } else if (valorAsString.indexOf(CodedValues.NOT_EQUAL_KEY) != -1) {
                valorAsString = valorAsString.replace(CodedValues.NOT_EQUAL_KEY, "");
            }
            query.setParameter(nomeParametro, valorAsString);

        } else {
            query.setParameter(nomeParametro, valor);
        }
    }

    @SuppressWarnings("rawtypes")
    public ScrollableResults executarScroll(Session session) throws HQueryException {
        final Query<?> query = prepararQuery(session);
        return query.scroll();
    }

    protected java.sql.Date parseDateString(String date) {
        try {
            return DateHelper.toSQLDate(DateHelper.parse(date, "yyyy-MM-dd"));
        } catch (final ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            return null;
        }
    }

    protected java.util.Date parseDateTimeString(String dateTime) {
        try {
            return DateHelper.parse(dateTime, "yyyy-MM-dd HH:mm:ss");
        } catch (final ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            return null;
        }
    }

    private boolean enableMultiValueParamExpansion(int collectionSize) {
        return !"false".equalsIgnoreCase(System.getProperty("enableMultiValueParamExpansion"))
            && !(this instanceof HNativeQuery) 
            && collectionSize < 50;
    }
}
