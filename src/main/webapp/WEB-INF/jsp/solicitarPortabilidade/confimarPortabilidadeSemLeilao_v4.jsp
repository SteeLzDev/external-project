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
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
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
    String cftDia = !TextHelper.isNull(request.getAttribute("CFT_DIA")) ? request.getAttribute("CFT_DIA").toString() : JspHelper.verificaVarQryStr(request, "CFT_DIA");
    boolean quinzenal = (Boolean) request.getAttribute("quinzenal");
    String orgCodigo = (String) request.getAttribute("orgCodigo");
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
    List<TransferObject> tdaList = (List<TransferObject>) request.getAttribute("tdaList");
    String csaIdentificador = (String) request.getAttribute("csaIdentificador");
    String cftCodigo = (String) request.getAttribute("cftCodigo");
    String dtjCodigo = (String) request.getAttribute("dtjCodigo");
    String cftVlr = (String) request.getAttribute("cftVlr");
    String adePeriodicidade = (String) request.getAttribute("adePeriodicidade");
    String ranking = !TextHelper.isNull(request.getAttribute("RANKING")) ? request.getAttribute("RANKING").toString() : JspHelper.verificaVarQryStr(request, "RANKING");
    String msgDadosSerNaoPermitemAlteracao = ApplicationResourcesHelper.getMessage("mensagem.dados.servidor.nao.permitem.alteracao", responsavel);
    String exigenciaConfirmacaoLeitura = (String) request.getAttribute("exigenciaConfirmacaoLeitura");
    Boolean serSenhaObrigatoria = !TextHelper.isNull(request.getAttribute("serSenhaObrigatoria")) ? (Boolean) request.getAttribute("serSenhaObrigatoria") : false;
    String termoConsentimentoDadosServidor = (String) request.getAttribute("termoConsentimentoDadosServidor");
    boolean enderecoObrigatorio = request.getAttribute("enderecoObrigatorio") != null && request.getAttribute("enderecoObrigatorio").toString().equals("true");
    boolean celularObrigatorio = request.getAttribute("celularObrigatorio") != null && request.getAttribute("celularObrigatorio").toString().equals("true");
    boolean enderecoCelularObrigatorio = request.getAttribute("enderecoCelularObrigatorio") != null && request.getAttribute("enderecoCelularObrigatorio").toString().equals("true");
    boolean isParcela = (boolean) request.getAttribute("isParcela");
    String acaoFormulario = request.getAttribute("acaoFormulario").toString();
    TransferObject adePortabilidade = (TransferObject) request.getAttribute("adePortabilidade");
    boolean reconhecimentoFacialServidorSimulacao = request.getAttribute("exigeReconhecimentoFacil") != null && request.getAttribute("exigeReconhecimentoFacil").equals("true");
	boolean vlrLiberadoOk = (Boolean) request.getAttribute("vlrLiberadoOk");
%>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="title">
    <%= TextHelper.forHtml(request.getAttribute("tituloPagina")) %>
