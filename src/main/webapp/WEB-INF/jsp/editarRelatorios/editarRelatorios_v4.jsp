<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.helper.upload.UploadHelper" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ page import="com.zetra.econsig.persistence.entity.TipoFiltroRelatorio"%>
<%@ taglib prefix="t"     tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl"    uri="/html-lib" %>
<%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String relCodigo = (String) request.getAttribute("relCodigo");
String relTitulo = (String) request.getAttribute("relTitulo") != null ? (String) request.getAttribute("relTitulo") : "";
String funDescricao = (String) request.getAttribute("funDescricao") != null ? (String) request.getAttribute("funDescricao") : "";
String itmDescricao = (String) request.getAttribute("itmDescricao") != null ? (String) request.getAttribute("itmDescricao") : "";
List<?> papCodigos = (List<?>) request.getAttribute("papCodigos");
Map<?,?> filtros = (Map<?,?>) request.getAttribute("filtros");
String relTemplateSql = (String) request.getAttribute("relTemplateSql") != null ? (String) request.getAttribute("relTemplateSql") : "";
String relAgendado = (String) request.getAttribute("relAgendado") != null ? (String) request.getAttribute("relAgendado") : "";
String relAgrupamento = (String) request.getAttribute("relAgrupamento") != null ? (String) request.getAttribute("relAgrupamento") : "";
Map<?,?> ordenacao = (Map<?,?>) request.getAttribute("ordenacao");
TransferObject relatorio = (TransferObject) request.getAttribute("relatorio");
String tipo = (String) request.getAttribute("tipo");
List<?> lstPapeis = (List<?>) request.getAttribute("lstPapeis");
List<?> papeis = (List<?>) request.getAttribute("papeis");
Map<?,?> relatorioFiltros = (Map<?,?>)request.getAttribute("relatorioFiltros");
Collection<?> filtrosRelatorio = (Collection<?>)request.getAttribute("filtrosRelatorio");
%>
<c:set var="title">
  <hl:message key="rotulo.editar.relatorio.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-sistema"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
    <div class="col-sm">
      <form method="post" action="../v3/editarRelatorio?acao=salvar&tipo=<%=tipo%>&MM_update=form1&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1" enctype="multipart/form-data">
        <!-- Dados básicos relatório -->
        <div class="card">
          <div class="card-header">
            <h2 class="card-header-title"><hl:message key="rotulo.editar.relatorio.dados"/></h2>
          </div>
          <div class="card-body">
            <div class="row">
              <div class="form-group col-sm-12 col-md-4 mt-1">
                <label for="REL_CODIGO"><hl:message key="rotulo.relatorio.codigo"/></label>
                <input class="form-control" name="REL_CODIGO" id="REL_CODIGO" type="text" 
                  size="10"
                  value="<%=TextHelper.forHtmlAttribute((relatorio != null ? (String)relatorio.getAttribute(Columns.REL_CODIGO) : relCodigo))%>"
                  onFocus="SetarEventoMascara(this,'#E32',true);" 
                  onBlur="fout(this);ValidaMascara(this);" <%=(String)(relatorio != null ? "disabled=\"disabled\"" : "")%>
                />
              </div>
              <div class="form-group col-sm-12 col-md-4 mt-1">
                <label for="REL_TITULO"><hl:message key="rotulo.relatorio.titulo"/></label>
                <input class="form-control" type="text" name="REL_TITULO" 
                value="<%=TextHelper.forHtmlAttribute((relatorio != null ? (String)relatorio.getAttribute(Columns.REL_TITULO) : relTitulo))%>" 
                size="50" 
                onFocus="SetarEventoMascara(this,'#*A50',true);" 
                onBlur="fout(this);ValidaMascara(this);"/>
              </div>
              <div class="form-group col-sm-12 col-md-4 mt-1">
                <label for="FUN_DESCRICAO"><hl:message key="rotulo.relatorio.funcao"/></label>
                <input class="form-control" type="text" name="FUN_DESCRICAO" 
                value="<%=TextHelper.forHtmlAttribute((relatorio != null ? (String)relatorio.getAttribute(Columns.FUN_DESCRICAO) : funDescricao))%>" 
                size="30" 
                onFocus="SetarEventoMascara(this,'#*A30',true);" 
                onBlur="fout(this);ValidaMascara(this);"/>
              </div>
          </div>
          <div class="row">
            <div class="form-group col-sm-12 col-md-4 mt-1">
              <label for="ITM_DESCRICAO"><hl:message key="rotulo.relatorio.menu"/></label>
              <input class="form-control" type="text" name="ITM_DESCRICAO" 
              value="<%=TextHelper.forHtmlAttribute((relatorio != null ? (String)relatorio.getAttribute(Columns.ITM_DESCRICAO) : itmDescricao))%>" 
              size="30" 
              onFocus="SetarEventoMascara(this,'#*A30',true);" 
              onBlur="fout(this);ValidaMascara(this);">
            </div>
            <div class="col-sm-12 col-md-8 mt-1">
              <div class="form-group" role="checkgroup">
                <span id="PAP_CODIGO"><hl:message key="rotulo.relatorio.papeis"/></span>
                <div class="form-check">
                  <div class="row">
                    <div class="col-sm-8 col-md-4">
                      <input type="checkbox" id="papCodigoCse" name="PAP_CODIGO" value="<%=(String)CodedValues.PAP_CONSIGNANTE%>" onClick="habilitaFiltros(this)" <%if ((lstPapeis != null && lstPapeis.contains(CodedValues.PAP_CONSIGNANTE)) || papCodigos != null && papCodigos.contains(CodedValues.PAP_CONSIGNANTE)) {%>checked<%}%>/>
                      <label for="papCodigoCse" class="labelSemNegrito"><hl:message key="rotulo.consignante.singular"/></label>
                    </div>
                    <div class="col-sm-8 col-md-4">
                      <input type="checkbox" id="papCodigoCsa" name="PAP_CODIGO" value="<%=(String)CodedValues.PAP_CONSIGNATARIA%>" onClick="habilitaFiltros(this)" <%if ((lstPapeis != null && lstPapeis.contains(CodedValues.PAP_CONSIGNATARIA)) || papCodigos != null && papCodigos.contains(CodedValues.PAP_CONSIGNATARIA)) {%>checked<%}%>/>
                      <label for="papCodigoCsa" class="labelSemNegrito"><hl:message key="rotulo.consignataria.singular"/></label>
                    </div>
                    <div class="col-sm-8 col-md-4">
                      <input type="checkbox" id="papCodigoOrg" name="PAP_CODIGO" value="<%=(String)CodedValues.PAP_ORGAO%>" onClick="habilitaFiltros(this)" <%if ((lstPapeis != null && lstPapeis.contains(CodedValues.PAP_ORGAO)) || papCodigos != null && papCodigos.contains(CodedValues.PAP_ORGAO)) {%>checked<%}%>/>
                      <label for="papCodigoOrg" class="labelSemNegrito"><hl:message key="rotulo.orgao.singular"/></label>
                    </div>
                    <div class="col-sm-8 col-md-4">
                      <input type="checkbox" id="papCodigoCor" name="PAP_CODIGO" value="<%=(String)CodedValues.PAP_CORRESPONDENTE%>" onClick="habilitaFiltros(this)" <%if ((lstPapeis != null && lstPapeis.contains(CodedValues.PAP_CORRESPONDENTE)) || papCodigos != null && papCodigos.contains(CodedValues.PAP_CORRESPONDENTE)) {%>checked<%}%>/>
                      <label for="papCodigoCor" class="labelSemNegrito"><hl:message key="rotulo.correspondente.singular"/></label>
                    </div>
                    <div class="col-sm-8 col-md-4">
                      <input type="checkbox" id="papCodioSup" name="PAP_CODIGO" value="<%=(String)CodedValues.PAP_SUPORTE%>" onClick="habilitaFiltros(this)" <%if ((lstPapeis != null && lstPapeis.contains(CodedValues.PAP_SUPORTE)) || papCodigos != null && papCodigos.contains(CodedValues.PAP_SUPORTE)) {%>checked<%}%>/>
                      <label for="papCodioSup" class="labelSemNegrito"><hl:message key="rotulo.suporte.singular"/></label>
                    </div>
                 </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- FIM Dados básicos relatório -->
      <%-- Incluir Filtros do relatório --%>
      <div class="card">
          <div class="card-header">
            <h2 class="card-header-title"><hl:message key="rotulo.relatorio.filtros"/></h2>
          </div>
          <div class="card-body">
            <div class="form">
          <%            
            Iterator<?> iteFiltros = filtrosRelatorio.iterator();
            while (iteFiltros.hasNext()) {
                TipoFiltroRelatorio filtro = (TipoFiltroRelatorio)iteFiltros.next();
                String tfrCodigo = filtro.getTfrCodigo();
                String tfrDescricao = filtro.getTfrDescricao();
                TransferObject relatorioFiltro = null;
                if (relatorioFiltros != null && relatorioFiltros.containsKey(tfrCodigo)) {
                    relatorioFiltro = (TransferObject)relatorioFiltros.get(tfrCodigo);
                }
          %>
              <fieldset>
                <div class="legend">
                  <span><%=TextHelper.forHtmlContent(tfrDescricao)%></span>
                </div>
                <div class="form-check">
                  <div class="row">
          <%
              Iterator<?> itePap = papeis.iterator();
              while (itePap.hasNext()) {
                  TransferObject papel = (TransferObject)itePap.next();
                  String papCodigo = papel.getAttribute(Columns.PAP_CODIGO).toString();
                  String papDescricao = papel.getAttribute(Columns.PAP_DESCRICAO).toString();
                  if (!papCodigo.equals(CodedValues.PAP_SERVIDOR)) {
                      String rfiExibe = "0";
                      if (relatorioFiltro != null) {
                        if (papCodigo.equals(CodedValues.PAP_CONSIGNANTE)) {
                          rfiExibe = relatorioFiltro.getAttribute(Columns.RFI_EXIBE_CSE).toString();
                        } else if (papCodigo.equals(CodedValues.PAP_ORGAO)) {
                          rfiExibe = relatorioFiltro.getAttribute(Columns.RFI_EXIBE_ORG).toString();
                        } else if (papCodigo.equals(CodedValues.PAP_CONSIGNATARIA)) {
                            rfiExibe = relatorioFiltro.getAttribute(Columns.RFI_EXIBE_CSA).toString();
                        } else if (papCodigo.equals(CodedValues.PAP_CORRESPONDENTE)) {
                            rfiExibe = relatorioFiltro.getAttribute(Columns.RFI_EXIBE_COR).toString();
                        } else if (papCodigo.equals(CodedValues.PAP_SERVIDOR)) {
                            rfiExibe = relatorioFiltro.getAttribute(Columns.RFI_EXIBE_SER).toString();
                        } else if (papCodigo.equals(CodedValues.PAP_SUPORTE)) {
                            rfiExibe = relatorioFiltro.getAttribute(Columns.RFI_EXIBE_SUP).toString();
                        }
                      } else if (filtros != null) {
                            Map<?,?> paps = (Map<?,?>)filtros.get(tfrCodigo);
                            rfiExibe = paps != null ? (!TextHelper.isNull((String)paps.get(papCodigo)) ? (String)paps.get(papCodigo) : "") : "";
                      }
                      
                      boolean enabled = (lstPapeis != null && (lstPapeis.contains(papCodigo))) || (papCodigos != null && (papCodigos.contains(papCodigo)));
          %>
                <div class="col-sm-12 col-md-2">
                  <label for="<%=papCodigo%>" class="labelSemNegrito"><%=papDescricao%></label>
                  <SELECT NAME="FILTRO" CLASS="form-control form-select" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascara(this);" onChange="atualiza(this, document.forms[0].ORDENACAO, '<%=TextHelper.forJavaScript(tfrDescricao)%>', '<%=TextHelper.forJavaScript(tfrCodigo)%>');atualizaFiltroQuery(this, document.forms[0].FILTRO_QUERY, '<%=TextHelper.forJavaScript(tfrCodigo)%>');" <%if (!enabled) {%>disabled="disabled"<%}%>>
                    <OPTION VALUE="<%=TextHelper.forHtmlAttribute(papCodigo+";"+tfrCodigo+";"+CodedValues.REL_FILTRO_NAO_EXISTENTE)%>" <%if (rfiExibe.equals(CodedValues.REL_FILTRO_NAO_EXISTENTE)) {%>SELECTED<%}%>><hl:message key="rotulo.relatorio.ausente"/></OPTION>
                    <OPTION VALUE="<%=TextHelper.forHtmlAttribute(papCodigo+";"+tfrCodigo+";"+CodedValues.REL_FILTRO_EXISTENTE)%>" <%if (rfiExibe.equals(CodedValues.REL_FILTRO_EXISTENTE)) {%>SELECTED<%}%>><hl:message key="rotulo.relatorio.opcional"/></OPTION>
                    <OPTION VALUE="<%=TextHelper.forHtmlAttribute(papCodigo+";"+tfrCodigo+";"+CodedValues.REL_FILTRO_OBRIGATORIO)%>" <%if (rfiExibe.equals(CodedValues.REL_FILTRO_OBRIGATORIO)) {%>SELECTED<%}%>><hl:message key="rotulo.relatorio.obrigatorio"/></OPTION>
                  </SELECT>
                </div>
          <%
                  }
              }
          %>
                </div>
              </div>
            </fieldset> 
          <%
            }
          %>
            <fieldset>
              <div class="legend">
                <span><hl:message key="rotulo.relatorio.ordem.filtros"/></span>
              </div>
            </fieldset>
         </div>
            <div class="row">
              <div class="form-group col-sm-12 col-md-5 mt-2">
                        <SELECT class="form-control form-select w-100" NAME="ORDENACAO" SIZE="4" >
                        <%
                          List<Object> filtroOrd = null; 
                          if (relatorioFiltros != null) {
                            filtroOrd = new ArrayList<>();
                            filtroOrd.addAll(relatorioFiltros.values());
                            
                          } else if (ordenacao != null) {
                              filtroOrd = new ArrayList<>();
                              Iterator<?> itOrder = ordenacao.keySet().iterator();
                              while (itOrder.hasNext()) {
                                String tfrCodigo = itOrder.next().toString();
                                TransferObject to = new CustomTransferObject();
                                to.setAttribute(Columns.TFR_CODIGO, tfrCodigo);
                                to.setAttribute(Columns.RFI_SEQUENCIA, ordenacao.get(tfrCodigo).toString());
                                filtroOrd.add(to);
                              }
                          }
                          
                          if (filtroOrd != null) {
                            Collections.sort(filtroOrd, new Comparator() {
                                public int compare(Object o1, Object o2) {
                                    if (o1 == null) {
                                        return (o2 == null) ? 0 : 1;
                                    } else {
                                        return ((TransferObject)o1).getAttribute(Columns.RFI_SEQUENCIA).toString().compareTo(((TransferObject)o2).getAttribute(Columns.RFI_SEQUENCIA).toString());
                                    }
                                }
                            });
                            
                            Map<String,String> mapTfr = new HashMap<>();
                            Iterator<?> iteTfr = filtrosRelatorio.iterator();
                            while (iteTfr.hasNext()) {
                                TipoFiltroRelatorio tfr = (TipoFiltroRelatorio)iteTfr.next();
                                mapTfr.put(tfr.getTfrCodigo(), tfr.getTfrDescricao());
                            }
    
                            Iterator<?> iteFiltroOrd = filtroOrd.iterator();
                            while (iteFiltroOrd.hasNext()) {
                                TransferObject to = (TransferObject)iteFiltroOrd.next();
                                String tfrCodigo = to.getAttribute(Columns.TFR_CODIGO).toString();
                                if (!tfrCodigo.equals("campo_formato_relatorio") && !tfrCodigo.equals("campo_data_execucao") && 
                                    !tfrCodigo.equals("campo_tipo_agendamento") && !tfrCodigo.equals("campo_periodicidade")) {
                        %>
                          <OPTION VALUE="<%=TextHelper.forHtmlAttribute(tfrCodigo)%>"><%=TextHelper.forHtmlContent(mapTfr.get(tfrCodigo).toString())%></OPTION>
                        <%        
                                }
                            }
                          }
                        %>
                        </SELECT>
               </div>
               <div class="form-group col-sm-12 col-md-1 p-0 mt-0">
                  <a class="btn btn-primary btn-ordenacao pr-0 mt-2" href="#no-back" onClick="move(document.forms[0].ORDENACAO, document.forms[0].ORDENACAO.selectedIndex, -1); setaOrdenacao(); return false;">
                    <svg width="15">
                     <use xlink:href="#i-avancar"></use>
                    </svg>
                  </a>
                  <a class="btn btn-primary btn-ordenacao pr-0 mt-2" href="#no-back" onClick="move(document.forms[0].ORDENACAO, document.forms[0].ORDENACAO.selectedIndex, +1); setaOrdenacao(); return false;">
                    <svg width="15">
                     <use xlink:href="#i-voltar"></use>
                    </svg>
                  </a>
               </div>
              <hl:htmlinput type="hidden" name="ORDENACAO_AUX" di="ORDENACAO_AUX" value="" />
        </div>
      </div>
    </div>
    <%-- FIM Filtros do relatório --%>
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><hl:message key="mensagem.informe.relatorio.parametros"/></h2>
      </div>
      <div class="card-body">
        <div class="row">
           <div class="form-group col-sm-6">
            <label for="REL_TEMPLATE_SQL"><hl:message key="rotulo.relatorio.consulta"/></label>
            <hl:htmlinput name="REL_TEMPLATE_SQL"
                             onFocus="SetarEventoMascara(this,'#*65000',true);"
                             type="textarea"
                             classe="form-control"
                             onBlur="fout(this);ValidaMascara(this);"    
                             rows="15"                   
                             cols="80"   
                             value="<%=TextHelper.forHtmlContent((relatorio != null ? relatorio.getAttribute(Columns.REL_TEMPLATE_SQL).toString() : relTemplateSql))%>"
             />
           </div>
           <div class="form-group col-sm-6">
              <label for="FILTRO_QUERY"><hl:message key="rotulo.relatorio.filtros.query"/></label>
              <SELECT class="form-control form-select w-100" NAME="FILTRO_QUERY" SIZE="13" ondblclick="incluiCampoQuery(this);">
              <%
                if (filtroOrd != null) {
                  Iterator<?> iteFiltroOrd = filtroOrd.iterator();
                  while (iteFiltroOrd.hasNext()) {
                      TransferObject to = (TransferObject)iteFiltroOrd.next();
                      String tfrCodigo = to.getAttribute(Columns.TFR_CODIGO).toString();
                      String valorFiltroQuery = "<@" + tfrCodigo + ">";
                      if (tfrCodigo.equals("campo_data_inclusao")) {
              %>
                <OPTION VALUE="<@campo_data_inclusao_ini>"></OPTION>
                <OPTION VALUE="<@campo_data_inclusao_fim>"></OPTION>
              <%        
                      } else if (!tfrCodigo.equals("campo_formato_relatorio") && !tfrCodigo.equals("campo_data_execucao") && 
                                 !tfrCodigo.equals("campo_tipo_agendamento") && !tfrCodigo.equals("campo_periodicidade")) {
              %>
                <OPTION VALUE="<%=TextHelper.forHtmlAttribute(valorFiltroQuery)%>"><%=TextHelper.forHtmlContent(valorFiltroQuery)%></OPTION>
              <%        
                      }
                  }
                }
              %>
              </SELECT>
              </div>
          </div>
          <div class="row">
            <div class="col-sm-12 col-md-6">
                <span id="descricao"><hl:message key="rotulo.relatorio.agendado"/></span>
              <div class="form-group mb-1" role="radiogroup" aria-labelledby="agDescricao">
                <div class="form-check form-check-inline">
                  <input class="form-check-input ml-1" type="radio" name="REL_AGENDADO" id="agendadoSim" title='<hl:message key="rotulo.sim"/>' VALUE="<%=(String)CodedValues.TPC_SIM%>" <%=(String)(relatorio != null && relatorio.getAttribute(Columns.REL_AGENDADO).toString().equals(CodedValues.TPC_SIM) ? "checked" : (relatorio == null && relAgendado.equals(CodedValues.TPC_SIM) ? "checked" : ""))%> <%= JspHelper.verificaVarQryStr(request, "agendado").equals("true") ? "checked" : "" %> onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);">
                  <label class="form-check-label labelSemNegrito ml-1 pr-4" for="agendadoSim"><hl:message key="rotulo.sim"/></label>
                </div>
                  <div class="form-check form-check-inline">
                    <input class="form-check-input ml-1" type="radio" name="REL_AGENDADO" id="agendadoNao" title='<hl:message key="rotulo.nao"/>' VALUE="<%=(String)CodedValues.TPC_NAO%>" <%=(String)(relatorio != null && !relatorio.getAttribute(Columns.REL_AGENDADO).toString().equals(CodedValues.TPC_SIM) ? "checked" : (relatorio == null && !relAgendado.equals(CodedValues.TPC_SIM) ? "checked" : ""))%> onFocus="SetarEventoMascara(this,'#*100',true);" onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);">
                  <label class="form-check-label labelSemNegrito ml-1 pr-4" for="agendadoNao"><hl:message key="rotulo.nao"/></label>
                </div>
              </div>
            </div>
          </div>
          <div class="row">
            <div class="form-group col-sm-12 col-md-6 mt-1">
              <label for="REL_AGRUPAMENTO"><hl:message key="rotulo.relatorio.agrupamento"/></label>
              <input class="form-control" name="REL_AGRUPAMENTO" id="REL_AGRUPAMENTO" 
                type="text" 
                value="<%=TextHelper.forHtmlAttribute((relatorio != null ? (String)relatorio.getAttribute(Columns.REL_AGRUPAMENTO) : relAgrupamento))%>"  
                size="40" onFocus="SetarEventoMascara(this,'#*40',true);" 
                onBlur="fout(this);ValidaMascara(this);">
            </div>
          </div>
              <hl:fileUploadV4 obrigatorio="<%=false%>" mostraCampoDescricao="<%=false%>" nomeCampoArquivo="FILE1" tituloCampoArquivo="<%=ApplicationResourcesHelper.getMessage("rotulo.relatorio.template.jasper", responsavel)%>" multiplo="false" extensoes="<%=UploadHelper.EXTENSOES_PERMITIDAS_SUBRELATORIO%>" tipoArquivo="relatorio_jasper" divClassArquivo="form-group col-sm-6 mt-2"/>
              <% if (relatorio != null && !TextHelper.isNull(relatorio.getAttribute(Columns.REL_TEMPLATE_JASPER)) && !relatorio.getAttribute(Columns.REL_TEMPLATE_JASPER).toString().equals(CodedValues.TEMPLATE_REL_EDITAVEL_JASPER)) { %>
              <input type="checkbox" name="REMOVE_TEMPLATE" id="REMOVE_TEMPLATE" value="<%=(String)CodedValues.TPC_SIM%>" />
              <label for="REMOVE_TEMPLATE"><hl:message key="rotulo.relatorio.remover.template.jasper"/></label>
              <% } %> 
          </div>
        </div>
        <div class="btn-action">
          <a class="btn btn-outline-danger" id="btnVoltar" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.cancelar"/></a>
          <a class="btn btn-primary" id="btnConfirmar" href="#no-back" onClick="if(checkForDml() && verificaCampos()){f0.submit();} return false;"><hl:message key="rotulo.botao.confirmar"/></a>
        </div>
        <hl:htmlinput type="hidden" name="FUN_CODIGO" di="FUN_CODIGO" value="<%=TextHelper.forHtmlAttribute((relatorio != null ? (String)relatorio.getAttribute(Columns.REL_FUN_CODIGO) : ""))%>" />
        <hl:htmlinput type="hidden" name="TAG_CODIGO" di="TAG_CODIGO" value="<%=TextHelper.forHtmlAttribute((relatorio != null && !TextHelper.isNull(relatorio.getAttribute(Columns.REL_TAG_CODIGO)) ? (String)relatorio.getAttribute(Columns.REL_TAG_CODIGO) : ""))%>" />
        <hl:htmlinput type="hidden" name="ITM_CODIGO" di="ITM_CODIGO" value="<%=TextHelper.forHtmlAttribute((relatorio != null ? (String)relatorio.getAttribute(Columns.ITM_CODIGO) : ""))%>" />
        <input name="tipo" type="hidden" value="<%=tipo%>">
      </form>   
    </div>
  </div>
