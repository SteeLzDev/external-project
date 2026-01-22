<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="java.util.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%

AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
List<Map.Entry<String, String>> listTO = (List<Map.Entry<String, String>>) request.getAttribute("listTO");

%>


<c:set var="title">
   <hl:message key="rotulo.editar.regra.restricao.acesso.titulo"/>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
<form method="post" action="../v3/restricaoAcesso?<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
  <input type="HIDDEN" name="acao" value="salvar">
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.dados.restricao.acesso"/></h2>
    </div>
    <div class="card-body">
      <div class="form-group col-sm-12 col-md-6">
        <div class="row">
          <span id="periodoDeVigencia"><hl:message key="rotulo.periodo.vigencia.restricao.acesso"/></span>
        </div>
        <div class="row mt-2" role="group" aria-labelledby="periodoDeVigencia">
          <div class="form-check pt-2 col-sm-12 col-md-1">
            <div class="float-left align-middle mt-4 form-control-label">
              <label for="horaIni" class="labelSemNegrito"><hl:message key="rotulo.data.de"/></label>
            </div>
          </div>
          <div class="form-check pt-2 col-sm-12 col-md-5">
            <hl:htmlinput name="horaIni" di="horaIni" type="text" value=""  classe="form-control" size="5" mask="DD:DD" placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.hora.min", responsavel) %>"/>
          </div>
          <div class="form-check pt-2 col-sm-12 col-md-1">
            <div class="float-left align-middle mt-4 form-control-label">
              <label for="horaFim" class="labelSemNegrito"><hl:message key="rotulo.data.ate"/></label>
            </div>
          </div>
          <div class="form-check pt-2 col-sm-12 col-md-5">
            <hl:htmlinput name="horaFim" di="horaFim" type="text" value=""  classe="form-control" size="5" mask="DD:DD" placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.hora.min", responsavel) %>"/>
          </div>
        </div>
      </div>
      <div class="row form-group col-sm-12 col-md-6">
        <label for="rraDescricao"><hl:message key="rotulo.descricao.restricao.acesso"/></label>
        <textarea name="rraDescricao" class="form-control" id="rraDescricao" onFocus="SetarEventoMascara(this,'#*65000',true);" onBlur="fout(this);ValidaMascara(this);" placeholder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.descricao", responsavel) %>"><%=TextHelper.forHtmlContent("")%></textarea>
      </div>
      <div class="row form-group col-sm-12 col-md-6">
        <label for="funcao"><hl:message key="rotulo.permissao.restricao.acesso"/></label>
        <select name="funcao" id="funcao" class="form-control">
          <option value=""><hl:message key="rotulo.campo.todas"/></option>
            <%
                Map sortedFunMap = new LinkedHashMap();
                for (Iterator it = listTO.iterator(); it.hasNext();) {
                    Map.Entry entry = (Map.Entry) it.next();
                    sortedFunMap.put(entry.getKey(), entry.getValue());
                }
    
                Set funSet = sortedFunMap.entrySet();
                Iterator setIt = funSet.iterator();
                while (setIt.hasNext()) {
                    Map.Entry funEntry = (Map.Entry) setIt.next();
                    String funCodigo = (String) funEntry.getKey();
                    String funcao = (String) funEntry.getValue();
            %>
                  <option value="<%=TextHelper.forHtmlAttribute(funCodigo)%>"><%=TextHelper.forHtmlContent(funcao)%></option>
            <%
                }
            %>
        </select>
      </div>
      <div class="row">
        <div class="form-group col-sm-12 col-md-6">
          <label for="data"><hl:message key="rotulo.permissao.restricao.data"/></label>
          <hl:htmlinput name="data" 
                        di="data" 
                        type="text" 
                        value="" 
                        onBlur="editaRestricao(this);" 
                        classe="form-control" 
                        size="10" 
                        mask="<%=LocaleHelper.getDateJavascriptPattern()%>" 
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.data.mascara", responsavel) %>"
          />
        </div>
      </div>
      <div class="row">
        <div class="form-group col-sm-12 col-md-6">
          <label for="diaUtil"><hl:message key="rotulo.permissao.restricao.dias.uteis.pergunta"/></label>
          <select class="form-control" name="diaUtil" id="diaUtil" onChange="editaRestricao(this);">
            <option value=""><hl:message key="rotulo.campo.nao.aplica"/></option>                      
            <option value="<%=(String)CodedValues.TPC_SIM%>" ><hl:message key="rotulo.sim"/></option>
            <option value="<%=(String)CodedValues.TPC_NAO%>" ><hl:message key="rotulo.nao"/></option>    
          </select>
        </div>
      </div>
      <fieldset>
          <div class="legend">
            <span><hl:message key="rotulo.permissao.restricao.papel.usuario"/></span>
          </div>
          <div class="form-check">
            <div class="row">
              <%if (!responsavel.isCsaCor()) { %>
              <div class="col-sm-12 col-md-4">
                <span class="text-nowrap align-text-top"> <input class="form-check-input ml-1" type="checkbox" NAME="chkPapel" ID="chkPapel1" VALUE="<%=(String)CodedValues.PAP_CONSIGNANTE%>" onBlur="fout(this);ValidaMascara(this);"> <label class="form-check-label labelSemNegrito ml-1" for="chkPapel1"><%=TextHelper.forHtmlContent(AcessoSistema.ENTIDADE_CSE )%></label>
                </span>
              </div>
              <div class="col-sm-12 col-md-4">
                <span class="text-nowrap align-text-top"> <input class="form-check-input ml-1" type="checkbox" NAME="chkPapel" ID="chkPapel2" VALUE="<%=(String)CodedValues.PAP_ORGAO%>" onBlur="fout(this);ValidaMascara(this);"> <label class="form-check-label labelSemNegrito ml-1" for="chkPapel2"><%=TextHelper.forHtmlContent(AcessoSistema.ENTIDADE_ORG )%></label>
                </span>
              </div>
              <div class="col-sm-12 col-md-4">
                <span class="text-nowrap align-text-top"> <input class="form-check-input ml-1" type="checkbox" NAME="chkPapel" ID="chkPapel3" VALUE="<%=(String)CodedValues.PAP_CONSIGNATARIA%>" onBlur="fout(this);ValidaMascara(this);"> <label class="form-check-label labelSemNegrito ml-1" for="chkPapel3"><%=TextHelper.forHtmlContent(AcessoSistema.ENTIDADE_CSA )%></label>
                </span>
              </div>
              <div class="col-sm-12 col-md-4">
                <span class="text-nowrap align-text-top"> <input class="form-check-input ml-1" type="checkbox" NAME="chkPapel" ID="chkPapel4" VALUE="<%=(String)CodedValues.PAP_CORRESPONDENTE%>" onBlur="fout(this);ValidaMascara(this);"> <label class="form-check-label labelSemNegrito ml-1" for="chkPapel4"><%=TextHelper.forHtmlContent(AcessoSistema.ENTIDADE_COR )%></label>
                </span>
              </div>
              <div class="col-sm-12 col-md-4">
                <span class="text-nowrap align-text-top"> <input class="form-check-input ml-1" type="checkbox" NAME="chkPapel" ID="chkPapel5" VALUE="<%=(String)CodedValues.PAP_SERVIDOR%>" onBlur="fout(this);ValidaMascara(this);"> <label class="form-check-label labelSemNegrito ml-1" for="chkPapel5"><%=TextHelper.forHtmlContent(AcessoSistema.ENTIDADE_SER )%></label>
                </span>
              </div>
              <div class="col-sm-12 col-md-4">
                <span class="text-nowrap align-text-top"> <input class="form-check-input ml-1" type="checkbox" NAME="chkPapel" ID="chkPapel6" VALUE="<%=(String)CodedValues.PAP_SUPORTE%>" onBlur="fout(this);ValidaMascara(this);"> <label class="form-check-label labelSemNegrito ml-1" for="chkPapel6"><%=TextHelper.forHtmlContent(AcessoSistema.ENTIDADE_SUP )%></label>
                </span>
              </div>
              <%} else { %>
              <div class="col-sm-12 col-md-4">
                <span class="text-nowrap align-text-top"> <input class="form-check-input ml-1" type="checkbox" NAME="chkPapel" ID="chkPapel7" VALUE="<%=(String)CodedValues.PAP_CONSIGNATARIA%>" onBlur="fout(this);ValidaMascara(this);"> <label class="form-check-label labelSemNegrito ml-1" for="chkPapel7"><%=TextHelper.forHtmlContent(AcessoSistema.ENTIDADE_CSA )%></label>
                </span>
              </div>
              <div class="col-sm-12 col-md-4">
                <span class="text-nowrap align-text-top"> <input class="form-check-input ml-1" type="checkbox" NAME="chkPapel" ID="chkPapel8" VALUE="<%=(String)CodedValues.PAP_CORRESPONDENTE%>" onBlur="fout(this);ValidaMascara(this);"> <label class="form-check-label labelSemNegrito ml-1" for="chkPapel8"><%=TextHelper.forHtmlContent(AcessoSistema.ENTIDADE_COR )%></label>
                </span>
              </div>
              <%} %>
            </div>
          </div>
        </fieldset>  
        <fieldset>
          <div class="legend">
            <span><hl:message key="rotulo.permissao.restricao.dia.semana"/></span>
          </div>
          <div class="form-check">
            <div class="row">
              <div class="col-sm-12 col-md-6">
                <span class="text-nowrap align-text-top">
                  <input class="form-check-input ml-1" type="checkbox" NAME="chkDiaSemana" ID="chkDiaSemana1" onClick="editaRestricao(this);" VALUE="<%=(int)Calendar.SUNDAY%>" >
                  <label class="labelSemNegrito ml-1 form-check-label" for="chkDiaSemana1"><%=DateHelper.getWeekDayName(Calendar.SUNDAY) %></label>
                </span>
              </div>
              <div class="col-sm-12 col-md-6">
                <span class="text-nowrap align-text-top">
                  <input class="form-check-input ml-1" type="checkbox" NAME="chkDiaSemana" ID="chkDiaSemana2" onClick="editaRestricao(this);" VALUE="<%=(int)Calendar.MONDAY%>">
                  <label class="labelSemNegrito ml-1 form-check-label" for="chkDiaSemana2"><%=DateHelper.getWeekDayName(Calendar.MONDAY) %></label>
                </span>
              </div>
              <div class="col-sm-12 col-md-6">
                <span class="text-nowrap align-text-top">
                  <input class="form-check-input ml-1" type="checkbox" NAME="chkDiaSemana" ID="chkDiaSemana3" onClick="editaRestricao(this);" VALUE="<%=(int)Calendar.TUESDAY%>" >
                  <label class="labelSemNegrito ml-1 form-check-label" for="chkDiaSemana3"><%=DateHelper.getWeekDayName(Calendar.TUESDAY) %></label>
                </span>
              </div>
              <div class="col-sm-12 col-md-6">
                <span class="text-nowrap align-text-top">
                  <input class="form-check-input ml-1" type="checkbox" NAME="chkDiaSemana" ID="chkDiaSemana4" onClick="editaRestricao(this);" VALUE="<%=(int)Calendar.WEDNESDAY%>" >
                  <label class="labelSemNegrito ml-1 form-check-label" for="chkDiaSemana4"><%=DateHelper.getWeekDayName(Calendar.WEDNESDAY) %></label>
                </span>
              </div>
              <div class="col-sm-12 col-md-6">
                <span class="text-nowrap align-text-top">
                  <input class="form-check-input ml-1" type="checkbox" NAME="chkDiaSemana" ID="chkDiaSemana5" onClick="editaRestricao(this);" VALUE="<%=(int)Calendar.THURSDAY%>" onBlur="fout(this);ValidaMascara(this);">
                  <label class="labelSemNegrito ml-1 form-check-label" for="chkDiaSemana5"><%=DateHelper.getWeekDayName(Calendar.THURSDAY) %></label>
                </span>
              </div>
              <div class="col-sm-12 col-md-6">
                <span class="text-nowrap align-text-top">
                  <input class="form-check-input ml-1" type="checkbox" NAME="chkDiaSemana" ID="chkDiaSemana6" onClick="editaRestricao(this);" VALUE="<%=(int)Calendar.FRIDAY%>" onBlur="fout(this);ValidaMascara(this);">
                  <label class="labelSemNegrito ml-1 form-check-label" for="chkDiaSemana6"><%=DateHelper.getWeekDayName(Calendar.FRIDAY) %></label>
                </span>
              </div>
              <div class="col-sm-12 col-md-6">
                <span class="text-nowrap align-text-top">
                  <input class="form-check-input ml-1" type="checkbox" NAME="chkDiaSemana" ID="chkDiaSemana7" onClick="editaRestricao(this);" VALUE="<%=(int)Calendar.SATURDAY%>" onBlur="fout(this);ValidaMascara(this);">
                  <label class="labelSemNegrito ml-1 form-check-label" for="chkDiaSemana7"><%=DateHelper.getWeekDayName(Calendar.SATURDAY) %></label>
                </span>
              </div>
            </div>
          </div>
        </fieldset>
      </div>
    </div>
    <div class="btn-action">
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.cancelar"/> </a>
      <a class="btn btn-primary" ID="btnEnvia" HREF="#no-back" onClick="if(enviar()){document.forms[0].submit();} return false;"><hl:message key="rotulo.botao.salvar"/></a>
    </div>
