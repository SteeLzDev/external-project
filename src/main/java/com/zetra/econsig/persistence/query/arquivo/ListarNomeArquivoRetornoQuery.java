package com.zetra.econsig.persistence.query.arquivo;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarArquivoRetornoQuery</p>
 * <p>Description: Lista linhas de arquivo de retorno</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarNomeArquivoRetornoQuery extends HQuery {

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select art.nomeArquivo ");
        corpoBuilder.append("from ArquivoRetorno art ");
        corpoBuilder.append("group by art.nomeArquivo ");
        corpoBuilder.append("order by count(*) desc ");

        return instanciarQuery(session, corpoBuilder.toString());
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ART_NOME_ARQUIVO
        };
    }

}
