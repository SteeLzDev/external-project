package com.zetra.econsig.service.servidor;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.assembler.RegistroServidorDtoAssembler;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.OcorrenciaUsuarioTransferObject;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.ArquivoControllerException;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.BeneficioControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.NotificacaoDispositivoControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.margem.MargemHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo;
import com.zetra.econsig.helper.servidor.GeradorCpfServidor;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.sms.SMSHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.dao.DAOFactory;
import com.zetra.econsig.persistence.dao.ParamConvenioRegistroServidorDAO;
import com.zetra.econsig.persistence.dao.ServidorDAO;
import com.zetra.econsig.persistence.entity.*;
import com.zetra.econsig.persistence.query.admin.ListaCapacidadeRegistroServidorQuery;
import com.zetra.econsig.persistence.query.admin.ListaCargoRegistroServidorQuery;
import com.zetra.econsig.persistence.query.admin.ListaPadraoRegistroServidorQuery;
import com.zetra.econsig.persistence.query.admin.ListaPostoRegistroServidorQuery;
import com.zetra.econsig.persistence.query.admin.ListaStatusRegistroServidorQuery;
import com.zetra.econsig.persistence.query.admin.ListaTipoRegistroServidorQuery;
import com.zetra.econsig.persistence.query.admin.ListaVencimentoQuery;
import com.zetra.econsig.persistence.query.admin.ListaVinculoRegistroServidorQuery;
import com.zetra.econsig.persistence.query.basecalc.ListaTipoBaseCalculoQuery;
import com.zetra.econsig.persistence.query.consignacao.ObtemConsignacaoPorCnvSerQuery;
import com.zetra.econsig.persistence.query.convenio.ListaConvenioVinculoRegistroServidorQuery;
import com.zetra.econsig.persistence.query.funcao.FuncoesEnvioEmailSerQuery;
import com.zetra.econsig.persistence.query.historico.ListarHistoricoServidorQuery;
import com.zetra.econsig.persistence.query.margem.ListaHistoricoMargemQuery;
import com.zetra.econsig.persistence.query.margem.ListaHistoricoVariacaoMargemBrutaQuery;
import com.zetra.econsig.persistence.query.margem.ListaHistoricoVariacaoMargemQuery;
import com.zetra.econsig.persistence.query.margem.ListaMargemRegistroServidorQuery;
import com.zetra.econsig.persistence.query.margem.ListaMargemRegistroServidoresQuery;
import com.zetra.econsig.persistence.query.orgao.ListaSubOrgaoQuery;
import com.zetra.econsig.persistence.query.orgao.ListaUnidadeQuery;
import com.zetra.econsig.persistence.query.orgao.ListaUnidadeSubOrgaoQuery;
import com.zetra.econsig.persistence.query.pergunta.ListaPerguntaDadosCadastraisQuery;
import com.zetra.econsig.persistence.query.registroservidor.ObtemRegistroServidorOcultoCsaQuery;
import com.zetra.econsig.persistence.query.registroservidor.ObtemTotalRegistroServidorQuery;
import com.zetra.econsig.persistence.query.servidor.ListaBloqueioConvenioServidorQuery;
import com.zetra.econsig.persistence.query.servidor.ListaComposicaoMargemRseQuery;
import com.zetra.econsig.persistence.query.servidor.ListaConsultaSalarioServidorQuery;
import com.zetra.econsig.persistence.query.servidor.ListaDadosServidorQuery;
import com.zetra.econsig.persistence.query.servidor.ListaEstadoCivilQuery;
import com.zetra.econsig.persistence.query.servidor.ListaNivelEscolaridadeQuery;
import com.zetra.econsig.persistence.query.servidor.ListaOcorrenciaRegistroServidorQuery;
import com.zetra.econsig.persistence.query.servidor.ListaOcorrenciaSerUnionRseQuery;
import com.zetra.econsig.persistence.query.servidor.ListaQtdeServidorPorOrgQuery;
import com.zetra.econsig.persistence.query.servidor.ListaRegistroServidorAuditoriaTotalQuery;
import com.zetra.econsig.persistence.query.servidor.ListaRegistroServidorPorCpfEmailQuery;
import com.zetra.econsig.persistence.query.servidor.ListaRegistroServidorQuery;
import com.zetra.econsig.persistence.query.servidor.ListaRegistroServidorSerCodigoQuery;
import com.zetra.econsig.persistence.query.servidor.ListaRegistroServidorUsuarioSerQuery;
import com.zetra.econsig.persistence.query.servidor.ListaRegistrosServidoresByCsaConvenioQuery;
import com.zetra.econsig.persistence.query.servidor.ListaRegistrosServidoresExcluidosQuery;
import com.zetra.econsig.persistence.query.servidor.ListaRegistrosServidoresLikeMatriculaQuery;
import com.zetra.econsig.persistence.query.servidor.ListaRegistrosServidoresQuery;
import com.zetra.econsig.persistence.query.servidor.ListaRegistrosServidoresTransferidosQuery;
import com.zetra.econsig.persistence.query.servidor.ListaSerParaArquivamentoQuery;
import com.zetra.econsig.persistence.query.servidor.ListaServidorPossuiAdeQuery;
import com.zetra.econsig.persistence.query.servidor.ListaServidorRseCodigoQuery;
import com.zetra.econsig.persistence.query.servidor.ListaTipoDadoAdicionalServidorQuery;
import com.zetra.econsig.persistence.query.servidor.ListaTipoHabitacaoQuery;
import com.zetra.econsig.persistence.query.servidor.ObtemDataOcorrenciaServidorQuery;
import com.zetra.econsig.persistence.query.servidor.ObtemServidorProprietarioAdeQuery;
import com.zetra.econsig.persistence.query.servidor.ObtemTotalServidoresPorEmailCelularQuery;
import com.zetra.econsig.persistence.query.transferencia.ObtemTotalConsignacaoSemConvenioTransfQuery;
import com.zetra.econsig.persistence.query.usuario.ObtemTotalUsuariosPorEmailQuery;
import com.zetra.econsig.persistence.query.vinculo.ListaCnvVinculoRegistroFaltanteQuery;
import com.zetra.econsig.service.arquivo.ArquivoController;
import com.zetra.econsig.service.beneficios.BeneficiarioController;
import com.zetra.econsig.service.consignacao.CancelarConsignacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignacao.TransferirConsignacaoController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.notificacao.NotificacaoDispositivoController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.pontuacao.PontuacaoServidorController;
import com.zetra.econsig.service.seguranca.SegurancaController;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.AcaoTipoDadoAdicionalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.values.OperacaoHistoricoMargemEnum;
import com.zetra.econsig.values.StatusBeneficiarioEnum;
import com.zetra.econsig.values.StatusProtocoloSenhaAutorizacaoEnum;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.values.TipoBeneficiarioEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;
import com.zetra.econsig.values.VencimentoEnum;
import com.zetra.econsig.values.VisibilidadeTipoDadoAdicionalEnum;

