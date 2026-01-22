<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TransferObjectHelper"%>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.TransferObject" %>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject" %>
<%@ page import="com.zetra.econsig.dto.entidade.ParamSvcTO" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession"/>

<% 
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String csaCodigo = (String) request.getAttribute("csaCodigo");
String csaNome = (String) request.getAttribute("csaNome");
List<TransferObject>  lstConsignatarias = (List<TransferObject> ) request.getAttribute("lstConsignatarias");
boolean temModuloCompra = (Boolean) request.getAttribute("temModuloCompra");
List<TransferObject> adeRejeitoPgt = (List<TransferObject>) request.getAttribute("adeRejeitoPgt");
List<TransferObject> servicosCadastroSaldo = (List<TransferObject>) request.getAttribute("servicosCadastroSaldo");
boolean bloqueiaCsaLiqSaldoPago = (Boolean) request.getAttribute("bloqueiaCsaLiqSaldoPago");
List<TransferObject> adesSaldoDev = (List<TransferObject>) request.getAttribute("adesSaldoDev");
boolean podeLerComunicacao = (Boolean) request.getAttribute("podeLerComunicacao");
List<TransferObject> comunicacoes = (List<TransferObject>) request.getAttribute("comunicacoes");
List<TransferObject> mensagens = (List<TransferObject>) request.getAttribute("mensagens");
List<TransferObject> adesSemMinAnexos = (List<TransferObject>) request.getAttribute("adesSemMinAnexos");
List<TransferObject> adesSolLiqNaoAtendida = (List<TransferObject>) request.getAttribute("adesSolLiqNaoAtendida");
List<TransferObject> lstSvcCetExpirado = (List<TransferObject>) request.getAttribute("lstSvcCetExpirado");

%>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="title">
  <hl:message key="rotulo.listar.bloqueios.csa.titulo"/>
</c:set>
<head>
  <link rel="stylesheet" href="../css/impromptu.css?<hl:message key="release.tag"/>" type="text/css">
</head>
<c:set var="bodyContent">

