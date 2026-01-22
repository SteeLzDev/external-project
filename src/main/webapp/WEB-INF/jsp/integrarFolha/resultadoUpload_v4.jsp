<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib uri="/function-lib" prefix="fl"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
String descricaoMargem = ApplicationResourcesHelper.getMessage("rotulo.margem.singular", responsavel);
%>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="title">
    <hl:message key="rotulo.integracao.orientada.titulo"/>
</c:set>
<c:set var="bodyContent">
    <form name="form1" method="POST" action="integrarFolha?tipo=${tipo}">
        <%=SynchronizerToken.generateHtmlToken(request)%>
        <input name="arquivo_nome" type="hidden" value="${fl:forHtmlAttribute(arquivoSalvo_name)}">
        <div class="row">
            <div class="col-sm">
                <div class="card">
                    <div class="card-header hasIcon">
                        <span class="card-header-icon"><svg width="26">
                        <use xlink:href="../img/sprite.svg#i-${'servidor'}"></use></svg></span>
                        <h2 class="card-header-title"><hl:message key="rotulo.integracao.orientada.resultado.validacao.arquivo" arg0="${fl:forHtmlAttribute(arquivoSalvo_name)}" arg1="${dataValidacaoArquivo}"/></h2>
                    </div>
                    <div class="card-body table-responsive p-0">
                        <table class="table table-striped table-hover">
                            <thead>
                                <tr>
                                    <th scope="col"><hl:message key="rotulo.servidor.nome"/></th>
                                    <%-- if (!omiteCpfServidor) { --%>
                                    <th scope="col"><hl:message key="rotulo.servidor.cpf"/></th>
                                    <%-- } --%>
                                    <%-- if (!omiteMatriculaServidor) { --%>
                                    <th scope="col"><hl:message key="rotulo.servidor.matricula"/></th>
                                    <%-- } --%>
                                    <c:if test="${tipo == 'margem'}">
                                    <th scope="col"><hl:message key="rotulo.servidor.codigo.localidade"/></th>
                                    <th scope="col"><hl:message key="rotulo.servidor.celular"/></th>
                                    <th scope="col"><hl:message key="rotulo.servidor.email"/></th>
                                    <th scope="col"><hl:message key="rotulo.servidor.margem" arg0="<%=descricaoMargem%>"/></th>
                                    </c:if>
                                    <c:if test="${tipo == 'retorno'}">
                                    <th scope="col"><hl:message key="rotulo.convenio.codigo.verba"/></th>
                                    <th scope="col"><hl:message key="rotulo.consignacao.valor.parcela.desc.folha"/></th>
                                    </c:if>
                                    <th scope="col"><hl:message key="rotulo.status.resultado"/></th>
                                    <th scope="col"><hl:message key="rotulo.status.observacao"/></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${validacao_arquivo_listagem}" var="record">     
                                <!--  <div><c:out value="${record.index}"/></div> -->
                                <!--  <div><c:out value="${record.state}"/></div> -->
                                <tr>
                                    <td><c:out value="${record.data.get('SER_NOME')}"/></td>
                                    <%-- if (!omiteCpfServidor) { --%>
                                    <td><c:out value="${record.data.get('SER_CPF')}"/></td>
                                    <%-- } --%>
                                    <%-- if (!omiteMatriculaServidor) { --%>
                                    <td><c:out value="${record.data.get('RSE_MATRICULA')}"/></td>
                                    <%-- } --%>
                                    <c:if test="${tipo == 'margem'}">
                                    <td><c:out value="${record.data.get('DDD')}"/></td>
                                    <td><c:out value="${record.data.get('SER_CELULAR')}"/></td>
                                    <td><c:out value="${record.data.get('SER_EMAIL')}"/></td>
                                    <td><c:out value="${record.data.get('RSE_MARGEM')}"/></td>
                                    </c:if>
                                    <c:if test="${tipo == 'retorno'}">
                                    <td><c:out value="${record.data.get('CNV_COD_VERBA')}"/></td>
                                    <td><c:out value="${record.data.get('PRD_VLR_REALIZADO')}"/></td>
                                    </c:if>
                                    <td><svg style="height:17px; width:17px; fill:${record.state == 0 ? 'green' : 'red' }"><use xlink:href="#i-status-${record.state == 0 ? 'v' : 'x' }"></use></svg></td>
                                    <td class="pre"><c:out value="${record.message}"/></td>
                                </tr>
                                </c:forEach>
                            </tbody>
                            <tfoot>
                                <tr>
                                    <td colspan="7"><hl:message key="rotulo.integracao.orientada.rodape.listagem"/></td>
                                </tr>
                            </tfoot>
                        </table>
                    </div>
                    <div class="card-footer">
                        <%--@ include file="../paginador/incluirBarraPaginacao_v4.jsp" --%>
                    </div>
                </div>
                <div class="btn-action">
                    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')" id="btnVoltar"><hl:message key="rotulo.botao.voltar"/></a>
                    <a class="btn btn-secondary" aria-label="<hl:message key="rotulo.botao.aria.download.critica"/>" href="#" onClick="downloadCritica(); return false;"><svg width="17"><use xlink:href="#i-download"></use></svg> <hl:message key="rotulo.botao.download.critica"/></a>
                    <c:set var="funcao" value="<%=CodedValues.FUN_INTEGRACAO_ORIENTADA_PROCESSAR%>"/>
                    <c:if test="${fl:temPermissao(responsavel, funcao, false)}">
                    <a class="btn btn-primary" aria-label="<hl:message key="rotulo.botao.aria.integracao.orientada.processar.arquivo"/>" href="#" onClick="processar(); return false;"><svg width="17"><use xlink:href="#i-confirmar"></use></svg> <hl:message key="rotulo.botao.processar"/></a>
                    </c:if>
                </div>
            </div>
        </div>
        <!-- Modal aguarde -->
        <div class="modal fade" id="modalAguarde" data-focus="false" tabindex="-1" role="dialog" aria-labelledby="modalAguardeLabel" aria-hidden="true">
            <div class="modal-dialog-upload modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-md-12 d-flex justify-content-center">
                                <img src="../img/loading.gif" class="loading">
                            </div>
                            <div class="col-md-12">
                                <div class="modal-body"><span id="modalAguardeLabel"><hl:message key="mensagem.integracao.orientada.aguarde.processamento"/></span></div>            
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </form>
</c:set>
<c:set var="javascript">
    <script type="text/JavaScript">
        var f0 = document.forms[0];
        var f0baseAction = f0.action;
        var temErros = ${!validacao_arquivo_sucesso};
        var processando = false;
        function setAcao(acao) {
            f0.action = f0baseAction + '&acao=' + acao;
        }
        function downloadCritica() {
            if (confirm('<hl:message key="mensagem.confirmacao.download.critica"/>')) {
                postData('../v3/downloadArquivo?tipo=${tipo}&arquivo_nome='+encodeURIComponent('${arquivoCritica_name}') + '&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');
            }
        }
        function processar() {
            if (processando) {
              jQuery('#modalAguarde').modal('show');
            } else if (!temErros) { 
                if (confirm('<hl:message key="mensagem.confirmacao.processar.arquivo"/>')) {
                    processando = true;
                    jQuery('#modalAguarde').modal('show'); 
                    f0.action = f0baseAction + '&_skip_history_=true&acao=processar';
                    f0.submit();
                }
            } else {
                alert('<hl:message key="mensagem.integracao.orientada.verifique.erros.arquivo"/>');
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
