package com.zetra.econsig.web.tag.v4;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.dto.entidade.ItemMenuTO;
import com.zetra.econsig.dto.entidade.MenuTO;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.ItemMenuEnum;
import com.zetra.econsig.web.tag.ZetraTagSupport;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

/**
 * <p>Title: DashBoardTag</p>
 * <p>Description: Tag para criação de dashboard do menu favoritos.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class DashBoardTag extends ZetraTagSupport {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DashBoardTag.class);

    @Autowired
    private ConsignanteController consignanteController;

    private boolean acessoInicial;

    public void setAcessoInicial(boolean acessoInicial) {
        this.acessoInicial = acessoInicial;
    }

    @Override
    public int doEndTag() throws JspException {
        try {
            pageContext.getOut().print(generateHtml());
        } catch (final IOException ex) {
            throw new JspException(ex.getMessage());
        }
        // Continue processing this page
        return (EVAL_PAGE);
    }

    private String generateHtml() {
        final HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final String browser = JspHelper.getUserBrowser(request);

        // Se não tem usuário autenticado, retorna vazio
        if (TextHelper.isNull(responsavel.getUsuCodigo()) || browser.contains("IE-7") || browser.contains("IE-8") || browser.contains("IE-9")) {
            return "";
        }

        String euConsigoMais = null;

        if((responsavel.isCse() || responsavel.isOrg()) && !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_URL_EUCONSIGOMAIS, responsavel))) {
            euConsigoMais = ParamSist.getInstance().getParam(CodedValues.TPC_URL_EUCONSIGOMAIS, responsavel).toString();
        }

        // Parâmetro de sistema para exibir url euconsigomais na tela inicial do módulo do servidor
        boolean exibeBotaoPortal = false;
        String idEmpresa = "";
        final String urlPortal = (String) ParamSist.getInstance().getParam(CodedValues.TPC_EXIBE_URL_EUCONSIGOMAIS_SERVIDOR, responsavel);
        if (responsavel.isSer() && !TextHelper.isNull(urlPortal)) {
            // Busca o cse_identificador que é o cnpj do consignante no euConsigoMais.
            try {
                final String cseIdInterno = consignanteController.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel).getCseIdentificador();
                idEmpresa = cseIdInterno.replace("t", "");
                exibeBotaoPortal = true;
            } catch (final ConsignanteControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }


        final StringBuilder html = new StringBuilder();
        final List<MenuTO> menuFavoritos = responsavel.getMenuFavoritos();

        if ((menuFavoritos != null) && !menuFavoritos.isEmpty()) {
            html.append("<script src=\"../node_modules/jquery/dist/jquery.min.js?").append(ApplicationResourcesHelper.getMessage("release.tag", responsavel)).append("\"></script>\n");
            html.append("\n<script src=\"../js/jquery.gridstrap.min.js?").append(ApplicationResourcesHelper.getMessage("release.tag", responsavel)).append("\"></script>");
        }
        html.append("<script src=\"../node_modules/js-cookie/dist/js.cookie.min.js?").append(ApplicationResourcesHelper.getMessage("release.tag", responsavel)).append("\"></script>\n");
        html.append("\n<script src=\"../js/jquery.gridstrap.min.js?").append(ApplicationResourcesHelper.getMessage("release.tag", responsavel)).append("\"></script>");
        html.append("<div id=\"containerFavoritos\" class=\"row shortcut-btns\">");
        if ((menuFavoritos != null) && !menuFavoritos.isEmpty()) {
            final MenuTO menu = menuFavoritos.stream().findFirst().get();
            final List<ItemMenuTO> itensMenu = menu.getItens();

            for (final ItemMenuTO itemMenu : itensMenu) {
                String rotulo = itemMenu.getItmTexChave();
                final String acrRecurso = itemMenu.getAcrRecurso();
                final String icone = !TextHelper.isNull(itemMenu.getItmImagem()) ? itemMenu.getItmImagem() : itemMenu.getMnuImagem();
                final String itmDescricao = TextHelper.forHtmlContent(itemMenu.getItmDescricao());
                String itmDescricaoNegrito = "";

                if (TextHelper.isNull(itemMenu.getFunCodigo()) || responsavel.temPermissao(itemMenu.getFunCodigo())) {
                    final int ultimoEspaco = itmDescricao.lastIndexOf(" ");
                    if (ultimoEspaco > -1) {
                        itmDescricaoNegrito = itmDescricao.substring(0, ultimoEspaco) + " <strong>" + itmDescricao.substring(ultimoEspaco + 1, itmDescricao.length()) + "</strong>";
                    } else {
                        itmDescricaoNegrito = "<strong>" + itmDescricao.substring(ultimoEspaco+1, itmDescricao.length()) + "</strong>";
                    }

                    html.append("<div class=\"col-md-6 col-lg-4 col-xl-3 fav-space\">");
                    if (!TextHelper.isNull(rotulo)) {
                        if (itemMenu.getItmCodigo().equals(String.valueOf(ItemMenuEnum.INTEGRAR_FOLHA.getCodigo())) && acessoInicial) {
                            rotulo = "mensagem.tooltip.dashboard.428.comeceAqui";
                        }
                        html.append("<span data-bs-toggle=\"tooltip\" title=\"\" data-original-title=\"").append(ApplicationResourcesHelper.getMessage(rotulo, responsavel)).append("\" aria-label=\"").append(ApplicationResourcesHelper.getMessage(rotulo, responsavel)).append("\">");
                    }
                    html.append("<a class=\"btn\" href=\"#no-back\" onmousedown=\"getInicialPositionCardGrid()\" onmouseup=\"if (getAtualPositionCardGrid()){").append(acrRecurso).append("}\">");
                    html.append("<svg width=\"51\"><use xlink:href=\"#").append(icone).append("\"></use></svg>");

                    if (itemMenu.getItmCodigo().equals(String.valueOf(ItemMenuEnum.INTEGRAR_FOLHA.getCodigo())) && acessoInicial) {
                        html.append(ApplicationResourcesHelper.getMessage("rotulo.dashboard.integrar.folha.comece.aqui", responsavel));
                    } else {
                        html.append(itmDescricaoNegrito);
                    }

                    html.append("</a>");
                    html.append("</span>");
                    html.append("<input type=\"hidden\" name=\"itmCodigo\" value=\"").append(itemMenu.getItmCodigo()).append("\">");
                    html.append("</div>");
                }
            }
        }

        // Acesso ao euConsigoMais teve que ser mantido fixo porque valida parâmetro de sistema
        if (!TextHelper.isNull(euConsigoMais)) {
            html.append("<div class=\"col-md-6 col-lg-4 col-xl-3\">");
            html.append("<span  data-bs-toggle=\"tooltip\" title=\"\" data-original-title=\"").append(ApplicationResourcesHelper.getMessage("mensagem.tooltip.dashboard.612", responsavel)).append("\" aria-label=\"").append(ApplicationResourcesHelper.getMessage("mensagem.tooltip.dashboard.612", responsavel)).append("\">");
            html.append("<a class=\"btn\" href=\"#no-back\" onClick=\"postData('../v3/autenticarEuConsigoMais?acao=gerarToken&").append(TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))).append("', 'new')\">");
            html.append("<svg width=\"40\"><use xlink:href=\"#i-simular\"></use></svg>");
            html.append(ApplicationResourcesHelper.getMessage("rotulo.dashboard.visualizar.link.euconsigomais.consignante", responsavel));
            html.append("</a>");
            html.append("</span>");
            html.append("</div>");
        }

        if (exibeBotaoPortal) {
            html.append("<div class=\"col-md-6 col-lg-4 col-xl-3\">");
            html.append("<a class=\"btn\" href=\"#no-back\" onClick=\"postData('").append(urlPortal).append("?idempresa=").append(idEmpresa).append("', 'new')\">");
            html.append("<svg width=\"40\"><use xlink:href=\"#i-simular\"></use></svg>");
            html.append(ApplicationResourcesHelper.getMessage("rotulo.dashboard.visualizar.link.euconsigomais.servidor", responsavel));
            html.append("</a>");
            html.append("</div>");
        }

        html.append("</div>\n");

        //Gera o efeito grid(transição) nos cards da tela principal e a lista de "itmCodigo" para realizar
        //a atualização da sequencia no banco de dados.
        html.append("<input type=\"hidden\" id=\"itmsCodigo\" name=\"itmsCodigo\" value=\"\">\n");
        html.append("\n<script type=\"text/JavaScript\">");
        html.append("\n$(function(){");
        html.append("\n   $('#containerFavoritos').gridstrap({");
        html.append("\n   /* default options */");
        html.append("\n   });");
        html.append("\n});");
        html.append("\nvar startX;");
        html.append("\nvar startY;");
        html.append("\nfunction getInicialPositionCardGrid(){");
        html.append("\n   startX = event.pageX;");
        html.append("\n   startY = event.pageY;");
        html.append("\n}");
        html.append("\nfunction getAtualPositionCardGrid(){");
        html.append("\nactualX = event.pageX;");
        html.append("\nactualY = event.pagey;");
        html.append("\n   if (Math.abs(startX - actualX) < 5 || Math.abs(startY - actualY) < 5) {");
        html.append("\n      return true;");
        html.append("\n   }");
        html.append("\nvar elementoPai = document.getElementById('containerFavoritos');");
        html.append("\nvar count = 0;");
        html.append("\nvar itmsCodigo = []");
        html.append("\nfor (var i = 1; i < elementoPai.childElementCount; i++) {");
        html.append("\n    itmsCodigo[count] = elementoPai.children[count].children[1].value;");
        html.append("\n    count++");
        html.append("\n}");
        html.append("\ndocument.getElementById('itmsCodigo').value = itmsCodigo;");
        html.append("$.ajax({\n");
        html.append("type : 'post',\n");
        html.append("url : \"../v3/favoritarMenu?acao=recarregarCardsDashboard&_skip_history_=true&itmsCodigo=\" + itmsCodigo\n");
        html.append("});\n");
        html.append("\nreturn false;");
        html.append("\n}");
        html.append("\n</script>");

        return html.toString();
    }

}