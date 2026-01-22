<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.values.NaturezaPlanoEnum"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />

<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
  
  boolean podeEditarPla = (boolean) request.getAttribute("podeEditarPla");
  boolean podeExcluirPla = (boolean) request.getAttribute("podeExcluirPla");
  boolean podeConsultarPla = (boolean) request.getAttribute("podeConsultarPla");
  String plaCodigo = (String) request.getAttribute("plaCodigo");
  String plaDescricao = (String) request.getAttribute("plaDescricao");
  String plaIdentificador = (String) request.getAttribute("plaIdentificador");
  Short plaAtivo = (Short) request.getAttribute("plaAtivo");
  String svcCodigo = (String) request.getAttribute("svcCodigo");
  String nplCodigo = (String) request.getAttribute("nplCodigo");
  String csaCodigo = (String) request.getAttribute("csaCodigo");
  String reqColumnsStr = (String) request.getAttribute("reqColumnsStr");
  String msgErro = (String) request.getAttribute("msgErro");
  String svcCodigoPg = (String) request.getAttribute("svcCodigoPg");
  String nplCodigoPg = (String) request.getAttribute("nplCodigoPg");
  TransferObject planoDesconto = (TransferObject) request.getAttribute("planoDesconto");
  HashMap<?, ?> hshParamPlano = (HashMap<?, ?>) request.getAttribute("hshParamPlano");
  String tituloPagina = (String) request.getAttribute("tituloPagina");
  List<?> lstSvc = (List<?>) request.getAttribute("lstSvc");
  List<?> lstNpl = (List<?>) request.getAttribute("lstNpl");
  String tppIndice = (String) request.getAttribute("tppIndice");
  String strMaxPrazo = (String) request.getAttribute("strMaxPrazo");
  int intMaxPrazo = (Integer) request.getAttribute("intMaxPrazo");
  String ade_vlr = (String) request.getAttribute("ade_vlr");
%>

<c:set var="title">
  <hl:message key="<%=TextHelper.forHtml("rotulo.incluir.plano.titulo")%>"/>
</c:set>

<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>

