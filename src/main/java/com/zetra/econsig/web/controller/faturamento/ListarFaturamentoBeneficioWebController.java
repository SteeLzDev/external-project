package com.zetra.econsig.web.controller.faturamento;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
import com.zetra.econsig.dto.web.ArquivoDownload;
import com.zetra.econsig.exception.FaturamentoBeneficioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaArquivoFaturamento;
import com.zetra.econsig.job.process.Processo;
import com.zetra.econsig.service.beneficios.FaturamentoBeneficioController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.NaturezaConsignatariaEnum;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ListarRelacaoBeneficioWebController</p>
 * <p>Description: Listar Faturamento de Benefício</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author: larissa.silva $
 * $Revision: 25619 $
 * $Date: 2018-11-01 16:09:29 -0200 (Qui, 01 nov 2018) $
 */

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/consultarFaturamentos" })
public class ListarFaturamentoBeneficioWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarFaturamentoBeneficioWebController.class);

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private FaturamentoBeneficioController faturamentoBeneficioController;

    @Override
    public void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        String titulo = JspHelper.verificaVarQryStr(request, "titulo");
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.faturamento.beneficios.titulo", responsavel, titulo));
        model.addAttribute("acaoFormulario", "../v3/consultarFaturamentos");
        model.addAttribute("omitirAdeNumero", true);
    }

    @RequestMapping(params = { "acao=iniciar" })
    public String listar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws FaturamentoBeneficioControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            SynchronizerToken.saveToken(request);

            CustomTransferObject criterio = new CustomTransferObject();

            Date periodo = null;
            String consignataria = null;

            List<TransferObject> consignatarias = consignatariaController.lstConsignatariaByNatureza(NaturezaConsignatariaEnum.OPERADORA_BENEFICIOS.getCodigo(), responsavel);

            if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "FAT_PERIODO"))) {
                periodo = DateHelper.parsePeriodString(JspHelper.verificaVarQryStr(request, "FAT_PERIODO"));
                criterio.setAttribute(Columns.FAT_PERIODO, periodo);
            }

            if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "CSA_CODIGO"))) {
                consignataria = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
                criterio.setAttribute(Columns.CSA_CODIGO, consignataria);
            }

            List<TransferObject> faturamentoBeneficio = faturamentoBeneficioController.findFaturamento(criterio, responsavel);

            model.addAttribute("faturamentoBeneficio", faturamentoBeneficio);
            model.addAttribute("consignatarias", consignatarias);

            return viewRedirect("jsp/consultarFaturamento/listarFaturamento", request, session, model, responsavel);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=consultar" })
    public String consultar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws FaturamentoBeneficioControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            //Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String fatCodigo = JspHelper.verificaVarQryStr(request, "FAT_CODIGO");

            if (TextHelper.isNull(fatCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.FAT_CODIGO, fatCodigo);

            List<TransferObject> lstFaturamento = faturamentoBeneficioController.findFaturamento(criterio, responsavel);

            if (lstFaturamento == null || lstFaturamento.isEmpty()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            TransferObject faturamento = lstFaturamento.get(0);
            String csaCodigo = faturamento.getAttribute(Columns.CSA_CODIGO).toString();
            String absolutePath = ParamSist.getDiretorioRaizArquivos();
            String pathArquivos = absolutePath + File.separatorChar + "beneficio" + File.separatorChar + "fatura" + File.separatorChar + "csa" + File.separatorChar + csaCodigo + File.separatorChar + fatCodigo + File.separatorChar;

            // Cria filtro para seleção de arquivos .zip
            FileFilter filtro = new FileFilter() {
                @Override
                public boolean accept(File arq) {
                    String arqNome = arq.getName().toLowerCase();
                    return (arqNome.endsWith(".zip"));
                }
            };
            File diretorioRetorno = new File(pathArquivos);
            File[] temp = diretorioRetorno.listFiles(filtro);

            List<ArquivoDownload> arquivosPaginaAtual = null;
            List<File> arquivosCombo = new ArrayList<>();
            if (temp != null) {
                arquivosCombo.addAll(Arrays.asList(temp));

                // Ordena os arquivos baseado na data de modificação
                Collections.sort(arquivosCombo, (f1, f2) -> {
                    Long d1 = f1.lastModified();
                    Long d2 = f2.lastModified();
                    return d2.compareTo(d1);
                });

                arquivosPaginaAtual = ArquivoDownload.carregarArquivos(arquivosCombo, pathArquivos, null, responsavel);
            }

            String chave = "ArquivoFaturamento" + "|" + responsavel.getUsuCodigo();
            Processo processoArqFat = ControladorProcessos.getInstance().getProcesso(chave);
            boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(chave, session);
            Boolean validarTerminoProcesso = JspHelper.verificaVarQryStr(request, "validarTerminoProcesso").equalsIgnoreCase("true");
            if (validarTerminoProcesso && !temProcessoRodando && processoArqFat != null && processoArqFat.isSucesso()) {
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.gerado.relatorio.sucesso", responsavel));
            }

            model.addAttribute("faturamentoBeneficio", faturamento);
            model.addAttribute("arquivos", arquivosPaginaAtual);
            model.addAttribute("temProcessoRodando", temProcessoRodando);

            return viewRedirect("jsp/consultarFaturamento/detalharFaturamento", request, session, model, responsavel);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=gerarFaturamento" })
    public String gerarFaturamento(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws FaturamentoBeneficioControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            // Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String fatCodigo = JspHelper.verificaVarQryStr(request, "FAT_CODIGO");

            if (TextHelper.isNull(fatCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String chave = "ArquivoFaturamento" + "|" + responsavel.getUsuCodigo();
            boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(chave, session);

            if (!temProcessoRodando) {
                temProcessoRodando = true;

                ProcessaArquivoFaturamento processaRel = new ProcessaArquivoFaturamento(fatCodigo, responsavel);
                processaRel.start();
                ControladorProcessos.getInstance().incluir(chave, processaRel);
            } else {
                // Se o arquivo está sendo processado por outro usuário,
                // dá mensagem de erro ao usuário e permite que ele escolha outro arquivo
                session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.processando.arquivo", responsavel));
                temProcessoRodando = false;
            }

            // Repassa o token salvo, pois o método irá revalidar o token
            request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

            return consultar(request, response, session, model);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    public String getLinkAction() {
        return "../v3/consultarFaturamentos?acao=iniciar";
    }
}