/**
 * <p>Title: ServidorControllerBean</p>
 * <p>Description: Session Bean para a operações relacionada a Servidores.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class ServidorControllerBean implements ServidorController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ServidorControllerBean.class);

    @Autowired
    private ConsultarMargemController consultarMargemController;

    @Autowired
    private CancelarConsignacaoController cancelarConsignacaoController;

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private PontuacaoServidorController pontuacaoServidorController;

    @Autowired
    private SistemaController sistemaController;

    @Autowired
    private ArquivoController arquivoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Autowired
    private TransferirConsignacaoController transferirConsignacaoController;

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private BeneficiarioController beneficiarioController;

    @Autowired
    private SegurancaController segurancaController;

    @Autowired
    private NotificacaoDispositivoController notificacaoDispositivoController;

    @Autowired
    private ConsignatariaController consignatariaController;

    /************************ SERVIDOR ******************************************/
    @Override
    public String cadastrarServidor(ServidorTransferObject servidor, RegistroServidorTO registroServidor, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            String serCodigo = null;
            String rseCodigo = null;
            // validações
            // CPF
            if (ParamSist.paramEquals(CodedValues.TPC_OMITE_CPF_SERVIDOR, CodedValues.TPC_NAO, responsavel) && TextHelper.isNull(servidor.getSerCpf())) {
                throw new ServidorControllerException("mensagem.erro.campo.nulo", responsavel, ApplicationResourcesHelper.getMessage("rotulo.cpf", responsavel));
            }
            // Matrícula
            if (!TextHelper.isNull(registroServidor.getRseMatricula())) {
                // Verifica se a matrícula é numérica
                try {
                    if (ParamSist.paramEquals(CodedValues.TPC_MATRICULA_NUMERICA, CodedValues.TPC_SIM, responsavel)) {
                        final long matricula = Long.parseLong(registroServidor.getRseMatricula());
                        if (matricula <= 0) {
                            throw new ServidorControllerException("mensagem.erro.matricula.invalida", responsavel);
                        }
                        registroServidor.setRseMatricula(Long.toString(matricula));
                    }
                } catch (final NumberFormatException ex) {
                    throw new ServidorControllerException("mensagem.erro.matricula.invalida", responsavel, ex);
                }

                // Pega o tamanho mínimo da matrícula
                int tamanhoMatricula = 0;
                try {
                    if (ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MATRICULA, responsavel) != null) {
                        tamanhoMatricula = Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MATRICULA, responsavel).toString());
                    }
                } catch (final NumberFormatException ex) {
                }

                if (registroServidor.getRseMatricula().length() < tamanhoMatricula) {
                    throw new ServidorControllerException("mensagem.erro.matricula.invalida", responsavel);
                }
            }
            // Situação do servidor
            if ((registroServidor != null) && !CodedValues.SRS_ATIVO.equals(registroServidor.getSrsCodigo()) && !CodedValues.SRS_PENDENTE.equals(registroServidor.getSrsCodigo())) {
                throw new ServidorControllerException("mensagem.erro.situacao.servidor.invalida", responsavel);
            }
            // Convênio ativo
            if (responsavel.isCsaCor() && (registroServidor != null) && !TextHelper.isNull(registroServidor.getOrgCodigo())) {
                try {
                    final List<TransferObject> convenios = convenioController.lstConvenios(null, responsavel.getCsaCodigo(), null, registroServidor.getOrgCodigo(), true, responsavel);
                    if ((convenios == null) || convenios.isEmpty()) {
                        throw new ServidorControllerException("mensagem.convenioNaoEncontrado", responsavel);
                    }
                } catch (final ConvenioControllerException e) {
                    throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel);
                }
            }

            // Verifica se o servidor já existe
            if (ParamSist.paramEquals(CodedValues.TPC_OMITE_CPF_SERVIDOR, CodedValues.TPC_NAO, responsavel)) {
                // Pesquisa servidores pelo CPF
                List<Servidor> servidoresMesmoCpf = null;
                try {
                    servidoresMesmoCpf = ServidorHome.findByCPF(servidor.getSerCpf());
                } catch (final FindException ex) {
                    throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel);
                }
                // Se encontrou, verifica se existe um com mesmo nome
                if ((servidoresMesmoCpf != null) && (servidoresMesmoCpf.size() > 0)) {
                    for (final Servidor ser : servidoresMesmoCpf) {
                        if (ser.getSerNome().equalsIgnoreCase(servidor.getSerNome())) {
                            serCodigo = ser.getSerCodigo();
                            break;
                        }
                    }
                    if (TextHelper.isNull(serCodigo)) {
                        // Se não encontrou um com mesmo nome e CPF, verifica se apenas um
                        // registro existe para o CPF, e caso exista, utiliza este atualizando os dados
                        if (servidoresMesmoCpf.size() == 1) {
                            serCodigo = servidoresMesmoCpf.get(0).getSerCodigo();
                            servidor.setSerCodigo(serCodigo);
                        } else {
                            throw new ServidorControllerException("mensagem.erro.mais.de.um.servidor.encontrado.cpf", responsavel);
                        }
                    }
                }
            } else {
                // Pesquisa servidores pelo nome, sobrenome e data de nascimento
                final TransferObject criterios = new CustomTransferObject();
                criterios.setAttribute("responsavel", responsavel);
                try {
                    // define se pesquisa pelo nome completo ou pelo primeiro e último nome
                    if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRIMEIRO_NOME, responsavel)) {
                        criterios.setAttribute("NOME", servidor.getSerPrimeiroNome());
                        criterios.setAttribute("SOBRENOME", servidor.getSerUltimoNome());
                    } else {
                        criterios.setAttribute("NOME", servidor.getSerNome());
                    }
                } catch (final ZetraException e) {
                    throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel);
                }

                criterios.setAttribute("serDataNascimento", servidor.getSerDataNasc());

                final List<TransferObject> lstServidores = pesquisarServidorController.pesquisaServidor(responsavel.getTipoEntidade(), responsavel.getCodigoEntidade(), null, null, null, null, -1, -1, responsavel, false, null, false, null, criterios);
                if ((lstServidores != null) && (lstServidores.size() == 1)) {
                    serCodigo = (String) lstServidores.get(0).getAttribute(Columns.SER_CODIGO);
                    servidor.setSerCodigo(serCodigo);
                } else if ((lstServidores != null) && (lstServidores.size() > 1)) {
                    /* OBS.: a pesquisa de registro servidor pode retornar mais de um registro nos seguintes casos:
                     * 1. o servidor possui mais de um cadastro funcional, para órgãos diferentes
                     * 2. a pesquisa usando LIKE pode retornar resultados onde o nome não seja idêntico ao pesquisado,
                     *    coincidindo a data de nascimento.
                     *    Ex.: ANA BEATRIZ DA SILVA e ANABELA MARIA DA SILVA
                     * 3. homonimos ou primeiro e último nome iguais com mesma data de nascimento
                     *    Ex.: ANA BEATRIZ DA SILVA e ANA MARIA DA SILVA                     *
                     *
                     * Comportamento para os casos acima:
                     * Caso 1: Retornar o primeiro da lista.
                     * Caso 2: Verificar se possui somente um registro com o nome idêntico ao informado para a pesquisa. Se sim, retornar
                     * o registro. Caso contrário, cai no caso 3.
                     * Caso 3: Retornar erro informando que mais de um servidor foi encontrado para a pesquisa.
                     */

                    List<String> serCodigos = new ArrayList<>();
                    // Verifica caso 1:
                    for (final TransferObject ser : lstServidores) {
                        if (!serCodigos.contains(ser.getAttribute(Columns.SER_CODIGO))) {
                            serCodigos.add(ser.getAttribute(Columns.SER_CODIGO).toString());
                        }
                    }
                    // Verifica caso 2:
                    if (serCodigos.size() > 1) {
                        serCodigos = new ArrayList<>();
                        for (final TransferObject ser : lstServidores) {
                            try {
                                if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRIMEIRO_NOME, responsavel)) {
                                    // pesquisa por primeiro e último nome
                                    if (!serCodigos.contains(ser.getAttribute(Columns.SER_CODIGO)) && servidor.getSerPrimeiroNome().equals(ser.getAttribute(Columns.SER_PRIMEIRO_NOME)) && servidor.getSerUltimoNome().equals(ser.getAttribute(Columns.SER_ULTIMO_NOME))) {
                                        serCodigos.add(ser.getAttribute(Columns.SER_CODIGO).toString());
                                    }
                                } else // pesquisa por nome completo
                                if (!serCodigos.contains(ser.getAttribute(Columns.SER_CODIGO)) && servidor.getSerNome().equals(ser.getAttribute(Columns.SER_NOME))) {
                                    serCodigos.add(ser.getAttribute(Columns.SER_CODIGO).toString());
                                }
                            } catch (final ZetraException e) {
                                throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel);
                            }
                        }
                        // Verifica caso 3:
                        if (serCodigos.size() > 1) {
                            throw new ServidorControllerException("mensagem.erro.mais.de.um.servidor.encontrado.nome.data.nascimento", responsavel);
                        }
                    }

                    // Se o caso 1 e 2 foram resolvidos, preenche o ser_codigo para atualização
                    if (serCodigos.size() == 1) {
                        serCodigo = serCodigos.get(0);
                        servidor.setSerCodigo(serCodigo);
                    }
                }
            }

            if (!TextHelper.isNull(serCodigo)) {
                // se encontrou um servidor, atualiza seus dados
                updateServidor(servidor, responsavel);
            } else {
                // se não encontrou servidor, cria um novo
                if (ParamSist.paramEquals(CodedValues.TPC_OMITE_CPF_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                    // gera um CPF para o servidor, necessário pois o campo é chave
                    servidor.setSerCpf(GeradorCpfServidor.getInstance().getNext());
                }
                serCodigo = createServidor(servidor, responsavel);
                criaOcorrenciaSER(serCodigo, CodedValues.TOC_INFORMACAO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ocs.obs.inclusao.manual", responsavel), null, responsavel);
            }

            // Define o código do servidor no registro servidor
            registroServidor.setSerCodigo(serCodigo);

            // Verifica se a matrícula existe no órgão informado
            try {
                final RegistroServidor rse = RegistroServidorHome.findByMatriculaOrgao(registroServidor.getRseMatricula(), registroServidor.getOrgCodigo());
                rseCodigo = rse.getRseCodigo();

                // Caso a matrícula existe no órgão, verifica se está ligada ao servidor informado
                if (!rse.getServidor().getSerCodigo().equals(serCodigo)) {
                    throw new ServidorControllerException("mensagem.erro.matricula.ja.cadastrada.outro.servidor", responsavel);
                } else {
                    updateRegistroServidorSemHistoricoMargem(registroServidor, responsavel);
                }
            } catch (final FindException ex) {
                // Caso não exista, procede a inclusão
                rseCodigo = createRegistroServidor(registroServidor, responsavel);
                criaOcorrenciaRSE(rseCodigo, CodedValues.TOC_RSE_INCLUSAO_MANUAL, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ors.obs.inclusao.manual", responsavel), null, responsavel);
                // Envia e-mail ao CSE/ORG relativo ao cadastro do servidor
                if (responsavel.isCsaCor() && ParamSist.paramEquals(CodedValues.TPC_ENVIA_EMAIL_NOTIFICACAO_CAD_SERVIDOR, CodedValues.TPC_SIM, responsavel) && (registroServidor.getSrsCodigo() != null) && CodedValues.SRS_PENDENTE.equals(registroServidor.getSrsCodigo())) {
                    EnviaEmailHelper.enviarEmailNotificacaoCadastroServidor(rseCodigo, responsavel);
                }
            }

            return serCodigo;
        } catch (final ViewHelperException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ServidorControllerException(ex);
        } catch (final ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw ex;
        }
    }

    protected String createServidor(ServidorTransferObject servidor, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final Servidor serBean = ServidorHome.create(servidor.getSerCpf(), servidor.getSerDataNasc(), servidor.getSerNomeMae(), servidor.getSerNomePai(), servidor.getSerNome(), servidor.getSerPrimeiroNome(), servidor.getSerNomeMeio(), servidor.getSerUltimoNome(), servidor.getSerTitulacao(), servidor.getSerSexo(), servidor.getSerEstCivil(), servidor.getSerQtdFilhos(), servidor.getSerNacionalidade(), servidor.getSerNroIdt(), servidor.getSerCartProf(), servidor.getSerPis(), servidor.getSerEnd(), servidor.getSerBairro(), servidor.getSerCidade(), servidor.getSerCompl(), servidor.getSerNro(),
                    servidor.getSerCep(), servidor.getSerUf(), servidor.getSerTel(), servidor.getSerEmail(), servidor.getSerEmissorIdt(), servidor.getSerUfIdt(), servidor.getSerDataIdt(), servidor.getSerCidNasc(), servidor.getSerUfNasc(), servidor.getSerNomeConjuge(), servidor.getSerDeficienteVisual(), servidor.getSerDataAlteracao(), servidor.getSerCelular(), servidor.getSerAcessaHostaHost(), servidor.getNesCodigo(), servidor.getThaCodigo(), servidor.getSseCodigo());

            final LogDelegate logDelegate = new LogDelegate(responsavel, Log.SERVIDOR, Log.CREATE, Log.LOG_INFORMACAO);
            logDelegate.setServidor(serBean.getSerCodigo());
            logDelegate.getUpdatedFields(servidor.getAtributos(), null);
            logDelegate.write();

            return serBean.getSerCodigo();
        } catch (final LogControllerException ex) {
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel);
        } catch (final CreateException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel);
        }
    }

    @Override
    public ServidorTransferObject findServidor(ServidorTransferObject servidor, AcessoSistema responsavel) throws ServidorControllerException {
        return setServidorValues(findServidorBean(servidor));
    }

    @Override
    public ServidorTransferObject findServidor(String serCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        return findServidor(new ServidorTransferObject(serCodigo), responsavel);
    }

    @Override
    public ServidorTransferObject findServidor(String serCpf, String rseMatricula, String serNroIdt, java.sql.Date serDataNasc, AcessoSistema responsavel) throws ServidorControllerException {
        ServidorTransferObject servidor = new ServidorTransferObject();
        servidor.setSerCpf(serCpf);

        servidor = findServidor(servidor, responsavel);
        if (!servidor.getSerNroIdt().equals(serNroIdt) || !servidor.getSerDataNasc().equals(serDataNasc)) {
            throw new ServidorControllerException("mensagem.nenhumServidorEncontrado", responsavel);
        }

        try {
            final List<RegistroServidor> registros = RegistroServidorHome.findBySerCodigo(servidor.getSerCodigo());
            boolean achou = false;
            for (final RegistroServidor rseBean : registros) {
                if (rseBean.getRseMatricula().equals(rseMatricula)) {
                    achou = true;
                    break;
                }
            }
            if (!achou) {
                throw new ServidorControllerException("mensagem.nenhumServidorEncontrado", responsavel);
            }

            return servidor;
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.nenhumServidorEncontrado", responsavel);
        }
    }

    /**
     * recupera os dados do cargo do servidor, caso preenchidos
     * @param serCodigos
     * @param responsavel
     * @return
     * @throws ServidorControllerException
     */
    @Override
    public TransferObject findCargoByCrsCodigo(String crsCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final CargoRegistroServidor crsRse = CargoRegistroServidorHome.findByPrimaryKey(crsCodigo);
            final TransferObject cargoSer = new CustomTransferObject();
            cargoSer.setAttribute(Columns.CRS_CODIGO, crsRse.getCrsCodigo());
            cargoSer.setAttribute(Columns.CRS_DESCRICAO, crsRse.getCrsDescricao());
            cargoSer.setAttribute(Columns.CRS_IDENTIFICADOR, crsRse.getCrsIdentificador());
            cargoSer.setAttribute(Columns.CRS_VLR_DESC_MAX, crsRse.getCrsVlrDescMax());

            return cargoSer;
        } catch (final FindException e) {
            return null;
        }
    }

    /**
     * Retorna lista de servidores ativos ordenados pela maior margem restante da natureza de empréstimo.
     *
     * @param serCpf
     * @param orgCodigoList
     * @param responsavel
     * @return
     * @throws ServidorControllerException
     */
    @Override
    public List<TransferObject> lstRegistroServidorPorCpf(String serCpf, List<String> orgCodigoList, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaRegistroServidorPorCpfEmailQuery query = new ListaRegistroServidorPorCpfEmailQuery();

            query.serCpf = serCpf;
            query.orgCodigos = orgCodigoList;
            query.recuperaRseExcluido = false;

            return ordenarRsePorMargemDesc(query.executarDTO(), responsavel);
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Retorna lista de servidores ativos ordenados pela maior margem restante da natureza de empréstimo.
     *
     * @param serEmail
     * @param orgCodigoList
     * @param responsavel
     * @return
     * @throws ServidorControllerException
     */
    @Override
    public List<TransferObject> lstRegistroServidorPorEmail(String serEmail, List<String> orgCodigoList, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaRegistroServidorPorCpfEmailQuery query = new ListaRegistroServidorPorCpfEmailQuery();

            query.serEmail = serEmail;
            query.orgCodigos = orgCodigoList;
            query.recuperaRseExcluido = false;

            return ordenarRsePorMargemDesc(query.executarDTO(), responsavel);
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Retorna lista de servidores passada como parâmetro ordenada pela maior margem restante
     * da natureza de empréstimo
     * @param registroServidores lista de servidores a serem ordenados
     * @param responsavel
     * @return
     * @throws ServidorControllerException
     */
    private List<TransferObject> ordenarRsePorMargemDesc(List<TransferObject> rses, AcessoSistema responsavel) throws ServidorControllerException {
        // Se houver somente um servidor, retorna a lista recebida sem tratamento
        if ((rses == null) || rses.isEmpty() || (rses.size() == 1)) {
            return rses;
        }

        final LinkedList<TransferObject> lstSerOrdenada = new LinkedList<>();

        outer: for (final TransferObject rse : rses) {
            final String rseCodigo = (String) rse.getAttribute(Columns.RSE_CODIGO);

            try {

                //busca código de um serviço de natureza de empréstimo para filtrar consulta de margem
                String svcCodigo = null;

                final List<TransferObject> lstSvcs = parametroController.lstServicoServidor(rseCodigo, null, CodedValues.NSE_EMPRESTIMO, true, responsavel);

                if ((lstSvcs != null) & !lstSvcs.isEmpty()) {
                    for (final TransferObject svcTO : lstSvcs) {
                        svcCodigo = (String) svcTO.getAttribute(Columns.SVC_CODIGO);
                        final List<MargemTO> margens = consultarMargemController.consultarMargem(rseCodigo, null, svcCodigo, null, null, false, null, false, true, null, null, responsavel);
                        if ((margens != null) && !margens.isEmpty()) {
                            final MargemTO margemTO = margens.get(0);
                            final TransferObject margemServidorTO = new CustomTransferObject(margemTO);
                            margemServidorTO.setAtributos(rse.getAtributos());
                            lstSerOrdenada.add(margemServidorTO);
                            continue outer;
                        }
                    }
                } else {
                    continue outer;
                }

            } catch (final ParametroControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }

        Collections.sort(lstSerOrdenada, (o1, o2) -> {
            final int compStatus = Short.valueOf((String) o1.getAttribute(Columns.SRS_CODIGO)).compareTo(Short.valueOf((String) o2.getAttribute(Columns.SRS_CODIGO)));

            if (compStatus == 0) {
                return ((BigDecimal) o2.getAttribute(Columns.MRS_MARGEM_REST)).compareTo((BigDecimal) o1.getAttribute(Columns.MRS_MARGEM_REST));
            }

            return compStatus;
        });

        return lstSerOrdenada;
    }

    /**
     * recupera dados do servidor ao qual pertence o contrato
     * @param adeCodigo código do contrato
     * @param responsavel
     * @return
     * @throws ServidorControllerException
     */
    @Override
    public TransferObject findServidorProprietarioAde(String adeCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        final ObtemServidorProprietarioAdeQuery ser = new ObtemServidorProprietarioAdeQuery();
        ser.adeCodigo = adeCodigo;

        try {
            final List<TransferObject> serList = ser.executarDTO();

            if (!serList.isEmpty()) {
                return serList.get(0);
            }

            return null;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * recupera o servidor pelo usuCodigo
     * @param usuCodigo código do usuário do servidor
     * @param responsavel
     * @return
     * @throws ServidorControllerException
     */
    @Override
    public ServidorTransferObject findServidorByUsuCodigo(String usuCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final Servidor servidorBean = ServidorHome.findByUsuCodigo(usuCodigo);

            return setServidorValues(servidorBean);
        } catch (final FindException e) {
            throw new ServidorControllerException("mensagem.nenhumServidorEncontrado", (AcessoSistema) null);
        }
    }

    @Override
    public ServidorTransferObject findServidorByRseCodigo(String rseCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final Servidor servidorBean = ServidorHome.findByRseCodigo(rseCodigo);

            return setServidorValues(servidorBean);
        } catch (final FindException e) {
            throw new ServidorControllerException("mensagem.nenhumServidorEncontrado", (AcessoSistema) null);
        }
    }

    private Servidor findServidorBean(ServidorTransferObject servidor) throws ServidorControllerException {
        Servidor servidorBean = null;
        if (servidor.getSerCodigo() != null) {
            try {
                servidorBean = ServidorHome.findByPrimaryKey(servidor.getSerCodigo());
            } catch (final FindException ex) {
                throw new ServidorControllerException("mensagem.nenhumServidorEncontrado", (AcessoSistema) null);
            }
        } else if ((servidor.getSerCpf() != null) && (servidor.getSerNome() != null)) {
            try {
                servidorBean = ServidorHome.findByCPFNome(servidor.getSerCpf(), servidor.getSerNome());
            } catch (final FindException ex) {
                throw new ServidorControllerException("mensagem.nenhumServidorEncontrado", (AcessoSistema) null);
            }
        } else {
            throw new ServidorControllerException("mensagem.nenhumServidorEncontrado", (AcessoSistema) null);
        }
        return servidorBean;
    }

    private ServidorTransferObject setServidorValues(Servidor servidorBean) {
        final ServidorTransferObject servidor = new ServidorTransferObject(servidorBean.getSerCodigo());

        servidor.setSerCpf(servidorBean.getSerCpf());
        servidor.setSerNomeMae(servidorBean.getSerNomeMae());
        servidor.setSerNomePai(servidorBean.getSerNomePai());
        servidor.setSerNome(servidorBean.getSerNome());
        servidor.setSerPrimeiroNome(servidorBean.getSerPrimeiroNome());
        servidor.setSerTitulacao(servidorBean.getSerTitulacao());
        servidor.setSerNomeMeio(servidorBean.getSerNomeMeio());
        servidor.setSerUltimoNome(servidorBean.getSerUltimoNome());
        servidor.setSerSexo(servidorBean.getSerSexo());
        servidor.setSerEstCivil(servidorBean.getSerEstCivil());
        servidor.setSerNacionalidade(servidorBean.getSerNacionalidade());
        servidor.setSerNroIdt(servidorBean.getSerNroIdt());
        servidor.setSerEmissorIdt(servidorBean.getSerEmissorIdt());
        servidor.setSerUfIdt(servidorBean.getSerUfIdt());
        servidor.setSerCartProf(servidorBean.getSerCartProf());
        servidor.setSerPis(servidorBean.getSerPis());
        servidor.setSerEnd(servidorBean.getSerEnd());
        servidor.setSerBairro(servidorBean.getSerBairro());
        servidor.setSerCidade(servidorBean.getSerCidade());
        servidor.setSerCompl(servidorBean.getSerCompl());
        servidor.setSerNro(servidorBean.getSerNro() == null ? null : servidorBean.getSerNro().toString());
        servidor.setSerCep(servidorBean.getSerCep());
        servidor.setSerUf(servidorBean.getSerUf());
        servidor.setSerTel(servidorBean.getSerTel());
        servidor.setSerCelular(servidorBean.getSerCelular());
        servidor.setSerEmail(servidorBean.getSerEmail());
        servidor.setSerCidNasc(servidorBean.getSerCidNasc());
        servidor.setSerUfNasc(servidorBean.getSerUfNasc());
        servidor.setSerNomeConjuge(servidorBean.getSerNomeConjuge());
        servidor.setSerDeficienteVisual(servidorBean.getSerDeficienteVisual() != null ? servidorBean.getSerDeficienteVisual().toString() : null);
        servidor.setSerAcessaHostaHost(servidorBean.getSerAcessaHostAHost() != null ? servidorBean.getSerAcessaHostAHost().toString() : null);
        servidor.setSerQtdFilhos(servidorBean.getSerQtdFilhos() != null ? servidorBean.getSerQtdFilhos() : null);
        servidor.setSerDispensaDigital(servidorBean.getSerDispensaDigital());
        servidor.setSerDataIdentificacaoPessoal(servidorBean.getSerDataIdentificacaoPessoal());
        servidor.setSerDataValidacaoEmail(servidorBean.getSerDataValidacaoEmail());
        servidor.setSerPermiteAlterarEmail(servidorBean.getSerPermiteAlterarEmail());

        if (servidorBean.getNivelEscolaridade() != null) {
            servidor.setNesCodigo(servidorBean.getNivelEscolaridade().getNesCodigo());
        }
        if (servidorBean.getTipoHabitacao() != null) {
            servidor.setThaCodigo(servidorBean.getTipoHabitacao().getThaCodigo());
        }
        if (servidorBean.getStatusServidor() != null) {
            servidor.setSseCodigo(servidorBean.getStatusServidor().getSseCodigo());
        }
        if (servidorBean.getUsuario() != null) {
            servidor.setUsuCodigo(servidorBean.getUsuario().getUsuCodigo());
        }
        if (servidorBean.getSerDataNasc() != null) {
            servidor.setSerDataNasc(DateHelper.toSQLDate(servidorBean.getSerDataNasc()));
        }
        if (servidorBean.getSerDataIdt() != null) {
            servidor.setSerDataIdt(DateHelper.toSQLDate(servidorBean.getSerDataIdt()));
        }
        if (servidorBean.getSerDataAlteracao() != null) {
            servidor.setSerDataAlteracao(new java.sql.Timestamp(servidorBean.getSerDataAlteracao().getTime()));
        }

        return servidor;
    }

    @Override
    public void updateServidor(ServidorTransferObject servidor, AcessoSistema responsavel) throws ServidorControllerException {
        updateServidor(servidor, CodedValues.TOC_RSE_ALTERACAO_DADOS_CADASTRAIS, true, responsavel);
    }

    @Override
    public void updateServidor(ServidorTransferObject servidor, String tocCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        updateServidor(servidor, tocCodigo, true, responsavel);
    }

    @Override
    public String updateServidor(ServidorTransferObject servidor, boolean enviaEmail, AcessoSistema responsavel) throws ServidorControllerException {
        return updateServidor(servidor, CodedValues.TOC_RSE_ALTERACAO_DADOS_CADASTRAIS, enviaEmail, responsavel);
    }

    private String updateServidor(ServidorTransferObject servidor, String tocCodigo, boolean enviaEmail, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final Servidor servidorBean = findServidorBean(servidor);
            final LogDelegate log = new LogDelegate(responsavel, Log.SERVIDOR, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setServidor(servidorBean.getSerCodigo());

            boolean cpfModificado = false;

            /* Compara a versão do cache com a passada por parâmetro */
            final ServidorTransferObject servidorCache = setServidorValues(servidorBean);
            final CustomTransferObject merge = log.getUpdatedFields(servidor.getAtributos(), servidorCache.getAtributos());

            final StringBuilder msgOcs = new StringBuilder();

            if (merge.getAtributos().containsKey(Columns.SER_CPF)) {
                // Verifica se não existe outro servidor com mesmo nome e CPF
                final ServidorTransferObject teste = new ServidorTransferObject();
                teste.setSerCpf((String) merge.getAttribute(Columns.SER_CPF));
                teste.setSerNome(servidorBean.getSerNome());

                boolean existe = false;
                try {
                    final Servidor serTeste = findServidorBean(teste);
                    existe = (serTeste != null) && !serTeste.getSerCodigo().equals(servidor.getSerCodigo());
                } catch (final ServidorControllerException ex) {
                    LOG.error(ex.getMessage());
                }
                if (existe) {
                    throw new ServidorControllerException("mensagem.erro.nao.possivel.excluir.registro.servidor.pois.existe.outro.mesmo.nome.cpf", responsavel);
                }

                final String cpfOld = servidorBean.getSerCpf() != null ? servidorBean.getSerCpf().toString() : "";
                final String cpfNew = merge.getAttribute(Columns.SER_CPF) != null ? merge.getAttribute(Columns.SER_CPF).toString() : "";
                if (!cpfOld.equals(cpfNew)) {
                    msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.cpf.alterado.de.arg0.para.arg1", responsavel, cpfOld, cpfNew));
                }
                servidorBean.setSerCpf((String) merge.getAttribute(Columns.SER_CPF));
                cpfModificado = true;
            }

            String primeiroNomeNew = null;
            String nomeMeioNew = null;
            String titulacaoNew = null;
            String ultimoNomeNew = null;
            boolean alterouNome = false;

            if (merge.getAtributos().containsKey(Columns.SER_BAIRRO)) {
                final String bairroOld = servidorBean.getSerBairro() != null ? servidorBean.getSerBairro().toString() : "";
                final String bairroNew = merge.getAttribute(Columns.SER_BAIRRO) != null ? merge.getAttribute(Columns.SER_BAIRRO).toString() : "";
                if (!bairroOld.equals(bairroNew)) {
                    msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.bairro.alterado.de.arg0.para.arg1", responsavel, bairroOld, bairroNew));
                }
                servidorBean.setSerBairro((String) merge.getAttribute(Columns.SER_BAIRRO));
            }
            if (merge.getAtributos().containsKey(Columns.SER_CART_PROF)) {
                final String cartProfOld = servidorBean.getSerCartProf() != null ? servidorBean.getSerCartProf().toString() : "";
                final String cartProfNew = merge.getAttribute(Columns.SER_CART_PROF) != null ? merge.getAttribute(Columns.SER_CART_PROF).toString() : "";
                if (!cartProfOld.equals(cartProfNew)) {
                    msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.cart.trabalho.alterada.de.arg0.para.arg1", responsavel, cartProfOld, cartProfNew));
                }
                servidorBean.setSerCartProf((String) merge.getAttribute(Columns.SER_CART_PROF));
            }
            if (merge.getAtributos().containsKey(Columns.SER_CEP)) {
                final String cepOld = servidorBean.getSerCep() != null ? servidorBean.getSerCep().toString() : "";
                final String cepNew = merge.getAttribute(Columns.SER_CEP) != null ? merge.getAttribute(Columns.SER_CEP).toString() : "";
                if (!cepOld.equals(cepNew)) {
                    msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.cep.alterado.de.arg0.para.arg1", responsavel, cepOld, cepNew));
                }
                servidorBean.setSerCep((String) merge.getAttribute(Columns.SER_CEP));
            }
            if (merge.getAtributos().containsKey(Columns.SER_CIDADE)) {
                final String cidadeOld = servidorBean.getSerCidade() != null ? servidorBean.getSerCidade().toString() : "";
                final String cidadeNew = merge.getAttribute(Columns.SER_CIDADE) != null ? merge.getAttribute(Columns.SER_CIDADE).toString() : "";
                if (!cidadeOld.equals(cidadeNew)) {
                    msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.cidade.alterada.de.arg0.para.arg1", responsavel, cidadeOld, cidadeNew));
                }
                servidorBean.setSerCidade((String) merge.getAttribute(Columns.SER_CIDADE));
            }
            if (merge.getAtributos().containsKey(Columns.SER_COMPL)) {
                final String complOld = servidorBean.getSerCompl() != null ? servidorBean.getSerCompl().toString() : "";
                final String complNew = merge.getAttribute(Columns.SER_COMPL) != null ? merge.getAttribute(Columns.SER_COMPL).toString() : "";
                if (!complOld.equals(complNew)) {
                    msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.complemento.alterado.de.arg0.para.arg1", responsavel, complOld, complNew));
                }
                servidorBean.setSerCompl((String) merge.getAttribute(Columns.SER_COMPL));
            }
            if (merge.getAtributos().containsKey(Columns.SER_DATA_NASC)) {
                final String dataOld = servidorBean.getSerDataNasc() != null ? DateHelper.toDateString(servidorBean.getSerDataNasc()) : "";
                final String dataNew = merge.getAttribute(Columns.SER_DATA_NASC) != null ? DateHelper.toDateString((Date) merge.getAttribute(Columns.SER_DATA_NASC)) : "";
                if (!dataOld.equals(dataNew)) {
                    msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.data.nascimento.alterada.de.arg0.para.arg1", responsavel, dataOld, dataNew));
                }
                servidorBean.setSerDataNasc((java.sql.Date) merge.getAttribute(Columns.SER_DATA_NASC));
            }
            if (merge.getAtributos().containsKey(Columns.SER_END)) {
                final String endOld = servidorBean.getSerEnd() != null ? servidorBean.getSerEnd().toString() : "";
                final String endNew = merge.getAttribute(Columns.SER_END) != null ? merge.getAttribute(Columns.SER_END).toString() : "";
                if (!endOld.equals(endNew)) {
                    msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.endereco.alterado.de.arg0.para.arg1", responsavel, endOld, endNew));
                }
                servidorBean.setSerEnd((String) merge.getAttribute(Columns.SER_END));
            }
            if (merge.getAtributos().containsKey(Columns.SER_NRO)) {
                final String nroOld = servidorBean.getSerNro() != null ? servidorBean.getSerNro().toString() : "";
                final String nroNew = merge.getAttribute(Columns.SER_NRO) != null ? merge.getAttribute(Columns.SER_NRO).toString() : "";
                if (!nroOld.equals(nroNew)) {
                    msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.numero.alterado.de.arg0.para.arg1", responsavel, nroOld, nroNew));
                }
                servidorBean.setSerNro((String) merge.getAttribute(Columns.SER_NRO));
            }
            if (merge.getAtributos().containsKey(Columns.SER_EST_CIVIL)) {
                final String estCivilOld = servidorBean.getSerEstCivil() != null ? servidorBean.getSerEstCivil().toString() : "";
                final String estCivilNew = merge.getAttribute(Columns.SER_EST_CIVIL) != null ? merge.getAttribute(Columns.SER_EST_CIVIL).toString() : "";
                if (!estCivilOld.equals(estCivilNew)) {
                    msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.estado.civil.alterado.de.arg0.para.arg1", responsavel, estCivilOld, estCivilNew));
                }
                servidorBean.setSerEstCivil((String) merge.getAttribute(Columns.SER_EST_CIVIL));
            }
            if (merge.getAtributos().containsKey(Columns.SER_NACIONALIDADE)) {
                final String nacionalidadeOld = servidorBean.getSerNacionalidade() != null ? servidorBean.getSerNacionalidade().toString() : "";
                final String nacionalidadeNew = merge.getAttribute(Columns.SER_NACIONALIDADE) != null ? merge.getAttribute(Columns.SER_NACIONALIDADE).toString() : "";
                if (!nacionalidadeOld.equals(nacionalidadeNew)) {
                    msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.nacionalidade.alterada.de.arg0.para.arg1", responsavel, nacionalidadeOld, nacionalidadeNew));
                }
                servidorBean.setSerNacionalidade((String) merge.getAttribute(Columns.SER_NACIONALIDADE));
            }
            if (!TextHelper.isNull(merge.getAttribute(Columns.SER_NOME)) && merge.getAtributos().containsKey(Columns.SER_NOME)) {
                final String nomeOld = servidorBean.getSerNome() != null ? servidorBean.getSerNome().toString() : "";
                titulacaoNew = merge.getAttribute(Columns.SER_NOME) != null ? merge.getAttribute(Columns.SER_NOME).toString() : "";
                if (!nomeOld.equals(titulacaoNew)) {
                    msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.nome.alterado.de.arg0.para.arg1", responsavel, nomeOld, titulacaoNew));
                }
                servidorBean.setSerNome((String) merge.getAttribute(Columns.SER_NOME));
            }
            if (merge.getAtributos().containsKey(Columns.SER_TITULACAO)) {
                final String nomeOld = servidorBean.getSerTitulacao() != null ? servidorBean.getSerTitulacao().toString() : "";
                titulacaoNew = merge.getAttribute(Columns.SER_TITULACAO) != null ? merge.getAttribute(Columns.SER_TITULACAO).toString() : "";
                if (!nomeOld.equals(titulacaoNew)) {
                    alterouNome = true;
                    msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.titularizacao.alterado.de.arg0.para.arg1", responsavel, nomeOld, titulacaoNew));
                }
                servidorBean.setSerTitulacao((String) merge.getAttribute(Columns.SER_TITULACAO));
            }
            if (merge.getAtributos().containsKey(Columns.SER_PRIMEIRO_NOME)) {
                final String nomeOld = servidorBean.getSerPrimeiroNome() != null ? servidorBean.getSerPrimeiroNome().toString() : "";
                primeiroNomeNew = merge.getAttribute(Columns.SER_PRIMEIRO_NOME) != null ? merge.getAttribute(Columns.SER_PRIMEIRO_NOME).toString() : "";
                if (!nomeOld.equals(primeiroNomeNew)) {
                    alterouNome = true;
                    msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.primeiro.nome.alterado.de.arg0.para.arg1", responsavel, nomeOld, primeiroNomeNew));
                }
                servidorBean.setSerPrimeiroNome((String) merge.getAttribute(Columns.SER_PRIMEIRO_NOME));
            }
            if (merge.getAtributos().containsKey(Columns.SER_NOME_MEIO)) {
                final String nomeOld = servidorBean.getSerNomeMeio() != null ? servidorBean.getSerNomeMeio().toString() : "";
                nomeMeioNew = merge.getAttribute(Columns.SER_NOME_MEIO) != null ? merge.getAttribute(Columns.SER_NOME_MEIO).toString() : "";
                if (!nomeOld.equals(nomeMeioNew)) {
                    alterouNome = true;
                    msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.nome.meio.alterado.de.arg0.para.arg1", responsavel, nomeOld, nomeMeioNew));
                }
                servidorBean.setSerNomeMeio((String) merge.getAttribute(Columns.SER_NOME_MEIO));
            }
            if (merge.getAtributos().containsKey(Columns.SER_ULTIMO_NOME)) {
                final String nomeOld = servidorBean.getSerUltimoNome() != null ? servidorBean.getSerUltimoNome().toString() : "";
                ultimoNomeNew = merge.getAttribute(Columns.SER_ULTIMO_NOME) != null ? merge.getAttribute(Columns.SER_ULTIMO_NOME).toString() : "";
                if (!nomeOld.equals(ultimoNomeNew)) {
                    alterouNome = true;
                    msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.ultimo.nome.alterado.de.arg0.para.arg1", responsavel, nomeOld, ultimoNomeNew));
                }
                servidorBean.setSerUltimoNome((String) merge.getAttribute(Columns.SER_ULTIMO_NOME));
            }

            // Se alterou um dos componentes do nome (em sistemas que os utilizam), altera também o campo com o nome completo
            if (alterouNome) {
                servidorBean.setSerNome(JspHelper.montaSerNome(servidorBean.getSerTitulacao(), servidorBean.getSerPrimeiroNome(), servidorBean.getSerNomeMeio(), servidorBean.getSerUltimoNome()));
            }

            if (merge.getAtributos().containsKey(Columns.SER_NOME_MAE)) {
                final String nomeMaeOld = servidorBean.getSerNomeMae() != null ? servidorBean.getSerNomeMae().toString() : "";
                final String nomeMaeNew = merge.getAttribute(Columns.SER_NOME_MAE) != null ? merge.getAttribute(Columns.SER_NOME_MAE).toString() : "";
                if (!nomeMaeOld.equals(nomeMaeNew)) {
                    msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.nome.mae.alterado.de.arg0.para.arg1", responsavel, nomeMaeOld, nomeMaeNew));
                }
                servidorBean.setSerNomeMae((String) merge.getAttribute(Columns.SER_NOME_MAE));
            }
            if (merge.getAtributos().containsKey(Columns.SER_NOME_PAI)) {
                final String nomePaiOld = servidorBean.getSerNomePai() != null ? servidorBean.getSerNomePai().toString() : "";
                final String nomePaiNew = merge.getAttribute(Columns.SER_NOME_PAI) != null ? merge.getAttribute(Columns.SER_NOME_PAI).toString() : "";
                if (!nomePaiOld.equals(nomePaiNew)) {
                    msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.nome.pai.alterado.de.arg0.para.arg1", responsavel, nomePaiOld, nomePaiNew));
                }
                servidorBean.setSerNomePai((String) merge.getAttribute(Columns.SER_NOME_PAI));
            }
            if (merge.getAtributos().containsKey(Columns.SER_NOME_CONJUGE)) {
                final String nomeConjugeOld = servidorBean.getSerNomeConjuge() != null ? servidorBean.getSerNomeConjuge().toString() : "";
                final String nomeConjugeNew = merge.getAttribute(Columns.SER_NOME_CONJUGE) != null ? merge.getAttribute(Columns.SER_NOME_CONJUGE).toString() : "";
                if (!nomeConjugeOld.equals(nomeConjugeNew)) {
                    msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.nome.conjuge.alterado.de.arg0.para.arg1", responsavel, nomeConjugeOld, nomeConjugeNew));
                }
                servidorBean.setSerNomeConjuge((String) merge.getAttribute(Columns.SER_NOME_CONJUGE));
            }
            if (merge.getAtributos().containsKey(Columns.SER_NRO_IDT)) {
                final String nroIdtOld = servidorBean.getSerNroIdt() != null ? servidorBean.getSerNroIdt().toString() : "";
                final String nroIdtNew = merge.getAttribute(Columns.SER_NRO_IDT) != null ? merge.getAttribute(Columns.SER_NRO_IDT).toString() : "";
                if (!nroIdtOld.equals(nroIdtNew)) {
                    msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.numero.carteira.identidade.alterado.de.arg0.para.arg1", responsavel, nroIdtOld, nroIdtNew));
                }
                servidorBean.setSerNroIdt((String) merge.getAttribute(Columns.SER_NRO_IDT));
            }
            if (merge.getAtributos().containsKey(Columns.SER_EMISSOR_IDT)) {
                final String emissorIdtOld = servidorBean.getSerEmissorIdt() != null ? servidorBean.getSerEmissorIdt().toString() : "";
                final String emissorIdtNew = merge.getAttribute(Columns.SER_EMISSOR_IDT) != null ? merge.getAttribute(Columns.SER_EMISSOR_IDT).toString() : "";
                if (!emissorIdtOld.equals(emissorIdtNew)) {
                    msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.emissor.carteira.identidade.alterado.de.arg0.para.arg1", responsavel, emissorIdtOld, emissorIdtNew));
                }
                servidorBean.setSerEmissorIdt((String) merge.getAttribute(Columns.SER_EMISSOR_IDT));
            }
            if (merge.getAtributos().containsKey(Columns.SER_UF_IDT)) {
                final String ufIdtOld = servidorBean.getSerUfIdt() != null ? servidorBean.getSerUfIdt().toString() : "";
                final String ufIdtNew = merge.getAttribute(Columns.SER_UF_IDT) != null ? merge.getAttribute(Columns.SER_UF_IDT).toString() : "";
                if (!ufIdtOld.equals(ufIdtNew)) {
                    msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.uf.emissao.carteira.identidade.alterado.de.arg0.para.arg1", responsavel, ufIdtOld, ufIdtNew));
                }
                servidorBean.setSerUfIdt((String) merge.getAttribute(Columns.SER_UF_IDT));
            }
            if (merge.getAtributos().containsKey(Columns.SER_DATA_IDT)) {
                final String dataOld = servidorBean.getSerDataIdt() != null ? DateHelper.toDateString(servidorBean.getSerDataIdt()) : "";
                final String dataNew = merge.getAttribute(Columns.SER_DATA_IDT) != null ? DateHelper.toDateString((Date) merge.getAttribute(Columns.SER_DATA_IDT)) : "";
                if (!dataOld.equals(dataNew)) {
                    msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.data.emissao.carteira.identidade.alterada.de.arg0.para.arg1", responsavel, dataOld, dataNew));
                }
                servidorBean.setSerDataIdt((java.sql.Date) merge.getAttribute(Columns.SER_DATA_IDT));
            }
            if (merge.getAtributos().containsKey(Columns.SER_PIS)) {
                final String pisOld = servidorBean.getSerPis() != null ? servidorBean.getSerPis().toString() : "";
                final String pisNew = merge.getAttribute(Columns.SER_PIS) != null ? merge.getAttribute(Columns.SER_PIS).toString() : "";
                if (!pisOld.equals(pisNew)) {
                    msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.numero.pis.alterado.de.arg0.para.arg1", responsavel, pisOld, pisNew));
                }
                servidorBean.setSerPis((String) merge.getAttribute(Columns.SER_PIS));
            }
            if (merge.getAtributos().containsKey(Columns.SER_SEXO)) {
                final String sexoOld = servidorBean.getSerSexo() != null ? servidorBean.getSerSexo().toString() : "";
                final String sexoNew = merge.getAttribute(Columns.SER_SEXO) != null ? merge.getAttribute(Columns.SER_SEXO).toString() : "";
                if (!sexoOld.equals(sexoNew)) {
                    msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.sexo.alterado.de.arg0.para.arg1", responsavel, sexoOld, sexoNew));
                }
                servidorBean.setSerSexo((String) merge.getAttribute(Columns.SER_SEXO));
            }

            if (merge.getAtributos().containsKey(Columns.SER_QTD_FILHOS)) {
                final String qtdFilhosOld = servidorBean.getSerQtdFilhos() != null ? servidorBean.getSerQtdFilhos().toString() : "";
                final String qtdFilhosnew = merge.getAttribute(Columns.SER_QTD_FILHOS) != null ? merge.getAttribute(Columns.SER_QTD_FILHOS).toString() : "";
                if (!qtdFilhosOld.equals(qtdFilhosnew)) {
                    msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.qtd.filhos.alterado.de.arg0.para.arg1", responsavel, qtdFilhosOld, qtdFilhosnew));
                }
                servidorBean.setSerQtdFilhos((Short) merge.getAttribute(Columns.SER_QTD_FILHOS));
            }

            if (merge.getAtributos().containsKey(Columns.SER_NES_CODIGO)) {
                final String getNesOld = servidorBean.getNivelEscolaridade() != null ? servidorBean.getNivelEscolaridade().getNesCodigo().toString() : "";
                final String getNesNew = merge.getAttribute(Columns.SER_NES_CODIGO) != null ? merge.getAttribute(Columns.SER_NES_CODIGO).toString() : "";
                if (!getNesOld.equals(getNesNew)) {
                    msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.nivel.escolaridade.alterado.de.arg0.para.arg1", responsavel, getNesOld, getNesNew));
                }
                servidorBean.setNivelEscolaridade((merge.getAttribute(Columns.SER_NES_CODIGO) != null) && !"".equals(merge.getAttribute(Columns.SER_NES_CODIGO)) ? NivelEscolaridadeHome.findByPrimaryKey((String) merge.getAttribute(Columns.SER_NES_CODIGO)) : null);
            }

            if (merge.getAtributos().containsKey(Columns.SER_THA_CODIGO)) {
                final String getThaOld = servidorBean.getTipoHabitacao() != null ? servidorBean.getTipoHabitacao().getThaCodigo().toString() : "";
                final String getThaNew = merge.getAttribute(Columns.SER_THA_CODIGO) != null ? merge.getAttribute(Columns.SER_THA_CODIGO).toString() : "";
                if (!getThaOld.equals(getThaNew)) {
                    msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.tipo.habitacao.alterado.de.arg0.para.arg1", responsavel, getThaOld, getThaNew));
                }
                servidorBean.setTipoHabitacao((merge.getAttribute(Columns.SER_THA_CODIGO) != null) && !"".equals(merge.getAttribute(Columns.SER_THA_CODIGO)) ? TipoHabitacaoHome.findByPrimaryKey((String) merge.getAttribute(Columns.SER_THA_CODIGO)) : null);
            }

            if (merge.getAtributos().containsKey(Columns.SER_TEL)) {
                final String telOld = servidorBean.getSerTel() != null ? servidorBean.getSerTel().toString() : "";
                final String telNew = merge.getAttribute(Columns.SER_TEL) != null ? merge.getAttribute(Columns.SER_TEL).toString() : "";
                if (!telOld.equals(telNew)) {
                    msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.telefone.alterado.de.arg0.para.arg1", responsavel, telOld, telNew));
                }
                servidorBean.setSerTel((String) merge.getAttribute(Columns.SER_TEL));
            }
            if (merge.getAtributos().containsKey(Columns.SER_CELULAR)) {
                final boolean verificaUnicidadeCelular = ParamSist.paramEquals(CodedValues.TPC_IMPEDE_CELULAR_IGUAL_ENTRE_SER_CPF, CodedValues.TPC_SIM, responsavel);
                if (verificaUnicidadeCelular && !TextHelper.isNull(servidor.getSerCelular())) {
                    // verfica se não existe outro servidor com o mesmo celular
                    final boolean existeCelularCadastrado = existeCelularCadastrado(servidor.getSerCelular().trim(), servidor.getSerCpf(), responsavel);
                    if (existeCelularCadastrado) {
                        throw new ServidorControllerException("mensagem.erro.celular.informado.em.uso.outro.cpf", responsavel);
                    }
                }
                final String celularOld = servidorBean.getSerCelular() != null ? servidorBean.getSerCelular().toString() : "";
                final String celularNew = merge.getAttribute(Columns.SER_CELULAR) != null ? merge.getAttribute(Columns.SER_CELULAR).toString() : "";
                if (!celularOld.equals(celularNew)) {
                    msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.celular.alterado.de.arg0.para.arg1", responsavel, celularOld, celularNew));
                }
                servidorBean.setSerCelular((String) merge.getAttribute(Columns.SER_CELULAR));
            }
            if (merge.getAtributos().containsKey(Columns.SER_UF)) {
                final String ufOld = servidorBean.getSerUf() != null ? servidorBean.getSerUf().toString() : "";
                final String ufNew = merge.getAttribute(Columns.SER_UF) != null ? merge.getAttribute(Columns.SER_UF).toString() : "";
                if (!ufOld.equals(ufNew)) {
                    msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.uf.alterado.de.arg0.para.arg1", responsavel, ufOld, ufNew));
                }
                servidorBean.setSerUf((String) merge.getAttribute(Columns.SER_UF));
            }
            if (merge.getAtributos().containsKey(Columns.SER_EMAIL)) {
                if (CodedValues.TPC_NAO.equals(servidorBean.getSerPermiteAlterarEmail())) {
                    throw new ServidorControllerException("mensagem.erro.email.nao.pode.ser.alterado", responsavel);
                }
                final String emailOld = servidorBean.getSerEmail() != null ? servidorBean.getSerEmail().toString() : "";
                final String emailNew = merge.getAttribute(Columns.SER_EMAIL) != null ? merge.getAttribute(Columns.SER_EMAIL).toString() : "";
                if (!TextHelper.isNull(emailNew)) {
                    // Verifica se não existe outro servidor com mesmo email
                    final boolean existeEmailCadastrado = existeEmailCadastrado(emailNew.trim(), servidor.getSerCpf(), responsavel);
                    if (existeEmailCadastrado) {
                        throw new ServidorControllerException("mensagem.erro.email.informado.em.uso.outro.cpf", responsavel);
                    }
                }
                if (!emailOld.equals(emailNew)) {
                    msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.email.alterado.de.arg0.para.arg1", responsavel, emailOld, emailNew));
                }
                servidorBean.setSerEmail(emailNew);
                // força nova validação do e-mail
                servidorBean.setSerDataValidacaoEmail(null);
            }
            if (merge.getAtributos().containsKey(Columns.SER_CID_NASC)) {
                final String cidNascOld = servidorBean.getSerCidNasc() != null ? servidorBean.getSerCidNasc().toString() : "";
                final String cidNascNew = merge.getAttribute(Columns.SER_CID_NASC) != null ? merge.getAttribute(Columns.SER_CID_NASC).toString() : "";
                if (!cidNascOld.equals(cidNascNew)) {
                    msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.cidade.nascimento.alterada.de.arg0.para.arg1", responsavel, cidNascOld, cidNascNew));
                }
                servidorBean.setSerCidNasc((String) merge.getAttribute(Columns.SER_CID_NASC));
            }
            if (merge.getAtributos().containsKey(Columns.SER_UF_NASC)) {
                final String ufNascOld = servidorBean.getSerUfNasc() != null ? servidorBean.getSerUfNasc().toString() : "";
                final String ufNascNew = merge.getAttribute(Columns.SER_UF_NASC) != null ? merge.getAttribute(Columns.SER_UF_NASC).toString() : "";
                if (!ufNascOld.equals(ufNascNew)) {
                    msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.uf.nascimento.alterado.de.arg0.para.arg1", responsavel, ufNascOld, ufNascNew));
                }
                servidorBean.setSerUfNasc((String) merge.getAttribute(Columns.SER_UF_NASC));
            }
            if (merge.getAtributos().containsKey(Columns.SER_DEFICIENTE_VISUAL)) {
                final String serDeficienteVisual = (String) merge.getAttribute(Columns.SER_DEFICIENTE_VISUAL);
                final String deficienteVisualOld = servidorBean.getSerDeficienteVisual() != null ? servidorBean.getSerDeficienteVisual().toString() : "";
                final String deficienteVisualNew = !TextHelper.isNull(serDeficienteVisual) ? serDeficienteVisual.substring(0, 1) : "";
                if (!deficienteVisualOld.equals(deficienteVisualNew)) {
                    msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.deficiente.visual.alterado.de.arg0.para.arg1", responsavel, deficienteVisualOld, deficienteVisualNew));
                }
                servidorBean.setSerDeficienteVisual(!TextHelper.isNull(serDeficienteVisual) ? serDeficienteVisual.substring(0, 1) : null);
            }
            if (merge.getAtributos().containsKey(Columns.SER_ACESSA_HOST_A_HOST)) {
                final String serAcessaHostaHost = (String) merge.getAttribute(Columns.SER_ACESSA_HOST_A_HOST);
                final String serAcessaHostaHostOld = servidorBean.getSerAcessaHostAHost() != null ? servidorBean.getSerAcessaHostAHost().toString() : "";
                final String serAcessaHostaHostlNew = !TextHelper.isNull(serAcessaHostaHost) ? serAcessaHostaHost.substring(0, 1) : "";
                if (!serAcessaHostaHostOld.equals(serAcessaHostaHostlNew)) {
                    msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.acesso.host.a.host.alterado.de.arg0.para.arg1", responsavel, serAcessaHostaHostOld, serAcessaHostaHostlNew));
                }
                servidorBean.setSerAcessaHostAHost(!TextHelper.isNull(serAcessaHostaHost) ? serAcessaHostaHost.substring(0, 1) : null);
            }
            // Atualiza o usuário modificador do registro servidor
            if (merge.getAtributos().containsKey(Columns.SER_USU_CODIGO)) {
                servidorBean.setUsuario(UsuarioHome.findByPrimaryKey((String) merge.getAttribute(Columns.SER_USU_CODIGO)));
            }
            // Atualiza a data de modificação
            if (merge.getAtributos().containsKey(Columns.SER_DATA_ALTERACAO)) {
                servidorBean.setSerDataAlteracao((Timestamp) merge.getAttribute(Columns.SER_DATA_ALTERACAO));
            }

            if (merge.getAtributos().containsKey(Columns.SER_DATA_VALIDACAO_EMAIL)) {
                final Timestamp dataValidacaoEmail = (Timestamp) merge.getAttribute(Columns.SER_DATA_VALIDACAO_EMAIL);
                servidorBean.setSerDataValidacaoEmail((Timestamp) merge.getAttribute(Columns.SER_DATA_VALIDACAO_EMAIL));
                msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.data.validacao.email.alterada.para.arg1", responsavel, DateHelper.format(dataValidacaoEmail, LocaleHelper.getDateTimePattern())));
            }

            if (merge.getAtributos().containsKey(Columns.SER_DATA_IDENTIFICACAO_PESSOAL)) {
                final Timestamp dataIdntPessoal = (Timestamp) merge.getAttribute(Columns.SER_DATA_IDENTIFICACAO_PESSOAL);
                servidorBean.setSerDataIdentificacaoPessoal(dataIdntPessoal);
                msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.data.identificao.pessoal.alterada.para.arg1", responsavel, DateHelper.format(dataIdntPessoal, LocaleHelper.getDateTimePattern())));
            }

            if (merge.getAtributos().containsKey(Columns.SER_PERMITE_ALTERAR_EMAIL)) {
                servidorBean.setSerPermiteAlterarEmail((Boolean) merge.getAttribute(Columns.SER_PERMITE_ALTERAR_EMAIL) ? CodedValues.TPC_SIM : CodedValues.TPC_NAO);
            }

            AbstractEntityHome.update(servidorBean);

            final boolean desabilitarEnvioCadastroEmailMobileSer = servidor.getMobile() && ParamSist.getBoolParamSist(CodedValues.TPC_DESABILITAR_ENVIO_CADASTRO_EMAIL_MOBILE_SER, responsavel);
            final boolean habilitaModuloRecuperarSenhaSer = ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_RECUPERAR_SENHA_SER, CodedValues.TPC_SIM, responsavel);

            // Caso o módulo de recuperação de senha do servidor esteja habilitado e o email do servidor tenha sido alterado, enviar email para o servidor
            if (enviaEmail && !desabilitarEnvioCadastroEmailMobileSer && habilitaModuloRecuperarSenhaSer && merge.getAtributos().containsKey(Columns.SER_EMAIL) && !TextHelper.isNull(merge.getAttribute(Columns.SER_EMAIL)) && !CodedValues.FUN_AUTO_CADASTRO_SENHA_SERVIDOR.equals(responsavel.getFunCodigo())) {
                try {
                    EnviaEmailHelper.enviarEmailCadastroSenhaSer((String) merge.getAttribute(Columns.SER_EMAIL), responsavel);
                } catch (final ViewHelperException e) {
                    LOG.error("Erro ao enviar e-mail na alteração de email de " + ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel).toLowerCase() + " no módulo de recuperação de senha.", e);
                }
            }

            // Cria ocorrência de alteração dos dados cadastrais
            if (msgOcs.length() > 0) {
                criaOcorrenciaSER(servidorBean.getSerCodigo(), tocCodigo, ApplicationResourcesHelper.getMessage("mensagem.informacao.dados.cadastrais.foram.alterados", responsavel, msgOcs.toString()), null, responsavel);
            }

            if (cpfModificado) {
                bloqueiaUsuarioCsaComCPFServidor(servidorBean.getSerCpf(), responsavel);
            }
            final StringBuilder corpoEmailSer = new StringBuilder();
            // Envia e-mail de notificação à todas CSA/COR que possuem contratos ativos com este servidor
            if (responsavel.isCseOrg() && (CodedValues.FUN_EDT_SERVIDOR.equals(responsavel.getFunCodigo()) || CodedValues.FUN_VALIDAR_SERVIDOR.equals(responsavel.getFunCodigo())) && ParamSist.paramEquals(CodedValues.TPC_ENVIA_EMAIL_NOTIFICACAO_EDT_SERVIDOR, CodedValues.TPC_SIM, responsavel) && (((merge.getAtributos().size() == 1) && !merge.getAtributos().containsKey(Columns.SER_DATA_ALTERACAO)) || (merge.getAtributos().size() > 1))) {
                try {
                    if (enviaEmail) {
                        EnviaEmailHelper.enviarEmailCsasAlteracaoSer(servidorCache, servidorBean.getSerCpf(), null, null, msgOcs.toString(), responsavel);
                    }
                    corpoEmailSer.append(msgOcs);
                } catch (final Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }

            log.write();
            if (enviaEmail) {
                return "";
            } else {
                return corpoEmailSer.toString();
            }
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final FindException | UpdateException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Bloqueia usuarios de consignataria ou correspondente que tenham mesmo CPF de um servidor.
     * @param serCpf CPF do servidor.
     * @param responsavel Responsavel pela operacao.
     * @throws ServidorControllerException Excecao padrao.
     */
    private void bloqueiaUsuarioCsaComCPFServidor(String serCpf, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            if (!TextHelper.isNull(serCpf)) {
                usuarioController.bloqueiaUsuarioCsaComCPFServidor(serCpf, responsavel);
            }
        } catch (final UsuarioControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /*****************************************************************************/

    /************************ REGISTRO SERVIDOR **********************************/

    @Override
    public RegistroServidorTO findRegistroServidor(String rseCodigo, boolean retornaMargem, AcessoSistema responsavel) throws ServidorControllerException {
        return findRegistroServidor(new RegistroServidorTO(rseCodigo), retornaMargem, responsavel);
    }

    @Override
    public RegistroServidorTO findRegistroServidor(RegistroServidorTO registroServidor, AcessoSistema responsavel) throws ServidorControllerException {
        return findRegistroServidor(registroServidor, false, responsavel);
    }

    @Override
    public RegistroServidorTO findRegistroServidor(RegistroServidorTO registroServidor, boolean retornaMargem, AcessoSistema responsavel) throws ServidorControllerException {
        return RegistroServidorDtoAssembler.createDto(findRegistroServidorBean(registroServidor), retornaMargem);
    }

    @Override
    public RegistroServidorTO findRegistroServidor(String rseCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        return findRegistroServidor(new RegistroServidorTO(rseCodigo), true, responsavel);
    }

    private RegistroServidor findRegistroServidorBean(RegistroServidorTO registroServidor) throws ServidorControllerException {
        RegistroServidor rseBean = null;

        if (registroServidor.getRseCodigo() != null) {
            try {
                rseBean = RegistroServidorHome.findByPrimaryKey(registroServidor.getRseCodigo());
            } catch (final FindException ex) {
                throw new ServidorControllerException("mensagem.erro.nenhum.registro.servidor.encontrado", (AcessoSistema) null);
            }
        } else if ((registroServidor.getOrgCodigo() != null) && (registroServidor.getRseMatricula() != null)) {
            try {
                rseBean = RegistroServidorHome.findByMatriculaOrgao(registroServidor.getRseMatricula(), registroServidor.getOrgCodigo());
            } catch (final FindException ex) {
                throw new ServidorControllerException("mensagem.erro.nenhum.registro.servidor.encontrado", (AcessoSistema) null);
            }
        } else {
            throw new ServidorControllerException("mensagem.erro.nenhum.registro.servidor.encontrado", (AcessoSistema) null);
        }

        return rseBean;
    }

    @Override
    public List<RegistroServidorTO> findRegistroServidorBySerCodigo(String serCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final List<RegistroServidor> rseEntities = RegistroServidorHome.findBySerCodigo(serCodigo);
            if ((rseEntities != null) && !rseEntities.isEmpty()) {
                final List<RegistroServidorTO> rseList = new ArrayList<>(rseEntities.size());
                for (final RegistroServidor rseEntity : rseEntities) {
                    rseList.add(RegistroServidorDtoAssembler.createDto(rseEntity, false));
                }
                return rseList;
            }
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
        return null;
    }

    @Override
    public void updateRegistroServidorSemHistoricoMargem(RegistroServidorTO registroServidor, AcessoSistema responsavel) throws ServidorControllerException {
        updateRegistroServidor(registroServidor, null, false, false, false, false, false, true, responsavel);
    }

    @Override
    public void updateRegistroServidorSemHistoricoMargem(RegistroServidorTO registroServidor, boolean importacaoTransferidos, AcessoSistema responsavel) throws ServidorControllerException {
        updateRegistroServidor(registroServidor, null, false, false, false, false, importacaoTransferidos, true, responsavel);
    }

    @Override
    public void updateRegistroServidor(RegistroServidorTO registroServidor, boolean validaMargem, boolean calculaMargem, boolean transferenciaMargem, AcessoSistema responsavel) throws ServidorControllerException {
        updateRegistroServidor(registroServidor, null, validaMargem, calculaMargem, transferenciaMargem, true, responsavel);
    }

    @Override
    public void updateRegistroServidor(RegistroServidorTO registroServidor, List<MargemTO> margens, boolean validaMargem, boolean calculaMargem, boolean transferenciaMargem, AcessoSistema responsavel) throws ServidorControllerException {
        updateRegistroServidor(registroServidor, margens, validaMargem, calculaMargem, transferenciaMargem, true, responsavel);
    }

    @Override
    public void updateRegistroServidor(RegistroServidorTO registroServidor, List<MargemTO> margens, boolean validaMargem, boolean calculaMargem, boolean transferenciaMargem, boolean geraHistoricoMargem, AcessoSistema responsavel) throws ServidorControllerException {
        updateRegistroServidor(registroServidor, margens, validaMargem, calculaMargem, transferenciaMargem, geraHistoricoMargem, false, true, responsavel);
    }

    private String updateRegistroServidor(RegistroServidorTO registroServidor, List<MargemTO> margens, boolean validaMargem, boolean calculaMargem, boolean transferenciaMargem, boolean geraHistoricoMargem, boolean importacaoTransferidos, boolean enviaEmail, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            boolean reativaServidorManualmente = false;
            boolean excluiServidorManualmente = false;
            boolean bloqueiaServidorManualmente = false;
            boolean desbloqueiaServidorManualmente = false;
            boolean falecimentoAutomaticoServidor = false;
            boolean falecimentoManualServidor = false;

            final RegistroServidor rseBean = findRegistroServidorBean(registroServidor);
            final String rseCodigo = rseBean.getRseCodigo();
            final LogDelegate log = new LogDelegate(responsavel, Log.REGISTRO_SERVIDOR, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setRegistroServidor(rseCodigo);

            final AcessoSistema loggerMargem = new AcessoSistema(responsavel.getUsuCodigo(), responsavel.getIpUsuario(), responsavel.getPortaLogicaUsuario());
            loggerMargem.setFunCodigo(CodedValues.FUN_ALT_MARGEM_CONSIGNAVEL);
            final LogDelegate logMargem = new LogDelegate(loggerMargem, Log.REGISTRO_SERVIDOR, Log.UPDATE, Log.LOG_INFORMACAO);
            logMargem.setRegistroServidor(rseCodigo);

            // Guarda os valores originais das margens restantes para gravação do histórico
            final BigDecimal margemRest1Antes = rseBean.getRseMargemRest();
            final BigDecimal margemRest2Antes = rseBean.getRseMargemRest2();
            final BigDecimal margemRest3Antes = rseBean.getRseMargemRest3();

            // Guarda os valores das margens restantes para gravação do histórico
            final HashMap<Short, BigDecimal> margemRestAntes = new HashMap<>();
            final HashMap<Short, BigDecimal> margemRestDepois = new HashMap<>();

            final StringBuilder msgOrs = new StringBuilder();
            final StringBuilder margemAlterada = new StringBuilder();

            final boolean exigeDetalhesExclusao = ParamSist.paramEquals(CodedValues.TPC_EXIGE_DETALHES_EXCL_BLOQ_SER, CodedValues.TPC_SIM, responsavel);

            /* Compara a versão do cache com a passada por parâmetro */
            final RegistroServidorTO rseCache = RegistroServidorDtoAssembler.createDto(rseBean, true);
            final CustomTransferObject merge = log.getUpdatedFields(registroServidor.getAtributos(), rseCache.getAtributos());

            if ((merge.getAttribute(Columns.TMO_CODIGO) != null) && TextHelper.isNull(merge.getAttribute(Columns.TMO_CODIGO).toString().trim())) {
                merge.remove(Columns.TMO_CODIGO);
            }
            if ((merge.getAttribute(Columns.OCA_OBS) != null) && TextHelper.isNull(merge.getAttribute(Columns.OCA_OBS).toString().trim())) {
                merge.remove(Columns.OCA_OBS);
            }

            if (merge.getAtributos().containsKey(Columns.RSE_MATRICULA) || merge.getAtributos().containsKey(Columns.RSE_ORG_CODIGO)) {
                // Verifica se não existe outro registro com a mesma matrícula no mesmo órgão
                final RegistroServidorTO teste = new RegistroServidorTO();

                if (merge.getAtributos().containsKey(Columns.RSE_MATRICULA)) {
                    teste.setRseMatricula((String) merge.getAttribute(Columns.RSE_MATRICULA));
                } else {
                    teste.setRseMatricula(rseBean.getRseMatricula());
                }

                if (merge.getAtributos().containsKey(Columns.RSE_ORG_CODIGO)) {
                    teste.setOrgCodigo((String) merge.getAttribute(Columns.RSE_ORG_CODIGO));
                } else {
                    teste.setOrgCodigo(rseBean.getOrgao().getOrgCodigo());
                }

                boolean existe = false;
                try {
                    findRegistroServidorBean(teste);
                    existe = true;
                } catch (final ServidorControllerException ex) {
                }
                if (existe) {
                    throw new ServidorControllerException("mensagem.erro.nao.possivel.alterar.registro.deste.servidor.pois.existe.outro.com.mesma.matricula.orgao.cadastrado.sistema", responsavel);
                }

                if (merge.getAtributos().containsKey(Columns.RSE_MATRICULA)) {
                    msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.matricula.alterada.de.arg0.para.arg1", responsavel, rseBean.getRseMatricula(), (String) merge.getAttribute(Columns.RSE_MATRICULA)));
                    rseBean.setRseMatricula((String) merge.getAttribute(Columns.RSE_MATRICULA));
                }

                if (merge.getAtributos().containsKey(Columns.RSE_ORG_CODIGO)) {
                    final String orgCodigoOld = rseBean.getOrgao().getOrgCodigo();
                    final String orgCodigoNew = (String) merge.getAttribute(Columns.RSE_ORG_CODIGO);

                    if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_EDITAR_ORGAO_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                        // Verificar se o novo órgão possui os mesmos convênios que o órgão atual, inclusive se possui código de verba válido (não nulo, não vazio).
                        final ObtemTotalConsignacaoSemConvenioTransfQuery validaTransferenciaQuery = new ObtemTotalConsignacaoSemConvenioTransfQuery();
                        validaTransferenciaQuery.rseCodigo = rseCodigo;
                        validaTransferenciaQuery.orgCodigo = orgCodigoNew;
                        if (validaTransferenciaQuery.executarContador() > 0) {
                            // Caso não exista algum convênio ou exista, porém sem código de verba, interromper a operação e alertar o usuário
                            // a impossibilidade de alteração em virtude de falta de convênio no órgão novo para as consignações que o servidor possui.
                            throw new ServidorControllerException("mensagem.erro.nao.possivel.alterar.orgao.servidor.convenios.faltantes", responsavel);
                        }

                        // Caso o registro servidor possua um bloqueio de verba ou uma consignação arquivada, interromper a operação
                        // e alertar o usuário a impossibilidade de alteração com a causa do problema.
                        final List<ParamConvenioRegistroSer> bloqueiosCnv = ParamConvenioRegistroServidorHome.findByRseCodigo(rseCodigo);
                        if ((bloqueiosCnv != null) && !bloqueiosCnv.isEmpty()) {
                            throw new ServidorControllerException("mensagem.erro.nao.possivel.alterar.orgao.servidor.possui.bloqueios", responsavel);
                        }

                        final List<HtAutDesconto> consignacoesArquivadas = AutDescontoHome.findArquivadasByRseCodigo(rseCodigo);
                        if ((consignacoesArquivadas != null) && !consignacoesArquivadas.isEmpty()) {
                            throw new ServidorControllerException("mensagem.erro.nao.possivel.alterar.orgao.servidor.possui.consignacoes.arquivadas", responsavel);
                        }

                        // Alterar o órgão do registro servidor
                        final Orgao orgaoOld = OrgaoHome.findByPrimaryKey(orgCodigoOld);
                        final Orgao orgaoNew = OrgaoHome.findByPrimaryKey(orgCodigoNew);
                        rseBean.setOrgao(orgaoNew);

                        // Registra na ocorrência geral de alteração
                        final String mensagemAlteracaoOrgao = ApplicationResourcesHelper.getMessage("mensagem.informacao.orgao.alterado.de.arg0.para.arg1", responsavel, orgaoOld.getOrgIdentificador(), orgaoNew.getOrgIdentificador());
                        msgOrs.append(mensagemAlteracaoOrgao);

                        // Registra no log de auditoria a alteração do órgão
                        log.setOrgao(orgCodigoNew);

                        // Registrar ocorrência específica de alteração de órgão
                        criaOcorrenciaRSE(rseCodigo, CodedValues.TOC_RSE_ALTERACAO_ORGAO_SERVIDOR, mensagemAlteracaoOrgao, registroServidor.getTipoMotivo(), responsavel);

                        // Atualizar os convênios das consignações do servidor para o correspondente no novo órgão e criar ocorrência nas consignações
                        transferirConsignacaoController.transfereAdeNovoOrgao(rseCodigo, orgCodigoNew, registroServidor.getTipoMotivo(), mensagemAlteracaoOrgao, responsavel);
                    } else {
                        throw new ServidorControllerException("mensagem.erro.nao.possivel.alterar.orgao.servidor.sistema.nao.permite", responsavel);
                    }
                }
            }

            if (merge.getAtributos().containsKey(Columns.RSE_TIPO)) {
                final String tipoOld = rseBean.getRseTipo() != null ? rseBean.getRseTipo() : "";
                final String tipoNew = merge.getAttribute(Columns.RSE_TIPO) != null ? (String) merge.getAttribute(Columns.RSE_TIPO) : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.tipo.alterado.de.arg0.para.arg1", responsavel, tipoOld, tipoNew));
                rseBean.setRseTipo((String) merge.getAttribute(Columns.RSE_TIPO));
            }
            if (merge.getAtributos().containsKey(Columns.SRS_CODIGO)) {
                final StatusRegistroServidor srs = StatusRegistroServidorHome.findByPrimaryKey((String) merge.getAttribute(Columns.SRS_CODIGO));
                final StatusRegistroServidor srsAntigo = StatusRegistroServidorHome.findByPrimaryKey(rseBean.getStatusRegistroServidor().getSrsCodigo());
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.situacao.alterado.de.arg0.para.arg1", responsavel, srsAntigo.getSrsDescricao(), srs.getSrsDescricao()));
                rseBean.setStatusRegistroServidor(srs);
                if (!TextHelper.isNull(merge.getAttribute(Columns.SRS_CODIGO))) {
                    log.setStatusRseCodigo((String) merge.getAttribute(Columns.SRS_CODIGO));
                }
                if (!importacaoTransferidos) {
                    // Se o status foi alterado, e o flag "importacaoTransferidos" não está setado
                    // significa que é uma alteração manual, feita na página de manutenção de servidor
                    if (CodedValues.SRS_INATIVOS.contains(srs.getSrsCodigo())) {
                        // Coloca true no flag "excluiServidorManualmente" para que seja criada a ocorrência de exclusão do servidor.
                        if (CodedValues.SRS_EXCLUIDO.equals(srs.getSrsCodigo())) {
                            excluiServidorManualmente = true;
                        } else if (CodedValues.FUN_IMP_SER_FALECIDO.equals(responsavel.getFunCodigo())) {
                            falecimentoAutomaticoServidor = true;
                        } else {
                            falecimentoManualServidor = true;
                        }
                        // Coloca null na data carga para permitir transferência de servidor
                        rseBean.setRseDataCarga(null);
                    } else if (CodedValues.SRS_ATIVO.equals(srs.getSrsCodigo()) && CodedValues.SRS_INATIVOS.contains(srsAntigo.getSrsCodigo())) {
                        // Se está indo para ativo, mas estava excluído
                        reativaServidorManualmente = true;
                    } else if (CodedValues.SRS_ATIVO.equals(srs.getSrsCodigo()) && CodedValues.SRS_BLOQUEADOS.contains(srsAntigo.getSrsCodigo())) {
                        // Se está indo para ativo, mas estava bloqueado
                        desbloqueiaServidorManualmente = true;
                    } else if (CodedValues.SRS_BLOQUEADO.equals(srs.getSrsCodigo()) && CodedValues.SRS_ATIVOS.contains(srsAntigo.getSrsCodigo())) {
                        // Se está indo para bloqueado, mas estava ativo
                        bloqueiaServidorManualmente = true;
                    }
                }

                // Se está mudando o status do servidor e está bloqueado por segurança, só o suporte pode fazer a operação
                // Não permite alteração do status do servidor caso bloqueado por segurança pela variação de margem
                if ((CodedValues.SRS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA.equals(srsAntigo.getSrsCodigo()) && !responsavel.isSup()) || CodedValues.SRS_BLOQUEADO_AUTOMATICAMENTE_VARIACAO_MARGEM.equals(srsAntigo.getSrsCodigo())) {
                    throw new ServidorControllerException("mensagem.erro.nao.possivel.desbloquear.servidor.arg0.pois.foi.bloqueado.por.seguranca", responsavel, rseBean.getRseMatricula());
                }

                if (exigeDetalhesExclusao && (excluiServidorManualmente || bloqueiaServidorManualmente) && !CodedValues.FUN_VALIDAR_SERVIDOR.equals(responsavel.getFunCodigo())) {
                    // No caso de servidor/funcionário Desligado (Excluído), o sistema deve obrigar o preenchimento da data de saída e a data do último salário
                    if (CodedValues.SRS_EXCLUIDO.equals(srs.getSrsCodigo()) || CodedValues.SRS_BLOQUEADO.equals(srs.getSrsCodigo())) {
                        if (!merge.getAtributos().containsKey(Columns.RSE_DATA_SAIDA) && (rseBean.getRseDataSaida() == null)) {
                            throw new ServidorControllerException("mensagem.erro.atualizar.data.saida", responsavel);
                        } else if (!merge.getAtributos().containsKey(Columns.RSE_DATA_ULT_SALARIO) && (rseBean.getRseDataUltSalario() == null)) {
                            throw new ServidorControllerException("mensagem.erro.atualizar.data.ultimo.salario", responsavel);
                        }
                    }

                    // No caso de servidor/funcionário Desligado (Excluído), o sistema deve obrigar o preenchimento da data de saída, data de retorno e a data do último salário
                    if (CodedValues.SRS_BLOQUEADO.equals(srs.getSrsCodigo()) && (!merge.getAtributos().containsKey(Columns.RSE_DATA_RETORNO) && (rseBean.getRseDataRetorno() == null))) {
                        throw new ServidorControllerException("mensagem.erro.atualizar.data.retorno", responsavel);
                    }
                }
            }

            //DESENV-16129 - Rio de Janeiro - Mostrar Motivo de Bloqueio do Servidor
            if ((merge.getAtributos().containsKey(Columns.SRS_CODIGO) && CodedValues.SRS_BLOQUEADO.equals(merge.getAttribute(Columns.SRS_CODIGO))) ||  CodedValues.SRS_BLOQUEADO.equals(rseBean.getStatusRegistroServidor().getSrsCodigo())) {
                if (merge.getAtributos().containsKey(Columns.RSE_MOTIVO_BLOQUEIO)) {
                    rseBean.setRseMotivoBloqueio((String) merge.getAttribute(Columns.RSE_MOTIVO_BLOQUEIO));
                } else {
                    rseBean.setRseMotivoBloqueio(null);
                }
            } else if (((merge.getAtributos().containsKey(Columns.SRS_CODIGO) && CodedValues.SRS_ATIVO.equals(merge.getAttribute(Columns.SRS_CODIGO))) || CodedValues.SRS_ATIVO.equals(rseBean.getStatusRegistroServidor().getSrsCodigo())) && !merge.getAtributos().containsKey(Columns.RSE_MOTIVO_BLOQUEIO)) {
                rseBean.setRseMotivoBloqueio(null);
            }

            if (merge.getAtributos().containsKey(Columns.VRS_CODIGO)) {
                final VinculoRegistroServidor vrs = merge.getAttribute(Columns.VRS_CODIGO) != null ? VinculoRegistroServidorHome.findByPrimaryKey((String) merge.getAttribute(Columns.VRS_CODIGO)) : null;
                final String vinculoOld = rseBean.getVinculoRegistroServidor() != null ? rseBean.getVinculoRegistroServidor().getVrsCodigo() : "";
                final String vinculoNew = (vrs != null) && (vrs.getVrsCodigo() != null) ? vrs.getVrsCodigo() : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.vinculo.alterado.de.arg0.para.arg1", responsavel, vinculoOld, vinculoNew));
                rseBean.setVinculoRegistroServidor(vrs);
                if (!TextHelper.isNull(merge.getAttribute(Columns.VRS_CODIGO))) {
                    log.setVincRseCodigo((String) merge.getAttribute(Columns.VRS_CODIGO));
                }
            }
            if (merge.getAtributos().containsKey(Columns.RSE_CLT)) {
                final String cltOld = rseBean.getRseClt() != null ? rseBean.getRseClt() : "";
                final String cltNew = merge.getAttribute(Columns.RSE_CLT) != null ? (String) merge.getAttribute(Columns.RSE_CLT) : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.sindicalizado.alterado.de.arg0.para.arg1", responsavel, cltOld, cltNew));
                rseBean.setRseClt((String) merge.getAttribute(Columns.RSE_CLT));
            }
            if (merge.getAtributos().containsKey(Columns.RSE_PRAZO)) {
                final String prazoOld = rseBean.getRsePrazo() != null ? rseBean.getRsePrazo().toString() : "";
                final String prazoNew = merge.getAttribute(Columns.RSE_PRAZO) != null ? merge.getAttribute(Columns.RSE_PRAZO).toString() : "";
                msgOrs.append("<BR>").append(ApplicationResourcesHelper.getMessage("mensagem.informacao.prazo.alterado.de.arg0.para.arg1", responsavel, prazoOld, prazoNew));
                rseBean.setRsePrazo((Integer) merge.getAttribute(Columns.RSE_PRAZO));
            }
            if (merge.getAtributos().containsKey(Columns.RSE_DATA_ADMISSAO)) {
                final String dataOld = rseBean.getRseDataAdmissao() != null ? DateHelper.toDateString(rseBean.getRseDataAdmissao()) : "";
                final String dataNew = merge.getAttribute(Columns.RSE_DATA_ADMISSAO) != null ? DateHelper.toDateString((Timestamp) merge.getAttribute(Columns.RSE_DATA_ADMISSAO)) : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.data.admissao.alterada.de.arg0.para.arg1", responsavel, dataOld, dataNew));
                rseBean.setRseDataAdmissao((Timestamp) merge.getAttribute(Columns.RSE_DATA_ADMISSAO));
            }
            if (merge.getAtributos().containsKey(Columns.CRS_CODIGO)) {
                final CargoRegistroServidor crs = merge.getAttribute(Columns.CRS_CODIGO) != null ? CargoRegistroServidorHome.findByPrimaryKey((String) merge.getAttribute(Columns.CRS_CODIGO)) : null;
                final String cargoOld = rseBean.getCargoRegistroServidor() != null ? rseBean.getCargoRegistroServidor().getCrsCodigo() : "";
                final String cargoNew = (crs != null) && (crs.getCrsCodigo() != null) ? crs.getCrsCodigo() : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.cargo.alterado.de.arg0.para.arg1", responsavel, cargoOld, cargoNew));
                rseBean.setCargoRegistroServidor(crs);
                if (!TextHelper.isNull(merge.getAttribute(Columns.CRS_CODIGO))) {
                    log.setCargoRseCodigo((String) merge.getAttribute(Columns.CRS_CODIGO));
                }
            }
            if (merge.getAtributos().containsKey(Columns.PRS_CODIGO)) {
                final PadraoRegistroServidor prs = merge.getAttribute(Columns.PRS_CODIGO) != null ? PadraoRegistroServidorHome.findByPrimaryKey((String) merge.getAttribute(Columns.PRS_CODIGO)) : null;
                final String padraoOld = rseBean.getPadraoRegistroServidor() != null ? rseBean.getPadraoRegistroServidor().getPrsCodigo() : "";
                final String padraoNew = (prs != null) && (prs.getPrsCodigo() != null) ? prs.getPrsCodigo() : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.padrao.alterado.de.arg0.para.arg1", responsavel, padraoOld, padraoNew));
                rseBean.setPadraoRegistroServidor(prs);
            }
            if (merge.getAtributos().containsKey(Columns.SBO_CODIGO)) {
                final SubOrgao sbo = merge.getAttribute(Columns.SBO_CODIGO) != null ? SubOrgaoHome.findByPrimaryKey((String) merge.getAttribute(Columns.SBO_CODIGO)) : null;
                final String subOrgaoOld = rseBean.getSubOrgao() != null ? rseBean.getSubOrgao().getSboCodigo() : "";
                final String subOrgaoNew = (sbo != null) && (sbo.getSboCodigo() != null) ? sbo.getSboCodigo() : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.suborgao.alterado.de.arg0.para.arg1", responsavel, subOrgaoOld, subOrgaoNew));
                rseBean.setSubOrgao(sbo);
            }
            if (merge.getAtributos().containsKey(Columns.UNI_CODIGO)) {
                final Unidade uni = merge.getAttribute(Columns.UNI_CODIGO) != null ? UnidadeHome.findByPrimaryKey((String) merge.getAttribute(Columns.UNI_CODIGO)) : null;
                final String unidadeOld = rseBean.getUnidade() != null ? rseBean.getUnidade().getUniCodigo() : "";
                final String unidadeNew = (uni != null) && (uni.getUniCodigo() != null) ? uni.getUniCodigo() : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.unidade.alterada.de.arg0.para.arg1", responsavel, unidadeOld, unidadeNew));
                rseBean.setUnidade(uni);
            }

            final Map<String, Object> margensNew = new HashMap<>();
            final Map<String, Object> margensOld = new HashMap<>();
            if (merge.getAtributos().containsKey(Columns.RSE_MARGEM)) {
                final BigDecimal margem = rseBean.getRseMargem() != null ? rseBean.getRseMargem() : new BigDecimal("0.00");
                final BigDecimal margemUsada = rseBean.getRseMargemUsada() != null ? rseBean.getRseMargemUsada() : new BigDecimal("0.00");
                final BigDecimal nova_margem = (BigDecimal) merge.getAttribute(Columns.RSE_MARGEM);

                margensNew.put(Columns.RSE_MARGEM, nova_margem);
                margensOld.put(Columns.RSE_MARGEM, margem);

                if (validaMargem) {
                    if (margem.compareTo(nova_margem) == -1) {
                        // Se a margem está sendo aumentada
                        throw new ServidorControllerException("mensagem.erro.nova.margem.arg0.nao.pode.ser.maior.arg0.atual", responsavel, MargemHelper.getInstance().getMarDescricao(CodedValues.INCIDE_MARGEM_SIM, responsavel));
                    } else if (nova_margem.compareTo(margemUsada) == -1) {
                        // Se a nova margem é menor do que a margem usada
                        throw new ServidorControllerException("mensagem.erro.nova.margem.arg0.nao.pode.ser.menor.arg0.usada", responsavel, MargemHelper.getInstance().getMarDescricao(CodedValues.INCIDE_MARGEM_SIM, responsavel));
                    }
                }
                margemAlterada.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0.alterada.de.arg1.para.arg2", responsavel, MargemHelper.getInstance().getMarDescricao(CodedValues.INCIDE_MARGEM_SIM, responsavel).toUpperCase(), rseBean.getRseMargem().toString(), nova_margem.toString()));

                // Atribui a nova margem
                rseBean.setRseMargem(nova_margem);

                // Se calculaMargem é igual a true, a margem restante do servidor será o valor
                // da margem informada subtraída do valor da margem usada atual
                if (calculaMargem) {
                    rseBean.setRseMargemRest(((BigDecimal) merge.getAttribute(Columns.RSE_MARGEM)).subtract(margemUsada));

                } else if (transferenciaMargem) { // Na transferencia, a margem restante foi informada no TO registroServidor
                    rseBean.setRseMargemRest((BigDecimal) merge.getAttribute(Columns.RSE_MARGEM_REST));

                } else {
                    rseBean.setRseMargemRest(nova_margem);
                }
            }

            if (merge.getAtributos().containsKey(Columns.RSE_MARGEM_2)) {
                final BigDecimal margem2 = rseBean.getRseMargem2() != null ? rseBean.getRseMargem2() : new BigDecimal("0.00");
                final BigDecimal margemUsada2 = rseBean.getRseMargemUsada2() != null ? rseBean.getRseMargemUsada2() : new BigDecimal("0.00");
                final BigDecimal nova_margem_2 = (BigDecimal) merge.getAttribute(Columns.RSE_MARGEM_2);

                margensNew.put(Columns.RSE_MARGEM_2, nova_margem_2);
                margensOld.put(Columns.RSE_MARGEM_2, margem2);

                if (validaMargem) {
                    if (margem2.compareTo(nova_margem_2) == -1) {
                        // Se a margem está sendo aumentada
                        throw new ServidorControllerException("mensagem.erro.nova.margem.arg0.nao.pode.ser.maior.arg0.atual", responsavel, MargemHelper.getInstance().getMarDescricao(CodedValues.INCIDE_MARGEM_SIM_2, responsavel));
                    } else if (nova_margem_2.compareTo(margemUsada2) == -1) {
                        // Se a nova margem é menor do que a margem usada
                        throw new ServidorControllerException("mensagem.erro.nova.margem.arg0.nao.pode.ser.menor.arg0.usada", responsavel, MargemHelper.getInstance().getMarDescricao(CodedValues.INCIDE_MARGEM_SIM_2, responsavel));
                    }
                }
                margemAlterada.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0.alterada.de.arg1.para.arg2", responsavel, MargemHelper.getInstance().getMarDescricao(CodedValues.INCIDE_MARGEM_SIM_2, responsavel).toUpperCase(), rseBean.getRseMargem2() != null ? rseBean.getRseMargem2().toString() : "0.00", nova_margem_2.toString()));

                // Atribui a nova margem
                rseBean.setRseMargem2(nova_margem_2);

                // Se calculaMargem é igual a true, a margem restante do servidor será o valor
                // da margem informada subtraída do valor da margem usada atual
                if (calculaMargem) {
                    rseBean.setRseMargemRest2(((BigDecimal) merge.getAttribute(Columns.RSE_MARGEM_2)).subtract(margemUsada2));

                } else if (transferenciaMargem) { // Na transferencia, a margem restante foi informada no TO registroServidor
                    rseBean.setRseMargemRest2((BigDecimal) merge.getAttribute(Columns.RSE_MARGEM_REST_2));

                } else {
                    rseBean.setRseMargemRest2(nova_margem_2);
                }
            }

            if (merge.getAtributos().containsKey(Columns.RSE_MARGEM_3)) {
                final BigDecimal margem3 = rseBean.getRseMargem3() != null ? rseBean.getRseMargem3() : new BigDecimal("0.00");
                final BigDecimal margemUsada3 = rseBean.getRseMargemUsada3() != null ? rseBean.getRseMargemUsada3() : new BigDecimal("0.00");
                final BigDecimal nova_margem_3 = (BigDecimal) merge.getAttribute(Columns.RSE_MARGEM_3);

                margensNew.put(Columns.RSE_MARGEM_3, nova_margem_3);
                margensOld.put(Columns.RSE_MARGEM_3, margem3);

                if (validaMargem) {
                    if (margem3.compareTo(nova_margem_3) == -1) {
                        // Se a margem está sendo aumentada
                        throw new ServidorControllerException("mensagem.erro.nova.margem.arg0.nao.pode.ser.maior.arg0.atual", responsavel, MargemHelper.getInstance().getMarDescricao(CodedValues.INCIDE_MARGEM_SIM_3, responsavel));
                    } else if (nova_margem_3.compareTo(margemUsada3) == -1) {
                        // Se a nova margem é menor do que a margem usada
                        throw new ServidorControllerException("mensagem.erro.nova.margem.arg0.nao.pode.ser.menor.arg0.usada", responsavel, MargemHelper.getInstance().getMarDescricao(CodedValues.INCIDE_MARGEM_SIM_3, responsavel));
                    }
                }
                margemAlterada.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0.alterada.de.arg1.para.arg2", responsavel, MargemHelper.getInstance().getMarDescricao(CodedValues.INCIDE_MARGEM_SIM_3, responsavel).toUpperCase(), rseBean.getRseMargem3() != null ? rseBean.getRseMargem3().toString() : "0.00", nova_margem_3.toString()));

                // Atribui a nova margem
                rseBean.setRseMargem3(nova_margem_3);

                // Se calculaMargem é igual a true, a margem restante do servidor será o valor
                // da margem informada subtraída do valor da margem usada atual
                if (calculaMargem) {
                    rseBean.setRseMargemRest3(((BigDecimal) merge.getAttribute(Columns.RSE_MARGEM_3)).subtract(margemUsada3));

                } else if (transferenciaMargem) { // Na transferencia, a margem restante foi informada no TO registroServidor
                    rseBean.setRseMargemRest3((BigDecimal) merge.getAttribute(Columns.RSE_MARGEM_REST_3));

                } else {
                    rseBean.setRseMargemRest3(nova_margem_3);
                }
            }

            logMargem.getUpdatedFields(margensNew, margensOld);

            if ((margens != null) && (margens.size() > 0)) {
                for (final MargemTO margemTO : margens) {
                    // Não se aplica se a margem é 1,2 ou 3
                    if (margemTO.getMarCodigo().equals((short) 1) || margemTO.getMarCodigo().equals((short) 2) || margemTO.getMarCodigo().equals((short) 3)) {
                        continue;
                    }

                    final MargemRegistroServidorId mrsPK = new MargemRegistroServidorId(margemTO.getMarCodigo(), rseCodigo);
                    final MargemRegistroServidor mrsBean = MargemRegistroServidorHome.findByPrimaryKeyForUpdate(mrsPK);

                    final BigDecimal margemAntigaFolha = mrsBean.getMrsMargem();
                    final BigDecimal margemAntigaUsada = mrsBean.getMrsMargemUsada() != null ? mrsBean.getMrsMargemUsada() : new BigDecimal("0.00");
                    final BigDecimal margemNovaFolha = margemTO.getMrsMargem() != null ? margemTO.getMrsMargem() : new BigDecimal("0.00");

                    // Se nada mudou, pula para a próxima margem
                    if (margemAntigaFolha.compareTo(margemNovaFolha) == 0) {
                        continue;
                    }

                    if (validaMargem) {
                        if (margemAntigaFolha.compareTo(margemNovaFolha) == -1) {
                            // Se a margem está sendo aumentada
                            throw new ServidorControllerException("mensagem.erro.nova.margem.arg0.nao.pode.ser.maior.arg0.atual", responsavel, MargemHelper.getInstance().getMarDescricao(margemTO.getMarCodigo(), responsavel));
                        } else if (margemNovaFolha.compareTo(margemAntigaUsada) == -1) {
                            // Se a nova margem é menor do que a margem usada
                            throw new ServidorControllerException("mensagem.erro.nova.margem.arg0.nao.pode.ser.menor.arg0.usada", responsavel, MargemHelper.getInstance().getMarDescricao(margemTO.getMarCodigo(), responsavel));
                        }
                    }

                    // Adiciona a alteração ao log
                    logMargem.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.atributo.margem.arg0.folha.alterado.arg1.para.arg2", responsavel, margemTO.getMarCodigo().toString(), logMargem.formatObject(margemAntigaFolha), logMargem.formatObject(margemNovaFolha)));

                    // Se calculaMargem é igual a true, a margem restante do servidor será o valor
                    // da margem informada subtraída do valor da margem usada atual
                    margemRestAntes.put(margemTO.getMarCodigo(), mrsBean.getMrsMargemRest());
                    BigDecimal margemRest = null;
                    if (calculaMargem) {
                        margemRest = margemNovaFolha.subtract(margemAntigaUsada);
                        // Falta tratar transferência
                    } else {
                        margemRest = margemNovaFolha;
                    }
                    margemRestDepois.put(margemTO.getMarCodigo(), margemRest);
                    margemAlterada.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0.alterada.de.arg1.para.arg2", responsavel, MargemHelper.getInstance().getMarDescricao(margemTO.getMarCodigo(), responsavel).toUpperCase(), margemAntigaFolha.toString(), margemNovaFolha.toString()));

                    mrsBean.setMrsMargem(margemNovaFolha);
                    mrsBean.setMrsMargemRest(margemRest);
                    AbstractEntityHome.update(mrsBean);
                }
            }
            // Calcula a pontuação do servidor, pois a margem pode ter sido alterada
            try {
                pontuacaoServidorController.calcularPontuacao(rseCodigo, responsavel);
            } catch (final ZetraException e) {
                // Captura a exceção para não gerar efeitos colaterais indesejados
                LOG.error(e.getMessage(), e);
            }

            // Grava dados sobre a conta salário do servidor
            if (merge.getAtributos().containsKey(Columns.RSE_BCO_CODIGO)) {
                final Banco bct = merge.getAttribute(Columns.RSE_BCO_CODIGO) != null ? BancoHome.findByPrimaryKey((Short) merge.getAttribute(Columns.RSE_BCO_CODIGO)) : null;
                final String bcoCodOld = rseBean.getBanco() != null ? rseBean.getBanco().getBcoCodigo().toString() : "";
                final String bcoCodNew = (bct != null) && (bct.getBcoCodigo() != null) ? bct.getBcoCodigo().toString() : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.codigo.banco.alterado.de.arg0.para.arg1", responsavel, bcoCodOld, bcoCodNew));
                rseBean.setBanco(bct);
            }
            if (merge.getAtributos().containsKey(Columns.RSE_MATRICULA_INST)) {
                final String matriculaInstOld = rseBean.getRseMatriculaInst() != null ? rseBean.getRseMatriculaInst() : "";
                final String matriculaInstNew = merge.getAttribute(Columns.RSE_MATRICULA_INST) != null ? (String) merge.getAttribute(Columns.RSE_MATRICULA_INST) : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.matricula.inst.alterada.de.arg0.para.arg1", responsavel, matriculaInstOld, matriculaInstNew));
                rseBean.setRseMatriculaInst((String) merge.getAttribute(Columns.RSE_MATRICULA_INST));
            }
            if (merge.getAtributos().containsKey(Columns.RSE_BASE_CALCULO)) {
                final String baseCalculoOld = rseBean.getRseBaseCalculo() != null ? rseBean.getRseBaseCalculo().toString() : "";
                final String baseCalculoNew = merge.getAttribute(Columns.RSE_BASE_CALCULO) != null ? merge.getAttribute(Columns.RSE_BASE_CALCULO).toString() : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.base.calculo.alterada.de.arg0.para.arg1", responsavel, baseCalculoOld, baseCalculoNew));
                rseBean.setRseBaseCalculo((BigDecimal) merge.getAttribute(Columns.RSE_BASE_CALCULO));
            }
            if (merge.getAtributos().containsKey(Columns.RSE_DATA_CTC)) {
                final String dataOld = rseBean.getRseDataCtc() != null ? DateHelper.toDateString(rseBean.getRseDataCtc()) : "";
                final String dataNew = merge.getAttribute(Columns.RSE_DATA_CTC) != null ? DateHelper.toDateString((java.util.Date) merge.getAttribute(Columns.RSE_DATA_CTC)) : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.data.ctc.alterada.de.arg0.para.arg1", responsavel, dataOld, dataNew));
                rseBean.setRseDataCtc((java.util.Date) merge.getAttribute(Columns.RSE_DATA_CTC));
            }
            if (merge.getAtributos().containsKey(Columns.RSE_BANCO_SAL)) {
                final String bancoSalOld = rseBean.getRseBancoSal() != null ? rseBean.getRseBancoSal() : "";
                final String bancoSalNew = merge.getAttribute(Columns.RSE_BANCO_SAL) != null ? (String) merge.getAttribute(Columns.RSE_BANCO_SAL) : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.banco.alterado.de.arg0.para.arg1", responsavel, bancoSalOld, bancoSalNew));
                rseBean.setRseBancoSal((String) merge.getAttribute(Columns.RSE_BANCO_SAL));
            }
            if (merge.getAtributos().containsKey(Columns.RSE_AGENCIA_SAL)) {
                final String agenciaSalOld = rseBean.getRseAgenciaSal() != null ? rseBean.getRseAgenciaSal() : "";
                final String agenciaSalONew = merge.getAttribute(Columns.RSE_AGENCIA_SAL) != null ? (String) merge.getAttribute(Columns.RSE_AGENCIA_SAL) : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.agencia.alterada.de.arg0.para.arg1", responsavel, agenciaSalOld, agenciaSalONew));
                rseBean.setRseAgenciaSal((String) merge.getAttribute(Columns.RSE_AGENCIA_SAL));
            }
            if (merge.getAtributos().containsKey(Columns.RSE_AGENCIA_DV_SAL)) {
                final String agenciaDvSalOld = rseBean.getRseAgenciaDvSal() != null ? rseBean.getRseAgenciaDvSal() : "";
                final String agenciaDvSalNew = merge.getAttribute(Columns.RSE_AGENCIA_DV_SAL) != null ? (String) merge.getAttribute(Columns.RSE_AGENCIA_DV_SAL) : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.agencia.dv.alterado.de.arg0.para.arg1", responsavel, agenciaDvSalOld, agenciaDvSalNew));
                rseBean.setRseAgenciaDvSal((String) merge.getAttribute(Columns.RSE_AGENCIA_DV_SAL));
            }
            if (merge.getAtributos().containsKey(Columns.RSE_CONTA_SAL)) {
                final String contaSalOld = rseBean.getRseContaSal() != null ? rseBean.getRseContaSal() : "";
                final String contaSalNew = merge.getAttribute(Columns.RSE_CONTA_SAL) != null ? (String) merge.getAttribute(Columns.RSE_CONTA_SAL) : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.conta.alterada.de.arg0.para.arg1", responsavel, contaSalOld, contaSalNew));
                rseBean.setRseContaSal((String) merge.getAttribute(Columns.RSE_CONTA_SAL));
            }
            if (merge.getAtributos().containsKey(Columns.RSE_CONTA_DV_SAL)) {
                final String contaDvSalOld = rseBean.getRseContaDvSal() != null ? rseBean.getRseContaDvSal() : "";
                final String contaDvSalNew = merge.getAttribute(Columns.RSE_CONTA_DV_SAL) != null ? (String) merge.getAttribute(Columns.RSE_CONTA_DV_SAL) : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.conta.dv.alterado.de.arg0.para.arg1", responsavel, contaDvSalOld, contaDvSalNew));
                rseBean.setRseContaDvSal((String) merge.getAttribute(Columns.RSE_CONTA_DV_SAL));
            }

            // Grava informações sobre o salário do servidor
            if (merge.getAtributos().containsKey(Columns.RSE_SALARIO)) {
                final String salarioOld = rseBean.getRseSalario() != null ? rseBean.getRseSalario().toString() : "";
                final String salarioNew = merge.getAttribute(Columns.RSE_SALARIO) != null ? merge.getAttribute(Columns.RSE_SALARIO).toString() : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.salario.alterado.de.arg0.para.arg1", responsavel, salarioOld, salarioNew));
                rseBean.setRseSalario((BigDecimal) merge.getAttribute(Columns.RSE_SALARIO));
            }
            if (merge.getAtributos().containsKey(Columns.RSE_PROVENTOS)) {
                final String proventosOld = rseBean.getRseProventos() != null ? rseBean.getRseProventos().toString() : "";
                final String proventosNew = merge.getAttribute(Columns.RSE_PROVENTOS) != null ? merge.getAttribute(Columns.RSE_PROVENTOS).toString() : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.proventos.alterados.de.arg0.para.arg1", responsavel, proventosOld, proventosNew));
                rseBean.setRseProventos((BigDecimal) merge.getAttribute(Columns.RSE_PROVENTOS));
            }
            if (merge.getAtributos().containsKey(Columns.RSE_DESCONTOS_COMP)) {
                final String descontosCompOld = rseBean.getRseDescontosComp() != null ? rseBean.getRseDescontosComp().toString() : "";
                final String descontosCompNew = merge.getAttribute(Columns.RSE_DESCONTOS_COMP) != null ? merge.getAttribute(Columns.RSE_DESCONTOS_COMP).toString() : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.descontos.compulsorios.alterados.de.arg0.para.arg1", responsavel, descontosCompOld, descontosCompNew));
                rseBean.setRseDescontosComp((BigDecimal) merge.getAttribute(Columns.RSE_DESCONTOS_COMP));
            }
            if (merge.getAtributos().containsKey(Columns.RSE_DESCONTOS_FACU)) {
                final String descontosFacuOld = rseBean.getRseDescontosFacu() != null ? rseBean.getRseDescontosFacu().toString() : "";
                final String descontosFacuNew = merge.getAttribute(Columns.RSE_DESCONTOS_FACU) != null ? merge.getAttribute(Columns.RSE_DESCONTOS_FACU).toString() : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.descontos.facultativos.alterados.de.arg0.para.arg1", responsavel, descontosFacuOld, descontosFacuNew));
                rseBean.setRseDescontosFacu((BigDecimal) merge.getAttribute(Columns.RSE_DESCONTOS_FACU));
            }
            if (merge.getAtributos().containsKey(Columns.RSE_OUTROS_DESCONTOS)) {
                final String outrosOld = rseBean.getRseOutrosDescontos() != null ? rseBean.getRseOutrosDescontos().toString() : "";
                final String outrosNew = merge.getAttribute(Columns.RSE_OUTROS_DESCONTOS) != null ? merge.getAttribute(Columns.RSE_OUTROS_DESCONTOS).toString() : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.outros.descontos.alterados.de.arg0.para.arg1", responsavel, outrosOld, outrosNew));
                rseBean.setRseOutrosDescontos((BigDecimal) merge.getAttribute(Columns.RSE_OUTROS_DESCONTOS));
            }
            if (merge.getAtributos().containsKey(Columns.RSE_ASSOCIADO)) {
                final String associadoOld = rseBean.getRseAssociado() != null ? rseBean.getRseAssociado() : "";
                final String associadoNew = merge.getAttribute(Columns.RSE_ASSOCIADO) != null ? (String) merge.getAttribute(Columns.RSE_ASSOCIADO) : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.associado.alterado.de.arg0.para.arg1", responsavel, associadoOld, associadoNew));
                rseBean.setRseAssociado((String) merge.getAttribute(Columns.RSE_ASSOCIADO));
            }
            if (merge.getAtributos().containsKey(Columns.RSE_DATA_CARGA)) {
                final String dataOld = rseBean.getRseDataCarga() != null ? DateHelper.toDateString(rseBean.getRseDataCarga()) : "";
                final String dataNew = merge.getAttribute(Columns.RSE_DATA_CARGA) != null ? DateHelper.toDateString((Timestamp) merge.getAttribute(Columns.RSE_DATA_CARGA)) : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.data.carga.alterada.de.arg0.para.arg1", responsavel, dataOld, dataNew));
                rseBean.setRseDataCarga((Timestamp) merge.getAttribute(Columns.RSE_DATA_CARGA));
            }
            // Grava observações sobre o servidor
            if (merge.getAtributos().containsKey(Columns.RSE_OBS)) {
                final String obsOld = rseBean.getRseObs() != null ? rseBean.getRseObs() : "";
                final String obsNew = merge.getAttribute(Columns.RSE_OBS) != null ? (String) merge.getAttribute(Columns.RSE_OBS) : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.observacao.alterada.de.arg0.para.arg1", responsavel, obsOld, obsNew));
                rseBean.setRseObs((String) merge.getAttribute(Columns.RSE_OBS));
            }

            // Grava quantidade default de contratos
            if (merge.getAtributos().containsKey(Columns.RSE_PARAM_QTD_ADE_DEFAULT)) {
                final String qtdeOld = rseBean.getRseParamQtdAdeDefault() != null ? rseBean.getRseParamQtdAdeDefault().toString() : "";
                final String qtdeNew = merge.getAttribute(Columns.RSE_PARAM_QTD_ADE_DEFAULT) != null ? merge.getAttribute(Columns.RSE_PARAM_QTD_ADE_DEFAULT).toString() : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.qtde.contratos.alterada.de.arg0.para.arg1", responsavel, qtdeOld, qtdeNew));
                rseBean.setRseParamQtdAdeDefault(merge.getAttribute(Columns.RSE_PARAM_QTD_ADE_DEFAULT) != null ? Short.valueOf(merge.getAttribute(Columns.RSE_PARAM_QTD_ADE_DEFAULT).toString()) : null);
            }

            // Atualiza o posto do registro servidor
            if (merge.getAtributos().containsKey(Columns.RSE_POS_CODIGO)) {
                final PostoRegistroServidor pos = merge.getAttribute(Columns.RSE_POS_CODIGO) != null ? PostoRegistroServidorHome.findByPrimaryKey((String) merge.getAttribute(Columns.RSE_POS_CODIGO)) : null;
                final String postoOld = rseBean.getPostoRegistroServidor() != null ? rseBean.getPostoRegistroServidor().getPosCodigo() : "";
                final String postoNew = (pos != null) && (pos.getPosCodigo() != null) ? pos.getPosCodigo() : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.posto.alterado.de.arg0.para.arg1", responsavel, postoOld, postoNew));
                rseBean.setPostoRegistroServidor(pos);
            }

            // Atualiza Tipo registro servidor
            if (merge.getAtributos().containsKey(Columns.RSE_TRS_CODIGO)) {
                final TipoRegistroServidor tps = merge.getAttribute(Columns.RSE_TRS_CODIGO) != null ? TipoRegistroServidorHome.findByPrimaryKey((String) merge.getAttribute(Columns.RSE_TRS_CODIGO)) : null;
                final String tipoRegistroOld = rseBean.getTipoRegistroServidor() != null ? rseBean.getTipoRegistroServidor().getTrsCodigo() : "";
                final String tipoRegistroNew = (tps != null) && (tps.getTrsCodigo() != null) ? tps.getTrsCodigo() : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.tipo.servidor.alterado.de.arg0.para.arg1", responsavel, tipoRegistroOld, tipoRegistroNew));
                rseBean.setTipoRegistroServidor(tps);
            }

            // Atualiza informação se servidor estabilzado
            boolean alterouEstabilidade = false;
            if (merge.getAtributos().containsKey(Columns.RSE_ESTABILIZADO)) {
                String estabilizadoOld = rseBean.getRseEstabilizado() != null ? rseBean.getRseEstabilizado() : "";
                String estabilizadoNew = merge.getAttribute(Columns.RSE_ESTABILIZADO) != null ? (String) merge.getAttribute(Columns.RSE_ESTABILIZADO) : "";

                if (!TextHelper.isNull(estabilizadoOld)) {
                    estabilizadoOld = CodedValues.TPC_SIM.equals(estabilizadoOld) ? ApplicationResourcesHelper.getMessage("rotulo.sim", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.nao", responsavel);
                }
                if (!TextHelper.isNull(estabilizadoNew)) {
                    estabilizadoNew = CodedValues.TPC_SIM.equals(estabilizadoNew) ? ApplicationResourcesHelper.getMessage("rotulo.sim", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.nao", responsavel);
                }

                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.estabilidade.alterada.de.arg0.para.arg1", responsavel, estabilizadoOld, estabilizadoNew));
                rseBean.setRseEstabilizado((String) merge.getAttribute(Columns.RSE_ESTABILIZADO));

                alterouEstabilidade = true;
            }

            // Atualiza informação da data de fim do engajamento se houver mudança ou se estabilidade tiver sido alterada
            if (alterouEstabilidade || merge.getAtributos().containsKey(Columns.RSE_DATA_FIM_ENGAJAMENTO)) {
                final String dataOld = rseBean.getRseDataFimEngajamento() != null ? DateHelper.toDateString(rseBean.getRseDataFimEngajamento()) : "";
                String dataNew = merge.getAttribute(Columns.RSE_DATA_FIM_ENGAJAMENTO) != null ? DateHelper.toDateString((Timestamp) merge.getAttribute(Columns.RSE_DATA_FIM_ENGAJAMENTO)) : "";

                // Se o registro servidor é estabilizado, a data fim de engajamento será sempre nula
                if (!TextHelper.isNull(rseBean.getRseEstabilizado()) && CodedValues.TPC_SIM.equalsIgnoreCase(rseBean.getRseEstabilizado())) {
                    dataNew = "";
                }

                if (!dataOld.equals(dataNew)) {
                    msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.data.fim.engajamento.alterada.de.arg0.para.arg1", responsavel, dataOld, dataNew));

                    // Se o registro servidor é estabilizado, a data fim de engajamento será sempre nula
                    if (!TextHelper.isNull(rseBean.getRseEstabilizado()) && CodedValues.TPC_SIM.equalsIgnoreCase(rseBean.getRseEstabilizado())) {
                        rseBean.setRseDataFimEngajamento(null);
                    } else {
                        rseBean.setRseDataFimEngajamento((Timestamp) merge.getAttribute(Columns.RSE_DATA_FIM_ENGAJAMENTO));
                    }
                }
            }

            // Atualiza informação da data de limite de permanência se houver mudança ou se estabilidade tiver sido alterada
            if (alterouEstabilidade || merge.getAtributos().containsKey(Columns.RSE_DATA_LIMITE_PERMANENCIA)) {
                final String dataOld = rseBean.getRseDataLimitePermanencia() != null ? DateHelper.toDateString(rseBean.getRseDataLimitePermanencia()) : "";
                String dataNew = merge.getAttribute(Columns.RSE_DATA_LIMITE_PERMANENCIA) != null ? DateHelper.toDateString((Timestamp) merge.getAttribute(Columns.RSE_DATA_LIMITE_PERMANENCIA)) : "";

                // Se o registro servidor é estabilizado, a data limite de permanência será sempre nula
                if (!TextHelper.isNull(rseBean.getRseEstabilizado()) && CodedValues.TPC_SIM.equalsIgnoreCase(rseBean.getRseEstabilizado())) {
                    dataNew = "";
                }

                if (!dataOld.equals(dataNew)) {
                    msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.data.limite.permanencia.alterada.de.arg0.para.arg1", responsavel, dataOld, dataNew));

                    // Se o registro servidor é estabilizado, a data limite de permanência será sempre nula
                    if (!TextHelper.isNull(rseBean.getRseEstabilizado()) && CodedValues.TPC_SIM.equalsIgnoreCase(rseBean.getRseEstabilizado())) {
                        rseBean.setRseDataLimitePermanencia(null);
                    } else {
                        rseBean.setRseDataLimitePermanencia((Timestamp) merge.getAttribute(Columns.RSE_DATA_LIMITE_PERMANENCIA));
                    }
                }
            }

            // Atualiza informação de capacidade civil do servidor
            if (merge.getAtributos().containsKey(Columns.RSE_CAP_CODIGO)) {
                final CapacidadeRegistroSer cap = merge.getAttribute(Columns.RSE_CAP_CODIGO) != null ? CapacidadeRegistroServidorHome.findByPrimaryKey((String) merge.getAttribute(Columns.RSE_CAP_CODIGO)) : null;
                final String capacidadeOld = rseBean.getCapacidadeRegistroSer() != null ? rseBean.getCapacidadeRegistroSer().getCapCodigo() : "";
                final String capacidadeNew = (cap != null) && (cap.getCapCodigo() != null) ? cap.getCapCodigo() : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.capacidade.civil.alterada.de.arg0.para.arg1", responsavel, capacidadeOld, capacidadeNew));
                rseBean.setCapacidadeRegistroSer(cap);
            }

            //DESENV-8327: atualiza margem limite específica do registro servidor
            if (merge.getAtributos().containsKey(Columns.RSE_MAR_CODIGO)) {
                final Margem margem = merge.getAttribute(Columns.RSE_MAR_CODIGO) != null ? MargemHome.findByPrimaryKey((Short) merge.getAttribute(Columns.RSE_MAR_CODIGO)) : null;
                final String margemOld = rseBean.getMargem() != null ? rseBean.getMargem().getMarCodigo().toString() : "";
                final String margemNew = merge.getAttribute(Columns.RSE_MAR_CODIGO) != null ? ((Short) merge.getAttribute(Columns.RSE_MAR_CODIGO)).toString() : "";

                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.margem.limite.alterado.de.arg0.para.arg1", responsavel, margemOld, margemNew));
                rseBean.setMargem(margem);
            }

            // Atualiza informação de código de banco alternativo do servidor
            if (merge.getAtributos().containsKey(Columns.RSE_BANCO_SAL_2)) {
                final String bancoSal2Old = rseBean.getRseBancoSal2() != null ? rseBean.getRseBancoSal2() : "";
                final String bancoSal2New = merge.getAttribute(Columns.RSE_BANCO_SAL_2) != null ? (String) merge.getAttribute(Columns.RSE_BANCO_SAL_2) : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.banco.alternativo.alterado.de.arg0.para.arg1", responsavel, bancoSal2Old, bancoSal2New));
                rseBean.setRseBancoSal2((String) merge.getAttribute(Columns.RSE_BANCO_SAL_2));
            }

            // Atualiza informação de agência alternativa do servidor
            if (merge.getAtributos().containsKey(Columns.RSE_AGENCIA_SAL_2)) {
                final String agenciaSal2Old = rseBean.getRseAgenciaSal2() != null ? rseBean.getRseAgenciaSal2() : "";
                final String agenciaSal2New = merge.getAttribute(Columns.RSE_AGENCIA_SAL_2) != null ? (String) merge.getAttribute(Columns.RSE_AGENCIA_SAL_2) : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.agencia.alternativa.alterada.de.arg0.para.arg1", responsavel, agenciaSal2Old, agenciaSal2New));
                rseBean.setRseAgenciaSal2((String) merge.getAttribute(Columns.RSE_AGENCIA_SAL_2));
            }

            // Atualiza informação de dígito verificador da agência alternativa do servidor
            if (merge.getAtributos().containsKey(Columns.RSE_AGENCIA_DV_SAL_2)) {
                final String agenciaDvSal2Old = rseBean.getRseAgenciaDvSal2() != null ? rseBean.getRseAgenciaDvSal2() : "";
                final String agenciaDvSal2New = merge.getAttribute(Columns.RSE_AGENCIA_DV_SAL_2) != null ? (String) merge.getAttribute(Columns.RSE_AGENCIA_DV_SAL_2) : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.agencia.dv.alternativo.alterado.de.arg0.para.arg1", responsavel, agenciaDvSal2Old, agenciaDvSal2New));
                rseBean.setRseAgenciaDvSal2((String) merge.getAttribute(Columns.RSE_AGENCIA_DV_SAL_2));
            }

            // Atualiza informação de conta alternativa do servidor
            if (merge.getAtributos().containsKey(Columns.RSE_CONTA_SAL_2)) {
                final String contaSal2Old = rseBean.getRseContaSal2() != null ? rseBean.getRseContaSal2() : "";
                final String contaSal2New = merge.getAttribute(Columns.RSE_CONTA_SAL_2) != null ? (String) merge.getAttribute(Columns.RSE_CONTA_SAL_2) : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.conta.alternativa.alterada.de.arg0.para.arg1", responsavel, contaSal2Old, contaSal2New));
                rseBean.setRseContaSal2((String) merge.getAttribute(Columns.RSE_CONTA_SAL_2));
            }

            // Atualiza informação de dígito verificador de conta alternativa do servidor
            if (merge.getAtributos().containsKey(Columns.RSE_CONTA_DV_SAL_2)) {
                final String contaDvSal2Old = rseBean.getRseContaDvSal2() != null ? rseBean.getRseContaDvSal2() : "";
                final String contaDvSal2New = merge.getAttribute(Columns.RSE_CONTA_DV_SAL_2) != null ? (String) merge.getAttribute(Columns.RSE_CONTA_DV_SAL_2) : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.conta.dv.alternativo.alterado.de.arg0.para.arg1", responsavel, contaDvSal2Old, contaDvSal2New));
                rseBean.setRseContaDvSal2((String) merge.getAttribute(Columns.RSE_CONTA_DV_SAL_2));
            }

            // Atualiza auditoria total
            if (merge.getAtributos().containsKey(Columns.RSE_AUDITORIA_TOTAL)) {
                final String auditoriaOld = rseBean.getRseAuditoriaTotal() != null ? rseBean.getRseAuditoriaTotal() : "";
                final String auditoriaNew = merge.getAttribute(Columns.RSE_AUDITORIA_TOTAL) != null ? (String) merge.getAttribute(Columns.RSE_AUDITORIA_TOTAL) : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.auditoria.estendida.alterada.de.arg0.para.arg1", responsavel, auditoriaOld, auditoriaNew));
                rseBean.setRseAuditoriaTotal((String) merge.getAttribute(Columns.RSE_AUDITORIA_TOTAL));
            }

            // Atualiza município lotação
            if (merge.getAtributos().containsKey(Columns.RSE_MUNICIPIO_LOTACAO)) {
                final String municipioLotOld = rseBean.getRseMunicipioLotacao() != null ? rseBean.getRseMunicipioLotacao() : "";
                final String municipioLotNew = merge.getAttribute(Columns.RSE_MUNICIPIO_LOTACAO) != null ? (String) merge.getAttribute(Columns.RSE_MUNICIPIO_LOTACAO) : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.municipio.lotacao.alterado.de.arg0.para.arg1", responsavel, municipioLotOld, municipioLotNew));
                rseBean.setRseMunicipioLotacao((String) merge.getAttribute(Columns.RSE_MUNICIPIO_LOTACAO));
            }

            // Atualiza beneficiário de financiamento de cartão
            if (merge.getAtributos().containsKey(Columns.RSE_BENEFICIARIO_FINAN_DV_CART)) {
                final String beneficiarioOld = rseBean.getRseBeneficiarioFinanDvCart() != null ? rseBean.getRseBeneficiarioFinanDvCart() : "";
                final String beneficiarioNew = merge.getAttribute(Columns.RSE_BENEFICIARIO_FINAN_DV_CART) != null ? (String) merge.getAttribute(Columns.RSE_BENEFICIARIO_FINAN_DV_CART) : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.beneficiario.financiamento.divida.cartao.alterado.de.arg0.para.arg1", responsavel, beneficiarioOld, beneficiarioNew));
                rseBean.setRseBeneficiarioFinanDvCart((String) merge.getAttribute(Columns.RSE_BENEFICIARIO_FINAN_DV_CART));
            }

            // Atualiza informações das praças do servidor
            if (merge.getAtributos().containsKey(Columns.RSE_PRACA)) {
                final String pracaOld = rseBean.getRsePraca() != null ? rseBean.getRsePraca() : "";
                final String pracaNew = merge.getAttribute(Columns.RSE_PRACA) != null ? (String) merge.getAttribute(Columns.RSE_PRACA) : "";
                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.praca.alterado.de.arg0.para.arg1", responsavel, pracaOld, pracaNew));
                rseBean.setRsePraca((String) merge.getAttribute(Columns.RSE_PRACA));
            }

            final String statusCorrente = merge.getAttribute(Columns.SRS_CODIGO) != null ? (String) merge.getAttribute(Columns.SRS_CODIGO) : rseCache.getSrsCodigo();
            final String dataSaidaNew = merge.getAttribute(Columns.RSE_DATA_SAIDA) != null ? DateHelper.toDateString((java.util.Date) merge.getAttribute(Columns.RSE_DATA_SAIDA)) : "";
            // Atualiza data de saída do servidor
            if (merge.getAtributos().containsKey(Columns.RSE_DATA_SAIDA)) {
                final String dataOld = rseBean.getRseDataSaida() != null ? DateHelper.toDateString(rseBean.getRseDataSaida()) : "";

                if (exigeDetalhesExclusao && !importacaoTransferidos && TextHelper.isNull(dataSaidaNew) && (CodedValues.SRS_EXCLUIDO.equals(statusCorrente) || CodedValues.SRS_BLOQUEADO.equals(statusCorrente))) {
                    throw new ServidorControllerException("mensagem.erro.rse.informe.data.saida", responsavel);
                }

                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.data.saida.ser.de.arg0.para.arg1", responsavel, dataOld, dataSaidaNew));
                rseBean.setRseDataSaida((java.util.Date) merge.getAttribute(Columns.RSE_DATA_SAIDA));
            }

            // Atualiza data de pagamento do último salário do servidor
            final String dataUltSalNew = merge.getAttribute(Columns.RSE_DATA_ULT_SALARIO) != null ? DateHelper.toDateString((java.util.Date) merge.getAttribute(Columns.RSE_DATA_ULT_SALARIO)) : "";
            if (merge.getAtributos().containsKey(Columns.RSE_DATA_ULT_SALARIO)) {
                final String dataOld = rseBean.getRseDataUltSalario() != null ? DateHelper.toDateString(rseBean.getRseDataUltSalario()) : "";

                if (exigeDetalhesExclusao && !importacaoTransferidos && TextHelper.isNull(dataUltSalNew) && (CodedValues.SRS_EXCLUIDO.equals(statusCorrente) || CodedValues.SRS_BLOQUEADO.equals(statusCorrente))) {
                    throw new ServidorControllerException("mensagem.erro.rse.informe.data.ult.salario", responsavel);
                }

                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.data.ult.salario.ser.de.arg0.para.arg1", responsavel, dataOld, dataUltSalNew));
                rseBean.setRseDataUltSalario((java.util.Date) merge.getAttribute(Columns.RSE_DATA_ULT_SALARIO));
            }

            // Atualiza data de pagamento do último salário do servidor
            final String dataRetornoNew = merge.getAttribute(Columns.RSE_DATA_RETORNO) != null ? DateHelper.toDateString((java.util.Date) merge.getAttribute(Columns.RSE_DATA_RETORNO)) : "";
            if (merge.getAtributos().containsKey(Columns.RSE_DATA_RETORNO)) {
                final String dataOld = rseBean.getRseDataRetorno() != null ? DateHelper.toDateString(rseBean.getRseDataRetorno()) : "";

                if (exigeDetalhesExclusao && !importacaoTransferidos && CodedValues.SRS_BLOQUEADO.equals(statusCorrente)) {
                    if (TextHelper.isNull(dataRetornoNew)) {
                        throw new ServidorControllerException("mensagem.erro.rse.informe.data.retorno", responsavel);
                    } else {
                        try {
                            final java.util.Date dataRetParse = DateHelper.parse(dataRetornoNew, LocaleHelper.getDatePattern());

                            if (DateHelper.dayDiff(dataRetParse) >= 0) {
                                throw new ServidorControllerException("mensagem.erro.rse.data.retorno.menor.atual", responsavel);
                            }
                        } catch (final ParseException e) {
                            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel);
                        }
                    }
                }

                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.data.retorno.ser.de.arg0.para.arg1", responsavel, dataOld, dataRetornoNew));
                rseBean.setRseDataRetorno((java.util.Date) merge.getAttribute(Columns.RSE_DATA_RETORNO));
            }

            if (merge.getAtributos().containsKey(Columns.RSE_PEDIDO_DEMISSAO)) {
                String pedidoDemissaoOld = rseBean.getRsePedidoDemissao() != null ? rseBean.getRsePedidoDemissao() : "";
                String pedidoDemissaoNew = merge.getAttribute(Columns.RSE_PEDIDO_DEMISSAO) != null ? (String) merge.getAttribute(Columns.RSE_PEDIDO_DEMISSAO) : "";
                if (TextHelper.isNull(pedidoDemissaoNew) && exigeDetalhesExclusao && !importacaoTransferidos && CodedValues.SRS_EXCLUIDO.equals(statusCorrente)) {
                    throw new ServidorControllerException("mensagem.erro.rse.informe.servidor.demitiuse", responsavel);
                }

                if (!TextHelper.isNull(pedidoDemissaoOld)) {
                    pedidoDemissaoOld = CodedValues.TPC_SIM.equals(pedidoDemissaoOld) ? ApplicationResourcesHelper.getMessage("rotulo.sim", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.nao", responsavel);
                }
                if (!TextHelper.isNull(pedidoDemissaoNew)) {
                    pedidoDemissaoNew = CodedValues.TPC_SIM.equals(pedidoDemissaoNew) ? ApplicationResourcesHelper.getMessage("rotulo.sim", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.nao", responsavel);
                }

                msgOrs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.rse.pedido.demissao.de.arg0.para.arg1", responsavel, pedidoDemissaoOld, pedidoDemissaoNew));
                rseBean.setRsePedidoDemissao((String) merge.getAttribute(Columns.RSE_PEDIDO_DEMISSAO));
            }

            // Atualiza o usuário modificador do registro servidor
            if (merge.getAtributos().containsKey(Columns.RSE_USU_CODIGO)) {
                final Usuario usu = UsuarioHome.findByPrimaryKey((String) merge.getAttribute(Columns.RSE_USU_CODIGO));
                rseBean.setUsuario(usu);
            }

            // Atualiza a data de modificação
            if (merge.getAtributos().containsKey(Columns.RSE_DATA_ALTERACAO)) {
                rseBean.setRseDataAlteracao((Timestamp) merge.getAttribute(Columns.RSE_DATA_ALTERACAO));
            }

            if (merge.getAtributos().containsKey(Columns.RSE_MOTIVO_FALTA_MARGEM)) {
                rseBean.setRseMotivoFaltaMargem((String) merge.getAttribute(Columns.RSE_MOTIVO_FALTA_MARGEM));
            }

            final String tmoCodigo = registroServidor.getTipoMotivo();
            final boolean tmoExigeObservacao = FuncaoExigeMotivo.getInstance().motivosExigeObs(tmoCodigo, responsavel);

            // Verifica se o tipo motivo operação e a observação foram preenchidas
            if (responsavel.isCseSupOrg() && (TextHelper.isNull(registroServidor.getTipoMotivo()) || TextHelper.isNull(registroServidor.getOrsObs()))) {
                if (!TextHelper.isNull(msgOrs) || !TextHelper.isNull(margemAlterada)) {
                    Funcao funcao = null;
                    if (responsavel.getFunCodigo() != null) {
                        funcao = usuarioController.findFuncao(responsavel.getFunCodigo(), responsavel);
                    } else {
                        funcao = usuarioController.findFuncao(CodedValues.FUN_EDT_SERVIDOR, responsavel);
                    }
                    final String exigeTmo = funcao.getFunExigeTmo();
                    if (!TextHelper.isNull(exigeTmo) && CodedValues.TPC_SIM.equals(exigeTmo)) {
                        if (TextHelper.isNull(registroServidor.getTipoMotivo())) {
                            throw new ServidorControllerException("mensagem.informe.tipo.motivo.operacao", responsavel);
                        } else if ((tmoCodigo != null) && tmoExigeObservacao && TextHelper.isNull(registroServidor.getOrsObs())) {
                            throw new ServidorControllerException("mensagem.informe.oca.observacao", responsavel);
                        }
                    }
                }

                if (exigeDetalhesExclusao && (excluiServidorManualmente || bloqueiaServidorManualmente) && merge.getAtributos().containsKey(Columns.SRS_CODIGO) && (CodedValues.SRS_BLOQUEADO.equals(merge.getAtributos().get(Columns.SRS_CODIGO).toString()) || CodedValues.SRS_EXCLUIDO.equals(merge.getAtributos().get(Columns.SRS_CODIGO).toString()))) {
                    if (TextHelper.isNull(registroServidor.getTipoMotivo())) {
                        throw new ServidorControllerException("mensagem.informe.tipo.motivo.operacao", responsavel);
                    } else if ((tmoCodigo != null) && tmoExigeObservacao && TextHelper.isNull(registroServidor.getOrsObs())) {
                        throw new ServidorControllerException("mensagem.informe.oca.observacao", responsavel);
                    }
                }

                if (!TextHelper.isNull(margemAlterada)) {
                    Funcao funcao = usuarioController.findFuncao(CodedValues.FUN_ALT_MARGEM_CONSIGNAVEL, responsavel);
                    String exigeTmo = funcao.getFunExigeTmo();

                    if (!TextHelper.isNull(exigeTmo) && CodedValues.TPC_SIM.equals(exigeTmo)) {
                        if (TextHelper.isNull(registroServidor.getTipoMotivo())) {
                            throw new ServidorControllerException("mensagem.informe.tipo.motivo.operacao", responsavel);
                        } else if ((tmoCodigo != null) && tmoExigeObservacao && TextHelper.isNull(registroServidor.getOrsObs())) {
                            throw new ServidorControllerException("mensagem.informe.oca.observacao", responsavel);
                        }

                    }

                    funcao = usuarioController.findFuncao(CodedValues.FUN_ALT_MARGEM_CONSIGNAVEL_MENOR, responsavel);
                    exigeTmo = funcao.getFunExigeTmo();

                    if (!TextHelper.isNull(exigeTmo) && CodedValues.TPC_SIM.equals(exigeTmo)) {
                        if (TextHelper.isNull(registroServidor.getTipoMotivo())) {
                            throw new ServidorControllerException("mensagem.informe.tipo.motivo.operacao", responsavel);
                        } else if ((tmoCodigo != null) && tmoExigeObservacao && TextHelper.isNull(registroServidor.getOrsObs())) {
                            throw new ServidorControllerException("mensagem.informe.oca.observacao", responsavel);
                        }
                    }
                }
            }

            // Valida se o motivo da operação selecionado é obrigatório("S") e também se o campo observação foi preenchido(orsObs)
            if ((tmoCodigo != null) && tmoExigeObservacao && registroServidor.getOrsObs().isEmpty()) {
                throw new ServidorControllerException("mensagem.erro.obrigatoriedade.observacao.motivo.operacao", responsavel);
            }

            AbstractEntityHome.update(rseBean);

            // Cria o beneficiário se o registro servidor foi ativado
            if (reativaServidorManualmente && ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_BENEFICIOS_SAUDE, CodedValues.TPC_SIM, responsavel)) {
                final CustomTransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.RSE_CODIGO, rseCodigo);
                criterio.setAttribute(Columns.TIB_CODIGO, TipoBeneficiarioEnum.TITULAR.tibCodigo);
                final List<TransferObject> beneficiarios = beneficiarioController.listarBeneficiarios(criterio, responsavel);

                if ((beneficiarios != null) && beneficiarios.isEmpty()) {
                    final TipoBeneficiario tipoBeneficiario = new TipoBeneficiario();
                    tipoBeneficiario.setTibCodigo(TipoBeneficiarioEnum.TITULAR.tibCodigo);

                    final StatusBeneficiario statusBeneficiario = new StatusBeneficiario();
                    statusBeneficiario.setSbeCodigo(StatusBeneficiarioEnum.ATIVO.sbeCodigo);

                    final CustomTransferObject servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);

                    final String serCodigo = (String) servidor.getAttribute(Columns.SER_CODIGO);
                    final String serNome = (String) servidor.getAttribute(Columns.SER_NOME);
                    final String serCpf = (String) servidor.getAttribute(Columns.SER_CPF);
                    final String serNroIdt = (String) servidor.getAttribute(Columns.SER_NRO_IDT);

                    String serSexo = null;
                    final Object sexoObj = servidor.getAttribute(Columns.SER_SEXO);
                    if (!TextHelper.isNull(sexoObj)) {
                        if (sexoObj instanceof String) {
                            serSexo = (String) sexoObj;
                        } else if (sexoObj instanceof Character) {
                            serSexo = String.valueOf(sexoObj);
                        }
                    }

                    final String serTel = (String) servidor.getAttribute(Columns.SER_TEL);
                    final Date serDataNasc = (Date) servidor.getAttribute(Columns.SER_DATA_NASC);

                    final Object estCivilObj = servidor.getAttribute(Columns.SER_EST_CIVIL);
                    String serEstCivil = null;

                    if (!TextHelper.isNull(estCivilObj)) {
                        if (estCivilObj instanceof String) {
                            serEstCivil = (String) estCivilObj;
                        } else if (estCivilObj instanceof Character) {
                            serEstCivil = String.valueOf(estCivilObj);
                        }
                    }

                    final String serCelular = (String) servidor.getAttribute(Columns.SER_CELULAR);
                    final String serNomeMae = (String) servidor.getAttribute(Columns.SER_NOME_MAE);

                    final Servidor ser = new Servidor();
                    ser.setSerCodigo(serCodigo);

                    beneficiarioController.create(ser, tipoBeneficiario, null, (short) 0, serNome, serCpf, serNroIdt, serSexo, serTel, serCelular, serNomeMae, null, serDataNasc, serEstCivil, null, null, null, null, statusBeneficiario, null, null, null, responsavel);
                }
            }

            String obs = "";
            if (!TextHelper.isNull(registroServidor.getTipoMotivo())) {
                final TipoMotivoOperacao tipoMotivoOperacao = TipoMotivoOperacaoHome.findByPrimaryKey(registroServidor.getTipoMotivo());
                obs += "<br> " + ApplicationResourcesHelper.getMessage("rotulo.motivo.arg0", responsavel, tipoMotivoOperacao.getTmoDescricao());
            }
            if (!TextHelper.isNull(registroServidor.getOrsObs())) {
                obs += "<br> " + ApplicationResourcesHelper.getMessage("rotulo.observacao.arg0", responsavel, registroServidor.getOrsObs());
            }

            // Grava ocorrência de alteração
            if (transferenciaMargem) {
                final String mensagem = responsavel.isSer() ? "<BR>" + ApplicationResourcesHelper.getMessage("mensagem.transferencia.margem.ocorrencia", responsavel).toUpperCase() : "";
                criaOcorrenciaRSE(rseCodigo, CodedValues.TOC_RSE_TRANSFERENCIA_ENTRE_MARGENS, ApplicationResourcesHelper.getMessage("mensagem.informacao.transferencia.valores.entre.margens", responsavel, margemAlterada.toString() + mensagem + obs), registroServidor.getTipoMotivo(), responsavel);
            } else {
                if (msgOrs.length() > 0) {
                    criaOcorrenciaRSE(rseCodigo, CodedValues.TOC_RSE_ALTERACAO_DADOS_CADASTRAIS, ApplicationResourcesHelper.getMessage("mensagem.informacao.dados.cadastrais.foram.alterados", responsavel, msgOrs.toString()) + obs, registroServidor.getTipoMotivo(), responsavel);
                }
                if (margemAlterada.length() > 0) {
                    criaOcorrenciaRSE(rseCodigo, CodedValues.TOC_RSE_ALTERACAO_MARGEM, ApplicationResourcesHelper.getMessage("mensagem.informacao.margem.servidor.foi.alterada", responsavel, margemAlterada.toString()) + obs, registroServidor.getTipoMotivo(), responsavel);
                }
                if (excluiServidorManualmente) {
                    criaOcorrenciaRSE(rseCodigo, CodedValues.TOC_RSE_EXCLUSAO_POR_CARGA_MARGEM, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ors.obs.exclusao.manual", responsavel) + obs, registroServidor.getTipoMotivo(), responsavel);
                }
                if (reativaServidorManualmente) {
                    criaOcorrenciaRSE(rseCodigo, CodedValues.TOC_RSE_REATIVACAO_POR_CARGA_MARGEM, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ors.obs.reativacao.manual", responsavel) + obs, registroServidor.getTipoMotivo(), responsavel);
                }
                if (bloqueiaServidorManualmente) {
                    criaOcorrenciaRSE(rseCodigo, CodedValues.TOC_RSE_BLOQUEIO_STATUS_MANUAL, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ors.obs.bloqueio.manual", responsavel) + obs, registroServidor.getTipoMotivo(), responsavel);
                }
                if (desbloqueiaServidorManualmente) {
                    criaOcorrenciaRSE(rseCodigo, CodedValues.TOC_RSE_DESBLOQUEIO_STATUS_MANUAL, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ors.obs.desbloqueio.manual", responsavel) + obs, registroServidor.getTipoMotivo(), responsavel);
                }
                if (falecimentoManualServidor || falecimentoAutomaticoServidor) {
                    criaOcorrenciaRSE(rseCodigo, CodedValues.TOC_FALECIMENTO_RSE, ApplicationResourcesHelper.getMessage(falecimentoAutomaticoServidor ? "mensagem.ocorrencia.ors.obs.falecido.carga.falecidos" : "mensagem.ocorrencia.ors.obs.falecido.manualmente", responsavel) + obs, registroServidor.getTipoMotivo(), responsavel);
                }
            }

            // Gravação de histórico de margem
            if (geraHistoricoMargem) {
                final String operacao = transferenciaMargem ? OperacaoHistoricoMargemEnum.TRANSFERENCIA_MARGEM.getCodigo() : OperacaoHistoricoMargemEnum.EDT_REGISTRO_SERVIDOR.getCodigo();
                boolean liberouMargem = false;

                final BigDecimal margemRest1Depois = rseBean.getRseMargemRest();
                final BigDecimal margemRest2Depois = rseBean.getRseMargemRest2();
                final BigDecimal margemRest3Depois = rseBean.getRseMargemRest3();

                if ((margemRest1Antes != null) && (margemRest1Depois != null) && !margemRest1Antes.equals(margemRest1Depois)) {
                    HistoricoMargemRegistroServidorHome.create(rseCodigo, CodedValues.INCIDE_MARGEM_SIM, null, operacao, margemRest1Antes, margemRest1Depois);
                    liberouMargem |= margemRest1Depois.compareTo(margemRest1Antes) > 0;
                }
                if ((margemRest2Antes != null) && (margemRest2Depois != null) && !margemRest2Antes.equals(margemRest2Depois)) {
                    HistoricoMargemRegistroServidorHome.create(rseCodigo, CodedValues.INCIDE_MARGEM_SIM_2, null, operacao, margemRest2Antes, margemRest2Depois);
                    liberouMargem |= margemRest2Depois.compareTo(margemRest2Antes) > 0;
                }
                if ((margemRest3Antes != null) && (margemRest3Depois != null) && !margemRest3Antes.equals(margemRest3Depois)) {
                    HistoricoMargemRegistroServidorHome.create(rseCodigo, CodedValues.INCIDE_MARGEM_SIM_3, null, operacao, margemRest3Antes, margemRest3Depois);
                    liberouMargem |= margemRest3Depois.compareTo(margemRest3Antes) > 0;
                }

                for (final Short codMargem : margemRestAntes.keySet()) {
                    if (margemRestDepois.containsKey(codMargem)) {
                        final BigDecimal vlrMargemRestAntes = margemRestAntes.get(codMargem);
                        final BigDecimal vlrMargemRestDepois = margemRestDepois.get(codMargem);
                        if ((vlrMargemRestAntes != null) && (vlrMargemRestDepois != null) && !vlrMargemRestAntes.equals(vlrMargemRestDepois)) {
                            HistoricoMargemRegistroServidorHome.create(rseCodigo, codMargem, null, operacao, vlrMargemRestAntes, vlrMargemRestDepois);
                            liberouMargem |= vlrMargemRestDepois.compareTo(vlrMargemRestAntes) > 0;
                        }
                    }
                }

                if (liberouMargem) {
                    // Se é uma operação de liberação de margem, então registra esta operação no controle de segurança
                    segurancaController.registrarOperacoesLiberacaoMargem(rseCodigo, null, responsavel);
                }
            }
            final StringBuilder corpoEmailRse = new StringBuilder();
            // Envia e-mail de notificação à todas CSA/COR que possuem contratos ativos com este servidor
            if (responsavel.isCseOrg() && (CodedValues.FUN_EDT_SERVIDOR.equals(responsavel.getFunCodigo()) || CodedValues.FUN_VALIDAR_SERVIDOR.equals(responsavel.getFunCodigo())) && ParamSist.paramEquals(CodedValues.TPC_ENVIA_EMAIL_NOTIFICACAO_EDT_SERVIDOR, CodedValues.TPC_SIM, responsavel) && (((merge.getAtributos().size() == 1) && !merge.getAtributos().containsKey(Columns.RSE_DATA_ALTERACAO)) || (merge.getAtributos().size() > 1))) {
                try {
                    final StringBuilder textoEmail = new StringBuilder();
                    if (msgOrs.length() > 0) {
                        textoEmail.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.dados.cadastrais.foram.alterados", responsavel, msgOrs.toString()));
                        textoEmail.append("<br/>\n");
                    }
                    if (margemAlterada.length() > 0) {
                        textoEmail.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.margem.servidor.foi.alterada", responsavel, margemAlterada.toString()));
                        textoEmail.append("<br/>\n");
                    }
                    if (falecimentoManualServidor) {
                        textoEmail.append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ors.obs.falecido.manualmente", responsavel));
                        textoEmail.append("<br/>\n");
                    }
                    textoEmail.append(obs).append("<br/>\n");
                    if (enviaEmail) {
                        EnviaEmailHelper.enviarEmailCsasAlteracaoSer(rseCache, null, merge.getAtributos().containsKey(Columns.RSE_MATRICULA) ? (String) merge.getAttribute(Columns.RSE_MATRICULA) : rseCache.getRseMatricula(), merge.getAtributos().containsKey(Columns.SRS_CODIGO) ? (String) merge.getAttribute(Columns.SRS_CODIGO) : null, textoEmail.toString(), responsavel);
                    }
                    corpoEmailRse.append(textoEmail);
                } catch (final Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }

            // Envia e-mail ao CSE/ORG relativo ao cadastro do servidor
            if (responsavel.isCsaCor() && ParamSist.paramEquals(CodedValues.TPC_ENVIA_EMAIL_NOTIFICACAO_CAD_SERVIDOR, CodedValues.TPC_SIM, responsavel) && (registroServidor.getSrsCodigo() != null) && CodedValues.SRS_PENDENTE.equals(registroServidor.getSrsCodigo())) {
                EnviaEmailHelper.enviarEmailNotificacaoCadastroServidor(rseCodigo, responsavel);
            }

            if (margemAlterada.length() > 0) {
                logMargem.write();
            }
            log.write();
            if (enviaEmail) {
                return "";
            } else {
                return corpoEmailRse.toString();
            }
        } catch (FindException | CreateException | UpdateException | HQueryException | BeneficioControllerException  ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final ZetraException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException(ex);
        }
    }

    @Override
    public String createRegistroServidor(RegistroServidorTO registroServidor, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final RegistroServidor rseBean = RegistroServidorHome.create(registroServidor.getSerCodigo(), registroServidor.getOrgCodigo(), registroServidor.getSrsCodigo(), registroServidor.getRseMatricula(), registroServidor.getRseMargem(), registroServidor.getRseMargemRest(), registroServidor.getRseMargemUsada(), registroServidor.getRseMargem2(), registroServidor.getRseMargemRest2(), registroServidor.getRseMargemUsada2(), registroServidor.getRseMargem3(), registroServidor.getRseMargemRest3(), registroServidor.getRseMargemUsada3(), registroServidor.getRseTipo(), registroServidor.getRsePrazo(),
                    registroServidor.getRseDataAdmissao(), registroServidor.getRseCLT(), registroServidor.getRseParamQtdAdeDefault(), registroServidor.getBcoCodigo(), registroServidor.getRseObs(), registroServidor.getRseAssociado(), registroServidor.getRseEstabilizado(), registroServidor.getRseDataCarga(), registroServidor.getRseDataFimEngajamento(), registroServidor.getRseDataLimitePermanencia(), registroServidor.getRseBancoSal(), registroServidor.getRseAgenciaSal(), registroServidor.getRseAgenciaDvSal(), registroServidor.getRseContaSal(), registroServidor.getRseContaDvSal(),
                    registroServidor.getRseBancoSalAlternativo(), registroServidor.getRseAgenciaSalAlternativa(), registroServidor.getRseAgenciaDvSalAlternativa(), registroServidor.getRseContaSalAlternativa(), registroServidor.getRseContaDvSalAlternativa(), registroServidor.getRseSalario(), registroServidor.getRseProventos(), registroServidor.getRseDescontosComp(), registroServidor.getRseDescontosFacu(), registroServidor.getRseOutrosDescontos(), registroServidor.getCrsCodigo(), registroServidor.getPrsCodigo(), registroServidor.getSboCodigo(), registroServidor.getUniCodigo(),
                                                                         registroServidor.getVrsCodigo(), registroServidor.getPosCodigo(), registroServidor.getTrsCodigo(), registroServidor.getCapCodigo(), registroServidor.getRsePraca(), registroServidor.getRseBeneficiarioFinanDvCart(), registroServidor.getRseMunicipioLotacao(), registroServidor.getRseMatriculaInst(), registroServidor.getRseDataContracheque(), registroServidor.getRseBaseCalculo(), registroServidor.getRsePedidoDemissao(), registroServidor.getRseDataSaida(), registroServidor.getRseDataUltSalario(),
                                                                         registroServidor.getRseDataRetorno(), registroServidor.getRseMotivoFaltaMargem());

            final String rseCodigo = rseBean.getRseCodigo();
            final LogDelegate log = new LogDelegate(responsavel, Log.REGISTRO_SERVIDOR, Log.CREATE, Log.LOG_INFORMACAO);
            log.setRegistroServidor(rseCodigo);
            log.setServidor(registroServidor.getSerCodigo());
            log.setOrgao(registroServidor.getOrgCodigo());
            if (!TextHelper.isNull(registroServidor.getVrsCodigo())) {
                log.setVincRseCodigo(registroServidor.getVrsCodigo());
            }
            if (!TextHelper.isNull(registroServidor.getCrsCodigo())) {
                log.setCargoRseCodigo(registroServidor.getCrsCodigo());
            }
            if (!TextHelper.isNull(registroServidor.getSrsCodigo())) {
                log.setStatusRseCodigo(registroServidor.getSrsCodigo());
            }
            log.getUpdatedFields(registroServidor.getAtributos(), null);
            log.write();

            return rseCodigo;
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            ServidorControllerException excecao = new ServidorControllerException("mensagem.erro.nao.possivel.criar.registro.servidor.erro.interno", responsavel, ex.getMessage());
            if (ex.getMessage().indexOf("Invalid argument value") != -1) {
                excecao = new ServidorControllerException("mensagem.erro.nao.possivel.criar.registro.servidor.existe.outro.mesma.matricula.orgao", responsavel);
            }
            throw excecao;
        }
    }

    @Override
    public int countRegistroServidor(List<String> srsCodigos, List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaRegistrosServidoresQuery query = new ListaRegistrosServidoresQuery(srsCodigos, orgCodigos, estCodigos, true);
            return query.executarContador();

        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public int countRegistroServidorTransferidos(AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaRegistrosServidoresTransferidosQuery query = new ListaRegistrosServidoresTransferidosQuery(true);
            return query.executarContador();

        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public int countRegistroServidorExcluidos(AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaRegistrosServidoresExcluidosQuery query = new ListaRegistrosServidoresExcluidosQuery(true);
            return query.executarContador();

        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> countQtdeServidorPorOrg(AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaQtdeServidorPorOrgQuery query = new ListaQtdeServidorPorOrgQuery(responsavel);
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstRegistroServidor(List<String> srsCodigos, List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaRegistrosServidoresQuery query = new ListaRegistrosServidoresQuery(srsCodigos, orgCodigos, estCodigos);
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstRegistroServidor(String serCodigo, String orgCodigo, String estCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        final List<String> orgCodigos = new ArrayList<>();
        if (!TextHelper.isNull(orgCodigo)) {
            orgCodigos.add(orgCodigo);
        }
        final List<String> estCodigos = new ArrayList<>();
        if (!TextHelper.isNull(estCodigo)) {
            estCodigos.add(estCodigo);
        }
        return lstRegistroServidor(serCodigo, orgCodigos, estCodigos, responsavel);
    }

    @Override
    public List<TransferObject> lstRegistroServidor(String serCodigo, List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws ServidorControllerException {
        return lstRegistroServidor(serCodigo, orgCodigos, estCodigos, true, responsavel);
    }

    @Override
    public List<TransferObject> lstRegistroServidor(String serCodigo, List<String> orgCodigos, List<String> estCodigos, boolean recuperaRseExcluidos, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaRegistroServidorQuery query = new ListaRegistroServidorQuery();
            query.serCodigo = serCodigo;
            query.orgCodigos = orgCodigos;
            query.estCodigos = estCodigos;
            query.recuperaRseExcluido = recuperaRseExcluidos;

            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstRegistroServidorAuditoriaTotal(AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaRegistroServidorAuditoriaTotalQuery query = new ListaRegistroServidorAuditoriaTotalQuery();
            return query.executarDTO();

        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public String criaOcorrenciaSER(String serCodigo, String tocCodigo, String ocsObs, String tmoCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final String usuCodigo = responsavel != null ? responsavel.getUsuCodigo() : null;
            final OcorrenciaServidor ocs = OcorrenciaServidorHome.create(serCodigo, tocCodigo, usuCodigo, ocsObs, responsavel.getIpUsuario(), tmoCodigo);
            return ocs.getOcsCodigo();
        } catch (final CreateException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public String criaOcorrenciaRSE(String rseCodigo, String tocCodigo, String orsObs, String tmoCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final String usuCodigo = responsavel != null ? responsavel.getUsuCodigo() : null;
            final OcorrenciaRegistroSer ors = OcorrenciaRegistroServidorHome.create(rseCodigo, tocCodigo, usuCodigo, orsObs, responsavel.getIpUsuario(), tmoCodigo);
            return ors.getOrsCodigo();
        } catch (final CreateException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void criaOcorrenciaRSE(List<TransferObject> excluidosTO, String tocCodigo, String orsObs, String tmoCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            for (final TransferObject to : excluidosTO) {
                final String rseCodigo = (String) to.getAttribute(Columns.RSE_CODIGO);
                criaOcorrenciaRSE(rseCodigo, tocCodigo, orsObs, tmoCodigo, responsavel);
            }
        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> lstOrsRegistroServidor(TransferObject toOrsRegistroServidor, int offset, int count, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaOcorrenciaRegistroServidorQuery query = new ListaOcorrenciaRegistroServidorQuery();
            if (offset != -1) {
                query.firstResult = offset;
            }
            if (count != -1) {
                query.maxResults = count;
            }
            query.rseCodigo = (String) toOrsRegistroServidor.getAttribute(Columns.ORS_RSE_CODIGO);
            query.tocCodigo = (String) toOrsRegistroServidor.getAttribute(Columns.ORS_TOC_CODIGO);
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erro.listar.ocorrencias", responsavel, ex);
        }
    }

    @Override
    public int countOcorrenciaSerUnionRse(TransferObject criterio, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaOcorrenciaSerUnionRseQuery query = new ListaOcorrenciaSerUnionRseQuery();
            query.count = true;
            query.serCodigo = (String) criterio.getAttribute(Columns.OCS_SER_CODIGO);
            query.tocCodigo = (String) criterio.getAttribute(Columns.OCS_TOC_CODIGO);
            query.rseCodigo = (String) criterio.getAttribute(Columns.ORS_RSE_CODIGO);
            query.tocCodigoRse = (String) criterio.getAttribute(Columns.ORS_TOC_CODIGO);
            return query.executarContador();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erro.contar.ocorrencias", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstOcorrenciaSerUnionRse(TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaOcorrenciaSerUnionRseQuery query = new ListaOcorrenciaSerUnionRseQuery();
            if (offset != -1) {
                query.firstResult = offset;
            }
            if (count != -1) {
                query.maxResults = count;
            }
            query.serCodigo = (String) criterio.getAttribute(Columns.OCS_SER_CODIGO);
            query.tocCodigo = (String) criterio.getAttribute(Columns.OCS_TOC_CODIGO);
            query.rseCodigo = (String) criterio.getAttribute(Columns.ORS_RSE_CODIGO);
            query.tocCodigoRse = (String) criterio.getAttribute(Columns.ORS_TOC_CODIGO);
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erro.listar.ocorrencias", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstDataOcorrenciaServidor(String serCodigo, String tocCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ObtemDataOcorrenciaServidorQuery query = new ObtemDataOcorrenciaServidorQuery();
            query.serCodigo = serCodigo;
            query.tocCodigo = tocCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erro.listar.ocorrencias", responsavel, ex);
        }
    }

    @Override
    public int countOrsRegistroServidor(TransferObject toOrsRegistroServidor, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaOcorrenciaRegistroServidorQuery query = new ListaOcorrenciaRegistroServidorQuery();
            query.count = true;
            query.rseCodigo = (String) toOrsRegistroServidor.getAttribute(Columns.ORS_RSE_CODIGO);
            query.tocCodigo = (String) toOrsRegistroServidor.getAttribute(Columns.ORS_TOC_CODIGO);
            return query.executarContador();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erro.contar.ocorrencias", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> findRegistroServidoresByMatriculas(List<String> rseMatriculas, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaRegistroServidorQuery listaRegistroServidorQuery = new ListaRegistroServidorQuery();
            listaRegistroServidorQuery.rseMatriculas = rseMatriculas;
            return listaRegistroServidorQuery.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void setRseQtdAdeDefault(List<String> cnvCodigo) throws ServidorControllerException {
        try {
            final ServidorDAO dao = DAOFactory.getDAOFactory().getServidorDAO();
            dao.setRseQtdAdeDefault(cnvCodigo);
        } catch (final DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public TransferObject getRegistroServidorPelaMatricula(String serCodigo, String orgCodigo, String estCodigo, String rseMatricula, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            TransferObject rse = null;
            final ListaRegistrosServidoresQuery query = new ListaRegistrosServidoresQuery(serCodigo, orgCodigo, estCodigo, rseMatricula);
            final List<TransferObject> servidores = query.executarDTO();

            if ((servidores != null) && (servidores.size() > 0)) {
                if (servidores.size() == 1) {
                    // Se encontrou apenas um, então este será o retorno da operação
                    final TransferObject servidorTmp = servidores.get(0);
                    final String srsCodigo = servidorTmp.getAttribute(Columns.SRS_CODIGO).toString();
                    if (!CodedValues.SRS_INATIVOS.contains(srsCodigo)) {
                        rse = servidorTmp;
                    }
                } else {
                    // Se encontrou mais de um, retorna aquele que possui maior margem restante
                    Double margemRest = Double.NEGATIVE_INFINITY;
                    for (final TransferObject servidorTmp : servidores) {
                        final Double margemRestTmp = Double.valueOf(servidorTmp.getAttribute(Columns.RSE_MARGEM_REST).toString());
                        final String srsCodigoTmp = servidorTmp.getAttribute(Columns.SRS_CODIGO).toString();
                        if ((margemRest.compareTo(margemRestTmp) < 0) && !CodedValues.SRS_INATIVOS.contains(srsCodigoTmp)) {
                            margemRest = margemRestTmp;
                            rse = servidorTmp;
                        }
                    }
                }
            }
            return rse;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /*****************************************************************************/

    @Override
    public List<TransferObject> getEstCivil(AcessoSistema responsavel) {
        try {
            final ListaEstadoCivilQuery query = new ListaEstadoCivilQuery();
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            return null;
        }
    }

    @Override
    public String getEstCivil(String estCvlCodigo, AcessoSistema responsavel) {
        try {
            if (!TextHelper.isNull(estCvlCodigo)) {
                final ListaEstadoCivilQuery query = new ListaEstadoCivilQuery();
                query.estCvlCodigo = estCvlCodigo;

                final List<TransferObject> estCivil = query.executarDTO();
                if ((estCivil != null) && (estCivil.size() > 0)) {
                    final TransferObject to = estCivil.get(0);
                    return (String) to.getAttribute(Columns.EST_CIVIL_DESCRICAO);
                }
            }

            return null;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            return null;
        }
    }

    @Override
    public List<TransferObject> getNivelEscolaridade(AcessoSistema responsavel) {
        try {
            final ListaNivelEscolaridadeQuery query = new ListaNivelEscolaridadeQuery();
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            return null;
        }
    }

    @Override
    public String getNivelEscolaridade(String nesCodigo, AcessoSistema responsavel) {
        try {
            if (!TextHelper.isNull(nesCodigo)) {
                final ListaNivelEscolaridadeQuery query = new ListaNivelEscolaridadeQuery();
                query.nesCodigo = nesCodigo;

                final List<TransferObject> nivelEscolaridadeList = query.executarDTO();
                if ((nivelEscolaridadeList != null) && (nivelEscolaridadeList.size() > 0)) {
                    final TransferObject to = nivelEscolaridadeList.get(0);
                    return (String) to.getAttribute(Columns.NES_DESCRICAO);
                }
            }

            return null;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            return null;
        }
    }

    @Override
    public List<TransferObject> getTipoHabitacao(AcessoSistema responsavel) {
        try {
            final ListaTipoHabitacaoQuery query = new ListaTipoHabitacaoQuery();
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            return null;
        }
    }

    @Override
    public String getTipoHabitacao(String thaCodigo, AcessoSistema responsavel) {
        try {
            if (!TextHelper.isNull(thaCodigo)) {
                final ListaTipoHabitacaoQuery query = new ListaTipoHabitacaoQuery();
                query.thaCodigo = thaCodigo;

                final List<TransferObject> tipoHabitacaoList = query.executarDTO();
                if ((tipoHabitacaoList != null) && (tipoHabitacaoList.size() > 0)) {
                    final TransferObject to = tipoHabitacaoList.get(0);
                    return (String) to.getAttribute(Columns.THA_DESCRICAO);
                }
            }

            return null;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            return null;
        }
    }

    @Override
    public String buscaImgServidor(String serCpf, String rseCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ServidorDAO dao = DAOFactory.getDAOFactory().getServidorDAO();
            return dao.buscaImgServidor(serCpf, rseCodigo);

        } catch (final DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    // Pesquisa o cargo do servidor e retorna uma lista de cargos
    @Override
    public List<TransferObject> lstCargo(AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaCargoRegistroServidorQuery query = new ListaCargoRegistroServidorQuery();
            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> findCargoByIdentificador(String crsIdentificador, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaCargoRegistroServidorQuery query = new ListaCargoRegistroServidorQuery();
            query.crsIdentificador = crsIdentificador;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    // Pesquisa o padrão do servidor e retorna uma lista
    @Override
    public List<TransferObject> lstPadrao(AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaPadraoRegistroServidorQuery query = new ListaPadraoRegistroServidorQuery();
            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> findPadraoByIdentificador(String prsIdentificador, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaPadraoRegistroServidorQuery query = new ListaPadraoRegistroServidorQuery();
            query.prsIdentificador = prsIdentificador;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstPosto(AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaPostoRegistroServidorQuery query = new ListaPostoRegistroServidorQuery();
            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstTipoRegistroServidor(AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaTipoRegistroServidorQuery query = new ListaTipoRegistroServidorQuery();
            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstCapacidadeCivil(AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaCapacidadeRegistroServidorQuery query = new ListaCapacidadeRegistroServidorQuery();
            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    // Pesquisa o Sub-Orgão do servidor e retorna todos eles numa lista
    @Override
    public List<TransferObject> lstSubOrgao(AcessoSistema responsavel, String orgCodigo) throws ServidorControllerException {
        try {
            final ListaSubOrgaoQuery query = new ListaSubOrgaoQuery();
            query.orgCodigo = orgCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> findSubOrgaoByIdentificador(String sboIdentificador, String orgCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaSubOrgaoQuery query = new ListaSubOrgaoQuery();
            query.orgCodigo = orgCodigo;
            query.sboIdentificador = sboIdentificador;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    // Pesquisa a unidade do servidor e retorna todas numa lista
    @Override
    public List<TransferObject> lstUnidade(AcessoSistema responsavel, String sboCodigo) throws ServidorControllerException {
        try {
            final ListaUnidadeQuery query = new ListaUnidadeQuery();
            query.sboCodigo = sboCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> findUnidadeByIdentificador(String uniIdentificador, String sboCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaUnidadeQuery query = new ListaUnidadeQuery();
            query.sboCodigo = sboCodigo;
            query.uniIdentificador = uniIdentificador;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstStatusRegistroServidor(boolean ignoraStatusExcluidos, boolean ignoraStatusBloqSeguranca, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaStatusRegistroServidorQuery query = new ListaStatusRegistroServidorQuery();
            query.ignoraStatusExcluidos = ignoraStatusExcluidos;
            query.ignoraStatusBloqSeguranca = ignoraStatusBloqSeguranca;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> selectVincRegistroServidor(boolean ativos, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaVinculoRegistroServidorQuery query = new ListaVinculoRegistroServidorQuery();
            query.ativo = ativos;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> findVincRegistroServidor(String vrsIdentificador, boolean ativos, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaVinculoRegistroServidorQuery query = new ListaVinculoRegistroServidorQuery();
            query.ativo = ativos;
            query.vrsIdentificador = vrsIdentificador;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<String> selectCnvVincRseSer(String csaCodigo, String svcCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaConvenioVinculoRegistroServidorQuery query = new ListaConvenioVinculoRegistroServidorQuery();
            query.csaCodigo = csaCodigo;
            query.svcCodigo = svcCodigo;

            return query.executarLista();
        } catch (final HQueryException ex) {
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void updateCnvVincRseSer(String csaCodigo, String svcCodigo, List<String> vrsCodigos, Map<String, List<String>> vinculosBloqDesbloq, AcessoSistema responsavel) throws ServidorControllerException {
        try {

            final boolean bloqPadrao = (parametroController.getParamCsa(csaCodigo, CodedValues.TPA_INFO_VINC_BLOQ_PADRAO, responsavel) != null) && CodedValues.TPA_SIM.equals(parametroController.getParamCsa(csaCodigo, CodedValues.TPA_INFO_VINC_BLOQ_PADRAO, responsavel));
            final Collection<ConvenioVinculoRegistro> cvrList = ConvenioVinculoRegistroHome.findByCsaSvcCodigo(csaCodigo, svcCodigo);

            for (final ConvenioVinculoRegistro cvr : cvrList) {
                final String vrsCodigo = cvr.getVrsCodigo();
                final String vrsIdentificador = cvr.getVinculoRegistroServidor().getVrsIdentificador();
                if (!vrsCodigos.contains(vrsCodigo)) {

                    if(bloqPadrao) {
                        if(!vinculosBloqDesbloq.get("bloqueados").contains(vrsIdentificador)) {
                            vinculosBloqDesbloq.get("bloqueados").add(vrsIdentificador);
                        }
                    } else if(!vinculosBloqDesbloq.get("desbloqueados").contains(vrsIdentificador)) {
                        vinculosBloqDesbloq.get("desbloqueados").add(vrsIdentificador);
                    }

                    // Remove a ligação do vínculo ao convênio
                    AbstractEntityHome.remove(cvr);

                    // Grava log da remoção da ligação do vínculo ao convênio
                    final LogDelegate log = new LogDelegate(responsavel, Log.CNV_VINCULO_REGISTRO_SERVIDOR, Log.DELETE , Log.LOG_INFORMACAO);
                    log.setConsignataria(csaCodigo);
                    log.setServico(svcCodigo);
                    log.setVincRseCodigo(vrsCodigo);
                    log.add(bloqPadrao ? ApplicationResourcesHelper.getMessage("mensagem.informacao.incluindo.bloqueio.vinculos.csa.svc", responsavel) : ApplicationResourcesHelper.getMessage("mensagem.informacao.removendo.bloqueio.vinculos.csa.svc", responsavel));
                    log.write();
                }
            }

            final ListaCnvVinculoRegistroFaltanteQuery query = new ListaCnvVinculoRegistroFaltanteQuery();
            query.csaCodigo = csaCodigo;
            query.svcCodigo = svcCodigo;
            query.vrsCodigos = vrsCodigos;

            final List<TransferObject> cvrFaltante = query.executarDTO();
            for (final TransferObject cvr : cvrFaltante) {
                final String vrsCodigo = cvr.getAttribute(Columns.VRS_CODIGO).toString();
                final String vrsIdentificador = cvr.getAttribute(Columns.VRS_IDENTIFICADOR).toString();

                ConvenioVinculoRegistroHome.create(vrsCodigo, svcCodigo, csaCodigo);

                if(bloqPadrao) {
                    if(!vinculosBloqDesbloq.get("desbloqueados").contains(vrsIdentificador)) {
                        vinculosBloqDesbloq.get("desbloqueados").add(vrsIdentificador);
                    }
                } else if(!vinculosBloqDesbloq.get("bloqueados").contains(vrsIdentificador)) {
                    vinculosBloqDesbloq.get("bloqueados").add(vrsIdentificador);
                }

                // Grava log da criação da ligação do vínculo ao convênio
                final LogDelegate log = new LogDelegate(responsavel, Log.CNV_VINCULO_REGISTRO_SERVIDOR, Log.CREATE , Log.LOG_INFORMACAO);
                log.setConsignataria(csaCodigo);
                log.setServico(svcCodigo);
                log.setVincRseCodigo(vrsCodigo);
                log.add(bloqPadrao ? ApplicationResourcesHelper.getMessage("mensagem.informacao.removendo.bloqueio.vinculos.csa.svc", responsavel) : ApplicationResourcesHelper.getMessage("mensagem.informacao.incluindo.bloqueio.vinculos.csa.svc", responsavel));
                log.write();
            }
        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /************************* HISTORICO MARGEM **********************************/

    @Override
    public List<TransferObject> pesquisarHistoricoMargem(String rseCodigo, int offset, int count, TransferObject criterio, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaHistoricoMargemQuery query = new ListaHistoricoMargemQuery(responsavel);
            query.rseCodigo = rseCodigo;
            if (count != -1) {
                query.maxResults = count;
                query.firstResult = offset;
            }
            setaCriterioHistoricoMargemQuery(query, criterio);

            final LogDelegate log = new LogDelegate(responsavel, Log.HISTORICO_MARGEM, Log.SELECT, Log.LOG_INFORMACAO);
            log.setRegistroServidor(rseCodigo);
            log.setMargem(criterio.getAttribute("marCodigo") != null ? criterio.getAttribute("marCodigo").toString() : null);
            log.write();

            return query.executarDTO();
        } catch (final LogControllerException | HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public int countHistoricoMargem(String rseCodigo, TransferObject criterio, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaHistoricoMargemQuery query = new ListaHistoricoMargemQuery(responsavel);
            query.count = true;
            query.rseCodigo = rseCodigo;
            setaCriterioHistoricoMargemQuery(query, criterio);
            return query.executarContador();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private void setaCriterioHistoricoMargemQuery(ListaHistoricoMargemQuery query, TransferObject criterio) {
        if (criterio != null) {
            if (!TextHelper.isNull(criterio.getAttribute("marCodigo"))) {
                try {
                    query.marCodigo = Short.valueOf(criterio.getAttribute("marCodigo").toString());
                } catch (final NumberFormatException e) {
                    LOG.error(e.getMessage(), e);
                    query.marCodigo = null;
                }
            }
            if (!TextHelper.isNull(criterio.getAttribute("adeNumero"))) {
                try {
                    query.adeNumero = Long.valueOf(criterio.getAttribute("adeNumero").toString());
                } catch (final NumberFormatException e) {
                    LOG.error(e.getMessage(), e);
                    query.adeNumero = null;
                }
            }
            if (!TextHelper.isNull(criterio.getAttribute("periodoIni"))) {
                try {
                    query.periodoIni = DateHelper.parse(criterio.getAttribute("periodoIni").toString() + " 00:00:00", LocaleHelper.getDateTimePattern());
                } catch (final ParseException e) {
                    LOG.error(e.getMessage(), e);
                    query.periodoIni = null;
                }
            }
            if (!TextHelper.isNull(criterio.getAttribute("periodoFim"))) {
                try {
                    query.periodoFim = DateHelper.parse(criterio.getAttribute("periodoFim").toString() + " 23:59:59", LocaleHelper.getDateTimePattern());
                } catch (final ParseException e) {
                    LOG.error(e.getMessage(), e);
                    query.periodoFim = null;
                }
            }
            if (!TextHelper.isNull(criterio.getAttribute("hmrOperacao"))) {
                query.hmrOperacao = criterio.getAttribute("hmrOperacao").toString();
            }
        }
    }

    /**
     * Recupera o histórico de variação de margem de um período de tempo de um determinado registro servidor.
     * @param rseCodigo
     * @param marCodigo
     * @param qtdeMesesPesquisa
     * @param responsavel
     * @return
     * @throws AutorizacaoControllerException
     */
    @Override
    public List<TransferObject> lstHistoricoVariacaoMargem(String rseCodigo, Short marCodigo, int qtdeMesesPesquisa, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaHistoricoVariacaoMargemQuery query = new ListaHistoricoVariacaoMargemQuery(qtdeMesesPesquisa);
            query.rseCodigo = rseCodigo;
            query.marCodigo = marCodigo;

            final LogDelegate log = new LogDelegate(responsavel, Log.HISTORICO_MARGEM, Log.SELECT, Log.LOG_INFORMACAO);
            log.setRegistroServidor(rseCodigo);
            log.setMargem(marCodigo.toString());
            log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.parametro.pesquisa.quantidade.meses", responsavel, String.valueOf(qtdeMesesPesquisa)));
            log.write();

            return query.executarDTO();
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /*****************************************************************************/

    @Override
    public int countConvenioBloqueados(String rseCodigo, String orgCodigo, String csaCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaBloqueioConvenioServidorQuery query = new ListaBloqueioConvenioServidorQuery();
            query.count = true;
            query.rseCodigo = rseCodigo;
            query.orgCodigo = orgCodigo;
            query.csaCodigo = csaCodigo;
            return query.executarContador();
        } catch (final HQueryException ex) {
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> selectConvenioBloqueados(String rseCodigo, String orgCodigo, String csaCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaBloqueioConvenioServidorQuery query = new ListaBloqueioConvenioServidorQuery();
            query.rseCodigo = rseCodigo;
            query.orgCodigo = orgCodigo;
            query.csaCodigo = csaCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Valida a data de nascimento do servidor caso a mesma esteja habilitada para o serviço dado.
     */
    @Override
    public boolean isDataNascServidorValida(String dataAValidar, String dataServidor, String svcCodigo, String dateFormat, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final CustomTransferObject paramCTO = parametroController.getParamSvcCse(svcCodigo, CodedValues.TPS_VALIDAR_DATA_NASCIMENTO_NA_RESERVA, responsavel);
            if (paramCTO != null) {
                final String paramValue = paramCTO.getAttribute(Columns.PSE_VLR).toString();
                final boolean validaData = ("1".equals(paramValue));

                if (validaData && ((dataAValidar == null) || (dataServidor == null) || (DateHelper.dateDiff(dataAValidar, dataServidor, dateFormat, null, "DIAS") != 0))) {
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }

        } catch (final RuntimeException re) {
            throw new ServidorControllerException("mensagem.dataMalFormatada", responsavel);
        } catch (final Exception slx) {
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, slx);
        }
    }

    /**
     * Realiza a transferência de valores entre margens.
     * @param registroServidor
     * @param transfTotal
     * @param valor
     * @param marCodigoOrigem
     * @param marCodigoDestino
     * @param responsavel
     * @return
     * @throws ServidorControllerException
     */
    @Override
    public boolean transferirMargem(RegistroServidorTO registroServidor, String transfTotal, BigDecimal valor, Short marCodigoOrigem, Short marCodigoDestino, AcessoSistema responsavel) throws ServidorControllerException {
        boolean validacaoOk = false;

        // A transferência de margem só deve ser realizada se o servidor estiver ativo
        if (CodedValues.SRS_ATIVO.equals(registroServidor.getSrsCodigo())) {
            // Parâmetro para casamento de margem
            final boolean margem1CasadaMargem3Lateral = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_LATERAL, CodedValues.TPC_SIM, responsavel);

            if (margem1CasadaMargem3Lateral && marCodigoOrigem.equals(CodedValues.INCIDE_MARGEM_SIM_3) && marCodigoDestino.equals(CodedValues.INCIDE_MARGEM_SIM)) {
                // Se margem casada lateralmente 1 com 3, ao transferir da margem 3 para 1 ao invés de transferir
                // o valor baseado na rse_margem_rest_3, tem que transferir pela diferença (rse_margem_3 - rse_margem_usada_3),
                // que, neste caso, é diferente da restante_3 por causa do casamento.

                if (CodedValues.TPC_SIM.equals(transfTotal)) {
                    valor = registroServidor.getRseMargemRest3();
                }
                if ((valor.compareTo(BigDecimal.ZERO) > 0) && (registroServidor.getRseMargemRest3().compareTo(valor) >= 0)) {
                    validacaoOk = true;

                    // Valor a ser transferido = (rse_margem_3 - rse_margem_usada_3) - (rse_margem_rest_3 - valor)
                    final BigDecimal margemRest3Real = registroServidor.getRseMargem3().subtract(registroServidor.getRseMargemUsada3());
                    final BigDecimal novaMargemRest3 = registroServidor.getRseMargemRest3().subtract(valor);
                    valor = margemRest3Real.subtract(novaMargemRest3);

                    // Subtrai da Margem 3
                    registroServidor.setRseMargem3(registroServidor.getRseMargem3().subtract(valor));
                    registroServidor.setRseMargemRest3(novaMargemRest3);
                    // Soma na margem 1
                    registroServidor.setRseMargem(registroServidor.getRseMargem().add(valor));
                    registroServidor.setRseMargemRest(registroServidor.getRseMargemRest().add(valor));
                }

            } else {
                // Margem origem deve ter seu valor diminuido
                if (marCodigoOrigem.equals(CodedValues.INCIDE_MARGEM_SIM)) {
                    if (CodedValues.TPC_SIM.equals(transfTotal)) {
                        valor = registroServidor.getRseMargemRest();
                    }
                    if ((valor.compareTo(BigDecimal.ZERO) > 0) && (registroServidor.getRseMargemRest().compareTo(valor) >= 0)) {
                        registroServidor.setRseMargem(registroServidor.getRseMargem().subtract(valor));
                        registroServidor.setRseMargemRest(registroServidor.getRseMargemRest().subtract(valor));
                        validacaoOk = true;
                    }

                } else if (marCodigoOrigem.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
                    if (CodedValues.TPC_SIM.equals(transfTotal)) {
                        valor = registroServidor.getRseMargemRest2();
                    }
                    if ((valor.compareTo(BigDecimal.ZERO) > 0) && (registroServidor.getRseMargemRest2().compareTo(valor) >= 0)) {
                        registroServidor.setRseMargem2(registroServidor.getRseMargem2().subtract(valor));
                        registroServidor.setRseMargemRest2(registroServidor.getRseMargemRest2().subtract(valor));
                        validacaoOk = true;
                    }

                } else if (marCodigoOrigem.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
                    if (CodedValues.TPC_SIM.equals(transfTotal)) {
                        valor = registroServidor.getRseMargemRest3();
                    }
                    if ((valor.compareTo(BigDecimal.ZERO) > 0) && (registroServidor.getRseMargemRest3().compareTo(valor) >= 0)) {
                        registroServidor.setRseMargem3(registroServidor.getRseMargem3().subtract(valor));
                        registroServidor.setRseMargemRest3(registroServidor.getRseMargemRest3().subtract(valor));
                        validacaoOk = true;
                    }
                }

                // Margem destino deve ter seu valor aumentado
                if (marCodigoDestino.equals(CodedValues.INCIDE_MARGEM_SIM)) {
                    registroServidor.setRseMargem(registroServidor.getRseMargem().add(valor));
                    registroServidor.setRseMargemRest(registroServidor.getRseMargemRest().add(valor));

                } else if (marCodigoDestino.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
                    registroServidor.setRseMargem2(registroServidor.getRseMargem2().add(valor));
                    registroServidor.setRseMargemRest2(registroServidor.getRseMargemRest2().add(valor));

                } else if (marCodigoDestino.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
                    registroServidor.setRseMargem3(registroServidor.getRseMargem3().add(valor));
                    registroServidor.setRseMargemRest3(registroServidor.getRseMargemRest3().add(valor));
                }
            }

            if (validacaoOk) {
                // Atualizacao das margens no banco
                updateRegistroServidor(registroServidor, null, false, false, true, true, responsavel);
            }
        } else {
            throw new ServidorControllerException("mensagem.erro.transferencia.margem.nao.pode.ser.realizada.pois.servidor.nao.esta.ativo", responsavel);
        }

        return validacaoOk;
    }

    /**
     * lista registro servidor de usuário servidor dado pelo login
     * @param usuLogin - login do usuário servidor
     * @param recuperaRseExcluidos - recupera registros excluídos?
     * @param responsavel
     * @return
     * @throws ServidorControllerException
     */
    @Override
    public List<TransferObject> lstRegistroServidorUsuarioSer(String usuLogin, boolean recuperaRseExcluidos, AcessoSistema responsavel) throws ServidorControllerException {
        final ListaRegistroServidorUsuarioSerQuery query = new ListaRegistroServidorUsuarioSerQuery();
        query.recuperaRseExcluido = recuperaRseExcluidos;
        query.usuLogin = usuLogin;

        try {
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * lista registro servidor de usuário servidor dado pelo cpf
     * @param serCpf - cpf do usuário servidor
     * @param recuperaRseExcluidos - recupera registros excluídos?
     * @param responsavel
     * @return
     * @throws ServidorControllerException
     */
    @Override
    public List<TransferObject> lstRegistroServidorSerCodigo(String serCodigo, boolean recuperaRseExcluidos, AcessoSistema responsavel) throws ServidorControllerException {

        final ListaRegistroServidorSerCodigoQuery query = new ListaRegistroServidorSerCodigoQuery();

        query.serCodigo = serCodigo;
        query.recuperaRseExcluido = recuperaRseExcluidos;

        try {
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * retorna a quantidade de registro servidor de usuário servidor dado pelo código (ser_codigo)
     * @param serCodigo - código do servidor
     * @param recuperaRseExcluidos - recupera registros excluídos?
     * @param responsavel
     * @return
     * @throws ServidorControllerException
     */
    @Override
    public int countRegistroServidorSerCodigo(String serCodigo, boolean recuperaRseExcluidos, AcessoSistema responsavel) throws ServidorControllerException {

        final ListaRegistroServidorSerCodigoQuery query = new ListaRegistroServidorSerCodigoQuery();
        query.count = true;
        query.serCodigo = serCodigo;
        query.recuperaRseExcluido = recuperaRseExcluidos;

        try {
            return query.executarContador();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * verifica se ja existe um determinado email cadastrado para um servidor
     * @param serEmail - email do servidor
     * @param serCpfExceto - lista de CPFs ignorados na pesquisa
     * @param responsavel
     * @return
     * @throws ServidorControllerException
     */
    @Override
    public boolean existeEmailCadastrado(String serEmail, String serCpfExceto, AcessoSistema responsavel) throws ServidorControllerException {
        boolean existeEmailCadastrado = false;
        try {
            if (ParamSist.paramEquals(CodedValues.TPC_IMPEDE_EMAIL_IGUAL_ENTRE_SER_CPF, CodedValues.TPC_SIM, responsavel)) {
                final ObtemTotalServidoresPorEmailCelularQuery query = new ObtemTotalServidoresPorEmailCelularQuery();
                query.serEmail = serEmail;
                query.serCpfExceto = serCpfExceto;
                existeEmailCadastrado = query.executarContador() > 0;
            }
            if (!existeEmailCadastrado && ParamSist.paramEquals(CodedValues.TPC_IMPEDE_EMAIL_IGUAL_ENTRE_USU_E_SER, CodedValues.TPC_SIM, responsavel)) {
                final ObtemTotalUsuariosPorEmailQuery query = new ObtemTotalUsuariosPorEmailQuery();
                query.usuEmail = serEmail;
                query.usuCpfExceto = serCpfExceto;
                existeEmailCadastrado = query.executarContador() > 0;
            }
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
        return existeEmailCadastrado;
    }

    /**
     * verifica se ja existe um determinado celular cadastrado para um servidor
     * @param serCelular - celular do servidor
     * @param serCpfExceto - lista de CPFs ignorados na pesquisa
     * @param responsavel
     * @return
     * @throws ServidorControllerException
     */
    private boolean existeCelularCadastrado(String serCelular, String serCpfExceto, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ObtemTotalServidoresPorEmailCelularQuery query = new ObtemTotalServidoresPorEmailCelularQuery();
            query.serCelular = serCelular;
            query.serCpfExceto = serCpfExceto;
            return query.executarContador() > 0;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Lista tipos de base de cálculo
     * @param responsavel
     * @return
     * @throws ServidorControllerException
     */
    @Override
    public List<TransferObject> lstTipoBaseCalculo(AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaTipoBaseCalculoQuery query = new ListaTipoBaseCalculoQuery();
            return query.executarDTO();
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Sorteia um pergunta de dados cadastrais do grupo informado.
     *
     * @param pdcGrupo
     * @param responsavel
     * @return
     * @throws ServidorControllerException
     */
    @Override
    public TransferObject sorteiaPerguntaDadosCadastrais(Short pdcGrupo, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaPerguntaDadosCadastraisQuery query = new ListaPerguntaDadosCadastraisQuery();
            query.pdcGrupo = pdcGrupo;

            final List<TransferObject> lista = query.executarDTO();
            if ((lista != null) && !lista.isEmpty()) {
                Collections.shuffle(lista, new SecureRandom());
                return lista.get(0);
            }

            return null;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public boolean validaPerguntaDadosCadastrais(String rseCodigo, Short pdcGrupo, Short pdcNumero, String resposta, AcessoSistema responsavel) throws ServidorControllerException {
        if (TextHelper.isNull(pdcGrupo)) {
            throw new ServidorControllerException("mensagem.grupo.pergunta.dados.cadastrais.obrigatorio", responsavel);
        }

        if (TextHelper.isNull(pdcNumero)) {
            throw new ServidorControllerException("mensagem.pergunta.dados.cadastrais.obrigatorio", responsavel);
        }

        if (TextHelper.isNull(resposta)) {
            throw new ServidorControllerException("mensagem.resposta.pergunta.dados.cadastrais.obrigatorio", responsavel);
        }

        String pdcCampo = "";
        try {
            final PerguntaDadosCadastraisId id = new PerguntaDadosCadastraisId(pdcGrupo, pdcNumero);
            final PerguntaDadosCadastrais pdc = PerguntaDadosCadastraisHome.findByPrimaryKey(id);
            pdcCampo = pdc.getPdcCampo();
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.pergunta.dados.cadastrais.nao.existe", responsavel, ex);
        }

        if (TextHelper.isNull(pdcCampo)) {
            throw new ServidorControllerException("mensagem.erro.pergunta.dados.cadastrais.configuracao", responsavel);
        }

        final TransferObject servidor = buscaServidor(rseCodigo, responsavel);

        if (servidor == null) {
            throw new ServidorControllerException("mensagem.nenhumServidorEncontrado", responsavel);
        }

        final String[] campo = pdcCampo.split(";");
        String atributo = String.valueOf(servidor.getAttribute(campo[0]));

        if (campo.length == 3) {
            atributo = StringUtils.substring(atributo, Integer.parseInt(campo[1]), Integer.parseInt(campo[2]));
        } else if (campo.length == 2) {
            atributo = StringUtils.substring(atributo, Integer.parseInt(campo[1]));
        }

        // Se não existe resposta para a pergunta, comparação será realizada com vazio, retornando sempre resposta incorreta
        if (TextHelper.isNull(atributo)) {
            atributo = "";
        }

        return resposta.trim().equals(atributo.trim());

    }

    private TransferObject buscaServidor(String rseCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaServidorRseCodigoQuery query = new ListaServidorRseCodigoQuery(rseCodigo);
            final List<TransferObject> lista = query.executarDTO();
            TransferObject servidor = null;

            if ((lista != null) && !lista.isEmpty()) {
                servidor = lista.get(0);
            }

            return servidor;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * cadastrar/atualizar email do servidor e do usuário do servidor
     * @param protocoloCodigo
     * @param email
     * @param responsavel
     * @param usuarioSuporteResponsavel
     * @return
     * @throws ServidorControllerException
     */
    @Override
    public void cadastrarEmailServidor(String rseCodigo, String email, String protocoloCodigo, AcessoSistema responsavel, AcessoSistema usuarioSuporteResponsavel) throws ServidorControllerException {
        try {
            // atualiza email do servidor e cria ocorrência de alteração pelo totem
            final ServidorTransferObject servidor = findServidor(responsavel.getCodigoEntidade(), responsavel);
            servidor.setSerEmail(email);
            final String usuCodigo = responsavel.getUsuCodigo();

            // atualiza email do usuário do servidor e cria ocorrência de alteração pelo totem
            if (!TextHelper.isNull(usuCodigo)) {
                final UsuarioTransferObject usuarioSer = usuarioController.findUsuario(usuCodigo, responsavel);
                // ocorrência de usuário de servidor
                final OcorrenciaUsuarioTransferObject ocorrenciaUsu = new OcorrenciaUsuarioTransferObject();
                ocorrenciaUsu.setUsuCodigo(usuCodigo);
                ocorrenciaUsu.setTocCodigo(CodedValues.TOC_CADASTRO_EMAIL_SERVIDOR_TOTEM);
                ocorrenciaUsu.setOusUsuCodigo(usuCodigo);
                final String emailAnterior = usuarioSer.getUsuEmail() != null ? usuarioSer.getUsuEmail() : "";
                final String msgOus = ApplicationResourcesHelper.getMessage("mensagem.informacao.email.alterado.de.arg0.para.arg1", responsavel, emailAnterior, email);
                ocorrenciaUsu.setOusObs(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.alteracao.usuario", responsavel) + msgOus.toString());
                ocorrenciaUsu.setOusIpAcesso(responsavel.getIpUsuario());
                // atualiza email do usuário e cria ocorrência
                usuarioSer.setUsuEmail(email);
                usuarioController.updateUsuario(usuarioSer, ocorrenciaUsu, null, null, AcessoSistema.ENTIDADE_SER, usuCodigo, null, responsavel);
            }

            updateServidor(servidor, CodedValues.TOC_CADASTRO_EMAIL_SERVIDOR_TOTEM, true, responsavel);
            // Grava protocolo de senha de autorização
            // TODO criar tabela para protocolo de cadastro de email de servidor
            usuarioController.createProtocoloSenhaAutorizacao(protocoloCodigo, responsavel.getUsuCodigo(), usuarioSuporteResponsavel);

            // Gera log de protocolo de cadastro de email consumido
            // TODO criar log para protocolo de cadastro de email de servidor quando a tabela de protocolo de cadastro de email de servidor for criada
            final LogDelegate logDelegate = new LogDelegate(usuarioSuporteResponsavel, Log.PROTOCOLO_CADASTRO_EMAIL, Log.CREATE, Log.LOG_INFORMACAO);
            logDelegate.setProtocoloSenhaAutorizacao(protocoloCodigo);
            logDelegate.setRegistroServidor(rseCodigo);
            logDelegate.setUsuario(usuCodigo);
            // TODO criar status para protocolo de cadastro de email de servidor quando a tabela de protocolo de cadastro de email de servidor for criada
            final StatusProtocoloSenhaAutorizacaoEnum consumido = StatusProtocoloSenhaAutorizacaoEnum.CONSUMIDO;
            logDelegate.setStatusProtocoloSenhaAutorizacao(consumido.getCodigo());
            logDelegate.add(ApplicationResourcesHelper.getMessage("rotulo.protocolo.cadastro.email.servidor.singular", usuarioSuporteResponsavel) + ": \"" + protocoloCodigo + "\" " + consumido.getDescricao());
            logDelegate.write();

            // Cria ocorrência de usuário de autorização de senha
            final OcorrenciaUsuarioTransferObject ocorrencia = new OcorrenciaUsuarioTransferObject();
            ocorrencia.setUsuCodigo(usuCodigo);
            ocorrencia.setTocCodigo(CodedValues.TOC_AUT_CADASTRO_EMAIL_SERVIDOR_TOTEM);
            ocorrencia.setOusUsuCodigo(usuarioSuporteResponsavel.getUsuCodigo());
            ocorrencia.setOusObs(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.autorizacao.cadastro.email.servidor.host.a.host", usuarioSuporteResponsavel, protocoloCodigo.replace("_EMAIL", "")));
            ocorrencia.setOusIpAcesso(usuarioSuporteResponsavel.getIpUsuario());

            usuarioController.createOcorrenciaUsuario(ocorrencia, usuarioSuporteResponsavel);
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final UsuarioControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw ex;
        }
    }

    @Override
    public TransferObject recuperarDadosBanco(Short bcoCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final Banco bctInfo = BancoHome.findByPrimaryKey(bcoCodigo);
            final TransferObject retorno = new CustomTransferObject();

            retorno.setAttribute(Columns.BCO_CODIGO, bctInfo.getBcoCodigo());
            retorno.setAttribute(Columns.BCO_DESCRICAO, bctInfo.getBcoDescricao());
            retorno.setAttribute(Columns.BCO_IDENTIFICADOR, bctInfo.getBcoIdentificador());
            retorno.setAttribute(Columns.BCO_ATIVO, bctInfo.getBcoAtivo());

            return retorno;
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void aprovarCadastroServidor(RegistroServidorTO registroServidor, ServidorTransferObject servidor, boolean aprovar, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            if (aprovar) {
                // Em caso de aprovação, muda o status para ativo
                registroServidor.setSrsCodigo(CodedValues.SRS_ATIVO);

                if (servidor != null) {
                    updateServidorAndUpdateRegistroServidor(registroServidor, servidor, null, false, responsavel);
                } else {
                    updateRegistroServidorSemHistoricoMargem(registroServidor, responsavel);
                }

            } else {
                // Em caso de rejeição, muda status para excluído caso ainda esteja pendente
                if ((registroServidor.getSrsCodigo() == null) || CodedValues.SRS_PENDENTE.equals(registroServidor.getSrsCodigo())) {
                    registroServidor.setSrsCodigo(CodedValues.SRS_EXCLUIDO);
                }
                updateRegistroServidorSemHistoricoMargem(registroServidor, responsavel);

                // Cancela as consignações aguard. confirmação e deferimento caso o status seja excluído/falecido
                if (CodedValues.SRS_INATIVOS.contains(registroServidor.getSrsCodigo())) {
                    final List<TransferObject> lstAde = pesquisarConsignacaoController.pesquisaAutorizacao(registroServidor.getRseCodigo(), null, CodedValues.SAD_CODIGOS_AGUARD_CONF, responsavel);
                    if ((lstAde != null) && !lstAde.isEmpty()) {
                        final CustomTransferObject motivoOperacao = new CustomTransferObject();
                        motivoOperacao.setAttribute(Columns.TMO_CODIGO, registroServidor.getTipoMotivo());
                        motivoOperacao.setAttribute(Columns.OCA_OBS, registroServidor.getOrsObs());

                        for (final TransferObject ade : lstAde) {
                            final String adeCodigo = ade.getAttribute(Columns.ADE_CODIGO).toString();
                            motivoOperacao.setAttribute(Columns.ADE_CODIGO, adeCodigo);
                            cancelarConsignacaoController.cancelar(adeCodigo, motivoOperacao, responsavel);
                        }
                    }
                }
            }

            final LogDelegate log = new LogDelegate(responsavel, Log.REGISTRO_SERVIDOR, Log.UPDATE, Log.LOG_INFORMACAO);
            log.add(ApplicationResourcesHelper.getMessage(aprovar ? "mensagem.log.aprovacao.servidor" : "mensagem.log.rejeicao.servidor", responsavel));
            log.setRegistroServidor(registroServidor.getRseCodigo());
            log.setServidor(registroServidor.getSerCodigo());
            log.setOrgao(registroServidor.getOrgCodigo());
            log.write();

        } catch (final AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ServidorControllerException(ex);
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void updateServidorAndUpdateRegistroServidor(RegistroServidorTO registroServidor, ServidorTransferObject servidor, List<MargemTO> margens, Boolean aceiteTermoUso, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            String corpoEmailSer = "";
            if (servidor != null) {
                corpoEmailSer = updateServidor(servidor, false, responsavel);
            }

            String novoStatus = null;
            String corpoEmailRse = "";
            if (registroServidor != null) {
                final String statusAnterior = findRegistroServidor(registroServidor.getRseCodigo(), responsavel).getSrsCodigo();
                corpoEmailRse = updateRegistroServidor(registroServidor, margens, !responsavel.temPermissao(CodedValues.FUN_ALT_MARGEM_CONSIGNAVEL), true, false, true, false, false, responsavel);
                final String statusAtual = findRegistroServidor(registroServidor.getRseCodigo(), responsavel).getSrsCodigo();
                if (!statusAnterior.equals(statusAtual)) {
                    novoStatus = statusAtual;
                }
            }

            if (aceiteTermoUso) {
                // Cria ocorrencia 111
                criaOcorrenciaSER(servidor.getSerCodigo(), CodedValues.TOC_ACEITACAO_TERMO_DE_USO, ApplicationResourcesHelper.getMessage("rotulo.termo.de.uso.email.aceito.ocorrencia", responsavel), null, responsavel);
            }

            if (responsavel.isCseOrg() && (CodedValues.FUN_EDT_SERVIDOR.equals(responsavel.getFunCodigo()) || CodedValues.FUN_VALIDAR_SERVIDOR.equals(responsavel.getFunCodigo())) && ParamSist.paramEquals(CodedValues.TPC_ENVIA_EMAIL_NOTIFICACAO_EDT_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                if ((registroServidor != null) && (servidor != null)) {
                    EnviaEmailHelper.enviarEmailCsasAlteracaoSer(servidor, servidor.getSerCpf(), registroServidor.getRseMatricula(), novoStatus, corpoEmailSer + corpoEmailRse, responsavel);
                } else if (registroServidor != null) {
                    EnviaEmailHelper.enviarEmailCsasAlteracaoSer(registroServidor, null, registroServidor.getRseMatricula(), novoStatus, corpoEmailSer + corpoEmailRse, responsavel);
                } else {
                    EnviaEmailHelper.enviarEmailCsasAlteracaoSer(servidor, servidor.getSerCpf(), null, novoStatus, corpoEmailSer + corpoEmailRse, responsavel);
                }
            }

        } catch (final ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ServidorControllerException(ex);
        } catch (final ViewHelperException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    @Override
    public java.util.Date obtemDataInclusaoRegistroServidor(String rseCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final List<OcorrenciaRegistroSer> rsList = OcorrenciaRegistroServidorHome.findByRseTocCodigo(rseCodigo, CodedValues.TOC_RSE_INCLUSAO_POR_CARGA_MARGEM);
            if (!rsList.isEmpty() && (rsList.size() > 0)) {
                final OcorrenciaRegistroSer OcorrenciaRegistroSer = rsList.iterator().next();
                return OcorrenciaRegistroSer.getOrsData();
            }
        } catch (final FindException ex) {
            throw new ServidorControllerException(ex);
        }

        return null;
    }

    @Override
    public void cancelarCadastroServidor(AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final Object paramValue = ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_CSE_VALIDA_CAD_SERVIDOR, AcessoSistema.getAcessoUsuarioSistema());
            final int diasParaCancelamento = paramValue != null ? Integer.parseInt((String) paramValue) : 0;

            final TransferObject criterioSer = new CustomTransferObject();
            criterioSer.setAttribute("validaAdeSerPendente", Boolean.FALSE);

            if (diasParaCancelamento > 0) {
                final List<TransferObject> lstRegistroServidor = pesquisarServidorController.pesquisarServidorPendente(criterioSer, -1, -1, responsavel);

                if ((lstRegistroServidor != null) && (lstRegistroServidor.size() > 0)) {

                    for (final TransferObject registroServidor : lstRegistroServidor) {

                        final Date orsData = DateHelper.toSQLDate((java.util.Date) registroServidor.getAttribute(Columns.ORS_DATA));

                        //Exclui o cadastro do servidor que tiver excedido o número de
                        //dias para validação, cadastrado no parâmetro de sistema 572.
                        if (DateHelper.dayDiff(orsData) > diasParaCancelamento) {
                            final String rseCodigo = (String) registroServidor.getAttribute(Columns.RSE_CODIGO);

                            final RegistroServidor rseServidor = RegistroServidorHome.findByPrimaryKey(rseCodigo);

                            rseServidor.setStatusRegistroServidor(new StatusRegistroServidor(CodedValues.SRS_EXCLUIDO));
                            criaOcorrenciaRSE(rseCodigo, CodedValues.TOC_CANCELANCAMENTO_AUT_CAD_SERVIDOR, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ors.obs.cancelamento.automatico.cadastro.servidor", responsavel), null, responsavel);
                            AbstractEntityHome.update(rseServidor);

                            final LogDelegate log = new LogDelegate(responsavel, Log.REGISTRO_SERVIDOR, Log.UPDATE, Log.LOG_INFORMACAO);
                            log.add(ApplicationResourcesHelper.getMessage("mensagem.log.cancelamento.cadastro.servidor", responsavel));
                            log.setRegistroServidor(rseServidor.getRseCodigo());
                            log.setServidor(rseServidor.getServidor().getSerCodigo());
                            log.setOrgao(rseServidor.getOrgao().getOrgCodigo());
                            log.write();

                            // Cancela as autorizações que estiverem aguardando confirmação ou aguardando deferimento
                            final ObtemConsignacaoPorCnvSerQuery adesPorCnvSer = new ObtemConsignacaoPorCnvSerQuery();
                            adesPorCnvSer.rseCodigo = rseCodigo;
                            adesPorCnvSer.sadCodigos = CodedValues.SAD_CODIGOS_AGUARD_CONF;

                            final List<TransferObject> lstAde = adesPorCnvSer.executarDTO();
                            if ((lstAde != null) && !lstAde.isEmpty()) {
                                for (final TransferObject ade : lstAde) {
                                    final String adeCodigo = ade.getAttribute(Columns.ADE_CODIGO).toString();
                                    cancelarConsignacaoController.cancelar(adeCodigo, responsavel);
                                    EnviaEmailHelper.enviaEmailNotificacaoCsaCancelamentoCadastroServidor(ade, responsavel);
                                }
                            }

                        }

                    }

                }

            }

        } catch (final AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ServidorControllerException(ex);
        } catch (LogControllerException | FindException | UpdateException | HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void createRelRegistroServidor(String rseCodigoOrigem, String rseCodigoDestino, String tntCodigo, String usuCodigo, java.util.Date rreData, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            RelacionamentoRegistroServidorHome.createRelRegistroServidor(rseCodigoOrigem, rseCodigoDestino, tntCodigo, usuCodigo, rreData, responsavel);
        } catch (final CreateException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<EnderecoServidor> listEnderecoServidorByCodigo(String serCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            return EnderecoServidorHome.listEnderecoServidorByCodigo(serCodigo);
        } catch (final FindException e) {
            throw new ServidorControllerException("mensagem.nenhumEnderecoServidorEncontrado", (AcessoSistema) null);
        }
    }

    @Override
    public EnderecoServidor findEnderecoServidorByCodigo(String ensCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            return EnderecoServidorHome.findByPrimaryKey(ensCodigo);
        } catch (final FindException e) {
            throw new ServidorControllerException("mensagem.nenhumEnderecoServidorEncontrado", (AcessoSistema) null);
        }
    }

    @Override
    public List<TipoEndereco> listAllTipoEndereco(AcessoSistema responsavel) throws ServidorControllerException {
        try {
            return TipoEnderecoHome.listAll();
        } catch (final FindException e) {
            throw new ServidorControllerException("mensagem.nenhumEnderecoServidorEncontrado", (AcessoSistema) null);
        }
    }

    @Override
    public EnderecoServidor createEnderecoServidor(EnderecoServidor enderecoServidor, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final EnderecoServidor ens = EnderecoServidorHome.create(enderecoServidor.getServidor(), enderecoServidor.getTipoEndereco(), enderecoServidor.getEnsLogradouro(), enderecoServidor.getEnsNumero(), enderecoServidor.getEnsComplemento(), enderecoServidor.getEnsBairro(), enderecoServidor.getEnsMunicipio(), enderecoServidor.getEnsCodigoMunicipio(), enderecoServidor.getEnsUf(), enderecoServidor.getEnsCep(), enderecoServidor.getEnsAtivo());

            final TipoOcorrencia tipoOcorrencia = new TipoOcorrencia();
            tipoOcorrencia.setTocCodigo(CodedValues.TOC_INCLUSAO_ENDERECO_SERVIDOR);
            final Usuario usuario = new Usuario();
            usuario.setUsuCodigo(responsavel.getUsuCodigo());
            OcorrenciaEnderecoServidorHome.create(tipoOcorrencia, usuario, ens, null, null, ApplicationResourcesHelper.getMessage("mensagem.informacao.endereco.servidor.inclusao", responsavel), responsavel.getIpUsuario());

            final LogDelegate log = new LogDelegate(responsavel, Log.ENDERECO_SERVIDOR, Log.CREATE, Log.LOG_INFORMACAO);
            log.write();

            return ens;
        } catch (final CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ServidorControllerException(ex);
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void updateEnderecoServidor(EnderecoServidor enderecoServidor, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final EnderecoServidor enderecoAntigo = EnderecoServidorHome.findByPrimaryKey(enderecoServidor.getEnsCodigo());

            final TipoOcorrencia tipoOcorrencia = new TipoOcorrencia();
            tipoOcorrencia.setTocCodigo(CodedValues.TOC_ALTERACAO_ENDERECO_SERVIDOR);

            boolean teveAlteracao = false;

            // Validando se a cidade e o codigo ibge passado na tela são valido no banco de dados
            final List<TransferObject> cidades = sistemaController.lstCidadeCodigoIBGE(enderecoServidor.getEnsCodigoMunicipio(), responsavel);
            if ((cidades == null) || (cidades.size() == 0) || (cidades.size() > 1)) {
                throw new ServidorControllerException("mensagem.informe.servidor.cidade", responsavel);
            } else {
                final TransferObject cidade = cidades.get(0);
                final String cidNome = (String) cidade.getAttribute(Columns.CID_NOME);

                if (!cidNome.equals(enderecoServidor.getEnsMunicipio())) {
                    throw new ServidorControllerException("mensagem.informe.servidor.cidade", responsavel);
                }
            }

            final LogDelegate log = new LogDelegate(responsavel, Log.ENDERECO_SERVIDOR, Log.UPDATE, Log.LOG_INFORMACAO);
            if (!enderecoAntigo.getTipoEndereco().getTieCodigo().equals(enderecoServidor.getTipoEndereco().getTieCodigo())) {
                log.addChangedField(Columns.ENS_TIE_CODIGO, enderecoServidor.getTipoEndereco().getTieCodigo());
                teveAlteracao = true;
            }
            if (!enderecoAntigo.getEnsBairro().equals(enderecoServidor.getEnsBairro())) {
                log.addChangedField(Columns.ENS_BAIRRO, enderecoServidor.getEnsBairro());
                teveAlteracao = true;
            }
            if (!enderecoAntigo.getEnsCep().equals(enderecoServidor.getEnsCep())) {
                log.addChangedField(Columns.ENS_CEP, enderecoServidor.getEnsCep());
                teveAlteracao = true;
            }
            if (!enderecoAntigo.getEnsMunicipio().equals(enderecoServidor.getEnsMunicipio())) {
                log.addChangedField(Columns.ENS_MUNICIPIO, enderecoServidor.getEnsMunicipio());
                teveAlteracao = true;
            }
            if (!enderecoAntigo.getEnsComplemento().equals(enderecoServidor.getEnsComplemento())) {
                log.addChangedField(Columns.ENS_COMPLEMENTO, enderecoServidor.getEnsComplemento());
                teveAlteracao = true;
            }
            if (!enderecoAntigo.getEnsUf().equals(enderecoServidor.getEnsUf())) {
                log.addChangedField(Columns.ENS_UF, enderecoServidor.getEnsUf());
                teveAlteracao = true;
            }
            if (!enderecoAntigo.getEnsNumero().equals(enderecoServidor.getEnsNumero())) {
                log.addChangedField(Columns.ENS_NUMERO, enderecoServidor.getEnsNumero());
                teveAlteracao = true;
            }
            if (!enderecoAntigo.getEnsLogradouro().equals(enderecoServidor.getEnsLogradouro())) {
                log.addChangedField(Columns.ENS_LOGRADOURO, enderecoServidor.getEnsLogradouro());
                teveAlteracao = true;
            }
            if ((enderecoAntigo.getEnsCodigoMunicipio() == null) || !enderecoAntigo.getEnsCodigoMunicipio().equals(enderecoServidor.getEnsCodigoMunicipio())) {
                log.addChangedField(Columns.ENS_CODIGO_MUNICIPIO, enderecoServidor.getEnsCodigoMunicipio());
                teveAlteracao = true;
            }
            if (((enderecoAntigo.getEnsCodigoMunicipio() == null) && (enderecoServidor.getEnsCodigoMunicipio() != null)) || ((enderecoAntigo.getEnsCodigoMunicipio() != null) && (enderecoServidor.getEnsCodigoMunicipio() == null)) || ((enderecoAntigo.getEnsCodigoMunicipio() != null) && (enderecoServidor.getEnsCodigoMunicipio() != null) && (enderecoServidor.getEnsCodigoMunicipio().compareTo(enderecoAntigo.getEnsCodigoMunicipio()) != 0))) {
                log.addChangedField(Columns.ENS_CODIGO_MUNICIPIO, enderecoServidor.getEnsCodigoMunicipio());
                teveAlteracao = true;
            }

            if (teveAlteracao) {
                final Usuario usuario = new Usuario();
                usuario.setUsuCodigo(responsavel.getUsuCodigo());
                OcorrenciaEnderecoServidorHome.create(tipoOcorrencia, usuario, enderecoServidor, null, null, ApplicationResourcesHelper.getMessage("mensagem.informacao.endereco.servidor.alteracao", responsavel), responsavel.getIpUsuario());

                AbstractEntityHome.update(enderecoServidor);
                log.write();
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void removeEnderecoServidor(EnderecoServidor enderecoServidor, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            enderecoServidor.setEnsAtivo(CodedValues.STS_INATIVO);

            AbstractEntityHome.update(enderecoServidor);

            final TipoOcorrencia tipoOcorrencia = new TipoOcorrencia();
            tipoOcorrencia.setTocCodigo(CodedValues.TOC_EXCLUSAO_ENDERECO_SERVIDOR);
            final Usuario usuario = new Usuario();
            usuario.setUsuCodigo(responsavel.getUsuCodigo());
            OcorrenciaEnderecoServidorHome.create(tipoOcorrencia, usuario, enderecoServidor, null, null, ApplicationResourcesHelper.getMessage("mensagem.informacao.endereco.servidor.exclusao", responsavel), responsavel.getIpUsuario());

            final LogDelegate log = new LogDelegate(responsavel, Log.ENDERECO_SERVIDOR, Log.DELETE, Log.LOG_INFORMACAO);
            log.write();
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> listarMargensRse(String rseCodigo, String svcCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        final List<TransferObject> margens = null;

        if (TextHelper.isNull(rseCodigo)) {
            return margens;
        }

        final ListaMargemRegistroServidorQuery lstMargem = new ListaMargemRegistroServidorQuery();
        lstMargem.rseCodigo = rseCodigo;
        lstMargem.svcCodigo = svcCodigo;

        try {
            return lstMargem.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Lista servidores que possuem consignação em qualquer situação caso seja informada consignatária.
     * @param serCpf
     * @param csaCodigo Caso seja informado, verifica se o servidor possui contratos em qualquer situação com a consignatária informada
     * @param responsavel
     * @return
     * @throws ServidorControllerException
     */
    @Override
    public List<TransferObject> listarServidorPossuiAde(String serCpf, AcessoSistema responsavel) throws ServidorControllerException {
        try {

            /**
             * Verifica parâmetro de sistema para informar se a CSA pode carregar boleto para qualquer servidor ou apenas para aqueles que esta possui consignação de qualquer status,
             * sendo o padrão permitir apenas para servidores com consignação.
             */
            final boolean csaRealizaUploadSerSemAde = ParamSist.getBoolParamSist(CodedValues.TPC_CSA_UPLOAD_BOLETO_SER_NAO_POSSUI_ADE, responsavel);

            final ListaServidorPossuiAdeQuery query = new ListaServidorPossuiAdeQuery();
            query.serCpf = serCpf;

            if (!csaRealizaUploadSerSemAde) {
                query.csaCodigo = responsavel.getCsaCodigo();
            }

            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void cadastrarDispensaDigitalServidor(TransferObject criterio, TransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final String serCodigo = criterio.getAttribute(Columns.SER_CODIGO).toString();
            final String serDispensaDigital = criterio.getAttribute(Columns.SER_DISPENSA_DIGITAL).toString();
            final StringBuilder msgOcs = new StringBuilder();

            criterio.setAttribute(Columns.ARQ_TAR_CODIGO, TipoArquivoEnum.ARQUIVO_DISPENSA_VALIDACAO_DIGITAL_SER.getCodigo());

            String tmoCodigo = null;
            String ocsObs = "";
            if (tipoMotivoOperacao != null) {
                tmoCodigo = tipoMotivoOperacao.getAttribute(Columns.TMO_CODIGO).toString();
                ocsObs = tipoMotivoOperacao.getAttribute(Columns.OCS_OBS).toString();
            }

            // Alterar o campo criado SER_DISPENSA_DIGITAL
            final Servidor servidor = ServidorHome.findByPrimaryKey(serCodigo);
            String tocCodigo = CodedValues.TOC_CAD_DISPENSA_VALIDACAO_DIGITAL_SER;
            if (!TextHelper.isNull(serDispensaDigital) && CodedValues.TPC_SIM.equals(serDispensaDigital)) {
                servidor.setSerDispensaDigital(CodedValues.TPC_SIM);
                msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.cadastrar.dispensa.validacao.digital", responsavel, ocsObs));
            } else {
                servidor.setSerDispensaDigital(CodedValues.TPC_NAO);
                msgOcs.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.cadastrar.validacao.digital", responsavel, ocsObs));
                tocCodigo = CodedValues.TOC_REV_DISPENSA_VALIDACAO_DIGITAL_SER;
            }
            AbstractEntityHome.update(servidor);

            // Registrar ocorrência criada no item 5
            criaOcorrenciaSER(serCodigo, tocCodigo, msgOcs.toString(), tmoCodigo, responsavel);

            // gravar os anexos e registrá-los na tabela do item 8.
            if (!TextHelper.isNull(criterio.getAttribute(Columns.ARQ_CONTEUDO))) {
                arquivoController.createArquivoServidor(criterio, responsavel);
            }

            final LogDelegate log = new LogDelegate(responsavel, Log.DISPENSA_VALIDACAO_DIGITAL, Log.UPDATE, Log.LOG_INFORMACAO);
            log.add(msgOcs.toString());
            log.setServidor(serCodigo);
            log.write();

        } catch (final FindException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.nenhumServidorEncontrado", responsavel);
        } catch (UpdateException | LogControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final ArquivoControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException(ex);
        }
    }

    @Override
    public List<Servidor> findByCpf(String serCpf, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            return ServidorHome.findByCPF(serCpf);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.nenhumServidorEncontrado", responsavel);
        }
    }

    @Override
    public boolean validaServidorDentroPrazoAcessoSistemaSemValidacaoEmail(String serCodigo, java.util.Date dataOcorrencia, AcessoSistema responsavel) throws ServidorControllerException {

        final int qtdeDiasQueUsuarioPodeUsarSistemaSemValidarEmail = TextHelper.isNum(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_ACESSO_SISTEMA_SERVIDOR_SEM_VALIDACAO_EMAIL, responsavel)) ? Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_ACESSO_SISTEMA_SERVIDOR_SEM_VALIDACAO_EMAIL, responsavel).toString()) : 0;

        if (qtdeDiasQueUsuarioPodeUsarSistemaSemValidarEmail > 0) {

            Servidor servidor = null;
            try {
                servidor = ServidorHome.findByPrimaryKey(serCodigo);
            } catch (final FindException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
            final int resultadoEmDiasEntreDataAtualMenosDataOcorrenciaEmailIncorreto = DateHelper.dayDiff(dataOcorrencia);

            //Se o usuário servidor já possui uma ocorrencia de e-mail incorreto precisamos validar a data da ocorrencia do parâmetro 705 e a data atual para saber se o mesmo pode ter acesso ao sistema.
            //Caso o usuario servidor já realizou a atualização do e-mail, o acesso ao sistema está liberado.
            if ((servidor != null) && TextHelper.isNull(servidor.getSerDataValidacaoEmail()) && (qtdeDiasQueUsuarioPodeUsarSistemaSemValidarEmail < resultadoEmDiasEntreDataAtualMenosDataOcorrenciaEmailIncorreto)) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public void updateMargensRegistroServidor(String rseCodigo, List<MargemTO> margens, String compulsorio, String tmoCodigo, String obsMotivoOperacao, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final LogDelegate logMargem = new LogDelegate(responsavel, Log.REGISTRO_SERVIDOR, Log.UPDATE, Log.LOG_INFORMACAO);
            logMargem.setRegistroServidor(rseCodigo);

            final StringBuilder margemAlterada = new StringBuilder();

            // Busca o registro servidor passado por parâmetro
            final RegistroServidor rseBean = RegistroServidorHome.findByPrimaryKeyForUpdate(rseCodigo);
            boolean salvarRSE = false;
            boolean liberouMargem = false;

            for (final MargemTO margemTO : margens) {
                final Short marCodigo = margemTO.getMarCodigo();
                MargemRegistroServidor mrsBean = null;
                BigDecimal margemAntigaFolha = null;
                BigDecimal margemAntigaUsada = null;
                BigDecimal margemAntigaRestante = null;
                BigDecimal margemNovaFolha = null;

                if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM)) {
                    margemAntigaFolha = rseBean.getRseMargem();
                    margemAntigaUsada = rseBean.getRseMargemUsada() != null ? rseBean.getRseMargemUsada() : new BigDecimal("0.00");
                    margemAntigaRestante = rseBean.getRseMargemRest() != null ? rseBean.getRseMargemRest() : new BigDecimal("0.00");
                    margemNovaFolha = margemTO.getMrsMargem() != null ? margemTO.getMrsMargem() : new BigDecimal("0.00");

                } else if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
                    margemAntigaFolha = rseBean.getRseMargem2();
                    margemAntigaUsada = rseBean.getRseMargemUsada2() != null ? rseBean.getRseMargemUsada2() : new BigDecimal("0.00");
                    margemAntigaRestante = rseBean.getRseMargemRest2() != null ? rseBean.getRseMargemRest2() : new BigDecimal("0.00");
                    margemNovaFolha = margemTO.getMrsMargem() != null ? margemTO.getMrsMargem() : new BigDecimal("0.00");

                } else if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
                    margemAntigaFolha = rseBean.getRseMargem3();
                    margemAntigaUsada = rseBean.getRseMargemUsada3() != null ? rseBean.getRseMargemUsada3() : new BigDecimal("0.00");
                    margemAntigaRestante = rseBean.getRseMargemRest3() != null ? rseBean.getRseMargemRest3() : new BigDecimal("0.00");
                    margemNovaFolha = margemTO.getMrsMargem() != null ? margemTO.getMrsMargem() : new BigDecimal("0.00");

                } else {
                    final MargemRegistroServidorId mrsPK = new MargemRegistroServidorId(marCodigo, rseCodigo);
                    mrsBean = MargemRegistroServidorHome.findByPrimaryKeyForUpdate(mrsPK);

                    margemAntigaFolha = mrsBean.getMrsMargem();
                    margemAntigaUsada = mrsBean.getMrsMargemUsada() != null ? mrsBean.getMrsMargemUsada() : new BigDecimal("0.00");
                    margemAntigaRestante = mrsBean.getMrsMargemRest() != null ? mrsBean.getMrsMargemRest() : new BigDecimal("0.00");
                    margemNovaFolha = margemTO.getMrsMargem() != null ? margemTO.getMrsMargem() : new BigDecimal("0.00");
                }

                // Se nada mudou, pula para a próxima margem
                if (margemAntigaFolha.compareTo(margemNovaFolha) == 0) {
                    continue;
                }

                // A margem restante do servidor será o valor da margem informada subtraída do valor da margem usada atual
                final BigDecimal margemRestNova = margemNovaFolha.subtract(margemAntigaUsada);

                // Adiciona a alteração ao log
                logMargem.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.atributo.margem.arg0.folha.alterado.arg1.para.arg2", responsavel, marCodigo.toString(), logMargem.formatObject(margemAntigaFolha), logMargem.formatObject(margemNovaFolha)));
                // Gera observação para gravação da ocorrência
                margemAlterada.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0.alterada.de.arg1.para.arg2", responsavel, MargemHelper.getInstance().getMarDescricao(marCodigo, responsavel).toUpperCase(), margemAntigaFolha.toString(), margemNovaFolha.toString()));

                if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM)) {
                    rseBean.setRseMargem(margemNovaFolha);
                    rseBean.setRseMargemRest(margemRestNova);
                    salvarRSE = true;

                } else if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
                    rseBean.setRseMargem2(margemNovaFolha);
                    rseBean.setRseMargemRest2(margemRestNova);
                    salvarRSE = true;

                } else if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
                    rseBean.setRseMargem3(margemNovaFolha);
                    rseBean.setRseMargemRest3(margemRestNova);
                    salvarRSE = true;

                } else {
                    mrsBean.setMrsMargem(margemNovaFolha);
                    mrsBean.setMrsMargemRest(margemRestNova);
                    AbstractEntityHome.update(mrsBean);
                }

                liberouMargem |= margemRestNova.compareTo(margemAntigaRestante) > 0;

                // Grava histórico da alteração da margem
                HistoricoMargemRegistroServidorHome.create(rseCodigo, marCodigo, null, OperacaoHistoricoMargemEnum.EDT_REGISTRO_SERVIDOR.getCodigo(), margemAntigaRestante, margemRestNova);
            }

            if (salvarRSE) {
                // Atualiza as margens do servidor
                AbstractEntityHome.update(rseBean);
            }

            if (liberouMargem) {
                // Se é uma operação de liberação de margem, então registra esta operação no controle de segurança
                segurancaController.registrarOperacoesLiberacaoMargem(rseCodigo, null, responsavel);
            }

            if (margemAlterada.length() > 0) {
                // Cria ocorrência de servidor indicando alteração de margem
                criaOcorrenciaRSE(rseCodigo, CodedValues.TOC_RSE_ALTERACAO_MARGEM, ApplicationResourcesHelper.getMessage("mensagem.informacao.margem.servidor.foi.alterada", responsavel, margemAlterada.toString()) + " " + compulsorio + obsMotivoOperacao, tmoCodigo, responsavel);

                logMargem.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.atributo.valor.compulsorio.arg",responsavel, compulsorio));

                // Salva as alterações no log
                logMargem.write();
            }

        } catch (FindException | UpdateException | CreateException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final ZetraException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> lstDadosServidor(AcaoTipoDadoAdicionalEnum acao, VisibilidadeTipoDadoAdicionalEnum visibilidade, String serCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaDadosServidorQuery query = new ListaDadosServidorQuery();
            query.responsavel = responsavel;
            query.acao = acao;
            query.visibilidade = visibilidade;
            query.serCodigo = serCodigo;

            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new ServidorControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> lstTipoDadoAdicionalServidorQuery(AcaoTipoDadoAdicionalEnum acao, VisibilidadeTipoDadoAdicionalEnum visibilidade, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaTipoDadoAdicionalServidorQuery query = new ListaTipoDadoAdicionalServidorQuery();
            query.responsavel = responsavel;
            query.acao = acao;
            query.visibilidade = visibilidade;

            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new ServidorControllerException(ex);
        }
    }

    @Override
    public String getValorDadoServidor(String serCodigo, String tdaCodigo) throws ServidorControllerException {
        try {
            final DadosServidor das = DadosServidorHome.findByPrimaryKey(new DadosServidorId(serCodigo, tdaCodigo));
            return das.getDasValor();
        } catch (final FindException ex) {
            return null;
        }
    }

    @Override
    public void setValorDadoServidor(String serCodigo, String tdaCodigo, String dasValor, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            String operacao;
            DadosServidor das = null;
            String vlrAnterior = null;
            try {
                // Verifica se o dado já existe na tabela
                das = DadosServidorHome.findByPrimaryKey(new DadosServidorId(serCodigo, tdaCodigo));
                vlrAnterior = das.getDasValor();

                // Se existe, verifica se deve ser atualizado ou removido
                if (!TextHelper.isNull(dasValor)) {
                    operacao = Log.UPDATE;
                    das.setDasValor(dasValor);
                    AbstractEntityHome.update(das);
                } else {
                    operacao = Log.DELETE;
                    AbstractEntityHome.remove(das);
                }
            } catch (final FindException ex) {
                if (dasValor != null) {
                    // Senão, cria o novo dado para o servidor
                    operacao = Log.CREATE;
                    DadosServidorHome.create(serCodigo, tdaCodigo, dasValor);
                } else {
                    // Se não existe, mas o valor não foi passado, não realiza operação
                    operacao = null;
                }
            }

            if (operacao != null) {
                // Grava log da operação
                final LogDelegate log = new LogDelegate(responsavel, Log.DADOS_SERVIDOR, operacao, Log.LOG_INFORMACAO);
                log.setServidor(serCodigo);
                log.setTipoDadoAdicional(tdaCodigo);
                log.addChangedField(Columns.DAS_VALOR, dasValor);
                log.write();

                // Grava ocorrencia da operação
                String tocCodigo;
                String obs;
                if (Log.CREATE.equals(operacao)) {
                    tocCodigo = CodedValues.TOC_CRIACAO_DADOS_ADICIONAIS;
                    obs = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.criacao.dados.servidor", responsavel, tdaCodigo, dasValor);
                } else if (Log.UPDATE.equals(operacao)) {
                    tocCodigo = CodedValues.TOC_ALTERACAO_DADOS_ADICIONAIS;
                    obs = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.alteracao.dados.servidor", responsavel, tdaCodigo, vlrAnterior, dasValor);
                } else {
                    tocCodigo = CodedValues.TOC_EXCLUSAO_DADOS_ADICIONAIS;
                    obs = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.exclusao.dados.servidor", responsavel, tdaCodigo, vlrAnterior);
                }
                OcorrenciaDadosServidorHome.create(serCodigo, tocCodigo, responsavel.getUsuCodigo(), tdaCodigo, null, obs, vlrAnterior, dasValor, responsavel.getIpUsuario());
            }
        } catch (UpdateException | RemoveException | CreateException | LogControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstDataOcorrenciaServidorDescontoParcial(String serCodigo, String tocCodigo, boolean ordenar, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ObtemDataOcorrenciaServidorQuery query = new ObtemDataOcorrenciaServidorQuery();
            query.serCodigo = serCodigo;
            query.tocCodigo = tocCodigo;
            query.ordenar = ordenar;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erro.listar.ocorrencias", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstServidoresArquivamento(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaSerParaArquivamentoQuery query = new ListaSerParaArquivamentoQuery();
            query.orgCodigos = orgCodigos;
            query.estCodigos = estCodigos;
            query.responsavel = responsavel;

            return query.executarDTO();

        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void arquivarServidor(String serCodigo, String rseCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            // Remove dependências do registro servidor
            AnaliseRiscoRegistroServidorHome.removeByRse(rseCodigo);
            BaseCalcRegistroServidorHome.removeByRse(rseCodigo);
            BlocoProcessamentoHome.removeByRse(rseCodigo);
            BloqueioRseFunHome.removeByRse(rseCodigo);
            CompMargemHome.removeByRse(rseCodigo);
            ComunicacaoHome.removeBySerRse(serCodigo, rseCodigo);
            ContrachequeRegistroServidorHome.removeByRse(rseCodigo);
            HistoricoConsultaMargemHome.removeByRse(rseCodigo);
            HistoricoMargemFolhaHome.removeByRse(rseCodigo);
            HistoricoMargemPeriodoHome.removeByRse(rseCodigo);
            HistoricoMargemRegistroServidorHome.removeByRse(rseCodigo);
            MargemRegistroServidorHome.removeByRse(rseCodigo);
            OcorrenciaRegistroServidorHome.removeByRse(rseCodigo);
            ParamConvenioRegistroServidorHome.removeByRse(rseCodigo);
            ParamNseRegistroServidorHome.removeByRse(rseCodigo);
            ParamServicoRegistroServidorHome.removeByRse(rseCodigo);
            OcorrenciaPermissionarioHome.removeByRse(rseCodigo);
            PermissionarioHome.removeByRse(rseCodigo);
            ReclamacaoMotivoHome.removeByRse(rseCodigo);
            ReclamacaoRegistroServidorHome.removeByRse(rseCodigo);
            RelacionamentoRegistroServidorHome.removeByRse(rseCodigo);
            VerbaRescisoriaRseHome.removeByRse(rseCodigo);
            ParamConsignatariaRegistroServidorHome.removeByRse(rseCodigo);
            ConsultaMargemSemSenhaHome.removeByRse(rseCodigo);
            RegistroServidorOcultoCsaHome.removeByRse(rseCodigo);
            PontuacaoRseCsaHome.removeByRse(rseCodigo);

            // Remove beneficiário e dependências
            final List<Beneficiario> beneficiarios = BeneficiarioHome.findByServidor(serCodigo);
            if ((beneficiarios != null) && !beneficiarios.isEmpty()) {
                for (final Beneficiario beneficiario : beneficiarios) {
                    AnexoBeneficiarioHome.removeByBeneficiario(beneficiario.getBfcCodigo());
                    ContratoBeneficioHome.removeByBeneficiario(beneficiario.getBfcCodigo());
                    OcorrenciaBeneficiarioHome.removeByBeneficiario(beneficiario.getBfcCodigo());
                    AbstractEntityHome.remove(beneficiario);
                }
            }

            // Remove dependências do servidor
            ArquivoHome.removeBySer(serCodigo);
            BoletoServidorHome.removeBySer(serCodigo);
            DadosServidorHome.removeBySer(serCodigo);
            DirfServidorHome.removeBySer(serCodigo);
            EnderecoServidorHome.removeBySer(serCodigo);
            OcorrenciaDadosServidorHome.removeBySer(serCodigo);
            OcorrenciaServidorHome.removeBySer(serCodigo);

            // Remove dependências do usuario
            final Usuario usuario = UsuarioHome.findByServidor(rseCodigo);

            if (usuario != null) {
                // Remove ligação do usuário com o servidor
                UsuarioSerHome.removeByUsu(usuario.getUsuCodigo());

                // Exclusão lógica do usuário
                // Altera o login do usuário para o seu código de modo que novos usuários possam usar o login deste usuário
                usuario.setUsuTipoBloq(usuario.getUsuLogin());
                usuario.setUsuLogin(usuario.getUsuCodigo());

                // Altera o status do usuário
                usuario.setStatusLogin(StatusLoginHome.findByPrimaryKey(CodedValues.STU_EXCLUIDO));
                AbstractEntityHome.update(usuario);
            }

            // Verifica se existe somente um registro servidor associado
            final ObtemTotalRegistroServidorQuery query = new ObtemTotalRegistroServidorQuery(serCodigo);
            final boolean possuiSomenteUmRse = query.executarContador() == 1;

            final RegistroServidor rse = RegistroServidorHome.findByPrimaryKey(rseCodigo);
            final Servidor ser = ServidorHome.findByPrimaryKey(serCodigo);

            // Verifica se existe o histórico do servidor, se existir altera, se não cria um novo histórico do servidor
            try {
                HtServidorHome.findByPrimaryKey(serCodigo);
                AbstractEntityHome.update(new HtServidor(ser));
            } catch (final FindException e) {
                HtServidorHome.create(ser);
            }

            // Cria histórico do registro servidor
            HtRegistroServidorHome.create(rse);

            // Remove registro servidor
            AbstractEntityHome.remove(rse);

            // Remove o servidor se houver somente um registro servidor associado
            if (possuiSomenteUmRse) {
                UsuarioSerHome.removeBySer(serCodigo);
                AbstractEntityHome.remove(ser);
            }

            // Grava log da operação
            final LogDelegate log = new LogDelegate(responsavel, Log.REGISTRO_SERVIDOR, Log.DELETE, Log.LOG_INFORMACAO);
            log.setServidor(serCodigo);
            log.setRegistroServidor(rseCodigo);
            log.add(ApplicationResourcesHelper.getMessage("mensagem.info.arquivamento.servidor", responsavel));
            log.write();

        } catch (CreateException | FindException | UpdateException | RemoveException | LogControllerException | HQueryException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erro.arquivamento.registro.servidor", responsavel, ex, rseCodigo);
        }
    }

    @Override
    public List<TransferObject> lstHistoricoServidor(String rseMatricula, String serCpf, String orgCodigo, String estCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListarHistoricoServidorQuery query = new ListarHistoricoServidorQuery();
            query.rseMatricula = rseMatricula;
            query.serCpf = serCpf;
            query.orgCodigo = orgCodigo;
            query.estCodigo = estCodigo;

            return query.executarDTO();

        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void bloquearRegistroServidorPorMotivoSeguranca(String rseCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            // Altera o status para bloqueado automaticamente por segurança (SRS_CODIGO = 7)
            RegistroServidorHome.bloquearRegistroServidorPorSeguranca(rseCodigo);

            // Cria ocorrência de bloqueio automático de segurança (TOC_CODIGO = 191).
            final String tocCodigo = CodedValues.TOC_BLOQUEIO_AUTOMATICO_SEGURANCA;
            final String orsObs = ApplicationResourcesHelper.getMessage("mensagem.registro.servidor.bloqueado.automaticamente.seguranca", responsavel);
            OcorrenciaRegistroServidorHome.create(rseCodigo, tocCodigo, responsavel.getUsuCodigo(), orsObs, responsavel.getIpUsuario(), null);

            // Grava log da operação
            final RegistroServidor rseServidor = RegistroServidorHome.findByPrimaryKey(rseCodigo);
            final LogDelegate log = new LogDelegate(responsavel, Log.REGISTRO_SERVIDOR, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setRegistroServidor(rseServidor.getRseCodigo());
            log.setServidor(rseServidor.getServidor().getSerCodigo());
            log.setOrgao(rseServidor.getOrgao().getOrgCodigo());
            log.setStatusRseCodigo(CodedValues.SRS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA);
            log.add(ApplicationResourcesHelper.getMessage("mensagem.registro.servidor.bloqueado.automaticamente.seguranca", responsavel));
            log.write();

        } catch (UpdateException | CreateException | FindException | LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstHistoricoVariacaoMargemBruta(String rseCodigo, Short marCodigo, int qtdeMesesPesquisa, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaHistoricoVariacaoMargemBrutaQuery query = new ListaHistoricoVariacaoMargemBrutaQuery(qtdeMesesPesquisa);
            query.rseCodigo = rseCodigo;
            query.marCodigo = marCodigo;

            final LogDelegate log = new LogDelegate(responsavel, Log.HISTORICO_MARGEM, Log.SELECT, Log.LOG_INFORMACAO);
            log.setRegistroServidor(rseCodigo);
            log.setMargem(marCodigo.toString());
            log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.parametro.pesquisa.quantidade.meses", responsavel, String.valueOf(qtdeMesesPesquisa)));
            log.write();

            return query.executarDTO();
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void gravarDadoServidor(String serCodigo, String tdaCodigo, String dasValor, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            String operacao;
            DadosServidor das = null;
            String vlrAnterior = null;
            try {
                // Verifica se o dado já existe na tabela
                das = DadosServidorHome.findByPrimaryKey(new DadosServidorId(serCodigo, tdaCodigo));
                vlrAnterior = das.getDasValor();

                // Se existe, verifica se deve ser atualizado ou removido
                if (!TextHelper.isNull(dasValor)) {
                    operacao = Log.UPDATE;
                    das.setDasValor(dasValor);
                    AbstractEntityHome.update(das);
                } else {
                    operacao = Log.DELETE;
                    AbstractEntityHome.remove(das);
                }
            } catch (final FindException ex) {
                if (dasValor != null) {
                    // Se não, cria o novo dado para a autorização de desconto
                    operacao = Log.CREATE;
                    DadosServidorHome.create(serCodigo, tdaCodigo, dasValor);
                } else {
                    // Se não existe, mas o valor não foi passado, não realiza operação
                    operacao = null;
                }
            }

            if (operacao != null) {
                // Grava log da operação
                final LogDelegate log = new LogDelegate(responsavel, Log.DADOS_SERVIDOR, operacao, Log.LOG_INFORMACAO);
                log.setServidor(serCodigo);
                log.setTipoDadoAdicional(tdaCodigo);
                log.addChangedField(Columns.DAS_VALOR, dasValor);
                log.write();

                // Grava ocorrencia da operação
                String tocCodigo;
                String obs;
                if (Log.CREATE.equals(operacao)) {
                    tocCodigo = CodedValues.TOC_CRIACAO_DADOS_ADICIONAIS;
                    obs = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.criacao.dados.servidor", responsavel, tdaCodigo, dasValor);
                } else if (Log.UPDATE.equals(operacao)) {
                    tocCodigo = CodedValues.TOC_ALTERACAO_DADOS_ADICIONAIS;
                    obs = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.alteracao.dados.servidor", responsavel, tdaCodigo, vlrAnterior, dasValor);
                } else {
                    tocCodigo = CodedValues.TOC_EXCLUSAO_DADOS_ADICIONAIS;
                    obs = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.exclusao.dados.servidor", responsavel, tdaCodigo, vlrAnterior);
                }
                OcorrenciaDadosServidorHome.create(serCodigo, tocCodigo, responsavel.getUsuCodigo(), tdaCodigo, null, obs, vlrAnterior, dasValor, responsavel.getIpUsuario());
            }
        } catch (UpdateException | RemoveException | CreateException | LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void notificaServidorContratosPendentesReativacao(AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final List<TransferObject> servidoresContratosPendentesReativacao = pesquisarServidorController.listarServidorConsignacaoPendenteReativacao(responsavel);
            String serCodigo = "";
            for (final TransferObject servidor : servidoresContratosPendentesReativacao) {
                final String serCodigoAtual = (String) servidor.getAttribute(Columns.SER_CODIGO);
                final String serEmail = (String) servidor.getAttribute(Columns.SER_EMAIL);
                final String serNome = (String) servidor.getAttribute(Columns.SER_NOME);
                final Short marCodigo = (Short) servidor.getAttribute(Columns.ADE_INC_MARGEM);
                final MargemTO margemTO = MargemHelper.getInstance().getMargem(marCodigo, responsavel);
                if(margemTO.temMargemDisponivel() && !serCodigo.equals(serCodigoAtual) && !TextHelper.isNull(serEmail)) {
                    EnviaEmailHelper.enviarEmailServidorContratosPendentesReativacao(serNome, serEmail, responsavel);
                    serCodigo = serCodigoAtual;
                }
                if(TextHelper.isNull(serEmail)) {
                    LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.erro.email.servidor.sem.email", responsavel, serCodigoAtual));
                }
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> listarComposicaoMargem(TransferObject composicaoMargem, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final String rseCodigo = (String) composicaoMargem.getAttribute(Columns.RSE_CODIGO);
            final ListaComposicaoMargemRseQuery query = new ListaComposicaoMargemRseQuery();
            query.rseCodigo = rseCodigo;

            return query.executarDTO();

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public TransferObject findComposicaoMargem(String cmaCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final CompMargem composicaoMargem = CompMargemHome.findByPrimaryKey(cmaCodigo);

            final TransferObject composicao = new CustomTransferObject();
            composicao.setAttribute(Columns.CMA_CODIGO, composicaoMargem.getCmaCodigo());
            composicao.setAttribute(Columns.CMA_RSE_CODIGO, composicaoMargem.getRseCodigo());
            composicao.setAttribute(Columns.CMA_VCT_CODIGO, composicaoMargem.getVctCodigo());
            composicao.setAttribute(Columns.CMA_VLR, composicaoMargem.getCmaVlr());
            composicao.setAttribute(Columns.CMA_VINCULO, composicaoMargem.getCmaVinculo());
            composicao.setAttribute(Columns.CMA_QUANTIDADE, composicaoMargem.getCmaQuantidade());
            composicao.setAttribute(Columns.CMA_VRS_CODIGO, composicaoMargem.getVrsCodigo());
            composicao.setAttribute(Columns.CMA_CRS_CODIGO, composicaoMargem.getCrsCodigo());

            return composicao;

        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void editarComposicaoMargem(TransferObject composicaoMargem, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final String cmaCodigo = (String) composicaoMargem.getAttribute(Columns.CMA_CODIGO);
            final String rseCodigo = (String) composicaoMargem.getAttribute(Columns.CMA_RSE_CODIGO);
            final String vctCodigo = (String) composicaoMargem.getAttribute(Columns.CMA_VCT_CODIGO);
            final String vrsCodigo = (String) composicaoMargem.getAttribute(Columns.CMA_VRS_CODIGO);
            final String crsCodigo = (String) composicaoMargem.getAttribute(Columns.CMA_CRS_CODIGO);
            final BigDecimal cmaValor = (BigDecimal) composicaoMargem.getAttribute(Columns.CMA_VLR);
            final String cmaVinculo = (String) composicaoMargem.getAttribute(Columns.CMA_VINCULO);
            final Integer cmaQuantidade = (Integer) composicaoMargem.getAttribute(Columns.CMA_QUANTIDADE);

            if (VencimentoEnum.PROVENTO_BASE.getCodigo().equals(vctCodigo) && (cmaValor.compareTo(BigDecimal.ZERO) < 0)) {
                throw new ServidorControllerException("mensagem.erro.editar.composicao.margem.valor.deve.positivo", responsavel);
            }

            // Verifica se já existe uma composição de margem do tipo
            final List<String> vctCodigos = new ArrayList<>();
            vctCodigos.add(VencimentoEnum.PROVENTO_BASE.getCodigo());
            vctCodigos.add(VencimentoEnum.IRPF.getCodigo());
            vctCodigos.add(VencimentoEnum.INSS.getCodigo());
            try {
                if (vctCodigos.contains(vctCodigo)) {
                    final List<CompMargem> composicoes = CompMargemHome.findByRseCodigoAndVctCodigo(rseCodigo, vctCodigo);

                    final CompMargem jaExiste = composicoes.stream().filter(comp -> !comp.getCmaCodigo().equals(cmaCodigo)).findAny().orElse(null);
                    if (jaExiste != null) {
                        throw new ServidorControllerException("mensagem.erro.editar.composicao.margem.vencimento.duplicado", responsavel);
                    }
                }
            } catch (final FindException ex) {
                LOG.info(ApplicationResourcesHelper.getMessage("mensagem.info.editar.composicao.margem.vencimento.nao.cadastrado", responsavel));
            }

            if (!TextHelper.isNull(cmaCodigo)) {
                try {
                    final CompMargem composicao = CompMargemHome.findByPrimaryKey(cmaCodigo);
                    if (!composicao.getVctCodigo().equals(vctCodigo)) {
                        composicao.setVctCodigo(vctCodigo);
                    }

                    composicao.setCmaVlr(cmaValor);
                    composicao.setCmaVinculo(cmaVinculo);
                    composicao.setCmaQuantidade(cmaQuantidade);
                    if (!TextHelper.isNull(vrsCodigo)) {
                        composicao.setVrsCodigo(vrsCodigo);
                    } else {
                        composicao.setVrsCodigo(null);
                    }
                    if (!TextHelper.isNull(crsCodigo)) {
                        composicao.setCrsCodigo(crsCodigo);
                    } else {
                        composicao.setCrsCodigo(null);
                    }

                    AbstractEntityHome.update(composicao);

                    salvaComposicaoMargemIrpfInss(rseCodigo, vctCodigo, cmaValor, responsavel);

                    // Grava log da operação
                    final LogDelegate log = new LogDelegate(responsavel, Log.COMPOSICAO_MARGEM, Log.UPDATE, Log.LOG_INFORMACAO);
                    log.setRegistroServidor(rseCodigo);
                    log.addChangedField(Columns.CMA_CODIGO, composicao.getCmaCodigo());
                    log.addChangedField(Columns.CMA_VLR, cmaValor);
                    log.write();

                } catch (final FindException ex) {
                    LOG.error(ex.getMessage(), ex);
                    throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
                }

            } else {
                final CompMargem composicao = CompMargemHome.create(rseCodigo, vctCodigo, vrsCodigo, crsCodigo, cmaValor, cmaVinculo, cmaQuantidade);

                salvaComposicaoMargemIrpfInss(rseCodigo, vctCodigo, cmaValor, responsavel);

                // Grava log da operação
                final LogDelegate log = new LogDelegate(responsavel, Log.COMPOSICAO_MARGEM, Log.CREATE, Log.LOG_INFORMACAO);
                log.setRegistroServidor(rseCodigo);
                log.addChangedField(Columns.CMA_CODIGO, composicao.getCmaCodigo());
                log.addChangedField(Columns.CMA_VLR, cmaValor);
                log.write();
            }

        } catch (final ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ServidorControllerException(ex);
        } catch (CreateException | UpdateException | LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private void salvaComposicaoMargemIrpfInss(String rseCodigo, String vctCodigo, BigDecimal cmaValor, AcessoSistema responsavel) throws ServidorControllerException, UpdateException, CreateException {
        if (VencimentoEnum.PROVENTO_BASE.getCodigo().equals(vctCodigo)) {
            // Recalcula IRPF
            try {
                final List<CompMargem> listaIRPF = CompMargemHome.findByRseCodigoAndVctCodigo(rseCodigo, VencimentoEnum.IRPF.getCodigo());

                if ((listaIRPF != null) && (listaIRPF.size() > 1)) {
                    throw new ServidorControllerException("mensagem.erro.editar.composicao.margem.vencimento.irpf.duplicado", responsavel);
                }

                final CompMargem irpf = listaIRPF.get(0);
                irpf.setCmaVlr(VencimentoEnum.IRPF.getValor(cmaValor));
                irpf.setCmaQuantidade(1);
                AbstractEntityHome.update(irpf);

            } catch (final FindException ex) {
                CompMargemHome.create(rseCodigo, VencimentoEnum.IRPF.getCodigo(), null, null, VencimentoEnum.IRPF.getValor(cmaValor), null, 1);
            }

            // Recalcula INSS
            try {
                final List<CompMargem> listaINSS = CompMargemHome.findByRseCodigoAndVctCodigo(rseCodigo, VencimentoEnum.INSS.getCodigo());

                if ((listaINSS != null) && (listaINSS.size() > 1)) {
                    throw new ServidorControllerException("mensagem.erro.editar.composicao.margem.vencimento.inss.duplicado", responsavel);
                }

                final CompMargem inss = listaINSS.get(0);
                inss.setCmaVlr(VencimentoEnum.INSS.getValor(cmaValor));
                inss.setCmaQuantidade(1);
                AbstractEntityHome.update(inss);

            } catch (final FindException ex) {
                CompMargemHome.create(rseCodigo, VencimentoEnum.INSS.getCodigo(), null, null, VencimentoEnum.INSS.getValor(cmaValor), null, 1);
            }
        }
    }

    @Override
    public void excluirComposicaoMargem(String cmaCodigo, AcessoSistema responsavel) throws ServidorControllerException {

        if (TextHelper.isNull(cmaCodigo)) {
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel);
        }

        try {
            final CompMargem compMargem = CompMargemHome.findByPrimaryKey(cmaCodigo);
            AbstractEntityHome.remove(compMargem);
        } catch (FindException | RemoveException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> findVencimento(String vctIdentificador, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaVencimentoQuery query = new ListaVencimentoQuery();
            query.vctIdentificador = vctIdentificador;

            return query.executarDTO();

        } catch (final HQueryException ex) {
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void confirmarMargemFolha(List<String> rseCodigos, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            if ((rseCodigos == null) || rseCodigos.isEmpty()) {
                throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel);
            }

            for (final String rseCodigo : rseCodigos) {
                // Na ação de confirmação, o servidor terá seu status alterado para Ativo.
                RegistroServidorHome.alterarStatusRegistroServidor(rseCodigo, CodedValues.SRS_ATIVO);

                criaOcorrenciaRSE(rseCodigo, CodedValues.TOC_CONFIRMACAO_MARGEM_FOLHA_RSE, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ors.obs.confirmar.margem.folha", responsavel), null, responsavel);
            }

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void rejeitarMargemFolha(List<String> rseCodigos, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            if ((rseCodigos == null) || rseCodigos.isEmpty()) {
                throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel);
            }

            for (final String rseCodigo : rseCodigos) {
                // A ação de rejeição deverá zerar as margens folha e recalcular a margem do servidor, além de alterar seu status para ativo.
                RegistroServidorHome.alterarStatusRegistroServidor(rseCodigo, CodedValues.SRS_ATIVO);

                final ListaMargemRegistroServidoresQuery lstMargemSvcAtivo = new ListaMargemRegistroServidoresQuery();
                lstMargemSvcAtivo.rseCodigo = List.of(rseCodigo);
                lstMargemSvcAtivo.margensComSvcAtivo = true;

                final List<TransferObject> margensSvcAtivo = lstMargemSvcAtivo.executarDTO();

                final List<MargemTO> margens = new ArrayList<>();

                final RegistroServidorTO registroServidor = findRegistroServidor(rseCodigo, responsavel);
                for (final TransferObject margem : margensSvcAtivo) {
                	final short marCodigo = (Short) margem.getAttribute(Columns.MAR_CODIGO);
                	switch (marCodigo) {
                	case 1 -> registroServidor.setRseMargem(BigDecimal.ZERO);
                	case 2 -> registroServidor.setRseMargem2(BigDecimal.ZERO);
                	case 3 -> registroServidor.setRseMargem3(BigDecimal.ZERO);
                	default -> {
                		if (!TextHelper.isNull(margem.getAttribute(Columns.MRS_MARGEM))) {
                			final MargemTO marExtra = new MargemTO(marCodigo);
                			marExtra.setMrsMargem(BigDecimal.ZERO);

                			margens.add(marExtra);
                		}
                	}
                	}
                }

                updateRegistroServidor(registroServidor, margens, false, true, false, responsavel);

                criaOcorrenciaRSE(rseCodigo, CodedValues.TOC_REJEICAO_MARGEM_FOLHA_RSE, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ors.obs.rejeitar.margem.folha", responsavel), null, responsavel);
            }

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public OcorrenciaRegistroSer obtemUltimaOcorrenciaRegistroServidor(String rseCodigo, List<String> tocCodigos, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            return OcorrenciaRegistroServidorHome.findLastByRseTocCodigos(rseCodigo, tocCodigos);
        } catch (final FindException ex) {
            throw new ServidorControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> lstUnidadeSubOrgao(AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaUnidadeSubOrgaoQuery query = new ListaUnidadeSubOrgaoQuery();
            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public TransferObject findRegistroServidoresByLikeMatricula(String rseMatricula, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaRegistrosServidoresLikeMatriculaQuery rseQuery = new ListaRegistrosServidoresLikeMatriculaQuery();
            rseQuery.rseMatricula = rseMatricula;
            final List<TransferObject> lstRseLikeMatricula = rseQuery.executarDTO();
            if((lstRseLikeMatricula != null) && !lstRseLikeMatricula.isEmpty()) {
                return lstRseLikeMatricula.get(0);
            }
            return null;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> listaServidorPorCsaVerba(String csaCodigo, String cnvCodVerba, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaRegistrosServidoresByCsaConvenioQuery rseSerQuery = new ListaRegistrosServidoresByCsaConvenioQuery();
            rseSerQuery.csaCodigo = csaCodigo;
            rseSerQuery.cnvCodVerba = cnvCodVerba;

            return rseSerQuery.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
	public Servidor findByCpfMatricula(String matricula, String cpf, AcessoSistema responsavel)
			throws ServidorControllerException {
		Servidor servidor = null;
		try {
			if (matricula != null) {
				servidor = ServidorHome.findByRseMatricula(matricula);
				if ((cpf != null) && !servidor.getSerCpf().equals(cpf)) {
                	return null;
                } else {
                	return servidor;
                }
			} else {
				final List<Servidor> result = findByCpf(cpf, responsavel);
				if (!result.isEmpty()) {
					return result.get(0);
				} else {
                    return null;
				}
			}
		} catch (final FindException ex) {
			LOG.error(ex.getMessage(), ex);
			throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
		}
	}

    @Override
    public RegistroServidor findRseTipo(String matricula, String serCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        RegistroServidor rgSer = null;
        try {
            List<RegistroServidor> result = null;
            if (matricula != null) {
                result = RegistroServidorHome.findByMatricula(matricula);
            } else {
                result = RegistroServidorHome.findBySerCodigo(serCodigo);
            }

            if (!result.isEmpty()) {
                rgSer = result.get(0);
            }

            return rgSer;
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void updateCnvVincCsaSvc(String csaCodigo, String svcCodigo, List<String> vrsCodigos, AcessoSistema responsavel) throws ServidorControllerException {
       try {
           final ParamConvenioRegistroServidorDAO dao = DAOFactory.getDAOFactory().getParamConvenioRegistroServidorDAO();
           dao.updateCnvVincCsaSvc(csaCodigo, svcCodigo, vrsCodigos, responsavel);
       } catch (ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstFuncoesEnvioEmailSer(String serCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            return new FuncoesEnvioEmailSerQuery(serCodigo).executarDTO();
        } catch (final HQueryException  ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void salvarFuncoesEnvioEmailSer(List<DestinatarioEmailSer> listaInc, List<DestinatarioEmailSer> listaExc, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            if ((listaInc != null) && !listaInc.isEmpty()) {
                for (final DestinatarioEmailSer destinatario : listaInc) {
                	DestinatarioEmailSerHome.create(destinatario.getFunCodigo(), destinatario.getPapCodigo(), destinatario.getSerCodigo(), destinatario.getDesReceber());

                    final LogDelegate log = new LogDelegate(responsavel, Log.CONFIGURACAO_ENVIO_EMAIL_SER, Log.CREATE, Log.LOG_INFORMACAO);
                    log.setFuncao(destinatario.getFunCodigo());
                    log.setPapel(destinatario.getPapCodigo());
                    log.setServidor(destinatario.getSerCodigo());
                    log.addChangedField(Columns.DES_RECEBER, destinatario.getDesReceber());
                    log.write();
                }
            }
            if ((listaExc != null) && !listaExc.isEmpty()) {
                for (final DestinatarioEmailSer destinatario : listaExc) {
                	final DestinatarioEmailSer dem = DestinatarioEmailSerHome.findByPrimaryKey(destinatario.getFunCodigo(), destinatario.getPapCodigo(), destinatario.getSerCodigo());
                	AbstractEntityHome.remove(dem);

                    final LogDelegate log = new LogDelegate(responsavel, Log.CONFIGURACAO_ENVIO_EMAIL_SER, Log.DELETE, Log.LOG_INFORMACAO);
                    log.setFuncao(destinatario.getFunCodigo());
                    log.setPapel(destinatario.getPapCodigo());
                    log.setServidor(destinatario.getSerCodigo());
                    log.write();
                }
            }
        } catch (CreateException | RemoveException | FindException | LogControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public String getEmailSerNotificacaoOperacao(String funCodigo, String papCodigoOperador, String serCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        final ServidorTransferObject servidor = findServidor(serCodigo, responsavel);
        try {
            final DestinatarioEmailSer bean = DestinatarioEmailSerHome.findByPrimaryKey(new DestinatarioEmailSerId(funCodigo, papCodigoOperador, serCodigo));
            if ("N".equalsIgnoreCase(bean.getDesReceber())) {
                // Se existe o registro e o servidor optou por não receber, então retorna nulo
                return null;
            } else {
                // Se existe o registro e o servidor optou por receber
                return servidor.getSerEmail();
            }
        } catch (final FindException ex) {
            // Se o servidor não tem configuração específica sobre a função, então deve retornar o e-mail do servidor
            return servidor.getSerEmail();
        }
    }

    @Override
    public List<TransferObject> findByRseTocCodigos(String rseCodigo, List<String> tocCodigos, AcessoSistema responsavel) throws ServidorControllerException {
    	try {
            final ListaOcorrenciaRegistroServidorQuery query = new ListaOcorrenciaRegistroServidorQuery();
            query.rseCodigo  = rseCodigo;
            query.tocCodigos = tocCodigos;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erro.listar.ocorrencias", responsavel, ex);
        }
    }

    @Override
    public void enviarNotificacaoVencimentoAutorizacao(Integer qtdeDiasVencimentoAutorizacao, AcessoSistema responsavel) {
        try {
            final List<ConsultaMargemSemSenha> consultas = ConsultaMargemSemSenhaHome.findAutorizacaoPrestesVencer(qtdeDiasVencimentoAutorizacao);
            if((consultas != null) && (!consultas.isEmpty())) {
                String csaNome = "";
                for(final ConsultaMargemSemSenha consulta : consultas) {
                    csaNome = consulta.getConsignataria().getCsaNome();
                    final String dataVencimento = DateHelper.toDateString(consultas.get(0).getCssDataFim());
                    final String corpoSms = ApplicationResourcesHelper.getMessage("mensagem.sms.vencimento.autorizacao", responsavel, csaNome, dataVencimento);
                    final String tituloPush = ApplicationResourcesHelper.getMessage("mensagem.notificacao.push.vencimento.autorizacao.titulo", responsavel);
                    final String textoPush = ApplicationResourcesHelper.getMessage("mensagem.notificacao.push.vencimento.autorizacao.texto", responsavel, csaNome, dataVencimento);
                    // Enviará a notificação de vencimento apenas uma vez
                    enviarNotificacaoSer(consulta.getRegistroServidor().getSerCodigo(), consulta.getCsaCodigo(), corpoSms, tituloPush, textoPush, TipoNotificacaoEnum.EMAIL_NOTIFICACAO_AUTORIZACAO_IRA_VENCER, dataVencimento, responsavel);
                }
            }
        } catch (final FindException ex) {
            LOG.error(ex.getMessage());
        }
    }

    @Override
    public void enviarNotificacaoSer(String serCodigo, String csaCodigo, String corpoSms, String tituloPush, String textoPush, TipoNotificacaoEnum tipoNotificacao, String dataVencimento, AcessoSistema responsavel) {

        String csaNome = "";
        Servidor servidor = null;
        try {
            csaNome = consignatariaController.findConsignataria(csaCodigo, responsavel).getCsaNome();
            servidor = ServidorHome.findByPrimaryKey(serCodigo);
        } catch (ConsignatariaControllerException | FindException ex) {
            LOG.error(ex.getMessage(), ex);
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.enviar.notificacao.ser", responsavel, serCodigo));
            return;
        }

        final String serEmail = servidor.getSerEmail();
        final String serNome = servidor.getSerNome();

        // Verifica se o sistem permite enviar notificação
        final Object paramEnviarNotificacaoSerReservaMargem = ParamSist.getInstance().getParam(CodedValues.TPC_ENVIAR_EMAIL_SER_RESERVA_MARGEM_APOS_OCORRER_SEM_SENHA, responsavel);

        final String strParamEnviarNotificacaoSerReservaMargem = paramEnviarNotificacaoSerReservaMargem != null ? paramEnviarNotificacaoSerReservaMargem.toString() : CodedValues.EMAIL_NOTIFICACAOO_RESERVA_MARGEM_DESABILITADO;

        if (CodedValues.EMAIL_NOTIFICACAOO_RESERVA_MARGEM_SMS.equals(strParamEnviarNotificacaoSerReservaMargem) || CodedValues.EMAIL_NOTIFICACAOO_RESERVA_MARGEM_SMS_EMAIL.equals(strParamEnviarNotificacaoSerReservaMargem)
                || CodedValues.EMAIL_NOTIFICACAOO_RESERVA_MARGEM_SMS_EMAIL_PUSH_NOTIFICATION.equals(strParamEnviarNotificacaoSerReservaMargem)) {
            try {
                final String accountSid = ParamSist.getInstance().getParam(CodedValues.TPC_SID_CONTA_SMS, responsavel).toString();
                final String authToken = ParamSist.getInstance().getParam(CodedValues.TPC_TOKEN_AUTENTICACAO_SMS, responsavel).toString();
                final String fromNumber = ParamSist.getInstance().getParam(CodedValues.TPC_NUMERO_REMETENTE_SMS, responsavel).toString();

                // Formata o telefone para o padrão do país
                final String serCelular = !TextHelper.isNull(servidor.getSerCelular()) ? LocaleHelper.formataCelular(servidor.getSerCelular()) : null;

                new SMSHelper(accountSid, authToken, fromNumber).send(serCelular, corpoSms);
            } catch (final ZetraException e) {
                try {
                    // Envia o e-mail caso o celular não tenha sido informado
                    if(dataVencimento == null) {
                        EnviaEmailHelper.notificarSerReservaMargem(serEmail, serNome, csaNome);
                    } else {
                        EnviaEmailHelper.notificarSerAutorizacaoIraVencer(serEmail, serNome, csaNome, dataVencimento);
                    }
                } catch (final ViewHelperException ex) {
                    try {
                        // Fazer push notification caso o celular e e-mail não tenha sido informado
                        notificacaoDispositivoController.enviarNotificacao(serCodigo, tituloPush, textoPush, tipoNotificacao, CodedValues.FUN_RES_MARGEM, responsavel);
                    } catch(final NotificacaoDispositivoControllerException exc) {
                        // Não dá rollback em caso de erro de notificação ao dispositivo
                        LOG.error(exc.getMessage(), exc);
                        LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.sms.ou.email.ou.push.notification.enviar", responsavel));
                    }
                }
            }
        } else if (CodedValues.EMAIL_NOTIFICACAOO_RESERVA_MARGEM_EMAIL.equals(strParamEnviarNotificacaoSerReservaMargem) || CodedValues.EMAIL_NOTIFICACAOO_RESERVA_MARGEM_EMAIL_PUSH_NOTIFICATION.equals(strParamEnviarNotificacaoSerReservaMargem)) {
            // Envia o e-mail
            try {
                if(dataVencimento == null) {
                    EnviaEmailHelper.notificarSerReservaMargem(serEmail, serNome, csaNome);
                } else {
                    EnviaEmailHelper.notificarSerAutorizacaoIraVencer(serEmail, serNome, csaNome, dataVencimento);
                }
            } catch (final ViewHelperException ex) {
                try {
                    // Fazer push notification caso o e-mail não tenha sido informado
                    notificacaoDispositivoController.enviarNotificacao(serCodigo, tituloPush, textoPush, tipoNotificacao, CodedValues.FUN_RES_MARGEM, responsavel);
                } catch(final NotificacaoDispositivoControllerException exc) {
                    // Não dá rollback em caso de erro de notificação ao dispositivo
                    LOG.error(exc.getMessage(), exc);
                    LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.email.ou.push.notification.enviar", responsavel));
                }
            }
        } else if (CodedValues.EMAIL_NOTIFICACAOO_RESERVA_MARGEM_PUSH_NOTIFICATION.equals(strParamEnviarNotificacaoSerReservaMargem)) {
            // Envia o push notification
            try {
                notificacaoDispositivoController.enviarNotificacao(serCodigo, tituloPush, textoPush, tipoNotificacao, CodedValues.FUN_RES_MARGEM, responsavel);
            } catch (final NotificacaoDispositivoControllerException ex) {
                // Não dá rollback em caso de erro de notificação ao dispositivo
                LOG.error(ex.getMessage(), ex);
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.push.notification.enviar", responsavel));
            }
        }
    }

    @Override
    public List<HistoricoMargemFolha> lstHistoricoMargemFolhaRseFiltro(String rseCodigo, java.util.Date periodoIni, java.util.Date periodoFim, Short marCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            return HistoricoMargemFolhaHome.findByRseFilters(rseCodigo, periodoIni, periodoFim, marCodigo);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> consultaSalarioServidor(String cpf, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            ListaConsultaSalarioServidorQuery query = new ListaConsultaSalarioServidorQuery();
            query.serCpf = cpf;


            return query.executarDTO();
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void createRegistroServidorOcultoCsa(String rseCodigo, List<String> csaCodigos, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            List<String> csaOcultas = listCsaOcultasRse(rseCodigo, responsavel);

            List<String> removerOcultos = csaOcultas.stream().filter(e -> !csaCodigos.contains(e)).collect(Collectors.toList());
            List<String> incluirOcultos = csaCodigos.stream().filter(e -> !csaOcultas.contains(e)).collect(Collectors.toList());

            // Remove consignatária que não devem estar ocultas
            if (removerOcultos != null && !removerOcultos.isEmpty()) {
                for (String csaCodigo : removerOcultos) {
                    RegistroServidorOcultoCsaHome.remove(new RegistroServidorOcultoCsa(rseCodigo, csaCodigo));
                    String csaNome = consignatariaController.findConsignataria(csaCodigo, responsavel).getCsaNome();
                    criaOcorrenciaRSE(rseCodigo, CodedValues.TOC_EXIBIR_REGISTRO_SER_OCULTO_CSA, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ors.obs.rse.exibido.consignataria", responsavel, csaNome), null, responsavel);
                }

            }

            // Oculta novas consignatárias
            if (incluirOcultos != null && !incluirOcultos.isEmpty()) {
                for (String csaCodigo : incluirOcultos) {
                    RegistroServidorOcultoCsaHome.create(rseCodigo, csaCodigo, responsavel);
                    String csaNome = consignatariaController.findConsignataria(csaCodigo, responsavel).getCsaNome();
                    criaOcorrenciaRSE(rseCodigo, CodedValues.TOC_OCULTAR_REGISTRO_SER_OCULTO_CSA, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ors.obs.rse.oculto.consignataria", responsavel, csaNome), null, responsavel);
                }
            }

        } catch (CreateException | RemoveException | ConsignatariaControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    public List<String> listCsaOcultasRse(String rseCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            ObtemRegistroServidorOcultoCsaQuery query = new ObtemRegistroServidorOcultoCsaQuery(rseCodigo);
            return query.executarLista();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}
