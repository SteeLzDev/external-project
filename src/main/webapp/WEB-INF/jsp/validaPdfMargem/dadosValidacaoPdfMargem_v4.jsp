<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page  import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.dto.TransferObject" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" /> <%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String versaoLeiaute = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel)) ? "v4" : (String) ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel);

TransferObject dados = (TransferObject) request.getAttribute("controle");
%>
<!DOCTYPE html>
<html lang="<%=LocaleHelper.getLocale()%>">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title><%=TextHelper.forHtmlContent(JspHelper.getNomeSistema(responsavel))%></title> <%-- Bootstrap core CSS --%>
    <link href="<c:url value='/node_modules/bootstrap/dist/css/bootstrap.min.css'/>" rel="stylesheet" type="text/css"> <%-- Font Monteserrat (titulos) e Roboto (corpo) CSS --%>
    <link href="<c:url value='/css/fonts/Google/family_Montserrat.css'/>" rel="stylesheet" type="text/css"> <%-- Arquivos Especializads CSS --%> 
    <% if ("v5".equals(versaoLeiaute)) { %>
    <link href="<c:url value='/css/econsig_v5.css'/>" rel="stylesheet" type="text/css">
    <link href="<c:url value='/css/custom_v5.css'/>" rel="stylesheet" type="text/css">
    <link href="<c:url value='/css/custom_v5_client.css'/>" rel="stylesheet" type="text/css">
    <% } else if ("v6".equals(versaoLeiaute)) { %>
    <link href="<c:url value='/css/econsig_v6.css'/>" rel="stylesheet" type="text/css">
    <link href="<c:url value='/css/custom_v6.css'/>" rel="stylesheet" type="text/css">
    <link href="<c:url value='/css/custom_v6_client.css'/>" rel="stylesheet" type="text/css">
    <% } else { %>
    <link href="<c:url value='/css/econsig.css'/>" rel="stylesheet" type="text/css">
    <link href="<c:url value='/css/custom_v4.css'/>" rel="stylesheet" type="text/css">
    <link href="<c:url value='/css/custom_v4_client.css'/>" rel="stylesheet" type="text/css"> 
    <% } %>
    <link href="<c:url value='/css/botui.min.css'/>" rel="stylesheet" type="text/css" />
    <link href="<c:url value='/css/botui-theme-default.css'/>" rel="stylesheet" type="text/css" />
</head>

<body>
    <div id="mensagemSucesso" class="alert alert-success alert-dismissible fade show w-100 text-center" role="alert">
     <hl:message key="rotulo.controle.validacao.sucesso"/><button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Fechar"></button>
    </div>
    <div class="container mt-3">
        <div class="logo-externa">
            <img src="../img/view.jsp?nome=login/logo_cse.gif" alt="<hl:message key="rotulo.texto.alternativo.logo.sistema"/>">
        </div>
        <div class="card box">
            <div class="card-header">
                <h2 class="card-header-title">
                    <hl:message key="rotulo.motivo.operacao.singular" />
                </h2>
            </div>
            <div class="card-body">
                <table>
                    <div>
                        <tr>
                            <td><strong>
                                    <hl:message key="rotulo.registrar.ocorrencia.contrato.nome.servidor"/>:
                                </strong> </td>
                            <td><%=dados.getAttribute(Columns.SER_NOME)%></td>
                        </tr>
                    </div>
                    <div>
                        </br>
                        <tr>
                            <td><strong>
                                    <hl:message key="rotulo.folha.data.hora" />:
                                </strong></td>
                            <td><%=dados.getAttribute(Columns.CDM_DATA)%></td>
                        </tr>
                    </div>
                </table>
            </div>
            <div class="btn-action btn-footer">
                                <a class="btn btn-outline-danger" href="../v3/validaPdf?acao=consultar">Voltar</a>
                                <button class="btn btn-primary" onclick="downloadOrView('<%=dados.getAttribute(Columns.CDM_LOCAL_ARQUIVO)%>', 'download'); return false;">Download</button>
                                <button class="btn btn-fourth" onclick="downloadOrView('<%=dados.getAttribute(Columns.CDM_LOCAL_ARQUIVO)%>', 'abrir'); return false;">Abrir</button>
                            </div>
        </div>
    </div>
    <script src="<c:url value='/js/validalogin.js'/>?<hl:message key='release.tag'/>"></script>
    <script src="<c:url value='/js/scripts_2810.js'/>?<hl:message key='release.tag'/>"></script>
    <script src="<c:url value='/node_modules/jquery/dist/jquery.min.js'/>?<hl:message key='release.tag'/>"></script>
    <script src="<c:url value='/js/jquery-nostrum.js'/>?<hl:message key='release.tag'/>"></script>
    <script src="<c:url value='/node_modules/bootstrap/dist/js/bootstrap.bundle.min.js'/>"></script>
    <script type="text/javascript">

        function downloadOrView(path, acao) {
            var dataToSend = JSON.stringify({
                'path': path,
                'acao': acao
            });
            $.ajax({
                url: "../v3/validaPdf?acao=gerarPdf&<%=SynchronizerToken.generateToken4URL(request)%>&_skip_history_=true",
                type: "POST",
                contentType: "application/json; charset=utf-8",
                data: dataToSend,
                xhrFields: {
                    responseType: 'blob'
                },
                success: function(response, status, xhr) {
                    var filename = "";
                    var disposition = xhr.getResponseHeader("Content-Disposition");
                    if (disposition && disposition.indexOf("filename") !== -1) {
                        var filenameRegex = /filename[^;=\n]*=((["']).*?\2|[^;\n]*)/;
                        var matches = filenameRegex.exec(disposition);
                        if (matches != null && matches[1]) {
                            filename = matches[1].replace(/[""]/g, "");
                        }
                    }
                    var blob = new Blob([response], {
                        type: 'application/pdf'
                    });
                    var url = window.URL.createObjectURL(blob);
                    if (acao === "download") {
                        var link = document.createElement('a');
                        link.href = url;
                        link.download = filename;
                        link.click();
                    } else if (acao === "abrir") {
                        window.open(url, '_blank'); // Abre o PDF em nova aba
                    }
                },
                error: function(request, status, error) {
                    alert("Erro ao processar o PDF.");
                }
            });
        }
    </script>
    <style>
    .container {
        width: 100vw;
        height: 100vh;
        display: flex;
        flex-direction: row;
        justify-content: center;
        align-items: center;
    }

    .box {
        width: 1400px;
        height: 400px;
        background: #F4F5F6;

    }
    </style>
</body>

</html>