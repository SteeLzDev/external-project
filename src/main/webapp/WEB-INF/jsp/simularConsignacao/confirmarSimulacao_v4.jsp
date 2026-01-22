<%--
* <p>Title: simulacao</p>
* <p>Description: Página de resultado da simulação de empréstimos</p>
* <p>Copyright: Copyright (c) 2007</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.zetra.econsig.helper.criptografia.RSA"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ page import="com.zetra.econsig.dto.TransferObject" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession"/>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
java.security.KeyPair keyPair = (java.security.KeyPair) request.getAttribute("keyPair");

boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);
boolean exigeTelefone = (Boolean) request.getAttribute("exigeTelefone");
boolean exigeMunicipioLotacao = (Boolean) request.getAttribute("exigeMunicipioLotacao");
boolean campoCidadeObrigatorio = (Boolean) request.getAttribute("campoCidadeObrigatorio");
boolean exigeCodAutSolicitacao = (Boolean) request.getAttribute("exigeCodAutSolicitacao");
boolean simulacaoMetodoMexicano = (Boolean) request.getAttribute("simulacaoMetodoMexicano");
boolean simulacaoMetodoBrasileiro = (Boolean) request.getAttribute("simulacaoMetodoBrasileiro");
boolean leilaoReverso = (request.getAttribute("leilaoReverso") != null && (Boolean) request.getAttribute("leilaoReverso"));
boolean quinzenal = (Boolean) request.getAttribute("quinzenal");
boolean temBloqueioLeilao = (request.getAttribute("temBloqueioLeilao") != null && (Boolean) request.getAttribute("temBloqueioLeilao"));
int qtdMaximaArquivos = (int) request.getAttribute("qtdMaximaArquivos");
String csaNome = (String) request.getAttribute("csaNome");
String vlrLiberado = (String) request.getAttribute("vlrLiberado");
String adeVlr = (String) request.getAttribute("adeVlr");
String przVlr = (String) request.getAttribute("przVlr");
int carenciaMinPermitida = (Integer) request.getAttribute("carenciaMinPermitida");
String dataIni = (String) request.getAttribute("dataIni");
String dataFim = (String) request.getAttribute("dataFim");
boolean exigeAssinaturaDigital = (Boolean) request.getAttribute("exigeAssinaturaDigital");
String serCodigo = (String) request.getAttribute("serCodigo");
String svcDescricao = (String) request.getAttribute("svcDescricao");
String rseCodigo = (String) request.getAttribute("rseCodigo");
String csaCodigo = (String) request.getAttribute("csaCodigo");
String svcCodigo = (String) request.getAttribute("svcCodigo");
String svcCodigoOrigem = (String) request.getAttribute("svcCodigoOrigem");
String svcIdentificador = (String) request.getAttribute("svcIdentificador");
String orgCodigo = (String) request.getAttribute("orgCodigo");
List lstCorrespondentes = (List) request.getAttribute("lstCorrespondentes");
List<TransferObject> tdaList = (List<TransferObject>) request.getAttribute("tdaList");
String csaIdentificador = (String) request.getAttribute("csaIdentificador");
String cftCodigo = (String) request.getAttribute("cftCodigo");
String dtjCodigo = (String) request.getAttribute("dtjCodigo");
String cftVlr = (String) request.getAttribute("cftVlr");
String adeVlrIva = (String) request.getAttribute("adeVlrIva");
String ade_vlr_cat = (String) request.getAttribute("ade_vlr_cat");
String adeVlrTac = (String) request.getAttribute("adeVlrTac");
String adeVlrIof = (String) request.getAttribute("adeVlrIof");
String adePeriodicidade = (String) request.getAttribute("adePeriodicidade");
String ranking = !TextHelper.isNull(request.getAttribute("RANKING")) ? request.getAttribute("RANKING").toString() : JspHelper.verificaVarQryStr(request, "RANKING");
String cftDia = !TextHelper.isNull(request.getAttribute("CFT_DIA")) ? request.getAttribute("CFT_DIA").toString() : JspHelper.verificaVarQryStr(request, "CFT_DIA");
String mensagemTermoAceite = (String) request.getAttribute("mensagemTermoAceite");
String dataPrevistaEncerramentoLeilao = (String) request.getAttribute("dataPrevistaEncerramentoLeilao");
TransferObject autdes = (TransferObject) request.getAttribute("autdes");
String msgDadosSerNaoPermitemAlteracao = ApplicationResourcesHelper.getMessage("mensagem.dados.servidor.nao.permitem.alteracao", responsavel);
boolean simulacaoAdeVlr = (Boolean) request.getAttribute("SIMULACAO_POR_ADE_VLR");
String exigenciaConfirmacaoLeitura = (String) request.getAttribute("exigenciaConfirmacaoLeitura");
Boolean serSenhaObrigatoria =  !TextHelper.isNull(request.getAttribute("serSenhaObrigatoria")) ? (Boolean) request.getAttribute("serSenhaObrigatoria") : false;
String mensagemSolicitacaoOutroSvc = (String) request.getAttribute("mensagemSolicitacaoOutroSvc");
String nomeOutroSvc = (String) request.getAttribute("nomeOutroSvc");
String novoCftCodigo = (String) request.getAttribute("novoCftCodigo");
String termoConsentimentoDadosServidor = (String) request.getAttribute("termoConsentimentoDadosServidor");
boolean enderecoObrigatorio = request.getAttribute("enderecoObrigatorio") != null && request.getAttribute("enderecoObrigatorio").toString().equals("true");
boolean celularObrigatorio = request.getAttribute("celularObrigatorio") != null && request.getAttribute("celularObrigatorio").toString().equals("true");
boolean enderecoCelularObrigatorio = request.getAttribute("enderecoCelularObrigatorio") != null && request.getAttribute("enderecoCelularObrigatorio").toString().equals("true");
String acaoFormulario = request.getAttribute("acaoFormulario").toString();
TransferObject adePortabilidade = (TransferObject) request.getAttribute("adePortabilidade");
boolean reconhecimentoFacialServidorSimulacao = request.getAttribute("exigeReconhecimentoFacil") != null && request.getAttribute("exigeReconhecimentoFacil").equals("true");
HashMap<String, TransferObject> hashCsaPermiteContato = (HashMap<String, TransferObject>) request.getAttribute("hashCsaPermiteContato"); 
String csa_whatsapp = "", csa_email_contato ="", csa_email = "", csa_email_usar = "", csa_contato_tel = "", tipo_contato = "";
if(hashCsaPermiteContato.get(csaCodigo) != null){
    TransferObject consignatariaContato = hashCsaPermiteContato.get(csaCodigo);
    csa_whatsapp = (String) consignatariaContato.getAttribute(Columns.CSA_WHATSAPP);
    csa_email_contato = (String) consignatariaContato.getAttribute(Columns.CSA_EMAIL_CONTATO);
    csa_email = (String) consignatariaContato.getAttribute(Columns.CSA_EMAIL);
    tipo_contato = (String) consignatariaContato.getAttribute(Columns.PCS_VLR);
    csa_email_usar = !TextHelper.isNull(csa_email_contato) ? csa_email_contato : csa_email;
    if(!TextHelper.isNull(csa_whatsapp)){
        csa_whatsapp = LocaleHelper.formataCelular(csa_whatsapp);
    }
    csa_contato_tel = (String) consignatariaContato.getAttribute(Columns.CSA_TEL);
}
boolean anexoObrigatorio = request.getAttribute("anexoObrigatorio") != null && (boolean) request.getAttribute("anexoObrigatorio");
String qtdeMinAnexos = (String) request.getAttribute("qtdeMinAnexos");
boolean exigeAnexoServidor = anexoObrigatorio && !TextHelper.isNull(qtdeMinAnexos) && Integer.valueOf(qtdeMinAnexos) > 0 && ParamSist.paramEquals(CodedValues.TPC_PERMITE_ANEXO_INCLUSAO_CONTRATOS, CodedValues.TPC_SIM, responsavel) && responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO);
boolean tpcSolicitarPortabilidadeRanking  = request.getAttribute("tpcSolicitarPortabilidadeRanking") != null && (boolean) request.getAttribute("tpcSolicitarPortabilidadeRanking");
boolean simularConsignacaoComReconhecimentoFacialELiveness = request.getAttribute("simularConsignacaoComReconhecimentoFacialELiveness") != null && request.getAttribute("simularConsignacaoComReconhecimentoFacialELiveness").equals("true");
boolean vlrLiberadoOk = (Boolean) request.getAttribute("vlrLiberadoOk");
%>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="title">
   <%= TextHelper.forHtml(request.getAttribute("tituloPagina")) %>
