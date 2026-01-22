package com.zetra.econsig.persistence.query.transferencia;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ListaServidorPorEmailQuery</p>
 * <p>Description: Retornar informações de servidores de acordo com o filtro.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemTotalConsignacaoSemConvenioTransfQuery extends HQuery {

    public String rseCodigo;
    public String orgCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select count(*) ");
        corpoBuilder.append("from AutDesconto ade ");
        corpoBuilder.append("inner join ade.verbaConvenio vco ");
        corpoBuilder.append("inner join vco.convenio cnv ");
        corpoBuilder.append("where ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));

        corpoBuilder.append(" and not exists (");
        corpoBuilder.append(" select 1 from VerbaConvenio vco2");
        corpoBuilder.append(" inner join vco2.convenio cnv2");
        corpoBuilder.append(" where cnv2.consignataria.csaCodigo = cnv.consignataria.csaCodigo");
        corpoBuilder.append("   and cnv2.servico.svcCodigo = cnv.servico.svcCodigo");
        corpoBuilder.append("   and cnv2.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        corpoBuilder.append("   and cnv2.statusConvenio.scvCodigo ").append(criaClausulaNomeada("scvCodigo", CodedValues.SCV_ATIVO));
        corpoBuilder.append("   and nullif(substituir(trim(cnv2.cnvCodVerba), '0', ''), '') is not null ");
        corpoBuilder.append(" )");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        defineValorClausulaNomeada("scvCodigo", CodedValues.SCV_ATIVO, query);

        return query;
    }
}