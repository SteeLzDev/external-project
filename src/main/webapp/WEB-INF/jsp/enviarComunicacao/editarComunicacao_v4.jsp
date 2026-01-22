<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.upload.UploadHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.helper.web.ParamSession"%>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.persistence.entity.Usuario"%>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

boolean podeConsultarAde = (boolean) request.getAttribute("podeConsultarAde");
boolean podeEditarCmn    = (boolean) request.getAttribute("podeEditarCmn");
boolean podeIncluirAnexo = (boolean) request.getAttribute("podeIncluirAnexo");
boolean sucesso          = (boolean) request.getAttribute("sucesso");

CustomTransferObject comunicacao = (CustomTransferObject) request.getAttribute("comunicacao");
CustomTransferObject servInfo    = (CustomTransferObject) request.getAttribute("servInfo");

List anexos      = (List) request.getAttribute("anexos");
List respostas   = (List) request.getAttribute("respostas");

ParamSession paramSession = (ParamSession) request.getAttribute("paramSession");

String tituloPagina   = (String) request.getAttribute("tituloPagina");
String cmnCodigo      = (String) request.getAttribute("cmnCodigo");
String rseCodigo      = (String) request.getAttribute("rseCodigo");
String rseMatricula   = (String) request.getAttribute("rseMatricula");
String serCpf         = (String) request.getAttribute("serCpf");

UploadHelper uploadHelper = (UploadHelper) request.getAttribute("uploadHelper");
Usuario usuResponsavel = (Usuario) request.getAttribute("usuResponsavel");
String adeCodigo = (String) comunicacao.getAttribute(Columns.CMN_ADE_CODIGO);
%>
<c:set var="title">
  <%=TextHelper.forHtml(tituloPagina)%>
</c:set>

<c:set var="imageHeader">
  <use xlink:href="#i-sistema"></use>
