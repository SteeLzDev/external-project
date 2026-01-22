<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="java.io.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.FaqTO"%>
<%@ page import="com.zetra.econsig.persistence.entity.CategoriaFaq"%>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@ taglib prefix="t"     tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl"    uri="/html-lib" %>
<%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />

<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
  List<CategoriaFaq> lstCategoria = (List<CategoriaFaq>) request.getAttribute("lstCategorias");
  FaqTO faqCafTO = (FaqTO) request.getAttribute("faqTO");
%>

<c:set var="title">
  <c:choose>
    <c:when test="${operacao == 'inserir'}">
      <hl:message key="rotulo.criar.faq.titulo"/>
    </c:when>
    <c:otherwise>
      <hl:message key="rotulo.editar.faq.titulo"/>
    </c:otherwise>
  </c:choose>
</c:set>

<link type="text/css" rel="stylesheet" href="../css/uedit.ui.css" />
<link type="text/css" rel="stylesheet" href="../css/uedit.ui.complete.css" />
<script type="text/JavaScript" src="../js/editorFaq.js?<hl:message key="release.tag"/>"></script>
<script type="text/javascript" src="../js/uedit.js?<hl:message key="release.tag"/>"></script>
<script type="text/javascript" src="../js/uedit.ui.complete.js?<hl:message key="release.tag"/>"></script>

<c:set var="imageHeader">
  <use xlink:href="#i-sistema"></use>
