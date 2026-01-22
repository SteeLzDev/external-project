<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.values.Columns"%>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist"%>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants"%>
<%@ page import="com.zetra.econsig.values.CodedValues"%>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper"%>
<%@ page import="java.util.List"%>
<%@ page import="com.zetra.econsig.dto.entidade.MargemTO"%>
<%@ page import="com.zetra.econsig.helper.web.v3.JspHelper"%>
<%@ page import="com.zetra.econsig.dto.entidade.RegistroServidorTO"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper"%>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper"%>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<%
AcessoSistema _responsavel_ = JspHelper.getAcessoSistema(request);
RegistroServidorTO _registroServidor = (RegistroServidorTO) request.getAttribute("registroServidor");
String _rseCodigo = _registroServidor.getRseCodigo();
List<MargemTO> _margens = (List<MargemTO>) request.getAttribute("margens");
boolean _readOnly_ = (request.getAttribute("readOnlyRse") != null && request.getAttribute("readOnlyRse").equals("true"));
boolean podeEditarOrgao = (request.getAttribute("podeEditarOrgao") != null && (Boolean) request.getAttribute("podeEditarOrgao"));
String _msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.alteracao.cadastro", _responsavel_);
String _msgConfirmacaoAprovacao = (request.getAttribute("msgConfirmacaoAprovacao") != null ? request.getAttribute("msgConfirmacaoAprovacao").toString() : "");
String _msgConfirmacaoExclusao = (request.getAttribute("msgConfirmacaoExclusao") != null ? request.getAttribute("msgConfirmacaoExclusao").toString() : "");
boolean _podeEdtStatusRse = _responsavel_.temPermissao(CodedValues.FUN_EDT_STATUS_REGISTRO_SERVIDOR);
int _contadorSubTitulo = (request.getAttribute("contadorSubTitulo") != null ? (Integer) request.getAttribute("contadorSubTitulo") : 1);
%>

   <% if (ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MATRICULA, _responsavel_) ||
        ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MATRICULA_INSTITUCIONAL, _responsavel_) ||
        ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ORGAO, _responsavel_) ||
        ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SUB_ORGAO, _responsavel_) ||
        ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_UNIDADE, _responsavel_) ||
        ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MUNICIPIO_LOTACAO, _responsavel_)) { %>
        <fieldset>
         <h3 class="legend"><span><hl:message key="rotulo.servidor.subtitulo.lotacao.v4"/></span></h3>
   <% } %>
         
         <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MATRICULA)%>">
          <% String maskMatricula = (ParamSist.paramEquals(CodedValues.TPC_MATRICULA_NUMERICA, CodedValues.TPC_SIM, _responsavel_) ? "#D20" : "#*20"); %>
           <div class="row">
            <div class="form-group col-sm-6">
                <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MATRICULA)%>"><hl:message key="rotulo.servidor.matricula" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MATRICULA)%>"/></label>
                <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MATRICULA)%>"
                          di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MATRICULA)%>"
                          type="text"
                          classe="form-control"
                          value="<%=TextHelper.forHtmlAttribute(_registroServidor.getRseMatricula())%>"
                          mask="<%=TextHelper.forHtmlAttribute(maskMatricula)%>"
                          others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"                              
                          configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MATRICULA)%>"
                          placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.matricula", _responsavel_) %>"                             
                />
            </div>
           </div>
         </show:showfield>
         
         <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MATRICULA_INSTITUCIONAL)%>">
           <div class="row">
            <div class="form-group col-sm-6">
                <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MATRICULA_INSTITUCIONAL)%>"><hl:message key="rotulo.servidor.matricula.institucional" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MATRICULA_INSTITUCIONAL)%>"/></label>
                <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MATRICULA_INSTITUCIONAL)%>"
                          di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MATRICULA_INSTITUCIONAL)%>"
                          type="text"
                          classe="form-control"
                          value="<%=TextHelper.forHtmlAttribute(_registroServidor.getRseMatriculaInst())%>"
                          mask="<%=TextHelper.forHtmlAttribute("#*20")%>"
                          others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"                              
                          configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MATRICULA_INSTITUCIONAL)%>"
                          placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.matricula.institucional", _responsavel_)%>"                             
                />
            </div>
           </div>
         </show:showfield>
         
         <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ORGAO)%>">
          <%
              List orgaos = (List) request.getAttribute("orgaos");
          %>
           <div class="row">
            <div class="form-group col-sm-6">
             <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ORGAO)%>"><hl:message key="rotulo.servidor.orgao" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ORGAO)%>"/></label>
             <hl:htmlcombo
                 listName="orgaos" 
                 name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ORGAO)%>" 
                 fieldValue="<%=TextHelper.forHtmlAttribute(Columns.ORG_CODIGO)%>" 
                 fieldLabel="<%=(String)(Columns.ORG_NOME + ";" + Columns.ORG_IDENTIFICADOR)%>" 
                 notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", _responsavel_)%>"
                 selectedValue="<%=TextHelper.forHtmlAttribute(_registroServidor.getOrgCodigo())%>" 
                 configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ORGAO)%>"
                 others="<%=TextHelper.forHtmlAttribute(_readOnly_ || !podeEditarOrgao ? "disabled" : "")%>"
                 classe="form-control"
                 >
            </hl:htmlcombo> 
           </div>
          </div>
         </show:showfield>

         <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SUB_ORGAO)%>">
           <%
               List subOrgaos = (List) request.getAttribute("subOrgaos");
           %>
           <div class="row">
            <div class="form-group col-sm-6">
             <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SUB_ORGAO)%>"><hl:message key="rotulo.servidor.sub.orgao" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SUB_ORGAO)%>"/></label>
             <hl:htmlcombo
                 listName="subOrgaos" 
                 name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SUB_ORGAO)%>" 
                 fieldValue="<%=TextHelper.forHtmlAttribute(Columns.SBO_CODIGO)%>" 
                 fieldLabel="<%=(String)(Columns.SBO_IDENTIFICADOR + ";" + Columns.SBO_DESCRICAO)%>" 
                 notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", _responsavel_)%>"
                 selectedValue="<%=TextHelper.forHtmlAttribute(_registroServidor.getSboCodigo())%>" 
                 configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SUB_ORGAO)%>"
                 others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"
                 classe="form-control"
                 >
            </hl:htmlcombo> 
           </div>
          </div>
         </show:showfield>

         <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_UNIDADE)%>">
          <%
              List unidades = (List) request.getAttribute("unidades");
          %>
           <div class="row">
            <div class="form-group col-sm-6">
             <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_UNIDADE)%>"><hl:message key="rotulo.servidor.unidade" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_UNIDADE)%>"/></label>
             <hl:htmlcombo
                 listName="unidades" 
                 name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_UNIDADE)%>" 
                 fieldValue="<%=TextHelper.forHtmlAttribute(Columns.UNI_CODIGO)%>" 
                 fieldLabel="<%=(String)(Columns.UNI_IDENTIFICADOR + ";" + Columns.UNI_DESCRICAO)%>" 
                 notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", _responsavel_)%>"
                 selectedValue="<%=TextHelper.forHtmlAttribute(_registroServidor.getUniCodigo())%>" 
                 configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_UNIDADE)%>"
                 others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"
                 classe="form-control"
                 >
            </hl:htmlcombo> 
           </div>
          </div>
         </show:showfield>       
         
         <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MUNICIPIO_LOTACAO)%>">
         <%
             String rseMunicipioLotacao = (_registroServidor != null && _registroServidor.getRseMunicipioLotacao() != null ? _registroServidor.getRseMunicipioLotacao().toString() : "");
         %>
          <div class="row">
           <div class="form-group col-sm-6">
            <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MUNICIPIO_LOTACAO)%>"><hl:message key="rotulo.servidor.municipioLotacao" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MUNICIPIO_LOTACAO)%>"/></label>
             <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MUNICIPIO_LOTACAO)%>"
                        di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MUNICIPIO_LOTACAO)%>"
                        type="text"
                        classe="form-control"
                        value="<%=TextHelper.forHtmlAttribute(rseMunicipioLotacao)%>"
                        mask="#*40"
                        others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"                              
                        configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MUNICIPIO_LOTACAO)%>"
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.municipio.lotacao", _responsavel_)%>"                             
              />
           </div>
          </div>
         </show:showfield>
         
   <%
                if (ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MATRICULA, _responsavel_) ||
                    ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MATRICULA_INSTITUCIONAL, _responsavel_) ||
                    ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ORGAO, _responsavel_) ||
                    ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SUB_ORGAO, _responsavel_) ||
                    ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_UNIDADE, _responsavel_) ||
                    ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MUNICIPIO_LOTACAO, _responsavel_)) {
            %>
        </fieldset>
   <%
       }
   %>

   <%
       if (ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SITUACAO, _responsavel_) ||
             ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_EXIBE_MOTIVO_BLOQUEIO, _responsavel_) ||
             ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_ADMISSAO, _responsavel_) ||
             ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PRAZO, _responsavel_) ||
             ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_SAIDA, _responsavel_) ||
             ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_ULT_SALARIO, _responsavel_) ||
             ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_RETORNO, _responsavel_) ||
             ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PEDIDO_DEMISSAO, _responsavel_)) {
   %>
        <fieldset>
         <h3 class="legend"><span><hl:message key="rotulo.servidor.subtitulo.contrato.trabalho"/></span></h3>
   <%
       }
   %>

         <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SITUACAO)%>">
          <%
              String others = " onChange=\"habilitaDesabilitaDatas(this); habilitaDesabilitaExibirMotivoBloqueio(this); if(typeof limpaCamposData == 'function'){limpaCamposData(this.value); }\" ";
               List listaSrs = (List) request.getAttribute("listaSrs");
          %>
          <div class="row">
           <div class="form-group col-sm-6">
            <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SITUACAO)%>"><hl:message key="rotulo.servidor.status" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SITUACAO)%>"/></label>
            <hl:htmlcombo
                listName="listaSrs" 
                name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SITUACAO)%>" 
                fieldValue="<%=TextHelper.forHtmlAttribute(Columns.SRS_CODIGO)%>" 
                fieldLabel="<%=TextHelper.forHtmlAttribute(Columns.SRS_DESCRICAO)%>" 
                notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", _responsavel_)%>"
                selectedValue="<%=TextHelper.forHtmlAttribute(_registroServidor.getSrsCodigo())%>" 
                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SITUACAO)%>"
                others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : (_podeEdtStatusRse ? "" : "disabled"))+others%>"
                classe="form-control"
                >
           </hl:htmlcombo> 
           </div>
          </div>
         </show:showfield>

        <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_EXIBE_MOTIVO_BLOQUEIO)%>">
          <div class="row">
           <div class=" col-md-6 form-check mt-2" role="radiogroup" aria-labelledby="exibe_motivo_bloqueio">
             <div class="form-group my-0">
               <span id="exibe_motivo_bloqueio"><hl:message key="rotulo.servidor.exibe.motivo.bloqueio" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_EXIBE_MOTIVO_BLOQUEIO)%>"/></span>
             </div>
             <div class="form-check form-check-inline mt-2">
               <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_EXIBE_MOTIVO_BLOQUEIO)%>"
                              di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_EXIBE_MOTIVO_BLOQUEIO) + "_S"%>"
                              type="radio"
                              value="S"
                              checked="<%=String.valueOf(_registroServidor.getRseMotivoBloqueio() != null && !_registroServidor.getRseMotivoBloqueio().equals(\"\"))%>"
                              mask="#*10"
                              others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : (_podeEdtStatusRse && _registroServidor.getSrsCodigo().equals(CodedValues.SRS_BLOQUEADO) ? "" : "disabled"))%>"
                              configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_EXIBE_MOTIVO_BLOQUEIO)%>"
                              classe="form-check-input ml-1"
                />
               <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.sim"/>' for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_EXIBE_MOTIVO_BLOQUEIO) + "_S"%>"><hl:message key="rotulo.sim"/></label>
             </div>
               <div class="form-check-inline form-check">
                 <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_EXIBE_MOTIVO_BLOQUEIO)%>"
                              di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_EXIBE_MOTIVO_BLOQUEIO) + "_N"%>"
                              type="radio"
                              value="N"
                              checked="<%=String.valueOf(_registroServidor.getRseMotivoBloqueio() != null && _registroServidor.getRseMotivoBloqueio().equals(\"\"))%>"
                              mask="#*10"
                              others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : (_podeEdtStatusRse && _registroServidor.getSrsCodigo().equals(CodedValues.SRS_BLOQUEADO) ? "" : "disabled"))%>"
                              configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_EXIBE_MOTIVO_BLOQUEIO)%>"
                              classe="form-check-input ml-1"
                />
               <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.nao"/>' for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_EXIBE_MOTIVO_BLOQUEIO) + "_N"%>"><hl:message key="rotulo.nao"/></label>
             </div>
           </div>
          </div>
        </show:showfield>

         <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_ADMISSAO)%>">
         <%
             String rseDataAdmissao = null;
              rseDataAdmissao = (_registroServidor != null && _registroServidor.getRseDataAdmissao() != null ? _registroServidor.getRseDataAdmissao().toString() : "");
              if (!rseDataAdmissao.equals("")) {
                 rseDataAdmissao = DateHelper.reformat(rseDataAdmissao, "yyyy-MM-dd", LocaleHelper.getDatePattern());
              }
         %>
          <div class="row">
           <div class="form-group col-sm-6">
            <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_ADMISSAO)%>"><hl:message key="rotulo.servidor.dataAdmissao" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_ADMISSAO)%>"/></label>
            <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_ADMISSAO)%>"
                        di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_ADMISSAO)%>"
                        type="text"
                        classe="form-control"
                        value="<%=TextHelper.forHtmlAttribute(rseDataAdmissao)%>"
                        mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                        maxlength='10'
                        others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"                              
                        configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_ADMISSAO)%>"
                        placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>"                             
             />
           </div>
          </div>
         </show:showfield>
         
         <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PRAZO)%>">
          <div class="row">
           <div class="form-group col-sm-6">
            <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PRAZO)%>"><hl:message key="rotulo.servidor.prazo" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PRAZO)%>"/></label>
            <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PRAZO)%>"
                        di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PRAZO)%>"
                        type="text"
                        classe="form-control"
                        value="<%=TextHelper.forHtml(_registroServidor.getRsePrazo() != null ? _registroServidor.getRsePrazo().toString() : "" )%>"
                        mask="#D11"
                        others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"                              
                        configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PRAZO)%>"
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.prazo", _responsavel_)%>"                             
            />
           </div>
          </div>
         </show:showfield> 

         <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_SAIDA)%>">
          <%
              String rseDataSaida = null;
                rseDataSaida = (_registroServidor != null && _registroServidor.getRseDataSaida() != null ? _registroServidor.getRseDataSaida().toString() : "");
                if (!rseDataSaida.equals("")) {
                    rseDataSaida = DateHelper.reformat(rseDataSaida, "yyyy-MM-dd", LocaleHelper.getDatePattern());
                }
          %>
          <div class="row">
           <div class="form-group col-sm-6">
            <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_SAIDA)%>"><hl:message key="rotulo.servidor.data.saida" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_SAIDA)%>"/></label>
             <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_SAIDA)%>"
                        di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_SAIDA)%>"
                        type="text"
                        classe="form-control"
                        size='10'
                        value="<%=TextHelper.forHtmlAttribute(rseDataSaida)%>"
                        mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                        maxlength='10'
                        others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"                              
                        configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_SAIDA)%>"
                        placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>"                             
              />
           </div>
          </div>
         </show:showfield>
         
         <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_ULT_SALARIO)%>">
          <%
              String rseDataUltSalario = null;
                rseDataUltSalario = (_registroServidor != null && _registroServidor.getRseDataUltSalario() != null ? _registroServidor.getRseDataUltSalario().toString() : "");
                if (!rseDataUltSalario.equals("")) {
                    rseDataUltSalario = DateHelper.reformat(rseDataUltSalario, "yyyy-MM-dd", LocaleHelper.getDatePattern());
                }
          %>
          <div class="row">
           <div class="form-group col-sm-6">
            <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_ULT_SALARIO)%>"><hl:message key="rotulo.servidor.data.ult.salario" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_ULT_SALARIO)%>"/></label>
             <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_ULT_SALARIO)%>"
                        di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_ULT_SALARIO)%>"
                        type="text"
                        classe="form-control"
                        value="<%=TextHelper.forHtmlAttribute(rseDataUltSalario)%>"
                        size='10'
                        mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                        maxlength='10'
                        others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"                              
                        configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_ULT_SALARIO)%>"
                        placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>"                             
              />
           </div>
          </div>
         </show:showfield>
         <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PEDIDO_DEMISSAO)%>">
          <div class="row">
           <div class=" col-md-6 form-check mt-2" role="radiogroup" aria-labelledby="demissao">
              <div class="form-group my-0">
                <span id="demissao"><hl:message key="rotulo.servidor.pedido.demissao" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PEDIDO_DEMISSAO)%>"/></span>
              </div>
              <div class="form-check form-check-inline mt-2">
                <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PEDIDO_DEMISSAO)%>"
                               di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PEDIDO_DEMISSAO) + "_S"%>"
                               type="radio"
                               value="S"
                               checked="<%=String.valueOf(_registroServidor.getRsePedidoDemissao() != null && _registroServidor.getRsePedidoDemissao().equalsIgnoreCase(\"S\"))%>"
                               mask="#*10"
                               others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"
                               configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PEDIDO_DEMISSAO)%>"
                               classe="form-check-input ml-1"
                 />
                <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.sim"/>' for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PEDIDO_DEMISSAO) + "_S"%>"><hl:message key="rotulo.sim"/></label>
              </div>
               <div class="form-check-inline form-check">
                  <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PEDIDO_DEMISSAO)%>"
                               di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PEDIDO_DEMISSAO) + "_N"%>"
                               type="radio"
                               value="N"
                               checked="<%=String.valueOf(_registroServidor.getRsePedidoDemissao() != null && _registroServidor.getRsePedidoDemissao().equalsIgnoreCase(\"N\"))%>"
                               mask="#*10"
                               others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"
                               configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PEDIDO_DEMISSAO)%>"
                               classe="form-check-input ml-1"
                 />
                <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.nao"/>' for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PEDIDO_DEMISSAO) + "_N"%>"><hl:message key="rotulo.nao"/></label>
              </div>
           </div>
          </div>
         </show:showfield>
         
         <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_RETORNO)%>">
          <%
              String rseDataRetorno = null;
                rseDataRetorno = (_registroServidor != null && _registroServidor.getRseDataRetorno() != null ? _registroServidor.getRseDataRetorno().toString() : "");
                if (!rseDataRetorno.equals("")) {
                    rseDataRetorno = DateHelper.reformat(rseDataRetorno, "yyyy-MM-dd", LocaleHelper.getDatePattern());
                }
          %>
          <div class="row">
           <div class="form-group col-sm-6">
            <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_RETORNO)%>"><hl:message key="rotulo.servidor.data.retorno" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_RETORNO)%>"/></label>
             <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_RETORNO)%>"
                        di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_RETORNO)%>"
                        type="text"
                        classe="form-control"
                        value="<%=TextHelper.forHtmlAttribute(rseDataRetorno)%>"
                        size="10"
                        mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                        maxlength='10'
                        others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"                              
                        configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_RETORNO)%>"
                        placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>"
              />
           </div>
          </div>
         </show:showfield>
   <%
       if (ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SITUACAO, _responsavel_) ||
             ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_EXIBE_MOTIVO_BLOQUEIO, _responsavel_) ||
             ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_ADMISSAO, _responsavel_) ||
             ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PRAZO, _responsavel_) ||
             ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_SAIDA, _responsavel_) ||
             ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_ULT_SALARIO, _responsavel_) ||
             ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_RETORNO, _responsavel_) ||
             ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PEDIDO_DEMISSAO, _responsavel_)) {
   %>
        </fieldset>
   <%
       }
   %>
   
   <%
          if (ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SALARIO, _responsavel_) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PROVENTOS, _responsavel_) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DESCONTOS_COMPULSORIOS, _responsavel_) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DESCONTOS_FACULTATIVOS, _responsavel_) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_OUTROS_DESCONTOS, _responsavel_) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BASE_CALCULO, _responsavel_) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_CONTRACHEQUE, _responsavel_) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BANCO, _responsavel_) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AGENCIA, _responsavel_) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CONTA, _responsavel_) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BANCO_ALTERNATIVO, _responsavel_) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AGENCIA_ALTERNATIVA, _responsavel_) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CONTA_ALTERNATIVA, _responsavel_) ||
           (!_readOnly_ && (_responsavel_.temPermissao(CodedValues.FUN_ALT_MARGEM_CONSIGNAVEL) || _responsavel_.temPermissao(CodedValues.FUN_ALT_MARGEM_CONSIGNAVEL_MENOR)) && _margens != null && _margens.size() > 0)) {
      %>
        <fieldset>
         <h3 class="legend"><span><hl:message key="rotulo.servidor.subtitulo.informacoes.financeiras"/></span></h3>        
   <%
               }
           %>

      <%
          if (!_readOnly_ && (_responsavel_.temPermissao(CodedValues.FUN_ALT_MARGEM_CONSIGNAVEL) || 
            _responsavel_.temPermissao(CodedValues.FUN_ALT_MARGEM_CONSIGNAVEL_MENOR))) {  
          if (_margens != null && _margens.size() > 0) {
              MargemTO margemTO = null;
              Iterator<MargemTO> itMargens = _margens.iterator();
              while (itMargens.hasNext()) {
                  margemTO = itMargens.next();
                  String descricao = (margemTO.getMarDescricao() != null ? margemTO.getMarDescricao() : ApplicationResourcesHelper.getMessage("rotulo.margem.singular", _responsavel_));
                  String edtRegistroServidor_margem = "edtRegistroServidor_margem" +  margemTO.getMarCodigo().toString();
                  String rse_margem = (margemTO.getMrsMargem() != null ? NumberHelper.format(margemTO.getMrsMargem().doubleValue(), NumberHelper.getLang()) : "0,00");
                  String rse_margem_rest = (margemTO.getMrsMargemRest() != null ? NumberHelper.format(margemTO.getMrsMargemRest().doubleValue(), NumberHelper.getLang()) : "0,00");
                  String rse_margem_usada = (margemTO.getMrsMargemUsada() != null ? NumberHelper.format(margemTO.getMrsMargemUsada().doubleValue(), NumberHelper.getLang()) : "0,00"); ;
      %>
                   <div class="row">
                    <div class="form-group col-sm-4">
                     <label for="<%=TextHelper.forHtmlAttribute(edtRegistroServidor_margem)%>"><hl:message key="rotulo.servidor.margem" arg0="<%=descricao%>" fieldKey="<%=TextHelper.forHtmlAttribute(edtRegistroServidor_margem)%>"/></label>
                      <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(edtRegistroServidor_margem)%>"
                                 di="<%=TextHelper.forHtmlAttribute(edtRegistroServidor_margem)%>"
                                 type="text"
                                 classe="form-control"
                                 value="<%=TextHelper.forHtmlAttribute(rse_margem)%>"
                                 onFocus="SetarEventoMascara(this,'#F11',true);"
                                 onBlur="fout(this);ValidaMascara(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                                 mask="#F11"
                                 configKey="<%=TextHelper.forHtmlAttribute(edtRegistroServidor_margem)%>"
                                 placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.margem", _responsavel_, descricao)%>"                             
                       />
                    </div>
                    <div class="form-group col-sm-4">
                     <label for="<%=TextHelper.forHtmlAttribute("margemUsada"+edtRegistroServidor_margem)%>"><hl:message key="rotulo.servidor.margem.usada"/></label>
                      <hl:htmlinput name="<%=TextHelper.forHtmlAttribute("margemUsada"+edtRegistroServidor_margem)%>"
                                 di="<%=TextHelper.forHtmlAttribute("margemUsada"+edtRegistroServidor_margem)%>"
                                 type="text"
                                 classe="form-control"
                                 value="<%=TextHelper.forHtmlAttribute(rse_margem_usada)%>"
                                 others="disabled"        
                       />
                    </div>
                    <div class="form-group col-sm-4">
                     <label for="<%=TextHelper.forHtmlAttribute("margemRest"+edtRegistroServidor_margem)%>"><hl:message key="rotulo.servidor.margem.restante"/></label>
                      <hl:htmlinput name="<%=TextHelper.forHtmlAttribute("margemRest"+edtRegistroServidor_margem)%>"
                                 di="<%=TextHelper.forHtmlAttribute("margemRest"+edtRegistroServidor_margem)%>"
                                 type="text"
                                 classe="form-control"
                                 value="<%=TextHelper.forHtmlAttribute(rse_margem_rest)%>"
                                 others="disabled"        
                       />
                    </div>
                   </div>
      <%
          }
          }
               }
      %>

       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SALARIO)%>">
        <div class="row">
         <div class="form-group col-sm-6">
          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SALARIO)%>"><hl:message key="rotulo.servidor.salario" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SALARIO)%>"/></label>
           <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SALARIO)%>"
                      di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SALARIO)%>"
                      type="text"
                      classe="form-control"
                      value='<%=TextHelper.forHtml(_registroServidor.getRseSalario() != null ? NumberHelper.format(_registroServidor.getRseSalario().doubleValue(), NumberHelper.getLang()) : "")%>'
                      mask="#F11"
                      others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"
                      onFocus="SetarEventoMascara(this,'#F11',true);"
                      onBlur="fout(this);ValidaMascara(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                      configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SALARIO)%>"
                      placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.salario", _responsavel_)%>"                             
            />
         </div>
        </div>
       </show:showfield>
            <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MOTIVO_FALTA_MARGEM)%>">
                <div class="row">
                    <div class="form-group col-sm-6">
                        <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MOTIVO_FALTA_MARGEM)%>"><hl:message key="rotulo.servidor.subtitulo.motivo.falta.margem" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MOTIVO_FALTA_MARGEM)%>"/></label>
                        <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MOTIVO_FALTA_MARGEM)%>"
                                      di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MOTIVO_FALTA_MARGEM)%>"
                                      type="text"
                                      classe="form-control"
                                      value='<%=TextHelper.forHtml(_registroServidor.getRseMotivoFaltaMargem() != null ? _registroServidor.getRseMotivoFaltaMargem() : "")%>'
                                      others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"
                                      configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MOTIVO_FALTA_MARGEM)%>"
                                      placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.servidor.subtitulo.motivo.falta.margem", _responsavel_)%>"
                        />
                    </div>
                </div>
            </show:showfield>
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PROVENTOS)%>">
        <div class="row">
         <div class="form-group col-sm-6">
          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PROVENTOS)%>"><hl:message key="rotulo.servidor.proventos" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PROVENTOS)%>"/></label>
           <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PROVENTOS)%>"
                      di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PROVENTOS)%>"
                      type="text"
                      classe="form-control"
                      value='<%=TextHelper.forHtml(_registroServidor.getRseProventos() != null ? NumberHelper.format(_registroServidor.getRseProventos().doubleValue(), NumberHelper.getLang()) : "")%>'
                      mask="#F11"
                      others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"
                      onFocus="SetarEventoMascara(this,'#F11',true);"
                      onBlur="fout(this);ValidaMascara(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                      configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PROVENTOS)%>"
                      placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.proventos", _responsavel_)%>"                             
            />
         </div>
        </div>
       </show:showfield>
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DESCONTOS_COMPULSORIOS)%>">
        <div class="row">
         <div class="form-group col-sm-6">
          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DESCONTOS_COMPULSORIOS)%>"><hl:message key="rotulo.servidor.descontos.compulsorios" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DESCONTOS_COMPULSORIOS)%>"/></label>
           <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DESCONTOS_COMPULSORIOS)%>"
                      di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DESCONTOS_COMPULSORIOS)%>"
                      type="text"
                      classe="form-control"
                      onFocus="SetarEventoMascara(this,'#F11',true);"
                      onBlur="fout(this);ValidaMascara(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                      value='<%=TextHelper.forHtml(_registroServidor.getRseDescontosComp() != null ? NumberHelper.format(_registroServidor.getRseDescontosComp().doubleValue(), NumberHelper.getLang()) : "")%>'
                      mask="#F11"  
                      configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DESCONTOS_COMPULSORIOS)%>"
                      placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.descontos.compulsorios", _responsavel_)%>" 
           />                                                   
         </div>
        </div>
       </show:showfield>
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DESCONTOS_FACULTATIVOS)%>">
        <div class="row">
         <div class="form-group col-sm-6">
          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DESCONTOS_FACULTATIVOS)%>"><hl:message key="rotulo.servidor.descontos.facultativos" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DESCONTOS_FACULTATIVOS)%>"/></label>
           <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DESCONTOS_FACULTATIVOS)%>"
                      di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DESCONTOS_FACULTATIVOS)%>"
                      type="text"
                      classe="form-control"
                      onFocus="SetarEventoMascara(this,'#F11',true);"
                      onBlur="fout(this);ValidaMascara(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                      value='<%=TextHelper.forHtml(_registroServidor.getRseDescontosFacu() != null ? NumberHelper.format(_registroServidor.getRseDescontosFacu().doubleValue(), NumberHelper.getLang()) : "")%>'
                      mask="#F11"  
                      configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DESCONTOS_FACULTATIVOS)%>"
                      placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.descontos.facultativos", _responsavel_)%>"
            />
         </div>
        </div>
       </show:showfield>
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_OUTROS_DESCONTOS)%>">
        <div class="row">
         <div class="form-group col-sm-6">
          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_OUTROS_DESCONTOS)%>"><hl:message key="rotulo.servidor.outros.descontos" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_OUTROS_DESCONTOS)%>"/></label>
           <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_OUTROS_DESCONTOS)%>"
                      di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_OUTROS_DESCONTOS)%>"
                      type="text"
                      classe="form-control"
                      onFocus="SetarEventoMascara(this,'#F11',true);"
                      onBlur="fout(this);ValidaMascara(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                      value='<%=TextHelper.forHtml(_registroServidor.getRseOutrosDescontos() != null ? NumberHelper.format(_registroServidor.getRseOutrosDescontos().doubleValue(), NumberHelper.getLang()) : "")%>'
                      mask="#F11"  
                      configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_OUTROS_DESCONTOS)%>"
                      placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.outros.descontos", _responsavel_)%>"
           />
         </div>
        </div>
       </show:showfield>
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BASE_CALCULO)%>">
        <div class="row">
         <div class="form-group col-sm-6">
          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BASE_CALCULO)%>"><hl:message key="rotulo.servidor.base.calculo" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BASE_CALCULO)%>"/></label>
          <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BASE_CALCULO)%>"
                        di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BASE_CALCULO)%>"
                        type="text"
                        classe="form-control"
                        onFocus="SetarEventoMascara(this,'#F11',true);"
                        onBlur="fout(this);ValidaMascara(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                        value='<%=TextHelper.forHtml(_registroServidor.getRseBaseCalculo() != null ? NumberHelper.format(_registroServidor.getRseBaseCalculo().doubleValue(), NumberHelper.getLang()) : "")%>'
                        mask="#F11"  
                        configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BASE_CALCULO)%>"
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.base.calculo", _responsavel_)%>"
          />
         </div>
        </div>       
       </show:showfield>
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_CONTRACHEQUE)%>">
        <%
            String rseDataContracheque = null;
            rseDataContracheque = (_registroServidor != null && _registroServidor.getRseDataContracheque() != null ? _registroServidor.getRseDataContracheque().toString() : "");
            if (!rseDataContracheque.equals("")) {
                rseDataContracheque = DateHelper.reformat(rseDataContracheque, "yyyy-MM-dd", LocaleHelper.getDatePattern());
            }
        %>
        <div class="row">
         <div class="form-group col-sm-6">
          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_CONTRACHEQUE)%>"><hl:message key="rotulo.servidor.data.contracheque" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_CONTRACHEQUE)%>"/></label>
          <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_CONTRACHEQUE)%>"
                        di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_CONTRACHEQUE)%>"
                        type="text"
                        classe="form-control"
                        value="<%=TextHelper.forHtmlAttribute(rseDataContracheque)%>"
                        mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                        others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"
                        configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_CONTRACHEQUE)%>"
                        placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>"
           />
         </div>
        </div>
       </show:showfield>
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BANCO)%>">
        <div class="row">
         <div class="form-group col-sm-4">
          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BANCO)%>"><hl:message key="rotulo.servidor.codigo.banco" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BANCO)%>"/></label>
          <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BANCO)%>"
                        di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BANCO)%>"
                        type="text" 
                        value="<%=TextHelper.forHtmlAttribute(_registroServidor.getRseBancoSal() != null ? _registroServidor.getRseBancoSal().toString() : "" )%>" 
                        classe="form-control" 
                        others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"
                        configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BANCO)%>"
                        onFocus="SetarEventoMascara(this,'#A8',true);" 
                        onBlur="<%=TextHelper.forJavaScript( "fout(this);ValidaMascara(this);if (!IsNulo(document.forms[0]." + FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BANCO + ")) {SelecionaComboBanco(document.forms[0].RSE_BANCOS, document.forms[0]." + FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BANCO + ".value, arrayBancos);}" )%>"
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.codigo.banco", _responsavel_)%>"
           />
         </div>
         <div class="form-group col-sm-6">
           <label for="RSE_BANCOS"><hl:message key="rotulo.servidor.banco" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BANCO)%>"/></label>
           <SELECT NAME="RSE_BANCOS" ID="RSE_BANCOS" CLASS="form-control form-select"
                   onChange="document.forms[0].<%=TextHelper.forJavaScript(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BANCO)%>.value = document.forms[0].RSE_BANCOS.value; "
                  <%if (_readOnly_ || ShowFieldHelper.isDisabled(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BANCO, _responsavel_)) {%> DISABLED <%}%>>
             <OPTION VALUE="" SELECTED><hl:message key="rotulo.campo.selecione"/></OPTION>
           </SELECT>
         </div>
        </div>       
       </show:showfield>
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AGENCIA)%>">
        <div class="row">
         <div class="form-group col-sm-6">
          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AGENCIA)%>"><hl:message key="rotulo.servidor.codigo.agencia" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AGENCIA)%>"/></label>
          <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AGENCIA)%>"
                        di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AGENCIA)%>"
                        type="text"
                        classe="form-control"
                        value='<%=TextHelper.forHtml(_registroServidor.getRseAgenciaSal() != null ? _registroServidor.getRseAgenciaSal().toString() : "" )%>'
                        mask="#*30"
                        others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"
                        configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AGENCIA)%>"
                        onFocus="SetarEventoMascara(this,'#D5',true);" 
                        onBlur="fout(this);ValidaMascara(this);"
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.codigo.agencia", _responsavel_)%>"
           />    
         </div>
        </div>
       </show:showfield>
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CONTA)%>">
        <div class="row">
         <div class="form-group col-sm-6">
          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CONTA)%>"><hl:message key="rotulo.servidor.codigo.conta" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CONTA)%>"/></label>
          <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CONTA)%>"
                        di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CONTA)%>"
                        type="text"
                        classe="form-control"
                        value='<%=TextHelper.forHtml(_registroServidor.getRseContaSal() != null ? _registroServidor.getRseContaSal().toString() : "" )%>'
                        mask="#*40"
                        others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"
                        onFocus="SetarEventoMascara(this,'#*40',true);" 
                        configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CONTA)%>"
                        onBlur="fout(this);ValidaMascara(this);"
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.codigo.conta", _responsavel_)%>"
           />
         </div>
        </div>
       </show:showfield> 
       
       
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BANCO_ALTERNATIVO)%>">
        <div class="row">
         <div class="form-group col-sm-4">
          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BANCO_ALTERNATIVO)%>"><hl:message key="rotulo.servidor.codigo.banco.alternativo" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BANCO_ALTERNATIVO)%>"/></label>
          <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BANCO_ALTERNATIVO)%>"
                        di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BANCO_ALTERNATIVO)%>"
                        type="text" 
                        value="<%=TextHelper.forHtmlAttribute(_registroServidor.getRseBancoSalAlternativo() != null ? _registroServidor.getRseBancoSalAlternativo().toString() : "" )%>" 
                        classe="form-control" 
                        others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"
                        configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BANCO_ALTERNATIVO)%>"
                        onFocus="SetarEventoMascara(this,'#A8',true);" 
                        onBlur="<%=TextHelper.forJavaScript( "fout(this);ValidaMascara(this);if (!IsNulo(document.forms[0]." + FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BANCO_ALTERNATIVO + ")) {SelecionaComboBanco(document.forms[0].RSE_BANCOS_2, document.forms[0]." + FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BANCO_ALTERNATIVO + ".value, arrayBancos);}" )%>"
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.banco.alternativo", _responsavel_)%>"
           />
         </div>
         <div class="form-group col-sm-6">
           <label for="RSE_BANCOS_2"><hl:message key="rotulo.servidor.banco" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BANCO)%>"/></label>
           <SELECT NAME="RSE_BANCOS_2" ID="RSE_BANCOS_2" CLASS="form-control form-select"
                   onChange="document.forms[0].<%=TextHelper.forJavaScript(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BANCO_ALTERNATIVO)%>.value = document.forms[0].RSE_BANCOS_2.value;"
                  <%if (_readOnly_ || ShowFieldHelper.isDisabled(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BANCO_ALTERNATIVO, _responsavel_)) {%> DISABLED <%}%>>
             <OPTION VALUE="" SELECTED><hl:message key="rotulo.campo.selecione"/></OPTION>
           </SELECT>
         </div>
        </div>       
       </show:showfield>
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AGENCIA_ALTERNATIVA)%>">
        <div class="row">
         <div class="form-group col-sm-6">
          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AGENCIA_ALTERNATIVA)%>"><hl:message key="rotulo.servidor.codigo.agencia.alternativo" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AGENCIA_ALTERNATIVA)%>"/></label>
          <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AGENCIA_ALTERNATIVA)%>"
                       di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AGENCIA_ALTERNATIVA)%>"
                       type="text"
                       classe="form-control"
                       value='<%=TextHelper.forHtml(_registroServidor.getRseAgenciaSalAlternativa() != null ? _registroServidor.getRseAgenciaSalAlternativa().toString() : "" )%>'
                       mask="#*30"
                       others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"
                       configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AGENCIA_ALTERNATIVA)%>"
                       onFocus="SetarEventoMascara(this,'#D5',true);" 
                       onBlur="fout(this);ValidaMascara(this);"
                       placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.codigo.agencia.alternativa", _responsavel_)%>"
          />  
         </div>
        </div>
       </show:showfield>
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CONTA_ALTERNATIVA)%>">
        <div class="row">
         <div class="form-group col-sm-6">
          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CONTA_ALTERNATIVA)%>"><hl:message key="rotulo.servidor.codigo.conta.alternativo" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CONTA_ALTERNATIVA)%>"/></label>
          <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CONTA_ALTERNATIVA)%>"
                        di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CONTA_ALTERNATIVA)%>"
                        type="text"
                        classe="form-control"
                        value='<%=TextHelper.forHtml(_registroServidor.getRseContaSalAlternativa() != null ? _registroServidor.getRseContaSalAlternativa().toString() : "" )%>'
                        mask="#*40"
                        others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"
                        configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CONTA_ALTERNATIVA)%>"
                        onFocus="SetarEventoMascara(this,'#*40',true);" 
                        onBlur="fout(this);ValidaMascara(this);"
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.codigo.conta.alternativa", _responsavel_)%>"
           />
         </div>
        </div>
       </show:showfield> 
       
   <%
               if (ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SALARIO, _responsavel_) ||
                ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PROVENTOS, _responsavel_) ||
                ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MOTIVO_FALTA_MARGEM, _responsavel_) ||
                ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DESCONTOS_COMPULSORIOS, _responsavel_) ||
                ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DESCONTOS_FACULTATIVOS, _responsavel_) ||
                ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_OUTROS_DESCONTOS, _responsavel_) ||
                ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BASE_CALCULO, _responsavel_) ||
                ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_CONTRACHEQUE, _responsavel_) ||
                ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BANCO, _responsavel_) ||
                ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AGENCIA, _responsavel_) ||
                ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CONTA, _responsavel_) ||
                ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BANCO_ALTERNATIVO, _responsavel_) ||
                ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AGENCIA_ALTERNATIVA, _responsavel_) ||
                ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CONTA_ALTERNATIVA, _responsavel_) ||
                (!_readOnly_ && (_responsavel_.temPermissao(CodedValues.FUN_ALT_MARGEM_CONSIGNAVEL) || _responsavel_.temPermissao(CodedValues.FUN_ALT_MARGEM_CONSIGNAVEL_MENOR)) && _margens != null && _margens.size() > 0)) {
           %>
        </fieldset>
    <%
        }
    %>
    
    <%
            if (ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CATEGORIA, _responsavel_) ||
                   ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CARGO, _responsavel_) ||
                   ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PADRAO, _responsavel_) ||
                   ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_VINCULO, _responsavel_) ||
                   ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_TIPO_REG_SERVIDOR, _responsavel_) ||
                   ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_POSTO, _responsavel_) ||
                   ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CAPACIDADE_CIVIL, _responsavel_) ||
                   ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ESTABILIZADO, _responsavel_) ||
                   ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CLT, _responsavel_) ||
                   ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ASSOCIADO, _responsavel_) ||
                   ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_FIM_ENGAJAMENTO, _responsavel_) ||
                   ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_LIMITE_PERMANENCIA, _responsavel_) ||
                   ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PRACA, _responsavel_) ||
                   ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AUDITORIA_TOTAL, _responsavel_) ||
                   ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BENEFICIARIO_FINAN_DV_CART, _responsavel_) ||
                   ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_OBSERVACAO, _responsavel_) ||
                   ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MARGEM_LIMITE_DESCONTO_FOLHA, _responsavel_)) {
        %>
        <fieldset>
         <h3 class="legend"><span><hl:message key="rotulo.servidor.subtitulo.informacoes.adicionais"/></span></h3>
    <%
        }
    %>
    
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CATEGORIA)%>">     
        <div class="row">
         <div class="form-group col-sm-6">
          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CATEGORIA)%>"><hl:message key="rotulo.servidor.categoria" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CATEGORIA)%>"/></label>
          <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CATEGORIA)%>"
                        di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CATEGORIA)%>"
                        type="text"
                        classe="form-control"
                        value="<%=TextHelper.forHtmlAttribute(_registroServidor.getRseTipo())%>"
                        mask="#*255"
                        others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"
                        configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CATEGORIA)%>"
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.categoria", _responsavel_)%>"
          />
         </div>
        </div>
       </show:showfield> 
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CARGO)%>">
          <%
              List cargos = (List) request.getAttribute("cargos");
          %>    
        <div class="row">
         <div class="form-group col-sm-6">
          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CARGO)%>"><hl:message key="rotulo.servidor.cargo" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CARGO)%>"/></label>
           <hl:htmlcombo
                     listName="cargos" 
                     name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CARGO)%>" 
                     fieldValue="<%=TextHelper.forHtmlAttribute(Columns.CRS_CODIGO)%>" 
                     fieldLabel="<%=(String)(Columns.CRS_IDENTIFICADOR + ";" + Columns.CRS_DESCRICAO)%>" 
                     notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", _responsavel_)%>"
                     selectedValue="<%=TextHelper.forHtmlAttribute(_registroServidor.getCrsCodigo())%>" 
                     configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CARGO)%>"
                     others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"
                     classe="form-control"
                     >
           </hl:htmlcombo>
          </div>
        </div>
       </show:showfield>
       
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PADRAO)%>">
             <%
                 List padrao = (List) request.getAttribute("padrao");
             %>        
             <div class="row">
              <div class="form-group col-sm-6">
               <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PADRAO)%>"><hl:message key="rotulo.servidor.padrao" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PADRAO)%>"/></label>
                <hl:htmlcombo
                     listName="padrao" 
                     name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PADRAO)%>"
                     di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PADRAO)%>" 
                     fieldValue="<%=TextHelper.forHtmlAttribute(Columns.PRS_CODIGO)%>" 
                     fieldLabel="<%=(String)(Columns.PRS_IDENTIFICADOR + ";" + Columns.PRS_DESCRICAO)%>" 
                     notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", _responsavel_)%>"
                     selectedValue="<%=TextHelper.forHtmlAttribute(_registroServidor.getPrsCodigo())%>" 
                     configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PADRAO)%>"
                     others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"
                     classe="form-control"
                     >
                </hl:htmlcombo>
              </div>
             </div>
       </show:showfield>
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_VINCULO)%>">
           <%
               List listaVincRegSer = (List) request.getAttribute("listaVincRegSer");
           %>                      
          <div class="row">
           <div class="form-group col-sm-6">
            <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_VINCULO)%>"><hl:message key="rotulo.servidor.vinculo" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_VINCULO)%>"/></label>
            <hl:htmlcombo
                     listName="listaVincRegSer" 
                     name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_VINCULO)%>"
                     di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_VINCULO)%>" 
                     fieldValue="<%=TextHelper.forHtmlAttribute(Columns.VRS_CODIGO)%>" 
                     fieldLabel="<%=TextHelper.forHtmlAttribute(Columns.VRS_DESCRICAO)%>" 
                     notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", _responsavel_)%>"
                     selectedValue="<%=TextHelper.forHtmlAttribute(_registroServidor.getVrsCodigo())%>" 
                     configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_VINCULO)%>"
                     others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"
                     classe="form-control"
                     >
            </hl:htmlcombo>
           </div>
         </div>
       </show:showfield>
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_TIPO_REG_SERVIDOR)%>">
         <%
             List listaTipoRegServidor = (List) request.getAttribute("listaTipoRegServidor");
         %>       
         <div class="row">
          <div class="form-group col-sm-6">
            <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_TIPO_REG_SERVIDOR)%>"><hl:message key="rotulo.servidor.tipo" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_TIPO_REG_SERVIDOR)%>"/></label>
            <hl:htmlcombo
                     listName="listaTipoRegServidor" 
                     name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_TIPO_REG_SERVIDOR)%>"
                     di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_TIPO_REG_SERVIDOR)%>" 
                     fieldValue="<%=TextHelper.forHtmlAttribute(Columns.TRS_CODIGO)%>" 
                     fieldLabel="<%=TextHelper.forHtmlAttribute(Columns.TRS_DESCRICAO)%>" 
                     notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", _responsavel_)%>"
                     selectedValue="<%=TextHelper.forHtmlAttribute(_registroServidor.getTrsCodigo())%>" 
                     configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_TIPO_REG_SERVIDOR)%>"
                     others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"
                     classe="form-control"
                     >
            </hl:htmlcombo>
           </div>
         </div>
       </show:showfield>
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_POSTO)%>">
         <%
             List listaPostoCodigo = (List) request.getAttribute("listaPostoCodigo");
         %>    
         <div class="row">
          <div class="form-group col-sm-6">
            <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_POSTO)%>"><hl:message key="rotulo.servidor.posto" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_POSTO)%>"/></label>
            <hl:htmlcombo
                     listName="listaPostoCodigo" 
                     name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_POSTO)%>"
                     di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_POSTO)%>" 
                     fieldValue="<%=TextHelper.forHtmlAttribute(Columns.POS_CODIGO)%>" 
                     fieldLabel="<%=TextHelper.forHtmlAttribute(Columns.POS_DESCRICAO)%>" 
                     notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", _responsavel_)%>"
                     selectedValue="<%=TextHelper.forHtmlAttribute(_registroServidor.getPosCodigo())%>" 
                     configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_POSTO)%>"
                     others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"
                     classe="form-control"
                     >
             </hl:htmlcombo>
            </div>
          </div>
       </show:showfield>
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CAPACIDADE_CIVIL)%>">
         <%
             List listaCapCivil = (List) request.getAttribute("listaCapCivil");
         %>
         <div class="row">
          <div class="form-group col-sm-6">
            <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CAPACIDADE_CIVIL)%>"><hl:message key="rotulo.servidor.capacidadeCivil" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CAPACIDADE_CIVIL)%>"/></label>
            <hl:htmlcombo
                     listName="listaCapCivil" 
                     name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CAPACIDADE_CIVIL)%>"
                     di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CAPACIDADE_CIVIL)%>" 
                     fieldValue="<%=TextHelper.forHtmlAttribute(Columns.CAP_CODIGO)%>" 
                     fieldLabel="<%=TextHelper.forHtmlAttribute(Columns.CAP_DESCRICAO)%>" 
                     notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", _responsavel_)%>"
                     selectedValue="<%=TextHelper.forHtmlAttribute(_registroServidor.getCapCodigo())%>" 
                     configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CAPACIDADE_CIVIL)%>"
                     others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"
                     classe="form-control"
                     >
            </hl:htmlcombo>
           </div>
         </div>
       </show:showfield>
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ESTABILIZADO)%>">
          <div class="row">
           <div class=" col-md-6 form-check mt-2" role="radiogroup" aria-labelledby="estabilizado">
             <div class="form-group my-0">
               <span id="estabilizado"><hl:message key="rotulo.servidor.estabilizado" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ESTABILIZADO)%>"/></span>
             </div>
             <div class="form-check form-check-inline mt-2">
               <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ESTABILIZADO)%>"
                              di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ESTABILIZADO) + "_S"%>"
                              type="radio"
                              value="S"
                              checked="<%=String.valueOf(_registroServidor.getRseEstabilizado() != null && _registroServidor.getRseEstabilizado().equalsIgnoreCase(\"S\"))%>"
                              mask="#*10"
                              others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"
                              configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ESTABILIZADO)%>"
                              onChange="<%="estabilidade();"%>"
                              classe="form-check-input ml-1"
                />
               <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.sim"/>' for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ESTABILIZADO) + "_S"%>"><hl:message key="rotulo.sim"/></label>
             </div>
               <div class="form-check-inline form-check">
                 <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ESTABILIZADO)%>"
                              di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ESTABILIZADO) + "_N"%>"
                              type="radio"
                              value="N"
                              checked="<%=String.valueOf(_registroServidor.getRseEstabilizado() != null && _registroServidor.getRseEstabilizado().equalsIgnoreCase(\"N\"))%>"
                              mask="#*10"
                              others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"
                              configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ESTABILIZADO)%>"
                              onChange="<%="estabilidade();"%>"
                              classe="form-check-input ml-1"
                />
               <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.nao"/>' for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ESTABILIZADO) + "_N"%>"><hl:message key="rotulo.nao"/></label>
             </div>
           </div>
          </div>
       </show:showfield>
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CLT)%>">
         <div class="row">
          <div class=" col-md-6 form-check mt-2" role="radiogroup" aria-labelledby="clt">
            <div class="form-group my-0">
              <span id="clt"><hl:message key="rotulo.servidor.clt" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CLT)%>"/></span>
            </div>
            <div class="form-check form-check-inline mt-2">
              <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CLT)%>"
                             di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CLT) + "_S"%>"
                             type="radio"
                             value="S"
                             checked="<%=String.valueOf(_registroServidor.getRseCLT() != null && _registroServidor.getRseCLT().equalsIgnoreCase(\"S\"))%>"
                             mask="#*10"
                             others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"
                             configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CLT)%>"
                             classe="form-check-input ml-1"
               />
              <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.sim"/>' for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CLT) + "_S"%>"><hl:message key="rotulo.sim"/></label>
            </div>
              <div class="form-check-inline form-check">
                <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CLT)%>"
                             di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CLT) + "_N"%>"
                             type="radio"
                             value="N"
                             checked="<%=String.valueOf(_registroServidor.getRseCLT() != null && _registroServidor.getRseCLT().equalsIgnoreCase(\"N\"))%>"
                             mask="#*10"
                             others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"
                             configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CLT)%>"
                             classe="form-check-input ml-1"
               />
              <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.nao"/>' for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CLT) + "_N"%>"><hl:message key="rotulo.nao"/></label>
            </div>
          </div>
         </div>
       </show:showfield>
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ASSOCIADO)%>">
         <div class="row">
          <div class=" col-md-6 form-check mt-2" role="radiogroup" aria-labelledby="associado">
            <div class="form-group my-0">
              <span id="associado"><hl:message key="rotulo.servidor.associado" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ASSOCIADO)%>"/></span>
            </div>
            <div class="form-check form-check-inline mt-2">
              <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ASSOCIADO)%>"
                             di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ASSOCIADO) + "_S"%>"
                             type="radio"
                             value="S"
                             checked="<%=String.valueOf(_registroServidor.getRseAssociado() != null && _registroServidor.getRseAssociado().equalsIgnoreCase(\"S\"))%>"
                             mask="#*10"
                             others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"
                             configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ASSOCIADO)%>"
                             classe="form-check-input ml-1"
               />
              <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.sim"/>' for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ASSOCIADO) + "_S"%>"><hl:message key="rotulo.sim"/></label>
            </div>
              <div class="form-check-inline form-check">
              <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ASSOCIADO)%>"
                             di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ASSOCIADO) + "_N"%>"
                             type="radio"
                             value="N"
                             checked="<%=String.valueOf(_registroServidor.getRseAssociado() != null && _registroServidor.getRseAssociado().equalsIgnoreCase(\"N\"))%>"
                             mask="#*10"
                             others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"
                             configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ASSOCIADO)%>"
                             classe="form-check-input ml-1"
               />
              <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.nao"/>' for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ASSOCIADO) + "_N"%>"><hl:message key="rotulo.nao"/></label>
            </div>
          </div>
         </div>
       </show:showfield>
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_FIM_ENGAJAMENTO)%>">
       <%
           String rseDataFimEngajamento = null;
              rseDataFimEngajamento = (_registroServidor != null && _registroServidor.getRseDataFimEngajamento() != null ? _registroServidor.getRseDataFimEngajamento().toString() : "");
              if (!rseDataFimEngajamento.equals("")) {
                  rseDataFimEngajamento = DateHelper.reformat(rseDataFimEngajamento, "yyyy-MM-dd", LocaleHelper.getDatePattern());
              }
       %>
        <div class="row">
         <div class="form-group col-sm-6">
          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_FIM_ENGAJAMENTO)%>"><hl:message key="rotulo.servidor.engajado" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_FIM_ENGAJAMENTO)%>"/></label>
          <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_FIM_ENGAJAMENTO)%>"
                        di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_FIM_ENGAJAMENTO)%>"
                        type="text"
                        classe="form-control"
                        value="<%=TextHelper.forHtmlAttribute(rseDataFimEngajamento)%>"
                        mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                        others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"
                        configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_FIM_ENGAJAMENTO)%>" 
                        placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>"
          />
         </div>
        </div>
       </show:showfield>
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_LIMITE_PERMANENCIA)%>">
       <%
           String rseDataLimitePermanencia = null;
              rseDataLimitePermanencia = (_registroServidor != null && _registroServidor.getRseDataLimitePermanencia() != null ? _registroServidor.getRseDataLimitePermanencia().toString() : "");
              if (!rseDataLimitePermanencia.equals("")) {
                  rseDataLimitePermanencia = DateHelper.reformat(rseDataLimitePermanencia, "yyyy-MM-dd", LocaleHelper.getDatePattern());
              }
       %>
        <div class="row">
         <div class="form-group col-sm-6">
          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_LIMITE_PERMANENCIA)%>"><hl:message key="rotulo.servidor.dataLimitePermanencia" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_LIMITE_PERMANENCIA)%>"/></label>
          <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_LIMITE_PERMANENCIA)%>"
                        di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_LIMITE_PERMANENCIA)%>"
                        type="text"
                        classe="form-control"
                        value="<%=TextHelper.forHtmlAttribute(rseDataLimitePermanencia)%>"
                        mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                        others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"
                        configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_LIMITE_PERMANENCIA)%>" 
                        placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>"
          />
         </div>
        </div>
       </show:showfield>
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PRACA)%>">    
          <%
                  String others = " onFocus=\"SetarEventoMascara(this,'#*65000',true);\" onBlur=\"fout(this);ValidaMascara(this);\"";
                  String rsePraca = _registroServidor.getRsePraca() !=  null ? _registroServidor.getRsePraca().toString() : "";
                  rsePraca = rsePraca.replaceAll(",|;", "\n");
              %>
          <div class="row">
           <div class="form-group col-sm-6">
            <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PRACA)%>"><hl:message key="rotulo.servidor.praca" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PRACA)%>"/></label>
               <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PRACA)%>"
                             di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PRACA)%>"
                             type="textarea"
                             classe="form-control"
                             value='<%=TextHelper.forHtml(rsePraca)%>'
                             others="<%=TextHelper.forHtmlAttribute((_readOnly_ ? "disabled" : "") + others)%>"
                             configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PRACA)%>"
                             rows="6"
                             placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.praca", _responsavel_)%>"                             
               />
            </div>
          </div>
       </show:showfield>
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AUDITORIA_TOTAL)%>">
         <div class="row">
          <div class=" col-md-6 form-check mt-2" role="radiogroup" aria-labelledby="auditoria_total">
            <div class="form-group my-0">
              <span id="auditoria_total"><hl:message key="rotulo.editar.registroservidor.rse_auditoria_total" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AUDITORIA_TOTAL)%>"/></span>
            </div>
            <div class="form-check form-check-inline mt-2">
              <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AUDITORIA_TOTAL)%>"
                             di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AUDITORIA_TOTAL) + "_S"%>"
                             type="radio"
                             value="S"
                             checked="<%=String.valueOf(_registroServidor.getRseAuditoriaTotal() != null && _registroServidor.getRseAuditoriaTotal().equalsIgnoreCase(\"S\"))%>"
                             mask="#*10"
                             others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"
                             configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AUDITORIA_TOTAL)%>"
                             classe="form-check-input ml-1"
               />
              <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.sim"/>' for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AUDITORIA_TOTAL) + "_S"%>"><hl:message key="rotulo.sim"/></label>
            </div>
              <div class="form-check form-check-inline mt-2">
                <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AUDITORIA_TOTAL)%>"
                             di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AUDITORIA_TOTAL) + "_N"%>"
                             type="radio"
                             value="N"
                             checked="<%=String.valueOf(_registroServidor.getRseAuditoriaTotal() != null && _registroServidor.getRseAuditoriaTotal().equalsIgnoreCase(\"N\"))%>"
                             mask="#*10"
                             others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"
                             configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AUDITORIA_TOTAL)%>"
                             classe="form-check-input ml-1"
               />
              <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.sim"/>' for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AUDITORIA_TOTAL) + "_N"%>"><hl:message key="rotulo.nao"/></label>
            </div>
          </div>
         </div>
       </show:showfield>
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BENEFICIARIO_FINAN_DV_CART)%>">       
         <%
                    if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_FINANC_DIVIDA_CARTAO, CodedValues.TPC_SIM, _responsavel_)) {
                %>
         <div class="row">
          <div class=" col-md-6 form-check mt-2" role="radiogroup" aria-labelledby="beneficiarioFinanDvCart">
            <div class="form-group my-0">
              <span id="beneficiarioFinanDvCart"><hl:message key="rotulo.servidor.beneficiarioFinanDvCart" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BENEFICIARIO_FINAN_DV_CART)%>"/></span>
            </div>
            <div class="form-check form-check-inline mt-2">
              <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BENEFICIARIO_FINAN_DV_CART)%>"
                             di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BENEFICIARIO_FINAN_DV_CART) + "_S"%>"
                             type="radio"
                             value="S"
                             checked="<%=String.valueOf(_registroServidor.getRseBeneficiarioFinanDvCart() != null && _registroServidor.getRseBeneficiarioFinanDvCart().equalsIgnoreCase(\"S\"))%>"
                             mask="#*10"
                             others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"
                             configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BENEFICIARIO_FINAN_DV_CART)%>"
                             classe="form-check-input ml-1"
               />
              <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.sim"/>' for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BENEFICIARIO_FINAN_DV_CART) + "_S"%>"><hl:message key="rotulo.sim"/></label>
            </div>
              <div class="form-check-inline form-check">
                <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BENEFICIARIO_FINAN_DV_CART)%>"
                             di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BENEFICIARIO_FINAN_DV_CART) + "_N"%>"
                             type="radio"
                             value="N"
                             checked="<%=String.valueOf(_registroServidor.getRseBeneficiarioFinanDvCart() != null && _registroServidor.getRseBeneficiarioFinanDvCart().equalsIgnoreCase(\"N\"))%>"
                             mask="#*10"
                             others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"
                             configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BENEFICIARIO_FINAN_DV_CART)%>"
                             classe="form-check-input ml-1"
               />
              <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.nao"/>' for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BENEFICIARIO_FINAN_DV_CART) + "_N"%>"><hl:message key="rotulo.nao"/></label>
            </div>
          </div>
         </div>
         <%
             }
         %>
       </show:showfield>
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_OBSERVACAO)%>">    
          <%
                  String others = " onFocus=\"SetarEventoMascara(this,'#*65000',true);\" onBlur=\"fout(this);ValidaMascara(this);\"";
              %>
          <div class="row">
           <div class="form-group col-sm-6">
            <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_OBSERVACAO)%>"><hl:message key="rotulo.servidor.obs" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_OBSERVACAO)%>"/></label>
            <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_OBSERVACAO)%>"
                          di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_OBSERVACAO)%>"
                          type="textarea"
                          classe="form-control"
                          value="<%=TextHelper.forHtml(_registroServidor.getRseObs() !=  null ? _registroServidor.getRseObs().toString() : "")%>"
                          others="<%=TextHelper.forHtmlAttribute((_readOnly_ ? "disabled" : "") + others)%>"
                          configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_OBSERVACAO)%>" 
                          rows="6"
                          placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.obs", _responsavel_)%>"
            />
           </div>
          </div>
       </show:showfield>
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MARGEM_LIMITE_DESCONTO_FOLHA)%>">          
           <div class="row">
            <div class="form-group col-sm-6">
             <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MARGEM_LIMITE_DESCONTO_FOLHA)%>"><hl:message key="rotulo.servidor.margem.limite.desconto.folha" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MARGEM_LIMITE_DESCONTO_FOLHA)%>"/></label>
             <hl:htmlcombo
                 listName="margensRaiz" 
                 name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MARGEM_LIMITE_DESCONTO_FOLHA)%>" 
                 fieldValue="<%=TextHelper.forHtmlAttribute(Columns.MAR_CODIGO)%>" 
                 fieldLabel="<%=TextHelper.forHtmlAttribute(Columns.MAR_DESCRICAO)%>" 
                 notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", _responsavel_)%>"
                 selectedValue="<%=TextHelper.forHtmlAttribute(_registroServidor.getMarCodigo())%>" 
                 configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MARGEM_LIMITE_DESCONTO_FOLHA)%>"
                 others="<%=TextHelper.forHtmlAttribute(_readOnly_ ? "disabled" : "")%>"
                 classe="form-control"
                 >
            </hl:htmlcombo> 
           </div>
          </div>
         </show:showfield>
       
    <% if (ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CATEGORIA, _responsavel_) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CARGO, _responsavel_) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PADRAO, _responsavel_) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_VINCULO, _responsavel_) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_TIPO_REG_SERVIDOR, _responsavel_) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_POSTO, _responsavel_) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CAPACIDADE_CIVIL, _responsavel_) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ESTABILIZADO, _responsavel_) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CLT, _responsavel_) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ASSOCIADO, _responsavel_) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_FIM_ENGAJAMENTO, _responsavel_) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_LIMITE_PERMANENCIA, _responsavel_) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PRACA, _responsavel_) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AUDITORIA_TOTAL, _responsavel_) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BENEFICIARIO_FINAN_DV_CART, _responsavel_) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_OBSERVACAO, _responsavel_) ||
           ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MARGEM_LIMITE_DESCONTO_FOLHA, _responsavel_)) { %>
        </fieldset>    
    <% } %>
    
    <% if (!_readOnly_) { %> 
         <fieldset>
          <div class="legend"></div>
          <%-- Utiliza a tag library EfetivaAcaoMotivoOperacaoTag.java para exibir os dados do Tipo de Motivo da Operação --%>
          <hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="" operacaoRegistroServidor="true" tmoSempreObrigatorio="false" inputSizeCSS="col-sm-12"/>
          <hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="" operacaoRegistroServidor="true" tmoSempreObrigatorio="false" inputSizeCSS="col-sm-12" scriptOnly="true"/>
          <%-- Fim dos dados do Motivo da Operação --%>
         </fieldset>
    <% } %>
      
