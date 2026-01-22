<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.usuario.LoginHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/showfield-lib" prefix="show"%>
<jsp:useBean id="paramSession" scope="session"
  class="com.zetra.econsig.helper.web.ParamSession" />
<%
    AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

			String srsCodigoPadrao = (String) request.getAttribute("srsCodigoPadrao");
%>

<c:set var="title">
  <hl:message key="rotulo.cadastrar.servidor.titulo" />
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
  <div class="card">
    <div class="card-header hasIcon">
      <span class="card-header-icon"> <svg width="26">
            <use xlink:href="#i-servidor"></use>
          </svg>
      </span>
      <h2 class="card-header-title">
        <hl:message key="rotulo.servidor.dados" />
      </h2>
    </div>
    <div class="card-body">
      <form method="post" action="../v3/cadastrarServidor?acao=salvar"
        name="form1">
        <hl:htmlinput name="SER_CPF" type="hidden"
          value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "SER_CPF"))%>" />
        <hl:htmlinput name="RSE_MATRICULA" type="hidden"
          value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "RSE_MATRICULA"))%>" />
        <%=(String) (SynchronizerToken.generateHtmlToken(request))%>
        <fieldset>
          <h3 class="legend">
            <span><hl:message
                key="rotulo.servidor.subtitulo.informacoes.gerais" /></span>
          </h3>
          <div class="row">
            <show:showfield
              key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_TITULACAO)%>">
              <div class="form-group col-sm-4">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_TITULACAO)%>">
                  <%
                      String titulacao = ApplicationResourcesHelper.getMessage("rotulo.servidor.titulacao.valores",
                  							responsavel);
                  					List<String> titLst = new ArrayList<String>();
                  					if (!TextHelper.isNull(titulacao)) {
                  						titLst = Arrays.asList(titulacao.split(",|;"));
                  					}
                  %> <hl:message key="rotulo.servidor.titulacao" />
                </label> <select
                  id="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_TITULACAO)%>"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_TITULACAO)%>"
                  class="form-control"
                  onFocus="SetarEventoMascara(this,'#*5',true);">
                  <option value=" " SELECTED>--</option>
                  <%
                      Iterator titIt = titLst.iterator();
                  					while (titIt.hasNext()) {
                  						String tratamento = (String) titIt.next();
                  %>
                  <option value="<%=tratamento%>"><%=tratamento%></OPTION>
                  <%
                      }
                  %>
                </select>
              </div>
            </show:showfield>
          </div>
          <div class="row">
            <show:showfield
              key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_PRIMEIRO_NOME)%>">
              <div class="form-group col-sm-3">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_PRIMEIRO_NOME)%>">
                  <hl:message key="rotulo.servidor.nome" />
                </label>
                <hl:htmlinput classe="form-control"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.primeiro.nome", responsavel)%>"
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_PRIMEIRO_NOME)%>"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_PRIMEIRO_NOME)%>"
                  type="text"
                  size="<%=JspHelper.configTamCampoNome(FieldKeysConstants.CADASTRAR_SERVIDOR_TITULACAO,
							FieldKeysConstants.CADASTRAR_SERVIDOR_PRIMEIRO_NOME,
							FieldKeysConstants.CADASTRAR_SERVIDOR_NOME_MEIO,
							FieldKeysConstants.CADASTRAR_SERVIDOR_ULTIMO_NOME, responsavel)%>"
                  mask="#*100"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_PRIMEIRO_NOME)%>" />
              </div>
            </show:showfield>
            <show:showfield
              key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NOME_MEIO)%>">
              <div class="form-group col-sm-3">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NOME_MEIO)%>">
                  <hl:message key="rotulo.servidor.meio.nome" />
                </label>
                <hl:htmlinput
               	  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.nome.meio", responsavel)%>"
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NOME_MEIO)%>"
                  classe="form-control" type="text"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NOME_MEIO)%>"
                  size="<%=JspHelper.configTamCampoNome(FieldKeysConstants.CADASTRAR_SERVIDOR_TITULACAO,
							FieldKeysConstants.CADASTRAR_SERVIDOR_PRIMEIRO_NOME,
							FieldKeysConstants.CADASTRAR_SERVIDOR_NOME_MEIO,
							FieldKeysConstants.CADASTRAR_SERVIDOR_ULTIMO_NOME, responsavel)%>"
                  mask="#*100"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NOME_MEIO)%>" />
              </div>
            </show:showfield>
            <show:showfield
              key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_ULTIMO_NOME)%>">
              <div class="form-group col-sm-3">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_ULTIMO_NOME)%>">
                  <hl:message key="rotulo.servidor.ultimo.nome" />
                </label>
                <hl:htmlinput
                placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.ultimo.nome", responsavel)%>"
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_ULTIMO_NOME)%>"
                  classe="form-control" type="text"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_ULTIMO_NOME)%>"
                  size="<%=JspHelper.configTamCampoNome(FieldKeysConstants.CADASTRAR_SERVIDOR_TITULACAO,
							FieldKeysConstants.CADASTRAR_SERVIDOR_PRIMEIRO_NOME,
							FieldKeysConstants.CADASTRAR_SERVIDOR_NOME_MEIO,
							FieldKeysConstants.CADASTRAR_SERVIDOR_ULTIMO_NOME, responsavel)%>"
                  mask="#*100"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_ULTIMO_NOME)%>" />
              </div>
            </show:showfield>
            <div class="form-group col-sm-6">
              <show:showfield
                key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NOME)%>">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NOME)%>"><hl:message
                    key="rotulo.servidor.nome.completo" /></label>
                <hl:htmlinput
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NOME)%>"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NOME)%>"
                  type="text" size="32" mask="#*100"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NOME)%>"
                  classe="form-control"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.nome", responsavel)%>" />
              </show:showfield>
            </div>
          </div>

          <show:showfield
            key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NOME_PAI)%>">
            <div class="row">
              <div class="form-group col-sm-6">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NOME_PAI)%>"><hl:message
                    key="rotulo.servidor.nomePai" /></label>
                <hl:htmlinput
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NOME_PAI)%>"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NOME_PAI)%>"
                  type="text" size="32" mask="#*100"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.nome.pai",
							responsavel)%>"
                  classe="form-control"
                  value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request,
							TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NOME_PAI)))%>"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NOME_PAI)%>" />
              </div>
            </div>
          </show:showfield>
          <show:showfield
            key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NOME_MAE)%>">
            <div class="row">
              <div class="form-group col-sm-6">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NOME_MAE)%>"><hl:message
                    key="rotulo.servidor.nomeMae" /></label>
                <hl:htmlinput
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NOME_MAE)%>"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NOME_MAE)%>"
                  type="text" size="32" mask="#*100"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.nome.mae",
							responsavel)%>"
                  classe="form-control"
                  value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request,
							TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NOME_MAE)))%>"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NOME_MAE)%>" />
              </div>
            </div>
          </show:showfield>
          <show:showfield
            key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_DATA_NASCIMENTO)%>">
            <div class="row">
              <div class="form-group col-sm-6">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_DATA_NASCIMENTO)%>"><hl:message
                    key="rotulo.servidor.dataNasc" /></label>
                <hl:htmlinput
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_DATA_NASCIMENTO)%>"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_DATA_NASCIMENTO)%>"
                  type="text" size="10"
                  mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                  classe="form-control"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_DATA_NASCIMENTO)%>"
                  placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>" />
              </div>
            </div>
          </show:showfield>
          <show:showfield
            key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NACIONALIDADE)%>">
            <div class="row">
              <div class="form-group col-sm-6">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NACIONALIDADE)%>"><hl:message
                    key="rotulo.servidor.nacionalidade" /></label>
                <hl:htmlinput
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NACIONALIDADE)%>"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NACIONALIDADE)%>"
                  type="text" size="32"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NACIONALIDADE)%>"
                  mask="#*40"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.nacionalidade",
							responsavel)%>"
                  classe="form-control" />
              </div>
            </div>
          </show:showfield>
          <show:showfield
            key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_ESTADO_CIVIL)%>">
            <div class="row">
              <div class="form-group col-sm-6">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_ESTADO_CIVIL)%>"><hl:message
                    key="rotulo.servidor.estadoCivil" /></label>
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
            </div>
          </show:showfield>
          <show:showfield
            key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_SEXO)%>">
            <div class="row">
              <div class=" col-md-6 form-check mt-2" role="radiogroup"
                aria-labelledby="sexo">
                <div class="form-group my-0">
                  <span id="sexo"><hl:message
                      key="rotulo.servidor.sexo" /></span>
                </div>
                <div class="form-check mt-2">
                  <hl:htmlinput
                    di="sexoMaculino"
                    name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_SEXO)%>"
                    type="radio" value="M" mask="#*10"
                    nf="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_ESTADO_CIVIL)%>"
                    configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_SEXO)%>"
                    classe="form-check-input ml-1" />
                  <label
                    class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top"
                    aria-label='<hl:message key="rotulo.servidor.sexo.masculino"/>'
                    for="sexoMaculino"><hl:message
                      key="rotulo.servidor.sexo.masculino" /></label>
                  <hl:htmlinput
                    di="sexoFeminino"
                    name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_SEXO)%>"
                    type="radio" value="F" mask="#*10"
                    nf="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_ESTADO_CIVIL)%>"
                    configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_SEXO)%>"
                    classe="form-check-input ml-1" />
                  <label
                    class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top"
                    aria-label='<hl:message key="rotulo.servidor.sexo.feminino"/>'
                    for="sexoFeminino"><hl:message
                      key="rotulo.servidor.sexo.feminino" /></label>
                </div>
              </div>
            </div>
          </show:showfield>
        </fieldset>
        <fieldset>
          <h3 class="legend">
            <span><hl:message
                key="rotulo.servidor.subtitulo.documentos.pessoais" /></span>
          </h3>
          <div class="row">
            <div class="form-group col-sm-6">
              <hl:campoCPFv4
                placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.cpf", responsavel)%>"
                name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CPF)%>"
                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CPF)%>"
                value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "SER_CPF"))%>" />

            </div>
          </div>
          <show:showfield
            key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NO_IDENTIDADE)%>">
            <div class="row">
              <div class="form-group col-sm-3">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NO_IDENTIDADE)%>"><hl:message
                    key="rotulo.servidor.cartIdentidade" /></label>
                <hl:htmlinput
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NO_IDENTIDADE)%>"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NO_IDENTIDADE)%>"
                  type="text" size="10"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NO_IDENTIDADE)%>"
                  mask="#*40"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.numero.identidade",
							responsavel)%>"
                  classe="form-control" />
              </div>
              <div class="form-group col-sm-3">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_EMISSOR_IDENTIDADE)%>"><hl:message
                    key="rotulo.servidor.rg.orgao.emissor" /></label>
                <hl:htmlinput
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_EMISSOR_IDENTIDADE)%>"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_EMISSOR_IDENTIDADE)%>"
                  type="text" size="5"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_EMISSOR_IDENTIDADE)%>"
                  mask="#*40" classe="form-control"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.orgao.emissor",
							responsavel)%>" />
              </div>
              <div class="form-group col-sm-3">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_UF_IDENTIDADE)%>"><hl:message
                    key="rotulo.servidor.rg.uf" /></label>
                <hl:htmlinput
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_UF_IDENTIDADE)%>"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_UF_IDENTIDADE)%>"
                  type="text" size="3"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_UF_IDENTIDADE)%>"
                  mask="#*2"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.uf", responsavel)%>"
                  classe="form-control" />
              </div>
              <div class="form-group col-sm-3">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_DATA_IDENTIDADE)%>"><hl:message
                    key="rotulo.servidor.rg.data.emissao" /></label>
                <hl:htmlinput
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_DATA_IDENTIDADE)%>"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_DATA_IDENTIDADE)%>"
                  type="text" size="10"
                  mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_DATA_IDENTIDADE)%>"
                  classe="form-control"
                  placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>" />
              </div>
            </div>
          </show:showfield>
          <div class="row">
            <show:showfield
              key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NUM_CARTEIRA_TRABALHO)%>">
              <div class="form-group col-sm-6">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NUM_CARTEIRA_TRABALHO)%>"><hl:message
                    key="rotulo.servidor.cartTrabalho" /></label>
                <hl:htmlinput
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NUM_CARTEIRA_TRABALHO)%>"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NUM_CARTEIRA_TRABALHO)%>"
                  type="text" size="32"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NUM_CARTEIRA_TRABALHO)%>"
                  mask="#*40"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.cartTrabalho",
							responsavel)%>"
                  classe="form-control" />
              </div>
            </show:showfield>
            <show:showfield
              key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NUM_PIS)%>">
              <div class="form-group col-sm-6">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NUM_PIS)%>"><hl:message
                    key="rotulo.servidor.pis" /></label>
                <hl:htmlinput
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NUM_PIS)%>"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NUM_PIS)%>"
                  type="text" size="32"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NUM_PIS)%>"
                  mask="#*40" classe="form-control"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.pis", responsavel)%>" />
              </div>
            </show:showfield>
          </div>
        </fieldset>
        <fieldset>
          <show:showfield
            key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_LOGRADOURO)%>">
            <h3 class="legend">
              <span><hl:message
                  key="rotulo.servidor.subtitulo.endereco.residencial" /></span>
            </h3>
            <div class="row">
              <div class="form-group col-sm-4">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_LOGRADOURO)%>"><hl:message
                    key="rotulo.endereco.logradouro" /></label>
                <hl:htmlinput
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_LOGRADOURO)%>"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_LOGRADOURO)%>"
                  type="text" size="32"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_LOGRADOURO)%>"
                  mask="#*100" classe="form-control"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.mensagem.endereco.logradouro", responsavel)%>" />
              </div>
          </show:showfield>
          <show:showfield
            key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NRO)%>">
            <div class="form-group col-sm-2">
              <label
                for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NRO)%>"><hl:message
                  key="rotulo.endereco.numero" /></label>
              <hl:htmlinput
                di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NRO)%>"
                name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NRO)%>"
                type="text" size="5"
                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_NRO)%>"
                mask="#D11" classe="form-control"
                placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.mensagem.endereco.numero", responsavel)%>" />
            </div>
          </show:showfield>

          <show:showfield
            key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_COMPLEMENTO)%>">
            <div class="form-group col-sm-3">
              <label
                for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_COMPLEMENTO)%>"><hl:message
                  key="rotulo.endereco.complemento" /></label>
              <hl:htmlinput
                di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_COMPLEMENTO)%>"
                name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_COMPLEMENTO)%>"
                type="text" size="22"
                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_COMPLEMENTO)%>"
                mask="#*40" classe="form-control"
                placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.mensagem.endereco.complemento", responsavel)%>" />
            </div>
          </show:showfield>
          <show:showfield
            key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_BAIRRO)%>">
            <div class="form-group col-sm-3">
              <label
                for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_BAIRRO)%>"><hl:message
                  key="rotulo.endereco.bairro" /></label>
              <hl:htmlinput
                di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_BAIRRO)%>"
                name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_BAIRRO)%>"
                type="text" size="32"
                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_BAIRRO)%>"
                mask="#*40" classe="form-control"
                placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.mensagem.endereco.bairro", responsavel)%>" />
            </div>
          </show:showfield>
          <show:showfield
            key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CIDADE)%>">
            <div class="form-group col-sm-6">
              <label
                for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CIDADE)%>"><hl:message
                  key="rotulo.endereco.cidade" /></label>
              <hl:htmlinput
                di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CIDADE)%>"
                name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CIDADE)%>"
                type="text"
                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CIDADE)%>"
                mask="#*40"
                placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.mensagem.endereco.cidade", responsavel)%>"
                classe="form-control" />
            </div>
          </show:showfield>
          <show:showfield
            key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_UF)%>">
            <div class="form-group col-sm-6">
              <label
                for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_UF)%>"><hl:message
                  key="rotulo.endereco.estado" /></label>
              <hl:campoUFv4
                name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_UF)%>"
                classe="form-control"
                di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_UF)%>"
                rotuloUf="rotulo.estados"
                placeHolder="<%=(String) ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.uf",
							responsavel)%>"
                valorCampo="<%=JspHelper.geraComboUF(FieldKeysConstants.CADASTRAR_SERVIDOR_UF, null,
							ShowFieldHelper.isDisabled(FieldKeysConstants.CADASTRAR_SERVIDOR_UF, responsavel),
							responsavel)%>" />
            </div>
          </show:showfield>
          <show:showfield
            key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CEP)%>">
              <div class="form-group col-sm-6">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CEP)%>"><hl:message
                    key="rotulo.endereco.cep" /></label>
                <hl:htmlinput
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CEP)%>"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CEP)%>"
                  type="text"
                  size="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCepSize())%>"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CEP)%>"
                  mask="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCepMask())%>"
                  classe="form-control"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.mensagem.endereco.cep", responsavel)%>" />
              </div>
          </show:showfield>
        </fieldset>
        <fieldset>
          <show:showfield
            key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_TELEFONE)%>">
            <h3 class="legend">
              <span><hl:message
                  key="rotulo.servidor.subtitulo.dados.contato" /></span>
            </h3>
            <div class="row">
              <show:showfield
                key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_DDD_TELEFONE)%>">
                <div class="form-group col-sm-2">
                  <label
                    for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_DDD_TELEFONE)%>"><hl:message
                      key="rotulo.servidor.codigo.localidade" /></label>
                  <hl:htmlinput
                    di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_DDD_TELEFONE)%>"
                    name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_DDD_TELEFONE)%>"
                    type="text" size="2"
                    mask="<%=LocaleHelper.getDDDMask()%>"
                    configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_DDD_TELEFONE)%>"
                    classe="form-control"
                    placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.ddd", responsavel)%>" />
                </div>
              </show:showfield>
              <div class="form-group col-sm-6">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_TELEFONE)%>"><hl:message
                    key="rotulo.servidor.telefone" /></label>
                <hl:htmlinput
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_TELEFONE)%>"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_TELEFONE)%>"
                  type="text" size="9"
                  mask="<%=LocaleHelper.getTelefoneMask()%>"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_TELEFONE)%>"
                  classe="form-control"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.telefone",
							responsavel)%>" />
              </div>
            </div>
          </show:showfield>
          <show:showfield
            key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CELULAR)%>">
            <div class="row">
              <show:showfield
                key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_DDD_CELULAR)%>">
                <div class="form-group col-sm-2">
                  <label
                    for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_DDD_CELULAR)%>"><hl:message
                      key="rotulo.servidor.codigo.localidade" /></label>
                  <hl:htmlinput
                    di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_DDD_CELULAR)%>"
                    name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_DDD_CELULAR)%>"
                    type="text" size="2"
                    mask="<%=LocaleHelper.getDDDCelularMask()%>"
                    configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_DDD_CELULAR)%>"
                    classe="form-control"
                    placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.ddd", responsavel)%>" />
                </div>
              </show:showfield>
              <div class="form-group col-sm-6">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CELULAR)%>"><hl:message
                    key="rotulo.servidor.celular"
                    fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CELULAR)%>" /></label>
                <hl:htmlinput
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CELULAR)%>"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CELULAR)%>"
                  type="text" size="9"
                  mask="<%=LocaleHelper.getCelularMask()%>"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CELULAR)%>"
                  classe="form-control"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.celular", responsavel)%>" />
              </div>
            </div>
          </show:showfield>

          <show:showfield
            key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_EMAIL)%>">
            <div class="row">
              <div class="form-group col-sm-6">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_EMAIL)%>"><hl:message
                    key="rotulo.servidor.email" /></label>
                <hl:htmlinput
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_EMAIL)%>"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_EMAIL)%>"
                  type="text" size="32" mask="#*100"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_EMAIL)%>"
                  classe="form-control"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.email", responsavel)%>" />
              </div>
            </div>
          </show:showfield>
        </fieldset>
        <fieldset>
          <h3 class="legend">
            <span><hl:message
                key="rotulo.servidor.subtitulo.lotacao.v4" /></span>
          </h3>
          <show:showfield
            key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_MATRICULA)%>">
            <div class="row">
              <div class="form-group col-sm-6">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_MATRICULA)%>"><hl:message
                    key="rotulo.servidor.matricula" /></label>
                <hl:htmlinput
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_MATRICULA)%>"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_MATRICULA)%>"
                  type="text"
                  value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "RSE_MATRICULA"))%>"
                  size="32"
                  mask="<%=TextHelper.forHtmlAttribute(LoginHelper.getMascaraMatriculaServidor())%>"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_MATRICULA)%>"
                  classe="form-control"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.matricula", responsavel)%>" />
              </div>
            </div>
          </show:showfield>

          <show:showfield
            key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_ORGAO)%>">
            <div class="row">
              <div class="form-group col-sm-6">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_ORGAO)%>"><hl:message
                    key="rotulo.servidor.orgao" /></label>
                <hl:htmlcombo listName="orgaos"
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_ORGAO)%>"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_ORGAO)%>"
                  fieldValue="<%=TextHelper.forHtmlAttribute(Columns.ORG_CODIGO)%>"
                  fieldLabel="<%=(String) (Columns.ORG_NOME + ";" + Columns.ORG_IDENTIFICADOR)%>"
                  notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_ORGAO)%>"
                  autoSelect="true" classe="form-control">
                </hl:htmlcombo>
              </div>
            </div>
          </show:showfield>
          <show:showfield
            key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_SUB_ORGAO)%>">
            <div class="row">
              <div class="form-group col-sm-6">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_SUB_ORGAO)%>"><hl:message
                    key="rotulo.servidor.sub.orgao" /></label>
                <hl:htmlcombo listName="subOrgaos"
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_SUB_ORGAO)%>"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_SUB_ORGAO)%>"
                  fieldValue="<%=TextHelper.forHtmlAttribute(Columns.SBO_CODIGO)%>"
                  fieldLabel="<%=(String) (Columns.SBO_IDENTIFICADOR + ";" + Columns.SBO_DESCRICAO)%>"
                  notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_SUB_ORGAO)%>"
                  classe="form-control">
                </hl:htmlcombo>
              </div>
            </div>
          </show:showfield>
          <show:showfield
            key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_UNIDADE)%>">
            <div class="row">
              <div class="form-group col-sm-6">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_UNIDADE)%>"><hl:message
                    key="rotulo.servidor.unidade" /></label>
                <hl:htmlcombo listName="unidades" classe="form-control"
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_UNIDADE)%>"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_UNIDADE)%>"
                  fieldValue="<%=TextHelper.forHtmlAttribute(Columns.UNI_CODIGO)%>"
                  fieldLabel="<%=(String) (Columns.UNI_IDENTIFICADOR + ";" + Columns.UNI_DESCRICAO)%>"
                  notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_UNIDADE)%>">
                </hl:htmlcombo>
              </div>
            </div>
          </show:showfield>

          <show:showfield
            key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_MUNICIPIO_LOTACAO)%>">
            <div class="row">
              <div class="form-group col-sm-6">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_MUNICIPIO_LOTACAO)%>">
                  <hl:message key="rotulo.servidor.municipioLotacao" />
                </label>
                <hl:htmlinput
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_MUNICIPIO_LOTACAO)%>"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_MUNICIPIO_LOTACAO)%>"
                  type="text" size="40" mask="#*40"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_MUNICIPIO_LOTACAO)%>"
                  classe="form-control"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.municipio.lotacao",
							responsavel)%>" />
              </div>
            </div>
          </show:showfield>
        </fieldset>
        <fieldset>
          <show:showfield
            key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_SITUACAO)%>">
            <h3 class="legend">
              <span><hl:message
                  key="rotulo.servidor.subtitulo.contrato.trabalho" /></span>
            </h3>
            <div class="row">
              <div class="form-group col-sm-6">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_SITUACAO)%>"><hl:message
                    key="rotulo.servidor.status"
                    fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_SITUACAO)%>" /></label>
                <hl:htmlcombo 
                  listName="listaSrs" 
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_SITUACAO)%>"
                       name="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CADASTRAR_SERVIDOR_SITUACAO)%>" 
                       fieldValue="<%=TextHelper.forHtmlAttribute( Columns.SRS_CODIGO)%>" 
                       fieldLabel="<%=TextHelper.forHtmlAttribute( Columns.SRS_DESCRICAO)%>" 
                       notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>"
                       configKey="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CADASTRAR_SERVIDOR_SITUACAO)%>"
                       selectedValue="<%=TextHelper.forHtmlAttribute( srsCodigoPadrao )%>"
                       classe="form-control">
                </hl:htmlcombo>
              </div>
            </div>
          </show:showfield>
          <show:showfield
            key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CATEGORIA)%>">
            <div class="row">
              <div class="form-group col-sm-6">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CATEGORIA)%>"><hl:message
                    key="rotulo.servidor.categoria" /></label>
                <hl:htmlinput
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CATEGORIA)%>"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CATEGORIA)%>"
                  type="text" size="32" mask="#*255"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CATEGORIA)%>"
                  classe="form-control" 
                   placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.categoria",
                   responsavel)%>"
                  />
              </div>
            </div>
          </show:showfield>
          <show:showfield
            key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CARGO)%>">
            <div class="row">
              <div class="form-group col-sm-6">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CARGO)%>"><hl:message
                    key="rotulo.servidor.cargo" /></label>
                <hl:htmlcombo
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CARGO)%>"
                  listName="cargos"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CARGO)%>"
                  fieldValue="<%=TextHelper.forHtmlAttribute(Columns.CRS_CODIGO)%>"
                  fieldLabel="<%=(String) (Columns.CRS_IDENTIFICADOR + ";" + Columns.CRS_DESCRICAO)%>"
                  notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CARGO)%>"
                  classe="form-control">
                </hl:htmlcombo>
              </div>
            </div>
          </show:showfield>
          <show:showfield
            key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_TIPO_REG_SERVIDOR)%>">
            <div class="row">
              <div class="form-group col-sm-12 col-md-6">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_TIPO_REG_SERVIDOR)%>"><hl:message
                    key="rotulo.servidor.tipo" /></label>
                <hl:htmlcombo listName="listaTipoRegServidor"
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_TIPO_REG_SERVIDOR)%>"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_TIPO_REG_SERVIDOR)%>"
                  fieldValue="<%=TextHelper.forHtmlAttribute(Columns.TRS_CODIGO)%>"
                  notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>"
                  fieldLabel="<%=TextHelper.forHtmlAttribute(Columns.TRS_DESCRICAO)%>"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_TIPO_REG_SERVIDOR)%>"
                  classe="form-control">
                </hl:htmlcombo>
              </div>
            </div>
          </show:showfield>
          <show:showfield
            key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_POSTO)%>">
            <div class="row">
              <div class="form-group col-sm-12 col-md-6">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_POSTO)%>"><hl:message
                    key="rotulo.servidor.posto" /></label>
                <hl:htmlcombo listName="listaPostoCodigo"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_POSTO)%>"
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_POSTO)%>"
                  fieldValue="<%=TextHelper.forHtmlAttribute(Columns.POS_CODIGO)%>"
                  notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>"
                  fieldLabel="<%=TextHelper.forHtmlAttribute(Columns.POS_DESCRICAO)%>"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_POSTO)%>"
                  classe="form-control">
                </hl:htmlcombo>
              </div>
            </div>
          </show:showfield>


          <show:showfield
            key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_ESTABILIZADO)%>">
            <div class="row">
              <div class=" col-md-6 form-check mt-2" role="radiogroup"
                aria-labelledby="estabilizado">
                <div class="form-group my-0">
                  <span id="estabilizado"><hl:message
                      key="rotulo.servidor.estabilizado"
                      fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_ESTABILIZADO)%>" /></span>
                </div>
                <div class="form-check mt-2">
                  <hl:htmlinput
                    di="estabilizadoSim"
                    name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_ESTABILIZADO)%>"
                    type="radio" value="S" mask="#*10"
                    configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_ESTABILIZADO)%>"
                    classe="form-check-input ml-1" />
                  <label
                    class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top"
                    aria-label='<hl:message key="rotulo.sim"/>'
                    for="estabilizadoSim"><hl:message
                      key="rotulo.sim" /></label>
                  <hl:htmlinput
                    di="estabilizadoNao"
                    name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_ESTABILIZADO)%>"
                    type="radio" value="N" mask="#*10"
                    configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_ESTABILIZADO)%>"
                    classe="form-check-input ml-1" />
                  <label
                    class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top"
                    aria-label='<hl:message key="rotulo.nao"/>'
                    for="estabilizadoNao"><hl:message
                      key="rotulo.nao" /></label>
                </div>
              </div>
            </div>
          </show:showfield>
          <show:showfield
            key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_DATA_FIM_ENGAJAMENTO)%>">
            <div class="row">
              <div class="form-group col-sm-6">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_DATA_FIM_ENGAJAMENTO)%>"><hl:message
                    key="rotulo.servidor.engajado" /></label>
                <hl:htmlinput
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_DATA_FIM_ENGAJAMENTO)%>"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_DATA_FIM_ENGAJAMENTO)%>"
                  type="text" size="10"
                  mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_DATA_FIM_ENGAJAMENTO)%>"
                  classe="form-control"
                  placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>" />
              </div>
            </div>
          </show:showfield>
          <show:showfield
            key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_DATA_LIMITE_PERMANENCIA)%>">
            <div class="row">
              <div class="form-group col-sm-6">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_DATA_LIMITE_PERMANENCIA)%>"><hl:message
                    key="rotulo.servidor.dataLimitePermanencia" /></label>
                <hl:htmlinput
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_DATA_LIMITE_PERMANENCIA)%>"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_DATA_LIMITE_PERMANENCIA)%>"
                  type="text" size="10"
                  mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_DATA_LIMITE_PERMANENCIA)%>"
                  classe="form-control"
                  placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>" />
              </div>
            </div>
          </show:showfield>

          <show:showfield
            key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CAPACIDADE_CIVIL)%>">
            <div class="row">
              <div class="form-group col-sm-12 col-md-6">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CAPACIDADE_CIVIL)%>"><hl:message
                    key="rotulo.servidor.capacidadeCivil" /></label>
                <hl:htmlcombo listName="listaCapCivil"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CAPACIDADE_CIVIL)%>"
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CAPACIDADE_CIVIL)%>"
                  fieldValue="<%=TextHelper.forHtmlAttribute(Columns.CAP_CODIGO)%>"
                  fieldLabel="<%=TextHelper.forHtmlAttribute(Columns.CAP_DESCRICAO)%>"
                  notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CAPACIDADE_CIVIL)%>"
                  classe="form-control">
                </hl:htmlcombo>
              </div>
            </div>
          </show:showfield>
          <show:showfield
            key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_VINCULO)%>">
            <div class="row">
              <div class="form-group col-sm-12 col-md-6">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_VINCULO)%>"><hl:message
                    key="rotulo.servidor.vinculo" /></label>
                <hl:htmlcombo listName="listaVincRegSer"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_VINCULO)%>"
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_VINCULO)%>"
                  fieldValue="<%=TextHelper.forHtmlAttribute(Columns.VRS_CODIGO)%>"
                  fieldLabel="<%=TextHelper.forHtmlAttribute(Columns.VRS_DESCRICAO)%>"
                  notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_VINCULO)%>"
                  classe="form-control">
                </hl:htmlcombo>
              </div>
            </div>
          </show:showfield>

          <show:showfield
            key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_PADRAO)%>">
            <div class="row">
              <div class="form-group col-sm-12 col-md-6">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_PADRAO)%>"><hl:message
                    key="rotulo.servidor.padrao" /></label>
                <hl:htmlcombo listName="padrao"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_PADRAO)%>"
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_PADRAO)%>"
                  fieldValue="<%=TextHelper.forHtmlAttribute(Columns.PRS_CODIGO)%>"
                  fieldLabel="<%=(String) (Columns.PRS_IDENTIFICADOR + ";" + Columns.PRS_DESCRICAO)%>"
                  notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_PADRAO)%>"
                  classe="form-control">
                </hl:htmlcombo>
              </div>
            </div>
          </show:showfield>
          <show:showfield
            key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CLT)%>">
            <div class="row">
              <div class=" col-md-6 form-check mt-2" role="radiogroup"
                aria-labelledby="clt">
                <div class="form-group my-0">
                  <span id="clt"><hl:message
                      key="rotulo.servidor.clt" /></span>
                </div>
                <div class="form-check mt-2">
                  <hl:htmlinput
                    di="sindicalizadoSim"
                    name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CLT)%>"
                    type="radio" value="S" mask="#*10"
                    nf="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_DATA_ADMISSAO)%>"
                    configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CLT)%>"
                    classe="form-check-input ml-1" />
                  <label
                    class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top"
                    aria-label='<hl:message key="rotulo.sim"/>'
                    for="sindicalizadoSim"><hl:message
                      key="rotulo.sim" /></label>
                  <hl:htmlinput
                    di="sindicalizadoNao"
                    name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CLT)%>"
                    type="radio" value="N" mask="#*10"
                    nf="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_DATA_ADMISSAO)%>"
                    configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CLT)%>"
                    classe="form-check-input ml-1" />
                  <label
                    class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top"
                    aria-label='<hl:message key="rotulo.nao"/>'
                    for="sindicalizadoNao"><hl:message
                      key="rotulo.nao" /></label>
                </div>
              </div>
            </div>
          </show:showfield>
          <show:showfield
            key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_DATA_ADMISSAO)%>">
            <div class="row">
              <div class="form-group col-sm-6">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_DATA_ADMISSAO)%>"><hl:message
                    key="rotulo.servidor.dataAdmissao" /></label>
                <hl:htmlinput
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_DATA_ADMISSAO)%>"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_DATA_ADMISSAO)%>"
                  type="text" size="10"
                  mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_DATA_ADMISSAO)%>"
                  classe="form-control"
                  placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>" />
              </div>
            </div>
          </show:showfield>
          <show:showfield
            key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_PRAZO)%>">
            <div class="row">
              <div class="form-group col-sm-6">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_PRAZO)%>"><hl:message
                    key="rotulo.servidor.prazo" /></label>
                <hl:htmlinput
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_PRAZO)%>"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_PRAZO)%>"
                  type="text" size="4" mask="#D11"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_PRAZO)%>"
                  classe="form-control"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.prazo", responsavel)%>" />
              </div>
            </div>
          </show:showfield>
        </fieldset>
        <fieldset>
          <h3 class="legend">
            <span><hl:message
                key="rotulo.servidor.subtitulo.informacoes.financeiras" /></span>
          </h3>
          <div class="row">
            <div class="form-group col-sm-12">
              <div class="row">
                <show:showfield
                  key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_BANCO)%>">
                  <div class="col-sm-5 col-md-4">
                    <label
                      for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_BANCO)%>"><hl:message
                        key="rotulo.servidor.codigo.banco" /></label>
                    <hl:htmlinput
                      di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_BANCO)%>"
                      name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_BANCO)%>"
                      type="text" size="8" maxlength="8"
                      configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_BANCO)%>"
                      onFocus="SetarEventoMascara(this,'#A8',true);"
                      onBlur="<%=TextHelper.forJavaScript("fout(this);ValidaMascara(this);if (!IsNulo(document.forms[0]."
							+ FieldKeysConstants.CADASTRAR_SERVIDOR_BANCO
							+ ")) {SelecionaComboBanco(document.forms[0].RSE_BANCOS, document.forms[0]."
							+ FieldKeysConstants.CADASTRAR_SERVIDOR_BANCO + ".value, arrayBancos);}")%>"
                      classe="form-control"
                      placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.codigo.banco",
							responsavel)%>" />
                  </div>
                  <div class="col-sm-7 col-md-6">
                    <label for="RSE_BANCOS1"><hl:message
                      key="rotulo.servidor.banco" /></label> <SELECT
                      id="RSE_BANCOS1"
                      NAME="RSE_BANCOS"
                      CLASS="form-control"
                      onChange="document.forms[0].<%=TextHelper.forJavaScript(FieldKeysConstants.CADASTRAR_SERVIDOR_BANCO)%>.value = document.forms[0].RSE_BANCOS.value;"
                      <%if (ShowFieldHelper.isDisabled(FieldKeysConstants.CADASTRAR_SERVIDOR_BANCO, responsavel)) {%>
                      DISABLED <%}%>>
                      <OPTION VALUE="" SELECTED><hl:message
                         key="rotulo.campo.selecione" /></OPTION>
                    </SELECT>
                  </div>
                </show:showfield>
              </div>
            </div>
            <show:showfield
              key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_AGENCIA)%>">
              <div class="form-group col-sm-5 col-md-4">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_AGENCIA)%>"><hl:message
                    key="rotulo.servidor.codigo.agencia" /></label>
                <hl:htmlinput
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_AGENCIA)%>"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_AGENCIA)%>"
                  type="text" size="10" mask="#*30"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_AGENCIA)%>"
                  maxlength="30"
                  onFocus="SetarEventoMascara(this,'#D5',true);"
                  onBlur="fout(this);ValidaMascara(this);"
                  classe="form-control"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.codigo.agencia",
							responsavel)%>" />
              </div>
            </show:showfield>
            <show:showfield
              key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CONTA)%>">
              <div class="form-group col-sm-5 col-md-4">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CONTA)%>"><hl:message
                    key="rotulo.servidor.codigo.conta" /></label>
                <hl:htmlinput
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CONTA)%>"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CONTA)%>"
                  type="text" size="10" mask="#*40" maxlength="40"
                  onFocus="SetarEventoMascara(this,'#*40',true);"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CONTA)%>"
                  onBlur="fout(this);ValidaMascara(this);"
                  classe="form-control"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.codigo.conta",
							responsavel)%>" />
              </div>
            </show:showfield>
          </div>

          <div class="row">
            <div class="form-group col-sm-12">
              <div class="row">
                <show:showfield
                  key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_BANCO_ALTERNATIVO)%>">
                  <div class="col-sm-5 col-md-4">
                    <label
                      for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_BANCO_ALTERNATIVO)%>"><hl:message
                        key="rotulo.servidor.codigo.banco.alternativo" /></label>
                    <hl:htmlinput
                      di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_BANCO_ALTERNATIVO)%>"
                      name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_BANCO_ALTERNATIVO)%>"
                      type="text" size="8" maxlength="8"
                      configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_BANCO_ALTERNATIVO)%>"
                      onFocus="SetarEventoMascara(this,'#A8',true);"
                      onBlur="<%=TextHelper.forJavaScript("fout(this);ValidaMascara(this);if (!IsNulo(document.forms[0]."
							+ FieldKeysConstants.CADASTRAR_SERVIDOR_BANCO_ALTERNATIVO
							+ ")) {SelecionaComboBanco(document.forms[0].RSE_BANCOS_2, document.forms[0]."
							+ FieldKeysConstants.CADASTRAR_SERVIDOR_BANCO_ALTERNATIVO + ".value, arrayBancos);}")%>"
                      classe="form-control"
                      placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.banco.alternativo",
							responsavel)%>" />
                  </div>
                  <div class="col-sm-7 col-md-6">
                    <label for="RSEBANCOS_2"><hl:message
                        key="rotulo.servidor.banco"
                        fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_BANCO_ALTERNATIVO)%>" /></label>
                    <SELECT
                      id="RSEBANCOS_2" NAME="RSE_BANCOS_2" CLASS="form-control"
                      onChange="document.forms[0].<%=TextHelper.forJavaScript(FieldKeysConstants.CADASTRAR_SERVIDOR_BANCO_ALTERNATIVO)%>.value = document.forms[0].RSE_BANCOS_2.value;"
                      <%if (ShowFieldHelper.isDisabled(FieldKeysConstants.CADASTRAR_SERVIDOR_BANCO_ALTERNATIVO,
							responsavel)) {%>
                      DISABLED <%}%>>
                      <OPTION VALUE="" SELECTED><hl:message
                          key="rotulo.campo.selecione" /></OPTION>
                    </SELECT>
                  </div>
                </show:showfield>
              </div>
            </div>
            <div class="form-group col-sm-12">
              <div class="row">
                <show:showfield
                  key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_AGENCIA_ALTERNATIVA)%>">
                  <div class="col-sm-5 col-md-4">
                    <label
                      for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_AGENCIA_ALTERNATIVA)%>"><hl:message
                        key="rotulo.servidor.codigo.agencia.alternativo" /></label>
                    <hl:htmlinput
                      di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_AGENCIA_ALTERNATIVA)%>"
                      name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_AGENCIA_ALTERNATIVA)%>"
                      type="text" size="10" mask="#*30" maxlength="30"
                      configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_AGENCIA_ALTERNATIVA)%>"
                      onFocus="SetarEventoMascara(this,'#D5',true);"
                      onBlur="fout(this);ValidaMascara(this);"
                      classe="form-control"
                      placeHolder="<%=ApplicationResourcesHelper
							.getMessage("mensagem.informacao.servidor.codigo.agencia.alternativa", responsavel)%>" />
                  </div>
                </show:showfield>
                <show:showfield
                  key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CONTA_ALTERNATIVA)%>">
                  <div class="col-sm-7 col-md-4">
                    <label
                      for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CONTA_ALTERNATIVA)%>"><hl:message
                        key="rotulo.servidor.codigo.conta.alternativo" /></label>
                    <hl:htmlinput
                      di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CONTA_ALTERNATIVA)%>"
                      name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CONTA_ALTERNATIVA)%>"
                      type="text" size="10" mask="#*40"
                      configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_CONTA_ALTERNATIVA)%>"
                      maxlength="40"
                      onFocus="SetarEventoMascara(this,'#*40',true);"
                      onBlur="fout(this);ValidaMascara(this);"
                      classe="form-control"
                      placeHolder="<%=ApplicationResourcesHelper
							.getMessage("mensagem.informacao.servidor.codigo.conta.alternativa", responsavel)%>" />
                  </div>
                </show:showfield>
              </div>
            </div>
          </div>
        </fieldset>
        <fieldset>
          <show:showfield
            key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_OBSERVACAO)%>">
            <h3 class="legend">
              <span><hl:message
                  key="rotulo.servidor.subtitulo.informacoes.adicionais" /></span>
            </h3>
            <div class="row">
              <div class="form-group col-sm-12">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_OBSERVACAO)%>"><hl:message
                    key="rotulo.servidor.obs" /></label>
                <hl:htmlinput
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_OBSERVACAO)%>"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_OBSERVACAO)%>"
                  type="textarea" rows="5" cols="50"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_OBSERVACAO)%>"
                  others="onFocus=\"SetarEventoMascara(this,'#*65000',true);\" onBlur=\"fout(this);ValidaMascara(this);\""
                  classe="form-control"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.obs", responsavel)%>" />
              </div>
            </div>
          </show:showfield>
        </fieldset>
      </form>
    </div>
  </div>
  <div class="btn-action">
    <a href="#no-back" id="btnCancelar"
      class="btn btn-outline-danger"
      onClick="postData('<%=TextHelper.forJavaScriptAttribute(
						SynchronizerToken.updateTokenInURL("../v3/consultarServidor?acao=iniciar", request))%>'); return false;"><hl:message
        key="rotulo.botao.cancelar" /></a> <a href="#no-back"
      class="btn btn-primary" id="btnSalvar"
      onClick="if(vf_edita_servidor()){f0.submit()};"><hl:message
        key="rotulo.botao.salvar" /></a>
  </div>
  </div>
