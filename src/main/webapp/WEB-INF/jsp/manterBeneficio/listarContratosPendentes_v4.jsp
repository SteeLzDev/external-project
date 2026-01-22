<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.persistence.entity.Consignataria"%>
<%@ page import="com.zetra.econsig.dto.TransferObject" %>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession"/>
<% 
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String csaCodigo = (String) request.getAttribute("csaCodigo");
boolean edtBeneficioAvancado = (boolean) request.getAttribute("edtBeneficioAvancado");
List<Consignataria>  lstConsignatarias = (List<Consignataria> ) request.getAttribute("lstConsignatarias");
List<TransferObject>  inclusaoPendentes = (List<TransferObject> ) request.getAttribute("inclusaoPendentes");
List<TransferObject>  exclusaoPendentes = (List<TransferObject> ) request.getAttribute("exclusaoPendentes");

%>
<c:set var="imageHeader">
  <use xlink:href="#i-beneficios"></use>
</c:set>
<c:set var="title">
  <hl:message key="rotulo.beneficio.contratos.pendentes"/>
</c:set>
<head>
  <link rel="stylesheet" href="../css/impromptu.css?<hl:message key="release.tag"/>" type="text/css">
</head>
<c:set var="bodyContent">
  <% if (responsavel.isSup()) { %>
    <form name="form1" method="post" ACTION="../v3/listarContratosBeneficioPendentes">
      <input type="hidden" name="acao" value="iniciar"/>
      <div class="row">
        <div class="form-group col-sm-12  col-md-6">
          <label for="consignataria"><hl:message key="rotulo.consignataria.singular"/></label>
          <select name="CSA_CODIGO" id="consignataria" class="form-control" onFocus="SetarEventoMascara(this,'#*200',true);"  onBlur="fout(this);ValidaMascara(this);" onChange="document.form1.submit();"  >
            <option value="" ><hl:message key="rotulo.campo.selecione"/></option>
            <%
            Iterator<Consignataria> itFiltro = lstConsignatarias.iterator();
            while (itFiltro.hasNext()) {
                Consignataria csaTO = itFiltro.next();
                String csa_codigo = (String) csaTO.getCsaCodigo();
                String csa_nome = (String) csaTO.getCsaNome();
            %>
              <option value="<%=TextHelper.forHtmlAttribute(csa_codigo)%>" <%=(String)((!TextHelper.isNull(csaCodigo) && csa_codigo.equals(csaCodigo)) ? "SELECTED" : "")%>><%=TextHelper.forHtmlContent(csa_nome)%></option>
            <%
            }
            %>
          </select>
        </div>
      </div>
      <% out.print(SynchronizerToken.generateHtmlToken(request)); %>
    </form>  
  <% } %>  
  <%--  INICIO DA LÓGICA DE EXCLUSÃO --%>
  <% if (!TextHelper.isNull(csaCodigo) && exclusaoPendentes != null) {   %>
    <div class="card">
      <div class="card-header hasIcon pl-3">
        <h2 class="card-header-title"><hl:message key="rotulo.beneficio.contratos.pendentes.exclusao"/></h2>
      </div>
      <div class="card-body table-responsive p-0">
        <table class="table table-striped table-hover">
          <thead>
            <tr>
              <th scope="col"><hl:message key="rotulo.matricula.singular"/></th>
              <th scope="col"><hl:message key="rotulo.relatorio.inclusao.beneficiarios.periodo.nome.beneficiario"/></th>
              <th scope="col"><hl:message key="rotulo.relatorio.documento.beneficiario.tipo.validade.cpf.beneficiario"/></th>
              <th scope="col"><hl:message key="rotulo.relacao.beneficios.numero.cliente"/></th>
              <th scope="col"><hl:message key="rotulo.beneficio.abreviado"/></th>
              <th scope="col"><hl:message key="rotulo.contrato.beneficio.data.fim.vigencia"/></th>
              <th scope="col"><hl:message key="rotulo.beneficio.contratos.pendentes.dias.mesmo.status"/></th>
              <% if (responsavel.isSup() && edtBeneficioAvancado) { %>
                <th><hl:message key="rotulo.acoes"/></th>
              <% } %>
            </tr>
          </thead>
        <tbody>
          <%
            String cbeCodigo, rseMatricula, nomeBeneficiario, cpfBeneficiario,numeroCliente,beneficioDescricao,fimVigencia,quantidadeDias;
          %>
          <%
            TransferObject contratoBeneficio = null;
            
            Iterator<TransferObject> contratosExclusaoPendentes = exclusaoPendentes.iterator();
            while (contratosExclusaoPendentes.hasNext()) {
              contratoBeneficio = contratosExclusaoPendentes.next();
              
              cbeCodigo = (String) contratoBeneficio.getAttribute(Columns.CBE_CODIGO).toString();
              rseMatricula = (String) contratoBeneficio.getAttribute(Columns.RSE_MATRICULA).toString();
              nomeBeneficiario = (String) contratoBeneficio.getAttribute(Columns.BFC_NOME).toString();
              cpfBeneficiario = (String) contratoBeneficio.getAttribute(Columns.BFC_CPF).toString();
              numeroCliente = (String) contratoBeneficio.getAttribute(Columns.CBE_NUMERO).toString();      
              beneficioDescricao = (String) contratoBeneficio.getAttribute(Columns.BEN_DESCRICAO).toString();
              fimVigencia = DateHelper.reformat(contratoBeneficio.getAttribute("FIM_VIGENCIA").toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern());
              quantidadeDias = (String) contratoBeneficio.getAttribute("DIAS_STATUS").toString();
           %>   
              <tr>
                <td><%=TextHelper.forHtmlContent(rseMatricula)%></td>
                <td><%=TextHelper.forHtmlContent(nomeBeneficiario)%></td>
                <td><%=TextHelper.forHtmlContent(cpfBeneficiario)%></td>
                <td><%=TextHelper.forHtmlContent(numeroCliente)%></td>
                <td><%=TextHelper.forHtmlContent(beneficioDescricao)%></td>
                <td><%=TextHelper.forHtmlContent(fimVigencia)%></td>
                <td><%=TextHelper.forHtmlContent(quantidadeDias)%></td>
                <% if (responsavel.isSup() && edtBeneficioAvancado) { %>
                  <td>
                    <div class="actions">
                        <a class="col-md-6 col-lg-4 col-xl-3" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL("../v3/alterarContratoBeneficio?acao=visualizar&cbeCodigo=" + cbeCodigo, request))%>')">
                         <hl:message key="rotulo.acoes.editar"/>
                        </a>       
                    </div>
                  </td>
                <% } %>
              </tr>
          <% 
               } 
          %>  
        </tbody>
        <tfoot>
          <tr>
            <td colspan="13"><%=ApplicationResourcesHelper.getMessage("rotulo.beneficio.contratos.pendentes.exclusao.lista", responsavel) + " - " %>
              <span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTituloExcluPen"))%></span>
            </td>
          </tr>
       </tfoot>
      </table>
      <% request.setAttribute("_indice", "IncluPen"); %>
      <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
      </div>
    </div>
  <% } %>
