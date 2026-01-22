<%--
  Created by IntelliJ IDEA.
  User: douglas.neves
  Date: 17/10/2022
  Time: 18:32
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.dto.web.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    AcessoSistema responsavel = (AcessoSistema) request.getAttribute("responsavel");
    boolean selecionaEstOrgUploadMargemRetorno = (boolean) request.getAttribute("selecionaEstOrgUploadMargemRetorno");
    boolean selecionaEstOrgUploadContracheque = (boolean) request.getAttribute("selecionaEstOrgUploadContracheque");
    boolean temProcessoRodando = (boolean) request.getAttribute("temProcessoRodando");
    boolean exibeCaptchaAvancado = (boolean) request.getAttribute("exibeCaptchaAvancado");
    boolean exibeCaptchaDeficiente = (boolean) request.getAttribute("exibeCaptchaDeficiente");
    boolean exibeCampoUpload = (boolean) request.getAttribute("exibeCampoUpload");
    List<ArquivoDownload> arquivosCombo = (List<ArquivoDownload>) request.getAttribute("arquivosCombo");
    List<?> codigosOrgao = (List<?>) request.getAttribute("codigosOrgao");
    String papCodigo = (String) request.getAttribute("papCodigo");
    String action = (String) request.getAttribute("action");
    String fluxo = (String) request.getAttribute("fluxo");
%>
<c:set var="title">
    <hl:message key="rotulo.upload.arquivo.titulo"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-upload"></use>
</c:set>
<c:set var="bodyContent">
    <% if (!temProcessoRodando) { %>
    <div class="page-title">
        <div id="uploadArquivo">
            <hl:uploadArquivosv4/>
        </div>
    </div>
    <div class="btn-action">
        <% if(fluxo.equals("uploadListarRetornoAtrasado")) { %>
        <a class="btn btn-outline-danger" aria-label='<hl:message key="rotulo.botao.voltar"/>' href="#no-back"
           onClick="postData('../v3/listarArquivosRetornoAtrasado?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>');"><hl:message key="rotulo.botao.voltar"/></a>
        <% } else if (fluxo.equals("uploadListarRetornoIntegracao")) { %>
        <a class="btn btn-outline-danger" aria-label='<hl:message key="rotulo.botao.voltar"/>' href="#no-back"
           onClick="postData('../v3/listarRetornoIntegracao?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>');"><hl:message key="rotulo.botao.voltar"/></a>
        <% } else if (fluxo.equals("uploadListarMargem") || fluxo.equals("uploadListarMargemComplementar") || fluxo.equals("transferidos")) { %>
        <a class="btn btn-outline-danger" aria-label='<hl:message key="rotulo.botao.voltar"/>' href="#no-back"
           onClick="postData('../v3/importarMargem?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>');"><hl:message key="rotulo.botao.voltar"/></a>
        <%  } else if (fluxo.equals("uploadListarHistorico")) { %>
        <a class="btn btn-outline-danger" aria-label='<hl:message key="rotulo.botao.voltar"/>' href="#no-back"
           onClick="postData('../v3/importarHistorico?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>');"><hl:message key="rotulo.botao.voltar"/></a>
        <%  } %>
        <%if (exibeCampoUpload) { %>
        <button class="btn btn-primary" type="submit" onClick="if(vf_upload_arquivos()){ f0.submit();} return false"><svg width="17"><use xlink:href="#i-confirmar"></use></svg> <hl:message key="rotulo.botao.confirmar"/></button>
        <% } %>
    </div>
    <% } %>
