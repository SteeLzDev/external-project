<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%
   String obrSerPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");   
   AcessoSistema responsavelSerPage = JspHelper.getAcessoSistema(request);
   List<TransferObject> cargosSerPage = (List<TransferObject>) request.getAttribute("listaCargosServidor");
   
   String matricula = (String) JspHelper.verificaVarQryStr(request, "matricula");
   String nome = (String) JspHelper.verificaVarQryStr(request, "nome");
   String tipo = (String) JspHelper.verificaVarQryStr(request, "tipo");
   String cargo = (String) JspHelper.verificaVarQryStr(request, "cargo");
   
   int tamanhoMatriculaSerPage = 0;
   int tamMaxMatriculaSerPage = 0;
   try {
       Object param = ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MATRICULA, responsavelSerPage);
       tamanhoMatriculaSerPage = (param != null && !param.equals("")) ? Integer.parseInt(param.toString()) : 0;
       param = ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MATRICULA_MAX, responsavelSerPage);
       tamMaxMatriculaSerPage = (param != null && !param.equals("")) ? Integer.parseInt(param.toString()) : 0;
   } catch (Exception ex) {
   }
   
   
   String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
   boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
   
   String valueMatricula = "";
   String others = "";
   if (!TextHelper.isNull(matricula)) {
       valueMatricula = matricula;
       others = "disabled";
   }
         
   String valueNome = "";
   if (!TextHelper.isNull(nome)) {
       valueNome = nome;
       others = "disabled";
   }
   
   String valueTipo = "";
   if (!TextHelper.isNull(tipo) && (!tipo.equals("limite_contrato_entidade") && !tipo.equals("limite_contrato_grupo_svc"))) {
       valueTipo = tipo;
       others = "disabled";
   }   
   
   if (desabilitado) {
       others = "disabled";
   }      
%>
          <div class="form-group col-sm-12 col-md-6">
            <label id="lblMatricula" for="matricula"><hl:message key="rotulo.servidor.matricula"/></label>
            <hl:htmlinput name="matricula" di="matricula" onBlur="vfRseMatricula();" value="<%=TextHelper.forHtmlAttribute(valueMatricula )%>" others="<%=TextHelper.forHtmlAttribute(others)%>" type="text" classe="form-control" size="40" mask="#*40" placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavelSerPage, ApplicationResourcesHelper.getMessage("rotulo.servidor.matricula", responsavelSerPage))%>"/>
          </div>
          <div class="form-group col-sm-12 col-md-6">
            <label id="lblNome" for="nome"><hl:message key="rotulo.servidor.nome"/></label>
            <hl:htmlinput name="nome" di="nome" value="<%=TextHelper.forHtmlAttribute(valueNome )%>" others="<%=TextHelper.forHtmlAttribute(others)%>" type="text" classe="form-control" size="40" mask="#*40" placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavelSerPage, ApplicationResourcesHelper.getMessage("rotulo.servidor.nome", responsavelSerPage))%>"/>
          </div>
          <div class="form-group col-sm-12 col-md-6">
            <label id="lblTipo" for="tipo"><hl:message key="rotulo.servidor.tipo"/></label>
            <hl:htmlinput name="tipo" di="tipoSerPage" value="<%=TextHelper.forHtmlAttribute(valueTipo )%>" others="<%=TextHelper.forHtmlAttribute(others)%>" type="text" classe="form-control" size="40" mask="#*40" placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavelSerPage, ApplicationResourcesHelper.getMessage("rotulo.servidor.tipo", responsavelSerPage))%>"/>
          </div>
          <div class="form-group col-sm-12 col-md-6">
            <label id="lblCargo" for="cargo"><hl:message key="rotulo.servidor.cargo"/></label>
            <%if (TextHelper.isNull(cargo) && !desabilitado) { %>
               <%=JspHelper.geraCombo(cargosSerPage, "cargo", Columns.CRS_DESCRICAO, Columns.CRS_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelSerPage), null, false, 1, cargo, null, false, "form-control")%>
            <%} else if (!TextHelper.isNull(cargo)) { %>
               <%=JspHelper.geraCombo(cargosSerPage, "cargo", Columns.CRS_DESCRICAO, Columns.CRS_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelSerPage), null, false, 1, cargo, null, true, "form-control")%>
            <%} else if (desabilitado) {%>
               <%=JspHelper.geraCombo(cargosSerPage, "cargo", Columns.CRS_DESCRICAO, Columns.CRS_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelSerPage), null, false, 1, null, null, true, "form-control")%>
            <%} %>
          </div>
          
    <% if (obrSerPage.equals("true")) { %>
       <script type="text/JavaScript">
       function funSerPage() {
          camposObrigatorios = camposObrigatorios + 'matricula,';
          camposObrigatorios = camposObrigatorios + 'nome,';
          camposObrigatorios = camposObrigatorios + 'tipo,';
          camposObrigatorios = camposObrigatorios + 'cargo,';
          msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.ser.matricula"/>,';
          msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.ser.nome"/>,';
          msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.ser.tipo"/>,';
          msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.ser.cargo"/>,';
       }
       addLoadEvent(funSerPage);     
       </script>
    <% } %>    
    
        <script type="text/JavaScript">
        function vfRseMatricula(validaForm) 
        {   
          if(validaForm === undefined) {
        	  validaForm = false;
          }
        	
          var matriculaField = document.getElementById('matricula');
          var matricula = matriculaField.value;
          var tamMinMatricula = <%=TextHelper.forJavaScriptBlock(tamanhoMatriculaSerPage)%>;    
          var tamMaxMatricula = <%=TextHelper.forJavaScriptBlock(tamMaxMatriculaSerPage)%>;
              
          if (matricula != ''){     
                  
            if(validaForm){
              if(matricula.length < tamMinMatricula){
                alert('<hl:message key="mensagem.erro.matricula.tamanho.min" arg0="<%=TextHelper.forHtmlAttribute(String.valueOf(tamanhoMatriculaSerPage))%>"/>');
                if (QualNavegador() == "NE") {
                      globalvar = matriculaField;
                      setTimeout("globalvar.focus()",0);
                  } 
                  else
                      matriculaField.focus();         
              }
              else if(tamMaxMatricula > 0 && matricula.length > tamMaxMatricula){
                alert('<hl:message key="mensagem.erro.matricula.tamanho.max" arg0="<%=TextHelper.forHtmlAttribute(String.valueOf(tamMaxMatriculaSerPage))%>"/>');
                if (QualNavegador() == "NE") {
                      globalvar = matriculaField;
                      setTimeout("globalvar.focus()",0);
                  } 
                  else
                      matriculaField.focus();
              }
              else{
                matriculaField.style.color = 'black';
                  return true;
              }     
            } 
            else{ 
              if(matricula.length < tamMinMatricula){
              matriculaField.style.color = 'red';
            return false;
              }       
              else if(tamMaxMatricula > 0 && matricula.length > tamMaxMatricula){
              matriculaField.style.color = 'red';
            return false;
              }
              else{
                matriculaField.style.color = 'black';
                return true;        
              }         
            }
          }
          else{
            matriculaField.style.color = 'black';
            return true;
          }      
        }  
         function valida_campo_dados_servidor() {
             return vfRseMatricula(true);
         }
        </script>        
         
