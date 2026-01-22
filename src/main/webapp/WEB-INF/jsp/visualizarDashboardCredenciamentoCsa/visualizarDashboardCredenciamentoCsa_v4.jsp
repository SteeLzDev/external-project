<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.persistence.entity.StatusCredenciamento"%>
<%@ page import="com.zetra.econsig.values.StatusCredenciamentoEnum"%>
<%@ page import="com.zetra.econsig.persistence.entity.CredenciamentoCsa"%>
<%@ page import="com.zetra.econsig.helper.upload.UploadHelper"%>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper"%>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper"%>
<%@ page import="com.zetra.econsig.dto.TransferObject" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.File" %>
<%@ page import="com.zetra.econsig.dto.web.*"%>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
List<TransferObject> lstCredenciamentoCsa = (List<TransferObject>) request.getAttribute("lstCredenciamentoCsa");
List<StatusCredenciamento> lstStatusCredenciamento = (List<StatusCredenciamento>) request.getAttribute("lstStatusCredenciamento");
List<TransferObject> lstConsignatarias = (List<TransferObject>) request.getAttribute("lstConsignatarias");
String fieldValue = Columns.CSA_CODIGO;
String fieldLabel = Columns.CSA_NOME + ";" + Columns.CSA_IDENTIFICADOR;
List<File> arquivos = (List<File>) request.getAttribute("arquivos");
HashMap<String, Boolean> hashAnexosCredenciamentoCsa = (HashMap<String,Boolean>) request.getAttribute("hashAnexosCredenciamentoCsa");
%>
<c:set var="title">
   <hl:message key="rotulo.dashboard.credenciamento.csa.titulo"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
