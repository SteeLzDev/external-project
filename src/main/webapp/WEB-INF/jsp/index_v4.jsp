<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.usuario.LoginHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="java.util.List" %>
<%@ page import="com.zetra.econsig.dto.TransferObject" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<jsp:useBean id="cseDelegate" scope="page" class="com.zetra.econsig.delegate.ConsignanteDelegate"/>
<%
    AcessoSistema responsavel = (AcessoSistema) request.getAttribute("responsavel");
    String versaoLeiaute = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel)) ? "v4" : (String) ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel);
    String nomeSistema = JspHelper.getNomeSistema(responsavel);
    String nomeCse = LoginHelper.getCseNome(responsavel);
    String linkLoginPadrao = (String) request.getAttribute("linkLoginPadrao");
    String linkLoginServidor = (String) request.getAttribute("linkLoginServidor");
    List<TransferObject> lstMensagens = (List<TransferObject>) request.getAttribute("lstMensagens");
%>

<html>
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link href="../node_modules/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet">
    <% if ("v5".equals(versaoLeiaute)) { %>
    <link href="../css/welcome_v5.css" rel="stylesheet" type="text/css">
    <link href="../css/custom_v5.css" rel="stylesheet" type="text/css">
    <link href="../css/custom_v5_client.css" rel="stylesheet" type="text/css">
    <link href="../css/econsig_V5.css" rel="stylesheet">
    <% } else if ("v6".equals(versaoLeiaute)) { %>
    <link href="../css/welcome_v6.css" rel="stylesheet" type="text/css">
    <link href="../css/custom_v6.css" rel="stylesheet" type="text/css">
    <link href="../css/custom_v6_client.css" rel="stylesheet" type="text/css">
    <link href="../css/econsig_v6.css" rel="stylesheet">
    <% } else { %>
    <link href="../css/econsig.css" rel="stylesheet">
    <link href="../css/custom_v4.css" rel="stylesheet" type="text/css">
    <link href="../css/welcome_v4.css" rel="stylesheet" type="text/css">
    <link href="../css/custom_v4_client.css" rel="stylesheet" type="text/css">
    <% } %>
    
    <title><%=TextHelper.forHtml(nomeSistema)%>
    </title>

