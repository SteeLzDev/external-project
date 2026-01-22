<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.usuario.LoginHelper"%>
<%@ page import="com.zetra.econsig.helper.upload.UploadHelper"%>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ page import="com.zetra.econsig.dto.web.*"%>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String msgSucess = request.getAttribute("msgSucess") != null ? (String) request.getAttribute("msgSucess") : "";
boolean ok = request.getAttribute("ok") != null ? (Boolean) request.getAttribute("ok") : false;

boolean usaRecuperacaoSenhaNoAutoCadastro = (Boolean) request.getAttribute("usaRecuperacaoSenhaNoAutoCadastro");
boolean cadastroAvancadoUsuSer = (Boolean) request.getAttribute("cadastroAvancadoUsuSer");
boolean atualizaDadosServidor = (Boolean) request.getAttribute("atualizaDadosServidor");
boolean senhaServidorNumerica = (Boolean) request.getAttribute("senhaServidorNumerica");
boolean celularSerEditavel = (boolean) request.getAttribute("celularSerEditavel");
boolean telefoneSerEditavel = (boolean) request.getAttribute("telefoneSerEditavel");
boolean emailSerEditavel = (boolean) request.getAttribute("emailSerEditavel");

String cpf = request.getAttribute("cpf") != null ? (String) request.getAttribute("cpf") : "";
String matricula = request.getAttribute("matricula") != null ? (String) request.getAttribute("matricula") : ""; 
String orgCod = request.getAttribute("orgCod") != null ? (String) request.getAttribute("orgCod") : "";
Integer intpwdStrength = (Integer) request.getAttribute("intpwdStrength");

int tamMaxSenhaServidor = (int) request.getAttribute("tamMaxSenhaServidor");
int tamMinSenhaServidor = (int) request.getAttribute("tamMinSenhaServidor");
int pwdStrengthLevel = (int) request.getAttribute("pwdStrengthLevel");
String strMensagemSenha = (String) request.getAttribute("strMensagemSenha");
String tamMax = (String) request.getAttribute("tamMax");
String tamMaxText = Math.round(Integer.parseInt(tamMax)) + " " + ApplicationResourcesHelper.getMessage("rotulo.kilobyte.abreviado", responsavel);

String termoUsoCadSenha = (String) request.getAttribute("termoUsoCadSenha");
String politicaPrivacidadeCadSenha = (String) request.getAttribute("politicaPrivacidadeCadSenha");
String politicaPrivacidadeFile = (String) request.getAttribute("politicaPrivacidadeFile");

UsuarioTransferObject usuarioSer = request.getAttribute("usuarioSer") != null ? (UsuarioTransferObject) request.getAttribute("usuarioSer") : null;

CustomTransferObject servidor = (CustomTransferObject) request.getAttribute("servidor");

List<?> listaOrgaos =  request.getAttribute("listaOrgaos") != null ? (List<?>) request.getAttribute("listaOrgaos") : null;
List<?> allowedDomains =  request.getAttribute("allowedDomains") != null ? (List<?>) request.getAttribute("allowedDomains") : null;
List<TransferObject> listaAnexos = (List<TransferObject>) request.getAttribute("listaAnexos");

List<CampoQuestionarioDadosServidor> camposQuestionarioDadosServidor =  request.getAttribute("camposQuestionarioDadosServidor") != null ? (List<CampoQuestionarioDadosServidor>) request.getAttribute("camposQuestionarioDadosServidor") : null;
%>
<c:set var="bodyContent">

