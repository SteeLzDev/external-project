<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.helper.Pair"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

List<Pair<File, String>> arquivosRetorno = (List<Pair<File, String>>) request.getAttribute("arquivosRetorno");
String pathRetorno = (String) request.getAttribute("pathRetorno");
String ultPeriodoImpRet = (String) request.getAttribute("ultPeriodoImpRet");
List<TransferObject> lstEstabelecimentos = (List<TransferObject>) request.getAttribute("lstEstabelecimento");
List<TransferObject> lstOrgaos = (List<TransferObject>) request.getAttribute("lstOrgao");
boolean quinzenal = (Boolean) request.getAttribute("quinzenal");
boolean atalhoUpload = request.getAttribute("atalhoUpload") != null ? (boolean) request.getAttribute("atalhoUpload") : false;
boolean temProcessoRodando = (boolean) request.getAttribute("temProcessoRodando");
%>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="title">
  <hl:message key="mensagem.folha.importar.retorno.integracao.atrasado.titulo"/>
</c:set>
<head>
  <link rel="stylesheet" href="../css/impromptu.css?<hl:message key="release.tag"/>" type="text/css">
</head>
<c:set var="bodyContent">
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
                           onclick="postData('../v3/listarArquivosRetornoAtrasado?acao=atalhoUpload&<%=SynchronizerToken.generateToken4URL(request)%>')"><hl:message
                                key="rotulo.atalho.upload"/></a>
                        <% } %>
                        <% if (responsavel.temPermissao(CodedValues.FUN_RECALCULAR_MARGEM_GERAL)) { %>
                        <a class="dropdown-item" href="#no-back"
                           onClick="postData('../v3/recalcularMargemGeral?acao=iniciar&direction=3&<%=SynchronizerToken.generateToken4URL(request)%>')">
                            <hl:message key="rotulo.recalcular.margem.geral"/> </a>
                        <% } %>
                        <% if (responsavel.temPermissao(CodedValues.FUN_RECALCULAR_MARGEM_PARCIAL)) {%>
                        <a class="dropdown-item" href="#no-back"
                           onClick="postData('../v3/recalcularMargemParcial?acao=iniciar&direction=3&<%=SynchronizerToken.generateToken4URL(request)%>')">
                            <hl:message key="rotulo.recalcular.margem.parcial"/> </a>
                        <% } %>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <% } %>
