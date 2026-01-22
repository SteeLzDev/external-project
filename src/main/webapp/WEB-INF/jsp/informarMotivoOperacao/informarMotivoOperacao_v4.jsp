<%--
* <p>Title: informarMotivoOperacao</p>
* <p>Description: Permite informar o motivo nas operações de consignação</p>
* <p>Copyright: Copyright (c) 2005</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="java.util.*"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String urlDestino = (String) request.getAttribute("urlDestino");
String tituloPagina = (String) request.getAttribute("tituloPagina");
String msgConfirmacao = (String) request.getAttribute("msgConfirmacao");

boolean deferirTodos = TextHelper.isNull(request.getAttribute("deferirTodos")) ? false : (boolean) request.getAttribute("deferirTodos");
boolean indeferirTodos = TextHelper.isNull(request.getAttribute("indeferirTodos")) ? false : (boolean) request.getAttribute("indeferirTodos");
boolean operacaoPermiteSelecionarPeriodo = TextHelper.isNull(request.getAttribute("operacaoPermiteSelecionarPeriodo")) ? false : (boolean) request.getAttribute("operacaoPermiteSelecionarPeriodo");
boolean exigeSenhaServidor = TextHelper.isNull(request.getAttribute("exigeSenhaServidor")) ? false : (boolean) request.getAttribute("exigeSenhaServidor");
Set<Date> periodos = (Set<Date>) request.getAttribute("periodos");

String[] adeCodigos = (String[]) request.getAttribute("adeCodigos");
String nomeCampo = (String) request.getAttribute("nomeCampo");
String[] nomeCampos = (String[]) request.getAttribute("nomeCampos");
String[] adesDeferir = (String[]) request.getAttribute("adesDeferir");
String[] chkDeferir = request.getParameterValues("chkDeferir");
String[] chkIndeferir = request.getParameterValues("chkIndeferir");

List autdesList = (List) request.getAttribute("autdesList");

String strAdeCodigo = (String) request.getAttribute("strAdeCodigo");
String strRseMatricula = (String) request.getAttribute("strRseMatricula");