</c:set>
<c:set var="bodyContent">
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title">
        <c:choose>
          <c:when test="${operacao == 'inserir'}">
            <hl:message key="rotulo.criar.faq.titulo"/>
          </c:when>
          <c:otherwise>
            <hl:message key="rotulo.editar.faq.titulo"/>
          </c:otherwise>
        </c:choose>
      </h2>
    </div>
    <div class="card-body">
      <form method="post" action="../v3/manterFaq?acao=salvar&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
        <c:choose>
          <c:when test="${operacao != 'inserir'}">
            <h3 class="legend">
          <span>
            ${fl:forHtmlContent(faqTO.faqTitulo1)}
          </span>
        </h3>
          </c:when>
        </c:choose>
        
        <div class="form-group col-sm-12 col-md-12">
          <div class="row col-sm-12 col-md-6">
            <label for="faqTituloPrincipal"><hl:message key="rotulo.faq.titulo.principal"/></label>
            <hl:htmlinput name="faqTitulo1" di="faqTituloPrincipal" type="text" classe="form-control" value="${fl:forHtmlContent(faqTO.faqTitulo1)}" size="32" mask="#M100" placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.faq.placeholder.titulo.principal", responsavel)%>" />
          </div>
        </div>
        <div class="form-group col-sm-12 col-md-12">
          <div class="row col-sm-12 col-md-6">
            <label for="tituloAlternativo"><hl:message key="rotulo.faq.titulo.alternativo"/></label>
            <hl:htmlinput name="faqTitulo2" di="tituloAlternativo" type="text" classe="form-control" value="${fl:forHtmlContent(faqTO.faqTitulo2)}" size="32" mask="#M100" placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.faq.placeholder.titulo.alternativo", responsavel)%>" />
          </div>
        </div>
        <div class="form-group col-sm-12 col-md-12">
          <div class="row col-sm-12 col-md-6">
            <label for="sequencia"><hl:message key="rotulo.faq.sequencia"/></label>
            <hl:htmlinput name="faqSequencia" di="sequencia" type="text" classe="form-control" value="${fl:forHtmlContent(faqTO.faqSequencia != null ? faqTO.faqSequencia : '')}" size="6" mask="#D4" placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.faq.placeholder.sequencia", responsavel)%>" />
          </div>
        </div>
        <div class="row">
          <div class="col-sm-12 col-md-12">
            <h3 class="legend">
              <span id="operacional"><hl:message key="rotulo.faq.exibir.para"/></span>
            </h3>
            <div class="form-check">
              <div class="row" role="group" aria-labelledby="operacional">
                <div class="col-sm-12 col-md-6">
                  <input class="form-check-input ml-4" id="consignante" name="faqExibeCse" type="checkbox" value="<%=(String)CodedValues.TPC_SIM%>" ${faqTO.faqExibeCse == 'S' ? 'CHECKED' : ''}>
                  <label class="form-check-label ml-4" for="consignante">
                    <span class="text-nowrap align-text-top"><hl:message key="rotulo.consignante.singular"/></span>
                  </label>
                </div>
                <div class="col-sm-12 col-md-6">
                  <input class="form-check-input ml-4" id="servidor"name="faqExibeSer" type="checkbox" value="<%=(String)CodedValues.TPC_SIM%>" ${faqTO.faqExibeSer == 'S' ? 'CHECKED' : ''}>
                  <label class="form-check-label ml-4" for="servidor">
                    <span class="text-nowrap align-text-top"><hl:message key="rotulo.servidor.singular"/></span>
                  </label>
                </div>
                <div class="col-sm-12 col-md-6">
                  <input class="form-check-input ml-4" id="orgao" name="faqExibeOrg" type="checkbox" value="<%=(String)CodedValues.TPC_SIM%>" ${faqTO.faqExibeOrg == 'S' ? 'CHECKED' : ''}>
                  <label class="form-check-label ml-4" for="orgao">
                    <span class="text-nowrap align-text-top"><hl:message key="rotulo.orgao.singular"/></span>
                  </label>
                </div>
                <div class="col-sm-12 col-md-6">
                  <input class="form-check-input ml-4" id="correspondente" name="faqExibeCor" type="checkbox" value="<%=(String)CodedValues.TPC_SIM%>" ${faqTO.faqExibeCor == 'S' ? 'CHECKED' : ''}>
                  <label class="form-check-label ml-4" for="correspondente">
                    <span class="text-nowrap align-text-top"><hl:message key="rotulo.correspondente.singular"/></span>
                  </label>
                </div>
                <div class="col-sm-12 col-md-6">
                  <input class="form-check-input ml-4" id="consignataria" name="faqExibeCsa" type="checkbox" value="<%=(String)CodedValues.TPC_SIM%>" ${faqTO.faqExibeCsa == 'S' ? 'CHECKED' : ''}>
                  <label class="form-check-label ml-4" for="consignataria">
                    <span class="text-nowrap align-text-top"><hl:message key="rotulo.consignataria.singular"/></span>
                  </label>
                </div>
                <div class="col-sm-12 col-md-6">
                  <input class="form-check-input ml-4" id="suporte" name="faqExibeSup" type="checkbox" value="<%=(String)CodedValues.TPC_SIM%>" ${faqTO.faqExibeSup == 'S' ? 'CHECKED' : ''}>
                  <label class="form-check-label ml-4" for="suporte">
                    <span class="text-nowrap align-text-top"><hl:message key="rotulo.suporte.singular"/></span>
                  </label>
                </div>
                <div class="col-sm-12 col-md-6">
                  <input class="form-check-input ml-4" id="todos" name="faqCheckTodos" type="checkbox" onclick="checkTodos()" value="S">
                  <label class="form-check-label ml-4" for="todos">
                    <span class="text-nowrap align-text-top font-weight-bold"><hl:message key="rotulo.campo.todos.simples"/></span>
                  </label>
                </div>
              </div>
            </div>
            <div class="row">
              <div class="col-sm-12 col-md-12">
            <div class="legend">
              <span id="isExibeMobile"><hl:message key="rotulo.faq.exibir.mobile"/></span>
            </div>
            <div class="form-group ml-3" role="radiogroup" aria-labelledby="isExibeMobile">
              <div class="form-check form-check-inline">
                <input class="form-check-input ml-1" type="radio" name="exibeMobile" id="exibeMobileS" value="S" ${faqTO.faqExibeMobile == 'S' ? 'CHECKED' : ''}>
                <label class="form-check-label labelSemNegrito pr-4" for="exibeMobileS"><hl:message key="rotulo.sim"/></label>
              </div>
              <div class="form-check form-check-inline">
              <input class="form-check-input ml-1" type="radio" name="exibeMobile" id="exibeMobileN" value="N" ${faqTO.faqExibeMobile == 'N' ? 'CHECKED' : ''}>
                <label class="form-check-label labelSemNegrito pr-4" for="exibeMobileN"><hl:message key="rotulo.nao"/></label>
              </div>
            </div>
              </div>
            </div>
            <%if(lstCategoria != null && !lstCategoria.isEmpty()) {%>
                <div class="form-group col-sm-12  col-md-6">
                  <label for="categoria"><hl:message key="rotulo.faq.categoria"/></label>
                  <select name="cafCodigo" id="categoria" class="form-control form-select" onFocus="SetarEventoMascara(this,'#*200',true);"  onBlur="fout(this);ValidaMascara(this);"  >
                    <option value="" ><hl:message key="rotulo.campo.selecione"/></option>
                    <%
                    for (CategoriaFaq categoria : lstCategoria){
                        String cafCodigo = categoria.getCafCodigo();
                        String cafDesricao = categoria.getCafDescricao();
                        String cafCodigoAtual = (String) faqCafTO.getCafCodigo();
                      %>
                        <option value="<%=TextHelper.forHtmlAttribute(categoria.getCafCodigo())%>" <%=(String)((!TextHelper.isNull(cafCodigoAtual) && cafCodigoAtual.equals(cafCodigo)) ? "SELECTED" : "")%>><%=TextHelper.forHtmlContent(cafDesricao)%></option>
                      <%
                      }
                      %>
                  </select>
                </div>
            <%} %>
            <h3 class="legend">
              <span id="operacional"><hl:message key="rotulo.faq.titulo"/></span>
            </h3>
            <div class="form-group">
              <ul id="uedit_button_strip"></ul>
              <textarea name="innerTemp" cols="80" rows="30" class="form-control" id="uedit_textarea" onFocus="SetarEventoMascara(this,'#*65000',true);" onBlur="fout(this);ValidaMascara(this);">${operacao == 'inserir' ? '' : fl:forHtmlContent(faqTO.faqTexto)}</textarea>
            </div>             
            <hl:htmlinput name="faqCodigo" type="hidden" value="${fl:forHtmlContent(faqTO.faqCodigo)}"/>
            <input type="hidden" name="innerTemp" value=""/>
            <input type="hidden" name="operacao" value="${operacao}"/>
          </div>
        </div>
      </form>
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger pl-4 pr-4" id="btnVoltar" href="#no-back" onClick="<%="postData('../v3/manterFaq?acao=iniciar&" + SynchronizerToken.generateToken4URL(request) + "'); return false;"%>"><hl:message key="rotulo.botao.cancelar"/></a>
    <a class="btn btn-primary" id="btnSalvar" href="#no-back" onClick="if(validaCampos() && validaCampoConteudo()){f0.submit();} return false;"><hl:message key="rotulo.botao.salvar"/></a>
  </div>                                              
</c:set>
<c:set var="javascript">
  <script id="MainScript">
  	var f0 = document.forms[0];
	
  	function formLoad() {
    	focusFirstField();
  	}
  	var uedit_textarea = document.getElementById("uedit_textarea");
    var uedit_button_strip = document.getElementById("uedit_button_strip");
    var ueditorInterface = ueditInterface(uedit_textarea, uedit_button_strip);
    
    window.onload = formLoad;
 </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>