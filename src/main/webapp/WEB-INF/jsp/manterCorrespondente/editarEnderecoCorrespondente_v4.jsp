<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.persistence.entity.EnderecoCorrespondente"%>
<%@ page import="com.zetra.econsig.persistence.entity.TipoEndereco"%>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%@ page import="com.zetra.econsig.dto.entidade.*" %>

<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String cor_codigo = (String) request.getAttribute("COR_CODIGO");
List<TipoEndereco> lstTipoEndereco = (List<TipoEndereco>) request.getAttribute("lstTipoEndereco");
String msgErro = (String) request.getAttribute("msgErro");
EnderecoCorrespondente enderecoCorrespondente = (EnderecoCorrespondente) request.getAttribute("enderecoCorrespondente");

%>
<c:set var="title">
  <hl:message key="rotulo.endereco.correspondente.singular"/>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
    <%if (enderecoCorrespondente != null) {%>
      <div class="col-sm-12 col-md-12 mb-2">
        <div class="float-end">
          <button data-bs-toggle="dropdown" aria-haspopup="true" id="acoes" aria-expanded="false" class="btn btn-primary" type="submit"><hl:message key="rotulo.acoes"/></button>
          <div class="dropdown-menu dropdown-menu-right" aria-labelledby="acoes">
            <a class="dropdown-item" href="#no-back" onClick="javascript: verificaExclusao();"><hl:message key="rotulo.acoes.excluir"/></a>
          </div>
        </div> 
      </div>  
    <%}%>
    <div class="col-sm-12">  
      <form method="post" action="../v3/manterEnderecosCorrespondente?acao=salvar&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
        <input type="hidden" name="COR_CODIGO" VALUE="<%=TextHelper.forHtmlAttribute(cor_codigo)%>" />
        <input type="hidden" name="ECR_CODIGO" VALUE="<%=enderecoCorrespondente != null ? TextHelper.forHtmlAttribute(enderecoCorrespondente.getEcrCodigo()) : ""%>" />
        <div class="card">
          <div class="card-header">
              <h2 class="card-header-title"><hl:message key="rotulo.titulo.novo.endereco.correspondente"/></h2>
          </div>
          <div class="card-body">
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="TIE_CODIGO"><hl:message key="rotulo.endereco.correspondente.tipo.endereco"/></label>
                <select class="form-control" id="TIE_CODIGO" name="TIE_CODIGO" required>
                  <OPTION VALUE=""><hl:message key="rotulo.campo.selecione"/></OPTION>
                  <%
                     for (TipoEndereco tipoEndereco : lstTipoEndereco) {
                  %>
                       <OPTION VALUE="<%=TextHelper.forHtmlAttribute(tipoEndereco.getTieCodigo())%>" <%= enderecoCorrespondente != null && enderecoCorrespondente.getTipoEndereco() != null && enderecoCorrespondente.getTipoEndereco().getTieCodigo().equals(tipoEndereco.getTieCodigo()) ? "SELECTED" : "" %>><%=TextHelper.forHtmlContent(tipoEndereco.getTieDescricao())%></OPTION>
                  <% } %>
                </select>
              </div>
              <div class="form-group col-sm-6">
                <label for="cep"><hl:message key="rotulo.endereco.correspondente.cep"/></label>
                <%String onFocusEcrCep = "SetarEventoMascaraV4(this,'" + LocaleHelper.getCepMask() + "',true);"; %>
                <hl:htmlinput name="ECR_CEP"
                  type="text"
                  size="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCepSize())%>"
                  classe="form-control"
                  onFocus="<%= onFocusEcrCep %>"
                  onChange="buscaCep();"
                  di="cep"
                  mask="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCepMask())%>"
                  value="<%=enderecoCorrespondente != null ? TextHelper.forHtmlAttribute(enderecoCorrespondente.getEcrCep()) : "" %>"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.cep", responsavel)%>"
                />
              </div>
              <div class="form-group col-sm-5">
                <label for="logradouro"><hl:message key="rotulo.endereco.correspondente.logradouro"/></label>
                <%String onFocusEcrLog = "SetarEventoMascara(this,'#*100',true);"; %>
                <hl:htmlinput name="ECR_LOGRADOURO"
                  type="text"
                  classe="form-control"
                  size="32"
                  onFocus="<%= onFocusEcrLog %>"
                  di="logradouro"
                  value="<%=enderecoCorrespondente != null ? TextHelper.forHtmlAttribute(enderecoCorrespondente.getEcrLogradouro()) : "" %>"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.logradouro", responsavel)%>"
                />
              </div>
              <div class="form-group col-sm-3">
                <label for="numero"><hl:message key="rotulo.endereco.correspondente.numero"/></label>
                <%String onFocusEcrNum = "SetarEventoMascara(this,'#D11',true);"; %>
                <hl:htmlinput name="ECR_NUMERO"
                  type="text"
                  classe="form-control"
                  size="32"
                  onFocus="<%= onFocusEcrNum %>"
                  di="numero"
                  value="<%=enderecoCorrespondente != null ? TextHelper.forHtmlAttribute(enderecoCorrespondente.getEcrNumero()) : "" %>"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.numero", responsavel)%>"
                />
              </div>
              <div class="form-group col-sm-4">
                <label for="complemento"><hl:message key="rotulo.endereco.correspondente.complemento"/></label>
                <%String onFocusEndComp = "SetarEventoMascara(this,'#*40',true);"; %>
                <hl:htmlinput name="ECR_COMPLEMENTO"
                  type="text"
                  classe="form-control"
                  size="32"
                  onFocus="<%= onFocusEndComp %>"
                  di="complemento"
                  value="<%=enderecoCorrespondente != null ? TextHelper.forHtmlAttribute(enderecoCorrespondente.getEcrComplemento()) : "" %>"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.mensagem.endereco.complemento", responsavel)%>"
                />
              </div>
              <div class="form-group col-sm-4">
                <label for="bairro"><hl:message key="rotulo.endereco.correspondente.bairro"/></label>
                <%String onFocusEcrBairro = "SetarEventoMascara(this,'#*100',true);"; %>
                <hl:htmlinput name="ECR_BAIRRO"
                  type="text"
                  classe="form-control"
                  size="32"
                  onFocus="<%= onFocusEcrBairro %>"
                  di="bairro"
                  value="<%=enderecoCorrespondente != null ? TextHelper.forHtmlAttribute(enderecoCorrespondente.getEcrBairro()) : "" %>"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.bairro", responsavel)%>"
                />
              </div>
              <div class="form-group col-sm-4">
                <label for="uf"><hl:message key="rotulo.endereco.correspondente.uf" /></label>
                <%=JspHelper.geraComboUF("ECR_UF", "uf", enderecoCorrespondente != null ? TextHelper.forHtmlAttribute(enderecoCorrespondente.getEcrUf()) : "", false, "form-control", responsavel) %>
              </div>
              <div class="form-group col-sm-4">
                <label for="municipio"><hl:message key="rotulo.endereco.correspondente.municipio"/></label>
                <%String onFocusEcrMun = "SetarEventoMascara(this,'#*100',true);"; %>
                <select name="ECR_MUNICIPIO" id="municipio" class="form-control" style="background-color: white; color: black;"></select>
              </div>
            </div>
          </div>
        </div>
        <div class="btn-action">
          <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.cancelar" /></a>
          <a class="btn btn-primary" onClick="verificaCampos(); return false;" href="#no-back"><hl:message key="rotulo.botao.salvar" /></a>
        </div>
      </form>
    </div>
  </div>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript" src="../js/listagem.js"></script>
