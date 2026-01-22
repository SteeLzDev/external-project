<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.persistence.entity.EnderecoConsignataria"%>
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

String csa_codigo = (String) request.getAttribute("CSA_CODIGO");
List<TipoEndereco> lstTipoEndereco = (List<TipoEndereco>) request.getAttribute("lstTipoEndereco");
String msgErro = (String) request.getAttribute("msgErro");
EnderecoConsignataria enderecoConsignataria = (EnderecoConsignataria) request.getAttribute("enderecoConsignataria");

%>
<c:set var="title">
  <hl:message key="rotulo.endereco.consignataria.singular"/>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
    <%if (enderecoConsignataria != null) {%>
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
      <form method="post" action="../v3/manterEnderecosConsignataria?acao=salvar&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
        <input type="hidden" name="CSA_CODIGO" VALUE="<%=TextHelper.forHtmlAttribute(csa_codigo)%>" />
        <input type="hidden" name="ENC_CODIGO" VALUE="<%=enderecoConsignataria != null ? TextHelper.forHtmlAttribute(enderecoConsignataria.getEncCodigo()) : ""%>" />
        <div class="card">
          <div class="card-header">
              <h2 class="card-header-title"><hl:message key="rotulo.titulo.novo.endereco.consignataria"/></h2>
          </div>
          <div class="card-body">
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="TIE_CODIGO"><hl:message key="rotulo.endereco.consignataria.tipo.endereco"/></label>
                <select class="form-control" id="TIE_CODIGO" name="TIE_CODIGO" required>
                  <OPTION VALUE=""><hl:message key="rotulo.campo.selecione"/></OPTION>
                  <%
                     for (TipoEndereco tipoEndereco : lstTipoEndereco) {
                  %>
                       <OPTION VALUE="<%=TextHelper.forHtmlAttribute(tipoEndereco.getTieCodigo())%>" <%= enderecoConsignataria != null && enderecoConsignataria.getTipoEndereco() != null && enderecoConsignataria.getTipoEndereco().getTieCodigo().equals(tipoEndereco.getTieCodigo()) ? "SELECTED" : "" %>><%=TextHelper.forHtmlContent(tipoEndereco.getTieDescricao())%></OPTION>
                  <% } %>
                </select>
              </div>
              <div class="form-group col-sm-6">
                <label for="cep"><hl:message key="rotulo.endereco.consignataria.cep"/></label>
                <%String onFocusEncCep = "SetarEventoMascaraV4(this,'" + LocaleHelper.getCepMask() + "',true);"; %>
                <hl:htmlinput name="ENC_CEP"
                  type="text"
                  size="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCepSize())%>"
                  classe="form-control"
                  onFocus="<%= onFocusEncCep %>"
                  onChange="buscaCep();"
                  di="cep"
                  mask="<%=LocaleHelper.getCepMask()%>"
                  value="<%=enderecoConsignataria != null ? TextHelper.forHtmlAttribute(enderecoConsignataria.getEncCep()) : "" %>"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.cep", responsavel)%>"
                />
              </div>
              <div class="form-group col-sm-5">
                <label for="logradouro"><hl:message key="rotulo.endereco.consignataria.logradouro"/></label>
                <%String onFocusEncLog = "SetarEventoMascara(this,'#*100',true);"; %>
                <hl:htmlinput name="ENC_LOGRADOURO"
                  type="text"
                  classe="form-control"
                  size="32"
                  onFocus="<%= onFocusEncLog %>"
                  di="logradouro"
                  value="<%=enderecoConsignataria != null ? TextHelper.forHtmlAttribute(enderecoConsignataria.getEncLogradouro()) : "" %>"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.logradouro", responsavel)%>"
                />
              </div>
              <div class="form-group col-sm-3">
                <label for="numero"><hl:message key="rotulo.endereco.consignataria.numero"/></label>
                <%String onFocusEncNum = "SetarEventoMascara(this,'#D11',true);"; %>
                <hl:htmlinput name="ENC_NUMERO"
                  type="text"
                  classe="form-control"
                  size="32"
                  onFocus="<%= onFocusEncNum %>"
                  di="numero"
                  value="<%=enderecoConsignataria != null ? TextHelper.forHtmlAttribute(enderecoConsignataria.getEncNumero()) : "" %>"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.numero", responsavel)%>"
                />
              </div>
              <div class="form-group col-sm-4">
                <label for="complemento"><hl:message key="rotulo.endereco.consignataria.complemento"/></label>
                <%String onFocusEndComp = "SetarEventoMascara(this,'#*40',true);"; %>
                <hl:htmlinput name="ENC_COMPLEMENTO"
                  type="text"
                  classe="form-control"
                  size="32"
                  onFocus="<%= onFocusEndComp %>"
                  di="complemento"
                  value="<%=enderecoConsignataria != null ? TextHelper.forHtmlAttribute(enderecoConsignataria.getEncComplemento()) : "" %>"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.mensagem.endereco.complemento", responsavel)%>"
                />
              </div>
              <div class="form-group col-sm-4">
                <label for="bairro"><hl:message key="rotulo.endereco.consignataria.bairro"/></label>
                <%String onFocusEncBairro = "SetarEventoMascara(this,'#*100',true);"; %>
                <hl:htmlinput name="ENC_BAIRRO"
                  type="text"
                  classe="form-control"
                  size="32"
                  onFocus="<%= onFocusEncBairro %>"
                  di="bairro"
                  value="<%=enderecoConsignataria != null ? TextHelper.forHtmlAttribute(enderecoConsignataria.getEncBairro()) : "" %>"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.bairro", responsavel)%>"
                />
              </div>
              <div class="form-group col-sm-4">
                <label for="uf"><hl:message key="rotulo.endereco.consignataria.uf" /></label>
                <%=JspHelper.geraComboUF("ENC_UF", "uf", enderecoConsignataria != null ? TextHelper.forHtmlAttribute(enderecoConsignataria.getEncUf()) : "", false, "form-control", responsavel) %>
              </div>
              <div class="form-group col-sm-4">
                <label for="municipio"><hl:message key="rotulo.endereco.consignataria.municipio"/></label>
                <%String onFocusEncMun = "SetarEventoMascara(this,'#*100',true);"; %>
                <select name="ENC_MUNICIPIO" id="municipio" class="form-control" style="background-color: white; color: black;"></select>
              </div>
            </div>
          </div>
        </div>
        <div class="btn-action">
          <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/manterEnderecosConsignataria?acao=iniciar&CSA_CODIGO=<%=csa_codigo%>&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>'); return false;"><hl:message key="rotulo.botao.cancelar" /></a>
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
    document.getElementsByName('ENC_UF')[0].setAttribute("onchange", "listarCidades(this.value), '' ");

    <%if(enderecoConsignataria != null && !TextHelper.isNull(enderecoConsignataria.getEncMunicipio())){%>
      listarCidades("<%=TextHelper.forJavaScriptAttribute(enderecoConsignataria.getEncUf())%>", "" , "<%=TextHelper.forJavaScriptAttribute(enderecoConsignataria.getEncMunicipio())%>");
    <%}%>
  });
 
  function listarCidades(codEstado, codCidade, nmcidade) {
    if (!codEstado)
       return;
    $.ajax({  
       type : 'post',
       url : "../v3/listarCidades?acao=manterEnderecosConsignataria&_skip_history_=true&codEstado=" + codEstado,
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
        	url: "../v3/buscaCep?acao=manterEnderecosConsignataria&_skip_history_=true&cep="+cep,
          async : true,
          contentType : 'application/json',
          success : function(data) {
          	if (!data){
                 document.getElementById('logradouro').value = '';
                 document.getElementById('bairro').value = '';
                 document.getElementById('numero').value = '';
                 document.getElementById('complemento').value = '';
                 document.getElementsByName('ENC_UF')[0].value = '';
                 document.getElementsByName('ENC_MUNICIPIO')[0].value = '';
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
                  document.getElementsByName('ENC_UF')[0].value = estadoSigla;
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
                "ENC_LOGRADOURO",
                "ENC_NUMERO",
                "ENC_BAIRRO",
                "ENC_UF",
                "ENC_MUNICIPIO",
                "ENC_CEP"
                );
    var msgs = new Array(
        "<hl:message key='mensagem.erro.endereco.consignataria.tipo.endereco.informar'/>",
        "<hl:message key='mensagem.erro.endereco.consignataria.logradouro.informar'/>",
        "<hl:message key='mensagem.erro.endereco.consignataria.numero.informar'/>",
        "<hl:message key='mensagem.erro.endereco.consignataria.bairro.informar'/>",
        "<hl:message key='mensagem.erro.endereco.consignataria.uf.informar'/>",
        "<hl:message key='mensagem.erro.endereco.consignataria.municipio.informar'/>",
        "<hl:message key='mensagem.erro.endereco.consignataria.cep.informar'/>"
        );
    
    if (ValidaCampos(controles, msgs)) {
       f0.submit();
    }
  }

  function verificaExclusao() {
    if (confirm(mensagem("mensagem.confirmacao.endereco.consignataria.exclusao").replace('{0}', '<%=enderecoConsignataria != null ? TextHelper.forJavaScript(enderecoConsignataria.getEncLogradouro()) : ""%>'))) {
      postData('../v3/manterEnderecosConsignataria?acao=excluir&ENC_CODIGO=<%=enderecoConsignataria != null ? TextHelper.forJavaScriptAttribute(enderecoConsignataria.getEncCodigo()) : ""%>&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csa_codigo)%>&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>');
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