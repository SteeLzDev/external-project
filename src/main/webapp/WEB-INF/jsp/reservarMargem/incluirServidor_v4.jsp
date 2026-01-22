<%--
* <p>Title: incluirServidor.jsp</p>
* <p>Description: Página de inclusão de um servidor/registro servidor para caso de uso incluir consignação.</p>
* <p>Copyright: Copyright (c) 2002-2017</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.usuario.LoginHelper"%>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
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

String ser_nome = JspHelper.verificaVarQryStr(request, "serNome");
String ser_sobrenome = JspHelper.verificaVarQryStr(request, "serSobreNome");
String ser_data_nasc = JspHelper.verificaVarQryStr(request, "serDataNasc");

String rse_matricula = JspHelper.verificaVarQryStr(request, "RSE_MATRICULA");
String org_codigo = JspHelper.verificaVarQryStr(request, "ORG_CODIGO");
String ser_cpf = JspHelper.verificaVarQryStr(request, "SER_CPF");
%>
<c:set var="title">
  <hl:message key="rotulo.cadastrar.servidor.incluir.consignacao.titulo" />
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
  <div class="card">
      <div class="card-header hasIcon">
        <span class="card-header-icon">
          <svg width="26">
            <use xlink:href="#i-servidor"></use>
          </svg>
        </span>
        <h2 class="card-header-title"><hl:message key="rotulo.servidor.dados"/></h2>
      </div>
    <div class="card-body">
      <form method="post" action="../v3/incluirConsignacao" name="form1">
        <hl:htmlinput name="acao" type="hidden" value="incluirServidorParaReserva" />
        <hl:htmlinput name="SVC_CODIGO" type="hidden" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "SVC_CODIGO"))%>" />
        <%=SynchronizerToken.generateHtmlToken(request)%>
        
          <% if (ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME, responsavel) ||
                 ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRIMEIRO_NOME, responsavel) ||
                 ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_PAI, responsavel) ||
                 ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_MAE, responsavel) ||
                 ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_CONJUGE, responsavel) ||
                 ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_NASCIMENTO, responsavel) ||
                 ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF_NASC, responsavel) ||
                 ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CIDADE_NASC, responsavel) ||
                 ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NACIONALIDADE, responsavel) ||
                 ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTADO_CIVIL, responsavel) ||
                 ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SEXO, responsavel)||
                 ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_QTD_FILHOS, responsavel)||
                 ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NIVEL_ESCOLARIDADE, responsavel)
                 ) { %>
           <fieldset>
               <h3 class="legend"><span><hl:message key="rotulo.servidor.subtitulo.informacoes.gerais"/></span></h3>
                <% } %>
                  <div class="row">
                         <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TITULACAO)%>">
                           <div class="form-group col-sm-3">
                             <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TITULACAO)%>"><hl:message key="rotulo.servidor.titulacao" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TITULACAO)%>"/></label>
                             <select name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TITULACAO)%>" id="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TITULACAO)%>" class="form-control" >
                                <option value=" " SELECTED ><hl:message key="rotulo.campo.selecione"/></option>
                                <%
                                    String titulacao = ApplicationResourcesHelper.getMessage("rotulo.servidor.titulacao.valores", responsavel);
                                    List<String> titLst = new ArrayList<String>();         
                                    String vlrRequest = JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TITULACAO));
                                    if (!TextHelper.isNull(titulacao)) {
                                        titLst = Arrays.asList(titulacao.split(",|;"));
                                    }
                                    for (String tratamento : titLst) { 
                                %>                          
                                       <option value="<%=tratamento%>" <%= (!TextHelper.isNull(vlrRequest) && vlrRequest.equals(tratamento)) ? "SELECTED" : "" %> ><%=tratamento%></OPTION>
                                <%
                                    }
                                %>
                            </select>
                           </div>
                         </show:showfield>   
                         <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRIMEIRO_NOME)%>">
                            <div class="form-group col-sm-3">
                                <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRIMEIRO_NOME)%>"><hl:message key="rotulo.servidor.nome" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRIMEIRO_NOME)%>"/></label>
                                <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRIMEIRO_NOME)%>"
                                    di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRIMEIRO_NOME)%>"
                                    type="text"
                                    classe="form-control"
                                    value="<%=TextHelper.isNull(ser_nome) ? TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRIMEIRO_NOME))) : ser_nome%>"
                                    mask="#*100"
                                    configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRIMEIRO_NOME)%>"
                                    placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.primeiro.nome", responsavel)%>"                             
                                />
                            </div>
                         </show:showfield>
                         
                         <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MEIO_NOME)%>">
                              <div class="form-group col-sm-3">
                                  <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MEIO_NOME)%>"><hl:message key="rotulo.servidor.meio.nome" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MEIO_NOME)%>"/></label>
                                  <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MEIO_NOME)%>"
                                      di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MEIO_NOME)%>"
                                      type="text"
                                      classe="form-control"
                                      value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MEIO_NOME)))%>"
                                      mask="#*100"
                                      configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MEIO_NOME)%>"
                                      placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.nome.meio", responsavel)%>"                             
                                  />
                              </div>
                           </show:showfield>
                           
                           <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ULTIMO_NOME)%>">
                           <div class="form-group col-sm-3">
                                <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ULTIMO_NOME)%>"><hl:message key="rotulo.servidor.sobrenome" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ULTIMO_NOME)%>"/></label>
                                <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ULTIMO_NOME)%>"
                                    di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ULTIMO_NOME)%>"
                                    type="text"
                                    classe="form-control"
                                    value="<%=TextHelper.isNull(ser_sobrenome) ? TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ULTIMO_NOME))) : ser_sobrenome%>"
                                    mask="#*100"
                                    configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ULTIMO_NOME)%>"
                                    placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.ultimo.nome", responsavel)%>"                             
                                />
                           </div>
                          </show:showfield>
                    </div>
                    <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME)%>">
                      <div class="row">
                       <div class="form-group col-sm-6">
                            <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME)%>"><hl:message key="rotulo.servidor.nome.completo" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME)%>"/></label>
                            <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME)%>"
                                di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME)%>"
                                type="text"
                                classe="form-control"
                                value="<%=TextHelper.isNull(ser_nome) ? TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME))) : JspHelper.montaSerNome(null, ser_nome, null, ser_sobrenome)%>"
                                mask="#*100"
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME)%>"
                                placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.nome", responsavel)%>"                             
                            />
                        </div>
                     </div>
                    </show:showfield>
                    
                     <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_PAI)%>">
                       <div class="row">
                        <div class="form-group col-sm-6">
                            <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_PAI)%>"><hl:message key="rotulo.servidor.nomePai" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_PAI)%>"/></label>
                            <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_PAI)%>"
                                di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_PAI)%>"
                                type="text"
                                classe="form-control"
                                value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_PAI)))%>"
                                mask="#*100"
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_PAI)%>"
                                placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.nome.pai", responsavel)%>"                             
                            />
                        </div>
                       </div>
                     </show:showfield>
                     
                     <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_MAE)%>">
                       <div class="row">
                        <div class="form-group col-sm-6">
                            <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_MAE)%>"><hl:message key="rotulo.servidor.nomeMae" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_MAE)%>"/></label>
                            <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_MAE)%>"
                                di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_MAE)%>"
                                type="text"
                                classe="form-control"
                                value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_MAE)))%>"
                                mask="#*100"
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_MAE)%>"
                                placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.nome.mae", responsavel)%>"                             
                            />
                        </div>
                       </div>
                     </show:showfield>
                     
                     <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_NASCIMENTO)%>">
                      <div class="row">
                       <div class="form-group col-sm-6">
                            <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_NASCIMENTO)%>"><hl:message key="rotulo.servidor.dataNasc" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_NASCIMENTO)%>"/></label>
                            <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_NASCIMENTO)%>"
                                di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_NASCIMENTO)%>"
                                type="text"
                                classe="form-control"
                                value="<%=TextHelper.isNull(ser_data_nasc) ? TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_NASCIMENTO))) : ser_data_nasc%>"
                                mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_NASCIMENTO)%>"
                                placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>"                             
                            />
                       </div>
                      </div>
                     </show:showfield>
                     
                     <div class = row>
                       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CIDADE_NASC)%>">
                         <div class="form-group col-sm-6">
                           <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CIDADE_NASC)%>"><hl:message key="rotulo.servidor.cidade.nascimento" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CIDADE_NASC)%>"/></label>
                           <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CIDADE_NASC)%>"
                               di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CIDADE_NASC)%>"
                               type="text"
                               classe="form-control"
                               value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CIDADE_NASC)))%>"
                               mask="#*40"
                               configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CIDADE_NASC)%>"
                               placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.cidade.nascimento", responsavel)%>"                             
                           />
                        </div>
                       </show:showfield>
                       
                       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF_NASC)%>">
                         <div class="form-group col-sm-6">
                           <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF_NASC)%>"><hl:message key="rotulo.servidor.estado.nascimento" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF_NASC)%>"/></label>
                            <hl:campoUFv4 name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF_NASC)%>"
                               classe="form-control"
                               di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF_NASC)%>"
                               rotuloUf="rotulo.estados"
                               placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.uf", responsavel)%>"
                               valorCampo="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF_NASC)))%>"
                             />                  
                           </div>
                       </show:showfield>
                     </div>
                     
                    <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SEXO)%>">
                    <div class="row">
                     <div class=" col-md-6 form-check mt-2" role="radiogroup" aria-labelledby="sexo">
                        <div class="form-group my-0">
                          <span id="sexo"><hl:message key="rotulo.servidor.sexo" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SEXO)%>"/></span>
                        </div>
                        <div class="form-check mt-2">
                          <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SEXO)%>"
                             di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SEXO) + "_M"%>"
                             type="radio"
                             value="M"
                             checked="<%=String.valueOf(TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SEXO))).equalsIgnoreCase(\"M\"))%>"
                             mask="#*10"
                             configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SEXO)%>"
                             classe="form-check-input ml-1"
                           />
                          <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.servidor.sexo.masculino"/>' for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SEXO)%>"><hl:message key="rotulo.servidor.sexo.masculino"/></label>
                          <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SEXO)%>"
                             di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SEXO) + "_F"%>"
                             type="radio"
                             value="F"
                             checked="<%=String.valueOf(TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SEXO))).equalsIgnoreCase(\"F\"))%>"
                             mask="#*10"
                             configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SEXO)%>"
                             classe="form-check-input ml-1"
                           />
                          <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.servidor.sexo.feminino"/>' for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SEXO)%>"><hl:message key="rotulo.servidor.sexo.feminino"/></label>
                        </div>
                     </div>
                    </div>
                   </show:showfield>
                    
                   <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTADO_CIVIL)%>">
                    <div class="row">
                     <div class="form-group col-sm-12 col-md-6">
                          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTADO_CIVIL)%>"><hl:message key="rotulo.servidor.estadoCivil" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTADO_CIVIL)%>"/></label>
                          <hl:htmlcombo
                             listName="listaEstadoCivil" 
                             name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTADO_CIVIL)%>" 
                             fieldValue="<%=TextHelper.forHtmlAttribute(Columns.EST_CIVIL_CODIGO )%>" 
                             fieldLabel="<%=TextHelper.forHtmlAttribute(Columns.EST_CIVIL_DESCRICAO )%>" 
                             notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>"
                             selectedValue="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTADO_CIVIL)))%>" 
                             configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTADO_CIVIL)%>"
                             classe="form-control"
                               >
                          </hl:htmlcombo>
                     </div>
                    </div>
                   </show:showfield>
                   
                   <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_QTD_FILHOS)%>">
                      <div class="row">
                       <div class="form-group col-sm-6">
                         <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_QTD_FILHOS)%>"><hl:message key="rotulo.servidor.quantidade.filhos" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_QTD_FILHOS)%>"/></label>
                         <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_QTD_FILHOS)%>"
                           di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_QTD_FILHOS)%>"
                           type="text"
                           classe="form-control"
                           value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_QTD_FILHOS)))%>"
                           mask="#D2"
                           configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_QTD_FILHOS)%>"
                           placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.qtd.filhos", responsavel)%>"                             
                         />
                       </div>
                      </div>
                     </show:showfield>
                     
                  <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NIVEL_ESCOLARIDADE)%>">
                    <c:if test="${not empty listaNivelEscolaridade}">
                      <div class="row">
                       <div class="form-group col-sm-12 col-md-6">
                            <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NIVEL_ESCOLARIDADE)%>"><hl:message key="rotulo.servidor.nivel.escolaridade" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NIVEL_ESCOLARIDADE)%>"/></label>
                            <hl:htmlcombo
                                 listName="listaNivelEscolaridade" 
                                 name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NIVEL_ESCOLARIDADE)%>" 
                                 fieldValue="<%=TextHelper.forHtmlAttribute(Columns.NES_CODIGO )%>" 
                                 fieldLabel="<%=TextHelper.forHtmlAttribute(Columns.NES_DESCRICAO )%>" 
                                 notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>"
                                 selectedValue="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NIVEL_ESCOLARIDADE)))%>" 
                                 configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NIVEL_ESCOLARIDADE)%>"
                                 classe="form-control"
                                 >
                            </hl:htmlcombo>
                       </div>
                      </div>
                     </c:if>
                   </show:showfield>
                   
                    <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NACIONALIDADE)%>">
                      <div class="row">
                       <div class="form-group col-sm-6">
                         <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NACIONALIDADE)%>"><hl:message key="rotulo.servidor.nacionalidade" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NACIONALIDADE)%>"/></label>
                         <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NACIONALIDADE)%>"
                           di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NACIONALIDADE)%>"
                           type="text"
                           classe="form-control"
                           value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NACIONALIDADE)))%>"
                           mask="#*40"
                           configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NACIONALIDADE)%>"
                           placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.nacionalidade", responsavel)%>"                             
                         />
                       </div>
                      </div>
                     </show:showfield>
                   
                   <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_CONJUGE)%>">
                     <div class="row">
                      <div class="form-group col-sm-6">
                          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_CONJUGE)%>"><hl:message key="rotulo.servidor.nome.conjuge" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_CONJUGE)%>"/></label>
                          <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_CONJUGE)%>"
                            di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_CONJUGE)%>"
                            type="text"
                            classe="form-control"
                            value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_CONJUGE)))%>"
                            mask="#*100"
                            configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_CONJUGE)%>"
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.nome.conjuge", responsavel)%>"                             
                          />
                      </div>
                     </div>
                   </show:showfield>            
                 <%
                                 if (ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME, responsavel) ||
                                              ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRIMEIRO_NOME, responsavel) ||
                                              ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_PAI, responsavel) ||
                                              ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_MAE, responsavel) ||
                                              ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_CONJUGE, responsavel) ||
                                              ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_NASCIMENTO, responsavel) ||
                                              ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF_NASC, responsavel) ||
                                              ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CIDADE_NASC, responsavel) ||
                                              ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NACIONALIDADE, responsavel) ||
                                              ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTADO_CIVIL, responsavel) ||
                                              ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SEXO, responsavel)||
                                              ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_QTD_FILHOS, responsavel)||
                                              ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NIVEL_ESCOLARIDADE, responsavel)
                                              ) {
                             %>
                  </fieldset>
                  <%
                      }
                  %>
                  
                  <%
                                        if (ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CPF, responsavel) ||
                                                     ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_IDENTIDADE, responsavel) ||
                                                     ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_EMISSOR_IDENTIDADE, responsavel) ||
                                                     ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF_IDENTIDADE, responsavel) ||
                                                     ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_IDENTIDADE, responsavel) ||
                                                     ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NUM_CARTEIRA_TRABALHO, responsavel) ||
                                                     ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NUM_PIS, responsavel)) {
                                    %>
                  <fieldset>
                     <h3 class="legend"><span><hl:message key="rotulo.servidor.subtitulo.documentos.pessoais"/></span></h3>
                  <%
                      }
                  %>
                  
                  <div class="row">
                     <div class="form-group col-sm-6">
                      <hl:campoCPFv4 name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CPF)%>"
                         configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CPF)%>"
                         value="<%=TextHelper.isNull(ser_cpf) ? TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CPF))) : ser_cpf%>"
                         others="<%=TextHelper.forHtmlAttribute((!ParamSist.paramEquals(CodedValues.TPC_PERMITE_EDITAR_CPF_SERVIDOR, CodedValues.TPC_SIM, responsavel)) ? "disabled" : "")%>"
                      />
                     </div>
                    </div>
                    
                    <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_IDENTIDADE)%>">
                     <div class="row">
                      <div class="form-group col-sm-3">
                        <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_IDENTIDADE)%>"><hl:message key="rotulo.servidor.cartIdentidade" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_IDENTIDADE)%>"/></label>
                        <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_IDENTIDADE)%>"
                            di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_IDENTIDADE)%>"
                            type="text"
                            classe="form-control"
                            value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_IDENTIDADE)))%>"
                            mask="#*40"
                            configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_IDENTIDADE)%>"
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.numero.identidade", responsavel)%>"                             
                        />
                      </div>
                      <div class="form-group col-sm-3">
                        <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_EMISSOR_IDENTIDADE)%>"><hl:message key="rotulo.servidor.rg.orgao.emissor" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_EMISSOR_IDENTIDADE)%>"/></label>
                        <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_EMISSOR_IDENTIDADE)%>"
                            di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_EMISSOR_IDENTIDADE)%>"
                            type="text"
                            classe="form-control"
                            value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_EMISSOR_IDENTIDADE)))%>"
                            mask="#*40"
                            configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_EMISSOR_IDENTIDADE)%>"
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.orgao.emissor", responsavel)%>"                             
                        />
                      </div>
                      <div class="form-group col-sm-3">
                        <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF_IDENTIDADE)%>"><hl:message key="rotulo.servidor.rg.uf" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF_IDENTIDADE)%>"/></label>
                        <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF_IDENTIDADE)%>"
                            di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF_IDENTIDADE)%>"
                            type="text"
                            classe="form-control"
                            value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF_IDENTIDADE)))%>"
                            mask="#*2"
                            configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF_IDENTIDADE)%>"
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.uf", responsavel)%>"                             
                        />
                      </div>
                      <div class="form-group col-sm-3">
                        <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_IDENTIDADE)%>"><hl:message key="rotulo.servidor.rg.data.emissao" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_IDENTIDADE)%>"/></label>
                        <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_IDENTIDADE)%>"
                          di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_IDENTIDADE)%>"
                          type="text"
                          classe="form-control"
                          value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_IDENTIDADE)))%>"
                          mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                          configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_IDENTIDADE)%>"
                          placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>"                             
                        />
                      </div>
                     </div>      
                    </show:showfield>
                    
                    <div class="row">
                      <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NUM_CARTEIRA_TRABALHO)%>">
                          <div class="form-group col-sm-6">
                            <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NUM_CARTEIRA_TRABALHO)%>"><hl:message key="rotulo.servidor.cartTrabalho" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NUM_CARTEIRA_TRABALHO)%>"/></label>
                            <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NUM_CARTEIRA_TRABALHO)%>"
                              di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NUM_CARTEIRA_TRABALHO)%>"
                              type="text"
                              classe="form-control"
                              value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NUM_CARTEIRA_TRABALHO)))%>"
                              mask="#*40"
                              configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NUM_CARTEIRA_TRABALHO)%>"
                              placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.cartTrabalho", responsavel)%>"                             
                            />
                         </div>
                      </show:showfield>
                        
                      <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NUM_PIS)%>">
                        <div class="form-group col-sm-6">
                         <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NUM_PIS)%>"><hl:message key="rotulo.servidor.pis" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NUM_PIS)%>"/></label>
                         <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NUM_PIS)%>"
                             di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NUM_PIS)%>"
                             type="text"
                             classe="form-control"
                             value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NUM_PIS)))%>"
                             mask="#*40"
                             configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NUM_PIS)%>"
                             placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.pis", responsavel)%>"                             
                         />
                        </div>
                      </show:showfield>
                    </div>
                    
                    <%
                                            if (ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CPF, responsavel) ||
                                                     ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_IDENTIDADE, responsavel) ||
                                                     ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_EMISSOR_IDENTIDADE, responsavel) ||
                                                     ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF_IDENTIDADE, responsavel) ||
                                                     ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_IDENTIDADE, responsavel) ||
                                                     ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NUM_CARTEIRA_TRABALHO, responsavel) ||
                                                     ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NUM_PIS, responsavel)) {
                                        %>
                    </fieldset>
                    <%
                        }
                    %>
                   
                    <%
                                           if (ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_LOGRADOURO, responsavel) ||
                                                          ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NRO, responsavel) ||
                                                          ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_COMPLEMENTO, responsavel) ||
                                                          ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BAIRRO, responsavel) ||
                                                          ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CIDADE, responsavel) ||
                                                          ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF, responsavel) ||
                                                          ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CEP, responsavel)) {
                                       %>
                    <fieldset>
                       <h3 class="legend"><span><hl:message key="rotulo.servidor.subtitulo.endereco.residencial"/></span></h3>
                    <%
                        }
                    %> 
                    
                    <div class="row">
                      <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_LOGRADOURO)%>">
                        <div class="form-group col-sm-4">
                         <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_LOGRADOURO)%>"><hl:message key="rotulo.endereco.logradouro" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_LOGRADOURO)%>"/></label>
                         <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_LOGRADOURO)%>"
                           di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_LOGRADOURO)%>"
                           type="text"
                           classe="form-control"
                           value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_LOGRADOURO)))%>"
                           mask="#*100"
                           configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_LOGRADOURO)%>"
                           placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.mensagem.endereco.logradouro", responsavel)%>"                             
                         />
                        </div>
                      </show:showfield>
                      
                     <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NRO)%>">
                      <div class="form-group col-sm-2">
                       <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NRO)%>"><hl:message key="rotulo.endereco.numero.extenso" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NRO)%>"/></label>
                       <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NRO)%>"
                           di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NRO)%>"
                           type="text"
                           classe="form-control"
                           value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NRO)))%>"
                           mask="#*15"
                           configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NRO)%>"
                           placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.mensagem.endereco.numero", responsavel)%>"                             
                       />
                      </div>
                     </show:showfield>
                        
                     <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_COMPLEMENTO)%>">
                        <div class="form-group col-sm-3">
                         <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_COMPLEMENTO)%>"><hl:message key="rotulo.endereco.complemento" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_COMPLEMENTO)%>"/></label>
                         <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_COMPLEMENTO)%>"
                             di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_COMPLEMENTO)%>"
                             type="text"
                             classe="form-control"
                             value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_COMPLEMENTO)))%>"
                             mask="#*40"
                             configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_COMPLEMENTO)%>"
                             placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.mensagem.endereco.complemento", responsavel)%>"                             
                         />
                       </div>
                      </show:showfield>
                      
                      <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BAIRRO)%>">
                        <div class="form-group col-sm-3">
                         <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BAIRRO)%>"><hl:message key="rotulo.endereco.bairro" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BAIRRO)%>"/></label>
                         <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BAIRRO)%>"
                             di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BAIRRO)%>"
                             type="text"
                             classe="form-control"
                             value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BAIRRO)))%>"
                             mask="#*40"
                             configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BAIRRO)%>"
                             placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.mensagem.endereco.bairro", responsavel)%>"                             
                         />
                       </div>
                      </show:showfield>
                    </div>
              
                    <div class="row">
                     <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CIDADE)%>">
                          <div class="form-group col-sm-6">
                           <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CIDADE)%>"><hl:message key="rotulo.endereco.cidade" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CIDADE)%>"/></label>
                           <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CIDADE)%>"
                               di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CIDADE)%>"
                               type="text"
                               classe="form-control"
                               value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CIDADE)))%>"
                               mask="#*40"
                               configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CIDADE)%>"
                               placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.mensagem.endereco.cidade", responsavel)%>"                             
                           />
                          </div>
                        </show:showfield>
                  
                        <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF)%>"> 
                          <div class="form-group col-sm-6">
                           <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF)%>"><hl:message key="rotulo.endereco.estado" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF_NASC)%>"/></label>
                            <hl:campoUFv4 name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF_NASC)%>"
                               classe="form-control"
                               di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF_NASC)%>"
                               rotuloUf="rotulo.estados"
                               placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.uf", responsavel)%>"
                               valorCampo="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF_NASC)))%>"
                             />                  
                           </div>
                        </show:showfield>
                      </div>
                      
                    <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CEP)%>">
                     <div class="row">
                      <div class="form-group col-sm-6">
                       <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CEP)%>"><hl:message key="rotulo.endereco.cep" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CEP)%>"/></label>
                       <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CEP)%>"
                           di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CEP)%>"
                           type="text"
                           classe="form-control"
                           value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CEP)))%>"
                           mask="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCepMask())%>"
                           configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CEP)%>"
                           placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.mensagem.endereco.cep", responsavel)%>"                             
                       />
                      </div>
                     </div>
                    </show:showfield>
                    
                   <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TIPO_HABITACAO)%>">
                    <c:if test="${not empty listaTipoHabitacao}">
                      <div class="row">
                       <div class="form-group col-sm-12 col-md-6">
                            <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TIPO_HABITACAO)%>"><hl:message key="rotulo.servidor.tipo.habitacao" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TIPO_HABITACAO)%>"/></label>
                            <hl:htmlcombo
                                 listName="listaTipoHabitacao" 
                                 name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TIPO_HABITACAO)%>" 
                                 fieldValue="<%=TextHelper.forHtmlAttribute(Columns.THA_CODIGO )%>" 
                                 fieldLabel="<%=TextHelper.forHtmlAttribute(Columns.THA_DESCRICAO )%>" 
                                 notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>"
                                 selectedValue="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TIPO_HABITACAO)))%>" 
                                 configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TIPO_HABITACAO)%>"
                                 classe="form-control"
                                 >
                            </hl:htmlcombo>
                       </div>
                      </div>
                     </c:if>
                   </show:showfield>
                    
                    <%
                                            if (ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_LOGRADOURO, responsavel) ||
                                                       ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NRO, responsavel) ||
                                                       ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_COMPLEMENTO, responsavel) ||
                                                       ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BAIRRO, responsavel) ||
                                                       ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CIDADE, responsavel) ||
                                                       ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF, responsavel) ||
                                                       ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CEP, responsavel)) {
                                        %>
                       </fieldset>
                       <%
                           }
                       %>
                    
                    <%
                                            if (ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TELEFONE, responsavel) ||
                                                             ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CELULAR, responsavel) ||
                                                             ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_EMAIL, responsavel)) {
                                        %>
                      <fieldset>
                         <h3 class="legend"><span><hl:message key="rotulo.servidor.subtitulo.dados.contato"/></span></h3>
                      <%
                          }
                      %>
                       
                       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TELEFONE)%>">
                        <div class="row">
                         <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DDD_TELEFONE)%>">
                          <div class="form-group col-sm-2">
                            <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DDD_TELEFONE)%>"><hl:message key="rotulo.servidor.codigo.localidade" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DDD_TELEFONE)%>"/></label>
                            <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DDD_TELEFONE)%>"
                                di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DDD_TELEFONE)%>"
                                type="text"
                                classe="form-control"
                                value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DDD_TELEFONE)))%>"
                                mask="<%=LocaleHelper.getDDDMask()%>"
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DDD_TELEFONE)%>"
                                placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.ddd", responsavel)%>"                             
                            />
                         </div>
                         </show:showfield>
                         <div class="form-group col-sm-6">
                           <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TELEFONE)%>"><hl:message key="rotulo.servidor.telefone" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TELEFONE)%>"/></label>
                           <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TELEFONE)%>"
                               di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TELEFONE)%>"
                               type="text"
                               classe="form-control"
                               value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TELEFONE)))%>"
                               mask="<%=LocaleHelper.getTelefoneMask()%>"
                               configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TELEFONE)%>"
                               placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.telefone", responsavel)%>"                             
                           />
                         </div>
                        </div>
                       </show:showfield>
                       
                       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CELULAR)%>">
                        <div class="row">
                         <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DDD_CELULAR)%>">
                          <div class="form-group col-sm-2">
                            <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DDD_CELULAR)%>"><hl:message key="rotulo.servidor.codigo.localidade" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DDD_CELULAR)%>"/></label>
                            <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DDD_CELULAR)%>"
                                di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DDD_CELULAR)%>"
                                type="text"
                                classe="form-control"
                                value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DDD_CELULAR)))%>"
                                mask="<%=LocaleHelper.getDDDCelularMask()%>"
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DDD_CELULAR)%>"
                                placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.ddd", responsavel)%>"                             
                            />
                         </div>
                         </show:showfield>
                         <div class="form-group col-sm-6">
                           <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CELULAR)%>"><hl:message key="rotulo.servidor.celular" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CELULAR)%>"/></label>
                           <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CELULAR)%>"
                               di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CELULAR)%>"
                               type="text"
                               classe="form-control"
                               value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CELULAR)))%>"
                               mask="<%=LocaleHelper.getCelularMask()%>"
                               configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CELULAR)%>"
                               placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.celular", responsavel)%>"                             
                           />
                         </div>
                        </div>
                       </show:showfield>
                       
                       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_EMAIL)%>">
                        <div class="row">
                         <div class="form-group col-sm-6">
                          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_EMAIL)%>"><hl:message key="rotulo.servidor.email" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_EMAIL)%>"/></label>
                          <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_EMAIL)%>"
                              di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_EMAIL)%>"
                              type="text"
                              classe="form-control"
                              value="<%=TextHelper.forHtmlAttribute(TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_EMAIL))) != null ? TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_FIM_ENGAJAMENTO))) :\"\")%>"
                              mask="#*100"
                              configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_EMAIL)%>"
                              placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.email", responsavel)%>"                             
                          />
                         </div>
                        </div>
                       </show:showfield>
                       
                    <%
                                               if (ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TELEFONE, responsavel) ||
                                                              ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CELULAR, responsavel) ||
                                                              ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_EMAIL, responsavel)) {
                                           %>
                    </fieldset>
                    <%
                        }
                    %>
                    <%
                        
                    %>
                    
                    <%
                                            if (ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MATRICULA, responsavel) ||
                                                            ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ORGAO, responsavel) ||
                                                            ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SUB_ORGAO, responsavel) ||
                                                            ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UNIDADE, responsavel) ||
                                                            ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MUNICIPIO_LOTACAO, responsavel)) {
                                        %>
                            <fieldset>
                             <h3 class="legend"><span><hl:message key="rotulo.servidor.subtitulo.lotacao.v4"/></span></h3>
                       <%
                           }
                       %>
                       
                    <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MATRICULA)%>">
                       <div class="row">
                        <div class="form-group col-sm-12 col-md-6">
                            <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MATRICULA)%>"><hl:message key="rotulo.servidor.matricula" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MATRICULA)%>"/></label>
                            <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MATRICULA)%>"
                                      di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MATRICULA)%>"
                                      type="text"
                                      classe="form-control"
                                      value="<%=TextHelper.isNull(rse_matricula) ? TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MATRICULA))) : rse_matricula%>"
                                      mask="<%=TextHelper.forHtmlAttribute(LoginHelper.getMascaraMatriculaServidor())%>"
                                      configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MATRICULA)%>"
                                      placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.matricula", responsavel)%>"                             
                            />
                        </div>
                       </div>
                     </show:showfield>
                     
                    <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ORGAO)%>">
                      <c:if test="${not empty lstOrgao}">
                       <div class="row">
                        <div class="form-group col-sm-12 col-md-6">
                         <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ORGAO)%>"><hl:message key="rotulo.servidor.orgao" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ORGAO)%>"/></label>
                         <hl:htmlcombo
                             listName="lstOrgao" 
                             name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ORGAO)%>" 
                             fieldValue="<%=TextHelper.forHtmlAttribute(Columns.ORG_CODIGO)%>" 
                             fieldLabel="<%=(String)(Columns.ORG_NOME + ";" + Columns.ORG_IDENTIFICADOR)%>" 
                             notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>"
                             selectedValue="<%=TextHelper.isNull(org_codigo) ? TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ORGAO))) : org_codigo%>"
                             configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ORGAO)%>"
                             classe="form-control"
                             >
                        </hl:htmlcombo> 
                       </div>
                      </div>
                      </c:if>
                     </show:showfield>
                     
                   <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MUNICIPIO_LOTACAO)%>">
                    <div class="row">
                     <div class="form-group col-sm-6">
                      <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MUNICIPIO_LOTACAO)%>">
                        <hl:message key="rotulo.servidor.municipioLotacao" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MUNICIPIO_LOTACAO)%>"/></label>
                        <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MUNICIPIO_LOTACAO)%>"
                          di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MUNICIPIO_LOTACAO)%>"
                          type="text"
                          classe="form-control"
                          value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MUNICIPIO_LOTACAO)))%>"
                          mask="#*40"
                          configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MUNICIPIO_LOTACAO)%>"
                          placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.municipio.lotacao", responsavel)%>"                             
                        />
                     </div>
                    </div>
                   </show:showfield>
                   
              <%
                                     if (ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MATRICULA, responsavel) ||
                                               ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MATRICULA_INSTITUCIONAL, responsavel) ||
                                               ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ORGAO, responsavel) ||
                                               ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SUB_ORGAO, responsavel) ||
                                               ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UNIDADE, responsavel) ||
                                               ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MUNICIPIO_LOTACAO, responsavel)) {
                                 %>
                    </fieldset>
               <%
                   }
               %>

                 <%
                     if (ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SITUACAO, responsavel) ||
                                 ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CATEGORIA, responsavel) ||
                                 ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CARGO, responsavel) ||
                                 ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TIPO_REG_SERVIDOR, responsavel) ||
                                 ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_POSTO, responsavel) ||
                                 ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTABILIZADO, responsavel) ||
                                 ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_FIM_ENGAJAMENTO, responsavel) ||
                                 ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_LIMITE_PERMANENCIA, responsavel) ||
                                 ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CAPACIDADE_CIVIL, responsavel)  ||
                                 ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_VINCULO, responsavel) ||
                                 ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PADRAO, responsavel) ||
                                 ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CLT, responsavel) ||
                                 ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_ADMISSAO, responsavel) ||
                                 ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRAZO, responsavel)) {
                 %>
                      <fieldset>
                       <h3 class="legend"><span><hl:message key="rotulo.servidor.subtitulo.contrato.trabalho"/></span></h3>
                 <%
                     }
                 %> 
                 
                 <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SITUACAO)%>">
                  <c:if test="${not empty listaStatusRegistroServidor}">
                  <div class="row">
                   <div class="form-group col-sm-12 col-md-6">
                    <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SITUACAO)%>"><hl:message key="rotulo.servidor.status" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SITUACAO)%>"/></label>
                    <hl:htmlcombo
                        listName="listaStatusRegistroServidor" 
                        name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SITUACAO)%>" 
                        fieldValue="<%=TextHelper.forHtmlAttribute(Columns.SRS_CODIGO)%>" 
                        fieldLabel="<%=TextHelper.forHtmlAttribute(Columns.SRS_DESCRICAO)%>" 
                        notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>"
                        selectedValue="<%=TextHelper.forHtmlAttribute( request.getAttribute("srsCodigoPadrao") )%>" 
                        configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SITUACAO)%>"
                        classe="form-control">
                   </hl:htmlcombo> 
                   </div>
                  </div>
                 </c:if>
                </show:showfield>
                
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CATEGORIA)%>">     
                  <div class="row">
                   <div class="form-group col-sm-12 col-md-6">
                    <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CATEGORIA)%>"><hl:message key="rotulo.servidor.categoria" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CATEGORIA)%>"/></label>
                    <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CATEGORIA)%>"
                        di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CATEGORIA)%>"
                        type="text"
                        classe="form-control"
                        value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CATEGORIA)))%>"
                        mask="#*255"
                        configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CATEGORIA)%>"
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.categoria", responsavel)%>"
                    />
                   </div>
                  </div>
                 </show:showfield> 
                 
                 <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CARGO)%>">
                  <c:if test="${not empty listaCargo}">
                   <div class="row">
                     <div class="form-group col-sm-12 col-md-6">
                      <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CARGO)%>"><hl:message key="rotulo.servidor.cargo" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CARGO)%>"/></label>
                       <hl:htmlcombo
                         listName="listaCargo" 
                         name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CARGO)%>" 
                         fieldValue="<%=TextHelper.forHtmlAttribute(Columns.CRS_CODIGO)%>" 
                         fieldLabel="<%=(String)(Columns.CRS_IDENTIFICADOR + ";" + Columns.CRS_DESCRICAO)%>" 
                         notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>"
                         selectedValue="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CARGO)))%>" 
                         configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CARGO)%>"
                         classe="form-control"
                       >
                       </hl:htmlcombo>
                      </div>
                    </div>
                  </c:if>
                 </show:showfield>
                   
                 <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TIPO_REG_SERVIDOR)%>">
                   <c:if test="${not empty listaTipoRegServidor}">
                     <div class="row">
                      <div class="form-group col-sm-12 col-md-6">
                        <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TIPO_REG_SERVIDOR)%>"><hl:message key="rotulo.servidor.tipo" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TIPO_REG_SERVIDOR)%>"/></label>
                        <hl:htmlcombo
                           listName="listaTipoRegServidor" 
                           name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TIPO_REG_SERVIDOR)%>"
                           di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TIPO_REG_SERVIDOR)%>" 
                           fieldValue="<%=TextHelper.forHtmlAttribute(Columns.TRS_CODIGO)%>" 
                           fieldLabel="<%=TextHelper.forHtmlAttribute(Columns.TRS_DESCRICAO)%>" 
                           notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>"
                           selectedValue="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TIPO_REG_SERVIDOR)))%>" 
                           configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TIPO_REG_SERVIDOR)%>"
                           classe="form-control"
                        >
                        </hl:htmlcombo>
                       </div>
                      </div>
                    </c:if>
                 </show:showfield>   
                 
                 <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_POSTO)%>">
                   <c:if test="${not empty listaPosto}">
                     <div class="row">
                      <div class="form-group col-sm-12 col-md-6">
                        <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_POSTO)%>"><hl:message key="rotulo.servidor.posto" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_POSTO)%>"/></label>
                        <hl:htmlcombo
                           listName="listaPosto" 
                           name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_POSTO)%>"
                           di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_POSTO)%>" 
                           fieldValue="<%=TextHelper.forHtmlAttribute(Columns.POS_CODIGO)%>" 
                           fieldLabel="<%=TextHelper.forHtmlAttribute(Columns.POS_DESCRICAO)%>" 
                           notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>"
                           selectedValue="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_POSTO)))%>" 
                           configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_POSTO)%>"
                           classe="form-control"
                         >
                         </hl:htmlcombo>
                        </div>
                      </div>
                    </c:if>
                   </show:showfield> 
                   
                   <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTABILIZADO)%>">
                    <div class="row">
                     <div class=" col-md-6 form-check mt-2" role="radiogroup" aria-labelledby="estabilizado">
                       <div class="form-group my-0">
                         <span id="estabilizado"><hl:message key="rotulo.servidor.estabilizado" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTABILIZADO)%>"/></span>
                       </div>
                       <div class="form-check mt-2">
                         <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTABILIZADO)%>"
                            di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTABILIZADO) + "_S"%>"
                            type="radio"
                            value="S"
                            checked="<%=String.valueOf(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTABILIZADO)).equalsIgnoreCase(\"S\"))%>"
                            mask="#*10"
                            configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTABILIZADO)%>"
                            onChange="<%="estabilidade();"%>"
                            classe="form-check-input ml-1"
                          />
                         <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.sim"/>' for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTABILIZADO) + "_S"%>"><hl:message key="rotulo.sim"/></label>
                         <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTABILIZADO)%>"
                            di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTABILIZADO) + "_N"%>"
                            type="radio"
                            value="N"
                            checked="<%=String.valueOf(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTABILIZADO)).equalsIgnoreCase(\"N\"))%>"
                            mask="#*10"
                            configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTABILIZADO)%>"
                            onChange="<%="estabilidade();"%>"
                            classe="form-check-input ml-1"
                          />
                         <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.nao"/>' for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTABILIZADO) + "_N"%>"><hl:message key="rotulo.nao"/></label>
                       </div>
                     </div>
                    </div>
                 </show:showfield>

                 <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_FIM_ENGAJAMENTO)%>">
                  <%
                      String rseDataFimEngajamento = null;
                               rseDataFimEngajamento = JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_FIM_ENGAJAMENTO));
                               if (!rseDataFimEngajamento.equals("")) {
                                   rseDataFimEngajamento = DateHelper.reformat(rseDataFimEngajamento, "yyyy-MM-dd", LocaleHelper.getDatePattern());
                               }
                  %>
                    <div class="row">
                     <div class="form-group col-sm-6">
                      <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_FIM_ENGAJAMENTO)%>"><hl:message key="rotulo.servidor.engajado" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_FIM_ENGAJAMENTO)%>"/></label>
                      <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_FIM_ENGAJAMENTO)%>"
                        di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_FIM_ENGAJAMENTO)%>"
                        type="text"
                        classe="form-control"
                        value="<%=TextHelper.forHtmlAttribute(rseDataFimEngajamento)%>"
                        mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                        configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_FIM_ENGAJAMENTO)%>" 
                        placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>"
                      />
                     </div>
                    </div>
                   </show:showfield>
               
                  <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_LIMITE_PERMANENCIA)%>">
                       <%
                           String rseDataLimitePermanencia = null;
                                         rseDataLimitePermanencia = JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_LIMITE_PERMANENCIA));
                                         if (!rseDataLimitePermanencia.equals("")) {
                                             rseDataLimitePermanencia = DateHelper.reformat(rseDataLimitePermanencia, "yyyy-MM-dd", LocaleHelper.getDatePattern());
                                         }
                       %>
                      <div class="row">
                       <div class="form-group col-sm-6">
                        <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_LIMITE_PERMANENCIA)%>"><hl:message key="rotulo.servidor.dataLimitePermanencia" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_LIMITE_PERMANENCIA)%>"/></label>
                        <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_LIMITE_PERMANENCIA)%>"
                          di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_LIMITE_PERMANENCIA)%>"
                          type="text"
                          classe="form-control"
                          value="<%=TextHelper.forHtmlAttribute(rseDataLimitePermanencia)%>"
                          mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                          configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_LIMITE_PERMANENCIA)%>" 
                          placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>"
                        />
                       </div>
                      </div>
                     </show:showfield>
                     
                      <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CAPACIDADE_CIVIL)%>">
                        <c:if test="${not empty listaCapCivil}">
                         <div class="row">
                          <div class="form-group col-sm-12 col-md-6">
                            <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CAPACIDADE_CIVIL)%>"><hl:message key="rotulo.servidor.capacidadeCivil" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CAPACIDADE_CIVIL)%>"/></label>
                            <hl:htmlcombo
                               listName="listaCapCivil" 
                               name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CAPACIDADE_CIVIL)%>"
                               di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CAPACIDADE_CIVIL)%>" 
                               fieldValue="<%=TextHelper.forHtmlAttribute(Columns.CAP_CODIGO)%>" 
                               fieldLabel="<%=TextHelper.forHtmlAttribute(Columns.CAP_DESCRICAO)%>" 
                               notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>"
                               selectedValue="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CAPACIDADE_CIVIL)))%>" 
                               configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CAPACIDADE_CIVIL)%>"
                               classe="form-control"
                            >
                            </hl:htmlcombo>
                           </div>
                         </div>
                        </c:if>
                     </show:showfield>
                     
                     <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_VINCULO)%>">
                      <c:if test="${not empty listaVincRegSer}">                  
                        <div class="row">
                         <div class="form-group col-sm-12 col-md-6">
                          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_VINCULO)%>"><hl:message key="rotulo.servidor.vinculo" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_VINCULO)%>"/></label>
                          <hl:htmlcombo
                             listName="listaVincRegSer" 
                             name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_VINCULO)%>"
                             di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_VINCULO)%>" 
                             fieldValue="<%=TextHelper.forHtmlAttribute(Columns.VRS_CODIGO)%>" 
                             fieldLabel="<%=TextHelper.forHtmlAttribute(Columns.VRS_DESCRICAO)%>" 
                             notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>"
                             selectedValue="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_VINCULO)))%>" 
                             configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_VINCULO)%>"
                             classe="form-control"
                          >
                          </hl:htmlcombo>
                         </div>
                       </div>
                     </c:if>
                   </show:showfield>
                                
                   <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PADRAO)%>">
                    <c:if test="${not empty listaPadraoRegSer}">      
                     <div class="row">
                      <div class="form-group col-sm-12 col-md-6">
                       <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PADRAO)%>"><hl:message key="rotulo.servidor.padrao" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PADRAO)%>"/></label>
                        <hl:htmlcombo
                           listName="listaPadraoRegSer" 
                           name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PADRAO)%>"
                           di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PADRAO)%>" 
                           fieldValue="<%=TextHelper.forHtmlAttribute(Columns.PRS_CODIGO)%>" 
                           fieldLabel="<%=(String)(Columns.PRS_IDENTIFICADOR + ";" + Columns.PRS_DESCRICAO)%>" 
                           notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>"
                           selectedValue="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PADRAO)))%>" 
                           configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PADRAO)%>"
                           classe="form-control"
                        >
                        </hl:htmlcombo>
                      </div>
                     </div>
                   </c:if>  
                 </show:showfield>          
                 
                 <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CLT)%>">
                   <div class="row">
                    <div class=" col-md-6 form-check mt-2" role="radiogroup" aria-labelledby="clt">
                      <div class="form-group my-0">
                        <span id="clt"><hl:message key="rotulo.servidor.clt" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CLT)%>"/></span>
                      </div>
                      <div class="form-check mt-2">
                        <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CLT)%>"
                           di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CLT) + "_S"%>"
                           type="radio"
                           value="S"
                           checked="<%=String.valueOf(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CLT)).equalsIgnoreCase(\"S\"))%>"
                           mask="#*10"
                           nf="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_ADMISSAO)%>"
                           configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CLT)%>"
                           classe="form-check-input ml-1"
                         />
                        <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.sim"/>' for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CLT) + "_S"%>"><hl:message key="rotulo.sim"/></label>
                        <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CLT)%>"
                           di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CLT) + "_N"%>"
                           type="radio"
                           value="N"
                           checked="<%=String.valueOf(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CLT)).equalsIgnoreCase(\"N\"))%>"
                           mask="#*10"
                           nf="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_ADMISSAO)%>"
                           configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CLT)%>"
                           classe="form-check-input ml-1"
                         />
                        <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.nao"/>' for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CLT) + "_N"%>"><hl:message key="rotulo.nao"/></label>
                      </div>
                    </div>
                   </div>
                 </show:showfield>                      
                 
                     <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_ADMISSAO)%>">
                      <%
                          String rseDataAdmissao = null;
                                      rseDataAdmissao = JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_ADMISSAO));
                                      if (!rseDataAdmissao.equals("")) {
                                         rseDataAdmissao = DateHelper.reformat(rseDataAdmissao, "yyyy-MM-dd", LocaleHelper.getDatePattern());
                                      }
                      %>
                        <div class="row">
                         <div class="form-group col-sm-6">
                          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_ADMISSAO)%>"><hl:message key="rotulo.servidor.dataAdmissao" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_ADMISSAO)%>"/></label>
                          <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_ADMISSAO)%>"
                            di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_ADMISSAO)%>"
                            type="text"
                            classe="form-control"
                            value="<%=TextHelper.forHtmlAttribute(rseDataAdmissao)%>"
                            mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                            configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_ADMISSAO)%>"
                            placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>"                             
                           />
                         </div>
                        </div>
                       </show:showfield> 
                      
                       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRAZO)%>">
                        <div class="row">
                         <div class="form-group col-sm-6">
                          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRAZO)%>"><hl:message key="rotulo.servidor.prazo" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRAZO)%>"/></label>
                          <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRAZO)%>"
                            di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRAZO)%>"
                            type="text"
                            classe="form-control"
                            value="<%=TextHelper.forHtml(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRAZO)))%>"
                            mask="#D11"
                            configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRAZO)%>"
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.prazo", responsavel)%>"                             
                          />
                         </div>
                        </div>
                       </show:showfield>
                      
                       <%
                                                 if (ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SITUACAO, responsavel) ||
                                                             ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CATEGORIA, responsavel) ||
                                                             ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CARGO, responsavel) ||
                                                             ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TIPO_REG_SERVIDOR, responsavel) ||
                                                             ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_POSTO, responsavel) ||
                                                             ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTABILIZADO, responsavel) ||
                                                             ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_FIM_ENGAJAMENTO, responsavel) ||
                                                             ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_LIMITE_PERMANENCIA, responsavel) ||
                                                             ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CAPACIDADE_CIVIL, responsavel)  ||
                                                             ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_VINCULO, responsavel) ||
                                                             ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PADRAO, responsavel) ||
                                                             ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CLT, responsavel) ||
                                                             ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_ADMISSAO, responsavel) ||
                                                             ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRAZO, responsavel)) {
                                             %>
                      </fieldset>
                 <%
                     }
                 %> 
                 
                 <%
                                       if (ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO, responsavel) ||
                                                ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_AGENCIA, responsavel) ||
                                                ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CONTA, responsavel) ||
                                                ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO_ALTERNATIVO, responsavel) ||
                                                ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_AGENCIA_ALTERNATIVA, responsavel) ||
                                                ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CONTA_ALTERNATIVA, responsavel))
                                                {
                                   %>
                      <fieldset>
                       <h3 class="legend"><span><hl:message key="rotulo.servidor.subtitulo.informacoes.financeiras"/></span></h3>        
                  <%
                              }
                          %>
                      
                   <div class="row">
                     <div class="form-group col-sm-12">
                       <div class="row">  
                        <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO)%>">
                           <div class="col-sm-5 col-md-4">
                            <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO)%>"><hl:message key="rotulo.servidor.codigo.banco" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO)%>"/></label>
                            <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO)%>"
                                di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO)%>"
                                type="text" 
                                value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO)))%>" 
                                classe="form-control" 
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO)%>"
                                onFocus="SetarEventoMascara(this,'#A8',true);" 
                                onBlur="<%=TextHelper.forJavaScript( "fout(this);ValidaMascara(this);if (!IsNulo(document.forms[0]." + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO + ")) {SelecionaComboBanco(document.forms[0].RSE_BANCOS, document.forms[0]." + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO + ".value, arrayBancos);}" )%>"
                                placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.codigo.banco", responsavel)%>"
                             />
                           </div>
                          <div class="col-sm-7 col-md-6">
                           <label for="RSE_BANCOS"><hl:message key="rotulo.servidor.banco" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO)%>"/></label>
                           <SELECT NAME="RSE_BANCOS" ID="RSE_BANCOS" CLASS="form-control"
                                   onChange="document.forms[0].<%=TextHelper.forJavaScript( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO)%>.value = document.forms[0].RSE_BANCOS.value;"
                                  <%if (ShowFieldHelper.isDisabled(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO, responsavel)) {%> DISABLED <%}%>>
                             <OPTION VALUE="" SELECTED><hl:message key="rotulo.campo.selecione"/></OPTION>
                           </SELECT>
                      </div>       
                     </show:showfield>
                    </div>
                  </div>
                     <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_AGENCIA)%>">
                       <div class="form-group col-sm-5 col-md-4">
                        <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_AGENCIA)%>"><hl:message key="rotulo.servidor.codigo.agencia" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_AGENCIA)%>"/></label>
                        <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_AGENCIA)%>"
                            di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_AGENCIA)%>"
                            type="text"
                            classe="form-control"
                            value='<%=TextHelper.forHtml(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_AGENCIA)))%>'
                            mask="#*30"
                            configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_AGENCIA)%>"
                            onFocus="SetarEventoMascara(this,'#D5',true);" 
                            onBlur="fout(this);ValidaMascara(this);"
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.codigo.agencia", responsavel)%>"
                         />    
                       </div>
                     </show:showfield>
                   
                     <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CONTA)%>">
                       <div class="form-group col-sm-5 col-md-4">
                        <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CONTA)%>"><hl:message key="rotulo.servidor.codigo.conta" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CONTA)%>"/></label>
                        <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CONTA)%>"
                            di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CONTA)%>"
                            type="text"
                            size="10"
                            maxlength="40" 
                            classe="form-control"
                            value='<%=TextHelper.forHtml(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CONTA)))%>'
                            mask="#*40"
                            onFocus="SetarEventoMascara(this,'#*40',true);" 
                            configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CONTA)%>"
                            onBlur="fout(this);ValidaMascara(this);"
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.codigo.conta", responsavel)%>"
                           />
                         </div>
                       </show:showfield> 
                     </div>
               
                <div class="row">  
                  <div class="form-group col-sm-12">
                    <div class="row">  
                     <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO_ALTERNATIVO)%>">
                       <div class="col-sm-5 col-md-4">
                        <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO_ALTERNATIVO)%>"><hl:message key="rotulo.servidor.codigo.banco.alternativo" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO_ALTERNATIVO)%>"/></label>
                        <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO_ALTERNATIVO)%>"
                            di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO_ALTERNATIVO)%>"
                            type="text" 
                            size="8" 
                            maxlength="8" 
                            value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO_ALTERNATIVO)))%>" 
                            classe="form-control" 
                            configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO_ALTERNATIVO)%>"
                            onFocus="SetarEventoMascara(this,'#A8',true);" 
                            onBlur="<%=TextHelper.forJavaScript( "fout(this);ValidaMascara(this);if (!IsNulo(document.forms[0]." + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO_ALTERNATIVO + ")) {SelecionaComboBanco(document.forms[0].RSE_BANCOS_2, document.forms[0]." + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO_ALTERNATIVO + ".value, arrayBancos);}" )%>"
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.banco.alternativo", responsavel)%>"
                         />
                       </div>
                       <div class="col-sm-7 col-md-6">
                         <label for="RSE_BANCOS_2"><hl:message key="rotulo.servidor.banco" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO_ALTERNATIVO)%>"/></label>
                         <SELECT NAME="RSE_BANCOS_2" ID="RSE_BANCOS_2" CLASS="form-control" 
                                 onChange="document.forms[0].<%=TextHelper.forJavaScript(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO_ALTERNATIVO)%>.value = document.forms[0].RSE_BANCOS_2.value;"
                                <%if (ShowFieldHelper.isDisabled(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO_ALTERNATIVO, responsavel)) {%> DISABLED <%}%>>
                           <OPTION VALUE="" SELECTED><hl:message key="rotulo.campo.selecione"/></OPTION>
                         </SELECT>
                       </div>
                     </show:showfield>
                   </div>
                 </div>
                   
                <div class="form-group col-sm-12">
                  <div class="row">
                   <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_AGENCIA_ALTERNATIVA)%>">
                     <div class="col-sm-5 col-md-4">
                      <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_AGENCIA_ALTERNATIVA)%>"><hl:message key="rotulo.servidor.codigo.agencia.alternativo" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_AGENCIA_ALTERNATIVA)%>"/></label>
                      <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_AGENCIA_ALTERNATIVA)%>"
                         di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_AGENCIA_ALTERNATIVA)%>"
                         type="text"
                         classe="form-control"
                         value='<%=TextHelper.forHtml(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_AGENCIA_ALTERNATIVA)))%>'
                         mask="#*30"
                         configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_AGENCIA_ALTERNATIVA)%>"
                         onFocus="SetarEventoMascara(this,'#D5',true);" 
                         onBlur="fout(this);ValidaMascara(this);"
                         placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.codigo.agencia.alternativa", responsavel)%>"
                      />  
                     </div>
                   </show:showfield>
                   
                   <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CONTA_ALTERNATIVA)%>">
                     <div class="col-sm-7 col-md-4">
                      <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CONTA_ALTERNATIVA)%>"><hl:message key="rotulo.servidor.codigo.conta.alternativo" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CONTA_ALTERNATIVA)%>"/></label>
                      <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CONTA_ALTERNATIVA)%>"
                          di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CONTA_ALTERNATIVA)%>"
                          type="text"
                          classe="form-control"
                          value='<%=TextHelper.forHtml(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CONTA_ALTERNATIVA)))%>'
                          mask="#*40"
                          maxlength="40"
                          configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CONTA_ALTERNATIVA)%>"
                          onFocus="SetarEventoMascara(this,'#*40',true);" 
                          onBlur="fout(this);ValidaMascara(this);"
                          placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.codigo.conta.alternativa", responsavel)%>"
                        />
                       </div>
                     </show:showfield>           
                   </div>
                 </div>
                </div>
               <%
                   if (ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO, responsavel) ||
                            ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_AGENCIA, responsavel) ||
                            ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CONTA, responsavel) ||
                            ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO_ALTERNATIVO, responsavel) ||
                            ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_AGENCIA_ALTERNATIVA, responsavel) ||
                            ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CONTA_ALTERNATIVA, responsavel))
                            {
               %>
                      </fieldset>
                  <%
                      }
                  %>
                             
                <%
                                                 if (ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SALARIO, responsavel) ||
                                                           ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PROVENTOS, responsavel) ||
                                                           ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DESCONTOS_COMPULSORIOS, responsavel) ||
                                                           ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DESCONTOS_FACULTATIVOS, responsavel) ||
                                                           ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_OUTROS_DESCONTOS, responsavel) ||
                                                           ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BASE_CALCULO, responsavel) ||
                                                           ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MATRICULA_INSTITUCIONAL, responsavel) ||
                                                           ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_CONTRACHEQUE, responsavel))
                                                          {
                                             %>
                      <fieldset>
                       <h3 class="legend"><span><hl:message key="rotulo.servidor.subtitulo.informacoes.folha.pagamento"/></span></h3>        
                  <%
                              }
                          %>
               
                    <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SALARIO)%>">
                      <div class="row">
                       <div class="form-group col-sm-6">
                        <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SALARIO)%>"><hl:message key="rotulo.servidor.salario" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SALARIO)%>"/></label>
                         <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SALARIO)%>"
                            di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SALARIO)%>"
                            type="text"
                            classe="form-control"
                            value='<%=TextHelper.forHtml(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SALARIO)))%>'
                            mask="#F11"
                            onFocus="SetarEventoMascara(this,'#F11',true);"
                            onBlur="fout(this);ValidaMascara(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                            configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SALARIO)%>"
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.salario", responsavel)%>"                             
                          />
                       </div>
                      </div>
                     </show:showfield>
                     
                     
                     <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PROVENTOS)%>">
                      <div class="row">
                       <div class="form-group col-sm-6">
                        <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PROVENTOS)%>"><hl:message key="rotulo.servidor.proventos" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PROVENTOS)%>"/></label>
                         <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PROVENTOS)%>"
                            di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PROVENTOS)%>"
                            type="text"
                            classe="form-control"
                            value='<%=TextHelper.forHtml(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PROVENTOS)))%>'
                            mask="#F11"
                            onFocus="SetarEventoMascara(this,'#F11',true);"
                            onBlur="fout(this);ValidaMascara(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                            configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PROVENTOS)%>"
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.proventos", responsavel)%>"                             
                          />
                       </div>
                      </div>
                     </show:showfield>
       
                     <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DESCONTOS_COMPULSORIOS)%>">
                      <div class="row">
                       <div class="form-group col-sm-6">
                        <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DESCONTOS_COMPULSORIOS)%>"><hl:message key="rotulo.servidor.descontos.compulsorios" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DESCONTOS_COMPULSORIOS)%>"/></label>
                         <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DESCONTOS_COMPULSORIOS)%>"
                            di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DESCONTOS_COMPULSORIOS)%>"
                            type="text"
                            classe="form-control"
                            onFocus="SetarEventoMascara(this,'#F11',true);"
                            onBlur="fout(this);ValidaMascara(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                            value='<%=TextHelper.forHtml(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DESCONTOS_COMPULSORIOS)))%>'
                            mask="#F11"  
                            configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DESCONTOS_COMPULSORIOS)%>"
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.descontos.compulsorios", responsavel)%>" 
                         />                                                   
                       </div>
                      </div>
                     </show:showfield>
                     
                     <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DESCONTOS_FACULTATIVOS)%>">
                      <div class="row">
                       <div class="form-group col-sm-6">
                        <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DESCONTOS_FACULTATIVOS)%>"><hl:message key="rotulo.servidor.descontos.facultativos" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DESCONTOS_FACULTATIVOS)%>"/></label>
                         <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DESCONTOS_FACULTATIVOS)%>"
                            di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DESCONTOS_FACULTATIVOS)%>"
                            type="text"
                            classe="form-control"
                            onFocus="SetarEventoMascara(this,'#F11',true);"
                            onBlur="fout(this);ValidaMascara(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                            value='<%=TextHelper.forHtml(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DESCONTOS_FACULTATIVOS)))%>'
                            mask="#F11"  
                            configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DESCONTOS_FACULTATIVOS)%>"
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.descontos.facultativos", responsavel)%>"
                          />
                       </div>
                      </div>
                     </show:showfield>
              
                     <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_OUTROS_DESCONTOS)%>">
                      <div class="row">
                       <div class="form-group col-sm-6">
                        <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_OUTROS_DESCONTOS)%>"><hl:message key="rotulo.servidor.outros.descontos" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_OUTROS_DESCONTOS)%>"/></label>
                         <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_OUTROS_DESCONTOS)%>"
                            di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_OUTROS_DESCONTOS)%>"
                            type="text"
                            classe="form-control"
                            onFocus="SetarEventoMascara(this,'#F11',true);"
                            onBlur="fout(this);ValidaMascara(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                            value='<%=TextHelper.forHtml(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_OUTROS_DESCONTOS)))%>'
                            mask="#F11"  
                            configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_OUTROS_DESCONTOS)%>"
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.outros.descontos", responsavel)%>"
                         />
                       </div>
                      </div>
                     </show:showfield>
                     
                     <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BASE_CALCULO)%>">
                      <div class="row">
                       <div class="form-group col-sm-6">
                        <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BASE_CALCULO)%>"><hl:message key="rotulo.servidor.base.calculo" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BASE_CALCULO)%>"/></label>
                        <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BASE_CALCULO)%>"
                            di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BASE_CALCULO)%>"
                            type="text"
                            classe="form-control"
                            onFocus="SetarEventoMascara(this,'#F11',true);"
                            onBlur="fout(this);ValidaMascara(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                            value='<%=TextHelper.forHtml(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BASE_CALCULO)))%>'
                            mask="#F11"  
                            configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BASE_CALCULO)%>"
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.base.calculo", responsavel)%>"
                        />
                       </div>
                      </div>       
                     </show:showfield>
                     
                     <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ASSOCIADO)%>">
                       <div class="row">
                        <div class="col-sm-12 col-md-6 mb-2">
                          <div class="form-group my-0">
                            <span id="associado"><hl:message key="rotulo.servidor.associado" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ASSOCIADO)%>"/></span>
                          </div>
                          <div class="form-check mt-2" role="radiogroup" aria-labelledby="associado">
                            <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ASSOCIADO)%>"
                               di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ASSOCIADO) + "_S"%>"
                               type="radio"
                               value="S"
                               checked="<%=String.valueOf(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ASSOCIADO)).equalsIgnoreCase(\"S\"))%>"
                               mask="#*10"
                               configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ASSOCIADO)%>"
                               classe="form-check-input ml-1"
                             />
                            <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.sim"/>' for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ASSOCIADO) + "_S"%>"><hl:message key="rotulo.sim"/></label>
                            <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ASSOCIADO)%>"
                               di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ASSOCIADO) + "_N"%>"
                               type="radio"
                               value="N"
                               checked="<%=String.valueOf(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ASSOCIADO)).equalsIgnoreCase(\"N\"))%>"
                               mask="#*10"
                               configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ASSOCIADO)%>"
                               classe="form-check-input ml-1"
                             />
                            <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.nao"/>' for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ASSOCIADO) + "_N"%>"><hl:message key="rotulo.nao"/></label>
                          </div>
                        </div>
                       </div>
                     </show:showfield>
                     
                     <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MATRICULA_INSTITUCIONAL)%>">
                       <div class="row">
                        <div class="form-group col-sm-6">
                            <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MATRICULA_INSTITUCIONAL)%>"><hl:message key="rotulo.servidor.matricula.institucional" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MATRICULA_INSTITUCIONAL)%>"/></label>
                            <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MATRICULA_INSTITUCIONAL)%>"
                                      di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MATRICULA_INSTITUCIONAL)%>"
                                      type="text"
                                      classe="form-control"
                                      value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MATRICULA_INSTITUCIONAL)))%>"
                                      mask="<%=TextHelper.forHtmlAttribute("#*20")%>"
                                      configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MATRICULA_INSTITUCIONAL)%>"
                                      placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.matricula.institucional", responsavel)%>"                             
                            />
                        </div>
                       </div>
                     </show:showfield>
                     
                      <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_CONTRACHEQUE)%>">
                        <div class="row">
                         <div class="form-group col-sm-6">
                          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_CONTRACHEQUE)%>"><hl:message key="rotulo.servidor.data.contracheque" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_CONTRACHEQUE)%>"/></label>
                          <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_CONTRACHEQUE)%>"
                              di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_CONTRACHEQUE)%>"
                              type="text"
                              classe="form-control"
                              value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_CONTRACHEQUE))%>"
                              mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                              configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_CONTRACHEQUE)%>"
                              placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>"
                           />
                         </div>
                        </div>
                     </show:showfield>
                    
                      <%
                                              if (ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SALARIO, responsavel) ||
                                                              ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PROVENTOS, responsavel) ||
                                                              ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DESCONTOS_COMPULSORIOS, responsavel) ||
                                                              ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DESCONTOS_FACULTATIVOS, responsavel) ||
                                                              ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_OUTROS_DESCONTOS, responsavel) ||
                                                              ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BASE_CALCULO, responsavel) ||
                                                              ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MATRICULA_INSTITUCIONAL, responsavel) ||
                                                              ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_CONTRACHEQUE, responsavel))
                                                             {
                                          %>
                            </fieldset>
                        <%
                            }
                        %>
                        
                    <%
                                                if (ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRACA, responsavel) ||
                                                             ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_OBSERVACAO, responsavel)) {
                                            %>
                      <fieldset>
                       <h3 class="legend"><span><hl:message key="rotulo.servidor.subtitulo.informacoes.adicionais"/></span></h3>
                    <%
                        }
                    %>
                    
                    <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRACA)%>">    
                      <div class="row">
                       <div class="form-group col-sm-12">
                        <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRACA)%>"><hl:message key="rotulo.servidor.praca" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRACA)%>"/></label>
                           <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRACA)%>"
                               di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRACA)%>"
                               type="textarea"
                               classe="form-control"
                               value='<%=TextHelper.forHtml(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRACA)))%>'
                               configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRACA)%>"
                               rows="6"
                               placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.praca", responsavel)%>"                             
                           />
                        </div>
                      </div>
                   </show:showfield>
                           
                   <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_OBSERVACAO)%>">    
                    <div class="row">
                     <div class="form-group col-sm-12">
                      <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_OBSERVACAO)%>"><hl:message key="rotulo.servidor.obs" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_OBSERVACAO)%>"/></label>
                      <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_OBSERVACAO)%>"
                          di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_OBSERVACAO)%>"
                          type="textarea"
                          classe="form-control"
                          value="<%=TextHelper.forHtml(JspHelper.verificaVarQryStr(request, TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_OBSERVACAO)))%>"
                          configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_OBSERVACAO)%>" 
                          rows="6"
                          placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.obs", responsavel)%>"
                      />
                     </div>
                    </div>
                 </show:showfield>
                 <% if (ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRACA, responsavel) ||
                         ShowFieldHelper.showField(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_OBSERVACAO, responsavel)) { %>
                      </fieldset>
                <% } %>
          </form>
      </div>
  </div>
  <div class="btn-action">
    <a href="#no-back" id="btnCancelar" class="btn btn-outline-danger"
      onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
    <a href="#no-back" class="btn btn-primary" id="btnSalvar" onClick="if(vf_edita_servidor()){f0.submit();} return false;"><hl:message key="rotulo.botao.salvar"/></a>
  </div>
