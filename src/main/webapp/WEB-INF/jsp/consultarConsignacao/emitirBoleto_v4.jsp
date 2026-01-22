<%--
* <p>Title: emitirBoleto_v3</p>
* <p>Description: Página de emissão de boleto</p>
* <p>Copyright: Copyright (c) 2007</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession"/>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String msgBoleto = (request.getAttribute("msgBoleto") != null ? request.getAttribute("msgBoleto").toString() : "");
String codigoAutorizacaoSolic = (String) request.getAttribute("codigoAutorizacaoSolic");
boolean exigeCodAutSolicitacao = (request.getAttribute("exigeCodAutSolicitacao") != null);
boolean botaoVoltarPaginaInicial = (request.getAttribute("botaoVoltarPaginaInicial") != null || request.getParameter("botaoVoltarPaginaInicial") != null);

ServidorTransferObject servidor = (ServidorTransferObject) request.getAttribute("servidor");
OrgaoTransferObject orgao = (OrgaoTransferObject) request.getAttribute("orgao");
TransferObject dadosConsignacao = (TransferObject) request.getAttribute("dadosConsignacao");

String rseMatricula = dadosConsignacao.getAttribute("rseMatricula").toString();
String rseTipo = dadosConsignacao.getAttribute("rseTipo").toString();
String rseDataAdmissao = dadosConsignacao.getAttribute("rseDataAdmissao").toString();
String serEstCivil = dadosConsignacao.getAttribute("serEstCivil").toString();
String csaNome = dadosConsignacao.getAttribute("csaNome").toString();
String adeResponsavel = dadosConsignacao.getAttribute("adeResponsavel").toString();
String codVerba = dadosConsignacao.getAttribute("codVerba").toString();
String svcDescricao = dadosConsignacao.getAttribute("svcDescricao").toString();
String adeNumero = dadosConsignacao.getAttribute("adeNumero").toString();
String ranking = dadosConsignacao.getAttribute("ranking").toString();
String adeVlrLiberado = dadosConsignacao.getAttribute("adeVlrLiberado").toString();
String adeVlr = dadosConsignacao.getAttribute("adeVlr").toString();
String adePrazo = dadosConsignacao.getAttribute("adePrazo").toString();
String dataIni = dadosConsignacao.getAttribute("dataIni").toString();
String dataFim = dadosConsignacao.getAttribute("dataFim").toString();
String dataValidade = dadosConsignacao.getAttribute("dataValidade").toString();
String dataSimulacao = dadosConsignacao.getAttribute("dataSimulacao").toString();
%>
<c:set var="title">
  <%=ApplicationResourcesHelper.getMessage("mensagem.acao.boleto.consignacao", responsavel) %>
