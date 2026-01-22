package com.zetra.econsig.persistence.query.solicitacao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaSolicitacaoAutorizacaoValidarDocumentosQuery</p>
 * <p>Description: Listagem de solicitacao autorizacao do caso de uso de validação de documentos</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date
 */
public class ListaSolicitacaoQuery extends HQuery {

    public String adeCodigo;
    public List<String> tisCodigos;
    public List<String> ssoCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

    	StringBuilder sql = new StringBuilder();
    	sql.append("SELECT soa.soaData, ");
    	sql.append("usu.usuLogin, ");
    	sql.append("sso.ssoDescricao, ");
    	sql.append("soa.soaObs, ");
        sql.append("soa.soaDataResposta ");
        sql.append("FROM SolicitacaoAutorizacao soa ");
        sql.append("INNER JOIN soa.usuario usu  ");
        sql.append("INNER JOIN soa.statusSolicitacao sso  ");
        sql.append("WHERE 1=1 ");

        if(!TextHelper.isNull(adeCodigo)) {
            sql.append("AND soa.adeCodigo = :adeCodigo  ");
        }

        if(tisCodigos != null && !tisCodigos.isEmpty()) {
            sql.append(" AND soa.tisCodigo ").append(criaClausulaNomeada("tisCodigos", tisCodigos));
        }

        if(ssoCodigos != null && !ssoCodigos.isEmpty()) {
            sql.append(" AND sso.ssoCodigo ").append(criaClausulaNomeada("ssoCodigos", ssoCodigos));
        }

        sql.append(" ORDER BY soa.soaDataResposta DESC, soa.soaData DESC ");

        Query<Object[]> query = instanciarQuery(session, sql.toString());

        if(!TextHelper.isNull(adeCodigo)) {
            defineValorClausulaNomeada("adeCodigo", adeCodigo, query);
        }

        if(tisCodigos != null && !tisCodigos.isEmpty()) {
            defineValorClausulaNomeada("tisCodigos", tisCodigos, query);
        }

        if(ssoCodigos != null && !ssoCodigos.isEmpty()) {
            defineValorClausulaNomeada("ssoCodigos", ssoCodigos, query);
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
