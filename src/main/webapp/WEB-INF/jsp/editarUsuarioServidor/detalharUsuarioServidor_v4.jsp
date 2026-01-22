<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.Date"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="java.util.List"%>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper"%>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken"%>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper"%>
<%@ page import="com.zetra.econsig.values.Columns"%>
<%@ page import="com.zetra.econsig.helper.texto.TransferObjectHelper"%>
<%@ page import="com.zetra.econsig.helper.web.v3.JspHelper"%>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />

<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
List hist = (List) request.getAttribute("hist");
CustomTransferObject servInfo = (CustomTransferObject) request.getAttribute("servInfo");

%>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="title">
   <hl:message key="rotulo.editar.usuario.titulo"/>
</c:set>
<c:set var="bodyContent">
      <div class="row">
        <div class="col-sm-12">
          <div class="card">
            <div class="card-header">
              <h2 class="card-header-title"><hl:message key="rotulo.dados.gerais"/></h2>
            </div>
            <div class="card-body">
              <dl class="row data-list">
              <%-- Utiliza a tag library DetalheServidorTag.java para exibir os dados do servidor --%>
              <% pageContext.setAttribute("servidor", servInfo); %>
              <hl:detalharServidorv4 name="servidor"/>
              <%-- Fim dos dados do servidor --%>
              </dl>
            </div>
          </div>
        </div>
        <div class="col-sm-12">
          <%
             if (hist.size() > 0) {
          %>
          <div class="card">
            <div class="card-header">
              <h2 class="card-header-title"><hl:message key="rotulo.servidor.usuario.detalhe.titulo"/></h2>
            </div>
            <div class="card-body table-responsive">
              <table class="table table-striped table-hover">
                <thead>
                  <tr>
                    <th scope="col"><hl:message key="rotulo.servidor.usuario.detalhe.data"/></th>
                    <th scope="col"><hl:message key="rotulo.servidor.usuario.detalhe.responsavel"/></th>
                    <th scope="col"><hl:message key="rotulo.servidor.usuario.detalhe.tipo"/></th>
                    <th scope="col"><hl:message key="rotulo.servidor.usuario.detalhe.descricao"/></th>
                    <th scope="col"><hl:message key="rotulo.servidor.usuario.detalhe.ip.acesso"/></th>
                  </tr>
                </thead>
                <tbody>
                <%
                    Iterator it = hist.iterator();
                     String ous_data, ous_responsavel, toc_descricao, ous_obs, ous_ip_acesso, tmoDescricao;
                     CustomTransferObject cto = null;
                     int i = 0;

                     while (it.hasNext()) {
                        cto = (CustomTransferObject) it.next();

                        cto = TransferObjectHelper.mascararUsuarioHistorico(cto, "USU_LOGIN_MOD", responsavel);

                        ous_data = DateHelper.toDateTimeString((Date) cto.getAttribute(Columns.OUS_DATA));
                        ous_responsavel = cto.getAttribute("USU_LOGIN_MOD").toString();
                        toc_descricao = cto.getAttribute(Columns.TOC_DESCRICAO).toString();
                        ous_obs = cto.getAttribute(Columns.OUS_OBS).toString();

                        tmoDescricao = cto.getAttribute(Columns.TMO_DESCRICAO) != null ?  cto.getAttribute(Columns.TMO_DESCRICAO).toString() : "";
                        if (!tmoDescricao.equals("")) {
                          String strComplemento = ous_obs.substring(ous_obs.indexOf(".") + 1, ous_obs.length());
                          ous_obs = ous_obs.substring(0, ous_obs.indexOf(".")) +
                                    "<br> " + ApplicationResourcesHelper.getMessage("rotulo.servidor.usuario.detalhe.motivo", responsavel) + ": " + tmoDescricao +
                                    "<br> " + ApplicationResourcesHelper.getMessage("rotulo.servidor.usuario.detalhe.observacao", responsavel) + ": " + strComplemento;
                        }

                        ous_ip_acesso = cto.getAttribute(Columns.OUS_IP_ACESSO) != null ? cto.getAttribute(Columns.OUS_IP_ACESSO).toString() : "";
                %>
                  <tr>
                    <td><%=TextHelper.forHtmlContent(ous_data)%></td>
                    <td><%=TextHelper.forHtmlContent(ous_responsavel)%></td>
                    <td><%=TextHelper.forHtmlContent(toc_descricao)%></td>
                    <td><%=TextHelper.forHtmlContentComTags(ous_obs)%></td>
                    <td><%=TextHelper.forHtmlContent(ous_ip_acesso)%></td>
                  </tr>
                <% } %>
                </tbody>
                <tfoot>
                  <tr>
                    <td colspan="5"><%=ApplicationResourcesHelper.getMessage("mensagem.listagem.historico.usuario.servidor", responsavel) + " - " %><span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTitulo"))%></span></td>
                  </tr>
                </tfoot>
              </table>
            </div>
            <div class="card-footer">
              <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
            </div>
          </div>
          <% } %>
          <div class="btn-action">
            <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.voltar"/></a>
          </div>
        </div>
      </div>

</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
</script>
</c:set>
<%-- Leiaute Fixo --%>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
