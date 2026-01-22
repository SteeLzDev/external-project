package com.zetra.econsig.persistence.query.margem;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ObtemTotalValorExcedenteProporcionalQuery</p>
 * <p>Description: Totaliza o valor excecente utilizado da margem proporcional dos contratos
 * para um servidor de acordo com os demais parâmetros informados</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemTotalValorExcedenteProporcionalQuery extends HQuery {

    public String rseCodigo;
    public String csaCodigo;
    public Short adeIncMargem;
    public List<String> sadCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String tdaCodigo = CodedValues.TDA_VALOR_EXCEDENTE_MARGEM_PROPORCIONAL;

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select sum(case isnumeric(dad.dadValor) when 1 then to_decimal(dad.dadValor, 13, 2) else 0 end) ");
        corpoBuilder.append("from DadosAutorizacaoDesconto dad ");
        corpoBuilder.append("inner join dad.autDesconto ade ");
        corpoBuilder.append("inner join ade.verbaConvenio vco ");
        corpoBuilder.append("inner join vco.convenio cnv ");

        corpoBuilder.append("where ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        corpoBuilder.append(" and dad.tipoDadoAdicional.tdaCodigo ").append(criaClausulaNomeada("tdaCodigo", tdaCodigo));
        corpoBuilder.append(" and ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));

        // Contrato incide na margem informada por parâmetro, ou não incide margem, mas o serviço incide
        corpoBuilder.append(" and (ade.adeIncMargem ").append(criaClausulaNomeada("adeIncMargem", adeIncMargem));
        corpoBuilder.append("  or (ade.adeIncMargem = 0 and exists (");
        corpoBuilder.append("    select 1 from ParamSvcConsignante pse ");
        corpoBuilder.append("    where pse.servico.svcCodigo = cnv.servico.svcCodigo ");
        corpoBuilder.append("    and pse.tipoParamSvc.tpsCodigo = :tpsCodigo");
        corpoBuilder.append("    and to_short(pse.pseVlr) ").append(criaClausulaNomeada("adeIncMargem", adeIncMargem));
        corpoBuilder.append("    ) ");
        corpoBuilder.append("  ) ");
        corpoBuilder.append(") ");

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        defineValorClausulaNomeada("tdaCodigo", tdaCodigo, query);
        defineValorClausulaNomeada("adeIncMargem", adeIncMargem, query);
        defineValorClausulaNomeada("sadCodigos", sadCodigos, query);
        defineValorClausulaNomeada("tpsCodigo", CodedValues.TPS_INCIDE_MARGEM, query);

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }
}
