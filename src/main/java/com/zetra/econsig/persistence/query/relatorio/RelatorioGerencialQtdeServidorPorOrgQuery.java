package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p> Title: RelatorioGerencialQtdeServidorPorOrgQuery</p>
 * <p> Description: Recupera a quantidade de servidores por órgão, ordenado pela quantidade decrescente dos dez primeiros órgãos.</p>
 * <p> Copyright: Copyright (c) 2009 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioGerencialQtdeServidorPorOrgQuery extends ReportHQuery {

    public boolean somenteOrgaoAtivo = false;

    public RelatorioGerencialQtdeServidorPorOrgQuery() {
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final String srsAtivo = CodedValues.SRS_ATIVO;

        final StringBuilder corpo = new StringBuilder();
        corpo.append(" select ");
        corpo.append(" coalesce(org.orgNome, '"+ApplicationResourcesHelper.getMessage("rotulo.campo.nao.definido.abreviado", (AcessoSistema) null)+"') AS ORG_NOME, ");
        corpo.append(" COUNT(DISTINCT rse.rseCodigo) AS QUANTIDADE ");
        corpo.append(" from RegistroServidor rse ");
        corpo.append(" INNER JOIN rse.orgao org ");
        corpo.append(" WHERE rse.statusRegistroServidor.srsCodigo ").append(criaClausulaNomeada("srsAtivo", srsAtivo));
        if(somenteOrgaoAtivo) {
            corpo.append(" AND org.orgAtivo = ").append(CodedValues.STS_ATIVO).append(" ");
        }
        corpo.append(" GROUP BY org.orgNome ");
        corpo.append(" ORDER BY COUNT(DISTINCT rse.rseCodigo) DESC");

        final Query<Object[]> query = instanciarQuery(session, corpo.toString());
        if(!somenteOrgaoAtivo) {
            query.setFirstResult(0);
            query.setMaxResults(10);
        }

        defineValorClausulaNomeada("srsAtivo", srsAtivo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ORG_NOME,
                "QUANTIDADE"
        };
    }
}
