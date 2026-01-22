<%--
* <p>Title: editarRegrasLimiteOperacao_v4</p>
* <p>Description: Editar/Criar Regras Limite Operacao v4</p>
* <p>Copyright: Copyright (c) 2024</p>
* <p>Company: ZetraSoft Internet Service</p>
  Created by IntelliJ IDEA.
  User: douglas.neves
  Date: 16/09/2024
  Time: 13:30
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.TransferObject" %>
<%@ page import="java.util.List" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession"/>
<%
    boolean exibeBotaoRodape = request.getAttribute("exibeBotaoRodape") != null && (boolean) (request.getAttribute("exibeBotaoRodape"));
    AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
    Boolean novo = (Boolean) request.getAttribute("novo");
    TransferObject regraLimiteOperacao = (TransferObject) request.getAttribute("regraLimiteOperacao");
    List<TransferObject> orgaos = (List) request.getAttribute("orgaos");
    List<TransferObject> consignatarias = (List) request.getAttribute("consignatarias");
    List<TransferObject> funcoes = (List) request.getAttribute("funcoes");
    List<TransferObject> servicos = (List) request.getAttribute("servicos");
    List<TransferObject> correspondentes = (List) request.getAttribute("correspondentes");
    List<TransferObject> subOrgaos = (List) request.getAttribute("subOrgaos");
    List<TransferObject> unidades = (List) request.getAttribute("unidades");
    List<TransferObject> estabelecimentos = (List) request.getAttribute("estabelecimentos");
    List<TransferObject> naturezaServicos = (List) request.getAttribute("naturezaServicos");
    List<TransferObject> naturezaConsignatarias = (List) request.getAttribute("naturezaConsignatarias");
    List<TransferObject> cargosRegistroServidor = (List) request.getAttribute("cargosRegistroServidor");
    List<TransferObject> capacidadesRegistroSer = (List) request.getAttribute("capacidadesRegistroSer");
    List<TransferObject> padroesRegistroSer = (List) request.getAttribute("padroesRegistroSer");
    List<TransferObject> postosRegistroSer = (List) request.getAttribute("postosRegistroSer");
    List<TransferObject> statusRegistroSer = (List) request.getAttribute("statusRegistroSer");
    List<TransferObject> tiposRegistroSer = (List) request.getAttribute("tiposRegistroSer");
    List<TransferObject> vinculosRegistroSer = (List) request.getAttribute("vinculosRegistroSer");
    String rloCodigo = !novo ? (String) regraLimiteOperacao.getAttribute(Columns.RLO_CODIGO) : null;