<form name="form1" method="post" action="../v3/listarArquivosRetornoAtrasado?<%=SynchronizerToken.generateToken4URL(request)%>">
  <!-- MODAL -->
  <div class="modal fade" id="importaRetorno" tabindex="-1" role="dialog" aria-labelledby="modalTitulo" aria-hidden="true" style="display: none;">
    <div class="modal-dialog modal-wide-content modal-dia-corte" role="document">
      <div class="modal-content">
        <div class="modal-header pb-0">
          <h5 class="modal-title about-title mb-0" id="modalTitulo"><hl:message key="mensagem.informe.folha.dados.confirmacao"/></h5>
          <button type="button" class="logout mr-2" data-bs-dismiss="modal" aria-label="<hl:message key="rotulo.botao.fechar"/>">
            <span aria-hidden="true">×</span>
          </button>
        </div>
        <div class="modal-body pb-0 pt-1">
            <div class="alert alert-warning" role="alert">
              <p id="dialogContent" class="mb-0"></p>
            </div>
            <div class="row">
              <div class="form-group col-sm-6">
                <label id="lblDataPeriodoPage" for="periodo"><hl:message key="rotulo.folha.periodo"/></label>
                <hl:htmlinput name="edtPeriodo" di="edtPeriodo" type="text" classe="form-control" size="10" mask="DD/DDDD" />
              </div>
            </div>
            <% if (responsavel.isCseSup()) { %>
            <div class="form-group mb-1" role="radiogroup" aria-labelledby="campoEntidade">
              <span id="campoEntidade"><hl:message key="rotulo.campo.entidade"/></span>
              <div class="form-check pt-3">
                <input type="radio" name="tipoEntidade" id="geralSingular" onChange="alterarTipoEntidade()" value="<%=(String)AcessoSistema.ENTIDADE_CSE%>" checked onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                <label class="form-check-label labelSemNegrito ml-1 pr-4" for="geralSingular"><hl:message key="rotulo.geral.singular"/></label>
                <input type="radio" name="tipoEntidade" id="estabelecimentoSingular" onChange="alterarTipoEntidade()" value="<%=(String)AcessoSistema.ENTIDADE_EST%>" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                <label class="form-check-label labelSemNegrito ml-1 pr-4" for="estabelecimentoSingular"><hl:message key="rotulo.estabelecimento.singular"/></label>
                <input type="radio" name="tipoEntidade" id="orgaoSingular" onChange="alterarTipoEntidade()" value="<%=(String)AcessoSistema.ENTIDADE_ORG%>" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                <label class="form-check-label labelSemNegrito ml-1 pr-4" for="orgaoSingular"><hl:message key="rotulo.orgao.singular"/></label>
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm-6">
                <label id="lblDataPeriodoPage" for="estCodigo"><hl:message key="rotulo.estabelecimento.singular"/></label>
                <%=JspHelper.geraCombo(lstEstabelecimentos, "estCodigo", Columns.EST_CODIGO, Columns.EST_NOME + ";" + Columns.EST_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), "class=\"form-control\"" , false, 1, "", true)%>
              </div>
              <div class="form-group col-sm-6">
                <label id="lblDataPeriodoPage" for="estCodigo"><hl:message key="rotulo.orgao.singular"/></label>
                <%=JspHelper.geraCombo(lstOrgaos, "orgCodigo", Columns.ORG_CODIGO, Columns.ORG_NOME + ";" + Columns.ORG_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), "class=\"form-control\"" , false, 1, "", true)%>
              </div>
            </div>
            <% } %>
            <div class="row">
              <fieldset class="col-sm-12 col-md-12">
                <div class="legend">
                  <span><hl:message key="rotulo.retorno.atrasado.soma.parcela"/></span>
                </div>
                <div class="form-check">
                  <div class="row">
                    <div class="col-sm-6 col-md-3">
                      <span class="text-nowrap align-text-top">
                      <input type="radio" name="retAtrasadoSomaParcela" id="retAtrasadoSomaParcela1" value="1" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                      <label class="form-check-label labelSemNegrito ml-1" for="retAtrasadoSomaParcela1"><hl:message key="rotulo.sim"/></label>
                      </span>
                    </div>
                    <div class="col-sm-6 col-md-3">
                      <span class="text-nowrap align-text-top">
                      <input type="radio" name="retAtrasadoSomaParcela" id="retAtrasadoSomaParcela0" value="0" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                      <label class="form-check-label labelSemNegrito ml-1" for="retAtrasadoSomaParcela0"><hl:message key="rotulo.nao"/></label>
                      </span>
                    </div>
                  </div>
                </div>
              </fieldset>
            </div>
        </div>
        <div class="modal-footer pt-0">
          <div class="btn-action mt-2 mb-0">
            <a class="btn btn-outline-danger" data-bs-dismiss="modal" href="#"><hl:message key="rotulo.botao.cancelar"/></a>
            <a class="btn btn-primary" data-bs-dismiss="modal" href="#noback" onClick="confirmaImportarRetorno()">
              <hl:message key="rotulo.botao.confirmar"/>
            </a>
          </div>
        </div>
      </div>
    </div>
  </div>
  
  
  
  
  <div class="card">
    <div class="card-header hasIcon pl-3">
      <h2 class="card-header-title"><hl:message key="mensagem.folha.arquivos.retorno.disponiveis.importacao"/></h2>
    </div>
    <div class="card-body table-responsive p-0">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th scope="col"><hl:message key="rotulo.folha.nome"/></th>
            <th scope="col"><hl:message key="rotulo.folha.tamanho.abreviado"/></th>
            <th scope="col"><hl:message key="rotulo.folha.data"/></th>
            <%if (responsavel.isCseSupOrg()) {%>
              <th scope="col"><hl:message key = "rotulo.estabelecimento.abreviado"/> - <hl:message key="rotulo.orgao.abreviado"/></th>
            <%}%>
            <th scope="col" width="10%"><hl:message key="rotulo.acoes"/></th>
          </tr>
        </thead>
        <tbody>
          <%
            if (arquivosRetorno == null || arquivosRetorno.size() == 0){
          %>
          <tr>
            <td colspan="6"><hl:message key="mensagem.folha.nenhum.arquivo.encontrado"/></td>
          </tr>
          <%
            } else {
                int i = 0;
                for (Pair<File, String> arquivoRetorno : arquivosRetorno) {
                   File arquivo = arquivoRetorno.first;
                   String entidade = arquivoRetorno.second;
                   String tam = "";
                   if (arquivo.length() > 1024.00) {
                      tam = Math.round(arquivo.length() / 1024.00) + " " + ApplicationResourcesHelper.getMessage("rotulo.kilobyte.abreviado", responsavel);
                   } else {
                      tam = arquivo.length() + " " + ApplicationResourcesHelper.getMessage("rotulo.byte.abreviado", responsavel);
                   }
                   String data = DateHelper.toDateTimeString(new java.sql.Date(arquivo.lastModified()));
                   String nome = arquivo.getPath().substring(pathRetorno.length());
                   String nomeOriginal = arquivo.getName().replaceAll("\\.crypt", "");
          %>
          <tr>
            <td><%=TextHelper.forHtmlContent(nomeOriginal)%></td>
            <td><%=TextHelper.forHtmlContent(tam)%>&nbsp;</td>
            <td><%=TextHelper.forHtmlContent(data)%></td>
            <% if (responsavel.isCseSupOrg()) { %>
              <td><%=TextHelper.forHtmlContent(entidade)%></td>
            <% } %>
            <td align="center"><a href="#no-back" onClick="importaRetorno(encodeURIComponent('<%=TextHelper.forJavaScript(nome)%>'), <%=responsavel.isCseSup()%>)" VALUE="Retorno"><hl:message key="rotulo.botao.processar"/></a></td>
          </tr>
          <%
                }
            }
          %>
        </tbody>
        <tfoot>
          <tr>
            <td colspan="6">
              <hl:message key="rotulo.listagem.arquivos.retorno.folha.disponivel"/>
            </td>
          </tr>
        </tfoot>
      </table>
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" aria-label='<hl:message key="rotulo.botao.voltar"/>' href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
  </div>       