<script language="JavaScript" type="text/JavaScript">
var arrayBancos = <%=(String)JspHelper.geraArrayBancos(_responsavel_)%>;

function rseFormLoad() {
  <% if (!_readOnly_) { %>
       <%       
       String rseBancoSal = "";
       String rseBancoSalAlt = "";
       try {
        // Remove os zeros à esquerda, e garante que o valor será
        // um numero inteiro
         rseBancoSal = new Integer(_registroServidor.getRseBancoSal()).toString();
         rseBancoSalAlt = new Integer(_registroServidor.getRseBancoSalAlternativo()).toString();
       } catch (NumberFormatException ex) {
       }
       %>
       var banco = '<%=TextHelper.forJavaScriptBlock(rseBancoSal)%>';
       var bancoAlt = '<%=TextHelper.forJavaScriptBlock(rseBancoSalAlt)%>';
       if (document.forms[0].RSE_BANCOS != null) {
         AtualizaFiltraComboExt(document.forms[0].RSE_BANCOS, arrayBancos, '', '', banco, false, false, '', '');
       }
       if (document.forms[0].RSE_BANCOS_2 != null) {
         AtualizaFiltraComboExt(document.forms[0].RSE_BANCOS_2, arrayBancos, '', '', bancoAlt, false, false, '', '');
       }      
       estabilidade();
  <% } %>
  limpaDesabilitaCampo();
  }

