<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.upload.UploadHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.web.*"%>
<%@ page import="com.zetra.econsig.values.TipoArquivoEnum"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
AcessoSistema responsavel = (AcessoSistema) request.getAttribute("responsavel");
String csaCodigo = (String) request.getAttribute("csaCodigo");
List<?> correspondentes = (List<?>) request.getAttribute("correspondentes");
boolean selecionaEstOrgUploadMargemRetorno = (boolean) request.getAttribute("selecionaEstOrgUploadMargemRetorno");
boolean selecionaEstOrgUploadContracheque = (boolean) request.getAttribute("selecionaEstOrgUploadContracheque");
List<ArquivoDownload> arquivosCombo = (List<ArquivoDownload>) request.getAttribute("arquivosCombo");
List<?> codigosOrgao = (List<?>) request.getAttribute("codigosOrgao");
boolean temProcessoRodando = (boolean) request.getAttribute("temProcessoRodando");
String msgResultadoComando = (String) request.getAttribute("msgResultadoComando");
String tipo = (String) request.getAttribute("tipo");
String papCodigo = (String) request.getAttribute("papCodigo");
String estCodigo = (String) request.getAttribute("estCodigo");
String orgCodigo = (String) request.getAttribute("orgCodigo");
boolean temPermissaoEst = (boolean) request.getAttribute("temPermissaoEst");
List<?> consignatarias = (List<?>) request.getAttribute("consignatarias");
boolean exibeCaptcha = (boolean) request.getAttribute("exibeCaptcha");
boolean exibeCaptchaAvancado = (boolean) request.getAttribute("exibeCaptchaAvancado");
boolean exibeCaptchaDeficiente = (boolean) request.getAttribute("exibeCaptchaDeficiente");
boolean comentario = (boolean) request.getAttribute("comentario");
boolean exibirArquivo = (boolean) request.getAttribute("exibirArquivo");
String pathDownload = (String) request.getAttribute("pathDownload");
HashMap<?,?> orgaos = (HashMap<?,?>) request.getAttribute("orgaos");
boolean exibeCampoUpload = (boolean) request.getAttribute("exibeCampoUpload");
List<?> lstArquivoEnviados = (List<?>) request.getAttribute("lstArquivoEnviados");

List<?> lstEstabelecimentos = (List<?>) request.getAttribute("lstEstabelecimentos");
List<?> lstOrgaos = (List<?>) request.getAttribute("lstOrgaos");

String action = (String) request.getAttribute("action");

%>

<c:set var="title">
  <hl:message key="rotulo.upload.arquivo.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-upload"></use>
