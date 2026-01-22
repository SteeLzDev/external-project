<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken"%>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper"%>
<%@ page import="com.zetra.econsig.values.CodedValues"%>
<%@ page import="com.zetra.econsig.values.Columns"%>
<%@ page import="com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist"%>
<%@ page import="com.zetra.econsig.helper.web.v3.JspHelper"%>
<%@ page import="java.util.List"%>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

// Exige tipo de motivo da operacao
boolean exigeMotivoOperacaoUsu = ParamSist.paramEquals(CodedValues.TPC_EXIGE_TIPO_MOTIVO_OPERACAO_USUARIO, CodedValues.TPC_SIM, responsavel);

// Se usa a segunda senha de autorização (desde que não sejam senhas múltiplas)
// permite a alteração da senha por usuários gestores
boolean usaSenhaAutorizacao = (ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, CodedValues.TPC_SIM, responsavel) && !ParamSist.paramEquals(CodedValues.TPC_USA_MULTIPLAS_SENHAS_AUTORIZACAO_SERVIDOR, CodedValues.TPC_SIM, responsavel));

boolean usaMultiplaSenhaAutorizacao = (ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, CodedValues.TPC_SIM, responsavel) && ParamSist.paramEquals(CodedValues.TPC_USA_MULTIPLAS_SENHAS_AUTORIZACAO_SERVIDOR, CodedValues.TPC_SIM, responsavel));

List<TransferObject> usuarioSerList = (List<TransferObject>) request.getAttribute("usuarioSerList");
%>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="title">
   <%= request.getAttribute("tituloPagina") %>
