<%--
* <p>Title: Listar e pesquisar vinculos de csa</p>
* <p>Description: Listar e pesquisar vinculos de csa</p>
* <p>Copyright: Copyright (c) 2024</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: dodo.neves $
* $Date: 2024-02-15 12:59:25 -0300 (qui, 15 fev 2024) $
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.persistence.entity.VinculoConsignataria" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<script type="text/JavaScript" src="../js/scripts_2810.js?<hl:message key="release.tag"/>"></script>
<script type="text/JavaScript" src="../js/validaform.js?<hl:message key="release.tag"/>"></script>
<script type="text/JavaScript" src="../js/validacoes.js?<hl:message key="release.tag"/>"></script>

<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession"/>
<%
    AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
    List<VinculoConsignataria> listVinculoCsa = (List<VinculoConsignataria>) request.getAttribute("listVinculoCsa");
    String titulo = (String) request.getAttribute("titulo");
    String csaCodigo = (String) request.getAttribute("csaCodigo");
%>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="title">
    <%=titulo%>
</c:set>
<c:set var="bodyContent">
    <div class="btn-action">
        <button class="btn btn-primary"
                href="#no-back"
                onclick="postData('../v3/manterVinculoCsaRse?acao=novo&csaCodigo=<%=csaCodigo%>&<%=SynchronizerToken.generateToken4URL(request)%>')">
            <hl:message key="rotulo.acao.novo.vinculo.csa.rse"/>
        </button>
    </div>
    <div class="card">
        <div class="card-header">
            <h2 class="card-header-title"><hl:message key="rotulo.filtro.plural"/></h2>
        </div>
        <div class="card-body">
            <div class="row">
                <div class="form-group col-sm-12 col-md-6 mt-1">
                    <label for="searchDescricao"><hl:message key="rotulo.vinculo.csa.rse.descricao"/></label>
                    <input id="searchDescricao" onkeyup="searchVinculoCsa()"
                           placeholder="<hl:message key="rotulo.vinculo.csa.rse.descricao"/>"
                           class="search form-control"
                           type="text">
                </div>
                <div class="form-group col-sm-12 col-md-6 mt-1">
                    <label for="searchIdentificador"><hl:message key="rotulo.vinculo.csa.rse.identificador"/></label>
                    <input id="searchIdentificador" onkeyup="searchIdentificador()"
                           placeholder="<hl:message key="rotulo.vinculo.csa.rse.identificador"/>"
                           class="search form-control"
                           type="text">
                </div>
                <div class="form-group col-sm-12">
                    <span id="filterSituacao"><hl:message key="rotulo.vinculo.csa.rse.situacao"/></span>
                    <br/>
                    <div class="form-check form-check-inline mt-2" role="radio-group" area-labeldbay="blocOrActive">
                        <input onclick="filterSituacao(0)" class="form-check-input" name="blocOrActive" type="radio"
                               id="active">
                        <label class="form-check-label labelSemNegrito" for="active"><hl:message
                                key="rotulo.status.ativo"/></label>
                    </div>
                    <div class="form-check-inline form-check">
                        <input onclick="filterSituacao(1)" class="form-check-input" name="blocOrActive" type="radio"
                               id="block">
                        <label class="form-check-label labelSemNegrito" for="block"><hl:message
                                key="rotulo.status.inativo"/></label>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="form-group col-sm">
                </div>
            </div>
        </div>
    </div>
    <div class="btn-action">
        <button class="btn btn-primary" onclick="clearFilters()">
            <hl:message key="rotulo.botao.limpar.filtros"/>
        </button>
    </div>
    <div class="card">
        <div class="card-header">
            <h2 class="card-header-title"><hl:message key="rotulo.vinculos.consignataria"/></h2>
        </div>
        <div class="card-body table-responsive p-0">
            <table class="table table-striped table-hover">
                <thead>
                <tr>
                    <th scope="col"><hl:message key="rotulo.vinculo.csa.rse.identificador"/></th>
                    <th scope="col"><hl:message key="rotulo.vinculo.csa.rse.descricao"/></th>
                    <th scope="col"><hl:message key="rotulo.vinculo.csa.rse.situacao"/></th>
                    <th scope="col"><hl:message key="rotulo.vinculo.csa.rse.data"/></th>
                    <th scope="col"><hl:message key="rotulo.acoes"/></th>
                </tr>
                </thead>
                <tbody>
                <%
                    Iterator<?> it = listVinculoCsa.iterator();
                    while (it.hasNext()) {
                        VinculoConsignataria vinculo = (VinculoConsignataria) it.next();
                        String vcsCodigo = vinculo.getVcsCodigo();
                        String vcsDescricao = vinculo.getVcsDescricao();
                        String vcsIdentificador = vinculo.getVcsIdentificador();
                        Short vcsAtivo = vinculo.getVcsAtivo();
                        String vcsData = DateHelper.toDateTimeString(vinculo.getVcsDataCriacao());
                        String vcsSituacao = null;
                        if (vcsAtivo == 1) {
                            vcsSituacao = ApplicationResourcesHelper.getMessage("rotulo.status.ativo", responsavel);
                        } else {
                            vcsSituacao = ApplicationResourcesHelper.getMessage("rotulo.status.inativo", responsavel);
                        }
                %>
                <tr>
                    <td><%= vcsIdentificador %>
                    </td>
                    <td><%= vcsDescricao %>
                    </td>
                    <td><%= vcsSituacao %>
                    </td>
                    <td><%= vcsData %>
                    </td>
                    <td>
                        <div class="actions">
                            <div class="dropdown">
                                <a class="dropdown-toggle ico-action" href="#" role="button" id="MASTER"
                                   data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                    <div class="form-inline">
                          <span class="mr-1" data-bs-toggle="tooltip" title=""
                                data-original-title="<hl:message key="rotulo.botao.opcoes" />"
                                aria-label="<hl:message key="rotulo.botao.opcoes" />"> <svg>
                              <use xlink:href="#i-engrenagem"></use></svg>
                          </span> <hl:message key="rotulo.botao.opcoes"/>
                                    </div>
                                </a>
                                <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                                    <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterVinculoCsaRse?acao=editar&vcsCodigo=<%=vcsCodigo%>&csaCodigo=<%=csaCodigo%>&<%=SynchronizerToken.generateToken4URL(request)%>')"><hl:message key="rotulo.acoes.editar"/></a>
                                    <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterVinculoCsaRse?acao=bloquearDesbloquear&vcsCodigo=<%=vcsCodigo%>&tipo=<%=vcsAtivo%>&csaCodigo=<%=csaCodigo%>&<%=SynchronizerToken.generateToken4URL(request)%>')""><%if(vcsAtivo == 1) { %> <hl:message key="rotulo.acoes.bloquear"/> <% } else { %> <hl:message key="rotulo.acoes.desbloquear"/><% } %></a>
                                    <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterVinculoCsaRse?acao=excluir&vcsCodigo=<%=vcsCodigo%>&csaCodigo=<%=csaCodigo%>&<%=SynchronizerToken.generateToken4URL(request)%>')"><hl:message key="rotulo.acoes.excluir"/></a>
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>
    <div class="btn-action">
        <a class="btn btn-outline-danger" href="#no-back"
           onClick="postData('../v3/manterConsignataria?acao=editarConsignataria&csa=<%=csaCodigo%>&<%=SynchronizerToken.generateToken4URL(request)%>'); return false;"><hl:message
                key="rotulo.botao.voltar"/></a>
    </div>
</c:set>
<c:set var="javascript">
    <script type="text/JavaScript">
        function searchIdentificador() {
            let input = document.getElementById('searchIdentificador').value
            input = input.toLowerCase();
            let x = document.getElementsByTagName('tbody')[0].rows;
            for (i = 0; i < x.length; i++) {
                td = x[i].getElementsByTagName("td")[0];
                if (td) {
                    if (td.innerHTML.toLowerCase().includes(input)) {
                        x[i].style.display = "";
                    } else {
                        x[i].style.display = "none";
                    }
                }
            }
        }

        function searchVinculoCsa() {
            let input = document.getElementById('searchDescricao').value
            input = input.toLowerCase();
            let x = document.getElementsByTagName('tbody')[0].rows;
            for (i = 0; i < x.length; i++) {
                td = x[i].getElementsByTagName("td")[1];
                if (td) {
                    if (td.innerHTML.toLowerCase().includes(input)) {
                        x[i].style.display = "";
                    } else {
                        x[i].style.display = "none";
                    }
                }
            }
        }

        function filterSituacao(type) {
            let activeOrBlock = "Ativo"
            if (type === 1) {
                activeOrBlock = "Bloqueado"
            }
            let x = document.getElementsByTagName('tbody')[0].rows;
            for (i = 0; i < x.length; i++) {
                td = x[i].getElementsByTagName("td")[2];
                if (td) {
                    if (td.innerHTML.includes(activeOrBlock)) {
                        x[i].style.display = "";
                    } else {
                        x[i].style.display = "none";
                    }
                }
            }
        }

        function clearFilters() {
            let x = document.getElementsByTagName('tbody')[0].rows;
            for (i = 0; i < x.length; i++) {
                    x[i].style.display = "";
            }
            document.getElementById('searchDescricao').value = "";
            document.getElementById('searchIdentificador').value = "";
            document.getElementById('block').checked = false;
            document.getElementById('active').checked = false;
        }
    </script>
</c:set>
<%-- Leiaute Fixo --%>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>