<form name="form1" method="post" action="../v3/cadastrarSenhaServidor?acao=salvar" autocomplete="off" ENCTYPE="multipart/form-data">
  <input name="score" type="hidden" id="score">
  <input name="matchlog" type="hidden" id="matchlog">
  <% if(!ok) { %>
    <div class="alert alert-warning" role="alert">
      <hl:message key="mensagem.auto.cadastro.senha.ser.ajuda.cadastro"/>
    </div>
    <% if (!usaRecuperacaoSenhaNoAutoCadastro) { %>
    <div class="alert alert-warning" role="alert">
      <%=strMensagemSenha%>
    </div>
    <% } %>
  <% } %>
  <%if (!ok) {%>
  <%-- CPF --%>
  <div class="form-group">  
    <hl:campoCPFv4 name="<%=(String)(cadastroAvancadoUsuSer ? "cpfDisabled" : TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_CPF))%>"
                   configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_CPF)%>"
                   value="<%=TextHelper.forHtmlAttribute(cpf)%>"
                   others="<%=(String)(cadastroAvancadoUsuSer ? "disabled" : "")%>"
                   placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.cpf", responsavel) %>"
                   textHelpKey="mensagem.auto.cadastro.senha.ser.ajuda.cpf"
    />
    <% if (cadastroAvancadoUsuSer) { %>
    
    <input type="hidden" 
           id="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_CPF)%>"
           name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_CPF)%>"
           value="<%=TextHelper.forHtmlAttribute(cpf)%>"
    />
    
    <% } %>
  </div>
  <%-- MATRICULA --%>
  <show:showfield key="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_MATRICULA)%>" >
    <div class="form-group">
      <label for="matriculaDisabled"><hl:message key="rotulo.servidor.matricula"/></label>
      <hl:htmlinput name="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_MATRICULA)%>"
                    di="matriculaDisabled"
                    value="<%=TextHelper.forHtmlAttribute(matricula)%>" 
                    type="text"
                    classe="form-control"
                    onFocus="SetarEventoMascaraV4(this,'#*30',true);"
                    onBlur="fout(this);ValidaMascaraV4(this);"
                    size="20"
                    configKey="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_MATRICULA)%>"
                    placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.matricula", responsavel) %>"
                    others="<%=(String)(cadastroAvancadoUsuSer ? "disabled" : "")%>"
      />
    </div>
    <% if (cadastroAvancadoUsuSer) { %>
    
    <input type="hidden" 
           id="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_MATRICULA)%>"
           name="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_MATRICULA)%>"
           value="<%=TextHelper.forHtmlAttribute(matricula)%>"
    />
    
    <% } %>
    <hl:message key="mensagem.auto.cadastro.senha.ser.ajuda.matricula"/>
  </show:showfield>
  <%-- ORGAO --%>
  <show:showfield key="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_ORG_CODIGO)%>" >
    <%
        // lista de órgãos 
        if (listaOrgaos != null) {
          request.setAttribute("listaOrgaos",listaOrgaos);  
          if (listaOrgaos.size() > 1) {
    %>
        <div class="form-group ">
          <label for="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_ORG_CODIGO)%>"><hl:message key="rotulo.orgao.singular"/></label>
          <hl:htmlcombo  listName="listaOrgaos" 
                         name="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_ORG_CODIGO)%>"
                         di="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_ORG_CODIGO)%>" 
                         fieldValue="<%=TextHelper.forHtmlAttribute( Columns.ORG_CODIGO)%>" 
                         fieldLabel="<%=(String)(Columns.ORG_NOME + ";" + Columns.ORG_IDENTIFICADOR)%>" 
                         selectedValue="<%=TextHelper.forHtmlAttribute( orgCod )%>"
                         notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>" 
                         configKey="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_ORG_CODIGO)%>"
                         disabled="<%=(boolean)cadastroAvancadoUsuSer%>"
                         classe="form-control"
          >
          </hl:htmlcombo>
        </div>
     
     <%
          } else {
           Iterator<?> itOrgReg = listaOrgaos.iterator();
           CustomTransferObject orgRegTO = (CustomTransferObject) itOrgReg.next();
           String orgCodigo = (String) orgRegTO.getAttribute(Columns.ORG_CODIGO);
      %>
         <input type="HIDDEN" name="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_ORG_CODIGO)%>" value="<%=TextHelper.forHtmlAttribute(orgCodigo)%>">
    <%
          }
       }
    %>
  </show:showfield>
  
   <%-- TPC 889 HABILITADO --%>
  <%
      if (atualizaDadosServidor) {
  %>  
      <%-- BANCO SALÁRIO --%>
      <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_BANCO_SAL)%>">
        <div class="form-group">
          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_BANCO_SAL)%>"> <hl:message key="rotulo.servidor.codigo.banco" /> </label>
          <hl:htmlinput di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_BANCO_SAL)%>"
                        name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_BANCO_SAL)%>"
                        type="text" size="8" maxlength="8"
                        configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_BANCO_SAL)%>"
                        onFocus="SetarEventoMascara(this,'#A8',true);"
                        onBlur="<%=TextHelper.forJavaScript("fout(this);ValidaMascara(this);if (!IsNulo(document.forms[0]."
                                + FieldKeysConstants.CAD_SERVIDOR_EDITAR_BANCO_SAL
                                + ")) {SelecionaComboBanco(document.forms[0].RSE_BANCOS, document.forms[0]."
                                + FieldKeysConstants.CAD_SERVIDOR_EDITAR_BANCO_SAL + ".value, arrayBancos);}")%>"
                        classe="form-control"
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.codigo.banco", responsavel)%>" 
            />
        </div>
        <div class="form-group">
          <label for="RSE_BANCOS1"><hl:message key="rotulo.servidor.banco" /></label> 
            <SELECT
              id="RSE_BANCOS1"
              NAME="RSE_BANCOS"
              CLASS="form-control"
              onChange="document.forms[0].<%=TextHelper.forJavaScript(FieldKeysConstants.CAD_SERVIDOR_EDITAR_BANCO_SAL)%>.value = document.forms[0].RSE_BANCOS.value;"
              <%if (ShowFieldHelper.isDisabled(FieldKeysConstants.CAD_SERVIDOR_EDITAR_BANCO_SAL, responsavel)) {%>
              DISABLED <%}%>>
              <OPTION VALUE="" SELECTED><hl:message key="rotulo.campo.selecione" /></OPTION>
            </SELECT>
        </div>
      </show:showfield>
      <%-- AGÊNCIA SALÁRIO --%>
      <show:showfield key="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_EDITAR_AGENCIA_SALARIO)%>" >
        <div class="form-group">
          <label for="agSalarioDisabled"><hl:message key="rotulo.servidor.informacoesbancarias.agencia"/></label>
           <hl:htmlinput
                      di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_AGENCIA_SALARIO)%>"
                      name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_AGENCIA_SALARIO)%>"
                      type="text" size="10" mask="#*30"
                      configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_AGENCIA_SALARIO)%>"
                      maxlength="30"
                      onFocus="SetarEventoMascara(this,'#D5',true);"
                      onBlur="fout(this);ValidaMascara(this);"
                      classe="form-control"
                      placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.codigo.agencia", responsavel)%>" />
        </div>
      </show:showfield>
      <%-- CONTA SALÁRIO --%>
      <show:showfield key="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_EDITAR_CONTA_SALARIO)%>" >
        <div class="form-group">
          <label for="contaSalarioDisabled"><hl:message key="rotulo.servidor.informacoesbancarias.conta"/></label>
          <hl:htmlinput
                      di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CONTA_SALARIO)%>"
                      name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CONTA_SALARIO)%>"
                      type="text" size="10" mask="#*40" maxlength="40"
                      onFocus="SetarEventoMascara(this,'#*40',true);"
                      configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CONTA_SALARIO)%>"
                      onBlur="fout(this);ValidaMascara(this);"
                      classe="form-control"
                      placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.codigo.conta",
                  responsavel)%>" />
        </div>
      </show:showfield>
      <%-- BANCO SALÁRIO ALTERNATIVO --%>
      <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_BANCO_SAL_2)%>">
        <div class="form-group">
          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_BANCO_SAL_2)%>"> <hl:message key="rotulo.servidor.codigo.banco.alternativo" /> </label>
          <hl:htmlinput di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_BANCO_SAL_2)%>"
                        name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_BANCO_SAL_2)%>"
                        type="text" size="8" maxlength="8"
                        configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_BANCO_SAL_2)%>"
                        onFocus="SetarEventoMascara(this,'#A8',true);"
                        onBlur="<%=TextHelper.forJavaScript("fout(this);ValidaMascara(this);if (!IsNulo(document.forms[0]."
                                + FieldKeysConstants.CAD_SERVIDOR_EDITAR_BANCO_SAL_2
                                + ")) {SelecionaComboBanco(document.forms[0].RSE_BANCOS_2, document.forms[0]."
                                + FieldKeysConstants.CAD_SERVIDOR_EDITAR_BANCO_SAL_2 + ".value, arrayBancos);}")%>"
                        classe="form-control"
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.banco.alternativo", responsavel)%>" 
            />
        </div>
        <div class="form-group">
          <label for="RSEBANCOS_2"><hl:message key="rotulo.servidor.banco" /></label> 
            <SELECT
              id="RSEBANCOS_2"
              NAME="RSE_BANCOS_2"
              CLASS="form-control"
              onChange="document.forms[0].<%=TextHelper.forJavaScript(FieldKeysConstants.CAD_SERVIDOR_EDITAR_BANCO_SAL_2)%>.value = document.forms[0].RSE_BANCOS_2.value;"
              <%if (ShowFieldHelper.isDisabled(FieldKeysConstants.CAD_SERVIDOR_EDITAR_BANCO_SAL_2, responsavel)) {%>
              DISABLED <%}%>>
              <OPTION VALUE="" SELECTED><hl:message key="rotulo.campo.selecione" /></OPTION>
            </SELECT>
        </div>
      </show:showfield>
      <%-- AGÊNCIA SALÁRIO ALTERNATIVO--%>
      <show:showfield key="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_EDITAR_AGENCIA_SALARIO2)%>" >
        <div class="form-group">
          <label for="agSalarioDisabled"><hl:message key="rotulo.servidor.codigo.agencia.alternativo"/></label>
           <hl:htmlinput
                      di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_AGENCIA_SALARIO2)%>"
                      name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_AGENCIA_SALARIO2)%>"
                      type="text" size="10" mask="#*30"
                      configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_AGENCIA_SALARIO2)%>"
                      maxlength="30"
                      onFocus="SetarEventoMascara(this,'#D5',true);"
                      onBlur="fout(this);ValidaMascara(this);"
                      classe="form-control"
                      placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.codigo.agencia.alternativa", responsavel)%>" />
        </div>
      </show:showfield>
      <%-- CONTA SALÁRIO ALTERNATIVO--%>
      <show:showfield key="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_EDITAR_CONTA_SALARIO2)%>" >
        <div class="form-group">
          <label for="contaSalarioDisabled"><hl:message key="rotulo.servidor.codigo.conta.alternativo"/></label>
          <hl:htmlinput
                      di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CONTA_SALARIO2)%>"
                      name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CONTA_SALARIO2)%>"
                      type="text" size="10" mask="#*40" maxlength="40"
                      onFocus="SetarEventoMascara(this,'#*40',true);"
                      configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CONTA_SALARIO2)%>"
                      onBlur="fout(this);ValidaMascara(this);"
                      classe="form-control"
                      placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.codigo.conta.alternativa", responsavel)%>" />
        </div>
      </show:showfield>
      <%-- ASSOCIADO --%>
      <show:showfield key="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_EDITAR_ASSOCIADO)%>" >
        <div class="form-group">
          <div class="form-group my-0">
            <span id="associado"><hl:message key="rotulo.servidor.associado" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_ASSOCIADO)%>"/></span>
          </div>
          <div class="form-check mt-2">
            <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_ASSOCIADO)%>"
                           di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_ASSOCIADO) + "_S"%>"
                           type="radio"
                           value="S"
                           mask="#*10"
                           configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_ASSOCIADO)%>"
                           classe="form-check-input ml-1"
             />
            <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.sim"/>' for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_ASSOCIADO) + "_S"%>"><hl:message key="rotulo.sim"/></label>
            <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_ASSOCIADO)%>"
                           di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ASSOCIADO) + "_N"%>"
                           type="radio"
                           value="N"
                           checked="true"
                           mask="#*10"
                           configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_ASSOCIADO)%>"
                           classe="form-check-input ml-1"
             />
            <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.nao"/>' for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_ASSOCIADO) + "_N"%>"><hl:message key="rotulo.nao"/></label>
          </div>
          
        </div>
      </show:showfield>
      <%-- CLT --%>
      <show:showfield  key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CLT)%>">
        <div class="form-group">
          <div class="form-group my-0">
            <span id="clt"><hl:message key="rotulo.servidor.clt" /></span>
          </div>
          <div class="form-check mt-2">
            <hl:htmlinput di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CLT) + "_S"%>"
                          name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CLT)%>"
                          type="radio" 
                          value="S" 
                          mask="#*10"
                          configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CLT)%>"
                          classe="form-check-input ml-1" 
            />
            <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.sim"/>' for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CLT) + "_S"%>"><hl:message key="rotulo.sim" /></label>
            <hl:htmlinput di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CLT) + "_N"%>"
                          name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CLT)%>"
                          type="radio" 
                          value="N" 
                          checked="true"
                          mask="#*10"
                          configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CLT)%>"
                          classe="form-check-input ml-1" 
            />
            <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.nao"/>' for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CLT) + "_N"%>"><hl:message key="rotulo.nao" /></label>
          </div>
        </div>
      </show:showfield>
      <%-- DATA FIM ENGAJAMENTO --%>
      <show:showfield key="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_EDITAR_DATA_FIM_ENGAJAMENTO)%>" >
        <div class="form-group">
          <label for="dataFimEngajamentoDisabled"><hl:message key="rotulo.servidor.engajado"/></label>
          <hl:htmlinput name="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_EDITAR_DATA_FIM_ENGAJAMENTO)%>"
                        di="dataFimEngajamentoDisabled"
                        type="text"
                        classe="form-control"
                        mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                        size="10"
                        configKey="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_EDITAR_DATA_FIM_ENGAJAMENTO)%>"
                        placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>"
          />
        </div>
      </show:showfield>
      <%-- DATA LIMITE PERMANENCIA --%>
      <show:showfield key="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_EDITAR_DATA_LIMITE_PERMANENCIA)%>" >
        <div class="form-group">
          <label for="dataLimitePermanenciaDisabled"><hl:message key="rotulo.servidor.dataLimitePermanencia"/></label>
          <hl:htmlinput name="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_EDITAR_DATA_LIMITE_PERMANENCIA)%>"
                        di="dataLimitePermanenciaDisabled"
                        type="text"
                        classe="form-control"
                        mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                        size="10"
                        configKey="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_EDITAR_DATA_LIMITE_PERMANENCIA)%>"
                        placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>"
          />
        </div>
      </show:showfield>
      <%-- ESTABILIZADO --%>
      <show:showfield  key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_ESTABILIZADO)%>">
        <div class="form-group">
          <div class="form-group my-0">
            <span id="clt"><hl:message key="rotulo.servidor.estabilizado" /></span>
          </div>
          <div class="form-check mt-2">
            <hl:htmlinput di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_ESTABILIZADO) + "_S"%>"
                          name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_ESTABILIZADO)%>"
                          type="radio" 
                          value="S" 
                          mask="#*10"
                          configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_ESTABILIZADO)%>"
                          classe="form-check-input ml-1" 
            />
            <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.sim"/>' for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_ESTABILIZADO) + "_S"%>"><hl:message key="rotulo.sim" /></label>
            <hl:htmlinput di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_ESTABILIZADO) + "_N"%>"
                          name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_ESTABILIZADO)%>"
                          type="radio" 
                          value="N" 
                          checked="true"
                          mask="#*10"
                          configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_ESTABILIZADO)%>"
                          classe="form-check-input ml-1" 
            />
            <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.nao"/>' for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_ESTABILIZADO) + "_N"%>"><hl:message key="rotulo.nao" /></label>
          </div>
        </div>
      </show:showfield>
      <%-- MUNICIPIO LOTAÇÃO --%>
      <show:showfield key="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_EDITAR_MUNICIPIO_LOTACAO)%>" >
        <div class="form-group">
          <label for="municipioLotacaoDisabled"><hl:message key="rotulo.servidor.municipioLotacao"/></label>
          <hl:htmlinput name="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_EDITAR_MUNICIPIO_LOTACAO)%>"
                        di="municipioLotacaoDisabled"
                        type="text" 
                        size="40" 
                        mask="#*40"
                        classe="form-control"
                        configKey="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_EDITAR_MUNICIPIO_LOTACAO)%>"
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.municipio.lotacao", responsavel)%>"
          />
        </div>
      </show:showfield>
      <%-- PRAÇA --%>
      <show:showfield key="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_EDITAR_PRACA)%>" >
        <div class="form-group">
          <label for="pracaDisabled"><hl:message key="rotulo.servidor.praca"/></label>
          <hl:htmlinput name="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_EDITAR_PRACA)%>"
                        di="pracaDisabled"
                        type="textarea"
                        classe="form-control"
                        rows="6"
                        configKey="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_EDITAR_PRACA)%>"
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.praca", responsavel) %>"
          />
        </div>
      </show:showfield>
      <%-- PRAZO --%>
      <show:showfield key="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_EDITAR_PRAZO)%>" >
        <div class="form-group">
          <label for="agSalarioDisabled"><hl:message key="rotulo.servidor.prazo"/></label>
          <hl:htmlinput name="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_EDITAR_PRAZO)%>"
                        di="agSalarioDisabled"
                        type="text"
                        classe="form-control"
                        mask="#D11"
                        configKey="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_EDITAR_PRAZO)%>"
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.prazo", responsavel) %>"
          />
        </div>
      </show:showfield>
       
      <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_NOME_PAI)%>">
        <div class="form-group">
          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_NOME_PAI)%>"><hl:message key="rotulo.servidor.nomePai" /></label>
          <hl:htmlinput
            di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_NOME_PAI)%>"
            name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_NOME_PAI)%>"
            type="text" size="32" mask="#*100"
            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.nome.pai", responsavel)%>"
            classe="form-control"
            value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_NOME_PAI)))%>"
            configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_NOME_PAI)%>" />
        </div>
      </show:showfield>
        
      <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_NOME_CONJUGE)%>">
        <div class="form-group">
          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_NOME_CONJUGE)%>"><hl:message key="rotulo.servidor.nome.conjuge" /></label>
          <hl:htmlinput
            di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_NOME_CONJUGE)%>"
            name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_NOME_CONJUGE)%>"
            type="text" size="32" mask="#*100"
            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.nome.conjuge", responsavel)%>"
            classe="form-control"
            value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_NOME_CONJUGE)))%>"
            configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_NOME_CONJUGE)%>" />
        </div>
      </show:showfield>
        
      <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_NACIONALIDADE)%>">
        <div class="form-group">
          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_NACIONALIDADE)%>"><hl:message key="rotulo.servidor.nacionalidade" /></label>
          <hl:htmlinput
            di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_NACIONALIDADE)%>"
            name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_NACIONALIDADE)%>"
            type="text" size="32"
            configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_NACIONALIDADE)%>"
            mask="#*40"
            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.nacionalidade", responsavel)%>"
            classe="form-control" />
        </div>
      </show:showfield>
        
      <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CID_NASC)%>">
        <div class="form-group">
          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CID_NASC)%>"><hl:message key="rotulo.endereco.cidade" /></label>
          <hl:htmlinput
            di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CID_NASC)%>"
            name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CID_NASC)%>"
            type="text"
            configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CID_NASC)%>"
            mask="#*40"
            placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.mensagem.endereco.cidade", responsavel)%>"
            classe="form-control" />
        </div>
      </show:showfield>
      
      <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_UF_NASC)%>">
        <div class="form-group">
          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_UF_NASC)%>"><hl:message key="rotulo.servidor.estado.nascimento.abrev" /></label>
          <hl:campoUFv4
            name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_UF_NASC)%>"
            classe="form-control"
            di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_UF_NASC)%>"
            rotuloUf="rotulo.estados"
            placeHolder="<%=(String) ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.uf.nascimento", responsavel)%>"
            valorCampo="<%=JspHelper.geraComboUF(FieldKeysConstants.CAD_SERVIDOR_EDITAR_UF_NASC, null, ShowFieldHelper.isDisabled(FieldKeysConstants.CAD_SERVIDOR_EDITAR_UF_NASC, responsavel), responsavel)%>" />
        </div>
      </show:showfield>
        
      <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_EST_CIVIL)%>">
        <div class="form-group">
          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_EST_CIVIL)%>"><hl:message key="rotulo.servidor.estadoCivil" /></label>
          <hl:htmlcombo listName="listEstadoCivil"
            di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_EST_CIVIL)%>"
            name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_EST_CIVIL)%>"
            fieldValue="<%=TextHelper.forHtmlAttribute(Columns.EST_CIVIL_CODIGO)%>"
            fieldLabel="<%=TextHelper.forHtmlAttribute(Columns.EST_CIVIL_DESCRICAO)%>"
            notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>"
            configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_EST_CIVIL)%>"
            classe="form-control">
          </hl:htmlcombo>
        </div>
      </show:showfield>
      
      <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_ESTADO_CIVIL)%>">
        <div class="form-group">
          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_ESTADO_CIVIL)%>"><hl:message key="rotulo.servidor.estadoCivil" /></label>
          <hl:htmlcombo listName="listEstadoCivil"
            di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_ESTADO_CIVIL)%>"
            name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_ESTADO_CIVIL)%>"
            fieldValue="<%=TextHelper.forHtmlAttribute(Columns.EST_CIVIL_CODIGO)%>"
            fieldLabel="<%=TextHelper.forHtmlAttribute(Columns.EST_CIVIL_DESCRICAO)%>"
            notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>"
            configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_ESTADO_CIVIL)%>"
            classe="form-control">
          </hl:htmlcombo>
        </div>
      </show:showfield>
        
      <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_NRO_IDT)%>">
        <div class="form-group">
          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_NRO_IDT)%>"><hl:message key="rotulo.servidor.cartIdentidade" /></label>
          <hl:htmlinput
            di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_NRO_IDT)%>"
            name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_NRO_IDT)%>"
            type="text" size="10"
            configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_NRO_IDT)%>"
            mask="#*40"
            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.numero.identidade", responsavel)%>"
            classe="form-control" />
        </div>
      </show:showfield> 
                  
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_EMISSOR_IDT)%>">    
          <div class="form-group">
            <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_EMISSOR_IDT)%>"><hl:message key="rotulo.servidor.rg.orgao.emissor" /></label>
            <hl:htmlinput
              di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_EMISSOR_IDT)%>"
              name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_EMISSOR_IDT)%>"
              type="text" size="5"
              configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_EMISSOR_IDT)%>"
              mask="#*40" classe="form-control"
              placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.orgao.emissor", responsavel)%>" />
          </div>
       </show:showfield>   
       
        <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_UF_IDT)%>">
         <div class="form-group">
           <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_UF_IDT)%>"><hl:message key="rotulo.servidor.rg.uf.identidade" /></label>
           <hl:campoUFv4
             name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_UF_IDT)%>"
             classe="form-control"
             di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_UF_IDT)%>"
             rotuloUf="rotulo.estados"
             placeHolder="<%=(String) ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.uf", responsavel)%>"
             valorCampo="<%=JspHelper.geraComboUF(FieldKeysConstants.CAD_SERVIDOR_EDITAR_UF_IDT, null, ShowFieldHelper.isDisabled(FieldKeysConstants.CAD_SERVIDOR_EDITAR_UF_IDT, responsavel), responsavel)%>" />
         </div>
       </show:showfield>
                  
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_DATA_IDT)%>">   
          <div class="form-group">
              <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_DATA_IDT)%>"><hl:message key="rotulo.servidor.rg.data.emissao" /></label>
              <hl:htmlinput
                di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_DATA_IDT)%>"
                name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_DATA_IDT)%>"
                type="text" size="10"
                mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_DATA_IDT)%>"
                classe="form-control"
                placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>" />
          </div>
       </show:showfield>
       
       <show:showfield  key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CART_PROF)%>">
          <div class="form-group">
            <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CART_PROF)%>"><hl:message key="rotulo.servidor.cartTrabalho" /></label>
            <hl:htmlinput
              di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CART_PROF)%>"
              name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CART_PROF)%>"
              type="text" size="32"
              configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CART_PROF)%>"
              mask="#*40"
              placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.cartTrabalho", responsavel)%>"
              classe="form-control" />
          </div>
        </show:showfield>
          
        <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_PIS)%>">
          <div class="form-group">
            <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_PIS)%>"><hl:message key="rotulo.servidor.pis" /></label>
            <hl:htmlinput
              di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_PIS)%>"
              name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_PIS)%>"
              type="text" size="32"
              configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_PIS)%>"
              mask="#*40" classe="form-control"
              placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.pis", responsavel)%>" />
          </div>
        </show:showfield>
        
       
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_END)%>">
         <div class="form-group">
            <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_END)%>"><hl:message key="rotulo.endereco.logradouro" /></label>
            <hl:htmlinput
              di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_END)%>"
              name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_END)%>"
              type="text" size="32"
              configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_END)%>"
              mask="#*100" classe="form-control"
              placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.mensagem.endereco.logradouro", responsavel)%>" />
         </div>
       </show:showfield>
        
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_NRO)%>">
          <div class="form-group">
            <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_NRO)%>"><hl:message key="rotulo.endereco.numero" /></label>
            <hl:htmlinput
              di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_NRO)%>"
              name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_NRO)%>"
              type="text" size="5"
              configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_NRO)%>"
              mask="#D11" classe="form-control"
              placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.mensagem.endereco.numero", responsavel)%>" />
          </div>
       </show:showfield>
    
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_COMPL)%>">
        <div class="form-group">
          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_COMPL)%>"><hl:message key="rotulo.endereco.complemento" /></label>
          <hl:htmlinput
            di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_COMPL)%>"
            name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_COMPL)%>"
            type="text" size="22"
            configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_COMPL)%>"
            mask="#*40" classe="form-control"
            placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.mensagem.endereco.complemento", responsavel)%>" />
        </div>
       </show:showfield>
      
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_BAIRRO)%>">
         <div class="form-group">
           <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_BAIRRO)%>"><hl:message key="rotulo.endereco.bairro" /></label>
           <hl:htmlinput
             di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_BAIRRO)%>"
             name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_BAIRRO)%>"
             type="text" size="32"
             configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_BAIRRO)%>"
             mask="#*40" classe="form-control"
             placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.mensagem.endereco.bairro", responsavel)%>" />
         </div>
       </show:showfield>
        
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CIDADE)%>">
         <div class="form-group">
           <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CIDADE)%>"><hl:message key="rotulo.endereco.cidade" /></label>
           <hl:htmlinput
             di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CIDADE)%>"
             name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CIDADE)%>"
             type="text"
             configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CIDADE)%>"
             mask="#*40"
             placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.mensagem.endereco.cidade", responsavel)%>"
             classe="form-control" />
         </div>
       </show:showfield>
        
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_UF)%>">
         <div class="form-group">
           <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_UF)%>"><hl:message key="rotulo.endereco.estado" /></label>
           <hl:campoUFv4
             name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_UF)%>"
             classe="form-control"
             di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_UF)%>"
             rotuloUf="rotulo.estados"
             placeHolder="<%=(String) ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.uf", responsavel)%>"
             valorCampo="<%=JspHelper.geraComboUF(FieldKeysConstants.CAD_SERVIDOR_EDITAR_UF, null, ShowFieldHelper.isDisabled(FieldKeysConstants.CAD_SERVIDOR_EDITAR_UF, responsavel), responsavel)%>" />
         </div>
       </show:showfield>
        
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CEP)%>">
           <div class="form-group">
             <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CEP)%>"><hl:message key="rotulo.endereco.cep" /></label>
             <hl:htmlinput
               di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CEP)%>"
               name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CEP)%>"
               type="text"
               size="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCepSize())%>"
               configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CEP)%>"
               mask="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCepMask())%>"
               classe="form-control"
               placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.mensagem.endereco.cep", responsavel)%>" />
           </div>
       </show:showfield> 

  <%
      }
  %>
  
  <%-- TPC 391 DESABILITADO --%>
  <%
      if (!cadastroAvancadoUsuSer) {
  %>   
  <%-- NOME DA MAE --%>
  <show:showfield key="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_PRIMEIRO_NOME_MAE)%>" >
    <div class="form-group">
      <label for="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_PRIMEIRO_NOME_MAE)%>"><hl:message key="rotulo.servidor.primeiro.nome.mae"/></label>
      <hl:htmlinput name="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_PRIMEIRO_NOME_MAE)%>"
                    di="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_PRIMEIRO_NOME_MAE)%>"
                    type="text"
                    classe="form-control"
                    size="32"
                    mask="#*100"
                    placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.nome.mae", responsavel)%>"
                    configKey="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_PRIMEIRO_NOME_MAE)%>"
       />
    </div>
  </show:showfield>
  <%-- DATA DE ADMISSAO --%>
  <show:showfield key="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_DATA_ADMISSAO)%>" >
    <div class="form-group">
      <label for="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_DATA_ADMISSAO)%>"><hl:message key="rotulo.servidor.dataAdmissao"/></label>
      <hl:htmlinput name="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_DATA_ADMISSAO)%>"
                    di="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_DATA_ADMISSAO)%>"
                    type="text"
                    classe="form-control"
                    size="10"
                    placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.data.emissao", responsavel)%>"
                    mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                    configKey="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_DATA_ADMISSAO)%>" 
      />
    </div> 
  </show:showfield>
  
  <show:showfield key="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_IDENTIDADE)%>" >
    <div class="form-group">
      <label for="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_IDENTIDADE)%>"><hl:message key="rotulo.servidor.cartIdentidade"/></label>
      <hl:htmlinput name="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_IDENTIDADE)%>"
                    di="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_IDENTIDADE)%>"
                    type="text"
                    classe="form-control"
                    onFocus="SetarEventoMascaraV4(this,'#*30',true);"
                    onBlur="fout(this);ValidaMascaraV4(this);"
                    size="20"
                    placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.numero.identidade", responsavel)%>"
                    configKey="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_IDENTIDADE)%>"
      />
    </div>  
  </show:showfield>
  
  
  <show:showfield key="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_NO_CARTEIRA)%>" >
    <div class="form-group">
      <label for="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_NO_CARTEIRA)%>"><hl:message key="rotulo.servidor.cartTrabalho"/></label>
      <hl:htmlinput name="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_NO_CARTEIRA)%>"
                    di="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_NO_CARTEIRA)%>"
                    type="text"
                    classe="form-control"
                    onFocus="SetarEventoMascaraV4(this,'#*30',true);"
                    onBlur="fout(this);ValidaMascaraV4(this);"
                    size="20"
                    placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.cartTrabalho", responsavel)%>"
                    configKey="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_NO_CARTEIRA)%>"
      />
    </div>   
    <hl:message key="mensagem.auto.cadastro.senha.ser.ajuda.nocarteira"/>
  </show:showfield>
  <show:showfield key="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_PIS)%>" >
    <div class="form-group">
      <label for="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_PIS)%>"><hl:message key="rotulo.servidor.pis"/></label>
      <hl:htmlinput name="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_PIS)%>"
                    di="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_PIS)%>"
                    type="text"
                    classe="form-control"
                    onFocus="SetarEventoMascaraV4(this,'#*30',true);"
                    onBlur="fout(this);ValidaMascaraV4(this);"
                    size="20"
                    placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.pis", responsavel)%>"
                    configKey="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_PIS)%>"
      />
    </div>   
    <hl:message key="mensagem.auto.cadastro.senha.ser.ajuda.pis"/>
  </show:showfield>
  <show:showfield key="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_DATA_NASC)%>" >          
    <div class="form-group">
      <label for="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_DATA_NASC)%>"><hl:message key="rotulo.servidor.dataNasc"/></label>
      <hl:htmlinput name="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_DATA_NASC)%>"
                    type="text"
                    classe="form-control"
                    di="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_DATA_NASC)%>"
                    mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                    size="10"
                    placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.data.emissao", responsavel)%>"
                    configKey="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_DATA_NASC)%>"
       />
    </div>  
    <hl:message key="mensagem.auto.cadastro.senha.ser.ajuda.datanasc"/>
  </show:showfield>
  
  <% } else if (camposQuestionarioDadosServidor != null) { %>
  <%-- QUESTIONÁRIO --%>
  <%-- Início Dados para confirmação do servidor --%>
  <%
    for (CampoQuestionarioDadosServidor campo : camposQuestionarioDadosServidor) {
      if (campo.getType().equals("INPUT")) { %>
        <div class="form-group">
          <label for="<%=campo.getName()%>"><%=campo.getLabel()%></label>
          <hl:htmlinput name="<%=campo.getName()%>"
                        di="<%=campo.getName()%>"
                        type="<%=campo.getType()%>"
                        classe="form-control"
                        size="10"
                        mask="<%=campo.getMask()%>"
          />
        </div>
    <%  } else if (campo.getType().equals("RADIO")) { %>
        <div class="form-group">
          <div class="form-group my-0">
            <span id="clt"><%=campo.getLabel()%></span>
          </div>
          <div class="form-check mt-2">
            <hl:htmlinput di="<%=campo.getName() + "_S"%>"
                          name="<%=campo.getName()%>"
                          type="radio" 
                          value="S" 
                          mask="#*10"
                          configKey="<%=campo.getName()%>"
                          classe="form-check-input ml-1" 
            />
            <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.sim"/>' for="<%=campo.getName() + "_S"%>"><hl:message key="rotulo.sim" /></label>
            <hl:htmlinput di="<%=campo.getName() + "_N"%>"
                          name="<%=campo.getName()%>"
                          type="radio" 
                          value="N" 
                          mask="#*10"
                          configKey="<%=campo.getName()%>"
                          classe="form-check-input ml-1" 
            />
            <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.nao"/>' for="<%=campo.getName() + "_N"%>"><hl:message key="rotulo.nao" /></label>
          </div>
        </div>
        
        
  <%  } else if (campo.getType().equals("SELECT")) {
        if(campo.getName().equalsIgnoreCase("RSE_BANCO_SAL")) {%>
          <div class="form-group">
            <label for="<%=campo.getName()%>"> <hl:message key="rotulo.servidor.codigo.banco" /> </label>
            <SELECT
              id="<%=campo.getName()%>"
              NAME="<%=campo.getName()%>"
              CLASS="form-control"
              <OPTION VALUE="" SELECTED><hl:message key="rotulo.campo.selecione" /></OPTION>
            </SELECT>
          </div>
      <%} else if(campo.getName().equalsIgnoreCase("RSE_BANCO_SAL_2")) { %>
          <div class="form-group">
            <label for="<%=campo.getName()%>"> <hl:message key="rotulo.servidor.codigo.banco.alternativo" /> </label>
            <SELECT
              id="<%=campo.getName()%>"
              NAME="<%=campo.getName()%>"
              CLASS="form-control"
              <OPTION VALUE="" SELECTED><hl:message key="rotulo.campo.selecione" /></OPTION>
            </SELECT>
          </div>
      
      <%} else { %>
          <div class="form-group ">
            <label for="<%=campo.getName()%>"><%=campo.getLabel()%></label>
            <%=JspHelper.geraCombo(campo.getContent(), campo.getName(), campo.getOptionValue(), campo.getOptionLabel(), campo.getNotSelectedLabel(), null, false, 0, null, null, false, "form-control")%>
          </div>   
      <%}           
      }
    } 
  } %>
  <%-- Fim Dados para confirmação do servidor --%>
  <%-- SENHA --%>
  <% if (!usaRecuperacaoSenhaNoAutoCadastro) { %>
  <div class="form-group mb-0">
    <div class="row">
      <div class="form-group col-sm-12">
        <label for="senha"><hl:message key="rotulo.servidor.senha"/></label>
        <hl:htmlpassword name="senha"
                         di="senha"
                         cryptedfield="senhaRSA"
                         onFocus="<%="SetarEventoMascaraV4(this,'#" + TextHelper.forJavaScript((senhaServidorNumerica) ? "D" : "*") + "30',true);setanewOnKeyUp(this);"%>"
                         onBlur="fout(this);ValidaMascaraV4(this);newOnKeyUp(this);"
                         classe="form-control"
                         isSenhaServidor="true"
                         size="32"
                         placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.login.digite.senha.acesso", responsavel) %>"
                         maxlength="<%=TextHelper.forHtmlAttribute(String.valueOf(tamMaxSenhaServidor))%>"/>
      </div>
    </div>
    <div class="row">
      <div class="form-group col-sm-12">
        <div id="divSeveridade" class="alert alert-danger divSeveridade" role="alert">
          <p class="mb-0"><hl:message key="rotulo.usuario.nivel.seguranca"/>: <span id="verdict"><hl:message key="rotulo.nivel.senha.muito.baixo"/></span></p>
        </div>
      </div>
    </div>
    <div class="row">
      <div class="form-group col-sm-12">
        <label for="senhaConfirmacao"><hl:message key="rotulo.servidor.confirma.senha"/></label>
        <input type="password" 
               onFocus="<%="SetarEventoMascaraV4(this,'#" + TextHelper.forJavaScript((senhaServidorNumerica) ? "D" : "*") + "30',true);"%>" 
               onBlur="fout(this);ValidaMascaraV4(this);" 
               name="senhaConfirmacao"
               id="senhaConfirmacao"
               class="form-control" 
               size="32" 
               placeholder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.alterar.senha.digite.confirmacao.senha", responsavel) %>"
               maxlength="<%=(int)tamMaxSenhaServidor%>">
      </div>
    </div>
    <div class="row">
      <div class="form-group col-sm-12">
        <label for="dica"><hl:message key="rotulo.servidor.dica.senha"/></label>
        <input name="dica" 
               value="<%=TextHelper.forHtmlAttribute((request.getParameter("dica") != null) ? request.getParameter("dica") : "")%>" 
               type="text" 
               class="form-control" 
               id="dica" 
               onFocus="SetarEventoMascaraV4(this,'#*120',true);" 
               onBlur="fout(this);ValidaMascaraV4(this);"
          	   placeholder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.dica.senha", responsavel) %>" 
               size="32">
      </div>
    </div>   
  </div>
  <% } %>
  <%-- EMAIL --%>
  <% if ((cadastroAvancadoUsuSer || usaRecuperacaoSenhaNoAutoCadastro || emailSerEditavel) && (usuarioSer == null || TextHelper.isNull(usuarioSer.getUsuEmail()))) { %>
    <%if(allowedDomains == null) {%>
      <div class="form-group mb-0">
        <div class="row">
          <div class="form-group col-sm-12">
            <label for="email"><hl:message key="rotulo.servidor.email"/></label>          
            <hl:htmlinput name="email"
                          di="email"
                          type="text"
                          classe="form-control"
                          onFocus="SetarEventoMascaraV4(this,'#*100',true);"
                          onBlur="fout(this);ValidaMascaraV4(this);"
                          placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.digite.email", responsavel) %>"
                          size="20"
             />
          </div>
        </div>  
        <div class="row">
          <div class="form-group col-sm-12">
            <label for="email_confirmacao"><hl:message key="rotulo.servidor.confirma.email"/></label>  
            <hl:htmlinput name="email_confirmacao"
                          di="email_confirmacao"
                          type="text"
                          classe="form-control"
                          onFocus="SetarEventoMascaraV4(this,'#*100',true);"
                          onBlur="fout(this);ValidaMascaraV4(this);"
                          placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.alterar.senha.digite.confirmacao.email", responsavel) %>"
                          size="20"
            />
          </div>
        </div>
      </div>
    <% } else { %>
      <div class="form-group mb-0">
        <div class="row">
          <div class="form-group col-sm-5" style="padding-right: 0px">
              <label for="email"><hl:message key="rotulo.servidor.email"/></label>          
              <hl:htmlinput name="email"
                            di="email"
                            type="text"
                            classe="form-control"
                            onFocus="SetarEventoMascaraV4(this,'#*100',true);"
                            onBlur="fout(this);ValidaMascaraV4(this);"
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.digite.email", responsavel) %>"
                            size="20"
               />
            </div>
            <div class="form-group col-sm-2" style="padding-left: 0px" >
            <label for="x">&nbsp;</label>
            <hl:htmlinput name="y"
                            di="y"
                            value="@"
                            type="text"
                            classe="form-control"
                            size="1"
                            others="disabled" />
            </div>
            <div class="form-group col-sm-5">
            
            <label for="domain"><hl:message key="rotulo.servidor.email.dominio"/></label>
            <hl:htmlcombo
                 listName="allowedDomains" 
                 name="domain_email"
                 di="domain_email" 
                 fieldValue="domain" 
                 fieldLabel="domain" 
                 configKey="domain_email"
                 classe="form-control"
               />
          
          </div>  
        </div> 
        
        <div class="row">
          <div class="form-group col-sm-5" style="padding-right: 0px">
              <label for="email"><hl:message key="rotulo.servidor.confirma.email"/></label>          
              <hl:htmlinput name="email_confirmacao"
                            di="email_confirmacao"
                            type="text"
                            classe="form-control"
                            onFocus="SetarEventoMascaraV4(this,'#*100',true);"
                            onBlur="fout(this);ValidaMascaraV4(this);"
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.alterar.senha.digite.confirmacao.email", responsavel) %>"
                            size="20"
               />
            </div>
            <div class="form-group col-sm-2" style="padding-left: 0px" >
            <label for="x">&nbsp;</label>
            <hl:htmlinput name="x"
                            di="x"
                            value="@"
                            type="text"
                            classe="form-control"
                            size="1"
                            others="disabled" />
            </div>
            <div class="form-group col-sm-5">
            
            <label for="domain"><hl:message key="rotulo.servidor.email.dominio"/></label>
            <hl:htmlcombo
                 listName="allowedDomains" 
                 name="domain_confirmacao"
                 di="domain_confirmacao" 
                 fieldValue="domain" 
                 fieldLabel="domain" 
                 configKey="domain_confirmacao"
                 classe="form-control"
               />
          
          </div>  
        </div>
      </div> 
   <% }
  } %>   
  <%-- TELEFONE --%>
  <% if (telefoneSerEditavel) {   %> 
 <div class="row">
  <div class="form-group col-sm-4">
    <label for="ddd"><hl:message key="rotulo.servidor.codigo.localidade"/></label>
    <hl:htmlinput name="ddd"
                  di="ddd"
                  type="text"
                  classe="form-control"
                  mask="<%=LocaleHelper.getDDDMask()%>"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.ddd", responsavel) %>"  />
  </div>
  <div class="form-group col-sm-8 col-md-8">
  	<label for="telefone"><hl:message key="rotulo.servidor.telefone"/></label>
    <hl:htmlinput name="telefone"
                 di="telefone"
                 type="text"
                 classe="form-control"
                 mask="<%=LocaleHelper.getTelefoneMask()%>"
                 placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.telefone", responsavel) %>" />
  </div>
 </div> 
   <% } %>
  <%-- CELULAR --%>
