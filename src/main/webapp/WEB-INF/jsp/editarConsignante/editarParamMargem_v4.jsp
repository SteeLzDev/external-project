<%--
* <p>Title: editarParamMargem</p>
* <p>Description: Contem as margens disponiveis</p>
* <p>Copyright: Copyright (c) 2005</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: anderson.assis $
* $Revision: 27720 $
* $Date: 2019-09-05 16:49:17 -0300 (Qui, 05 set 2019) $
--%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.margem.ExibeMargem" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ page import="com.zetra.econsig.helper.usuario.LoginHelper"%>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
boolean podeEditarConsignante = responsavel.temPermissao(CodedValues.FUN_EDT_CONSIGNANTE);
List<MargemTO> lstMargemRaiz = (List<MargemTO>) request.getAttribute("lstMargemRaiz");
%>
<c:set var="title">
  <hl:message key="rotulo.margem.exibicao.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>

<c:set var="bodyContent">
  <form method="post" action="../v3/editarParamMargem?acao=salvar&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><%=TextHelper.forHtmlContent(LoginHelper.getCseNome(responsavel))%></h2>
        </div>
        <div class="card-body">
<%
List<Short> marCodigos = new ArrayList<>();
Iterator<MargemTO> it = lstMargemRaiz.iterator();
while (it.hasNext()) {
  MargemTO margem = it.next();
  Short marCodigo = margem.getMarCodigo();
  if (!marCodigo.equals(CodedValues.INCIDE_MARGEM_NAO)) {
      marCodigos.add(marCodigo);
%>
          <div class="row mt-4">
            <div class="col">
              <div class="legend">
                <span><hl:message key="rotulo.margem.exibicao.legenda"/> <%=TextHelper.forHtmlAttribute(margem.getMarDescricao())%></span>
              </div>
            </div>
          </div>
          <div class="row">
            <div class="form-group col-sm-4">
                <label for="<%=TextHelper.forHtmlAttribute("mar_descricao_"+marCodigo)%>"><hl:message key="rotulo.margem.exibicao.descricao"/></label>
                <hl:htmlinput classe="form-control w-100" di="<%=TextHelper.forHtmlAttribute("mar_descricao_"+marCodigo)%>"
            			  name="<%=TextHelper.forHtmlAttribute("mar_descricao_"+marCodigo)%>"
                          type="text"
                          value="<%=TextHelper.forHtmlAttribute(margem.getMarDescricao())%>"
                          size="20"
                          others="<%=(String)( !podeEditarConsignante ? "disabled" : "")%>"/>
            </div>  
            <div class="form-group col-sm-4">
                <label for="<%=TextHelper.forHtmlAttribute("mar_porcentagem_"+marCodigo)%>"><hl:message key="rotulo.margem.exibicao.porcentagem"/></label>
                <hl:htmlinput classe="form-control w-100"
                          di="<%=TextHelper.forHtmlAttribute("mar_porcentagem_"+marCodigo)%>"
                          name="<%=TextHelper.forHtmlAttribute("mar_porcentagem_"+marCodigo)%>"
                          type="text"
                          value="<%=TextHelper.forHtmlAttribute((margem.getMarPorcentagem() != null) ? NumberHelper.reformat(margem.getMarPorcentagem().toString(), "en", NumberHelper.getLang()) : "")%>" 
                          size="8"
                          onFocus="SetarEventoMascara(this,'#F11',true);" 
                          onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2);tamanhoCampo()}"
                          placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.placeholder.margem.exibicao.porcentagem", responsavel)%>"
                          others="<%=(String)( !podeEditarConsignante ? "disabled" : "")%>"/>
            </div>  
          </div>
          <div class="row">
            <div class="form-group col-sm-4">
              <label for="<%=TextHelper.forHtmlAttribute("mar_exibe_cse_"+marCodigo)%>"><hl:message key="rotulo.consignante.singular"/></label>              
              <select class="form-control form-select" id="<%=TextHelper.forHtmlAttribute("mar_exibe_cse_"+marCodigo)%>" name="mar_exibe_cse_<%=(Short)marCodigo%>" onFocus="SetarEventoMascara(this,'#A40',true);" onBlur="fout(this);ValidaMascara(this);" <%=(String)(!podeEditarConsignante ? "disabled" : "")%>>
                <option value="<%=(Character)ExibeMargem.NAO_EXIBE%>" <%=(String)(ExibeMargem.NAO_EXIBE.equals(margem.getMarExibeCse()) ? "selected" : "")%>><hl:message key="rotulo.margem.exibicao.nao"/></option>
                <option value="<%=(Character)ExibeMargem.EXIBE_ZERO_QUANDO_NEGATIVA%>" <%=(String)(ExibeMargem.EXIBE_ZERO_QUANDO_NEGATIVA.equals(margem.getMarExibeCse()) ? "selected" : "")%>><hl:message key="rotulo.margem.exibicao.zero.negativo"/></option>
                <option value="<%=(Character)ExibeMargem.EXIBE_SEM_RESTRICAO%>" <%=(String)(ExibeMargem.EXIBE_SEM_RESTRICAO.equals(margem.getMarExibeCse()) ? "selected" : "")%>><hl:message key="rotulo.margem.exibicao.sem.restricao"/></option>
              </select>
            </div>
            <div class="form-group col-sm-4">
              <label for="<%=TextHelper.forHtmlAttribute("mar_exibe_csa_"+marCodigo)%>"><hl:message key="rotulo.consignataria.singular"/></label>             
              <select class="form-control form-select" id="<%=TextHelper.forHtmlAttribute("mar_exibe_csa_"+marCodigo)%>" name="mar_exibe_csa_<%=(Short)marCodigo%>" onFocus="SetarEventoMascara(this,'#A40',true);" onBlur="fout(this);ValidaMascara(this);" <%=(String)(!podeEditarConsignante ? "disabled" : "")%>>
                <option value="<%=(Character)ExibeMargem.NAO_EXIBE%>" <%=(String)(ExibeMargem.NAO_EXIBE.equals(margem.getMarExibeCsa()) ? "selected" : "")%>><hl:message key="rotulo.margem.exibicao.nao"/></option>
                <option value="<%=(Character)ExibeMargem.EXIBE_ZERO_QUANDO_NEGATIVA%>" <%=(String)(ExibeMargem.EXIBE_ZERO_QUANDO_NEGATIVA.equals(margem.getMarExibeCsa()) ? "selected" : "")%>><hl:message key="rotulo.margem.exibicao.zero.negativo"/></option>
                <option value="<%=(Character)ExibeMargem.EXIBE_SEM_RESTRICAO%>" <%=(String)(ExibeMargem.EXIBE_SEM_RESTRICAO.equals(margem.getMarExibeCsa()) ? "selected" : "")%>><hl:message key="rotulo.margem.exibicao.sem.restricao"/></option>
              </select>
            </div>
            <div class="form-group col-sm-4">
              <label for="<%=TextHelper.forHtmlAttribute("mar_exibe_ser_"+marCodigo)%>"><hl:message key="rotulo.servidor.singular"/></label>              
              <select class="form-control form-select" id="<%=TextHelper.forHtmlAttribute("mar_exibe_ser_"+marCodigo)%>" name="mar_exibe_ser_<%=(Short)marCodigo%>" onFocus="SetarEventoMascara(this,'#A40',true);" onBlur="fout(this);ValidaMascara(this);" <%=(String)(!podeEditarConsignante ? "disabled" : "")%>>
                <option value="<%=(Character)ExibeMargem.NAO_EXIBE%>" <%=(String)(ExibeMargem.NAO_EXIBE.equals(margem.getMarExibeSer()) ? "selected" : "")%>><hl:message key="rotulo.margem.exibicao.nao"/></option>
                <option value="<%=(Character)ExibeMargem.EXIBE_ZERO_QUANDO_NEGATIVA%>" <%=(String)(ExibeMargem.EXIBE_ZERO_QUANDO_NEGATIVA.equals(margem.getMarExibeSer()) ? "selected" : "")%>><hl:message key="rotulo.margem.exibicao.zero.negativo"/></option>
                <option value="<%=(Character)ExibeMargem.EXIBE_SEM_RESTRICAO%>" <%=(String)(ExibeMargem.EXIBE_SEM_RESTRICAO.equals(margem.getMarExibeSer()) ? "selected" : "")%>><hl:message key="rotulo.margem.exibicao.sem.restricao"/></option>
              </select>
            </div>
          </div>
          <div class="row">
            <div class="form-group col-sm-4">
              <label for="<%=TextHelper.forHtmlAttribute("mar_exibe_org_"+marCodigo)%>"><hl:message key="rotulo.orgao.singular"/></label>
              <select class="form-control form-select" id="<%=TextHelper.forHtmlAttribute("mar_exibe_org_"+marCodigo)%>" name="mar_exibe_org_<%=(Short)marCodigo%>" onFocus="SetarEventoMascara(this,'#A40',true);" onBlur="fout(this);ValidaMascara(this);" <%=(String)(!podeEditarConsignante ? "disabled" : "")%>>
                <option value="<%=(Character)ExibeMargem.NAO_EXIBE%>" <%=(String)(ExibeMargem.NAO_EXIBE.equals(margem.getMarExibeOrg()) ? "selected" : "")%>><hl:message key="rotulo.margem.exibicao.nao"/></option>
                <option value="<%=(Character)ExibeMargem.EXIBE_ZERO_QUANDO_NEGATIVA%>" <%=(String)(ExibeMargem.EXIBE_ZERO_QUANDO_NEGATIVA.equals(margem.getMarExibeOrg()) ? "selected" : "")%>><hl:message key="rotulo.margem.exibicao.zero.negativo"/></option>
                <option value="<%=(Character)ExibeMargem.EXIBE_SEM_RESTRICAO%>" <%=(String)(ExibeMargem.EXIBE_SEM_RESTRICAO.equals(margem.getMarExibeOrg()) ? "selected" : "")%>><hl:message key="rotulo.margem.exibicao.sem.restricao"/></option>
              </select>
            </div>
            <div class="form-group col-sm-4">
              <label for="<%=TextHelper.forHtmlAttribute("mar_exibe_cor_"+marCodigo)%>"><hl:message key="rotulo.correspondente.singular"/></label>
              <select class="form-control form-select" id="<%=TextHelper.forHtmlAttribute("mar_exibe_cor_"+marCodigo)%>" name="mar_exibe_cor_<%=(Short)marCodigo%>" onFocus="SetarEventoMascara(this,'#A40',true);" onBlur="fout(this);ValidaMascara(this);" <%=(String)(!podeEditarConsignante ? "disabled" : "")%>>
                <option value="<%=(Character)ExibeMargem.NAO_EXIBE%>" <%=(String)(ExibeMargem.NAO_EXIBE.equals(margem.getMarExibeCor()) ? "selected" : "")%>><hl:message key="rotulo.margem.exibicao.nao"/></option>
                <option value="<%=(Character)ExibeMargem.EXIBE_ZERO_QUANDO_NEGATIVA%>" <%=(String)(ExibeMargem.EXIBE_ZERO_QUANDO_NEGATIVA.equals(margem.getMarExibeCor()) ? "selected" : "")%>><hl:message key="rotulo.margem.exibicao.zero.negativo"/></option>
                <option value="<%=(Character)ExibeMargem.EXIBE_SEM_RESTRICAO%>" <%=(String)(ExibeMargem.EXIBE_SEM_RESTRICAO.equals(margem.getMarExibeCor()) ? "selected" : "")%>><hl:message key="rotulo.margem.exibicao.sem.restricao"/></option>
              </select>
            </div>
            <div class="form-group col-sm-4">
              <label for="<%=TextHelper.forHtmlAttribute("mar_exibe_sup_"+marCodigo)%>"><hl:message key="rotulo.suporte.singular"/></label>
              <select class="form-control form-select" id="<%=TextHelper.forHtmlAttribute("mar_exibe_sup_"+marCodigo)%>" name="mar_exibe_sup_<%=(Short)marCodigo%>" onFocus="SetarEventoMascara(this,'#A40',true);" onBlur="fout(this);ValidaMascara(this);" <%=(String)(!podeEditarConsignante ? "disabled" : "")%>>
                <option value="<%=(Character)ExibeMargem.NAO_EXIBE%>" <%=(String)(ExibeMargem.NAO_EXIBE.equals(margem.getMarExibeSup()) ? "selected" : "")%>><hl:message key="rotulo.margem.exibicao.nao"/></option>
                <option value="<%=(Character)ExibeMargem.EXIBE_ZERO_QUANDO_NEGATIVA%>" <%=(String)(ExibeMargem.EXIBE_ZERO_QUANDO_NEGATIVA.equals(margem.getMarExibeSup()) ? "selected" : "")%>><hl:message key="rotulo.margem.exibicao.zero.negativo"/></option>
                <option value="<%=(Character)ExibeMargem.EXIBE_SEM_RESTRICAO%>" <%=(String)(ExibeMargem.EXIBE_SEM_RESTRICAO.equals(margem.getMarExibeSup()) ? "selected" : "")%>><hl:message key="rotulo.margem.exibicao.sem.restricao"/></option>
              </select>
            </div>
          </div>