</c:set>
<c:set var="javascript">
  <script language="JavaScript" type="text/JavaScript"
    src="../js/scripts_2810.js?<hl:message key="release.tag"/>"></script>
  <script language="JavaScript" type="text/JavaScript"
    src="../js/validaform.js?<hl:message key="release.tag"/>"></script>
  <script language="JavaScript" type="text/JavaScript"
    src="../js/validacoes.js?<hl:message key="release.tag"/>"></script>
  <script language="JavaScript" type="text/JavaScript"
    src="../js/validaemail.js?<hl:message key="release.tag"/>"></script>
  <script language="JavaScript" type="text/JavaScript"
    src="../js/xbdhtml.js?<hl:message key="release.tag"/>"></script>
  <script type="text/JavaScript">


var f0 = document.forms[0];

var arrayBancos = <%=(String) JspHelper.geraArrayBancos(responsavel)%>;

function formLoad() {
	if (document.forms[0].RSE_BANCOS != null) {
       AtualizaFiltraComboExt(document.forms[0].RSE_BANCOS, arrayBancos, '', '', '', false, false, '', '');
     }
     if (document.forms[0].RSE_BANCOS_2 != null) {
       AtualizaFiltraComboExt(document.forms[0].RSE_BANCOS_2, arrayBancos, '', '', '', false, false, '', '');
     }
  focusFirstField();
}
window.onload = formLoad;

