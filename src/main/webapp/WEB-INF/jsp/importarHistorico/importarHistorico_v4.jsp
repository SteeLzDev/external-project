<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.Pair"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
List lstMtvOperacao = (List)request.getAttribute("lstMtvOperacao");
String motivo = (String) request.getAttribute("motivo");
boolean atalhoUpload = request.getAttribute("atalhoUpload") != null ? (boolean) request.getAttribute("atalhoUpload") : false;

// Path dos arquivos de retorno
String pathHistorico = (String) request.getAttribute("pathHistorico");

// Lista os arquivos
List<Pair<File, String>> arquivosHistorico = (List<Pair<File, String>>) request.getAttribute("arquivosHistorico");
%>
<c:set var="title">
  <hl:message key="mensagem.folha.arquivos.historico.disponiveis.importacao"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>

<c:set var="bodyContent">
  <% if (responsavel.temPermissao(CodedValues.FUN_RECALCULAR_MARGEM_GERAL) || responsavel.temPermissao(CodedValues.FUN_RECALCULAR_MARGEM_PARCIAL) || atalhoUpload) { %>
  <div class="page-title">
    <div class="row d-print-none">
      <div class="col-sm-12 col-md-12 mb-2">
        <div class="float-end">
          <button data-bs-toggle="dropdown" aria-haspopup="true" id="acoes" aria-expanded="false"
                  class="btn btn-primary" type="submit"><hl:message key="rotulo.acoes"/></button>
          <div class="dropdown-menu dropdown-menu-right" aria-labelledby="acoes">
            <%if (atalhoUpload) { %>
            <a class="dropdown-item" href="no-back"
               onclick="postData('../v3/importarHistorico?acao=atalhoUpload&<%=SynchronizerToken.generateToken4URL(request)%>')"><hl:message
                    key="rotulo.atalho.upload"/></a>
            <% } %>
            <% if (responsavel.temPermissao(CodedValues.FUN_RECALCULAR_MARGEM_GERAL)) { %>
            <a class="dropdown-item" href="#no-back"
               onClick="postData('../v3/recalcularMargemGeral?acao=iniciar&direction=4&<%=SynchronizerToken.generateToken4URL(request)%>')">
              <hl:message key="rotulo.recalcular.margem.geral"/> </a>
            <% } %>
            <% if (responsavel.temPermissao(CodedValues.FUN_RECALCULAR_MARGEM_PARCIAL)) {%>
            <a class="dropdown-item" href="#no-back"
               onClick="postData('../v3/recalcularMargemParcial?acao=iniciar&direction=4&<%=SynchronizerToken.generateToken4URL(request)%>')">
              <hl:message key="rotulo.recalcular.margem.parcial"/> </a>
            <% } %>
          </div>
        </div>
      </div>
    </div>
  </div>
  <% } %>
  <form name="form1" action="../v3/importarHistorico?acao=importar&<%=SynchronizerToken.generateToken4URL(request)%>" method="post">
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><hl:message key="rotulo.editar.grid"/></h2>
      </div>
      <div class="card-body">
        <dl class="row data-list">
          <dt class="col-6"><hl:message key="rotulo.folha.historico.apenas.servidores.ativos"/></dt>
          <dd class="col-6">
            <span class="mr-4">
              <label for="servidor-ativo-sim"><hl:message key="rotulo.sim"/></label>
              <input class="form-check-input ml-1" type="radio" name="servidoresAtivos_" value="true" id="servidoresAtivos_">                
            </span>
            <span class="mr-4">
              <label for="servidor-ativo-nao"><hl:message key="rotulo.nao"/></label>
              <input class="form-check-input ml-1" type="radio" name="servidoresAtivos_" value="false" id="servidoresAtivos_" checked>
            </span>
          </dd>
          <dt class="col-6"><hl:message key="rotulo.folha.historico.apenas.convenios.ativos"/></dt>
          <dd class="col-6">
            <span class="mr-4">
              <label for="servidor-ativo-sim"><hl:message key="rotulo.sim"/></label>
              <input class="form-check-input ml-1" type="radio" name="conveniosAtivos_" value="true" id="conveniosAtivos_">                
            </span>
            <span class="mr-4">
              <label for="servidor-ativo-nao"><hl:message key="rotulo.nao"/></label>
              <input class="form-check-input ml-1" type="radio" name="conveniosAtivos_" value="false" id="conveniosAtivos_" checked>
            </span>
          </dd>
          
          <dt class="col-6"><hl:message key="rotulo.folha.historico.apenas.servicos.ativos"/></dt>
          <dd class="col-6">
            <span class="mr-4">
              <label for="servidor-ativo-sim"><hl:message key="rotulo.sim"/></label>
              <input class="form-check-input ml-1" type="radio" name="servicosAtivos_" value="true" id="servicosAtivos_">                
            </span>
            <span class="mr-4">
              <label for="servidor-ativo-nao"><hl:message key="rotulo.nao"/></label>
              <input class="form-check-input ml-1" type="radio" name="servicosAtivos_" value="false" id="servicosAtivos_" checked>
            </span>
          </dd>          
          <dt class="col-6"><hl:message key="rotulo.folha.historico.apenas.servidores.ativos.convenio"/></dt>
          <dd class="col-6">
            <span class="mr-4">
              <label for="servidor-ativo-sim"><hl:message key="rotulo.sim"/></label>
              <input class="form-check-input ml-1" type="radio" name="servidoresConvenioAtivos_" value="true" id="servidoresConvenioAtivos_">                
            </span>
            <span class="mr-4">
              <label for="servidor-ativo-nao"><hl:message key="rotulo.nao"/></label>
              <input class="form-check-input ml-1" type="radio" name="servidoresConvenioAtivos_" value="false" id="servidoresConvenioAtivos_" checked>
            </span>
          </dd>          
          <dt class="col-6"><hl:message key="rotulo.folha.historico.apenas.consignatarias.ativas"/></dt>
          <dd class="col-6">
            <span class="mr-4">
              <label for="servidor-ativo-sim"><hl:message key="rotulo.sim"/></label>
              <input class="form-check-input ml-1" type="radio" name="consignatariasAtivas_" value="true" id="consignatariasAtivas_">                
            </span>
            <span class="mr-4">
              <label for="servidor-ativo-nao"><hl:message key="rotulo.nao"/></label>
              <input class="form-check-input ml-1" type="radio" name="consignatariasAtivas_" value="false" id="consignatariasAtivas_" checked>
            </span>
          </dd>          
          <dt class="col-6"><hl:message key="rotulo.folha.historico.apenas.orgaos.ativos"/></dt>
          <dd class="col-6">
            <span class="mr-4">
              <label for="servidor-ativo-sim"><hl:message key="rotulo.sim"/></label>
              <input class="form-check-input ml-1" type="radio" name="orgaosAtivos_" value="true" id="orgaosAtivos_">                
            </span>
            <span class="mr-4">
              <label for="servidor-ativo-nao"><hl:message key="rotulo.nao"/></label>
              <input class="form-check-input ml-1" type="radio" name="orgaosAtivos_" value="false" id="orgaosAtivos_" checked>
            </span>
          </dd>          
          <dt class="col-6"><hl:message key="rotulo.folha.historico.apenas.estabelecimentos.ativos"/></dt>
          <dd class="col-6">
            <span class="mr-4">
              <label for="servidor-ativo-sim"><hl:message key="rotulo.sim"/></label>
              <input class="form-check-input ml-1" type="radio" name="estabelecimentosAtivos_" value="true" id="estabelecimentosAtivos_">                
            </span>
            <span class="mr-4">
              <label for="servidor-ativo-nao"><hl:message key="rotulo.nao"/></label>
              <input class="form-check-input ml-1" type="radio" name="estabelecimentosAtivos_" value="false" id="estabelecimentosAtivos_" checked>
            </span>
          </dd>          
          <dt class="col-6"><hl:message key="rotulo.folha.historico.apenas.consignantes.ativos"/></dt>
          <dd class="col-6">
            <span class="mr-4">
              <label for="servidor-ativo-sim"><hl:message key="rotulo.sim"/></label>
              <input class="form-check-input ml-1" type="radio" name="consignantesAtivos_" value="true" id="consignantesAtivos_">                
            </span>
            <span class="mr-4">
              <label for="servidor-ativo-nao"><hl:message key="rotulo.nao"/></label>
              <input class="form-check-input ml-1" type="radio" name="consignantesAtivos_" value="false" id="consignantesAtivos_" checked>
            </span>
          </dd>          
          <dt class="col-6"><hl:message key="rotulo.folha.historico.validar.taxa.juros"/></dt>
          <dd class="col-6">
            <span class="mr-4">
              <label for="servidor-ativo-sim"><hl:message key="rotulo.sim"/></label>
              <input class="form-check-input ml-1" type="radio" name="validarTaxaJuros_" value="true" id="validarTaxaJuros_" checked>                
            </span>
            <span class="mr-4">
              <label for="servidor-ativo-nao"><hl:message key="rotulo.nao"/></label>
              <input class="form-check-input ml-1" type="radio" name="validarTaxaJuros_" value="false" id="validarTaxaJuros_">
            </span>
          </dd>          
          <dt class="col-6"><hl:message key="rotulo.folha.historico.validar.prazo"/></dt>
          <dd class="col-6">
            <span class="mr-4">
              <label for="servidor-ativo-sim"><hl:message key="rotulo.sim"/></label>
              <input class="form-check-input ml-1" type="radio" name="validarPrazo_" value="true" id="validarPrazo_" checked>                
            </span>
            <span class="mr-4">
              <label for="servidor-ativo-nao"><hl:message key="rotulo.nao"/></label>
              <input class="form-check-input ml-1" type="radio" name="validarPrazo_" value="false" id="validarPrazo_">
            </span>
          </dd>
          
          <dt class="col-6"><hl:message key="rotulo.folha.historico.validar.dados.bancarios"/></dt>
          <dd class="col-6">
            <span class="mr-4">
              <label for="servidor-ativo-sim"><hl:message key="rotulo.sim"/></label>
              <input class="form-check-input ml-1" type="radio" name="validarDadosBancarios_" value="true" id="validarDadosBancarios_" checked>                
            </span>
            <span class="mr-4">
              <label for="servidor-ativo-nao"><hl:message key="rotulo.nao"/></label>
              <input class="form-check-input ml-1" type="radio" name="validarDadosBancarios_" value="false" id="validarDadosBancarios_">
            </span>
          </dd>          
          <dt class="col-6"><hl:message key="rotulo.folha.historico.validar.senha.servidor"/></dt>
          <dd class="col-6">
            <span class="mr-4">
              <label for="servidor-ativo-sim"><hl:message key="rotulo.sim"/></label>
              <input class="form-check-input ml-1" type="radio" name="validarSenhaServidor_" value="true" id="validarSenhaServidor_" checked>                
            </span>
            <span class="mr-4">
              <label for="servidor-ativo-nao"><hl:message key="rotulo.nao"/></label>
              <input class="form-check-input ml-1" type="radio" name="validarSenhaServidor_" value="false" id="validarSenhaServidor_">
            </span>
          </dd>          
          <dt class="col-6"><hl:message key="rotulo.folha.historico.validar.data.nascimento"/></dt>
          <dd class="col-6">
            <span class="mr-4">
              <label for="servidor-ativo-sim"><hl:message key="rotulo.sim"/></label>
              <input class="form-check-input ml-1" type="radio" name="validarDataNasc_" value="true" id="validarDataNasc_" checked>                
            </span>
            <span class="mr-4">
              <label for="servidor-ativo-nao"><hl:message key="rotulo.nao"/></label>
              <input class="form-check-input ml-1" type="radio" name="validarDataNasc_" value="false" id="validarDataNasc_">
            </span>
          </dd>          
          <dt class="col-6"><hl:message key="rotulo.folha.historico.validar.limite.contrato"/></dt>
          <dd class="col-6">
            <span class="mr-4">
              <label for="servidor-ativo-sim"><hl:message key="rotulo.sim"/></label>
              <input class="form-check-input ml-1" type="radio" name="validarLimiteContrato_" value="true" id="validarLimiteContrato_" checked>                
            </span>
            <span class="mr-4">
              <label for="servidor-ativo-nao"><hl:message key="rotulo.nao"/></label>
              <input class="form-check-input ml-1" type="radio" name="validarLimiteContrato_" value="false" id="validarLimiteContrato_">
            </span>
          </dd>
          <dt class="col-6"><hl:message key="rotulo.folha.historico.motivo.operacao"/></dt>
          <dd class="col-6">
            <%=JspHelper.geraCombo(lstMtvOperacao, "tmoCodigo_", Columns.TMO_CODIGO, Columns.TMO_DESCRICAO, (motivo == null ? ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel) : motivo) , "class=\"form-control\"", false, 1)%>
          </dd>
          <dt class="col-6"><hl:message key="rotulo.folha.historico.observacao"/></dt>
          <dd class="col-6">
            <textarea class="form-control" placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.obs",responsavel)%>' rows="6" name="adeObs_" id="adeObs_" onFocus="SetarEventoMascara(this,'#*10000',true);" onBlur="fout(this);ValidaMascara(this);"></textarea>
          </dd>          
        </dl>
      </div>
    </div>
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><hl:message key="mensagem.folha.arquivos.historico.disponiveis.importacao"/></h2>
      </div>
      <div class="card-body table-responsive">
        <table class="table table-striped table-hover">
          <thead>
            <tr>
              <th scope="col"><hl:message key="rotulo.folha.nome"/></th>
              <th scope="col"><hl:message key="rotulo.folha.tamanho.abreviado"/> </th>
              <th scope="col"><hl:message key="rotulo.folha.data"/></th>
              <th scope="col"><hl:message key="rotulo.estabelecimento.abreviado"/> - <hl:message key="rotulo.orgao.abreviado"/></th>
              <th scope="col"><hl:message key="rotulo.acoes"/></th>
            </tr>            
          </thead>
          <tboby>
