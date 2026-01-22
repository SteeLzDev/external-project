package com.zetra.econsig.web.controller.servidor;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.NotificacaoUsuario;
import com.zetra.econsig.persistence.entity.NotificacaoUsuarioId;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.notificacao.NotificacaoUsuarioController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.sistema.TipoMotivoOperacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.values.TipoNotificacaoEnum;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: AbstractServidorWebController</p>
 * <p>Description: Controlador Web base para casos de uso que manipulam dados do Servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class AbstractServidorWebController extends ControlePaginacaoWebController {

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Autowired
    private TipoMotivoOperacaoController tipoMotivoOperacaoController;

    protected void recuperarDadosServidor(ServidorTransferObject servidor, HttpServletRequest request, NotificacaoUsuarioController notificacaoUsuarioController, AcessoSistema responsavel) throws ZetraException, ParseException, InstantiationException, IllegalAccessException {
        if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_EDITAR_CPF_SERVIDOR, CodedValues.TPC_SIM, responsavel) &&
                !ParamSist.paramEquals(CodedValues.TPC_OMITE_CPF_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
            servidor.setSerCpf((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_CPF, servidor.getSerCpf(), responsavel));
        }

        final Object serDataNasc = JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_DATA_NASCIMENTO, servidor.getSerDataNasc(), true, responsavel);
        if (serDataNasc instanceof String) {
            servidor.setSerDataNasc(serDataNasc != null ? DateHelper.toSQLDate(DateHelper.parse((String) serDataNasc, LocaleHelper.getDatePattern())) : null);
        }

        final Object qtdFilhos = JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_QTD_FILHOS, servidor.getSerQtdFilhos(), responsavel);
        if (qtdFilhos instanceof String) {
            servidor.setSerQtdFilhos(!TextHelper.isNull(qtdFilhos) ? Short.valueOf(JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_QTD_FILHOS, servidor.getSerQtdFilhos(), responsavel).toString()) : null);
        }

        servidor.setSerPrimeiroNome((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_PRIMEIRO_NOME, servidor.getSerPrimeiroNome(), responsavel));
        servidor.setSerTitulacao((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_TITULACAO, servidor.getSerTitulacao(), responsavel));
        servidor.setSerNomeMeio((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_NOME_MEIO, servidor.getSerNomeMeio(), responsavel));
        servidor.setSerUltimoNome((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_ULTIMO_NOME, servidor.getSerUltimoNome(), responsavel));
        servidor.setSerNomePai((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_NOME_PAI, servidor.getSerNomePai(), responsavel));
        servidor.setSerNomeMae((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_NOME_MAE, servidor.getSerNomeMae(), responsavel));
        servidor.setSerNomeConjuge((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_NOME_CONJUGE, servidor.getSerNomeConjuge(), responsavel));
        servidor.setSerSexo((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_SEXO, servidor.getSerSexo(), responsavel));
        servidor.setNesCodigo((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_NIVEL_ESCOLARIDADE, servidor.getNesCodigo(), responsavel));
        servidor.setThaCodigo((String) JspHelper.getFieldValue(request,  FieldKeysConstants.EDT_SERVIDOR_TIPO_HABITACAO, servidor.getThaCodigo(), responsavel));
        servidor.setSerNacionalidade((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_NACIONALIDADE, servidor.getSerNacionalidade(), responsavel));
        servidor.setSerSexo((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_SEXO, servidor.getSerSexo(), responsavel));
        servidor.setSerEstCivil((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_ESTADO_CIVIL, servidor.getSerEstCivil(), responsavel));
        servidor.setSerNroIdt((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_NO_IDENTIDADE, servidor.getSerNroIdt(), responsavel));
        servidor.setSerEmissorIdt((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_EMISSOR_IDENTIDADE, servidor.getSerEmissorIdt(), responsavel));
        servidor.setSerUfIdt((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_UF_IDENTIDADE, servidor.getSerUfIdt(), responsavel));
        servidor.setSerCartProf((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_NUM_CARTEIRA_TRABALHO, servidor.getSerCartProf(), responsavel));
        servidor.setSerPis((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_NUM_PIS, servidor.getSerPis(), responsavel));
        servidor.setSerEnd((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_LOGRADOURO, servidor.getSerEnd(), responsavel));
        servidor.setSerCompl((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_COMPLEMENTO, servidor.getSerCompl(), responsavel));
        servidor.setSerBairro((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_BAIRRO, servidor.getSerBairro(), responsavel));
        servidor.setSerCidade((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_CIDADE, servidor.getSerCidade(), responsavel));
        servidor.setSerUf((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_UF, servidor.getSerUf(), responsavel));
        servidor.setSerCep((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_CEP, servidor.getSerCep(), responsavel));
        servidor.setSerDeficienteVisual((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_DEFICIENTE_VISUAL, servidor.getSerDeficienteVisual(), responsavel));
        servidor.setSerAcessaHostaHost((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_ACESSA_HOST_A_HOST, servidor.getSerAcessaHostaHost(), responsavel));

        String serNome = servidor.getSerNome();
        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_NOME, responsavel)) {
            serNome = (String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_NOME, serNome, responsavel);
        } else if (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_PRIMEIRO_NOME, responsavel)) {
            serNome = JspHelper.montaSerNome(servidor.getSerTitulacao(), servidor.getSerPrimeiroNome(), servidor.getSerNomeMeio(), servidor.getSerUltimoNome());
        }
        if (!TextHelper.isNull(serNome)) {
            servidor.setSerNome(serNome);
        }

        // Quebra o telefone em DDD + número.
        String serTel = "", serTelDdd = "";
        serTel = ((servidor != null) && (servidor.getSerTel() != null)) ? TextHelper.dropSeparator(servidor.getSerTel()) : "";
        if (serTel.length() == 10) {
            serTelDdd = serTel.substring(0, 2);
            serTel = serTel.substring(2, serTel.length());
        }

        serTelDdd = (String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_DDD_TELEFONE, serTelDdd, responsavel);
        serTel = (String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_TELEFONE, serTel, responsavel);
        serTel = TextHelper.dropSeparator(TextHelper.dropBlankSpace(serTel));
        if (TextHelper.isNull(serTelDdd)) {
            servidor.setSerTel(serTel);
        } else {
            servidor.setSerTel(serTelDdd + "-" + serTel);
        }

        // Quebra o celular em DDD + número.
        String serCelular = "", serCelularDdd = "";
        serCelular = ((servidor != null) && (servidor.getSerCelular() != null)) ? TextHelper.dropSeparator(servidor.getSerCelular()) : "";
        if ((serCelular.length() == 10) || (serCelular.length() == 11)) {
            serCelularDdd = serCelular.substring(0, 2);
            serCelular = serCelular.substring(2, serCelular.length());
        }

        serCelularDdd = (String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_DDD_CELULAR, serCelularDdd, responsavel);
        serCelular = (String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_CELULAR, serCelular, responsavel);
        serCelular = TextHelper.dropSeparator(TextHelper.dropBlankSpace(serCelular));
        if (TextHelper.isNull(serCelular)) {
            servidor.setSerCelular("");
        } else if (TextHelper.isNull(serCelularDdd)) {
            servidor.setSerCelular(serCelular);
        } else {
            servidor.setSerCelular(serCelularDdd + "-" + serCelular);
        }

        servidor.setSerNro((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_NRO, servidor.getSerNro(), true, responsavel));
        servidor.setSerEmail((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_EMAIL, servidor.getSerEmail(), responsavel));

        Object serDataIdt = JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_DATA_IDENTIDADE, servidor.getSerDataIdt(), true, responsavel);
        if (serDataIdt != null) {
            try {
                serDataIdt = DateHelper.toSQLDate(DateHelper.parse((String) serDataIdt, LocaleHelper.getDatePattern()));
            } catch (final Exception e) {
                final String periodoFormatado = DateHelper.format((Date) serDataIdt, "yyyy-MM-dd");
                final Date periodo = DateHelper.parse(periodoFormatado, "yyyy-MM-dd");
                serDataIdt = DateHelper.toSQLDate(periodo);
            }
        }

        // Seta e-mail para contratos rejeitados
        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_EMAIL_CONTRATOS_REJEITADOS_FOLHA, CodedValues.NOTIFICA_CONTRATOS_REJEITADOS_FOLHA_OPCIONAL, responsavel) &&
        		ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_EMAIL_CONTRATOS_REJEITADOS, responsavel)) {

            final String notificaContratosRejeitados = (String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_EMAIL_CONTRATOS_REJEITADOS, null, true, responsavel);
            final Short nusAtivo = ("S".equals(notificaContratosRejeitados)) ? CodedValues.STS_ATIVO : CodedValues.STS_INATIVO;

            final CustomTransferObject usuarioSer = pesquisarServidorController.buscaUsuarioServidorBySerCodigo(servidor.getSerCodigo(), responsavel);
            final String usuCodigo = usuarioSer.getAttribute(Columns.USU_CODIGO).toString();

        	final NotificacaoUsuarioId id = new NotificacaoUsuarioId();
        	id.setTnoCodigo(TipoNotificacaoEnum.EMAIL_CONTRATOS_REJEITADOS_FOLHA.getCodigo());
        	id.setUsuCodigo(usuCodigo);

        	final NotificacaoUsuario notificacaoUsuario = notificacaoUsuarioController.find(usuCodigo, TipoNotificacaoEnum.EMAIL_CONTRATOS_REJEITADOS_FOLHA.getCodigo(), responsavel);

        	if (notificacaoUsuario == null) {
            	notificacaoUsuarioController.create(usuCodigo, TipoNotificacaoEnum.EMAIL_CONTRATOS_REJEITADOS_FOLHA.getCodigo(), nusAtivo, responsavel);
        	} else {
        		notificacaoUsuario.setNusAtivo(nusAtivo);
        		notificacaoUsuarioController.update(notificacaoUsuario, responsavel);
        	}
        }

        servidor.setSerDataIdt((java.sql.Date) serDataIdt);

        servidor.setSerCidNasc((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_CIDADE_NASCIMENTO, servidor.getSerCidNasc(), responsavel));
        servidor.setSerUfNasc((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_SERVIDOR_UF_NASCIMENTO, servidor.getSerUfNasc(), responsavel));

        // Seta código do usuário que está alterando e a data de alteração
        servidor.setUsuCodigo(responsavel.getUsuCodigo());
        servidor.setSerDataAlteracao(new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis()));
    }

    protected void recuperarDadosRegistroServidor(RegistroServidorTO registroServidor, HttpServletRequest request, AcessoSistema responsavel) throws ZetraException, ParseException {
        // Seta matricula
        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MATRICULA, responsavel)) {
            registroServidor.setRseMatricula((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MATRICULA, registroServidor.getRseMatricula(), true, responsavel));

            // Busca a matricula que foi salva no caso de matrícula numérica inciiada com zero
            if (ParamSist.paramEquals(CodedValues.TPC_MATRICULA_NUMERICA, CodedValues.TPC_SIM, responsavel)) {
                final long matricula = Long.parseLong(registroServidor.getRseMatricula());
                if (matricula <= 0) {
                    throw new ServidorControllerException("mensagem.erro.matricula.invalida", responsavel);
                }
                registroServidor.setRseMatricula(Long.toString(matricula));
            }
        }

        if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_EDITAR_ORGAO_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
            registroServidor.setOrgCodigo((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ORGAO, registroServidor.getOrgCodigo(), true, responsavel));
        }

        registroServidor.setRseTipo((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CATEGORIA, registroServidor.getRseTipo(), responsavel));
        registroServidor.setRseCLT((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CLT, registroServidor.getRseCLT(), responsavel));
        registroServidor.setSrsCodigo((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SITUACAO, registroServidor.getSrsCodigo(), responsavel));
        registroServidor.setRseEstabilizado((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ESTABILIZADO, registroServidor.getRseEstabilizado(), responsavel));

        registroServidor.setVrsCodigo((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_VINCULO, registroServidor.getVrsCodigo(), true, responsavel));
        registroServidor.setCrsCodigo((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CARGO, registroServidor.getCrsCodigo(), true, responsavel));
        registroServidor.setPrsCodigo((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PADRAO, registroServidor.getPrsCodigo(), true, responsavel));
        registroServidor.setSboCodigo((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SUB_ORGAO, registroServidor.getSboCodigo(), true, responsavel));
        registroServidor.setUniCodigo((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_UNIDADE, registroServidor.getUniCodigo(), true, responsavel));
        registroServidor.setRseMunicipioLotacao((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MUNICIPIO_LOTACAO, registroServidor.getRseMunicipioLotacao(), true, responsavel));

        final Object rsePrazo = JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PRAZO, registroServidor.getRsePrazo(), true, responsavel);
        registroServidor.setRsePrazo(rsePrazo != null ? Integer.valueOf(rsePrazo.toString()) : null);

        final Object rseDataAdmissao = JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_ADMISSAO, registroServidor.getRseDataAdmissao(), true, responsavel);
        if (rseDataAdmissao instanceof String) {
            registroServidor.setRseDataAdmissao(rseDataAdmissao != null ? new java.sql.Timestamp(DateHelper.parse((String) rseDataAdmissao, LocaleHelper.getDatePattern()).getTime()) : null);
        } else if (rseDataAdmissao == null) {
            registroServidor.setRseDataAdmissao(null);
        }

        registroServidor.setRseBancoSal((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BANCO, registroServidor.getRseBancoSal(), true, responsavel));
        registroServidor.setRseAgenciaSal((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AGENCIA, registroServidor.getRseAgenciaSal(), true, responsavel));
        registroServidor.setRseContaSal((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CONTA, registroServidor.getRseContaSal(), true, responsavel));

        registroServidor.setRseBancoSalAlternativo((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BANCO_ALTERNATIVO, registroServidor.getRseBancoSalAlternativo(), true, responsavel));
        registroServidor.setRseAgenciaSalAlternativa((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AGENCIA_ALTERNATIVA, registroServidor.getRseAgenciaSalAlternativa(), true, responsavel));
        registroServidor.setRseContaSalAlternativa((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CONTA_ALTERNATIVA, registroServidor.getRseContaSalAlternativa(), true, responsavel));

        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SALARIO, responsavel)) {
            final Object rseSalario = JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SALARIO, registroServidor.getRseSalario(), responsavel);
            if (!TextHelper.isNull(rseSalario)) {
                if (rseSalario instanceof BigDecimal) {
                    registroServidor.setRseSalario((BigDecimal) rseSalario);
                } else {
                    registroServidor.setRseSalario(new BigDecimal(NumberHelper.reformat((String) rseSalario, NumberHelper.getLang(), "en")));
                }
            } else {
                registroServidor.setRseSalario(null);
            }
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MOTIVO_FALTA_MARGEM, responsavel)) {
            final Object rseMotivoFaltaMargem = JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MOTIVO_FALTA_MARGEM, registroServidor.getRseMotivoFaltaMargem(), responsavel);
            if (!TextHelper.isNull(rseMotivoFaltaMargem)) {
                registroServidor.setRseMotivoFaltaMargem((String) rseMotivoFaltaMargem);
            } else {
                registroServidor.setRseMotivoFaltaMargem(null);
            }
        }

        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PROVENTOS, responsavel)) {
            final Object rseProventos = JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PROVENTOS, registroServidor.getRseProventos(), responsavel);

            if (!TextHelper.isNull(rseProventos)) {
                if (rseProventos instanceof BigDecimal) {
                    registroServidor.setRseProventos((BigDecimal) rseProventos);
                } else {
                    registroServidor.setRseProventos(new BigDecimal(NumberHelper.reformat((String) rseProventos, NumberHelper.getLang(), "en")));
                }
            } else {
                registroServidor.setRseProventos(null);
            }
        }

        registroServidor.setRsePedidoDemissao((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PEDIDO_DEMISSAO, registroServidor.getRsePedidoDemissao(), true, responsavel));

        final Object rseDataSaida = JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_SAIDA, registroServidor.getRseDataSaida(), true, responsavel);
        if (rseDataSaida instanceof String) {
            registroServidor.setRseDataSaida(rseDataSaida != null ? DateHelper.parse((String) rseDataSaida, LocaleHelper.getDatePattern()) : null);
        } else if (rseDataSaida == null) {
            registroServidor.setRseDataSaida(null);
        }
        final Object rseDataUltSalario = JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_ULT_SALARIO, registroServidor.getRseDataUltSalario(), true, responsavel);
        if (rseDataUltSalario instanceof String) {
            registroServidor.setRseDataUltSalario(rseDataUltSalario != null ? DateHelper.parse((String) rseDataUltSalario, LocaleHelper.getDatePattern()) : null);
        } else if (rseDataUltSalario == null) {
            registroServidor.setRseDataUltSalario(null);
        }
        final Object rseDataRetorno = JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_RETORNO, registroServidor.getRseDataRetorno(), true, responsavel);
        if (rseDataRetorno instanceof String) {
            registroServidor.setRseDataRetorno(rseDataRetorno != null ? DateHelper.parse((String) rseDataRetorno, LocaleHelper.getDatePattern()) : null);
        } else if (rseDataRetorno == null) {
            registroServidor.setRseDataRetorno(null);
        }

        registroServidor.setPosCodigo((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_POSTO, registroServidor.getPosCodigo(), true, responsavel));
        registroServidor.setTrsCodigo((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_TIPO_REG_SERVIDOR, registroServidor.getTrsCodigo(), true, responsavel));

        final Object dataFimEngajamento = JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_FIM_ENGAJAMENTO, registroServidor.getRseDataFimEngajamento(), true, responsavel);
        if (dataFimEngajamento instanceof String) {
            registroServidor.setRseDataFimEngajamento(dataFimEngajamento != null ? new java.sql.Timestamp(DateHelper.parse((String) dataFimEngajamento, LocaleHelper.getDatePattern()).getTime()) : null);
        } else if (dataFimEngajamento instanceof java.sql.Timestamp) {
            registroServidor.setRseDataFimEngajamento(dataFimEngajamento != null ? (java.sql.Timestamp) dataFimEngajamento : null);
        } else if (dataFimEngajamento == null) {
            registroServidor.setRseDataFimEngajamento(null);
        }

        final Object dataLimitePermanencia = JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_LIMITE_PERMANENCIA, registroServidor.getRseDataLimitePermanencia(), true, responsavel);
        if (dataLimitePermanencia instanceof String) {
            registroServidor.setRseDataLimitePermanencia(dataLimitePermanencia != null ? new java.sql.Timestamp(DateHelper.parse((String) dataLimitePermanencia, LocaleHelper.getDatePattern()).getTime()) : null);
        } else if (dataLimitePermanencia instanceof java.sql.Timestamp) {
            registroServidor.setRseDataLimitePermanencia(dataLimitePermanencia != null ? (java.sql.Timestamp) dataLimitePermanencia : null);
        } else if (dataLimitePermanencia == null) {
            registroServidor.setRseDataLimitePermanencia(null);
        }

        registroServidor.setCapCodigo((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CAPACIDADE_CIVIL, registroServidor.getCapCodigo(), true, responsavel));
        final Object margemLimite = JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MARGEM_LIMITE_DESCONTO_FOLHA, registroServidor.getMarCodigo(), true, responsavel);
        if (margemLimite != null) {
            if (margemLimite instanceof String) {
                registroServidor.setMarCodigo(Short.parseShort((String) margemLimite));
            } else if (margemLimite instanceof Short) {
                registroServidor.setMarCodigo((Short) margemLimite);
            }
        } else {
            registroServidor.setMarCodigo(null);
        }

        // Seta observações sobre o servidor
        registroServidor.setRseObs((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_OBSERVACAO, registroServidor.getRseObs(), true, responsavel));

        // Salva as praças do registro servidor
        String rsePraca = (String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PRACA, registroServidor.getRsePraca(), true, responsavel);
        if (!TextHelper.isNull(rsePraca)) {
            rsePraca = rsePraca.replace("\r\n", ";").replace('\r', ';').replace('\n', ';').replace(',', ';').replace(";;", ";");
        }
        registroServidor.setRsePraca(rsePraca);

        // Seta se o registro servidor deve ser auditado
        registroServidor.setRseAuditoriaTotal((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AUDITORIA_TOTAL, registroServidor.getRseAuditoriaTotal(), responsavel));

        // Seta se o servidor é beneficiário de financiamento de dívida
        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_FINANC_DIVIDA_CARTAO, CodedValues.TPC_SIM, responsavel)) {
            registroServidor.setRseBeneficiarioFinanDvCart((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BENEFICIARIO_FINAN_DV_CART, registroServidor.getRseBeneficiarioFinanDvCart(), true, responsavel));
        }

        // Seta se o servidor é associado
        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ASSOCIADO, responsavel)) {
            registroServidor.setRseAssociado((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ASSOCIADO, registroServidor.getRseAssociado(), true, responsavel));
        }

        // Seta base de calculo
        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BASE_CALCULO, responsavel)) {
            final Object rseBaseCalculo = JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BASE_CALCULO, registroServidor.getRseBaseCalculo(), responsavel);
            if (!TextHelper.isNull(rseBaseCalculo)) {
                if (rseBaseCalculo instanceof BigDecimal) {
                    registroServidor.setRseBaseCalculo((BigDecimal) rseBaseCalculo);
                } else {
                    registroServidor.setRseBaseCalculo(new BigDecimal(NumberHelper.reformat((String) rseBaseCalculo, NumberHelper.getLang(), "en")));
                }
            } else {
                registroServidor.setRseBaseCalculo(null);
            }
        }

        // Seta data contracheque
        final Object rseDataContracheque = JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_CONTRACHEQUE, registroServidor.getRseDataContracheque(), true, responsavel);
        if (rseDataContracheque instanceof String) {
            registroServidor.setRseDataContracheque(rseDataContracheque != null ? DateHelper.parse((String) rseDataContracheque, LocaleHelper.getDatePattern()) : null);
        } else if (rseDataContracheque == null) {
            registroServidor.setRseDataContracheque(null);
        }

        // Seta descontos compulsorios
        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DESCONTOS_COMPULSORIOS, responsavel)) {
            final Object rseCompulsorio = JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DESCONTOS_COMPULSORIOS, registroServidor.getRseDescontosComp(), responsavel);
            if (!TextHelper.isNull(rseCompulsorio)) {
                if (rseCompulsorio instanceof BigDecimal) {
                    registroServidor.setRseDescontosComp((BigDecimal) rseCompulsorio);
                } else {
                    registroServidor.setRseDescontosComp(new BigDecimal(NumberHelper.reformat((String) rseCompulsorio, NumberHelper.getLang(), "en")));
                }
            } else {
                registroServidor.setRseDescontosComp(null);
            }
        }

        // Seta descontos facultativos
        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DESCONTOS_FACULTATIVOS, responsavel)) {
            final Object rseDescFacultativo = JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DESCONTOS_FACULTATIVOS, registroServidor.getRseDescontosFacu(), responsavel);
            if (!TextHelper.isNull(rseDescFacultativo)) {
                if (rseDescFacultativo instanceof BigDecimal) {
                    registroServidor.setRseDescontosFacu((BigDecimal) rseDescFacultativo);
                } else {
                    registroServidor.setRseDescontosFacu(new BigDecimal(NumberHelper.reformat((String) rseDescFacultativo, NumberHelper.getLang(), "en")));
                }
            } else {
                registroServidor.setRseDescontosFacu(null);
            }
        }

        // Seta outros descontos
        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_OUTROS_DESCONTOS, responsavel)) {
            final Object rseOutrosDescontos = JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_OUTROS_DESCONTOS, registroServidor.getRseOutrosDescontos(), responsavel);
            if (!TextHelper.isNull(rseOutrosDescontos)) {
                if (rseOutrosDescontos instanceof BigDecimal) {
                    registroServidor.setRseOutrosDescontos((BigDecimal) rseOutrosDescontos);
                } else {
                    registroServidor.setRseOutrosDescontos(new BigDecimal(NumberHelper.reformat((String) rseOutrosDescontos, NumberHelper.getLang(), "en")));
                }
            } else {
                registroServidor.setRseOutrosDescontos(null);
            }
        }

        // Seta matricula institucional
        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MATRICULA_INSTITUCIONAL, responsavel)) {
            registroServidor.setRseMatriculaInst((String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MATRICULA_INSTITUCIONAL, registroServidor.getRseMatriculaInst(), true, responsavel));
        }

        // Seta código do usuário que está alterando e a data de alteração
        registroServidor.setUsuCodigo(responsavel.getUsuCodigo());
        registroServidor.setRseDataAlteracao(new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis()));

        // Valida se o motivo da operação selecionado é obrigatório("S") e também se o campo observação foi preenchido(orsObs)
        final String orsObs = JspHelper.verificaVarQryStr(request, "ADE_OBS");
        final String tmoCodigo = JspHelper.verificaVarQryStr(request, "TMO_CODIGO");

        if ((tmoCodigo != null) && FuncaoExigeMotivo.getInstance().motivosExigeObs(tmoCodigo, responsavel) && orsObs.isEmpty()) {
            throw new ZetraException("mensagem.erro.obrigatoriedade.observacao.motivo.operacao", responsavel);
        }

        //DESENV-16129 - Rio de Janeiro - Mostrar Motivo de Bloqueio do Servidor
        if ((registroServidor.getAttribute(Columns.SRS_CODIGO) != null) && CodedValues.SRS_BLOQUEADO.equals(registroServidor.getAttribute(Columns.SRS_CODIGO))) {
            final String edtRegistroServidorExibeMotivoBloqueioField = (String) JspHelper.getFieldValue(request, FieldKeysConstants.EDT_REGISTRO_SERVIDOR_EXIBE_MOTIVO_BLOQUEIO, null, true, responsavel);
            if (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_EXIBE_MOTIVO_BLOQUEIO, responsavel) && "S".equals(edtRegistroServidorExibeMotivoBloqueioField) && (tmoCodigo != null) && !TextHelper.isNull(tmoCodigo.trim()) && (orsObs != null) && !TextHelper.isNull(orsObs.trim())) {
                final String tmoDescricao = tipoMotivoOperacaoController.findMotivoOperacao(tmoCodigo, responsavel).getTmoDescricao();
                registroServidor.setRseMotivoBloqueio(ApplicationResourcesHelper.getMessage("mensagem.informacao.situacao.servidor.bloqueado.informacoes", responsavel) + ": <br>" + ApplicationResourcesHelper.getMessage("rotulo.efetiva.acao.consignacao.dados.tipo.mtv.cancelamento", responsavel) + ": " + tmoDescricao + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.efetiva.acao.consignacao.dados.observacao", responsavel) + ": " + orsObs);
            }
        }

        // Seta tipo motivo e observação
        registroServidor.setTipoMotivo(tmoCodigo);
        registroServidor.setOrsObs(orsObs);
    }

    /**
     * recupera dados adicionais de registro servidor
     * @param registroServidor - TO com dados básicos do servidor
     * @param request
     * @param responsavel
     * @throws ZetraException
     * @throws ParseException
     */
    protected void recuperarDadosAdicionaisRseServidor(TransferObject registroServidor, HttpServletRequest request, AcessoSistema responsavel) throws ZetraException {
        if (ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ORGAO, responsavel)) {
            final CustomTransferObject criterio = new CustomTransferObject();

            // Verifica se o órgão pode ser editado
            boolean podeEditarOrgao = true;
            if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_EDITAR_ORGAO_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                // Monta um critério de busca com base no perfil do usuário, seja de ORG ou EST, e CSE/SUP listando todos
                if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
                    criterio.setAttribute(Columns.ORG_EST_CODIGO, responsavel.getCodigoEntidadePai());
                } else if (responsavel.isOrg()) {
                    criterio.setAttribute(Columns.ORG_CODIGO, responsavel.getCodigoEntidade());
                }
            } else {
                podeEditarOrgao = false;
                // Monta um critério de busca para retornar apenas o órgão atual
                criterio.setAttribute(Columns.ORG_CODIGO, registroServidor.getAttribute(Columns.ORG_CODIGO));
            }

            // Seleciona os órgãos
            final List<TransferObject> orgaos = consignanteController.lstOrgaos(criterio, responsavel);
            request.setAttribute("orgaos", orgaos);
            request.setAttribute("podeEditarOrgao", podeEditarOrgao);
        }

        if (ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SUB_ORGAO, responsavel)) {
            // Seleciona sub-orgão
            final List<TransferObject> subOrgaos = servidorController.lstSubOrgao(responsavel, (String) registroServidor.getAttribute(Columns.ORG_CODIGO));
            request.setAttribute("subOrgaos", subOrgaos);
        }

        if (ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_UNIDADE, responsavel)) {
            // Seleciona unidade
            final List<TransferObject> unidades = servidorController.lstUnidade(responsavel, (String) registroServidor.getAttribute(Columns.SBO_CODIGO));
            request.setAttribute("unidades", unidades);
        }

        if (ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PADRAO, responsavel)) {
            // Seleciona padrão
            final List<TransferObject> padrao = servidorController.lstPadrao(responsavel);
            request.setAttribute("padrao", padrao);
        }

        if (ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CARGO, responsavel)) {
            // Seleciona cargo
            final List<TransferObject> cargos = servidorController.lstCargo(responsavel);
            request.setAttribute("cargos",cargos);
        }

        if (ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SITUACAO, responsavel)) {
            // Seleciona status de registro servidor
            final boolean ignoraStatusBloqSeguranca = !CodedValues.SRS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA.equals(registroServidor.getAttribute(Columns.SRS_CODIGO));
            final List<TransferObject> listaSrs = servidorController.lstStatusRegistroServidor(false, ignoraStatusBloqSeguranca, responsavel);
            request.setAttribute("listaSrs", listaSrs);
        }

        if (ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_TIPO_REG_SERVIDOR, responsavel)) {
            // Seleciona os tipos de registro servidor
            final List<TransferObject> listaTipoRegServidor = servidorController.lstTipoRegistroServidor(responsavel);
            request.setAttribute("listaTipoRegServidor", listaTipoRegServidor);
        }

        if (ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_POSTO, responsavel)) {
            // Seleciona os postos
            final List<TransferObject> listaPostoCodigo = servidorController.lstPosto(responsavel);
            request.setAttribute("listaPostoCodigo", listaPostoCodigo);
        }

        if (ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CAPACIDADE_CIVIL, responsavel)) {
            // Seleciona as capacidades
            final List<TransferObject> listaCapCivil = servidorController.lstCapacidadeCivil(responsavel);
            request.setAttribute("listaCapCivil", listaCapCivil);
        }

        if (ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_VINCULO, responsavel)) {
            // Seleciona os vínculos
            final List<TransferObject> listaVincRegSer = servidorController.selectVincRegistroServidor(true, responsavel);
            request.setAttribute("listaVincRegSer", listaVincRegSer);
        }
    }

    protected boolean validarDadosServidor(ServidorTransferObject servidor, HttpServletRequest request, AcessoSistema responsavel) throws ZetraException {
        final HttpSession session = request.getSession();

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_NOME, responsavel) && TextHelper.isNull(servidor.getSerNome())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.nome", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_TITULACAO, responsavel) && TextHelper.isNull(servidor.getSerTitulacao())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.tratamento.nome", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_PRIMEIRO_NOME, responsavel) && TextHelper.isNull(servidor.getSerPrimeiroNome())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.primeiro.nome", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_NOME_MEIO, responsavel) && TextHelper.isNull(servidor.getSerNomeMeio())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.meio.nome", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_ULTIMO_NOME, responsavel) && TextHelper.isNull(servidor.getSerUltimoNome())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.ultimo.nome", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_NOME_PAI, responsavel) && TextHelper.isNull(servidor.getSerNomePai())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.nome.pai", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_NOME_MAE, responsavel) && TextHelper.isNull(servidor.getSerNomeMae())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.nome.mae", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_NOME_CONJUGE, responsavel) && TextHelper.isNull(servidor.getSerNomeConjuge())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.nome.conjuge", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_DATA_NASCIMENTO, responsavel) && TextHelper.isNull(servidor.getSerDataNasc())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.data.nascimento", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_CIDADE_NASCIMENTO, responsavel) && TextHelper.isNull(servidor.getSerCidNasc())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.cidade.nascimento", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_UF_NASCIMENTO, responsavel) && TextHelper.isNull(servidor.getSerUfNasc())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.uf.nascimento", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_ESTADO_CIVIL, responsavel) && TextHelper.isNull(servidor.getSerEstCivil())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.estado.civil", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_NACIONALIDADE, responsavel) && TextHelper.isNull(servidor.getSerNacionalidade())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.naturalidade", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_SEXO, responsavel) && TextHelper.isNull(servidor.getSerSexo())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.sexo", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_QTD_FILHOS, responsavel) && TextHelper.isNull(servidor.getSerQtdFilhos())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.quantidade.filhos", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_NIVEL_ESCOLARIDADE, responsavel) && TextHelper.isNull(servidor.getNesCodigo())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.nivel.escolaridade", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_DEFICIENTE_VISUAL, responsavel) && TextHelper.isNull(servidor.getSerDeficienteVisual())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.deficiente.visual", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_CPF, responsavel) && TextHelper.isNull(servidor.getSerCpf())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.cpf", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_NUM_CARTEIRA_TRABALHO, responsavel) && TextHelper.isNull(servidor.getSerCartProf())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.cart.trabalho", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_NUM_PIS, responsavel) && TextHelper.isNull(servidor.getSerPis())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.pis", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_NO_IDENTIDADE, responsavel) && TextHelper.isNull(servidor.getSerNroIdt())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.identidade", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_EMISSOR_IDENTIDADE, responsavel) && TextHelper.isNull(servidor.getSerEmissorIdt())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.emissor.identidade", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_UF_IDENTIDADE, responsavel) && TextHelper.isNull(servidor.getSerUfIdt())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.uf.identidade", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_DATA_IDENTIDADE, responsavel) && TextHelper.isNull(servidor.getSerDataIdt())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.data.emissao.identidade", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_LOGRADOURO, responsavel) && TextHelper.isNull(servidor.getSerEnd())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.logradouro", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_NRO, responsavel) && TextHelper.isNull(servidor.getSerNro())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.numero", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_COMPLEMENTO, responsavel) && TextHelper.isNull(servidor.getSerCompl())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.complemento", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_BAIRRO, responsavel) && TextHelper.isNull(servidor.getSerBairro())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.bairro", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_CIDADE, responsavel) && TextHelper.isNull(servidor.getSerCidade())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.cidade", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_UF, responsavel) && TextHelper.isNull(servidor.getSerUf())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.estado", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_CEP, responsavel) && TextHelper.isNull(servidor.getSerCep())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.cep", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_TIPO_HABITACAO, responsavel) && TextHelper.isNull(servidor.getThaCodigo())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.tipo.habitacao", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_TELEFONE, responsavel) && TextHelper.isNull(servidor.getSerTel())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.telefone", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_CELULAR, responsavel) && TextHelper.isNull(servidor.getSerCelular())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.celular", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_EMAIL, responsavel) && TextHelper.isNull(servidor.getSerEmail())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.email", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_ACESSA_HOST_A_HOST, responsavel) && TextHelper.isNull(servidor.getSerAcessaHostaHost())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.hostahost", responsavel));
            return false;
        }

        return true;
    }

    protected boolean validarDadosRegistroServidor(RegistroServidorTO registroServidor, HttpServletRequest request, AcessoSistema responsavel) throws ZetraException {
        final HttpSession session = request.getSession();

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MATRICULA, responsavel) && TextHelper.isNull(registroServidor.getRseMatricula())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.matricula", responsavel));
            return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MATRICULA_INSTITUCIONAL, responsavel) && TextHelper.isNull(registroServidor.getRseMatriculaInst())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.matricula.institucional", responsavel));
            return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ORGAO, responsavel) && TextHelper.isNull(registroServidor.getOrgCodigo())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.orgao", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SUB_ORGAO, responsavel) && TextHelper.isNull(registroServidor.getSboCodigo())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.sub.orgao", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_UNIDADE, responsavel) && TextHelper.isNull(registroServidor.getUniCodigo())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.unidade", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MUNICIPIO_LOTACAO, responsavel) && TextHelper.isNull(registroServidor.getRseMunicipioLotacao())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.municipio", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SITUACAO, responsavel) && TextHelper.isNull(registroServidor.getSrsCodigo())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.situacao", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_ADMISSAO, responsavel) && TextHelper.isNull(registroServidor.getRseDataAdmissao())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.data.admissao", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PRAZO, responsavel) && TextHelper.isNull(registroServidor.getRsePrazo())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.prazo", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_SAIDA, responsavel) && TextHelper.isNull(registroServidor.getRseDataSaida())
                 && (CodedValues.SRS_BLOQUEADO.equals(registroServidor.getSrsCodigo()) || CodedValues.SRS_EXCLUIDO.equals(registroServidor.getSrsCodigo()))) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.rse.informe.data.saida", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_ULT_SALARIO, responsavel) && TextHelper.isNull(registroServidor.getRseDataUltSalario())
                 && (CodedValues.SRS_BLOQUEADO.equals(registroServidor.getSrsCodigo()) || CodedValues.SRS_EXCLUIDO.equals(registroServidor.getSrsCodigo()))) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.rse.informe.data.ult.salario", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PEDIDO_DEMISSAO, responsavel) && TextHelper.isNull(registroServidor.getRsePedidoDemissao())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.pedido.demissao.obrigatorio", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_RETORNO, responsavel) && TextHelper.isNull(registroServidor.getRseDataRetorno())
                 && (CodedValues.SRS_BLOQUEADO.equals(registroServidor.getSrsCodigo()) || CodedValues.SRS_EXCLUIDO.equals(registroServidor.getSrsCodigo()))) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.rse.informe.data.retorno", responsavel));
             return false;
         }

         if ((ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MARGEM_1, responsavel) && TextHelper.isNull(registroServidor.getRseMargem())) || (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MARGEM_2, responsavel) && TextHelper.isNull(registroServidor.getRseMargem2()))) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.rse.margem", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MARGEM_3, responsavel) && TextHelper.isNull(registroServidor.getRseMargem3())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.rse.margem", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_SALARIO, responsavel) && TextHelper.isNull(registroServidor.getRseSalario())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.salario", responsavel));
             return false;
         }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MOTIVO_FALTA_MARGEM, responsavel) && TextHelper.isNull(registroServidor.getRseMotivoFaltaMargem())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.motivo.falta.margem", responsavel));
            return false;
        }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PROVENTOS, responsavel) && TextHelper.isNull(registroServidor.getRseProventos())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.proventos", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DESCONTOS_COMPULSORIOS, responsavel) && TextHelper.isNull(registroServidor.getRseDescontosComp())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.descontos.compulsorios", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DESCONTOS_FACULTATIVOS, responsavel) && TextHelper.isNull(registroServidor.getRseDescontosFacu())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.descontos.facultativos", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_OUTROS_DESCONTOS, responsavel) && TextHelper.isNull(registroServidor.getRseOutrosDescontos())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.outros.descontos", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BASE_CALCULO, responsavel) && TextHelper.isNull(registroServidor.getRseBaseCalculo())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.base.calculo", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_CONTRACHEQUE, responsavel) && TextHelper.isNull(registroServidor.getRseDataContracheque())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.data.contracheque", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BANCO, responsavel) && TextHelper.isNull(registroServidor.getRseBancoSal())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.banco", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AGENCIA, responsavel) && TextHelper.isNull(registroServidor.getRseAgenciaSal())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.agencia", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CONTA, responsavel) && TextHelper.isNull(registroServidor.getRseContaSal())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.conta", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BANCO_ALTERNATIVO, responsavel) && TextHelper.isNull(registroServidor.getRseBancoSalAlternativo())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.banco.alternativo", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AGENCIA_ALTERNATIVA, responsavel) && TextHelper.isNull(registroServidor.getRseAgenciaSalAlternativa())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.agencia.alternativa", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CONTA_ALTERNATIVA, responsavel) && TextHelper.isNull(registroServidor.getRseContaSalAlternativa())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.conta.alternativa", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CATEGORIA, responsavel) && TextHelper.isNull(registroServidor.getRseTipo())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.categoria", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CARGO, responsavel) && TextHelper.isNull(registroServidor.getCrsCodigo())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.cargo", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PADRAO, responsavel) && TextHelper.isNull(registroServidor.getPrsCodigo())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.padrao", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_VINCULO, responsavel) && TextHelper.isNull(registroServidor.getVrsCodigo())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.vinculo", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_TIPO_REG_SERVIDOR, responsavel) && TextHelper.isNull(registroServidor.getTrsCodigo())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.tipo", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_POSTO, responsavel) && TextHelper.isNull(registroServidor.getPosCodigo())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.posto", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CAPACIDADE_CIVIL, responsavel) && TextHelper.isNull(registroServidor.getCapCodigo())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.capacidade.civil", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ESTABILIZADO, responsavel) && TextHelper.isNull(registroServidor.getRseEstabilizado())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.estabilizado", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_CLT, responsavel) && TextHelper.isNull(registroServidor.getRseCLT())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.sindicalizado", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_ASSOCIADO, responsavel) && TextHelper.isNull(registroServidor.getRseAssociado())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.associado", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_FIM_ENGAJAMENTO, responsavel) && TextHelper.isNull(registroServidor.getRseDataFimEngajamento())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.engajado", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_DATA_LIMITE_PERMANENCIA, responsavel) && TextHelper.isNull(registroServidor.getRseDataLimitePermanencia())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.data.limite.permanencia", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_PRACA, responsavel) && TextHelper.isNull(registroServidor.getRsePraca())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.praca", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_AUDITORIA_TOTAL, responsavel) && TextHelper.isNull(registroServidor.getRseAuditoriaTotal())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.auditoria.total", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_BENEFICIARIO_FINAN_DV_CART, responsavel) && TextHelper.isNull(registroServidor.getRseBeneficiarioFinanDvCart())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.divida.cartao", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_OBSERVACAO, responsavel) && TextHelper.isNull(registroServidor.getRseObs())) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.obs", responsavel));
             return false;
         }

         return true;
    }

    protected boolean validarDadosStatusRegistroServidor(RegistroServidorTO registroServidor, HttpServletRequest request, AcessoSistema responsavel) throws ZetraException {

        final HttpSession session = request.getSession();

        final String situacao       = request.getParameter("editarStatusRegistroServidor_situacao");
        final String dataSaida      = request.getParameter("editarStatusRegistroServidor_dataSaida");
        final String dataUltSalario = request.getParameter("editarStatusRegistroServidor_dataUltSalario");
        final String salario        = request.getParameter("editarStatusRegistroServidor_salario");
        final String proventos      = request.getParameter("editarStatusRegistroServidor_proventos");
        final String pedidoDemissao = request.getParameter("editarStatusRegistroServidor_pedidoDemissao");
        final String dataRetorno    = request.getParameter("editarStatusRegistroServidor_dataRetorno");

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_SITUACAO, responsavel) && TextHelper.isNull(situacao)) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.situacao", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_SAIDA, responsavel) && TextHelper.isNull(dataSaida)
                 && (CodedValues.SRS_BLOQUEADO.equals(situacao) || CodedValues.SRS_EXCLUIDO.equals(situacao))) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.rse.informe.data.saida", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_ULT_SALARIO, responsavel) && TextHelper.isNull(dataUltSalario)
                 && (CodedValues.SRS_BLOQUEADO.equals(situacao) || CodedValues.SRS_EXCLUIDO.equals(situacao))) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.rse.informe.data.ult.salario", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_PEDIDO_DEMISSAO, responsavel) && TextHelper.isNull(pedidoDemissao) && CodedValues.SRS_ATIVO.equals(situacao)) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.pedido.demissao.obrigatorio", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_RETORNO, responsavel) && TextHelper.isNull(dataRetorno)
                 && (CodedValues.SRS_BLOQUEADO.equals(situacao) || CodedValues.SRS_EXCLUIDO.equals(situacao))) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.rse.informe.data.retorno", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_SALARIO, responsavel) && TextHelper.isNull(salario) && CodedValues.SRS_ATIVO.equals(situacao)) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.salario", responsavel));
             return false;
         }

         if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_PROVENTOS, responsavel) && TextHelper.isNull(proventos) && CodedValues.SRS_ATIVO.equals(situacao)) {
             session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.proventos", responsavel));
             return false;
         }

         return true;
    }
}
