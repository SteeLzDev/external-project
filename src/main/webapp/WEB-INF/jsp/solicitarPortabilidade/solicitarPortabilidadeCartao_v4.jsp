<%--
* <p>Title: solicitarPortabilidadeCartao.jsp</p>
* <p>Description: Página de solicitação de portabilidade de cartão de crédito</p>
* <p>Copyright: Copyright (c) 2024</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.periodo.PeriodoHelper" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ page import="com.zetra.econsig.dto.TransferObject" %>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject" %>
<%@ page import="com.zetra.econsig.helper.margem.MargemDisponivel" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession"/>
<%
    AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

    CustomTransferObject servidor = (CustomTransferObject) request.getAttribute("servidor");
    MargemDisponivel margemDisponivel = (MargemDisponivel) request.getAttribute("margemDisponivel");
    BigDecimal margemConsignavel = (BigDecimal) request.getAttribute("margemConsignavel");
    String adeCodigo = (String) request.getAttribute("adeCodigo");
    String rseCodigo = (String) request.getAttribute("rseCodigo");
    String cnvCodigo = (String) request.getAttribute("cnvCodigo");
    String svcCodigo = (String) request.getAttribute("svcCodigo");
    String csaCodigo = (String) request.getAttribute("csaCodigo");

    Short intFolha = (Short) request.getAttribute("intFolha");
    Short incMargem = (Short) request.getAttribute("incMargem");
    String tipoVlr = (String) request.getAttribute("tipoVlr");
    Boolean permiteVlrNegativo = (Boolean) request.getAttribute("permiteVlrNegativo");
    String vlrLimite = (String) request.getAttribute("vlrLimite");
    String txtExplicativo = (String) request.getAttribute("txtExplicativo");
    Boolean exibeAlertaMsgPertenceCategoria = (Boolean) request.getAttribute("exibeAlertaMsgPertenceCategoria");
    String msgPertenceCategoria = (String) request.getAttribute("msgPertenceCategoria");

    String rseMatricula = (String) request.getAttribute("rseMatricula");

    boolean exigeCaptcha = request.getAttribute("exigeCaptcha") != null && (boolean) request.getAttribute("exigeCaptcha");
    boolean exibeCaptchaAvancado = request.getAttribute("exibeCaptchaAvancado") != null && (boolean) request.getAttribute("exibeCaptchaAvancado");
    boolean exibeCaptchaDeficiente = request.getAttribute("exibeCaptchaDeficiente") != null && (boolean) request.getAttribute("exibeCaptchaDeficiente");

    List<TransferObject> lstTipoDadoAdicional = (List<TransferObject>) request.getAttribute("lstTipoDadoAdicional");
    Map<String, String> dadosAutorizacao = (Map<String, String>) request.getAttribute("dadosAutorizacao");
    String termoConsentimentoDadosServidor = (String) request.getAttribute("termoConsentimentoDadosServidor");

    boolean enderecoObrigatorio = request.getAttribute("enderecoObrigatorio") != null && request.getAttribute("enderecoObrigatorio").toString().equals("true");
    boolean celularObrigatorio = request.getAttribute("celularObrigatorio") != null && request.getAttribute("celularObrigatorio").toString().equals("true");
    boolean enderecoCelularObrigatorio = request.getAttribute("enderecoCelularObrigatorio") != null && request.getAttribute("enderecoCelularObrigatorio").toString().equals("true");
    TransferObject adePortabilidade = (TransferObject) request.getAttribute("adePortabilidade");


    boolean portalBeneficio = request.getAttribute("portalBeneficio") != null;
    String corCodigoPortal = (String) request.getAttribute("corCodigo");

    HashMap<String, TransferObject> hashCsaPermiteContato = (HashMap<String, TransferObject>) request.getAttribute("hashCsaPermiteContato");
    String csa_whatsapp = "", csa_email_contato = "", csa_email = "", csa_email_usar = "", csa_contato_tel = "", tipo_contato = "";
    if (hashCsaPermiteContato.get(csaCodigo) != null) {
        TransferObject consignatariaContato = hashCsaPermiteContato.get(csaCodigo);
        csa_whatsapp = (String) consignatariaContato.getAttribute(Columns.CSA_WHATSAPP);
        csa_email_contato = (String) consignatariaContato.getAttribute(Columns.CSA_EMAIL_CONTATO);
        csa_email = (String) consignatariaContato.getAttribute(Columns.CSA_EMAIL);
        tipo_contato = (String) consignatariaContato.getAttribute(Columns.PCS_VLR);
        csa_email_usar = !TextHelper.isNull(csa_email_contato) ? csa_email_contato : csa_email;
        if (!TextHelper.isNull(csa_whatsapp)) {
            csa_whatsapp = LocaleHelper.formataCelular(csa_whatsapp);
        }
        csa_contato_tel = (String) consignatariaContato.getAttribute(Columns.CSA_TEL);
    }

    boolean anexoObrigatorio = request.getAttribute("anexoObrigatorio") != null && (boolean) request.getAttribute("anexoObrigatorio");
    String qtdeMinAnexos = (String) request.getAttribute("qtdeMinAnexos");

    boolean temCET = request.getAttribute("temCET") != null && (Boolean) request.getAttribute("temCET");
    String taxa = NumberHelper.format(((BigDecimal) request.getAttribute("coeficienteValor")).doubleValue(), NumberHelper.getLang(), 2, 8);
