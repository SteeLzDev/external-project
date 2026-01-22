package com.zetra.econsig.persistence.query.admin;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaPostoRegistroServidorQuery</p>
 * <p>Description: Listagem de Postos</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaPostoRegistroServidorQuery extends HQuery {

    public boolean count = false;

    public String posCodigo = null;
    public String posIdentificador = null;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {

        String corpo = "";

        if (!count) {
            corpo = "select posto.posCodigo, posto.posDescricao, posto.posIdentificador, posto.posPercTxUso, posto.posPercTxUsoCond, posto.posVlrSoldo "
                  + "from PostoRegistroServidor posto "
                  + "where 1=1 ";

            if (!TextHelper.isNull(posCodigo)) {
                corpo += " and posto.posCodigo " + criaClausulaNomeada("posCodigo", posCodigo);
            }
            if (!TextHelper.isNull(posIdentificador)) {
                corpo += " and posto.posIdentificador " + criaClausulaNomeada("posIdentificador", posIdentificador);
            }

            corpo += " order by posto.posVlrSoldo desc";
        } else {
            corpo = "select count(*) from PostoRegistroServidor posto";
        }

        Query<Object[]> query = instanciarQuery(session, corpo);

        if (!count) {
            if (!TextHelper.isNull(posCodigo)) {
                defineValorClausulaNomeada("posCodigo", posCodigo, query);
            }
            if (!TextHelper.isNull(posIdentificador)) {
                defineValorClausulaNomeada("posIdentificador", posIdentificador, query);
            }
        }

        return query;

    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.POS_CODIGO,
                Columns.POS_DESCRICAO,
                Columns.POS_IDENTIFICADOR,
                Columns.POS_PERC_TAXA_USO,
                Columns.POS_PERC_TAXA_USO_COND,
                Columns.POS_VALOR_SOLDO
                };
    }
}
