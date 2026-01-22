<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken"%>
<%@ page
  import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper"%>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@ page import="com.zetra.econsig.helper.web.JspHelper"%>
<%@ page import="com.zetra.econsig.values.CodedValues"%>
<%@ page import="com.zetra.econsig.values.Columns"%>
<%@ page import="com.zetra.econsig.persistence.entity.Subrelatorio"%>
<%@ page import="com.zetra.econsig.helper.upload.UploadHelper"%>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page
  import="com.zetra.econsig.persistence.entity.TipoFiltroRelatorio"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:useBean id="paramSession" scope="session"
  class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
Subrelatorio subrelatorio = (Subrelatorio) request.getAttribute("subrelatorio");
Map<String,TransferObject> relatorioFiltros = (Map<String,TransferObject>)request.getAttribute("relatorioFiltros");
String relCodigo = (String) request.getAttribute("relCodigo");
String tipo = (String) request.getAttribute("tipo");
String sreCodigo = (String) request.getAttribute("sreCodigo");
%>
<c:set var="title">
  <hl:message key="rotulo.subrelatorio.criar.novo" />
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-sistema"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
    <div class="col-sm">
      <form method="post"
        action="../v3/editarSubrelatorio?acao=salvar&tipo=<%=tipo%>&relCodigo=<%=relCodigo%>&MM_update=form1&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>"
        name="form1" enctype="multipart/form-data">
        <!-- Dados básicos relatório -->
        <div class="card">
          <div class="card-header">
            <h2 class="card-header-title">
              <hl:message key="rotulo.editar.subrelatorio.dados" /> - <%=!TextHelper.isNull(subrelatorio) ? TextHelper.forHtmlAttribute(subrelatorio.getSreTemplateJasper()) : ApplicationResourcesHelper.getMessage("rotulo.subrelatorio.criar.titulo", responsavel)%></h2>
          </div>
          <div class="card-body">
            <div class="row">
              <div class="col-sm-12 col-md-6">
                <span id="descricao"><hl:message key="rotulo.editar.subrelatorio.campo.fonte.original" /></span>
                <div class="form-group mb-1" role="radiogroup" aria-labelledby="fonteDescricao">
                  <div class="form-check form-check-inline">
                    <input class="form-check-input ml-1" type="radio" name="SRE_FONTE_DADOS" id="fonteDadosSim" title='<hl:message key="rotulo.sim"/>' VALUE="<%=(String)CodedValues.TPC_SIM%>" onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);">
                    <label class="form-check-label labelSemNegrito ml-1 pr-4" for="fonteDadosSim"><hl:message key="rotulo.sim" /></label>
                  </div>
                  <div class="form-check form-check-inline">
                    <input class="form-check-input ml-1" type="radio" name="SRE_FONTE_DADOS" id="fonteDadosNao" title='<hl:message key="rotulo.nao"/>' VALUE="<%=(String)CodedValues.TPC_NAO%>" checked onFocus="SetarEventoMascara(this,'#*100',true);" onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);">
                    <label class="form-check-label labelSemNegrito ml-1 pr-4" for="fonteDadosNao"><hl:message key="rotulo.nao" /></label>
                  </div>
                </div>
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm-12 col-md-4 mt-1">
                <label for="SRE_NOME_PARAMETRO"><hl:message key="rotulo.editar.subrelatorio.campo.nome.parametro" /></label>
                <input class="form-control" type="text" name="SRE_NOME_PARAMETRO" value="<%=TextHelper.forHtmlAttribute((subrelatorio != null ? subrelatorio.getSreNomeParametro() : ""))%>" size="100" onFocus="SetarEventoMascara(this,'#*A50',true);" onBlur="fout(this);ValidaMascara(this);" required/>
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="SRE_TEMPLATE_SQL"><hl:message key="rotulo.subrelatorio.campo.consulta" /></label>
                <hl:htmlinput name="SRE_TEMPLATE_SQL" di="SRE_TEMPLATE_SQL" onFocus="SetarEventoMascara(this,'#*65000',true);" type="textarea" classe="form-control" onBlur="fout(this);ValidaMascara(this);" rows="15" cols="80" value="<%=TextHelper.forHtmlAttribute((subrelatorio != null ? subrelatorio.getSreTemplateSql() : ""))%>" />
              </div>
              <div class="form-group col-sm-6">
                <label for="FILTRO_QUERY"><hl:message key="rotulo.relatorio.filtros.query" /></label>
                <SELECT class="form-control form-select w-100" NAME="FILTRO_QUERY" SIZE="13" ondblclick="incluiCampoQuery(this);">
                  <%
                    for(String tfrCodigo : relatorioFiltros.keySet()){
                      if(tfrCodigo.equals("campo_data_inclusao")){
                  %>
                        <OPTION VALUE="<@campo_data_inclusao_ini>"></OPTION>
                        <OPTION VALUE="<@campo_data_inclusao_fim>"></OPTION>
                  <%
                      } else if(!tfrCodigo.equals("campo_formato_relatorio") && !tfrCodigo.equals("campo_data_execucao") && 
                           !tfrCodigo.equals("campo_tipo_agendamento") && !tfrCodigo.equals("campo_periodicidade")){
                              String valorFiltroQuery = "<@" + tfrCodigo + ">";
                  %>
                              <OPTION VALUE="<%=TextHelper.forHtmlAttribute(valorFiltroQuery)%>"><%=TextHelper.forHtmlContent(valorFiltroQuery)%></OPTION>
                  <%
                        }
                    }
                  %>
                </SELECT>
              </div>
            </div>
            <hl:fileUploadV4 obrigatorio="<%=true%>" mostraCampoDescricao="<%=false%>" nomeCampoArquivo="FILE1" tituloCampoArquivo="<%=ApplicationResourcesHelper.getMessage("rotulo.subrelatorio.template.jasper", responsavel)%>" multiplo="false" extensoes="<%=UploadHelper.EXTENSOES_PERMITIDAS_SUBRELATORIO%>" tipoArquivo="relatorio_jasper" divClassArquivo="form-group col-sm-6 mt-2" />
            <% if (subrelatorio != null && !TextHelper.isNull(subrelatorio.getSreTemplateJasper())) { %>
              <input type="checkbox" name="removeSubTemplatejasper" id="removeSubTemplatejasper" value="<%=(String)CodedValues.TPC_SIM%>" />
              <label for="removeSubTemplatejasper"><hl:message key="rotulo.subrelatorio.remover.template.jasper" /></label>
            <% } %>
          </div>
        </div>
        <div class="btn-action">
          <a class="btn btn-outline-danger" id="btnVoltar" href="#no-back" onClick="postData('../v3/editarSubrelatorio?acao=iniciar&relCodigo=<%=TextHelper.forJavaScriptAttribute(relCodigo)%>&_skip_history_=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.botao.cancelar" /></a>
          <a class="btn btn-primary" id="btnConfirmar" href="#no-back" onClick="if(checkForDml() && verificaCampos()){f0.submit();} return false;"><hl:message key="rotulo.botao.confirmar" /></a>
        </div>
        <hl:htmlinput type="hidden" name="sreCodigo" di="sreCodigo"
          value="<%=sreCodigo%>" />
      </form>
    </div>
  </div>
