package com.zetra.econsig.persistence.query.compra;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemRegistroServidorPorAdeQuery</p>
 * <p>Description: Busca o registro servidor dos contratos informados</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemRseParaBloqueioCompraQuery extends HQuery {

    public List<String> adeCodigos;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT ade.registroServidor.rseCodigo, max(add_day(current_timestamp(), (CASE ISNUMERIC(pse194.pseVlrRef) WHEN 1 THEN TO_NUMERIC(COALESCE(NULLIF(TRIM(pse194.pseVlrRef), ''), '0')) ELSE 0 END)))");
        corpoBuilder.append(" FROM AutDesconto ade");
        corpoBuilder.append(" INNER JOIN ade.verbaConvenio vco");
        corpoBuilder.append(" INNER JOIN vco.convenio cnv");
        corpoBuilder.append(" INNER JOIN cnv.servico.paramSvcConsignanteSet pse194");
        corpoBuilder.append(" WHERE ade.adeCodigo ").append(criaClausulaNomeada("adeCodigos", adeCodigos));
        corpoBuilder.append("   AND pse194.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_ACAO_PARA_NAO_APR_SALDO_DV).append("'");
        corpoBuilder.append(" GROUP BY ade.registroServidor.rseCodigo");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("adeCodigos", adeCodigos, query);
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.BRS_RSE_CODIGO,
                Columns.BRS_DATA_LIMITE
        };
    }
}
