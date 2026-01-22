<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.dto.TransferObject" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="java.util.Objects" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession"/>
<%
    AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

    String csaCodigo = (String) request.getAttribute("csaCodigo");
    List<?> servicos = (List<?>) request.getAttribute("servicos");
    List<?> postos = (List<?>) request.getAttribute("postos");
    String svcSelected = (String) request.getAttribute("svcSelected");
    boolean exibePostos = (boolean) request.getAttribute("exibePostos");
    String btnVoltar = (String) request.getAttribute("btnCancelar");
%>
<c:set var="title">
    <hl:message key="rotulo.manutencao.valor.fixo.posto"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
    <div class="">
        <div class="card">
            <div class="card-header">
                <h2 class="card-header-title"><hl:message key="rotulo.editar.grid"/></h2>
            </div>
            <div class="card-body">
                <form method="post"
                      class="needs-validation"
                      action="../v3/manterConsignataria?acao=salvarValorFixoPosto&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>"
                      name="form1">
                    <div class="form-group col-sm-6">
                        <label for="SVC_CODIGO"><hl:message key="rotulo.campo.selecione.servico"/></label>
                        <select class="select form-select form-select-sm" name="SVC_CODIGO" id="SVC_CODIGO"
                                onChange="changePosto();">
                            <option value="" <%=((TextHelper.isNull(servicos) && svcSelected.equals("")) ? "SELECTED" : "")%>>
                                <hl:message
                                        key="rotulo.campo.selecione"/></option>
                            <%
                                Iterator<?> iteSvc = servicos.iterator();
                                while (iteSvc.hasNext()) {
                                    TransferObject ctoSvc = (TransferObject) iteSvc.next();
                                    String fieldValueSvc = ctoSvc.getAttribute(Columns.SVC_CODIGO).toString();
                                    String fielLabelSvc = ctoSvc.getAttribute(Columns.SVC_DESCRICAO).toString();
                            %>
                            <option value="<%=TextHelper.forHtmlAttribute(fieldValueSvc)%>" <%=(String) (fieldValueSvc.equals(svcSelected) ? "SELECTED" : "")%>><%=TextHelper.forHtmlContent(fielLabelSvc)%>
                            </option>
                            <% } %>
                        </select>

                    </div>
                    <% if (exibePostos) { %>
                    <table id="tablePostos" class="table table-striped table-hover" style="width:100%">
                        <thead>
                        <tr>
                            <th>
                                <hl:message key="rotulo.posto.postos"/>
                            </th>
                            <th>
                                <hl:message key="rotulo.posto.valor.fixo"/>
                            </th>
                        </tr>
                        </thead>
                        <tbody>
                        <%
                            Iterator<?> itePos = postos.iterator();
                            while (itePos.hasNext()) {
                                TransferObject ctoPos = (TransferObject) itePos.next();
                                String fieldPpoVlr = (ctoPos.getAttribute(Columns.PSP_PPO_VALOR) != null ? ctoPos.getAttribute(Columns.PSP_PPO_VALOR).toString() : "");
                                String fieldLabelPos = ctoPos.getAttribute(Columns.POS_DESCRICAO).toString();
                                String fieldValueId = ctoPos.getAttribute(Columns.POS_CODIGO).toString();
                        %>
                        <tr>
                            <td>
                                <%=fieldLabelPos %>
                            </td>
                            <td>
                                <label type="hidden" for="<%=fieldValueId%>"></label>
                                <input placeholder="<%=ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel)%>"
                                       id="<%=fieldValueId%>"
                                       name="<%=fieldValueId%>"
                                       value="<%=fieldPpoVlr%>"
                                       type="text"
                                       onkeyup="formatarMoeda(this)">
                            </td>

                        </tr>
                        <%
                            }
                        %>
                        </tbody>
                    </table>
                    <% } %>
                </form>
            </div>
        </div>
    </div>
    <div id="actions" class="btn-action">
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=btnVoltar%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
        <% if (!Objects.equals(svcSelected, "")) { %>
        <a class="btn btn-primary" type="submit" href="#no-back" onClick="submitform(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
        <% } %>
    </div>
</c:set>
<c:set var="javascript">
    <script type="text/JavaScript" src="../js/listagem.js"></script>
    <script type="text/JavaScript">
        f0 = document.forms[0];

        function changePosto() {
            const selectSvc = document.getElementById("SVC_CODIGO")
            const svcCodigo = selectSvc.options[selectSvc.selectedIndex].value;

            if (svcCodigo.length > 0) {
                postData('../v3/manterConsignataria?acao=editarVlrPostoFixo&SVC_SELECTED=' + svcCodigo + '&POSTO_VOLTA=S&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');
            } else {
                postData('../v3/manterConsignataria?acao=editarVlrPostoFixo&SVC_SELECTED=' + svcCodigo + '&POSTO_VOLTA=N&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');
            }
        }

        function submitform() {
            const selectSvc = document.getElementById("SVC_CODIGO")
            const svcCodigo = selectSvc.options[selectSvc.selectedIndex].value;

            if (svcCodigo.length <= 0) {
                window.alert("Selecione um serviço")
            } else {
                f0.submit();
            }
        }

        function formatarMoeda(i) {
            var v = i.value.replace(/\D/g, '');
            v = (v / 100).toFixed(2) + '';
            v = v.replace(".", ",");
            v = v.replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1.');
            i.value = v;
        }
    </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>