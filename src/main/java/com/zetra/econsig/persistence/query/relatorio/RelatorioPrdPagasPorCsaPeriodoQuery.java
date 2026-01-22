package com.zetra.econsig.persistence.query.relatorio;

import java.text.ParseException;
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
import com.zetra.econsig.persistence.query.retorno.ListaUltimosPeriodosHistConclusaoRetornoQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioPrdPagasPorCsaPeriodoQuery</p>
 * <p>Description: Query para relatório sintético de parcelas pagas por consignatária e período</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioPrdPagasPorCsaPeriodoQuery extends ReportHNativeQuery {
    public AcessoSistema responsavel;
    private String estCodigo;
    private List<String> orgCodigos;
    private String csaCodigo;
    private Boolean csaAtivo = null;
    public int countMes;
    public int countEst;
    public int countPeriodos;
    public List<String> campos;
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
        alias = new LinkedList<>();

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

        final StringBuilder sql = new StringBuilder();
        sql.append("SELECT csa.csa_nome as DESCRIPCION, csa.csa_identificador as CLAVE, ");

        Integer mesAnterior = null;
        List<String> periodosAnteriores = new ArrayList<>();

        final StringBuilder resultSequence = new StringBuilder();

        final int maxPeriodos = 1;
        campos.add(ApplicationResourcesHelper.getMessage("rotulo.relatorio.saldo.por.csa.identificador", responsavel));
        campos.add(ApplicationResourcesHelper.getMessage("rotulo.relatorio.saldo.por.csa.nome", responsavel));

        for (int i = (periodos.size() - 1); i > 0; i--) {
            final TransferObject periodoHist = periodos.get(i);
            final Date hcrPeriodo = (Date) periodoHist.getAttribute(Columns.HCR_PERIODO);
            final int mes = DateHelper.getMonth(hcrPeriodo);
            final String perFormatado = DateHelper.format(hcrPeriodo, "yyyy-MM-dd");

            if (mesAnterior == null) {
                mesAnterior = mes;
                periodosAnteriores.add(perFormatado);
            } else if (mesAnterior != mes) {
                resultSequence.append("sum(case when ");
                int inicio = 0;
                for (final String periodoAnterior : periodosAnteriores) {
                    if (inicio == 0) {
                        resultSequence.append("prd.prd_data_desconto in ('").append(periodoAnterior);
                        inicio++;
                    } else {
                        resultSequence.append("','").append(periodoAnterior);
                    }
                }
                resultSequence.append("')");

                Date dataAnterior = null;
                try {
                    dataAnterior = DateHelper.parse(periodosAnteriores.get(0), "yyyy-MM-dd");
                } catch (final ParseException e) {
                    throw new HQueryException("mensagem.erroInternoSistema", responsavel);
                }

                resultSequence.append(" AND prd.spd_codigo = '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("' then prd.prd_vlr_realizado else 0 end) as PAGO_").append(countMes).append(",");
                campos.add(DateHelper.getMonthName(dataAnterior) + "/" + DateHelper.getYear(dataAnterior));
                countMes++;
                periodosAnteriores = new ArrayList<>();
                periodosAnteriores.add(perFormatado);
                mesAnterior = mes;
            } else {
                periodosAnteriores.add(perFormatado);
            }
        }

        final String periodoBaseFormatado = DateHelper.format(periodoBase, "yyyy-MM-dd");
        final int mes = DateHelper.getMonth(periodoBase);

        if ((mesAnterior == null) || (mesAnterior == mes)) {
            periodosAnteriores.add(periodoBaseFormatado);

            resultSequence.append("sum(case when ");
            int inicio = 0;
            if ((periodosAnteriores != null) && !periodosAnteriores.isEmpty()) {
                for (final String periodoAnterior : periodosAnteriores) {
                    if (inicio == 0) {
                        resultSequence.append("prd.prd_data_desconto in ('").append(periodoAnterior);
                        inicio++;
                    } else {
                        resultSequence.append("','").append(periodoAnterior);
                    }
                }
            } else {
                resultSequence.append("prd.prd_data_desconto in ('").append(periodoBaseFormatado);
            }
            resultSequence.append("')");

            resultSequence.append(" AND prd.spd_codigo = '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("' then prd.prd_vlr_realizado else 0 end) as PAGO_").append(countMes);
            campos.add(DateHelper.getMonthName(periodoBase) + "/" + DateHelper.getYear(periodoBase));
        } else {
            resultSequence.append("sum(case when ");
            int inicio = 0;
            for (final String periodoAnterior : periodosAnteriores) {
                if (inicio == 0) {
                    resultSequence.append("prd.prd_data_desconto in ('").append(periodoAnterior);
                    inicio++;
                } else {
                    resultSequence.append("','").append(periodoAnterior);
                }
            }
            resultSequence.append("')");

            Date dataAnterior = null;
            try {
                dataAnterior = DateHelper.parse(periodosAnteriores.get(0), "yyyy-MM-dd");
            } catch (final ParseException e) {
                throw new HQueryException("mensagem.erroInternoSistema", responsavel);
            }

            resultSequence.append(" AND prd.spd_codigo = '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("' then prd.prd_vlr_realizado else 0 end) as PAGO_").append(countMes).append(",");
            campos.add(DateHelper.getMonthName(dataAnterior) + "/" + DateHelper.getYear(dataAnterior));
            countMes++;

            resultSequence.append("sum(case when ").append("prd.prd_data_desconto in ('").append(periodoBaseFormatado).append("')").append(" AND prd.spd_codigo = '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("' then prd.prd_vlr_realizado else 0 end) as PAGO_").append(countMes);
            campos.add(DateHelper.getMonthName(periodoBase) + "/" + DateHelper.getYear(periodoBase));
        }

        sql.append(resultSequence);
        sql.append(" FROM tb_parcela_desconto prd");
        sql.append(" INNER JOIN tb_aut_desconto ade ON (prd.ade_codigo = ade.ade_codigo)");
        sql.append(" INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo)");
        sql.append(" INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo)");
        sql.append(" INNER JOIN tb_consignataria csa ON (cnv.csa_codigo = csa.csa_codigo)");
        sql.append(" INNER JOIN tb_orgao org ON (cnv.org_codigo = org.org_codigo)");
        sql.append(" INNER JOIN tb_estabelecimento est ON (org.est_codigo = est.est_codigo)");
        sql.append(" WHERE 1=1 ");
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
        sql.append(" GROUP BY csa.csa_nome, csa.csa_identificador");

        final Query<Object[]> query = instanciarQuery(session, sql.toString());

        if (!TextHelper.isNull(estCodigo)) {
            defineValorClausulaNomeada("estCodigo", estCodigo, query);
        }
        if ((orgCodigos != null) && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }
        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        countMes = countMes + 1;
        countPeriodos = maxPeriodos;

        return query;
    }

    @Override
    public String[] getFields() {
        return campos != null ? campos.toArray(new String[]{}) : null;
    }
}