</c:set>
<c:set var="bodyContent">
<form method="POST" action="<%=tpcSolicitarPortabilidadeRanking ? "../v3/renegociarConsignacao?acao=incluirReserva&" : acaoFormulario + "?acao=emitirBoleto&"%><%=reconhecimentoFacialServidorSimulacao ? "reconhecimentoFacial=true&" : ""%><%=SynchronizerToken.generateToken4URL(request)%>" name="form1" ENCTYPE="multipart/form-data">
    <%if (reconhecimentoFacialServidorSimulacao && responsavel.isSer()) {%>
          <input type="hidden" id="reconhecimentoFacialServidorSimulacao" name="reconhecimentoFacialServidorSimulacao" value="<%=reconhecimentoFacialServidorSimulacao%>">
    <%} %>
    <% if (exigeCodAutSolicitacao) { %>
    <div class="alert alert-info" role="alert">
      <p class="mb-0">
        <%=ApplicationResourcesHelper.getMessage("mensagem.preenchimento.email.codigo.autorizacao", responsavel)%>
      </p>
    </div>
    <% } %>
	<% if (!vlrLiberadoOk) { %>
	    <div class="alert alert-warning mb-1" role="alert">
	      <p class="mb-0">
	        <% String simulacao = ApplicationResourcesHelper.getMessage("rotulo.consignacao.origem.simulacao", responsavel);%>
			<%=ApplicationResourcesHelper.getMessage("mensagem.alerta.alteracao.simulacao", responsavel, simulacao.toLowerCase())%>
	      </p>
	    </div>
    <% } %>
    <div class="row">
        <div class="col-sm-4">
            <div class="card">
                <div class="card-header">
                    <h2 class="card-header-title"><hl:message key="rotulo.confirmar.dados.titulo"/></h2>
                </div>
                <div class="card-body">
                    <dl class="row data-list">
                        <% if (!leilaoReverso) { %>
                        <dt class="col-6"><hl:message key="rotulo.consignataria.singular"/>:</dt> <dd class="col-6"><%=TextHelper.forHtmlContent(csaNome)%></dd>
                        <% } %>
                        <dt class="col-6"><hl:message key="rotulo.consignacao.data"/>:</dt> <dd class="col-6"><%=DateHelper.toDateString(DateHelper.getSystemDatetime())%></dd>
                        <dt class="col-6"><hl:message key="rotulo.consignacao.valor.liberado"/>:</dt> <dd class="col-6"><hl:message key="rotulo.moeda"/> <%=TextHelper.forHtmlContent(vlrLiberado)%></dd>
                        <dt class="col-6"><hl:message key="rotulo.consignacao.valor.parcela.moeda"/>:</dt> <dd class="col-6"><hl:message key="rotulo.moeda"/> <%=NumberHelper.reformat(adeVlr, "en", NumberHelper.getLang())%></dd>
                        <dt class="col-6"><hl:message key="rotulo.consignacao.prazo"/>:</dt> <dd class="col-6"><%=TextHelper.forHtmlContent(przVlr)%></dd>
                        <dt class="col-6"><%= ApplicationResourcesHelper.getMessage((temCET ? "rotulo.consignacao.cet" : "rotulo.consignacao.taxa.juros"), responsavel) %>:</dt> <dd class="col-6"><%=TextHelper.forHtmlContent(cftVlr)%></dd>
                        <% if (!leilaoReverso && ShowFieldHelper.showField(FieldKeysConstants.RESERVAR_MARGEM_CARENCIA, responsavel) ) { %>
                        <dt class="col-6"><hl:message key="rotulo.consignacao.carencia"/>:</dt> <dd class="col-6"><%=String.valueOf(carenciaMinPermitida)%></dd>
                        <%}%> 
                        <dt class="col-6"><hl:message key="rotulo.consignacao.data.inicial"/>:</dt> <dd class="col-6"><%=TextHelper.forHtmlContent(dataIni)%></dd>
                        <dt class="col-6"><hl:message key="rotulo.consignacao.data.final"/>:</dt> <dd class="col-6"><%=TextHelper.forHtmlContent(TextHelper.isNull(dataFim) ? ApplicationResourcesHelper.getMessage("rotulo.indeterminado", responsavel) : dataFim)%></dd>
                        <% if (!leilaoReverso) { %>
                        <dt class="col-6"><hl:message key="rotulo.servico.singular"/>:</dt> <dd class="col-6"><%=TextHelper.forHtmlContent(svcDescricao)%></dd>
                        <% } %>
                        <% if (simulacaoMetodoMexicano) { %>
                        <dt class="col-6"><hl:message key="rotulo.consignacao.periodicidade"/>:</dt> <dd class="col-6"><hl:message key="<%=quinzenal ? "rotulo.consignacao.periodicidade.quinzenal" : "rotulo.consignacao.periodicidade.mensal" %>"/></dd>
                        <% } %>    
                          <% if (!leilaoReverso && lstCorrespondentes != null && lstCorrespondentes.size() > 0) { %>
                        <dt class="col-6"><hl:message key="rotulo.correspondente.singular"/> <hl:message key="rotulo.campo.opcional"/>:</dt> <dd class="col-6"><%=JspHelper.geraCombo(lstCorrespondentes, "COR_CODIGO", Columns.COR_CODIGO, Columns.COR_NOME + ";" + Columns.COR_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, null, null, false, "form-control")%></dd>
                        <% } %>
                        <% if (leilaoReverso) { %>
                        <dt class="col-6"><hl:message key="rotulo.leilao.solicitacao.prazo.encerramento.previsto"/>:</dt> <dd class="col-6"><%=TextHelper.forHtmlContent(dataPrevistaEncerramentoLeilao)%></dd>
                        <% } %>
                        <%if (!TextHelper.isNull(tipo_contato) && hashCsaPermiteContato.get(csaCodigo) != null){ %>
                          <dt class="col-6"><hl:message key="rotulo.consignataria.contato.qr.code"/>:</dt>
                          <dd class="col-6">
                              <%if (!TextHelper.isNull(csa_whatsapp) && CodedValues.TPA_CONTATOS_WHATSAPP.contains(tipo_contato)){ %>
                                  <i class="fa fa-whatsapp icon-contato-ranking" onclick="openModalQRCode(1,'<%=csa_whatsapp%>')"></i>
                              <%} %>
                              <%if (!TextHelper.isNull(csa_email_usar) && CodedValues.TPA_CONTATOS_EMAIL.contains(tipo_contato)){ %>
                                  <i class="fa fa-at icon-contato-ranking" onclick="openModalQRCode(2,'<%=csa_email_usar%>')"></i>
                              <%} %>
                              <%if (!TextHelper.isNull(csa_contato_tel) && CodedValues.TPA_CONTATOS_TELEFONE.contains(tipo_contato)){ %>
                                  <i class="fa fa-phone icon-contato-ranking" onclick="openModalQRCode(3,'<%=csa_contato_tel%>')"></i>                              
                              <%} %>                
                          </dd>
                        <% } %>
                    </dl>
                </div>
            </div>

            <%-- Se é solicitação de portabilidade, exibe o detalhe da consignação a ser transferida --%>
            <% if (adePortabilidade != null) { %>
            <div class="card">
                <div class="card-header">
                    <h2 class="card-header-title"><hl:message key="mensagem.solicitar.portabilidade.consignacao.titulo"/></h2>
                </div>
                <div class="card-body">
                    <dl class="row data-list">
                        <dt class="col-6"><hl:message key="rotulo.consignataria.singular"/>:</dt> <dd class="col-6"><%=TextHelper.forHtmlContent(adePortabilidade.getAttribute(Columns.CSA_NOME))%></dd>
                        <dt class="col-6"><hl:message key="rotulo.servico.singular"/>:</dt> <dd class="col-6"><%=TextHelper.forHtmlContent(adePortabilidade.getAttribute(Columns.SVC_DESCRICAO))%></dd>
                        <dt class="col-6"><hl:message key="rotulo.consignacao.numero"/>:</dt> <dd class="col-6"><%=TextHelper.forHtmlContent(adePortabilidade.getAttribute(Columns.ADE_NUMERO))%></dd>
                        <dt class="col-6"><hl:message key="rotulo.consignacao.data"/>:</dt> <dd class="col-6"><%=DateHelper.toDateString((Date) adePortabilidade.getAttribute(Columns.ADE_DATA))%></dd>
                        <dt class="col-6"><hl:message key="rotulo.consignacao.valor.parcela.moeda"/>:</dt> <dd class="col-6"><hl:message key="rotulo.moeda"/> <%=NumberHelper.format(((BigDecimal) adePortabilidade.getAttribute(Columns.ADE_VLR)).doubleValue(), NumberHelper.getLang())%></dd>
                        <dt class="col-6"><hl:message key="rotulo.consignacao.prazo"/>:</dt> <dd class="col-6"><%=adePortabilidade.getAttribute(Columns.ADE_PRAZO) == null ? ApplicationResourcesHelper.getMessage("rotulo.indeterminado", responsavel) : TextHelper.forHtmlContent(adePortabilidade.getAttribute(Columns.ADE_PRAZO).toString())%></dd>
                        <dt class="col-6"><hl:message key="rotulo.consignacao.pagas"/>:</dt> <dd class="col-6"><%=adePortabilidade.getAttribute(Columns.ADE_PRD_PAGAS) == null ? "0" : TextHelper.forHtmlContent(adePortabilidade.getAttribute(Columns.ADE_PRD_PAGAS).toString())%></dd>
                        <dt class="col-6"><hl:message key="rotulo.consignacao.data.inicial"/>:</dt> <dd class="col-6"><%=DateHelper.toPeriodString((Date) adePortabilidade.getAttribute(Columns.ADE_ANO_MES_INI))%></dd>
                        <dt class="col-6"><hl:message key="rotulo.consignacao.data.final"/>:</dt> <dd class="col-6"><%=adePortabilidade.getAttribute(Columns.ADE_ANO_MES_FIM) == null ? ApplicationResourcesHelper.getMessage("rotulo.indeterminado", responsavel) : DateHelper.toPeriodString((Date) adePortabilidade.getAttribute(Columns.ADE_ANO_MES_FIM))%></dd>
                        <% if (simulacaoMetodoMexicano) { %>
                        <dt class="col-6"><hl:message key="rotulo.consignacao.periodicidade"/>:</dt> <dd class="col-6"><hl:message key="<%=CodedValues.PERIODICIDADE_FOLHA_QUINZENAL.equals(adePortabilidade.getAttribute(Columns.ADE_PERIODICIDADE)) ? "rotulo.consignacao.periodicidade.quinzenal" : "rotulo.consignacao.periodicidade.mensal" %>"/></dd>
                        <% } %>    
                    </dl>
                </div>
            </div>
            <% } %>
            <%-- FIM --%>

        </div>
        <div class="col-sm">
            <hl:confirmarDadosSERv4 serCodigo="<%=TextHelper.forHtmlAttribute(serCodigo)%>" rseCodigo="<%=TextHelper.forHtmlAttribute(rseCodigo)%>" csaCodigo="<%=TextHelper.forHtmlAttribute(csaCodigo)%>"/>

            <%-- Senha do servidor --%>
            <% if (responsavel.isSer() && ParamSist.getBoolParamSist(CodedValues.TPC_SOLICITA_SENHA_AUTORIZACAO_OPE_REALIZADAS_SER, responsavel)) { %>
              <div class="card">
              <%
                 String mascaraLogin = (String) ParamSist.getInstance().getParam(CodedValues.TPC_MASCARA_LOGIN_EXTERNO_SERVIDOR, responsavel);
                 if (!TextHelper.isNull(mascaraLogin)) {
              %>
                <div class="row">
                  <div class="form-group col-sm-12  col-md-12">
                    <label for="serLogin"><hl:message key="rotulo.usuario.autorizacao.servidor.singular"/><%=serSenhaObrigatoria ? "" : " " + ApplicationResourcesHelper.getMessage("rotulo.campo.opcional", responsavel)%><hl:message key="rotulo.campo.opcional"/></label>
                    <hl:htmlinput name="serLogin" type="text" classe="form-control" di="serLogin" size="15" mask="<%=TextHelper.forHtmlAttribute(mascaraLogin)%>" />
                  </div>            
                </div>
                <% } %>
                <div class="row">
                  <div class="col-sm-12 col-md-12 form-group">
                    <hl:senhaServidorv4 senhaObrigatoria="<%=(String)(serSenhaObrigatoria ? "true" : "false")%>"                                                 
                                      svcCodigo="<%=TextHelper.forHtmlAttribute(svcCodigo)%>"
                                      senhaParaAutorizacaoReserva="true"
                                      nomeCampoSenhaCriptografada="serAutorizacao"
                                      rseCodigo="<%=TextHelper.forHtmlAttribute(rseCodigo)%>"
                                      nf="btnEnvia"
                                      classe="form-control"/>
                  </div>
                </div>
              </div>
            <% } %>
        
            <% if (exigeAssinaturaDigital) { %>
            <div class="card" id="divAnexosAssinaturaDigital">
                <div class="card-header hasIcon">
                    <span class="card-header-icon"><svg width="26"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-upload"></use></svg></span>
                    <h2 class="card-header-title"><hl:message key="rotulo.anexo.credito.eletronico.doc.adicional"/></h2>
                </div>
                <div class="card-body" id="tabelaAnexosAssinaturaDigital">
                    <div class="alert alert-warning" role="alert"><hl:message key="mensagem.confirmacao.instrucao.anexo.assinatura.digital"/></div>

                    <div id="rowToClone" class="row hide">
                      <div class="form-group col-sm-6">
                        <label for="FILE1"><hl:message key="rotulo.upload.arquivo.arquivo"/></label>
                        <input type="file" class="form-control fileToClone" onChange="cloneRow();">
                      </div>
                    </div>
                    
                    <div class="row">
                      <div class="form-group col-sm-6">
                        <label for="FILE1"><hl:message key="rotulo.upload.arquivo.arquivo"/></label>
                        <input type="file" class="form-control" id="file" name="file" onChange="cloneRow();">
                      </div>
                    </div>

                </div>
               </div>
            <% } else if (exigeAnexoServidor) { %>
                    <hl:fileUploadV4 obrigatorio="<%=(boolean)anexoObrigatorio%>" tipoArquivo="anexo_consignacao"/>
            <% }%>

            <% if (!TextHelper.isNull(msgDadosSerNaoPermitemAlteracao)) { %>
            <div class="alert alert-info" role="alert">
              <p class="mb-0">
                  <hl:message key="mensagem.dados.servidor.nao.permitem.alteracao"/>
              </p>
            </div>
            <% } %>

            <% if (leilaoReverso && !temBloqueioLeilao && !tpcSolicitarPortabilidadeRanking) { %>
            <div class="alert alerta-checkbox" role="alert">
              <input type="checkbox" name="TERMO_ACEITE_LEILAO" id="TERMO_ACEITE_LEILAO" class="form-check-input" value="SIM" onclick="exibeDiv()" disabled checked/>
              <label for="TERMO_ACEITE_LEILAO" class="form-check-label font-weight-bold">
                <%= TextHelper.forHtml(mensagemTermoAceite) %>
              </label>
            </div>
            <% } %>

            <% if (!TextHelper.isNull(termoConsentimentoDadosServidor)) { %>
               <div>
                 <p>  
                   <span class="info" style="display: block;">
                      <input type="checkbox" name="aceitoTermoUsoColetaDados" id="aceitoTermoUsoColetaDados" value="S" />
                      <hl:message key="mensagem.termo.de.consentimento.coleta.dados.servidor.aceito"/>&nbsp;<a href="#" data-bs-toggle="modal" data-bs-target="#confirmarTermoUso"><hl:message key="mensagem.termo.de.consentimento.coleta.dados.servidor.link"/></a>
                   </span>   
                 </p>
               </div>
            <% } %>

            <div class="btn-action">
                <a class="btn btn-outline-danger" href="#" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.voltar"/></a>
                <a class="btn btn-primary" id="btnEnvia" href="#" ><svg width="17"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-confirmar"></use></svg><hl:message key="rotulo.botao.confirmar"/></a>
            </div>
        </div>
    </div>

    <hl:htmlinput name="tipo"   type="hidden" value="<%= leilaoReverso ? "leilao" : "simulacao" %>"/>
    <hl:htmlinput name="exigeAssinaturaDigital"   type="hidden" value="<%=TextHelper.forHtmlAttribute(exigeAssinaturaDigital)%>"/>
    <hl:htmlinput name="SER_CODIGO"   type="hidden" value="<%=TextHelper.forHtmlAttribute(serCodigo)%>"/>
    <hl:htmlinput name="RSE_CODIGO"   type="hidden" value="<%=TextHelper.forHtmlAttribute(rseCodigo)%>"/>
    <hl:htmlinput name="CSA_CODIGO"   type="hidden" value="<%=TextHelper.forHtmlAttribute(csaCodigo)%>"/>
    <hl:htmlinput name="CSA_NOME"     type="hidden" value="<%=TextHelper.forHtmlAttribute(csaNome)%>"/>
    <hl:htmlinput name="SVC_CODIGO"   type="hidden" value="<%=TextHelper.forHtmlAttribute(svcCodigo)%>"/>
    <hl:htmlinput name="SVC_CODIGO_ORIGEM"   type="hidden" value="<%=TextHelper.forHtmlAttribute(svcCodigoOrigem)%>"/>
    <hl:htmlinput name="SVC_IDENTIFICADOR"   type="hidden" value="<%=TextHelper.forHtmlAttribute(svcIdentificador)%>"/>
    <hl:htmlinput name="CSA_IDENTIFICADOR"   type="hidden" value="<%=TextHelper.forHtmlAttribute(csaIdentificador)%>"/>
    <hl:htmlinput name="CFT_CODIGO"   type="hidden" value="<%=TextHelper.forHtmlAttribute(cftCodigo)%>"/>
    <hl:htmlinput name="DTJ_CODIGO"   type="hidden" value="<%=TextHelper.forHtmlAttribute(dtjCodigo)%>"/>
    <hl:htmlinput name="ADE_VLR"      type="hidden" value="<%=TextHelper.forHtmlAttribute(adeVlr)%>"/>
    <hl:htmlinput name="ADE_CARENCIA" type="hidden" value="<%=TextHelper.forHtmlAttribute(String.valueOf(carenciaMinPermitida))%>"/>
    <% if(simulacaoMetodoMexicano) { %>
      <hl:htmlinput name="ADE_VLR_CAT"  type="hidden" value="<%=TextHelper.forHtmlAttribute(ade_vlr_cat)%>"/>
      <hl:htmlinput name="ADE_VLR_IVA"  type="hidden" value="<%=TextHelper.forHtmlAttribute(adeVlrIva)%>"/>
    <% } else if (simulacaoMetodoBrasileiro) { %>
      <hl:htmlinput name="ADE_VLR_TAC"  type="hidden" value="<%=TextHelper.forHtmlAttribute(adeVlrTac)%>"/>
      <hl:htmlinput name="ADE_VLR_IOF"  type="hidden" value="<%=TextHelper.forHtmlAttribute(adeVlrIof)%>"/>
    <% } %>
    <% if (adePortabilidade != null) { %>
      <hl:htmlinput name="ADE_CODIGO_PORTABILIDADE"  type="hidden" value="<%=TextHelper.forHtmlAttribute(adePortabilidade.getAttribute(Columns.ADE_CODIGO))%>"/>
    <% } %>
    <% if (exigenciaConfirmacaoLeitura != null) { %>
      <hl:htmlinput name="exigenciaConfirmacaoLeitura" type="hidden" value="<%=TextHelper.forHtmlAttribute(exigenciaConfirmacaoLeitura)%>"/>
    <% } %>
    
    <hl:htmlinput type="hidden" name="telaConfirmacaoDuplicidade" value="<%=TextHelper.forHtmlAttribute(request.getParameter("telaConfirmacaoDuplicidade")) %>" />
    <hl:htmlinput type="hidden" name="chkConfirmarDuplicidade" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "chkConfirmarDuplicidade")) %>"/>
    <hl:htmlinput type="hidden" name="TMO_CODIGO" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "TMO_CODIGO")) %>" />
    <hl:htmlinput type="hidden" name="ADE_OBS" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "ADE_OBS")) %>" />
    
    <hl:htmlinput name="VLR_LIBERADO" type="hidden" value="<%=TextHelper.forHtmlAttribute(vlrLiberado)%>"/>
    <hl:htmlinput name="PRZ_VLR"      type="hidden" value="<%=TextHelper.forHtmlAttribute(przVlr)%>"/>
    <hl:htmlinput name="titulo"       type="hidden" value="<%=TextHelper.forHtmlAttribute(svcDescricao)%>"/>
    <hl:htmlinput name="RANKING"      type="hidden" value="<%=TextHelper.forHtmlAttribute(ranking)%>"/>
    <hl:htmlinput name="CFT_DIA"      type="hidden" value="<%=TextHelper.forHtmlAttribute(cftDia)%>"/>
    <hl:htmlinput name="ADE_PERIODICIDADE" type="hidden" value="<%=TextHelper.forHtmlAttribute(adePeriodicidade)%>" />
    <hl:htmlinput name="SIMULACAO_POR_ADE_VLR" type="hidden" value="<%=TextHelper.forHtmlAttribute(simulacaoAdeVlr)%>" />

    <hl:htmlinput type="hidden" name="chaveSeguranca" value="<%=TextHelper.forHtmlAttribute(request.getAttribute("chaveSeguranca")) %>" />