function estabilidade() {
    var estabilizado = getCheckedRadio('form1', '<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ESTABILIZADO)%>');
    var dataFimEngajamento = document.forms[0].<%=TextHelper.forHtmlAttribute( FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_FIM_ENGAJAMENTO)%>;
    var dataLimitePermanencia = document.forms[0].<%=TextHelper.forHtmlAttribute( FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_LIMITE_PERMANENCIA)%>;
    
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

function enviar() {
<%if (ParamSist.paramEquals(CodedValues.TPC_EXIGE_DETALHES_EXCL_BLOQ_SER, CodedValues.TPC_SIM, _responsavel_)) {%>
   if (typeof checkCamposExclusao == 'function') {
     if (!checkCamposExclusao()) {
       return false;
     }
   }
<%}%>

<%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MATRICULA, _responsavel_)) {%>
     var matriculaField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MATRICULA)%>;
     if (matriculaField.value == null || matriculaField.value == '') {
         alert('<hl:message key="mensagem.informe.registro.servidor.matricula"/>');
         matriculaField.focus();
         return false;
     }
<%}%>

<%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MATRICULA_INSTITUCIONAL, _responsavel_)) {%>
     var matriculaInstField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MATRICULA_INSTITUCIONAL)%>;
     if (matriculaInstField.value == null || matriculaInstField.value == '') {
         alert('<hl:message key="mensagem.informe.registro.servidor.matricula.institucional"/>');
         matriculaInstField.focus();
         return false;
     }