<% if (celularSerEditavel) { %>
  <div class="row">
    <div class="form-group col-sm-4">
  	<label for="dddcel"><hl:message key="rotulo.servidor.codigo.localidade"/></label>
  	<hl:htmlinput name="dddcel"
                    di="dddcel"
                    type="text"
                    classe="form-control"
                    mask="<%=LocaleHelper.getDDDCelularMask()%>"
                    placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.ddd", responsavel) %>"  />
  	
    </div>
    <div class="form-group col-sm-8 col-md-8">
    	<label for="celular"><hl:message key="rotulo.servidor.celular"/></label>
    	<hl:htmlinput name="celular"
                     di="celular"
                     type="text"
                     classe="form-control"
                     mask="<%=LocaleHelper.getCelularMask()%>"
                     placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.celular", responsavel) %>" />
    	
    </div>
  </div> 
<% } %>

<% if(listaAnexos != null && atualizaDadosServidor)  { %>
<%   for(TransferObject anexo : listaAnexos) { %>
     <div class="row">
        <div class="form-group col-sm-12">
          <label for="btn-arquivo-<%=anexo.getAttribute(Columns.TAR_CODIGO).toString()%>"><%=anexo.getAttribute(Columns.TAR_DESCRICAO).toString()%></label>
          <input class="form-control file" type="FILE" name="btn-arquivo-<%=anexo.getAttribute(Columns.TAR_CODIGO).toString()%>" id="btn-arquivo-<%=anexo.getAttribute(Columns.TAR_CODIGO).toString()%>">
          </div>
    </div>
  <% }
  } %>


  <%-- CAPTCHA --%>
  <div class="row">
    <div class="form-group col-sm-5">
        <label for="loginCodigo"><hl:message key="rotulo.captcha.codigo"/></label>
        <input type="text" class="form-control" id="captcha" name="captcha" placeholder='<hl:message key="mensagem.informacao.login.digite.codigo.acesso"/>'>
    </div>
    <div class="form-group col-sm-6">
      <div class="captcha">    
        <img name="captcha_img" src="../captcha.jpg?t=<%=DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss")%>" alt='<hl:message key="rotulo.captcha.codigo"/>' title='<hl:message key="rotulo.captcha.codigo"/>'/>
        <a href="#no-back" onclick="reloadCaptcha()"><img src="../img/icones/refresh.png" alt='<hl:message key="rotulo.captcha.novo.codigo"/>' title='<hl:message key="rotulo.captcha.novo.codigo"/>' border="0"/></a>
        <a href="#no-back" class="btn-i-right pr-1" data-bs-toggle="popover" title="<hl:message key="rotulo.ajuda" />"
          data-bs-content='<hl:message key="mensagem.ajuda.captcha.usuario.v3"/>'
          data-original-title=<hl:message key="rotulo.ajuda" />>
          <img src="../img/icones/help.png" alt='Ajuda' title='<hl:message key="rotulo.ajuda" />' border="0">
        </a>
      </div>
    </div>
  </div>
  <input name="termoDeUso" type="hidden" id="termoDeUso" value="false">
  <input name="politicaPrivacidade" type="hidden" id="politicaPrivacidade" value="false">
