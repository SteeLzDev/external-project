<%--
* <p>Title: acompanhamento.jsp</p>
* <p>Description: Página de acompanhamento do processo de financiamento de dívida</p>
* <p>Copyright: Copyright (c) 2002-2014</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: alexandre $
* $Revision: 31521 $
* $Date: 2021-03-24 19:29:19 -0300 (qua, 24 mar 2021) $
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl"%>

<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
boolean filtroDataObrigatorio = (boolean) request.getAttribute("filtroDataObrigatorio");

String filtro = (String) request.getAttribute("filtro");

// Verifica as permissões do usuário para exibição dos ícones
boolean podeEdtSaldo = (boolean) request.getAttribute("podeEdtSaldo");
boolean podeEdtProposta = (boolean) request.getAttribute("podeEdtProposta");
boolean podeRenegociar = (boolean) request.getAttribute("podeRenegociar");
boolean podeComprar = (boolean) request.getAttribute("podeComprar");
boolean podeConsultarAde = (boolean) request.getAttribute("podeConsultarAde");

String filtroDataIni = (String) request.getAttribute("filtroDataIni");
String filtroDataFim = (String) request.getAttribute("filtroDataFim");

List<TransferObject> lstResultado = (List<TransferObject>) request.getAttribute("lstResultado");

// Colspan da coluna "Ações"
int colspan = (int) request.getAttribute("colspan");

%>
<c:set var="title">
  <hl:message key="rotulo.acompanhamento.financiamento.divida.cartao.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>