%>
<c:set var="title">
    <% if (novo) { %>
    <hl:message key="rotulo.regra.limite.operacao.inclusao"/>
    <%} else {%>
    <hl:message key="rotulo.regra.limite.operacao.editar"/>
    <%} %>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
    <form method="post"
          action="../v3/regrasLimiteOperacao?acao=salvar&NOVO=<%=novo ? "S" : "N&RLO_CODIGO="+ rloCodigo%>&<%=SynchronizerToken.generateToken4URL(request)%>&_skip_history_=true"
          name="form1" id="form1">
        <div class="card">
            <div class="card-header">
                <h3 class="card-header-title">
                    <% if (novo) { %>
                    <hl:message key="rotulo.regra.limite.operacao.inclusao"/>
                    <% } else { %>
                    <hl:message key="rotulo.regra.limite.operacao.editar"/>
                    <% } %>
                </h3>
            </div>
            <div class="card-body">
                <div class="row">
                    <% if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_ESTABELECIMENTO, responsavel)) { %>
                    <div class="form-group col-sm">
                        <label for="estCodigo"><hl:message
                                key="rotulo.estabelecimento.singular"/></label>
                        <%=JspHelper.geraCombo(estabelecimentos, "estCodigo", Columns.EST_CODIGO, Columns.EST_NOME, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, regraLimiteOperacao.getAttribute(Columns.RLO_EST_CODIGO) != null ? (String) regraLimiteOperacao.getAttribute(Columns.RLO_EST_CODIGO) : null, null, false, "form-control")%>
                    </div>
                    <% } %>
                    <% if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_ORGAO, responsavel)) { %>
                    <div class="form-group col-sm">
                        <label for="orgCodigo"><hl:message
                                key="rotulo.orgao.singular"/></label>
                        <%=JspHelper.geraCombo(orgaos, "orgCodigo", Columns.ORG_CODIGO, Columns.ORG_NOME + ";" + Columns.ORG_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), "onChange=\"carregaDadosFiltro('orgSbo');\"", false, 1, regraLimiteOperacao.getAttribute(Columns.RLO_ORG_CODIGO) != null ? (String) regraLimiteOperacao.getAttribute(Columns.RLO_ORG_CODIGO) : null, null, false, "form-control")%>
                    </div>
                    <% } %>
                    <% if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_SUBORGAO, responsavel)) { %>
                    <div class="form-group col-sm">
                        <label for="sboCodigo"><hl:message
                                key="rotulo.suborgao.singular"/></label>
                        <%=JspHelper.geraCombo(subOrgaos, "sboCodigo", Columns.SBO_CODIGO, Columns.SBO_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, regraLimiteOperacao.getAttribute(Columns.RLO_SBO_CODIGO) != null ? (String) regraLimiteOperacao.getAttribute(Columns.RLO_SBO_CODIGO) : null, null, false, "form-control")%>
                    </div>
                    <% } %>
                </div>
                <div class="row">
                    <% if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_UNIDADE, responsavel)) { %>
                    <div class="form-group col-sm">
                        <label for="uniCodigo"><hl:message
                                key="rotulo.unidade.singular"/></label>
                        <%=JspHelper.geraCombo(unidades, "uniCodigo", Columns.UNI_CODIGO, Columns.UNI_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, regraLimiteOperacao.getAttribute(Columns.RLO_UNI_CODIGO) != null ? (String) regraLimiteOperacao.getAttribute(Columns.RLO_UNI_CODIGO) : null, null, false, "form-control")%>
                    </div>
                    <% } %>
                    <% if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_NATUREZA_CSA, responsavel)) { %>
                    <div class="form-group col-sm">
                        <label for="ncaCodigo"><hl:message
                                key="rotulo.natureza.consignataria.singular"/></label>
                        <%=JspHelper.geraCombo(naturezaConsignatarias, "ncaCodigo", Columns.NCA_CODIGO, Columns.NCA_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), !responsavel.isCsa() ? "onChange=\"carregaDadosFiltro('ncaCsa');\"" : null, false, 1, regraLimiteOperacao.getAttribute(Columns.RLO_NCA_CODIGO) != null ? (String) regraLimiteOperacao.getAttribute(Columns.RLO_NCA_CODIGO) : null, null, false, "form-control")%>
                    </div>
                    <% } %>
                    <% if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_CONSIGNATARIA, responsavel)) { %>
                    <div class="form-group col-sm">
                        <label for="csaCodigo"><hl:message
                                key="rotulo.consignataria.singular"/></label>
                        <%=JspHelper.geraCombo(consignatarias, "csaCodigo", Columns.CSA_CODIGO, Columns.CSA_NOME, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), "onChange=\"carregaDadosFiltro('csaCor');\"", false, 1, regraLimiteOperacao.getAttribute(Columns.RLO_CSA_CODIGO) != null ? (String) regraLimiteOperacao.getAttribute(Columns.RLO_CSA_CODIGO) : null, null, false, "form-control")%>
                    </div>
                    <% } %>
                </div>
                <div class="row">
                    <% if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_CORRESPONDENTE, responsavel)) { %>
                    <div class="form-group col-sm">
                        <label for="corCodigo"><hl:message
                                key="rotulo.correspondente.singular"/></label>
                        <%=JspHelper.geraCombo(correspondentes, "corCodigo", Columns.COR_CODIGO, Columns.COR_NOME, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, regraLimiteOperacao.getAttribute(Columns.RLO_COR_CODIGO) != null ? (String) regraLimiteOperacao.getAttribute(Columns.RLO_COR_CODIGO) : null, null, false, "form-control")%>
                    </div>
                    <% } %>
                    <% if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_NATUREZA_SVC, responsavel)) { %>
                    <div class="form-group col-sm">
                        <label for="nseCodigo"><hl:message
                                key="rotulo.natureza.servico.titulo"/></label>
                        <%=JspHelper.geraCombo(naturezaServicos, "nseCodigo", Columns.NSE_CODIGO, Columns.NSE_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), "onChange=\"carregaDadosFiltro('nseSvc');\"", false, 1, regraLimiteOperacao.getAttribute(Columns.RLO_NSE_CODIGO) != null ? (String) regraLimiteOperacao.getAttribute(Columns.RLO_NSE_CODIGO) : null, null, false, "form-control")%>
                    </div>
                    <% } %>
                    <% if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_SERVICO, responsavel)) { %>
                    <div class="form-group col-sm">
                        <label for="svcCodigo"><hl:message key="rotulo.servico.singular"/></label>
                        <%=JspHelper.geraCombo(servicos, "svcCodigo", Columns.SVC_CODIGO + ";" + Columns.SVC_DESCRICAO, Columns.SVC_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, regraLimiteOperacao.getAttribute(Columns.RLO_SVC_CODIGO) != null ? (String) regraLimiteOperacao.getAttribute(Columns.RLO_SVC_CODIGO) : null, null, false, "form-control")%>
                    </div>
                    <% } %>
                </div>
                <div class="row">
                    <% if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_CAPACIDADE, responsavel)) { %>
                    <div class="form-group col-sm">
                        <label for="capCodigo"><hl:message
                                key="rotulo.capacidade.registro.servidor.singular"/></label>
                        <%=JspHelper.geraCombo(capacidadesRegistroSer, "capCodigo", Columns.CAP_CODIGO, Columns.CAP_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, regraLimiteOperacao.getAttribute(Columns.RLO_CAP_CODIGO) != null ? (String) regraLimiteOperacao.getAttribute(Columns.RLO_CAP_CODIGO) : null, null, false, "form-control")%>
                    </div>
                    <% } %>
                    <% if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_CARGO, responsavel)) { %>
                    <div class="form-group col-sm">
                        <label for="crsCodigo"><hl:message
                                key="rotulo.cargo.registro.servidor.singular"/></label>
                        <%=JspHelper.geraCombo(cargosRegistroServidor, "crsCodigo", Columns.CRS_CODIGO, Columns.CRS_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, regraLimiteOperacao.getAttribute(Columns.RLO_CRS_CODIGO) != null ? (String) regraLimiteOperacao.getAttribute(Columns.RLO_CRS_CODIGO) : null, null, false, "form-control")%>
                    </div>
                    <% } %>
                    <% if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_PADRAO, responsavel)) { %>
                    <div class="form-group col-sm">
                        <label for="prsCodigo"><hl:message
                                key="rotulo.padrao.registro.servidor.singular"/></label>
                        <%=JspHelper.geraCombo(padroesRegistroSer, "prsCodigo", Columns.PRS_CODIGO, Columns.PRS_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, regraLimiteOperacao.getAttribute(Columns.RLO_PRS_CODIGO) != null ? (String) regraLimiteOperacao.getAttribute(Columns.RLO_PRS_CODIGO) : null, null, false, "form-control")%>
                    </div>
                    <% } %>
                </div>
                <div class="row">
                    <% if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_POSTO, responsavel)) { %>
                    <div class="form-group col-sm">
                        <label for="posCodigo"><hl:message
                                key="rotulo.posto.registro.servidor.singular"/></label>
                        <%=JspHelper.geraCombo(postosRegistroSer, "posCodigo", Columns.POS_CODIGO, Columns.POS_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, regraLimiteOperacao.getAttribute(Columns.RLO_POS_CODIGO) != null ? (String) regraLimiteOperacao.getAttribute(Columns.RLO_POS_CODIGO) : null, null, false, "form-control")%>
                    </div>
                    <% } %>
                    <% if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_TIPO, responsavel)) { %>
                    <div class="form-group col-sm">
                        <label for="trsCodigo"><hl:message
                                key="rotulo.tipo.registro.servidor.singular"/></label>
                        <%=JspHelper.geraCombo(tiposRegistroSer, "trsCodigo", Columns.TRS_CODIGO, Columns.TRS_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, regraLimiteOperacao.getAttribute(Columns.RLO_TRS_CODIGO) != null ? (String) regraLimiteOperacao.getAttribute(Columns.RLO_TRS_CODIGO) : null, null, false, "form-control")%>
                    </div>
                    <% } %>
                    <% if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_STATUS, responsavel)) { %>
                    <div class="form-group col-sm">
                        <label for="srsCodigo"><hl:message
                                key="rotulo.status.registro.servidor.singular"/></label>
                        <%=JspHelper.geraCombo(statusRegistroSer, "srsCodigo", Columns.SRS_CODIGO, Columns.SRS_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, regraLimiteOperacao.getAttribute(Columns.RLO_SRS_CODIGO) != null ? (String) regraLimiteOperacao.getAttribute(Columns.RLO_SRS_CODIGO) : null, null, false, "form-control")%>
                    </div>
                    <% } %>
                </div>
                <div class="row">
                    <% if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_VINCULO, responsavel)) { %>
                    <div class="form-group col-sm">
                        <label for="vrsCodigo"><hl:message
                                key="rotulo.vinculo.registro.servidor.singular"/></label>
                        <%=JspHelper.geraCombo(vinculosRegistroSer, "vrsCodigo", Columns.VRS_CODIGO, Columns.VRS_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, regraLimiteOperacao.getAttribute(Columns.RLO_VRS_CODIGO) != null ? (String) regraLimiteOperacao.getAttribute(Columns.RLO_VRS_CODIGO) : null, null, false, "form-control")%>
                    </div>
                    <% } %>
                    <% if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_FUNCAO, responsavel)) { %>
                    <div class="form-group col-sm">
                        <label for="funCodigo"><hl:message
                                key="rotulo.funcao.singular"/></label>
                        <%=JspHelper.geraCombo(funcoes, "funCodigo", Columns.FUN_CODIGO, Columns.FUN_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, regraLimiteOperacao.getAttribute(Columns.RLO_FUN_CODIGO) != null ? (String) regraLimiteOperacao.getAttribute(Columns.RLO_FUN_CODIGO) : null, null, false, "form-control")%>
                    </div>
                    <% } %>
                </div>
                <fieldset>
                    <h3 class="legend">
                        <span><hl:message key="rotulo.regra.limite.operacao.vigencia"/></span>
                    </h3>
                    <div class="row">
                        <div class="form-group col-sm-3">
                            <label for=rloDataVigenciaInicial><hl:message
                                    key="rotulo.regra.limite.operacao.data.vigencia.inicial"/></label>
                            <hl:htmlinput name="rloDataVigenciaInicial" type="text"
                                          classe="Edit form-control"
                                          di="rloDataVigenciaInicial" size="10"
                                          placeHolder="<%=LocaleHelper.getDateTimePlaceHolder()%>"
                                          mask="<%=LocaleHelper.getDateTimePattern()%>"
                                          value="<%=regraLimiteOperacao.getAttribute(Columns.RLO_DATA_VIGENCIA_INI) != null ? DateHelper.reformat(regraLimiteOperacao.getAttribute(Columns.RLO_DATA_VIGENCIA_INI).toString().replace(".0", ""), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern()) : ""%>"
                            />
                        </div>
                        <div class="form-group col-sm-3">
                            <label for="rloDataVigenciaFinal"><hl:message
                                    key="rotulo.regra.limite.operacao.data.vigencia.final"/>
                            </label>
                            <hl:htmlinput name="rloDataVigenciaFinal" type="text"
                                          classe="Edit form-control"
                                          di="rloDataVigenciaFinal" size="10"
                                          placeHolder="<%=LocaleHelper.getDateTimePlaceHolder()%>"
                                          mask="<%=LocaleHelper.getDateTimePattern()%>"
                                          value="<%=regraLimiteOperacao.getAttribute(Columns.RLO_DATA_VIGENCIA_FIM) != null ? DateHelper.reformat(regraLimiteOperacao.getAttribute(Columns.RLO_DATA_VIGENCIA_FIM).toString().replace(".0", ""), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern()) : ""%>"
                            />
                        </div>
                    </div>
                </fieldset>
                <% if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_TEMPO_SERVICO, responsavel)) { %>
                <fieldset>
                    <h3 class="legend">
                        <span><hl:message key="rotulo.regra.limite.operacao.faixa.tempo.servico"/></span>
                    </h3>
                    <div class="row">
                        <div class="form-group col-sm-3">
                            <label for="rloFaixaTempoServicoInicial"><hl:message
                                    key="rotulo.regra.limite.operacao.faixa.tempo.servico.inicial"/>
                            </label>
                            <hl:htmlinput name="rloFaixaTempoServicoInicial" type="text"
                                          classe="Edit form-control"
                                          di="rloFaixaTempoServicoInicial" size="2" mask="#D2"
                                          value="<%=regraLimiteOperacao.getAttribute(Columns.RLO_FAIXA_TEMPO_SERVICO_INI) != null ? regraLimiteOperacao.getAttribute(Columns.RLO_FAIXA_TEMPO_SERVICO_INI).toString() : ""%>"
                            />
                        </div>
                        <div class="form-group col-sm-3">
                            <label for="rloFaixaTempoServicoFinal"><hl:message
                                    key="rotulo.regra.limite.operacao.faixa.tempo.servico.final"/>
                            </label>
                            <hl:htmlinput name="rloFaixaTempoServicoFinal" type="text"
                                          classe="Edit form-control"
                                          di="rloFaixaTempoServicoFinal" size="2" mask="#D2"
                                          value="<%=regraLimiteOperacao.getAttribute(Columns.RLO_FAIXA_TEMPO_SERVICO_FIM) != null ? regraLimiteOperacao.getAttribute(Columns.RLO_FAIXA_TEMPO_SERVICO_FIM).toString() : ""%>"

                            />
                        </div>
                    </div>
                </fieldset>
                <% } %>
                <% if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_FAIXA_SALARIO, responsavel)) { %>
                <fieldset>
                    <h3 class="legend">
                        <span><hl:message key="rotulo.regra.limite.operacao.faixa.salario"/></span>
                    </h3>
                    <div class="row">
                        <div class="form-group col-sm-3">
                            <label for="rloFaixaSalarioInicial"><hl:message
                                    key="rotulo.regra.limite.operacao.faixa.salario.inicial"/>
                            </label>
                            <hl:htmlinput name="rloFaixaSalarioInicial" type="text"
                                          classe="Edit form-control"
                                          di="rloFaixaServicoInicial" size="12"
                                          onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                                          value="<%=regraLimiteOperacao.getAttribute(Columns.RLO_FAIXA_SALARIO_INI) != null ? regraLimiteOperacao.getAttribute(Columns.RLO_FAIXA_SALARIO_INI).toString().replace(".", ",") : ""%>"
                            />
                        </div>
                        <div class="form-group col-sm-3">
                            <label for="rloFaixaSalarioFinal"><hl:message
                                    key="rotulo.regra.limite.operacao.faixa.salario.final"/>
                            </label>
                            <hl:htmlinput name="rloFaixaSalarioFinal" type="text"
                                          classe="Edit form-control"
                                          di="rloFaixaSalarioFinal" size="12"
                                          onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                                          value="<%=regraLimiteOperacao.getAttribute(Columns.RLO_FAIXA_SALARIO_FIM) != null ? regraLimiteOperacao.getAttribute(Columns.RLO_FAIXA_SALARIO_FIM).toString().replace(".", ",") : ""%>"

                            />
                        </div>
                    </div>
                </fieldset>
                <% } %>
                <% if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_FAIXA_ETARIA, responsavel)) { %>
                <fieldset>
                    <h3 class="legend">
                        <span><hl:message key="rotulo.regra.limite.operacao.faixa.etaria"/></span>
                    </h3>
                    <div class="row">
                        <div class="form-group col-sm-3">
                            <label for="rloFaixaEtariaInicial"><hl:message
                                    key="rotulo.regra.taxa.juros.faixa.etaria.inicial"/>&nbsp;</label>
                            <hl:htmlinput classe="Edit form-control" type="text"
                                          name="rloFaixaEtariaInicial" di="rloFaixaEtariaInicial"
                                          size="2" mask="#D2"
                                          value="<%=regraLimiteOperacao.getAttribute(Columns.RLO_FAIXA_ETARIA_INI) != null ? regraLimiteOperacao.getAttribute(Columns.RLO_FAIXA_ETARIA_INI).toString() : ""%>"
                            />
                        </div>
                        <div class="form-group col-sm-3">
                            <label for="rloFaixaEtariaFinal"><hl:message
                                    key="rotulo.regra.taxa.juros.faixa.etaria.final"/>&nbsp;</label>
                            <hl:htmlinput classe="Edit form-control" type="text"
                                          name="rloFaixaEtariaFinal" di="rloFaixaEtariaFinal"
                                          size="2" mask="#D2"
                                          value="<%=regraLimiteOperacao.getAttribute(Columns.RLO_FAIXA_ETARIA_FIM) != null ? regraLimiteOperacao.getAttribute(Columns.RLO_FAIXA_ETARIA_FIM).toString() : ""%>"
                            />
                        </div>
                    </div>
                </fieldset>
                <% } %>
                <% if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_MARGEM_FOLHA, responsavel)) { %>
                <fieldset>
                    <h3 class="legend">
                        <span><hl:message key="rotulo.regra.limite.operacao.faixa.margem"/> (<hl:message
                                key="rotulo.moeda"/>)</span>
                    </h3>
                    <div class="row">
                        <div class="form-group col-sm-3">
                            <label for="rloFaixaMargemInicial"><hl:message
                                    key="rotulo.regra.limite.operacao.faixa.margem.inicial"/>&nbsp;</label>
                            <hl:htmlinput name="rloFaixaMargemInicial" type="text"
                                          classe="Edit form-control"
                                          di="rloFaixaMargemInicial" size="12"
                                          onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                                          value="<%=regraLimiteOperacao.getAttribute(Columns.RLO_FAIXA_MARGEM_FOLHA_INI) != null ? regraLimiteOperacao.getAttribute(Columns.RLO_FAIXA_MARGEM_FOLHA_INI).toString().replace(".", ",") : ""%>"
                            />
                        </div>
                        <div class="form-group col-sm-3">
                            <label for="rloFaixaMargemFinal"><hl:message
                                    key="rotulo.regra.limite.operacao.faixa.margem.final"/>&nbsp;</label>
                            <hl:htmlinput name="rloFaixaMargemFinal" type="text"
                                          classe="Edit form-control"
                                          di="rloFaixaMargemFinal" size="12"
                                          onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                                          value="<%=regraLimiteOperacao.getAttribute(Columns.RLO_FAIXA_MARGEM_FOLHA_FIM) != null ? regraLimiteOperacao.getAttribute(Columns.RLO_FAIXA_MARGEM_FOLHA_FIM).toString().replace(".", ",") : ""%>"
                            />
                        </div>
                    </div>
                </fieldset>
                <% } %>
                <% if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_PADRAO_MATRICULA, responsavel)) { %>
                <fieldset>
                    <h3 class="legend">
                        <span><hl:message key="rotulo.regra.limite.operacao.padrao.matricula"/></span>
                    </h3>
                    <div class="row">
                        <div class="form-group col-sm-3">
                            <label for="rloPadraoMatricula"><hl:message
                                    key="rotulo.regra.limite.operacao.padrao.matricula"/></label>
                            <hl:htmlinput name="rloPadraoMatricula" type="text"
                                          classe="Edit form-control"
                                          di="rloPadraoMatricula"
                                          value="<%=regraLimiteOperacao.getAttribute(Columns.RLO_PADRAO_MATRICULA) != null ? regraLimiteOperacao.getAttribute(Columns.RLO_PADRAO_MATRICULA).toString() : ""%>"
                            />
                        </div>
                    </div>
                </fieldset>
                <% } %>
                <% if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_PADRAO_CATEGORIA, responsavel)) { %>
                <fieldset>
                    <h3 class="legend">
                        <span><hl:message key="rotulo.regra.limite.operacao.padrao.categoria"/></span>
                    </h3>
                    <div class="row">
                        <div class="form-group col-sm-3">
                            <label for="rloPadraoCategoria"><hl:message
                                    key="rotulo.regra.limite.operacao.padrao.categoria"/>&nbsp;</label>
                            <hl:htmlinput name="rloPadraoCategoria" type="text"
                                          classe="Edit form-control"
                                          di="rloPadraoCategoria"
                                          value="<%=regraLimiteOperacao.getAttribute(Columns.RLO_PADRAO_CATEGORIA) != null ? regraLimiteOperacao.getAttribute(Columns.RLO_PADRAO_CATEGORIA).toString() : ""%>"
                            />
                        </div>
                    </div>
                </fieldset>
                <% } %>
                <% if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_PADRAO_VERBA, responsavel)) { %>
                <fieldset>
                    <h3 class="legend">
                        <span><hl:message key="rotulo.regra.limite.operacao.padrao.verba"/></span>
                    </h3>
                    <div class="row">
                        <div class="form-group col-sm-3">
                            <label for="rloPadraoVerba"><hl:message
                                    key="rotulo.regra.limite.operacao.padrao.verba"/>&nbsp;</label>
                            <hl:htmlinput name="rloPadraoVerba" type="text"
                                          classe="Edit form-control"
                                          di="rloPadraoVerba"
                                          value="<%=regraLimiteOperacao.getAttribute(Columns.RLO_PADRAO_VERBA) != null ? regraLimiteOperacao.getAttribute(Columns.RLO_PADRAO_VERBA).toString() : ""%>"
                            />
                        </div>
                    </div>
                </fieldset>
                <% } %>
                <% if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_PADRAO_VERBA_REF, responsavel)) { %>
                <fieldset>
                    <h3 class="legend">
                        <span><hl:message key="rotulo.regra.limite.operacao.padrao.verba.ref"/></span>
                    </h3>
                    <div class="row">
                        <div class="form-group col-sm-3">
                            <label for="rloPadraoVerbaRef"><hl:message
                                    key="rotulo.regra.limite.operacao.padrao.verba.ref"/>&nbsp;</label>
                            <hl:htmlinput name="rloPadraoVerbaRef" type="text"
                                          classe="Edit form-control"
                                          di="rloPadraoVerbaRef"
                                          value="<%=regraLimiteOperacao.getAttribute(Columns.RLO_PADRAO_VERBA_REF) != null ? regraLimiteOperacao.getAttribute(Columns.RLO_PADRAO_VERBA_REF).toString() : ""%>"
                            />
                        </div>
                    </div>
                </fieldset>
                <% } %>
                <% if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_MENSAGEM_ERRO, responsavel)) { %>
                <fieldset>
                    <h3 class="legend">
                        <span><hl:message key="rotulo.regra.limite.operacao.mensagem.erro"/></span>
                    </h3>
                    <div class="row">
                        <div class="form-group col-sm-3">
                            <label for="rloMensagemErro"><hl:message
                                    key="rotulo.regra.limite.operacao.mensagem.erro"/>&nbsp;</label>
                            <hl:htmlinput name="rloMensagemErro" type="text"
                                          classe="Edit form-control"
                                          di="rloMensagemErro"
                                          value="<%=regraLimiteOperacao.getAttribute(Columns.RLO_MENSAGEM_ERRO) != null ? regraLimiteOperacao.getAttribute(Columns.RLO_MENSAGEM_ERRO).toString() : ""%>"
                            />
                        </div>
                    </div>
                </fieldset>
                <% } %>
                <% if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_LIMITE_QUANTIDADE, responsavel)) { %>
                <fieldset>
                    <h3 class="legend">
                        <span><hl:message key="rotulo.regra.limite.operacao.limite.quantidade"/></span>
                    </h3>
                    <div class="row">
                        <div class="form-group col-sm-3">
                            <label for="rloLimiteQuantidade"><hl:message
                                    key="rotulo.regra.limite.operacao.limite.quantidade"/>&nbsp;</label>
                            <hl:htmlinput name="rloLimiteQuantidade" type="text"
                                          classe="Edit form-control"
                                          di="rloLimiteQuantidade" size="2" mask="#D2"
                                          value="<%=regraLimiteOperacao.getAttribute(Columns.RLO_LIMITE_QUANTIDADE) != null ? regraLimiteOperacao.getAttribute(Columns.RLO_LIMITE_QUANTIDADE).toString() : ""%>"
                            />
                        </div>
                    </div>
                </fieldset>
                <% } %>
                <% if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_LIMITE_DATA_ADE, responsavel)) { %>
                <fieldset>
                    <h3 class="legend">
                        <span><hl:message key="rotulo.regra.limite.operacao.limite.data.ade"/></span>
                    </h3>
                    <div class="row">
                        <div class="form-group col-sm-3">
                            <label for="rloDataLimiteAde"><hl:message
                                    key="rotulo.regra.limite.operacao.limite.data.ade"/>&nbsp;</label>
                            <hl:htmlinput name="rloDataLimiteAde" type="text"
                                          classe="Edit form-control"
                                          di="rloDataLimiteAde"
                                          size="10"
                                          placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>"
                                          mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                                          value="<%=regraLimiteOperacao.getAttribute(Columns.RLO_LIMITE_DATA_FIM_ADE) != null ? DateHelper.reformat(regraLimiteOperacao.getAttribute(Columns.RLO_LIMITE_DATA_FIM_ADE).toString(), "yyyy-MM-dd", LocaleHelper.getDatePattern()) : ""%>"
                            />
                        </div>
                    </div>
                </fieldset>
                <% } %>
                <% if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_LIMITE_PRAZO, responsavel)) { %>
                <fieldset>
                    <h3 class="legend">
                        <span><hl:message key="rotulo.regra.limite.operacao.limite.prazo"/></span>
                    </h3>
                    <div class="row">
                        <div class="form-group col-sm-3">
                            <label for="rloLimitePrazo"><hl:message
                                    key="rotulo.regra.limite.operacao.limite.prazo"/>&nbsp;</label>
                            <hl:htmlinput name="rloLimitePrazo" type="text"
                                          classe="Edit form-control"
                                          di="rloLimitePrazo" size="2" mask="#D2"
                                          value="<%=regraLimiteOperacao.getAttribute(Columns.RLO_LIMITE_PRAZO) != null ? regraLimiteOperacao.getAttribute(Columns.RLO_LIMITE_PRAZO).toString() : ""%>"
                            />
                        </div>
                    </div>
                </fieldset>
                <% } %>
                <% if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_LIMITE_VLR_PARCELA, responsavel)) { %>
                <fieldset>
                    <h3 class="legend">
                        <span><hl:message key="rotulo.regra.limite.operacao.limite.vlr.parcela"/></span>
                    </h3>
                    <div class="row">
                        <div class="form-group col-sm-3">
                            <label for="rloLimiteVlrParcela"><hl:message
                                    key="rotulo.regra.limite.operacao.limite.vlr.parcela"/>&nbsp;</label>
                            <hl:htmlinput name="rloLimiteVlrParcela" type="text"
                                          classe="Edit form-control"
                                          di="rloLimiteVlrParcela" size="12"
                                          onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                                          value="<%=regraLimiteOperacao.getAttribute(Columns.RLO_LIMITE_VALOR_PARCELA) != null ? regraLimiteOperacao.getAttribute(Columns.RLO_LIMITE_VALOR_PARCELA).toString().replace(".", ",") : ""%>"
                            />
                        </div>
                    </div>
                </fieldset>
                <% } %>
                <% if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_LIMITE_VLR_LIBERADO, responsavel)) { %>
                <fieldset>
                    <h3 class="legend">
                        <span><hl:message key="rotulo.regra.limite.operacao.limite.vlr.liberado"/></span>
                    </h3>
                    <div class="row">
                        <div class="form-group col-sm-3">
                            <label for="rloLimiteVlrLiberado"><hl:message
                                    key="rotulo.regra.limite.operacao.limite.vlr.liberado"/>&nbsp;</label>
                            <hl:htmlinput name="rloLimiteVlrLiberado" type="text"
                                          classe="Edit form-control"
                                          di="rloLimiteVlrLiberado" size="12"
                                          onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                                          value="<%=regraLimiteOperacao.getAttribute(Columns.RLO_LIMITE_VALOR_LIBERADO) != null ? regraLimiteOperacao.getAttribute(Columns.RLO_LIMITE_VALOR_LIBERADO).toString().replace(".", ",") : ""%>"
                            />
                        </div>
                    </div>
                </fieldset>
                <% } %>
                <% if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_LIMITE_CAPITAL_DEVIDO, responsavel)) { %>
                <fieldset>
                    <h3 class="legend">
                        <span><hl:message key="rotulo.regra.limite.operacao.limite.capital.devido"/></span>
                    </h3>
                    <div class="row">
                        <div class="form-group col-sm-3">
                            <label for="rloLimiteCapitalDevido"><hl:message
                                    key="rotulo.regra.limite.operacao.limite.capital.devido"/>&nbsp;</label>
                            <hl:htmlinput name="rloLimiteCapitalDevido" type="text"
                                          classe="Edit form-control"
                                          di="rloLimiteCapitalDevido" size="12"
                                          onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                                          value="<%=regraLimiteOperacao.getAttribute(Columns.RLO_LIMITE_CAPITAL_DEVIDO) != null ? regraLimiteOperacao.getAttribute(Columns.RLO_LIMITE_CAPITAL_DEVIDO).toString().replace(".", ",") : ""%>"
                            />
                        </div>
                    </div>
                </fieldset>
                <% } %>
            </div>
        </div>
        <div id="btn-actions" class="btn-action col-sm">
            <a class="btn btn-outline-danger" href="#no-back"
               onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getCurrentHistory(), request))%>'); return false;"
               id="btnVoltar"><hl:message key="rotulo.botao.voltar"/></a>
            <a class="btn btn-primary" href="#no-back" onClick="salvar(); return false;"><hl:message
                    key="rotulo.botao.salvar"/></a> &nbsp;&nbsp;&nbsp;
        </div>
    </form>
    <% if (exibeBotaoRodape) { %>
    <div id="btns">
        <a id="page-up" onclick="up()">
            <svg xmlns="http://www.w3.org/2000/svg" width="147.344" height="147.344" viewBox="0 0 147.344 147.344">
                <path id="União_3" data-name="União 3"
                      d="M-20,60.672a73.672,73.672,0,1,1,73.672,73.672A73.66,73.66,0,0,1-20,60.672ZM9.61,16.61a62.252,62.252,0,0,0,0,88.124,62.252,62.252,0,0,0,88.124,0,62.252,62.252,0,0,0,0-88.124,62.252,62.252,0,0,0-88.124,0ZM49.174,88.087,15.278,54.3a6.85,6.85,0,0,1,9.67-9.706L53.672,73.6,82.362,45.336A6.825,6.825,0,1,1,92,55.005L58.6,88.3a6.827,6.827,0,0,1-9.421-.212Z"
                      transform="translate(20 13)"/>
            </svg>
        </a>
        <a id="page-down" onclick="down()">
            <svg xmlns="http://www.w3.org/2000/svg" width="147.344" height="147.344" viewBox="0 0 147.344 147.344">
                <path id="União_3" data-name="União 3"
                      d="M-20,60.672a73.672,73.672,0,1,1,73.672,73.672A73.66,73.66,0,0,1-20,60.672ZM9.61,16.61a62.252,62.252,0,0,0,0,88.124,62.252,62.252,0,0,0,88.124,0,62.252,62.252,0,0,0,0-88.124,62.252,62.252,0,0,0-88.124,0ZM49.174,88.087,15.278,54.3a6.85,6.85,0,0,1,9.67-9.706L53.672,73.6,82.362,45.336A6.825,6.825,0,1,1,92,55.005L58.6,88.3a6.827,6.827,0,0,1-9.421-.212Z"
                      transform="translate(20 13)"/>
            </svg>
        </a>
        <a id="page-actions" onclick="toActionBtns()">
            <svg xmlns="http://www.w3.org/2000/svg" width="145.344" height="145.344" viewBox="0 0 145.344 145.344">
                <path id="União_1" data-name="União 1"
                      d="M-20,59.672a72.672,72.672,0,1,1,72.671,72.672A72.671,72.671,0,0,1-20,59.672Zm10.164,0A62.508,62.508,0,1,0,52.672-2.836,62.579,62.579,0,0,0-9.836,59.672Zm82.6,40.182H24.545A12.069,12.069,0,0,1,12.49,87.8V31.544A12.069,12.069,0,0,1,24.545,19.49h44.2a4.014,4.014,0,0,1,2.841,1.177L91.678,40.757A4.019,4.019,0,0,1,92.855,43.6V87.8A12.069,12.069,0,0,1,80.8,99.854Zm0-40.182a4.018,4.018,0,0,1,4.019,4.018V91.817H80.8A4.023,4.023,0,0,0,84.818,87.8V45.263L67.081,27.526H36.6V39.58H64.727a4.019,4.019,0,0,1,0,8.037H32.581A4.018,4.018,0,0,1,28.563,43.6V27.526H24.545a4.023,4.023,0,0,0-4.018,4.019V87.8a4.023,4.023,0,0,0,4.018,4.018h4.019V63.689a4.018,4.018,0,0,1,4.018-4.018ZM36.6,91.817H68.745V67.708H36.6Z"
                      transform="translate(20 13)"/>
            </svg>
        </a>
    </div>
    <% }%>
