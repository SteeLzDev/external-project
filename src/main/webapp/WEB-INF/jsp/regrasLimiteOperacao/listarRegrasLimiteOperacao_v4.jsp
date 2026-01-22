<%--
* <p>Title: listarRegrasLimiteOperacao_v4</p>
* <p>Description: Listar Regras Limite Operacao v4</p>
* <p>Copyright: Copyright (c) 2024</p>
* <p>Company: ZetraSoft Internet Service</p>
  Created by IntelliJ IDEA.
  User: douglas.neves
  Date: 16/09/2024
  Time: 11:40
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="java.util.List" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.dto.TransferObject" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession"/>
<%
    boolean exibeBotaoRodape = request.getAttribute("exibeBotaoRodape") != null && (boolean) (request.getAttribute("exibeBotaoRodape"));
    AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
    List<TransferObject> regraLimiteOperacaos = (List) request.getAttribute("regraLimiteOperacoes");

%>
<c:set var="javascript">
    <script type="text/JavaScript">

    </script>
</c:set>
<c:set var="title">
    <hl:message key="rotulo.regra.limite.operacao.listar"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
    <input type="hidden" name="csaCodigo"/>
    <div class="row">
        <div class="col-sm-12 col-md-12 mb-2">
            <div class="float-end">
                <a class="btn btn-primary"
                   onClick="postData('../v3/regrasLimiteOperacao?acao=criarEditarRegra&NOVO=S&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>')"
                   href="#no-back"><hl:message key="rotulo.regra.limite.operacao.nova"/></a>
            </div>
        </div>
    </div>
    <div class="row">
    <div class="col-sm">
    <form NAME="form1" METHOD="post"
    ACTION="../v3/editarRegraTaxaJuros?acao=consultar&_skip_history_=true&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute("123")%>&<%=SynchronizerToken.generateToken4URL(request)%>">
    <div class="row">
        <div class="col-sm">
            <div class="card">
                <div class="card-header">
                    <h3 class="card-header-title">
                        <hl:message key="rotulo.regra.limite.operacao.listar"/>
                    </h3>
                </div>
                <div class="card-body p-0 table-responsive">
                    <table id="rules" class="table table-striped table-hover">
                        <thead>
                        <tr>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_ESTABELECIMENTO, responsavel)) { %>
                            <th><hl:message key="rotulo.estabelecimento.singular"/></th>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_ORGAO, responsavel)) { %>
                            <th><hl:message key="rotulo.orgao.singular"/></th>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_SUBORGAO, responsavel)) { %>
                            <th><hl:message key="rotulo.suborgao.singular"/></th>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_UNIDADE, responsavel)) { %>
                            <th><hl:message key="rotulo.unidade.singular"/></th>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_NATUREZA_CSA, responsavel)) { %>
                            <th><hl:message key="rotulo.natureza.consignataria.singular"/></th>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_CONSIGNATARIA, responsavel)) { %>
                            <th><hl:message key="rotulo.consignataria.singular"/></th>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_CORRESPONDENTE, responsavel)) { %>
                            <th><hl:message key="rotulo.correspondente.singular"/></th>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_NATUREZA_SVC, responsavel)) { %>
                            <th><hl:message key="rotulo.natureza.servico.titulo"/></th>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_SERVICO, responsavel)) { %>
                            <th><hl:message key="rotulo.servico.singular"/></th>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_CAPACIDADE, responsavel)) { %>
                            <th><hl:message key="rotulo.capacidade.registro.servidor.singular"/></th>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_CARGO, responsavel)) { %>
                            <th><hl:message key="rotulo.cargo.registro.servidor.singular"/></th>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_PADRAO, responsavel)) { %>
                            <th><hl:message key="rotulo.padrao.registro.servidor.singular"/></th>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_POSTO, responsavel)) { %>
                            <th><hl:message key="rotulo.posto.registro.servidor.singular"/></th>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_TIPO, responsavel)) { %>
                            <th><hl:message key="rotulo.tipo.registro.servidor.singular"/></th>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_STATUS, responsavel)) { %>
                            <th><hl:message key="rotulo.status.registro.servidor.singular"/></th>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_VINCULO, responsavel)) { %>
                            <th><hl:message key="rotulo.vinculo.registro.servidor.singular"/></th>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_FUNCAO, responsavel)) { %>
                            <th><hl:message key="rotulo.funcao.singular"/></th>
                            <% } %>
                            <th><hl:message key="rotulo.regra.limite.operacao.data.vigencia.inicial"/></th>
                            <th><hl:message key="rotulo.regra.limite.operacao.data.vigencia.final"/></th>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_FAIXA_ETARIA, responsavel)) { %>
                            <th><hl:message key="rotulo.regra.limite.operacao.faixa.etaria.inicial"/></th>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_FAIXA_ETARIA, responsavel)) { %>
                            <th><hl:message key="rotulo.regra.limite.operacao.faixa.etaria.final"/></th>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_FAIXA_SALARIO, responsavel)) { %>
                            <th><hl:message key="rotulo.regra.limite.operacao.faixa.salario.inicial"/></th>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_FAIXA_SALARIO, responsavel)) { %>
                            <th><hl:message key="rotulo.regra.limite.operacao.faixa.salario.final"/></th>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_MARGEM_FOLHA, responsavel)) { %>
                            <th><hl:message key="rotulo.regra.limite.operacao.faixa.margem.inicial"/></th>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_MARGEM_FOLHA, responsavel)) { %>
                            <th><hl:message key="rotulo.regra.limite.operacao.faixa.margem.final"/></th>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_TEMPO_SERVICO, responsavel)) { %>
                            <th><hl:message key="rotulo.regra.limite.operacao.faixa.tempo.servico.inicial"/></th>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_TEMPO_SERVICO, responsavel)) { %>
                            <th><hl:message key="rotulo.regra.limite.operacao.faixa.tempo.servico.final"/></th>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_PADRAO_VERBA_REF, responsavel)) { %>
                            <th><hl:message key="rotulo.regra.limite.operacao.padrao.verba.ref"/></th>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_PADRAO_VERBA, responsavel)) { %>
                            <th><hl:message key="rotulo.regra.limite.operacao.padrao.verba"/></th>
                            <% } %>
                            <th><hl:message key="rotulo.regra.limite.operacao.data.cadastro"/></th>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_PADRAO_MATRICULA, responsavel)) { %>
                            <th><hl:message key="rotulo.regra.limite.operacao.padrao.matricula"/></th>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_PADRAO_CATEGORIA, responsavel)) { %>
                            <th><hl:message key="rotulo.regra.limite.operacao.padrao.categoria"/></th>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_MENSAGEM_ERRO, responsavel)) { %>
                            <th><hl:message key="rotulo.regra.limite.operacao.mensagem.erro"/></th>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_LIMITE_CAPITAL_DEVIDO, responsavel)) { %>
                            <th><hl:message key="rotulo.regra.limite.operacao.limite.capital.devido"/></th>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_LIMITE_DATA_ADE, responsavel)) { %>
                            <th><hl:message key="rotulo.regra.limite.operacao.limite.data.ade"/></th>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_LIMITE_PRAZO, responsavel)) { %>
                            <th><hl:message key="rotulo.regra.limite.operacao.limite.prazo"/></th>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_LIMITE_QUANTIDADE, responsavel)) { %>
                            <th><hl:message key="rotulo.regra.limite.operacao.limite.quantidade"/></th>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_LIMITE_VLR_LIBERADO, responsavel)) { %>
                            <th><hl:message key="rotulo.regra.limite.operacao.limite.vlr.liberado"/></th>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_LIMITE_VLR_PARCELA, responsavel)) { %>
                            <th><hl:message key="rotulo.regra.limite.operacao.limite.vlr.parcela"/></th>
                            <% } %>
                            <th><hl:message key="rotulo.acoes"/></th>
                        </tr>
                        </thead>
                        <tbody>
                        <%
                            for (TransferObject regra : regraLimiteOperacaos) {
                        %>
                        <tr>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_ESTABELECIMENTO, responsavel)) { %>
                            <td><%=regra.getAttribute(Columns.RLO_EST_CODIGO) != null ? regra.getAttribute(Columns.EST_NOME) : ""%>
                            </td>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_ORGAO, responsavel)) { %>
                            <td><%=regra.getAttribute(Columns.RLO_ORG_CODIGO) != null ? regra.getAttribute(Columns.ORG_NOME) : ""%>
                            </td>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_SUBORGAO, responsavel)) { %>
                            <td><%=regra.getAttribute(Columns.RLO_SBO_CODIGO) != null ? regra.getAttribute(Columns.SBO_DESCRICAO) : ""%>
                            </td>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_UNIDADE, responsavel)) { %>
                            <td><%=regra.getAttribute(Columns.RLO_UNI_CODIGO) != null ? regra.getAttribute(Columns.UNI_DESCRICAO) : ""%>
                            </td>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_NATUREZA_CSA, responsavel)) { %>
                            <td><%=regra.getAttribute(Columns.RLO_NCA_CODIGO) != null ? regra.getAttribute(Columns.NCA_DESCRICAO) : ""%>
                            </td>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_CONSIGNATARIA, responsavel)) { %>
                            <td><%=regra.getAttribute(Columns.RLO_CSA_CODIGO) != null ? regra.getAttribute(Columns.CSA_NOME) : ""%>
                            </td>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_CORRESPONDENTE, responsavel)) { %>
                            <td><%=regra.getAttribute(Columns.RLO_COR_CODIGO) != null ? regra.getAttribute(Columns.COR_NOME) : ""%>
                            </td>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_NATUREZA_SVC, responsavel)) { %>
                            <td><%=regra.getAttribute(Columns.RLO_NSE_CODIGO) != null ? regra.getAttribute(Columns.NSE_DESCRICAO) : ""%>
                            </td>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_SERVICO, responsavel)) { %>
                            <td><%=regra.getAttribute(Columns.RLO_SVC_CODIGO) != null ? regra.getAttribute(Columns.SVC_DESCRICAO) : ""%>
                            </td>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_CAPACIDADE, responsavel)) { %>
                            <td><%=regra.getAttribute(Columns.RLO_CAP_CODIGO) != null ? regra.getAttribute(Columns.CAP_DESCRICAO) : ""%>
                            </td>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_CARGO, responsavel)) { %>
                            <td><%=regra.getAttribute(Columns.RLO_CRS_CODIGO) != null ? regra.getAttribute(Columns.CRS_DESCRICAO) : ""%>
                            </td>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_PADRAO, responsavel)) { %>
                            <td><%=regra.getAttribute(Columns.RLO_PRS_CODIGO) != null ? regra.getAttribute(Columns.PRS_DESCRICAO) : ""%>
                            </td>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_POSTO, responsavel)) { %>
                            <td><%=regra.getAttribute(Columns.POS_CODIGO) != null ? regra.getAttribute(Columns.POS_DESCRICAO) : ""%>
                            </td>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_TIPO, responsavel)) { %>
                            <td><%=regra.getAttribute(Columns.RLO_TRS_CODIGO) != null ? regra.getAttribute(Columns.TRS_DESCRICAO) : ""%>
                            </td>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_STATUS, responsavel)) { %>
                            <td><%=regra.getAttribute(Columns.RLO_SRS_CODIGO) != null ? regra.getAttribute(Columns.SRS_DESCRICAO) : ""%>
                            </td>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_VINCULO, responsavel)) { %>
                            <td><%=regra.getAttribute(Columns.RLO_VRS_CODIGO) != null ? regra.getAttribute(Columns.VRS_DESCRICAO) : ""%>
                            </td>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_FUNCAO, responsavel)) { %>
                            <td><%=regra.getAttribute(Columns.RLO_FUN_CODIGO) != null ? regra.getAttribute(Columns.FUN_DESCRICAO) : ""%>
                            </td>
                            <% } %>
                            <td><%=regra.getAttribute(Columns.RLO_DATA_VIGENCIA_INI) != null ? DateHelper.reformat(regra.getAttribute(Columns.RLO_DATA_VIGENCIA_INI).toString().replace(".0", ""), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern()) : ""%>
                            </td>
                            <td><%=regra.getAttribute(Columns.RLO_DATA_VIGENCIA_FIM) != null ? DateHelper.reformat(regra.getAttribute(Columns.RLO_DATA_VIGENCIA_FIM).toString().replace(".0", ""), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern()) : ""%>
                            </td>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_FAIXA_ETARIA, responsavel)) { %>
                            <td><%=regra.getAttribute(Columns.RLO_FAIXA_ETARIA_INI) != null ? regra.getAttribute(Columns.RLO_FAIXA_ETARIA_INI) : ""%>
                            </td>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_FAIXA_ETARIA, responsavel)) { %>
                            <td><%=regra.getAttribute(Columns.RLO_FAIXA_ETARIA_FIM) != null ? regra.getAttribute(Columns.RLO_FAIXA_ETARIA_FIM) : ""%>
                            </td>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_FAIXA_SALARIO, responsavel)) { %>
                            <td><%=regra.getAttribute(Columns.RLO_FAIXA_SALARIO_INI) != null ? regra.getAttribute(Columns.RLO_FAIXA_SALARIO_INI).toString().replace(".", ",") : ""%>
                            </td>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_FAIXA_SALARIO, responsavel)) { %>
                            <td><%=regra.getAttribute(Columns.RLO_FAIXA_SALARIO_FIM) != null ? regra.getAttribute(Columns.RLO_FAIXA_SALARIO_FIM).toString().replace(".", ",") : ""%>
                            </td>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_MARGEM_FOLHA, responsavel)) { %>
                            <td><%=regra.getAttribute(Columns.RLO_FAIXA_MARGEM_FOLHA_INI) != null ? regra.getAttribute(Columns.RLO_FAIXA_MARGEM_FOLHA_INI).toString().replace(".", ",") : ""%>
                            </td>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_MARGEM_FOLHA, responsavel)) { %>
                            <td><%=regra.getAttribute(Columns.RLO_FAIXA_MARGEM_FOLHA_FIM) != null ? regra.getAttribute(Columns.RLO_FAIXA_MARGEM_FOLHA_FIM).toString().replace(".", ",") : ""%>
                            </td>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_TEMPO_SERVICO, responsavel)) { %>
                            <td><%=regra.getAttribute(Columns.RLO_FAIXA_TEMPO_SERVICO_INI) != null ? regra.getAttribute(Columns.RLO_FAIXA_TEMPO_SERVICO_INI) : ""%>
                            </td>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_TEMPO_SERVICO, responsavel)) { %>
                            <td><%=regra.getAttribute(Columns.RLO_FAIXA_TEMPO_SERVICO_FIM) != null ? regra.getAttribute(Columns.RLO_FAIXA_TEMPO_SERVICO_FIM) : ""%>
                            </td>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_PADRAO_VERBA_REF, responsavel)) { %>
                            <td><%=regra.getAttribute(Columns.RLO_PADRAO_VERBA_REF) != null ? regra.getAttribute(Columns.RLO_PADRAO_VERBA_REF) : ""%>
                            </td>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_PADRAO_VERBA, responsavel)) { %>
                            <td><%=regra.getAttribute(Columns.RLO_PADRAO_VERBA) != null ? regra.getAttribute(Columns.RLO_PADRAO_VERBA) : ""%>
                            </td>
                            <% } %>
                            <td><%=regra.getAttribute(Columns.RLO_DATA_CADASTRO) != null ? DateHelper.reformat(regra.getAttribute(Columns.RLO_DATA_CADASTRO).toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern()) : ""%>
                            </td>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_PADRAO_MATRICULA, responsavel)) { %>
                            <td><%=regra.getAttribute(Columns.RLO_PADRAO_MATRICULA) != null ? regra.getAttribute(Columns.RLO_PADRAO_MATRICULA) : ""%>
                            </td>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_PADRAO_CATEGORIA, responsavel)) { %>
                            <td><%=regra.getAttribute(Columns.RLO_PADRAO_CATEGORIA) != null ? regra.getAttribute(Columns.RLO_PADRAO_CATEGORIA) : ""%>
                            </td>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_MENSAGEM_ERRO, responsavel)) { %>
                            <td><%=regra.getAttribute(Columns.RLO_MENSAGEM_ERRO) != null ? regra.getAttribute(Columns.RLO_MENSAGEM_ERRO) : ""%>
                            </td>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_LIMITE_CAPITAL_DEVIDO, responsavel)) { %>
                            <td><%=regra.getAttribute(Columns.RLO_LIMITE_CAPITAL_DEVIDO) != null ? regra.getAttribute(Columns.RLO_LIMITE_CAPITAL_DEVIDO).toString().replace(".", ",") : ""%>
                            </td>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_LIMITE_DATA_ADE, responsavel)) { %>
                            <td><%=regra.getAttribute(Columns.RLO_LIMITE_DATA_FIM_ADE) != null ? DateHelper.reformat(regra.getAttribute(Columns.RLO_LIMITE_DATA_FIM_ADE).toString(), "yyyy-MM-dd", LocaleHelper.getDatePattern()) : ""%>
                            </td>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_LIMITE_PRAZO, responsavel)) { %>
                            <td><%=regra.getAttribute(Columns.RLO_LIMITE_PRAZO) != null ? regra.getAttribute(Columns.RLO_LIMITE_PRAZO) : ""%>
                            </td>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_LIMITE_QUANTIDADE, responsavel)) { %>
                            <td><%=regra.getAttribute(Columns.RLO_LIMITE_QUANTIDADE) != null ? regra.getAttribute(Columns.RLO_LIMITE_QUANTIDADE) : ""%>
                            </td>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_LIMITE_VLR_LIBERADO, responsavel)) { %>
                            <td><%=regra.getAttribute(Columns.RLO_LIMITE_VALOR_LIBERADO) != null ? regra.getAttribute(Columns.RLO_LIMITE_VALOR_LIBERADO).toString().replace(".", ",") : ""%>
                            </td>
                            <% } %>
                            <%if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_LIMITE_VLR_PARCELA, responsavel)) { %>
                            <td><%=regra.getAttribute(Columns.RLO_LIMITE_VALOR_PARCELA) != null ? regra.getAttribute(Columns.RLO_LIMITE_VALOR_PARCELA).toString().replace(".", ",") : ""%>
                            </td>
                            <% } %>
                            <td>
                                <div class="actions">
                                    <div class="dropdown">
                                        <a class="dropdown-toggle ico-action" href="#"
                                           role="button" id="userMenu"
                                           data-bs-toggle="dropdown" aria-haspopup="true"
                                           aria-expanded="false">
                                            <div class="form-inline">
                          <span class="mr-1" data-bs-toggle="tooltip"
                                title=""
                                data-original-title="<%=ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)%>"
                                aria-label="<%=ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)%>"> <svg>
                              <use
                                      xmlns:xlink="http://www.w3.org/1999/xlink"
                                      xlink:href="#i-engrenagem"></use></svg>
                          </span>
                                                <hl:message
                                                        key="rotulo.acoes.lst.arq.generico.opcoes"/>
                                            </div>
                                        </a>
                                        <div class="dropdown-menu dropdown-menu-right"
                                             aria-labelledby="userMenu">
                                            <a class="dropdown-item"
                                               onclick="postData('../v3/regrasLimiteOperacao?acao=criarEditarRegra&NOVO=N&RLO_CODIGO=<%=regra.getAttribute(Columns.RLO_CODIGO)%>&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>')"
                                               href="#no-back">
                                                <hl:message key="rotulo.acoes.editar"/>
                                            </a>
                                            <a class="dropdown-item"
                                               onclick="postData('../v3/regrasLimiteOperacao?acao=excluir&RLO_CODIGO=<%=regra.getAttribute(Columns.RLO_CODIGO)%>&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>')"
                                               href="#no-back">
                                                <hl:message key="rotulo.acoes.excluir"/>
                                            </a>
                                        </div>
                                    </div>
                                </div>
                            </td>
                        </tr>
                        <% } %>
                        </tbody>
                    </table>
                </div>
                <div class="card-footer align-content-center">
                    <%=ApplicationResourcesHelper.getMessage("rotulo.regra.limite.operacao.titulo.paginacao", responsavel) + " - "%>
                    <span class="font-italic"> <hl:message key="rotulo.paginacao.registros.sem.estilo"
                                                           arg0="${_paginacaoPrimeiro}"
                                                           arg1="${_paginacaoUltimo}"
                                                           arg2="${_paginacaoQtdTotal}"/></span>
                    <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
                </div>
            </div>
        </div>
    </div>
    <div class="btn-action">
        <a class="btn btn-outline-danger" href="#no-back"
           onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"
           id="btnVoltar"><hl:message key="rotulo.botao.voltar"/></a>
    </div>
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
    </div>
    <% }%>
