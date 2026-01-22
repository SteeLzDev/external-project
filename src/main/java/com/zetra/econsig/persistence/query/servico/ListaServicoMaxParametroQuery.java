package com.zetra.econsig.persistence.query.servico;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaServicoMaxParametroQuery</p>
 * <p>Description: Busca o serviço que tenha o maior parâmetro de "tpsCodigo"
 * da natureza de serviço "nseCodigo", podendo ser somento os serviços, órgãos e consignatárias ativos.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaServicoMaxParametroQuery extends HQuery {

    public String tpsCodigo;
    public String nseCodigo;
    public boolean ativos = true;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final String corpo =
            "select svc.svcCodigo, "+
            "   svc.svcIdentificador, " +
            "   svc.svcDescricao, " +
            "   pse.pseVlr ";

        final StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append("from Servico svc ");
        corpoBuilder.append("inner join svc.convenioSet cnv ");
        corpoBuilder.append("inner join cnv.consignataria csa ");
        corpoBuilder.append("inner join cnv.orgao org ");
        corpoBuilder.append("left outer join svc.paramSvcConsignanteSet pse WITH ");
        corpoBuilder.append("pse.tipoParamSvc.tpsCodigo ").append(criaClausulaNomeada("tpsCodigo", tpsCodigo));
        corpoBuilder.append(" where 1=1 ");

        if (ativos) {
            corpoBuilder.append(" and cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
            corpoBuilder.append(" and (svc.svcAtivo IS NULL OR svc.svcAtivo = ").append(CodedValues.STS_ATIVO).append(")");
            corpoBuilder.append(" and (csa.csaAtivo IS NULL OR csa.csaAtivo = ").append(CodedValues.STS_ATIVO).append(")");
            corpoBuilder.append(" and (org.orgAtivo IS NULL OR org.orgAtivo = ").append(CodedValues.STS_ATIVO).append(")");
        }

        if (!TextHelper.isNull(nseCodigo)) {
            corpoBuilder.append(" and svc.naturezaServico.nseCodigo ").append(criaClausulaNomeada("nseCodigo", nseCodigo));
        }

        corpoBuilder.append(" order by pse.pseVlr desc ");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(tpsCodigo)) {
            defineValorClausulaNomeada("tpsCodigo", tpsCodigo, query);
        }

        if (!TextHelper.isNull(nseCodigo)) {
            defineValorClausulaNomeada("nseCodigo", nseCodigo, query);
        }

        query.setMaxResults(1);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SVC_CODIGO,
                Columns.SVC_IDENTIFICADOR,
                Columns.SVC_DESCRICAO,
                Columns.PSE_VLR
        };
    }
}
