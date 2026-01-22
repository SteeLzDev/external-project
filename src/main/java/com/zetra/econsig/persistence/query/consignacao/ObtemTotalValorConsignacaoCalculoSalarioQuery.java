package com.zetra.econsig.persistence.query.consignacao;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.margem.CasamentoMargem;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ObtemTotalValorConsignacaoCalculoSalarioQuery</p>
 * <p>Description: Totaliza o valor dos contratos abertos para um servidor
 * que incidem na margem informada e nas margens casadas que afetam esta margem</p>
 * <p>Copyright: Copyright (c) 2002-2025</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemTotalValorConsignacaoCalculoSalarioQuery extends HQuery {

    public String rseCodigo;
    public Short marCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final List<Short> marCodigos = CasamentoMargem.getInstance().getMargemOrigemAfetaDestino(marCodigo);
        final StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select sum(coalesce(ade.adeVlrFolha, ade.adeVlr)) ");
        corpoBuilder.append("from AutDesconto ade ");
        corpoBuilder.append("inner join ade.verbaConvenio vco ");
        corpoBuilder.append("inner join vco.convenio cnv ");
        corpoBuilder.append("where ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        corpoBuilder.append("  and ade.adeCodigo NOT IN (:adeCodigosExceto) ");
        corpoBuilder.append("  and coalesce(ade.adeIncMargem, ").append(CodedValues.INCIDE_MARGEM_SIM).append(") ").append(criaClausulaNomeada("marCodigos", marCodigos));
    
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        defineValorClausulaNomeada("marCodigos", marCodigos, query);
        defineValorClausulaNomeada("adeCodigosExceto", CodedValues.SAD_CODIGOS_INATIVOS, query);

        return query;
    }
}
