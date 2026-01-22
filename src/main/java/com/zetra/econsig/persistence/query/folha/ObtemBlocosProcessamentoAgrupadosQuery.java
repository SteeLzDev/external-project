package com.zetra.econsig.persistence.query.folha;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;

/**
 * <p>Title: ObtemBlocosProcessamentoAgrupadosQuery</p>
 * <p>Description: Obtém os blocos de processamento agrupados por registro servidor</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemBlocosProcessamentoAgrupadosQuery extends HQuery  {

    public String tipoEntidade;
    public String codigoEntidade;

    public List<String> sbpCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT MIN(bpr.bprCodigo) ");
        corpoBuilder.append("FROM BlocoProcessamento bpr ");
        corpoBuilder.append("WHERE 1=1 ");

        if (sbpCodigos != null && !sbpCodigos.isEmpty()) {
            corpoBuilder.append(" AND bpr.statusBlocoProcessamento.sbpCodigo ").append(criaClausulaNomeada("sbpCodigos", sbpCodigos));
        }

        if (tipoEntidade != null && (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST))) {
            if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
                corpoBuilder.append(" AND bpr.orgao.orgCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            } else {
                corpoBuilder.append(" AND bpr.estabelecimento.estCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            }
        }

        corpoBuilder.append(" GROUP BY COALESCE(bpr.registroServidor.rseCodigo, bpr.rseMatricula)");
        corpoBuilder.append(" ORDER BY 1");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (sbpCodigos != null && !sbpCodigos.isEmpty()) {
            defineValorClausulaNomeada("sbpCodigos", sbpCodigos, query);
        }
        if (tipoEntidade != null && (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST))) {
            defineValorClausulaNomeada("codigoEntidade", codigoEntidade, query);
        }

        return query;
    }
}
