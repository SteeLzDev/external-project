<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t"     tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl"    uri="/html-lib" %>
<%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.TransferObject" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
String nivelAtual = (String) request.getAttribute("nivelAtual");

// Lista com os níveis de segurança (código e descrição)
List<TransferObject> niveisSeguranca = (List<TransferObject>) request.getAttribute("niveisSeguranca");

// Lista com os detalhes sobre o nível de segurança
List<Map<String, String>> detalheNiveisSegurancaParamSist = (List<Map<String, String>>) request.getAttribute("detalheNiveisSegurancaParamSist");
%>
<c:set var="title">
  <hl:message key="rotulo.nivel.seguranca.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-home"></use>
</c:set>
<c:set var="bodyContent">
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.nivel.seguranca.titulo"/></h2>
    </div>
    <div class="card-body table-responsive p-0">
      <div class="alert alert-warning m-0" role="alert">
        <p class="mb-0"><hl:message key="rotulo.nivel.seguranca.atual"/> <%=TextHelper.forHtmlContent(nivelAtual)%></p>
      </div>
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th><hl:message key="rotulo.nivel.seguranca.parametro"/></th>
            <th><hl:message key="rotulo.nivel.seguranca.valor.atual"/></th>
            <%
              Iterator it = niveisSeguranca.iterator();
              while (it.hasNext()) {
                  TransferObject to = (TransferObject) it.next();
                  String nsgDescricao = to.getAttribute(Columns.NSG_DESCRICAO).toString();
              %>
                <th><hl:message key="rotulo.nivel.seguranca.nivel"/> <%=TextHelper.forHtmlContent(nsgDescricao)%></th>
            <% } %>
          </tr>
        </thead>
        <tbody>
          <%
          Iterator it2 = detalheNiveisSegurancaParamSist.iterator();
          while (it2.hasNext()) {
            Map valoresParam = (Map) it2.next();
            String codigo = (String) valoresParam.get("CODIGO");
            String descricao = (String) valoresParam.get("DESCRICAO");
            String valorAtual = (String) valoresParam.get("VALOR_ATUAL");
          
          %>
          <tr>
            <td><%=TextHelper.forHtmlContent( descricao )%><!-- <%=TextHelper.forHtmlContent( codigo )%> --></td>
            <td><span class="textweight-bold"><%=TextHelper.forHtmlContent( valorAtual )%></span></td>
            <%
            Iterator it3 = niveisSeguranca.iterator();
            while (it3.hasNext()) {
                TransferObject to = (TransferObject) it3.next();
                String nsgDescricao = to.getAttribute(Columns.NSG_DESCRICAO).toString();
                String valorEsperado = (String) valoresParam.get(nsgDescricao);
                String valorOk = (String) valoresParam.get(nsgDescricao + " OK?");
                String corFonte = (valorOk  != null && valorOk.equals("S")) ? "blue" : "red";
            %>
              <td><font color="<%=TextHelper.forHtmlAttribute(corFonte)%>"><%=TextHelper.forHtmlContent( valorEsperado )%></font></td>
            <% } %>
          </tr>
          <%
            }
          %>
        </tbody>
        <tfoot>
          <tr>
            <td colspan="6"><hl:message key="rotulo.nivel.seguranca.titulo.listagem"/></td>
          </tr>
        </tfoot>
      </table>
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>