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
public class ListaBloqueioPostoCsaSvcQuery extends HQuery {

    public String csaCodigo;
    public String svcCodigo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpo = new StringBuilder();
        corpo.append("select pos.posCodigo, pos.posDescricao, pos.posIdentificador, bpc.bpcBloqSolicitacao, bpc.bpcBloqReserva ");
        corpo.append("from PostoRegistroServidor pos ");
        corpo.append("left outer join pos.bloqueioPostoCsaSvcSet bpc ");
        corpo.append("with bpc.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        corpo.append(" and bpc.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));

        Query<Object[]> query = instanciarQuery(session, corpo.toString());
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        defineValorClausulaNomeada("svcCodigo", svcCodigo, query);

        return query;

    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.POS_CODIGO,
                Columns.POS_DESCRICAO,
                Columns.POS_IDENTIFICADOR,
                Columns.BPC_BLOQ_SOLICITACAO,
                Columns.BPC_BLOQ_RESERVA
        };
    }
}
