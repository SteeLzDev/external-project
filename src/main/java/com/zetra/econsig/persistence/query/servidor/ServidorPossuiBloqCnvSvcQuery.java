package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ServidorPossuiBloqCnvSvcQuery</p>
 * <p>Description: Verifica se o servidor possui bloqueio (qtd contratos = 0)
 * no convênio ou serviço da verba passada por parâmetro.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ServidorPossuiBloqCnvSvcQuery extends HQuery {

    public String rseCodigo;
    public String vcoCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select count(*) ");
        corpoBuilder.append(" from VerbaConvenio vco ");
        corpoBuilder.append(" inner join vco.convenio cnv ");
        corpoBuilder.append(" inner join cnv.servico svc ");
        corpoBuilder.append(" inner join svc.naturezaServico nse ");

        corpoBuilder.append(" left outer join cnv.paramConvenioRegistroSerSet pcr WITH ");
        corpoBuilder.append(" pcr.tpsCodigo = '").append(CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO).append("' and ");
        corpoBuilder.append(" pcr.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));

        corpoBuilder.append(" left outer join svc.paramServicoRegistroSerSet psr WITH ");
        corpoBuilder.append(" psr.tpsCodigo = '").append(CodedValues.TPS_NUM_CONTRATOS_POR_SERVICO).append("' and ");
        corpoBuilder.append(" psr.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));

        corpoBuilder.append(" left outer join nse.paramNseRegistroSerSet pnr WITH ");
        corpoBuilder.append(" pnr.tpsCodigo = '").append(CodedValues.TPS_NUM_CONTRATOS_POR_NATUREZA_SERVICO).append("' and ");
        corpoBuilder.append(" pnr.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));

        corpoBuilder.append(" where vco.vcoCodigo ").append(criaClausulaNomeada("vcoCodigo", vcoCodigo));

        // Verifica se existe bloqueio de convênio ou de serviço
        corpoBuilder.append(" and (coalesce(pcr.pcrVlr, '99') = '0' or coalesce(psr.psrVlr, '99') = '0' or coalesce(pnr.pnrVlr, '99') = '0') ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        defineValorClausulaNomeada("vcoCodigo", vcoCodigo, query);

        return query;
    }
}
