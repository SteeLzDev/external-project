<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.comunicacao.ControleComunicacaoPermitida"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.helper.web.ParamSession"%>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

boolean podeIncluirAnexo      = (boolean) request.getAttribute("podeIncluirAnexo");

CustomTransferObject cse = (CustomTransferObject) request.getAttribute("cse");

int tamMaxMsg      = (int) request.getAttribute("tamMaxMsg");

List<TransferObject> consignatarias = (List<TransferObject>) request.getAttribute("consignatarias");
List<?> orgaos         = (List<?>) request.getAttribute("orgaos");
List<?> assuntos       = (List<?>) request.getAttribute("assuntos");
List<?> naturezas      = (List<?>) request.getAttribute("naturezas");

short enviarEmailValue = (short) request.getAttribute("enviarEmailValue");

String tituloPagina        = (String) request.getAttribute("tituloPagina");
String rseCodigo           = (String) request.getAttribute("rseCodigo");
String nseCodigo           = (String) request.getAttribute("rseCodigo");
String cseCodigo           = (String) request.getAttribute("cseCodigo");
String papCodigo           = (String) request.getAttribute("papCodigo");
String serCodigo           = (String) request.getAttribute("serCodigo");
String serNome             = (String) (request.getAttribute("serNome") != null ? request.getAttribute("serNome") : "");
String csaCodigo           = (String) request.getAttribute("csaCodigo");
String orgCodigo           = (String) request.getAttribute("orgCodigo");
String ascCodigo           = (String) request.getAttribute("ascCodigo");
String email               = (String) request.getAttribute("email");
String serMail             = (String) request.getAttribute("serMail");
String mensagem            = (String) request.getAttribute("mensagem");
String adeCodigo           = (String) request.getAttribute("adeCodigo");

ParamSession paramSession = (ParamSession) request.getAttribute("paramSession");

List<TransferObject> verbas = (List<TransferObject>) request.getAttribute("verbas");
List<String> servidores = !TextHelper.isNull(request.getAttribute("servidores")) ? (List<String>) request.getAttribute("servidores") : new ArrayList<>();
%>
<c:set var="title">
  <%=TextHelper.forHtml(tituloPagina)%>
</c:set>

<c:set var="imageHeader">
  <use xlink:href="#i-sistema"></use>