<%if(!responsavel.isCsa()) { %>
    <div class="page-title">
        <div class="row d-print-none">
          <div class="col-sm-12 col-md-12 mb-2">
            <div class="btn-action">
              <button data-bs-toggle="dropdown" aria-haspopup="true" id="acoes" aria-expanded="false" class="btn btn-primary" type="submit"><hl:message key="rotulo.mais.acoes"/></button>
              <div class="dropdown-menu dropdown-menu-right" aria-labelledby="acoes">
                <a class="dropdown-item" href="#no-back" onclick="show_descricao(1)"><hl:message key="rotulo.dashboard.credenciamento.acao.enviar.lista.doc"/></a>
                <a class="dropdown-item" href="#no-back" onclick="show_descricao(2)"><hl:message key="rotulo.dashboard.credenciamento.acao.enviar.minuta"/></a>
                <%if (responsavel.temPermissao(CodedValues.FUN_CONS_CONSIGNATARIAS)){ %>
                	<a class="dropdown-item" href="#no-back" onclick="javascript:postData('../v3/manterConsignataria?acao=iniciar')"><hl:message key="rotulo.consignataria.plural"/></a>
                	<a class="dropdown-item" href="#no-back" onclick="javascript:postData('../v3/listarRelatorio?tipo=conf_cad_csa')"><hl:message key="rotulo.dashboard.credenciamento.relatorio.consignatarias"/></a>
                <%} %>
              </div>
            </div>
          </div>
        </div>
    </div>
    <div class="opcoes-avancadas">
      <a class="opcoes-avancadas-head" href="#filtro" data-bs-toggle="collapse" aria-expanded="false" aria-controls="filtro"><hl:message key="rotulo.filtro.plural"/></a>
      <div class="collapse" id="filtro">
        <div class="opcoes-avancadas-body pl-4">
          <form action="../v3/visualizarDashboardCredenciamento?acao=filtrar&_skip_history_=true" method="post" name="formFiltros">
            <fieldset>
              <div class="row">
                <div class="form-group col-sm-12 col-md-6">
                  <span id="dataInclusao"><hl:message key="rotulo.dashboard.credenciamento.data.processo"/></span>
                  <div class="row" role="group" aria-labelledby="dataInclusao">
                    <div class="col-sm-6">
                      <div class="row">
                        <div class="form-check col-sm-2 col-md-2">
                          <div class="float-left align-middle mt-4 form-control-label">
                            <label for="periodoIni" class="labelSemNegrito"><hl:message key="rotulo.data.de"/></label>
                          </div>
                        </div>
                        <div class="form-check col-sm-10 col-md-10">
                          <hl:htmlinput name="periodoIni" di="periodoIni" type="text" classe="form-control w-100" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>"/>
                        </div>
                      </div>
                    </div>
                    <div class="col-sm-6">
                      <div class="row">
                        <div class="form-check col-sm-2 col-md-2">
                          <div class="float-left align-middle mt-4 form-control-label">
                            <label for="periodoFim" class="labelSemNegrito"><hl:message key="rotulo.data.ate"/></label>
                          </div>
                        </div>
                        <div class="form-check col-sm-10 col-md-10">
                          <hl:htmlinput name="periodoFim" di="periodoFim" type="text" classe="form-control w-100" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>"/>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </fieldset>
            <fieldset>
              <div class="row">
                <div class="form-group col-sm-12 col-md-12">
                  <span id="situacaoServidor"><hl:message key="rotulo.dashboard.credenciamento.csa.status.credenciamento"/></span>
                  <div class="form-check">
                    <div class="row">
                      <%for (StatusCredenciamento statusCredenciamento : lstStatusCredenciamento){ %>
                          <div class="col-sm-12 col-md-4">
                              <span class="align-text-top">
                                <input type="checkbox" class="form-check-input ml-1" name="scrCodigos" id="<%=statusCredenciamento.getScrCodigo()%>" value="<%=statusCredenciamento.getScrCodigo()%>" title="<%=TextHelper.forHtmlContent(statusCredenciamento.getScrDescricao())%>" onBlur="fout(this);ValidaMascara(this);">
                                <label class="form-check-label labelSemNegrito ml-1" for="<%=statusCredenciamento.getScrCodigo()%>"><%=TextHelper.forHtmlContent(statusCredenciamento.getScrDescricao())%></label>
                              </span>
                          </div>
                        <%} %>
                    </div>
                  </div>
                </div>
             </div>
            </fieldset>
            <fieldset>
              <div class="row">
                <div class="form-group col-sm-12 col-md-12">
                  <span id="situacaoServidor"><hl:message key="rotulo.consignataria.plural"/></span>
                  <div class="form-check">
                    <div class="row">
                      <% if (responsavel.isCseSup()) { %>
                              <div class="form-group col-sm-12 col-md-6">
                                <%=JspHelper.geraCombo(lstConsignatarias, "csaCodigo", fieldValue, fieldLabel, "", "", true, 3, "", null, false, "form-control")%>
                              </div>
                          <%} else if (responsavel.isCsa()) { %>
                              <div class="form-group col-sm-12 col-md-6">
                                <select name="csaCodigo" id="csaCodigo" class="Select form-select form-control" onFocus="SetarEventoMascaraV4(this,'#*200',true);" onBlur="fout(this);ValidaMascaraV4(this);">
                                  <option value=""><%=TextHelper.forHtmlContent(responsavel.getNomeEntidade())%></option>
                               </select>
                              </div>
                       <% } %>
                    </div>
                  </div>
                </div>
             </div>
            </fieldset>
          </form>
          <div class="btn-action d-print-none">
            <a class="btn btn-primary" href="#no-back" onClick="if(validaCampos()){f0.submit();} return false;"><svg width="20"><use xlink:href="../img/sprite.svg#i-consultar"></use></svg><hl:message key="rotulo.botao.pesquisar"/></a>
          </div>
        </div>
      </div>
    </div>
<%} %>
<div class="card">
  <div class="card-header">
    <h2 class="card-header-title">
       <hl:message key="rotulo.dashboard.credenciamento.csa"/>
    </h2>
  </div>
  <div class="tab-content table-responsive">
    <table id="dataTables" class="table table-striped table-hover w-100">
      <thead>
        <tr>
         <th scope="col"><hl:message key="rotulo.consignataria.singular"/></th>
         <th scope="col"><hl:message key="rotulo.dashboard.credenciamento.csa.status.credenciamento"/></th>
         <th scope="col"><hl:message key="rotulo.dashboard.credenciamento.csa.data.inicio"/></th>
         <th scope="col"><hl:message key="rotulo.acoes"/></th>
        </tr>
      </thead>
      <tbody>
      <%=JspHelper.msgRstVazio(lstCredenciamentoCsa == null || lstCredenciamentoCsa.isEmpty(), 4, responsavel)%>
      <% if (lstCredenciamentoCsa != null && !lstCredenciamentoCsa.isEmpty()) { %>
         <% for (TransferObject credenciamentoCsa : lstCredenciamentoCsa) {
             String csaNome = (String) credenciamentoCsa.getAttribute(Columns.CSA_NOME);
             String creCodigo = (String) credenciamentoCsa.getAttribute(Columns.CRE_CODIGO);
             String csaIdentificador = (String) credenciamentoCsa.getAttribute(Columns.CSA_IDENTIFICADOR);
             String creDataIni = DateHelper.format((Date) credenciamentoCsa.getAttribute(Columns.CRE_DATA_INI), LocaleHelper.getDateTimePattern());
             String statusCredenciamento = (String) credenciamentoCsa.getAttribute(Columns.SCR_DESCRICAO);
             String scrCodigo = (String) credenciamentoCsa.getAttribute(Columns.CRE_SCR_CODIGO);
             String csaCodigo = (String) credenciamentoCsa.getAttribute(Columns.CRE_CSA_CODIGO);
         %>
              <tr class="selecionarLinha">
                <td class="selecionarColuna"><%=TextHelper.forHtmlContent(csaIdentificador+"-"+csaNome)%></td>
                <td class="selecionarColuna"><%=TextHelper.forHtmlContent(statusCredenciamento)%></td>
                <td class="selecionarColuna"><%=TextHelper.forHtmlContent(creDataIni)%></td>
                <td class="acoes">
                  <div class="actions">
                    <div class="dropdown">
                      <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <div class="form-inline">
                          <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<%=ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)%>" aria-label="<%=ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)%>"> <svg>
                              <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                          </span> <hl:message key="rotulo.botao.opcoes"/>
                        </div>
                      </a>
                      <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                          <a class="dropdown-item" href="#no-back" onClick="postData('../v3/visualizarDashboardCredenciamento?acao=detalharCredenciamento&creCodigo=<%=TextHelper.forJavaScriptAttribute(creCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                            <hl:message key="rotulo.acoes.detalhar" />
                          </a>
                          <%if(hashAnexosCredenciamentoCsa.get(creCodigo)){ %>
                            <a class="dropdown-item" href="#no-back" onClick="postData('../v3/downloadArquivosCredenciamento?tipo=anexo_credenciamento&creCodigo=<%=TextHelper.forJavaScriptAttribute(creCodigo)%>&csaCodigo=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&_skip_history_=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                              <hl:message key="rotulo.dashboard.credenciamento.csa.opcao.download" />
                            </a>
                          <%} %>
                          <% if (scrCodigo.equals(StatusCredenciamentoEnum.FINALIZADO.getCodigo())) { %>
                          <a class="dropdown-item" href="#no-back" onClick="postData('../v3/downloadArquivosCredenciamento?tipo=anexo_credenciamento&termoAditivo=S&creCodigo=<%=TextHelper.forJavaScriptAttribute(creCodigo)%>&csaCodigo=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&_skip_history_=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                              <hl:message key="rotulo.dashboard.credenciamento.csa.opcao.download.termo" />
                          </a>
                          <% } %>
                      </div>
                    </div>
                  </div>
                </td>
              </tr>
          <% } %>
        <% } %>
      </tbody>
    </table>
  </div>
