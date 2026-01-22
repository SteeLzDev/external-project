package com.zetra.econsig.persistence.query.compra;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: CompraPossuiRejeitoPgtSaldoQuery</p>
 * <p>Description: Verifica se uma compra de contratos possui rejeito de pagamento de saldo.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CompraPossuiRejeitoPgtSaldoQuery extends HQuery {

    public String adeCodigoDestino;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append(" SELECT COUNT(*)");
        corpoBuilder.append(" FROM RelacionamentoAutorizacao rad");
        corpoBuilder.append(" INNER JOIN rad.autDescontoByAdeCodigoOrigem adeOrigem");        
        corpoBuilder.append(" WHERE rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTROLE_COMPRA).append("'");
        corpoBuilder.append(" AND rad.adeCodigoDestino ").append(criaClausulaNomeada("adeCodigoDestino", adeCodigoDestino));

        // E não existe uma rejeição de pagamento de saldo devedor
        corpoBuilder.append(" AND EXISTS (");
        corpoBuilder.append(" SELECT _oca.ocaCodigo");
        corpoBuilder.append(" FROM adeOrigem.ocorrenciaAutorizacaoSet _oca");
        corpoBuilder.append(" WHERE _oca.tipoOcorrencia.tocCodigo = '").append(CodedValues.TOC_REJEICAO_PAGAMENTO_SALDO_DEVEDOR).append("'");
        corpoBuilder.append(" AND _oca.ocaData > rad.radData");
        corpoBuilder.append(")");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("adeCodigoDestino", adeCodigoDestino, query);
        return query;
    }
}
