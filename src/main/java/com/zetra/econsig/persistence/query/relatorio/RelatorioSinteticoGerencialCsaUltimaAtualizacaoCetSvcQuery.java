package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: RelatorioSinteticoGerencialCsaBloqueiosQuery</p>
 * <p>Description: Recuperar voluma averbação por tipo gráfico</p>
 * <p>Copyright: Copyright (c) 2024</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioSinteticoGerencialCsaUltimaAtualizacaoCetSvcQuery extends ReportHQuery {

    public String csaCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final String tps314 = CodedValues.TPS_DIAS_VIGENCIA_CET;

        final StringBuilder corpo = new StringBuilder();
        corpo.append(" SELECT svc.svcDescricao, tca.cftVlr , tca.cftDataFimVig ");
        corpo.append(" FROM CoeficienteAtivo tca ");
        corpo.append(" INNER JOIN tca.prazoConsignataria przc");
        corpo.append(" INNER JOIN przc.prazo prz");
        corpo.append(" INNER JOIN prz.servico svc");
        corpo.append(" INNER JOIN svc.paramSvcConsignanteSet tps");
        corpo.append(" WHERE tps.tpsCodigo= :tps314 ");
        corpo.append(" AND tca.cftDataFimVig is not null");
        corpo.append(" AND przc.csaCodigo = :csaCodigo ");
        corpo.append(" group by svc.svcCodigo");

        final Query<Object[]> query = instanciarQuery(session, corpo.toString());
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        defineValorClausulaNomeada("tps314", tps314, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
            "NOME",
            "VALOR",
            "DATA_FIM"
        };
    }
}
