package com.zetra.econsig.persistence.query.parametro;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaParamOrgaoQuery</p>
 * <p>Description: Lista parâmetros de órgão</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaParamOrgaoEditavelQuery extends HQuery {

    private final AcessoSistema responsavel;
    private final String orgCodigo;

    public ListaParamOrgaoEditavelQuery(String orgCodigo, AcessoSistema responsavel) {
        this.orgCodigo = orgCodigo;
        this.responsavel = responsavel;
    }

	@Override
	public Query<Object[]> preparar(Session session) throws HQueryException {
	    StringBuilder corpo = new StringBuilder();

	    corpo.append("select ");
        corpo.append("tao.taoCodigo, ");
        corpo.append("tao.taoDescricao, ");
        corpo.append("tao.taoDominio, ");
        corpo.append("tao.taoVlrDefault, ");
        corpo.append("tao.taoSupAltera, ");
        corpo.append("tao.taoCseAltera, ");
        corpo.append("tao.taoOrgAltera, ");
        corpo.append("pao.paoVlr ");

        corpo.append("from TipoParamOrgao tao ");
        corpo.append("left outer join tao.paramOrgaoSet pao with (pao.orgCodigo = :orgCodigo) ");
        corpo.append("where 1=1 ");

        if (responsavel.isSup()) {
            corpo.append(" and tao.taoSupConsulta = 'S' ");
        } else if (responsavel.isCse()) {
            corpo.append(" and tao.taoCseConsulta = 'S' ");
        } else if (responsavel.isOrg()) {
            corpo.append(" and tao.taoOrgConsulta = 'S' ");
        }

		corpo.append(" order by tao.taoDescricao");

		Query<Object[]> query = instanciarQuery(session, corpo.toString());
		defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
		return query;
	}

	@Override
	protected String[] getFields() {
		return new String[] {
                Columns.TAO_CODIGO,
                Columns.TAO_DESCRICAO,
                Columns.TAO_DOMINIO,
                Columns.TAO_VLR_DEFAULT,
                Columns.TAO_SUP_ALTERA,
                Columns.TAO_CSE_ALTERA,
                Columns.TAO_ORG_ALTERA,
                Columns.PAO_VLR
        };
	}
}
