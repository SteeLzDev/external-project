<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
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

	TransferObject faturamentoBeneficio = (TransferObject) request.getAttribute("faturamentoBeneficio");
	
	String fat_codigo = (String) faturamentoBeneficio.getAttribute(Columns.FAT_CODIGO);
	String fat_periodo = DateHelper.format((Date)faturamentoBeneficio.getAttribute(Columns.FAT_PERIODO), "MM/yyyy");
	String csa_codigo = (String) faturamentoBeneficio.getAttribute(Columns.CSA_CODIGO);
	String csa_nome = (String) faturamentoBeneficio.getAttribute(Columns.CSA_NOME);
	String fat_data = DateHelper.format((Date)faturamentoBeneficio.getAttribute(Columns.FAT_PERIODO), LocaleHelper.getDateTimePattern());
 
	List<ArquivoDownload> arquivos = (List<ArquivoDownload>) request.getAttribute("arquivos");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<c:set var="title">
	<hl:message key="rotulo.faturamento.beneficios.titulo" />
</c:set>

<c:set var="imageHeader">
	<use xlink:href="#i-beneficios"></use>
</c:set>

<c:set var="bodyContent">
	<div class="page-title">
	  <div class="row">
        <div class="col-sm mb-2">
          <div class="float-end">
            <button data-bs-toggle="dropdown" aria-haspopup="true" id="acoes" aria-expanded="false" class="btn btn-primary" type="submit"><hl:message key="rotulo.mais.acoes"/></button>
            <div class="dropdown-menu dropdown-menu-right" aria-labelledby="acoes">
              <a class="dropdown-item" href="#no-back" onClick="postData('../v3/consultarFaturamentos?acao=gerarFaturamento&FAT_CODIGO=<%=TextHelper.forJavaScript(fat_codigo)%>&_skip_history_=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>'); return false;"><hl:message key="rotulo.faturamento.beneficios.gerar.faturamento"/></a>
              <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterArquivoFaturamentoBeneficio?acao=iniciar&FAT_CODIGO=<%=TextHelper.forJavaScriptAttribute(fat_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>'); return false;"><hl:message key="rotulo.faturamento.beneficios.consultar.arquivos"/></a>
              <a class="dropdown-item" href="#no-back" onClick="postData('../v3/validarFaturamento?acao=iniciar&FAT_CODIGO=<%=TextHelper.forJavaScript(fat_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>'); return false;"><hl:message key="rotulo.faturamento.beneficios.validar.faturamento"/></a>
            </div>
          </div>
        </div>
      </div>
	</div>
	<div class="row">
		<div class="col-sm">
			<div class="card">
				<div class="card-header">
					<h2 class="card-header-title"><hl:message key="rotulo.faturamento.beneficios.detalhes.faturamento" /></h2>
				</div>
				<div class="card-body">
					<dl class="row data-list">
								<dt class="col-6"><hl:message key="rotulo.faturamento.beneficios.periodo" />:</dt>
								<dd class="col-6"><%=fat_periodo != null ? TextHelper.forHtmlContent(fat_periodo) : ""%></dd>
								<dt class="col-6"><hl:message key="rotulo.faturamento.beneficios.operadora" />:</dt>
								<dd class="col-6"><%=csa_nome != null ? TextHelper.forHtmlContent(csa_nome) : ""%></dd>
								<dt class="col-6"><hl:message key="rotulo.faturamento.beneficios.data.faturamento" />:</dt>
								<dd class="col-6"><%=fat_data != null ? TextHelper.forHtmlContent(fat_data) : ""%></dd>
							</dl>
				</div>
			</div>
		</div>
	</div>

  <% if (arquivos != null && !arquivos.isEmpty()) { %>
    <div class="card">
      <div class="card-header hasIcon">
        <span class="card-header-icon"><svg width="25"><use xlink:href="../img/sprite.svg#i-relatorio"></use></svg></span>
        <h2 class="card-header-title"><%=ApplicationResourcesHelper.getMessage("mensagem.info.arquivos.faturamento.beneficio.disponivel", responsavel) %></h2>
      </div>
      <div class="card-body table-responsive">
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
            for (ArquivoDownload arquivo : arquivos) {
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
                        <a class="dropdown-item" href="#no-back" aria-label="<hl:message key="rotulo.botao.aria.download.arquivo" arg0="${arquivo.nome}"/>" onClick="javascript:downloadArquivo(encodeURIComponent('<%=TextHelper.forJavaScript(arquivo.getNome())%>')); return false;"><hl:message key="rotulo.botao.download"/></a>
                        <a class="dropdown-item" href="#no-back" aria-label="<hl:message key="rotulo.botao.aria.excluir.arquivo" arg0="${arquivo.nome}"/>" onClick="javascript:excluirArquivo(encodeURIComponent('<%=TextHelper.forJavaScript(arquivo.getNome())%>')); return false;"><hl:message key="rotulo.botao.excluir"/></a>
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
              <td colspan="5"><%=ApplicationResourcesHelper.getMessage("mensagem.info.lista.arquivos.faturamento.beneficio.disponivel", responsavel) %></td>
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
</c:set>

<c:set var="javascript">
<script type="text/JavaScript">

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

  function downloadArquivo(arquivo) {
     var endereco = "../v3/downloadArquivo?arquivo_nome=" + arquivo +"&tipo=fatura&csaCodigo=<%=csa_codigo%>&FAT_CODIGO=<%=fat_codigo%>&<%=SynchronizerToken.generateToken4URL(request)%>";
     postData(endereco,'download');
  }

  function excluirArquivo(arquivo) {
     var endereco = "../v3/excluirArquivo?arquivo_nome=" + encodeURIComponent(arquivo) +"&tipo=fatura&csaCodigo=<%=csa_codigo%>&FAT_CODIGO=<%=fat_codigo%>&<%=SynchronizerToken.generateToken4URL(request)%>";
     postData(endereco);
  }

  function doLoad(reload) {
    if (reload) {
      setTimeout("refresh()", 10*1000);
    }
  }

  function refresh() {
    postData('../v3/consultarFaturamentos?acao=consultar&FAT_CODIGO=<%=fat_codigo%>&validarTerminoProcesso=true&<%=SynchronizerToken.generateToken4URL(request)%>');
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