<%}%>

<%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ORGAO, _responsavel_)) {%>
     var orgaoField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ORGAO)%>;
     if (orgaoField.value == null || orgaoField.value == '') {
         alert('<hl:message key="mensagem.informe.registro.servidor.orgao"/>');
         orgaoField.focus();
         return false;
     }
<%}%>

<%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SUB_ORGAO, _responsavel_)) {%>
     var subOrgaoField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SUB_ORGAO)%>;
     if (subOrgaoField.value == null || subOrgaoField.value == '') {
         alert('<hl:message key="mensagem.informe.registro.servidor.sub.orgao"/>');
         subOrgaoField.focus();
         return false;
     }
<%}%>

<%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_UNIDADE, _responsavel_)) {%>
    var unidadeField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_UNIDADE)%>;
    if (unidadeField.value == null || unidadeField.value == '') {
         alert('<hl:message key="mensagem.informe.registro.servidor.unidade"/>');
         unidadeField.focus();
         return false;
    }
<%}%>

<%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MUNICIPIO_LOTACAO, _responsavel_)) {%>
    var municipioField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MUNICIPIO_LOTACAO)%>;
    if (municipioField.value == null || municipioField.value == '') {
         alert('<hl:message key="mensagem.informe.registro.servidor.municipio"/>');
         municipioField.focus();
         return false;
    }
