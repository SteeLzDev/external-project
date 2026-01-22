package com.zetra.econsig.persistence.query.consignacao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignacaoTransferenciaQuery</p>
 * <p>Description: Listagem de Consignações para Transferencia de Servidor</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignacaoTransferenciaQuery extends HQuery {

	public String adeNumero;
    public String rseCodigo;
    public List<String> sadCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select ade.adeCodigo ");
        corpoBuilder.append("from AutDesconto ade ");
        corpoBuilder.append("inner join ade.verbaConvenio vco ");
        corpoBuilder.append("inner join vco.convenio cnv ");
        corpoBuilder.append("inner join cnv.servico svc ");
        corpoBuilder.append("left outer join svc.naturezaServico nse ");
        corpoBuilder.append("where 1 = 1 ");

        // A natureza do contrato está habilitada para transferência, ou
        // algum contrato relacionado possui natureza habilitada para transferência.
        corpoBuilder.append("AND (coalesce(nse.nseTransferirAde, 'S') = 'S'");
        corpoBuilder.append(" OR exists (");
        corpoBuilder.append("   select 1 FROM ade.relacionamentoAutorizacaoByAdeCodigoDestinoSet radDest");
        corpoBuilder.append("   inner join radDest.autDescontoByAdeCodigoDestino adeDest ");
        corpoBuilder.append("   inner join adeDest.verbaConvenio vcoDest ");
        corpoBuilder.append("   inner join vcoDest.convenio cnvDest ");
        corpoBuilder.append("   inner join cnvDest.servico svcDest ");
        corpoBuilder.append("   left outer join svcDest.naturezaServico nseDest ");
        corpoBuilder.append("   where coalesce(nseDest.nseTransferirAde, 'S') = 'S' ");
        corpoBuilder.append(" )");
        corpoBuilder.append(" OR exists (");
        corpoBuilder.append("   select 1 FROM ade.relacionamentoAutorizacaoByAdeCodigoOrigemSet radOrig");
        corpoBuilder.append("   inner join radOrig.autDescontoByAdeCodigoOrigem adeOrig ");
        corpoBuilder.append("   inner join adeOrig.verbaConvenio vcoOrig ");
        corpoBuilder.append("   inner join vcoOrig.convenio cnvOrig ");
        corpoBuilder.append("   inner join cnvOrig.servico svcOrig ");
        corpoBuilder.append("   left outer join svcOrig.naturezaServico nseOrig ");
        corpoBuilder.append("   where coalesce(nseOrig.nseTransferirAde, 'S') = 'S' ");
        corpoBuilder.append(" )");
        corpoBuilder.append(")");

        if (!TextHelper.isNull(rseCodigo)) {
            corpoBuilder.append(" AND ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        }
        if (!TextHelper.isNull(adeNumero)) {
            corpoBuilder.append(" AND ade.adeNumero ").append(criaClausulaNomeada("adeNumero", adeNumero));
        }
        if (sadCodigos != null && sadCodigos.size() > 0) {
            corpoBuilder.append(" AND ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigo", sadCodigos));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }
        if (!TextHelper.isNull(adeNumero)) {
            defineValorClausulaNomeada("adeNumero", Long.parseLong(adeNumero), query);
        }
        if (sadCodigos != null && sadCodigos.size() > 0) {
            defineValorClausulaNomeada("sadCodigo", sadCodigos, query);
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