</c:set> 
<c:set var="bodyContent">  
<div id="main">
   <div class="card">
    <div class="card-header"> 
      <h2 class="card-header-title"> <hl:message key="rotulo.criar.comunicacao.subtitulo"/></h2>
    </div>
      <div class="card-body">
       <form method="post" action="../v3/enviarComunicacao?acao=salvar&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1" ENCTYPE="multipart/form-data">
        <div class="form-group" role="radiogroup" aria-labelledby="entidade">
          <div><span id="entidade"><hl:message key="rotulo.comunicacao.entidade"/></span></div>
          <div class="form-check form-check-inline pt-2">
            <% if (ControleComunicacaoPermitida.getInstance().permite(responsavel.getPapCodigo(), CodedValues.PAP_CONSIGNANTE) && TextHelper.isNull(adeCodigo)) { %>
               <INPUT class="form-check-input ml-1" TYPE="radio" NAME="PAP_CODIGO" id="boxConsignante" onClick="exibeEscondeLinha('CSE_CODIGO')" VALUE="<%=(String)CodedValues.PAP_CONSIGNANTE%>" <% if (papCodigo.equals(CodedValues.PAP_CONSIGNANTE)) {%>checked<% } %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" >
               <label class="form-check-label labelSemNegrito ml-1 pr-2" for="boxConsignante"><hl:message key="rotulo.consignante.singular"/></label>
            <% } %>
            </div>
            <div class="form-check form-check-inline pt-2">
            <% if (ControleComunicacaoPermitida.getInstance().permite(responsavel.getPapCodigo(), CodedValues.PAP_CONSIGNATARIA) || !TextHelper.isNull(adeCodigo)) { %>
               <INPUT class="form-check-input ml-1" TYPE="radio" NAME="PAP_CODIGO" id="boxConsignataria" onClick="exibeEscondeLinha('CSA_CODIGO')" VALUE="<%=(String)CodedValues.PAP_CONSIGNATARIA%>" <% if (papCodigo.equals(CodedValues.PAP_CONSIGNATARIA) || !TextHelper.isNull(adeCodigo)) {%>checked<% } %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" >
               <label class="form-check-label labelSemNegrito ml-1 pr-2" for="boxConsignataria"><hl:message key="rotulo.consignataria.singular"/></label>
            <% } %>
            </div>
            <div class="form-check form-check-inline pt-2">
            <% if (ControleComunicacaoPermitida.getInstance().permite(responsavel.getPapCodigo(), CodedValues.PAP_ORGAO) && TextHelper.isNull(adeCodigo)) { %>
               <INPUT class="form-check-input ml-1" TYPE="radio" NAME="PAP_CODIGO" id="boxOrgao" onClick="exibeEscondeLinha('ORG_CODIGO')" VALUE="<%=(String)CodedValues.PAP_ORGAO%>" <% if (papCodigo.equals(CodedValues.PAP_ORGAO)) {%>checked<% } %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
               <label class="form-check-label labelSemNegrito ml-1 pr-2" for="boxOrgao"><hl:message key="rotulo.orgao.singular"/></label>
            <% } %>
            </div>
            <div class="form-check form-check-inline pt-2">
            <% if (ControleComunicacaoPermitida.getInstance().permite(responsavel.getPapCodigo(), CodedValues.PAP_SERVIDOR) && TextHelper.isNull(adeCodigo)) { %>
               <INPUT class="form-check-input ml-1" TYPE="radio" NAME="PAP_CODIGO" id="boxServidor" onClick="exibeEscondeLinha('servidor')" VALUE="<%=(String)CodedValues.PAP_SERVIDOR%>" <% if (papCodigo.equals(CodedValues.PAP_SERVIDOR)) {%>checked<% } %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" <%=responsavel.isCseSup() && !TextHelper.isNull(csaCodigo) ? "checked" : "" %> >
               <label class="form-check-label labelSemNegrito ml-1 pr-2" for="boxServidor"><hl:message key="rotulo.servidor.singular"/></label>
            <% } %>
          </div>
         </div>
        
        <div class="row">
          <div class="form-group col-sm-12  col-md-6">
            <label for="CSE_CODIGO"><hl:message key="rotulo.consignante.singular"/></label>
            <select class="form-control form-select"  name="CSE_CODIGO" id="CSE_CODIGO" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);" >
              <option value="" <%=(String)(TextHelper.isNull(cseCodigo) ? "SELECTED" : "")%>><hl:message key="rotulo.campo.selecione"/></option>
                <%
                  List cseList = new ArrayList();
                  cseList.add(cse);
                  Iterator iteCse = cseList.iterator();
                  while (iteCse.hasNext()) {
                      CustomTransferObject ctoCse = (CustomTransferObject)iteCse.next();
                      String fieldValueCse = ctoCse.getAttribute(Columns.CSE_CODIGO).toString();
                      String fieldLabelCse = ctoCse.getAttribute(Columns.CSE_NOME) + " - " + ctoCse.getAttribute(Columns.CSE_IDENTIFICADOR);
                %>
                <option value="<%=TextHelper.forHtmlAttribute(fieldValueCse)%>" <%=(String)((!TextHelper.isNull(cseCodigo) && cseCodigo.equals(fieldValueCse)) ? "SELECTED" : "")%>><%=TextHelper.forHtmlContent(fieldLabelCse)%></option>
                <%    
                  }
                %>
             </select>
            </div>
          </div>

        <% if (consignatarias != null) { %>
        <div class="row">
          <div class="form-group col-sm-12  col-md-6">
            <label for="CSA_CODIGO"><hl:message key="rotulo.consignataria.singular"/></label>
            <select class="form-control form-select" name="CSA_CODIGO" id="CSA_CODIGO" onFocus="SetarEventoMascara(this,'#*200',true);" multiple="multiple" onBlur="fout(this);" onClick="mudaSelectNse()" size="6">
            <%if (TextHelper.isNull(adeCodigo)) {%>
             	<option value="todas" <%=(String)(TextHelper.isNull(csaCodigo) ? "SELECTED" : "")%>><hl:message key="rotulo.campo.todas"/></option>
             <%} %>
                <%
                 Iterator<?> it = consignatarias.iterator();
                 CustomTransferObject csa = null;
                 String csaCodigo2, csaNome, csaId;
                 while (it.hasNext()) {
                   csa = (CustomTransferObject)it.next();
                   csaCodigo2 = (String)csa.getAttribute(Columns.CSA_CODIGO);
                   csaId = csa.getAttribute(Columns.CSA_IDENTIFICADOR).toString();
                   csaNome = csa.getAttribute(Columns.CSA_NOME).toString();
                   if (csaNome.length() > 50){
                      csaNome = csaNome.substring(0, 47) + "...";
                   }
                %>
                     <option value="<%=TextHelper.forHtmlAttribute(csaCodigo2)%>" <%=(String)((!TextHelper.isNull(csaCodigo) && csaCodigo.equals(csaCodigo2) || !TextHelper.isNull(adeCodigo)) ? "SELECTED" : "")%>><%=TextHelper.forHtmlContent(csaNome)%> - <%=TextHelper.forHtmlContent(csaId)%></option>
                <% } %>
            </select>
          </div>
        </div>
        <%if (TextHelper.isNull(adeCodigo)){ %>
        <div class="row">
          <div class="form-group col-sm-12  col-md-6">
            <label for="NSE_CODIGO"><hl:message key="rotulo.param.svc.natureza.servico"/></label>
            <select class="form-control form-select" name="NSE_CODIGO" id="NSE_CODIGO" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);" onClick="mudaSelectCsa()">
             <option value="" <%=(String)(TextHelper.isNull(nseCodigo) ? "SELECTED" : "")%>><hl:message key="rotulo.campo.selecione"/></option>
            <%
              Iterator<?> itNse = naturezas.iterator();
              CustomTransferObject natureza = null;
              String nse_descricao, nse_codigo;
              while (itNse.hasNext()) {
                natureza = (CustomTransferObject)itNse.next();
                nse_descricao = (String)natureza.getAttribute(Columns.NSE_DESCRICAO);
                nse_codigo = (String)natureza.getAttribute(Columns.NSE_CODIGO);                    
            %>
               <option value="<%=TextHelper.forHtmlAttribute(nse_codigo)%>"><%=TextHelper.forHtmlContent(nse_descricao)%></option>
            <% } %>
            </select>
          </div>
        </div> 
             <% } %>
         <% } %>

        <% if (orgaos != null) { %>
        <div class="row">
          <div class="form-group col-sm-12  col-md-6">
            <label for="ORG_CODIGO"><hl:message key="rotulo.orgao.singular"/></label>
            <select class="form-control form-select" name="ORG_CODIGO" id="ORG_CODIGO" onFocus="SetarEventoMascara(this,'#*200',true);" multiple="multiple" onBlur="fout(this);" size="6">
             <option value=""><hl:message key="rotulo.campo.selecione"/></option>
             <option value="todos"><hl:message key="rotulo.campo.todos"/></option>
                <%
                 Iterator it = orgaos.iterator();
                 CustomTransferObject org = null;
                 String orgCodigo2, orgNome, orgId;
                 while (it.hasNext()) {
                     org = (CustomTransferObject)it.next();
                     orgCodigo2 = (String)org.getAttribute(Columns.ORG_CODIGO);
                     orgId = org.getAttribute(Columns.ORG_IDENTIFICADOR).toString();
                     orgNome = org.getAttribute(Columns.ORG_NOME).toString();
                     if (orgNome.length() > 50){
                         orgNome = orgNome.substring(0, 47) + "...";
                     }
                %>
                     <option value="<%=TextHelper.forHtmlAttribute(orgCodigo2)%>" <%=(String)((!TextHelper.isNull(orgCodigo) && orgCodigo.equals(orgCodigo2)) ? "SELECTED" : "")%>><%=TextHelper.forHtmlContent(orgNome)%> - <%=TextHelper.forHtmlContent(orgId)%></option>
                <% } %>
            </select>
          </div>
         </div>
         <% } %>
         
         <div class="row">
          <div class="form-group col-sm-4">
            <label for="servidor"><hl:message key="rotulo.servidor.singular"/></label>
            <div class="col-sm-12">
                <input type="text" class="form-control" id="servidor" name="servidor" value="<%=TextHelper.forHtmlAttribute(serNome)%>" size="50" placeholder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.procurar.servidor", responsavel) %>"  onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" readonly>
              </div>
              <div>
                <a class="btn btn-primary" href="#" onClick="pesquisaServidor()">
                  <svg width="20">
                    <use xlink:href="#i-consultar"></use>
                  </svg> <hl:message key="rotulo.botao.pesquisar"/>
                </a>
                <input TYPE="hidden" name="SER_CODIGO" value="<%=TextHelper.forHtmlAttribute(TextHelper.isNull(serCodigo) ? "" : serCodigo)%>">
                <input TYPE="hidden" name="RSE_CODIGO" value="<%=TextHelper.forHtmlAttribute(TextHelper.isNull(rseCodigo) ? "" : rseCodigo)%>">
              </div>
          </div>
          <div class="form-group col-sm-4 col-md-1 mt-4">
            <a id="removeAdeLista" class="btn btn-primary w-50 mt-0" href="javascript:void(0);" onClick="removeServidor()" aria-label='<hl:message key="mensagem.remover.ade.numero.clique.aqui"/>'>
              <svg width="15"><use xlink:href="../img/sprite.svg#i-voltar"></use></svg>
            </a>
          </div>
          <div id="adeLista" class="form-group col-sm-4">
            <label for="servidor"><hl:message key="rotulo.criar.comunicacao.listagem.servidores"/></label>
            <select class="form-control w-100" id="SERVIDOR_LIST" name="SERVIDOR_LIST" multiple="multiple" size="6">
                <%if (servidores != null && !servidores.isEmpty()){ 
                    for (String servidor : servidores) {
                        String[] rseSerNome = servidor.split("-");
                        String nomeServidor = rseSerNome[2];
                    %>
                    <option value="<%=TextHelper.forHtmlAttribute(servidor)%>"><%=nomeServidor%></option>
                    <%} %>
                <%} %>
            </select>
          </div>
        </div>
        
        <%if(responsavel.isCseSup()){ %>
            <div class="row">
              <div class="form-group col-sm-12">
                <div class="form-group col-sm-12 col-md-6">
                  <label id="lblConsignataria" for="csaCodigo"><hl:message key="rotulo.consignataria.plural"/></label>
                  <%=JspHelper.geraCombo(consignatarias, "csaCodigo", Columns.CSA_CODIGO, Columns.CSA_NOME, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), "onChange=\"carregaCodVerbas(this);\"", false, 1, csaCodigo, null, false, "form-control")%>
                </div>
              </div>
              <div class="form-group col-sm-12">
                <div class="form-group col-sm-12 col-md-6">
                  <label id="lblverbas" for="cnvCodigo"><hl:message key="rotulo.servidor.verbas"/></label>
                  <%=JspHelper.geraCombo(verbas, "cnvCodVerba", Columns.CNV_COD_VERBA, Columns.CNV_COD_VERBA, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, null, null, false, "form-control")%>
                </div>
              </div>
            </div>
        <%} %>
        
        <% if (responsavel.isSer()) { %>
           <div class="row">
               <div class="form-group col-sm-12 col-md-6" role="radiogroup" >
                   <div><span><hl:message key="rotulo.criar.comunicacao.aviso.csa"/></span></div>
                   <div class="form-check form-check-inline pt-2">
                       <input class="form-check-input ml-1" type="radio" name="enviarEmail" id="enviarEmailSim" onClick="changeForm()" value="1" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" <%=(String)((enviarEmailValue == 1) ? "CHECKED":"")%> >
                       <label class="form-check-label labelSemNegrito ml-1 pl-4 pr-1" for="enviarEmailSim"><hl:message key="rotulo.sim"/></label>
                   </div>
                   <div class="form-check form-check-inline pt-2">
                       <input class="form-check-input ml-1" type="radio" name="enviarEmail" id="enviarEmailNao" onClick="changeForm()" value="0" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" <%=(String)((enviarEmailValue == 0) ? "CHECKED":"")%> >
                       <label class="form-check-label labelSemNegrito ml-1 pl-4 pr-1" for="enviarEmailNao"><hl:message key="rotulo.nao"/></label>
                   </div>
               </div>
           </div>
        <div class="row">
          <div class="form-group col-sm-12 col-md-6">
             <span><hl:message key="rotulo.criar.comunicacao.email.resposta"/></span>
              <div class="form-check pt-2">
                <input class="form-control" name="email" type="text" value="<%=TextHelper.forHtmlAttribute((email != null) ? email:(serMail != null) ? serMail:"")%>" size="50" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" disabled >
              </div>
          </div>
          </div>
      <% } else if (ControleComunicacaoPermitida.getInstance().permite(responsavel.getPapCodigo(), CodedValues.PAP_SERVIDOR)) { %>
        <div class="row">
          <div class="form-group col-sm-12 col-md-6" role="radiogroup" >
             <span><hl:message key="rotulo.criar.comunicacao.enviar.copia.email"/></span>
              <div class="form-check form-check-inline pt-2">
                <input class="form-check-input ml-1" type="radio" name="enviarCopiaEmail" id="enviarCopiaEmailSim" value="1" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                <label class="form-check-label labelSemNegrito ml-1 pl-4 pr-1" for="enviarCopiaEmailSim"><hl:message key="rotulo.sim"/></label>
              </div>
              <div class="form-check form-check-inline pt-2">
                <input class="form-check-input ml-1" type="radio" name="enviarCopiaEmail" id="enviarCopiaEmailNao" value="0" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" CHECKED>
                <label class="form-check-label labelSemNegrito ml-1 pl-4 pr-1" for="enviarCopiaEmailNao"><hl:message key="rotulo.nao"/></label>
              </div>
          </div>
        </div>
      <% } %>
      <% if (ControleComunicacaoPermitida.getInstance().permite(responsavel.getPapCodigo(), CodedValues.PAP_ORGAO) || ControleComunicacaoPermitida.getInstance().permite(responsavel.getPapCodigo(), CodedValues.PAP_CONSIGNANTE)) { %>
        <div class="row">
          <div class="form-group col-sm-12 col-md-6" role="radiogroup" >
             <span><hl:message key="rotulo.criar.comunicacao.enviar.copia.email.cse.org"/></span>
              <div class="form-check form-check-inline pt-2">
                <input class="form-check-input ml-1" type="radio" name="enviarCopiaEmail" id="enviarCopiaEmailCseOrgSim" value="1" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                <label class="form-check-label labelSemNegrito ml-1 pl-4 pr-1" for="enviarCopiaEmailCseOrgSim"><hl:message key="rotulo.sim"/></label>
              </div>
              <div class="form-check form-check-inline pt-2">
                <input class="form-check-input ml-1" type="radio" name="enviarCopiaEmail" id="enviarCopiaEmailCseOrgNao" value="0" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" CHECKED>
                <label class="form-check-label labelSemNegrito ml-1 pl-4 pr-1" for="enviarCopiaEmailCseOrgNao"><hl:message key="rotulo.nao"/></label>
              </div>
          </div>
        </div>
      <% } %>
      
      <div class="row">
        <div class="form-group col-sm-12  col-md-6">
          <label for="categoriaAssunto"><hl:message key="rotulo.comunicacao.categoria"/></label>
          <select class="form-control form-select" name="ASC_CODIGO" id="ASC_CODIGO" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);">
          <option value="" <%=(String)((TextHelper.isNull(ascCodigo)) ? "SELECTED" : "")%>><hl:message key="rotulo.campo.selecione"/></option>
           <%
                 Iterator iteratorAssunto = assuntos.iterator();
                 CustomTransferObject asc = null;
                 String ascDescricao;
                 String ascCodigo2;
                 while (iteratorAssunto.hasNext()) {
                   asc = (CustomTransferObject)iteratorAssunto.next();
                   ascCodigo2 = (String)asc.getAttribute(Columns.ASC_CODIGO);
                   ascDescricao = asc.getAttribute(Columns.ASC_DESCRICAO).toString();
                 %>
                   <option value="<%=TextHelper.forHtmlAttribute(ascCodigo2)%>" <%=(String)(ascCodigo2.equals(ascCodigo) ? "SELECTED" : "")%>><%=TextHelper.forHtmlContent(ascDescricao)%></option>
                <% } %>
          </select>
       </div>
      </div>
      
      <div class="row">
        <div class="form-group col-sm-6">
          <label for="texto"><hl:message key="rotulo.comunicacao.texto"/></label>
          <textarea class="form-control" id="texto" rows="6" placeholder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.texto", responsavel)%>" name="mensagem" onFocus="SetarEventoMascara(this,'#*<%=TextHelper.forJavaScript((tamMaxMsg))%>',true);" onBlur="fout(this);ValidaMascara(this);"><%=TextHelper.forHtmlContent((mensagem != null) ? mensagem:"")%></textarea>
        </div>
      </div>  
      
        <%if(podeIncluirAnexo){ %>
        <div class="row">
          <div class="form-group col-sm-6">
            <label for="iArquivo"><hl:message key="rotulo.comunicacao.anexo.arquivo"/></label>
            <input type="file" class="form-control" name="FILE1" id="FILE1" size="56">
          </div>
        </div>
        <%} %>
        <input TYPE="hidden" name="ADE_CODIGO" value="<%=adeCodigo%>" />
     </form>
   </div>
  </div>
  <div class="btn-action">
       <hl:htmlinput name="exibeCse" type="hidden" value="true" />
       <hl:htmlinput name="exibeCsa" type="hidden" value="true" />
       <hl:htmlinput name="exibeSer" type="hidden" value="true" />
       
       <a class="btn btn-outline-danger" href="#" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;" ><hl:message key="rotulo.botao.cancelar"/></a>
       <a class="btn btn-primary" href="#" onClick="javascript: enviar(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
    </div>
  </div>
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
            <div class="modal-body"><span><hl:message key="mensagem.info.comunicacao.aguarde"/></span></div>            
           </div>
         </div>
       </div>
     </div>
   </div>
  </div>