<% if (responsavel.isCseSup()) { %>
<form name="form1" method="post" ACTION="../v3/listarBloqueiosConsignataria">
  <input type="hidden" name="acao" value="iniciar"/>
  <div class="row">
    <div class="form-group col-sm-12  col-md-6">
      <label for="consignataria"><hl:message key="rotulo.consignataria.singular"/></label>
      <select name="CSA_CODIGO" id="consignataria" class="form-control form-select" onFocus="SetarEventoMascara(this,'#*200',true);"  onBlur="fout(this);ValidaMascara(this);" onChange="document.form1.submit();"  >
        <option value="" ><hl:message key="rotulo.campo.selecione"/></option>
        <%
        Iterator<TransferObject> itFiltro = lstConsignatarias.iterator();
        while (itFiltro.hasNext()) {
            TransferObject csaTO = itFiltro.next();
            String csa_codigo = (String) csaTO.getAttribute(Columns.CSA_CODIGO);
            String csa_nome = (String) csaTO.getAttribute(Columns.CSA_NOME);
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
  <% if (!TextHelper.isNull(csaCodigo) && temModuloCompra) {   %>
    <hl:listaAcompanhamentoComprav4
      csaCodigo="<%=TextHelper.forHtmlAttribute(csaCodigo)%>"
      orgCodigo="<%=null%>"
      corCodigo="<%=null%>"
      pesquisar="true"
      filtroConfiguravel="2"
      criteriosPesquisa="<%=(CustomTransferObject)(new CustomTransferObject())%>"
      link="../v3/listarBloqueiosConsignataria?acao=iniciar"
      linkPaginacao="${linkPaginacaoTag}"
      offset="${offsetTag}"
      reusePageToken="true"
    />
  <% } %>   

  <% if (!TextHelper.isNull(csaCodigo) && adeRejeitoPgt != null) {   %>
  <%
    String adeCodigoDestino, adeCodigoOrigem, adeNumeroOrigem, adeIdentificadorOrigem, adeTipoVlrOrigem, adeVlrOrigem, adePrazoOrigem, adeDataOrigem, sadDescricaoOrigem;
    String serNome, rseMatricula, serCpf;
    String radData, radDataInfSaldo, radDataPgtSaldo;
    String csaDestNome, csaOriNome,csaDestCod, csaOriCod;
    
  %>
  
      <div class="card">
        <div class="card-header pl-3">
          <h2 class="card-header-title"><hl:message key="rotulo.listar.bloqueios.rejeito.pagamento.saldo.devedor"/></h2>
        </div>
        <div class="card-body table-responsive">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th scope="col"><hl:message key="rotulo.consignataria.origem"/></th>
                <th scope="col"><hl:message key="rotulo.consignataria.destino"/></th>
                <th scope="col"><hl:message key="rotulo.servidor.singular"/></th>
                <th scope="col"><hl:message key="rotulo.consignacao.numero"/></th>
                <th scope="col"><hl:message key="rotulo.consignacao.data.inclusao"/></th>
                <th scope="col" colspan="2"><hl:message key="rotulo.consignacao.valor.parcela.abreviado"/></th>
                <th scope="col"><hl:message key="rotulo.consignacao.prazo.abreviado"/></th>
                <th scope="col"><hl:message key="rotulo.data.compra.abreviado"/></th>
                <th scope="col"><hl:message key="rotulo.saldo.devedor.data.abreviado"/></th>
                <th scope="col"><hl:message key="rotulo.saldo.devedor.data.pagamento.abreviado"/></th>
                <th scope="col"><hl:message key="rotulo.consignacao.status"/></th>
                <th><hl:message key="rotulo.acoes"/></th>
              </tr>
            </thead>
            <tbody>
<%
    TransferObject ade = null;
    
    Iterator<TransferObject> itSaldoDevNew = adeRejeitoPgt.iterator();
    while (itSaldoDevNew.hasNext()) {
      ade = itSaldoDevNew.next();
      
      csaDestNome = (String) ade.getAttribute(Columns.CSA_NOME+"Dest").toString();
      csaOriNome = (String) ade.getAttribute(Columns.CSA_NOME+"Ori").toString();
      csaDestCod = (String) ade.getAttribute(Columns.CSA_CODIGO+"Dest").toString();
      csaOriCod = (String) ade.getAttribute(Columns.CSA_CODIGO+"Ori").toString();      
      serNome = (String) ade.getAttribute(Columns.SER_NOME).toString();
      rseMatricula = (String) ade.getAttribute(Columns.RSE_MATRICULA).toString();
      serCpf = (String) ade.getAttribute(Columns.SER_CPF).toString();
      adeDataOrigem = DateHelper.reformat(ade.getAttribute(Columns.ADE_DATA).toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern());
      radData = TextHelper.isNull(ade.getAttribute(Columns.RAD_DATA)) ? "" : DateHelper.reformat(ade.getAttribute(Columns.RAD_DATA).toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern());
      radDataInfSaldo = TextHelper.isNull(ade.getAttribute(Columns.RAD_DATA_INF_SALDO)) ? "" : DateHelper.reformat(ade.getAttribute(Columns.RAD_DATA_INF_SALDO).toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern());
      radDataPgtSaldo = TextHelper.isNull(ade.getAttribute(Columns.RAD_DATA_PGT_SALDO)) ? "" : DateHelper.reformat(ade.getAttribute(Columns.RAD_DATA_PGT_SALDO).toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern());
      
      adeCodigoDestino = ade.getAttribute(Columns.ADE_CODIGO+"Dest").toString();
      adeCodigoOrigem = ade.getAttribute(Columns.ADE_CODIGO).toString();
      adeTipoVlrOrigem = (String) ade.getAttribute(Columns.ADE_TIPO_VLR).toString();
      adeVlrOrigem = ade.getAttribute(Columns.ADE_VLR) != null ? ade.getAttribute(Columns.ADE_VLR).toString() : "";
          
      if (!adeVlrOrigem.equals("")) {
        adeVlrOrigem = NumberHelper.format(Double.valueOf(adeVlrOrigem).doubleValue(), NumberHelper.getLang());
      }

      adeNumeroOrigem = ade.getAttribute(Columns.ADE_NUMERO).toString();
      adeIdentificadorOrigem = ade.getAttribute(Columns.ADE_IDENTIFICADOR) != null ? ade.getAttribute(Columns.ADE_IDENTIFICADOR).toString() : "";
      adePrazoOrigem = ade.getAttribute(Columns.ADE_PRAZO) != null ? ade.getAttribute(Columns.ADE_PRAZO).toString() : ApplicationResourcesHelper.getMessage("rotulo.consignacao.prazo.indeterminado.abreviado", responsavel);

      sadDescricaoOrigem = ade.getAttribute(Columns.SAD_DESCRICAO).toString();          
%>  
              <tr>
                <td><%=TextHelper.forHtmlContent(csaOriNome)%></td>
                <td><%=TextHelper.forHtmlContent(csaDestNome)%></td>
                <td><%=TextHelper.forHtmlContent(rseMatricula + " - " + serNome + " - " + serCpf)%></td>
                <td><%=TextHelper.forHtmlContent(adeNumeroOrigem)%></td>
                <td><%=TextHelper.forHtmlContent(adeDataOrigem)%></td>
                <td class="text-right"><%=TextHelper.forHtmlContent(ParamSvcTO.getDescricaoTpsTipoVlr(adeTipoVlrOrigem))%></td>
                <td class="text-right"><%=TextHelper.forHtmlContent(adeVlrOrigem)%></td>
                <td class="text-right"><%=TextHelper.forHtmlContent(adePrazoOrigem)%></td>
                <td><%=TextHelper.forHtmlContent(radData)%></td>
                <td><%=TextHelper.forHtmlContent(radDataInfSaldo)%></td>
                <td><%=TextHelper.forHtmlContent(radDataPgtSaldo)%></td>
                <td><%=TextHelper.forHtmlContent(sadDescricaoOrigem)%></td>
                <td>
                  <div class="actions">
                    <div class="dropdown">
                      <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu4" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <div class="form-inline">
                          <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.mais.acoes"/>" aria-label="<hl:message key="rotulo.mais.acoes"/>">
                            <svg>
                              <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use>
                            </svg>
                          </span> <hl:message key="rotulo.botao.opcoes"/>
                        </div>
                      </a>
                      <div class="dropdown-menu dropdown-menu-right">
                        <% if (responsavel.isCseSup() || (responsavel.isCsa() && responsavel.getCsaCodigo().equalsIgnoreCase(csaOriCod))) { %>
                          <a class="dropdown-item" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL("../v3/consultarConsignacao?acao=detalharConsignacao&ADE_CODIGO=" + adeCodigoOrigem, request))%>')">
                            <hl:message key="mensagem.consultar.consignacao.clique.aqui"/>
                          </a>       
                        <% } else if (responsavel.isCsa() && responsavel.getCsaCodigo().equalsIgnoreCase(csaDestCod)) { %>
                          <a class="dropdown-item" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL("../v3/consultarConsignacao?acao=detalharConsignacao&ADE_CODIGO=" + adeCodigoOrigem + "&adeDest=" + adeCodigoDestino + "&barraAcoes=false&isOrigem=1", request))%>')">
                            <hl:message key="mensagem.consultar.consignacao.clique.aqui"/>
                          </a>       
                        <% } %>
                                  
                        <% if (responsavel.temPermissao(CodedValues.FUN_EDT_SALDO_DEVEDOR) && responsavel.isCsa() && responsavel.getCsaCodigo().equalsIgnoreCase(csaOriCod)) { %>
                          <a class="dropdown-item" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL("../v3/editarSaldoDevedor?acao=iniciar&tipo=compra&ADE_CODIGO=" + adeCodigoOrigem, request))%>')">
                            <hl:message key="mensagem.editar.saldo.devedor.clique.aqui"/>
                          </a>
                        <% } else if(responsavel.isCsa()) { %>
                          <a>
                            <hl:message key="mensagem.editar.saldo.devedor.clique.aqui"/>
                          </a>
                        <% } %>
                        
                        <% if (responsavel.temPermissao(CodedValues.FUN_CONF_LIQUIDACAO_COMPRA) && responsavel.isCsa() && responsavel.getCsaCodigo().equalsIgnoreCase(csaOriCod)) { %>
                          <a class="dropdown-item" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL("../v3/liquidarConsignacao?acao=efetivarAcao&ADE_CODIGO=" + adeCodigoOrigem, request))%>')">
                            <hl:message key="mensagem.liquidar.consignacao.clique.aqui"/>
                          </a> 
                        <% } else if(responsavel.isCsa()) { %>
                          <a>
                            <hl:message key="mensagem.liquidar.consignacao.clique.aqui"/>
                          </a>
                        <% } %>
                        
                        <% if (responsavel.temPermissao(CodedValues.FUN_INFORMAR_PGT_SALDO_DEVEDOR)  && responsavel.isCsa() && responsavel.getCsaCodigo().equalsIgnoreCase(csaDestCod)) { %>
                          <a class="dropdown-item" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL("../v3/acompanharPortabilidade?opt=i&acao=informarPgtSdv&ade=" + adeCodigoOrigem, request))%>')">
                            <hl:message key="mensagem.informar.pagamento.saldo.devedor.consignacao"/>
                          </a>
                        <% } else if(responsavel.isCsa()) { %>
                          <a>
                            <hl:message key="mensagem.informar.pagamento.saldo.devedor.consignacao"/>
                          </a>
                        <% } %>
                      </div>
                    </div>
                  </div>
                </td>
              </tr>
            <% 
                } 
            %>
            </tbody>
            <tfoot>
        <tr>
          <td colspan="13"><%=ApplicationResourcesHelper.getMessage("rotulo.paginacao.titulo.bloqueio.rejeito.pagamento.saldo.devedor", responsavel) + " - " %>
            <span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTituloRejPagDev"))%></span>
          </td>
        </tr>
      </tfoot>
      </table>
      <% request.setAttribute("_indice", "RejPagDev"); %>
      <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
      </div>
    </div>

