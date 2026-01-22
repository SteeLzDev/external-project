package com.zetra.econsig.persistence.query.parametro;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaParamCsaQuery</p>
 * <p>Description: Listagem dos parâmetros de consignatária, para o gestor ou para a propria consignatária.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaParamCsaQuery extends HQuery {

    public String csaCodigo;
    public String tpaCodigo;
    public String tpaCseAltera;
    public String tpaCsaAltera;
    public String tpaSupAltera;
    public boolean naoNulo = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "SELECT " +
                "tpa.tpaCodigo, " +
                "tpa.tpaDescricao, " +
                "tpa.tpaDominio, " +
                "pcs.pcsVlr, " +
                "pcs.consignataria.csaCodigo ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" FROM TipoParamConsignataria tpa ");
        if (naoNulo) {
            corpoBuilder.append(" INNER JOIN tpa.paramConsignatariaSet pcs ");
        } else {
            corpoBuilder.append(" LEFT OUTER JOIN tpa.paramConsignatariaSet pcs ");
        }
        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" WITH pcs.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }
        corpoBuilder.append(" WHERE 1=1 ");

        if (!TextHelper.isNull(tpaCodigo)) {
            corpoBuilder.append(" AND tpa.tpaCodigo ").append(criaClausulaNomeada("tpaCodigo", tpaCodigo));
        }
        if (!TextHelper.isNull(tpaCseAltera)) {
            corpoBuilder.append(" AND tpa.tpaCseAltera ").append(criaClausulaNomeada("tpaCseAltera", tpaCseAltera));
        }
        if (!TextHelper.isNull(tpaCsaAltera)) {
            corpoBuilder.append(" AND tpa.tpaCsaAltera ").append(criaClausulaNomeada("tpaCsaAltera", tpaCsaAltera));
        }
        if (!TextHelper.isNull(tpaSupAltera)) {
            corpoBuilder.append(" AND tpa.tpaSupAltera ").append(criaClausulaNomeada("tpaSupAltera", tpaSupAltera));
        }
        if (naoNulo) {
            corpoBuilder.append(" AND pcs.pcsVlr IS NOT NULL");
        }

        corpoBuilder.append(" ORDER BY tpa.tpaDescricao");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }
        if (!TextHelper.isNull(tpaCodigo)) {
            defineValorClausulaNomeada("tpaCodigo", tpaCodigo, query);
        }
        if (!TextHelper.isNull(tpaCseAltera)) {
            defineValorClausulaNomeada("tpaCseAltera", tpaCseAltera, query);
        }
        if (!TextHelper.isNull(tpaCsaAltera)) {
            defineValorClausulaNomeada("tpaCsaAltera", tpaCsaAltera, query);
        }
        if (!TextHelper.isNull(tpaSupAltera)) {
            defineValorClausulaNomeada("tpaSupAltera", tpaSupAltera, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.TPA_CODIGO,
                Columns.TPA_DESCRICAO,
                Columns.TPA_DOMINIO,
                Columns.PCS_VLR,
                Columns.PCS_CSA_CODIGO
        };
    }
}