</form>
<div class="btn-action mt-2 mb-0">
  <button class="btn btn-outline-danger mr-2" aria-label="Voltar" onClick="postData('<%=TextHelper.forJavaScriptAttribute(LoginHelper.getPaginaLoginServidor())%>')">
    <hl:message key="rotulo.botao.voltar"/>
  </button>
  <button id="btnOK" name="btnOK" class="btn btn-primary" type="submit" onClick="if(verificaForm()){f0.submit();} return false;">         
          <svg width="17"><use xlink:href="#i-avancar"></use></svg>
          <hl:message key="rotulo.botao.confirmar"/>                
  </button>
</div>
<% } else { %>
  <div class="alert alert-success" role="alert">
    <%=TextHelper.forHtmlContent(msgSucess)%>
  </div>
  <div class="row mr-1 justify-content-end"> 
    <a class="btn btn-outline-danger" aria-label="Voltar" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(LoginHelper.getPaginaLoginServidor())%>')">
      <hl:message key="rotulo.botao.voltar"/>
    </a>
  </div>
<% } %>

 <div class="modal fade" id="dialogTermoUso" tabindex="-1" role="dialog" aria-labelledby="modalTitulo" data-backdrop="static" data-keyboard="false" aria-hidden="true" style="display: none;">
   <div class="modal-dialog modalTermoUso" role="document">
     <div class="modal-content">
       <div class="modal-header pb-0">
         <h5 class="modal-title about-title mb-0" id="modalTitulo"><hl:message key="mensagem.termo.de.consentimento.cadastro.usuario.servidor.titulo"/></h5>
       </div>
       <div class="modal-body">
         <span id="textoTermoUso">
           <%=termoUsoCadSenha%>
         </span>
       </div>
       <div class="modal-footer pt-0">
         <div class="btn-action mt-2 mb-0">
           <a class="btn btn-outline-danger" onclick="postData('<%=TextHelper.forJavaScriptAttribute(LoginHelper.getPaginaLoginServidor())%>')" href="#no-back" aria-label='<hl:message key="rotulo.botao.voltar"/>' alt="<hl:message key="rotulo.botao.voltar"/>" title="<hl:message key="rotulo.botao.voltar"/>">
             <hl:message key="rotulo.botao.voltar" />
           </a>
           <a class="btn btn-primary" data-bs-dismiss="modal" href="#no-back" onclick="aceiteTermoDeUso()" aria-label='<hl:message key="rotulo.botao.continuar"/>' alt="<hl:message key="rotulo.botao.continuar"/>" title="<hl:message key="rotulo.botao.continuar"/>">
             <hl:message key="rotulo.botao.continuar" />
           </a>
         </div>
       </div>
     </div>
   </div>
 </div>
 <div class="modal fade" id="dialogPoliticaPrivacidade" tabindex="-1" role="dialog" aria-labelledby="modalTitulo2" data-backdrop="static" data-keyboard="false" aria-hidden="true" style="display: none;">
   <div class="modal-dialog modalTermoUso" role="document">
     <div class="modal-content">
       <div class="modal-header pb-0">
         <h5 class="modal-title about-title mb-0" id="modalTitulo2"><hl:message key="mensagem.politica.privacidade.cadastro.usuario.servidor.titulo"/></h5>
       </div>
       <div class="modal-body">
         <span id="politicaPrivacidade"><%=politicaPrivacidadeCadSenha%></span>
         <span><a download="politica_privacidade.pdf" href="data:application/pdf;base64,<%=politicaPrivacidadeFile%>"><hl:message key="rotulo.acoes.download"/></a></span>
         
       </div>
       <div class="modal-footer pt-0">
         <div class="btn-action mt-2 mb-0">
           <a class="btn btn-outline-danger" onclick="postData('<%=TextHelper.forJavaScriptAttribute(LoginHelper.getPaginaLoginServidor())%>')" href="#no-back" aria-label='<hl:message key="rotulo.botao.voltar"/>' alt="<hl:message key="rotulo.botao.voltar"/>" title="<hl:message key="rotulo.botao.voltar"/>">
             <hl:message key="rotulo.botao.voltar" />
           </a>
           <a class="btn btn-primary" data-bs-dismiss="modal" href="#no-back" onclick="aceitePoliticaPrivacidade()" aria-label='<hl:message key="rotulo.botao.continuar"/>' alt="<hl:message key="rotulo.botao.continuar"/>" title="<hl:message key="rotulo.botao.continuar"/>">
             <hl:message key="rotulo.botao.continuar" />
           </a>
         </div>
       </div>
     </div>
   </div>
 </div>
 <div class="modal fade" id="modalAguarde" data-focus="false" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
   <div class="modal-dialog-upload modal-dialog" role="document">
     <div class="modal-content">
       <div class="modal-body">
         <div class="row">
           <div class="col-md-12 d-flex justify-content-center">
             <img src="../img/loading.gif" class="loading">
           </div>
           <div class="col-md-12">
             <div class="modal-body"><span><hl:message key="mensagem.cadastro.usuario.servidor.aguarde"/></span></div>            
           </div>
         </div>
       </div>
     </div>
   </div>
  </div>