<% } %> 


<% if (!TextHelper.isNull(csaCodigo) && servicosCadastroSaldo != null && !servicosCadastroSaldo.isEmpty()) { %>
  <div class="card">
    <div class="card-header pl-3">
      <h2 class="card-header-title"><hl:message key="rotulo.listar.bloqueios.pendencia.solicitacao.saldo.devedor"/></h2>
    </div>
    <div class="card-body table-responsive">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th><hl:message key="rotulo.ocorrencia.responsavel"/></th>
            <th><hl:message key="rotulo.consignacao.numero"/></th>
            <th><hl:message key="rotulo.consignacao.identificador"/></th>
            <th><hl:message key="rotulo.servico.singular"/></th>
            <th><hl:message key="rotulo.consignacao.data.inclusao"/></th>
            <th><hl:message key="rotulo.consignacao.valor.parcela.abreviado"/></th>
            <th><hl:message key="rotulo.consignacao.prazo.abreviado"/></th>
            <th><hl:message key="rotulo.consignacao.pagas"/></th>
            <% if (bloqueiaCsaLiqSaldoPago) { %>
            <th><hl:message key="rotulo.saldo.devedor.data.abreviado"/></th>
            <th><hl:message key="rotulo.saldo.devedor.valor.abreviado"/></th>
            <% } %>
            <th><hl:message key="rotulo.consignacao.status"/></th>
            <th><hl:message key="rotulo.acoes"/></th>
          </tr>
        </thead>
        <tbody>
          <%
              String adeCodigo, adeNumero, adeTipoVlr, adeData, adePrazo, adeVlr, adeIdentificador, prdPagas, adeCodReg;
              String servico, servidor, serTel, sadDescricao, cpf;
              String loginResponsavel, adeResponsavel;
              String sdvValor, sdvData;

              CustomTransferObject ade = null;

              Iterator<TransferObject> itSaldoDev = adesSaldoDev.iterator();
              while (itSaldoDev.hasNext()) {
                  ade = (CustomTransferObject) itSaldoDev.next();
                  ade = TransferObjectHelper.mascararUsuarioHistorico(ade, null, responsavel);
                  adeCodigo = ade.getAttribute(Columns.ADE_CODIGO).toString();

                  adeTipoVlr = (String) ade.getAttribute(Columns.ADE_TIPO_VLR);
                  adeVlr = ade.getAttribute(Columns.ADE_VLR) != null ? ade.getAttribute(Columns.ADE_VLR).toString() : "";
                  if (!adeVlr.equals("")) {
                      adeVlr = NumberHelper.format(Double.valueOf(adeVlr).doubleValue(), NumberHelper.getLang());
                  }

                  adeNumero = ade.getAttribute(Columns.ADE_NUMERO).toString();
                  adeIdentificador = ade.getAttribute(Columns.ADE_IDENTIFICADOR) != null ? ade.getAttribute(Columns.ADE_IDENTIFICADOR).toString() : "";
                  adePrazo = ade.getAttribute(Columns.ADE_PRAZO) != null ? ade.getAttribute(Columns.ADE_PRAZO).toString() : "Indeterminado";
                  adeData = DateHelper.reformat(ade.getAttribute(Columns.ADE_DATA).toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern());
                  prdPagas = ade.getAttribute(Columns.ADE_PRD_PAGAS) != null ? ade.getAttribute(Columns.ADE_PRD_PAGAS).toString() : "0";
                  servico = (ade.getAttribute(Columns.CNV_COD_VERBA) != null && !ade.getAttribute(Columns.CNV_COD_VERBA).toString().equals("")) ? ade.getAttribute(Columns.CNV_COD_VERBA).toString() : ade.getAttribute(Columns.SVC_IDENTIFICADOR).toString();
                  servico += (ade.getAttribute(Columns.ADE_INDICE) != null && !ade.getAttribute(Columns.ADE_INDICE).toString().equals("")) ? ade.getAttribute(Columns.ADE_INDICE).toString() : "";
                  servico += " - " + ade.getAttribute(Columns.SVC_DESCRICAO).toString();

                  loginResponsavel = ade.getAttribute(Columns.USU_LOGIN) != null ? ade.getAttribute(Columns.USU_LOGIN).toString() : "";
                  adeResponsavel = (loginResponsavel.equalsIgnoreCase((String) ade.getAttribute(Columns.USU_CODIGO)) && ade.getAttribute(Columns.USU_TIPO_BLOQ) != null) ? (ade.getAttribute(Columns.USU_TIPO_BLOQ).toString() + "(*)") : loginResponsavel;

                  sdvValor = ade.getAttribute(Columns.SDV_VALOR) != null ? NumberHelper.format(((java.math.BigDecimal) ade.getAttribute(Columns.SDV_VALOR)).doubleValue(), NumberHelper.getLang()) : "";
                  sdvData = ade.getAttribute(Columns.SDV_DATA_MOD) != null ? DateHelper.toDateTimeString((java.util.Date) ade.getAttribute(Columns.SDV_DATA_MOD)) : "";

                  sadDescricao = ade.getAttribute(Columns.SAD_DESCRICAO).toString();
          %>
          <tr>
            <td><%=TextHelper.forHtmlContent(adeResponsavel)%></td>
            <td><%=TextHelper.forHtmlContent(adeNumero)%></td>
            <td><%=TextHelper.forHtmlContent(adeIdentificador)%></td>
            <td><%=TextHelper.forHtmlContent(servico)%></td>
            <td><%=TextHelper.forHtmlContent(adeData)%></td>
            <td class="text-right"><%=TextHelper.forHtmlContent(ParamSvcTO.getDescricaoTpsTipoVlr(adeTipoVlr))%> <%=TextHelper.forHtmlContent(adeVlr)%></td>
            <td class="text-right"><%=TextHelper.forHtmlContent(adePrazo)%></td>
            <td class="text-right"><%=TextHelper.forHtmlContent(prdPagas)%></td>
            <% if (bloqueiaCsaLiqSaldoPago) { %>
            <td><%=TextHelper.forHtmlContent(sdvData)%></td>
            <td class="text-right"><%=TextHelper.forHtmlContent(sdvValor)%></td>
            <% } %>
            <td><%=TextHelper.forHtmlContent(sadDescricao)%></td>     
            <td>
              <div class="actions">
                <div class="dropdown">
                  <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <div class="form-inline">
                      <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.mais.acoes"/>" aria-label="<hl:message key="rotulo.mais.acoes"/>"> <svg>
                        <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                      </span> <hl:message key="rotulo.botao.opcoes"/>
                    </div>
                  </a>
                  <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                    <% if (responsavel.temPermissao(CodedValues.FUN_EDT_SALDO_DEVEDOR_SOLICITACAO_SER) && responsavel.isCsaCor()) { %>
                      <a class="dropdown-item" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL("../v3/editarSaldoDevedorSolicitacao?acao=iniciar&tipo=solicitacao_saldo&ADE_CODIGO=" + adeCodigo, request))%>')">
                        <hl:message key="mensagem.editar.saldo.devedor.clique.aqui"/>
                      </a>
                    <% } %>
                    <% if (responsavel.temPermissao(CodedValues.FUN_LIQ_CONTRATO) && responsavel.isCsaCor() && bloqueiaCsaLiqSaldoPago) { %>
                      <a class="dropdown-item" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL("../v3/liquidarConsignacao?acao=efetivarAcao&ADE_CODIGO=" + adeCodigo, request))%>')">
                        <hl:message key="mensagem.liquidar.consignacao.clique.aqui"/>
                      </a>
                    <% } %>
                    <a class="dropdown-item" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL("../v3/listarSolicitacaoSaldo?acao=detalharConsignacao&ADE_CODIGO=" + adeCodigo, request))%>')">
                      <hl:message key="mensagem.consultar.consignacao.clique.aqui"/>
                    </a>
                  </div>
                </div>
              </div>
            </td>
          </tr>
        <%
            }
        %>
        </tbody>
        <tfoot>
          <tr>
            <td colspan="12"><%=ApplicationResourcesHelper.getMessage("rotulo.paginacao.titulo.bloqueio.pendencia.saldo.devedor", responsavel) + " - " %>
              <span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTituloSaldoDev"))%></span>
            </td>
          </tr>
        </tfoot>
      </table>
      <% request.setAttribute("_indice", "SaldoDev"); %>
      <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
      </div>
    </div>
