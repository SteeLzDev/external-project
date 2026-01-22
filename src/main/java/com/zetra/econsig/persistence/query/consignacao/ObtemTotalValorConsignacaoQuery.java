package com.zetra.econsig.persistence.query.consignacao;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ObtemTotalValorConsignacaoQuery</p>
 * <p>Description: Totaliza o valor dos contratos para um servidor de acordo
 * com os demais parâmetros informados</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemTotalValorConsignacaoQuery extends HQuery {

    public String rseCodigo;
    public String csaCodigo;
    public Short adeIncMargem;
    public List<String> sadCodigos;
    public List<String> adeCodigosExceto;
    public boolean tratamentoEspecialMargem = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select sum(ade.adeVlr) ");
        corpoBuilder.append("from AutDesconto ade ");
        corpoBuilder.append("inner join ade.verbaConvenio vco ");
        corpoBuilder.append("inner join vco.convenio cnv ");
        if (tratamentoEspecialMargem) {
            corpoBuilder.append(" inner join cnv.servico svc ");
            corpoBuilder.append(" inner join svc.paramSvcConsignanteSet psexx with ");
            corpoBuilder.append(" psexx.tipoParamSvc.tpsCodigo = :tpsServicoTratamentoEspecial ");
        }

        corpoBuilder.append("where ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        corpoBuilder.append(" and ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));

        if (adeCodigosExceto != null && !adeCodigosExceto.isEmpty()) {
            corpoBuilder.append(" and ade.adeCodigo NOT IN (:adeCodigosExceto) ");
        }

        if (tratamentoEspecialMargem) {
            corpoBuilder.append(" and ade.adeIncMargem = 0 and psexx.pseVlr = '1' "); // No Espirito Santo, Saúde não incide e o serviço possui tratamento especial de margem
        } else if (adeIncMargem != null) {
            // Contrato incide na margem informada por parâmetro, ou não incide margem, mas o serviço incide
            corpoBuilder.append(" and (ade.adeIncMargem ").append(criaClausulaNomeada("adeIncMargem", adeIncMargem));
            corpoBuilder.append("  or (ade.adeIncMargem = 0 and exists (");
            corpoBuilder.append("    select 1 from ParamSvcConsignante pse ");
            corpoBuilder.append("    where pse.servico.svcCodigo = cnv.servico.svcCodigo ");
            corpoBuilder.append("    and pse.tipoParamSvc.tpsCodigo = :tpsIncideMargem ");
            corpoBuilder.append("    and pse.pseVlr ").append(criaClausulaNomeada("adeIncMargem", adeIncMargem));
            corpoBuilder.append("    ) ");
            corpoBuilder.append("  ) ");
            corpoBuilder.append(") ");
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        if (tratamentoEspecialMargem) {
            defineValorClausulaNomeada("tpsServicoTratamentoEspecial", CodedValues.TPS_SERVICO_TRATAMENTO_ESPECIAL_MARGEM, query);
        } else if (adeIncMargem != null) {
            defineValorClausulaNomeada("adeIncMargem", adeIncMargem, query);
            defineValorClausulaNomeada("tpsIncideMargem", CodedValues.TPS_INCIDE_MARGEM, query);
        }

        defineValorClausulaNomeada("sadCodigos", sadCodigos, query);

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (adeCodigosExceto != null && !adeCodigosExceto.isEmpty()) {
            defineValorClausulaNomeada("adeCodigosExceto", adeCodigosExceto, query);
        }

        return query;
    }
}
