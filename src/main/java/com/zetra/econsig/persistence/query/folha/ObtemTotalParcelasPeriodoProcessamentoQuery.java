package com.zetra.econsig.persistence.query.folha;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;

/**
 * <p>Title: ObtemTotalParcelasPeriodoProcessamentoQuery</p>
 * <p>Description: Retorna o total de parcelas do periodo de processamento.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemTotalParcelasPeriodoProcessamentoQuery extends HQuery {

    public Date periodo;
    public String tipoEntidade;
    public String codigoEntidade;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT COUNT(*) ");
        corpoBuilder.append("FROM ParcelaDescontoPeriodo prd ");

        if (tipoEntidade != null && (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST))) {
            corpoBuilder.append("INNER JOIN prd.autDesconto ade ");
            corpoBuilder.append("INNER JOIN ade.verbaConvenio vco ");
            corpoBuilder.append("INNER JOIN vco.convenio cnv ");

            if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST)) {
                corpoBuilder.append("INNER JOIN cnv.orgao org ");
            }
        }

        corpoBuilder.append("WHERE prd.prdDataDesconto ").append(criaClausulaNomeada("periodo", periodo));

        if (tipoEntidade != null && (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST))) {
            if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
                corpoBuilder.append(" AND cnv.orgao.orgCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            } else {
                corpoBuilder.append(" AND org.estabelecimento.estCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            }
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("periodo", periodo, query);

        if (tipoEntidade != null && (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST))) {
            defineValorClausulaNomeada("codigoEntidade", codigoEntidade, query);
        }

        return query;
    }
}
