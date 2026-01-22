<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.regex.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
  List<?> consignatarias = (List<?>) request.getAttribute("consignatarias");
  boolean exibeCaptcha = (boolean) request.getAttribute("exibeCaptcha");
  boolean exibeCaptchaAvancado = (boolean) request.getAttribute("exibeCaptchaAvancado");
  boolean exibeCaptchaDeficiente = (boolean) request.getAttribute("exibeCaptchaDeficiente");
  List<?> naturezas = (List<?>) request.getAttribute("naturezas");
%>
<c:set var="title">
  <hl:message key="rotulo.upload.generico.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-upload"></use>
</c:set>
<c:set var="bodyContent">
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.upload.arquivo.titulo"/></h2>
    </div>
    <div class="alert-warning" role="alert"><br/><hl:message key="mensagem.alerta.upload.generico"/><br/>&nbsp;</div>

  </div>
  <div class="card-body">
    <FORM NAME="form1" METHOD="POST" ACTION="../v3/uploadArquivoGenerico?acao=upload&<%=SynchronizerToken.generateToken4URL(request)%>" ENCTYPE="multipart/form-data">
<!-- selecionar arquivo -->
      <div class="row">
        <div class="form-group col-sm-6">
          <label for="FILE1"><hl:message key="rotulo.comunicacao.anexo.arquivo"/></label>
          <input type="file" class="form-control" id="FILE1" name="FILE1">
        </div>
      </div>
<!-- selecionar mais de uma consignataria -->
        <div class="row">
          <div class="form-group col-sm-6">
            <label for="csa_codigo_aux"><hl:message key="rotulo.consignataria.singular"/></label>
            <SELECT class="form-control" name="csa_codigo_aux" id="csa_codigo_aux" size="4" multiple>
              <OPTION VALUE=""><hl:message key="rotulo.campo.todas"/></OPTION>
              <%
              Iterator<?> it = consignatarias.iterator();
              CustomTransferObject csa = null;
              String csa_nome = null;
              while (it.hasNext()) {
                csa = (CustomTransferObject)it.next();

                csa_nome = (String)csa.getAttribute(Columns.CSA_NOME_ABREV);
                if (csa_nome == null || csa_nome.trim().length() == 0)
                  csa_nome = csa.getAttribute(Columns.CSA_NOME).toString();
              %>
             <OPTION VALUE="<%=TextHelper.forHtmlAttribute(csa.getAttribute(Columns.CSA_CODIGO))%>"><%=TextHelper.forHtml(csa_nome)%></OPTION>
              <%
                }
              %>
            </select>
          </div>
          <div class="form-group col-sm-6">
            <label for="nse_codigo_aux"><hl:message key="rotulo.param.svc.natureza.servico"/></label>
            <SELECT class="form-control" name="nse_codigo_aux" id="nse_codigo_aux" size="4" onclick="alterouVlrNatureza()" multiple>
              <OPTION VALUE=""><hl:message key="rotulo.campo.todas"/></OPTION>
              <%
              Iterator<?> itnatureza = naturezas.iterator();
              CustomTransferObject natureza = null;
              String nse_descricao = null;
              while (itnatureza.hasNext()) {
                  natureza = (CustomTransferObject)itnatureza.next();
                  nse_descricao = (String)natureza.getAttribute(Columns.NSE_DESCRICAO);
              %>
             <OPTION VALUE="<%=TextHelper.forHtmlAttribute(natureza.getAttribute(Columns.NSE_CODIGO))%>"><%=TextHelper.forHtml(nse_descricao)%></OPTION>
              <%
                }
              %>
            </select>
          </div>
        </div>
        <div class="row">
            <div class="slider col-sm-6 col-md-6 mb-2">
              <div class="tooltip-inner"><hl:message key="mensagem.upload.generico.selecionar.multiplas"/></div>
            </div>
            <div class="slider col-sm-6 col-md-6 mb-2">
              <div class="tooltip-inner"><hl:message key="mensagem.upload.generico.selecionar.multiplas"/></div>
            </div>
        </div>