%>
<c:set var="title">
    <%=TextHelper.forHtmlContent(request.getAttribute("tituloPagina"))%>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
    <form action="../v3/solicitarPortabilidade" method="post"
          name="form1">
        <%= SynchronizerToken.generateHtmlToken(request) %>
        <hl:htmlinput type="hidden" name="acao" value="confirmarCartao"/>
        <div class="row">
            <div class="col-sm-5">
                <div class="card">
                    <div class="card-header">
                        <h2 class="card-header-title"><hl:message key="rotulo.confirmar.dados.titulo"/></h2>
                        <% if (exigeCaptcha) { %>
                        <span class="card-header-icon-ocultar-margem-ser">
                          <a href="#" onclick="exibirmargem()" id="olhoMargemOculto">
                        <svg width="30" height="30" class="icon-oculta-margem-simu">
                            <use xlink:href="#i-eye-slash-regular"></use>
                        </svg>
                    </a>
                    </span>
                        <% } %>
                    </div>
                    <div class="card-body">
                        <dl class="row data-list">
                            <% if (request.getAttribute("csaNome") != null) { %>
                            <dt class="col-6"><hl:message key="rotulo.consignataria.singular"/>:</dt>
                            <dd class="col-6"><%=TextHelper.forHtmlContent(request.getAttribute("csaNome"))%>
                            </dd>
                            <% } %>
                            <% if (request.getAttribute("exibirValorMargem") != null) { /* Mostra a Margem */ %>
                            <% if (margemDisponivel.getMargemRestanteDependente() == null) { %>
                            <dt class="col-6"><hl:message key="rotulo.reservar.margem.disponivel"/>:</dt>
                            <dd class="col-6"><%=TextHelper.forHtmlContent(ParamSvcTO.getDescricaoTpsTipoVlr(margemDisponivel.getTipoVlr()))%>&nbsp;<%=TextHelper.forHtmlContent(margemDisponivel.getExibeMargem().isSemRestricao() || margemConsignavel.doubleValue() > 0 ? NumberHelper.format(margemConsignavel.doubleValue(), NumberHelper.getLang()) : "0,00")%>
                            </dd>
                            <% } else { %>
                            <dt class="col-6"><%=TextHelper.forHtmlContent(margemDisponivel.getMarDescricao())%>:</dt>
                            <dd class="col-6"><%=TextHelper.forHtmlContent(ParamSvcTO.getDescricaoTpsTipoVlr(margemDisponivel.getTipoVlr()))%>&nbsp;<%=TextHelper.forHtmlContent(margemDisponivel.getExibeMargem().isSemRestricao() || margemConsignavel.doubleValue() > 0 ? NumberHelper.format(margemConsignavel.doubleValue(), NumberHelper.getLang()) : "0,00")%>
                            </dd>
                            <dt class="col-6"><%=TextHelper.forHtmlContent(margemDisponivel.getMarDescricaoDependente())%>
                                :
                            </dt>
                            <dd class="col-6"><%=TextHelper.forHtmlContent(ParamSvcTO.getDescricaoTpsTipoVlr(margemDisponivel.getTipoVlr()))%>&nbsp;<%=TextHelper.forHtmlContent(margemDisponivel.getExibeMargem().isSemRestricao() || margemDisponivel.getMargemRestanteDependente().doubleValue() > 0 ? NumberHelper.format(margemDisponivel.getMargemRestanteDependente().doubleValue(), NumberHelper.getLang()) : "0,00")%>
                            </dd>
                            <% } %>
                            <% } else if (exigeCaptcha) { %>
                            <dt class="col-6"><hl:message key="rotulo.reservar.margem.disponivel"/>:</dt>
                            <dd class="col-6"><hl:message key="rotulo.margem.moeda"/><hl:message
                                    key="rotulo.margem.disponivel.codigo"/></dd>
                            <% } %>
                            <% if (request.getAttribute("plaDescricao") != null) { %>
                            <dt class="col-6"><hl:message key="rotulo.plano.singular"/>:</dt>
                            <dd class="col-6"><%=TextHelper.forHtmlContent(request.getAttribute("plaDescricao"))%>
                            </dd>
                            <% } %>
                            <% if (request.getAttribute("cnvDescricao") != null) { %>
                            <dt class="col-6"><hl:message key="rotulo.servico.singular"/>:&nbsp;</dt>
                            <dd class="col-6"><%=TextHelper.forHtmlContent(request.getAttribute("cnvDescricao"))%>
                            </dd>
                            <% } %>
                            <dt class="col-6">
                                <% if (temCET) { %>
                                    <hl:message key="rotulo.consignacao.cet"/>:
                                <% } else { %>
                                    <hl:message key="rotulo.consignacao.taxa.juros"/>:
                                <% } %>
                                </dt>
                                <dd class="col-6">
                                 <%=taxa%>%
                                </dd>
                            <%if (!TextHelper.isNull(tipo_contato) && hashCsaPermiteContato.get(csaCodigo) != null) { %>
                            <dt class="col-6"><hl:message key="rotulo.consignataria.contato.qr.code"/>:</dt>
                            <dd class="col-6">
                                <%if (!TextHelper.isNull(csa_whatsapp) && CodedValues.TPA_CONTATOS_WHATSAPP.contains(tipo_contato)) { %>
                                <i class="fa fa-whatsapp icon-contato-ranking"
                                   onclick="openModalQRCode(1,'<%=csa_whatsapp%>')"></i>
                                <%} %>
                                <%if (!TextHelper.isNull(csa_email_usar) && CodedValues.TPA_CONTATOS_EMAIL.contains(tipo_contato)) { %>
                                <i class="fa fa-at icon-contato-ranking"
                                   onclick="openModalQRCode(2,'<%=csa_email_usar%>')"></i>
                                <%} %>
                                <%if (!TextHelper.isNull(csa_contato_tel) && CodedValues.TPA_CONTATOS_TELEFONE.contains(tipo_contato)) { %>
                                <i class="fa fa-phone icon-contato-ranking"
                                   onclick="openModalQRCode(3,'<%=csa_contato_tel%>')"></i>
                                <%} %>
                            </dd>
                            <% } %>
                        </dl>
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
                            </dl>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-sm">
                <div class="card">
                    <div class="card-header hasIcon">
                        <span class="card-header-icon"><svg width="26"><use
                                xlink:href="#i-consignacao"></use></svg></span>
                        <h2 class="card-header-title"><hl:message key="rotulo.dados.consignacao.titulo"/></h2>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="form-group col-sm-12">
                                <label for="adeVlr"><hl:message key="rotulo.consignacao.valor.parcela"/>
                                    <%if (!txtExplicativo.isEmpty()) { %>
                                    <%=TextHelper.forHtmlContent(txtExplicativo)%>
                                    <% } %>
                                    (<%=TextHelper.forHtmlContent(ParamSvcTO.getDescricaoTpsTipoVlr(tipoVlr))%>)
                                </label>
                                <hl:htmlinput name="adeVlr"
                                              type="text"
                                              classe="form-control"
                                              di="adeVlr"
                                              size="8"
                                              mask="#F11"
                                              onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                                              value="<%=TextHelper.forHtmlAttribute(request.getParameter("adeVlr") != null ? request.getParameter("adeVlr") : "" )%>"
                                              placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.digite.valor.parcela", responsavel)%>'
                                />
                            </div>
                        </div>
                        <hl:htmlinput type="hidden" name="adePeriodicidade" di="adePeriodicidade"
                                      value="<%=TextHelper.forHtmlAttribute(PeriodoHelper.getPeriodicidadeFolha(responsavel))%>"/>
                        <div class="row">
                            <div id="adePrzInd" class="adeSemPrazo form-group col-sm-4 col-md-6">
                      <span class="text-nowrap align-text-top">
                        <input class="form-check-input ml-1" type="checkbox" name="adeSemPrazo" id="adeSemPrazo"
                               onFocus="SetarEventoMascara(this,'#*200',true);"
                               onBlur="fout(this);ValidaMascara(this);" value="1" checked disabled>
                        <label class="form-check-label labelSemNegirto ml-1"
                               aria-label='<hl:message key="rotulo.consignacao.prazo.indeterminado"/>'
                               for="adeSemPrazo"><hl:message key="rotulo.consignacao.prazo.indeterminado"/></label>
                      </span>
                            </div>
                        </div>
                        <% if (request.getAttribute("lstPeriodos") != null) { %>
                        <div class="row">
                            <div class="form-group col-sm-12">
                                <label for="ocaPeriodo"><hl:message key="rotulo.folha.periodo"/></label>
                                <select name="ocaPeriodo" id="ocaPeriodo" class="form-control form-select"
                                        onFocus="SetarEventoMascara(this,'#*200',true);"
                                        onBlur="fout(this);ValidaMascara(this);">
                                    <% for (Date periodo : (Set<Date>) request.getAttribute("lstPeriodos")) { %>
                                    <option value="<%=TextHelper.forHtmlAttribute(periodo)%>"><%=TextHelper.forHtmlContent(DateHelper.toPeriodString(periodo))%>
                                    </option>
                                    <% } %>
                                </select>
                            </div>
                        </div>
                        <% } %>
                        <hl:htmlinput name="adeCarencia" di="adeCarencia" type="hidden"
                                      value="<%=request.getParameter("adeCarencia") != null ? TextHelper.forHtmlAttribute(request.getParameter("adeCarencia").toString()) : "0"%>"/>
                        <% if (lstTipoDadoAdicional != null) { %>
                        <% for (TransferObject tda : lstTipoDadoAdicional) { %>
                        <hl:paramv4
                                prefixo="TDA_"
                                descricao="<%=TextHelper.forHtmlAttribute(tda.getAttribute(Columns.TDA_DESCRICAO))%>"
                                codigo="<%=(String) tda.getAttribute(Columns.TDA_CODIGO)%>"
                                dominio="<%=(String) tda.getAttribute(Columns.TDA_DOMINIO)%>"
                                valor="<%= dadosAutorizacao != null && dadosAutorizacao.get((String) tda.getAttribute(Columns.TDA_CODIGO)) != null ? dadosAutorizacao.get((String) tda.getAttribute(Columns.TDA_CODIGO)) : "" %>"
                                desabilitado="<%= CodedValues.CAS_BLOQUEADO.equals(tda.getAttribute(Columns.SPT_EXIBE)) %>"
                        />
                        <% } %>
                        <% } %>

                        <% if (request.getAttribute("processaReservaMargem") != null) { %>
                        <%=TextHelper.forHtmlContent(request.getAttribute("processaReservaMargem"))%>
                        <% } %>

                            <%--                 TODO Anexo de arquivo	--%>
                        <%
                            if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_ANEXO_INCLUSAO_CONTRATOS, CodedValues.TPC_SIM, responsavel) &&
                                    responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO)) {
                        %>
                        <hl:fileUploadV4 obrigatorio="<%=(boolean)anexoObrigatorio%>" tipoArquivo="anexo_consignacao"/>
                        <%
                            }
                        %>
                    </div>
                </div>
                <%
                    if (!TextHelper.isNull(termoConsentimentoDadosServidor)) {
                %>
                <div>
                    <p>
                 <span class="info" style="display: block;">
                    <input type="checkbox" name="aceitoTermoUsoColetaDados" id="aceitoTermoUsoColetaDados" value="S"/>
                    <hl:message key="mensagem.termo.de.consentimento.coleta.dados.servidor.aceito"/>&nbsp;<a href="#"
                                                                                                             data-bs-toggle="modal"
                                                                                                             data-bs-target="#confirmarTermoUso"><hl:message
                         key="mensagem.termo.de.consentimento.coleta.dados.servidor.link"/></a>
                 </span>
                    </p>
                </div>
                <%
                    }
                %>
            </div>
            <hl:htmlinput type="hidden" di="CSA_CODIGO" name="CSA_CODIGO"
                          value="<%=TextHelper.forHtmlAttribute(csaCodigo)%>"/>
            <hl:htmlinput type="hidden" name="CNV_CODIGO" value="<%=TextHelper.forHtmlAttribute(cnvCodigo)%>"/>
            <hl:htmlinput type="hidden" di="SVC_CODIGO" name="SVC_CODIGO"
                          value="<%=TextHelper.forHtmlAttribute(svcCodigo)%>"/>
            <hl:htmlinput type="hidden" di="RSE_CODIGO" name="RSE_CODIGO"
                          value="<%=TextHelper.forHtmlAttribute(rseCodigo)%>"/>
            <hl:htmlinput type="hidden" di="ADE_CODIGO" name="ADE_CODIGO" value="<%=adeCodigo%>"/>
            <hl:htmlinput type="hidden" name="adePrazo"
                          value="<%=TextHelper.forHtmlAttribute("0")%>"/>
            <hl:htmlinput type="hidden" name="adeIntFolha" value="<%=TextHelper.forHtmlAttribute((intFolha))%>"/>
            <hl:htmlinput type="hidden" name="adeIncMargem" value="<%=TextHelper.forHtmlAttribute((incMargem))%>"/>
            <hl:htmlinput type="hidden" name="adeTipoVlr" value="<%=TextHelper.forHtmlAttribute(tipoVlr)%>"/>
            <hl:htmlinput type="hidden" name="vlrLimite" value="<%=TextHelper.forHtmlAttribute(vlrLimite)%>"/>
            <hl:htmlinput type="hidden" name="rsePrazo"
                          value="<%=TextHelper.forHtmlAttribute(servidor.getAttribute(Columns.RSE_PRAZO))%>"/>
            <hl:htmlinput type="hidden" name="rseMatricula" value="<%=TextHelper.forHtmlAttribute(rseMatricula)%>"/>
            <hl:htmlinput type="hidden" name="nomeDependente" di="nomeDependente"/>
            <%
                if (portalBeneficio && !TextHelper.isNull(corCodigoPortal)) {
            %>
            <hl:htmlinput type="hidden" name="COR_CODIGO" value="<%=TextHelper.forHtmlAttribute(corCodigoPortal)%>"/>
            <hl:htmlinput type="hidden" name="PORTAL_BENEFICIO"
                          value="<%=TextHelper.forHtmlAttribute(portalBeneficio)%>"/>
            <%
                }
            %>
            <%
                if (enderecoObrigatorio) {
            %>
            <hl:htmlinput type="hidden" name="enderecoObrigatorio"
                          value="<%=TextHelper.forHtmlAttribute(enderecoObrigatorio)%>"/>
            <%
                }
            %>
            <%
                if (celularObrigatorio) {
            %>
            <hl:htmlinput type="hidden" name="celularObrigatorio"
                          value="<%=TextHelper.forHtmlAttribute(celularObrigatorio)%>"/>
            <%
                }
            %>
            <%
                if (enderecoCelularObrigatorio) {
            %>
            <hl:htmlinput type="hidden" name="enderecoCelularObrigatorio"
                          value="<%=TextHelper.forHtmlAttribute(enderecoCelularObrigatorio)%>"/>
            <%
                }
            %>
        </div>
        <%
            if (exigeCaptcha) {
        %>
        <hl:modalCaptchaSer type="portabilidadeCartao"/>
        <%
            }
        %>
        <div class="btn-action">
            <a class="btn btn-outline-danger" href="#no-back"
               onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"
               alt="<hl:message key="rotulo.botao.voltar"/>" title="<hl:message key="rotulo.botao.voltar"/>"><hl:message
                    key="rotulo.botao.voltar"/></a>
            <a class="btn btn-primary" href="#no-back"
               onClick=" if(exibeMensagemCategoria('<%=msgPertenceCategoria%>') && verificaAnexo() && (campos() && verificaPrazo() && vf_reservar_margem('<%=TextHelper.forJavaScriptAttribute("")%>', <%=(boolean) permiteVlrNegativo%>)) && verificarCamposAdicionais() && verificaTermoConsentimento()){enableAllCustom(); f0.submit();} return false;"
               alt="<hl:message key="rotulo.botao.confirmar"/>" title="<hl:message key="rotulo.botao.confirmar"/>">
                <svg width="17">
                    <use xlink:href="#i-confirmar"></use>
                </svg>
                <hl:message key="rotulo.botao.confirmar"/></a>
        </div>
    </form>
