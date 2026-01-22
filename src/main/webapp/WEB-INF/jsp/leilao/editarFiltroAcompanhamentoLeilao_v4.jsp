<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper"%>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants"%>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper"%>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
    AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
    
    String email = (String) request.getAttribute("email");
    String filtro = (String) request.getAttribute("filtro");
    String dataAberturaIni = (String) request.getAttribute("dataAberturaIni");
    String dataAberturaFim = (String) request.getAttribute("dataAberturaFim");
    String rsePontuacaoFiltro = (String) request.getAttribute("rsePontuacaoFiltro");
    String arrRiscoFiltro = (String) request.getAttribute("arrRiscoFiltro");
    String posCodigo = (String) request.getAttribute("posCodigo");
    String rseMargemLivreFiltro = (String) request.getAttribute("rseMargemLivreFiltro");
    boolean temRiscoPelaCsa = (Boolean) request.getAttribute("temRiscoPelaCsa");
    boolean desabilitado = (Boolean) request.getAttribute("desabilitado");
    List<TransferObject> postos = (List<TransferObject>) request.getAttribute("postos");
%>
<link rel="stylesheet" href="../css/impromptu.css?<hl:message key="release.tag"/>" type="text/css">
<c:set var="imageHeader">
    <use xlink:href="#i-mensagem"></use>
</c:set>
<c:set var="title">
   <hl:message key="rotulo.acompanhamento.leilao.solicitacao.titulo"/>
