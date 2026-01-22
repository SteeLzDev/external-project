package com.zetra.econsig.persistence.query.parametro;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaParamSvcCseQuery</p>
 * <p>Description: Listagem dos parâmetros de serviço de consignante cadastrados no sistema.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaParamSvcCseQuery extends HQuery {
    
    public String svcCodigo;
    public String tpsCodigo;
    public String pseVlr;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                "pse.consignante.cseCodigo, " +
                "pse.servico.svcCodigo, " +
                "pse.tipoParamSvc.tpsCodigo, " +
                "pse.pseCodigo, " +
                "pse.pseVlr, " +
                "pse.pseVlrRef ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from ParamSvcConsignante pse ");
        corpoBuilder.append(" where 1 = 1 ");
        
        if (!TextHelper.isNull(svcCodigo)) {
            corpoBuilder.append(" and pse.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));            
        }              
        if (!TextHelper.isNull(tpsCodigo)) {
            corpoBuilder.append(" and pse.tipoParamSvc.tpsCodigo ").append(criaClausulaNomeada("tpsCodigo", tpsCodigo));
        }        
        if (!TextHelper.isNull(pseVlr)) {
            corpoBuilder.append(" and pse.pseVlr ").append(criaClausulaNomeada("pseVlr", pseVlr));            
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        
        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }
        if (!TextHelper.isNull(tpsCodigo)) {
            defineValorClausulaNomeada("tpsCodigo", tpsCodigo, query);
        }
        if (!TextHelper.isNull(pseVlr)) {
            defineValorClausulaNomeada("pseVlr", pseVlr, query);
        }
        
        return query;
    }
    
    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.PSE_CSE_CODIGO,
                Columns.PSE_SVC_CODIGO,                                     
                Columns.PSE_TPS_CODIGO,
                Columns.PSE_CODIGO,
                Columns.PSE_VLR,
                Columns.PSE_VLR_REF
        };
    }
}
