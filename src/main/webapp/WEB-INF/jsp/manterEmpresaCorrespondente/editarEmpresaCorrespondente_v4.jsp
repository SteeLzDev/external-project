<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
  
  String eco_codigo = (String) request.getAttribute("eco_codigo");
  String eco_ativo = (String) request.getAttribute("eco_ativo");
  String eco_nome = (String) request.getAttribute("eco_nome");
  String eco_identificador = (String) request.getAttribute("eco_identificador");
  String titulo = (String) request.getAttribute("titulo");
  String msgErro = (String) request.getAttribute("msgErro");
  
  TransferObject empresa = (TransferObject) request.getAttribute("empresa");
  
  boolean podeEditar = (boolean) request.getAttribute("podeEditar");
%>

<c:set var="title">
  <hl:message key="rotulo.empresacorrespondente.singular"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <form method="post" action="../v3/manterEmpresaCorrespondente?acao=editar&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
  <% if (eco_codigo != null && empresa != null) { %>
  <%  if (podeEditar) { %>
    <div class="btn-action">
      <button class="btn btn-primary" onClick="vf_bloqueio_empresa('<%=TextHelper.forJavaScript(eco_codigo)%>','<%=TextHelper.forJavaScript(eco_nome)%>','<%=TextHelper.forJavaScript(eco_ativo)%>');return false;">
  <%    if (eco_ativo.equals("1")) { %>
          <hl:message key='rotulo.consignante.bloquear.consignante' />                    
  <%    } else { %>
          <hl:message key='rotulo.consignante.desbloquear.consignante' />
  <%    } %>        
      </button>
    </div>  
  <%  } %>
  <% } %>   
    <div class="card">
      <div class="card-header hasIcon pl-3">
        <h2 class="card-header-title">
  <% if (empresa != null) { %>
          <%=TextHelper.forHtmlContent(eco_identificador + " - " + eco_nome)%>
  <% } else { %>
          <hl:message key="rotulo.novo.empresa.correspondente"/>
  <% } %>
        </h2>
      </div>
      <div class="card-body">
        <div class="row">
          <div class="form-group col-sm-6">
            <label for="codigo"><hl:message key="rotulo.codigo.empresa.correspondente"/></label>
            <hl:htmlinput name="ECO_IDENTIFICADOR"
                type="text"
                classe="form-control"
                value="<%=TextHelper.forHtmlAttribute(empresa != null ? eco_identificador: JspHelper.verificaVarQryStr(request, \"ECO_IDENTIFICADOR\"))%>"
                size="32"
                mask="#A40"
                others="<%=TextHelper.forHtmlAttribute( !podeEditar ? "disabled" : "")%>"
              />
            <%=JspHelper.verificaCampoNulo(request, "ECO_IDENTIFICADOR")%>
          </div>
        </div>
        <div class="row">
          <div class="form-group col-sm-6">
            <label for="codigo"><hl:message key="rotulo.nome.empresa.correspondente"/></label>
            <hl:htmlinput name="ECO_NOME"
                type="text"
                classe="form-control"
                value="<%=TextHelper.forHtmlAttribute(empresa != null ? eco_nome: JspHelper.verificaVarQryStr(request, \"ECO_NOME\"))%>"
                size="32"
                mask="#*100"
                others="<%=TextHelper.forHtmlAttribute( !podeEditar ? "disabled" : "")%>"
            />
            <%=JspHelper.verificaCampoNulo(request, "ECO_NOME")%>
          </div>    
        </div>
        <div class="row">
          <div class="form-group col-sm-6">
            <label for="codigo"><hl:message key="rotulo.cnpj.empresa.correspondente"/></label>
            <hl:htmlinput name="ECO_CNPJ"
                type="text"
                classe="form-control"
                value="<%=TextHelper.forHtmlAttribute(empresa !=null ? (String) empresa.getAttribute(Columns.ECO_CNPJ): JspHelper.verificaVarQryStr(request, \"ECO_CNPJ\"))%>"
                mask="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCnpjMask())%>"
                size="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCnpjSize())%>"
                maxlength="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCnpjMaxLenght())%>"
                others="<%=TextHelper.forHtmlAttribute( !podeEditar ? "disabled" : "")%>"
              />
              <%=JspHelper.verificaCampoNulo(request, "ECO_CNPJ")%>
          </div>
        </div>
        <div class="legend">
          <span><hl:message key="rotulo.responsavel.plural"/></span>
        </div>
        <div class="row">
          <div class="form-group col-sm-4">
            <label for="codigo"><hl:message key="rotulo.responsavel1.empresa.correspondente"/></label>
            <hl:htmlinput name="ECO_RESPONSAVEL"
              type="text"
              classe="form-control"
              value="<%=TextHelper.forHtmlAttribute(empresa != null ? (String) empresa.getAttribute(Columns.ECO_RESPONSAVEL): JspHelper.verificaVarQryStr(request, \"ECO_RESPONSAVEL\"))%>"
              mask="#*100"
              others="<%=TextHelper.forHtmlAttribute(!podeEditar ? "disabled" : "")%>"
            />
          </div>
          <div class="form-group col-sm-4">
            <label for="codigo"><hl:message key="rotulo.cargo1.empresa.correspondente"/></label>  
            <hl:htmlinput name="ECO_RESP_CARGO"
                type="text"
                classe="form-control"
                value="<%=TextHelper.forHtmlAttribute(empresa != null ? (String) empresa.getAttribute(Columns.ECO_RESP_CARGO): JspHelper.verificaVarQryStr(request, \"ECO_RESP_CARGO\"))%>"
                mask="#*100"
                others="<%=TextHelper.forHtmlAttribute(!podeEditar ? "disabled" : "")%>"
              />
           </div>
           <div class="form-group col-sm-4">
            <label for="codigo"><hl:message key="rotulo.telefone1.empresa.correspondente"/></label>  
            <hl:htmlinput name="ECO_RESP_TELEFONE"
                type="text"
                classe="form-control"
                value="<%=TextHelper.forHtmlAttribute(empresa != null ? (String) empresa.getAttribute(Columns.ECO_RESP_TELEFONE): JspHelper.verificaVarQryStr(request, \"ECO_RESP_TELEFONE\"))%>"
                mask="#*100"
                others="<%=TextHelper.forHtmlAttribute(!podeEditar ? "disabled" : "")%>"
              />
           </div>           
           <div class="form-group col-sm-4">
            <label for="codigo"><hl:message key="rotulo.responsavel2.empresa.correspondente"/></label>
            <hl:htmlinput name="ECO_RESPONSAVEL_2"
              type="text"
              classe="form-control"
              value="<%=TextHelper.forHtmlAttribute(empresa != null ? (String) empresa.getAttribute(Columns.ECO_RESPONSAVEL_2): JspHelper.verificaVarQryStr(request, \"ECO_RESPONSAVEL_2\"))%>"
              mask="#*100"
              others="<%=TextHelper.forHtmlAttribute(!podeEditar ? "disabled" : "")%>"
            />
          </div>
          <div class="form-group col-sm-4">
            <label for="codigo"><hl:message key="rotulo.cargo2.empresa.correspondente"/></label>  
            <hl:htmlinput name="ECO_RESP_CARGO_2"
              type="text"
              classe="form-control"
              value="<%=TextHelper.forHtmlAttribute(empresa != null ? (String) empresa.getAttribute(Columns.ECO_RESP_CARGO_2): JspHelper.verificaVarQryStr(request, \"ECO_RESP_CARGO_2\"))%>"
              size="32"
              mask="#*100"
              others="<%=TextHelper.forHtmlAttribute(!podeEditar ? "disabled" : "")%>"
            />
           </div>
           <div class="form-group col-sm-4">
              <label for="codigo"><hl:message key="rotulo.telefone2.empresa.correspondente"/></label>  
              <hl:htmlinput name="ECO_RESP_TELEFONE_2"
                type="text"
                classe="form-control"
                value="<%=TextHelper.forHtmlAttribute(empresa != null ? (String) empresa.getAttribute(Columns.ECO_RESP_TELEFONE_2): JspHelper.verificaVarQryStr(request, \"ECO_RESP_TELEFONE_2\"))%>"
                size="32"
                mask="#*100"
                others="<%=TextHelper.forHtmlAttribute(!podeEditar ? "disabled" : "")%>"
              />
           </div>          
          <div class="form-group col-sm-4">
            <label for="codigo"><hl:message key="rotulo.responsavel3.empresa.correspondente"/></label>
            <hl:htmlinput name="ECO_RESPONSAVEL_3"
                type="text"
                classe="form-control"
                value="<%=TextHelper.forHtmlAttribute(empresa != null ? (String) empresa.getAttribute(Columns.ECO_RESPONSAVEL_3): JspHelper.verificaVarQryStr(request, \"ECO_RESPONSAVEL_3\"))%>"
                size="32"
                mask="#*100"
                others="<%=TextHelper.forHtmlAttribute(!podeEditar ? "disabled" : "")%>"
              />
          </div>
          <div class="form-group col-sm-4">
            <label for="codigo"><hl:message key="rotulo.cargo3.empresa.correspondente"/></label>  
            <hl:htmlinput name="ECO_RESP_CARGO_3"
                type="text"
                classe="form-control"
                value="<%=TextHelper.forHtmlAttribute(empresa != null ? (String) empresa.getAttribute(Columns.ECO_RESP_CARGO_3): JspHelper.verificaVarQryStr(request, \"ECO_RESP_CARGO_3\"))%>"
                size="32"
                mask="#*100"
                others="<%=TextHelper.forHtmlAttribute(!podeEditar ? "disabled" : "")%>"
              />
           </div>
           <div class="form-group col-sm-4">
              <label for="codigo"><hl:message key="rotulo.telefone3.empresa.correspondente"/></label>  
              <hl:htmlinput name="ECO_RESP_TELEFONE_3"
                type="text"
                classe="form-control"
                value="<%=TextHelper.forHtmlAttribute(empresa != null ? (String) empresa.getAttribute(Columns.ECO_RESP_TELEFONE_3): JspHelper.verificaVarQryStr(request, \"ECO_RESP_TELEFONE_3\"))%>"
                size="32"
                mask="#*100"
                others="<%=TextHelper.forHtmlAttribute(!podeEditar ? "disabled" : "")%>"
              />
           </div> 
        </div>
        <div class="legend">
          <span><hl:message key="rotulo.endereco"/></span>
        </div>
        <div class="row">
          <div class="form-group col-sm-4">
            <label for="codigo"><hl:message key="rotulo.logradouro.empresa.correspondente"/></label>
            <hl:htmlinput name="ECO_LOGRADOURO"
              type="text"
              classe="form-control"
              value="<%=TextHelper.forHtmlAttribute(empresa != null ? (String) empresa.getAttribute(Columns.ECO_LOGRADOURO): JspHelper.verificaVarQryStr(request, \"ECO_LOGRADOURO\"))%>"
              mask="#*100"
              others="<%=TextHelper.forHtmlAttribute(!podeEditar ? "disabled" : "")%>"
            />
          </div>
          <div class="form-group col-sm-2">
            <label for="codigo"><hl:message key="rotulo.numero.empresa.correspondente"/></label>
            <hl:htmlinput name="ECO_NRO"
              type="text"
              classe="form-control"
              value="<%=(String) TextHelper.forHtmlAttribute(empresa!=null && empresa.getAttribute(Columns.ECO_NRO) != null ? empresa.getAttribute(Columns.ECO_NRO): JspHelper.verificaVarQryStr(request, "ECO_NRO"))%>"
              mask="#D11"
              others="<%=(String)(!podeEditar ? "disabled" : "")%>"
            />           
          </div>
          <div class="form-group col-sm-3">
            <label for="codigo"><hl:message key="rotulo.complemento.empresa.correspondente"/></label>
            <hl:htmlinput name="ECO_COMPL"
              type="text"
              classe="form-control"
              value="<%=TextHelper.forHtmlAttribute(empresa != null ? (String) empresa.getAttribute(Columns.ECO_COMPL): JspHelper.verificaVarQryStr(request, "ECO_COMPL"))%>"
              size="32"
              mask="#*40"
              others="<%=TextHelper.forHtmlAttribute(!podeEditar ? "disabled" : "")%>"
            />
          </div>
          <div class="form-group col-sm-3">
            <label for="codigo"><hl:message key="rotulo.bairro.empresa.correspondente"/></label>
            <hl:htmlinput name="ECO_BAIRRO"
              type="text"
              classe="form-control"
              value="<%=TextHelper.forHtmlAttribute(empresa != null ? (String) empresa.getAttribute(Columns.ECO_BAIRRO): JspHelper.verificaVarQryStr(request, \"ECO_BAIRRO\"))%>"
              size="32"
              mask="#*100"
              others="<%=TextHelper.forHtmlAttribute(!podeEditar ? "disabled" : "")%>"
            />
          </div>
        </div>
        <div class="row">
          <div class="form-group col-sm-4">
            <label for="codigo"><hl:message key="rotulo.cidade.empresa.correspondente"/></label>
            <hl:htmlinput name="ECO_CIDADE"
              type="text"
              classe="form-control"
              value="<%=TextHelper.forHtmlAttribute(empresa != null ? (String) empresa.getAttribute(Columns.ECO_CIDADE): JspHelper.verificaVarQryStr(request, \"ECO_CIDADE\"))%>"
              size="32"
              mask="#*100"
              others="<%=TextHelper.forHtmlAttribute(!podeEditar ? "disabled" : "")%>"
            />        
          </div>
          <div class="form-group col-sm-4">
            <label for="codigo"><hl:message key="rotulo.uf.empresa.correspondente"/></label>
            <hl:campoUFv4 name="ECO_UF"
              rotuloUf="rotulo.uf.empresa.correspondente"
              classe="form-control"
              valorCampo="<%=TextHelper.forHtmlAttribute(empresa != null ? (empresa.getAttribute(Columns.ECO_UF) != null ? empresa.getAttribute(Columns.ECO_UF).toString() : JspHelper.verificaVarQryStr(request, \"ECO_UF\")) : "")%>"
              desabilitado="<%=!podeEditar %>"
            />
          </div>
          <div class="form-group col-sm-4">
            <label for="codigo"><hl:message key="rotulo.cep.empresa.correspondente"/></label>
            <hl:htmlinput name="ECO_CEP"
              type="text"
              value="<%=TextHelper.forHtmlAttribute(empresa != null ? (String) empresa.getAttribute(Columns.ECO_CEP): JspHelper.verificaVarQryStr(request, \"ECO_CEP\"))%>"
              size="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCepSize())%>"
              classe="form-control"
              mask="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCepMask())%>"
              others="<%=TextHelper.forHtmlAttribute(!podeEditar ? "disabled" : "")%>"
            />            
          </div>        
        </div>
        <div class="legend">
          <span><hl:message key="rotulo.contato"/></span>
        </div>
        <div class="row">
          <div class="form-group col-sm-4">
            <label for="codigo"><hl:message key="rotulo.telefone.empresa.correspondente"/></label>
            <hl:htmlinput name="ECO_TEL"
              type="text"
              classe="form-control"
              value="<%=TextHelper.forHtmlAttribute(empresa != null ? (String) empresa.getAttribute(Columns.ECO_TEL): JspHelper.verificaVarQryStr(request, \"ECO_TEL\"))%>"
              mask="#*30"
              others="<%=TextHelper.forHtmlAttribute(!podeEditar ? "disabled" : "")%>"
            />
          </div>
          <div class="form-group col-sm-4">
            <label for="codigo"><hl:message key="rotulo.fax.empresa.correspondente"/></label>
            <hl:htmlinput name="ECO_FAX"
              type="text"
              classe="form-control"
              value="<%=TextHelper.forHtmlAttribute(empresa != null ? (String) empresa.getAttribute(Columns.ECO_FAX): JspHelper.verificaVarQryStr(request, \"ECO_FAX\"))%>"
              mask="<%=LocaleHelper.getMultiplosTelefonesMask()%>"
              others="<%=TextHelper.forHtmlAttribute(!podeEditar ? "disabled" : "")%>"
            />
          </div>
        </div>
        <div class="row">
          <div class="form-group col-sm-6">
            <label for="codigo"><hl:message key="rotulo.email.empresa.correspondente"/></label>
            <hl:htmlinput name="ECO_EMAIL"
              di="ECO_EMAIL"
              type="textarea"
              rows="3"
              cols="67"                
              classe="form-control"
              onBlur="formataEmails('ECO_EMAIL')"
              value="<%=TextHelper.forHtmlAttribute(empresa != null ? (String) empresa.getAttribute(Columns.ECO_EMAIL): JspHelper.verificaVarQryStr(request, \"ECO_EMAIL\"))%>"
              size="69"
              mask="#*100"
              others="<%=TextHelper.forHtmlAttribute(!podeEditar ? "disabled" : "")%>"
              nf="submit"
            />
          </div>
        </div>
      </div>
    </div>
    <div class="btn-action">
      <hl:htmlinput name="tipo" type="hidden"  value="editar" />
      <hl:htmlinput name="MM_update" type="hidden"  value="form1" />
  <%if (eco_codigo != null) {%>
      <hl:htmlinput name="ECO_CODIGO" type="hidden" value="<%=TextHelper.forHtmlAttribute(eco_codigo)%>"/>
  <%}%> 
  <%if (podeEditar) {%>
      <a class="btn btn-outline-danger" aria-label='<hl:message key="rotulo.botao.cancelar"/>' href="#no-back" onClick="<%="postData('" + TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)) + "');return false;"%>"><hl:message key="rotulo.botao.cancelar" /></a>
      <a class="btn btn-primary" aria-label='<hl:message key="rotulo.botao.salvar"/>' href="#no-back" onClick="vf_cadastro_empresa_cor('<hl:message key="rotulo.empresacorrespondente.singular"/>'); return false;"><hl:message key="rotulo.botao.salvar"/></a>
  <% } else { %>  
      <a class="btn btn-outline-danger" aria-label='<hl:message key="rotulo.botao.voltar"/>' href="#no-back" onClick="<%="postData('" + TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)) + "');return false;"%>"><hl:message key="rotulo.botao.voltar" /></a>
  <% } %>
    </div>
  </form>  
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript" src="../js/scripts_2810.js"></script>
  <script type="text/JavaScript" src="../js/validaMascara_v4.js"></script>
  <script type="text/JavaScript" src="../js/validaform.js"></script>
  <script type="text/JavaScript" src="../js/validacoes.js"></script>
  <script type="text/JavaScript" src="../js/xbdhtml.js"></script>
  <script type="text/JavaScript" src="../js/listagem.js"></script>
  <script type="text/JavaScript">
    function formLoad() {
      focusFirstField();
    }
    function vf_cadastro_empresa_cor(rotulo)
    {
      var Controles = new Array("ECO_IDENTIFICADOR", "ECO_NOME", "ECO_CNPJ");
      var Msgs = new Array('<hl:message key="mensagem.informe.empresa.correspondente.codigo"/>'.replace('{0}', rotulo),
                   '<hl:message key="mensagem.informe.empresa.correspondente.descricao"/>'.replace('{0}', rotulo),
                   '<hl:message key="mensagem.informe.empresa.correspondente.cnpj"/>'.replace('{0}', rotulo));
      
      if (ValidaCampos(Controles, Msgs)) {
        f0.submit();
      }
    }
    
    function vf_bloqueio_empresa(ecoCodigo, ecoNome, ecoAtivo) {
      var mensagemAlert = ecoAtivo == '1'? '<hl:message key="mensagem.empresa.correspondente.bloqueio.confirma"/>': '<hl:message key="mensagem.empresa.correspondente.desbloqueio.confirma"/>';
      if (confirm(mensagemAlert.replace('{0}',ecoNome))) {
         postData('../v3/manterEmpresaCorrespondente?acao=modificaEmpresa&ECO_CODIGO=' + ecoCodigo + '&ECO_ATIVO=' + ecoAtivo + '&operacao=bloquear&tipo=editar&_skip_history_=true&telaEdicao=S&<%=SynchronizerToken.generateToken4URL(request)%>');
      } 
    }
    
    //field pode ser ID ou NAME
    function formataEmails(field) {
      
        var emailsTag = document.getElementById(field);        
        
        if(!!emailsTag)
        {                  
          console.log(emailsTag);
            document.getElementById(field).value = replaceCaracteresInvalidos(emailsTag.value.trim());        
        }
        else
        {
          var emails = document.getElementsByName(field)[0].value;    
          if(!!emails)
        {           
            emails = replaceCaracteresInvalidos(emails.trim());
            document.getElementsByName(field)[0].value = emails;        
        }           
        }
    }
    
    function replaceCaracteresInvalidos(emailString)
    {
        var emails = emailString;   
        var separador = ',';
        
        emails = replaceAll(emails,' ', separador);
        emails = replaceAll(emails,';', separador);
        emails = replaceAll(emails,'\n',separador); 
        emails = replaceAll(emails,'\t',separador);
        
        while(emails.indexOf(",,") !== -1){
            emails = replaceAll(emails,",,",separador);
        }
        
        if(emails[0] === separador){    // Se o primeiro caracter é vírgula ',' então
            emails = emails.slice(1);   // remove o primeiro caracter.
        }
        
        if(emails.slice(-1) === separador){ // Se o último caracter é vírgula ',' então
            emails = emails.slice(0,-1);    // remove o último caracter.
        }
        
        return emails;
    }
    
    function replaceAll(str, find, replace) {   
      return str.replace(new RegExp(find, 'g'), replace);
    }   
  </script>
  <script type="text/JavaScript">
  	f0 = document.forms[0];
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>