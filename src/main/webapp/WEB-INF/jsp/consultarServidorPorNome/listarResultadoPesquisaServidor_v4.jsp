<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String tituloPagina = (String) request.getAttribute("tituloPagina");
String linkRet = (String) request.getAttribute("linkRet");

boolean omiteCpfServidor = ParamSist.paramEquals(CodedValues.TPC_OMITE_CPF_SERVIDOR, CodedValues.TPC_SIM, responsavel);
List<TransferObject> servicos = (List<TransferObject>) request.getAttribute("lstServico");
List<TransferObject> lstServidor = (List<TransferObject>) request.getAttribute("lstServidor");
boolean senhaObrigatoria = !TextHelper.isNull(request.getAttribute("senhaObrigatoria"));
%>

<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="title">
   ${tituloPagina}
</c:set>
<c:set var="bodyContent">

<div class="card">
  <div class="card-header hasIcon pl-3">
    <h2 class="card-header-title"><hl:message key="rotulo.resultado.pesquisa"/></h2>
  </div>
  <div class="card-body table-responsive ">
    <table class="table table-striped table-hover">
      <thead>
        <tr>
          <th scope="col"><hl:message key="rotulo.servidor.nome"/></th>
          <% if (!omiteCpfServidor) { %>
          <th scope="col"><hl:message key="rotulo.servidor.cpf"/></th>
          <% } %>
          <th scope="col" class="pr-1"><hl:message key="rotulo.servidor.dataNasc"/></th>
          <th scope="col"><hl:message key="rotulo.estabelecimento.singular"/></th>
          <th scope="col"><hl:message key="rotulo.orgao.singular"/></th>
          <th scope="col"><hl:message key="rotulo.servidor.matricula"/></th>
          <th scope="col"><hl:message key="rotulo.servidor.salario"/></th>
          <th scope="col"><hl:message key="rotulo.servidor.proventos"/></th>
          <th scope="col"><hl:message key="rotulo.registro.servidor.status"/></th>
          <th scope="col"><hl:message key="rotulo.acoes"/></th>  
        </tr>
      </thead>
      <tbody>
      <%=JspHelper.msgRstVazio(lstServidor == null || lstServidor.size() == 0, "12", "lp")%>
      <%
        for (TransferObject serTO : lstServidor) {
            String serNome = (String)serTO.getAttribute(Columns.SER_NOME);
            String rseSalario = TextHelper.forHtmlContent(!TextHelper.isNull(serTO.getAttribute(Columns.RSE_SALARIO)) ? NumberHelper.format(((BigDecimal) serTO.getAttribute(Columns.RSE_SALARIO)).doubleValue(), NumberHelper.getLang()) : "");
            String rseProventos = TextHelper.forHtmlContent(!TextHelper.isNull(serTO.getAttribute(Columns.RSE_PROVENTOS)) ? NumberHelper.format(((BigDecimal) serTO.getAttribute(Columns.RSE_PROVENTOS)).doubleValue(), NumberHelper.getLang()) : "");
      %>
      <tr>
          <td><%=TextHelper.forHtmlContent(serNome)%></td>
          <% if (!omiteCpfServidor) { %>
          <td><%=TextHelper.forHtmlContent(serTO.getAttribute(Columns.SER_CPF))%></td>
          <% } %>
          <td><%=TextHelper.forHtmlContent(DateHelper.format((Date) serTO.getAttribute(Columns.SER_DATA_NASC), LocaleHelper.getDatePattern()))%></td>
          <td><%=TextHelper.forHtmlContent(serTO.getAttribute(Columns.EST_NOME))%></td>
          <td><%=TextHelper.forHtmlContent(serTO.getAttribute(Columns.ORG_NOME))%></td>
          <td><%=TextHelper.forHtmlContent(serTO.getAttribute(Columns.RSE_MATRICULA))%></td>
          <td><hl:message key="rotulo.moeda"/>&nbsp;<%=TextHelper.forHtmlContent(rseSalario)%></td>
          <td><hl:message key="rotulo.moeda"/>&nbsp;<%=TextHelper.forHtmlContent(rseProventos)%></td>
          <td><%=TextHelper.forHtmlContent(serTO.getAttribute(Columns.SRS_DESCRICAO))%></td>
          <td>
            <div class="actions">
              <div class="dropdown">
                <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                  <div class="form-inline">
                    <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.mais.acoes"/>" aria-label="<hl:message key="rotulo.mais.acoes"/>"><svg><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg></span>
                    <hl:message key="rotulo.botao.opcoes"/>
                  </div>
                </a>
                <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                  <% if (responsavel.temPermissao(CodedValues.FUN_CONS_MARGEM)) { %>
                    <a href="javascript:void(0);" class="dropdown-item" onclick="doIt('consultarMargem', '<%=TextHelper.forHtmlAttribute(serTO.getAttribute(Columns.RSE_CODIGO))%>', '<%=TextHelper.forHtmlAttribute(serTO.getAttribute(Columns.SER_CODIGO))%>','', '<%=TextHelper.forHtmlAttribute(serTO.getAttribute(Columns.RSE_MATRICULA))%>')"><hl:message key="rotulo.consultar.margem.titulo"/></a>
                  <% } %>
                  <% if (responsavel.temPermissao(CodedValues.FUN_CONSULTAR_SERVIDOR)) { %>
                    <a href="javascript:void(0);" class="dropdown-item" onclick="doIt('edtServidor', '<%=TextHelper.forHtmlAttribute(serTO.getAttribute(Columns.RSE_CODIGO))%>', '<%=TextHelper.forHtmlAttribute(serTO.getAttribute(Columns.SER_CODIGO))%>')"><hl:message key="rotulo.editar.servidor.titulo"/></a>
                  <% } %>
                  <% if (responsavel.temPermissao(CodedValues.FUN_CONS_USU_SERVIDORES)) { %>
                    <a href="javascript:void(0);" class="dropdown-item" onclick="doIt('edtUsuServidor', '', '', '', '<%=TextHelper.forHtmlAttribute(serTO.getAttribute(Columns.RSE_MATRICULA))%>', '<%=TextHelper.forHtmlAttribute(serTO.getAttribute(Columns.SER_CPF))%>')"><hl:message key="rotulo.editar.usuario.servidor.titulo"/></a>
                  <% } %>
                  <% if (responsavel.temPermissao(CodedValues.FUN_CONS_CONSIGNACAO)) { %>
                    <a href="javascript:void(0);" class="dropdown-item" onclick="doIt('consConsignacao', '<%=TextHelper.forHtmlAttribute(serTO.getAttribute(Columns.RSE_CODIGO))%>', '<%=TextHelper.forHtmlAttribute(serTO.getAttribute(Columns.SER_CODIGO))%>')"><hl:message key="rotulo.consultar.consignacao.titulo"/></a>
                  <% } %>
                  <% if (responsavel.temPermissao(CodedValues.FUN_EDT_STATUS_REGISTRO_SERVIDOR)) { %>
                    <a href="javascript:void(0);" class="dropdown-item" onclick="doIt('edtSrsServidor', '<%=TextHelper.forHtmlAttribute(serTO.getAttribute(Columns.RSE_CODIGO))%>', '<%=TextHelper.forHtmlAttribute(serTO.getAttribute(Columns.SER_CODIGO))%>')"><hl:message key="rotulo.editar.status.servidor.titulo"/></a>
                  <% } %>
                  <% if (responsavel.isCsaCor() && responsavel.temPermissao(CodedValues.FUN_RES_MARGEM)) {
                       if (servicos != null && !servicos.isEmpty()) {
                           for (TransferObject svcTO : servicos) {
                  %>
                    <a href="javascript:void(0);" class="dropdown-item" onclick="doIt('reservarMargem', '<%=TextHelper.forHtmlAttribute(serTO.getAttribute(Columns.RSE_CODIGO))%>', '<%=TextHelper.forHtmlAttribute(serTO.getAttribute(Columns.SER_CODIGO))%>', '<%=TextHelper.forHtmlAttribute(svcTO.getAttribute(Columns.SVC_CODIGO))%>','<%=TextHelper.forHtmlAttribute(serTO.getAttribute(Columns.RSE_MATRICULA))%>')"><hl:message key="rotulo.reservar.margem.titulo"/>&nbsp;<%=TextHelper.forHtmlAttribute(svcTO.getAttribute(Columns.SVC_DESCRICAO))%></a>
                  <%
                           }
                       }
                    } 
                  %>
                  <% if (responsavel.isCsaCor() && responsavel.temPermissao(CodedValues.FUN_INCLUIR_CONSIGNACAO)) {
                       if (servicos != null && !servicos.isEmpty()) {
                           for (TransferObject svcTO : servicos) {
                  %>
                    <a href="javascript:void(0);" class="dropdown-item" onclick="doIt('incluirConsignacao', '<%=TextHelper.forHtmlAttribute(serTO.getAttribute(Columns.RSE_CODIGO))%>', '<%=TextHelper.forHtmlAttribute(serTO.getAttribute(Columns.SER_CODIGO))%>', '<%=TextHelper.forHtmlAttribute(svcTO.getAttribute(Columns.SVC_CODIGO))%>')"><hl:message key="rotulo.incluir.consignacao.titulo"/>&nbsp;<%=TextHelper.forHtmlAttribute(svcTO.getAttribute(Columns.SVC_DESCRICAO))%></a>
                  <%      }
                       }
                    } 
                  %> 
                  
                </div>
              </div>
            </div>
          </td>
        </tr>
        <%   
            }
        %>
       </tbody>
       <tfoot>
          <tr>
              <td colspan="10">
                <hl:message key="mensagem.listagem.servidor.data" arg0="<%=DateHelper.toDateTimeString(DateHelper.getSystemDatetime())%>" />
                <span class="font-italic"> - <%=request.getAttribute("_paginacaoSubTitulo")%></span>
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
  <a class="btn btn-outline-danger" href="javascript:void(0);" onClick="postData('../v3/pesquisarServidor?acao=iniciar'); return false;"><hl:message key="rotulo.botao.cancelar"/></a> 
