package com.zetra.econsig.persistence.query.senhaexterna;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaParamSenhaExternaQuery</p>
 * <p>Description: Listagem de parâmetros de senha externa.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaParamSenhaExternaQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select psx.psxChave, psx.psxValor from ParamSenhaExterna psx order by psx.psxChave";
        return instanciarQuery(session, corpo);
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.PSX_CHAVE,
                Columns.PSX_VALOR
        };
    }
}
