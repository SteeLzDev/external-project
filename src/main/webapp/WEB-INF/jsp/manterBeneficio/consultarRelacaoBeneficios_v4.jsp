<%--
* <p>Title: consultarRelacaoBeneficios</p>
* <p>Description: Consultar relação benefícios v4</p>
* <p>Copyright: Copyright (c) 2005</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>

<%@ page import="com.zetra.econsig.values.TipoBeneficiarioEnum"%>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper"%>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper"%>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper"%>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.util.Date"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper"%>
<%@ page import="com.zetra.econsig.values.CodedValues"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ page import="com.zetra.econsig.values.Columns"%>
<%@ page import="java.util.List"%>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ page import="com.zetra.econsig.helper.web.JspHelper"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken"%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
	AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

	List<TransferObject> relacaoBeneficios = (List<TransferObject>) request.getAttribute("relacaoBeneficios");

    String ser_codigo = (String) request.getAttribute(Columns.SER_CODIGO);
    String rse_codigo = (String) request.getAttribute(Columns.RSE_CODIGO);
    String ben_codigo = (String) request.getAttribute(Columns.BEN_CODIGO);
    String contratosAtivos = (String) request.getAttribute("contratosAtivos");
    String scb_codigo_titular = null;

    boolean podeListarLançamentos= responsavel.temPermissao(CodedValues.FUN_LISTAR_LANCAMENTOS_CBE);
    boolean podeRegistrarOcorrencia = responsavel.temPermissao(CodedValues.FUN_REGISTRAR_OCORRENCIA_CONTRATO_BENEFICIO);
    boolean funEditarContratoBeneficio = responsavel.temPermissao(CodedValues.FUN_EDITAR_CONTRATO_BENEFICIO);
    boolean funEditarContratoBeneficioAvancado = responsavel.temPermissao(CodedValues.FUN_EDITAR_CONTRATO_BENEFICIO_AVANCADO);
    boolean permiteCancelarBeneficioSemAprovacao = (boolean) request.getAttribute("permiteCancelarBeneficioSemAprovacao");
    
    Iterator it = relacaoBeneficios.iterator();
    BigDecimal margem_utilizada = new BigDecimal(0);
    while (it.hasNext()) {
        CustomTransferObject relacaoBeneficio = (CustomTransferObject)it.next();
        margem_utilizada = margem_utilizada.add((BigDecimal) relacaoBeneficio.getAttribute(Columns.ADE_VLR));
        
        String tib_codigo = (String)relacaoBeneficio.getAttribute(Columns.TIB_CODIGO);
        
        if(tib_codigo.equals(TipoBeneficiarioEnum.TITULAR.tibCodigo)) { 
            scb_codigo_titular = (String) relacaoBeneficio.getAttribute(Columns.SCB_CODIGO);
          }
    }
    
    String somatorio = NumberHelper.format(margem_utilizada.doubleValue(), NumberHelper.getLang());
    somatorio = ApplicationResourcesHelper.getMessage("rotulo.relacao.beneficios.margem.utilizada.valor", responsavel, somatorio);
      
