<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist"%>
<%@ page import="com.zetra.econsig.values.CodedValues"%>
<%@ page import="com.zetra.econsig.values.Columns"%>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper"%>
<%@ page import="java.util.Arrays"%>
<%@ page import="java.util.List"%>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ page import="com.zetra.econsig.dto.entidade.ServidorTransferObject"%>
<%@ page import="com.zetra.econsig.helper.web.v3.JspHelper"%>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper"%>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<%
AcessoSistema _responsavel = JspHelper.getAcessoSistema(request);
ServidorTransferObject _servidor = (ServidorTransferObject) request.getAttribute("servidor");
boolean _readOnly = (request.getAttribute("readOnly") != null && request.getAttribute("readOnly").equals("true"));
boolean _podeAlterarEmail = !(_servidor != null && _servidor.getSerPermiteAlterarEmail().equals("N"));
String _rseCodigo_ = (String) request.getAttribute("rseCodigo");
int contadorSubTitulo = (request.getAttribute("contadorSubTitulo") != null ? (Integer) request.getAttribute("contadorSubTitulo") : 1);

String emailContratosRejeitados = (String) request.getAttribute(FieldKeysConstants.EDT_SERVIDOR_EMAIL_CONTRATOS_REJEITADOS);

%>

      <% if (ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_NOME, _responsavel) ||
             ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_PRIMEIRO_NOME, _responsavel) ||
             ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_NOME_PAI, _responsavel) ||
             ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_NOME_MAE, _responsavel) ||
             ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_NOME_CONJUGE, _responsavel) ||
             ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_DATA_NASCIMENTO, _responsavel) ||
             ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_UF_NASCIMENTO, _responsavel) ||
             ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_CIDADE_NASCIMENTO, _responsavel) ||
             ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_NACIONALIDADE, _responsavel) ||
             ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_ESTADO_CIVIL, _responsavel) ||
             ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_SEXO, _responsavel) ||
             ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_DEFICIENTE_VISUAL, _responsavel) ||
             ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_ACESSA_HOST_A_HOST, _responsavel)||
             ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_QTD_FILHOS,_responsavel)||
             ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_NIVEL_ESCOLARIDADE,_responsavel)
              ) { %>
       <fieldset>
		 <h3 class="legend"><span><hl:message key="rotulo.servidor.subtitulo.informacoes.gerais"/></span></h3>
      <% } %>
         <div class="row">
           <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_TITULACAO)%>">
             <div class="form-group col-sm-2">
               <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_TITULACAO)%>"><hl:message key="rotulo.servidor.titulacao.abrev" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_TITULACAO)%>"/></label>
               <select name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_TITULACAO)%>" id="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_TITULACAO)%>" class="form-control form-select" <%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%> >
                  <option value="" <%=TextHelper.isNull(_servidor.getSerTitulacao()) ? "SELECTED" : ""%>>--</option>
                  <%
                      String titulacao = ApplicationResourcesHelper.getMessage("rotulo.servidor.titulacao.valores", _responsavel);
                      List<String> titLst = new ArrayList<String>();                           
                      if (!TextHelper.isNull(titulacao)) {
                          titLst = Arrays.asList(titulacao.split(",|;"));
                      }
                      for (String tratamento : titLst) { 
                  %>                          
                         <option value="<%=tratamento%>" <%= (_servidor != null && !TextHelper.isNull(_servidor.getSerTitulacao()) && _servidor.getSerTitulacao().equals(tratamento)) ? "SELECTED" : "" %>><%=tratamento%></OPTION>
                  <%
                      }
                  %>
              </select>
             </div>
           </show:showfield>                  
           <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NOME)%>">
            &nbsp;
             <div class="form-group col-sm-6">
                  <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NOME)%>"><hl:message key="rotulo.servidor.nome" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NOME)%>"/></label>
                  <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NOME)%>"
                            di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NOME)%>"
                            type="text"
                            classe="form-control"
                            value="<%=TextHelper.forHtmlAttribute(_servidor != null && _servidor.getSerNome() != null ? _servidor.getSerNome().toString() : "")%>"
                            mask="#*100"
                            others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"                              
                            configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NOME)%>"
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.nome", _responsavel)%>"                             
                  />
             </div>
           </show:showfield>
           <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_PRIMEIRO_NOME)%>">
             &nbsp;
              <div class="form-group col-sm-3">
                  <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_PRIMEIRO_NOME)%>"><hl:message key="rotulo.servidor.primeiro.nome" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_PRIMEIRO_NOME)%>"/></label>
                  <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_PRIMEIRO_NOME)%>"
                            di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_PRIMEIRO_NOME)%>"
                            type="text"
                            classe="form-control"
                            value="<%=TextHelper.forHtmlAttribute(_servidor != null && _servidor.getSerPrimeiroNome() != null ? _servidor.getSerPrimeiroNome().toString() : "")%>"
                            mask="#*100"
                            others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"                              
                            configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_PRIMEIRO_NOME)%>"
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.primeiro.nome", _responsavel)%>"                             
                  />
              </div>
           </show:showfield>
           <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NOME_MEIO)%>">
             &nbsp;
              <div class="form-group col-sm-3">
                  <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NOME_MEIO)%>"><hl:message key="rotulo.servidor.meio.nome" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NOME_MEIO)%>"/></label>
                  <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NOME_MEIO)%>"
                            di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NOME_MEIO)%>"
                            type="text"
                            classe="form-control"
                            value="<%=TextHelper.forHtmlAttribute(_servidor != null && _servidor.getSerNomeMeio() != null ? _servidor.getSerNomeMeio().toString() : "")%>"
                            mask="#*100"
                            others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"                              
                            configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NOME_MEIO)%>"
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.nome.meio", _responsavel)%>"                             
                  />
              </div>
           </show:showfield>
           <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_ULTIMO_NOME)%>">
             &nbsp;
              <div class="form-group col-sm-3">
                  <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_ULTIMO_NOME)%>"><hl:message key="rotulo.servidor.ultimo.nome" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_ULTIMO_NOME)%>"/></label>
                  <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_ULTIMO_NOME)%>"
                            di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_ULTIMO_NOME)%>"
                            type="text"
                            classe="form-control"
                            value="<%=TextHelper.forHtmlAttribute(_servidor != null && _servidor.getSerUltimoNome() != null ? _servidor.getSerUltimoNome().toString() : "")%>"
                            mask="#*100"
                            others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"                              
                            configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_ULTIMO_NOME)%>"
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.ultimo.nome", _responsavel)%>"                             
                  />
             </div>
           </show:showfield>

           <%
               if (_responsavel.isSup() && _servidor != null && _servidor.getSerDataIdentificacaoPessoal() != null) {
           %>
              <div class="form-group slider col-sm-4 col-md-4 mt-4 mb-2">
                 <div class="tooltip-inner"><hl:message key="mensagem.informacao.pessoal.validado.data" arg0="<%=DateHelper.format(_servidor.getSerDataIdentificacaoPessoal(), LocaleHelper.getDateTimePattern())%>"/></div>
              </div>
           <%
               }
           %> 

         </div>
         
         <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NOME_PAI)%>">
           <div class="row">
            <div class="form-group col-sm-6">
                <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NOME_PAI)%>"><hl:message key="rotulo.servidor.nomePai" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NOME_PAI)%>"/></label>
                <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NOME_PAI)%>"
                          di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NOME_PAI)%>"
                          type="text"
                          classe="form-control"
                          value="<%=TextHelper.forHtmlAttribute(_servidor != null && _servidor.getSerNomePai() != null ? _servidor.getSerNomePai().toString() : "")%>"
                          mask="#*100"
                          others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"                              
                          configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NOME_PAI)%>"
                          placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.nome.pai", _responsavel)%>"                             
                />
            </div>
           </div>
         </show:showfield>
         <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NOME_MAE)%>">
           <div class="row">
            <div class="form-group col-sm-6">
                <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NOME_MAE)%>"><hl:message key="rotulo.servidor.nomeMae" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NOME_MAE)%>"/></label>
                <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NOME_MAE)%>"
                          di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NOME_MAE)%>"
                          type="text"
                          classe="form-control"
                          value="<%=TextHelper.forHtmlAttribute(_servidor != null && _servidor.getSerNomeMae() != null ? _servidor.getSerNomeMae().toString() : "")%>"
                          mask="#*100"
                          others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"                              
                          configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NOME_MAE)%>"
                          placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.nome.mae", _responsavel)%>"                             
                />
            </div>
           </div>
         </show:showfield>
         <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NOME_CONJUGE)%>">
           <div class="row">
            <div class="form-group col-sm-6">
                <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NOME_CONJUGE)%>"><hl:message key="rotulo.servidor.nome.conjuge" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NOME_CONJUGE)%>"/></label>
                <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NOME_CONJUGE)%>"
                          di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NOME_CONJUGE)%>"
                          type="text"
                          classe="form-control"
                          value="<%=TextHelper.forHtmlAttribute(_servidor != null && _servidor.getSerNomeConjuge() != null ? _servidor.getSerNomeConjuge().toString() : "")%>"
                          mask="#*100"
                          others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"                              
                          configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NOME_CONJUGE)%>"
                          placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.nome.conjuge", _responsavel)%>"                             
                />
            </div>
           </div>
         </show:showfield>
         <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_DATA_NASCIMENTO)%>">
          <div class="row">
           <div class="form-group col-sm-6">
                <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_DATA_NASCIMENTO)%>"><hl:message key="rotulo.servidor.dataNasc" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_DATA_NASCIMENTO)%>"/></label>
                <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_DATA_NASCIMENTO)%>"
                          di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_DATA_NASCIMENTO)%>"
                          type="text"
                          classe="form-control"
                          value="<%=TextHelper.forHtmlAttribute(_servidor != null && _servidor.getSerDataNasc() != null ? DateHelper.format(_servidor.getSerDataNasc(), LocaleHelper.getDatePattern()) : "")%>"
                          mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                          others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"                              
                          configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_DATA_NASCIMENTO)%>"
                          placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>"                             
                />
           </div>
          </div>
         </show:showfield>
         <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_UF_NASCIMENTO)%>">
          <div class="row">
           <div class="form-group col-sm-6">
             <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_UF_NASCIMENTO)%>"><hl:message key="rotulo.servidor.estado.nascimento" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_UF_NASCIMENTO)%>"/></label>
             <%=JspHelper.geraComboUF(FieldKeysConstants.EDT_SERVIDOR_UF_NASCIMENTO, (_servidor != null && _servidor.getSerUfNasc() != null ? _servidor.getSerUfNasc() : ""), _readOnly || ShowFieldHelper.isDisabled(FieldKeysConstants.EDT_SERVIDOR_UF_NASCIMENTO, _responsavel), ApplicationResourcesHelper.getMessage("rotulo.estados", _responsavel), _responsavel)%>
           </div>
          </div>
         </show:showfield>
         <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_CIDADE_NASCIMENTO)%>">
          <div class="row">
           <div class="form-group col-sm-6">
             <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_CIDADE_NASCIMENTO)%>"><hl:message key="rotulo.servidor.cidade.nascimento" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_CIDADE_NASCIMENTO)%>"/></label>
             <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_CIDADE_NASCIMENTO)%>"
                       di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_CIDADE_NASCIMENTO)%>"
                       type="text"
                       classe="form-control"
                       value="<%=TextHelper.forHtmlAttribute(_servidor != null && _servidor.getSerCidNasc() != null ? _servidor.getSerCidNasc() : "")%>"
                       mask="#*40"
                       others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"                              
                       configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_CIDADE_NASCIMENTO)%>"
                       placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.cidade.nascimento", _responsavel)%>"                             
             />
           </div>
          </div>
         </show:showfield>
         <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NACIONALIDADE)%>">
          <div class="row">
           <div class="form-group col-sm-6">
             <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NACIONALIDADE)%>"><hl:message key="rotulo.servidor.nacionalidade" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NACIONALIDADE)%>"/></label>
             <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NACIONALIDADE)%>"
                       di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NACIONALIDADE)%>"
                       type="text"
                       classe="form-control"
                       value="<%=TextHelper.forHtmlAttribute(_servidor != null && _servidor.getSerNacionalidade() != null ? _servidor.getSerNacionalidade() : "")%>"
                       mask="#*40"
                       others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"                              
                       configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NACIONALIDADE)%>"
                       placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.nacionalidade", _responsavel)%>"                             
             />
           </div>
          </div>
         </show:showfield>
         <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_ESTADO_CIVIL)%>">
          <div class="row">
           <div class="form-group col-sm-6">
                <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_ESTADO_CIVIL)%>"><hl:message key="rotulo.servidor.estadoCivil" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_ESTADO_CIVIL)%>"/></label>
                <hl:htmlcombo
                     listName="listEstadoCivil" 
                     name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_ESTADO_CIVIL)%>" 
                     fieldValue="<%=TextHelper.forHtmlAttribute(Columns.EST_CIVIL_CODIGO )%>" 
                     fieldLabel="<%=TextHelper.forHtmlAttribute(Columns.EST_CIVIL_DESCRICAO )%>" 
                     notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", _responsavel)%>"
                     selectedValue="<%=TextHelper.forHtmlAttribute(_servidor != null ? _servidor.getSerEstCivil() : "" )%>" 
                     configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_ESTADO_CIVIL)%>"
                     others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"
                     classe="form-control"
                     >
                </hl:htmlcombo>
           </div>
          </div>
         </show:showfield>
         <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_SEXO)%>">
          <div class="row">
           <div class=" col-md-6 form-check mt-2" role="radiogroup" aria-labelledby="sexo">
              <div class="form-group my-0">
                <span id="sexo"><hl:message key="rotulo.servidor.sexo" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_SEXO)%>"/></span>
              </div>
              <div class="form-check form-check-inline mt-2">
                <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_SEXO)%>"
                               di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_SEXO) + "_M"%>"
                               type="radio"
                               value="M"
                               checked="<%=String.valueOf(_servidor.getSerSexo() != null && _servidor.getSerSexo().equalsIgnoreCase(\"M\"))%>"
                               mask="#*10"
                               others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"
                               configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_SEXO)%>"
                               classe="form-check-input ml-1"
                 />
                <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.servidor.sexo.masculino"/>' for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_SEXO) + "_M"%>"><hl:message key="rotulo.servidor.sexo.masculino"/></label>
              </div>
               <div class="form-check-inline form-check">
                  <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_SEXO)%>"
                               di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_SEXO) + "_F"%>"
                               type="radio"
                               value="F"
                               checked="<%=String.valueOf(_servidor.getSerSexo() != null && _servidor.getSerSexo().equalsIgnoreCase(\"F\"))%>"
                               mask="#*10"
                               others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"
                               configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_SEXO)%>"
                               classe="form-check-input ml-1"
                 />
                <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.servidor.sexo.feminino"/>' for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_SEXO) + "_F"%>"><hl:message key="rotulo.servidor.sexo.feminino"/></label>
              </div>
           </div>
          </div>
         </show:showfield>
         
         <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_QTD_FILHOS)%>">
          <div class="row">
           <div class="form-group col-sm-6">
             <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_QTD_FILHOS)%>"><hl:message key="rotulo.servidor.quantidade.filhos" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_QTD_FILHOS)%>"/></label>
             <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_QTD_FILHOS)%>"
                       di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_QTD_FILHOS)%>"
                       type="text"
                       classe="form-control"
                       value="<%=TextHelper.forHtmlAttribute(_servidor != null && _servidor.getSerQtdFilhos() != null ? _servidor.getSerQtdFilhos() : "")%>"
                       mask="#D2"
                       others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"                              
                       configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_QTD_FILHOS)%>"
                       placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.qtd.filhos", _responsavel)%>"                             
             />
           </div>
          </div>
         </show:showfield>
         <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NIVEL_ESCOLARIDADE)%>">
           <%
               if (request.getAttribute("listNivelEscolaridade") != null) {
           %>
            <div class="row">
             <div class="form-group col-sm-6">
                  <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NIVEL_ESCOLARIDADE)%>"><hl:message key="rotulo.servidor.nivel.escolaridade" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NIVEL_ESCOLARIDADE)%>"/></label>
                  <hl:htmlcombo
                       listName="listNivelEscolaridade" 
                       name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NIVEL_ESCOLARIDADE)%>" 
                       fieldValue="<%=TextHelper.forHtmlAttribute(Columns.NES_CODIGO )%>" 
                       fieldLabel="<%=TextHelper.forHtmlAttribute(Columns.NES_DESCRICAO )%>" 
                       notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", _responsavel)%>"
                       selectedValue="<%=TextHelper.forHtmlAttribute(_servidor != null ? _servidor.getNesCodigo() : "" )%>" 
                       configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NIVEL_ESCOLARIDADE)%>"
                       others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"
                       classe="form-control"
                       >
                  </hl:htmlcombo>
             </div>
            </div>
          <%
              }
          %>
         </show:showfield>
         <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_DEFICIENTE_VISUAL)%>">
          <%
              if (_responsavel.isCseSupOrg()) {
          %>
                <div class="row">
                 <div class=" col-md-6 form-check mt-2" role="radiogroup" aria-labelledby="deficienteVisual">
                   <div class="form-group my-0">
                     <span id="deficienteVisual"><hl:message key="rotulo.servidor.deficiente.visual" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_DEFICIENTE_VISUAL)%>"/></span>
                   </div>
                   <div class="form-check form-check-inline mt-2">
                     <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_DEFICIENTE_VISUAL)%>"
                                    di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_DEFICIENTE_VISUAL) + "_S"%>"
                                    type="radio"
                                    value="S"
                                    checked="<%=TextHelper.forHtmlAttribute(String.valueOf(_servidor.getSerDeficienteVisual() != null && _servidor.getSerDeficienteVisual().equalsIgnoreCase(CodedValues.TPC_SIM)))%>"
                                    mask="#*10"
                                    others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"
                                    configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_DEFICIENTE_VISUAL)%>"
                                    classe="form-check-input ml-1"
                      />
                     <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.sim"/>' for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_DEFICIENTE_VISUAL) + "_S"%>"><hl:message key="rotulo.sim"/></label>
                     </div>
                     <div class="form-check form-check-inline mt-2">
                     <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_DEFICIENTE_VISUAL)%>"
                                    di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_DEFICIENTE_VISUAL) + "_N"%>"
                                    type="radio"
                                    value="N"
                                    checked="<%=TextHelper.forHtmlAttribute(String.valueOf(_servidor.getSerDeficienteVisual() != null && _servidor.getSerDeficienteVisual().equalsIgnoreCase(CodedValues.TPC_NAO)))%>"
                                    mask="#*10"
                                    others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"
                                    configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_DEFICIENTE_VISUAL)%>"
                                    classe="form-check-input ml-1"
                      />
                     <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.nao"/>' for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_DEFICIENTE_VISUAL) + "_N"%>"><hl:message key="rotulo.nao"/></label>
                   </div>
                 </div>
                </div>
          <%
              }
          %>
         </show:showfield>
         <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_ACESSA_HOST_A_HOST)%>">
          <div class="row">
           <div class=" col-md-6 form-check mt-2" role="radiogroup" aria-labelledby="hostahost">
              <div class="form-group my-0">
                <span id="hostahost"><hl:message key="rotulo.servidor.acessa.host.a.host" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_ACESSA_HOST_A_HOST)%>"/></span>
              </div>
              <div class="form-check form-check-inline mt-2">
                <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_ACESSA_HOST_A_HOST)%>"
                               di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_ACESSA_HOST_A_HOST) + "_S"%>"
                               type="radio"
                               value="S"
                               checked="<%=TextHelper.forHtmlAttribute(String.valueOf(_servidor.getSerAcessaHostaHost() != null && _servidor.getSerAcessaHostaHost().equalsIgnoreCase(CodedValues.TPC_SIM)))%>"
                               mask="#*10"
                               others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"
                               configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_ACESSA_HOST_A_HOST)%>"
                               classe="form-check-input ml-1"
                 />
                <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.sim"/>' for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_ACESSA_HOST_A_HOST) + "_S"%>"><hl:message key="rotulo.sim"/></label>
              </div>
               <div class="form-check form-check-inline">
                  <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_ACESSA_HOST_A_HOST)%>"
                               di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_ACESSA_HOST_A_HOST) + "_N"%>"
                               type="radio"
                               value="N"
                               checked="<%=TextHelper.forHtmlAttribute(String.valueOf(_servidor.getSerAcessaHostaHost() != null && _servidor.getSerAcessaHostaHost().equalsIgnoreCase(CodedValues.TPC_NAO)))%>"
                               mask="#*10"
                               others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"
                               configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_ACESSA_HOST_A_HOST)%>"
                               classe="form-check-input ml-1"
                 />
                <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.nao"/>' for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_ACESSA_HOST_A_HOST) + "_N"%>"><hl:message key="rotulo.nao"/></label>
              </div>
           </div>
          </div>
         </show:showfield>
      <%
          if (ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_NOME, _responsavel) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_PRIMEIRO_NOME, _responsavel) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_NOME_PAI, _responsavel) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_NOME_MAE, _responsavel) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_NOME_CONJUGE, _responsavel) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_DATA_NASCIMENTO, _responsavel) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_UF_NASCIMENTO, _responsavel) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_CIDADE_NASCIMENTO, _responsavel) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_NACIONALIDADE, _responsavel) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_ESTADO_CIVIL, _responsavel) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_SEXO, _responsavel) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_DEFICIENTE_VISUAL, _responsavel) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_ACESSA_HOST_A_HOST, _responsavel)||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_QTD_FILHOS,_responsavel)||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_NIVEL_ESCOLARIDADE,_responsavel)
            ) {
      %>
      </fieldset>
      <%
          }
      %>
      <%
          if (ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_CPF, _responsavel) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_NO_IDENTIDADE, _responsavel) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_NUM_CARTEIRA_TRABALHO, _responsavel) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_NUM_PIS, _responsavel)) {
      %>
      <fieldset>
         <h3 class="legend"><span><hl:message key="rotulo.servidor.subtitulo.documentos.pessoais"/></span></h3>
      <%
          }
      %>
      
      <div class="row">
       <div class="form-group col-sm-6">
        <hl:campoCPFv4 name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_CPF)%>"
                   placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.cpf", _responsavel)%>" 
                   classe="form-control"
                   configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_CPF)%>"
                   value="<%=TextHelper.forHtmlAttribute(_servidor.getSerCpf())%>"
                   others="<%=TextHelper.forHtmlAttribute((_readOnly || !ParamSist.paramEquals(CodedValues.TPC_PERMITE_EDITAR_CPF_SERVIDOR, CodedValues.TPC_SIM, _responsavel)) ? "disabled" : "")%>"
        />
       </div>
      </div>
      
      <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NO_IDENTIDADE)%>">
       <div class="row">
        <div class="form-group col-sm-3">
          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NO_IDENTIDADE)%>"><hl:message key="rotulo.servidor.cartIdentidade" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NO_IDENTIDADE)%>"/></label>
          <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NO_IDENTIDADE)%>"
                    di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NO_IDENTIDADE)%>"
                    type="text"
                    classe="form-control"
                    value="<%=TextHelper.forHtmlAttribute(_servidor != null && _servidor.getSerNroIdt() != null ? _servidor.getSerNroIdt() : "")%>"
                    mask="#*40"
                    others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"                              
                    configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NO_IDENTIDADE)%>"
                    placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.numero.identidade", _responsavel)%>"                             
          />
        </div>
        <div class="form-group col-sm-3">
          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_EMISSOR_IDENTIDADE)%>"><hl:message key="rotulo.servidor.rg.orgao.emissor" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_EMISSOR_IDENTIDADE)%>"/></label>
          <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_EMISSOR_IDENTIDADE)%>"
                    di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_EMISSOR_IDENTIDADE)%>"
                    type="text"
                    classe="form-control"
                    value="<%=TextHelper.forHtmlAttribute(_servidor != null && _servidor.getSerEmissorIdt() != null ? _servidor.getSerEmissorIdt() : "")%>"
                    mask="#*40"
                    others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"                              
                    configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_EMISSOR_IDENTIDADE)%>"
                    placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.orgao.emissor", _responsavel)%>"                             
          />
        </div>
        <div class="form-group col-sm-3">
          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_UF_IDENTIDADE)%>"><hl:message key="rotulo.servidor.rg.uf" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_UF_IDENTIDADE)%>"/></label>
          <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_UF_IDENTIDADE)%>"
                    di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_UF_IDENTIDADE)%>"
                    type="text"
                    classe="form-control"
                    value="<%=TextHelper.forHtmlAttribute(_servidor != null && _servidor.getSerUfIdt() != null ? _servidor.getSerUfIdt() : "")%>"
                    mask="#*2"
                    others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"                              
                    configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_UF_IDENTIDADE)%>"
                    placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.uf", _responsavel)%>"                             
          />
        </div>
        <div class="form-group col-sm-3">
          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_DATA_IDENTIDADE)%>"><hl:message key="rotulo.servidor.rg.data.emissao" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_DATA_IDENTIDADE)%>"/></label>
          <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_DATA_IDENTIDADE)%>"
                    di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_DATA_IDENTIDADE)%>"
                    type="text"
                    classe="form-control"
                    value="<%=TextHelper.forHtmlAttribute(_servidor != null && _servidor.getSerDataIdt() != null ? DateHelper.format(_servidor.getSerDataIdt(), LocaleHelper.getDatePattern()) : "")%>"
                    mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                    others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"                              
                    configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_DATA_IDENTIDADE)%>"
                    placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>"                             
          />
        </div>
       </div>      
      </show:showfield>
      
      <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NUM_CARTEIRA_TRABALHO)%>">
       <div class="row">
        <div class="form-group col-sm-6">
          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NUM_CARTEIRA_TRABALHO)%>"><hl:message key="rotulo.servidor.cartTrabalho" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NUM_CARTEIRA_TRABALHO)%>"/></label>
          <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NUM_CARTEIRA_TRABALHO)%>"
                    di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NUM_CARTEIRA_TRABALHO)%>"
                    type="text"
                    classe="form-control"
                    value="<%=TextHelper.forHtmlAttribute(_servidor != null && _servidor.getSerCartProf() != null ? _servidor.getSerCartProf().toString() : "")%>"
                    mask="#*40"
                    others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"                              
                    configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NUM_CARTEIRA_TRABALHO)%>"
                    placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.cartTrabalho", _responsavel)%>"                             
          />
        </div>
       </div>
      </show:showfield>
      
      <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NUM_PIS)%>">
       <div class="row">
        <div class="form-group col-sm-6">
         <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NUM_PIS)%>"><hl:message key="rotulo.servidor.pis" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NUM_PIS)%>"/></label>
         <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NUM_PIS)%>"
                   di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NUM_PIS)%>"
                   type="text"
                   classe="form-control"
                   value="<%=TextHelper.forHtmlAttribute(_servidor != null && _servidor.getSerPis() != null ? _servidor.getSerPis().toString() : "")%>"
                   mask="#*40"
                   others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"                              
                   configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NUM_PIS)%>"
                   placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.pis", _responsavel)%>"                             
         />
        </div>
       </div>
      </show:showfield>
      
      <%
                if (ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_CPF, _responsavel) ||
                 ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_NO_IDENTIDADE, _responsavel) ||
                 ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_NUM_CARTEIRA_TRABALHO, _responsavel) ||
                 ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_NUM_PIS, _responsavel)) {
            %>
      </fieldset>
      <%
          }
      %>
      
      <%
                if (ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_LOGRADOURO, _responsavel) ||
                 ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_NRO, _responsavel) ||
                 ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_COMPLEMENTO, _responsavel) ||
                 ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_BAIRRO, _responsavel) ||
                 ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_CIDADE, _responsavel) ||
                 ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_UF, _responsavel) ||
                 ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_CEP, _responsavel)||
                 ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_TIPO_HABITACAO, _responsavel)){
            %>
      <fieldset>
         <h3 class="legend"><span><hl:message key="rotulo.servidor.subtitulo.endereco.residencial"/></span></h3>
      <%
          }
      %>
      
      <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_LOGRADOURO)%>">
       <div class="row">
        <div class="form-group col-sm-6">
         <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_LOGRADOURO)%>"><hl:message key="rotulo.endereco.logradouro" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_LOGRADOURO)%>"/></label>
         <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_LOGRADOURO)%>"
                   di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_LOGRADOURO)%>"
                   type="text"
                   classe="form-control"
                   value="<%=TextHelper.forHtmlAttribute(_servidor != null && _servidor.getSerEnd() != null ? _servidor.getSerEnd().toString() : "")%>"
                   mask="#*100"
                   others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"                              
                   configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_LOGRADOURO)%>"
                   placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.mensagem.endereco.logradouro", _responsavel)%>"                             
         />
        </div>
       </div>
      </show:showfield>
      
      <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NRO)%>">
       <div class="row">
        <div class="form-group col-sm-6">
         <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NRO)%>"><hl:message key="rotulo.endereco.numero" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NRO)%>"/></label>
         <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NRO)%>"
                   di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NRO)%>"
                   type="text"
                   classe="form-control"
                   value="<%=TextHelper.forHtmlAttribute(_servidor != null && _servidor.getSerNro() != null ? _servidor.getSerNro().toString() : "")%>"
                   mask="#*15"
                   others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"                              
                   configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_NRO)%>"
                   placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.mensagem.endereco.numero", _responsavel)%>"                             
         />
        </div>
       </div>
      </show:showfield>
      
      <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_COMPLEMENTO)%>">
       <div class="row">
        <div class="form-group col-sm-6">
         <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_COMPLEMENTO)%>"><hl:message key="rotulo.endereco.complemento" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_COMPLEMENTO)%>"/></label>
         <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_COMPLEMENTO)%>"
                   di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_COMPLEMENTO)%>"
                   type="text"
                   classe="form-control"
                   value="<%=TextHelper.forHtmlAttribute(_servidor != null && _servidor.getSerCompl() != null ? _servidor.getSerCompl().toString() : "")%>"
                   mask="#*40"
                   others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"                              
                   configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_COMPLEMENTO)%>"
                   placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.mensagem.endereco.complemento", _responsavel)%>"                             
         />
        </div>
       </div>
      </show:showfield>
      
      <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_BAIRRO)%>">
       <div class="row">
        <div class="form-group col-sm-6">
         <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_BAIRRO)%>"><hl:message key="rotulo.endereco.bairro" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_BAIRRO)%>"/></label>
         <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_BAIRRO)%>"
                   di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_BAIRRO)%>"
                   type="text"
                   classe="form-control"
                   value="<%=TextHelper.forHtmlAttribute(_servidor != null && _servidor.getSerBairro() != null ? _servidor.getSerBairro().toString() : "")%>"
                   mask="#*40"
                   others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"                              
                   configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_BAIRRO)%>"
                   placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.mensagem.endereco.bairro", _responsavel)%>"                             
         />
        </div>
       </div>
      </show:showfield>

      <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_CIDADE)%>">
       <div class="row">
        <div class="form-group col-sm-6">
         <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_CIDADE)%>"><hl:message key="rotulo.endereco.cidade" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_CIDADE)%>"/></label>
         <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_CIDADE)%>"
                   di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_CIDADE)%>"
                   type="text"
                   classe="form-control"
                   value="<%=TextHelper.forHtmlAttribute(_servidor != null && _servidor.getSerCidade() != null ? _servidor.getSerCidade().toString() : "")%>"
                   mask="#*40"
                   others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"                              
                   configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_CIDADE)%>"
                   placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.mensagem.endereco.cidade", _responsavel)%>"                             
         />
        </div>
       </div>
      </show:showfield>

      <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_UF)%>"> 
        <div class="row">
          <div class="form-group col-sm-6">
             <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_UF)%>"><hl:message key="rotulo.endereco.estado" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_UF)%>"/></label>
             <%=JspHelper.geraComboUF(FieldKeysConstants.EDT_SERVIDOR_UF, (_servidor != null && _servidor.getSerUf() != null ? _servidor.getSerUf() : ""), _readOnly || ShowFieldHelper.isDisabled(FieldKeysConstants.EDT_SERVIDOR_UF, _responsavel), ApplicationResourcesHelper.getMessage("rotulo.estados", _responsavel), _responsavel)%>
          </div>
        </div>
      </show:showfield>
      
      <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_CEP)%>">
       <div class="row">
        <div class="form-group col-sm-6">
         <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_CEP)%>"><hl:message key="rotulo.endereco.cep" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_CEP)%>"/></label>
         <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_CEP)%>"
                   di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_CEP)%>"
                   type="text"
                   classe="form-control"
                   value="<%=TextHelper.forHtmlAttribute(_servidor != null && _servidor.getSerCep() != null ? _servidor.getSerCep().toString() : "")%>"
                   mask="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCepMask())%>"
                   others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"                              
                   configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_CEP)%>"
                   placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.mensagem.endereco.cep", _responsavel)%>"                             
         />
        </div>
       </div>
      </show:showfield>
      <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_TIPO_HABITACAO)%>">
        <%
            if (request.getAttribute("listTipoHabitacao") != null) {
        %>
          <div class="row">
           <div class="form-group col-sm-6">
                <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_TIPO_HABITACAO)%>"><hl:message key="rotulo.servidor.tipo.habitacao" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_TIPO_HABITACAO)%>"/></label>
                <hl:htmlcombo
                     listName="listTipoHabitacao" 
                     name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_TIPO_HABITACAO)%>" 
                     fieldValue="<%=TextHelper.forHtmlAttribute(Columns.THA_CODIGO )%>" 
                     fieldLabel="<%=TextHelper.forHtmlAttribute(Columns.THA_DESCRICAO )%>" 
                     notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", _responsavel)%>"
                     selectedValue="<%=TextHelper.forHtmlAttribute(_servidor != null ? _servidor.getThaCodigo() : "" )%>" 
                     configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_TIPO_HABITACAO)%>"
                     others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"
                     classe="form-control"
                     >
                </hl:htmlcombo>
           </div>
          </div>
          <%
              }
          %>
       </show:showfield>
      
      <%
                if (ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_LOGRADOURO, _responsavel) ||
                 ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_NRO, _responsavel) ||
                 ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_COMPLEMENTO, _responsavel) ||
                 ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_BAIRRO, _responsavel) ||
                 ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_CIDADE, _responsavel) ||
                 ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_UF, _responsavel) ||
                 ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_CEP, _responsavel)||
                 ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_TIPO_HABITACAO, _responsavel)){
            %>
      </fieldset>
      <%
          }
      %>
      
      <%
                if (ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_TELEFONE, _responsavel) ||
                 ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_CELULAR, _responsavel) ||
                 ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_EMAIL, _responsavel)) {
            %>
      <fieldset>
         <h3 class="legend"><span><hl:message key="rotulo.servidor.subtitulo.dados.contato"/></span></h3>
      <%
          }
      %>
      
         <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_TELEFONE)%>">
          <%
              // Quebra o telefone em DDD + número.
                    String serTel = "", serTelDdd = "";
                    serTel = (_servidor != null && _servidor.getSerTel() != null) ? TextHelper.dropSeparator(_servidor.getSerTel()) : "";
                    if (ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_DDD_TELEFONE, _responsavel) && serTel.length() == 10 || serTel.length() == 11) {
                serTelDdd = serTel.substring(0, 2);
                serTel = serTel.substring(2, serTel.length());
                    }
          %>
          <div class="row">
           <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_DDD_TELEFONE)%>">
            <div class="form-group col-sm-2">
              <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_DDD_TELEFONE)%>"><hl:message key="rotulo.servidor.codigo.localidade" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_DDD_TELEFONE)%>"/></label>
              <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_DDD_TELEFONE)%>"
                        di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_DDD_TELEFONE)%>"
                        type="text"
                        classe="form-control"
                        value="<%=TextHelper.forHtmlAttribute(serTelDdd)%>"
                        mask="<%=LocaleHelper.getDDDMask()%>"
                        others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"                              
                        configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_DDD_TELEFONE)%>"
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.ddd", _responsavel)%>"                             
              />
           </div>
           </show:showfield>
           <div class="form-group col-sm-6">
             <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_TELEFONE)%>"><hl:message key="rotulo.servidor.telefone" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_TELEFONE)%>"/></label>
             <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_TELEFONE)%>"
                       di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_TELEFONE)%>"
                       type="text"
                       classe="form-control"
                       value="<%=TextHelper.forHtmlAttribute(serTel)%>"
                       mask="<%=LocaleHelper.getTelefoneMask()%>"
                       others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"                              
                       configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_TELEFONE)%>"
                       placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.telefone", _responsavel)%>"                             
             />
           </div>
          </div>
         </show:showfield>
         
         <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_CELULAR)%>">
          <%
              // Quebra o celular em DDD + número.
                    String serCelular = "", serCelularDdd = "";
                    serCelular = (_servidor != null && _servidor.getSerCelular() != null) ? TextHelper.dropSeparator(_servidor.getSerCelular()) : "";
                    if (ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_DDD_CELULAR, _responsavel) && (serCelular.length() == 10 || serCelular.length() == 11)) {
               serCelularDdd = serCelular.substring(0, LocaleHelper.getDDDCelularMask().length());
               serCelular = serCelular.substring(LocaleHelper.getDDDCelularMask().length(), serCelular.length());
                    }
          %>
          <div class="row">
           <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_DDD_CELULAR)%>">
            <div class="form-group col-sm-2">
              <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_DDD_CELULAR)%>"><hl:message key="rotulo.servidor.codigo.localidade" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_DDD_CELULAR)%>"/></label>
              <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_DDD_CELULAR)%>"
                        di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_DDD_CELULAR)%>"
                        type="text"
                        classe="form-control"
                        value="<%=TextHelper.forHtmlAttribute(serCelularDdd)%>"
                        mask="<%=LocaleHelper.getDDDCelularMask()%>"
                        others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"                              
                        configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_DDD_CELULAR)%>"
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.ddd", _responsavel)%>"                             
              />
           </div>
           </show:showfield>
           <div class="form-group col-sm-6">
             <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_CELULAR)%>"><hl:message key="rotulo.servidor.celular" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_CELULAR)%>"/></label>
             <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_CELULAR)%>"
                       di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_CELULAR)%>"
                       type="text"
                       classe="form-control"
                       value="<%=TextHelper.forHtmlAttribute(serCelular)%>"
                       mask="<%=LocaleHelper.getCelularMask()%>"
                       others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"                              
                       configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_CELULAR)%>"
                       placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.celular", _responsavel)%>"                             
             />
           </div>
          </div>
         </show:showfield>
         
         <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_EMAIL)%>">
          <div class="row">
           <div class="form-group col-sm-6">
            <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_EMAIL)%>"><hl:message key="rotulo.servidor.email" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_EMAIL)%>"/></label>
            <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_EMAIL)%>"
                      di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_EMAIL)%>"
                      type="text"
                      classe="form-control"
                      value="<%=TextHelper.forHtmlAttribute(_servidor != null && _servidor.getSerEmail() != null ? _servidor.getSerEmail().toString() :\"\")%>"
                      mask="#*100"
                      others="<%=TextHelper.forHtmlAttribute(_readOnly || !_podeAlterarEmail ? "disabled" : "")%>"                              
                      configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_EMAIL)%>"
                      placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.email", _responsavel)%>"                             
            />
           </div>

           <%
               if (_responsavel.isSup() && _servidor != null && _servidor.getSerDataValidacaoEmail() != null) {
           %>
              <div class="form-group slider col-sm-4 col-md-4 mt-4 mb-2">
                 <div class="tooltip-inner"><hl:message key="mensagem.informacao.email.validado.data" arg0="<%=DateHelper.format(_servidor.getSerDataValidacaoEmail(), LocaleHelper.getDateTimePattern())%>"/></div>
              </div>
           <%
               }
           %> 

          </div>
         </show:showfield>
         
         <%
                      if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_EMAIL_CONTRATOS_REJEITADOS_FOLHA, CodedValues.NOTIFICA_CONTRATOS_REJEITADOS_FOLHA_OPCIONAL, _responsavel)) {
                  %>
         
         <!-- DESENV-5578 - Envio de e-mail ao rejeitar parcela -->
         <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_EMAIL_CONTRATOS_REJEITADOS)%>">
          <div class="row">
           <div class=" col-md-6 form-check mt-2">
              <div class="form-group my-0">
                  <span><hl:message key="rotulo.habilita.email.servidor.contratos.rejeitados" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_EMAIL_CONTRATOS_REJEITADOS)%>"/></span>
              </div>
              <div class="form-check form-check-inline mt-2">
                <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_EMAIL_CONTRATOS_REJEITADOS)%>"
                               di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_EMAIL_CONTRATOS_REJEITADOS) + "_S"%>"
                               type="radio"
                               value="S"
                               checked="<%=TextHelper.forHtmlAttribute(String.valueOf(emailContratosRejeitados != null && emailContratosRejeitados.equalsIgnoreCase(CodedValues.TPC_SIM)))%>"
                               mask="#*10"
                               others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"
                               configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_EMAIL_CONTRATOS_REJEITADOS)%>"
                               classe="form-check-input ml-1"
                 />
                <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.sim"/>' for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_EMAIL_CONTRATOS_REJEITADOS) + "_S"%>"><hl:message key="rotulo.sim"/></label>
              </div>
               <div class="form-check-inline form-check">
                  <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_EMAIL_CONTRATOS_REJEITADOS)%>"
                               di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_EMAIL_CONTRATOS_REJEITADOS) + "_N"%>"
                               type="radio"
                               value="N"
                               checked="<%=TextHelper.forHtmlAttribute(String.valueOf(emailContratosRejeitados != null && emailContratosRejeitados.equalsIgnoreCase(CodedValues.TPC_NAO)))%>"
                               mask="#*10"
                               others="<%=TextHelper.forHtmlAttribute(_readOnly ? "disabled" : "")%>"
                               configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_EMAIL_CONTRATOS_REJEITADOS)%>"
                               classe="form-check-input ml-1"
                 />
                <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.nao"/>' for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_EMAIL_CONTRATOS_REJEITADOS) + "_N"%>"><hl:message key="rotulo.nao"/></label>
               </div>
           </div>
           </div>
          </show:showfield>
          <% } %>
         
         
      <% if (ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_TELEFONE, _responsavel) ||
             ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_CELULAR, _responsavel) ||
             ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_EMAIL, _responsavel)) { %>
      </fieldset>
      <% } %>

