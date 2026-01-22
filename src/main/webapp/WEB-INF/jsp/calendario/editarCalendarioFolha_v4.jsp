<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.zetra.econsig.helper.periodo.PeriodoHelper" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.web.CalendarioFolhaModel" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
CalendarioFolhaModel calModel = (CalendarioFolhaModel) request.getAttribute("calModel");
%>
<c:set var="title">
<hl:message key="rotulo.calendario.folha.titulo"/>
</c:set>
<c:set var="imageHeader">
<use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
      <form method="post" action="../v3/editarCalendarioFolha?<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
      
        <input type="hidden" name="acao" id="acao" value="editar">
        <input type="hidden" name="updateDiaCorteGeral" id="updateDiaCorteGeral" value="0">
        <input type="hidden" name="ano" id="ano" value="<%=calModel.getAno()%>">
        <input type="hidden" name="replicarQuinzenal" id="replicarQuinzenal" value="false"/>
      
        <% if (responsavel.isCseSup() || (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO))) { %>
        <div class="card">
          <div class="card-header">
            <h2 class="card-header-title"><hl:message key="rotulo.calendario.folha.selecione.entidade"/></h2>
          </div>
          <div class="card-body">
            <div class="row">
              <div class="form-group col-sm-12">
                <span id="iEntidade"><hl:message key="rotulo.calendario.folha.entidade"/></span>
                <br/>
                <div class="form-check form-check-inline mt-2" role="radio-group" area-labeldbay="iEntidade">
                <% if (responsavel.isCseSup()) { %>
                  <input class="form-check-input mt-1 ml-1" type="radio" name="tipoEntidade" id="tipoEntidadeGeral" onChange="alterarTipoEntidade()" value="<%=(String)AcessoSistema.ENTIDADE_CSE%>" <% if (TextHelper.isNull(calModel.getTipoEntidade()) || calModel.getTipoEntidade().equals(AcessoSistema.ENTIDADE_CSE)) {%>checked<% } %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                  <label class="form-check-label labelSemNegrito ml-1 pr-4 pt-1" for="tipoEntidadeGeral"><hl:message key="rotulo.geral.singular"/></label>
                  <% } %>
                </div>
                <div class="form-check-inline form-check">
                  <input class="form-check-input mt-1 ml-1" type="radio" name="tipoEntidade" id="tipoEntidadeEstabelecimento" onChange="alterarTipoEntidade()" value="<%=(String)AcessoSistema.ENTIDADE_EST%>" <% if (calModel.getTipoEntidade().equals(AcessoSistema.ENTIDADE_EST)) {%>checked<% } %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                  <label class="form-check-label labelSemNegrito ml-1 pr-4 pt-1" for="tipoEntidadeEstabelecimento"><hl:message key="rotulo.estabelecimento.singular"/></label>
                </div>
                <div class="form-check-inline form-check">
                  <input class="form-check-input mt-1 ml-1" type="radio" name="tipoEntidade" id="tipoEntidadeOrgao" onChange="alterarTipoEntidade()" value="<%=(String)AcessoSistema.ENTIDADE_ORG%>" <% if (calModel.getTipoEntidade().equals(AcessoSistema.ENTIDADE_ORG)) {%>checked<% } %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                  <label class="form-check-label labelSemNegrito ml-1 pr-4 pt-1" for="tipoEntidadeOrgao"><hl:message key="rotulo.orgao.singular"/></label>
                </div>
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm-12">
                <label for="estCodigo"><hl:message key="rotulo.estabelecimento.singular"/></label>
               <%=JspHelper.geraCombo(calModel.getLstEstabelecimentos(), "estCodigo", Columns.EST_CODIGO, Columns.EST_NOME + ";" + Columns.EST_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel) , "onChange=\"recarregar()\"", false, 1, calModel.getEstCodigo(), null, !calModel.getTipoEntidade().equals(AcessoSistema.ENTIDADE_EST), "form-control")%>
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm-12">
                <label for="orgCodigo"><hl:message key="rotulo.orgao.singular"/></label>
                <%=JspHelper.geraCombo(calModel.getLstOrgaos(), "orgCodigo", Columns.ORG_CODIGO, Columns.ORG_NOME + ";" + Columns.ORG_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), "onChange=\"recarregar()\"", false, 1, calModel.getOrgCodigo(), null, !calModel.getTipoEntidade().equals(AcessoSistema.ENTIDADE_ORG), "form-control")%>
              </div>
            </div>
          </div>
       </div>
       <% } %>
        <div class="card">
          <div class="card-header">
            <h2 class="card-header-title"><hl:message key="rotulo.calendario.folha.ano" arg0="<%=TextHelper.forHtmlContent(calModel.getAno())%>"/> </h2>
          </div>
          <div class="card-body table-responsive p-0">
              <table class="table table-striped table-hover">
                <thead>
                  <tr>
                    <th scope="col" id="iPeriodo" width="15%"><hl:message key="rotulo.calendario.folha.periodo"/></th>
                    <th scope="col" id="iCorte" width="12%"><hl:message key="rotulo.calendario.folha.corte"/></th>
                    <th scope="col" id="iDataInicio"><hl:message key="rotulo.calendario.folha.data.inicio"/></th>
                    <th scope="col" id="iDataFim"><hl:message key="rotulo.calendario.folha.data.fim"/></th>
                    <% if (calModel.isHabilitaDataFiscal()) { %>
                      <th scope="col" id="iDataInicioFiscal"><hl:message key="rotulo.calendario.folha.data.inicio.fiscal"/></th>
                      <th scope="col" id="iDataFimFiscal"><hl:message key="rotulo.calendario.folha.data.fim.fiscal"/></th>
                    <%} %>
                    <% if (calModel.isHabilitaPeriodoAjustes()) { %>
                      <th scope="col" id="iFimAjustes"><hl:message key="rotulo.calendario.folha.data.fim.ajustes"/></th>
                    <% } %>
                    <% if (calModel.isHabilitaDataPrevistaRetorno()) { %>
                      <th scope="col" id="iDataPrevistaRetorno"><hl:message key="rotulo.calendario.folha.data.prevista.retorno"/></th>
                    <% } %>
                    <% if (calModel.isPermiteApenasReducoes()) { %>
                      <th scope="col" id="iApenasReducoes"><hl:message key="rotulo.calendario.folha.apenas.reducoes"/></th>
                    <% } %>
                  </tr>
                </thead>
                <tbody>
                
                <%
                      for (int contador = 1; contador <= calModel.getQtdPeriodos(); contador++) {
                        boolean proxAno = (contador == calModel.getQtdPeriodos());
                        Map<Integer, TransferObject> calendarioAno = calModel.getCalendarioAno();
                        if (proxAno) { 
                            calendarioAno = calModel.getCalendarioAnoProximoAno();
                        }
                        
                        String periodo = String.format("%02d", proxAno ? 1 : contador) + "/" + (proxAno ? calModel.getAno() + 1 : calModel.getAno());
                        TransferObject calendario = (TransferObject) calendarioAno.get(Integer.valueOf(proxAno ? 1 : contador));
                        String dataIni = (calendario != null && calendario.getAttribute(calModel.getNomeCampoDataIni()) != null) ? DateHelper.toDateString((Date) calendario.getAttribute(calModel.getNomeCampoDataIni())) : "";
                        String dataFim = (calendario != null && calendario.getAttribute(calModel.getNomeCampoDataFim()) != null) ? DateHelper.toDateString((Date) calendario.getAttribute(calModel.getNomeCampoDataFim())) : "";
                        String dataFimAjustes = (calendario != null && calendario.getAttribute(calModel.getNomeCampoDataFimAjustes()) != null) ? DateHelper.toDateString((Date) calendario.getAttribute(calModel.getNomeCampoDataFimAjustes())) : "";
                        String diaCorte = (calendario != null && calendario.getAttribute(calModel.getNomeCampoDiaCorte()) != null) ? calendario.getAttribute(calModel.getNomeCampoDiaCorte()).toString() : "";
                        String dataPrevistaRetorno = (calendario != null && calendario.getAttribute(calModel.getNomeCampoDataPrevistaRetorno()) != null) ? DateHelper.toDateString((Date) calendario.getAttribute(calModel.getNomeCampoDataPrevistaRetorno())) : "";
                        String dataIniFiscal = calendario != null && calendario.getAttribute(calModel.getNomeCampoDataIniFiscal()) != null ? DateHelper.toDateString((Date) calendario.getAttribute(calModel.getNomeCampoDataIniFiscal())) : "";
                        String dataFimFiscal = calendario != null && calendario.getAttribute(calModel.getNomeCampoDataFimFiscal()) != null ? DateHelper.toDateString((Date) calendario.getAttribute(calModel.getNomeCampoDataFimFiscal())) : "";
                        int anoCalendario = (calendario != null && calendario.getAttribute(calModel.getNomeCampoDataFim()) != null) ? DateHelper.getYear((Date) calendario.getAttribute(calModel.getNomeCampoDataFim())) : calModel.getAno();
                        boolean apenasReducoes = (calendario != null && calendario.getAttribute(calModel.getNomeCampoApenasReducoes()) != null) ? calendario.getAttribute(calModel.getNomeCampoApenasReducoes()).equals("S") : false;
                        boolean anteriorDataAtual = (calendario != null && calendario.getAttribute(calModel.getNomeCampoDataFim()) != null && DateHelper.getSystemDatetime().after((Date) calendario.getAttribute(calModel.getNomeCampoDataFim())));

                        if (JspHelper.verificaVarQryStr(request, "acao").equals("editar")) {
                            // Recupera valores do request, caso não tenha valores salvos
                            diaCorte = TextHelper.isNull(diaCorte) ? JspHelper.verificaVarQryStr(request, "diaCorte_" + contador) : diaCorte;
                            dataIni  = TextHelper.isNull(dataIni)  ? JspHelper.verificaVarQryStr(request, "dataIni_"  + contador) : dataIni;
                            dataFim  = TextHelper.isNull(dataFim)  ? JspHelper.verificaVarQryStr(request, "dataFim_"  + contador) : dataFim;
                            dataIniFiscal = TextHelper.isNull(dataIniFiscal) ? JspHelper.verificaVarQryStr(request, "dataIniFiscal_" + contador) : dataIniFiscal;
                            dataFimFiscal = TextHelper.isNull(dataFimFiscal) ? JspHelper.verificaVarQryStr(request, "dataFimFiscal_" + contador) : dataFimFiscal;
                            dataFimAjustes = TextHelper.isNull(dataFimAjustes) ? JspHelper.verificaVarQryStr(request, "dataFimAjustes_" + contador) : dataFimAjustes;
                            dataPrevistaRetorno = TextHelper.isNull(dataPrevistaRetorno) ? JspHelper.verificaVarQryStr(request, "dataPrevistaRetorno_" + contador) : dataPrevistaRetorno;
                        }
                %>
                
                  <tr>
                    <td><%=TextHelper.forHtmlContent(periodo)%></td>
                    <td>
                      <hl:htmlinput name="<%=(String)("diaCorte_" + (int)contador)%>"
                            di="<%=(String)("diaCorte_" + (int)contador)%>"
                            type="text"
                            classe="form-control pl-1 pr-1"
                            value="<%=TextHelper.forHtmlAttribute(diaCorte)%>"
                            size="3"
                            maxlength="2"
                            mask="#D3"
                            nf="<%=(String)(contador < calModel.getQtdPeriodos() - 2 ? "diaCorte_" + ((int)contador + 1) : "btnSalvar")%>"
                            others="<%=(String)(!calModel.isPodeEditarCalendario() || anteriorDataAtual || proxAno ? "disabled" : "onChange='alterarValorCorte(this, " + (int)anoCalendario + ")'")%>"
                         />
                    </td>
                    <td>
                      <hl:htmlinput name="<%=(String)("dataIni_" + (int)contador)%>"
                          di="<%=(String)("dataIni_" + (int)contador)%>"
                          type="text"
                          classe="form-control pl-1 pr-1"
                          value="<%=TextHelper.forHtmlAttribute(dataIni)%>"
                          size="10"
                          maxlength="10"
                          mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                          others="<%=(String)(!calModel.isPodeEditarCalendario() || anteriorDataAtual || proxAno ? "disabled" : "onChange='alterarValorCorte(" + (String)("diaCorte_" + (int)contador) + ", " + (int)anoCalendario + ")'")%>"
                       />
                    </td>
                    <td>
                      <hl:htmlinput name="<%=(String)("dataFim_" + (int)contador)%>"
                          di="<%=(String)("dataFim_" + (int)contador)%>"
                          type="text"
                          classe="form-control pl-1 pr-1"
                          value="<%=TextHelper.forHtmlAttribute(dataFim)%>"
                          size="10"
                          maxlength="10"
                          mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                          others="<%=(String)(!calModel.isPodeEditarCalendario() || anteriorDataAtual || proxAno ? "disabled" : "onChange='alterarValorCorte(" + (String)("diaCorte_" + (int)contador) + ", " + (int)anoCalendario + ")'")%>"
                      />
                    </td>
                    <% if (calModel.isHabilitaDataFiscal()) { %>
                      <td>
                        <hl:htmlinput name="<%=(String)("dataInicioFiscal_" + (int)contador)%>"
                          di="<%=(String)("dataInicioFiscal_" + (int)contador)%>"
                          type="text"
                          classe="form-control pl-1 pr-1"
                          value="<%=TextHelper.forHtmlAttribute(dataIniFiscal)%>"
                          size="10"
                          maxlength="10"
                          mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                          others="<%=(String)(!calModel.isPodeEditarCalendario() || anteriorDataAtual || proxAno ? "disabled" : "")%>"                          
                        />
                      </td>
                      <td>
                        <hl:htmlinput name="<%=(String)("dataFimFiscal_" + (int)contador)%>"
                          di="<%=(String)("dataFimFiscal_" + (int)contador)%>"
                          type="text"
                          classe="form-control pl-1 pr-1"
                          value="<%=TextHelper.forHtmlAttribute(dataFimFiscal)%>"
                          size="10"
                          maxlength="10"
                          mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                          others="<%=(String)(!calModel.isPodeEditarCalendario() || anteriorDataAtual || proxAno ? "disabled" : "")%>"                          
                        />
                      </td>
                    <% } %>
                    <% if (calModel.isHabilitaPeriodoAjustes()) { %>
                      <td>
                        <hl:htmlinput name="<%=(String)("dataFimAjustes_" + (int)contador)%>"
                          di="<%=(String)("dataFimAjustes_" + (int)contador)%>"
                          type="text"
                          classe="form-control pl-1 pr-1"
                          value="<%=TextHelper.forHtmlAttribute(dataFimAjustes)%>"
                          size="10"
                          maxlength="10"
                          mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                          others="<%=(String)(!calModel.isPodeEditarCalendario() || anteriorDataAtual || proxAno ? "disabled" : "")%>"
                        />
                      </td>
                    <% } %>
                    <% if (calModel.isHabilitaDataPrevistaRetorno()) { %>
                      <td>
                        <hl:htmlinput name="<%=(String)("dataPrevistaRetorno_" + (int)contador)%>"
                          di="<%=(String)("dataPrevistaRetorno_" + (int)contador)%>"
                          type="text"
                          classe="form-control pl-1 pr-1"
                          value="<%=TextHelper.forHtmlAttribute(dataPrevistaRetorno)%>"
                          size="10"
                          maxlength="10"
                          mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                          others="<%=(String)(!calModel.isPodeEditarCalendario() || anteriorDataAtual || proxAno ? "disabled" : "")%>"
                        />
                      </td>
                    <% } %>
                    <% if (calModel.isPermiteApenasReducoes()) { %>
                      <td>
                        <hl:htmlinput name="<%=(String)("apenasReducoes_" + (int)contador)%>"
                          di="<%=(String)("apenasReducoes_" + (int)contador)%>"
                          type="checkbox"
                          classe="form-control pl-1 pr-1"
                          value="S"
                          checked="<%=(String)(apenasReducoes ? "true" : "false")%>"
                          others="<%=(String)(!calModel.isPodeEditarCalendario() || anteriorDataAtual || proxAno ? "disabled" : "")%>"
                        />
                      </td>
                    <% } %>
                    </tr>
                  <%
                    }
                  %>
                </tbody>
                <tfoot>
                  <tr>
                    <td colspan="5"><hl:message key="rotulo.calendario.folha.listagem" arg0="<%=TextHelper.forHtmlContent(calModel.getAno())%>"/></td>
                  </tr>
                </tfoot>
              </table>
          </div>
          <nav class="mt-2 pr-3" aria-label="<hl:message key="rotulo.calendario.folha.navegar.listagem"/>">
            <div class="row">
              <div class="col-sm-4">
                <div class="justify-content-left ml-3 mb-3">
                  <a class="btn btn-primary" href="#DiaCorte" data-bs-toggle="modal"><hl:message key="rotulo.calendario.folha.aplicar.dia.corte.periodos.futuros"/></a>
                </div>
              </div>
              <div class="col-sm-8">
                <ul class="pagination justify-content-end">
                  <li class="page-item justify-content-end"><a class="page-link" href="javascript:navegarAnterior();" aria-label="<hl:message key="rotulo.botao.anterior"/>">«</a></li>
                  <li class="page-item justify-content-end disabled"><a class="page-link" href="#"><%=TextHelper.forHtmlContent(calModel.getAno())%></a></li>
                  <li class="page-item justify-content-end"><a class="page-link" href="javascript:navegarProximo();" aria-label="<hl:message key="rotulo.botao.proximo"/>">»</a></li>
                </ul>
              </div>
            </div>
          </nav>
        </div>
      
        <div class="btn-action">
          <a class="btn btn-outline-danger" aria-label="<hl:message key="rotulo.botao.voltar"/>" href="javascript:void(0);" onclick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
          <% if (calModel.isPodeEditarCalendario()) { %>
            <button class="btn btn-primary" onClick="return salvar()"><hl:message key="rotulo.botao.salvar"/></button>
          <% } %>
        </div>
      
        <% if (calModel.isPodeEditarCalendario()) { %>
        <!-- MODAL -->
        <div class="modal fade" id="DiaCorte" tabindex="-1" role="dialog" aria-labelledby="modalTitulo" aria-hidden="true" style="display: none;">
          <div class="modal-dialog modal-dia-corte" role="document">
            <div class="modal-content">
              <div class="modal-header pb-0">
                <h5 class="modal-title about-title mb-0" id="modalTitulo"><hl:message key="rotulo.calendario.folha.aplicar.dia.corte"/></h5>
                <button type="button" class="logout mr-2" data-bs-dismiss="modal" aria-label="<hl:message key="rotulo.botao.fechar"/>">
                  <span aria-hidden="true">×</span>
                </button>
              </div>
              <div class="modal-body pb-0 pt-1">
                  <% if (calModel.isMensal()) { %>
                  <div class="form-group">
                    <label for="diaCorte_geral"><hl:message key="rotulo.calendario.folha.aplicar.dia.corte.periodos.futuros"/></label>
                    <hl:htmlinput name="diaCorte_geral"
                                       di="diaCorte_geral"
                                       type="text"
                                       classe="form-control"
                                       size="3"
                                       maxlength="2"
                                       mask="#D2"/>
                  </div>
                  <%} else { %>
                  <div class="form-group">
                    <label for="deReplicar"><hl:message key="rotulo.calendario.folha.replicar.periodos.cadastrados.de"/></label>
                    <hl:htmlinput name="deReplicar"
                                       di="deReplicar"
                                       type="text"
                                       classe="form-control"
                                       size="4"
                                       maxlength="4"
                                       mask="#D4"/>
                  </div>
                  <div class="form-group">
                    <label for="ateReplicar"><hl:message key="rotulo.calendario.folha.replicar.periodos.cadastrados.ate"/></label>
                   <hl:htmlinput name="ateReplicar"
                                       di="ateReplicar"
                                       type="text"
                                       classe="form-control"
                                       size="4"
                                       maxlength="4"
                                       mask="#D4"/>
                  </div>
                  <%} %>
              </div>
              <div class="modal-footer pt-0">
                <div class="btn-action mt-2 mb-0">
                  <a class="btn btn-outline-danger" data-bs-dismiss="modal" href="#"><hl:message key="rotulo.botao.cancelar"/></a>
                  <% if (calModel.isMensal()) { %>
                  <a class="btn btn-primary" data-bs-dismiss="modal" href="#noback" onClick="javascript:return aplicarDiaCorte();">
                    <hl:message key="rotulo.botao.aplicar"/>
                  </a>
                  <%} else { %>
                  <a class="btn btn-primary" data-bs-dismiss="modal" href="#noback" onClick="javascript:return replicarPeriodoQuinzenal();">
                    <hl:message key="rotulo.botao.aplicar"/>
                  </a>
                  <%} %>
                </div>
              </div>
            </div>
          </div>
        </div>
        <% } %>
      </form>
