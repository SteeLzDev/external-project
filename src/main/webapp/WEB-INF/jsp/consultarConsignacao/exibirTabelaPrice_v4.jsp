<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ taglib prefix="t"     tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl"    uri="/html-lib" %>
<%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core" %>
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

  String adeCodigo = (String) request.getAttribute("ADE_CODIGO");
%>
<c:set var="bodyContent">
  <div class="modal-header pt-0 p-0">
    <div class="btn-action ms-auto mt-0 mb-2">
      <a class="btn btn-primary" data-bs-dismiss="modal" onClick="gerarPDFTabelaPrice();" aria-label='<hl:message key="rotulo.botao.gerar.pdf"/>' href="#" alt="<hl:message key="rotulo.botao.gerar.pdf"/>"
        title="<hl:message key="rotulo.botao.gerar.pdf"/>">
        <hl:message key="rotulo.botao.gerar.pdf" />
      </a>
    </div>
  </div>
  <div class="row firefox-print-fix">
    <div class="col-sm-12 col-md-12">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.tabela.price.titulo.pagina"/></h2>
        </div>
        <div class="card-body">
          <hl:tabelaPriceV4 name="autdes" scope="request"/>
        </div>
      </div>
    </div>
  </div>
</c:set>
<c:set var="javascript">
      <script src="<c:url value='/node_modules/jquery/dist/jquery.min.js'/>?<hl:message key='release.tag'/>"></script>
      <script language="JavaScript" type="text/JavaScript">
          function gerarPDFTabelaPrice() {
            var dadosMargem = document.getElementById('tabelaPrice').innerHTML;
            
            var doc = dadosMargem;

            var dataToSend = JSON.stringify({'html': doc, 'adeCodigo': '<%=TextHelper.forJavaScriptAttribute(adeCodigo)%>'});
            $.ajax({ url: "../v3/consultarConsignacao?acao=gerarPdfTabelaPrice&<%=SynchronizerToken.generateToken4URL(request)%>&_skip_history_=true",
                  type: "POST",
                  contentType: "application/json; charset=utf-8",
                  data: dataToSend ,
                  responseType: 'arraybuffer',
                  xhrFields: { responseType: 'blob' },
                  success: function (response, status, xhr) {
                      var filename = "";
                      var disposition = xhr.getResponseHeader("Content-Disposition");
                      if (disposition && disposition.indexOf("filename") !== -1) {
                          var filenameRegex = /filename[^;=\n]*=(([""]).*?\2|[^;\n]*)/;
                          var matches = filenameRegex.exec(disposition);
                          if (matches != null && matches[1])
                              filename = matches[1].replace(/[""]/g, "");
                      }
                      var blob = new Blob([response], {type: 'application/pdf'});
                      var link = document.createElement('a');
                      link.href = window.URL.createObjectURL(blob);
                      link.download = filename;
                      link.click();
                  },
                  error: function (request, status, error) {
                      alert ("<hl:message key="mensagem.erro.download"/>");
                  }
              });
          }
      </script>
</c:set>
<t:popup_v4>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:popup_v4>
