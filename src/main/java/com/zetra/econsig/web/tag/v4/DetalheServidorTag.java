package com.zetra.econsig.web.tag.v4;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: DetalheServidorTag</p>
 * <p>Description: Tag para exibição de dados do servidor layout v4.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class DetalheServidorTag extends com.zetra.econsig.web.tag.DetalheServidorTag {

    @Override
    protected String montarLinha(String descricao, Object valor, String fieldKey) {
        return montarLinhav4(descricao, valor, fieldKey);
    }

    @Override
    protected String montarLinhaTooltip(String descricao, Object valor, String fieldKey, String tooltip) {
        return super.montarLinhaTooltipv4(descricao, valor, null, null, fieldKey, tooltip);
    }

    @Override
    protected String montarLinkConsultarServidor(String textoMatriculaNome, String link, AcessoSistema responsavel) {
        String msgAlt = ApplicationResourcesHelper.getMessage("mensagem.consultar.servidor.clique.aqui", responsavel);
        return montarLinkPadrao(link, "btnEdtServidor", textoMatriculaNome, msgAlt, responsavel);
    }

    @Override
    protected String montarLinkConsultarBloqueiosServidor(String link, AcessoSistema responsavel) {
        String msgAlt = ApplicationResourcesHelper.getMessage("mensagem.convenios.bloq.clique.aqui", responsavel);
        return montarLinkPadrao(link, "btnBloqueioVerbaServidor", msgAlt, msgAlt, responsavel);
    }

    private String montarLinkPadrao(String link, String idLink, String msgLink, String msgAlt, AcessoSistema responsavel) {
        return "<a href=\"#no-back\" onClick=\"postData('" + link + "')\" id=\"" + idLink + "\" aria-label=\"" + msgAlt + "\"><span class=\"icon-menu\">" + msgLink + "</a>";
    }

}
