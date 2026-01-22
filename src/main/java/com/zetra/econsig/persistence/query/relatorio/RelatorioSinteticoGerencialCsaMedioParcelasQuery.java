package com.zetra.econsig.persistence.query.relatorio;

import java.text.ParseException;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;

/**
 * <p>Title: RelatorioVolumeAverbacaoCsaQuery</p>
 * <p>Description: Recuperar voluma averbação por tipo gráfico</p>
 * <p>Copyright: Copyright (c) 2024</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioSinteticoGerencialCsaMedioParcelasQuery extends ReportHQuery {

    public String csaCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final StringBuilder corpo = new StringBuilder();
        corpo.append(" SELECT to_decimal(AVG(prd.prdVlrPrevisto), 5 ,2)");
        corpo.append(" FROM ParcelaDesconto prd");
        corpo.append(" INNER JOIN prd.autDesconto ade");
        corpo.append(" INNER JOIN ade.verbaConvenio vco ");
        corpo.append(" INNER JOIN vco.convenio cnv ");
        corpo.append(" WHERE prd.prdDataDesconto BETWEEN add_month(:periodoIni, -6 ) AND :periodoFim");
        corpo.append(" AND cnv.csaCodigo = :csaCodigo");

        final Query<Object[]> query = instanciarQuery(session, corpo.toString());
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);

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
            "VALOR_MEDIO"
        };
    }
}
