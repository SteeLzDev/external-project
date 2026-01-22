<%--
* <p>Title: cadastrarDispensaValidacaoDigitalServidor_v4</p>
* <p>Description: Cadastrar dispensa de validacao de digital de servidor layout v4</p>
* <p>Copyright: Copyright (c) 2005</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.upload.UploadHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
ServidorTransferObject servidor = (ServidorTransferObject) request.getAttribute("servidor");
List<TransferObject> arquivos = (List<TransferObject>) request.getAttribute("arquivos");
%>

<c:set var="javascript">
<script type="text/JavaScript" src="../js/javacrypt.js?<hl:message key="release.tag"/>"></script>

<hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="" operacaoRegistroServidor="true" tmoSempreObrigatorio="true" inputSizeCSS="col-sm-12" scriptOnly="true"/>
<hl:fileUploadV4 tipoArquivo="anexar_dispensa_digital" multiplo="true" mostraCampoDescricao="false" extensoes="<%=UploadHelper.EXTENSOES_PERMITIDAS_DISPENSA_DIGITAL%>" divClassArquivo="form-group col-sm-12 mt-2" scriptOnly="true" />

<script type="text/JavaScript">
  function formLoad() {
    f0 = document.forms[0];
  }

  function verificaCampos() {

    var controles = new Array(
                "<%=Columns.ENS_LOGRADOURO%>",
                "<%=Columns.ENS_NUMERO%>",
                "<%=Columns.ENS_BAIRRO%>",
                "<%=Columns.ENS_UF%>",
                "<%=Columns.ENS_MUNICIPIO%>",
                "<%=Columns.ENS_CEP%>"
                );
    var msgs = new Array(
        "<hl:message key='mensagem.servidor.endereco.logradouro.informar'/>",
        "<hl:message key='mensagem.servidor.endereco.numero.informar'/>",
        "<hl:message key='mensagem.servidor.endereco.bairro.informar'/>",
        "<hl:message key='mensagem.servidor.endereco.estado.informar'/>",
        "<hl:message key='mensagem.servidor.endereco.cidade.informar'/>",
        "<hl:message key='mensagem.servidor.endereco.cep.informar'/>"
        );

    //if (ValidaCampos(controles, msgs)) {
  	if (confirmaAcaoConsignacao()) {
       document.getElementById("form1").submit();
    }
  }

  $(document).ready(function() {
  });

  function fazDownload(codigo) {
	  postData('../v3/cadastrarDispensaValidacaoDigitalServidor?acao=download&arqCodigo=' + codigo + '&serCodigo=<%=servidor.getSerCodigo()%>&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>', 'download');
  }

  function doIt(opt, codigo, arq) {
      var msg = '', j;
      if (opt == 'e') {
      msg = '<hl:message key="mensagem.confirmacao.exclusao.arquivo"/>'.replace("{0}", arq);
        j = '../v3/cadastrarDispensaValidacaoDigitalServidor?acao=excluir&arqCodigo=' + codigo + '&serCodigo=<%=servidor.getSerCodigo()%>&_skip_history_=true';
      } else {
        return false;
      }
  
      j = j + '&<%=SynchronizerToken.generateToken4URL(request)%>'
      if (msg != '') {
        if (confirm(msg)) {
          if (opt == 'i' || opt == 'v') {
          postData(j);
          } else {
            postData(j);
          }
        } else {
          return false;
        }
      } else {
        postData(j);
      }
      return true;
  }

