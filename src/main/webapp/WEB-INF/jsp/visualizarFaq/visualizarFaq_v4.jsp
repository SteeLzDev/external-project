<%@page import="java.util.List"%>
<%@page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@page import="com.zetra.econsig.helper.web.JspHelper"%>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%
int filtro = (int) request.getAttribute("filtro");
%>
<c:set var="imageHeader">
    <use xlink:href="#i-ajuda"></use>
</c:set>
<c:set var="title">
   <%= request.getAttribute("tituloPagina") %>
</c:set>
<c:set var="bodyContent">
<form name="form1" method="post" action="../v3/visualizarFaq?acao=salvarAvaliacaoFaq&<%=SynchronizerToken.generateToken4URL(request)%>">
      <div class="card">
        <div class="card-header hasIcon pl-3">
          <h2 class="card-header-title"><hl:message key="rotulo.acao.pesquisar"/></h2>
        </div>
        <div class="card-body">
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="iFiltro"><hl:message key="rotulo.faq.filtro"/></label>
                <input type="text" class="form-control" id="iFiltro" name="FILTRO" placeholder="<hl:message key="rotulo.acao.digite.filtro"/>" size="10" value="" onFocus="SetarEventoMascara(this,'#*200',true);"  onBlur="fout(this);ValidaMascara(this);">
              </div>
            </div>
            <input type="hidden" name="faqCodigo" id="faqCodigo" value="">
            <input type="hidden" name="avfCodigo" id="avfCodigo" value="">
        </div>
      </div>
      <div class="btn-action">
        <a class="btn btn-primary" href="#no-back" onClick = "pesquisarAvaliacaoFaq();">
          <svg width="20">
            <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-consultar"></use>
          </svg>
          <hl:message key="rotulo.botao.pesquisar"/>
        </a>
      </div>
  <%
    AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
    List faqs = (List) request.getAttribute("faqs");
  %>
      <% 
        String nomeComponente = "";
        for (int i=0; i < faqs.size(); i++) { 
          CustomTransferObject faqTO = (CustomTransferObject)faqs.get(i);
          String faqTitulo1 = faqTO.getAttribute(Columns.FAQ_TITULO_1).toString(); 
          String faqTitulo2 = faqTO.getAttribute(Columns.FAQ_TITULO_2).toString(); 
          String faqTexto = faqTO.getAttribute(Columns.FAQ_TEXTO).toString();
          String faqCodigo = faqTO.getAttribute(Columns.FAQ_CODIGO).toString();
          String avfNota = (String) faqTO.getAttribute(Columns.AVF_NOTA);
          String avfComentario = (String) faqTO.getAttribute(Columns.AVF_COMENTARIO);
          String avfCodigo = "";
          if (!TextHelper.isNull(faqTO.getAttribute(Columns.AVF_CODIGO))) {
        	  avfCodigo = (String) faqTO.getAttribute(Columns.AVF_CODIGO);
          }
          nomeComponente = "FAQ_"+i;
          if (faqTexto != null && !faqTexto.equals("")) {
      %>
      <div class="question">
        <a class="question-head" onclick="verificaCampoObs('<%=TextHelper.forJavaScriptAttribute(faqCodigo)%>'); desabillitaCampoObs('<%=TextHelper.forJavaScriptAttribute(faqCodigo)%>', '<%=TextHelper.forJavaScriptAttribute(avfComentario)%>');" href="#faq<%=i%>" data-bs-toggle="collapse" aria-expanded="false" aria-controls="faq<%=i%>">
          <%=TextHelper.forHtmlContent(faqTitulo1)%>
        </a>
        <div class="collapse" id="faq<%=i%>">
          <div class="question-body">
            <p><%=faqTexto%></p>
            <div class="form-group mb-0">
              <span id="infoUteis"><hl:message key="rotulo.faq.pergunta.informacoes.uteis"/></span>
            </div>
            <div class="form-check form-check-inline pt-2 custom-controls-stacked mt-2 d-block" aria-labelledby="infoUteis">
             <input class="form-check-input ml-1" type="radio" name="avaliacaoFaq_<%=TextHelper.forJavaScriptAttribute(faqCodigo)%>" id="avaliacaoFaqSim_<%=TextHelper.forJavaScriptAttribute(faqCodigo)%>" value="1" onchange="exibeCampoObs(this, '<%=TextHelper.forJavaScriptAttribute(faqCodigo)%>')" <%=(String)((avfNota != null && avfNota.equals("1")) ? "CHECKED":"")%> >
               <label class="form-check-label labelSemNegrito ml-1 pl-4 pr-1" for="avaliacaoFaqSim"><hl:message key="rotulo.sim"/></label>
            </div>
              <div class="form-check-inline form-check">
                <input class="form-check-input ml-1" type="radio" name="avaliacaoFaq_<%=TextHelper.forJavaScriptAttribute(faqCodigo)%>" id="avaliacaoFaqNao_<%=TextHelper.forJavaScriptAttribute(faqCodigo)%>" onchange="exibeCampoObs(this, '<%=TextHelper.forJavaScriptAttribute(faqCodigo)%>')" value="0" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" <%=(String)((avfNota != null && avfNota.equals("0")) ? "CHECKED":"")%> >
             <label class="form-check-label labelSemNegrito ml-1 pl-4 pr-1" for="avaliacaoFaqNao"><hl:message key="rotulo.nao"/></label>
          </div>
          <div class="form-group d-none" id="observacaoAvaliacaoFaq_<%=TextHelper.forJavaScriptAttribute(faqCodigo)%>">
             <label for=avaliacaoFaqComentario_><hl:message key="rotulo.faq.pergunta.como.podemos.melhorar"/></label>
              <textarea class="form-control" id="avaliacaoFaqComentario_<%=TextHelper.forJavaScriptAttribute(faqCodigo)%>" name="avaliacaoFaqComentario_<%=TextHelper.forJavaScriptAttribute(faqCodigo)%>" placeholder=""><%=TextHelper.forJavaScriptAttribute(avfComentario != null ? avfComentario : "")%></textarea>
        </div>
        <div class="btn-action mt-2">
        <a class="btn btn-primary" href="#no-back" onClick = "salvarAvaliacaoFaq('<%=TextHelper.forJavaScriptAttribute(faqCodigo)%>', '<%=TextHelper.forJavaScriptAttribute(avfCodigo)%>')">
          <hl:message key="rotulo.botao.enviar"/>
        </a>
      </div>
          </div>
        </div>
      </div>
      
      <% } %>
     <% } %>
     </form>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
 f0 = document.forms[0];
 
 
 function exibeCampoObs(el, faqCodigo){
	 
	  if(el.value == '1'){
		  document.getElementById("observacaoAvaliacaoFaq_" + faqCodigo).classList.add('d-none');
	  }
	  
	  if(el.value == '0'){
		  document.getElementById("observacaoAvaliacaoFaq_" + faqCodigo).classList.remove('d-none');
	  }

 }
	 
