<%--
* <p>Title: alterarBeneficio_v4</p>
* <p>Description: Listar benefícios v4</p>
* <p>Copyright: Copyright (c) 2005</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: larissa.silva $
* $Revision: 24740 $
* $Date: 2018-06-26 15:48:25 -0300 (Ter, 26 jun 2018) $
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.zetra.econsig.persistence.entity.Beneficio"%>
<%@ page import="com.zetra.econsig.persistence.entity.BeneficioServico"%>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
  Boolean podeEditar = (Boolean) request.getAttribute("podeEditar");
  Boolean novo = (Boolean) request.getAttribute("novo");
  Beneficio beneficio = (Beneficio) request.getAttribute("beneficio");
  List<TransferObject> operadorasBeneficios = (List) request.getAttribute("operadoras");
  List<TransferObject> naturezas = (List) request.getAttribute("naturezas");
  List<TransferObject> servicos = (List) request.getAttribute("servicos");
  List<BeneficioServico> beneficioServico = (List) request.getAttribute("beneficioServico");
  List<TransferObject> tipoBeneficiario = (List) request.getAttribute("tipoBeneficiario");
%>
<c:set var="javascript">
  <script type="text/JavaScript">
			function formLoad() {
				f0 = document.forms[0];
			}
			function verificaCampos() {
				var controles = new Array("<%=Columns.CSA_CODIGO%>", "<%=Columns.NSE_CODIGO%>",
						"<%=Columns.getColumnName(Columns.BEN_DESCRICAO)%>", "<%=Columns.getColumnName(Columns.BEN_CODIGO_PLANO)%>", "<%=Columns.getColumnName(Columns.BEN_CODIGO_REGISTRO)%>",
						"<%=Columns.getColumnName(Columns.BEN_CODIGO_CONTRATO)%>");
				var msgs = new Array(
						"<hl:message key='mensagem.beneficio.operadora.informar'/>",
						"<hl:message key='mensagem.beneficio.natureza.informar'/>",
						"<hl:message key='mensagem.beneficio.descricao.informar'/>",
						"<hl:message key='mensagem.beneficio.codigo.plano.informar'/>",
						"<hl:message key='mensagem.beneficio.codigo.registro.informar'/>",
						"<hl:message key='mensagem.beneficio.codigo.contrato.informar'/>");

				if (!ValidaCampos(controles, msgs)) {
					return false;
				}

        f0.submit();
				return false;
			}
			window.onload = formLoad;
	</script>
