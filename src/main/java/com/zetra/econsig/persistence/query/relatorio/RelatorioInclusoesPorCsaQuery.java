package com.zetra.econsig.persistence.query.relatorio;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.delegate.PeriodoDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.persistence.query.estabelecimento.ListaEstabelecimentoQuery;
import com.zetra.econsig.persistence.query.retorno.ListaUltimosPeriodosHistConclusaoRetornoQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioInclusoesPorCsaQuery</p>
 * <p>Description: Query para relatório de inclusões por consignatária e período</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioInclusoesPorCsaQuery extends ReportHNativeQuery {

    public AcessoSistema responsavel;
    private String estCodigo;
    private List<String> orgCodigos;
    private String csaCodigo;
    private Boolean csaAtivo = null;
    public int countMes;
    public int countEst;
    public int countPeriodos;
    public List<String> campos;
    public List<String> camposTotal;
    public List<String> alias;

    @Override
    public void setCriterios(TransferObject criterio) {
        estCodigo = (String) criterio.getAttribute(Columns.EST_CODIGO);
        orgCodigos = (List<String>) criterio.getAttribute(Columns.ORG_CODIGO);
        csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
        csaAtivo = (Boolean) criterio.getAttribute(Columns.CSA_ATIVO);
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final ListaUltimosPeriodosHistConclusaoRetornoQuery lstPeriodos = new ListaUltimosPeriodosHistConclusaoRetornoQuery();
        lstPeriodos.orgCodigos = orgCodigos;
        lstPeriodos.maxResults = 24;
        Date periodoBase = null;
        campos = new LinkedList<>();
        camposTotal = new LinkedList<>();
        alias = new LinkedList<>();
        final String tituloTotal = ApplicationResourcesHelper.getMessage("rotulo.relatorio.inclusoes.por.csa.total", responsavel);

        // Status
        final List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_SOLICITADO);
        sadCodigos.add(CodedValues.SAD_AGUARD_CONF);
        sadCodigos.add(CodedValues.SAD_AGUARD_DEFER);
        sadCodigos.add(CodedValues.SAD_INDEFERIDA);
        sadCodigos.add(CodedValues.SAD_CANCELADA);

        try {
            final PeriodoDelegate periodoDelegate = new PeriodoDelegate();
            final List<String> estCodigos = new ArrayList<>();
            estCodigos.add(estCodigo);

            periodoBase = periodoDelegate.obtemUltimoPeriodoExportado(orgCodigos, estCodigos, false, null, responsavel);
        } catch (final PeriodoException e) {
            throw new HQueryException("mensagem.erroInternoSistema", responsavel);
        }

        lstPeriodos.periodo = DateHelper.format(periodoBase, "yyyy-MM-dd");
        final List<TransferObject> periodos = lstPeriodos.executarDTO(session, null);

        if (periodoBase == null) {
            final TransferObject periodoRecente = periodos.get(0);
            periodoBase = (Date) periodoRecente.getAttribute(Columns.HCR_PERIODO);
        }

        final ListaEstabelecimentoQuery lstEst = new ListaEstabelecimentoQuery();
        lstEst.estCodigo = estCodigo;
        final List<TransferObject> estabelecimentos = lstEst.executarDTO(session, null);

        final StringBuilder sql = new StringBuilder();
        sql.append("SELECT inc.csa_nome as CSA_NOME, inc.csa_identificador as CSA_IDENTIFICADOR ");
        alias.add("CSA_NOME");
        alias.add("CSA_IDENTIFICADOR");

        final StringBuilder innerSql = new StringBuilder("SELECT csa.csa_codigo, csa.csa_nome, csa.csa_identificador ");

        Integer mesAnterior = null;
        final List<String> periodosAnteriores = new ArrayList<>();

        final StringBuilder resultSequence = new StringBuilder();

        int countMes = 0;
        int countEst = 1;
        int countPeriodos = 1;
        int maxPeriodos = 1;
        Date periodoIni = null;
        Date periodoFim = null;

        for (int i = (periodos.size() -1); i >= 0; i--) {
            final TransferObject periodoHist = periodos.get(i);
            final Date hcrPeriodo = (Date) periodoHist.getAttribute(Columns.HCR_PERIODO);
            if (i == (periodos.size() -1)) {
                periodoIni = hcrPeriodo;
            }
            if (i == 0) {
                periodoFim = hcrPeriodo;
            }
            final int mes = DateHelper.getMonth(hcrPeriodo);
            final int dia = DateHelper.getDay(hcrPeriodo);
            final String quinzena = ApplicationResourcesHelper.getMessage("rotulo.relatorio.inclusoes.por.csa.quinzena." + dia, responsavel);
            final String perFormatado = DateHelper.format(hcrPeriodo, "yyyy-MM-dd");
            countEst = 1;

            if (mesAnterior == null) {
                mesAnterior = mes;
                countMes++;
            }

            if (mesAnterior != mes) {
                countMes++;
            }

            periodosAnteriores.add(perFormatado);
            maxPeriodos = (countPeriodos > maxPeriodos) ? countPeriodos : maxPeriodos;
            for (final TransferObject est : estabelecimentos) {
                final String estIdn = (String) est.getAttribute(Columns.EST_IDENTIFICADOR);
                final String estNome = (String) (!TextHelper.isNull(est.getAttribute(Columns.EST_NOME_ABREV)) ? est.getAttribute(Columns.EST_NOME_ABREV) : est.getAttribute(Columns.EST_NOME));
                innerSql.append(", SUM(CASE WHEN est.est_identificador = '").append(estIdn).append("' AND COALESCE(ade.ade_ano_mes_ini_ref, ade.ade_ano_mes_ini) = '").append(perFormatado).append("' ");
                innerSql.append("THEN 1 ELSE 0 END) AS ").append("QTDE_Q_").append(countMes).append("_").append(countEst).append("_").append(countPeriodos);
                innerSql.append(", SUM(CASE WHEN est.est_identificador = '").append(estIdn).append("' AND COALESCE(ade.ade_ano_mes_ini_ref, ade.ade_ano_mes_ini) = '").append(perFormatado).append("' ");
                innerSql.append("THEN COALESCE(COALESCE(ade.ade_prazo_ref,  ade.ade_prazo),0) ELSE 0 END) AS ").append("VALOR_Q_").append(countMes).append("_").append(countEst).append("_").append(countPeriodos);
                resultSequence.append(", SUM(QTDE_Q_").append(countMes).append("_").append(countEst).append("_").append(countPeriodos).append(") as TOTAL_QTDE_Q_").append(countMes).append("_").append(countEst).append("_").append(countPeriodos);
                resultSequence.append(", SUM(VALOR_Q_").append(countMes).append("_").append(countEst).append("_").append(countPeriodos).append(") as TOTAL_VALOR_Q_").append(countMes).append("_").append(countEst).append("_").append(countPeriodos);
                final String titulo = quinzena + " " + DateHelper.getMonthName(hcrPeriodo).toUpperCase() + "/" + DateHelper.getYear(hcrPeriodo) + " " + estNome;
                campos.add(titulo);
                alias.add("TOTAL_QTDE_Q_" + countMes + "_" + countEst + "_" + countPeriodos);
                alias.add("TOTAL_VALOR_Q_" + countMes + "_" + countEst + "_" + countPeriodos);
                countEst++;
            }
            // total acumulado por periodo (todos os estabelecimentos)
            innerSql.append(", SUM(CASE WHEN COALESCE(ade.ade_ano_mes_ini_ref, ade.ade_ano_mes_ini) = '").append(perFormatado).append("' ");
            innerSql.append("THEN 1 ELSE 0 END) AS ").append("QTDE_Q_").append(countMes).append("_").append(countPeriodos);
            innerSql.append(", SUM(CASE WHEN COALESCE(ade.ade_ano_mes_ini_ref, ade.ade_ano_mes_ini) = '").append(perFormatado).append("' ");
            innerSql.append("THEN COALESCE(COALESCE(ade.ade_prazo_ref,  ade.ade_prazo),0) ELSE 0 END) AS ").append("VALOR_Q_").append(countMes).append("_").append(countPeriodos);
            resultSequence.append(", SUM(QTDE_Q_").append(countMes).append("_").append(countPeriodos).append(") as TOTAL_QTDE_Q_").append(countMes).append("_").append(countPeriodos);
            resultSequence.append(", SUM(VALOR_Q_").append(countMes).append("_").append(countPeriodos).append(") as TOTAL_VALOR_Q_").append(countMes).append("_").append(countPeriodos);
            final String titulo = quinzena + " " + DateHelper.getMonthName(hcrPeriodo).toUpperCase() + "/" + DateHelper.getYear(hcrPeriodo) + " " + tituloTotal;
            campos.add(titulo);
            alias.add("TOTAL_QTDE_Q_" + countMes + "_" + countPeriodos);
            alias.add("TOTAL_VALOR_Q_" + countMes + "_" + countPeriodos);
            mesAnterior = mes;
            countPeriodos++;
        }

        sql.append(resultSequence).append(" FROM (").append(innerSql);
        sql.append(" FROM tb_aut_desconto ade ");
        sql.append(" INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo)");
        sql.append(" INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo)");
        sql.append(" INNER JOIN tb_consignataria csa ON (cnv.csa_codigo = csa.csa_codigo)");
        sql.append(" INNER JOIN tb_orgao org ON (cnv.org_codigo = org.org_codigo)");
        sql.append(" INNER JOIN tb_estabelecimento est ON (org.est_codigo = est.est_codigo)");
        sql.append(" WHERE ade.sad_codigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));
        if (!TextHelper.isNull(periodoIni) && !TextHelper.isNull(periodoFim)) {
            sql.append(" AND COALESCE(ade.ade_ano_mes_ini_ref, ade.ade_ano_mes_ini) BETWEEN '").append(periodoIni).append("' AND '").append(periodoFim).append("' ");
        }
        if (!TextHelper.isNull(estCodigo)) {
            sql.append(" and est.est_codigo ").append(criaClausulaNomeada("estCodigo", estCodigo));
        }
        if ((orgCodigos != null) && !orgCodigos.isEmpty()) {
            sql.append(" and org.org_codigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }
        if (!TextHelper.isNull(csaCodigo)) {
            sql.append(" and csa.csa_codigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }
        if ((csaAtivo != null) && csaAtivo) {
            sql.append(" and csa.csa_ativo = ").append(CodedValues.STS_ATIVO);
        } else if ((csaAtivo != null) && !csaAtivo) {
            sql.append(" and csa.csa_ativo = ").append(CodedValues.STS_INATIVO);
        }
        sql.append(" GROUP BY csa.csa_codigo, csa.csa_nome, csa.csa_identificador ");
        sql.append(") inc ");
        sql.append(" GROUP BY inc.csa_nome, inc.csa_identificador ");

        final Query<Object[]> query = instanciarQuery(session, sql.toString());

        defineValorClausulaNomeada("sadCodigos", sadCodigos, query);
        if (!TextHelper.isNull(estCodigo)) {
            defineValorClausulaNomeada("estCodigo", estCodigo, query);
        }
        if ((orgCodigos != null) && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }
        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        this.countEst = estabelecimentos.size();
        this.countMes = countMes;
        this.countPeriodos = maxPeriodos;

        return query;
    }

    @Override
    public String[] getFields() {
        return alias != null ? alias.toArray(new String[]{}) : null;
    }
}