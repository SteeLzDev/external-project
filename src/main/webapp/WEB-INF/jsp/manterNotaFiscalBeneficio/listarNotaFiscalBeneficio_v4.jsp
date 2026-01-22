<%--
* <p>Title: ManterNotaFiscalBeneficioWebController</p>
* <p>Description: Contem formulario de reajuste de contratos</p>
* <p>Copyright: Copyright (c) 2005</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.math.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.persistence.entity.FaturamentoBeneficioNf"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

List<?> nfList = (List<?>) request.getAttribute("nfList");
String faturamentoCodigo = (String) request.getParameter("faturamentoCodigo");

%>

<c:set var="title">
  <hl:message key="rotulo.notafiscal.faturamento.beneficio.titulo" />
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
    <div class="col-sm-12 col-md-12 mb-2">
      <div class="float-end">
        <div class="btn-action">
          <a class="btn btn-primary" href="#no-back" onClick="postData('../v3/manterNotaFiscalBeneficio?acao=editar&faturamentoCodigo=<%=TextHelper.forJavaScript(faturamentoCodigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>')"><hl:message key="rotulo.notafiscal.faturamento.beneficio.novo"/></a>
        </div>
      </div>
    </div>
  </div>
  <div class="card mb-2">
    <div class="card-header">
      <h2 class="card-header-title">
        <hl:message key="rotulo.notafiscal.faturamento.beneficio.lista" />
      </h2>
    </div>
    <div class="card-body p-0 table-responsive ">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th><hl:message key="rotulo.notafiscal.faturamento.beneficio.codigo.contrato" /></th>
            <th><hl:message key="rotulo.notafiscal.faturamento.beneficio.tipo.nota.fiscal" /></th>
            <th><hl:message key="rotulo.notafiscal.faturamento.beneficio.numero.nf" /></th>
            <th><hl:message key="rotulo.notafiscal.faturamento.beneficio.numero.titulo" /></th>
            <th><hl:message key="rotulo.notafiscal.faturamento.beneficio.data.vencimento" /></th>
            <th><hl:message key="rotulo.faturamento.beneficios.acoes" /></th>
          </tr>
        </thead>
        <tbody>
          <%
            Iterator <?> it = nfList.iterator();
                    while (it.hasNext()) {
                        FaturamentoBeneficioNf fnf = (FaturamentoBeneficioNf) it.next();
           %>
          <tr>
            <td><%=TextHelper.forHtmlContent(fnf.getFnfCodigoContrato())%></td>
            <td><%=TextHelper.forHtmlContent(fnf.getTipoNotaFiscal().getTnfDescricao())%></td>
            <td><%=fnf.getFnfNumeroNf() != null ? TextHelper.forHtmlContent(fnf.getFnfNumeroNf()) : ""%></td>
            <td><%=fnf.getFnfNumeroTitulo() != null ? TextHelper.forHtmlContent(fnf.getFnfNumeroTitulo()) : ""%></td>
            <td><%=fnf.getFnfDataVencimento() != null ? TextHelper.forHtmlContent(DateHelper.toDateString(fnf.getFnfDataVencimento())) : ""%></td>
            <td>
              <div class="actions">
                <div class="dropdown">
                  <a class="dropdown-toggle ico-action" href="#"
                    role="button" id="userMenu" data-bs-toggle="dropdown"
                    aria-haspopup="true" aria-expanded="false">
                    <div class="form-inline">
                      <span class="mr-1" data-bs-toggle="tooltip"
                        aria-label='<hl:message key="rotulo.faturamento.beneficios.opcoes" />'
                        title=""
                        data-original-title='<hl:message key="rotulo.faturamento.beneficios.opcoes" />'><svg>
                                        <use
                            xmlns:xlink="http://www.w3.org/1999/xlink"
                            xlink:href="#i-engrenagem"></use></svg></span>
                      <hl:message
                        key="rotulo.faturamento.beneficios.opcoes" />
                    </div>
                  </a>
                  <div class="dropdown-menu dropdown-menu-right"
                    aria-labelledby="userMenu">
                    <a class="dropdown-item" style="cursor: pointer;"
                      onClick="postData('../v3/manterNotaFiscalBeneficio?acao=editar&notaFiscalCodigo=<%=TextHelper.forJavaScript(fnf.getFnfCodigo())%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"
                      aria-label='<hl:message key="rotulo.notafiscal.faturamento.beneficio.editar" />'><hl:message
                        key="rotulo.notafiscal.faturamento.beneficio.editar" /></a>
                    <a class="dropdown-item" style="cursor: pointer;"
                      onClick="confirmar('<%=TextHelper.forJavaScript(fnf.getFnfCodigo())%>'); return false;"
                      aria-label='<hl:message key="rotulo.notafiscal.faturamento.beneficio.excluir" />'><hl:message
                        key="rotulo.notafiscal.faturamento.beneficio.excluir" /></a>
                  </div>
                </div>
              </div>
            </td>
          </tr>
          <% } %>
        </tbody>
        <tfoot>
          <tr>
            <td colspan="5"><%=ApplicationResourcesHelper.getMessage("rotulo.faturamento.beneficios.listagem", responsavel)%></td>
          </tr>
        </tfoot>
      </table>
    </div>
  </div>
  <div class="float-end">
    <div class="btn-action">
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')" id="btnVoltar"><hl:message key="rotulo.botao.voltar" /></a>
    </div>
  </div>
</c:set>
<c:set var="javascript">

<script type="text/javascript">

function confirmar (id) {
	if (confirm('<%=ApplicationResourcesHelper.getMessage("mensagem.notafiscal.faturamento.beneficio.deseja.excluir", responsavel)%>')) {
		postData('../v3/manterNotaFiscalBeneficio?acao=excluir&notaFiscalCodigo=' + id + '&_skip_history_=true&faturamentoCodigo=<%=TextHelper.forJavaScript(faturamentoCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');
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


