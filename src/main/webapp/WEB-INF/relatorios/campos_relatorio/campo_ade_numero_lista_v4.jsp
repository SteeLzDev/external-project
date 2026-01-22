<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="descricao">${descricoes[recurso]}</c:set>
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
  String obrAdeNumPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
  String descAdeNumPage = pageContext.getAttribute("descricao").toString();     
  String adeNumero = (String) JspHelper.verificaVarQryStr(request, "adeNumero");
  String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
  boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;

%>
    <div class="form-group col-sm-12 col-md-6">
      <label for="ADE_NUMERO"><hl:message key="rotulo.consignacao.numero"/></label>
      <hl:htmlinput name="ADE_NUMERO"
                    di="ADE_NUMERO"
                    type="text"
                    classe="form-control w-100"
                    mask="#D20"
                    value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "ADE_NUMERO"))%>"
                    placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.ade.numero", responsavel)%>'
                    nf="RSE_MATRICULA" 
      />
    </div>
    <div class="form-group col-sm-12 col-md-1 mt-4">
      <a id="adicionaAdeLista" class="btn btn-primary w-50" href="javascript:void(0);" onClick="adicionaNumero()" aria-label='<hl:message key="mensagem.inserir.ade.numero.clique.aqui"/>'>
        <svg width="15"><use xlink:href="../img/sprite.svg#i-avancar"></use></svg>
      </a>
      <a id="removeAdeLista" class="btn btn-primary w-50 mt-1" href="javascript:void(0);" onClick="removeNumero()" aria-label='<hl:message key="mensagem.remover.ade.numero.clique.aqui"/>' style="display: none">
        <svg width="15"><use xlink:href="../img/sprite.svg#i-voltar"></use></svg>
      </a>
    </div>
    <div id="adeLista" class="form-group col-sm-12 col-md-5 mt-4" style="display: none">
      <select class="form-control w-100" id="ADE_NUMERO_LIST" name="ADE_NUMERO_LIST" multiple="multiple" size="6"></select>
    </div>  
 
          
  <% if (obrAdeNumPage.equals("true")) { %>
    <script type="text/JavaScript">
      function funAdeNumPage() {
          camposObrigatorios = camposObrigatorios + 'ADE_NUMERO_LIST,';
          msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.ade.numero"/>,';
      }
      addLoadEvent(funAdeNumPage);     
  	</script>
  <% } %>   
    
  <script type="text/JavaScript">
  	function valida_campo_ade_numero_lista() {
         return true;
    }
     
    function pesquisar() {
    	  if (validarCamposObrigatorios() && validarCamposOpcionais()) {
    	    if (f0.senha != null && f0.senha.value != '') {
    	      CriptografaSenha(f0.senha, f0.serAutorizacao, false);
    	    }
    	    selecionarTodosItens('ADE_NUMERO_LIST');
    	    f0.submit();
    	  }
     }
       

     function listarTudo() {
    	  if (validarCamposOpcionais()) {
    	    f0.TIPO_LISTA.value = 'TUDO';
    	    selecionarTodosItens('ADE_NUMERO_LIST');
    	    f0.submit();
    	  }
      }     
     
     <% if (!responsavel.isSer()) { %>
     
 		function obgAdeCpfMat() {
 			ControlesAvancados = new Array("ADE_NUMERO", "ADE_NUMERO_LIST", "CPF", "RSE_MATRICULA");
 			MsgPeloMenosUm = '<hl:message key="mensagem.pesquisa.informe.ade.matricula.ou.cpf"/>';
 		
 		}
     	addLoadEvent(obgAdeCpfMat);
   <% } %> 

   	function adicionaNumero() {
    	    var ade = document.getElementById('ADE_NUMERO').value;

    	    if (ade != '' && (/\D/.test(ade) || ade.length > 20)) {
    	        alert('<hl:message key="mensagem.erro.ade.numero.invalido"/>');
    	         return;
    	    }
    	    
    	    if (document.getElementById('ADE_NUMERO').value != '') {
    	      document.getElementById('adeLista').style.display = '';
    	      document.getElementById('removeAdeLista').style.display = '';
    	      insereItem('ADE_NUMERO', 'ADE_NUMERO_LIST');
    	    }
    }
     
    function removeNumero() {
    	    removeDaLista('ADE_NUMERO_LIST');
    	    if (document.getElementById('ADE_NUMERO_LIST').length == 0) {
    	        document.getElementById('adeLista').style.display = 'none';
    	        document.getElementById('removeAdeLista').style.display = 'none';
    	    }
    	}
    
    function insereItem(nomeCampoValor, nomeCampoLista) {
    	  var lista = document.getElementById(nomeCampoLista);
    	  var valor = document.getElementById(nomeCampoValor);

    	  if (valor.value != null && valor.value != '') {
    	    for (var i = 0; i < lista.length; i++) {
    	      if (lista.options[i].value == valor.value) {
    	        alert(mensagem('mensagem.erro.lista.valor.existe'));
    	        valor.focus();
    	        return;
    	      }
    	    }

    	    var opt = new Option(valor.value, valor.value);
    	    lista.options[lista.length] = opt;
    	    valor.value = '';
    	    valor.focus();
    	  }
    	}
    
    function removeDaLista(selectId) {
    	  var selectComp = document.getElementById(selectId);
    	  
    	  for (var i = selectComp.length - 1; i >= 0; i--) {
    	    if (selectComp.options[i].selected) {
    	      selectComp.options[i] = null;
    	    }
    	  }
    	}
    
    function selecionarTodosItens(selectId) {
    	  var selectComp = document.getElementById(selectId);
    	    if (selectComp != null) {
    	    for (var i = selectComp.length - 1; i >= 0; i--) {
    	      selectComp.options[i].selected = true;
    	    }
    	  }
    	}
	</script>        
                
