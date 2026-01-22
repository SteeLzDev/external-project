package com.zetra.econsig.service.folha;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.RegraValidacaoMovimentoTO;
import com.zetra.econsig.dto.entidade.ResultadoRegraValidacaoMovimentoTO;
import com.zetra.econsig.dto.entidade.ResultadoValidacaoMovimentoTO;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.ValidacaoMovimentoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.folha.exportacao.validacao.ImportarArquivo;
import com.zetra.econsig.folha.exportacao.validacao.regra.Regra;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.DAOFactory;
import com.zetra.econsig.persistence.dao.ResultadoRegraValidacaoMovimentoDAO;
import com.zetra.econsig.persistence.entity.ResultadoRegraValidacaoMovimentoHome;
import com.zetra.econsig.persistence.entity.ResultadoValidacaoMov;
import com.zetra.econsig.persistence.entity.ResultadoValidacaoMovimentoHome;
import com.zetra.econsig.persistence.entity.Usuario;
import com.zetra.econsig.persistence.entity.UsuarioHome;
import com.zetra.econsig.persistence.query.movimento.ListaRegraValidacaoMovimentoQuery;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ValidacaoMovimentoControllerBean</p>
 * <p>Description: Implementação do EJB bean do controller de Validacao Movimento.</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class ValidacaoMovimentoControllerBean implements ValidacaoMovimentoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ValidacaoMovimentoControllerBean.class);

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private PeriodoController periodoController;

    /**
     * Executa rotina de validação do arquivo de movimento financeiro
     * @param nomeArquivo
     * @param estCodigos
     * @param orgCodigos
     * @param periodo
     * @param responsavel
     * @throws ConsignanteControllerException
     */
    @Override
    public void validarArquivoMovimento(String nomeArquivo, List<String> estCodigos, List<String> orgCodigos, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {

            LOG.debug("validando o arquivo " + nomeArquivo + " filtrando através dos estabelecimentos: " + (estCodigos == null ? "" : TextHelper.join(estCodigos, ",")) + " e orgãos: " + (orgCodigos == null ? "" : TextHelper.join(orgCodigos, ",")) + " e utilizando o usuário : " + (responsavel == null ? "" : responsavel.getUsuCodigo()));

            // Define a data de processamento.
            Date dataProcesso = DateHelper.getSystemDatetime();

            // Pega o código do órgão do usuário, caso este não seja de consignante
            String orgCodigoUsu = usuarioController.isOrg((responsavel != null ? responsavel.getUsuCodigo() : null));

            // Pega as configurações dos arquivos xml
            ParamSist ps = ParamSist.getInstance();
            String absolutePath = ParamSist.getDiretorioRaizArquivos();

            String nomeArqConfEntrada = (String) ps.getParam(CodedValues.TPC_ARQ_CONF_ENTRADA_IMP_MOV_FIN, responsavel);
            String nomeArqConfTradutor = (String) ps.getParam(CodedValues.TPC_ARQ_CONF_TRADUTOR_IMP_MOV_FIN, responsavel);
            String nomeArqConfSaida = (String) ps.getParam(CodedValues.TPC_ARQ_CONF_SAIDA_IMP_MOV_FIN, responsavel);

            String pathConf = absolutePath + File.separatorChar + "conf";
            String pathConfOrg = null;
            String pathConfEst = null;

            if (orgCodigoUsu != null) {
                /**
                 * Verifica se quem está validando o arquivo de movimento é usuário de orgão, se for pega os arquivos
                 * de configuração do diretório especifico do órgão, senão pega do diretório raiz.
                 * Se não tiver arquivo de configuração no diretório do órgão, usará o da raiz.
                 * Se não existir em ambos, gerará excessão.
                 */
                File dirPathConf = new File(pathConf + File.separatorChar + "cse" + File.separatorChar + orgCodigoUsu + File.separatorChar + nomeArqConfEntrada);
                if (dirPathConf.exists()) {
                    pathConfOrg = pathConf + File.separatorChar + "cse" + File.separatorChar + orgCodigoUsu;
                }
            } else {
                if (responsavel != null && ! responsavel.getUsuCodigo().equals("1")) {
                    LOG.debug("O código de usuário informado não é um usuário de orgão.");
                }
            }

            if (estCodigos != null && estCodigos.size() == 1) {
                /**
                 * Caso seja informado apenas um estabelecimento, recupera os arquivos de configuração deste estabelecimento
                 * Verifica se o estabelecimento informado possui arquivos de configuração.
                 * Caso não possua, usa os arquivos do diretório raiz.
                 */
                File dirPathConfEst = new File(pathConf + File.separatorChar + "est" + File.separatorChar + estCodigos.get(0).toString() + File.separatorChar + nomeArqConfEntrada);
                if (dirPathConfEst.exists()) {
                    pathConfEst = pathConf + File.separatorChar + "est" + File.separatorChar + estCodigos.get(0).toString();
                }
            }
            // Define o diretório para os arquivos de configuração, dando preferência para órgão, depois estabelecimento e, por fim, diretório conf (raiz/conf)
            pathConf = (!TextHelper.isNull(pathConfOrg) ? pathConfOrg : (!TextHelper.isNull(pathConfEst) ? pathConfEst : pathConf));

            nomeArqConfEntrada = pathConf + File.separatorChar + nomeArqConfEntrada;
            nomeArqConfTradutor = pathConf + File.separatorChar + nomeArqConfTradutor;
            nomeArqConfSaida = pathConf + File.separatorChar + nomeArqConfSaida;

            // Verifica se os arquivos existem
            LOG.debug("Utilizando arquivos de configuração: " + nomeArqConfEntrada + ", " + nomeArqConfTradutor + ", " + nomeArqConfSaida);
            File[] arqConf = { new File(nomeArqConfEntrada), new File(nomeArqConfTradutor), new File(nomeArqConfSaida) };
            for (File element : arqConf) {
                if (!element.exists()) {
                    throw new ConsignanteControllerException("mensagem.erro.arquivo.configuracao.nao.encontrado.arg0", responsavel, element.getPath());
                }
            }

            // Importa o arquivo de movimento a ser validado, gerando o histórico.
            ImportarArquivo importarArquivo = new ImportarArquivo(nomeArquivo, nomeArqConfEntrada, nomeArqConfTradutor, nomeArqConfSaida);
            importarArquivo.executar(true);

            // Recupera o período
            TransferObject periodoExportacao = periodoController.obtemPeriodoExportacaoDistinto(orgCodigos, estCodigos, responsavel);
            Date periodo = (Date) periodoExportacao.getAttribute(Columns.PEX_PERIODO);

            /*
             * Se existir um resultado para este arquivo, atualiza este registro e exclui os resultados das regras.
             * Do contrário, cria um novo.
             */
            ResultadoValidacaoMovimentoTO rvaTO;
            try {
                rvaTO = findResultadoValidacaoMovimentoByNomeArquivo(nomeArquivo, responsavel);
            } catch (ValidacaoMovimentoControllerException e) {
                // Não encontrou.
                rvaTO = new ResultadoValidacaoMovimentoTO();
            }
            rvaTO.setRvaNomeArquivo(nomeArquivo);
            rvaTO.setRvaPeriodo(periodo);
            rvaTO.setRvaDataProcesso(dataProcesso);
            rvaTO.setRvaResultado(null);
            rvaTO.setUsuCodigo(responsavel.getUsuCodigo());
            rvaTO.setRvaAceite(null);
            rvaTO.setRvaDataAceite(null);
            if (rvaTO.getRvaCodigo() == null) {
                // CREATE
                rvaTO.setAttribute(Columns.RVA_CODIGO, createResultadoValidacaoMovimento(rvaTO, responsavel));
            } else {
                // UPDATE
                updateResultadoValidacaoMovimento(rvaTO, responsavel);
                // Exclui os resultados das regras do processamento anterior.
                ResultadoRegraValidacaoMovimentoDAO rrvDAO = DAOFactory.getDAOFactory().getResultadoRegraValidacaoMovimentoDAO();
                rrvDAO.deleteResultadoRegras(rvaTO.getRvaCodigo(), null);
                rrvDAO = null;
            }

            // Executa as regras de validação do arquivo, pegando o resultado geral do arquivo.
            String resultado = executarRegras(estCodigos, orgCodigos, rvaTO, responsavel);

            // Salvar resultado geral da validação.
            rvaTO.setRvaResultado(resultado);
            updateResultadoValidacaoMovimento(rvaTO, responsavel);

        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private String executarRegras(List<String> estCodigos, List<String> orgCodigos, ResultadoValidacaoMovimentoTO rvaTO, AcessoSistema responsavel) throws ZetraException {

        // 1. listar regras
        ListaRegraValidacaoMovimentoQuery rvmQuery = new ListaRegraValidacaoMovimentoQuery();
        rvmQuery.rvmAtivo = Boolean.TRUE;
        List<RegraValidacaoMovimentoTO> regras = rvmQuery.executarDTO(RegraValidacaoMovimentoTO.class);
        /*
         * DESENV-8810 : Percorre todas as classes criando as tabelas necessárias.
         */
        for (RegraValidacaoMovimentoTO regraTO: regras) {
            try {
                Regra regra = (Regra) Class.forName(regraTO.getRvmJavaClassName()).getDeclaredConstructor().newInstance();
                regra.criarTabelasValidacao();
            } catch (Exception ex) {
                throw new ZetraException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
            }
        }

        String resultado = CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_OK;
        RegraValidacaoMovimentoTO regraTO;
        Iterator<RegraValidacaoMovimentoTO> it = regras.iterator();
        Regra regra;
        ResultadoRegraValidacaoMovimentoTO resultadoTO;
        while (it.hasNext()) {
            regraTO = it.next();
            // 2a. executar regra
            // continua a executar a proxima regra mesmo que nao tenha conseguido executar a atual.
            // invalida o arquivo de acordo com a configuração da regra.
            try {
                regra = (Regra) Class.forName(regraTO.getRvmJavaClassName()).getDeclaredConstructor().newInstance();
                LOG.debug(regraTO.getRvmJavaClassName());
                regra.executar(estCodigos, orgCodigos, rvaTO, regraTO);
                resultadoTO = regra.getResultado();
            } catch (Exception ex) {
                resultadoTO = new ResultadoRegraValidacaoMovimentoTO(rvaTO.getRvaCodigo(), regraTO.getRvmCodigo());
                resultadoTO.setRrvResultado(CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_ERRO);
                resultadoTO.setRrvValorEncontrado(ex.getClass().getName());
            }
            // 2b. salvar resultado da regra
            if (CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_AVISO.equals(resultadoTO.getRrvResultado())) {
                if (CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_OK.equals(resultado)) {
                    resultado = CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_AVISO;
                }
            } else if (CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_ERRO.equals(resultadoTO.getRrvResultado())) {
                // verifica se invalida o arquivo
                if (regraTO.getRvmInvalidaMovimento().booleanValue()) {
                    resultado = CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_ERRO;
                } else if (CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_OK.equals(resultado)) {
                    resultado = CodedValues.VALIDACAO_MOVIMENTO_RESULTADO_AVISO;
                }
            }
            createResultadoRegraValidacaoMovimento(resultadoTO, responsavel);
        }
        return resultado;
    }

    // ResultadoValidacaoMov
    private ResultadoValidacaoMovimentoTO setResultadoValidacaoMovimentoValues(ResultadoValidacaoMov rvmBean) {
        ResultadoValidacaoMovimentoTO rvm = new ResultadoValidacaoMovimentoTO(rvmBean.getRvaCodigo());
        rvm.setUsuCodigo(rvmBean.getUsuario().getUsuCodigo());
        rvm.setRvaNomeArquivo(rvmBean.getRvaNomeArquivo());
        rvm.setRvaPeriodo(rvmBean.getRvaPeriodo());
        rvm.setRvaResultado(rvmBean.getRvaResultado());
        rvm.setRvaAceite(rvmBean.getRvaAceite());
        rvm.setRvaDataProcesso(rvmBean.getRvaDataProcesso());
        rvm.setRvaDataAceite(rvmBean.getRvaDataAceite());
        return rvm;
    }

    @Override
    public ResultadoValidacaoMovimentoTO findResultadoValidacaoMovimento(String rvaCodigo, AcessoSistema responsavel) throws ValidacaoMovimentoControllerException {
        ResultadoValidacaoMovimentoTO criterio = new ResultadoValidacaoMovimentoTO(rvaCodigo);
        return findResultadoValidacaoMovimento(criterio, responsavel);
    }

    @Override
    public ResultadoValidacaoMovimentoTO findResultadoValidacaoMovimentoByNomeArquivo(String rvaNomeArquivo, AcessoSistema responsavel) throws ValidacaoMovimentoControllerException {
        ResultadoValidacaoMovimentoTO criterio = new ResultadoValidacaoMovimentoTO();
        criterio.setRvaNomeArquivo(rvaNomeArquivo);
        return findResultadoValidacaoMovimento(criterio, responsavel);
    }

    @Override
    public ResultadoValidacaoMovimentoTO findResultadoValidacaoMovimento(ResultadoValidacaoMovimentoTO rvm, AcessoSistema responsavel) throws ValidacaoMovimentoControllerException {
        return setResultadoValidacaoMovimentoValues(findResultadoValidacaoMovimentoBean(rvm));
    }

    private ResultadoValidacaoMov findResultadoValidacaoMovimentoBean(ResultadoValidacaoMovimentoTO rvm) throws ValidacaoMovimentoControllerException {
        ResultadoValidacaoMov rvmBean = null;
        if (rvm.getRvaCodigo() != null) {
            try {
                rvmBean = ResultadoValidacaoMovimentoHome.findByPrimaryKey(rvm.getRvaCodigo());
            } catch (FindException ex) {
                throw new ValidacaoMovimentoControllerException("mensagem.erro.resultadovalidacaomovimento.nao.encontrada", (AcessoSistema) null);
            }
        } else if (rvm.getRvaNomeArquivo() != null) {
            try {
                rvmBean = ResultadoValidacaoMovimentoHome.findByNomeArquivo(rvm.getRvaNomeArquivo());
            } catch (FindException e) {
                throw new ValidacaoMovimentoControllerException("mensagem.erro.resultadovalidacaomovimento.nao.encontrada", (AcessoSistema) null);
            }
        } else {
            throw new ValidacaoMovimentoControllerException("mensagem.erro.resultadovalidacaomovimento.nao.encontrada", (AcessoSistema) null);
        }
        return rvmBean;
    }

    private String createResultadoValidacaoMovimento(ResultadoValidacaoMovimentoTO rvm, AcessoSistema responsavel) throws ValidacaoMovimentoControllerException {
        String rvaCodigo = null;
        try {
            ResultadoValidacaoMov rvmBean = ResultadoValidacaoMovimentoHome.create(rvm.getUsuCodigo(), rvm.getRvaNomeArquivo(), rvm.getRvaResultado(), rvm.getRvaAceite(), rvm.getRvaDataAceite(), rvm.getRvaPeriodo(), rvm.getRvaDataProcesso());
            // Pega o código do resultadoValidacaoMovimento criado
            rvaCodigo = rvmBean.getRvaCodigo();
        } catch (com.zetra.econsig.exception.CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            ValidacaoMovimentoControllerException excecao = new ValidacaoMovimentoControllerException("mensagem.erro.nao.possivel.criar.resultado.validacao.movimento.erro.interno.arg0", responsavel,ex.getMessage());
            if (ex.getMessage().indexOf("Invalid argument value") != -1) {
                excecao = new ValidacaoMovimentoControllerException("mensagem.erro.nao.possivel.criar.resultado.validacao.movimento.pois.existe.outro.mesmo.codigo.sistema", responsavel);
            }
            throw excecao;
        }
        return rvaCodigo;
    }

    @Override
    public void updateResultadoValidacaoMovimento(ResultadoValidacaoMovimentoTO rvm, AcessoSistema responsavel) throws ValidacaoMovimentoControllerException {

        try {
            ResultadoValidacaoMov rvmBean = findResultadoValidacaoMovimentoBean(rvm);
            LogDelegate log = new LogDelegate(responsavel, Log.RESULTADO_VALIDACAO_MOVIMENTO, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setResultadoValidacaoMovimento(rvmBean.getRvaCodigo());

            /* Compara a versão do cache com a passada por parâmetro */
            ResultadoValidacaoMovimentoTO rvmCache = setResultadoValidacaoMovimentoValues(rvmBean);
            CustomTransferObject merge = log.getUpdatedFields(rvm.getAtributos(), rvmCache.getAtributos());

            if (merge.getAtributos().containsKey(Columns.RVA_USU_CODIGO)) {
                Usuario usuario;
                try {
                    usuario = UsuarioHome.findByPrimaryKey((String) merge.getAttribute(Columns.RVA_USU_CODIGO));
                } catch (FindException ex) {
                    LOG.error(ex.getMessage(), ex);
                    throw new ValidacaoMovimentoControllerException("mensagem.erroInternoSistema", responsavel, ex);
                }
                rvmBean.setUsuario(usuario);
            }
            if (merge.getAtributos().containsKey(Columns.RVA_NOME_ARQUIVO)) {
                rvmBean.setRvaNomeArquivo((String) merge.getAttribute(Columns.RVA_NOME_ARQUIVO));
            }
            if (merge.getAtributos().containsKey(Columns.RVA_PERIODO)) {
                rvmBean.setRvaPeriodo((Date) merge.getAttribute(Columns.RVA_PERIODO));
            }
            if (merge.getAtributos().containsKey(Columns.RVA_RESULTADO)) {
                rvmBean.setRvaResultado((String) merge.getAttribute(Columns.RVA_RESULTADO));
            }
            if (merge.getAtributos().containsKey(Columns.RVA_ACEITE)) {
                rvmBean.setRvaAceite((Boolean) merge.getAttribute(Columns.RVA_ACEITE));
            }
            if (merge.getAtributos().containsKey(Columns.RVA_DATA_PROCESSO)) {
                rvmBean.setRvaDataProcesso((Date) merge.getAttribute(Columns.RVA_DATA_PROCESSO));
            }
            if (merge.getAtributos().containsKey(Columns.RVA_DATA_ACEITE)) {
                rvmBean.setRvaDataAceite((Date) merge.getAttribute(Columns.RVA_DATA_ACEITE));
            }

            ResultadoValidacaoMovimentoHome.update(rvmBean);
            log.write();
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ValidacaoMovimentoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new ValidacaoMovimentoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    // ResultadoRegraValidMov
    private void createResultadoRegraValidacaoMovimento(ResultadoRegraValidacaoMovimentoTO resultadoRegraValidacaoMovimento, AcessoSistema responsavel) throws ValidacaoMovimentoControllerException {
        try {
            ResultadoRegraValidacaoMovimentoHome.create(resultadoRegraValidacaoMovimento.getRvaCodigo(), resultadoRegraValidacaoMovimento.getRvmCodigo(), resultadoRegraValidacaoMovimento.getRrvResultado(), resultadoRegraValidacaoMovimento.getRrvValorEncontrado());
        } catch (com.zetra.econsig.exception.CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            ValidacaoMovimentoControllerException excecao = new ValidacaoMovimentoControllerException("mensagem.erro.nao.possivel.criar.resultado.validacao.movimento.erro.interno.arg0", responsavel, ex.getMessage());
            if (ex.getMessage().indexOf("Invalid argument value") != -1) {
                excecao = new ValidacaoMovimentoControllerException("mensagem.erro.nao.possivel.criar.resultado.validacao.movimento.pois.existe.outro.mesmo.codigo.sistema", responsavel);
            }
            throw new ValidacaoMovimentoControllerException(excecao);
        }
    }

    @Override
    public List<TransferObject> selectResultadoRegras(String rvaCodigo, AcessoSistema responsavel) throws ValidacaoMovimentoControllerException {
        try {
            ResultadoRegraValidacaoMovimentoDAO dao = DAOFactory.getDAOFactory().getResultadoRegraValidacaoMovimentoDAO();
            return dao.selectResultadoRegras(rvaCodigo);
        } catch (DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ValidacaoMovimentoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}
