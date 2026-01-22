package com.zetra.econsig.persistence.query.consignacao;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;

/**
 * <p>Title: ObtemTotalConsignacoesPorRseNseQuery</p>
 * <p>Description: Obtem o total de consignações por registro servidor e natureza de serviço</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemTotalConsignacoesPorRseNseQuery extends HQuery {

    public String rseCodigo;
    public String nseCodigo;
    public String csaCodigo;

    public List<String> sadCodigos;
    public Date adeAnoMesIni;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select count(*) ");
        corpoBuilder.append("from AutDesconto ade ");
        corpoBuilder.append("inner join ade.verbaConvenio vco ");
        corpoBuilder.append("inner join vco.convenio cnv ");
        corpoBuilder.append("inner join cnv.servico svc ");
        corpoBuilder.append("where ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        corpoBuilder.append(" and svc.naturezaServico.nseCodigo ").append(criaClausulaNomeada("nseCodigo", nseCodigo));
        corpoBuilder.append(" and cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));


        if (sadCodigos != null && sadCodigos.size() > 0) {
            corpoBuilder.append(" and ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigo", sadCodigos));
        }

        if (adeAnoMesIni != null) {
            corpoBuilder.append(" and ade.adeAnoMesIni ").append(criaClausulaNomeada("adeAnoMesIni", adeAnoMesIni));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        defineValorClausulaNomeada("nseCodigo", nseCodigo, query);
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);

        if (sadCodigos != null && sadCodigos.size() > 0) {
            defineValorClausulaNomeada("sadCodigo", sadCodigos, query);
        }

        if (adeAnoMesIni != null) {
            defineValorClausulaNomeada("adeAnoMesIni", adeAnoMesIni, query);
        }

        return query;
    }
}
