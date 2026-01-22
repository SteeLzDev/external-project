package com.zetra.econsig.persistence.query.relatorio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioComprometimentoQuery</p>
 * <p>Description: Hibernate Query para o relatório de comprometimento de margens.</p>
 * <p>Copyright: Copyright (c) 2002-2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioComprometimentoQuery extends ReportHNativeQuery {

    public AcessoSistema responsavel;
    private String estCodigo;
    private List<String> orgCodigos;
    private List<CustomTransferObject> servicos;
    private Short incideMargem;

    private List<String> sinalMargem;
    private List<String> comprometimentoMargem;
    private String percentualVariacaoMargemInicio;
    private String percentualVariacaoMargemFim;
    private Date penultimoPeriodoHisticoMargem;
    private boolean percentualVariacao = false;

    @Override
    public void setCriterios(TransferObject criterio) {
        estCodigo = (String) criterio.getAttribute(Columns.EST_CODIGO);
        orgCodigos = (List<String>) criterio.getAttribute(Columns.ORG_CODIGO);
        servicos = (List<CustomTransferObject>) criterio.getAttribute(Columns.CNV_SVC_CODIGO);
        incideMargem = criterio.getAttribute(CodedValues.TPS_INCIDE_MARGEM) != null ? (Short) criterio.getAttribute(CodedValues.TPS_INCIDE_MARGEM) : 0;

        sinalMargem = (List<String>) criterio.getAttribute("SINAL_MARGEM");
        comprometimentoMargem = (List<String>) criterio.getAttribute("COMPROMETIMENTO_MARGEM");
        percentualVariacaoMargemInicio = (String) criterio.getAttribute("PERCENTUAL_VARIACAO_MARGEM_INICIO");
        percentualVariacaoMargemFim = (String) criterio.getAttribute("PERCENTUAL_VARIACAO_MARGEM_FIM");
        penultimoPeriodoHisticoMargem = (Date) criterio.getAttribute("PENULTIMO_PERIODO");

        if ((responsavel == null) && (criterio.getAttribute("responsavel") != null)) {
            responsavel = (AcessoSistema) criterio.getAttribute("responsavel");
        }

        percentualVariacao = (!TextHelper.isNull(percentualVariacaoMargemInicio) || !TextHelper.isNull(percentualVariacaoMargemFim)) && !TextHelper.isNull(penultimoPeriodoHisticoMargem);
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final boolean margem1CasadaMargem3 = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3, CodedValues.TPC_SIM, responsavel);
        final boolean margem123Casadas = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_2_3_CASADAS, CodedValues.TPC_SIM, responsavel);
        final boolean margem1CasadaMargem3Esq = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_ESQUERDA, CodedValues.TPC_SIM, responsavel);
        final boolean margem123CasadasEsq = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_2_3_CASADAS_PELA_ESQUERDA, CodedValues.TPC_SIM, responsavel);

        final List<String> incidencias = new ArrayList<>();
        incidencias.add(incideMargem.toString());

        if (margem1CasadaMargem3) {
            if (incideMargem.equals(CodedValues.INCIDE_MARGEM_SIM)) {
                incidencias.add(CodedValues.INCIDE_MARGEM_SIM.toString());
                incidencias.add(CodedValues.INCIDE_MARGEM_SIM_3.toString());
            } else if (incideMargem.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
                incidencias.add(CodedValues.INCIDE_MARGEM_SIM_2.toString());
            } else if (incideMargem.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
                incidencias.add(CodedValues.INCIDE_MARGEM_SIM_3.toString());
            }
        } else if (margem123Casadas) {
            if (incideMargem.equals(CodedValues.INCIDE_MARGEM_SIM)) {
                incidencias.add(CodedValues.INCIDE_MARGEM_SIM.toString());
                incidencias.add(CodedValues.INCIDE_MARGEM_SIM_2.toString());
                incidencias.add(CodedValues.INCIDE_MARGEM_SIM_3.toString());
            } else if (incideMargem.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
                incidencias.add(CodedValues.INCIDE_MARGEM_SIM_2.toString());
                incidencias.add(CodedValues.INCIDE_MARGEM_SIM_3.toString());
            } else if (incideMargem.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
                incidencias.add(CodedValues.INCIDE_MARGEM_SIM_3.toString());
            }
        } else if (margem1CasadaMargem3Esq) {
            if (incideMargem.equals(CodedValues.INCIDE_MARGEM_SIM)) {
                incidencias.add(CodedValues.INCIDE_MARGEM_SIM.toString());
                incidencias.add(CodedValues.INCIDE_MARGEM_SIM_3.toString());
            } else if (incideMargem.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
                incidencias.add(CodedValues.INCIDE_MARGEM_SIM_2.toString());
            } else if (incideMargem.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
                incidencias.add(CodedValues.INCIDE_MARGEM_SIM.toString());
                incidencias.add(CodedValues.INCIDE_MARGEM_SIM_3.toString());
            }
        } else if (margem123CasadasEsq) {
            incidencias.add(CodedValues.INCIDE_MARGEM_SIM.toString());
            incidencias.add(CodedValues.INCIDE_MARGEM_SIM_2.toString());
            incidencias.add(CodedValues.INCIDE_MARGEM_SIM_3.toString());
        }

        final String colunaMargemFolha;
        final String colunaMargemRestante;
        if (incideMargem.equals(CodedValues.INCIDE_MARGEM_SIM)) {
            colunaMargemFolha = "rse_margem";
            colunaMargemRestante = "rse_margem_rest";
        } else if (incideMargem.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
            colunaMargemFolha = "rse_margem_2";
            colunaMargemRestante = "rse_margem_rest_2";
        } else if (incideMargem.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
            colunaMargemFolha = "rse_margem_3";
            colunaMargemRestante = "rse_margem_rest_3";
        } else {
            colunaMargemFolha = null;
            colunaMargemRestante = null;
        }

        final List<String> svcCodigos = new ArrayList<>();
        if ((servicos != null) && !servicos.isEmpty()) {
            for (final CustomTransferObject servico : servicos) {
                svcCodigos.add((String) servico.getAttribute(Columns.SVC_CODIGO));
            }
        }

        final StringBuilder corpoSinalMargem = new StringBuilder();
        if ((sinalMargem != null) && !sinalMargem.isEmpty() && (colunaMargemRestante != null)) {
            corpoSinalMargem.append(" AND ( 1 = 2 ");
            for (final String sinal : sinalMargem) {
                if ("1".equals(sinal)) {
                    corpoSinalMargem.append(" OR (").append(colunaMargemRestante).append(" > 0 ) ");
                }

                if ("0".equals(sinal)) {
                    corpoSinalMargem.append(" OR ( ").append(colunaMargemRestante).append(" = 0 ) ");
                }

                if ("-1".equals(sinal)) {
                    corpoSinalMargem.append(" OR ( ").append(colunaMargemRestante).append(" < 0 ) ");
                }
            }
            corpoSinalMargem.append(" ) ");
        }

        final StringBuilder sql = new StringBuilder();
        sql.append("SELECT FAIXA AS FAIXA, QTDE AS QTDE, MARGEM_USADA AS MARGEM_USADA, ");
        sql.append("MARGEM_TOTAL AS MARGEM_TOTAL, PERCENTUAL AS PERCENTUAL FROM (");
        sql.append("SELECT FAIXA AS FAIXA, (QTDE*1.00) AS QTDE, MARGEM_USADA AS MARGEM_USADA, ");
        sql.append("MARGEM_TOTAL AS MARGEM_TOTAL, ");
        sql.append("((QTDE*100.00)/(select count(rse_codigo) from tb_registro_servidor ");
        sql.append("where srs_codigo = '").append(CodedValues.SRS_ATIVO).append("' ").append(corpoSinalMargem).append(") ) AS PERCENTUAL ");
        sql.append("FROM ( ");
        sql.append("SELECT FAIXA, ");
        sql.append("sum(QTDE) AS QTDE, SUM(MARGEM_USADA) AS MARGEM_USADA, SUM(MARGEM_TOTAL) AS MARGEM_TOTAL ");
        sql.append("from ( ");
        sql.append("select ");
        sql.append("CASE ");
        sql.append("WHEN PERCENTUAL <= 0  THEN '  <= 0%' ");
        sql.append("WHEN PERCENTUAL <  20 THEN '  >0%/20%' ");
        sql.append("WHEN PERCENTUAL <= 25 THEN ' 20/25%' ");
        sql.append("WHEN PERCENTUAL <= 30 THEN ' 25/30%' ");
        sql.append("WHEN PERCENTUAL <= 35 THEN ' 30/35%' ");
        sql.append("WHEN PERCENTUAL <= 40 THEN ' 35/40%' ");
        sql.append("WHEN PERCENTUAL <= 45 THEN ' 40/45%' ");
        sql.append("WHEN PERCENTUAL <= 50 THEN ' 45/50%' ");
        sql.append("WHEN PERCENTUAL <= 55 THEN ' 50/55%' ");
        sql.append("WHEN PERCENTUAL <= 60 THEN ' 55/60%' ");
        sql.append("WHEN PERCENTUAL <= 65 THEN ' 60/65%' ");
        sql.append("WHEN PERCENTUAL <= 70 THEN ' 65/70%' ");
        sql.append("WHEN PERCENTUAL <= 75 THEN ' 70/75%' ");
        sql.append("WHEN PERCENTUAL <= 80 THEN ' 75/80%' ");
        sql.append("WHEN PERCENTUAL <= 85 THEN ' 80/85%' ");
        sql.append("WHEN PERCENTUAL <= 90 THEN ' 85/90%' ");
        sql.append("WHEN PERCENTUAL <= 95 THEN ' 90/95%' ");
        sql.append("WHEN PERCENTUAL <= 100 THEN ' 95/100%' ");
        sql.append("ELSE '> 100%' ");
        sql.append("END AS FAIXA,  ");
        sql.append("sum(QTDE) AS QTDE, SUM(MARGEM_USADA) AS MARGEM_USADA, SUM(MARGEM_TOTAL) AS MARGEM_TOTAL ");
        sql.append("from ( ");
        sql.append("select  ");
        sql.append("rse.rse_codigo, ");
        sql.append("count(distinct rse.rse_codigo) as QTDE, ");
        sql.append("coalesce(sum( ");

        if (!svcCodigos.isEmpty()) {
            sql.append("case when cnv.svc_codigo ").append(criaClausulaNomeada("svcCodigos", svcCodigos)).append(" then ");
        }
        sql.append("coalesce(nullif(ade_vlr_folha, 0), ade_vlr) ");
        if (!svcCodigos.isEmpty()) {
            sql.append("else 0 end ");
        }

        sql.append("), 0) as MARGEM_USADA, ");

        if (colunaMargemRestante != null) {
            sql.append("(coalesce(sum(coalesce(nullif(ade_vlr_folha, 0),  ade_vlr)), 0) + ");
            sql.append("coalesce(").append(colunaMargemRestante).append(",0)) as MARGEM_TOTAL, ");

            sql.append("CASE ");

            sql.append("WHEN (coalesce(sum(coalesce(nullif(ade_vlr_folha, 0),  ade_vlr)), 0) + ");
            sql.append("coalesce(").append(colunaMargemRestante).append(",0)) < 0 THEN 101 ");

            sql.append("WHEN (coalesce(sum(coalesce(nullif(ade_vlr_folha, 0),  ade_vlr)), 0) + ");
            sql.append("coalesce(").append(colunaMargemRestante).append(",0)) = 0 THEN 100 ");

            sql.append("ELSE ");
            sql.append("((coalesce(sum( ");

            if (!svcCodigos.isEmpty()) {
                sql.append("case when cnv.svc_codigo ").append(criaClausulaNomeada("svcCodigos", svcCodigos)).append(" then ");
            }
            sql.append("coalesce(nullif(ade_vlr_folha, 0), ade_vlr)  ");
            if (!svcCodigos.isEmpty()) {
                sql.append("else 0 end ");
            }

            sql.append("), 0)*100.00)/(coalesce(sum(coalesce(nullif(ade_vlr_folha, 0),  ade_vlr)), 0) + ");
            sql.append("coalesce(").append(colunaMargemRestante).append(",0))) ");
            sql.append("END as PERCENTUAL ");
        } else {
            sql.append("0 as MARGEM_TOTAL, ");
            sql.append("0 as PERCENTUAL ");
        }

        if (percentualVariacao && (penultimoPeriodoHisticoMargem != null) && (colunaMargemFolha != null)) {
            sql.append(", (rse."+colunaMargemFolha+"/(select hma.hma_margem_folha from tb_historico_margem_folha hma where rse.rse_codigo = hma.rse_codigo AND hma.hma_periodo = :penultimoPeriodoHisticoMargem AND hma.mar_codigo ='"+incideMargem+"')-1)*100 ").append(" as variacaoMargem ");
        }
        sql.append("from tb_registro_servidor rse ");
        sql.append("inner join tb_orgao org on (rse.org_codigo = org.org_codigo) ");
        sql.append("inner join tb_estabelecimento est on (est.est_codigo = org.est_codigo) ");
        sql.append("left outer join tb_aut_desconto ade on (ade.rse_codigo = rse.rse_codigo ");
        sql.append("and ade.sad_codigo not in ('");
        sql.append(TextHelper.join(CodedValues.SAD_CODIGOS_INATIVOS, "','")).append("')");
        sql.append(" and ade.ade_inc_margem ").append(criaClausulaNomeada("incidencias", incidencias));
        sql.append(") ");
        sql.append("left outer join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) ");
        sql.append("left outer join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo ");

        if (!svcCodigos.isEmpty()) {
            sql.append("and cnv.svc_codigo ").append(criaClausulaNomeada("svcCodigos", svcCodigos));
        }

        sql.append(") ");

        sql.append("where rse.srs_codigo = '").append(CodedValues.SRS_ATIVO).append("' ");
        sql.append(corpoSinalMargem);

        if (!TextHelper.isNull(estCodigo)) {
            sql.append(" and est.est_codigo ").append(criaClausulaNomeada("estCodigo", estCodigo));
        }
        if ((orgCodigos != null) && !orgCodigos.isEmpty()) {
            sql.append(" and org.org_codigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        sql.append(" group by rse.rse_codigo");
        if (colunaMargemRestante != null) {
            sql.append(", ").append(colunaMargemRestante);
        }

        sql.append(") X ");
        sql.append(" WHERE 1=1" );
        if ((comprometimentoMargem != null) && !comprometimentoMargem.isEmpty()) {
            sql.append(" AND ( 1 = 2 ");
            for (final String comprometimento : comprometimentoMargem) {
                switch (comprometimento) {
                    case CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_MENOR_ZERO:
                        sql.append(" OR (X.PERCENTUAL <= 0.00) ");
                        break;
                    case CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_0_A_10:
                        sql.append(" OR (X.PERCENTUAL > 0.00 ");
                        sql.append(" AND X.PERCENTUAL <= 10.00) ");
                        break;
                    case CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_10_A_20:
                        sql.append(" OR (X.PERCENTUAL > 10.00 ");
                        sql.append(" AND X.PERCENTUAL <= 20.00) ");
                        break;
                    case CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_20_A_30:
                        sql.append(" OR (X.PERCENTUAL > 20.00 ");
                        sql.append(" AND X.PERCENTUAL <= 30.00) ");
                        break;
                    case CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_30_A_40:
                        sql.append(" OR (X.PERCENTUAL > 30.00 ");
                        sql.append(" AND X.PERCENTUAL <= 40.00) ");
                        break;
                    case CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_40_A_50:
                        sql.append(" OR (X.PERCENTUAL > 40.00 ");
                        sql.append(" AND X.PERCENTUAL <= 50.00) ");
                        break;
                    case CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_50_A_60:
                        sql.append(" OR (X.PERCENTUAL > 50.00 ");
                        sql.append(" AND X.PERCENTUAL <= 60.00) ");
                        break;
                    case CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_60_A_70:
                        sql.append(" OR (X.PERCENTUAL > 60.00 ");
                        sql.append(" AND X.PERCENTUAL <= 70.00) ");
                        break;
                    case CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_70_A_80:
                        sql.append(" OR (X.PERCENTUAL > 70.00 ");
                        sql.append(" AND X.PERCENTUAL <= 80.00) ");
                        break;
                    case CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_80_A_90:
                        sql.append(" OR (X.PERCENTUAL > 80.00 ");
                        sql.append(" AND X.PERCENTUAL <= 90.00) ");
                        break;
                    case CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_90_A_100:
                        sql.append(" OR (X.PERCENTUAL > 90.00 ");
                        sql.append(" AND X.PERCENTUAL <= 100.00) ");
                        break;
                    case CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_MAIOR_CEM:
                        sql.append(" OR (X.PERCENTUAL > 100.00) ");
                        break;
                    default:
                        break;
                }
            }
            sql.append(" ) ");
        }

        if (percentualVariacao) {
            if (!TextHelper.isNull(percentualVariacaoMargemInicio) && !TextHelper.isNull(percentualVariacaoMargemFim)) {
                sql.append(" AND variacaoMargem  >= :percentualVariacaoMargemInicio AND variacaoMargem <= :percentualVariacaoMargemFim");
            } else if (!TextHelper.isNull(percentualVariacaoMargemInicio) && TextHelper.isNull(percentualVariacaoMargemFim)) {
                sql.append(" AND variacaoMargem  >= :percentualVariacaoMargemInicio");
            } else {
                sql.append(" AND variacaoMargem  <= :percentualVariacaoMargemFim");
            }
        }

        sql.append(" group by PERCENTUAL ");
        sql.append(") Y ");
        sql.append("group by FAIXA ");
        sql.append(") Z ");
        sql.append(") COMPROMETIMENTO ");
        sql.append("order by FAIXA ");

        final Query<Object[]> query = instanciarQuery(session, sql.toString());

        if (!TextHelper.isNull(estCodigo)) {
            defineValorClausulaNomeada("estCodigo", estCodigo, query);
        }
        if ((orgCodigos != null) && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }
        if (!svcCodigos.isEmpty()) {
            defineValorClausulaNomeada("svcCodigos", svcCodigos, query);
        }
        if (percentualVariacao) {
            if (!TextHelper.isNull(percentualVariacaoMargemInicio)) {
                defineValorClausulaNomeada("percentualVariacaoMargemInicio", percentualVariacaoMargemInicio, query);
            }
            if (!TextHelper.isNull(percentualVariacaoMargemFim)) {
                defineValorClausulaNomeada("percentualVariacaoMargemFim", percentualVariacaoMargemFim, query);
            }
            if (penultimoPeriodoHisticoMargem != null) {
                defineValorClausulaNomeada("penultimoPeriodoHisticoMargem", penultimoPeriodoHisticoMargem, query);
            }
        }
        defineValorClausulaNomeada("incidencias", incidencias, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[]{
                "FAIXA",
                "QTDE",
                "MARGEM_USADA",
                "MARGEM_TOTAL",
                "PERCENTUAL"
        };
    }
}
