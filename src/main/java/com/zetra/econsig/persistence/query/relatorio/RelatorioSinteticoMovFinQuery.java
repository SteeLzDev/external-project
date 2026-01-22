package com.zetra.econsig.persistence.query.relatorio;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.persistence.query.parcela.ObtemTotalParcelasPeriodoQuery;
import com.zetra.econsig.persistence.query.parcela.ObtemTotalParcelasQuery;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.values.CamposRelatorioSinteticoEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioSinteticoMovFinQuery</p>
 * <p>Description: Query de relatório Sintético de Mov. Financeiro</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioSinteticoMovFinQuery extends ReportHNativeQuery {
    public String tipoEntidade;
    public List<String> corCodigos;
    public String csaCodigo;
    public List<String> orgCodigos;
    public String sboCodigo;
    public String uniCodigo;
    public String periodo;
    public Date periodoIni;
    public Date periodoFim;
    public List<String> campos;
    public List<String> camposOrdem;
    public List<String> sadCodigo;
    public List<String> svcCodigos;
    public List<String> spdCodigos;
    public String[] fields;
    private List<String> srsCodigos;
    public String matricula;
    public String cpf;
    public Map<String,String> tipoOrdMap;
    public List<String> marCodigos;
    private boolean useParcelaPeriodo;
    private boolean useParcela;
    private boolean innerJoin = true;
    private boolean useTarifacao = false;

    /**
     * verifica se há parcelas para o período para incluir na busca do relatório
     * @param session
     * @throws HQueryException
     */
    private void setUsoPeriodo(Session session) throws HQueryException {
        ObtemTotalParcelasQuery totalParcelas = new ObtemTotalParcelasQuery();
        totalParcelas.periodo = periodo;
        totalParcelas.relatorio = true;
        int totalPrd = totalParcelas.executarContador(session);
        useParcela = (totalPrd > 0);

        ObtemTotalParcelasPeriodoQuery totalParcelasPer = new ObtemTotalParcelasPeriodoQuery();
        totalParcelasPer.periodo = periodo;
        int totalPdp = totalParcelasPer.executarContador(session);
        useParcelaPeriodo = (totalPdp > 0);

        boolean processamentoFerias = ParamSist.paramEquals(CodedValues.TPC_TEM_PROCESSAMENTO_FERIAS, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        boolean permiteLiquidarParcelaFutura = ParamSist.paramEquals(CodedValues.TPC_PERMITE_LIQUIDAR_PARCELA_FUTURA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

        if (processamentoFerias || permiteLiquidarParcelaFutura) {
            if (useParcelaPeriodo && useParcela) {
                // Se existe parcela nas duas tabelas, e o sistema possui processamento
                // de férias, utiliza ambas, fazendo "Left Join" pois as parcelas
                // existentes podem apenas ser de contratos que tiveram pagamento de férias.
                // Neste caso, não deixa usar o hint de indice, pois fará terá performance pior.
                indexHintEnabled = false;
            } else if (useParcelaPeriodo || useParcela) {
                // Se não existe parcela nas duas tabelas, e o sistema possui processamento
                // de férias, caso exista parcela em uma das tabelas, não faz inner join
                // pois as parcelas existentes podem ser apenas as de férias.
                // DESENV-14394 - Quando o sistema também permita liquidação de futura é necessário utilizar o left
                innerJoin = false;
            }

        } else if (useParcelaPeriodo && useParcela) {
            // Se existe parcela nas duas tabelas, verifica em qual tem mais parcelas, e
            // utiliza apenas uma das tabelas
            useParcela = (totalPrd >= totalPdp);
            useParcelaPeriodo = !useParcela;
        }

        useTarifacao = useParcela && spdCodigos != null && !spdCodigos.isEmpty() && (spdCodigos.contains(CodedValues.SPD_LIQUIDADAFOLHA) || spdCodigos.contains(CodedValues.SPD_LIQUIDADAMANUAL));
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        setUsoPeriodo(session);

        boolean temStatusRSE = (srsCodigos != null && !srsCodigos.isEmpty());

        String campoStatus = "";
        String campoNumParcelas = "";
        String campoTotalPrestacoes = "";
        String campoTotalPrevisto = "";

        // Imita comportamento do Relatorio de Movimento Financeiro quando nenhum status de parcela é selecionado
        if (spdCodigos == null || (spdCodigos != null && spdCodigos.isEmpty())) {
            if (spdCodigos == null) {
                spdCodigos = new ArrayList<>();
            }
            spdCodigos.add(CodedValues.SPD_AGUARD_PROCESSAMENTO);
            spdCodigos.add(CodedValues.SPD_EMABERTO);
            spdCodigos.add(CodedValues.SPD_EMPROCESSAMENTO);
            spdCodigos.add(CodedValues.SPD_LIQUIDADAFOLHA);
            spdCodigos.add(CodedValues.SPD_LIQUIDADAMANUAL);
            spdCodigos.add(CodedValues.SPD_REJEITADAFOLHA);
            spdCodigos.add(CodedValues.SPD_SEM_RETORNO);
        }

        if (useParcelaPeriodo && useParcela) {
            campoStatus = "concat(coalesce(coalesce(spd1.spd_descricao, spd2.spd_descricao), '"+ApplicationResourcesHelper.getMessage("rotulo.em.aberto", (AcessoSistema) null)+"'), text_to_string(coalesce(concat('/', text_to_string(coalesce(ocp1.ocp_obs, ocp2.ocp_obs))),'')))";
            if (spdCodigos != null && !spdCodigos.isEmpty() && spdCodigos.contains(CodedValues.SPD_EMABERTO)) {
                campoNumParcelas = "COUNT(ade.ade_codigo)";
            } else {
                campoNumParcelas = "COUNT(pdp.ade_codigo) + COUNT(prd.ade_codigo)";
            }
            campoTotalPrestacoes = "SUM(coalesce(pdp.prd_vlr_realizado, 0)) + " +
                    "SUM(coalesce(prd.prd_vlr_realizado, 0))";
            campoTotalPrevisto = "SUM(coalesce(coalesce(prd.prd_vlr_previsto,pdp.prd_vlr_previsto),ade.ade_vlr))";

        } else if (useParcela) {
            campoStatus = "concat(coalesce(spd2.spd_descricao, '"+ApplicationResourcesHelper.getMessage("rotulo.em.aberto", (AcessoSistema) null)+"'), text_to_string(coalesce(concat('/', text_to_string(ocp2.ocp_obs)), '')))";
            if (spdCodigos != null && !spdCodigos.isEmpty() && spdCodigos.contains(CodedValues.SPD_EMABERTO)) {
                campoNumParcelas = "COUNT(ade.ade_codigo)";
            } else {
                campoNumParcelas = "COUNT(prd.ade_codigo)";
            }
            campoTotalPrestacoes = "SUM(coalesce(prd.prd_vlr_realizado, 0))";
            campoTotalPrevisto = "SUM(coalesce(prd.prd_vlr_previsto,ade.ade_vlr))";

        } else if (useParcelaPeriodo) {
            campoStatus = "concat(coalesce(spd1.spd_descricao, '"+ApplicationResourcesHelper.getMessage("rotulo.em.aberto", (AcessoSistema) null)+"'), text_to_string(coalesce(concat('/', text_to_string(ocp1.ocp_obs)), '')))";
            if (spdCodigos != null && !spdCodigos.isEmpty() && spdCodigos.contains(CodedValues.SPD_EMABERTO)) {
                campoNumParcelas = "COUNT(ade.ade_codigo)";
            } else {
                campoNumParcelas = "COUNT(pdp.ade_codigo)";
            }
            campoTotalPrestacoes = "SUM(coalesce(pdp.prd_vlr_realizado, 0))";
            campoTotalPrevisto = "SUM(coalesce(pdp.prd_vlr_previsto,ade.ade_vlr))";
        } else {
            campoStatus = "'"+ApplicationResourcesHelper.getMessage("rotulo.em.aberto", (AcessoSistema) null)+"'";
            campoNumParcelas = "COUNT(*)";
            campoTotalPrestacoes = "cast(0.0 as decimal)";
            campoTotalPrevisto = "sum(ade.ade_vlr)";
        }

        StringBuilder corpoBuilder = new StringBuilder();

        List<String> camposQuery = new ArrayList<>();
        for (String key : campos) {
            CamposRelatorioSinteticoEnum camposEnum = CamposRelatorioSinteticoEnum.recuperaCampo(key);
            if (camposEnum != null) {
                camposQuery.add(camposEnum.getCampo());
            }
        }

        String sql = TextHelper.join(camposQuery, ",");
        String groupList = TextHelper.join(camposQuery, ",");

        List<String> camposOrderQuery = new ArrayList<>();
        for (String key : camposOrdem) {
            CamposRelatorioSinteticoEnum camposEnum = CamposRelatorioSinteticoEnum.recuperaCampo(key);
            if (camposEnum != null) {
                camposOrderQuery.add(camposEnum.getCampo());
            }
        }

        String order = TextHelper.join(camposOrderQuery, ",");

        // renomeia os campos do select
        sql = sql.replaceAll(Columns.EST_NOME, "est.est_nome as est_nome");
        sql = sql.replaceAll(Columns.CNV_COD_VERBA, "cnv.cnv_cod_verba as cnv_cod_verba");
        sql = sql.replaceAll(Columns.SVC_DESCRICAO, "svc.svc_descricao as svc_descricao");
        sql = sql.replaceAll(Columns.ORG_NOME, "org.org_nome as org_nome");
        sql = sql.replaceAll(Columns.SAD_DESCRICAO, "sad.sad_descricao as sad_descricao");
        sql = sql.replaceAll(Columns.CSA_NOME_ABREV, "csa.csa_nome_abrev as csa_nome_abrev");
        sql = sql.replaceAll(Columns.CSA_NOME, "csa.csa_nome as csa_nome");
        sql = sql.replaceAll(Columns.COR_NOME, "cor.cor_nome as cor_nome");
        sql = sql.replaceAll("MEDIA_QTD_PARCELAS", "AVG(coalesce(ade.ade_prazo,1)) as media_qtd_parcelas");
        sql = sql.replaceAll("VALOR_MEDIO_PARCELAS", "AVG(ade.ade_vlr) as valor_medio_parcelas");
        sql = sql.replaceAll("MEDIA_QNTD_PARCELAS_PAGAS", "AVG(ade.ade_prd_pagas) as media_qntd_parcelas_pagas");
        sql = sql.replaceAll("CAPITAL_DEVIDO", "SUM((case when ade.ade_prazo is null then 1 else ade.ade_prazo - coalesce(ade.ade_prd_pagas, 0) end) * coalesce(ade.ade_vlr_parcela_folha, ade.ade_vlr)) as capital_devido");
        sql = sql.replaceAll(Columns.ADE_DATA, "to_period(ade.ade_ano_mes_ini) as ade_ano_mes_ini, ade.ade_ano_mes_ini as data_ini");

        // renomeia os campos do group
        groupList = groupList.replaceAll(Columns.EST_NOME, "est.est_nome");
        groupList = groupList.replaceAll(Columns.CNV_COD_VERBA, "cnv.cnv_cod_verba");
        groupList = groupList.replaceAll(Columns.SVC_DESCRICAO, "svc.svc_descricao");
        groupList = groupList.replaceAll(Columns.ORG_NOME, "org.org_nome");
        groupList = groupList.replaceAll(Columns.SAD_DESCRICAO, "sad.sad_descricao");
        groupList = groupList.replaceAll(Columns.CSA_NOME_ABREV, "csa.csa_nome_abrev");
        groupList = groupList.replaceAll(Columns.CSA_NOME, "csa.csa_nome");
        groupList = groupList.replaceAll(Columns.COR_NOME, "cor.cor_nome");
        groupList = groupList.replaceAll(",MEDIA_QTD_PARCELAS", "");
        groupList = groupList.replaceAll(",VALOR_MEDIO_PARCELAS", "");
        groupList = groupList.replaceAll(",MEDIA_QNTD_PARCELAS_PAGAS", "");
        groupList = groupList.replaceAll(",CAPITAL_DEVIDO", "");
        groupList = groupList.replaceAll("MEDIA_QTD_PARCELAS", "");
        groupList = groupList.replaceAll("VALOR_MEDIO_PARCELAS", "");
        groupList = groupList.replaceAll("MEDIA_QNTD_PARCELAS_PAGAS", "");
        groupList = groupList.replaceAll("CAPITAL_DEVIDO", "");
        groupList = groupList.replaceAll(Columns.ADE_DATA, "to_period(ade.ade_ano_mes_ini), ade_ano_mes_ini");

        // renomeia os campos da ordenação
        order = order.replaceAll(Columns.EST_NOME, "est.est_nome");
        order = order.replaceAll(Columns.CNV_COD_VERBA, "cnv.cnv_cod_verba");
        order = order.replaceAll(Columns.SVC_DESCRICAO, "svc.svc_descricao");
        order = order.replaceAll(Columns.ORG_NOME, "org.org_nome");
        order = order.replaceAll(Columns.SAD_DESCRICAO, "sad.sad_descricao");
        order = order.replaceAll(Columns.CSA_NOME_ABREV, "csa.csa_nome_abrev");
        order = order.replaceAll(Columns.CSA_NOME, "csa.csa_nome");
        order = order.replaceAll(Columns.COR_NOME, "cor.cor_nome");
        order = order.replaceAll(Columns.ADE_DATA, "ade.ade_ano_mes_ini");
        order = order.replaceAll("MEDIA_QTD_PARCELAS", "media_qtd_parcelas");
        order = order.replaceAll("VALOR_MEDIO_PARCELAS", "valor_medio_parcelas");
        order = order.replaceAll("MEDIA_QNTD_PARCELAS_PAGAS", "media_qntd_parcelas_pagas");
        order = order.replaceAll("CAPITAL_DEVIDO", "capital_devido");

        if (!useParcelaPeriodo && !useParcela) {
            String[] orderTemp1 = TextHelper.split(order, ",");
            LinkedList<String> orderTemp2 = new LinkedList<>();
            for (String field: orderTemp1) {
                if (!field.split(" ")[0].trim().equals("STATUS")) {
                    orderTemp2.add(field);
                }
            }
            order = TextHelper.join(orderTemp2, ",");
        }

        if (useParcelaPeriodo || useParcela) {
            order = order.replaceAll("STATUS", campoStatus);
            order = order.replaceAll("NUM_PARCELAS", campoNumParcelas);
            order = order.replaceAll("TOTAL_PRESTACOES", campoTotalPrestacoes);
        }

        // criando a consulta.
        corpoBuilder.append("SELECT ").append(sql);
        if (!sql.equals("")) {
            corpoBuilder.append(",");
        }
        corpoBuilder.append(campoStatus + " as STATUS, ");

        if (useTarifacao) {
            // DESENV-17697 - Incluir valor de tarifação no relatório sintético.
            corpoBuilder.append("COUNT(DISTINCT prd.ade_codigo) AS NUM_PARCELAS, ");

            corpoBuilder.append("to_decimal(SUM(CASE ");
            corpoBuilder.append("    WHEN coalesce(ptc.tpt_codigo, '1') = 1 ");
            corpoBuilder.append("    THEN coalesce(prd.prd_vlr_realizado,  0) ");
            corpoBuilder.append("    ELSE 0 ");
            corpoBuilder.append("    END ");
            corpoBuilder.append("), 13, 2) AS TOTAL_PRESTACOES, ");

            corpoBuilder.append("to_decimal(SUM(CASE ");
            corpoBuilder.append("    WHEN coalesce(ptc.tpt_codigo, '1') = 1 ");
            corpoBuilder.append("    THEN coalesce(prd.prd_vlr_previsto, ade.ade_vlr) ");
            corpoBuilder.append("    ELSE 0 ");
            corpoBuilder.append("    END ");
            corpoBuilder.append("), 13, 2) AS TOTAL_PREVISTO, ");

            corpoBuilder.append("to_decimal(SUM(CASE ");
            corpoBuilder.append("    WHEN prd.spd_codigo IN ('").append(CodedValues.SPD_LIQUIDADAFOLHA).append("','").append(CodedValues.SPD_LIQUIDADAMANUAL).append("') ");
            corpoBuilder.append("    THEN ");
            corpoBuilder.append("       case ");
            corpoBuilder.append("         when coalesce(nullif(psc.psc_vlr_ref,  ''),  to_string(ptc.pcv_forma_calc)) = '1' ");
            corpoBuilder.append("         then coalesce(coalesce(to_decimal(nullif(psc.psc_vlr, ''), 13,2), ptc.pcv_vlr), 0) ");
            corpoBuilder.append("         else (coalesce(prd.prd_vlr_previsto, ade.ade_vlr) * coalesce(coalesce(to_decimal(nullif(psc.psc_vlr, ''), 13,2), ptc.pcv_vlr), 0)) / 100.00 ");
            corpoBuilder.append("       end ");
            corpoBuilder.append("    ELSE 0 END ");
            corpoBuilder.append("), 13, 2) AS VALOR_TARIFADO ");

        } else {
            corpoBuilder.append(campoNumParcelas +  " AS NUM_PARCELAS, ");
            corpoBuilder.append(campoTotalPrestacoes + " AS TOTAL_PRESTACOES, ");
            corpoBuilder.append(campoTotalPrevisto + " AS TOTAL_PREVISTO, ");
            corpoBuilder.append(" 0.00 AS VALOR_TARIFADO");
        }

        if (marCodigos != null && !marCodigos.isEmpty()) {
            corpoBuilder.append(", mar.mar_descricao as mar_descricao");
        }

        //      JOINS
        corpoBuilder.append(" FROM tb_aut_desconto ade");
        corpoBuilder.append(" INNER JOIN tb_status_autorizacao_desconto sad on (sad.sad_codigo = ade.SAD_CODIGO)");
        corpoBuilder.append(" INNER JOIN tb_verba_convenio vco on (vco.vco_codigo = ade.VCO_CODIGO)");
        corpoBuilder.append(" INNER JOIN tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
        corpoBuilder.append(" INNER JOIN tb_consignataria csa on (csa.csa_codigo = cnv.csa_codigo)");
        corpoBuilder.append(" INNER JOIN tb_orgao org on (cnv.org_codigo = org.ORG_CODIGO)");
        corpoBuilder.append(" INNER JOIN tb_estabelecimento est on (est.est_codigo = org.EST_CODIGO)");
        corpoBuilder.append(" INNER JOIN tb_servico svc on (svc.svc_codigo = cnv.svc_codigo)");
        corpoBuilder.append(" INNER JOIN tb_registro_servidor rse on (rse.rse_codigo = ade.RSE_CODIGO)");
        corpoBuilder.append(" INNER JOIN tb_status_registro_servidor srs on (srs.SRS_CODIGO = rse.SRS_CODIGO)");
        corpoBuilder.append(" INNER JOIN tb_servidor ser on (ser.ser_codigo = rse.ser_codigo)");

        if(marCodigos != null && !marCodigos.isEmpty()) {
            corpoBuilder.append(" INNER JOIN tb_margem mar on (mar.mar_codigo = ade.ade_inc_margem)");
        }

        if (!TextHelper.isNull(sboCodigo)) {
            corpoBuilder.append(" INNER JOIN tb_sub_orgao sbo on (rse.org_codigo = sbo.org_codigo)");
        }

        if (!TextHelper.isNull(uniCodigo)) {
            corpoBuilder.append(" INNER JOIN tb_unidade uni on (rse.uni_codigo = uni.uni_codigo)");
        }

        if (campos != null && campos.contains(CamposRelatorioSinteticoEnum.CAMPO_CORRESPONDENTE.getCodigo())) {
            corpoBuilder.append(" LEFT OUTER JOIN tb_correspondente cor on (ade.cor_codigo = cor.cor_codigo)");
        }

        if (useParcelaPeriodo && useParcela) {
            corpoBuilder.append(" LEFT OUTER JOIN tb_parcela_desconto prd on ( ade.ade_codigo = prd.ade_codigo and prd.prd_data_desconto ").append(criaClausulaNomeada("periodo", periodo));
            corpoBuilder.append(" and prd.spd_codigo in ('");
            corpoBuilder.append(CodedValues.SPD_REJEITADAFOLHA).append("','");
            corpoBuilder.append(CodedValues.SPD_LIQUIDADAFOLHA).append("','");
            corpoBuilder.append(CodedValues.SPD_LIQUIDADAMANUAL).append("'))");
            corpoBuilder.append(" LEFT OUTER JOIN tb_status_parcela_desconto spd2 on (spd2.spd_codigo = prd.spd_codigo)");
            corpoBuilder.append(" LEFT OUTER JOIN tb_ocorrencia_parcela ocp2 on (ocp2.prd_codigo = prd.prd_codigo)");

            corpoBuilder.append(" LEFT OUTER JOIN tb_parcela_desconto_periodo pdp on ( ade.ade_codigo = pdp.ade_codigo and pdp.prd_data_desconto ").append(criaClausulaNomeada("periodo", periodo));
            corpoBuilder.append(" and pdp.spd_codigo in ('");
            corpoBuilder.append(CodedValues.SPD_EMABERTO).append("','");
            corpoBuilder.append(CodedValues.SPD_EMPROCESSAMENTO).append("','");
            corpoBuilder.append(CodedValues.SPD_SEM_RETORNO).append("'))");
            corpoBuilder.append(" LEFT OUTER JOIN tb_status_parcela_desconto spd1 on (spd1.spd_codigo = pdp.spd_codigo)");
            corpoBuilder.append(" LEFT OUTER JOIN tb_ocorrencia_parcela ocp1 on (ocp1.prd_codigo = pdp.prd_codigo)");

        } else if (useParcela) {
            if (innerJoin) {
                corpoBuilder.append(" INNER JOIN tb_parcela_desconto prd on ( ade.ade_codigo = prd.ade_codigo and prd.prd_data_desconto ").append(criaClausulaNomeada("periodo", periodo));
                corpoBuilder.append(" and prd.spd_codigo in ('");
                corpoBuilder.append(CodedValues.SPD_REJEITADAFOLHA).append("','");
                corpoBuilder.append(CodedValues.SPD_LIQUIDADAFOLHA).append("','");
                corpoBuilder.append(CodedValues.SPD_LIQUIDADAMANUAL).append("'))");
                corpoBuilder.append(" INNER JOIN tb_status_parcela_desconto spd2 on (spd2.spd_codigo = prd.spd_codigo)");
                corpoBuilder.append(" INNER JOIN tb_ocorrencia_parcela ocp2 on (ocp2.prd_codigo = prd.prd_codigo)");
            } else {
                corpoBuilder.append(" LEFT OUTER JOIN tb_parcela_desconto prd on ( ade.ade_codigo = prd.ade_codigo and prd.prd_data_desconto ").append(criaClausulaNomeada("periodo", periodo));
                corpoBuilder.append(" and prd.spd_codigo in ('");
                corpoBuilder.append(CodedValues.SPD_REJEITADAFOLHA).append("','");
                corpoBuilder.append(CodedValues.SPD_LIQUIDADAFOLHA).append("','");
                corpoBuilder.append(CodedValues.SPD_LIQUIDADAMANUAL).append("'))");
                corpoBuilder.append(" LEFT OUTER JOIN tb_status_parcela_desconto spd2 on (spd2.spd_codigo = prd.spd_codigo)");
                corpoBuilder.append(" LEFT OUTER JOIN tb_ocorrencia_parcela ocp2 on (ocp2.prd_codigo = prd.prd_codigo)");
            }

        } else if (useParcelaPeriodo) {
            if (innerJoin) {
                corpoBuilder.append(" INNER JOIN tb_parcela_desconto_periodo pdp on ( ade.ade_codigo = pdp.ade_codigo and pdp.prd_data_desconto ").append(criaClausulaNomeada("periodo", periodo));
                corpoBuilder.append(" and pdp.spd_codigo in ('");
                corpoBuilder.append(CodedValues.SPD_EMABERTO).append("','");
                corpoBuilder.append(CodedValues.SPD_EMPROCESSAMENTO).append("','");
                corpoBuilder.append(CodedValues.SPD_SEM_RETORNO).append("'))");
                corpoBuilder.append(" INNER JOIN tb_status_parcela_desconto spd1 on (spd1.spd_codigo = pdp.spd_codigo)");
                corpoBuilder.append(" LEFT OUTER JOIN tb_ocorrencia_parcela ocp1 on (ocp1.prd_codigo = pdp.prd_codigo)");
            } else {
                corpoBuilder.append(" LEFT OUTER JOIN tb_parcela_desconto_periodo pdp on ( ade.ade_codigo = pdp.ade_codigo and pdp.prd_data_desconto ").append(criaClausulaNomeada("periodo", periodo));
                corpoBuilder.append(" and pdp.spd_codigo in ('");
                corpoBuilder.append(CodedValues.SPD_EMABERTO).append("','");
                corpoBuilder.append(CodedValues.SPD_EMPROCESSAMENTO).append("','");
                corpoBuilder.append(CodedValues.SPD_SEM_RETORNO).append("'))");
                corpoBuilder.append(" LEFT OUTER JOIN tb_status_parcela_desconto spd1 on (spd1.spd_codigo = pdp.spd_codigo)");
                corpoBuilder.append(" LEFT OUTER JOIN tb_ocorrencia_parcela ocp1 on (ocp1.prd_codigo = pdp.prd_codigo)");
            }
        }

        // DESENV-17697 - Incluir valor de tarifação no relatório sintético de movimento financeiro.
        if (useTarifacao) {
            corpoBuilder.append(" LEFT OUTER JOIN tb_param_svc_consignataria psc on (psc.tps_codigo = '").append(CodedValues.TPS_VLR_INTERVENIENCIA).append("'");
            corpoBuilder.append(" and csa.csa_codigo = psc.csa_codigo");
            corpoBuilder.append(" and svc.svc_codigo = psc.svc_codigo)");

            corpoBuilder.append(" LEFT OUTER JOIN tb_param_tarif_consignante ptc on (ptc.svc_codigo = cnv.svc_codigo");
            corpoBuilder.append(" and ptc.pcv_base_calc = '2'");
            corpoBuilder.append(" and ptc.pcv_vlr > 0.00)");
        }
        corpoBuilder.append(" WHERE 1=1");

        corpoBuilder.append(" AND (ade.ade_int_folha = '").append(CodedValues.INTEGRA_FOLHA_SIM).append("'");
        corpoBuilder.append(" or (ade.ade_int_folha = '").append(CodedValues.INTEGRA_FOLHA_SOMENTE_EXCLUSAO).append("'");
        if (useParcelaPeriodo && useParcela) {
            corpoBuilder.append(" and (prd.ade_codigo is not null or pdp.ade_codigo is not null)");
        } else if (useParcela) {
            corpoBuilder.append(" and prd.ade_codigo is not null");
        } else if (useParcelaPeriodo) {
            corpoBuilder.append(" and pdp.ade_codigo is not null");
        } else {
            corpoBuilder.append(" and 1=2");
        }
        corpoBuilder.append("))");

        corpoBuilder.append(" and ((sad.sad_codigo in ('");
        corpoBuilder.append(CodedValues.SAD_DEFERIDA).append("','");
        corpoBuilder.append(CodedValues.SAD_EMANDAMENTO).append("','");
        corpoBuilder.append(CodedValues.SAD_ESTOQUE).append("','");
        corpoBuilder.append(CodedValues.SAD_ESTOQUE_MENSAL).append("','");
        corpoBuilder.append(CodedValues.SAD_AGUARD_LIQUIDACAO).append("','");
        corpoBuilder.append(CodedValues.SAD_AGUARD_LIQUI_COMPRA).append("'");
        if (useParcelaPeriodo || useParcela) {
            corpoBuilder.append(",'").append(CodedValues.SAD_ESTOQUE_NAO_LIBERADO).append("'");
        }
        corpoBuilder.append(")) or (sad.sad_codigo in ('");
        corpoBuilder.append(CodedValues.SAD_SUSPENSA).append("','");
        corpoBuilder.append(CodedValues.SAD_INDEFERIDA).append("','");
        corpoBuilder.append(CodedValues.SAD_SUSPENSA_CSE).append("','");
        corpoBuilder.append(CodedValues.SAD_CANCELADA).append("','");
        corpoBuilder.append(CodedValues.SAD_LIQUIDADA).append("','");
        corpoBuilder.append(CodedValues.SAD_CONCLUIDO).append("','");
        corpoBuilder.append(CodedValues.SAD_ENCERRADO).append("','");
        corpoBuilder.append(CodedValues.SAD_EMCARENCIA).append("')");

        if (useParcelaPeriodo && useParcela) {
            corpoBuilder.append(" and (prd.ade_codigo is not null or pdp.ade_codigo is not null)))");
        } else if (useParcela) {
            corpoBuilder.append(" and prd.ade_codigo is not null))");
        } else if (useParcelaPeriodo) {
            corpoBuilder.append(" and pdp.ade_codigo is not null))");
        } else {
            corpoBuilder.append(" and 1=2))");
        }

        if (temStatusRSE) {
            corpoBuilder.append(" AND srs.srs_codigo").append(criaClausulaNomeada("srsCodigos", srsCodigos));
        }

        // Contratos do periodo informado, ou anteriores, ...
        corpoBuilder.append(" and (ade.ade_ano_mes_ini <= :periodo");

        // Ou contratos reimplantados que tem parcela criada no período solicitado.
        if (useParcelaPeriodo && useParcela) {
            corpoBuilder.append(" or prd.ade_codigo is not null or pdp.ade_codigo is not null)");
        } else if (useParcela) {
            corpoBuilder.append(" or prd.ade_codigo is not null)");
        } else if (useParcelaPeriodo) {
            corpoBuilder.append(" or pdp.ade_codigo is not null)");
        } else {
            corpoBuilder.append(")");
        }

        if (periodoIni != null && periodoFim != null) {
            corpoBuilder.append(" AND ade.ade_data BETWEEN :periodoIni AND :periodoFim ");
        } else if (periodoIni != null) {
            corpoBuilder.append(" AND ade.ade_data >= :periodoIni ");
        } else if (periodoFim != null) {
            corpoBuilder.append(" AND ade.ade_data <= :periodoFim ");
        }

        if ((corCodigos != null && !corCodigos.isEmpty()) && !corCodigos.contains("-1") && !corCodigos.contains("")) {
            corpoBuilder.append(" AND ade.cor_codigo ").append(criaClausulaNomeada("corCodigos", corCodigos));
        }
        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND csa.csa_codigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }
        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            corpoBuilder.append(" AND cnv.org_codigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }
        if (!TextHelper.isNull(sboCodigo)) {
            corpoBuilder.append(" and sbo.sbo_codigo ").append(criaClausulaNomeada("sboCodigo", sboCodigo));
        }
        if (!TextHelper.isNull(uniCodigo)) {
            corpoBuilder.append(" and uni.uni_codigo ").append(criaClausulaNomeada("uniCodigo", uniCodigo));
        }
        if (svcCodigos != null && !svcCodigos.isEmpty()) {
            corpoBuilder.append(" AND cnv.svc_codigo ").append(criaClausulaNomeada("svcCodigo", svcCodigos));
        }

        if (sadCodigo != null && !sadCodigo.isEmpty()) {
            corpoBuilder.append(" AND ade.sad_codigo ").append(criaClausulaNomeada("sadCodigo", sadCodigo));
        }

        if (marCodigos !=null && !marCodigos.isEmpty()) {
            corpoBuilder.append(" AND ade.ade_inc_margem ").append(criaClausulaNomeada("marCodigos", marCodigos));
        }

        if (spdCodigos != null && !spdCodigos.isEmpty()) {
            if (spdCodigos.contains(CodedValues.SPD_EMABERTO)) {
                // Se o usuário selecionou o status em aberto, faz uma cláusula para pegar contratos
                // que ainda não tem parcela para o período
                if (useParcelaPeriodo && useParcela) {
                    corpoBuilder.append(" and (spd2.spd_codigo ").append(criaClausulaNomeada("spdCodigos", spdCodigos));
                    corpoBuilder.append(" or spd1.spd_codigo ").append(criaClausulaNomeada("spdCodigos", spdCodigos));
                    corpoBuilder.append(" or (prd.ade_codigo is null and pdp.ade_codigo is null))");

                    // DESENV-15537: Quando o parâmetro de sistema de processamento de férias e parcela futura esta habilitado
                    // é feito um left na tabela de parcela e com isso pode ocorrer de contratos suspensos no periodo do relatório
                    // serem exibidos no relatório, por isso é necessário remover estes contratos suspensos.

                    corpoBuilder.append(" AND NOT EXISTS (");
                    corpoBuilder.append(" SELECT ocaSusp.ade_codigo FROM tb_ocorrencia_autorizacao ocaSusp");
                    corpoBuilder.append(" WHERE (ocaSusp.ade_codigo = ade.ade_codigo AND ocaSusp.toc_codigo = '").append(CodedValues.TOC_SUSPENSAO_CONTRATO).append("'");
                    corpoBuilder.append(" AND ocaSusp.oca_periodo <= :periodo)");
                    corpoBuilder.append(" AND NOT EXISTS (SELECT ocaReat.ade_codigo FROM tb_ocorrencia_autorizacao ocaReat WHERE ocaReat.ade_codigo = ocaSusp.ade_codigo");
                    corpoBuilder.append(" AND ocaReat.toc_codigo = '").append(CodedValues.TOC_REATIVACAO_CONTRATO).append("' and ocaReat.oca_data > ocaSusp.oca_data and ocaReat.oca_periodo <= :periodo)");
                    corpoBuilder.append(" AND NOT EXISTS (SELECT prd.ade_codigo FROM tb_parcela_desconto prd where prd.ade_codigo = ocaSusp.ade_codigo and prd.prd_data_desconto = :periodo)");
                    corpoBuilder.append(" )");
                } else if (useParcela) {
                    corpoBuilder.append(" and (spd2.spd_codigo ").append(criaClausulaNomeada("spdCodigos", spdCodigos));
                    corpoBuilder.append(" or prd.ade_codigo is null)");

                    // DESENV-15537: Quando o parâmetro de sistema de processamento de férias e parcela futura esta habilitado
                    // é feito um left na tabela de parcela e com isso pode ocorrer de contratos suspensos no periodo do relatório
                    // serem exibidos no relatório, por isso é necessário remover estes contratos suspensos.

                    corpoBuilder.append(" AND NOT EXISTS (");
                    corpoBuilder.append(" SELECT ocaSusp.ade_codigo FROM tb_ocorrencia_autorizacao ocaSusp");
                    corpoBuilder.append(" WHERE (ocaSusp.ade_codigo = ade.ade_codigo AND ocaSusp.toc_codigo = '").append(CodedValues.TOC_SUSPENSAO_CONTRATO).append("'");
                    corpoBuilder.append(" AND ocaSusp.oca_periodo <= :periodo)");
                    corpoBuilder.append(" AND NOT EXISTS (SELECT ocaReat.ade_codigo FROM tb_ocorrencia_autorizacao ocaReat WHERE ocaReat.ade_codigo = ocaSusp.ade_codigo ");
                    corpoBuilder.append(" AND ocaReat.toc_codigo = '").append(CodedValues.TOC_REATIVACAO_CONTRATO).append("' and ocaReat.oca_data > ocaSusp.oca_data and ocaReat.oca_periodo <= :periodo)");
                    corpoBuilder.append(" AND NOT EXISTS (SELECT prd.ade_codigo FROM tb_parcela_desconto prd where prd.ade_codigo = ocaSusp.ade_codigo and prd.prd_data_desconto = :periodo)");
                    corpoBuilder.append(" )");
                } else if (useParcelaPeriodo) {
                    corpoBuilder.append(" and (spd1.spd_codigo ").append(criaClausulaNomeada("spdCodigos", spdCodigos));
                    corpoBuilder.append(" or pdp.ade_codigo is null)");
                } else {
                    corpoBuilder.append(" and 1=1");
                }
            } else if (useParcelaPeriodo && useParcela) {
                corpoBuilder.append(" and (spd2.spd_codigo ").append(criaClausulaNomeada("spdCodigos", spdCodigos));
                corpoBuilder.append(" or spd1.spd_codigo ").append(criaClausulaNomeada("spdCodigos", spdCodigos)).append(")");
            } else if (useParcela) {
                corpoBuilder.append(" and spd2.spd_codigo ").append(criaClausulaNomeada("spdCodigos", spdCodigos));
            } else if (useParcelaPeriodo) {
                corpoBuilder.append(" and spd1.spd_codigo ").append(criaClausulaNomeada("spdCodigos", spdCodigos));
            } else {
                corpoBuilder.append(" and 1=2");
            }
        }

        if (useParcela && useParcelaPeriodo) {
            corpoBuilder.append(" and not exists (select ocp3.PRD_CODIGO ");
            corpoBuilder.append(" from tb_ocorrencia_parcela ocp3 ");
            corpoBuilder.append(" where prd.prd_codigo = ocp3.prd_codigo and ocp3.ocp_data>ocp2.ocp_data ");
            corpoBuilder.append(" )");

            corpoBuilder.append(" and not exists (select ocp3.PRD_CODIGO ");
            corpoBuilder.append(" from tb_ocorrencia_parcela ocp3");
            corpoBuilder.append(" where pdp.prd_codigo = ocp3.prd_codigo and ocp3.ocp_data>ocp1.ocp_data ");
            corpoBuilder.append(" )");
        } else if (useParcela) {
            corpoBuilder.append(" and not exists (select ocp3.PRD_CODIGO ");
            corpoBuilder.append(" from tb_ocorrencia_parcela ocp3 ");
            corpoBuilder.append(" where prd.prd_codigo = ocp3.prd_codigo and ocp3.ocp_data>ocp2.ocp_data ");
            corpoBuilder.append(" )");
        } else if (useParcelaPeriodo) {
            corpoBuilder.append(" and not exists (select ocp3.PRD_CODIGO ");
            corpoBuilder.append(" from tb_ocorrencia_parcela ocp3");
            corpoBuilder.append(" where pdp.prd_codigo = ocp3.prd_codigo and ocp3.ocp_data>ocp1.ocp_data ");
            corpoBuilder.append(" )");
        }

        if(!TextHelper.isNull(matricula)) {
            corpoBuilder.append(" and rse.rse_matricula ").append(criaClausulaNomeada("rseMatricula", matricula));
        }

        if(!TextHelper.isNull(cpf)) {
            corpoBuilder.append(" and ser.ser_cpf ").append(criaClausulaNomeada("serCpf", cpf));
        }


        if (!useParcelaPeriodo && !useParcela) {
            if (!sql.equals("") && !groupList.trim().equals("")) {
                corpoBuilder.append(" GROUP BY ").append(groupList);
            }
        } else {
            corpoBuilder.append(" GROUP BY ").append(campoStatus);
            if (!sql.equals("") && !groupList.trim().equals("")) {
                corpoBuilder.append(", ").append(groupList);
            }
        }

        corpoBuilder.append(" ORDER BY ").append(!TextHelper.isNull(order) ? order : "1");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(periodo)) {
            try {
                defineValorClausulaNomeada("periodo", DateHelper.parse(periodo, "yyyy-MM-dd"), query);
            } catch (ParseException ex) {
                throw new HQueryException("mensagem.erro.data.informada.invalida.arg0", (AcessoSistema) null, periodo);
            }
        }

        if (temStatusRSE) {
            defineValorClausulaNomeada("srsCodigos", srsCodigos, query);
        }

        if (periodoIni != null) {
            defineValorClausulaNomeada("periodoIni", periodoIni, query);
        }
        if (periodoFim != null) {
            defineValorClausulaNomeada("periodoFim", periodoFim, query);
        }

        if ((corCodigos != null && !corCodigos.isEmpty()) && !corCodigos.contains("-1") && !corCodigos.contains("")) {
            defineValorClausulaNomeada("corCodigos", corCodigos, query);
        }
        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }
        if (!TextHelper.isNull(sboCodigo)) {
            defineValorClausulaNomeada("sboCodigo", sboCodigo, query);
        }
        if (!TextHelper.isNull(uniCodigo)) {
            defineValorClausulaNomeada("uniCodigo", uniCodigo, query);
        }
        if (svcCodigos != null && !svcCodigos.isEmpty()) {
            defineValorClausulaNomeada("svcCodigo", svcCodigos, query);
        }

        if (sadCodigo != null && !sadCodigo.isEmpty()) {
            defineValorClausulaNomeada("sadCodigo", sadCodigo, query);
        }

        if (spdCodigos != null && !spdCodigos.isEmpty() && (useParcela || useParcelaPeriodo)) {
            defineValorClausulaNomeada("spdCodigos", spdCodigos, query);
        }

        if (!TextHelper.isNull(cpf)) {
            defineValorClausulaNomeada("serCpf", cpf, query);
        }

        if (!TextHelper.isNull(matricula)) {
            defineValorClausulaNomeada("rseMatricula", matricula, query);
        }

        if (marCodigos !=null && !marCodigos.isEmpty()) {
            defineValorClausulaNomeada("marCodigos", marCodigos, query);
        }

        List<String> _fields = new ArrayList<>();
        _fields.addAll(camposQuery);
        if (camposQuery.indexOf(Columns.ADE_DATA) != -1) {
            _fields.add(camposQuery.indexOf(Columns.ADE_DATA) + 1, "data_ini");
        }
        _fields.add("STATUS");
        _fields.add("NUM_PARCELAS");
        _fields.add("TOTAL_PRESTACOES");
        _fields.add("TOTAL_PREVISTO");
        _fields.add("VALOR_TARIFADO");
        if (marCodigos != null && !marCodigos.isEmpty()) {
            _fields.add("mar_descricao");
        }

        fields = _fields.toArray(new String[] {});

        return query;
    }


    @Override
    public void setCriterios(TransferObject criterio) {
        periodo = (String) criterio.getAttribute("PERIODO");
        corCodigos = (List<String>) criterio.getAttribute(Columns.COR_CODIGO);
        csaCodigo = (String) criterio.getAttribute("CSA_CODIGO");
        orgCodigos = (List<String>) criterio.getAttribute("ORG_CODIGO");
        sboCodigo = (String) criterio.getAttribute("SBO_CODIGO");
        uniCodigo = (String) criterio.getAttribute("UNI_CODIGO");
        tipoEntidade = (String) criterio.getAttribute("TIPO_ENTIDADE");
        campos = (List<String>) criterio.getAttribute("CAMPOS");
        svcCodigos = (List<String>) criterio.getAttribute("SVC_CODIGO");
        spdCodigos = (List<String>) criterio.getAttribute("SPD_CODIGO");
        sadCodigo = (List<String>) criterio.getAttribute("SAD_CODIGO");
        tipoOrdMap = (Map<String,String>) criterio.getAttribute("TIPO_ORD");
        srsCodigos = (List<String>) criterio.getAttribute(Columns.SRS_CODIGO);
        matricula = (String) criterio.getAttribute("RSE_MATRICULA");
        cpf = (String) criterio.getAttribute("CPF");

        if (criterio.getAttribute("ORDER") instanceof List<?> list) {
            camposOrdem = (List<String>) list;
        } else if (criterio.getAttribute("ORDER") instanceof String string) {
            camposOrdem = Arrays.asList(string.split("\s*,\s*"));
        }

        if (!TextHelper.isNull(criterio.getAttribute(ReportManager.PARAM_NAME_PERIODO_INICIO))) {
            try {
                periodoIni = DateHelper.parse((String) criterio.getAttribute(ReportManager.PARAM_NAME_PERIODO_INICIO), "yyyy-MM-dd");
            } catch (ParseException ex) {
                periodoIni = null;
            }
        }
        if (!TextHelper.isNull(criterio.getAttribute(ReportManager.PARAM_NAME_PERIODO_FIM))) {
            try {
                periodoFim  = DateHelper.parse((String) criterio.getAttribute(ReportManager.PARAM_NAME_PERIODO_FIM), "yyyy-MM-dd");
            } catch (ParseException ex) {
                periodoFim = null;
            }
        }
        marCodigos = (List<String>) criterio.getAttribute("MAR_CODIGOS");
    }

    @Override
    public String[] getFields() {
        return fields;
    }
}