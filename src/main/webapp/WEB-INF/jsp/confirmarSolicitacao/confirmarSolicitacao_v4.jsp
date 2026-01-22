<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject" %>
<%@ page import="com.zetra.econsig.helper.texto.TransferObjectHelper" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.ParamSession"%>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.util.*" %>

<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>  
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

ParamSession paramSession = (ParamSession) request.getAttribute("paramSession");

BigDecimal adeTac = (BigDecimal) request.getAttribute("adeTac");
BigDecimal adeOp  = (BigDecimal) request.getAttribute("adeOp");
BigDecimal cftVlrAtual = (BigDecimal) request.getAttribute("cftVlrAtual");

boolean temCET                    = (boolean) request.getAttribute("temCET");
boolean exigeMotivo               = (boolean) request.getAttribute("exigeMotivo");
boolean ocultarCamposTac          = (boolean) request.getAttribute("ocultarCamposTac");
boolean rseTemInfBancaria         = (boolean) request.getAttribute("rseTemInfBancaria");
boolean exigeSenhaServidor        = (boolean) request.getAttribute("exigeSenhaServidor");
boolean validarInfBancaria        = (boolean) request.getAttribute("validarInfBancaria");
boolean exigeMatriculaSerCsa      = (boolean) request.getAttribute("exigeMatriculaSerCsa");
boolean simulacaoPorTaxaJuros     = (boolean) request.getAttribute("simulacaoPorTaxaJuros");
boolean exigeCodAutSolicitacao    = (boolean) request.getAttribute("exigeCodAutSolicitacao");
boolean exigeModalidadeOperacao   = (boolean) request.getAttribute("exigeModalidadeOperacao");
boolean podeAlterarValorParcela   = (boolean) request.getAttribute("podeAlterarValorParcela");
boolean simulacaoMetodoMexicano   = (boolean) request.getAttribute("simulacaoMetodoMexicano");
boolean simulacaoMetodoBrasileiro   = (boolean) request.getAttribute("simulacaoMetodoBrasileiro");
boolean serInfBancariaObrigatoria = (boolean) request.getAttribute("serInfBancariaObrigatoria");
boolean pulaInformacaoValorPrazo = (boolean) request.getAttribute("pulaInformacaoValorPrazo");

CustomTransferObject cftdes = (CustomTransferObject) request.getAttribute("cftdes");
CustomTransferObject cft    = (CustomTransferObject) request.getAttribute("cft");
CustomTransferObject autdes = (CustomTransferObject) request.getAttribute("autdes");

String numBanco   = (String) request.getAttribute("numBanco");
String numAgencia = (String) request.getAttribute("numAgencia");
String numConta1  = (String) request.getAttribute("numConta1");
String numConta2  = (String) request.getAttribute("numConta2");

String numBancoAlt  = (String) request.getAttribute("numBancoAlt");
String numAgenciaAlt= (String) request.getAttribute("numAgenciaAlt");
String numContaAlt1 = (String) request.getAttribute("numContaAlt1");
String numContaAlt2 = (String) request.getAttribute("numContaAlt2");

String linkAcao  = (String) request.getAttribute("linkAcao");
String rseCodigo = (String) request.getAttribute("rseCodigo");
String adeCodigo = (String) request.getAttribute("adeCodigo");

