package com.zetra.econsig.delegate;

import java.util.Date;
import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.CorrespondenteTransferObject;
import com.zetra.econsig.dto.entidade.GrupoConsignatariaTransferObject;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.Consignataria;
import com.zetra.econsig.persistence.entity.CredenciamentoCsa;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ConsignatariaDelegate</p>
 * <p>Description: Delegate para operações de manipulação de consignatárias e correspondentes</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConsignatariaDelegate extends AbstractDelegate {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConsignatariaDelegate.class);

    private ConsignatariaController consig = null;

    public ConsignatariaDelegate() throws ConsignatariaControllerException {
        try {
            consig = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignatariaControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    // Correspondente
    private CorrespondenteTransferObject findCorrespondente(CorrespondenteTransferObject criterio, AcessoSistema responsavel) throws ConsignatariaControllerException {

        return consig.findCorrespondente(criterio, responsavel);

    }

    public CorrespondenteTransferObject findCorrespondente(String corCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        CorrespondenteTransferObject criterio = new CorrespondenteTransferObject(corCodigo);
        return findCorrespondente(criterio, responsavel);
    }

    public CorrespondenteTransferObject findCorrespondenteByIdn(String corIdentificador, String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        CorrespondenteTransferObject criterio = new CorrespondenteTransferObject();
        criterio.setCorIdentificador(corIdentificador);
        criterio.setCsaCodigo(csaCodigo);
        return findCorrespondente(criterio, responsavel);
    }

    public int countCorrespondentes(TransferObject criterio, AcessoSistema responsavel) throws ConsignatariaControllerException {
        return consig.countCorrespondentes(criterio, responsavel);
    }

    // Consignataria
    private ConsignatariaTransferObject findConsignataria(ConsignatariaTransferObject criterio, AcessoSistema responsavel) throws ConsignatariaControllerException {
        return consig.findConsignataria(criterio, responsavel);
    }

    public ConsignatariaTransferObject findConsignataria(String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        ConsignatariaTransferObject criterio = new ConsignatariaTransferObject(csaCodigo);
        return findConsignataria(criterio, responsavel);
    }

    public ConsignatariaTransferObject findConsignatariaByIdn(String csaIdentificador, AcessoSistema responsavel) throws ConsignatariaControllerException {
        ConsignatariaTransferObject criterio = new ConsignatariaTransferObject();
        criterio.setCsaIdentificador(csaIdentificador);
        return findConsignataria(criterio, responsavel);
    }

    public List<Consignataria> findConsignatariaComEmailCadastrado(AcessoSistema responsavel) throws ConsignatariaControllerException {
        return consig.findConsignatariaComEmailCadastrado(responsavel);
    }

    //Consignatárias
    public String createConsignataria(ConsignatariaTransferObject consignataria, AcessoSistema responsavel) throws ConsignatariaControllerException {
        return consig.createConsignataria(consignataria, responsavel);
    }

    public List<TransferObject> lstConsignatarias(TransferObject criterio, AcessoSistema responsavel) throws ConsignatariaControllerException {
        return consig.lstConsignatarias(criterio, responsavel);
    }

    public List<TransferObject> lstConsignatarias(TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws ConsignatariaControllerException {
        return consig.lstConsignatarias(criterio, offset, count, responsavel);
    }

    public int countConsignatarias(TransferObject criterio, AcessoSistema responsavel) throws ConsignatariaControllerException {
        return consig.countConsignatarias(criterio, responsavel);
    }

    public void bloqueiaCsaExpiradas(AcessoSistema responsavel) throws ConsignatariaControllerException {
        consig.bloqueiaCsaExpiradas(responsavel);
    }

    public void bloqueiaCsaSolicitacaoSaldoPagoComAnexoNaoLiquidado(AcessoSistema responsavel) throws ConsignatariaControllerException {
        consig.bloqueiaCsaSolicitacaoSaldoPagoComAnexoNaoLiquidado(responsavel);
    }

    public void bloqueiaCsaMensagemNaoLida() throws ConsignatariaControllerException {
        consig.bloqueiaCsaMensagemNaoLida();
    }

    public void desbloqueiaCsa(ConsignatariaTransferObject consignataria, String observacao, String tpeCodigo, String tmoCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        consig.desbloqueiaCsa(consignataria, observacao, tpeCodigo, tmoCodigo, responsavel);
    }

    public void desbloqueiaCsaPenalidadeExpirada() throws ConsignatariaControllerException {
        consig.desbloqueiaCsaPenalidadeExpirada();
    }

    public void desbloqueiaCsaPrazoDesbloqAutomatico() throws ConsignatariaControllerException {
        consig.desbloqueiaCsaPrazoDesbloqAutomatico();
    }

    public boolean verificarDesbloqueioAutomaticoConsignataria(String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        return consig.verificarDesbloqueioAutomaticoConsignataria(csaCodigo, responsavel);
    }

    //Grupo de Consignatárias
    public GrupoConsignatariaTransferObject findGrupoCsaByIdentificador(String tgcIdentificador) throws ConsignatariaControllerException {
        return consig.findGrupoCsaByIdentificador(tgcIdentificador);
    }

    /**
     * lista consignatárias para as quais o servidor tem contratos (não importa o status do contrato).
     * @param serCodigo
     * @param responsavel
     * @return
     * @throws ConsignatariaControllerException
     */
    public List<TransferObject> lstConsignatariaSerTemAde(String serCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        return consig.lstConsignatariaSerTemAde(serCodigo, responsavel);
    }

    public List<TransferObject> lstConsignatariaSerTemAde(String serCodigo, String rseCodigo, boolean sadAtivos, AcessoSistema responsavel) throws ConsignatariaControllerException {
        return consig.lstConsignatariaSerTemAde(serCodigo, rseCodigo, sadAtivos, responsavel);
    }

    public List<ConsignatariaTransferObject> lstConsignatariasAExpirar(Date dataExpiracao, AcessoSistema responsavel) throws ConsignatariaControllerException {
        return consig.lstConsignatariasAExpirar(dataExpiracao, responsavel);
    }

    public List<Consignataria> lstConsignatariaProjetoInadimplencia() throws ConsignatariaControllerException {
        return consig.lstConsignatariaProjetoInadimplencia();
    }

    public void enviaEmailAlertaProximidadeCorte(AcessoSistema responsavel) throws ConsignatariaControllerException {
        consig.enviaEmailAlertaProximidadeCorte(responsavel);
    }

    public List<TransferObject> lstConsignatariaConvenio(TransferObject criterio, AcessoSistema responsavel) throws ConsignatariaControllerException {
        return consig.lstConsignatariaConvenio(criterio, responsavel);
    }

    public List<TransferObject> lstCorrespondenteConvenio(TransferObject criterio, AcessoSistema responsavel) throws ConsignatariaControllerException {
        return consig.lstCorrespondenteConvenio(criterio, responsavel);
    }

    public void enviarEmailAlertaRetornoServidor(AcessoSistema responsavel) throws ConsignatariaControllerException {
        consig.enviarEmailAlertaRetornoServidor(responsavel);
    }

    public void bloqueiaConsignatariasComAdeSemNumAnexosMin(Date dataIniVerificacao, AcessoSistema responsavel) throws ConsignatariaControllerException {
        consig.bloqueiaConsignatariasComAdeSemNumAnexosMin(dataIniVerificacao, responsavel);
    }

    public String getEmailCsaNotificacaoOperacao(String funCodigo, String papCodigoOperador, String csaCodigo, String svcCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        return consig.getEmailCsaNotificacaoOperacao(funCodigo, papCodigoOperador, csaCodigo, svcCodigo, responsavel);
    }

    public CredenciamentoCsa findByCsaCodigoCredenciamentoCsa(String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException {
        return consig.findByCsaCodigoCredenciamentoCsa(csaCodigo, responsavel);
    }
}
