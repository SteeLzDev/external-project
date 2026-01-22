package com.zetra.econsig.persistence.query.margem;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaMargemNaturezaServicoQuery</p>
 * <p>Description: Listagem de margens.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$ $Revision$
 * $Date$
 */
public class ListaMargemNaturezaServicoQuery extends HNativeQuery {

    public String marCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT nse.nse_codigo, nse.nse_descricao ");
        corpoBuilder.append("from tb_servico svc ");
        corpoBuilder.append("INNER JOIN tb_natureza_servico nse on (svc.nse_codigo = nse.nse_codigo) ");
        corpoBuilder.append("INNER JOIN tb_param_svc_consignante pse on (svc.svc_codigo = pse.svc_codigo) ");
        corpoBuilder.append("WHERE svc.svc_ativo = ").append(CodedValues.SCV_ATIVO);
        corpoBuilder.append("  AND pse.tps_codigo = '").append(CodedValues.TPS_INCIDE_MARGEM).append("' ");
        corpoBuilder.append("  AND pse.pse_vlr ").append(criaClausulaNomeada("marCodigo", marCodigo));
        corpoBuilder.append("  AND EXISTS (");
        corpoBuilder.append("     select 1 from tb_convenio cnv ");
        corpoBuilder.append("     where cnv.svc_codigo = svc.svc_codigo ");
        corpoBuilder.append("       and cnv.scv_codigo = '").append(CodedValues.SCV_ATIVO).append("'");
        corpoBuilder.append(") ");
        corpoBuilder.append("GROUP BY nse.nse_codigo, nse.nse_descricao");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("marCodigo", marCodigo, query);
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.NSE_CODIGO,
                Columns.NSE_DESCRICAO
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }
}
