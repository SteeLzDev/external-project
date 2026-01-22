package com.zetra.econsig.persistence.query.parametro;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaOcorrenciaParamSistCseQuery</p>
 * <p>Description: Lista ocorrências de parâmetros de sistema CSE</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 */
public class ListaOcorrenciaParamSistCseQuery extends HQuery {

    public String tpcDescricao = null;
    public String tpcCodigo = null;
    public String usuLogin = null;
    public boolean count = false;

	@Override
	public Query<Object[]> preparar(Session session) throws HQueryException {
	    String corpo = "select count(ops.opsCodigo) as total";
	    if (!count) {
	        corpo = "select ops.opsCodigo, " +
		               " ops.tipoOcorrencia.tocCodigo, " +
		               " ops.usuario.usuCodigo, " +
		               " trim(ops.tipoParamSistConsignante.tpcCodigo), " +
		               " ops.consignante.cseCodigo, " +
		               " ops.opsData, " +
		               " ops.opsObs, " +
		               " ops.opsIpAcesso, " +
		               " ops.tipoParamSistConsignante.tpcDescricao, " +
		               " ops.usuario.usuLogin ";
	    }
		corpo += " from OcorrenciaParamSistCse ops where 1 = 1 " ;

		if (!TextHelper.isNull(tpcDescricao)) {
		    corpo += " and ops.tipoParamSistConsignante.tpcDescricao "  + criaClausulaNomeada("tpcDescricao", tpcDescricao);
		}

		if (!TextHelper.isNull(tpcCodigo)) {
            corpo += " and ops.tipoParamSistConsignante.tpcCodigo "  + criaClausulaNomeada("tpcCodigo", tpcCodigo);
        }

		if (!TextHelper.isNull(usuLogin)) {
            corpo += " and ops.usuario.usuLogin "  + criaClausulaNomeada("usuLogin", usuLogin);
        }

		if (!count) {
		    corpo += " order by ops.opsData desc";
		}

		Query<Object[]> query = instanciarQuery(session, corpo);

        // Seta os parâmetros na query
		if (!TextHelper.isNull(tpcDescricao)) {
		    defineValorClausulaNomeada("tpcDescricao", tpcDescricao, query);
		}

        if (!TextHelper.isNull(tpcCodigo)) {
            defineValorClausulaNomeada("tpcCodigo", tpcCodigo, query);
        }

        if (!TextHelper.isNull(usuLogin)) {
            defineValorClausulaNomeada("usuLogin", usuLogin, query);
        }

		return query;
	}

	@Override
	protected String[] getFields() {
		return new String[] {
                Columns.OPS_CODIGO,
                Columns.TOC_CODIGO,
                Columns.USU_CODIGO,
                Columns.TPC_CODIGO,
                Columns.CSE_CODIGO,
                Columns.OPS_DATA,
                Columns.OPS_OBS,
                Columns.OPS_IP_ACESSO,
                Columns.TPC_DESCRICAO,
                Columns.USU_LOGIN
        };
	}
}
