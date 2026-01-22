package com.zetra.econsig.persistence.query.subrelatorio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaSubrelatorioQuery</p>
 * <p>Description: Querys de listagem de subrelatorios</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Eduardo Fortes
 */
public class ListaSubrelatorioQuery extends ReportHQuery{
    public String sreCodigo;
    public String relCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String corpo = "select sre.sreCodigo, "
            + " sre.relCodigo, "
            + " sre.sreTemplateJasper, "
            + " sre.sreNomeParametro, "
            + " sre.sreTemplateSql";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from Subrelatorio sre ");
        corpoBuilder.append(" where 1=1  ");

        if (!TextHelper.isNull(sreCodigo)) {
            corpoBuilder.append(" and ").append(criaClausulaNomeada("sre.sreCodigo", "sreCodigo", sreCodigo));
        }
        if (!TextHelper.isNull(relCodigo)) {
            corpoBuilder.append(" and ").append(criaClausulaNomeada("sre.relCodigo", "relCodigo", relCodigo));
        }
        
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(sreCodigo)) {
            defineValorClausulaNomeada("sreCodigo", sreCodigo, query);
        }
        if (!TextHelper.isNull(relCodigo)) {
            defineValorClausulaNomeada("relCodigo", relCodigo, query);
        }
        
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SRE_CODIGO,
                Columns.REL_CODIGO,
                Columns.SRE_TEMPLATE_JASPER,
                Columns.SRE_NOME_PARAMETRO,
                Columns.SRE_TEMPLATE_SQL,
                };
    }
}
