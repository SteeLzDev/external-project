<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.values.CodedValues"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
boolean temProcessoRodando = (boolean) request.getAttribute("temProcessoRodando");
%>
<c:set var="title">
   <hl:message key="rotulo.listar.arquivos.download.rescisao.titulo"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-rescisao"></use>
</c:set>
<c:set var="bodyContent">
<% if (!temProcessoRodando) { %>
<% if (ParamSist.paramEquals(CodedValues.TPC_GERA_ARQUIVO_RESCISAO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) { %>
  <div class="page-title">
    <div class="row d-print-none">
      <div class="col-sm-12 col-md-12 mb-2">
        <div class="float-end">
          <button class="btn btn-primary" onclick="gerarArquivoRescisao()"><hl:message key="rotulo.botao.gerar.arquivo.rescisao"/></button>
        </div>
      </div>
    </div>
  </div>
 <% } %>
  <div class="card">
    <div class="card-header hasIcon">
        <span class="card-header-icon"><svg width="25"><use xlink:href="../img/sprite.svg#i-relatorio"></use></svg></span>
        <h2 class="card-header-title"><hl:message key="mensagem.listar.arquivos.download.rescisao.disponiveis.download"/></h2>
    </div>
    <div class="card-body table-responsive p-0">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th scope="col"><hl:message key="rotulo.arquivo.nome"/></th>
            <th scope="col"><hl:message key="rotulo.arquivo.tamanho"/></th>
            <th scope="col"><hl:message key="rotulo.arquivo.data"/></th>
            <th scope="col" width="10%"><hl:message key="rotulo.acoes"/></th>
          </tr>
        </thead>
        <tbody>
          <c:choose>
            <c:when test="${empty arquivos}">
              <tr>
                <td colspan="7"><hl:message key="mensagem.listar.arquivos.download.rescisao.nenhum.arquivo.encontrado"/></td>
              </tr>
            </c:when>
            <c:otherwise>
              <c:forEach items="${arquivos}" var="arquivo">   
                <tr>
                  <td>${fl:forHtmlContent(arquivo.nomeOriginal)}</td>
                  <td>${fl:forHtmlContent(arquivo.tamanho)}</td>
                  <td>${fl:forHtmlContent(arquivo.data)}</td>
                  <td>
                    <a href="#no-back" onClick="postData('../v3/downloadArquivo?arquivo_nome='+encodeURIComponent('${fl:forJavaScriptAttribute(arquivo.nome)}') + '&tipo=${fl:forJavaScriptAttribute(tipo)}&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>'); return false;" aria-label='<hl:message key="rotulo.botao.aria.download.arquivo" arg0="${arquivo.nome}"/>'>
                      <hl:message key="rotulo.acoes.download"/>
                    </a>
                  </td>
                </tr>
              </c:forEach>
            </c:otherwise>
          </c:choose>
        </tbody>
         <tfoot>
            <tr>
              <td colspan="5"><hl:message key="rotulo.listar.arquivos.download.rescisao.titulo.paginacao"/>
                <span class="font-italic"> - 
                  <hl:message key="rotulo.paginacao.registros.sem.estilo" arg0="${_paginacaoPrimeiro}" arg1="${_paginacaoUltimo}" arg2="${_paginacaoQtdTotal}"/>
                </span>
              </td>
            </tr>
          </tfoot>
      </table>
    </div>
    <div class="card-footer">
      <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>   
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" aria-label='<hl:message key="rotulo.botao.voltar"/>' href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
  <%}else{ %>
  <div class="card">
    <div class="btn-action">
      <a class="btn btn-outline-danger" aria-label='<hl:message key="rotulo.botao.voltar"/>' href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
    </div>
  </div>
  <%} %>
</c:set>
<c:set var="javascript">


  <% if (ParamSist.paramEquals(CodedValues.TPC_GERA_ARQUIVO_RESCISAO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) { %>
  <script>
  function gerarArquivoRescisao(){
		postData('../v3/listarArquivosDownloadRescisao?acao=executar');
	}
  </script>
  <% } %>
  
  <script>
  doLoad(<%=temProcessoRodando%>);
  function doLoad(reload) {
      if (reload) {
        setTimeout("refresh()", 5*1000);
      }
    }
    function refresh() {
      postData("../v3/listarArquivosDownloadRescisao?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>");
    }
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
