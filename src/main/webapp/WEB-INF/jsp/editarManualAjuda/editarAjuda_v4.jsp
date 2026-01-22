<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.persistence.entity.Ajuda"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
String rotuloBotaoCancelar = (String) request.getAttribute("rotuloBotaoCancelar");

Ajuda ajuda = (Ajuda) request.getAttribute("ajuda");

String ajuTitulo = (String) request.getAttribute("ajuTitulo");
String tituloPagina = (String) request.getAttribute("tituloPagina");
String ajuTexto = (String) request.getAttribute("ajuTexto");
List<?> acrCodigos = (List<?>) request.getAttribute("acrCodigos");
String ajuSequencia = (String) request.getAttribute("ajuSequencia");
int qtdeAcessoAjudaCadastrada = (Integer) request.getAttribute("qtdeAcessoAjudaCadastrada");
String acrCodigoOriginal = (String) request.getAttribute("acrCodigoOriginal");
String ajudaPopup = (String) request.getAttribute("ajudaPopup");

String acao = (String) request.getAttribute("acao");
%>
<c:set var="title">
  <%=TextHelper.forHtmlAttribute(tituloPagina)%>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-editar"></use>
</c:set>
<c:set var="bodyContent">
  <div class="card">
    <div class="card-header hasIcon pl-3">
      <h2 class="card-header-title">
        <%=TextHelper.forHtmlAttribute(tituloPagina)%>
        <% if(!ajuTitulo.isEmpty()) {%> - <%=TextHelper.forHtmlContent(ajuTitulo)%>
        <% } %>
      </h2>
    </div>
    <form method="post" action="../v3/editarManualAjuda?acao=salvar&acrCodigoOriginal=<%=acrCodigoOriginal != null ? acrCodigoOriginal : acrCodigos.get(0)%>&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
      <div class="form-group col-sm-4">
        <label for="ajuTitulo">
          <hl:message key="rotulo.ajuda.titulo" />
        </label>
        <hl:htmlinput name="ajuTitulo" type="text" classe="form-control" value="<%=TextHelper.forHtmlAttribute(ajuTitulo)%>" size="32" mask="#*A40"/>
        <label for="ajuSequencia">
          <hl:message key="rotulo.ajuda.sequencia" />
        </label>
        <hl:htmlinput name="ajuSequencia" type="text" classe="form-control" value="<%=(String) (ajuda != null ? ajuda.getAjuSequencia().toString() : "")%>" size="6" mask="#*D4"/>
      </div>
      <div class="row p-4">
        <div class="form-group col-sm-12 col-md-12">
          <ul style="margin-top: 5px" id="uedit_button_strip"></ul>
          <textarea name="innerTemp" cols="120" rows="30" class="form-control" id="uedit_textarea" onFocus="SetarEventoMascara(this,'#*65000',true);" onBlur="fout(this);ValidaMascara(this);"><%=TextHelper.forHtmlContent(ajuda != null ? ajuda.getAjuTexto() : "")%></textarea>
        </div>
      </div>
      <% 
      Iterator ite = acrCodigos.iterator();
      while (ite.hasNext()) {
    %>
      <hl:htmlinput name="acrCodigos" type="hidden" value="<%=TextHelper.forHtmlAttribute(ite.next())%>"/>
    <% 
      }
    %>
    </form>
  </div>

    <div class="btn-action">
      <input type="hidden" name="MM_update" value="form1">
      <input type="hidden" name="innerTemp" value="">
      <input type="hidden" name="acao" value="salvar">

      <a class="btn btn-outline-danger mt-3" href="#no-back" onclick="postData('../v3/visualizarAjudaContexto?acao=visualizar&ajudaPopup=<%=TextHelper.forJavaScriptAttribute(ajudaPopup)%>&_skip_history_=true&acrCodigo=<%=acrCodigos.get(0)%>&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>'); return false;" id="btnCancelar"><%=TextHelper.forHtmlContent(rotuloBotaoCancelar)%></a>
      <a class="btn btn-primary mt-3" href="#no-back" onclick="if(validaCampos()){f0.submit();} return false;"><hl:message key="rotulo.botao.salvar"/></a>
    </div>  
</c:set>

<c:set var="javascript">
  <link type="text/css" rel="stylesheet" href="../css/uedit.ui.css" />
  <link type="text/css" rel="stylesheet" href="../css/uedit.ui.complete.css" />
  <script type="text/javascript" src="../js/uedit.js"></script>
  <script type="text/javascript" src="../js/uedit.ui.complete.js"></script>
  <script id="MainScript">
    var f0 = document.forms[0];

    function formLoad() {
      focusFirstField();
    }
    window.onload = formLoad;

    function validaCampos(){
      field = getElt("ajuTitulo");
      if (field.value == '') {
        alert('<hl:message key="mensagem.informe.titulo.ajuda"/>');
        field.focus();
        return false;
      }

      <% if (qtdeAcessoAjudaCadastrada > 1) { %>
        return confirm('<hl:message key="mensagem.ajuda.informacoes.sobreescritas"/>' + "\n\n" + '<hl:message key="mensagem.ajuda.continuar.operacao"/>');
      <% } else { %>
        return true;
      <% } %>
    }
  </script>
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
