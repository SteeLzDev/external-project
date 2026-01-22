package com.zetra.econsig.persistence.query.coeficiente;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCoeficientesInativosQuery</p>
 * <p>Description: Listagem de registros inativos da tabela de coeficientes ativos.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCoeficientesInativosQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append(" SELECT cfa.cftCodigo, ");
        corpoBuilder.append(" pzc.przCsaCodigo, ");
        corpoBuilder.append(" cfa.cftDia, ");
        corpoBuilder.append(" cfa.cftVlr, ");
        corpoBuilder.append(" cfa.cftDataIniVig, ");
        corpoBuilder.append(" cfa.cftDataFimVig, ");
        corpoBuilder.append(" cfa.cftDataCadastro ");
        corpoBuilder.append(" FROM CoeficienteAtivo cfa");
        corpoBuilder.append(" INNER JOIN cfa.prazoConsignataria pzc");
        corpoBuilder.append(" WHERE cfa.cftDataFimVig < current_date()");
        
        // Define os valores para os parâmetros nomeados
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        return query;
    }

    @Override
    protected String[] getFields() {        
        return new String[] {
                Columns.CFA_CODIGO,
                Columns.CFA_PRZ_CSA_CODIGO,
                Columns.CFA_DIA,
                Columns.CFA_VLR,
                Columns.CFA_DATA_INI_VIG,
                Columns.CFA_DATA_FIM_VIG,
                Columns.CFA_DATA_CADASTRO
        };
    }
}