</form>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
 function editaRestricao(element) {
     if (element.disabled) {
         return false;
     }
     
     var arrayField = null;
     if (element.name == "chkDiaSemana") {
         arrayField = ["data", "diaUtil"];
     } else if (element.id == "data") {
         arrayField = ["chkDiaSemana", "diaUtil"];
     } else if (element.id == "diaUtil") {
         arrayField = ["chkDiaSemana", "data"];
     }
     
     var preenchido = false;
     
     if (element.name == "chkDiaSemana") {
        var i=0;
         for (i = 0; i < 7; i++) {
             if (document.forms[0].chkDiaSemana[i].checked) {
                 preenchido = true;
                 break;
             }
        }
     } else {
         if (element.value != null && element.value != "") {
             preenchido = true;              
         }
     }
     
     if (preenchido) {                               
       var i=0;
       for (i=0;i<arrayField.length;i++) {
            var field = document.getElementById(arrayField[i]);
                 
            if (arrayField[i] != "chkDiaSemana") {
              field.value = "";
              field.disabled = true;
            } else {                    
                document.forms[0].chkDiaSemana[0].checked = false;document.forms[0].chkDiaSemana[0].disabled = true;
                document.forms[0].chkDiaSemana[1].checked = false;document.forms[0].chkDiaSemana[1].disabled = true;
                document.forms[0].chkDiaSemana[2].checked = false;document.forms[0].chkDiaSemana[2].disabled = true;
                document.forms[0].chkDiaSemana[3].checked = false;document.forms[0].chkDiaSemana[3].disabled = true;
                document.forms[0].chkDiaSemana[4].checked = false;document.forms[0].chkDiaSemana[4].disabled = true;
                document.forms[0].chkDiaSemana[5].checked = false;document.forms[0].chkDiaSemana[5].disabled = true;
                document.forms[0].chkDiaSemana[6].checked = false;document.forms[0].chkDiaSemana[6].disabled = true;                    
            }
         }                                   
     } else {
         var i=0;
         for (i=0;i<arrayField.length;i++) {
             var field = document.getElementById(arrayField[i]);                                         
             if (arrayField[i] != "chkDiaSemana") {
               field.disabled = false;
             } else {
                document.forms[0].chkDiaSemana[0].checked = false;document.forms[0].chkDiaSemana[0].disabled = false;
                document.forms[0].chkDiaSemana[1].checked = false;document.forms[0].chkDiaSemana[1].disabled = false;
                document.forms[0].chkDiaSemana[2].checked = false;document.forms[0].chkDiaSemana[2].disabled = false;
                document.forms[0].chkDiaSemana[3].checked = false;document.forms[0].chkDiaSemana[3].disabled = false;
                document.forms[0].chkDiaSemana[4].checked = false;document.forms[0].chkDiaSemana[4].disabled = false;
                document.forms[0].chkDiaSemana[5].checked = false;document.forms[0].chkDiaSemana[5].disabled = false;
                document.forms[0].chkDiaSemana[6].checked = false;document.forms[0].chkDiaSemana[6].disabled = false;
             }
         }               
     }           
 }
 
 function verificaPeriodoHoras(horaIni, horaFim) {
     var PartesHora = new Array();
     PartesHora = horaIni.value.split(':');
     var Hora = PartesHora[0];
     var Minuto = PartesHora[1];        
     PartesHora = horaFim.value.split(':');
     var HoraFim = PartesHora[0];
     var MinutoFim = PartesHora[1];
     
     if (Hora == null || Hora == "" || Minuto == null || Minuto == "") {
         alert("<hl:message key='mensagem.erro.restricao.acesso.hora.inicial.invalida'/>");
         document.forms[0].horaIni = "";
         document.forms[0].horaIni.focus();
         return false;
     }
     
     if (HoraFim == null || HoraFim == "" || MinutoFim == null || MinutoFim == "") {
         alert("<hl:message key='mensagem.erro.restricao.acesso.hora.final.invalida'/>");
         document.forms[0].horaFim = "";
         document.forms[0].horaFim.focus();
         return false;
     }
     
     if (Hora > 23 || Hora < 0 || Minuto < 0 || Minuto > 59 ) {
         alert("<hl:message key='mensagem.erro.restricao.acesso.hora.inicial.invalida'/>");
         document.forms[0].horaIni = "";
         document.forms[0].horaIni.focus();
         return false;
     }
     
     if (HoraFim > 23 || HoraFim < 0 || MinutoFim < 0 || MinutoFim > 59) {
         alert("<hl:message key='mensagem.erro.restricao.acesso.hora.final.invalida'/>");
         document.forms[0].horaFim = "";
         document.forms[0].horaFim.focus();
         return false;
     }
     
     var datai = new Date("01/01/1970");
     var dataf = new Date("01/01/1970");
     
     dataf.setHours(HoraFim);
     dataf.setMinutes(MinutoFim);
              
     datai.setHours(Hora);
     datai.setMinutes(Minuto);  
        
    if (dataf < datai) {
        alert("<hl:message key='mensagem.erro.restricao.acesso.hora.final.deve.ser.maior.hora.inicial'/>");
        return false;
    } else {
        return true;
    }
}
 
 function enviar() {           
     var Controles = new Array("horaIni", "horaFim", "rraDescricao");
     var Msgs = new Array("<hl:message key='mensagem.informe.restricao.acesso.hora.inicio'/>", "<hl:message key='mensagem.informe.restricao.acesso.hora.fim'/>", "<hl:message key='mensagem.informe.restricao.acesso.descricao'/>");
     var horaIni = document.getElementById("horaIni");
     var horaFim = document.getElementById("horaFim");
     
     if (!verificaPeriodoHoras(horaIni, horaFim)) {
         return false;
     }

     if (ValidaCampos(Controles, Msgs)) {
         // document.forms[0].submit();
         return true;
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