</c:set>
<c:set var="javascript">
  <hl:fileUploadV4 obrigatorio="<%=true%>" botaoVisualizarRemover="<%=false%>" multiplo="false" scriptOnly="true" nomeCampoArquivo="FILE1" extensoes="<%=UploadHelper.EXTENSOES_PERMITIDAS_SUBRELATORIO%>" tipoArquivo="relatorio_jasper" />
  <script type="text/JavaScript">
        $(document).ready(function () {
      	  if($('input[type=radio][id=fonteDadosSim]').is(':checked')) {
      		  EnabledisableFields(true);
      	  }
        });
    	// Verifica a presença de requisições de alteração do banco, evito o submit
    	function checkForDml() {
    	  var requisicoes = new Array ("INSERT", "UPDATE", "DELETE", "DROP", "CREATE", "ALTER", "TRUNCATE");
    	  var sql = f0.SRE_TEMPLATE_SQL.value;
    	  for (var i = 0; i < requisicoes.length; i++) {
    	    var requisicao = requisicoes[i];
    	    if (sql.toUpperCase().indexOf(requisicao) > -1) {
    	      alert('<hl:message key="mensagem.erro.relatorio.palavra.reservada"/>'.replace("{0}", requisicao));
    	      return false;
    	    }
    	  }
    	  return true;
    	}  

    	function incluiCampoQuery(filtro) {
      	  var valor = filtro.options[filtro.selectedIndex].value;
      	  insertAtCursor(f0.SRE_TEMPLATE_SQL, valor);
      	}

    	function insertAtCursor(myField, myValue) {
    		if (document.selection) {
    		 	//IE support
    	    	myField.focus();
    	    	sel = document.selection.createRange();
    	    	sel.text = myValue;
    		} else if (myField.selectionStart || myField.selectionStart == '0') {
    			//MOZILLA/NETSCAPE support
    	    	var startPos = myField.selectionStart;
    	    	var endPos = myField.selectionEnd;
    	    	myField.value = myField.value.substring(0, startPos) + myValue + myField.value.substring(endPos, myField.value.length);
    		} else {
    			myField.value += myValue;
    		}
    	}

    	function verificaCampos() {
    	  var controles = new Array("SRE_NOME_PARAMETRO");
    	  var msgs = new Array ('<hl:message key="mensagem.informe.subrelatorio.parametro"/>');

    	  if (!ValidaCampos(controles, msgs)) {
    	    return false;
    	  }
    	  
    	  // Verifica filtros
    	  
    	  if(!document.getElementById("fonteDadosSim").checked){
        	  with(document.forms[0]) {
        	    // Verifica se todos os filtros selecionados foram incluídos na query
        	    for (var i = 0; i < FILTRO_QUERY.length; i++) {
        	      var filtroQuery = FILTRO_QUERY.options[i].value;
        	   	  if (SRE_TEMPLATE_SQL.value.indexOf(filtroQuery) < 0) {
        	   	    alert('<hl:message key="mensagem.informe.relatorio.filtro.query"/>'.replace("{0}", filtroQuery));
        	   	    return false;
        	   	  }
        	    }
        
        	    // Verifica se todos os filtros incluídos na query foram selecionados
        	    var filtrosQuery = SRE_TEMPLATE_SQL.value.match(/\<[@][A-Za-z0-9_\-\.]+\>/g);
        	    if (filtrosQuery != null) {
        	      for (var x = 0; x < filtrosQuery.length; x++) {
        	        var possuiFiltro = false;
        	   	    for (var i = 0; i < FILTRO_QUERY.length; i++) {
        	   	      if (FILTRO_QUERY.options[i].value == filtrosQuery[x]) {
        	   	    	possuiFiltro = true;
        	   	      }
        	   	    }
        		    if (!possuiFiltro) {
        	   	      alert('<hl:message key="mensagem.informe.relatorio.filtro"/>'.replace("{0}", filtrosQuery[x]));
        	   	      return false;
        	   	    }
        	      }
        	    }
        	  }
    	  }
    	  return true;
    	}
    
    	function filtroAtivo(value) {
    	  value = value.replace('<@', '').replace('>', '');
    	  var filtros = document.getElementsByTagName('select');
    	  for(var i = 0; i < filtros.length; i++) {
    	    var filtro = filtros[i];
    	    if(filtro.name == 'FILTRO' && !filtro.disabled) {
    	      var valor = filtro.options[filtro.selectedIndex].value;
    	      var papCodigo = valor.substring(0, valor.indexOf(';'));
    	      var tfrCodigo = valor.substring(valor.indexOf(';') + 1, valor.lastIndexOf(';'));
    	      var papFiltro = valor.substring(valor.lastIndexOf(';') + 1, valor.length);
    	      
    		  if (value == tfrCodigo && papFiltro != '<%=(String)CodedValues.REL_FILTRO_NAO_EXISTENTE%>') {
    			return true;
    		  }      
    	    }
    	  }
    	  
    	  return false;
    	}

    	$('input[type=radio][name=SRE_FONTE_DADOS]').change(function() {
    	    if (this.value == '<%=(String)CodedValues.TPC_SIM%>') {
    	    	EnabledisableFields(true);
    	    }
    	    else if (this.value == '<%=(String)CodedValues.TPC_NAO%>') {
					EnabledisableFields(false);
				}
			});

			function EnabledisableFields(toogle) {
				var form = document.forms[0];
				var elements = form.elements;
				for (var i = 0, len = elements.length; i < len; ++i) {
					if (elements[i].name != "SRE_FONTE_DADOS"
							&& elements[i].name != "SRE_NOME_PARAMETRO"
							&& elements[i].name != "sreCodigo"
							&& elements[i].id != "upload-btn-FILE1"
							&& elements[i].name != "input-FILE1"
							&& elements[i].name != "FILE1") {
						elements[i].disabled = toogle;
					}
				}
			}

			var f0 = document.forms[0];
		</script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>