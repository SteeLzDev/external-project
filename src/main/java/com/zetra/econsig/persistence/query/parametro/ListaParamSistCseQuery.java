package com.zetra.econsig.persistence.query.parametro;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaParamSistCseQuery</p>
 * <p>Description: Lista parâmetros de sistema</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaParamSistCseQuery extends HQuery {

    private final AcessoSistema responsavel;

    public String perCodigo;

    public String tpcCseAltera = null;
    public String tpcCseConsulta = null;
    public String tpcSupAltera = null;
    public String tpcSupConsulta = null;

    public ListaParamSistCseQuery(AcessoSistema responsavel) {
        this.responsavel = responsavel;
    }

	@Override
	public Query<Object[]> preparar(Session session) throws HQueryException {
	    final StringBuilder corpo = new StringBuilder();

	    corpo.append("select ");
        corpo.append("tpc.gpsCodigo, ");
        corpo.append("gps.gpsDescricao, ");
        corpo.append("trim(tpc.tpcCodigo), ");
        corpo.append("tpc.tpcDescricao, ");
        corpo.append("tpc.tpcDominio, ");
        corpo.append("psi.psiVlr, ");
        corpo.append("coalesce(tpc.tpcVlrDefault, '') ");
        corpo.append("from TipoParamSistConsignante tpc ");
        corpo.append("left outer join tpc.grupoParamSistCse gps ");
        corpo.append("left outer join tpc.paramSistConsignanteSet psi ");
        corpo.append("where 1 = 1 ");

        if (!TextHelper.isNull(tpcCseAltera)) {
            corpo.append(" and tpc.tpcCseAltera = :tpcCseAltera ");
        }
        if (!TextHelper.isNull(tpcCseConsulta)) {
            corpo.append(" and tpc.tpcCseConsulta = :tpcCseConsulta ");
        }
        if (!TextHelper.isNull(tpcSupAltera)) {
            corpo.append(" and tpc.tpcSupAltera = :tpcSupAltera ");
        }
        if (!TextHelper.isNull(tpcSupConsulta)) {
            corpo.append(" and tpc.tpcSupConsulta = :tpcSupConsulta ");
        }

        if (!TextHelper.isNull(perCodigo)) {
            corpo.append(" and exists (");
            corpo.append("select 1 ");
            corpo.append("from tpc.perfilParamSistCseSet pps ");
            corpo.append("where pps.perCodigo = :perCodigo) ");
        } else if (CodedValues.TPC_SIM.equals(tpcCseAltera) || CodedValues.TPC_SIM.equals(tpcSupAltera)) {
            corpo.append(" and exists (");
            corpo.append("select 1 ");
            corpo.append("from tpc.perfilParamSistCseSet pps ");
            corpo.append("inner join pps.perfil per ");
            corpo.append("inner join per.perfilUsuarioSet upe ");
            corpo.append("where upe.usuCodigo = :usuCodigo) ");
        } else if (CodedValues.TPC_SIM.equals(tpcCseConsulta) || CodedValues.TPC_SIM.equals(tpcSupConsulta)) {
            // Se lista os parâmetros para consulta, então retorna aqueles que o perfil do usuário
            // não edita ou aqueles que não são alteráveis.
            corpo.append(" and (not exists (");
            corpo.append("select 1 ");
            corpo.append("from tpc.perfilParamSistCseSet pps ");
            corpo.append("inner join pps.perfil per ");
            corpo.append("inner join per.perfilUsuarioSet upe ");
            corpo.append("where upe.usuCodigo = :usuCodigo) ");
            if (!TextHelper.isNull(tpcCseConsulta)) {
                corpo.append(" or tpc.tpcCseAltera = 'N' ");
            }
            if (!TextHelper.isNull(tpcSupConsulta)) {
                corpo.append(" or tpc.tpcSupAltera = 'N' ");
            }
            corpo.append(")");
        }

        corpo.append(" order by coalesce(tpc.gpsCodigo, '999'), tpc.tpcDescricao");

		final Query<Object[]> query = instanciarQuery(session, corpo.toString());

        if (!TextHelper.isNull(tpcCseAltera)) {
            query.setParameter("tpcCseAltera", tpcCseAltera);
        }
        if (!TextHelper.isNull(tpcCseConsulta)) {
            query.setParameter("tpcCseConsulta", tpcCseConsulta);
        }
        if (!TextHelper.isNull(tpcSupAltera)) {
            query.setParameter("tpcSupAltera", tpcSupAltera);
        }
        if (!TextHelper.isNull(tpcSupConsulta)) {
            query.setParameter("tpcSupConsulta", tpcSupConsulta);
        }
        if (!TextHelper.isNull(perCodigo)) {
            query.setParameter("perCodigo", perCodigo);
        } else if (CodedValues.TPC_SIM.equals(tpcCseAltera) || CodedValues.TPC_SIM.equals(tpcSupAltera) ||
                CodedValues.TPC_SIM.equals(tpcCseConsulta) || CodedValues.TPC_SIM.equals(tpcSupConsulta)) {
            query.setParameter("usuCodigo", responsavel.getUsuCodigo());
        }

		return query;
	}

	@Override
	protected String[] getFields() {
		return new String[] {
                Columns.GPS_CODIGO,
                Columns.GPS_DESCRICAO,
                Columns.TPC_CODIGO,
                Columns.TPC_DESCRICAO,
                Columns.TPC_DOMINIO,
                Columns.PSI_VLR,
                Columns.TPC_VLR_DEFAULT
        };
	}
}
