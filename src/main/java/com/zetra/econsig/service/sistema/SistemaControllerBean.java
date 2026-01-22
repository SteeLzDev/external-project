package com.zetra.econsig.service.sistema;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileInfo;
import com.zetra.econsig.helper.criptografia.AES;
import com.zetra.econsig.helper.email.EmailAlteracaoOperacaoSensivel;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.rede.HttpHelper;
import com.zetra.econsig.helper.seguranca.AcessoRecursoHelper.AcessoRecurso;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.dao.ArquivamentoDAO;
import com.zetra.econsig.persistence.dao.BatchScriptDAO;
import com.zetra.econsig.persistence.dao.DAOFactory;
import com.zetra.econsig.persistence.entity.AcessoRecursoHome;
import com.zetra.econsig.persistence.entity.ArquivoOpeNaoConfirmadas;
import com.zetra.econsig.persistence.entity.ArquivoOpeNaoConfirmadasHome;
import com.zetra.econsig.persistence.entity.CampoSistemaHome;
import com.zetra.econsig.persistence.entity.ChaveCriptografiaArquivo;
import com.zetra.econsig.persistence.entity.ChaveCriptografiaArquivoHome;
import com.zetra.econsig.persistence.entity.DbOcorrencia;
import com.zetra.econsig.persistence.entity.DbOcorrenciaHome;
import com.zetra.econsig.persistence.entity.DestinatarioEmail;
import com.zetra.econsig.persistence.entity.DestinatarioEmailHome;
import com.zetra.econsig.persistence.entity.Funcao;
import com.zetra.econsig.persistence.entity.FuncaoHome;
import com.zetra.econsig.persistence.entity.ModeloEmail;
import com.zetra.econsig.persistence.entity.OperacaoNaoConfirmada;
import com.zetra.econsig.persistence.entity.OperacaoNaoConfirmadaHome;
import com.zetra.econsig.persistence.entity.RecursoSistema;
import com.zetra.econsig.persistence.entity.RecursoSistemaHome;
import com.zetra.econsig.persistence.entity.TextoSistemaHome;
import com.zetra.econsig.persistence.entity.Usuario;
import com.zetra.econsig.persistence.entity.UsuarioHome;
import com.zetra.econsig.persistence.query.admin.ListaBancoQuery;
import com.zetra.econsig.persistence.query.cep.ListaCepQuery;
import com.zetra.econsig.persistence.query.cidade.ListaCidadeCodigoIBGEQuery;
import com.zetra.econsig.persistence.query.cidade.ListaCidadeUfQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaTipoOcorrenciaQuery;
import com.zetra.econsig.persistence.query.funcao.ListarOperacoesSensiviesQuery;
import com.zetra.econsig.persistence.query.justica.ListaTipoJusticaQuery;
import com.zetra.econsig.persistence.query.mensagem.ListaModeloEmailQuery;
import com.zetra.econsig.persistence.query.senhaexterna.ListaParamSenhaExternaQuery;
import com.zetra.econsig.persistence.query.texto.ListaCampoSistemaQuery;
import com.zetra.econsig.persistence.query.texto.ListaTextoSistemaQuery;
import com.zetra.econsig.persistence.query.uf.ListaUfQuery;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;

