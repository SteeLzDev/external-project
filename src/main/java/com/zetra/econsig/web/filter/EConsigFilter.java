package com.zetra.econsig.web.filter;

import static com.zetra.econsig.web.controller.admin.CompilarJspWebController.JSP_PRECOMPILE_PARAM;

import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;

import jakarta.servlet.Filter;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;

/**
 * <p>Title: EConsigFilter</p>
 * <p>Description: Filtro base do qual todos os filtros do sistema extendem.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class EConsigFilter implements Filter {

    /**
     * Determina se o filtro deve prosseguir a execução da requisição sem executar as validações
     * @param request
     * @return
     */
    protected boolean prosseguirSemExecutar(ServletRequest servletRequest) {
        if (servletRequest instanceof final HttpServletRequest request) {
            if ((request.getParameter(JSP_PRECOMPILE_PARAM) != null) && request.getParameter(JSP_PRECOMPILE_PARAM).equals("true")) {
                final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
                String ipsAcessoLiberado = (String) ParamSist.getInstance().getParam(CodedValues.TPC_IPS_LIBERADOS_PAGINA_ADMINISTRACAO, responsavel);
                if (TextHelper.isNull(ipsAcessoLiberado)) {
                    ipsAcessoLiberado = "127.0.0.1";
                }
                if (JspHelper.validaDDNS(JspHelper.getRemoteAddr(request), ipsAcessoLiberado)) {
                    return true;

                }
            }
            if (getRecurso(request).startsWith("/WEB-INF/jsp/")) {
                return true;
            }
        }
        return false;
    }

    /**
     * extrai o nome do recurso idependente do contexto da aplicação.
     * @param request
     * @return
     */
    public static String getRecurso(HttpServletRequest request) {
        final String uri = request.getRequestURI();
        final String context = request.getContextPath();

        return uri.substring(uri.indexOf(context) + context.length());
    }
}