<%--  INICIO DA LÓGICA DE INCLUSÃO --%>
  <% if (!TextHelper.isNull(csaCodigo) && exclusaoPendentes != null) {   %>
    <div class="card">
      <div class="card-header hasIcon pl-3">
        <h2 class="card-header-title"><hl:message key="rotulo.beneficio.contratos.pendentes.inclusao"/></h2>
      </div>
      <div class="card-body table-responsive p-0">
        <table class="table table-striped table-hover">
          <thead>
            <tr>
              <th scope="col"><hl:message key="rotulo.matricula.singular"/></th>
              <th scope="col"><hl:message key="rotulo.relatorio.inclusao.beneficiarios.periodo.nome.beneficiario"/></th>
              <th scope="col"><hl:message key="rotulo.relatorio.documento.beneficiario.tipo.validade.cpf.beneficiario"/></th>
              <th scope="col"><hl:message key="rotulo.relacao.beneficios.numero.cliente"/></th>
              <th scope="col"><hl:message key="rotulo.beneficio.abreviado"/></th>
              <th scope="col"><hl:message key="rotulo.contrato.beneficio.data.inicio.vigencia"/></th>
              <th scope="col"><hl:message key="rotulo.beneficio.contratos.pendentes.dias.mesmo.status"/></th>
              <% if (responsavel.isSup() && edtBeneficioAvancado) { %>
                <th><hl:message key="rotulo.acoes"/></th>
              <% } %>
            </tr>
          </thead>
        <tbody>
          <%
          String cbeCodigo, rseMatricula, nomeBeneficiario, cpfBeneficiario,numeroCliente,beneficioDescricao,inicioVigencia,quantidadeDias;
          %>
          <%
          TransferObject contratoBeneficio = null;
          
          Iterator<TransferObject> contratosInclusaoPendentes = inclusaoPendentes.iterator();
          while (contratosInclusaoPendentes.hasNext()) {
            contratoBeneficio = contratosInclusaoPendentes.next();
            
            cbeCodigo = (String) contratoBeneficio.getAttribute(Columns.CBE_CODIGO).toString();
            rseMatricula = (String) contratoBeneficio.getAttribute(Columns.RSE_MATRICULA).toString();
            nomeBeneficiario = (String) contratoBeneficio.getAttribute(Columns.BFC_NOME).toString();
            cpfBeneficiario = (String) contratoBeneficio.getAttribute(Columns.BFC_CPF).toString();
            numeroCliente = (String) contratoBeneficio.getAttribute(Columns.CBE_NUMERO).toString();      
            beneficioDescricao = (String) contratoBeneficio.getAttribute(Columns.BEN_DESCRICAO).toString();
            inicioVigencia = DateHelper.reformat(contratoBeneficio.getAttribute(Columns.CBE_DATA_INICIO_VIGENCIA).toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern());
            quantidadeDias = (String) contratoBeneficio.getAttribute("DIAS_STATUS").toString();
          %>   
            <tr>
              <td><%=TextHelper.forHtmlContent(rseMatricula)%></td>
              <td><%=TextHelper.forHtmlContent(nomeBeneficiario)%></td>
              <td><%=TextHelper.forHtmlContent(cpfBeneficiario)%></td>
              <td><%=TextHelper.forHtmlContent(numeroCliente)%></td>
              <td><%=TextHelper.forHtmlContent(beneficioDescricao)%></td>
              <td><%=TextHelper.forHtmlContent(inicioVigencia)%></td>
              <td><%=TextHelper.forHtmlContent(quantidadeDias)%></td>
              <% if (responsavel.isSup() && edtBeneficioAvancado) { %>
                <td>
                  <div class="actions">
                    <a class="col-md-6 col-lg-4 col-xl-3" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL("../v3/alterarContratoBeneficio?acao=visualizar&cbeCodigo=" + cbeCodigo, request))%>')">
                     <hl:message key="rotulo.acoes.editar"/>
                    </a>       
                  </div>
               </td>
             <% } %>
            </tr>
            <% 
              } 
            %> 
          </tbody>
        <tfoot>
          <tr>
            <td colspan="13"><%=ApplicationResourcesHelper.getMessage("rotulo.beneficio.contratos.pendentes.inclusao.lista", responsavel) + " - " %>
              <span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTituloIncluPen"))%></span>
            </td>
          </tr>
      </tfoot>
      </table>
      <% request.setAttribute("_indice", "IncluPen"); %>
      <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
    </div>
  </div>
 <% } %>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#" onClick="postData('../v3/carregarPrincipal')">
      <hl:message key="rotulo.botao.cancelar"/>
    </a>
  </div>
</c:set>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>