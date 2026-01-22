package com.zetra.econsig.persistence.query.relatorio;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.delegate.PeriodoDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.Estabelecimento;
import com.zetra.econsig.persistence.entity.EstabelecimentoHome;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.persistence.query.estabelecimento.ListaEstabelecimentoQuery;
import com.zetra.econsig.persistence.query.retorno.ListaUltimosPeriodosHistConclusaoRetornoQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioSaldoDevedorPorCsaPeriodoQuery</p>
 * <p>Description: Query para relatório sintético de saldo devedor por consignatária e período</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioSaldoDevedorPorCsaPeriodoQuery extends ReportHNativeQuery {

    public AcessoSistema responsavel;
    private String estCodigo = "";
    private List<String> orgCodigos;
    private String csaCodigo = "";
    private Boolean csaAtivo = null;
    public int countMes;
    public int countEst;
    public int countPeriodos;
    public List<String> campos;
    public List<String> camposTotal;
    public List<String> camposSpan;
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
        camposSpan = new LinkedList<>();
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

        List<TransferObject> estabelecimentos = null;

        if (TextHelper.isNull(estCodigo)) {
            final ListaEstabelecimentoQuery lstEst = new ListaEstabelecimentoQuery();
            estabelecimentos = lstEst.executarDTO(session, null);

        } else {
            try {
                final Estabelecimento estabelecimento = EstabelecimentoHome.findByPrimaryKey(estCodigo);
                final TransferObject estTo = new CustomTransferObject();
                estTo.setAttribute(Columns.EST_IDENTIFICADOR, estabelecimento.getEstIdentificador());
                estTo.setAttribute(Columns.EST_NOME_ABREV, !TextHelper.isNull(estabelecimento.getEstNomeAbrev()) ? estabelecimento.getEstNomeAbrev() : estabelecimento.getEstNome());
                estabelecimentos = new ArrayList<> ();
                estabelecimentos.add(estTo);
            } catch (final FindException e) {
                throw new HQueryException("mensagem.erro.estabelecimento.nao.encontrado", responsavel);
            }
        }

        final StringBuilder sql = new StringBuilder();
        sql.append("SELECT X.csa_nome as DESCRIPCION, X.csa_identificador as CLAVE, ");

        final StringBuilder innerSql = new StringBuilder("SELECT csa.csa_codigo, csa.csa_nome, csa.csa_identificador, ade.ade_codigo,");
        innerSql.append("coalesce(ade.ade_vlr_ref, ade.ade_vlr) * coalesce(ade.ade_prazo_ref, ade.ade_prazo) as TOTAL,");

        Integer mesAnterior = null;
        List<String> periodosAnteriores = new ArrayList<>();

        final StringBuilder resultSequence = new StringBuilder();

        int countRecMensual = 1;
        int countMes = 1;
        int countEst = 1;
        int countPeriodos = 1;
        int maxPeriodos = 1;
        campos.add(ApplicationResourcesHelper.getMessage("rotulo.relatorio.saldo.por.csa.identificador", responsavel));
        campos.add(ApplicationResourcesHelper.getMessage("rotulo.relatorio.saldo.por.csa.nome", responsavel));

        for (int i = (periodos.size() - 1); i > 0; i--) {
            final TransferObject periodoHist = periodos.get(i);
            final Date hcrPeriodo = (Date) periodoHist.getAttribute(Columns.HCR_PERIODO);
            final int mes = DateHelper.getMonth(hcrPeriodo);
            final int dia = DateHelper.getDay(hcrPeriodo);
            final String perFormatado = DateHelper.format(hcrPeriodo, "yyyy-MM-dd");

            camposSpan.add(dia + ApplicationResourcesHelper.getMessage("rotulo.relatorio.saldo.por.csa.ordinal.prep", responsavel) + " " + DateHelper.getMonthName(hcrPeriodo) + " " + DateHelper.getYear(hcrPeriodo));

            if (mesAnterior == null) {
                mesAnterior = mes;
                camposTotal.add(ApplicationResourcesHelper.getMessage("rotulo.relatorio.saldo.por.csa.total", responsavel) + " " + DateHelper.getDay(hcrPeriodo) + ApplicationResourcesHelper.getMessage("rotulo.relatorio.saldo.por.csa.ordinal.prep", responsavel) + " " + DateHelper.getMonthName(hcrPeriodo) + "/" + DateHelper.getYear(hcrPeriodo));
                periodosAnteriores.add(perFormatado);
                maxPeriodos = (countPeriodos > maxPeriodos) ? countPeriodos : maxPeriodos;
                final ArrayList<String> totalOperandos = new ArrayList<>();
                for (final TransferObject est : estabelecimentos) {
                    final String estIdn = (String) est.getAttribute(Columns.EST_IDENTIFICADOR);
                    final String estNome = (String) (!TextHelper.isNull(est.getAttribute(Columns.EST_NOME_ABREV)) ? est.getAttribute(Columns.EST_NOME_ABREV) : est.getAttribute(Columns.EST_NOME));
                    innerSql.append("sum(case when ").append("est.est_identificador = '").append(estIdn).append("' AND prd.prd_data_desconto <= '").append(perFormatado).append("' and prd.spd_codigo = '");
                    innerSql.append(CodedValues.SPD_LIQUIDADAFOLHA).append("' then prd.prd_vlr_realizado else 0 end) as ").append("REALIZADO_Q_").append(countMes).append("_").append(countEst).append("_").append(countPeriodos).append(",");
                    final StringBuilder montaResult = new StringBuilder("coalesce(sum(TOTAL) - sum(REALIZADO_Q_").append(countMes).append("_").append(countEst).append("_").append(countPeriodos).append("), 0)");
                    final StringBuilder resultFinal = new StringBuilder(montaResult).append(" as DEVIDO_Q_").append(countMes).append("_").append(countEst).append("_").append(countPeriodos).append(",");
                    resultSequence.append(resultFinal.toString());
                    campos.add(estNome);
                    alias.add("DEVIDO_Q_" + countMes + "_" + countEst + "_" + countPeriodos);
                    totalOperandos.add(montaResult.toString());
                    countEst++;
                }
                StringBuilder montaTotal = null;
                for (final String operando: totalOperandos) {
                    if (montaTotal == null) {
                        montaTotal = new StringBuilder(operando);
                    } else {
                        montaTotal.append(" + ").append(operando);
                    }
                }
                resultSequence.append(montaTotal).append(" as TOTAL_").append(countMes).append("_").append(countPeriodos).append(",");
                alias.add("TOTAL_" + countMes + "_" + countPeriodos);
                campos.add(ApplicationResourcesHelper.getMessage("rotulo.relatorio.saldo.por.csa.total", responsavel));
            } else if (mesAnterior != mes) {
                camposTotal.add(ApplicationResourcesHelper.getMessage("rotulo.relatorio.saldo.por.csa.total", responsavel) + " " + DateHelper.getDay(hcrPeriodo) + ApplicationResourcesHelper.getMessage("rotulo.relatorio.saldo.por.csa.ordinal.prep", responsavel) + " " + DateHelper.getMonthName(hcrPeriodo) + "/" + DateHelper.getYear(hcrPeriodo));
                innerSql.append("sum(case when ");
                int inicio = 0;
                for (final String periodoAnterior : periodosAnteriores) {
                    if (inicio == 0) {
                        innerSql.append("prd.prd_data_desconto in ('").append(periodoAnterior);
                        inicio++;
                    } else {
                        innerSql.append("','").append(periodoAnterior);
                    }
                }
                innerSql.append("')");

                Date dataAnterior = null;
                try {
                    dataAnterior = DateHelper.parse(periodosAnteriores.get(0), "yyyy-MM-dd");
                } catch (final ParseException e) {
                    throw new HQueryException("mensagem.erroInternoSistema", responsavel);
                }

                innerSql.append(" AND prd.spd_codigo = '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("' then prd.prd_vlr_realizado else 0 end) as REC_MENSUAL_").append(countRecMensual).append(",");
                resultSequence.append("sum(REC_MENSUAL_").append(countRecMensual).append(") as REC_MENSUAL_").append(countRecMensual).append(",");
                campos.add(ApplicationResourcesHelper.getMessage("rotulo.relatorio.saldo.por.csa.rec.mensal", responsavel) + "/" + DateHelper.getMonthName(dataAnterior) + "/" + DateHelper.getYear(dataAnterior));
                countRecMensual++;

                countMes++;
                countEst = 1;
                countPeriodos = 1;
                final ArrayList<String> totalOperandos = new ArrayList<>();
                for (final TransferObject est : estabelecimentos) {
                    final String estIdn = (String) est.getAttribute(Columns.EST_IDENTIFICADOR);
                    final String estNome = (String) (!TextHelper.isNull(est.getAttribute(Columns.EST_NOME_ABREV)) ? est.getAttribute(Columns.EST_NOME_ABREV) : est.getAttribute(Columns.EST_NOME));
                    innerSql.append("sum(case when ").append("est.est_identificador = '").append(estIdn).append("' AND prd.prd_data_desconto <= '").append(perFormatado).append("' and prd.spd_codigo = '");
                    innerSql.append(CodedValues.SPD_LIQUIDADAFOLHA).append("' then prd.prd_vlr_realizado else 0 end) as ").append("REALIZADO_Q_").append(countMes).append("_").append(countEst).append("_").append(countPeriodos).append(",");
                    final StringBuilder montaResult = new StringBuilder("coalesce(sum(TOTAL) - sum(REALIZADO_Q_").append(countMes).append("_").append(countEst).append("_").append(countPeriodos).append("), 0)");
                    final StringBuilder resultFinal = new StringBuilder(montaResult).append(" as DEVIDO_Q_").append(countMes).append("_").append(countEst).append("_").append(countPeriodos).append(",");
                    resultSequence.append(resultFinal.toString());
                    campos.add(estNome);
                    alias.add("DEVIDO_Q_" + countMes + "_" + countEst + "_" + countPeriodos);
                    totalOperandos.add(montaResult.toString());
                    countEst++;
                }
                StringBuilder montaTotal = null;
                for (final String operando: totalOperandos) {
                    if (montaTotal == null) {
                        montaTotal = new StringBuilder(operando);
                    } else {
                        montaTotal.append(" + ").append(operando);
                    }
                }
                resultSequence.append(montaTotal).append(" as TOTAL_").append(countMes).append("_").append(countPeriodos).append(",");
                alias.add("TOTAL_" + countMes + "_" + countPeriodos);
                campos.add(ApplicationResourcesHelper.getMessage("rotulo.relatorio.saldo.por.csa.total", responsavel));

                periodosAnteriores = new ArrayList<>();
                periodosAnteriores.add(perFormatado);
                mesAnterior = mes;
            } else {
                camposTotal.add(ApplicationResourcesHelper.getMessage("rotulo.relatorio.saldo.por.csa.total", responsavel) + " " + DateHelper.getDay(hcrPeriodo) + ApplicationResourcesHelper.getMessage("rotulo.relatorio.saldo.por.csa.ordinal.prep", responsavel) + " " + DateHelper.getMonthName(hcrPeriodo) + "/" + DateHelper.getYear(hcrPeriodo));
                countPeriodos++;
                countEst = 1;
                maxPeriodos = (countPeriodos > maxPeriodos) ? countPeriodos : maxPeriodos;
                final ArrayList<String> totalOperandos = new ArrayList<>();
                for (final TransferObject est : estabelecimentos) {
                    final String estIdn = (String) est.getAttribute(Columns.EST_IDENTIFICADOR);
                    final String estNome = (String) (!TextHelper.isNull(est.getAttribute(Columns.EST_NOME_ABREV)) ? est.getAttribute(Columns.EST_NOME_ABREV) : est.getAttribute(Columns.EST_NOME));
                    innerSql.append("sum(case when ").append("est.est_identificador = '").append(estIdn).append("' AND prd.prd_data_desconto <= '").append(perFormatado).append("' and prd.spd_codigo = '");
                    innerSql.append(CodedValues.SPD_LIQUIDADAFOLHA).append("' then prd.prd_vlr_realizado else 0 end) as ").append("REALIZADO_Q_").append(countMes).append("_").append(countEst).append("_").append(countPeriodos).append(",");
                    final StringBuilder montaResult = new StringBuilder("coalesce(sum(TOTAL) - sum(REALIZADO_Q_").append(countMes).append("_").append(countEst).append("_").append(countPeriodos).append("), 0)");
                    final StringBuilder resultFinal = new StringBuilder(montaResult).append(" as DEVIDO_Q_").append(countMes).append("_").append(countEst).append("_").append(countPeriodos).append(",");
                    resultSequence.append(resultFinal.toString());
                    campos.add(estNome);
                    alias.add("DEVIDO_Q_" + countMes + "_" + countEst + "_" + countPeriodos);
                    totalOperandos.add(montaResult.toString());
                    countEst++;
                }
                StringBuilder montaTotal = null;
                for (final String operando: totalOperandos) {
                    if (montaTotal == null) {
                        montaTotal = new StringBuilder(operando);
                    } else {
                        montaTotal.append(" + ").append(operando);
                    }
                }
                resultSequence.append(montaTotal).append(" as TOTAL_").append(countMes).append("_").append(countPeriodos).append(",");
                alias.add("TOTAL_" + countMes + "_" + countPeriodos);
                campos.add(ApplicationResourcesHelper.getMessage("rotulo.relatorio.saldo.por.csa.total", responsavel));

                periodosAnteriores.add(perFormatado);
            }
        }

        final String periodoBaseFormatado = DateHelper.format(periodoBase, "yyyy-MM-dd");
        final int mes = DateHelper.getMonth(periodoBase);
        final int dia = DateHelper.getDay(periodoBase);

        countEst = 1;
        camposSpan.add(dia + ApplicationResourcesHelper.getMessage("rotulo.relatorio.saldo.por.csa.ordinal.prep", responsavel) + " " + DateHelper.getMonthName(periodoBase) + " " + DateHelper.getYear(periodoBase));

        if ((mesAnterior == null) || (mesAnterior == mes)) {
            camposTotal.add(ApplicationResourcesHelper.getMessage("rotulo.relatorio.saldo.por.csa.total", responsavel) + " " + DateHelper.getDay(periodoBase) + ApplicationResourcesHelper.getMessage("rotulo.relatorio.saldo.por.csa.ordinal.prep", responsavel) + " " + DateHelper.getMonthName(periodoBase) + "/" + DateHelper.getYear(periodoBase));
            if (mesAnterior != null) {
                countPeriodos++;
            }
            maxPeriodos = (countPeriodos > maxPeriodos) ? countPeriodos : maxPeriodos;
            final ArrayList<String> totalOperandos = new ArrayList<>();
            for (final TransferObject est : estabelecimentos) {
                final String estIdn = (String) est.getAttribute(Columns.EST_IDENTIFICADOR);
                final String estNome = (String) (!TextHelper.isNull(est.getAttribute(Columns.EST_NOME_ABREV)) ? est.getAttribute(Columns.EST_NOME_ABREV) : est.getAttribute(Columns.EST_NOME));
                innerSql.append("sum(case when ").append("est.est_identificador = '").append(estIdn).append("' AND prd.prd_data_desconto <= '").append(periodoBaseFormatado).append("' and prd.spd_codigo = '");
                innerSql.append(CodedValues.SPD_LIQUIDADAFOLHA).append("' then prd.prd_vlr_realizado else 0 end) as ").append("REALIZADO_Q_").append(countMes).append("_").append(countEst).append("_").append(countPeriodos).append(",");
                final StringBuilder montaResult = new StringBuilder("coalesce(sum(TOTAL) - sum(REALIZADO_Q_").append(countMes).append("_").append(countEst).append("_").append(countPeriodos).append("), 0)");
                final StringBuilder resultFinal = new StringBuilder(montaResult).append(" as DEVIDO_Q_").append(countMes).append("_").append(countEst).append("_").append(countPeriodos).append(",");
                resultSequence.append(resultFinal.toString());
                campos.add(estNome);
                alias.add("DEVIDO_Q_" + countMes + "_" + countEst + "_" + countPeriodos);
                totalOperandos.add(montaResult.toString());
                countEst++;
            }
            StringBuilder montaTotal = null;
            for (final String operando: totalOperandos) {
                if (montaTotal == null) {
                    montaTotal = new StringBuilder(operando);
                } else {
                    montaTotal.append(" + ").append(operando);
                }
            }
            resultSequence.append(montaTotal).append(" as TOTAL_").append(countMes).append("_").append(countPeriodos).append(",");
            alias.add("TOTAL_" + countMes + "_" + countPeriodos);
            campos.add(ApplicationResourcesHelper.getMessage("rotulo.relatorio.saldo.por.csa.total", responsavel));

            periodosAnteriores.add(periodoBaseFormatado);

            innerSql.append("sum(case when ");
            int inicio = 0;
            if ((periodosAnteriores != null) && !periodosAnteriores.isEmpty()) {
                for (final String periodoAnterior : periodosAnteriores) {
                    if (inicio == 0) {
                        innerSql.append("prd.prd_data_desconto in ('").append(periodoAnterior);
                        inicio++;
                    } else {
                        innerSql.append("','").append(periodoAnterior);
                    }
                }
            } else {
                innerSql.append("prd.prd_data_desconto in ('").append(periodoBaseFormatado);
            }
            innerSql.append("')");

            innerSql.append(" AND prd.spd_codigo = '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("' then prd.prd_vlr_realizado else 0 end) as REC_MENSUAL_").append(countRecMensual);
            resultSequence.append("sum(REC_MENSUAL_").append(countRecMensual).append(") as REC_MENSUAL_").append(countRecMensual);
            campos.add(ApplicationResourcesHelper.getMessage("rotulo.relatorio.saldo.por.csa.rec.mensal", responsavel) + "/" + DateHelper.getMonthName(periodoBase) + "/" + DateHelper.getYear(periodoBase));
        } else {
            countMes++;
            camposTotal.add(ApplicationResourcesHelper.getMessage("rotulo.relatorio.saldo.por.csa.total", responsavel) + " " + DateHelper.getMonthName(periodoBase) + "/" + DateHelper.getYear(periodoBase));
            innerSql.append("sum(case when ");
            int inicio = 0;
            for (final String periodoAnterior : periodosAnteriores) {
                if (inicio == 0) {
                    innerSql.append("prd.prd_data_desconto in ('").append(periodoAnterior);
                    inicio++;
                } else {
                    innerSql.append("','").append(periodoAnterior);
                }
            }
            innerSql.append("')");

            Date dataAnterior = null;
            try {
                dataAnterior = DateHelper.parse(periodosAnteriores.get(0), "yyyy-MM-dd");
            } catch (final ParseException e) {
                throw new HQueryException("mensagem.erroInternoSistema", responsavel);
            }

            innerSql.append(" AND prd.spd_codigo = '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("' then prd.prd_vlr_realizado else 0 end) as REC_MENSUAL_").append(countRecMensual).append(",");
            resultSequence.append("sum(REC_MENSUAL_").append(countRecMensual).append(") as REC_MENSUAL_").append(countRecMensual).append(",");
            campos.add(ApplicationResourcesHelper.getMessage("rotulo.relatorio.saldo.por.csa.rec.mensal", responsavel) + "/" + DateHelper.getMonthName(dataAnterior) + "/" + DateHelper.getYear(dataAnterior));

            countEst = 1;
            countPeriodos = 1;
            countRecMensual++;
            maxPeriodos = (countPeriodos > maxPeriodos) ? countPeriodos : maxPeriodos;
            final ArrayList<String> totalOperandos = new ArrayList<>();
            for (final TransferObject est : estabelecimentos) {
                final String estIdn = (String) est.getAttribute(Columns.EST_IDENTIFICADOR);
                final String estNome = (String) (!TextHelper.isNull(est.getAttribute(Columns.EST_NOME_ABREV)) ? est.getAttribute(Columns.EST_NOME_ABREV) : est.getAttribute(Columns.EST_NOME));
                innerSql.append("sum(case when ").append("est.est_identificador = '").append(estIdn).append("' AND prd.prd_data_desconto <= '").append(periodoBaseFormatado).append("' and prd.spd_codigo = '");
                innerSql.append(CodedValues.SPD_LIQUIDADAFOLHA).append("' then prd.prd_vlr_realizado else 0 end) as ").append("REALIZADO_Q_").append(countMes).append("_").append(countEst).append("_").append(countPeriodos).append(",");
                final StringBuilder montaResult = new StringBuilder("coalesce(sum(TOTAL) - sum(REALIZADO_Q_").append(countMes).append("_").append(countEst).append("_").append(countPeriodos).append("), 0)");
                final StringBuilder resultFinal = new StringBuilder(montaResult).append(" as DEVIDO_Q_").append(countMes).append("_").append(countEst).append("_").append(countPeriodos).append(",");
                resultSequence.append(resultFinal.toString());
                campos.add(estNome);
                alias.add("DEVIDO_Q_" + countMes + "_" + countEst + "_" + countPeriodos);
                totalOperandos.add(montaResult.toString());
                countEst++;
            }
            StringBuilder montaTotal = null;
            for (final String operando: totalOperandos) {
                if (montaTotal == null) {
                    montaTotal = new StringBuilder(operando);
                } else {
                    montaTotal.append(" + ").append(operando);
                }
            }
            resultSequence.append(montaTotal).append(" as TOTAL_").append(countMes).append("_").append(countPeriodos).append(",");
            alias.add("TOTAL_" + countMes + "_" + countPeriodos);
            campos.add(ApplicationResourcesHelper.getMessage("rotulo.relatorio.saldo.por.csa.total", responsavel));

            innerSql.append("sum(case when ").append("prd.prd_data_desconto in ('").append(periodoBaseFormatado).append("')").append(" AND prd.spd_codigo = '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("' then prd.prd_vlr_realizado else 0 end) as REC_MENSUAL_").append(countRecMensual);
            resultSequence.append("sum(REC_MENSUAL_").append(countRecMensual).append(") as REC_MENSUAL_").append(countRecMensual);
            campos.add(ApplicationResourcesHelper.getMessage("rotulo.relatorio.saldo.por.csa.rec.mensal", responsavel) + "/" + DateHelper.getMonthName(periodoBase) + "/" + DateHelper.getYear(periodoBase));
        }

        sql.append(resultSequence).append(" FROM (").append(innerSql);
        sql.append(" FROM tb_parcela_desconto prd");
        sql.append(" INNER JOIN tb_aut_desconto ade ON (prd.ade_codigo = ade.ade_codigo)");
        sql.append(" INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo)");
        sql.append(" INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo)");
        sql.append(" INNER JOIN tb_consignataria csa ON (cnv.csa_codigo = csa.csa_codigo)");
        sql.append(" INNER JOIN tb_orgao org ON (cnv.org_codigo = org.org_codigo)");
        sql.append(" INNER JOIN tb_estabelecimento est ON (org.est_codigo = est.est_codigo)");
        sql.append(" WHERE 1 = 1 ");
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

        sql.append(" GROUP BY csa.csa_codigo, csa.csa_nome, csa.csa_identificador, ade.ade_codigo, ade.ade_vlr_ref, ade.ade_vlr, ade.ade_prazo_ref, ade.ade_prazo");
        sql.append(") X");
        sql.append(" GROUP BY  X.csa_nome, X.csa_identificador");

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

        this.countEst = estabelecimentos.size();
        this.countMes = countMes + 1;
        this.countPeriodos = maxPeriodos;

        return query;
    }

    @Override
    public String[] getFields() {
        return campos != null ? campos.toArray(new String[]{}) : null;
    }
}
