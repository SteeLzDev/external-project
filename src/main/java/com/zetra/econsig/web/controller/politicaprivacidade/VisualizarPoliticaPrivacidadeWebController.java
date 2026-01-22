package com.zetra.econsig.web.controller.politicaprivacidade;

import java.io.File;
import java.util.ArrayList;
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
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.dto.entidade.EstabelecimentoTransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: VisualizarPoliticaPrivacidadeWebController</p>
 * <p>Description: Controlador Web para o caso de uso Visualizar Política Privacidade.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author: ricardo.magno $
 * $Date:  $
 */
@Controller
public class VisualizarPoliticaPrivacidadeWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(VisualizarPoliticaPrivacidadeWebController.class);

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private UsuarioController usuarioController;

    @RequestMapping(value = { "/v3/visualizarPoliticaPrivacidade" }, method = { RequestMethod.GET, RequestMethod.POST }, params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws HQueryException, ConsignanteControllerException, ViewHelperException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        SynchronizerToken.saveToken(request);

        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.politica.privacidade.titulo", responsavel));

        File arqPoliticaPrivacidade = null;
        String absolutePath = ParamSist.getDiretorioRaizArquivos() + File.separatorChar + "politica_privacidade";
        String nomeArquivo = "cse.msg";
        if (responsavel.isOrg()) {
            nomeArquivo = "org.msg";
        } else if (responsavel.isSer()) {
            nomeArquivo = "ser.msg";
        } else if (responsavel.isCsa()) {
            nomeArquivo = "csa.msg";
        } else if (responsavel.isCor()) {
            nomeArquivo = "cor.msg";
        } else if (responsavel.isSup()) {
            nomeArquivo = "sup.msg";
        }
        arqPoliticaPrivacidade = new File(absolutePath, nomeArquivo);
        if (arqPoliticaPrivacidade == null || !arqPoliticaPrivacidade.exists()) {
            nomeArquivo = "geral.msg";
            arqPoliticaPrivacidade = new File(absolutePath, nomeArquivo);
            if (!arqPoliticaPrivacidade.exists()) {
                arqPoliticaPrivacidade = null;
            }
        }

        if (arqPoliticaPrivacidade == null) {
            try {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.politica.privacidade.nao.encontrado", responsavel));
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        boolean aceiteValido = false;
        java.util.Date dataUltimaAceitacao = null;
        java.util.Date dataPoliticaPrivacidade = null;
        String chaveAceitacaoPoliticaPrivacidade = CodedValues.TPC_DATA_POLITICA_PRIVACIDADE_CSE;
        if (responsavel.isOrg()) {
            chaveAceitacaoPoliticaPrivacidade = CodedValues.TPC_DATA_POLITICA_PRIVACIDADE_ORG;
        } else if (responsavel.isSer()) {
            chaveAceitacaoPoliticaPrivacidade = CodedValues.TPC_DATA_POLITICA_PRIVACIDADE_SER;
        } else if (responsavel.isCsa()) {
            chaveAceitacaoPoliticaPrivacidade = CodedValues.TPC_DATA_POLITICA_PRIVACIDADE_CSA;
        } else if (responsavel.isCor()) {
            chaveAceitacaoPoliticaPrivacidade = CodedValues.TPC_DATA_POLITICA_PRIVACIDADE_COR;
        } else if (responsavel.isSup()) {
            chaveAceitacaoPoliticaPrivacidade = CodedValues.TPC_DATA_POLITICA_PRIVACIDADE_SUP;
        }
        Object paramAceitacaoPoliticaPrivacidade = ParamSist.getInstance().getParam(chaveAceitacaoPoliticaPrivacidade, responsavel);
        if (!TextHelper.isNull(paramAceitacaoPoliticaPrivacidade)) {
            try {
                dataPoliticaPrivacidade = DateHelper.parse(paramAceitacaoPoliticaPrivacidade.toString(), "yyyy-MM-dd");
                List<String> tocCodigos = new ArrayList<>();
                tocCodigos.add(CodedValues.TOC_ACEITACAO_POLITICA_PRIVACIDADE_SISTEMA);

                CustomTransferObject filtro = new CustomTransferObject();
                filtro.setAttribute("tocCodigos", tocCodigos);
                filtro.setAttribute(Columns.OUS_USU_CODIGO, responsavel.getUsuCodigo());

                List<TransferObject> ocorrencias = usuarioController.lstOcorrenciaUsuario(filtro, -1, -1, responsavel);
                if (ocorrencias.size() > 0) {
                    dataUltimaAceitacao = (java.util.Date) ocorrencias.get(0).getAttribute(Columns.OUS_DATA);
                    if (dataUltimaAceitacao.compareTo(dataPoliticaPrivacidade) > 0) {
                        aceiteValido = true;
                    }
                }
            } catch (UsuarioControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            } catch (java.text.ParseException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        ConsignanteTransferObject cse = null;
        OrgaoTransferObject org = null;
        EstabelecimentoTransferObject est = null;

        if (responsavel.isSer() || responsavel.isOrg()){
            cse =  consignanteController.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
            est =  consignanteController.findEstabelecimento(responsavel.getEstCodigo(), responsavel);
            org =  consignanteController.findOrgao(responsavel.getOrgCodigo(), responsavel);
        } else {
            cse =  consignanteController.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
        }

        String msg = FileHelper.readAll(arqPoliticaPrivacidade.getAbsolutePath());
        msg = FileHelper.substituirDados(msg, cse, org, est);
        msg = msg.replaceAll("\n", "<br>");

        model.addAttribute("msg", msg);
        model.addAttribute("dataPoliticaPrivacidade", dataPoliticaPrivacidade);
        model.addAttribute("dataUltimaAceitacao", dataUltimaAceitacao);
        model.addAttribute("aceiteValido", aceiteValido);

        try {
            model.addAttribute("dataUltimaAtualizacaoSistema", consignanteController.dataUltimaAtualizacaoSistema());
        } catch (ConsignanteControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/visualizarPoliticaPrivacidade/visualizarPoliticaPrivacidade", request, session, model, responsavel);
    }

    @RequestMapping(value = { "/v3/aceitarPoliticaPrivacidade" }, method = { RequestMethod.POST }, params = { "acao=aceitar" })
    public String aceitar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws HQueryException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            // Cria ocorrência de inclusão de usuário
            CustomTransferObject ocorrencia = new CustomTransferObject();
            ocorrencia.setAttribute(Columns.OUS_USU_CODIGO, responsavel.getUsuCodigo());
            ocorrencia.setAttribute(Columns.OUS_TOC_CODIGO, CodedValues.TOC_ACEITACAO_POLITICA_PRIVACIDADE_SISTEMA);
            ocorrencia.setAttribute(Columns.OUS_OUS_USU_CODIGO, responsavel.getUsuCodigo());
            ocorrencia.setAttribute(Columns.OUS_OBS, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.aceitacao.politica.privacidade", responsavel));
            ocorrencia.setAttribute(Columns.OUS_IP_ACESSO, responsavel.getIpUsuario());

            usuarioController.createOcorrenciaUsuario(ocorrencia, responsavel);

            session.removeAttribute("AceitarPoliticaPrivacidade");

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.politica.privacidade.aceito.com.sucesso", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);

        } catch (UsuarioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }
}