</form>

<div class="modal fade" id="confirmarSimulacaoModal" tabindex="-1" role="dialog" aria-labelledby="confirmarSimulacaoModalLabel" aria-hidden="true">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header pb-0">
        <span class="modal-title about-title mb-0" id="confirmarSimulacaoModalLabel"> <hl:message key="mensagem.aviso" /></span>
        <button type="button" class="logout mr-1 d-print-none" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
        <span id="mensagemConfirmacao"></span>
      </div>
      <div class="modal-footer pt-0">
        <div class="btn-action mt-2 mb-0">
          <a class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.cancelar"/>' href="#" alt="<hl:message key="rotulo.botao.cancelar"/>"
            title="<hl:message key="rotulo.botao.cancelar"/>">
            <hl:message key="rotulo.botao.cancelar" />
          </a>
          <a id="confirmarSimulacao" class="btn btn-primary" href="#no-back"
            alt="<hl:message key="rotulo.botao.confirmar"/>" title="<hl:message key="rotulo.botao.confirmar"/>">
            <hl:message key="rotulo.botao.confirmar" />
          </a>
        </div>
      </div>
    </div>
  </div>
</div>

</c:set>

<c:set var="javascript">
<script src="../js/validalogin.js?<hl:message key="release.tag"/>"></script>
<% if (responsavel.isSer() && ParamSist.getBoolParamSist(CodedValues.TPC_SOLICITA_SENHA_AUTORIZACAO_OPE_REALIZADAS_SER, responsavel)) { %>
<hl:senhaServidorv4 senhaObrigatoria="<%=(String)(serSenhaObrigatoria ? "true" : "false")%>"                                                 
                  svcCodigo="<%=request.getAttribute("svcCodigo").toString()%>"
                  senhaParaAutorizacaoReserva="true"
                  nomeCampoSenhaCriptografada="serAutorizacao"
                  rseCodigo="<%=rseCodigo%>"
                  nf="submit"
                  classe="form-control"
                  scriptOnly="true"/>
<% } %>
<%if (exigeAnexoServidor && !exigeAssinaturaDigital) { %>
  <hl:fileUploadV4 scriptOnly="true" tipoArquivo="anexo_consignacao" />
<%} %>
<script type="text/JavaScript">
f0 = document.forms[0];

