package com.zetra.econsig.persistence.query.consignataria;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignatariaConvenioQuery</p>
 * <p>Description: Listagem de consignatárias avaliando convênio de acordo com o responsável</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignatariaConvenioQuery extends HQuery {

    public Object csaAtivo;
    public AcessoSistema responsavel;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select csa.csaCodigo, " +
                       "csa.csaAtivo, " +
                       "csa.csaEmail ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from Consignataria csa WHERE 1=1 ");

        if (csaAtivo != null) {
            corpoBuilder.append(" and csa.csaAtivo ").append(criaClausulaNomeada("csaAtivo", csaAtivo));
        }

        if (responsavel.isOrg()) {
            if (responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
                corpoBuilder.append(" and exists (");
                corpoBuilder.append(" select 1 ");
                corpoBuilder.append(" from Convenio cnv ");
                corpoBuilder.append(" inner join cnv.orgao org ");
                corpoBuilder.append(" where cnv.consignataria.csaCodigo = csa.csaCodigo");
                corpoBuilder.append(" and cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
                corpoBuilder.append(" and org.estabelecimento.estCodigo ").append(criaClausulaNomeada("estCodigo", responsavel.getEstCodigo()));
                corpoBuilder.append(" ) ");
            } else {
                corpoBuilder.append(" and exists (");
                corpoBuilder.append(" select 1 ");
                corpoBuilder.append(" from Convenio cnv ");
                corpoBuilder.append(" inner join cnv.orgao org ");
                corpoBuilder.append(" where cnv.consignataria.csaCodigo = csa.csaCodigo");
                corpoBuilder.append(" and cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
                corpoBuilder.append(" and org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", responsavel.getOrgCodigo()));
                corpoBuilder.append(" ) ");
            }
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (csaAtivo != null) {
            defineValorClausulaNomeada("csaAtivo", csaAtivo, query);
        }

        if (responsavel.isOrg()) {
            if (responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
                defineValorClausulaNomeada("estCodigo", responsavel.getEstCodigo(), query);
            } else {
                defineValorClausulaNomeada("orgCodigo", responsavel.getOrgCodigo(), query);
            }
        }

        return query;
    }


    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CSA_CODIGO,
                Columns.CSA_ATIVO,
                Columns.CSA_EMAIL
        };
    }

}
