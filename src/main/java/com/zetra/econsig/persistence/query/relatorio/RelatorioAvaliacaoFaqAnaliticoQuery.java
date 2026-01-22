package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;

/**
 * <p>Title: RelatorioAvaliacaoFaqAnaliticoQuery</p>
 * <p>Description: Query para relatório de avaliação de FAQ Analitico.</p>
 * <p>Copyright: Copyright (c) 2021</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioAvaliacaoFaqAnaliticoQuery extends ReportHNativeQuery {

    public AcessoSistema responsavel;
    private String periodoIni;
    private String periodoFim;
    private String faqCodigo;
    private String avaliacaoFaq;
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
            avaliacaoFaq = (String) criterio.getAttribute("avaliacaoFaq");
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

        corpoBuilder.append(" SELECT ava.AVF_CODIGO as AVF_CODIGO, faq.FAQ_CODIGO as FAQ_CODIGO, faq.FAQ_TITULO_1 as FAQ_TITULO_1,");
        corpoBuilder.append(" case");
        corpoBuilder.append(" when avf_nota = '1' then '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.avaliacao.util", responsavel)).append("' else '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.avaliacao.inutil", responsavel)).append("' end as AVALIACAO, ");
        corpoBuilder.append(" ava.AVF_DATA as AVF_DATA , usu.usu_nome as USU_NOME, usu.usu_login as USU_LOGIN,");
        corpoBuilder.append(" case");
        corpoBuilder.append(" when usuarioCse.usu_codigo is not null then '").append(ApplicationResourcesHelper.getMessage("rotulo.consignante.singular", responsavel)).append("'");
        corpoBuilder.append(" when usuarioCsa.usu_codigo is not null then '").append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel)).append("'");
        corpoBuilder.append(" when usuarioOrg.usu_codigo is not null then '").append(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel)).append("'");
        corpoBuilder.append(" when usuarioCor.usu_codigo is not null then '").append(ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular", responsavel)).append("'");
        corpoBuilder.append(" when usuarioSer.usu_codigo is not null then '").append(ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel)).append("'");
        corpoBuilder.append(" end as ENTIDADE,");
        corpoBuilder.append(" text_to_string(ava.AVF_COMENTARIO) as AVF_COMENTARIO");
        corpoBuilder.append(" FROM tb_avaliacao_faq ava");
        corpoBuilder.append(" INNER JOIN tb_faq faq on (faq.faq_codigo = ava.faq_codigo)");
        corpoBuilder.append(" INNER JOIN tb_usuario usu on (usu.usu_codigo = ava.usu_codigo)");
        corpoBuilder.append(" LEFT JOIN tb_usuario_cse usuarioCse on (usu.usu_codigo = usuarioCse.usu_codigo)");
        corpoBuilder.append(" LEFT JOIN tb_usuario_org usuarioOrg on (usu.usu_codigo = usuarioOrg.usu_codigo)");
        corpoBuilder.append(" LEFT JOIN tb_usuario_csa usuarioCsa on (usu.usu_codigo = usuarioCsa.usu_codigo)");
        corpoBuilder.append(" LEFT JOIN tb_usuario_cor usuarioCor on (usu.usu_codigo = usuarioCor.usu_codigo)");
        corpoBuilder.append(" LEFT JOIN tb_usuario_ser usuarioSer on (usu.usu_codigo = usuarioSer.usu_codigo)");

        corpoBuilder.append(" WHERE 1=1");

        if(!TextHelper.isNull(faqCodigo)) {
            corpoBuilder.append(" AND faq.faq_codigo = :faqCodigo ");
        }

        if(!TextHelper.isNull(avaliacaoFaq)) {
            corpoBuilder.append(" AND ava.AVF_NOTA = :avaliacaoFaq ");
        } else {
            corpoBuilder.append(" AND ava.AVF_NOTA = '0' ");
        }

        if (!cse || !org || !csa || !cor || !ser) {
            corpoBuilder.append(" and ( 1 = 2 ");
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

        if(!TextHelper.isNull(periodoIni) && !TextHelper.isNull(periodoFim)) {
            corpoBuilder.append(" AND ava.AVF_DATA BETWEEN :periodoIni AND :periodoFim ");
        }

        corpoBuilder.append(" ORDER BY ava.AVF_DATA DESC");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if(!TextHelper.isNull(faqCodigo)) {
            defineValorClausulaNomeada("faqCodigo", faqCodigo, query);
        }

        if(!TextHelper.isNull(avaliacaoFaq)) {
            defineValorClausulaNomeada("avaliacaoFaq", avaliacaoFaq, query);
        }

        if(!TextHelper.isNull(periodoIni) && !TextHelper.isNull(periodoFim)) {
            defineValorClausulaNomeada("periodoIni", parseDateString(periodoIni), query);
            defineValorClausulaNomeada("periodoFim", parseDateString(periodoFim), query);
        } else {
            query.setMaxResults(50);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "avfCodigo",
                "faqCodigo",
                "faqTitulo1",
                "avaliacao",
                "avfData",
                "usuNome",
                "usuLogin",
                "entidade",
                "avfComentario"
        };
    }
}