<c:set var="bodyContent">
<%if (podeEditarPla) {%>
  <% if (!TextHelper.isNull(plaCodigo)) { %>
    <div class="btn-action"> 
    <%if(plaAtivo == 1){ %> 
      <a class="btn btn-primary" href="#no-back" onClick="BloquearEntidade(<%=TextHelper.forJavaScript((plaAtivo))%>, '<%=TextHelper.forJavaScript(plaCodigo)%>', 'PLA', '../v3/consultarPlanoDesconto?acao=bloquear&tipo=editar&svcCodigo=<%=TextHelper.forJavaScript(svcCodigo )%>&nplCodigo=<%=TextHelper.forJavaScript(nplCodigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>', '<%=TextHelper.forJavaScript(plaDescricao)%>', ''); return false;"><hl:message key="rotulo.plano.bloquear"/></a>     
    <%} else { %>
      <a class="btn btn-primary" href="#no-back" onClick="BloquearEntidade(<%=TextHelper.forJavaScript((plaAtivo))%>, '<%=TextHelper.forJavaScript(plaCodigo)%>', 'PLA', '../v3/consultarPlanoDesconto?acao=bloquear&tipo=editar&svcCodigo=<%=TextHelper.forJavaScript(svcCodigo )%>&nplCodigo=<%=TextHelper.forJavaScript(nplCodigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>', '<%=TextHelper.forJavaScript(plaDescricao)%>', ''); return false;"><hl:message key="rotulo.plano.desbloquear"/></a>  
    <%} %>
    </div>
  <%} %>
<%} %>
 <form method="post" action="../v3/consultarPlanoDesconto?acao=editar&tipo=salvar&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1" >
  <div class="card">
    <div class="card-header hasIcon pl-3">
    <% if (TextHelper.isNull(plaCodigo)) { %>
      <h2 class="card-header-title"><hl:message key="rotulo.incluir.plano.subtitulo"/></h2>
    <% } else { %>
      <h2 class="card-header-title"><%=TextHelper.forHtmlContent(plaIdentificador)%> - <%=TextHelper.forHtmlContent(plaDescricao)%></h2>
    <% } %>
    </div>
    <div class="card-body">
        <fieldset>
             <div class="row">
              <div class="form-group col-sm-6  col-md-6">
                <label for="plaIdentificador"><hl:message key="rotulo.plano.codigo"/></label>
                <hl:htmlinput name="plaIdentificador"
                              type="text"
                              classe="form-control"
                              value="<%=TextHelper.forHtmlAttribute(planoDesconto !=null ? (String) planoDesconto.getAttribute(Columns.PLA_IDENTIFICADOR):JspHelper.verificaVarQryStr(request, \"plaIdentificador\"))%>"
                              size="32"
                              mask="#L40"
                              others="<%=TextHelper.forHtmlAttribute( !podeEditarPla || !TextHelper.isNull(plaCodigo) ? "disabled" : "")%>"/>
                <%if (request.getParameter("MM_insert")!= null) {%>
                  <%=JspHelper.verificaCampoNulo(request, "plaIdentificador")%>
                <%} %>
              </div>
              <div class="form-group col-sm-6  col-md-6">
                <label for="plaDescricao"><hl:message key="rotulo.plano.descricao"/></label>
                <hl:htmlinput name="plaDescricao"
                              type="text"
                              classe="form-control"
                              value="<%=TextHelper.forHtmlAttribute(planoDesconto !=null ? (String) planoDesconto.getAttribute(Columns.PLA_DESCRICAO):JspHelper.verificaVarQryStr(request, \"plaDescricao\"))%>"
                              size="50"
                              mask="#*100"
                              others="<%=TextHelper.forHtmlAttribute( !podeEditarPla ? "disabled" : "")%>"/>
                <%if (request.getParameter("MM_insert")!= null) {%>
                   <%=JspHelper.verificaCampoNulo(request, "plaDescricao")%>
                <%} %>                
              </div>           
             </div>
             <div class="row">
              <div class="form-group col-sm-6  col-md-6">
                <label for="svcCodigo"><hl:message key="rotulo.servico.singular"/></label>
            <%              
              Collections.sort(lstSvc, new Comparator () {
                public int compare(Object one, Object two){
                  TransferObject oneTO = (TransferObject) one;
                  TransferObject twoTO = (TransferObject) two;
                  
                  String oneDesc = (String) oneTO.getAttribute(Columns.SVC_DESCRICAO);
                  String twoDesc = (String) twoTO.getAttribute(Columns.SVC_DESCRICAO);
                  return oneDesc.compareTo(twoDesc);
                } 
              });              

              // Define variaveis de grupo
              String svcCodigoRegistro, svcDescricao;          

              if (!lstSvc.isEmpty()) {
                CustomTransferObject svcTO = new CustomTransferObject();
            %>
                <select class="form-control" name="svcCodigo" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" <%=(String)(!podeEditarPla || !TextHelper.isNull(plaCodigo) ? "disabled" : "")%>>
                  <optgroup label="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>">
                    <option value=""><hl:message key="rotulo.campo.selecione"/></option>
              <%
                Iterator<?> itSvc = lstSvc.iterator();
                while(itSvc.hasNext()){
                  svcTO = (CustomTransferObject)itSvc.next();
                  svcCodigoRegistro = svcTO.getAttribute(Columns.SVC_CODIGO).toString();
                  svcDescricao  = svcTO.getAttribute(Columns.SVC_DESCRICAO).toString();                      
              %>
                    <option value="<%=TextHelper.forHtmlAttribute(svcCodigoRegistro)%>" <%=svcCodigoRegistro.equals(svcCodigo) ? "SELECTED" : ""%>><%=TextHelper.forHtmlContent(svcDescricao)%></option>
              <% } %>
                  </optgroup>
                </select>
           <% } %>
           <%if (request.getParameter("MM_insert")!= null) {%>
             <%=JspHelper.verificaCampoNulo(request, "svcCodigo")%>
           <% } %>
              </div>
  
              <div class="form-group col-sm-6  col-md-6">
                <label for="nplCodigo"><hl:message key="rotulo.plano.natureza"/></label>
                <%              
              // Define variaveis de grupo
              String nplCodigoReg, nplDescricao;          

              if (!lstNpl.isEmpty()) {
                CustomTransferObject nplTO = new CustomTransferObject();
              %>
                <select class="form-control" name="nplCodigo" onChange="mudaNatureza()" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" <%=(String)(!podeEditarPla || !TextHelper.isNull(plaCodigo) ? "disabled" : "")%>>
                  <optgroup>
                    <option value=""><hl:message key="rotulo.campo.selecione"/></option>
                  <%
                    Iterator<?> itNpl = lstNpl.iterator();
                    while(itNpl.hasNext()){
                      nplTO = (CustomTransferObject)itNpl.next();
                      nplCodigoReg = nplTO.getAttribute(Columns.NPL_CODIGO).toString();
                      nplDescricao  = nplTO.getAttribute(Columns.NPL_DESCRICAO).toString();                      
                  %>
                    <option value="<%=TextHelper.forHtmlAttribute(nplCodigoReg)%>" <%=nplCodigoReg.equals(nplCodigo) ? "SELECTED" : ""%>><%=TextHelper.forHtmlContent(nplDescricao)%></option>
                  <% } %>
                  </optgroup>
                </select>
              <% } %>
           <%if (request.getParameter("MM_insert") != null) {%>
            <%=JspHelper.verificaCampoNulo(request, "nplCodigo")%>
           <%} %>
              </div>
             </div>      
        </fieldset>        
    </div>
  </div>
  
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.listar.plano.subtitulo.parametros"/></h2>
    </div>
    <div class="card-body">
      <div class="row">
        <div class="form-group col-sm-6  col-md-6">
          <label for="tpp_<%=(String)CodedValues.TPP_INDICE_PLANO%>"><hl:message key="rotulo.plano.indice"/></label>
          <input name="tpp_<%=(String)CodedValues.TPP_INDICE_PLANO%>" 
                type="text"
                class="form-control" 
                id="tpp_<%=(String)CodedValues.TPP_INDICE_PLANO%>" 
                value="<%=TextHelper.forHtmlAttribute(!TextHelper.isNull(tppIndice) ? tppIndice : "" )%>"
                <%= !podeEditarPla ? "disabled" : ""%>>
        </div>
      </div>
      <div class="row">
        <div class="form-group col-sm-6 col-md-6">
          <label for="tpp_<%=(String)CodedValues.TPP_PRAZO_MAX_PLANO%>"><hl:message key="rotulo.plano.quantidade.maxima.parcelas"/></label>
          <input class="form-control" name="tpp_<%=(String)CodedValues.TPP_PRAZO_MAX_PLANO%>" type="text" value="<%=TextHelper.forHtmlAttribute(strMaxPrazo != null ? strMaxPrazo : "")%>" onChange="f0.prazo.selectedIndex=1" <%=(String)((!podeEditarPla || nplCodigo.equals(NaturezaPlanoEnum.TAXA_USO.getCodigo()))? "disabled" : "")%> onFocus="SetarEventoMascara(this,'#D12',true);" onBlur="fout(this);ValidaMascara(this);">          
        </div>
        <div class="form-group col-sm-3  col-md-3">
          <label for="prazo"><hl:message key="rotulo.plano.prazo"/></label>
          <select class="form-control" name="prazo" onChange="mudaPrazo()" <%=(String)((!podeEditarPla || nplCodigo.equals(NaturezaPlanoEnum.TAXA_USO.getCodigo()))? "disabled" : "")%> >             
              <optgroup>
               <option VALUE="0" <%=(String)(intMaxPrazo == 0 ? "SELECTED":"")%>><hl:message key="rotulo.plano.indeterminado"/></option>
               <option VALUE="1" <%=(String)(intMaxPrazo > 0 ? "SELECTED":"")%>><hl:message key="rotulo.plano.limitado"/></option>
               <option VALUE="-1" <%=(String)(intMaxPrazo == -1 ? "SELECTED":"")%>><hl:message key="rotulo.plano.qualquer"/></option>
              </optgroup>
            </select>
        </div>
        <div class="form-group col-sm-3">
          <label for="checkPrazoFixo"><hl:message key="rotulo.plano.fixo"/></label>
          <div class="form-check pt-2" name="checkPrazoFixo">
            <div class="form-check form-check-inline pt-2">
              <input class="form-check-input ml-1" type="radio" id="codigoSim" name="tpp_<%=(String)CodedValues.TPP_PRAZO_FIXO_PLANO%>" value="<%=(String)CodedValues.PLANO_PRAZO_FIXO_SIM%>" <%=(String)(hshParamPlano.containsKey(CodedValues.TPP_PRAZO_FIXO_PLANO) &&  hshParamPlano.get(CodedValues.TPP_PRAZO_FIXO_PLANO).equals(CodedValues.PLANO_PRAZO_FIXO_SIM) ? " checked" : "")%> <%=(String)((!podeEditarPla || nplCodigo.equals(NaturezaPlanoEnum.TAXA_USO.getCodigo()))? " disabled" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
              <label class="form-check-label labelSemNegrito ml-1 pr-4" for="codigoSim"><hl:message key="rotulo.sim"/></label>
            </div>
            <div class="form-check form-check-inline pt-2">
              <input class="form-check-input ml-1" type="radio" id="codigoNao" name="tpp_<%=(String)CodedValues.TPP_PRAZO_FIXO_PLANO%>" value="<%=(String)CodedValues.PLANO_PRAZO_FIXO_NAO%>" <%=(String)(hshParamPlano.containsKey(CodedValues.TPP_PRAZO_FIXO_PLANO) && !hshParamPlano.get(CodedValues.TPP_PRAZO_FIXO_PLANO).equals(CodedValues.PLANO_PRAZO_FIXO_SIM) || !hshParamPlano.containsKey(CodedValues.TPP_PRAZO_FIXO_PLANO) ? " checked" : "")%> <%=(String)((!podeEditarPla || nplCodigo.equals(NaturezaPlanoEnum.TAXA_USO.getCodigo()))? " disabled" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">              
              <label class="form-check-label labelSemNegrito ml-1 pr-4" for="codigoNao"><hl:message key="rotulo.nao"/></label>
            </div>
          </div>
        </div>
      </div>
      <div class="row">
        <div class="form-group col-sm-6  col-md-6">
          <label for="tpp_<%=(String)CodedValues.TPP_VLR_PLANO%>"><hl:message key="rotulo.plano.valor"/></label>
          <input class="form-control" name="tpp_<%=(String)CodedValues.TPP_VLR_PLANO%>" type="text" value="<%=TextHelper.forHtmlAttribute(ade_vlr)%>" <%=(String)((!podeEditarPla || nplCodigo.equals(NaturezaPlanoEnum.TAXA_USO.getCodigo()))? "disabled" : "")%> onFocus="SetarEventoMascara(this,'#F12',true);" onBlur="fout(this);ValidaMascara(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }">
        </div>
        <div class="form-group col-sm-3  col-md-3">
          <label for="tpp_<%=(String)CodedValues.TPP_VLR_FIXO_PLANO%>"><hl:message key="rotulo.plano.tipo.valor"/></label>
          <select class="form-control" name="tpp_<%=(String)CodedValues.TPP_VLR_FIXO_PLANO%>" <%=(String)((!podeEditarPla || nplCodigo.equals(NaturezaPlanoEnum.TAXA_USO.getCodigo()))? "disabled" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
            <optgroup>
              <option value="<%=(String)CodedValues.PLANO_VALOR_ALTERAVEL%>" <%=(String)(hshParamPlano.containsKey(CodedValues.TPP_VLR_FIXO_PLANO) &&  !hshParamPlano.get(CodedValues.TPP_VLR_FIXO_PLANO).equals(CodedValues.PLANO_VALOR_PRE_DETERMINADO) || !hshParamPlano.containsKey(CodedValues.TPP_VLR_FIXO_PLANO) ? "SELECTED":"")%>><hl:message key="rotulo.plano.valor.alteravel"/></option>
              <option value="<%=(String)CodedValues.PLANO_VALOR_PRE_DETERMINADO%>" <%=(String)((hshParamPlano.containsKey(CodedValues.TPP_VLR_FIXO_PLANO) &&  hshParamPlano.get(CodedValues.TPP_VLR_FIXO_PLANO).equals(CodedValues.PLANO_VALOR_PRE_DETERMINADO))  ? "SELECTED":"")%>><hl:message key="rotulo.plano.valor.pre.determinado"/></option>
            </optgroup>
          </select>
        </div>
      </div>
      <div class="row">
        <div class="form-group col-sm-6  col-md-6">
        <label for="tipoRateio"><hl:message key="rotulo.plano.tipo.rateio"/></label>
          <div class="form-check pt-2" name="tipoRateio">
            <div class="form-check form-check-inline pt-2">
              <input class="form-check-input ml-1" type="radio" id="codigoRateioSem" name="tpp_<%=(String)CodedValues.TPP_TIPO_RATEIO_PLANO%>" value="<%=(String)CodedValues.PLANO_SEM_RATEIO%>" <%=(String)(hshParamPlano.containsKey(CodedValues.TPP_TIPO_RATEIO_PLANO) && hshParamPlano.get(CodedValues.TPP_TIPO_RATEIO_PLANO).equals(CodedValues.PLANO_SEM_RATEIO)?"CHECKED":"")%> <%=(String)((!podeEditarPla || nplCodigo.equals(NaturezaPlanoEnum.TAXA_USO.getCodigo()))? "disabled" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
              <label class="form-check-label labelSemNegrito ml-1 pr-4" for="codigoRateioSem"><hl:message key="rotulo.plano.sem.rateio"/></label>
            </div>
            <div class="form-check form-check-inline pt-2">
              <input class="form-check-input ml-1" type="radio" id="codigoPermissionario" name="tpp_<%=(String)CodedValues.TPP_TIPO_RATEIO_PLANO%>" value="<%=(String)CodedValues.PLANO_RATEIO_POR_PERMISSIONARIO%>" <%=(String)(hshParamPlano.containsKey(CodedValues.TPP_TIPO_RATEIO_PLANO) &&  hshParamPlano.get(CodedValues.TPP_TIPO_RATEIO_PLANO).equals(CodedValues.PLANO_RATEIO_POR_PERMISSIONARIO)?"CHECKED":"")%> <%=(String)((!podeEditarPla || nplCodigo.equals(NaturezaPlanoEnum.TAXA_USO.getCodigo()))? "disabled" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
              <label class="form-check-label labelSemNegrito ml-1 pr-4" for="codigoPermissionario"><hl:message key="rotulo.plano.por.permissionario"/></label>
            </div>
            <div class="form-check form-check-inline pt-2">
              <input class="form-check-input ml-1" type="radio" id="codigoUnidade"name="tpp_<%=(String)CodedValues.TPP_TIPO_RATEIO_PLANO%>" value="<%=(String)CodedValues.PLANO_RATEIO_POR_UNIDADE%>" <%=(String)(hshParamPlano.containsKey(CodedValues.TPP_TIPO_RATEIO_PLANO) &&  hshParamPlano.get(CodedValues.TPP_TIPO_RATEIO_PLANO).equals(CodedValues.PLANO_RATEIO_POR_UNIDADE)?"CHECKED":"")%> <%=(String)((!podeEditarPla || nplCodigo.equals(NaturezaPlanoEnum.TAXA_USO.getCodigo()))? "disabled" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
              <label class="form-check-label labelSemNegrito ml-1 pr-4" for="codigoUnidade"><hl:message key="rotulo.plano.por.unidade"/></label>
            </div>
          </div>
        </div>
        <div class="form-group col-sm-6  col-md-6">
        <label for="descontoPosto"><hl:message key="rotulo.plano.desconto.posto"/></label>
          <div class="form-check pt-2" name="descontoPosto">
            <div class="form-check form-check-inline pt-2">
              <input class="form-check-input ml-1" type="radio" id="codigoPostoSim" name="tpp_<%=(String)CodedValues.TPP_DESCONTO_POR_POSTO%>" value="<%=(String)CodedValues.PLANO_DESCONTO_POR_POSTO_SIM%>" <%=(String)(hshParamPlano.containsKey(CodedValues.TPP_DESCONTO_POR_POSTO) && hshParamPlano.get(CodedValues.TPP_DESCONTO_POR_POSTO).equals(CodedValues.PLANO_DESCONTO_POR_POSTO_SIM)?"CHECKED":"")%> <%=(String)((!podeEditarPla || nplCodigo.equals(NaturezaPlanoEnum.TAXA_USO.getCodigo()))? "disabled" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
              <label class="form-check-label labelSemNegrito ml-1 pr-4" for="codigoPostoSim"><hl:message key="rotulo.sim"/></label>
            </div>
            <div class="form-check form-check-inline pt-2">
              <input class="form-check-input ml-1" type="radio" id="codigoPostoNao" NAME="tpp_<%=(String)CodedValues.TPP_DESCONTO_POR_POSTO%>" value="<%=(String)CodedValues.PLANO_DESCONTO_POR_POSTO_NAO%>" <%=(String)(hshParamPlano.containsKey(CodedValues.TPP_DESCONTO_POR_POSTO) && hshParamPlano.get(CodedValues.TPP_DESCONTO_POR_POSTO).equals(CodedValues.PLANO_DESCONTO_POR_POSTO_NAO)?"CHECKED":"")%> <%=(String)((!podeEditarPla || nplCodigo.equals(NaturezaPlanoEnum.TAXA_USO.getCodigo()))? "disabled" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
              <label class="form-check-label labelSemNegrito ml-1 pr-4" for="codigoPostoNao"><hl:message key="rotulo.nao"/></label>
            </div>           
          </div>
        </div>
      </div>
      <div class="row">
        <div class="form-group col-sm-6  col-md-6">
          <label for="descontoAutometico"><hl:message key="rotulo.plano.despesa.automatica.ao.incluir.permissionario"/></label>
          <div class="form-check pt-2" id="descontoAutometico">
            <div class="form-check form-check-inline pt-2">
              <input class="form-check-input ml-1" type="radio" id="codigoDescontoAutSim"name="tpp_<%=(String)CodedValues.TPP_DESCONTO_AUTOMATICO%>" value="<%=(String)CodedValues.TPP_SIM%>" <%=(String)(hshParamPlano.containsKey(CodedValues.TPP_DESCONTO_AUTOMATICO) && hshParamPlano.get(CodedValues.TPP_DESCONTO_AUTOMATICO).equals(CodedValues.TPP_SIM)?"CHECKED":"")%> <%=(String)((!podeEditarPla || nplCodigo.equals(NaturezaPlanoEnum.TAXA_USO.getCodigo()))? "disabled" : "")%>  onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
              <label class="form-check-label labelSemNegrito ml-1 pr-4" for="codigoDescontoAutSim"><hl:message key="rotulo.sim"/></label>
            </div>
            <div class="form-check form-check-inline pt-2">
              <input class="form-check-input ml-1" type="radio" id="codigoDescontoAutNao" name="tpp_<%=(String)CodedValues.TPP_DESCONTO_AUTOMATICO%>" value="<%=(String)CodedValues.TPP_NAO%>" <%=(String)(hshParamPlano.containsKey(CodedValues.TPP_DESCONTO_AUTOMATICO) && hshParamPlano.get(CodedValues.TPP_DESCONTO_AUTOMATICO).equals(CodedValues.TPP_NAO)?"CHECKED":"")%> <%=(String)((!podeEditarPla || nplCodigo.equals(NaturezaPlanoEnum.TAXA_USO.getCodigo()))? "disabled" : "")%>  onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
              <label class="form-check-label labelSemNegrito ml-1 pr-4" for="codigoDescontoAutNao"><hl:message key="rotulo.nao"/></label>
            </div>
          </div>         
        </div>
        <div class="form-group col-sm-6  col-md-6">
          <label for="exclusaoAutomatica"><hl:message key="rotulo.plano.exclusao.automatica.ao.excluir.permissionario"/></label>
          <div class="form-check pt-2" id="exclusaoAutomatica">
            <div class="form-check form-check-inline pt-2">
              <input class="form-check-input ml-1" type="radio" id="codigoExclusaoAutSim" name="tpp_<%=(String)CodedValues.TPP_EXCLUSAO_AUTOMATICA%>" value="<%=(String)CodedValues.TPP_SIM%>" <%=(String)(hshParamPlano.containsKey(CodedValues.TPP_EXCLUSAO_AUTOMATICA) && hshParamPlano.get(CodedValues.TPP_EXCLUSAO_AUTOMATICA).equals(CodedValues.TPP_SIM)?"CHECKED":"")%> <%=(String)((!podeEditarPla || nplCodigo.equals(NaturezaPlanoEnum.TAXA_USO.getCodigo()))? "disabled" : "")%>  onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
              <label class="form-check-label labelSemNegrito ml-1 pr-4" for="codigoExclusaoAutSim"><hl:message key="rotulo.sim"/></label>
            </div>
            <div class="form-check form-check-inline pt-2">
              <input class="form-check-input ml-1" type="radio" id="codigoExclusaoAutNao" name="tpp_<%=(String)CodedValues.TPP_EXCLUSAO_AUTOMATICA%>" value="<%=(String)CodedValues.TPP_NAO%>" <%=(String)(hshParamPlano.containsKey(CodedValues.TPP_EXCLUSAO_AUTOMATICA) && hshParamPlano.get(CodedValues.TPP_EXCLUSAO_AUTOMATICA).equals(CodedValues.TPP_NAO)?"CHECKED":"")%> <%=(String)((!podeEditarPla || nplCodigo.equals(NaturezaPlanoEnum.TAXA_USO.getCodigo()))? "disabled" : "")%>  onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
              <label class="form-check-label labelSemNegrito ml-1 pr-4" for="codigoExclusaoAutNao"><hl:message key="rotulo.nao"/></label>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>           

  <div class="btn-action">
  <% if (podeEditarPla) {%>
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/consultarPlanoDesconto?acao=iniciar&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
    <a class="btn btn-primary" href="#no-back" onClick="enviar(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
  <%} else {%>  
    <a class="btn btn-primary" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
  <%} %>
  
  <%if (!TextHelper.isNull(plaCodigo)) {%>
      <hl:htmlinput name="plaCodigo"
        type="hidden"
        value="<%=TextHelper.forHtmlAttribute(plaCodigo)%>"
      /> 
      <hl:htmlinput name="MM_update"
        type="hidden"
        value="form1"
      />
      <hl:htmlinput name="svcCodigoHidden"
        type="hidden"
        value="<%=TextHelper.forHtmlAttribute(svcCodigo )%>"
      />
      <hl:htmlinput name="nplCodigoHidden"
        type="hidden"
        value="<%=TextHelper.forHtmlAttribute(nplCodigo )%>"
      />        
    <%} else { %>
      <hl:htmlinput name="MM_insert"
        type="hidden"
        value="form1"
      />
    <%} %>
    <hl:htmlinput name="tipo"
      type="hidden"
      value="editar"
    />
    <hl:htmlinput name="_skip_history_" di="_skip_history_" type="hidden" value="true"/>
  </div> 
 </form>      
</c:set>

<c:set var="javascript">
  <script type="text/JavaScript" src="../js/scripts_2810.js?<hl:message key="release.tag"/>"></script>
  <script type="text/JavaScript" src="../js/validaform.js?<hl:message key="release.tag"/>"></script>
  <script type="text/JavaScript" src="../js/validacoes.js?<hl:message key="release.tag"/>"></script>

  <script type="text/JavaScript">
    function formLoad() {     
       focusFirstField();
    }
    
    function mudaPrazo() {
        var selecao =  f0.prazo.selectedIndex;
        if (f0.prazo.selectedIndex == '0') {
          f0.tpp_<%=(String)CodedValues.TPP_PRAZO_MAX_PLANO%>.value = '0';
          f0.tpp_<%=(String)CodedValues.TPP_PRAZO_FIXO_PLANO%>[1].checked = true;
          f0.tpp_<%=(String)CodedValues.TPP_PRAZO_FIXO_PLANO%>[0].disabled = true;
        } else if (f0.prazo.selectedIndex == '-1' || f0.prazo.selectedIndex == '2') {
          f0.tpp_<%=(String)CodedValues.TPP_PRAZO_MAX_PLANO%>.value = '';
          f0.tpp_<%=(String)CodedValues.TPP_PRAZO_FIXO_PLANO%>[1].checked = true;
          f0.tpp_<%=(String)CodedValues.TPP_PRAZO_FIXO_PLANO%>[0].disabled = true;        
        } else if (f0.prazo.selectedIndex == '1') {
          f0.tpp_<%=(String)CodedValues.TPP_PRAZO_FIXO_PLANO%>[0].disabled = false;
        }
      
    }
  
    function mudaNatureza() {
      var selecao = f0.nplCodigo.selectedIndex;
      if (selecao == <%=TextHelper.forJavaScriptBlock(NaturezaPlanoEnum.TAXA_USO.getCodigo())%>) {  
        f0.tpp_<%=(String)CodedValues.TPP_PRAZO_MAX_PLANO%>.value = '';
        f0.tpp_<%=(String)CodedValues.TPP_PRAZO_MAX_PLANO%>.disabled = true;
        f0.prazo.value = '-1';
        f0.prazo.disabled = true;
        for (var i=0; i<f0.tpp_<%=(String)CodedValues.TPP_PRAZO_FIXO_PLANO%>.length; i++){
          f0.tpp_<%=(String)CodedValues.TPP_PRAZO_FIXO_PLANO%>[i].checked = false;
              f0.tpp_<%=(String)CodedValues.TPP_PRAZO_FIXO_PLANO%>[i].disabled = true;  
        }
        f0.tpp_<%=(String)CodedValues.TPP_VLR_PLANO%>.value = '';
        f0.tpp_<%=(String)CodedValues.TPP_VLR_PLANO%>.disabled = true;
        for (var i=0; i<f0.tpp_<%=(String)CodedValues.TPP_DESCONTO_POR_POSTO%>.length; i++){
          f0.tpp_<%=(String)CodedValues.TPP_DESCONTO_POR_POSTO%>[i].checked = false;
          f0.tpp_<%=(String)CodedValues.TPP_DESCONTO_POR_POSTO%>[i].disabled = true;
        }
        for (var i=0; i<f0.tpp_<%=(String)CodedValues.TPP_DESCONTO_AUTOMATICO%>.length; i++){
          f0.tpp_<%=(String)CodedValues.TPP_DESCONTO_AUTOMATICO%>[i].checked = false;
          f0.tpp_<%=(String)CodedValues.TPP_DESCONTO_AUTOMATICO%>[i].disabled = true;
        }
        f0.tpp_<%=(String)CodedValues.TPP_VLR_FIXO_PLANO%>.value = '1';
        f0.tpp_<%=(String)CodedValues.TPP_VLR_FIXO_PLANO%>.disabled = true;
        for (var i=0; i<f0.tpp_<%=(String)CodedValues.TPP_TIPO_RATEIO_PLANO%>.length; i++){ 
          f0.tpp_<%=(String)CodedValues.TPP_TIPO_RATEIO_PLANO%>[i].checked = false;
          f0.tpp_<%=(String)CodedValues.TPP_TIPO_RATEIO_PLANO%>[i].disabled = true;
        }   
      } else {
        f0.tpp_<%=(String)CodedValues.TPP_PRAZO_MAX_PLANO%>.disabled = false;
        f0.prazo.disabled = false;
        for (var i=0; i<f0.tpp_<%=(String)CodedValues.TPP_PRAZO_FIXO_PLANO%>.length; i++){        
              f0.tpp_<%=(String)CodedValues.TPP_PRAZO_FIXO_PLANO%>[i].disabled = false; 
        }
        f0.tpp_<%=(String)CodedValues.TPP_VLR_PLANO%>.disabled = false;
        for (var i=0; i<f0.tpp_<%=(String)CodedValues.TPP_DESCONTO_POR_POSTO%>.length; i++){
          f0.tpp_<%=(String)CodedValues.TPP_DESCONTO_POR_POSTO%>[i].disabled = false;
        }
        for (var i=0; i<f0.tpp_<%=(String)CodedValues.TPP_DESCONTO_AUTOMATICO%>.length; i++){
          f0.tpp_<%=(String)CodedValues.TPP_DESCONTO_AUTOMATICO%>[i].disabled = false;
        }
        f0.tpp_<%=(String)CodedValues.TPP_VLR_FIXO_PLANO%>.disabled = false;
        for (var i=0; i<f0.tpp_<%=(String)CodedValues.TPP_TIPO_RATEIO_PLANO%>.length; i++){ 
          f0.tpp_<%=(String)CodedValues.TPP_TIPO_RATEIO_PLANO%>[i].disabled = false;
        }
      }
    }
    
    function enviar() {
      f0.submit();
    }
  </script>
    <script type="text/JavaScript">
    f0 = document.forms[0];
  </script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>