package com.zetra.econsig.persistence.query.consignacao;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ObtemTotalAdeVlrPorPeriodoInclusaoQuery</p>
 * <p>Description: Soma o valor dos contratos criados dentro do período da especificado para determinada margem</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemTotalAdeVlrPorPeriodoInclusaoQuery extends HQuery {

    public String rseCodigo;
    public Date periodoIni;
    public Date periodoFim;
    public Short adeIncMargem;
    public List<String> adeCodigosExclusao;
    public AcessoSistema responsavel;
    public boolean verificaAdeAnoMesIni = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select sum(ade.adeVlr) as total ");
        corpoBuilder.append("from AutDesconto ade ");
        corpoBuilder.append("where ade.statusAutorizacaoDesconto.sadCodigo in (:sadCodigo) ");

        if(!verificaAdeAnoMesIni) {
            if (periodoIni != null) {
                corpoBuilder.append(" AND ade.adeData >= :paramPeriodoIni ");
            }
            if (periodoFim != null) {
                corpoBuilder.append(" AND ade.adeData <= :paramPeriodoFim ");
            }
        } else {
            if (periodoIni != null) {
                corpoBuilder.append(" AND ade.adeAnoMesIni >= :paramPeriodoIni ");
            }
            if (periodoFim != null) {
                corpoBuilder.append(" AND ade.adeAnoMesIni <= :paramPeriodoFim ");
            }
        }

        if (adeIncMargem != null) {
            corpoBuilder.append(" AND ade.adeIncMargem ").append(criaClausulaNomeada("adeIncMargem", adeIncMargem));
        }
        if (rseCodigo != null) {
            corpoBuilder.append(" AND ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        }
        if (adeCodigosExclusao != null && !adeCodigosExclusao.isEmpty()) {
            corpoBuilder.append(" AND ade.adeCodigo not in (:adeCodigosExclusao) ");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("sadCodigo", CodedValues.SAD_CODIGOS_ABERTOS_INCIDEM_MARGEM_BRUTA, query);

        if (periodoIni != null) {
            defineValorClausulaNomeada("paramPeriodoIni", periodoIni, query);
        }
        if (periodoFim != null) {
            defineValorClausulaNomeada("paramPeriodoFim", periodoFim, query);
        }
        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }
        if (adeIncMargem != null) {
            defineValorClausulaNomeada("adeIncMargem", adeIncMargem, query);
        }
        if (adeCodigosExclusao != null && !adeCodigosExclusao.isEmpty()) {
            defineValorClausulaNomeada("adeCodigosExclusao", adeCodigosExclusao, query);
        }

        return query;
    }

}
