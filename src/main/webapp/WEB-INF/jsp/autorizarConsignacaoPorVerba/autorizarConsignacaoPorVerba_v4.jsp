<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

TransferObject servidor = (TransferObject) request.getAttribute("servidor");
List<?> lstConsignacao = (List<?>) request.getAttribute("lstConsignacao");
String rotuloPrazoIndet = ApplicationResourcesHelper.getMessage("rotulo.indeterminado.abreviado", responsavel);
boolean confirmouAutorizacao = (boolean) request.getAttribute("confirmouAutorizacao");
%>
<c:set var="title">
  <%= TextHelper.forHtml(request.getAttribute("tituloPagina")) %>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
    <div class="row d-print-none">
      <div class="col-sm-12 col-md-12 mb-2">
        <div class="float-end">
          <button id="acoes" class="btn btn-primary" type="submit" onClick="imprimir()"><hl:message key="rotulo.botao.imprimir"/></button>
        </div>
      </div>
    </div>
    <div class="card">
      <div class="card-header hasIcon">
        <span class="card-header-icon">
          <svg width="24">
              <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-consignacao"></use>
          </svg>
        </span>
        <h2 class="card-header-title">
           <hl:message key="rotulo.consignacao.plural"/>
        </h2>
      </div>
      <div class="card-body table-responsive">
        <form action="../v3/autorizarConsignacaoPorVerba?acao=autorizar&<%=SynchronizerToken.generateToken4URL(request)%>" method="post" name="form1">
          <table class="table table-striped table-hover">
            <thead>
                <tr>
                <th class="text-center"><hl:message key="rotulo.tabela.autorizar.consignacao.aceito"/></th>
                <th class="text-center"><hl:message key="rotulo.tabela.autorizar.consignacao.nao.aceito"/></th>
                <th><hl:message key="rotulo.consignacao.numero.ade.abreviado"/></th>
                <th><hl:message key="rotulo.consignataria.singular"/></th>
                <th><hl:message key="rotulo.servico.singular"/></th>
                <th><hl:message key="rotulo.consignacao.data.inclusao"/></th>
                <th><hl:message key="rotulo.consignacao.valor.parcela.abreviado"/></th>
                <th><hl:message key="rotulo.consignacao.prazo.abreviado"/></th>
                <th><hl:message key="rotulo.consignacao.pagas"/></th>
                <th><hl:message key="rotulo.consignacao.status"/></th>
                </tr>
            </thead>
            <tbody>
                <% Iterator<?> it = lstConsignacao.iterator();
                while (it.hasNext()) {
                    TransferObject consignacao = (TransferObject) it.next();
                    String adeCodigo = (String) consignacao.getAttribute(Columns.ADE_CODIGO);
                    String dadValor = (String) consignacao.getAttribute(CodedValues.TDA_AUTORIZA_DESCONTO);
                %>
                <tr>
                <td class="text-center"><input type="radio" name="rdb_<%=TextHelper.forHtmlAttribute(adeCodigo)%>" value="<%=CodedValues.TDA_SIM%>" <%= CodedValues.TDA_SIM.equals(dadValor) ? " checked" : "" %><%= confirmouAutorizacao ? " disabled" : "" %>/></td>
                <td class="text-center"><input type="radio" name="rdb_<%=TextHelper.forHtmlAttribute(adeCodigo)%>" value="<%=CodedValues.TDA_NAO%>" <%= CodedValues.TDA_NAO.equals(dadValor) ? " checked" : "" %><%= confirmouAutorizacao ? " disabled" : "" %>/></td>
                <td><%=TextHelper.forHtmlContent(consignacao.getAttribute(Columns.ADE_NUMERO))%></td>
                <td><%=TextHelper.forHtmlContent(consignacao.getAttribute(Columns.CSA_IDENTIFICADOR) + " - " + consignacao.getAttribute(Columns.CSA_NOME))%></td>
                <td><%=TextHelper.forHtmlContent(consignacao.getAttribute(Columns.CNV_COD_VERBA) + " - " + consignacao.getAttribute(Columns.SVC_DESCRICAO))%></td>
                <td><%=TextHelper.forHtmlContent(DateHelper.format((Date) consignacao.getAttribute(Columns.ADE_DATA), LocaleHelper.getDateTimePattern()))%></td>
                <td class="text-end"><%=TextHelper.forHtmlContent(ParamSvcTO.getDescricaoTpsTipoVlr((String) consignacao.getAttribute(Columns.ADE_TIPO_VLR)))%> <%=TextHelper.forHtmlContent(NumberHelper.format(Double.parseDouble(consignacao.getAttribute(Columns.ADE_VLR).toString()), NumberHelper.getLang()))%></td>
                <td class="text-end"><%=TextHelper.forHtmlContent(consignacao.getAttribute(Columns.ADE_PRAZO) != null ? consignacao.getAttribute(Columns.ADE_PRAZO).toString() : rotuloPrazoIndet)%></td>
                <td class="text-end"><%=TextHelper.forHtmlContent(consignacao.getAttribute(Columns.ADE_PRD_PAGAS) != null ? consignacao.getAttribute(Columns.ADE_PRD_PAGAS).toString() : "0")%></td>
                <td><%=TextHelper.forHtmlContent(consignacao.getAttribute(Columns.SAD_DESCRICAO))%></td>
                </tr>
                <%
                }
                %>  
            </tbody>
            <tfoot>
              <tr>
                <td colspan="13"><%=ApplicationResourcesHelper.getMessage("mensagem.info.listagem.confirmar.autorizacao", responsavel)%></td>
              </tr>
            </tfoot>
          </table>
      </div>
        <div class="card-footer">
        </div>
      </div>
      <div>
        <input type="checkbox" name="aceiteTermo" id="aceiteTermo" value="1" <%= confirmouAutorizacao ? "checked disabled" : "" %>/>&nbsp;<span class="font-weight-bold"><hl:message key="mensagem.info.autorizar.estou.ciente.escolhas"/></span>
      </div>
    </form>
    <div class="btn-action">
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.voltar"/></a>
      <% if (!confirmouAutorizacao) { %>
      <a class="btn btn-primary" href="#no-back" onClick="confirmar()"><hl:message key="rotulo.botao.confirmar"/></a>
      <% } %>
    </div>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript">
    function confirmar() {
        if ($("#aceiteTermo").is(":checked")) {
            if ($('input[type="radio"]:checked').length * 2 == $('input[type="radio"]').length) {
                document.forms[0].submit();
            } else {
                alert('<hl:message key="mensagem.info.autorizar.verificar.todas"/>');
                return false;
            }
        } else {
            alert('<hl:message key="mensagem.info.autorizar.confirmar.autorizacao"/>');
            return false;
        }
    }

    function imprimir() {
        window.print();
    }
  </script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>