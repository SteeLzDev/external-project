package com.zetra.econsig.persistence.query.posto;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaBloqueioPostoCsaSvcQuery</p>
 * <p>Description: Listagem de bloqueios de postos por CSA e SVC</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaBloqueioSolicitacaoPorPostoCsaSvcQuery extends HQuery {

    public String posCodigo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpo = new StringBuilder();
        corpo.append("select bpc.consignataria.csaCodigo, bpc.servico.svcCodigo ");
        corpo.append("from BloqueioPostoCsaSvc bpc ");
        corpo.append("where bpc.postoRegistroServidor.posCodigo ").append(criaClausulaNomeada("posCodigo", posCodigo));
        corpo.append("  and bpc.bpcBloqSolicitacao = 'S'");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());
        defineValorClausulaNomeada("posCodigo", posCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CSA_CODIGO,
                Columns.SVC_CODIGO
        };
    }
}
