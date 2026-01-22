package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioInadimplenciaQuery</p>
 * <p>Description: Recupera dados do consignante para o relatório inadimplencia.</p>
 * <p>Copyright: Copyright (c) 2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioInadimplenciaQuery extends ReportHQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder();
        corpo.append("select ");
        corpo.append("cseCodigo AS CSE_CODIGO, cseIdentificador AS CSE_IDENTIFICADOR, ");
        corpo.append("cseNome AS CSE_NOME, cseEmail AS CSE_EMAIL, cseResponsavel AS CSE_RESPONSAVEL, ");
        corpo.append("cseRespCargo AS CSE_RESP_CARGO, cseRespTelefone AS CSE_RESP_TELEFONE, ");
        corpo.append("cseAtivo AS CSE_ATIVO, cseTel AS CSE_TEL, cseFax AS CSE_FAX ");
        corpo.append("from Consignante ");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CSE_CODIGO,
                Columns.CSE_IDENTIFICADOR,
                Columns.CSE_NOME,
                Columns.CSE_EMAIL,
                Columns.CSE_RESPONSAVEL,
                Columns.CSE_RESP_CARGO,
                Columns.CSE_RESP_TELEFONE,
                Columns.CSE_ATIVO,
                Columns.CSE_TEL,
                Columns.CSE_FAX
        };
    }
}