// Verifica formularios de cadastro de servidores
function vf_edita_servidor() {
  var Controles = new Array('<%=(String) (FieldKeysConstants.CADASTRAR_SERVIDOR_CPF)%>',
                            '<%=(String) (FieldKeysConstants.CADASTRAR_SERVIDOR_ORGAO)%>',
                            '<%=(String) (FieldKeysConstants.CADASTRAR_SERVIDOR_MATRICULA)%>');

  var Msgs = new Array('<hl:message key="mensagem.informe.servidor.cpf"/>',
                       '<hl:message key="mensagem.informe.servidor.orgao"/>',
                       '<hl:message key="mensagem.informe.servidor.matricula"/>');

<%if (ShowFieldHelper.isRequired(FieldKeysConstants.CADASTRAR_SERVIDOR_NOME, responsavel)) {%>
     var serNome = f0.<%=(String) (FieldKeysConstants.CADASTRAR_SERVIDOR_NOME)%>;
     if (serNome.value == null || serNome.value == '') {
       alert('<hl:message key="mensagem.informe.servidor.nome"/>');
       serNome.focus();
       return false;
     }
<%}%>

<%if (ShowFieldHelper.isRequired(FieldKeysConstants.CADASTRAR_SERVIDOR_PRIMEIRO_NOME, responsavel)) {%>
     var primeiroNome = f0.<%=(String) (FieldKeysConstants.CADASTRAR_SERVIDOR_PRIMEIRO_NOME)%>;
       if (primeiroNome.value == null || primeiroNome.value == '') {
         alert('<hl:message key="mensagem.informe.servidor.nome"/>');
         primeiroNome.focus();
         return false;
       }
<%}%>

<%if (ShowFieldHelper.isRequired(FieldKeysConstants.CADASTRAR_SERVIDOR_TITULACAO, responsavel)) {%>
      var nomeMeio = f0.<%=(String) (FieldKeysConstants.CADASTRAR_SERVIDOR_TITULACAO)%>;
      if (nomeMeio.value == null || nomeMeio.value == '') {
         alert('<hl:message key="mensagem.informe.servidor.tratamento.nome"/>');
         nomeMeio.focus();
        return false;
     }
<%}%>

<%if (ShowFieldHelper.isRequired(FieldKeysConstants.CADASTRAR_SERVIDOR_NOME_MEIO, responsavel)) {%>
     var nomeMeio = f0.<%=(String) (FieldKeysConstants.CADASTRAR_SERVIDOR_NOME_MEIO)%>;
       if (nomeMeio.value == null || nomeMeio.value == '') {
         alert('<hl:message key="mensagem.informe.servidor.meio.nome"/>');
         nomeMeio.focus();
         return false;
     }
<%}%>

<%if (ShowFieldHelper.isRequired(FieldKeysConstants.CADASTRAR_SERVIDOR_ULTIMO_NOME, responsavel)) {%>
     var ultimoNome = f0.<%=(String) (FieldKeysConstants.CADASTRAR_SERVIDOR_ULTIMO_NOME)%>;
       if (ultimoNome.value == null || ultimoNome.value == '') {
         alert('<hl:message key="mensagem.informe.servidor.ultimo.nome"/>');
         ultimoNome.focus();
         return false;
     }
<%}%>

<%if (ShowFieldHelper.canEdit(FieldKeysConstants.CADASTRAR_SERVIDOR_EMAIL, responsavel)) {%>
  var emailField = f0.<%=(String) (FieldKeysConstants.CADASTRAR_SERVIDOR_EMAIL)%>;
  if (emailField.value != null && emailField.value != '' && !isEmailValid(emailField.value)) {
    alert('<hl:message key="mensagem.erro.servidor.email.invalido"/>');
    emailField.focus();
    return false;
  }
<%}%>

<%if (ShowFieldHelper.canEdit(FieldKeysConstants.CADASTRAR_SERVIDOR_TELEFONE, responsavel)) {%>
  var telefoneField = f0.<%=(String) (FieldKeysConstants.CADASTRAR_SERVIDOR_TELEFONE)%>;
  if (telefoneField.value != null && telefoneField.value != '' && telefoneField.value.length < '<%=LocaleHelper.getTelefoneSize()%>') {
  	alert('<hl:message key="mensagem.erro.servidor.telefone.invalido"/>');
  	telefoneField.focus();
  	return false;
  }
<%}%>

<%if (ShowFieldHelper.canEdit(FieldKeysConstants.CADASTRAR_SERVIDOR_CELULAR, responsavel)) {%>
  var celularField = f0.<%=(String) (FieldKeysConstants.CADASTRAR_SERVIDOR_CELULAR)%>;
  if (celularField.value != null && celularField.value != '' && celularField.value.length < '<%=LocaleHelper.getCelularSize()%>') {
	alert('<hl:message key="mensagem.erro.servidor.celular.invalido"/>');
	celularField.focus();
	return false;
  }
<%}%>

  if (ValidaCampos(Controles, Msgs)) {
    enableAll();
    return true;
  } else {
    return false;
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