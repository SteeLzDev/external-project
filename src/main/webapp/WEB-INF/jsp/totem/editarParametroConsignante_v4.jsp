<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="com.zetra.econsig.helper.usuario.LoginHelper"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

List<?> parametros = (List<?>) request.getAttribute("totemParamCse");

%>
<c:set var="title">
  <hl:message key="rotulo.parametro.consignante.titulo" />
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <form method="post" action="../v3/configurarTotem?acao=salvar&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title">
          <hl:message key="rotulo.editar.grid" />
        </h2>
      </div>
      <div class="card-body">
        <div class="legend">
          <%=TextHelper.forHtmlContent(LoginHelper.getCseNome(responsavel))%>
        </div>
        <%
        if (parametros != null && !parametros.isEmpty()) {
            String tpa_codigo, tpa_descricao, pce_valor, controle;
            CustomTransferObject next = null;
            Iterator<?> it = parametros.iterator();
            while (it.hasNext()) {
                next = (CustomTransferObject) it.next();
                tpa_codigo = next.getAttribute("TPA_CODIGO").toString();
                tpa_descricao = next.getAttribute("TPA_DESCRICAO").toString();
                pce_valor = next.getAttribute("PCE_VALOR") != null ? next.getAttribute("PCE_VALOR").toString() : "";
        %>
        <div class="row">
          <div class="col-sm-6 col-md-12 mb-2">
            <div class="form-group mb-0">
              <label for="<%=TextHelper.forHtmlContent(tpa_codigo)%>"><%=TextHelper.forHtmlContent(tpa_descricao)%></label>
            </div>
            <%
            	controle = JspHelper.montaValor(tpa_codigo, "ALFA", TextHelper.forHtmlContent(pce_valor), true, null, -1, 400, "form-control", null, null);
            %>
            <%=controle%>
          </div>
        </div>
        <%
            }
        }
        %>
      </div>
    </div>
  </form>
  <div class="row">
    <div class="col-sm-12">
      <div class="btn-action">
        <a class="btn btn-outline-danger" href="#no-back"
          onClick="postData('../v3/carregarPrincipal?mostraMensagem=true&limitaMsg=true'); return false;">        
          <hl:message key="rotulo.acoes.cancelar" />
        </a> 
        <%
        if (parametros != null && !parametros.isEmpty()) {
        %>
        <a class="btn btn-primary" href="#no-back"
          onClick="f0.submit(); return false;"> <hl:message
            key="rotulo.botao.salvar" />
        </a>
        <%
        }
        %>
      </div>
    </div>
  </div>

</c:set>
<c:set var="javascript">
  <script type="text/JavaScript">
      f0 = document.forms[0];
      function formLoad() {
        focusFirstField();
      }
      window.onload = formLoad;
    </script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>