</c:set>

<c:set var="javascript">
<script type="text/JavaScript">
var f0 = document.forms[0];

var arrayBancos = <%=(String)JspHelper.geraArrayBancos(responsavel)%>;

function formLoad() {
	if (document.forms[0].RSE_BANCOS != null) {
       AtualizaFiltraComboExt(document.forms[0].RSE_BANCOS, arrayBancos, '', '', '', false, false, '', '');
     }
     if (document.forms[0].RSE_BANCOS_2 != null) {
       AtualizaFiltraComboExt(document.forms[0].RSE_BANCOS_2, arrayBancos, '', '', '', false, false, '', '');
     }
  focusFirstField();
  estabilidade();
}
window.onload = formLoad;

function estabilidade() {
 var estabilizado = getCheckedRadio('form1', '<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTABILIZADO)%>');
 var dataFimEngajamento = document.forms[0].<%=TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_FIM_ENGAJAMENTO)%>;
 var dataLimitePermanencia = document.forms[0].<%=TextHelper.forHtmlAttribute( FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_LIMITE_PERMANENCIA)%>;
 
 if (estabilizado == null || estabilizado == 'undefined') {
  return;
 }
 
 if (estabilizado == 'S') {
  dataFimEngajamento.value = '';
  dataFimEngajamento.disabled = true;
  dataLimitePermanencia.value = '';
  dataLimitePermanencia.disabled = true;
 } else if (estabilizado == 'N') {
  dataFimEngajamento.disabled = false;
  dataLimitePermanencia.disabled = false;
 }
}

