package com.zetra.econsig.persistence.query.comunicacao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaUsuariosComunicacoesPendentesCsaQuery</p>
 * <p>Description: lista de usuários distintos que tem comunicação pendente com para a CSA.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaUsuariosComunicacoesPendentesCsaQuery extends HQuery {
    public boolean count = false;
    public String csaCodigo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        corpo = "SELECT cmnCsa.csaCodigo, usu.usuCodigo ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" FROM Comunicacao cmn");
        corpoBuilder.append(" INNER JOIN cmn.comunicacaoCsaSet cmnCsa");
        corpoBuilder.append(" INNER JOIN cmn.usuario usu");

        corpoBuilder.append(" WHERE cmnCsa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        corpoBuilder.append(" AND cmn.cmnPendencia = :cmnPendencia");
        corpoBuilder.append(" GROUP BY cmnCsa.csaCodigo, usu.usuCodigo");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("cmnPendencia", Boolean.TRUE, query);

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CMC_CSA_CODIGO,
                Columns.USU_CODIGO
        };
    }


}
