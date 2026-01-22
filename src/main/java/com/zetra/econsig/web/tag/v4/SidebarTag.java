package com.zetra.econsig.web.tag.v4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.zetra.econsig.dto.entidade.ItemMenuTO;
import com.zetra.econsig.dto.entidade.MenuTO;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.ItemMenuEnum;
import com.zetra.econsig.web.tag.ZetraTagSupport;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

/**
 * <p>Title: SidebarTagv4</p>
 * <p>Description: Tag para criação da barra lateral de Menu layout versão 4.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class SidebarTag extends ZetraTagSupport {

    @Override
    public int doEndTag() throws JspException {
        try {
            pageContext.getOut().print(generateHtml());
        } catch (IOException ex) {
            throw new JspException(ex.getMessage());
        }
        // Continue processing this page
        return (EVAL_PAGE);
    }


    private static boolean isMenuThin(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("menu") && cookie.getValue().equals("true")) {
                    return true;
                }
            }
        }
        return false;
    }

    private String generateHtml() {
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String browser = JspHelper.getUserBrowser(request);

        // Se não tem usuário autenticado, retorna vazio
        if (TextHelper.isNull(responsavel.getUsuCodigo()) || browser.contains("IE-7") || browser.contains("IE-8") || browser.contains("IE-9")) {
            return "";
        }

        List<MenuTO> menuList = responsavel.getMenu();

        StringBuilder html = new StringBuilder();
        String textoMenuPrincipal = ApplicationResourcesHelper.getMessage("rotulo.menu.pagina.inicial", responsavel);
        String versaoLeiaute = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel)) ? "v4" : (String) ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel);

        if (menuList != null && !menuList.isEmpty()) {
            boolean menuThin = isMenuThin(request);

            if (menuThin) {

                html.append("<style>\n");
                html.append(".nav-bar {\n");
                html.append("width: 60px;\n");
                html.append("}\n");
                html.append(".header-logo {\n");
                html.append("width: 60px;\n");
                html.append("}\n");
                html.append(".zetra {\n");
                if (!"v4".equals(versaoLeiaute)) {
                    html.append("width: 26px;\n");
                } else {
                    html.append("width: 22px;\n");
                }
                html.append("}\n");
                html.append(".partner-logo {\n");
                html.append("display: none;\n");
                html.append("}\n");
                html.append(".main-menu li > a {\n");
                html.append("width: 230px;\n");
                html.append("margin-left: 0;\n");
                html.append("border-radius: 0;\n");
                html.append("overflow: hidden;\n");
                html.append("}\n");
                html.append(".submenu {\n");
                html.append("margin-left: 0;\n");
                html.append("}\n");
                html.append(".submenu li > a {\n");
                html.append("visibility: hidden;\n");
                html.append("}\n");
                html.append(".main {\n");
                html.append("margin-left: 60px;\n");
                html.append("}\n");
                html.append(".nav-bar:hover {\n");
                html.append("width: 230px;\n");
                html.append("}\n");
                html.append(".nav-bar:hover .submenu li > a {\n");
                html.append("visibility: visible;\n");
                html.append("}\n");
                html.append(".nav-bar:hover .partner-logo {\n");
                html.append("display: block;\n");
                html.append("}\n");
                html.append("</style>");
            } else {
                html.append("<style>\n");
                html.append(".nav-bar {\n");
                html.append("width: 230px;\n");
                html.append("}\n");
                html.append(".header-logo {\n");
                html.append("width: 230px;\n");
                html.append("}\n");
                html.append(".zetra {\n");
                html.append("width: 140px;\n");
                html.append("}\n");
                html.append(".partner-logo {\n");
                html.append("display: block;\n");
                html.append("}\n");
                html.append(".main-menu li > a {\n");
                html.append("width: 230px;\n");
                html.append("margin-left: 0;\n");
                html.append("border-radius: .5rem 0 0 .5rem;\n");
                html.append("overflow-wrap: break-word\n");
                html.append("}\n");
                html.append(".submenu {\n");
                html.append("margin-left: -10px;\n");
                html.append("}\n");
                html.append(".submenu li > a {\n");
                html.append("visibility: visible;\n");
                html.append("}\n");
                html.append(".main {\n");
                html.append("margin-left: 230px;\n");
                html.append("}\n");
                html.append(".nav-bar:hover {\n");
                html.append("width: 230px;\n");
                html.append("}\n");
                html.append(".nav-bar:hover .submenu li > a {\n");
                html.append("visibility: visible;\n");
                html.append("}\n");
                html.append(".nav-bar:hover .partner-logo {\n");
                html.append("display: block;\n");
                html.append("}\n");
                html.append("</style>");
            }

            html.append("<div id=\"container\">\n");
            html.append("<ul class=\"main-menu\">\n");

            html.append("<li>\n");
            html.append("<a href=\"#no-back\" onClick=\"postData('../v3/carregarPrincipal?mostraMensagem=true&limitaMsg=true')\" alt=\"" + textoMenuPrincipal + "\" title=\"" + textoMenuPrincipal + "\">\n");
            html.append("<span class=\"icon-menu\"><svg width=\"27\" height=\"21\"><use xlink:href=\"#i-home\"></use></svg></span>\n");
            html.append(textoMenuPrincipal);
            html.append("</a>\n");
            html.append("</li>\n");

            for (MenuTO menu : menuList) {
                if (!menu.getItens().isEmpty()) {
                    Integer mnuCodigo = menu.getMnuCodigo();
                    String id = "";

                    html.append("<li>\n");
                    switch (mnuCodigo) {
                        // Favorito
                        case 0:
                            id = "menuFavoritos";
                            html.append("<a href=\"#menuFavoritos\" onclick=\"sortMenuv4('" + id + "');\" data-parent=\"#container\" data-bs-toggle=\"collapse\" aria-expanded=\"false\" aria-controls=\"menuFavoritos\" alt=\"" + TextHelper.forHtmlContent(menu.getMnuDescricao()) + "\" title=\"" + TextHelper.forHtmlContent(menu.getMnuDescricao()) + "\">\n");
                            html.append("<span class=\"icon-menu\"><svg width=\"26\" height=\"25\"><use xlink:href=\"#").append(menu.getMnuImagem()).append("\"></use></svg></span>\n");
                            html.append(TextHelper.forHtmlContent(menu.getMnuDescricao()));
                            html.append("</a>\n");
                            break;
                        // Operacional
                        case 1:
                            id = "menuOperacional";
                            html.append("<a href=\"#menuOperacional\" onclick=\"sortMenuv4('" + id + "');\" data-parent=\"#container\" data-bs-toggle=\"collapse\" aria-expanded=\"false\" aria-controls=\"menuOperacional\" alt=\"" + TextHelper.forHtmlContent(menu.getMnuDescricao()) + "\" title=\"" + TextHelper.forHtmlContent(menu.getMnuDescricao()) + "\">\n");
                            html.append("<span class=\"icon-menu\"><svg width=\"25\" height=\"23\"><use xlink:href=\"#").append(menu.getMnuImagem()).append("\"></use></svg></span>\n");
                            html.append(TextHelper.forHtmlContent(menu.getMnuDescricao()));
                            html.append("</a>\n");
                            break;
                        // Relatorio
                        case 2:
                            id = "menuRelatorio";
                            html.append("<a href=\"#menuRelatorio\" onclick=\"sortMenuv4('" + id + "');\" data-parent=\"#container\" data-bs-toggle=\"collapse\" aria-expanded=\"false\" aria-controls=\"menuRelatorio\" alt=\"" + TextHelper.forHtmlContent(menu.getMnuDescricao()) + "\" title=\"" + TextHelper.forHtmlContent(menu.getMnuDescricao()) + "\">\n");
                            html.append("<span class=\"icon-menu\"><svg width=\"22\" height=\"26\"><use xlink:href=\"#").append(menu.getMnuImagem()).append("\"></use></svg></span>\n");
                            html.append(TextHelper.forHtmlContent(menu.getMnuDescricao()));
                            html.append("</a>\n");
                            break;
                        // Manutencao
                        case 3:
                            id = "menuManutencao";
                            html.append("<a href=\"#menuManutencao\" onclick=\"sortMenuv4('" + id + "');\" data-parent=\"#container\" data-bs-toggle=\"collapse\" aria-expanded=\"false\" aria-controls=\"menuManutencao\" alt=\"" + TextHelper.forHtmlContent(menu.getMnuDescricao()) + "\" title=\"" + TextHelper.forHtmlContent(menu.getMnuDescricao()) + "\">\n");
                            html.append("<span class=\"icon-menu\"><svg width=\"26\" height=\"25\"><use xlink:href=\"#").append(menu.getMnuImagem()).append("\"></use></svg></span>\n");
                            html.append(TextHelper.forHtmlContent(menu.getMnuDescricao()));
                            html.append("</a>\n");
                            break;
                        // Sistema
                        case 4:
                            id = "menuSistema";
                            html.append("<a href=\"#menuSistema\" onclick=\"sortMenuv4('" + id + "');\" data-parent=\"#container\" data-bs-toggle=\"collapse\" aria-expanded=\"false\" aria-controls=\"menuSistema\" alt=\"" + TextHelper.forHtmlContent(menu.getMnuDescricao()) + "\" title=\"" + TextHelper.forHtmlContent(menu.getMnuDescricao()) + "\">\n");
                            html.append("<span class=\"icon-menu\"><svg width=\"24\" height=\"24\"><use xlink:href=\"#").append(menu.getMnuImagem()).append("\"></use></svg></span>\n");
                            html.append(TextHelper.forHtmlContent(menu.getMnuDescricao()));
                            html.append("</a>\n");
                            break;
                        case 5:
                            id = "menuBeneficios";
                            html.append("<a href=\"#menuBeneficios\" onclick=\"sortMenuv4('" + id + "');\" data-parent=\"#container\" data-bs-toggle=\"collapse\" aria-expanded=\"false\" aria-controls=\"menuBeneficios\" alt=\"" + TextHelper.forHtmlContent(menu.getMnuDescricao()) + "\" title=\"" + TextHelper.forHtmlContent(menu.getMnuDescricao()) + "\">\n");
                            html.append("<span class=\"icon-menu\"><svg width=\"26\" height=\"25\"><use xlink:href=\"#").append(menu.getMnuImagem()).append("\"></use></svg></span>\n");
                            html.append(TextHelper.forHtmlContent(menu.getMnuDescricao()));
                            html.append("</a>\n");
                            break;
                        case 7:
                            id = "menuRescisao";
                            html.append("<a href=\"#menuRescisao\" onclick=\"sortMenuv4('" + id + "');\" data-parent=\"#container\" data-bs-toggle=\"collapse\" aria-expanded=\"false\" aria-controls=\"menuRescisao\" alt=\"" + TextHelper.forHtmlContent(menu.getMnuDescricao()) + "\" title=\"" + TextHelper.forHtmlContent(menu.getMnuDescricao()) + "\">\n");
                            html.append("<span class=\"icon-menu\"><svg width=\"26\" height=\"25\"><use xlink:href=\"#").append(menu.getMnuImagem()).append("\"></use></svg></span>\n");
                            html.append(TextHelper.forHtmlContent(menu.getMnuDescricao()));
                            html.append("</a>\n");
                            break;
                        case 8:
                            id = "menuBI";
                            html.append("<a href=\"#menuBI\" onclick=\"sortMenuv4('" + id + "');\" data-parent=\"#container\" data-bs-toggle=\"collapse\" aria-expanded=\"false\" aria-controls=\"menuBI\" alt=\"" + TextHelper.forHtmlContent(menu.getMnuDescricao()) + "\" title=\"" + TextHelper.forHtmlContent(menu.getMnuDescricao()) + "\">\n");
                            html.append("<span class=\"icon-menu aling-top\"><svg width=\"26\" height=\"25\"><use xlink:href=\"#").append(menu.getMnuImagem()).append("\"></use></svg></span>\n");
                            html.append(TextHelper.forHtmlContent(menu.getMnuDescricao()));
                            html.append("</a>\n");
                        break;
                        case 6:
                            // Exibe menu de ajuda de contexto
                            // A ajuda de contexto possui somente um item menu, dessa forma não será criado sub-menu e ao clicar no menu exibirá diretamente a ajuda contextualizada
                            String acrRecurso = "../v3/visualizarAjudaContexto?acao=visualizar";
                            if (menu.getItens() != null && !menu.getItens().isEmpty()) {
                                acrRecurso = menu.getItens().stream().findFirst().get().getAcrRecurso();
                            }
                            acrRecurso += "&ajudaPopup=S&_skip_history_=true";

                            id = "menuAjuda";
                            html.append("<a href=\"#menuAjuda\" onclick=\"postData('").append(acrRecurso).append("');\" alt=\"" + TextHelper.forHtmlContent(menu.getMnuDescricao()) + "\" title=\"" + TextHelper.forHtmlContent(menu.getMnuDescricao()) + "\">\n");
                            html.append("<span class=\"icon-menu\"><svg width=\"23\" height=\"23\"><use xlink:href=\"#").append(menu.getMnuImagem()).append("\"></use></svg></span>\n");
                            html.append(TextHelper.forHtmlContent(menu.getMnuDescricao()));
                            html.append("</a>\n");
                            break;
                    }
                    html.append("</li>\n");

                    List<ItemMenuTO> subMenu = menu.getItens();
                    if (subMenu != null && !subMenu.isEmpty()) {
                        html.append("<div class=\"collapse\" id=\"").append(id).append("\">\n");
                        html.append("<ul class=\"submenu\">\n");

                        for (ItemMenuTO itemMenu : subMenu) {
                            List<String> naoExibir = new ArrayList<>();
                            //naoExibir.add(String.valueOf(ItemMenuEnum.AJUDA.getCodigo()));
                            naoExibir.add(String.valueOf(ItemMenuEnum.SAIR_DO_SISTEMA.getCodigo()));

                            // não imprimir o menu de ajuda
                            if (!naoExibir.contains(itemMenu.getItmCodigo())) {
                                html.append("<li>\n");
                                
                                if (itemMenu.getAcrRecurso().startsWith("javascript:")) {
                                    html.append("<a href=\"#no-back\" class=\"link-menu\" onClick=\"" + itemMenu.getAcrRecurso() + "\">" + TextHelper.forHtmlContent(itemMenu.getItmDescricao()) + "</a>\n");
                                } else {
                                    html.append("<a href=\"" + itemMenu.getAcrRecurso() + "\" class=\"link-menu\">" + TextHelper.forHtmlContent(itemMenu.getItmDescricao()) + "</a>\n");
                                }

                                String acaoFavorito = "";
                                if (!TextHelper.isNull(itemMenu.getImfSequencia())) {
                                    acaoFavorito = ApplicationResourcesHelper.getMessage("rotulo.menu.acao.remover.favorito", responsavel);
                                } else {
                                    acaoFavorito = ApplicationResourcesHelper.getMessage("rotulo.menu.acao.adicionar.favorito", responsavel);
                                }

                                html.append("<div class=\"dropdown more-options\">");
                                html.append("<a class=\"dropdown-toggle\" href=\"#\" role=\"button\" id=\"menuOptions\" data-bs-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\" alt=\"" + TextHelper.forHtmlContent(itemMenu.getItmDescricao()) + "\" title=\"" + TextHelper.forHtmlContent(itemMenu.getItmDescricao()) + "\">");
                                html.append("<svg><use xlink:href=\"#i-menu-dots\"></use></svg>");
                                html.append("</a>");
                                html.append("<div class=\"dropdown-menu dropdown-menu-right\" aria-labelledby=\"menuOptions\">");
                                html.append("<a class=\"dropdown-item\" href=\"javascript:void(0)\" onclick=\"favNavV4('" + itemMenu.getItmCodigo() + "')\" alt=\"" + acaoFavorito + "\" title=\"" + acaoFavorito + "\">");
                                html.append(acaoFavorito);
                                html.append("</a>");
                                html.append("</div>");
                                html.append("</div>");

                                html.append("</li>\n");

                            }
                        }

                        html.append("</ul>\n");
                        html.append("</div>\n");
                    }

                }
            }
            html.append("</ul>\n");
            html.append("<a id=\"btn-navbar\" class=\"btn-toggle-menu ").append(menuThin ? "btn-toggle-menu-active" : "").append("\" onClick=\"clickBtnMenu()\">");
            html.append("<span id=\"arrow-image\" class=\"").append(menuThin ? "rigth-arrow" : "left-arrow").append(" mt-1\"></span>");
            html.append("</a>");
            html.append("</div>\n");

            html.append("\n<script type=\"text/JavaScript\">\n");
            html.append("function reloadSidebar(){\n");
            html.append("$(\"#containerFavoritos\").load(\" #containerFavoritos > *\");\n");
            html.append("\n}\n");
            html.append("</script>\n");
        }

        return html.toString();
    }

}
