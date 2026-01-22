<%--
* <p>Title: editarServidor_v4</p>
* <p>Description: Editar Servidor v4</p>
* <p>Copyright: Copyright (c) 2005</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>

<%@page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper"%>
<%@page import="com.zetra.econsig.helper.texto.LocaleHelper"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken"%>
<%@ page import="com.zetra.econsig.values.CodedValues"%>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper"%>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants"%>
<%@ page import="com.zetra.econsig.dto.entidade.MargemTO"%>
<%@ page import="java.util.List"%>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ page import="java.util.Map"%>
<%@ page import="com.zetra.econsig.helper.upload.UploadHelper" %>
<%@ page import="com.zetra.econsig.values.Columns"%>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.dto.entidade.RegistroServidorTO"%>
<%@ page import="com.zetra.econsig.dto.entidade.ServidorTransferObject"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ page import="com.zetra.econsig.helper.web.JspHelper"%>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<%
 AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
 String linkRet = (String) request.getAttribute("linkRet");
 String linkRetorno = (String) request.getAttribute("linkRet");
 String serCodigo = (String) request.getAttribute("serCodigo");
 ServidorTransferObject servidor = (ServidorTransferObject) request.getAttribute("servidor");
 
 boolean obrigInfTodosDadosSer = (boolean) request.getAttribute("obrigInfTodosDadosSer");
 boolean habilitaSaldoDevedorExclusaoServidor = (boolean) request.getAttribute("habilitaSaldoDevedorExclusaoServidor");
 boolean exigeDetalheExclusaoSer = (boolean) request.getAttribute("exigeDetalheExclusaoSer");
 boolean exigeMotivo = (boolean) request.getAttribute("exigeMotivo");
 
 String acao = (String) request.getAttribute("acao");
 String readOnly = (String) request.getAttribute("readOnly");
 String readOnlyRse = (String) request.getAttribute("readOnlyRse");
 
 String serNome = (String)servidor.getAttribute(Columns.SER_NOME);
 String serNomeCodificado = TextHelper.encode64(serNome);
 
 RegistroServidorTO rseSelecionado = (RegistroServidorTO) request.getAttribute("rseSelecionado");
 String rseCodigo = (String) request.getAttribute("rseCodigo");
 String rseMatricula = (String) rseSelecionado.getAttribute(Columns.RSE_MATRICULA);
 boolean exigeTermoUsoAlteracaoEmail = (responsavel.isCseSupOrg() && ParamSist.paramEquals(CodedValues.TPC_EXIGE_CONFIRMA_LEITURA_TERMO_MUDANCA_EMAIL, CodedValues.TPC_SIM, responsavel));
 String termoUsoEmail = (String) request.getAttribute("termoUsoEmail");
  
 // bloqueios de serviços por servidor
 Map bloqueioServico = (Map) request.getAttribute("bloqueioServico");
 // bloqueios de naturezas de serviço por servidor
 Map bloqueioNaturezaServico = (Map) request.getAttribute("bloqueioNaturezaServico");
 // bloqueios de convênios por servidor
 CustomTransferObject bloqueioServidor = (CustomTransferObject) request.getAttribute("bloqueioServidor");
 // Busca a lista de margens do servidor
 List<MargemTO> margens = (List) request.getAttribute("margens");
 // bloqueios de consignatárias por servidor
 Map bloqueioConsignataria = (Map) request.getAttribute("bloqueioConsignataria");
 
 // Recupera ações possíveis
 Map acoes = (Map) request.getAttribute("acoes");

 //Exibe Botao Rodapé
 boolean exibeBotaoRodape = (boolean) (request.getAttribute("exibeBotaoRodape"));
 boolean operacaoSensivel = (boolean) (request.getAttribute("operacaoSensivel"));
 String camposObrigatorios = (String) request.getAttribute("camposObrigatorios");
%>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="title">
   <%= request.getAttribute("tituloPagina") %>
