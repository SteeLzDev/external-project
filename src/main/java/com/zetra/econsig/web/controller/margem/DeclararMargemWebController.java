package com.zetra.econsig.web.controller.margem;

import java.io.File;
import java.math.BigDecimal;
import java.util.Calendar;
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
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.dto.entidade.EstabelecimentoTransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.consignacao.BoletoHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.margem.TextoMargem;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.helper.web.v3.JspHelper;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.AcaoTipoDadoAdicionalEnum;
import com.zetra.econsig.values.CodedNames;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.VisibilidadeTipoDadoAdicionalEnum;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: DeclararMargemWebController</p>
 * <p>Description: Controlador Web para o caso de uso Declaracao de margem.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/declararMargem" })
public class DeclararMargemWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DeclararMargemWebController.class);

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Autowired
    private ConsultarMargemController consultarMargemController;

    @Autowired
    private ServidorController servidorController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String rseCodigo = JspHelper.verificaVarQryStr(request, "RSE_CODIGO");
        if (TextHelper.isNull(rseCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        CustomTransferObject servidor = null;

        try {
            // Busca os dados do servidor
            servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
            // Adiciona campos da data de referencia da margem
            Date[] datasReferenciaMargem = TextoMargem.getDatasReferenciaMargem(servidor, responsavel);
            if (datasReferenciaMargem != null) {
                servidor.setAttribute("rse_data_referencia_margem", datasReferenciaMargem[0]);
                servidor.setAttribute("rse_data_referencia_anterior_margem", datasReferenciaMargem[1]);
            }

            // Busca os dados do órgão, estabelecimento e consignante do servidor
            String orgCodigo = servidor.getAttribute(Columns.ORG_CODIGO).toString();
            OrgaoTransferObject orgao = consignanteController.findOrgao(orgCodigo, responsavel);
            EstabelecimentoTransferObject estabelecimento = consignanteController.findEstabelecimento(orgao.getEstCodigo(), responsavel);
            ConsignanteTransferObject consignante = consignanteController.findConsignante(estabelecimento.getCseCodigo(), responsavel);
            servidor.setAtributos(orgao.getAtributos());
            servidor.setAtributos(estabelecimento.getAtributos());
            servidor.setAtributos(consignante.getAtributos());

            // Calcula data de validade da declaração
            Calendar dataAtual = Calendar.getInstance();
            dataAtual.add(Calendar.DAY_OF_MONTH, 7);
            servidor.setAttribute("data_validade", dataAtual.getTime());
            // Consulta as margens do servidor e adiciona ao conjunto de parâmetros
            List<MargemTO> margens = consultarMargemController.consultarMargem(rseCodigo, null, null, null, false, false, responsavel);
            for (MargemTO margem : margens) {
                Short marCodigo = margem.getMarCodigo();
                BigDecimal vlrMargemFolha = margem.getMrsMargem();
                BigDecimal vlrMargemRest = margem.getMrsMargemRest();
                if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM)) {
                    servidor.setAttribute(Columns.RSE_MARGEM, vlrMargemFolha);
                    servidor.setAttribute(Columns.RSE_MARGEM_REST, vlrMargemRest);
                } else if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
                    servidor.setAttribute(Columns.RSE_MARGEM_2, vlrMargemFolha);
                    servidor.setAttribute(Columns.RSE_MARGEM_REST_2, vlrMargemRest);
                } else if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
                    servidor.setAttribute(Columns.RSE_MARGEM_3, vlrMargemFolha);
                    servidor.setAttribute(Columns.RSE_MARGEM_REST_3, vlrMargemRest);
                }
            }

            // Gera senha de autorização, caso esteja habilitado parâmetros
            if (ParamSist.paramEquals(CodedValues.TPC_GERA_SENHA_AUT_SER_DECLARACAO_MARGEM, CodedValues.TPC_SIM, responsavel) && ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, CodedValues.TPC_SIM, responsavel) && ParamSist.paramEquals(CodedValues.TPC_USA_MULTIPLAS_SENHAS_AUTORIZACAO_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                String novaSenhaPlana = "";
                if (responsavel.temPermissao(CodedValues.FUN_GESTOR_EDITA_SENHA_MULT_AUT_SER)) {
                    TransferObject usuarioSer = usuarioController.getSenhaServidor(rseCodigo, responsavel);
                    if (usuarioSer != null) {
                        novaSenhaPlana = usuarioController.gerarSenhaAutorizacao((String) usuarioSer.getAttribute(Columns.USU_CODIGO), false, responsavel);
                    }
                }
                servidor.setAttribute("codigo_unico", novaSenhaPlana);
            }

            List<TransferObject> lstDadosSer = servidorController.lstDadosServidor(AcaoTipoDadoAdicionalEnum.CONSULTA, VisibilidadeTipoDadoAdicionalEnum.WEB, servidor.getAttribute(Columns.SER_CODIGO).toString(), responsavel);

            for (TransferObject dadosSer : lstDadosSer) {
                if ((dadosSer.getAttribute(Columns.TDA_TEN_CODIGO).equals(Log.SERVIDOR))) {
                    String tdaCodigo = (String) dadosSer.getAttribute(Columns.TDA_CODIGO);
                    String dasValor = (String) dadosSer.getAttribute(Columns.DAS_VALOR);

                    if (tdaCodigo.equals(CodedValues.TDA_LICENCAS)) {
                        // Se 0, exibir "Não houve suspensões", Se 1, "Houve suspensões"
                        if (!TextHelper.isNull(dasValor) && dasValor.equals(CodedValues.TDA_TEM_ANTECIPACAO_FUNDO_GARANTIA_SIM)) {
                            dasValor = ApplicationResourcesHelper.getMessage("rotulo.declaracao.dado.adicional.licenca.houve.suspensao", responsavel);
                        } else {
                            dasValor = ApplicationResourcesHelper.getMessage("rotulo.declaracao.dado.adicional.licenca.nao.houve.suspensao", responsavel);
                        }

                    } else if (tdaCodigo.equals(CodedValues.TDA_MOTIVO_LICENCAS_SOFRIDAS)) {
                        // M - doença, I - lesão, G - gravidez, P - puerpério, S - serviço militar, A - Outro
                        if (!TextHelper.isNull(dasValor) && dasValor.equals(CodedValues.TDA_MOTIVO_LICENCAS_SOFRIDAS_DOENCA)) {
                            dasValor = ApplicationResourcesHelper.getMessage("rotulo.declaracao.dado.adicional.motivo.licenca.doenca", responsavel);
                        } else if (!TextHelper.isNull(dasValor) && dasValor.equals(CodedValues.TDA_MOTIVO_LICENCAS_SOFRIDAS_LESAO)) {
                            dasValor = ApplicationResourcesHelper.getMessage("rotulo.declaracao.dado.adicional.motivo.licenca.lesao", responsavel);
                        } else if (!TextHelper.isNull(dasValor) && dasValor.equals(CodedValues.TDA_MOTIVO_LICENCAS_SOFRIDAS_GRAVIDEZ)) {
                            dasValor = ApplicationResourcesHelper.getMessage("rotulo.declaracao.dado.adicional.motivo.licenca.gravidez", responsavel);
                        } else if (!TextHelper.isNull(dasValor) && dasValor.equals(CodedValues.TDA_MOTIVO_LICENCAS_SOFRIDAS_PUERPERIO)) {
                            dasValor = ApplicationResourcesHelper.getMessage("rotulo.declaracao.dado.adicional.motivo.licenca.pruerperio", responsavel);
                        } else if (!TextHelper.isNull(dasValor) && dasValor.equals(CodedValues.TDA_MOTIVO_LICENCAS_SOFRIDAS_SERVICO_MILITAR)) {
                            dasValor = ApplicationResourcesHelper.getMessage("rotulo.declaracao.dado.adicional.motivo.licenca.servico.militar", responsavel);
                        } else if (!TextHelper.isNull(dasValor) && dasValor.equals(CodedValues.TDA_MOTIVO_LICENCAS_SOFRIDAS_OUTRO)) {
                            dasValor = ApplicationResourcesHelper.getMessage("rotulo.declaracao.dado.adicional.motivo.licenca.outro", responsavel);
                        }

                    } else if (tdaCodigo.equals(CodedValues.TDA_TIPO_APOSENTADORIA_QUE_TERA)) {
                        // P - Pensão, L - Liquidação
                        if (!TextHelper.isNull(dasValor) && dasValor.equals(CodedValues.TDA_TIPO_APOSENTADORIA_PENSAO)) {
                            dasValor = ApplicationResourcesHelper.getMessage("rotulo.declaracao.dado.adicional.tipo.aposentadoria.pensao", responsavel);
                        } else if (!TextHelper.isNull(dasValor) && dasValor.equals(CodedValues.TDA_TIPO_APOSENTADORIA_LIQUIDACAO)) {
                            dasValor = ApplicationResourcesHelper.getMessage("rotulo.declaracao.dado.adicional.tipo.aposentadoria.liquidacao", responsavel);
                        }

                    } else if (tdaCodigo.equals(CodedValues.TDA_CUSTODIANTE_FUNDO_GARANTIA)) {
                        // A - ATC, I - INPS, C - Fundo Complementar
                        if (!TextHelper.isNull(dasValor) && dasValor.equals(CodedValues.TDA_CUSTODIANTE_FUNDO_GARANTIA_ATC)) {
                            dasValor = ApplicationResourcesHelper.getMessage("rotulo.declaracao.dado.adicional.fundo.garantia.atc", responsavel);
                        } else if (!TextHelper.isNull(dasValor) && dasValor.equals(CodedValues.TDA_CUSTODIANTE_FUNDO_GARANTIA_INPS)) {
                            dasValor = ApplicationResourcesHelper.getMessage("rotulo.declaracao.dado.adicional.fundo.garantia.inps", responsavel);
                        } else if (!TextHelper.isNull(dasValor) && dasValor.equals(CodedValues.TDA_CUSTODIANTE_FUNDO_GARANTIA_COMPLEMENTAR)) {
                            dasValor = ApplicationResourcesHelper.getMessage("rotulo.declaracao.dado.adicional.fundo.garantia.fundo.complementar", responsavel);
                        }

                    } else if (tdaCodigo.equals(CodedValues.TDA_TEM_ANTECIPACAO_FUNDO_GARANTIA)) {
                        // 0 - Não, 1 - Sim
                        if (!TextHelper.isNull(dasValor) && dasValor.equals(CodedValues.TDA_TEM_ANTECIPACAO_FUNDO_GARANTIA_SIM)) {
                            dasValor = ApplicationResourcesHelper.getMessage("rotulo.sim", responsavel);
                        } else {
                            dasValor = ApplicationResourcesHelper.getMessage("rotulo.nao", responsavel);
                        }
                    }

                    servidor.setAttribute("das_valor_" + tdaCodigo, dasValor);
                }
            }

        } catch (UsuarioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String absolutePath = ParamSist.getDiretorioRaizArquivos();
        String templatePath = absolutePath + File.separatorChar + "boleto" + File.separatorChar + CodedNames.TEMPLATE_DECLARACAO_MARGEM;
        File templateFile = new File(templatePath);
        if (!templateFile.exists() || !templateFile.canRead()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.template.declaracao.nao.encontrado", responsavel, templatePath));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String templateText = FileHelper.readAll(templatePath);
        String boletoText = null;
        try {
            boletoText = BoletoHelper.substituirPadroes(templateText, servidor);
        } catch (ViewHelperException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        ParamSession paramSession = ParamSession.getParamSession(session);
        String destinoBotaoVoltar = null;
        if (responsavel.isSer()) {
            destinoBotaoVoltar = "../v3/carregarPrincipal";
        } else {
            destinoBotaoVoltar = SynchronizerToken.updateTokenInURL(paramSession.getLastHistory() + "&RSE_CODIGO=" + rseCodigo, request);
            if (destinoBotaoVoltar.contains("declararMargem")) {
                destinoBotaoVoltar = destinoBotaoVoltar.replace("&acao=pesquisarServidor", "&acao=iniciar");
            } else if (destinoBotaoVoltar.contains("consultarMargem")) {
                destinoBotaoVoltar = destinoBotaoVoltar.replace("&acao=pesquisarServidor", "&acao=consultar");
            } else {
                destinoBotaoVoltar = destinoBotaoVoltar.replace("&acao=pesquisarServidor", "&acao=reservarMargem");
            }
        }

        model.addAttribute("boletoText", boletoText);
        model.addAttribute("destinoBotaoVoltar", destinoBotaoVoltar);

        return viewRedirect("jsp/declararMargem/declararMargem", request, session, model, responsavel);
    }
}