<%
  if (arquivosHistorico == null || arquivosHistorico.size() == 0){
%>
            <tr class="Lp">
              <td colspan="7"><hl:message key="mensagem.folha.nenhum.arquivo.encontrado"/></td>
            </tr>
<%
    } else {
      for (Pair<File, String> arquivoHistorico : arquivosHistorico) {
        File arquivo = arquivoHistorico.first;
        String entidade = arquivoHistorico.second;
        String tam = "";
        if (arquivo.length() > 1024.00) {
          tam = Math.round(arquivo.length() / 1024.00) + " " + ApplicationResourcesHelper.getMessage("rotulo.kilobyte.abreviado", responsavel);
        } else {
          tam = arquivo.length() + " " + ApplicationResourcesHelper.getMessage("rotulo.byte.abreviado", responsavel);
        }
        String data = DateHelper.toDateTimeString(new java.sql.Date(arquivo.lastModified()));
        String nome = arquivo.getPath().substring(pathHistorico.length());
        String nomeCodificado = java.net.URLEncoder.encode(nome, "UTF-8");
        String formato = (nome.toLowerCase().endsWith(".zip") ? "zip.gif" : "text.gif");
%>         
            <tr>
              <td><%=TextHelper.forHtmlContent(arquivo.getName())%></td>
              <td><%=TextHelper.forHtmlContent(tam)%>&nbsp;</td>
              <td><%=TextHelper.forHtmlContent(data)%></td>
              <td><%=TextHelper.forHtmlContent(entidade)%></td>
              <td class="acoes">
                   <div class="actions">
                    <div class="dropdown">
                      <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <div class="form-inline">
                          <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<%=ApplicationResourcesHelper.getMessage("rotulo.acoes", responsavel)%>" aria-label="<%=ApplicationResourcesHelper.getMessage("rotulo.fila.op.sensiveis.ver.detalhes", responsavel)%>"> <svg>
                              <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                          </span> <hl:message key="rotulo.botao.opcoes"/>
                        </div>
                      </a>
                      <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                         <a class="dropdown-item" href="#" name="submit1" onClick="javascript:importaHistorico('<%=TextHelper.forJavaScript(nome)%>', '<%=TextHelper.forJavaScript(arquivo.getName())%>'); return false;"><hl:message key="rotulo.acoes.importar"/></a>
                         <a class="dropdown-item" href="#" name="download" onClick="javascript:downloadArquivo(encodeURIComponent('<%=TextHelper.forJavaScript(nomeCodificado)%>'),'historico'); return false;"><hl:message key="rotulo.acoes.download"/></a>                         
                         <a class="dropdown-item" href="#" name="excluir" onClick="javascript:removeArquivo('<%=TextHelper.forJavaScript(nomeCodificado)%>', '<%=TextHelper.forJavaScript(arquivo.getName())%>'); return false;"><hl:message key="rotulo.acoes.excluir"/></a>
                      </div>
                    </div>
                   </div>   
                </td>
            </tr>
<%
      }
    }
