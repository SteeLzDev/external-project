package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p> Title: RelatorioGerencialQtdeServidorPorEstQuery</p>
 * <p> Description: Recupera a quantidade de servidores por estabelecimento, ordenado pela quantidade decrescente dos dez primeiros estabelecimentos.</p>
 * <p> Copyright: Copyright (c) 2009 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioGerencialQtdeServidorPorEstQuery extends ReportHQuery {

    public RelatorioGerencialQtdeServidorPorEstQuery() {
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String srsAtivo = CodedValues.SRS_ATIVO;

        StringBuilder corpo = new StringBuilder();
        corpo.append(" select ");
        corpo.append(" coalesce(est.estNome, '"+ApplicationResourcesHelper.getMessage("rotulo.campo.nao.definido.abreviado", (AcessoSistema) null)+"') AS EST_NOME, ");
        corpo.append(" COUNT(DISTINCT rse.rseCodigo) AS QUANTIDADE ");
        corpo.append(" from RegistroServidor rse ");
        corpo.append(" INNER JOIN rse.orgao org ");
        corpo.append(" INNER JOIN org.estabelecimento est ");
        corpo.append(" WHERE rse.statusRegistroServidor.srsCodigo ").append(criaClausulaNomeada("srsAtivo", srsAtivo));
        corpo.append(" GROUP BY est.estNome ");
        corpo.append(" ORDER BY COUNT(DISTINCT rse.rseCodigo) DESC");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());
        query.setFirstResult(0);
        query.setMaxResults(10);

        defineValorClausulaNomeada("srsAtivo", srsAtivo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.EST_NOME,
                "QUANTIDADE"
        };
    }
}
