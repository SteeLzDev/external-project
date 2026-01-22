<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.dto.web.ArquivoDownload"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
	AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

	List<ArquivoDownload> arquivosCancelamento = (List<ArquivoDownload>) request.getAttribute("arquivosCancelamento");
	List<ArquivoDownload> arquivosCritica = (List<ArquivoDownload>) request.getAttribute("arquivosCritica");

    String rotuloSelecionar = ApplicationResourcesHelper.getMessage("rotulo.acoes.selecionar", responsavel);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<c:set var="title">
	<hl:message key="rotulo.cancelar.beneficio.inadimplente.titulo" />
</c:set>

<c:set var="imageHeader">
	<use xlink:href="#i-beneficios"></use>
</c:set>

<c:set var="bodyContent">
 <form method="post">
    <div class="card">
      <div class="card-header hasIcon">
        <span class="card-header-icon"><svg width="25"><use xlink:href="../img/sprite.svg#i-relatorio"></use></svg></span>
        <h2 class="card-header-title"><%=ApplicationResourcesHelper.getMessage("mensagem.info.arquivos.cancelar.beneficio.inadimplente.disponivel", responsavel) %></h2>
      </div>
      <div class="card-body table-responsive  p-0">
        <table class="table table-striped table-hover">
          <thead>
            <tr>
              <th scope="col"><hl:message key="rotulo.arquivo.nome"/></th>
              <th scope="col"><hl:message key="rotulo.arquivo.tamanho"/></th>
              <th scope="col"><hl:message key="rotulo.arquivo.data"/></th>
              <th scope="col" width="15%"><hl:message key="rotulo.acoes"/></th>
            </tr>
          </thead>
          <tbody>
        <% if (arquivosCancelamento == null || arquivosCancelamento.isEmpty()) { %>
          <tr>
            <td colspan="5"><hl:message key="mensagem.folha.nenhum.arquivo.encontrado"/></td>
          </tr>
        <% } else { %>
          <%
            int i = 0;
            for (ArquivoDownload arquivo : arquivosCancelamento) {
                String arqNome = arquivo.getNome();
          %>
              <tr class="selecionarLinha">
                <td class="selecionarColuna"><%=TextHelper.forHtmlContent(arquivo.getNomeOriginal())%></td>
                <td class="selecionarColuna"><%=TextHelper.forHtmlContent(arquivo.getTamanho())%></td>
                <td class="selecionarColuna"><%=TextHelper.forHtmlContent(arquivo.getData())%></td>
                <td class="acoes">
                  <div class="actions">
                    <div class="dropdown">
                      <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <div class="form-inline">
                          <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.botao.opcoes"/>" aria-label="<hl:message key="rotulo.botao.opcoes"/>">
                            <svg><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="../img/sprite.svg#i-engrenagem"></use></svg>
                          </span>
                          <hl:message key="rotulo.botao.opcoes"/>
                        </div>
                      </a>
                      <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                        <a class="dropdown-item" href="#" onClick="postData('../v3/cancelarContratoBeneficioInadimplencia?acao=processar&nomeArquivo=<%=TextHelper.forJavaScript(arqNome)%>&<%=(String)(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.importar"/></a>
                        <a class="dropdown-item" href="#no-back" aria-label="<hl:message key="rotulo.botao.aria.download.arquivo" arg0="${arquivo.nome}"/>" onClick="javascript:downloadArquivo(encodeURIComponent('<%=TextHelper.forJavaScript(arquivo.getNome())%>'), 'cancelamentoporinadimplencia'); return false;"><hl:message key="rotulo.botao.download"/></a>
                        <a class="dropdown-item" href="#no-back" aria-label="<hl:message key="rotulo.botao.aria.excluir.arquivo" arg0="${arquivo.nome}"/>" onClick="javascript:excluirArquivo(encodeURIComponent('<%=TextHelper.forJavaScript(arquivo.getNome())%>'), 'cancelamentoporinadimplencia'); return false;"><hl:message key="rotulo.botao.excluir"/></a>
                      </div>
                    </div>
                  </div>
                </td>
              </tr>
        <%
            }
          }
        %>
          </tbody>
          <tfoot>
            <tr>
              <td colspan="5"><%=ApplicationResourcesHelper.getMessage("mensagem.info.lista.arquivos.cancelar.beneficio.inadimplente.disponivel", responsavel) %></td>
            </tr>
          </tfoot>
        </table>
      </div>
    </div>

  <% if (arquivosCritica != null && !arquivosCritica.isEmpty()) { %>
    <div class="card">
      <div class="card-header hasIcon">
        <span class="card-header-icon"><svg width="25"><use xlink:href="../img/sprite.svg#i-relatorio"></use></svg></span>
        <h2 class="card-header-title"><%=ApplicationResourcesHelper.getMessage("mensagem.info.arquivos.critica.cancelar.beneficio.inadimplente.disponivel", responsavel) %></h2>
      </div>
      <div class="card-body table-responsive  p-0">
        <table class="table table-striped table-hover">
          <thead>
            <tr>
              <th scope="col"><hl:message key="rotulo.arquivo.nome"/></th>
              <th scope="col"><hl:message key="rotulo.arquivo.tamanho"/></th>
              <th scope="col"><hl:message key="rotulo.arquivo.data"/></th>
              <th scope="col" width="15%"><hl:message key="rotulo.acoes"/></th>
            </tr>
          </thead>
          <tbody>
          <%
            int i = 0;
            for (ArquivoDownload arquivo : arquivosCritica) {
          %>
              <tr>
                <td><%=TextHelper.forHtmlContent(arquivo.getNomeOriginal())%></td>
                <td><%=TextHelper.forHtmlContent(arquivo.getTamanho())%></td>
                <td><%=TextHelper.forHtmlContent(arquivo.getData())%></td>
                <td>
                  <div class="actions">
                    <div class="dropdown">
                      <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <div class="form-inline">
                          <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.botao.opcoes"/>" aria-label="<hl:message key="rotulo.botao.opcoes"/>">
                            <svg><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="../img/sprite.svg#i-engrenagem"></use></svg>
                          </span>
                          <hl:message key="rotulo.botao.opcoes"/>
                        </div>
                      </a>
                      <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                        <a class="dropdown-item" href="#no-back" aria-label="<hl:message key="rotulo.botao.aria.download.arquivo" arg0="${arquivo.nome}"/>" onClick="javascript:downloadArquivo(encodeURIComponent('<%=TextHelper.forJavaScript(arquivo.getNome())%>'), 'cancelamentoporinadimplencia'); return false;"><hl:message key="rotulo.botao.download"/></a>
                        <a class="dropdown-item" href="#no-back" aria-label="<hl:message key="rotulo.botao.aria.excluir.arquivo" arg0="${arquivo.nome}"/>" onClick="javascript:excluirArquivo(encodeURIComponent('<%=TextHelper.forJavaScript(arquivo.getNome())%>'), 'cancelamentoporinadimplencia'); return false;"><hl:message key="rotulo.botao.excluir"/></a>
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
              <td colspan="5"><%=ApplicationResourcesHelper.getMessage("mensagem.info.lista.arquivos.critica.cancelar.beneficio.inadimplente.disponivel", responsavel) %></td>
            </tr>
          </tfoot>
        </table>
      </div>
    </div>
  <% } %>

  <div class="float-end">
    <div class="btn-action">
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;" id="btnVoltar"><hl:message key="rotulo.botao.voltar" /></a>
    </div>
  </div>
 </form>
</c:set>

<c:set var="javascript">
<script type="text/JavaScript">

  var f0 = document.forms['form1'];

  function addLoadEvent(func) {
    var oldonload = window.onload;
    if (typeof window.onload != 'function') {
      window.onload = func;
    } else {
      window.onload = function() {
        if (oldonload) {
            oldonload();
        }
        func();
      }
    }
  }

  function downloadArquivo(arquivo, tipo) {
     var endereco = "../v3/downloadArquivo?arquivo_nome=" + arquivo +"&tipo=" + encodeURIComponent(tipo) + "&validarTerminoProcesso=false&<%=SynchronizerToken.generateToken4URL(request)%>";
     postData(endereco,'download');
  }

  function excluirArquivo(arquivo, tipo) {
     var endereco = "../v3/excluirArquivo?arquivo_nome=" + encodeURIComponent(arquivo) + "&tipo=" + encodeURIComponent(tipo) + "&validarTerminoProcesso=false&<%=SynchronizerToken.generateToken4URL(request)%>";
     postData(endereco);
  }

  function doLoad(reload) {
    if (reload) {
      setTimeout("refresh()", 10*1000);
    }
  }

  function refresh() {
    postData('../v3/cancelarContratoBeneficioInadimplencia?acao=iniciar&validarTerminoProcesso=true&<%=SynchronizerToken.generateToken4URL(request)%>');
  }

  addLoadEvent(function() { doLoad(${temProcessoRodando}); });
</script>
</c:set>

<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>