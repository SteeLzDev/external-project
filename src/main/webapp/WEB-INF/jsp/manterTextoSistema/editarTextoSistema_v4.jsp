<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="java.math.BigDecimal, java.io.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.TextoSistemaTO"%>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%

String btnCancelar = (String) request.getAttribute("btnCancelar");
TextoSistemaTO textoSistemaTO = (TextoSistemaTO) request.getAttribute("textoSistemaTO");
%>
<link type="text/css" rel="stylesheet" href="../css/uedit.ui.css" />
<link type="text/css" rel="stylesheet" href="../css/uedit.ui.complete.css" />
<c:set var="title">
   <hl:message key="rotulo.editar.texto.sistema"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-sistema"></use>
</c:set>

<c:set var="bodyContent">
	<div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><%=TextHelper.forHtmlContent(textoSistemaTO.getTexChave())%></h2>
        </div>
        <div class="card-body">
			<form method="post" action="../v3/manterTextoSistema?acao=salvar&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
				<hl:htmlinput name="texChave" type="hidden" value="<%=TextHelper.forHtmlAttribute(textoSistemaTO.getTexChave())%>"/>
            	<div class="row">
                	<div class="form-group col-sm-12 col-md-12">
                        <ul id="uedit_button_strip"></ul>
                    	<textarea class="form-control" name="innerTemp" cols="80" rows="30" id="uedit_textarea" onFocus="SetarEventoMascara(this,'#*65000',true); " onBlur="fout(this);ValidaMascara(this);"><%=TextHelper.forHtmlContent(textoSistemaTO.getTexTexto())%></textarea>
                    </div>
                </div>
  			</form>
		</div>
	</div>
	<div class="btn-action">
        <input type="hidden" name="innerTemp" value="">
		<a class="btn btn-outline-danger" href="#no-back" onClick="<%="postData('" + TextHelper.forJavaScriptAttribute(btnCancelar) + "'); return false;"%>" id="btnCancelar"><hl:message key="rotulo.botao.cancelar"/></a>
		<a class="btn btn-primary" HREF="#no-back" onClick="if (validaCampos()){f0.submit();} return false;"><hl:message key="rotulo.botao.salvar"/></a>
	</div>
</c:set>

<c:set var="javascript">
  <script id="MainScript">
  	var f0 = document.forms[0];
  
  	function formLoad() {
  			focusFirstField();
  	}

  	function temTag(texto) {
  			var pos = texto.indexOf('<');
  			while (pos != -1) {
    			pos++;
    			if (pos < texto.length && texto.charAt(pos) != ' ') {
      				return true;
    			}
    			pos = texto.indexOf('<', pos);
  			}
  			return false;
  	}
  
  	function validaCampos() {
  			field = getElt("innerTemp");
  			if (field.value == '') {
    			alert(mensagem('mensagem.informe.texto.sistema.texto'));
    			field.focus();
    			return false;
  			} else if (temTag(field.value)) {
    			alert(mensagem('mensagem.erro.texto.sistema.edicao.bloqueada'));
    			field.focus();
    			return false;
  			}
  			return true;
  	}
  
  	window.onload = formLoad;
  </script>
  <script type="text/javascript" src="../js/uedit.js?<hl:message key="release.tag"/>"></script>
  <script type="text/javascript" src="../js/uedit.ui.complete.js?<hl:message key="release.tag"/>"></script>
  <script type="text/javascript">
      var uedit_textarea = document.getElementById("uedit_textarea");
      var uedit_button_strip = document.getElementById("uedit_button_strip");
      var ueditorInterface = ueditInterface(uedit_textarea, uedit_button_strip);
  </script>
</c:set>
<t:page_v4>
	<jsp:attribute name="header">${title}</jsp:attribute>
	<jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
	<jsp:attribute name="javascript">${javascript}</jsp:attribute>
	<jsp:body>${bodyContent}</jsp:body>
</t:page_v4>