</c:set>
<c:set var="javascript">
    <% if (exibeCaptchaAvancado) { %>
    <script src='https://www.google.com/recaptcha/api.js'></script>
    <% } %>
    <script type="text/JavaScript">
        function formFullLoad() {
            formLoad();
            doLoad(<%=(boolean)(temProcessoRodando)%>);
        }

        function formLoad() {
            if (f0.tipo != null) {
                f0.tipo.focus();
            }
            <% if (exibeCaptchaDeficiente) {%>
            montaCaptchaSom();
            <% } %>
        }

        function doLoad(reload) {
            if (reload) {
                setTimeout("refresh()", 10 * 1000);
            }
        }

        function refresh() {
            postData('<%=SynchronizerToken.updateTokenInURL(action.split("\\?")[0] + "?acao=carregar", request)%>');
        }

        function vf_nome_arquivo() {
            var orgCodigo = '';
            <% if ((selecionaEstOrgUploadMargemRetorno || selecionaEstOrgUploadContracheque) && responsavel.isCseSup()) { %>
            var papCodigo = getCheckedRadio('form2', 'PAP_CODIGO');

            if (papCodigo != null) {
                if (papCodigo == 'ORG') {
                    orgCodigo = f0.ORG_CODIGO.options[f0.ORG_CODIGO.selectedIndex].value;
                    if (orgCodigo == '') {
                        alert('<%=ApplicationResourcesHelper.getMessage("mensagem.aviso.upload.selecione.orgao", responsavel)%>');
                        f0.ORG_CODIGO.focus();
                        f0.FILE1.value = '';
                        return;
                    }
                } else if (papCodigo == 'EST') {
                    estCodigo = f0.EST_CODIGO.options[f0.EST_CODIGO.selectedIndex].value;
                    if (estCodigo == '') {
                        alert('<%=ApplicationResourcesHelper.getMessage("mensagem.aviso.upload.selecione.estabelecimento", responsavel)%>');
                        f0.EST_CODIGO.focus();
                        f0.FILE1.value = '';
                        return;
                    }
                }
            }
            <%  } %>

            var targetFileName = f0.FILE1.value;
            <%
                if (arquivosCombo != null && !arquivosCombo.isEmpty()) {
            %>
            if (targetFileName != null && targetFileName != '') {
                var arrayFiles = new Array("<%=TextHelper.forJavaScriptBlock(TextHelper.join(arquivosCombo, "\",\""))%>");
                for (i = 0; i < arrayFiles.length; i++) {
                    var nomeAbrev = targetFileName.substring(targetFileName.lastIndexOf("\\") + 1, targetFileName.length);
                    var codIdnResponsavel = '<%=TextHelper.forJavaScriptBlock(responsavel.getCodigoEntidade())%>';
                    if (arrayFiles[i].replace(/^.*[\\\/]/, '') == nomeAbrev) {
                        <% if (TextHelper.isNull(papCodigo) || papCodigo.equals(AcessoSistema.ENTIDADE_CSE)) { %>
                        <%if (responsavel.getTipoEntidade().equals(AcessoSistema.ENTIDADE_CSE)) {%>
                        var path = "cse/";
                        if (orgCodigo != '') {
                            path += orgCodigo + "/";
                        }

                        if (arrayFiles[i].indexOf(path + nomeAbrev) > -1) {
                            alert('<%=ApplicationResourcesHelper.getMessage("mensagem.aviso.upload.sobreposicao.arquivo", responsavel)%>'.replace('{0}', nomeAbrev));
                            return;
                        }
                        <%} else if (!codigosOrgao.isEmpty()) { %>
                        var arrayOrgId = new Array("<%=TextHelper.forJavaScriptBlock(TextHelper.join(codigosOrgao, "\",\""))%>");
                        for (j = 0; j < arrayOrgId.length; j++) {
                            if (arrayFiles[i].indexOf("/" + arrayOrgId[j] + "/") > -1 && codIdnResponsavel == arrayOrgId[j]) {
                                alert('<%=ApplicationResourcesHelper.getMessage("mensagem.aviso.upload.sobreposicao.arquivo", responsavel)%>'.replace('{0}', nomeAbrev));
                                return;
                            }
                        }
                        <% } %>
                        <%} else {%>
                        alert('<%=ApplicationResourcesHelper.getMessage("mensagem.aviso.upload.sobreposicao.arquivo", responsavel)%>'.replace('{0}', nomeAbrev));
                        return;
                        <%} %>
                    }
                }
            }
            <% } %>
        }

        function vf_upload_arquivos() {
            var controles = '';
            var msgs = '';

            <% if (selecionaEstOrgUploadMargemRetorno || selecionaEstOrgUploadContracheque) { %>
            var papCodigo = getCheckedRadio('form2', 'PAP_CODIGO');

            if (papCodigo == null || papCodigo == '') {
                alert('<%=ApplicationResourcesHelper.getMessage("mensagem.aviso.upload.selecione.entidade", responsavel)%>');
                return;
            }

            <% if (responsavel.isCseSup()) { %>
            if (papCodigo == 'EST') {
                if (f0.EST_CODIGO.options[f0.EST_CODIGO.selectedIndex].value == '') {
                    alert('<%=ApplicationResourcesHelper.getMessage("mensagem.aviso.upload.selecione.estabelecimento", responsavel)%>');
                    f0.EST_CODIGO.focus();
                    return;
                }
            }

            if (papCodigo == 'ORG') {
                if (f0.ORG_CODIGO.options[f0.ORG_CODIGO.selectedIndex].value == '') {
                    alert('<%=ApplicationResourcesHelper.getMessage("mensagem.aviso.upload.selecione.orgao", responsavel)%>');
                    f0.ORG_CODIGO.focus();
                    return;
                }
            }
            <%} %>
            <%} %>

            controles = new Array("FILE1", "tipo");
            msgs = new Array('<%=ApplicationResourcesHelper.getMessage("mensagem.aviso.upload.selecione.arquivo", responsavel)%>',
                '<%=ApplicationResourcesHelper.getMessage("mensagem.aviso.upload.selecione.tipo.arquivo", responsavel)%>');

            var ok = ValidaCampos(controles, msgs);
            if (ok) {
                $('#modalAguarde').modal({
                    backdrop: 'static',
                    keyboard: false
                });
            }
            <%
            action += "&" + fluxo;
            %>
            return ok;
        }

        function alterarTipoArquivo() {
            var tipo = f0.tipo.options[f0.tipo.selectedIndex].value;

            if (tipo !== '') {
                tipo = tipo.charAt(0).toUpperCase() + tipo.slice(1);
                link = "../v3/uploadArquivo" + tipo + "?acao=carregar";

                if (f0.PAP_CODIGO != null) {
                    var papCodigo = getCheckedRadio('form2', 'PAP_CODIGO');
                    link += "&PAP_CODIGO=" + papCodigo;
                }

                if (f0.EST_CODIGO != null) {
                    var estCodigo = getFieldValue(f0.EST_CODIGO);
                    link += "&EST_CODIGO=" + estCodigo;
                }

                if (f0.ORG_CODIGO != null) {
                    var orgCodigo = getFieldValue(f0.ORG_CODIGO);
                    link += "&ORG_CODIGO=" + orgCodigo;
                }
                link += "&FLUXO=" + "<%=fluxo%>";
            }
            link += "&" + "<%=SynchronizerToken.generateToken4URL(request)%>";
            postData(link);
        }

        var f0 = document.forms[0];

        if (document.getElementById('captcha') != 'null' && document.getElementById('captcha') != null && document.getElementById('captcha') != 'undefined') {
            document.getElementById('captcha').blur();
        }

        window.onload = formFullLoad;
    </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>