package com.zetra.econsig.web.tag.v4;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.FieldKeysConstants;

/**
 * <p>Title: DetalheConsignacaoTag</p>
 * <p>Description: Tag para exibição dos detalhes de uma consignação no leiaute v4.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class DetalheConsignacaoTag extends com.zetra.econsig.web.tag.DetalheConsignacaoTag {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DetalheConsignacaoTag.class);

    public String divSizeCSS = "col-sm";

    private final StringBuilder dadosConsignacao = new StringBuilder();

    private final StringBuilder dadosConsignacaoOrigem = new StringBuilder();

    private final StringBuilder dadosServidor = new StringBuilder();

    private final StringBuilder dadosConsignante = new StringBuilder();

    private final StringBuilder dadosConsignataria = new StringBuilder();

    public String getDivSizeCSS() {
        return divSizeCSS;
    }

    public void setDivSizeCSS(String inputSizeCSS) {
        divSizeCSS = inputSizeCSS;
    }

    @Override
    public int doEndTag() throws JspException {
        try {
            // Obtém o DTO com os dados da consignação
            Object autdesObject = pageContext.getAttribute(name, getScopeAsInt(scope));
            geraDetalheConsignacao(autdesObject);

            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

            boolean simularRenegociacao = !TextHelper.isNull(type) && type.equals("simular_renegociacao");

            StringBuilder html = new StringBuilder();

            html.append("\n").append("<div class=\"" + divSizeCSS + "\">");
            html.append("\n").append("    <div class=\"card\">");
            html.append("\n").append("        <div class=\"card-header\"><h2 class=\"card-header-title\">").append(ApplicationResourcesHelper.getMessage("rotulo.consultar.consignacao.dados.consignacao", responsavel)).append("</h2></div>");
            html.append("\n").append("        <div class=\"card-body\">");
            if (!multiplosServidores) {
                html.append("\n").append("            <dl class=\"row data-list firefox-print-fix \">");
            } else {
                html.append("\n").append("            <dl class=\"row data-list firefox-print-fix col-6 \">");
            }
            html.append("\n").append(dadosConsignacao.toString());
            if (type.equals("desliquidar")) {
                html.append("\n").append(dadosConsignataria.toString());
            }
            html.append("\n").append("            </dl>");
            html.append("\n").append("        </div>");
            html.append("\n").append("    </div>");
            html.append("\n").append("</div>");
            if (!multiplosServidores) {
                html.append("\n").append("<div class=\"" + divSizeCSS + "\">");
                if (!type.equals("desliquidar") && !dadosConsignataria.toString().isEmpty()) {
                    html.append("\n").append("    <div class=\"card\">");
                    html.append("\n").append("        <div class=\"card-header\"><h2 class=\"card-header-title\">").append(ApplicationResourcesHelper.getMessage("rotulo.consultar.consignacao.dados.convenio", responsavel)).append("</h2></div>");
                    html.append("\n").append("        <div class=\"card-body\">");
                    html.append("\n").append("            <dl class=\"row data-list firefox-print-fix\">");
                    html.append("\n").append(dadosConsignataria.toString());
                    html.append("\n").append("            </dl>");
                    html.append("\n").append("        </div>");
                    html.append("\n").append("    </div>");
                }
                if(!dadosConsignacaoOrigem.toString().isEmpty()){
                    html.append("\n").append("    <div class=\"card\">");
                    html.append("\n").append("        <div class=\"card-header\"><h2 class=\"card-header-title\">").append(ApplicationResourcesHelper.getMessage("rotulo.consultar.consignacao.dados.convenio.origem", responsavel)).append("</h2></div>");
                    html.append("\n").append("        <div class=\"card-body\">");
                    html.append("\n").append("            <dl class=\"row data-list firefox-print-fix\">");
                    html.append("\n").append(dadosConsignacaoOrigem.toString());
                    html.append("\n").append("            </dl>");
                    html.append("\n").append("        </div>");
                    html.append("\n").append("    </div>");
                }
                if (!dadosConsignante.toString().isEmpty()) {
                    html.append("\n").append("    <div class=\"card\">");
                    html.append("\n").append("        <div class=\"card-header\"><h2 class=\"card-header-title\">").append(ApplicationResourcesHelper.getMessage("rotulo.consultar.consignacao.dados.consignante", responsavel)).append("</h2></div>");
                    html.append("\n").append("        <div class=\"card-body\">");
                    html.append("\n").append("            <dl class=\"row data-list firefox-print-fix\">");
                    html.append("\n").append(dadosConsignante.toString());
                    html.append("\n").append("            </dl>");
                    html.append("\n").append("        </div>");
                    html.append("\n").append("    </div>");
                }
                if (!simularRenegociacao && !dadosServidor.toString().isEmpty()) {
                    html.append("\n").append("    <div class=\"card\">");
                    html.append("\n").append("        <div class=\"card-header\"><h2 class=\"card-header-title\">").append(ApplicationResourcesHelper.getMessage("rotulo.consultar.consignacao.dados.servidor", responsavel)).append("</h2></div>");
                    html.append("\n").append("        <div class=\"card-body\">");
                    html.append("\n").append("            <dl class=\"row data-list firefox-print-fix \">");
                    html.append("\n").append(dadosServidor.toString());
                    html.append("\n").append("            </dl>");
                    html.append("\n").append("        </div>");
                    html.append("\n").append("    </div>");
                }
                html.append("\n").append("</div>");
            }

            pageContext.getOut().print(html.toString());

            return EVAL_PAGE;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new JspException(ex.getMessage(), ex);
        } finally {
            clean();
        }
    }

    protected void clean() {
        dadosConsignacao.setLength(0);
        dadosServidor.setLength(0);
        dadosConsignante.setLength(0);
        dadosConsignataria.setLength(0);
        dadosConsignacaoOrigem.setLength(0);
    }

    @Override
    protected String montarLinha(String descricao, Object valor, String descricaoCss, String valorCss, String fieldKey) {
        if (TextHelper.isNull(descricao)) {
            return "";
        }
        try {
            if (TextHelper.isNull(fieldKey)) {
                return gerarLinhaDetalheAde(descricao, valor, true);
            } else {
                AcessoSistema responsavel = JspHelper.getAcessoSistema((HttpServletRequest) pageContext.getRequest());
                if (ShowFieldHelper.showField(fieldKey, responsavel)) {

                    // Não valida XSS no campo Matrícula pois é adicionado um link para acesso à consulta de servidor
                    boolean validarXssCampoValor = !fieldKey.equals(FieldKeysConstants.DETALHE_SERVIDOR_SERVIDOR_MATRICULA);

                    StringBuilder linha = new StringBuilder();

                    if (fieldKey.equals(FieldKeysConstants.DETALHE_CONSIGNACAO_NUMERO)) {
                        Object autdesObjct = pageContext.getAttribute(name, getScopeAsInt(scope));
                        if ((autdesObjct instanceof List<?>) && ((List<?>) autdesObjct).size() > 1) {
                            if (dadosConsignacao.length() > 0) {
                                linha.append("</dl>");
                            }
                            linha.append("<div class=\"legend\">");
                            linha.append("<span>").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.numero.arg0", responsavel, ((Long) valor).toString())).append("</span>");
                            linha.append("</div>");
                            if (!multiplosServidores) {
                                linha.append("<dl class=\"row data-list firefox-print-fix \">");
                            } else {
                                linha.append("<dl class=\"row data-list firefox-print-fix col-6\">");
                            }
                        }
                    }

                    linha.append(gerarLinhaDetalheAde(descricao, valor, validarXssCampoValor));

                    if ((fieldKey.equals(FieldKeysConstants.DETALHE_CONSIGNACAO_CONSIGNANTE) || fieldKey.equals(FieldKeysConstants.DETALHE_SERVIDOR_ESTABELECIMENTO) || fieldKey.equals(FieldKeysConstants.DETALHE_SERVIDOR_ORGAO) || fieldKey.equals(FieldKeysConstants.DETALHE_SERVIDOR_SUB_ORGAO) || fieldKey.equals(FieldKeysConstants.DETALHE_SERVIDOR_UNIDADE)) && !multiplosServidores) {
                        dadosConsignante.append(linha);

                    } else if ((fieldKey.equals(FieldKeysConstants.DETALHE_CONSIGNACAO_CONSIGNATARIA) || fieldKey.equals(FieldKeysConstants.DETALHE_CONSIGNACAO_CORRESPONDENTE) || fieldKey.equals(FieldKeysConstants.DETALHE_CONSIGNACAO_SERVICO)) && !multiplosServidores) {
                        dadosConsignataria.append(linha);

                    } else if (fieldKey.startsWith(FieldKeysConstants.DETALHE_SERVIDOR) && !multiplosServidores) {
                        dadosServidor.append(linha);

                    } else if (fieldKey.equals(FieldKeysConstants.DETALHE_CONSIGNACAO_MSG_PARCELA_PROCESSAMENTO) && !multiplosServidores) {
                        dadosConsignacao.append("\n                <div class=\"alert alert-warning col-sm-12\" role=\"alert\">" + descricao + "</div>");
                    } else if (fieldKey.equals(FieldKeysConstants.DETALHE_CONSIGNACAO_ORIGEM) && !multiplosServidores){
                        dadosConsignacaoOrigem.append(linha);
                    } else {
                        dadosConsignacao.append(linha);
                    }
                }
            }
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return "";
    }

    private String gerarLinhaDetalheAde(String descricao, Object valor, boolean validarXssCampoValor) {
        /*
         * <dt class="col-6">Data da reserva:</dt>
         * <dd class="col-6">22/04/2014 14:55:39</dd>
         */
        if (TextHelper.isNull(valor)) {
            valor = ApplicationResourcesHelper.getMessage("rotulo.campo.nao.definido.abreviado", AcessoSistema.getAcessoUsuarioSistema());
        }

        String addClassCss = descricao.equals(ApplicationResourcesHelper.getMessage("rotulo.detalhe.consignacao.data.cancelamento.automatico", AcessoSistema.getAcessoUsuarioSistema())) ? "classe" : "";

        if (validarXssCampoValor) {
            return "\n                <dt class=\"col-6 " + addClassCss + "\">" + TextHelper.forHtmlContentComTags(descricao) + "</dt><dd class=\"col-6\">" + TextHelper.forHtmlContentComTags(valor) + "</dd>";
        } else {
            return "\n                <dt class=\"col-6 " + addClassCss + "\" >" + TextHelper.forHtmlContentComTags(descricao) + "</dt><dd class=\"col-6\">" + valor + "</dd>";
        }
    }

    @Override
    protected String montarLinkConsultarServidor(String textoMatriculaNome, String link, AcessoSistema responsavel) {
        String msgAlt = ApplicationResourcesHelper.getMessage("mensagem.consultar.servidor.clique.aqui", responsavel);
        return "<a href=\"#no-back\" onClick=\"postData('" + link + "')\" id=\"btnEdtServidor\" aria-label=\"" + msgAlt + "\"><span class=\"icon-menu\">" + textoMatriculaNome + "</a>";
    }

    @Override
    protected String montarLinhaTooltip(String descricao, Object valor, String fieldKey, String tooltip) {
        if (fieldKey.equals(FieldKeysConstants.DETALHE_SERVIDOR_PERFIL_PRELIMINAR)) {
            dadosServidor.append(super.montarLinhaTooltipv4(descricao, valor, null, null, fieldKey, tooltip));
        }
        return "";
    }

    @Override
    public void release() {
        super.release();
        multiplosServidores = false;
    }
}