</script>
</c:set>
<c:set var="title">
  <hl:message key="rotulo.servidor.cadastrar.dispensa.validacao.digital.titulo" />
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
<form method="POST" action="../v3/cadastrarDispensaValidacaoDigitalServidor?acao=salvar&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1" id="form1">
      <INPUT TYPE="hidden" NAME="SER_CODIGO" VALUE="<%=servidor.getSerCodigo()%>" />
      <div class="row">
        <div class="col-sm-12 col-md-6">
          <div class="card">
            <div class="card-header">
              <h2 class="card-header-title"><hl:message key="rotulo.servidor.singular"/></h2>
            </div>
            <div class="card-body">
              <%-- Utiliza a tag library DetalheServidorTag.java para exibir os dados do servidor --%>
              <hl:detalharServidorv4 name="servidor" complementos="true" scope="request" />
              <%-- Fim dos dados da ADE --%>
            </div>
          </div>
        </div>
        <div class="col-sm-12 col-md-6">
          <div class="card">
            <div class="card-header">
              <h2 class="card-header-title"><hl:message key="rotulo.servidor.cadastrar.dispensa.validacao.digital"/></h2>
            </div>
            <div class="card-body">
              <div class="row">
	              <div class="col-sm-12" role="radiogroup" aria-labelledby="dispensaDigital">
	                  <div class="form-group my-0">
	                    <span id="dispensaDigital"><hl:message key="rotulo.servidor.dispensa.digital" /></span>
	                  </div>
	                  <div class="form-check form-check-inline mt-2">
	                    <hl:htmlinput name="dispensaDigital"
	                                   di="dispensaDigital_S"
	                                   type="radio"
	                                   value="<%=CodedValues.TPC_SIM%>"
	                                   checked="<%=String.valueOf(servidor.getSerDispensaDigital() != null && servidor.getSerDispensaDigital().equalsIgnoreCase(\"S\"))%>"
	                                   mask="#*10"
	                                   classe="form-check-input ml-1"
	                     />
	                    <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label="<%=ApplicationResourcesHelper.getMessage("rotulo.sim", responsavel)%>" for="dispensaDigital_S"><hl:message key="rotulo.sim" /></label>
                      </div>
                    <div class="form-check-inline form-check">
	                    <hl:htmlinput name="dispensaDigital"
	                                   di="dispensaDigital_N"
	                                   type="radio"
	                                   value="<%=CodedValues.TPC_NAO%>"
	                                   checked="<%=String.valueOf(TextHelper.isNull(servidor.getSerDispensaDigital()) || (servidor.getSerDispensaDigital() != null && servidor.getSerDispensaDigital().equalsIgnoreCase(CodedValues.TPC_NAO)))%>"
	                                   mask="#*10"
	                                   classe="form-check-input ml-1"
	                     />
	                    <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label="<%=ApplicationResourcesHelper.getMessage("rotulo.nao", responsavel)%>" for="dispensaDigital_N"><hl:message key="rotulo.nao" /></label>
	                  </div>
	              </div>
              </div>
              <div class="form-group col-sm-12">
                  <hl:fileUploadV4 tipoArquivo="anexar_dispensa_digital" multiplo="true" mostraCampoDescricao="false" extensoes="<%=UploadHelper.EXTENSOES_PERMITIDAS_DISPENSA_DIGITAL%>" divClassArquivo="form-group col-sm-12 mt-2" />
              </div>
              <div class="form-group col-sm-12">
                <div class="legend"></div>
                <%-- Utiliza a tag library EfetivaAcaoMotivoOperacaoTag.java para exibir os dados do Tipo de Motivo da Operação --%>
                <hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="" operacaoDispensaValidacaoDigital="true" tmoSempreObrigatorio="true" inputSizeCSS="col-sm-12"/>
                <%-- Fim dos dados do Motivo da Operação --%>
              </div>
            </div>
          </div>
          <div class="pull-right">
            <div class="btn-action">
              <% if (arquivos == null || arquivos.isEmpty()) { %>
              <a onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;" class="btn btn-outline-danger" href="#"><hl:message key="rotulo.botao.cancelar"/></a>
              <% } %>
              <a href="#" onClick="verificaCampos(); return false;" class="btn btn-primary"><hl:message key="rotulo.botao.salvar"/></a>         
            </div>
          </div>
        </div>
      </div>

      <% if (arquivos != null && !arquivos.isEmpty()) { %>
      <div class="row">
        <div class="col-sm-12 col-md-12 mb-2">
          <div class="card">
            <div class="card-header hasIcon pl-3">
              <h2 class="card-header-title"><hl:message key="rotulo.servidor.cadastrar.dispensa.validacao.digital.arquivo.disponivel"/></h2>
            </div>
            <div class="card-body table-responsive p-0">
              <table class="table table-striped table-hover table-responsive">
                <thead>
                  <tr>
                    <th id="dataArquivo"><hl:message key="rotulo.servidor.cadastrar.dispensa.validacao.digital.data" /></th>
                    <th id="responsavelArquivo"><hl:message key="rotulo.responsavel.singular" /></th>
                    <th id="descricaoArquivo"><hl:message key="rotulo.servidor.cadastrar.dispensa.validacao.digital.nome" /></th>
                    <th id="arquivo"><hl:message key="rotulo.acoes" /></th>
                  </tr>
                </thead>
                <tbody>
                  <%
                    String arqCodigo, usuLogin, aseNome, aseDataCriacao;
                    for (TransferObject arquivo : arquivos) {
                        arqCodigo = arquivo.getAttribute(Columns.ARQ_CODIGO).toString();
                        usuLogin = arquivo.getAttribute(Columns.USU_LOGIN).toString();
                        aseNome = arquivo.getAttribute(Columns.ASE_NOME).toString();
                        aseDataCriacao = DateHelper.toDateTimeString((Date) arquivo.getAttribute(Columns.ASE_DATA_CRIACAO));
                  %>
                  <tr>
                    <td header="dataArquivo"><%=TextHelper.forHtmlContent(aseDataCriacao)%></td>
                    <td header="responsavelArquivo"><%=TextHelper.forHtmlContent(usuLogin)%></td>
                    <td header="descricaoArquivo"><%=TextHelper.forHtmlContent(aseNome)%></td>
                    <td class="text-nowrap" header="arquivo" id="nomeArquivo">
                      <div class="actions">
                        <div class="dropdown">
                          <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                            <div class="form-inline">
                              <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<%=ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)%>" aria-label="<%=ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)%>"> <svg>
                                  <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                              </span> <hl:message key="rotulo.botao.opcoes"/>
                            </div>
                          </a>
                          <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                            <div class="position-relative">
  	                          <a href="#no-back" class="dropdown-item" onClick="fazDownload('<%=TextHelper.forJavaScript(arqCodigo)%>'); return false;"><hl:message key="rotulo.acoes.download"/>&nbsp;</a>
                              <a href="#no-back" class="dropdown-item" onClick="doIt('e', '<%=TextHelper.forJavaScript(arqCodigo)%>', '<%=TextHelper.forJavaScript(aseNome)%>'); return false;"><hl:message key="rotulo.acoes.excluir"/>&nbsp;</a>
                            </div>
                          </div>
                        </div>
                      </div>
                    </td>
                  </tr>
                  <% 
                    }
                  %>
                </tbody>
                <tfoot>
                  <tr><td colspan="4"><hl:message key="mensagem.servidor.cadastrar.dispensa.validacao.digital.lista.arquivos" /></td></tr>
                </tfoot>
              </table>
            </div>
          </div>
          <div class="btn-action">
            <a onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;" class="btn btn-outline-danger" href="#"><hl:message key="rotulo.botao.cancelar"/></a>
          </div>
        </div>
      </div>
      <% } %>
        
      </form>
  
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>