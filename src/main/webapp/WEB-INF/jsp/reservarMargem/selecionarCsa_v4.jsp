<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
List<TransferObject> consignatarias = (List<TransferObject>) request.getAttribute("listaConsignataria");

String rseCodigo = responsavel.getRseCodigo();
String rseMatricula = responsavel.getRseMatricula();
String orgCodigo = responsavel.getOrgCodigo();

String svcDescricao = JspHelper.verificaVarQryStr(request, "SVC_DESCRICAO");
String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
HashMap<String, TransferObject> hashCsaPermiteContato = (HashMap<String, TransferObject>) request.getAttribute("hashCsaPermiteContato");
boolean exibeTaxaJuros = request.getAttribute("exibeTaxaJuros") != null && (Boolean) request.getAttribute("exibeTaxaJuros");
boolean temCET = request.getAttribute("temCET") != null && (Boolean) request.getAttribute("temCET");
%>

<c:set var="title">
<hl:message key="rotulo.solicitar.consignacao.titulo"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-sistema"></use>
</c:set>
<c:set var="bodyContent">
      <div class="alert alert-warning" role="alert">
        <p class="mb-0"><%= ApplicationResourcesHelper.getMessage("mensagem.solicitar.consignacao.escolha.consignataria", responsavel, ApplicationResourcesHelper.getMessage("rotulo.botao.selecionar", responsavel))%>
       </p>
      </div>
      <div class="card">
        <div class="card-header hasIcon pl-3">
          <h2 class="card-header-title"><hl:message key="rotulo.resultado.pesquisa"/></h2>
        </div>
        <div class="card-body table-responsive p-0">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th> <hl:message key="rotulo.funcao.codigo"/></th>
                <th> <hl:message key="rotulo.consignataria.singular"/></th>
                <% if (exibeTaxaJuros && temCET) { %>
                <th scope="col"><hl:message key="rotulo.consignacao.cet"/></th>
                <% } else if (exibeTaxaJuros) { %>
                <th scope="col"><hl:message key="rotulo.consignacao.taxa.juros"/></th>
                <% } %>
                <th> <hl:message key="rotulo.acoes"/></th>
              </tr>
            </thead>
            <tbody>
            <% if (consignatarias == null || consignatarias.size() == 0) { %>
                <tr>
                  <td colspan="3"><hl:message key="mensagem.erro.solicitar.consignacao.nenhuma.consignataria"/></td>
                </tr>
                <% } else { %>
                <% String csa_whatsapp = "", csa_email_contato ="", csa_email = "", csa_email_usar = "", csa_contato_tel = "", tipo_contato = "", taxa = "";
                for (TransferObject csa : consignatarias) {
                  String csaCodigo = csa.getAttribute(Columns.CSA_CODIGO).toString();
                  String csaIdentificador = csa.getAttribute(Columns.CSA_IDENTIFICADOR).toString();
                  String csaNome = csa.getAttribute(Columns.CSA_NOME).toString();
                  if (exibeTaxaJuros) {
                      taxa = NumberHelper.format(((BigDecimal) csa.getAttribute(Columns.CFT_VLR)).doubleValue(), NumberHelper.getLang(), 2, 8);
                  }
                  if (hashCsaPermiteContato.get(csaCodigo) != null) {
                      TransferObject consignatariaContato = hashCsaPermiteContato.get(csaCodigo);
                      csa_whatsapp = (String) consignatariaContato.getAttribute(Columns.CSA_WHATSAPP);
                      csa_email_contato = (String) consignatariaContato.getAttribute(Columns.CSA_EMAIL_CONTATO);
                      csa_email = (String) consignatariaContato.getAttribute(Columns.CSA_EMAIL);
                      tipo_contato = (String) consignatariaContato.getAttribute(Columns.PCS_VLR);
                      csa_email_usar = !TextHelper.isNull(csa_email_contato) ? csa_email_contato : csa_email;
                      if(!TextHelper.isNull(csa_whatsapp)){
                          csa_whatsapp = LocaleHelper.formataCelular(csa_whatsapp);
                      }
                      csa_contato_tel = (String) consignatariaContato.getAttribute(Columns.CSA_TEL);
                  }
                %>
              <tr>
                <td> <%=TextHelper.forHtmlContent(csaIdentificador)%></td>
                <td> <%=TextHelper.forHtmlContent(csaNome.toUpperCase())%></td>
                <% if (exibeTaxaJuros) { %>
                <td><div class="p-2"><%=TextHelper.forHtmlContent(taxa)%><span>%</span></div></td>
                <% } %> 
                <td> <a href="#no-back" onClick="reservar('<%=TextHelper.forJavaScript(csaCodigo)%>'); return false;" alt="<hl:message key="rotulo.botao.selecionar"/>" title="<hl:message key="rotulo.botao.selecionar"/>"><hl:message key="rotulo.botao.selecionar"/></a>
                    <%if (!TextHelper.isNull(tipo_contato) && hashCsaPermiteContato.get(csaCodigo) != null){ %>
                      <%if (!TextHelper.isNull(csa_whatsapp) && CodedValues.TPA_CONTATOS_WHATSAPP.contains(tipo_contato)){ %>
                          <i class="fa fa-whatsapp icon-contato-ranking" onclick="openModalQRCode(1,'<%=csa_whatsapp%>')"></i>
                      <%} %>
                      <%if (!TextHelper.isNull(csa_email_usar) && CodedValues.TPA_CONTATOS_EMAIL.contains(tipo_contato)){ %>
                          <i class="fa fa-at icon-contato-ranking" onclick="openModalQRCode(2,'<%=csa_email_usar%>')"></i>
                      <%} %>
                      <%if (!TextHelper.isNull(csa_contato_tel) && CodedValues.TPA_CONTATOS_TELEFONE.contains(tipo_contato)){ %>
                          <i class="fa fa-phone icon-contato-ranking" onclick="openModalQRCode(3,'<%=csa_contato_tel%>')"></i>                              
                      <%} %>
                    <%} %>
                </td>
              </tr>
               <% } %> 
               <% } %> 
              </tbody>
            <tfoot>
              <tr>
                <td colspan="3"><hl:message key="rotulo.lote.listagem.consignatarias"/>
                </td>
              </tr>
            </tfoot>
          </table>
        </div>
      </div>
      <div class="btn-action">
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal')" alt="<hl:message key="rotulo.botao.cancelar"/>" title="<hl:message key="rotulo.botao.cancelar"/>"><hl:message key="rotulo.botao.cancelar"/></a>
      </div>
</c:set>
<c:set var="javascript">
<script>
function reservar(codigo) {
  var URL = '<%= TextHelper.forHtmlAttribute(request.getAttribute("acaoFormulario")) %>?acao=<%= TextHelper.forJavaScriptBlock(request.getAttribute("proximaAcao")) %>'
                + '&CSA_CODIGO=' + codigo
                + '&SVC_CODIGO=<%=TextHelper.forJavaScriptBlock(svcCodigo)%>'
                + '&RSE_CODIGO=<%=TextHelper.forJavaScriptBlock(rseCodigo)%>'
                + '&RSE_MATRICULA=<%=TextHelper.forJavaScriptBlock(rseMatricula)%>'
                + '&<%= SynchronizerToken.generateToken4URL(request) %>';

  postData(URL);
}
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>