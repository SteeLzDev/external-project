<%--
* <p>Title: transferirConsignacaoGeral_v4.jsp</p>
* <p>Description: Transferência geral de consignações </p>
* <p>Copyright: Copyright (c) 2002-2019</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: marlon.silva $
* $Revision: 27601 $
* $Date: 2019-08-16 14:10:27 -0300 (sex, 16 ago 2019) $
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject" %>
<%@ page import="com.zetra.econsig.dto.TransferObject" %>
<%@ page import="com.zetra.econsig.job.process.ProcessaRelatorioTransfereAde" %>
<%@ page import="org.json.simple.JSONObject" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String csaCodigoOrigem = request.getAttribute("csaCodigoOrigem") != null ? request.getAttribute("csaCodigoOrigem").toString() : "";
String csaCodigoDestino = request.getAttribute("csaCodigoDestino") != null ? request.getAttribute("csaCodigoDestino").toString() : "";
String svcCodigoOrigem = request.getAttribute("svcCodigoOrigem") != null ? request.getAttribute("svcCodigoOrigem").toString() : "";
String svcCodigoDestino = request.getAttribute("svcCodigoDestino") != null ? request.getAttribute("svcCodigoDestino").toString() : "";
String orgCodigo = request.getAttribute("orgCodigo") != null ? request.getAttribute("orgCodigo").toString() : "";

String periodoIni = request.getAttribute("periodoIni") != null ? request.getAttribute("periodoIni").toString() : "";
String periodoFim = request.getAttribute("periodoFim") != null ? request.getAttribute("periodoFim").toString() : "";

List<TransferObject> consignatarias = (List<TransferObject>) request.getAttribute("consignatarias");
List<TransferObject> orgaos = (List<TransferObject>) request.getAttribute("orgaos");
List<TransferObject> servicos = (List<TransferObject>) request.getAttribute("servicos");
%>
<c:set var="title">
    <hl:message key="rotulo.transf.contratos.titulo"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-sistema"></use>
