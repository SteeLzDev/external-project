<%--
* <p>Title: listarArquivoDispensaDigitalServidor_v4.jsp</p>
* <p>Description: Listar arquivo de dispensa de validacao de digital de servidor</p>
* <p>Copyright: Copyright (c) 2005</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
CustomTransferObject servidor = (CustomTransferObject) request.getAttribute("servidor");
List<TransferObject> arquivos = (List<TransferObject>) request.getAttribute("arquivos");
String linkAction = (String) request.getAttribute("linkAction");
%>

<c:set var="javascript">
<script type="text/JavaScript" src="../js/javacrypt.js?<hl:message key="release.tag"/>"></script>

<script type="text/JavaScript">

  function fazDownload(codigo) {
	  postData('../v3/listarArquivoDispensaDigitalServidor?acao=download<%=linkAction%>&arqCodigo=' + codigo + '&serCodigo=<%=servidor.getAttribute(Columns.SER_CODIGO).toString()%>&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>', 'download');
  }

</script>
</c:set>
<c:set var="title">
  <hl:message key="rotulo.servidor.arquivos.dispensa.validacao.digital.titulo" />
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
      <div class="row">
        <div class="col-sm-12 col-md-12">
          <div class="card">
            <div class="card-header">
              <h2 class="card-header-title"><hl:message key="rotulo.servidor.singular"/></h2>
            </div>
            <div class="card-body">
              <%-- Utiliza a tag library DetalheServidorTag.java para exibir os dados do servidor --%>
              <hl:detalharServidorv4 name="servidor" complementos="true" scope="request" />
              <%-- Fim dos dados da ADE --%>
            </div>
          </div>
        </div>
      </div>

      <% if (arquivos != null && !arquivos.isEmpty()) { %>
      <div class="row">
        <div class="col-sm-12 col-md-12 mb-2">
          <div class="card">
            <div class="card-header hasIcon pl-3">
              <h2 class="card-header-title"><hl:message key="rotulo.servidor.cadastrar.dispensa.validacao.digital.arquivo.disponivel"/></h2>
            </div>
            <div class="card-body table-responsive p-0">
              <table class="table table-striped table-hover table-responsive">
                <thead>
                  <tr>
                    <th id="dataArquivo"><hl:message key="rotulo.servidor.cadastrar.dispensa.validacao.digital.data" /></th>
                    <th id="responsavelArquivo"><hl:message key="rotulo.responsavel.singular" /></th>
                    <th id="descricaoArquivo"><hl:message key="rotulo.servidor.cadastrar.dispensa.validacao.digital.nome" /></th>
                    <th id="arquivo"><hl:message key="rotulo.acoes" /></th>
                  </tr>
                </thead>
                <tbody>
                  <%
                    String arqCodigo, usuLogin, aseNome, aseDataCriacao;
                    for (TransferObject arquivo : arquivos) {
                        arqCodigo = arquivo.getAttribute(Columns.ARQ_CODIGO).toString();
                        usuLogin = arquivo.getAttribute(Columns.USU_LOGIN).toString();
                        aseNome = arquivo.getAttribute(Columns.ASE_NOME).toString();
                        aseDataCriacao = DateHelper.toDateTimeString((Date) arquivo.getAttribute(Columns.ASE_DATA_CRIACAO));
                  %>
                  <tr>
                    <td header="dataArquivo"><%=TextHelper.forHtmlContent(aseDataCriacao)%></td>
                    <td header="responsavelArquivo"><%=TextHelper.forHtmlContent(usuLogin)%></td>
                    <td header="descricaoArquivo"><%=TextHelper.forHtmlContent(aseNome)%></td>
                    <td class="text-nowrap" header="arquivo" id="nomeArquivo">
                      <div class="position-relative">
                        <a href="#no-back" onClick="fazDownload('<%=TextHelper.forJavaScript(arqCodigo)%>'); return false;"><hl:message key="rotulo.acoes.download"/>&nbsp;</a>
                      </div>
                    </td>
                  </tr>
                  <% 
                    }
                  %>
                </tbody>
                <tfoot>
                  <tr><td colspan="4"><hl:message key="mensagem.servidor.cadastrar.dispensa.validacao.digital.lista.arquivos" /></td></tr>
                </tfoot>
              </table>
            </div>
          </div>
          <div class="btn-action">
            <a onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;" class="btn btn-outline-danger" href="#"><hl:message key="rotulo.botao.cancelar"/></a>
          </div>
        </div>
      </div>
      <% } %>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>