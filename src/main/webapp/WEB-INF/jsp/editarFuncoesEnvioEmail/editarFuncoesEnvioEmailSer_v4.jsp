<%--
* <p>Title: editarFuncoesEnvioEmailSer_v4.jsp</p>
* <p>Description: Página de edição de funções para envio de e-mail do servidor</p>
* <p>Copyright: Copyright (c) 2024</p>
* <p>Company: ZetraSoft Internet Service</p>
* @author Alexandre Fernandes
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

List<TransferObject> funcoes = (List<TransferObject>) request.getAttribute("funcoes");
String serNome = (String) request.getAttribute("serNome");
String serEmail = (String) request.getAttribute("serEmail");

boolean readOnly = (boolean) request.getAttribute("readOnly");
%>

<c:set var="title">
    <%= TextHelper.forHtmlContent(request.getAttribute("tituloPagina")) %>
</c:set>

<c:set var="imageHeader">
    <use xlink:href="#i-manutencao"></use>
</c:set>

<c:set var="bodyContent">
  <% if (!readOnly) { %>
    <div class="row">
        <div class="col-sm mb-2">
            <div class="float-end">
                  <button data-bs-toggle="dropdown" aria-haspopup="true" id="acoes" aria-expanded="false" class="btn btn-primary" type="submit"><hl:message key="rotulo.acoes"/></button>
                  <div class="dropdown-menu dropdown-menu-right" aria-labelledby="acoes">
                  <% if (funcoes != null && !funcoes.isEmpty()) { %>
                    <a class="dropdown-item" href="#no-back" id="reativar" onClick="checkAll()"><hl:message key="rotulo.acoes.habilitar.todos"/></a>
                    <a class="dropdown-item" href="#no-back" id="reativar" onClick="uncheckAll()"><hl:message key="rotulo.acoes.desabilitar.todos"/></a>
                  <% } %>
                  </div>
            </div>
        </div>
    </div>
  <% } %>
    <div class="row justify-content-md-center">
        <div class="col-sm-12 form-check mt-2 form-group">
            <div class="card">
                <div class="card-header">
                    <h2 class="card-header-title">
                        <%= TextHelper.forHtmlContent(request.getAttribute("serNome")) %>
                    </h2>
                </div>
                <div class="card-body">
                    <form action="../v3/editarFuncoesEnvioEmailSer?acao=salvar&<%=SynchronizerToken.generateToken4URL(request)%>" method="post" name="form1">
                        <input type="hidden" name="SER_CODIGO" value="<%=TextHelper.forHtmlAttribute(request.getAttribute("serCodigo"))%>">
                    
                        <%
                        if (funcoes != null && !funcoes.isEmpty()) {
                            Iterator<TransferObject> itFuncao = funcoes.iterator();
                              while (itFuncao.hasNext()) {
                                CustomTransferObject funcao = (CustomTransferObject) itFuncao.next();
                                String funCodigo = TextHelper.forHtmlAttribute(funcao.getAttribute(Columns.FUN_CODIGO));
                                String funDescricao = TextHelper.forHtmlContent(funcao.getAttribute(Columns.FUN_DESCRICAO));
                                String papCodigo = TextHelper.forHtmlAttribute(funcao.getAttribute(Columns.PAP_CODIGO));
                                String papDescricao = TextHelper.forHtmlContent(funcao.getAttribute(Columns.PAP_DESCRICAO));
                                boolean receber = !"N".equalsIgnoreCase((String) funcao.getAttribute(Columns.DES_RECEBER));
                                String chaveCampo = funCodigo + "_" + papCodigo;
                        %>
                        <div class="row">
                          <div class="col-sm-12 col-md-12">
                            <h3 class="legend">
                              <span><hl:message key="rotulo.editar.funcoes.envio.email.funcao.papel" arg0="<%=funDescricao%>" arg1="<%=papDescricao%>"/></span>
                            </h3>
                            <div class="form-group">
                              <div class="row">
                                <div class="col-sm-6 col-md-2" role="radiogroup" aria-labelledby="rotulo1">
                                  <span id="rotulo1"><hl:message key="rotulo.editar.funcoes.envio.email.receber"/></span>
                                  <br>
                                  <div class="form-check form-check-inline">
                                    <hl:htmlinput classe="form-check-input ml-1" type="radio" name="<%="receber_" + chaveCampo%>" di="<%="receberSim_" + chaveCampo%>" value="S" others="<%= (receber ? " checked " : "") + (readOnly ? " disabled " : "") %>" />
                                    <label class="form-check-label labelSemNegrito" for="<%="receberSim_" + chaveCampo%>"><hl:message key="rotulo.sim"/></label>
                                  </div>
                                  <div class="form-check form-check-inline">
                                    <hl:htmlinput classe="form-check-input ml-1" type="radio" name="<%="receber_" + chaveCampo%>" di="<%="receberNao_" + chaveCampo%>" value="N" others="<%= (!receber ? " checked " : "") + (readOnly ? " disabled " : "") %>" />
                                    <label class="form-check-label labelSemNegrito" for="<%="receberNao_" + chaveCampo%>"><hl:message key="rotulo.nao"/></label>
                                  </div>
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                            <%     
                            } 
                      }    %>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <div class="btn-action">
    <% if (!readOnly) { %>
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
        <a class="btn btn-primary" href="#no-back" onClick="salvar(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
    <% } else { %>
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
    <% } %>
    </div>
</c:set>

<c:set var="javascript">
    <script type="text/JavaScript" src="../js/scripts_2810.js"></script>
    <script type="text/JavaScript" src="../js/validaform.js"></script>
    <script type="text/JavaScript" src="../js/validacoes.js"></script>
    <script type="text/JavaScript">
        var f0 = document.forms[0];

        function checkAll() {
        	$('[id*="receberSim_"]').click();
        }
    
        function uncheckAll() {
            $('[id*="receberNao_"]').click();
        }
    
        function salvar() {
        	var enviar = true;
        	$('[id*="email_"]').each(function() {
        	    if (!this.disabled && !this.value && enviar) {
        	    	alert('<%= ApplicationResourcesHelper.getMessage("mensagem.erro.editar.funcoes.envio.email.vazio", responsavel) %>');
        	    	enviar = false;
        	    }
        	});
        	if (enviar) {
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