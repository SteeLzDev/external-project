package com.zetra.econsig.web.controller;

import java.util.List;

import org.springframework.ui.Model;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;

import jakarta.servlet.http.HttpServletRequest;

/**
 * <p>Title: ControlePaginacaoWebController</p>
 * <p>Description: Controlador Web para geração de link para paginação.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class ControlePaginacaoWebController extends AbstractWebController {

    private String getLinkPaginacaoDireto(String linkPaginaLista, int totalRegistros, List<String> requestParams, HttpServletRequest request, boolean skipHistory) {
        StringBuilder linkListBuild = new StringBuilder(linkPaginaLista);
        String queryString = getQueryString(requestParams, request);

        if (!TextHelper.isNull(queryString)) {
            if (linkListBuild.toString().indexOf('?') == -1) {
                linkListBuild.append("?").append(queryString);
            } else {
                linkListBuild.append("&").append(queryString);
            }
        }
        if (linkListBuild.toString().indexOf('?') == -1) {
            linkListBuild.append("?pager=true");
        } else {
            linkListBuild.append("&pager=true");
        }
        if (skipHistory) {
            linkListBuild.append("&_skip_history_=true");
        }
        return linkListBuild.toString();
    }

    @Deprecated
    protected String montaLinkQrystring(String link, List<String> requestParamNames, HttpServletRequest request) {
        StringBuilder linkListBuild = new StringBuilder(link);

        if (requestParamNames != null && !requestParamNames.isEmpty()) {
            boolean primeiro = true;

            for (String param: requestParamNames) {
                String paramValue = TextHelper.forHtml(request.getParameter(param));
                if (!TextHelper.isNull(paramValue)) {
                    if (primeiro) {
                        linkListBuild.append("$").append(param).append("(").append(paramValue);
                        primeiro = false;
                    } else {
                        linkListBuild.append("|").append(param).append("(").append(paramValue);
                    }
                }
            }
        }

        return linkListBuild.toString();
    }

    protected String getQueryString(List<String> requestParams, HttpServletRequest request) {
        StringBuilder linkListBuild = new StringBuilder();

        // Concatena os parâmetros de request
        if (requestParams != null && !requestParams.isEmpty()) {
            for (String param: requestParams) {
                String[] paramValues = request.getParameterValues(param);
                if (paramValues != null && paramValues.length > 0) {
                    for (String paramValue : paramValues) {
                        if (!TextHelper.isNull(paramValue)) {
                            linkListBuild.append("&").append(param).append("=").append(TextHelper.forUriComponent(paramValue));
                        }
                    }
                }
            }
        }
        // Remove o primeiro "&"
        if (linkListBuild.length() > 0) {
            linkListBuild.deleteCharAt(0);
        }

        return linkListBuild.toString();
    }


    protected void configurarPaginador(String linkPaginaListagem, String chaveTituloPaginacao, int totalRegistros, int qtdRegistrosPorPagina, List<String> requestParams, boolean skipHistory, HttpServletRequest request, Model model) {
        configurarPaginador("", linkPaginaListagem, chaveTituloPaginacao, totalRegistros, qtdRegistrosPorPagina, requestParams, skipHistory, request, model);
    }

    protected void configurarPaginador(String indice, String linkPaginaListagem, String chaveTituloPaginacao, int totalRegistros, int qtdRegistrosPorPagina, List<String> requestParams, boolean skipHistory, HttpServletRequest request, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String offsetParam = "offset" + indice;

        int offset = (!TextHelper.isNull(request.getParameter(offsetParam)) && TextHelper.isNum(request.getParameter(offsetParam))) ?  Integer.parseInt(request.getParameter(offsetParam)) : 0;

        int offsetMax = totalRegistros - ((totalRegistros % qtdRegistrosPorPagina) == 0 ? qtdRegistrosPorPagina : totalRegistros % qtdRegistrosPorPagina);
        if (offset == -1) {
            offset = offsetMax;
        }

        int first = (totalRegistros > 0) ? offset + 1 : 0;
        int last = Math.min(offset + qtdRegistrosPorPagina, totalRegistros);

        int actualPage = (last / qtdRegistrosPorPagina) + ((last % qtdRegistrosPorPagina) == 0 ? 0 : 1);
        int offsetPreviousPage = Math.max((actualPage - 2) * qtdRegistrosPorPagina, 0);

        model.addAttribute("_paginacaoOffsetParam" + indice, offsetParam);
        model.addAttribute("_paginacaoOffset" + indice, offset);
        model.addAttribute("_paginacaoQtdTotal" + indice, totalRegistros);
        model.addAttribute("_paginacaoQtdPorPagina" + indice, qtdRegistrosPorPagina);
        model.addAttribute("_paginacaoQtdPagina" + indice, (totalRegistros / qtdRegistrosPorPagina) + ((totalRegistros % qtdRegistrosPorPagina) == 0 ? 0 : 1));
        model.addAttribute("_paginacaoPaginaAtual" + indice, actualPage);
        model.addAttribute("_paginacaoPaginaAnterior" + indice, offsetPreviousPage);
        model.addAttribute("_paginacaoPrimeiro" + indice, first);
        model.addAttribute("_paginacaoUltimo" + indice, last);
        model.addAttribute("_paginacaoQtdAtalhos" + indice, 5);
        model.addAttribute("_paginacaoTitulo" + indice, ApplicationResourcesHelper.getMessage(chaveTituloPaginacao, responsavel));
        model.addAttribute("_paginacaoSubTitulo" + indice, ApplicationResourcesHelper.getMessage("rotulo.paginacao.registros.sem.estilo", responsavel, String.valueOf(first), String.valueOf(last), String.valueOf(totalRegistros)));
        model.addAttribute("_linkPaginacao" + indice, getLinkPaginacaoDireto(linkPaginaListagem, totalRegistros, requestParams, request, skipHistory));
    }
}
