<%--
* <p>Title: Incluir e editar vinculos csa</p>
* <p>Description: Criação e edição de novos/antigos vinculos</p>
* <p>Copyright: Copyright (c) 2024</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: dodo.neves $
* $Date: 2024-02-15 11:59:25 -0300 (qui, 15 fev 2024) $
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.persistence.entity.VinculoRegistroServidor" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="com.zetra.econsig.persistence.entity.VinculoConsignataria" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession"/>
<%
    AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
    String descricao = "";
    String identificador = "";
    String vrscodigo = "";
    String vrsdescricao = "";
    String titulo = (String) request.getAttribute("titulo");
    String csaCodigo = (String) request.getAttribute("csaCodigo");
    VinculoConsignataria vinculoConsignataria = request.getAttribute("vinculoConsignataria") != null ? (VinculoConsignataria) request.getAttribute("vinculoConsignataria") : null;
    List<VinculoRegistroServidor> listVinculoRse = request.getAttribute("listVinculoRse") != null ? (List<VinculoRegistroServidor>) request.getAttribute("listVinculoRse") : null;
    VinculoRegistroServidor vinculoRegistroServidor = request.getAttribute("vinculoRse") != null ? (VinculoRegistroServidor) request.getAttribute("vinculoRse") : null;
    if (vinculoConsignataria != null) {
        descricao = vinculoConsignataria.getVcsDescricao();
        identificador = vinculoConsignataria.getVcsIdentificador();
        if (vinculoRegistroServidor != null) {
            vrscodigo = vinculoRegistroServidor.getVrsCodigo();
            vrsdescricao = vinculoRegistroServidor.getVrsDescricao();
        }
    }
%>
<c:set var="title">
    <%=TextHelper.forHtml(titulo)%>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
    <div class="card">
        <div class="card-header">
            <h2 class="card-header-title"><hl:message key="rotulo.acao.novo.vinculo.csa.rse"/></h2>
        </div>
        <div class="card-body">
            <form NAME="form1" METHOD="post"
                  ACTION="../v3/manterVinculoCsaRse?acao=salvar<%if(vinculoConsignataria != null ) { %>&vcsCodigo=<%=vinculoConsignataria.getVcsCodigo()%> <% } %>&csaCodigo=<%=csaCodigo%>&<%=SynchronizerToken.generateToken4URL(request)%>">
                <div class="row">
                    <div class="form-group col-sm-6">
                        <label for="vcsDescricao"><hl:message key="rotulo.vinculo.csa.rse.descricao"/></label>
                        <input value="<%=descricao%>" class="form-control" name="vcsDescricao" id="vcsDescricao"
                               type="text">
                    </div>
                    <div class="form-group col-sm-6">
                        <label for="vcsIdentificador"><hl:message key="rotulo.vinculo.csa.rse.identificador"/></label>
                        <input value="<%=identificador%>" class="form-control" name="vcsIdentificador"
                               id="vcsIdentificador" type="text">
                    </div>
                </div>
                <div class="row">
                    <div class="form-group col-sm-6">
                        <label for="vrsCodigo"><hl:message key="rotulo.select.vinculo.rse"/></label>
                        <select class="form-select form-control" name="vrsCodigo" id="vrsCodigo">
                            <%
                                if (vinculoRegistroServidor != null) { %>
                            <option value=""><hl:message key="rotulo.botao.remover"/></option>
                            <option selected value="<%=vrscodigo%>"><%=vrsdescricao%>
                            </option>
                            <% } else { %>
                            <option value=""><hl:message key="rotulo.campo.selecione"/></option>
                            <% } %>
                            <%
                                if (listVinculoRse != null) {
                                    Iterator<?> it = listVinculoRse.iterator();
                                    while (it.hasNext()) {
                                        VinculoRegistroServidor vinculo = (VinculoRegistroServidor) it.next();
                                        String vrsCodigo = vinculo.getVrsCodigo();
                                        String vrsDescricao = vinculo.getVrsDescricao();
                            %>
                            <option value="<%=vrsCodigo%>"><%=vrsDescricao%>
                            </option>
                            <% }
                            } %>
                        </select>
                    </div>
                </div>
            </form>
        </div>
    </div>
    <div class="btn-action">
        <a class="btn btn-outline-danger" href="#no-back"
           onclick="postData('../v3/manterVinculoCsaRse?acao=iniciar&csaCodigo=<%=csaCodigo%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>'); return false;"><hl:message
                key="rotulo.botao.voltar"/></a>
        <a class="btn btn-primary" data-bs-dismiss="modal" href="#no-back" onclick="salvarVinculo()"
           aria-label='<hl:message key="rotulo.botao.salvar"/>' title="<hl:message key="rotulo.botao.salvar"/>">
            <hl:message key="rotulo.botao.salvar"/>
        </a>
    </div>
</c:set>
<c:set var="javascript">
    <script type="text/JavaScript">
        const f0 = document.forms[0];

        function salvarVinculo() {
            if (f0.vcsDescricao.value === "") {
                window.alert("<hl:message key="mensagem.descricao.obrigatoria"/>")
            } else if (f0.vcsIdentificador.value === "") {
                window.alert("<hl:message key="mensagem.identificador.obrigatoria"/>")
            } else {
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