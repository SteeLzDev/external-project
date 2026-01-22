package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemConvenioMesmoSvcTransfQuery</p>
 * <p>Description: Retorna os dados para a transferência de contratos</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemConvenioMesmoSvcTransfQuery extends HNativeQuery {

    // Código do órgão do novo servidor
    public String orgCodigo;
    // Código do contrato a ser transferido
    public String adeCodigo;

    @Override
    public void setCriterios(TransferObject criterio) {
        orgCodigo = (String) criterio.getAttribute(Columns.ORG_CODIGO);
        adeCodigo = (String) criterio.getAttribute(Columns.ADE_CODIGO);
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select ");
        corpoBuilder.append(" ade.ade_codigo as ADE_CODIGO,");
        corpoBuilder.append(" ade.ade_numero as ADE_NUMERO,");
        corpoBuilder.append(" cnv.csa_codigo as CSA_CODIGO,");
        corpoBuilder.append(" cnv.svc_codigo AS SVC_CODIGO,");
        corpoBuilder.append(" vco.vco_codigo as VCO_CODIGO,");
        corpoBuilder.append(" vco2.vco_codigo as VCO_CODIGO_NOVO,");
        corpoBuilder.append(" cnv2.cnv_cod_verba as VERBA_NOVO");

        corpoBuilder.append(" from tb_aut_desconto ade");
        corpoBuilder.append(" inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo)");
        corpoBuilder.append(" inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo)");

        // Localiza verba convênio do mesmo serviço
        corpoBuilder.append(" left outer join tb_convenio cnv2");
        corpoBuilder.append(" on (cnv2.csa_codigo = cnv.csa_codigo");
        corpoBuilder.append(" and cnv2.svc_codigo = cnv.svc_codigo");
        corpoBuilder.append(" and cnv2.org_codigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo)).append(")");
        corpoBuilder.append(" left outer join tb_verba_convenio vco2 on (cnv2.cnv_codigo = vco2.cnv_codigo)");

        corpoBuilder.append(" where ade.ade_codigo ").append(criaClausulaNomeada("adeCodigo", adeCodigo));

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        query.setMaxResults(1);

        defineValorClausulaNomeada("adeCodigo", adeCodigo, query);
        defineValorClausulaNomeada("orgCodigo", orgCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "ADE_CODIGO",
                "ADE_NUMERO",
                "CSA_CODIGO",
                "SVC_CODIGO",
                "VCO_CODIGO",
                "VCO_CODIGO_NOVO",
                "VERBA_NOVO"

        };
    }
}