<%}%>

<%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SITUACAO, _responsavel_)) {%>
    var situacaoField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SITUACAO)%>;
    if (situacaoField.value == null || situacaoField.value == '') {
         alert('<hl:message key="mensagem.informe.registro.servidor.situacao"/>');
         situacaoField.focus();
         return false;
    }
<%}%>

<%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_ADMISSAO, _responsavel_)) {%>
   var dtAdmissaoField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_ADMISSAO)%>;
   if (dtAdmissaoField.value == null || dtAdmissaoField.value == '') {
     alert('<hl:message key="mensagem.informe.registro.servidor.data.admissao"/>');
     dtAdmissaoField.focus();
     return false;
   }
<%}%>

<%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PRAZO, _responsavel_)) {%>
    var prazoField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PRAZO)%>;
    if (prazoField.value == null || prazoField.value == '') {
         alert('<hl:message key="mensagem.informe.registro.servidor.prazo"/>');
         prazoField.focus();
         return false;
    }
<%}%>

<%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_SAIDA, _responsavel_)) {%>
    var dtSaidaField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_SAIDA)%>;
    if (dtSaidaField.value == null || dtSaidaField.value == '') {
         alert('<hl:message key="mensagem.erro.rse.informe.data.saida"/>');
         dtSaidaField.focus();
         return false;
    }
