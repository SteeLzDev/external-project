package com.zetra.econsig.service.rescisao;

import java.io.File;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.exception.ExportarArquivoRescisaoControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.ArquivoRescisaoDAO;
import com.zetra.econsig.persistence.dao.DAOFactory;
import com.zetra.econsig.values.CodedValues;


/**
 * <p>Title: ExportaArquivosBeneficioControllerBean</p>
 * <p>Description: Classe Bean para exportação de arquivos do modulo de rescisão</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Nostrum Consultoria e Projetos.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class ExportarArquivoRescisaoControllerBean implements ExportarArquivoRescisaoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ExportarArquivoRescisaoControllerBean.class);

    @Override
    public void exportarArquivoRescisao(AcessoSistema responsavel) throws ExportarArquivoRescisaoControllerException {
        try {
            // Pega os parâmetros de sistema necessários
            ParamSist ps = ParamSist.getInstance();

            // Diretório raiz de arquivos eConsig
            String absolutePath = ParamSist.getDiretorioRaizArquivos();
            String pathConf = absolutePath + File.separatorChar + "conf";
            
            // Arquivos de configuração para processamento do retorno
            String nomeArqConfEntrada = ps.getParam(CodedValues.TPC_ARQ_CONF_ENTRADA_EXP_RESCISAO, responsavel) != null ? pathConf  + File.separatorChar + (String) ps.getParam(CodedValues.TPC_ARQ_CONF_ENTRADA_EXP_RESCISAO, responsavel) : null;
            String nomeArqConfSaida = ps.getParam(CodedValues.TPC_ARQ_CONF_SAIDA_EXP_RESCISAO, responsavel) != null ? pathConf + File.separatorChar + (String) ps.getParam(CodedValues.TPC_ARQ_CONF_SAIDA_EXP_RESCISAO, responsavel) : null;
            String nomeArqConfTradutor = ps.getParam(CodedValues.TPC_ARQ_CONF_TRADUTOR_EXP_RESCISAO, responsavel) != null ? pathConf + File.separatorChar + (String) ps.getParam(CodedValues.TPC_ARQ_CONF_TRADUTOR_EXP_RESCISAO, responsavel) : null;

            // Verifica se os arquivos XML foram configurados
            if (TextHelper.isNull(nomeArqConfEntrada) || TextHelper.isNull(nomeArqConfSaida) || TextHelper.isNull(nomeArqConfTradutor)) {
                LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.erro.sistema.parametros.exportacao.rescisao.ausentes", responsavel));
                throw new ExportarArquivoRescisaoControllerException("mensagem.erro.sistema.parametros.exportacao.rescisao.ausentes", responsavel);
            }
            
            // Verifica se os arquivos XML existem no path informado nos parâmetros 
            File arqConfEntrada = new File(nomeArqConfEntrada);
            File arqConfTradutor = new File(nomeArqConfTradutor);
            File arqConfSaida = new File(nomeArqConfSaida);
            if (!arqConfEntrada.exists() || !arqConfTradutor.exists() || !arqConfSaida.exists()) {
                throw new ViewHelperException("mensagem.erro.sistema.arquivos.exportacao.rescisao.ausentes", responsavel);
            }

            // Cria diretório para arquivos de rescisão, caso não exista
            String pathArqSaida = absolutePath + File.separatorChar  + "rescisao" + File.separatorChar  + "cse";
            File filePathArqSaida = new File(pathArqSaida);
            if (!filePathArqSaida.exists()) {
                filePathArqSaida.mkdirs();
            }
            
            // Define o nome do arquivo de saída de exportação de rescisão
            String dia = DateHelper.format(new java.util.Date(), "dd-MM-yyyy-HH-MM-SS");
            String nomeArqSaida = ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.rescisao.prefixo", responsavel) + dia + ".txt";
            String nomeArqSaidaAbsoluto = pathArqSaida + File.separatorChar + nomeArqSaida;
            File arqSaida = new File(nomeArqSaidaAbsoluto);
            
            ArquivoRescisaoDAO arqDAO = DAOFactory.getDAOFactory().getArquivoRescisaoDAO();
            arqDAO.gerarArquivoRescisao(nomeArqSaidaAbsoluto, nomeArqConfEntrada, nomeArqConfTradutor, nomeArqConfSaida, responsavel);
            
            if(!arqSaida.exists()) {
                throw new ExportarArquivoRescisaoControllerException("mensagem.aviso.processo.movimento.rescisao.arquivo.vazio", responsavel);
            }
           
        } catch (ExportarArquivoRescisaoControllerException ex) {
            if(ex.getMessageKey() != null && ex.getMessageKey().equals("mensagem.erro.sistema.parametros.exportacao.rescisao.ausentes")) {
                throw new ExportarArquivoRescisaoControllerException("mensagem.erro.sistema.parametros.exportacao.rescisao.ausentes", responsavel);
            }else {
                throw new ExportarArquivoRescisaoControllerException("mensagem.aviso.processo.movimento.rescisao.arquivo.vazio", responsavel);    
            }
        } catch (Exception ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ExportarArquivoRescisaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}