</form>
<% } else { %>
  <div class="btn-action">
    <a class="btn btn-outline-danger" aria-label='<hl:message key="rotulo.botao.voltar"/>' href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
<% } %>
</c:set>
<c:set var="javascript">
<link rel="stylesheet" href="../css/jquery-dialog.css?<hl:message key="release.tag"/>" type="text/css">
<script type="text/JavaScript">
var arquivoCache,isCseSupCache  = null;

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
function importaRetorno(arquivo, isCseSup) {
	arquivoCache = arquivo;
	isCseSupCache = (isCseSup ? true : false);
    // Mensagem para confirmação da importação
    var msg = '<hl:message key="<%= quinzenal ? "mensagem.confirmacao.importacao.retorno.atrasado.quinzenal" : "mensagem.confirmacao.importacao.retorno.atrasado.mensal" %>" arg0="<%=TextHelper.forHtmlAttribute(ultPeriodoImpRet)%>"/>';
    $( '.alert-warning #dialogContent').text(msg);
    // Solicita confirmação da importação
    $("#importaRetorno").modal("show");
}

function confirmaImportarRetorno() {
	importaRetornoAtrasado(arquivoCache, isCseSupCache);
}

function importaRetornoAtrasado(arquivo, validarCamposEntidade) {
    var url = '../v3/listarArquivosRetornoAtrasado?acao=importarRetornoAtrasado&arquivo=' + arquivo + '&periodo=' + $( "#edtPeriodo").val() + '&<%=SynchronizerToken.generateToken4URL(request)%>';
    if (validarCamposEntidade) {
    var tipoEntidade = $("input:radio[name=tipoEntidade]:checked" ).val();
    var est = $("#estCodigo").val();
    var org = $("#orgCodigo").val();
    if (tipoEntidade == 'EST' || tipoEntidade == 'ORG') {
      if (tipoEntidade == 'EST') {
        if (est == null || est == "") {
          alert('<hl:message key="mensagem.informe.estabelecimento"/>');
          return false;
        }
      } else if (tipoEntidade == 'ORG') {
        if (org == null || org == "") {
          alert('<hl:message key="mensagem.informe.orgao"/>');
          return false;
        }
      }
      url = url + "&ENTIDADEALTERADA=S&EST_CODIGO=" + est + "&ORG_CODIGO=" + org;
    }
    }

    if($("input[name='retAtrasadoSomaParcela']:checked").val() == '1') {
      url = url + "&retAtrasadoSomaParcela=true";
    }
    
    postData(url);
    // returna true para fechar o diálogo de confirmação
    return true;
}

function doLoad(reload) {
    if (reload) {
        setTimeout("refresh()", 15*1000);
    }
}

function refresh() {
    postData("../v3/listarArquivosRetornoAtrasado?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>");
}

doLoad(<%=temProcessoRodando%>);
</script>
  <script type="text/JavaScript">
    var f0 = document.forms[0];

    function alterarTipoArquivo() {

      var tipo = "retornoatrasado";

      var link = "../v3/listarArquivosRetornoAtrasado?acao=iniciar";

      if (tipo != '') {
        tipo = tipo.charAt(0).toUpperCase() + tipo.slice(1);
        link = "../v3/uploadListar" + tipo + "?acao=carregar";

        if (f0.PAP_CODIGO != null) {
          var papCodigo = getCheckedRadio('form2', 'PAP_CODIGO');
          link += "&PAP_CODIGO=" + papCodigo;
        }
      }
      link += "&" + "<%=SynchronizerToken.generateToken4URL(request)%>";
      console.log(link);
      postData(link);
    }


  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