</c:set>
<c:set var="javascript">
 <script type="text/JavaScript" src="../js/listutils.js?<hl:message key="release.tag"/>"></script>
 <script type="text/JavaScript" src="../js/listagem.js?<hl:message key="release.tag"/>"></script>
<script type="text/JavaScript">
window.onload = formLoad;
f0 = document.forms[0];

function exibeEscondeLinha(atributo){
    limpaCampos(atributo);
    escondeLinhas();
    $("#"+atributo).parents("div.row").toggle();
    
    if(atributo == 'CSA_CODIGO'){
        <% if (TextHelper.isNull(adeCodigo)) { %>
    		$("#NSE_CODIGO").parents("div.row").toggle();
    	<%}%>
    }
    if (atributo == 'servidor') {
        $("#enviarCopiaEmailSim").parents("div.row").show();
        $("#lblConsignataria").parents("div.row").show();
    } else if (atributo == 'CSE_CODIGO' || atributo == 'ORG_CODIGO') { 
    	$('#enviarCopiaEmailCseOrgSim').parents("div.row").show();
    } else {
        $('#enviarCopiaEmailSim').parents("div.row").hide();
    	$('#enviarCopiaEmailCseOrgSim').parents("div.row").hide();
    }
}

function escondeLinhas(){
    $('#CSE_CODIGO').parents("div.row").hide();
    $('#CSA_CODIGO').parents("div.row").hide();
    $('#ORG_CODIGO').parents("div.row").hide();
    <% if (TextHelper.isNull(adeCodigo)) { %>
	    $('#NSE_CODIGO').parents("div.row").hide();
	<%}%>    
    $('#servidor').parents("div.row").hide();
    $('#enviarCopiaEmailSim').parents("div.row").hide();
	$('#enviarCopiaEmailCseOrgSim').parents("div.row").hide();
	$('#lblConsignataria').parents("div.row").hide();
}

