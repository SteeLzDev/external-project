<%--
* <p>Title: editarEnderecoServidor_v4</p>
* <p>Description: Editar Endereço Servidor v4</p>
* <p>Copyright: Copyright (c) 2005</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ page import="com.zetra.econsig.persistence.entity.EnderecoServidor"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
ServidorTransferObject servidor = (ServidorTransferObject) request.getAttribute("servidor");
EnderecoServidor enderecoServidor = (EnderecoServidor) request.getAttribute("enderecoServidor");
List<TransferObject> tipoEndereco = (List<TransferObject>) request.getAttribute("tipoEndereco");
Boolean podeEditar = (Boolean) request.getAttribute("podeEditar");
%>

<c:set var="javascript">
<script type="text/JavaScript">
function formLoad() {
    f0 = document.forms[0];
  }

  function verificaCampos() {

    var controles = new Array(
                "<%=Columns.getColumnName(Columns.ENS_LOGRADOURO)%>",
                "<%=Columns.getColumnName(Columns.ENS_NUMERO)%>",
                "<%=Columns.getColumnName(Columns.ENS_BAIRRO)%>",
                "<%=Columns.getColumnName(Columns.ENS_UF)%>",
                "<%=Columns.getColumnName(Columns.ENS_MUNICIPIO)%>",
                "<%=Columns.getColumnName(Columns.ENS_CEP)%>"
                );
    var msgs = new Array(
        "<hl:message key='mensagem.servidor.endereco.logradouro.informar'/>",
        "<hl:message key='mensagem.servidor.endereco.numero.informar'/>",
        "<hl:message key='mensagem.servidor.endereco.bairro.informar'/>",
        "<hl:message key='mensagem.servidor.endereco.estado.informar'/>",
        "<hl:message key='mensagem.servidor.endereco.cidade.informar'/>",
        "<hl:message key='mensagem.servidor.endereco.cep.informar'/>"
        );

    if (ValidaCampos(controles, msgs)) {
       document.getElementById("form1").submit();
    }
  }

  $(document).ready(function() {
      document.getElementsByName('<%=Columns.getColumnName(Columns.ENS_UF)%>')[0].setAttribute("onchange", "listarCidades(this.value), '' ");
    <%if(!TextHelper.isNull(enderecoServidor) && !TextHelper.isNull(enderecoServidor.getEnsCodigoMunicipio())){%>
        listarCidades("<%=enderecoServidor.getEnsUf()%>", "<%=enderecoServidor.getEnsCodigoMunicipio()%>", "")
    <%} else if (!TextHelper.isNull(enderecoServidor) && TextHelper.isNull(enderecoServidor.getEnsCodigoMunicipio())) {%>
        listarCidades("<%=enderecoServidor.getEnsUf()%>", "", "");  
    <%}%>
  });

  function listarCidades(codEstado, codCidade, nmcidade) {
      if (!codEstado)
          return;
      $.ajax({  
          type : 'post',
          url : "../v3/listarCidades?_skip_history_=true&codEstado=" + codEstado,
          async : true,
          contentType : 'application/json',
          success : function(data) {
              var options = "<option value>" + "<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>" + "</option> ";
              var cidades = null;
              var nomeCidade = null;
                cidades = JSON.parse(JSON.stringify(data));
                cidades.forEach(function(objeto) {
                codigoCidade = objeto.atributos['<%=Columns.CID_CODIGO_IBGE%>'];
                nomeCidade = objeto.atributos['<%=Columns.CID_NOME%>'];
                  if (codCidade != '' && codCidade != null && codCidade != undefined) { 
                     if (codCidade == codigoCidade) {
                        options = options.concat('<option selected="true" value="').concat(codigoCidade + ';' + nomeCidade).concat('">').concat(nomeCidade).concat('</option>');
                     } else {
                        options = options.concat('<option value="').concat(codigoCidade + ';' + nomeCidade).concat('">').concat(nomeCidade).concat('</option>');
                     }
                  } else if (nmcidade != ''){
                        if (nmcidade == nomeCidade) {
                           options = options.concat('<option selected="true" value="').concat(codigoCidade + ';' + nomeCidade).concat('">').concat(nomeCidade).concat('</option>');
                        } else {
                           options = options.concat('<option value="').concat(codigoCidade + ';' + nomeCidade).concat('">').concat(nomeCidade).concat('</option>');
                        }                	  
                  } else {
                      options = options.concat('<option value="').concat(codigoCidade + ';' + nomeCidade).concat('">').concat(nomeCidade).concat('</option>');
                  }
              });
              document.getElementById('<%=Columns.getColumnName(Columns.ENS_MUNICIPIO)%>').innerHTML = options;
          },
          error: function (response) {
              console.log(response.statusText);
          }
      });
  }
  
  function buscaCep(){
	  var cep = document.getElementById('<%=Columns.getColumnName(Columns.ENS_CEP)%>').value;
	  cep = cep.replace('.','').replace('-','');
	  if (cep != null){
  	     $.ajax({
        	  	type: 'post',
   	        	url: "../v3/buscaCep?cep="+cep,
  	            async : true,
  	            contentType : 'application/json',
  	            success : function(data) {
  	            	if (!data){
  	  	                 document.getElementById('<%=Columns.getColumnName(Columns.ENS_LOGRADOURO)%>').value = '';
   	  	                 document.getElementById('<%=Columns.getColumnName(Columns.ENS_BAIRRO)%>').value =   '';
   	  	                 document.getElementById('<%=Columns.getColumnName(Columns.ENS_NUMERO)%>').value = '';
   	  	                 document.getElementById('<%=Columns.getColumnName(Columns.ENS_COMPLEMENTO)%>').value = '';
   	  	                 document.getElementsByName('<%=Columns.getColumnName(Columns.ENS_UF)%>')[0].value = '';
   	  	                 document.getElementsByName('<%=Columns.getColumnName(Columns.ENS_MUNICIPIO)%>')[0].value = '';
  	            	} else {
  	  	            	 var logradouro = null;
  	  	            	 var bairro = null;
  	  	            	 var cidade = null;
  	  	            	 var estadoSigla = null;
  	  	            	 var dados = JSON.parse(JSON.stringify(data));
  	   	            	 dados.forEach(function(objeto){
  	  	 	            	 logradouro = objeto.atributos['<%=Columns.CEP_LOGRADOURO%>'];
  	  	                     bairro = objeto.atributos['<%=Columns.CEP_BAIRRO%>'];
  	  	                     cidade = objeto.atributos['<%=Columns.CEP_CIDADE%>'];
  	  	                     estado = objeto.atributos['<%=Columns.CEP_ESTADO%>'];
  	  	                     estadoSigla = objeto.atributos['<%=Columns.CEP_ESTADO_SIGLA%>'];
  	   	            	 })
  	   	            	 if (logradouro){
  	   	  	                 document.getElementById('<%=Columns.getColumnName(Columns.ENS_LOGRADOURO)%>').value = logradouro;
  	   	  	                 document.getElementById('<%=Columns.getColumnName(Columns.ENS_BAIRRO)%>').value =   bairro;
  	   	  	                 document.getElementsByName('<%=Columns.getColumnName(Columns.ENS_UF)%>')[0].value = estadoSigla;
  	   	  	                 listarCidades(estadoSigla, '', cidade);
  	   	            	 }   	            		
  	            	}
 	            },
             error: function (response) {
                 console.log(response.statusText)
             }
 	     });
	  }
  }
