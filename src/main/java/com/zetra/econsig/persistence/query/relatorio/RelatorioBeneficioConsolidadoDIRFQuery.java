package com.zetra.econsig.persistence.query.relatorio;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioBeneficioConsolidadoDIRFQuery</p>
 * <p>Description: Consulta benefícios consolidados DIRF para um dado período</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioBeneficioConsolidadoDIRFQuery extends ReportHNativeQuery {

    public String periodo;
    public List<String> lstOrgaos;
    public List<String> lstCsas;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select ").append(Columns.CNV_COD_VERBA_DIRF).append(" as CNV_COD_VERBA_DIRF, ");
        corpoBuilder.append(Columns.BFC_CPF).append(" as BFC_CPF, ");
        corpoBuilder.append(Columns.RSE_MATRICULA).append(" as RSE_MATRICULA, ");
        corpoBuilder.append(Columns.CSA_CNPJ).append(" as CSA_CNPJ, ");
        corpoBuilder.append(Columns.TLA_NSE_CODIGO).append(" as NATUREZA_SERVICO, ");
        corpoBuilder.append("SUM(COALESCE(").append(Columns.PRD_VLR_REALIZADO).append(",0))").append(" as PRD_VLR_REALIZADO, ");
        corpoBuilder.append(Columns.ORG_IDENTIFICADOR_BENEFICIO).append(" as ORG_IDENTIFICADOR_BENEFICIO, ");
        try {
            corpoBuilder.append("'").append(DateHelper.reformat(periodo, "yyyy-MM-dd", "dd/MM/yyyy")).append("' as PERIODO_REFERENCIA ");
        } catch (ParseException e) {
            throw new HQueryException(e);
        }

        corpoBuilder.append("from ").append(Columns.TB_CONTRATO_BENEFICIO).append(" ");
        corpoBuilder.append("inner join ").append(Columns.TB_AUTORIZACAO_DESCONTO).append(" on (").append(Columns.CBE_CODIGO).append(" = ").append(Columns.ADE_CBE_CODIGO).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_PARCELA_DESCONTO).append(" on (").append(Columns.PRD_ADE_CODIGO).append(" = ").append(Columns.ADE_CODIGO).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_VERBA_CONVENIO).append(" on (").append(Columns.ADE_VCO_CODIGO).append(" = ").append(Columns.VCO_CODIGO).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_CONVENIO).append(" on (").append(Columns.VCO_CNV_CODIGO).append(" = ").append(Columns.CNV_CODIGO).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_TIPO_LANCAMENTO).append(" on (").append(Columns.ADE_TLA_CODIGO).append(" = ").append(Columns.TLA_CODIGO).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_CONSIGNATARIA).append(" on (").append(Columns.CNV_CSA_CODIGO).append(" = ").append(Columns.CSA_CODIGO).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_BENEFICIARIO).append(" on (").append(Columns.CBE_BFC_CODIGO).append(" = ").append(Columns.BFC_CODIGO).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_REGISTRO_SERVIDOR).append(" on (").append(Columns.ADE_RSE_CODIGO).append(" = ").append(Columns.RSE_CODIGO).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_ORGAO).append(" on (").append(Columns.RSE_ORG_CODIGO).append(" = ").append(Columns.ORG_CODIGO).append(") ");

        corpoBuilder.append(" WHERE ").append(Columns.TLA_NSE_CODIGO).append(" IN ('").append(CodedValues.NSE_PLANO_DE_SAUDE).append("','").append(CodedValues.NSE_PLANO_ODONTOLOGICO).append("')");

        List<String> tntSubsidios = new ArrayList<String>();
        tntSubsidios.add(CodedValues.NOT_EQUAL_KEY);
        tntSubsidios.addAll(CodedValues.TNT_TODOS_BENEFICIO_SUBSIDIO);

        corpoBuilder.append(" AND ").append(Columns.TLA_TNT_CODIGO).append(criaClausulaNomeada("tntSubsidios", tntSubsidios));
        corpoBuilder.append(" AND ").append(Columns.PRD_DATA_DESCONTO).append(" ").append(criaClausulaNomeada("periodo", periodo));

        if (lstOrgaos != null && !lstOrgaos.isEmpty()) {
            corpoBuilder.append(" AND ").append(Columns.ORG_CODIGO).append(" ").append(criaClausulaNomeada("lstOrgaos", lstOrgaos));
        }

        if (lstCsas != null && !lstCsas.isEmpty()) {
            corpoBuilder.append(" AND ").append(Columns.CSA_CODIGO).append(" ").append(criaClausulaNomeada("lstCsas", lstCsas));
        }

        corpoBuilder.append(" GROUP BY ").append("RSE_MATRICULA,").append("BFC_CPF,").append("CSA_CNPJ,").append("NATUREZA_SERVICO,");
        corpoBuilder.append("ORG_IDENTIFICADOR_BENEFICIO,").append("CNV_COD_VERBA_DIRF,").append("PERIODO_REFERENCIA");
        corpoBuilder.append(" HAVING PRD_VLR_REALIZADO > 0");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("periodo", parseDateString(periodo), query);

        if (lstCsas != null && !lstCsas.isEmpty()) {
            defineValorClausulaNomeada("lstCsas", lstCsas, query);
        }

        if (lstOrgaos != null && !lstOrgaos.isEmpty()) {
            defineValorClausulaNomeada("lstOrgaos", lstOrgaos, query);
        }

        defineValorClausulaNomeada("tntSubsidios", tntSubsidios, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "CNV_COD_VERBA_DIRF",
                "BFC_CPF",
                "RSE_MATRICULA",
                "CSA_CNPJ",
                "NATUREZA_SERVICO",
                "PRD_VLR_REALIZADO",
                "ORG_IDENTIFICADOR_BENEFICIO",
                "PERIODO_REFERENCIA"
        };
    }

}
