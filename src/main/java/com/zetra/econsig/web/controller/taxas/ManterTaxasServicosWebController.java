package com.zetra.econsig.web.controller.taxas;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcCseTO;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.PrazoTransferObject;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.consignacao.SimulacaoHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.coeficiente.CoeficienteAtivoController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ManterTaxasServicos </p>
 * <p>Description: Controlador Web para o caso de uso Manter taxas.</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author: fagner.luiz $ $Revision: 22388 $ $Date: 2017-09-06 10:39:44 -0300
 * (Qua, 06 Set 2017) $
 */
@Controller
@RequestMapping(value = "/v3/manterTaxas")
public class ManterTaxasServicosWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ManterTaxasServicosWebController.class);

    @Autowired
    private CoeficienteAtivoController coeficienteAtivoController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private ServicoController servicoController;

    @Autowired
    private SimulacaoController simulacaoController;

    @RequestMapping(params = { "acao=editarTaxa" })
    public String editarCoeficiente(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=consultarTaxa" })
    public String consultarCoeficiente(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return iniciar(request, response, session, model);
    }

    private String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            // Valida o token
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Verifica se o sistema está configurado para trabalhar com o CET.
            boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);
            boolean editaTaxaCet = ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_CADASTRO_TAXA_JUROS_EDITAR_CET_MANUTENCAO_CSA, responsavel);

            String tipo = JspHelper.verificaVarQryStr(request, "tipo");
            String subtipo = JspHelper.verificaVarQryStr(request, "subtipo");
            String csaCodigo = responsavel.isCsa() ? responsavel.getCodigoEntidade() : JspHelper.verificaVarQryStr(request, "CSA_CODIGO");

            String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
            String svcDescricao = JspHelper.verificaVarQryStr(request, "SVC_DESCRICAO");
            String titulo = JspHelper.verificaVarQryStr(request, "titulo");

            if (svcCodigo.equals("") || svcDescricao.equals("") || csaCodigo.equals("") || titulo.equals("")) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            List<PrazoTransferObject> prazos = null;
            // Seleciona prazos ativos.
            try {
                prazos = simulacaoController.findPrazoCsaByServico(svcCodigo, csaCodigo, responsavel);
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // verifica se há arquivo carregando prazos
            String fileName = JspHelper.verificaVarQryStr(request, "FILE1");
            Map<String, String> prazosFile = null;

            if (!TextHelper.isNull(fileName)) {
                String hashDir = session.getId();
                String diretorioRaizArquivos = ParamSist.getDiretorioRaizArquivos();
                String diretorioDestinoUploadHelper = UploadHelper.SUBDIR_ARQUIVOS_TEMPORARIOS + File.separatorChar + "anexo" + File.separatorChar + hashDir;
                String file = diretorioRaizArquivos + File.separator + diretorioDestinoUploadHelper + File.separatorChar + fileName;

                List<String> fileToList = FileHelper.readAllToList(file);

                if (fileToList != null && !fileToList.isEmpty()) {
                    int countLinha = 0;
                    prazosFile = new HashMap<>();
                    for (String linha : fileToList) {
                        String[] przCarregado = linha.split(";");
                        countLinha++;

                        try {
                            if (przCarregado[1].indexOf(',') > 0) {
                                przCarregado[1] = NumberHelper.reformat(przCarregado[1], NumberHelper.getLang(), "en", 2, 20);
                            }
                            Double vlrCarregado = Double.valueOf(przCarregado[1]);
                            vlrCarregado = (vlrCarregado.compareTo(0.0) <= 0) ? 0.0 : vlrCarregado;
                            prazosFile.put(przCarregado[0], vlrCarregado.toString());
                        } catch (Exception ex) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.taxa.juros.arquivo.mal.formatado", responsavel, Integer.valueOf(countLinha).toString()));
                            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                        }
                    }
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.taxa.juros.arquivo.carregado.sucesso", responsavel));
                }
            }

            boolean temLimiteTaxa = false;
            List<TransferObject> limiteTaxa = null;
            try {
                limiteTaxa = simulacaoController.getLimiteTaxas(svcCodigo, responsavel);
                temLimiteTaxa = (limiteTaxa != null && limiteTaxa.size() > 0);
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }

            // Formata o mês de referencia e a data de abertura da taxa de juros
            CustomTransferObject dataTaxaJuros = SimulacaoHelper.calcularDataTaxaJuros(svcCodigo, responsavel);
            String periodo = (String) dataTaxaJuros.getAttribute(Columns.CFT_DATA_INI_VIG);
            String dataAbertura = DateHelper.reformat(periodo, "yyyy-MM-dd", "dd");

            SimpleDateFormat sfm = new SimpleDateFormat("MMMM", LocaleHelper.getLocaleObject());
            String mesReferencia = sfm.format(DateHelper.parse(periodo, "yyyy-MM-dd"));

            // Busca parâmetro para data limite de digitação da taxa de juros
            String dataLimite = "";

            try {
                ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
                dataLimite = paramSvcCse.getTpsDataLimiteDigitTaxa() != null ? paramSvcCse.getTpsDataLimiteDigitTaxa() : "";
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if (!TextHelper.isNull(dataLimite)) {
                // Formata data limite de digitação da Taxa
                Calendar cal = Calendar.getInstance();
                int diaDoMes = cal.get(Calendar.DAY_OF_MONTH);
                if (diaDoMes > Integer.parseInt(dataLimite)) {
                    cal.add(Calendar.MONTH, 1);
                }
                dataLimite += " " + ApplicationResourcesHelper.getMessage("rotulo.taxa.juros.data.limite.de", responsavel) + " " + sfm.format(cal.getTime());
            }

            Map<String, TransferObject> coeficientes = null;
            // Seleciona coeficientes cadastrados.
            try {
                coeficientes = new HashMap<>();

                List<TransferObject> valores = simulacaoController.getTaxas(null, csaCodigo, svcCodigo, null, false, true, responsavel);
                if (valores == null || valores.size() == 0) {
                    // Se não encontrou taxas com data fim vigência nula, procura por aquela que
                    // tem fim vigência maior que data atual e estará ativa
                    valores = simulacaoController.getTaxas(null, csaCodigo, svcCodigo, null, true, true, responsavel);
                }
                for (TransferObject valor : valores) {
                    coeficientes.put(valor.getAttribute(Columns.PRZ_VLR).toString(), valor);
                }
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                coeficientes = new HashMap<>();
            }

            // Seleciona as taxas fixas
            String tac = "", tipoTac = "", valorMinTac = "", valorMaxTac = "";

            try {
                List<String> tpsCodigos = new ArrayList<>();
                tpsCodigos.add(CodedValues.TPS_TAC_FINANCIADA);
                tpsCodigos.add(CodedValues.TPS_TIPO_TAC);
                tpsCodigos.add(CodedValues.TPS_VALOR_MIN_TAC);
                tpsCodigos.add(CodedValues.TPS_VALOR_MAX_TAC);

                List<TransferObject> parametros = parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, tpsCodigos, false, responsavel);
                Iterator<TransferObject> it2 = parametros.iterator();
                TransferObject next = null;
                while (it2.hasNext()) {
                    next = it2.next();
                    if (next.getAttribute(Columns.TPS_CODIGO).equals(CodedValues.TPS_TAC_FINANCIADA)) {
                        tac = next.getAttribute(Columns.PSC_VLR).toString();
                        try {
                            tac = NumberHelper.reformat(tac, "en", NumberHelper.getLang());
                        } catch (java.text.ParseException ex2) {
                        }
                    }

                    if (next.getAttribute(Columns.TPS_CODIGO).equals(CodedValues.TPS_TIPO_TAC)) {
                        tipoTac = next.getAttribute(Columns.PSC_VLR).toString();
                    } else if (next.getAttribute(Columns.TPS_CODIGO).equals(CodedValues.TPS_VALOR_MIN_TAC)) {
                        valorMinTac = next.getAttribute(Columns.PSC_VLR).toString();
                        try {
                            valorMinTac = NumberHelper.reformat(valorMinTac, "en", NumberHelper.getLang());
                        } catch (java.text.ParseException ex2) {
                        }
                    } else if (next.getAttribute(Columns.TPS_CODIGO).equals(CodedValues.TPS_VALOR_MAX_TAC)) {
                        valorMaxTac = next.getAttribute(Columns.PSC_VLR).toString();
                        try {
                            valorMaxTac = NumberHelper.reformat(valorMaxTac, "en", NumberHelper.getLang());
                        } catch (java.text.ParseException ex2) {
                        }
                    }
                }
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }

            // Pega limite de valor de TAC cadastrado pelo CSE.
            boolean temLimiteMaxTacCse = false;
            String maxTacCseEn = "";
            String maxTacCsePt = "";
            String ordTaxas = CodedValues.ORDEM_TAXAS_NA;
            try {
                ParamSvcCseTO pscTO = new ParamSvcCseTO();
                pscTO.setCseCodigo(CodedValues.CSE_CODIGO_SISTEMA);
                pscTO.setSvcCodigo(svcCodigo);
                pscTO.setTpsCodigo(CodedValues.TPS_VALOR_MAX_TAC);
                pscTO = parametroController.findParamSvcCse(pscTO, responsavel);
                maxTacCseEn = pscTO.getPseVlr();
                if (!TextHelper.isNull(maxTacCseEn)) {
                    temLimiteMaxTacCse = true;
                    maxTacCsePt = NumberHelper.reformat(maxTacCseEn, "en", NumberHelper.getLang());
                }
            } catch (Exception ex) {
                // Não imprime erro, pois o método lança exceção quando
                // o parâmetro não está cadastrado na base de dados.
            }

            try {
                // Recupera o tipo de ordenação das taxas pelo prazo
                ParamSvcCseTO pscOrdTO = new ParamSvcCseTO();
                pscOrdTO.setCseCodigo(CodedValues.CSE_CODIGO_SISTEMA);
                pscOrdTO.setSvcCodigo(svcCodigo);
                pscOrdTO.setTpsCodigo(CodedValues.TPS_ORDENACAO_CADASTRO_TAXAS);
                pscOrdTO = parametroController.findParamSvcCse(pscOrdTO, responsavel);
                ordTaxas = (pscOrdTO.getPseVlr() != null) ? pscOrdTO.getPseVlr() : CodedValues.ORDEM_TAXAS_NA;
            } catch (Exception ex) {
                // Não imprime erro, pois o método lança exceção quando
                // o parâmetro não está cadastrado na base de dados.
            }

            boolean dataInicialFutura = false;
            Date dataIniVigencia = null;
            Date dataCadastro = null;
            if (coeficientes != null && !coeficientes.isEmpty()) {
                try {
                    List<String> chaves = new ArrayList<>(coeficientes.keySet());
                    String chave = chaves.get(0).toString();
                    CustomTransferObject coefTO = (CustomTransferObject) coeficientes.get(chave);
                    dataCadastro = (Date) coefTO.getAttribute(Columns.CFT_DATA_CADASTRO);
                    dataIniVigencia = (Date) coefTO.getAttribute(Columns.CFT_DATA_INI_VIG);
                    dataInicialFutura = (dataIniVigencia != null && DateHelper.clearHourTime(dataIniVigencia).after(DateHelper.clearHourTime(new Date())));
                } catch (Exception ex) {
                    dataInicialFutura = false;
                    dataIniVigencia = null;
                }
            }

            boolean editar = (responsavel.temPermissao(CodedValues.FUN_EDT_COEFICIENTES) || responsavel.temPermissao(CodedValues.FUN_TAXA_JUROS));

            Map<String, String> svcBloqEdicaoTaxas = simulacaoController.getSvcCadTaxaBloqueado(responsavel);
            editar = editar && !svcBloqEdicaoTaxas.containsKey(svcCodigo);

            boolean readOnly = editar ? false : true;
            boolean ocultarCamposTac = ParamSist.getInstance().getParam(CodedValues.TPC_OCULTAR_CAMPOS_TAC, responsavel) != null && ParamSist.getInstance().getParam(CodedValues.TPC_OCULTAR_CAMPOS_TAC, responsavel).toString().equals(CodedValues.TPC_SIM);

            if (!editar && svcBloqEdicaoTaxas.containsKey(svcCodigo)) {
                CustomTransferObject sto = servicoController.findServico(svcBloqEdicaoTaxas.get(svcCodigo));
                model.addAttribute("sto", sto);
            }

            // Lista os códigos dos serviços que possuem prazos para a consignatária
            List<String> svcCodigos = null;
            try {
                svcCodigos = simulacaoController.getSvcCodigosParaCadastroTaxas(csaCodigo, responsavel);
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }

            List<TransferObject> servicos = null;

            if (svcCodigos != null && svcCodigos.size() > 0) {
                try {
                    CustomTransferObject criterio = new CustomTransferObject();
                    criterio.setAttribute(Columns.SVC_CODIGO, svcCodigos);
                    criterio.setAttribute(Columns.SVC_ATIVO, CodedValues.STS_ATIVO);

                    servicos = convenioController.lstServicos(criterio, -1, -1, responsavel);

                } catch (Exception ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    servicos = new ArrayList<>();
                }
            } else {
                // Nenhum Serviço com prazo cadastrado
                servicos = new ArrayList<>();
            }

            // Exibe Botao que leva ao rodapé
            boolean exibeBotaoRodape = ParamSist.paramEquals(CodedValues.TPC_EXIBE_BOTAO_RESPONSAVEL_PELO_RODAPE_DA_PAGINA, CodedValues.TPC_SIM, responsavel);

            final ParamSvcTO paramSvc = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
            boolean taxaJurosObrigatoria = paramSvc.isTpsExigeCadastroTaxaJurosParaCet();
            
            boolean exibeCETMinMax = ParamSist.getBoolParamSist(CodedValues.TPC_CSA_PODE_CADASTRAR_CET_MININO_E_MAXIMO, responsavel);

            model.addAttribute("exibeBotaoRodape", exibeBotaoRodape);
            model.addAttribute("temCET", temCET);
            model.addAttribute("editaTaxaCet", editaTaxaCet);
            model.addAttribute("subtipo", subtipo);
            model.addAttribute("svcDescricao", svcDescricao);
            model.addAttribute("titulo", titulo);
            model.addAttribute("editar", editar);
            model.addAttribute("svcBloqEdicaoTaxas", svcBloqEdicaoTaxas);
            model.addAttribute("svcCodigo", svcCodigo);
            model.addAttribute("temLimiteTaxa", temLimiteTaxa);
            model.addAttribute("mesReferencia", mesReferencia);
            model.addAttribute("limiteTaxa", limiteTaxa);
            model.addAttribute("dataAbertura", dataAbertura);
            model.addAttribute("dataLimite", dataLimite);
            model.addAttribute("ordTaxas", ordTaxas);
            model.addAttribute("readOnly", readOnly);
            model.addAttribute("ocultarCamposTac", ocultarCamposTac);
            model.addAttribute("tipoTac", tipoTac);
            model.addAttribute("tac", tac);
            model.addAttribute("valorMinTac", valorMinTac);
            model.addAttribute("valorMaxTac", valorMaxTac);
            model.addAttribute("dataCadastro", dataCadastro);
            model.addAttribute("dataIniVigencia", dataIniVigencia);
            model.addAttribute("dataInicialFutura", dataInicialFutura);
            model.addAttribute("periodo", periodo);
            model.addAttribute("prazos", prazos);
            model.addAttribute("prazosFile", prazosFile);
            model.addAttribute("csaCodigo", csaCodigo);
            model.addAttribute("tipo", tipo);
            model.addAttribute("temLimiteMaxTacCse", temLimiteMaxTacCse);
            model.addAttribute("maxTacCseEn", maxTacCseEn);
            model.addAttribute("maxTacCsePt", maxTacCsePt);
            model.addAttribute("coeficientes", coeficientes);
            model.addAttribute("servicos", servicos);
            model.addAttribute("taxaJurosObrigatoria", taxaJurosObrigatoria);
            model.addAttribute("exibeCETMinMax", exibeCETMinMax);

        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        return viewRedirect("jsp/manterTaxas/editarTaxa", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            String csaCodigo = responsavel.isCsa() ? responsavel.getCodigoEntidade() : JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
            String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");

            List<PrazoTransferObject> prazos = null;
            // Seleciona prazos ativos.
            try {
                prazos = simulacaoController.findPrazoCsaByServico(svcCodigo, csaCodigo, responsavel);
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            TransferObject cto = null;

            String nomeCampo;
            String cftVlr;
            String cftVlrMinimo = null;
            String cftVlrRef;
            Short przVlr;

            PrazoTransferObject pto = null;
            Iterator<PrazoTransferObject> it = null;

            // Salva coeficientes preechidos.
            // Sincroniza a sessão do usuário para evitar duplo request
            synchronized (session) {
                // Compara os tokens de sincronização
                if (!SynchronizerToken.isTokenValid(request)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.taxa.juros.ja.cadastradas", responsavel));
                } else {
                    try {
                        String msgSucesso = "";
                        final ParamSvcTO paramSvc = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
                        boolean taxaJurosObrigatoria = paramSvc.isTpsExigeCadastroTaxaJurosParaCet();
                        boolean exibeCETMinMax = ParamSist.getBoolParamSist(CodedValues.TPC_CSA_PODE_CADASTRAR_CET_MININO_E_MAXIMO, responsavel);
                        if (JspHelper.verificaVarQryStr(request, "ALTERA_CFT").equals("1")) {
                            // Salva os coeficientes
                            List<TransferObject> lstCoeficientes = new ArrayList<>();
                            it = prazos.iterator();
                            while (it.hasNext()) {
                                pto = it.next();
                                przVlr = pto.getPrzVlr();
                                nomeCampo = String.valueOf(przVlr);
                                if(exibeCETMinMax) {
                                	cftVlrMinimo = JspHelper.verificaVarQryStr(request, "cft_min_" + nomeCampo);
                                }
                                cftVlr = JspHelper.verificaVarQryStr(request, "cft_" + nomeCampo);     
                                cftVlrRef = JspHelper.verificaVarQryStr(request, "taxa_" + nomeCampo);

                                if (!TextHelper.isNull(cftVlr)) {
                                    cto = new CustomTransferObject();
                                    if(!TextHelper.isNull(cftVlrMinimo)) {
                                    	cto.setAttribute(Columns.CFT_VLR_MINIMO, NumberHelper.reformat(cftVlrMinimo, NumberHelper.getLang(), "en", 2, 4));
                                    }
                                    cto.setAttribute(Columns.CFT_VLR, NumberHelper.reformat(cftVlr, NumberHelper.getLang(), "en", 2, 4));
                                    boolean taxaCetZerada = Float.valueOf(0).equals(Float.parseFloat(cftVlr.replace(",",".")));
                                    if (!TextHelper.isNull(cftVlrRef)) {
                                        boolean jurosMenorQueCet = cftVlr.replace(",",".").compareTo(cftVlrRef.replace(",", ".")) >= 0;
                                        if (!taxaJurosObrigatoria || jurosMenorQueCet) {
                                            cto.setAttribute(Columns.CFT_VLR_REF, NumberHelper.reformat(cftVlrRef, NumberHelper.getLang(), "en", 2, 4));
                                        } else {
                                            throw new Exception(ApplicationResourcesHelper.getMessage("mensagem.erro.limite.taxa.juros.composicao.cet.valor.maximo", responsavel));
                                        }
                                    } else if (taxaJurosObrigatoria && !taxaCetZerada) {
                                        throw new Exception(ApplicationResourcesHelper.getMessage("mensagem.erro.limite.taxa.juros.obrigatoria", responsavel));
                                    }
                                    cto.setAttribute(Columns.PRZ_VLR, przVlr);
                                    lstCoeficientes.add(cto);
                                }
                            }
                            simulacaoController.setTaxaJuros(csaCodigo, svcCodigo, lstCoeficientes, responsavel);
                            msgSucesso += ApplicationResourcesHelper.getMessage("mensagem.taxa.juros.cadastrada.sucesso", responsavel);
                        } else if (TextHelper.isNull(JspHelper.verificaVarQryStr(request, "FILE1"))) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.taxa.juros.ja.cadastradas", responsavel));
                        }

                        if (JspHelper.verificaVarQryStr(request, "ALTERA_PSC").equals("1")) {
                            // Salva as taxas fixas (paramSvcCsa)
                            List<String> tpsCodigos = new ArrayList<>();
                            tpsCodigos.add(CodedValues.TPS_TAC_FINANCIADA);
                            tpsCodigos.add(CodedValues.TPS_TIPO_TAC);
                            tpsCodigos.add(CodedValues.TPS_VALOR_MIN_TAC);
                            tpsCodigos.add(CodedValues.TPS_VALOR_MAX_TAC);
                            List<TransferObject> param = new ArrayList<>();
                            String pscVlr = null;
                            for (String element : tpsCodigos) {
                                cto = new CustomTransferObject();
                                pscVlr = JspHelper.verificaVarQryStr(request, "tps_" + element);
                                if (!pscVlr.equals("") && !element.equals(CodedValues.TPS_TIPO_TAC)) {
                                    pscVlr = NumberHelper.reformat(pscVlr, NumberHelper.getLang(), "en", 2, 2);
                                }
                                cto.setAttribute(Columns.TPS_CODIGO, element);
                                cto.setAttribute(Columns.PSC_SVC_CODIGO, svcCodigo);
                                cto.setAttribute(Columns.PSC_CSA_CODIGO, csaCodigo);
                                cto.setAttribute(Columns.PSC_VLR, pscVlr);
                                param.add(cto);
                            }
                            parametroController.updateParamSvcCsa(param, responsavel);
                            msgSucesso += ApplicationResourcesHelper.getMessage("mensagem.taxa.juros.tac.cadastrada.sucesso", responsavel);
                        }

                        session.setAttribute(CodedValues.MSG_INFO, msgSucesso);
                    } catch (Exception ex) {
                        session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    }

                    // Copia os parâmetros do serviço atual para os serviços selecionados
                    String[] svcsDestino = request.getParameterValues("copia_svc_corrente");
                    if (svcsDestino != null && svcsDestino.length > 0) {
                        for (String svcCodigoDestino : svcsDestino) {
                            // Copia os parâmetros do serviço atual para o serviço selecionado
                            if (!svcCodigo.equals(svcCodigoDestino)) {
                                simulacaoController.copiaTaxaJuros(svcCodigo, svcCodigoDestino, csaCodigo, responsavel);
                            }
                        }
                    }

                    if (ParamSist.paramEquals(CodedValues.TPC_BLOQUEIA_CSA_POR_CET_EXPIRADO, CodedValues.TPC_SIM, responsavel) &&
                            consignatariaController.verificarDesbloqueioAutomaticoConsignataria(csaCodigo, responsavel)) {
                        session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.informacao.csa.desbloqueada.automaticamente", responsavel));
                    }
                }
            }
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        return iniciar(request, response, session, model);

    }

    @RequestMapping(params = { "acao=ativar" })
    public String ativar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            String csaCodigo = responsavel.isCsa() ? responsavel.getCodigoEntidade() : JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
            String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");

            if ((responsavel.isCseSup() || responsavel.isCsa()) && responsavel.temPermissao(CodedValues.FUN_ATIVAR_TAXA_JUROS_DATA_FUTURA)) {

                String cfrDataVig = JspHelper.verificaVarQryStr(request, "CFT_DATA_VIG");
                String cfrDataVigOld = JspHelper.verificaVarQryStr(request, "CFT_DATA_VIG_OLD");
                // Nova data deve ser maior ou igual a atual
                if (!cfrDataVig.equals(cfrDataVigOld) && DateHelper.parse(cfrDataVig, LocaleHelper.getDatePattern()).compareTo(DateHelper.clearHourTime(new Date())) >= 0) {
                    coeficienteAtivoController.anteciparDataInicioCoeficiente(cfrDataVig, csaCodigo, svcCodigo, responsavel);
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.informacao.taxa.juros.data.vigencia.alterada.sucesso", responsavel));
                }
            }
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        return iniciar(request, response, session, model);
    }
}