</c:set>
<c:set var="bodyContent">
  <% if (!temProcessoRodando) { %>
     <%if (!TextHelper.isNull(msgResultadoComando)) { %>
        <div class="alert alert-warning" role="alert">
          <i class="fa fa-exclamation-triangle fa-2x fa-stack" aria-hidden="true"></i>
          <%=msgResultadoComando%>
        </div>
     <%} %>
  <div class="card">
    <div class="card-header hasIcon">
      <span class="card-header-icon"><svg width="26"><use xlink:href="#i-upload"></use></svg></span>
      <h2 class="card-header-title"><hl:message key="rotulo.upload.arquivo.titulo"/></h2>
    </div>
    <div class="card-body">
      <form name="form1" method="POST" action="${action}" enctype="multipart/form-data">
        <div class="row">
          <div class="form-group col-sm-6">
            <label for="tipo"><hl:message key="rotulo.upload.arquivo.tipo"/></label>
            <select class="form-control form-select" id="tipo" name="tipo" onChange="desabilitaCsaCodigo(); alterarTipoArquivo();">
              <option value=""><hl:message key="rotulo.campo.selecione"/></option>

            <% if (responsavel.isCseSupOrg()) {%>
              <% if (UploadHelper.temPermissaoUpload(TipoArquivoEnum.ARQUIVO_CADASTRO_MARGENS, responsavel)) { %>
                <option value="margem" <%=(String)(tipo != null && tipo.equals("margem") ? "SELECTED" : "")%>><hl:message key="rotulo.upload.arquivo.selecione.margens.servidores"/></option>
              <% } %>
              <% if (UploadHelper.temPermissaoUpload(TipoArquivoEnum.ARQUIVO_CADASTRO_MARGEM_COMPLEMENTAR, responsavel)) { %>
                <option value="margemcomplementar" <%=(String)(tipo != null && tipo.equals("margemcomplementar") ? "SELECTED" : "")%>><hl:message key="rotulo.upload.arquivo.selecione.margem.complementar"/></option>
              <% } %>
              <% if (UploadHelper.temPermissaoUpload(TipoArquivoEnum.ARQUIVO_RETORNO_INTEGRACAO, responsavel)) { %>
                <option value="retorno" <%=(String)(tipo != null && tipo.equals("retorno") ? "SELECTED" : "")%>><hl:message key="rotulo.upload.arquivo.selecione.retorno.integracao"/></option>
              <% } %>
              <% if (UploadHelper.temPermissaoUpload(TipoArquivoEnum.ARQUIVO_RETORNO_ATRASADO, responsavel)) { %>
                <option value="retornoatrasado" <%=(String)(tipo != null && tipo.equals("retornoatrasado") ? "SELECTED" : "")%>><hl:message key="rotulo.upload.arquivo.selecione.retorno.atrasado"/></option>
              <% } %>
              <% if (UploadHelper.temPermissaoUpload(TipoArquivoEnum.ARQUIVO_CRITICA, responsavel)) { %>
                <option value="critica" <%=(String)(tipo != null && tipo.equals("critica") ? "SELECTED" : "")%>><hl:message key="rotulo.upload.arquivo.selecione.critica.integracao"/></option>
              <% } %>
              <% if (UploadHelper.temPermissaoUpload(TipoArquivoEnum.ARQUIVO_TRANSFERIDOS, responsavel)) { %>
                <option value="transferidos" <%=(String)(tipo != null && tipo.equals("transferidos") ? "SELECTED" : "")%>><hl:message key="rotulo.upload.arquivo.selecione.transferidos"/></option>
              <% } %>
              <% if (UploadHelper.temPermissaoUpload(TipoArquivoEnum.ARQUIVO_CONTRACHEQUES, responsavel)) { %>
                <option value="contracheque" <%=(String)(tipo != null && tipo.equals("contracheque") ? "SELECTED" : "")%>><hl:message key="rotulo.upload.arquivo.selecione.contracheque"/> </option>
              <% } %>
              <% if (UploadHelper.temPermissaoUpload(TipoArquivoEnum.ARQUIVO_DIRF_SERVIDOR, responsavel)) { %>
                <option value="dirf" <%=(String)(tipo != null && tipo.equals("dirf") ? "SELECTED" : "")%>><hl:message key="rotulo.upload.arquivo.selecione.dirf.servidor"/> </option>
              <% } %>
              <% if (UploadHelper.temPermissaoUpload(TipoArquivoEnum.ARQUIVO_HISTORICO, responsavel)) { %>
                <option value="historico" <%=(String)(tipo != null && tipo.equals("historico") ? "SELECTED" : "")%>><hl:message key="rotulo.upload.arquivo.selecione.historico"/></option>
              <% } %>
            <%}%>

              <% if (responsavel.isCseSup() || responsavel.isCsa()) {%>
              <% if (UploadHelper.temPermissaoUpload(TipoArquivoEnum.ARQUIVO_BLOQUEIO_SERVIDOR, responsavel)) { %>
              <option value="bloqueio_ser" <%=(String) (tipo != null && tipo.equals("bloqueio_ser") ? "SELECTED" : "")%>>
                <hl:message key="rotulo.upload.arquivo.selecione.bloqueio.servidor"/></option>
              <% } } %>
              <%if (responsavel.isCseSup()) {%>
              <% if (UploadHelper.temPermissaoUpload(TipoArquivoEnum.ARQUIVO_FALECIDO, responsavel)) { %>
                <option value="falecido" <%=(String)(tipo != null && tipo.equals("falecido") ? "SELECTED" : "")%>><hl:message key="rotulo.upload.arquivo.selecione.falecido"/></option>
              <% } %>
              <% if (UploadHelper.temPermissaoUpload(TipoArquivoEnum.ARQUIVO_DESLIGADO_BLOQUEADO, responsavel)) { %>
                <option value="desligado" <%=(String)(tipo != null && tipo.equals("desligado") ? "SELECTED" : "")%>><hl:message key="rotulo.upload.arquivo.selecione.desligado.bloqueado"/></option>
              <% } %>
            <%}%>

            <%if (responsavel.isSup()) {%>
              <% if (UploadHelper.temPermissaoUpload(TipoArquivoEnum.ARQUIVO_ADEQUACAO_A_MARGEM, responsavel)) { %>
                <option value="adequacao" <%=(String)(tipo != null && tipo.equals("adequacao") ? "SELECTED" : "")%>><hl:message key="rotulo.upload.arquivo.selecione.adequacao.margem"/></option>
              <% } %>
              <% if (UploadHelper.temPermissaoUpload(TipoArquivoEnum.ARQUIVO_CANCELAMENTO_BENEFICIOS_POR_INADIMPLENCIA, responsavel)) { %>
                <option value="cancelamentoporinadimplencia" <%=(String)(tipo != null && tipo.equals("cancelamentoporinadimplencia") ? "SELECTED" : "")%>><hl:message key="rotulo.upload.arquivo.cancelamento.beneficios.por.inadiplencia"/></option>
              <% } %>
            <%}%>

            <%if (responsavel.isCseSupOrg() || responsavel.isCsaCor()) {%>
              <% if (UploadHelper.temPermissaoUpload(TipoArquivoEnum.ARQUIVO_LOTE, responsavel)) { %>
                <option value="lote" <%=(String)(tipo != null && tipo.equals("lote") ? "SELECTED" : "")%>><hl:message key="rotulo.upload.arquivo.selecione.arquivos.lote"/></option>
              <% } %>
            <%}%>

            <%if (responsavel.isCseSup() && responsavel.temPermissao(CodedValues.FUN_PROCESSA_LOTE_MULTIPLAS_CSAS)) {%>
              <% if (UploadHelper.temPermissaoUpload(TipoArquivoEnum.ARQUIVO_LOTE, responsavel)) { %>
                <option value="lotemultiplo" <%=(String)(tipo != null && tipo.equals("lotemultiplo") ? "SELECTED" : "")%>><hl:message key="rotulo.upload.arquivo.selecione.arquivos.lote.multiplas.csa"/></option>
              <% } %>
            <%}%>

            <%if (responsavel.isCseSupOrg()) {%>
              <% if (UploadHelper.temPermissaoUpload(TipoArquivoEnum.ARQUIVO_LOTE_RESCISAO, responsavel)) { %>
                <option value="loterescisao" <%=(String)(tipo != null && tipo.equals("loterescisao") ? "SELECTED" : "")%>><hl:message key="rotulo.upload.arquivo.selecione.arquivos.lote.rescisao"/></option>
              <% } %>
            <%}%>

            <%if (responsavel.isCseSup() || responsavel.isCsa()) {%>
              <% if (UploadHelper.temPermissaoUpload(TipoArquivoEnum.ARQUIVO_CONCILIACAO, responsavel)) { %>
                <option value="conciliacao" <%=(String)(tipo != null && tipo.equals("conciliacao") ? "SELECTED" : "")%>><hl:message key="rotulo.upload.arquivo.selecione.arquivos.conciliacao"/></option>
              <% } %>
            <%}%>

            <%if (responsavel.isCseSup() && responsavel.temPermissao(CodedValues.FUN_IMP_ARQ_CONCILIACAO_MULTIPLAS_CSAS)) {%>
              <% if (UploadHelper.temPermissaoUpload(TipoArquivoEnum.ARQUIVO_CONCILIACAO, responsavel)) { %>
                <option value="conciliacaomultiplo" <%=(String)(tipo != null && tipo.equals("conciliacaomultiplo") ? "SELECTED" : "")%>><hl:message key="rotulo.upload.arquivo.selecione.arquivos.conciliacao.multiplas.csas"/></option>
              <% } %>
            <%}%>

            <%if (responsavel.isCseSup()) {%>
              <% if (UploadHelper.temPermissaoUpload(TipoArquivoEnum.ARQUIVO_CONSIGNATARIAS, responsavel)) { %>
                <option value="consignatarias" <%=(String)(tipo != null && tipo.equals("consignatarias") ? "SELECTED" : "")%>><hl:message key="rotulo.upload.arquivo.selecione.arquivos.consignatarias"/></option>
              <% } %>
              <% if (UploadHelper.temPermissaoUpload(TipoArquivoEnum.ARQUIVO_CONVENIO, responsavel)) { %>
                <option value="convenios" <%=(String)(tipo != null && tipo.equals("convenios") ? "SELECTED" : "")%>><hl:message key="rotulo.upload.arquivo.selecione.arquivos.convenios"/></option>
              <% } %>
            <%}%>

            <%if (responsavel.isSup() && responsavel.temPermissao(CodedValues.FUN_PROCESSA_LOTE_INCONSISTENCIA)) {%>
              <% if (UploadHelper.temPermissaoUpload(TipoArquivoEnum.ARQUIVO_REGRA_INCONSISTENCIA, responsavel)) { %>
                <option value="inconsistencia" <%=(String)(tipo != null && tipo.equals("inconsistencia") ? "SELECTED" : "")%>><hl:message key="rotulo.upload.arquivo.selecione.inconsistencia"/></option>
              <% } %>
            <%}%>

            <% if (responsavel.isSup() && UploadHelper.temPermissaoUpload(TipoArquivoEnum.ARQUIVO_PREVIA_FATURAMENTO_BENEFICIOS, responsavel)) { %>
                <option value="previafaturamentobeneficios" <%=(String)(tipo != null && tipo.equals("previafaturamentobeneficios") ? "SELECTED" : "")%>><hl:message key="rotulo.upload.arquivo.previa.faturamento.beneficios"/></option>
            <% } %>

            <% if ((responsavel.isSup() || responsavel.isCsa()) && responsavel.temPermissao(CodedValues.FUN_ENVIAR_ARQ_RECUPERACAO_CREDITO)) { %>
                <option value="recuperacaoCredito" <%=(String)(tipo != null && tipo.equals("recuperacaoCredito") ? "SELECTED" : "")%>><hl:message key="rotulo.upload.arquivo.recuperacao.credito"/></option>
            <% } %>

            <% if ((responsavel.isSup() || responsavel.isCsaCor()) && responsavel.temPermissao(CodedValues.FUN_ENVIAR_ARQ_SALDO_DEVEDOR_LOTE)) { %>
                <option value="saldodevedor" <%=(String)(tipo != null && tipo.equals("saldodevedor") ? "SELECTED" : "")%>><hl:message key="rotulo.upload.arquivo.saldo.devedor"/></option>
            <% } %>

            <% if (responsavel.isSup() && UploadHelper.temPermissaoUpload(TipoArquivoEnum.ARQUIVO_CARTEIRINHAS_A_SEREM_TOMBADAS, responsavel)) { %>
                <option value="carteirinhasTombadas" <%=(String)(tipo != null && tipo.equals("carteirinhasTombadas") ? "SELECTED" : "")%>><hl:message key="rotulo.upload.arquivo.carteirinhas.serem.tombadas"/></option>
            <% } %>
            <% if (responsavel.isCseSup() && UploadHelper.temPermissaoUpload(TipoArquivoEnum.ARQUIVO_CADASTRO_DEPENDENTE, responsavel)) { %>
                <option value="cadastroDependentes" <%=(String)(tipo != null && tipo.equals("cadastroDependentes") ? "SELECTED" : "")%>><hl:message key="rotulo.upload.arquivo.cadastro.dependente"/></option>
            <% } %>
            <% if (responsavel.isSup() && UploadHelper.temPermissaoUpload(TipoArquivoEnum.ARQUIVO_RELATORIO_CUSTOMIZADO, responsavel)) { %>
                <option value="relatorioCustomizado" <%=(String)(tipo != null && tipo.equals("relatorioCustomizado") ? "SELECTED" : "")%>><hl:message key="rotulo.upload.arquivo.relatorio.customizado"/></option>
            <% } %>
            <% if (responsavel.isCseSup() && UploadHelper.temPermissaoUpload(TipoArquivoEnum.ARQUIVO_XML_MARGEM_RETORNO_MOVIMENTO, responsavel)) { %>
                <option value="xmlMargemRetornoMovimento" <%=(String)(tipo != null && tipo.equals("xmlMargemRetornoMovimento") ? "SELECTED" : "")%>><hl:message key="rotulo.upload.arquivo.xml.margem.retorno.movimento"/></option>
            <% } %>
            
            <% if (responsavel.isCseSupOrg() && UploadHelper.temPermissaoUpload(TipoArquivoEnum.ARQUIVO_LOTE_CADASTRO_CONSIGNATARIA, responsavel)) { %>
                <option value="cadastroConsignatarias" <%=(String)(tipo != null && tipo.equals("cadastroConsignatarias") ? "SELECTED" : "")%>><hl:message key="rotulo.upload.arquivo.lote.cadastro.consignatarias"/></option>
            <% } %>

            </select>
          </div>

          <%if ((selecionaEstOrgUploadMargemRetorno || selecionaEstOrgUploadContracheque) && responsavel.isCseSup()) { %>
          <div class="col-sm-12 col-md-6">
            <span id="descricao"><hl:message key="rotulo.upload.arquivo.entidade"/></span>
            <div class="form-group">
              <div class="form-check form-check-inline mt-2">
                <input class="form-check-input mt-1 ml-1" type="radio" name="PAP_CODIGO" id="entidadeGeral" onChange="desabilitaCsaCodigo(); alterarTipoArquivo();" value="<%=(String)AcessoSistema.ENTIDADE_CSE%>" <% if (TextHelper.isNull(papCodigo) || papCodigo.equals(AcessoSistema.ENTIDADE_CSE)) {%>checked<% } %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                <label class="form-check-label labelSemNegrito pt-1 ml-1 pr-4" for="entidadeGeral"><hl:message key="rotulo.upload.arquivo.geral"/></label>
              </div>
              <div class="form-check form-check-inline mt-2">
                <input class="form-check-input mt-1 ml-1" type="radio" name="PAP_CODIGO" id="entidadeEstabelecimento" onChange="desabilitaCsaCodigo(); alterarTipoArquivo();" VALUE="<%=(String)AcessoSistema.ENTIDADE_EST%>" <% if (!TextHelper.isNull(papCodigo) && papCodigo.equals(AcessoSistema.ENTIDADE_EST)) {%>checked<% } %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                <label class="form-check-label labelSemNegrito ml-1 pr-4 pt-1" for="entidadeEstabelecimento"><hl:message key="rotulo.estabelecimento.singular"/></label>
              </div>
              <div class="form-check form-check-inline mt-2">
                <input class="form-check-input mt-1 ml-1" type="radio" name="PAP_CODIGO" id="entidadeOrgao" onChange="desabilitaCsaCodigo(); alterarTipoArquivo();" VALUE="<%=(String)AcessoSistema.ENTIDADE_ORG%>" <% if (!TextHelper.isNull(papCodigo) && papCodigo.equals(AcessoSistema.ENTIDADE_ORG)) {%>checked<% } %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                <label class="form-check-label labelSemNegrito ml-1 pr-4 pt-1" for="entidadeOrgao"><hl:message key="rotulo.orgao.singular"/></label>
              </div>
            </div>
          </div>
          <%} %>
          </div>


         <% if ((selecionaEstOrgUploadMargemRetorno || selecionaEstOrgUploadContracheque) && responsavel.isCseSup()) { %>
            <div class="row">
            <% if (!TextHelper.isNull(papCodigo) && papCodigo.equals(AcessoSistema.ENTIDADE_EST)) { %>
              <div class="form-group col-sm-6">
                <label for="EST_CODIGO"><hl:message key="rotulo.estabelecimento.singular"/></label>
                <select class="form-control form-select" id="EST_CODIGO" name="EST_CODIGO" onChange="vf_nome_arquivo(); alterarTipoArquivo();" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);">
                  <option value="" <%=(String)(TextHelper.isNull(estCodigo) ? "SELECTED" : "")%>><hl:message key="rotulo.campo.selecione"/></option>
                  <%
                    Iterator<?> iteEst = lstEstabelecimentos.iterator();
                    while (iteEst.hasNext()) {
                      TransferObject ctoEst = (TransferObject)iteEst.next();
                      String fieldValueEst = ctoEst.getAttribute(Columns.EST_CODIGO).toString();
                      String fieldLabelEst = ctoEst.getAttribute(Columns.EST_NOME) + " - " + ctoEst.getAttribute(Columns.EST_IDENTIFICADOR);
                      %>
                      <option value="<%=TextHelper.forHtmlAttribute(fieldValueEst)%>" <%=(String)((!TextHelper.isNull(estCodigo) && estCodigo.equalsIgnoreCase(fieldValueEst))  ? "SELECTED" : "")%>><%=TextHelper.forHtmlContent(fieldLabelEst)%></option>
                      <%
                     }
                   %>
                </select>
              </div>
             <% } else if (!TextHelper.isNull(papCodigo) && papCodigo.equals(AcessoSistema.ENTIDADE_ORG)) { %>
              <div class="form-group col-sm-6">
                <label for="ORG_CODIGO"><hl:message key="rotulo.orgao.singular"/></label>
                <select class="form-control form-select" id="ORG_CODIGO" name="ORG_CODIGO" onChange="vf_nome_arquivo(); alterarTipoArquivo();" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);">
                  <option value="" <%=(String)(TextHelper.isNull(orgCodigo) ? "SELECTED" : "")%>><hl:message key="rotulo.campo.selecione"/></option>
                  <%
                    Iterator<?> iteOrg = lstOrgaos.iterator();
                    while (iteOrg.hasNext()) {
                      TransferObject ctoOrg = (TransferObject)iteOrg.next();
                      String fieldValueOrg = ctoOrg.getAttribute(Columns.ORG_CODIGO).toString();
                      String fieldLabelOrg = ctoOrg.getAttribute(Columns.ORG_NOME) + " - " + ctoOrg.getAttribute(Columns.ORG_IDENTIFICADOR);
                      %>
                      <option value="<%=TextHelper.forHtmlAttribute(fieldValueOrg)%>" <%=(String)((!TextHelper.isNull(orgCodigo) && orgCodigo.equalsIgnoreCase(fieldValueOrg)) ? "SELECTED" : "")%>><%=TextHelper.forHtmlContent(fieldLabelOrg)%></option>
                      <%
                    }
                    %>
                </select>
              </div>
             <% } %>
            </div>
          <% } %>

          <% if (responsavel.isOrg() && temPermissaoEst) { %>
               <input name="EST_CODIGO" type="HIDDEN" value="<%=TextHelper.forHtmlAttribute(responsavel.getEstCodigo())%>">
          <% } else if (responsavel.isOrg()) { %>
               <input name="ORG_CODIGO" type="HIDDEN" value="<%=TextHelper.forHtmlAttribute(responsavel.getOrgCodigo())%>">
          <% } %>

          <%if (responsavel.isCseSupOrg() && !TextHelper.isNull(tipo) && (tipo.equals("lote") || tipo.equals("conciliacao") || tipo.equals("previafaturamentobeneficios") || tipo.equals("relatorioCustomizado"))) {%>
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="CSA_CODIGO_AUX"><hl:message key="rotulo.consignataria.singular"/></label>
                <select class="form-control form-select" id="CSA_CODIGO_AUX" name="CSA_CODIGO_AUX" ONCHANGE="montaComboCor();">
                  <%
                    Iterator<?> it = consignatarias.iterator();
                    CustomTransferObject csa = null;
                    String csa_nome = null;
                    String csa_codigo = "";
                    String csa_identificador = "";
                    while (it.hasNext()) {
                      csa = (CustomTransferObject)it.next();
                      csa_codigo = (String)csa.getAttribute(Columns.CSA_CODIGO);
                      csa_identificador = (String)csa.getAttribute(Columns.CSA_IDENTIFICADOR);
                      csa_nome = (String)csa.getAttribute(Columns.CSA_NOME_ABREV);
                      if (csa_nome == null || csa_nome.trim().length() == 0)
                        csa_nome = csa.getAttribute(Columns.CSA_NOME).toString();
                      %>
                      <OPTION VALUE="<%=TextHelper.forHtmlAttribute(csa_codigo)%>" <%=(String)(csaCodigo != null && csaCodigo.equals(csa_codigo) ? "SELECTED" : "")%>><%=TextHelper.forHtmlContent(csa_identificador)%> - <%=TextHelper.forHtmlContent(csa_nome)%></OPTION>
                      <%
                    }
                  %>
                </select>
              </div>
              <%if (responsavel.isCseSupOrg() && !TextHelper.isNull(tipo) && (tipo.equals("lote"))) {%>
                <div class="form-group col-sm-6">
                  <label for="COR_CODIGO_AUX"><hl:message key="rotulo.correspondente.singular"/></label>
                  <select class="form-control form-select" id="COR_CODIGO_AUX" name="COR_CODIGO_AUX">
                    <option value="-1"><hl:message key="rotulo.campo.selecione"/></option>
                    <%
                      Iterator<?> itCor = correspondentes.iterator();
                      CustomTransferObject cor = null;
                      while (itCor.hasNext()) {
                        cor = (CustomTransferObject)itCor.next();
                        String cor_codigo = (String)cor.getAttribute(Columns.COR_CODIGO);
                        String cor_identificador = (String)cor.getAttribute(Columns.COR_IDENTIFICADOR);
                        String cor_nome = (String)cor.getAttribute(Columns.COR_NOME);
                        %>
                        <OPTION VALUE="<%=TextHelper.forHtmlAttribute(cor_codigo)%>"><%=TextHelper.forHtmlContent(cor_identificador)%> - <%=TextHelper.forHtmlContent(cor_nome)%></OPTION>
                        <%
                      }
                    %>
                  </select>
                </div>
              <%} %>
            </div>
        <%}%>

        <%if (responsavel.isSup() && !TextHelper.isNull(tipo) && (tipo.equals("recuperacaoCredito") || tipo.equals("saldodevedor"))) {%>
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="CSA_CODIGO_AUX"><hl:message key="rotulo.consignataria.singular"/></label>
                <select class="form-control form-select" id="CSA_CODIGO_AUX" name="CSA_CODIGO_AUX">
                  <%
                    Iterator<?> it = consignatarias.iterator();
                    CustomTransferObject csa = null;
                    String csa_nome = null;
                    String csa_codigo = "";
                    String csa_identificador = "";
                    while (it.hasNext()) {
                      csa = (CustomTransferObject)it.next();
                      csa_codigo = (String)csa.getAttribute(Columns.CSA_CODIGO);
                      csa_identificador = (String)csa.getAttribute(Columns.CSA_IDENTIFICADOR);
                      csa_nome = (String)csa.getAttribute(Columns.CSA_NOME_ABREV);
                      if (csa_nome == null || csa_nome.trim().length() == 0)
                        csa_nome = csa.getAttribute(Columns.CSA_NOME).toString();
                      %>
                      <OPTION VALUE="<%=TextHelper.forHtmlAttribute(csa_codigo)%>" <%=(String)(csaCodigo != null && csaCodigo.equals(csa_codigo) ? "SELECTED" : "")%>><%=TextHelper.forHtmlContent(csa_identificador)%> - <%=TextHelper.forHtmlContent(csa_nome)%></OPTION>
                      <%
                    }
                  %>
                </select>
              </div>
            </div>
        <%}%>

        <%if (exibeCampoUpload) { %>
          <div class="row">
            <div class="form-group col-sm-12">
              <label for="arquivo"><hl:message key="rotulo.upload.arquivo.arquivo"/></label>
              <input type="file" class="form-control" id="arquivo" name="FILE1" onChange="vf_nome_arquivo();">
            </div>
          </div>
        <%} %>

        <%if(comentario){ %>
          <div class="row">
            <div class="form-group col-sm-12">
              <label for="obs"><hl:message key="rotulo.upload.arquivo.observacao"/></label>
              <hl:htmlinput name="obs" type="textarea" placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.obs",responsavel)%>' classe="form-control" di="obs" rows="6" others="onFocus=\"SetarEventoMascara(this,'#*65000',true);\" onBlur=\"fout(this);ValidaMascara(this);\""/>
            </div>
          </div>
        <%} %>

        <div class="row">
          <% if (exibeCaptcha || exibeCaptchaDeficiente) { %>
            <div class="form-group col-sm-5">
              <label for="captcha"><hl:message key="rotulo.captcha.codigo"/>:</label>
          	  <input type="text" class="form-control" id="captcha" name="captcha" placeholder='<hl:message key="mensagem.informacao.login.digite.codigo.acesso"/>'>
          	</div>
           <% } %>
    	     <div class="form-group col-sm-6">
    	       <div class="captcha">
               <% if (exibeCaptcha) { %>
                 <img name="captcha_img" src="../captcha.jpg?t=<%=DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss")%>" alt='<hl:message key="rotulo.captcha.codigo"/>' title='<hl:message key="rotulo.captcha.codigo"/>'/>
                 <a href="#no-back" onclick="reloadCaptcha()"><img src="../img/icones/refresh.png" alt='<hl:message key="rotulo.captcha.novo.codigo"/>' title='<hl:message key="rotulo.captcha.novo.codigo"/>' border="0"/></a>
                 <a href="#no-back" class="btn-i-right pr-1" data-bs-toggle="popover" title="<hl:message key="rotulo.ajuda" />"
                   data-bs-content='<hl:message key="mensagem.ajuda.captcha.operacao.imagem.v3"/>'
                   data-original-title=<hl:message key="rotulo.ajuda" />>
                   <img src="../img/icones/help.png" alt='<hl:message key="rotulo.ajuda" />' title='<hl:message key="rotulo.ajuda" />' border="0">
                 </a>
               <% } else if (exibeCaptchaAvancado) { %>
                 <hl:recaptcha />
               <% } else if (exibeCaptchaDeficiente) {%>
                    <div id="divCaptchaSound"></div>
                    <a href="#no-back" onclick="reloadSimpleCaptcha()"><img src="../img/icones/refresh.png" alt='<hl:message key="rotulo.captcha.novo.audio"/>' title='<hl:message key="rotulo.captcha.novo.audio"/>' border="0"/></a>
                    <a href="#no-back" onclick="helpCaptcha3();"><img src="../img/icones/help.png" alt='<hl:message key="rotulo.ajuda"/>' title='<hl:message key="rotulo.ajuda"/>' border="0"/></a>
               <% } %>
    		   </div>
           </div>
        </div>

        <input name="CSA_CODIGO" type="hidden" value="<%=TextHelper.forHtmlAttribute((responsavel.isCsa()) ? responsavel.getCsaCodigo() : "")%>">
        <input name="COR_CODIGO" type="HIDDEN" value="<%=TextHelper.forHtmlAttribute((responsavel.isCor()) ? responsavel.getCorCodigo() : "")%>">

      </form>
    </div>
  </div>
  <div class="btn-action">
  <% if(!exibirArquivo) { %>
    <a class="btn btn-outline-danger" aria-label="<hl:message key="rotulo.botao.voltar"/>" href="#" onClick="postData('../v3/carregarPrincipal')"><hl:message key="rotulo.botao.voltar"/></a>
  <%}%>
  <%if (exibeCampoUpload) { %>
    <button class="btn btn-primary" type="submit" onClick="if(vf_upload_arquivos()){ f0.submit();} return false"><svg width="17"><use xlink:href="#i-confirmar"></use></svg> <hl:message key="rotulo.botao.confirmar"/></button>
  <% } %>
  </div>

  <% if(exibirArquivo) { %>
    <div class="card">
      <div class="card-header hasIcon">
        <span class="card-header-icon"><svg width="25"><use xlink:href="../img/sprite.svg#i-relatorio"></use></svg></span>
        <h2 class="card-header-title"><%=ApplicationResourcesHelper.getMessage("rotulo.upload.arquivo."+tipo+".disponivel", responsavel) %></h2>
      </div>
      <div class="card-body table-responsive p-0">
        <table class="table table-striped table-hover">
          <thead>
            <tr>
              <th scope="col"><hl:message key="rotulo.upload.arquivo.nome"/></th>
              <th scope="col"><hl:message key="rotulo.upload.arquivo.tamanho"/></th>
              <th scope="col"><hl:message key="rotulo.upload.arquivo.data"/></th>
              <%if(responsavel.isCseSup() && !tipo.equals("recuperacaoCredito")){%>
                <th scope="col"><hl:message key="rotulo.orgao.singular"/>-<br><hl:message key = "rotulo.estabelecimento.abreviado"/></th>
              <%} else if (responsavel.isSup() && tipo.equals("recuperacaoCredito")) {%>
                <th scope="col"><hl:message key="rotulo.consignataria.singular"/></th>
              <%}%>
              <th scope="col" width="15%"><hl:message key="rotulo.acoes.upload.arquivo.acoes"/></th>
            </tr>
          </thead>
          <tbody>

          <%
          if (arquivosCombo == null || arquivosCombo.size() == 0) {
          %>
            <tr class="Lp">
              <td colspan="7"><hl:message key="mensagem.erro.upload.arquivo.nenhum.encontrado"/></td>
            </tr>
          <%
          } else {
            int i = 0;
            for (ArquivoDownload arquivo : arquivosCombo) {
        %>
              <tr>
                <td><%=TextHelper.forHtmlContent(arquivo.getNomeOriginal())%></td>
                <td><%=TextHelper.forHtmlContent(arquivo.getTamanho())%></td>
                <td><%=TextHelper.forHtmlContent(arquivo.getData())%></td>
                <%if(responsavel.isCseSup()){%>
                  <td><%=TextHelper.forHtmlContent(arquivo.getEntidade())%></td>
                <%}%>
                <td>
                  <% if (responsavel.temPermissao(CodedValues.FUN_DOW_ARQ_INTEGRACAO) || tipo.equals("saldodevedor")) {%>
                    <div class="actions">
                      <a class="ico-action" href="#">
                        <div class="form-inline" onClick="javascript:downloadArquivo(encodeURIComponent('<%=TextHelper.forJavaScript(arquivo.getNome())%>'),'<%=TextHelper.forJavaScript(tipo)%>'); return false;">
                          <span class="mr-1" data-bs-toggle="tooltip" aria-label="<hl:message key="rotulo.botao.download"/> <%=arquivo.getNomeOriginal()%>" title="" data-original-title="download">
                            <svg> <use xlink:href="../img/sprite.svg#i-download"></use></svg>
                          </span>
                          <hl:message key="rotulo.acoes.upload.arquivo.download"/>
                        </div>
                      </a>
                    </div>
                  <% } %>
                </td>
              </tr>
        <%
            }
          }
        %>

          </tbody>
          <tfoot>
	        <tr>
              <td colspan="5"><%=ApplicationResourcesHelper.getMessage("mensagem.upload.arquivo.resultado."+tipo+".disponivel", responsavel) %>
              </td>
            </tr>
          </tfoot>


        </table>
      </div>
    </div>

  <%} %>

  <% if(exibirArquivo && (tipo.equals("margem") || tipo.equals("margemcomplementar") ||
		tipo.equals("transferidos") || tipo.equals("retorno") || tipo.equals("retornoatrasado") || tipo.equals("critica") || tipo.equals("xmlMargemRetornoMovimento")  || tipo.equals("saldodevedor"))) { %>
    <div class="card">
      <div class="card-header hasIcon">
        <span class="card-header-icon"><svg width="25"><use xlink:href="../img/sprite.svg#i-relatorio"></use></svg></span>
        <h2 class="card-header-title"><%=ApplicationResourcesHelper.getMessage("rotulo.upload.historico.arquivo.enviado", responsavel) %></h2>
      </div>
      <div class="card-body table-responsive p-0">
        <table class="table table-striped table-hover">
          <thead>
            <tr>
              <th scope="col"><hl:message key="rotulo.upload.arquivo.nome"/></th>
              <th scope="col"><hl:message key="rotulo.upload.arquivo.data"/></th>
              <%if(responsavel.isCseSup()){%>
                <th scope="col"><hl:message key="rotulo.orgao.singular"/>-<br><hl:message key = "rotulo.estabelecimento.abreviado"/></th>
              <%}%>
              <th scope="col"><hl:message key="rotulo.servidor.historico.responsavel.lower"/></th>
            </tr>
          </thead>
          <tbody>

          <%
          if (lstArquivoEnviados == null || lstArquivoEnviados.size() == 0){
          %>
            <tr class="Lp">
              <td colspan="7"><hl:message key="mensagem.erro.upload.arquivo.nenhum.encontrado"/></td>
            </tr>
          <%
          } else {
            int i = 0;
            Iterator<?> it = lstArquivoEnviados.iterator();
            while (it.hasNext()) {
              TransferObject arquivosEnviados = (TransferObject)it.next();

              String usuResponsavel = arquivosEnviados.getAttribute(Columns.USU_NOME).toString();
              Date data = (Date) arquivosEnviados.getAttribute(Columns.HAR_DATA_PROC);
        	  String nome = arquivosEnviados.getAttribute(Columns.HAR_NOME_ARQUIVO).toString();

              String formato = "";
              if (nome.toLowerCase().endsWith(".zip")) {
                formato = "zip.gif";
              } else if (nome.toLowerCase().endsWith(".txt")) {
                formato = "text.gif";
              } else {
                formato = "help.gif";
              }

              CustomTransferObject orgao = null;
              String org_identificador = "", est_identificador = "";

              orgao = (CustomTransferObject) orgaos.get(arquivosEnviados.getAttribute("CODIGO_ENTIDADE"));
              org_identificador = orgao != null ? orgao.getAttribute(Columns.ORG_IDENTIFICADOR).toString() : "";
              est_identificador = orgao != null ? orgao.getAttribute(Columns.EST_IDENTIFICADOR).toString() : "";

              nome = java.net.URLEncoder.encode(nome, "UTF-8");
        %>

              <tr>
                <td><%=TextHelper.forHtmlContent(nome)%></td>
                <td><%=TextHelper.forHtmlContent(data)%></td>
                <%if(responsavel.isCseSup()){%>
                <td><%=TextHelper.forHtmlContent(org_identificador.toUpperCase())%><%=(String)(!org_identificador.equals("") ? " - " : "")%><%=TextHelper.forHtmlContent(est_identificador.toUpperCase())%></td>
                <%}%>
                <td><%=TextHelper.forHtmlContent(usuResponsavel)%></td>
              </tr>
        <%
            }
          }
        %>
          </tbody>
          <tfoot>
	        <tr>
	        <td colspan="5"><%=ApplicationResourcesHelper.getMessage("mensagem.upload.arquivo.enviados", responsavel) %>
            </tr>
          </tfoot>


        </table>
      </div>
    </div>

  <div class="btn-action">
    <a class="btn btn-outline-danger" aria-label="<hl:message key="rotulo.botao.voltar"/>" href="#" onClick="postData('../v3/carregarPrincipal')"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
  <%} %>

  <!-- Modal aguarde -->
  <div class="modal fade" id="modalAguarde" data-focus="false" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
   <div class="modal-dialog-upload modal-dialog" role="document">
     <div class="modal-content">
       <div class="modal-body">
         <div class="row">
           <div class="col-md-12 d-flex justify-content-center">
             <img src="../img/loading.gif" class="loading">
           </div>
           <div class="col-md-12">
             <div class="modal-body"><span><hl:message key="mensagem.upload.generico.aguarde"/></span></div>
           </div>
         </div>
       </div>
     </div>
   </div>
  </div>