function limpaCampos(atributo) { 
    if ( atributo != 'CSE_CODIGO') {
        f0.CSE_CODIGO.options.selectedIndex = 0;
    }
    if (atributo != 'CSA_CODIGO') {
        f0.CSA_CODIGO.selectedIndex = -1;
        <% if (TextHelper.isNull(adeCodigo)) { %>
	        f0.NSE_CODIGO.selectedIndex = 0;
	    <%}%>
    }
    if (atributo != 'ORG_CODIGO') {
        f0.ORG_CODIGO.selectedIndex = 0;
    }
    if (atributo != 'servidor') {
        $('#servidor').val("");
        $('#SER_CODIGO').val("");
        $('#RSE_CODIGO').val("");
    }
}

function formLoad() {
    escondeLinhas();
    if (<%=TextHelper.isNull(adeCodigo)%> && ($('#servidor').val() != "" || $('#boxServidor:radio:checked').length > 0)) {
        $('#boxServidor').attr('checked', true);
        exibeEscondeLinha("servidor");
    } else if ($('#boxConsignataria:radio:checked').length > 0 || <%=!TextHelper.isNull(adeCodigo)%>) {
        exibeEscondeLinha("CSA_CODIGO");        
        $('#CSA_CODIGO').attr('checked', true);
    } else if ($('#ORG_CODIGO').val() != "" || $('#boxOrgao:radio:checked').length > 0) {
        exibeEscondeLinha("ORG_CODIGO");
        $('#ORG_CODIGO').attr('checked', true);
    } else if ($('#CSE_CODIGO').val() != "" || $('#boxConsignante:radio:checked').length > 0) {
        exibeEscondeLinha("CSE_CODIGO");
        $('#CSE_CODIGO').attr('checked', true);
    } 
}   

