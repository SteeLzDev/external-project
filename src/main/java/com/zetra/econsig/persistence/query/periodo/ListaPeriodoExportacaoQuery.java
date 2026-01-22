package com.zetra.econsig.persistence.query.periodo;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaPeriodoExportacaoQuery</p>
 * <p>Description: Recupera o período de exportação..</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaPeriodoExportacaoQuery extends HQuery {

    public List<String> orgCodigos;
    public List<String> estCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String corpo = "select " +
                "org.orgCodigo, " +
                "org.orgIdentificador, " +
                "org.orgNome, " +
                "est.estCodigo, " +
                "est.estIdentificador, " +
                "est.estNome, " +
                "pex.orgCodigo, " +
                "pex.pexDiaCorte, " +
                "pex.pexDataIni, " +
                "pex.pexDataFim, " +
                "pex.pexPeriodo ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from PeriodoExportacao pex");
        corpoBuilder.append(" inner join pex.orgao org");
        corpoBuilder.append(" inner join org.estabelecimento est");

        corpoBuilder.append(" where 1=1");

        if (estCodigos != null && estCodigos.size() > 0) {
            corpoBuilder.append(" and org.estabelecimento.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigos));
        }
        if (orgCodigos != null && orgCodigos.size() > 0) {
            corpoBuilder.append(" and pex.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (estCodigos != null && estCodigos.size() > 0) {
            defineValorClausulaNomeada("estCodigo", estCodigos, query);
        }
        if (orgCodigos != null && orgCodigos.size() > 0) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ORG_CODIGO,
                Columns.ORG_IDENTIFICADOR,
                Columns.ORG_NOME,
                Columns.EST_CODIGO,
                Columns.EST_IDENTIFICADOR,
                Columns.EST_NOME,
                Columns.PEX_ORG_CODIGO,
                Columns.PEX_DIA_CORTE,
                Columns.PEX_DATA_INI,
                Columns.PEX_DATA_FIM,
                Columns.PEX_PERIODO
        };
    }
}
