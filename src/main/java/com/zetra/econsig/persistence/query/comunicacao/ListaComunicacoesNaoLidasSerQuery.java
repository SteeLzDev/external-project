package com.zetra.econsig.persistence.query.comunicacao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaComunicacoesNaoLidasSerQuery</p>
 * <p>Description: lista de comunicações não lidas da SER.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaComunicacoesNaoLidasSerQuery extends HQuery {

    public int diasAposEnvio;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT ser.serCodigo, ser.serNome, text_to_string(ser.serEmail), count(*) ");
        corpoBuilder.append("FROM Comunicacao cmn ");
        corpoBuilder.append("INNER JOIN cmn.comunicacaoSerSet cms ");
        corpoBuilder.append("INNER JOIN cms.servidor ser ");

        corpoBuilder.append("WHERE cmn.comunicacaoPai.cmnCodigo IS NULL ");

        corpoBuilder.append("AND (date_diff(data_corrente(), cmn.cmnData) = :diasAposEnvio ");
        corpoBuilder.append("OR EXISTS (SELECT 1 FROM Comunicacao cmnFilha ");
        corpoBuilder.append("WHERE cmnFilha.comunicacaoPai.cmnCodigo = cmn.cmnCodigo ");
        corpoBuilder.append("AND date_diff(data_corrente(), cmnFilha.cmnData) = :diasAposEnvio ");
        corpoBuilder.append("))");

        corpoBuilder.append("AND NOT EXISTS (SELECT 1 FROM LeituraComunicacaoUsuario lcu ");
        corpoBuilder.append("INNER JOIN lcu.usuario usu ");
        corpoBuilder.append("INNER JOIN usu.usuarioSerSet usr ");
        corpoBuilder.append("WHERE lcu.cmnCodigo = cmn.cmnCodigo ");
        corpoBuilder.append("  AND usr.serCodigo = ser.serCodigo ");
        corpoBuilder.append(")");

        corpoBuilder.append("GROUP BY ser.serCodigo, ser.serNome, text_to_string(ser.serEmail) ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        query.setParameter("diasAposEnvio", diasAposEnvio);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SER_CODIGO,
                Columns.SER_NOME,
                Columns.SER_EMAIL,
                "QTD_CMN_NAO_LIDA"
        };
    }
}