</div>
<div class="float-end">
  <div class="btn-action">
      <a class="btn btn-outline-danger" href="#no-back" onclick="postData('../v3/carregarPrincipal')"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
</div>
<div class="modal fade" id="confirmarMensagem" tabindex="-1" role="dialog" aria-labelledby="modalTitulo" aria-hidden="true" style="display: none;">
    <form name="form1" id="form1" method="POST" action="../v3/visualizarDashboardCredenciamento?acao=uploadArquivoCredenciamento&<%=SynchronizerToken.generateToken4URL(request)%>" enctype="multipart/form-data">
        <div class="modal-dialog modal-dialog-width" role="document">
          <div class="modal-content">
                <div class="modal-header">
                  <h5 class="modal-title about-title mb-0" id="modalTitulo"></h5>
                  <button type="button" class="logout mr-3" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
                    <span aria-hidden="true"></span>
                  </button>
                </div>
                <div class="form-group modal-body m-0">
                   <hl:fileUploadV4 obrigatorio="<%=false%>" mostraCampoDescricao="<%=false%>" nomeCampoArquivo="FILE1" multiplo="false" tipoArquivo="anexo_credenciamento"/>
                </div>
                 <% if (!TextHelper.isNull(arquivos)) { %>
                   <div class="col-md-12">
                     <div class="card">
                        <div class="card-header hasIcon pl-3">
                          <h2 class="card-header-title"><hl:message key="rotulo.dashboard.titulo.table.modal.credenciamento"/></h2>
                        </div>
                        <div class="card-body table-responsive">
                          <div class="table-responsive">
                          <table class="table table-striped table-hover">
                             <thead>
                                <tr>
                                  <th scope="col"><hl:message key="rotulo.dashboard.nome"/></th>
                                  <th scope="col"><hl:message key="rotulo.acoes"/></th>
                                </tr>
                             </thead>
                             <tbody>
                             <%
                             for(File arquivo : arquivos){
                               String nome_arquivo = arquivo.getName();
                             %>
                                   <tr>
                                     <td><%=TextHelper.forHtmlContent(nome_arquivo)%></td>
                                     <td>
                                       <div class="actions">
                                         <div class="dropdown">
                                            <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                              <div class="form-inline">
                                                 <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.mais.acoes"/>" aria-label="<hl:message key="rotulo.mais.acoes"/>"> <svg>
                                                   <use xlink:href="#i-engrenagem"></use></svg>
                                                 </span> <hl:message key="rotulo.botao.opcoes"/>
                                              </div>
                                            </a>
                                            <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                                                <a class="dropdown-item" href="#no-back" onClick="javascript:fazDownload('<%=TextHelper.forJavaScript(arquivo.getName())%>');"><hl:message key="rotulo.botao.download.arquivo"/></a>
                                                   <a class="dropdown-item" href="#no-back" onClick="javascript:doIt('e', '<%=TextHelper.forJavaScript(arquivo.getName())%>','<%=TextHelper.forJavaScript(arquivo.getName())%>'); return false;"><hl:message key="rotulo.botao.excluir.arquivo"/></a>
                                               </div>
                                        </div>
                                       </div>
                                     </td>
                                   </tr>
                               <% } %>
                             </tbody>
                             <tfoot>
                               <tr><td colspan="5"><%=ApplicationResourcesHelper.getMessage("mensagem.listagem.arquivos.credenciamento", responsavel)%></td></tr>
                             </tfoot>
                          </table>
                          </div>
                        </div>
                     </div>
                  </div>
              <% } %>
              <div class="modal-footer pt-0">
                  <div class="btn-action mt-2 mb-0">
                        <a class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.cancelar"/>' href="#"><hl:message key="rotulo.botao.cancelar"/></a>
                        <input hidden="true"  id="aad_nome" value="">
                        <input hidden="true"  id="aad_descricao" value="">
                        <a class="btn btn-primary" href="#" onclick="if(vf_upload_arquivos()){document.form1.submit();} return false;"><hl:message key="rotulo.botao.confirmar" /></a>
                  </div>
                </div>
          </div>
        </div>
        <input type="hidden" id="tipoArquivo" name="tipoArquivo" value="1">
  </form>
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
                         <div class="modal-body"><span><hl:message key="mensagem.upload.generico.aguarde"/></span></div>
                   </div>
                 </div>
           </div>
         </div>
   </div>
