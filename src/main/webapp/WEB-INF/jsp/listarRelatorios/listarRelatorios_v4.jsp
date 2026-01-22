<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="fl" uri="/function-lib"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%
  boolean exibeBotaoRodape = request.getAttribute("exibeBotaoRodape") != null && (boolean) (request.getAttribute("exibeBotaoRodape"));
  boolean relatorioCustomizaoOrg = request.getAttribute("relatorioCustomizaoOrg") != null && (boolean) (request.getAttribute("relatorioCustomizaoOrg"));
  int refreshTimeout = request.getAttribute("refreshTimeout") != null ? (int) request.getAttribute("refreshTimeout") : 10*1000;
%>
<c:set var="imageHeader">
    <use xlink:href="#i-relatorio"></use>
</c:set>
<c:set var="title">
   ${relatorio.titulo}
</c:set>
<c:set var="bodyContent">
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
  </script>
  <c:if test="${!temProcessoRodando}">
    <form name="form1" method="POST" action="${fl:forHtmlAttribute(formAction)}">
      <c:if test="${not empty recursos}">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="mensagem.informe.relatorio.parametros"/></h2>
        </div>
        <div class="card-body">
          <hl:htmlinput type="hidden" name="tipo" di="tipo" value="${fl:forHtmlAttribute(strTipo)}" />
          <hl:htmlinput type="hidden" name="LOG_OBSERVACAO" di="LOG_OBSERVACAO" value="" />
          <div class="row">
          <c:forEach items="${recursos}" var="recurso">
            <c:set var="recurso" value="${recurso}" scope="session"/>
            <jsp:include page="${fl:forHtmlAttribute(recurso)}" flush="true">
              <jsp:param name="OBRIGATORIO" value="${obrigatorios.contains(recurso)}" />
              <jsp:param name="PARAMETRO" value="${fl:forHtmlAttribute((parametros[recurso]))}" />
              <jsp:param name="STRTIPO" value="${fl:forHtmlAttribute(strTipo)}" />
            </jsp:include>
          </c:forEach>
          </div>
        </div>
      </div>
      <div id="actions" class="btn-action">
        <c:choose>
        <c:when test="${relatorio.isAgendado()}">
          <A class="btn btn-primary" NAME="btnAgenda" ID="btnAgenda" HREF="#no-back" onClick="if(vf_periodo_rel()){f0.submit();} return false;"><svg width="17"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="../img/sprite.svg#i-confirmar"></use></svg><hl:message key="rotulo.botao.confirmar"/></a>
        </c:when>
        <c:otherwise>
          <A class="btn btn-primary" NAME="btnEnvia" ID="btnEnvia" HREF="#no-back" onClick="if(vf_periodo_rel()){executaRelatorio();} return false;"><svg width="17"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="../img/sprite.svg#i-confirmar"></use></svg><hl:message key="rotulo.botao.confirmar"/></a>
        </c:otherwise>
        </c:choose>
      </div>
      </c:if>
      <div class="card">
        <div class="card-header hasIcon">
          <span class="card-header-icon"><svg width="25"><use xlink:href="../img/sprite.svg#i-relatorio"></use></svg></span>
          <h2 class="card-header-title"><hl:message key="rotulo.listar.arq.relatorio.download"/></h2>
        </div>
        <div class="card-body table-responsive">
          <table id="dataTables" class="table table-striped table-hover">
            <thead>
              <tr>
                <th><hl:message key="rotulo.relatorio.nome"/></th>
                <th><hl:message key="rotulo.relatorio.tamanho.abreviado"/></th>
                <th><hl:message key="rotulo.relatorio.data"/></th>
                <c:if test="${responsavel.isCseSup()}">
                <c:choose>
                <c:when test="${exibeColunaCsa}">
                <th><hl:message key="rotulo.consignataria.singular"/></th>
                </c:when>
                <c:otherwise>
                <th><hl:message key="rotulo.orgao.singular"/></th>
                </c:otherwise>
                </c:choose>
                </c:if>
                <th><hl:message key="rotulo.acoes"/></th>
              </tr>
            </thead>
            <tbody>
            <c:choose>
              <c:when test="${empty arquivosDTO}">
              <tr>
                <td colspan="6"><hl:message key="rotulo.nenhum.relatorio.encontrado"/></td>
              </tr>
              </c:when>
              <c:otherwise>
                <c:forEach items="${arquivosDTO}" var="arq" varStatus="arqStatus">                    
                <tr>
                  <td>${arq.originalNome}</td>
                  <td>${arq.tam}</td>
                  <td>${arq.data}</td>
                  <c:if test="${responsavel.isCseSup()}">
                  <c:choose>
                  <c:when test="${exibeColunaCsa}">
                  <td>${arq.csaIdentificador}${not empty arq.csaIdentificador ? '-' : '&nbsp;'}${arq.csaNome}</td>
                  </c:when>
                  <c:otherwise>
                  <td>${arq.orgIdentificador}${not empty arq.orgIdentificador ? '-' : '&nbsp;'}${arq.estIdentificador}</td>
                  </c:otherwise>
                  </c:choose>
                  </c:if>
                  <td>
                    <div class="actions">
                      <div class="dropdown">
                        <c:choose>
                        <c:when test="${podeExclRelatorio}">
                        <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                          <div class="form-inline">
                            <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.botao.opcoes"/>" aria-label="<hl:message key="rotulo.botao.opcoes"/>">
                              <svg><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="../img/sprite.svg#i-engrenagem"></use></svg>
                            </span>
                            <hl:message key="rotulo.botao.opcoes"/>
                          </div>
                        </a>
                        <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                          <a class="dropdown-item" href="#no-back" aria-label="<hl:message key="rotulo.botao.aria.download.arquivo" arg0="${arq.nome}"/>" onClick="fazDownload('${arq.nome}', '${strTipo}', '${not empty arq.csaCodigo ? arq.csaCodigo : csaCodigo}', '${offset}');"><hl:message key="rotulo.botao.download"/></a>
                          <a class="dropdown-item" href="#no-back" aria-label="<hl:message key="rotulo.botao.aria.excluir.arquivo" arg0="${arq.nome}"/>" onClick="doIt('e', '${arq.originalNome}','${arq.nome}'); return false;"><hl:message key="rotulo.botao.excluir"/></a>
                        </div>
                        </c:when>
                        <c:otherwise>
                          <a class="ico-action" href="#no-back" onClick="fazDownload('${arq.nome}', '${strTipo}', '${not empty arq.csaCodigo ? arq.csaCodigo : csaCodigo}', '${offset}');">
                            <div class="form-inline">
                              <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.botao.aria.download.arquivo" arg0="${arq.nome}"/>" aria-label="<hl:message key="rotulo.botao.aria.download.arquivo" arg0="${arq.nome}"/>">
                                <svg><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="../img/sprite.svg#i-download"></use></svg>
                              </span>
                              <hl:message key="rotulo.botao.download"/>
                            </div>
                          </a>      
                        </c:otherwise>
                        </c:choose>
                      </div>
                    </div>
                  </td>
                </tr>
                </c:forEach>
              </c:otherwise>
            </c:choose>
            </tbody>
          </table>
        </div>
      </div>
      <hl:listaAgendamentosv4 tipoRelatorio="${fl:forHtmlAttribute(strTipo)}" linkPaginacao="${linkPaginacaoAgendamento}" offset="${offset2}"/>
      <div class="btn-action">
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal')"><hl:message key="rotulo.botao.voltar"/></a>
      </div>
    </form>
    <% if (exibeBotaoRodape) { %>
        <div id="btns">
          <a id="page-up" onclick="up()">
            <svg xmlns="http://www.w3.org/2000/svg" width="147.344" height="147.344" viewBox="0 0 147.344 147.344">
              <path id="União_3" data-name="União 3" d="M-20,60.672a73.672,73.672,0,1,1,73.672,73.672A73.66,73.66,0,0,1-20,60.672ZM9.61,16.61a62.252,62.252,0,0,0,0,88.124,62.252,62.252,0,0,0,88.124,0,62.252,62.252,0,0,0,0-88.124,62.252,62.252,0,0,0-88.124,0ZM49.174,88.087,15.278,54.3a6.85,6.85,0,0,1,9.67-9.706L53.672,73.6,82.362,45.336A6.825,6.825,0,1,1,92,55.005L58.6,88.3a6.827,6.827,0,0,1-9.421-.212Z" transform="translate(20 13)"/>
            </svg>              
          </a>
          <a id="page-down" onclick="down()">
            <svg xmlns="http://www.w3.org/2000/svg" width="147.344" height="147.344" viewBox="0 0 147.344 147.344">
              <path id="União_3" data-name="União 3" d="M-20,60.672a73.672,73.672,0,1,1,73.672,73.672A73.66,73.66,0,0,1-20,60.672ZM9.61,16.61a62.252,62.252,0,0,0,0,88.124,62.252,62.252,0,0,0,88.124,0,62.252,62.252,0,0,0,0-88.124,62.252,62.252,0,0,0-88.124,0ZM49.174,88.087,15.278,54.3a6.85,6.85,0,0,1,9.67-9.706L53.672,73.6,82.362,45.336A6.825,6.825,0,1,1,92,55.005L58.6,88.3a6.827,6.827,0,0,1-9.421-.212Z" transform="translate(20 13)"/>
            </svg>
          </a>
          <a id="page-actions" onclick="toActionBtns()">
            <svg xmlns="http://www.w3.org/2000/svg" width="145.344" height="145.344" viewBox="0 0 145.344 145.344">
              <path id="União_1" data-name="União 1" d="M-20,59.672a72.672,72.672,0,1,1,72.671,72.672A72.671,72.671,0,0,1-20,59.672Zm10.164,0A62.508,62.508,0,1,0,52.672-2.836,62.579,62.579,0,0,0-9.836,59.672Zm82.6,40.182H24.545A12.069,12.069,0,0,1,12.49,87.8V31.544A12.069,12.069,0,0,1,24.545,19.49h44.2a4.014,4.014,0,0,1,2.841,1.177L91.678,40.757A4.019,4.019,0,0,1,92.855,43.6V87.8A12.069,12.069,0,0,1,80.8,99.854Zm0-40.182a4.018,4.018,0,0,1,4.019,4.018V91.817H80.8A4.023,4.023,0,0,0,84.818,87.8V45.263L67.081,27.526H36.6V39.58H64.727a4.019,4.019,0,0,1,0,8.037H32.581A4.018,4.018,0,0,1,28.563,43.6V27.526H24.545a4.023,4.023,0,0,0-4.018,4.019V87.8a4.023,4.023,0,0,0,4.018,4.018h4.019V63.689a4.018,4.018,0,0,1,4.018-4.018ZM36.6,91.817H68.745V67.708H36.6Z" transform="translate(20 13)"/>
            </svg>
          </a>
        </div>
    <% }%>
  </c:if>
