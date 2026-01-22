package com.zetra.econsig.persistence.query.seguranca;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarOperacoesLiberacaoMargemConfirmadasQuery</p>
 * <p>Description: Listar dados das operações de liberação de margem confirmadas para realizar a análise de notificação/bloqueio</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarOperacoesLiberacaoMargemConfirmadasQuery extends HQuery {

    public AcessoSistema responsavel;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select distinct olm.usuCodigo ");
        corpoBuilder.append(", olm.csaCodigo ");
        corpoBuilder.append("from OperacaoLiberaMargem olm ");
        corpoBuilder.append("where olm.olmBloqueio = 'N' ");
        corpoBuilder.append("and olm.olmConfirmada = 'S' ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.OLM_USU_CODIGO,
                Columns.OLM_CSA_CODIGO
        };
    }

}
