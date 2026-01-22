package com.zetra.econsig.web.controller.consignacao;

import java.io.File;
import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.SaldoDevedorControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.texto.TransferObjectHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.saldodevedor.SaldoDevedorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: AlongarConsignacaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso Anexar pagamento de consignação.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: Leonardo $
 * $Revision: 28732 $
 * $Date: 2020-01-02 15:27:15 -0300 (qua, 05 fev 2020) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/anexarPagamentoConsignacao" })
public class AnexarPagamentoConsignacaoWebController extends AbstractConsultarConsignacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AnexarPagamentoConsignacaoWebController.class);

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private SaldoDevedorController saldoDevedorController;

    @Override
    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        if (!(responsavel.isSer() || responsavel.isCsaCor()) || !responsavel.temPermissao(CodedValues.FUN_ANEXAR_COMPROVANTE_PAG_SALDO)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usuarioNaoTemPermissao", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Verifica obrigatóriedade do anexo
        boolean anexoObrigatorio = responsavel.isSer() || ParamSist.paramEquals(CodedValues.TPC_EXIGE_ANEXO_COMPROVANTE_PAGAMENTO_SALDO_SERVIDOR, CodedValues.TPC_SIM, responsavel);

        // Recupera a autorização desconto
        String adeCodigo = request.getParameter("ADE_CODIGO");
        CustomTransferObject autdes = null;
        try {
            autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
            autdes = TransferObjectHelper.mascararUsuarioHistorico(autdes, null, responsavel);
        } catch (AutorizacaoControllerException ex) {
            String msg = ex.getMessage();
            session.setAttribute(CodedValues.MSG_ERRO, msg);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            boolean permiteBloqCsaNaoLiqAdePagoAnexoSer = ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_BLOQ_CSA_N_LIQ_ADE_SALDO_PAGO_SER, responsavel);
            boolean temSolicitacaoSaldoDevedorLiquidacao = saldoDevedorController.temSolicitacaoSaldoDevedorLiquidacaoRespondida(adeCodigo, responsavel);
            if (responsavel.isSer() && (!permiteBloqCsaNaoLiqAdePagoAnexoSer || !temSolicitacaoSaldoDevedorLiquidacao)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String sadCodigo = autdes.getAttribute(Columns.ADE_SAD_CODIGO).toString();
            if (sadCodigo.equals(CodedValues.SAD_INDEFERIDA) || sadCodigo.equals(CodedValues.SAD_CANCELADA) || sadCodigo.equals(CodedValues.SAD_LIQUIDADA) || sadCodigo.equals(CodedValues.SAD_CONCLUIDO) || sadCodigo.equals(CodedValues.SAD_ENCERRADO) || sadCodigo.equals(CodedValues.SAD_SUSPENSA) || sadCodigo.equals(CodedValues.SAD_SUSPENSA_CSE)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informar.pagamento.saldo.devedor.anexo.status.invalido", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String[] extensoes = UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_CONTRATO;
            if (TextHelper.isNull(extensoes)) {
                extensoes = "txt,zip".split(",");
            } else {
                extensoes = ((TextHelper.join(extensoes, ",")).replaceAll("[.]", "")).split(",");
            }

            LOG.debug("FORM : '" + JspHelper.verificaVarQryStr(request, "FORM") + "'");
            if (JspHelper.verificaVarQryStr(request, "FORM").equals("form1")) {
                String aadNome = JspHelper.verificaVarQryStr(request, "FILE1");
                String aadDescricao = JspHelper.verificaVarQryStr(request, "AAD_DESCRICAO");
                String idAnexo = session.getId();
                String obs = JspHelper.verificaVarQryStr(request, "obs");

                if ((TextHelper.isNull(idAnexo) || TextHelper.isNull(aadNome)) && anexoObrigatorio) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informar.pagamento.saldo.devedor.anexo.obrigatorio", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                if (aadNome.length() > 255) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informar.pagamento.saldo.devedor.anexo.nome.muito.longo", responsavel, "255"));
                } else {
                    String diretorioRaizArquivos = ParamSist.getDiretorioRaizArquivos();
                    File diretorioDestino = new File(diretorioRaizArquivos + File.separator + "anexo" + File.separatorChar + DateHelper.format((Date) autdes.getAttribute(Columns.ADE_DATA), "yyyyMMdd") + File.separatorChar + (String) autdes.getAttribute(Columns.ADE_CODIGO));
                    File arquivoDestino = new File(diretorioDestino.getPath() + File.separator + aadNome);
                    if (arquivoDestino.exists()) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informar.pagamento.saldo.devedor.anexo.nome.ja.existe", responsavel));
                    } else {
                        try {
                            if (responsavel.isSer()) {
                                boolean ok = saldoDevedorController.informarComprovantePagamentoSaldoDevedor(adeCodigo, idAnexo, aadNome, aadDescricao, responsavel);
                                if (ok) {
                                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.informar.pagamento.saldo.devedor.anexo.sucesso", responsavel));
                                }
                            } else if (responsavel.isCsaCor()) {
                                saldoDevedorController.informarPagamentoSaldoDevedor(adeCodigo, obs, idAnexo, aadNome, aadDescricao, responsavel);
                                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.saldo.devedor.sucesso", responsavel));
                                // Verifica se a consignatária pode ser desbloqueada automaticamente
                                String csaCodigo = (responsavel.isCor() ? responsavel.getCodigoEntidadePai() : responsavel.getCodigoEntidade());
                                if (consignatariaController.verificarDesbloqueioAutomaticoConsignataria(csaCodigo, responsavel)) {
                                    //   session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.informacao.csa.desbloqueada.automaticamente", responsavel));
                                }
                                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                            }
                        } catch (SaldoDevedorControllerException ex) {
                            LOG.error(ex.getMessage(), ex);
                            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
        }

        model.addAttribute("adeCodigo", adeCodigo);
        model.addAttribute("obrigatorio", anexoObrigatorio);

        return viewRedirect("jsp/alterarConsignacao/anexarPagamentoConsignacao", request, session, model, responsavel);
    }
}