</c:set>
<c:set var="javascript">
    <%
        if (request.getAttribute("exibeInformacaoCsaServidor") != null) {
    %>
    <link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-buttons-dt/css/buttons.dataTables.min.css"/>
    <link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-responsive-bs5/css/responsive.bootstrap5.min.css"/>
    <link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-bs5/css/dataTables.bootstrap5.min.css"/>
    <script  src="../node_modules/jszip/dist/jszip.min.js"></script>
  	<script  src="../node_modules/pdfmake/build/pdfmake.min.js"></script>
  	<script  src="../node_modules/pdfmake/build/vfs_fonts.js"></script>
  	<script  src="../node_modules/datatables.net/js/jquery.dataTables.min.js"></script>
  	<script  src="../node_modules/datatables.net-bs5/js/dataTables.bootstrap5.min.js"></script>
  	<script  src="../node_modules/datatables.net-buttons/js/dataTables.buttons.min.js"></script>
  	<script  src="../node_modules/datatables.net-buttons/js/buttons.colVis.min.js"></script>
  	<script  src="../node_modules/datatables.net-buttons/js/buttons.html5.min.js"></script>
  	<script  src="../node_modules/datatables.net-buttons/js/buttons.print.min.js"></script>
  	<script  src="../node_modules/datatables.net-responsive/js/dataTables.responsive.min.js"></script>
  	<script  src="../node_modules/moment/min/moment.min.js"></script>
  	<script  src="../node_modules/datatables.net-plugins/sorting/datetime-moment.js"></script>
    <script type="text/JavaScript" src="../js/javacrypt.js?<hl:message key="release.tag"/>"></script>

    <%
        }
    %>
    <link rel="stylesheet" type="text/css" href="../viewer/css/viewer.css"/>
    <script src="../viewer/js/viewer.min.js"></script>
    <script type="text/JavaScript" src="../js/javacrypt.js?<hl:message key="release.tag"/>"></script>
    <%
        if (exibeCaptchaAvancado) {
    %>
    <script src='https://www.google.com/recaptcha/api.js'></script>
    <%
        }
    %>
    <hl:fileUploadV4 scriptOnly="true" tipoArquivo="anexo_consignacao"/>
    <script type="text/JavaScript">
        f0 = document.forms[0];

        function formLoad() {
            focusFirstField();
        }

        window.onload = formLoad;

        function enableAllCustom() {
            if (document.forms[0] != null) {
                for (var i = 0; (i < document.forms[0].elements.length); i++) {
                    var e = document.forms[0].elements[i];
                    if (e.type != 'button' && e.type != 'hidden' &&
                        e.type != 'image' && e.type != 'reset' &&
                        e.type != 'submit') {
                        if (e.disabled && e.id.slice(-1) != '_') {
                            e.disabled = false;
                        }
                    }
                }
            }
        }

        function campos() {
            if (f0.adeVlrLiquido != null && f0.adeVlrLiquido.disabled == false) {
                if (f0.adeVlrLiquido.value == '') {
                    alert('<hl:message key="mensagem.informe.ade.valor.liberado"/>');
                    f0.adeVlrLiquido.focus();
                    return false;
                }
            }


            if ((<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_TELEFONE, responsavel)%> || (<%=ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_TELEFONE, responsavel)%>
                && (<%=celularObrigatorio%> || <%=enderecoCelularObrigatorio%>))
        ) &&
            f0.SER_TEL != null && trim(f0.SER_TEL.value) == ''
        )
            {
                alert('<hl:message key="mensagem.informe.servidor.telefone"/>');
                f0.SER_TEL.focus();
                return false;
            }
            if ((<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_LOGRADOURO, responsavel)%> || (<%=ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_LOGRADOURO, responsavel)%>
                && (<%=enderecoObrigatorio%> || <%=enderecoCelularObrigatorio%>))
        ) &&
            f0.SER_END != null && trim(f0.SER_END.value) == ''
        )
            {
                alert('<hl:message key="mensagem.informe.servidor.logradouro"/>');
                f0.SER_END.focus();
                return false;
            }
            if ((<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NRO, responsavel)%> || (<%=ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NRO, responsavel)%>
                && (<%=enderecoObrigatorio%> || <%=enderecoCelularObrigatorio%>))
        ) &&
            f0.SER_NRO != null && trim(f0.SER_NRO.value) == ''
        )
            {
                alert('<hl:message key="mensagem.informe.servidor.numero"/>');
                f0.SER_NRO.focus();
                return false;
            }
            if (<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_COMPLEMENTO, responsavel)%> &&
            f0.SER_COMPL != null && trim(f0.SER_COMPL.value) == ''
        )
            {
                alert('<hl:message key="mensagem.informe.servidor.complemento"/>');
                f0.SER_COMPL.focus();
                return false;
            }
            if ((<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_BAIRRO, responsavel)%> || (<%=ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_BAIRRO, responsavel)%>
                && (<%=enderecoObrigatorio%> || <%=enderecoCelularObrigatorio%>))
        ) &&
            f0.SER_BAIRRO != null && trim(f0.SER_BAIRRO.value) == ''
        )
            {
                alert('<hl:message key="mensagem.informe.servidor.bairro"/>');
                f0.SER_BAIRRO.focus();
                return false;
            }
            if ((<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CIDADE, responsavel)%> || (<%=ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CIDADE, responsavel)%>
                && (<%=enderecoObrigatorio%> || <%=enderecoCelularObrigatorio%>))
        ) &&
            f0.SER_CIDADE != null && trim(f0.SER_CIDADE.value) == ''
        )
            {
                alert('<hl:message key="mensagem.informe.servidor.cidade"/>');
                f0.SER_CIDADE.focus();
                return false;
            }
            if ((<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CEP, responsavel)%> || (<%=ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CEP, responsavel)%>
                && (<%=enderecoObrigatorio%> || <%=enderecoCelularObrigatorio%>))
        ) &&
            f0.SER_CEP != null && trim(f0.SER_CEP.value) == ''
        )
            {
                alert('<hl:message key="mensagem.informe.servidor.cep"/>');
                f0.SER_CEP.focus();
                return false;
            }
            if ((<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_UF, responsavel)%> || (<%=ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_UF, responsavel)%>
                && (<%=enderecoObrigatorio%> || <%=enderecoCelularObrigatorio%>))
        ) &&
            f0.SER_UF != null && trim(f0.SER_UF.value) == ''
        )
            {
                alert('<hl:message key="mensagem.informe.servidor.estado"/>');
                f0.SER_UF.focus();
                return false;
            }
            if ((<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CELULAR, responsavel)%> || (<%=ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CELULAR, responsavel)%>
                && (<%=celularObrigatorio%> || <%=enderecoCelularObrigatorio%>))
        ) &&
            f0.SER_CEL != null && trim(f0.SER_CEL.value) == ''
        )
            {
                alert('<hl:message key="mensagem.informe.servidor.celular"/>');
                f0.SER_CEL.focus();
                return false;
            }
            return true;
        }

        function exibeMensagemCategoria(msg) {
            <%if(exibeAlertaMsgPertenceCategoria){%>
            if (confirm(msg)) {
                return true;
            } else {
                return false;
            }
            <%} else { %>
            return true;
            <%}%>

        }

        function verificaAnexo() {
            <%if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_ANEXO_INCLUSAO_CONTRATOS, CodedValues.TPC_SIM, responsavel) &&
                        responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO) && anexoObrigatorio) {%>
            if (document.getElementById('FILE1').value == '') {
                alert('<hl:message key="mensagem.informe.arquivo.upload"/>');
                return false;
            }

            <%if(!TextHelper.isNull(qtdeMinAnexos) && Integer.valueOf(qtdeMinAnexos) > 0){%>
            let elemento = document.getElementById("pic-progress-wrap-FILE1");
            let qtdeMin = <%=qtdeMinAnexos%>;
            if (elemento == null || elemento == 'undefined' || elemento.childNodes.length < qtdeMin) {
                alert('<%=ApplicationResourcesHelper.getMessage("mensagem.erro.upload.arquivo.qunt.min", responsavel, qtdeMinAnexos)%>');
                return false;
            }
            <%}%>
            <% } %>
            return true;
        }

        function exibirmargem() {
            <% if (exibeCaptchaDeficiente) { %>
            montaCaptchaSomSer('portabilidadeCartao');
            <% } %>
            $('#modalCaptcha_portabilidadeCartao').modal('show');
        }

        function verificarCamposAdicionais() {
            <% if (lstTipoDadoAdicional != null) { %>
            <% for (TransferObject tda : lstTipoDadoAdicional) { %>
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

    </script>
</c:set>
<c:set var="pageModals">
    <t:modalSubAcesso>
        <jsp:attribute name="titulo"><hl:message key="rotulo.historico.liq.antecipada.acao"/></jsp:attribute>
    </t:modalSubAcesso>

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
                        <span aria-hidden="true">x</span>
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
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="pageModals">${pageModals}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>