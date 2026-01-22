<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TransferObjectHelper"%>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

  boolean bloqPadrao = (boolean) request.getAttribute("ParamBloqPadrao");

  String csa_codigo = (String) request.getAttribute("csa_codigo");
  String svc_codigo = (String) request.getAttribute("svc_codigo");
  String svc_identificador = (String) request.getAttribute("svc_identificador");
  String svc_descricao = (String) request.getAttribute("svc_descricao");
  String voltar = (String) request.getAttribute("voltar");
  List<TransferObject> svcList = (List<TransferObject>) request.getAttribute("svcList");
  List<?> lstOcorrencias = (List<?>) request.getAttribute("historicoBloqDesbloqVinculos");
%>
<c:set var="title">
  <hl:message key="rotulo.manutencao.bloqueio.vinculo.servico" />
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <form method="post" action="../v3/manterConsignataria?acao=salvarCnvVincServidor&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><hl:message key="rotulo.acoes.lst.arq.generico.opcoes"/></h2>
      </div>
      <div class="card-body">
        <div class="row">
          <div class="form-group col-sm-12 col-md-6 mt-1">
            <label for="search"><hl:message key="mensagem.datatables.search.placeholder"/></label>
            <input id="search"  onkeyup="searchVinculo()"
                   placeholder="<hl:message key="rotulo.procurar.vinculo.edicao"/>" class="search form-control"
                   type="text">
          <div class="col-sm-12 col-md-6 mt-3">
            <input class="form-check-input selectAll" name="selectAll" type="checkbox" id="selectAll">
            <label class="form-check-label" for="selectAll"><hl:message key="rotulo.acoes.selecionar.todos"/></label>
          </div>
          </div>
          <% if(!svcList.isEmpty() && svcList != null) { %>
          <div class="form-group col-sm-12 col-md-6 mt-2">
            <label for="services"><hl:message key="rotulo.servico.aplicar.configuracao"/></label>
            <select class="form-control form-select col-sm-12 m-1" name="services" multiple id="services">
              <%
                Iterator<?> itSvc = svcList.iterator();
                while (itSvc.hasNext()) {
                  CustomTransferObject ctoServico = (CustomTransferObject) itSvc.next();
                  String svcCodigo = ctoServico.getAttribute(Columns.SVC_CODIGO).toString();
                  String svcIdentificador = ctoServico.getAttribute(Columns.SVC_IDENTIFICADOR).toString();
                  String svcDescricao = ctoServico.getAttribute(Columns.SVC_DESCRICAO).toString();
              %>
              <option VALUE="<%=TextHelper.forHtmlAttribute(svcCodigo)%>-<%=TextHelper.forHtmlContent(svcIdentificador)%>" <%=svc_codigo.equals(svcCodigo) ? "SELECTED" : ""%>><%=TextHelper.forHtmlContent(svcDescricao)%>
              </option>
              <% } %>
            </select>
            <div class="slider col-sm-8 col-md-12 pl-0 pr-0 mb-2">
              <div class="tooltip-inner"><hl:message key="mensagem.utilize.crtl"/></div>
            </div>
            <div class="btn-action float-end mt-3">
              <a class="btn btn-outline-danger" onClick="limparCombo(document.forms[0].services)" href="javascript:void(0);"><hl:message key="mensagem.limpar.selecao"/></a>
            </div>
          </div>
        <% } %>
        </div>
      </div>
    </div>
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><% if(!bloqPadrao) { %> <hl:message key="rotulo.selecione.vinculos.servico"/> <% } else { %> <hl:message key="rotulo.selecione.vinculos.nao.servico"/> <% } %><%=TextHelper.forHtmlContent(svc_descricao)%></h2>
      </div>
      <div class="card-body">
        <div class="row">
          <div class="col-sm-12 col-md-12">
            <div class="form-check">
              <div class="row" role="group">
              <%
                List<?> vrs = (List<?>) request.getAttribute("vrs");  
                List<?> cnvVincRse = (List<?>) request.getAttribute("cnvVincRse");
                Iterator<?> ite = vrs.iterator();
                while (ite.hasNext()) {
                  CustomTransferObject cto = (CustomTransferObject)ite.next();
                  String vrsCodigo = cto.getAttribute(Columns.VRS_CODIGO).toString();
                  String vrsIdentificador = cto.getAttribute(Columns.VRS_IDENTIFICADOR).toString();
                  String vrsDescricao = cto.getAttribute(Columns.VRS_DESCRICAO).toString();
              %>
                      <div  class="vinculos col-sm-12 col-md-6">
                        <input name="vinculo" type="checkbox" id="<%=TextHelper.forHtmlAttribute(vrsCodigo)%>" value="<%=TextHelper.forHtmlAttribute(vrsCodigo)%>" <%=(String)(cnvVincRse.contains(vrsCodigo) ? "CHECKED" : "")%>>
                        <label for="<%=TextHelper.forHtmlAttribute(vrsCodigo)%>">
                          <span class="text-nowrap"><%=TextHelper.forHtmlContent(vrsIdentificador)%>-<%=TextHelper.forHtmlContent(vrsDescricao)%></span>
                        </label>
                      </div>
              <%
                }
              %>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="col-sm-12 pl-0 pr-0">
      <div class="card">
        <div class="card-header hasIcon">
           <span class="card-header-icon"><svg width="26">
             <use xlink:href="#i-consignacao"></use></svg></span>
           <h2 class="card-header-title"><hl:message key="rotulo.ocorrencia.vinculos.csa.titulo"/></h2>
        </div>
        <div class="card-body table-responsive">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th scope="col" width="10%"><hl:message key="rotulo.ocorrencia.vinculos.csa.data"/></th>
                <th scope="col" width="10%"><hl:message key="rotulo.ocorrencia.vinculos.csa.responsavel"/></th>
                <th scope="col" width="10%"><hl:message key="rotulo.ocorrencia.vinculos.csa.tipo"/></th>
                <th scope="col" width="10%"><hl:message key="rotulo.ocorrencia.vinculos.csa.descricao"/></th>
                <th scope="col" width="10%"><hl:message key="rotulo.ocorrencia.vinculos.csa.ip"/></th>
              </tr>
            </thead>
            <tbody>
            <%
               Iterator<?> itHistorico = lstOcorrencias.iterator();
               while (itHistorico.hasNext()) { 
                   CustomTransferObject cto = (CustomTransferObject) itHistorico.next();
                   cto = TransferObjectHelper.mascararUsuarioHistorico((CustomTransferObject) cto, null, responsavel);
                 
                 String occData = cto.getAttribute(Columns.OCC_DATA) != null ? DateHelper.toDateTimeString((Date) cto.getAttribute(Columns.OCC_DATA)) : "";
                 String occObs = cto.getAttribute(Columns.OCC_OBS) != null ? cto.getAttribute(Columns.OCC_OBS).toString() : "";
                 String occIpAcesso = cto.getAttribute(Columns.OCC_IP_ACESSO) != null ?  cto.getAttribute(Columns.OCC_IP_ACESSO).toString() : "";
                 String loginOccResponsavel = cto.getAttribute(Columns.USU_LOGIN) != null ? cto.getAttribute(Columns.USU_LOGIN).toString() : "";
                 String occResponsavel = (loginOccResponsavel.equalsIgnoreCase((String) cto.getAttribute(Columns.USU_CODIGO)) &&
                         cto.getAttribute(Columns.USU_TIPO_BLOQ) != null) ? (cto.getAttribute(Columns.USU_TIPO_BLOQ).toString() + "(*)") : loginOccResponsavel;
                 String occTipo = cto.getAttribute(Columns.TOC_DESCRICAO) != null ? cto.getAttribute(Columns.TOC_DESCRICAO).toString() : "";
            %>
               <tr>
                 <td><%=TextHelper.forHtmlContent(occData)%></td>
                 <td><%=TextHelper.forHtmlContent(occResponsavel)%></td>
                 <td><%=TextHelper.forHtmlContent(occTipo)%></td>
                 <td><%=JspHelper.formataMsgOca(occObs)%></td>
                 <td><%=TextHelper.forHtmlContent(occIpAcesso)%></td>
               </tr>
            <% } %>
            </tbody>
            <tfoot>
               <tr><td colspan="5"><%=ApplicationResourcesHelper.getMessage("mensagem.listagem.historico.disponivel", responsavel) + " - " %><span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTitulo"))%></span></td></tr>
            </tfoot>
          </table>
        </div>
        <div class="card-footer">
          <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
        </div>
      </div>
    </div>
    <input name="svc" type="hidden" value="<%=TextHelper.forHtmlAttribute(svc_codigo)%>">
    <input type="hidden" name="svc" value="<%=TextHelper.forHtmlAttribute(svc_codigo)%>">
    <input type="hidden" name="csa" value="<%=TextHelper.forHtmlAttribute(csa_codigo)%>">
    <input type="hidden" name="SVC_IDENTIFICADOR" value="<%=TextHelper.forHtmlAttribute(svc_identificador)%>">
    <input type="hidden" name="SVC_DESCRICAO" value="<%=TextHelper.forHtmlAttribute(svc_descricao)%>">
  </form>
  <div class="btn-action mt-3">
    <a class="btn btn-outline-danger" id="btnVoltar" href="#no-back" onClick="postData('<%=voltar%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
    <a class="btn btn-primary" href="#no-back" onClick="f0.submit(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
  </div>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript">
    function formLoad() {
    }
    window.onload = formLoad;
	var f0 = document.forms[0];

    function searchVinculo() {
      let input = document.getElementById('search').value
      input = input.toLowerCase();
      let x = document.getElementsByClassName('vinculos');
      for (i = 0; i < x.length; i++) {
        if (!x[i].innerHTML.toLowerCase().includes(input)) {
          x[i].style.display = "none";
        } else {
          x[i].style.display = "";
        }
      }
    }

    $('#selectAll').click(function () {
      if (this.checked) {
        $(':checkbox').each(function () {
          this.checked = true;
        });
      } else {
        $(':checkbox').each(function () {
          this.checked = false;
        });
      }
    });

    $('#search').on('keydown', function (e) {
        if (e.keyCode === 13) {
            return false;
        }
    });

  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>