%>
<c:set var="title">
  <hl:message key="rotulo.relacao.beneficios.titulo" />
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-beneficios"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
    <div class="col-sm">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><%=relacaoBeneficios.get(0).getAttribute(Columns.NSE_DESCRICAO) != null ? TextHelper.forHtmlContent(relacaoBeneficios.get(0).getAttribute(Columns.NSE_DESCRICAO)) : ""%></h2>
        </div>
        <div class="card-body">
          <dl class="row data-list">
            <dt class="col-6"><hl:message key="rotulo.beneficio.codigo.plano" />: </dt>
            <dd class="col-6"><%=relacaoBeneficios.get(0).getAttribute(Columns.BEN_CODIGO) != null ? TextHelper.forHtmlContent(relacaoBeneficios.get(0).getAttribute(Columns.BEN_CODIGO)) : ""%></dd>
            <dt class="col-6"><hl:message key="rotulo.relacao.beneficios.descricao"/>: </dt>
            <dd class="col-6"><%=relacaoBeneficios.get(0).getAttribute(Columns.BEN_DESCRICAO) != null ? TextHelper.forHtmlContent(relacaoBeneficios.get(0).getAttribute(Columns.BEN_DESCRICAO)) : ""%></dd>
            <dt class="col-6"><hl:message key="rotulo.relacao.beneficios.operadora"/></dt>
            <dd class="col-6"><%=relacaoBeneficios.get(0).getAttribute(Columns.CSA_NOME) != null ? TextHelper.forHtmlContent(relacaoBeneficios.get(0).getAttribute(Columns.CSA_NOME)) : ""%></dd>
            <dt class="col-6"><hl:message key="rotulo.beneficio.codigo.contrato" />: </dt>
            <dd class="col-6"><%=relacaoBeneficios.get(0).getAttribute(Columns.BEN_CODIGO_CONTRATO) != null ? TextHelper.forHtmlContent(relacaoBeneficios.get(0).getAttribute(Columns.BEN_CODIGO_CONTRATO)) : ""%></dd>
            <dt class="col-6"><hl:message key="rotulo.beneficio.codigo.registro" />: </dt>
            <dd class="col-6"><%=relacaoBeneficios.get(0).getAttribute(Columns.BEN_CODIGO_REGISTRO) != null ? TextHelper.forHtmlContent(relacaoBeneficios.get(0).getAttribute(Columns.BEN_CODIGO_REGISTRO)) : ""%></dt>
            <dt class="col-6"><hl:message key="rotulo.relacao.beneficios.margem.utilizada"/></dt>
            <dd class="col-6"><%=somatorio%></dd>
          </dl>
        </div>
      </div>
    </div>
  </div>
  <div class="row">
    <div class="col-sm">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.relacao.beneficios.beneficiarios"/></h2>
        </div>
        <div class="card-body table-responsive p-0">
          <table class="table table-striped table-hover" id="myTable">
            <thead>
              <tr>
                <th class="ocultarColuna" scope="col" width="10%" title='<hl:message key="rotulo.simulacao.beneficio.selecione.todos.beneficiarios"/>' style="display: none;">
                  <form class="form-check">
                    <input type="checkbox" class="form-check-input ml-0" name="checkAll">
                  </form>
                </th>
                <% if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_RESERVA_SAUDE_SEM_FLUXO_MODULO_SAUDE, CodedValues.TPC_SIM, responsavel)) { %>
                <th scope="col"><hl:message key="rotulo.relacao.beneficios.nome"/></th>
                <th scope="col"><hl:message key="rotulo.relacao.beneficios.identificador"/></th>
                <th scope="col"><hl:message key="rotulo.relacao.beneficios.inicio.vigencia" /></th>
                <th scope="col"><hl:message key="rotulo.relacao.beneficios.tipo.beneficiario"/></th>
                <th scope="col"><hl:message key="rotulo.relacao.beneficios.situacao.contrato" /></th> 
                <th scope="col"><hl:message key="rotulo.relacao.beneficios.valor.mensalidade"/></th>
                <% } else {%>
                <th scope="col"><hl:message key="rotulo.relacao.beneficios.numero.cliente" /></th>
                <th scope="col"><hl:message key="rotulo.relacao.beneficios.nome"/></th>
                <th scope="col"><hl:message key="rotulo.relacao.beneficios.inicio.vigencia" /></th>
                  <%     if(responsavel.isSup() || responsavel.isSer()) { %>
                <th scope="col"><hl:message key="rotulo.relacao.beneficios.fim.vigencia" /></th>
                  <%     } %>
                <th scope="col"><hl:message key="rotulo.relacao.beneficios.tipo.beneficiario"/></th>
                <th scope="col"><hl:message key="rotulo.relacao.beneficios.situacao.contrato" /></th> 
                <th scope="col"><hl:message key="rotulo.relacao.beneficios.valor.mensalidade"/></th>
                <th scope="col"><hl:message key="rotulo.relacao.beneficios.valor.subsidio"/></th>
                <th scope="col"><hl:message key="rotulo.relacao.beneficios.valor.final"/></th>
                <%       if (responsavel.isSup()) { %>
                <th scope="col"><hl:message key="rotulo.relacao.beneficios.possui.restricao"/></th>
                  <%     } %>
                <%} %>
                <th><hl:message key="rotulo.relacao.beneficios.acoes"/></th>
              </tr>
            </thead>
            <tbody>
              <%
              it = relacaoBeneficios.iterator();
              while (it.hasNext()) {
                CustomTransferObject relacaoBeneficio = (CustomTransferObject)it.next();
                String bfc_codigo = (String)relacaoBeneficio.getAttribute(Columns.BFC_CODIGO);
                String cbe_numero = (String)relacaoBeneficio.getAttribute(Columns.CBE_NUMERO);
                String cbe_codigo = (String)relacaoBeneficio.getAttribute(Columns.CBE_CODIGO);
                String tib_codigo = (String)relacaoBeneficio.getAttribute(Columns.TIB_CODIGO);
                String scb_codigo = (String)relacaoBeneficio.getAttribute(Columns.SCB_CODIGO);
                String sad_codigo = (String)relacaoBeneficio.getAttribute(Columns.SAD_CODIGO);
                String bfc_subsidio_concedido = String.valueOf(relacaoBeneficio.getAttribute(Columns.BFC_SUBSIDIO_CONCEDIDO));
                String ade_codigo = (String)relacaoBeneficio.getAttribute(Columns.ADE_CODIGO);
                
                String bfc_nome = (String)relacaoBeneficio.getAttribute(Columns.BFC_NOME);
                String bfc_identificador = (String)relacaoBeneficio.getAttribute(Columns.BFC_IDENTIFICADOR);
                String cbe_inicio_vigencia = DateHelper.format((Date)relacaoBeneficio.getAttribute(Columns.CBE_DATA_INICIO_VIGENCIA), LocaleHelper.getDatePattern());
                String cbe_fim_vigencia = DateHelper.format((Date)relacaoBeneficio.getAttribute(Columns.CBE_DATA_FIM_VIGENCIA), LocaleHelper.getDatePattern());
                String tipo_beneficiario = (String)relacaoBeneficio.getAttribute(Columns.TIB_DESCRICAO);
                String valor_mensalidade = NumberHelper.format(((BigDecimal) relacaoBeneficio.getAttribute(Columns.CBE_VALOR_TOTAL)).doubleValue(), NumberHelper.getLang());
                String valor_subsidio = NumberHelper.format(((BigDecimal) relacaoBeneficio.getAttribute(Columns.CBE_VALOR_SUBSIDIO)).doubleValue(), NumberHelper.getLang());
                String valor_final = NumberHelper.format(((BigDecimal) relacaoBeneficio.getAttribute(Columns.ADE_VLR)).doubleValue(), NumberHelper.getLang());
              %>
              <tr class="selecionarLinha">
                <td class="ocultarColuna" aria-label='<hl:message key="rotulo.simulacao.beneficio.selecione.multiplos.beneficiarios"/>' title='<hl:message key="rotulo.simulacao.beneficio.selecione.multiplos.beneficiarios"/>' style="display: none;">
                  <div class="form-check">
                    <input type="checkbox" class="form-check-input ml-0" name="selecionarCheckBox">
                  </div>
                </td>
                <% if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_RESERVA_SAUDE_SEM_FLUXO_MODULO_SAUDE, CodedValues.TPC_SIM, responsavel)) { %>
                <td class="selecionarColuna"><%=bfc_nome != null ? TextHelper.forHtmlContent(bfc_nome) : ""%></td>
                <td class="selecionarColuna"><%=bfc_identificador != null ? TextHelper.forHtmlContent(bfc_identificador) : ""%></td>
                <td class="selecionarColuna"><%=cbe_inicio_vigencia != null ? TextHelper.forHtmlContent(cbe_inicio_vigencia) : ""%></td>
                <td class="selecionarColuna"><%=tipo_beneficiario != null ? TextHelper.forHtmlContent(tipo_beneficiario) : ""%></td>
                <td class="selectionarColuna"><%= relacaoBeneficio.getAttribute(Columns.SCB_DESCRICAO) != null ? TextHelper.forHtmlContent(relacaoBeneficio.getAttribute(Columns.SCB_DESCRICAO)) : ""%></td>
                <td class="selecionarColuna" align="right"><%=valor_mensalidade != null ? TextHelper.forHtmlContent(valor_mensalidade) : ""%></td>
                <% } else {%>
                <td class="selecionarColuna"><%=cbe_numero != null ? TextHelper.forHtmlContent(cbe_numero) : ""%></td>
                <td class="selecionarColuna"><%=bfc_nome != null ? TextHelper.forHtmlContent(bfc_nome) : ""%></td>
                <td class="selecionarColuna"><%=cbe_inicio_vigencia != null ? TextHelper.forHtmlContent(cbe_inicio_vigencia) : ""%></td>
                <% if(responsavel.isSup() || responsavel.isSer()) { %>
                <td class="selecionarColuna"><%=cbe_fim_vigencia != null ? TextHelper.forHtmlContent(cbe_fim_vigencia) : ""%></td>
                <% } %>
                <td class="selecionarColuna"><%=tipo_beneficiario != null ? TextHelper.forHtmlContent(tipo_beneficiario) : ""%></td>
                <td class="selectionarColuna"><%= relacaoBeneficio.getAttribute(Columns.SCB_DESCRICAO) != null ? TextHelper.forHtmlContent(relacaoBeneficio.getAttribute(Columns.SCB_DESCRICAO)) : ""%></td>
                <td class="selecionarColuna" align="right"><%=valor_mensalidade != null ? TextHelper.forHtmlContent(valor_mensalidade) : ""%></td>
                <td class="selecionarColuna" align="right"><%=valor_subsidio != null ? TextHelper.forHtmlContent(valor_subsidio) : ""%></td>
                <td class="selecionarColuna" align="right"><%=valor_final != null ? TextHelper.forHtmlContent(valor_final) : ""%></td>
                <% if (responsavel.isSup()) { %>
                <td class="selecionarColuna" align="right"><%=bfc_subsidio_concedido != null ? (TextHelper.forHtmlContent(bfc_subsidio_concedido).equals(CodedValues.TPC_SIM)? ApplicationResourcesHelper.getMessage("rotulo.sim", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.nao", responsavel)) : ""%></td>
                <%} %>
                <% } %>
                <% if(!responsavel.isSer()) { %>
                <td>
                  <div class="actions">
                    <div class="dropdown">
                      <%  if ((scb_codigo.equals(CodedValues.SCB_CODIGO_CANCELADO) || scb_codigo.equals(CodedValues.SCB_CODIGO_CANCELAMENTO_SOLICITADO)) && !podeListarLançamentos) { %>
                        <span class="nao-disponivel">
    						<hl:message key="rotulo.acoes.nao.disponivel"/>
						</span>
                      <%  } else { %>
                      <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                      <div class="form-inline">
                        <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title='<hl:message key="rotulo.mais.acoes"/>' aria-label='<hl:message key="rotulo.mais.acoes"/>'> <svg>
                            <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                        </span>
                        <hl:message key="rotulo.acoes.lst.arq.generico.opcoes" />
                      </div>
                      </a>
                      <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                        <%
                           if(scb_codigo.equals(CodedValues.SCB_CODIGO_SOLICITADO) && sad_codigo.equals(CodedValues.SAD_SOLICITADO) && contratosAtivos.equals("true")) {
                        %>                                       
                        <a class="dropdown-item" href="#no-back" onClick="postData('../v3/aprovarSolicitacao?acao=aprovar&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&<%=Columns.getColumnName(Columns.BEN_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(ben_codigo)%>&<%=Columns.getColumnName(Columns.RSE_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(rse_codigo)%>&<%=Columns.getColumnName(Columns.BFC_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(bfc_codigo)%>&<%=Columns.getColumnName(Columns.CBE_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(cbe_codigo)%>&<%=Columns.getColumnName(Columns.SER_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(ser_codigo)%>&<%=Columns.getColumnName(Columns.TIB_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(tib_codigo)%>&<%=Columns.getColumnName(Columns.SCB_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(scb_codigo)%>&contratosAtivos=<%=contratosAtivos%>&_skip_history_=true'); return false;">
                          <hl:message key="rotulo.relacao.beneficios.aprovar.solicitacao" />
                        </a> 
                        <a class="dropdown-item" href="#no-back" onClick="postData('../v3/aprovarSolicitacao?acao=rejeitar&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&<%=Columns.getColumnName(Columns.BEN_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(ben_codigo)%>&<%=Columns.getColumnName(Columns.RSE_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(rse_codigo)%>&<%=Columns.getColumnName(Columns.BFC_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(bfc_codigo)%>&<%=Columns.getColumnName(Columns.CBE_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(cbe_codigo)%>&<%=Columns.getColumnName(Columns.SER_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(ser_codigo)%>&<%=Columns.getColumnName(Columns.TIB_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(tib_codigo)%>&<%=Columns.getColumnName(Columns.SCB_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(scb_codigo)%>&contratosAtivos=<%=contratosAtivos%>&_skip_history_=true'); return false;">
                          <hl:message key="rotulo.relacao.beneficios.cancelar.solicitacao" />
                        </a>
                          <%if(tib_codigo.equals(TipoBeneficiarioEnum.TITULAR.tibCodigo) && responsavel.temPermissao(CodedValues.FUN_EDT_ENDERECO_SERVIDOR) && responsavel.temPermissao(CodedValues.FUN_CONSULTAR_ENDERECO_SERVIDOR)) { %>
                              <a class="dropdown-item" href="#no-back" onClick="postData('../v3/editarServidor?acao=listarEndereco&SER_CODIGO=<%=TextHelper.forJavaScriptAttribute(ser_codigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>'); return false;">
                                <hl:message key="rotulo.servidor.manutencao.endereco.editar.item.menu" />
                              </a>
                          <%} %>
                        <%  } else if (scb_codigo.equals(CodedValues.SCB_CODIGO_ATIVO) && contratosAtivos.equals("true")) { %>
                        <a class="dropdown-item" href="#no-back" onClick="postData('../v3/aprovarSolicitacao?acao=cancelar&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&<%=Columns.getColumnName(Columns.BEN_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(ben_codigo)%>&<%=Columns.getColumnName(Columns.RSE_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(rse_codigo)%>&<%=Columns.getColumnName(Columns.BFC_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(bfc_codigo)%>&<%=Columns.getColumnName(Columns.CBE_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(cbe_codigo)%>&<%=Columns.getColumnName(Columns.SER_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(ser_codigo)%>&<%=Columns.getColumnName(Columns.TIB_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(tib_codigo)%>&<%=Columns.getColumnName(Columns.BFC_SUBSIDIO_CONCEDIDO) %>=<%=TextHelper.forJavaScriptAttribute(bfc_subsidio_concedido)%>&<%=Columns.getColumnName(Columns.SCB_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(scb_codigo)%>&contratosAtivos=<%=contratosAtivos%>&_skip_history_=true'); return false;">
                          <hl:message key="rotulo.relacao.beneficios.cancelar.benecificio" />
                        </a> 
                        <% } %>
                        <%if(podeListarLançamentos){ %>
                        <a class="dropdown-item" href="#no-back" onClick="postData('../v3/listarLancamentosContratosBeneficios?acao=listar&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&<%=Columns.getColumnName(Columns.BEN_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(ben_codigo)%>&<%=Columns.getColumnName(Columns.RSE_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(rse_codigo)%>&<%=Columns.getColumnName(Columns.BFC_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(bfc_codigo)%>&<%=Columns.getColumnName(Columns.CBE_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(cbe_codigo)%>&<%=Columns.getColumnName(Columns.SER_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(ser_codigo)%>&<%=Columns.getColumnName(Columns.TIB_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(tib_codigo)%>&contratosAtivos=<%=contratosAtivos%>'); return false;">
                          <hl:message key="rotulo.relacao.beneficios.listar.lancamentos" />
                        </a>
                        <% } %>
                        <% if (podeRegistrarOcorrencia) { %>
                        <a class="dropdown-item" href="#no-back" onClick="postData('../v3/registrarOcorrenciaContratoBeneficio?acao=iniciar&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&<%=Columns.getColumnName(Columns.BEN_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(ben_codigo)%>&<%=Columns.getColumnName(Columns.RSE_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(rse_codigo)%>&<%=Columns.getColumnName(Columns.BFC_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(bfc_codigo)%>&<%=Columns.getColumnName(Columns.CBE_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(cbe_codigo)%>&<%=Columns.getColumnName(Columns.SER_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(ser_codigo)%>&<%=Columns.getColumnName(Columns.TIB_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(tib_codigo)%>&contratosAtivos=<%=contratosAtivos%>'); return false;">
                          <hl:message key="rotulo.relacao.beneficios.registrar.ocorrencia" />
                        </a>
                        <% } %>

                        <% if (funEditarContratoBeneficio) { %>
                        <a class="dropdown-item" href="#no-back" onClick="postData('../v3/alterarContratoBeneficio?acao=visualizar&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&cbeCodigo=<%=TextHelper.forJavaScriptAttribute(cbe_codigo)%>'); return false;">
                          <hl:message key="rotulo.acoes.editar" />
                        </a>
                        <% } %>
                        <% if (funEditarContratoBeneficioAvancado && scb_codigo.equals(CodedValues.SCB_CODIGO_AGUARDANDO_EXCLUSAO_OPERADORA)) { %>
                          <a class="dropdown-item" href="#no-back" onClick="postData('../v3/alterarContratoBeneficio?acao=visualizar&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&cbeCodigo=<%=TextHelper.forJavaScriptAttribute(cbe_codigo)%>&exclusaoManual=true'); return false;">
                            <hl:message key="rotulo.contrato.beneficio.acao.exclusao.manual" />
                          </a>
                        <% } %>
                        <% if (funEditarContratoBeneficioAvancado && scb_codigo.equals(CodedValues.SCB_CODIGO_AGUARDANDO_INCLUSAO_OPERADORA)) { %>
                          <a class="dropdown-item" href="#no-back" onClick="postData('../v3/alterarContratoBeneficio?acao=visualizar&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&cbeCodigo=<%=TextHelper.forJavaScriptAttribute(cbe_codigo)%>&inclusaoManual=true'); return false;">
                            <hl:message key="rotulo.contrato.beneficio.acao.inclusao.manual" />
                          </a>
                          <a class="dropdown-item" href="#no-back" onClick="postData('../v3/alterarContratoBeneficio?acao=visualizar&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&cbeCodigo=<%=TextHelper.forJavaScriptAttribute(cbe_codigo)%>&cancelarInclusao=true'); return false;">
                            <hl:message key="rotulo.contrato.beneficio.acao.cancelar.inclusao" />
                          </a>
                        <% } %>
                        <% if (scb_codigo.equals(CodedValues.SCB_CODIGO_CANCELAMENTO_SOLICITADO_BENEFICIARIO) && !ParamSist.paramEquals(CodedValues.TPC_PERMITE_CANCELAR_BENEFICIO_SEM_APROVACAO, CodedValues.TPC_SIM, responsavel)) { %>
                          <a class="dropdown-item" href="#no-back" onClick="postData('../v3/aprovarSolicitacao?acao=cancelar&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&<%=Columns.getColumnName(Columns.BEN_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(ben_codigo)%>&<%=Columns.getColumnName(Columns.RSE_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(rse_codigo)%>&<%=Columns.getColumnName(Columns.BFC_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(bfc_codigo)%>&<%=Columns.getColumnName(Columns.CBE_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(cbe_codigo)%>&<%=Columns.getColumnName(Columns.SER_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(ser_codigo)%>&<%=Columns.getColumnName(Columns.TIB_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(tib_codigo)%>&<%=Columns.getColumnName(Columns.BFC_SUBSIDIO_CONCEDIDO) %>=<%=TextHelper.forJavaScriptAttribute(bfc_subsidio_concedido)%>&<%=Columns.getColumnName(Columns.SCB_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(scb_codigo)%>&contratosAtivos=<%=contratosAtivos%>&_skip_history_=true'); return false;">
                            <hl:message key="rotulo.contrato.beneficio.acao.aprovar.cancelamento" />
                          </a>
                          <a class="dropdown-item" href="#no-back" onClick="postData('../v3/aprovarSolicitacao?acao=desfazerCancelamento&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&<%=Columns.getColumnName(Columns.BEN_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(ben_codigo)%>&<%=Columns.getColumnName(Columns.RSE_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(rse_codigo)%>&<%=Columns.getColumnName(Columns.BFC_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(bfc_codigo)%>&<%=Columns.getColumnName(Columns.CBE_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(cbe_codigo)%>&<%=Columns.getColumnName(Columns.SER_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(ser_codigo)%>&<%=Columns.getColumnName(Columns.TIB_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(tib_codigo)%>&<%=Columns.getColumnName(Columns.BFC_SUBSIDIO_CONCEDIDO) %>=<%=TextHelper.forJavaScriptAttribute(bfc_subsidio_concedido)%>&<%=Columns.getColumnName(Columns.SCB_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(scb_codigo)%>&contratosAtivos=<%=contratosAtivos%>&_skip_history_=true'); return false;">
                            <hl:message key="rotulo.contrato.beneficio.acao.cancelar.solicitacao.cancelamento" />
                          </a>
                        <% } %>
                        <%if(responsavel.isSup()){ %>
                        <a class="dropdown-item" href="#no-back" onClick="postData('../v3/listarOcorrenciaContratoBeneficio?acao=listar&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&<%=Columns.getColumnName(Columns.BEN_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(ben_codigo)%>&<%=Columns.getColumnName(Columns.RSE_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(rse_codigo)%>&<%=Columns.getColumnName(Columns.BFC_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(bfc_codigo)%>&<%=Columns.getColumnName(Columns.CBE_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(cbe_codigo)%>&<%=Columns.getColumnName(Columns.SER_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(ser_codigo)%>&<%=Columns.getColumnName(Columns.TIB_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(tib_codigo)%>&contratosAtivos=<%=contratosAtivos%>'); return false;">
                          <hl:message key="rotulo.historico" />
                        </a>
                        <% } %>
                        <a class="dropdown-item"
                           style="cursor: pointer;"
                           onClick="postData('../v3/consultarConsignacao?acao=detalharConsignacao&ADE_CODIGO=<%=TextHelper.forJavaScriptAttribute(ade_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"
                           aria-label='<hl:message key="rotulo.relacao.beneficios.detalhar.consignacao" />'><hl:message
                           key="rotulo.relacao.beneficios.detalhar.consignacao" />
                        </a>
                      </div>
                      <%  } %>
                    </div>
                  </div>
                </td>
                <% } else if(responsavel.isSer()) { %>
                <td>
                  <div class="actions">
                    <div class="dropdown">
                      <%  if ((scb_codigo.equals(CodedValues.SCB_CODIGO_CANCELADO) || scb_codigo.equals(CodedValues.SCB_CODIGO_CANCELAMENTO_SOLICITADO)) && !podeListarLançamentos) { %>
                        <span class="nao-disponivel">
    						<hl:message key="rotulo.acoes.nao.disponivel"/>
						</span>
                      <%  } else { %>
                            <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                              <div class="form-inline">
                                <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title='<hl:message key="rotulo.mais.acoes"/>' aria-label='<hl:message key="rotulo.mais.acoes"/>'> <svg>
                                    <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                                </span>
                                <hl:message key="rotulo.acoes.lst.arq.generico.opcoes" />
                              </div>
                            </a>
                            <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                            <%if(scb_codigo.equals(CodedValues.SCB_CODIGO_ATIVO) && responsavel.temPermissao(CodedValues.FUN_SER_SOLICITAR_CANCELAMENTO_BENEFICIO)) {%>
                                  <a class="dropdown-item" href="#no-back" onClick="if(validaCancelamento(<%=TextHelper.forJavaScriptAttribute(tib_codigo)%>)){postData('../v3/aprovarSolicitacao?acao=solicitarCancelamento&solicitacao=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&<%=Columns.getColumnName(Columns.BEN_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(ben_codigo)%>&<%=Columns.getColumnName(Columns.RSE_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(rse_codigo)%>&<%=Columns.getColumnName(Columns.BFC_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(bfc_codigo)%>&<%=Columns.getColumnName(Columns.CBE_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(cbe_codigo)%>&<%=Columns.getColumnName(Columns.SER_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(ser_codigo)%>&<%=Columns.getColumnName(Columns.TIB_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(tib_codigo)%>&<%=Columns.getColumnName(Columns.BFC_SUBSIDIO_CONCEDIDO)%>=<%=TextHelper.forJavaScriptAttribute(bfc_subsidio_concedido)%>&<%=Columns.getColumnName(Columns.SCB_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(scb_codigo)%>&contratosAtivos=<%=contratosAtivos%>&_skip_history_=true'); return false;};">
                                    <hl:message key="rotulo.relacao.beneficios.solicitar.cancelamento.benecificio" />
                                  </a>
                            <% } %>
                            <%if(podeListarLançamentos){ %>
                                  <a class="dropdown-item" href="#no-back" onClick="postData('../v3/listarLancamentosContratosBeneficios?acao=listar&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&<%=Columns.getColumnName(Columns.BEN_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(ben_codigo)%>&<%=Columns.getColumnName(Columns.RSE_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(rse_codigo)%>&<%=Columns.getColumnName(Columns.BFC_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(bfc_codigo)%>&<%=Columns.getColumnName(Columns.CBE_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(cbe_codigo)%>&<%=Columns.getColumnName(Columns.SER_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(ser_codigo)%>&<%=Columns.getColumnName(Columns.TIB_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(tib_codigo)%>&contratosAtivos=<%=contratosAtivos%>'); return false;">
                                    <hl:message key="rotulo.relacao.beneficios.listar.lancamentos" />
                                  </a>
                            <% } %>
                        <% } %>
                    </div>
                  </div>
                </td>
              <%} %>
              </tr>
            <% } %>            
            </tbody>
            <tfoot>
              <tr>
                <td colspan="7"><hl:message key="rotulo.relacao.beneficios.titular.dependentes"/></td>
              </tr>
            </tfoot>
          </table>
        </div>
      </div>
    </div>
  </div>
   <div class="float-end">
    <div class="btn-action">
      <a href="#no-back" name="Button" class="btn btn-outline-danger" 
        onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"
      ><hl:message key="rotulo.botao.voltar"/></a>
    </div>
  </div>
</c:set>
<c:set var="javascript">
  <script>
  	function validaCancelamento(tipoBenef){
  		
  	  		var tibCodigo = tipoBenef;
  	  		var tibCodigoTitular = <%=TipoBeneficiarioEnum.TITULAR.tibCodigo%>;
  	  		var benDescricao = '<%=TextHelper.forJavaScript((String)(relacaoBeneficios.get(0).getAttribute(Columns.BEN_DESCRICAO)))%>';
  	  		
  	  		console.log(benDescricao);
  	  		
  			if(<%=permiteCancelarBeneficioSemAprovacao%>){
  				if (tibCodigo == tibCodigoTitular) {
  					if (confirm('<hl:message key="rotulo.confirma.solicitacao.cancelamento.beneficio.param.habilitado.titular"/>'.replace('{0}', benDescricao))) {
  						return true;
  		  			}
  				} else {
  					if (confirm('<hl:message key="rotulo.confirma.solicitacao.cancelamento.beneficio.param.habilitado"/>')) {	
  						return true;
  		  			}
  				}
  			} else {
  				if (tibCodigo == tibCodigoTitular) {
  					if (confirm('<hl:message key="rotulo.confirma.solicitacao.cancelamento.beneficio.param.desabilitado.titular"/>'.replace('{0}', benDescricao))) {
  						return true;
  					}
  				} else {
  					if (confirm('<hl:message key="rotulo.confirma.solicitacao.cancelamento.beneficio.param.desabilitado"/>')) {
  						return true;
  					}
  				}
  			}
  		}
  </script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>