</c:set>

<c:set var="javascript">
<script type="text/JavaScript">
f0 = document.forms[0];
</script>
<script type="text/JavaScript">
var primOnFocus = true;

var arrayBancos = <%=(String) JspHelper.geraArrayBancos(responsavel)%>;
var arrayBancosPergunta;
var arrayBancosAlternativoPergunta;

<%if (!ok && ShowFieldHelper.canEdit(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_BANCO_SAL, responsavel)) { %>
	arrayBancosPergunta = <%=(String) JspHelper.geraArrayBancosParaValidacaoPerguntas(servidor.getAttribute(Columns.RSE_BANCO_SAL).toString(), null, responsavel)%>;
<%} %>
<%if (!ok && ShowFieldHelper.canEdit(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_BANCO_SAL_2, responsavel)) {%>
	arrayBancosAlternativoPergunta = <%=(String) JspHelper.geraArrayBancosParaValidacaoPerguntas(servidor.getAttribute(Columns.RSE_BANCO_SAL_2).toString(), null, responsavel)%>;
<%} %>

function formLoad() {
   if (document.forms[0].RSE_BANCOS != null) {
     AtualizaFiltraComboExt(document.forms[0].RSE_BANCOS, arrayBancos, '', '', '', false, false, '', '');
   }
   
   if (document.forms[0].RSE_BANCOS_2 != null) {
     AtualizaFiltraComboExt(document.forms[0].RSE_BANCOS_2, arrayBancos, '', '', '', false, false, '', '');
   }
   
  focusFirstField();
  
  if(<%=!ok%>) {
  	$("#dialogTermoUso").modal("show");
  }
  
  if (document.forms[0].RSE_BANCO_SAL != null) {
	AtualizaFiltraComboExt(document.forms[0].RSE_BANCO_SAL, arrayBancosPergunta, '', '', '', false, false, '', '');
  }
  
  if (document.forms[0].RSE_BANCO_SAL_2 != null) {
	AtualizaFiltraComboExt(document.forms[0].RSE_BANCO_SAL_2, arrayBancosAlternativoPergunta, '', '', '', false, false, '', '');
  }
}