</c:set>
<c:set var="bodyContent">
    <form method="POST"
          action="<%="../v3/comprarConsignacao?acao=incluirReserva&" + SynchronizerToken.generateToken4URL(request)%>"
          name="form1" ENCTYPE="multipart/form-data">
        <%if (reconhecimentoFacialServidorSimulacao && responsavel.isSer()) {%>
        <input type="hidden" id="reconhecimentoFacialServidorSimulacao" name="reconhecimentoFacialServidorSimulacao"
               value="<%=reconhecimentoFacialServidorSimulacao%>">
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
				<% String portabilidade = ApplicationResourcesHelper.getMessage("rotulo.consignacao.origem.compra", responsavel);%>
				<%=ApplicationResourcesHelper.getMessage("mensagem.alerta.alteracao.simulacao", responsavel, portabilidade.toLowerCase())%>
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
                            <dt class="col-6"><hl:message key="rotulo.consignacao.data"/>:</dt> <dd class="col-6"><%=DateHelper.toDateString(DateHelper.getSystemDatetime())%></dd>
                            <dt class="col-6"><hl:message key="rotulo.consignacao.valor.liberado"/>:</dt> <dd class="col-6"><hl:message key="rotulo.moeda"/> <%=!isParcela ? TextHelper.forHtmlContent(NumberHelper.reformat(vlrLiberado, "en", NumberHelper.getLang())) : vlrLiberado%></dd>
                            <dt class="col-6"><hl:message key="rotulo.consignacao.valor.parcela.moeda"/>:</dt> <dd class="col-6"><hl:message key="rotulo.moeda"/> <%=TextHelper.forHtmlContent(NumberHelper.reformat(adeVlr, "en", NumberHelper.getLang()))%> </dd>
                            <dt class="col-6"><hl:message key="rotulo.consignacao.prazo"/>:</dt> <dd class="col-6"><%=TextHelper.forHtmlContent(przVlr)%></dd>
                            <dt class="col-6"><%= ApplicationResourcesHelper.getMessage((temCET ? "rotulo.consignacao.cet" : "rotulo.consignacao.taxa.juros"), responsavel) %>:</dt> <dd class="col-6"><%=TextHelper.forHtmlContent(cftVlr)%></dd>
                            <dt class="col-6"><hl:message key="rotulo.consignacao.data.inicial"/>:</dt> <dd class="col-6"><%=TextHelper.forHtmlContent(dataIni)%></dd>
                            <dt class="col-6"><hl:message key="rotulo.consignacao.data.final"/>:</dt> <dd class="col-6"><%=TextHelper.forHtmlContent(TextHelper.isNull(dataFim) ? ApplicationResourcesHelper.getMessage("rotulo.indeterminado", responsavel) : dataFim)%></dd>
                            <% if (simulacaoMetodoMexicano) { %>
                            <dt class="col-6"><hl:message key="rotulo.consignacao.periodicidade"/>:</dt> <dd class="col-6"><hl:message key="<%=quinzenal ? "rotulo.consignacao.periodicidade.quinzenal" : "rotulo.consignacao.periodicidade.mensal" %>"/></dd>
                            <% } %>
                        </dl>
                    </div>
                </div>
                <div class="card">
                    <div class="card-header">
                        <h2 class="card-header-title"><hl:message
                                key="mensagem.solicitar.portabilidade.consignacao.titulo"/></h2>
                    </div>
                    <div class="card-body">
                        <dl class="row data-list">
                            <dt class="col-6"><hl:message key="rotulo.consignataria.singular"/>:</dt>
                            <dd class="col-6"><%=TextHelper.forHtmlContent(adePortabilidade.getAttribute(Columns.CSA_NOME))%>
                            </dd>
                            <dt class="col-6"><hl:message key="rotulo.servico.singular"/>:</dt>
                            <dd class="col-6"><%=TextHelper.forHtmlContent(adePortabilidade.getAttribute(Columns.SVC_DESCRICAO))%>
                            </dd>
                            <dt class="col-6"><hl:message key="rotulo.consignacao.numero"/>:</dt>
                            <dd class="col-6"><%=TextHelper.forHtmlContent(adePortabilidade.getAttribute(Columns.ADE_NUMERO))%>
                            </dd>
                            <dt class="col-6"><hl:message key="rotulo.consignacao.data"/>:</dt>
                            <dd class="col-6"><%=DateHelper.toDateString((Date) adePortabilidade.getAttribute(Columns.ADE_DATA))%>
                            </dd>
                            <dt class="col-6"><hl:message key="rotulo.consignacao.valor.parcela.moeda"/>:</dt>
                            <dd class="col-6"><hl:message
                                    key="rotulo.moeda"/> <%=NumberHelper.format(((BigDecimal) adePortabilidade.getAttribute(Columns.ADE_VLR)).doubleValue(), NumberHelper.getLang())%>
                            </dd>
                            <dt class="col-6"><hl:message key="rotulo.consignacao.prazo"/>:</dt>
                            <dd class="col-6"><%=adePortabilidade.getAttribute(Columns.ADE_PRAZO) == null ? ApplicationResourcesHelper.getMessage("rotulo.indeterminado", responsavel) : TextHelper.forHtmlContent(adePortabilidade.getAttribute(Columns.ADE_PRAZO).toString())%>
                            </dd>
                            <dt class="col-6"><hl:message key="rotulo.consignacao.pagas"/>:</dt>
                            <dd class="col-6"><%=adePortabilidade.getAttribute(Columns.ADE_PRD_PAGAS) == null ? "0" : TextHelper.forHtmlContent(adePortabilidade.getAttribute(Columns.ADE_PRD_PAGAS).toString())%>
                            </dd>
                            <dt class="col-6"><hl:message key="rotulo.consignacao.data.inicial"/>:</dt>
                            <dd class="col-6"><%=DateHelper.toPeriodString((Date) adePortabilidade.getAttribute(Columns.ADE_ANO_MES_INI))%>
                            </dd>
                            <dt class="col-6"><hl:message key="rotulo.consignacao.data.final"/>:</dt>
                            <dd class="col-6"><%=adePortabilidade.getAttribute(Columns.ADE_ANO_MES_FIM) == null ? ApplicationResourcesHelper.getMessage("rotulo.indeterminado", responsavel) : DateHelper.toPeriodString((Date) adePortabilidade.getAttribute(Columns.ADE_ANO_MES_FIM))%>
                            </dd>
                            <% if (simulacaoMetodoMexicano) { %>
                            <dt class="col-6"><hl:message key="rotulo.consignacao.periodicidade"/>:</dt>
                            <dd class="col-6"><hl:message
                                    key="<%=CodedValues.PERIODICIDADE_FOLHA_QUINZENAL.equals(adePortabilidade.getAttribute(Columns.ADE_PERIODICIDADE)) ? "rotulo.consignacao.periodicidade.quinzenal" : "rotulo.consignacao.periodicidade.mensal" %>"/></dd>
                            <% } %>
                        </dl>
                    </div>
                </div>
            </div>
            <div class="col-sm">
                <hl:confirmarDadosSERv4 serCodigo="<%=TextHelper.forHtmlAttribute(serCodigo)%>"
                                        rseCodigo="<%=TextHelper.forHtmlAttribute(rseCodigo)%>"
                                        csaCodigo="<%=TextHelper.forHtmlAttribute(csaCodigo)%>"/>

                    <%-- Senha do servidor --%>
                <% if (responsavel.isSer() && ParamSist.getBoolParamSist(CodedValues.TPC_SOLICITA_SENHA_AUTORIZACAO_OPE_REALIZADAS_SER, responsavel)) { %>
                <div class="card">
                    <%
                        String mascaraLogin = (String) ParamSist.getInstance().getParam(CodedValues.TPC_MASCARA_LOGIN_EXTERNO_SERVIDOR, responsavel);
                        if (!TextHelper.isNull(mascaraLogin)) {
                    %>
                    <div class="row">
                        <div class="form-group col-sm-12  col-md-12">
                            <label for="serLogin"><hl:message
                                    key="rotulo.usuario.autorizacao.servidor.singular"/><%=serSenhaObrigatoria ? "" : " " + ApplicationResourcesHelper.getMessage("rotulo.campo.opcional", responsavel)%><hl:message
                                    key="rotulo.campo.opcional"/></label>
                            <hl:htmlinput name="serLogin" type="text" classe="form-control" di="serLogin" size="15"
                                          mask="<%=TextHelper.forHtmlAttribute(mascaraLogin)%>"/>
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
                        <span class="card-header-icon"><svg width="26"><use xmlns:xlink="http://www.w3.org/1999/xlink"
                                                                            xlink:href="#i-upload"></use></svg></span>
                        <h2 class="card-header-title"><hl:message
                                key="rotulo.anexo.credito.eletronico.doc.adicional"/></h2>
                    </div>
                    <div class="card-body" id="tabelaAnexosAssinaturaDigital">
                        <div class="alert alert-warning" role="alert"><hl:message
                                key="mensagem.confirmacao.instrucao.anexo.assinatura.digital"/></div>

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
                <% } %>

                <% if (!TextHelper.isNull(msgDadosSerNaoPermitemAlteracao)) { %>
                <div class="alert alert-info" role="alert">
                    <p class="mb-0">
                        <hl:message key="mensagem.dados.servidor.nao.permitem.alteracao"/>
                    </p>
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
    <script type="text/JavaScript">
        f0 = document.forms[0];

        function formLoad() {
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
            mensagem = '<hl:message key="mensagem.confirmacao.solicitacao.portabilidade.consignataria" arg0="<%=TextHelper.forHtmlAttribute(csaNome)%>"/> <%if (responsavel.isSer()) {%><%=ApplicationResourcesHelper.getMessage("mensagem.alerta.envio.solicitacao", responsavel) %> <%} %>';
            document.getElementById("mensagemConfirmacao").innerText = mensagem;
            myModal.show();
        }

        $('#btnEnvia').click(function (event) {
            if (!vf_upload_arquivos() || !vf_valida_dados() || !verificaEmail()) {
                return false;
            }
            mensagemConfirmacao();
        });

        $('#confirmarSimulacao').click(function () {
            incluirReservaNaRenegociacao();
        });

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
                clone.id = "novoUp" + cont; // change id or other attributes/contents
                clone.classList.remove('hide');
                table.appendChild(clone); // add new row to end of table
                document.getElementById("novoUp" + cont).getElementsByClassName("fileToClone")[0].setAttribute('name', "file" + cont)

                cont++
            }
        }

        function incluirReservaNaRenegociacao(){
            var url = '../v3/renegociarConsignacao?acao=incluirReserva'
                + '&titulo=<%=TextHelper.forJavaScriptBlock(java.net.URLEncoder.encode(svcDescricao, "ISO-8859-1"))%>'
                + '&RSE_CODIGO=<%=TextHelper.forJavaScriptBlock(rseCodigo)%>'
                + '&CSA_CODIGO=<%=TextHelper.forJavaScriptBlock(csaCodigo)%>'
                + '&ORG_CODIGO=<%=TextHelper.forJavaScriptBlock(orgCodigo)%>'
                + '&SVC_CODIGO=<%=TextHelper.forJavaScriptBlock(svcCodigo)%>'
                + '&CFT_CODIGO=<%=TextHelper.forJavaScriptBlock(cftCodigo)%>'
                + '&DTJ_CODIGO=<%=TextHelper.forJavaScriptBlock(dtjCodigo)%>'
                + '&adeVlr=<%=TextHelper.forHtmlContent(NumberHelper.reformat(adeVlr, "en", NumberHelper.getLang()))%>'
                + '&adePrazo=<%=TextHelper.forJavaScriptBlock(przVlr)%>'
                + '&adeCarencia=<%=TextHelper.forJavaScriptBlock(carenciaMinPermitida)%>'
                + '&RANKING=<%=TextHelper.forJavaScriptBlock(ranking)%>'
                + '&vlrLiberado=<%=!isParcela ? vlrLiberado.replace(".", ",") : vlrLiberado%>'
                + '&TPC_SOLICITAR_PORTABILIDADE_RANKING=true'
                <% if(adePortabilidade != null){%>
                + '&ADE_CODIGO_PORTABILIDADE=<%=TextHelper.forHtmlAttribute(adePortabilidade.getAttribute(Columns.ADE_CODIGO))%>'
                <% } %>
                + '&SVC_CODIGO_ORIGEM=<%=TextHelper.forJavaScriptBlock(svcCodigo)%>'
                + '&PRZ_VLR=<%=TextHelper.forJavaScriptBlock(przVlr)%>'
                + '&CFT_DIA=<%=TextHelper.forJavaScriptBlock(cftDia)%>';


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


        function vf_upload_arquivos() {
            <%if (!exigeAssinaturaDigital) {%>
            return true;
            <%}%>

            if (!$("#divAnexosAssinaturaDigital").is(":visible")) {
                return true;
            }

            var controles = new Array("file");
            var msgs = new Array('<%=ApplicationResourcesHelper.getMessage("mensagem.erro.upload.arquivo.assinatura.digital.selecione.arquivo", responsavel)%>');

            var ok = ValidaCampos(controles, msgs)

            return ok;
        }

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
    <div class="modal fade" id="dialogErro" tabindex="-1" role="dialog" aria-labelledby="dialogErroLabel"
         aria-hidden="true" title='<hl:message key="mensagem.confirmacao.operacao.sensivel.titulo.modal"/>'>
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-body">
                    <div id="dialogErroLabel" class="alert alert-danger" role="alert"></div>
                </div>
                <div class="modal-footer pt-0">
                    <div class="btn-action mt-2 mb-0">
                        <a class="btn btn-primary" data-bs-dismiss="modal"
                           aria-label='<hl:message key="rotulo.botao.ok"/>' href="#"
                           title="<hl:message key="rotulo.botao.ok"/>">
                            <hl:message key="rotulo.botao.ok"/>
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <%-- Modal: Termo de Consentimento de coleta de dados do servidor --%>
    <% if (!TextHelper.isNull(termoConsentimentoDadosServidor)) { %>
    <div class="modal fade" id="confirmarTermoUso" tabindex="-1" role="dialog" aria-labelledby="modalTitulo"
         aria-hidden="true" style="display: none;">
        <div class="modal-dialog modalTermoUso" role="document">
            <div class="modal-content">
                <div class="modal-header pb-0">
                    <h5 class="modal-title about-title mb-0" id="modalTitulo"><hl:message
                            key="mensagem.termo.de.consentimento.coleta.dados.servidor.titulo"/></h5>
                    <button type="button" class="logout mr-2" data-bs-dismiss="modal"
                            aria-label='<hl:message key="rotulo.botao.fechar"/>'>
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
                        <a class="btn btn-outline-danger" data-bs-dismiss="modal"
                           aria-label='<hl:message key="rotulo.botao.fechar"/>' href="#"
                           alt="<hl:message key="rotulo.botao.fechar"/>"
                           title="<hl:message key="rotulo.botao.fechar"/>">
                            <hl:message key="rotulo.botao.fechar"/>
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <% } %>
</c:set>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:attribute name="pageModals">${pageModals}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>