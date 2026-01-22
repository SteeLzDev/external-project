package com.zetra.econsig.persistence.query.parcela;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaOcorrenciaParcelaAgrupadaQuery</p>
 * <p>Description: Agrupa as ocorrências de parcela por tipo de
 * um determinado período.</p>
 * <p>Copyright: Copyright (c) 2002-2016</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaOcorrenciaParcelaAgrupadaQuery extends HQuery {

    public String estCodigo;
    public String orgCodigo;
    public Date prdDataDesconto;
    public List<String> spdCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        if (spdCodigos == null || spdCodigos.isEmpty()) {
            spdCodigos = new ArrayList<>();
            spdCodigos.add(CodedValues.SPD_LIQUIDADAFOLHA);
            spdCodigos.add(CodedValues.SPD_REJEITADAFOLHA);
        }

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT prd.prdDataDesconto, to_date(prd.prdDataRealizado), toc.tocCodigo, toc.tocDescricao, count(distinct prd.autDesconto.adeCodigo) ");
        corpoBuilder.append("FROM OcorrenciaParcela ocp ");
        corpoBuilder.append("INNER JOIN ocp.tipoOcorrencia toc ");
        corpoBuilder.append("INNER JOIN ocp.parcelaDesconto prd ");

        if (!TextHelper.isNull(estCodigo) || !TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append("INNER JOIN prd.autDesconto ade ");
            corpoBuilder.append("INNER JOIN ade.registroServidor rse ");
            corpoBuilder.append("INNER JOIN rse.orgao org ");
        }

        corpoBuilder.append("WHERE prd.prdDataDesconto >= :prdDataDesconto ");
        corpoBuilder.append("  AND prd.statusParcelaDesconto.spdCodigo ").append(criaClausulaNomeada("spdCodigo", spdCodigos));

        if (!TextHelper.isNull(estCodigo)) {
            corpoBuilder.append(" AND org.estabelecimento.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigo));
        }
        if (!TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" AND org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }

        corpoBuilder.append(" GROUP BY prd.prdDataDesconto, to_date(prd.prdDataRealizado), toc.tocCodigo, toc.tocDescricao");
        corpoBuilder.append(" ORDER BY prd.prdDataDesconto, to_date(prd.prdDataRealizado)");


        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("prdDataDesconto", prdDataDesconto, query);
        defineValorClausulaNomeada("spdCodigo", spdCodigos, query);

        if (!TextHelper.isNull(estCodigo)) {
            defineValorClausulaNomeada("estCodigo", estCodigo, query);
        }
        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.PRD_DATA_DESCONTO,
                Columns.PRD_DATA_REALIZADO,
                Columns.TOC_CODIGO,
                Columns.TOC_DESCRICAO,
                "QTDE"
        };
    }
}
