<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String csa_codigo = (String) request.getAttribute("csaCodigo");
String svc_codigo = (String) request.getAttribute("svcCodigo");
String svcIdentificador = (String) request.getAttribute("svcIdentificador");
String csaIdentificador = (String) request.getAttribute("csaIdentificador");
String tipo = (String) request.getAttribute("tipo");

String[] chkAde = (String[]) request.getAttribute("chkAde");
List<?> autdesList = (List<?>) request.getAttribute("autdesList"); 

String ser_codigo = (String) request.getAttribute("serCodigo");
String rse_codigo = (String) request.getAttribute("rseCodigo");
String org_codigo = (String) request.getAttribute("orgCodigo");

CustomTransferObject convenio = (CustomTransferObject) request.getAttribute("convenio");
CustomTransferObject autdes = (CustomTransferObject) request.getAttribute("autdes");

String ade_vlr = (String) request.getAttribute("adeVlr");
String ade_vlr_tac = (String) request.getAttribute("adeVlrTac");
String ade_vlr_iof = (String) request.getAttribute("adeVlrIof");
String ade_vlr_cat = (String) request.getAttribute("adeVlrCat");
String ade_vlr_iva = (String) request.getAttribute("adeVlrIva");
String adePeriodicidade = (String) request.getAttribute("adePeriodicidade");
String cft_codigo = (String) request.getAttribute("cftCodigo");
String dtj_codigo = (String) request.getAttribute("dtjCodigo");
String prz_vlr = (String) request.getAttribute("przVlr");
String vlr_liberado = (String) request.getAttribute("vlrLiberado");
boolean vlrLiberadoOk = (Boolean) request.getAttribute("vlrLiberadoOk");

Integer prazo = (Integer) request.getAttribute("prazo");
BigDecimal valor = (BigDecimal) request.getAttribute("valor");
BigDecimal liberado = (BigDecimal) request.getAttribute("liberado");

String svcDescricao = (String) convenio.getAttribute(Columns.SVC_DESCRICAO);
String csaNome = (String) convenio.getAttribute(Columns.CSA_NOME);

int carenciaMinCse = (int) request.getAttribute("carenciaMinCse");
int carenciaMaxCse = (int) request.getAttribute("carenciaMaxCse");
boolean exigeCodAutSolicitacao = (Boolean) request.getAttribute("exigeCodAutSolicitacao");
boolean exibirTabelaPrice = (Boolean) request.getAttribute("exibirTabelaPrice");
boolean campoCidadeObrigatorio = (Boolean) request.getAttribute("campoCidadeObrigatorio");

// Parâmetros de convênio
int carenciaMinima = (int) request.getAttribute("carenciaMinima");
int carenciaMaxima = (int) request.getAttribute("carenciaMaxima");

// Define os valores de carência mínimo e máximo
int carenciaMinPermitida = (int) request.getAttribute("carenciaMinPermitida");

boolean simulacaoMetodoMexicano = (Boolean) request.getAttribute("simulacaoMetodoMexicano");
boolean simulacaoMetodoBrasileiro = (Boolean) request.getAttribute("simulacaoMetodoBrasileiro");
boolean quinzenal = (Boolean) request.getAttribute("quinzenal");

// data inicial e final do contrato
Date adeAnoMesIni = (Date) request.getAttribute("adeAnoMesIni");
Date adeAnoMesFim = (Date) request.getAttribute("adeAnoMesFim");

String dataIni = (String) request.getAttribute("dataIni");
String dataFim = (String) request.getAttribute("dataFim");

boolean exigeTelefone = (Boolean) request.getAttribute("exigeTelefone");
boolean exigeMunicipioLotacao = (Boolean) request.getAttribute("exigeMunicipioLotacao");
Boolean serSenhaObrigatoria =  !TextHelper.isNull(request.getAttribute("serSenhaObrigatoria")) ? (Boolean) request.getAttribute("serSenhaObrigatoria") : false;

// Verifica se permite servidor escolher correspondentes
List<TransferObject> lstCorrespondentes = (List<TransferObject>) request.getAttribute("lstCorrespondentes");

// Tipo dado adicional
List<?> tdaList = (List<?>) request.getAttribute("tdaList");
%>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="title">
  <hl:message key="rotulo.renegociar.consignacao.comfirmacao.dados.titulo"/>