<!-- Captcha -->
        <div class="row">
          <% if (exibeCaptcha || exibeCaptchaDeficiente) { %>
            <div class="form-group col-sm-5">
              <label for="captcha"><hl:message key="rotulo.captcha.codigo"/>:</label>
              <input type="text" class="form-control" id="captcha" name="captcha" placeholder='<hl:message key="mensagem.informacao.login.digite.codigo.acesso"/>'>
            </div>
           <% } %>
           <div class="form-group col-sm-6">
             <div class="captcha">
               <% if (exibeCaptcha) { %>
                 <img name="captcha_img" src="../captcha.jpg?t=<%=DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss")%>" alt='<hl:message key="rotulo.captcha.codigo"/>' title='<hl:message key="rotulo.captcha.codigo"/>'/>
                 <a href="#no-back" onclick="reloadCaptcha()"><img src="../img/icones/refresh.png" alt='<hl:message key="rotulo.captcha.novo.codigo"/>' title='<hl:message key="rotulo.captcha.novo.codigo"/>' border="0"/></a>
                 <a href="#no-back" class="btn-i-right pr-1" data-bs-toggle="popover" title="<hl:message key="rotulo.ajuda" />"
                   data-bs-content='<hl:message key="mensagem.ajuda.captcha.operacao.imagem.v3"/>'
                   data-original-title=<hl:message key="rotulo.ajuda" />>
                   <img src="../img/icones/help.png" alt='<hl:message key="rotulo.ajuda" />' title='<hl:message key="rotulo.ajuda" />' border="0">
                 </a>
               <% } else if (exibeCaptchaAvancado) { %>
                 <hl:recaptcha />
               <% } else if (exibeCaptchaDeficiente) {%>
                    <div id="divCaptchaSound"></div>
                    <a href="#no-back" onclick="reloadSimpleCaptcha()"><img src="../img/icones/refresh.png" alt='<hl:message key="rotulo.captcha.novo.audio"/>' title='<hl:message key="rotulo.captcha.novo.audio"/>' border="0"/></a>
                    <a href="#no-back" onclick="helpCaptcha3();"><img src="../img/icones/help.png" alt='<hl:message key="rotulo.ajuda"/>' title='<hl:message key="rotulo.ajuda"/>' border="0"/></a>
               <% } %>
            </div>
           </div>
        </div>
        <input name="FORM" type="hidden" value="form1">
        <INPUT NAME="CSA_CODIGO" TYPE="HIDDEN" VALUE="">
        <input name="NSE_CODIGO" type="hidden" value="">
		<!-- botão confirmar e voltar -->
        <div class="btn-action">
          <a class="btn btn-outline-danger" aria-label="<hl:message key="rotulo.botao.voltar"/>" href="#" onClick="postData('../v3/carregarPrincipal')"><hl:message key="rotulo.botao.voltar"/></a>
          <button class="btn btn-primary" type="submit" onClick="if(vf_upload_arquivos()){ vf_escolha_nse(); f0.submit();} return false"><svg width="17"><use xlink:href="#i-confirmar"></use></svg> <hl:message key="rotulo.botao.confirmar"/></button>
        </div>
      </FORM>
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
</c:set>
<c:set var="javascript">
<% if (exibeCaptchaAvancado) { %>
<script src='https://www.google.com/recaptcha/api.js'></script>
<% } %>
  <script type="text/JavaScript">
    var f0 = document.forms[0];

    <% if (exibeCaptcha || exibeCaptchaDeficiente || exibeCaptchaAvancado) { %>
         document.getElementById('captcha').blur();
    <% } %>
  </script>
  <script type="text/JavaScript">
  function formLoad() {
    f0.FILE1.focus();
    <% if (exibeCaptchaDeficiente) {%>
    montaCaptchaSom();
    <% } %>
  }
  function vf_upload_arquivos() {
    var controles = new Array("FILE1");
    var msgs = new Array ('<%=ApplicationResourcesHelper.getMessage("mensagem.upload.generico.arquivo", responsavel)%>');
    var csaCodigo = '';
    var complemento = '';

    if (f0.csa_codigo_aux != null) {
      for (i = 0 ; i < f0.csa_codigo_aux.length ; i++) {
        if (f0.csa_codigo_aux.options[i].selected) {
          csaCodigo += complemento;
          csaCodigo += (f0.csa_codigo_aux.options[i].value);
          complemento = ',';
        }
      }
      f0.CSA_CODIGO.value = csaCodigo;
    }

    var ok = ValidaCampos(controles, msgs)
    if (ok) {
    	$('#modalAguarde').modal({
    	    backdrop: 'static',
    	    keyboard: false
    	});
    }
    return ok;
  }

  function alterouVlrNatureza() {
	    if (f0.nse_codigo_aux.value != null && f0.nse_codigo_aux.value != "") {
	        f0.csa_codigo_aux.disabled=true;
	        limparCombo(document.forms[0].csa_codigo_aux);
	      } else {
	        f0.csa_codigo_aux.disabled=false;
	      }
	}

  function vf_escolha_nse() {
	  var nseCodigo = '';
	  var complemento = '';

	  if (f0.nse_codigo_aux != null) {
	    for (i = 0 ; i < f0.nse_codigo_aux.length ; i++) {
	      if (f0.nse_codigo_aux.options[i].selected) {
    	    nseCodigo += complemento;
    	    nseCodigo += (f0.nse_codigo_aux.options[i].value);
	        complemento = ',';
	      }
	    }
	    f0.NSE_CODIGO.value = nseCodigo;
	  }
	}

    window.onload = formLoad;
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>