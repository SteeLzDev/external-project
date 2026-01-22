package com.zetra.econsig.persistence.query.validardocumento;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoSolicitacaoEnum;

/**
 * <p>Title: ListaSolicitacaoAutorizacaoValidarDocumentosQuery</p>
 * <p>Description: Listagem de solicitacao autorizacao do caso de uso de validação de documentos</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date
 */
public class ListaSolicitacaoAutorizacaoValidarDocumentosQuery extends HQuery {

    public String adeCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

    	final StringBuilder sql = new StringBuilder();
    	sql.append("SELECT soa.soaData, ");
    	sql.append("case when usu.statusLogin.stuCodigo <> '").append(CodedValues.STU_EXCLUIDO).append("' ");
        sql.append("then usu.usuLogin else coalesce(nullif(concat(usu.usuTipoBloq, '*'), ''), usu.usuLogin) end AS USU_LOGIN, ");
        sql.append("sso.ssoDescricao, ");
    	sql.append("oso.osoDescricao, ");
    	sql.append("soa.soaObs, ");
        sql.append("soa.soaDataResposta ");
        sql.append("FROM SolicitacaoAutorizacao soa ");
        sql.append("INNER JOIN soa.usuario usu  ");
        sql.append("INNER JOIN soa.statusSolicitacao sso  ");
        sql.append("INNER JOIN soa.origemSolicitacao oso  ");
        sql.append("WHERE soa.tisCodigo ='").append(TipoSolicitacaoEnum.SOLICITACAO_DEPENDE_AUTORIZACAO.getCodigo()).append("' ");

        if(!TextHelper.isNull(adeCodigo)) {
            sql.append("AND soa.adeCodigo = :adeCodigo  ");
        }

        sql.append("ORDER BY COALESCE(soa.soaDataResposta,current_timestamp) DESC, soa.soaData DESC ");

        final Query<Object[]> query = instanciarQuery(session, sql.toString());

        if(!TextHelper.isNull(adeCodigo)) {
            defineValorClausulaNomeada("adeCodigo", adeCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
    	return new String[] {
                Columns.SOA_DATA,
                Columns.USU_LOGIN,
                Columns.SSO_DESCRICAO,
                Columns.OSO_DESCRICAO,
                Columns.SOA_OBS,
                Columns.SOA_DATA_RESPOSTA
    	};
    }
}