<script type="text/JavaScript">
function validarDadosObrigatoriosServidor() {

<% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_NOME, _responsavel)) { %>
     var serNomeField = f0.<%=(String)(FieldKeysConstants.EDT_SERVIDOR_NOME)%>;
     if (serNomeField.value == null || serNomeField.value == '') {
         alert('<hl:message key="mensagem.informe.servidor.nome"/>');
         serNomeField.focus();
         return false;
     }
<% } %>

<% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_TITULACAO, _responsavel)) { %>
     var serTitulacaoField = f0.<%=(String)(FieldKeysConstants.EDT_SERVIDOR_TITULACAO)%>;
     if (serTitulacaoField.value == null || serTitulacaoField.value == '') {
         alert('<hl:message key="mensagem.informe.servidor.tratamento.nome"/>');
         serTitulacaoField.focus();
         return false;
     }
<% } %>

<% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_PRIMEIRO_NOME, _responsavel)) { %>
     var serPrimeiroNomeField = f0.<%=(String)(FieldKeysConstants.EDT_SERVIDOR_PRIMEIRO_NOME)%>;
     if (serPrimeiroNomeField.value == null || serPrimeiroNomeField.value == '') {
         alert('<hl:message key="mensagem.informe.servidor.primeiro.nome"/>');
         serPrimeiroNomeField.focus();
         return false;
     }
<% } %>

<% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_NOME_MEIO, _responsavel)) { %>
     var serNomeMeioField = f0.<%=(String)(FieldKeysConstants.EDT_SERVIDOR_NOME_MEIO)%>;
     if (serNomeMeioField.value == null || serNomeMeioField.value == '') {
         alert('<hl:message key="mensagem.informe.servidor.meio.nome"/>');
         serNomeMeioField.focus();
         return false;
     }
<% } %>

<% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_ULTIMO_NOME, _responsavel)) { %>
     var serUltimoNomeField = f0.<%=(String)(FieldKeysConstants.EDT_SERVIDOR_ULTIMO_NOME)%>;
     if (serUltimoNomeField.value == null || serUltimoNomeField.value == '') {
         alert('<hl:message key="mensagem.informe.servidor.ultimo.nome"/>');
         serUltimoNomeField.focus();
         return false;
     }
<% } %>

<% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_NOME_PAI, _responsavel)) { %>
     var serNomePaiField = f0.<%=(String)(FieldKeysConstants.EDT_SERVIDOR_NOME_PAI)%>;
     if (serNomePaiField.value == null || serNomePaiField.value == '') {
         alert('<hl:message key="mensagem.informe.servidor.nome.pai"/>');
         serNomePaiField.focus();
         return false;
     }
<% } %>

<% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_NOME_MAE, _responsavel)) { %>
     var serNomeMaeField = f0.<%=(String)(FieldKeysConstants.EDT_SERVIDOR_NOME_MAE)%>;
     if (serNomeMaeField.value == null || serNomeMaeField.value == '') {
         alert('<hl:message key="mensagem.informe.servidor.nome.mae"/>');
         serNomeMaeField.focus();
         return false;
     }
<% } %>

<% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_NOME_CONJUGE, _responsavel)) { %>
     var serNomeConjugeField = f0.<%=(String)(FieldKeysConstants.EDT_SERVIDOR_NOME_CONJUGE)%>;
     if (serNomeConjugeField.value == null || serNomeConjugeField.value == '') {
         alert('<hl:message key="mensagem.informe.servidor.nome.conjuge"/>');
         serNomeConjugeField.focus();
         return false;
     }
<% } %>

<% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_DATA_NASCIMENTO, _responsavel)) { %>
     var serDataNascField = f0.<%=(String)(FieldKeysConstants.EDT_SERVIDOR_DATA_NASCIMENTO)%>;
     if (serDataNascField.value == null || serDataNascField.value == '') {
         alert('<hl:message key="mensagem.informe.servidor.data.nascimento"/>');
         serDataNascField.focus();
         return false;
     }
<% } %>

<% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_CIDADE_NASCIMENTO, _responsavel)) { %>
     var serCidNascField = f0.<%=(String)(FieldKeysConstants.EDT_SERVIDOR_CIDADE_NASCIMENTO)%>;
     if (serCidNascField.value == null || serCidNascField.value == '') {
         alert('<hl:message key="mensagem.informe.servidor.cidade.nascimento"/>');
         serCidNascField.focus();
         return false;
     }
<% } %>

<% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_UF_NASCIMENTO, _responsavel)) { %>
     var serUfNascField = f0.<%=(String)(FieldKeysConstants.EDT_SERVIDOR_UF_NASCIMENTO)%>;
     if (serUfNascField.value == null || serUfNascField.value == '') {
         alert('<hl:message key="mensagem.informe.servidor.uf.nascimento"/>');
         serUfNascField.focus();
         return false;
     }
<% } %>

<% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_ESTADO_CIVIL, _responsavel)) { %>
     var serEstCivilField = f0.<%=(String)(FieldKeysConstants.EDT_SERVIDOR_ESTADO_CIVIL)%>;
     if (serEstCivilField.value == null || serEstCivilField.value == '') {
         alert('<hl:message key="mensagem.informe.servidor.estado.civil"/>');
         serEstCivilField.focus();
         return false;
     }
<% } %>

<% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_NACIONALIDADE, _responsavel)) { %>
     var serNacionalidadeField = f0.<%=(String)(FieldKeysConstants.EDT_SERVIDOR_NACIONALIDADE)%>;
     if (serNacionalidadeField.value == null || serNacionalidadeField.value == '') {
         alert('<hl:message key="mensagem.informe.servidor.naturalidade"/>');
         serNacionalidadeField.focus();
         return false;
     }
<% } %>

<% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_SEXO, _responsavel)) { %>
     var serSexoField = f0.<%=(String)(FieldKeysConstants.EDT_SERVIDOR_SEXO)%>;
     if (serSexoField.value == null || serSexoField.value == '') {
         alert('<hl:message key="mensagem.informe.servidor.sexo"/>');
         serSexoField[0].focus();
         return false;
     }
<% } %>

<% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_DEFICIENTE_VISUAL, _responsavel)) { %>
     var serDeficienteVisualField = f0.<%=(String)(FieldKeysConstants.EDT_SERVIDOR_DEFICIENTE_VISUAL)%>;
     if (serDeficienteVisualField.value == null || serDeficienteVisualField.value == '') {
         alert('<hl:message key="mensagem.informe.servidor.deficiente.visual"/>');
         serDeficienteVisualField[0].focus();
         return false;
     }
<% } %>

<% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_CPF, _responsavel)) { %>
     var serCpfField = f0.<%=(String)(FieldKeysConstants.EDT_SERVIDOR_CPF)%>;
     if (serCpfField.value == null || serCpfField.value == '') {
         alert('<hl:message key="mensagem.informe.servidor.cpf"/>');
         serCpfField.focus();
         return false;
     }
<% } %>

<% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_NUM_CARTEIRA_TRABALHO, _responsavel)) { %>
     var serCartProfField = f0.<%=(String)(FieldKeysConstants.EDT_SERVIDOR_NUM_CARTEIRA_TRABALHO)%>;
     if (serCartProfField.value == null || serCartProfField.value == '') {
         alert('<hl:message key="mensagem.informe.servidor.cart.trabalho"/>');
         serCartProfField.focus();
         return false;
     }
<% } %>

<% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_NUM_PIS, _responsavel)) { %>
     var serPisField = f0.<%=(String)(FieldKeysConstants.EDT_SERVIDOR_NUM_PIS)%>;
     if (serPisField.value == null || serPisField.value == '') {
         alert('<hl:message key="mensagem.informe.servidor.pis"/>');
         serPisField.focus();
         return false;
     }
<% } %>

<% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_NO_IDENTIDADE, _responsavel)) { %>
     var serNroIdtField = f0.<%=(String)(FieldKeysConstants.EDT_SERVIDOR_NO_IDENTIDADE)%>;
     if (serNroIdtField.value == null || serNroIdtField.value == '') {
         alert('<hl:message key="mensagem.informe.servidor.identidade"/>');
         serNroIdtField.focus();
         return false;
     }
<% } %>

<% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_EMISSOR_IDENTIDADE, _responsavel)) { %>
     var serEmissorIdtField = f0.<%=(String)(FieldKeysConstants.EDT_SERVIDOR_EMISSOR_IDENTIDADE)%>;
     if (serEmissorIdtField.value == null || serEmissorIdtField.value == '') {
         alert('<hl:message key="mensagem.informe.servidor.emissor.identidade"/>');
         serEmissorIdtField.focus();
         return false;
     }
<% } %>

<% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_UF_IDENTIDADE, _responsavel)) { %>
     var serUfIdtField = f0.<%=(String)(FieldKeysConstants.EDT_SERVIDOR_UF_IDENTIDADE)%>;
     if (serUfIdtField.value == null || serUfIdtField.value == '') {
         alert('<hl:message key="mensagem.informe.servidor.uf.identidade"/>');
         serUfIdtField.focus();
         return false;
     }
<% } %>

<% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_DATA_IDENTIDADE, _responsavel)) { %>
     var serDataIdtField = f0.<%=(String)(FieldKeysConstants.EDT_SERVIDOR_DATA_IDENTIDADE)%>;
     if (serDataIdtField.value == null || serDataIdtField.value == '') {
         alert('<hl:message key="mensagem.informe.servidor.data.emissao.identidade"/>');
         serDataIdtField.focus();
         return false;
     }
<% } %>

<% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_LOGRADOURO, _responsavel)) { %>
     var serEndField = f0.<%=(String)(FieldKeysConstants.EDT_SERVIDOR_LOGRADOURO)%>;
     if (serEndField.value == null || serEndField.value == '') {
         alert('<hl:message key="mensagem.informe.servidor.logradouro"/>');
         serEndField.focus();
         return false;
     }
<% } %>

<% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_NRO, _responsavel)) { %>
     var serNroField = f0.<%=(String)(FieldKeysConstants.EDT_SERVIDOR_NRO)%>;
     if (serNroField.value == null || serNroField.value == '') {
         alert('<hl:message key="mensagem.informe.servidor.numero"/>');
         serNroField.focus();
         return false;
     }
<% } %>

<% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_COMPLEMENTO, _responsavel)) { %>
     var serComplField = f0.<%=(String)(FieldKeysConstants.EDT_SERVIDOR_COMPLEMENTO)%>;
     if (serComplField.value == null || serComplField.value == '') {
         alert('<hl:message key="mensagem.informe.servidor.complemento"/>');
         serComplField.focus();
         return false;
     }
<% } %>

<% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_BAIRRO, _responsavel)) { %>
     var serBairroField = f0.<%=(String)(FieldKeysConstants.EDT_SERVIDOR_BAIRRO)%>;
     if (serBairroField.value == null || serBairroField.value == '') {
         alert('<hl:message key="mensagem.informe.servidor.bairro"/>');
         serBairroField.focus();
         return false;
     }
<% } %>

<% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_CIDADE, _responsavel)) { %>
     var serCidadeField = f0.<%=(String)(FieldKeysConstants.EDT_SERVIDOR_CIDADE)%>;
     if (serCidadeField.value == null || serCidadeField.value == '') {
         alert('<hl:message key="mensagem.informe.servidor.cidade"/>');
         serCidadeField.focus();
         return false;
     }
<% } %>

<% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_UF, _responsavel)) { %>
     var serUfField = f0.<%=(String)(FieldKeysConstants.EDT_SERVIDOR_UF)%>;
     if (serUfField.value == null || serUfField.value == '') {
         alert('<hl:message key="mensagem.informe.servidor.estado"/>');
         serUfField.focus();
         return false;
     }
<% } %>

<% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_CEP, _responsavel)) { %>
     var serCepField = f0.<%=(String)(FieldKeysConstants.EDT_SERVIDOR_CEP)%>;
     if (serCepField.value == null || serCepField.value == '') {
         alert('<hl:message key="mensagem.informe.servidor.cep"/>');
         serCepField.focus();
         return false;
     }
<% } %>

<% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_TELEFONE, _responsavel)) { %>
     var serTelField = f0.<%=(String)(FieldKeysConstants.EDT_SERVIDOR_TELEFONE)%>;
     if (serTelField.value == null || serTelField.value == '') {
         alert('<hl:message key="mensagem.informe.servidor.telefone"/>');
         serTelField.focus();
         return false;
     }
<% } %>

<% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_CELULAR, _responsavel)) { %>
     var serCelularField = f0.<%=(String)(FieldKeysConstants.EDT_SERVIDOR_CELULAR)%>;
     if (serCelularField.value == null || serCelularField.value == '') {
         alert('<hl:message key="mensagem.informe.servidor.celular"/>');
         serCelularField.focus();
         return false;
     }
<% } %>

<% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_EMAIL, _responsavel)) { %>
     var serEmailField = f0.<%=(String)(FieldKeysConstants.EDT_SERVIDOR_EMAIL)%>;
     if (serEmailField.value == null || serEmailField.value == '') {
         alert('<hl:message key="mensagem.informe.servidor.email"/>');
         serEmailField.focus();
         return false;
     }
<% } %>

<% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_ACESSA_HOST_A_HOST, _responsavel)) { %>
     var serAcessaHostaHostField = f0.<%=(String)(FieldKeysConstants.EDT_SERVIDOR_ACESSA_HOST_A_HOST)%>;
     if (serAcessaHostaHostField.value == null || serAcessaHostaHostField.value == '') {
         alert('<hl:message key="mensagem.informe.servidor.hostahost"/>');
         serAcessaHostaHostField[0].focus();
         return false;
     }
<% } %>

<% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_QTD_FILHOS, _responsavel)) { %>
    var serFilhosField = f0.<%=(String)(FieldKeysConstants.EDT_SERVIDOR_QTD_FILHOS)%>;
    if (serFilhosField.value == null || serFilhosField.value == '') {
        alert('<hl:message key="mensagem.informe.servidor.quantidade.filhos"/>');
        serFilhosField[0].focus();
        return false;
    }
<% } %>

<% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_NIVEL_ESCOLARIDADE, _responsavel)) { %>
    var serEscolaridadeField = f0.<%=(String)(FieldKeysConstants.EDT_SERVIDOR_NIVEL_ESCOLARIDADE)%>;
    if (serEscolaridadeField.value == null || serEscolaridadeField.value == '') {
        alert('<hl:message key="mensagem.informe.servidor.nivel.escolaridade"/>');
        serEscolaridadeField[0].focus();
        return false;
    }
<% } %>

<% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_TIPO_HABITACAO, _responsavel)) { %>
    var serHabitacaoField = f0.<%=(String)(FieldKeysConstants.EDT_SERVIDOR_TIPO_HABITACAO)%>;
    if (serHabitacaoField.value == null || serHabitacaoField.value == '') {
        alert('<hl:message key="mensagem.informe.servidor.tipo.habitacao"/>');
        serHabitacaoField[0].focus();
        return false;
    }
<% } %>

    return true;
}
</script>
