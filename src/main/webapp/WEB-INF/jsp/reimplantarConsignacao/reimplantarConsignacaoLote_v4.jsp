<%--
  Created by IntelliJ IDEA.
  User: douglas.neves
  Date: 16/05/2023
  Time: 14:59
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.helper.texto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.ParamSvcTO" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession"/>
<%
    AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

    boolean permiteAlterarNumeroAde = (ParamSist.paramEquals(CodedValues.TPC_PERMITE_ALTERAR_ADE_NUMERO_REIMP_MANUAL, CodedValues.TPC_SIM, responsavel) && !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_CLASSE_GERADOR_ADE_NUMERO, responsavel)));
    boolean flowOne = (boolean) request.getAttribute("flowOne");
    List<String> adeCodigosFull = request.getAttribute("adeCodigosFull") != null ? (List<String>) request.getAttribute("adeCodigosFull") : null;
    List<CustomTransferObject> consignacoes = new ArrayList<>();
    String retornoPath = request.getAttribute("retornoPath") != null ? (String) request.getAttribute("retornoPath") : "";
    if (!flowOne) {
        consignacoes = (List<CustomTransferObject>) request.getAttribute("consignacoes");
    }
    //java
%>
<c:set var="title">
    <hl:message key="rotulo.efetiva.acao.consignacao.reimplantar.lote"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
    <% if (flowOne) { %>
    <form name="form1" method="post"
          action="../v3/reimplantarConsignacaoLote?acao=processar&<%=SynchronizerToken.generateToken4URL(request)%>">
        <input type="hidden" id="ADES" name="ADES">
        <div class="btn-action">
            <button class="btn btn-primary" onclick="postData('../v3/reimplantarConsignacaoLote?acao=listarRelatorios'); return false;">
                <hl:message key="rotulo.button.download.relatorio"/>
            </button>
        </div>
        <%if (!TextHelper.isNull(retornoPath) && !retornoPath.isEmpty()) { %>
        <div class="btn-action">
            <button class="btn btn-primary" onclick="downloadRetorno(); return false;">
                <hl:message key="rotulo.button.download.retorno"/>
            </button>
        </div>
        <% } %>
        <div class="card">
            <div class="card-header">
                <h2 class="card-header-title"><hl:message
                        key="rotulo.preencha.dados.reimplante.lote"/></h2>
            </div>
            <div class="card-body">
                    <%-- Disponibiliza upload de arquivo para recalculo de margem parcial --%>
                <hl:fileUploadV4
                        divClassArquivo="form-group col-sm-6 mb-1"
                        obrigatorio="false"
                        multiplo="false"
                        mostraCampoDescricao="false"
                        extensoes="<%=new String[]{"txt"}%>"
                        tipoArquivo="reimplantar_lote"/>
                <div class="row">
                    <div class="form-group col-sm-12  col-md-6">
                        <label for="ADE_NUMERO"><hl:message key="rotulo.lista"/></label>
                        <input type="text" name="ADE_NUMERO" id="ADE_NUMERO" class="form-control"
                               onfocus="SetarEventoMascaraV4(this,'#D18',true);"
                               onblur="fout(this);ValidaMascaraV4(this);"
                               oninput="this.value = this.value.replace(/[^0-9.]/g, '').replace(/(\..*?)\..*/g, '$1');"
                               placeholder="<%=ApplicationResourcesHelper.getMessage("rotulo.digite.numero.ade", responsavel)%>"
                               style="background-color: white; color: black;">
                    </div>
                    <div class="form-group col-sm-12 col-md-1 mt-4">
                        <a id="adicionaAdeLista" class="btn btn-primary w-50" href="javascript:void(0);"
                           onClick="insereItem('ADE_NUMERO', 'LISTA');"
                           aria-label='<hl:message key="mensagem.inserir.ade.numero.clique.aqui"/>'>
                            <svg width="15">
                                <use xlink:href="../img/sprite.svg#i-avancar"></use>
                            </svg>
                        </a>
                        <a id="removeAdeLista" class="btn btn-primary w-50 mt-1" href="javascript:void(0);"
                           onClick="removeDaLista('LISTA');"
                           aria-label='<hl:message key="mensagem.remover.ade.numero.clique.aqui"/>'>
                            <svg width="15">
                                <use xlink:href="../img/sprite.svg#i-voltar"></use>
                            </svg>
                        </a>
                    </div>
                    <div id="adeLista" class="form-group col-sm-12 col-md-5">
                        <label for="LISTA"><hl:message key="rotulo.lista"/></label>
                        <select class="form-control w-100" id="LISTA" multiple="multiple" size="6"></select>
                    </div>
                </div>
            </div>
        </div>
        <div class="btn-action">
            <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal')"
               title="<hl:message key="rotulo.botao.voltar"/>"><hl:message key="rotulo.botao.cancelar"/></a>
            <a class="btn btn-primary" id="submit" href="#no-back" onClick="if (vrAcao('pesquisar')) { f0.submit(); } return false;"
               title="<hl:message key="rotulo.botao.confirmar"/>"><hl:message key="rotulo.botao.pesquisar"/></a>
        </div>
    </form>
    <% } else { %>
    <form name="form1" method="post"
          action="../v3/reimplantarConsignacaoLote?acao=reimplantar&<%=SynchronizerToken.generateToken4URL(request)%>">
        <% if (!adeCodigosFull.isEmpty() && !TextHelper.isNull(adeCodigosFull)) {
                for (String adeCodigo : adeCodigosFull) { %>
        <input type="hidden" value="<%=adeCodigo%>" name="adeFullCodigos">
        <% }
        } %>
        <%if (permiteAlterarNumeroAde) { %>
        <div class="card">
            <div class="card-header">
                <h2 class="card-header-title">
                    <hl:message key="rotulo.transf.contratos.dados.operacao"/>
                </h2>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-sm-12 col-md-6">
                        <div class="form-group mb-1" role="radiogroup" aria-labelledby="alterarNumeroAde">
                            <div><span id="alterarNumeroAde"> <hl:message
                                    key="mensagem.informe.alterar.numero.contrato"/></span></div>
                            <div class="form-check form-check-inline pt-3">
                                <input class="form-check-input ml-1" type="radio" name="alterarNumeroAde"
                                       value="S" id="mudaNumSim"
                                       title="<hl:message key="rotulo.sim"/>">
                                <label class="form-check-label labelSemNegrito ml-1 pr-4" for="mudaNumSim">
                                    <hl:message key="rotulo.sim"/>
                                </label>
                            </div>
                            <div class="form-check form-check-inline pt-3">
                                <input class="form-check-input ml-1" type="radio" name="alterarNumeroAde" value="N"
                                       id="mudaNumNao"
                                       title="<hl:message key="rotulo.nao"/>" checked>
                                <label class="form-check-label labelSemNegrito ml-1 pr-4" for="mudaNumNao">
                                    <hl:message key="rotulo.nao"/>
                                </label>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <% } %>
        <div class="card">
            <div class="card-header">
                <h2 class="card-header-title">
                    <hl:message key="mensagem.informe.contratos.reimplante"/>
                </h2>
            </div>
            <div class="card-body table-responsive p-0">
                <table class="table table-striped table-hover">
                    <thead>
                    <tr>
                        <th scope="col" width="3%" class="colunaUnica">
                            <div class="form-check">
                                <input type="checkbox" class="form-check-input ml-0" name="checkAll_chkADE"
                                       id="checkAll_chkADE" checked data-bs-toggle="tooltip" data-original-title=""
                                       alt=""
                                       title=""
                                >
                            </div>
                        </th>
                        <th scope="col"><hl:message
                                key="<%= (!responsavel.isCsa()) ? "rotulo.consignataria.singular" : "rotulo.correspondente.singular" %>"/></th>
                        <th scope="col"><hl:message key="rotulo.consignacao.responsavel"/></th>
                        <th scope="col"><hl:message key="rotulo.consignacao.numero"/></th>
                        <th scope="col"><hl:message key="rotulo.consignacao.identificador"/></th>
                        <th scope="col"><hl:message key="rotulo.servico.singular"/></th>
                        <th scope="col"><hl:message key="rotulo.consignacao.data.inclusao"/></th>
                        <th scope="col"><hl:message key="rotulo.consignacao.valor.parcela.abreviado"/></th>
                        <th scope="col"><hl:message key="rotulo.consignacao.prazo.abreviado"/></th>
                        <th scope="col"><hl:message key="rotulo.consignacao.pagas"/></th>
                        <th scope="col"><hl:message key="rotulo.consignacao.status"/></th>
                        <th scope="col"><hl:message key="rotulo.acoes"/></th>
                    </tr>
                    </thead>
                    <tbody>
                    <%
                        if (!consignacoes.isEmpty() && !TextHelper.isNull(consignacoes)) {
                            String adeNumero, adeCodigo, adeTipoVlr, adeData, adePrazo, adeVlr, adeIdentificador, prdPagas, adeCodReg;
                            String nome, servico, sadDescricao;
                            String loginResponsavel, adeResponsavel;

                            CustomTransferObject ade = null;
                            Iterator<CustomTransferObject> it = consignacoes.iterator();
                            while (it.hasNext()) {
                                ade = it.next();

                                adeCodigo = ade.getAttribute(Columns.ADE_CODIGO).toString();
                                adeTipoVlr = (String) ade.getAttribute(Columns.ADE_TIPO_VLR);
                                adeVlr = ade.getAttribute(Columns.ADE_VLR) != null ? ade.getAttribute(Columns.ADE_VLR).toString() : "";
                                if (!adeVlr.equals("")) {
                                    adeVlr = NumberHelper.format(Double.parseDouble(adeVlr), NumberHelper.getLang());
                                }
                                adeNumero = ade.getAttribute(Columns.ADE_NUMERO).toString();
                                adeIdentificador = ade.getAttribute(Columns.ADE_IDENTIFICADOR) != null ? ade.getAttribute(Columns.ADE_IDENTIFICADOR).toString() : "";
                                adePrazo = ade.getAttribute(Columns.ADE_PRAZO) != null ? ade.getAttribute(Columns.ADE_PRAZO).toString() : ApplicationResourcesHelper.getMessage("rotulo.indeterminado", responsavel);
                                adeData = DateHelper.reformat(ade.getAttribute(Columns.ADE_DATA).toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern());
                                prdPagas = ade.getAttribute(Columns.ADE_PRD_PAGAS) != null ? ade.getAttribute(Columns.ADE_PRD_PAGAS).toString() : "0";
                                adeCodReg = ((ade.getAttribute(Columns.ADE_COD_REG) != null && !ade.getAttribute(Columns.ADE_COD_REG).equals("")) ? ade.getAttribute(Columns.ADE_COD_REG).toString() : CodedValues.COD_REG_DESCONTO);
                                adeNumero = ade.getAttribute(Columns.ADE_NUMERO).toString();
                                adeIdentificador = ade.getAttribute(Columns.ADE_IDENTIFICADOR) != null ? ade.getAttribute(Columns.ADE_IDENTIFICADOR).toString() : "";
                                adePrazo = ade.getAttribute(Columns.ADE_PRAZO) != null ? ade.getAttribute(Columns.ADE_PRAZO).toString() : ApplicationResourcesHelper.getMessage("rotulo.indeterminado", responsavel);
                                adeData = DateHelper.reformat(ade.getAttribute(Columns.ADE_DATA).toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern());
                                prdPagas = ade.getAttribute(Columns.ADE_PRD_PAGAS) != null ? ade.getAttribute(Columns.ADE_PRD_PAGAS).toString() : "0";
                                servico = (ade.getAttribute(Columns.CNV_COD_VERBA) != null && !ade.getAttribute(Columns.CNV_COD_VERBA).toString().equals("")) ? ade.getAttribute(Columns.CNV_COD_VERBA).toString() : ade.getAttribute(Columns.SVC_IDENTIFICADOR).toString();
                                servico += (ade.getAttribute(Columns.ADE_INDICE) != null && !ade.getAttribute(Columns.ADE_INDICE).toString().equals("")) ? ade.getAttribute(Columns.ADE_INDICE).toString() : "";
                                servico += " - " + ade.getAttribute(Columns.SVC_DESCRICAO).toString();
                                adeCodReg = ((ade.getAttribute(Columns.ADE_COD_REG) != null && !ade.getAttribute(Columns.ADE_COD_REG).equals("")) ? ade.getAttribute(Columns.ADE_COD_REG).toString() : CodedValues.COD_REG_DESCONTO);
                                nome = (String) ade.getAttribute(Columns.CSA_NOME_ABREV);

                                if (nome == null || nome.trim().length() == 0) {
                                    nome = ade.getAttribute(Columns.CSA_NOME).toString();
                                }

                                loginResponsavel = ade.getAttribute(Columns.USU_LOGIN) != null ? ade.getAttribute(Columns.USU_LOGIN).toString() : "";
                                adeResponsavel = (loginResponsavel.equalsIgnoreCase((String) ade.getAttribute(Columns.USU_CODIGO)) && ade.getAttribute(Columns.USU_TIPO_BLOQ) != null) ? (ade.getAttribute(Columns.USU_TIPO_BLOQ).toString() + "(*)") : loginResponsavel;
                                sadDescricao = ade.getAttribute(Columns.SAD_DESCRICAO).toString();
                    %>
                    <input type="hidden" name="ADE_CODIGO_NUMBER" value="<%=TextHelper.forHtmlAttribute(adeCodigo)%>">
                    <tr class="selecionarLinha">
                        <td class="colunaUnica" aria-label="" title="" data-bs-toggle="tooltip" data-original-title="">
                            <div class="form-check">
                                <input type="checkbox" class="form-check-input ml-0" checked name="chkAdeCodigo"
                                       value="<%=TextHelper.forHtmlAttribute(adeCodigo)%>">
                            </div>
                        </td>
                        <td class="selecionarColuna"><%=TextHelper.forHtmlContent(nome)%>
                        </td>
                        <td class="selecionarColuna"><%=TextHelper.forHtmlContent(adeResponsavel)%>
                        </td>
                        <td class="selecionarColuna"><%=TextHelper.forHtmlContent(adeNumero)%>
                        </td>
                        <td class="selecionarColuna"><%=TextHelper.forHtmlContent(adeIdentificador)%>
                        </td>
                        <td class="selecionarColuna"><%=TextHelper.forHtmlContent(servico + (adeCodReg.equals(CodedValues.COD_REG_ESTORNO) ? " - " + ApplicationResourcesHelper.getMessage("rotulo.consignacao.cod.reg.credito", responsavel) : ""))%>&nbsp;</td>
                        <td class="selecionarColuna"><%=TextHelper.forHtmlContent(adeData)%>
                        </td>
                        <td class="selecionarColuna"><%=TextHelper.forHtmlContent(ParamSvcTO.getDescricaoTpsTipoVlr(adeTipoVlr))%> <%=TextHelper.forHtmlContent(adeVlr)%>
                        </td>
                        <td class="selecionarColuna"><%=TextHelper.forHtmlContent(adePrazo)%>
                        </td>
                        <td class="selecionarColuna"><%=TextHelper.forHtmlContent(prdPagas)%>
                        </td>
                        <td class="selecionarColuna"><%=TextHelper.forHtmlContent(sadDescricao)%>
                        </td>
                        <td class="acoes">
                            <div class="actions">
                                <a class="" href="#" onclick="escolhechk('Selecionar',this)"><hl:message
                                        key="rotulo.acoes.selecionar"/></a>
                            </div>
                        </td>
                    </tr>
                    <%
                        }
                    } else {
                    %>
                    <tr>
                        <td colspan="12"><hl:message
                                key="mensagem.erro.nenhuma.consignacao.encontrada.transferir.consignacao"/></td>
                    </tr>
                    <%
                        }
                    %>
                    </tbody>
                    <tfoot>
                    <tr>
                        <td colspan="12"><%=ApplicationResourcesHelper.getMessage("rotulo.consignacao.listagem.consignacao", responsavel) %>
                            <span class="font-italic"> - <hl:message key="rotulo.paginacao.registros.sem.estilo"
                                                                     arg0="${_paginacaoPrimeiro}"
                                                                     arg1="${_paginacaoUltimo}"
                                                                     arg2="${_paginacaoQtdTotal}"/></span>
                    </tr>
                    </tfoot>
                </table>
                <div class="card-footer">
                    <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
                </div>
            </div>
        </div>
        <div class="btn-action">
            <a class="btn btn-outline-danger" href="#no-back"
               onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"
               title="<hl:message key="rotulo.botao.voltar"/>"><hl:message key="rotulo.botao.voltar"/></a>
            <% if (!consignacoes.isEmpty() && !TextHelper.isNull(consignacoes)) { %>
            <a class="btn btn-primary" id="submit" href="#no-back" onClick="if (vrAcao('processar')) { f0.submit(); } return false;"
               title="<hl:message key="rotulo.botao.confirmar"/>"><hl:message key="rotulo.botao.reimplantar.lote"/></a>
            <% } %>
        </div>
    </form>
    <% } %>
