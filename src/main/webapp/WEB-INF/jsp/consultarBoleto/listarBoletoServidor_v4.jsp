<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%
AcessoSistema responsavel = (AcessoSistema) request.getAttribute("responsavel");
List boletoServidor = (List) request.getAttribute("boletoServidor");
boolean exibePaginacao = (boolean) request.getAttribute("exibePaginacao");

%>
<c:set var="title">
  ${tituloPagina}
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
    <!-- Boleto servidor -->
    <div class="col-sm-7 col-md-12">
      <div class="card">
        <div class="card-header hasIcon pl-3">
          <h2 class="card-header-title">
            ${tituloPagina}
          </h2>
        </div>
        <div class="card-body ">
          <div class="table-responsive">
            <table class="table table-striped table-hover">
              <thead>
                <tr>
                  <th scope="col"><hl:message key="rotulo.consignataria.singular" /></th>
                  <th scope="col"><hl:message key="rotulo.boleto.servidor.data.upload" /></th>
                  <th scope="col"><hl:message key="rotulo.boleto.servidor.data.download" /></th>
                  <th scope="col"><hl:message key="rotulo.acoes" /></th>
                </tr>
              </thead>
              <tbody>
                <%
                  Iterator<TransferObject> it = boletoServidor.iterator();
                        while (it.hasNext()) {
                            TransferObject arquivo = (TransferObject) it.next();

                            String bosCodigo = arquivo.getAttribute(Columns.BOS_CODIGO).toString();
                            String csaNome = arquivo.getAttribute(Columns.CSA_NOME).toString();

                            String dataUpload = "";
                            if (!TextHelper.isNull(arquivo.getAttribute(Columns.BOS_DATA_UPLOAD))) {
                              dataUpload = DateHelper.toDateTimeString((Date) arquivo.getAttribute(Columns.BOS_DATA_UPLOAD));
                            }
                            String dataDownload = "";
                            if (!TextHelper.isNull(arquivo.getAttribute(Columns.BOS_DATA_DOWNLOAD))) {
                              dataDownload = DateHelper.toDateTimeString((Date) arquivo.getAttribute(Columns.BOS_DATA_DOWNLOAD));
                            }
                            
                %>
                <tr>
                  <td><%=TextHelper.forHtmlContent(csaNome)%></td>
                  <td><%=TextHelper.forHtmlContent(dataUpload)%></td>
                  <td><%=TextHelper.forHtmlContent(dataDownload)%></td>
                  <td>
                    <div class="position-relative">
                      <a href="#no-back" onClick="fazDownload('<%=TextHelper.forJavaScript(bosCodigo)%>'); return false;">
                        <hl:message key="rotulo.acoes.download"/>&nbsp;</span>
                      </a>
                    </div>
                  </td>
                </tr>
                <%
                    }
                %>
              </tbody>
              <tfoot>
                <tr>
                  <td colspan="4">
                    <hl:message key="rotulo.lote.listagem.boletos" /> 
                    <% if (exibePaginacao) { %>
                    <span class="font-italic"> - <%=request.getAttribute("_paginacaoSubTitulo")%></span>
                    <% } %>
                  </td>
                </tr>
              </tfoot>
            </table>
            <% if (exibePaginacao) { %>
            <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
            <% } %>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" aria-label='<hl:message key="rotulo.botao.voltar"/>' href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar" /></a>
  </div>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
function fazDownload(codigo){
  postData('../v3/consultarBoleto?acao=download&bosCodigo=' + codigo + '&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>', 'download');
  
}

</script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>