</c:set>
<c:set var="bodyContent">
    <div class="row">
      <div class="col-sm-7 col-md">
        <div class="card">
          <div class="card-header hasIcon pl-3">
            <h2 class="card-header-title"><hl:message key="rotulo.listar.usuarios.servidor.titulo"/></h2>
          </div>
          <div class="card-body table-responsive">
            <table class="table table-striped table-hover">
              <thead>
                <tr>
                  <th scope="col"><hl:message key="rotulo.listar.usuarios.servidor.login"/></th>
                  <th scope="col"><hl:message key="rotulo.listar.usuarios.servidor.nome"/></th>
                  <th scope="col"><hl:message key="rotulo.listar.usuarios.servidor.cpf"/></th>
                  <th scope="col"><hl:message key="rotulo.servidor.matricula"/></th>
                  <th scope="col"><hl:message key="rotulo.listar.usuarios.servidor.orgao"/></th>
                  <th scope="col"><hl:message key="rotulo.listar.usuarios.servidor.estabelecimento"/></th>
                  <th scope="col"><hl:message key="rotulo.acoes.status"/></th>
                  <th scope="col"><hl:message key="rotulo.acoes"/></th>
                </tr>
              </thead>
              <tbody>
              <%
                // Caso nao exista usuario para o registro do servidor...
                if (usuarioSerList == null || usuarioSerList.isEmpty()) { 
              %>
               <tr>
                  <td colspan="100%"><%=ApplicationResourcesHelper.getMessage("mensagem.erro.nenhum.registro.encontrado", responsavel)%></td>
               </tr>
              <% // No caso de existir usuario para o registro do servidor, os detalhes deste serao mostrados na tela
                } else {
                    for (TransferObject usuarioSer : usuarioSerList) {

                      String serNome = usuarioSer.getAttribute(Columns.SER_NOME).toString();                      
                      String stuCodigo = usuarioSer.getAttribute(Columns.USU_STU_CODIGO) != null ? usuarioSer.getAttribute(Columns.USU_STU_CODIGO).toString() : CodedValues.STU_ATIVO;
                      String serCpf = usuarioSer.getAttribute(Columns.SER_CPF).toString();                  
                      String orgNome = usuarioSer.getAttribute(Columns.ORG_NOME).toString();
                      String usuLogin = usuarioSer.getAttribute(Columns.USU_LOGIN) != null ? usuarioSer.getAttribute(Columns.USU_LOGIN).toString() : ""; 
                      String usuCodigo = usuarioSer.getAttribute(Columns.USU_CODIGO) != null ? usuarioSer.getAttribute(Columns.USU_CODIGO).toString() : "";
                      String orgIdentificador = usuarioSer.getAttribute(Columns.ORG_IDENTIFICADOR).toString();
                      String estIdentificador = usuarioSer.getAttribute(Columns.EST_IDENTIFICADOR).toString();
                      String orgCodigo = usuarioSer.getAttribute(Columns.ORG_CODIGO).toString();
                      String rseCodigo = usuarioSer.getAttribute(Columns.RSE_CODIGO).toString();
                      String matricula = usuarioSer.getAttribute(Columns.RSE_MATRICULA).toString();
                      String serCodigo = (String) usuarioSer.getAttribute(Columns.SER_CODIGO);
                      String usuLoginCons = usuLogin;
                      String usuLoginClass = "";
                      String stuCodigoCons = "";
                      String stuCodigoClass = "";

                      boolean usuarioExiste = !TextHelper.isNull(usuCodigo);
                      if (usuarioExiste && stuCodigo.equals(CodedValues.STU_ATIVO)) {
                          stuCodigoCons = ApplicationResourcesHelper.getMessage("rotulo.listar.usuarios.servidor.desbloqueado", responsavel);
                      } else if (CodedValues.STU_CODIGOS_INATIVOS.contains(stuCodigo)) {
                          stuCodigoClass = "block";
                          stuCodigoCons = ApplicationResourcesHelper.getMessage("rotulo.listar.usuarios.servidor.bloqueado", responsavel);
                      } else {
                          usuLoginClass = "block";
                          usuLoginCons = ApplicationResourcesHelper.getMessage("rotulo.listar.usuarios.servidor.inexistente", responsavel);
                          stuCodigoClass = "block";
                          stuCodigoCons = ApplicationResourcesHelper.getMessage("rotulo.listar.usuarios.servidor.inexistente", responsavel);
                      }
                      
                      boolean podeDesExcessoTentativas = responsavel.temPermissao(CodedValues.FUN_DESBLOQUEAR_USUARIOS_SER);
                      boolean usuarioBloqueadoTentativas = false;
                      
                      if (podeDesExcessoTentativas && CodedValues.STU_BLOQUEADO_AUTOMATICAMENTE.equals(stuCodigo)) {
                          usuarioBloqueadoTentativas = true;    
                      }
              %>
                <tr>
                  <td class="<%=TextHelper.forHtmlAttribute(usuLoginClass)%>"><%=TextHelper.forHtmlContent(usuLoginCons)%></td>
                  <td><%=TextHelper.forHtmlContent(serNome.toUpperCase())%></td>
                  <td><%=TextHelper.forHtmlContent(serCpf)%></td>
                  <td><%=TextHelper.forHtmlContent(matricula)%></td>
                  <td><%=TextHelper.forHtmlContent(orgNome + " - " + orgIdentificador)%></td>
                  <td><%=TextHelper.forHtmlContent(estIdentificador)%></td>
                  <td class="<%=TextHelper.forHtmlAttribute(stuCodigoClass)%>"><%=TextHelper.forHtmlContent(stuCodigoCons)%></td>
                  <td>
                    <div class="actions">
                      <div class="dropdown">
                        <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                          <div class="form-inline">
                            <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.mais.acoes"/>" aria-label="<hl:message key="rotulo.mais.acoes"/>"> <svg>
                                <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                            </span> <hl:message key="rotulo.botao.opcoes"/>
                          </div>
                        </a>
                        <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                        <% if (usuarioExiste) { %>
                          <% if (responsavel.temPermissao(CodedValues.FUN_BLOQ_DESBLOQUEAR_USU_SERVIDOR)) { %>
                            <a class="dropdown-item" href="#no-back" onClick="bloquearUsuarioSer('<%=TextHelper.forJavaScript(stuCodigo)%>', '<%=TextHelper.forJavaScript(usuCodigo)%>', '<%=TextHelper.forJavaScript(serNome.toUpperCase())%>', <%=(boolean)(exigeMotivoOperacaoUsu && FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_BLOQ_DESBLOQUEAR_USU_SERVIDOR, responsavel))%>)"><hl:message key="rotulo.acoes.bloquear.desbloquear"/></a>
                          <% } else { %>
                            <% if (usuarioBloqueadoTentativas) {%>
                            <a class="dropdown-item" href="#no-back" onClick="desbloquearNumTentativas('<%=TextHelper.forJavaScript(stuCodigo)%>', '<%=TextHelper.forJavaScript(usuCodigo)%>', '<%=TextHelper.forJavaScript(serNome.toUpperCase())%>', <%=(boolean)(exigeMotivoOperacaoUsu && FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_BLOQ_DESBLOQUEAR_USU_SERVIDOR, responsavel))%>)"><hl:message key="rotulo.acoes.bloquear.desbloquear"/></a>
                            <% } else { %>
                            <a class="dropdown-item" href="motivo-operacao.php?titulo=Bloquear/Desbloquear">Bloquear/Desbloquear -- Não usar</a>
                            <% } %>
                          <% } %>
                        <% } else { %>
                          <a class="dropdown-item" href="#no-back" onClick="postData('../v3/inserirUsuarioServidor?acao=iniciar&RSE_MATRICULA=<%=TextHelper.forJavaScriptAttribute(matricula)%>&SER_CPF=<%=TextHelper.forJavaScript(serCpf)%>&EST_IDENTIFICADOR=<%=TextHelper.forJavaScript(estIdentificador)%>&ORG_IDENTIFICADOR=<%=TextHelper.forJavaScript(orgIdentificador)%>&ORG_CODIGO=<%=TextHelper.forJavaScript(orgCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.usuario.criar.novo"/></a>
                        <% } %>

                        <% if (usuarioExiste && responsavel.temPermissao(CodedValues.FUN_ALTERAR_SENHA_USU_SERVIDOR)) { %>
                          <a class="dropdown-item" href="#no-back" onClick="alterarUsuario('<%=TextHelper.forJavaScript(matricula)%>', '<%=TextHelper.forJavaScript(serCpf)%>', '<%=TextHelper.forJavaScript(orgIdentificador)%>', '<%=TextHelper.forJavaScript(orgCodigo)%>', '<%=TextHelper.forJavaScript(estIdentificador)%>')"><hl:message key="rotulo.acoes.alterar.senha"/></a>
                        <% } %>
                        <% if (usuarioExiste && responsavel.temPermissao(CodedValues.FUN_ALTERAR_SENHA_AUTORIZACAO_USU_SER) && usaSenhaAutorizacao) { %>
                          <a class="dropdown-item" href="#no-back" onClick="alterarSenhaAutorizacao('<%=TextHelper.forJavaScript(matricula)%>', '<%=TextHelper.forJavaScript(serCpf)%>', '<%=TextHelper.forJavaScript(orgIdentificador)%>', '<%=TextHelper.forJavaScript(orgCodigo)%>', '<%=TextHelper.forJavaScript(estIdentificador)%>')"><hl:message key="rotulo.acoes.alterar.senha.autorizacao"/></a>
                        <% } else if (usuarioExiste && usaMultiplaSenhaAutorizacao && responsavel.temPermissao(CodedValues.FUN_CONSULTA_SENHA_MULT_AUTORIZA_SER)) {%>
                          <a class="dropdown-item" href="#no-back" onClick="lstSenhasAutorizacao('<%=TextHelper.forJavaScript(usuCodigo)%>', '<%=TextHelper.forJavaScript(serCodigo)%>')"><hl:message key="rotulo.acoes.alterar.senha.autorizacao"/></a>
                        <% } %>
                        <% if (usuarioExiste) { %>
                          <a class="dropdown-item" href="#no-back" onClick="detalheServidor('<%=TextHelper.forJavaScript(matricula)%>', '<%=TextHelper.forJavaScript(serCpf)%>', '<%=TextHelper.forJavaScript(orgIdentificador)%>', '<%=TextHelper.forJavaScript(orgCodigo)%>', '<%=TextHelper.forJavaScript(estIdentificador)%>')"><hl:message key="rotulo.acoes.detalhar"/></a>
                        <% } %>
                        <% if (usuarioExiste && responsavel.temPermissao(CodedValues.FUN_REINICIALIZAR_SENHA_SERVIDOR)) { %>
                          <a class="dropdown-item" href="#no-back" onClick="reiniciarSenhaUsuarioSer('<%=TextHelper.forJavaScript(usuCodigo)%>', '<%=TextHelper.forJavaScript(usuLogin)%>', '<%=TextHelper.forJavaScript(serNome.toUpperCase())%>', <%=(boolean)(exigeMotivoOperacaoUsu && FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_REINICIALIZAR_SENHA_SERVIDOR, responsavel))%>)"><hl:message key="rotulo.acoes.reinicializar.senha"/></a>
                        <% } %>
                        <% if (usuarioExiste && responsavel.temPermissao(CodedValues.FUN_CAD_DISPENSA_VALIDACAO_DIGITAL_SER)) { %>
                          <a class="dropdown-item" href="#no-back" onClick="cadastrarValidacaoDispensaDigital('<%=TextHelper.forJavaScript(rseCodigo)%>');"><hl:message key="rotulo.servidor.cadastrar.dispensa.validacao.digital"/></a>
                        <% } %>
                        </div>
                      </div>
                    </div>
                  </td>
                </tr>
                <% }
                 } %>
              </tbody>
              <tfoot>
				<tr>
				  <td colspan="100%">
				    <%=ApplicationResourcesHelper.getMessage("mensagem.listagem.usuario.servidore.plural", responsavel)%>
				    <span class="font-italic"> - <%=request.getAttribute("_paginacaoSubTitulo")%></span>
				  </td>
				</tr>
			  </tfoot>
            </table>
          </div>
          <div class="card-footer">
		    <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
		  </div>
        </div>
      </div>
    </div>
    <div class="btn-action">
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.voltar"/></a>
    </div>
    <form name="form_edit" id="form_edit" method="post">
      <input type="hidden" name="operacao" id="operacao" value=""/>
      <input type="hidden" name="tipo" id="tipo" value=""/>
      <input type="hidden" name="codigoEntidade" id="codigoEntidade" value=""/>
      <input type="hidden" name="USU_CODIGO" id="USU_CODIGO" value=""/>
      <input type="hidden" name="USU_LOGIN" id="USU_LOGIN" value=""/>
      <input type="hidden" name="STATUS" id="STATUS" value=""/>
    </form>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
