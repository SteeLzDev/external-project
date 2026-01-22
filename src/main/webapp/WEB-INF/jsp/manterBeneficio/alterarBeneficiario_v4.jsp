<%--
* <p>Title: alterarBeneficiario_v4</p>
* <p>Description: Listar beneficiários v4</p>
* <p>Copyright: Copyright (c) 2005</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: larissa.silva $
* $Revision: 24740 $
* $Date: 2018-07-18 14:14:41 -0300 (Qua, 18 jul 2018) $
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper"%>
<%@ page import="com.zetra.econsig.persistence.entity.ContratoBeneficio"%>
<%@ page import="com.zetra.econsig.persistence.entity.Beneficiario"%>
<%@ page import="com.zetra.econsig.values.EstadoCivilEnum"%>
<%@ page import="com.zetra.econsig.values.GrauParentescoEnum"%>
<%@ page import="com.zetra.econsig.values.TipoBeneficiarioEnum"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
Boolean podeEditar = (Boolean) request.getAttribute("podeEditar");
Boolean novo = (Boolean) request.getAttribute("novo");
Beneficiario beneficiario = (Beneficiario) request.getAttribute("beneficiario");
List<TransferObject> tipoBeneficiarios = (List<TransferObject>) request.getAttribute("tipoBeneficiarios");
List<TransferObject> grauParentesco = (List<TransferObject>) request.getAttribute("grauParentesco");
List<TransferObject> estadoCivil = (List<TransferObject>) request.getAttribute("estadoCivil");
List<TransferObject> motivoDependencia = (List<TransferObject>) request.getAttribute("motivoDependencia");
List<TransferObject> nacionalidade = (List<TransferObject>) request.getAttribute("nacionalidade");
List<ContratoBeneficio> cbes = (List<ContratoBeneficio>) request.getAttribute("contratoBeneficio");
String serCodigo = (String) request.getAttribute(Columns.SER_CODIGO);
String rse_codigo = (String) request.getAttribute(Columns.RSE_CODIGO);
String dddTelServidor = (String) request.getAttribute("dddTelServidor");
String telServidor = (String) request.getAttribute("telServidor");
String dddCelServidor = (String) request.getAttribute("dddCelServidor");
String celServidor = (String) request.getAttribute("celServidor");
boolean telInvalido = (request.getAttribute("telInvalido") != null && (Boolean) request.getAttribute("telInvalido"));
boolean celInvalido = (request.getAttribute("celInvalido") != null && (Boolean) request.getAttribute("celInvalido"));
boolean voltaReserva = (request.getAttribute("voltarReserva") != null && (Boolean) request.getAttribute("voltarReserva"));
%>
<c:set var="title">
  <% if(novo) { %>
  <hl:message key="rotulo.botao.novo.beneficiario"/>
  <% } else if(podeEditar) { %>
  <hl:message key="rotulo.beneficiario.edicao.minusculo"/>
  <% } else { %>
  <hl:message key="rotulo.beneficiario.detalhes"/>
  <% } %>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-beneficios"></use>
