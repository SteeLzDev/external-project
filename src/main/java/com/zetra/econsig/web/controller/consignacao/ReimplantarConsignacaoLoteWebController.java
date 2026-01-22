package com.zetra.econsig.web.controller.consignacao;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ServicoTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.MargemControllerException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoFuncaoServico;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.job.process.ProcessaRelatorioReimplanteLote;
import com.zetra.econsig.persistence.entity.MargemRegistroServidor;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignacao.ReimplantarConsignacaoController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ReimplantarConsignacaoLoteWebController</p>
 * <p>Description: WebController que contém os endpoints do fluxo de reimplante de consignação via lote.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */

@Controller
@RequestMapping(method = {RequestMethod.POST}, value = {"/v3/reimplantarConsignacaoLote"})
public class ReimplantarConsignacaoLoteWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ReimplantarConsignacaoLoteWebController.class);

    public static final int TAMANHO_MSG_ERRO_DEFAULT = 100;

    public static final String COMPLEMENTO_DEFAULT = " ";

    @Autowired
    private ReimplantarConsignacaoController reimplantarConsignacaoController;

    @Autowired
    private ConsultarMargemController consultarMargemController;

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @RequestMapping(params = {"acao=iniciar"})
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);


        model.addAttribute("flowOne", true);
        return viewRedirect("jsp/reimplantarConsignacao/reimplantarConsignacaoLote", request, session, model, responsavel);
    }

    @RequestMapping(params = {"acao=processar"})
    public String procesarAde(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws AutorizacaoControllerException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        final String fileName = JspHelper.verificaVarQryStr(request, "FILE1");
        List<TransferObject> consignacoes;

        final int size = JspHelper.LIMITE;
        int offset = 0;
        try {
            offset = Integer.parseInt(request.getParameter("offset"));
        } catch (final Exception ex) {
        }

        final ArrayList<String> adeList = new ArrayList<>();
        final Map<String, Object> errorAdeNumero = new HashMap<>();

        if (!TextHelper.isNull(fileName)) {
            final String hashDir = session.getId();
            final String diretorioRaizArquivos = ParamSist.getDiretorioRaizArquivos();
            final String diretorioDestinoUploadHelper = UploadHelper.SUBDIR_ARQUIVOS_TEMPORARIOS + File.separatorChar + "anexo" + File.separatorChar + hashDir;
            final String file = diretorioRaizArquivos + File.separator + diretorioDestinoUploadHelper + File.separatorChar + fileName;

            final List<String> fileToList = FileHelper.readAllToList(file);
            if (!TextHelper.isNull(fileToList) && !fileToList.isEmpty()) {
                String adeNumero = null;

                for (String linha : fileToList) {
                    if (linha.trim().contains(",") || linha.trim().contains(".") || linha.trim().contains("@") || linha.trim().contains(";")) {
                        LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.erro.layout.lote.reimplante", responsavel));
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.layout.lote.reimplante", responsavel, linha.trim()));
                        return iniciar(request, response, session, model);
                    } else {
                        adeNumero = linha.trim();
                        adeList.add(adeNumero);
                        errorAdeNumero.put(adeNumero, null);
                    }
                }
            }

            consignacoes = pesquisarConsignacaoController.findAdeReimplanteLote(adeList, offset, size, responsavel);
        } else {
            final String[] ades = JspHelper.verificaVarQryStr(request, "ADES").split(";");
            adeList.addAll(Arrays.asList(ades));
            for (final String ade : ades) {
                errorAdeNumero.put(ade, null);
            }

            consignacoes = pesquisarConsignacaoController.findAdeReimplanteLote(adeList, offset, size, responsavel);
        }

        final List<String> adeCodigosFull = new ArrayList<>();

        final List<TransferObject> consignacoesTotal = pesquisarConsignacaoController.findAdeReimplanteLote(adeList, -1, -1, responsavel);
        for (final TransferObject ade : consignacoesTotal) {
            final String adeNum = ade.getAttribute(Columns.ADE_NUMERO).toString();
            final String adeCodigo = ade.getAttribute(Columns.ADE_CODIGO).toString();
            final String exist = (String) errorAdeNumero.get(adeNum);
            if (TextHelper.isNull(exist)) {
                adeCodigosFull.add(adeCodigo);
                errorAdeNumero.put(adeNum, ade);
            }
        }

        final int total = pesquisarConsignacaoController.findAdeReimplanteLoteCount(adeList, responsavel);

        final Map<String, String[]> parameterMap = new HashMap<>(request.getParameterMap());
        parameterMap.remove("offset");
        parameterMap.remove("back");

        // Monta lista de parâmetros através dos parâmetros de request
        final Set<String> params = new HashSet<>(request.getParameterMap().keySet());

        // Ignora os parâmetros abaixo
        params.remove("senha");
        params.remove("serAutorizacao");
        params.remove("cryptedPasswordFieldName");
        params.remove("offset");
        params.remove("back");
        params.remove("linkRet");
        params.remove("linkRet64");
        params.remove("eConsig.page.token");
        params.remove("_skip_history_");
        params.remove("pager");

        final List<String> requestParams = new ArrayList<>(params);

        final String linkListagem = "../v3/reimplantarConsignacaoLote?acao=processar";
        configurarPaginador(linkListagem, "rotulo.paginacao.titulo.download.arq.integracao", total, size, requestParams, false, request, model);

        if (!TextHelper.isNull(errorAdeNumero) && !errorAdeNumero.isEmpty()) {
            final StringBuilder msgn = new StringBuilder();
            for (final Map.Entry<String, Object> ade : errorAdeNumero.entrySet()) {
                if (TextHelper.isNull(ade.getValue())) {
                    msgn.append(ade.getKey()).append(" ");
                }
            }
            if (!TextHelper.isNull(msgn) && !msgn.isEmpty()) {
                session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("rotulo.alert.reimplantar.lote.ade", responsavel) + ": " + msgn);
            }
        }

        model.addAttribute("adeCodigosFull", adeCodigosFull);
        model.addAttribute("consignacoes", consignacoes);
        model.addAttribute("flowOne", false);
        return viewRedirect("jsp/reimplantarConsignacao/reimplantarConsignacaoLote", request, session, model, responsavel);
    }

    @RequestMapping(params = {"acao=reimplantar"})
    public String reimplantar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws AutorizacaoControllerException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        final Map<String, String[]> parameterMap = new HashMap<>();

        final List<String> retorno = new ArrayList<>();
        final String absolutePath = ParamSist.getDiretorioRaizArquivos();
        final String pathFull = absolutePath + File.separatorChar + "reimplante" + File.separatorChar + "cse" + File.separatorChar;

        final File dir = new File(pathFull);

        if (!dir.exists() && !dir.mkdirs()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.diretorio.inexistente", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String[] adeCodigoArray = null;
        final String todasAdes = JspHelper.verificaVarQryStr(request, "checkAll_chkADE");
        if (todasAdes.equals("on")) {
            adeCodigoArray = request.getParameterValues("adeFullCodigos");
        } else {
            adeCodigoArray = request.getParameterValues("chkAdeCodigo");
        }
        List<String> adeCodRelatorio = new ArrayList<>();

        final List<String> adeCodigos = new ArrayList<>(Arrays.asList(adeCodigoArray));

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request) || TextHelper.isNull(adeCodigos)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final boolean exigeMotivo = (ParamSist.paramEquals(CodedValues.TPC_EXIGE_TIPO_MOTIVO_CANC, CodedValues.TPC_SIM, responsavel) && FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_REIMP_CONSIGNACAO, responsavel));

        final boolean permiteAlterarNumeroAde = (ParamSist.paramEquals(CodedValues.TPC_PERMITE_ALTERAR_ADE_NUMERO_REIMP_MANUAL, CodedValues.TPC_SIM, responsavel) && !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_CLASSE_GERADOR_ADE_NUMERO, responsavel)));

        final boolean permiteReducaoValorParcela = (ParamSist.paramEquals(CodedValues.TPC_EXPORTACAO_APENAS_INICIAL, CodedValues.TPC_SIM, responsavel) && ParamSist.paramEquals(CodedValues.TPC_HABILITA_REIMPLANTACAO_COM_REDUCAO_VALOR, CodedValues.TPC_SIM, responsavel));

        final boolean alterarNumeroAde = (permiteAlterarNumeroAde && JspHelper.verificaVarQryStr(request, "alterarNumeroAde").equals("S"));

        for (final String adeCodigo : adeCodigos) {
            Long adeNum = null;
            try {
                final ServicoTransferObject servico = convenioController.findServicoByAdeCodigo(adeCodigo, responsavel);
                if (!AcessoFuncaoServico.temAcessoFuncao(request, CodedValues.FUN_REIMP_CONSIGNACAO, responsavel.getUsuCodigo(), servico.getSvcCodigo())) {
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                try {
                    CustomTransferObject tmo = null;
                    String obsOca = null;

                    if (!exigeMotivo) {
                        obsOca = JspHelper.verificaVarQryStr(request, "obs");
                    } else {
                        tmo = new CustomTransferObject();
                        tmo.setAttribute(Columns.ADE_CODIGO, adeCodigo);
                        tmo.setAttribute(Columns.TMO_CODIGO, JspHelper.verificaVarQryStr(request, "TMO_CODIGO"));
                        tmo.setAttribute(Columns.OCA_OBS, JspHelper.verificaVarQryStr(request, "ADE_OBS"));
                        tmo.setAttribute(Columns.OCA_PERIODO, JspHelper.verificaVarQryStr(request, "OCA_PERIODO"));
                    }

                    CustomTransferObject autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
                    adeNum = (Long) autdes.getAttribute(Columns.ADE_NUMERO);
                    final Date adeAnoMesIniOld = (Date) autdes.getAttribute(Columns.ADE_ANO_MES_INI);
                    reimplantarConsignacaoController.reimplantar(adeCodigo, obsOca, tmo, alterarNumeroAde, permiteReducaoValorParcela, false, responsavel);

                    autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
                    final String rseCodigo = (String) autdes.getAttribute(Columns.RSE_CODIGO);
                    final Short incMargem = (Short) autdes.getAttribute(Columns.ADE_INC_MARGEM);

                    final Date adeAnoMesIniNew = (Date) autdes.getAttribute(Columns.ADE_ANO_MES_INI);
                    final MargemRegistroServidor mrsRse = consultarMargemController.getMargemRegistroServidor(rseCodigo, incMargem, responsavel);

                    if ((mrsRse != null) && (adeAnoMesIniNew.compareTo(adeAnoMesIniOld) > 0) && (mrsRse.getMrsPeriodoIni() != null) && (adeAnoMesIniNew.compareTo(mrsRse.getMrsPeriodoIni()) >= 0)) {
                        retorno.add(gerarLinhaArquivoSaida(adeNum.toString() + ";" , " ", ApplicationResourcesHelper.getMessage("mensagem.info.calcular.data.inicio.fim.margem.extra.rse.data.margem.maior.web", responsavel)));
                        session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.info.calcular.data.inicio.fim.margem.extra.rse.data.margem.maior.web", responsavel));
                    } else if (autdes.getAttribute(Columns.ADE_NUMERO).equals(adeNum)) {
                        adeCodRelatorio.add(adeCodigo);
                        retorno.add(gerarLinhaArquivoSaida(adeNum.toString() + ";", " ", ApplicationResourcesHelper.getMessage("mensagem.informe.reimplate.sucesso", responsavel)));
                    } else {
                        adeCodRelatorio.add(adeCodigo);
                        retorno.add(gerarLinhaArquivoSaida(adeNum.toString() + ";", " ", ApplicationResourcesHelper.getMessage("rotulo.critica.sucesso.alteracao.adenumero", responsavel, autdes.getAttribute(Columns.ADE_NUMERO).toString())));
                    }
                } catch (AutorizacaoControllerException | MargemControllerException ex) {
                    retorno.add(gerarLinhaArquivoSaida(adeNum.toString() + ";", " ", ex.getMessage()));
                    LOG.error(ex.getMessage());
                }

            } catch (final ConvenioControllerException ex) {
                retorno.add(gerarLinhaArquivoSaida(";", " ", ex.getMessage()));
                LOG.error(ex.getMessage());
            }
        }

        if (!retorno.isEmpty()) {
            String nomeArqSaida;
            String nomeArqSaidaTxt;
            String nomeArqSaidaZip;
            try {
                LOG.debug("ARQUIVOS RETORNO: " + DateHelper.getSystemDatetime());

                nomeArqSaida = pathFull + ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.retorno.prefixo", responsavel);

                nomeArqSaida += DateHelper.format(DateHelper.getSystemDatetime(), "dd-MM-yyyy-HHmmss");
                nomeArqSaidaTxt = nomeArqSaida + ".txt";
                final PrintWriter arqSaida = new PrintWriter(new BufferedWriter(new FileWriter(nomeArqSaidaTxt)));
                LOG.debug("nomeArqSaidaTxt: " + nomeArqSaidaTxt);

                for (final String retor : retorno) {
                    arqSaida.println(retor);
                }

                arqSaida.close();

                LOG.debug("FIM ARQUIVOS RETORNO: " + DateHelper.getSystemDatetime());
                LOG.debug("compacta os arquivos: " + DateHelper.getSystemDatetime());

                nomeArqSaidaZip = nomeArqSaida + ".zip";
                FileHelper.zip(nomeArqSaidaTxt, nomeArqSaidaZip);
                LOG.debug("fim - compacta os arquivos: " + DateHelper.getSystemDatetime());
                FileHelper.delete(nomeArqSaidaTxt);

                final String nomeArq = new File(nomeArqSaidaZip).getName();

                if (!adeCodRelatorio.isEmpty()) {
                    String pathRel = pathFull + File.separatorChar + "relatorios";
                    File dire = new File(pathRel);

                    if (!dire.exists()) {
                        dire.mkdirs();
                    }

                    String[] adeCod = adeCodRelatorio.toArray(new String[0]);
                    String[] alteraNum = {alterarNumeroAde ? "S" : "N"};
                    parameterMap.put("adeCodigos", adeCod);
                    parameterMap.put("adeNumAltera", alteraNum);
                    // Gera relatório de reimplante de contratos
                    ProcessaRelatorioReimplanteLote relatorio = new ProcessaRelatorioReimplanteLote(parameterMap, session, false, responsavel);
                    relatorio.start();

                }
                model.addAttribute("retornoPath", nomeArq);
                model.addAttribute("flowOne", true);
                session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.informe.reimplante.retorno", responsavel, nomeArq));
                return viewRedirect("jsp/reimplantarConsignacao/reimplantarConsignacaoLote", request, session, model, responsavel);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
        model.addAttribute("flowOne", true);
        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.reimplante.erro", responsavel));
        return viewRedirect("jsp/reimplantarConsignacao/reimplantarConsignacaoLote", request, session, model, responsavel);
    }

    private static String gerarLinhaArquivoSaida(String linha, String delimitador, String mensagem) {
        // Concatena a mensagem de erro no final da linha de entrada
        mensagem = (mensagem == null ? "" : mensagem);
        return (linha + delimitador + formataMsgErro(mensagem, COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
    }

    private static String formataMsgErro(String mensagem, String complemento, int tamanho, boolean alinhaEsquerda) {
        mensagem = (mensagem == null ? "" : mensagem);
        return TextHelper.removeAccent(TextHelper.formataMensagem(mensagem, complemento, tamanho, alinhaEsquerda)).toUpperCase();
    }

    @RequestMapping(params = {"acao=listarRelatorios"})
    public String listarRelatorios(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws AutorizacaoControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String absolutePath = ParamSist.getDiretorioRaizArquivos();
        String pathFullRel = absolutePath + File.separatorChar + "relatorio" + File.separatorChar + "cse" + File.separatorChar + "reimplante";
        String pathFullRet = absolutePath + File.separatorChar + "reimplante" + File.separatorChar + "cse";
        File diretorioRel = new File(pathFullRel);
        File diretorioRet = new File(pathFullRet);
        List<TransferObject> relatorios = null;
        List<TransferObject> retornos = null;

        FileFilter filtro = arq -> arq.getName().toLowerCase().endsWith(".zip");

        int size = JspHelper.LIMITE;
        int offset = 0;
        try {
            offset = Integer.parseInt(request.getParameter("offset"));
        } catch (Exception ex) {
        }

        ArrayList<File> arquivosRel = new ArrayList<>();
        File[] tempRel = diretorioRel.listFiles(filtro);
        if (tempRel != null) {
            arquivosRel = new ArrayList<>(Arrays.asList(tempRel));
        }

        arquivosRel.sort((f1, f2) -> {
            Long d1 = f1.lastModified();
            Long d2 = f2.lastModified();
            return d2.compareTo(d1);
        });

        ArrayList<File> arquivosRet = new ArrayList<>();
        File[] tempCri = diretorioRet.listFiles(filtro);
        if (tempCri != null) {
            arquivosRet = new ArrayList<>(Arrays.asList(tempCri));
        }

        arquivosRet.sort((f1, f2) -> {
            Long d1 = f1.lastModified();
            Long d2 = f2.lastModified();
            return d2.compareTo(d1);
        });

        TransferObject arq = null;
        if (!arquivosRel.isEmpty()) {
            relatorios = new ArrayList<>();
            for (File arquivo : arquivosRel) {
                arq = new CustomTransferObject();
                String nomeArq = arquivo.getPath().substring(pathFullRel.length() + 1);
                String tam = "";
                if (arquivo.length() > 1024.00) {
                    tam = Math.round(arquivo.length() / 1024.00) + " " + ApplicationResourcesHelper.getMessage("rotulo.kilobyte.abreviado", responsavel);
                } else {
                    tam = arquivo.length() + " " + ApplicationResourcesHelper.getMessage("rotulo.byte.abreviado", responsavel);
                }
                String data = DateHelper.toDateTimeString(new java.util.Date(arquivo.lastModified()));
                arq.setAttribute("nomeArq", nomeArq);
                arq.setAttribute("tamArq", tam);
                arq.setAttribute("dataArq", data);
                relatorios.add(arq);
            }
        }

        if (!arquivosRet.isEmpty()) {
            retornos = new ArrayList<>();
            int i = 0;
            int j = offset == -1 ? ((arquivosRet.size() % size) == 0 ? (arquivosRet.size() - size) : arquivosRet.size() - (arquivosRet.size() % size)) : offset;

            while (arquivosRet.size() > j && i < size) {
                File arquivo = arquivosRet.get(j);
                arq = new CustomTransferObject();
                String nomeArq = arquivo.getPath().substring(pathFullRet.length() + 1);
                String tam = "";
                if (arquivo.length() > 1024.00) {
                    tam = Math.round(arquivo.length() / 1024.00) + " " + ApplicationResourcesHelper.getMessage("rotulo.kilobyte.abreviado", responsavel);
                } else {
                    tam = arquivo.length() + " " + ApplicationResourcesHelper.getMessage("rotulo.byte.abreviado", responsavel);
                }
                String data = DateHelper.toDateTimeString(new java.util.Date(arquivo.lastModified()));
                i++;
                j++;
                arq.setAttribute("nomeArq", nomeArq);
                arq.setAttribute("tamArq", tam);
                arq.setAttribute("dataArq", data);
                retornos.add(arq);
            }
        }

        int totalRet = arquivosRet.size();

        Set<String> params = new HashSet<>(request.getParameterMap().keySet());

        // Ignora os parâmetros abaixo
        params.remove("senha");
        params.remove("serAutorizacao");
        params.remove("cryptedPasswordFieldName");
        params.remove("offset");
        params.remove("back");
        params.remove("linkRet");
        params.remove("linkRet64");
        params.remove("eConsig.page.token");
        params.remove("_skip_history_");
        params.remove("pager");

        List<String> requestParams = new ArrayList<>(params);

        String linkListagem = "../v3/reimplantarConsignacaoLote?acao=listarRelatorios";
        configurarPaginador(linkListagem, "rotulo.paginacao.titulo.download.arq.integracao", totalRet, size, requestParams, false, request, model);

        model.addAttribute("retornos", retornos);
        model.addAttribute("relatorios", relatorios);
        return viewRedirect("jsp/reimplantarConsignacao/listaRelatoriosReimplante", request, session, model, responsavel);
    }


}