</c:set>
<c:set var="javascript">
    <hl:fileUploadV4 scriptOnly="true" tipoArquivo="reimplantar_lote" extensoes="<%=new String[]{"txt"}%>"
                     multiplo="false"/>
    <script type="text/JavaScript" src="../js/listagem.js"></script>
    <script>
        var f0 = document.forms[0];

        function vrAcao(acao) {
            if (acao === 'pesquisar') {
                const file1 = document.getElementById('FILE1');
                const lista = document.getElementById('LISTA');

                if (file1 === undefined || file1.value.trim() === '') {
                    if (lista === undefined || lista.length === undefined || lista.length === 0) {
                        alert('<%=ApplicationResourcesHelper.getMessage("rotulo.informe.ade.numero.reimplante.lote", responsavel)%>');
                        f0.ADE_NUMERO.focus();
                        return false;
                    }
                    montaListaIps('ADES', 'LISTA');
                    return true;
                } else {
                    return true;
                }
            } else if (acao === 'processar') {
                var cont = 0;

                for (i = 0; i < f0.elements.length; i++) {
                    var e = f0.elements[i];
                    if (((e.type == 'check') || (e.type == 'checkbox')) && e.checked) {
                        cont++;
                    }
                }

                if (cont < 1) {
                    alert('<hl:message key="mensagem.informe.um.contrato.transferencia"/>');
                    return false;
                } else if (confirm('Contratos selecionados serão reimplantados, quer continuar?')) {
                    f0.submit();
                }
            }
        }

        var clicklinha = false;

        $(".selecionarColuna").click(function () {
            // 1- Seleciona a linha e mostra a coluna dos checks

            var checked = $("table tbody tr input[type=checkbox]:checked").length;

            if (checked === 0) {

                if (clicklinha) {
                    $("table th:nth-child(-n+1)").hide();
                    $(".colunaUnica").hide();
                } else {
                    $("table th:nth-child(-n+1)").show();
                    $(".colunaUnica").show();
                }

                clicklinha = !clicklinha;
            }
        });

        var verificarCheckbox = function () {
            var checked = $("table tbody tr input[type=checkbox]:checked").length;
            var total = $("table tbody tr input[type=checkbox]").length;
            $("input[id*=checkAll_]").prop('checked', checked == total);
            if (checked === 0) {
                $("table thead tr th.colunaUnica, table tbody tr td.colunaUnica").hide();
            } else {
                $("table thead tr th.colunaUnica, table tbody tr td.colunaUnica").show();
            }
        };

        $("table tbody tr td").not("td.colunaUnica, td.acoes").click(function (e) {
            $(e.target).parents('tr').find('input[type=checkbox]').click();
        });

        function escolhechk(idchk, e) {
            $(e).parents('tr').find('input[type=checkbox]').click();
            document.getElementById("LISTA").value = $(e).parents('tr').find('input[type=checkbox]').val();
        }

        $("table tbody tr input[type=checkbox]").click(function (e) {
            verificarCheckbox();
            var checked = e.target.checked;
            if (checked) {
                $(e.target).parents('tr').addClass("table-checked");
            } else {
                $(e.target).parents('tr').removeClass("table-checked");
            }
        });

        $("input[id*=checkAll_").click(function (e) {
            var checked = e.target.checked;
            $('table tbody tr input[type=checkbox]').prop('checked', checked);
            if (checked) {
                $("table tbody tr").addClass("table-checked");
            } else {
                $("table tbody tr").removeClass("table-checked");
            }
            verificarCheckbox();
        });

        function downloadRetorno() {
            if (confirm('<hl:message key="mensagem.confirmacao.download.retorno"/>')) {
                postData('../v3/downloadArquivo?tipo=reimplante&arquivo_nome=' + encodeURIComponent('<%=retornoPath%>') + '&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');
            } else {
                return false;
            }
        }

    </script>

</c:set>

<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>