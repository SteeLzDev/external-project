package com.zetra.econsig.persistence.query.parcela;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaResumoParcelasPeriodoQuery</p>
 * <p>Description: Lista a quantidade de parelas por situação no período.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaResumoParcelasPeriodoQuery extends HNativeQuery {

    public Date periodo;
    public List<String> orgCodigos;
    public List<String> estCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "SELECT spd_codigo, spd_descricao, count(*) AS qtde";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" FROM (");
        corpoBuilder.append(getQuery("tb_parcela_desconto_periodo"));
        corpoBuilder.append(" UNION ALL ");
        corpoBuilder.append(getQuery("tb_parcela_desconto"));
        corpoBuilder.append(") AS status");
        corpoBuilder.append(" GROUP BY spd_codigo, spd_descricao ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("prdDataDesconto", periodo, query);

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigos", orgCodigos, query);
        }

        if (estCodigos != null && !estCodigos.isEmpty()) {
            defineValorClausulaNomeada("estCodigos", estCodigos, query);
        }

        return query;
    }

    private String getQuery(String tabela) {
        String corpo = "SELECT spd.spd_codigo, spd.spd_descricao";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" FROM ").append(tabela).append(" prd ");
        corpoBuilder.append(" INNER JOIN tb_status_parcela_desconto spd ON (prd.spd_codigo = spd.spd_codigo)");
        corpoBuilder.append(" INNER JOIN tb_aut_desconto ade ON (prd.ade_codigo = ade.ade_codigo)");
        if ((orgCodigos != null && !orgCodigos.isEmpty()) || (estCodigos != null && !estCodigos.isEmpty())) {
            corpoBuilder.append(" INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo)");
            corpoBuilder.append(" INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo)");
            corpoBuilder.append(" INNER JOIN tb_orgao org ON (cnv.org_codigo = org.org_codigo) ");
        }
        corpoBuilder.append(" WHERE prd.prd_data_desconto ").append(criaClausulaNomeada("prdDataDesconto", periodo));
        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            corpoBuilder.append(" AND org.org_codigo IN (:orgCodigos)");
        }
        if (estCodigos != null && !estCodigos.isEmpty()) {
            corpoBuilder.append(" AND org.est_codigo IN (:estCodigos)");
        }
        return corpoBuilder.toString();
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SPD_CODIGO,
                Columns.SPD_DESCRICAO,
                "qtde"
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }
}
