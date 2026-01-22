<%--
* <p>Title: editarServicoOrg</p>
* <p>Description: edita o servico do orgão</p>
* <p>Copyright: Copyright (c) 2005</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: andrea.giorgini $
* $Revision: 26722 $
* $Date: 2019-05-16 07:55:10 -0300 (qui, 16 mai 2019) $
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession"/>
<%
    AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

    boolean podeEditarSvc = (Boolean) request.getAttribute("podeEditarSvc");

    String orgao = (String) request.getAttribute("orgao");
    String servico = (String) request.getAttribute("servico");
    String cnv_consolida_descontos = (String) request.getAttribute("cnv_consolida_descontos");
    String svc_descricao = (String) request.getAttribute("svc_descricao");
    String svc_identificador = (String) request.getAttribute("svc_identificador");
    String org_nome = (String) request.getAttribute("org_nome");
%>
<c:set var="title">
    <hl:message key="rotulo.servico.manutencao.titulo"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
    <div class="row">
        <div class="col-sm">
            <div class="card">
                <div class="card-header">
                    <h2 class="card-header-title"><%=TextHelper.forHtmlAttribute(svc_descricao)%>
                        - <%=TextHelper.forHtmlContent(org_nome)%>
                    </h2>
                </div>
                <div class="card-body">
                    <form method="post"
                          action="../v3/manterServico?acao=editarServicoOrg&<%=SynchronizerToken.generateToken4URL(request)%>"
                          name="form1">
                        <input name="tipo" type="hidden" value="editar">
                        <input name="svc" type="hidden" value="<%=TextHelper.forHtmlAttribute(servico)%>">
                        <input name="org" type="hidden" value="<%=TextHelper.forHtmlAttribute(orgao)%>">
                        <input type="hidden" name="MM_update" value="form1">
                        <div class="row">
                            <div class="form-group col-sm-12 col-md-4 mt-1" aria-labelledby="consolidaDescontos">
                                <span id="consolidaDescontos"><hl:message
                                        key="rotulo.servico.consolida.descontos"/></span>
                                <br>
                                <div class="form-check-inline form-check">
                                    <INPUT TYPE="radio" NAME="CNV_CONSOLIDA_DESCONTOS"
                                           VALUE="S" <%if(!podeEditarSvc){%> disabled <%}%>
                                            <%=(String)(cnv_consolida_descontos.equals("S")?"CHECKED":"")%> id='sim'>
                                    <label class="labelSemNegrito" for="sim"><hl:message key="rotulo.sim"/></label>
                                </div>
                                <div class="form-check-inline form-check">
                                    <INPUT TYPE="radio" NAME="CNV_CONSOLIDA_DESCONTOS" VALUE="N" <%if(!podeEditarSvc){%>
                                           disabled <%}%> <%=(String)(cnv_consolida_descontos.equals("N")?"CHECKED":"")%>
                                           id='nao'>
                                    <label class="labelSemNegrito" for="nao"><hl:message key="rotulo.nao"/></label>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
    <div class="btn-action">
        <%if (podeEditarSvc) {%>
        <a class="btn btn-outline-danger" href="#no-back"
           onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message
                key="rotulo.botao.cancelar"/></a>
        <a class="btn btn-primary" HREF="#no-back" onClick="f0.submit(); return false;" ><hl:message
                key="rotulo.botao.salvar"/></a>
        <%} else {%>
        <a class="btn btn-outline-danger" href="#no-back"
           onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message
                key="rotulo.botao.voltar"/></a>
        <%}%>
    </div>
</c:set>
<c:set var="javascript">
    <script type="text/JavaScript" src="../js/scripts_2810.js"></script>
    <script type="text/JavaScript">
        var f0 = document.forms[0];
        window.onload = formLoad;

        function formLoad() {
            focusFirstField();
        }
    </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>