</c:set>
<c:set var="bodyContent">
<form action="../v3/acompanharLeilao?<%=SynchronizerToken.generateToken4URL(request)%>" method="post" name="form1">
  <input type="hidden" name="acao" value="salvarFiltro" />      
  <%if (!responsavel.isSer()) {%>
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.filtro.leilao.solicitacao.inclusao"/></h2>
    </div>
    <div class="card-body">
      <div class="row">
        <div class="form-group col-sm-12  col-md-6">
          <label for="descricao"><hl:message key="rotulo.filtro.leilao.solicitacao.descricao"/></label>
          <hl:htmlinput name="descricao" 
                        di="descricao" 
                        type="text" 
                        classe="form-control"
                        size="32"
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.descricao", responsavel) %>"
                        maxlength="100"
                        value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "descricao"))%>"
        />
        </div>
      </div>
      <div class="row">
        <div class="form-group col-sm-12  col-md-6">
          <label for="email"><hl:message key="rotulo.filtro.leilao.solicitacao.email.notificacao"/></label>
          <hl:htmlinput name="email" 
                        di="email" 
                        type="text" 
                        classe="form-control"
                        size="32"
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.email", responsavel) %>"
                        maxlength="100"
                        value="<%=TextHelper.forHtmlAttribute(email)%>"
        />
        </div>
      </div>
      <div class="row">
        <div class="col-sm-12 col-md-6">
          <div class="form-group mb-1" role="radiogroup" aria-labelledby="statusDaProposta">
            <span id="statusDaProposta"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.proposta.status"/></span>
            <div class="form-check pt-3">
              <div class="form-check">
                <input class="form-check-input ml-1" type="radio" name="filtro" id="filtro0" value="0" <%=(String)(filtro.equals("") || filtro.equals("0") ? "checked" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                <label class="form-check-label labelSemNegrito col-md-12 col-sm-12 ml-1 pr-4" for="filtro0"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.proposta.pendente"/></label>
              </div>
              <div class="form-check">
                <input class="form-check-input ml-1" type="radio" name="filtro" id="filtro1" value="1" <%=(String)(filtro.equals("1") ? "checked" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                <label class="form-check-label labelSemNegrito col-md-12 col-sm-12 ml-1 pr-4" for="filtro1"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.proposta.informada"/></label>
              </div>
              <div class="form-check">
                <input class="form-check-input ml-1" type="radio" name="filtro" id="filtro2" value="1" <%=(String)(filtro.equals("2") ? "checked" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                <label class="form-check-label labelSemNegrito col-md-12 col-sm-12 ml-1 pr-4" for="filtro2"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.proposta.vencedora"/></label>
              </div>
              <div class="form-check">
                <input class="form-check-input ml-1" type="radio" name="filtro" id="filtro3" value="1" <%=(String)(filtro.equals("3") ? "checked" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                <label class="form-check-label labelSemNegrito col-md-12 col-sm-12 ml-1 pr-4" for="filtro3"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.proposta.perdedora"/></label>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="row mt-3">
        <div class="form-group col-sm-12 col-md-6">
          <label for="dataDeAbertura" class="labelSemNegrito"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.data.abertura"/></label>
          <div class="row mt-2" role="group" aria-labelledby="dataDeAbertura">
            <div class="form-check pt-2 col-sm-12 col-md-1">
              <div class="float-left align-middle mt-4 form-control-label">
                <label for="dataAberturaIni" class="labelSemNegrito"><hl:message key="rotulo.data.de"/></label>
              </div>
            </div>
            <div class="form-check pt-2 col-sm-12 col-md-5">
              <hl:htmlinput name="dataAberturaIni" 
                            di="dataAberturaIni" 
                            type="text" 
                            classe="form-control" 
                            size="10" 
                            mask="<%=LocaleHelper.getDateJavascriptPattern()%>" 
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.inicial", responsavel) %>"
                            value="<%=TextHelper.forHtmlAttribute(dataAberturaIni)%>" 
              />
            </div>
            <div class="form-check pt-2 col-sm-12 col-md-1">
              <div class="float-left align-middle mt-4 form-control-label">
                <label for="dataAberturaFim" class="labelSemNegrito"><hl:message key="rotulo.data.ate"/></label>
              </div>
            </div>
            <div class="form-check pt-2 col-sm-12 col-md-5">
              <hl:htmlinput name="dataAberturaFim" 
                            di="dataAberturaFim" 
                            type="text" 
                            classe="form-control" 
                            size="10" 
                            mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.final", responsavel) %>" 
                            value="<%=TextHelper.forHtmlAttribute(dataAberturaFim)%>" 
              />
            </div>
          </div>
        </div>
      </div>       
      <div class="row">
        <div class="form-group col-sm-12  col-md-4">
          <label for="horasFimLeilao"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.horas.restantes.fim.leilao"/></label>
          <hl:htmlinput name="horasFimLeilao" 
                        di="horasFimLeilao" 
                        type="text" 
                        classe="form-control"
                        mask="#D3" 
                        size="2"
                        maxlength="3"
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.horas", responsavel) %>"
                        value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "horasFimLeilao"))%>"
          />
        </div>
      </div>
      <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.FILTRO_PESQUISA_POSTO_SERVIDOR)%>" >
      <%
      if (!responsavel.isSer()) {//mostra combo para selecao de posto do servidor                    
         %>
        <div class="row">
          <div class="form-group col-sm-12  col-md-6">
            <label for="posto"><hl:message key="rotulo.servidor.posto"/></label>
            <%if (TextHelper.isNull(posCodigo) && !desabilitado) { %>
              <%=JspHelper.geraCombo(postos, FieldKeysConstants.FILTRO_PESQUISA_POSTO_SERVIDOR, Columns.POS_CODIGO,  Columns.POS_IDENTIFICADOR + ";" + Columns.POS_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavel), null, false, 1, null, null, false, "form-control")%>
            <%} else if (!TextHelper.isNull(posCodigo)  && !desabilitado) { %>
              <%=JspHelper.geraCombo(postos, FieldKeysConstants.FILTRO_PESQUISA_POSTO_SERVIDOR, Columns.POS_CODIGO,  Columns.POS_IDENTIFICADOR + ";" + Columns.POS_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavel), null, false, 1, posCodigo, null, false, "form-control")%>
            <%} else if (desabilitado) {%>
              <%=JspHelper.geraCombo(postos, FieldKeysConstants.FILTRO_PESQUISA_POSTO_SERVIDOR, Columns.POS_CODIGO,  Columns.POS_IDENTIFICADOR + ";" + Columns.POS_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavel), null, false, 1, null, null, true, "form-control")%>
            <%} %> 
          </div>
        </div>
      <%} %>
      </show:showfield>      
      <div class="row">
        <div class="form-group col-sm-12  col-md-6">
          <label for="RSE_PONTUACAO"><hl:message key="rotulo.servidor.pontuacao.minima"/></label>
          <select class="form-control form-select" name="RSE_PONTUACAO" id="RSE_PONTUACAO">
            <option <%=rsePontuacaoFiltro.equals("0") ? "SELECTED" : ""%> value="0"><hl:message key="rotulo.servidor.pontuacao.0.20"/></option>
            <option <%=rsePontuacaoFiltro.equals("21")? "SELECTED" : ""%> value="21"><hl:message key="rotulo.servidor.pontuacao.21.40"/></option>
            <option <%=rsePontuacaoFiltro.equals("41")? "SELECTED" : ""%> value="41"><hl:message key="rotulo.servidor.pontuacao.41.60"/></option>
            <option <%=rsePontuacaoFiltro.equals("61")? "SELECTED" : ""%> value="61"><hl:message key="rotulo.servidor.pontuacao.61.80"/></option>
            <option <%=rsePontuacaoFiltro.equals("81")? "SELECTED" : ""%> value="81"><hl:message key="rotulo.servidor.pontuacao.81.100"/></option>
          </select>
        </div>
      </div>
      <%if (responsavel.isCsa() && temRiscoPelaCsa) {%>       
      <div class="row">
        <div class="form-group col-sm-12  col-md-6">
          <label for="ARR_RISCO"><hl:message key="rotulo.servidor.risco.csa"/></label>
          <select class="form-control form-select" name="ARR_RISCO" id="ARR_RISCO">
            <option <%=arrRiscoFiltro.equals("4") ? "SELECTED" : ""%> value="4"><hl:message key="rotulo.servidor.risco.csa.altissimo"/></option>
            <option <%=arrRiscoFiltro.equals("3") ? "SELECTED" : ""%> value="3"><hl:message key="rotulo.servidor.risco.csa.alto"/></option>
            <option <%=arrRiscoFiltro.equals("2") ? "SELECTED" : ""%> value="2"><hl:message key="rotulo.servidor.risco.csa.medio"/></option>
            <option <%=arrRiscoFiltro.equals("1") ? "SELECTED" : ""%> value="1"><hl:message key="rotulo.servidor.risco.csa.baixo"/></option>
            <option <%=arrRiscoFiltro.equals("0") ? "SELECTED" : ""%> value="0"><hl:message key="rotulo.servidor.risco.csa.baixissimo"/></option>
          </select>
        </div>
      </div>         
      <%}%>        
      <div class="row">
        <div class="form-group col-sm-12  col-md-6">
          <label for="margemLivre"><hl:message key="rotulo.servidor.variacao.margem.livre"/></label>
          <select class="form-control form-select" name="RSE_MARGEM_LIVRE" id="RSE_MARGEM_LIVRE">
            <option <%=TextHelper.isNull(rseMargemLivreFiltro) ? "SELECTED" : ""%> value=""><hl:message key="rotulo.campo.todas"/></option>
            <option <%=rseMargemLivreFiltro.equals("10") ? "SELECTED" : ""%> value="10"><hl:message key="rotulo.servidor.variacao.margem.livre.0.10"/></option>
            <option <%=rseMargemLivreFiltro.equals("20") ? "SELECTED" : ""%> value="20"><hl:message key="rotulo.servidor.variacao.margem.livre.11.20"/></option>
            <option <%=rseMargemLivreFiltro.equals("30") ? "SELECTED" : ""%> value="30"><hl:message key="rotulo.servidor.variacao.margem.livre.21.30"/></option>
            <option <%=rseMargemLivreFiltro.equals("40") ? "SELECTED" : ""%> value="40"><hl:message key="rotulo.servidor.variacao.margem.livre.31.40"/></option>
            <option <%=rseMargemLivreFiltro.equals("50") ? "SELECTED" : ""%> value="50"><hl:message key="rotulo.servidor.variacao.margem.livre.41.50"/></option>
            <option <%=rseMargemLivreFiltro.equals("60") ? "SELECTED" : ""%> value="60"><hl:message key="rotulo.servidor.variacao.margem.livre.51.60"/></option>
            <option <%=rseMargemLivreFiltro.equals("70") ? "SELECTED" : ""%> value="70"><hl:message key="rotulo.servidor.variacao.margem.livre.61.70"/></option>
            <option <%=rseMargemLivreFiltro.equals("80") ? "SELECTED" : ""%> value="80"><hl:message key="rotulo.servidor.variacao.margem.livre.71.80"/></option>
            <option <%=rseMargemLivreFiltro.equals("90") ? "SELECTED" : ""%> value="90"><hl:message key="rotulo.servidor.variacao.margem.livre.81.90"/></option>
            <option <%=rseMargemLivreFiltro.equals("100")? "SELECTED" : ""%> value="100"><hl:message key="rotulo.servidor.variacao.margem.livre.91.100"/></option>
          </select>
        </div>
      </div>         
      <%-- Inclui o campo de matrícula --%>
      <%@ include file="../consultarMargem/include_campo_matricula_v4.jsp" %>
      <div class="row">
        <div class="form-group col-sm-12  col-md-6">
          <hl:campoCPFv4 nf="btnSalvar" placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.cpf", responsavel)%>' classe="form-control"/>
        </div>
      </div>
      <%--Inclui o campo cidade/uf --%>
      <% request.setAttribute("NSE_CODIGO",CodedValues.NSE_EMPRESTIMO); %>
      <%@ include file="../cidade/include_campo_cidade_uf_v4.jsp"%> 
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/acompanharLeilao?acao=iniciar'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
    <a class="btn btn-primary" id="btnSalvar" href="#no-back" onClick="validaSubmit(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
  </div>
  <% } %>
</form>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
  var f0 = document.forms[0];
  
  // valida o formulário antes do envio do submit
  function validForm() {
    $("#descricao").removeClass("invalid");
    $("#email").removeClass("invalid");
    with (f0) {
      if (descricao != null
          && descricao.value.trim() == '') {
        alert('<hl:message key="mensagem.campos.obrigatorios"/>');
        $("#descricao").addClass("invalid");
        descricao.focus();
        return false;
      }
      if (email != null && email.value.trim() == '') {
        alert('<hl:message key="mensagem.campos.obrigatorios"/>');
        $("#email").addClass("invalid");
        email.focus();
        return false;
      }
      if (email != null && email.value.trim() != ''
          && !isEmailValid(email.value)) {
        alert('<hl:message key="mensagem.informe.email.valido"/>');
        $("#email").addClass("invalid");
        email.focus();
        return false;
      }
    }
    return true;
  }
  
  function validaSubmit()
      {
          if( validForm() )
          { 
            if(typeof vfRseMatricula === 'function')
            {
              if(vfRseMatricula(true))
              {
                f0.submit();
              }
            }
            else
            {
              f0.submit();
            } 
          }
      }
</script>
</c:set>
<%-- Leiaute Fixo --%>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>