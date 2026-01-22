<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper" %>

<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
    List orgaos = (List) request.getAttribute("orgaos");
    List consignatarias = (List) request.getAttribute("consignatarias");
    List<TransferObject> lstTipoOcorrencia = (List<TransferObject>) request.getAttribute("lstTipoOcorrencia");
    boolean requerMatriculaCpf = (boolean) request.getAttribute("requerMatriculaCpf");
%>
<c:set var="title">
    <hl:message key="rotulo.folha.cadastro.retorno.integracao.titulo"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
    <div class="row">
        <div class="col-sm-12">
            <div class="card">
                <div class="card-header hasIcon">
                    <span class="card-header-icon"><svg width="25"><use xlink:href="#i-consultar"></use></svg></span>
                    <h2 class="card-header-title"><hl:message key="mensagem.pesquisa.titulo"/></h2>
                </div>
                <div class="card-body">
                    <form method="post"
                          action="../v3/cadastrarRetornoIntegracao?acao=listarIntegracao&<%=SynchronizerToken.generateToken4URL(request)%>"
                          name="form1">
                        <div class="row">
                            <div class="form-group col-sm-6">
                                <label for="CSA_CODIGO"><hl:message key="rotulo.consignataria.singular"/></label>
                                <%=JspHelper.geraCombo(consignatarias, "CSA_CODIGO", Columns.CSA_CODIGO, Columns.CSA_IDENTIFICADOR + ";" + Columns.CSA_NOME, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), "class=\"form-control form-select\"", false, 1, null, "selectName(this)")%>
                            </div>
                            <input type="hidden" id="CSA_NOME" name="CSA_NOME">
                            <div class="form-group col-sm-6">
                                <label for="ADE_NUMERO"><hl:message key="rotulo.folha.numero.ade"/></label>
                                <hl:htmlinput name="ADE_NUMERO" type="text"
                                              classe="form-control w-100" di="ADE_NUMERO"
                                              mask="#*20" size="8"
                                              value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, \"ADE_NUMERO\"))%>"
                                              placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.ade.numero", responsavel)%>'
                                />
                            </div>
                        </div>
                        <% if (ShowFieldHelper.showField(FieldKeysConstants.CADASTRAR_RETORNO_INTEGRACAO_ADE_IDENTIFICADOR, responsavel)) { %>
                        <div class="row">
                            <div class="form-group col-sm-6">
                                <label for="ADE_IDENTIFICADOR">
                                    <hl:message key="rotulo.folha.identificador.ade"/>
                                </label>
                                <hl:htmlinput name="ADE_IDENTIFICADOR" type="text" classe="form-control"
                                              placeHolder="<%=(String) ApplicationResourcesHelper.getMessage("mensagem.informacao.ade.identificador",responsavel)%>"
                                              di="ADE_IDENTIFICADOR"
                                              value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, \"ADE_IDENTIFICADOR\"))%>"
                                              size="40" mask="#*40"/>
                            </div>
                        </div>
                        <% } %>
                        <div class="row">
                            <div class="form-group col-sm-12 col-md-6">
                                <span id="dataInclusao"><hl:message key="rotulo.pesquisa.data.periodo"/></span>
                                <div class="row" role="group" aria-labelledby="dataInclusao">
                                    <div class="form-group col-sm-12 col-md-1">
                                        <div class="float-left align-middle mt-4 form-control-label">
                                            <span><hl:message key="rotulo.pesquisa.data.prefixo.inicio"/></span>
                                        </div>
                                    </div>
                                    <div class="form-group col-sm-12 col-md-5">
                                        <hl:htmlinput name="periodoIni" di="periodoIni" type="text"
                                                      classe="form-control w-100"
                                                      placeHolder="<%=LocaleHelper.getPeriodoPlaceHolder()%>"
                                                      mask="DD/DDDD" size="10"
                                                      value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "periodoIni"))%>"/>
                                    </div>
                                    <div class="form-group col-sm-12 col-md-1">
                                        <div class="float-left align-middle mt-4 form-control-label">
                                            <span><hl:message key="rotulo.pesquisa.data.prefixo.fim"/></span>
                                        </div>
                                    </div>
                                    <div class="form-group col-sm-12 col-md-5">
                                        <hl:htmlinput name="periodoFim" di="periodoFim" type="text"
                                                      classe="form-control w-100"
                                                      placeHolder="<%=LocaleHelper.getPeriodoPlaceHolder()%>"
                                                      mask="DD/DDDD" size="10"
                                                      value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "periodoFim"))%>"/>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <% if (ShowFieldHelper.showField(FieldKeysConstants.CADASTRAR_RETORNO_INTEGRACAO_ORG_CODIGO, responsavel)) { %>
                        <div class="row">
                            <div class="form-group col-sm-6">
                                <label for=""><hl:message key="rotulo.orgao.singular"/></label>
                                <%=JspHelper.geraCombo(orgaos, "ORG_CODIGO", Columns.ORG_CODIGO, Columns.ORG_NOME + ";" + Columns.ORG_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, JspHelper.verificaVarQryStr(request, "ORG_CODIGO"), null, false, "form-control")%>
                            </div>
                        </div>
                        <% } %>
                        <div class="row">
                            <div class="form-group col-sm-6">
                                <hl:campoMatriculav4
                                        placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.matricula", responsavel)%>'/>
                            </div>
                            <div class="form-group col-sm-6">
                                <hl:campoCPFv4
                                        placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.cpf", responsavel)%>'
                                        classe="form-control"/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="form-group col-sm-6">
                                <label for="PESQUISA"><hl:message key="rotulo.folha.opcao"/></label>
                                <select name="PESQUISA" id="PESQUISA" class="form-select form-control select"
                                        nf="btnEnvia" onChange="habilitaSituacao(this);"
                                        onFocus="SetarEventoMascara(this,'#*200',true);"
                                        onBlur="fout(this);ValidaMascara(this);">
                                    <option value="PROCESSAMENTO" selected><hl:message
                                            key="rotulo.folha.parcelas.processamento"/></option>
                                    <option value="INTEGRADAS"><hl:message
                                            key="rotulo.folha.parcelas.integradas"/></option>
                                </select>
                            </div>
                            <div class="form-group col-sm-6">
                                <label for="SITUACOES"><hl:message key="rotulo.folha.situacao"/></label>
                                <select name="SITUACOES" id="SITUACOES" MULTIPLE SIZE="4"
                                        class="SelectMedio form-control select" nf="btnEnvia"
                                        onFocus="SetarEventoMascara(this,'#*200',true);"
                                        onBlur="fout(this);ValidaMascara(this);" disabled="true">
                                    <optgroup>
                                        <option value="" selected><hl:message key="rotulo.campo.todos"/></option>
                                        <option value="5"><hl:message key="rotulo.folha.rejeitada"/></option>
                                        <option value="6"><hl:message key="rotulo.folha.liquidada"/></option>
                                        <option value="7"><hl:message key="rotulo.folha.liquidada.manual"/></option>
                                    </optgroup>
                                </select><input type='hidden' name='SITUACAO' id='SITUACAO'/>
                            </div>
                        </div>
                        <% if (ShowFieldHelper.showField(FieldKeysConstants.CADASTRAR_RETORNO_INTEGRACAO_TOC_CODIGO, responsavel)) { %>
                        <div class="row">
                            <div class="form-group col-sm-6">
                                <label for="OCORRENCIAS"><hl:message key="rotulo.folha.tipo.ocorrencia"/></label>
                                <select name="OCORRENCIAS" id="OCORRENCIAS" MULTIPLE SIZE="7"
                                        class="SelectMedio form-control select"
                                        onFocus="SetarEventoMascaraV4(this,'#*200',true);"
                                        onBlur="fout(this);ValidaMascaraV4(this);" nf="Filtrar" disabled="true">
                                    <optgroup>
                                        <option value="" selected><hl:message key="rotulo.campo.todos"/></option>
                                        <%
                                            Iterator<TransferObject> tocIterator = lstTipoOcorrencia.iterator();
                                            while (tocIterator.hasNext()) {
                                                TransferObject tipoOcorrencia = tocIterator.next();
                                                String tocCodigo = (String) tipoOcorrencia.getAttribute(Columns.TOC_CODIGO);
                                        %>
                                        <option value="<%=tocCodigo%>"><%=tipoOcorrencia.getAttribute(Columns.TOC_DESCRICAO)%>
                                        </option>
                                        <%
                                            }
                                        %>
                                    </optgroup>
                                </select>
                            </div>
                        </div>
                        <% } %>
                        </select><input type='hidden' name='TOC_CODIGO' id='TOC_CODIGO' value=""/>
                        <div class="row">
                            <div class="form-group col-sm-6">
                                <label for="PAPEIS"><hl:message key="rotulo.folha.responsavel"/></label>
                                <select name="PAPEIS" id="PAPEIS" MULTIPLE SIZE="6"
                                        class="SelectMedio form-control select" nf="btnEnvia"
                                        onFocus="SetarEventoMascara(this,'#*200',true);"
                                        onBlur="fout(this);ValidaMascara(this);" disabled="true">
                                    <optgroup>
                                        <option value="" selected><hl:message key="rotulo.campo.todos"/></option>
                                        <option value="1"><hl:message key="rotulo.consignante.singular"/></option>
                                        <option value="2"><hl:message key="rotulo.consignataria.singular"/></option>
                                        <option value="3"><hl:message key="rotulo.orgao.singular"/></option>
                                        <option value="4"><hl:message key="rotulo.correspondente.singular"/></option>
                                        <option value="7"><hl:message key="rotulo.suporte.singular"/></option>
                                    </optgroup>
                                </select><input type='hidden' name='PAPEL' id='PAPEL'/>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
            <div class="btn-action">
                <a class="btn btn-outline-danger" href="#no-back" id="btnVolta"
                   onClick="postData('../v3/carregarPrincipal')"><hl:message key="rotulo.botao.voltar"/></a>
                <a class="btn btn-primary" href="#" id="btnEnvia" onClick="validaSubmit()"><hl:message
                        key="rotulo.botao.pesquisar"/></a>
            </div>
        </div>
    </div>
    <script type="text/JavaScript">
        var f0 = document.forms[0];
        var requerAmbos = <%=requerMatriculaCpf%>;
        window.onload = formLoad;

        function formLoad() {
            if (f0.ADE_NUMERO) {
                f0.ADE_NUMERO.focus();
            } else if (f0.RSE_MATRICULA) {
                f0.RSE_MATRICULA.focus();
            } else if (f0.SER_CPF) {
                f0.SER_CPF.focus();
            }
        }

        function habilitaSituacao(variavel) {
            var escolha = variavel.value;
            if (escolha == "INTEGRADAS") {
                document.getElementById('SITUACOES').disabled = false;
                document.getElementById('PAPEIS').disabled = false;
                <% if (ShowFieldHelper.showField(FieldKeysConstants.CADASTRAR_RETORNO_INTEGRACAO_TOC_CODIGO, responsavel)) { %>
                document.getElementById('OCORRENCIAS').disabled = false;
                <% } %>
            } else {
                document.getElementById('SITUACOES').disabled = true;
                document.getElementById('SITUACOES').value = '';
                document.getElementById('PAPEIS').disabled = true;
                document.getElementById('PAPEIS').value = '';
                <% if (ShowFieldHelper.showField(FieldKeysConstants.CADASTRAR_RETORNO_INTEGRACAO_TOC_CODIGO, responsavel)) { %>
                document.getElementById('OCORRENCIAS').disabled = true;
                document.getElementById('OCORRENCIAS').value = '';
                <% } %>
            }
        }

        function pegaSituacao() {
            var situa = $('#SITUACOES').val();
            f0.SITUACAO.value = situa;
        }

        function pegaPapel() {
            var pap = $('#PAPEIS').val();
            f0.PAPEL.value = pap;
        }

        function pegaTipoOcorrencia() {
            var tipoOcorrencia = $('#OCORRENCIAS').val();
            if (typeof tipoOcorrencia != 'undefined') {
                f0.TOC_CODIGO.value = tipoOcorrencia;
            }
        }

        function validaSubmit() {
            pegaSituacao();
            pegaPapel();
            pegaTipoOcorrencia();

            <%if (ShowFieldHelper.isRequired(FieldKeysConstants.CADASTRAR_RETORNO_INTEGRACAO_ADE_IDENTIFICADOR, responsavel)) { %>
            if (f0.ADE_IDENTIFICADOR.value == null || f0.ADE_IDENTIFICADOR.value == '') {
                f0.ADE_IDENTIFICADOR.focus();
                alert('<hl:message key="mensagem.informe.ade.identificador"/>');
                return false;
            }
            <%} %>

            <%if (ShowFieldHelper.isRequired(FieldKeysConstants.CADASTRAR_RETORNO_INTEGRACAO_ORG_CODIGO, responsavel)) { %>
            if (f0.ORG_CODIGO.value == null || f0.ORG_CODIGO.value == '') {
                f0.ORG_CODIGO.focus();
                alert('<hl:message key="mensagem.informe.org.nome"/>');
                return false;
            }
            <%} %>

            <%if (ShowFieldHelper.isRequired(FieldKeysConstants.CADASTRAR_RETORNO_INTEGRACAO_TOC_CODIGO, responsavel)) { %>
            if (f0.TOC_CODIGO.value == null || f0.TOC_CODIGO.value == '') {
                f0.OCORRENCIAS.focus();
                alert('<hl:message key="mensagem.informe.tipo.ocorrencia"/>');
                return false;
            }
            <%} %>

            if (vf_pesquisa_retorno(requerAmbos) && vfRseMatricula()) {
                if (typeof vfRseMatricula === 'function') {
                    if (vfRseMatricula(true) || (f0.RSE_MATRICULA.value === '' || f0.RSE_MATRICULA.value != null)) {
                        f0.submit();
                    }
                } else {
                    f0.submit();
                }
            }
        }

        function selectName(x) {
            if(x.value !== '') {
                $('#CSA_NOME').val(x.options[x.selectedIndex].text);
            } else {
                $('#CSA_NOME').val('');
            }
        }
    </script>
    <hl:campoMatriculav4 scriptOnly="true"/>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4><%!
%>