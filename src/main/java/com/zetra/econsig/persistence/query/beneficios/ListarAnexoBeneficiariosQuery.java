package com.zetra.econsig.persistence.query.beneficios;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarAnexoBeneficiariosQuery</p>
 * <p>Description: Listagem de Anexo de beneficiários</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 * */
public class ListarAnexoBeneficiariosQuery extends HQuery {

    public String bfcCodigo = null;

    //Executar count ao invez da query
    public Boolean count = false;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder();

        if(!count) {
            corpo.append("SELECT distinct abf.abfNome, "
                    + "tar.tarDescricao, "
                    + "abf.abfDescricao, "
                    + "abf.abfData, "
                    + "abf.abfDataValidade ");
        }else {
            corpo.append("SELECT count(*) as total ");
        }
        corpo.append("FROM AnexoBeneficiario abf ");
        corpo.append("INNER JOIN abf.tipoArquivo tar ");
        corpo.append("INNER JOIN abf.beneficiario bfc ");
        corpo.append("WHERE bfc.bfcCodigo = :bfcCodigo ");

        corpo.append("ORDER BY abf.abfData");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        if (!TextHelper.isNull(bfcCodigo)) {
            defineValorClausulaNomeada("bfcCodigo", bfcCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ABF_NOME,
                Columns.TAR_DESCRICAO,
                Columns.ABF_DESCRICAO,
                Columns.ABF_DATA,
                Columns.ABF_DATA_VALIDADE
        };
    }

}