<% } %>

<% if (!TextHelper.isNull(csaCodigo) && podeLerComunicacao) { %>
<div class="card">
    <div class="card-header pl-3">
      <h2 class="card-header-title"><hl:message key="rotulo.listar.bloqueios.pendencia.comunicacao"/></h2>
    </div>
    <div class="card-body table-responsive">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th><hl:message key="rotulo.servidor.matricula"/></th>
          	<th><hl:message key="rotulo.usuario.singular"/></th>                           
          	<th><hl:message key="rotulo.comunicacao.data"/></th>
          	<th><hl:message key="rotulo.comunicacao.mensagem"/></th>
          	<th><hl:message key="rotulo.comunicacao.ler"/></th>        
          </tr>
        </thead>
        <tbody>
<%
    String usuNome = "", cmnCodigo = "", matricula = "";
    String data = "";
    String msg = "";
    String tipoEntidade = "";
    long leituras = 0L;
               
    Iterator<TransferObject> itComunicacao = comunicacoes.iterator();
    while (itComunicacao.hasNext()) {
      TransferObject next = (TransferObject) itComunicacao.next();
      matricula = !TextHelper.isNull(next.getAttribute(Columns.RSE_MATRICULA)) ? next.getAttribute(Columns.RSE_MATRICULA).toString() : "";
      cmnCodigo = (String) next.getAttribute(Columns.CMN_CODIGO);
      usuNome = (String) next.getAttribute(Columns.USU_NOME);
      data = DateHelper.toDateTimeString((Date) next.getAttribute(Columns.CMN_DATA));                          
      msg = (String) next.getAttribute(Columns.CMN_TEXTO);
      leituras = ((Long) next.getAttribute("COUNT_LEITURAS")).longValue();
                  
      boolean lida = (leituras > 0) ? true:false;
      String displayMsg = (msg.length() > 100) ? msg.substring(0, 100) + "..." :msg;
%>               
        <tr>
          <td class="text-center"><%=TextHelper.forHtmlContent(matricula)%></td>
          <td><%=TextHelper.forHtmlContent(usuNome)%></td>
          <td><%=TextHelper.forHtmlContent(data)%></td>
          <td class="text-left"><%if (!lida) { %> <B> <% }%> <a class="linkTexto" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL("../v3/enviarComunicacao?acao=editar&cmn_codigo=" + cmnCodigo, request))%>')"><%=TextHelper.forHtmlContent(displayMsg)%></a><%if (!lida) { %> </B> <% }%></td>                    
          <td class="text-center">
            <a href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL("../v3/enviarComunicacao?acao=editar&cmn_codigo=" + cmnCodigo, request))%>')">
              <hl:message key="mensagem.ler.comunicacao.clique.aqui"/>
            </a>
          </td>
        </tr>
      <% 
          } 
      %>
      </tbody>    
     <tfoot>
        <tr>
          <td colspan="5"><%=ApplicationResourcesHelper.getMessage("rotulo.paginacao.titulo.bloqueio.pendencia.comunicacao", responsavel) + " - " %>
            <span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTituloComunicacao"))%></span>
          </td>
        </tr>
      </tfoot>
      </table>
      <% request.setAttribute("_indice", "Comunicacao"); %>
      <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
      </div>
    </div>