<%}%>

<%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_ULT_SALARIO, _responsavel_)) {%>
    var dtUltSalField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_ULT_SALARIO)%>;
    if (dtUltSalField.value == null || dtUltSalField.value == '') {
         alert('<hl:message key="mensagem.erro.rse.informe.data.ult.salario"/>');
         dtUltSalField.focus();
         return false;
    }
<%}%>   

<%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PEDIDO_DEMISSAO, _responsavel_)) {%>
    var demissaoField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PEDIDO_DEMISSAO)%>;
    if (demissaoField.value == null || demissaoField.value == '') {
         alert('<hl:message key="mensagem.erro.pedido.demissao.obrigatorio"/>');
         demissaoField[0].focus();
         return false;
    }
<%}%>

<%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_RETORNO, _responsavel_)) {%>
    var dtRetornoField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_RETORNO)%>;
    if (dtRetornoField.value == null || dtRetornoField.value == '') {
         alert('<hl:message key="mensagem.erro.rse.informe.data.retorno"/>');
         dtRetornoField.focus();
         return false;
    }
<%}%>

<%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MARGEM_1, _responsavel_)) {%>
    var margem1Field = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MARGEM_1)%>;
    if (margem1Field.value == null || margem1Field.value == '') {
         alert('<hl:message key="mensagem.informe.rse.margem"/>');
         margem1Field.focus();
         return false;
    }
<%}%>

<%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MARGEM_2, _responsavel_)) {%>
    var margem2Field = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MARGEM_2)%>;
    if (margem2Field.value == null || margem2Field.value == '') {
         alert('<hl:message key="mensagem.informe.rse.margem"/>');
         margem2Field.focus();
         return false;
    }
<%}%>

<%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MARGEM_3, _responsavel_)) {%>
    var margem3Field = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MARGEM_3)%>;
    if (margem3Field.value == null || margem3Field.value == '') {
         alert('<hl:message key="mensagem.informe.rse.margem"/>');
         margem3Field.focus();
         return false;
    }
<%}%>

