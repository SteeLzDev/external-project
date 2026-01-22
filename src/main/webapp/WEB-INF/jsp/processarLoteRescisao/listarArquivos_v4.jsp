<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession"/>
<%
    boolean temProcessoRodando = (boolean) request.getAttribute("temProcessoRodando");
    String entidade = (String) request.getAttribute("entidade");
    String tipo = (String) request.getAttribute("tipo");
%>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%--
  Created by IntelliJ IDEA.
  User: douglas.neves
  Date: 10/04/2023
  Time: 14:25
  To change this template use File | Settings | File Templates.
--%>
<c:set var="title">
    <hl:message key="rotulo.processar.lote.rescisao.titulo"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-rescisao"></use>
</c:set>
<c:set var="bodyContent">
    <div class="card">
        <div class="card-header hasIcon">
            <span class="card-header-icon"><svg width="25"><use xlink:href="../img/sprite.svg#i-relatorio"></use></svg></span>
            <h2 class="card-header-title"><hl:message key="rotulo.lote.arquivos.rescisao.disponiveis"/></h2>
        </div>
        <div class="card-body table-responsive p-0">
            <table class="table table-striped table-hover">
                <thead>
                <tr>
                    <th scope="col"><hl:message key="rotulo.lote.nome"/></th>
                    <th scope="col"><hl:message key="rotulo.lote.tamanho.abreviado"/></th>
                    <th scope="col"><hl:message key="rotulo.lote.data"/></th>
                    <th scope="col"><hl:message key="rotulo.acoes"/></th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${empty arquivos}">
                        <tr>
                            <td colspan='7'><hl:message key="mensagem.erro.nenhum.arquivo.encontrado"/></td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${arquivos}" var="arquivo">
                            <tr>
                                <td>${fl:forHtmlContent(arquivo.nomeOriginal)}</td>
                                <td>${fl:forHtmlContent(arquivo.tamanho)}</td>
                                <td>${fl:forHtmlContent(arquivo.data)}</td>
                                <td>
                                    <div class="actions">
                                        <div class="actions">
                                            <div class="dropdown">
                                                <a class="dropdown-toggle ico-action" href="#" role="button"
                                                   id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true"
                                                   aria-expanded="false">
                                                    <div class="form-inline">
                        <span class="mr-1" data-bs-toggle="tooltip" title=""
                              data-original-title="<hl:message key='rotulo.mais.acoes'/>"
                              aria-label="<hl:message key='rotulo.mais.acoes'/>">
                          <svg><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                        </span><hl:message key="rotulo.botao.opcoes"/>
                                                    </div>
                                                </a>
                                                <div class="dropdown-menu dropdown-menu-right"
                                                     aria-labelledby="userMenu">
                                                    <c:if test="${fl:forValidationArquivo(arquivo.nomeOriginal)}">
                                                        <a class="dropdown-item" href="#"
                                                           onClick="doIt('v', '${fl:forJavaScriptAttribute(arquivo.nomeOriginal)}', '${fl:forJavaScriptAttribute(arquivo.nomeOriginal)}'); return false;"
                                                           ><hl:message
                                                                key="rotulo.acoes.validar"/></a>
                                                        <a class="dropdown-item" href="#"
                                                           onClick="doIt('i', '${fl:forJavaScriptAttribute(arquivo.nomeOriginal)}', '${fl:forJavaScriptAttribute(arquivo.nomeOriginal)}'); return false;"
                                                           ><hl:message
                                                                key="rotulo.acoes.processar"/></a>
                                                    </c:if>
                                                    <a class="dropdown-item" href="#"
                                                       onClick="fazDownload('${fl:forJavaScriptAttribute(arquivo.nomeOriginal)}', '<%=TextHelper.forJavaScript(tipo)%>', '<%=TextHelper.forJavaScript(entidade)%>'); return false;"
                                                       ><hl:message
                                                            key="rotulo.acoes.download"/></a>
                                                    <a class="dropdown-item" href="#"
                                                       onClick="doIt('e', '${fl:forJavaScriptAttribute(arquivo.nomeOriginal)}', '${fl:forJavaScriptAttribute(arquivo.nomeOriginal)}'); return false;"
                                                       ><hl:message
                                                            key="rotulo.acoes.excluir"/></a>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
                </tbody>
                <tfoot>
                <tr>
                    <td colspan="4">
                        <hl:message key="rotulo.listar.arquivos.download.rescisao.titulo.paginacao"/>
                        <span class="font-italic">
                    <hl:message key="rotulo.paginacao.registros.sem.estilo" arg0="${_paginacaoPrimeiro}"
                                arg1="${_paginacaoUltimo}" arg2="${_paginacaoQtdTotal}"/>
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
    <% if (!temProcessoRodando) { %>
    <div class="btn-action">
        <a class="btn btn-outline-danger" HREF="#"
           onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message
                key="rotulo.botao.teclado.virtual.cancelar"/></a>
        <input name="MM_update" type="hidden" value="form1">
        <input name="arquivo_nome" type="hidden" value="">
    </div>
    <% } else { %>
    <div class="btn-action">
        <a class="btn btn-outline-danger" aria-label='<hl:message key="rotulo.botao.voltar"/>' href="#"
           onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
    </div>
    <% } %>
</c:set>
<c:set var="javascript">
    <script type="text/JavaScript">
        function fazDownload(nome, tipo, entidade) {
            postData('../v3/downloadArquivo?arquivo_nome=' + nome + '&tipo=' + tipo + '&entidade=' + entidade + '&subtipo=rescisaoDownload&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>', 'download');
        }

        doLoad(<%=(boolean)temProcessoRodando%>);

        function doIt(opt, arq, path) {
            var msg = '', j;
            if (opt == 'e') {
                msg = '<hl:message key="mensagem.confirmacao.exclusao.lote"/>'.replace("{0}", arq);
                j = '../v3/processarLoteRescisao?acao=excluirArquivo&arquivo_nome=' + encodeURIComponent(path) + '&ext=exc' + '&tipo=<%=TextHelper.forJavaScriptBlock(tipo)%>&entidade=<%=TextHelper.forJavaScriptBlock(entidade)%>&<%=SynchronizerToken.generateToken4URL(request)%>';
            } else if (opt == 'i') {
                msg = '<hl:message key="mensagem.confirmacao.processamento.lote"/>'.replace("{0}", arq);
                j = '../v3/processarLoteRescisao?acao=processar&arquivo_nome=' + encodeURIComponent(path);
            } else if (opt == 'v') {
                msg = '<hl:message key="mensagem.confirmacao.validacao.lote"/>'.replace("{0}", arq);
                j = '../v3/processarLoteRescisao?acao=validar&arquivo_nome=' + encodeURIComponent(path);
            } else {
                return false;
            }

            j = j + '&<%=SynchronizerToken.generateToken4URL(request)%>'
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
                setTimeout('refresh()', 15 * 1000);
            }
        }

        function refresh() {
            postData("../v3/processarLoteRescisao?acao=listarArquivos&<%=SynchronizerToken.generateToken4URL(request)%>");
        }
    </script>

</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>