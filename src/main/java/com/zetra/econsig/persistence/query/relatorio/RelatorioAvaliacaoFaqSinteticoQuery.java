package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;

/**
 * <p>Title: RelatorioAvaliacaoFaqSinteticoQuery</p>
 * <p>Description: Query para relatório de avaliação de FAQ, dados sinteticos.</p>
 * <p>Copyright: Copyright (c) 2021</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioAvaliacaoFaqSinteticoQuery extends ReportHNativeQuery {

    public AcessoSistema responsavel;
    private String periodoIni;
    private String periodoFim;
    private String faqCodigo;
    public boolean cse;
    public boolean org;
    public boolean csa;
    public boolean cor;
    public boolean ser;

    @Override
    public void setCriterios(TransferObject criterio) {
        if (criterio != null) {
            responsavel = (AcessoSistema) criterio.getAttribute("responsavel");
            periodoIni = (String) criterio.getAttribute("periodoIni");
            periodoFim = (String) criterio.getAttribute("periodoFim");
            faqCodigo = (String) criterio.getAttribute("faqCodigo");
            cse = (boolean) criterio.getAttribute("cse");
            org = (boolean) criterio.getAttribute("org");
            csa = (boolean) criterio.getAttribute("csa");
            cor = (boolean) criterio.getAttribute("cor");
            ser = (boolean) criterio.getAttribute("ser");
        }
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append(" SELECT faq.FAQ_CODIGO, faq.FAQ_TITULO_1, max(taf.AVF_DATA) as DATA_AVALIACAO,");
        corpoBuilder.append(" sum(case when avf_nota = '1' then 1 else 0 end) as UTIL,");
        corpoBuilder.append(" sum(case when avf_nota = '0' then 1 else 0 end) as INUTIL");
        corpoBuilder.append(" FROM tb_avaliacao_faq taf");
        corpoBuilder.append(" INNER JOIN tb_faq faq ON (taf.faq_codigo = faq.faq_codigo)");
        corpoBuilder.append(" INNER JOIN tb_usuario usu on (usu.usu_codigo = taf.usu_codigo)");
        corpoBuilder.append(" LEFT JOIN tb_usuario_cse usuarioCse on (usu.usu_codigo = usuarioCse.usu_codigo)");
        corpoBuilder.append(" LEFT JOIN tb_usuario_org usuarioOrg on (usu.usu_codigo = usuarioOrg.usu_codigo)");
        corpoBuilder.append(" LEFT JOIN tb_usuario_csa usuarioCsa on (usu.usu_codigo = usuarioCsa.usu_codigo)");
        corpoBuilder.append(" LEFT JOIN tb_usuario_cor usuarioCor on (usu.usu_codigo = usuarioCor.usu_codigo)");
        corpoBuilder.append(" LEFT JOIN tb_usuario_ser usuarioSer on (usu.usu_codigo = usuarioSer.usu_codigo)");

        corpoBuilder.append(" WHERE 1=1");
        if(!TextHelper.isNull(periodoIni) && !TextHelper.isNull(periodoFim)) {
            corpoBuilder.append(" AND taf.AVF_DATA BETWEEN :periodoIni AND :periodoFim ");
        }
        if(!TextHelper.isNull(faqCodigo)) {
            corpoBuilder.append(" AND faq.faq_codigo = :faqCodigo ");
        }

        if (!cse || !org || !csa || !cor || !ser) {
            corpoBuilder.append(" AND ( 1 = 2 ");
            if (csa) {
                corpoBuilder.append(" or usuarioCsa.csa_codigo is not null");
            }
            if (cor) {
                corpoBuilder.append(" or usuarioCor.cor_codigo is not null");
            }
            if (ser) {
                corpoBuilder.append(" or usuarioSer.ser_codigo is not null");
            }
            if (cse) {
                corpoBuilder.append(" or usuarioCse.cse_codigo is not null");
            }
            if (org) {
                corpoBuilder.append(" or usuarioOrg.org_codigo is not null");
            }
            corpoBuilder.append(" )");
        }
        corpoBuilder.append(" GROUP BY faq.FAQ_CODIGO, faq.FAQ_TITULO_1");
        corpoBuilder.append(" ORDER BY DATA_AVALIACAO DESC");


        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if(!TextHelper.isNull(faqCodigo)) {
            defineValorClausulaNomeada("faqCodigo", faqCodigo, query);
        }

        if(!TextHelper.isNull(periodoIni) && !TextHelper.isNull(periodoFim)) {
            defineValorClausulaNomeada("periodoIni", parseDateString(periodoIni), query);
            defineValorClausulaNomeada("periodoFim", parseDateString(periodoFim), query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "faqCodigo",
                "faqTitulo1",
                "ultimaDataAvaliacao",
                "util",
                "inutil"
        };
    }
}
