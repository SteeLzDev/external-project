package com.zetra.econsig.web.controller.consignacao;

import static com.zetra.econsig.values.ApplicationResourcesKeys.MENSAGEM_ERRO_UPLOAD_EDT_ANEXO_ADE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.TipoMotivoOperacaoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.pdf.PDFHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.AnexoCredenciamentoCsa;
import com.zetra.econsig.persistence.entity.CredenciamentoCsa;
import com.zetra.econsig.persistence.entity.ModeloTermoAditivo;
import com.zetra.econsig.persistence.entity.ModeloTermoTag;
import com.zetra.econsig.persistence.entity.OcorrenciaCredenciamentoCsa;
import com.zetra.econsig.persistence.entity.StatusCredenciamento;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.sistema.TipoMotivoOperacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusCredenciamentoEnum;
import com.zetra.econsig.values.TipoArquivoEnum;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: VisualizarDashboardCredenciamentoWebController</p>
 * <p>Description: Controlador Web para o caso de uso Dashboard Credenciamento.</p>
 * <p>Copyright: Copyright (c) 2002-2022</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/visualizarDashboardCredenciamento" })
public class VisualizarDashboardCredenciamentoCsaWebController extends AbstractConsultarConsignacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(VisualizarDashboardCredenciamentoCsaWebController.class);

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private TipoMotivoOperacaoController tipoMotivoOperacaoController;

    private static final String MENSAGEM_ERRO_INTERNO = "mensagem.erroInternoSistema";
    private static final String JSP_VISUALIZAR_ERRO = "jsp/visualizarPaginaErro/visualizarMensagem";

    @Override
    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        final List<String> scrCodigos = new ArrayList<>();
        scrCodigos.add(StatusCredenciamentoEnum.AGUARDANDO_APROVACAO_TERMO_ADITIVO.getCodigo());
        scrCodigos.add(StatusCredenciamentoEnum.AGUARDANDO_ASSINATURA_TERMO_ADITIVO_CSA.getCodigo());
        scrCodigos.add(StatusCredenciamentoEnum.AGUARDANDO_ASSINATURA_TERMO_ADITIVO_CSE.getCodigo());
        scrCodigos.add(StatusCredenciamentoEnum.AGUARDANDO_ENVIO_DOCUMENTACAO_CSA.getCodigo());
        scrCodigos.add(StatusCredenciamentoEnum.AGUARDANDO_PREENCHIMENTO_TERMO_ADITIVO_CSE.getCodigo());
        scrCodigos.add(StatusCredenciamentoEnum.AGUARDANDO_VALIDACAO_DOCUMENTACAO_CSE.getCodigo());
        scrCodigos.add(StatusCredenciamentoEnum.FINALIZADO.getCodigo());

        List<TransferObject> lstCredenciamentoCsa = new ArrayList<>();
        List<StatusCredenciamento> lstStatusCredenciamento = new ArrayList<>();
        List<TransferObject> lstConsignatarias = new ArrayList<>();
        try {
            if(TextHelper.isNull(model.getAttribute("lstCredenciamentoCsa"))) {
                lstCredenciamentoCsa = consignatariaController.lstCredenciamentoCsaDashboard(responsavel);
            } else {
                lstCredenciamentoCsa = (List<TransferObject>) model.getAttribute("lstCredenciamentoCsa");
            }
            lstStatusCredenciamento = consignatariaController.lstStatusCredenciamentoByScrCodigos(scrCodigos, responsavel);
            lstConsignatarias = consignatariaController.lstConsignatarias(null, responsavel);
        } catch (final Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_INTERNO, responsavel));
            LOG.error(ex.getMessage(), ex);
            return viewRedirect(JSP_VISUALIZAR_ERRO, request, session, model, responsavel);
        }

        try {
            final HashMap<String, Boolean> hashAnexosCredenciamentoCsa = new HashMap<>();
            for(final TransferObject credenciamentoCsa : lstCredenciamentoCsa) {
                final String creCodigo = (String) credenciamentoCsa.getAttribute(Columns.CRE_CODIGO);
                final List<AnexoCredenciamentoCsa>  anexoCredenciamento = consignatariaController.lstAnexoCredenciamentoCsa(creCodigo, responsavel);
                hashAnexosCredenciamentoCsa.put(creCodigo, !anexoCredenciamento.isEmpty());
            }
            model.addAttribute("hashAnexosCredenciamentoCsa", hashAnexosCredenciamentoCsa);
        } catch (final Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_INTERNO, responsavel));
            LOG.error(ex.getMessage(), ex);
            return viewRedirect(JSP_VISUALIZAR_ERRO, request, session, model, responsavel);
        }

        final File diretorio = new File(ParamSist.getDiretorioRaizArquivos() + File.separatorChar + "credenciamento" + File.separatorChar + "modelo"  + File.separatorChar);
        final List<File> arquivos = new ArrayList<>();
        if (diretorio.exists()) {
            final File[] temp = diretorio.listFiles();

            for (final File temps : temp) {
            	if (((ApplicationResourcesHelper.getMessage("rotulo.dashboard.nome.arquivo.lista.documentos", responsavel) + "." + FilenameUtils.getExtension(temps.getName())).equals(temps.getName())) || ((ApplicationResourcesHelper.getMessage("rotulo.dashboard.nome.arquivo.termo.aditivo", responsavel) + "." + FilenameUtils.getExtension(temps.getName())).equals(temps.getName()))) {
            		arquivos.add(temps);
            	}
            }
        }
        if(TextHelper.isNull(model.getAttribute("lstCredenciamentoCsa"))) {
            model.addAttribute("lstCredenciamentoCsa", lstCredenciamentoCsa);
        }
        model.addAttribute("arquivos", arquivos);
        model.addAttribute("lstStatusCredenciamento", lstStatusCredenciamento);
        model.addAttribute("lstConsignatarias", lstConsignatarias);
        return viewRedirect("jsp/visualizarDashboardCredenciamentoCsa/visualizarDashboardCredenciamentoCsa", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=filtrar" })
    public String filtrarCredenciamento(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        List<TransferObject> lstCredenciamentoCsa = new ArrayList<>();

        try {
            final String strCreDataIni = JspHelper.verificaVarQryStr(request, "periodoIni");
            final String strCreDataFim = JspHelper.verificaVarQryStr(request, "periodoFim");
            Date creDataIni = null;
            Date creDataFim = null;
            final List<String> scrCodigos = request.getParameter("scrCodigos") != null ? Arrays.asList(request.getParameterValues("scrCodigos")) : new ArrayList<>();
            final List<String> csaCodigos = request.getParameter("csaCodigo") != null ? Arrays.asList(request.getParameterValues("csaCodigo")) : new ArrayList<>();

            if(!TextHelper.isNull(strCreDataIni)) {
                creDataIni = DateHelper.parse(strCreDataIni, "dd/MM/yyyy");
            }

            if(!TextHelper.isNull(strCreDataFim)) {
                creDataFim = DateHelper.parse(strCreDataFim, "dd/MM/yyyy");
            }

            lstCredenciamentoCsa = consignatariaController.lstCredenciamentoCsaDashboardFiltro(creDataIni, creDataFim, scrCodigos, csaCodigos, responsavel);
        } catch (final Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_INTERNO, responsavel));
            LOG.error(ex.getMessage(), ex);
            return viewRedirect(JSP_VISUALIZAR_ERRO, request, session, model, responsavel);
        }

        model.addAttribute("lstCredenciamentoCsa", lstCredenciamentoCsa);
        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=uploadArquivoCredenciamento" })
    public String uploadArquivoCredenciamento(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws IOException, ZetraException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        final String paramTamMaxArqAnexo = (String) ParamSist.getInstance().getParam(CodedValues.TPC_TAM_MAX_ARQ_ANEXO_CREDENCIAMENTO, responsavel);
        final int tamMaxArqAnexo = (!TextHelper.isNull(paramTamMaxArqAnexo) ? Integer.parseInt(paramTamMaxArqAnexo) : 200);
        final UploadHelper uploadHelper = new UploadHelper();

        try {
            uploadHelper.processarRequisicao(request.getServletContext(), request, tamMaxArqAnexo * 1024);
        } catch (final Throwable ex) {
            LOG.error(ex.getMessage(), ex);
            final String msg = ex.getMessage();
            if (!TextHelper.isNull(msg)) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, msg);
            }
            return viewRedirect(JSP_VISUALIZAR_ERRO, request, session, model, responsavel);
        }

        if (uploadHelper.getValorCampoFormulario("FILE1") != null) {
        	final String tipoArquivo = JspHelper.verificaVarQryStr(request, uploadHelper, "tipoArquivo");
        	final String nomeUpload = uploadHelper.getValorCampoFormulario("FILE1");
        	String nomeAnexo = "";

        	if (!TextHelper.isNull(tipoArquivo) && "1".equals(tipoArquivo)) {
        		nomeAnexo =  ApplicationResourcesHelper.getMessage("rotulo.dashboard.nome.arquivo.lista.documentos", responsavel) + "." + FilenameUtils.getExtension(nomeUpload);
        	} else if (!TextHelper.isNull(tipoArquivo) && "2".equals(tipoArquivo)) {
        		nomeAnexo =  ApplicationResourcesHelper.getMessage("rotulo.dashboard.nome.arquivo.termo.aditivo", responsavel) + "." + FilenameUtils.getExtension(nomeUpload);
        	} else {
        		throw new ZetraException("mensagem.erro.upload.anexo.confirmar.credenciamento", responsavel);
        	}

        	final String diretorioRaizArquivos = ParamSist.getDiretorioRaizArquivos();
            final String diretorioTemporario = diretorioRaizArquivos + File.separator + UploadHelper.SUBDIR_ARQUIVOS_TEMPORARIOS;
            final String path = File.separatorChar + "credenciamento" + File.separatorChar + "modelo"  + File.separatorChar;
            final File diretorioTemporarioNome = new File(diretorioTemporario + File.separatorChar + "anexo" + File.separatorChar + session.getId() + File.separatorChar);
            final File arquivo = new File(diretorioTemporarioNome.getPath() + File.separatorChar + nomeUpload);
            final File diretorioDestino = new File(diretorioRaizArquivos + File.separator + path);
            final File arquivoDestino = new File(diretorioDestino.getPath() + File.separator + nomeAnexo);
            uploadHelper.salvarArquivos(path, UploadHelper.EXTENSOES_PERMITIDAS_DOCUMENTOS_CREDENCIAMENTO, null);

            if (!diretorioTemporarioNome.exists()) {
            	diretorioTemporarioNome.mkdirs();
            } else if (!diretorioDestino.exists()) {
            	diretorioDestino.mkdirs();
            }

        	try {
                FileHelper.copyFile(arquivo, arquivoDestino);
            } catch (final Exception e) {
                if (diretorioTemporarioNome.exists()) {
                    FileHelper.deleteDir(diretorioTemporarioNome.getPath());
                }
                throw new ZetraException("mensagem.erro.upload.anexo.confirmar.credenciamento", responsavel);
            }

        	// Gera log de upload de arquivo
            try {
                final LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.UPLOAD_FILE, Log.LOG_INFORMACAO);
                log.add(ApplicationResourcesHelper.getMessage("rotulo.compact.arquivo.log", responsavel) + ": " + arquivo.getAbsolutePath());
                log.write();
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("rotulo.compact.erro.interno", responsavel));
            }
        	session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.upload.sucesso.renomeado.arquivo", responsavel, nomeUpload, nomeAnexo));

        	if(arquivo.exists()){
        	  FileHelper.deleteDir(arquivo.getPath());
        	}
        }
    return iniciar(request, response, session, model);
  }

    @RequestMapping(params = { "acao=detalharCredenciamento" })
    public String detalharCredenciamento(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        CredenciamentoCsa credenciamentoCsa = new CredenciamentoCsa();
        List<OcorrenciaCredenciamentoCsa> lstOcorrencia = new ArrayList<>();
        List<AnexoCredenciamentoCsa> lstAnexo = new ArrayList<>();
        final String creCodigo = JspHelper.verificaVarQryStr(request, "creCodigo");
        boolean permiteConcluirAssinatura = false;
        final String credenciamentoTermoAditivoAssinadoCsa = TipoArquivoEnum.ARQUIVO_ANEXO_CREDENCIAMENTO_TERMO_ADITIVO_ASSINADO.getCodigo();
        final String credenciamentoTermoAditivoAssinadoCse = TipoArquivoEnum.ARQUIVO_ANEXO_CREDENCIAMENTO_TERMO_ADITIVO_ASSINADO_CSE.getCodigo();
        try {
            credenciamentoCsa = consignatariaController.findByCreCodigoCredenciamentoCsa(creCodigo, responsavel);

            lstOcorrencia = consignatariaController.lstOcorrenciaCredenciamentoCsa(creCodigo,responsavel);
            lstAnexo = consignatariaController.lstAnexoCredenciamentoCsa(creCodigo, responsavel);

            final boolean existeAnexo = lstAnexo.stream().anyMatch(l -> l.getTarCodigo().equals(credenciamentoTermoAditivoAssinadoCsa) || l.getTarCodigo().equals(credenciamentoTermoAditivoAssinadoCse));

            if(((responsavel.isCsa() && credenciamentoCsa.getScrCodigo().equals(StatusCredenciamentoEnum.AGUARDANDO_ASSINATURA_TERMO_ADITIVO_CSA.getCodigo())) ||
                    (responsavel.isCseSup() && credenciamentoCsa.getScrCodigo().equals(StatusCredenciamentoEnum.AGUARDANDO_ASSINATURA_TERMO_ADITIVO_CSE.getCodigo())) ||
                    (responsavel.isCseSup() && credenciamentoCsa.getScrCodigo().equals(StatusCredenciamentoEnum.AGUARDANDO_APROVACAO_TERMO_ADITIVO.getCodigo()) && responsavel.temPermissao(CodedValues.FUN_APROVAR_TERMO_ADITIVO_CSA)))
                    && existeAnexo){
                permiteConcluirAssinatura = true;
            }

        } catch (final ConsignatariaControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.credenciamento.nao.existe", responsavel));
            LOG.error(ex.getMessage(), ex);
            return viewRedirect(JSP_VISUALIZAR_ERRO, request, session, model, responsavel);
        }

        final File diretorio = new File(ParamSist.getDiretorioRaizArquivos() + File.separatorChar + "credenciamento" + File.separatorChar + "modelo"  + File.separatorChar);
        File arquivoListaDoc = null;
        File arquivoTermoAditivo = null;
        if (diretorio.exists()) {
            final File[] temp = diretorio.listFiles();

            for (final File temps : temp) {
                if (((ApplicationResourcesHelper.getMessage("rotulo.dashboard.nome.arquivo.lista.documentos", responsavel) + "." + FilenameUtils.getExtension(temps.getName())).equals(temps.getName()))) {
                    arquivoListaDoc = temps;
                }
                if (((ApplicationResourcesHelper.getMessage("rotulo.dashboard.nome.arquivo.termo.aditivo", responsavel) + "." + FilenameUtils.getExtension(temps.getName())).equals(temps.getName()))) {
                    arquivoTermoAditivo = temps;
                }
            }
        }

        List<TransferObject> tiposMotivoOperacao = null;
        List<TransferObject> lstModelotermoAditivo = new ArrayList<>();

        try {
            tiposMotivoOperacao = tipoMotivoOperacaoController.lstMotivoOperacaoConsignataria(null, responsavel);
            List<AnexoCredenciamentoCsa> arquivoTermoAditivoPreenchidos  = new ArrayList<>();
            List<AnexoCredenciamentoCsa> arquivoTermoAditivoAssinado  = new ArrayList<>();
            List<AnexoCredenciamentoCsa> arquivoTermoAditivoAssinadoOrdenacao  = null;

            if(credenciamentoCsa.getScrCodigo().equals(StatusCredenciamentoEnum.AGUARDANDO_ASSINATURA_TERMO_ADITIVO_CSA.getCodigo())) {
                arquivoTermoAditivoAssinado = consignatariaController.lstAnexoCredenciamentoCsaTipoArquivo(creCodigo, credenciamentoTermoAditivoAssinadoCsa, responsavel);

                if((arquivoTermoAditivoAssinado != null) && !arquivoTermoAditivoAssinado.isEmpty()) {
                    arquivoTermoAditivoPreenchidos.add(arquivoTermoAditivoAssinado.get(0));
                } else {
                    arquivoTermoAditivoPreenchidos = consignatariaController.lstAnexoCredenciamentoCsaTipoArquivo(creCodigo, TipoArquivoEnum.ARQUIVO_ANEXO_CREDENCIAMENTO_TERMO_ADITIVO.getCodigo(), responsavel);
                }
            } else if (credenciamentoCsa.getScrCodigo().equals(StatusCredenciamentoEnum.AGUARDANDO_ASSINATURA_TERMO_ADITIVO_CSE.getCodigo())) {
                arquivoTermoAditivoAssinadoOrdenacao = consignatariaController.lstAnexoCredenciamentoCsaTipoArquivo(creCodigo, credenciamentoTermoAditivoAssinadoCse, responsavel);
                if((arquivoTermoAditivoAssinadoOrdenacao != null) && !arquivoTermoAditivoAssinadoOrdenacao.isEmpty()) {
                    arquivoTermoAditivoAssinado.add(arquivoTermoAditivoAssinadoOrdenacao.get(0));
                } else {
                    arquivoTermoAditivoAssinadoOrdenacao = consignatariaController.lstAnexoCredenciamentoCsaTipoArquivo(creCodigo, credenciamentoTermoAditivoAssinadoCsa, responsavel);
                    arquivoTermoAditivoAssinado.add(arquivoTermoAditivoAssinadoOrdenacao.get(0));
                }
            } else if (credenciamentoCsa.getScrCodigo().equals(StatusCredenciamentoEnum.AGUARDANDO_APROVACAO_TERMO_ADITIVO.getCodigo())) {
                arquivoTermoAditivoAssinadoOrdenacao = consignatariaController.lstAnexoCredenciamentoCsaTipoArquivo(creCodigo, credenciamentoTermoAditivoAssinadoCse, responsavel);
                arquivoTermoAditivoAssinado.add(arquivoTermoAditivoAssinadoOrdenacao.get(0));
            } else if (responsavel.isCseSup() && credenciamentoCsa.getScrCodigo().equals(StatusCredenciamentoEnum.AGUARDANDO_PREENCHIMENTO_TERMO_ADITIVO_CSE.getCodigo())) {
                lstModelotermoAditivo = consignatariaController.listaCodTituloModeloTermoAditivo(null, responsavel);
            }

            model.addAttribute("tiposMotivoOperacao", tiposMotivoOperacao);
            model.addAttribute("arquivoTermoAditivoPreenchidos", arquivoTermoAditivoPreenchidos);
            model.addAttribute("arquivoTermoAditivoAssinado", arquivoTermoAditivoAssinado);
        } catch (TipoMotivoOperacaoControllerException | ConsignatariaControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
        }


        model.addAttribute("arquivoListaDoc", arquivoListaDoc);
        model.addAttribute("arquivoTermoAditivo", arquivoTermoAditivo);
        model.addAttribute("credenciamentoCsa", credenciamentoCsa);
        model.addAttribute("consignataria", credenciamentoCsa.getConsignataria());
        model.addAttribute("lstOcorrencia", lstOcorrencia);
        model.addAttribute("lstAnexo", lstAnexo);
        model.addAttribute("permiteConcluirAssinatura", permiteConcluirAssinatura);
        model.addAttribute("lstModelotermoAditivo", lstModelotermoAditivo);

        return viewRedirect("jsp/visualizarDashboardCredenciamentoCsa/detalharCredenciamentoCsa", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=uploadArquivoCredenciamentoCsa" })
    public String uploadArquivoCredenciamentoCsa(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws IOException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        final String paramTamMaxArqAnexo = (String) ParamSist.getInstance().getParam(CodedValues.TPC_TAM_MAX_ARQ_ANEXO_CREDENCIAMENTO, responsavel);
        final int tamMaxArqAnexo = (!TextHelper.isNull(paramTamMaxArqAnexo) ? Integer.parseInt(paramTamMaxArqAnexo) : 200);
        final UploadHelper uploadHelper = new UploadHelper();

        try {
            uploadHelper.processarRequisicao(request.getServletContext(), request, tamMaxArqAnexo * 1024);
        } catch (final Throwable ex) {
            LOG.error(ex.getMessage(), ex);
            final String msg = ex.getMessage();
            if (!TextHelper.isNull(msg)) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, msg);
            }
        }

        if (uploadHelper.getValorCampoFormulario("FILE1") != null) {
            final String creCodigo = JspHelper.verificaVarQryStr(request, "creCodigo");
            final String csaCodigo = JspHelper.verificaVarQryStr(request, "csaCodigo");
            final String nomeAnexo = uploadHelper.getValorCampoFormulario("FILE1");

            final String diretorioRaizArquivos = ParamSist.getDiretorioRaizArquivos();
            final String diretorioTemporario = diretorioRaizArquivos  + File.separatorChar + "temp" + File.separatorChar + "upload" + File.separatorChar + "anexo"  + File.separatorChar + session.getId()  + File.separatorChar;

            final String pathCsa = diretorioRaizArquivos + File.separatorChar + "credenciamento" + File.separatorChar + "csa";
            final String pathCsaSubDir = pathCsa + File.separatorChar + csaCodigo;

            final File diretorioCsa = new File(pathCsa);
            if(!diretorioCsa.exists()) {
                diretorioCsa.mkdir();
            }

            final File diretorioCsaSubDir = new File(pathCsaSubDir);
            if(!diretorioCsaSubDir.exists()) {
                diretorioCsaSubDir.mkdir();
            }

            String ancCodigo = null;
            try {
                ancCodigo = consignatariaController.registrarAnexoCredenciamentoCsa(creCodigo, nomeAnexo, TipoArquivoEnum.ARQUIVO_ANEXO_CREDENCIAMENTO_DOC_CSA.getCodigo(), responsavel);
            } catch (final ConsignatariaControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_INTERNO, responsavel));
                LOG.error(ex.getMessage(), ex);
                return viewRedirect(JSP_VISUALIZAR_ERRO, request, session, model, responsavel);
            }
            final File anexo = null;
            final List<String> arquivos = new ArrayList<>();
            if (!TextHelper.isNull(nomeAnexo) && !TextHelper.isNull(ancCodigo)) {
                try {
                    String[] anexosName;
                    anexosName = nomeAnexo.split(";");
                    for (final String nomeAnexoCorrente : anexosName) {
                        final File arquivo = new File (diretorioTemporario + nomeAnexoCorrente);
                        final File arquivoDestino = new File (pathCsaSubDir+ File.separatorChar + nomeAnexoCorrente);
                        if(arquivo.exists()) {
                            FileHelper.copyFile(arquivo, arquivoDestino);
                        }
                        final String destino = pathCsaSubDir + File.separatorChar + nomeAnexoCorrente;
                        arquivos.add(destino);
                    }
                } catch (final Exception ex) {
                    Optional.ofNullable(anexo).ifPresent(anx -> {
                        try {
                            Files.delete(anx.toPath());
                        } catch (final IOException e) {
                            LOG.error(ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_UPLOAD_EDT_ANEXO_ADE, responsavel), e);
                        }
                    });
                }
            }

            try {
                // Cria o zip do arquivo
                if(!arquivos.isEmpty()) {
                    final String arquivoZip = pathCsaSubDir + File.separatorChar + ancCodigo + ".zip";
                    FileHelper.zip(arquivos, arquivoZip);

                    //altera status e envia email com o anexo para a consignante
                    final List<String> anexosEnviarCse = new ArrayList<>();
                    anexosEnviarCse.add(arquivoZip);
                    consignatariaController.alterarStatusNotificarCseCredenciamento(creCodigo, anexosEnviarCse, responsavel);

                    //Remove Arquivos
                    for(final String arquivo : arquivos) {
                        FileHelper.delete(arquivo);
                    }
                }

                // Gera log de upload de arquivo
                final LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.UPLOAD_FILE, Log.LOG_INFORMACAO);
                log.add(ApplicationResourcesHelper.getMessage("rotulo.compact.arquivo.log", responsavel) + ": " + pathCsaSubDir);
                log.write();
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("rotulo.compact.erro.interno", responsavel));
                return iniciar(request, response, session, model);
            }
        }
        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.credenciamento.arquivo.enviado.csa", responsavel));
        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=aprovarCredenciamentoCsa" })
    public String aprovarCredenciamentoCsa(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final String creCodigo = JspHelper.verificaVarQryStr(request, "creCodigo");

        if(TextHelper.isNull(creCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_INTERNO, responsavel));
            return viewRedirect(JSP_VISUALIZAR_ERRO, request, session, model, responsavel);
        }
        try {
            consignatariaController.aprovarCredenciamentoCsa(creCodigo, responsavel);
        } catch (final ConsignatariaControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("rotulo.compact.erro.interno", responsavel));
            return viewRedirect(JSP_VISUALIZAR_ERRO, request, session, model, responsavel);
        }
        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.credenciamento.aprovacao.documentacao.sucesso", responsavel));
        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=reprovarCredenciamentoCsa" })
    public String reprovarCredenciamentoCsa(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final String creCodigo = JspHelper.verificaVarQryStr(request, "creCodigo");
        final String reprovarAssTermo = JspHelper.verificaVarQryStr(request, "reprovarAssTermo");
        final String tmoCodigo = JspHelper.verificaVarQryStr(request, "tmoCodigo");
        final String tmoObs = JspHelper.verificaVarQryStr(request, "OCC_OBS");

        if(TextHelper.isNull(creCodigo) || TextHelper.isNull(tmoCodigo) || TextHelper.isNull(tmoObs)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_INTERNO, responsavel));
            return viewRedirect(JSP_VISUALIZAR_ERRO, request, session, model, responsavel);
        }
        try {
            consignatariaController.reprovarCredenciamentoCsa(creCodigo, tmoCodigo, tmoObs, !TextHelper.isNull(reprovarAssTermo), responsavel);
        } catch (final ConsignatariaControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("rotulo.compact.erro.interno", responsavel));
            return viewRedirect(JSP_VISUALIZAR_ERRO, request, session, model, responsavel);
        }
        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.credenciamento.reprovacao.documentacao.sucesso", responsavel));
        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=preencherTermoCredenciamentoCsa" })
    public String preencherTermoCredenciamentoCsa(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        final String creCodigo = JspHelper.verificaVarQryStr(request, "creCodigo");
        final String csaCodigo = JspHelper.verificaVarQryStr(request, "csaCodigo");
        final String mtaCodigo = JspHelper.verificaVarQryStr(request, "mtaCodigo");

        if (TextHelper.isNull(mtaCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("rotulo.dashboard.titulo.termo.aditivo.nao.escolhido", responsavel));
            return iniciar(request, response, session, model);
        }

        try {
            final List<ModeloTermoTag> modeloTermoTags = consignatariaController.listaTagsModeloTermoAditivo(mtaCodigo, responsavel);
            final List<TransferObject> lstModeloTermoAditivo = consignatariaController.listaCodTituloModeloTermoAditivo(mtaCodigo, responsavel);

            if ((modeloTermoTags == null) || modeloTermoTags.isEmpty()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("rotulo.dashboard.titulo.table.modal.titulo.termo.tag.nao.encontrado", responsavel));
                return iniciar(request, response, session, model);
            }

            model.addAttribute("csaCodigo", csaCodigo);
            model.addAttribute("creCodigo", creCodigo);
            model.addAttribute("modeloTermoTags", modeloTermoTags);
            model.addAttribute("modeloTermoAditivo", lstModeloTermoAditivo.get(0));

            return viewRedirect("jsp/visualizarDashboardCredenciamentoCsa/preencherTermoAditivoCredenciamentoCsa", request, session, model, responsavel);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("rotulo.compact.erro.interno", responsavel));
            return iniciar(request, response, session, model);
        }
    }

    @RequestMapping(params = { "acao=assinarTermoCredenciamentoCsa" })
    public String assinarTermoCredenciamentoCsa(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        final String creCodigo = JspHelper.verificaVarQryStr(request, "creCodigo");
        final List<String> infoArquivo = salvarArquivoConsignataria(request, responsavel, session);
        if(infoArquivo.isEmpty()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_INTERNO, responsavel));
            return viewRedirect(JSP_VISUALIZAR_ERRO, request, session, model, responsavel);
        }
        final String nomeArquivo = infoArquivo.get(0);
        final String destino = infoArquivo.get(1);

            try {
                // Regista anexo
                consignatariaController.registrarAnexoCredenciamentoCsa(creCodigo, nomeArquivo, TipoArquivoEnum.ARQUIVO_ANEXO_CREDENCIAMENTO_TERMO_ADITIVO_ASSINADO.getCodigo(), responsavel);

                // Gera log de upload de arquivo
                final LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.UPLOAD_FILE, Log.LOG_INFORMACAO);
                log.add(ApplicationResourcesHelper.getMessage("rotulo.compact.arquivo.log", responsavel) + ": " + destino);
                log.write();
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return iniciar(request, response, session, model);
            }
        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.sucesso.upload.credenciamento.csa.upload.termo.csa", responsavel));
        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=assinarTermoCseCredenciamentoCsa" })
    public String assinarTermoCseCredenciamentoCsa(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        final String creCodigo = JspHelper.verificaVarQryStr(request, "creCodigo");
        final List<String> infoArquivo = salvarArquivoConsignataria(request, responsavel, session);
        if(infoArquivo.isEmpty()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_INTERNO, responsavel));
            return viewRedirect(JSP_VISUALIZAR_ERRO, request, session, model, responsavel);
        }
        final String nomeArquivo = infoArquivo.get(0);
        final String destino = infoArquivo.get(1);

            try {
                // Regista anexo
                consignatariaController.registrarAnexoCredenciamentoCsa(creCodigo, nomeArquivo, TipoArquivoEnum.ARQUIVO_ANEXO_CREDENCIAMENTO_TERMO_ADITIVO_ASSINADO_CSE.getCodigo(), responsavel);

                // Gera log de upload de arquivo
                final LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.UPLOAD_FILE, Log.LOG_INFORMACAO);
                log.add(ApplicationResourcesHelper.getMessage("rotulo.compact.arquivo.log", responsavel) + ": " + destino);
                log.write();
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return iniciar(request, response, session, model);
            }
        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.sucesso.upload.credenciamento.csa.upload.termo.csa", responsavel));
        return iniciar(request, response, session, model);
    }


    @RequestMapping(params = { "acao=finalizarCredenciamentoCsa" })
    public String finalizarCredenciamentoCsa(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        final String creCodigo = JspHelper.verificaVarQryStr(request, "creCodigo");
        final List<String> infoArquivo = salvarArquivoConsignataria(request, responsavel, session);
        if(infoArquivo.isEmpty()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_INTERNO, responsavel));
            return viewRedirect(JSP_VISUALIZAR_ERRO, request, session, model, responsavel);
        }
        final String nomeArquivo = infoArquivo.get(0);
        final String destino = infoArquivo.get(1);

            try {
                // Regista anexo
                consignatariaController.registrarAnexoCredenciamentoCsa(creCodigo, nomeArquivo, TipoArquivoEnum.ARQUIVO_ANEXO_CREDENCIAMENTO_TERMO_ADITIVO_ASSINADO_CSE.getCodigo(), responsavel);

                // Gera log de upload de arquivo
                final LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.UPLOAD_FILE, Log.LOG_INFORMACAO);
                log.add(ApplicationResourcesHelper.getMessage("rotulo.compact.arquivo.log", responsavel) + ": " + destino);
                log.write();
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return iniciar(request, response, session, model);
            }
        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.sucesso.upload.credenciamento.csa.upload.termo.csa", responsavel));
        return iniciar(request, response, session, model);
    }

    private List<String> salvarArquivoConsignataria(HttpServletRequest request, AcessoSistema responsavel, HttpSession session){

        final List<String> infoArquivos = new ArrayList<>();
        final String creCodigo = JspHelper.verificaVarQryStr(request, "creCodigo");
        final String csaCodigo = JspHelper.verificaVarQryStr(request, "csaCodigo");
        final String diretorioRaizArquivos = ParamSist.getDiretorioRaizArquivos();
        final String pathCsa = diretorioRaizArquivos + File.separatorChar + "credenciamento" + File.separatorChar + "csa" + File.separatorChar + csaCodigo;
        final String paramTamMaxArqAnexo = (String) ParamSist.getInstance().getParam(CodedValues.TPC_TAM_MAX_ARQ_ANEXO_CREDENCIAMENTO, responsavel);
        final int tamMaxArqAnexo = (!TextHelper.isNull(paramTamMaxArqAnexo) ? Integer.parseInt(paramTamMaxArqAnexo) : 200);
        final UploadHelper uploadHelper = new UploadHelper();

        try {
            uploadHelper.processarRequisicao(request.getServletContext(), request, tamMaxArqAnexo * 1024);

            final CredenciamentoCsa credenciamentoCsa = consignatariaController.findByCreCodigoCredenciamentoCsa(creCodigo, responsavel);
            if (credenciamentoCsa != null) {
                List<AnexoCredenciamentoCsa> lstAnexoCredenciamentoCsas = null;
                String destino = "";
                if (credenciamentoCsa.getStatusCredenciamento().getScrCodigo().equals(StatusCredenciamentoEnum.AGUARDANDO_ASSINATURA_TERMO_ADITIVO_CSA.getCodigo())) {
                    lstAnexoCredenciamentoCsas = consignatariaController.lstAnexoCredenciamentoCsaTipoArquivo(creCodigo, TipoArquivoEnum.ARQUIVO_ANEXO_CREDENCIAMENTO_TERMO_ADITIVO_ASSINADO.getCodigo(), responsavel);
                    if((lstAnexoCredenciamentoCsas == null) || lstAnexoCredenciamentoCsas.isEmpty()) {
                        lstAnexoCredenciamentoCsas = consignatariaController.lstAnexoCredenciamentoCsaTipoArquivo(creCodigo, TipoArquivoEnum.ARQUIVO_ANEXO_CREDENCIAMENTO_TERMO_ADITIVO.getCodigo(), responsavel);
                    }
                    destino = pathCsa + File.separatorChar + lstAnexoCredenciamentoCsas.get(0).getAncNome();
                } else if ((credenciamentoCsa.getStatusCredenciamento().getScrCodigo().equals(StatusCredenciamentoEnum.AGUARDANDO_ASSINATURA_TERMO_ADITIVO_CSE.getCodigo()) || credenciamentoCsa.getStatusCredenciamento().getScrCodigo().equals(StatusCredenciamentoEnum.AGUARDANDO_APROVACAO_TERMO_ADITIVO.getCodigo()))) {
                    lstAnexoCredenciamentoCsas = consignatariaController.lstAnexoCredenciamentoCsaTipoArquivo(creCodigo, TipoArquivoEnum.ARQUIVO_ANEXO_CREDENCIAMENTO_TERMO_ADITIVO_ASSINADO_CSE.getCodigo(), responsavel);
                    if((lstAnexoCredenciamentoCsas == null) || lstAnexoCredenciamentoCsas.isEmpty()) {
                        lstAnexoCredenciamentoCsas = consignatariaController.lstAnexoCredenciamentoCsaTipoArquivo(creCodigo, TipoArquivoEnum.ARQUIVO_ANEXO_CREDENCIAMENTO_TERMO_ADITIVO_ASSINADO.getCodigo(), responsavel);
                    }
                    destino = pathCsa + File.separatorChar + lstAnexoCredenciamentoCsas.get(0).getAncNome();
                }
                if (!TextHelper.isNull(destino)) {
                    FileHelper.delete(destino);
                }
            }
        } catch (final Throwable ex) {
            LOG.error(ex.getMessage(), ex);
            final String msg = ex.getMessage();
            if (!TextHelper.isNull(msg)) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, msg);
            }
        }

        if (uploadHelper.getValorCampoFormulario("FILE1") != null) {
            final String nomeAnexo = uploadHelper.getValorCampoFormulario("FILE1");
            final String diretorioTemporario = diretorioRaizArquivos  + File.separatorChar + "temp" + File.separatorChar + "upload" + File.separatorChar + "anexo"  + File.separatorChar + session.getId()  + File.separatorChar;

            final File anexo = null;
            String destino = null;
            String nomeArquivo = null;
            if (!TextHelper.isNull(nomeAnexo)) {
                nomeArquivo = FilenameUtils.getBaseName(nomeAnexo) + "_" + DateHelper.format(DateHelper.getSystemDatetime(), "yyyy-MM-DD_HH-mm-ss") + "." + FilenameUtils.getExtension(nomeAnexo);
                try {
                    final File arquivo = new File (diretorioTemporario + nomeAnexo);
                    destino = pathCsa+ File.separatorChar + nomeArquivo;
                    final File arquivoDestino = new File (destino);
                    if(arquivo.exists()) {
                        FileHelper.copyFile(arquivo, arquivoDestino);
                    }
                } catch (final Exception ex) {
                    Optional.ofNullable(anexo).ifPresent(anx -> {
                        try {
                            Files.delete(anx.toPath());
                        } catch (final IOException e) {
                            LOG.error(ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_UPLOAD_EDT_ANEXO_ADE, responsavel), e);
                        }
                    });
                }
            }

            infoArquivos.add(nomeArquivo);
            infoArquivos.add(destino);
        }

        return infoArquivos;
    }

    @RequestMapping(params = { "acao=assinarTermoCertificado" })
    public String assinarTermoCertificado(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        final String pdfAssinado = JspHelper.verificaVarQryStr(request, "pdfAssinado");
        final String creCodigo = JspHelper.verificaVarQryStr(request, "creCodigo");

        if(!TextHelper.isNull(pdfAssinado)) {
            final String nomeArquivo = JspHelper.verificaVarQryStr(request, "nomeArquivo");
            final String csaCodigo = JspHelper.verificaVarQryStr(request, "csaCodigo");

            final String absolutePath = ParamSist.getDiretorioRaizArquivos();
            final String arquivo = absolutePath + File.separatorChar + "credenciamento" + File.separatorChar + "csa" + File.separatorChar + csaCodigo + File.separatorChar + nomeArquivo;

            try {
                FileHelper.delete(arquivo);
                final byte[] fileBytes = Base64.decodeBase64(pdfAssinado);
                FileHelper.saveByteArrayToFile(fileBytes, arquivo);

                final CredenciamentoCsa credenciamentoCsa = consignatariaController.findByCreCodigoCredenciamentoCsa(creCodigo, responsavel);
                if (credenciamentoCsa.getStatusCredenciamento().getScrCodigo().equals(StatusCredenciamentoEnum.AGUARDANDO_ASSINATURA_TERMO_ADITIVO_CSA.getCodigo())) {
                    consignatariaController.registrarAnexoCredenciamentoCsa(creCodigo, nomeArquivo, TipoArquivoEnum.ARQUIVO_ANEXO_CREDENCIAMENTO_TERMO_ADITIVO_ASSINADO.getCodigo(), responsavel);
                } else if (credenciamentoCsa.getStatusCredenciamento().getScrCodigo().equals(StatusCredenciamentoEnum.AGUARDANDO_ASSINATURA_TERMO_ADITIVO_CSE.getCodigo()) || credenciamentoCsa.getStatusCredenciamento().getScrCodigo().equals(StatusCredenciamentoEnum.AGUARDANDO_APROVACAO_TERMO_ADITIVO.getCodigo())) {
                    consignatariaController.registrarAnexoCredenciamentoCsa(creCodigo, nomeArquivo, TipoArquivoEnum.ARQUIVO_ANEXO_CREDENCIAMENTO_TERMO_ADITIVO_ASSINADO_CSE.getCodigo(), responsavel);
                }

                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.sucesso.termo.aditivo.csa.assinado.certificado.digital", responsavel));
                return iniciar(request, response, session, model);
            } catch (final IOException | ConsignatariaControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_INTERNO, responsavel));
            }
        }

        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.termo.aditivo.csa.assinado.certificado.digital", responsavel));
        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=finalizarAssinaturaTermo" })
    public String finalizarAssinaturaTermo(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String mensagem = "";
        final String csaCodigo = JspHelper.verificaVarQryStr(request, "csaCodigo");
        final String creCodigo = JspHelper.verificaVarQryStr(request, "creCodigo");
        final String desbloquearCsa = JspHelper.verificaVarQryStr(request, "desbloquearCsa");
        final String diretorioRaizArquivos = ParamSist.getDiretorioRaizArquivos();
        final String pathCsa = diretorioRaizArquivos + File.separatorChar + "credenciamento" + File.separatorChar + "csa" + File.separatorChar + csaCodigo;
        String destino = "";
        try {
            final CredenciamentoCsa credenciamentoCsa = consignatariaController.findByCreCodigoCredenciamentoCsa(creCodigo, responsavel);
            List<AnexoCredenciamentoCsa> lstAnexoCredenciamentoCsas = null;
            if (credenciamentoCsa == null) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.finalizar.credenciamento.csa", responsavel));
                return iniciar(request, response, session, model);
            }
            if (credenciamentoCsa.getStatusCredenciamento().getScrCodigo().equals(StatusCredenciamentoEnum.AGUARDANDO_ASSINATURA_TERMO_ADITIVO_CSA.getCodigo())) {
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.credenciamento.arquivo.termo.assinado.sucesso", responsavel);
                lstAnexoCredenciamentoCsas = consignatariaController.lstAnexoCredenciamentoCsaTipoArquivo(creCodigo, TipoArquivoEnum.ARQUIVO_ANEXO_CREDENCIAMENTO_TERMO_ADITIVO_ASSINADO.getCodigo(), responsavel);
                destino = pathCsa + File.separatorChar + lstAnexoCredenciamentoCsas.get(0).getAncNome();
                consignatariaController.assinarTermoAditivoCredenciamentoCsa(creCodigo, destino, responsavel);
            } else if (credenciamentoCsa.getStatusCredenciamento().getScrCodigo().equals(StatusCredenciamentoEnum.AGUARDANDO_ASSINATURA_TERMO_ADITIVO_CSE.getCodigo())) {
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.sucesso", responsavel);
                lstAnexoCredenciamentoCsas = consignatariaController.lstAnexoCredenciamentoCsaTipoArquivo(creCodigo, TipoArquivoEnum.ARQUIVO_ANEXO_CREDENCIAMENTO_TERMO_ADITIVO_ASSINADO_CSE.getCodigo(), responsavel);
                destino = pathCsa + File.separatorChar + lstAnexoCredenciamentoCsas.get(0).getAncNome();
                consignatariaController.assinarTermoAditivoCseCredenciamentoCsa(creCodigo, destino, responsavel);
            } else if (credenciamentoCsa.getStatusCredenciamento().getScrCodigo().equals(StatusCredenciamentoEnum.AGUARDANDO_APROVACAO_TERMO_ADITIVO.getCodigo())) {
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.credenciamento.concluido.sucesso", responsavel);
                lstAnexoCredenciamentoCsas = consignatariaController.lstAnexoCredenciamentoCsaTipoArquivo(creCodigo, TipoArquivoEnum.ARQUIVO_ANEXO_CREDENCIAMENTO_TERMO_ADITIVO_ASSINADO_CSE.getCodigo(), responsavel);
                destino = pathCsa + File.separatorChar + lstAnexoCredenciamentoCsas.get(0).getAncNome();
                consignatariaController.finalizarCredenciamentoCsa(creCodigo, destino, !TextHelper.isNull(desbloquearCsa) && "true".equals(desbloquearCsa), responsavel);
            }

            session.setAttribute(CodedValues.MSG_INFO, mensagem);
            return iniciar(request, response, session, model);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return iniciar(request, response, session, model);
        }
    }

    @RequestMapping(params = { "acao=preVisualizarTermoCredenciamentoCsa" })
    public String preVisualizarTermoCredenciamentoCsa(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model){
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        final String creCodigo = JspHelper.verificaVarQryStr(request, "creCodigo");
        final String csaCodigo = JspHelper.verificaVarQryStr(request, "csaCodigo");
        final String mtaCodigo = JspHelper.verificaVarQryStr(request, "mtaCodigo");
        final String visualizar = JspHelper.verificaVarQryStr(request, "visualizar");

        try {
            final List<ModeloTermoTag> modeloTermoTags = consignatariaController.listaTagsModeloTermoAditivo(mtaCodigo, responsavel);
            final ModeloTermoAditivo modeloTermoAditivo = consignatariaController.findModeloTermoAditivo(mtaCodigo, responsavel);

            if (TextHelper.isNull(visualizar) || "false".equals(visualizar)) {
                for (final ModeloTermoTag modeloTermoTag : modeloTermoTags) {
                    final String valorTag = JspHelper.verificaVarQryStr(request, "tag_" + modeloTermoTag.getMttCodigo());
                    if (!TextHelper.isNull(valorTag)) {
                        modeloTermoAditivo.setMtaTexto(modeloTermoAditivo.getMtaTexto().replace(modeloTermoTag.getMttTag(), valorTag));
                    } else {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("rotulo.dashboard.titulo.termo.aditivo.tag.campo.obrigatorio", responsavel, modeloTermoTag.getMttValor()));
                        return iniciar(request, response, session, model);
                    }
                }
            }

            model.addAttribute("csaCodigo", csaCodigo);
            model.addAttribute("creCodigo", creCodigo);
            model.addAttribute("modeloTermoAditivo", modeloTermoAditivo);
            model.addAttribute("visualizar", visualizar);

            return viewRedirect("jsp/visualizarDashboardCredenciamentoCsa/preVisualizarTermoAditivoCredenciamentoCsa", request, session, model, responsavel);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("rotulo.compact.erro.interno", responsavel));
            return iniciar(request, response, session, model);
        }
    }

    @RequestMapping(params = { "acao=confirmarTermoCredenciamentoCsa" }, method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public ResponseEntity<?> confirmarTermoCredenciamentoCsa(@RequestBody(required = true) Map<String,Object> corpo, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        final String creCodigo = String.valueOf(corpo.get("creCodigo"));
        final String csaCodigo = String.valueOf(corpo.get("csaCodigo"));
        final String mtaDescricao = String.valueOf(corpo.get("mtaDescricao"));
        final String termoPreenchido = String.valueOf(corpo.get("termoPreenchido"));

        final String nomeArquivo = mtaDescricao + ".pdf";
        final String diretorioRaizArquivos = ParamSist.getDiretorioRaizArquivos();
        final String destino = diretorioRaizArquivos + File.separatorChar + "credenciamento" + File.separatorChar + "csa" + File.separatorChar + csaCodigo;

        final Document document = new Document();
        document.setPageSize(PageSize.A4.rotate());

        final String arquivoTermoAditivo = destino + File.separatorChar + nomeArquivo;

        final File diretorioDestino = new File (destino);
        if (!diretorioDestino.exists()){
            diretorioDestino.mkdirs();
        }

        OutputStream file = null;
        PdfWriter writer = null;
        try {
            file = new FileOutputStream(new File(arquivoTermoAditivo));
            final Paragraph paragrafo = new Paragraph(" ");

            writer = PdfWriter.getInstance(document, file);
            document.open();
            document.add(paragrafo);
            PDFHelper.addHTMLToPDF(document, termoPreenchido);
        } catch (final FileNotFoundException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.arquivo.nao.encontrado", responsavel));
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (DocumentException | IOException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.sistema.motivo.arg0", responsavel, ex.getMessage()));
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        } finally {
            document.close();

            if (writer != null) {
                writer.flush();
                writer.close();
            }
            if (file != null) {
                try {
                    file.close();
                } catch (final IOException ex) {
                    LOG.error(ex.getMessage(), ex);
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.sistema.motivo.arg0", responsavel, ex.getMessage()));
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }

        try {
            // Regista anexo
            consignatariaController.registrarAnexoCredenciamentoCsa(creCodigo, nomeArquivo, TipoArquivoEnum.ARQUIVO_ANEXO_CREDENCIAMENTO_TERMO_ADITIVO.getCodigo(), responsavel);

            //Notificar csa
            consignatariaController.preencherTermoCredenciamentoCsa(creCodigo, destino, responsavel);

            // Gera log de upload de arquivo
            final LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.UPLOAD_FILE, Log.LOG_INFORMACAO);
            log.add(ApplicationResourcesHelper.getMessage("rotulo.compact.arquivo.log", responsavel) + ": " + destino);
            log.write();
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.credenciamento.arquivo.termo.preenchido.sucesso", responsavel));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}