function formLoad() {
  <% if (!TextHelper.isNull(mensagemSolicitacaoOutroSvc)) { %>
  $("#dialogEscolherOutroSvc").modal("show");
  <% } %>
  f0.SER_END.focus();
}

function vf_valida_dados() {
  if (<%=(boolean)serSenhaObrigatoria%> && f0.serLogin != null && f0.serLogin.value == '') {
    f0.serLogin.focus();
    alert('<hl:message key="mensagem.informe.ser.usuario"/>');
    return false;
  }
  if (<%=(boolean)serSenhaObrigatoria%> && f0.senha != null && trim(f0.senha.value) == '') {
    f0.senha.focus();
    alert('<hl:message key="mensagem.informe.ser.senha"/>');
    return false;
  }
  if ((<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_LOGRADOURO, responsavel)%> || (<%=ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_LOGRADOURO, responsavel)%> && (<%=enderecoObrigatorio%> || <%=enderecoCelularObrigatorio%>))) && f0.SER_END != null && trim(f0.SER_END.value) == '') {
        alert('<hl:message key="mensagem.informe.servidor.logradouro"/>');
        f0.SER_END.focus();
        return false;  
      }
  if ((<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NRO, responsavel)%> || (<%=ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NRO, responsavel)%> && (<%=enderecoObrigatorio%> || <%=enderecoCelularObrigatorio%>))) && f0.SER_NRO != null && trim(f0.SER_NRO.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.numero"/>');
    f0.SER_NRO.focus();
    return false;  
  }
  if (<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_COMPLEMENTO, responsavel)%> && f0.SER_COMPL != null && trim(f0.SER_COMPL.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.complemento"/>');
    f0.SER_COMPL.focus();
    return false;  
  }
  if ((<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_BAIRRO, responsavel)%> || (<%=ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_BAIRRO, responsavel)%> && (<%=enderecoObrigatorio%> || <%=enderecoCelularObrigatorio%>))) && f0.SER_BAIRRO != null && trim(f0.SER_BAIRRO.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.bairro"/>');
    f0.SER_BAIRRO.focus();
    return false;  
  }
  if ((<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CIDADE, responsavel)%> || (<%=ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CIDADE, responsavel)%> && (<%=enderecoObrigatorio%> || <%=enderecoCelularObrigatorio%>))) && f0.SER_CIDADE != null && trim(f0.SER_CIDADE.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.cidade"/>');
    f0.SER_CIDADE.focus();
    return false;  
  }
  if ((<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CEP, responsavel)%> || (<%=ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CEP, responsavel)%> && (<%=enderecoObrigatorio%> || <%=enderecoCelularObrigatorio%>))) && f0.SER_CEP != null && trim(f0.SER_CEP.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.cep"/>');
    f0.SER_CEP.focus();
    return false;  
  }
  if ((<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_UF, responsavel)%> || (<%=ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_UF, responsavel)%> && (<%=enderecoObrigatorio%> || <%=enderecoCelularObrigatorio%>))) && f0.SER_UF != null && trim(f0.SER_UF.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.estado"/>');
    f0.SER_UF.focus();
    return false;  
  }
  if (<%=(boolean)(exigeTelefone)%> && f0.TDA_25 != null && trim(f0.TDA_25.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.telefone.solicitacao"/>');
    f0.TDA_25.focus();
    return false;
  }
  if ((<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_TELEFONE, responsavel)%> || (<%=ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_TELEFONE, responsavel)%> && (<%=celularObrigatorio%> || <%=enderecoCelularObrigatorio%>))) && f0.SER_TEL != null && trim(f0.SER_TEL.value) == '') {
        alert('<hl:message key="mensagem.informe.servidor.telefone"/>');
        f0.SER_TEL.focus();
        return false;
      }
  if (<%=(boolean)(exigeMunicipioLotacao)%> && f0.RSE_MUNICIPIO_LOTACAO != null && trim(f0.RSE_MUNICIPIO_LOTACAO.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.municipio.lotacao"/>');
    f0.RSE_MUNICIPIO_LOTACAO.focus();
    return false;
  }
  if (<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_IBAN, responsavel)%> && f0.SER_IBAN != null && trim(f0.SER_IBAN.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.iban"/>');
    f0.SER_IBAN.focus();
    return false; 
  }
  if (<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_DATA_NASCIMENTO, responsavel)%> && f0.SER_DATA_NASC != null && trim(f0.SER_DATA_NASC.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.data.nascimento"/>');
    f0.SER_DATA_NASC.focus();
    return false; 
  }
  if (<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_DATA_ADMISSAO, responsavel)%> && f0.SER_DATA_ADMISSAO != null && trim(f0.SER_DATA_ADMISSAO.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.data.admissao"/>');
    f0.SER_DATA_ADMISSAO.focus();
    return false; 
  }
  if (<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_SEXO, responsavel)%> && f0.SER_SEXO != null && trim(f0.SER_SEXO.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.sexo"/>');
    f0.SER_SEXO.focus();
    return false; 
  }
  if (<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NRO_IDT, responsavel)%> && f0.SER_NRO_IDT != null && trim(f0.SER_NRO_IDT.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.identidade"/>');
    f0.SER_NRO_IDT.focus();
    return false; 
  }
  if (<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_DATA_IDT, responsavel)%> && f0.SER_DATA_IDT != null && trim(f0.SER_DATA_IDT.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.data.emissao.identidade"/>');
    f0.SER_DATA_IDT.focus();
    return false; 
  }
  if ((<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CELULAR, responsavel)%> || (<%=ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CELULAR, responsavel)%> && (<%=celularObrigatorio%> || <%=enderecoCelularObrigatorio%>))) && f0.SER_CEL != null && trim(f0.SER_CEL.value) == '') {
        alert('<hl:message key="mensagem.informe.servidor.celular"/>');
        f0.SER_CEL.focus();
        return false;  
      }
  if (<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NACIONALIDADE, responsavel)%> && f0.SER_NACIONALIDADE != null && trim(f0.SER_NACIONALIDADE.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.nacionalidade"/>');
    f0.SER_NACIONALIDADE.focus();
    return false; 
  }
  if (<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_SALARIO, responsavel)%> && f0.SER_SALARIO != null && trim(f0.SER_SALARIO.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.salario"/>');
    f0.SER_SALARIO.focus();
    return false; 
  }
  if (<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NATURALIDADE, responsavel)%> && f0.SER_NATURALIDADE != null && trim(f0.SER_NATURALIDADE.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.naturalidade"/>');
    f0.SER_NATURALIDADE.focus();
    return false; 
  }
  if (<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_UF_NASCIMENTO, responsavel)%> && f0.SER_UF_NASCIMENTO != null && trim(f0.SER_UF_NASCIMENTO.value) == '') {
    alert('<hl:message key="mensagem.informe.uf.nascimento"/>');
    f0.SER_UF_NASCIMENTO.focus();
    return false; 
  } 
  if (<%=campoCidadeObrigatorio%> && f0.CID_CODIGO != null && trim(f0.CID_CODIGO.value) == '') {
    alert('<hl:message key="mensagem.informe.cidade.assinatura.contrato"/>');
    f0.CID_CODIGO.focus();
    return false; 
  }

  if (!verificarCamposAdicionais() || !verificaTermoConsentimento()) {
      return false;
  }

  if (f0.senha != null && trim(f0.senha.value) != '') {
    CriptografaSenha(f0.senha, f0.serAutorizacao, false);
  }

  return true; 
}