function newOnKeyUp(Controle) {
  testPassword(Controle.value, 'divSeveridade', <%=intpwdStrength%>);
}

function aceiteTermoDeUso() {
	$("#termoDeUso").val("true");
	
	if(<%=!TextHelper.isNull(politicaPrivacidadeCadSenha)%>) {
		focusFirstField();
		 $("#dialogPoliticaPrivacidade").modal("show");
	}
}

function aceitePoliticaPrivacidade() {
	$("#politicaPrivacidade").val("true");
	$("#dialogTermoUso").modal("hide");
	$("#dialogPoliticaPrivacidade").modal("hide");
}
	
function setanewOnKeyUp(Controle) {
  if (!primOnFocus) {
    return false;
  }
  primOnFocus = false;
  var oldonkeyup = Controle.onkeyup;
  if (typeof Controle.onkeyup != 'function') {
    Controle.onkeyup = newOnKeyUp(Controle);
  } else {
    Controle.onkeyup = function() {
      if (oldonkeyup) {
        oldonkeyup(Controle);
      }
      testPassword(Controle.value, 'divSeveridade', <%=intpwdStrength%>);
    }
  } 
}

function limpaSenhas() {
  f0.senha.value = '';
  f0.senhaConfirmacao.value = '';
  setanewOnKeyUp(f0.senha);
  f0.senha.focus();
}

