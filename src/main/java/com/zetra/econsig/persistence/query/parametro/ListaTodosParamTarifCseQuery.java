package com.zetra.econsig.persistence.query.parametro;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaTodosParamTarifCseQuery</p>
 * <p>Description: Listagem de todos os parâmetros de tarifação, mesmo que 
 * só exista o tipo de parâmetro cadastrado.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaTodosParamTarifCseQuery extends HQuery {
    
    public String svcCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                "tpt.tptCodigo, " +
                "tpt.tptDescricao, " +
                "tpt.tptTipoInterface, " +
                "pcv.pcvCodigo, " +
                "pcv.pcvVlr, " +
                "pcv.pcvDecimais, " +
                "pcv.pcvFormaCalc, " +
                "pcv.pcvBaseCalc ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from TipoParamTarifCse tpt");
        corpoBuilder.append(" left outer join tpt.paramTarifConsignanteSet pcv WITH ");
        corpoBuilder.append(" pcv.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        corpoBuilder.append(" order by tpt.tptDescricao");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        
        return query;
    }
    
    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.TPT_CODIGO,
                Columns.TPT_DESCRICAO,
                Columns.TPT_TIPO_INTERFACE,
                Columns.PCV_CODIGO,
                Columns.PCV_VLR,
                Columns.PCV_DECIMAIS,
                Columns.PCV_FORMA_CALC,
                Columns.PCV_BASE_CALC
        };
    }
}