</c:set>
<c:set var="bodyContent">
<% if (!vlrLiberadoOk) { %>
    <div class="alert alert-warning mb-1" role="alert">
      <p class="mb-0">
		<% String renegociacao = ApplicationResourcesHelper.getMessage("rotulo.consignacao.origem.renegociacao", responsavel);%>
		<%=ApplicationResourcesHelper.getMessage("mensagem.alerta.alteracao.simulacao", responsavel, renegociacao.toLowerCase())%>
      </p>
    </div>
<% } %>	
<form method="POST" action="../v3/simularRenegociacao?acao=incluirReserva&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
  <div class="row">
    <div class="col-sm-6">
      <%-- Utiliza a tag library DetalheConsignacaoTag.java para exibir os dados da ADE --%>
      <% pageContext.setAttribute("autdes", autdesList); %>
      <hl:detalharADEv4 name="autdes" table="false" type="simular_renegociacao" />
      <%-- Fim dos dados da ADE --%> 
    </div>
    <div class="col-sm">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.consignacao.dados.novo.contrato"/></h2>
        </div>
        <div class="card-body">   
          <dl class="row data-list firefox-print-fix">
            <dt class="col-6"><hl:message key="rotulo.consignataria.singular"/></dt>
            <dd class="col-6"><%=TextHelper.forHtmlContent(csaNome)%></dd>
            <dt class="col-6"><hl:message key="rotulo.consignacao.data"/></dt>
            <dd class="col-6"><%=DateHelper.toDateString(DateHelper.getSystemDatetime())%></dd>
            <dt class="col-6"><hl:message key="rotulo.consignacao.valor.liberado"/> (<hl:message key="rotulo.moeda"/>)</dt>
            <dd class="col-6"><%=TextHelper.forHtmlContent(vlr_liberado)%></dd>
            <dt class="col-6"><hl:message key="rotulo.consignacao.valor.parcela.moeda"/></dt>
            <dd class="col-6"><%=TextHelper.forHtmlContent(ade_vlr)%></dd>
            <dt class="col-6"><hl:message key="rotulo.consignacao.prazo"/></dt>
            <dd class="col-6"><%=TextHelper.forHtmlContent(prz_vlr)%></dd>
            <% if (ShowFieldHelper.showField(FieldKeysConstants.SIMULACAO_RENEGOCIACAO_CARENCIA, responsavel)) {%>
                  <dt class="col-6"><hl:message key="rotulo.consignacao.carencia"/></dt>
                  <dd class="col-6"><%=String.valueOf(carenciaMinPermitida)%></dd>
            <% } %>
            <dt class="col-6"><hl:message key="rotulo.consignacao.data.inicial"/></dt>
            <dd class="col-6"><%=TextHelper.forHtmlContent(dataIni)%></dd>
            <dt class="col-6"><hl:message key="rotulo.consignacao.data.final"/></dt>
            <dd class="col-6"><%=TextHelper.forHtmlContent(dataFim.equals("") ? "Indeterminado" : dataFim)%></dd>
            <dt class="col-6"><hl:message key="rotulo.servico.singular"/></dt>
            <dd class="col-6"><%=TextHelper.forHtmlContent(svcDescricao)%></dd>
            <% if (simulacaoMetodoMexicano) { %>
            <dt class="col-6"><hl:message key="rotulo.consignacao.periodicidade"/></dt>
            <dd class="col-6"><hl:message key="<%=quinzenal ? "rotulo.consignacao.periodicidade.quinzenal" : "rotulo.consignacao.periodicidade.mensal" %>"/></dd>
            <% } %>
            <% if (lstCorrespondentes != null && lstCorrespondentes.size() > 0) { %>
          </dl>
          <div class="form-group">
            <label for="COR_CODIGO"><hl:message key="rotulo.correspondente.singular"/>&nbsp;<hl:message key="rotulo.campo.opcional"/></label>
            <%=JspHelper.geraCombo(lstCorrespondentes, "COR_CODIGO", Columns.COR_CODIGO, Columns.COR_NOME + ";" + Columns.COR_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), "class=\"form-control\"")%>
          </div>
          <% } %>
        </div>      
      </div>
      <hl:confirmarDadosSERv4 serCodigo="<%=TextHelper.forHtmlAttribute(ser_codigo)%>" rseCodigo="<%=TextHelper.forHtmlAttribute(rse_codigo)%>" csaCodigo="<%=TextHelper.forHtmlAttribute(csa_codigo)%>"/>

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
                                svcCodigo="<%=TextHelper.forHtmlAttribute(svc_codigo)%>"
                                senhaParaAutorizacaoReserva="true"
                                nomeCampoSenhaCriptografada="serAutorizacao"
                                rseCodigo="<%=TextHelper.forHtmlAttribute(rse_codigo)%>"
                                nf="btnEnvia"
                                classe="form-control"/>
            </div>
          </div>
        </div>
      <% } %>
    </div>
  </div>
  
        <hl:htmlinput name="flow" type="hidden" value="endpoint"/>
        <hl:htmlinput name="tipo"   type="hidden" value="simular_renegociacao"/>

        <% for (int i = 0; i < chkAde.length; i++) {  %>
           <input type="hidden" name="chkADE" value="<%=TextHelper.forHtmlAttribute(chkAde[i])%>">
        <% } %>

        <hl:htmlinput name="SER_CODIGO"   type="hidden" value="<%=TextHelper.forHtmlAttribute(ser_codigo)%>"/>
        <hl:htmlinput name="RSE_CODIGO"   type="hidden" value="<%=TextHelper.forHtmlAttribute(rse_codigo)%>"/>
        <hl:htmlinput name="CSA_CODIGO"   type="hidden" value="<%=TextHelper.forHtmlAttribute(csa_codigo)%>"/>
        <hl:htmlinput name="CSA_NOME"     type="hidden" value="<%=TextHelper.forHtmlAttribute(csaNome)%>"/>
        <hl:htmlinput name="SVC_CODIGO"   type="hidden" value="<%=TextHelper.forHtmlAttribute(svc_codigo)%>"/>
        <hl:htmlinput name="SVC_IDENTIFICADOR"   type="hidden" value="<%=TextHelper.forHtmlAttribute(svcIdentificador)%>"/>
        <hl:htmlinput name="CSA_IDENTIFICADOR"   type="hidden" value="<%=TextHelper.forHtmlAttribute(csaIdentificador)%>"/>
        <hl:htmlinput name="CNV_CODIGO"   type="hidden" value="<%=TextHelper.forHtmlAttribute(convenio.getAttribute(Columns.CNV_CODIGO))%>" />
        <hl:htmlinput name="CFT_CODIGO"   type="hidden" value="<%=TextHelper.forHtmlAttribute(cft_codigo)%>"/>
        <hl:htmlinput name="DTJ_CODIGO"   type="hidden" value="<%=TextHelper.forHtmlAttribute(dtj_codigo)%>"/>
        <hl:htmlinput name="adeVlr"       type="hidden" value="<%=TextHelper.forHtmlAttribute(ade_vlr)%>"/>
        <hl:htmlinput name="adeCarencia"  type="hidden" value="<%=TextHelper.forHtmlAttribute(String.valueOf(carenciaMinPermitida))%>"/>
        <% if(simulacaoMetodoMexicano) { %>
          <hl:htmlinput name="adeVlrCat"  type="hidden" value="<%=TextHelper.forHtmlAttribute(ade_vlr_cat)%>"/>
          <hl:htmlinput name="adeVlrIva"  type="hidden" value="<%=TextHelper.forHtmlAttribute(ade_vlr_iva)%>"/>
        <% } else if (simulacaoMetodoBrasileiro) { %>
          <hl:htmlinput name="adeVlrTac"  type="hidden" value="<%=TextHelper.forHtmlAttribute(ade_vlr_tac)%>"/>
          <hl:htmlinput name="adeVlrIof"  type="hidden" value="<%=TextHelper.forHtmlAttribute(ade_vlr_iof)%>"/>
        <% } %>
        <hl:htmlinput name="vlrLiberado"  type="hidden" value="<%=TextHelper.forHtmlAttribute(vlr_liberado)%>"/>
        <hl:htmlinput name="adePrazo"     type="hidden" value="<%=TextHelper.forHtmlAttribute(prz_vlr)%>"/>
        <hl:htmlinput name="titulo"       type="hidden" value="<%=TextHelper.forHtmlAttribute(svcDescricao)%>"/>
        <hl:htmlinput name="ranking"      type="hidden" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, \"RANKING\"))%>"/>
        <hl:htmlinput name="CFT_DIA"      type="hidden" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, \"CFT_DIA\"))%>"/>
        <hl:htmlinput name="adePeriodicidade" type="hidden" value="<%=TextHelper.forHtmlAttribute(adePeriodicidade)%>" />
        <hl:htmlinput name="setAdeIdentificador" type="hidden" value="<%=ApplicationResourcesHelper.getMessage("rotulo.consignacao.identificador.solicitacao", responsavel)%>" />
        <hl:htmlinput name="MM_update"    type="hidden" value="form1"/>
          
        <div class="btn-action">
          <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.voltar"/></a>
          <a class="btn btn-primary" id="btnConfirmar" href="#" onClick="if (vf_valida_dados() && verificaEmail() && vf_confirma_ser()) {habilitaCampos(); f0.submit();} return false;"><svg width="17"><use xlink:href="#i-confirmar"></use></svg> <hl:message key="rotulo.botao.confirmar"/></a>
        </div>
        
