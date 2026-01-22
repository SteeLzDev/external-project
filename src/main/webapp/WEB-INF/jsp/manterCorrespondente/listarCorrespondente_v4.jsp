<%@page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

boolean podeCriarUsu = (Boolean) request.getAttribute("podeCriarUsu");
boolean podeConsultarUsu = (Boolean) request.getAttribute("podeConsultarUsu");
boolean podeConsultarCnvCor = (Boolean) request.getAttribute("podeConsultarCnvCor");
boolean podeEditarCnvCor = (Boolean) request.getAttribute("podeEditarCnvCor");
boolean podeConsultarCor = (Boolean) request.getAttribute("podeConsultarCor");
boolean podeEditarCor = (Boolean) request.getAttribute("podeEditarCor");
boolean podeExcluirCor = (Boolean) request.getAttribute("podeExcluirCor");
boolean podeEditarEnderecoAcesso = (Boolean) request.getAttribute("podeEditarEnderecoAcesso");
boolean podeEditarEnderecosCor = (Boolean) request.getAttribute("podeEditarEnderecosCor");
boolean podeConsultarPerfilUsu = (Boolean) request.getAttribute("podeConsultarPerfilUsu");

String csa_codigo = (String) request.getAttribute("csa_codigo");
String titulo = (String) request.getAttribute("titulo");
String novoCorr = (String) request.getAttribute("novoCorr");
String editaCorr = (String) request.getAttribute("editaCorr");
String canc = (String) request.getAttribute("canc");
String linkRet = (String) request.getAttribute("linkRet");
String linkEdit = (String) request.getAttribute("linkEdit");
List<?> correspondentes = (List<?>) request.getAttribute("correspondentes");
String filtro = (String) request.getAttribute("filtro");
int filtro_tipo = (int) request.getAttribute("filtro_tipo");

