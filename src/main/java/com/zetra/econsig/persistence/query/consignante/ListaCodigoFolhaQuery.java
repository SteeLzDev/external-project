package com.zetra.econsig.persistence.query.consignante;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p> Title: ListaCodigoFolhaQuery </p>
 * <p> Description: lista os códigos folha por orgão. </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCodigoFolhaQuery extends HQuery {

	@Override
	protected Query<Object[]> preparar(Session session) throws HQueryException {
		final String corpo = "";

		final StringBuilder corpoBuilder = new StringBuilder(corpo);
		corpoBuilder.append(
				"select COALESCE(NULLIF(org.orgFolha, ''), COALESCE(NULLIF(est.estFolha, ''), NULLIF(cse.cseFolha, ''))) as codigoFolha ");
		corpoBuilder.append("from Orgao org ");
		corpoBuilder.append("inner join org.estabelecimento est ");
		corpoBuilder.append("inner join est.consignante cse ");
		corpoBuilder.append("where org.orgAtivo = ").append(CodedValues.STS_ATIVO).append("");

		final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

		return query;
	}

	/**
	 * Verifica se o código de folha está preenchido corretamente
	 * @return
	 * @throws HQueryException
	 */
	public Boolean verificarPreenchimento() throws HQueryException {
	    final List<String> lstCodigoFolha = executarLista();
	    for (final String codigoFolha : lstCodigoFolha) {
	        if (StringUtils.isBlank(codigoFolha)) {
	            return false;
	        }
	    }
	    return true;
	}
}
