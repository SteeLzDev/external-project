package com.zetra.econsig.persistence.query.vinculo;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignatariasExisteCnvVinculoRegistroServidorQuery</p>
 * <p>Description: Listagem de consignatárias que possuem vinculos com o registro servidor para novos vinculos</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignatariasExisteCnvVinculoRegistroServidorQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT DISTINCT csa.csaCodigo, csa.csaIdentificador, csa.csaNome, csa.csaNomeAbrev, csa.csaEmail, ");
        corpoBuilder.append("CASE WHEN pcs.pcsVlr IS NULL THEN 'N' else pcs.pcsVlr END as VALOR_PARAM, ");
        corpoBuilder.append("CASE WHEN pcsa.pcsVlr IS NULL THEN NULL else pcsa.pcsVlr END as VALOR_PARAM_95 ");
        corpoBuilder.append("FROM Consignataria csa ");
        corpoBuilder.append("INNER JOIN csa.convenioVinculoRegistrosSet cvr ");
        corpoBuilder.append("LEFT OUTER JOIN csa.paramConsignatariaSet pcs with pcs.tpaCodigo='").append(CodedValues.TPA_INFO_VINC_BLOQ_PADRAO).append("' ");
        corpoBuilder.append("LEFT OUTER JOIN csa.paramConsignatariaSet pcsa with pcsa.tpaCodigo='").append(CodedValues.TPA_EMAIL_CSA_NOTIFICACAO_NOVO_VINCULO).append("'");
        return instanciarQuery(session, corpoBuilder.toString());
    }

    @Override
    protected String[] getFields() {
        return new String[]{
                Columns.CSA_CODIGO,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_NOME,
                Columns.CSA_NOME_ABREV,
                Columns.CSA_EMAIL,
                "VALOR_PARAM",
                "VALOR_PARAM_95"
        };
    }
}
