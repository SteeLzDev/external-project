<%--
* <p>Title: Editar Servico</p>
* <p>Description: Lista de serviços no sistema para edicao</p>
* <p>Copyright: Copyright (c) 2005</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: andrea.giorgini $
* $Revision: 26732 $
* $Date: 2019-05-16 11:21:52 -0300 (qui, 16 mai 2019) $
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t"     tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl"    uri="/html-lib" %>
<%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.dto.web.*"%>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
  
  boolean podeEditarSvc = (Boolean) request.getAttribute("podeEditarSvc");
  boolean permitePriorizarServico = (Boolean) request.getAttribute("permitePriorizarServico");
  boolean temControleCompulsorios = (Boolean) request.getAttribute("temControleCompulsorios");
  boolean exibeBotaoRodape = (Boolean) request.getAttribute("exibeBotaoRodape");
  boolean validaTaxaRenegociacao = (Boolean) request.getAttribute("validaTaxaRenegociacao");
  boolean validaTaxaPortabilidade = (Boolean) request.getAttribute("validaTaxaPortabilidade");

  int grupos = 0;
  
  List<?> paramTarif = (List<?>) request.getAttribute("paramTarif");
  List<GrupoParametroServico> gruposParametros = (List<GrupoParametroServico>) request.getAttribute("gruposParametros");
  List<RelacionamentoServico> relacionamentoSvc = (List<RelacionamentoServico>) request.getAttribute("relacionamentoSvc");
  List<TransferObject> lstOcorrencias = (List<TransferObject>) request.getAttribute("lstOcorrencias");
  
  Map<String, String> hshParamSvcCse = (Map<String, String>) request.getAttribute("paramSvcCseMap");
  
  ServicoTransferObject servico = (ServicoTransferObject) request.getAttribute("servico");
  
  String svc_codigo = (String) request.getAttribute("svc_codigo");
  String svcNseCodigo  = (String) request.getAttribute("svcNseCodigo");
  
  StringBuffer campos = new StringBuffer();
  StringBuffer pcv_campos = new StringBuffer();
%>
<c:set var="title">
  <hl:message key="rotulo.servico.manutencao.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
    <div class="col-sm">
    <form method="post" action="../v3/manterServico?<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
