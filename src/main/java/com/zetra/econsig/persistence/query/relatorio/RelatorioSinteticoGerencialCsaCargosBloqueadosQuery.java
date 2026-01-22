package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;

/**
 * <p>Title: RelatorioVolumeAverbacaoCsaQuery</p>
 * <p>Description: Recuperar voluma averbação por tipo gráfico</p>
 * <p>Copyright: Copyright (c) 2024</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioSinteticoGerencialCsaCargosBloqueadosQuery extends ReportHQuery {

    public String csaCodigo;
    public boolean defaultBloqueio;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final StringBuilder corpo = new StringBuilder();
        corpo.append(" SELECT DISTINCT CONCAT(vrs.vrsIdentificador, ' - ' , vrs.vrsDescricao)");
        if(defaultBloqueio) {
            corpo.append(" FROM VinculoRegistroServidor vrs");
            corpo.append(" LEFT JOIN vrs.convenioVinculoRegistroSet cvr");
        } else {
            corpo.append(" FROM VinculoRegistroServidor vrs");
            corpo.append(" INNER JOIN vrs.convenioVinculoRegistroSet cvr");
        }
        corpo.append(" WHERE cvr.csaCodigo = :csaCodigo");
        if(defaultBloqueio) {
            corpo.append(" AND cvr.vrsCodigo IS NULL");
        }

        final Query<Object[]> query = instanciarQuery(session, corpo.toString());
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
            "NOME"
        };
    }
}