function verificarCamposAdicionais() {

    <% if (tdaList != null) { %>
    <% for (TransferObject tda : tdaList) { %>
        var sptExibe = '<%=(String) tda.getAttribute(Columns.SPT_EXIBE)%>'; 
        var cptExibe = '<%=(String) tda.getAttribute(Columns.CPT_EXIBE)%>'; 
        if ('O' == sptExibe || 'O' == cptExibe) {
            var elements = document.getElementsByName('TDA_<%=(String) tda.getAttribute(Columns.TDA_CODIGO)%>')

            if (elements[0].type == 'text') {
                var value = elements[0].value;
                if (!value || !value.trim()) {
                    alert('<hl:message key="mensagem.preencher.campos.adicionais" arg0="<%=TextHelper.forHtmlAttribute(tda.getAttribute(Columns.TDA_DESCRICAO))%>"/> ');
                    return false;
                }
            } else if (elements[0].type == 'radio') {
                var preenchido = false;
                for (el of elements) {
                       if (el.checked) {
                        preenchido = true;
                        break;
                       }
                }
                if (!preenchido) {
                    alert('<hl:message key="mensagem.preencher.campos.adicionais" arg0="<%=TextHelper.forHtmlAttribute(tda.getAttribute(Columns.TDA_DESCRICAO))%>"/> ');
                    return false;
               }
            }
       }
    <% } %>
  <% } %>

  return true;
}

function verificaEmail() {
    if (f0.SER_EMAIL != null) {
      if ((f0.SER_EMAIL.value != '') &&
          (!isEmailValid(f0.SER_EMAIL.value))) {
        alert('<hl:message key="mensagem.erro.solicitacao.email.codigo.autorizacao.invalido"/>');
        f0.SER_EMAIL.focus();
        return false;
      }
    }

    return true;
}

