<%--
* <p>Title: listarCoeficienteCorrecao</p>
* <p>Description: Contem os coeficientes de correcao disponiveis para edição</p>
* <p>Copyright: Copyright (c) 2005</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: anderson.assis $
* $Revision: 28531 $
* $Date: 2020-01-03 14:15:54 (Sex, 03 jan 2020) $
--%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
boolean readOnly = !responsavel.temPermissao(CodedValues.FUN_EDT_COEFICIENTE_CORRECAO);

List<TransferObject> tiposCoeficienteCorrecao = (List<TransferObject>) request.getAttribute("tiposCoeficienteCorrecao");
%>
<c:set var="title">
  <hl:message key="rotulo.lst.coeficiente.correcao.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <div class="page-title">
    <div class="row d-print-none">
      <div class="col-sm-12 col-md-12 mb-2">
        <div class="float-end">
          <div class="btn-action">
          <% if (!readOnly) { %>
          <a class="btn btn-primary" href="#no-back" onClick="postData('../v3/manterCoeficienteCorrecao?acao=editar&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.novo.coeficiente"/></a>
          <% } %>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div class="row">
    <div class="col-sm-12">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.coeficiente.correcao.listar"/></h2>
        </div>
        <div class="card-body table-responsive ">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th scope="col" width="80%"><hl:message key="rotulo.descricao.coeficiente.correcao"/></th>
                <th scope="col" width="20%"><hl:message key="rotulo.acoes"/></th>
              </tr>
            </thead>
            <tbody>
<%
  Iterator it = tiposCoeficienteCorrecao.iterator();
   while (it.hasNext()) {
     CustomTransferObject coefCorrecaoCTO = (CustomTransferObject)it.next();
     String ccrTccCodigo = coefCorrecaoCTO.getAttribute(Columns.TCC_CODIGO).toString();
     String tccDescricao = (String)coefCorrecaoCTO.getAttribute(Columns.TCC_DESCRICAO);
%>
              <tr>
                <td><%=TextHelper.forHtmlContent(tccDescricao)%></td>
                <td>
                  <div class="actions">
                    <div class="dropdown">
                      <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <div class="form-inline">
                          <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="Mais ações" aria-label="Mais ações"> <svg>
                              <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                          </span> <hl:message key="rotulo.coeficiente.opcoes"/>
                        </div>
                      </a>
                      <% if (!readOnly) { %>
                      <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                        <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterCoeficienteCorrecao?acao=editar&ccrTccCodigo=<%=TextHelper.forJavaScriptAttribute(ccrTccCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.editar"/></a>
                        <a class="dropdown-item" href="#no-back" onClick="ExcluirCoeficiente('<%=TextHelper.forJavaScript(ccrTccCodigo)%>', '', '', 'CCR', '../v3/manterCoeficienteCorrecao?acao=excluir&<%=SynchronizerToken.generateToken4URL(request)%>', '<%=TextHelper.forJavaScript(tccDescricao)%>')"><hl:message key="rotulo.coeficiente.botao.excluir"/></a>
                      </div>
                      <%} else {%>
                      <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                        <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterCoeficienteCorrecao?acao=editar&ccrTccCodigo=<%=TextHelper.forJavaScriptAttribute(ccrTccCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.coeficiente.botao.visualizar"/></a>                       
                      </div>
                      <%}%>
                    </div>
                  </div>
                </td>
              </tr>
<%
 }
%>
            <tfoot>
              <tr>
                <td colspan="2"><hl:message key="rotulo.listagem.coeficientes.correcao"/></td>
              </tr>
            </tfoot>            
          </table>
        </div>
      </div>
    </div>
  </div> 
  <div class="btn-action">
    <button class="btn btn-outline-danger" onClick="postData('../v3/carregarPrincipal'); return false;" VALUE="Cancelar"><hl:message key="rotulo.coeficiente.voltar"/></button>
  </div>            
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript">
    function ExcluirCoeficiente(codigo, mes, ano, tipo, alink, desc) {
      var url = alink + (alink.indexOf('?') == -1 ? "?" : "&")  + "codigo=" + codigo + "&ccrAno=" + ano + "&ccrMes=" + mes + "&excluir=sim&tccDescricao=" + desc;
      return ConfirmaUrl('<hl:message key="mensagem.confirma.exclusao.tabela.coeficientes"/>'.replace('{0}', desc), url + '&<%=SynchronizerToken.generateToken4URL(request)%>');
    }
  </script>  
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>