</c:set>
<c:set var="javascript">
<script language="JavaScript" type="text/JavaScript">

  var f0 = document.forms[0];
  var corteMaximo = new Array(0, <%=(String) TextHelper.join(calModel.getDiasMaxMes(), ",") %>);

  /** Verifica se os campos foram preenchidos com valores válidos */
  function salvar(callback) {
    if (document.forms[0] != null) {
      for (var i = 0; (i < document.forms[0].elements.length); i++) {
        var e = document.forms[0].elements[i];
        if (!e.disabled && e.value != "") {
          if (e.type == "text" && (
                  e.name.indexOf("dataIni_") == 0 || 
                  e.name.indexOf("dataFim_") == 0 ||
                  e.name.indexOf("dataFimAjustes_") == 0 )) {
            if (!verificaData(e.value)) {
              e.focus();
              return false;
            }
          } else if (e.type == "text" && e.name.indexOf("diaCorte_") == 0) {
            if (isNaN(e.value) || (e.value < 1) || (e.value > 31)) {                    
              alert('<hl:message key="mensagem.calendario.folha.dia.corte"/>');
              e.focus();
            return false;
            }
          } else if (e.type == "text" && e.name.indexOf("dataPrevistaRetorno_") == 0) {
            if (e.value == "") {
              alert('<hl:message key="mensagem.erro.calendario.folha.preencha.data.prevista.retorno"/>');
              e.focus();
              return false;
            } else if (!verificaData(e.value)) {
              e.focus();
              return false;
            }
          }
        }
      }
      <%if (calModel.isHabilitaDataPrevistaRetorno()) { %>
           if(!campoDataPrevistaPreenchido (<%=calModel.getQtdPeriodos()%>)) {
               return false;
           }
      <%}%> 
		      
      <%if (calModel.isHabilitaDataFiscal()) { %>
          if(!validaCamposDataFiscal (<%=calModel.getQtdPeriodos()%>)) {
              return false;
          }
      <%}%>
    }
    enableAll();
    
    if (callback) {
		callback();
    }
    
    f0.submit();
    
    return false;
    
  }
    
  
  function replicarPeriodoQuinzenal() {
    if (document.forms[0].deReplicar.value == '' || document.forms[0].ateReplicar.value == ''){
      alert('<hl:message key="mensagem.erro.calendario.folha.ano.inicial.ano.final"/>');
      document.forms[0].replicarQuinzenal.value = false;
      document.forms[0].deReplicar.focus();
      return false;
    }
    
    if (document.forms[0].deReplicar.value <= <%=calModel.getAno()%> || document.forms[0].ateReplicar.value <= <%=calModel.getAno()%>){
      alert('<hl:message key="mensagem.erro.calendario.folha.ano.inicial.ano.final.maior.ano.atual"/>');
      document.forms[0].replicarQuinzenal.value = false;
      document.forms[0].deReplicar.focus();
      return false;
    }
    
    if (document.forms[0].deReplicar.value > document.forms[0].ateReplicar.value) {
      alert('<hl:message key="mensagem.erro.calendario.folha.ano.inicial.maior.ano.final"/>');
      document.forms[0].replicarQuinzenal.value = false;
      document.forms[0].deReplicar.focus();
      return false;
    }
    
    salvar(function () {
		document.forms[0].replicarQuinzenal.value = true;
    });
    
  }

  /** Calcula os demais campos baseado no corte e nos campos já preenchidos */
  function alterarValorCorte(e, ano) {
    // Obtém pelo nome do campo de dia do corte, os campos de datas iniciais e finais
    var campo = parseInt(e.name.substring("diaCorte_".length));
    var campoDataIni = document.getElementById("dataIni_" + campo);
    var campoDataFim = document.getElementById("dataFim_" + campo);
    var campoDataIniFiscal = document.getElementById("dataInicioFiscal_" + campo);
	var campoDataFimFiscal = document.getElementById("dataFimFiscal_" + campo);
    
    // Determina o mês pelo nome do campos
    <% if (calModel.isSemanal()) { %>
    var mes = Math.min(Math.floor(campo / 4), 12);
    <% } else if (calModel.isQuinzenal() || calModel.isQuatorzenal()) { %>
    var mes = Math.min(Math.floor(campo / 2), 12);
    <% } else { %>
    var mes = campo;
    <% } %>
    
    // Caso a data fim esteja preenchida, verifica o mês a que se refere o período pela data fim
    if (campoDataFim.value != "") {
      var arrayValorDataFim = obtemPartesData(campoDataFim.value);
      mes = parseInt(arrayValorDataFim[1]);
    } else if (campoDataIni.value != "") {
      var arrayValorDataIni = obtemPartesData(campoDataIni.value);
      mes = parseInt(arrayValorDataIni[1]);
    }

    // Se o corte não foi preenchido, ou preenchido com algo 
    // não numérico, não faz nada.
    if (e.value == null || e.value == '' || isNaN(e.value)) {
      return;
    }

    // Obtém o dia de corte preenchido no campo
    var corte = parseInt(e.value);

    if (campoDataIni.value == "") {
      if (campo > 1) {
        // Se não for janeiro pega a data fim anterior
        var ant = parseInt(campo) - 1;
        var campoDataFimAnt = document.getElementById("dataFim_" + ant);
        if (campoDataFimAnt.value != "") {
          // Preenche a data ini atual com d+1 da data fim anterior
          var arrayDataFimAnt = obtemPartesData(campoDataFimAnt.value);
          var diaAux = parseInt(arrayDataFimAnt[0]) + 1;
          var mesAux = parseInt(arrayDataFimAnt[1]);
          var anoAux = arrayDataFimAnt[2];
          if (diaAux > corteMaximo[mesAux]) {
            diaAux = 1;
            mesAux = mesAux + 1;
            if (mesAux > 12) {
              mesAux = 1;
              anoAux = parseInt(anoAux) + 1;
            }
          }
          campoDataIni.value = lpad(diaAux, 2, '0') + "/" + lpad(mesAux, 2, '0') + "/" + anoAux;
          mes = mesAux;
        }
      }
      // Se ainda permanece não preenchida, ou é porque é janeiro ou
      // o período anterior ainda não está preenchido. Usa o corte para
      // determinar o valor mais correto possível
      if (campoDataIni.value == "") {
        var diaAux = corte + 1;
        var mesAux = (campo == 1) ? 12 : mes;
        var anoAux = (campo == 1) ? (ano - 1) : ano;
        if (corte >= corteMaximo[mes]) {
          diaAux = 1;
          mesAux = (campo == 1) ? 1 : mes + 1;
          anoAux = ano;
        }

        campoDataIni.value = lpad(diaAux, 2, '0') + "/" + lpad(mesAux, 2, '0') + "/" + anoAux;
        mes = mesAux;
      }
    }

    // Determina o próximo mês pela periodicidade
    <% if (calModel.isMensal()) { %>
    var proxMes = mes + 1;
    <% } else { %>
    var proxMes = mes;
    if (campoDataIni.value != "") {
      var arrayDataIni = obtemPartesData(campoDataIni.value);
      var diaIni = arrayDataIni[0];
      if (corte <= diaIni) {
        proxMes++;
      }
    }
    <% } %>

    if (proxMes == 13) {
      proxMes = 1;
      ano++;
    }

    // Define a data fim com base na alteração do corte
    if (campoDataFim.value != "") {
      var arrayDataFim = obtemPartesData(campoDataFim.value);
      proxMes = arrayDataFim[1];
      ano = arrayDataFim[2];
    }
    // Se o dia de corte é maior que o máximo permitido para o mês
    // atribui o dia máximo, evitando erros de cálculos
    if (corte > corteMaximo[proxMes]) {
      corte = corteMaximo[proxMes];
      e.value = corteMaximo[proxMes];
    }
    campoDataFim.value = lpad(corte, 2, '0') + "/" + lpad(proxMes, 2, '0') + "/" + ano;

    // Define a data inicial do próximo período com base na data fim do período alterado
    if (mes <= 12) {
      var diaAux = (corte >= corteMaximo[mes]) ? 1 : corte + 1;
      var mesAux = (corte >= corteMaximo[mes]) ? parseInt(proxMes) + 1 : proxMes;
      var anoAux = ano;
      if (mesAux == 13) {
        mesAux = 1;
        anoAux = parseInt(anoAux) + 1;
      }
      
      var prox = parseInt(campo) + 1;
      var campoDataIniProx = document.getElementById("dataIni_" + prox);
      campoDataIniProx.value = lpad(diaAux, 2, '0') + "/" + lpad(mesAux, 2, '0') + "/" + anoAux;
    }
    
    <%if (calModel.isHabilitaDataFiscal()) { %>
    // Sugestão valores campo data ini fiscal
    if (campoDataIniFiscal.value == "") {
        if (campo > 0) {
          // Se não for janeiro (começa de zero) pega a data fim anterior
          var ant = parseInt(campo) - 1;
          var campoDataFimFiscalAnt = document.getElementById("dataFimFiscal_" + ant);
          if (campoDataFimFiscalAnt.value != "") {
            // Preenche a data ini atual com d+1 da data fim anterior
            var arrayDataFimFiscalAnt = obtemPartesData(campoDataFimFiscalAnt.value);
            var diaAux = parseInt(arrayDataFimFiscalAnt[0]) + 1;
            var mesAux = parseInt(arrayDataFimFiscalAnt[1]);
            var anoAux = arrayDataFimFiscalAnt[2];
            if (diaAux > corteMaximo[mesAux-1]) {
              diaAux = 1;
              mesAux = mesAux + 1;
              if (mesAux > 12) {
                mesAux = 1;
                anoAux = parseInt(anoAux) + 1;
              }
            }
            campoDataIniFiscal.value = lpad(diaAux, 2, '0') + "/" + lpad(mesAux, 2, '0') + "/" + anoAux;
          }
        }
  	}
    
    // Sugestão de valores campo data fim fiscal
    if (campoDataFimFiscal.value == "") {
        if (campo > 0) {
          if (campoDataIniFiscal.value != "") {
            // Preenche a data ini atual com d+30 da data fim anterior
            var arrayDataIniFiscal = obtemPartesData(campoDataIniFiscal.value);
            var diaAux = parseInt(arrayDataIniFiscal[0]) -1;
            var mesAux = parseInt(arrayDataIniFiscal[1]) +1;
            var anoAux = arrayDataIniFiscal[2];
            if (diaAux > corteMaximo[mesAux-1]) {
              diaAux = 1;
              mesAux = mesAux + 1;
            }
            if (mesAux > 12) {
                mesAux = 1;
                anoAux = parseInt(anoAux) + 1;
              }
            campoDataFimFiscal.value = lpad(diaAux, 2, '0') + "/" + lpad(mesAux, 2, '0') + "/" + anoAux;
          }
        }
  	}
    <% } %>
    
  }

  function alterarTipoEntidade() {
    var tipoEntidade = getCheckedRadio('form1', 'tipoEntidade');

    if (tipoEntidade == null || tipoEntidade == '') {
      alert('<hl:message key="mensagem.calendario.folha.selecione.entidade"/>');
      return;
    }

    if (tipoEntidade == 'CSE') {
      f0.estCodigo.disabled = true;
      f0.orgCodigo.disabled = true;
      recarregar();
    } else if (tipoEntidade == 'EST') {
      f0.estCodigo.disabled = false;
      f0.orgCodigo.disabled = true;
    } else if (tipoEntidade == 'ORG') {
      f0.estCodigo.disabled = true;
      f0.orgCodigo.disabled = false;
    }
  }

  function recarregar() {
    f0.acao.value = "iniciar";
    f0.submit();
  }

  function navegarAnterior() {
    f0.acao.value = "iniciar";
    f0.ano.value = <%=TextHelper.forJavaScriptBlock(calModel.getAno() - 1)%>;
    f0.submit();
  }

  function navegarProximo() {
    f0.acao.value = "iniciar";
    f0.ano.value = <%=TextHelper.forJavaScriptBlock(calModel.getAno() + 1)%>;
    f0.submit();
  }
  
  function aplicarDiaCorte() {
    var diaCorteGeral = document.getElementById("diaCorte_geral").value;
    
    if (diaCorteGeral == 'null' || diaCorteGeral == null || diaCorteGeral == 'undefined' || diaCorteGeral == ''){
      alert('<hl:message key="mensagem.calendario.folha.preencha.dia.corte"/>');
      document.getElementById("diaCorte_geral").focus();
      return false;       
    } else if (diaCorteGeral < 1 || diaCorteGeral > 31){
      alert('<hl:message key="mensagem.calendario.folha.dia.corte"/>');
      document.getElementById("diaCorte_geral").focus();
      return false;
    } else {
      document.getElementById("updateDiaCorteGeral").value = "1";
      f0.submit();
    }
  }

  <%if (calModel.isHabilitaDataPrevistaRetorno()) { %>
  function campoDataPrevistaPreenchido (contador) {
      for (var i=0; i < contador; i++) {
          var campoDataIni = document.getElementById("dataIni_" + i);
          var campoDataPrev = document.getElementById("dataPrevistaRetorno_" + i);
          if(!campoDataIni.disabled && (campoDataIni.value != null && campoDataIni.value != '') && (campoDataPrev.value == null || campoDataPrev.value == '')) {                
        	  alert('<hl:message key="mensagem.erro.calendario.folha.preencha.data.prevista.retorno"/>');
            campoDataPrev.focus();
              return false;
          }
        }
    
    return true;
  }
  <%}%>
  
  <%if (calModel.isHabilitaDataFiscal()) { %>
  function validaCamposDataFiscal(contador) {
     for(var i=0; i< contador; i++) {
       var campoDataIniCivil = document.getElementById("dataIni_" + i);
       var campoDataIniFiscal = document.getElementById("dataInicioFiscal_" + i);
       var campoDataFimFiscal = document.getElementById("dataFimFiscal_" + i);
       var campoDataCorte = document.getElementById("diaCorte_" + i);
       /*Validações se os campos estão preenchidos ou não*/
       if(campoDataCorte.value != null && campoDataCorte.value != ''){    	   
         if(!campoDataIniFiscal.disabled && (campoDataIniFiscal.value == null || campoDataIniFiscal.value == '')) {
            alert('<hl:message key="mensagem.erro.calendario.folha.data.inicio.fiscal.obrigatorio"/>');
            campoDataIniFiscal.focus();
            return false;
      	 } else if(!campoDataFimFiscal.disabled && (campoDataFimFiscal.value == null || campoDataFimFiscal.value == '')) {
            alert('<hl:message key="mensagem.erro.calendario.folha.data.fim.fiscal.obrigatorio"/>');
            campoDataFimFiscal.focus();
            return false;
      	 }
       }
     }
     return true;
  }
  <%}%>
        
</script>   
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>