</c:set>
<c:set var="bodyContent">
  <div>
    <form method="post" action="../v3/alterarBeneficiarios?acao=salvar&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1" id="form1">
      <div class="card">
        <div class="card-header">
          <% if(novo) { %>
          <h2 class="card-header-title">
            <hl:message key="rotulo.botao.novo.beneficiario"/>
          </h2>
          <% } else { %>
          <h2 class="card-header-title">
            <%=TextHelper.forHtmlAttribute(beneficiario.getBfcNome())%>
          </h2>
          <% } %>
        </div>
        <div class="card-body">
          <fieldset>          
            <input class="Edit" TYPE="hidden" name="<%=Columns.getColumnName(Columns.BFC_CODIGO)%>" value="<%= !novo ? TextHelper.forHtmlAttribute(beneficiario.getBfcCodigo()) : ""%>" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
            <input class="Edit" TYPE="hidden" name="<%=Columns.getColumnName(Columns.SER_CODIGO)%>" value="<%=TextHelper.forHtmlAttribute(serCodigo)%>" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
            <input class="Edit" TYPE="hidden" name="<%=Columns.getColumnName(Columns.RSE_CODIGO)%>" value="<%=TextHelper.forHtmlAttribute(rse_codigo)%>" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
            <input TYPE="hidden" name="BFC_TITULAR" value="<%=!novo && !beneficiario.getTipoBeneficiario().getTibCodigo().equals(TipoBeneficiarioEnum.TITULAR.tibCodigo) ? "N" : "S"%>">
            <div class="row">
              <div class="form-group col-sm-4">
              <label for="tibCodigo"><hl:message key="rotulo.beneficiario.tipo.beneficiario"/></label>
                <% if((podeEditar && responsavel.isCseSupOrg()) || (podeEditar && responsavel.isCsaCor()) || (podeEditar && responsavel.isSer() && (cbes == null))){%>
                  <%=JspHelper.geraCombo(tipoBeneficiarios, Columns.getColumnName(Columns.TIB_CODIGO),Columns.TIB_CODIGO, Columns.TIB_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, !novo ? beneficiario.getTipoBeneficiario().getTibCodigo() : "", null, false, "form-control")%>
                <%} else {%>
                  <%=JspHelper.geraCombo(tipoBeneficiarios, Columns.getColumnName(Columns.TIB_CODIGO), Columns.TIB_CODIGO, Columns.TIB_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, !novo ? beneficiario.getTipoBeneficiario().getTibCodigo() : "", null, true, "form-control")%>
                <% } %>
              </div>
              <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.ALTERAR_BENEFICIARIO_NOME)%>">
                <div class="form-group col-sm-8">
                  <label for="bfcNome"><hl:message key="rotulo.beneficiario.nome"/></label>
                  <input class="Edit form-control" id="bfcNome" TYPE="text" NAME="<%=Columns.getColumnName(Columns.BFC_NOME)%>" VALUE="<%= !novo ? TextHelper.forHtmlAttribute(beneficiario.getBfcNome()) : ""%>" SIZE="32" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" onkeypress=" return noEspecialChar()"<%=(String)( !((podeEditar && responsavel.isCseSupOrg()) || (podeEditar && responsavel.isCsaCor()) || (podeEditar && responsavel.isSer() && (cbes == null))) ? "disabled" : "")%>>
                  <div class="invalid-feedback">
                    <hl:message key='mensagem.erro.nome.beneficiario.abreviado'/>
                  </div>
                </div>
              </show:showfield>              
            </div>
            <div class="row" style="display: none;" id="divTermoCienciaCheckbox">
              <div class="form-check col-sm-12">
                <input class="form-check-input ml-0" type="checkbox" value="true" id="termoCienciaCheckbox" name="termoCienciaCheckbox">
                <hl:message key="mensagem.titulo.nome.beneficiario.abreviado.termo.ciencia"/>
              </div>
            </div>
            <div class="row">
              <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.ALTERAR_BENEFICIARIO_CPF)%>">
                <div class="form-group col-sm">
                  <label for=""><hl:message key="rotulo.beneficiario.cpf"/></label>
                  <hl:htmlinput type="text" mask="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCpfMask())%>" name="<%=Columns.getColumnName(Columns.BFC_CPF)%>" di="<%=Columns.getColumnName(Columns.BFC_CPF)%>"
                  value="<%=TextHelper.forHtmlAttribute(beneficiario.getBfcCpf())%>" size="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCpfSize())%>" classe="Edit form-control"
                  others="<%=TextHelper.forHtmlAttribute(!((podeEditar && responsavel.isCseSupOrg()) || (podeEditar && responsavel.isCsaCor()) || (podeEditar && responsavel.isSer() && (cbes == null))) ? "disabled" : "")%>"/>
                </div>
              </show:showfield>
              <div class="form-group col-sm">
                <label for=""><hl:message key="rotulo.beneficiario.rg"/></label>
                <input class="Edit form-control" type="text" name="<%=Columns.getColumnName(Columns.BFC_RG)%>" value="<%=!novo ? (TextHelper.forHtmlAttribute(beneficiario.getBfcRg()) != null ? TextHelper.forHtmlAttribute(beneficiario.getBfcRg()) : "") : ""%>" SIZE="32" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" <%=(String)( !((podeEditar && responsavel.isCseSupOrg()) || (podeEditar && responsavel.isCsaCor()) || (podeEditar && responsavel.isSer() && (cbes == null))) ? "disabled" : "")%>>
              </div>
              <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.ALTERAR_BENEFICIARIO_SEXO)%>">
                <div class="form-group col-sm">
                  <div><label for="<%=Columns.getColumnName(Columns.BFC_SEXO)%>"><hl:message key="rotulo.beneficiario.sexo"/></label></div>
                  <div class="form-check form-check-inline mt-2">
                    <hl:htmlinput name="<%=Columns.getColumnName(Columns.BFC_SEXO)%>" di="smasculino" type="radio"
                    value="M" checked="<%=String.valueOf((beneficiario.getBfcSexo() != null && beneficiario.getBfcSexo().toString().toUpperCase().equals(\"M\")))%>"
                    mask="#*10" others="<%=TextHelper.forHtmlAttribute(!((podeEditar && responsavel.isCseSupOrg()) || (podeEditar && responsavel.isCsaCor()) || (podeEditar && responsavel.isSer() && (cbes == null))) ? "disabled" : "")%>"
                    configKey="<%=Columns.getColumnName(Columns.BFC_SEXO)%>" classe="form-check-input ml-1" />
                    <label for="smasculino" class="form-check-label labelSemNegrito" aria-label='<hl:message key="rotulo.servidor.sexo.masculino"/>'><hl:message key="rotulo.servidor.sexo.masculino"/></label>
                    </div>
                    <div class="form-check form-check-inline mt-2">
                    <hl:htmlinput name="<%=Columns.getColumnName(Columns.BFC_SEXO)%>" di="sfeminino" type="radio" value="F"
                    checked="<%=String.valueOf(beneficiario.getBfcSexo() != null && beneficiario.getBfcSexo().toString().toUpperCase().equals(\"F\"))%>"
                    mask="#*10" others="<%=TextHelper.forHtmlAttribute(!((podeEditar && responsavel.isCseSupOrg()) || (podeEditar && responsavel.isCsaCor()) || (podeEditar && responsavel.isSer() && (cbes == null))) ? "disabled" : "")%>"
                    configKey="<%=Columns.getColumnName(Columns.BFC_SEXO)%>" classe="form-check-input ml-1" />
                    <label for="sfeminino" class="form-check-label labelSemNegrito" aria-label='<hl:message key="rotulo.servidor.sexo.feminino"/>'><hl:message key="rotulo.servidor.sexo.feminino"/></label>
                  </div>
                </div>
              </show:showfield>
            </div>
            <div class="row">
            </div>
            <div class="row">
              <div class="form-group col-sm">
                <div class="row">
                  <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.ALTERAR_BENEFICIARIO_TELEFONE)%>">
                    <div class="col-sm-2">
                      <label for="BFC_DDD_TELEFONE"><hl:message key="rotulo.beneficiario.ddd"/></label>
                      <hl:htmlinput di="BFC_DDD_TELEFONE" name="BFC_DDD_TELEFONE" type="text" classe="form-control"                    
                      value="<%=!novo && beneficiario.getBfcTelefone() != null && beneficiario.getBfcTelefone().length() >= 2 ? TextHelper.forHtmlAttribute(beneficiario.getBfcTelefone().substring(0,2)) : ""%>"
                      mask="<%=LocaleHelper.getDDDMask()%>" others="<%=TextHelper.forHtmlAttribute((!podeEditar) ? "disabled" : "")%>" />
                    </div>
                    <div class="col-sm-6">
                      <label for="<%=Columns.getColumnName(Columns.BFC_TELEFONE)%>"><hl:message key="rotulo.beneficiario.telefone"/></label>
                      <hl:htmlinput name="<%=Columns.getColumnName(Columns.BFC_TELEFONE)%>" type="tel" classe="form-control" di="<%=Columns.getColumnName(Columns.BFC_TELEFONE)%>"                    
                      value="<%=!novo && beneficiario.getBfcTelefone() != null && beneficiario.getBfcTelefone().length() >= 2 ? TextHelper.forHtmlAttribute(beneficiario.getBfcTelefone().substring(2)) : ""%>"
                      mask="<%=LocaleHelper.getTelefoneMask()%>" others="<%=TextHelper.forHtmlAttribute((!podeEditar) ? "disabled" : "")%>" />
                    </div>
                    <%
                        if(novo || (!novo && !beneficiario.getTipoBeneficiario().getTibCodigo().equals(TipoBeneficiarioEnum.TITULAR.tibCodigo))) {
                    %>
                    <div class="form-check pt-3 col-sm-4">
                      <input type="checkbox" class="form-check-input ml-1" name="usarTelTitular" id="usarTelTitular" value="N" onclick="copiaTelTitular()"/>
                      <label for="usarTelTitular" class="form-check-label labelSemNegrito ml-1 pr-4 text-nowrap align-text-top"><hl:message key="rotulo.beneficiario.usar.dados.titular"/></label>
                    </div>
                    <%
                        }
                    %>
                  </show:showfield>
                </div>
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm">
                <div class="row">
                  <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.ALTERAR_BENEFICIARIO_CELULAR)%>">
                    <div class="col-sm-2">
                      <label for="BFC_DDD_CELULAR"><hl:message key="rotulo.beneficiario.ddd"/></label>
                      <hl:htmlinput di="BFC_DDD_CELULAR" name="BFC_DDD_CELULAR" type="text" classe="form-control"                    
                      value="<%=!novo && beneficiario.getBfcCelular() != null && beneficiario.getBfcCelular().length() >= 2 ? TextHelper.forHtmlAttribute(beneficiario.getBfcCelular().substring(0,2)) : ""%>"
                      mask="<%=LocaleHelper.getDDDCelularMask()%>" others="<%=TextHelper.forHtmlAttribute((!podeEditar) ? "disabled" : "")%>" />
                    </div>
                    <div class="col-sm-6">
                      <label for="<%=Columns.getColumnName(Columns.BFC_CELULAR)%>"><hl:message key="rotulo.beneficiario.celular"/></label>
                      <hl:htmlinput name="<%=Columns.getColumnName(Columns.BFC_CELULAR)%>" type="text" classe="form-control" di="<%=Columns.getColumnName(Columns.BFC_CELULAR)%>"                    
                      value="<%=!novo && beneficiario.getBfcCelular() != null && beneficiario.getBfcCelular().length() >= 2 ? TextHelper.forHtmlAttribute(beneficiario.getBfcCelular().substring(2)) : ""%>"
                      mask="<%=LocaleHelper.getCelularMask()%>" others="<%=TextHelper.forHtmlAttribute((!podeEditar) ? "disabled" : "")%>" />
                    </div>
                    <%
                        if(novo || (!novo && !beneficiario.getTipoBeneficiario().getTibCodigo().equals(TipoBeneficiarioEnum.TITULAR.tibCodigo))) {
                    %>
                    <div class="form-check pt-3 col-sm-4">
                      <input type="checkbox" class="form-check-input ml-1" name="usarCelTitular" id="usarCelTitular" value="N" onclick="copiaCelTitular()"/>
                      <label for="usarCelTitular" class="form-check-label labelSemNegrito ml-1 pr-4 text-nowrap align-text-top"><hl:message key="rotulo.beneficiario.usar.dados.titular"/></label>
                    </div>
                    <%
                        }
                    %>
                  </show:showfield>
                </div>
              </div>
            </div>
            <div class="row">
              <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.ALTERAR_BENEFICIARIO_NOME_MAE)%>">
                <div class="form-group col-sm">
                  <label for="bfcNomeMae"><hl:message key="rotulo.beneficiario.nome.mae"/></label>
                  <input class="Edit form-control" id="bfcNomeMae" type="text" name="<%=Columns.getColumnName(Columns.BFC_NOME_MAE)%>" value="<%=!novo ? (TextHelper.forHtmlAttribute(beneficiario.getBfcNomeMae()) == null ? "" : TextHelper.forHtmlAttribute(beneficiario.getBfcNomeMae()) ) : ""%>" size="32" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" onkeypress="return noEspecialChar();" <%=(String)( !((podeEditar && responsavel.isCseSupOrg()) || (podeEditar && responsavel.isCsaCor()) || (podeEditar && responsavel.isSer() && (cbes == null))) ? "disabled" : "")%>>
                  <div class="invalid-feedback">
                    <hl:message key='mensagem.erro.nome.mae.beneficiario.abreviado'/>
                  </div>
                </div>
              </show:showfield>
              <div class="row" style="display: none;" id="divTermoCienciaMaeCheckbox">
                <div class="form-check col-sm-12">
                  <input class="form-check-input ml-1" type="checkbox" value="true" id="termoCienciaMaeCheckbox" name="termoCienciaMaeCheckbox">
                  <hl:message key="mensagem.titulo.nome.mae.beneficiario.abreviado.termo.ciencia"/>
                </div>
              </div>
              <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.ALTERAR_BENEFICIARIO_GRAU_PARENTESCO)%>">
              <%
                  if(novo || (!novo && !beneficiario.getTipoBeneficiario().getTibCodigo().equals(TipoBeneficiarioEnum.TITULAR.tibCodigo))) {
              %>
                <div class="form-group col-sm-4">
                  <label id="lblTipoBeneficiario" for="csaCodigo"><hl:message key="rotulo.beneficiario.grau.paretensco"/></label>
                <%
                    if(((podeEditar && responsavel.isCseSupOrg()) || (podeEditar && responsavel.isCsaCor()) || (podeEditar && responsavel.isSer() && (cbes == null)))){
                %>
                  <%=JspHelper.geraCombo(grauParentesco, Columns.getColumnName(Columns.GRP_CODIGO), Columns.GRP_CODIGO, Columns.GRP_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, !novo && beneficiario.getGrauParentesco() != null ? beneficiario.getGrauParentesco().getGrpCodigo() : "", "analisaGrauParentesco(this)", false, "form-control")%>
                <%
                    } else {
                %>
                  <%=JspHelper.geraCombo(grauParentesco, Columns.getColumnName(Columns.GRP_CODIGO), Columns.GRP_CODIGO, Columns.GRP_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, !novo && beneficiario.getGrauParentesco() != null ? beneficiario.getGrauParentesco().getGrpCodigo() : "", "analisaGrauParentesco(this)", true, "form-control")%>
                <%
                    }
                %>
                </div>
              <%
                  }
              %>
              </show:showfield>
            </div>
            <div class="row">
              <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.ALTERAR_BENEFICIARIO_DATA_NASCIMENTO)%>">
                <div class="form-group col-sm-4">
                  <label for=""><hl:message key="rotulo.beneficiario.data.nascimento"/></label>
                  <hl:htmlinput name="<%=Columns.getColumnName(Columns.BFC_DATA_NASCIMENTO)%>" di="<%=Columns.getColumnName(Columns.BFC_DATA_NASCIMENTO)%>" type="text" 
                  classe="Edit form-control" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" 
                  value="<%=!novo ? TextHelper.forHtmlAttribute(DateHelper.format(beneficiario.getBfcDataNascimento(), "dd/MM/yyyy")) : ""%>" 
                  others="<%=TextHelper.forHtmlAttribute(!((podeEditar && responsavel.isCseSupOrg()) || (podeEditar && responsavel.isCsaCor()) || (podeEditar && responsavel.isSer() && (cbes == null))) ? "disabled" : "")%>" />
                </div>
              </show:showfield>
              <div class="form-group col-sm-4">
                <label for="<%=Columns.getColumnName(Columns.BFC_DATA_CASAMENTO)%>"><hl:message key="rotulo.beneficiario.data.casamento"/></label>
                <hl:htmlinput name="<%=Columns.getColumnName(Columns.BFC_DATA_CASAMENTO)%>" di="<%=Columns.getColumnName(Columns.BFC_DATA_CASAMENTO)%>" type="text" 
                classe="Edit form-control" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" 
                value="<%=!novo ? TextHelper.forHtmlAttribute(DateHelper.format(beneficiario.getBfcDataCasamento(), LocaleHelper.getDatePattern())) : ""%>" 
                onBlur="" onChange="validaDataCasamento()"
                others="<%=TextHelper.forHtmlAttribute( !novo && podeEditar && responsavel.isSup() && beneficiario.getGrauParentesco() != null && GrauParentescoEnum.permiteEdicaoDataCasamento(beneficiario.getGrauParentesco().getGrpCodigo())  ? "" : "disabled")%>" />
              </div>
              <%
                  if (responsavel.isSup() && responsavel.temPermissao(CodedValues.FUN_EDITAR_CADASTRO_BENEFICIARIO_AVANCADA) && !novo){
              %>
              <div class="form-group col-sm-4">
                <label for=""><hl:message key="rotulo.beneficiario.data.obito"/></label>
                <hl:htmlinput name="<%=Columns.getColumnName(Columns.BFC_DATA_OBITO)%>" di="<%=Columns.getColumnName(Columns.BFC_DATA_OBITO)%>" type="text" 
                classe="Edit form-control" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" 
                value="<%=!novo ? TextHelper.forHtmlAttribute(DateHelper.format(beneficiario.getBfcDataObito(), LocaleHelper.getDatePattern())) : ""%>" 
                others="<%=TextHelper.forHtmlAttribute(!((podeEditar && responsavel.isCseSupOrg()) || (podeEditar && responsavel.isCsaCor()) || (podeEditar && responsavel.isSer() && (cbes == null))) ? "disabled" : "")%>" />
              </div>                       
              <%
                                         }
                                     %>
              <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.ALTERAR_BENEFICIARIO_NACIONALIDADE)%>">       
                <div class="form-group col-sm-4">
                  <label for="lblNacionalidade"><hl:message key="rotulo.beneficiario.nacionalidade"/></label>
                  <%
                      if(podeEditar){
                  %>
                    <%=JspHelper.geraCombo(nacionalidade, Columns.getColumnName(Columns.NAC_CODIGO), Columns.NAC_CODIGO, Columns.NAC_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, !novo && beneficiario.getNacionalidade() != null ? beneficiario.getNacionalidade().getNacCodigo() : "", null, false, "form-control")%>
                  <%
                      } else {
                  %>
                    <%=JspHelper.geraCombo(nacionalidade, Columns.getColumnName(Columns.NAC_CODIGO), Columns.NAC_CODIGO, Columns.NAC_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, !novo && beneficiario.getNacionalidade() != null ? beneficiario.getNacionalidade().getNacCodigo() : "", null, true, "form-control")%>
                  <%
                      }
                  %>
                </div>
              </show:showfield>
              <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.ALTERAR_BENEFICIARIO_ESTADO_CIVIL)%>">
                <div class="form-group col-sm-4">
                  <label id="lblEstadoCivil" for="estCvlCodigo"><hl:message key="rotulo.beneficiario.estado.civil"/></label>
                  <%
                      if(((podeEditar && responsavel.isCseSupOrg()) || (podeEditar && responsavel.isCsaCor()) || (podeEditar && responsavel.isSer() && (cbes == null)))){
                  %>
                    <%
                        if((beneficiario.getBfcEstadoCivil() != null && beneficiario.getBfcEstadoCivil().toString().equals(EstadoCivilEnum.CASADO.getCodigo())) && (beneficiario.getGrauParentesco() != null && (beneficiario.getGrauParentesco().getGrpCodigo().equals(GrauParentescoEnum.CONJUGE.getCodigo()) || beneficiario.getGrauParentesco().getGrpCodigo().equals(GrauParentescoEnum.COMPANHEIRO.getCodigo())))) {
                    %>
                      <%=JspHelper.geraCombo(estadoCivil, Columns.getColumnName(Columns.EST_CIVIL_CODIGO), Columns.EST_CIVIL_CODIGO, Columns.EST_CIVIL_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, !novo ? "" + beneficiario.getBfcEstadoCivil() : "", null, true, "form-control")%>
                    <%
                        } else {
                    %>
                      <%=JspHelper.geraCombo(estadoCivil, Columns.getColumnName(Columns.EST_CIVIL_CODIGO), Columns.EST_CIVIL_CODIGO, Columns.EST_CIVIL_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, !novo ? "" + beneficiario.getBfcEstadoCivil() : "", null, false, "form-control")%>
                    <%
                        }
                    %>
                  <%
                      } else {
                  %>
                    <%=JspHelper.geraCombo(estadoCivil, Columns.getColumnName(Columns.EST_CIVIL_CODIGO), Columns.EST_CIVIL_CODIGO, Columns.EST_CIVIL_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, !novo ? "" + beneficiario.getBfcEstadoCivil() : "", null, true, "form-control")%>
                  <%
                      }
                  %>  
                </div>
              </show:showfield>
              <div class="form-group col-sm-12">
                <label id="lblMotivoDependencia" for="mdeCodigo"><hl:message key="rotulo.beneficiario.motivo.dependencia"/></label>
                <% if(podeEditar && !responsavel.isSer()){%>
                  <%=JspHelper.geraCombo(motivoDependencia, Columns.getColumnName(Columns.MDE_CODIGO), Columns.MDE_CODIGO, Columns.MDE_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, !novo && beneficiario.getMotivoDependencia() != null ? beneficiario.getMotivoDependencia().getMdeCodigo() : "", null, false, "form-control")%>
                <%} else {%>
                  <%=JspHelper.geraCombo(motivoDependencia, Columns.getColumnName(Columns.MDE_CODIGO), Columns.MDE_CODIGO, Columns.MDE_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, !novo && beneficiario.getMotivoDependencia() != null ? beneficiario.getMotivoDependencia().getMdeCodigo() : "", null, true, "form-control")%>
                <%} %>
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm">
                <label for="<%=Columns.getColumnName(Columns.BFC_EXCECAO_DEPENDENCIA_INI)%>"><hl:message key="rotulo.beneficiario.motivo.dependencia.ini"/></label>
                <hl:htmlinput name="<%=Columns.getColumnName(Columns.BFC_EXCECAO_DEPENDENCIA_INI)%>" di="<%=Columns.getColumnName(Columns.BFC_EXCECAO_DEPENDENCIA_INI)%>" 
                type="text" classe="Edit form-control" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" 
                value="<%= !novo ? TextHelper.forHtmlAttribute(DateHelper.format(beneficiario.getBfcExcecaoDependenciaIni(), "dd/MM/yyyy")) : ""%>" 
                others="<%=TextHelper.forHtmlAttribute(!podeEditar || responsavel.isSer() ? "disabled" : "")%>" />
              </div>
              <div class="form-group col-sm">
                <label for=""><hl:message key="rotulo.beneficiario.motivo.dependencia.fim"/></label>
                <hl:htmlinput name="<%=Columns.getColumnName(Columns.BFC_EXCECAO_DEPENDENCIA_FIM)%>" di="<%=Columns.getColumnName(Columns.BFC_EXCECAO_DEPENDENCIA_FIM)%>" type="text" 
                classe="Edit form-control" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" 
                value="<%= !novo ? TextHelper.forHtmlAttribute(DateHelper.format(beneficiario.getBfcExcecaoDependenciaFim(), "dd/MM/yyyy")) : ""%>" 
                others="<%=TextHelper.forHtmlAttribute(!podeEditar || responsavel.isSer() ? "disabled" : "")%>" />
                </div>
            </div>
            <div class="row">
              <div class="form-group col-sm-2">
                <div><label for=""><hl:message key="rotulo.beneficiario.excecao.subsidio"/></label></div>
                  <div class="form-check form-check-inline">
                    <hl:htmlinput name="<%=Columns.getColumnName(Columns.BFC_SUBSIDIO_CONCEDIDO)%>" di="<%=Columns.getColumnName(Columns.BFC_SUBSIDIO_CONCEDIDO) + "_S"%>"
                             type="radio" value="S" checked="<%=String.valueOf(beneficiario.getBfcSubsidioConcedido() != null && beneficiario.getBfcSubsidioConcedido().toString().toUpperCase().equals(\"S\"))%>"
                             mask="#*10" others="<%=TextHelper.forHtmlAttribute(!podeEditar || responsavel.isSer() ? "disabled" : "")%>"
                             configKey="<%=Columns.getColumnName(Columns.BFC_SUBSIDIO_CONCEDIDO)%>" classe="form-check-input ml-1"  />
                    <label class="form-check-label labelSemNegrito" aria-label='<hl:message key="rotulo.beneficiario.subsidio.concedido.sim"/>' for="<%=Columns.getColumnName(Columns.BFC_SUBSIDIO_CONCEDIDO) + "_S"%>"><hl:message key="rotulo.beneficiario.subsidio.concedido.sim"/></label>
                  </div>
                  <div class="form-check form-check-inline">
                    <hl:htmlinput name="<%=Columns.getColumnName(Columns.BFC_SUBSIDIO_CONCEDIDO)%>" di="<%=Columns.getColumnName(Columns.BFC_SUBSIDIO_CONCEDIDO) + "_N"%>"
                             type="radio" value="N" checked="<%=String.valueOf(beneficiario.getBfcSubsidioConcedido() != null && beneficiario.getBfcSubsidioConcedido().toString().toUpperCase().equals(\"N\") || novo || beneficiario.getBfcSubsidioConcedido() == null)%>"
                             mask="#*10" others="<%=TextHelper.forHtmlAttribute(!podeEditar || responsavel.isSer() ? "disabled" : "")%>"
                             configKey="<%=Columns.getColumnName(Columns.BFC_SUBSIDIO_CONCEDIDO)%>" classe="form-check-input ml-1" />
                    <label class="form-check-label labelSemNegrito" aria-label='<hl:message key="rotulo.beneficiario.subsidio.concedido.nao"/>' for="<%=Columns.getColumnName(Columns.BFC_SUBSIDIO_CONCEDIDO) + "_N"%>"><hl:message key="rotulo.beneficiario.subsidio.concedido.nao"/></label>
                </div>
              </div>
              <div class="form-group col-sm">
                <label for=""><hl:message key="rotulo.beneficiario.subsidio.motivo.excecao"/></label>
                <input class="Edit form-control" type="text" id="<%=Columns.getColumnName(Columns.BFC_SUBSIDIO_CONCEDIDO_MOTIVO)%>" NAME="<%=Columns.getColumnName(Columns.BFC_SUBSIDIO_CONCEDIDO_MOTIVO)%>" VALUE="<%= !novo ? (TextHelper.forHtmlAttribute(beneficiario.getBfcSubsidioConcedidoMotivo()) != null ? TextHelper.forHtmlAttribute(beneficiario.getBfcSubsidioConcedidoMotivo()) : "") : ""%>" SIZE="32" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" <%=(String)( !podeEditar || responsavel.isSer() ? "disabled" : "")%>>
              </div>
            </div>
          </fieldset>
        </div>
      </div>
      <div class="btn-action col-sm">
        <% if(voltaReserva){ %>
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getCurrentHistory(), request))%>')"  value="Cancelar"><hl:message key="rotulo.botao.voltar"/></a>
        <%} else {%>
        <a href="#no-back" name="Button" class="btn btn-outline-danger" onClick="postData('../v3/listarBeneficiarios?acao=listar&_skip_history_=true&<%=Columns.getColumnName(Columns.RSE_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(rse_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
        <% } %>
        <% if(podeEditar){%>
        <a href="#no-back" name="submit2" value="Salvar" onClick="verificaCampos(); return false;" class="btn btn-primary"><hl:message key="rotulo.botao.salvar"/></a>
        <%} %>
      </div>
    </form>
  </div>
