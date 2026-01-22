<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

boolean exigeMotivo = false;

String rseCodigo = (String) request.getAttribute("rseCodigo");
ServidorTransferObject servidor = (ServidorTransferObject) request.getAttribute("servidor");
RegistroServidorTO registroServidor = (RegistroServidorTO) request.getAttribute("registroServidor");

List csaOculta = (List) request.getAttribute("csaOculta");
String possuiContratoCsa = (String) request.getAttribute("possuiContratoCsa");
List consignatarias = (List) request.getAttribute("consignatarias");

%>

<c:set var="javascript">
<% if (!responsavel.isSer()) { %>
<hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="<%=ApplicationResourcesHelper.getMessage("mensagem.confirmacao.ocultar.rse.csa", responsavel)%>" scriptOnly="true" />
<% } %>

<script language="JavaScript" type="text/JavaScript">
var f0 = document.forms[0];

const possuiContratoCsa = [<%=possuiContratoCsa%>];
</script>

<script language="JavaScript" type="text/JavaScript">
function formLoad() {
  focusFirstField();
}
  //habilita campo de valor realizado quando linha da tabela é clicada e aparece coluna de checkbox pela 1a vez
  $(".selecionarLinha").click(function() {
  	var prdNumero = $(this).parent().find('input[type="hidden"]');		
  });

  function checkUnCheckAll() {
      if (f0.checkAll.checked) {
    	  checkAll(f0, 'selecionarCheckBox');
    	  checkAllCampos(f0);
      }	else {
    	  uncheckAll(f0, 'selecionarCheckBox');
    	  uncheckAllCampos(f0);
      }    
  }

  function abrirModal(){
	   $('#copiarParaTodos').modal('show');
	}

  function checkAllCampos(form) {
	  checkObj = document.forms[0].selecionarCheckBox;
	  for (i=0; i<checkObj.length; ++i) {
	    checkObj[i].checked = true;
	    //document.getElementById('cv_' + checkObj[i].value).disabled = false;
	  }
	}

	function uncheckAllCampos(form) {
		checkObj = document.forms[0].selecionarCheckBox;
	  for (i=0; i<checkObj.length; ++i) {
	    checkObj[i].checked = false;
	    //document.getElementById('cv_' + checkObj[i].value).disabled = true;
	  }
	}  

function copia_qdte(campoQtd, campoObs) {
  // Navega em todos os campos do formulário
  for (i = 0; i < f0.elements.length; i++) {
    var e = f0.elements[i];
    
    if (e.name.indexOf('nse_') == 0) {
      // Se é campo de Qtd, atribui valor do campo padrão
      e.value = campoQtd.value;
    } else if (e.name.indexOf('nse2_') == 0) {
      // Se é campo de Obs, atribui valor do campo padrão
      e.value = campoObs.value;
    }
  }
}

function checkObs(nse) {
  qtdField = getElt('nse_' + nse);
  obsField = getElt('nse2_' + nse);
  
  if (qtdField.value == '') {
    obsField.value = '';
  }
}

function validaForm(){
  var tmoCodigo = getElt('TMO_CODIGO');

  const resultado = [];
  const csaCodigos = document.forms[0].selecionarCheckBox;

  for (i=0; i<csaCodigos.length; ++i) {
    if (csaCodigos[i].checked) {
      const encontrado = possuiContratoCsa.find(item => item.codigo === csaCodigos[i].value);
      if (encontrado) {
        resultado.push(encontrado.nome);
      }
    }
  }

  var msg = '<hl:message key="mensagem.confirmacao.ocultar.rse.csa"/>';
  if (resultado.length > 0) {
    msg = mensagem('mensagem.confirmacao.ocultar.rse.csa.arg').replace('{0}', resultado);
  }

  if (confirm(msg)) {
    // Se exige motivo, tem que validar a seleção de um motivo e o preenchimento ou não da observação; 
    if (<%=exigeMotivo %>) {
        if(tmoCodigo.value && confirmaAcaoConsignacao()) {
          f0.submit();
        // Se exige motivo o motivo tem que estar preenchido, do contrário manda alerta para o usuário
        } else if (!tmoCodigo.value) {
            alert('<hl:message key="mensagem.motivo.operacao.obrigatorio"/>');
              return false;
        }
    } else {
        // Se NÃO exige motivo, se o motivo não foi selecionado, pode dar submit
        if (tmoCodigo == undefined || !tmoCodigo.value) {
            f0.submit();
        } 
        // porém se o motivo foi selecionado, tem que verificar se o motivo exige obs
        else if (tmoCodigo.value && confirmaAcaoConsignacao()) {
            f0.submit();
        } 
    }
  }
}
</script>
</c:set>
<c:set var="title">
  <hl:message key="rotulo.ocultar.servidor.csa.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">

