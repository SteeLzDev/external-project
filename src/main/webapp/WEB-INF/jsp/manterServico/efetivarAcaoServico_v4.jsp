<%--
* <p>Title: efetivarAcaoServico.jsp</p>
* <p>Description: Motivo de operação nas ações realizadas no serviço</p>
* <p>Copyright: Copyright (c) 2002-2018</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: andrea.giorgini $
* $Revision: 26722 $
* $Date: 2019-05-16 07:55:10 -0300 (qui, 16 mai 2019) $
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t"     tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl"    uri="/html-lib" %>
<%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.dto.web.*"%>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

  boolean podeEditarSvc = (Boolean) request.getAttribute("podeEditarSvc");
  
  Map<String, String[]> parametros = (Map<String, String[]>) request.getAttribute("parametros");
 
  String msgConfirmacao = (String) request.getAttribute("msgConfirmacao");
  String msgErro = (String) request.getAttribute("msgErro");
  String operacao = (String) request.getAttribute("operacao");
  String reqColumnsStr = (String) request.getAttribute("reqColumnsStr");
  String svcCodigo = (String) request.getAttribute("svcCodigo");
  String svcIdentificador = (String) request.getAttribute("svcIdentificador");
  String svcDescricao = (String) request.getAttribute("svcDescricao");
  String tituloPagina = (String) request.getAttribute("tituloPagina");
%>
<c:set var="title">
  <%=TextHelper.forHtml(tituloPagina)%>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
<form method="post" action="../v3/manterServico" name="formTmo">
  <div class="row">
    <div class="col-sm">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.efetiva.acao.servico.dados.servico"/></h2>
        </div>
          <input type="hidden" name="MM_update" value="formTmo">
          <%
          out.print(SynchronizerToken.generateHtmlToken(request));
          Iterator<String> chaves = parametros.keySet().iterator();
          while (chaves.hasNext()) {
              String chave = chaves.next();
              String[] valores = parametros.get(chave);
              for (int i = 0; i < valores.length; i++) {
                  out.print("<input type=\"hidden\" name=\"" + chave + "\" value=\"" + valores[i] + "\">");
              }
          }
          %>
        <div class="card-body">
          <div class="row">
              <div class="form-group col-sm-12 col-md-6 mt-1">
                <label for="SVC_IDENTIFICADOR"><hl:message key="rotulo.servico.identificador"/></label>
                <input class="form-control" name="SVC_IDENTIFICADOR" type="text" size="32"
                  value="<%=TextHelper.forHtmlContent(svcIdentificador)%>" disabled='disabled'
                />
              </div>
              <div class="form-group col-sm-12 col-md-6 mt-1">
                <label for="SVC_DESCRICAO"><hl:message key="rotulo.servico.descricao"/></label>
                <input class="form-control" name="SVC_DESCRICAO" type="text" size="32"
                      value="<%=TextHelper.forHtmlContent(svcDescricao)%>" disabled='disabled'
                />
              </div>
          </div>
        </div>
      </div>
    </div>
  </div>
    <div class="row">
    <div class="col-sm">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.motivo.operacao.singular" /></h2>
        </div>
        <div class="card-body">
          <div class="row">
            <div class="form-group col-sm-12">
              <!--  Precisa das duas linhas: a primeira inclui os campos que aparecem na tela, a segunda: inclui os javascripts -->
              <hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="<%=(String)msgConfirmacao%>" operacaoConvenio="true" inputSizeCSS="col-sm-12"/>
              <hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="<%=(String)msgConfirmacao%>" operacaoConvenio="true" inputSizeCSS="col-sm-12" scriptOnly="true"/>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  </form>
  <div class="btn-action">
   <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
   <a class="btn btn-primary" HREF="#no-back" onClick="if(confirmaAcaoConsignacao()){f0.submit();};return false;" ><hl:message key="rotulo.botao.salvar"/></a>
  </div>
</c:set>
<c:set var="javascript">
   <script type="text/JavaScript" src="../js/scripts_2810.js?<hl:message key="release.tag"/>"></script>
   <script type="text/JavaScript" src="../js/validaform.js?<hl:message key="release.tag"/>"></script>
   <script type="text/JavaScript" src="../js/validacoes.js?<hl:message key="release.tag"/>"></script>
   <script type="text/JavaScript" src="../js/xbdhtml.js?<hl:message key="release.tag"/>"></script>
   <script type="text/JavaScript">
     var f0 = document.forms[0];
  	 window.onload = formLoad;
     function formLoad(){
       focusFirstField();
     }
   </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>