boolean cadastraEmpCor = (Boolean) request.getAttribute("cadastraEmpCor");
%>
<c:set var="title">
  <hl:message key="rotulo.lista.correspondentes.titulo"/>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <div id="main">
    <div class="row">
      <div class="col-sm-12 col-md-12 mb-2">
        <div class="float-end">
          <div class="btn-action">
            <a class="btn btn-primary" href="#" onClick="postData('<%=TextHelper.forJavaScriptAttribute(novoCorr)%>')"><hl:message key="rotulo.criar.novo.correspondente"/></a>
          </div>
        </div>
      </div>
    </div>
    <div class="row">
      <div class="col-sm-5 col-md-4">
        <div class="card">
          <div class="card-header hasIcon pl-3">
            <h2 class="card-header-title"><hl:message key="rotulo.listagem.mensagem.pesquisar"/></h2>
          </div>
          <div class="card-body">
            <form name="form1" method="post" action="<%=TextHelper.forHtmlAttribute(linkEdit)%>">
              <div class="row">
                <div class="form-group col-sm">
                  <label for="iFiltro"><hl:message key="rotulo.filtrar.correspondente"/></label>
                  <input placeholder="<hl:message key="rotulo.acao.digite.filtro"/>" TYPE="text" NAME="FILTRO" class="form-control" id="iFiltro" SIZE="10" VALUE="<%=TextHelper.forHtmlAttribute(filtro)%>" onFocus="SetarEventoMascara(this,'#*200',true);"  onBlur="fout(this);ValidaMascara(this);">
                </div>
              </div>
              <div class="row">
                <div class="form-group col-sm">
                  <label for="iFiltrarPor"><hl:message key="rotulo.acao.filtrar.por"/></label>
                  <select NAME="FILTRO_TIPO" class="form-control form-select select" id="iFiltrarPor" onFocus="SetarEventoMascara(this,'#*200',true);"  onBlur="fout(this);ValidaMascara(this);">
                    <optgroup label="<%=ApplicationResourcesHelper.getMessage("rotulo.filtro.plural", responsavel)%>:">
                      <option value=""   <%=(String)((filtro_tipo == -1) ? "SELECTED" : "")%>><hl:message key="rotulo.campo.sem.filtro"/></OPTION>
                      <option value="02" <%=(String)((filtro_tipo ==  2) ? "SELECTED" : "")%>><hl:message key="rotulo.codigo.correspondente"/></OPTION>
                      <option value="03" <%=(String)((filtro_tipo ==  3) ? "SELECTED" : "")%>><hl:message key="rotulo.nome.correspondente"/></OPTION>
                      <option value="00" <%=(String)((filtro_tipo ==  0) ? "SELECTED" : "")%>><hl:message key="rotulo.bloqueado.correspondente"/></OPTION>
                      <option value="01" <%=(String)((filtro_tipo ==  1) ? "SELECTED" : "")%>><hl:message key="rotulo.desbloqueado.correspondente"/></OPTION>
                    </optgroup>
                  </select>
                </div>
              </div>
            </form>
          </div>
        </div>
        <div class="btn-action">
          <a class="btn btn-primary" href="#no-back" onClick="f0.submit(); return false;"> 
            <svg width="20">
              <use xlink:href="../img/sprite.svg#i-consultar"></use>
            </svg>
            <hl:message key="rotulo.acao.pesquisar"/>
          </a>
        </div>
      </div>
      <div class="col-sm-7 col-md-8">
        <div class="card">
          <div class="card-header hasIcon pl-3">
            <h2 class="card-header-title"><hl:message key="rotulo.correspondente.singular"/></h2>
          </div>
          <div class="card-body table-responsive ">
            <table class="table table-striped table-hover">
              <thead>
                <tr>
                  <th scope="col"><hl:message key="rotulo.codigo.correspondente"/></th>
                  <th scope="col"><hl:message key="rotulo.nome.correspondente"/></th>
                  <th scope="col"><hl:message key="rotulo.situacao"/></th>
                  <th scope="col"><hl:message key="rotulo.acoes"/></th>
                </tr>
              </thead>
              <tbody>
              <%=JspHelper.msgRstVazio(correspondentes.size()==0, 13, responsavel)%>
              <%
              Iterator<?> it = correspondentes.iterator();
              while (it.hasNext()) {
                CustomTransferObject correspondente = (CustomTransferObject)it.next();
                String cor_nome = (String)correspondente.getAttribute(Columns.COR_NOME);
                String cor_codigo = (String)correspondente.getAttribute(Columns.COR_CODIGO);
                String cor_identificador = (String)correspondente.getAttribute(Columns.COR_IDENTIFICADOR);
                String cor_ativo = correspondente.getAttribute(Columns.COR_ATIVO) != null ? correspondente.getAttribute(Columns.COR_ATIVO).toString() : CodedValues.STS_ATIVO.toString();
                
                String cor_nome_script = cor_nome.replaceAll("\'", "\\\\\\\'").replaceAll("\"", "");

                // Se cor_ativo for igual a 2: correspondente excluido, não listado
                if (!cor_ativo.equals(CodedValues.STS_INDISP.toString())) {
                %>  
                <tr>
                  <td><%=TextHelper.forHtmlContent(cor_identificador)%></td>
                  <td><%=TextHelper.forHtmlContent(cor_nome.toUpperCase())%></td>
                  <% String msgCorBloqueadoDesbloqueado = cor_ativo.equals(CodedValues.STS_ATIVO.toString()) ? ApplicationResourcesHelper.getMessage("rotulo.correspondente.satatus.desbloqueado", responsavel): ApplicationResourcesHelper.getMessage("rotulo.correspondente.satatus.bloqueado", responsavel); %>
                  <td <%=!cor_ativo.equals(CodedValues.STS_ATIVO.toString()) ? "class=\"block\"" : ""%>><%=TextHelper.forHtmlAttribute(msgCorBloqueadoDesbloqueado)%></td>
                  <td>
                    <div class="actions">
                      <div class="dropdown">
                        <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                          <div class="form-inline">
                            <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.mais.acoes"/>" aria-label="<hl:message key="rotulo.mais.acoes"/>"> <svg>
                                <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                            </span> 
                            <hl:message key="rotulo.botao.opcoes"/>
                          </div>
                        </a>
                        <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                          <% if (podeEditarCor) { %> 
                            <a class="dropdown-item" href="#no-back" onClick="BloquearEntidade(<%=TextHelper.forJavaScript(cor_ativo)%>, '<%=TextHelper.forJavaScript(cor_codigo)%>', 'COR', '../v3/manterCorrespondente?acao=bloquear&csa=<%=csa_codigo%>&<%=SynchronizerToken.generateToken4URL(request)%>', '<%=TextHelper.forJavaScript(cor_nome_script)%>')"><hl:message key="rotulo.acao.bloquear.desbloquear"/></a> 
                            <a class="dropdown-item" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(editaCorr + "&cor=" + cor_codigo)%>')"><hl:message key="rotulo.acoes.editar"/></a> 
                            <% if (podeExcluirCor) { %>
                            <a class="dropdown-item" href="#no-back" onClick="ExcluirEntidade('<%=TextHelper.forJavaScript(cor_codigo)%>', 'COR', '../v3/manterCorrespondente?acao=excluir&csa=<%=csa_codigo%>&<%=SynchronizerToken.generateToken4URL(request)%>', '<%=TextHelper.forJavaScript(cor_nome_script)%>')"><hl:message key="rotulo.acoes.excluir"/></a> 
                            <% } %>
                          <% } else if (podeConsultarCor) { %>
                            <a class="dropdown-item" href="#no-back" onClick="BloquearEntidade(<%=TextHelper.forJavaScript(cor_ativo)%>, '<%=TextHelper.forJavaScript(cor_codigo)%>', 'COR', '../v3/manterCorrespondente?acao=bloquear&csa=<%=csa_codigo%>&<%=SynchronizerToken.generateToken4URL(request)%>', '<%=TextHelper.forJavaScript(cor_nome_script)%>')" disabled><hl:message key="rotulo.acao.bloquear.desbloquear"/></a>
                            <% if (podeEditarEnderecoAcesso) { %>
                              <a class="dropdown-item" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(editaCorr + "&cor=" + cor_codigo)%>')"><hl:message key="rotulo.acoes.editar"/></a> 
                            <% } else { %>
                              <a class="dropdown-item" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(editaCorr + "&cor=" + cor_codigo)%>')"><hl:message key="rotulo.acoes.visualizar"/></a>
                            <% }
                          } %>
                          <% if (podeConsultarPerfilUsu) {%>
                            <a class="dropdown-item" href="#no-back" onClick="postData('../v3/listarPerfilCor?acao=listar&codigo=<%=TextHelper.forJavaScriptAttribute(cor_codigo)%>&titulo=<%=TextHelper.forJavaScript(cor_nome)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acao.listar.perfil.usuario"/></a>
                          <% } 
                          if (podeConsultarUsu) { %>
                            <a class="dropdown-item" href="#no-back" onClick="postData('../v3/listarUsuarioCor?acao=listar&codigo=<%=TextHelper.forJavaScriptAttribute(cor_codigo)%>&titulo=<%=TextHelper.encode64(cor_nome)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.usuarios"/></a>
                          <% } 
                          if (podeCriarUsu) { %>
                            <a class="dropdown-item" href="#no-back" onClick="postData('../v3/inserirUsuarioCor?acao=iniciar&operacao=inserir_cor&tipo=COR&codigo=<%=TextHelper.forJavaScriptAttribute(cor_codigo)%>&titulo=<%=TextHelper.encode64(cor_nome)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.novo.usuario"/></a>
                          <% } 
                          if (podeEditarCnvCor || podeConsultarCnvCor) { %>
                            <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterConvenioCorrespondente?acao=iniciar&csa=<%=TextHelper.forJavaScriptAttribute(csa_codigo)%>&cor=<%=TextHelper.forJavaScript(cor_codigo)%>&titulo=<%=TextHelper.forJavaScript(cor_nome)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.consultar.convenio"/></a>
                          <% } %>
                          <% if (podeEditarEnderecosCor) {%>
                                <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterEnderecosCorrespondente?acao=iniciar&COR_CODIGO=<%=TextHelper.forJavaScriptAttribute(cor_codigo)%>&titulo=<%=TextHelper.forJavaScript(titulo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.correspondente.editar.enderecos"/></a>
                          <% } %>
                        </div>
                      </div>
                    </div>
                  </td>
                </tr>
                <%
                  }
                }
                %>
              </tbody>
              <tfoot>
                <tr>
                  <td colspan="4"><hl:message key="mensagem.correspondente.lista.disponiveis"/> - <span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTitulo"))%></span>
                  </td>
                </tr>
              </tfoot>
            </table>
          </div>
          <div class="card-footer">
            <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
          </div>
        </div>
        <div class="btn-action">
          <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(canc)%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
        </div>
      </div>
    </div>
  </div>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript">
  var f0 = document.forms[0];
  </script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>