<% } %>
</c:set>

<c:set var="javascript">
<% if (exibeCaptchaAvancado) { %>
<script src='https://www.google.com/recaptcha/api.js'></script>
<% } %>
<script type="text/JavaScript">

  function formFullLoad() {
    formLoad();
    doLoad(<%=(boolean)(temProcessoRodando)%>);
  }

  function formLoad() {
    if (f0.tipo != null) {
      f0.tipo.focus();
    }

    <% if (TextHelper.isNull(csaCodigo)) {%>
    desabilitaCsaCodigo();
    <% } %>
    <% if (correspondentes == null || correspondentes.isEmpty()) {%>
    desabilitaCorCodigo();
    <% } %>
    <% if (exibeCaptchaDeficiente) {%>
    montaCaptchaSom();
    <% } %>
  }

  function doLoad(reload) {
    if (reload) {
      setTimeout("refresh()", 10*1000);
    }
  }

  function refresh() {
    postData('<%=SynchronizerToken.updateTokenInURL(action.split("\\?")[0] + "?acao=carregar", request)%>');
  }

  function vf_nome_arquivo() {

    var orgCodigo = '';
 <% if ((selecionaEstOrgUploadMargemRetorno || selecionaEstOrgUploadContracheque) && responsavel.isCseSup()) { %>
      var papCodigo = getCheckedRadio('form1', 'PAP_CODIGO');

      if (papCodigo != null) {
        if (papCodigo == 'ORG') {
          orgCodigo = f0.ORG_CODIGO.options[f0.ORG_CODIGO.selectedIndex].value;
          if (orgCodigo == '') {
            alert('<%=ApplicationResourcesHelper.getMessage("mensagem.aviso.upload.selecione.orgao", responsavel)%>');
            f0.ORG_CODIGO.focus();
            f0.FILE1.value = '';
            return;
          }
        } else if (papCodigo == 'EST')  {
            estCodigo = f0.EST_CODIGO.options[f0.EST_CODIGO.selectedIndex].value;
            if (estCodigo == '') {
              alert('<%=ApplicationResourcesHelper.getMessage("mensagem.aviso.upload.selecione.estabelecimento", responsavel)%>');
              f0.EST_CODIGO.focus();
              f0.FILE1.value = '';
              return;
            }
        }
      }
<%  } %>

    var targetFileName = f0.FILE1.value;
      <%
      if (arquivosCombo != null && !arquivosCombo.isEmpty()) {
      %>
        if (targetFileName != null && targetFileName != '') {
          var arrayFiles = new Array("<%=TextHelper.forJavaScriptBlock(TextHelper.join(arquivosCombo, "\",\""))%>");
          for (i=0;i<arrayFiles.length;i++) {
            var nomeAbrev = targetFileName.substring(targetFileName.lastIndexOf("\\") + 1, targetFileName.length);
            var codIdnResponsavel = '<%=TextHelper.forJavaScriptBlock(responsavel.getCodigoEntidade())%>';
            if (arrayFiles[i].replace(/^.*[\\\/]/, '') == nomeAbrev) {
           <% if (TextHelper.isNull(papCodigo) || papCodigo.equals(AcessoSistema.ENTIDADE_CSE)) { %>
              <%if (responsavel.getTipoEntidade().equals(AcessoSistema.ENTIDADE_CSE)) {%>
                  var path = "cse/";
                  if (orgCodigo != '') {
                     path += orgCodigo + "/";
                  }

                  if (arrayFiles[i].indexOf(path + nomeAbrev) > -1) {
                    alert('<%=ApplicationResourcesHelper.getMessage("mensagem.aviso.upload.sobreposicao.arquivo", responsavel)%>'.replace('{0}',nomeAbrev));
                    return;
                  }
              <%} else if (!codigosOrgao.isEmpty()) { %>
                      var arrayOrgId = new Array("<%=TextHelper.forJavaScriptBlock(TextHelper.join(codigosOrgao, "\",\""))%>");
                      for (j=0;j<arrayOrgId.length;j++) {
                        if (arrayFiles[i].indexOf("/" + arrayOrgId[j] + "/") > -1 && codIdnResponsavel == arrayOrgId[j]) {
                          alert('<%=ApplicationResourcesHelper.getMessage("mensagem.aviso.upload.sobreposicao.arquivo", responsavel)%>'.replace('{0}',nomeAbrev));
                          return;
                        }
                      }
                <% } %>
               <%} else {%>
                    alert('<%=ApplicationResourcesHelper.getMessage("mensagem.aviso.upload.sobreposicao.arquivo", responsavel)%>'.replace('{0}',nomeAbrev));
                    return;
               <%} %>
               }
             }
           }
      <% } %>
    }

  function vf_upload_arquivos() {
    var controles = '';
    var msgs = '';

 <% if (selecionaEstOrgUploadMargemRetorno || selecionaEstOrgUploadContracheque) { %>
    var papCodigo = getCheckedRadio('form1', 'PAP_CODIGO');

    if (papCodigo == null || papCodigo == '') {
      alert('<%=ApplicationResourcesHelper.getMessage("mensagem.aviso.upload.selecione.entidade", responsavel)%>');
      return;
    }

 <% if (responsavel.isCseSup()) { %>
      if (papCodigo == 'EST') {
        if (f0.EST_CODIGO.options[f0.EST_CODIGO.selectedIndex].value == '') {
          alert('<%=ApplicationResourcesHelper.getMessage("mensagem.aviso.upload.selecione.estabelecimento", responsavel)%>');
          f0.EST_CODIGO.focus();
          return;
        }
      }

      if (papCodigo == 'ORG') {
        if (f0.ORG_CODIGO.options[f0.ORG_CODIGO.selectedIndex].value == '') {
          alert('<%=ApplicationResourcesHelper.getMessage("mensagem.aviso.upload.selecione.orgao", responsavel)%>');
          f0.ORG_CODIGO.focus();
          return;
        }
      }
  <%} %>
<%} %>

  if (f0.CSA_CODIGO_AUX != null) {
    if (!f0.CSA_CODIGO_AUX.disabled && f0.CSA_CODIGO_AUX.selectedIndex > -1) {
      f0.CSA_CODIGO.value = getFieldValue(f0.CSA_CODIGO_AUX);
    }
  }

  if (f0.COR_CODIGO_AUX != null) {
    if (!f0.CSA_CODIGO_AUX.disabled  && f0.COR_CODIGO_AUX.selectedIndex > 0) {
      f0.COR_CODIGO.value = getFieldValue(f0.COR_CODIGO_AUX);
    }
  }

  if (f0.tipo.options[f0.tipo.selectedIndex].value == 'lote' || f0.tipo.options[f0.tipo.selectedIndex].value == 'conciliacao' || f0.tipo.options[f0.tipo.selectedIndex].value == 'arquivosEnviados' || f0.tipo.options[f0.tipo.selectedIndex].value == 'previafaturamentobeneficios' || f0.tipo.options[f0.tipo.selectedIndex].value == 'recuperacaoCredito' || f0.tipo.options[f0.tipo.selectedIndex].value == 'saldodevedor') {
    controles = new Array("FILE1", "tipo", "CSA_CODIGO_AUX");
    msgs = new Array ('<%=ApplicationResourcesHelper.getMessage("mensagem.aviso.upload.selecione.arquivo", responsavel)%>',
                      '<%=ApplicationResourcesHelper.getMessage("mensagem.aviso.upload.selecione.tipo.arquivo", responsavel)%>',
                      '<%=ApplicationResourcesHelper.getMessage("mensagem.aviso.upload.selecione.consignataria", responsavel)%>');
  } else {
      controles = new Array("FILE1", "tipo");
      msgs = new Array ('<%=ApplicationResourcesHelper.getMessage("mensagem.aviso.upload.selecione.arquivo", responsavel)%>',
              '<%=ApplicationResourcesHelper.getMessage("mensagem.aviso.upload.selecione.tipo.arquivo", responsavel)%>');
  }

  var ok = ValidaCampos(controles, msgs);
  if (ok) {
    $('#modalAguarde').modal({
	    backdrop: 'static',
	    keyboard: false
	});
  }

  return ok;
 }

  function downloadArquivo(arquivo,tipo) {
     endereco = "";
     if (tipo != "previafaturamentobeneficios") {
     	endereco = "../v3/downloadArquivo?arquivo_nome=" + arquivo +"&tipo=" + tipo + "&" + "<%=SynchronizerToken.generateToken4URL(request)%>";
     } else {
    	endereco = "../v3/downloadArquivo?arquivo_nome=" + arquivo +"&tipo=" + tipo + "&csaCodigo=<%=request.getParameter("CSA_CODIGO_AUX") != null ? request.getParameter("CSA_CODIGO_AUX") : request.getParameter("CSA_CODIGO")%>&<%=SynchronizerToken.generateToken4URL(request)%>";
     }
     postData(endereco,'download');
  }

  function alterarTipoArquivo() {
    var tipo = f0.tipo.options[f0.tipo.selectedIndex].value;

    var link = "../v3/uploadArquivo?acao=iniciar";

      if (tipo != '') {
      tipo = tipo.charAt(0).toUpperCase() + tipo.slice(1);
      link = "../v3/uploadArquivo" + tipo + "?acao=carregar";

      if (f0.PAP_CODIGO != null) {
        var papCodigo = getCheckedRadio('form1', 'PAP_CODIGO');
        link += "&PAP_CODIGO=" + papCodigo;
      }

      if (f0.EST_CODIGO != null) {
        var estCodigo = getFieldValue(f0.EST_CODIGO);
        link += "&EST_CODIGO=" + estCodigo;
      }

      if (f0.ORG_CODIGO != null) {
        var orgCodigo = getFieldValue(f0.ORG_CODIGO);
        link += "&ORG_CODIGO=" + orgCodigo;
      }

      if (f0.CSA_CODIGO_AUX != null) {
        var csaCodigo = getFieldValue(f0.CSA_CODIGO_AUX);
        link += "&CSA_CODIGO=" + csaCodigo;
      }

      if (f0.COR_CODIGO_AUX != null) {
        var corCodigo = getFieldValue(f0.COR_CODIGO_AUX);
        link += "&COR_CODIGO=" + corCodigo;
      }
    }

      link += "&" + "<%=SynchronizerToken.generateToken4URL(request)%>";
      postData(link);
  }


  function desabilitaCsaCodigo() {
    if (f0.tipo != null && f0.CSA_CODIGO_AUX != null) {
      if (f0.tipo.options[f0.tipo.selectedIndex].value == 'conciliacao') {
        f0.CSA_CODIGO_AUX.selectedIndex = -1;
        f0.CSA_CODIGO_AUX.disabled = false;
        desabilitaCorCodigo();
      } else if (f0.tipo.options[f0.tipo.selectedIndex].value == 'lote') {
        f0.CSA_CODIGO_AUX.selectedIndex = -1;
        f0.CSA_CODIGO_AUX.disabled = false;
      } else if (f0.tipo.options[f0.tipo.selectedIndex].value == 'previafaturamentobeneficios') {
        f0.CSA_CODIGO_AUX.selectedIndex = -1;
        f0.CSA_CODIGO_AUX.disabled = false;
      } else if (f0.tipo.options[f0.tipo.selectedIndex].value == 'recuperacaoCredito') {
          f0.CSA_CODIGO_AUX.selectedIndex = -1;
          f0.CSA_CODIGO_AUX.disabled = false;
      } else if (f0.tipo.options[f0.tipo.selectedIndex].value == 'saldodevedor') {
          f0.CSA_CODIGO_AUX.selectedIndex = -1;
          f0.CSA_CODIGO_AUX.disabled = false;
      } else if (f0.tipo.options[f0.tipo.selectedIndex].value == 'relatorioCustomizado') {
          f0.CSA_CODIGO_AUX.selectedIndex = -1;
          f0.CSA_CODIGO_AUX.disabled = false;
      } else {
        f0.CSA_CODIGO_AUX.selectedIndex = -1;
        f0.CSA_CODIGO_AUX.disabled = true;
        desabilitaCorCodigo();
      }
    }
  }

  function desabilitaCorCodigo() {
    var div = document.getElementById('correspondentes');
    if (div != null) {
      f0.COR_CODIGO_AUX.selectedIndex = -1;
      div.style.display = 'none';
    }
  }

  function montaComboCor() {
    if (f0.tipo.options[f0.tipo.selectedIndex].value == 'conciliacao') {
      return;
    }

    if (f0.CSA_CODIGO_AUX != null) {
      for (var i = 0 ; i < f0.CSA_CODIGO_AUX.length ; i++) {
        if (f0.CSA_CODIGO_AUX.options[i].selected) {
          f0.CSA_CODIGO.value = (f0.CSA_CODIGO_AUX.options[i].value);
          break;
        }
      }
    }

    //f0.submit();
    alterarTipoArquivo();
  }

  var f0 = document.forms[0];

  if (document.getElementById('captcha') != 'null' && document.getElementById('captcha') != null && document.getElementById('captcha') != 'undefined') {
      document.getElementById('captcha').blur();
  }

  window.onload = formFullLoad;
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
