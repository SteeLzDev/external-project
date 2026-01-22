package com.zetra.econsig.persistence.query.relatorio;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioContratoLiquidadoPosCorteQuery</p>
 * <p>Description: Listagem de contratos de liquidados após o corte e antes da importação do retorno.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioContratoLiquidadoPosCorteQuery extends ReportHNativeQuery {

    public String periodo;
    public String csaCodigo;
    public String corCodigo;
    public List<String>orgCodigos;
    public List<String> svcCodigos;
    public boolean ordenaPorCsa = true;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select ");
        // Consignatária/Correspondente
        corpoBuilder.append(Columns.CSA_CODIGO).append(" as ").append(Columns.getColumnName(Columns.CSA_CODIGO)).append(MySqlDAOFactory.SEPARADOR);
        corpoBuilder.append(Columns.CSA_IDENTIFICADOR).append(" as ").append(Columns.getColumnName(Columns.CSA_IDENTIFICADOR)).append(MySqlDAOFactory.SEPARADOR);
        corpoBuilder.append(Columns.CSA_NOME).append(" as ").append(Columns.getColumnName(Columns.CSA_NOME)).append(MySqlDAOFactory.SEPARADOR);
        corpoBuilder.append("concatenar(concatenar(").append(Columns.CSA_IDENTIFICADOR).append(", ' - '), case when nullif(trim(").append(Columns.CSA_NOME_ABREV).append("), '') is null then ").append(Columns.CSA_NOME).append(" else ").append(Columns.CSA_NOME_ABREV).append(" end) AS CONSIGNATARIA, ");

        corpoBuilder.append(Columns.COR_CODIGO).append(" as ").append(Columns.getColumnName(Columns.COR_CODIGO)).append(MySqlDAOFactory.SEPARADOR);
        corpoBuilder.append(Columns.COR_IDENTIFICADOR).append(" as ").append(Columns.getColumnName(Columns.COR_IDENTIFICADOR)).append(MySqlDAOFactory.SEPARADOR);
        corpoBuilder.append(Columns.COR_NOME).append(" as ").append(Columns.getColumnName(Columns.COR_NOME)).append(MySqlDAOFactory.SEPARADOR);
        corpoBuilder.append("concatenar(concatenar(").append(Columns.COR_IDENTIFICADOR).append(", ' - '), ").append(Columns.COR_NOME).append(") AS CORRESPONDENTE, ");

        // Órgão
        corpoBuilder.append(Columns.ORG_CODIGO).append(" as ").append(Columns.getColumnName(Columns.ORG_CODIGO)).append(MySqlDAOFactory.SEPARADOR);
        corpoBuilder.append(Columns.ORG_IDENTIFICADOR).append(" as ").append(Columns.getColumnName(Columns.ORG_IDENTIFICADOR)).append(MySqlDAOFactory.SEPARADOR);
        corpoBuilder.append(Columns.ORG_NOME).append(" as ").append(Columns.getColumnName(Columns.ORG_NOME)).append(MySqlDAOFactory.SEPARADOR);
        corpoBuilder.append("concatenar(concatenar(").append(Columns.ORG_IDENTIFICADOR).append(", ' - '), ").append(Columns.ORG_NOME).append(") AS ORGAO, ");

        // Serviço/Verba
        corpoBuilder.append(Columns.CNV_COD_VERBA).append(" as ").append(Columns.getColumnName(Columns.CNV_COD_VERBA)).append(MySqlDAOFactory.SEPARADOR);
        corpoBuilder.append(Columns.SVC_IDENTIFICADOR).append(" as ").append(Columns.getColumnName(Columns.SVC_IDENTIFICADOR)).append(MySqlDAOFactory.SEPARADOR);
        corpoBuilder.append(Columns.SVC_DESCRICAO).append(" as ").append(Columns.getColumnName(Columns.SVC_DESCRICAO)).append(MySqlDAOFactory.SEPARADOR);
        corpoBuilder.append("concatenar(concatenar(concatenar(case when nullif(trim(").append(Columns.CNV_COD_VERBA).append("), '') is not null then ").append(Columns.CNV_COD_VERBA).append(" else ").append(Columns.SVC_IDENTIFICADOR).append(" end, case when nullif(trim(").append(Columns.ADE_INDICE).append("), '') is not null then ").append(Columns.ADE_INDICE).append(" else '' end), ' - '), ").append(Columns.SVC_DESCRICAO).append(") AS SERVICO, ");

        // Servidor/Matrícula/CPF
        corpoBuilder.append(Columns.RSE_MATRICULA).append(" as ").append(Columns.getColumnName(Columns.RSE_MATRICULA)).append(MySqlDAOFactory.SEPARADOR);
        corpoBuilder.append(Columns.SER_NOME).append(" as ").append(Columns.getColumnName(Columns.SER_NOME)).append(MySqlDAOFactory.SEPARADOR);
        corpoBuilder.append(Columns.SER_CPF).append(" as ").append(Columns.getColumnName(Columns.SER_CPF)).append(MySqlDAOFactory.SEPARADOR);
        corpoBuilder.append("concat(").append(Columns.RSE_MATRICULA).append(", ' - ', ").append(Columns.SER_NOME).append(") as SERVIDOR, ");

        // ADE N.
        corpoBuilder.append("to_string(").append(Columns.ADE_NUMERO).append(") as ").append(Columns.getColumnName(Columns.ADE_NUMERO)).append(MySqlDAOFactory.SEPARADOR);

        // ADE Índice
        corpoBuilder.append(Columns.ADE_INDICE).append(" as ").append(Columns.getColumnName(Columns.ADE_INDICE)).append(MySqlDAOFactory.SEPARADOR);

        // Valor Previsto / Valor Pago
        corpoBuilder.append(Columns.PRD_VLR_PREVISTO).append(" as ").append(Columns.getColumnName(Columns.PRD_VLR_PREVISTO)).append(MySqlDAOFactory.SEPARADOR);
        corpoBuilder.append(Columns.PRD_VLR_REALIZADO).append(" as ").append(Columns.getColumnName(Columns.PRD_VLR_REALIZADO)).append(MySqlDAOFactory.SEPARADOR);

        // Prazo / Pagas
        corpoBuilder.append(Columns.ADE_PRAZO).append(" as ").append(Columns.getColumnName(Columns.ADE_PRAZO)).append(MySqlDAOFactory.SEPARADOR);
        corpoBuilder.append("to_decimal(coalesce(").append(Columns.ADE_PRD_PAGAS).append(", 0), 9, 0) as ").append(Columns.getColumnName(Columns.ADE_PRD_PAGAS)).append(MySqlDAOFactory.SEPARADOR);

        // Data Liquidação
        corpoBuilder.append(Columns.OCA_DATA).append(" as ").append(Columns.getColumnName(Columns.OCA_DATA)).append(MySqlDAOFactory.SEPARADOR);

        if (ordenaPorCsa) {
            corpoBuilder.append(Columns.CSA_CODIGO);
        } else {
            corpoBuilder.append(Columns.ORG_CODIGO);
        }
        corpoBuilder.append(" as ORDEM ");

        corpoBuilder.append("from ").append(Columns.TB_AUTORIZACAO_DESCONTO).append(" ");
        corpoBuilder.append("inner join ").append(Columns.TB_REGISTRO_SERVIDOR).append(" on (").append(Columns.ADE_RSE_CODIGO).append(" = ").append(Columns.RSE_CODIGO).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_SERVIDOR).append(" on (").append(Columns.RSE_SER_CODIGO).append(" = ").append(Columns.SER_CODIGO).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_VERBA_CONVENIO).append(" on (").append(Columns.ADE_VCO_CODIGO).append(" = ").append(Columns.VCO_CODIGO).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_CONVENIO).append(" on (").append(Columns.CNV_CODIGO).append(" = ").append(Columns.VCO_CNV_CODIGO).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_ORGAO).append(" on (").append(Columns.CNV_ORG_CODIGO).append(" = ").append(Columns.ORG_CODIGO).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_SERVICO).append(" on (").append(Columns.CNV_SVC_CODIGO).append(" = ").append(Columns.SVC_CODIGO).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_CONSIGNATARIA).append(" on (").append(Columns.CNV_CSA_CODIGO).append(" = ").append(Columns.CSA_CODIGO).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_OCORRENCIA_AUTORIZACAO).append(" on (").append(Columns.ADE_CODIGO).append(" = ").append(Columns.OCA_ADE_CODIGO).append(" and ").append(Columns.OCA_TOC_CODIGO).append(" = '6') ");
        corpoBuilder.append("inner join ").append(Columns.TB_PARCELA_DESCONTO).append(" on (").append(Columns.ADE_CODIGO).append(" = ").append(Columns.PRD_ADE_CODIGO).append(" and ").append(Columns.PRD_SPD_CODIGO).append(" in ('6', '7') and ").append(Columns.PRD_DATA_DESCONTO).append(" ").append(criaClausulaNomeada("periodo", periodo)).append(") ");
        corpoBuilder.append("left outer join ").append(Columns.TB_CORRESPONDENTE).append(" on (").append(Columns.ADE_COR_CODIGO).append(" = ").append(Columns.COR_CODIGO).append(") ");
        corpoBuilder.append("where 1 = 1 ");
        corpoBuilder.append("and ").append(Columns.ADE_SAD_CODIGO).append(" = '").append(CodedValues.SAD_LIQUIDADA).append("' ");
        corpoBuilder.append("and ").append(Columns.OCA_DATA).append(" ");
        corpoBuilder.append("between (select min(").append(Columns.HIE_DATA_FIM).append(") from ").append(Columns.TB_HISTORICO_EXPORTACAO).append(" where ").append(Columns.CNV_ORG_CODIGO).append(" = ").append(Columns.HIE_ORG_CODIGO).append(" and ").append(Columns.HIE_PERIODO).append(" ").append(criaClausulaNomeada("periodo", periodo)).append(") ");
        corpoBuilder.append("and (select min(").append(Columns.HCR_DATA_FIM).append(") from ").append(Columns.TB_HISTORICO_CONCLUSAO_RETORNO).append(" where ").append(Columns.CNV_ORG_CODIGO).append(" = ").append(Columns.HCR_ORG_CODIGO).append(" and ").append(Columns.HCR_PERIODO).append(" ").append(criaClausulaNomeada("periodo", periodo)).append(") ");

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND ").append(Columns.CNV_CSA_CODIGO).append(" ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(corCodigo)) {
            corpoBuilder.append(" AND ").append(Columns.ADE_COR_CODIGO).append(" ").append(criaClausulaNomeada("corCodigo", corCodigo));
        }

        if ((orgCodigos != null) && !orgCodigos.isEmpty()) {
            corpoBuilder.append(" AND ").append(Columns.CNV_ORG_CODIGO).append(" ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        if (!TextHelper.isNull(svcCodigos) && !svcCodigos.isEmpty()) {
            corpoBuilder.append(" AND ").append(Columns.SVC_CODIGO).append(" ").append(criaClausulaNomeada("svcCodigos", svcCodigos));
        }

        corpoBuilder.append(" order by ");
        if (ordenaPorCsa) {
            corpoBuilder.append(Columns.CSA_CODIGO);
        } else {
            corpoBuilder.append(Columns.ORG_CODIGO);
        }

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }
        if (!TextHelper.isNull(corCodigo)) {
            defineValorClausulaNomeada("corCodigo", corCodigo, query);
        }
        if ((orgCodigos != null) && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }
        if (!TextHelper.isNull(svcCodigos) && !svcCodigos.isEmpty()) {
            defineValorClausulaNomeada("svcCodigos", svcCodigos, query);
        }

        defineValorClausulaNomeada("periodo", parseDateString(periodo), query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CSA_CODIGO,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_NOME,
                "CONSIGNATARIA",
                Columns.COR_CODIGO,
                Columns.COR_IDENTIFICADOR,
                Columns.COR_NOME,
                "CORRESPONDENTE",
                Columns.ORG_CODIGO,
                Columns.ORG_IDENTIFICADOR,
                Columns.ORG_NOME,
                "ORGAO",
                Columns.CNV_COD_VERBA,
                Columns.SVC_IDENTIFICADOR,
                Columns.SVC_DESCRICAO,
                "SERVICO",
                Columns.RSE_MATRICULA,
                Columns.SER_NOME,
                Columns.SER_CPF,
                "SERVIDOR",
                Columns.ADE_NUMERO,
                Columns.ADE_INDICE,
                Columns.PRD_VLR_PREVISTO,
                Columns.PRD_VLR_REALIZADO,
                Columns.ADE_PRAZO,
                Columns.ADE_PRD_PAGAS,
                Columns.OCA_DATA,
                "ORDEM"
        };
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setCriterios(TransferObject criterio) {
        orgCodigos = (List<String>)criterio.getAttribute(Columns.ORG_CODIGO);
        csaCodigo = (String)criterio.getAttribute(Columns.CSA_CODIGO);
        corCodigo = (String)criterio.getAttribute(Columns.COR_CODIGO);
        svcCodigos = (List<String>)criterio.getAttribute(Columns.SVC_CODIGO);
        periodo = (String)criterio.getAttribute("PERIODO");
        if (criterio.getAttribute("ORDEM") != null) {
            ordenaPorCsa = Boolean.parseBoolean(criterio.getAttribute("ORDEM").toString());
        }
    }

}