<!--

function alterarUsuario (matricula, cpf, orgao, orgCodigo, estabelecimento) {
  var url = "../v3/alterarSenhaUsuarioServidor?acao=iniciar&RSE_MATRICULA=" + matricula + "&SER_CPF=" + cpf + "&EST_IDENTIFICADOR=" + estabelecimento + "&ORG_IDENTIFICADOR=" + orgao + "&ORG_CODIGO=" + orgCodigo + "&<%=SynchronizerToken.generateToken4URL(request)%>";
  postData(url);
}

function alterarSenhaAutorizacao (matricula, cpf, orgao, orgCodigo, estabelecimento) {
  var url = "../v3/alterarSenhaAutorizacaoUsuarioServidor?acao=iniciar&RSE_MATRICULA=" + matricula + "&SER_CPF=" + cpf + "&EST_IDENTIFICADOR=" + estabelecimento + "&ORG_IDENTIFICADOR=" + orgao + "&ORG_CODIGO=" + orgCodigo + "&<%=SynchronizerToken.generateToken4URL(request)%>";
  postData(url);
}

function detalheServidor (matricula, cpf, orgao, orgCodigo, estabelecimento) {
  var url = "../v3/detalharUsuarioServidor?acao=iniciar&RSE_MATRICULA=" + matricula + "&ORG_IDENTIFICADOR=" + orgao + "&ORG_CODIGO=" + orgCodigo + "&EST_IDENTIFICADOR=" + estabelecimento + "&SER_CPF=" + cpf + "&TIPO_USUARIO=SER&<%=SynchronizerToken.generateToken4URL(request)%>";
  postData(url);
}