</c:set>
<c:set var="bodyContent">
    <form name="form1" method="post" action="../v3/transferirConsignacaoGeral?acao=pesquisar&<%=SynchronizerToken.generateToken4URL(request)%>" enctype="multipart/form-data">
        <div class="card">
            <div class="card-header">
                <h2 class="card-header-title"><hl:message key="rotulo.dados.transferencia.titulo"/></h2>
            </div>
            <div class="card-body">
                <div class="row">
                      <div class="form-group col-sm-12 col-md-6">
                           <label for="periodo" class="labelSemNegrito"><hl:message key="rotulo.pesquisa.data.periodo"/></label>
                           <div class="row mt-2" role="group" aria-labelledby="periodo">
                            <div class="form-check pt-2 col-sm-12 col-md-1">
                                  <div class="float-left align-middle mt-4 form-control-label">
                                       <label for="periodoIni" class="labelSemNegrito"><hl:message key="rotulo.pesquisa.data.prefixo.inicio"/></label>
                                   </div>
                               </div>
                            <div class="form-check pt-2 col-sm-12 col-md-5">
                                   <hl:htmlinput name="periodoIni" di="periodoIni" type="text" classe="form-control" placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" value="<%=TextHelper.forHtmlAttribute(periodoIni)%>" />
                             </div>
                            <div class="form-check pt-2 col-sm-12 col-md-1">
                                <div class="float-left align-middle mt-4 form-control-label">
                                       <label for="periodoFim" class="labelSemNegrito"><hl:message key="rotulo.pesquisa.data.prefixo.fim"/></label>
                                   </div>
                               </div>
                            <div class="form-check pt-2 col-sm-12 col-md-5">
                                   <hl:htmlinput name="periodoFim" di="periodoFim" type="text" classe="form-control" placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" value="<%=TextHelper.forHtmlAttribute(periodoFim)%>" />
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="form-group col-sm-12 col-md-6">
                        <label for="csaCodigoOrigem"><hl:message key="rotulo.consignataria.singular"/> / <hl:message key="rotulo.transf.contratos.origem"/></label>
                        <%=JspHelper.geraCombo(consignatarias, "csaCodigoOrigem", Columns.CSA_CODIGO, Columns.CSA_NOME + ";" + Columns.CSA_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavel).toUpperCase(), "class=\"form-control\"", false, 1)%>
                    </div>
                    <div class="form-group col-sm-12 col-md-6">
                        <label for="csaCodigoDestino"><hl:message key="rotulo.consignataria.singular"/> / <hl:message key="rotulo.transf.contratos.destino"/></label>
                        <%=JspHelper.geraCombo(consignatarias, "csaCodigoDestino", Columns.CSA_CODIGO, Columns.CSA_NOME + ";" + Columns.CSA_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavel).toUpperCase(), "class=\"form-control\"", false, 1)%>
                      </div>
                </div>
                  <div class="row">
                    <div class="form-group col-sm-12 col-md-6">
                        <label for="svcCodigoOrigem"><hl:message key="rotulo.servico.singular"/> / <hl:message key="rotulo.transf.contratos.origem"/></label>
                        <%=JspHelper.geraCombo(servicos, "svcCodigoOrigem", Columns.SVC_CODIGO, Columns.SVC_DESCRICAO + ";" + Columns.SVC_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavel).toUpperCase(), "class=\"form-control\"", false, 1)%>
                    </div>
                    <div class="form-group col-sm-12 col-md-6">
                        <label for="svcCodigoDestino"><hl:message key="rotulo.servico.singular"/> / <hl:message key="rotulo.transf.contratos.destino"/></label>
                        <%=JspHelper.geraCombo(servicos, "svcCodigoDestino", Columns.SVC_CODIGO, Columns.SVC_DESCRICAO + ";" + Columns.SVC_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavel).toUpperCase(), "class=\"form-control\"", false, 1)%>
                      </div>
                </div>
                <div class="row">
                    <div class="form-group col-sm-12 col-md-6">
                        <label for="svcCodigoOrigem"><hl:message key="rotulo.orgao.singular"/></label>
                        <%=JspHelper.geraCombo(orgaos, "orgCodigo", Columns.ORG_CODIGO, Columns.ORG_NOME + ";" + Columns.ORG_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavel).toUpperCase(), "class=\"form-control\"", false, 1)%>
                    </div>
                </div>
                <div class="row mb-3">
                       <div class="col-sm-12 col-md-12">
                         <h3 class="legend">
                               <span id="situacaoContrato"><hl:message key="rotulo.consignacao.status"/></span>
                         </h3>
                         <hl:filtroStatusAdeTagv4/>
                    </div>
                </div>
                <div class="row">
                    <div class="form-group col-sm-12 col-md-6">
                        <label for="arquivo"><hl:message key="mensagem.transf.contratos.arquivo.lista.ade"/></label>
                        <input type="file" class="form-control" id="arquivo" name="FILE1">
                    </div>
                </div>
                <div class="row">
                    <div class="form-group col-sm-12 col-md-6">
                      <label for="ADE_NUMERO"><hl:message key="rotulo.consignacao.numero"/></label>
                      <hl:htmlinput name="ADE_NUMERO"
                          di="ADE_NUMERO"
                          type="text"
                          classe="form-control"
                          mask="#D20"
                          size="8"
                          placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.ade.numero", responsavel)%>'
                          value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "ADE_NUMERO"))%>" 
                        />
                    </div>
                    <div class="form-group col-sm-12 col-md-1 mt-4">
                        <a id="adicionaAdeLista" class="btn btn-primary w-50" href="javascript:void(0);" onClick="adicionaNumero()" aria-label='<hl:message key="mensagem.inserir.ade.numero.clique.aqui"/>'>
                            <svg width="15"><use xlink:href="../img/sprite.svg#i-avancar"></use></svg>
                        </a>
                        <a id="removeAdeLista" class="btn btn-primary w-50 mt-1" href="javascript:void(0);" onClick="removeNumero()" aria-label='<hl:message key="mensagem.remover.ade.numero.clique.aqui"/>' style="display: none">
                            <svg width="15"><use xlink:href="../img/sprite.svg#i-voltar"></use></svg>
                        </a>
                    </div>
                    <div id="adeLista" class="form-group col-sm-12 col-md-5 mt-4" style="display: none">
                        <select class="form-control w-100" id="ADE_NUMERO_LIST" name="ADE_NUMERO_LIST" multiple="multiple" size="6"></select>
                    </div>
                </div>
                <div class="row">
                    <div class="col-sm-12 col-md-6">
                        <%@ include file="../consultarMargem/include_campo_matricula_v4.jsp" %>
                    </div>
                </div>
                <div class="row">
                    <div class="form-group col-sm-12 col-md-6">
                        <hl:campoCPFv4 placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.cpf", responsavel)%>'/>
                    </div>
                </div>
            </div>
        </div>
    </form>
    <div class="btn-action">
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
        <a class="btn btn-primary" href="#no-back" onClick="validaSubmit(); return false;">
          <svg width="17">
            <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-consultar"></use>
          </svg><hl:message key="rotulo.botao.pesquisar"/>
        </a>
    </div>