function mensagemConfirmacao() {
    var mensagem = '';
    var myModal = new bootstrap.Modal(document.getElementById("confirmarSimulacaoModal"), {});

  <% if (adePortabilidade != null && !tpcSolicitarPortabilidadeRanking) { %>
    mensagem = '<hl:message key="mensagem.confirmacao.solicitacao.portabilidade"/>';
  <% } else if (adePortabilidade != null && tpcSolicitarPortabilidadeRanking) { %>
    mensagem = '<hl:message key="mensagem.confirmacao.solicitacao.portabilidade.ranking.consignataria"/>';
  <% } else if (leilaoReverso) { %>
    mensagem = '<hl:message key="mensagem.confirmacao.solicitacao.leilao.reverso"/>';
  <% } else { %>
    if (f0.TERMO_ACEITE_LEILAO != null && f0.TERMO_ACEITE_LEILAO.checked) {
        mensagem = '<hl:message key="mensagem.confirmacao.solicitacao.leilao.reverso"/>';
    } else {
   	    mensagem = '<hl:message key="mensagem.confirmacao.solicitacao.reserva.consignataria" arg0="<%=TextHelper.forHtmlAttribute(csaNome)%>"/> <%if (responsavel.isSer()) {%><%=ApplicationResourcesHelper.getMessage("mensagem.alerta.envio.solicitacao", responsavel) %> <%} %>';
    }
  <% } %>
  document.getElementById("mensagemConfirmacao").innerText = mensagem;
  myModal.show();
}

$('#btnEnvia').click(async function(event){
    if (!vf_upload_arquivos() || !vf_valida_dados() || !verificaEmail()) {
        return false;
    }
    
    <%if(simularConsignacaoComReconhecimentoFacialELiveness){%>
	    let otpValidado = await validarOtpServidorSimularConsignacao();
	    if(otpValidado){
	    	$('#escolhaMetodoConfirmacaoSolicitacao').modal('show');	
	    }else{
	    	postData('../v3/simularConsignacao?acao=erroValidaOtpServidorSimularConsignacao');
	    }
    <%}else {%>
    	mensagemConfirmacao();
    <%}%>
});

$('#confirmarSimulacao').click(function(){
    habilitaCampos();
   <%if(tpcSolicitarPortabilidadeRanking && !leilaoReverso){%>
   	incluirReservaNaRenegociacao();
   <%}else {%>
   	f0.submit();
   <%}%>
});

function incluirReservaNaRenegociacao(){
	var url = '../v3/renegociarConsignacao?acao=incluirReserva'
	       + '&titulo=<%=TextHelper.forJavaScriptBlock(java.net.URLEncoder.encode(svcDescricao, "ISO-8859-1"))%>'
	       + '&RSE_CODIGO=<%=TextHelper.forJavaScriptBlock(rseCodigo)%>'
	       + '&CSA_CODIGO=<%=TextHelper.forJavaScriptBlock(csaCodigo)%>'
	       + '&ORG_CODIGO=<%=TextHelper.forJavaScriptBlock(orgCodigo)%>'
	       + '&SVC_CODIGO=<%=TextHelper.forJavaScriptBlock(svcCodigo)%>'
	       + '&CFT_CODIGO=<%=TextHelper.forJavaScriptBlock(cftCodigo)%>'
	       + '&DTJ_CODIGO=<%=TextHelper.forJavaScriptBlock(dtjCodigo)%>'
	       + '&adeVlr=<%=NumberHelper.reformat(adeVlr, "en", NumberHelper.getLang())%>'
	       + '&adePrazo=<%=TextHelper.forJavaScriptBlock(przVlr)%>'
	       + '&adeCarencia=<%=TextHelper.forJavaScriptBlock(carenciaMinPermitida)%>'
	       + '&ranking=<%=TextHelper.forJavaScriptBlock(ranking)%>'
	       + '&vlrLiberado=<%=TextHelper.forJavaScriptBlock(vlrLiberado)%>'
	       + '&TPC_SOLICITAR_PORTABILIDADE_RANKING=true'
	       <% if(adePortabilidade != null){%>
	       	+ '&ADE_CODIGO_PORTABILIDADE=<%=TextHelper.forHtmlAttribute(adePortabilidade.getAttribute(Columns.ADE_CODIGO))%>'
	       <% } %>
	       
	       if(f0.SER_END != null && trim(f0.SER_END.value) != ''){
	    	    url += '&SER_END='+f0.SER_END.value
	    	}
	    	if(f0.SER_NRO != null && trim(f0.SER_NRO.value) != ''){
	    	    url += '&SER_NRO='+f0.SER_NRO.value
	    	}
	    	if(f0.SER_COMPL != null && trim(f0.SER_COMPL.value) != ''){
	    	    url += '&SER_COMPL='+f0.SER_COMPL.value
	    	}
	    	if(f0.SER_BAIRRO != null && trim(f0.SER_BAIRRO.value) != ''){
	    	    url += '&SER_BAIRRO='+f0.SER_BAIRRO.value
	    	}
	    	if(f0.SER_CIDADE != null && trim(f0.SER_CIDADE.value) != ''){
	    	    url += '&SER_CIDADE='+f0.SER_CIDADE.value
	    	}
	    	if(f0.SER_CEP != null && trim(f0.SER_CEP.value) != ''){
	    	    url += '&SER_CEP='+f0.SER_CEP.value
	    	}
	    	if(f0.SER_UF != null && trim(f0.SER_UF.value) != ''){
	    	    url += '&SER_UF='+f0.SER_UF.value
	    	}
	    	if(f0.SER_TEL != null && trim(f0.SER_TEL.value) != ''){
	    	    url += '&SER_TEL='+f0.SER_TEL.value
	    	}
	    	if(f0.SER_IBAN != null && trim(f0.SER_IBAN.value) != ''){
	    	    url += '&SER_IBAN='+f0.SER_IBAN.value
	    	}
	    	if(f0.SER_DATA_NASC != null && trim(f0.SER_DATA_NASC.value) != ''){
	    	    url += '&SER_DATA_NASC='+f0.SER_DATA_NASC.value
	    	}
	    	if(f0.SER_DATA_ADMISSAO != null && trim(f0.SER_DATA_ADMISSAO.value) != ''){
	    	    url += '&SER_DATA_ADMISSAO='+f0.SER_DATA_ADMISSAO.value
	    	}
	    	if(f0.SER_SEXO != null && trim(f0.SER_SEXO.value) != ''){
	    	    url += '&SER_SEXO='+f0.SER_SEXO.value
	    	}
	    	if(f0.SER_NRO_IDT != null && trim(f0.SER_NRO_IDT.value) != ''){
	    	    url += '&SER_NRO_IDT='+f0.SER_NRO_IDT.value
	    	}
	    	if(f0.SER_CEL != null && trim(f0.SER_CEL.value) != ''){
	    	    url += '&SER_CEL='+f0.SER_CEL.value
	    	}
	    	if(f0.SER_NACIONALIDADE != null && trim(f0.SER_NACIONALIDADE.value) != ''){
	    	    url += '&SER_NACIONALIDADE='+f0.SER_NACIONALIDADE.value
	    	}
	    	if(f0.SER_NATURALIDADE != null && trim(f0.SER_NATURALIDADE.value) != ''){
	    	    url += '&SER_NATURALIDADE='+f0.SER_NATURALIDADE.value
	    	}
	    	if(f0.SER_UF_NASCIMENTO != null && trim(f0.SER_UF_NASCIMENTO.value) != ''){
	    	    url += '&SER_UF_NASCIMENTO='+f0.SER_UF_NASCIMENTO.value
	    	}

	    	<% if (tdaList != null) { %>
	    	  <% for (TransferObject tda : tdaList) { %>
	    	      var sptExibe = '<%=(String) tda.getAttribute(Columns.SPT_EXIBE)%>'; 
	    	      var cptExibe = '<%=(String) tda.getAttribute(Columns.CPT_EXIBE)%>'; 
	    	      if ('O' == sptExibe || 'O' == cptExibe) {
	    	          var elements = document.getElementsByName('TDA_<%=(String) tda.getAttribute(Columns.TDA_CODIGO)%>')

	    	          if (elements[0].type == 'text') {
	    	              var value = elements[0].value;
	    	              if (!value || !value.trim()) {
	    	                  alert('<hl:message key="mensagem.preencher.campos.adicionais" arg0="<%=TextHelper.forHtmlAttribute(tda.getAttribute(Columns.TDA_DESCRICAO))%>"/> ');
	    	                  return false;
	    	              } else {
	    	            	  url += '&TDA_<%=(String) tda.getAttribute(Columns.TDA_CODIGO)%>='+value
	    	              }
	    	          } else if (elements[0].type == 'radio') {
	    	              var preenchido = false;
	    	              for (el of elements) {
	    	                     if (el.checked) {
	    	                      preenchido = true;
	    	                      break;
	    	                     }
	    	              }
	    	              if (!preenchido) {
	    	                  alert('<hl:message key="mensagem.preencher.campos.adicionais" arg0="<%=TextHelper.forHtmlAttribute(tda.getAttribute(Columns.TDA_DESCRICAO))%>"/> ');
	    	                  return false;
	    	             } else {
	    	           	  	url += '&TDA_<%=(String) tda.getAttribute(Columns.TDA_CODIGO)%>='+preenchido
	    	             }
	    	          }
	    	     }
	    	  <% } %>
	    	<% } %>
	    	url += '&<%out.print(SynchronizerToken.generateToken4URL(request));%>';
	    	
	postData(url);
}

