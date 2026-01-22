package com.zetra.econsig.web.controller.servidor;

import java.math.BigDecimal;
import java.text.ParseException;
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

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: CadastrarServidorWebController</p>
 * <p>Description: Controlador Web para o caso de uso Cadastrar Servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/cadastrarServidor" })
public class CadastrarServidorWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CadastrarServidorWebController.class);

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private ServidorController servidorController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            // Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            List<TransferObject> orgaos = consignanteController.lstOrgaos(null, responsavel);
            List<TransferObject> listEstadoCivil = servidorController.getEstCivil(responsavel);
            List<TransferObject> subOrgaos = servidorController.lstSubOrgao(responsavel, null);
            List<TransferObject> unidades = servidorController.lstUnidade(responsavel, null);
            List<TransferObject> listaSrs = servidorController.lstStatusRegistroServidor(true, true, responsavel);
            List<TransferObject> cargos = servidorController.lstCargo(responsavel);
            List<TransferObject> listaTipoRegServidor = servidorController.lstTipoRegistroServidor(responsavel);
            List<TransferObject> listaPostoCodigo = servidorController.lstPosto(responsavel);
            List<TransferObject> listaCapCivil = servidorController.lstCapacidadeCivil(responsavel);
            // Busca lista com todos os vinculos existentes para criar combo
            List<TransferObject> listaVincRegSer = servidorController.selectVincRegistroServidor(true, responsavel);
            List<TransferObject> padrao = servidorController.lstPadrao(responsavel);
            String srsCodigoPadrao = (ParamSist.paramEquals(CodedValues.TPC_ALTERA_STATUS_RSE_PARA_PENDENTE_NOVA_ADE, CodedValues.TPC_SIM, responsavel)) ? CodedValues.SRS_PENDENTE : CodedValues.SRS_ATIVO;

            model.addAttribute("orgaos", orgaos);
            model.addAttribute("listEstadoCivil", listEstadoCivil);
            model.addAttribute("subOrgaos", subOrgaos);
            model.addAttribute("unidades", unidades);
            model.addAttribute("listaSrs", listaSrs);
            model.addAttribute("srsCodigoPadrao", srsCodigoPadrao);
            model.addAttribute("cargos", cargos);
            model.addAttribute("listaTipoRegServidor", listaTipoRegServidor);
            model.addAttribute("listaPostoCodigo", listaPostoCodigo);
            model.addAttribute("listaCapCivil", listaCapCivil);
            model.addAttribute("listaVincRegSer", listaVincRegSer);
            model.addAttribute("padrao", padrao);

            return viewRedirect("jsp/cadastrarServidor/cadastrarServidor", request, session, model, responsavel);

        } catch (ConsignanteControllerException | ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            // Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String reqColumnsStr = (ShowFieldHelper.canEdit(FieldKeysConstants.CADASTRAR_SERVIDOR_PRIMEIRO_NOME, responsavel) ? "|" + FieldKeysConstants.CADASTRAR_SERVIDOR_PRIMEIRO_NOME : "") + (ShowFieldHelper.canEdit(FieldKeysConstants.CADASTRAR_SERVIDOR_NOME, responsavel) ? "|" + FieldKeysConstants.CADASTRAR_SERVIDOR_NOME : "") + (ShowFieldHelper.canEdit(FieldKeysConstants.CADASTRAR_SERVIDOR_CPF, responsavel) ? "|" + FieldKeysConstants.CADASTRAR_SERVIDOR_CPF : "")
                    + (ShowFieldHelper.canEdit(FieldKeysConstants.CADASTRAR_SERVIDOR_ORGAO, responsavel) ? "|" + FieldKeysConstants.CADASTRAR_SERVIDOR_ORGAO : "") + (ShowFieldHelper.canEdit(FieldKeysConstants.CADASTRAR_SERVIDOR_MATRICULA, responsavel) ? "|" + FieldKeysConstants.CADASTRAR_SERVIDOR_MATRICULA : "");

            String msgErro = JspHelper.verificaCamposForm(request, reqColumnsStr, ApplicationResourcesHelper.getMessage("mensagem.campos.obrigatorios", responsavel), "100%");

            // Realiza a criação
            if (msgErro.length() == 0 && responsavel.temPermissao(CodedValues.FUN_CADASTRAR_SERVIDOR)) {
                // Recupera os dados do cadastro do servidor
                ServidorTransferObject servidor = new ServidorTransferObject();

                servidor.setSerCpf((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_CPF, null, responsavel));
                servidor.setSerPrimeiroNome((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_PRIMEIRO_NOME, null, responsavel));
                servidor.setSerTitulacao((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_TITULACAO, null, responsavel));
                servidor.setSerNomeMeio((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_NOME_MEIO, null, responsavel));
                servidor.setSerUltimoNome((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_ULTIMO_NOME, null, responsavel));
                servidor.setSerNomePai((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_NOME_PAI, null, responsavel));
                servidor.setSerNomeMae((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_NOME_MAE, null, responsavel));

                String serNome = (!TextHelper.isNull(JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_NOME, null, responsavel))) ? (String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_NOME, null, responsavel) : JspHelper.montaSerNome(servidor.getSerTitulacao(), servidor.getSerPrimeiroNome(), servidor.getSerNomeMeio(), servidor.getSerUltimoNome());
                servidor.setSerNome(serNome);

                Object serDataNasc = JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_DATA_NASCIMENTO, null, true, responsavel);
                if (serDataNasc instanceof String) {
                    servidor.setSerDataNasc(serDataNasc != null ? DateHelper.toSQLDate(DateHelper.parse((String) serDataNasc, LocaleHelper.getDatePattern())) : null);
                }

                servidor.setSerNacionalidade((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_NACIONALIDADE, null, responsavel));
                servidor.setSerSexo((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_SEXO, null, responsavel));
                servidor.setSerEstCivil((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_ESTADO_CIVIL, null, responsavel));
                servidor.setSerNroIdt((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_NO_IDENTIDADE, null, responsavel));
                servidor.setSerEmissorIdt((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_EMISSOR_IDENTIDADE, null, responsavel));
                servidor.setSerUfIdt((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_UF_IDENTIDADE, null, responsavel));
                servidor.setSerCartProf((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_NUM_CARTEIRA_TRABALHO, null, responsavel));
                servidor.setSerPis((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_NUM_PIS, null, responsavel));
                servidor.setSerEnd((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_LOGRADOURO, null, responsavel));
                servidor.setSerCompl((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_COMPLEMENTO, null, responsavel));
                servidor.setSerBairro((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_BAIRRO, null, responsavel));
                servidor.setSerCidade((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_CIDADE, null, responsavel));
                servidor.setSerUf((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_UF, null, responsavel));
                servidor.setSerCep((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_CEP, null, responsavel));

                String serTelDdd = (String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_DDD_TELEFONE, null, responsavel);
                String serTel = (String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_TELEFONE, null, responsavel);
                if (!TextHelper.isNull(serTel)) {
                    serTel = TextHelper.dropSeparator(TextHelper.dropBlankSpace(serTel));
                    if (TextHelper.isNull(serTelDdd)) {
                        servidor.setSerTel(serTel);
                    } else {
                        servidor.setSerTel(serTelDdd + "-" + serTel);
                    }
                }

                String serCelularDdd = (String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_DDD_CELULAR, null, responsavel);
                String serCelular = (String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_CELULAR, null, responsavel);
                if (!TextHelper.isNull(serCelular)) {
                    serCelular = TextHelper.dropSeparator(TextHelper.dropBlankSpace(serCelular));
                    if (TextHelper.isNull(serCelularDdd)) {
                        servidor.setSerCelular(serCelular);
                    } else {
                        servidor.setSerCelular(serCelularDdd + "-" + serCelular);
                    }
                }

                servidor.setSerNro((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_NRO, null, true, responsavel));
                servidor.setSerEmail((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_EMAIL, null, responsavel));

                Object serDataIdt = JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_DATA_IDENTIDADE, null, true, responsavel);
                if (serDataIdt != null) {
                    try {
                        serDataIdt = DateHelper.toSQLDate(DateHelper.parse((String) serDataIdt, LocaleHelper.getDatePattern()));
                    } catch (Exception e) {
                        String periodoFormatado = DateHelper.format((Date) serDataIdt, "yyyy-MM-dd");
                        Date periodo = DateHelper.parse(periodoFormatado, "yyyy-MM-dd");
                        serDataIdt = DateHelper.toSQLDate(periodo);
                    }
                }
                servidor.setSerDataIdt((java.sql.Date) serDataIdt);

                // Recupera os dados do cadastro do registro servidor
                RegistroServidorTO registroServidor = new RegistroServidorTO();

                registroServidor.setRseMatricula((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_MATRICULA, null, responsavel));
                registroServidor.setRseTipo((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_CATEGORIA, null, responsavel));
                registroServidor.setRseMunicipioLotacao((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_MUNICIPIO_LOTACAO, null, responsavel));
                registroServidor.setRseCLT((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_CLT, null, responsavel));

                registroServidor.setOrgCodigo((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_ORGAO, null, true, responsavel));
                registroServidor.setSrsCodigo((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_SITUACAO, null, true, responsavel));
                registroServidor.setVrsCodigo((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_VINCULO, null, true, responsavel));
                registroServidor.setCrsCodigo((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_CARGO, null, true, responsavel));
                registroServidor.setPrsCodigo((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_PADRAO, null, true, responsavel));
                registroServidor.setSboCodigo((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_SUB_ORGAO, null, true, responsavel));
                registroServidor.setUniCodigo((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_UNIDADE, null, true, responsavel));
                registroServidor.setPosCodigo((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_POSTO, null, true, responsavel));
                registroServidor.setTrsCodigo((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_TIPO_REG_SERVIDOR, null, true, responsavel));
                registroServidor.setCapCodigo((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_CAPACIDADE_CIVIL, null, true, responsavel));

                Object rsePrazo = JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_PRAZO, null, true, responsavel);
                registroServidor.setRsePrazo(rsePrazo != null ? Integer.valueOf(rsePrazo.toString()) : null);

                Object rseDataAdmissao = JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_DATA_ADMISSAO, null, true, responsavel);
                if (rseDataAdmissao instanceof String) {
                    registroServidor.setRseDataAdmissao(rseDataAdmissao != null ? new java.sql.Timestamp(DateHelper.parse((String) rseDataAdmissao, LocaleHelper.getDatePattern()).getTime()) : null);
                } else if (rseDataAdmissao == null) {
                    registroServidor.setRseDataAdmissao(null);
                }

                registroServidor.setRseBancoSal((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_BANCO, null, responsavel));
                registroServidor.setRseAgenciaSal((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_AGENCIA, null, responsavel));
                registroServidor.setRseContaSal((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_CONTA, null, responsavel));

                registroServidor.setRseBancoSalAlternativo((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_BANCO_ALTERNATIVO, null, responsavel));
                registroServidor.setRseAgenciaSalAlternativa((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_AGENCIA_ALTERNATIVA, null, responsavel));
                registroServidor.setRseContaSalAlternativa((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_CONTA_ALTERNATIVA, null, responsavel));

                registroServidor.setRseObs((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_OBSERVACAO, null, responsavel));
                registroServidor.setRseEstabilizado((String) JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_ESTABILIZADO, null, responsavel));

                Object dataFimEngajamento = JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_DATA_FIM_ENGAJAMENTO, null, true, responsavel);
                if (dataFimEngajamento instanceof String) {
                    registroServidor.setRseDataFimEngajamento(dataFimEngajamento != null ? new java.sql.Timestamp(DateHelper.parse((String) dataFimEngajamento, LocaleHelper.getDatePattern()).getTime()) : null);
                } else if (dataFimEngajamento == null) {
                    registroServidor.setRseDataFimEngajamento(null);
                }

                Object dataLimitePermanencia = JspHelper.getFieldValue(request, FieldKeysConstants.CADASTRAR_SERVIDOR_DATA_LIMITE_PERMANENCIA, null, true, responsavel);
                if (dataLimitePermanencia instanceof String) {
                    registroServidor.setRseDataLimitePermanencia(dataLimitePermanencia != null ? new java.sql.Timestamp(DateHelper.parse((String) dataLimitePermanencia, LocaleHelper.getDatePattern()).getTime()) : null);
                } else if (dataLimitePermanencia == null) {
                    registroServidor.setRseDataLimitePermanencia(null);
                }

                // Seta a margem para Zero
                registroServidor.setRseMargem(BigDecimal.ZERO);
                registroServidor.setRseMargemRest(BigDecimal.ZERO);
                registroServidor.setRseMargemUsada(BigDecimal.ZERO);

                if (TextHelper.isNull(registroServidor.getSrsCodigo())) {
                    if (ParamSist.paramEquals(CodedValues.TPC_ALTERA_STATUS_RSE_PARA_PENDENTE_NOVA_ADE, CodedValues.TPC_SIM, responsavel)) {
                        registroServidor.setSrsCodigo(CodedValues.SRS_PENDENTE);
                    } else {
                        registroServidor.setSrsCodigo(CodedValues.SRS_ATIVO);
                    }
                }

                // Salva os dados do servidor / registro servidor
                String serCodigo = servidorController.cadastrarServidor(servidor, registroServidor, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.servidor.cadastrado.sucesso", responsavel));

                TransferObject registroServidorIncluido = servidorController.getRegistroServidorPelaMatricula(serCodigo, registroServidor.getOrgCodigo(), null, registroServidor.getRseMatricula(), responsavel);
                String rseCodigo = (String) registroServidorIncluido.getAttribute(Columns.RSE_CODIGO);

                String link = TextHelper.encode64(SynchronizerToken.updateTokenInURL("../v3/consultarServidor?acao=consultar&RSE_CODIGO=" + rseCodigo, request));
                request.setAttribute("url64", link);
                return "jsp/redirecionador/redirecionar";
            }

            model.addAttribute("msgErro", msgErro);

            // Repassa o token salvo, pois o método irá revalidar o token
            request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

            return iniciar(request, response, session, model);

        } catch (NumberFormatException | ZetraException | ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());

            // Repassa o token salvo, pois o método irá revalidar o token
            request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));
            return iniciar(request, response, session, model);
        }
    }
}
