package com.zetra.econsig.report.reports;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p> Title: RelatorioEditavel</p>
 * <p> Description: Relatório Editável</p>
 * <p> Copyright: Copyright (c) 2011-2023</p>
 * <p> Company: ZetraSoft Ltda. </p>
 * @author Alexandre Fernandes, Ricardo Magno, Igor lucas, Douglas Neves, Leonel Martins
 */
public class RelatorioEditavel extends ReportTemplate {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RelatorioEditavel.class);

    public RelatorioEditavel() {
        //
    }

    @Override
    public String getSql(CustomTransferObject criterio) throws DAOException {
        try {
            // Define parâmetros da consulta que será executada
            return defineParametrosQuery(relatorio.getTemplateSql(), criterio);
        } catch (final Exception ex) {
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.erro.gerar.relatorio", (AcessoSistema) null), ex);
            if (ex instanceof final DAOException dex) {
                throw dex;
            } else  {
                throw new DAOException("mensagem.erro.gerar.relatorio", (AcessoSistema) null, ex);
            }
        }
    }

    @Override
    public String getSqlSubrelatorio(String query, CustomTransferObject criterio) throws DAOException {
        try {
            // Define parâmetros da consulta que será executada
            return defineParametrosQuery(query, criterio);
        } catch (Exception e) {
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.erro.gerar.relatorio", (AcessoSistema) null), e);
            if (e instanceof DAOException) {
                throw (DAOException)e;
            } else  {
                throw new DAOException("mensagem.erro.gerar.relatorio", (AcessoSistema) null, e);
            }
        }
    }

    @Override
    public void preSqlProcess(Connection conn){
    }

    @Override
    public void postSqlProcess(Connection conn){
    }

    /**
     * Define o valor para a cláusula nomeada de acordo com o tipo do
     * objeto valor informado por parâmetro.
     * @param nomeParametro
     * @param valor
     * @param query
     */
    protected static String defineValorClausulaNomeada(String nomeParametro, Object valor, String query) {
        if (TextHelper.isNull(valor)) {
            final String regex = "\\((()|( )+)" + nomeParametro + "(()|( )+)\\)";
            query = query.replaceAll(regex, "('') or 1 = 1");
            query = query.replaceAll(nomeParametro, "'' or 1 = 1");

        } else if (valor instanceof Collection<?>) {
            if (((Collection<?>) valor).size() > 1) {
                query = query.replaceAll(nomeParametro, "'" + TextHelper.join((Collection<?>) valor, "', '") + "'");
            } else if (((Collection<?>) valor).size() == 1) {
                query = query.replaceAll(nomeParametro, "'" + ((Collection<?>) valor).iterator().next() + "'");
            } else {
                query = query.replaceAll(nomeParametro, "''");
            }

        } else if (valor.equals(CodedValues.IS_NULL_KEY) || valor.equals(CodedValues.IS_NOT_NULL_KEY)) {
            // Não faz nada, pois esta cláusula não tem atributos

        } else if (valor instanceof String) {
            valor = TextHelper.escapeSql(valor);

            if ((valor.toString().indexOf(CodedValues.LIKE_MULTIPLO) != -1) ||
                    (valor.toString().indexOf(CodedValues.LIKE_UNICO) != -1)) {
                valor = valor.toString().replace(CodedValues.LIKE_MULTIPLO, "%");
                valor = valor.toString().replace(CodedValues.LIKE_UNICO, "_");

            } else if (valor.toString().indexOf(CodedValues.NOT_EQUAL_KEY) != -1) {
                valor = valor.toString().replace(CodedValues.NOT_EQUAL_KEY, "");
            }
            query = query.replaceAll(nomeParametro, "'" + valor.toString() + "'");

        } else {
            query = query.replaceAll(nomeParametro, "'" + valor + "'");
        }

        return query;
    }

    protected static String defineParametrosQuery(String query, CustomTransferObject criterio) {
        query = query.toLowerCase();

        int inicio = 0;
        int fim = 0;
        do {
            inicio = query.indexOf("<@", inicio);
            fim = 0;
            if (inicio > 0) {
                fim = query.indexOf(">", inicio) + 1;
                LOG.debug(query.toLowerCase().substring(inicio, fim));
                final String parametro = query.toLowerCase().substring(inicio, fim);
                Object valor = criterio.getAttribute(parametro);

                if (parametro.equals("<@campo_org>") && valor != null) {
                    // Se é campo de órgão e o valor foi informado, remove aspas dos valores passados, pois o campo aceita múltiplos
                    // valores e estes já chegam com aspas em cada valor
                    if (valor instanceof Collection<?> lista) {
                        List<Object> novaLista = new ArrayList<>();
                        for (Object elemento : lista) {
                            if (elemento instanceof String elementoString) {
                                if (elementoString != null && elementoString.length() >= 2 && elementoString.charAt(0) == '\'' && elementoString.charAt(elementoString.length() - 1) == '\'') {
                                    elemento = elementoString.substring(1, elementoString.length() - 1);
                                }
                            }
                            novaLista.add(elemento);
                        }
                        valor = novaLista;
                    }
                }

                query = defineValorClausulaNomeada(parametro, valor, query);

                inicio = fim;
            }
        } while (fim > 0);

        LOG.debug("RELATORIO EDITAVEL QUERY: " + query);

        return query;
    }
}