</c:set>
<c:set var="bodyContent">
<%if (acoes != null && !acoes.isEmpty()) {%>
   <div class="page-title">
    <div class="row">
     <div class="col-sm mb-2">
      <div class="float-end">
       <button data-bs-toggle="dropdown" aria-haspopup="true" id="acoes" aria-expanded="false" class="btn btn-primary" type="submit"><hl:message key="rotulo.mais.acoes"/></button>
       <div class="dropdown-menu dropdown-menu-right" aria-labelledby="acoes">
        <c:forEach var="acao" items="${acoes}">
          <a class="dropdown-item" href="#no-back" onClick="acaoRse('<%=TextHelper.forJavaScriptAttribute(rseCodigo)%>', '<%=TextHelper.forJavaScriptAttribute(serCodigo)%>', '<%=TextHelper.forJavaScriptAttribute(rseMatricula)%>', '<%=TextHelper.forHtmlContent(serNomeCodificado)%>', '${acao.key}');">${acao.value}</a>
        </c:forEach>
       </div>
      </div>
     </div>
    </div>
   </div>
<% } %>
 <form method="post" action="../v3/editarServidor?acao=editar&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
   <div class="card">
     <div class="card-header">
        <h2 class="card-header-title"><hl:message key="rotulo.editar.grid"/></h2>
     </div>
     <div class="card-body">
        <jsp:include page="../editarServidor/include_campos_servidor_v4.jsp" />
         <%
             List<TransferObject> listaDadosAdicionaisServidor = (List) request.getAttribute("listaDadosAdicionaisServidor");
             Map<String,String> dadosAutorizacao = (Map<String,String>) request.getAttribute("dadosAutorizacao");
         %>
         <% if (listaDadosAdicionaisServidor != null) { %>
             <h3 class="legend"><span><hl:message key="rotulo.dados.adicionais"/></span></h3>
          <% for (TransferObject tda : listaDadosAdicionaisServidor) { %>
             <hl:paramv4
                 prefixo="TDA_"
                 descricao="<%=TextHelper.forHtmlAttribute(tda.getAttribute(Columns.TDA_DESCRICAO))%>"
                 codigo="<%=(String) tda.getAttribute(Columns.TDA_CODIGO)%>"
                 dominio="<%=(String) tda.getAttribute(Columns.TDA_DOMINIO)%>"
                 valor="<%= dadosAutorizacao != null && dadosAutorizacao.get((String) tda.getAttribute(Columns.TDA_CODIGO)) != null ? dadosAutorizacao.get((String) tda.getAttribute(Columns.TDA_CODIGO)) : "" %>"
                 desabilitado="<%= CodedValues.CAS_BLOQUEADO.equals(tda.getAttribute(Columns.SPT_EXIBE)) %>"
                 />
          <% } %>
        <% } %>
        <%-- Lista os registros do servidor --%>
        <% request.setAttribute("registroServidor", rseSelecionado); %>
        <jsp:include page="../editarServidor/include_campos_registro_servidor_v4.jsp"/>
        <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ANEXO_DOCUMENTO)%>">
          <div class="row">
             <div class="form-group col-sm-12">
              <hl:fileUploadV4 obrigatorio="<%=ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ANEXO_DOCUMENTO, responsavel) %>" multiplo="false" tituloCampoArquivo="<%=ApplicationResourcesHelper.getMessage("rotulo.servidor.anexo.documento", responsavel) %>" mostraCampoDescricao="false" extensoes="<%=UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_REGISTRO_SERVIDOR%>" tipoArquivo="anexo_registro_servidor"/>
             </div>
          </div>
        </show:showfield>
        <% if (exigeTermoUsoAlteracaoEmail) { %>
           <div id="checkTermoUso" style="display: none;">
           <p>
             <span class="info" style="display: block; width: 50%;">
                <input type="checkbox" name="verTermoUsoEmail" id="verTermoUsoEmail" value="S" />
                <hl:message key="rotulo.termo.de.uso.email.aceito"/> <a href="#" data-bs-toggle="modal" onClick="validaTermoDeUsoEmail()" ><hl:message key="rotulo.termo.de.uso.email.aceito.link"/></a>
             </span>
           </p>
           </div>
        <% } %>

        <hl:htmlinput name="RSE_CODIGO" type="hidden" value="<%=TextHelper.forHtmlAttribute(rseCodigo)%>" />
        <hl:htmlinput name="SER_CODIGO" type="hidden" value="<%=TextHelper.forHtmlAttribute(serCodigo)%>" />
        <hl:htmlinput name="linkRet"    type="hidden" value="<%=TextHelper.forHtmlAttribute(linkRetorno)%>" />
        <% if (exigeDetalheExclusaoSer) {%>
          <hl:htmlinput name="srsOriginal" type="hidden" value="<%=TextHelper.forHtmlAttribute((rseSelecionado != null && rseSelecionado.getSrsCodigo() != null ? rseSelecionado.getSrsCodigo() : ""))%>"/>
        <% } %>
     </div>
   </div>

   <div id="actions" class="btn-action">
     <a class="btn btn-outline-danger" href="#no-back" onClick="<%=(String)"postData('" + linkRet + "'); return false;"%>"><hl:message key="rotulo.botao.cancelar"/></a>
     <a class="btn btn-primary" href="#no-back" onClick="if (validaExigeMotivoForm() && vf_edita_servidor()) { submitNovoForm(); } return false;"><hl:message key="rotulo.botao.salvar"/></a>
   </div>
 </form>

 <!-- Modal: Termo de Uso E-mail -->
 <div class="modal fade" id="confirmarTermoUso" tabindex="-1" role="dialog" aria-labelledby="modalTitulo" aria-hidden="true" style="display: none;">
  <div class="modal-dialog modalTermoUso" role="document">
    <div class="modal-content">
      <div class="modal-header pb-0">
        <h5 class="modal-title about-title mb-0" id="modalTitulo"><hl:message key="rotulo.termo.de.uso.email.titulo"/></h5>
        <button type="button" class="logout mr-2" data-bs-dismiss="modal" aria-label="Fechar">
          <span aria-hidden="true">x</span>
        </button>
      </div>
      <div class="modal-body">
        <span id="termoUsoEmail">
          <%=termoUsoEmail%>
        </span>
      </div>
      <div class="modal-footer pt-0">
        <div class="btn-action mt-2 mb-0">
          <a class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.cancelar"/>' href="#" alt="<hl:message key="rotulo.botao.cancelar"/>" title="<hl:message key="rotulo.botao.cancelar"/>">
            <hl:message key="rotulo.botao.cancelar" />
          </a>
        </div>
      </div>
    </div>
  </div>
