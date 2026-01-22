package com.zetra.econsig.persistence.query.beneficios;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaBeneficioQuery</p>
 * <p>Description: Listagem de beneficios</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaBeneficioQuery extends HQuery {
    public Object csaCodigo;
    public Object benCodigo;

    public void setCriterios(TransferObject criterio) {
        csaCodigo = criterio.getAttribute("csaCodigo");

    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "SELECT beneficio.benCodigo, " +
            		"consignataria.csaCodigo, " +
            		"naturezaServico.nseCodigo, " +
            		"beneficio.benDescricao, " +
            		"beneficio.benCodigoPlano, " +
            		"beneficio.benCodigoRegistro, " +
            		"beneficio.benCodigoContrato " +
            		"from Beneficio beneficio " +
            		"inner join beneficio.consignataria as consignataria " +
            		"inner join beneficio.naturezaServico as naturezaServico";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" where 1 = 1 ");

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }
        
        if (!TextHelper.isNull(benCodigo)) {
        	corpoBuilder.append(" and beneficio.benCodigo ").append(criaClausulaNomeada("benCodigo", benCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(benCodigo)) {
        	defineValorClausulaNomeada("benCodigo", benCodigo, query);
        }
        
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] { Columns.BEN_CODIGO,
                Columns.BEN_CSA_CODIGO,
                Columns.BEN_NSE_CODIGO,
                Columns.BEN_DESCRICAO,
                Columns.BEN_CODIGO_PLANO,
                Columns.BEN_CODIGO_REGISTRO,
                Columns.BEN_CODIGO_CONTRATO
                };
    }
}
