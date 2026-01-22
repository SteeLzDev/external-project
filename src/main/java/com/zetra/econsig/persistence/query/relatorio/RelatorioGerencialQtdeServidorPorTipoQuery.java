package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioGerencialQtdeServidorPorTipoQuery</p>
 * <p>Description: Retorna a quantidade de servidores agrupados por tipo.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioGerencialQtdeServidorPorTipoQuery extends ReportHQuery {

    private int maxResultados = 0;

    public RelatorioGerencialQtdeServidorPorTipoQuery() {
    }

    public RelatorioGerencialQtdeServidorPorTipoQuery(int maxResultados) {
        this.maxResultados = maxResultados;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String srsCodigo = CodedValues.SRS_ATIVO;

        StringBuilder corpo = new StringBuilder();
        corpo.append("SELECT coalesce(nullif(trim(rse.rseTipo),''),'"+ApplicationResourcesHelper.getMessage("rotulo.campo.nao.definido.abreviado", (AcessoSistema) null)+"') AS RSE_TIPO, COUNT(*) AS QUANTIDADE ");
        corpo.append("FROM RegistroServidor rse ");
        corpo.append("WHERE rse.statusRegistroServidor.srsCodigo ").append(criaClausulaNomeada("srsCodigo", srsCodigo ));
        corpo.append(" AND (rse.rseTipo IS NOT NULL OR trim(rse.rseTipo) != '')");
        corpo.append(" GROUP BY coalesce(nullif(trim(rse.rseTipo),''),'"+ApplicationResourcesHelper.getMessage("rotulo.campo.nao.definido.abreviado", (AcessoSistema) null)+"') ");
        corpo.append(" ORDER BY COUNT(*) DESC ");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());
        if (maxResultados > 0) {
            query.setMaxResults(maxResultados);
        }

        defineValorClausulaNomeada("srsCodigo", srsCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RSE_TIPO,
                "QUANTIDADE"
        };
    }
}
