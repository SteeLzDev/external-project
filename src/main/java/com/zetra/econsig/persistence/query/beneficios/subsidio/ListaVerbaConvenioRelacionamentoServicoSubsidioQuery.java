package com.zetra.econsig.persistence.query.beneficios.subsidio;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaVerbaConvenioRelacionamentoServicoSubsidioQuery</p>
 * <p>Description: Lista o tipo de naturela, serviço destino de uma relação de serviço</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaVerbaConvenioRelacionamentoServicoSubsidioQuery extends HQuery {

    public String svcCodigo;
    public String csaCodigo;
    public String orgCodigo;
    public List<String> tntCodigos;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT ");
        corpoBuilder.append("vco.vcoCodigo, ");
        corpoBuilder.append("rel.tipoNatureza.tntCodigo ");

        corpoBuilder.append("FROM  RelacionamentoServico rel ");
        corpoBuilder.append("INNER JOIN rel.servicoBySvcCodigoDestino svc ");
        corpoBuilder.append("INNER JOIN svc.convenioSet cnv ");
        corpoBuilder.append("INNER JOIN cnv.verbaConvenioSet vco ");

        corpoBuilder.append("WHERE 1 = 1");
        corpoBuilder.append("AND rel.servicoBySvcCodigoOrigem.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo)).append(" ");
        corpoBuilder.append("AND cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo)).append(" ");
        corpoBuilder.append("AND cnv.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo)).append(" ");
        corpoBuilder.append("AND rel.tipoNatureza.tntCodigo ").append(criaClausulaNomeada("tntCodigo", tntCodigos)).append(" ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("tntCodigo", tntCodigos, query);
        defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        defineValorClausulaNomeada("orgCodigo", orgCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.VCO_CODIGO,
                Columns.TNT_CODIGO
        };
    }
}
