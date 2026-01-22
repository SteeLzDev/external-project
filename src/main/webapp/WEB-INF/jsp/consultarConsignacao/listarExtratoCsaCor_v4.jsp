<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

List<TransferObject> transacoes = (List<TransferObject>) request.getAttribute("transacoes");
List<String> dozeMesesPraTras = (List<String>) request.getAttribute("dozeMesesPraTras");
String mesAnoSelecionado = (String) request.getAttribute("mesAnoSelecionado");
String periodoIni = (String) request.getAttribute("periodoIni");
String periodoFim = (String) request.getAttribute("periodoFim");
String totalTrans = NumberHelper.reformat((!TextHelper.isNull(request.getAttribute("somaTransDia"))) ? ((BigDecimal) request.getAttribute("somaTransDia")).toString() : new BigDecimal("0").toString(), "en", NumberHelper.getLang());
List<TransferObject> lstTipoOcorrencia = (List<TransferObject>) request.getAttribute("lstTipoOcorrencia");
String tocCodigoSelecionado = (String) request.getAttribute("tocCodigoSelecionado");

boolean csaTemCorrespondentes = responsavel.isCsa() && (boolean) request.getAttribute("csaTemCorrespondentes");

%>
<c:set var="title">
    <hl:message key="rotulo.consultar.orgao.titulo"/>
</c:set>

<c:set var="imageHeader">
    <use xlink:href="#i-manutencao"></use>
</c:set>

