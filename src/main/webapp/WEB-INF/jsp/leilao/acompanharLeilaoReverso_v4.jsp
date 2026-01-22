<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper"%>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants"%>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.web.AcompanharLeilaoModel" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

AcompanharLeilaoModel acompanharLeilaoModel = (AcompanharLeilaoModel) request.getAttribute("acompanharLeilaoModel");
boolean temRiscoPelaCsa = acompanharLeilaoModel.isTemRiscoPelaCsa();
List<TransferObject> lstResultado = acompanharLeilaoModel.getLstResultado();
%>
<link rel="stylesheet" href="../css/impromptu.css?<hl:message key="release.tag"/>" type="text/css">
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="title">
   <hl:message key="rotulo.acompanhamento.leilao.solicitacao.titulo"/>
</c:set>
<c:set var="bodyContent">
<form action="../v3/acompanharLeilao" method="post" name="form1">      
  <input type="hidden" name="acao" value="pesquisar" />
  <%out.print(SynchronizerToken.generateHtmlToken(request));%>
  <% if (responsavel.isCsaCor()) { %>
  <div class="page-title">
    <div class="row d-print-none">
      <div class="col-sm-12 col-md-12 mb-2">
        <div class="float-end">
          <button data-bs-toggle="dropdown" aria-haspopup="true" id="acoes" aria-expanded="false" class="btn btn-primary" type="submit"><hl:message key="rotulo.mais.acoes"/></button>
          <div class="dropdown-menu dropdown-menu-right" aria-labelledby="acoes" x-placement="bottom-end" style="position: absolute; transform: translate3d(1009px, 50px, 0px); top: 0px; left: 0px; will-change: transform;">
            <a class="dropdown-item" id="btnListar" href="#no-back" onclick="postData('../v3/acompanharLeilao?acao=visualizarFiltro&<%=SynchronizerToken.generateToken4URL(request)%>')"><hl:message key="rotulo.filtro.leilao.solicitacao.visualizar.filtros"/></a>
            <a class="dropdown-item" id="btnSalvar" href="#no-back" onclick="document.forms[0].action='../v3/acompanharLeilao?acao=iniciarCriacaoFiltro'; document.forms[0].submit();"><hl:message key="rotulo.filtro.leilao.solicitacao.salvar.filtro"/></a>
          </div>
        </div>
      </div>
    </div>
  </div>      
  <% } %>      
  <%if (!responsavel.isSer()) {%>
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><a class="card-header-title" href="#filtros" id="filtrosLink" data-bs-toggle="collapse" aria-expanded="false" aria-controls="filtros" aria-label='<hl:message key="mensagem.informe.opcoes.pesquisa"/>'><hl:message key="mensagem.informe.opcoes.pesquisa"/></a></h2>
    </div>
    <div class="card-body collapse" id="filtros">
      <% if (responsavel.isCsaCor()) { %>
        <div class="row">
          <div class="col-sm-12 col-md-6">
            <div class="form-group mb-1" role="radiogroup" aria-labelledby="tipoLeilao">
              <span id="tipoLeilao"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.tipo"/></span>
              <div class="form-check pt-3">
                <div class="form-check">
                  <input class="form-check-input ml-1" type="radio" name="tipo" id="leilaoTodos" value="0" <%=(String)(acompanharLeilaoModel.getTipo().equals("") || acompanharLeilaoModel.getTipo().equals("0") ? "checked" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                  <label class="form-check-label labelSemNegrito col-md-12 col-sm-12 ml-1 pr-4" for="leilaoTodos"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.tipo.todos"/></label>
                </div>
                <div class="form-check">
                  <input class="form-check-input ml-1" type="radio" name="tipo" id="leilaoNovo" value="1" <%=(String)(acompanharLeilaoModel.getTipo().equals("1") ? "checked" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                  <label class="form-check-label labelSemNegrito col-md-12 col-sm-12 ml-1 pr-4" for="leilaoNovo"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.tipo.novo"/></label>
                </div>
                <div class="form-check">
                  <input class="form-check-input ml-1" type="radio" name="tipo" id="leilaoPortabilidade" value="2" <%=(String)(acompanharLeilaoModel.getTipo().equals("2") ? "checked" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                  <label class="form-check-label labelSemNegrito col-md-12 col-sm-12 ml-1 pr-4" for="leilaoPortabilidade"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.tipo.portabilidade"/></label>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="row">
          <div class="col-sm-12 col-md-6">
            <div class="form-group mb-1" role="radiogroup" aria-labelledby="statusDaProposta">
              <span id="statusDaProposta"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.proposta.status"/></span>
              <div class="form-check pt-3">
                <div class="form-check">
                  <input class="form-check-input ml-1" type="radio" name="filtro" id="propostaNaoInformada" value="0" <%=(String)(acompanharLeilaoModel.getFiltro().equals("") || acompanharLeilaoModel.getFiltro().equals("0") ? "checked" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                  <label class="form-check-label labelSemNegrito col-md-12 col-sm-12 ml-1 pr-4" for="propostaNaoInformada"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.proposta.pendente"/></label>
                </div>
                <div class="form-check">
                  <input class="form-check-input ml-1" type="radio" name="filtro" id="propostaJaInformada" value="1" <%=(String)(acompanharLeilaoModel.getFiltro().equals("1") ? "checked" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                  <label class="form-check-label labelSemNegrito col-md-12 col-sm-12 ml-1 pr-4" for="propostaJaInformada"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.proposta.informada"/></label>
                </div>
                <div class="form-check">
                  <input class="form-check-input ml-1" type="radio" name="filtro" id="propostaVencedora" value="2" <%=(String)(acompanharLeilaoModel.getFiltro().equals("2") ? "checked" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                  <label class="form-check-label labelSemNegrito col-md-12 col-sm-12 ml-1 pr-4" for="propostaVencedora"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.proposta.vencedora"/></label>
                </div>
                <div class="form-check">
                  <input class="form-check-input ml-1" type="radio" name="filtro" id="propostaPerdedora" value="3" <%=(String)(acompanharLeilaoModel.getFiltro().equals("3") ? "checked" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                  <label class="form-check-label labelSemNegrito col-md-12 col-sm-12 ml-1 pr-4" for="propostaPerdedora"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.proposta.perdedora"/></label>
                </div>
              </div>
            </div>
          </div>
        </div>
      <% } %>
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
              <hl:htmlinput name="dataAberturaIni" di="dataAberturaIni" type="text" classe="form-control" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" value="<%=TextHelper.forHtmlAttribute(acompanharLeilaoModel.getDataAberturaIni())%>" />
            </div>
            <div class="form-check pt-2 col-sm-12 col-md-1">
              <div class="float-left align-middle mt-4 form-control-label">
                <label for="dataAberturaFim" class="labelSemNegrito"><hl:message key="rotulo.data.ate"/></label>
              </div>
            </div>
            <div class="form-check pt-2 col-sm-12 col-md-5">
              <hl:htmlinput name="dataAberturaFim" di="dataAberturaFim" type="text" classe="form-control" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" value="<%=TextHelper.forHtmlAttribute(acompanharLeilaoModel.getDataAberturaFim())%>" />
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
                        value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "horasFimLeilao"))%>"
          />
        </div>
      </div>
      <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.FILTRO_PESQUISA_POSTO_SERVIDOR)%>" >
        <%
        boolean desabilitado = !ShowFieldHelper.canEdit(FieldKeysConstants.FILTRO_PESQUISA_POSTO_SERVIDOR, responsavel);
        String posCodigo = JspHelper.verificaVarQryStr(request, FieldKeysConstants.FILTRO_PESQUISA_POSTO_SERVIDOR);
        
        if (!responsavel.isSer()) {//mostra combo para selecao de posto do servidor                    
        %>
          <div class="row">
            <div class="form-group col-sm-12  col-md-6">
              <label for="posto"><hl:message key="rotulo.servidor.posto"/></label>
              <%if (TextHelper.isNull(posCodigo) && !desabilitado) { %>
                <%=JspHelper.geraCombo(acompanharLeilaoModel.getPostos(), FieldKeysConstants.FILTRO_PESQUISA_POSTO_SERVIDOR, Columns.POS_CODIGO,  Columns.POS_IDENTIFICADOR + ";" + Columns.POS_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavel), null, false, 1, null, null, false, "form-control")%>
              <%} else if (!TextHelper.isNull(posCodigo)  && !desabilitado) { %>
                <%=JspHelper.geraCombo(acompanharLeilaoModel.getPostos(), FieldKeysConstants.FILTRO_PESQUISA_POSTO_SERVIDOR, Columns.POS_CODIGO,  Columns.POS_IDENTIFICADOR + ";" + Columns.POS_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavel), null, false, 1, posCodigo, null, false, "form-control")%>
              <%} else if (desabilitado) {%>
                <%=JspHelper.geraCombo(acompanharLeilaoModel.getPostos(), FieldKeysConstants.FILTRO_PESQUISA_POSTO_SERVIDOR, Columns.POS_CODIGO,  Columns.POS_IDENTIFICADOR + ";" + Columns.POS_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavel), null, false, 1, null, null, true, "form-control")%>
              <%} %>                 
            </div>
          </div>                
        <%} %>
      </show:showfield>    
      <div class="row">
        <div class="form-group col-sm-12  col-md-6">
          <label for="RSE_PONTUACAO"><hl:message key="rotulo.servidor.pontuacao.minima"/></label>
          <select class="form-control form-select" name="RSE_PONTUACAO" id="RSE_PONTUACAO">
             <%String rsePontuacaoFiltro = acompanharLeilaoModel.getRsePontuacaoFiltro(); %>
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
            <label for="ARR_RISCO"><hl:message key="rotulo.servidor.risco.csa.minimo"/></label>
            <select class="form-control form-select" name="ARR_RISCO" id="ARR_RISCO">
              <%String arrRiscoFiltro = acompanharLeilaoModel.getArrRiscoFiltro(); %>
              <option <%=arrRiscoFiltro.equals("4") ? "SELECTED" : ""%> value="4"><hl:message key="rotulo.servidor.risco.csa.altissimo"/></option>
              <option <%=arrRiscoFiltro.equals("3") ? "SELECTED" : ""%> value="3"><hl:message key="rotulo.servidor.risco.csa.alto"/></option>
              <option <%=arrRiscoFiltro.equals("2") ? "SELECTED" : ""%> value="2"><hl:message key="rotulo.servidor.risco.csa.medio"/></option>
              <option <%=arrRiscoFiltro.equals("1") ? "SELECTED" : ""%> value="1"><hl:message key="rotulo.servidor.risco.csa.baixo"/></option>
              <option <%=arrRiscoFiltro.equals("0") ? "SELECTED" : ""%> value="0"><hl:message key="rotulo.servidor.risco.csa.baixissimo"/></option>
            </select>
          </div>
        </div>
      <%}%>    
      <%if (!responsavel.isSer()) {%>
      <div class="row">
        <div class="form-group col-sm-12  col-md-6">
          <label for="RSE_MARGEM_LIVRE"><hl:message key="rotulo.servidor.variacao.margem.livre"/></label>
          <select class="form-control form-select" name="RSE_MARGEM_LIVRE" id="RSE_MARGEM_LIVRE">
            <%String rseMargemLivreFiltro = acompanharLeilaoModel.getRseMargemLivreFiltro(); %>
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
      <% } %> 
      <%-- Inclui o campo de matrícula --%>
      <div class="row">
        <div class="form-group col-sm-12  col-md-6">
          <%@ include file="../consultarMargem/include_campo_matricula_v4.jsp" %>
        </div>
      </div>
      <div class="row">
        <div class="form-group col-sm-12  col-md-6">
          <hl:campoCPFv4 nf="btnEnvia" placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.cpf", responsavel)%>' classe="form-control"/>
        </div>
      </div>
     
      <%
        String ordenacaoAux = (String) JspHelper.verificaVarQryStr(request, "ORDENACAO");
        List<String> lstOrdenacaoAux = new ArrayList<>();
        if (!TextHelper.isNull(ordenacaoAux)) {
            lstOrdenacaoAux = Arrays.asList(ordenacaoAux.split(","));
        }
        
        String ordemDataAbertura = (String) JspHelper.verificaVarQryStr(request, "ORDEM_DATA_ABERTURA");
        String ordemDataFechamento = (String) JspHelper.verificaVarQryStr(request, "ORDEM_DATA_FECHAMENTO");
        String ordemServidor = (String) JspHelper.verificaVarQryStr(request, "ORDEM_SERVIDOR");
        String ordemCidadeUf = (String) JspHelper.verificaVarQryStr(request, "ORDEM_CIDADE_UF");
        String ordemAdeNumero = (String) JspHelper.verificaVarQryStr(request, "ORDEM_ADE_NUMERO");
        String ordemValorLiberado = (String) JspHelper.verificaVarQryStr(request, "ORDEM_VALOR_LIBERADO");
        String ordemValorPrestacao = (String) JspHelper.verificaVarQryStr(request, "ORDEM_PRESTACAO");
        
        String ordemNumPrestacao = (String) JspHelper.verificaVarQryStr(request, "ORDEM_PRAZO");
        String ordemPontuacao = (String) JspHelper.verificaVarQryStr(request, "ORDEM_PONTUACAO");
        String ordemTaxaInformada = (String) JspHelper.verificaVarQryStr(request, "ORDEM_TAXA_INFORMADA");
        String ordemMelhorTaxa = (String) JspHelper.verificaVarQryStr(request, "ORDEM_MELHOR_TAXA");
        String ordemMargemLivre = (String) JspHelper.verificaVarQryStr(request, "ORDEM_MARGEM_LIVRE");
        String ordemRiscoServidor = (String) JspHelper.verificaVarQryStr(request, "ORDEM_RISCO_SERVIDOR");
        
      %>
      <div class="row">
        <div class="form-group col-sm-12 col-md-5">
          <label for="ORDENACAO"><hl:message key="rotulo.ordenacao"/></label>
          <select class="form-control form-select w-100" NAME="ORDENACAO" ID="ORDENACAO" SIZE="10">
            <%
            // Se existir uma ordem já pré selecionada para a ordenação, monta a lista na ordem
            if (lstOrdenacaoAux != null && !lstOrdenacaoAux.isEmpty()) {
                Iterator<String> iteOrdAux = lstOrdenacaoAux.iterator();
                String ordAux = "";
                while (iteOrdAux.hasNext()) {
                    ordAux = iteOrdAux.next();
                    ordAux = ordAux.replaceAll("ASC|DESC", "").replaceAll("\\[|\\]|;", "").trim();
                    if (ordAux.equalsIgnoreCase("ORD01")) {
                      %>
                        <OPTION VALUE="ORD01"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.data.abertura"/></OPTION>
                      <%
                    } else if (ordAux.equalsIgnoreCase("ORD02")) {
                      %>
                        <OPTION VALUE="ORD02"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.data.fechamento"/></OPTION>
                      <%
                    } else if (ordAux.equalsIgnoreCase("ORD03")) {
                      %>
                        <OPTION VALUE="ORD03"><hl:message key="rotulo.servidor.singular"/></OPTION>
                      <%
                    } else if (ordAux.equalsIgnoreCase("ORD04")) {
                      %>
                        <OPTION VALUE="ORD04"><hl:message key="rotulo.endereco.cidade.uf"/></OPTION>
                      <%
                  	} else if (ordAux.equalsIgnoreCase("ORD05")) {
                      %>
                        <OPTION VALUE="ORD05"><hl:message key="rotulo.consignacao.numero"/></OPTION>
                      <%
                    } else if (ordAux.equalsIgnoreCase("ORD06")) {
                      %>
                        <OPTION VALUE="ORD06"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.valor.liberado"/></OPTION>
                      <%
                    } else if (ordAux.equalsIgnoreCase("ORD07")) {
                      %>
                        <OPTION VALUE="ORD07"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.valor.prestacao"/></OPTION>
                      <%
                    } else if (ordAux.equalsIgnoreCase("ORD08")) {
                      %>
                        <OPTION VALUE="ORD08"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.prazo"/></OPTION>
                      <%
                    } else if (ordAux.equalsIgnoreCase("ORD09")) {
                      %>
                        <OPTION VALUE="ORD09"><hl:message key="rotulo.servidor.pontuacao"/></OPTION>
                      <%
                    } else if (ordAux.equalsIgnoreCase("ORD10") && responsavel.isCsa()) {
                      %>
                        <OPTION VALUE="ORD10"><hl:message key="rotulo.proposta.leilao.solicitacao.proposta.taxa.informada"/></OPTION>
                      <%
                    } else if (ordAux.equalsIgnoreCase("ORD11")) {
                      %>
                        <OPTION VALUE="ORD11"><hl:message key="rotulo.proposta.leilao.solicitacao.proposta.melhor.taxa"/></OPTION>
                      <%
                    } else if (ordAux.equalsIgnoreCase("ORD12")) {
                        %>
                          <OPTION VALUE="ORD12"><hl:message key="rotulo.servidor.variacao.margem.livre"/></OPTION>
                        <%
                    } else if (ordAux.equalsIgnoreCase("ORD13")) {
                        %>
                        <OPTION VALUE="ORD13"><hl:message key="rotulo.servidor.risco.csa"/></OPTION>
                      <%
                  }
                 }
              } else {
                %>
                      <OPTION VALUE="ORD01"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.data.abertura"/></OPTION>
                      <OPTION VALUE="ORD02"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.data.fechamento"/></OPTION>
                      <OPTION VALUE="ORD03"><hl:message key="rotulo.servidor.singular"/></OPTION>
                      <OPTION VALUE="ORD04"><hl:message key="rotulo.endereco.cidade.uf"/></OPTION>
                      <OPTION VALUE="ORD05"><hl:message key="rotulo.consignacao.numero"/></OPTION>
                      <OPTION VALUE="ORD06"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.valor.liberado"/></OPTION>
                      <OPTION VALUE="ORD07"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.valor.prestacao"/></OPTION>
                      <OPTION VALUE="ORD08"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.prazo"/></OPTION>
                      <OPTION VALUE="ORD09"><hl:message key="rotulo.servidor.pontuacao"/></OPTION>
                 <%if (responsavel.isCsa()) {%>
                      <OPTION VALUE="ORD10"><hl:message key="rotulo.proposta.leilao.solicitacao.proposta.taxa.informada"/></OPTION>
                 <%}%>
                      <OPTION VALUE="ORD11"><hl:message key="rotulo.proposta.leilao.solicitacao.proposta.melhor.taxa"/></OPTION>
                      <OPTION VALUE="ORD12"><hl:message key="rotulo.servidor.variacao.margem.livre"/></OPTION>
                 <%if (responsavel.isCsa() && temRiscoPelaCsa) {%>
                      <OPTION VALUE="ORD13"><hl:message key="rotulo.servidor.risco.csa"/></OPTION>
                 <%}%>
           <%}%>
          </select>
        </div>
        <div class="form-group col-sm-12 col-md-1 p-0 mt-5">
          <a class="btn btn-primary btn-ordenacao" href="#no-back" onClick="move(document.forms[0].ORDENACAO, document.forms[0].ORDENACAO.selectedIndex, -1); atribui_ordenacao(); return false;">
            <svg width="15">
                <use xlink:href="#i-avancar"></use>
              </svg>
          </a>
          <a class="btn btn-primary btn-ordenacao" href="#no-back" onClick="move(document.forms[0].ORDENACAO, document.forms[0].ORDENACAO.selectedIndex, +1); atribui_ordenacao(); return false;">
            <svg width="15">
                <use xlink:href="#i-voltar"></use>
              </svg>
          </a>
        </div>
        <div class="col-sm-12 col-md-6 mt-3">
          <div class="form-check">
            <div class="row">
              <div class="col-sm-12 col-md-12 mt-2">
                <div class="row" role="radiogroup" aria-labelledby="dataAbertura">
                  <div class="col-sm-12 col-md-4">
                    <div class="form-group my-0">
                      <span class="mr-2 text-nowrap" id="dataAbertura"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.data.abertura"/></span>
                    </div>
                  </div>
                  <div class="col-sm-12 col-md-3 ml-3">
                    <input class="form-check-input ml-1" type="radio" 
                           NAME="ORDEM_DATA_ABERTURA" ID="ORDEM_DATA_ABERTURA1" 
                           VALUE="ASC" <%if ((!TextHelper.isNull(ordemDataAbertura) && ordemDataAbertura.equals("ASC"))) { %> checked <%} %> 
                           onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);"
                    />
                    <label class="form-check-label formatacao ml-1 pr-4" for="ORDEM_DATA_ABERTURA1"><hl:message key="rotulo.crescente"/></label>
                  </div>
                  <div class="col-sm-12 col-md-3">
                    <input class="form-check-input ml-1" type="radio" 
                           NAME=ORDEM_DATA_ABERTURA ID="ORDEM_DATA_ABERTURA2" VALUE="DESC" 
                           <%if (TextHelper.isNull(ordemDataAbertura) || (!TextHelper.isNull(ordemDataAbertura) && ordemDataAbertura.equals("DESC"))) { %> checked <%} %> 
                           onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);"
                    />
                    <label class="form-check-label formatacao ml-1" for="ORDEM_DATA_ABERTURA2"><hl:message key="rotulo.decrescente"/></label>
                  </div>
                </div>
              </div>
            </div>
            <div class="row">
              <div class="col-sm-12 col-md-12 mt-2">
                <div class="row" role="radiogroup" aria-labelledby="dataFechamento">
                  <div class="col-sm-12 col-md-4">
                    <div class="form-group my-0">
                      <span class="mr-2 text-nowrap" id="dataFechamento"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.data.fechamento"/></span>
                    </div>
                  </div>
                  <div class="col-sm-12 col-md-3 ml-3">
                    <input class="form-check-input ml-1" type="radio" 
                           NAME="ORDEM_DATA_FECHAMENTO" ID="ORDEM_DATA_FECHAMENTO1" VALUE="ASC" 
                           <%if ((!TextHelper.isNull(ordemDataFechamento) && ordemDataFechamento.equals("ASC"))) { %> checked <%} %> 
                           onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" 
                    />
                    <label class="form-check-label formatacao ml-1 pr-4" for="ORDEM_DATA_FECHAMENTO1"><hl:message key="rotulo.crescente"/></label>
                  </div>
                  <div class="col-sm-12 col-md-3">
                    <input class="form-check-input ml-1" type="radio" 
                           NAME=ORDEM_DATA_FECHAMENTO ID="ORDEM_DATA_FECHAMENTO2" VALUE="DESC" 
                           <%if (TextHelper.isNull(ordemDataFechamento) || (!TextHelper.isNull(ordemDataFechamento) && ordemDataFechamento.equals("DESC"))) { %> checked <%} %> 
                           onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);"
                    />
                    <label class="form-check-label formatacao ml-1" for="ORDEM_DATA_FECHAMENTO2"><hl:message key="rotulo.decrescente"/></label>
                  </div>
                </div>
              </div>
            </div>
            <div class="row">
              <div class="col-sm-12 col-md-12 mt-2">
                <div class="row" role="radiogroup" aria-labelledby="servidorSingular">
                  <div class="col-sm-12 col-md-4">
                    <div class="form-group my-0">
                      <span class="mr-2 text-nowrap" id="servidorSingular"><hl:message key="rotulo.servidor.singular"/></span>
                    </div>
                  </div>
                  <div class="col-sm-12 col-md-3 ml-3">
                    <input class="form-check-input ml-1" type="radio" 
                           NAME="ORDEM_SERVIDOR" ID="ORDEM_SERVIDOR1" VALUE="ASC"
                           <%if (TextHelper.isNull(ordemServidor) || (!TextHelper.isNull(ordemServidor) && ordemServidor.equals("ASC"))) { %> checked <%} %> 
                           onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" 
                    />
                    <label class="form-check-label formatacao ml-1 pr-4" for="ORDEM_SERVIDOR1"><hl:message key="rotulo.crescente"/></label>
                  </div>
                  <div class="col-sm-12 col-md-3">
                    <input class="form-check-input ml-1" type="radio" 
                           NAME="ORDEM_SERVIDOR" ID="ORDEM_SERVIDOR2" VALUE="DESC"
                           <%if ((!TextHelper.isNull(ordemServidor) && ordemServidor.equals("DESC"))) { %> checked <%} %> 
                           onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);"
                    /> 
                    <label class="form-check-label formatacao ml-1" for="ORDEM_SERVIDOR2"><hl:message key="rotulo.decrescente"/></label>
                  </div>
                </div>
              </div>
            </div>
            <div class="row">
              <div class="col-sm-12 col-md-12 mt-2">
                <div class="row" role="radiogroup" aria-labelledby="endCidadeUf">
                  <div class="col-sm-12 col-md-4">
                    <div class="form-group my-0">
                      <span class="mr-2 text-nowrap" id="endCidadeUf"><hl:message key="rotulo.endereco.cidade.uf"/></span>
                    </div>
                  </div>
                  <div class="col-sm-12 col-md-3 ml-3">
                    <input class="form-check-input ml-1" type="radio" 
                           NAME="ORDEM_CIDADE_UF" ID="ORDEM_CIDADE_UF1" VALUE="ASC"
                           <%if (TextHelper.isNull(ordemCidadeUf) || (!TextHelper.isNull(ordemCidadeUf) && ordemServidor.equals("ASC"))) { %> checked <%} %> 
                           onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" 
                    />
                    <label class="form-check-label formatacao ml-1 pr-4" for="ORDEM_CIDADE_UF1"><hl:message key="rotulo.crescente"/></label>
                  </div>
                  <div class="col-sm-12 col-md-3">
                    <input class="form-check-input ml-1" type="radio" 
                           NAME="ORDEM_CIDADE_UF" ID="ORDEM_CIDADE_UF2" VALUE="DESC"
                           <%if ((!TextHelper.isNull(ordemCidadeUf) && ordemCidadeUf.equals("DESC"))) { %> checked <%} %> 
                           onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" 
                     /> 
                    <label class="form-check-label formatacao ml-1" for="ORDEM_CIDADE_UF2"><hl:message key="rotulo.decrescente"/></label>
                  </div>
                </div>
              </div>
            </div>
            <div class="row">
              <div class="col-sm-12 col-md-12 mt-2">
                <div class="row" role="radiogroup" aria-labelledby="consignacaoNum">
                  <div class="col-sm-12 col-md-4">
                    <div class="form-group my-0">
                      <span class="mr-2 text-nowrap" id="consignacaoNum"><hl:message key="rotulo.consignacao.numero"/></span>
                    </div>
                  </div>
                  <div class="col-sm-12 col-md-3 ml-3">
                    <input class="form-check-input ml-1" type="radio" 
                           NAME="ORDEM_ADE_NUMERO" ID="ORDEM_ADE_NUMERO1" VALUE="ASC"
                           <%if (TextHelper.isNull(ordemAdeNumero) || (!TextHelper.isNull(ordemAdeNumero) && ordemAdeNumero.equals("ASC"))) { %> checked <%} %> 
                           onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" 
                    />
                    <label class="form-check-label formatacao ml-1 pr-4" for="ORDEM_ADE_NUMERO1"><hl:message key="rotulo.crescente"/></label>
                  </div>
                  <div class="col-sm-12 col-md-3">
                    <input class="form-check-input ml-1" type="radio" 
                           NAME="ORDEM_ADE_NUMERO" ID="ORDEM_ADE_NUMERO2" VALUE="DESC"
                           <%if ((!TextHelper.isNull(ordemAdeNumero) && ordemAdeNumero.equals("DESC"))) { %> checked <%} %> 
                           onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" 
                     /> 
                    <label class="form-check-label formatacao ml-1" for="ORDEM_ADE_NUMERO2"><hl:message key="rotulo.decrescente"/></label>
                  </div>
                </div>
              </div>
            </div>
            <div class="row">
              <div class="col-sm-12 col-md-12 mt-2">
                <div class="row" role="radiogroup" aria-labelledby="valorLiberado">
                  <div class="col-sm-12 col-md-4">
                    <div class="form-group my-0">
                      <span class="mr-2 text-nowrap" id="valorLiberado"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.valor.liberado"/></span>
                    </div>
                  </div>
                  <div class="col-sm-12 col-md-3 ml-3">
                    <input class="form-check-input ml-1" type="radio" 
                           NAME="ORDEM_VALOR_LIBERADO" ID="ORDEM_VALOR_LIBERADO1" VALUE="ASC"
                           <%if (TextHelper.isNull(ordemValorLiberado) || (!TextHelper.isNull(ordemValorLiberado) && ordemValorLiberado.equals("ASC"))) { %> checked <%} %> 
                           onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" 
                    />
                    <label class="form-check-label formatacao ml-1 pr-4" for="ORDEM_VALOR_LIBERADO1"><hl:message key="rotulo.crescente"/></label>
                  </div>
                  <div class="col-sm-12 col-md-3">
                    <input class="form-check-input ml-1" type="radio" 
                           NAME="ORDEM_VALOR_LIBERADO" ID="ORDEM_VALOR_LIBERADO2" VALUE="DESC"
                           <%if ((!TextHelper.isNull(ordemValorLiberado) && ordemValorLiberado.equals("DESC"))) { %> checked <%} %> 
                           onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" 
                     /> 
                    <label class="form-check-label formatacao ml-1" for="ORDEM_VALOR_LIBERADO2"><hl:message key="rotulo.decrescente"/></label>
                  </div>
                </div>
              </div>
            </div>
            <div class="row">
              <div class="col-sm-12 col-md-12 mt-2">
                <div class="row" role="radiogroup" aria-labelledby="valorPrestacao">
                  <div class="col-sm-12 col-md-4">
                    <div class="form-group my-0">
                      <span class="mr-2 text-nowrap" id="valorPrestacao"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.valor.prestacao"/></span>
                    </div>
                  </div>
                  <div class="col-sm-12 col-md-3 ml-3">
                    <input class="form-check-input ml-1" type="radio" 
                           NAME="ORDEM_PRESTACAO" ID="ORDEM_PRESTACAO1" VALUE="ASC"
                           <%if (TextHelper.isNull(ordemValorPrestacao) ||  (!TextHelper.isNull(ordemValorPrestacao) && ordemValorPrestacao.equals("ASC"))) { %> checked <%} %> 
                           onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" 
                    />
                    <label class="form-check-label formatacao ml-1 pr-4" for="ORDEM_PRESTACAO1"><hl:message key="rotulo.crescente"/></label>
                  </div>
                  <div class="col-sm-12 col-md-3">
                    <input class="form-check-input ml-1" type="radio" 
                           NAME="ORDEM_PRESTACAO" ID="ORDEM_PRESTACAO2" VALUE="DESC"
                           <%if ((!TextHelper.isNull(ordemValorPrestacao) && ordemValorPrestacao.equals("DESC"))) { %> checked <%} %> 
                           onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" 
                     /> 
                    <label class="form-check-label formatacao ml-1" for="ORDEM_PRESTACAO2"><hl:message key="rotulo.decrescente"/></label>
                  </div>
                </div>
              </div>
            </div>
            <div class="row">
              <div class="col-sm-12 col-md-12 mt-2">
                <div class="row" role="radiogroup" aria-labelledby="leilaoSoliPrazo">
                  <div class="col-sm-12 col-md-4">
                    <div class="form-group my-0">
                      <span class="mr-2 text-nowrap" id="leilaoSoliPrazo"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.prazo"/></span>
                    </div>
                  </div>
                  <div class="col-sm-12 col-md-3 ml-3">
                    <input class="form-check-input ml-1" type="radio" 
                           NAME="ORDEM_PRAZO" ID="ORDEM_PRAZO1" VALUE="ASC"
                           <%if (TextHelper.isNull(ordemNumPrestacao) ||  (!TextHelper.isNull(ordemNumPrestacao) && ordemNumPrestacao.equals("ASC"))) { %> checked <%} %> 
                           onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" 
                    />
                    <label class="form-check-label formatacao ml-1 pr-4" for="ORDEM_PRAZO1"><hl:message key="rotulo.crescente"/></label>
                  </div>
                  <div class="col-sm-12 col-md-3">
                    <input class="form-check-input ml-1" type="radio" 
                           NAME="ORDEM_PRAZO" ID="ORDEM_PRAZO2" VALUE="DESC"
                           <%if ((!TextHelper.isNull(ordemNumPrestacao) && ordemNumPrestacao.equals("DESC"))) { %> checked <%} %> 
                           onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" 
                     /> 
                    <label class="form-check-label formatacao ml-1" for="ORDEM_PRAZO2"><hl:message key="rotulo.decrescente"/></label>
                  </div>
                </div>
              </div>
            </div>
            <div class="row">
              <div class="col-sm-12 col-md-12 mt-2">
                <div class="row" role="radiogroup" aria-labelledby="servidorPontuacao">
                  <div class="col-sm-12 col-md-4">
                    <div class="form-group my-0">
                      <span class="mr-2 text-nowrap" id="servidorPontuacao"><hl:message key="rotulo.servidor.pontuacao"/></span>
                    </div>
                  </div>
                  <div class="col-sm-12 col-md-3 ml-3">
                    <input class="form-check-input ml-1" type="radio" 
                           NAME="ORDEM_PONTUACAO" ID="ORDEM_PONTUACAO1"  VALUE="ASC"
                           <%if (TextHelper.isNull(ordemPontuacao) ||  (!TextHelper.isNull(ordemPontuacao) && ordemPontuacao.equals("ASC"))) { %> checked <%} %> 
                           onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" 
                    />
                    <label class="form-check-label formatacao ml-1 pr-4" for="ORDEM_PONTUACAO1"><hl:message key="rotulo.crescente"/></label>
                  </div>
                  <div class="col-sm-12 col-md-3">
                    <input class="form-check-input ml-1" type="radio" 
                           NAME="ORDEM_PONTUACAO" ID="ORDEM_PONTUACAO2" VALUE="DESC"
                           <%if ((!TextHelper.isNull(ordemPontuacao) && ordemPontuacao.equals("DESC"))) { %> checked <%} %> 
                           onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" 
                     /> 
                    <label class="form-check-label formatacao ml-1" for="ORDEM_PONTUACAO2"><hl:message key="rotulo.decrescente"/></label>
                  </div>
                </div>
              </div>
            </div>
            <%if(responsavel.isCsa()){ %>
            <div class="row">
              <div class="col-sm-12 col-md-12 mt-2">
                <div class="row" role="radiogroup" aria-labelledby="propostaTaxa">
                  <div class="col-sm-12 col-md-4">
                    <div class="form-group my-0">
                      <span class="mr-2 text-nowrap" id="propostaTaxa"><hl:message key="rotulo.proposta.leilao.solicitacao.proposta.taxa.informada"/></span>
                    </div>
                  </div>
                  <div class="col-sm-12 col-md-3 ml-3">
                    <input class="form-check-input ml-1" type="radio" 
                           NAME="ORDEM_TAXA_INFORMADA" ID="ORDEM_TAXA_INFORMADA1" VALUE="ASC"
                           <%if (TextHelper.isNull(ordemTaxaInformada) ||  (!TextHelper.isNull(ordemTaxaInformada) && ordemTaxaInformada.equals("ASC"))) { %> checked <%} %> 
                           onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" 
                    />
                    <label class="form-check-label formatacao ml-1 pr-4" for="ORDEM_TAXA_INFORMADA1"><hl:message key="rotulo.crescente"/></label>
                  </div>
                  <div class="col-sm-12 col-md-3">
                    <input class="form-check-input ml-1" type="radio" 
                           NAME="ORDEM_TAXA_INFORMADA" ID="ORDEM_TAXA_INFORMADA2" VALUE="DESC"
                           <%if ((!TextHelper.isNull(ordemTaxaInformada) && ordemTaxaInformada.equals("DESC"))) { %> checked <%} %> 
                           onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" 
                     /> 
                    <label class="form-check-label formatacao ml-1" for="ORDEM_TAXA_INFORMADA2"><hl:message key="rotulo.decrescente"/></label>
                  </div>
                </div>
              </div>
            </div>
            <%} %>
            <div class="row">
              <div class="col-sm-12 col-md-12 mt-2">
                <div class="row" role="radiogroup" aria-labelledby="melhorTaxa">
                  <div class="col-sm-12 col-md-4">
                    <div class="form-group my-0">
                      <span class="mr-2 text-nowrap" id="melhorTaxa"><hl:message key="rotulo.proposta.leilao.solicitacao.proposta.melhor.taxa"/></span>
                    </div>
                  </div>
                  <div class="col-sm-12 col-md-3 ml-3">
                    <input class="form-check-input ml-1" type="radio" 
                           NAME="ORDEM_MELHOR_TAXA" ID="ORDEM_MELHOR_TAXA1" VALUE="ASC"
                           <%if (TextHelper.isNull(ordemMelhorTaxa) ||  (!TextHelper.isNull(ordemMelhorTaxa) && ordemMelhorTaxa.equals("ASC"))) { %> checked <%} %> 
                           onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" 
                    />
                    <label class="form-check-label formatacao ml-1 pr-4" for="ORDEM_MELHOR_TAXA1"><hl:message key="rotulo.crescente"/></label>
                  </div>
                  <div class="col-sm-12 col-md-3">
                    <input class="form-check-input ml-1" type="radio" 
                           NAME="ORDEM_MELHOR_TAXA" ID="ORDEM_MELHOR_TAXA2" VALUE="DESC"
                           <%if ((!TextHelper.isNull(ordemMelhorTaxa) && ordemMelhorTaxa.equals("DESC"))) { %> checked <%} %> 
                           onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" 
                     /> 
                    <label class="form-check-label formatacao ml-1" for="ORDEM_MELHOR_TAXA2"><hl:message key="rotulo.decrescente"/></label>
                  </div>
                </div>
              </div>
            </div>
            <div class="row">
              <div class="col-sm-12 col-md-12 mt-2">
                <div class="row" role="radiogroup" aria-labelledby="margemLivre">
                  <div class="col-sm-12 col-md-4">
                    <div class="form-group my-0">
                      <span class="mr-2 text-nowrap" id="margemLivre"><hl:message key="rotulo.servidor.variacao.margem.livre"/></span>
                    </div>
                  </div>
                  <div class="col-sm-12 col-md-3 ml-3">
                    <input class="form-check-input ml-1" type="radio" 
                           NAME="ORDEM_MARGEM_LIVRE" ID="ORDEM_MARGEM_LIVRE1" VALUE="ASC"
                           <%if (TextHelper.isNull(ordemMargemLivre) ||  (!TextHelper.isNull(ordemMargemLivre) && ordemMargemLivre.equals("ASC"))) { %> checked <%} %> 
                           onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" 
                    />
                    <label class="form-check-label formatacao ml-1 pr-4" for="ORDEM_MARGEM_LIVRE1"><hl:message key="rotulo.crescente"/></label>
                  </div>
                  <div class="col-sm-12 col-md-3">
                    <input class="form-check-input ml-1" type="radio" 
                           NAME="ORDEM_MARGEM_LIVRE" ID="ORDEM_MARGEM_LIVRE2" VALUE="DESC"
                           <%if ((!TextHelper.isNull(ordemMargemLivre) && ordemMargemLivre.equals("DESC"))) { %> checked <%} %> 
                           onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" 
                     /> 
                    <label class="form-check-label formatacao ml-1" for="ORDEM_MARGEM_LIVRE2"><hl:message key="rotulo.decrescente"/></label>
                  </div>
                </div>
              </div>
            </div>
            <%if (responsavel.isCsa() && temRiscoPelaCsa) {%>
            <div class="row">
              <div class="col-sm-12 col-md-12 mt-2">
                <div class="row" role="radiogroup" aria-labelledby="margemLivre">
                  <div class="col-sm-12 col-md-4">
                    <div class="form-group my-0">
                      <span class="mr-2 text-nowrap" id="margemLivre"><hl:message key="rotulo.servidor.risco.csa"/></span>
                    </div>
                  </div>
                  <div class="col-sm-12 col-md-3 ml-3">
                    <input class="form-check-input ml-1" type="radio" 
                           NAME="ORDEM_RISCO_SERVIDOR" ID="ORDEM_RISCO_SERVIDOR1" VALUE="ASC"
                           <%if (TextHelper.isNull(ordemRiscoServidor) ||  (!TextHelper.isNull(ordemRiscoServidor) && ordemMargemLivre.equals("ASC"))) { %> checked <%} %> 
                           onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" 
                    />
                    <label class="form-check-label formatacao ml-1 pr-4" for="ORDEM_RISCO_SERVIDOR1"><hl:message key="rotulo.crescente"/></label>
                  </div>
                  <div class="col-sm-12 col-md-3">
                    <input class="form-check-input ml-1" type="radio" 
                           NAME="ORDEM_RISCO_SERVIDOR" ID="ORDEM_RISCO_SERVIDOR2" VALUE="DESC"
                           <%if ((!TextHelper.isNull(ordemRiscoServidor) && ordemRiscoServidor.equals("DESC"))) { %> checked <%} %> 
                           onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" 
                     /> 
                    <label class="form-check-label formatacao ml-1" for="ORDEM_RISCO_SERVIDOR2"><hl:message key="rotulo.decrescente"/></label>
                  </div>
                </div>
              </div>
            </div>
            <%} %>
          </div>
        </div>
      </div>
      <div class="row">
        <%--Inclui o campo cidade/uf --%>
        <% request.setAttribute("NSE_CODIGO",CodedValues.NSE_EMPRESTIMO); %>
        <%@ include file="../cidade/include_campo_cidade_uf_v4.jsp"%> 
      </div>
      <hl:htmlinput type="hidden" name="ORDENACAO_AUX" di="ORDENACAO_AUX" value="" />                      
      <hl:htmlinput type="hidden" name="DESC_ORDENACAO" di="DESC_ORDENACAO" value="" />                      
     </div> 
  </div> 
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal')"><hl:message key="rotulo.botao.cancelar"/></a>
    <a class="btn btn-primary" href="#no-back" onClick="validaSubmit()"><svg width="20"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="../img/sprite.svg#i-consultar"></use></svg><hl:message key="rotulo.botao.pesquisar"/></a>
  </div>
  <% } %>
  <% if (lstResultado != null) { %>
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.acompanhamento.resultado.pesquisa"/></h2>
    </div>
    <div class="card-body table-responsive p-0">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <%int colCount = 12; %>
            <th scope="col"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.data.abertura"/></th>
            <th scope="col"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.data.fechamento"/></th>
            <th scope="col"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.horas.restantes.fim.leilao"/></th>
            <th scope="col"><hl:message key="rotulo.servidor.singular"/></th>
            <%if (!responsavel.isSer()) {%> 
            <th scope="col"><hl:message key="rotulo.endereco.cidade.uf"/></th>
            <%} else {%>
            <th scope="col"><hl:message key="rotulo.status.proposta"/>
              <span class="question-icon">
                <svg data-bs-toggle="tooltip" title="" data-original-title="<%=ApplicationResourcesHelper.getMessage("mensagem.servidor.status.leilao", responsavel)%>">
                  <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-question"></use>
                </svg>
              </span>
            </th>            
            <%} %>
            <th scope="col"><hl:message key="rotulo.consignacao.numero"/></th>
            <th scope="col"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.valor.liberado"/></th>
            <th scope="col"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.valor.prestacao"/></th>
            <th scope="col"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.prazo"/></th>
            <%if(!responsavel.isSer()) {
                colCount++; 
            %>
            <th scope="col"><hl:message key="rotulo.servidor.pontuacao"/>
              <span class="question-icon">
                <svg data-bs-toggle="popover" data-bs-content="<%=ApplicationResourcesHelper.getMessage("mensagem.servidor.pontuacao.forma.calculo", responsavel)%>">
                  <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-question"></use>
                </svg>
              </span>
            </th>
            <th scope="col"><hl:message key="rotulo.servidor.variacao.margem.livre"/></th>
            <%} %>
            <%if(responsavel.isCsa()) {
                colCount++; %>
              <%if(temRiscoPelaCsa) {
                  colCount++;
              %>
            <th scope="col"><hl:message key="rotulo.servidor.risco.csa"/></th>
            <%} %>
            <th scope="col"><hl:message key="rotulo.proposta.leilao.solicitacao.proposta.taxa.informada"/> (<hl:message key="rotulo.porcentagem"/>)</th>
            <%} %>
            <% if (acompanharLeilaoModel.getColspan() > 0) { 
                  colCount++;%>
            <th scope="col"><hl:message key="rotulo.proposta.leilao.solicitacao.proposta.melhor.taxa"/> (<hl:message key="rotulo.porcentagem"/>)</th>
            <th scope="col"><hl:message key="rotulo.acoes"/></th>
            <% } %>
          </tr>
        </thead>
        <tbody>
          <tr>
            <%=JspHelper.msgRstVazio(lstResultado.isEmpty(), Integer.toString(colCount), "lp")%>
            <%             
            TransferObject resultado = null;
            Iterator<TransferObject> it = lstResultado.iterator();
            while (it.hasNext()) {
                resultado = it.next();
              
                AcompanharLeilaoModel.LinhaAcompanhamentoLeilao linhaAcompanhamento = acompanharLeilaoModel.gerarLinhaAcompanhamento(resultado, responsavel);
            %>
            <td valign="top"><%=TextHelper.forHtmlContent(linhaAcompanhamento.soaData)%></td>
            <td valign="top"><%=TextHelper.forHtmlContent(linhaAcompanhamento.soaDataValidadeFim)%></td>
            <td valign="top" align="center"><b><%=TextHelper.forHtmlContent(linhaAcompanhamento.soaDataValidade)%></b></td>
            <td valign="top"><%=TextHelper.forHtmlContent(linhaAcompanhamento.servidor)%></td>
            <%if (!responsavel.isSer()) {%>
            	<td valign="top"><%=TextHelper.forHtmlContent(linhaAcompanhamento.cidadeUf)%></td>
            <%} else {%>
            	<td valign="top"><%=TextHelper.forHtmlContent(linhaAcompanhamento.textoStatusLeilao)%></td>
            <%}%>
            <td valign="top"><%=TextHelper.forHtmlContent(linhaAcompanhamento.adeNumero)%></td>
            <td valign="top" align="right" NOWRAP><hl:message key="rotulo.moeda"/> <%=TextHelper.forHtmlContent(linhaAcompanhamento.adeVlrLiberado)%></td>
            <td valign="top" align="right" NOWRAP><%=TextHelper.forHtmlContent(ParamSvcTO.getDescricaoTpsTipoVlr(linhaAcompanhamento.adeTipoVlr))%> <%=TextHelper.forHtmlContent(linhaAcompanhamento.adeVlr)%></td>
            <td valign="top" align="right" NOWRAP><%=TextHelper.forHtmlContent(linhaAcompanhamento.adePrazo)%>&nbsp;</td>
            <%if(!responsavel.isSer()) {%>
              <td valign="top" align="right" NOWRAP><%=TextHelper.forHtmlContent(linhaAcompanhamento.rsePontuacao)%>&nbsp;</td>
              <td valign="top" align="center" NOWRAP><%=TextHelper.forHtmlContent(linhaAcompanhamento.strVariacaoMargemLivre)%>&nbsp;</td>
            <%} %>
             <%if(responsavel.isCsa()) {%>
             	<%if(temRiscoPelaCsa) {%>
             		<td valign="top" align="right" NOWRAP><%=TextHelper.forHtmlContent(linhaAcompanhamento.arrRisco)%>&nbsp;</td>
             	<%}%>
             <td valign="top" align="right" NOWRAP><%=TextHelper.forHtmlContent(linhaAcompanhamento.taxa)%>&nbsp;</td>
            <%} %>
            <td valign="top" align="right" NOWRAP><%=TextHelper.forHtmlContent(linhaAcompanhamento.taxaMin)%>&nbsp;</td>
  
            <% if (acompanharLeilaoModel.getColspan() > 0) { %>
              <% if ((acompanharLeilaoModel.isPodeEdtProposta() && acompanharLeilaoModel.isPodecadastrarRisco())
                      || acompanharLeilaoModel.isPodeEdtProposta() && (acompanharLeilaoModel.isPodeConsultarAde() && (responsavel.isCseSupOrg() || responsavel.isSer()))
                      || acompanharLeilaoModel.isPodecadastrarRisco() && (acompanharLeilaoModel.isPodeConsultarAde() && (responsavel.isCseSupOrg() || responsavel.isSer()))) { %>
              <td>
                <div class="actions">
                  <div class="dropdown">
                    <a class="dropdown-toggle ico-action" href="#" role="button" id="MASTER" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                      <div class="form-inline">
                        <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="Opções" aria-label="Opções"> <svg>
                            <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                        </span> <hl:message key="rotulo.botao.opcoes"/>
                      </div>
                    </a>
                    
                    <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu" x-placement="top-end" style="position: absolute; transform: translate3d(356px, 93px, 0px); top: 0px; left: 0px; will-change: transform;">
                      <%if (acompanharLeilaoModel.isPodeEdtProposta()) { %>
                      <a class="dropdown-item" href="#no-back" onClick="doIt('p', '<%=TextHelper.forJavaScript(linhaAcompanhamento.adeCodigo)%>');"><hl:message key="rotulo.acoes.proposta"/></a>
                      <% } %>
                      <% if (acompanharLeilaoModel.isPodecadastrarRisco()) { %>
                      <a class="dropdown-item" href="#no-back" onClick="postData('../v3/cadastrarAnaliseRiscoServidor?acao=iniciar&RSE_CODIGO=<%=TextHelper.forJavaScript(linhaAcompanhamento.rseCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.cadastrar.risco"/></a>
                      <% } %>
                      <% if (acompanharLeilaoModel.isPodeConsultarAde() && (responsavel.isCseSupOrg() || responsavel.isSer())) { %>
                      <a class="dropdown-item" href="#no-back" onClick="doIt('e', '<%=TextHelper.forJavaScript(linhaAcompanhamento.adeCodigo)%>');"><hl:message key="rotulo.acoes.consultar"/> </a>
                      <% } %>
                    </div>
                    
                  </div>
                </div>
              </td>
              <%} else {
              
              if (acompanharLeilaoModel.isPodeEdtProposta()) { %>
              <td align="center"><a href="#no-back" onClick="doIt('p', '<%=TextHelper.forJavaScript(linhaAcompanhamento.adeCodigo)%>');"><hl:message key="rotulo.acoes.proposta"/></a></td>
              <% } %>
              <% if (acompanharLeilaoModel.isPodecadastrarRisco()) { %>
              <td align="center"><a href="#no-back" onClick="postData('../v3/cadastrarAnaliseRiscoServidor?acao=iniciar&RSE_CODIGO=<%=TextHelper.forJavaScript(linhaAcompanhamento.rseCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.cadastrar.risco"/></a></td>
              <% } %>
              <% if (acompanharLeilaoModel.isPodeConsultarAde() && (responsavel.isCseSupOrg() || responsavel.isSer())) { %>
              <td align="center"><a href="#no-back" onClick="doIt('e', '<%=TextHelper.forJavaScript(linhaAcompanhamento.adeCodigo)%>');"><hl:message key="rotulo.acoes.consultar"/> </a></td>
              <% }
              }%>
            <% } %>
          </tr>
          <%
          }
          %>
        </tbody>
        <tfoot>
        	<tr>
            	<td colspan="7">
                	<hl:message key="rotulo.acompanhamento.leilao.solicitacao.rodape"/>
                	<span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTitulo"))%></span>
                </td>
          	</tr>
        </tfoot>
      </table>
    </div>
    <div class="card-footer">
      <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal')"><hl:message key="rotulo.botao.cancelar"/></a>
  </div>
  <% } %>
