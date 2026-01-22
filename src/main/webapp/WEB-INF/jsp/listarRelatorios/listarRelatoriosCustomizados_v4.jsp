<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.web.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%
    AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
    String csaCodigo = (String) request.getAttribute("consignataria");
    List<TransferObject> consignatarias = (List<TransferObject>) request.getAttribute("consignatarias");
    List<ArquivoDownload> arquivosCombo = (List<ArquivoDownload>) request.getAttribute("arquivosCombo");
%>
<c:set var="imageHeader">
    <use xlink:href="#i-relatorio"></use>
</c:set>
<c:set var="title">
    <hl:message key="rotulo.relatorio.customizado"/>
</c:set>
<c:set var="bodyContent">
    <form name="form1" method="POST" action="../v3/listarRelatorioCustomizado?acao=iniciar">
        <% if (responsavel.isSup()) { %>
        <div class="card">
            <div class="card-header hasIcon">
                <span class="card-header-icon"><svg width="25"><use
                        xlink:href="../img/sprite.svg#i-relatorio"></use></svg></span>
                <h2 class="card-header-title"><hl:message key="rotulo.relatorio.customizado"/></h2>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="form-group col-sm-6">
                        <label for="CSA_CODIGO_AUX"><hl:message key="rotulo.consignataria.singular"/></label>
                        <select class="form-control" id="consignataria" name="consignataria" onChange="alteraCsa();">
                            <OPTION value="" <%=(String) (csaCodigo != null && csaCodigo.equals("") ? "SELECTED" : "")%>>
                                <hl:message key="rotulo.campo.selecione"/></OPTION>
                            <%
                                Iterator<?> it = consignatarias.iterator();
                                CustomTransferObject csa = null;
                                String csa_nome = null;
                                String csa_codigo = "";
                                String csa_identificador = "";
                                while (it.hasNext()) {
                                    csa = (CustomTransferObject) it.next();
                                    csa_codigo = (String) csa.getAttribute(Columns.CSA_CODIGO);
                                    csa_identificador = (String) csa.getAttribute(Columns.CSA_IDENTIFICADOR);
                                    csa_nome = (String) csa.getAttribute(Columns.CSA_NOME_ABREV);
                                    if (csa_nome == null || csa_nome.trim().length() == 0)
                                        csa_nome = csa.getAttribute(Columns.CSA_NOME).toString();
                            %>
                            <OPTION VALUE="<%=TextHelper.forHtmlAttribute(csa_codigo)%>" <%=(String) (csaCodigo != null && csaCodigo.equals(csa_codigo) ? "SELECTED" : "")%>><%=TextHelper.forHtmlContent(csa_identificador)%>
                                - <%=TextHelper.forHtmlContent(csa_nome)%>
                            </OPTION>
                            <%
                                }
                            %>
                        </select>
                    </div>
                </div>
            </div>
        </div>
        <% } %>
        <div class="card">
            <div class="card-header hasIcon">
                <span class="card-header-icon"><svg width="25"><use
                        xlink:href="../img/sprite.svg#i-relatorio"></use></svg></span>
                <h2 class="card-header-title"><hl:message key="rotulo.listar.arq.relatorio.download"/></h2>
            </div>
            <div class="card-body table-responsive">
                <table class="table table-striped table-hover">
                    <thead>
                    <tr>
                        <th scope="col"><hl:message key="rotulo.upload.arquivo.nome"/></th>
                        <th scope="col"><hl:message key="rotulo.upload.arquivo.tamanho"/></th>
                        <th scope="col"><hl:message key="rotulo.upload.arquivo.data"/></th>
                        <th scope="col" width="15%"><hl:message key="rotulo.acoes.upload.arquivo.acoes"/></th>
                    </tr>
                    </thead>
                    <tbody>

                    <%
                        if (arquivosCombo == null || arquivosCombo.size() == 0) {
                    %>
                    <tr class="Lp">
                        <td colspan="7"><hl:message key="mensagem.erro.upload.arquivo.nenhum.encontrado"/></td>
                    </tr>
                    <%
                    } else {
                        int i = 0;
                        for (ArquivoDownload arquivo : arquivosCombo) {
                    %>
                    <tr>
                        <td><%=TextHelper.forHtmlContent(arquivo.getNomeOriginal())%>
                        </td>
                        <td><%=TextHelper.forHtmlContent(arquivo.getTamanho())%>
                        </td>
                        <td><%=TextHelper.forHtmlContent(arquivo.getData())%>
                        </td>
                        <td>
                            <div class="actions">
                                <a class="ico-action" href="#">
                                    <div class="form-inline"
                                         onClick="javascript:downloadArquivo(encodeURIComponent('<%=TextHelper.forJavaScript(arquivo.getNome())%>')); return false;">
                         <span class="mr-1" data-bs-toggle="tooltip"
                               aria-label="<hl:message key="rotulo.botao.download"/> <%=arquivo.getNomeOriginal()%>"
                               title="" data-original-title="download">
                           <svg> <use xlink:href="../img/sprite.svg#i-download"></use></svg>
                         </span>
                                        <hl:message key="rotulo.acoes.upload.arquivo.download"/>
                                    </div>
                                </a>
                            </div>
                        </td>
                    </tr>
                    <%
                            }
                        }
                    %>

                    </tbody>
                    <tfoot>
                    <tr>
                        <td colspan="5"></td>
                    </tr>
                    </tfoot>


                </table>
            </div>
        </div>

        <div class="btn-action">
            <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal')"><hl:message
                    key="rotulo.botao.voltar"/></a>
        </div>
    </form>
</c:set>
<c:set var="javascript">
    <script>
        var f0 = document.forms[0];

        function alteraCsa() {
            f0.submit();
        }

        function downloadArquivo(arquivo) {
            var csaSelect = null;
            var type = null;
            <%if(responsavel.isSup()) {%>
            csaSelect = document.getElementById("consignataria").value;
            type = csaSelect !== "" ? "relatorioCustomizadoCsa" : "relatorioCustomizadoCse";
            <%}%>
            <% if(responsavel.isCsa()) { %>
            type = "relatorioCustomizadoCsa";
            <% } %>
            <% if(responsavel.isCse()) { %>
            type = "relatorioCustomizadoCse";
            <% } %>
            var csa = "<%=csaCodigo%>";
            endereco = "../v3/downloadArquivo?arquivo_nome=" + arquivo + "&tipo=" + type + "&csa_codigo=" + csa + "&" + "<%=SynchronizerToken.generateToken4URL(request)%>";
            postData(endereco, 'download');
        }
    </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>