<c:set var="bodyContent">
    <div class="row">
      <div class="col-sm-12 col-md-12 mb-2">
        <div class="float-end">
          <div class="btn-action">
          </div>
        </div>
      </div>
    </div>
    <form name="form1" method="post" action="../v3/listarExtrato?<%out.print(SynchronizerToken.generateToken4URL(request));%>">
      <div class="row">
      
      
        <div class="col-sm-5 col-md-4">
          <div class="card">
            <div class="card-header hasIcon pl-3">
              <h2 class="card-header-title"><hl:message key="rotulo.botao.pesquisar"/></h2>
            </div>
            <div class="card-body">
                <div class="row">
                  <div class="form-group col-sm">
                    <label for="mesAno"><hl:message key="rotulo.extrato.filtro.mes.ano"/></label>
                    <select class="form-control form-select select" id="mesAno" name="mesAno" onFocus="SetarEventoMascaraV4(this,'#*200',true);"  onBlur="fout(this);ValidaMascaraV4(this);" nf="Filtrar">
                      <optgroup label="<hl:message key="rotulo.extrato.filtro.mes.ano"/>:">
                        <option value="" <%=(String)(TextHelper.isNull(mesAnoSelecionado) ? "SELECTED" : "")%> ><hl:message key="rotulo.campo.sem.filtro"/></option>
                        <%  
                          Iterator<String> mesesIterator = dozeMesesPraTras.iterator();
                          while (mesesIterator.hasNext()) {
                              String mesAno = mesesIterator.next();
                        %>
                            <option value="<%=mesAno%>" <%= mesAnoSelecionado != null && mesAno.equals(mesAnoSelecionado) ? "SELECTED" : "" %>><%=mesAno%></option>
                        <%
                          }
                        %>
                      </optgroup>
                    </select>
                  </div>
                </div>
                
                <div class="row">
                  <div class="form-group col-sm-12 col-md-12">
                    <span id="dataInclusao"><hl:message key="rotulo.extrato.filtro.periodo"/></span>
                    <div class="row" role="group" aria-labelledby="dataInclusao">
                      <div class="col-sm-6">
                        <div class="row">
                          <div class="form-check col-sm-2 col-md-2">
                            <div class="float-left align-middle mt-4 form-control-label">
                              <label for="periodoIni" class="labelSemNegrito"><hl:message key="rotulo.data.de"/></label>
                            </div>
                          </div>
                          <div class="form-check col-sm-12 col-md-12">
                            <hl:htmlinput name="periodoIni" di="periodoIni" type="text" classe="form-control w-100" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>" value="<%=TextHelper.forHtmlAttribute(periodoIni)%>"/>
                          </div>
                        </div>
                      </div>
                      <div class="col-sm-6">
                        <div class="row">
                          <div class="form-check col-sm-2 col-md-2">
                            <div class="float-left align-middle mt-4 form-control-label">
                              <label for="periodoFim" class="labelSemNegrito"><hl:message key="rotulo.data.ate"/></label>
                            </div>
                          </div>
                          <div class="form-check col-sm-12 col-md-12">
                            <hl:htmlinput name="periodoFim" di="periodoFim" type="text" classe="form-control w-100" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>" value="<%=TextHelper.forHtmlAttribute(periodoFim)%>"/>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                
                <div class="row">
                  <div class="form-group col-sm">
                    <label for="tocCodigo"><hl:message key="rotulo.consignacao.status"/></label>
                    <select class="form-control form-select select" id="tocCodigo" name="tocCodigo" onFocus="SetarEventoMascaraV4(this,'#*200',true);"  onBlur="fout(this);ValidaMascaraV4(this);" nf="Filtrar">
                      <optgroup label="<hl:message key="rotulo.extrato.filtro.situacao"/>:">
                        <option value="" <%=(String)(TextHelper.isNull(tocCodigoSelecionado) ? "SELECTED" : "")%> ><hl:message key="rotulo.campo.sem.filtro"/></option>
                        <%  
                          Iterator<TransferObject> tocIterator = lstTipoOcorrencia.iterator();
                          while (tocIterator.hasNext()) {
                              TransferObject tipoOcorrencia = tocIterator.next();
                              String tocCodigo = (String) tipoOcorrencia.getAttribute(Columns.TOC_CODIGO);
                        %>
                            <option value="<%=tocCodigo%>" <%= tocCodigoSelecionado != null && tocCodigo.equals(tocCodigoSelecionado) ? "SELECTED" : "" %>><%=tipoOcorrencia.getAttribute(Columns.TOC_DESCRICAO)%></option>
                        <%
                          }
                        %>
                      </optgroup>
                    </select>
                  </div>
                </div>
            </div>
          </div>
          <div class="btn-action">
            <button class="btn btn-primary">
              <svg width="20">
                <use xlink:href="#i-consultar"></use>
              </svg>
              <hl:message key="rotulo.acao.pesquisar"/>
            </button>
          </div>
        </div>
        
        <div class="col-sm-7 col-md-8">
          <div class="card">
            <div class="card-header hasIcon pl-3">
              <h2 class="card-header-title"><hl:message key="rotulo.consignacao.extrato"/></h2>
            </div>
            <div class="card-body table-responsive">
              <div class="alert alert-warning m-0" role="alert">
                <p class="mb-0"><hl:message key="rotulo.consignacao.total.vendas"/>: <span id="somaExtrato" class="font-weight-bold"><hl:message key="rotulo.moeda"/> <%=" " + totalTrans %></span></p>
              </div>
            </div>
            <div class="card-body table-responsive">
              <table class="table table-striped table-hover">
                <thead>
                  <tr>
                    <th scope="col"><hl:message key="rotulo.consignacao.numero.ade.abreviado"/></th>
                    <th scope="col"><hl:message key="rotulo.consignacao.data.e.hora"/></th>
                    <th scope="col"><hl:message key="rotulo.consignacao.valor.contrato"/></th>
                    <th scope="col"><hl:message key="rotulo.servidor.cpf"/></th>
                    <% if (responsavel.isCsa() && csaTemCorrespondentes) { %>
                          <th scope="col"><hl:message key="rotulo.correspondente.singular"/></th>
                    <% } %>
                    <th scope="col"><hl:message key="rotulo.acoes"/></th>
                  </tr>
                </thead>
                <tbody>
                <%
                  Iterator<TransferObject> transacoesIterator = transacoes.iterator();
                  while (transacoesIterator.hasNext()) {
                      TransferObject transacao = transacoesIterator.next();
                      
                      Long adeNumero = (Long) transacao.getAttribute(Columns.ADE_NUMERO);
                      String adeCodigo = (String) transacao.getAttribute(Columns.ADE_CODIGO);
                      Date ocaData = (Date) transacao.getAttribute(Columns.OCA_DATA);
                      String ocaDataFormatada = DateHelper.format(ocaData, LocaleHelper.getDateTimePattern());
                      BigDecimal adeVlr = (BigDecimal) transacao.getAttribute(Columns.ADE_VLR);
                      String adeVlrFormatado = ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel) + " " + NumberHelper.reformat((adeVlr).toString(), "en", NumberHelper.getLang());
                      String tocDescricao = (String) transacao.getAttribute(Columns.TOC_DESCRICAO);
                      String serCpf = (String) transacao.getAttribute(Columns.SER_CPF);
                      String cpfEscondido = TextHelper.escondeCpf(serCpf);
                      String sadCodigo = (String) transacao.getAttribute(Columns.SAD_CODIGO);
                      String svcCodigo = (String) transacao.getAttribute(Columns.SVC_CODIGO);
                      String tocCodigo = (String) transacao.getAttribute(Columns.TOC_CODIGO);
                      String correspondente = (String) transacao.getAttribute(Columns.COR_NOME);
                      
                      boolean valorNegativo = tocCodigo.equals(CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO) || tocCodigo.equals(CodedValues.TOC_TARIF_CANCELAMENTO_RESERVA);
                      
                      if (valorNegativo) {
                          adeVlrFormatado = "- " + adeVlrFormatado;
                      }
                %>
                  <tr>
                    <td><%=TextHelper.forHtml(adeNumero)%></td>
                    <td><%=TextHelper.forHtml(ocaDataFormatada)%></td>
                    <td><span class='<%= valorNegativo ? "rotulo-pendente" : ""%>'><%=TextHelper.forHtml(adeVlrFormatado)%></span></td>
                    <td><%=TextHelper.forHtml(cpfEscondido)%></td>
                    <% if (responsavel.isCsa() && csaTemCorrespondentes ) { %>
                          <td><%=!TextHelper.isNull(correspondente) ? TextHelper.forHtml(correspondente) : "" %></td>
                    <% } %> 
                    <td><a href="#no-back" onclick="javascript:detalharConsignacao('<%=adeNumero%>','<%=adeCodigo%>');" id="selecionaServidor" aria-label="<hl:message key="mensagem.consultar.consignacao.clique.aqui"/>"><hl:message key="rotulo.acoes.visualizar"/></a></td>
                  </tr>
                <%} %>
                </tbody>
                <tfoot>
                  <tr>
                    <td colspan="5">
                      <hl:message key="rotulo.lote.listagem.consignacoes"/>
                      <span class="font-italic"> - 
                        <hl:message key="rotulo.paginacao.registros.sem.estilo" arg0="${_paginacaoPrimeiro}" arg1="${_paginacaoUltimo}" arg2="${_paginacaoQtdTotal}"/>
                      </span>
                    </td>
                  </tr>
                </tfoot>
              </table>
            </div>
            <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
          </div>
        </div>
      </div>
    </form>
    <div class="btn-action">
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
    </div>
</c:set>

<c:set var="javascript">
  <script type="text/JavaScript">
    function detalharConsignacao(adeNumero, adeCodigo) {
  	  var link = '../v3/consultarConsignacao?acao=detalharConsignacao';
  	  link += '&ADE_NUMERO='+ adeNumero + '&tipo=consultar&ADE_CODIGO=' + adeCodigo + '&<%out.print(SynchronizerToken.generateToken4URL(request));%>';
  	  postData(link);
  	}
  </script>
</c:set>

<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