function lstSenhasAutorizacao (usuCodigo, serCodigo) {
  var url = "../v3/listarSenhaAutorizacao?acao=iniciar&USU_CODIGO=" + usuCodigo + "&SER_CODIGO=" + serCodigo + "&<%=SynchronizerToken.generateToken4URL(request)%>";
  postData(url);
}

function cadastrarValidacaoDispensaDigital (rseCodigo) {
  var url = "../v3/cadastrarDispensaValidacaoDigitalServidor?acao=iniciar&RSE_CODIGO=" + rseCodigo + "&<%=SynchronizerToken.generateToken4URL(request)%>";
  postData(url);
}

function reiniciarSenhaUsuarioSer(usuario, login, desc, redireciona) {  
   var opr = 'reinicializar-ser';
   
   var msg = mensagem('mensagem.confirmacao.reinicializar.senha.usuario').replace('{0}', desc);
   if (redireciona) {
     msg = "";
   }
   
   if (msg == "" || confirm(msg)) {
     var formEdit = document.forms['form_edit'];
     formEdit.operacao.value = opr;
     formEdit.USU_CODIGO.value = usuario;
     formEdit.USU_LOGIN.value = login;
     formEdit.tipo.value = "SER";
     formEdit.action = "../v3/reinicializarSenhaServidor?acao=iniciarReinicializacaoSenhaSer&<%=SynchronizerToken.generateToken4URL(request)%>";
     formEdit.submit();
   }
}

