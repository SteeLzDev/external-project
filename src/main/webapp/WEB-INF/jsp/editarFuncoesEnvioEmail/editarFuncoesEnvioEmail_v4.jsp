<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, java.util.stream.*"%>
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
List<TransferObject> servicos = (List<TransferObject>) request.getAttribute("servicos");
Map<String, List<String>> mapaServicosEnvioEmailCsa = (Map<String, List<String>>) request.getAttribute("mapaServicosEnvioEmailCsa");
String email = (String) request.getAttribute("email");
String nome = (String) request.getAttribute("nome");
String csaCodigo = (String) request.getAttribute("csaCodigo");
String cseCodigo = (String) request.getAttribute("cseCodigo");
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
                        <%= TextHelper.forHtmlContent(nome) %>
                    </h2>
                </div>
                <div class="card-body">
                    <form action="../v3/editarFuncoesEnvioEmail?acao=salvar&<%=SynchronizerToken.generateToken4URL(request)%>" method="post" name="form1">
                        <%
                        if (funcoes != null && !funcoes.isEmpty() && (responsavel.isCsa() || responsavel.isCseSup()) && !TextHelper.isNull(csaCodigo)) {
                            Iterator<TransferObject> itFuncao = funcoes.iterator();
                              while (itFuncao.hasNext()) {
                                CustomTransferObject funcao = (CustomTransferObject) itFuncao.next();
                                String funCodigo = TextHelper.forHtmlAttribute(funcao.getAttribute(Columns.FUN_CODIGO));
                                String funDescricao = TextHelper.forHtmlContent(funcao.getAttribute(Columns.FUN_DESCRICAO));
                                String papCodigo = TextHelper.forHtmlAttribute(funcao.getAttribute(Columns.PAP_CODIGO));
                                String papDescricao = TextHelper.forHtmlContent(funcao.getAttribute(Columns.PAP_DESCRICAO));
                                String demEmail = TextHelper.forHtmlAttribute(!TextHelper.isNull(funcao.getAttribute(Columns.DEM_EMAIL)) ? funcao.getAttribute(Columns.DEM_EMAIL) : email);
                                boolean emailAlterado = !demEmail.equalsIgnoreCase(email);
                                boolean receber = !"N".equalsIgnoreCase((String) funcao.getAttribute(Columns.DEM_RECEBER));
                                String chaveCampo = funCodigo + "_" + papCodigo;
                        %>
                        <div class="row">
                          <div class="col-sm-12 col-md-12">
                            <h3 class="legend">
                              <span><hl:message key="rotulo.editar.funcoes.envio.email.funcao.papel" arg0="<%=funDescricao%>" arg1="<%=papDescricao%>"/></span>
                            </h3>
							<div class="form-group">
							  <div class="row">
							    <!-- Primeira coluna -->
							    <div class="col-sm-6 col-md-6">
							      <div class="row">
							        <div class="col-sm-6 col-md-6" role="radiogroup" aria-labelledby="rotulo1">
							          <span id="rotulo1"><hl:message key="rotulo.editar.funcoes.envio.email.receber"/></span>
							          <br>
							          <div class="form-check form-check-inline">
							            <hl:htmlinput classe="form-check-input ml-1" type="radio" name="<%="receber_" + chaveCampo%>" di="<%="receberSim_" + chaveCampo%>" value="S" onClick="<%="hablitarCampos(1, '" + chaveCampo + "')"%>" others="<%= (receber ? " checked " : "") + (readOnly ? " disabled " : "") %>" />
							            <label class="form-check-label labelSemNegrito" for="<%="receberSim_" + chaveCampo%>"><hl:message key="rotulo.sim"/></label>
							          </div>
							          <div class="form-check form-check-inline">
							            <hl:htmlinput classe="form-check-input ml-1" type="radio" name="<%="receber_" + chaveCampo%>" di="<%="receberNao_" + chaveCampo%>" value="N" onClick="<%="hablitarCampos(2, '" + chaveCampo + "')"%>" others="<%= (!receber ? " checked " : "") + (readOnly ? " disabled " : "") %>" />
							            <label class="form-check-label labelSemNegrito" for="<%="receberNao_" + chaveCampo%>"><hl:message key="rotulo.nao"/></label>
							          </div>
							        </div>
							        <div class="col-sm-6 col-md-6" role="radiogroup" aria-labelledby="rotulo2">
							          <span id="rotulo2"><hl:message key="rotulo.editar.funcoes.envio.email.alterar"/></span>
							          <br>
							          <div class="form-check form-check-inline">
							            <hl:htmlinput classe="form-check-input ml-1" type="radio" name="<%="alterar_" + chaveCampo%>" di="<%="alterarSim_" + chaveCampo%>" value="S" onClick="<%="hablitarCampos(3, '" + chaveCampo + "')"%>" others="<%= (emailAlterado ? " checked " : "") + (!receber || readOnly ? " disabled " : "") %>" />
							            <label class="form-check-label labelSemNegrito" for="<%="alterarSim_" + chaveCampo%>"><hl:message key="rotulo.sim"/></label>
							          </div>
							          <div class="form-check form-check-inline">
							            <hl:htmlinput classe="form-check-input ml-1" type="radio" name="<%="alterar_" + chaveCampo%>" di="<%="alterarNao_" + chaveCampo%>" value="N" onClick="<%="hablitarCampos(4, '" + chaveCampo + "')"%>" others="<%= (!emailAlterado ? " checked " : "") + (!receber || readOnly ? " disabled " : "") %>" />
							            <label class="form-check-label labelSemNegrito" for="<%="alterarNao_" + chaveCampo%>"><hl:message key="rotulo.nao"/></label>
							          </div>
							        </div>
							      </div>
                    <div class="row">
                      <div class="col-sm-12 col-md-12">
                        <label for="<%="email_" + chaveCampo%>"><hl:message key="rotulo.editar.funcoes.envio.email.endereco"/></label>
                            <hl:htmlinput type="text" classe="form-control" name="<%="email_" + chaveCampo%>" di="<%="email_" + chaveCampo%>" value="<%=demEmail%>" placeHolder="<%= ApplicationResourcesHelper.getMessage("mensagem.editar.funcoes.envio.email.placeholder", responsavel) %>" others="<%= !emailAlterado || readOnly ? " disabled " : "" %>"/>
                      </div>								      
                    </div>
							    </div>

							    <!-- Segunda coluna -->
                  <div class="col-sm-6 col-md-6">
                    <div class="row">
                      <label for="<%="svc_" + chaveCampo%>"><hl:message key="rotulo.servico.aplicar.configuracao"/></label>
                      <select class="form-control form-select col-sm-12 m-1" name="<%="svc_" + chaveCampo%>" multiple id="<%="svc_" + chaveCampo%>">
                          <%
                            Iterator<?> itSvc = servicos.iterator();
                            while (itSvc.hasNext()) {
                              CustomTransferObject ctoServico = (CustomTransferObject) itSvc.next();
                              String svcCodigo = ctoServico.getAttribute(Columns.SVC_CODIGO).toString();
                              String svcIdentificador = ctoServico.getAttribute(Columns.SVC_IDENTIFICADOR).toString();
                              String svcDescricao = ctoServico.getAttribute(Columns.SVC_DESCRICAO).toString();
                              boolean isSelected = (mapaServicosEnvioEmailCsa.get(chaveCampo) != null && mapaServicosEnvioEmailCsa.get(chaveCampo).contains(svcCodigo));
                          %>
                            <option VALUE="<%=TextHelper.forHtmlAttribute(svcCodigo)%>" <%=isSelected ? "SELECTED" : ""%>><%=TextHelper.forHtmlContent(svcDescricao)%>
                            </option>
                          <% } %>
                      </select>
                      <div class="slider col-sm-8 col-md-12 pl-0 pr-0 mb-2">
                        <div class="tooltip-inner"><hl:message key="mensagem.utilize.crtl"/></div>
                      </div>
                      <div class="btn-action float-end mt-3">
                        <a class="btn btn-outline-danger" onClick="limparCombo(document.getElementById('svc_<%=chaveCampo%>'))" href="javascript:void(0);"><hl:message key="mensagem.limpar.selecao"/></a>
                      </div>
                    </div>
                  </div>								
							  </div>
							</div>
                            <% } %>
                            <input type="hidden" name="CSA_CODIGO" value="<%=TextHelper.forHtmlAttribute(csaCodigo)%>">
                        <% } %>
                      
                      
                      
                      <%
                        if (funcoes != null && !funcoes.isEmpty() && responsavel.isCseSup() && !TextHelper.isNull(cseCodigo) && TextHelper.isNull(csaCodigo)) {
                            Iterator<TransferObject> itFuncao = funcoes.iterator();
                              while (itFuncao.hasNext()) {
                                CustomTransferObject funcao = (CustomTransferObject) itFuncao.next();
                                String funCodigo = TextHelper.forHtmlAttribute(funcao.getAttribute(Columns.FUN_CODIGO));
                                String funDescricao = TextHelper.forHtmlContent(funcao.getAttribute(Columns.FUN_DESCRICAO));
                                String papCodigo = TextHelper.forHtmlAttribute(funcao.getAttribute(Columns.PAP_CODIGO));
                                String papDescricao = TextHelper.forHtmlContent(funcao.getAttribute(Columns.PAP_DESCRICAO));
                                String deeEmail = TextHelper.forHtmlAttribute(!TextHelper.isNull(funcao.getAttribute(Columns.DEE_EMAIL)) ? funcao.getAttribute(Columns.DEE_EMAIL) : email);
                                boolean emailAlterado = !deeEmail.equalsIgnoreCase(email);
                                boolean receber = !"N".equalsIgnoreCase((String) funcao.getAttribute(Columns.DEE_RECEBER));
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
                                    <hl:htmlinput classe="form-check-input ml-1" type="radio" name="<%="receber_" + chaveCampo%>" di="<%="receberSim_" + chaveCampo%>" value="S" onClick="<%="hablitarCampos(1, '" + chaveCampo + "')"%>" others="<%= (receber ? " checked " : "") + (readOnly ? " disabled " : "") %>" />
                                    <label class="form-check-label labelSemNegrito" for="<%="receberSim_" + chaveCampo%>"><hl:message key="rotulo.sim"/></label>
                                  </div>
                                  <div class="form-check form-check-inline">
                                    <hl:htmlinput classe="form-check-input ml-1" type="radio" name="<%="receber_" + chaveCampo%>" di="<%="receberNao_" + chaveCampo%>" value="N" onClick="<%="hablitarCampos(2, '" + chaveCampo + "')"%>" others="<%= (!receber ? " checked " : "") + (readOnly ? " disabled " : "") %>" />
                                    <label class="form-check-label labelSemNegrito" for="<%="receberNao_" + chaveCampo%>"><hl:message key="rotulo.nao"/></label>
                                  </div>
                                </div>
                                <div class="col-sm-6 col-md-2" role="radiogroup" aria-labelledby="rotulo2">
                                  <span id="rotulo2"><hl:message key="rotulo.editar.funcoes.envio.email.alterar"/></span>
                                  <br>
                                  <div class="form-check form-check-inline">
                                    <hl:htmlinput classe="form-check-input ml-1" type="radio" name="<%="alterar_" + chaveCampo%>" di="<%="alterarSim_" + chaveCampo%>" value="S" onClick="<%="hablitarCampos(3, '" + chaveCampo + "')"%>" others="<%= (emailAlterado ? " checked " : "") + (!receber || readOnly ? " disabled " : "") %>" />
                                    <label class="form-check-label labelSemNegrito" for="<%="alterarSim_" + chaveCampo%>"><hl:message key="rotulo.sim"/></label>
                                  </div>
                                  <div class="form-check form-check-inline">
                                    <hl:htmlinput classe="form-check-input ml-1" type="radio" name="<%="alterar_" + chaveCampo%>" di="<%="alterarNao_" + chaveCampo%>" value="N" onClick="<%="hablitarCampos(4, '" + chaveCampo + "')"%>" others="<%= (!emailAlterado ? " checked " : "") + (!receber || readOnly ? " disabled " : "") %>" />
                                    <label class="form-check-label labelSemNegrito" for="<%="alterarNao_" + chaveCampo%>"><hl:message key="rotulo.nao"/></label>
                                  </div>
                                </div>
                                <div class="col-sm-12 col-md-8">
                                  <label for="<%="email_" + chaveCampo%>"><hl:message key="rotulo.editar.funcoes.envio.email.endereco"/></label>
                                  <hl:htmlinput type="text" classe="form-control" name="<%="email_" + chaveCampo%>" di="<%="email_" + chaveCampo%>" value="<%=deeEmail%>" placeHolder="<%= ApplicationResourcesHelper.getMessage("mensagem.editar.funcoes.envio.email.placeholder", responsavel) %>" others="<%= !emailAlterado || readOnly ? " disabled " : "" %>"/>
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                            <% } %>
                           <input type="hidden" name="CSE_CODIGO" value="<%=TextHelper.forHtmlAttribute(cseCodigo)%>">
                        <% } %> 
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
        var emailGeral = '<%= TextHelper.forJavaScriptBlock(email) %>';

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
    
        function hablitarCampos(tipo, funcao) {
            switch (tipo) {
              case 1:
                  $("#alterarSim_" + funcao).prop("disabled", false);
                  $("#alterarNao_" + funcao).prop("disabled", false);
                  if ($("#alterarSim_" + funcao)[0].checked) {
                      $("#email_" + funcao).prop("disabled", false);
                  } else {
                      $("#email_" + funcao).prop("disabled", true);
                      $("#email_" + funcao).val(emailGeral);
                  }
                  break; 
              case 2:
                  $("#alterarSim_" + funcao).prop("disabled", true).prop("checked", false);
                  $("#alterarNao_" + funcao).prop("disabled", true).prop("checked", true);
                  $("#email_" + funcao).prop("disabled", true);
                  $("#email_" + funcao).val(emailGeral);
                  break;
              case 3:
                  $("#email_" + funcao).prop("disabled", false);
                  break;
              default:
                  $("#email_" + funcao).prop("disabled", true);
                  $("#email_" + funcao).val(emailGeral);
                  break;
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