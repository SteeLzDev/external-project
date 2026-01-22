package com.zetra.econsig.persistence.query.compra;

import java.util.Collection;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCompraPassivelCancelamentoQuery</p>
 * <p>Description: Lista as compras que são passíveis de cancelamento.</p>
 * <p>Copyright: Copyright (c) 2002-2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCompraPassivelCancelamentoQuery extends HQuery {

    public String adeCodigo;
    public Collection<String> adeCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append(" SELECT adeDestino.adeCodigo");
        corpoBuilder.append(" FROM RelacionamentoAutorizacao rad");
        corpoBuilder.append(" INNER JOIN rad.autDescontoByAdeCodigoDestino adeDestino");
        corpoBuilder.append(" INNER JOIN rad.autDescontoByAdeCodigoOrigem adeOrigem");
        corpoBuilder.append(" WHERE rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTROLE_COMPRA).append("'");
        corpoBuilder.append(" AND adeOrigem.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_AGUARD_LIQUI_COMPRA).append("'");
        corpoBuilder.append(" AND adeDestino.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_AGUARD_CONF).append("'");

        // Adiciona cláusula que verifica se a compra pode ser cancelada
        corpoBuilder.append(CompraPassivelCancelamentoQuery.gerarClausulaPendenciaCancelCompra());

        if (!TextHelper.isNull(adeCodigo)) {
            corpoBuilder.append(" AND adeDestino.adeCodigo ").append(criaClausulaNomeada("adeCodigo", adeCodigo));
        } else if (adeCodigos != null && adeCodigos.size() > 0) {
            corpoBuilder.append(" AND adeDestino.adeCodigo ").append(criaClausulaNomeada("adeCodigo", adeCodigos));
        }

        corpoBuilder.append(" GROUP BY adeDestino.adeCodigo");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        if (!TextHelper.isNull(adeCodigo)) {
            defineValorClausulaNomeada("adeCodigo", adeCodigo, query);
        } else if (adeCodigos != null && adeCodigos.size() > 0) {
            defineValorClausulaNomeada("adeCodigo", adeCodigos, query);
        }
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO
        };
    }
}
