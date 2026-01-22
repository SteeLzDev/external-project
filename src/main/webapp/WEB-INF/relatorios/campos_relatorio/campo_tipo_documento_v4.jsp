<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<% 
List<TransferObject> tipoDocumento = (List<TransferObject>) request.getAttribute("listaTiposDocumento");

String obrScvPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
 
String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
%>  
    <fieldset class="form-group col-sm-12 col-md-12">
      <label id="lblTipoDocumento">${descricoes[recurso]}</label><br>
      <div class="form-check">
        <div class="row">
          <input type="hidden" id="tipoDocumento" name="tipoDocumento"></input>
          <% for (TransferObject td : tipoDocumento){ %>
          <div class="col-sm-12 col-md-6">
              <input type="checkbox" onChange="montarObjeto(this.name)" <% if(desabilitado){%> disabled <%}%> name="<%= td.getAttribute(Columns.TAR_CODIGO) %>" id="<%= td.getAttribute(Columns.TAR_CODIGO) %>" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascara(this);"> <%=td.getAttribute(Columns.TAR_DESCRICAO)%>
          </div>
           <% } %>
        </div>
      </div>
    </fieldset>
                
<script language="JavaScript" type="text/JavaScript">
  function valida_campo_tipo_documento() {
     return true;
  }

  var tipoDocumentoArray = new Array();

  function montarObjeto(tipoDocumentoCodigo){
      if(tipoDocumentoArray.indexOf(tipoDocumentoCodigo) > -1){
        tipoDocumentoArray.splice(tipoDocumentoArray.indexOf(tipoDocumentoCodigo), 1); 
      }else{
        tipoDocumentoArray.push(tipoDocumentoCodigo);
      }
      document.getElementById("tipoDocumento").value = tipoDocumentoArray;
  }
</script>