</div>
</c:set>
<c:set var="javascript">
  <link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-buttons-dt/css/buttons.dataTables.min.css"/>
  <link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-responsive-bs5/css/responsive.bootstrap5.min.css"/>
  <link rel="stylesheet" type="text/css" href="../datatables/DataTables-1.12.1/css/dataTables.bootstrap5.min.css"/>
  <link rel="stylesheet" href="../css/impromptu.css?<hl:message key="release.tag"/>" type="text/css">
  <script  src="../node_modules/jszip/dist/jszip.min.js"></script>
  <script  src="../node_modules/pdfmake/build/pdfmake.min.js"></script>
  <script  src="../node_modules/pdfmake/build/vfs_fonts.js"></script>
  <script  src="../node_modules/datatables.net/js/jquery.dataTables.min.js"></script>
  <script  src="../node_modules/datatables.net-bs5/js/dataTables.bootstrap5.min.js"></script>
  <script  src="../node_modules/datatables.net-buttons/js/dataTables.buttons.min.js"></script>
  <script  src="../node_modules/datatables.net-buttons/js/buttons.colVis.min.js"></script>
  <script  src="../node_modules/datatables.net-buttons/js/buttons.html5.min.js"></script>
  <script  src="../node_modules/datatables.net-buttons/js/buttons.print.min.js"></script>
  <script  src="../node_modules/datatables.net-responsive/js/dataTables.responsive.min.js"></script>
  <script  src="../node_modules/moment/min/moment.min.js"></script>
  <script  src="../node_modules/datatables.net-plugins/sorting/datetime-moment.js"></script>
  <hl:fileUploadV4 botaoVisualizarRemover="<%=true%>" multiplo="false" scriptOnly="true" nomeCampoArquivo="FILE1" extensoes="<%=UploadHelper.EXTENSOES_PERMITIDAS_DOCUMENTOS_CREDENCIAMENTO%>" tipoArquivo="anexo_credenciamento"/>
  <script type="text/javascript">
  f0 = document.forms[0];
  $(document).ready(function() {
      $.fn.dataTable.moment( 'DD/MM/YYYY HH:mm:ss' );
      $('#dataTables').DataTable({
          "paging": true,
          "pageLength": 20,
          "lengthMenu": [
              [20, 50, 100, -1],
              [20, 50, 100, '<hl:message key="mensagem.datatables.all"/>']
          ],
          "pagingType": "simple_numbers",
          "dom": '<"card-body p-0" <"row pl-0 pr-4" <"col-sm-2 pl-0" B > <"col-sm-6 pl-0" l > <"col-sm-4 pr-0" f >> <"table-responsive" t> > <"card-footer" <"row" <"col-sm-6" i> <"col-sm-6" p >>>',
          buttons: [
              'colvis'
          ],
          stateSave: true,
          stateSaveParams: function (settings, data) {
              data.search.search = "";
          },
          language: {
              search:            '_INPUT_',
              searchPlaceholder: '<hl:message key="mensagem.datatables.search.placeholder"/>',
              processing:        '<hl:message key="mensagem.datatables.processing"/>',
              loadingRecords:    '<hl:message key="mensagem.datatables.loading"/>',
              info:              '<hl:message key="mensagem.datatables.info"/>',
              lengthMenu:        '<hl:message key="mensagem.datatables.length.menu"/>',
              infoEmpty:         '<hl:message key="mensagem.datatables.info.empty"/>',
              infoFiltered:      '<hl:message key="mensagem.datatables.info.filtered"/>',
              infoPostFix:       '',
              zeroRecords:       '<hl:message key="mensagem.datatables.zero.records"/>',
              emptyTable:        '<hl:message key="mensagem.datatables.empty.table"/>',
              aria: {
                  sortAscending: '<hl:message key="mensagem.datatables.aria.sort.ascending"/>',
                  sortDescending:'<hl:message key="mensagem.datatables.aria.sort.descending"/>'
              },
              paginate: {
                  first:         '<hl:message key="mensagem.datatables.paginate.first.padrao.econsig"/>',
                  previous:      '<hl:message key="mensagem.datatables.paginate.previous.padrao.econsig"/>',
                  next:          '<hl:message key="mensagem.datatables.paginate.next.padrao.econsig"/>',
                  last:          '<hl:message key="mensagem.datatables.paginate.last.padrao.econsig"/>'
              },
              buttons: {
                  print :        '<hl:message key="mensagem.datatables.buttons.print"/>',
                  colvis :       '<hl:message key="mensagem.datatables.buttons.colvis"/>'
              },
              decimal: ",",
          },
          initComplete: function () {
              var btns = $('.dt-button');
              btns.addClass('btn btn-primary btn-sm');
              btns.removeClass('dt-button');
          }
      });

      $("#dataTables_filter").addClass('pt-2 px-3');
      $('#dataTables_info').addClass('p-3');
      $("#dataTables_length").addClass('pt-3');
  });

  function validaCampos() {
      var periodoIni = document.getElementById("periodoIni");
      var periodoFim = document.getElementById("periodoFim");

      if (((periodoIni != null && periodoIni.value != '') && (periodoFim == null || periodoFim.value == '')) ||
          ((periodoIni == null || periodoIni.value == '') && (periodoFim != null && periodoFim.value != ''))) {
          alert('<hl:message key="mensagem.informe.data.inicio.data.fim.credenciamento"/>');
          periodo.focus();
          return false;
      }
      if (periodoIni != null && periodoIni.value != '' && (!verificaData(periodoIni.value))) {
          periodoIni.focus();
          return false;
      }
      if (periodoFim != null && periodoFim.value != '' && (!verificaData(periodoFim.value))) {
          periodoFim.focus();
          return false;
      }
      return true;
  }

  function show_descricao(tipoDescricao) {
      if (tipoDescricao == 1) {
          $('#modalTitulo').empty();
          $('#modalTitulo').append('<hl:message key="rotulo.dashboard.credenciamento.acao.enviar.lista.doc"/>');
          $('#tipoArquivo').empty();
          $('#tipoArquivo').val('1');
      } else {
          $('#modalTitulo').empty();
          $('#modalTitulo').append('<hl:message key="rotulo.dashboard.credenciamento.acao.enviar.minuta"/>');
          $('#tipoArquivo').empty();
          $('#tipoArquivo').attr('value','2');
      }
      $('#confirmarMensagem').modal('show');
  }

  var form1 = document.querySelector('#form1');
  function vf_upload_arquivos() {
      var arquivo = tratarArquivo();
      if ((arquivo == null) || (trim(arquivo) == "") || (arquivo.toUpperCase() == "NULL")) {
          alert('<hl:message key="mensagem.editar.anexo.consignacao.selecione.arquivo"/>');
          return false;
      } else {
          $('#modalAguarde').modal({
              backdrop: 'static',
              keyboard: false
          });
          document.getElementById("FILE1").value = arquivo;
          form1.submit();
          return true;
      }
  }

  function tratarArquivo(){
      var arquivo = document.getElementById("FILE1").value;
      var nomes = arquivo.split(';');
      var nomesArquivosFinal = "";
      for (var i = 0; i < nomes.length; i++) {
          // Faz o tratamento caso o usuario tenha removido algum arquivo temporario para upload
          if (nomes[i] != "removido") {
              if (nomesArquivosFinal == "") {
                  nomesArquivosFinal = nomes[i];
              } else {
                  nomesArquivosFinal += ';' + nomes[i];
              }
          }
      }
      return nomesArquivosFinal;
  }

  function fazDownload(nome){
      postData('../v3/downloadArquivo?arquivo_nome=' + encodeURIComponent(nome) + '&tipo=anexo_credenciamento&<%=SynchronizerToken.generateToken4URL(request)%>','download');
  }

  function doIt(opt, arq, path) {
      var msg = '', j;
      if (opt == 'e') {
          msg = '<hl:message key="mensagem.confirmacao.lst.arq.generico.exclusao"/>'.replace('{0}', arq);
          j = '../v3/excluirArquivo?<%=SynchronizerToken.generateToken4URL(request)%>&arquivo_nome=' + encodeURIComponent(path) + '&tipo=anexo_credenciamento';
      } else {
          return false;
      }
      if (msg != '') {
          ConfirmaUrl(msg, j);
      } else {
          postData(j);
      }
      return true;
  }

  function excluirArquivo(nomeArquivo) {
      if (confirm('<%=ApplicationResourcesHelper.getMessage("mensagem.confirmacao.excluir.arquivo", responsavel)%>\n\n' + nomeArquivo)) {
          f0.action = '../v3/excluirArquivo?&tipo=' + tipo + '&arquivo_nome=' + nomeArquivo + '&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>';
          f0.submit();
      }
  }

  function removeAnexoVisualizacao(posicao) {
      let nomeArquivo = $("#FILE1").val();
      var nomes = nomeArquivo.split(';');
      nomeArquivo = nomes[posicao];
      var novaFILE1 = "";
      for (var i = 0; i < nomes.length; i++) {
          if (i != posicao) {
              if (novaFILE1 == "") {
                  novaFILE1 = nomes[i];
              } else {
                  novaFILE1 += ';' + nomes[i];
              }
          } else if(novaFILE1 == ""){
              novaFILE1 += "removido";
          } else {
              novaFILE1 += ';' + "removido";
          }
      }
      document.getElementById('FILE1').value = novaFILE1;
      $.ajax({
          type: 'POST',
          url: '../v3/excluirArquivo?arquivo_nome=' + nomeArquivo + '&tipo=anexo_credenciamento_temp&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>',
          success: function (data) {
              // Busca a div do arquivo que foi removido para nao exibir na lista abaixo do botao para anexar documento.
              var divArquivoRemovido = document.getElementById(nomeArquivo);
              if (divArquivoRemovido != null) {
                  divArquivoRemovido.parentNode.removeChild(divArquivoRemovido);
              }
              // Se tiver uma mensagem de sessao de sucesso, apaga ela para nao confundir o usuario
              var divAlertSucess = document.getElementsByClassName('alert alert-success');
              if (divAlertSucess.length > 0) {
                  divAlertSucess[0].parentNode.removeChild(divAlertSucess[0]);
              }
              // Caso nao tenha mais arquivos para upload ele oculta a barra azul, para melhorar experiencia do usuario.
              if (document.getElementById("pic-progress-wrap-FILE1") != null && arquivosRemovidos(novaFILE1)) {
                  document.getElementById("pic-progress-wrap-FILE1").style.display = 'none';
              }
          },
          error: function (error) {
              console.log(error);
          }
      })
  }

  function downloadAnexoVisualizacao(posicao) {
      let nomeArquivo = $("#FILE1").val();
      var nomes = nomeArquivo.split(';');
      nomeArquivo = nomes[posicao];
      postData("../v3/downloadArquivo?arquivo_nome=" + nomeArquivo + "&tipo=anexo_credenciamento_temp&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>");
  }

  function arquivosRemovidos(fileValores) {
      var todosRemovidos = true;
      var nomes = fileValores.split(';');
      for (var i=0; i<nomes.length; i++) {
          if (nomes[i] != "removido") {
              todosRemovidos = false;
          }
      }
      return todosRemovidos;
    }
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
