package com.zetra.econsig.persistence.query.convenio;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConveniosParaAlteracaoQuery</p>
 * <p>Description: Listagem de convênios para alteração.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConveniosParaAlteracaoQuery extends HQuery {

    public String svcCodigo;
    public String csaCodigo;
    public String orgCodigo;
    public List<String> codigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        if (TextHelper.isNull(svcCodigo)) {
            throw new HQueryException("mensagem.erro.servico.nao.informado", (AcessoSistema) null);
        }

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select cnv.cnvCodigo ");
        corpoBuilder.append("from Convenio cnv ");
        corpoBuilder.append("where 1 = 1 ");

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" AND cnv.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }

        corpoBuilder.append(" AND cnv.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));

        if (codigos != null && !codigos.isEmpty()) {
            if (!TextHelper.isNull(csaCodigo)) {
                corpoBuilder.append(" AND cnv.orgao.orgCodigo ").append(criaClausulaNomeada("codigos", codigos));
            } else if (!TextHelper.isNull(orgCodigo)) {
                corpoBuilder.append(" AND cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("codigos", codigos));
            }
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }
        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }
        if (codigos != null && !codigos.isEmpty()) {
            defineValorClausulaNomeada("codigos", codigos, query);
        }
        defineValorClausulaNomeada("svcCodigo", svcCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CNV_CODIGO
        };
    }

}
