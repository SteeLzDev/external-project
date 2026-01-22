package com.zetra.econsig.persistence.query.convenio;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConveniosComAdeAtivosQuery</p>
 * <p>Description: lista código de entidades entre as presentes no parâmetro 'codigos'
 *                 ligadas a convênios que, por sua vez, estão ligaods a contratos ativos.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConveniosComAdeAtivosQuery extends HQuery {
    public String csaCodigo;
    public String orgCodigo;
    public String svcCodigo;
    public List<String> codigos;

    @Override
    /**
     * lista código de entidades entre as presentes no parâmetro 'codigos'
     * ligadas a convênios que, por sua vez, estão ligaods a contratos ativos.
     */
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        if (!TextHelper.isNull(csaCodigo)) {
            corpo = "select cnv.orgao.orgCodigo ";
        } else {
            corpo = "select cnv.consignataria.csaCodigo ";
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from Convenio cnv");
        corpoBuilder.append(" inner join cnv.verbaConvenioSet vco");
        corpoBuilder.append(" inner join vco.autDescontoSet ade");
        corpoBuilder.append(" inner join cnv.servico svc");

        corpoBuilder.append(" where 1=1 ");

        if (!TextHelper.isNull(svcCodigo)) {
            corpoBuilder.append(" AND cnv.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo",svcCodigo));
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo",csaCodigo));

            if(codigos != null && !codigos.isEmpty()) {
                corpoBuilder.append(" AND cnv.orgao.orgCodigo ").append(criaClausulaNomeada("codigos",codigos));
            }
        }

        if (!TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" AND cnv.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo",orgCodigo));

            if(codigos != null && !codigos.isEmpty()) {
                corpoBuilder.append(" AND cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("codigos",codigos));
            }
        }

        corpoBuilder.append(" AND cnv.cnvCodVerba is not null ");
        corpoBuilder.append(" AND ade.statusAutorizacaoDesconto.sadCodigo ").append(" NOT IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_INATIVOS, "','")).append("')");

        corpoBuilder.append(" GROUP BY ").append((!TextHelper.isNull(csaCodigo)) ? "cnv.orgao.orgCodigo" : "cnv.consignataria.csaCodigo");


        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }

        if(codigos != null && !codigos.isEmpty()) {
            defineValorClausulaNomeada("codigos", codigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        if (!TextHelper.isNull(csaCodigo)) {
            return new String[] {
                    Columns.CNV_ORG_CODIGO
            };
        } else {
            return new String[] {
                    Columns.CNV_CSA_CODIGO
            };
        }
    }

}