</head>
<body>
<main>
    <aside class="page-image">
        <%if("v6".equals(versaoLeiaute)) {%>
            <img src="../img/logo_salary_serasa.svg">
        <% } else { %>
            <img src="../img/view.jsp?nome=capa.jpg">
        <% } %>
    </aside>
    <section class="page-content">
        <div class="page-container">
            <div class="grid-space-md"></div>
            <header>
                <div class="page-presentation">
                    <h1><span class="hello-style"><hl:message key="label.boas.vindas.ola"/> </span><span
                            class="main-title"><hl:message key="label.boas.vindas.cabecalho"/></span></h1>
                    <span class="page-logo-econsig">
							<svg>
                                 <%if ("v5".equals(versaoLeiaute)) {%>
								    <use xlink:href="../img/sprite_v5.svg#i-logo-econsig"></use>
                                 <% } else if ("v6".equals(versaoLeiaute)) {%>
                                    <use xlink:href="../img/sprite_v6.svg#i-logo-econsig"></use>
                                 <% } else { %>
                                    <use xlink:href="../img/sprite.svg#i-logo-econsig"></use>
                                 <% } %>
                            </svg>
						</span>
                </div>
                <%if("v5".equals(versaoLeiaute)) {%>
                <div class="page-logo-partner">
                    <img src="../img/logo_empresa_v5.png" alt="logotipo da empresa"/>
                </div>
                <% } else if ("v6".equals(versaoLeiaute)) { %>
                <div class="page-logo-partner">
                    <img src="../img/logo_sistema_v5.png" alt="logotipo da empresa"/>
                </div>
                <% } else { %>
                <div class="page-logo-partner">
                    <img src="../img/logo_empresa.png" alt="logotipo da empresa"/>
                </div>
                <% } %>
            </header>
            <div class="grid-center"></div>
            <section>
                <label><hl:message key="mensagem.label.acesso"/></label>
                <div class="btn-actions">
                    <a class="card-btn card-btn-wine" href="<%=TextHelper.forHtmlAttribute(linkLoginServidor)%>">
                        <div class="btn-icon">
                            <svg>
                                <use xlink:href="../img/sprite.svg#i-login-servidor"></use>
                            </svg>
                        </div>
                        <div class="btn-text">
                            <h5><span style="font-weight: 600 !important;"><hl:message key="rotulo.boas.vindas.botao.servidor"/></span></h5>
                        </div>
                    </a>
                    <a class="card-btn card-btn-white" href="<%=TextHelper.forHtmlAttribute(linkLoginPadrao)%>">
                        <div class="btn-icon">
                            <svg>
                                <%if ("v4".equals(versaoLeiaute)) { %>
                                <use xlink:href="../img/sprite.svg#i-login-csa"></use>
                                <% } else { %>
                                <use xlink:href="../img/sprite_v5.svg#i-login-csa"></use>
                                <%}%>
                            </svg>
                        </div>
                        <div class="btn-text">
                            <h5><span class="text-button"><hl:message key="rotulo.boas.vindas.botao.csa.gestor"/></span>
                            <span class="text-button-ou"><hl:message key="rotulo.boas.vindas.botao.ou"/></span>
                            <span class="text-button"><hl:message key="rotulo.boas.vindas.botao.csa.consignataria"/></span></h5>
                        </div>
                    </a>
                </div>
            </section>
            <div class="grid-top"></div>
            <div class="division"></div>
            <div class="grid-top"></div>
            <div class="page-slide">
                <div id="carouselIndicators" class="carousel slide" data-bs-ride="carousel">
                    <ol class="carousel-indicators">
                        <%
                            int total = lstMensagens.size();
                            int count = 0;
                            while (count < total) {
                                if (count == 0) {

                        %>
                        <li data-bs-target="#carouselIndicators" data-bs-slide-to="<%=count%>" aria-current="true" class="active"></li>
                        <%
                        } else {
                        %>
                        <li data-bs-target="#carouselIndicators" data-bs-slide-to="<%=count%>"></li>
                        <%
                                }
                                count++;
                            }
                        %>
                    </ol>
                    <div class="carousel-inner">
                        <%
                            String msgTitulo = null;
                            String msgTexto = null;
                            boolean primeiro = true;
                            String active = "active";
                            for (TransferObject mensagem : lstMensagens) {
                                msgTitulo = mensagem.getAttribute(Columns.MEN_TITULO).toString();
                                msgTexto = mensagem.getAttribute(Columns.MEN_TEXTO).toString();
                                if (!primeiro) {
                                    active = "";
                                }
                        %>
                        <div class="carousel-item <%=active%>">
                            <h6 class="msg-title"><%=msgTitulo%>
                            </h6>
                            <p><%=msgTexto%></p>
                        </div>
                        <%
                                primeiro = false;
                            }
                        %>
                    </div>
                    <button class="carousel-control-prev" type="button" data-bs-target="#carouselIndicators" data-bs-slide="prev">
                        <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                        <span class="visually-hidden">Previous</span>
                    </button>
                    <button class="carousel-control-next" type="button" data-bs-target="#carouselIndicators" data-bs-slide="next">
                        <span class="carousel-control-next-icon" aria-hidden="true"></span>
                        <span class="visually-hidden">Next</span>
                    </button>
                </div>
            </div>
        </div>
    </section>
</main>
<script src="../node_modules/jquery/dist/jquery.min.js?<hl:message key="release.tag"/>"></script>
<script src="<c:url value='/node_modules/@popperjs/core/dist/umd/popper.min.js'/>"></script>
<script src="<c:url value='/node_modules/bootstrap/dist/js/bootstrap.min.js'/>"></script>
</body>
</html>