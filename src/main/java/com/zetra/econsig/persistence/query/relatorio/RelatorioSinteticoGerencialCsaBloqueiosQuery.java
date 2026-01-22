package com.zetra.econsig.persistence.query.relatorio;

import java.text.ParseException;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
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
public class RelatorioSinteticoGerencialCsaBloqueiosQuery extends ReportHQuery {

    public String csaCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final String tocBloqueio = CodedValues.TOC_BLOQ_CSA_POR_DATA_EXPIRACAO;
        final String tocDesbloqueio = CodedValues.TOC_DESBLOQUEIO_CONSIGNATARIA_PENDENTE;

        final StringBuilder corpo = new StringBuilder();
        corpo.append(" SELECT occ_bloqueio.occData, occ_desbloq.occData, date_diff(occ_desbloq.occData, occ_bloqueio.occData) , occ_desbloq.occObs");
        corpo.append(" FROM Consignataria csa");
        corpo.append(" INNER JOIN csa.ocorrenciaConsignatariaSet occ_bloqueio");
        corpo.append(" INNER JOIN csa.ocorrenciaConsignatariaSet occ_desbloq");
        corpo.append(" WHERE occ_bloqueio.tocCodigo = :tocBloqueio AND occ_desbloq.tocCodigo = :tocDesbloqueio ");
        corpo.append(" AND occ_desbloq.occData > occ_bloqueio.occData");
        corpo.append(" AND occ_bloqueio.occData BETWEEN add_month(:periodoIni, -6) AND :periodoFim");
        corpo.append(" AND csa.csaCodigo = :csaCodigo ");

        final Query<Object[]> query = instanciarQuery(session, corpo.toString());
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        defineValorClausulaNomeada("tocBloqueio", tocBloqueio, query);
        defineValorClausulaNomeada("tocDesbloqueio", tocDesbloqueio, query);

        try {
        	defineValorClausulaNomeada("periodoIni", DateHelper.parse(DateHelper.format(DateHelper.getSystemDatetime(), "yyyy-MM-dd") + " 00:00:00", "yyyy-MM-dd HH:mm:ss"), query);
        	defineValorClausulaNomeada("periodoFim", DateHelper.parse(DateHelper.format(DateHelper.getSystemDatetime(), "yyyy-MM-dd") + " 23:59:59", "yyyy-MM-dd HH:mm:ss"), query);
        } catch (ParseException ex) {
            throw new HQueryException("mensagem.erro.data.fim.parse.invalido",  (AcessoSistema) null);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
            "DATA_INICIO",
            "DATA_FIM",
            "QUANTIDADE",
            "OBSERVACAO"
        };
    }
}