<% } %>

<% if (!TextHelper.isNull(csaCodigo)) { %>
  <div class="card">
    <div class="card-header pl-3">
      <h2 class="card-header-title"><hl:message key="rotulo.listar.bloqueios.pendencia.mensagem"/></h2>
    </div>
    <div class="card-body table-responsive">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th align="center" width="20%" class="tabelatopo"><hl:message key="rotulo.usuario.login"/></th>
            <th align="center" width="60%" class="tabelatopo"><hl:message key="rotulo.mensagem.titulo"/></th>                           
            <th align="center" width="20%" class="tabelatopo"><hl:message key="rotulo.mensagem.data.criacao"/></th>
        </tr>
      </thead>
      <tbody>
<%
    String usuLogin = "", menTitulo = "", menData = "";
    
    Iterator<TransferObject> itMensagem = mensagens.iterator();
    while (itMensagem.hasNext()) {
      TransferObject next = (TransferObject) itMensagem.next();
      usuLogin = (String) next.getAttribute(Columns.USU_LOGIN);
      menTitulo = (String) next.getAttribute(Columns.MEN_TITULO);
      menData = DateHelper.toDateTimeString((Date) next.getAttribute(Columns.MEN_DATA));                          
%>               
        <tr>
          <td class="text-center"><%=TextHelper.forHtmlContent(usuLogin)%></td>
          <td class="text-center"><%=TextHelper.forHtmlContent(menTitulo)%></td>
          <td class="text-center"><%=TextHelper.forHtmlContent(menData)%></td>
        </tr>
        <% 
            }
        %>
       </tbody>  
        <tfoot>
        <tr>
          <td colspan="3"><%=ApplicationResourcesHelper.getMessage("rotulo.paginacao.titulo.bloqueio.pendencia.confirmacao.leitura.mensagem", responsavel) + " - " %>
            <span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTituloMensagem"))%></span>
          </td>
        </tr>
      </tfoot>
      </table>
      <% request.setAttribute("_indice", "Mensagem"); %>
      <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
      </div>
    </div>
<% } %>

