package com.zetra.econsig.persistence.query.compra;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ContaComprasAbertasServidorQuery</p>
 * <p>Description: Conta quantas compras abertas abertas o servidor possui.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ContaComprasAbertasServidorQuery extends HQuery {
    public String rseCodigo;
    public String adeCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT COUNT(DISTINCT adeDestino.adeCodigo) AS TOTAL");
        corpoBuilder.append(" FROM AutDesconto adeDestino");
        corpoBuilder.append(" INNER JOIN adeDestino.relacionamentoAutorizacaoByAdeCodigoDestinoSet rad");
        corpoBuilder.append("       WITH rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTROLE_COMPRA).append("'");
        corpoBuilder.append(" WHERE 1 = 1");
        corpoBuilder.append(" AND adeDestino.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_AGUARD_CONF).append("'");
        corpoBuilder.append(" AND adeDestino.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));

        if (!TextHelper.isNull(adeCodigo)) {
            corpoBuilder.append(" AND adeDestino.adeCodigo <> :adeCodigo");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

        if (!TextHelper.isNull(adeCodigo)) {
            defineValorClausulaNomeada("adeCodigo", adeCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "TOTAL"
        };
    }
}
