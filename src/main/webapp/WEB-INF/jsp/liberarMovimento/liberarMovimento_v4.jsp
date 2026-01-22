<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%
AcessoSistema responsavel = (AcessoSistema) request.getAttribute("responsavel");
ResultadoValidacaoMovimentoTO resultadoValidacaoMovimentoTO = (ResultadoValidacaoMovimentoTO) request.getAttribute("resultadoValidacaoMovimentoTO");
String nomeArquivo = (String) request.getAttribute("nomeArquivo");
UsuarioTransferObject usuarioTransferObject = (UsuarioTransferObject) request.getAttribute("usuarioTransferObject");
boolean liberouMovimento = (request.getAttribute("liberouMovimento") != null) ? (Boolean) request.getAttribute("liberouMovimento") : false; 
List<?> resultadoRegras = (List<?>) request.getAttribute("resultadoRegras");
%>

<c:set var="title">
  <hl:message key="rotulo.folha.liberacao.arquivo.movimento.titulo" />
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row ">
    <div class="col-sm-12">
      <form
        action="../v3/liberarMovimento?<%=SynchronizerToken.generateToken4URL(request)%>"
        method="post" name="form1">
        <input type="hidden" name="arquivo_nome"
          value="<%=TextHelper.forHtmlAttribute(nomeArquivo)%>" /> <input
          type="hidden" name="tipo" value="movimento" /> <input
          type="hidden" name="acao" value="liberarMovimento" /> <input
          type="hidden" name="liberouMovimento" value="true" />
        <div class="card">
          <div class="card-header hasIcon pl-3">
            <h2 class="card-header-title">
              <hl:message
                key="rotulo.folha.liberacao.arquivo.movimento.titulo" />
            </h2>
          </div>
          <div class="card-body">
            <div class="form-group col-sm">
              <div class="col-sm-12">
                <div class="row firefox-print-fix">

                  <div class="card-body ">
                    <dl class="row data-list firefox-print-fix">
                      <dt class="col-6">
                        <hl:message key="rotulo.folha.periodo" />
                      </dt>
                      <dd class="col-6"><%=DateHelper.toPeriodString(resultadoValidacaoMovimentoTO.getRvaPeriodo())%></dd>
                      <dt class="col-6">
                        <hl:message key="rotulo.folha.arquivo.movimento" />
                      </dt>
                      <dd class="col-6"><%=TextHelper.forHtmlContent(resultadoValidacaoMovimentoTO.getRvaNomeArquivoFormatado())%></dd>
                      <dt class="col-6">
                        <hl:message key="rotulo.folha.data.validacao" />
                      </dt>
                      <dd class="col-6"><%=DateHelper.toDateTimeString(resultadoValidacaoMovimentoTO.getRvaDataProcesso())%></dd>
                      <%
                          if (resultadoValidacaoMovimentoTO.getRvaDataAceite() != null) {
                      %>
                      <dt class="col-6">
                        <hl:message key="rotulo.folha.data.liberacao" />
                      </dt>
                      <dd class="col-6"><%=DateHelper.toDateTimeString(resultadoValidacaoMovimentoTO.getRvaDataAceite())%></dd>
                      <dt class="col-6">
                        <hl:message key="rotulo.folha.responsavel" />
                      </dt>
                      <dd class="col-6"><%=TextHelper
							.forHtmlContent(usuarioTransferObject != null ? usuarioTransferObject.getUsuLogin() : "")%></dd>
                      <%
                          }
                      %>
                    </dl>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="card">
          <div class="card-header hasIcon pl-3">
            <h2 class="card-header-title">
              <hl:message
                key="mensagem.folha.resultado.validacao.arquivo.movimento" />
            </h2>
          </div>
          <div class="card-body table-responsive">
            <table class="table table-striped table-hover">
              <thead>
                <tr>
                  <th scope="col"><hl:message
                      key="rotulo.folha.sequencia.abreviado" /></th>
                  <th scope="col"><hl:message
                      key="rotulo.folha.regra" /></th>
                  <th scope="col"><hl:message
                      key="rotulo.folha.valor.encontrado" /></th>
                  <th scope="col"><hl:message
                      key="rotulo.folha.resutado" /></th>
                </tr>
              </thead>
              <tbody>
                <%
                    int i = 0;
                				Iterator<?> it = resultadoRegras.iterator();
                				CustomTransferObject resultado = null;
                				while (it.hasNext()) {
                					resultado = (CustomTransferObject) it.next();
                %>
                <tr>
                  <td><%=TextHelper.forHtmlContent(resultado.getAttribute(Columns.RVM_SEQUENCIA))%></td>
                  <td><%=TextHelper.forHtmlContent(resultado.getAttribute(Columns.RVM_DESCRICAO))%></td>
                  <td><%=TextHelper.forHtmlContent(resultado.getAttribute(Columns.RRV_VALOR_ENCONTRADO))%></td>
                  <td>
                    <%
                        if (resultado.getAttribute(Columns.RRV_RESULTADO).toString()
                    							.equals(CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_OK)) {
                      %> <hl:message key="rotulo.folha.resultado.ok" /> <%
                  } else if (resultado.getAttribute(Columns.RRV_RESULTADO).toString()
 							.equals(CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_ERRO)) {
                      %> <hl:message key="rotulo.folha.resultado.erro" /> <%
                  } else if (resultado.getAttribute(Columns.RRV_RESULTADO).toString()
 							.equals(CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_AVISO)) {
                      %> <hl:message key="rotulo.folha.resultado.aviso" /> <%
                        }
                    %>
                  </td>
                  <%
                      }
                  %>
                </tr>
              </tbody>
            </table>
            <div class="btn-action">
              <button class="btn btn-outline-danger" id="btnCancelar"
                onClick="postData('../v3/listarArquivosDownloadIntegracao?acao=iniciar&tipo=movimento')">
                <hl:message key="rotulo.botao.cancelar" />
              </button>
              <%
                  if (resultadoValidacaoMovimentoTO.getRvaDataAceite() != null) {
              %>
              <button class="btn btn-primary" id="btnDownload"
                onClick="liberarMovimento()">
                <hl:message key="rotulo.botao.download" />
              </button>
              <%
                  } else {
              %>
              <button class="btn btn-primary" id="btnValidar"
                onClick="liberarMovimento()">
                <hl:message key="rotulo.botao.validar" />
              </button>
              <%
                  }
              %>
            </div>
          </div>
        </div>
      </form>
    </div>
  </div>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript"> 
  window.onload = "<%=TextHelper.forJavaScriptAttribute((liberouMovimento ? "liberarMovimento()" : ""))%>"
</script>
<script type="text/JavaScript">    
  function liberarMovimento() {
      <% if (resultadoValidacaoMovimentoTO.getRvaDataAceite() == null) { %>
          if (confirm('<hl:message key="mensagem.folha.confirma.liberacao.arquivo.movimento"/>')) {
            f0 = document.forms[0];
            f0.submit();
          }
      <% } else { %>
          postData('../v3/downloadArquivo?arquivo_nome=<%=TextHelper.forJavaScriptBlock(nomeArquivo)%>&tipo=movimento&<%=SynchronizerToken.generateToken4URL(request)%>');
      <% } %>
    }    
</script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>