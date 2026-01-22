<%@ page import="com.zetra.econsig.dto.CustomTransferObject" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.TransferObject" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="java.util.List" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession"/>
<%
    AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
    List<TransferObject> relatorios = (List<TransferObject>) request.getAttribute("relatorios");
    List<TransferObject> retornos = (List<TransferObject>) request.getAttribute("retornos");
%>
<c:set var="title">
    <hl:message key="rotulo.relatorios.reimplantar.lote"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
    <div class="card">
        <div class="card-header hasIcon">
            <span class="card-header-icon"><svg width="25"><use xlink:href="../img/sprite.svg#i-relatorio"></use></svg></span>
            <h2 class="card-header-title"><hl:message key="rotulo.listar.arq.relatorio.download"/></h2>
        </div>
        <div class="card-body table-responsive">
            <table id="dataTables" class="table table-striped table-hover w-100">
                <thead>
                <tr>
                    <th scope="col"><hl:message key="rotulo.relatorio.nome"/></th>
                    <th scope="col"><hl:message key="rotulo.relatorio.tamanho.abreviado"/></th>
                    <th scope="col"><hl:message key="rotulo.relatorio.data"/></th>
                    <th scope="col"><hl:message key="rotulo.acoes"/></th>
                </tr>
                </thead>
                <tbody>
                <% if (TextHelper.isNull(relatorios)) { %>
                <tr>
                    <td colspan="6"><hl:message key="rotulo.nenhum.relatorio.encontrado"/></td>
                </tr>
                <%
                } else {
                    for (TransferObject relatorio : relatorios) {
                        CustomTransferObject fileRelatorio = (CustomTransferObject) relatorio;
                        String nomeArq = fileRelatorio.getAttribute("nomeArq").toString();
                        String tamArq = fileRelatorio.getAttribute("tamArq").toString();
                        String dataArq = fileRelatorio.getAttribute("dataArq").toString();
                %>
                <tr>
                    <td><%=TextHelper.forHtmlContent(nomeArq)%>
                    </td>
                    <td><%=TextHelper.forHtmlContent(tamArq)%>
                    </td>
                    <td><%=TextHelper.forHtmlAttribute(dataArq)%>
                    </td>
                    <td>
                        <div class="actions">
                            <div class="dropdown">
                                <a class="dropdown-toggle ico-action" href="#"
                                   role="button" id="userMenu"
                                   data-bs-toggle="dropdown" aria-haspopup="true"
                                   aria-expanded="false">
                                    <div class="form-inline">
                                    <span class="mr-1" data-bs-toggle="tooltip"
                                          title=""
                                          data-original-title="<%=ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)%>"
                                          aria-label="<%=ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)%>">
                                    <svg><use xmlns:xlink="http://www.w3.org/1999/xlink"
                                              xlink:href="#i-engrenagem"></use></svg></span><hl:message
                                            key="rotulo.botao.opcoes"/>
                                    </div>
                                </a>
                                <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                                    <a class="dropdown-item" href="#no-back"
                                       aria-label="<hl:message key="rotulo.botao.aria.download.arquivo" arg0="<%=TextHelper.forHtmlContent(nomeArq)%>"/>"
                                       onClick="postData('../v3/downloadArquivo?tipo=reimplanteRelatorio&arquivo_nome=' + encodeURIComponent('<%=nomeArq%>') + '&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>'); return false;"
                                       href="#"><hl:message key="rotulo.botao.download"/></a>
                                    <a class="dropdown-item" href="#no-back"
                                       aria-label="<hl:message key="rotulo.botao.aria.excluir.arquivo" arg0="<%=TextHelper.forHtmlContent(nomeArq)%>"/>"
                                       onClick="confirmExcluir('<%=nomeArq%>'); return false;"><hl:message
                                            key="rotulo.botao.excluir"/></a>
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
                <% } %>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>
    <div class="card">
        <div class="card-header hasIcon">
            <span class="card-header-icon"><svg width="25"><use xlink:href="../img/sprite.svg#i-relatorio"></use></svg></span>
            <h2 class="card-header-title"><hl:message key="rotulo.listar.retornos.download"/></h2>
        </div>
        <div class="card-body table-responsive">
            <table id="dataTables" class="table table-striped table-hover w-100">
                <thead>
                <tr>
                    <th scope="col"><hl:message key="rotulo.relatorio.nome"/></th>
                    <th scope="col"><hl:message key="rotulo.relatorio.tamanho.abreviado"/></th>
                    <th scope="col"><hl:message key="rotulo.relatorio.data"/></th>
                    <th scope="col"><hl:message key="rotulo.acoes"/></th>
                </tr>
                </thead>
                <tbody>
                <% if (TextHelper.isNull(retornos)) { %>
                <tr>
                    <td colspan="6"><hl:message
                            key="rotulo.nenhum.retorno.encontrado"/></td>
                </tr>
                <%
                } else {
                    for (TransferObject retorno : retornos) {
                        CustomTransferObject FileRetorno = (CustomTransferObject) retorno;
                        String nomeArq = FileRetorno.getAttribute("nomeArq").toString();
                        String tamArq = FileRetorno.getAttribute("tamArq").toString();
                        String dataArq = FileRetorno.getAttribute("dataArq").toString();
                %>
                <tr>
                    <td><%=TextHelper.forHtmlContent(nomeArq)%>
                    </td>
                    <td><%=TextHelper.forHtmlContent(tamArq)%>
                    </td>
                    <td><%=TextHelper.forHtmlAttribute(dataArq)%>
                    </td>
                    <td>
                        <div class="form-inline">
                            <a class="ico-action"
                               onClick="postData('../v3/downloadArquivo?tipo=reimplante&arquivo_nome=' + encodeURIComponent('<%=nomeArq%>') + '&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');"
                               href="#">
                          <span class="mr-1" data-bs-toggle="tooltip"
                                aria-label="<hl:message key="rotulo.botao.download"/>" title=""
                                data-original-title="download">
                            <svg class="icon-download-mensagem" width="26" height="18"> <use
                                    xlink:href="../img/sprite.svg#i-download"></use></svg>
                          </span>
                                <hl:message key="rotulo.acoes.upload.arquivo.download"/>
                            </a>
                        </div>
                    </td>
                </tr>
                <% } %>
                <% } %>
                </tbody>
                <tfoot>
                <tr>
                    <td colspan="12"><%=ApplicationResourcesHelper.getMessage("rotulo.consignacao.listagem.consignacao", responsavel) %>
                        <span class="font-italic"> - <hl:message key="rotulo.paginacao.registros.sem.estilo"
                                                                 arg0="${_paginacaoPrimeiro}"
                                                                 arg1="${_paginacaoUltimo}"
                                                                 arg2="${_paginacaoQtdTotal}"/></span>
                </tr>
                </tfoot>
            </table>
            <div class="card-footer">
                <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
            </div>
        </div>
    </div>
    <div class="btn-action">
        <a href="#no-back" class="btn btn-outline-danger"
           onClick="postData('../v3/reimplantarConsignacaoLote?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>'); return false;"><hl:message
                key="rotulo.botao.voltar"/></a>
    </div>
</c:set>
<c:set var="javascript">
    <script>

        function confirmExcluir(nome) {
            if (confirm('<hl:message key="mensagem.confirmacao.excluir.retorno"/>')) {
                postData('../v3/excluirArquivo?tipo=reimplanteRelatorio&arquivo_nome=' + encodeURIComponent(nome) + '&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');
            } else {
                return false;
            }
        }

    </script>

</c:set>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>