<%-- DESENV-18230 : listagem de serviços com cadastro de CET / Taxa de juros expirada --%>
<% if (!TextHelper.isNull(csaCodigo) && lstSvcCetExpirado != null && !lstSvcCetExpirado.isEmpty()) { %>
  <div class="card">
    <div class="card-header pl-3">
      <h2 class="card-header-title"><hl:message key="rotulo.listar.bloqueios.servicos.cet.expirado"/></h2>
    </div>
    <div class="card-body table-responsive">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th scope="col"><hl:message key="rotulo.servico.singular"/></th>
            <% if (responsavel.temPermissao(CodedValues.FUN_CONS_TAXA_JUROS)) { %>
            <th class="text-center"><hl:message key="rotulo.acoes"/></th>
            <% } %>
        </tr>
      </thead>
      <tbody>
      <%
      for (TransferObject next : lstSvcCetExpirado) {
        String svcCodigo = (String) next.getAttribute(Columns.SVC_CODIGO);
        String svcDescricao = (String) next.getAttribute(Columns.SVC_DESCRICAO);
        String linkEdtTaxa = String.format("../v3/manterTaxas?acao=consultarTaxa&CSA_CODIGO=%s&SVC_CODIGO=%s&SVC_DESCRICAO=%s&titulo=%s", csaCodigo, svcCodigo, svcDescricao, csaNome);
      %>
        <tr>
          <td><%=TextHelper.forHtmlContent(svcDescricao)%></td>
          <% if (responsavel.temPermissao(CodedValues.FUN_CONS_TAXA_JUROS)) { %>
          <td class="text-center">
            <a href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(linkEdtTaxa, request))%>')">
              <hl:message key="rotulo.acoes.detalhe"/>
            </a>
          </td>
          <% } %>
        </tr>
      <% 
      }
      %>
       </tbody>  
        <tfoot>
        <tr>
          <td colspan="3"><%=ApplicationResourcesHelper.getMessage("rotulo.paginacao.titulo.bloqueio.pendencia.cet.expirado", responsavel) + " - " %>
            <span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTituloSvcCetExpirado"))%></span>
          </td>
        </tr>
      </tfoot>
      </table>
      <% request.setAttribute("_indice", "SvcCetExpirado"); %>
      <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
      </div>
    </div>
<% } %>

