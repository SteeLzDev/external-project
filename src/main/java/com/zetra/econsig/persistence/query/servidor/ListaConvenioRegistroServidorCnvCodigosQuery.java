package com.zetra.econsig.persistence.query.servidor;

import java.util.List;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConvenioRegistroServidorCnvCodigosQuery</p>
 * <p>Description: Listagem de convênios bloqueados do servidor filtrado por cnvCodigos.</p>
 * <p>Copyright: Copyright (c) 2024</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConvenioRegistroServidorCnvCodigosQuery extends HQuery {

    public List<String> cnvCodigos;
    public AcessoSistema responsavel;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final String corpo = "select " +
                "param.cnvCodigo, " +
                "param.rseCodigo, " +
                "cnv.csaCodigo ";

        final StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from ParamConvenioRegistroSer param ");
        corpoBuilder.append(" inner join param.convenio cnv");

        if (!TextHelper.isNull(cnvCodigos)) {
            corpoBuilder.append(" WHERE param.cnvCodigo ").append(criaClausulaNomeada("cnvCodigos", cnvCodigos));
            if (responsavel.isCsa()) {
                corpoBuilder.append(" AND (param.pcrVlrCsa IS NOT NULL AND param.pcrVlrCsa != '') ");
            } else if (responsavel.isCseSup()) {
                corpoBuilder.append(" AND (param.pcrVlrCse IS NOT NULL AND param.pcrVlrCse != '') ");
            }
        }

        corpoBuilder.append(" order by param.rseCodigo, cnv.csaCodigo ");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(cnvCodigos)) {
            defineValorClausulaNomeada("cnvCodigos", cnvCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CNV_CODIGO,
                Columns.RSE_CODIGO,
                Columns.CSA_CODIGO
        };
    }
}
