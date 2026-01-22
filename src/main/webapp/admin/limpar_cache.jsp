<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist"%>
<%@ page import="com.zetra.econsig.values.CodedValues"%>
<%@ include file="../geral/env_navegacao.jsp" %>
<%
String ipsAcessoLiberado = (String) ParamSist.getInstance().getParam(CodedValues.TPC_IPS_LIBERADOS_PAGINA_ADMINISTRACAO, AcessoSistema.getAcessoUsuarioSistema());
if ((TextHelper.isNull(ipsAcessoLiberado) && JspHelper.validaLanIP(request.getRemoteAddr())) || JspHelper.validaDDNS(JspHelper.getRemoteAddr(request), ipsAcessoLiberado)) {
    out.print(JspHelper.limparCacheParametros());
} else {
    LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.acesso.limpar.cache", null, request.getRemoteAddr()));
}
%>