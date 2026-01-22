<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
    String adeCodigo = (String) request.getAttribute("adeCodigo");
int offset = ((Integer) request.getAttribute("offset")).intValue();
String acaoFormulario = (String) request.getAttribute("acaoFormulario");
AcessoSistema responsavel = (AcessoSistema) request.getAttribute("responsavel");
CustomTransferObject autdes = (CustomTransferObject) request.getAttribute("autdes");
boolean exigeAssinaturaDigital = (Boolean) request.getAttribute("exigeAssinaturaDigital");
boolean anexoObrigatorio = (Boolean) request.getAttribute("anexoObrigatorio");
%>
<c:set var="title">
  <hl:message key="rotulo.editar.anexo.consignacao.titulo" />
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
<FORM NAME="form1" METHOD="POST" ACTION="<%=SynchronizerToken.updateTokenInURL(acaoFormulario + "?acao=upload&_skip_history_=true&ADE_CODIGO=" + adeCodigo, request)%>" ENCTYPE="multipart/form-data">
  <% out.print(SynchronizerToken.generateHtmlToken(request)); %>
    <div class="row">
      <div class="col-sm-12 col-md-6">
        <%-- Utiliza a tag library DetalheConsignacaoTag.java para exibir os dados da ADE --%>
          <% pageContext.setAttribute("autdes", autdes); %>
        <hl:detalharADEv4 name="autdes" table="false" type="consultar" />
        <%-- Fim dos dados da ADE --%>
      </div>
      <div class="col-sm-12 col-md-6">
        <div class="card">
          <div class="card-header">
            <h2 class="card-header-title"><hl:message key="rotulo.anexo.arquivo.titulo"/></h2>
          </div>
          <div class="card-body">
            <% if (exigeAssinaturaDigital) { %>
            <div class="row">
            	<div class="col-sm-12">
		            <div class="alert alert-warning" role="alert">
		                <hl:message key="mensagem.confirmacao.instrucao.anexo.assinatura.digital" />
		            </div>
            	</div>
            </div>
              <hl:fileUploadV4 obrigatorio="<%=true%>" tipoArquivo="anexo_solicitacao"/>

            <% } else if (responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO)) {%>
              <hl:fileUploadV4 obrigatorio="<%=true%>" tipoArquivo="anexo_consignacao"/>
            <% } %>

            <% if (request.getAttribute("lstPeriodos") != null) { %>
            <div class="row">
              <div class="form-group col-sm-12">
                    <label for="ocaPeriodo"><hl:message key="rotulo.folha.periodo"/></label>
                    <select name="ocaPeriodo" id="ocaPeriodo" class="form-control" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascara(this);">
                      <% for (Date periodo : (Set<Date>) request.getAttribute("lstPeriodos")) { %>
                        <option value="<%=TextHelper.forHtmlAttribute(periodo)%>"><%=TextHelper.forHtmlContent(DateHelper.toPeriodString(periodo))%></option>
                      <% } %>
                </select>
              </div>
            </div>
             <% } %>
          </div>
        </div>
        <div class="btn-action">
          <a class="btn btn-primary" href="#" onclick="if(vf_upload_arquivos()){document.form1.submit();} return false;"><hl:message key="rotulo.botao.confirmar" /></a>
        </div>
      </div>
    </div>
    <input name="FORM" type="hidden" value="form1">
  </form>
  <%
      List<?> anexos = (List<?>) request.getAttribute("anexos");
  %>
  <div class="row">
    <div class="col-sm-12 col-md-12 mb-2">
      <div class="card">
        <div class="card-header hasIcon pl-3">
              <h2 class="card-header-title"><hl:message key="rotulo.anexo.disponivel.plural.titulo"/></h2>
        </div>
        <div class="card-body table-responsive">
          <%-- Utiliza a tag library ListaAnexosContratoTag.java para listar os anexos do contrato --%>
             <% pageContext.setAttribute("anexos", anexos); %>
             <hl:listaAnexosContratov4 name="anexos" table="true" type="alterar" temAcao="true"/>
          <%-- Fim dos anexos do contrato --%>
        </div>
				<div class="card-footer">
        	<%@ include file="../paginador/incluirBarraPaginacao_v4.jsp"%>
				</div>
      </div>
      <div class="btn-action">
            <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.voltar"/></a>
            <%if(((!responsavel.isSer() && responsavel.temPermissao(CodedValues.FUN_EXCLUIR_ANEXOS_CONSIGNACAO)) || (responsavel.isSer() && (responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_SOLICITACAO) || responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO))))) {%>
              <button class="btn btn-outline-danger removerMUltiplosAnexos" href="#no-back" onClick="excluir_anexos_selecionados()"><hl:message key="rotulo.botao.remover"/></button>
            <%} %>
      </div>
    </div>
  </div>

      <div class="modal fade" id="confirmarMensagem" tabindex="-1" role="dialog" aria-labelledby="modalTitulo" aria-hidden="true" style="display: none;">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title about-title mb-0" id="modalTitulo"><hl:message key="rotulo.anexo.descricao"/></h5>
          <button type="button" class="logout mr-3" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
            <span aria-hidden="true"></span>
          </button>
        </div>
        <div class="form-group modal-body m-0">
              <label for="editfield"><hl:message key="mensagem.informe.anexo.consignacao.descricao"/></label>
              <textarea class="form-control" id="editfield" name="editfield" rows="3" cols="28"></textarea>

        </div>
        <div class="modal-footer pt-0">
          <div class="btn-action mt-2 mb-0">
            <a class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.cancelar"/>' href="#"><hl:message key="rotulo.botao.cancelar" /></a>
            <input hidden="true" id="aad_nome" value="">
            <input hidden="true" id="aad_descricao" value="">
            <input hidden="true" id="posicao" value="0">
            <a class="btn btn-primary" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.confirmar"/>' onclick="show_setDescricao();" href="#"><hl:message key="rotulo.botao.confirmar" /></a>
          </div>
        </div>
      </div>
    </div>
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

  <c:set var="javascript">
    <link rel="stylesheet" href="../css/impromptu.css?<hl:message key="release.tag"/>" type="text/css">
    <hl:fileUploadV4 botaoVisualizarRemover="<%=true%>" scriptOnly="true" tipoArquivo="<%=exigeAssinaturaDigital ? "anexo_solicitacao" : "anexo_consignacao"%>" />
    <script type="text/JavaScript">
       var f0 = document.form1; 
       
       var aad_lista = []
       var aad_nome = "";
       var aadNomes = "";
       var index = 0;
       var i = 0;
       
       function vf_upload_arquivos() {
           var arquivo = tratarArquivo();
           var descricao = document.getElementById("AAD_DESCRICAO").value;
           if ((arquivo == null) || (trim(arquivo) == "") || (arquivo.toUpperCase() == "NULL")) {
               alert('<hl:message key="mensagem.editar.anexo.consignacao.selecione.arquivo"/>');
               return false;
           } else if ((descricao == null) || (trim(descricao) == "") || (descricao.toUpperCase() == "NULL")) {
               alert('<hl:message key="mensagem.informe.anexo.consignacao.descricao"/>');
               return false;
           } else if (descricao.length > 255) {
               alert('<hl:message key="mensagem.editar.anexo.consignacao.descricao.maxima"/>');
               return false;
           } else {
           	  $('#modalAguarde').modal({
           	    backdrop: 'static',
           	    keyboard: false
           	  });
         	 	document.getElementById("FILE1").value = arquivo;
                 return true;    	  
           }
       }

       function tratarArquivo(){
     	  var arquivo = document.getElementById("FILE1").value;
     	  var nomes = arquivo.split(';');
     	  var nomesArquivosFinal = "";
     	  for(var i = 0; i < nomes.length; i++) {
         	  // Faz o tratamento caso o usuario tenha removido algum arquivo temporario para upload
         	  if(nomes[i] != "removido") {
           		if (nomesArquivosFinal == "") {
           			nomesArquivosFinal = nomes[i];
           		} else {
           			nomesArquivosFinal += ';' + nomes[i];
         		}  
         	  }
     	  }
     		  return nomesArquivosFinal;
       }

       function show_setDescricao(){
     	  	urlSolicitacao = '<%=SynchronizerToken.updateTokenInURL(
     		acaoFormulario + "?acao=descrever&ADE_CODIGO=" + adeCodigo + "&_skip_history_=true&offset=" + offset, request)%>';
     	    urlSolicitacao = urlSolicitacao + '&NOME_ARQ='+ $("#aad_nome").val();
     	    postData(urlSolicitacao + '&DESCRICAO=' + $('#editfield').val());
       }

       function show_descricao(aad_nome, aad_descricao) {
        	$('#confirmarMensagem').modal('show');
     	$("#aad_nome").val(aad_nome);
     	$("#aad_descricao").val(aad_descricao);
     	$('#editfield').val(aad_descricao);
       }  

       function downloadAnexoVisualizacao(posicao) {
     	  let nomeArquivo = $("#FILE1").val();
     	  var nomes = nomeArquivo.split(';');
     	  nomeArquivo = nomes[posicao];
           postData("../v3/downloadArquivo?arquivo_nome=" + nomeArquivo + "&tipo=anexo_consignacao_temp&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>");
       }

       function removeAnexoVisualizacao(posicao) {
     	  let nomeArquivo = $("#FILE1").val();
     	  var nomes = nomeArquivo.split(';');
     	  nomeArquivo = nomes[posicao];
     	  var novaFILE1 = "";
     	  for (var i = 0; i < nomes.length; i++) {
     		  if(i != posicao) {
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
     
     	  $.post("../v3/editarAnexosConsignacao?acao=excluirAnexoTemp&NOME_ARQ="+ nomeArquivo +"&ADE_CODIGO=<%=TextHelper.forJavaScriptBlock(adeCodigo)%>&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>", function(data) {
     			  
     	       try {
     	    	   // Busca a div do arquivo que foi removido para nao exibir na lista abaixo do botao para anexar documento.
             	   var divArquivoRemovido = document.getElementById(nomeArquivo);
     	    	   if (divArquivoRemovido != null) {
     	        	   divArquivoRemovido.parentNode.removeChild(divArquivoRemovido);
     	    	   }
     	    	           	   
             	   // Se tiver uma mensagem de sessao de sucesso, apaga ela para nao confundir o usuario
             	   var divAlertSucess = document.getElementsByClassName('alert alert-success');
             	   if(divAlertSucess.length > 0) {
             	   		divAlertSucess[0].parentNode.removeChild(divAlertSucess[0]);
             	   }
             	   
             	   // Caso nao tenha mais arquivos para upload ele oculta a barra azul, para melhorar experiencia do usuario.
             	   if(document.getElementById("pic-progress-wrap-FILE1") != null && arquivosRemovidos(novaFILE1)) {
             		   document.getElementById("pic-progress-wrap-FILE1").style.display = 'none';
             	   }
     
             	   alert('<hl:message key="rotulo.anexo.removido"/>');
       	        } catch(err) {
       	     	   console.log(err);
       	        } 
     	  	}, "json");
     	}

       function verificarDownload(adeCodigo, aadNome, dataReserva) {
           $.post("../v3/verificarAnexoContratoConsignacao?arquivo_nome=" + aadNome + "&tipo=anexo&entidade=" + adeCodigo + "&data=" + dataReserva + "&_skip_history_=true", function(data) {
               try {
               	var jsonResult = $.trim(JSON.stringify(data));
                   var obj = JSON.parse(jsonResult);
                   var statusArquivo = obj.statusArquivo;
           
                   if (statusArquivo) {
                   	postData("../v3/downloadAnexoContratoConsignacao?arquivo_nome=" + aadNome + "&tipo=anexo&entidade=" + adeCodigo + "&data=" + dataReserva + "&_skip_history_=true");
                   } else {
                   	postData("../v3/editarAnexosConsignacao?acao=exibir&ADE_CODIGO=" + adeCodigo + "&<%out.print(SynchronizerToken.generateToken4URL(request));%>");
                   }
                } catch (err) {
                }
           }, "json");
       }

       function arquivosRemovidos(fileValores) {
     	  var todosRemovidos = true;
     	  var nomes = fileValores.split(';');
     	  for(var i=0; i<nomes.length; i++)
     		  if(nomes[i] != "removido") {
     			  todosRemovidos = false;
     		  }
     	  
     	  return todosRemovidos;
       }
       
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
      
      function escolhechk(aadNome,idchk,e) {
    	aad_nome = aadNome;
    	$(e).parents('tr').find('input[type=checkbox]').click();
      }
      
      $("table tbody tr input[type=checkbox]").click(function (e) {
      	verificarCheckbox();
      	var checked = e.target.checked;
      	if (checked) {
      		$(e.target).parents('tr').addClass("table-checked");
      		aadNomes += aad_nome + ",";
      	} else {
      		$(e.target).parents('tr').removeClass("table-checked");
      		index = aadNomes.indexOf(aad_nome);
      		if(index > -1) {
      			aadNomes = aadNomes.replace(aad_nome + ",", "");
      		}
      	}
      });
      
   	 $("input[id*=checkAll_").click(function (e){
  		var checked = e.target.checked;
  		$('table tbody tr input[type=checkbox]').prop('checked', checked);
  		if (checked) {
  			for(var i = 0; i < document.getElementsByName("chkAadNome").length; i++){
  				if(aadNomes.indexOf(document.getElementsByName("chkAadNome")[i].value + ",") == -1){
  					aadNomes += document.getElementsByName("chkAadNome")[i].value + ",";
  				}
  			}
  			$("table tbody tr").addClass("table-checked");
  		} else {
  			aadNomes = "";
  			$("table tbody tr").removeClass("table-checked");
  		}
  		verificarCheckbox();
  	});
   	 
   	function excluir_anexo(aad_nome) {    
        var url = '<%=SynchronizerToken
    		.updateTokenInURL(acaoFormulario + "?acao=excluir&_skip_history_=true&ADE_CODIGO=" + adeCodigo, request)%>';
        url = url + '&NOME_ARQ=' + aad_nome;
    
        var msgAnexo = '<hl:message key="mensagem.confirmacao.exclusao.anexo.consignacao"/>';
        return ConfirmaUrl(msgAnexo.replace("{0}", aad_nome), url);           
      }
   	
   	function excluir_anexos_selecionados() {
        var url = '<%=SynchronizerToken
    		.updateTokenInURL(acaoFormulario + "?acao=excluir&_skip_history_=true&ADE_CODIGO=" + adeCodigo, request)%>';
        url = url + '&AAD_NOMES=' + aadNomes;
    
        var msgAnexo = '<hl:message key="mensagem.confirmacao.exclusao.anexos.selecionados"/>'; 
        if(aadNomes != ""){
        	return ConfirmaUrl(msgAnexo.replace("{0}", aad_nome), url);
        }
   	}
  	</script>
  </c:set>
</c:set>

<t:page_v4>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
