<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.dto.*"%>

<%
AcessoSistema responsavel = (AcessoSistema)request.getAttribute("responsavel"); 
CustomTransferObject autdes = (CustomTransferObject)request.getAttribute("autdes");
Boolean exigeSenhaSerCancel = (Boolean)request.getAttribute("exigeSenhaSerCancel");
Boolean exigeMotivo = (Boolean)request.getAttribute("exigeMotivo");
String adeCodigo = (String)request.getAttribute("adeCodigo");
boolean temProcessoRodando = (boolean) request.getAttribute("temProcessoRodando");
%>

<c:set var="title">
   <hl:message key="rotulo.importar.bloqueio.servidor.titulo.lower"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
  <div class="card">
    <div class="card-header hasIcon">
      <span class="card-header-icon"><svg width="25"><use xlink:href="../img/sprite.svg#i-servidor"></use></svg></span>
      <h2 class="card-header-title"><hl:message key="rotulo.importar.bloqueio.servidor.subtitulo"/></h2>
    </div>
    <div class="card-body p-0">
    	<div class="alert alert-warning m-0" role="alert">
    		<p class="mb-0"><hl:message key="mensagem.informacao.importar.bloqueio.servidor"/></p>
   		</div>
      <div class="table-responsive">
        <table class="table table-striped table-hover">
          <thead>
            <tr>
              <th scope="col" width="70%"><hl:message key="rotulo.importar.bloqueio.servidor.nome"/></th>
              <th scope="col" width="10%"><hl:message key="rotulo.importar.bloqueio.servidor.tamanho"/></th>
              <th scope="col" width="10%"><hl:message key="rotulo.importar.bloqueio.servidor.data"/></th>
              <th scope="col" width="10%"><hl:message key="rotulo.acoes"/></th>
            </tr>
          </thead>
            <c:choose>
              <c:when test="${empty arquivosDTO}">
                <tr>
                  <td colspan="4"><hl:message key="mensagem.folha.nenhum.arquivo.encontrado"/></td>
                </tr>
              </c:when>
              <c:otherwise>
                <c:forEach items="${arquivosDTO}" var="arq" varStatus="arqStatus">
                   <tr>
                     <td><IMG SRC="../img/icones/${fl:forHtmlAttribute(arq.formato)}" BORDER="0" ALIGN="ABSMIDDLE"> ${fl:forHtmlContent(arq.originalNome)}</td>
                       <td align="right">${fl:forHtmlContent(arq.tam)}&nbsp;</td>
                       <td align="center">${fl:forHtmlContent(arq.data)}</td>
                       <c:if test="${podeProcessarBloqSer}">
                       <td>
                      <div class="actions">
                        <div class="dropdown">
                          <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                            <div class="form-inline">
                              <span class="mr-1" data-bs-toggle="tooltip" aria-label='<hl:message key="rotulo.botao.opcoes"/>' title='<hl:message key="rotulo.botao.opcoes"/>'>
                                <svg><use xlink:href="../img/sprite.svg#i-engrenagem"></use></svg>
                              </span>
                              <hl:message key="rotulo.botao.opcoes"/>
                            </div>
                          </a>
                          <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                              <a class="dropdown-item" href="#no-back" onClick="doIt('i', '${fl:forJavaScript(arq.nome)}', '${fl:forJavaScript(arq.nome)}'); return false;" aria-label='<hl:message key="mensagem.processar.arquivo.bloqueio.servidor.clique.aqui" arg0="${arquivo.nome}"/>'><hl:message key="rotulo.acoes.importar"/></a>
                              <a class="dropdown-item" href="#confirmarMensagem" onClick="postData('../v3/downloadArquivo?arquivo_nome='+encodeURIComponent('${fl:forJavaScriptAttribute(arq.nome)}') + '&tipo=${fl:forJavaScript(tipo)}&entidade=${fl:forJavaScript(entidade)}&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>'); doLoad(<%=true%>); return false;" data-bs-toggle="modal" aria-label="<hl:message key='rotulo.botao.aria.download.arquivo' arg0='${fl:forJavaScriptAttribute(arq.nome)}'/>"><hl:message key='rotulo.acoes.lst.arq.generico.download'/></a>
  						  <c:if test="${podeExcluirArqBloqSer}">  
  						    <a class="dropdown-item" href="#no-back" onClick="doIt('e', '${fl:forJavaScript(arq.nome)}', '${fl:forJavaScript(arq.nome)}'); return false;" aria-label='<hl:message key="mensagem.folha.excluir.arquivo.nome.clique.aqui" arg0="${arquivo.nome}"/>'><hl:message key="rotulo.acoes.excluir"/></a>
                           </c:if>
                          </div>
                        </div>
                      </div>
                    </td>
                       </c:if>
                     </tr>
                </c:forEach>
              </c:otherwise>
            </c:choose>
             <tfoot>
              <tr>
                  <td colspan="4"><hl:message key="rotulo.importar.bloqueio.servidor.listagem"/>
                    <span class="font-italic"> - 
                      <hl:message key="rotulo.paginacao.registros.sem.estilo" arg0="${_paginacaoPrimeiro}" arg1="${_paginacaoUltimo}" arg2="${_paginacaoQtdTotal}"/>
                    </span>
                </td>
              </tr>
            </tfoot>
        </table>
      </div>
      <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" aria-label='<hl:message key="rotulo.botao.cancelar"/>' href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
  </div>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript" src="../js/scripts_2810.js"></script>
<script type="text/JavaScript">
doLoad(<%=temProcessoRodando%>);
function doIt(opt, arq, path) {
  var msg = '', j;
  if (opt == 'e') {
    msg = '<hl:message key="mensagem.confirmacao.importar.bloqueio.servidor.exclusao"/>';
    msg = msg.replace('{0}', arq);
    j = '../v3/excluirArquivo?arquivo_nome=' + encodeURIComponent(path) + '&tipo=${fl:forJavaScriptBlock(tipo)}';
  } else if (opt == 'i') {
    msg = '<hl:message key="mensagem.confirmacao.importar.bloqueio.servidor.processamento"/>';
    msg = msg.replace('{0}', arq);
    j = '../v3/importarServidoresBloqueados?acao=iniciar&arquivo_nome=' + encodeURIComponent(path) + '&tipo=${fl:forJavaScriptBlock(tipo)}&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>';
  } else {
    return false;
  }
  j = j + '&<%=SynchronizerToken.generateToken4URL(request)%>';
  if (msg != '') {
    if (confirm(msg)) {
      if (opt == 'i' || opt == 'v') {
          postData(j);
      } else {
        postData(j);
      }
    } else {
      return false;
    }
  } else {
    postData(j);
  }
  return true;
}

function doLoad(reload) {
  if (reload) {
    setTimeout("refresh()", 15*1000);
  }
}

function refresh() {
	postData("../v3/importarServidoresBloqueados?acao=iniciar&CSA_CODIGO&<%=SynchronizerToken.generateToken4URL(request)%>");
}
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>