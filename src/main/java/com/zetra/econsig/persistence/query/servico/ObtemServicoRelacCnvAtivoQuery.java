package com.zetra.econsig.persistence.query.servico;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemServicoRelacCnvAtivoQuery</p>
 * <p>Description: Obtém o serviço relacionado pela natureza informada
 * por parâmetro que possua convênio ativo para a consignatária e
 * órgãos informados.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemServicoRelacCnvAtivoQuery extends HQuery {

    public String svcCodigoOrigem;
    public String csaCodigo;
    public String orgCodigo;
    public String tntCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select svc.svcCodigo ");
        corpoBuilder.append(" from RelacionamentoServico rsv");
        corpoBuilder.append(" inner join rsv.servicoBySvcCodigoDestino svc");
        corpoBuilder.append(" inner join svc.convenioSet cnv");

        corpoBuilder.append(" where cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
        corpoBuilder.append(" and rsv.tipoNatureza.tntCodigo ").append(criaClausulaNomeada("tntCodigo", tntCodigo));

        if (!TextHelper.isNull(svcCodigoOrigem)) {
            corpoBuilder.append(" and rsv.servicoBySvcCodigoOrigem.svcCodigo ").append(criaClausulaNomeada("svcCodigoOrigem", svcCodigoOrigem));
        }
        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }
        if (!TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" and cnv.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }

        corpoBuilder.append(" GROUP BY svc.svcCodigo");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("tntCodigo", tntCodigo, query);

        if (!TextHelper.isNull(svcCodigoOrigem)) {
            defineValorClausulaNomeada("svcCodigoOrigem", svcCodigoOrigem, query);
        }
        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }
        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SVC_CODIGO
        };
    }
}
