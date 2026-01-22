package com.zetra.econsig.persistence.query.parcela;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.text.ParseException;

/**
 * <p>Title: RelatorioMovFinSerQuery</p>
 * <p>Description: Relatório de Movimentação financeira do servidor</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class ObtemTotalParcelasEntreQuery extends HQuery {

    public String adeCodigo;
    public String periodoIni;
    public String periodoFim;
    public boolean relatorio = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT COUNT(*) ");
        corpoBuilder.append("FROM ParcelaDesconto prd ");
        corpoBuilder.append("WHERE 1 = 1 ");

        if (!TextHelper.isNull(adeCodigo)) {
            corpoBuilder.append(" AND prd.autDesconto.adeCodigo ").append(criaClausulaNomeada("adeCodigo", adeCodigo));
        }

        if (!TextHelper.isNull(periodoIni) && !TextHelper.isNull(periodoFim)) {
            corpoBuilder.append(" AND prd.prdDataDesconto between to_date(:periodoIni) and to_date(:periodoFim)");
        } else if (!TextHelper.isNull(periodoIni) && TextHelper.isNull(periodoFim)) {
            corpoBuilder.append(" AND prd.prdDataDesconto = to_date(:periodoIni) ");
        }

        if (relatorio) {
            corpoBuilder.append(" AND prd.statusParcelaDesconto.spdCodigo !='").append(CodedValues.SPD_EMABERTO).append("' ");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        if (!TextHelper.isNull(adeCodigo)) {
            defineValorClausulaNomeada("adeCodigo", adeCodigo, query);
        }

        if (!TextHelper.isNull(periodoIni) && !TextHelper.isNull(periodoFim)) {
            try {
                defineValorClausulaNomeada("periodoIni", DateHelper.parse(periodoIni, "yyyy-MM-dd"), query);
                defineValorClausulaNomeada("periodoFim", DateHelper.parse(periodoIni, "yyyy-MM-dd"), query);
            } catch (ParseException ex) {
                throw new HQueryException("mensagem.erro.data.informada.invalida.arg0", (AcessoSistema) null, periodoIni + " , " + periodoFim);
            }
        } else if (!TextHelper.isNull(periodoIni) && TextHelper.isNull(periodoFim)) {
            try {
                defineValorClausulaNomeada("periodoIni", DateHelper.parse(periodoIni, "yyyy-MM-dd"), query);
            } catch (ParseException ex) {
                throw new HQueryException("mensagem.erro.data.informada.invalida.arg0", (AcessoSistema) null, periodoIni + " , " + periodoFim);
            }
        }

        return query;
    }
}