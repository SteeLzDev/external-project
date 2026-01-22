package com.zetra.econsig.persistence.query.consignacao;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignacaoEmDuplicidadeQuery</p>
 * <p>Description: Listagem de Consignações ativas em duplicidade com os filtros dados</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignacaoEmDuplicidadeQuery extends HQuery {

    public String rseCodigo;
    public String csaCodigo;
    public String nseCodigo;
    public BigDecimal adeVlr;
    public Integer adePrazo;
    public Date adeAnoMesIni;
    public List<String> sadCodigos;
    public List<String> adeCodigosNaoConsiderar;
    public AcessoSistema responsavel;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        if (TextHelper.isNull(rseCodigo) || TextHelper.isNull(csaCodigo) || TextHelper.isNull(nseCodigo) || adeVlr == null || adeAnoMesIni == null) {
            throw new HQueryException("mensagem.erro.filtros.insuficiente.busca.ade.duplicidade", responsavel);
        }

        String corpo = "select ade.adeCodigo, ade.adeData, ade.adeAnoMesIni ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append("from AutDesconto ade ");
        corpoBuilder.append("inner join ade.statusAutorizacaoDesconto sad ");
        corpoBuilder.append("inner join ade.verbaConvenio vco ");
        corpoBuilder.append("inner join vco.convenio cnv ");
        corpoBuilder.append("inner join cnv.servico svc ");

        corpoBuilder.append(" WHERE ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        corpoBuilder.append(" AND cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        corpoBuilder.append(" AND svc.naturezaServico.nseCodigo ").append(criaClausulaNomeada("nseCodigo", nseCodigo));
        if (adePrazo != null && adePrazo > 0) {
            corpoBuilder.append(" AND ade.adePrazo ").append(criaClausulaNomeada("adePrazo", adePrazo));
        } else {
            corpoBuilder.append(" AND ade.adePrazo IS NULL");
        }
        corpoBuilder.append(" AND ade.adeVlr ").append(criaClausulaNomeada("adeVlr", adeVlr));
        corpoBuilder.append(" AND ade.adeAnoMesIni ").append(criaClausulaNomeada("adeAnoMesIni", adeAnoMesIni));
        corpoBuilder.append(" AND ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));

        if (adeCodigosNaoConsiderar != null && !adeCodigosNaoConsiderar.isEmpty()) {
            List<String> codigos = new ArrayList<>(adeCodigosNaoConsiderar);
            codigos.add(CodedValues.NOT_EQUAL_KEY);
            corpoBuilder.append(" AND ade.adeCodigo ").append(criaClausulaNomeada("adeCodigos", codigos));
        }

        corpoBuilder.append(" ORDER BY ade.adeData DESC");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        defineValorClausulaNomeada("nseCodigo", nseCodigo, query);
        if (adePrazo != null && adePrazo > 0) {
            defineValorClausulaNomeada("adePrazo", adePrazo, query);
        }
        defineValorClausulaNomeada("adeVlr", adeVlr, query);
        defineValorClausulaNomeada("adeAnoMesIni", adeAnoMesIni, query);
        defineValorClausulaNomeada("sadCodigos", sadCodigos, query);

        if (adeCodigosNaoConsiderar != null && !adeCodigosNaoConsiderar.isEmpty()) {
            defineValorClausulaNomeada("adeCodigos", adeCodigosNaoConsiderar, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
               Columns.ADE_CODIGO,
               Columns.ADE_DATA,
               Columns.ADE_ANO_MES_INI
        };
    }
}
