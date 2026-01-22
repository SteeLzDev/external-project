package com.zetra.econsig.persistence.query.relatorio;

import java.text.ParseException;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: RelatorioVolumeAverbacaoCsaQuery</p>
 * <p>Description: Recuperar voluma averbação por tipo gráfico</p>
 * <p>Copyright: Copyright (c) 2024</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioSinteticoGerencialCsaVolumeAverbacaoGraficoQuery extends ReportHQuery {

    public AcessoSistema responsavel;
    public String csaCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final String nseRenegecociacao = CodedValues.TNT_CONTROLE_RENEGOCIACAO;
        final String nseCompra = CodedValues.TNT_CONTROLE_COMPRA;

        final StringBuilder corpo = new StringBuilder();
        corpo.append(" select");
        corpo.append("   case when not exists (select 1 from RelacionamentoAutorizacao rad where ade.adeCodigo = rad.adeCodigoDestino and rad.tntCodigo in ('").append(nseRenegecociacao).append("','").append(nseCompra).append("')) then '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.averbacao.grafico.novo.contrato", responsavel)).append("'");
        corpo.append("        when exists (select 1 from RelacionamentoAutorizacao rad where ade.adeCodigo = rad.adeCodigoDestino and rad.tntCodigo = :nseRenegecociacao ) then '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.averbacao.grafico.novo.contrato.renegociacao", responsavel)).append("'");
        corpo.append("        when exists (select 1 from RelacionamentoAutorizacao rad where ade.adeCodigo = rad.adeCodigoDestino and rad.tntCodigo = :nseCompra) then '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.averbacao.grafico.novo.contrato.compra", responsavel)).append("'");
        corpo.append("   end as tipo,");
        corpo.append("   to_decimal(count(*) / (");
        corpo.append("       select count(*)");
        corpo.append("       FROM AutDesconto ade");
        corpo.append("       inner join ade.verbaConvenio vco");
        corpo.append("       inner join vco.convenio cnv ");
        corpo.append("       inner join cnv.consignataria csa ");
        corpo.append("       where ade.adeData between add_month(:periodoIni , -6) and :periodoFim");
        corpo.append("       and csa.csaCodigo = :csaCodigo");
        corpo.append("    ) * 100, 5, 2) as percentual");
        corpo.append(" FROM AutDesconto ade");
        corpo.append("       inner join ade.verbaConvenio vco");
        corpo.append("       inner join vco.convenio cnv ");
        corpo.append("       inner join cnv.consignataria csa ");
        corpo.append("       where ade.adeData between add_month(:periodoIni , -6) and :periodoFim");
        corpo.append(" and csa.csaCodigo = :csaCodigo");
        corpo.append(" group by tipo");
        corpo.append(" ");

        final Query<Object[]> query = instanciarQuery(session, corpo.toString());
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        defineValorClausulaNomeada("nseRenegecociacao", nseRenegecociacao, query);
        defineValorClausulaNomeada("nseCompra", nseCompra, query);

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
            "TIPO",
            "PERCENTUAL"
        };
    }
}
