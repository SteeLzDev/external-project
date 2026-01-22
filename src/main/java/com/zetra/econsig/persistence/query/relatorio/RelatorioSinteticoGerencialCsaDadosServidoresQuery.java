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
public class RelatorioSinteticoGerencialCsaDadosServidoresQuery extends ReportHQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final StringBuilder corpo = new StringBuilder();
        corpo.append(" SELECT srs.srsDescricao as servidores, count(*) as quantidade");
        corpo.append(" FROM RegistroServidor rse");
        corpo.append(" INNER JOIN rse.statusRegistroServidor srs");
        corpo.append(" group by srs.srsCodigo");

        return instanciarQuery(session, corpo.toString());
    }

    @Override
    protected String[] getFields() {
        return new String[] {
            "STATUS",
            "QUANTIDADE"
        };
    }
}
