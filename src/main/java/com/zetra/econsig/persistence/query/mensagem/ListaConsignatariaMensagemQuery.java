package com.zetra.econsig.persistence.query.mensagem;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignatariasQuery</p>
 * <p>Description: Listagem de Consignatárias as quais receberam a mensagem.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignatariaMensagemQuery extends HQuery {

    public String csaIdentificador;
    public String csaNome;
    public String csaNomeAbrev;
    public String csaCodigo;
    public String menCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "SELECT COALESCE(NULLIF(TRIM(csa.csaNomeAbrev), ''), csa.csaNome), " +
                       "csa.csaCodigo, " +
                       "csa.csaIdentificador, " +
                       "csa.csaNome, " +
                       "csa.csaAtivo, " +
                       "csa.csaEmail, ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        if (!TextHelper.isNull(menCodigo)) {
            corpoBuilder.append("CASE WHEN menCsa.menCodigo IS NULL THEN 'N' ELSE 'S' END AS SELECIONADO ");
            corpoBuilder.append("FROM Consignataria csa ");

            corpoBuilder.append("LEFT OUTER JOIN csa.mensagemCsaSet menCsa WITH menCsa.menCodigo = :menCodigo ");

        } else {
            corpoBuilder.append("'N' AS SELECIONADO ");
            corpoBuilder.append("FROM Consignataria csa ");
        }

        corpoBuilder.append("ORDER BY COALESCE(NULLIF(TRIM(csa.csaNomeAbrev), ''), csa.csaNome)");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(menCodigo)) {
            query.setParameter("menCodigo", menCodigo);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.getColumnName(Columns.CSA_NOME_ABREV),
                Columns.CSA_CODIGO,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_NOME,
                Columns.CSA_ATIVO,
                Columns.CSA_EMAIL,
                "SELECIONADO"
        };
    }
}
