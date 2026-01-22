package com.zetra.econsig.service.correspondente;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.CorrespondenteControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.Correspondente;
import com.zetra.econsig.persistence.entity.CorrespondenteHome;
import com.zetra.econsig.persistence.entity.EnderecoCorrespondente;
import com.zetra.econsig.persistence.entity.EnderecoCorrespondenteHome;
import com.zetra.econsig.persistence.entity.OcorrenciaCorrespondenteHome;
import com.zetra.econsig.persistence.entity.TipoEndereco;
import com.zetra.econsig.persistence.entity.TipoEnderecoHome;
import com.zetra.econsig.persistence.query.consignataria.ListaEnderecosCorrespondenteQuery;

/**
 * <p>Title: CorrespondenteControllerBean</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class CorrespondenteControllerBean implements CorrespondenteController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CorrespondenteControllerBean.class);

    @Override
    public int countEnderecoCorrespondenteByCorCodigo(String corCodigo, String csaCodigo, AcessoSistema responsavel) throws CorrespondenteControllerException {
        ListaEnderecosCorrespondenteQuery query = new ListaEnderecosCorrespondenteQuery();
        query.corCodigo = corCodigo;
        query.count = true;

        try {
            return query.executarContador();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new CorrespondenteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstEnderecoCorrespondenteByCorCodigo(String corCodigo, String csaCodigo, int count, int offset, AcessoSistema responsavel) throws CorrespondenteControllerException {
        ListaEnderecosCorrespondenteQuery query = new ListaEnderecosCorrespondenteQuery();
        query.corCodigo = corCodigo;
        query.csaCodigo = csaCodigo;

        if (count != -1) {
            query.maxResults = count;
        }

        if (offset != -1) {
            query.firstResult = offset;
        }

        try {
            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new CorrespondenteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public EnderecoCorrespondente findEnderecoCorrespondenteByPKCorCodigo(String ecrCodigo, String corCodigo, AcessoSistema responsavel) throws CorrespondenteControllerException {
        try {

            String csaCodigo = null;
            if (responsavel.isCsa()) {
                csaCodigo = responsavel.getCsaCodigo();
            }

            return EnderecoCorrespondenteHome.findByPrimaryKeyAndCorCodigo(ecrCodigo, csaCodigo, corCodigo);
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new CorrespondenteControllerException("mensagem.erro.endereco.correspondente.nao.encontrado", responsavel);
        }
    }

    @Override
    public List<TipoEndereco> listAllTipoEndereco(AcessoSistema responsavel) throws CorrespondenteControllerException {
        try {
            return TipoEnderecoHome.listAll();
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new CorrespondenteControllerException("mensagem.erro.tipo.endereco.correspondente.nao.encontrado", responsavel);
        }
    }

    private void verificarCorrespondentePertenceConsignataria(Correspondente cor, String csaCodigo, AcessoSistema responsavel) throws CorrespondenteControllerException {

        if (!csaCodigo.equals(cor.getConsignataria().getCsaCodigo())) {
            throw new CorrespondenteControllerException("mensagem.erro.endereco.correspondente.nao.pertence.consignataria", responsavel);
        }

    }

    @Override
    public EnderecoCorrespondente createEnderecoCorrespondente(String corCodigo, String tieCodigo, String ecrLogradouro, String ecrNumero, String ecrComplemento, String ecrBairro, String ecrMunicipio, String ecrUf, String ecrCep, BigDecimal ecrLatitude, BigDecimal ecrLongitude, AcessoSistema responsavel) throws CorrespondenteControllerException {
        try {

            if (responsavel.isCsa()) {
                Correspondente cor = CorrespondenteHome.findByPrimaryKey(corCodigo);
                verificarCorrespondentePertenceConsignataria(cor, responsavel.getCsaCodigo(), responsavel);
            }

            EnderecoCorrespondente enderecoCorrespondente = EnderecoCorrespondenteHome.create(corCodigo, tieCodigo, ecrLogradouro, ecrNumero, ecrComplemento, ecrBairro, ecrMunicipio, ecrUf, ecrCep, ecrLatitude, ecrLongitude);

            LogDelegate log = new LogDelegate(responsavel, Log.ENDERECO_CORRESPONDENTE, Log.CREATE, Log.LOG_INFORMACAO);
            log.setEnderecoCorrespondente(enderecoCorrespondente.getEcrCodigo());
            log.write();

            return enderecoCorrespondente;
        } catch (CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new CorrespondenteControllerException("mensagem.erro.endereco.correspondente.criar", responsavel, ex);
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new CorrespondenteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new CorrespondenteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public EnderecoCorrespondente updateEnderecoCorrespondente(String ecrCodigo, String corCodigo, String tieCodigo, String ecrLogradouro, String ecrNumero, String ecrComplemento, String ecrBairro, String ecrMunicipio, String ecrUf, String ecrCep, BigDecimal ecrLatitude, BigDecimal ecrLongitude, AcessoSistema responsavel) throws CorrespondenteControllerException {
        try {

            if (responsavel.isCsa()) {
                Correspondente cor = CorrespondenteHome.findByPrimaryKey(corCodigo);
                verificarCorrespondentePertenceConsignataria(cor, responsavel.getCsaCodigo(), responsavel);
            }

            EnderecoCorrespondente EnderecoCorrespondente = EnderecoCorrespondenteHome.update(ecrCodigo, corCodigo, tieCodigo, ecrLogradouro, ecrNumero, ecrComplemento, ecrBairro, ecrMunicipio, ecrUf, ecrCep, ecrLatitude, ecrLongitude);

            LogDelegate log = new LogDelegate(responsavel, Log.ENDERECO_CORRESPONDENTE, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setEnderecoCorrespondente(EnderecoCorrespondente.getEcrCodigo());
            log.write();

            return EnderecoCorrespondente;
        } catch (UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new CorrespondenteControllerException("mensagem.erro.endereco.correspondente.atualizar", responsavel, ex);
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new CorrespondenteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new CorrespondenteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public EnderecoCorrespondente removeEnderecoCorrespondente(String ecrCodigo, String corCodigo, AcessoSistema responsavel) throws CorrespondenteControllerException {
        try {

            if (responsavel.isCsa()) {
                Correspondente cor = CorrespondenteHome.findByPrimaryKey(corCodigo);
                verificarCorrespondentePertenceConsignataria(cor, responsavel.getCsaCodigo(), responsavel);
            }

            EnderecoCorrespondente enderecoCorrespondente = new EnderecoCorrespondente();
            enderecoCorrespondente.setEcrCodigo(ecrCodigo);
            Correspondente correspondente = new Correspondente();
            correspondente.setCorCodigo(corCodigo);
            enderecoCorrespondente.setCorrespondente(correspondente);
            EnderecoCorrespondenteHome.remove(enderecoCorrespondente);

            LogDelegate log = new LogDelegate(responsavel, Log.ENDERECO_CORRESPONDENTE, Log.DELETE, Log.LOG_INFORMACAO);
            log.setEnderecoCorrespondente(enderecoCorrespondente.getEcrCodigo());
            log.write();

            return enderecoCorrespondente;
        } catch (RemoveException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new CorrespondenteControllerException("mensagem.erro.endereco.correspondente.remover", responsavel, ex);
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new CorrespondenteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new CorrespondenteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void createOcorrenciaCorrespondente(String corCodigo, String tocCodigo, String ocrObs, String tmoCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        try {
            OcorrenciaCorrespondenteHome.create(corCodigo, responsavel.getUsuCodigo(), tocCodigo, ocrObs, tmoCodigo, responsavel.getIpUsuario());
        } catch (com.zetra.econsig.exception.CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConsignatariaControllerException("mensagem.erro.nao.possivel.criar.ocorrencia.erro.interno", responsavel, ex.getMessage());
        }
    }

    public Correspondente findCorrespondenteByPrimaryKey(String corCodigo, AcessoSistema responsavel) throws CorrespondenteControllerException {
        try {
            return CorrespondenteHome.findByPrimaryKey(corCodigo);
        } catch (FindException e) {
            LOG.error(e.getMessage(), e);
            throw new CorrespondenteControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

}