</c:set>
<c:set var="javascript">
    <script type="text/JavaScript" src="../../../js/validacoes.js"></script>
    <script type="text/JavaScript">

        function formLoad() {
            f0 = document.forms[0];
        }

        window.onload = formLoad;

        async function salvar() {
            <% if (responsavel.isCsa()) { %>
            if ($('#csaCodigo').val() == '') {
               window.alert("<%=ApplicationResourcesHelper.getMessage("rotulo.campo.csa.uma", responsavel)%>");
            } else {
                if (validaDateTime($('#rloDataVigenciaInicial').val(), 'rloDataVigenciaInicial', true) &&
                    validaDateTime($('#rloDataVigenciaFinal').val(), 'rloDataVigenciaFinal', true) &&
                    validaDateTime($('#rloDataLimiteAde').val(), 'rloDataLimiteAde', false) &&
                    validaDataIniFim($('#rloDataVigenciaInicial').val(), $('#rloDataVigenciaFinal').val())) {
                    f0.submit();
                }
            }
            <% } else { %>
                if (validaDateTime($('#rloDataVigenciaInicial').val(), 'rloDataVigenciaInicial', true) &&
                    validaDateTime($('#rloDataVigenciaFinal').val(), 'rloDataVigenciaFinal', true) &&
                    validaDateTime($('#rloDataLimiteAde').val(), 'rloDataLimiteAde', false) &&
                    validaDataIniFim($('#rloDataVigenciaInicial').val(), $('#rloDataVigenciaFinal').val())) {
                    f0.submit();
                }
            <% } %>
        }

        function carregaDadosFiltro(tipo) {
            var codeFilterOne;
            var codeFilterTwo;
            if (tipo === 'csaCor') {
                codeFilterOne = $('#csaCodigo').val()
            } else if (tipo === 'nseSvc') {
                codeFilterOne = $('#nseCodigo').val()
                codeFilterTwo = $('#csaCodigo').val()
            } else if (tipo === 'csaSvc') {
                codeFilterOne = $('#csaCodigo').val()
            } else if (tipo === 'ncaCsa') {
                codeFilterOne = $('#ncaCodigo').val()
            } else if (tipo === 'orgSbo') {
                codeFilterOne = $('#orgCodigo').val()
            }
            $.ajax({
                    type: 'post',
                    url: "../v3/regrasLimiteOperacao?acao=filtroCampoSelect&_skip_history_=true&tipo=" + tipo + "&codeFilterOne=" + codeFilterOne + "&codeFilterTwo=" + codeFilterTwo,
                    async: true,
                    contentType: 'application/json',
                    success: function (data) {
                        var options = "<option value>" + "<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>" + "</option> ";
                        var result;
                        var labelValue = null;
                        var labelName = null;
                        result = JSON.parse(JSON.stringify(data));
                        if (tipo === 'csaCor') {
                            if (result.length > 0) {
                                result.forEach(function (objeto) {
                                    labelValue = objeto.atributos['<%=Columns.COR_CODIGO%>'];
                                    labelName = objeto.atributos['<%=Columns.COR_NOME%>'];
                                    options = options.concat('<option value="').concat(labelValue).concat('">').concat(labelName).concat('</option>');
                                });
                            } else {
                                alert('<hl:message key="rotulo.regra.limite.operacao.titulo.alert.correspondente.csa"/>')
                            }
                            document.getElementById('corCodigo').innerHTML = options;
                            carregaDadosFiltro('csaSvc');
                        } else if (tipo === 'nseSvc') {
                            if (result.length > 0) {
                                result.forEach(function (objeto) {
                                    labelValue = objeto.atributos['<%=Columns.SVC_CODIGO%>'];
                                    labelName = objeto.atributos['<%=Columns.SVC_DESCRICAO%>'];
                                    options = options.concat('<option value="').concat(labelValue).concat('">').concat(labelName).concat('</option>');
                                });
                            } else {
                                if (codeFilterTwo !== '') {
                                    alert('<hl:message key="rotulo.regra.limite.operacao.titulo.alert.servico.nse.csa"/>');
                                } else {
                                    alert('<hl:message key="rotulo.regra.limite.operacao.titulo.alert.servico.nse"/>');
                                }
                            }
                            document.getElementById('svcCodigo').innerHTML = options;
                        } else if (tipo === 'csaSvc') {
                            if (codeFilterOne !== '' && $('#nseCodigo').val() !== '') {
                                carregaDadosFiltro('nseSvc');
                            } else {
                                if (result.length > 0) {
                                    result.forEach(function (objeto) {
                                        labelValue = objeto.atributos['<%=Columns.SVC_CODIGO%>'];
                                        labelName = objeto.atributos['<%=Columns.SVC_DESCRICAO%>'];
                                        options = options.concat('<option value="').concat(labelValue).concat('">').concat(labelName).concat('</option>');
                                    });
                                } else {
                                    alert('<hl:message key="rotulo.regra.limite.operacao.titulo.alert.servico.csa"/>');
                                }
                                document.getElementById('svcCodigo').innerHTML = options;
                            }
                        } else if (tipo === 'orgSbo') {
                            if (result.length > 0) {
                                result.forEach(function (objeto) {
                                    labelValue = objeto.atributos['<%=Columns.SBO_CODIGO%>'];
                                    labelName = objeto.atributos['<%=Columns.SBO_DESCRICAO%>'];
                                    options = options.concat('<option value="').concat(labelValue).concat('">').concat(labelName).concat('</option>');
                                });
                            } else {
                                alert('<hl:message key="rotulo.regra.limite.operacao.titulo.alert.suborgao.org"/>');
                            }
                            document.getElementById('sboCodigo').innerHTML = options;
                        } else if (tipo === 'ncaCsa') {
                            if (result.length > 0) {
                                result.forEach(function (objeto) {
                                    labelValue = objeto.atributos['<%=Columns.CSA_CODIGO%>'];
                                    labelName = objeto.atributos['<%=Columns.CSA_NOME%>'];
                                    options = options.concat('<option value="').concat(labelValue).concat('">').concat(labelName).concat('</option>');
                                });
                            } else {
                                alert('<hl:message key="rotulo.regra.limite.operacao.titulo.alert.consgnataria.nsa"/>');
                            }
                            document.getElementById('csaCodigo').innerHTML = options;
                        }
                    },
                    error: function (response) {
                        console.log(response.statusText);
                    }
                }
            );
        }

        //Valida DateTime
        function validaDateTime(data, campo, checkAll) {
            var dateTime,
                date,
                time;
            if (data !== '') {
                dateTime = data.split(" ");
                if (checkAll) {
                    if (dateTime.length !== 2) {
                        alert("Data Incorreta");
                        document.getElementById(campo).focus();
                        return false;
                    }
                }
                date = dateTime[0];
                if (!verificaData(date)) {
                    document.getElementById(campo).focus();
                    return false;
                }

                if (checkAll) {
                    time = dateTime[1];
                    if (!verificaHora(time)) {
                        document.getElementById(campo).focus();
                        return false;
                    }
                }
                return true;
            }
            return true;
        }

        function validaDataIniFim(dataIni, dataFim) {
            if (dataIni === '') {
                dataIni = new Date();
                dataIni = dataIni.toLocaleString("pt-br").replace(",", "");
            }

            if (dataIni !== '' && dataFim !== '') {
                var dateIni, dateFim, partesData;
                dateIni = dataIni.split(" ");
                dateFim = dataFim.split(" ")
                partesData = obtemPartesData(dateIni[0]);
                var dia = partesData[0];
                var mes = partesData[1];
                var ano = partesData[2];
                partesData = obtemPartesData(dateFim[0]);
                var diaFim = partesData[0];
                var mesFim = partesData[1];
                var anoFim = partesData[2];

                if (dateIni[1] != null && dateFim[1] != null) {
                    var diasDif = '';
                    var partesHora;
                    partesHora = dataIni[1].split(':');
                    var hora = partesHora[0];
                    var minuto = partesHora[1];
                    var segundo = partesHora[2];
                    partesHora = dateFim[1].split(':');
                    var horaFim = partesHora[0];
                    var minutoFim = partesHora[1];
                    var segundoFim = partesHora[2];
                    if (!VerificaPeriodoExt(dia, mes, ano, diaFim, mesFim, anoFim, diasDif, hora, minuto, segundo, horaFim, minutoFim, segundoFim)) {
                        return false;
                    } else {
                        return true;
                    }
                }
            } else {
                return true;
            }
        }
    </script>
    <% if (exibeBotaoRodape) { %>
    <script>
        let btnDown = document.querySelector('#btns');
        const pageActions = document.querySelector('#page-actions');
        const pageSize = document.body.scrollHeight;

        function up() {
            window.scrollTo({
                top: 0,
                behavior: "smooth",
            });
        }

        function down() {
            let toDown = document.body.scrollHeight;
            window.scrollBy({
                top: toDown,
                behavior: "smooth",
            });
        }

        function toActionBtns() {
            let save = document.querySelector('#btn-actions').getBoundingClientRect().top;
            window.scrollBy({
                top: save,
                behavior: "smooth",
            });
        }

        function btnTab() {
            let scrollSize = document.documentElement.scrollTop;
            if (scrollSize >= 300) {
                btnDown.classList.add('btns-active');
            } else {
                btnDown.classList.remove('btns-active');
            }
        }

        window.addEventListener('scroll', btnTab);
    </script>
    <% } %>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
