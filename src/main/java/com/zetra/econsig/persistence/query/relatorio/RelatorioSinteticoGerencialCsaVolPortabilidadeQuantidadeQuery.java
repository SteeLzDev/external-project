package com.zetra.econsig.persistence.query.relatorio;

import java.text.ParseException;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
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
public class RelatorioSinteticoGerencialCsaVolPortabilidadeQuantidadeQuery extends ReportHNativeQuery {

    public String csaCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final String nseCompra = CodedValues.TNT_CONTROLE_COMPRA;

        final StringBuilder corpo = new StringBuilder();
        corpo.append(" select COALESCE(sum(quantidade),0) FROM (");
        corpo.append(" select count(*) as quantidade");
        corpo.append(" from tb_aut_desconto ade");
        corpo.append(" inner join tb_verba_convenio vco USING (vco_codigo)");
        corpo.append(" INNER join tb_convenio cnv USING (cnv_codigo)");
        corpo.append(" INNER join tb_relacionamento_autorizacao rad ON (rad.tnt_codigo = :nseCompra AND rad.ade_codigo_destino = ade.ade_codigo)");
        corpo.append(" INNER JOIN tb_consignataria csaVendedora on (rad.csa_codigo_origem = csaVendedora.csa_codigo)");
        corpo.append(" where rad.rad_data BETWEEN add_month(:periodoIni, -6) AND :periodoFim");
        corpo.append("   AND cnv.csa_codigo = :csaCodigo ");
        corpo.append(" group by csaVendedora.csa_codigo");
        corpo.append(" UNION ALL");
        corpo.append(" select count(*) as quantidade");
        corpo.append(" from tb_aut_desconto ade");
        corpo.append(" inner join tb_verba_convenio vco USING (vco_codigo)");
        corpo.append(" INNER join tb_convenio cnv USING (cnv_codigo)");
        corpo.append(" INNER join tb_relacionamento_autorizacao rad ON (rad.tnt_codigo = :nseCompra AND rad.ade_codigo_origem = ade.ade_codigo)");
        corpo.append(" INNER JOIN tb_consignataria csaCompradora on (rad.csa_codigo_destino = csaCompradora.csa_codigo)");
        corpo.append(" where rad.rad_data BETWEEN add_month(:periodoIni, -6) AND :periodoFim");
        corpo.append("   AND cnv.csa_codigo = :csaCodigo ");
        corpo.append(" group by csaCompradora.csa_codigo");
        corpo.append(" ) AS x");

        final Query<Object[]> query = instanciarQuery(session, corpo.toString());
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
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
            "QUANTIDADE"
        };
    }
}