/**
 * <p>Title: SistemaControllerBean</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class SistemaControllerBean implements SistemaController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SistemaControllerBean.class);

    private final static int BUFFER_SIZE = 2048;

    private final String NOME_PARAM_BLOQ_SIST = "BLOQUEIO";

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private ParametroController parametroController;

    @Override
    public boolean isSistemaBloqueado(AcessoSistema responsavel) {
        try {
            final Short codigo = verificaBloqueioSistema(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
            return (codigo.equals(CodedValues.STS_INDISP) || codigo.equals(CodedValues.STS_INATIVO));
        } catch (final ConsignanteControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            return true;
        }
    }

    @Override
    public Short verificaBloqueioSistema(String cseCodigo, AcessoSistema responsavel) throws ConsignanteControllerException {
        boolean bloqueado = false;
        boolean indisponivel = false;
        boolean pegaParametro = true;

        final ParamSist ps = ParamSist.getInstance();
        final int dia_atual = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        if (ps.hasParam(NOME_PARAM_BLOQ_SIST)) {
            final String param = ps.getParam(NOME_PARAM_BLOQ_SIST, responsavel).toString();
            final String valor = param.substring(param.indexOf("|") + 1, param.length());
            final int dia_param = Integer.parseInt(param.substring(0, param.indexOf("|")));

            if (dia_atual == dia_param) {
                // se o parâmetro existir e for o dia correto, obedece o que está marcado lá
                bloqueado = (valor.equals("S"));
                indisponivel = (valor.equals("I"));
                pegaParametro = false;
            }
        }

        // se o parâmetro existir e não for o dia correto ou se o parâmetro não existir
        if (pegaParametro) {
            int diaIni = -1, diaFim = -1;
            try {
                diaIni = Integer.parseInt(ps.getParam(CodedValues.TPC_DIA_INI_FECHAMENTO_SIST, responsavel).toString());
                diaFim = Integer.parseInt(ps.getParam(CodedValues.TPC_DIA_FIN_FECHAMENTO_SIST, responsavel).toString());
            } catch (final Exception ex) {
                diaIni = -1;
                diaFim = -1;
            }

            // se existir os parâmetros
            if (diaIni > 0 && diaFim > 0) {
                // se o dia atual for um dia de sistema fechado
                if (((diaIni < diaFim) && (diaIni <= dia_atual) && (diaFim >= dia_atual)) || ((diaIni > diaFim) && (diaIni <= dia_atual || diaFim >= dia_atual))) {
                    bloqueado = true;
                    ps.setParam(NOME_PARAM_BLOQ_SIST, dia_atual + "|S");
                } else {
                    bloqueado = false;
                    ps.setParam(NOME_PARAM_BLOQ_SIST, dia_atual + "|N");
                }
            } else {
                // se os parâmetros não existirem, olha se a cse está bloqueada
                final ConsignanteTransferObject cse = consignanteController.findConsignante(cseCodigo, responsavel);
                if (cse.getCseAtivo() != null && cse.getCseAtivo().equals(CodedValues.STS_INATIVO)) {
                    // se a consignante está bloqueada, seta parametro indicando que o sistema está fechado
                    bloqueado = true;
                    ps.setParam(NOME_PARAM_BLOQ_SIST, dia_atual + "|S");
                } else if (cse.getCseAtivo() != null && cse.getCseAtivo().equals(CodedValues.STS_ATIVO)) {
                    bloqueado = false;
                    ps.setParam(NOME_PARAM_BLOQ_SIST, dia_atual + "|N");
                } else if (cse.getCseAtivo() != null && cse.getCseAtivo().equals(CodedValues.STS_INDISP)) {
                    indisponivel = true;
                    bloqueado = false;
                    ps.setParam(NOME_PARAM_BLOQ_SIST, dia_atual + "|I");
                }
            }
        }

        return ((indisponivel) ? CodedValues.STS_INDISP : ((bloqueado) ? CodedValues.STS_INATIVO : CodedValues.STS_ATIVO));
    }

    @Override
    public void alteraStatusSistema(String cseCodigo, Short status, String msg, boolean alteraMsgSistema, AcessoSistema responsavel) throws ConsignanteControllerException {
        final ParamSist ps = ParamSist.getInstance();
        if (status.equals(CodedValues.STS_INATIVO)) {
            // Está bloqueando, seta parametro indicando que o sistema está fechado
            final int dia_atual = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            ps.setParam(NOME_PARAM_BLOQ_SIST, dia_atual + "|S");
        } else if (status.equals(CodedValues.STS_ATIVO)) {
            // Está desbloqueando, remover parâmetro de bloqueio do sistema do paramSist
            ps.dropParam(NOME_PARAM_BLOQ_SIST);
        } else {
            // Está colocando o sistema como indisponível
            status = CodedValues.STS_INDISP;
            // seta parametro indicando que o sistema está fechado
            final int dia_atual = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            ps.setParam(NOME_PARAM_BLOQ_SIST, dia_atual + "|I");
        }

        // Atualiza o consignante
        final ConsignanteTransferObject cseBloq = new ConsignanteTransferObject(cseCodigo);
        cseBloq.setCseAtivo(status);
        consignanteController.updateConsignante(cseBloq, msg, responsavel);

        if (!TextHelper.isNull(msg) && alteraMsgSistema) {
            // Atribui a mensagem de alteração de status no cache
            ps.setParam(CodedValues.TPC_ALTERA_MSG_BLOQUEIO_SISTEMA, msg);
            try {
                // Atribui a mensagem de alteração de status no banco
                parametroController.updateParamSistCse(msg, CodedValues.TPC_ALTERA_MSG_BLOQUEIO_SISTEMA, CodedValues.CSE_CODIGO_SISTEMA, responsavel);
            } catch (final ParametroControllerException ex) {
                throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    @Override
    public void alteraStatusSistema(String cseCodigo, Short status, String msg, AcessoSistema responsavel) throws ConsignanteControllerException {
        alteraStatusSistema(cseCodigo, status, msg, false, responsavel);
    }

    @Override
    public List<TransferObject> lstTipoOcorrencia(List<String> tocCodigos, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            final ListaTipoOcorrenciaQuery query = new ListaTipoOcorrenciaQuery();
            query.tocCodigos = tocCodigos;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erro.impossivel.listar.tipo.ocorrencia", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> pesquisarBancos(AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            final ListaBancoQuery query = new ListaBancoQuery();
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstCampoSistema(AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            final ListaCampoSistemaQuery query = new ListaCampoSistemaQuery();
            query.responsavel = responsavel;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstCampoSistema(String casChave, boolean somenteCamposEditaveis, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            final ListaCampoSistemaQuery query = new ListaCampoSistemaQuery();
            query.casChave = casChave;
            query.somenteCamposEditaveis = somenteCamposEditaveis;
            query.responsavel = responsavel;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void carregarCampoSistema(Map<String, String> recursos, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            if (recursos != null && !recursos.isEmpty()) {
                for (final String chave : recursos.keySet()) {
                    final String valor = recursos.get(chave);
                    try {
                        // Verifica se a chave já foi carregada: se foi, não sobrepõe.
                        CampoSistemaHome.findByPrimaryKey(chave);
                    } catch (final FindException ex) {
                        // Caso não seja encontrada, carrega na base de dados.
                        CampoSistemaHome.create(chave, valor);
                    }
                }
            }
        } catch (final com.zetra.econsig.exception.CreateException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<RecursoSistema> lstRecursoSistema(AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            return RecursoSistemaHome.listAll();
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstParamSenhaExterna(AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            final ListaParamSenhaExternaQuery query = new ListaParamSenhaExternaQuery();
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstUf(AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            final ListaUfQuery query = new ListaUfQuery();
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstTipoJustica(AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            final ListaTipoJusticaQuery query = new ListaTipoJusticaQuery();
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void arquivarConsignacoesFinalizadas(AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            final DAOFactory daoFactory = DAOFactory.getDAOFactory();
            final ArquivamentoDAO arqDAO = daoFactory.getArquivamentoDAO();
            arqDAO.arquivarConsignacoesFinalizadas(responsavel);
        } catch (final DAOException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstCidadeUf(String ufCod, String termo, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            final ListaCidadeUfQuery query = new ListaCidadeUfQuery();
            query.ufCod = ufCod;
            query.termo = termo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstCidadeCodigoIBGE(String cidCodigoIbge, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            final ListaCidadeCodigoIBGEQuery query = new ListaCidadeCodigoIBGEQuery();
            query.cidCodigoIbge = cidCodigoIbge;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public boolean temDbOcorrencia(String dboArquivo, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            final List<DbOcorrencia> dboList = DbOcorrenciaHome.findByArquivo(dboArquivo);
            if (dboList != null && !dboList.isEmpty()) {
                return true;
            }
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
        return false;
    }

    @Override
    public void executarBatchScript(String nomeArquivo, String conteudo, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            // Se já tem registro de execução do arquivo então retorna sem executar
            if (temDbOcorrencia(nomeArquivo, responsavel)) {
                return;
            }

            // Executa o batch SQL de atualização
            final BatchScriptDAO dao = DAOFactory.getDAOFactory().getBatchScriptDAO();
            dao.executeBatch(conteudo);

            // Caso o próprio batch não tenha registrado sua execução, realiza o registro
            if (!temDbOcorrencia(nomeArquivo, responsavel)) {
                DbOcorrenciaHome.create(nomeArquivo);
            }
        } catch (DAOException | com.zetra.econsig.exception.CreateException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Modelo de Email
     **/
    @Override
    public ModeloEmail findModeloEmail(String memCodigo, AcessoSistema responsavel) throws ConsignanteControllerException {
        ModeloEmail result = null;
        if (memCodigo != null) {
            try {
                final ListaModeloEmailQuery query = new ListaModeloEmailQuery();
                query.memCodigo = memCodigo;
                query.responsavel = responsavel;
                List<TransferObject> list = query.executarDTO();
                if (list.size() == 0) {
                    query.responsavel = null;
                    list = query.executarDTO();
                }
                if (list.size() > 0) {
                    final TransferObject modeloEmail = list.get(0);
                    result = new ModeloEmail();
                    result.setMemCodigo((String)modeloEmail.getAttribute(Columns.MEM_CODIGO));
                    result.setMemTitulo((String)modeloEmail.getAttribute(Columns.MEM_TITULO));
                    result.setMemTexto((String)modeloEmail.getAttribute(Columns.MEM_TEXTO));
                }
            } catch (final HQueryException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
        return result;
    }

    @Override
    public byte[] obtemChaveCriptografiaArquivos(String papCodigo, String tarCodigo, String caaCodigoEnt, AcessoSistema responsavel) throws ConsignanteControllerException {
        byte[] chavePlana = null;
        try {
            // Se a chave existe, descriptografa e retorna
            final ChaveCriptografiaArquivo bean = ChaveCriptografiaArquivoHome.findByPrimaryKey(papCodigo, tarCodigo, caaCodigoEnt);
            chavePlana = AES.decryptKey(bean.getCaaChave());
        } catch (final FindException ex) {
            try {
                // Chave não existe, então cria uma nova
                chavePlana = AES.generateNewAESKey();

                // Criptografa e salva a nova chave
                ChaveCriptografiaArquivoHome.create(papCodigo, tarCodigo, caaCodigoEnt, AES.encryptKey(chavePlana));
            } catch (final com.zetra.econsig.exception.CreateException ex2) {
                LOG.error(ex.getMessage(), ex);
                TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
                throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
        if (chavePlana == null) {
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel);
        }

        return chavePlana;
    }

    @Override
    public List<TransferObject> findCep(String cepCodigo, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            final ListaCepQuery query = new ListaCepQuery();
            query.cepCodigo = cepCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Movido do TextoSistemaController pois este foi migrado para Spring e o helper
     * de mensagens carrega antes do Spring, de modo que não funciona corretamente.
     * @param responsavel
     * @throws ConsignanteControllerException
     */
    @Override
    public List<TransferObject> lstTextoSistema(AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            final ListaTextoSistemaQuery query = new ListaTextoSistemaQuery();
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Movido do TextoSistemaController pois este foi migrado para Spring e o helper
     * de mensagens carrega antes do Spring, de modo que não funciona corretamente.
     * @param recursos
     * @param responsavel
     * @throws ConsignanteControllerException
     */
    @Override
    public void carregarTextoSistema(Map<String, String> recursos, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            if (recursos != null && !recursos.isEmpty()) {
                final ListaTextoSistemaQuery ltsq = new ListaTextoSistemaQuery();
                try {
                    final List<TransferObject> textos = ltsq.executarDTO();
                    for (final TransferObject texto : textos) {
                        final String chave = texto.getAttribute(Columns.TEX_CHAVE).toString().trim();
                        recursos.remove(chave);
                        // Remove também chave com caracter especial para evitar erro de chave duplicada
                        recursos.remove(TextHelper.removeAccent(chave));
                    }
                } catch (final HQueryException ex) {
                    TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
                    LOG.error(ex.getMessage(), ex);
                    throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
                }
                for (final String chave : recursos.keySet()) {
                    // Tags de release não devem ser gravados no banco, já que continuarão sendo gerados no application resources
                    if (!chave.equals("release.date") && !chave.equals("release.tag")) {
                        final String valor = recursos.get(chave).trim();
                        LOG.info("Novo TextoSistema: " + chave + "=" + valor);
                        TextoSistemaHome.create(chave, valor);
                    }
                }
            }
        } catch (final com.zetra.econsig.exception.CreateException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Grava na base do sistema um snapshot da página com os valores da operação a ser aprovada posteriormente
     * @param conteudo - string que representa o conteúdo da página em formato ZIP
     * @param ipAcesso - IP de solicitação da operação
     * @param parametros - parâmetros de request necessários para executar a operação a aprovar
     * @param uploadHelper - parâmetro com helper para tratar eventuais arquivos enviados em requisição Multi-part
     * @param responsavel
     * @throws ConsignanteControllerException
     */
    @Override
    public void gravarPaginaOperacaoSensivel(String conteudo, String ipAcesso, Map<String, Map<String, String[]>> parametros, UploadHelper uploadHelper, String hashDir, AcessoRecurso acessoRecurso, AcessoSistema responsavel) throws ConsignanteControllerException {
        if (TextHelper.isNull(ipAcesso)) {
            throw new ConsignanteControllerException("mensagem.erro.endereco.ip.invalido", responsavel);
        }

        final JsonObjectBuilder requestParamJson = Json.createObjectBuilder();

        if (parametros != null)  {
            parametros.forEach((key, mapParam) -> {
                if (mapParam != null) {
                    final JsonObjectBuilder jsonInnerList = Json.createObjectBuilder();
                    mapParam.forEach((innerKey, value) -> {
                        if (value != null) {
                            if (value.length == 1) {
                                jsonInnerList.add(innerKey, value[0]);
                            } else {
                                final JsonArrayBuilder array = Json.createArrayBuilder();
                                for (final String param: value) {
                                    if (!TextHelper.isNull(param)) {
                                        array.add(param);
                                    }
                                }
                                jsonInnerList.add(innerKey, array);
                            }
                        }
                    });
                    requestParamJson.add(key, jsonInnerList);
                }
            });
        }

        OperacaoNaoConfirmada opNaoConfirmada = null;
        try {
            JsonObject requestParamJsonObject = requestParamJson.build();
            
            String rseCodigo = null;
            if(acessoRecurso.getFunCodigo().equals(CodedValues.FUN_EDT_SERVIDOR)) {
                JsonObject queryString = requestParamJsonObject.getJsonObject("queryString");
                rseCodigo = queryString.getString("RSE_CODIGO");
            }
            
            opNaoConfirmada = OperacaoNaoConfirmadaHome.create(acessoRecurso.getAcrCodigo(), responsavel.getUsuCodigo(), rseCodigo, ipAcesso, conteudo, requestParamJsonObject.toString(), DateHelper.getSystemDatetime());

            if (uploadHelper != null) {
                uploadHelper.salvarArquivosBaseOpNaoConfirmadas(opNaoConfirmada.getOncCodigo(), hashDir);
            }
        } catch (final com.zetra.econsig.exception.CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new ConsignanteControllerException("mensagem.erro.inserir.operacao.sensivel.fila.confirmacao", responsavel, ex);
        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new ConsignanteControllerException(ex);
        }
    }

    @Override
    public void gravarAnexosFilaAutorizacao(String oncCodigo, String arqNome, Long tamArq, String conteudoArq, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            ArquivoOpeNaoConfirmadasHome.create(arqNome, tamArq, conteudoArq, oncCodigo);
        } catch (final com.zetra.econsig.exception.CreateException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new ConsignanteControllerException("mensagem.erro.inserir.arq.operacao.sensivel.fila.confirmacao", responsavel, ex);
        }
    }

    /**
     * lista operações sensíveis na fila de autorização que podem ser autorizadas pelo usuário responsável
     * @param entCodigo - código da entidade à qual pertence o usuário logado
     * @param responsavel
     * @return
     * @throws ConsignanteControllerException
     */
    @Override
    public List<TransferObject> listarFilaOperacao(AcessoSistema responsavel)  throws ConsignanteControllerException {
        final ListarOperacoesSensiviesQuery lstOpAutorizar = new ListarOperacoesSensiviesQuery();
        lstOpAutorizar.entidadeCodigo = responsavel.getCodigoEntidade();
        lstOpAutorizar.responsavel = responsavel;

        try {
            final List<TransferObject> lstFilaOperacoes = lstOpAutorizar.executarDTO();

            // retorna lista filtrada com as operações que o responsável tem permissão de acesso
            return lstFilaOperacoes.stream().filter(operacao -> responsavel.temPermissao((String) operacao.getAttribute(Columns.FUN_CODIGO))).collect(Collectors.toList());
        } catch (final HQueryException e) {
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel);
        }
    }

    @Override
    public int countOperacoesFilaAutorizacao(AcessoSistema responsavel)  throws ConsignanteControllerException {
        final ListarOperacoesSensiviesQuery lstOpAutorizar = new ListarOperacoesSensiviesQuery();
        lstOpAutorizar.entidadeCodigo = responsavel.getCodigoEntidade();
        lstOpAutorizar.responsavel = responsavel;

        try {
            List<TransferObject> lstFilaOperacoes = lstOpAutorizar.executarDTO();

            // retorna lista filtrada com as operações que o responsável tem permissão de acesso
            lstFilaOperacoes =  lstFilaOperacoes.stream().filter(operacao -> responsavel.temPermissao((String) operacao.getAttribute(Columns.FUN_CODIGO))).collect(Collectors.toList());
            return lstFilaOperacoes.size();
        } catch (final HQueryException e) {
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel);
        }
    }

    @Override
    public OperacaoNaoConfirmada findOperacaoNaoConfirmada (String oncCodigo, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            return OperacaoNaoConfirmadaHome.findByPrimaryKey(oncCodigo);
        } catch (final FindException e) {
            throw new ConsignanteControllerException("mensagem.erro.fila.op.nao.encontrado", responsavel);
        }
    }

    /**
     * Resolve as operações em fila de autorização, confirmando ou descartando-as de acordo com o definido pelo usuário autorizador
     * @param oncCodigoDescartar - operações em fila a descartar
     * @param oncCodigoConfirmar  - operações em fila a confirmar
     * @param obsDescarte - Observação de descarte. Obrigatório caso haja operação em oncCodigoDescartar
     * @param responsavel
     * @throws ConsignanteControllerException
     */
    @Override
    public void descartarOpFilaAutorizacao (String oncCodigoDescartar, String obsDescarte, Map<HttpHelper.SessionKeysEnum, String> sessionConfig, AcessoSistema responsavel) throws ConsignanteControllerException {
        final CloseableHttpClient instance = null;

        try {
            if (!TextHelper.isNull(oncCodigoDescartar)) {
                if (TextHelper.isNull(obsDescarte)) {
                    throw new ConsignanteControllerException("mensagem.erro.fila.op.obs.descarte.vazio", responsavel);
                }

                descartarOpFilaAutorizacao(oncCodigoDescartar, obsDescarte, responsavel);
            }
        } catch (final RemoveException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erro.fila.op.remover", responsavel);
        } catch (final FindException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erro.fila.op.nao.encontrado", responsavel);
        } catch (LogControllerException | ViewHelperException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel);
        } finally {
            if (instance != null) {
                try {
                    instance.close();
                } catch (final IOException ex) {
                    LOG.error(ex.getMessage(), ex);
                    throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel);
                }
            }
        }
    }

    @Override
    public void confirmarOperacaoFila (String oncConfirmar, Map<HttpHelper.SessionKeysEnum, String> sessionConfig, AcessoSistema responsavel) throws ConsignanteControllerException {
        OperacaoNaoConfirmada operacao = null;
        Reader responseStream = null;
        Reader responseStream1 = null;
        HttpURLConnection con = null;
        HttpURLConnection conRedirect = null;
        JsonReader jsonReader = null;
        JsonReader jsonReader1 = null;
        ZipInputStream zipFile = null;
        String errorMsg = "";

        try {
            operacao = OperacaoNaoConfirmadaHome.findByPrimaryKey(oncConfirmar);

            final com.zetra.econsig.persistence.entity.AcessoRecurso accessoOperacaoFila = AcessoRecursoHome.findByPrimaryKey(operacao.getAcessoRecurso().getAcrCodigo());

            jsonReader = Json.createReader(new StringReader(operacao.getOncParametros().trim()));
            final JsonObject paramsRequisicao = jsonReader.readObject();

            final Collection<ArquivoOpeNaoConfirmadas> arquivos = ArquivoOpeNaoConfirmadasHome.findByOncCodigo(operacao.getOncCodigo());

            final List<FileInfo> arquivosInfo = new ArrayList<>();
            if (arquivos != null && !arquivos.isEmpty()) {
                for (final ArquivoOpeNaoConfirmadas arq: arquivos) {
                    final FileInfo fileInfo = new FileInfo();
                    fileInfo.fileName = arq.getAonNome();
                    fileInfo.fileSize = arq.getAonTamanho();
                    zipFile = new ZipInputStream(new BufferedInputStream(new ByteArrayInputStream(Base64.decodeBase64(arq.getAonConteudo()))));

                    ZipEntry entry = null;
                    final List<ByteArrayOutputStream> listEntryZip = new ArrayList<>();
                    while ((entry = zipFile.getNextEntry()) != null) {
                        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        int count = 0;
                        final byte data[] = new byte[BUFFER_SIZE];

                        if (!entry.isDirectory()) {
                            final BufferedOutputStream out = new BufferedOutputStream(
                                    outputStream, BUFFER_SIZE);
                            while ((count = zipFile.read(data, 0, BUFFER_SIZE)) != -1) {
                                out.write(data, 0, count);
                            }
                            out.flush();
                            out.close();
                            listEntryZip.add(outputStream);

                        }
                    }

                    fileInfo.fileContent = listEntryZip.get(0).toByteArray();
                    arquivosInfo.add(fileInfo);

                    ArquivoOpeNaoConfirmadasHome.remove(arq);
                }
            }

            con = HttpHelper.eConsigPostHttpRequest(sessionConfig, accessoOperacaoFila, paramsRequisicao, arquivosInfo, responsavel);

            responseStream = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

            final StringBuilder sb = new StringBuilder();
            for (int c; (c = responseStream.read()) >= 0;) {
                sb.append((char)c);
            }

            if ("true".equals(con.getHeaderField("redirect"))) {
                final com.zetra.econsig.persistence.entity.AcessoRecurso acessoRedirecionamento = AcessoRecursoHome.findByAcrRecurso("/v3/exibirMensagem", responsavel.getPapCodigo());

                final String json = "{\"" + HttpHelper.REQUEST_QUERY_STRING_JSON + "\": { \"acao\": \"exibirMsgSessao\" } }";

                jsonReader1 = Json.createReader(new StringReader(json.trim()));
                final JsonObject paramsRequisicao1 = jsonReader1.readObject();

                conRedirect = HttpHelper.eConsigPostHttpRequest(sessionConfig, acessoRedirecionamento, paramsRequisicao1, new ArrayList<>(), responsavel);

                responseStream1 = new BufferedReader(new InputStreamReader(conRedirect.getInputStream(), "UTF-8"));
                sb.setLength(0);
                for (int c; (c = responseStream1.read()) >= 0;) {
                    sb.append((char)c);
                }
            }

            final Usuario usuExecutor = UsuarioHome.findByPrimaryKey(operacao.getUsuario().getUsuCodigo());
            final Funcao funcao = FuncaoHome.findByPrimaryKey(accessoOperacaoFila.getFuncao().getFunCodigo());

            if (con.getResponseCode() != java.net.HttpURLConnection.HTTP_OK) {
                final LogDelegate log = new LogDelegate(responsavel, Log.OPERACAO_FILA_AUTORIZACAO, Log.DELETE, Log.LOG_ERRO);
                log.setUsuario(operacao.getUsuario().getUsuCodigo());
                log.setFuncao(AcessoRecursoHome.findByPrimaryKey(operacao.getAcessoRecurso().getAcrCodigo()).getFuncao().getFunCodigo());
                log.add(sb.toString());
                log.write();

                throw new ConsignanteControllerException("mensagem.erro.fila.op.execucao.autorizador", responsavel, funcao.getFunDescricao(),
                                                         DateHelper.format(operacao.getOncData(), LocaleHelper.getDateTimePattern()), usuExecutor.getUsuLogin());
            } else {
                final LogDelegate log = new LogDelegate(responsavel, Log.OPERACAO_FILA_AUTORIZACAO, Log.DELETE, Log.LOG_INFORMACAO);
                log.setUsuario(operacao.getUsuario().getUsuCodigo());
                log.setFuncao(AcessoRecursoHome.findByPrimaryKey(operacao.getAcessoRecurso().getAcrCodigo()).getFuncao().getFunCodigo());
                log.add(ApplicationResourcesHelper.getMessage("mensagem.info.fila.op.execucao.autorizador", responsavel,
                        funcao.getFunDescricao(),
                        DateHelper.format(operacao.getOncData(), LocaleHelper.getDateTimePattern()), usuExecutor.getUsuLogin()));
                log.add(sb.toString());
                log.write();
            }

            OperacaoNaoConfirmadaHome.remove(operacao);

            final EmailAlteracaoOperacaoSensivel email = new EmailAlteracaoOperacaoSensivel();
            email.setUsuNomeExecutor(usuExecutor.getUsuNome());
            email.setUsuLoginExecutor(usuExecutor.getUsuLogin());
            email.setUsuEmailExecutor(usuExecutor.getUsuEmail());
            email.setUsuNomeAutenticador(responsavel.getUsuNome());
            email.setUsuLoginAutenticador(responsavel.getUsuLogin());
            email.setOperacao(funcao.getFunDescricao());
            email.setDataOperacao(operacao.getOncData());
            email.setDataAlteracaoOperacao(DateHelper.getSystemDate());
            if (!TextHelper.isNull(sb.toString()) && sb.toString().contains(JspHelper.ID_MSG_ERROR_SESSION)) {
                final org.jsoup.nodes.Document doc = Jsoup.parse(sb.toString());
                email.setMotivoOperacao(doc.getElementById(JspHelper.ID_MSG_ERROR_SESSION).text());

                // Se tem erro envia email de reprovação
                EnviaEmailHelper.enviarEmailReprovacaoOperacaoSensivel(email, responsavel);
                //lança exception para informar a quem está aprovando que ocorreu problema
                errorMsg = doc.getElementById(JspHelper.ID_MSG_ERROR_SESSION).text();
                throw new ConsignanteControllerException("mensagem.erro.fila.op.execucao.autorizador.com.msg.erro", responsavel, funcao.getFunDescricao(),
                        DateHelper.format(operacao.getOncData(), LocaleHelper.getDateTimePattern()), usuExecutor.getUsuLogin(), errorMsg, usuExecutor.getUsuLogin());
            } else {

                // Envia email de confirmação da operação
                EnviaEmailHelper.enviarEmailConfirmacaoOperacaoSensivel(email, responsavel);
            }

        } catch (final FindException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erro.fila.op.nao.encontrado", responsavel);
        } catch (IOException | ViewHelperException | LogControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel);
        } catch (final RemoveException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erro.fila.op.remover", responsavel);
        } catch (final ZetraException ex) {
            if(TextHelper.isNull(errorMsg)) {
                TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            }
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException(TextHelper.isNull(ex.getMessageKey()) ? "mensagem.erroInternoSistema" : ex.getMessageKey(), responsavel, ex.getMessageArgs());
        } finally {
            if (con != null) {
                con.disconnect();
            }

            if (conRedirect != null) {
                conRedirect.disconnect();
            }

            if (jsonReader != null) {
                jsonReader.close();
            }

            if (jsonReader1 != null) {
                jsonReader1.close();
            }

            if (responseStream != null) {
                try {
                    responseStream.close();
                } catch (final IOException ex) {
                    LOG.error(ex.getMessage(), ex);
                    throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel);
                }
            }

            if (responseStream1 != null) {
                try {
                    responseStream1.close();
                } catch (final IOException ex) {
                    LOG.error(ex.getMessage(), ex);
                    throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel);
                }
            }

            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (final IOException ex) {
                    LOG.error(ex.getMessage(), ex);
                    throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel);
                }
            }
        }
    }

    @Override
    public com.zetra.econsig.persistence.entity.AcessoRecurso findAcessoRecurso (String acrCodigo, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            return AcessoRecursoHome.findByPrimaryKey(acrCodigo);
        } catch (final FindException e) {
            throw new ConsignanteControllerException("mensagem.erro.entidade.nao.encontrada", responsavel);
        }
    }

    private void descartarOpFilaAutorizacao (String oncCodigo, String obsDescarte, AcessoSistema responsavel) throws RemoveException, FindException, LogControllerException, ViewHelperException {
        final Collection<ArquivoOpeNaoConfirmadas> lstArqs = ArquivoOpeNaoConfirmadasHome.findByOncCodigo(oncCodigo);

        if (lstArqs != null && !lstArqs.isEmpty()) {
            for (final ArquivoOpeNaoConfirmadas arq: lstArqs) {
                ArquivoOpeNaoConfirmadasHome.remove(arq);
            }
        }

        final OperacaoNaoConfirmada operacao = OperacaoNaoConfirmadaHome.findByPrimaryKey(oncCodigo);
        final Usuario usuExecutor = UsuarioHome.findByPrimaryKey(operacao.getUsuario().getUsuCodigo());
        final com.zetra.econsig.persistence.entity.AcessoRecurso accessoOperacaoFila = AcessoRecursoHome.findByPrimaryKey(operacao.getAcessoRecurso().getAcrCodigo());
        final Funcao funcao = FuncaoHome.findByPrimaryKey(accessoOperacaoFila.getFuncao().getFunCodigo());

        final LogDelegate log = new LogDelegate(responsavel, Log.OPERACAO_FILA_AUTORIZACAO, Log.DELETE, Log.LOG_INFORMACAO);
        log.setUsuario(operacao.getUsuario().getUsuCodigo());
        log.setFuncao(AcessoRecursoHome.findByPrimaryKey(operacao.getAcessoRecurso().getAcrCodigo()).getFuncao().getFunCodigo());
        log.add(obsDescarte);
        log.write();

        OperacaoNaoConfirmadaHome.remove(OperacaoNaoConfirmadaHome.findByPrimaryKey(oncCodigo));

        try {
            final EmailAlteracaoOperacaoSensivel email = new EmailAlteracaoOperacaoSensivel();
            email.setUsuNomeExecutor(usuExecutor.getUsuNome());
            email.setUsuLoginExecutor(usuExecutor.getUsuLogin());
            email.setUsuEmailExecutor(usuExecutor.getUsuEmail());
            email.setUsuNomeAutenticador(responsavel.getUsuNome());
            email.setUsuLoginAutenticador(responsavel.getUsuLogin());
            email.setOperacao(funcao.getFunDescricao());
            email.setDataOperacao(operacao.getOncData());
            email.setDataAlteracaoOperacao(DateHelper.getSystemDate());
            email.setMotivoOperacao(obsDescarte);

            EnviaEmailHelper.enviarEmailReprovacaoOperacaoSensivel(email, responsavel);
        } catch (final ViewHelperException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    @Override
    public List<String> listarPapeisEnvioEmailOperacoes(String funCodigo, String papCodigoOperador, AcessoSistema responsavel) {
        try {
            final List<DestinatarioEmail> listDests = DestinatarioEmailHome.listByFunCodigoPapOperador(funCodigo, papCodigoOperador);
            return listDests.stream().map(t -> t.getPapelDestinatario().getPapCodigo()).collect(Collectors.toList());
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            return null;
        }
    }
}