%>            
            
          </tboby>
        </table>
      </div>
    </div>
    <input name="arquivo_nome" type="hidden" value="">
    <div class="btn-action">
      <a class="btn btn-outline-danger" id="btnVoltar" href="#no-back" onClick="postData('../v3/carregarPrincipal')"><hl:message key="rotulo.botao.voltar"/></a>
    </div>
      <!-- Modal aguarde -->
	  <div class="modal fade" id="modalAguarde" data-focus="false" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
	   <div class="modal-dialog-upload modal-dialog" role="document">
		 <div class="modal-content">
		   <div class="modal-body">
			 <div class="row">
			   <div class="col-md-12 d-flex justify-content-center">
				 <img src="../img/loading.gif" class="loading">
			   </div>
			   <div class="col-md-12">
				 <div class="modal-body"><span><hl:message key="mensagem.folha.aguarde.importacao"/></span></div>            
			   </div>
			 </div>
		   </div>
		 </div>
	   </div>
	  </div>
  </form>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript">
    var f0 = document.forms[0];
  </script>
  <script type="text/JavaScript">
    function importaHistorico(arquivo, nomeArq) {
      var msg = '<hl:message key="mensagem.confirmacao.importa.historico"/>'.replace("{0}", nomeArq);
      if(confirm(msg)){
        f0.arquivo_nome.value = arquivo;
        $('#modalAguarde').modal({
			backdrop: 'static',
			keyboard: false
		});
            f0.submit();
      }
    }
    
    function downloadArquivo(arquivo,tipo) {
      postData('../v3/downloadArquivo?arquivo_nome=' + arquivo + '&tipo=' + tipo + '&skip_history=true' + '&<%=SynchronizerToken.generateToken4URL(request)%>','download');
    }
    
    function removeArquivo(arquivo, nome) {
      var msg = '<hl:message key="mensagem.folha.confirmacao.remover.arquivo.nome"/>'.replace('{0}', nome);
      if (confirm(msg)) {
        postData('../v3/importarHistorico?acao=excluirArquivoHistorico&arquivo_nome=' + encodeURIComponent(arquivo) + '&<%=SynchronizerToken.generateToken4URL(request)%>');
      }
    }
  </script>
  <script type="text/JavaScript" src="../js/scripts_2810.js?<hl:message key="release.tag"/>"></script>
  <script type="text/JavaScript" src="../js/validacoes.js?<hl:message key="release.tag"/>"></script>
  <script type="text/JavaScript" src="../js/validaform.js?<hl:message key="release.tag"/>"></script>
  <script type="text/JavaScript" src="../js/xbdhtml.js?<hl:message key="release.tag"/>"></script>
  <script type="text/JavaScript" src="../js/scripts_2810.js?<hl:message key="release.tag"/>"></script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>