<%-- Utiliza a tag library TabelaPriceTag.java para exibir o cálculo da tabela price --%>
<% if (exibirTabelaPrice && autdes != null) { %>
<hl:tabelaPriceV4 name="autdes" scope="request"/>
<% } %>
<%-- Fim dos dados da ADE --%>
</form>
</c:set>
<c:set var="javascript">
<% if (responsavel.isSer() && ParamSist.getBoolParamSist(CodedValues.TPC_SOLICITA_SENHA_AUTORIZACAO_OPE_REALIZADAS_SER, responsavel)) { %>
<hl:senhaServidorv4 senhaObrigatoria="<%=(String)(serSenhaObrigatoria ? "true" : "false")%>"                                                 
                  svcCodigo="<%=TextHelper.forHtmlAttribute(svc_codigo)%>"
                  senhaParaAutorizacaoReserva="true"
                  nomeCampoSenhaCriptografada="serAutorizacao"
                  rseCodigo="<%=TextHelper.forHtmlAttribute(rse_codigo)%>"
                  nf="btnEnvia"
                  classe="form-control"
                  scriptOnly="true"/>
<% } %>
<script type="text/JavaScript">
f0 = document.forms[0];
</script>
<script type="text/JavaScript">
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

  if (<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_LOGRADOURO, responsavel)%> && f0.SER_END != null && trim(f0.SER_END.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.logradouro"/>');
    f0.SER_END.focus();
    return false; 
    }

  if (<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NRO, responsavel)%> && f0.SER_NRO != null && trim(f0.SER_NRO.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.numero"/>');
    f0.SER_NRO.focus();
    return false; 
    }

  if (<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_COMPLEMENTO, responsavel)%> && f0.SER_COMPL != null && trim(f0.SER_COMPL.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.complemento"/>');
    f0.SER_COMPL.focus();
    return false; 
    }

  if (<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_BAIRRO, responsavel)%> && f0.SER_BAIRRO != null && trim(f0.SER_BAIRRO.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.bairro"/>');
    f0.SER_BAIRRO.focus();
    return false; 
    }

  if (<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CIDADE, responsavel)%> && f0.SER_CIDADE != null && trim(f0.SER_CIDADE.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.cidade"/>');
    f0.SER_CIDADE.focus();
    return false; 
    }

  if (<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CEP, responsavel)%> && f0.SER_CEP != null && trim(f0.SER_CEP.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.cep"/>');
    f0.SER_CEP.focus();
    return false; 
    }

  if (<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_UF, responsavel)%> && f0.SER_UF != null && trim(f0.SER_UF.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.estado"/>');
    f0.SER_UF.focus();
    return false; 
    }
  
    if (<%=(boolean)(exigeTelefone)%> && f0.TDA_25 != null && trim(f0.TDA_25.value) == '') {
        alert('<hl:message key="mensagem.informe.servidor.telefone.solicitacao"/>');
        f0.TDA_25.focus();
        return false;
    }
    
    if (<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_TELEFONE, responsavel)%> && f0.SER_TEL != null && trim(f0.SER_TEL.value) == '') {
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

    if (<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CELULAR, responsavel)%> && f0.SER_CEL != null && trim(f0.SER_CEL.value) == '') {
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

    if (f0.senha != null && trim(f0.senha.value) != '') {
   	    CriptografaSenha(f0.senha, f0.serAutorizacao, false);
    }

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

function vf_confirma_ser() {
    return (confirm('<hl:message key="mensagem.confirmacao.solicitacao.renegociacao.consignataria" arg0="<%=TextHelper.forHtmlAttribute(csaNome)%>"/> ' + '<%if (responsavel.isSer()) {%><%=ApplicationResourcesHelper.getMessage("mensagem.alerta.envio.solicitacao", responsavel) %> <%} %>'));
}

function habilitaCampos() {
}
</script>
</c:set>

<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