</c:set> 
<c:set var="bodyContent">  
  <%if (servInfo != null) { %>
  <div class="card">
        <div class="card-header hasIcon">
          <span class="card-header-icon"><svg width="26">
              <use xlink:href="#i-servidor"></use></svg></span>
          <h2 class="card-header-title"><hl:message key="rotulo.servidor.dados"/></h2>
        </div>
        <div class="card-body">
          <dl class="row data-list">
          <%-- Utiliza a tag library DetalheServidorTag.java para exibir os dados do servidor --%>
          <% pageContext.setAttribute("servidor", servInfo); %>
          <hl:detalharServidorv4 name="servidor"/>
          <%-- Fim dos dados do servidor --%> 
          <%
                boolean isser = responsavel.isSer();
                if (podeConsultarAde && (!isser || !TextHelper.isNull(adeCodigo) )) {
                  session.setAttribute("servidor", servInfo);
          %>
             <hl:htmlinput name="cmnCodigo" type="hidden" di="RSE_MATRICULA" value="<%=TextHelper.forHtmlAttribute(cmnCodigo)%>" />     
             <dt class="col-6">
             	<hl:message key="rotulo.consultar.consignacao.titulo"/>
             </dt>  
             <dd class="col-6">
               <%if (TextHelper.isNull(adeCodigo)) { %>
	               <a class="pl-0" href="#no-back" onclick="postData('../v3/consultarConsignacao?acao=pesquisarConsignacao&RSE_CODIGO=<%=TextHelper.forJavaScript(rseCodigo)%>&RSE_MATRICULA=<%=TextHelper.forJavaScript(rseMatricula)%>&SER_CPF=<%=TextHelper.forJavaScript(serCpf)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');">
	                <hl:message key="rotulo.consultar.consignacao.titulo"/>
	               </a>
               <%} else {%>
               		<a class="pl-0" href="#no-back" onclick="postData('../v3/consultarConsignacao?acao=detalharConsignacao&ADE_CODIGO=<%=TextHelper.forJavaScript(adeCodigo)%>&RSE_CODIGO=<%=TextHelper.forJavaScript(rseCodigo)%>&RSE_MATRICULA=<%=TextHelper.forJavaScript(rseMatricula)%>&SER_CPF=<%=TextHelper.forJavaScript(serCpf)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');">
	                	<hl:message key="rotulo.visualizar.consignacao.titulo"/>
	                </a>
               <%} %>
             </dd>
       </dl>
      <%} %> 
      </div>
    </div>
    <%} %>
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.comunicacao.original.titulo"/></h2>
        </div>
        <div class="card-body">
          <p class="font-weight-bold"><%=ApplicationResourcesHelper.getMessage("mensagem.comunicacao.original", responsavel, TextHelper.forHtmlContent(usuResponsavel.getUsuNome()), DateHelper.toDateTimeString((Date) comunicacao.getAttribute(Columns.CMN_DATA)), TextHelper.forHtmlContent((String) comunicacao.getAttribute("NOME_ENTIDADE_DESTINATARIO")))%></p>
          <p><%=TextHelper.forHtmlContent((String) comunicacao.getAttribute(Columns.CMN_TEXTO))%></p>
        </div>
      </div>
      
    <%
        if (!respostas.isEmpty()) {
    %>
    <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.comunicacao.resposta.plural"/></h2>
        </div>
        <div class="card-body">
              <%
                  Iterator respIt = respostas.iterator();
                        CustomTransferObject resposta = null;
                        while (respIt.hasNext()) {
                            resposta = (CustomTransferObject) respIt.next();
              %>
                         <p class="font-weight-bold"><%=ApplicationResourcesHelper.getMessage("mensagem.comunicacao.resposta", responsavel, (String) TextHelper.forHtmlContent(resposta.getAttribute(Columns.USU_NOME)), DateHelper.toDateTimeString((Date) resposta.getAttribute(Columns.CMN_DATA)))%></p>
                         <p><%=TextHelper.forHtmlContent(resposta.getAttribute(Columns.CMN_TEXTO) )%></p>
              <%  } %>
      </div>
    </div>
    <%
        }
    %>
    
    <%if(anexos != null && !anexos.isEmpty()) {%>
      <div class="card">
            <%-- Utiliza a tag library ListaAnexosComunicacaoTag.java para listar os anexos da comunicacao --%>
            <% pageContext.setAttribute("anexos", anexos); %>
            <hl:listaAnexosComunicacaov4 name="anexos" table="true" />
            <%-- Fim dos anexos da comunicacao --%>
      </div>
  <%
    }
    if (podeEditarCmn) {
  %> 
  
  <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="mensagem.comunicacao.nova.resposta"/></h2>
        </div>
        <div class="card-body">
          <form method="post" action="../v3/enviarComunicacao?acao=editar&operacao=upload&cmn_codigo=<%=TextHelper.forHtmlAttribute(cmnCodigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1" ENCTYPE="multipart/form-data">
          <input type="HIDDEN" name="cmn_codigo" value="<%=TextHelper.forHtmlAttribute(request.getParameter("cmn_codigo"))%>">
          <input name="FORM" type="hidden" value="form1">
          <div class="row">
              <div class="form-group col-sm-6">
                <label for="texto"><hl:message key="rotulo.comunicacao.texto"/></label>
                <textarea class="form-control" name="mensagem" rows="6" onFocus="SetarEventoMascara(this,'#*65000',true);" onBlur="fout(this);ValidaMascara(this);"><%=JspHelper.verificaVarQryStr(request, uploadHelper, "mensagem") == null || sucesso ? "" : JspHelper.verificaVarQryStr(request, uploadHelper, "mensagem")%></textarea>
              </div>
            </div>
            <%if(podeIncluirAnexo){ %>
              <div class="row">
                <div class="form-group col-sm-6">
                  <label for="iArquivo"><hl:message key="rotulo.comunicacao.anexo.arquivo"/></label>
                  <input type="file" class="form-control" type="FILE" name="FILE1" id="FILE1">
                </div>
              </div>
            <%} %>
          </form> 
        </div>
      </div>
    <div class="btn-action">
      <a class="btn btn-outline-danger" href="#" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
      <a class="btn btn-primary" href="#" onClick="enviar(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
    </div>
  <%} else { %>
    <div class="btn-action">
      <a class="btn btn-outline-danger" href="#" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
    </div>
  <%}  %>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
<%
  if (podeEditarCmn) {
 %> 
    var f0 = document.forms[0];
    function enviar() {
       var msg = trim(f0.mensagem.value);
       
       // Verifica quantidade de caracteres informados na mensagem
       if (msg.length < 10) {
         alert('<hl:message key="mensagem.erro.comunicacao.texto.minimo"/>');
       f0.mensagem.focus();
         return;
       }

       // Verifica se existe pelo menos uma letra na mensagem
       var regex = /([a-zA-Z]+)/g;
       if (!msg.match(regex)) {
         alert('<hl:message key="mensagem.erro.comunicacao.texto.invalido"/>');
       f0.mensagem.focus();
         return;
       }
       
       var Controles = new Array("mensagem");
       var Msgs = new Array('<hl:message key="mensagem.informe.comunicacao.texto.resposta"/>');
       
       if (ValidaCampos(Controles, Msgs)) {
           f0.submit();
       }          
    }
    
    function excluir_anexo(acm_nome){    
      var url = '../v3/editarComunicacao?acao=editar&operacao=excluir&NOME_ARQ='+ acm_nome + '&CMN_CODIGO=<%=TextHelper.forJavaScriptBlock(cmnCodigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>';
      var msgAnexo = '<hl:message key="mensagem.confirmacao.exclusao.anexo.comunicacao"/>';
      return ConfirmaUrl(msgAnexo.replace("{0}", acm_nome), url);
    }
 <%} %>
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
