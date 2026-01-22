package com.zetra.econsig.persistence.query.parcela;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaParcelasReimplanteManualQuery</p>
 * <p>Description: Retorna as parcelas não reimplantadas manualmente de um contrato.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaParcelasReimplanteManualQuery extends HQuery {

    public String adeCodigo;
    public List<String> spdCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "SELECT " +
                       "prd.autDesconto.adeCodigo, " +
                       "prd.prdNumero, " +
                       "spd.spdCodigo, " +
                       "spd.spdDescricao, " +
                       "prd.prdDataDesconto, " +
                       "prd.prdDataRealizado, " +
                       "prd.prdVlrPrevisto, " +
                       "prd.prdVlrRealizado ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append("FROM ParcelaDesconto prd ");
        corpoBuilder.append("INNER JOIN prd.statusParcelaDesconto spd ");
        corpoBuilder.append("WHERE prd.autDesconto.adeCodigo ").append(criaClausulaNomeada("adeCodigo", adeCodigo));

        if (spdCodigos != null && !spdCodigos.isEmpty()) {
            corpoBuilder.append(" AND spd.spdCodigo ").append(criaClausulaNomeada("spdCodigo", spdCodigos));
        }

        corpoBuilder.append("AND NOT EXISTS (SELECT 1 FROM OcorrenciaParcela ocp WHERE ocp.parcelaDesconto.prdCodigo = prd.prdCodigo ");
        corpoBuilder.append("AND ocp.tipoOcorrencia.tocCodigo ='").append(CodedValues.TOC_REIMPLANTE_PARCELA_MANUAL).append("')");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("adeCodigo", adeCodigo, query);

        if (spdCodigos != null && !spdCodigos.isEmpty()) {
            defineValorClausulaNomeada("spdCodigo", spdCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.PRD_ADE_CODIGO,
                Columns.PRD_NUMERO,
                Columns.PRD_SPD_CODIGO,
                Columns.SPD_DESCRICAO,
                Columns.PRD_DATA_DESCONTO,
                Columns.PRD_DATA_REALIZADO,
                Columns.PRD_VLR_PREVISTO,
                Columns.PRD_VLR_REALIZADO
        };
    }
}