function bloquearUsuarioSer(status, usuario, desc, redireciona) {  
    var opr = 'bloquear-ser';
    
    var msg = "";
    
    if (!redireciona) {
      if (status == <%=CodedValues.STU_ATIVO%>) {
        msg = mensagem('mensagem.confirmacao.bloqueio.usuario').replace('{0}', desc);
      } else {
        msg = mensagem('mensagem.confirmacao.desbloqueio.usuario').replace('{0}', desc);
      }
    }
    
    if (msg == "" || confirm(msg)) {
      var formEdit = document.forms['form_edit'];
      formEdit.operacao.value = opr;
      formEdit.USU_CODIGO.value = usuario;
      formEdit.STATUS.value = status;
      formEdit.tipo.value = "SER";
      formEdit.action = "../v3/bloquearUsuario?acao=iniciarBloqueioSer&<%=SynchronizerToken.generateToken4URL(request)%>";
      formEdit.submit();
    }
}

function desbloquearNumTentativas(status, usuario, desc, redireciona) {  
    var opr = 'desbloquear-ser-num-tentativas';
    
    var msg = "";
    
    if (!redireciona) {
      if (status == <%=CodedValues.STU_ATIVO%>) {
        msg = mensagem('mensagem.confirmacao.bloqueio.usuario').replace('{0}', desc);
      } else {
        msg = mensagem('mensagem.confirmacao.desbloqueio.usuario').replace('{0}', desc);
      }
    }
    
    if (msg == "" || confirm(msg)) {
      var formEdit = document.forms['form_edit'];
      formEdit.operacao.value = opr;
      formEdit.USU_CODIGO.value = usuario;
      formEdit.STATUS.value = status;
      formEdit.tipo.value = "SER";
      formEdit.action = "../v3/desbloquearNumTentativas?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>";
      formEdit.submit();
    }
}
 
//-->
</script>
</c:set>
<%-- Leiaute Fixo --%>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4> 