<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ taglib prefix="t"     tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl"    uri="/html-lib" %>
<%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

  int qtdColunas = (int) request.getAttribute("qtdColunas");  
  String filtro = (String) request.getAttribute("filtro");
  int filtro_tipo = (int) request.getAttribute("filtro_tipo");
  List lstFaqs = (List) request.getAttribute("lstFaqs");
%>
<c:set var="title">
  <hl:message key="rotulo.faq.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-sistema"></use>
</c:set>
<c:set var="bodyContent">
  <div class="btn-action">
    <a class="btn btn-primary" href="#no-back" onClick="postData('../v3/manterFaq?acao=editar&operacao=inserir&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>')">
      <hl:message key="rotulo.botao.novo.faq"/>
    </a>
  </div>
  <div class="row">
    <div class="col-sm-5 col-md-4">
      <div class="card">
        <div class="card-header hasIcon pl-3">
          <h2 class="card-header-title"><hl:message key="rotulo.acao.pesquisar"/></h2>
        </div>
        <div class="card-body">
          <form name="form1" method="post" action="../v3/manterFaq?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>">
            <div class="row">
              <div class="form-group col-sm">
                <label for="iFiltro"><hl:message key="rotulo.faq.filtro"/></label>
                <input type="text" class="form-control" id="iFiltro" name="FILTRO" placeholder="<hl:message key="rotulo.acao.digite.filtro"/>" size="10" value="${filtro}" onFocus="SetarEventoMascara(this,'#*200',true);"  onBlur="fout(this);ValidaMascara(this);">
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm">
                <label for="iFiltrarPor"><hl:message key="rotulo.acao.filtrar.por"/></label>
                <select id="iFiltrarPor" name="FILTRO_TIPO" class="form-control form-select select"  onBlur="fout(this);ValidaMascara(this);" nf="Filtrar" onChange="verificaCombo()">
                  <option value="1" ${filtro_tipo == 1 ? "SELECTED" : ""}><hl:message key="rotulo.campo.sem.filtro"/></OPTION>
                  <option value="2" ${filtro_tipo == 2 ? "SELECTED" : ""}><hl:message key="rotulo.consignante.singular"/></OPTION>
                  <option value="3" ${filtro_tipo == 3 ? "SELECTED" : ""}><hl:message key="rotulo.consignataria.singular"/></OPTION>
                  <option value="4" ${filtro_tipo == 4 ? "SELECTED" : ""}><hl:message key="rotulo.correspondente.singular"/></OPTION>
                  <option value="5" ${filtro_tipo == 5 ? "SELECTED" : ""}><hl:message key="rotulo.orgao.singular"/></OPTION>    
                  <option value="6" ${filtro_tipo == 6 ? "SELECTED" : ""}><hl:message key="rotulo.servidor.singular"/></OPTION>
                  <option value="7" ${filtro_tipo == 7 ? "SELECTED" : ""}><hl:message key="rotulo.suporte.singular"/></OPTION>
                  <option value="8" ${filtro_tipo == 8 ? "SELECTED" : ""}><hl:message key="rotulo.faq.campo.titulo"/></OPTION>
                </select>
              </div>
            </div>
          </form>
        </div>
      </div>
      <div class="btn-action">
        <a class="btn btn-primary" href="#no-back" onClick="document.forms['form1'].submit(); return false;">
          <svg width="20">
            <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-consultar"></use>
          </svg>
          <hl:message key="rotulo.botao.pesquisar"/>
        </a>
      </div>
    </div>
    <div class="col-sm-7 col-md-8">
      <div class="card">
        <div class="card-header hasIcon pl-3">
          <h2 class="card-header-title"><hl:message key="rotulo.faq.titulo"/></h2>
        </div>
        <div class="card-body table-responsive p-0">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th scope="col"><hl:message key="rotulo.faq.campo.titulo"/></th>
                <th scope="col"><hl:message key="rotulo.faq.sequencia"/></th>
                <th scope="col"><hl:message key="rotulo.faq.data.criacao"/></th>
                <th scope="col" width="20%"><hl:message key="rotulo.acoes"/></th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <c:choose>
                  <c:when test="${not empty lstFaqs}">
                    <c:forEach items="${lstFaqs}" var="faq" varStatus="faqStatus">
                      <tr>
                        <td>${fl:forHtmlContent(faq.atributos['tb_faq.faq_titulo_1'])}</td>
                        <td>${fl:forHtmlContent(faq.atributos['tb_faq.faq_sequencia'] != null ? faq.atributos['tb_faq.faq_sequencia'] : '')}</td>
                        <td >${fl:forHtmlContent(faq.atributos['tb_faq.faq_data'] != null ? faq.atributos['tb_faq.faq_data'] : '')}</td>
                        <td>
                          <div class="actions">
                            <div class="dropdown">
                              <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                <div class="form-inline">
                                  <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.botao.opcoes"/>" aria-label="<hl:message key="rotulo.botao.opcoes"/>">
                                    <svg><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="../img/sprite.svg#i-engrenagem"></use></svg>
                                  </span>
                                  <hl:message key="rotulo.botao.opcoes"/>
                                </div>
                              </a>
                              <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                                <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterFaq?acao=editar&faqCodigo=${faq.atributos['tb_faq.faq_codigo']}&operacao=editar&<%=(String)(SynchronizerToken.generateToken4URL(request))%>')">
                                  <hl:message key="rotulo.acoes.editar"/>
                                </a>
                                <a class="dropdown-item" href="#no-back" onClick="ExcluirFaq('${faq.atributos['tb_faq.faq_codigo']}', '../v3/manterFaq?acao=excluir&<%=SynchronizerToken.generateToken4URL(request)%>', '${fl:forJavaScriptAttribute(faq.atributos['tb_faq.faq_titulo_1'])}')">
                                  <hl:message key="rotulo.acoes.remover"/>
                                </a>
                              </div>
                            </div>
                          </div>
                        </td>
                      </tr>
                    </c:forEach>
                  </c:when>
                  <c:otherwise>
                    <tr>
                      <td colspan="<%=qtdColunas%>"><hl:message key="mensagem.nenhum.faq.encontrado"/></td>
                    </tr>
                  </c:otherwise>
                </c:choose>
              </tr>
            </tbody>
            <tfoot>
             <tr><td colspan="<%=qtdColunas%>"><%=ApplicationResourcesHelper.getMessage("rotulo.faq.listagem", responsavel) + " - " %><span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTitulo"))%></span></td></tr>
           </tfoot>
          </table>
        </div>
        <div class="card-footer">
          <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
        </div>                
      </div>
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" id="btnVoltar" href="#no-back" onClick="postData('../v3/carregarPrincipal')"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
</c:set>
<c:set var="javascript">
  <script  type="text/JavaScript">
	function ExcluirFaq(faqCodigo, alink, faqTitulo1) {
		var url = alink + (alink.indexOf('?') == -1 ? "?" : "&")  + "faqCodigo=" + faqCodigo + "&faqTitulo1=" + faqTitulo1 + "&excluir=sim";
    	return ConfirmaUrl(('<hl:message key="mensagem.confirmacao.faq"/>').replace("{0}", faqTitulo1), url);
   	}
	
   	function verificaCombo() {
    	var selecao = document.form1.FILTRO_TIPO[document.form1.FILTRO_TIPO.selectedIndex].value;    
      	// Desabilita campo de filtro se a selecao for cse, servidor ou orgao
      	if (selecao != 08) {
        	document.form1.FILTRO.disabled=true;
        	// Limpa campo de filtro
        	document.form1.FILTRO.value="";
      	} else {
        	document.form1.FILTRO.disabled=false;
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
