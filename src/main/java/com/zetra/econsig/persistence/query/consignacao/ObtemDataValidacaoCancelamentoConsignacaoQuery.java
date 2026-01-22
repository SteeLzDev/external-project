package com.zetra.econsig.persistence.query.consignacao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemDataValidacaoCancelamentoConsignacaoQuery</p>
 * <p>Description: Obtem a data da ADE para validação do limite de cancelamento, somente se a ADE for a mais atual do servidor
 * para o mesmo serviço</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemDataValidacaoCancelamentoConsignacaoQuery extends HQuery {

    public String adeCodigo;
    public List<String> sadCodigos;
    
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append(" SELECT ade.adeData");
        corpoBuilder.append(" FROM AutDesconto ade ");
        corpoBuilder.append(" INNER JOIN ade.verbaConvenio vco");
        corpoBuilder.append(" INNER JOIN vco.convenio cnv");
        corpoBuilder.append(" INNER JOIN cnv.servico svc");
        corpoBuilder.append(" WHERE ade.adeCodigo ").append(criaClausulaNomeada("adeCodigo", adeCodigo));
        corpoBuilder.append(" AND NOT EXISTS (SELECT 1 FROM AutDesconto ade2 ");
        corpoBuilder.append(" INNER JOIN ade2.verbaConvenio vco2");
        corpoBuilder.append(" INNER JOIN vco2.convenio cnv2");
        corpoBuilder.append(" INNER JOIN cnv2.servico svc2");
        corpoBuilder.append(" WHERE ade2.registroServidor.rseCodigo = ade.registroServidor.rseCodigo");
        corpoBuilder.append(" AND svc2.svcCodigo = svc.svcCodigo");
        corpoBuilder.append(" AND ade2.adeData > ade.adeData)");
        
        if (sadCodigos != null && !sadCodigos.isEmpty()) {
            corpoBuilder.append(" AND ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));
        }
    
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        query.setMaxResults(1);
        
        if (!TextHelper.isNull(adeCodigo)) {
            defineValorClausulaNomeada("adeCodigo", adeCodigo, query);
        }
        
        if (sadCodigos != null && !sadCodigos.isEmpty()) {
            defineValorClausulaNomeada("sadCodigos", sadCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_DATA
        };
    }
}
