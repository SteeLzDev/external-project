package com.zetra.econsig.web.controller.servidor;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.BeneficioControllerException;
import com.zetra.econsig.exception.CalculoBeneficioControllerException;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.persistence.entity.EnderecoServidor;
import com.zetra.econsig.persistence.entity.Servidor;
import com.zetra.econsig.persistence.entity.TipoEndereco;
import com.zetra.econsig.service.arquivo.ArquivoController;
import com.zetra.econsig.service.notificacao.NotificacaoUsuarioController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.AcaoTipoDadoAdicionalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.values.TipoEnderecoEnum;
import com.zetra.econsig.values.VisibilidadeTipoDadoAdicionalEnum;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: EditarServidorWebController</p>
 * <p>Description: Controlador Web para o caso de uso Editar Servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/editarServidor" })
public class EditarServidorWebController extends ConsultarServidorWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EditarServidorWebController.class);

    @Autowired
    private NotificacaoUsuarioController notificacaoUsuarioController;

    @Autowired
    private ConsultarMargemController consultarMargemController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private ArquivoController arquivoController;

    @RequestMapping(params = { "acao=editar" })
    public String editar(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            // Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            if (responsavel.isSer()) {
                rseCodigo = responsavel.getRseCodigo();
            }

            final ServidorTransferObject servidor = buscaServidor(rseCodigo, session, responsavel);

            final String msgErro = verificarCamposForm(request, session, false, responsavel);
            Boolean aceiteTermoUso = "S".equals(JspHelper.verificaVarQryStr(request, "verTermoUsoEmail"));

            final String serEmailAntigo = (servidor.getSerEmail() != null ? servidor.getSerEmail() : "");
            final String serEmailAtual = (String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_EMAIL, serEmailAntigo, responsavel);
            if (responsavel.isCseSupOrg() && ParamSist.paramEquals(CodedValues.TPC_EXIGE_CONFIRMA_LEITURA_TERMO_MUDANCA_EMAIL, CodedValues.TPC_SIM, responsavel)) {
                if (!serEmailAntigo.equals(serEmailAtual) && !aceiteTermoUso) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("rotulo.termo.de.uso.email.aceitar.alert", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                } else if (serEmailAntigo.equals(serEmailAtual)) {
                    aceiteTermoUso = false;
                }
            } else {
                aceiteTermoUso = false;
            }

            // Realiza o update
            if ((msgErro.length() == 0) && responsavel.temPermissao(CodedValues.FUN_EDT_SERVIDOR)) {
                try {
                    // Atualiza o Registro Servidor
                    if (!TextHelper.isNull(rseCodigo)) {

                        try {
                            final RegistroServidorTO registroServidor = servidorController.findRegistroServidor(rseCodigo, true, responsavel);
                            recuperarDadosRegistroServidor(registroServidor, request, responsavel);

                            //DESENV-17918: Quando o uniCodigo do registro servidor está ativado o email só pode ser editado se o usuário tiver permissão para editar esta unidade.
                            if (!responsavel.isSup() && !TextHelper.isNull(registroServidor.getUniCodigo()) && !responsavel.temPermissaoEdtUnidade(registroServidor.getUniCodigo()) && !serEmailAntigo.equals(serEmailAtual) ) {
                                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.permissao.edt.email", responsavel));
                                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                            }

                            List<MargemTO> margens = null;
                            if (responsavel.temPermissao(CodedValues.FUN_ALT_MARGEM_CONSIGNAVEL) || responsavel.temPermissao(CodedValues.FUN_ALT_MARGEM_CONSIGNAVEL_MENOR)) {
                                // Atualiza as margens
                                // Busca a lista de margens do servidor
                                try {
                                    margens = consultarMargemController.consultarMargem(rseCodigo, null, null, null, true, false, responsavel);
                                } catch (final Exception ex) {
                                    LOG.error(ex.getMessage(), ex);
                                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                                }

                                if ((margens != null) && !margens.isEmpty()) {
                                    for (final MargemTO margemTO : margens) {
                                        final String edtRegistroServidor_margem = "edtRegistroServidor_margem" + margemTO.getMarCodigo().toString();
                                        final Object rseMargem = JspHelper.verificaVarQryStr(request, edtRegistroServidor_margem);
                                        if (!TextHelper.isNull(rseMargem) && (rseMargem instanceof String)) {
                                            margemTO.setMrsMargem(new BigDecimal(NumberHelper.reformat((String) rseMargem, NumberHelper.getLang(), "en")));
                                            if ("edtRegistroServidor_margem1".equals(edtRegistroServidor_margem)) {
                                                registroServidor.setRseMargem(new BigDecimal(NumberHelper.reformat((String) rseMargem, NumberHelper.getLang(), "en")));
                                            } else if ("edtRegistroServidor_margem2".equals(edtRegistroServidor_margem)) {
                                                registroServidor.setRseMargem2(new BigDecimal(NumberHelper.reformat((String) rseMargem, NumberHelper.getLang(), "en")));
                                            } else if ("edtRegistroServidor_margem3".equals(edtRegistroServidor_margem)) {
                                                registroServidor.setRseMargem3(new BigDecimal(NumberHelper.reformat((String) rseMargem, NumberHelper.getLang(), "en")));
                                            }
                                        }
                                    }
                                }
                            }

                            if (responsavel.temPermissao(CodedValues.FUN_EDT_SERVIDOR)) {

                                // DESENV-15976 - Anexar documento para o registro servidor
                                final String nomeAnexo = JspHelper.verificaVarQryStr(request, "FILE1");

                                if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ANEXO_DOCUMENTO, responsavel) && TextHelper.isNull(nomeAnexo)) {
                                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.anexo.documento", responsavel));
                                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                                }

                                // Dados adicionais do servidor
                                final List<TransferObject> tdaList = servidorController.lstTipoDadoAdicionalServidorQuery(AcaoTipoDadoAdicionalEnum.ALTERA, VisibilidadeTipoDadoAdicionalEnum.WEB, responsavel);
                                for (final TransferObject tda : tdaList) {
                                    final String tdaCodigo = (String) tda.getAttribute(Columns.TDA_CODIGO);
                                    final String dadValor = JspHelper.parseValor(request, null, "TDA_" + tdaCodigo, (String) tda.getAttribute(Columns.TDA_DOMINIO));
                                    servidorController.setValorDadoServidor(servidor.getSerCodigo(), tdaCodigo, dadValor, responsavel);
                                }

                                if (validarDadosRegistroServidor(registroServidor, request, responsavel)) {
                                    // Executa rotina de atualização
                                    recuperarDadosServidor(servidor, request, notificacaoUsuarioController, responsavel);
                                    servidorController.updateServidorAndUpdateRegistroServidor(registroServidor, servidor, margens, aceiteTermoUso, responsavel);

                                    final String[] anexosName = !TextHelper.isNull(nomeAnexo) ? nomeAnexo.split(";") : null;
                                    final String idAnexo = session.getId();

                                    final List<TransferObject> conteudos = new ArrayList<>();
                                    if (anexosName != null) {
                                        for (final String nomeAnexoCorrente : anexosName) {
                                            try {
                                                final TransferObject conteudo = new CustomTransferObject();

                                                final File anexo = UploadHelper.retornaArquivoAnexoTemporario(nomeAnexoCorrente, idAnexo, responsavel);
                                                final byte[] fileContent = FileUtils.readFileToByteArray(anexo);
                                                final byte[] conteudoArquivoBase64 = Base64.getEncoder().encode(fileContent);

                                                conteudo.setAttribute(Columns.ARQ_CONTEUDO, conteudoArquivoBase64);
                                                conteudo.setAttribute(Columns.ARS_NOME, nomeAnexoCorrente);

                                                conteudos.add(conteudo);
                                            } catch (final IOException e) {
                                                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                                                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                                            }
                                        }

                                        final TransferObject criterio = new CustomTransferObject();
                                        criterio.setAttribute(Columns.ARQ_CONTEUDO, conteudos);

                                        arquivoController.createArquivoRegistroServidor(rseCodigo, TipoArquivoEnum.ARQUIVO_ANEXO_DOCUMENTO_REGISTRO_SERVIDOR.getCodigo(), criterio, responsavel);
                                    }

                                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.servidor.alterado.sucesso", responsavel));
                                } else {
                                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                                }
                            }

                        } catch (final Exception ex) {
                            LOG.error(ex.getMessage(), ex);
                            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                        }
                    }

                } catch (final Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            model.addAttribute("msgErro", msgErro);

            // Repassa o token salvo, pois o método irá revalidar o token
            request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

            final ParamSession paramSession = ParamSession.getParamSession(session);
            paramSession.halfBack();

            return consultar(rseCodigo, request, response, session, model);

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=listarEndereco" })
    public String listarEndereco(@RequestParam(value = "SER_CODIGO", required = true, defaultValue = "") String serCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            // Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            SynchronizerToken.saveToken(request);

            final List<EnderecoServidor> listaEnderecoServidor = servidorController.listEnderecoServidorByCodigo(serCodigo, responsavel);

            final List<TipoEndereco> tipoEndereco = servidorController.listAllTipoEndereco(responsavel);

            if (listaEnderecoServidor != null) {
                for (final EnderecoServidor endServidor : listaEnderecoServidor) {
                    if (tipoEndereco.contains(endServidor.getTipoEndereco())) {
                        tipoEndereco.remove(endServidor.getTipoEndereco());
                    }
                }
            }

            if ((tipoEndereco.size() == 0) || ((tipoEndereco.size() == 1) && tipoEndereco.get(0).getTieCodigo().equals(TipoEnderecoEnum.RESIDENCIAL.getCodigo()))) {
                model.addAttribute("novo", false);
            } else {
                model.addAttribute("novo", true);
            }

            final ServidorTransferObject servidor = servidorController.findServidor(serCodigo, responsavel);

            model.addAttribute("podeEditar", responsavel.temPermissao(CodedValues.FUN_EDT_ENDERECO_SERVIDOR));
            model.addAttribute("servidor", servidor);
            model.addAttribute("enderecoServidor", listaEnderecoServidor);

            if ((listaEnderecoServidor == null) || (listaEnderecoServidor.size() == 0)) {
                session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.erro.endereco.servidor.nenhum.registro", responsavel));
            }

            return viewRedirect("jsp/editarServidor/listarEnderecoServidor", request, session, model, responsavel);

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=salvarEndereco" })
    public String salvarEndereco(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            // Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final EnderecoServidor enderecoServidor = new EnderecoServidor();
            final TipoEndereco tipoEndereco = new TipoEndereco();
            final Servidor servidor = new Servidor();

            enderecoServidor.setEnsLogradouro(JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.ENS_LOGRADOURO)));
            enderecoServidor.setEnsBairro(JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.ENS_BAIRRO)));
            enderecoServidor.setEnsNumero(JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.ENS_NUMERO)));
            enderecoServidor.setEnsComplemento(JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.ENS_COMPLEMENTO)));
            enderecoServidor.setEnsCep(JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.ENS_CEP)));
            enderecoServidor.setEnsCodigoMunicipio(JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.ENS_MUNICIPIO)).split(";")[0]);
            enderecoServidor.setEnsMunicipio(JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.ENS_MUNICIPIO)).split(";")[1]);
            servidor.setSerCodigo(JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.ENS_SER_CODIGO)));
            enderecoServidor.setEnsUf(JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.ENS_UF)));
            enderecoServidor.setServidor(servidor);
            tipoEndereco.setTieCodigo(JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.ENS_TIE_CODIGO)));
            enderecoServidor.setTipoEndereco(tipoEndereco);
            enderecoServidor.setEnsAtivo(CodedValues.STS_ATIVO);

            if (!"".equals(JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.ENS_CODIGO)))) {
                enderecoServidor.setEnsCodigo(JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.ENS_CODIGO)));
                servidorController.updateEnderecoServidor(enderecoServidor, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.servidor.alterado.sucesso", responsavel));
            } else {
                servidorController.createEnderecoServidor(enderecoServidor, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.novo.endereco.servidor.exibicao.sucesso", responsavel));
            }
            String ensCodigo = !TextHelper.isNull(enderecoServidor.getEnsCodigo()) ? enderecoServidor.getEnsCodigo() : null;

            // Se o ensCodigo for nulo, ou seja, um endereço novo, itera até encontrar o último endereço ativo criado no banco de dados do mesmo tipoEndereço
            if (TextHelper.isNull(ensCodigo)) {
                final List<EnderecoServidor> listaEnderecoServidor = servidorController.listEnderecoServidorByCodigo(servidor.getSerCodigo(), responsavel);
                for (final EnderecoServidor element : listaEnderecoServidor) {
                    // Se o endereço estiver ativo e o código do tipoEndereço for o mesmo código do tipoEndereço criado, seta no ensCodigo para tela de edição.
                    if (element.getEnsAtivo().equals(CodedValues.STS_ATIVO) && element.getTipoEndereco().getTieCodigo().equals(enderecoServidor.getTipoEndereco().getTieCodigo())) {
                        ensCodigo = element.getEnsCodigo();
                        break;
                    }
                }
            }

            return editarEndereco(ensCodigo, servidor.getSerCodigo(), request, response, session, model);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=novoEndereco" })
    public String novoEndereco(@RequestParam(value = "SER_CODIGO", required = true, defaultValue = "") String serCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            // Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            SynchronizerToken.saveToken(request);

            final ServidorTransferObject servidor = servidorController.findServidor(serCodigo, responsavel);

            final List<TipoEndereco> tipoEndereco = servidorController.listAllTipoEndereco(responsavel);

            final List<EnderecoServidor> listaEnderecoServidor = servidorController.listEnderecoServidorByCodigo(serCodigo, responsavel);

            final List<TransferObject> tipoEnd = new ArrayList<>();

            TipoEndereco tipoEnderecoResidencial = new TipoEndereco();

            for (final TipoEndereco tie : tipoEndereco) {
                if (tie.getTieCodigo().equals(TipoEnderecoEnum.RESIDENCIAL.getCodigo())) {
                    tipoEnderecoResidencial = tie;
                }
            }

            tipoEndereco.remove(tipoEnderecoResidencial);

            if (listaEnderecoServidor != null) {
                for (final EnderecoServidor end : listaEnderecoServidor) {
                    if ((end.getTipoEndereco() != null) && tipoEndereco.contains(end.getTipoEndereco())) {
                        tipoEndereco.remove(end.getTipoEndereco());
                    }
                }
            }

            final TipoEndereco tipoEnderecoArray[] = new TipoEndereco[4];

            for (final TipoEndereco tie : tipoEndereco) {
                if (tie.getTieCodigo().equals(TipoEnderecoEnum.COMERCIAL.getCodigo())) {
                    tipoEnderecoArray[0] = tie;
                } else if (tie.getTieCodigo().equals(TipoEnderecoEnum.FISCAL.getCodigo())) {
                    tipoEnderecoArray[1] = tie;
                } else if (tie.getTieCodigo().equals(TipoEnderecoEnum.COBRANCA.getCodigo())) {
                    tipoEnderecoArray[2] = tie;
                } else if (tie.getTieCodigo().equals(TipoEnderecoEnum.OUTRO.getCodigo())) {
                    tipoEnderecoArray[3] = tie;
                }
            }

            final List<TipoEndereco> listaTipoEnderecoOrdenada = new ArrayList<>(Arrays.asList(tipoEnderecoArray));

            for (final TipoEndereco tie : listaTipoEnderecoOrdenada) {
                if (tie != null) {
                    final CustomTransferObject customTransfer = new CustomTransferObject();
                    customTransfer.setAttribute(Columns.TIE_CODIGO, tie.getTieCodigo());
                    customTransfer.setAttribute(Columns.TIE_DESCRICAO, tie.getTieDescricao());
                    tipoEnd.add(customTransfer);
                }
            }

            model.addAttribute("podeEditar", responsavel.temPermissao(CodedValues.FUN_EDT_ENDERECO_SERVIDOR));
            model.addAttribute("servidor", servidor);
            model.addAttribute("tipoEndereco", tipoEnd);

            return viewRedirect("jsp/editarServidor/editarEnderecoServidor", request, session, model, responsavel);

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=editarEndereco" })
    public String editarEndereco(@RequestParam(value = "ENS_CODIGO", required = true, defaultValue = "") String ensCodigo, @RequestParam(value = "SER_CODIGO", required = true, defaultValue = "") String serCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            // Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            SynchronizerToken.saveToken(request);

            final EnderecoServidor enderecoServidor = servidorController.findEnderecoServidorByCodigo(ensCodigo, responsavel);

            final ServidorTransferObject servidor = servidorController.findServidor(serCodigo, responsavel);

            final List<TipoEndereco> tipoEndereco = servidorController.listAllTipoEndereco(responsavel);

            final List<EnderecoServidor> listaEnderecoServidor = servidorController.listEnderecoServidorByCodigo(serCodigo, responsavel);

            final List<TransferObject> tipoEnd = new ArrayList<>();

            TipoEndereco tipoEnderecoResidencial = new TipoEndereco();

            for (final TipoEndereco tie : tipoEndereco) {
                if (tie.getTieCodigo().equals(TipoEnderecoEnum.RESIDENCIAL.getCodigo())) {
                    tipoEnderecoResidencial = tie;
                }
            }

            tipoEndereco.remove(tipoEnderecoResidencial);

            if (listaEnderecoServidor != null) {
                for (final EnderecoServidor end : listaEnderecoServidor) {
                    if (((end.getTipoEndereco() != null) && tipoEndereco.contains(end.getTipoEndereco())) && !enderecoServidor.getTipoEndereco().getTieCodigo().equals(end.getTipoEndereco().getTieCodigo())) {
                        tipoEndereco.remove(end.getTipoEndereco());
                    }
                }
            }

            final TipoEndereco tipoEnderecoArray[] = new TipoEndereco[4];

            for (final TipoEndereco tie : tipoEndereco) {

                if (tie.getTieCodigo().equals(TipoEnderecoEnum.COMERCIAL.getCodigo())) {
                    tipoEnderecoArray[0] = tie;
                } else if (tie.getTieCodigo().equals(TipoEnderecoEnum.FISCAL.getCodigo())) {
                    tipoEnderecoArray[1] = tie;
                } else if (tie.getTieCodigo().equals(TipoEnderecoEnum.COBRANCA.getCodigo())) {
                    tipoEnderecoArray[2] = tie;
                } else if (tie.getTieCodigo().equals(TipoEnderecoEnum.OUTRO.getCodigo())) {
                    tipoEnderecoArray[3] = tie;
                }

            }

            final List<TipoEndereco> listaTipoEnderecoOrdenada = new ArrayList<>(Arrays.asList(tipoEnderecoArray));

            for (final TipoEndereco tie : listaTipoEnderecoOrdenada) {
                if (tie != null) {
                    final CustomTransferObject customTransfer = new CustomTransferObject();
                    customTransfer.setAttribute(Columns.TIE_CODIGO, tie.getTieCodigo());
                    customTransfer.setAttribute(Columns.TIE_DESCRICAO, tie.getTieDescricao());
                    tipoEnd.add(customTransfer);
                }
            }

            model.addAttribute("podeEditar", responsavel.temPermissao(CodedValues.FUN_EDT_ENDERECO_SERVIDOR));
            model.addAttribute("servidor", servidor);
            model.addAttribute("tipoEndereco", tipoEnd);
            model.addAttribute("enderecoServidor", enderecoServidor);

            return viewRedirect("jsp/editarServidor/editarEnderecoServidor", request, session, model, responsavel);

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=visualizarEndereco" })
    public String visualizarEndereco(@RequestParam(value = "ENS_CODIGO", required = true, defaultValue = "") String ensCodigo, @RequestParam(value = "SER_CODIGO", required = true, defaultValue = "") String serCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return editarEndereco(ensCodigo, serCodigo, request, response, session, model);
    }

    @RequestMapping(params = { "acao=excluirEndereco" })
    public String excluirEndereco(@RequestParam(value = "ENS_CODIGO", required = true) String ensCodigo, @RequestParam(value = "SER_CODIGO", required = true) String serCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws CalculoBeneficioControllerException, ConsignanteControllerException, BeneficioControllerException, InstantiationException, IllegalAccessException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final ParamSession paramSession = ParamSession.getParamSession(session);

        SynchronizerToken.saveToken(request);

        try {
            final EnderecoServidor enderecoServidor = servidorController.findEnderecoServidorByCodigo(ensCodigo, responsavel);
            enderecoServidor.setEnsCodigo(ensCodigo);
            servidorController.removeEnderecoServidor(enderecoServidor, responsavel);

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.servidor.endereco.sucesso.remover", responsavel));
        } catch (final Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.servidor.endereco.erro.remover", responsavel));
        }

        request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";
    }

}
