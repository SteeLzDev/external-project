package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;

/**
 * <p> Title: RelatorioAlteracaoMultiplasConsignacoesQuery</p>
 * <p> Description: Classe para consulta ao banco de dados do Relatório de Alteração de Multiplas Consignações</p>
 * <p> Copyright: Copyright (c) 2009 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioAlteracaoMultiplasConsignacoesQuery extends ReportHQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder();
        corpo.append(" SELECT 1 as CSA_NOME, 1 as ADE_NUMERO, 1 as SERVICO, 1 as ADE_VLR, 1 as ADE_VLR_NOVO, 1 as ADE_PRAZO, 1 as ADE_PRAZO_NOVO, 1 as ADE_VLR_ULT ");
        corpo.append(" FROM Consignante ");
        // Não é necessário utilizar a tabela exata do TOTEM
        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "CSA_NOME",
                "ADE_NUMERO",
                "SERVICO",
                "ADE_VLR",
                "ADE_VLR_NOVO",
                "ADE_PRAZO",
                "ADE_PRAZO_NOVO",
                "ADE_VLR_ULT"
        };
    }

}
