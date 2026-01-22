package com.zetra.econsig.persistence.query.correspondente;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCorrespondenteConvenioQuery</p>
 * <p>Description: Listagem de correspondentes avaliando convênio de acordo com o responsável</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCorrespondenteConvenioQuery extends HQuery {

    public Object corAtivo;
    public AcessoSistema responsavel;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select cor.corCodigo, " +
                       "cor.corAtivo, " +
                       "cor.corEmail ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from Correspondente cor WHERE 1=1 ");

        if (corAtivo != null) {
            corpoBuilder.append(" and cor.corAtivo ").append(criaClausulaNomeada("corAtivo", corAtivo));
        }

        if (responsavel.isOrg()) {
            if (responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
                corpoBuilder.append(" and exists (");
                corpoBuilder.append(" select 1 ");
                corpoBuilder.append(" from Convenio cnv ");
                corpoBuilder.append(" inner join cnv.orgao org ");
                corpoBuilder.append(" inner join cnv.correspondenteConvenioSet crc ");
                corpoBuilder.append(" where crc.corCodigo = cor.corCodigo");
                corpoBuilder.append(" and cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
                corpoBuilder.append(" and crc.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
                corpoBuilder.append(" and org.estabelecimento.estCodigo ").append(criaClausulaNomeada("estCodigo", responsavel.getEstCodigo()));
                corpoBuilder.append(" ) ");
            } else {
                corpoBuilder.append(" and exists (");
                corpoBuilder.append(" select 1 ");
                corpoBuilder.append(" from Convenio cnv ");
                corpoBuilder.append(" inner join cnv.orgao org ");
                corpoBuilder.append(" inner join cnv.correspondenteConvenioSet crc ");
                corpoBuilder.append(" where crc.corCodigo = cor.corCodigo");
                corpoBuilder.append(" and cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
                corpoBuilder.append(" and crc.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
                corpoBuilder.append(" and org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", responsavel.getOrgCodigo()));
                corpoBuilder.append(" ) ");
            }
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (corAtivo != null) {
            defineValorClausulaNomeada("corAtivo", corAtivo, query);
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
                Columns.COR_CODIGO,
                Columns.COR_ATIVO,
                Columns.COR_EMAIL
        };
    }

}
