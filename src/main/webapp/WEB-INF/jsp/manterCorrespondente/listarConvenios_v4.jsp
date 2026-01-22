<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@page import="com.zetra.econsig.dto.TransferObject"%>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String csa_codigo = (String) request.getAttribute("csaCodigo");
String cor_codigo = (String) request.getAttribute("corCodigo");
String cor_nome = (String) request.getAttribute("corNome");
String svc_codigo = (String) request.getAttribute("svcCodigo");
String svc_descricao = (String) request.getAttribute("svcDescricao");
String svc_identificador = (String) request.getAttribute("svcIdentificador");
List<TransferObject> convenios = (List<TransferObject>) request.getAttribute("convenios");
boolean podeEditarCnvCor = (boolean) request.getAttribute("podeEditarCnvCor");
String titulo = cor_nome.equals("") ? ApplicationResourcesHelper.getMessage("rotulo.manutencao.servicos.titulo", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.manutencao.servicos.existente.titulo", responsavel, cor_nome);
%>
<c:set var="title">
  <%=titulo%>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <form method="post" action="../v3/manterConvenioCorrespondente?acao=salvar&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
    <input type="hidden" name="svc_codigo" value="<%=TextHelper.forHtmlAttribute(svc_codigo)%>">
    <input type="hidden" name="csa" value="<%=TextHelper.forHtmlAttribute(csa_codigo)%>">
    <input type="hidden" name="cor" value="<%=TextHelper.forHtmlAttribute(cor_codigo)%>">
    <div class="row firefox-print-fix">
      <div class="col-sm-7 col-md-12">
        <div class="card">
          <div class="card-header hasIcon pl-3">
            <h2 class="card-header-title"><hl:message key="rotulo.convenio.plural"/> : <%= TextHelper.forHtmlContent(svc_descricao) + " - " + TextHelper.forHtmlContent(svc_identificador) %></h2>
          </div>
          <div class="card-body table-responsive ">
            <% if (podeEditarCnvCor) { %>
            <div class="row mr-0 pl-3 pr-3 pt-2 pb-0 d-print-none">
              <div class="col-sm-12">
                <div class="form-group mb-1">
                  <span><hl:message key="mensagem.selecione.convenio.habilitar"/></span>
                </div>
              </div>
            </div>
            <% } %>
            <div class="pt-3 table-responsive">
              <table class="table table-striped table-hover">
                <thead>
                  <tr>
                    <% if (podeEditarCnvCor) { %>
                    <th nowrap scope="col" width="3%">
                      <div class="form-check"><input type="checkbox" class="form-check-input ml-0" id="checkAll" name="checkAll" onClick="checkUnCheckAll();" data-bs-toggle="tooltip" data-original-title='<hl:message key="rotulo.acoes.selecionar"/>' alt='<hl:message key="rotulo.acoes.selecionar"/>' title='<hl:message key="rotulo.acoes.selecionar"/>' <%=(String)(podeEditarCnvCor ? "" : " disabled ")%>></div>
                    </th>
                    <% } %>
                    <th scope="col"><hl:message key="rotulo.orgao.nome"/></th>
                    <th nowrap><hl:message key="rotulo.acoes"/></th>
                  </tr>
                </thead>
                <tbody>
                  <c:choose>
                    <c:when test="<%=convenios != null && !convenios.isEmpty()%>">
                      <%
                        TransferObject convenio = null;
                        String nome = "", codigo = "", scv_codigo = "";
                        Iterator<TransferObject> it = convenios.iterator();
                        while (it.hasNext()) {
                          convenio = it.next();
                          nome = convenio.getAttribute(Columns.EST_IDENTIFICADOR).toString() + " - "
                               + convenio.getAttribute(Columns.ORG_NOME).toString() + " - "
                               + convenio.getAttribute(Columns.ORG_IDENTIFICADOR).toString();
                          codigo = convenio.getAttribute(Columns.CNV_CODIGO).toString();
                          scv_codigo = convenio.getAttribute("STATUS").toString();
                      %>
                        <tr>
                          <% if (podeEditarCnvCor) { %>
                            <td nowrap class="ocultarColuna" data-bs-toggle="tooltip" aria-label="<%=ApplicationResourcesHelper.getMessage("rotulo.acoes.selecionar", responsavel)%>" title="<%=ApplicationResourcesHelper.getMessage("rotulo.acoes.selecionar", responsavel)%>" data-original-title="<%=ApplicationResourcesHelper.getMessage("rotulo.acoes.selecionar", responsavel)%>">
                              <div class="form-check">
                                <input type="checkbox" class="form-check-input ml-0" value="<%=TextHelper.forHtmlAttribute(codigo)%>" 
                                id="<%=TextHelper.forHtmlAttribute(codigo)%>" name="CNV_CODIGO"
                                data-exibe-msg2="0" data-usa-link2="0" <%=(String)(scv_codigo.equals(CodedValues.SCV_ATIVO) ? " checked " : "")%> <%=(String)(podeEditarCnvCor ? "" : " disabled " )%>>
                              </div>
                            </td>
                            <td class="selecionarColuna selecionarLinha"><%=TextHelper.forHtmlContent(nome)%></td>
                            <td class="selecionarColuna selecionarLinha"><a href="javascript:void(0);"><hl:message key="rotulo.acoes.selecionar"/></a></td>
                          <% } else { %>
                            <td><%=TextHelper.forHtmlContent(nome)%></td>
                            <td><%=TextHelper.forHtmlContent(ApplicationResourcesHelper.getMessage((scv_codigo.equals(CodedValues.SCV_ATIVO) ? "rotulo.lista.servico.desbloqueado" : "rotulo.lista.servico.bloqueado"), responsavel))%></td>
                          <% } %>
                        </tr>
                      <% 
                        }
                      %>
                    </c:when>
                    <c:otherwise>
                      <tr class="lp">
                        <td colspan="13"><hl:message key="mensagem.erro.nenhum.registro.encontrado"/></td>
                      </tr>
                    </c:otherwise>
                  </c:choose>
                </tbody>
                <tfoot>
                  <tr><td colspan="5"><%=ApplicationResourcesHelper.getMessage("mensagem.listagem.convenios", responsavel) + " - " %><span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTitulo"))%></span></td></tr>
                </tfoot>
              </table>
            </div> 
          </div>
          <div class="card-footer">
          </div>
        </div>
      </div>
    </div>
    <div class="btn-action">
      <c:choose>
        <c:when test="<%= convenios != null && convenios.size() > 0 && podeEditarCnvCor%>">
          <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;" id="btnVoltar"><hl:message key="rotulo.botao.cancelar"/></a>
          <a class="btn btn-primary" href="#no-back" onClick="f0.submit(); return false;" id="btnSalvar"><hl:message key="rotulo.botao.salvar"/></a>
        </c:when>
        <c:otherwise>
          <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;" id="btnVoltar"><hl:message key="rotulo.botao.voltar"/></a>
        </c:otherwise>
      </c:choose>
    </div>
  </form>
</c:set>
<c:set var="javascript">
  <script src="../js/colunaCheckbox.js?<hl:message key="release.tag"/>"></script>
  <script type="text/JavaScript" src="../js/scripts_2810.js"></script>
  <script type="text/JavaScript">
    f0 = document.forms[0];
  </script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>