</c:set>
<c:set var="javascript">
<hl:fileUploadV4 botaoVisualizarRemover="<%=false%>" multiplo="false" scriptOnly="true" nomeCampoArquivo="FILE1" extensoes="<%=UploadHelper.EXTENSOES_PERMITIDAS_SUBRELATORIO%>" tipoArquivo="relatorio_jasper"/>
  <script type="text/JavaScript">
    function formLoad() {
    	  if (f0.REL_CODIGO != null && !f0.REL_CODIGO.disabled) {
    	  	f0.REL_CODIGO.focus();
    	  } else {
    		f0.REL_TITULO.focus();
    	  }
    	}
    
    	// Verifica a presença de requisições de alteração do banco, evito o submit
    	function checkForDml() {
    	  var requisicoes = new Array ("INSERT", "UPDATE", "DELETE", "DROP", "CREATE", "ALTER", "TRUNCATE");
    	  var sql = f0.REL_TEMPLATE_SQL.value;
    	  for (var i = 0; i < requisicoes.length; i++) {
    	    var requisicao = requisicoes[i];
    	    if (sql.toUpperCase().indexOf(requisicao) > -1) {
    	      alert('<hl:message key="mensagem.erro.relatorio.palavra.reservada"/>'.replace("{0}", requisicao));
    	      return false;
    	    }
    	  }
    	  return true;
    	}  
    
    	function verificaCampos() {
    	  var controles = new Array("REL_CODIGO", "REL_TITULO", "REL_TEMPLATE_SQL");
    	  var msgs = new Array ('<hl:message key="mensagem.informe.relatorio.codigo"/>',
    	                        '<hl:message key="mensagem.informe.relatorio.titulo"/>',
    	                        '<hl:message key="mensagem.informe.relatorio.consulta"/>');
    
    	  if (!ValidaCampos(controles, msgs)) {
    	    return false;
    	  }
    
    	  // Verifica se um papel foi escolhido
    	  var checked = false;
    	  var aChk = document.getElementsByName("PAP_CODIGO");
    	  for (var i = 0; i < aChk.length; i++) {
    	    if (aChk[i].checked == true) {
    	      checked = true;
    	    }
    	  }
    
    	  if (!checked) {
    		alert('<hl:message key="mensagem.informe.relatorio.papeis"/>');
    	    return false;
    	  }
    	  
    	  // Verifica filtros
    	  with(document.forms[0]) {
    	    // Verifica se todos os filtros selecionados foram incluídos na query
    	    for (var i = 0; i < FILTRO_QUERY.length; i++) {
    	      var filtroQuery = FILTRO_QUERY.options[i].value;
    	   	  if (REL_TEMPLATE_SQL.value.indexOf(filtroQuery) < 0) {
    	   	    alert('<hl:message key="mensagem.informe.relatorio.filtro.query"/>'.replace("{0}", filtroQuery));
    	   	    return false;
    	   	  }
    	    }
    
    	    // Verifica se todos os filtros incluídos na query foram selecionados
    	    var filtrosQuery = REL_TEMPLATE_SQL.value.match(/\<[@][A-Za-z0-9_\-\.]+\>/g);
    	    if (filtrosQuery != null) {
    	      for (var x = 0; x < filtrosQuery.length; x++) {
    	        var possuiFiltro = false;
    	   	    for (var i = 0; i < FILTRO_QUERY.length; i++) {
    	   	      if (FILTRO_QUERY.options[i].value == filtrosQuery[x]) {
    	   	    	possuiFiltro = true;
    	   	      }
    	   	    }
    		    if (!possuiFiltro) {
    	   	      alert('<hl:message key="mensagem.informe.relatorio.filtro"/>'.replace("{0}", filtrosQuery[x]));
    	   	      return false;
    	   	    }
    	      }
    	    }
    	  }
    
    	  setaOrdenacao();
    
    	  f0.REL_CODIGO.disabled = false;
    
    	  return true;
    	}
    
    	function filtroAtivo(value) {
    	  value = value.replace('<@', '').replace('>', '');
    	  var filtros = document.getElementsByTagName('select');
    	  for(var i = 0; i < filtros.length; i++) {
    	    var filtro = filtros[i];
    	    if(filtro.name == 'FILTRO' && !filtro.disabled) {
    	      var valor = filtro.options[filtro.selectedIndex].value;
    	      var papCodigo = valor.substring(0, valor.indexOf(';'));
    	      var tfrCodigo = valor.substring(valor.indexOf(';') + 1, valor.lastIndexOf(';'));
    	      var papFiltro = valor.substring(valor.lastIndexOf(';') + 1, valor.length);
    	      
    		  if (value == tfrCodigo && papFiltro != '<%=(String)CodedValues.REL_FILTRO_NAO_EXISTENTE%>') {
    			return true;
    		  }      
    	    }
    	  }
    	  
    	  return false;
    	}
    
    	function habilitaFiltros(papel) {
    	  var papCodigo = papel.value;
    	  var checked = papel.checked;
    	  var filtros = document.getElementsByTagName('select');
    	  
    	  if (filtros == undefined || filtros.length == undefined || filtros.length == 0) {
    		alert('<hl:message key="mensagem.informe.um.item"/>');
    		return false;
    	  } else {
    	    for(var i = 0; i < filtros.length; i++) {
    	  	  var filtro = filtros[i];
    	      if(filtro.name == 'FILTRO') {
    	        var proximo = filtro.options[0].value;
    	        var papFiltro = proximo.substring(0, proximo.indexOf(';'));
    	        if (papCodigo == papFiltro) {
    	       	  filtro.disabled = !checked;
    	        }
    	      }
    	    }
    	  }
    	}
    
    	function setaOrdenacao() {
    	  if (f0.ORDENACAO != null && f0.ORDENACAO.length > 0 && f0.ORDENACAO_AUX != null) {
    		atribui_ordenacao.call();
    	  }  
    	}
    
    	function atribui_ordenacao() {
    	  var ordenacao = "";              
    	  with(document.forms[0]) {
    	     for (var i = 0; i < ORDENACAO.length; i++) {
    	       ordenacao += ORDENACAO.options[i].value;
    	       if (i < ORDENACAO.length - 1) {
    	         ordenacao += ",";
    	       }
    	     }
    	     ORDENACAO_AUX.value = ordenacao;
    	  }
    	  return true;
    	}
    
    	function atualizaCombo(box, name, value, check) {
    	  if (!check) {
    	    if (!exists(box,name,value)) {
    	      add(box,name,value); 
    	    }
    	  } else {
    	    if (!filtroAtivo(value)) {
    	      remove(box,name,value);
    	    }
    	  }
    	  setaOrdenacao();
    	}
    
    	function atualiza(filtro, box, name, value) {
    	  var valor = filtro.options[filtro.selectedIndex].value;
    	  var papFiltro = valor.substring(valor.lastIndexOf(';') + 1, valor.length);
    	  var check = (papFiltro == '<%=(String)CodedValues.REL_FILTRO_NAO_EXISTENTE%>');
    	  
    	  atualizaCombo(box, name, value, check);
    	}
    
    	function atualizaFiltroQuery(filtro, box, value) {
    	  var valor = filtro.options[filtro.selectedIndex].value;
    	  var tfrCodigo = valor.substring(valor.indexOf(';') + 1, valor.lastIndexOf(';'));
    	  var papFiltro = valor.substring(valor.lastIndexOf(';') + 1, valor.length);
    	  var check = (papFiltro == '<%=(String)CodedValues.REL_FILTRO_NAO_EXISTENTE%>');
    	  
    	  if (tfrCodigo == 'campo_data_inclusao') {
    	    atualizaCombo(box, '<@campo_data_inclusao_ini>', '<@campo_data_inclusao_ini>', check);
    	    atualizaCombo(box, '<@campo_data_inclusao_fim>', '<@campo_data_inclusao_fim>', check);
    	  } else {
    	    var name = '<@' + value + '>';
    	    atualizaCombo(box, name, name, check);
    	  }
    	}
    
    	function incluiCampoQuery(filtro) {
    	  var valor = filtro.options[filtro.selectedIndex].value;
    	  insertAtCursor(f0.REL_TEMPLATE_SQL, valor);
    	}
    
    	function insertAtCursor(myField, myValue) {
    		if (document.selection) {
    		 	//IE support
    	    	myField.focus();
    	    	sel = document.selection.createRange();
    	    	sel.text = myValue;
    		} else if (myField.selectionStart || myField.selectionStart == '0') {
    			//MOZILLA/NETSCAPE support
    	    	var startPos = myField.selectionStart;
    	    	var endPos = myField.selectionEnd;
    	    	myField.value = myField.value.substring(0, startPos) + myValue + myField.value.substring(endPos, myField.value.length);
    		} else {
    			myField.value += myValue;
    		}
    	}
      var f0 = document.forms[0];
      setaOrdenacao();
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>