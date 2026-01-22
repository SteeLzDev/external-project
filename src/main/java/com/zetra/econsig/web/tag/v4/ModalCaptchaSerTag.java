package com.zetra.econsig.web.tag.v4;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.jsp.JspException;

import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.tag.ZetraTagSupport;

public class ModalCaptchaSerTag extends ZetraTagSupport {
    protected AcessoSistema responsavel;

    private String type;

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int doEndTag() throws JspException {
        try {
            pageContext.getOut().print(generateModal());
        } catch (IOException | UsuarioControllerException ex) {
            throw new JspException(ex.getMessage());
        }
        // Continue processing this page
        return (EVAL_PAGE);
    }

    public String generateModal() throws UsuarioControllerException {
        responsavel = JspHelper.getAcessoSistema((HttpServletRequest) pageContext.getRequest());
        final HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        final HttpSession session = pageContext.getSession();
        final ParamSession paramSession = ParamSession.getParamSession(session);

        boolean exibeCaptcha = false;
        boolean exibeCaptchaAvancado = false;
        boolean exibeCaptchaDeficiente = false;
        String refresh = SynchronizerToken.updateTokenInURL(paramSession.getCurrentHistory(),request);


        if(refresh == null){
            refresh = "refreshPage";
        }

        if (type.equals("topo")) {
            exibeCaptcha = (boolean) session.getAttribute("exibeCaptcha");
            exibeCaptchaAvancado = (boolean) session.getAttribute("exibeCaptchaAvancado");
            exibeCaptchaDeficiente = (boolean) session.getAttribute("exibeCaptchaDeficiente");
        } else if (type.equals("consultar")) {
            final boolean defVisual = responsavel.isDeficienteVisual();
            if (!defVisual) {
                exibeCaptchaAvancado = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_AVANCADO_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                exibeCaptcha = !exibeCaptchaAvancado;
            } else {
                exibeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            }
        } else {
            exibeCaptcha = (boolean) request.getAttribute("exibeCaptcha");
            exibeCaptchaAvancado = (boolean) request.getAttribute("exibeCaptchaAvancado");
            exibeCaptchaDeficiente = (boolean) request.getAttribute("exibeCaptchaDeficiente");
        }

        String chavePublica = "";
        if (exibeCaptchaAvancado) {
            chavePublica = (String) ParamSist.getInstance().getParam(CodedValues.TPC_CAPTCHA_AVANCADO_CHAVE_PUBLICA, responsavel);
            if (chavePublica.isEmpty()) {
                throw new UsuarioControllerException("mensagem.erro.captcha.invalido", (AcessoSistema) null);
            }
        }

        final StringBuilder html = new StringBuilder();
        html.append(" <div class=\"modal fade\" id=\"modalCaptcha_" + TextHelper.forJavaScriptAttribute(type) +  "\" tabindex=\"-1\" aria-labelledby=\"TESTE\" aria-hidden=\"true\">");
        html.append(" <div class=\"modal-dialog\">");
        html.append(" <div class=\"modal-content\" id=\"modal-captcha\">");
        html.append(" <div class=\"modal-header\">");
        html.append(" <h5 class=\"modal-title\" id=\"modalCatpchaTitle\">").append( ApplicationResourcesHelper.getMessage("rotulo.captcha.visualizar", responsavel)).append("</h5>");
        html.append(" <button type=\"button\" class=\"btn-close\" data-bs-dismiss=\"modal\" aria-label=\"Close\"></button>");
        html.append(" </div>");
        html.append(" <div class=\"modal-body\">");
        html.append(" <div class=\"row\">");
        if (exibeCaptcha || exibeCaptchaDeficiente) {
            html.append(" <div class=\"form-group col-sm-4\">" +
                    " <label for=\"codigoCa\">" + ApplicationResourcesHelper.getMessage("rotulo.captcha.codigo", responsavel) + "</label>" +
                    " <input type='text' class='form-control' id=\"captcha_" + TextHelper.forJavaScriptAttribute(type) +  "\" name=\"captcha\" placeholder=\"" + ApplicationResourcesHelper.getMessage("mensagem.informacao.login.digite.codigo.acesso", responsavel) + "\"/>").append("</div>");
        }
        html.append(" <div class=\"form-group col-sm-6\">" +
                " <div class=\"captcha\">");
        if (exibeCaptcha) {
            html.append("<img id=\"captcha_img_" + TextHelper.forJavaScriptAttribute(type) +  "\" name=\"captcha_img_" + TextHelper.forJavaScriptAttribute(type) +  "\" src=\"../captcha.jpg?t=" + DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss") + "\"" + "/>")
                    .append(" <a href=\"#no-back\" class=\"btn-i-right pr-1\" data-bs-toggle=\"popover\" title=\"" + ApplicationResourcesHelper.getMessage("rotulo.ajuda", responsavel))
                    .append("\" data-bs-content=\"" + ApplicationResourcesHelper.getMessage("mensagem.ajuda.captcha.operacao.imagem.v3", responsavel) + "\" data-original-title=" + ApplicationResourcesHelper.getMessage("rotulo.ajuda", responsavel) + ">")
                    .append(" <img src=\"../img/icones/help.png\" alt=\"" + ApplicationResourcesHelper.getMessage("rotulo.ajuda", responsavel) + "title=\"" + ApplicationResourcesHelper.getMessage("rotulo.ajuda", responsavel) + "\"\n</a>")
                    .append(" <a href=\"#no-back\" onclick=reloadCaptchaSer('" + TextHelper.forJavaScriptAttribute(type) + "'); ><img src=../img/icones/refresh.png alt=\"" + ApplicationResourcesHelper.getMessage("rotulo.captcha.novo.codigo", responsavel) + "\" title=\"" + ApplicationResourcesHelper.getMessage("rotulo.captcha.novo.codigo", responsavel) + "\" /></a>\n");

        } else if (exibeCaptchaAvancado) {
            html.append("<div class=\"g-recaptcha\" data-sitekey=\"").append(chavePublica).append("\"></div>");
        } else if (exibeCaptchaDeficiente) {
            html.append("<div id=\"divCaptchaSound_" + TextHelper.forJavaScriptAttribute(type) + "\"></div>\n" +
                    "                    <a href=\"#no-back\" onclick=\"reloadSimpleCaptchaSer('" + TextHelper.forJavaScriptAttribute(type) + "');\"><img src=\"../img/icones/refresh.png\" alt=" + ApplicationResourcesHelper.getMessage("rotulo.captcha.novo.audio", responsavel) + "title=" + ApplicationResourcesHelper.getMessage("rotulo.captcha.novo.audio", responsavel) + "/></a>\n" +
                    "                    <a href=\"#no-back\" onclick=\"helpCaptcha5();\"><img src=\"../img/icones/help.png\" alt=" + ApplicationResourcesHelper.getMessage("rotulo.ajuda", responsavel) + "title=" + ApplicationResourcesHelper.getMessage("rotulo.ajuda", responsavel) + "/></a>\n");
        }
        html.append("</div></div>");
        html.append("<div class=\"btn-action\">");
        html.append("<a href=\"#\" class=\"btn btn-primary\" onclick=\"visualizarMargemSer('"+ TextHelper.forJavaScriptAttribute(type) + "','" + TextHelper.encode64((SynchronizerToken.generateToken4URL(request))) + "','" + TextHelper.forJavaScriptAttribute(exibeCaptchaAvancado) + "','" + TextHelper.encode64((refresh)) + "')\">").append(ApplicationResourcesHelper.getMessage("rotulo.captcha.ver.margem", responsavel)).append("</a>");
        html.append("</div>");
        html.append("      </div>");
        html.append("     </div>");
        html.append("    </div>");
        html.append("   </div>");
        html.append("  </div>");

        return html.toString();
    }
}