<%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SALARIO, _responsavel_)) {%>
    var salarioField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SALARIO)%>;
    if (salarioField.value == null || salarioField.value == '') {
         alert('<hl:message key="mensagem.informe.registro.servidor.salario"/>');
         salarioField.focus();
         return false;
    }
<%}%>

    <%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MOTIVO_FALTA_MARGEM, _responsavel_)) {%>
    var motivoFaltaMargemField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MOTIVO_FALTA_MARGEM)%>;
    if (motivoFaltaMargemField.value == null || motivoFaltaMargemField.value == '') {
        alert('<hl:message key="mensagem.informe.registro.servidor.salario"/>');
        motivoFaltaMargemField.focus();
        return false;
    }
    <%}%>

<%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PROVENTOS, _responsavel_)) {%>
    var proventosField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PROVENTOS)%>;
    if (proventosField.value == null || proventosField.value == '') {
         alert('<hl:message key="mensagem.informe.registro.servidor.proventos"/>');
         proventosField.focus();
         return false;
    }
<%}%>

<%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DESCONTOS_COMPULSORIOS, _responsavel_)) {%>
    var descCompField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DESCONTOS_COMPULSORIOS)%>;
    if (descCompField.value == null || descCompField.value == '') {
         alert('<hl:message key="mensagem.informe.registro.servidor.descontos.compulsorios"/>');
         descCompField.focus();
         return false;
    }
<%}%>
   
<%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DESCONTOS_FACULTATIVOS, _responsavel_)) {%>
    var descFacuField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DESCONTOS_FACULTATIVOS)%>;
    if (descFacuField.value == null || descFacuField.value == '') {
         alert('<hl:message key="mensagem.informe.registro.servidor.descontos.facultativos"/>');
         descFacuField.focus();
         return false;
    }
<%}%>

<%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_OUTROS_DESCONTOS, _responsavel_)) {%>
    var outrosDescField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_OUTROS_DESCONTOS)%>;
    if (outrosDescField.value == null || outrosDescField.value == '') {
         alert('<hl:message key="mensagem.informe.registro.servidor.outros.descontos"/>');
         outrosDescField.focus();
         return false;
    }
<%}%>

<%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BASE_CALCULO, _responsavel_)) {%>
    var baseCalcField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BASE_CALCULO)%>;
    if (baseCalcField.value == null || baseCalcField.value == '') {
         alert('<hl:message key="mensagem.informe.registro.servidor.base.calculo"/>');
         baseCalcField.focus();
         return false;
    }
<%}%>

<%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_CONTRACHEQUE, _responsavel_)) {%>
    var dtContraField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_CONTRACHEQUE)%>;
    if (dtContraField.value == null || dtContraField.value == '') {
         alert('<hl:message key="mensagem.informe.registro.servidor.data.contracheque"/>');
         dtContraField.focus();
         return false;
    }
<%}%>

<%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BANCO, _responsavel_)) {%>
    var bancoField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BANCO)%>;
    if (bancoField.value == null || bancoField.value == '') {
         alert('<hl:message key="mensagem.informe.registro.servidor.banco"/>');
         bancoField.focus();
         return false;
    }
<%}%>

<%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AGENCIA, _responsavel_)) {%>
    var agenciaField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AGENCIA)%>;
    if (agenciaField.value == null || agenciaField.value == '') {
         alert('<hl:message key="mensagem.informe.registro.servidor.agencia"/>');
         agenciaField.focus();
         return false;
    }
<%}%>

<%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CONTA, _responsavel_)) {%>
    var contaField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CONTA)%>;
    if (contaField.value == null || contaField.value == '') {
         alert('<hl:message key="mensagem.informe.registro.servidor.conta"/>');
         contaField.focus();
         return false;
    }
<%}%>

<%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BANCO_ALTERNATIVO, _responsavel_)) {%>
    var bancoAltField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BANCO_ALTERNATIVO)%>;
    if (bancoAltField.value == null || bancoAltField.value == '') {
         alert('<hl:message key="mensagem.informe.registro.servidor.banco.alternativo"/>');
         bancoAltField.focus();
         return false;
    }
<%}%>

<%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AGENCIA_ALTERNATIVA, _responsavel_)) {%>
    var agenciaAltField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AGENCIA_ALTERNATIVA)%>;
    if (agenciaAltField.value == null || agenciaAltField.value == '') {
         alert('<hl:message key="mensagem.informe.registro.servidor.agencia.alternativa"/>');
         agenciaAltField.focus();
         return false;
    }
<%}%>

<%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CONTA_ALTERNATIVA, _responsavel_)) {%>
    var contaAltField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CONTA_ALTERNATIVA)%>;
    if (contaAltField.value == null || contaAltField.value == '') {
         alert('<hl:message key="mensagem.informe.registro.servidor.conta.alternativa"/>');
         contaAltField.focus();
         return false;
    }
<%}%>

<%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CATEGORIA, _responsavel_)) {%>
    var categoriaField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CATEGORIA)%>;
    if (categoriaField.value == null || categoriaField.value == '') {
         alert('<hl:message key="mensagem.informe.registro.servidor.categoria"/>');
         categoriaField.focus();
         return false;
    }
 <%}%>
 
 <%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CARGO, _responsavel_)) {%>
     var cargoField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CARGO)%>;
     if (cargoField.value == null || cargoField.value == '') {
         alert('<hl:message key="mensagem.informe.registro.servidor.cargo"/>');
         cargoField.focus();
         return false;
     }
 <%}%>
 
 <%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PADRAO, _responsavel_)) {%>
     var padraoField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PADRAO)%>;
     if (padraoField.value == null || padraoField.value == '') {
         alert('<hl:message key="mensagem.informe.registro.servidor.padrao"/>');
         padraoField.focus();
         return false;
     }
 <%}%>
 
 <%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_VINCULO, _responsavel_)) {%>
     var vinculoField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_VINCULO)%>;
     if (vinculoField.value == null || vinculoField.value == '') {
         alert('<hl:message key="mensagem.informe.registro.servidor.vinculo"/>');
         vinculoField.focus();
         return false;
     }
 <%}%>
 
 <%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_TIPO_REG_SERVIDOR, _responsavel_)) {%>
     var tipoField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_TIPO_REG_SERVIDOR)%>;
     if (tipoField.value == null || tipoField.value == '') {
         alert('<hl:message key="mensagem.informe.registro.servidor.tipo"/>');
         tipoField.focus();
         return false;
     }
 <%}%>
 
 <%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_POSTO, _responsavel_)) {%>
     var postoField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_POSTO)%>;
     if (postoField.value == null || postoField.value == '') {
         alert('<hl:message key="mensagem.informe.registro.servidor.posto"/>');
         postoField.focus();
         return false;
     }
 <%}%>
 
 <%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CAPACIDADE_CIVIL, _responsavel_)) {%>
     var capacidadeField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CAPACIDADE_CIVIL)%>;
     if (capacidadeField.value == null || capacidadeField.value == '') {
         alert('<hl:message key="mensagem.informe.registro.servidor.capacidade.civil"/>');
         capacidadeField.focus();
         return false;
     }
 <%}%>    
 
 <%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ESTABILIZADO, _responsavel_)) {%>
     var estabilizadoField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ESTABILIZADO)%>;
     if (estabilizadoField.value == null || estabilizadoField.value == '') {
         alert('<hl:message key="mensagem.informe.registro.servidor.estabilizado"/>');
         estabilizadoField[0].focus();
         return false;
     }
 <%}%> 
 
 <%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CLT, _responsavel_)) {%>
     var cltField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CLT)%>;
     if (cltField.value == null || cltField.value == '') {
         alert('<hl:message key="mensagem.informe.registro.servidor.sindicalizado"/>');
         cltField[0].focus();
         return false;
     }
 <%}%> 
 
 <%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ASSOCIADO, _responsavel_)) {%>
     var associadoField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ASSOCIADO)%>;
     if (associadoField.value == null || associadoField.value == '') {
         alert('<hl:message key="mensagem.informe.registro.servidor.associado"/>');
         associadoField.focus();
         return false;
     }
 <%}%>
 
 <%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_FIM_ENGAJAMENTO, _responsavel_)) {%>
     var dtFimEngField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_FIM_ENGAJAMENTO)%>;
     if (dtFimEngField.value == null || dtFimEngField.value == '') {
         alert('<hl:message key="mensagem.informe.registro.servidor.engajado"/>');
         dtFimEngField.focus();
         return false;
     }
 <%}%>
 
 <%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_LIMITE_PERMANENCIA, _responsavel_)) {%>
     var dtLimPerField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_LIMITE_PERMANENCIA)%>;
     if (dtLimPerField.value == null || dtLimPerField.value == '') {
         alert('<hl:message key="mensagem.informe.registro.servidor.data.limite.permanencia"/>');
         dtLimPerField.focus();
         return false;
     }
 <%}%>
 
 <%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PRACA, _responsavel_)) {%>
     var pracaField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PRACA)%>;
     if (pracaField.value == null || pracaField.value == '') {
         alert('<hl:message key="mensagem.informe.registro.servidor.praca"/>');
         pracaField.focus();
         return false;
     }
 <%}%>
 
 <%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AUDITORIA_TOTAL, _responsavel_)) {%>
     var auditoriaField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AUDITORIA_TOTAL)%>;
     if (auditoriaField.value == null || auditoriaField.value == '') {
         alert('<hl:message key="mensagem.informe.registro.servidor.auditoria.total"/>');
         auditoriaField[0].focus();
         return false;
     }
 <%}%>
 
 <%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BENEFICIARIO_FINAN_DV_CART, _responsavel_)) {%>
     var beneficiarioField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BENEFICIARIO_FINAN_DV_CART)%>;
     if (beneficiarioField.value == null || beneficiarioField.value == '') {
         alert('<hl:message key="mensagem.informe.registro.servidor.divida.cartao"/>');
         beneficiarioField[0].focus();
         return false;
     }
 <%}%>
 
 <%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_OBSERVACAO, _responsavel_)) {%>
     var obsField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_OBSERVACAO)%>;
     if (obsField.value == null || obsField.value == '') {
         alert('<hl:message key="mensagem.informe.registro.servidor.obs"/>');
         obsField.focus();
         return false;
     }
 <%}%>

 <%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MARGEM_LIMITE_DESCONTO_FOLHA, _responsavel_)) {%>
     var margemLimiteField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MARGEM_LIMITE_DESCONTO_FOLHA)%>;
     if (margemLimiteField.value == null || margemLimiteField.value == '') {
       alert('<hl:message key="mensagem.informe.registro.servidor.margem.limite"/>');
       margemLimiteField.focus();
       return false;
     }
 <%}%>
 
 <%
 Boolean validarObsOperacao = (Boolean)request.getAttribute("validarObsOperacao");
 if (validarObsOperacao != null && validarObsOperacao) { 
  %>
  
   var obsField = f0.ADE_OBS;
   var motivoField = document.getElementById('TMO_CODIGO');
   var exigeObs = document.getElementById('exige_obs_' + motivoField.value);
   if (exigeObs.value == 'S' && (obsField.value == null || obsField.value.trim() == '')) {
       alert('<hl:message key="mensagem.informe.registro.servidor.obs.operacao"/>');
       obsField.focus();
       return false;
   }
 
 <%
 }
 %>
 
 var msgConfirmacao = '<%= TextHelper.forJavaScriptBlock(_msgConfirmacao) %>';
 var msgConfirmacaoAprovacao = '<%= TextHelper.forJavaScriptBlock(_msgConfirmacaoAprovacao) %>';
 var msgConfirmacaoExclusao = '<%= TextHelper.forJavaScriptBlock(_msgConfirmacaoExclusao) %>';

 var status = f0.<%= (String) FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SITUACAO %>;
 if (status != null) {
   if (status.value == <%="'" + CodedValues.SRS_EXCLUIDO + "'"%> || status.value == <%="'" + CodedValues.SRS_BLOQUEADO + "'"%> || status.value == <%="'" + CodedValues.SRS_FALECIDO + "'"%>) {
     msgConfirmacao = (msgConfirmacaoExclusao != '' ? msgConfirmacaoExclusao : msgConfirmacao);
   } else {
     msgConfirmacao = (msgConfirmacaoAprovacao != '' ? msgConfirmacaoAprovacao : msgConfirmacao);
   }
 }
 
 if (vf_edt_registro_servidor('<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SITUACAO)%>',
                              '<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MARGEM_1)%>', 
                              '<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MARGEM_2)%>', 
                              '<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MARGEM_3)%>') &&
                            confirm(msgConfirmacao)) {

      // Habilita os campos antes de fazer um submit
      enableAll();
      return true; 
    }

    return false;
}