<script type="text/JavaScript">
  var f0 = document.forms[0];

  $(document).ready(function() {
    document.getElementsByName('ECR_UF')[0].setAttribute("onchange", "listarCidades(this.value), '' ");

    <%if(enderecoCorrespondente != null && !TextHelper.isNull(enderecoCorrespondente.getEcrMunicipio())){%>
      listarCidades("<%=TextHelper.forJavaScriptAttribute(enderecoCorrespondente.getEcrUf())%>", "" , "<%=TextHelper.forJavaScriptAttribute(enderecoCorrespondente.getEcrMunicipio())%>");
    <%}%>
  });
 
  function listarCidades(codEstado, codCidade, nmcidade) {
    if (!codEstado)
       return;
    $.ajax({  
       type : 'post',
       url : "../v3/listarCidades?acao=manterEnderecosCorrespondente&_skip_history_=true&codEstado=" + codEstado,
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
                   options = options.concat('<option selected="true" value="').concat(nomeCidade).concat('">').concat(nomeCidade).concat('</option>');
                } else {
                   options = options.concat('<option value="').concat(nomeCidade).concat('">').concat(nomeCidade).concat('</option>');
                }
             } else {
                if (nmcidade != ''){
                   if (nmcidade == nomeCidade) {
                      options = options.concat('<option selected="true" value="').concat(nomeCidade).concat('">').concat(nomeCidade).concat('</option>');
                   } else {
                      options = options.concat('<option value="').concat(nomeCidade).concat('">').concat(nomeCidade).concat('</option>');
                   }                	  
                }
             }
           });
           document.getElementById('municipio').innerHTML = options;
       },
      error: function (response) {
        console.log(response.statusText);
      }
    });
  }
 
  function buscaCep(){
    var cep = document.getElementById('cep').value;
    cep = cep.replace('.','').replace('-','');
   
    if (cep != null && cep.length == 8){
       $.ajax({
        type: 'post',
        	url: "../v3/buscaCep?acao=manterEnderecosCorrespondente&_skip_history_=true&cep="+cep,
          async : true,
          contentType : 'application/json',
          success : function(data) {
          	if (!data){
                 document.getElementById('logradouro').value = '';
                 document.getElementById('bairro').value = '';
                 document.getElementById('numero').value = '';
                 document.getElementById('complemento').value = '';
                 document.getElementsByName('ECR_UF')[0].value = '';
                 document.getElementsByName('ECR_MUNICIPIO')[0].value = '';
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
                  document.getElementById('logradouro').value = logradouro;
                  document.getElementById('bairro').value = bairro;
                  document.getElementsByName('ECR_UF')[0].value = estadoSigla;
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

  function verificaCampos() {
    var controles = new Array(
    		    "TIE_CODIGO",
                "ECR_LOGRADOURO",
                "ECR_NUMERO",
                "ECR_BAIRRO",
                "ECR_UF",
                "ECR_MUNICIPIO",
                "ECR_CEP"
                );
    var msgs = new Array(
        "<hl:message key='mensagem.erro.endereco.correspondente.tipo.endereco.informar'/>",
        "<hl:message key='mensagem.erro.endereco.correspondente.logradouro.informar'/>",
        "<hl:message key='mensagem.erro.endereco.correspondente.numero.informar'/>",
        "<hl:message key='mensagem.erro.endereco.correspondente.bairro.informar'/>",
        "<hl:message key='mensagem.erro.endereco.correspondente.uf.informar'/>",
        "<hl:message key='mensagem.erro.endereco.correspondente.municipio.informar'/>",
        "<hl:message key='mensagem.erro.endereco.correspondente.cep.informar'/>"
        );
    
    if (ValidaCampos(controles, msgs)) {
       f0.submit();
    }
  }

  function verificaExclusao() {
    if (confirm(mensagem("mensagem.confirmacao.endereco.correspondente.exclusao").replace('{0}', '<%=enderecoCorrespondente != null ? TextHelper.forJavaScript(enderecoCorrespondente.getEcrLogradouro()) : ""%>'))) {
      postData('../v3/manterEnderecosCorrespondente?acao=excluir&ECR_CODIGO=<%=enderecoCorrespondente != null ? TextHelper.forJavaScriptAttribute(enderecoCorrespondente.getEcrCodigo()) : ""%>&COR_CODIGO=<%=TextHelper.forJavaScriptAttribute(cor_codigo)%>&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>');
    } else {
      return false;
    }
  }
</script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>