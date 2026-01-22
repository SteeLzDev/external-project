<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>

<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

    String adeNumero = JspHelper.verificaVarQryStr(request, "ADE_NUMERO");
    String rseMatricula = JspHelper.verificaVarQryStr(request, "RSE_MATRICULA");
    String serCpf = JspHelper.verificaVarQryStr(request, "SER_CPF");
    boolean integradas = JspHelper.verificaVarQryStr(request, "PESQUISA").equals("INTEGRADAS");
    boolean filtros = (boolean) request.getAttribute("filtros");

// Seleciona parcelas que já foram integradas
    List<TransferObject> parcelas = (List<TransferObject>) request.getAttribute("parcelas");
    int offset = (int) request.getAttribute("offset");

%>
<c:set var="title">
    <hl:message key="rotulo.folha.cadastro.retorno.integracao.titulo"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
    <% if (!parcelas.isEmpty() && !integradas) { %>
    <div class="page-title d-print-none">
        <div class="row">
            <div class="col-sm-12 col-md-12 mb-2">
                <div class="float-end">
                    <button data-bs-toggle="dropdown" aria-haspopup="true" id="acoes" aria-expanded="false"
                            class="btn btn-primary" type="submit"><hl:message key="rotulo.acoes"/></button>
                    <div class="dropdown-menu dropdown-menu-right" aria-labelledby="acoes">
                        <a class="dropdown-item" href="#" onClick="rejeitarTodas(); return false;"><hl:message
                                key="rotulo.botao.rejeitar.todas"/></a>
                        <a class="dropdown-item" href="#" onClick="liquidarTodas(); return false;"><hl:message
                                key="rotulo.botao.liquidar.todas"/></a>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <% } %>
    <div class="card">
        <div class="card-header hasIcon">
            <span class="card-header-icon"><svg width="25"><use xlink:href="#i-consignacao"></use></svg></span>
            <h2 class="card-header-title">
                <% if (integradas) { %>
                <hl:message key="rotulo.folha.cadastro.retorno.integracao.retorno"/>
                <% } else { %>
                <hl:message key="rotulo.folha.parcelas.processamento"/>
                <% } %>
            </h2>
        </div>
        <div class="card-body table-responsive">
            <form method="post"
                  action="../v3/cadastrarRetornoIntegracao?acao=listarIntegracao&<%=SynchronizerToken.generateToken4URL(request)%>"
                  name="form1">
                <table class="table table-striped table-hover">
                    <% if (parcelas.isEmpty()) { %>
                    <tr>
                        <td scope="col" colspan="4"><hl:message key="mensagem.erro.cadastro.retorno.integracao"/></td>
                    </tr>
                    <% } else { %>
                    <thead>
                    <tr>
                        <% if (!integradas) { %>
                        <th scope="col" width="10%"
                            title="<hl:message key="rotulo.folha.cadastro.retorno.integracao.checkbox.label"/>">
                            <form class="form-check">
                                <input type="checkbox" class="form-check-input ml-0" id="checkAll">
                            </form>
                        </th>
                        <% } %>
                        <th scope="col"><hl:message key="rotulo.folha.data"/></th>
                        <th scope="col"><hl:message key="rotulo.servidor.singular"/></th>
                        <th scope="col"><hl:message key="rotulo.servidor.matricula"/></th>
                        <th scope="col"><hl:message key="rotulo.estabelecimento.abreviado"/> - <hl:message
                                key="rotulo.orgao.singular"/></th>
                        <th scope="col"><hl:message key="rotulo.folha.cod.verba"/></th>
                        <th scope="col"><hl:message key="rotulo.servico.singular"/></th>
                        <th scope="col"><hl:message key="rotulo.folha.numero.ade"/></th>
                        <th scope="col"><hl:message key="rotulo.folha.numero.parcela"/></th>
                        <th scope="col"><hl:message key="rotulo.folha.valor"/></th>
                        <% if (integradas) { %>
                        <th scope="col"><hl:message key="rotulo.folha.situacao"/></th>
                        <th scope="col"><hl:message key="rotulo.folha.responsavel"/></th>
                        <% } %>
                        <th scope="col"><hl:message key="rotulo.acoes"/></th>
                    </tr>
                    </thead>
                    <tbody>
                    <%
                        Iterator<TransferObject> it = parcelas.iterator();
                        String prd_vlr, prd_data;
                        TransferObject parcela = null;

                        while (it.hasNext()) {
                            parcela = it.next();
                            try {
                                if (integradas) {
                                    prd_vlr = NumberHelper.reformat(parcela.getAttribute(Columns.PRD_VLR_REALIZADO).toString(), "en", NumberHelper.getLang());
                                    prd_data = DateHelper.reformat(parcela.getAttribute(Columns.PRD_DATA_REALIZADO).toString(), "yyyy-MM-dd", LocaleHelper.getDatePattern());
                                } else {
                                    prd_vlr = NumberHelper.reformat(parcela.getAttribute(Columns.PRD_VLR_PREVISTO).toString(), "en", NumberHelper.getLang());
                                    prd_data = DateHelper.reformat(parcela.getAttribute(Columns.PRD_DATA_DESCONTO).toString(), "yyyy-MM-dd", LocaleHelper.getDatePattern());
                                }
                            } catch (Exception ex) {
                                prd_vlr = "";
                                prd_data = "";
                            }

                    %>
                    <% if (!integradas) { %>
                    <tr class="selecionarLinha">
                        <td class="ocultarColuna"
                            aria-label="<hl:message key="rotulo.folha.cadastro.retorno.integracao.checkbox.label"/>"
                            title="<hl:message key="rotulo.folha.cadastro.retorno.integracao.checkbox.label"/>">
                            <div class="form-check">
                                <input type="checkbox" name="PARCELA" class="form-check-input ml-0"
                                       id="selecionarCheckBox"
                                       value="<%=TextHelper.forHtmlAttribute(parcela.getAttribute(Columns.ADE_CODIGO) + ";" + parcela.getAttribute(Columns.PRD_CODIGO))%>">
                            </div>
                        </td>
                        <td class="selecionarColuna"><%=TextHelper.forHtmlContent(prd_data)%>
                        </td>
                        <td class="selecionarColuna"><%=TextHelper.forHtmlContent(parcela.getAttribute(Columns.SER_NOME).toString().toUpperCase())%>
                        </td>
                        <td class="selecionarColuna"><%=TextHelper.forHtmlContent(parcela.getAttribute(Columns.RSE_MATRICULA))%>
                        </td>
                        <td class="selecionarColuna"><%=TextHelper.forHtmlContent(parcela.getAttribute(Columns.EST_IDENTIFICADOR))%>
                            - <%=TextHelper.forHtmlContent(parcela.getAttribute(Columns.ORG_IDENTIFICADOR))%>
                        </td>
                        <td class="selecionarColuna"><%=TextHelper.forHtmlContent(parcela.getAttribute(Columns.CNV_COD_VERBA))%>
                        </td>
                        <td class="selecionarColuna"><%=TextHelper.forHtmlContent(parcela.getAttribute(Columns.SVC_DESCRICAO))%>
                        </td>
                        <td class="selecionarColuna"><%=TextHelper.forHtmlContent(parcela.getAttribute(Columns.ADE_NUMERO))%>
                        </td>
                        <td class="selecionarColuna"><%=TextHelper.forHtmlContent(parcela.getAttribute(Columns.PRD_NUMERO))%>
                        </td>
                        <td class="selecionarColuna"><%=TextHelper.forHtmlContent(prd_vlr)%>
                        </td>
                        <td>
                            <div class="actions">
                                <div class="dropdown">
                                    <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu"
                                       data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                        <div class="form-inline">
                          <span class="mr-1" data-bs-toggle="tooltip" title=""
                                data-original-title="<%=ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)%>"
                                aria-label="<%=ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)%>"> <svg>
                              <use xlink:href="#i-engrenagem"></use></svg>
                          </span> <hl:message key="rotulo.botao.opcoes"/>
                                        </div>
                                    </a>
                                    <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                                        <a class="dropdown-item" href="#" name="selecionaAcaoSelecionar"><hl:message
                                                key="rotulo.acoes.selecionar"/></a>
                                        <a class="dropdown-item" href="#no-back"
                                           onClick="editar('<%=TextHelper.forJavaScript((parcela.getAttribute(Columns.ADE_CODIGO)))%>', '<%=TextHelper.forJavaScript((parcela.getAttribute(Columns.PRD_NUMERO)))%>', '<%=TextHelper.forJavaScript((parcela.getAttribute(Columns.PRD_CODIGO)))%>'); return false;"><hl:message
                                                key="rotulo.acoes.editar"/></a>
                                    </div>
                                </div>
                            </div>
                        </td>
                                <% } else { %>
                    <tr>
                        <td><%=TextHelper.forHtmlContent(prd_data)%>
                        </td>
                        <td><%=TextHelper.forHtmlContent(parcela.getAttribute(Columns.SER_NOME).toString().toUpperCase())%>
                        </td>
                        <td><%=TextHelper.forHtmlContent(parcela.getAttribute(Columns.RSE_MATRICULA))%>
                        </td>
                        <td><%=TextHelper.forHtmlContent(parcela.getAttribute(Columns.EST_IDENTIFICADOR))%>
                            - <%=TextHelper.forHtmlContent(parcela.getAttribute(Columns.ORG_IDENTIFICADOR))%>
                        </td>
                        <td><%=TextHelper.forHtmlContent(parcela.getAttribute(Columns.CNV_COD_VERBA))%>
                        </td>
                        <td><%=TextHelper.forHtmlContent(parcela.getAttribute(Columns.SVC_DESCRICAO))%>
                        </td>
                        <td><%=TextHelper.forHtmlContent(parcela.getAttribute(Columns.ADE_NUMERO))%>
                        </td>
                        <td><%=TextHelper.forHtmlContent(parcela.getAttribute(Columns.PRD_NUMERO))%>
                        </td>
                        <td><%=TextHelper.forHtmlContent(prd_vlr)%>
                        </td>
                        <td><%=TextHelper.forHtmlContent(parcela.getAttribute(Columns.SPD_DESCRICAO))%>
                        </td>
                        <td><%=TextHelper.forHtmlContent(parcela.getAttribute(Columns.PAP_DESCRICAO))%>
                        </td>
                        <td>
                            <% if (responsavel.temPermissao(CodedValues.FUN_CAD_RET_INTEGRACAO)) { %>
                            <div class="actions">
                                <div class="dropdown">
                                    <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu"
                                       data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                        <div class="form-inline">
                            <span class="mr-1" data-bs-toggle="tooltip" title=""
                                  data-original-title="<%=ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)%>"
                                  aria-label="<%=ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)%>"> <svg>
                                <use xlink:href="#i-engrenagem"></use></svg>
                            </span> <hl:message key="rotulo.botao.opcoes"/>
                                        </div>
                                    </a>
                                    <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                                        <a class="dropdown-item" href="#no-back"
                                           onClick="desfazer('<%=TextHelper.forJavaScript((parcela.getAttribute(Columns.ADE_CODIGO)))%>', '<%=TextHelper.forJavaScript((parcela.getAttribute(Columns.PRD_NUMERO)))%>', '<%=TextHelper.forJavaScript((parcela.getAttribute(Columns.PRD_CODIGO)))%>'); return false;"><hl:message
                                                key="rotulo.acoes.desfazer"/></a>
                                        <a class="dropdown-item" href="#no-back"
                                           onClick="editar('<%=TextHelper.forJavaScript((parcela.getAttribute(Columns.ADE_CODIGO)))%>', '<%=TextHelper.forJavaScript((parcela.getAttribute(Columns.PRD_NUMERO)))%>', '<%=TextHelper.forJavaScript((parcela.getAttribute(Columns.PRD_CODIGO)))%>', true); return false;"><hl:message
                                                key="rotulo.acoes.editar"/></a>
                                    </div>
                                </div>
                            </div>
                            <% } else { %>
                            <a href="#no-back"
                               onClick="desfazer('<%=TextHelper.forJavaScript((parcela.getAttribute(Columns.ADE_CODIGO)))%>', '<%=TextHelper.forJavaScript((parcela.getAttribute(Columns.PRD_NUMERO)))%>', '<%=TextHelper.forJavaScript((parcela.getAttribute(Columns.PRD_CODIGO)))%>'); return false;"><hl:message
                                    key="rotulo.acoes.desfazer"/></a>
                            <% } %>
                        </td>
                        <% } %>
                    </tr>
                    <% } %>
                    </tbody>
                    <% } %>
                    <tfoot>
                    <tr>
                        <td colspan="7">
                            <% if (integradas) { %>
                            <hl:message key="rotulo.folha.cadastro.retorno.integracao.listagem.parcelas.integradas"/>
                            <% } else { %>
                            <hl:message key="rotulo.folha.cadastro.retorno.integracao.listagem.parcelas.processamento"/>
                            <% } %> -
                            <span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTitulo"))%></span>
                        </td>
                    </tr>
                    </tfoot>
                </table>
                <% if (filtros) {
                    String adeIdentificador = request.getAttribute("adeIdentificador") != null ? request.getAttribute("adeIdentificador").toString() : "";
                    String periodoIni = request.getAttribute("periodoIni") != null ? request.getAttribute("periodoIni").toString() : "";
                    String periodoFim = request.getAttribute("periodoFim") != null ? request.getAttribute("periodoFim").toString() : "";
                    String tocCodigo = request.getAttribute("tocCodigo") != null ? request.getAttribute("tocCodigo").toString() : "";
                    String orgCodigo = request.getAttribute("orgCodigo") != null ? request.getAttribute("orgCodigo").toString() : "";
                    String situacao = request.getAttribute("situacao") != null ? request.getAttribute("situacao").toString() : "";
                    String papel = request.getAttribute("papel") != null ? request.getAttribute("papel").toString() : "";
                %>
                <input type="hidden" name="adeIdentificador" value="<%=TextHelper.forHtmlAttribute(adeIdentificador)%>">
                <input type="hidden" id="periodoIni" name="periodoIni" value="<%=TextHelper.forHtmlAttribute(periodoIni)%>">
                <input type="hidden" id="periodoFim" name="periodoFim" value="<%=TextHelper.forHtmlAttribute(periodoFim)%>">
                <input type="hidden" name="tocCodigo" value="<%=TextHelper.forHtmlAttribute(tocCodigo)%>">
                <input type="hidden" name="orgCodigo" value="<%=TextHelper.forHtmlAttribute(orgCodigo)%>">
                <input type="hidden" name="situacao" value="<%=TextHelper.forHtmlAttribute(situacao)%>">
                <input type="hidden" name="papel" value="<%=TextHelper.forHtmlAttribute(papel)%>">
                <input type="hidden" id="fil" name="fil" value="S">
                <% } else { %>
                <input type="hidden" id="fil" name="fil" value="N">
                <% } %>
                <input type="hidden" name="operacao">
                <input type="hidden" name="ADE_NUMERO" value="<%=TextHelper.forHtmlAttribute(adeNumero)%>">
                <input type="hidden" name="RSE_MATRICULA" value="<%=TextHelper.forHtmlAttribute(rseMatricula)%>">
                <input type="hidden" name="SER_CPF" value="<%=TextHelper.forHtmlAttribute(serCpf)%>">
                <input type="hidden" name="offset" value="<%=(int)offset%>">
            </form>
        </div>
        <div class="card-footer">
            <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
        </div>
    </div>
    <div class="btn-action">
        <a class="btn btn-outline-danger" href="#"
           onClick="postData('../v3/cadastrarRetornoIntegracao?acao=iniciar&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>'); return false;"><hl:message
                key="rotulo.botao.voltar"/></a>
        <% if (parcelas.size() > 0 && !integradas) { %>
        <a class="btn btn-outline-danger" href="#"
           onClick="javascript:f0.operacao.value='rejeitar'; vf_submit('rejeitadas'); return false;"><hl:message
                key="rotulo.botao.rejeitar"/></a>
        <a class="btn btn-primary" href="#"
           onClick="javascript:f0.operacao.value='liquidar'; vf_submit('liquidadas'); return false;"><hl:message
                key="rotulo.botao.liquidar"/></a>
        <% } %>
    </div>