</c:set>
<c:set var="javascript">
<link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-buttons-dt/css/buttons.dataTables.min.css"/>
<link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-responsive-bs5/css/responsive.bootstrap5.min.css"/>
<link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-bs5/css/dataTables.bootstrap5.min.css"/>
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
<script type="text/JavaScript">

$(document).ready(function() {
    $.fn.dataTable.moment( 'DD/MM/YYYY HH:mm:ss' );

    $('#dataTables').DataTable({
        "paging": true,
        "pageLength": 20,
        "lengthMenu": [
          [20, 50, 100, -1],
          [20, 50, 100, '<hl:message key="mensagem.datatables.all"/>']
        ],
        "order": [[ 3, "desc" ]],
        "pagingType": "simple_numbers",
        "dom": '<"row" <"col-sm-2" B > <"col-sm-6" l > <"col-sm-4" f >> <"table-responsive" t > <"card-footer" <"row" <"col-sm-6" i> <"col-sm-6" p >>>',
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
              lengthMenu:        '<hl:message key="mensagem.datatables.length.menu"/>',
              info:              '<hl:message key="mensagem.rodape.tabela.relatorio.download" arg0="${relatorio.titulo}" arg1="<%=DateHelper.format(DateHelper.getSystemDate(), LocaleHelper.getMediumDatePattern())%>"/>',
              infoEmpty:         '<hl:message key="mensagem.datatables.info.empty"/>',
              infoFiltered:      '<hl:message key="mensagem.datatables.info.filtered"/>',
              infoPostFix:       '',
              zeroRecords:       '<hl:message key="mensagem.datatables.zero.records"/>',
              emptyTable:        '<hl:message key="mensagem.datatables.empty.table"/>',
              paginate: {
                  first:         '<hl:message key="mensagem.datatables.paginate.first.padrao.econsig"/>',
                  previous:      '<hl:message key="mensagem.datatables.paginate.previous.padrao.econsig"/>',
                  next:          '<hl:message key="mensagem.datatables.paginate.next.padrao.econsig"/>',
                  last:          '<hl:message key="mensagem.datatables.paginate.last.padrao.econsig"/>'
              },
              aria: {
                  sortAscending: '<hl:message key="mensagem.datatables.aria.sort.ascending"/>',
                  sortDescending:'<hl:message key="mensagem.datatables.aria.sort.descending"/>'
              },
              buttons: {
                  print :        '<hl:message key="mensagem.datatables.buttons.print"/>',
                  colvis :       '<hl:message key="mensagem.datatables.buttons.colvis"/>'
              },
              decimal: ","
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

var ControlesAvancados = null; 
var MsgPeloMenosUm = null;

if (document.getElementById('djuEstado') != null) {
  $(document).ready(function() {
    document.getElementById('djuEstado').setAttribute("onchange", "listarCidades(this.value)");
    document.getElementById('djuComarca').setAttribute("onchange", "setCidCodigo(this.value)");
    
    if (document.getElementById('djuEstado').value != '') {
        listarCidades(document.getElementById('djuEstado').value);
    }
    
  });
  
  function listarCidades(codEstado) {
      if (!codEstado) {
          document.getElementById('djuComarca').innerText = "";
          $("[name='cidCodigo']").val("");            
          return;
      } else {  
        $.ajax({  
          type : 'post',
          url : "../v3/listarCidades?acao=${fl:forJavaScriptBlock(strTipo)}&codEstado=" + codEstado + "&_skip_history_=true",
          async : true,
          contentType : 'application/json',            
          success : function(data) {
  
              var options = "<option value>" + '<hl:message key="rotulo.campo.selecione"/>' + "</option> ";
              var cidades = null;
              var nomeCidade = null;
              var codigoCidade = null;                 
  
              data.forEach(function(objeto) {
                codigoCidade = objeto.atributos['<%=Columns.CID_CODIGO_IBGE%>'];
                nomeCidade = objeto.atributos['<%=Columns.CID_NOME%>'];
                options = options.concat('<option value="').concat(objeto.atributos['<%=Columns.CID_CODIGO%>']).concat('">').concat(nomeCidade).concat('</option>');                    
              });
              
              document.getElementById('djuComarca').innerHTML = options;                
          },
          error: function (response) {
            console.log(response.statusText);
          }
        });
      }
  }
  
  function setCidCodigo(cidCodigo) {
      $("[name='cidCodigo']").val(cidCodigo);
  }
}

function formLoad() {
  enableAll();
  habilitaDesabilitaAgendamento();
  desabilitaCamposRelEditavel();
  focusFirstField();
}

function vf_periodo_rel() {
  if (camposObrigatorios != '' && camposObrigatorios[camposObrigatorios.length - 1] == ',') {
    camposObrigatorios = camposObrigatorios.substring(0, camposObrigatorios.length - 1);
  }
  
  if (msgCamposObrigatorios != '' && msgCamposObrigatorios[msgCamposObrigatorios.length - 1] == ',') {
    msgCamposObrigatorios = msgCamposObrigatorios.substring(0, msgCamposObrigatorios.length - 1);
  }
  
  if (msgCamposObrigatorios != '' && msgCamposObrigatorios.indexOf("*") >= 0) {
    msgCamposObrigatorios = msgCamposObrigatorios.replace(/\*/g,"");
  } 

  var _controles = camposObrigatorios.split(',');   
  var _msgs = msgCamposObrigatorios.split(',');
  
 
  limparErros();
  
  if (ControlesAvancados != null) {
      if (!ValidaCamposPeloMenosUmPreenchido(ControlesAvancados, MsgPeloMenosUm)){
          return false;
      }
  }
  
  if (_controles != null && _controles.length > 0 && _controles[0].trim() != '') {
    if (!validarCampos("mensagens", _controles, _msgs)) {
      return false;
    }
  }

  <c:forEach items="${campos}" var="campo">
    <c:set var="funcao" value="valida_${campo}"/>
    if (!${fl:forJavaScriptBlock(funcao)}.call()) {
      return false;
    }
  </c:forEach>
  // Seta observação que será usada para gerar o log do relatório gerado
  setaObservacaoLog();

  return true;
}

function doIt(opt, arq, path) {
  var msg = '', j;
  if (opt == 'e') {
    msg = '<hl:message key="mensagem.confirmacao.exclusao.arquivo"/>'.replace("{0}", arq);
    j = '../v3/excluirArquivo?<%=SynchronizerToken.generateToken4URL(request)%>&arquivo_nome=' + encodeURIComponent(path) + '&tipo=relatorio&subtipo=${fl:forJavaScriptBlock(strTipo)}';
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

function cancelaAgendamento(agdCodigo) {
  var msg = '', j;
  msg = '<hl:message key="mensagem.confirmacao.cancelamento.agendamento"/>';
  j = '../v3/cancelarAgendamentoRelatorio?tipo=${fl:forJavaScriptBlock(strTipo)}&agdCodigo=' + agdCodigo + '&<%=SynchronizerToken.generateToken4URL(request)%>';
  ConfirmaUrl(msg, j);

  return true;
}

function setaOrdenacao() {
  if (f0.ORDENACAO != null && f0.ORDENACAO.length > 0 && f0.ORDENACAO_AUX != null) {
    atribui_ordenacao.call();
  }  
}

function atualiza(check, box, name, value) {
  if (check) {
    add(box,name,value);    
  } else {
    remove(box,name,value);    
  }
  setaOrdenacao();
}

function setTimeDefault(campo, tempo) {
  if (campo.value == '') {
    campo.value = tempo;
  }
}

function doLoad(reload) {
  if (reload) {
    setTimeout("refresh()", <%=refreshTimeout%>);
  }
}

function refresh(param) {
  postData("../v3/listarRelatorio?tipo=${fl:forJavaScriptBlock(strTipo)}&<%=SynchronizerToken.generateToken4URL(request)%>" + (param ? "&" + param : ""));
}

function setaObservacaoLog() {
  var obs = '';
  var labels = document.getElementsByTagName("label");

  for (var i = 0; i < labels.length; i++) {
    if (labels[i].htmlFor != '' && labels[i].htmlFor != null && labels[i].htmlFor != undefined) {
      if (labels[i].innerText != undefined) {
        obs += labels[i].innerText + ' ';
      } else if (labels[i].textContent != undefined) {
          obs += labels[i].textContent + ' ';
      }

      var destinos = labels[i].htmlFor.split(/;/);
      for (var x = 0; x < destinos.length; x++) {
        var texto = recuperaTexto(destinos[x]);
        if (texto != '') {
          if (obs.charAt(obs.length - 1) != ':' && obs.charAt(obs.length - 2) != ':') {
            obs += ', ';
          }
          obs += texto;
        }
      }
      obs += ' - ';
    }
  }
  f0.LOG_OBSERVACAO.value = obs;
}

function recuperaTexto(id) {
  var ctrl = document.getElementById(id);
  if (ctrl == null || ctrl.disabled) {
    return '';
  } else if (ctrl.type=='select-one') {
    if (ctrl.selectedIndex != -1) {
      return ctrl.options[ctrl.selectedIndex].text;
    } else {
      return '';
    }
  } else if (ctrl.type=='select-multiple') {
    var text = '';
    for (var i = 0; i < ctrl.length; i++) {
      if (ctrl[i].selected) {
        text += ctrl.options[i].text;
        if (i < ctrl.length - 1) {
          text += ', ';
        }
      }
    }
    return text;
  } else if (ctrl.type=='text') {
    return ctrl.value;
  } else if (ctrl.type=='hidden') {
    return ctrl.value;
  } else if (ctrl.type=='radio' && ctrl.checked) {
    return ctrl.title;
  } else if (ctrl.type=='checkbox' && ctrl.checked) {
    return ctrl.title;
  } else {
    if (ctrl.innerText != null && ctrl.innerText != undefined) {
      return ctrl.innerText;
    } else if (ctrl.textContent != null && ctrl.textContent != undefined) {
      return ctrl.textContent;
    } else {
      return '';
    }
  }
}

function fazDownload(nome, strTipo, csaCodigo, offset) {
  postData('../v3/downloadArquivo?subtipo=' + strTipo + '&arquivo_nome=' + encodeURIComponent(nome) + '&tipo=relatorio&codigoEntidade=' + csaCodigo + '&offset=' + offset + '&skip_history=true' + '&<%=SynchronizerToken.generateToken4URL(request)%>','download');
}

function habilitaDesabilitaAgendamento() {
  if (document.getElementById('agendadoSim') != null) {
    var disabled = !document.getElementById('agendadoSim').checked;
    f0.dataPrevista.disabled = disabled;
    f0.periodicidade.disabled = disabled;
    f0.tagCodigo.disabled = disabled;
    if (f0.email_destinatario != null) {
      f0.email_destinatario.disabled = disabled;
    }
  }
}

function executaRelatorio() {
  // DESENV-16082 - Criado um filtro de seleção múltipla de ADE Numero.
  if (document.getElementById('ADE_NUMERO_LIST') != null){
      selecionarTodosItens('ADE_NUMERO_LIST');
  }
  
  if (document.getElementById('agendadoSim') != null) {
    // altera a ação do formulário para chamar o agendamento de relatórios
    if (document.getElementById('agendadoSim').checked) {
      if (f0.formato != null && f0.formato.value == 'HTML') {
        alert('<hl:message key="mensagem.erro.relatorio.agendado.pre.visualizacao"/>');
        return;
      }
      f0.action = '${formActionAgendamento}';
    }
  }
  f0.submit();
}

function carregaDadosFiltro(comboName) {
   if (($('#ncaCodigo').val() && $('#ncaCodigo').val().length != 0 && comboName == "ncaCodigo") || ($('#nseCodigo').val() && $('#nseCodigo').val().length != 0 && comboName == "nseCodigo") || f0.corCodigo != null || f0.sboCodigo != null || f0.uniCodigo != null || f0.CNV_COD_VERBA != null || f0.csaCodigo != null) {
     var link = window.location.href;
     if (link.indexOf('#no-back') > -1) {
         link = link.substring(0, link.indexOf('#no-back'));
     }
     if (link.indexOf('&csaCodigo=') > -1) {
         link = link.substring(0, link.indexOf('&csaCodigo='));
     }
     if (link.indexOf('&orgCodigo=') > -1) {
         link = link.substring(0, link.indexOf('&orgCodigo='));
     }
     if (link.indexOf('&sboCodigo=') > -1) {
         link = link.substring(0, link.indexOf('&sboCodigo='));
     }
     if (link.indexOf('&uniCodigo=') > -1) {
         link = link.substring(0, link.indexOf('&uniCodigo='));
     }
     if (link.indexOf('&ncaCodigo=') > -1) {
         link = link.substring(0, link.indexOf('&ncaCodigo='));
     }
     if (link.indexOf('&nseCodigo=') > -1) {
         link = link.substring(0, link.indexOf('&nseCodigo='));
     }
     if (link.indexOf('&MAR_CODIGO=') > -1) {
         link = link.substring(0, link.indexOf('&MAR_CODIGO='));
     }
     link += '?tipo=${fl:forJavaScriptBlock(strTipo)}&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>';

     var parametro = '';
     if (f0.corCodigo != null || f0.CNV_COD_VERBA != null) {
         parametro = f0.csaCodigo.value.split(';', 1);
         if (parametro != '' && parametro != 'NENHUM' && parametro != 'TODOS_DA_CSA') {
             link+='&csaCodigo=' + f0.csaCodigo.value;
         }
         if (f0.orgCodigo != null) {
             parametro = f0.orgCodigo.value.split(';', 1);
             if (parametro != '' && parametro != 'TODOS') {
                 link+='&orgCodigo=' + f0.orgCodigo.value;
             }
         }
     }
     if (f0.sboCodigo != null) {
       parametro = f0.orgCodigo.value.split(';', 1);
       if (parametro != '' && parametro != 'TODOS') {
           link+='&orgCodigo=' + f0.orgCodigo.value;
       }
     }
     if (f0.uniCodigo != null) {
       parametro = f0.sboCodigo.value.split(';', 1);
       if (parametro != '' && parametro != 'TODOS') {
           link+='&sboCodigo=' + f0.sboCodigo.value;
           link+='&uniCodigo=' + f0.uniCodigo.value;
       }
     }
     if ($('#ncaCodigo').val() && $('#ncaCodigo').val().length != 0) {
       parametro = $('#ncaCodigo').val()[0].split(';', 1);
       if (parametro != '' && parametro != 'TODOS') {
         for (i = 0; i < $('#ncaCodigo').val().length; i++) {
            link+='&ncaCodigo=' + $('#ncaCodigo').val()[i].split(';')[0] + '&ncaDescricao=' + $('#ncaCodigo').val()[i].split(';')[1];
         }
       }
     }
     if ($('#nseCodigo').val() && $('#nseCodigo').val().length != 0) {
       parametro = $('#nseCodigo').val()[0].split(';', 1);
       if (parametro != '' && parametro != 'TODOS') {
         for (i = 0; i < $('#nseCodigo').val().length; i++) {
           link+='&nseCodigo=' + $('#nseCodigo').val()[i].split(';')[0] + '&nseDescricao=' + $('#nseCodigo').val()[i].split(';')[1];
         }
       }
     }
     if ($('#MAR_CODIGO').val() && $('#MAR_CODIGO').val().length != 0) {
       parametro = $('#MAR_CODIGO').val()[0].split(';', 1);
       if (parametro != '' && parametro != 'TODOS') {
         for (i = 0; i < $('#MAR_CODIGO').val().length; i++) {
            link+='&MAR_CODIGO=' + $('#MAR_CODIGO').val()[i].split(';')[0];
         }
       }
     }
     
     if (link.indexOf('&csaCodigo=') < 0 && f0.csaCodigo != null) {
         parametro = f0.csaCodigo.value.split(';', 1);
         if (parametro != '' && parametro != 'TODOS') {
             link+='&csaCodigo=' + parametro;
         }
       }

     // Recupera campos preenchidos para ser preenchido no reload da página
     if(f0.periodo != null && f0.periodo.value != null && f0.periodo.value != "") {
         link+='&periodo=' + f0.periodo.value;
     }
     if(f0.periodoIni != null && f0.periodoIni.value != null && f0.periodoIni.value != "") {
         link+='&periodoIni=' + f0.periodoIni.value;
     }
     if(f0.periodoFim != null && f0.periodoFim.value != null && f0.periodoFim.value != "") {
         link+='&periodoFim=' + f0.periodoFim.value;
     }
     if(f0.estCodigo != null && f0.estCodigo.value != null && f0.estCodigo.value != "") {
         link+='&estCodigo=' + f0.estCodigo.value;
     }
     // Para o relatorio mov_mes_csa, a seleção de correspondente é única, diferente dos otros, aí podemos enviar o corCodigo para manter o correspondente selecionado
     if(${responsavel.isCsa()} && f0.corCodigo != null && f0.corCodigo.value != null && f0.corCodigo.value != "") {
         link+='&corCodigo=' + f0.corCodigo.value;
     }

     postData(link);
   }
}

function desabilitaCamposRelEditavel() {
  <%if(relatorioCustomizaoOrg){%>
        if (document.getElementById('nome') != null) {
            document.getElementById('nome').disabled = true;
        }
        if (document.getElementById('OP_LOGIN') != null) {
            document.getElementById('OP_LOGIN').disabled = true;
        }
  <%}%>
}

var f0 = document.forms[0];
var camposObrigatorios = '';
var msgCamposObrigatorios = '';
setaOrdenacao();
addLoadEvent(function() { formLoad(); doLoad(${temProcessoRodando}); habilitaDesabilitaAgendamento() });
</script>
<% if (exibeBotaoRodape) { %>
<script>
    let btnDown = document.querySelector('#btns');
    const pageActions = document.querySelector('#page-actions');
    const pageSize = document.body.scrollHeight;

    function up() {
        window.scrollTo({
            top: 0,
            behavior: "smooth",
        });
    }

    function down() {
        let toDown = document.body.scrollHeight;
        window.scrollBy({
            top: toDown,
            behavior: "smooth",
        });
    }

    function toActionBtns() {
        let save = document.querySelector('#actions').getBoundingClientRect().top;
        window.scrollBy({
            top: save,
            behavior: "smooth",
        });
    }

    function btnTab() {
        let scrollSize = document.documentElement.scrollTop;
        if (scrollSize >= 300) {
            btnDown.classList.add('btns-active');    
        } else {
            btnDown.classList.remove('btns-active');
        }
    }

    window.addEventListener('scroll', btnTab);
</script>
<% } %>
</c:set>
<%-- Leiaute Fixo --%>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>