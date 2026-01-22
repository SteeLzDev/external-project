package com.zetra.econsig.persistence.query.consignataria;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignatariaConvenioSvcCodigoQuery</p>
 * <p>Description: Listagem de consignatárias avaliando convênio de acordo com a lista de Serviços</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author: anderson.assis $
 * $Revision: 26246 $
 * $Date: 2020-11-25 16:17:49 -0200 (Qua, 25 nov 2020) $
 */

public class ListaConsignatariaConvenioNseCodigoQuery extends HQuery {
    public AcessoSistema responsavel;
    public String nseCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT cnv.consignataria.csaCodigo ");
        corpoBuilder.append("FROM Convenio cnv ");
        corpoBuilder.append("INNER JOIN cnv.servico svc ");
        corpoBuilder.append("WHERE svc.naturezaServico.nseCodigo ").append(criaClausulaNomeada("nseCodigo", nseCodigo));
        corpoBuilder.append("  AND cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("' ");

        if (responsavel != null && responsavel.isSer()) {
            // Adiciona cláusula para buscar apenas consignatárias que o servidor possui consignação
            corpoBuilder.append(" AND EXISTS (");
            corpoBuilder.append("  SELECT 1 FROM AutDesconto ade ");
            corpoBuilder.append("  INNER JOIN ade.verbaConvenio vco ");
            corpoBuilder.append("  WHERE ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", responsavel.getRseCodigo()));
            corpoBuilder.append("    AND vco.convenio.cnvCodigo = cnv.cnvCodigo ");
            corpoBuilder.append(")");
        }

        corpoBuilder.append(" GROUP BY cnv.consignataria.csaCodigo ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("nseCodigo", nseCodigo, query);

        if (responsavel != null && responsavel.isSer()) {
            defineValorClausulaNomeada("rseCodigo", responsavel.getRseCodigo(), query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] { Columns.CSA_CODIGO };
    }
}
