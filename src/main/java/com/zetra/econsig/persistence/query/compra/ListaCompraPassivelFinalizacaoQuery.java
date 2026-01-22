package com.zetra.econsig.persistence.query.compra;

import java.util.Collection;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusCompraEnum;

/**
 * <p>Title: ListaCompraPassivelFinalizacaoQuery</p>
 * <p>Description: Lista as compras que são passíveis de finalização do processo,
 * ou seja, os contratos origem já se encontram liquidados ou concluídos.</p>
 * <p>Copyright: Copyright (c) 2002-2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCompraPassivelFinalizacaoQuery extends HQuery {

    public String adeCodigo;
    public Collection<String> adeCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append(" SELECT ade.adeCodigo");
        corpoBuilder.append(" FROM AutDesconto ade");
        corpoBuilder.append(" WHERE ade.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_AGUARD_CONF).append("'");

        // Contratos que são destino de um relacionamento de compra
        corpoBuilder.append(" AND EXISTS (");
        corpoBuilder.append(" SELECT rad.adeCodigoDestino");
        corpoBuilder.append(" FROM ade.relacionamentoAutorizacaoByAdeCodigoDestinoSet rad");
        corpoBuilder.append(" WHERE rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTROLE_COMPRA).append("'");
        corpoBuilder.append(")");

        // E que a compra não teve contrato aguardando saldo, pagamento ou liquidação
        corpoBuilder.append(" AND NOT EXISTS (");
        corpoBuilder.append(" SELECT rad2.adeCodigoDestino");
        corpoBuilder.append(" FROM ade.relacionamentoAutorizacaoByAdeCodigoDestinoSet rad2");
        corpoBuilder.append(" WHERE rad2.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTROLE_COMPRA).append("'");
        corpoBuilder.append(" AND rad2.statusCompra.stcCodigo in ('");
        corpoBuilder.append(StatusCompraEnum.AGUARDANDO_INF_SALDO.getCodigo()).append("', '");
        corpoBuilder.append(StatusCompraEnum.AGUARDANDO_PAG_SALDO.getCodigo()).append("', '");
        corpoBuilder.append(StatusCompraEnum.AGUARDANDO_LIQUIDACAO.getCodigo()).append("')");
        corpoBuilder.append(")");

        if (!TextHelper.isNull(adeCodigo)) {
            corpoBuilder.append(" AND ade.adeCodigo ").append(criaClausulaNomeada("adeCodigo", adeCodigo));
        } else if (adeCodigos != null && adeCodigos.size() > 0) {
            corpoBuilder.append(" AND ade.adeCodigo ").append(criaClausulaNomeada("adeCodigo", adeCodigos));
        }
        
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
