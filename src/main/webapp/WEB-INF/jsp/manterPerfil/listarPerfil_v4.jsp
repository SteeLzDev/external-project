<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
String linkVoltar = (String) request.getAttribute("linkVoltar");
String codigo = (String) request.getAttribute("codigo");
String parametros = (String) request.getAttribute("parametros");
List perfil = (List) request.getAttribute("perfil");
String opInserir = (String) request.getAttribute("opInserir");
String opEditar = (String) request.getAttribute("opEditar");
String opConsultar = (String) request.getAttribute("opConsultar");
String opManter = (String) request.getAttribute("opManter");
String opListarUsuario = (String) request.getAttribute("opListarUsuario");
String titulo = (String) request.getAttribute("titulo");
String linkAction = (String) request.getAttribute("linkAction");
String tituloCompleto = ApplicationResourcesHelper.getMessage("rotulo.perfil.lista.perfil.nome.titulo", responsavel, titulo);
%>
<c:set var="title">
  <%=tituloCompleto %>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
    <div class="col-sm-12 col-md-12 mb-2">
      <div class="float-end">
        <div class="btn-action">
          <a class="btn btn-primary" href="#no-back" onClick="postData('../v3/<%=opInserir%>?acao=iniciar&codigo=<%=TextHelper.forJavaScript(codigo)%>&titulo=<%=TextHelper.forJavaScript(titulo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.usuario.perfil.novoPerfil"/></a>
        </div>
      </div>
    </div>
  </div>
  <div class="row">
    <div class="col-sm-5 col-md-4">
      <div class="card">
        <div class="card-header hasIcon pl-3">
          <h2 class="card-header-title"><hl:message key="rotulo.botao.pesquisar"/></h2>
        </div>
        <div class="card-body">
          <form NAME="form1" METHOD="post" ACTION="${linkAction}&<%=SynchronizerToken.generateToken4URL(request)%>">
            <input type="hidden" name="codigo" id="codigo" value="<%=codigo%>"/>
            <input type="hidden" name="titulo" id="titulo" value="<%=TextHelper.forHtmlAttribute(titulo)%>">
            <div class="row">
              <div class="form-group col-sm">
                <label for="iFiltro"><hl:message key="rotulo.usuario.perfil.filtrarPerfil"/></label>
                <input type="text" class="form-control" id="FILTRO" name="FILTRO" placeholder="<%=ApplicationResourcesHelper.getMessage("mensagem.informe.filtro", responsavel) %>">   
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm">
                <label for="iFiltrarPor"><hl:message key="rotulo.usuario.perfil.filtrar.por"/>:</label>
                <select class="form-control form-select select" id="FILTRO_TIPO" name="FILTRO_TIPO">
                  <optgroup label="<%=ApplicationResourcesHelper.getMessage("rotulo.perfil.filtro", responsavel) %>:">
                    <option value="" ${filtro_tipo == -1 ? "SELECTED" : ""}><hl:message key="rotulo.usuario.perfil.sem.filtro"/></option>
                    <option value="02" ${filtro_tipo ==  2 ? "SELECTED" : ""}><hl:message key="rotulo.perfil.descricao"/></option>
                    <option value="00" ${filtro_tipo ==  0 ? "SELECTED" : ""}><hl:message key="rotulo.perfil.bloqueado"/></option>
                    <option value="01" ${filtro_tipo ==  1 ? "SELECTED" : ""}><hl:message key="rotulo.perfil.desbloqueado"/></option>
                  </optgroup>
                </select>
              </div>
            </div>
          </form>
        </div>
      </div>
      <div class="btn-action">
        <a class="btn btn-primary" href="#" onClick="document.forms[0].submit(); return false;"> <svg width="20">
          <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="../img/sprite.svg#i-consultar"></use></svg> <hl:message key="rotulo.botao.pesquisar"/>
        </a>
      </div>
    </div>
    <div class="col-sm-7 col-md-8">
      <div class="card">
        <div class="card-header hasIcon pl-3">
          <h2 class="card-header-title"><hl:message key="rotulo.perfil.plural"/></h2>
        </div>
        <div class="card-body table-responsive p-0">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th scope="col"><hl:message key="rotulo.usuario.perfil.descricao"/></th>
                <th scope="col"><hl:message key="rotulo.usuario.perfil.situacao"/></th>
                <th scope="col"><hl:message key="rotulo.acoes.filtrar"/></th>
              </tr>
            </thead>
            <tbody>
            <%
              String perCodigo = "", perDescricao = "";
              Short status = null;
              String stu_codigo_descricao = "", stu_codigo_alt = "", stu_codigo_class = "";
              
              Iterator it = perfil.iterator();
              while (it.hasNext()) {
                CustomTransferObject next = (CustomTransferObject)it.next();
                perCodigo = next.getAttribute(Columns.PER_CODIGO).toString();
                perDescricao = next.getAttribute(Columns.PER_DESCRICAO).toString();
                status = new Short(next.getAttribute("STATUS").toString());
                stu_codigo_class = "";
              
                if (status.equals(CodedValues.STS_ATIVO)) {
                  stu_codigo_alt = ApplicationResourcesHelper.getMessage("rotulo.acoes.bloquear", responsavel);
                  stu_codigo_descricao = ApplicationResourcesHelper.getMessage("rotulo.perfil.desbloqueado", responsavel);
                } else if(status.equals(CodedValues.STS_INATIVO)) {
                  stu_codigo_alt = ApplicationResourcesHelper.getMessage("rotulo.acoes.desbloquear", responsavel);
                  stu_codigo_descricao = ApplicationResourcesHelper.getMessage("rotulo.perfil.bloqueado", responsavel);
                  stu_codigo_class = "block";
                }
                %>
              <tr>
                <td><%=TextHelper.forHtmlContent(perDescricao.toUpperCase())%></td>
                <td class="<%=TextHelper.forHtmlAttribute(stu_codigo_class)%>"><%=TextHelper.forHtmlContent(stu_codigo_descricao)%></td>
                <td>
                  <div class="actions">
                    <div class="dropdown">
                      <a class="dropdown-toggle ico-action" href="#" role="button" id="<%=TextHelper.forHtmlAttribute(perDescricao.toUpperCase())%>" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <div class="form-inline">
                          <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<%=ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)%>" aria-label="<%=ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)%>"> <svg>
                            <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                          </span> <hl:message key="rotulo.botao.opcoes"/>
                        </div>
                      </a>
                      <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                        <c:if test="${podeEditarUsu || podeConsultarUsu}">
                          <a class="dropdown-item" href="#no-back" onClick="postData('../v3/<%=opListarUsuario%>?acao=listar&codigo=<%=TextHelper.forJavaScript(codigo)%>&titulo=<%=TextHelper.encode64(titulo)%>&per_codigo=<%=TextHelper.forJavaScript(perCodigo)%>&fromLstPerfil=1&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.usuario.plural"/></a>
                        </c:if>
                        <c:choose>
                          <c:when test="${podeEditarPerfil}">
                            <a class="dropdown-item" href="#no-back" onClick="BloquearPerfil('<%=TextHelper.forJavaScript((status))%>', '<%=TextHelper.forJavaScript(perCodigo)%>', '<%=TextHelper.forJavaScript(perDescricao)%>')"><%=TextHelper.forHtmlAttribute(stu_codigo_alt)%></a>
                            <a class="dropdown-item" href="#no-back" onClick="postData('../v3/<%=opEditar%>?acao=iniciar&PER_CODIGO=<%=TextHelper.forJavaScriptAttribute(perCodigo)%>&PER_DESCRICAO=<%=TextHelper.forJavaScript(perDescricao)%>&codigo=<%=TextHelper.forJavaScript(codigo)%>&titulo=<%=TextHelper.forJavaScript(titulo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.editar"/></a>
                            <a class="dropdown-item" href="#no-back" onClick="ExcluirPerfil('<%=TextHelper.forJavaScript(perCodigo)%>', '<%=TextHelper.forJavaScript(perDescricao)%>');"><hl:message key="rotulo.acoes.excluir"/></a>
                          </c:when>
                          <c:when test="${podeConsultarPerfil}">
                            <a class="dropdown-item" href="#no-back" onClick="postData('../v3/<%=opConsultar%>?acao=iniciar&PER_CODIGO=<%=TextHelper.forJavaScriptAttribute(perCodigo)%>&titulo=<%=TextHelper.forJavaScript(titulo)%>&codigo=<%=TextHelper.forJavaScript(codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.botao.consultar"/></a>
                          </c:when>
                        </c:choose>
                        <c:if test="<%=responsavel.temPermissao(CodedValues.FUN_CONS_PERFIL)%>">
                          <a class="dropdown-item" href="#no-back" onClick="postData('../v3/listarOcorrenciaPerfil?acao=iniciar&PER_CODIGO=<%=TextHelper.forJavaScriptAttribute(perCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.perfil.exibir.ocorrencias"/></a>
                        </c:if>
                      </div>
                    </div>
                  </div>
                </td>
              </tr>
              
            <%}%>
            </tbody>
            <tfoot>
              <tr>
                <td colspan="4"><hl:message key="rotulo.usuario.perfil.listagem.footer"/></td>
              </tr>
            </tfoot>
          </table>
        </div>
      </div>
      <div class="btn-action">
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(linkVoltar)%>')"><hl:message key="rotulo.botao.voltar"/></a> 
      </div>
    </div>
  </div>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript">
  function BloquearPerfil(status, perCodigo, perDescricao) {
    var url = "../v3/<%=opManter%>?acao=bloquear"+ "&PER_CODIGO=" + perCodigo + "&STATUS=" + status + "&codigo=<%=TextHelper.forJavaScriptBlock(codigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>";
    if (status == "1") {
      return ConfirmaUrl('<hl:message key="mensagem.perfil.todos.serao.bloqueados.confirma"/>'.replace('{0}', perDescricao), url);
    } else {
      return ConfirmaUrl('<hl:message key="mensagem.perfil.todos.serao.desbloqueados.confirma"/>'.replace('{0}', perDescricao), url);
    }
  }
  
  function ExcluirPerfil(perCodigo, perDescricao) {
    var url = "../v3/<%=opManter%>?acao=excluir"+ "&PER_CODIGO=" + perCodigo + "&codigo=<%=TextHelper.forJavaScriptBlock(codigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>";
    return ConfirmaUrl('<hl:message key="mensagem.perfil.exclusao.confirma"/>'.replace('{0}', perDescricao), url);
  }
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
  