</c:set>
<c:set var="javascript">
  <script src="../node_modules/inputmask/bundle.js?<hl:message key="release.tag"/>"></script>
  <script type="text/javascript">
  
  String.prototype.replaceAll = function(search, replacement) {
      var target = this;
      return target.replace(new RegExp(search, 'g'), replacement);
  };
  
  function formLoad() {
     f0 = document.forms[0];  
    }
  
  $(document).ready(function() {

  formLoad();

// Trocado para utilização da mascara dinamica.
//  let maskTelefone = new Inputmask("9999-9999", { clearIncomplete: true });
<%--  let telefone = document.getElementById("<%=Columns.BFC_TELEFONE%>"); --%>
//  maskTelefone.mask(telefone);
  
//  let maskCelular = new Inputmask("9999-9999", { clearIncomplete: true });
<%--  let celular = document.getElementById("<%=Columns.BFC_CELULAR%>"); --%>
//  maskCelular.mask(celular);
      
  document.getElementById("<%=Columns.getColumnName(Columns.BFC_SUBSIDIO_CONCEDIDO) + "_S"%>").setAttribute("onchange", "validaMotivo()");
  document.getElementById("<%=Columns.getColumnName(Columns.BFC_SUBSIDIO_CONCEDIDO) + "_N"%>").setAttribute("onchange", "validaMotivo()");
  checked = document.getElementById("<%=Columns.getColumnName(Columns.BFC_SUBSIDIO_CONCEDIDO) + "_S"%>").checked;
     
  if(<%=beneficiario.getTipoBeneficiario() != null && beneficiario.getTipoBeneficiario().getTibCodigo().compareTo(TipoBeneficiarioEnum.TITULAR.tibCodigo) == 0%>){
    document.getElementById("<%=Columns.getColumnName(Columns.TIB_CODIGO)%>").disabled = true;
  }
     
  if(checked == false){
    document.getElementById("<%=Columns.getColumnName(Columns.BFC_SUBSIDIO_CONCEDIDO_MOTIVO)%>").disabled = true;
  }
  });

  function verificaCampos() { 
	  
      var controlesSup = new Array();
      var msgsSup = new Array();   
      var sexoF = document.getElementById("sfeminino").checked;
      var sexoM = document.getElementById("smasculino").checked;
      
      var telTi = document.getElementById("usarTelTitular");
      var teltiC = document.getElementById("usarCelTitular");
      
      <%if(ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_BENEFICIARIO_TIPO, responsavel)) {%>      
      		controlesSup.push("<%=Columns.getColumnName(Columns.TIB_CODIGO)%>");
	   		msgsSup.push("<hl:message key='mensagem.beneficiario.tipo.beneficiario'/>");
      <%}%>      
      <%if(ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_BENEFICIARIO_NOME, responsavel)) {%>      
           controlesSup.push("<%=Columns.getColumnName(Columns.BFC_NOME)%>");
    	   msgsSup.push("<hl:message key='mensagem.beneficiario.nome.informar'/>");
      <%}%>
      <%if(ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_BENEFICIARIO_CPF, responsavel)) {%>      
      		controlesSup.push("<%=Columns.getColumnName(Columns.BFC_CPF)%>");
	   		msgsSup.push("<hl:message key='mensagem.beneficiario.cpf.informar'/>");
      <%}%>
      <%if(ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_BENEFICIARIO_NOME_MAE, responsavel)) {%>      
      		controlesSup.push("<%=Columns.getColumnName(Columns.BFC_NOME_MAE)%>");
       		msgsSup.push("<hl:message key='mensagem.beneficiario.nome.mae.informar'/>");
      <%}%>
      <%if(ShowFieldHelper.canEdit(FieldKeysConstants.ALTERAR_BENEFICIARIO_GRAU_PARENTESCO, responsavel)) {%>      
      		controlesSup.push("<%=Columns.getColumnName(Columns.GRP_CODIGO)%>");
       		msgsSup.push("<hl:message key='mensagem.beneficiario.grau.parentesco.informar'/>");
      <%}%>
      <%if(ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_BENEFICIARIO_DATA_NASCIMENTO, responsavel)) {%>      
      		controlesSup.push("<%=Columns.getColumnName(Columns.BFC_DATA_NASCIMENTO)%>");
       		msgsSup.push("<hl:message key='mensagem.beneficiario.data.nascimento.informar'/>");
      <%}%>
      <%if(ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_BENEFICIARIO_NACIONALIDADE, responsavel)) {%>      
      		controlesSup.push("<%=Columns.getColumnName(Columns.NAC_CODIGO)%>");
       		msgsSup.push("<hl:message key='mensagem.beneficiario.nacionalidade.informar'/>");
      <%}%>
      <%if(ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_BENEFICIARIO_ESTADO_CIVIL, responsavel)) {%>      
      		controlesSup.push("<%=Columns.getColumnName(Columns.EST_CIVIL_CODIGO)%>");
       		msgsSup.push("<hl:message key='mensagem.beneficiario.estado.civil'/>");
      <%}%>
      
      <%if((novo || (!novo && beneficiario.getTipoBeneficiario().getTibCodigo().equals(TipoBeneficiarioEnum.TITULAR.tibCodigo))) && ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_BENEFICIARIO_TELEFONE, responsavel)) {%>      
      	  controlesSup.push("BFC_DDD_TELEFONE", "<%=Columns.getColumnName(Columns.BFC_TELEFONE) %>");
    	  msgsSup.push("<hl:message key='mensagem.informe.servidor.ddd.telefone'/>", 
                  	   "<hl:message key='mensagem.informe.servidor.telefone'/>");
    <%} else if ((novo || (!novo && !beneficiario.getTipoBeneficiario().getTibCodigo().equals(TipoBeneficiarioEnum.TITULAR.tibCodigo))) && ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_BENEFICIARIO_TELEFONE, responsavel)) {%>      
          if (telTi) {
          	if(!telTi.checked){
            	  controlesSup.push("BFC_DDD_TELEFONE", "<%=Columns.getColumnName(Columns.BFC_TELEFONE) %>");
            	  msgsSup.push("<hl:message key='mensagem.informe.servidor.ddd.telefone'/>", 
                          	   "<hl:message key='mensagem.informe.servidor.telefone'/>");
              }
          }
    <%}%>
    
    <%if((novo || (!novo && beneficiario.getTipoBeneficiario().getTibCodigo().equals(TipoBeneficiarioEnum.TITULAR.tibCodigo))) && ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_BENEFICIARIO_CELULAR, responsavel)) {%>
  	        controlesSup.push("BFC_DDD_CELULAR", "<%=Columns.getColumnName(Columns.BFC_CELULAR) %>");
      	  	msgsSup.push("<hl:message key='mensagem.informe.servidor.ddd.celular'/>", 
                  	   "<hl:message key='mensagem.informe.servidor.celular'/>");
    <%} else if((novo || (!novo && !beneficiario.getTipoBeneficiario().getTibCodigo().equals(TipoBeneficiarioEnum.TITULAR.tibCodigo))) && ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_BENEFICIARIO_CELULAR, responsavel)) {%>
    	if (teltiC) {
    	   	if(!teltiC.checked){
            	  controlesSup.push("BFC_DDD_CELULAR", "<%=Columns.getColumnName(Columns.BFC_CELULAR) %>");
            	  msgsSup.push("<hl:message key='mensagem.informe.servidor.ddd.celular'/>", 
                          	   "<hl:message key='mensagem.informe.servidor.celular'/>");
            }
    	}
    <%}%>
    
    if(!sexoM && !sexoF && <%=ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_BENEFICIARIO_SEXO, responsavel)%>) {      
		alert("<hl:message key='mensagem.beneficiario.sexo.informar'/>");
		return false;
    }   
    
      if(ValidaCampos(controlesSup, msgsSup)){
        let nome = document.getElementById("bfcNome").value;
        document.getElementById("bfcNome").classList.remove("is-invalid");
        let nomeToken = nome.split(" ");
        
        var i;
        for (i = 0; i < nomeToken.length; i++) { 
          let token = nomeToken[i];
          token = token.replaceAll("[^A-Za-z]", "");
          if (token.length <= 1 && document.getElementById("termoCienciaCheckbox").checked == false) {
            document.getElementById("bfcNome").classList.add("is-invalid");
            document.getElementById("divTermoCienciaCheckbox").removeAttribute("style");
            alert("<hl:message key='mensagem.erro.nome.beneficiario.abreviado'/>");
            return false;
          }
        }

        let nomeMae = document.getElementById("bfcNomeMae").value;
        document.getElementById("bfcNomeMae").classList.remove("is-invalid");
        let nomeMaeToken = nomeMae.split(" ");

        var j;
        for (j = 0; j < nomeMaeToken.length; j++) { 
          let token = nomeMaeToken[j];
          token = token.replaceAll("[^A-Za-z]", "");
          if (token.length <= 1 && document.getElementById("termoCienciaMaeCheckbox").checked == false) {
            document.getElementById("bfcNomeMae").classList.add("is-invalid");
            document.getElementById("divTermoCienciaMaeCheckbox").removeAttribute("style");
            alert("<hl:message key='mensagem.erro.nome.mae.beneficiario.abreviado'/>");
            document.getElementById("bfcNomeMae").focus();
            return false;
          }
        }
        
          let cpf = document.getElementById("<%=Columns.getColumnName(Columns.BFC_CPF)%>").value;
          if (!CPF_OK(extraiNumCNPJCPF(cpf))) {
          return false;
          }
        
        <%if (ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_BENEFICIARIO_TELEFONE, responsavel)) {%>
            let telefone = document.getElementById("<%=Columns.getColumnName(Columns.BFC_TELEFONE) %>").value;
            if (telefone != null && telefone != "" && telefone.length < <%=LocaleHelper.getTelefoneSize()%>) {
              alert("<hl:message key='mensagem.erro.servidor.telefone.invalido'/>");
              return false;
            }
        <%}%>
        <%if (ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_BENEFICIARIO_CELULAR, responsavel)) {%>
            let celular = document.getElementById("<%=Columns.getColumnName(Columns.BFC_CELULAR)%>").value;
            if (celular != null && celular != "" && celular.length < <%=LocaleHelper.getTelefoneSize()%>) {
              alert("<hl:message key='mensagem.erro.servidor.celular.invalido'/>");
              return false;
            }
        <%}%>

        let dataObitoString = document.getElementById("<%=Columns.getColumnName(Columns.BFC_DATA_OBITO)%>");
          let dataNascimentoString = document.getElementById("<%=Columns.getColumnName(Columns.BFC_DATA_NASCIMENTO)%>");
          let dataCasamentoString = document.getElementById("<%=Columns.getColumnName(Columns.BFC_DATA_CASAMENTO)%>");
          let grauParentescoString = document.getElementById("<%=Columns.getColumnName(Columns.GRP_CODIGO)%>")
          let dataAtual = new Date(); 
          
          if (dataObitoString && dataObitoString.value) {
          let datas = obtemPartesData(dataObitoString.value); 
          let dataObito = new Date(datas[2], datas[1]-1, datas[0]); 
          
          if (dataObito > dataAtual) {
            alert("<hl:message key='mensagem.erro.data.obito.data.futura'/>");
            return false;
          }
          }
          
          if (dataNascimentoString && dataNascimentoString.value) {
          let datas = obtemPartesData(dataNascimentoString.value); 
          let dataNascimento = new Date(datas[2], datas[1]-1, datas[0]);  
          
          if (dataNascimento > dataAtual) {
            alert("<hl:message key='mensagem.erro.data.nascimento.data.futura'/>");
            return false;
          }
          }
          
          if (dataCasamentoString && dataCasamentoString.value) {
          let datas = obtemPartesData(dataCasamentoString.value); 
          let dataCasamento = new Date(datas[2], datas[1]-1, datas[0]);
          datas = obtemPartesData(dataNascimentoString.value);
          let dataNascimento = new Date(datas[2], datas[1]-1, datas[0]);
          let grauParentescoString = document.getElementById("<%=Columns.getColumnName(Columns.GRP_CODIGO)%>")
          let dataAtual = new Date();
          
          if (dataCasamento > dataAtual) {
            alert("<hl:message key='mensagem.erro.data.casamento.data.futura'/>");
            return false;
          }
          
          if (dataCasamento < dataNascimento) {
            alert("<hl:message key='mensagem.erro.data.casamento.data.passada'/>");
            return false;
          }
          
          if (grauParentescoString && grauParentescoString.value) {
              let value = grauParentescoString.value;
              if (<%=GrauParentescoEnum.CONJUGE.getCodigo()%> != value && <%=GrauParentescoEnum.COMPANHEIRO.getCodigo()%> != value) {
                alert("<hl:message key='mensagem.erro.data.casamento.invalida.para.grau.parentesco.selecionado'/>");
                return false;
              }
          }
          }
          
          if (grauParentescoString && grauParentescoString.value) {
              let grauParentesco = grauParentescoString.value;
              if ((<%=GrauParentescoEnum.CONJUGE.getCodigo()%> == grauParentesco || <%=GrauParentescoEnum.COMPANHEIRO.getCodigo()%> == grauParentesco) && (dataCasamentoString && !dataCasamentoString.value)) {
              alert("<hl:message key='mensagem.erro.data.casamento.nao.informada.para.grau.parentesco.selecionado'/>");
              return false;
            } 
          }

          enableAll();
          f0.submit();
        return true;
      }
      
    return false;
  }
  
  function validaMotivo(){
  if(document.getElementById("<%=Columns.getColumnName(Columns.BFC_SUBSIDIO_CONCEDIDO) + "_S"%>").checked){
      document.getElementById("<%=Columns.getColumnName(Columns.BFC_SUBSIDIO_CONCEDIDO_MOTIVO)%>").disabled = false;
    } else {
      document.getElementById("<%=Columns.getColumnName(Columns.BFC_SUBSIDIO_CONCEDIDO_MOTIVO)%>").disabled = true;
    }
  }
  
  function validaDataCasamento() {
  let dataNascimentoString = document.getElementById("<%=Columns.getColumnName(Columns.BFC_DATA_NASCIMENTO)%>");
  let dataCasamentoString = document.getElementById("<%=Columns.getColumnName(Columns.BFC_DATA_CASAMENTO)%>");
  let grauParentescoString = document.getElementById("<%=Columns.getColumnName(Columns.GRP_CODIGO)%>")
  let dataAtual = new Date();
    
    
  if (dataCasamentoString && dataCasamentoString.value && dataNascimentoString && dataNascimentoString.value) {
    let datas = obtemPartesData(dataCasamentoString.value); 
    let dataCasamento = new Date(datas[2], datas[1]-1, datas[0]);  
    datas = obtemPartesData(dataNascimentoString.value);
    let dataNascimento = new Date(datas[2], datas[1]-1, datas[0]);
    
    if (dataCasamento < dataNascimento) {
      alert("<hl:message key='mensagem.erro.data.casamento.data.passada'/>");
      document.getElementById("<%=Columns.getColumnName(Columns.BFC_DATA_CASAMENTO)%>").focus();
      return false;
    }
    }
  }
  
  function analisaGrauParentesco(selectObject) {
    let value = selectObject.value
    let data = document.getElementById("<%=Columns.getColumnName(Columns.BFC_DATA_CASAMENTO)%>")
    let estCivil = document.getElementById("<%=Columns.getColumnName(Columns.EST_CIVIL_CODIGO)%>")
    if (<%=GrauParentescoEnum.CONJUGE.getCodigo()%> == value || <%=GrauParentescoEnum.COMPANHEIRO.getCodigo()%> == value) {
      data.removeAttribute("disabled");
      var opts = estCivil.options;
      for (var opt, j = 0; opt = opts[j]; j++) {
        if (opt.value == "<%=EstadoCivilEnum.CASADO.getCodigo()%>") {
          estCivil.selectedIndex = j;
          break;
        }
      }
      estCivil.setAttribute("disabled","");
      
    } else { 
      data.value='';
      data.removeAttribute("style");
      data.setAttribute("disabled","");
      estCivil.removeAttribute("disabled","");
    }
  }
  
  function noEspecialChar(){
      tecla = event.keyCode;
      if ((tecla >= 33 && tecla <= 64) || (tecla >= 91 && tecla <= 96) || (tecla >= 123)){
          return false;
      }else{
         return true;
      }
  }
  
  function copiaTelTitular(){
	  let usarTelTitular = document.querySelector("#usarTelTitular");
	  let dddTel = document.getElementById("BFC_DDD_TELEFONE");
	  let telefone = document.getElementById("<%=Columns.getColumnName(Columns.BFC_TELEFONE)%>");
	  
	  if (usarTelTitular.checked && <%=telInvalido%>) {
    	  usarTelTitular.value = "N";
    	  usarTelTitular.checked = false;
    	  alert("<hl:message key='mensagem.erro.servidor.telefone.invalido'/>");
		  return false;
      } else if(usarTelTitular.checked && <%=!TextHelper.isNull(telServidor)%>){
		  dddTel.value="<%=dddTelServidor%>";
		  telefone.value="<%=telServidor%>";
		  dddTel.disabled = true;
		  telefone.disabled = true;
		  usarTelTitular.value = "S";
	  }	else if (usarTelTitular.checked && <%=TextHelper.isNull(telServidor)%>) {
		  alert("<hl:message key='mensagem.beneficiario.telefone.titular.nulo'/>");
    	  usarTelTitular.value = "N";
    	  usarTelTitular.checked = false;
		  return false;
	  } else {
		  dddTel.disabled = false;
		  telefone.disabled = false;
		  usarTelTitular.value = "N";
	  }  
  }
  
  function copiaCelTitular(){
	  let usarCelTitular = document.querySelector("#usarCelTitular");
	  let dddCel = document.querySelector("#BFC_DDD_CELULAR");
	  let celular = document.getElementById("<%=Columns.getColumnName(Columns.BFC_CELULAR)%>");
	  
      if (usarCelTitular.checked && <%=celInvalido%>) {
    	  usarCelTitular.value = "N";
    	  usarCelTitular.checked = false;
    	  alert("<hl:message key='mensagem.erro.servidor.celular.invalido'/>");
		  return false;
      } else if(usarCelTitular.checked && <%=!TextHelper.isNull(celServidor)%>){
		  dddCel.value="<%=dddCelServidor%>";
		  celular.value="<%=celServidor%>";
		  dddCel.disabled = true;
		  celular.disabled = true;
		  usarCelTitular.value = "S";
	  }	else if (usarCelTitular.checked && <%=TextHelper.isNull(celServidor)%>) {
		  usarCelTitular.value = "N";
		  usarCelTitular.checked = false;
		  alert("<hl:message key='mensagem.beneficiario.celular.titular.nulo'/>");
		  return false;
	  }else {
		  dddCel.disabled = false;
		  celular.disabled = false;
		  usarCelTitular.value = "N";
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