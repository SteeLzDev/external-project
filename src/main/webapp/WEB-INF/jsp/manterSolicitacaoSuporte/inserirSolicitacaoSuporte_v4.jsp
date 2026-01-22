<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken"%>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
Map<String, String> servicoMap = (Map<String, String>) request.getAttribute("servicoMap");
%>
<c:set var="title">
<hl:message key="rotulo.inserir.solicitacao.suporte.titulo"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-home"></use>
</c:set>
<c:set var="bodyContent">
<form method="post" action="../v3/inserirSolicitacaoSuporte?acao=salvar&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1" >
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.solicitacao.suporte.dados.solicitacao"/></h2>
    </div>
    <div class="card-body">
      <div class="row">
        <div class="form-group col-sm-6">
          <label for="sosServico"><hl:message key="rotulo.solicitacao.suporte.tipo.servico"/></label>
          <select class="form-control select" name="sosServico" id="sosServico" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);">
            <option value="" selected><hl:message key="rotulo.campo.selecione"/></option>
            <%
              Set<String> servicoKey = servicoMap.keySet();
              Iterator<String> itKey = servicoKey.iterator();
              while (itKey.hasNext()) {
                  String key = itKey.next();
                  String value = servicoMap.get(key);
                %>
                <option value="<%=TextHelper.forHtmlAttribute(key)%>"><%=TextHelper.forHtmlContent(value)%></option>
                <%    
              }
            %>
          </select>
        </div>
        <div class="form-group col-sm-6">
          <label for="sosSumario"><hl:message key="rotulo.solicitacao.suporte.assunto"/></label>
          <input type="text" placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.assunto", responsavel)%>" class="form-control" name="sosSumario" id="sosSumario" TYPE="text" VALUE="" SIZE="100" onFocus="SetarEventoMascara(this,'#*200',true);">
        </div>
        <div class="form-check form-group col-md-12 mt-2">
          <label for="adeObs"><hl:message key="rotulo.solicitacao.suporte.descricao"/></label>
          <textarea class="form-control" name="sosDescricao" cols="100" rows="15" onFocus="SetarEventoMascara(this,'#*65000',true);" onBlur="fout(this);ValidaMascara(this);"></textarea>
        </div>
      </div>
    </div>
  </div>
  <div class="btn-action mt-3">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
    <a class="btn btn-primary" href="#no-back" onClick="enviar(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
  </div>
  <input name="FORM" type="hidden" value="form1"> 
</form>
</c:set>
<c:set var="javascript">
<script language="JavaScript" type="text/JavaScript">
   function enviar() {
    var sosServico = f0.sosServico.value;
    var sosDescricao = f0.sosDescricao.value;
    var sosSumario = f0.sosSumario.value;
    
    if ((sosServico == null || sosServico == "") ||
        (sosDescricao == null || sosDescricao == "") ||
        (sosSumario == null || sosSumario == "")) {
      alert('<hl:message key="mensagem.informe.todos.campos"/>');
      return false;
    }
    
    if ((sosSumario != null && sosSumario != "") && sosSumario.length > 200) {
         alert('<hl:message key="mensagem.erro.assunto.tamanho.maximo"/>');
         f0.sosSumario.value = "";
         f0.sosSumario.focus();
         return;
        }
     
     f0.submit();
   }
</script>
<script language="JavaScript" type="text/JavaScript">
    f0 = document.forms[0];
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>      