</c:set>

<c:set var="javascript">
    <script type="text/JavaScript" src="../js/validaMascara_v4.js?<hl:message key="release.tag"/>"></script>
    <script type="text/JavaScript" src="../js/listutils.js?<hl:message key="release.tag"/>"></script>
    <script type="text/JavaScript" src="../js/listagem.js?<hl:message key="release.tag"/>"></script>
    <script type="text/JavaScript">
        var f0 = document.forms[0];
        function formLoad() {
          f0.periodoIni.focus();
        
          SelecionaComboMsg(f0.csaCodigoOrigem, '<%=TextHelper.forHtmlContent(csaCodigoOrigem)%>');
          SelecionaComboMsg(f0.csaCodigoDestino, '<%=TextHelper.forHtmlContent(csaCodigoDestino)%>');
          SelecionaComboMsg(f0.svcCodigoOrigem, '<%=TextHelper.forHtmlContent(svcCodigoOrigem)%>');
          SelecionaComboMsg(f0.svcCodigoDestino, '<%=TextHelper.forHtmlContent(svcCodigoDestino)%>');
          SelecionaComboMsg(f0.orgCodigo, '<%=TextHelper.forHtmlContent(orgCodigo)%>');
        }

        function adicionaNumero() {
            var ade = document.getElementById('ADE_NUMERO').value;

            if (ade != '' && (/\D/.test(ade) || ade.length > 20)) {
                alert('<hl:message key="mensagem.erro.ade.numero.invalido"/>');
                return;
            }
            
            if (document.getElementById('ADE_NUMERO').value != '') {
              document.getElementById('adeLista').style.display = '';
              document.getElementById('removeAdeLista').style.display = '';
              insereItem('ADE_NUMERO', 'ADE_NUMERO_LIST');
            }
        }

        function removeNumero() {
            removeDaLista('ADE_NUMERO_LIST');
            if (document.getElementById('ADE_NUMERO_LIST').length == 0) {
                document.getElementById('adeLista').style.display = 'none';
                document.getElementById('removeAdeLista').style.display = 'none';
            }
        }

        function vf_trasnf() {
          var controles = new Array("periodoIni", "periodoFim");
          var msgs = new Array ('<%=ApplicationResourcesHelper.getMessage("mensagem.informe.transf.contratos.inicio.periodo", responsavel)%>',
                                  '<%=ApplicationResourcesHelper.getMessage("mensagem.informe.transf.contratos.final.periodo", responsavel)%>');
        
          if (!ValidaCampos(controles, msgs)) {
            return false;
          }
        
          if ((f0.periodoIni != null) && (!verificaData(f0.periodoIni.value))) {
            f0.periodoIni.focus();
            return false;
          }
        
          if ((f0.periodoFim != null) && (!verificaData(f0.periodoFim.value))) {
            f0.periodoFim.focus();
            return false;
          }
        
          if (f0.periodoIni != null && f0.periodoFim != null) {
            var PartesData = new Array();
            PartesData = obtemPartesData(f0.periodoIni.value);
            var Dia = PartesData[0];
            var Mes = PartesData[1];
            var Ano = PartesData[2];
            PartesData = obtemPartesData(f0.periodoFim.value);
            var DiaFim = PartesData[0];
            var MesFim = PartesData[1];
            var AnoFim = PartesData[2];
        
            if (!VerificaPeriodoExt(Dia, Mes, Ano, DiaFim, MesFim, AnoFim, "")) {
              f0.periodoIni.focus();
              return false;
            }
            
            if (f0.csaCodigoDestino.value == '' && f0.svcCodigoDestino.value == '') {
              alert('<hl:message key="mensagem.erro.transf.contratos.csa.svc.destino.nulos"/>');
              return false;
            }
          }
        
          return true;
        }
        
        function validaSubmit() {
          if (!vf_trasnf() || (typeof vfRseMatricula === 'function' && !vfRseMatricula(true))) {
            return false;
          }
          selecionarTodosItens('ADE_NUMERO_LIST');
          f0.submit();
        }

        window.onload = formLoad;
    </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>