</div>

</c:set>
<c:set var="javascript">

<script type="text/JavaScript">
  function doIt(op, rse, ser, svc, matricula, cpf) {
    var link = '';
    if (op == 'consultarMargem') {
      <% if(senhaObrigatoria) {%>
           link = '../v3/consultarMargem?acao=iniciar&RSE_MATRICULA='+matricula+'&ADE_VLR=1&linkRetHistoricoFluxo=<%=linkRet%>';
      <% } else { %>
           link = '../v3/consultarMargem?acao=consultar&ADE_VLR=1&linkRetHistoricoFluxo=<%=linkRet%>';
      <% } %>
    } else if (op == 'edtServidor') {
      link = '../v3/consultarServidor?acao=consultar&linkRet=<%=linkRet%>';
    } else if (op == 'edtUsuServidor') {
      link = '../v3/listarUsuarioServidor?acao=pesquisarServidor&rse_matricula';
    } else if (op == 'consConsignacao') {
      link = '../v3/consultarConsignacao?acao=pesquisarConsignacao&linkRetHistoricoFluxo=<%=linkRet%>';
    } else if (op == 'edtSrsServidor') {
      link = '../v3/editarStatusRegistroServidor?acao=iniciar';
    } else if (op == 'reservarMargem') {
      <% if(senhaObrigatoria) {%>
          link = '../v3/reservarMargem?acao=iniciar&RSE_MATRICULA='+matricula;
      <% } else { %>
          link = '../v3/reservarMargem?acao=reservarMargem';
      <% } %>
    } else if (op == 'incluirConsignacao') {
      link = '../v3/incluirConsignacao?acao=reservarMargem';
    } else {
      return;
    }

    link += '&<%=SynchronizerToken.generateToken4URL(request)%>'
        + '&RSE_CODIGO=' + (rse ?? "")
        + '&SER_CODIGO=' + (ser ?? "")
        + '&SVC_CODIGO=' + (svc ?? "")
        + '&RSE_MATRICULA=' + (matricula ?? "")
        + '&SER_CPF=' + (cpf ?? "")
        ;
  
  	postData(link);
  }      
</script>

</c:set>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>