//Verifica formularios de cadastro de servidores
function vf_edita_servidor() {
	<%
    String ctr = "'" + request.getAttribute("listaCampos").toString().replaceAll("\\|", "', '") + "'";
    String msg = "'" + request.getAttribute("listaMensagens").toString().replaceAll("\\|", "', '") + "'";
    %>
    
  	var Controles = new Array(<%=ctr%>);
  	var Msgs = new Array(<%=msg%>);
	
  	var estabilizado = getCheckedRadio('form1', '<%=TextHelper.forHtmlAttribute(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTABILIZADO)%>');
  	if (estabilizado == 'S') {
  		// remove obrigatoriedade da data fim engajamento
  		var indexCtr1 = Controles.indexOf('<%=FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_FIM_ENGAJAMENTO%>');
  		var indexMsg1 = Msgs.indexOf('<%=ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.engajado", responsavel)%>');
  		
  		if (indexCtr1 > -1) {
  			Controles.splice(indexCtr1, 1);
  		}
  		if (indexMsg1 > -1) {
  			Msgs.splice(indexMsg1, 1);
  		}
		// remove obrigatoriedade da data fim engajamento
  		var indexCtr2 = Controles.indexOf('<%=FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_LIMITE_PERMANENCIA%>');
  		var indexMsg2 = Msgs.indexOf('<%=ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.data.limite.permanencia", responsavel)%>');

  		if (indexCtr2 > -1) {
  			Controles.splice(indexCtr2, 1);
  		}
  		if (indexMsg2 > -1) {
  			Msgs.splice(indexMsg2, 1);
  		}
  	}

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
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
