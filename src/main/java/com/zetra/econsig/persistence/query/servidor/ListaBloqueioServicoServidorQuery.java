package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaBloqueioServicoServidorQuery</p>
 * <p>Description: Listagem de serviços do servidor, juntamente com os
 * bloqueios de serviço, caso existam.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaBloqueioServicoServidorQuery extends HQuery {

    public String rseCodigo;
    public String svcCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                "svc.svcCodigo, " +
                "svc.svcIdentificador, " +
                "svc.svcDescricao, " +
                "psr.psrVlr, " +
                "psr.psrObs, " +
                "psr.psrAlteradoPeloServidor ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from Servico svc ");
        corpoBuilder.append(" left outer join svc.paramServicoRegistroSerSet psr WITH ");
        corpoBuilder.append(" psr.tpsCodigo = '").append(CodedValues.TPS_NUM_CONTRATOS_POR_SERVICO).append("' and ");
        corpoBuilder.append(" psr.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));

        if (!TextHelper.isNull(svcCodigo)) {
            corpoBuilder.append(" where svc.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }

        corpoBuilder.append(" order by svc.svcDescricao");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SVC_CODIGO,
                Columns.SVC_IDENTIFICADOR,
                Columns.SVC_DESCRICAO,
                Columns.PSR_VLR,
                Columns.PSR_OBS,
                Columns.PSR_ALTERADO_PELO_SERVIDOR
        };
    }
}
