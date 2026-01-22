package com.zetra.econsig.persistence.query.destinatarioemailcsasvc;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaDestinatarioEmailCsaSvcQuery</p>
 * <p>Description: Listagem de DestinatarioEmailCsaSvc  </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaDestinatarioEmailCsaSvcQuery extends HQuery {

    public String funCodigo;

    public String papCodigo;
    
    public String csaCodigo;

    public String svcCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder corpoBuilder = new StringBuilder();
        
        corpoBuilder.append("select dcs.funcao.funCodigo, dcs.papel.papCodigo, dcs.consignataria.csaCodigo, dcs.servico.svcCodigo ");
        corpoBuilder.append("from DestinatarioEmailCsaSvc dcs ");
        corpoBuilder.append("where 1=1 ");

        if (!TextHelper.isNull(funCodigo)) {
            corpoBuilder.append(" and dcs.funcao.funCodigo ").append(criaClausulaNomeada("funCodigo", funCodigo));
        }

        if (!TextHelper.isNull(papCodigo)) {
            corpoBuilder.append(" and dcs.papel.papCodigo ").append(criaClausulaNomeada("papCodigo", papCodigo));
        }
        
        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and dcs.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }
        
        if (!TextHelper.isNull(svcCodigo)) {
            corpoBuilder.append(" and dcs.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        // Seta os parâmetros na query
        if (!TextHelper.isNull(funCodigo)) {
            defineValorClausulaNomeada("funCodigo", funCodigo, query);
        }

        if (!TextHelper.isNull(papCodigo)) {
            defineValorClausulaNomeada("papCodigo", papCodigo, query);
        }
        
        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
			Columns.FUN_CODIGO,
			Columns.PAP_CODIGO,
			Columns.CSA_CODIGO,
			Columns.SVC_CODIGO
        };
    }
}
