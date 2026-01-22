package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p> Title: RelatorioHistoricoLoginQuery</p>
 * <p> Description: Query do relatório de histórico de login.</p>
 * <p> Copyright: Copyright (c) 2006-2023</p>
 * <p> Company: ZetraSoft Ltda. </p>
 * @author Eduardo Fortes, Leonel Martins
 */
public class RelatorioHistoricoLoginQuery extends ReportHQuery {

    public AcessoSistema responsavel;
    private String dataIni;
    private String dataFim;

    @Override
    public void setCriterios(TransferObject criterio) {
        dataIni = (String) criterio.getAttribute("DATA_INI");
        dataFim = (String) criterio.getAttribute("DATA_FIM");
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final StringBuilder corpo = new StringBuilder();
        corpo.append("select count(distinct hlo.usuCodigo) as Total, count(hlo.usuCodigo) as Distintos, hlo.hloCanal as Canal ")
             .append("from HistoricoLogin hlo LEFT JOIN hlo.usuario usu INNER JOIN usu.usuarioSerSet usuarioSer ")
             .append("where hlo.hloCanal in ('1', '3') and hlo.hloData between :dataIni and :dataFim group by (hlo.hloCanal)");

        final Query<Object[]> query = instanciarQuery(session, corpo.toString());
        defineValorClausulaNomeada("dataIni", parseDateTimeString(dataIni), query);
        defineValorClausulaNomeada("dataFim", parseDateTimeString(dataFim), query);

        return query;
    }

    @Override
    protected String[] getFields() {

        return new String[] {
                "Total",
                "Distintos",
                "Canal"
        };
    }

}