<!-- Dados básicos do serviço -->
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.servico.dados.basicos"/></h2>
        </div>
        <div class="card-body">
          <div class="row">
            <div class="form-group col-sm-12 col-md-4 mt-1">
                <label for="SVC_IDENTIFICADOR"><hl:message key="rotulo.servico.identificador"/></label>
                <input class="form-control" name="SVC_IDENTIFICADOR" id="SVC_IDENTIFICADOR" type="text" size="10"
                  value="<%=TextHelper.forHtmlAttribute(servico.getSvcIdentificador())%>"
                  <%=TextHelper.forHtmlContent((String)(!podeEditarSvc ? "disabled" : ""))%>
                  onFocus="SetarEventoMascara(this,'#A40',true);" onBlur="fout(this);ValidaMascara(this);"
                	placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.servico.identificador", responsavel).toLowerCase()) %>"
                />
            </div>
            <div class="form-group col-sm-12 col-md-4 mt-1">
              <label for="SVC_DESCRICAO"><hl:message key="rotulo.servico.descricao"/></label>
              <INPUT class="form-control" name="SVC_DESCRICAO" id="SVC_DESCRICAO" type="text" size="32" 
                VALUE="<%=TextHelper.forHtmlAttribute(servico.getSvcDescricao())%>" <%=TextHelper.forHtmlContent((String)(!podeEditarSvc ? "disabled" : ""))%> 
                onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);"
                placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.servico.descricao", responsavel).toLowerCase()) %>"
                />
            </div>
            <div class="form-group col-sm-12 col-md-4 mt-1">
                <label for="NSE_CODIGO"><hl:message key="rotulo.param.svc.natureza.servico"/></label>
                <%List<?> naturezas = (List<?>) request.getAttribute("naturezas");
                  request.setAttribute("naturezas", naturezas); %>
                <hl:htmlcombo listName="naturezas" name="NSE_CODIGO" classe="form-control"   
                    fieldValue="<%=TextHelper.forHtmlAttribute( Columns.NSE_CODIGO )%>" 
                    fieldLabel="<%=TextHelper.forHtmlAttribute( Columns.NSE_DESCRICAO )%>" 
                    selectedValue="<%=TextHelper.forHtmlAttribute( svcNseCodigo )%>"
                    onChange="submitRecarregarNseCodigo()"
                    disabled='<%=(boolean)(!podeEditarSvc ? true : false)%>'
                />
            </div>
          </div>
          <%-- Parametros de Tarifação --%>
          <%
             Iterator<?> it = paramTarif.iterator();
             while (it.hasNext()) {
               CustomTransferObject next = (CustomTransferObject)it.next();
    
               String tpt_codigo = next.getAttribute(Columns.TPT_CODIGO).toString();
               String tpt_tipo_interface = next.getAttribute(Columns.TPT_TIPO_INTERFACE).toString();
               String tpt_descricao = next.getAttribute(Columns.TPT_DESCRICAO).toString();
    
               int pcv_base_calc = next.getAttribute(Columns.PCV_BASE_CALC) != null ? Integer.parseInt(next.getAttribute(Columns.PCV_BASE_CALC).toString()) : 1;
               int pcv_forma_calc = next.getAttribute(Columns.PCV_FORMA_CALC) != null ? Integer.parseInt(next.getAttribute(Columns.PCV_FORMA_CALC).toString()) : 1;
               String pcv_codigo = next.getAttribute(Columns.PCV_CODIGO) != null ? next.getAttribute(Columns.PCV_CODIGO).toString() : "";
               String pcv_valor = next.getAttribute(Columns.PCV_VLR) != null ? next.getAttribute(Columns.PCV_VLR).toString() : "";
          %>
          <div class="row">
            <div class="form-group col-sm-12 col-md-4 mt-1">
              <label for="PCV_VLR-<%=TextHelper.forHtmlAttribute(tpt_codigo)%>"><%=TextHelper.forHtmlContent(tpt_descricao)%><%=pcv_forma_calc == 1 ? " (" + ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel) + ")" : " (" + ApplicationResourcesHelper.getMessage("rotulo.porcentagem", responsavel)+  ")"%></label>
             <%
               if (tpt_tipo_interface.equals("1")) {
                 pcv_campos.append("PCV_CODIGO-").append(tpt_codigo);
                 pcv_valor = (!pcv_valor.equals("") ? NumberHelper.reformat(pcv_valor, "en", NumberHelper.getLang()) : "");
             %>
              <INPUT class="form-control" NAME="PCV_VLR-<%=TextHelper.forHtmlAttribute(tpt_codigo)%>" id="PCV_VLR-<%=TextHelper.forHtmlAttribute(tpt_codigo)%>" 
                TYPE="text" VALUE="<%=TextHelper.forHtmlAttribute(pcv_valor)%>" SIZE="12" <%=(String)(!podeEditarSvc ? "disabled" : "")%> 
                onFocus="SetarEventoMascara(this,'#F12',true);" onBlur="fout(this);ValidaMascara(this);"
                placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, TextHelper.forHtmlContent(tpt_descricao).toLowerCase()) %>">
            </div>
            <div class="form-group col-sm-12 col-md-4 mt-1">
              <label for="PCV_FORMA_CALC-<%=TextHelper.forHtmlAttribute(tpt_codigo)%>"><hl:message key="rotulo.servico.csa.tipo.valor"/></label>
              <SELECT class="form-control form-select col-sm-12 m-1" NAME="PCV_FORMA_CALC-<%=TextHelper.forHtmlAttribute(tpt_codigo)%>" id="PCV_FORMA_CALC-<%=TextHelper.forHtmlAttribute(tpt_codigo)%>" <%=(String)(!podeEditarSvc ? "disabled" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
               <OPTION VALUE="1" <%=(String)(pcv_forma_calc==1?"SELECTED":"")%>><hl:message key="rotulo.servico.valor"/></OPTION>
               <OPTION VALUE="2" <%=(String)(pcv_forma_calc!=1?"SELECTED":"")%>><hl:message key="rotulo.servico.valor.percentual"/></OPTION>
              </SELECT> 
            </div>
            <div class="form-group col-sm-12 col-md-4 mt-1">
              <label for="PCV_BASE_CALC-<%=TextHelper.forHtmlAttribute(tpt_codigo)%>"><hl:message key="rotulo.servico.valor.sobre"/></label>
              <SELECT class="form-control form-select col-sm-12 m-1" NAME="PCV_BASE_CALC-<%=TextHelper.forHtmlAttribute(tpt_codigo)%>" id="PCV_BASE_CALC-<%=TextHelper.forHtmlAttribute(tpt_codigo)%>" <%=(String)(!podeEditarSvc ? "disabled" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                <%-- <OPTION VALUE="1" <%=(String)(pcv_base_calc==1?"SELECTED":"")%>>Operação</OPTION>  --%>
                <OPTION VALUE="2" <%=(String)(pcv_base_calc==2?"SELECTED":"")%>><hl:message key="rotulo.servico.parcela"/></OPTION>
                <%-- <OPTION VALUE="3" <%=(String)(pcv_base_calc==3?"SELECTED":"")%>>Operação/Verba</OPTION>
                <OPTION VALUE="4" <%=(String)(pcv_base_calc==4?"SELECTED":"")%>>Parcela/Verba</OPTION> --%>
              </SELECT>
            </div>
            <INPUT TYPE="hidden" NAME="TPT_CODIGO-<%=TextHelper.forHtmlAttribute(tpt_codigo)%>" VALUE="<%=TextHelper.forHtmlAttribute(tpt_codigo)%>">
            <INPUT TYPE="hidden" NAME="PCV_CODIGO-<%=TextHelper.forHtmlAttribute(tpt_codigo)%>" VALUE="<%=TextHelper.forHtmlAttribute(pcv_codigo)%>">
            <INPUT TYPE="hidden" NAME="PCV_ATIVO-<%=TextHelper.forHtmlAttribute(tpt_codigo)%>" VALUE="1">
           <%
             } else {
              session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
             }
           %>
            </div>
           <%
             if (it.hasNext()) pcv_campos.append(";");
           }
           %>

      <%-- INÍCIO DOS PARÂMETROS BÁSICOS DO SERVIÇO --%>
      <%-- Parametro Serviço que define a ordem de desconto do serviço gerado no movimento financeiro --%>
          <div class="row">
          <% if (permitePriorizarServico) { %>
            <div class="form-group col-sm-12 col-md-4 mt-1">
                <label for="SVC_PRIORIDADE"><hl:message key="rotulo.param.svc.permite.priorizar.servico"/></label>
                <input class="form-control" name="SVC_PRIORIDADE" id="SVC_PRIORIDADE" type="text" size="6"
                  value="<%=TextHelper.forHtmlAttribute(servico.getSvcPrioridade() != null ? servico.getSvcPrioridade().toString() : "" )%>"
                  <%=TextHelper.forHtmlContent((String)(!podeEditarSvc ? "disabled" : ""))%>
                  onFocus="SetarEventoMascara(this,'#D3',true);" onBlur="fout(this);ValidaMascara(this);"
                	placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.param.svc.permite.priorizar.servico", responsavel).toLowerCase()) %>"
                />
            </div>
          <% } %>
      <%-- Parametro Serviço que define grupo para o serviço --%>
          <% if (hshParamSvcCse.containsKey(CodedValues.TPS_GRUPO_SERVICO)) {
              String svcTgsCodigo = (servico.getSvcTgsCodigo() != null ? servico.getSvcTgsCodigo() : "");
              List<?> lstGrupo = (List<?>) request.getAttribute("lstGrupo");
              if (lstGrupo != null && !lstGrupo.isEmpty()) {
          %>
            <div class="form-group col-sm-12 col-md-4 mt-1">
              <label for="TGS_CODIGO"><hl:message key="rotulo.param.svc.grupo.servico"/></label>
              <SELECT class="form-control form-select col-sm-12 m-1" NAME="TGS_CODIGO" ID="TGS_CODIGO" <%=(String)(!podeEditarSvc ? "disabled" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                <OPTION VALUE=""><hl:message key="rotulo.campo.selecione"/></OPTION>
                 <%
                   Iterator<?> itGrpServico = lstGrupo.iterator();
                   while (itGrpServico.hasNext()) {
                     CustomTransferObject ctoGrpServico = (CustomTransferObject) itGrpServico.next();
                     String tgsCodigo = ctoGrpServico.getAttribute(Columns.TGS_CODIGO).toString();
                     String tgsGrupo  = ctoGrpServico.getAttribute(Columns.TGS_GRUPO).toString();
                 %>
                <OPTION VALUE="<%=TextHelper.forHtmlAttribute(tgsCodigo)%>" <%=svcTgsCodigo.equals(tgsCodigo) ? "SELECTED" : ""%>><%=TextHelper.forHtmlContent(tgsGrupo)%></OPTION>
                <% } %>
              </SELECT>
            </div>
            <% } %>
          <% } %>
          <% if (responsavel != null && responsavel.isSup()){ %>
            <div class="form-group col-sm-12 col-md-4 mt-1">
                <label for="SVC_OBS"><hl:message key="rotulo.servico.obs"/></label>
                <textarea class="form-control" id="SVC_OBS" name="SVC_OBS" rows="5" cols="50"><%=servico.getSvcObs() != null ? TextHelper.forHtmlAttribute(servico.getSvcObs().trim()) : ""%></textarea>
            </div>
          <% } %>
          </div>
        </div>
      </div>
<!-- Relacionamentos -->
      <%-- INÍCIO DO RELACIONAMENTO ENTRE OS SERVIÇOS --%>
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key='rotulo.servico.relacionamentos'/></h2>
        </div>
        <div class="card-body">
          <div class="row">
            <div class="form-group col-sm-12 col-md-12 mt-1">
             <% for (RelacionamentoServico relacionamento : relacionamentoSvc) { %>
              <% pageContext.setAttribute(relacionamento.getNome(), relacionamento.getValores()); %>
              <hl:relacionamentoServicoV4 name="<%= relacionamento.getNome() %>" key="<%= relacionamento.getChaveDescricao() %>" disabled="<%= relacionamento.isDesabilitado() %>"/>                
             <% } %>
            </div>
          </div>
        </div>
      </div>
<%-- INÍCIO DOS PARÂMETROS DO SERVIÇO --%>
      <% 
      for (GrupoParametroServico grupo : gruposParametros) {
        if (grupo.getParametros() != null && !grupo.getParametros().isEmpty()) { 
      %>
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><%= grupo.getDescricao() %></h2>
        </div>
        <div class="card-body">
             <%
              for (ParametroServico paramSvc : grupo.getParametros()) {
                if (!paramSvc.isCustom() && !paramSvc.isCombo()) { %>
                 <hl:parametroServicoV4 codigo="<%= paramSvc.getCodigo() %>"
                                      descricao="<%= paramSvc.getDescricao() %>"
                                      dominio="<%= paramSvc.getDominio() %>"
                                      valor="<%= paramSvc.getValor() %>"
                                      valorPadrao="<%= paramSvc.getValorPadrao() %>"
                                      size="<%= paramSvc.getSize() %>"
                                      maxSize="<%= paramSvc.getMaxSize() %>"
                                      onClick="<%= paramSvc.getOnClick() %>"
                                      desabilitado='<%=(boolean)(!podeEditarSvc ? true : false)%>'
                 />
             <% campos.append("PSEVLR_").append(paramSvc.getCodigo()).append(";"); %>
          <% } else if (!paramSvc.isCustom() && paramSvc.isCombo()) { %>
          <div class="row">
            <div class="form-group col-sm-12 col-md-4 mt-1">
              <label for="<%= "PSEVLR_" + paramSvc.getCodigo() %>"><%= TextHelper.forHtmlContentComTags(paramSvc.getDescricao().replace("<br>", " ")) %></label>
              <% request.setAttribute("PSEVLR_" + paramSvc.getCodigo(), paramSvc.getComboValues()); %>
              <hl:htmlcombo listName="<%= "PSEVLR_" + paramSvc.getCodigo() %>"
                                name="<%= "PSEVLR_" + paramSvc.getCodigo() %>"
                                classe="form-control" 
                                fieldValue="<%= paramSvc.getCampoValor() %>" 
                                fieldLabel="<%= paramSvc.getCampoLabel() %>" 
                                notSelectedLabel="<%= paramSvc.getLabelNaoSelecionado() %>"
                                selectedValue="<%= paramSvc.getValor() %>"
                                disabled='<%=(boolean)(!podeEditarSvc ? true : false)%>'
                      />
            </div>
          </div>
            <% campos.append("PSEVLR_").append(paramSvc.getCodigo()).append(";"); %>
          <% } else { %>
          <%
             if (paramSvc.getCodigo().equals(CodedValues.TPS_INCIDE_MARGEM)) {
               MargemTO margemTO = null;
               List<MargemTO> margens = (List<MargemTO>) request.getAttribute("margens");
               Iterator<MargemTO> itMargens = (margens != null ? margens.iterator() : null);
          %>
          <div class="row">
          <% if (itMargens == null || !itMargens.hasNext()) { %>
            <div class="form-group col-sm-12 col-md-12 mt-1" role='radiogroup' aria-labelledby='PSEVLR_<%=(String)CodedValues.TPS_INCIDE_MARGEM%>'>
              <span id='PSEVLR_<%=(String)CodedValues.TPS_INCIDE_MARGEM%>'><hl:message key='rotulo.param.svc.contrato.incide.margem'/></span>
              <div class='form-check form-check-inline' >
                <INPUT TYPE="radio" class='form-check-input ml-1' NAME="PSEVLR_<%=(String)CodedValues.TPS_INCIDE_MARGEM%>" id="PSEVLR_<%=(String)CodedValues.TPS_INCIDE_MARGEM%>_0" VALUE="0" <%=(String)(hshParamSvcCse.get(CodedValues.TPS_INCIDE_MARGEM).equals("0")?"CHECKED":"")%> <%=(String)(!podeEditarSvc ? "disabled" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                <label class='form-check-label labelSemNegrito ml-1 pr-4 text-nowrap align-text-top' for='PSEVLR_<%=(String)CodedValues.TPS_INCIDE_MARGEM%>_0'><hl:message key='rotulo.param.svc.incide.margem.nao'/></label>
                </div>
                <div class='form-check form-check-inline'>
                <INPUT TYPE="radio" class='form-check-input ml-1' NAME="PSEVLR_<%=(String)CodedValues.TPS_INCIDE_MARGEM%>" id="PSEVLR_<%=(String)CodedValues.TPS_INCIDE_MARGEM%>_1" VALUE="1" <%=(String)(hshParamSvcCse.get(CodedValues.TPS_INCIDE_MARGEM).equals("1") || hshParamSvcCse.get(CodedValues.TPS_INCIDE_MARGEM).equals("") ?"CHECKED":"")%> <%=(String)(!podeEditarSvc ? "disabled" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                <label class='form-check-label labelSemNegrito ml-1 pr-4 text-nowrap align-text-top' for='PSEVLR_<%=(String)CodedValues.TPS_INCIDE_MARGEM%>_1'><hl:message key='rotulo.param.svc.incide.margem.1'/></label>
                </div>
                <div class='form-check form-check-inline'>
                <INPUT TYPE="radio" class='form-check-input ml-1' NAME="PSEVLR_<%=(String)CodedValues.TPS_INCIDE_MARGEM%>" id="PSEVLR_<%=(String)CodedValues.TPS_INCIDE_MARGEM%>_2" VALUE="2" <%=(String)(hshParamSvcCse.get(CodedValues.TPS_INCIDE_MARGEM).equals("2")?"CHECKED":"")%> <%=(String)(!podeEditarSvc ? "disabled" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                <label class='form-check-label labelSemNegrito ml-1 pr-4 text-nowrap align-text-top' for='PSEVLR_<%=(String)CodedValues.TPS_INCIDE_MARGEM%>_2'><hl:message key='rotulo.param.svc.incide.margem.2'/></label>
                </div>
                <div class='form-check form-check-inline'>
                <INPUT TYPE="radio" class='form-check-input ml-1' NAME="PSEVLR_<%=(String)CodedValues.TPS_INCIDE_MARGEM%>" id="PSEVLR_<%=(String)CodedValues.TPS_INCIDE_MARGEM%>_3" VALUE="3" <%=(String)(hshParamSvcCse.get(CodedValues.TPS_INCIDE_MARGEM).equals("3")?"CHECKED":"")%> <%=(String)(!podeEditarSvc ? "disabled" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                <label class='form-check-label labelSemNegrito ml-1 pr-4 text-nowrap align-text-top' for='PSEVLR_<%=(String)CodedValues.TPS_INCIDE_MARGEM%>_3'><hl:message key='rotulo.param.svc.incide.margem.3'/></label>
              </div>
            </div>
          <% } else { %>
            <div class="form-group col-sm-4 col-md-4 mt-1">
              <label for='PSEVLR_<%=(String)CodedValues.TPS_INCIDE_MARGEM%>'><hl:message key='rotulo.param.svc.contrato.incide.margem'/></label>
                <SELECT CLASS="form-control form-select col-sm-12 m-1" NAME="PSEVLR_<%=(String)CodedValues.TPS_INCIDE_MARGEM%>" id="PSEVLR_<%=(String)CodedValues.TPS_INCIDE_MARGEM%>" <%=(String)(!podeEditarSvc ? "disabled" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                  <% while (itMargens.hasNext()) { %>
                    <% margemTO = (MargemTO) itMargens.next(); %>
                    <OPTION VALUE="<%=TextHelper.forHtmlAttribute((margemTO.getMarCodigo()))%>" <%=(hshParamSvcCse.get(CodedValues.TPS_INCIDE_MARGEM).equals(margemTO.getMarCodigo().toString()) || (hshParamSvcCse.get(CodedValues.TPS_INCIDE_MARGEM).equals("") && margemTO.getMarCodigo().equals(CodedValues.INCIDE_MARGEM_SIM))) ? "SELECTED" : ""%>><%=TextHelper.forHtmlContent(margemTO.getMarDescricao())%></OPTION>
                  <% } %>
                </SELECT>
                </div>
          <% } %>
          </div>
            <% campos.append("PSEVLR_").append(CodedValues.TPS_INCIDE_MARGEM).append(";"); %>  
          <%} else if (paramSvc.getCodigo().equals(CodedValues.TPS_TIPO_VLR)) { %>
          <div class="row">
                <div class="form-group col-sm-4 col-md-4 mt-1">
                  <label for='PSEVLR_<%=(String)CodedValues.TPS_ADE_VLR%>'><hl:message key="rotulo.param.svc.valor.autorizacao"/> <%=TextHelper.forHtmlContent("(" + ParamSvcTO.getDescricaoTpsTipoVlr((String) hshParamSvcCse.get(CodedValues.TPS_TIPO_VLR)) + ")")%></label>
                  <% 
                  String ade_vlr = "";
                  if (hshParamSvcCse.get(CodedValues.TPS_ADE_VLR) != null && !hshParamSvcCse.get(CodedValues.TPS_ADE_VLR).equals("")) {
                      if (hshParamSvcCse.get(CodedValues.TPS_TIPO_VLR) != null && hshParamSvcCse.get(CodedValues.TPS_TIPO_VLR).equals(CodedValues.TIPO_VLR_PERCENTUAL)) {
                          ade_vlr = NumberHelper.reformat(hshParamSvcCse.get(CodedValues.TPS_ADE_VLR).toString(), "en", NumberHelper.getLang(), 2, 5); 
                      } else {
                          ade_vlr = NumberHelper.reformat(hshParamSvcCse.get(CodedValues.TPS_ADE_VLR).toString(), "en", NumberHelper.getLang()); 
                      }
                  }
                  %>
                  <INPUT class="form-control" NAME="PSEVLR_<%=(String)CodedValues.TPS_ADE_VLR%>" id="PSEVLR_<%=(String)CodedValues.TPS_ADE_VLR%>" TYPE="text" 
                  VALUE="<%=TextHelper.forHtmlAttribute(ade_vlr)%>" SIZE="12" <%=(String)(!podeEditarSvc ? "disabled" : "")%> 
                  onFocus="SetarEventoMascara(this,'#F12',true);" onBlur="fout(this);ValidaMascara(this); if (this.value != '') {if (f0.PSEVLR_<%=(String)(CodedValues.TPS_TIPO_VLR)%>.value == '<%=(String)(CodedValues.TIPO_VLR_PERCENTUAL)%>') { this.value = FormataContabil(parse_num(this.value), 5); } else {this.value = FormataContabil(parse_num(this.value), 2);}}"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.param.svc.valor.autorizacao", responsavel).toLowerCase()) %>">
                </div>
                <div class="form-group col-sm-4 col-md-4 mt-1">
                <label for='PSEVLR_<%=(String)(CodedValues.TPS_TIPO_VLR)%>'><hl:message key="rotulo.param.svc.valor.autorizacao.valor.titulo"/></label>
                  <SELECT CLASS="form-control form-select col-sm-12 m-1" NAME="PSEVLR_<%=(String)(CodedValues.TPS_TIPO_VLR)%>" id="PSEVLR_<%=(String)(CodedValues.TPS_TIPO_VLR)%>" <%=(String)(!podeEditarSvc ? "disabled" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this); if (f0.PSEVLR_<%=(String)CodedValues.TPS_ADE_VLR%>.value != '') { if (this.value == '<%=(String)(CodedValues.TIPO_VLR_PERCENTUAL)%>') { f0.PSEVLR_<%=(String)CodedValues.TPS_ADE_VLR%>.value = FormataContabil(parse_num(f0.PSEVLR_<%=(String)CodedValues.TPS_ADE_VLR%>.value), 5); } else {f0.PSEVLR_<%=(String)CodedValues.TPS_ADE_VLR%>.value = FormataContabil(parse_num(f0.PSEVLR_<%=(String)CodedValues.TPS_ADE_VLR%>.value), 2);}}" onChange="controlaCamposTipoVlrAde()">
                   <OPTION VALUE="<%=(String)(CodedValues.TIPO_VLR_FIXO)%>" <%=(String)((hshParamSvcCse.get(CodedValues.TPS_TIPO_VLR).equals("") || hshParamSvcCse.get(CodedValues.TPS_TIPO_VLR).equals(CodedValues.TIPO_VLR_FIXO))?"SELECTED":"")%>><hl:message key="rotulo.param.svc.valor.autorizacao.valor"/></OPTION>
                   <OPTION VALUE="<%=(String)(CodedValues.TIPO_VLR_PERCENTUAL)%>" <%=(String)(hshParamSvcCse.get(CodedValues.TPS_TIPO_VLR).equals(CodedValues.TIPO_VLR_PERCENTUAL)?"SELECTED":"")%>><hl:message key="rotulo.param.svc.valor.autorizacao.percentual"/></OPTION>
                   <OPTION VALUE="<%=(String)(CodedValues.TIPO_VLR_TOTAL_MARGEM)%>" <%=(String)(hshParamSvcCse.get(CodedValues.TPS_TIPO_VLR).equals(CodedValues.TIPO_VLR_TOTAL_MARGEM)?"SELECTED":"")%>><hl:message key="rotulo.param.svc.valor.autorizacao.total.margem"/></OPTION>
                   <OPTION VALUE="<%=(String)(CodedValues.TIPO_VLR_KILOGRAMAS)%>" <%=(String)(hshParamSvcCse.get(CodedValues.TPS_TIPO_VLR).equals(CodedValues.TIPO_VLR_KILOGRAMAS)?"SELECTED":"")%>><hl:message key="rotulo.param.svc.valor.autorizacao.kilogramas"/></OPTION>
                  </SELECT>
                  </div> 
                <div class="form-group col-sm-4 col-md-4 mt-1">
                <label for='PSEVLR_<%=(String)(CodedValues.TPS_ALTERA_ADE_VLR)%>'><hl:message key="rotulo.param.svc.valor.autorizacao.alteravel.titulo"/></label>
                  <SELECT CLASS="form-control form-select col-sm-12 m-1" NAME="PSEVLR_<%=(String)(CodedValues.TPS_ALTERA_ADE_VLR)%>" is="PSEVLR_<%=(String)(CodedValues.TPS_ALTERA_ADE_VLR)%>" <%=(String)(!podeEditarSvc ? "disabled" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                   <OPTION VALUE="1" <%=(String)(!hshParamSvcCse.get(CodedValues.TPS_ALTERA_ADE_VLR).equals("0")?"SELECTED":"")%>><hl:message key="rotulo.param.svc.valor.autorizacao.alteravel"/></OPTION>
                   <OPTION VALUE="0" <%=(String)(hshParamSvcCse.get(CodedValues.TPS_ALTERA_ADE_VLR).equals("0")?"SELECTED":"")%>><hl:message key="rotulo.param.svc.valor.autorizacao.pre.determinado"/></OPTION>
                  </SELECT>
                </div>
            <%  
                campos.append("PSEVLR_").append(CodedValues.TPS_TIPO_VLR).append(";");
                campos.append("PSEVLR_").append(CodedValues.TPS_ADE_VLR).append(";");
                campos.append("PSEVLR_").append(CodedValues.TPS_ALTERA_ADE_VLR).append(";");
            %>
          </div>
          <% } else if (paramSvc.getCodigo().equals(CodedValues.TPS_MAX_PRAZO)) { %>
          <div class="row">
            <div class="form-group col-sm-12 col-md-4 mt-1">
              <label for='PSEVLR_<%=(String)CodedValues.TPS_MAX_PRAZO%>'><hl:message key="rotulo.param.svc.qtde.parcelas"/></label>
              <%
                String strMaxPrazo = hshParamSvcCse.get(CodedValues.TPS_MAX_PRAZO).toString();
                int intMaxPrazo = (!strMaxPrazo.equals("")) ? Integer.parseInt(strMaxPrazo) : -1;
              %>
              <INPUT class="form-control" NAME="PSEVLR_<%=(String)CodedValues.TPS_MAX_PRAZO%>" id="PSEVLR_<%=(String)CodedValues.TPS_MAX_PRAZO%>" TYPE="text" 
              VALUE="<%=TextHelper.forHtmlAttribute(strMaxPrazo)%>" SIZE="12" onChange="f0.prazo.selectedIndex=1" <%=(String)(!podeEditarSvc ? "disabled" : "")%> 
              onFocus="SetarEventoMascara(this,'#D12',true);" onBlur="fout(this);ValidaMascara(this);"
              placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.param.svc.qtde.parcelas", responsavel).toLowerCase()) %>">
            </div>
            <div class="form-group col-sm-12 col-md-4 mt-1 align-text-bottom">
              <label for='PSEVLR_<%=(String)CodedValues.TPS_MAX_PRAZO%>'><hl:message key="rotulo.relatorio.transferencia.ade.prazo"/></label>
              <SELECT CLASS="form-control form-select col-sm-12 m-1" NAME="prazo" id="prazo" onChange="mudaPrazo()" <%=TextHelper.forHtmlContent((String)(!podeEditarSvc ? "disabled" : ""))%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                <OPTION VALUE="0" <%=(String)(intMaxPrazo  == 0 ? "SELECTED":"")%>><hl:message key="rotulo.param.svc.qtde.parcelas.indeterminado"/></OPTION>
                <OPTION VALUE="1" <%=(String)(intMaxPrazo   > 0 ? "SELECTED":"")%>><hl:message key="rotulo.param.svc.qtde.parcelas.limitado"/></OPTION>
                <OPTION VALUE="2" <%=(String)(intMaxPrazo == -1 ? "SELECTED":"")%>><hl:message key="rotulo.param.svc.qtde.parcelas.qualquer"/></OPTION>
              </SELECT>
              </div>
              <% campos.append("PSEVLR_").append(CodedValues.TPS_MAX_PRAZO).append(";"); %>
              <%-- Parametro Serviço que verifica se é fixo o prazo --%>
              <% if(hshParamSvcCse.containsKey(CodedValues.TPS_PRAZO_FIXO)) { %>
            <div class="form-group col-sm-12 col-md-4 mt-1">
              <label for='PSEVLR_<%=(String)CodedValues.TPS_PRAZO_FIXO%>'><hl:message key="rotulo.param.svc.qtde.parcelas.fixo"/></label>
            <br/>
              <div class='form-check form-check-inline'>
                <INPUT TYPE="radio" NAME="PSEVLR_<%=(String)CodedValues.TPS_PRAZO_FIXO%>" id="PSEVLR_<%=(String)CodedValues.TPS_PRAZO_FIXO%>_1" VALUE="1" <%=(String)( hshParamSvcCse.get(CodedValues.TPS_PRAZO_FIXO).equals("1") ? " checked" : "")%> <%=(String)(!podeEditarSvc ? " disabled" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                <label class="form-check-label labelSemNegrito ml-1 pr-4 text-nowrap align-text-top" for='PSEVLR_<%=(String)CodedValues.TPS_PRAZO_FIXO%>_1'><hl:message key="rotulo.sim"/></label>
                </div>
                <div class='form-check form-check-inline'>
                <INPUT TYPE="radio" NAME="PSEVLR_<%=(String)CodedValues.TPS_PRAZO_FIXO%>" id="PSEVLR_<%=(String)CodedValues.TPS_PRAZO_FIXO%>_0" VALUE="0" <%=(String)(!hshParamSvcCse.get(CodedValues.TPS_PRAZO_FIXO).equals("1") ? " checked" : "")%> <%=(String)(!podeEditarSvc ? " disabled" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                <label class="form-check-label labelSemNegrito ml-1 pr-4 text-nowrap align-text-top" for='PSEVLR_<%=(String)CodedValues.TPS_PRAZO_FIXO%>_0'><hl:message key="rotulo.nao"/></label>
              </div>
            </div>
              <% campos.append("PSEVLR_").append(CodedValues.TPS_PRAZO_FIXO).append(";"); %>
              <% } %>
          </div>
          <% } else if (paramSvc.getCodigo().equals(CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_PORTABILIDADE)) { %>
          <div class="row">
            <div class="form-group col-sm-12 col-md-4 mt-1">
               <label for='PSEVLR_<%=(String)CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_PORTABILIDADE%>'><hl:message key="rotulo.param.svc.qtde.parcelas.renegociacao"/></label>
                <%
                  String strMaxPrazoRenego = hshParamSvcCse.get(CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_PORTABILIDADE).toString();
                  int intMaxPrazoRenego = (!strMaxPrazoRenego.equals("")) ? Integer.parseInt(strMaxPrazoRenego) : -1;
                %>
                 <INPUT class="form-control" NAME="PSEVLR_<%=(String)CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_PORTABILIDADE%>" id="PSEVLR_<%=(String)CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_PORTABILIDADE%>" 
                 TYPE="text" VALUE="<%=TextHelper.forHtmlAttribute(strMaxPrazoRenego)%>" SIZE="12" onChange="f0.prazo.selectedIndex=1" <%=(String)(!podeEditarSvc ? "disabled" : "")%> 
                 onFocus="SetarEventoMascara(this,'#D12',true);" onBlur="fout(this);ValidaMascara(this);"
                 placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.param.svc.qtde.parcelas.renegociacao", responsavel).toLowerCase()) %>">
            </div>
              <%-- Se possui apenas param de max prazo de renegociação/portabilidade, opções de tipo de prazo se aplicam somente a este --%>
              <% if (!hshParamSvcCse.containsKey(CodedValues.TPS_MAX_PRAZO)) { %>
             <div class="form-group col-sm-12 col-md-4 mt-1 align-text-bottom align-bottom pt-3">
               <label for='PSEVLR_<%=(String)CodedValues.TPS_MAX_PRAZO%>'><hl:message key="rotulo.relatorio.transferencia.ade.prazo"/></label>
                <SELECT CLASS="form-control form-select col-sm-12 m-1" NAME="prazo" id="prazo" onChange="mudaPrazo()" <%=(String)(!podeEditarSvc ? "disabled" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                  <OPTION VALUE="0" <%=(String)(intMaxPrazoRenego  == 0 ? "SELECTED":"")%>><hl:message key="rotulo.param.svc.qtde.parcelas.indeterminado"/></OPTION>
                  <OPTION VALUE="1" <%=(String)(intMaxPrazoRenego   > 0 ? "SELECTED":"")%>><hl:message key="rotulo.param.svc.qtde.parcelas.limitado"/></OPTION>
                  <OPTION VALUE="2" <%=(String)(intMaxPrazoRenego == -1 ? "SELECTED":"")%>><hl:message key="rotulo.param.svc.qtde.parcelas.qualquer"/></OPTION>
                </SELECT>
             </div>
             <%-- Parametro Serviço que verifica se é fixo o prazo --%>
             <% if(hshParamSvcCse.containsKey(CodedValues.TPS_PRAZO_FIXO)) { %>
              <div class="form-group col-sm-12 col-md-4 mt-1 align-bottom">
                <label for='PSEVLR_<%=(String)CodedValues.TPS_PRAZO_FIXO%>'><hl:message key="rotulo.param.svc.qtde.parcelas.fixo"/></label>
                <div class='form-check form-check-inline'>
                  <INPUT TYPE="radio" NAME="PSEVLR_<%=(String)CodedValues.TPS_PRAZO_FIXO%>" id="PSEVLR_<%=(String)CodedValues.TPS_PRAZO_FIXO%>_1" VALUE="1" <%=(String)( hshParamSvcCse.get(CodedValues.TPS_PRAZO_FIXO).equals("1") ? " checked" : "")%> <%=(String)(!podeEditarSvc ? " disabled" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                  <label class="form-check-label labelSemNegrito ml-1 pr-4 text-nowrap align-text-top" for='PSEVLR_<%=(String)CodedValues.TPS_PRAZO_FIXO%>_1'><hl:message key="rotulo.sim"/></label>
                  </div>
                  <div class='form-check form-check-inline'>
                  <INPUT TYPE="radio" NAME="PSEVLR_<%=(String)CodedValues.TPS_PRAZO_FIXO%>" id="PSEVLR_<%=(String)CodedValues.TPS_PRAZO_FIXO%>_0" VALUE="0" <%=(String)(!hshParamSvcCse.get(CodedValues.TPS_PRAZO_FIXO).equals("1") ? " checked" : "")%> <%=(String)(!podeEditarSvc ? " disabled" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                  <label class="form-check-label labelSemNegrito ml-1 pr-4 text-nowrap align-text-top" for='PSEVLR_<%=(String)CodedValues.TPS_PRAZO_FIXO%>_0'><hl:message key="rotulo.nao"/></label>
                  <% campos.append("PSEVLR_").append(CodedValues.TPS_PRAZO_FIXO).append(";"); %>
                </div>
              </div>
              <% } %>
          <% } %>            
          </div>
          <% campos.append("PSEVLR_").append(CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_PORTABILIDADE).append(";"); %>
          <% } else if (paramSvc.getCodigo().equals("svcRelacionamentoCorrecao")) { %>
          <div class="row">
            <div class="form-group col-sm-12 col-md-4 mt-1">
              <label for='svcRelacionamentoCorrecao'><hl:message key="rotulo.servico.relacionado.correcao.saldo.devedor"/></label>
                <SELECT class="form-control form-select col-sm-12 m-1" NAME="svcRelacionamentoCorrecao" id="svcRelacionamentoCorrecao" <%=(String)(!podeEditarSvc ? "disabled" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                  <OPTION VALUE=""><hl:message key="rotulo.campo.servico"/></OPTION>
                  <%
                   List<?> svcRelacionamentoCorrecao = (List<?>) request.getAttribute("svcRelacionamentoCorrecao");
                   if (svcRelacionamentoCorrecao != null && svcRelacionamentoCorrecao.size() > 0) {
                     Iterator<?> itSvcCorrecao = svcRelacionamentoCorrecao.iterator();
                     CustomTransferObject cto = null;
                     while(itSvcCorrecao.hasNext()) {
                       cto = (CustomTransferObject) itSvcCorrecao.next();
                       %>
                       <OPTION VALUE="<%=TextHelper.forHtmlAttribute((cto.getAttribute(Columns.SVC_CODIGO)))%>" <%=TextHelper.forHtml((cto.getAttribute("SELECTED")))%>><%=TextHelper.forHtml((cto.getAttribute(Columns.SVC_IDENTIFICADOR)))%> - <%=TextHelper.forHtml((cto.getAttribute(Columns.SVC_DESCRICAO)))%></OPTION>
                       <%
                       }
                     }
                  %>
                </SELECT>
            </div>
          </div>
          <% } %>
        <% } %>
      <% } %>
         </div>
      </div>
      <%-- FIM DOS PARÂMETROS DO SERVIÇO --%>
    <% } %>
  <% } %>
  <% if (podeEditarSvc) { %>
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.servico.copiar.parametros.titulo"/></h2>
        </div>
        <div class="card-body">
          <div class="row">
            <div class="form-group col-sm-12 col-md-4 mt-1">
              <label for="copia_para_svc_corrente"><hl:message key="rotulo.servico.copiar.configuracao"/></label>
                <%
                List<?> servicos = (List<?>) request.getAttribute("servicos");
                request.setAttribute("servicos", servicos);
                %>
                <hl:htmlcombo listName="servicos" 
                              name="copia_para_svc_corrente"
                              di="copia_para_svc_corrente" 
                              classe="form-control"
                              fieldValue="<%=TextHelper.forHtmlAttribute( Columns.SVC_CODIGO )%>" 
                              fieldLabel="<%=(String)(Columns.SVC_IDENTIFICADOR + ";" + Columns.SVC_DESCRICAO)%>" 
                              notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel) %>"
                    />
            </div>
            <div class="form-group col-sm-12 col-md-6 mt-1">
              <label for="copia_svc_corrente"><hl:message key="rotulo.servico.aplicar.configuracao"/></label>
                <hl:htmlcombo listName="servicos"
                              name="copia_svc_corrente"
                              di="copia_svc_corrente"
                              classe="form-control" 
                              fieldValue="<%=TextHelper.forHtmlAttribute( Columns.SVC_CODIGO )%>" 
                              fieldLabel="<%=(String)(Columns.SVC_IDENTIFICADOR + ";" + Columns.SVC_DESCRICAO)%>" 
                              size="4"
                />
              <div class='slider mt-2 col-sm-12 col-md-12 pl-0 pr-0'>
                <div class='tooltip-inner'><hl:message key="mensagem.utilize.crtl"/></div>
                <div class='btn-action float-end mt-3'>
                <a class='btn btn-outline-danger' href='#' onclick="desmarcarSelecao('copia_svc_corrente')"><hl:message key="mensagem.limpar.selecao"/></a>
                </div>
              </div>
                
            </div>
          </div> 
        </div>
      </div>
          <input name="acao" type="hidden" value="salvarServico">
          <input name="svc" type="hidden" value="<%=TextHelper.forHtmlAttribute(svc_codigo)%>">
          <input name="salvar" type="hidden" value="true">
          <input name="codigo" type="hidden" value="<%=TextHelper.forHtmlAttribute(svc_codigo)%>">
          <input name="PCV_CAMPOS" type="hidden" value="<%=TextHelper.forHtmlAttribute((pcv_campos))%>">
          <input name="PSE_CAMPOS" type="hidden" value="<%=TextHelper.forHtmlAttribute((campos))%>">
          <input name="recarregarNseCodigo" type="hidden">
  <% } %>      
  </form>
    <div id="actions" class="btn-action">
<%if (podeEditarSvc && lstOcorrencias == null ) {%>
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
      <a class="btn btn-primary" HREF="#no-back" onClick="if(validaForm()){f0.submit();};return false;"><hl:message key="rotulo.botao.salvar"/></a>
<%} else if (!podeEditarSvc && lstOcorrencias == null){ %>
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
<%} else if (podeEditarSvc && (lstOcorrencias != null && !lstOcorrencias.isEmpty()) ){%>
      <a class="btn btn-primary" HREF="#no-back" onClick="if(validaForm()){f0.submit();};return false;"><hl:message key="rotulo.botao.salvar"/></a>
<%} %>
    </div>
<!-- lista de ocorrências referentes à edição do serviço -->
<% if (lstOcorrencias != null && !lstOcorrencias.isEmpty()) { %>
      <div class="card">
        <div class="card-header hasIcon pl-3">
          <h2 class="card-header-title"><hl:message key="rotulo.ocorrencia.svc.titulo"/></h2>
        </div>
        <div class="card-body table-responsive p-0">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th scope="col"><hl:message key="rotulo.ocorrencia.svc.data"/></th>
                <th scope="col"><hl:message key="rotulo.ocorrencia.svc.responsavel"/></th>
                <th scope="col"><hl:message key="rotulo.ocorrencia.svc.tipo"/></th>
                <th scope="col"><hl:message key="rotulo.ocorrencia.svc.descricao"/></th>
                <th scope="col"><hl:message key="rotulo.ocorrencia.svc.ip.acesso"/></th>
              </tr>
            </thead>
            <tbody>
            <%
            int i = 0;
            Iterator<TransferObject> itHistorico = lstOcorrencias.iterator();
            while (itHistorico.hasNext()) { 
                TransferObject cto = itHistorico.next();
                String oseData = DateHelper.toDateTimeString((Date) cto.getAttribute(Columns.OSE_DATA));
      
                String loginOseResponsavel = cto.getAttribute(Columns.USU_LOGIN) != null ? cto.getAttribute(Columns.USU_LOGIN).toString() : "";
                String oseResponsavel = (loginOseResponsavel.equalsIgnoreCase((String) cto.getAttribute(Columns.USU_CODIGO)) && cto.getAttribute(Columns.USU_TIPO_BLOQ) != null) ? 
                                        cto.getAttribute(Columns.USU_TIPO_BLOQ).toString() + "(*)" : loginOseResponsavel;
                String oseTipo = cto.getAttribute(Columns.TOC_DESCRICAO).toString();
                String oseObs = cto.getAttribute(Columns.OSE_OBS).toString();
                String oseIpAcesso = cto.getAttribute(Columns.OSE_IP_ACESSO) != null ? cto.getAttribute(Columns.OSE_IP_ACESSO).toString() : "";
                String tmoDescricao = cto.getAttribute(Columns.TMO_DESCRICAO) != null ? cto.getAttribute(Columns.TMO_DESCRICAO).toString() : "";
                if (!TextHelper.isNull(tmoDescricao)) {
                    oseObs += " " + ApplicationResourcesHelper.getMessage("rotulo.motivo.arg0", responsavel, tmoDescricao);
                }
            %>
              <tr>
                <td><%=TextHelper.forHtmlContent(oseData)%></td>
                <td><%=TextHelper.forHtmlContent(oseResponsavel)%></td>
                <td><%=TextHelper.forHtmlContent(oseTipo)%></td>
                <td><%=TextHelper.forHtmlContent(oseObs)%></td>
                <td><%=TextHelper.forHtmlContent(oseIpAcesso)%></td>
              </tr>
      <%  } %>
            </tbody>
            <tfoot>
              <tr>
                <td colspan="5">
                  <hl:message key="rotulo.listagem.ocorrencia.editar.servico"/>
                  <span class="font-italic"> - 
                    <hl:message key="rotulo.paginacao.registros.sem.estilo" arg0="${_paginacaoPrimeiro}" arg1="${_paginacaoUltimo}" arg2="${_paginacaoQtdTotal}"/>
                  </span>
                </td>
              </tr>
            </tfoot>
          </table>
        </div>
        <div class="card-footer"><%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %></div>
      </div>
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
  </div> 
<% } %>
  <%if (exibeBotaoRodape) { %>
	<div id="btns">
	  <a id="page-up" onclick="up()">
        <svg xmlns="http://www.w3.org/2000/svg" width="147.344" height="147.344" viewBox="0 0 147.344 147.344">
		  <path id="União_3" data-name="União 3" d="M-20,60.672a73.672,73.672,0,1,1,73.672,73.672A73.66,73.66,0,0,1-20,60.672ZM9.61,16.61a62.252,62.252,0,0,0,0,88.124,62.252,62.252,0,0,0,88.124,0,62.252,62.252,0,0,0,0-88.124,62.252,62.252,0,0,0-88.124,0ZM49.174,88.087,15.278,54.3a6.85,6.85,0,0,1,9.67-9.706L53.672,73.6,82.362,45.336A6.825,6.825,0,1,1,92,55.005L58.6,88.3a6.827,6.827,0,0,1-9.421-.212Z" transform="translate(20 13)"/>
		</svg>              
	  </a>
	  <a id="page-down" onclick="down()">
        <svg xmlns="http://www.w3.org/2000/svg" width="147.344" height="147.344" viewBox="0 0 147.344 147.344">
		  <path id="União_3" data-name="União 3" d="M-20,60.672a73.672,73.672,0,1,1,73.672,73.672A73.66,73.66,0,0,1-20,60.672ZM9.61,16.61a62.252,62.252,0,0,0,0,88.124,62.252,62.252,0,0,0,88.124,0,62.252,62.252,0,0,0,0-88.124,62.252,62.252,0,0,0-88.124,0ZM49.174,88.087,15.278,54.3a6.85,6.85,0,0,1,9.67-9.706L53.672,73.6,82.362,45.336A6.825,6.825,0,1,1,92,55.005L58.6,88.3a6.827,6.827,0,0,1-9.421-.212Z" transform="translate(20 13)"/>
		</svg>
	  </a>
	  <a id="page-actions" onclick="toActionBtns()">
		<svg xmlns="http://www.w3.org/2000/svg" width="145.344" height="145.344" viewBox="0 0 145.344 145.344">
		  <path id="União_1" data-name="União 1" d="M-20,59.672a72.672,72.672,0,1,1,72.671,72.672A72.671,72.671,0,0,1-20,59.672Zm10.164,0A62.508,62.508,0,1,0,52.672-2.836,62.579,62.579,0,0,0-9.836,59.672Zm82.6,40.182H24.545A12.069,12.069,0,0,1,12.49,87.8V31.544A12.069,12.069,0,0,1,24.545,19.49h44.2a4.014,4.014,0,0,1,2.841,1.177L91.678,40.757A4.019,4.019,0,0,1,92.855,43.6V87.8A12.069,12.069,0,0,1,80.8,99.854Zm0-40.182a4.018,4.018,0,0,1,4.019,4.018V91.817H80.8A4.023,4.023,0,0,0,84.818,87.8V45.263L67.081,27.526H36.6V39.58H64.727a4.019,4.019,0,0,1,0,8.037H32.581A4.018,4.018,0,0,1,28.563,43.6V27.526H24.545a4.023,4.023,0,0,0-4.018,4.019V87.8a4.023,4.023,0,0,0,4.018,4.018h4.019V63.689a4.018,4.018,0,0,1,4.018-4.018ZM36.6,91.817H68.745V67.708H36.6Z" transform="translate(20 13)"/>
		</svg>
	  </a>
	</div> 
  <% } %>
</c:set>
<c:set var="javascript">
   <script type="text/JavaScript" src="../js/scripts_2810.js"></script>
   <script type="text/JavaScript" src="../js/validaform.js"></script>
   <script type="text/JavaScript" src="../js/validacoes.js"></script>
   <script type="text/JavaScript" src="../js/xbdhtml.js"></script>
  <script type="text/JavaScript">
    var f0 = document.forms[0];

    function desmarcarSelecao(elementId) {
      var elt = document.getElementById(elementId);
      elt.style.backgroundColor = "white";
      elt.selectedIndex = -1;
      elt.focus();
  	  return true;
    }    
    
    function formLoad() {
      <% if (podeEditarSvc) { %>
          focusFirstField();
          validaMaxPrazo();
          controlaCamposSaldoDevedor();
          controlaCamposSvcCompulsorio();
          controlaCamposCorrecaoVlrPresente();
          controlaCamposTipoVlrAde();
      <% } %>
    }

    function validaForm() {
      validaRenegociacao();
      <% if (validaTaxaRenegociacao) { %>
        if (document.getElementById('PSEVLR_' + '<%= CodedValues.TPS_VLR_PERC_MAXIMO_PARCELA_RENEGOCIACAO %>').value >= 100) {
          alert('<hl:message key="rotulo.param.svc.compra.percentual.valor.max.nova.parcela.maior.cem"/>');
          document.getElementById('PSEVLR_' + '<%= CodedValues.TPS_VLR_PERC_MAXIMO_PARCELA_RENEGOCIACAO %>').focus();
          return false;
        }
      <% } %>
      <% if (validaTaxaPortabilidade) { %>
        if (document.getElementById('PSEVLR_' + '<%= CodedValues.TPS_VLR_PERC_MAXIMO_PARCELA_PORTABILIDADE %>').value >= 100) {
          alert('<hl:message key="rotulo.param.svc.compra.percentual.valor.max.nova.parcela.maior.cem"/>');
          document.getElementById('PSEVLR_' + '<%= CodedValues.TPS_VLR_PERC_MAXIMO_PARCELA_PORTABILIDADE %>').focus();
          return false;
        }
      <% } %>      
            
      if (validaCamposSaldoDevedor() && validaCamposCorrecaoVlrPresente() && validaCamposMaxPrazo() && validaCalculoSubsidio() && validaDiasVigenciaCET()) {
        habilitarTodosCampos();
        return true;
      } else {
        return false;
      }
    }
    
    function validaDiasVigenciaCET() {
    	diasVigenciaCet = document.getElementById('PSEVLR_' + '<%=CodedValues.TPS_DIAS_VIGENCIA_CET%>');
        if ((diasVigenciaCet != undefined) && (diasVigenciaCet.value != undefined && diasVigenciaCet.value != "")) {
      	  if (diasVigenciaCet.value <= 0) {
      		  alert('<hl:message key="mensagem.erro.dias.vigencia.cet.negativo.ou.zero"/>');
      		  diasVigenciaCet.value = "";
                document.getElementById('PSEVLR_' + '<%=CodedValues.TPS_DIAS_VIGENCIA_CET%>').focus();
                return false;
      	  }
        }
        return true;
    }

    function setHidden(campo, valor) {
      campo.value = valor;
    }
      
    function submitRecarregarNseCodigo() {
   		f0.salvar.remove();
   		f0.recarregarNseCodigo.value = "true";
   		f0.acao.value = 'recarregarNseCodigo'
  		f0.submit();
    }

    function habilitarTodosCampos() {
      habilitaCamposSaldoDevedor();
      habilitaCamposSvcCompulsorio();
      habilitaCamposCorrecaoVlrPresente();
      habilitaCamposTipoVlrAde();
    }
  
    function mudaPrazo() {
      if(<%=(boolean)(hshParamSvcCse.containsKey(CodedValues.TPS_MAX_PRAZO))%>){
        if (f0.prazo.selectedIndex == 0) {
          f0.PSEVLR_<%=(String)(CodedValues.TPS_MAX_PRAZO)%>.value = '0';
          f0.PSEVLR_<%=(String)(CodedValues.TPS_PRAZO_FIXO)%>[1].checked = true;
          f0.PSEVLR_<%=(String)(CodedValues.TPS_PRAZO_FIXO)%>[0].disabled = true;
        } else if (f0.prazo.selectedIndex == 2) {
          f0.PSEVLR_<%=(String)(CodedValues.TPS_MAX_PRAZO)%>.value = '';
          f0.PSEVLR_<%=(String)(CodedValues.TPS_PRAZO_FIXO)%>[1].checked = true;
          f0.PSEVLR_<%=(String)(CodedValues.TPS_PRAZO_FIXO)%>[0].disabled = true;
          f0.PSEVLR_<%=(String)(CodedValues.TPS_PRAZO_FIXO)%>.VLR_INI_VCO = '0';
        } else if (f0.prazo.selectedIndex == 1) {
          f0.PSEVLR_<%=(String)(CodedValues.TPS_PRAZO_FIXO)%>[0].disabled = false;
        }
      }
      
      if(<%=(boolean)(hshParamSvcCse.containsKey(CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_PORTABILIDADE))%>){
        if (f0.prazo.selectedIndex == 0) {
          f0.PSEVLR_<%=(String)(CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_PORTABILIDADE)%>.value = '0';
          f0.PSEVLR_<%=(String)(CodedValues.TPS_PRAZO_FIXO)%>[1].checked = true;
          f0.PSEVLR_<%=(String)(CodedValues.TPS_PRAZO_FIXO)%>[0].disabled = true;
        } else if (f0.prazo.selectedIndex == 2) {
          f0.PSEVLR_<%=(String)(CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_PORTABILIDADE)%>.value = '';
          f0.PSEVLR_<%=(String)(CodedValues.TPS_PRAZO_FIXO)%>[1].checked = true;
          f0.PSEVLR_<%=(String)(CodedValues.TPS_PRAZO_FIXO)%>[0].disabled = true;
          f0.PSEVLR_<%=(String)(CodedValues.TPS_PRAZO_FIXO)%>.VLR_INI_VCO = '0';
        } else if (f0.prazo.selectedIndex == 1) {
          f0.PSEVLR_<%=(String)(CodedValues.TPS_PRAZO_FIXO)%>[0].disabled = false;
        }
      }
    }
    
    function validaMaxPrazo() {
      if(<%=(boolean)(hshParamSvcCse.containsKey(CodedValues.TPS_MAX_PRAZO))%>){
        if (f0.prazo.selectedIndex == 0) {
         f0.PSEVLR_<%=(String)(CodedValues.TPS_MAX_PRAZO)%>.value = '0';
         f0.PSEVLR_<%=(String)(CodedValues.TPS_PRAZO_FIXO)%>[1].checked = true;
         f0.PSEVLR_<%=(String)(CodedValues.TPS_PRAZO_FIXO)%>[0].disabled = true;
        } else if (f0.prazo.selectedIndex == 2) {
           f0.PSEVLR_<%=(String)(CodedValues.TPS_MAX_PRAZO)%>.value = '';
           f0.PSEVLR_<%=(String)(CodedValues.TPS_PRAZO_FIXO)%>[1].checked = true;
           f0.PSEVLR_<%=(String)(CodedValues.TPS_PRAZO_FIXO)%>[0].disabled = true;
           f0.PSEVLR_<%=(String)(CodedValues.TPS_PRAZO_FIXO)%>.VLR_INI_VCO = '0';
        } else if (f0.prazo.selectedIndex == 1) {
           f0.PSEVLR_<%=(String)(CodedValues.TPS_PRAZO_FIXO)%>[0].disabled = false;
        }
      }
    }
    
    function validaRenegociacao() {
     if(f0.PSEVLR_<%=(String)(CodedValues.TPS_PERMITE_RENEGOCIACAO)%> != null  &&
        f0.PSEVLR_<%=(String)(CodedValues.TPS_PERMITE_RENEGOCIACAO)%>[1].checked) {
        f0.PSEVLR_<%=(String)(CodedValues.TPS_QTDE_MAX_ADE_RENEGOCIACAO)%>.value = '';
     }
     return true;
    }
    
    function validaCamposSaldoDevedor() {
     if ((f0.PSEVLR_<%=(String)(CodedValues.TPS_CONTROLA_SALDO)%> != null) &&
         (getCheckedRadio("form1", "PSEVLR_<%=(String)(CodedValues.TPS_CONTROLA_SALDO)%>") == '<%=(String)(CodedValues.POSSUI_CONTROLE_SALDO_DEVEDOR)%>') &&
         (getCheckedRadio("form1", "PSEVLR_<%=(String)(CodedValues.TPS_POSSUI_CORRECAO_SALDO_DEVEDOR)%>") == '<%=(String)(CodedValues.CORRECAO_SALDO_DEVEDOR_EM_OUTRO_SERVICO)%>') &&
         (f0.svcRelacionamentoCorrecao.value == '')) {
         alert('<hl:message key="mensagem.aviso.correcao.saldo.devedor.exige.servico.correcao"/>');
         f0.svcRelacionamentoCorrecao.focus();
         return false;
     }
     if ((f0.PSEVLR_<%=(String)(CodedValues.TPS_CONTROLA_SALDO)%> != null) &&
         (getCheckedRadio("form1", "PSEVLR_<%=(String)(CodedValues.TPS_CONTROLA_SALDO)%>") == '<%=(String)(CodedValues.POSSUI_CONTROLE_SALDO_DEVEDOR)%>') &&
         (f0.PSEVLR_<%=(String)(CodedValues.TPS_CONTROLA_VLR_MAX_DESCONTO)%> != null) &&
         (getCheckedRadio("form1", "PSEVLR_<%=(String)(CodedValues.TPS_CONTROLA_VLR_MAX_DESCONTO)%>") != '<%=(String)(CodedValues.CONTROLA_VLR_MAX_DESCONTO_PELA_PARCELA)%>') &&
         (f0.PSEVLR_<%=(String)(CodedValues.TPS_MAX_PRAZO)%>.value != 1)) {
         alert('<hl:message key="mensagem.aviso.saldo.devedor.sem.controle.valor.maximo.prazo.permitido"/>');
         f0.PSEVLR_<%=(String)(CodedValues.TPS_MAX_PRAZO)%>.focus();
         return false;
     }
     return true;
    }
    
    function validaCamposMaxPrazo() {
    	if ((f0.PSEVLR_<%=(String)(CodedValues.TPS_MAX_PRAZO)%> != null && f0.PSEVLR_<%=(String)(CodedValues.TPS_MAX_PRAZO)%>.value != '') && (f0.PSEVLR_<%=(String)(CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_PORTABILIDADE)%> != null && f0.PSEVLR_<%=(String)(CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_PORTABILIDADE)%>.value != '')) {
    		if ((f0.PSEVLR_<%=(String)(CodedValues.TPS_MAX_PRAZO)%>.value > 0 && f0.PSEVLR_<%=(String)(CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_PORTABILIDADE)%>.value <= 0)
    			 || (f0.PSEVLR_<%=(String)(CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_PORTABILIDADE)%>.value > 0 && f0.PSEVLR_<%=(String)(CodedValues.TPS_MAX_PRAZO)%>.value <= 0)) {
    			alert('<hl:message key="mensagem.aviso.maxprazo.insercao.renegociacao.inconsistentes"/>');
    		     f0.PSEVLR_<%=(String)(CodedValues.TPS_MAX_PRAZO)%>.focus();
    		     return false;
    		}
    	}
    	
    	if ((f0.PSEVLR_<%=(String)(CodedValues.TPS_MAX_PRAZO)%> == null || f0.PSEVLR_<%=(String)(CodedValues.TPS_MAX_PRAZO)%>.value == '') && 
    		(f0.PSEVLR_<%=(String)(CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_PORTABILIDADE)%> != null && f0.PSEVLR_<%=(String)(CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_PORTABILIDADE)%>.value != '')) {
    		alert('<hl:message key="mensagem.servico.maxprazo.insercao.nao.preenchido"/>');
    	     f0.PSEVLR_<%=(String)(CodedValues.TPS_MAX_PRAZO)%>.focus();
    	     return false;
    	}
    	
    	return true;
    }
    
    function habilitaCamposSaldoDevedor() {
     <% if (hshParamSvcCse.containsKey(CodedValues.TPS_POSSUI_CORRECAO_SALDO_DEVEDOR)) { %>
       disableRadioButton("form1", "PSEVLR_<%=(String)(CodedValues.TPS_POSSUI_CORRECAO_SALDO_DEVEDOR)%>", false);
       f0.svcRelacionamentoCorrecao.disabled = false;
     <% } %>
     <% if (hshParamSvcCse.containsKey(CodedValues.TPS_FORMA_CALCULO_CORRECAO_SALDO_DV)) { %>
       f0.PSEVLR_<%=(String)(CodedValues.TPS_FORMA_CALCULO_CORRECAO_SALDO_DV)%>.disabled = false;
     <% } %>
     <% if (hshParamSvcCse.containsKey(CodedValues.TPS_CORRECAO_SOBRE_TOTAL_SALDO_DV)) { %>
       disableRadioButton("form1", "PSEVLR_<%=(String)(CodedValues.TPS_CORRECAO_SOBRE_TOTAL_SALDO_DV)%>", false);
     <% } %>
     <% if (hshParamSvcCse.containsKey(CodedValues.TPS_CORRECAO_ENVIADA_APOS_PRINCIPAL)) { %>
       disableRadioButton("form1", "PSEVLR_<%=(String)(CodedValues.TPS_CORRECAO_ENVIADA_APOS_PRINCIPAL)%>", false);
     <% } %>
    }
    
    function controlaCamposSaldoDevedor() {
     if (f0.PSEVLR_<%=(String)(CodedValues.TPS_CONTROLA_SALDO)%> != null) {
       // Desabilita os campos de correção de saldo devedor
       <% if (hshParamSvcCse.containsKey(CodedValues.TPS_POSSUI_CORRECAO_SALDO_DEVEDOR)) { %>
         disableRadioButton("form1", "PSEVLR_<%=(String)(CodedValues.TPS_POSSUI_CORRECAO_SALDO_DEVEDOR)%>", true);
         f0.svcRelacionamentoCorrecao.disabled = true;
       <% } %>
       <% if (hshParamSvcCse.containsKey(CodedValues.TPS_FORMA_CALCULO_CORRECAO_SALDO_DV)) { %>
         f0.PSEVLR_<%=(String)(CodedValues.TPS_FORMA_CALCULO_CORRECAO_SALDO_DV)%>.disabled = true;
       <% } %>
       <% if (hshParamSvcCse.containsKey(CodedValues.TPS_CORRECAO_SOBRE_TOTAL_SALDO_DV)) { %>
         disableRadioButton("form1", "PSEVLR_<%=(String)(CodedValues.TPS_CORRECAO_SOBRE_TOTAL_SALDO_DV)%>", true);
       <% } %>
       <% if (hshParamSvcCse.containsKey(CodedValues.TPS_CORRECAO_ENVIADA_APOS_PRINCIPAL)) { %>
         disableRadioButton("form1", "PSEVLR_<%=(String)(CodedValues.TPS_CORRECAO_ENVIADA_APOS_PRINCIPAL)%>", true);
       <% } %>
    
       if (getCheckedRadio("form1", "PSEVLR_<%=(String)(CodedValues.TPS_CONTROLA_SALDO)%>") == '<%=(String)(CodedValues.NAO_POSSUI_CONTROLE_SALDO_DEVEDOR)%>') {
         // Se não tem controle de saldo devedor, então deixa os campos desabilitados
         // e desmarca correção de saldo devedor
         <% if (hshParamSvcCse.containsKey(CodedValues.TPS_POSSUI_CORRECAO_SALDO_DEVEDOR)) { %>
           setCheckedRadio("form1", "PSEVLR_<%=(String)(CodedValues.TPS_POSSUI_CORRECAO_SALDO_DEVEDOR)%>", "<%=(String)(CodedValues.NAO_POSSUI_CORRECAO_SALDO_DEVEDOR)%>");
           setCheckedRadio("form1", "PSEVLR_<%=(String)(CodedValues.TPS_CORRECAO_SOBRE_TOTAL_SALDO_DV)%>", "<%=(String)(CodedValues.CORRECAO_SOBRE_SALDO_PARCELAS)%>");
           setCheckedRadio("form1", "PSEVLR_<%=(String)(CodedValues.TPS_CORRECAO_ENVIADA_APOS_PRINCIPAL)%>", "<%=(String)(CodedValues.CORRECAO_ENVIADA_JUNTO_PRINCIPAL)%>");
           f0.PSEVLR_<%=(String)(CodedValues.TPS_FORMA_CALCULO_CORRECAO_SALDO_DV)%>.value = '';
           f0.svcRelacionamentoCorrecao.value = '';
         <% } %>
    
       } else {
         // Se tem controle de saldo, mas o controle de teto não é pela parcela,
         // então limita o prazo do contrato para 1 mês
         if (f0.PSEVLR_<%=(String)(CodedValues.TPS_CONTROLA_VLR_MAX_DESCONTO)%> != null) {
           if (getCheckedRadio("form1", "PSEVLR_<%=(String)(CodedValues.TPS_CONTROLA_VLR_MAX_DESCONTO)%>") != '<%=(String)(CodedValues.CONTROLA_VLR_MAX_DESCONTO_PELA_PARCELA)%>') {
             f0.PSEVLR_<%=(String)(CodedValues.TPS_MAX_PRAZO)%>.value  = 1;
             setCheckedRadio("form1", "PSEVLR_<%=(String)(CodedValues.TPS_PRAZO_FIXO)%>", "1");
             f0.prazo.value = 1;
             mudaPrazo();
           } else {
             f0.PSEVLR_<%=(String)(CodedValues.TPS_MAX_PRAZO)%>.value  = 99;
             setCheckedRadio("form1", "PSEVLR_<%=(String)(CodedValues.TPS_PRAZO_FIXO)%>", "0");
             f0.prazo.value = 1;
           }
         }
    
         // Se tem controle de saldo devedor, então habilita campos
         // de correção de saldo devedor
         <% if (hshParamSvcCse.containsKey(CodedValues.TPS_POSSUI_CORRECAO_SALDO_DEVEDOR)) { %>
           disableRadioButton("form1", "PSEVLR_<%=(String)(CodedValues.TPS_POSSUI_CORRECAO_SALDO_DEVEDOR)%>", false);
    
           if (getCheckedRadio("form1", "PSEVLR_<%=(String)(CodedValues.TPS_POSSUI_CORRECAO_SALDO_DEVEDOR)%>") != '<%=(String)(CodedValues.NAO_POSSUI_CORRECAO_SALDO_DEVEDOR)%>') {
             // Serviço possui correção
             <% if (hshParamSvcCse.containsKey(CodedValues.TPS_FORMA_CALCULO_CORRECAO_SALDO_DV)) { %>
               f0.PSEVLR_<%=(String)(CodedValues.TPS_FORMA_CALCULO_CORRECAO_SALDO_DV)%>.disabled = false;
             <% } %>
             <% if (hshParamSvcCse.containsKey(CodedValues.TPS_CORRECAO_SOBRE_TOTAL_SALDO_DV)) { %>
               disableRadioButton("form1", "PSEVLR_<%=(String)(CodedValues.TPS_CORRECAO_SOBRE_TOTAL_SALDO_DV)%>", false);
             <% } %>
    
             if (getCheckedRadio("form1", "PSEVLR_<%=(String)(CodedValues.TPS_POSSUI_CORRECAO_SALDO_DEVEDOR)%>") == '<%=(String)(CodedValues.CORRECAO_SALDO_DEVEDOR_EM_OUTRO_SERVICO)%>') {
               // Serviço possui correção em outro serviço
               f0.svcRelacionamentoCorrecao.disabled = false;
               <% if (hshParamSvcCse.containsKey(CodedValues.TPS_CORRECAO_ENVIADA_APOS_PRINCIPAL)) { %>
                 disableRadioButton("form1", "PSEVLR_<%=(String)(CodedValues.TPS_CORRECAO_ENVIADA_APOS_PRINCIPAL)%>", false);
               <% } %>
             } else {
               setCheckedRadio("form1", "PSEVLR_<%=(String)(CodedValues.TPS_CORRECAO_ENVIADA_APOS_PRINCIPAL)%>", "<%=(String)(CodedValues.CORRECAO_ENVIADA_JUNTO_PRINCIPAL)%>");
             }
           }
         <% } %>
       }
     }
    }
    
    function controlaCamposSvcCompulsorio() {
     <% if (temControleCompulsorios && hshParamSvcCse.containsKey(CodedValues.TPS_SERVICO_COMPULSORIO)) { %>
       if (f0.PSEVLR_<%=(String)(CodedValues.TPS_SERVICO_COMPULSORIO)%> != null) {
         if (getCheckedRadio("form1", "PSEVLR_<%=(String)(CodedValues.TPS_SERVICO_COMPULSORIO)%>") == '1') {
           disableRadioButton("form1", "PSEVLR_<%=(String)(CodedValues.TPS_RETIRAVEL_POR_SVC_COMP_PRIORITARIO)%>", false);
         } else {
           // Se não é compulsório, então o serviço sempre poderá ser retirado por um compulsório
           setCheckedRadio("form1", "PSEVLR_<%=(String)(CodedValues.TPS_RETIRAVEL_POR_SVC_COMP_PRIORITARIO)%>", "1");
           disableRadioButton("form1", "PSEVLR_<%=(String)(CodedValues.TPS_RETIRAVEL_POR_SVC_COMP_PRIORITARIO)%>", true);
         }
       }
     <% } %>
    }
    
    function habilitaCamposSvcCompulsorio() {
     <% if (temControleCompulsorios && hshParamSvcCse.containsKey(CodedValues.TPS_RETIRAVEL_POR_SVC_COMP_PRIORITARIO)) { %>
       disableRadioButton("form1", "PSEVLR_<%=(String)(CodedValues.TPS_RETIRAVEL_POR_SVC_COMP_PRIORITARIO)%>", false);
     <% } %>
    }
    
    function controlaCamposCorrecaoVlrPresente() {
     <% if (hshParamSvcCse.containsKey(CodedValues.TPS_POSSUI_CORRECAO_VALOR_PRESENTE) &&
            hshParamSvcCse.containsKey(CodedValues.TPS_FORMA_CALCULO_CORRECAO_VLR_PRESENTE)) { %>
       if (f0.PSEVLR_<%=(String)(CodedValues.TPS_POSSUI_CORRECAO_VALOR_PRESENTE)%> != null) {
         // Se tem correção de valor presente, habilita o combo de seleção de forma de correção
         if (getCheckedRadio("form1", "PSEVLR_<%=(String)(CodedValues.TPS_POSSUI_CORRECAO_VALOR_PRESENTE)%>") == '1') {
           f0.PSEVLR_<%=(String)(CodedValues.TPS_FORMA_CALCULO_CORRECAO_VLR_PRESENTE)%>.disabled = false;
         } else {
           // Se não tem correção, desabilita o combo e seta valor default
           f0.PSEVLR_<%=(String)(CodedValues.TPS_FORMA_CALCULO_CORRECAO_VLR_PRESENTE)%>.value = '';
           f0.PSEVLR_<%=(String)(CodedValues.TPS_FORMA_CALCULO_CORRECAO_VLR_PRESENTE)%>.disabled = true;
         }
       }
     <% } %>
    }
    
    function habilitaCamposCorrecaoVlrPresente() {
     <% if (hshParamSvcCse.containsKey(CodedValues.TPS_FORMA_CALCULO_CORRECAO_VLR_PRESENTE)) { %>
       f0.PSEVLR_<%=(String)(CodedValues.TPS_FORMA_CALCULO_CORRECAO_VLR_PRESENTE)%>.disabled = false;
     <% } %>
    }
    
    function validaCamposCorrecaoVlrPresente() {
     <% if (hshParamSvcCse.containsKey(CodedValues.TPS_POSSUI_CORRECAO_VALOR_PRESENTE) &&
            hshParamSvcCse.containsKey(CodedValues.TPS_FORMA_CALCULO_CORRECAO_VLR_PRESENTE)) { %>
       if ((f0.PSEVLR_<%=(String)(CodedValues.TPS_POSSUI_CORRECAO_VALOR_PRESENTE)%> != null) &&
           (getCheckedRadio("form1", "PSEVLR_<%=(String)(CodedValues.TPS_POSSUI_CORRECAO_VALOR_PRESENTE)%>") == '1') &&
           (f0.PSEVLR_<%=(String)(CodedValues.TPS_FORMA_CALCULO_CORRECAO_VLR_PRESENTE)%>.value == '')) {
           alert('<hl:message key="mensagem.aviso.correcao.valor.presente.exige.forma.calculo"/>');
           f0.PSEVLR_<%=(String)(CodedValues.TPS_FORMA_CALCULO_CORRECAO_VLR_PRESENTE)%>.focus();
           return false;
       }
     <% } %>
     return true;
    }
    
    function controlaCamposTipoVlrAde() {
     <% if (hshParamSvcCse.containsKey(CodedValues.TPS_TIPO_VLR) && hshParamSvcCse.containsKey(CodedValues.TPS_ADE_VLR) && hshParamSvcCse.containsKey(CodedValues.TPS_ALTERA_ADE_VLR)) { %>
       if (f0.PSEVLR_<%=(String)(CodedValues.TPS_TIPO_VLR)%> != null &&
           f0.PSEVLR_<%=(String)(CodedValues.TPS_TIPO_VLR)%>.value == 'T') {
         // Se é tipo valor total, então desabilita o campo de escolha entre valor alterável ou pré-determinado
         f0.PSEVLR_<%=(String)(CodedValues.TPS_ALTERA_ADE_VLR)%>.value = '0';
         f0.PSEVLR_<%=(String)(CodedValues.TPS_ALTERA_ADE_VLR)%>.disabled = true;
       } else {
         // Se não é tipo valor total, então habilita o combo e seta valor default
         f0.PSEVLR_<%=(String)(CodedValues.TPS_ALTERA_ADE_VLR)%>.value = '<%=(String)(!hshParamSvcCse.get(CodedValues.TPS_ALTERA_ADE_VLR).equals("0") ? "1" : "0")%>';
         f0.PSEVLR_<%=(String)(CodedValues.TPS_ALTERA_ADE_VLR)%>.disabled = false;
       }
     <% } %>
    }
    
    function habilitaCamposTipoVlrAde() {
     <% if (hshParamSvcCse.containsKey(CodedValues.TPS_TIPO_VLR) && hshParamSvcCse.containsKey(CodedValues.TPS_ADE_VLR) && hshParamSvcCse.containsKey(CodedValues.TPS_ALTERA_ADE_VLR)) { %>
       f0.PSEVLR_<%=(String)CodedValues.TPS_ALTERA_ADE_VLR%>.disabled = false;
     <% } %>
    }
    
    function montaCombo(valor, combo) {
     var valores = [['D', [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31], [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31]],
                    ['S', [1, 2, 3, 4, 5, 6, 7], [<%=JspHelper.getWeekdayList()%>]],
                    ['U', [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31], [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31]]];
     combo.options.length = 0;
     for (cont = 0; cont < valores.length; cont++) {
       if (valores[cont][0] == valor) {
         for (i = 0; i < valores[cont][1].length; i++) {
           combo.options[i] = new Option(valores[cont][2][i], valores[cont][1][i]);
         }
       }
     }
    }
    
    function validaCalculoSubsidio(){
    	<%if(hshParamSvcCse.containsKey(CodedValues.TPS_TEM_SUBSIDIO) && hshParamSvcCse.containsKey(CodedValues.TPS_TIPO_CALCULO_SUBSIDIO)){%>
    		if(getCheckedRadio("form1", "PSEVLR_<%=(String)(CodedValues.TPS_TEM_SUBSIDIO)%>") == '1'){
    			if(f0.PSEVLR_<%=(String)(CodedValues.TPS_TIPO_CALCULO_SUBSIDIO)%>.value == ''){
    				alert('<hl:message key="mensagem.aviso.necessita.calculo.beneficio"/>');
    				return false;
    			}
    		}
    	<%}%>
    	return true;
    }
    
    var f0 = document.forms[0];  
    window.onload = formLoad;
  </script>
  <script>
	let btnDown = document.querySelector('#btns');
	const pageActions = document.querySelector('#page-actions');
	const pageSize = document.body.scrollHeight;
	
	function up(){
		window.scrollTo({
			top: 0,
			behavior: "smooth",
		});
	}
	
	function down(){
		let toDown = document.body.scrollHeight;
		window.scrollBy({
			top: toDown,
			behavior: "smooth",
		});
	}

	function toActionBtns(){
		let save = document.querySelector('#actions').getBoundingClientRect().top;
		window.scrollBy({
			top: save,
			behavior: "smooth",
		});
	}
	
	function btnTab(){
	    let scrollSize = document.documentElement.scrollTop;
	    
	    if(scrollSize >= 300){
		    btnDown.classList.add('btns-active');    
	    } else {
		    btnDown.classList.remove('btns-active');
	    }
	}
	

	window.addEventListener('scroll', btnTab);
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>