</div>
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
</c:set>
<c:set var="javascript">
<hl:fileUploadV4 multiplo="false" obrigatorio="<%=ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ANEXO_DOCUMENTO, responsavel)%>" mostraCampoDescricao="false" extensoes="<%=UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_REGISTRO_SERVIDOR%>" tipoArquivo="anexo_registro_servidor" scriptOnly="true"/>
   <script type="text/JavaScript">
   var f0 = document.forms['form1'];
   var emailChange = false;
   var emailValue = '<%= request.getAttribute("serEmail") %>';
   
   var valoresOriginais = {};
   
   var camposObrigatorios = '<%= !TextHelper.isNull(camposObrigatorios) ? camposObrigatorios : "" %>'
   
   function preencherValoresOriginais() {
	   Array.from(f0.elements).forEach(function(element) {
		   var radioVazio = false;
		   var chave = element.id !== null && element.id !== undefined ? element.id : element.name;
		   if(element.type == "radio" && element.checked){
			   chave = element.name;
		   } else if (element.type == "radio" && !element.checked && valoresOriginais[element.name] == undefined) {
			   chave = element.name;
			   radioVazio = true;
		   }
		   if(!radioVazio){
		       valoresOriginais[chave] = element.value;
		   } else {
		       valoresOriginais[chave] = "";
		   }
       });
   }
   
   function submitNovoForm(){
	   <% if (operacaoSensivel) {%>
		    var formData = new FormData();
	
		    Array.from(f0.elements).forEach(function(element) {
		    	if (element.value !== undefined && element.value !== '' &&
	    		    (((element.id !== null && element.id !== undefined && element.id !== '' && element.id !== "RSE_BANCOS" && element.id !== "FILE1") && valoresOriginais[element.id] !== undefined) && (element.value !== valoresOriginais[element.id]) && element.type != "radio" ||
	    		    ((element.name !== null && element.name !== undefined && element.name !== ''  && element.name !== "RSE_BANCOS"  && element.name !== "FILE1" && valoresOriginais[element.name] !== undefined) && (element.value !== valoresOriginais[element.name]) && element.type != "radio")
	    		    || element.type == "radio" && element.checked && (valoresOriginais[element.name] !== undefined && element.value !== valoresOriginais[element.name]) )) {
	    		    formData.append(element.name, element.value);
	    		    
	    		    var indicatorSpan = document.createElement("label");
	    	        indicatorSpan.textContent = '<%=ApplicationResourcesHelper.getMessage("rotulo.operacao.sensivel.campo.alterado", responsavel)%>';
	    	        element.insertAdjacentElement('beforebegin', indicatorSpan);
	    		}
		    });
		    
		    Array.from(f0.elements).forEach(function(element) {
		        var isCampoObrigatorio = element.id !== "" && element.name !== "" && (camposObrigatorios.includes(element.id) || camposObrigatorios.includes(element.name));
		        
		        if (isCampoObrigatorio && !formData.has(element.name)) {
		            formData.append(element.name, element.value);
		        }
		    });
	
		    var novoForm = document.createElement("form");
		    novoForm.method = "post";
		    novoForm.action = f0.action;
	
		    formData.forEach(function(value, key) {
		        var input = document.createElement("input");
		        input.type = "hidden";
		        input.name = key;
		        input.value = value;
		        novoForm.appendChild(input);
		    });
		    
		    Array.from(f0.elements).forEach(function(element) {
		        if (element.type === "hidden" && element.name != "FILE1") {
		            var input = document.createElement("input");
		            input.type = "hidden";
		            input.name = element.name;
		            input.value = element.value;
		            novoForm.appendChild(input);
		        }
		    });
		    <% if (exigeTermoUsoAlteracaoEmail) { %>
			    var verTermoUsoEmail = f0.querySelector("#verTermoUsoEmail");
			    if (verTermoUsoEmail) {
			        verTermoUsoEmail.parentNode.removeChild(verTermoUsoEmail);
			        
			        novoForm.appendChild(verTermoUsoEmail);
			    }
		    <% } %>
	
		    document.body.appendChild(novoForm);
		    novoForm.submit();
	   <%} else {%>
	   		f0.submit();
	   <%}%>
   }
   
   function validaTermoDeUsoEmail(){
       $('#confirmarTermoUso').modal('show');
   }
   function formLoad() {  
      focusFirstField();
      rseFormLoad();

      <% if (exigeTermoUsoAlteracaoEmail) { %>
      function emailEventHandler(event) {
        var emailNewValue = document.getElementById('<%=(String)( FieldKeysConstants.EDT_SERVIDOR_EMAIL )%>').value;
        var divCheckbox = $("#checkTermoUso");

        if (emailValue == emailNewValue) {
          divCheckbox.hide();
          emailChange = false;
        } else {
          divCheckbox.show();
          emailChange = true;
        }
      }


      <%
      if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_EMAIL_CONTRATOS_REJEITADOS_FOLHA, CodedValues.NOTIFICA_CONTRATOS_REJEITADOS_FOLHA_OPCIONAL, responsavel) && 
            ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_EMAIL_CONTRATOS_REJEITADOS, responsavel)) {
      %>
      
      function habilitaDesabilitaEmailContratosRejeitados (habilita) {
        if (!habilita) {
            $('[name=<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_EMAIL_CONTRATOS_REJEITADOS)%>]').attr("disabled", "disabled");
            $('#<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_EMAIL_CONTRATOS_REJEITADOS)%>_N').prop("checked", true);
        } else {
            $('[name=<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_EMAIL_CONTRATOS_REJEITADOS)%>]').removeAttr("disabled");
        }
      }

      <%
      }
      %>
      
      var emailField = f0.<%=(String)( FieldKeysConstants.EDT_SERVIDOR_EMAIL )%>;

      emailField.addEventListener("change", function (evt) {
        emailEventHandler(evt);
      }, false);

      emailField.addEventListener("paste", function (evt) {
        emailEventHandler(evt);
      }, false);

      emailField.addEventListener("keyup", function (evt) {
        emailEventHandler(evt);
      }, false);

      emailField.addEventListener("blur", function (evt) {
          emailEventHandler(evt);
          <%
          if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_EMAIL_CONTRATOS_REJEITADOS_FOLHA, CodedValues.NOTIFICA_CONTRATOS_REJEITADOS_FOLHA_OPCIONAL, responsavel) && 
              ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_EMAIL_CONTRATOS_REJEITADOS, responsavel)) {
          %>
          habilitaDesabilitaEmailContratosRejeitados(f0.<%=(String)FieldKeysConstants.EDT_SERVIDOR_EMAIL%>.value.trim() != '' && isEmailValid(f0.<%=(String)FieldKeysConstants.EDT_SERVIDOR_EMAIL%>.value));
          <%
          }
          %>
      }, false);  

      <%
      if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_EMAIL_CONTRATOS_REJEITADOS_FOLHA, CodedValues.NOTIFICA_CONTRATOS_REJEITADOS_FOLHA_OPCIONAL, responsavel) && 
          ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_EMAIL_CONTRATOS_REJEITADOS, responsavel)) {
      %>
      habilitaDesabilitaEmailContratosRejeitados(f0.<%=(String)FieldKeysConstants.EDT_SERVIDOR_EMAIL%>.value.trim() != '' && isEmailValid(f0.<%=(String)FieldKeysConstants.EDT_SERVIDOR_EMAIL%>.value));
      <%
      }
      %>

      <% 
      } 
      %>
    }

    function validaExigeMotivoForm() {
        var tmoCodigo = getElt('TMO_CODIGO');
        if (!tmoCodigo) {
            return true;  
        }
        // Se exige motivo, tem que validar a seleção de um motivo e o preenchimento ou não da observação; 
        if (<%=exigeMotivo%>) {
            if (tmoCodigo.value && confirmaAcaoConsignacao()) {
               return true;
            }
            //  Se exige motivo o motivo tem que estar preenchido, do contrário manda alerta para o usuário
            else if (!tmoCodigo.value) {
               alert('<hl:message key="mensagem.motivo.operacao.obrigatorio"/>');
               return false;
            }
        } else {
            // Se NÃO exige motivo, se o motivo não foi selecionado, pode dar submit
            if (!tmoCodigo.value) {
                return true;
            }
            //  porém se o motivo foi selecionado, tem que verificar se o motivo exige obs
            else if (tmoCodigo.value && confirmaAcaoConsignacao()) {
                return true;
            }
        }
    }

    // Verifica formularios de cadastro de servidores
    function vf_edita_servidor() {
    <% if (obrigInfTodosDadosSer) { %>
      var Controles = new Array("<%=(String)( FieldKeysConstants.EDT_SERVIDOR_PRIMEIRO_NOME)%>",
                                "<%=(String)( FieldKeysConstants.EDT_SERVIDOR_NOME)%>",
                                "<%=(String)( FieldKeysConstants.EDT_SERVIDOR_TITULACAO)%>",
                                "<%=(String)( FieldKeysConstants.EDT_SERVIDOR_NOME_MEIO)%>",
                                "<%=(String)( FieldKeysConstants.EDT_SERVIDOR_ULTIMO_NOME)%>", 
                                "<%=(String)( FieldKeysConstants.EDT_SERVIDOR_CPF)%>",
                                "<%=(String)( FieldKeysConstants.EDT_SERVIDOR_NOME_PAI)%>",
                                "<%=(String)( FieldKeysConstants.EDT_SERVIDOR_NOME_MAE)%>",
                                "<%=(String)( FieldKeysConstants.EDT_SERVIDOR_NOME_CONJUGE)%>",
                                "<%=(String)( FieldKeysConstants.EDT_SERVIDOR_SEXO)%>",
                                "<%=(String)( FieldKeysConstants.EDT_SERVIDOR_DATA_NASCIMENTO)%>",
                                "<%=(String)( FieldKeysConstants.EDT_SERVIDOR_UF_NASCIMENTO)%>",
                                "<%=(String)( FieldKeysConstants.EDT_SERVIDOR_CIDADE_NASCIMENTO)%>",
                                "<%=(String)( FieldKeysConstants.EDT_SERVIDOR_NO_IDENTIDADE)%>",
                                "<%=(String)( FieldKeysConstants.EDT_SERVIDOR_ESTADO_CIVIL)%>",
                                "<%=(String)( FieldKeysConstants.EDT_SERVIDOR_LOGRADOURO)%>",
                                "<%=(String)( FieldKeysConstants.EDT_SERVIDOR_BAIRRO)%>",
                                "<%=(String)( FieldKeysConstants.EDT_SERVIDOR_CIDADE)%>",
                                "<%=(String)( FieldKeysConstants.EDT_SERVIDOR_UF)%>",
                                "<%=(String)( FieldKeysConstants.EDT_SERVIDOR_CEP)%>",
                                "<%=(String)( FieldKeysConstants.EDT_SERVIDOR_DDD_TELEFONE)%>",
                                "<%=(String)( FieldKeysConstants.EDT_SERVIDOR_TELEFONE )%>",
                                "<%=(String)( FieldKeysConstants.EDT_SERVIDOR_DDD_CELULAR)%>",
                                "<%=(String)( FieldKeysConstants.EDT_SERVIDOR_CELULAR )%>",
                                "<%=(String)( FieldKeysConstants.EDT_SERVIDOR_QTD_FILHOS)%>",
                                "<%=(String)( FieldKeysConstants.EDT_SERVIDOR_NIVEL_ESCOLARIDADE)%>",
                                "<%=(String)( FieldKeysConstants.EDT_SERVIDOR_TIPO_HABITACAO)%>");
    
      var Msgs = new Array('<hl:message key="mensagem.informe.servidor.nome"/>',
                           '<hl:message key="mensagem.informe.servidor.nome"/>',
                           '<hl:message key="mensagem.informe.servidor.tratamento.nome"/>',
                           '<hl:message key="mensagem.informe.servidor.meio.nome"/>',
                           '<hl:message key="mensagem.informe.servidor.ultimo.nome"/>',
                           '<hl:message key="mensagem.informe.servidor.cpf"/>',
                           '<hl:message key="mensagem.informe.servidor.nome.pai"/>',
                           '<hl:message key="mensagem.informe.servidor.nome.mae"/>',
                           '<hl:message key="mensagem.informe.servidor.nome.conjuge"/>',
                           '<hl:message key="mensagem.informe.servidor.sexo"/>',
                           '<hl:message key="mensagem.informe.servidor.data.nascimento"/>',
                           '<hl:message key="mensagem.informe.servidor.uf.nascimento"/>',
                           '<hl:message key="mensagem.informe.servidor.cidade.nascimento"/>',
                           '<hl:message key="mensagem.informe.servidor.identidade"/>',
                           '<hl:message key="mensagem.informe.servidor.estado.civil"/>',
                           '<hl:message key="mensagem.informe.servidor.logradouro"/>',
                           '<hl:message key="mensagem.informe.servidor.bairro"/>',
                           '<hl:message key="mensagem.informe.servidor.cidade"/>',
                           '<hl:message key="mensagem.informe.servidor.estado"/>',
                           '<hl:message key="mensagem.informe.servidor.cep"/>',
                           '<hl:message key="mensagem.informe.servidor.ddd.telefone"/>',
                           '<hl:message key="mensagem.informe.servidor.telefone"/>',
                           '<hl:message key="mensagem.informe.servidor.ddd.celular"/>',
                           '<hl:message key="mensagem.informe.servidor.celular"/>',
                           '<hl:message key="mensagem.informe.servidor.quantidade.filhos"/>',
                           '<hl:message key="mensagem.informe.servidor.nivel.escolaridade"/>',
                           '<hl:message key="mensagem.informe.servidor.tipo.habitacao"/>');
    <% } else { %>
      var Controles = new Array("<%=(String)( FieldKeysConstants.EDT_SERVIDOR_NOME )%>",
                                "<%=(String)( FieldKeysConstants.EDT_SERVIDOR_CPF )%>");
      var Msgs = new Array('<hl:message key="mensagem.informe.servidor.nome"/>',
                           '<hl:message key="mensagem.informe.servidor.cpf"/>');
    <% } %>
    
    <% if (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_EMAIL, responsavel)) { %>
      var emailField = f0.<%=(String)( FieldKeysConstants.EDT_SERVIDOR_EMAIL )%>;
      if (emailField.value != null && emailField.value != '' && !isEmailValid(emailField.value)) {
        alert('<hl:message key="mensagem.erro.servidor.email.invalido"/>');
        emailField.focus();
        return false;
      }
    <% } %>

    <% if (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_DDD_TELEFONE, responsavel)) { %>
       var dddTelefoneField = f0.<%=(String)( FieldKeysConstants.EDT_SERVIDOR_DDD_TELEFONE )%>;
          if (dddTelefoneField.value == null && dddTelefoneField.value.trim() == '') {
            alert('<hl:message key="mensagem.erro.servidor.ddd.telefone.invalido"/>');
            telefoneField.focus();
            return false;
          }
  <% } %>
    
    <% if (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_TELEFONE, responsavel)) { %>
       var telefoneField = f0.<%=(String)( FieldKeysConstants.EDT_SERVIDOR_TELEFONE )%>;
          if (telefoneField.value != null && telefoneField.value != '' && telefoneField.value.length < '<%=LocaleHelper.getTelefoneSize()%>') {
            alert('<hl:message key="mensagem.erro.servidor.telefone.invalido"/>');
            telefoneField.focus();
            return false;
          }
    <% } %>
    
    <% if (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_DDD_CELULAR, responsavel)) { %>
       var dddCelularField = f0.<%=(String)( FieldKeysConstants.EDT_SERVIDOR_DDD_CELULAR )%>;
          if (dddCelularField.value == null && dddCelularField.value.trim() == '') {
            alert('<hl:message key="mensagem.erro.servidor.ddd.celular.invalido"/>');
            celularField.focus();
            return false;
          }
  <% } %>
    
    <% if (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_CELULAR, responsavel)) { %>
      var celularField = f0.<%=(String)( FieldKeysConstants.EDT_SERVIDOR_CELULAR )%>;
          if (celularField.value != null && celularField.value != '' && celularField.value.length < '<%=LocaleHelper.getCelularSize()%>') {
            alert('<hl:message key="mensagem.erro.servidor.celular.invalido"/>');
            celularField.focus();
            return false;
          }
    <% } %>

    <%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ANEXO_DOCUMENTO, responsavel)) {%>
        if (document.getElementById('FILE1').value == '') {
              alert('<hl:message key="mensagem.informe.registro.servidor.anexo.documento"/>');
              return false;
        }
    <%}%>

    <% if (exigeTermoUsoAlteracaoEmail) { %>
    var checkboxVer = document.getElementById("verTermoUsoEmail");
    if (!checkboxVer.checked) {
      var emailNewValue = document.getElementById('<%=(String)( FieldKeysConstants.EDT_SERVIDOR_EMAIL )%>').value;
      if (emailValue != emailNewValue) {
        alert('<hl:message key="rotulo.termo.de.uso.email.aceitar.alert"/>');
        return false;
      }
    }
    <% } %>
    
      if (!ValidaCampos(Controles, Msgs) || !validarDadosObrigatoriosServidor() || !enviar()) {
          return false;  
      }
      
      var emailNovo = document.getElementById('<%=(String)( FieldKeysConstants.EDT_SERVIDOR_EMAIL )%>');
      if (emailNovo != null && emailValue != emailNovo.value) {
        <%if(!responsavel.isSup() && !TextHelper.isNull(rseSelecionado.getUniCodigo()) && !responsavel.temPermissaoEdtUnidade(rseSelecionado.getUniCodigo())){ %>
            alert('<hl:message key="mensagem.informe.registro.servidor.permissao.edt.email"/>');
            return false;
        <%}%>
      }
      
      enableAll();
      return true;
    }
   
     function acaoRse(rseCodigo, serCodigo, rseMatricula, serNome, valorCombo) {
         var acao = valorCombo;
         if (acao == 'listarOcorrencias') {
            postData('../v3/listarOcorrenciaServidor?acao=iniciar&RSE_CODIGO=' + rseCodigo + '&SER_CODIGO=' + serCodigo + '&<%=SynchronizerToken.generateToken4URL(request)%>');
         } else if (acao == 'bloqServicos' || acao == 'consultarServicos') {
            postData('../v3/listarServicoServidor?acao=iniciar&RSE_CODIGO=' + rseCodigo + '&RSE_MATRICULA=' + rseMatricula + '&SER_NOME=' + serNome + '&<%=SynchronizerToken.generateToken4URL(request)%>');
         } else if (acao == 'bloqNatServicos' || acao == 'consultarNatServicos') {
            postData('../v3/listarNaturezaServicoServidor?acao=iniciar&RSE_CODIGO=' + rseCodigo + '&RSE_MATRICULA=' + rseMatricula + '&SER_NOME=' + serNome + '&<%=SynchronizerToken.generateToken4URL(request)%>');
         } else if (acao == 'bloqConvenios' || acao == 'consultarConvenios') {
            postData('../v3/listarConvenioServidor?acao=iniciar&RSE_CODIGO=' + rseCodigo + '&RSE_MATRICULA=' + rseMatricula + '&SER_NOME=' + serNome + '&<%=SynchronizerToken.generateToken4URL(request)%>');
         } else if (acao == 'transferirMargem') {
            postData('../v3/transferirMargemServidor?acao=iniciar&RSE_CODIGO=' + rseCodigo + '&RSE_MATRICULA=' + rseMatricula + '&SER_NOME=' + serNome + '&<%=SynchronizerToken.generateToken4URL(request)%>');
         } else if (acao == 'consContraCheques') {
            postData('../v3/listarContrachequeServidor?acao=iniciar&RSE_CODIGO=' + rseCodigo + '&RSE_MATRICULA=' + rseMatricula + '&SER_NOME=' + serNome + '&<%=SynchronizerToken.generateToken4URL(request)%>');
         } else if (acao == 'listarEndereco') {
          postData('../v3/editarServidor?acao=listarEndereco&SER_CODIGO=' + serCodigo + '&<%=SynchronizerToken.generateToken4URL(request)%>');
         } else if (acao == 'solicitarSaldoDevedor') {
            if (confirm('<hl:message key="mensagem.confirmacao.solicitar.saldo.devedor.exclusao.ser"/>')) {
                postData('../v3/solicitarSaldoDevedor?acao=solicitar_saldo_exclusao_ser&tipo=consultar&RSE_CODIGO=' + rseCodigo + '&SER_CODIGO=' + serCodigo + '&SER_NOME=' + serNome + '&<%=SynchronizerToken.generateToken4URL(request)%>');
            }
         } else if (acao == 'sobrepoeRse') {
             postData('../v3/listarServicoServidor?acao=listarSvcSerSobrepoeParam&RSE_CODIGO=' + rseCodigo + '&RSE_MATRICULA=' + rseMatricula + '&SER_NOME=' + serNome + '&<%=SynchronizerToken.generateToken4URL(request)%>');
         } else if (acao == 'consultarServidor'){
             postData('../v3/listarUsuarioServidor?acao=pesquisarServidor&RSE_MATRICULA=' + rseMatricula + '&<%=SynchronizerToken.generateToken4URL(request)%>');
         } else if (acao == 'cadastrarDispensaValidacaoDigitalServidor') {
             postData('../v3/cadastrarDispensaValidacaoDigitalServidor?acao=iniciar&RSE_CODIGO=' + rseCodigo + '&SER_CODIGO=' + serCodigo + '&<%=SynchronizerToken.generateToken4URL(request)%>');
         } else if (acao == 'bloqConsignatarias') {
             postData('../v3/listarConsignatariaServidor?acao=iniciar&RSE_CODIGO=' + rseCodigo + '&SER_CODIGO=' + serCodigo + '&RSE_MATRICULA=' + rseMatricula + '&SER_NOME=' + serNome + '&<%=SynchronizerToken.generateToken4URL(request)%>');
         } else if (acao == 'consultarAnexoRse') {
             postData('../v3/editarAnexosRegistroServidor?acao=iniciar&RSE_CODIGO=' + rseCodigo + '&<%=SynchronizerToken.generateToken4URL(request)%>');
         } else if (acao == 'editarAnexoRse') {
             postData('../v3/editarAnexosRegistroServidor?acao=editar&RSE_CODIGO=' + rseCodigo + '&<%=SynchronizerToken.generateToken4URL(request)%>');
         } else if (acao == 'editarCompMargem') {
             postData('../v3/manterComposicaoMargemServidor?acao=listar&RSE_CODIGO=' + rseCodigo + '&<%=SynchronizerToken.generateToken4URL(request)%>');
         } else if  (acao == 'anexarFoto'){
             postData('../v3/uploadArquivoServidor?acao=iniciar&RSE_CODIGO=' + rseCodigo + '&<%=SynchronizerToken.generateToken4URL(request)%>');
         } else if (acao == 'editarFuncoesEnvioEmail') {
             postData('../v3/editarFuncoesEnvioEmailSer?acao=iniciar&SER_CODIGO=' + serCodigo + '&<%=SynchronizerToken.generateToken4URL(request)%>');
         } else if (acao == 'ocultarRegistroSerCsa') {
            postData('../v3/ocultarRegistroSerCsa?acao=iniciar&RSE_CODIGO=' + rseCodigo + '&<%=SynchronizerToken.generateToken4URL(request)%>');
         } else {
            postData("../v3/consultarServidor?acao=consultar&RSE_CODIGO=" + acao + '&SER_CODIGO=' + serCodigo + '&<%=SynchronizerToken.generateToken4URL(request)%>');
         }
     }
     window.onload = formLoad;
   </script>
	<script>
		let btnDown = document.querySelector('#btns');
		const pageActions = document.querySelector('#page-actions');
		const pageSize = document.body.scrollHeight;
		
		function up(){
			window.scrollTo({
				top: 0,
				behavior: "smooth",
			});
		}
		
		function down(){
			let toDown = document.body.scrollHeight;
			window.scrollBy({
				top: toDown,
				behavior: "smooth",
			});
		}
	
		function toActionBtns(){
			let save = document.querySelector('#actions').getBoundingClientRect().top;
			window.scrollBy({
				top: save,
				behavior: "smooth",
			});
		}
		
		function btnTab(){
		    let scrollSize = document.documentElement.scrollTop;
		    
		    if(scrollSize >= 300){
			    btnDown.classList.add('btns-active');    
		    } else {
			    btnDown.classList.remove('btns-active');
		    }
		}
		
	
		window.addEventListener('scroll', btnTab);
		
		document.addEventListener("DOMContentLoaded", function() {
			preencherValoresOriginais();
		});
	</script>
</c:set>
<%-- Leiaute Fixo --%>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4> 