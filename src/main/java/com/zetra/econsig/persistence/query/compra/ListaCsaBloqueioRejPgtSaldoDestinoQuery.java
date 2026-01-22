package com.zetra.econsig.persistence.query.compra;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusCompraEnum;

/**
 * <p>Title: ListaCsaBloqueioRejPgtSaldoDestinoQuery</p>
 * <p>Description: Listagem de Consignatárias para bloqueio em virtude da existência
 * de um rejeito de pagamento para um contrato comprado por ela.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCsaBloqueioRejPgtSaldoDestinoQuery extends HNativeQuery {

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
                        "CASE WHEN NULLIF(TRIM(psc78.PSC_VLR), '') IS NULL THEN " + Columns.CSA_EMAIL +
                            " ELSE psc78.PSC_VLR END AS EMAIL_AVISO_CSA, " +
                        "psc168.PSC_VLR AS DESTINATARIOS_EMAILS";

        StringBuilder corpoBuilder = new StringBuilder(campos);

        corpoBuilder.append(" FROM " + Columns.TB_RELACIONAMENTO_AUTORIZACAO);
        corpoBuilder.append(" INNER JOIN " + Columns.TB_AUTORIZACAO_DESCONTO + " adeDestino ON (adeDestino.ADE_CODIGO = " + Columns.RAD_ADE_CODIGO_DESTINO + ")");
        corpoBuilder.append(" INNER JOIN " + Columns.TB_AUTORIZACAO_DESCONTO + " adeOrigem ON (adeOrigem.ADE_CODIGO = " + Columns.RAD_ADE_CODIGO_ORIGEM + ")");
        corpoBuilder.append(" INNER JOIN " + Columns.TB_REGISTRO_SERVIDOR + " ON (" + Columns.RSE_CODIGO + " = adeDestino.RSE_CODIGO)");
        corpoBuilder.append(" INNER JOIN " + Columns.TB_SERVIDOR + " ON (" + Columns.SER_CODIGO + " = " + Columns.RSE_SER_CODIGO + ")");
        corpoBuilder.append(" INNER JOIN " + Columns.TB_VERBA_CONVENIO + " ON (" + Columns.VCO_CODIGO + " = adeDestino.VCO_CODIGO)");
        corpoBuilder.append(" INNER JOIN " + Columns.TB_CONVENIO + " ON (" + Columns.CNV_CODIGO + " = " + Columns.VCO_CNV_CODIGO + ")");
        corpoBuilder.append(" INNER JOIN " + Columns.TB_CONSIGNATARIA + " ON (" + Columns.CSA_CODIGO + " = " + Columns.CNV_CSA_CODIGO + ")");
        corpoBuilder.append(" INNER JOIN " + Columns.TB_VERBA_CONVENIO + " vcoOrigem ON (vcoOrigem.VCO_CODIGO = adeOrigem.VCO_CODIGO)");
        corpoBuilder.append(" INNER JOIN " + Columns.TB_CONVENIO + " cnvOrigem ON (cnvOrigem.CNV_CODIGO = vcoOrigem.CNV_CODIGO)");
        corpoBuilder.append(" INNER JOIN " + Columns.TB_SERVICO + " ON (" + Columns.SVC_CODIGO + " = " + Columns.CNV_SVC_CODIGO + ")");
        corpoBuilder.append(" INNER JOIN " + Columns.TB_PARAM_SVC_CONSIGNANTE + " pse195 ON (pse195.SVC_CODIGO = " + Columns.SVC_CODIGO + " AND pse195.TPS_CODIGO = '" + CodedValues.TPS_REJEICAO_PGT_SDV_BLOQUEIA_AMBAS_CSAS + "')");
        corpoBuilder.append(" LEFT OUTER JOIN " + Columns.TB_CORRESPONDENTE + " ON (" + Columns.COR_CODIGO + " = adeDestino.COR_CODIGO)");
        corpoBuilder.append(" LEFT OUTER JOIN " + Columns.TB_PARAM_SVC_CONSIGNATARIA + " psc78 ON (psc78.SVC_CODIGO = " + Columns.SVC_CODIGO + " AND psc78.CSA_CODIGO = " + Columns.CSA_CODIGO + " AND psc78.TPS_CODIGO = '" + CodedValues.TPS_EMAIL_INF_PGT_SALDO_DEVEDOR + "')");
        corpoBuilder.append(" LEFT OUTER JOIN " + Columns.TB_PARAM_SVC_CONSIGNATARIA + " psc168 ON (psc168.SVC_CODIGO = " + Columns.SVC_CODIGO + " AND psc168.CSA_CODIGO = " + Columns.CSA_CODIGO + " AND psc168.TPS_CODIGO = '" + CodedValues.TPS_DESTINATARIOS_EMAILS_CONTROLE_COMPRA + "')");
        corpoBuilder.append(" WHERE " + Columns.RAD_TNT_CODIGO + " = '" + CodedValues.TNT_CONTROLE_COMPRA + "'");
        corpoBuilder.append("   AND adeOrigem.SAD_CODIGO = '" + CodedValues.SAD_AGUARD_LIQUI_COMPRA + "'");
        corpoBuilder.append("   AND adeDestino.SAD_CODIGO = '" + CodedValues.SAD_AGUARD_CONF + "'");
        corpoBuilder.append("   AND coalesce(pse195.PSE_VLR, '0') = '1' ");
        corpoBuilder.append("   AND cnvOrigem.CSA_CODIGO <> " + Columns.CSA_CODIGO);

        // A compra ainda está aberta
        String[] stcCodigosFinalizados = {StatusCompraEnum.LIQUIDADO.getCodigo(), StatusCompraEnum.FINALIZADO.getCodigo(), StatusCompraEnum.CANCELADO.getCodigo()};
        corpoBuilder.append("   AND " + Columns.RAD_STC_CODIGO + " NOT IN ('" + TextHelper.join(stcCodigosFinalizados, "','") + "') ");

        // E que existe uma rejeição de pagamento de saldo devedor
        corpoBuilder.append(" AND EXISTS (");
        corpoBuilder.append(" SELECT 1");
        corpoBuilder.append(" FROM " + Columns.TB_OCORRENCIA_AUTORIZACAO);
        corpoBuilder.append(" WHERE " + Columns.OCA_ADE_CODIGO + " = adeOrigem.ADE_CODIGO");
        corpoBuilder.append("   AND " + Columns.OCA_TOC_CODIGO + " = '" + CodedValues.TOC_REJEICAO_PAGAMENTO_SALDO_DEVEDOR + "'");
        corpoBuilder.append("   AND " + Columns.OCA_DATA + " > " + Columns.RAD_DATA);
        corpoBuilder.append(")");

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