</c:set>
<c:set var="bodyContent">

      <div class="row">
        <div class="col-sm-12 col-md-12 mb-2">
          <div class="float-end">
            <button id="imprimir" aria-expanded="false" class="btn btn-primary d-print-none" type="submit" onclick="imprime();"><hl:message key="rotulo.botao.imprimir"/></button>
          </div>
        </div>
      </div>
      <div class="row firefox-print-fix">
        <div class="col-sm">
          <div class="card">
            <div class="card-header">
              <h2 class="card-header-title">
                <hl:message key="rotulo.autorizacao.desconto.singular"/>:&nbsp;<%=TextHelper.forHtmlContent(adeNumero)%>
              </h2>
            </div>
            <div class="card-body">
              <div class="row firefox-print-fix">
              <div class="col-sm">
                <div class="card">
                  <div class="card-header">
                    <h2 class="card-header-title"><hl:message key="rotulo.boleto.dados.pessoais"/></h2>
                  </div>
                  <div class="card-body">
                    <dl class="row data-list firefox-print-fix">
                      <dt class="col-6"><hl:message key="rotulo.servidor.nome"/>:</dt>
                      <dd class="col-6"><%=TextHelper.forHtmlContent(servidor.getSerNome() == null ? "" : servidor.getSerNome())%></dd>
                      <dt class="col-6"><hl:message key="rotulo.servidor.cpf"/>:</dt>
                      <dd class="col-6"><%=TextHelper.forHtmlContent(servidor.getSerCpf() == null ? "" : servidor.getSerCpf())%></dd>
                      <dt class="col-6"><hl:message key="rotulo.servidor.identidade"/>:</dt>
                      <dd class="col-6"><%=TextHelper.forHtmlContent(servidor.getSerNroIdt() == null ? "" : servidor.getSerNroIdt())%></dd>
                      <dt class="col-6"><hl:message key="rotulo.servidor.dataNasc"/>:</dt>
                      <dd class="col-6"><%=DateHelper.toDateString(servidor.getSerDataNasc())%></dd>
                      <dt class="col-6"><hl:message key="rotulo.servidor.estadoCivil"/>:</dt>
                      <dd class="col-6"><%=TextHelper.forHtmlContent(serEstCivil == null ? "" : serEstCivil)%></dd>
                      <dt class="col-6"><hl:message key="rotulo.endereco.singular"/>:</dt>
                      <dd class="col-6"><%=TextHelper.forHtmlContent(servidor.getSerEnd() == null ? "" : servidor.getSerEnd())%>,&nbsp;<%=TextHelper.forHtmlContent(servidor.getSerNro() == null ? "" : servidor.getSerNro())%></dd>
                      <dt class="col-6"><hl:message key="rotulo.endereco.complemento"/>:</dt>
                      <dd class="col-6"><%=TextHelper.forHtmlContent(servidor.getSerCompl() == null ? "" : servidor.getSerCompl())%></dd>
                      <dt class="col-6"><hl:message key="rotulo.endereco.bairro"/>:</dt>
                      <dd class="col-6"><%=TextHelper.forHtmlContent(servidor.getSerBairro() == null ? "" : servidor.getSerBairro())%></dd>
                      <dt class="col-6"><hl:message key="rotulo.endereco.cidade"/>:</dt>
                      <dd class="col-6"><%=TextHelper.forHtmlContent(servidor.getSerCidade() == null ? "" : servidor.getSerCidade())%></dd>
                      <dt class="col-6"><hl:message key="rotulo.endereco.estado"/>:</dt>
                      <dd class="col-6"><%=TextHelper.forHtmlContent(servidor.getSerUf() == null ? "" : servidor.getSerUf())%></dd>
                      <dt class="col-6"><hl:message key="rotulo.endereco.cep"/>:</dt>
                      <dd class="col-6"><%=TextHelper.forHtmlContent(servidor.getSerCep() == null ? "" : servidor.getSerCep())%></dd>
                      <dt class="col-6"><hl:message key="rotulo.servidor.telefone"/>:</dt>
                      <dd class="col-6"><%=TextHelper.forHtmlContent(servidor.getSerTel() == null ? "" : servidor.getSerTel())%></dd>
                    </dl>
                  </div>
      
                </div>
                <div class="card">
                  <div class="card-header">
                    <h2 class="card-header-title"><hl:message key="rotulo.boleto.dados.funcionais"/></h2>
                  </div>
                  <div class="card-body">
                    <dl class="row data-list firefox-print-fix">
                      <dt class="col-6"><hl:message key="rotulo.matricula.singular"/>:</dt>
                      <dd class="col-6"><%=TextHelper.forHtmlContent(rseMatricula)%></dd>
                      <dt class="col-6"><hl:message key="rotulo.servidor.dataAdmissao"/>:</dt>
                      <dd class="col-6"><%=TextHelper.forHtmlContent(rseDataAdmissao)%></dd>
                      <dt class="col-6"><hl:message key="rotulo.orgao.singular"/>:</dt>
                      <dd class="col-6"><%=TextHelper.forHtmlContent(orgao.getOrgIdentificador())%> - <%=TextHelper.forHtmlContent(orgao.getOrgNome())%></dd>
                    </dl>
                  </div>
                </div>
              </div>
              <div class="col-sm">
                <div class="card">
                  <div class="card-header">
                    <h2 class="card-header-title"><hl:message key="rotulo.boleto.caracteristicas.operacao"/></h2>
                  </div>
                  <div class="card-body">
                    <dl class="row data-list firefox-print-fix">
                      <dt class="col-6"><hl:message key="rotulo.consignataria.singular"/>:</dt>
                      <dd class="col-6"><%=TextHelper.forHtmlContent(csaNome)%></dd>
                      <dt class="col-6"><hl:message key="rotulo.usuario.responsavel"/>:</dt>
                      <dd class="col-6"><%=TextHelper.forHtmlContent(adeResponsavel)%></dd>
                      <dt class="col-6"><hl:message key="rotulo.consignacao.ranking"/>:</dt>
                      <dd class="col-6"><%=TextHelper.forHtmlContent(ranking)%>&ordf;</dd>
                      <dt class="col-6"><hl:message key="rotulo.natureza.operacao"/>:</dt>
                      <dd class="col-6"><%=TextHelper.forHtmlContent(svcDescricao)%></dd>
                      <dt class="col-6"><hl:message key="rotulo.consignacao.data.contrato"/>:</dt>
                      <dd class="col-6"><%=TextHelper.forHtmlContent(dataSimulacao)%></dd>
                      <dt class="col-6"><hl:message key="rotulo.consignacao.numero.extenso"/>:</dt>
                      <dd class="col-6"><%=TextHelper.forHtmlContent(adeNumero)%></dd>
                      <dt class="col-6"><hl:message key="rotulo.consignacao.valor.parcela"/>:</dt>
                      <dd class="col-6"><hl:message key="rotulo.moeda"/> <%=TextHelper.forHtmlContent(adeVlr)%></dd>
                      <dt class="col-6"><hl:message key="rotulo.consignacao.prazo.extenso"/>:</dt>
                      <dd class="col-6"><%=TextHelper.forHtmlContent(adePrazo)%></dd>
                      <dt class="col-6"><hl:message key="rotulo.consignacao.data.inicial"/>:</dt>
                      <dd class="col-6"><%=TextHelper.forHtmlContent(dataIni)%></dd>
                      <dt class="col-6"><hl:message key="rotulo.consignacao.data.final"/>:</dt>
                      <dd class="col-6"><%=TextHelper.forHtmlContent(dataFim)%></dd>
                      <dt class="col-6"><hl:message key="rotulo.codigo.verba.singular"/>:</dt>
                      <dd class="col-6"><%=TextHelper.forHtmlContent(codVerba)%></dd>
                      <dt class="col-6"><hl:message key="rotulo.consignacao.valor.liberado"/>:</dt>
                      <dd class="col-6"><hl:message key="rotulo.moeda"/> <%=TextHelper.forHtmlContent(adeVlrLiberado)%></dd>
                    </dl>
                  </div>
                </div>
              </div>
            </div>
            </div>
          </div>
        </div>
      </div>
      

      <div class="row">
        <div class="col-sm">
          <%= msgBoleto %>
        </div>
      </div>
      <div class="row">
        <div class="col-sm">
          <p class="mt-5">__________________________________ , ______ <hl:message key="rotulo.data.de"/> _____________________ <hl:message key="rotulo.data.de"/> 20_____</p>
          <div class="row mt-5">
            <div class="col-md-6 text-center">
              <p>__________________________________________________________</p>
              <p class="text-uppercase"><hl:message key="rotulo.servidor.singular"/></p>
            </div>
            <div class="col-md-6 text-center">
              <p>__________________________________________________________</p>
              <p class="text-uppercase"><hl:message key="rotulo.consignataria.singular"/></p>
            </div>
          </div>
        </div>
      </div>

      <div class="form-group slider">
        <div class="tooltip-inner">
          <p class="mb-0"><hl:message key="rotulo.gerado.por.arg0" arg0="<%=TextHelper.forHtmlContent(adeResponsavel)%>" /> - <%=TextHelper.forHtmlContent(dataSimulacao)%>.</p>
        </div>
      </div>

      <div class="btn-action">
        <a class="btn btn-outline-danger mt-2" href="#no-back" onclick="postData('<%=botaoVoltarPaginaInicial ? "../v3/carregarPrincipal" : TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.voltar"/></a>
      </div>