</c:set>
<c:set var="title">
<% if(novo) { %>
  <hl:message key="rotulo.beneficio.inclusao.beneficio.minusculo"/>
<% } else if(podeEditar) { %>
  <hl:message key="rotulo.beneficio.edicao.minusculo"/>
<% }else{ %>
  <hl:message key="rotulo.beneficio.visualizar.minusculo"/>
<% } %>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-beneficios"></use>
</c:set>
<c:set var="bodyContent"> 
  <form method="post" action="../v3/alterarBeneficio?<%=SynchronizerToken.generateToken4URL(request)%>" name="form1" id="form1">
    <div class="col-sm">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title">
            <% if(novo) { %>
              <hl:message key="rotulo.beneficio.inclusao.beneficio.minusculo"/>
            <% } else if(podeEditar) { %>
              <hl:message key="rotulo.beneficio.edicao.minusculo"/>
            <% } else { %>
              <hl:message key="rotulo.beneficio.visualizar.minusculo"/>
            <%}%>
          </h2>
        </div>
        <div class="card-body">
          <input class="Edit" type="hidden" name="<%=Columns.getColumnName(Columns.BEN_CODIGO)%>" value="<%= !novo ? TextHelper.forHtmlAttribute(beneficio.getBenCodigo()) : ""%>" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
          <div class="row">
            <div class="form-group col-sm">
              <label id="lblOperadoraBeneficio" for="lblOperadoraBeneficio"><hl:message key="rotulo.beneficio.operadora.singular"/></label>
              <% if(podeEditar){%>
                <%=JspHelper.geraCombo(operadorasBeneficios, Columns.CSA_CODIGO, Columns.CSA_CODIGO, Columns.CSA_NOME + ";" + Columns.CSA_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione.operadora", responsavel), null, false, 1, !novo ? beneficio.getConsignataria().getCsaCodigo() : "", null, false, "form-control")%>
              <%} else {%>
                <%=JspHelper.geraCombo(operadorasBeneficios, Columns.CSA_CODIGO, Columns.CSA_CODIGO, Columns.CSA_NOME + ";" + Columns.CSA_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione.operadora", responsavel), null, false, 1, beneficio.getConsignataria().getCsaCodigo(), null, true, "form-control")%>
              <% } %>
            </div>
            <div class="form-group col-sm">
              <label id="lblNaturezaServico" for="lblNaturezaServico"><hl:message key="rotulo.beneficio.natureza"/></label>
              <% if(podeEditar){%>
                <%=JspHelper.geraCombo(naturezas, Columns.NSE_CODIGO, Columns.NSE_CODIGO, Columns.NSE_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione.natureza", responsavel), null, false, 1, !novo ? beneficio.getNaturezaServico().getNseCodigo() : "", "buscarServicos(this.value)", false, "form-control")%>
              <%} else {%>
                <%=JspHelper.geraCombo(naturezas, Columns.NSE_CODIGO, Columns.NSE_CODIGO, Columns.NSE_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione.natureza", responsavel), null, false, 1, beneficio.getNaturezaServico().getNseCodigo(), null, true, "form-control")%>
              <% } %>
            </div>
            <div class="form-group col-sm">
              <label for="<%=Columns.BEN_DESCRICAO%>"><hl:message key="rotulo.beneficio.descricao"/></label>
              <INPUT class="Edit form-control" TYPE="text" NAME="<%=Columns.getColumnName(Columns.BEN_DESCRICAO)%>" VALUE="<%= !novo ? TextHelper.forHtmlAttribute(beneficio.getBenDescricao()) : ""%>" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" <%=(String)( !podeEditar ? "disabled" : "")%>>
            </div>
          </div>
          <div class="row">
            <div class="form-group col-sm">
              <label for="<%=Columns.BEN_CODIGO_PLANO%>"><hl:message key="rotulo.beneficio.codigo.plano"/></label>
              <INPUT class="Edit form-control" TYPE="text" NAME="<%=Columns.getColumnName(Columns.BEN_CODIGO_PLANO)%>" VALUE="<%= !novo ? TextHelper.forHtmlAttribute(beneficio.getBenCodigoPlano()) : ""%>" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" <%=(String)( !podeEditar ? "disabled" : "")%>>
            </div>
              <div class="form-group col-sm">
              <label for="<%=Columns.BEN_CODIGO_REGISTRO%>"><hl:message key="rotulo.beneficio.codigo.registro"/></label>
            <INPUT class="Edit form-control" TYPE="text" NAME="<%=Columns.getColumnName(Columns.BEN_CODIGO_REGISTRO)%>" VALUE="<%= !novo ? TextHelper.forHtmlAttribute(beneficio.getBenCodigoRegistro()) : ""%>" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" <%=(String)( !podeEditar ? "disabled" : "")%>>
            </div>
            <div class="form-group col-sm">
              <label for="<%=Columns.BEN_CODIGO_CONTRATO%>"><hl:message key="rotulo.beneficio.codigo.contrato"/></label>
              <INPUT class="Edit form-control" TYPE="text" NAME="<%=Columns.getColumnName(Columns.BEN_CODIGO_CONTRATO)%>" VALUE="<%= !novo ? TextHelper.forHtmlAttribute(beneficio.getBenCodigoContrato()) : ""%>" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" <%=(String)( !podeEditar ? "disabled" : "")%>>
            </div>
          </div>
          
          <div class="row">
            <div class="form-group col-sm-4">
              <label id="lblTipoBeneficiario" for="lblTipoBeneficiario"><hl:message key="rotulo.beneficio.tipo.beneficiario"/></label>
              <% if(podeEditar){%>
                <%=JspHelper.geraCombo(tipoBeneficiario, Columns.TIB_CODIGO, Columns.TIB_CODIGO, Columns.TIB_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione.tipo.beneficiario", responsavel), null, false, 1, "", "disableSelectServico(this.value)", false, "form-control")%>
              <%} else {%>
                <%=JspHelper.geraCombo(tipoBeneficiario, Columns.TIB_CODIGO, Columns.TIB_CODIGO, Columns.TIB_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione.tipo.beneficiario", responsavel), null, false, 1, "", null, true, "form-control")%>
              <% } %>
            </div>
            
            <div class="form-group col-sm-8">
                <label id="lblServicoBeneficio" for="lblServicoBeneficio"><hl:message key="rotulo.beneficio.servicos"/></label>
                <% if(podeEditar){%>
                  <%=JspHelper.geraCombo(servicos, Columns.SVC_CODIGO, Columns.SVC_CODIGO, Columns.SVC_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione.servicos", responsavel), null, false, 1, "", "inserirServico(this)", true, "form-control")%>
                <%} else {%>
                  <%=JspHelper.geraCombo(servicos, Columns.SVC_CODIGO, Columns.SVC_CODIGO, Columns.SVC_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione.servicos", responsavel), null, false, 1, "", "", true, "form-control")%>
                <% } %>
              </div>
          </div>
          <div class="row">
            <div class="form-group col-sm-12">
                <ul id="sortable" style="width: 85%; padding-left: 10px; margin-left: auto; margin-right: auto;">
         <%       
            if(!novo && beneficioServico != null && !beneficioServico.isEmpty()){
               for(BeneficioServico benSvc : beneficioServico){
                   %> 
                    <li class="form-control" value="<%=benSvc.getServico().getSvcCodigo()%>;<%=benSvc.getTipoBeneficiario().getTibCodigo()%>" id="<%=benSvc.getServico().getSvcCodigo()%>;<%=benSvc.getTipoBeneficiario().getTibCodigo()%>"><strong><%=benSvc.getBseOrdem()%> - </strong><span style="font-weight: normal;"><%=benSvc.getServico().getSvcDescricao()%></span><strong>&nbsp;&nbsp;&nbsp;(<%=benSvc.getTipoBeneficiario().getTibDescricao()%>)</strong><button type="button" class="btn btn-secondary" onclick="remove(this)" style="float: right; height: 20px; width: 20px; padding: 0px; visibility: hidden;">X</button></li>
                 <%
               }
            } 
         %>
                </ul>
            </div>
          </div>
        </div>
      </div>
      <div class="btn-action">
        <a href="#no-back" name="Button" class="btn btn-outline-danger" onClick="postData('../v3/listarBeneficio?acao=listar&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
        <% if(podeEditar){%><a href="#no-back" name="submit2" onClick="verificaCampos(); return false;" class="btn btn-primary"><hl:message key="rotulo.botao.salvar"/></a><%} %>
        <input type="hidden" class="btn btn-primary" name="MM_update" value="form1">
        <input type="hidden" class="btn btn-primary" name="ben_codigo" value="editar">
        <input type="hidden" class="btn btn-primary" name="tipo" value="editar">
        <input type="hidden" class="btn btn-primary" name="tmrCodigo" value="">
        <input type="hidden" class="btn btn-primary" name="acao" value="salvar">
        <input type="hidden" name="servicos" id="servicos">
        <input type="hidden" name="<%=Columns.getColumnName(Columns.BEN_ATIVO)%>" value="<%= !novo ? TextHelper.forHtmlAttribute(beneficio.getBenAtivo()) : ""%>">
      </div>
    </div>
  </form>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>   
  <jsp:attribute name="javascript">${javascript}</jsp:attribute> 
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
<script>
document.getElementById("sortable").style.cursor = "move";
function sortingOptions() {
	var selectOptions = $("select[id='<%=Columns.SVC_CODIGO%>'] option");
	    
	selectOptions.sort(function(a, b) {

        if(a.value.length == 0){
            return -1;
        }

	    if (a.text > b.text) {
	        return 1;
	    }
	    else if (a.text < b.text) {
	        return -1;
	    }
	    else {
	        return 0
	    }
	});

	$("select[id='<%=Columns.SVC_CODIGO%>']").empty().append(selectOptions);
	var selectedOption = $("select[id='<%=Columns.SVC_CODIGO%>']").val();
	$("select[id='<%=Columns.SVC_CODIGO%>']").val(selectedOption);
	$("select[id='<%=Columns.SVC_CODIGO%>']").val("")
}

sortingOptions()

function montarObjetoServico(){
   var servicos = [];
    $('#sortable').find('li').each(function(i){
      servicos.push(this.id);
    });

    $('#servicos').val(servicos.toString());
}

montarObjetoServico();

function inserirServico(element){
	if(element.options[element.selectedIndex].value.length == 0)
    return;

	var numberList = $("#sortable li").length;
	$("#sortable").append('<li class="form-control" value="' + element.options[element.selectedIndex].value + ';' + $("select[id='<%=Columns.TIB_CODIGO%>']").val() +'" id="' + element.options[element.selectedIndex].value + ';' + $("select[id='<%=Columns.TIB_CODIGO%>']").val() + '"><strong>' + numberList + ' - </strong><span style="font-weight: normal;">' + element.options[element.selectedIndex].text + '</span><strong>&nbsp;&nbsp;&nbsp;(' + $("select[id='<%=Columns.TIB_CODIGO%>'] :selected").text() +')</strong><button type="button" class="btn btn-secondary" onclick="remove(this)" style="float: right; height: 20px; width: 20px; padding: 0; visibility: hidden">X</button></li>');

	activateButtonRemove();
	sortingOptions();
	montarObjetoServico();
}

function remove(element){
	 element.parentNode.parentNode.removeChild(element.parentNode); 
	 $('#sortable').find('li').each(function(i){
        $(this.firstChild.innerText = i + " - ");
     });

	 sortingOptions();
	 montarObjetoServico();
}

$( "#sortable" ).sortable({
	stop: function( event, ui ) {
  	$('#sortable').find('li').each(function(i){
                $(this.firstChild.innerText = i + " - ");
       });
  	montarObjetoServico();
    }
});

function activateButtonRemove(){
  $("#sortable li")
  .mouseover(function() {
    $( this ).find( "button" ).css("visibility", "visible");
  
  })
  .mouseout(function() {
  	$( this ).find( "button" ).css("visibility", "hidden");
  });
}

activateButtonRemove();

$( function() {
    $( "#sortable" ).sortable();
    $( "#sortable" ).disableSelection();
 });

function buscarServicos(naturezaServico){
  console.log("entrou");
  $.ajax({
      type : 'post',
      url : "../v3/buscarServicos?_skip_history_=true&naturezaServico=" + naturezaServico,
      async : true,
      contentType : 'application/json',
      success : function(data) {

    	  $('#servicos').val('');
    	  $('#sortable').empty();
    	  $("select[id='<%=Columns.SVC_CODIGO%>'] option").remove();
    	  $("select[id='<%=Columns.SVC_CODIGO%>']").append($('<option>', {
    		    value: "",
    		    text: "<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione.servicos", responsavel)%>"
    		}));

          for (var key in data) {
        	  $("select[id='<%=Columns.SVC_CODIGO%>']").append($('<option>', {
        		    value: key,
        		    text: data[key]
        		}));
        	}
          sortingOptions();
      }
  });

}

function disableSelectServico(tibCodigo){
     if(tibCodigo.length > 0){
       $("select[id='<%=Columns.SVC_CODIGO%>']").removeAttr('disabled');
       $("select[id='<%=Columns.SVC_CODIGO%>']").css("background-color","white");
       $("select[id='<%=Columns.SVC_CODIGO%>']").css("opacity","1");
       $("select[id='<%=Columns.SVC_CODIGO%>']").css("color","black");
     }else{
       $("select[id='<%=Columns.SVC_CODIGO%>']").attr("disabled","disabled");
       $("select[id='<%=Columns.SVC_CODIGO%>']").css("background-color","#EBEDEE");
       $("select[id='<%=Columns.SVC_CODIGO%>']").css("opacity","1");
       $("select[id='<%=Columns.SVC_CODIGO%>']").css("color","#8F939A");
     }
}
</script>
          
          
          
          
          
          
          
          
          
          
          
          
          
          