</form>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
      var f0 = document.forms[0];
      window.onload = formLoad;

      function formLoad() {
        <% if (lstResultado == null) { %>
        $('#filtros').collapse('show');
        <% } %>
      }
      
      // valida o formulário antes do envio do submit
      function validForm() {
        with(f0) {
          <% if (responsavel.isCsaCor()) { %>
          if (getCheckedRadio('form1', 'filtro') == null) {
              alert('<hl:message key="mensagem.informe.filtro"/>');
              filtro.focus();
              return false;
          }
          <% } %>
          if (dataAberturaIni != null && dataAberturaIni.value != '' && !verificaData(dataAberturaIni.value)) {
            dataAberturaIni.focus();
              return false;
          }
          if (dataAberturaFim != null && dataAberturaFim.value != '' && !verificaData(dataAberturaFim.value)) {
            dataAberturaFim.focus();
              return false;
          }
        }
        return true;
      }

      function doIt(opt, ade) {
        var filtro = getCheckedRadio("form1", "filtro");
        var qs = '&ADE_CODIGO=' + ade + '&<%out.print(SynchronizerToken.generateToken4URL(request));%>';
        var msg = '';
        var link = '';
        switch (opt) {
          case 'e':
              link = '../v3/consultarConsignacao?acao=detalharConsignacao';
              break;
          case 'p':
              link = '../v3/editarPropostaLeilao?acao=iniciar' + '&filtro=' + filtro;
              break;
          default:
              return false;
              break;  
        } 

        if (msg == "" || confirm(msg)) {
          postData(link + qs);
        }
      }

      function atribui_ordenacao() {
        var ordenacao = "";
        var descOrdenacao = "";
          with(document.forms[0]) {
             for (var i = 0; i < ORDENACAO.length; i++) {
               if (ORDENACAO.options[i].value == 'ORD01') {
                   ordenacao += "[" + ORDENACAO.options[i].value + ";" + getCheckedRadio('form1', 'ORDEM_DATA_ABERTURA') + "]";
               } else if (ORDENACAO.options[i].value == 'ORD02') {
                   ordenacao += "[" + ORDENACAO.options[i].value + ";" + getCheckedRadio('form1', 'ORDEM_DATA_FECHAMENTO') + "]";
               } else if (ORDENACAO.options[i].value == 'ORD03') {
                   ordenacao += "[" + ORDENACAO.options[i].value + ";" + getCheckedRadio('form1', 'ORDEM_SERVIDOR') + "]";
               } else if (ORDENACAO.options[i].value == 'ORD04') {
                   ordenacao += "[" + ORDENACAO.options[i].value + ";" + getCheckedRadio('form1', 'ORDEM_CIDADE_UF') + "]";
               } else if (ORDENACAO.options[i].value == 'ORD05') {
                   ordenacao += "[" + ORDENACAO.options[i].value + ";" + getCheckedRadio('form1', 'ORDEM_ADE_NUMERO') + "]";
               } else if (ORDENACAO.options[i].value == 'ORD06') {
                   ordenacao += "[" + ORDENACAO.options[i].value + ";" + getCheckedRadio('form1', 'ORDEM_VALOR_LIBERADO') + "]";
               } else if (ORDENACAO.options[i].value == 'ORD07') {
                   ordenacao += "[" + ORDENACAO.options[i].value + ";" + getCheckedRadio('form1', 'ORDEM_PRESTACAO') + "]";
               } else if (ORDENACAO.options[i].value == 'ORD08') {
                   ordenacao += "[" + ORDENACAO.options[i].value + ";" + getCheckedRadio('form1', 'ORDEM_PRAZO') + "]";
               } else if (ORDENACAO.options[i].value == 'ORD09') {
                   ordenacao += "[" + ORDENACAO.options[i].value + ";" + getCheckedRadio('form1', 'ORDEM_PONTUACAO') + "]";
               } else if (ORDENACAO.options[i].value == 'ORD10') {
                   ordenacao += "[" + ORDENACAO.options[i].value + ";" + getCheckedRadio('form1', 'ORDEM_TAXA_INFORMADA') + "]";
               } else if (ORDENACAO.options[i].value == 'ORD11') {
                   ordenacao += "[" + ORDENACAO.options[i].value + ";" + getCheckedRadio('form1', 'ORDEM_MELHOR_TAXA') + "]";
               } else if (ORDENACAO.options[i].value == 'ORD12') {
                   ordenacao += "[" + ORDENACAO.options[i].value + ";" + getCheckedRadio('form1', 'ORDEM_MARGEM_LIVRE') + "]";
               } else if (ORDENACAO.options[i].value == 'ORD13') {
                   ordenacao += "[" + ORDENACAO.options[i].value + ";" + getCheckedRadio('form1', 'ORDEM_RISCO_SERVIDOR') + "]";
               }

               if (i < ORDENACAO.length - 1) {
                   ordenacao += ",";
               }
             }
             ORDENACAO_AUX.value = ordenacao;
          }
      } 
      
      function validaSubmit()
      {
          if( validForm() )
          { 
            if(typeof vfRseMatricula === 'function')
            {
              if(vfRseMatricula(true))
              {
                atribui_ordenacao();
                f0.submit();
              }
            }
            else
            {
            atribui_ordenacao();
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