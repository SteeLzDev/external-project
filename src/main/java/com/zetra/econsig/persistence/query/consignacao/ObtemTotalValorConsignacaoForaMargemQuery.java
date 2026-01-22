package com.zetra.econsig.persistence.query.consignacao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ObtemTotalValorConsignacaoQuery</p>
 * <p>Description: Totaliza o valor dos contratos para um servidor de acordo
 * com os demais parâmetros informados</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemTotalValorConsignacaoForaMargemQuery extends HQuery {

    public String rseCodigo;
    public List<String> sadCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select sum(ade.adeVlr) ");
        corpoBuilder.append("from AutDesconto ade ");
        corpoBuilder.append("inner join ade.verbaConvenio vco ");
        corpoBuilder.append("inner join vco.convenio cnv ");
        corpoBuilder.append("inner join cnv.servico svc ");
        corpoBuilder.append("inner join svc.paramSvcConsignatariaSet psc ");

        corpoBuilder.append("where ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        corpoBuilder.append(" and ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));
        corpoBuilder.append(" and psc.consignataria.csaCodigo = cnv.consignataria.csaCodigo ");
        corpoBuilder.append(" and psc.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_EXIBIR_COMO_VALOR_FORA_DA_MARGEM).append("' ");
        corpoBuilder.append(" and psc.pscVlr = '").append(CodedValues.PSC_BOOLEANO_SIM).append("' ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        defineValorClausulaNomeada("sadCodigos", sadCodigos, query);

        return query;
    }
}