function verificaTermoConsentimento() {
  <% if (!TextHelper.isNull(termoConsentimentoDadosServidor)) { %>
  var checkboxVer = document.getElementById("aceitoTermoUsoColetaDados");
  if (!checkboxVer.checked) {
    alert('<hl:message key="mensagem.termo.de.consentimento.coleta.dados.servidor.alerta"/>');
    return false;
  }
  <% } %>
  return true;
}

function habilitaCampos() {
  <% if (leilaoReverso) { %>
      f0.TERMO_ACEITE_LEILAO.disabled = false;  
  <% } %>
}

function exibeDiv() {
    var div2 = $("#divAnexosAssinaturaDigital");
    if (div2.length) {
     div2.toggle();
    }
}

var cont = 1;
var anexosAssinaturaDigital = true;

function cloneRow() {
  var elements = $('input[type=file][name*=file]');

    if ((elements.length < <%=qtdMaximaArquivos%>)) {
      var row = document.getElementById("rowToClone"); // find row to copy
        var table = document.getElementById("tabelaAnexosAssinaturaDigital"); // find table to append to
        var clone = row.cloneNode(true); // copy children too
        clone.id = "novoUp"+cont; // change id or other attributes/contents
        clone.classList.remove('hide');
        table.appendChild(clone); // add new row to end of table
        document.getElementById("novoUp"+cont).getElementsByClassName("fileToClone")[0].setAttribute('name', "file"+cont)
        
        cont++
    }
}

function vf_upload_arquivos() {
    <%if (!exigeAssinaturaDigital && !exigeAnexoServidor) {%>
        return true;
    <%} else if (exigeAnexoServidor && !exigeAssinaturaDigital) {%>
   		let elemento = document.getElementById("pic-progress-wrap-FILE1");
		let qtdeMin = <%=qtdeMinAnexos%>;
		if(elemento == null || elemento == 'undefined' || elemento.childNodes.length < qtdeMin){
			alert('<%=ApplicationResourcesHelper.getMessage("mensagem.erro.upload.arquivo.qunt.min", responsavel, qtdeMinAnexos)%>');
        return false;
		}
    <%}%>
    
    if(!$("#divAnexosAssinaturaDigital").is(":visible")) {
     return true;
    }
    
    var controles = new Array("file");
    var msgs = new Array ('<%=ApplicationResourcesHelper.getMessage("mensagem.erro.upload.arquivo.assinatura.digital.selecione.arquivo", responsavel)%>');

    var ok = ValidaCampos(controles, msgs)
    
    return ok;
}

<% if (!TextHelper.isNull(mensagemSolicitacaoOutroSvc)) { %>
function escolherOutroSvc() {
    var url = '../v3/simularConsignacao?acao=escolherOutroSvc'
            + '&titulo=<%=TextHelper.forJavaScriptBlock(java.net.URLEncoder.encode(svcDescricao, "ISO-8859-1"))%>'
            + '&ADE_VLR=<%=TextHelper.forJavaScriptBlock(adeVlr)%>'
            + '&VLR_LIBERADO='  <%-- vlr liberado vazio --%>
            + '&RSE_CODIGO=<%=TextHelper.forJavaScriptBlock(rseCodigo)%>'
            + '&ORG_CODIGO=<%=TextHelper.forJavaScriptBlock(orgCodigo)%>'
            + '&SVC_CODIGO=<%=TextHelper.forJavaScriptBlock(svcCodigo)%>'
            + '&SVC_CODIGO_ORIGEM=<%=TextHelper.forJavaScriptBlock(svcCodigo)%>'
            + '&PRZ_VLR=<%=TextHelper.forJavaScriptBlock(przVlr)%>'
            + '&CFT_DIA=<%=TextHelper.forJavaScriptBlock(cftDia)%>'
            + '&NOVO_CFT_CODIGO=<%=TextHelper.forJavaScriptBlock(novoCftCodigo)%>'
            <% if (simulacaoMetodoMexicano) { %>
            + '&ADE_PERIODICIDADE=' + '<%=TextHelper.forJavaScriptBlock(adePeriodicidade)%>'
            <% } %>
            + '&chaveSeguranca=<%=RSA.encrypt(adeVlr + "|" + "" + "|" + przVlr, keyPair.getPublic())%>' <%-- vlr liberado vazio --%>
            + '&<%out.print(SynchronizerToken.generateToken4URL(request));%>';

    postData(url);
}
<% } %>

<%if(simularConsignacaoComReconhecimentoFacialELiveness){%>
	async function validarOtpServidorSimularConsignacao() {
		let serLogin = null;
		let senhaCriptografada = null;
		if (f0.serAutorizacao.value != null && f0.serAutorizacao.value != '') {
			senhaCriptografada = f0.serAutorizacao.value;
		}
		
		<% if (responsavel.isSer() && ParamSist.getBoolParamSist(CodedValues.TPC_SOLICITA_SENHA_AUTORIZACAO_OPE_REALIZADAS_SER, responsavel)) {
				String mascaraLogin = (String) ParamSist.getInstance().getParam(CodedValues.TPC_MASCARA_LOGIN_EXTERNO_SERVIDOR, responsavel);
				if (!TextHelper.isNull(mascaraLogin)) { %>
					serLogin = document.getElementById("serLogin").value;
	   			<%}
		}%>
	
	
		let parametrosValidacao = {
			RSE_CODIGO: '<%=TextHelper.forJavaScriptBlock(rseCodigo)%>',
			SVC_CODIGO: '<%=TextHelper.forJavaScriptBlock(svcCodigo)%>',
			CSA_CODIGO: '<%=TextHelper.forJavaScriptBlock(csaCodigo)%>',
			SENHA: senhaCriptografada,
			SER_LOGIN: serLogin
		};
		
		try {
			const url = '../v3/simularConsignacao?acao=validaOtpServidorSimularConsignacao&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&_skip_history_';
			const response = await fetch(url, {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify(parametrosValidacao)
			});
	
			if (response.status === 200) {
				return true;
			} else {
				return false;
			}
		} catch (error) {
			return false;
		}
	}
	
	async function gerarSenhaOtpComModoEntrega(modoEntrega) {
		var parametros = "acao=gerarSenhaAutorizacaoOtp" + "&RSE_CODIGO=" + '<%=TextHelper.forJavaScriptBlock(rseCodigo)%>' + "&MODO_ENTREGA=" + modoEntrega + "&_skip_history_=1";
		try {
			const url = '../v3/simularConsignacao?' + parametros;
	
			const response = await fetch(url, { method: 'POST' });
			
			if (response.status === 200) {
				let data = await response.json();
				
				if (data.otpReconhecimentoFacial == null || data.otpReconhecimentoFacial == '') {
					fecharModalCarregamento();
					$('#inserirOtpModal').modal('show');
				} else {
					document.getElementById("otpGeradoParaReconhecimentoFacial").value = data.otpReconhecimentoFacial;
					iniciarInstrucoes();
				}
			} else if (response.status == 409) {
				let data = await response.json();
				if(data.mensagem != null && data.mensagem != ''){
					document.getElementById('botaoSairCarregamento').style.display = 'block';
					document.getElementById('mensagemCarregamento').innerText = data.mensagem;
					document.getElementById('carregamentoModalSimularConsignacao').style.display = 'none';
				}else{
					postData('../v3/simularConsignacao?acao=erroValidaOtpServidorSimularConsignacao&mensagem=' + '<%=ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel)%>');
				}
			} else {
				postData('../v3/simularConsignacao?acao=erroValidaOtpServidorSimularConsignacao&mensagem=' + '<%=ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel)%>');
			}
		} catch (error) {
			postData('../v3/simularConsignacao?acao=erroValidaOtpServidorSimularConsignacao&mensagem=' + '<%=ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel)%>');
		}
	}
	
	function fecharModalEscolhaMetodoConfirmacaoSolicitacao() {
		$('#escolhaMetodoConfirmacaoSolicitacao').modal('hide');
	}
	
	function fecharModalOtpGeradoParaReconhecimentoFacial() {
		$('#inserirOtpModal').modal('hide');
	}
	
	function emitirBoletoValidacaoOtp() {
		f0.senha.value = document.getElementById('otpReconhecimentoFacialComLiveness').value;
	
		if (!vf_upload_arquivos() || !vf_valida_dados() || !verificaEmail()) {
			return false;
		}
	
		mensagemConfirmacao();
		document.getElementById('otpReconhecimentoFacialComLiveness').value = '';
	}
	
	function modalCarregamento(){
		$('#modalCarregamento').modal('show');
	}
	
	function fecharModalCarregamento(){
		$('#modalCarregamento').modal('hide');
		document.getElementById('botaoSairCarregamento').style.display = 'none';
		document.getElementById('mensagemCarregamento').innerText = '<%=ApplicationResourcesHelper.getMessage("rotulo.reconhecimento.facial.modal.carregamento.aguarde", responsavel)%>';
		document.getElementById('carregamentoModalSimularConsignacao').style.display = 'block';
		
		
		
	}
<%}%>



