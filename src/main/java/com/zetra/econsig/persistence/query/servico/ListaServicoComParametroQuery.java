package com.zetra.econsig.persistence.query.servico;

import java.util.List;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaServicoComParametroQuery</p>
 * <p>Description: Busca os serviços que tenham o parâmetro de serviço identificado pelo código
 * "tpsCodigo" com valores iguais a "pseVlrs" (ou com parâmetro null, caso o flag "selectNull"
 * seja igual a true). O padrão é que os serviços estejam ativos e que eles possuam algum
 * convênio ativo, com órgão e consignatária ativos.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaServicoComParametroQuery extends HQuery {

    public String tpsCodigo;
    public String orgCodigo;
    public String csaCodigo;
    public String corCodigo;
    public List<String> pseVlrs;
    public String svcCodigo;
    public boolean selectNull;
    public String nseCodigo;
    public boolean ativos = true;
    public String entidade = AcessoSistema.ENTIDADE_CSE;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final String corpo =
            "select distinct svc.svcCodigo, " +
            "   svc.svcIdentificador, " +
            "   svc.svcDescricao, " +
            "   nse.nseCodigo, " +
            "   nse.nseDescricao ";

        final StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append("from Servico svc ");
        corpoBuilder.append("inner join svc.naturezaServico nse ");
        corpoBuilder.append("inner join svc.convenioSet cnv ");
        corpoBuilder.append("inner join cnv.consignataria csa ");
        if(!TextHelper.isNull(corCodigo)) {
            corpoBuilder.append("inner join cnv.correspondenteConvenioSet corCnv ");
        }
        corpoBuilder.append("inner join cnv.orgao org ");
        corpoBuilder.append("left outer join svc.paramSvcConsignanteSet pse WITH ");
        corpoBuilder.append("pse.tipoParamSvc.tpsCodigo ").append(criaClausulaNomeada("tpsCodigo", tpsCodigo));
        if (entidade.equals(AcessoSistema.ENTIDADE_CSA)) {
            corpoBuilder.append("left outer join svc.paramSvcConsignatariaSet psc WITH ");
            corpoBuilder.append("psc.tipoParamSvc.tpsCodigo ").append(criaClausulaNomeada("tpsCodigoCsa", tpsCodigo));
        }
        corpoBuilder.append(" where 1=1 ");

        if (ativos) {
            if(!TextHelper.isNull(corCodigo)) {
                corpoBuilder.append(" and corCnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
            } else {
                corpoBuilder.append(" and cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
            }
            corpoBuilder.append(" and (svc.svcAtivo IS NULL OR svc.svcAtivo = ").append(CodedValues.STS_ATIVO).append(")");
            corpoBuilder.append(" and (csa.csaAtivo IS NULL OR csa.csaAtivo = ").append(CodedValues.STS_ATIVO).append(")");
            corpoBuilder.append(" and (org.orgAtivo IS NULL OR org.orgAtivo = ").append(CodedValues.STS_ATIVO).append(")");
        }

        if (!TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" and org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }
        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }
        if (!TextHelper.isNull(corCodigo)) {
            corpoBuilder.append(" and corCnv.correspondente.corCodigo ").append(criaClausulaNomeada("corCodigo", corCodigo));
        }
        if ((pseVlrs != null) && !pseVlrs.isEmpty()) {
            if (selectNull) {
                corpoBuilder.append(" and (pse.pseVlr IS NULL OR pse.pseVlr ").append(criaClausulaNomeada("pseVlr", pseVlrs)).append(")");
                if (entidade.equals(AcessoSistema.ENTIDADE_CSA)) {
                    corpoBuilder.append(" or (psc.pscVlr IS NULL OR psc.pscVlr ").append(criaClausulaNomeada("pscVlr", pseVlrs)).append(")");
                }
            } else {
                corpoBuilder.append(" and pse.pseVlr ").append(criaClausulaNomeada("pseVlr", pseVlrs));
                if (entidade.equals(AcessoSistema.ENTIDADE_CSA)) {
                    corpoBuilder.append(" or psc.pscVlr ").append(criaClausulaNomeada("pscVlr", pseVlrs));
                }
            }
        } else if (selectNull) {
            corpoBuilder.append(" and (pse.pseVlr IS NULL)");
            if (entidade.equals(AcessoSistema.ENTIDADE_CSA)) {
                corpoBuilder.append(" or (psc.pscVlr IS NULL)");
            }
        }

        if (!TextHelper.isNull(nseCodigo)) {
            corpoBuilder.append(" and nse.nseCodigo ").append(criaClausulaNomeada("nseCodigo", nseCodigo));
        }
        if (!TextHelper.isNull(svcCodigo)) {
            corpoBuilder.append(" and svc.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }

        corpoBuilder.append(" order by svc.svcDescricao");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("tpsCodigo", tpsCodigo, query);
        if (entidade.equals(AcessoSistema.ENTIDADE_CSA)) {
            defineValorClausulaNomeada("tpsCodigoCsa", tpsCodigo, query);
        }
        if ((pseVlrs != null) && !pseVlrs.isEmpty()) {
            defineValorClausulaNomeada("pseVlr", pseVlrs, query);
            if (entidade.equals(AcessoSistema.ENTIDADE_CSA)) {
                defineValorClausulaNomeada("pscVlr", pseVlrs, query);
            }
        }
        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }
        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }
        if (!TextHelper.isNull(corCodigo)) {
            defineValorClausulaNomeada("corCodigo", corCodigo, query);
        }
        if (!TextHelper.isNull(nseCodigo)) {
            defineValorClausulaNomeada("nseCodigo", nseCodigo, query);
        }
        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SVC_CODIGO,
                Columns.SVC_IDENTIFICADOR,
                Columns.SVC_DESCRICAO,
                Columns.NSE_CODIGO,
                Columns.NSE_DESCRICAO
        };
    }
}
