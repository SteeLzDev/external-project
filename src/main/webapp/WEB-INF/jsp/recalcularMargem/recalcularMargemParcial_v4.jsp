<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@page import="com.zetra.econsig.dto.TransferObject"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ page import="com.zetra.econsig.job.process.ControladorProcessos"%>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession"/>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
boolean matriculaApenasNumerica = (boolean) request.getAttribute("matriculaApenasNumerica");
List<TransferObject> servidores = (List<TransferObject>) request.getAttribute("servidores");
String chave = (String) request.getAttribute("chave");
boolean temProcessoRodando = (boolean) ControladorProcessos.getInstance().verificar(chave, session);
String fileName = (String) request.getAttribute("fileName");
String direction = (String) request.getAttribute("direction");
%>
<c:set var="title">
  <hl:message key="rotulo.recalcula.margem.servidores.titulo"/>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-sistema"></use>
</c:set>
<c:set var="bodyContent">
<form name="form1" method="post" action="../v3/recalcularMargemParcial?acao=confirmar&<%=SynchronizerToken.generateToken4URL(request)%>">
  <input type="hidden" name="Acao" id="Acao" value="" />
  <input type="hidden" name="direction" id="direction" value="<%=direction%>">
  <% if (!temProcessoRodando) { %>
    <% if (servidores == null || servidores.size() == 0) { %>
    <input type="hidden" name="MATRICULAS" id="MATRICULAS" value="" />
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="mensagem.informe.recalcula.margem.servidores.dados"/></h2>
        </div>
        <div class="card-body">
          <%-- Disponibiliza upload de arquivo para recalculo de margem parcial --%>
          <hl:fileUploadV4 
            divClassArquivo="form-group col-sm-6 mb-1"
            obrigatorio="true"
            mostraCampoDescricao="false"
            extensoes="<%=new String[]{"txt"}%>" 
            tipoArquivo="margem_parcial" />
          <div class="row">
            <div class="form-group col-sm-12  col-md-6">
              <hl:campoMatriculav4 placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.matricula", responsavel)%>' />
            </div>
            <div class="form-group col-sm-12 col-md-1 mt-4">
              <a id="adicionaAdeLista" class="btn btn-primary w-50" href="javascript:void(0);" onClick="insereItem('RSE_MATRICULA', 'LISTA');" aria-label='<hl:message key="mensagem.inserir.ade.numero.clique.aqui"/>'>
                <svg width="15"><use xlink:href="../img/sprite.svg#i-avancar"></use></svg>
              </a>
              <a id="removeAdeLista" class="btn btn-primary w-50 mt-1" href="javascript:void(0);" onClick="removeDaLista('LISTA');" aria-label='<hl:message key="mensagem.remover.ade.numero.clique.aqui"/>'>
                <svg width="15"><use xlink:href="../img/sprite.svg#i-voltar"></use></svg>
              </a>
            </div>
            <div id="adeLista" class="form-group col-sm-12 col-md-5">
              <label for="LISTA"><hl:message key="rotulo.lista"/></label>
              <select class="form-control w-100" id="LISTA" multiple="multiple" size="6"></select>
            </div>
          </div>
        </div>
      </div>  
      <div class="btn-action">
        <%if (direction.isEmpty()){%>
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal')"><hl:message key="rotulo.botao.cancelar"/></a>
        <% } else if (direction.equals("1")) { %>
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/importarMargem?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>')"><hl:message key="rotulo.botao.voltar"/></a>
        <% } else if(direction.equals("2")) { %>
        <a class="btn btn-outline-danger" href="#no-back" onClick=" postData('../v3/listarRetornoIntegracao?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>')"><hl:message key="rotulo.botao.voltar"/></a>
        <% } else if(direction.equals("3")) { %>
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/listarArquivosRetornoAtrasado?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>')"><hl:message key="rotulo.botao.voltar"/></a>
        <% } else if(direction.equals("4")) {%>
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/importarHistorico?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>')"><hl:message key="rotulo.botao.voltar"/></a>
        <% } %>
        <a class="btn btn-primary" id="submit" href="#no-back" onClick="if (setAcao('Pesquisar')) { f0.submit(); } return false;" title="<hl:message key="rotulo.botao.confirmar"/>"><hl:message key="rotulo.botao.pesquisar"/></a>
      </div>
      <div class="card">
        <hl:infoPeriodoV4 tipo="margem_parcial"/>
      </div>      
    <% } else { %>
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><%=(TextHelper.isNull(fileName) ? ApplicationResourcesHelper.getMessage("rotulo.selecione.servidores", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.conferir.servidores.arquivo", responsavel))%></h2>
        </div>
        <div class="card-body table-responsive p-0">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th scope="col" width="3%" class="colunaUnica" <%=TextHelper.isNull(fileName) ? "style=\"display: none;\"": "" %>>
                  <input type="checkbox" class="form-check-input ml-0" name="checkAll_margemParcial" id="checkAll_margemParcial" data-bs-toggle="tooltip" title="<hl:message key="rotulo.campo.todos.simples"/>" <%=TextHelper.isNull(fileName) ? "": "disabled=\"true\" checked" %>>
                </th>
                <th scope="col"><hl:message key="rotulo.servidor.nome"/></th>
                <th scope="col"><hl:message key="rotulo.servidor.cpf"/></th>
                <th scope="col"><hl:message key="rotulo.servidor.status"/></th>
                <th scope="col"><hl:message key="rotulo.servidor.matricula"/></th>
                <th scope="col"><hl:message key="rotulo.orgao.singular"/></th>
                <th scope="col"><hl:message key="rotulo.estabelecimento.abreviado"/></th>
                <% if (TextHelper.isNull(fileName)) { %>
                  <th scope="col"><hl:message key="rotulo.acoes"/></th>
                <% } %>
              </tr>
            </thead>
            <tbody>
              <%
              boolean recalculaMargemArquivo = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "FILE1"));
              CustomTransferObject servidor = null;
              String serNome, serCpf, rseMatricula, orgNome, rseCodigo, orgIdentificador, estIdentificador, serStatus;
              Iterator<TransferObject> it = servidores.iterator();
              while (it.hasNext()) {
                servidor = (CustomTransferObject)it.next();
                serNome  = (String)servidor.getAttribute(Columns.SER_NOME);
                serCpf   = (String)servidor.getAttribute(Columns.SER_CPF);
                rseMatricula = (String)servidor.getAttribute(Columns.RSE_MATRICULA);
                orgNome   = (String)servidor.getAttribute(Columns.ORG_NOME);
                rseCodigo = (String)servidor.getAttribute(Columns.RSE_CODIGO);
                orgIdentificador = (String)servidor.getAttribute(Columns.ORG_IDENTIFICADOR);
                estIdentificador = (String)servidor.getAttribute(Columns.EST_IDENTIFICADOR);
                serStatus = (String)servidor.getAttribute(Columns.SRS_DESCRICAO);
              %>
                <tr class="selecionarLinha">
                  <td class="colunaUnica" aria-label="" title="" data-bs-toggle="tooltip" data-original-title="" <%=TextHelper.isNull(fileName) ? "style=\"display: none;\"": "" %>>
                    <div class="form-check">
                      <input type="checkbox" class="form-check-input ml-0" name="RSE_CODIGO" value="<%=TextHelper.forHtmlAttribute(rseCodigo)%>" <%=(recalculaMargemArquivo ? " disabled=\"true\" checked " : "")%>>
                    </div>
                  </td>
                  <td class="selecionarColuna"><%=TextHelper.forHtmlContent(serNome)%></td>
                  <td class="selecionarColuna"><%=TextHelper.forHtmlContent(serCpf)%></td>
                  <td class="selecionarColuna"><%=TextHelper.forHtmlContent(serStatus)%></td>
                  <td class="selecionarColuna"><%=TextHelper.forHtmlContent(rseMatricula)%></td>
                  <td class="selecionarColuna"><%=TextHelper.forHtmlContent(orgNome + " - " + orgIdentificador)%></td>
                  <td class="selecionarColuna"><%=TextHelper.forHtmlContent(estIdentificador)%></td>
                  <% if (TextHelper.isNull(fileName)) { %>
                    <td class="acoes">
                      <a href="#" onclick="escolhechk('Selecionar',this)"><hl:message key="rotulo.acoes.selecionar"/></a>
                    </td>
                  <% } %>
                </tr>
              <%
              }
              %>
            </tbody>
          </table>
        </div>
      </div>
      <div class="btn-action">
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;" title="<hl:message key="rotulo.botao.voltar"/>"><hl:message key="rotulo.botao.voltar"/></a>
        <a class="btn btn-primary" id="submit" href="#no-back" onClick="validaSubmit(); return false;" title="<hl:message key="rotulo.botao.confirmar"/>"><svg width="17"><use xlink:href="#i-confirmar"></use></svg><hl:message key="rotulo.botao.confirmar"/></a>
      </div>
    <% } 
    } %>