<% if (!TextHelper.isNull(csaCodigo) && adesSemMinAnexos != null && !adesSemMinAnexos.isEmpty()) { %>
      <div class="card">
         <div class="card-header pl-3">
           <h2 class="card-header-title"><hl:message key="rotulo.listar.bloqueios.ades.sem.num.min.anexo"/></h2>
         </div>
         <div class="card-body table-responsive">
         <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th align="center" width="10%" class="tabelatopo"><hl:message key="rotulo.consignacao.numero"/></th>
                <th align="center" width="15%" class="tabelatopo"><hl:message key="rotulo.consignacao.identificador"/></th>
                <th align="center" width="45%" class="tabelatopo"><hl:message key="rotulo.servico.singular"/></th>                           
                <th align="center" width="12%" class="tabelatopo"><hl:message key="rotulo.servico.numero.min.anexo.ade"/></th>
                <th align="center" width="13%" class="tabelatopo"><hl:message key="rotulo.num.anexos.ade"/></th>
                <th align="center" width="5%" class="tabelatopo"><hl:message key="rotulo.acoes"/></th>
              </tr>
            </thead>
           <tbody>
           <%                     
               for (TransferObject adeSemAnexoMin: adesSemMinAnexos) {
           %>               
              <tr>
                <td class="text-center"><%=TextHelper.forHtmlContent(adeSemAnexoMin.getAttribute(Columns.ADE_NUMERO))%></td>
                <td class="text-center"><%=(!TextHelper.isNull(adeSemAnexoMin.getAttribute(Columns.ADE_IDENTIFICADOR))) ? TextHelper.forHtmlContent(adeSemAnexoMin.getAttribute(Columns.ADE_IDENTIFICADOR)) : "-"%></td>
                <td class="text-center"><%=TextHelper.forHtmlContent(adeSemAnexoMin.getAttribute(Columns.SVC_DESCRICAO))%></td>
                <td class="text-center"><%=TextHelper.forHtmlContent(adeSemAnexoMin.getAttribute(Columns.PSE_VLR_REF))%></td>
                <td class="text-center"><%=TextHelper.forHtmlContent(adeSemAnexoMin.getAttribute("NUM_ANEXOS"))%></td>
                <td>
                  <div class="actions">
                    <div class="dropdown">
                      <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu4" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <div class="form-inline">
                          <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.mais.acoes"/>" aria-label="<hl:message key="rotulo.mais.acoes"/>">
                            <svg>
                              <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use>
                            </svg>
                          </span> <hl:message key="rotulo.botao.opcoes"/>
                        </div>
                      </a>
                      <div class="dropdown-menu dropdown-menu-right">                        
                          <a class="dropdown-item" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL("../v3/editarAnexosConsignacao?acao=exibir&ADE_CODIGO=" + TextHelper.forHtmlContent(adeSemAnexoMin.getAttribute(Columns.ADE_CODIGO)), request))%>')">
                            <hl:message key="mensagem.editar.anexo.consignacao.clique.aqui"/>
                          </a>
                      </div>
                    </div>
                  </div>
                </td>   
              </tr>
        <% 
              }
        %>
           </tbody>  
           <tfoot>
             <tr>
               <td colspan="3"><%=ApplicationResourcesHelper.getMessage("rotulo.paginacao.titulo.bloqueio.pendencia.ade.sem.min.anexos", responsavel) + " - " %>
                 <span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTituloAdesSemMinAnexos"))%></span>
               </td>
             </tr>
          </tfoot>
        </table>
      <% request.setAttribute("_indice", "AdesSemMinAnexos"); %>
      <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
      </div>
    </div>