</c:set>
<c:set var="javascript">
<style>
  @media print {    /* for good browsers */
    body {
      margin:0;
      padding:0;
    }
    
    body p, .card-body p{
      font-size: 10px;
      margin-top: 0;
      padding-top: 0;
      line-height: 1.1;
    }
 
    h2, h3, .card .card-header .card-header-title{
      font-size: 12px;
      line-height: 1.2;      
      margin-rigth: .20rem;
      margin-left: .20rem;
      font-weight: bold;
      white-space: nowrap;
    } 
    
    h2, h3, .card, .card .card-header, .card .card-header .card-header-title{
      padding-top: 0;
      padding-bottom: 0;
      margin-top: 0;
      margin-bottom: 0;
    }
    
    .card-header h3 .card-title{
      font-size: 12px !important;
      line-height: 1.2 !important;
    }

    .card-body p{
      padding-bottom: 0;
      margin: 0;
      padding-rigth: .15rem;
    }
         
    #menuAcessibilidade {
      display: none;
    }
    
    .firefox-print-fix {
      display: flex;
    }
    
    .firefox-print-fix dt {
      text-align: left;
    }
    
    .card-header-title {
      font-size: 12px;
    }
    
    .data-list {
      line-height: 1;
      padding:0 !important;
      margin: 0 !important;
    }
    
    .data-list dt, .data-list dd{
      font-size: 10px;
    }

  }
  
  @page {
    margin: 0.3cm 0.3cm;
  }
</style>
<script type="text/JavaScript">
  function imprime() {
    window.print();
  }
</script>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>