<form method="post" action="../v3/ocultarRegistroSerCsa?acao=editar&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><%=TextHelper.forHtmlContent(registroServidor.getRseMatricula())%> - <%=TextHelper.forHtmlContent(servidor.getSerNome())%></h2>
    </div>
    <div class="card-body table-responsive p-0">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th nowrap scope="col" width="3%">
              <div class="form-check">
                <input type="checkbox" class="form-check-input ml-0" id="checkAll" name="checkAll" onClick="checkUnCheckAll();" data-bs-toggle="tooltip" data-original-title='<hl:message key="rotulo.acoes.selecionar"/>' alt='<hl:message key="rotulo.acoes.selecionar"/>' title='<hl:message key="rotulo.acoes.selecionar"/>'>
              </div>
            </th>
            <th scope="col" width="10%"><hl:message key="rotulo.ocultar.servidor.csa.codigo"/></th>
            <th scope="col" width="35%"><hl:message key="rotulo.ocultar.servidor.csa.nome"/></th>
            <th scope="col" width="35%"><hl:message key="rotulo.ocultar.servidor.csa.nome.abrev"/></th>
          </tr>
        </thead>
        <tbody>
        <%=JspHelper.msgRstVazio(consignatarias.size()==0, "3", "lp")%>
        <%
          CustomTransferObject consignataria = null;
          String csa_codigo, csa_identificador, csa_nome, csa_nome_abrev;
          List listaSvcAlterados = new ArrayList();
          Iterator it = consignatarias.iterator();
          while (it.hasNext()) {
            consignataria = (CustomTransferObject)it.next();
            csa_codigo = (String)consignataria.getAttribute(Columns.CSA_CODIGO);
            csa_identificador = consignataria.getAttribute(Columns.CSA_IDENTIFICADOR) != null ? (String)consignataria.getAttribute(Columns.CSA_IDENTIFICADOR) : "";
            csa_nome = consignataria.getAttribute(Columns.CSA_NOME) != null ? (String)consignataria.getAttribute(Columns.CSA_NOME) : "";
            csa_nome_abrev = consignataria.getAttribute(Columns.getColumnName(Columns.CSA_NOME_ABREV)) != null ? (String)consignataria.getAttribute(Columns.getColumnName(Columns.CSA_NOME_ABREV)) : "";
        %>
        <tr>
          <td nowrap class="ocultarColuna" aria-label='<hl:message key="rotulo.acoes.selecionar"/>' title='<hl:message key="rotulo.acoes.selecionar"/>' data-bs-toggle="tooltip" data-original-title='<hl:message key="rotulo.acoes.selecionar"/>'>
            <div class="form-check">
              <input type="checkbox" class="form-check-input ml-0" value="<%=TextHelper.forHtmlAttribute(csa_codigo)%>" 
              id="chk_<%=TextHelper.forHtmlAttribute(csa_codigo)%>" name="selecionarCheckBox"
              data-exibe-msg2="0" data-usa-link2="0" <%=(String)(csaOculta.contains(csa_codigo) ? "CHECKED" : "")%> >
            </div>
          </td>
          <td><%=TextHelper.forHtmlContent(csa_identificador.toUpperCase())%></td>
          <td><%=TextHelper.forHtmlContent(csa_nome.toUpperCase())%></td>
          <td><%=TextHelper.forHtmlContent(csa_nome_abrev.toUpperCase())%></td>
        </tr>
        <%
          }
        %>
        </tbody>
        <tfoot>
          <tr>
            <td colspan="4"><hl:message key="rotulo.ocultar.servidor.csa.listagem.consignataria"/></td>
          </tr>
        </tfoot>
      </table>
    </div>
  </div>
  <div class="btn-action">
      <INPUT TYPE="hidden" NAME="MM_update"          VALUE="true">
      <INPUT TYPE="hidden" NAME="RSE_CODIGO"         VALUE="<%=TextHelper.forHtmlAttribute(rseCodigo)%>">    
      <INPUT TYPE="hidden" NAME="tipo"               VALUE="editar">
      <a class="btn btn-outline-danger" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;" href="#"><hl:message key="rotulo.botao.cancelar"/></a>
      <a id="btnEnvia" class="btn btn-primary" href="#no-back" onClick="validaForm(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
  </div>
</form>  
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