<%if (ParamSist.paramEquals(CodedValues.TPC_EXIGE_DETALHES_EXCL_BLOQ_SER, CodedValues.TPC_SIM, _responsavel_)) {%>
  function checkCamposExclusao() {
     var status = f0.<%=TextHelper.forHtmlAttribute( FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SITUACAO)%>;     

     var dataSaida = null;
     var dataUltSalario = null;
     var pedidoDemissao = null;
     var dataRetorno = null;
     if (status != null && (status.value == <%="'" + CodedValues.SRS_EXCLUIDO + "'"%> || status.value == <%="'" + CodedValues.SRS_BLOQUEADO + "'"%>)) {
         dataSaida = f0.<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_SAIDA)%>;
         if (dataSaida == null ||  dataSaida.value == '') {
            alert('<hl:message key="mensagem.erro.rse.informe.data.saida"/>');
            dataSaida.focus();
            return false;  
         }
         
         dataUltSalario = f0.<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_ULT_SALARIO)%>;
         if (dataUltSalario == null ||  dataUltSalario.value == '') {
            alert('<hl:message key="mensagem.erro.rse.informe.data.ult.salario"/>');
            dataUltSalario.focus();
            return false;  
         }
         
     } 
     
     if (status != null && status.value == <%="'" + CodedValues.SRS_BLOQUEADO + "'"%>) {
         dataRetorno = f0.<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_RETORNO)%>;
         if (dataRetorno == null ||  dataRetorno.value == '') {
            alert('<hl:message key="mensagem.erro.rse.informe.data.retorno"/>');
            dataRetorno.focus();            
            return false;  
         }

         if (verificaData(dataRetorno.value)) {
            var now = new Date();
            now.setHours(0);
            now.setMinutes(0);
            now.setSeconds(0);  
            now.setMilliseconds(0);  
            var campos = obtemPartesData(new String(dataRetorno.value));
            var then = new Date(campos[2], campos[1] - 1, campos[0]);
            if (then.getTime() < now.getTime()) {
              alert('<hl:message key="mensagem.erro.rse.data.retorno.menor.atual"/>');
              dataRetorno.focus();          
              return false;
            }
         } else {
            dataRetorno.focus();            
            return false;
         }
     } 
     
     if (status != null && status.value == <%="'" + CodedValues.SRS_EXCLUIDO + "'"%>) {
         pedidoDemissao = f0.<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PEDIDO_DEMISSAO)%>;
         if (pedidoDemissao == null ||  pedidoDemissao.value == '') {
            alert('<hl:message key="mensagem.erro.rse.informe.servidor.demitiuse"/>');          
            return false;  
         }
     }

     if (status != null && status.value != f0.srsOriginal.value) {
         var tmoCodigo = document.getElementById("TMO_CODIGO");
         if (tmoCodigo == null || tmoCodigo.value == "") {
             alert('<hl:message key="mensagem.erro.informacao.motivo.operacao.ausente"/>');
             tmoCodigo.focus();         
            return false;  
         }

         var tmoObs = document.getElementById("ADE_OBS");
         var motivoField = document.getElementById('TMO_CODIGO');
         var exigeObs = document.getElementById('exige_obs_' + motivoField.value);
         if (exigeObs.value == 'S' && (tmoObs == null || tmoObs.value == "")) {
             alert('<hl:message key="mensagem.erro.obs.motivo.operacao.ausente"/>');
             tmoObs.focus();            
            return false;  
         }
     } 
     
     return true;
  }
<%} %>

function limpaCamposData(srsCodigo) {
    if (srsCodigo == '<%=CodedValues.SRS_BLOQUEADO%>' || srsCodigo == '<%=CodedValues.SRS_EXCLUIDO%>') {
       var dtSaidaField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_SAIDA)%>;
       if (typeof(dtSaidaField) != 'undefined' && !dtSaidaField.disabled) {
           dtSaidaField.value = '';
       }

       var dtUltSalField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_ULT_SALARIO)%>;
       if (typeof(dtUltSalField) != 'undefined' && !dtUltSalField.disabled) {
           dtUltSalField.value = '';
       }
       
    } 
    
    if (srsCodigo == '<%=CodedValues.SRS_BLOQUEADO%>') {
       var dtRetornoField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_RETORNO)%>;
       if (typeof(dtRetornoField) != 'undefined' && !dtRetornoField.disabled) {
           dtRetornoField.value = '';
       }
    }
}

function habilitaDesabilitaDatas(obj) {
	var status = obj;
<%	if (ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_SAIDA, _responsavel_) 
		|| ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_ULT_SALARIO, _responsavel_)
		|| ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_RETORNO, _responsavel_)) {%>
		
    	var dtSaidaField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_SAIDA)%>;
    	var dataUltSalario = f0.<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_ULT_SALARIO)%>;
    	var dataRetorno = f0.<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_RETORNO)%>;
    	
    	if (status.value == <%="'" + CodedValues.SRS_EXCLUIDO + "'"%>) {
    		if (dataRetorno.value != null && dataRetorno.value !="") {
	  			var confirmar = confirm('<hl:message key="mensagem.desabilita.datas"/>');
    			if (confirmar == true) {
    				dtSaidaField.value = "";
        			dataUltSalario.value = "";
        			dataRetorno.value = ""; 
            		dataRetorno.disabled = true;
            		dtSaidaField.disabled = false;
            		dataUltSalario.disabled = false;    			
  	  			} else {
  	  				var statusAnterior = f0.<%=TextHelper.forHtmlAttribute( FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SITUACAO)%>;
  	  				statusAnterior.value = '<%=TextHelper.forHtmlAttribute(_registroServidor.getSrsCodigo())%>';
  	  			}    			
    		}else{
				dtSaidaField.value = "";
    			dataUltSalario.value = "";
    			dataRetorno.value = ""; 
        		dataRetorno.disabled = true;
        		dtSaidaField.disabled = false;
        		dataUltSalario.disabled = false;    			
    		}
    	}
    	if (status.value == <%="'" + CodedValues.SRS_BLOQUEADO + "'"%>){
    		if ((dtSaidaField.value != null && dtSaidaField.value !="") || (dataUltSalario.value != null && dataUltSalario.value !="")){
	  			var confirmar = confirm('<hl:message key="mensagem.desabilita.datas"/>');
    			if (confirmar == true) {
    				dtSaidaField.value = "";
        			dataUltSalario.value = "";
        			dataRetorno.value = ""; 
        			dtSaidaField.disabled = false;
        			dataUltSalario.disabled = false;
        			dataRetorno.disabled = false;   			
  	  			} else {
  	  				var statusAnterior = f0.<%=TextHelper.forHtmlAttribute( FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SITUACAO)%>;
  	  				statusAnterior.value = '<%=TextHelper.forHtmlAttribute(_registroServidor.getSrsCodigo())%>';
  	  			}    			
    		}else{
				dtSaidaField.value = "";
    			dataUltSalario.value = "";
    			dataRetorno.value = ""; 
    			dtSaidaField.disabled = false;
    			dataUltSalario.disabled = false;
    			dataRetorno.disabled = false;   			
    		}
    	}
    	if (status.value == <%="'" + CodedValues.SRS_ATIVO + "'"%> || status.value == <%="'" + CodedValues.SRS_PENDENTE + "'"%>){
     		if (
     			(dtSaidaField.value != null && dtSaidaField.value !="") 
         		|| (dataUltSalario.value != null && dataUltSalario.value !="") 
         		|| (dataRetorno.value != null && dataRetorno.value !="")
         		){
  		  		var confirmar = confirm('<hl:message key="mensagem.desabilita.datas"/>');
  		  		if (confirmar == true) {
  		      		dtSaidaField.value = "";
  		      		dataUltSalario.value = "";
  		      		dataRetorno.value = "";
  		      		dtSaidaField.disabled = true;
  		      		dataUltSalario.disabled = true;
  		      		dataRetorno.disabled = true;
  		  		}else {
  		  			var statusAnterior = f0.<%=TextHelper.forHtmlAttribute( FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SITUACAO)%>;
  		  			statusAnterior.value = '<%=TextHelper.forHtmlAttribute(_registroServidor.getSrsCodigo())%>';
  		  			if (statusAnterior.value != <%="'" + CodedValues.SRS_EXCLUIDO + "'"%> && statusAnterior.value != <%="'" + CodedValues.SRS_BLOQUEADO + "'"%>){
  		  				dtSaidaField.disabled = true;
  		  				dataUltSalario.disabled = true;
  		  				dataRetorno.disabled = true;
  		  			}else{
  		  				if (statusAnterior.value == <%="'" + CodedValues.SRS_EXCLUIDO + "'"%>){
  		  		    		dtSaidaField.disabled = false;
  		  		    		dataUltSalario.disabled = false;
  		  				}
  		  				if (statusAnterior.value == <%="'" + CodedValues.SRS_BLOQUEADO + "'"%>){
  		  					dtSaidaField.disabled = false;
  		  					dataUltSalario.disabled = false;
  		  					dataRetorno.disabled = false;
  		  		    	}
  		  			}
  		  		}
      		}
    	}
		if (status.value != <%="'" + CodedValues.SRS_EXCLUIDO + "'"%> && status.value != <%="'" + CodedValues.SRS_BLOQUEADO + "'"%>){
			dtSaidaField.disabled = true;
			dataUltSalario.disabled = true;
			dataRetorno.disabled = true;
		}
    	
  <%}%>
}

function habilitaDesabilitaExibirMotivoBloqueio(obj) {
	var status = obj;
	<%
    if (ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_EXIBE_MOTIVO_BLOQUEIO, _responsavel_)) {
    %>
    	var exibeMotivoBloqueio = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_EXIBE_MOTIVO_BLOQUEIO)%>;

		if (exibeMotivoBloqueio) {
			if (status.value == <%="'" + CodedValues.SRS_BLOQUEADO + "'"%>) {
				exibeMotivoBloqueio[0].disabled = false;
				exibeMotivoBloqueio[1].disabled = false;
			} else {
				exibeMotivoBloqueio[0].checked = false;
				exibeMotivoBloqueio[1].checked = false;
				exibeMotivoBloqueio[0].disabled = true;
				exibeMotivoBloqueio[1].disabled = true;
			}
		}
	<%
    }
    %>
}

function limpaDesabilitaCampo(){

<%	if (ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_SAIDA, _responsavel_) || 
        ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_ULT_SALARIO, _responsavel_) ||
		ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_RETORNO, _responsavel_)){%>

		var dtSaidaField = f0.<%=(String)(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_SAIDA)%>;
		var dataUltSalario = f0.<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_ULT_SALARIO)%>;
		var dataRetorno = f0.<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_RETORNO)%>;
		var status = '<%=TextHelper.forHtmlAttribute(_registroServidor.getSrsCodigo())%>';
		if (status == <%="'" + CodedValues.SRS_EXCLUIDO + "'"%>) {
			dtSaidaField.disabled = false;
			dataUltSalario.disabled = false;
			dataRetorno.disabled = true;
		}
		if (status == <%="'" + CodedValues.SRS_BLOQUEADO + "'"%>){
			dtSaidaField.disabled = false;
			dataUltSalario.disabled = false;
			dataRetorno.disabled = false;
		}
		if (status != <%="'" + CodedValues.SRS_EXCLUIDO + "'"%> && status != <%="'" + CodedValues.SRS_BLOQUEADO + "'"%>){
			dtSaidaField.disabled = true;
			dataUltSalario.disabled = true;
			dataRetorno.disabled = true;
		}
		
<%  }%>
}
</script>
