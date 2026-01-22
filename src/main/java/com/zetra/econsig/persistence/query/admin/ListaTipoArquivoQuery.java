package com.zetra.econsig.persistence.query.admin;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaTipoArquivoQuery</p>
 * <p>Description: Listagem de Tipos de Arquivos</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaTipoArquivoQuery extends HQuery {

    public String tarUploadSer;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                       "tar.tarCodigo, " +
                       "tar.tarDescricao, " +
                       "tar.tarQtdDiasLimpeza " +
                       "from TipoArquivo tar " +
                       "where 1=1 ";

        if (!TextHelper.isNull(tarUploadSer)) {
            corpo += "and tar.tarUploadSer " + criaClausulaNomeada("tarUploadSer", tarUploadSer);
        }

        Query<Object[]> query = instanciarQuery(session, corpo);

        if (!TextHelper.isNull(tarUploadSer)) {
            defineValorClausulaNomeada("tarUploadSer", tarUploadSer, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
    	return new String[] {
    			Columns.TAR_CODIGO,
                Columns.TAR_DESCRICAO,
                Columns.TAR_QTD_DIAS_LIMPEZA
    	};
    }
}