<%  
    }
  }
%>
        </div>
      </div>
      <div class="btn-action">
<% if (podeEditarConsignante) { %>
       <input type="hidden" name="tipo" value="editar">
       <input type="hidden" name="MAR_CODIGOS" value="<%=TextHelper.forHtmlAttribute(TextHelper.join(marCodigos, ","))%>">     
       <button class="btn btn-outline-danger" name="btnCancelar" onClick="postData('../v3/editarConsignante?acao=iniciar&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>'); return false;" value="Cancelar"><hl:message key="rotulo.botao.cancelar"/></button>
       <button type="button" class="btn btn-primary" name="btnSalvar" aria-label='<hl:message key="rotulo.botao.salvar"/>' onClick="validaForm(); return false;"><hl:message key="rotulo.botao.salvar"/></button>
<%} else {%>
      <button class="btn btn-primary" name="Button" onClick="postData('../v3/editarConsignante?acao=iniciar&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>'); return false;" value="Voltar"><hl:message key="rotulo.botao.voltar"/></button>
<% } %>
      </div>      
  </form>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript">
    function formLoad() {
      focusFirstField();
    }
  
    function validaForm() {
      var marCodigos = f0.MAR_CODIGOS.value.split(',');
      var descField;
      for (var i=0; i < marCodigos.length; i++) {
        descField = document.getElementById('mar_descricao_' + marCodigos[i]);
        if (descField.value == '') {
          alert('<hl:message key="mensagem.informe.margem.exibicao.descricao"/>');
          descField.focus();
          return false;
        }
      }
	  f0.submit();
    }

    function tamanhoCampo(){
    	var marCodigos = f0.MAR_CODIGOS.value.split(',');
        var percentual;
        for (var i=0; i < marCodigos.length; i++) {
        	percentual = document.getElementById('mar_porcentagem_' + marCodigos[i]).value;
        	percentual = parseFloat(percentual);
            if (percentual > 100){
            	$("#mar_porcentagem_" + marCodigos[i]).val("100");
            }
        }
    }
    
    window.onload = formLoad();
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