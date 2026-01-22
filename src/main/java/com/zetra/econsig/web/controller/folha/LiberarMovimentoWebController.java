package com.zetra.econsig.web.controller.folha;

import java.io.File;
import java.io.IOException;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ResultadoValidacaoMovimentoTO;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.HistoricoArquivoControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ValidacaoMovimentoControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.arquivo.HistoricoArquivoController;
import com.zetra.econsig.service.folha.ValidacaoMovimentoController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: LiberarMovimentoWebController</p>
 * <p>Description: Controlador Web para o caso de uso de liberar movimento financeiro.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 */
@Controller
public class LiberarMovimentoWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(LiberarMovimentoWebController.class);

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private HistoricoArquivoController historicoArquivoController;

    @Autowired
    private ValidacaoMovimentoController validacaoMovimentoController;

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        super.configurarPagina(request, session, model, responsavel);
        // Adiciona ao model as informações específicas da operação
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.folha.liberacao.arquivo.movimento.titulo", responsavel));
    }

    @RequestMapping(method = { RequestMethod.POST }, value = { "/v3/liberarMovimento" }, params = { "acao=liberarMovimento" })
    public String liberarMovimento(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        ParamSession paramSession = ParamSession.getParamSession(session);

        if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "arquivo_nome")) && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String nomeArquivo = JspHelper.verificaVarQryStr(request, "arquivo_nome");

        boolean temResultadoValidacao = false;
        String tipo = JspHelper.verificaVarQryStr(request, "tipo");
        ResultadoValidacaoMovimentoTO resultadoValidacaoMovimentoTO = null;
        UsuarioTransferObject usuarioTransferObject = null;
        List<TransferObject> resultadoRegras = null;
        boolean liberar = JspHelper.verificaVarQryStr(request, "liberouMovimento").equals("true");

        // Busca resultado de validação de movimento para o arquivo
        // que o usuário está tentando fazer download
        String caminhoArquivo = null;
        if (tipo.equals("movimento")) {
            try {
                String diretorioRaiz = ParamSist.getDiretorioRaizArquivos();
                caminhoArquivo = diretorioRaiz + File.separatorChar + "movimento" + File.separatorChar + "cse" + File.separatorChar + nomeArquivo;
                File arquivoMovimento = new File(caminhoArquivo);

                resultadoValidacaoMovimentoTO = validacaoMovimentoController.findResultadoValidacaoMovimentoByNomeArquivo(arquivoMovimento.getCanonicalPath(), responsavel);
                if (resultadoValidacaoMovimentoTO != null) {
                    // Busca o resultado das regras para exibição ao usuário
                    resultadoRegras = validacaoMovimentoController.selectResultadoRegras(resultadoValidacaoMovimentoTO.getRvaCodigo(), responsavel);
                    // Se tem o código do usuário que aceitou o movimento, busca os dados do usuário
                    if (resultadoValidacaoMovimentoTO.getUsuCodigo() != null) {
                        usuarioTransferObject = usuarioController.findUsuario(resultadoValidacaoMovimentoTO.getUsuCodigo(), responsavel);
                    }

                    temResultadoValidacao = true;
                }
            } catch (ValidacaoMovimentoControllerException | UsuarioControllerException | IOException ex) {
                LOG.error(ex.getMessage());
            }
        }
        model.addAttribute("_skip_history_", Boolean.TRUE);

        if (!temResultadoValidacao || ParamSist.getInstance().getParam(CodedValues.TPC_PULA_VALIDACAO_ARQUIVO_INTEGRACAO, responsavel).equals(CodedValues.TPC_SIM)) {
            // Se não é arquivo de movimento, ou é movimento mas não tem resultado
            // de validação, então redireciona para a página de download
            paramSession.halfBack();

            // Se for download de arquivo de movimento financeiro, insere histórico de download de arquivo de movimento financeiro
            if (tipo.equals("movimento")) {
                try {
                    String harResultado = CodedValues.STS_ATIVO.toString();
                    String harObs = ApplicationResourcesHelper.getMessage("mensagem.info.download.movimento.financeiro.sucesso", responsavel);
                    historicoArquivoController.createHistoricoArquivo(null, null, TipoArquivoEnum.ARQUIVO_MOVIMENTO_FINANCEIRO_DOWNLOAD, caminhoArquivo, harObs, null, null, harResultado, responsavel.getFunCodigo(), responsavel);
                } catch (HistoricoArquivoControllerException e) {
                    LOG.error("Não foi possível inserir o histórico do arquivo de exportação do movimento financeiro '" + nomeArquivo + "'.", e);
                }
            }

            return "forward:/v3/downloadArquivo";
        } else {
            if (resultadoValidacaoMovimentoTO.getRvaResultado().equals(CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_OK)) {
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("rotulo.folha.validacao.ok", responsavel));
            } else if (resultadoValidacaoMovimentoTO.getRvaResultado().equals(CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_ERRO)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("rotulo.folha.validacao.erro", responsavel));
            } else if (resultadoValidacaoMovimentoTO.getRvaResultado().equals(CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_AVISO)) {
                session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("rotulo.folha.validacao.atencao", responsavel));
            }

            SynchronizerToken.saveToken(request);

            model.addAttribute("resultadoValidacaoMovimentoTO", resultadoValidacaoMovimentoTO);
            model.addAttribute("nomeArquivo", nomeArquivo);
            model.addAttribute("usuarioTransferObject", usuarioTransferObject);
            model.addAttribute("resultadoRegras", resultadoRegras);
            model.addAttribute("liberouMovimento", liberar);

            if (liberar) {
                try {
                    resultadoValidacaoMovimentoTO.setRvaDataAceite(DateHelper.getSystemDatetime());
                    resultadoValidacaoMovimentoTO.setUsuCodigo(responsavel.getUsuCodigo());
                    validacaoMovimentoController.updateResultadoValidacaoMovimento(resultadoValidacaoMovimentoTO, responsavel);
                } catch (ValidacaoMovimentoControllerException ex) {
                    LOG.error(ex.getMessage());
                }
            }

            return viewRedirect("jsp/liberarMovimento/liberarMovimento", request, session, model, responsavel);
        }

    }

}