String rotuloMoeda  = (String) request.getAttribute("rotuloMoeda");
%>
<c:set var="title">
  <hl:message key="rotulo.confirmar.solicitacao.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
            
  <div class="row firefox-print-fix">
    <%-- Utiliza a tag library DetalheConsignacaoTag.java para exibir os dados da ADE --%>
    <hl:detalharADEv4 name="autdes" 
                      table="true" 
                      type="consultar" 
                      scope="request"
                      divSizeCSS="col-sm-6"                    
                      />
    <%-- Fim dos dados da ADE --%>     
  </div>
  <div class="row">      
    <div class="col-sm">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title">
            <hl:message key="rotulo.confirmar.solicitacao.titulo"/>
          </h2>
        </div>
        <div class="card-body">
          <form action="<%=TextHelper.forHtmlAttribute(linkAcao)%>" method="post" name="form1">
          <fieldset>
            <h3 class="legend"><span><hl:message key="rotulo.servidor.subtitulo.informacoes.gerais"/></span></h3>
            
            <% if (cftdes != null) { %>            
              <% if (!temCET) { %>
              <div class=" col-sm-6">
                <dl class="row data-list">
                  <% if (simulacaoPorTaxaJuros) { %>                
                    <% if (!ocultarCamposTac) { %>                                    
                      <dt class="col-5">
                        <hl:message key="rotulo.consignacao.valor.tac.abreviado"/>&nbsp;(<hl:message key="rotulo.moeda"/>)
                      </dt>
                      <dd class="col-7">
                        <%=cftdes.getAttribute(Columns.CDE_VLR_TAC) != null ? NumberHelper.format(((BigDecimal) cftdes.getAttribute(Columns.CDE_VLR_TAC)).doubleValue(), NumberHelper.getLang()) : "0,00"%>
                      </dd>                     
                    <% } %>                  
                    <% if (simulacaoMetodoBrasileiro) { %>
                      <dt class="col-5">
                        <hl:message key="rotulo.consignacao.valor.iof.abreviado"/>&nbsp;(<hl:message key="rotulo.moeda"/>)
                      </dt>
                      <dd class="col-7">
                        <%=cftdes.getAttribute(Columns.CDE_VLR_IOF) != null ? NumberHelper.format(((BigDecimal) cftdes.getAttribute(Columns.CDE_VLR_IOF)).doubleValue(), NumberHelper.getLang()) : "0,00"%>
                      </dd>                    
                    <% } %>
                  <% } else { %>              
                    <% if (!ocultarCamposTac && (simulacaoMetodoBrasileiro || simulacaoMetodoMexicano)) { %>
                      <dt class="col-0">
                        <hl:message key="rotulo.coeficiente.tac"/>
                      </dt>
                      <dd class="col-0">
                        <%=NumberHelper.format(adeTac.doubleValue(), NumberHelper.getLang())%>
                      </dd>            
                    <% } %>                
                    <% if (simulacaoMetodoBrasileiro) { %>
                      <dt class="col-0">
                        <hl:message key="rotulo.coeficiente.op"/>
                      </dt>
                      <dd class="col-0">
                        <%=NumberHelper.format(adeOp.doubleValue(), NumberHelper.getLang())%>
                      </dd>                
                    <% } %>
                  <% } %>
                </dl>
              </div>
              <% } %>
            
            <div class="row">              
              <div class="form-group col-sm-6">
                <label for="CFT_VLR">
                  <hl:message key="rotulo.confirmar.solicitacao.coeficiente"/>
                </label>                                
                
                <% if (podeAlterarValorParcela) { %>                  
                  <hl:htmlinput name="CFT_VLR" 
                                di="CFT_VLR"
                                type="text" 
                                size="10"
                                mask="#F20"
                                classe="form-control" 
                                value="<%=TextHelper.forHtmlAttribute(NumberHelper.format(cftVlrAtual.doubleValue(), NumberHelper.getLang(), 2, 8))%>" 
                                others="onChange=\"f0.ALTERA_VLR_LIBERADO_S.disabled = f0.ALTERA_VLR_LIBERADO_N.disabled = false; if (!f0.ALTERA_VLR_LIBERADO_S.checked) {f0.ALTERA_VLR_LIBERADO_N.checked = true;}\"" 
                                />                                      
                <% } else { %>
                  <hl:htmlinput name="CFT_VLR" 
                                di="CFT_VLR"
                                type="text" 
                                size="10"
                                mask="#F20"
                                classe="form-control"                                                                              
                                value="<%=TextHelper.forHtmlAttribute(NumberHelper.format(cftVlrAtual.doubleValue(), NumberHelper.getLang(), 2, 8))%>" 
                                others="onChange=\"f0.ALTERA_VLR_LIBERADO_S.disabled = false; f0.ALTERA_VLR_LIBERADO_S.checked = true;\"" 
                                />                        
                <% } %>

              </div>

              <div class="form-group col-sm-6 mt-3" role="radiogroup" aria-labelledby="coefiente">
                <div class="form-check pt-2">              

                <% if (podeAlterarValorParcela) { %>
                                                           
                  <hl:htmlinput name="ALTERA_VLR_LIBERADO" 
                                di="ALTERA_VLR_LIBERADO_S"
                                classe="form-check-input ml-1" 
                                type="radio" 
                                value="S" 
                                nf="ADE_IDENTIFICADOR" 
                                />
                  <label class="form-check-label ml-1 labelSemNegrito" for="aumentarValorFinanciado">                            
                    <hl:message key="mensagem.confirmar.solicitacao.aumentar.valor.liberado" />
                  </label>
                  
                  <hl:htmlinput name="ALTERA_VLR_LIBERADO" 
                                di="ALTERA_VLR_LIBERADO_N" 
                                classe="form-check-input ml-1"
                                type="radio" 
                                value="N" 
                                mask="#*100"
                                nf="ADE_IDENTIFICADOR" 
                                />
                  <label class="form-check-label ml-1 labelSemNegrito" for="aumentarValorFinanciado">
                    <hl:message key="mensagem.confirmar.solicitacao.diminuir.valor.parcela" />
                  </label>
                                            
                <% } else { %>
                
                  <hl:htmlinput name="ALTERA_VLR_LIBERADO" 
                                di="ALTERA_VLR_LIBERADO_S" 
                                classe="form-check-input ml-1"
                                type="radio" 
                                value="S"
                                mask="#*100" 
                                nf="ADE_IDENTIFICADOR" 
                                checked="true" />
                  <label class="form-check-label labelSemNegrito" for="aumentarValorFinanciado">
                    <hl:message key="mensagem.confirmar.solicitacao.aumentar.valor.liberado" />
                  </label>
                  
                  <hl:htmlinput name="ALTERA_VLR_LIBERADO" 
                                di="ALTERA_VLR_LIBERADO_N"
                                classe="form-check-input ml-1" 
                                type="radio" 
                                value="N" 
                                mask="#*100"
                                nf="ADE_IDENTIFICADOR" 
                                others="disabled" />
                                
                  <label class="form-check-label labelSemNegrito" for="aumentarValorFinanciado">
                    <hl:message key="mensagem.confirmar.solicitacao.diminuir.valor.parcela" />
                  </label>
                                            
                <% } %>
                
                </div>                        
              </div>
            </div>                                        
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="COEFICIENTE_CALCULADO_PARCELA">
                  <hl:message key="rotulo.confirmar.solicitacao.coeficiente.nova.parcela"/>
                </label>
                
                <hl:htmlinput name="COEFICIENTE_CALCULADO_PARCELA" 
                              di="COEFICIENTE_CALCULADO_PARCELA"
                              type="text" 
                              size="10" 
                              classe="form-control"                                                                          
                              others="disabled"
                />                                                          
              </div>                      
              <div class="form-group col-sm-6">
                <label for="COEFICIENTE_CALCULADO_FINANCIADO">
                  <hl:message key="rotulo.confirmar.solicitacao.coeficiente.nova.financiado"/>
                </label>
                
                <hl:htmlinput name="COEFICIENTE_CALCULADO_FINANCIADO" 
                              di="COEFICIENTE_CALCULADO_FINANCIADO"
                              type="text"
                              size="10"   
                              classe="form-control"                                      
                              others="disabled" 
                />                                                                                                         
              </div>
            </div>                                                      
          <% } %>
          
          <div class="row">
            <div class="form-group col-sm-6">
              <label for="ADE_IDENTIFICADOR">
                <hl:message key="rotulo.consignacao.identificador"/>
              </label>
              <hl:htmlinput name="ADE_IDENTIFICADOR" 
                       type="text" 
                       classe="form-control"
                       di="ADE_IDENTIFICADOR" 
                       size="20" 
                       mask="#*20"                                
                       nf="<%=TextHelper.forHtmlAttribute( serInfBancariaObrigatoria ? "numBanco" : (exigeSenhaServidor ? "senha" : "btnEnvia") )%>" 
                       value="<%=TextHelper.forHtmlAttribute((autdes.getAttribute(Columns.ADE_IDENTIFICADOR)))%>" 
                       />                                                                                                         
            </div>
          </div>    

          <% if (responsavel.isCsaCor() && exigeModalidadeOperacao) { %>
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="tdaModalidadeOp">
                  <hl:message key="rotulo.consignacao.modalidade.operacao"/>
                </label>          
                <hl:htmlinput name="tdaModalidadeOp" 
                              di="tdaModalidadeOp" 
                              type="text" 
                              classe="form-control"
                              size="6" 
                              mask="#*6" />                 
              </div>
            </div>          
          <% } %>
               
          <% if (responsavel.isCsaCor() && exigeMatriculaSerCsa) { %>               
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="tdaMatriculaCsa">
                  <hl:message key="rotulo.consignacao.matricula.ser.csa"/>
                </label>          
                <hl:htmlinput name="tdaMatriculaCsa" 
                              di="tdaMatriculaCsa" 
                              type="text" 
                              classe="form-control"
                              size="20" 
                              mask="#*20" 
                />                                                                                                                                
              </div>
            </div>              
          <% } %>
          </fieldset>
          <fieldset>
          <% if (serInfBancariaObrigatoria) { %>
            <h3 class="legend">
              <span><hl:message key="rotulo.servidor.informacoesbancarias"/></span>
            </h3>
            <div class="row">
              <div class="form-group col-sm-6">  
                <div class="row">
                  <div class="col-sm-3">                                
                    <label for="numBanco">
                      <hl:message key="rotulo.servidor.informacoesbancarias.banco.abreviado"/>
                    </label>
                    <hl:htmlinput name="numBanco" 
                                  type="text" 
                                  classe="form-control" 
                                  di="numBanco" 
                                  size="3" 
                                  mask="#D3"
                    />
                  </div>
                  <div class="col-sm-9">              
                    <label for="numAgencia">
                      <hl:message key="rotulo.servidor.informacoesbancarias.agencia.abreviado"/>
                    </label>
                      <hl:htmlinput name="numAgencia" 
                                    type="text" 
                                    classe="form-control" 
                                    di="numAgencia" 
                                    size="5"
                                    mask="#*30"/>                                                                                                                                  
                  </div> 
                </div>             
                </div>
                <div class="form-group col-sm-3">
                  <label for="numConta">
                    <hl:message key="rotulo.servidor.informacoesbancarias.conta.abreviado"/>
                  </label>

                  <hl:htmlinput name="numConta" 
                                type="text" 
                                classe="form-control" 
                                di="numConta"
                                size="12"
                                mask="#*40" 
                                nf="<%=TextHelper.forHtmlAttribute( exigeSenhaServidor ? "senha" : "btnEnvia" )%>" />
                </div>                                
              </div>            
          </fieldset>
          <% } %>          

          <%-- Solicita novo valor e novo prazo quando o parametro de servico 277 esta habilitado --%>
          <% if (pulaInformacaoValorPrazo && responsavel.isCsaCor()) {%>
          <fieldset>
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="ADE_VLR">
                  <hl:message key="rotulo.consignacao.valor.parcela.novo"/> (<%=rotuloMoeda %>)
                </label>
                <hl:htmlinput name="ADE_VLR" type="text" classe="form-control" size="10" di="ADE_VLR" value="<%=TextHelper.forHtmlAttribute((autdes.getAttribute(Columns.ADE_VLR)))%>"/>
              </div>
              <div class="form-group col-sm-6">
                <label for="ADE_PRAZO">
                  <hl:message key="rotulo.consignacao.prazo.novo"/> (<%=rotuloMoeda %>)
                </label>
                <hl:htmlinput name="ADE_PRAZO" type="text" classe="form-control" size="10" di="ADE_PRAZO" value="<%=TextHelper.forHtmlAttribute((autdes.getAttribute(Columns.ADE_PRAZO)))%>"/>
              </div>
            </div>                              
          </fieldset>
          <%} %>  

          <%-- Solicita código de autorização para confirmar solicitação --%>
          <% if (exigeCodAutSolicitacao) { %>            
          <fieldset>
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="codAutorizacao">
                  <hl:message key="rotulo.consignacao.codigo.autorizacao"/>
                </label>
                  <hl:htmlinput name="codAutorizacao" 
                                type="text" 
                                classe="form-control" 
                                di="codAutorizacao"
                                mask="#D4"/>                                                                                                                                  
              </div>
            </div>                              
          </fieldset>
          <% } %>
        <% if (exigeMotivo) { %>
        <%-- Utiliza a tag library EfetivaAcaoMotivoOperacaoTag.java para exibir os dados do Tipo de Motivo da Operação --%>
          <hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="<%=ApplicationResourcesHelper.getMessage("mensagem.confirmacao.solicitacao", responsavel)%>" />
        <%-- Fim dos dados do Motivo da Operação --%>
        <% } %>  
      </div>
      <% if (exigeSenhaServidor) { %>
      <div class="row">
          <div class="col-sm">    
            <div class="card">
              <div class="card-header">
                <h2 class="card-header-title"><hl:message key="rotulo.senha.servidor.consulta.singular"/></h2>
              </div>
              <div class="card-body">
                <div class="row">
                  <div class="form-group col-sm-6">                    
                    <%
                      String mascaraLogin = (String) ParamSist.getInstance().getParam(CodedValues.TPC_MASCARA_LOGIN_EXTERNO_SERVIDOR, responsavel);
                      if (!TextHelper.isNull(mascaraLogin)) {
                    %>
                    <hl:htmlinput name="serLogin" 
                              di="serLogin"
                              type="text" 
                              classe="form-control"                                    
                              mask="<%=TextHelper.forHtmlAttribute(mascaraLogin)%>" 
                    />
                    <% } %>
                    <hl:senhaServidorv4 senhaObrigatoria="true"                                
                                senhaParaAutorizacaoReserva="true"
                                nomeCampoSenhaCriptografada="serAutorizacao"
                                rseCodigo="<%=rseCodigo%>"
                                classe="form-control"
                                inputSizeCSS="col-0"
                                nf="btnEnvia" />
                   </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <% } %>
          <hl:htmlinput name="MM_update"      type="hidden" value="form1" />
          <hl:htmlinput name="acao"           type="hidden" value="salvar" />
          <hl:htmlinput name="ADE_CODIGO"     type="hidden" di="ADE_CODIGO" value="<%=TextHelper.forHtmlAttribute(adeCodigo)%>" />
          <hl:htmlinput name="rseCodigo"      type="hidden" di="rseCodigo" value="<%=TextHelper.forHtmlAttribute(rseCodigo)%>" />        
    </div>          
    <div class="btn-action" aria-label="<hl:message key="rotulo.botoes.acao.pagina"/>">                                                      
      <a name="btnCancela"
         class="btn btn-outline-danger mt-2"
         id="btnCancela" 
         href="#no-back"                     
         onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')">      
        <hl:message key="rotulo.botao.cancelar"/>
      </a>      
      <a name="btnEnvia" 
         class="btn btn-primary mt-2"
         id="btnEnvia" 
         href="#no-back" 
         onClick="<%="if (vfCamposSenha() && verificaInfBanco() " + ((cftdes != null) ? " && validarConfirmacaoAlteracao() " : "") + " && vf_confirma_solicitacao()) { f0.submit();}"%>" >        
        <hl:message key="rotulo.botao.confirmar"/>
      </a>                                                            
    </div>
    </form>      
  </div>
