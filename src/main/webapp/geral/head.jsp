<%@page import="com.zetra.econsig.helper.parametro.ParamSist"%>
<%@page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@page import="com.zetra.econsig.helper.web.JspHelper"%>
<%@page import="com.zetra.econsig.values.CodedValues"%>
<%@page import="com.zetra.econsig.helper.texto.DateHelper"%>
<%@page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%
response.setHeader("Cache-Control", "private");

AcessoSistema usuarioLogado = JspHelper.getAcessoSistema(request);

boolean sessaoValida = usuarioLogado != null 
                        && usuarioLogado.getUsuCodigo() != null 
                        && !usuarioLogado.getUsuCodigo().equals(CodedValues.USU_CODIGO_SISTEMA);
%>
<script language="JavaScript" type="text/JavaScript" src="../node_modules/jquery/dist/jquery.min.js?<hl:message key="release.tag"/>"></script>
<script language="JavaScript" type="text/JavaScript" src="../js/jquery-impromptu.js?<hl:message key="release.tag"/>"></script>
<script language="JavaScript" type="text/JavaScript" src="../js/jquery-nostrum.js?<hl:message key="release.tag"/>"></script>
<script language="JavaScript" type="text/JavaScript" src="../js/intl/numeral.min.js?<hl:message key="release.tag"/>"></script>
<script language="JavaScript" type="text/JavaScript" src="../js/intl/languages.min.js?<hl:message key="release.tag"/>"></script>
<script language="JavaScript" type="text/JavaScript" src="../js/mensagens.jsp?t=<%=DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss")%>"></script>
<script language="JavaScript" type="text/JavaScript" src="../js/SimpleAjaxUploader.min.js?<hl:message key="release.tag"/>"></script>
<script language="JavaScript" type="text/JavaScript" src="../js/UploadAnexo.js?<hl:message key="release.tag"/>"></script>
<script language="JavaScript" type="text/JavaScript" src="../js/sidebar.js?<hl:message key="release.tag"/>"></script>
<script language="JavaScript" type="text/JavaScript" src="../node_modules/mobile-detect/mobile-detect.min.js?<hl:message key="release.tag"/>"></script>
<script type="text/javascript" language="JavaScript">
navigator.sayswho= (function(){
    var ua= navigator.userAgent, tem, 
    M= ua.match(/(opera|chrome|safari|firefox|msie|trident(?=\/))\/?\s*([\d\.]+)/i) || [];
    if(/trident/i.test(M[1])){
        tem=  /\brv[ :]+(\d+(\.\d+)?)/g.exec(ua) || [];
        return 'IE '+(tem[1] || '');
    }
    M= M[2]? [M[1], M[2]]:[navigator.appName, navigator.appVersion, '-?'];
    if((tem= ua.match(/version\/([\.\d]+)/i))!= null) M[2]= tem[1];
    //return M.join(' ');
    return M[0];
})();

window.history.forward();
if (navigator.sayswho.toLowerCase() == "msie") {
  window.location.hash="no-back";
  window.onhashchange = function(){ window.location.hash = "no-back"; }
} else {
  window.history.pushState({}, "", "#no-back");
  window.onhashchange = function(){ window.location.hash = "no-back"; }
}

numeral.language(locale());

if ("function" == typeof parent.startCountdown) {
	parent.startCountdown(<%=session.getMaxInactiveInterval()%>);
	parent.continueCountdown(<%=sessaoValida%>);
}

if (window.opener != null) {
	if ("function" == typeof window.opener.parent.startCountdown) {
    	window.opener.parent.startCountdown(<%=session.getMaxInactiveInterval()%>);
    	window.opener.parent.continueCountdown(<%=sessaoValida%>);
	}
}
</script>
<link rel="stylesheet" href="../node_modules/font-awesome/css/font-awesome.min.css">
<link rel="stylesheet" href="../css/sidebar-style.css">
