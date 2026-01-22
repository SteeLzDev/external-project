package com.zetra.econsig.web.controller.consignacao;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;

/**
 * <p>Title: IncluirConsignacaoWebController</p>
 * <p>Description: Controlador Web base para o casos de uso de inclusão de consignação.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/incluirConsignacao" })
public class IncluirConsignacaoWebController extends AbstractIncluirConsignacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(IncluirConsignacaoWebController.class);

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private ParametroController parametroController;

    @Override
    protected String getFunCodigo() {
        return CodedValues.FUN_INCLUIR_CONSIGNACAO;
    }

    @Override
    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (responsavel.isCsaCor()) {
            carregarListaOrgao(request, session, model, responsavel);
        }
        return super.iniciar(request, response, session, model);
    }

    @Override
    protected String validarServicoOperacao(String svcCodigo, String rseCodigo, Map<String, String> parametrosPlano, HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        if (TextHelper.isNull(svcCodigo)) {
            svcCodigo = request.getParameter("SVC_CODIGO");
        }
        if (TextHelper.isNull(svcCodigo)) {
            throw new ViewHelperException("mensagem.erro.servico.nao.informado", responsavel);
        }
        return svcCodigo;
    }

    @Override
    protected String tratarSevidorNaoEncontrado(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        // Repassa o token salvo, pois o método irá revalidar o token
        request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

        // Retorna para operação de inclusão de servidor
        return iniciarInclusaoServidor(request, response, session, model);
    }

    @RequestMapping(params = { "acao=iniciarInclusaoServidor" })
    public String iniciarInclusaoServidor(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        // Define mensagem de aviso que o servidor pesquisado não existe
        session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.erro.nenhum.servidor.encontrado.incluir.consignacao", responsavel));
        session.removeAttribute(CodedValues.MSG_ERRO);

        try {
            // Carrega lista de órgãos
            carregarListaOrgao(request, session, model, responsavel);

            String titulacao = ApplicationResourcesHelper.getMessage("rotulo.servidor.titulacao.valores", responsavel);
            List<String> listaTitulacao = new ArrayList<>();
            if (!TextHelper.isNull(titulacao)) {
                listaTitulacao = Arrays.asList(titulacao.split(",|;"));
                if (!listaTitulacao.isEmpty()) {
                    model.addAttribute("listaTitulacao", listaTitulacao);
                }
            }

            List<TransferObject> listaEstadoCivil = servidorController.getEstCivil(responsavel);
            if (listaEstadoCivil != null && !listaEstadoCivil.isEmpty()) {
                model.addAttribute("listaEstadoCivil", listaEstadoCivil);
            }

            List<TransferObject> listaNivelEscolaridade = servidorController.getNivelEscolaridade(responsavel);
            if (listaNivelEscolaridade != null && !listaNivelEscolaridade.isEmpty()) {
                model.addAttribute("listaNivelEscolaridade", listaNivelEscolaridade);
            }

            List<TransferObject> listaTipoHabitacao = servidorController.getTipoHabitacao(responsavel);
            if (listaTipoHabitacao != null && !listaTipoHabitacao.isEmpty()) {
                model.addAttribute("listaTipoHabitacao", listaTipoHabitacao);
            }

            // Busca lista com todos os status existentes para criar combo
            List<TransferObject> listaStatusRegistroServidor = servidorController.lstStatusRegistroServidor(true, true, responsavel);
            if (listaStatusRegistroServidor != null && !listaStatusRegistroServidor.isEmpty()) {
                model.addAttribute("listaStatusRegistroServidor", listaStatusRegistroServidor);
                model.addAttribute("srsCodigoPadrao", (ParamSist.paramEquals(CodedValues.TPC_ALTERA_STATUS_RSE_PARA_PENDENTE_NOVA_ADE, CodedValues.TPC_SIM, responsavel)) ? CodedValues.SRS_PENDENTE : CodedValues.SRS_ATIVO);
            }

            List<TransferObject> listaCargo = servidorController.lstCargo(responsavel);
            if (listaCargo != null && !listaCargo.isEmpty()) {
                model.addAttribute("listaCargo", listaCargo);
            }

            List<TransferObject> listaTipoRegServidor = servidorController.lstTipoRegistroServidor(responsavel);
            if (listaTipoRegServidor != null && !listaTipoRegServidor.isEmpty()) {
                model.addAttribute("listaTipoRegServidor", listaTipoRegServidor);
            }

            List<TransferObject> listaPosto = servidorController.lstPosto(responsavel);
            if (listaPosto != null && !listaPosto.isEmpty()) {
                model.addAttribute("listaPosto", listaPosto);
            }

            List<TransferObject> listaCapCivil = servidorController.lstCapacidadeCivil(responsavel);
            if (listaCapCivil != null && !listaCapCivil.isEmpty()) {
                model.addAttribute("listaCapCivil", listaCapCivil);
            }

            List<TransferObject> listaVincRegSer = servidorController.selectVincRegistroServidor(true, responsavel);
            if (listaVincRegSer != null && !listaVincRegSer.isEmpty()) {
                model.addAttribute("listaVincRegSer", listaVincRegSer);
            }

            List<TransferObject> listaPadraoRegSer = servidorController.lstPadrao(responsavel);
            if (listaPadraoRegSer != null && !listaPadraoRegSer.isEmpty()) {
                model.addAttribute("listaPadraoRegSer", listaPadraoRegSer);
            }

            String listaCampos = getCamposObrigatorios(request, responsavel);
            model.addAttribute("listaCampos", listaCampos);

            String listaMensagens = getMensagemCamposObrigatorios(responsavel);
            model.addAttribute("listaMensagens", listaMensagens);

        } catch (ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/reservarMargem/incluirServidor", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=incluirServidorParaReserva" })
    public String incluirServidorParaReserva(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            String listaCampos = getCamposObrigatorios(request, responsavel);
            String msgErro = JspHelper.verificaCamposForm(request, session, listaCampos, ApplicationResourcesHelper.getMessage("mensagem.campos.obrigatorios", responsavel), "100%");
            if (msgErro.length() > 0) {
                model.addAttribute("msgErro", msgErro);

                // Repassa o token salvo, pois o método irá revalidar o token
                request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

                return iniciarInclusaoServidor(request, response, session, model);
            }

            // Recupera os dados do cadastro do servidor
            ServidorTransferObject servidor = new ServidorTransferObject();

            servidor.setSerCpf(JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CPF));
            servidor.setSerNomePai(JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_PAI));
            servidor.setSerNomeMae(JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_MAE));

            Object serDataNasc = JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_NASCIMENTO);
            if (!TextHelper.isNull(serDataNasc) && serDataNasc instanceof String) {
                servidor.setSerDataNasc(DateHelper.toSQLDate(DateHelper.parse((String) serDataNasc, LocaleHelper.getDatePattern())));
            }

            Object qtdFilhos = JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_QTD_FILHOS);
            if (!TextHelper.isNull(qtdFilhos) && qtdFilhos instanceof String) {
                servidor.setSerQtdFilhos(Short.valueOf(qtdFilhos.toString()));
            }

            servidor.setSerPrimeiroNome(JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRIMEIRO_NOME));
            servidor.setSerNomeMeio(JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MEIO_NOME));
            servidor.setSerUltimoNome(JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ULTIMO_NOME));
            servidor.setSerTitulacao(JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TITULACAO));
            servidor.setSerNacionalidade(JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NACIONALIDADE));
            servidor.setSerSexo(JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SEXO));
            servidor.setSerEstCivil(JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTADO_CIVIL));
            servidor.setNesCodigo(JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NIVEL_ESCOLARIDADE));
            servidor.setThaCodigo(JspHelper.verificaVarQryStr(request,  FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TIPO_HABITACAO));
            servidor.setSerNroIdt(JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_IDENTIDADE));
            servidor.setSerEmissorIdt(JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_EMISSOR_IDENTIDADE));
            servidor.setSerUfIdt(JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF_IDENTIDADE));
            servidor.setSerCartProf(JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NUM_CARTEIRA_TRABALHO));
            servidor.setSerPis(JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NUM_PIS));
            servidor.setSerEnd(JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_LOGRADOURO));
            servidor.setSerCompl(JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_COMPLEMENTO));
            servidor.setSerBairro(JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BAIRRO));
            servidor.setSerCidade(JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CIDADE));
            servidor.setSerUf(JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF));
            servidor.setSerCep(JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CEP));
            servidor.setSerUfNasc(JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF_NASC));
            servidor.setSerCidNasc(JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CIDADE_NASC));
            servidor.setSerNomeConjuge(JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_CONJUGE));

            String serNome = (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME))) ? (String) JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME) : JspHelper.montaSerNome(servidor.getSerTitulacao(), servidor.getSerPrimeiroNome(), servidor.getSerNomeMeio(), servidor.getSerUltimoNome());
            servidor.setSerNome(serNome);

            String serTelDdd = JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DDD_TELEFONE);
            String serTel = JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TELEFONE);
            serTel = TextHelper.dropSeparator(TextHelper.dropBlankSpace(serTel));
            if (TextHelper.isNull(serTelDdd)) {
                servidor.setSerTel(serTel);
            } else {
                servidor.setSerTel(serTelDdd + "-" + serTel);
            }

            String serCelularDdd = JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DDD_CELULAR);
            String serCelular = JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CELULAR);
            serCelular = TextHelper.dropSeparator(TextHelper.dropBlankSpace(serCelular));
            if (TextHelper.isNull(serCelular)) {
                servidor.setSerCelular("");
            } else {
                if (TextHelper.isNull(serCelularDdd)) {
                    servidor.setSerCelular(serCelular);
                } else {
                    servidor.setSerCelular(serCelularDdd + "-" + serCelular);
                }
            }

            servidor.setSerNro(JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NRO));
            servidor.setSerEmail(JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_EMAIL));

            Object serDataIdt = JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_IDENTIDADE);
            if (!TextHelper.isNull(serDataIdt) && serDataIdt instanceof String) {
                try {
                    serDataIdt = DateHelper.toSQLDate(DateHelper.parse((String) serDataIdt, LocaleHelper.getDatePattern()));
                } catch (Exception e) {
                    String periodoFormatado = DateHelper.format((Date) serDataIdt, "yyyy-MM-dd");
                    Date periodo = DateHelper.parse(periodoFormatado, "yyyy-MM-dd");
                    serDataIdt = DateHelper.toSQLDate(periodo);
                }
                servidor.setSerDataIdt((java.sql.Date) serDataIdt);
            }

            servidor.setSerDataAlteracao(new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis()));

            // Recupera os dados do cadastro do registro servidor
            RegistroServidorTO registroServidor = new RegistroServidorTO();

            registroServidor.setRseTipo((String) JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CATEGORIA, registroServidor.getRseTipo(), responsavel));
            registroServidor.setRseCLT((String) JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CLT, registroServidor.getRseCLT(), responsavel));
            registroServidor.setSrsCodigo((String) JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SITUACAO, registroServidor.getSrsCodigo(), responsavel));

            if (TextHelper.isNull(registroServidor.getSrsCodigo())) {
                if (ParamSist.paramEquals(CodedValues.TPC_ALTERA_STATUS_RSE_PARA_PENDENTE_NOVA_ADE, CodedValues.TPC_SIM, responsavel)) {
                    registroServidor.setSrsCodigo(CodedValues.SRS_PENDENTE);
                } else {
                    registroServidor.setSrsCodigo(CodedValues.SRS_ATIVO);
                }
            }

            registroServidor.setRseEstabilizado((String) JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTABILIZADO, registroServidor.getRseEstabilizado(), responsavel));
            registroServidor.setVrsCodigo((String) JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_VINCULO, registroServidor.getVrsCodigo(), true, responsavel));
            registroServidor.setCrsCodigo((String) JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CARGO, registroServidor.getCrsCodigo(), true, responsavel));
            registroServidor.setPrsCodigo((String) JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PADRAO, registroServidor.getPrsCodigo(), true, responsavel));
            //registroServidor.setSboCodigo((String) JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SUB_ORGAO, registroServidor.getSboCodigo(), true, responsavel));
            //registroServidor.setUniCodigo((String) JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UNIDADE, registroServidor.getUniCodigo(), true, responsavel));
            registroServidor.setRseMunicipioLotacao((String) JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MUNICIPIO_LOTACAO, registroServidor.getRseMunicipioLotacao(), true, responsavel));

            Object rsePrazo = JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRAZO, registroServidor.getRsePrazo(), true, responsavel);
            registroServidor.setRsePrazo(rsePrazo != null ? Integer.valueOf(rsePrazo.toString()) : null);

            Object rseDataAdmissao = JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_ADMISSAO, registroServidor.getRseDataAdmissao(), true, responsavel);
            if (rseDataAdmissao instanceof String) {
                registroServidor.setRseDataAdmissao(rseDataAdmissao != null ? new java.sql.Timestamp(DateHelper.parse((String) rseDataAdmissao, LocaleHelper.getDatePattern()).getTime()) : null);
            } else if (rseDataAdmissao == null) {
                registroServidor.setRseDataAdmissao(null);
            }

            registroServidor.setRseBancoSal((String) JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO, registroServidor.getRseBancoSal(), true, responsavel));
            registroServidor.setRseAgenciaSal((String) JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_AGENCIA, registroServidor.getRseAgenciaSal(), true, responsavel));
            registroServidor.setRseContaSal((String) JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CONTA, registroServidor.getRseContaSal(), true, responsavel));

            registroServidor.setRseBancoSalAlternativo((String) JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO_ALTERNATIVO, registroServidor.getRseBancoSalAlternativo(), true, responsavel));
            registroServidor.setRseAgenciaSalAlternativa((String) JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_AGENCIA_ALTERNATIVA, registroServidor.getRseAgenciaSalAlternativa(), true, responsavel));
            registroServidor.setRseContaSalAlternativa((String) JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CONTA_ALTERNATIVA, registroServidor.getRseContaSalAlternativa(), true, responsavel));

            registroServidor.setPosCodigo((String) JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_POSTO, registroServidor.getPosCodigo(), true, responsavel));
            registroServidor.setTrsCodigo((String) JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TIPO_REG_SERVIDOR, registroServidor.getTrsCodigo(), true, responsavel));

            Object dataFimEngajamento = JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_FIM_ENGAJAMENTO, registroServidor.getRseDataFimEngajamento(), true, responsavel);
            if (dataFimEngajamento instanceof String) {
                registroServidor.setRseDataFimEngajamento(dataFimEngajamento != null ? new java.sql.Timestamp(DateHelper.parse((String) dataFimEngajamento, LocaleHelper.getDatePattern()).getTime()) : null);
            } else if (dataFimEngajamento instanceof java.sql.Timestamp) {
                registroServidor.setRseDataFimEngajamento(dataFimEngajamento != null ? (java.sql.Timestamp) dataFimEngajamento : null);
            } else if (dataFimEngajamento == null) {
                registroServidor.setRseDataFimEngajamento(null);
            }

            Object dataLimitePermanencia = JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_LIMITE_PERMANENCIA, registroServidor.getRseDataLimitePermanencia(), true, responsavel);
            if (dataLimitePermanencia instanceof String) {
                registroServidor.setRseDataLimitePermanencia(dataLimitePermanencia != null ? new java.sql.Timestamp(DateHelper.parse((String) dataLimitePermanencia, LocaleHelper.getDatePattern()).getTime()) : null);
            } else if (dataLimitePermanencia instanceof java.sql.Timestamp) {
                registroServidor.setRseDataLimitePermanencia(dataLimitePermanencia != null ? (java.sql.Timestamp) dataLimitePermanencia : null);
            } else if (dataLimitePermanencia == null) {
                registroServidor.setRseDataLimitePermanencia(null);
            }

            registroServidor.setCapCodigo((String) JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CAPACIDADE_CIVIL, registroServidor.getCapCodigo(), true, responsavel));

            // Seta observações sobre o servidor
            registroServidor.setRseObs((String) JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_OBSERVACAO, registroServidor.getRseObs(), true, responsavel));

            // Salva as praças do registro servidor
            String rsePraca = (String) JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRACA, registroServidor.getRsePraca(), true, responsavel);
            if (!TextHelper.isNull(rsePraca)) {
                rsePraca = rsePraca.replaceAll("\r\n", ";").replaceAll("\r", ";").replaceAll("\n", ";").replaceAll(",", ";").replaceAll(";;", ";");
            }
            registroServidor.setRsePraca(rsePraca);

            registroServidor.setRseMatricula((String) JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MATRICULA, registroServidor.getRseMatricula(), responsavel));
            registroServidor.setOrgCodigo((String) JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ORGAO, registroServidor.getOrgCodigo(), responsavel));

            //seta rse_margem e rse_margem_rest como 0 pois não podem ser null
            registroServidor.setRseMargem(BigDecimal.ZERO);
            registroServidor.setRseMargemRest(BigDecimal.ZERO);
            registroServidor.setRseMargemUsada(BigDecimal.ZERO);

            registroServidor.setRseSalario(!TextHelper.isNull(JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SALARIO, registroServidor.getRseSalario(), responsavel)) ? new BigDecimal(NumberHelper.reformat((String) JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SALARIO, registroServidor.getRseSalario(), responsavel), NumberHelper.getLang(), "en")) : null);
            registroServidor.setRseProventos(!TextHelper.isNull(JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PROVENTOS, registroServidor.getRseProventos(), responsavel)) ? new BigDecimal(NumberHelper.reformat((String) JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PROVENTOS, registroServidor.getRseProventos(), responsavel), NumberHelper.getLang(), "en")) : null);

            // Seta código do usuário que está alterando e a data de alteração
            registroServidor.setUsuCodigo(responsavel.getUsuCodigo());
            registroServidor.setRseDataAlteracao(new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis()));

            registroServidor.setRseDescontosComp(!TextHelper.isNull(JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DESCONTOS_COMPULSORIOS, registroServidor.getRseDescontosComp(), responsavel)) ? new BigDecimal(NumberHelper.reformat((String) JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DESCONTOS_COMPULSORIOS, registroServidor.getRseDescontosComp(), responsavel), NumberHelper.getLang(), "en")) : null);
            registroServidor.setRseDescontosFacu(!TextHelper.isNull(JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DESCONTOS_FACULTATIVOS, registroServidor.getRseDescontosFacu(), responsavel)) ? new BigDecimal(NumberHelper.reformat((String) JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DESCONTOS_FACULTATIVOS, registroServidor.getRseDescontosFacu(), responsavel), NumberHelper.getLang(), "en")) : null);
            registroServidor.setRseOutrosDescontos(!TextHelper.isNull(JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_OUTROS_DESCONTOS, registroServidor.getRseOutrosDescontos(), responsavel)) ? new BigDecimal(NumberHelper.reformat((String) JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_OUTROS_DESCONTOS, registroServidor.getRseOutrosDescontos(), responsavel), NumberHelper.getLang(), "en")) : null);
            registroServidor.setRseAssociado((String) JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ASSOCIADO, registroServidor.getRseAssociado(), true, responsavel));
            registroServidor.setRseBaseCalculo(!TextHelper.isNull(JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BASE_CALCULO, registroServidor.getRseBaseCalculo(), responsavel)) ? new BigDecimal(NumberHelper.reformat((String) JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BASE_CALCULO, registroServidor.getRseBaseCalculo(), responsavel), NumberHelper.getLang(), "en")) : null);
            registroServidor.setRseMatriculaInst((String) JspHelper.getFieldValue(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MATRICULA_INSTITUCIONAL, registroServidor.getRseMatriculaInst(), true, responsavel));

            Object rseDataContracheque = JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_CONTRACHEQUE);
            if (!TextHelper.isNull(rseDataContracheque) && rseDataContracheque instanceof String) {
                registroServidor.setRseDataContracheque(rseDataContracheque != null ? DateHelper.toSQLDate(DateHelper.parse((String) rseDataContracheque, LocaleHelper.getDatePattern())) : null);
            }

            // Salva os dados do servidor / registro servidor
            String serCodigo = servidorController.cadastrarServidor(servidor, registroServidor, responsavel);

            // Busca a matricula que foi salva no caso de matrícula numérica inciiada com zero
            if (ParamSist.paramEquals(CodedValues.TPC_MATRICULA_NUMERICA, CodedValues.TPC_SIM, responsavel)) {
                Long matricula = Long.valueOf(registroServidor.getRseMatricula());
                if (matricula <= 0) {
                    throw new ServidorControllerException("mensagem.erro.matricula.invalida", responsavel);
                }
                registroServidor.setRseMatricula(matricula.toString());
            }

            TransferObject registroServiorIncluido = servidorController.getRegistroServidorPelaMatricula(serCodigo, registroServidor.getOrgCodigo(), null, registroServidor.getRseMatricula(), responsavel);
            String rseCodigo = (String) registroServiorIncluido.getAttribute(Columns.RSE_CODIGO);

            // Repassa o token salvo, pois o método irá revalidar o token
            request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

            // Volta um elemento no topo da pilha para que ao voltar, o usuário caia no início da operação
            ParamSession paramSession = ParamSession.getParamSession(session);
            paramSession.halfBack();

            // Se o serviço já foi informado, então está vindo do fluxo de reserva de margem
            String svcCodigo = request.getParameter("SVC_CODIGO");
            if (!TextHelper.isNull(svcCodigo)) {
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.informacao.prosseguir.incluir.consignacao", responsavel));
                return reservarMargem(rseCodigo, request, response, session, model);
            } else {
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.servidor.cadastrado.sucesso", responsavel));
                // Se ainda não tem serviço, está vindo do fluxo de pesquisa de servidor. Retorna para a listagem
                return "forward:/v3/pesquisarServidor?acao=pesquisar&RSE_CODIGO=" + rseCodigo;
            }
        } catch (NumberFormatException | ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } catch (ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.incluir.consignacao.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/incluirConsignacao");
        model.addAttribute("tipoOperacao", "incluir");
    }

    private String getCamposObrigatorios(HttpServletRequest request, AcessoSistema responsavel) throws ZetraException {
        String listaCampos = (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TITULACAO, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TITULACAO : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRIMEIRO_NOME, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRIMEIRO_NOME : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME : "")
                           + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MEIO_NOME, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MEIO_NOME : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ULTIMO_NOME, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ULTIMO_NOME : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CPF, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CPF : "")
                           + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_PAI, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_PAI : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_MAE, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_MAE : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SEXO, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SEXO : "")
                           + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_NASCIMENTO, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_NASCIMENTO : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_IDENTIDADE, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_IDENTIDADE : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTADO_CIVIL, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTADO_CIVIL : "")
                           + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NACIONALIDADE, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NACIONALIDADE : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_LOGRADOURO, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_LOGRADOURO : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BAIRRO, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BAIRRO : "")
                           + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CIDADE, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CIDADE : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CEP, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CEP : "")
                           + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TELEFONE, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TELEFONE : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CELULAR, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CELULAR : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_EMISSOR_IDENTIDADE, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_EMISSOR_IDENTIDADE : "")
                           + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF_IDENTIDADE, responsavel) && ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_EMISSOR_IDENTIDADE, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF_IDENTIDADE : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NUM_CARTEIRA_TRABALHO, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NUM_CARTEIRA_TRABALHO : "")
                           + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NUM_PIS, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NUM_PIS : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_COMPLEMENTO, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_COMPLEMENTO : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DDD_TELEFONE, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DDD_TELEFONE : "")
                           + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NRO, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NRO : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DDD_CELULAR, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DDD_CELULAR : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_EMAIL, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_EMAIL : "")
                           + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_IDENTIDADE, responsavel) && ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_EMISSOR_IDENTIDADE, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_IDENTIDADE : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF_NASC, responsavel) && ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF_NASC, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF_NASC : "")
                           + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CIDADE_NASC, responsavel) && ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CIDADE_NASC, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CIDADE_NASC : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_CONJUGE, responsavel) && ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_CONJUGE, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_CONJUGE : "")
                           + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PADRAO, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PADRAO : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_POSTO, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_POSTO : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TIPO_REG_SERVIDOR, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TIPO_REG_SERVIDOR : "")
                           + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTABILIZADO, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTABILIZADO : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_FIM_ENGAJAMENTO, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_FIM_ENGAJAMENTO : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_LIMITE_PERMANENCIA, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_LIMITE_PERMANENCIA : "")
                           + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CAPACIDADE_CIVIL, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CAPACIDADE_CIVIL : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO_ALTERNATIVO, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO_ALTERNATIVO : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_AGENCIA_ALTERNATIVA, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_AGENCIA_ALTERNATIVA : "")
                           + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CONTA_ALTERNATIVA, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CONTA_ALTERNATIVA : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CATEGORIA, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CATEGORIA : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CARGO, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CARGO : "")
                           + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CLT, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CLT : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_ADMISSAO, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_ADMISSAO : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRAZO, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRAZO : "")
                           + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_VINCULO, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_VINCULO : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MATRICULA, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MATRICULA : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ORGAO, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ORGAO : "")
                           + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SITUACAO, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SITUACAO : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_AGENCIA, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_AGENCIA : "")
                           + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CONTA, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CONTA : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_OBSERVACAO, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_OBSERVACAO : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MUNICIPIO_LOTACAO, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MUNICIPIO_LOTACAO : "")
                           + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SALARIO, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SALARIO : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PROVENTOS, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PROVENTOS : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRACA, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRACA : "")
                           + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DESCONTOS_COMPULSORIOS, responsavel) && ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DESCONTOS_COMPULSORIOS, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DESCONTOS_COMPULSORIOS : "")
                           + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DESCONTOS_FACULTATIVOS, responsavel) && ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DESCONTOS_FACULTATIVOS, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DESCONTOS_FACULTATIVOS : "")
                           + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_OUTROS_DESCONTOS, responsavel) && ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_OUTROS_DESCONTOS, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_OUTROS_DESCONTOS : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ASSOCIADO, responsavel) && ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ASSOCIADO, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ASSOCIADO : "")
                           + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BASE_CALCULO, responsavel) && ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BASE_CALCULO, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BASE_CALCULO : "")
                           + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MATRICULA_INSTITUCIONAL, responsavel) && ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MATRICULA_INSTITUCIONAL, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MATRICULA_INSTITUCIONAL : "")
                           + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_CONTRACHEQUE, responsavel) && ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_CONTRACHEQUE, responsavel) ? "|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_CONTRACHEQUE : "");

        listaCampos = listaCampos.charAt(0) == '|' ? listaCampos.substring(1, listaCampos.length()) : listaCampos;
        if (TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTABILIZADO)).equals("S")) {
            listaCampos = listaCampos.replaceAll("\\|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_FIM_ENGAJAMENTO, "");
            listaCampos = listaCampos.replaceAll("\\|" + FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_LIMITE_PERMANENCIA, "");
        }

        return listaCampos;
    }

    private String getMensagemCamposObrigatorios(AcessoSistema responsavel) throws ZetraException {
        String listaMensagens = (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TITULACAO, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.tratamento.nome", responsavel) : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRIMEIRO_NOME, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.nome", responsavel) : "")
                              + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.nome", responsavel) : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MEIO_NOME, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.meio.nome", responsavel) : "")
                              + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ULTIMO_NOME, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.ultimo.nome", responsavel) : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CPF, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.cpf", responsavel) : "")
                              + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_PAI, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.nome.pai", responsavel) : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_MAE, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.nome.mae", responsavel) : "")
                              + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SEXO, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.sexo", responsavel) : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_NASCIMENTO, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.data.nascimento", responsavel) : "")
                              + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_IDENTIDADE, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.identidade", responsavel) : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTADO_CIVIL, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.estado.civil", responsavel) : "")
                              + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NACIONALIDADE, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.nacionalidade", responsavel) : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_LOGRADOURO, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.logradouro", responsavel) : "")
                              + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BAIRRO, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.bairro", responsavel) : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CIDADE, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.cidade", responsavel) : "")
                              + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.estado", responsavel) : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CEP, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.cep", responsavel) : "")
                              + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TELEFONE, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.telefone", responsavel) : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CELULAR, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.celular", responsavel) : "")
                              + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_EMISSOR_IDENTIDADE, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.emissor.identidade", responsavel) : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF_IDENTIDADE, responsavel) && ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_EMISSOR_IDENTIDADE, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.uf.identidade", responsavel) : "")
                              + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NUM_CARTEIRA_TRABALHO, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.cart.trabalho", responsavel) : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NUM_PIS, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.pis", responsavel) : "")
                              + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_COMPLEMENTO, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.complemento", responsavel) : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DDD_TELEFONE, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.ddd.telefone", responsavel) : "")
                              + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NRO, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.numero", responsavel) : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DDD_CELULAR, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.ddd.celular", responsavel) : "")
                              + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_EMAIL, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.email", responsavel) : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_IDENTIDADE, responsavel) && ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_EMISSOR_IDENTIDADE, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.data.emissao.identidade", responsavel) : "")
                              + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF_NASC, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.uf.nascimento", responsavel) : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CIDADE_NASC, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.cidade.nascimento", responsavel) : "")
                              + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_CONJUGE, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.nome.conjuge", responsavel) : "")
                              + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PADRAO, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.padrao", responsavel) : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_POSTO, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.posto", responsavel) : "")
                              + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TIPO_REG_SERVIDOR, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.tipo", responsavel) : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTABILIZADO, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.estabilizado", responsavel) : "")
                              + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_FIM_ENGAJAMENTO, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.engajado", responsavel) : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_LIMITE_PERMANENCIA, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.data.limite.permanencia", responsavel) : "")
                              + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CAPACIDADE_CIVIL, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.capacidade.civil", responsavel) : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO_ALTERNATIVO, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.banco.alternativo", responsavel) : "")
                              + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_AGENCIA_ALTERNATIVA, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.agencia.alternativa", responsavel) : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CONTA_ALTERNATIVA, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.conta.alternativa", responsavel) : "")
                              + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CATEGORIA, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.categoria", responsavel) : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CARGO, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.cargo", responsavel) : "")
                              + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CLT, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.sindicalizado", responsavel) : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_ADMISSAO, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.data.admissao", responsavel) : "")
                              + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRAZO, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.prazo", responsavel) : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_VINCULO, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.vinculo", responsavel) : "")
                              + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MATRICULA, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.matricula", responsavel) : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ORGAO, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.orgao", responsavel) : "")
                              + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SITUACAO, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.situacao", responsavel) : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.banco", responsavel) : "")
                              + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_AGENCIA, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.agencia", responsavel) : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CONTA, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.conta", responsavel) : "")
                              + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_OBSERVACAO, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.obs", responsavel) : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MUNICIPIO_LOTACAO, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.municipio", responsavel) : "")
                              + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SALARIO, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.salario", responsavel) : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PROVENTOS, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.proventos", responsavel) : "")
                              + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRACA, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.praca", responsavel) : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DESCONTOS_COMPULSORIOS, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.descontos.compulsorios", responsavel) : "")
                              + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DESCONTOS_FACULTATIVOS, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.descontos.facultativos", responsavel) : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_OUTROS_DESCONTOS, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.outros.descontos", responsavel) : "")
                              + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ASSOCIADO, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.associado", responsavel) : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BASE_CALCULO, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.base.calculo", responsavel) : "")
                              + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MATRICULA_INSTITUCIONAL, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.matricula.institucional", responsavel) : "") + (ShowFieldHelper.isRequired(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_CONTRACHEQUE, responsavel) ? "|" + ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.data.contracheque", responsavel) : "");

        listaMensagens = listaMensagens.charAt(0) == '|' ? listaMensagens.substring(1, listaMensagens.length()) : listaMensagens;

        return listaMensagens;
    }

    @Override
    protected void validarValoresObrigatorios(HttpServletRequest request, String rseCodigo, String csaCodigo, String orgCodigo, String cnvCodigo, String svcCodigo, Object adeValor, AcessoSistema responsavel) throws ViewHelperException {

        boolean celularObrigatorio = false;
        boolean enderecoObrigatorio = false;
        boolean enderecoCelularObrigatorio = false;

        List<String> tpsCodigos = new ArrayList<>();
        tpsCodigos.add(CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO);
        // DESENV-17017 - Por decisão do setor de segurança (LGPD) essa implementação não deve ser liberada, por isso os valores são setados como false
        List<TransferObject> paramSvcCsa;
        try {
            paramSvcCsa = parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, tpsCodigos, false, responsavel);
            for (TransferObject param2 : paramSvcCsa) {
                CustomTransferObject param = (CustomTransferObject) param2;
                if (param != null && param.getAttribute(Columns.PSC_VLR) != null) {
                    if (param.getAttribute(Columns.TPS_CODIGO).equals(CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO)){
                        String pscVlr = (!param.getAttribute(Columns.PSC_VLR).toString().isEmpty()) ? param.getAttribute(Columns.PSC_VLR).toString() : "";
                        if(pscVlr.equals("E")) {
                           enderecoObrigatorio = true;
                        } else if (pscVlr.equals("C")) {
                           celularObrigatorio = true;
                        } else if (pscVlr.equals("EC")) {
                            enderecoCelularObrigatorio = true;
                        }
                    }
                }
            }

            if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_TELEFONE, responsavel) || (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_TELEFONE, responsavel) && (celularObrigatorio|| enderecoCelularObrigatorio)) && TextHelper.isNull(request.getParameter("SER_TEL"))) {
               throw new ViewHelperException("mensagem.informe.servidor.telefone",responsavel);
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_LOGRADOURO, responsavel)|| (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_LOGRADOURO, responsavel)&& (enderecoObrigatorio|| enderecoCelularObrigatorio)) && TextHelper.isNull(request.getParameter("SER_END"))) {
                throw new ViewHelperException("mensagem.informe.servidor.logradouro",responsavel);
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NRO, responsavel)|| (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NRO, responsavel) && (enderecoObrigatorio || enderecoCelularObrigatorio)) && TextHelper.isNull(request.getParameter("SER_NRO"))) {
                throw new ViewHelperException("mensagem.informe.servidor.numero",responsavel);
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_COMPLEMENTO, responsavel)|| (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_COMPLEMENTO, responsavel)&& (enderecoObrigatorio|| enderecoCelularObrigatorio)) && TextHelper.isNull(request.getParameter("SER_COMPL"))) {
                throw new ViewHelperException("mensagem.informe.servidor.complemento",responsavel);
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_BAIRRO, responsavel)|| (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_BAIRRO, responsavel)&& (enderecoObrigatorio|| enderecoCelularObrigatorio)) && TextHelper.isNull(request.getParameter("SER_BAIRRO"))) {
                throw new ViewHelperException("mensagem.informe.servidor.bairro",responsavel);
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CIDADE, responsavel)|| (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CIDADE, responsavel)&& (enderecoObrigatorio|| enderecoCelularObrigatorio)) && TextHelper.isNull(request.getParameter("SER_CIDADE"))) {
                throw new ViewHelperException("mensagem.informe.servidor.cidade",responsavel);
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CEP, responsavel)|| (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CEP, responsavel)&& (enderecoObrigatorio|| enderecoCelularObrigatorio)) && TextHelper.isNull(request.getParameter("SER_CEP"))) {
                throw new ViewHelperException("mensagem.informe.servidor.cep",responsavel);
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_UF, responsavel)|| (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_UF, responsavel)&& (enderecoObrigatorio|| enderecoCelularObrigatorio)) && TextHelper.isNull(request.getParameter("SER_UF"))) {
                throw new ViewHelperException("mensagem.informe.servidor.estado",responsavel);
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CELULAR, responsavel)|| (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CELULAR, responsavel)&& (celularObrigatorio|| enderecoCelularObrigatorio)) && TextHelper.isNull(request.getParameter("SER_CEL"))) {
                throw new ViewHelperException("mensagem.informe.servidor.celular",responsavel);
            }
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException(ex.getMessage(),responsavel);
        }
        super.validarValoresObrigatorios(request, rseCodigo, csaCodigo, orgCodigo, cnvCodigo, svcCodigo, adeValor, responsavel);
    }
}
