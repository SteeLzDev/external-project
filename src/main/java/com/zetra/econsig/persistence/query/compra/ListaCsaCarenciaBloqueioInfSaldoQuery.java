package com.zetra.econsig.persistence.query.compra;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCsaCarenciaBloqueioInfSaldoQuery</p>
 * <p>Description: Listagem de Consignatárias com carência para bloqueio em virtude do atraso
 * na informação do saldo devedor de contratos envolvidos em processo de compra.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCsaCarenciaBloqueioInfSaldoQuery extends HNativeQuery {

    public String csaCodigo;

    @Override
    public void setCriterios(TransferObject criterio) {
        csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String campos = "SELECT " +
                        Columns.CSA_CODIGO + "," +
                        Columns.CSA_NOME_ABREV + "," +
                        Columns.CSA_NOME + "," +
                        "adeOrigem.ADE_NUMERO" + "," +
                        Columns.SVC_DESCRICAO + "," +
                        "adeOrigem.ADE_VLR" + "," +
                        Columns.SER_NOME + "," +
                        Columns.SER_CPF + "," +
                        "adeOrigem.ADE_IDENTIFICADOR" + "," +
                        "adeOrigem.ADE_PRAZO" + "," +
                        "adeOrigem.ADE_PRD_PAGAS" + "," +
                        Columns.CNV_COD_VERBA + "," +
                        "adeOrigem.ADE_INDICE" + "," +
                        Columns.SVC_IDENTIFICADOR + "," +
                        Columns.RSE_MATRICULA + "," +
                        Columns.COR_EMAIL + "," +
                        "CASE WHEN NULLIF(TRIM(psc76.PSC_VLR), '') IS NULL THEN " + Columns.CSA_EMAIL +
                            " ELSE psc76.PSC_VLR END AS EMAIL_AVISO_CSA, " +
                        "psc168.PSC_VLR AS DESTINATARIOS_EMAILS";

        StringBuilder corpoBuilder = new StringBuilder(campos);

        corpoBuilder.append(" FROM " + Columns.TB_RELACIONAMENTO_AUTORIZACAO);
        corpoBuilder.append(" INNER JOIN " + Columns.TB_AUTORIZACAO_DESCONTO + " adeDestino ON (adeDestino.ADE_CODIGO = " + Columns.RAD_ADE_CODIGO_DESTINO + ")");
        corpoBuilder.append(" INNER JOIN " + Columns.TB_AUTORIZACAO_DESCONTO + " adeOrigem ON (adeOrigem.ADE_CODIGO = " + Columns.RAD_ADE_CODIGO_ORIGEM + ")");
        corpoBuilder.append(" INNER JOIN " + Columns.TB_REGISTRO_SERVIDOR + " ON (" + Columns.RSE_CODIGO + " = adeOrigem.RSE_CODIGO)");
        corpoBuilder.append(" INNER JOIN " + Columns.TB_SERVIDOR + " ON (" + Columns.SER_CODIGO + " = " + Columns.RSE_SER_CODIGO + ")");
        corpoBuilder.append(" INNER JOIN " + Columns.TB_VERBA_CONVENIO + " ON (" + Columns.VCO_CODIGO + " = adeOrigem.VCO_CODIGO)");
        corpoBuilder.append(" INNER JOIN " + Columns.TB_CONVENIO + " ON (" + Columns.CNV_CODIGO + " = " + Columns.VCO_CNV_CODIGO + ")");
        corpoBuilder.append(" INNER JOIN " + Columns.TB_CONSIGNATARIA + " ON (" + Columns.CSA_CODIGO + " = " + Columns.CNV_CSA_CODIGO + ")");
        corpoBuilder.append(" INNER JOIN " + Columns.TB_VERBA_CONVENIO + " vcoDestino ON (vcoDestino.VCO_CODIGO = adeDestino.VCO_CODIGO)");
        corpoBuilder.append(" INNER JOIN " + Columns.TB_CONVENIO + " cnvDestino ON (cnvDestino.CNV_CODIGO = vcoDestino.CNV_CODIGO)");
        corpoBuilder.append(" INNER JOIN " + Columns.TB_SERVICO + " ON (" + Columns.SVC_CODIGO + " = " + Columns.CNV_SVC_CODIGO + ")");
        corpoBuilder.append(" INNER JOIN " + Columns.TB_PARAM_SVC_CONSIGNANTE + " pse149 ON (pse149.SVC_CODIGO = " + Columns.SVC_CODIGO + " AND pse149.TPS_CODIGO = '" + CodedValues.TPS_DIAS_INF_SALDO_DV_CONTROLE_COMPRA + "')");
        corpoBuilder.append(" INNER JOIN " + Columns.TB_PARAM_SVC_CONSIGNANTE + " pse152 ON (pse152.SVC_CODIGO = " + Columns.SVC_CODIGO + " AND pse152.TPS_CODIGO = '" + CodedValues.TPS_ACAO_PARA_NAO_INF_SALDO_DV + "')");
        corpoBuilder.append(" LEFT OUTER JOIN " + Columns.TB_CORRESPONDENTE + " ON (" + Columns.COR_CODIGO + " = adeOrigem.COR_CODIGO)");
        corpoBuilder.append(" LEFT OUTER JOIN " + Columns.TB_PARAM_SVC_CONSIGNATARIA + " psc76 ON (psc76.SVC_CODIGO = " + Columns.SVC_CODIGO + " AND psc76.CSA_CODIGO = " + Columns.CSA_CODIGO + " AND psc76.TPS_CODIGO = '" + CodedValues.TPS_EMAIL_INF_CONTRATOS_COMPRADOS + "')");
        corpoBuilder.append(" LEFT OUTER JOIN " + Columns.TB_PARAM_SVC_CONSIGNATARIA + " psc168 ON (psc168.SVC_CODIGO = " + Columns.SVC_CODIGO + " AND psc168.CSA_CODIGO = " + Columns.CSA_CODIGO + " AND psc168.TPS_CODIGO = '" + CodedValues.TPS_DESTINATARIOS_EMAILS_CONTROLE_COMPRA + "')");
        corpoBuilder.append(" WHERE " + Columns.RAD_TNT_CODIGO + " = '" + CodedValues.TNT_CONTROLE_COMPRA + "'");
        corpoBuilder.append("   AND adeOrigem.SAD_CODIGO in ('" + CodedValues.SAD_AGUARD_LIQUI_COMPRA + "','" + CodedValues.SAD_LIQUIDADA + "')");
        corpoBuilder.append("   AND adeDestino.SAD_CODIGO in ('" + CodedValues.SAD_AGUARD_CONF + "','" + CodedValues.SAD_AGUARD_DEFER + "','" + CodedValues.SAD_DEFERIDA + "')");
        corpoBuilder.append("   AND NULLIF(TRIM(pse149.PSE_VLR), '') IS NOT NULL");
        corpoBuilder.append("   AND coalesce(pse152.PSE_VLR, '0') = '1' ");
        corpoBuilder.append("   AND coalesce(nullif(pse152.PSE_VLR_REF, ''), '0') <> '0' ");
        corpoBuilder.append("   AND cnvDestino.CSA_CODIGO <> " + Columns.CSA_CODIGO);

        // Prazo para informação de saldo foi excedido
        if (ParamSist.paramEquals(CodedValues.TPC_USA_DIAS_UTEIS_CONTROLE_COMPRA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            // OBS: O operador aqui deve ser ">" apenas, pois o cálculo do between entre duas datas retorna um valor maior que
            // a subtração dos dias entre as duas datas.
            corpoBuilder.append(" AND (SELECT COUNT(1) FROM " + Columns.TB_CALENDARIO + " WHERE " + Columns.CAL_DIA_UTIL + " = 'S' AND " + Columns.CAL_DATA + " BETWEEN to_date(" + Columns.RAD_DATA_REF_INF_SALDO + ") AND to_date(" + Columns.RAD_DATA_INF_SALDO + ")) > ");
        } else {
            corpoBuilder.append(" AND (to_days(" + Columns.RAD_DATA_INF_SALDO + ") - to_days(" + Columns.RAD_DATA_REF_INF_SALDO + ")) >= ");
        }
        corpoBuilder.append("     (CASE isnumeric(pse149.PSE_VLR) WHEN 1 THEN to_numeric(COALESCE(NULLIF(TRIM(pse149.PSE_VLR), ''), '0')) ELSE 99999 END) ");

        // A carência para desbloqueio ainda não passou
        if (ParamSist.paramEquals(CodedValues.TPC_USA_DIAS_UTEIS_CONTROLE_COMPRA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            // OBS: O operador aqui deve ser "<=", pois o cálculo do between entre duas datas retorna um valor maior que
            // a subtração dos dias entre as duas datas.
            corpoBuilder.append(" AND (SELECT COUNT(1) FROM " + Columns.TB_CALENDARIO + " WHERE " + Columns.CAL_DIA_UTIL + " = 'S' AND " + Columns.CAL_DATA + " BETWEEN to_date(" + Columns.RAD_DATA_INF_SALDO + ") AND data_corrente()) <= ");
        } else {
            corpoBuilder.append(" AND (to_days(data_corrente()) - to_days(" + Columns.RAD_DATA_INF_SALDO + ")) < ");
        }
        corpoBuilder.append("     (CASE isnumeric(pse152.PSE_VLR_REF) WHEN 1 THEN to_numeric(COALESCE(NULLIF(TRIM(pse152.PSE_VLR_REF), ''), '0')) ELSE 0 END) ");

        // A data de referência e a data de informação de saldo estão preenchidas
        corpoBuilder.append("   AND " + Columns.RAD_DATA_REF_INF_SALDO + " IS NOT NULL AND " + Columns.RAD_DATA_INF_SALDO + " IS NOT NULL ");

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append("   AND " + Columns.CSA_CODIGO).append(criaClausulaNomeada("csaCodigo", csaCodigo));
        } else {
            corpoBuilder.append("   AND " + Columns.CSA_ATIVO + " = '").append(CodedValues.STS_ATIVO).append("'");
        }
        corpoBuilder.append(" ORDER BY " + Columns.CSA_CODIGO + ",");
        corpoBuilder.append(" adeOrigem.ADE_NUMERO + 0");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CSA_CODIGO,
                Columns.CSA_NOME_ABREV,
                Columns.CSA_NOME,
                Columns.ADE_NUMERO,
                Columns.SVC_DESCRICAO,
                Columns.ADE_VLR,
                Columns.SER_NOME,
                Columns.SER_CPF,
                Columns.ADE_IDENTIFICADOR,
                Columns.ADE_PRAZO,
                Columns.ADE_PRD_PAGAS,
                Columns.CNV_COD_VERBA,
                Columns.ADE_INDICE,
                Columns.SVC_IDENTIFICADOR,
                Columns.RSE_MATRICULA,
                Columns.COR_EMAIL,
                "EMAIL_AVISO_CSA",
                "DESTINATARIOS_EMAILS"
         };
    }
}
