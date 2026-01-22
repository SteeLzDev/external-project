package com.zetra.econsig.persistence.query.boleto;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaBoletoServidorExpiradoQuery</p>
 * <p>Description: Lista boletos do servidor que já expiraram e devem ser removidos</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaBoletoServidorExpiradoQuery extends HQuery {

    public int diasAposEnvio;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select bos.bosCodigo ");
        corpoBuilder.append("from BoletoServidor bos ");
        corpoBuilder.append("where bos.bosDataExclusao is null ");
        corpoBuilder.append("and add_day(bos.bosDataUpload, :dias) < current_date() ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("dias", diasAposEnvio, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.BOS_CODIGO
        };
    }
}
