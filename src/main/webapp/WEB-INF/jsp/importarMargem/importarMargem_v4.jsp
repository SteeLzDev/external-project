<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ page import="com.zetra.econsig.dto.web.ArquivoDownload"%>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
boolean atalhoUpload = request.getAttribute("atalhoUpload") != null ? (boolean) request.getAttribute("atalhoUpload") : false;
boolean margemTotal = (boolean) request.getAttribute("margemTotal");
boolean geraTrans = (boolean) request.getAttribute("geraTrans");
boolean temProcessoRodando = (boolean) request.getAttribute("temProcessoRodando");
List<TransferObject> lstEstabelecimentos = (List) request.getAttribute("lstEstabelecimentos");
List<TransferObject> lstOrgaos = (List) request.getAttribute("lstOrgaos");

List<ArquivoDownload> arquivosMargem = (List) request.getAttribute("arquivosMargem");
List<ArquivoDownload> arquivosMargemComplementar = (List) request.getAttribute("arquivosMargemComplementar");
List<ArquivoDownload> arquivosTransferidos = (List) request.getAttribute("arquivosTransferidos");
%>
<c:set var="title">
  <hl:message key="rotulo.folha.processamento.margens.transferidos.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
<form method="post">
  <div class="modal fade" id="dialog" tabindex="-1" role="dialog" aria-labelledby="modalTitulo" aria-hidden="true" style="display: none;">
    <div class="modal-dialog modal-wide-content modal-dia-corte" role="document">
      <div class="modal-content">
        <div class="modal-header pb-0">
          <h5 class="modal-title about-title mb-0" id="modalTitulo"><hl:message key="mensagem.informe.folha.dados.confirmacao"/></h5>
          <button type="button" class="logout mr-2" data-bs-dismiss="modal" aria-label="<hl:message key="rotulo.botao.fechar"/>">
            <span aria-hidden="true">×</span>
          </button>
        </div>
        <div class="modal-body pb-0 pt-1">
          <% if (responsavel.isCseSup()) { %>
          <div class="row">
            <div class="form-group col-sm-12">
              <span id="iEntidade"><hl:message key="rotulo.recalcula.margem.entidade"/></span>
              <div class="form-check mt-2" role="radio-group" area-labeldbay="iEntidade">
                <div class="form-check">
                  <input class="form-check-input mt-1 ml-1" type="radio" name="tipoEntidade" id="tipoEntidadeGeral" onChange="alterarTipoEntidade()" value="<%=(String)AcessoSistema.ENTIDADE_CSE%>" checked onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                  <label class="form-check-label labelSemNegrito ml-1 pr-4" for="tipoEntidadeGeral"><hl:message key="rotulo.geral.singular"/></label>
                </div>
                <div class="form-check">
                  <input class="form-check-input mt-1 ml-1" type="radio" name="tipoEntidade" id="tipoEntidadeEstabelecimento" onChange="alterarTipoEntidade()" value="<%=(String)AcessoSistema.ENTIDADE_EST%>" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                  <label class="form-check-label labelSemNegrito ml-1 pr-4 pt-1" for="tipoEntidadeEstabelecimento"><hl:message key="rotulo.estabelecimento.singular"/></label>
                </div>
                <div class="form-check">
                  <input class="form-check-input mt-1 ml-1" type="radio" name="tipoEntidade" id="tipoEntidadeOrgao" onChange="alterarTipoEntidade()" value="<%=(String)AcessoSistema.ENTIDADE_ORG%>" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                  <label class="form-check-label labelSemNegrito ml-1 pr-4 pt-1" for="tipoEntidadeOrgao"><hl:message key="rotulo.orgao.singular"/></label>
                </div>
              </div>
            </div>
          </div>
          <div class="row">
            <div class="form-group col-sm-12">
              <label for="estCodigo"><hl:message key="rotulo.estabelecimento.singular"/></label>
              <%=JspHelper.geraCombo(lstEstabelecimentos, "estCodigo", Columns.EST_CODIGO, Columns.EST_NOME + ";" + Columns.EST_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel) , "", false, 1, "", "", true,"form-control")%>
            </div>
          </div>
          <div class="row">
            <div class="form-group col-sm-12">
              <label for="orgCodigo"><hl:message key="rotulo.orgao.singular"/></label>
              <%=JspHelper.geraCombo(lstOrgaos, "orgCodigo", Columns.ORG_CODIGO, Columns.ORG_NOME + ";" + Columns.ORG_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), "", false, 1, "", "", true, "form-control")%>
            </div>
          </div>
        <% } %>
        </div>
        <div class="modal-footer pt-0">
          <div class="btn-action mt-2 mb-0">
            <a class="btn btn-outline-danger" data-bs-dismiss="modal" href="#"><hl:message key="rotulo.botao.cancelar"/></a>
            <a class="btn btn-primary" data-bs-dismiss="modal" href="#noback" onClick="confirmaImportar()">
              <hl:message key="rotulo.botao.confirmar"/>
            </a>
          </div>
        </div>
      </div>
    </div>
  </div>
  <% if (!temProcessoRodando) { %>
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
               onclick="postData('../v3/importarMargem?acao=atalhoUpload&<%=SynchronizerToken.generateToken4URL(request)%>')"><hl:message
                    key="rotulo.atalho.upload"/></a>
            <% } %>
            <% if (responsavel.temPermissao(CodedValues.FUN_RECALCULAR_MARGEM_GERAL)) { %>
            <a class="dropdown-item" href="#no-back"
               onClick="postData('../v3/recalcularMargemGeral?acao=iniciar&direction=1&<%=SynchronizerToken.generateToken4URL(request)%>')">
              <hl:message key="rotulo.recalcular.margem.geral"/> </a>
            <% } %>
            <% if (responsavel.temPermissao(CodedValues.FUN_RECALCULAR_MARGEM_PARCIAL)) {%>
            <a class="dropdown-item" href="#no-back"
               onClick="postData('../v3/recalcularMargemParcial?acao=iniciar&direction=1&<%=SynchronizerToken.generateToken4URL(request)%>')">
              <hl:message key="rotulo.recalcular.margem.parcial"/> </a>
            <% } %>
          </div>
        </div>
      </div>
    </div>
  </div>
  <% } %>
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="mensagem.folha.arquivos.margens.disponiveis.importacao"/></h2>
    </div>
    <div class="card-body table-responsive p-0">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th scope="col"><hl:message key="rotulo.folha.nome"/></th>
            <th scope="col"><hl:message key="rotulo.folha.tamanho.abreviado"/></th>
            <th scope="col"><hl:message key="rotulo.folha.data"/></th>
            <% if (responsavel.isCseSup()) { %>
            <th scope="col"><hl:message key = "rotulo.estabelecimento.abreviado"/> - <hl:message key="rotulo.orgao.abreviado"/></th>
            <% } %>
            <th scope="col" width="10%"><hl:message key="rotulo.folha.margem.total"/></th>
            <th scope="col" width="10%"><hl:message key="rotulo.folha.gerar.transferidos"/></th>
            <th scope="col"><hl:message key="rotulo.acoes"/></th>
          </tr>
        </thead>
        <tbody>
        <%
          if (arquivosMargem == null || arquivosMargem.size() == 0) {
        %>
            <tr class="Lp">
              <td colspan="7"><hl:message key="mensagem.folha.nenhum.arquivo.encontrado"/></td>
            </tr>
        <%
            } else {
              int contador = 0;
              int i = 0;
              for (ArquivoDownload arquivoMargem : arquivosMargem) {
                String entidade = arquivoMargem.getEntidade();
                String tam = arquivoMargem.getTamanho();
                String data = arquivoMargem.getData();
                String nome = arquivoMargem.getNome();
                String nomeOriginal = arquivoMargem.getNomeOriginal();
                String formato = arquivoMargem.getFormatoImagem();
        %>
          <tr>
            <td><%=TextHelper.forHtmlContent(nomeOriginal)%></td>
            <td align="right"><%=TextHelper.forHtmlContent(tam)%>&nbsp;</td>
            <td align="center"><%=TextHelper.forHtmlContent(data)%></td>
            <% if (responsavel.isCseSup()) { %>
            <td align="center"><%=TextHelper.forHtmlContent(entidade)%></td>
            <% } %>
            <td class="align-middle" aria-label='<hl:message key="mensagem.folha.selecione.margem.total"/>' title='<hl:message key="mensagem.folha.selecione.margem.total"/>'>
              <div class="d-flex justify-content-center">
                <input class="form-check-input mb-1 ml-1" type="checkbox" name="chk<%=(int)contador%>" value="<%=TextHelper.forHtmlAttribute(nome)%>" id="chk<%=(int)contador%>" <%if(margemTotal){%>checked<%}%>>
              </div>
            </td>
            <td class="align-middle" aria-label='<hl:message key="mensagem.folha.selecione.gerar.transferidos"/>' title='<hl:message key="mensagem.folha.selecione.gerar.transferidos"/>'>
              <div class="d-flex justify-content-center">
                <input class="form-check-input mb-1 ml-1" type="checkbox" name="chkTRANSFERIDOS<%=(int)contador%>" value="<%=TextHelper.forHtmlAttribute(nome)%>" ID="chkTRANSFERIDOS<%=(int)contador%>" <%if(geraTrans){%>checked<%}%>>
              </div>
            </td>
            <td>
              <div class="actions">
                <div class="dropdown">
                  <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <div class="form-inline">
                      <span class="mr-1" data-bs-toggle="tooltip" aria-label='<hl:message key="rotulo.botao.opcoes"/>' title="" data-original-title='<hl:message key="rotulo.botao.opcoes"/>'><svg>
                      <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg></span><hl:message key="rotulo.botao.opcoes"/>
                    </div>
                  </a>
                  <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                    <a class="dropdown-item" href="#no-back" onClick="downloadArquivo(encodeURIComponent('<%=TextHelper.forJavaScript(nome)%>'),'margem');"><hl:message key="rotulo.acoes.download"/></a>
                    <a class="dropdown-item" href="#no-back" onClick="importaMargem(encodeURIComponent('<%=TextHelper.forJavaScript(nome)%>'),'chk<%=(int)contador%>', 'chkTRANSFERIDOS<%=(int)contador%>');"><hl:message key="rotulo.acoes.importar"/></a>
                    <a class="dropdown-item" href="#no-back" onClick="doIt('e', '<%=TextHelper.forJavaScript(nome)%>', '<%=TextHelper.forJavaScript(nomeOriginal)%>','m');"><hl:message key="rotulo.acoes.excluir"/></a>
                  </div>
                </div>
              </div>
            </td>
          </tr>
          <%
                  contador++;
                }
              }
          %>
        </tbody>
        <tfoot>
          <tr>
            <td colspan="7"><hl:message key="mensagem.folha.arquivos.margens.disponiveis.importacao.listagem"/>
            </td>
          </tr>
        </tfoot>
      </table>
    </div>
  </div>
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="mensagem.folha.arquivos.margens.complementares.disponiveis.importacao"/></h2>
    </div>
    <div class="card-body table-responsive p-0">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th scope="col"><hl:message key="rotulo.folha.nome"/></th>
            <th scope="col"><hl:message key="rotulo.folha.tamanho.abreviado"/></th>
            <th scope="col"><hl:message key="rotulo.folha.data"/></th>
            <% if (responsavel.isCseSup()) { %>
            <th scope="col"><hl:message key = "rotulo.estabelecimento.abreviado"/> - <hl:message key="rotulo.orgao.abreviado"/></th>
            <% } %>
            <th scope="col"><hl:message key="rotulo.acoes"/></th>
          </tr>
        </thead>
        <tbody>
          <%
              if (arquivosMargemComplementar == null || arquivosMargemComplementar.size() == 0) {
          %>
              <tr class="Lp">
                <td colspan="8"><hl:message key="mensagem.folha.nenhum.arquivo.encontrado"/></td>
              </tr>
          <%
              } else {
                int i = 0;
                for (ArquivoDownload arquivoMargemComplementar : arquivosMargemComplementar) {
                  String entidade = arquivoMargemComplementar.getEntidade();
                  String tam = arquivoMargemComplementar.getTamanho();
                  String data = arquivoMargemComplementar.getData();
                  String nome = arquivoMargemComplementar.getNome();
                  String nomeOriginal = arquivoMargemComplementar.getNomeOriginal();
                  String formato = arquivoMargemComplementar.getFormatoImagem();
          %>
                  <tr>
                    <td><%=TextHelper.forHtmlContent(nomeOriginal)%></td>
                    <td align="right"><%=TextHelper.forHtmlContent(tam)%>&nbsp;</td>
                    <td align="center"><%=TextHelper.forHtmlContent(data)%></td>
                    <% if (responsavel.isCseSup()) { %>
                    <td align="center"><%=TextHelper.forHtmlContent(entidade)%></td>
                    <% } %>
                    <td>
                      <div class="actions">
                        <div class="dropdown">
                          <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                            <div class="form-inline">
                              <span class="mr-1" data-bs-toggle="tooltip" aria-label='<hl:message key="rotulo.botao.opcoes"/>' title="" data-original-title='<hl:message key="rotulo.botao.opcoes"/>'><svg>
                              <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg></span><hl:message key="rotulo.botao.opcoes"/>
                            </div>
                          </a>
                          <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu" x-placement="top-end" style="position: absolute; transform: translate3d(724px, 65px, 0px); top: 0px; left: 0px; will-change: transform;">
                            <a class="dropdown-item" href="#no-back" onClick="downloadArquivo(encodeURIComponent('<%=TextHelper.forJavaScript(nome)%>'),'margemcomplementar');"><hl:message key="rotulo.acoes.download"/></a>
                            <a class="dropdown-item" href="#no-back" onClick="importaMargemComplementarSelecionaEntidade(encodeURIComponent('<%=TextHelper.forJavaScript(nome)%>'), <%=responsavel.isCseSup()%>);"><hl:message key="rotulo.acoes.importar"/></a>
                            <a class="dropdown-item" href="#no-back" onClick="doIt('e', '<%=TextHelper.forJavaScript(nome)%>', '<%=TextHelper.forJavaScript(nomeOriginal)%>','mc');"><hl:message key="rotulo.acoes.excluir"/></a>
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
            <td colspan="5"><hl:message key="mensagem.folha.arquivos.margens.disponiveis.importacao.listagem.complementares"/></td>
          </tr>
        </tfoot>
      </table>
    </div>
  </div>
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="mensagem.folha.arquivos.transferidos.disponiveis.importacao"/></h2>
    </div>
    <div class="card-body table-responsive p-0">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th scope="col"><hl:message key="rotulo.folha.nome"/></th>
            <th scope="col"><hl:message key="rotulo.folha.tamanho.abreviado"/></th>
            <th scope="col"><hl:message key="rotulo.folha.data"/></th>
            <% if (responsavel.isCseSup()) { %>
            <th scope="col"><hl:message key = "rotulo.estabelecimento.abreviado"/> - <hl:message key="rotulo.orgao.abreviado"/></th>
            <% } %>
            <th scope="col"><hl:message key="rotulo.acoes"/></th>
          </tr>
        </thead>
        <tbody>
          <%
              if (arquivosTransferidos == null || arquivosTransferidos.size() == 0){
          %>
              <tr class="Lp">
                <td colspan="8"><hl:message key="mensagem.folha.nenhum.arquivo.encontrado"/></td>
              </tr>
          <%
              } else {
                for (ArquivoDownload arquivoTransferidos : arquivosTransferidos) {
                  String entidade = arquivoTransferidos.getEntidade();
                  String tam = arquivoTransferidos.getTamanho();
                  String data = arquivoTransferidos.getData();
                  String nome = arquivoTransferidos.getNome();
                  String nomeOriginal = arquivoTransferidos.getNomeOriginal();
                  String formato = arquivoTransferidos.getFormatoImagem();
          %>
                  <tr>
                    <td><%=TextHelper.forHtmlContent(nomeOriginal)%></td>
                    <td align="right"><%=TextHelper.forHtmlContent(tam)%>&nbsp;</td>
                    <td align="center"><%=TextHelper.forHtmlContent(data)%></td>
                    <% if (responsavel.isCseSup()) { %>
                    <td align="center"><%=TextHelper.forHtmlContent(entidade)%></td>
                    <% } %>
                    <td>
                      <div class="actions">
                        <div class="dropdown">
                          <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                            <div class="form-inline">
                              <span class="mr-1" data-bs-toggle="tooltip" aria-label='<hl:message key="rotulo.botao.opcoes"/>' title="" data-original-title='<hl:message key="rotulo.botao.opcoes"/>'><svg>
                              <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg></span><hl:message key="rotulo.botao.opcoes"/>
                            </div>
                          </a>
                          <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu" x-placement="top-end" style="position: absolute; transform: translate3d(724px, 65px, 0px); top: 0px; left: 0px; will-change: transform;">
                            <a class="dropdown-item" href="#no-back" onClick="downloadArquivo(encodeURIComponent('<%=TextHelper.forJavaScript(nome)%>'),'transferidos');"><hl:message key="rotulo.acoes.download"/></a>
                            <a class="dropdown-item" href="#no-back" onClick="importaTransferidos(encodeURIComponent('<%=TextHelper.forJavaScript(nome)%>'));"><hl:message key="rotulo.acoes.importar"/></a>
                            <a class="dropdown-item" href="#no-back" onClick="doIt('e', '<%=TextHelper.forJavaScript(nome)%>', '<%=TextHelper.forJavaScript(nomeOriginal)%>','t');"><hl:message key="rotulo.acoes.excluir"/></a>
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
            <td colspan="5"><hl:message key="mensagem.folha.arquivos.margens.disponiveis.importacao.listagem.transferidos"/></td>
          </tr>
        </tfoot>
      </table>
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" id="btnVoltar" href="#no-back" onClick="postData('../v3/carregarPrincipal')"><hl:message key="rotulo.botao.cancelar"/></a>
  </div>
  <% } else { %>
  <div class="btn-action">
    <a class="btn btn-outline-danger" id="btnVoltar" href="#no-back" onClick="postData('../v3/carregarPrincipal')"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
  <% } %>
</form>
</c:set>
<c:set var="javascript">
<link rel="stylesheet" href="../css/jquery-dialog.css?<hl:message key="release.tag"/>" type="text/css">
<script type="text/JavaScript">
  var f0 = document.forms[0];
  var marcado = false;
  var enderecoRetornoBase = "../v3/importarMargem?acao=processar&arquivo_nome=";
  var enderecoRetorno = "";
  var arquivoCache, isCseSupCache = null

  function importaMargem(arquivo, nomeBox, nomeBox2) {
    if (f0.elements[nomeBox].checked) {
      total = "S";
    } else {
      total = "N";
    }
  
    if (f0.elements[nomeBox2].checked) {
      transferidos = "S";
    } else {
      transferidos = "N";
    }
  
    if (transferidos == 'S' && total == 'N') {
      alert('<hl:message key="mensagem.folha.tranferidos.somente.margem.total.selecionada"/>');
      return false;
    }
    if (confirm('<hl:message key="mensagem.folha.confirmacao.importacao.margens"/>')) {
      enderecoRetorno = enderecoRetornoBase + arquivo + "&TIPO=MARGEM&TOTAL=" + total + "&TRANSFERIDOS=" + transferidos + "&<%=SynchronizerToken.generateToken4URL(request)%>";
      postData(enderecoRetorno);
      return;
    }
  }
  
  function importaTransferidos(arquivo) {
    if (confirm('<hl:message key="mensagem.folha.confirmacao.importacao.transferidos"/>')) {
      enderecoRetorno = enderecoRetornoBase + arquivo + "&TIPO=TRANSFERIDOS" + "&<%=SynchronizerToken.generateToken4URL(request)%>";
      postData(enderecoRetorno);
      return;
    }
  }
  
  function downloadArquivo(arquivo,tipo) {
    postData('../v3/downloadArquivo?arquivo_nome=' + arquivo + '&tipo=' + tipo + '&skip_history=true' + '&<%=SynchronizerToken.generateToken4URL(request)%>','download');
  }
  
  function doLoad(reload) {
    if (reload) {
      setTimeout("refresh()", 15*1000);
    }
  }
  
  function refresh() {
    postData("../v3/importarMargem?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>");
  }
  
  function doIt(opt, arq, path,tipo) {
    var msg = '', j;
    if (opt == 'e') {
      msg = '<hl:message key="mensagem.folha.confirmacao.remover.arquivo.nome"/>'.replace('{0}', path);
      if(tipo == 'm'){
        j = '../v3/excluirArquivo?arquivo_nome=' + encodeURIComponent(arq) + '&tipo=margem&link=../v3/importarMargem?acao=iniciar';
      } else if(tipo == 'mc'){
        j = '../v3/excluirArquivo?arquivo_nome=' + encodeURIComponent(arq) + '&tipo=margemcomplementar&link=../v3/importarMargem?acao=iniciar';
      } else{
        j = '../v3/excluirArquivo?arquivo_nome=' + encodeURIComponent(arq) + '&tipo=transferidos&link=../v3/importarMargem?acao=iniciar';
      }
    } else {
      return false;
    }
    j = j + '&<%=SynchronizerToken.generateToken4URL(request)%>';
    if (msg != '') {
      ConfirmaUrl(msg, j);
    } else {
      postData(j);
    }
    return true;
  }
  
  function alterarTipoEntidade() {
    var tipoEntidade = $("input:radio[name=tipoEntidade]:checked" ).val();
    if (tipoEntidade == 'CSE') {
      $("#estCodigo").attr("disabled", true); 
        $("#orgCodigo").attr("disabled", true);
        $("#estCodigo").val("");
        $("#orgCodigo").val("");
    } else if (tipoEntidade == 'EST') {
      $("#estCodigo").attr("disabled", false); 
        $("#orgCodigo").attr("disabled", true );
        $("#orgCodigo").val("");
    } else if (tipoEntidade == 'ORG') {
      $("#estCodigo").attr("disabled", true); 
        $("#orgCodigo").attr("disabled", false);
        $("#estCodigo").val("");
    }
    return;
  }
  
  function importaMargemComplementar(arquivo, validarCamposEntidade) {
    if (!validarCamposEntidade) {
      enderecoRetorno = enderecoRetornoBase + arquivo + "&TIPO=MARGEMCOMPLEMENTAR&TOTAL=N&TRANSFERIDOS=N" + "&<%=SynchronizerToken.generateToken4URL(request)%>";
    } else {
      var tipoEntidade = $("input:radio[name=tipoEntidade]:checked" ).val();
      var est = $("#estCodigo").val();
      var org = $("#orgCodigo").val();
      var codigoEntidade;
      
      if (tipoEntidade != null && tipoEntidade != '' && tipoEntidade != 'CSE') {
        if (tipoEntidade == 'EST') {
          if (est == null || est == "") {
            alert('<hl:message key="mensagem.informe.estabelecimento"/>');
            return false;
          } else {
            codigoEntidade = est;
          }
        } else if (tipoEntidade == 'ORG') {
          if (org == null || org == "") {
            alert('<hl:message key="mensagem.informe.orgao"/>');
            return false;
          } else {
            codigoEntidade = org;
          }
        }
        // importação por entidade
        enderecoRetorno = enderecoRetornoBase + arquivo + "&TIPO=MARGEMCOMPLEMENTAR&TOTAL=N&TRANSFERIDOS=N&ENTIDADEALTERADA=S&TIPOENTIDADE=" + tipoEntidade + "&CODIGOENTIDADE=" + codigoEntidade + "&<%=SynchronizerToken.generateToken4URL(request)%>";  
      } else {
        // importação geral
        enderecoRetorno = enderecoRetornoBase + arquivo + "&TIPO=MARGEMCOMPLEMENTAR&TOTAL=N&TRANSFERIDOS=N" + "&<%=SynchronizerToken.generateToken4URL(request)%>";  
      }
    }
    postData(enderecoRetorno);
    return;
  }
  
  function importaMargemComplementarSelecionaEntidade(arquivo, isCseSup) {
    // mensagem para confirmação da importação
    var msg = '<hl:message key="mensagem.folha.confirmacao.importacao.margem.complementar"/>';
    $('#dialog span').text(msg);
    arquivoCache = arquivo;
    isCseSupCache = isCseSup;
    // solicita confirmação da importação
    $("#dialog").modal("show"); 
  }
  
  function confirmaImportar() {
    importaMargemComplementar(arquivoCache, isCseSupCache);
  }

  window.onload = doLoad(<%=(boolean)temProcessoRodando%>);

</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>