function changeForm() {
    var radio = f0.enviarEmail;
    with (document.form1) {
        if (getCheckedRadio('form1', 'enviarEmail') == '1') {
            if (f0.email == null || f0.email.value == '') {
                alert('<hl:message key="mensagem.erro.email.ser.nao.cadastrado"/>');
                for (c=0;c<radio.length;c++) {
                    if (radio[c].value == '0') {
                        radio[c].checked = true;
                        return;
                    }
                }
                return;
            }
        } 
    }
}
   
function enviar() {
    var papCodigo = getCheckedRadio('form1', 'PAP_CODIGO');
    if ((papCodigo == null || papCodigo == '') == true) {
        alert('<hl:message key="mensagem.informe.comunicacao.entidade"/>');
        return;
    }
   
    var ascCodigo = trim(f0.ASC_CODIGO.value);
    if ((ascCodigo == null || ascCodigo == '') == true) {
        alert('<hl:message key="mensagem.informe.comunicacao.categoria"/>');
        return;
    }
   
    var msg = trim(f0.mensagem.value);
    
    // Verifica quantidade de caracteres informados na mensagem
    if (msg.length < 10) {
        alert('<hl:message key="mensagem.erro.comunicacao.texto.minimo"/>');
        f0.mensagem.focus();
        return;
    }
    
    // Verifica se existe pelo menos uma letra na mensagem
    var regex = /([a-zA-Z]+)/g;
    if (!msg.match(regex)) {
        alert('<hl:message key="mensagem.erro.comunicacao.texto.invalido"/>');
        f0.mensagem.focus();
        return;
    }
    
    var Controles = [];
    var Msgs = [];
    var campoCsa = document.querySelector('#CSA_CODIGO').value;
    var campoNse = '';
   
    if (papCodigo == '<%=(String)CodedValues.PAP_CONSIGNANTE%>') {
        Controles = new Array("CSE_CODIGO", "mensagem");
        Msgs = new Array('<hl:message key="mensagem.informe.comunicacao.cse"/>',
                         '<hl:message key="mensagem.informe.comunicacao.texto"/>');
    
    } else if (papCodigo == '<%=(String)CodedValues.PAP_CONSIGNATARIA%>' && (campoCsa == "" && campoNse == "")) {    	
        Controles = new Array("CSA_CODIGO", "mensagem");
        Msgs = new Array('<hl:message key="mensagem.informe.comunicacao.csa"/>',
                         '<hl:message key="mensagem.informe.comunicacao.texto"/>');

    } else if (papCodigo == '<%=(String)CodedValues.PAP_ORGAO%>') {
        Controles = new Array("ORG_CODIGO", "mensagem");
        Msgs = new Array('<hl:message key="mensagem.informe.comunicacao.org"/>',
                         '<hl:message key="mensagem.informe.comunicacao.texto"/>');
       
    } else if (papCodigo == '<%=(String)CodedValues.PAP_SERVIDOR%>') {
    	var servidorSelecionado = document.getElementById("servidor").value;
    	<%if(responsavel.isCseSup()){%>
    		var consignatariaSelecionada = document.getElementById("csaCodigo").value;
    		if ((servidorSelecionado == "" || servidorSelecionado == "undefined" || servidorSelecionado == null) && (consignatariaSelecionada == "" || consignatariaSelecionada == "undefined" || consignatariaSelecionada == null)){
    			alert('<hl:message key="mensagem.informe.comunicacao.ser.csa"/>');
    			return false;
    		}
    	<%} else {%>
        	Controles = new Array("servidor", "mensagem");
            Msgs = new Array('<hl:message key="mensagem.informe.comunicacao.ser"/>',
                             '<hl:message key="mensagem.informe.comunicacao.texto"/>');
        <%}%>

        selecionarTodosItens('SERVIDOR_LIST');
    }
   
    if (ValidaCampos(Controles, Msgs)) {
        if((getCheckedRadio('form1', 'enviarEmail') == '1') && (f0.email != null && f0.email.value == '')) {
           alert('<hl:message key="mensagem.informe.comunicacao.email"/>');
           f0.email.focus();
        } else {
          $('#modalAguarde').modal({
            backdrop: 'static',
            keyboard: false
      });
           f0.submit();
        }
    }
}