<% if (cadastroAvancadoUsuSer && camposQuestionarioDadosServidor != null) { %>
  function confirmaDadosServidor() {
      var controles = [];
        var msgs  = [];
        <% for (CampoQuestionarioDadosServidor campo : camposQuestionarioDadosServidor) { %>
      controles.push("<%=campo.getName()%>");
      msgs.push("<%=campo.getMsgControle()%>");
        <% } %>
        if (!ValidaCampos(controles, msgs)) {
            return false;
        }
        return true;
    }
<% } %>

function verificaForm() {
  var Controles = new Array("<%=(String)( FieldKeysConstants.CAD_SERVIDOR_CPF )%>",
                            "<%=(String)( FieldKeysConstants.CAD_SERVIDOR_MATRICULA )%>",
                            "<%=(String)( FieldKeysConstants.CAD_SERVIDOR_IDENTIDADE )%>",
                            "<%=(String)( FieldKeysConstants.CAD_SERVIDOR_DATA_NASC )%>",
                            "<%=(String)( FieldKeysConstants.CAD_SERVIDOR_ORG_CODIGO )%>",
                            "senha",
                            "senhaConfirmacao",
                            "<%=(String)( FieldKeysConstants.CAD_SERVIDOR_DATA_ADMISSAO )%>",
                            "<%=(String)( FieldKeysConstants.CAD_SERVIDOR_PRIMEIRO_NOME_MAE )%>");
  var Msgs = new Array('<hl:message key="mensagem.informe.servidor.cpf"/>',
               '<hl:message key="mensagem.informe.servidor.matricula"/>',
               '<hl:message key="mensagem.informe.servidor.identidade"/>',
               '<hl:message key="mensagem.informe.servidor.data.nascimento"/>',
               '<hl:message key="mensagem.informe.servidor.orgao"/>',
               '<hl:message key="mensagem.informe.servidor.usuario.senha"/>',
               '<hl:message key="mensagem.informe.servidor.usuario.senha.confirmacao"/>',
               '<hl:message key="mensagem.informe.servidor.data.admissao"/>',
               '<hl:message key="mensagem.informe.servidor.primeiro.nome.mae"/>'
  );
  
  <% if(listaAnexos != null && atualizaDadosServidor)  { %>
  <%    for(TransferObject anexo : listaAnexos) { %>
  			Controles.push("btn-arquivo-"+<%=anexo.getAttribute(Columns.TAR_CODIGO).toString()%>);
  			Msgs.push("<hl:message key='mensagem.informe.anexo.cad.senha.servidor'/>");
  <%    }
    }%>
  
  
  
  if (!ValidaCampos(Controles, Msgs)) {
     return false;
  }
  
 
  //validar o tamanho dos arquivos antes de fazer upload
  $('input[type=file]').each(function(index, field){
     const file = field.files[0];
     if (file.size/1024 > <%=tamMax%>) {
     	alert('<hl:message key="mensagem.erro.arquivo.tamanho.maximo" arg0="<%=tamMaxText%>"/>');
     	return false;
     } 
	});
  
  
  
  if (f0.<%=(String)( FieldKeysConstants.CAD_SERVIDOR_CPF )%> != null && !CPF_OK(extraiNumCNPJCPF(f0.<%=(String)( FieldKeysConstants.CAD_SERVIDOR_CPF )%>.value))) {
     return false;
  }

  <% if (usaRecuperacaoSenhaNoAutoCadastro || ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_USU_SERVIDOR_EMAIL, responsavel)) { %>
      if (f0.email != null && (f0.email.value == null || f0.email.value.trim() == '')) {
        alert('<hl:message key="mensagem.informe.ser.email"/>');
        f0.email.focus();
        return false;
      }
    
      if (f0.email_confirmacao != null && (f0.email_confirmacao.value == null || f0.email_confirmacao.value.trim() == '')) {
        alert('<hl:message key="mensagem.informe.ser.email.confirmacao"/>');
        f0.email_confirmacao.focus();
        return false;
      }
    
      if (f0.email != null && f0.email_confirmacao != null && f0.email.value != f0.email_confirmacao.value) {
        alert('<hl:message key="mensagem.erro.email.diverge.email.confirmacao"/>');
        f0.email.focus();
        return false;
      }
      
      <% if(allowedDomains == null) {%>
	      if (f0.email != null && f0.email.value != null && f0.email.value != '' && !isEmailValid(f0.email.value)) {
		      alert('<hl:message key="mensagem.erro.email.invalido"/>');
		      f0.email.focus();
		      return false;
	      }
      <%} else {%>
	      if (f0.email != null && f0.email.value != null && f0.email.value != '' && !isEmailValid(f0.email.value+'@'+f0.domain_email.value)) {
		      alert('<hl:message key="mensagem.erro.email.invalido"/>');
		      f0.email.focus();
		      return false;
	      }
      <% } %>
  <% } else { %>
        if (f0.email != null && f0.email != undefined && (trim(f0.email.value) != '' || trim(f0.email_confirmacao.value) != '')) {
           if (f0.email.value != f0.email_confirmacao.value) {
              alert('<hl:message key="mensagem.erro.cadastrar.senha.servidor.email"/>');
              f0.email.focus();
              return false;
           }
        }
        
        <% if(allowedDomains == null) {%>
	      if (f0.email != null && f0.email.value != null && f0.email.value != '' && !isEmailValid(f0.email.value)) {
		      alert('<hl:message key="mensagem.erro.email.invalido"/>');
		      f0.email.focus();
		      return false;
	      }
    	<%} else {%>
	      if (f0.email != null && f0.email.value != null && f0.email.value != '' && !isEmailValid(f0.email.value+'@'+f0.domain_email.value)) {
		      alert('<hl:message key="mensagem.erro.email.invalido"/>');
		      f0.email.focus();
		      return false;
	      }
    	<% } %>
  <% } %>

  // Validação se os campos telefone e celular são obrigatórios no front
  <% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_USU_SERVIDOR_TELEFONE, responsavel)) { %>
  var serTelField = f0.telefone;
  var dddTelefoneField = f0.ddd;
    if (serTelField.value == null || serTelField.value == '') {
        alert('<hl:message key="mensagem.informe.servidor.telefone"/>');
        serTelField.focus();
        return false;
    } else if (dddTelefoneField.value == null || dddTelefoneField.value.trim() == '') {
        alert('<hl:message key="mensagem.informe.servidor.ddd.telefone"/>');
        dddTelefoneField.focus();
        return false;
        }
<% } %>
  
  <% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_USU_SERVIDOR_CELULAR, responsavel)) { %>
      var serCelField = f0.celular;
      var dddCelularField = f0.dddcel;
      if (serCelField.value == null || serCelField.value == '') {
          alert('<hl:message key="mensagem.informe.servidor.celular"/>');
          serCelField.focus();
          return false;
       } else if (dddCelularField.value == null || dddCelularField.value.trim() == '') {
             alert('<hl:message key="mensagem.informe.servidor.ddd.celular"/>');
             dddCelularField.focus();
             return false;
           }
  <% } %>
  
  // Validação se os campos podem ser editados e se estão válidos
  <% if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_USU_SERVIDOR_TELEFONE, responsavel)) { %>
      var telefoneField = f0.telefone;
      var dddTelefoneField = f0.ddd;
      if (telefoneField.value != null && telefoneField.value != '' && telefoneField.value.length < '<%=LocaleHelper.getTelefoneSize()%>') {
      	alert('<hl:message key="mensagem.erro.servidor.telefone.invalido"/>');
      	telefoneField.focus();
      	return false;
      } else if (dddTelefoneField.value != null && dddTelefoneField.value != '' && (telefoneField.value == null || telefoneField.value == '')){
        	alert('<hl:message key="mensagem.erro.servidor.telefone.invalido"/>');
          	telefoneField.focus();
          	return false;
          }
  
      if ((telefoneField.value != null && telefoneField.value != '') && (dddTelefoneField.value != null && dddTelefoneField.value.trim().length < <%=LocaleHelper.getDDDMask().length()%>)) {
      	alert('<hl:message key="mensagem.erro.servidor.ddd.telefone.invalido"/>');
      	dddTelefoneField.focus();
      	return false;
      }
  <% } %>
  
  <% if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_USU_SERVIDOR_CELULAR, responsavel)) { %>
      var celularField = f0.celular;
      var dddCelularField = f0.dddcel;
      if (celularField.value != null && celularField.value != '' && celularField.value.length < '<%=LocaleHelper.getCelularSize()%>') {
      	alert('<hl:message key="mensagem.erro.servidor.celular.invalido"/>');
      	celularField.focus();
      	return false;
      } else if (dddCelularField.value != null && dddCelularField.value != '' && (celularField.value == null || celularField.value == '')){
        alert('<hl:message key="mensagem.erro.servidor.celular.invalido"/>');
        dddCelularField.focus();
        return false;
      }
       if (celularField.value != null && celularField.value != '' && (dddCelularField.value != null && dddCelularField.value.trim().length < <%=LocaleHelper.getDDDCelularMask().length()%>)) {
      	alert('<hl:message key="mensagem.erro.servidor.ddd.celular.invalido"/>');
      	dddCelularField.focus();
      	return false;
        }
   <% } %>

  if ((f0.captcha != undefined) && (f0.captcha.value == "")) {
    alert('<hl:message key="mensagem.informe.captcha.codigo"/>');
    f0.captcha.focus();
    return false;
  }

  <% if (cadastroAvancadoUsuSer) { %>
  if (!confirmaDadosServidor()) {
   return false;
  }
  <% } %>

  <% if (!usaRecuperacaoSenhaNoAutoCadastro) { %>
  if ((f0.senha.value!=null) && (f0.senhaConfirmacao.value!=null) &&
      (f0.senha.value != "") && (f0.senhaConfirmacao.value != "")) {
    testPassword(f0.senha.value, 'divSeveridade', <%=intpwdStrength%>);
    if (f0.senha.value.length > <%=(int)(tamMaxSenhaServidor)%>) {
        alert('<hl:message key="mensagem.erro.cadastrar.senha.servidor.maximo"/>'.replace('{0}', <%=(int)(tamMaxSenhaServidor)%>));
        limpaSenhas();
        return false;
    } else if (f0.senha.value.length < <%=(int)(tamMinSenhaServidor)%>) {
        alert('<hl:message key="mensagem.erro.cadastrar.senha.servidor.minimo"/>'.replace('{0}', <%=(int)(tamMinSenhaServidor)%>));
        limpaSenhas();
        return false;
    } else if (f0.senha.value == f0.senhaConfirmacao.value) {
      if (f0.score.value < <%=(int)(pwdStrengthLevel)%>) {
        alert ('<hl:message key="mensagem.erro.cadastrar.senha.servidor.invalida"/>');
        limpaSenhas();
        return false;
      }

      CriptografaSenha(f0.senha, f0.senhaRSA, false);
      f0.senhaConfirmacao.value = ''; 

      <% if (cadastroAvancadoUsuSer) { %>
      if (f0.<%=(String)(FieldKeysConstants.CAD_SERVIDOR_ORG_CODIGO)%> != null) {
        f0.<%=(String)(FieldKeysConstants.CAD_SERVIDOR_ORG_CODIGO)%>.disabled = false;
      }
      <% } %>
      
      $('#modalAguarde').modal({
          backdrop: 'static',
          keyboard: false
      });

      return true;
    } else {
      limpaSenhas();
      alert('<hl:message key="mensagem.erro.cadastrar.senha.servidor.diferente"/>');
      return false;
    }
  } else {
    f0.senha.focus();
    return false;
  }
  <% } else { %>
    if (f0.<%=(String)(FieldKeysConstants.CAD_SERVIDOR_ORG_CODIGO)%> != null) {
        f0.<%=(String)(FieldKeysConstants.CAD_SERVIDOR_ORG_CODIGO)%>.disabled = false;
      }

    $('#modalAguarde').modal({
        backdrop: 'static',
        keyboard: false
    });

    return true;
  <% } %>
  
}

function setAcao(Acao) {
  f0.Acao.value = Acao;
}

window.onload = formLoad;

</script>
<script type="text/JavaScript" src="../js/passwordmeter_3010.js?<hl:message key="release.tag"/>"></script>
</c:set>

<t:empty_v4>    
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:empty_v4>
