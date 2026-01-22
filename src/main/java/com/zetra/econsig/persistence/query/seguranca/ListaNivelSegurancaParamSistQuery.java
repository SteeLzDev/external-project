package com.zetra.econsig.persistence.query.seguranca;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaNivelSegurancaParamSistQuery</p>
 * <p>Description: Lista as parametrizações de sistema que definem 
 * os níveis de segurança.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaNivelSegurancaParamSistQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT nsg.nsgCodigo, nsg.nsgDescricao, tpc.tpcCodigo, tpc.tpcDescricao, tpc.tpcDominio, nsp.nspVlrEsperado, coalesce(psi.psiVlr, tpc.tpcVlrDefault) ");
        corpoBuilder.append("FROM NivelSegurancaParamSist nsp ");
        corpoBuilder.append("INNER JOIN nsp.nivelSeguranca nsg ");
        corpoBuilder.append("INNER JOIN nsp.tipoParamSistConsignante tpc ");
        corpoBuilder.append("LEFT OUTER JOIN tpc.paramSistConsignanteSet psi ");
        corpoBuilder.append("ORDER BY 1, 4");
        
        return instanciarQuery(session, corpoBuilder.toString());
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.NSG_CODIGO,
                Columns.NSG_DESCRICAO,
                Columns.TPC_CODIGO,
                Columns.TPC_DESCRICAO,
                Columns.TPC_DOMINIO,
                Columns.NSP_VLR_ESPERADO,
                Columns.PSI_VLR
        };
    }
}