String rseCodigoOri = (String) request.getParameter("RSE_CODIGO_ORI");
String rseCodigoDes = (String) request.getParameter("RSE_CODIGO_DES");
%>
<c:set var="title">
${tituloPagina}
</c:set>
<c:set var="imageHeader">
<use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
      <form method="post" action="<%=TextHelper.forHtmlAttribute(urlDestino)%>" name="formTmo">
        <div class="row">
          <% if (deferirTodos) { %>
            <div>
              <dl>
                  <dt><img src="../img/icones/warning_big.gif" border="0"></dt>
                  <dd><font class="aviso"><%=ApplicationResourcesHelper.getMessage("mensagem.informacao.deferir.qtde.contratos", responsavel, String.valueOf(adesDeferir.length))%>&nbsp;</font></dd>
             </dl>
            </div>
          <% } else if (indeferirTodos) { %>
            <div>
              <dl>
                  <dt><img src="../img/icones/warning_big.gif" border="0"></dt>
                    <dd><font class="aviso"><%=ApplicationResourcesHelper.getMessage("mensagem.informacao.indeferir.qtde.contratos", responsavel, String.valueOf(adesDeferir.length))%>&nbsp;</font></dd>
              </dl>
            </div>
          <% } %>
        <div class="col-sm-7">
            <% if (!deferirTodos && !indeferirTodos) {  %>
               <%-- Utiliza a tag library DetalheConsignacaoTag.java para exibir os dados da ADE --%>
               <% pageContext.setAttribute("autdes", autdesList); %>       
               <hl:detalharADEv4 name="autdes" table="false" type="alterar"/>
               <%-- Fim dos dados da ADE --%>
               <% } %>
        </div>
          <div class="col-sm-5">
              <div class="card">
                  <div class="card-header">
                    <h2 class="card-header-title"><hl:message key="rotulo.efetiva.acao.consignacao.dados.operacao"/></h2>
                  </div>
                    <div class="card-body">
                      <% if (operacaoPermiteSelecionarPeriodo && (periodos != null && !periodos.isEmpty())) { %>
                         <dl>
                            <dt class="col-sm-12 px-1">
                                <label for="OCA_PERIODO">
                                  <hl:message key="rotulo.folha.periodo"/>
                                </label>
                            </dt>
                             <dd class="col-sm-12 px-1">
                                <select name="OCA_PERIODO" 
                                        id="OCA_PERIODO" 
                                        class="form-control" 
                                        onFocus="SetarEventoMascara(this,'#*200',true);" 
                                        onBlur="fout(this);ValidaMascara(this);">
                                  <% for (Date periodo : periodos) { %>
                                    <option value="<%=TextHelper.forHtmlAttribute(periodo)%>">
                                      <%=TextHelper.forHtmlContent(DateHelper.toPeriodString(periodo))%>
                                    </option>
                                  <% } %>
                                </select>
                             </dd>
                         </dl>
                      <% } %>

                      <%-- Utiliza a tag library EfetivaAcaoMotivoOperacaoTag.java para exibir os dados do Tipo de Motivo da Operação --%>
                      <hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="<%=TextHelper.forHtmlAttribute(msgConfirmacao)%>" inputSizeCSS="col-sm-12"/>
                      <%-- Fim dos dados do Motivo da Operação --%>
                      
                      <% if (exigeSenhaServidor) { %>
                        <dl>
                          <hl:senhaServidorv4 senhaObrigatoria="true"                                                 
                                              senhaParaAutorizacaoReserva="true"
                                              nomeCampoSenhaCriptografada="serAutorizacao"
                                              rseCodigo="<%=request.getAttribute("rseCodigo") != null ? request.getAttribute("rseCodigo").toString() : ""%>"
                                              svcCodigo="<%=request.getAttribute("svcCodigo") != null ? request.getAttribute("svcCodigo").toString() : ""%>"
                                              nf="btnEnvia"
                                              classe="form-control"
                                              inputSizeCSS="col-sm-12 px-1"
                                              separador2pontos="false"
                              />
                        </dl>
                      <% } %>

                    </div>
              </div>
              <div class="btn-action">
                <a class="btn btn-outline-danger" href="#no-back"  onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.voltar"/></a>
                <a class="btn btn-primary" id="btnEnvia" href="#" onClick="concluir(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
                <input type="hidden" name="MM_update" value="formTmo">
                <% if (!TextHelper.isNull(rseCodigoOri) && !TextHelper.isNull(rseCodigoDes)) { %>
			    <input type="hidden" name="RSE_CODIGO_ORI" value="<%=TextHelper.forHtmlAttribute(rseCodigoOri)%>">
			    <input type="hidden" name="RSE_CODIGO_DES" value="<%=TextHelper.forHtmlAttribute(rseCodigoDes)%>">
			    <% } %>
                <input type="hidden" name="RSE_MATRICULA" value="<%=TextHelper.forHtmlAttribute(strRseMatricula)%>">
                <input type="hidden" name="deferirTodos" value="<%=TextHelper.forHtmlAttribute(deferirTodos)%>">
                <input type="hidden" name="indeferirTodos" value="<%=TextHelper.forHtmlAttribute(indeferirTodos)%>">
                <%if(!TextHelper.isNull(strAdeCodigo)) {%>
                  <input type="hidden" name="ADE_CODIGO" value="<%=TextHelper.forHtmlAttribute(strAdeCodigo)%>">
                <%} %>
                <% if (nomeCampos != null) {
                    for (int i = 0; i < nomeCampos.length; i++) {
                        String[] ades = request.getParameterValues(nomeCampos[i]); 
                        if (ades != null) {
                            for (int j = 0; j < ades.length; j++) { %>
                              <input type="hidden" name="<%=TextHelper.forHtmlAttribute(nomeCampos[i])%>" value="<%=TextHelper.forHtmlAttribute(ades[j])%>">
                        <%  }
                        } 
                    }    
                } else { %>
                <% if (adeCodigos != null) { 
                    for (int i = 0; i < adeCodigos.length; i++) { %>
                      <input type="hidden" name="<%=TextHelper.forHtmlAttribute(nomeCampo)%>" value="<%=TextHelper.forHtmlAttribute(adeCodigos[i])%>">
                <%  }    
                  } 
                }%>
              <% if (adesDeferir != null) {
                 	for (int i = 0; i < adesDeferir.length; i++) {%>
                         <input type="hidden" name="adesDeferir" value="<%=TextHelper.forHtmlAttribute(adesDeferir[i])%>">
              <%    }
                 }
              %>
              
              <% if (chkDeferir != null) {
                 	for (int i = 0; i < chkDeferir.length; i++) {%>
                         <input type="hidden" name="chkDeferir" value="<%=TextHelper.forHtmlAttribute(chkDeferir[i])%>">
              <%    }
                 }
              %>
              
              <% if (chkIndeferir != null) {
                 	for (int i = 0; i < chkIndeferir.length; i++) {%>
                         <input type="hidden" name="chkIndeferir" value="<%=TextHelper.forHtmlAttribute(chkIndeferir[i])%>">
              <%    }
                 }
              %>
             </div>
          </div>
       </div>
   </form>
</c:set>
<c:set var="javascript">
<hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="<%=TextHelper.forHtmlAttribute(msgConfirmacao)%>" scriptOnly="true" />
<% if (exigeSenhaServidor) { %>
  <hl:senhaServidorv4 senhaObrigatoria="true"                                                 
                      senhaParaAutorizacaoReserva="true"
                      nomeCampoSenhaCriptografada="serAutorizacao"
                      rseCodigo="<%=request.getAttribute("rseCodigo") != null ? request.getAttribute("rseCodigo").toString() : ""%>"
                      svcCodigo="<%=request.getAttribute("svcCodigo") != null ? request.getAttribute("svcCodigo").toString() : ""%>"
                      scriptOnly="true"
      />
<% } %>
<script language="JavaScript" type="text/JavaScript">
var f0 = document.forms[0];

function concluir() {
  if (confirmaAcaoConsignacao()) {
    if (f0.senha != null && trim(f0.senha.value) != '') {
      CriptografaSenha(f0.senha, f0.serAutorizacao, false);
    }
    f0.submit();
  }
}
</script>
</c:set> 
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