function mudaSelectCsa() {
 <%if (TextHelper.isNull(adeCodigo)){ %>
	  if (f0.NSE_CODIGO.value != null && f0.NSE_CODIGO.value != "") {
	  	$('#CSA_CODIGO').val("");  	
	  }
  <%}%>
}

function mudaSelectNse() {
  if (f0.CSA_CODIGO.value != null && f0.CSA_CODIGO.value != "") {
  	$("#NSE_CODIGO").val("");
  }
}
          
function pesquisaServidor() {
    const selectServidorList = document.getElementById("SERVIDOR_LIST");
    if (selectServidorList) {
        let valorLista = [];

        for (let option of selectServidorList.options) {
            if (option.value !== null && option.value !== undefined && option.value.trim() !== "") {
                valorLista.push(option.value);
            }
        }

        let valoresListaConcatenados = valorLista.join(';') + ';';
        postData('../v3/enviarComunicacao?acao=iniciar&listServidor='+ valoresListaConcatenados + '&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>')
    } else {
        postData('../v3/enviarComunicacao?acao=iniciar&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>')
    }
}

function carregaCodVerbas(valor){
	var categoria = document.getElementById("ASC_CODIGO").value;
	var mensagem = document.getElementById("texto").value;
    postData('../v3/enviarComunicacao?acao=enviar&_skip_history_=true&CSA_CODIGO='+valor.value+'&mensagem='+mensagem+'&ASC_CODIGO='+categoria+'&PAP_CODIGO=<%=CodedValues.PAP_SERVIDOR%>&<%=SynchronizerToken.generateToken4URL(request)%>')
}

function removeServidor() {
    removeDaLista('SERVIDOR_LIST');
    if (document.getElementById('SERVIDOR_LIST').length == 0) {
        document.getElementById('removeAdeLista').style.display = 'none';
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