</div> 
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript" src="../js/javacrypt.js?<hl:message key="release.tag"/>"></script>
  <script src="../node_modules/responsive-bootstrap-tabs/jquery.responsivetabs.js"></script>

  <hl:senhaServidorv4 senhaObrigatoria="true"                                
              senhaParaAutorizacaoReserva="true"
              nomeCampoSenhaCriptografada="serAutorizacao"
              rseCodigo="<%=rseCodigo%>"
              classe="form-control"
              inputSizeCSS="col-0"
              nf="btnEnvia"
              scriptOnly="true" />

  <script type="text/JavaScript">
  var f0 = document.forms[0];
  
  $(function() {
      $('.nav-tabs').responsiveTabs();
  });
  
  var valor = 'IB';
  var validarInfBancaria = <%=TextHelper.forJavaScriptBlock(serInfBancariaObrigatoria && validarInfBancaria)%>;
  
  function formLoad() {
    if (f0.ALTERA_VLR_LIBERADO_S != null || f0.ALTERA_VLR_LIBERADO_N != null) {
      f0.ALTERA_VLR_LIBERADO_S.disabled = true;
      f0.ALTERA_VLR_LIBERADO_N.disabled = true;
    }
    focusFirstField();
  }
  
  function validarConfirmacaoAlteracao() {
    
    var alteracaoRealizada = !f0.ALTERA_VLR_LIBERADO_S.disabled || !f0.ALTERA_VLR_LIBERADO_N.disabled;
    
    // Se true é porque o campo coeficiente foi alterado
    if (alteracaoRealizada) {
      var mensagem = '<hl:message key="mensagem.confirmar.coeficiente.alterado.mensagem.inicial"/>';
      // O default é diminuir valor da prestação, mesmo que f0.ALTERA_VLR_LIBERADO[1] não esteja checkado
      if (f0.ALTERA_VLR_LIBERADO_S.checked) {
        mensagem += '\n<hl:message key="rotulo.confirmar.solicitacao.coeficiente.nova.financiado"/> ' + $("#COEFICIENTE_CALCULADO_FINANCIADO").val();  
      } else {
        mensagem += '\n<hl:message key="rotulo.confirmar.solicitacao.coeficiente.nova.parcela"/> ' + $("#COEFICIENTE_CALCULADO_PARCELA").val();
      }
      mensagem += '\n<hl:message key="mensagem.confirmar.coeficiente.alterado.mensagem.final"/>';
      if (!confirm(mensagem)) {
        return false;
      }    
    }  
    return true;  
  }
  
  function vf_confirma_solicitacao() {
    
    // Verificar se o coeficiente é menor
    var cft_old = <%=TextHelper.forJavaScriptBlock(cftVlrAtual)%>;
  
    if (f0.CFT_VLR != null && f0.CFT_VLR.value != '') {
      var cft_new = parse_num (f0.CFT_VLR.value);
  
      if (cft_new > 0) {
        if (cft_new == cft_old){
          f0.ALTERA_VLR_LIBERADO_S.checked = false;
          f0.ALTERA_VLR_LIBERADO_N.checked = false;
        } else if (cft_new < cft_old) {
          if (!f0.ALTERA_VLR_LIBERADO_S.checked && !f0.ALTERA_VLR_LIBERADO_N.checked) {
            alert('<hl:message key="mensagem.confirmar.solicitacao.escolha.alteracao"/>');
            if (f0.ALTERA_VLR_LIBERADO_S!=null) {
              f0.ALTERA_VLR_LIBERADO_S.focus();
            }
            return false;
          }
        } else {
          alert('<hl:message key="mensagem.erro.coeficiente.menor.anterior"/>');
          if (f0.CFT_VLR!=null)
            f0.CFT_VLR.focus();
          return false;
        }
        
        <% if (responsavel.isCsaCor() && exigeModalidadeOperacao) { %>  
        if (f0.tdaModalidadeOp.value == null || f0.tdaModalidadeOp.value == '') {
         alert('<hl:message key="mensagem.erro.modalidade.operacao.obrigatorio"/>');
         return false;
        }  
      <% }%>
      
      <% if (responsavel.isCsaCor() && exigeMatriculaSerCsa) { %>  
          if (f0.tdaMatriculaCsa.value == null || f0.tdaMatriculaCsa.value == '') {
            alert('<hl:message key="mensagem.erro.matricula.csa.obrigatoria"/>');
            return false;
          }
      <%}%>
      } else {
        alert('<hl:message key="mensagem.erro.coeficiente.maior.zero"/>');
        if (f0.CFT_VLR!=null)
          f0.CFT_VLR.focus();
        return false;
      }
    }
  
    <% if (responsavel.isCsaCor() && pulaInformacaoValorPrazo) { %>
    if ((f0.ADE_VLR.value.trim() == '' || f0.ADE_PRAZO.value.trim() == '')) {
      alert('<hl:message key="mensagem.erro.pula.etapa.obrigatorios"/>');
      return false;
    }
    <%}%>  
    
    <% if (exigeMotivo) { %>
    if(!confirmaAcaoConsignacao())  {
      return false;
    }<% } else { %>
      return confirm('<hl:message key="mensagem.confirmacao.confirmar.solicitacao"/>');
      return true;
    <% } %>
    return true;
  }
  
  function vfCamposSenha() {
    if (<%=(boolean)exigeCodAutSolicitacao%> && f0.codAutorizacao != null && f0.codAutorizacao.value == '') {
      alert('<hl:message key="mensagem.informe.ade.codigo.autorizacao"/>');
      f0.codAutorizacao.focus();
      return false;
    }
    if (<%=(boolean)exigeSenhaServidor%> && f0.serLogin != null && f0.serLogin.value == '') {
      alert('<hl:message key="mensagem.informe.ser.usuario"/>');
      f0.serLogin.focus();
      return false;
    }
    if (<%=(boolean)exigeSenhaServidor%> && f0.senha != null && trim(f0.senha.value) == '') {
      alert('<hl:message key="mensagem.informe.ser.senha"/>');
      f0.senha.focus();
      return false;
    }
  
    if (f0.senha != null && trim(f0.senha.value) != '') {
      CriptografaSenha(f0.senha, f0.serAutorizacao, false);
    }
    return true;
  }
  
  function verificaInfBanco() {
  <% if (serInfBancariaObrigatoria && rseTemInfBancaria) { %>
    var Controles = new Array("numBanco", "numAgencia", "numConta");
    var Msgs = new Array('<hl:message key="mensagem.informacaoBancariaObrigatoria"/>',
                         '<hl:message key="mensagem.informacaoBancariaObrigatoria"/>',
                         '<hl:message key="mensagem.informacaoBancariaObrigatoria"/>');
  
    var banco = Javacrypt.crypt(valor, formataParaComparacao(f0.numBanco.value))[0];
    var agencia = Javacrypt.crypt(valor, formataParaComparacao(f0.numAgencia.value))[0];
  
    var conta = formataParaComparacao(f0.numConta.value);
    var pos = 0;
    var letra = conta.substr(pos, 1);
    while (letra == 0 && pos < conta.length) {
      pos++;
      letra = conta.substr(pos, 1)  ;
    }
  
    conta = conta.substr(pos,conta.length);
    var conta1 = Javacrypt.crypt(valor, conta.substr(0, conta.length/2))[0];
    var conta2 = Javacrypt.crypt(valor, conta.substr(conta.length/2, conta.length))[0];
  
    if (ValidaCampos(Controles, Msgs)) {
      if (((banco != '<%=TextHelper.forJavaScriptBlock(numBanco)%>') || (agencia != '<%=TextHelper.forJavaScriptBlock(numAgencia)%>') || (conta1 != '<%=TextHelper.forJavaScriptBlock(numConta1)%>') || (conta2 != '<%=TextHelper.forJavaScriptBlock(numConta2)%>')) &&
           ((banco != '<%=TextHelper.forJavaScriptBlock(numBancoAlt)%>') || (agencia != '<%=TextHelper.forJavaScriptBlock(numAgenciaAlt)%>') || (conta1 != '<%=TextHelper.forJavaScriptBlock(numContaAlt1)%>') || (conta2 != '<%=TextHelper.forJavaScriptBlock(numContaAlt2)%>'))) {
        if (validarInfBancaria) {
          alert('<hl:message key="mensagem.informacaoBancariaIncorreta"/>');
          return false;
        }
        if(confirm('<hl:message key="mensagem.informacaoBancariaIncorreta.continuar"/>')) {
          return true;
        } else {
          f0.numBanco.focus();
          return false;
        }
      }
      return true;
    }
    return false;
  <% } else { %>
    return true;
  <% } %>
  }

  $(function () {
  
    if ($('#CFT_VLR').val() == null || $('#CFT_VLR').val() == '') {
        return false;
    }    
    
    var cftVlrOriginal = $('#CFT_VLR').val();   
    var cacheValores = {};  
    var calcularCoeficienteEvent = function () {
  
      var cftVlr = $('#CFT_VLR').val();
      var adeCodigo = $('#ADE_CODIGO').val();
      var cftVlrNumber = new Number(cftVlr.replace(',', '.'));
      
      if (cftVlrNumber != 0) {
        
        var vlrLiberadoSelecionado = null;
        
        $('[name=ALTERA_VLR_LIBERADO]').each(function (i, e) {
          if (e.checked) {
            vlrLiberadoSelecionado = e.value;
          }
        });
        
        var parametrosBase = "ADE_CODIGO=" + adeCodigo + "&_skip_history_=1";      
        var parametros = "&ALTERA_VLR_LIBERADO=N";      
        var cftVlrUtilizadoN = "";
        
        if (!vlrLiberadoSelecionado || vlrLiberadoSelecionado === 'N') {
          parametros = parametrosBase + parametros + "&CFT_VLR=" + cftVlr;
          cftVlrUtilizadoN = cftVlr;
        } else {
          parametros = parametrosBase + parametros + "&CFT_VLR=" + cftVlrOriginal;
          cftVlrUtilizadoN = cftVlrOriginal;
        } 
        
          $.post("../v3/calcularCoeficiente?acao=calcular", parametros, function(data) {
              try {
                
                if (!cacheValores[cftVlrUtilizadoN]) {
                  cacheValores[cftVlrUtilizadoN] = {};
                }
                
                if (!cacheValores[cftVlrUtilizadoN]['N']) {
                  cacheValores[cftVlrUtilizadoN]['N'] ={};
                }
                
                cacheValores[cftVlrUtilizadoN]['N'].valor = data.valor;
                
                var vlrLiberadoSelecionadoAtual = null;
              
              $('[name=ALTERA_VLR_LIBERADO]').each(function (i, e) {
                if (e.checked) {
                  vlrLiberadoSelecionadoAtual = e.value;
                }
              });
                
                if (!vlrLiberadoSelecionadoAtual || vlrLiberadoSelecionadoAtual === 'N') {
                  var cftVlrAtual = $('#CFT_VLR').val();
                  if (cacheValores[cftVlrAtual] && cacheValores[cftVlrAtual]['N']) {
                    $("#COEFICIENTE_CALCULADO_PARCELA").val(cacheValores[cftVlrAtual]['N'].valor);
                  }
                } else {
                  if (cacheValores[cftVlrOriginal] && cacheValores[cftVlrOriginal]['N']) {
                    $("#COEFICIENTE_CALCULADO_PARCELA").val(cacheValores[cftVlrOriginal]['N'].valor);
                  }
                }
              
              } catch(err) {
              }
              
          }, "json");
        
          var parametros = "&ALTERA_VLR_LIBERADO=S";        
          var cftVlrUtilizadoS = "";
        
          if (vlrLiberadoSelecionado === 'S') {
            parametros = parametrosBase + parametros + "&CFT_VLR=" + cftVlr;
            cftVlrUtilizadoS = cftVlr;
          } 
          else 
          {
            parametros = parametrosBase + parametros + "&CFT_VLR=" + cftVlrOriginal;
            cftVlrUtilizadoS = cftVlrOriginal;
          } 
          
          $.post("../v3/calcularCoeficiente?acao=calcular", parametros, function(data) {
              try {              
                if (!cacheValores[cftVlrUtilizadoS]) {
                  cacheValores[cftVlrUtilizadoS] = {};
                }              
                if (!cacheValores[cftVlrUtilizadoS]['S']) {
                  cacheValores[cftVlrUtilizadoS]['S'] ={};
                }              
                cacheValores[cftVlrUtilizadoS]['S'].valor = data.valor;              
                
                var vlrLiberadoSelecionadoAtual = null;
              
              $('[name=ALTERA_VLR_LIBERADO]').each(function (i, e) {
                if (e.checked) {
                  vlrLiberadoSelecionadoAtual = e.value;
                  }
                });
                
                if (vlrLiberadoSelecionadoAtual === 'S') {
                    
                  var cftVlrAtual = $('#CFT_VLR').val();
                    if (cacheValores[cftVlrAtual] && cacheValores[cftVlrAtual]['S']) {
                      $("#COEFICIENTE_CALCULADO_FINANCIADO").val(cacheValores[cftVlrAtual]['S'].valor);
                    }
                } 
                else 
                {
                  if (cacheValores[cftVlrOriginal] && cacheValores[cftVlrOriginal]['S']) {
                    $("#COEFICIENTE_CALCULADO_FINANCIADO").val(cacheValores[cftVlrOriginal]['S'].valor);
                  }
                }            
              } catch(err) {}
          }, "json");
      } 
      else 
      {
        $("#COEFICIENTE_CALCULADO_FINANCIADO").val("");
        $("#COEFICIENTE_CALCULADO_PARCELA").val("");
      }
      return false;         
    }; 
    
    $('#CFT_VLR').bind('keyup', function (e) {
      calcularCoeficienteEvent();
    });
    
    $('[name=ALTERA_VLR_LIBERADO]').bind('change', function (e) {
      calcularCoeficienteEvent();
    });
    
    calcularCoeficienteEvent();   
  });

  window.onload = formLoad;
  </script>
  <hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="" scriptOnly="true" />
</c:set>

<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