</c:set>
<c:set var="javascript">
    <link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-buttons-dt/css/buttons.dataTables.min.css"/>
    <link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-responsive-bs5/css/responsive.bootstrap5.min.css"/>
    <link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-bs5/css/dataTables.bootstrap5.min.css"/>
    <script src="../node_modules/pdfmake/build/vfs_fonts.js"></script>
    <script src="../node_modules/datatables.net/js/jquery.dataTables.min.js"></script>
    <script src="../node_modules/datatables.net-bs5/js/dataTables.bootstrap5.min.js"></script>
    <script src="../node_modules/datatables.net-buttons/js/dataTables.buttons.min.js"></script>
    <script src="../node_modules/datatables.net-buttons/js/buttons.colVis.min.js"></script>
    <script src="../node_modules/datatables.net-buttons/js/buttons.html5.min.js"></script>
    <script src="../node_modules/datatables.net-buttons/js/buttons.print.min.js"></script>
    <script src="../node_modules/datatables.net-responsive/js/dataTables.responsive.min.js"></script>
    <script  src="../node_modules/moment/min/moment.min.js"></script>
  	<script  src="../node_modules/datatables.net-plugins/sorting/datetime-moment.js"></script>
    <script type="text/javascript">
        $('#rules').DataTable({
            "paging": false,
            "info": false,
            "pagingType": "simple_numbers",
            "dom": '<"row" <"col-sm-2" B > <"col-sm-6" l > <"col-sm-4" f >> <"table-responsive" t > <"card-footer" <"row" <"col-sm-6" i> <"col-sm-6" p >>>',
            buttons: [
                'colvis'
            ],
            stateSave: true,
            stateSaveParams: function (settings, data) {
                data.search.search = "";
            },
            language: {
                search: '_INPUT_',
                searchPlaceholder: '<hl:message key="mensagem.datatables.search.placeholder"/>',
                processing: '<hl:message key="mensagem.datatables.processing"/>',
                loadingRecords: '<hl:message key="mensagem.datatables.loading"/>',
                info: '<hl:message key="mensagem.datatables.info.consignatarias"/>',
                infoEmpty: '<hl:message key="mensagem.datatables.info.empty"/>',
                infoFiltered: '<hl:message key="mensagem.datatables.info.filtered"/>',
                infoPostFix: '',
                zeroRecords: '<hl:message key="mensagem.datatables.zero.records"/>',
                emptyTable: '<hl:message key="mensagem.datatables.empty.table"/>',
                aria: {
                    sortAscending: '<hl:message key="mensagem.datatables.aria.sort.ascending"/>',
                    sortDescending: '<hl:message key="mensagem.datatables.aria.sort.descending"/>'
                },
                buttons: {
                    print: '<hl:message key="mensagem.datatables.buttons.print"/>',
                    colvis: '<hl:message key="mensagem.datatables.buttons.colvis"/>'
                },
                decimal: ","
            },
            initComplete: function () {
                var btns = $('.dt-button');
                btns.addClass('btn btn-primary btn-sm');
                btns.removeClass('dt-button');
            }
        });

        $("#rules_filter").addClass('pt-2 px-3');
        $('#rules_info').addClass('p-3');
        $("#rules_length").addClass('pt-3');
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