</c:set>
<c:set var="javascript">
    <script src="../js/colunaCheckbox.js?<hl:message key="release.tag"/>"></script>
    <script type="text/JavaScript">
        var f0 = document.forms[0];
        var f1 = document.forms[1];
        if (<%=!integradas%>) {
            ocultarColuna();
        }

        function vf_submit(acao) {
            var checked = false;

            for (i = 0; i < f0.elements.length; i++) {
                var e = f0.elements[i];
                if (((e.type == 'check') || (e.type == 'checkbox')) && (e.checked == true)) {
                    checked = true;
                }
            }
            if (!checked) {
                alert('<hl:message key="mensagem.folha.escolha.parcela"/>');
            } else {
                if (confirm('<hl:message key="mensagem.folha.confirma.operacao"/>'.replace('{0}', acao))) {
                    f0.submit();
                }
            }
        }

        function desfazer(ade_codigo, prd_numero, prd_codigo) {
            if (confirm('<hl:message key="mensagem.folha.confirma.desfazer.integracao"/>')) {
                var URL = "../v3/cadastrarRetornoIntegracao?acao=editarIntegracao&ade_codigo=" + ade_codigo + "&prd_numero=" + prd_numero + "&prd_codigo=" + prd_codigo
                    + "&operacao=desfazer&<%=SynchronizerToken.generateToken4URL(request)%>";
                postData(URL);
            }
        }

        function editar(ade_codigo, prd_numero, prd_codigo, altera) {
            var URL = "../v3/cadastrarRetornoIntegracao?acao=editarIntegracao&ade_codigo=" + ade_codigo + "&prd_numero=" + prd_numero + "&prd_codigo=" + prd_codigo
                + '&<%=SynchronizerToken.generateToken4URL(request)%>';
            if (altera) {
                URL += "&alterarParcela=true"
            }
            postData(URL);
        }

        function liquidarTodas() {
            if (confirm('<hl:message key="mensagem.folha.confirma.liquidar.todas.parcelas"/>')) {
                f0.operacao.value = "L"
                f0.submit();
            }
        }

        function rejeitarTodas() {
            if (confirm('<hl:message key="mensagem.folha.confirma.rejeitar.todas.processamento"/>')) {
                f0.operacao.value = "R"
                f0.submit();
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

