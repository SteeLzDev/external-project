package com.zetra.econsig.persistence.query.relatorio;

import java.util.ArrayList;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioGerencialGeralTaxasQuery</p>
 * <p>Description: Consulta de ranking de taxa de juros.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioGerencialGeralTaxasQuery extends ReportHNativeQuery {
    public String svcCodigo;
    public int maxPrazo;

    private final ArrayList<String> fieldList = new ArrayList<>();

    public RelatorioGerencialGeralTaxasQuery(String svcCodigo) {
        this.svcCodigo = svcCodigo;
    }

    @Override
    public void setCriterios(TransferObject criterio) {
        svcCodigo = (String) criterio.getAttribute("svcCodigo");
        maxPrazo = (int) criterio.getAttribute("maxPrazo");
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        // Define os campos básicos do relatório
        fieldList.clear();
        fieldList.add("CONSIGNATARIA");

        String fieldName;
        String order = "";

        String consignataria = " concatenar(concatenar(" + Columns.getColumnName(Columns.CSA_IDENTIFICADOR) + ", ' - '), case when nullif(trim(" + Columns.getColumnName(Columns.CSA_NOME_ABREV) + "), '') is null then " + Columns.getColumnName(Columns.CSA_NOME) + " else " + Columns.getColumnName(Columns.CSA_NOME_ABREV) + " end)";

        StringBuilder fields = new StringBuilder();
        fields.append(consignataria).append(" AS CONSIGNATARIA");

        for(int i = 12; i <= maxPrazo; i += 12){
            String prazo = String.valueOf(i);
            fieldName = "cft_vlr" + prazo;
            fieldList.add(fieldName);
            fields.append(", COALESCE(SUM(").append(Columns.getColumnName(Columns.CFT_VLR)).append(" * (1-abs(sign(").append(Columns.getColumnName(Columns.PRZ_VLR)).append(" - ").append(prazo).append(")))), 0.00) AS ").append(fieldName);
        }

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT ").append(fields.toString()).append(" FROM ( ");

        corpoBuilder.append(getCorpoQueryRelatorio());

        corpoBuilder.append(") FROM_VIRTUAL ");

        corpoBuilder.append(" GROUP BY ").append(consignataria);

        corpoBuilder.append(" ORDER BY ");

        for(int i = 12; i <= maxPrazo; i += 12){
            String prazo = String.valueOf(i);
            order += "cft_vlr" + prazo + ",";
        }

        order = order.substring(0,order.length() - 1);

        corpoBuilder.append(order);

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }

        return query;
    }

    /**
     * Monta o corpo da query de acordo com qual tabela de taxas utilizar.
     * @param campos
     * @return
     */
    private String getCorpoQueryRelatorio() {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT ");
        corpoBuilder.append(Columns.CSA_IDENTIFICADOR).append(", ").append(Columns.CSA_NOME).append(", ").append(Columns.CSA_NOME_ABREV).append(", ");
        corpoBuilder.append(Columns.CFA_VLR).append(", ").append(Columns.PRZ_VLR);

        corpoBuilder.append(" FROM ").append(Columns.TB_PRAZO);
        corpoBuilder.append(" INNER JOIN ").append(Columns.TB_PRAZO_CONSIGNATARIA);
        corpoBuilder.append(" ON ").append(Columns.PZC_PRZ_CODIGO).append(" = ").append(Columns.PRZ_CODIGO);
        corpoBuilder.append(" INNER JOIN ").append(Columns.TB_COEFICIENTE_ATIVO);
        corpoBuilder.append(" ON ").append(Columns.CFA_PRZ_CSA_CODIGO).append(" = ").append(Columns.PZC_CODIGO);
        corpoBuilder.append(" INNER JOIN ").append(Columns.TB_CONSIGNATARIA);
        corpoBuilder.append(" ON ").append(Columns.CSA_CODIGO).append(" = ").append(Columns.PZC_CSA_CODIGO);

        corpoBuilder.append(" WHERE ").append(Columns.PRZ_SVC_CODIGO).append(criaClausulaNomeada("svcCodigo", svcCodigo));
        // Buscando as taxas de juros que estão ativas no momento
        corpoBuilder.append(" AND to_days(").append(Columns.CFA_DATA_INI_VIG).append(") <= to_days(data_corrente())");
        corpoBuilder.append(" AND (to_days(").append(Columns.CFA_DATA_FIM_VIG).append(") >= to_days(data_corrente()) OR ").append(Columns.CFA_DATA_FIM_VIG).append(" IS NULL)");
        corpoBuilder.append(" AND mod(").append(Columns.PRZ_VLR).append(", 12) = 0 ");

        corpoBuilder.append(" GROUP BY ");
        corpoBuilder.append(Columns.CSA_IDENTIFICADOR).append(", ").append(Columns.CSA_NOME).append(", ").append(Columns.CSA_NOME_ABREV).append(", ");
        corpoBuilder.append(Columns.CFA_VLR).append(", ").append(Columns.PRZ_VLR);

        return corpoBuilder.toString();
    }

    @Override
    protected String[] getFields() {
        return fieldList.toArray(new String[]{});
    }
}
