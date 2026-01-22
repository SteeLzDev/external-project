package com.zetra.econsig.persistence.query.admin;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaRegraRestricaoAcessoQuery</p>
 * <p>Description: Listagem de Regras de Restrição de Acesso</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaRegraRestricaoAcessoQuery extends HQuery {

	public String csaCodigo;
	public boolean count;
	public boolean todos;

	@Override
	protected Query<Object[]> preparar(Session session) throws HQueryException {
		StringBuilder corpoBuilder = new StringBuilder();

		if (!count) {
			corpoBuilder.append(" select ");
			corpoBuilder.append(" rra.rraCodigo, ");
			corpoBuilder.append(" rra.rraDescricao, ");
			corpoBuilder.append(" rra.rraData, ");
			corpoBuilder.append(" rra.rraDiaSemana, ");
			corpoBuilder.append(" rra.rraDiasUteis, ");
			corpoBuilder.append(" rra.rraHoraInicio, ");
			corpoBuilder.append(" rra.rraHoraFim, ");
			corpoBuilder.append(" rra.papel.papCodigo, ");
			corpoBuilder.append(" rra.funcao.funCodigo, ");
			if (todos) {
				corpoBuilder.append(" rraCsa.csaCodigo, ");
			}
			corpoBuilder.append(" fun.funDescricao ");
		} else {
			corpoBuilder.append(" select count(*) as total ");
		}

		corpoBuilder.append(" from RegraRestricaoAcesso rra ");
		corpoBuilder.append(" left outer join rra.funcao fun ");

        if (!TextHelper.isNull(csaCodigo)) {
        	corpoBuilder.append(" inner join rra.regraRestricaoAcessoCsaSet rraCsa ");
        } else {
        	if (todos) {
        		corpoBuilder.append(" left outer join rra.regraRestricaoAcessoCsaSet rraCsa ");
        	}
        }

        corpoBuilder.append(" where 1=1 ");


        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and rraCsa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        } else if (!todos) {
        	corpoBuilder.append(" and  not exists (select rraCsa.rraCodigo from RegraRestricaoAcessoCsa rraCsa where rraCsa.rraCodigo = rra.rraCodigo) ");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
	}

	@Override
	protected String[] getFields() {
		if (todos) {
		return new String[] {
				Columns.RRA_CODIGO,
				Columns.RRA_DESCRICAO,
				Columns.RRA_DATA,
				Columns.RRA_DIA_SEMANA,
				Columns.RRA_DIAS_UTEIS,
				Columns.RRA_HORA_INICIO,
				Columns.RRA_HORA_FIM,
				Columns.RRA_PAP_CODIGO,
				Columns.RRA_FUN_CODIGO,
				Columns.RCA_CSA_CODIGO,
				Columns.FUN_DESCRICAO
		};
		} else {
			return new String[] {
					Columns.RRA_CODIGO,
					Columns.RRA_DESCRICAO,
					Columns.RRA_DATA,
					Columns.RRA_DIA_SEMANA,
					Columns.RRA_DIAS_UTEIS,
					Columns.RRA_HORA_INICIO,
					Columns.RRA_HORA_FIM,
					Columns.RRA_PAP_CODIGO,
					Columns.RRA_FUN_CODIGO,
					Columns.FUN_DESCRICAO
			};
		}
	}

}
