package com.zetra.econsig.persistence.query.periodo;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemPeriodoExportacaoAtualQuery</p>
 * <p>Description: Retorna o período de exportação atual.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemPeriodoExportacaoAtualQuery extends HQuery {
    public List<String> orgCodigos;
    public List<String> estCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select pex.pexPeriodo, pex.pexDataIni, pex.pexDataFim, count(*) as qtde");
        corpoBuilder.append(" from PeriodoExportacao pex ");

        if (estCodigos != null && estCodigos.size() > 0) {
            corpoBuilder.append(" inner join pex.orgao org");
        }
        corpoBuilder.append(" where 1=1");
        if (estCodigos != null && estCodigos.size() > 0) {
            corpoBuilder.append(" and org.estabelecimento.estCodigo ").append(criaClausulaNomeada("estCodigos", estCodigos));
        }
        if (orgCodigos != null && orgCodigos.size() > 0) {
            corpoBuilder.append(" and pex.orgCodigo ").append(criaClausulaNomeada("orgCodigos", orgCodigos));
        }

        corpoBuilder.append(" group by pex.pexPeriodo, pex.pexDataIni, pex.pexDataFim ");
        corpoBuilder.append(" order by count(*) desc ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        query.setMaxResults(1);

        if (estCodigos != null && estCodigos.size() > 0) {
            defineValorClausulaNomeada("estCodigos", estCodigos, query);
        }
        if (orgCodigos != null && orgCodigos.size() > 0) {
            defineValorClausulaNomeada("orgCodigos", orgCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.PEX_PERIODO,
                Columns.PEX_DATA_INI,
                Columns.PEX_DATA_FIM,
                "QUANTIDADE"
        };
    }
}