</script>
</c:set>
<c:set var="title">
  <hl:message key="rotulo.servidor.manutencao.endereco.titulo" />
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
<form method="POST" action="../v3/editarServidor?acao=salvarEndereco&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1" id="form1">
        <INPUT TYPE="hidden" NAME="<%=Columns.getColumnName(Columns.ENS_SER_CODIGO)%>" VALUE="<%=servidor.getSerCodigo()%>" />
        <INPUT TYPE="hidden" NAME="<%=Columns.getColumnName(Columns.ENS_CODIGO)%>" VALUE="<%=!TextHelper.isNull(enderecoServidor) && !TextHelper.isNull(enderecoServidor.getEnsCodigo()) ? enderecoServidor.getEnsCodigo() : ""%>" />
        <div class="row">
        <div class="col-sm">
          <div class="card">
            <div class="card-header">
              <h2 class="card-header-title"><%=TextHelper.forHtmlContent(servidor.getSerNome().toUpperCase())%></h2>
            </div>
            <div class="card-body">
                <div class="row">
                  <div class="form-group col-sm-4">
                    <label for="<%=Columns.getColumnName(Columns.ENS_CEP)%>"><hl:message key="rotulo.endereco.cep" /></label>
                    <hl:htmlinput name="<%=Columns.getColumnName(Columns.ENS_CEP)%>"
                              di="<%=Columns.getColumnName(Columns.ENS_CEP)%>"
                              type="text"
                              classe="form-control" onChange='buscaCep();'
                              value="<%=TextHelper.forHtmlAttribute(enderecoServidor != null && enderecoServidor.getEnsCep() != null ? enderecoServidor.getEnsCep() : "")%>"
                              size="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCepSize())%>"
                              mask="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCepMask())%>"
                              others='<%=!podeEditar ? "disabled" :""%>'/>
                  </div>
                </div>            
            
            
                <div class="row">
                  <div class="form-group col-sm-4">
                    <label for="<%=Columns.getColumnName(Columns.ENS_TIE_CODIGO)%>"><hl:message key="rotulo.tipo.endereco"/></label>
                    <%=JspHelper.geraCombo(tipoEndereco, Columns.getColumnName(Columns.ENS_TIE_CODIGO), Columns.TIE_CODIGO, Columns.TIE_DESCRICAO, null, null, false, 1, !TextHelper.isNull(enderecoServidor) && !TextHelper.isNull(enderecoServidor.getTipoEndereco().getTieCodigo()) ? enderecoServidor.getTipoEndereco().getTieCodigo() : "", null, podeEditar ? false : true, "form-control")%>
                  </div>
                   <div class="form-group col-sm-8">
                    <label for="<%=Columns.getColumnName(Columns.ENS_LOGRADOURO)%>"><hl:message key="rotulo.endereco.logradouro" /></label>
                    <hl:htmlinput name="<%=Columns.getColumnName(Columns.ENS_LOGRADOURO)%>"
                              di="<%=Columns.getColumnName(Columns.ENS_LOGRADOURO)%>"
                              type="text"
                              classe="form-control"
                              value="<%=TextHelper.forHtmlAttribute(enderecoServidor != null && enderecoServidor.getEnsLogradouro() != null ? TextHelper.forHtmlContent(enderecoServidor.getEnsLogradouro()) : "")%>"
                              size="32"
                              mask="#*100"
                              others='<%=!podeEditar ? "disabled" :""%>' />                  
                    </div>
                </div>
                
                <div class="row">
                  <div class="form-group col-sm-4">
                    <label for="<%=Columns.getColumnName(Columns.ENS_NUMERO)%>"><hl:message key="rotulo.endereco.numero" /></label>
                    <hl:htmlinput name="<%=Columns.getColumnName(Columns.ENS_NUMERO)%>"
                              di="<%=Columns.getColumnName(Columns.ENS_NUMERO)%>"
                              type="number"
                              classe="form-control"
                              value="<%=TextHelper.forHtmlAttribute(enderecoServidor != null && enderecoServidor.getEnsNumero() != null ? enderecoServidor.getEnsNumero() : "")%>"
                              size="5"
                              others='<%=!podeEditar ? "disabled" :""%>'  />                 
                   </div>
                  <div class="form-group col-sm-3">
                    <label for="<%=Columns.getColumnName(Columns.ENS_COMPLEMENTO)%>"><hl:message key="rotulo.endereco.complemento"/></label>
                    <hl:htmlinput name="<%=Columns.getColumnName(Columns.ENS_COMPLEMENTO)%>"
                              di="<%=Columns.getColumnName(Columns.ENS_COMPLEMENTO)%>"
                              type="text"
                              classe="form-control"
                              value="<%=TextHelper.forHtmlAttribute(enderecoServidor != null && enderecoServidor.getEnsComplemento() != null ? TextHelper.forHtmlContent(enderecoServidor.getEnsComplemento()) : "")%>"
                              size="22"
                              mask="#*40"
                              others='<%=!podeEditar ? "disabled" :""%>'/>
                  </div>
                  <div class="form-group col-sm-5">
                    <label for="<%=Columns.getColumnName(Columns.ENS_BAIRRO)%>"><hl:message key="rotulo.endereco.bairro" /></label>
                    <hl:htmlinput name="<%=Columns.getColumnName(Columns.ENS_BAIRRO)%>"
                              di="<%=Columns.getColumnName(Columns.ENS_BAIRRO)%>"
                              type="text"
                              classe="form-control"
                              value="<%=TextHelper.forHtmlAttribute(enderecoServidor != null && enderecoServidor.getEnsBairro() != null ? TextHelper.forHtmlContent(enderecoServidor.getEnsBairro()) : "")%>"
                              size="32"
                              mask="#*40"
                              others='<%=!podeEditar ? "disabled" :""%>'/>
                  </div>
                  
                </div>
                
                <div class="row">
                  <div class="form-group col-sm-3">
                    <label for="<%=Columns.getColumnName(Columns.ENS_UF)%>"><hl:message key="rotulo.endereco.estado"/></label>
                    <%=JspHelper.geraComboUF(Columns.getColumnName(Columns.ENS_UF), TextHelper.forHtmlAttribute(enderecoServidor != null && enderecoServidor.getEnsUf() != null ? enderecoServidor.getEnsUf() : ""), podeEditar ? false : true, "form-control", responsavel)%>
                  </div>
                  
                  <div class="form-group col-sm-9">
                    <label for="<%=Columns.getColumnName(Columns.ENS_MUNICIPIO)%>"><hl:message key="rotulo.endereco.cidade" /></label>
                    <select <%=!podeEditar ? "disabled" : ""%> name="<%=Columns.getColumnName(Columns.ENS_MUNICIPIO)%>" id="<%=Columns.getColumnName(Columns.ENS_MUNICIPIO)%>" class="form-control" style="background-color: white; color: black;"></select>
                  </div>
                </div>
            </div>
          </div>
        </div>
      </div>
     
      <div class="pull-right">
        <div class="btn-action">
          <a onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;" class="btn btn-outline-danger" href="#"><hl:message key="rotulo.botao.cancelar"/></a>
          <a href="#" onClick="verificaCampos(); return false;" class="btn btn-primary"><hl:message key="rotulo.botao.salvar"/></a>         
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