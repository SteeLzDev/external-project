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
 
	List<ArquivoDownload> arquivosCritica = (List<ArquivoDownload>) request.getAttribute("arquivosCritica");
	List<ArquivoDownload> arquivosPrevia = (List<ArquivoDownload>) request.getAttribute("arquivosPrevia");
 
    String rotuloSelecionar = ApplicationResourcesHelper.getMessage("rotulo.acoes.selecionar", responsavel);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<c:set var="title">
	<hl:message key="rotulo.previa.faturamento.beneficios.titulo" />
</c:set>

<c:set var="imageHeader">
	<use xlink:href="#i-beneficios"></use>
</c:set>

<c:set var="bodyContent">
 <form method="post" action="../v3/validarFaturamento?acao=validar&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
    <hl:htmlinput name="FAT_CODIGO" type="hidden" value="<%=TextHelper.forHtmlAttribute(fat_codigo)%>" /> 
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

    <div class="card">
      <div class="card-header hasIcon">
        <span class="card-header-icon"><svg width="25"><use xlink:href="../img/sprite.svg#i-relatorio"></use></svg></span>
        <h2 class="card-header-title"><%=ApplicationResourcesHelper.getMessage("mensagem.info.arquivos.previa.faturamento.beneficio.disponivel", responsavel) %></h2>
      </div>
      <div class="card-body table-responsive">
        <table class="table table-striped table-hover">
          <thead>
            <tr>
              <th scope="col" width="3%" class="colunaUnica" style="display: none;">
                <div class="form-check"><%=rotuloSelecionar%><br/>
                  <input type="checkbox" class="form-check-input ml-0" name="checkAll_" id="checkAll_" data-bs-toggle="tooltip" data-original-title="<%=rotuloSelecionar%>" alt="<%=rotuloSelecionar%>" title="<%=rotuloSelecionar%>">
                </div>                  
              </th>
              <th scope="col"><hl:message key="rotulo.arquivo.nome"/></th>
              <th scope="col"><hl:message key="rotulo.arquivo.tamanho"/></th>
              <th scope="col"><hl:message key="rotulo.arquivo.data"/></th>
              <th scope="col" width="15%"><hl:message key="rotulo.acoes"/></th>
            </tr>
          </thead>
          <tbody>
        <% if (arquivosPrevia == null || arquivosPrevia.isEmpty()) { %>
          <tr>
            <td colspan="5"><hl:message key="mensagem.folha.nenhum.arquivo.encontrado"/></td>
          </tr>
        <% } else { %>
          <%
            int i = 0;
            for (ArquivoDownload arquivo : arquivosPrevia) {
                String arqNome = arquivo.getNome();
          %>
              <tr class="selecionarLinha">
                <td class="colunaUnica" aria-label="<%=rotuloSelecionar%>" title="<%=rotuloSelecionar%>" data-bs-toggle="tooltip" data-original-title="<%=rotuloSelecionar%>" style="display: none;">
                   <div class="form-check">
                     <input type="checkbox" class="form-check-input ml-0" name="chkArquivoPrevia" value="<%=TextHelper.forHtmlAttribute(arqNome)%>" data-exibe-msg2="0" data-usa-link2="0">
                   </div>
                </td>
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
                        <a class="dropdown-item" href="#" onclick ="escolhechk('<%=ApplicationResourcesHelper.getMessage("rotulo.acoes.selecionar", responsavel)%>',this)"><hl:message key="rotulo.acoes.selecionar"/></a>
                        <a class="dropdown-item" href="#no-back" aria-label="<hl:message key="rotulo.botao.aria.download.arquivo" arg0="${arquivo.nome}"/>" onClick="javascript:downloadArquivo(encodeURIComponent('<%=TextHelper.forJavaScript(arquivo.getNome())%>'), 'previa'); return false;"><hl:message key="rotulo.botao.download"/></a>
                        <a class="dropdown-item" href="#no-back" aria-label="<hl:message key="rotulo.botao.aria.excluir.arquivo" arg0="${arquivo.nome}"/>" onClick="javascript:excluirArquivo(encodeURIComponent('<%=TextHelper.forJavaScript(arquivo.getNome())%>'), 'previa'); return false;"><hl:message key="rotulo.botao.excluir"/></a>
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
              <td colspan="5"><%=ApplicationResourcesHelper.getMessage("mensagem.info.lista.arquivos.previa.faturamento.beneficio.disponivel", responsavel) %></td>
            </tr>
          </tfoot>
        </table>
      </div>
    </div>

  <% if (arquivosCritica != null && !arquivosCritica.isEmpty()) { %>
    <div class="card">
      <div class="card-header hasIcon">
        <span class="card-header-icon"><svg width="25"><use xlink:href="../img/sprite.svg#i-relatorio"></use></svg></span>
        <h2 class="card-header-title"><%=ApplicationResourcesHelper.getMessage("mensagem.info.arquivos.critica.faturamento.beneficio.disponivel", responsavel) %></h2>
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
                        <a class="dropdown-item" href="#no-back" aria-label="<hl:message key="rotulo.botao.aria.download.arquivo" arg0="${arquivo.nome}"/>" onClick="javascript:downloadArquivo(encodeURIComponent('<%=TextHelper.forJavaScript(arquivo.getNome())%>'), 'fatura'); return false;"><hl:message key="rotulo.botao.download"/></a>
                        <a class="dropdown-item" href="#no-back" aria-label="<hl:message key="rotulo.botao.aria.excluir.arquivo" arg0="${arquivo.nome}"/>" onClick="javascript:excluirArquivo(encodeURIComponent('<%=TextHelper.forJavaScript(arquivo.getNome())%>'), 'fatura'); return false;"><hl:message key="rotulo.botao.excluir"/></a>
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
              <td colspan="5"><%=ApplicationResourcesHelper.getMessage("mensagem.info.lista.arquivos.critica.faturamento.beneficio.disponivel", responsavel) %></td>
            </tr>
          </tfoot>
        </table>
      </div>
    </div>
  <% } %>

  <div class="float-end">
    <div class="btn-action">
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;" id="btnVoltar"><hl:message key="rotulo.botao.voltar" /></a>
      <a class="btn btn-primary" href="#no-back" onClick="if (validar()) { f0.submit(); } return false;"><hl:message key="rotulo.botao.confirmar"/></a>
    </div>
  </div>
 </form>
</c:set>

<c:set var="javascript">
<script type="text/JavaScript">

  var f0 = document.forms['form1'];

  function validar() {
	var checked = $("table tbody tr input[type=checkbox]:checked").length;
    if (checked == 0 && !confirm('<%=ApplicationResourcesHelper.getMessage("mensagem.confirmacao.processa.previa.faturamento.beneficio.sem.arquivo", responsavel) %>')) {
   	  return false;  
    }
	
    return true;
  }

  var verificarCheckbox = function () {
		var checked = $("table tbody tr input[type=checkbox]:checked").length;
		var total = $("table tbody tr input[type=checkbox]").length;
		$("input[id*=checkAll_]").prop('checked', checked == total);
		if (checked == 0) {
			$("table thead tr th.colunaUnica, table tbody tr td.colunaUnica").hide();
		} else {
			$("table thead tr th.colunaUnica, table tbody tr td.colunaUnica").show();
		}
	};

	$("table tbody tr td").not("td.colunaUnica, td.acoes").click(function (e) {
		$(e.target).parents('tr').find('input[type=checkbox]').click();
	});

	function escolhechk(idchk,e) {
	 	$(e).parents('tr').find('input[type=checkbox]').click();
	}

	$("table tbody tr input[type=checkbox]").click(function (e) {
		verificarCheckbox();
		var checked = e.target.checked;
		if (checked) {
			$(e.target).parents('tr').addClass("table-checked");
		} else {
			$(e.target).parents('tr').removeClass("table-checked");
		}
	});

	$("input[id*=checkAll_").click(function (e){
		var checked = e.target.checked;
		$('table tbody tr input[type=checkbox]').prop('checked', checked);
		if (checked) {
			$("table tbody tr").addClass("table-checked");
		} else {
			$("table tbody tr").removeClass("table-checked");
		}
		verificarCheckbox();
	});

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
     var endereco = "../v3/downloadArquivo?arquivo_nome=" + arquivo +"&tipo=" + encodeURIComponent(tipo) + "&csaCodigo=<%=csa_codigo%>&FAT_CODIGO=<%=fat_codigo%>&validarTerminoProcesso=false&<%=SynchronizerToken.generateToken4URL(request)%>";
     postData(endereco,'download');
  }

  function excluirArquivo(arquivo, tipo) {
     var endereco = "../v3/excluirArquivo?arquivo_nome=" + encodeURIComponent(arquivo) + "&tipo=" + encodeURIComponent(tipo) + "&csaCodigo=<%=csa_codigo%>&FAT_CODIGO=<%=fat_codigo%>&validarTerminoProcesso=false&<%=SynchronizerToken.generateToken4URL(request)%>";
     postData(endereco);
  }

  function doLoad(reload) {
    if (reload) {
      setTimeout("refresh()", 10*1000);
    }
  }

  function refresh() {
    postData('../v3/validarFaturamento?acao=iniciar&FAT_CODIGO=<%=fat_codigo%>&validarTerminoProcesso=true&<%=SynchronizerToken.generateToken4URL(request)%>');
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