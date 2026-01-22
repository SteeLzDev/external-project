package com.zetra.econsig.web.controller.folha;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.web.CalendarioFolhaModel;
import com.zetra.econsig.exception.CalendarioControllerException;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ImpRetornoControllerException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.solicitacaosuporte.jira.JiraUtil;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.calendario.CalendarioController;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.folha.ImpRetornoController;
import com.zetra.econsig.service.folha.PeriodoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: CalendarioFolhaWebController</p>
 * <p>Description: REST Controller para manutenção de calendário folha.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: fagner.luiz $
 * $Revision: 27758 $
 * $Date: 2019-09-06 15:26:28 -0300 (sex, 06 set 2019) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/editarCalendarioFolha" })
public class CalendarioFolhaWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CalendarioFolhaWebController.class);

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private CalendarioController calendarioController;

    @Autowired
    private ImpRetornoController impRetornoController;

    @Autowired
    private PeriodoController periodoController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ParametroControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        SynchronizerToken.saveToken(request);

        boolean podeEditarCalendario = responsavel.temPermissao(CodedValues.FUN_EDT_CALENDARIO_FOLHA);
        boolean habilitaPeriodoAjustes = ParamSist.paramEquals(CodedValues.TPC_HABILITA_EXTENSAO_PERIODO_FOLHA_AJUSTES, CodedValues.TPC_SIM, responsavel);
        boolean habilitaDataPrevistaRetorno = ParamSist.paramEquals(CodedValues.TPC_HABILITA_DATA_PREVISTA_RETORNO, CodedValues.TPC_SIM, responsavel);
        boolean permiteApenasReducoes = ParamSist.paramEquals(CodedValues.TPC_PERMITE_PERIODO_ACEITA_APENAS_REDUCOES, CodedValues.TPC_SIM, responsavel);
        String periodicidade = PeriodoHelper.getPeriodicidadeFolha(responsavel);
        int qtdPeriodos = PeriodoHelper.getQuantidadePeriodosFolha(responsavel) + 1; // o adicional é do ano seguinte, para atualizar a data início do período

        int ano = Calendar.getInstance().get(Calendar.YEAR);
        String anoFromRequest = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "ano")) ? JspHelper.verificaVarQryStr(request, "ano") : (String) request.getAttribute("ano");
        if (!TextHelper.isNull(anoFromRequest)) {
            try {
                ano = Integer.parseInt(anoFromRequest);
            } catch (NumberFormatException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
            }
        }

        String tipoEntidade = responsavel.isSup() ? AcessoSistema.ENTIDADE_CSE : responsavel.getTipoEntidade();
        String codigoEntidade = responsavel.getCodigoEntidade();

        String estCodigo = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "estCodigo")) ? JspHelper.verificaVarQryStr(request, "estCodigo") : (String) request.getAttribute("estCodigo");
        String orgCodigo = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "orgCodigo")) ? JspHelper.verificaVarQryStr(request, "orgCodigo") : (String) request.getAttribute("orgCodigo");

        TransferObject criterio = null;

        if (responsavel.isCseSup()) {
            // Usuário de Consignante e Suporte podem editar calendário das demais entidades
            if (!TextHelper.isNull(estCodigo)) {
                tipoEntidade = AcessoSistema.ENTIDADE_EST;
                codigoEntidade = estCodigo;
            } else if (!TextHelper.isNull(orgCodigo)) {
                tipoEntidade = AcessoSistema.ENTIDADE_ORG;
                codigoEntidade = orgCodigo;
            }
        } else if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
            criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.EST_CODIGO, responsavel.getCodigoEntidadePai());

            // Usuário de órgão com acesso ao estabelecimento pode editar o calendário
            // do estabelecimento ou de um dos órgãos do estabelecimento
            if (!TextHelper.isNull(orgCodigo)) {
                tipoEntidade = AcessoSistema.ENTIDADE_ORG;
                codigoEntidade = orgCodigo;
            } else {
                tipoEntidade = AcessoSistema.ENTIDADE_EST;
                codigoEntidade = responsavel.getCodigoEntidadePai();
            }
        }

        String nomeCampoDataIni = Columns.CFC_DATA_INI;
        String nomeCampoDataFim = Columns.CFC_DATA_FIM;
        String nomeCampoDataFimAjustes = Columns.CFC_DATA_FIM_AJUSTES;
        String nomeCampoDiaCorte = Columns.CFC_DIA_CORTE;
        String nomeCampoApenasReducoes = Columns.CFC_APENAS_REDUCOES;
        String nomeCampoDataPrevistaRetorno = Columns.CFC_DATA_PREVISTA_RETORNO;
        String nomeCampoDataIniFiscal = Columns.CFC_DATA_INI_FISCAL;
        String nomeCampoDataFimFiscal = Columns.CFC_DATA_FIM_FISCAL;
        String nomeCampoNumPeriodo = Columns.CFC_NUM_PERIODO;

        if (tipoEntidade.equals(AcessoSistema.ENTIDADE_EST)) {
            nomeCampoDataIni = Columns.CFE_DATA_INI;
            nomeCampoDataFim = Columns.CFE_DATA_FIM;
            nomeCampoDataFimAjustes = Columns.CFE_DATA_FIM_AJUSTES;
            nomeCampoDiaCorte = Columns.CFE_DIA_CORTE;
            nomeCampoApenasReducoes = Columns.CFE_APENAS_REDUCOES;
            nomeCampoDataPrevistaRetorno = Columns.CFE_DATA_PREVISTA_RETORNO;
            nomeCampoDataIniFiscal = Columns.CFE_DATA_INI_FISCAL;
            nomeCampoDataFimFiscal = Columns.CFE_DATA_FIM_FISCAL;
            nomeCampoNumPeriodo = Columns.CFE_NUM_PERIODO;
        } else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_ORG)) {
            nomeCampoDataIni = Columns.CFO_DATA_INI;
            nomeCampoDataFim = Columns.CFO_DATA_FIM;
            nomeCampoDataFimAjustes = Columns.CFO_DATA_FIM_AJUSTES;
            nomeCampoDiaCorte = Columns.CFO_DIA_CORTE;
            nomeCampoApenasReducoes = Columns.CFO_APENAS_REDUCOES;
            nomeCampoDataPrevistaRetorno = Columns.CFO_DATA_PREVISTA_RETORNO;
            nomeCampoDataIniFiscal = Columns.CFO_DATA_INI_FISCAL;
            nomeCampoDataFimFiscal = Columns.CFO_DATA_FIM_FISCAL;
            nomeCampoNumPeriodo = Columns.CFO_NUM_PERIODO;
        }

        List<Integer> diasMaxMes = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            Calendar anoCorrente = Calendar.getInstance();
            anoCorrente.set(Calendar.YEAR, ano);
            anoCorrente.set(Calendar.MONTH, i);
            anoCorrente.set(Calendar.DAY_OF_MONTH, 1);
            diasMaxMes.add(anoCorrente.getActualMaximum(Calendar.DAY_OF_MONTH));
        }

        CalendarioFolhaModel calModel = new CalendarioFolhaModel();
        try {
            Map<Integer, TransferObject> calendarioAno = calendarioController.lstCalendarioFolhaAno(ano, tipoEntidade, codigoEntidade, responsavel);
            calModel.setCalendarioAno(calendarioAno);

            Map<Integer, TransferObject> calendarioAnoProximoAno = calendarioController.lstCalendarioFolhaAno(ano + 1, tipoEntidade, codigoEntidade, responsavel);
            calModel.setCalendarioAnoProximoAno(calendarioAnoProximoAno);

            List<TransferObject> lstEstabelecimentos = consignanteController.lstEstabelecimentos(criterio, responsavel);
            calModel.setLstEstabelecimentos(lstEstabelecimentos);

            List<TransferObject> lstOrgaos = consignanteController.lstOrgaos(calModel.getCriterio(), responsavel);
            calModel.setLstOrgaos(lstOrgaos);

        } catch (CalendarioControllerException | ConsignanteControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        calModel.setDiasMaxMes(diasMaxMes);
        calModel.setHabilitaDataPrevistaRetorno(habilitaDataPrevistaRetorno);
        calModel.setQtdPeriodos(qtdPeriodos);
        calModel.setAno(ano);
        calModel.setPeriodicidade(periodicidade);
        calModel.setCriterio(criterio);
        calModel.setEstCodigo(estCodigo);
        calModel.setOrgCodigo(orgCodigo);
        calModel.setHabilitaPeriodoAjustes(habilitaPeriodoAjustes);
        calModel.setPermiteApenasReducoes(permiteApenasReducoes);
        calModel.setNomeCampoDataIni(nomeCampoDataIni);
        calModel.setNomeCampoDataFim(nomeCampoDataFim);
        calModel.setNomeCampoDataFimAjustes(nomeCampoDataFimAjustes);
        calModel.setNomeCampoDiaCorte(nomeCampoDiaCorte);
        calModel.setNomeCampoDataPrevistaRetorno(nomeCampoDataPrevistaRetorno);
        calModel.setNomeCampoApenasReducoes(nomeCampoApenasReducoes);
        calModel.setNomeCampoDataIniFiscal(nomeCampoDataIniFiscal);
        calModel.setNomeCampoDataFimFiscal(nomeCampoDataFimFiscal);
        calModel.setNomeCampoNumPeriodo(nomeCampoNumPeriodo);
        calModel.setPodeEditarCalendario(podeEditarCalendario);
        calModel.setTipoEntidade(tipoEntidade);
        calModel.setHabilitaDataFiscal(calendarioController.exibirCalendarioFiscal(tipoEntidade, estCodigo, orgCodigo, responsavel));

        model.addAttribute("calModel", calModel);
        return viewRedirect("jsp/calendario/editarCalendarioFolha", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=editar" })
    public String editar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ParametroControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        boolean integraJira = ParamSist.paramEquals(CodedValues.TPC_INTEGRA_JIRA, CodedValues.TPC_SIM, responsavel);
        boolean habilitaPeriodoAjustes = ParamSist.paramEquals(CodedValues.TPC_HABILITA_EXTENSAO_PERIODO_FOLHA_AJUSTES, CodedValues.TPC_SIM, responsavel);
        boolean habilitaDataPrevistaRetorno = ParamSist.paramEquals(CodedValues.TPC_HABILITA_DATA_PREVISTA_RETORNO, CodedValues.TPC_SIM, responsavel);
        boolean permiteApenasReducoes = ParamSist.paramEquals(CodedValues.TPC_PERMITE_PERIODO_ACEITA_APENAS_REDUCOES, CodedValues.TPC_SIM, responsavel);
        boolean agrupaPeriodos = ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, CodedValues.TPC_SIM, responsavel);
        boolean folhaMensal = PeriodoHelper.folhaMensal(responsavel);
        boolean podeEditarCalendario = responsavel.temPermissao(CodedValues.FUN_EDT_CALENDARIO_FOLHA);
        int qtdPeriodos = PeriodoHelper.getQuantidadePeriodosFolha(responsavel) + 1; // o adicional é do ano seguinte, para atualizar a data início do período

        String updateDiaCorteGeral = JspHelper.verificaVarQryStr(request, "updateDiaCorteGeral");

        int ano = Calendar.getInstance().get(Calendar.YEAR);
        if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "ano"))) {
            try {
                ano = Integer.parseInt(JspHelper.verificaVarQryStr(request, "ano"));
            } catch (NumberFormatException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }

        String deReplicarStr = JspHelper.verificaVarQryStr(request, "deReplicar");
        String ateReplicarStr = JspHelper.verificaVarQryStr(request, "ateReplicar");
        Integer deReplicar = Integer.parseInt(TextHelper.isNull(deReplicarStr) ? "0" : deReplicarStr);
        Integer ateReplicar = Integer.parseInt(TextHelper.isNull(deReplicarStr) ? "0" : ateReplicarStr);
        boolean replicarCalendario = !folhaMensal && JspHelper.verificaVarQryStr(request, "replicarQuinzenal").equals("true");

        // Validação das regras para a replicação quinzenal
        if (replicarCalendario) {
            if (TextHelper.isNull(deReplicar) || TextHelper.isNull(ateReplicar)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.calendario.folha.ano.inicial.ano.final", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if (deReplicar <= ano || ateReplicar <= ano) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.calendario.folha.ano.inicial.ano.final.maior.ano.atual", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if (deReplicar > ateReplicar) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.calendario.folha.ano.inicial.maior.ano.final", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            for (int contador = 1; contador <= qtdPeriodos - 1; contador++) {
                String dataIni = JspHelper.verificaVarQryStr(request, "dataIni_" + contador);
                String dataFim = JspHelper.verificaVarQryStr(request, "dataFim_" + contador);
                String diaCorte = JspHelper.verificaVarQryStr(request, "diaCorte_" + contador);

                if (TextHelper.isNull(dataIni) || TextHelper.isNull(dataFim) || TextHelper.isNull(diaCorte)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.calendario.folha.preencha.todos.periodos", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }
        }

        String tipoEntidade = responsavel.isSup() ? AcessoSistema.ENTIDADE_CSE : responsavel.getTipoEntidade();
        String codigoEntidade = responsavel.getCodigoEntidade();
        String estCodigo = JspHelper.verificaVarQryStr(request, "estCodigo");
        String orgCodigo = JspHelper.verificaVarQryStr(request, "orgCodigo");

        if (responsavel.isCseSup()) {
            // Usuário de Consignante e Suporte podem editar calendário das demais entidades
            if (!estCodigo.equals("")) {
                tipoEntidade = AcessoSistema.ENTIDADE_EST;
                codigoEntidade = estCodigo;
            } else if (!orgCodigo.equals("")) {
                tipoEntidade = AcessoSistema.ENTIDADE_ORG;
                codigoEntidade = orgCodigo;
            }
        } else if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
            // Usuário de órgão com acesso ao estabelecimento pode editar o calendário
            // do estabelecimento ou de um dos órgãos do estabelecimento
            if (!orgCodigo.equals("")) {
                tipoEntidade = AcessoSistema.ENTIDADE_ORG;
                codigoEntidade = orgCodigo;
            } else {
                tipoEntidade = AcessoSistema.ENTIDADE_EST;
                codigoEntidade = responsavel.getCodigoEntidadePai();
            }
        }

        model.addAttribute("ano", ano);
        model.addAttribute("estCodigo", estCodigo);
        model.addAttribute("orgCodigo", orgCodigo);

        try {
            Map<Integer, TransferObject> calendarioAno = calendarioController.lstCalendarioFolhaAno(ano, tipoEntidade, codigoEntidade, responsavel);
            LinkedList<Map<String, String>> alteraJira = new LinkedList<>();

            String nomeCampoEntidade = Columns.CFC_CSE_CODIGO;
            String nomeCampoPeriodo = Columns.CFC_PERIODO;
            String nomeCampoNumPeriodo = Columns.CFC_NUM_PERIODO;
            String nomeCampoDataIni = Columns.CFC_DATA_INI;
            String nomeCampoDataFim = Columns.CFC_DATA_FIM;
            String nomeCampoDataFimAjustes = Columns.CFC_DATA_FIM_AJUSTES;
            String nomeCampoDiaCorte = Columns.CFC_DIA_CORTE;
            String nomeCampoApenasReducoes = Columns.CFC_APENAS_REDUCOES;
            String nomeCampoDataPrevistaRetorno = Columns.CFC_DATA_PREVISTA_RETORNO;
            String nomeCampoDataIniFiscal = Columns.CFC_DATA_INI_FISCAL;
            String nomeCampoDataFimFiscal = Columns.CFC_DATA_FIM_FISCAL;

            if (tipoEntidade.equals(AcessoSistema.ENTIDADE_EST)) {
                nomeCampoEntidade = Columns.CFE_EST_CODIGO;
                nomeCampoPeriodo = Columns.CFE_PERIODO;
                nomeCampoNumPeriodo = Columns.CFE_NUM_PERIODO;
                nomeCampoDataIni = Columns.CFE_DATA_INI;
                nomeCampoDataFim = Columns.CFE_DATA_FIM;
                nomeCampoDataFimAjustes = Columns.CFE_DATA_FIM_AJUSTES;
                nomeCampoDiaCorte = Columns.CFE_DIA_CORTE;
                nomeCampoApenasReducoes = Columns.CFE_APENAS_REDUCOES;
                nomeCampoDataPrevistaRetorno = Columns.CFE_DATA_PREVISTA_RETORNO;
                nomeCampoDataIniFiscal = Columns.CFE_DATA_INI_FISCAL;
                nomeCampoDataFimFiscal = Columns.CFE_DATA_FIM_FISCAL;
            } else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_ORG)) {
                nomeCampoEntidade = Columns.CFO_ORG_CODIGO;
                nomeCampoPeriodo = Columns.CFO_PERIODO;
                nomeCampoNumPeriodo = Columns.CFO_NUM_PERIODO;
                nomeCampoDataIni = Columns.CFO_DATA_INI;
                nomeCampoDataFim = Columns.CFO_DATA_FIM;
                nomeCampoDataFimAjustes = Columns.CFO_DATA_FIM_AJUSTES;
                nomeCampoDiaCorte = Columns.CFO_DIA_CORTE;
                nomeCampoApenasReducoes = Columns.CFO_APENAS_REDUCOES;
                nomeCampoDataPrevistaRetorno = Columns.CFO_DATA_PREVISTA_RETORNO;
                nomeCampoDataIniFiscal = Columns.CFO_DATA_INI_FISCAL;
                nomeCampoDataFimFiscal = Columns.CFO_DATA_FIM_FISCAL;
            }

            if (!TextHelper.isNull(updateDiaCorteGeral) && updateDiaCorteGeral.equals("0")) {
                String papelSelecionado = JspHelper.verificaVarQryStr(request, "tipoEntidade");
                if (papelSelecionado == null) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.tipo.entidade", responsavel));
                    return iniciar(request, response, session, model);
                } else if (papelSelecionado.equals(AcessoSistema.ENTIDADE_EST) && TextHelper.isNull(estCodigo)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.estabelecimento", responsavel));
                    return iniciar(request, response, session, model);
                } else if (papelSelecionado.equals(AcessoSistema.ENTIDADE_ORG) && TextHelper.isNull(orgCodigo)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.orgao", responsavel));
                    return iniciar(request, response, session, model);
                }

                Date proximoPeriodo = null;
                if (integraJira) {
                    try {
                        Date ultPeriodoRetorno = DateHelper.parse(impRetornoController.recuperaPeriodoRetorno(CodedValues.TIPO_RETORNO_NORMAL, null, null, null, responsavel), "yyyy-MM-dd");
                        if (folhaMensal) {
                            proximoPeriodo = DateHelper.addMonths(ultPeriodoRetorno, 1);
                        } else {
                            proximoPeriodo = periodoController.obtemPeriodoAposPrazo(orgCodigo, 1, ultPeriodoRetorno, true, responsavel);
                        }
                    } catch (ParseException ex) {
                        LOG.error(ex.getMessage(), ex);
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    } catch (ImpRetornoControllerException | PeriodoException ex) {
                        LOG.error(ex.getMessage(), ex);
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.calendario.folha.integracao.invalida", responsavel));
                        return iniciar(request, response, session, model);
                    }
                }

                // Se exibe datas de calendário fiscal, os campos devem ser preenchidos
                boolean exibirCalendarioFiscal = calendarioController.exibirCalendarioFiscal(tipoEntidade, estCodigo, orgCodigo, responsavel);

                List<TransferObject> lstCalendarioFolhaAlteracao = new ArrayList<>();
                List<TransferObject> lstCalendarioFolhaExclusao = new ArrayList<>();

                for (int contador = 1; contador <= qtdPeriodos; contador++) {
                    TransferObject calendario = null;
                    boolean proxAno = (contador == qtdPeriodos);
                    if (proxAno) {
                        calendarioAno = calendarioController.lstCalendarioFolhaAno(ano + 1, tipoEntidade, codigoEntidade, responsavel);
                        calendario = calendarioAno.get(1);
                    } else {
                        calendario = calendarioAno.get(contador);
                    }

                    String dataIni = JspHelper.verificaVarQryStr(request, "dataIni_" + contador);
                    String dataFim = JspHelper.verificaVarQryStr(request, "dataFim_" + contador);
                    String dataFimAjustes = JspHelper.verificaVarQryStr(request, "dataFimAjustes_" + contador);
                    String diaCorte = JspHelper.verificaVarQryStr(request, "diaCorte_" + contador);
                    String apenasReducoes = JspHelper.verificaVarQryStr(request, "apenasReducoes_" + contador);
                    String dataPrevistaRetorno = JspHelper.verificaVarQryStr(request, "dataPrevistaRetorno_" + contador);
                    String dataInicioFiscal = JspHelper.verificaVarQryStr(request, "dataInicioFiscal_" + contador);
                    String dataFimFiscal = JspHelper.verificaVarQryStr(request, "dataFimFiscal_" + contador);

                    if (!dataIni.equals("") &&
                            !dataFim.equals("") &&
                            !diaCorte.equals("") &&
                            (!habilitaPeriodoAjustes || !dataFimAjustes.equals(""))
                            && (!habilitaDataPrevistaRetorno || !dataPrevistaRetorno.equals(""))) {

                        String diaCorteNew = diaCorte;
                        String diaCorteOld = calendario == null ? null : calendario.getAttribute(nomeCampoDiaCorte).toString();
                        String dataIniOld = calendario == null ? null : calendario.getAttribute(nomeCampoDataIni).toString();
                        String dataFimOld = calendario == null ? null : calendario.getAttribute(nomeCampoDataFim).toString();
                        String dataPrevistaRetornoOld = calendario == null ? null : calendario.getAttribute(nomeCampoDataPrevistaRetorno) == null ? "0000-00-00" : calendario.getAttribute(nomeCampoDataPrevistaRetorno).toString();
                        String dataFimAjustesOld = calendario == null ? null : calendario.getAttribute(nomeCampoDataFimAjustes) == null ? "0000-00-00" : calendario.getAttribute(nomeCampoDataFimAjustes).toString();
                        String apenasReducoesOld = (String) (calendario == null ? null : calendario.getAttribute(nomeCampoApenasReducoes));

                        if (calendario == null) {
                            calendario = new CustomTransferObject();
                            calendario.setAttribute(nomeCampoPeriodo, PeriodoHelper.converterNumPeriodoNoAnoParaPeriodo(contador, ano, responsavel));
                            calendario.setAttribute(nomeCampoNumPeriodo, contador);
                            calendario.setAttribute(nomeCampoEntidade, codigoEntidade);
                        }

                        calendario.setAttribute(nomeCampoDataIni, DateHelper.parse(dataIni + " 00:00:00", LocaleHelper.getDateTimePattern()));
                        calendario.setAttribute(nomeCampoDataFim, DateHelper.parse(dataFim + " 23:59:59", LocaleHelper.getDateTimePattern()));
                        calendario.setAttribute(nomeCampoDiaCorte, Short.valueOf(diaCorte));


                        boolean calendarioFiscalAlterado = false;

                        if (exibirCalendarioFiscal) {
                            boolean anteriorDataAtual = DateHelper.getSystemDatetime().after((Date) calendario.getAttribute(nomeCampoDataFim));

                            if (!proxAno && !anteriorDataAtual) {
                                // Verifica se data início e fim fiscal foram preenchidas
                                if (TextHelper.isNull(dataInicioFiscal)) {
                                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.calendario.folha.data.inicio.fiscal.obrigatorio", responsavel));
                                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                                } else if (TextHelper.isNull(dataFimFiscal)) {
                                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.calendario.folha.data.fim.fiscal.obrigatorio", responsavel));
                                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                                }
                            }

                            if (!TextHelper.isNull(dataInicioFiscal) && !TextHelper.isNull(dataFimFiscal)) {
                                // Verifica se data inicio fiscal é maior que a data início
                                int resultado = DateHelper.parse(dataIni + " 00:00:00", LocaleHelper.getDateTimePattern()).compareTo(DateHelper.parse(dataInicioFiscal + " 00:00:00", LocaleHelper.getDateTimePattern()));
                                if (resultado >= 0) {
                                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.calendario.folha.data.ini.fiscal.maior", responsavel));
                                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                                }

                                // Verifica se data fim fiscal é maior que a data início fiscal
                                resultado = DateHelper.parse(dataInicioFiscal + " 00:00:00", LocaleHelper.getDateTimePattern()).compareTo(DateHelper.parse(dataFimFiscal + " 00:00:00", LocaleHelper.getDateTimePattern()));
                                if (resultado >= 0) {
                                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.calendario.folha.data.fim.fiscal.maior", responsavel));
                                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                                }

                                String dataIniFiscalOld = calendario.getAttribute(nomeCampoDataIniFiscal) != null ? calendario.getAttribute(nomeCampoDataIniFiscal).toString() : null;
                                String dataFimFiscalOld = calendario.getAttribute(nomeCampoDataFimFiscal) != null ? calendario.getAttribute(nomeCampoDataFimFiscal).toString() : null;

                                if (TextHelper.isNull(dataIniFiscalOld) || TextHelper.isNull(dataFimFiscalOld) ||
                                        (DateHelper.parse(dataInicioFiscal, LocaleHelper.getDatePattern()).compareTo(DateHelper.parse(dataIniFiscalOld, "yyyy-MM-dd")) != 0 ||
                                        (DateHelper.parse(dataFimFiscal, LocaleHelper.getDatePattern()).compareTo(DateHelper.parse(dataFimFiscalOld, "yyyy-MM-dd")) != 0))) {

                                    calendario.setAttribute(nomeCampoDataIniFiscal, DateHelper.parse(dataInicioFiscal + " 00:00:00", LocaleHelper.getDateTimePattern()));
                                    calendario.setAttribute(nomeCampoDataFimFiscal, DateHelper.parse(dataFimFiscal + " 23:59:59", LocaleHelper.getDateTimePattern()));

                                    calendarioFiscalAlterado = true;
                                }
                            }
                        }

                        if (habilitaDataPrevistaRetorno) {
                            calendario.setAttribute(nomeCampoDataPrevistaRetorno, DateHelper.parse(dataPrevistaRetorno, LocaleHelper.getDatePattern()));
                        }
                        if (habilitaPeriodoAjustes) {
                            calendario.setAttribute(nomeCampoDataFimAjustes, DateHelper.parse(dataFimAjustes + " 23:59:59", LocaleHelper.getDateTimePattern()));
                        }
                        if (permiteApenasReducoes) {
                            apenasReducoesOld = (TextHelper.isNull(apenasReducoesOld) ? "N" : apenasReducoesOld);
                            apenasReducoes = (TextHelper.isNull(apenasReducoes) ? "N" : apenasReducoes);
                            calendario.setAttribute(nomeCampoApenasReducoes, apenasReducoes);
                        }
                        if (agrupaPeriodos) {
                            // Se permite agrupamento de períodos na mesma data de corte, verifica se a data fim
                            // do período atual é igual à data de fim de algum período anterior a este
                            boolean periodoMesmaDataFim = false;
                            for (int j = 0; j < contador; j++) {
                                String dataFimPeriodoAnterior = JspHelper.verificaVarQryStr(request, "dataFim_" + j);
                                if (dataFim.equals(dataFimPeriodoAnterior)) {
                                    periodoMesmaDataFim = true;
                                    break;
                                }
                            }
                            if (periodoMesmaDataFim) {
                                // Salva data ini com o mesmo valor da data fim para que o período não tenha conteúdo
                                calendario.setAttribute(nomeCampoDataIni, DateHelper.parse(dataFim + " 23:59:59", LocaleHelper.getDateTimePattern()));
                            }
                        }

                        if (dataIniOld == null || dataFimOld == null || diaCorteOld == null ||
                                DateHelper.parse(dataIni, LocaleHelper.getDatePattern()).compareTo(DateHelper.parse(dataIniOld, "yyyy-MM-dd")) != 0 ||
                                DateHelper.parse(dataFim, LocaleHelper.getDatePattern()).compareTo(DateHelper.parse(dataFimOld, "yyyy-MM-dd")) != 0 ||
                                (habilitaPeriodoAjustes && DateHelper.parse(dataFimAjustes, LocaleHelper.getDatePattern()).compareTo(DateHelper.parse(dataFimAjustesOld, "yyyy-MM-dd")) != 0) ||
                                (habilitaDataPrevistaRetorno && DateHelper.parse(dataPrevistaRetorno, LocaleHelper.getDatePattern()).compareTo(DateHelper.parse(dataPrevistaRetornoOld, "yyyy-MM-dd")) != 0) ||
                                !diaCorteNew.equals(diaCorteOld) ||
                                (permiteApenasReducoes && !apenasReducoesOld.equals(apenasReducoes)) ||
                                calendarioFiscalAlterado) {
                            // Adiciona o calendário para inclusão/alteração
                            lstCalendarioFolhaAlteracao.add(calendario);
                        }

                        if (integraJira) {
                            Date periodoEditado = (Date) calendario.getAttribute(nomeCampoPeriodo);
                            if ((diaCorteOld == null && diaCorteNew != null) || (!diaCorteOld.equals(diaCorteNew) && proximoPeriodo.compareTo(periodoEditado) >= 0)) {
                                Map<String, String> alteracao = new HashMap<>();
                                alteracao.put("corteOld", diaCorteOld);
                                alteracao.put("corteNew", diaCorteNew);
                                alteracao.put("tipoEntidade", tipoEntidade);
                                alteracao.put("codEntidade", codigoEntidade);
                                alteracao.put("periodo", DateHelper.format(periodoEditado, "yyyy-MM-dd"));

                                alteraJira.push(alteracao);
                            }
                        }

                    } else if (calendario != null) {
                        // Se um dos campos não foi preenchido, e existe registro de calendário
                        // para o período, então adiciona o período à lista de remoção, desde
                        // que não seja anterior à data atual, pois estes não podem ser removidos.
                        if (calendario.getAttribute(nomeCampoDataFim) == null || !DateHelper.getSystemDatetime().after((Date) calendario.getAttribute(nomeCampoDataFim))) {
                            // Verifica se todos os campos foram limpos
                            if (TextHelper.isNull(dataIni) &&
                                    TextHelper.isNull(dataFim) &&
                                    TextHelper.isNull(diaCorte) &&
                                    ((habilitaPeriodoAjustes && TextHelper.isNull(dataFimAjustes) || !habilitaPeriodoAjustes) &&
                                    ((habilitaDataPrevistaRetorno && TextHelper.isNull(dataPrevistaRetorno) || !habilitaDataPrevistaRetorno)))) {
                                lstCalendarioFolhaExclusao.add(calendario);
                            }
                        }
                    }
                }

                if (replicarCalendario) {
                    calendarioController.atualizaCalendarioFolhaReplica(lstCalendarioFolhaAlteracao, lstCalendarioFolhaExclusao, ano, deReplicar, ateReplicar, tipoEntidade, codigoEntidade, responsavel);
                } else {
                    // Salva os registros do ano
                    calendarioController.updateCalendarioFolha(lstCalendarioFolhaAlteracao, lstCalendarioFolhaExclusao, tipoEntidade, codigoEntidade, false, responsavel);
                    // Chama as alterações do Jira
                    JiraUtil jiraUtil = new JiraUtil();
                    if (integraJira && !alteraJira.isEmpty()) {
                        for (Map<String, String> mapa : alteraJira) {
                            jiraUtil.atualizaDataCorteProducao(mapa.get("corteOld"), mapa.get("corteNew"), responsavel, mapa.get("periodo"), mapa.get("tipoEntidade"), mapa.get("codEntidade"));
                        }
                    }
                }

                // Limpa cache dos períodos
                PeriodoHelper.getInstance().reset();
                // Seta mensagem de sucesso
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.calendario.folha.alteracoes.salvas.sucesso", responsavel));

            } else if (podeEditarCalendario && updateDiaCorteGeral.equals("1")) {
                String novaDataCorte = JspHelper.verificaVarQryStr(request, "diaCorte_geral");

                if (TextHelper.isNull(novaDataCorte)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.calendario.folha.preencha.dia.corte", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                int novaDataCorteInt = Integer.valueOf(novaDataCorte);
                if (novaDataCorteInt < 1 || novaDataCorteInt > 31) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.calendario.folha.dia.corte", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                try {
                    calendarioController.updateTodosCalendarioFolha(novaDataCorteInt, tipoEntidade, codigoEntidade, false, responsavel);
                } catch (CalendarioControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
                // Limpa cache dos períodos
                PeriodoHelper.getInstance().reset();
                // Seta mensagem de sucesso
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.calendario.folha.alteracoes.salvas.sucesso", responsavel));
            }

            return iniciar(request, response, session, model);

        } catch (CalendarioControllerException | ConsignanteControllerException | LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return iniciar(request, response, session, model);
        } catch (ParseException | IOException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }
}