<c:set var="bodyContent">
<form action="../v3/acompanharFinanciamentoDivida?acao=iniciar" method="post" name="form1">
  <input type="hidden" name="pesquisar" value="true" />
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="mensagem.informe.opcoes.pesquisa"/></h2>
    </div>
    <div class="card-body">
      <div class="row">
        <div class=" col-md-12 form-check mt-2" role="radiogroup" aria-labelledby="radioOrigem">
          <div class="form-group my-0">
            <span id="radioOrigem"><hl:message key="rotulo.acompanhamento.origem"/></span>
          </div>
          <div class="form-check mt-2">
            <input class="form-check-input ml-1" type="radio" name="origem" id="origemEntidade" value="0" onClick="changeForm()" <%=!JspHelper.verificaVarQryStr(request, "origem").equals("1") ? "checked" : "" %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
            <label class="form-check-label pr-3" for="origemEntidade">
              <span class="text-nowrap align-text-top"><hl:message key="rotulo.acompanhamento.contratos.entidade"/></span>
            </label>
            <input class="form-check-input ml-1" type="radio" name="origem" id="origemTerceiros" value="1" onClick="changeForm()" <%= JspHelper.verificaVarQryStr(request, "origem").equals("1") ? "checked" : "" %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
            <label class="form-check-label" for="origemTerceiros">
              <span class="text-nowrap align-text-top"><hl:message key="rotulo.acompanhamento.contratos.terceiros"/></span>
            </label>
          </div>
        </div>
      </div>
      <div class="row">
        <div class=" col-md-12 form-check mt-2" role="radiogroup" aria-labelledby="iPermiteUtilizarPrazoNaoCadastradoServico">
          <div class="form-group my-0">
            <span id="iPermiteUtilizarPrazoNaoCadastradoServico"><hl:message key="rotulo.acompanhamento.tipo.filtro"/></span>
          </div>
          <div class="form-check mt-2">
            <div class="row" id="filtroOrigem0">
              <div class = "col-12">
                <input class="form-check-input ml-1" type="radio" name="filtro" id="filtroSaldoProposta" value="0" <%=(String)(filtro.equals("0") ? "checked" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                <label class="form-check-label pr-3" for="filtroSaldoProposta">
                  <span class="text-nowrap align-text-top"><hl:message key="mensagem.acompanhamento.pendentes.informacao.saldo.proposta"/></span>
                </label>
              </div>
              <div class = "col-12">
                <input class="form-check-input ml-1" type="radio" name="filtro" id="filtroAguardandoAprovacao" value="1" <%=(String)(filtro.equals("1") ? "checked" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                <label class="form-check-label" for="filtroAguardandoAprovacao">
                  <span class="text-nowrap align-text-top"><hl:message key="mensagem.acompanhamento.saldo.informado.poposta.ofertada.aguardando.aprovacao"/></span>
                </label>
              </div>
              <div class = "col-12">
                <input class="form-check-input ml-1" type="radio" name="filtro" id="filtroAguardandoRenegociacao" value="2" <%=(String)(filtro.equals("2") ? "checked" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                <label class="form-check-label pr-3" for="filtroAguardandoRenegociacao">
                  <span class="text-nowrap align-text-top"><hl:message key="mensagem.acompanhamento.aguardando.renegociacao"/></span>
                </label>
              </div>
            </div>
            <div class="row d-none" id="filtroOrigem1">
              <div class = "col-12">
                <input class="form-check-input ml-1" type="radio" name="filtro" id="filtroInfoProposta" value="3" <%=(String)(filtro.equals("3") ? "checked" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                <label class="form-check-label pr-3" for="filtroInfoProposta">
                  <span class="text-nowrap align-text-top"><hl:message key="mensagem.acompanhamento.pendentes.informacao.proposta"/></span>
                </label>
              </div>
              <div class = "col-12">
                <input class="form-check-input ml-1" type="radio" name="filtro" id="filtroPropostaAguardandoAprovacao" value="4" <%=(String)(filtro.equals("4") ? "checked" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                <label class="form-check-label" for="filtroPropostaAguardandoAprovacao">
                  <span class="text-nowrap align-text-top"><hl:message key="mensagem.acompanhamento.poposta.ofertada.aguardando.aprovacao"/></span>
                </label>
              </div>
              <div class = "col-12">
                <input class="form-check-input ml-1" type="radio" name="filtro" id="filtroAguardandoProcesoCompra" value="5" <%=(String)(filtro.equals("5") ? "checked" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                <label class="form-check-label pr-3" for="filtroAguardandoProcesoCompra">
                  <span class="text-nowrap align-text-top"><hl:message key="rotulo.propostas.ofertadas.aceitas.aguardando.processo.compra"/></span>
                </label>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="row">
        <div class="form-group col-sm-12 col-md-6 cep-input">
          <span id="dataInclusao"><hl:message key="rotulo.acompanhamento.data.solicitacao"/></span>
          <div class="row" role="group" aria-labelledby="dataInclusao">
            <div class="form-group col-sm-12 col-md-1">
              <div class="float-left align-middle mt-4 form-control-label">
                <span><hl:message key="rotulo.data.de"/></span>
              </div>
            </div>
            <div class="form-group col-sm-12 col-md-5">
              <hl:htmlinput name="periodoIni" di="periodoIni" type="text" classe="form-control" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" value="<%=TextHelper.forHtmlAttribute(filtroDataIni)%>" />
            </div>
            <div class="form-group col-sm-12 col-md-1">
              <div class="float-left align-middle mt-4 form-control-label">
                <span><hl:message key="rotulo.data.ate"/></span>
              </div>
            </div>
            <div class="form-group col-sm-12 col-md-5">
              <hl:htmlinput name="periodoFim" di="periodoFim" type="text" classe="form-control" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" value="<%=TextHelper.forHtmlAttribute(filtroDataFim)%>" />
            </div>
          </div>
        </div>
      </div>
      <div class="row">
        <div class="form-group col-sm-12  col-md-6">
          <label for="diasSemSaldoDevedor"><hl:message key="rotulo.consignacao.numero"/></label>
          <hl:htmlinput name="ADE_NUMERO" 
                        di="ADE_NUMERO" 
                        type="text" 
                        classe="form-control"
                        mask="#D20" 
                        size="8"
                        value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "ADE_NUMERO"))%>" 
          />
        </div>
      </div>
      <div class="row">
        <div class="form-group col-sm">
          <%-- Inclui o campo de matrícula --%>
          <hl:campoMatriculav4
            placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.matricula", responsavel)%>' />
        </div>
      </div>
      <div class="row">
        <div class="form-group col-sm">
          <hl:campoCPFv4 nf="btnEnvia" />
        </div>
      </div>
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" id="btnVoltar" href="#no-back" onClick="postData('../v3/carregarPrincipal')"><hl:message key="rotulo.botao.voltar"/></a>
    <a class="btn btn-primary" id="btnEnvia"  href="#no-back" onClick="validaSubmit()"><svg width="20"><use xlink:href="../img/sprite.svg#i-consultar"></use></svg><hl:message key="rotulo.botao.pesquisar"/></a>
  </div>
  <% if (lstResultado != null) { %>
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.acompanhamento.resultado.pesquisa"/></h2>
    </div>
    <div class="card-body table-responsive  p-0">
      <table class="table table-striped table-hover" id="tabelaRegistrosAuditoria">
        <thead>
          <tr>
            <th class="colunaUnica" scope="col" title="Selecione múltiplos registros" style="display: none;" width="10%">
              <div class="form-check">
                <input type="checkbox" class="form-check-input ml-0" name="checkAll" id="checkAll">
              </div>
            </th>
            <th scope="col"><hl:message key="rotulo.acompanhamento.data.solicitacao"/></th>
            <th scope="col"><hl:message key="rotulo.consignataria.singular"/></th>
            <th scope="col"><hl:message key="rotulo.consignacao.numero"/></th>
            <th scope="col"><hl:message key="rotulo.servidor.singular"/></th>
            <th scope="col"><hl:message key="rotulo.acompanhamento.valor.prestacao.abreviado"/></th>
            <th scope="col"><hl:message key="rotulo.acompanhamento.valor.saldo.abreviado"/></th>
            <th scope="col"><hl:message key="rotulo.acompanhamento.valor.com.desconto"/></th>
            <% if (colspan > 0) { %>
            <th scope="col"><hl:message key="rotulo.acoes"/></th>
             <% } %>
          </tr>
        </thead>
        <tbody>
          <%=JspHelper.msgRstVazio(lstResultado.isEmpty(), "15", "lp")%>
          <% 
          String adeCodigo, adeNumero, adeTipoVlr, adeVlr;
          String soaData, sdvValor, sdvValorComDesconto;
          String servidor, consignataria;
          String svcDestino;
          TransferObject resultado = null;
          Iterator it = lstResultado.iterator();
          while (it.hasNext()) {
            resultado = (TransferObject) it.next();
          
            adeCodigo = resultado.getAttribute(Columns.ADE_CODIGO).toString();
            adeNumero = resultado.getAttribute(Columns.ADE_NUMERO).toString();
            adeTipoVlr = (String) resultado.getAttribute(Columns.ADE_TIPO_VLR);
            adeVlr = NumberHelper.format(((BigDecimal) resultado.getAttribute(Columns.ADE_VLR)).doubleValue(), NumberHelper.getLang());
          
            if (resultado.getAttribute(Columns.SDV_VALOR) != null) {
              sdvValor = NumberHelper.format(((BigDecimal) resultado.getAttribute(Columns.SDV_VALOR)).doubleValue(), NumberHelper.getLang());
            } else {
              sdvValor = ""; 
            }
          
            if (resultado.getAttribute(Columns.SDV_VALOR_COM_DESCONTO) != null) {
              sdvValorComDesconto = NumberHelper.format(((BigDecimal) resultado.getAttribute(Columns.SDV_VALOR_COM_DESCONTO)).doubleValue(), NumberHelper.getLang());
            } else {
              sdvValorComDesconto = ""; 
            }
          
            soaData = DateHelper.reformat(resultado.getAttribute(Columns.SOA_DATA).toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern());
            
            servidor = resultado.getAttribute(Columns.RSE_MATRICULA) + " - " + resultado.getAttribute(Columns.SER_CPF) + " - " + resultado.getAttribute(Columns.SER_NOME);
            
            consignataria = (String) resultado.getAttribute(Columns.CSA_NOME_ABREV);
            if (consignataria == null || consignataria.trim().length() == 0) {
              consignataria = resultado.getAttribute(Columns.CSA_NOME).toString();
            }
            consignataria = resultado.getAttribute(Columns.CSA_IDENTIFICADOR) + " - " + consignataria;
            
            svcDestino = resultado.getAttribute(Columns.RSV_SVC_CODIGO_DESTINO).toString();
          %>
          <tr>
            <td><%=TextHelper.forHtmlContent(soaData)%></td>
            <td><%=TextHelper.forHtmlContent(consignataria)%></td>
            <td><%=TextHelper.forHtmlContent(adeNumero)%></td>
            <td><%=TextHelper.forHtmlContent(servidor)%></td>
            <td><%=TextHelper.forHtmlContent(ParamSvcTO.getDescricaoTpsTipoVlr(adeTipoVlr))%><%=TextHelper.forHtmlContent(adeVlr)%></td>
            <td><hl:message key="rotulo.moeda"/><%=TextHelper.forHtmlContent(sdvValor)%></td>
            <td><hl:message key="rotulo.moeda"/><%=TextHelper.forHtmlContent(sdvValorComDesconto)%></td>
            <% if (colspan > 0) { %>
            <td class="acoes">
               <div class="actions">
                <div class="dropdown">
                  <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <div class="form-inline">
                      <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<%=ApplicationResourcesHelper.getMessage("rotulo.acoes", responsavel)%>" aria-label="<%=ApplicationResourcesHelper.getMessage("rotulo.fila.op.sensiveis.ver.detalhes", responsavel)%>"> <svg>
                          <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                      </span> <hl:message key="rotulo.botao.opcoes"/>
                    </div>
                  </a>
                  <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                     <% if ((filtro.equals("0") || filtro.equals("1")) && podeEdtSaldo) { %>
                     <a class="dropdown-item" href="#" onClick="doIt('sdv', '<%=TextHelper.forJavaScript(adeCodigo)%>', '<%=TextHelper.forJavaScript(svcDestino)%>');"><hl:message key="rotulo.acoes.saldo"/></a>
                     <% } %>
                     <% if ((filtro.equals("1") || filtro.equals("2")) && podeRenegociar) { %>
                     <a class="dropdown-item" href="#" onClick="doIt('ren', '<%=TextHelper.forJavaScript(adeCodigo)%>', '<%=TextHelper.forJavaScript(svcDestino)%>');"><hl:message key="rotulo.acoes.finalizar"/></a>                         
                     <% } %>
                     <% if ((filtro.equals("3") || filtro.equals("4")) && podeEdtProposta) { %>
                     <a class="dropdown-item" href="#" onClick="doIt('ppd', '<%=TextHelper.forJavaScript(adeCodigo)%>', '<%=TextHelper.forJavaScript(svcDestino)%>');"><hl:message key="rotulo.acoes.propostas"/></a>
                     <% } %>
                     <% if ((filtro.equals("4") || filtro.equals("5")) && podeComprar) { %>
                     <a class="dropdown-item" href="#" onClick="doIt('com', '<%=TextHelper.forJavaScript(adeCodigo)%>', '<%=TextHelper.forJavaScript(svcDestino)%>');"><hl:message key="rotulo.acoes.finalizar"/></a>
                     <% } %>
                     <% if ((filtro.equals("0") || filtro.equals("1") || filtro.equals("2")) && podeConsultarAde) { %>
                     <a class="dropdown-item" href="#" onClick="doIt('e', '<%=TextHelper.forJavaScript(adeCodigo)%>', '<%=TextHelper.forJavaScript(svcDestino)%>');"><hl:message key="rotulo.acoes.consultar"/></a>                  
                     <% } %>
                  </div>
                </div>
               </div>   
            </td> 
            <% } %>
          </tr>
          <% } %>
        </tbody>
        <tfoot>
          <tr>
            <td colspan="8">
              <hl:message key="rotulo.listagem.operacoes.fila.autorizacao"/> - 
              <span class="font-italic"> <hl:message key="rotulo.paginacao.registros.sem.estilo" arg0="${_paginacaoPrimeiro}" arg1="${_paginacaoUltimo}" arg2="${_paginacaoQtdTotal}"/></span>
            </td>
          </tr>
       </tfoot>
      </table>
    </div>
  </div>
  <% } %>
</form>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
    var f0 = document.forms[0];

    window.onload = formLoad();
    
    function formLoad() {
      changeForm();
    }

    // ajusta as opções do formulário de acordo as opções já marcadas
    function changeForm() {
      with(f0) {
        if (getCheckedRadio('form1', 'origem') == '0') {
            document.getElementById('filtroOrigem0').classList.remove('d-none');
            document.getElementById('filtroOrigem1').classList.add('d-none');
            var vlrFiltro = getCheckedRadio('form1', 'filtro');
            if (vlrFiltro == null || vlrFiltro == '3' || vlrFiltro == '4' || vlrFiltro == '5') {
              setCheckedRadio('form1', 'filtro', '0');
            }
        } else if (getCheckedRadio('form1', 'origem') == '1') {
            document.getElementById('filtroOrigem0').classList.add('d-none');
            document.getElementById('filtroOrigem1').classList.remove('d-none');
            var vlrFiltro = getCheckedRadio('form1', 'filtro');
            if (vlrFiltro == null || vlrFiltro == '0' || vlrFiltro == '1' || vlrFiltro == '2') {
              setCheckedRadio('form1', 'filtro', '3');
            }
        }

        if (document.forms[0].CSA_CODIGO != null) {
          if (CSA_CODIGO.selectedIndex == 0) {
            origem[0].disabled = true;
            origem[1].disabled = true;
          }
        }
      }
    }

    // valida o formulário antes do envio do submit
    function validForm() {
      with(f0) {
        if (getCheckedRadio('form1', 'filtro') == null) {
            alert('<hl:message key="mensagem.informe.filtro"/>');
            filtro.focus();
            return false;
        }
        <% if (filtroDataObrigatorio) { %>
        if (!periodoIni.disabled && !periodoFim.disabled) {
          if (periodoIni.value == '' || periodoFim.value == '') {
            alert('<hl:message key="mensagem.confirmacao.informe.data.solicitacao"/>');
            if (periodoIni.value == '') {
              periodoIni.focus();
            } else {
              periodoFim.focus();
            }
            return false;
          } else {
            if (!verificaData(periodoIni.value)) {
              periodoIni.focus();
              return false;
            }
            if (!verificaData(periodoFim.value)) {
              periodoFim.focus();
              return false;
            }

            // valida se as datas estão preenchidas corretamente
            var PartesData = new Array();
            PartesData = obtemPartesData(f0.periodoIni.value);
            var DiaIni = PartesData[0];
            var MesIni = PartesData[1];
            var AnoIni = PartesData[2];
            PartesData = obtemPartesData(f0.periodoFim.value);
            var DiaFim = PartesData[0];
            var MesFim = PartesData[1];
            var AnoFim = PartesData[2];
            if (!VerificaPeriodoExt(DiaIni, MesIni, AnoIni, DiaFim, MesFim, AnoFim, 30)) {
              periodoIni.focus();
              return false;
            }
          }
        }
        <% } %>
      }
      return true;
    }
    
    function validaSubmit()
    {
        if( validForm() )
        { 
          if(typeof vfRseMatricula === 'function')
          {
            if(vfRseMatricula(true))
            {
              f0.submit();
            }
          }
          else
          {
            f0.submit();
          } 
        }
    }


    function doIt(opt, ade, svc) {
      var qs = '&ADE_CODIGO=' + ade + '&SVC_CODIGO=' + svc + '&<%SynchronizerToken.saveToken(request);out.print(SynchronizerToken.generateToken4URL(request));%>';
      var msg = '';
      var link = '';
      switch (opt) {
        case 'e':
            link = '../v3/consultarConsignacao?acao=detalharConsignacao';
            break;
        case 'sdv':
            link = '../v3/editarSaldoDevedorSolicitacao?acao=iniciar&tipo=solicitacao_saldo';
            break;  
        case 'ppd':
            link = '../v3/acompanharFinanciamentoDivida?acao=editar';
            break;
        case 'ren':
            link = '../v3/renegociarConsignacao?acao=renegociarConsignacao';
            break;    
        case 'com':
            link = '../v3/comprarConsignacao?acao=comprarConsignacao';
            break;    
        default:
            return false;
            break;  
      } 

      if (msg == "" || confirm(msg)) {
        postData(link + qs);
      }
    }
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="pageModals">${pageModals}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>