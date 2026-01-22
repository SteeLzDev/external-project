package com.zetra.econsig.service.sdp;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.EnderecoTransferObject;
import com.zetra.econsig.exception.DespesaIndividualControllerException;
import com.zetra.econsig.exception.EnderecoConjuntoHabitacionalControllerException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.EnderecoConjHabitacional;
import com.zetra.econsig.persistence.entity.EnderecoConjuntoHabitacionalHome;
import com.zetra.econsig.persistence.entity.PostoRegistroServidor;
import com.zetra.econsig.persistence.entity.PostoRegistroServidorHome;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.persistence.entity.RegistroServidorHome;
import com.zetra.econsig.persistence.query.endereco.ListaEnderecoQuery;
import com.zetra.econsig.persistence.query.sdp.despesaindividual.ListaDespesaTaxaUsoAtualizacaoQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: EnderecoConjuntoHabitacionalControllerBean</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author: juniogoncalves $
 * $Revision:  $
 * $Date: 2012-11-28 17:46:00 -0300 (qua, 28 nov 2012) $
 */
@Service
@Transactional
public class EnderecoConjuntoHabitacionalControllerBean implements EnderecoConjuntoHabitacionalController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnderecoConjuntoHabitacionalControllerBean.class);

    @Autowired
    private DespesaIndividualController despesaIndividualController;

	@Override
    public List<TransferObject> listaEndereco (TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws EnderecoConjuntoHabitacionalControllerException{
        try {
            ListaEnderecoQuery query = new ListaEnderecoQuery();

            if (offset != -1) {
                query.firstResult = offset;
            }

            if (count != -1) {
                query.maxResults = count;
            }

            if (criterio != null) {
                query.csaCodigo = criterio.getAttribute(Columns.ECH_CSA_CODIGO);
                query.echCodigo = (String) criterio.getAttribute(Columns.ECH_CODIGO);
                query.echDescricao = (String) criterio.getAttribute(Columns.ECH_DESCRICAO);
                query.echCondominio = (String) criterio.getAttribute(Columns.ECH_CONDOMINIO);
                query.echIdentificador = (String) criterio.getAttribute(Columns.ECH_IDENTIFICADOR);
            }

            return query.executarDTO();
        } catch (HQueryException ex) {
            throw new EnderecoConjuntoHabitacionalControllerException(ex);
        }
	}

    @Override
    public int countEndereco(TransferObject criterio, AcessoSistema responsavel) throws EnderecoConjuntoHabitacionalControllerException {
        try {
        	ListaEnderecoQuery query = new ListaEnderecoQuery();
            query.count = true;

            if (criterio != null) {
            	query.csaCodigo = criterio.getAttribute(Columns.ECH_CSA_CODIGO);
                query.echCodigo = (String) criterio.getAttribute(Columns.ECH_CODIGO);
                query.echDescricao = (String) criterio.getAttribute(Columns.ECH_DESCRICAO);
                query.echCondominio = (String) criterio.getAttribute(Columns.ECH_CONDOMINIO);
                query.echIdentificador = (String) criterio.getAttribute(Columns.ECH_IDENTIFICADOR);
            }

            return query.executarContador();
        } catch (HQueryException ex) {
            throw new EnderecoConjuntoHabitacionalControllerException(ex);
        }
    }

    @Override
    public void removeEndereco(EnderecoTransferObject endereco, AcessoSistema responsavel) throws EnderecoConjuntoHabitacionalControllerException {
        try {
        	EnderecoConjHabitacional enderecoBean = findEnderecoBean(endereco, responsavel);
            String ehcCodigo = enderecoBean.getEchCodigo();
            EnderecoConjuntoHabitacionalHome.remove(enderecoBean);
            LogDelegate logDelegate = new LogDelegate(responsavel, Log.ENDERECO_CONJUNTO_HABITACIONAL, Log.DELETE, Log.LOG_INFORMACAO);
            logDelegate.setEndereco(ehcCodigo);
            logDelegate.write();
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new EnderecoConjuntoHabitacionalControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (com.zetra.econsig.exception.RemoveException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new EnderecoConjuntoHabitacionalControllerException("mensagem.erro.excluir.endereco.conjunto.habitacional", responsavel);
        }
    }

    private EnderecoConjHabitacional findEnderecoBean(EnderecoTransferObject endereco, AcessoSistema responsavel) throws EnderecoConjuntoHabitacionalControllerException {
        EnderecoConjHabitacional enderecoBean = null;
        if (endereco.getEchCodigo() != null) {
            try {
            	enderecoBean = EnderecoConjuntoHabitacionalHome.findByPrimaryKey(endereco.getEchCodigo());
            } catch (FindException ex) {
                throw new EnderecoConjuntoHabitacionalControllerException("mensagem.erro.endereco.conjunto.habitacional.nao.encontrado", responsavel);
            }
        } else {
            throw new EnderecoConjuntoHabitacionalControllerException("mensagem.erro.endereco.conjunto.habitacional.nao.encontrado", responsavel);
        }
        return enderecoBean;
    }

    @Override
    public String createEndereco(EnderecoTransferObject endereco, AcessoSistema responsavel) throws EnderecoConjuntoHabitacionalControllerException {
    	String endCodigo = null;

    	try{
    		validaEndereco(endereco, responsavel);

            try {
                EnderecoConjuntoHabitacionalHome.findByIdn(endereco.getEchIdentificador(), endereco.getConsignataria());
                throw new EnderecoConjuntoHabitacionalControllerException("mensagem.erro.incluir.endereco.conjunto.habitacional.mesmo.codigo", responsavel);
            } catch (FindException ex) {
            }

	    	EnderecoConjHabitacional EnderecoConjHabitacional = EnderecoConjuntoHabitacionalHome.create(endereco.getConsignataria(), endereco.getEchIdentificador(),
	    			endereco.getEchDescricao(), endereco.getEchCondominio(), endereco.getEchQtdUnidades());

	    	endCodigo = EnderecoConjHabitacional.getEchCodigo();

	        LogDelegate logDelegate = new LogDelegate(responsavel, Log.ENDERECO_CONJUNTO_HABITACIONAL, Log.CREATE, Log.LOG_INFORMACAO);
	        logDelegate.setEndereco(endCodigo);
	        logDelegate.getUpdatedFields(endereco.getAtributos(), null);
	        logDelegate.write();

	    } catch (LogControllerException ex) {
	        LOG.error(ex.getMessage(), ex);
	        throw new EnderecoConjuntoHabitacionalControllerException("mensagem.erroInternoSistema", responsavel, ex);
	    } catch (com.zetra.econsig.exception.CreateException ex) {
	        LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
	        throw new EnderecoConjuntoHabitacionalControllerException("mensagem.erro.incluir.endereco.conjunto.habitacional.arg0", responsavel, ex.getMessage());
	    }

    	return endCodigo;
    }

    @Override
    public EnderecoTransferObject buscaEnderecoByPK(EnderecoTransferObject endereco, AcessoSistema responsavel) throws EnderecoConjuntoHabitacionalControllerException{
    	try {
			return setEnderecoTOValues(EnderecoConjuntoHabitacionalHome.findByPrimaryKey(endereco.getEchCodigo()));
		} catch (FindException ex) {
			LOG.error(ex.getMessage(), ex);
	        throw new EnderecoConjuntoHabitacionalControllerException("mensagem.erroInternoSistema", responsavel, ex);
		}
    }

    @Override
    public String updateEndereco(EnderecoTransferObject enderecoTO, AcessoSistema responsavel) throws EnderecoConjuntoHabitacionalControllerException {
    	try{
    		validaEndereco(enderecoTO, responsavel);

            try {
                EnderecoConjuntoHabitacionalHome.findByIdn(enderecoTO.getEchIdentificador(), enderecoTO.getConsignataria(), enderecoTO.getEchCodigo());
                throw new EnderecoConjuntoHabitacionalControllerException("mensagem.erro.alterar.endereco.conjunto.habitacional.mesmo.codigo", responsavel);
            } catch (FindException ex) {
            }

            String echCodigo = enderecoTO.getEchCodigo();

    		LogDelegate logDelegate = new LogDelegate(responsavel, Log.ENDERECO_CONJUNTO_HABITACIONAL, Log.UPDATE, Log.LOG_INFORMACAO);
    		logDelegate.setEndereco(echCodigo);

    		EnderecoConjHabitacional end = EnderecoConjuntoHabitacionalHome.findByPrimaryKey(echCodigo);

            TransferObject enderecoCache = setEnderecoTOValues(end);
            CustomTransferObject merge = logDelegate.getUpdatedFields(enderecoTO.getAtributos(), enderecoCache.getAtributos());

            if (merge.getAtributos().containsKey(Columns.ECH_IDENTIFICADOR)) {
            	end.setEchIdentificador((String) merge.getAttribute(Columns.ECH_IDENTIFICADOR));
            }

            if (merge.getAtributos().containsKey(Columns.ECH_DESCRICAO)) {
            	end.setEchDescricao((String) merge.getAttribute(Columns.ECH_DESCRICAO));
            }

            if (merge.getAtributos().containsKey(Columns.ECH_CONDOMINIO)) {
            	end.setEchCondominio((String) merge.getAttribute(Columns.ECH_CONDOMINIO));

            	ListaDespesaTaxaUsoAtualizacaoQuery query = new ListaDespesaTaxaUsoAtualizacaoQuery();
                query.echCodigo = echCodigo;
                List<TransferObject> lista = query.executarDTO();
                Iterator<TransferObject> iterator = lista.iterator();

                while(iterator.hasNext()){
                    TransferObject adeTaxaUso = iterator.next();
                    String adeCodigo = adeTaxaUso.getAttribute(Columns.ADE_CODIGO).toString();
                    BigDecimal valorTaxaUsoAtual = new BigDecimal(adeTaxaUso.getAttribute(Columns.ADE_VLR).toString());

                    // Calculo do valor da taxa de uso deve ser considerado o posto do permissionario
                    // Se o endereço for condomínio, utiliza a taxa de uso de condomínio
                    RegistroServidor registroServidor = RegistroServidorHome.findByPrimaryKey((String) adeTaxaUso.getAttribute(Columns.RSE_CODIGO));
                    PostoRegistroServidor posto = PostoRegistroServidorHome.findByPrimaryKey(registroServidor.getPostoRegistroServidor().getPosCodigo());
                    BigDecimal valorSoldo = posto.getPosVlrSoldo();
                    BigDecimal taxaUso = end.getEchCondominio().equals(CodedValues.TPC_SIM) ? posto.getPosPercTxUsoCond() : posto.getPosPercTxUso();

                    BigDecimal valorTaxaUso = valorSoldo.multiply(taxaUso).divide(new BigDecimal(100), 2, java.math.RoundingMode.DOWN);

                    // Se o novo valor da taxa de uso for diferente do valor atual, atualiza o valor da taxa de uso
                    if (valorTaxaUso.compareTo(valorTaxaUsoAtual) != 0) {
                        if (valorTaxaUso.compareTo(BigDecimal.ZERO) < 0) {
                            throw new EnderecoConjuntoHabitacionalControllerException("mensagem.erro.alterar.endereco.conjunto.habitacional.calculo.taxa", responsavel);
                        }
                        // Alterar o valor da taxa de uso
                        despesaIndividualController.alterarTaxaUso(adeCodigo, valorTaxaUso, responsavel);
                    }
                }
            }

            if (merge.getAtributos().containsKey(Columns.ECH_QTD_UNIDADES)) {
            	end.setEchQtdUnidades((Short) merge.getAttribute(Columns.ECH_QTD_UNIDADES));
            }

    		EnderecoConjuntoHabitacionalHome.update(end);

	        logDelegate.write();

	        return echCodigo;

	    } catch (LogControllerException | FindException ex) {
	        LOG.error(ex.getMessage(), ex);
	        throw new EnderecoConjuntoHabitacionalControllerException("mensagem.erroInternoSistema", responsavel, ex);
	    } catch (com.zetra.econsig.exception.UpdateException | DespesaIndividualControllerException | HQueryException ex) {
	        LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
	        throw new EnderecoConjuntoHabitacionalControllerException("mensagem.erro.alterar.endereco.conjunto.habitacional.arg0", responsavel, ex.getMessage());
        }
    }

    private EnderecoTransferObject setEnderecoTOValues(EnderecoConjHabitacional enderecoConjHabitacional) {
    	EnderecoTransferObject end = new EnderecoTransferObject(enderecoConjHabitacional.getEchCodigo());
    	end.setEchCondominio(enderecoConjHabitacional.getEchCondominio());
    	end.setEchDescricao(enderecoConjHabitacional.getEchDescricao());
    	end.setEchIdentificador(enderecoConjHabitacional.getEchIdentificador());
    	end.setEchQtdUnidades(enderecoConjHabitacional.getEchQtdUnidades());
        return end;
    }

    private void validaEndereco(EnderecoTransferObject endereco, AcessoSistema responsavel) throws EnderecoConjuntoHabitacionalControllerException{
    	if(endereco != null){
    		if(TextHelper.isNull(endereco.getEchIdentificador())){
    			throw new EnderecoConjuntoHabitacionalControllerException("mensagem.informe.ech.identificador", responsavel);
    		}

    		if(TextHelper.isNull(endereco.getEchDescricao())){
    			throw new EnderecoConjuntoHabitacionalControllerException("mensagem.informe.ech.descricao", responsavel);
    		}

    		if(TextHelper.isNull(endereco.getEchQtdUnidades()) || endereco.getEchQtdUnidades() <= 0){
    			throw new EnderecoConjuntoHabitacionalControllerException("mensagem.informe.ech.qtd.unidade", responsavel);
    		}
    	}
    }
}