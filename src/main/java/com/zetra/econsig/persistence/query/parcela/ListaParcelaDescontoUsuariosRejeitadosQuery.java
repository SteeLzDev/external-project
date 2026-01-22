package com.zetra.econsig.persistence.query.parcela;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoNotificacaoEnum;

/**
 * <p>Title: ListaParcelaDescontoQuery</p>
 * <p>Description: Busca os usuários que possuem parcelas rejeitadas e que estão com o envio de
 * email habilitados.</p>
 * <p>Copyright: Copyright (c) 2002-2016</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaParcelaDescontoUsuariosRejeitadosQuery extends HQuery {

	private final String periodo;
	private final List<String> orgCodigo;
	private final List<String> estCodigo;
	private final boolean notificaoObrigatoria;

	public ListaParcelaDescontoUsuariosRejeitadosQuery(String periodo, List<String> orgCodigo, List<String> estCodigo, boolean notificaoObrigatoria) {
		this.periodo = periodo;
		this.orgCodigo = orgCodigo;
		this.estCodigo = estCodigo;
		this.notificaoObrigatoria = notificaoObrigatoria;
	}

	@Override
	protected Query<Object[]> preparar(Session session) throws HQueryException {

		StringBuilder sql = new StringBuilder();
		sql.append(" select distinct(usu.usuCodigo), ser.serEmail, ser.serCodigo ");
		sql.append(" from ParcelaDescontoPeriodo prd ");
		sql.append(" join prd.statusParcelaDesconto spd ");
		sql.append(" join prd.autDesconto ade ");
		sql.append(" join ade.verbaConvenio vco ");
		sql.append(" join vco.convenio cnv ");
		sql.append(" join cnv.orgao org ");
		sql.append(" join org.estabelecimento est ");
		sql.append(" join ade.registroServidor rse ");
		sql.append(" join rse.servidor ser ");
		sql.append(" join ser.usuarioSerSet usuSer ");
		sql.append(" join usuSer.usuario usu ");
		if(!notificaoObrigatoria) {
		    sql.append(" join usu.notificacaoUsuarioSet nou ");
		}
		sql.append(" where spd.spdCodigo ").append(criaClausulaNomeada("spdCodigo", CodedValues.SPD_REJEITADAFOLHA));
		sql.append(" and prd.prdDataDesconto ").append(criaClausulaNomeada("periodo", periodo));
		if(!notificaoObrigatoria) {
		    sql.append(" and nou.tipoNotificacao.tnoCodigo ").append(criaClausulaNomeada("tipoNotificacao", TipoNotificacaoEnum.EMAIL_CONTRATOS_REJEITADOS_FOLHA.getCodigo()));
		    sql.append(" and nou.nusAtivo = 1 ");
		}
		sql.append(" and nullif(trim(usu.usuEmail), '') is not null ");

		if (ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_EST_ORG_ID, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
			sql.append(" and usu.usuLogin = concat(concat(concat(concat(est.estIdentificador, '-'), org.orgIdentificador), '-'), rse.rseMatricula)");
        } else  {
        	sql.append(" and usu.usuLogin = concat(concat(est.estIdentificador, '-'), rse.rseMatricula)");
        }

		if (!TextHelper.isNull(orgCodigo)) {
			sql.append(" and org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
		}

		if (!TextHelper.isNull(estCodigo)) {
			sql.append(" and est.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigo));
		}

		Query<Object[]> query = instanciarQuery(session, sql.toString());

		defineValorClausulaNomeada("spdCodigo", CodedValues.SPD_REJEITADAFOLHA, query);
		defineValorClausulaNomeada("periodo", parseDateString(periodo), query);
		if(!notificaoObrigatoria) {
            defineValorClausulaNomeada("tipoNotificacao", TipoNotificacaoEnum.EMAIL_CONTRATOS_REJEITADOS_FOLHA.getCodigo(), query);
        }

		if (!TextHelper.isNull(orgCodigo)) {
			defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
		}

		if (!TextHelper.isNull(estCodigo)) {
			defineValorClausulaNomeada("estCodigo", estCodigo, query);
		}

		return query;
	}

	@Override
	protected String[] getFields() {
		return new String [] {
			Columns.USU_CODIGO,
			Columns.SER_EMAIL,
			Columns.SER_CODIGO
		};
	}

}
