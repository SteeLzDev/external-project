<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t"    tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl"   uri="/html-lib" %>
<%@ taglib prefix="fl"   uri="/function-lib" %>
<%@ taglib prefix="c"    uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.regex.*" %>
<%@ page import="com.zetra.econsig.helper.periodo.PeriodoHelper" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession"/>
<%
AcessoSistema responsavel  = (AcessoSistema) request.getAttribute("responsavel");
boolean temProcessoRodando = (boolean) request.getAttribute("temProcessoRodando");
boolean podeValidarLote    = (boolean) request.getAttribute("podeValidarLote");
boolean podeProcessarLote  = (boolean) request.getAttribute("podeProcessarLote");
boolean podeExcluirArqLote = (boolean) request.getAttribute("podeExcluirArqLote");
int size                   = (int) request.getAttribute("size");
int offset                 = (int) request.getAttribute("offset");
List<?> arquivos           = (List<?>) request.getAttribute("arquivos");
String xml                 = (String) request.getAttribute("xml");
String corCodigo           = (String) request.getAttribute("corCodigo");
String parametros          = (String) request.getAttribute("parametros");
String entidade            = (String) request.getAttribute("entidade");
String tipo                = (String) request.getAttribute("tipo");
String tipoCodigo          = (String) request.getAttribute("tipoCodigo");
String csaCodigo           = (String) request.getAttribute("csaCodigo");
String absolutePath        = (String) request.getAttribute("absolutePath");
boolean saldoDevedor       = !TextHelper.isNull(request.getAttribute("saldoDevedor"));
List<Date> periodoAgrupado    = (List<Date>) request.getAttribute("periodoAgrupado");
boolean csaCorEscolherPeriodo = responsavel.isCsaCor() && periodoAgrupado != null && periodoAgrupado.size() > 1;
%>
<c:set var="title">
  <%if(!saldoDevedor){ %>
      <hl:message key="rotulo.processar.lote.titulo"/>
  <%} else { %>
      <hl:message key="rotulo.processar.lote.saldo.devedor.titulo"/>
  <%} %>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
  <% if (!temProcessoRodando) { %>
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.editar.grid"/></h2>
        </div>
        <div class="card-body">
          <div class="alert alert-warning" role="alert">
            <p class="mb-0"><hl:message key="mensagem.informacao.recomendacao.validacao.lote"/></p>
            <p class="mb-0"><hl:message key="mensagem.informacao.processamento.nao.pode.ser.desfeito"/></p>
            <% if (!responsavel.temPermissao(CodedValues.FUN_CONF_RESERVA)) { %>
            <p class="mb-0"><hl:message key="mensagem.alerta.permissaoConfirmarReserva"/></p>
            <% } %>
          </div>
          <% if (!saldoDevedor) { %>
            <form name="form1" action="../v3/processarLote?acao=listarArquivosImportacao&<%=SynchronizerToken.generateToken4URL(request)%>" method="post">
              <% if (responsavel.isCseSup()) { %>    
                <div class="row">
                  <div class="col-sm-6">
                    <div class="form-group">
                      <label for="permiteLoteAtrasado"><hl:message key="rotulo.lote.ignorar.periodo.atrasado"/>: </label>
                      <input class="form-check-input ml-1" class="form-control" type="checkbox" name="permiteLoteAtrasado" id="permiteLoteAtrasado" onClick="javascript:habilitaPeriodoManual();">
                    </div>
                    <div class="form-group">
                      <label for="periodo"><hl:message key="rotulo.lote.forcar.periodo"/></label>
                      <hl:htmlinput name="periodo" di="periodo" type="text" value="" classe="form-control" size="7" mask="DD/DDDD" placeHolder="<%=LocaleHelper.getPeriodoPlaceHolder()%>" onBlur="<%="return verificaPeriodo(this.value, '" + PeriodoHelper.getPeriodicidadeFolha(responsavel) + "');"%>" others="disabled"/>
                    </div>
                  </div>
                </div>
              <% } else if (csaCorEscolherPeriodo){ %>
                  <div class="row">
                    <div class="col-sm-6">
                      <div class="form-group">
                        <label for="permiteLoteAtrasado"><hl:message key="rotulo.lote.optar.forcar.periodo.csa.cor"/>: </label>
                        <input class="form-check-input ml-1" class="form-control" type="checkbox" name="permiteLoteAtrasado" id="permiteLoteAtrasado" onClick="javascript:habilitaPeriodoManual();">
                      </div>
                      <div class="form-group">
                          <label for="periodo"><hl:message key="rotulo.lote.forcar.periodo"/></label>
                          <select class="form-control form-select" name=periodo id="periodo" disabled>
                            <option value="" selected><hl:message key="rotulo.campo.selecione"/></option>
                            <%for (Date periodo : periodoAgrupado) { %>
                               <option value="<%=DateHelper.toPeriodString(periodo)%>"><%=DateHelper.toPeriodString(periodo)%></option>
                            <%} %>
                          </select> 
                      </div>
                    </div>
                  </div>
              <%} %>
              <div class="row">
                <div class="col-sm-6">
                  <div class="form-group">
                    <label for="permiteReducaoLancamentoCartao"><hl:message key="rotulo.lote.permite.reducao.lancamento.cartao"/>: </label>
                    <input class="form-check-input ml-1" class="form-control" type="checkbox" name="permiteReducaoLancamentoCartao" id="permiteReducaoLancamentoCartao">
                  </div>
                </div>
              </div>
            </form>
          <% } %>
        </div>
      </div>
      <div class="card">
        <div class="card-header pl-3">
          <h2 class="card-header-title"><hl:message key="rotulo.lote.arquivos.disponiveis"/></h2>
        </div>
        <div class="card-body table-responsive p-0">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th scope="col"><hl:message key="rotulo.lote.nome"/></th>
                <th scope="col"><hl:message key="rotulo.lote.tamanho.abreviado"/></th>
                <th scope="col"><hl:message key="rotulo.lote.data"/></th>
                <th scope="col" width="20%"><hl:message key="rotulo.acoes"/></th>
              </tr>
            </thead>
            <tbody>
            <%
              if (arquivos == null || arquivos.size() == 0){
            %>
              <tr>
                <td scope="col" colspan='4'><hl:message key="mensagem.erro.nenhum.arquivo.encontrado"/></td>
              </tr>
            <%
                } else {
                int i = 0;
                int j = offset == -1 ? ((arquivos.size() % size) == 0 ? (arquivos.size() - size) : arquivos.size() - (arquivos.size() % size)) : offset;
                Iterator<?> it = arquivos.iterator();
                while (arquivos.size() > j && i < size) {
                  File arquivo = (File)arquivos.get(j);
                  String tam   = "";
                  if (arquivo.length() > 1024.00) {
                    tam = Math.round(arquivo.length() / 1024.00) + " " + ApplicationResourcesHelper.getMessage("rotulo.kilobyte.abreviado", responsavel);
                  } else {
                    tam = arquivo.length() + " " + ApplicationResourcesHelper.getMessage("rotulo.byte.abreviado", responsavel);
                  }
                  String data    = DateHelper.toDateTimeString(new java.sql.Date(arquivo.lastModified()));
                  String nome    = arquivo.getPath().substring(absolutePath.length());
                  String formato = (nome.toLowerCase().endsWith(".zip") ? "zip.gif" : "text.gif");
            
                  j++;
                  nome = java.net.URLEncoder.encode(nome, "UTF-8");
            %>
              <tr>
                <td><%=TextHelper.forHtmlContent(arquivo.getName())%></td>
                <td><%=TextHelper.forHtmlContent(tam)%></td>
                <td><%=TextHelper.forHtmlContent(data)%></td>
                <td>
                <div class="actions">
                  <div class="dropdown">
                    <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                      <div class="form-inline">
                        <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key='rotulo.mais.acoes'/>" aria-label="<hl:message key='rotulo.mais.acoes'/>">
                          <svg><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                        </span><hl:message key="rotulo.botao.opcoes"/>
                      </div>
                    </a>

                    <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                    <%if(!saldoDevedor) {%>
                        <% if (!(arquivo.getName().toLowerCase().indexOf(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.validacao.prefixo", responsavel)) > -1) ) { %>
                          <% if (podeValidarLote) { %>
                            <a class="dropdown-item" href="#" onClick="doIt('v', '<%=TextHelper.forJavaScript(arquivo.getName())%>', '<%=TextHelper.forJavaScript(nome)%>'); return false;" ><hl:message key="rotulo.acoes.validar"/></a>
                          <% } %>
                          <% if (podeProcessarLote) { %>
                            <a class="dropdown-item" href="#" onClick="doIt('i', '<%=TextHelper.forJavaScript(arquivo.getName())%>', '<%=TextHelper.forJavaScript(nome)%>'); return false;" ><hl:message key="rotulo.acoes.processar"/></a>
                          <% } %>
                        <% } %>
                            <a class="dropdown-item" href="#" onClick="fazDownload('<%=TextHelper.forJavaScript(nome)%>', '<%=TextHelper.forJavaScript(tipo)%>', '<%=TextHelper.forJavaScript(entidade)%>', '<%=TextHelper.forJavaScript(tipoCodigo)%>'); return false;" ><hl:message key="rotulo.acoes.download"/></a>
                          <% if (podeExcluirArqLote) { %>
                            <a class="dropdown-item" href="#" onClick="doIt('e', '<%=TextHelper.forJavaScript(arquivo.getName())%>', '<%=TextHelper.forJavaScript(nome)%>'); return false;" ><hl:message key="rotulo.acoes.excluir"/></a>
                          <% } %>
                    <%} else { %>
                           <% if (!(arquivo.getName().toLowerCase().indexOf(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.validacao.prefixo", responsavel)) > -1) ) { %>
                              <a class="dropdown-item" href="#" onClick="doIt('v', '<%=TextHelper.forJavaScript(arquivo.getName())%>', '<%=TextHelper.forJavaScript(nome)%>'); return false;" ><hl:message key="rotulo.acoes.validar"/></a>
                              <a class="dropdown-item" href="#" onClick="doIt('i', '<%=TextHelper.forJavaScript(arquivo.getName())%>', '<%=TextHelper.forJavaScript(nome)%>'); return false;" ><hl:message key="rotulo.acoes.processar"/></a>
                           <%} %>
                            <a class="dropdown-item" href="#" onClick="fazDownload('<%=TextHelper.forJavaScript(nome)%>', '<%=TextHelper.forJavaScript(tipo)%>', '<%=TextHelper.forJavaScript(entidade)%>', '<%=TextHelper.forJavaScript(tipoCodigo)%>'); return false;" ><hl:message key="rotulo.acoes.download"/></a>
                          <% if (podeExcluirArqLote) { %>
                            <a class="dropdown-item" href="#" onClick="doIt('e', '<%=TextHelper.forJavaScript(arquivo.getName())%>', '<%=TextHelper.forJavaScript(nome)%>'); return false;" ><hl:message key="rotulo.acoes.excluir"/></a>
                          <% } %>
                    <%} %>
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
                <td colspan="4">
                  <hl:message key="rotulo.lote.listagem.lote"/>
                  <span class="font-italic">
                    <hl:message key="rotulo.paginacao.registros.sem.estilo" arg0="${_paginacaoPrimeiro}" arg1="${_paginacaoUltimo}" arg2="${_paginacaoQtdTotal}"/>
                  </span>
                </td>
              </tr>
            </tfoot>
          </table>
            <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
        </div>
      </div>
      <div class="btn-action">
        <a class="btn btn-outline-danger" HREF="#" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.teclado.virtual.cancelar"/></a>
        <input name="MM_update"    type="hidden" value="form1">
        <input name="arquivo_nome" type="hidden" value="">
        <input name="CSA_CODIGO"   type="hidden" value="<%=TextHelper.forHtmlAttribute(csaCodigo)%>">
        <input name="COR_CODIGO"   type="hidden" value="<%=TextHelper.forHtmlAttribute(corCodigo)%>">
        <input name="XML"          type="hidden" value="<%=TextHelper.forHtmlAttribute(xml)%>">
      </div>
  <% } else { %>
      <div class="btn-action">
        <a class="btn btn-outline-danger" aria-label='<hl:message key="rotulo.botao.voltar"/>' href="#" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
      </div>
  <% } %>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript">
  
  doLoad(<%=(boolean)temProcessoRodando%>);
  
  function doIt(opt, arq, path) {
    var msg = '', j;
    <%if(!saldoDevedor) {%>
          <% if (responsavel.isCseSup() || csaCorEscolherPeriodo) { %>
          var loteAtrasado = f0.permiteLoteAtrasado;
          var permiteReducaoLancamentoCartao = f0.permiteReducaoLancamentoCartao;
          var periodoAtrasado = f0.periodo.value;
          if (opt == 'e') {
            msg = '<hl:message key="mensagem.confirmacao.exclusao.lote"/>'.replace("{0}", arq);
            j = '../v3/excluirArquivo?arquivo_nome=' + encodeURIComponent(path) + '&ext=exc' + '&tipo=<%=TextHelper.forJavaScriptBlock(tipo)%>&entidade=<%=TextHelper.forJavaScriptBlock(entidade)%>&subtipo=<%=TextHelper.forJavaScriptBlock(tipoCodigo)%>&link=../v3/processarLote?acao=listarArquivosImportacao&<%=(String)parametros%>|offset(<%=TextHelper.forJavaScriptBlock(offset)%>';
          } else if (opt == 'i') {
            msg =  '<hl:message key="mensagem.confirmacao.processamento.lote"/>'.replace("{0}", arq);
            j = '../v3/processarLote?acao=importar&periodo=' + periodoAtrasado + '&permiteLoteAtrasado=' + loteAtrasado.checked + '&permiteReducaoLancamentoCartao=' + permiteReducaoLancamentoCartao.checked + '&arquivo_nome=' + encodeURIComponent(path) + '&XML=<%=TextHelper.forJavaScriptBlock(xml)%>&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&COR_CODIGO=<%=TextHelper.forJavaScriptAttribute(corCodigo)%>';
          } else if (opt == 'v') {
            msg = '<hl:message key="mensagem.confirmacao.validacao.lote"/>'.replace("{0}", arq);
            j = '../v3/processarLote?acao=validar&periodo=' + periodoAtrasado + '&permiteLoteAtrasado=' + loteAtrasado.checked + '&permiteReducaoLancamentoCartao=' + permiteReducaoLancamentoCartao.checked + '&arquivo_nome=' + encodeURIComponent(path) + '&XML=<%=TextHelper.forJavaScriptBlock(xml)%>&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&COR_CODIGO=<%=TextHelper.forJavaScriptAttribute(corCodigo)%>';
          } else {
            return false;
          }
          <% } else { %>
          var permiteReducaoLancamentoCartao = f0.permiteReducaoLancamentoCartao;
          if (opt == 'e') {
            msg = '<hl:message key="mensagem.confirmacao.exclusao.lote"/>'.replace("{0}", arq);
            j = '../v3/excluirArquivo?arquivo_nome=' + encodeURIComponent(path) + '&ext=exc' + '&tipo=<%=TextHelper.forJavaScriptBlock(tipo)%>&entidade=<%=TextHelper.forJavaScriptBlock(entidade)%>&subtipo=<%=TextHelper.forJavaScriptBlock(tipoCodigo)%>&link=../v3/processarLote?acao=listarArquivosImportacao&<%=(String)parametros%>|offset(<%=TextHelper.forJavaScriptBlock(offset)%>';
          } else if (opt == 'i') {
            msg =  '<hl:message key="mensagem.confirmacao.processamento.lote"/>'.replace("{0}", arq);
            j = '../v3/processarLote?acao=importar' + '&permiteReducaoLancamentoCartao=' + permiteReducaoLancamentoCartao.checked + '&arquivo_nome=' + encodeURIComponent(path) + '&XML=<%=TextHelper.forJavaScriptBlock(xml)%>&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&COR_CODIGO=<%=TextHelper.forJavaScriptAttribute(corCodigo)%>';
          } else if (opt == 'v') {
            msg = '<hl:message key="mensagem.confirmacao.validacao.lote"/>'.replace("{0}", arq);
            j = '../v3/processarLote?acao=validar' + '&permiteReducaoLancamentoCartao=' + permiteReducaoLancamentoCartao.checked + '&arquivo_nome=' + encodeURIComponent(path) + '&XML=<%=TextHelper.forJavaScriptBlock(xml)%>&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&COR_CODIGO=<%=TextHelper.forJavaScriptAttribute(corCodigo)%>';
          } else {
            return false;
          }
          <% } %>
    <%} else {%>
        if (opt == 'e') {
            msg = '<hl:message key="mensagem.confirmacao.exclusao.lote"/>'.replace("{0}", arq);
            j = '../v3/excluirArquivo?arquivo_nome=' + encodeURIComponent(path) + '&ext=exc' + '&tipo=<%=TextHelper.forJavaScriptBlock(tipo)%>&entidade=<%=TextHelper.forJavaScriptBlock(entidade)%>&subtipo=<%=TextHelper.forJavaScriptBlock(tipoCodigo)%>&link=../v3/processarLoteInfoSaldoDevedor?acao=listarArquivosImportacao&<%=(String)parametros%>|offset(<%=TextHelper.forJavaScriptBlock(offset)%>';
          } else if (opt == 'i') {
            msg =  '<hl:message key="mensagem.confirmacao.processamento.lote"/>'.replace("{0}", arq);
            j = '../v3/processarLoteInfoSaldoDevedor?acao=processar&arquivo_nome=' + encodeURIComponent(path) + '&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>';
          } else if (opt == 'v') {
            msg = '<hl:message key="mensagem.confirmacao.validacao.lote"/>'.replace("{0}", arq);
            j = '../v3/processarLoteInfoSaldoDevedor?acao=validar&arquivo_nome=' + encodeURIComponent(path) + '&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>';
          } else {
            return false;
          }
    <%}%>
    
    j = j + '&<%=SynchronizerToken.generateToken4URL(request)%>'
    if (msg != '') {
      if (confirm(msg)) {
        if (opt == 'i' || opt == 'v') {
          postData(j);
        } else {
          postData(j);
        }
      } else {
        return false;
      }
    } else {
      postData(j);
    }
    return true;
  }
  
  function doLoad(reload) {
    if (reload) {
      setTimeout('refresh()', 15*1000);
    }
  }
  
  function refresh() {
    postData("../v3/<%=!saldoDevedor ? "processarLote" : "processarLoteInfoSaldoDevedor"%>?acao=listarArquivosImportacao&CSA_CODIGO=<%=TextHelper.forJavaScriptBlock(csaCodigo)%>&COR_CODIGO=<%=TextHelper.forJavaScriptBlock(corCodigo)%>&XML=<%=TextHelper.forJavaScriptBlock(xml)%>&<%=SynchronizerToken.generateToken4URL(request)%>");
  }
  
  function fazDownload(nome, tipo, entidade, tipoCodigo){
    postData('../v3/downloadArquivo?arquivo_nome='+ nome + '&tipo='+ tipo+ '&entidade=' + entidade +'&subtipo='+ tipoCodigo + '&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>','download');
  }
  
  function habilitaPeriodoManual() {
    var checkLote = f0.permiteLoteAtrasado;
    
    if (checkLote.checked) {
      f0.periodo.disabled = false;
    } else {
      f0.periodo.value = '';
      f0.periodo.disabled = true;
    }
    
    return true;
  }
  //-->
  </script>
  <script type="text/JavaScript">
  	var f0 = document.forms[0];
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>