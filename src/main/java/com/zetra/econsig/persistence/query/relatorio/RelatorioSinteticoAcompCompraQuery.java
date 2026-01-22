package com.zetra.econsig.persistence.query.relatorio;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusCompraEnum;

/**
 * <p>Title: RelatorioSinteticoAcompCompraQuery</p>
 * <p>Description: Relatório sintético de acompanhamento de compra de contrato.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioSinteticoAcompCompraQuery extends ReportHNativeQuery {

    public AcessoSistema responsavel;
    public String dataInicio;
    public String dataFim;
    public String csaCodigo;
    public List<String> orgCodigos;
    public List<String> svcCodigos;
    public boolean exibirSomenteVencidas = false;

    @Override
    public void setCriterios(TransferObject criterio) {
        dataInicio = (String) criterio.getAttribute("DATA_INI");
        dataFim = (String) criterio.getAttribute("DATA_FIM");
        csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
        orgCodigos = (List<String>) criterio.getAttribute(Columns.ORG_CODIGO);
        svcCodigos = (List<String>) criterio.getAttribute(Columns.SVC_CODIGO);

        if (!TextHelper.isNull(criterio.getAttribute("EXIBIR_SOMENTE_VENCIDAS"))) {
            exibirSomenteVencidas = Boolean.valueOf(criterio.getAttribute("EXIBIR_SOMENTE_VENCIDAS").toString());
        }
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        boolean usaDiasUteis = ParamSist.paramEquals(CodedValues.TPC_USA_DIAS_UTEIS_CONTROLE_COMPRA, CodedValues.TPC_SIM, responsavel);

        // Filtra pelo contrato comprador, nos status abaixo
        String[] sadCodigos = {CodedValues.SAD_AGUARD_CONF, CodedValues.SAD_AGUARD_DEFER, CodedValues.SAD_DEFERIDA};

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT CSA_CODIGO AS CSA_CODIGO, CSA_IDENTIFICADOR AS CSA_IDENTIFICADOR, CSA_NOME AS CSA_NOME, CONSIGNATARIA AS CONSIGNATARIA, ");
        sql.append("to_decimal(SUM(SALDO), 13, 2) AS SALDO, to_decimal(SUM(PAGAMENTO), 13, 2) AS PAGAMENTO, to_decimal(SUM(LIQUIDACAO), 13, 2) AS LIQUIDACAO FROM ( ");

        // Query para verificar Informacao de Saldo
        sql.append("SELECT adeOrigem.ADE_CODIGO, adeOrigem.ADE_NUMERO, ");
        sql.append("csaOrigem.CSA_CODIGO, csaOrigem.CSA_IDENTIFICADOR, ");
        sql.append("case when nullif(trim(csaOrigem.CSA_NOME_ABREV), '') is null then csaOrigem.CSA_NOME else csaOrigem.CSA_NOME_ABREV end AS CSA_NOME, ");
        sql.append("concatenar(concatenar(csaOrigem.CSA_IDENTIFICADOR, ' - '), case when nullif(trim(csaOrigem.CSA_NOME_ABREV), '') is null then csaOrigem.CSA_NOME else csaOrigem.CSA_NOME_ABREV end) AS CONSIGNATARIA,");
        sql.append("1 AS SALDO, 0 AS PAGAMENTO, 0 LIQUIDACAO ");
        sql.append("FROM tb_aut_desconto adeOrigem ");
        sql.append("INNER JOIN tb_verba_convenio vcoOrigem ON (adeOrigem.VCO_CODIGO=vcoOrigem.VCO_CODIGO) ");
        sql.append("INNER JOIN tb_convenio cnvOrigem ON (vcoOrigem.CNV_CODIGO=cnvOrigem.CNV_CODIGO) ");
        sql.append("INNER JOIN tb_consignataria csaOrigem ON (cnvOrigem.CSA_CODIGO=csaOrigem.CSA_CODIGO) ");
        sql.append("INNER JOIN tb_relacionamento_autorizacao rad ON (adeOrigem.ADE_CODIGO=rad.ADE_CODIGO_ORIGEM) ");
        sql.append("INNER JOIN tb_aut_desconto adeDestino ON (rad.ADE_CODIGO_DESTINO=adeDestino.ADE_CODIGO) ");
        sql.append("INNER JOIN tb_verba_convenio vcoDestino ON (adeDestino.VCO_CODIGO=vcoDestino.VCO_CODIGO) ");
        sql.append("INNER JOIN tb_convenio cnvDestino ON (vcoDestino.CNV_CODIGO=cnvDestino.CNV_CODIGO) ");
        sql.append("INNER JOIN tb_consignataria csaDestino ON (cnvDestino.CSA_CODIGO=csaDestino.CSA_CODIGO) ");

        if (exibirSomenteVencidas) {
            sql.append("INNER JOIN tb_param_svc_consignante pse ON (cnvOrigem.SVC_CODIGO = pse.SVC_CODIGO ");
            sql.append("AND pse.TPS_CODIGO = '").append(CodedValues.TPS_DIAS_INF_SALDO_DV_CONTROLE_COMPRA).append("') ");
        }

        sql.append(" WHERE 1=1 ");
        sql.append(" AND adeDestino.SAD_CODIGO IN ('").append(TextHelper.join(sadCodigos, "','")).append("')");
        sql.append(" AND adeOrigem.SAD_CODIGO = '").append(CodedValues.SAD_AGUARD_LIQUI_COMPRA).append("'");
        sql.append(" AND rad.TNT_CODIGO = '").append(CodedValues.TNT_CONTROLE_COMPRA).append("' ");
        sql.append(" AND rad.STC_CODIGO = '").append(StatusCompraEnum.AGUARDANDO_INF_SALDO.getCodigo()).append("' ");

        if (!TextHelper.isNull(dataInicio)) {
            sql.append(" AND rad.RAD_DATA >= :dataInicio ");
        }
        if (!TextHelper.isNull(dataFim)) {
            sql.append(" AND rad.RAD_DATA <= :dataFim ");
        }

        sql.append(" AND rad.RAD_DATA_INF_SALDO IS NULL ");
        if (usaDiasUteis) {
            sql.append(" AND (SELECT COUNT(*) FROM tb_calendario cal WHERE cal.cal_dia_util = 'S' AND cal.cal_data between to_date(rad.RAD_DATA_REF_INF_SALDO) and data_corrente()) > ");
        } else {
            sql.append(" AND to_days(data_corrente()) - to_days(rad.RAD_DATA_REF_INF_SALDO) >= ");
        }
        sql.append(exibirSomenteVencidas ? " to_numeric_ne(pse.PSE_VLR) " : " 0 ");

        if (!TextHelper.isNull(csaCodigo)) {
            sql.append(" AND csaOrigem.CSA_CODIGO ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            sql.append(" AND cnvOrigem.ORG_CODIGO ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        if (svcCodigos != null && !svcCodigos.isEmpty()) {
            sql.append(" AND cnvOrigem.SVC_CODIGO ").append(criaClausulaNomeada("svcCodigos", svcCodigos));
        }

        sql.append(" GROUP BY adeOrigem.ADE_CODIGO , adeOrigem.ADE_NUMERO, ");
        sql.append("csaOrigem.CSA_CODIGO, csaOrigem.CSA_IDENTIFICADOR, ");
        sql.append("case when nullif(trim(csaOrigem.CSA_NOME_ABREV), '') is null then csaOrigem.CSA_NOME else csaOrigem.CSA_NOME_ABREV end, ");
        sql.append("concatenar(concatenar(csaOrigem.CSA_IDENTIFICADOR, ' - '), case when nullif(trim(csaOrigem.CSA_NOME_ABREV), '') is null then csaOrigem.CSA_NOME else csaOrigem.CSA_NOME_ABREV end),");
        sql.append("rad.RAD_DATA ");

        sql.append("UNION ALL ");

        // Query para verificar Pagamento de Saldo
        sql.append("SELECT  adeOrigem.ADE_CODIGO, adeOrigem.ADE_NUMERO, ");
        sql.append("csaDestino.CSA_CODIGO, csaDestino.CSA_IDENTIFICADOR, ");
        sql.append("case when nullif(trim(csaDestino.CSA_NOME_ABREV), '') is null then csaDestino.CSA_NOME else csaDestino.CSA_NOME_ABREV end AS CSA_NOME, ");
        sql.append("concatenar(concatenar(csaDestino.CSA_IDENTIFICADOR, ' - '), case when nullif(trim(csaDestino.CSA_NOME_ABREV), '') is null then csaDestino.CSA_NOME else csaDestino.CSA_NOME_ABREV end) AS CONSIGNATARIA,");
        sql.append("0 AS SALDO, 1 AS PAGAMENTO, 0 LIQUIDACAO ");
        sql.append("FROM tb_aut_desconto adeOrigem ");
        sql.append("INNER JOIN tb_verba_convenio vcoOrigem ON (adeOrigem.VCO_CODIGO=vcoOrigem.VCO_CODIGO) ");
        sql.append("INNER JOIN tb_convenio cnvOrigem ON (vcoOrigem.CNV_CODIGO=cnvOrigem.CNV_CODIGO) ");
        sql.append("INNER JOIN tb_consignataria csaOrigem ON (cnvOrigem.CSA_CODIGO=csaOrigem.CSA_CODIGO) ");
        sql.append("INNER JOIN tb_relacionamento_autorizacao rad ON (adeOrigem.ADE_CODIGO=rad.ADE_CODIGO_ORIGEM) ");
        sql.append("INNER JOIN tb_aut_desconto adeDestino ON (rad.ADE_CODIGO_DESTINO=adeDestino.ADE_CODIGO) ");
        sql.append("INNER JOIN tb_verba_convenio vcoDestino ON (adeDestino.VCO_CODIGO=vcoDestino.VCO_CODIGO) ");
        sql.append("INNER JOIN tb_convenio cnvDestino ON (vcoDestino.CNV_CODIGO=cnvDestino.CNV_CODIGO) ");
        sql.append("INNER JOIN tb_consignataria csaDestino ON (cnvDestino.CSA_CODIGO=csaDestino.CSA_CODIGO) ");

        if (exibirSomenteVencidas) {
            sql.append("INNER JOIN tb_param_svc_consignante pse ON (cnvDestino.SVC_CODIGO = pse.SVC_CODIGO ");
            sql.append("AND pse.TPS_CODIGO = '").append(CodedValues.TPS_DIAS_INF_PGT_SALDO_CONTROLE_COMPRA).append("') ");
        }

        sql.append(" WHERE 1=1 ");
        sql.append(" AND adeDestino.SAD_CODIGO IN ('").append(TextHelper.join(sadCodigos, "','")).append("')");
        sql.append(" AND adeOrigem.SAD_CODIGO = '").append(CodedValues.SAD_AGUARD_LIQUI_COMPRA).append("'");
        sql.append(" AND rad.TNT_CODIGO = '").append(CodedValues.TNT_CONTROLE_COMPRA).append("' ");
        sql.append(" AND rad.STC_CODIGO = '").append(StatusCompraEnum.AGUARDANDO_PAG_SALDO.getCodigo()).append("' ");

        if (!TextHelper.isNull(dataInicio)) {
            sql.append(" AND rad.RAD_DATA >= :dataInicio ");
        }

        if (!TextHelper.isNull(dataFim)) {
            sql.append(" AND rad.RAD_DATA <= :dataFim ");
        }

        sql.append(" AND rad.RAD_DATA_INF_SALDO IS NOT NULL ");
        sql.append(" AND rad.RAD_DATA_PGT_SALDO IS NULL ");
        if (usaDiasUteis) {
            sql.append(" AND (SELECT COUNT(*) FROM tb_calendario cal WHERE cal.cal_dia_util = 'S' AND cal.cal_data between to_date(rad.RAD_DATA_REF_PGT_SALDO) and data_corrente()) > ");
        } else {
            sql.append(" AND to_days(data_corrente()) - to_days(rad.RAD_DATA_REF_PGT_SALDO) >= ");
        }
        sql.append(exibirSomenteVencidas ? " to_numeric_ne(pse.PSE_VLR) " : " 0 ");

        if (!TextHelper.isNull(csaCodigo)) {
            sql.append(" AND csaDestino.CSA_CODIGO ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            sql.append(" AND cnvDestino.ORG_CODIGO ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        if (svcCodigos != null && !svcCodigos.isEmpty()) {
            sql.append(" AND cnvDestino.SVC_CODIGO ").append(criaClausulaNomeada("svcCodigos", svcCodigos));
        }

        sql.append(" GROUP BY adeOrigem.ADE_CODIGO , adeOrigem.ADE_NUMERO, ");
        sql.append("csaDestino.CSA_CODIGO, csaDestino.CSA_IDENTIFICADOR, ");
        sql.append("case when nullif(trim(csaDestino.CSA_NOME_ABREV), '') is null then csaDestino.CSA_NOME else csaDestino.CSA_NOME_ABREV end, ");
        sql.append("concatenar(concatenar(csaDestino.CSA_IDENTIFICADOR, ' - '), case when nullif(trim(csaDestino.CSA_NOME_ABREV), '') is null then csaDestino.CSA_NOME else csaDestino.CSA_NOME_ABREV end),");
        sql.append("rad.RAD_DATA_INF_SALDO, rad.RAD_DATA ");

        sql.append("UNION ALL ");

        // Query para verificar Liquidacao da compra
        sql.append("SELECT  adeOrigem.ADE_CODIGO, adeOrigem.ADE_NUMERO, ");
        sql.append("csaOrigem.CSA_CODIGO, csaOrigem.CSA_IDENTIFICADOR, ");
        sql.append("case when nullif(trim(csaOrigem.CSA_NOME_ABREV), '') is null then csaOrigem.CSA_NOME else csaOrigem.CSA_NOME_ABREV end AS CSA_NOME, ");
        sql.append("concatenar(concatenar(csaOrigem.CSA_IDENTIFICADOR, ' - '), case when nullif(trim(csaOrigem.CSA_NOME_ABREV), '') is null then csaOrigem.CSA_NOME else csaOrigem.CSA_NOME_ABREV end) AS CONSIGNATARIA,");
        sql.append("0 AS SALDO, 0 AS PAGAMENTO, 1 LIQUIDACAO ");
        sql.append("FROM tb_aut_desconto adeOrigem ");
        sql.append("INNER JOIN tb_verba_convenio vcoOrigem ON (adeOrigem.VCO_CODIGO=vcoOrigem.VCO_CODIGO) ");
        sql.append("INNER JOIN tb_convenio cnvOrigem ON (vcoOrigem.CNV_CODIGO=cnvOrigem.CNV_CODIGO) ");
        sql.append("INNER JOIN tb_consignataria csaOrigem ON (cnvOrigem.CSA_CODIGO=csaOrigem.CSA_CODIGO) ");
        sql.append("INNER JOIN tb_relacionamento_autorizacao rad ON (adeOrigem.ADE_CODIGO=rad.ADE_CODIGO_ORIGEM) ");
        sql.append("INNER JOIN tb_aut_desconto adeDestino ON (rad.ADE_CODIGO_DESTINO=adeDestino.ADE_CODIGO) ");
        sql.append("INNER JOIN tb_verba_convenio vcoDestino ON (adeDestino.VCO_CODIGO=vcoDestino.VCO_CODIGO) ");
        sql.append("INNER JOIN tb_convenio cnvDestino ON (vcoDestino.CNV_CODIGO=cnvDestino.CNV_CODIGO) ");
        sql.append("INNER JOIN tb_consignataria csaDestino ON (cnvDestino.CSA_CODIGO=csaDestino.CSA_CODIGO) ");

        if (exibirSomenteVencidas) {
            sql.append("INNER JOIN tb_param_svc_consignante pse ON (cnvOrigem.SVC_CODIGO = pse.SVC_CODIGO ");
            sql.append("AND pse.TPS_CODIGO = '").append(CodedValues.TPS_DIAS_LIQUIDACAO_ADE_CONTROLE_COMPRA).append("') ");
        }

        sql.append(" WHERE 1=1 ");
        sql.append(" AND adeDestino.SAD_CODIGO IN ('").append(TextHelper.join(sadCodigos, "','")).append("')");
        sql.append(" AND adeOrigem.SAD_CODIGO = '").append(CodedValues.SAD_AGUARD_LIQUI_COMPRA).append("'");
        sql.append(" AND rad.TNT_CODIGO = '").append(CodedValues.TNT_CONTROLE_COMPRA).append("' ");
        sql.append(" AND rad.STC_CODIGO = '").append(StatusCompraEnum.AGUARDANDO_LIQUIDACAO.getCodigo()).append("' ");

        if (!TextHelper.isNull(dataInicio)) {
            sql.append(" AND rad.RAD_DATA >= :dataInicio ");
        }

        if (!TextHelper.isNull(dataFim)) {
            sql.append(" AND rad.RAD_DATA <= :dataFim ");
        }

        sql.append(" AND rad.RAD_DATA_INF_SALDO IS NOT NULL ");
        sql.append(" AND rad.RAD_DATA_PGT_SALDO IS NOT NULL ");
        sql.append(" AND rad.RAD_DATA_LIQUIDACAO IS NULL ");
        if (usaDiasUteis) {
            sql.append(" AND (SELECT COUNT(*) FROM tb_calendario cal WHERE cal.cal_dia_util = 'S' AND cal.cal_data between to_date(rad.RAD_DATA_REF_LIQUIDACAO) and data_corrente()) > ");
        } else {
            sql.append(" AND to_days(data_corrente()) - to_days(rad.RAD_DATA_REF_LIQUIDACAO) >= ");
        }
        sql.append(exibirSomenteVencidas ? " to_numeric_ne(pse.PSE_VLR) " : " 0 ");

        if (!TextHelper.isNull(csaCodigo)) {
            sql.append(" AND csaOrigem.CSA_CODIGO ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            sql.append(" AND cnvOrigem.ORG_CODIGO ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        if (svcCodigos != null && !svcCodigos.isEmpty()) {
            sql.append(" AND cnvOrigem.SVC_CODIGO ").append(criaClausulaNomeada("svcCodigos", svcCodigos));
        }

        sql.append(" GROUP BY adeOrigem.ADE_CODIGO , adeOrigem.ADE_NUMERO, ");
        sql.append("csaOrigem.CSA_CODIGO, csaOrigem.CSA_IDENTIFICADOR, ");
        sql.append("case when nullif(trim(csaOrigem.CSA_NOME_ABREV), '') is null then csaOrigem.CSA_NOME else csaOrigem.CSA_NOME_ABREV end, ");
        sql.append("concatenar(concatenar(csaOrigem.CSA_IDENTIFICADOR, ' - '), case when nullif(trim(csaOrigem.CSA_NOME_ABREV), '') is null then csaOrigem.CSA_NOME else csaOrigem.CSA_NOME_ABREV end),");
        sql.append("rad.RAD_DATA_INF_SALDO, rad.ADE_CODIGO_DESTINO, rad.RAD_DATA ");

        sql.append(") PENDENCIA ");

        sql.append(" GROUP BY CSA_CODIGO, CSA_IDENTIFICADOR, CSA_NOME, CONSIGNATARIA ");


        Query<Object[]> query = instanciarQuery(session, sql.toString());

        if (!TextHelper.isNull(dataInicio)) {
            defineValorClausulaNomeada("dataInicio", parseDateTimeString(dataInicio), query);
        }

        if (!TextHelper.isNull(dataFim)) {
            defineValorClausulaNomeada("dataFim", parseDateTimeString(dataFim), query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }

        if (svcCodigos != null && !svcCodigos.isEmpty()) {
            defineValorClausulaNomeada("svcCodigos", svcCodigos, query);
        }

        return query;
    }
}
