package com.zetra.econsig.persistence.query.consignacao;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaRelAutorizacaoInconsTransfQuery</p>
 * <p>Description: Listagem de consignações, que possuem relacionamento entre si, que ficaram inconsistentes após
 * transferencia de servidor, ou seja a origem ligada a um registro servidor e o destino a outro. Provavelmente
 * por causa de configurações diversas que dizem que uma natureza tenha transferência e a outra não, ou que
 * uma verba/serviço esteja bloqueado no servidor destino.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaRelAutorizacaoInconsTransfQuery extends HQuery {

    // Caso TRUE, procura o registro servidor antigo na origem do relacionamento,
    // caso contrário, no destino do relacionamento.
    public boolean origem = true;

    // Código do registro servidor novo e antigo
    public String rseCodigoAnt;
    public String rseCodigoNov;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select ").append(origem ? "adeOrig" : "adeDest").append(".adeCodigo ");
        corpoBuilder.append("from RelacionamentoAutorizacao rad ");
        corpoBuilder.append("inner join rad.autDescontoByAdeCodigoOrigem adeOrig ");
        corpoBuilder.append("inner join rad.autDescontoByAdeCodigoDestino adeDest ");
        corpoBuilder.append("where adeOrig.registroServidor.rseCodigo <> adeDest.registroServidor.rseCodigo ");
        corpoBuilder.append("  and ").append(origem ? "adeOrig" : "adeDest").append(".registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigoAnt", rseCodigoAnt));
        corpoBuilder.append("  and ").append(origem ? "adeDest" : "adeOrig").append(".registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigoNov", rseCodigoNov));
        corpoBuilder.append("  and rad.tipoNatureza.tntCodigo <> '").append(CodedValues.TNT_TRANSFERENCIA_CONTRATO).append("' ");
        corpoBuilder.append(" group by ").append(origem ? "adeOrig" : "adeDest").append(".adeCodigo ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigoAnt", rseCodigoAnt, query);
        defineValorClausulaNomeada("rseCodigoNov", rseCodigoNov, query);

        return query;
    }

    @Override
    protected String[] getFields() {
    	return new String[] {
        		Columns.ADE_CODIGO
         };
    }
}