</form>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript" src="../js/listagem.js"></script>
<hl:fileUploadV4 scriptOnly="true" tipoArquivo="margem_parcial" />
<% if (servidores == null || servidores.size() == 0) { %>
  <hl:campoMatriculav4 scriptOnly="true"/>
<% } %>
<script>
var f0 = document.forms[0];

window.onload = doLoad(<%=(boolean)temProcessoRodando%>);formLoad();

function setAcao(Acao) {
  if (Acao == 'Confirmar') {
    var tam = f0.RSE_CODIGO.length;
    if (tam == undefined) {
      if (f0.RSE_CODIGO.checked == false) {
        alert('<%=ApplicationResourcesHelper.getMessage("mensagem.informe.recalcula.margem.servidores.selecao", responsavel)%>');
        return false;
      }
    } else {
      var qtd = 0;
      for(var i = 0; i < tam; i++) {
        if (f0.RSE_CODIGO[i].checked == true) {
          qtd++
        }
      }
      if (qtd <= 0) {
        alert('<%=ApplicationResourcesHelper.getMessage("mensagem.informe.recalcula.margem.servidores.selecao", responsavel)%>');
        return false;
      }
    }
    if (confirm('<%=ApplicationResourcesHelper.getMessage("mensagem.recalcula.margem.confirma", responsavel)%>')) {
      f0.Acao.value = Acao;
      return true;
    }
    
  } else if (Acao == 'Pesquisar') {
    var lista = document.getElementById('LISTA');
    var file1 = document.getElementById('FILE1');
    
  if (file1 == undefined || file1.value.trim() == '') {
      if (lista == undefined || lista.length == undefined || lista.length == 0) {
        alert('<%=ApplicationResourcesHelper.getMessage("mensagem.informe.recalcula.margem.servidores.adicao", responsavel)%>');
        f0.RSE_MATRICULA.focus();
        return false;
      } else if (<%=TextHelper.forJavaScriptBlock(matriculaApenasNumerica)%>) {
        for (var i = 0; i < lista.length; i++) {
          var proximo = lista.options[i].value;
          if (isNumber(proximo)) {
            lista.options[i].value = proximo.replace(/^[0]+/g,"");
          } else {
            alert(mensagem("mensagem.erro.recalcula.margem.servidores.valor").replace("{0}",proximo));
            return false;
          }
        }
      }
    
      montaListaIps('MATRICULAS','LISTA'); 
  }

  document.forms[0].action= '../v3/recalcularMargemParcial?acao=pesquisar&<%=SynchronizerToken.generateToken4URL(request)%>';
    return true;
  }
  return false;
}

function doLoad(reload) {
  if (reload) {
    setTimeout("refresh()", 15*1000);
  }
}

function refresh() {
  postData('../v3/recalcularMargemParcial?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>');
        
}
function formLoad() {
  if (f0.RSE_MATRICULA != undefined && f0.RSE_MATRICULA != null) {
    f0.RSE_MATRICULA.focus();
  }
}

function validaSubmit()
{
    if( setAcao('Confirmar') )
    { 
      if(typeof vfRseMatricula === 'function')
      {
        if(vfRseMatricula(true))
        {
          enableAll();
          f0.submit();
        }
      }
      else
      {
      enableAll();
        f0.submit();
      } 
    }
}

//Checkbox
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

</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>   
    <jsp:attribute name="javascript">${javascript}</jsp:attribute> 
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>   