<% } %>
<% if (!TextHelper.isNull(csaCodigo)) { %>
  <div class="card">
    <div class="card-header pl-3">
      <h2 class="card-header-title"><hl:message key="rotulo.listar.bloqueios.solicitacao.liquidacao.nao.atendida"/></h2>
    </div>
    <div class="card-body table-responsive">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th><hl:message key="rotulo.ocorrencia.responsavel"/></th>
            <th><hl:message key="rotulo.consignacao.numero"/></th>
            <th><hl:message key="rotulo.consignacao.identificador"/></th>
            <th><hl:message key="rotulo.servico.singular"/></th>
            <th><hl:message key="rotulo.consignacao.data.inclusao"/></th>
            <th><hl:message key="rotulo.consignacao.valor.parcela.abreviado"/></th>
            <th><hl:message key="rotulo.consignacao.prazo.abreviado"/></th>
            <th><hl:message key="rotulo.consignacao.pagas"/></th>
            <th><hl:message key="rotulo.consignacao.status"/></th>
            <th><hl:message key="rotulo.acoes"/></th>
          </tr>
        </thead>
        <tbody>
          <%
              String adeCodigo, adeNumero, adeTipoVlr, adeData, adePrazo, adeVlr, adeIdentificador, prdPagas, adeCodReg;
              String servico, servidor, serTel, sadDescricao, cpf;
              String loginResponsavel, adeResponsavel;

              CustomTransferObject ade = null;

              Iterator<TransferObject> itSolLiqNaoAtendida = adesSolLiqNaoAtendida.iterator();
              while (itSolLiqNaoAtendida.hasNext()) {
                  ade = (CustomTransferObject) itSolLiqNaoAtendida.next();
                  ade = TransferObjectHelper.mascararUsuarioHistorico(ade, null, responsavel);
                  adeCodigo = ade.getAttribute(Columns.ADE_CODIGO).toString();

                  adeTipoVlr = (String) ade.getAttribute(Columns.ADE_TIPO_VLR);
                  adeVlr = ade.getAttribute(Columns.ADE_VLR) != null ? ade.getAttribute(Columns.ADE_VLR).toString() : "";
                  if (!adeVlr.equals("")) {
                      adeVlr = NumberHelper.format(Double.valueOf(adeVlr).doubleValue(), NumberHelper.getLang());
                  }

                  adeNumero = ade.getAttribute(Columns.ADE_NUMERO).toString();
                  adeIdentificador = ade.getAttribute(Columns.ADE_IDENTIFICADOR) != null ? ade.getAttribute(Columns.ADE_IDENTIFICADOR).toString() : "";
                  adePrazo = ade.getAttribute(Columns.ADE_PRAZO) != null ? ade.getAttribute(Columns.ADE_PRAZO).toString() : "Indeterminado";
                  adeData = DateHelper.reformat(ade.getAttribute(Columns.ADE_DATA).toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern());
                  prdPagas = ade.getAttribute(Columns.ADE_PRD_PAGAS) != null ? ade.getAttribute(Columns.ADE_PRD_PAGAS).toString() : "0";
                  servico = (ade.getAttribute(Columns.CNV_COD_VERBA) != null && !ade.getAttribute(Columns.CNV_COD_VERBA).toString().equals("")) ? ade.getAttribute(Columns.CNV_COD_VERBA).toString() : ade.getAttribute(Columns.SVC_IDENTIFICADOR).toString();
                  servico += (ade.getAttribute(Columns.ADE_INDICE) != null && !ade.getAttribute(Columns.ADE_INDICE).toString().equals("")) ? ade.getAttribute(Columns.ADE_INDICE).toString() : "";
                  servico += " - " + ade.getAttribute(Columns.SVC_DESCRICAO).toString();

                  loginResponsavel = ade.getAttribute(Columns.USU_LOGIN) != null ? ade.getAttribute(Columns.USU_LOGIN).toString() : "";
                  adeResponsavel = (loginResponsavel.equalsIgnoreCase((String) ade.getAttribute(Columns.USU_CODIGO)) && ade.getAttribute(Columns.USU_TIPO_BLOQ) != null) ? (ade.getAttribute(Columns.USU_TIPO_BLOQ).toString() + "(*)") : loginResponsavel;

                  sadDescricao = ade.getAttribute(Columns.SAD_DESCRICAO).toString();
          %>
          <tr>
            <td><%=TextHelper.forHtmlContent(adeResponsavel)%></td>
            <td><%=TextHelper.forHtmlContent(adeNumero)%></td>
            <td><%=TextHelper.forHtmlContent(adeIdentificador)%></td>
            <td><%=TextHelper.forHtmlContent(servico)%></td>
            <td><%=TextHelper.forHtmlContent(adeData)%></td>
            <td class="text-right"><%=TextHelper.forHtmlContent(ParamSvcTO.getDescricaoTpsTipoVlr(adeTipoVlr))%> <%=TextHelper.forHtmlContent(adeVlr)%></td>
            <td class="text-right"><%=TextHelper.forHtmlContent(adePrazo)%></td>
            <td class="text-right"><%=TextHelper.forHtmlContent(prdPagas)%></td>
            <td><%=TextHelper.forHtmlContent(sadDescricao)%></td>     
            <td>
              <div class="actions">
                <div class="dropdown">
                  <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <div class="form-inline">
                      <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.mais.acoes"/>" aria-label="<hl:message key="rotulo.mais.acoes"/>"> <svg>
                        <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                      </span> <hl:message key="rotulo.botao.opcoes"/>
                    </div>
                  </a>
                  <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                    <% if (responsavel.temPermissao(CodedValues.FUN_CONF_LIQUIDACAO)) { %>
                      <a class="dropdown-item" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL("../v3/confirmarLiquidacao?acao=efetivarAcao&ade=" + adeCodigo, request))%>')">
                        <hl:message key="rotulo.efetiva.acao.consignacao.confLiquidacao"/>
                      </a>
                    <% } %>
                    <% if (responsavel.temPermissao(CodedValues.FUN_CANC_SOLICITAR_LIQUIDAR_CONSIGNACAO)) { %>
                      <a class="dropdown-item" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL("../v3/solicitarLiquidacao?acao=efetivarCancelamentoSolicitacao&ADE_CODIGO=" + adeCodigo , request))%>')">
                        <hl:message key="mensagem.acao.cancelar.solicitar.liquidacao.consignacao"/>
                      </a>
                    <% } %>
                  </div>
                </div>
              </div>
            </td>
          </tr>
        <%
            }
        %>
        </tbody>
        <tfoot>
          <tr>
            <td colspan="12"><%=ApplicationResourcesHelper.getMessage("rotulo.paginacao.titulo.bloqueio.pela.solicitacao.liquidacao.nao.atendida", responsavel) + " - " %>
              <span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTituloSolicitacaoLiquidacaoNaoAtendida"))%></span>
            </td>
          </tr>
        </tfoot>
      </table>
      <% request.setAttribute("_indice", "SolicitacaoLiquidacaoNaoAtendida"); %>
      <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
      </div>
    </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#" onClick="postData('../v3/carregarPrincipal')">
      <hl:message key="rotulo.botao.cancelar"/>
    </a>
  </div>
  <% } %>
<% response.flushBuffer(); %>
</c:set>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>