function salvarAvaliacaoFaq(faqCodigo, avfCodigo) {
	document.getElementById('faqCodigo').value = faqCodigo;
	if (avfCodigo != null && avfCodigo != '') {
		document.getElementById('avfCodigo').value = avfCodigo;
	}
	f0.submit();

}

function pesquisarAvaliacaoFaq() {
	var filtro = f0.FILTRO.value;
	var link = '../v3/visualizarFaq?acao=pesquisarAvaliacaoFaq&FILTRO='+filtro +'&<%=SynchronizerToken.generateToken4URL(request)%>';
	postData(link);

}

function verificaCampoObs(faqCodigo) {
	 
	var value = $('input[name="avaliacaoFaq_' + faqCodigo + '"]:checked').val();
	 
	 if(value == '0'){
		  document.getElementById("observacaoAvaliacaoFaq_" + faqCodigo).classList.remove('d-none');
	  }
}

function desabillitaCampoObs(faqCodigo, avfComentario) {
	 
	var value = $('input[name="avaliacaoFaq_' + faqCodigo + '"]:checked').val();
	 if(value == '0' && avfComentario != ""){
		  $('#avaliacaoFaqComentario_' + faqCodigo).prop('readonly', true);
	  }
}
</script>
</c:set>
<%-- Leiaute Fixo --%>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>