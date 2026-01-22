package com.zetra.econsig.persistence.query.pergunta;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaPerguntaDadosCadastrais</p>
 * <p>Description:Lista as perguntas para verificação dos dados cadastrais.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaPerguntaDadosCadastraisQuery extends HQuery {

    public Short pdcGrupo;
    public Short pdcNumero;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        Short pdcStatus = CodedValues.STS_ATIVO;

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select pdc.pdcGrupo, pdc.pdcNumero, pdc.pdcStatus, pdc.pdcTexto, pdc.pdcCampo ");
        corpoBuilder.append("FROM PerguntaDadosCadastrais pdc ");

        corpoBuilder.append("WHERE pdc.pdcStatus ").append(criaClausulaNomeada("pdcStatus", pdcStatus));

        if (!TextHelper.isNull(pdcGrupo)) {
            corpoBuilder.append(" AND pdc.pdcGrupo ").append(criaClausulaNomeada("pdcGrupo", pdcGrupo));
        }

        if (!TextHelper.isNull(pdcNumero)) {
            corpoBuilder.append(" AND pdc.pdcNumero ").append(criaClausulaNomeada("pdcNumero", pdcNumero));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("pdcStatus", pdcStatus, query);

        if (!TextHelper.isNull(pdcGrupo)) {
            defineValorClausulaNomeada("pdcGrupo", pdcGrupo, query);
        }

        if (!TextHelper.isNull(pdcNumero)) {
            defineValorClausulaNomeada("pdcNumero", pdcNumero, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.PDC_GRUPO,
                Columns.PDC_NUMERO,
                Columns.PDC_STATUS,
                Columns.PDC_TEXTO,
                Columns.PDC_CAMPO
        };
    }
}
