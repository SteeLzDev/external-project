<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

boolean podeEditarAlgumUsu = (boolean) request.getAttribute("podeEditarAlgumUsu");
boolean podeBlDesblAlgumUsu = (boolean) request.getAttribute("podeBlDesblAlgumUsu");
boolean podeExcluirAlgumUsu = (boolean) request.getAttribute("podeExcluirAlgumUsu");
boolean podeReinicSenhaAlgumUsu = (boolean) request.getAttribute("podeReinicSenhaAlgumUsu");
int qtdColunas = Integer.valueOf(request.getAttribute("qtdColunas").toString());
Map<String, Map<String, Boolean>> permissoes = (Map<String, Map<String, Boolean>>) request.getAttribute("permissoes");

//Exige tipo de motivo da operacao
boolean exigeMotivoOperacaoUsu = (boolean) request.getAttribute("exigeMotivoOperacaoUsu");

String tipo = (String) request.getAttribute("tipo");
String codigo = responsavel.getCodigoEntidade();
String filtro = (String) request.getAttribute("filtro");
int filtro_tipo = (int) request.getAttribute("filtro_tipo");
List usuarios = (List) request.getAttribute("usuarios");

%>
<c:set var="title">
  ${tituloPagina}
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
   <div class="row">
      <div class="col-sm-5 col-md-4">
          <div class="card">
            <div class="card-header hasIcon pl-3">
              <h2 class="card-header-title"><hl:message key="rotulo.botao.pesquisar"/></h2>
            </div>
            <div class="card-body">
              <form NAME="form1" METHOD="post" ACTION="${linkAction}">
                <input type="hidden" name="acao" value="iniciar">
                <div class="row">
                  <div class="form-group col-sm">
                    <label for="FILTRO"><hl:message key="rotulo.acoes.filtrar"/> <hl:message key="rotulo.usuario.singular"/></label>
                    <input type="text" class="form-control" id="FILTRO" name="FILTRO" value="<%=TextHelper.forHtmlAttribute(filtro)%>" placeholder="<hl:message key="rotulo.acao.digite.filtro"/>">
                  </div>
                </div>
                <div class="row">
                  <div class="form-group col-sm">
                    <label for="FILTRO_TIPO"><hl:message key="rotulo.acao.filtrar.por"/></label>
                    <select class="form-control form-select select" id="FILTRO_TIPO" name="FILTRO_TIPO">
                      <optgroup label="<%=ApplicationResourcesHelper.getMessage("rotulo.usuario.lista.filtros", responsavel)%>:">
                        <option value="" <%=(String)((filtro_tipo == -1) ? "SELECTED" : "")%>><hl:message key="rotulo.campo.sem.filtro"/></option>
                        <option value="02" <%=(String)((filtro_tipo ==  2) ? "SELECTED" : "")%>><hl:message key="rotulo.usuario.singular"/></option>
                        <option value="03" <%=(String)((filtro_tipo ==  3) ? "SELECTED" : "")%>><hl:message key="rotulo.usuario.nome"/></option>
                        <% if (tipo.equals(AcessoSistema.ENTIDADE_CSE)) { %>
                          <%if(responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_CSE)) { %>
                            <option value="04" <%=(String)((filtro_tipo ==  4) ? "SELECTED" : "")%>><hl:message key="rotulo.consignante.singular"/></option>
                          <%} %>
                          <%if(responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_ORG)) { %>
                            <option value="05" <%=(String)((filtro_tipo ==  5) ? "SELECTED" : "")%>><hl:message key="rotulo.orgao.singular"/></option>
                          <%} %>
                          <%if(responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_SUP) && responsavel.isSup()) { %>
                            <option value="08" <%=(String)((filtro_tipo ==  8) ? "SELECTED" : "")%>><hl:message key="rotulo.suporte.singular"/></option>
                          <%} %>
                        <% } %>
                        <% if (tipo.equals(AcessoSistema.ENTIDADE_CSE) || tipo.equals(AcessoSistema.ENTIDADE_CSA)) { %>
                          <%if(responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_CSA)) { %>
                            <option value="06" <%=(String)((filtro_tipo ==  6) ? "SELECTED" : "")%>><hl:message key="rotulo.consignataria.singular"/></option>
                          <%} %>
                          <%if(responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_COR)) { %>
                            <option value="07" <%=(String)((filtro_tipo ==  7) ? "SELECTED" : "")%>><hl:message key="rotulo.correspondente.singular"/></option>
                          <%} %>
                        <% } %>
                        <% if (tipo.equals(AcessoSistema.ENTIDADE_ORG) && responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_CSA)) { %>
                          <option value="05" <%=(String)((filtro_tipo ==  5) ? "SELECTED" : "")%>><hl:message key="rotulo.orgao.singular"/></option>
                          <option value="06" <%=(String)((filtro_tipo ==  6) ? "SELECTED" : "")%>><hl:message key="rotulo.consignataria.singular"/></option>
                        <% } %>
                        <option value="00" <%=(String)((filtro_tipo ==  0) ? "SELECTED" : "")%>><hl:message key="rotulo.usuario.lista.bloqueado"/></option>
                        <option value="01" <%=(String)((filtro_tipo ==  1) ? "SELECTED" : "")%>><hl:message key="rotulo.usuario.lista.desbloqueado"/></option>
                        <option value="09" <%=(String)((filtro_tipo ==  9) ? "SELECTED" : "")%>><hl:message key="rotulo.usuario.lista.cpf"/></option>
                      </optgroup>
                    </select>
                  </div>
                </div>
              </form>
            </div>
          </div>
          <div class="btn-action">
            <a class="btn btn-primary" href="#no-back" onClick="filtrar();">
              <svg width="20">
                <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="../img/sprite.svg#i-consultar"></use></svg> <hl:message key="rotulo.botao.pesquisar"/>
            </a>
          </div>
      </div>
      <div class="col-sm-7 col-md-8">
          <div class="card">
            <div class="card-header hasIcon pl-3">
              <h2 class="card-header-title"><hl:message key="rotulo.usuario.plural"/></h2>
            </div>
            <div class="card-body table-responsive p-0">
              <table class="table table-striped table-hover">
                <thead>
                  <tr>
                    <th scope="col"><hl:message key="rotulo.usuario.singular"/></th>
                    <th scope="col"><hl:message key="rotulo.usuario.nome"/></th>
                    <% if (tipo.equals(AcessoSistema.ENTIDADE_CSE) || tipo.equals(AcessoSistema.ENTIDADE_CSA) || (tipo.equals(AcessoSistema.ENTIDADE_ORG) && responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_CSA))) { %>
                      <th scope="col"><hl:message key="rotulo.usuario.entidade"/></th>
                    <% } %>                    
                    <th scope="col"><hl:message key="rotulo.usuario.situacao"/></th>
                    <th scope="col"><hl:message key="rotulo.usuario.lista.tipo.bloqueio"/></th>
                    <th scope="col"><hl:message key="rotulo.acoes"/></th>
                  </tr>
                </thead>
                <tbody>
                  <%=JspHelper.msgRstVazio(usuarios.size()==0, qtdColunas+1, responsavel)%>
                  <%
                      String usu_nome, usu_login, stu_codigo, usu_codigo, usu_tipo_bloq, usu_tipo, usu_entidade, codigo_entidade;
                      String linkConsultarUsuario, linkEditarUsuario, linkBloquearUsuario, linkReinicializarSenhaUsuario, linkExcluirUsuario, linkDetalharUsuario;
                      String stu_codigo_class = "";
                      String stu_codigo_descricao = "";
                      
                      Iterator it = usuarios.iterator();
                      while (it.hasNext()) {
                        CustomTransferObject next = (CustomTransferObject)it.next();
                        usu_nome = next.getAttribute(Columns.USU_NOME).toString();
                        usu_login = next.getAttribute(Columns.USU_LOGIN).toString();
                        stu_codigo = next.getAttribute(Columns.USU_STU_CODIGO).toString();
                        usu_codigo = next.getAttribute(Columns.USU_CODIGO).toString();
                        usu_tipo_bloq = (next.getAttribute(Columns.USU_TIPO_BLOQ) != null && (CodedValues.STU_CODIGOS_INATIVOS.contains(stu_codigo))) ? next.getAttribute(Columns.USU_TIPO_BLOQ).toString() : "";
                        usu_tipo = next.getAttribute("TIPO").toString().toUpperCase();
                        usu_entidade = next.getAttribute("ENTIDADE").toString();
                        codigo_entidade = (String) next.getAttribute("CODIGO_ENTIDADE");
                        stu_codigo_class = "";
                        stu_codigo_descricao = "";
                        
                        if (stu_codigo.equals(CodedValues.STU_ATIVO)) {
                          stu_codigo_descricao = ApplicationResourcesHelper.getMessage("rotulo.usuario.papel.desbloqueado", responsavel);
                        } else if(CodedValues.STU_CODIGOS_INATIVOS.contains(stu_codigo)) {
                          stu_codigo_class = "block";
                          stu_codigo_descricao = ApplicationResourcesHelper.getMessage("rotulo.usuario.papel.bloqueado", responsavel);
                        } else if(stu_codigo.equals(CodedValues.STU_EXCLUIDO)) {
                          stu_codigo_descricao = ApplicationResourcesHelper.getMessage("rotulo.usuario.papel.excluido", responsavel);
                        }
                        
                        linkConsultarUsuario = next.getAttribute("linkConsultarUsuario").toString();
                        //linkInserirUsuario = next.getAttribute("linkInserirUsuario").toString();
                        linkEditarUsuario = next.getAttribute("linkEditarUsuario").toString();
                        linkBloquearUsuario = next.getAttribute("linkBloquearUsuario").toString();
                        linkReinicializarSenhaUsuario = next.getAttribute("linkReinicializarSenhaUsuario").toString();
                        linkExcluirUsuario = next.getAttribute("linkExcluirUsuario").toString();
                        linkDetalharUsuario = next.getAttribute("linkDetalharUsuario").toString();
                  %>
                  <tr>
                    <td><%=TextHelper.forHtmlContent(usu_login)%></td>
                    <td><%=TextHelper.forHtmlContent(usu_nome.toUpperCase())%></td>
                    <% if (tipo.equals(AcessoSistema.ENTIDADE_CSE) || tipo.equals(AcessoSistema.ENTIDADE_CSA) || (tipo.equals(AcessoSistema.ENTIDADE_ORG) && responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_CSA))) { %>
                      <td><%=TextHelper.forHtmlContent(usu_entidade.toUpperCase())%></td>
                    <% } %>
                    <td class="<%=TextHelper.forHtmlAttribute(stu_codigo_class)%>"><%=TextHelper.forHtmlContent(stu_codigo_descricao)%></td>
                    <td align="center" class="<%=TextHelper.forHtmlAttribute(stu_codigo_class)%>"><%=TextHelper.forHtmlContent((usu_tipo_bloq != "") ? usu_tipo_bloq.toUpperCase() : "-")%></td>
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
                            <% if (permissoes.get(usu_tipo).get("podeBlDesblUsu") && !stu_codigo.equals(CodedValues.STU_EXCLUIDO)) { %>
                              <a class="dropdown-item" href="#no-back" onClick="BloquearUsuario('<%=TextHelper.forJavaScript(linkBloquearUsuario)%>', '<%=TextHelper.forJavaScript(stu_codigo)%>', '<%=TextHelper.forJavaScript(usu_codigo)%>', '<%=TextHelper.forJavaScript(codigo_entidade)%>', '<%=TextHelper.forJavaScript(usu_nome.toUpperCase())%>', <%=(boolean)(exigeMotivoOperacaoUsu && permissoes.get(usu_tipo).get("bloquearUsuExigeMotivo"))%>, '<%=TextHelper.forJavaScript((session.getAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY)))%>')"><hl:message key="rotulo.acao.bloquear.desbloquear"/></a>
                            <% } %>
                            
                            <% if (permissoes.get(usu_tipo).get("podeEditarUsu")) { %>
                              <a class="dropdown-item" href="#no-back" onClick="postData('<%=linkEditarUsuario%>?acao=iniciar&usu_codigo=<%=TextHelper.forJavaScript(usu_codigo)%>&tipo=<%=TextHelper.forJavaScript(usu_tipo)%>&codigo=<%=TextHelper.forJavaScript(codigo_entidade)%>&titulo=<%=TextHelper.encode64(usu_entidade)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.editar"/></a>
                            <% } else { %>
                              <a class="dropdown-item" href="#no-back" onClick="postData('<%=linkConsultarUsuario%>?acao=iniciar&readOnly=true&usu_codigo=<%=TextHelper.forJavaScript(usu_codigo)%>&tipo=<%=TextHelper.forJavaScript(usu_tipo)%>&codigo=<%=TextHelper.forJavaScript(codigo_entidade)%>&titulo=<%=TextHelper.encode64(usu_entidade)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.consultar"/></a>
                            <% } %>
                            <a class="dropdown-item" href="#no-back" onClick="detalheUsuario('<%=TextHelper.forJavaScript(linkDetalharUsuario)%>', '<%=TextHelper.forJavaScript(usu_codigo)%>', '<%=TextHelper.forJavaScript((session.getAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY)))%>')"><hl:message key="rotulo.usuario.historico"/></a>
                            <% if (!stu_codigo.equals(CodedValues.STU_EXCLUIDO)) { %>
                              <% if (podeExcluirAlgumUsu) { %>
                                <% if (permissoes.get(usu_tipo).get("podeExcluirUsu")) { %>
                                    <a class="dropdown-item" href="#no-back" onClick="ExcluirUsuario('<%=TextHelper.forJavaScript(linkExcluirUsuario)%>', '<%=TextHelper.forJavaScript(usu_codigo)%>', '<%=TextHelper.forJavaScript(usu_nome.toUpperCase())%>', '<%=TextHelper.forJavaScript(codigo_entidade)%>', <%=(boolean)(exigeMotivoOperacaoUsu && permissoes.get(usu_tipo).get("excluirUsuExigeMotivo"))%>, '<%=TextHelper.forJavaScript((session.getAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY)))%>')"><hl:message key="rotulo.acoes.excluir"/></a>
                                <% } %>
                              <% } %>
                              <% if (podeReinicSenhaAlgumUsu) { %>
                                <% if (permissoes.get(usu_tipo).get("podeReinicSenha")) { %>
                                    <a class="dropdown-item" href="#no-back" onClick="ReiniciarSenha('<%=TextHelper.forJavaScript(linkReinicializarSenhaUsuario)%>', '<%=TextHelper.forJavaScript(usu_codigo)%>', '<%=TextHelper.forJavaScript(codigo_entidade)%>', '<%=TextHelper.forJavaScript(usu_login)%>', '<%=TextHelper.forJavaScript(usu_nome.toUpperCase())%>', <%=(boolean)(exigeMotivoOperacaoUsu && permissoes.get(usu_tipo).get("reiniciarSenhaExigeMotivo"))%>, '<%=TextHelper.forJavaScript((session.getAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY)))%>')"><hl:message key="rotulo.usuario.reinicializar.senha.v4"/></a>
                                <% } %>
                              <% } %>
                            <% } %>
                          </div>
                        </div>
                      </div>
                    </td>
                  </tr>
                  <% } %>
                </tbody>
                <tfoot>
                  <tr>
                    <td colspan="<%= qtdColunas+1 %>"><%=ApplicationResourcesHelper.getMessage("mensagem.listagem.usuarios", responsavel) + " - " %><span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTitulo"))%></span></td>
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
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;" id="btnVoltar"><hl:message key="rotulo.botao.voltar"/></a>
    </div>   
</c:set>
<c:set var="javascript">
<script src="../js/usuario.js?<hl:message key="release.tag"/>"></script>
<script type="text/JavaScript" src="../js/mascaraCpf.js"></script>
<script type="text/JavaScript">
f0 = document.forms[0];
function imprime() {
    window.print();
}

function filtrar() {
	
	//FILTRO_TIPO == CPF
	if (FILTRO_TIPO.value == 09) {
		if (FILTRO.value != null && FILTRO.value == '') {
			alert(mensagem('mensagem.informe.cpf'));
	 	} else if (!CPF_OK(extraiNumCNPJCPF(FILTRO.value))) {
	 		return false;
		} else {
			f0.submit();
		}
	}
	
	if (FILTRO_TIPO.value != 09) {
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