window.onload = formLoad;

</script>
<style>
    .hide {
        display: none;
    }
</style>
</c:set>
<c:set var="pageModals">
  <%-- Modal mensagem de erro --%>
  <div class="modal fade" id="dialogErro" tabindex="-1" role="dialog" aria-labelledby="dialogErroLabel" aria-hidden="true" title='<hl:message key="mensagem.confirmacao.operacao.sensivel.titulo.modal"/>' >          
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-body">
          <div id="dialogErroLabel" class="alert alert-danger" role="alert"></div>
        </div>
        <div class="modal-footer pt-0">
          <div class="btn-action mt-2 mb-0">
            <a class="btn btn-primary" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.ok"/>' href="#" title="<hl:message key="rotulo.botao.ok"/>">
              <hl:message key="rotulo.botao.ok" />
            </a>
          </div>
        </div>
      </div>
    </div>
  </div>
  <%-- Modal outro serviço --%>
  <% if (!TextHelper.isNull(mensagemSolicitacaoOutroSvc)) { %>
  <div class="modal fade" id="dialogEscolherOutroSvc" tabindex="-1" role="dialog" aria-labelledby="labelEscolherOutroSvc" aria-hidden="true" >          
    <div class="modal-dialog modal-wide-content" role="document">
      <div class="modal-content">
        <div class="modal-header pb-0">
          <span class="modal-title about-title mb-0" id="labelEscolherOutroSvc"><hl:message key="mensagem.solicitar.consignacao.outro.servico.titulo" arg0="<%=nomeOutroSvc %>"/></span>
          <button type="button" class="logout mr-1 d-print-none" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
            <span aria-hidden="true">&times;</span>
          </button>
        </div>
       <div class="modal-body">
         <%= TextHelper.forHtmlContent(mensagemSolicitacaoOutroSvc) %>
       </div>
       <div class="modal-footer pt-0">
         <div class="btn-action mt-2 mb-0">
           <a class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.quero.contratar.nao"/>' href="#no-back" alt="<hl:message key="rotulo.botao.quero.contratar.nao"/>" title="<hl:message key="rotulo.botao.quero.contratar.nao"/>">
             <hl:message key="rotulo.botao.quero.contratar.nao" />
           </a>
           <a class="btn btn-primary" onclick="escolherOutroSvc()" href="#no-back" alt="<hl:message key="rotulo.botao.quero.contratar.sim"/>" title="<hl:message key="rotulo.botao.quero.contratar.sim"/>">
             <hl:message key="rotulo.botao.quero.contratar.sim" />
           </a>
         </div>
       </div>
     </div>
   </div>
 </div>
 <% } %>
 <%-- Modal: Termo de Consentimento de coleta de dados do servidor --%>
 <% if (!TextHelper.isNull(termoConsentimentoDadosServidor)) { %>
 <div class="modal fade" id="confirmarTermoUso" tabindex="-1" role="dialog" aria-labelledby="modalTitulo" aria-hidden="true" style="display: none;">
   <div class="modal-dialog modalTermoUso" role="document">
     <div class="modal-content">
       <div class="modal-header pb-0">
         <h5 class="modal-title about-title mb-0" id="modalTitulo"><hl:message key="mensagem.termo.de.consentimento.coleta.dados.servidor.titulo"/></h5>
         <button type="button" class="logout mr-2" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
           <span aria-hidden="true">&times;</span>
         </button>
       </div>
       <div class="modal-body">
         <span id="textoTermoUso">
           <%=termoConsentimentoDadosServidor%>
         </span>
       </div>
       <div class="modal-footer pt-0">
         <div class="btn-action mt-2 mb-0">
           <a class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>' href="#" alt="<hl:message key="rotulo.botao.fechar"/>" title="<hl:message key="rotulo.botao.fechar"/>">
             <hl:message key="rotulo.botao.fechar" />
           </a>
         </div>
       </div>
     </div>
   </div>
 </div>
 <% } %>
<% if(simularConsignacaoComReconhecimentoFacialELiveness){%>
<input type="hidden" id="otpGeradoParaReconhecimentoFacial" name="otpGeradoParaReconhecimentoFacial">
<div class="modal fade" id="escolhaMetodoConfirmacaoSolicitacao" tabindex="-1" aria-labelledby="escolhaMetodoConfirmacaoSolicitacao" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h1 class="modal-title fs-5"><hl:message key="rotulo.reconhecimento.facial.titulo.geracao.otp"/></h1>
			</div>
			<div class="modal-body">
				<div class="container-fluid">
					<div class="row m-4">
						<button type="button" class="btn btn-primary" onclick="fecharModalEscolhaMetodoConfirmacaoSolicitacao();modalCarregamento();gerarSenhaOtpComModoEntrega('<%=CodedValues.ALTERACAO_SENHA_AUT_SER_SMS%>');"><hl:message key="rotulo.reconhecimento.facial.botao.receber.otp.sms"/></button>
					</div>
					<div class="row m-4">
						<button type="button" class="btn btn-primary" onclick="fecharModalEscolhaMetodoConfirmacaoSolicitacao();modalCarregamento();gerarSenhaOtpComModoEntrega('<%=CodedValues.ALTERACAO_SENHA_AUT_SER_ENVIA_EMAIL%>');"><hl:message key="rotulo.reconhecimento.facial.botao.receber.otp.email"/></button>
					</div>
					<div class="row m-4">
						<button type="button" class="btn btn-primary" onclick="fecharModalEscolhaMetodoConfirmacaoSolicitacao();gerarSenhaOtpComModoEntrega('<%=CodedValues.ALTERACAO_SENHA_AUT_SER_RECONHECIMENTO_FACIAL%>');"><hl:message key="rotulo.reconhecimento.facial.botao.receber.otp.reconhecimento.facial"/></button>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<div class="modal fade" id="inserirOtpModal" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="inserirOtpModal" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="staticBackdropLabel"><hl:message key="rotulo.reconhecimento.facial.titulo.inserir.otp"/></h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        <div class="container-fluid">
			<div class="row m-4">
	             <input type="password" class="form-control" id="otpReconhecimentoFacialComLiveness" name="otpReconhecimentoFacialComLiveness" placeholder='<hl:message key="rotulo.reconhecimento.facial.inserir.otp.placeholder"/>'>
			</div>
		</div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" onClick='fecharModalOtpGeradoParaReconhecimentoFacial();emitirBoletoValidacaoOtp();'><hl:message key="rotulo.botao.enviar"/></button>
        <button type="button" class="btn btn btn-outline-danger" data-bs-dismiss="modal"><hl:message key="rotulo.botao.cancelar"/></button>
      </div>
    </div>
  </div>
</div>

<div class="modal fade" id="modalCarregamento" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="modalCarregamento" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-body">
      	<div id="carregamentoModalSimularConsignacao" class="text-center">
      	<div class="spinner-border" role="status"></div>
      	</div>
        <h5 class="modal-title" id="mensagemCarregamento"><hl:message key="rotulo.reconhecimento.facial.modal.carregamento.aguarde"/></h5>
        <div class="modal-footer" id="botaoSairCarregamento">
	        <button type="button" onclick="fecharModalCarregamento();" class="btn btn-secondary" data-bs-dismiss="modal"><hl:message key="rotulo.botao.sair"/></button>
    	</div>
      </div>
    </div>
  </div>
</div>
<%} %>
</c:set>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:attribute name="pageModals">${pageModals}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>