package com.zetra.econsig.delegate;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.dto.entidade.EstabelecimentoTransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ConsignanteDelegate</p>
 * <p>Description: Delegate de Consignante e processos</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConsignanteDelegate extends AbstractDelegate {

    private ConsignanteController cseController = null;
    private SistemaController sistemaController = null;

    private ConsignanteController getConsignanteController() throws ConsignanteControllerException {
        try {
            if (cseController == null) {
                cseController = ApplicationContextProvider.getApplicationContext().getBean(ConsignanteController.class);
            }
            return cseController;
        } catch (final Exception ex) {
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    private SistemaController getSistemaController() throws ConsignanteControllerException {
        try {
            if (sistemaController == null) {
                sistemaController = ApplicationContextProvider.getApplicationContext().getBean(SistemaController.class);
            }
            return sistemaController;
        } catch (final Exception ex) {
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    // Órgãos
    private OrgaoTransferObject findOrgao(OrgaoTransferObject criterio, AcessoSistema responsavel) throws ConsignanteControllerException {

        return getConsignanteController().findOrgao(criterio, responsavel);

    }

    public OrgaoTransferObject findOrgao(String orgCodigo, AcessoSistema responsavel) throws ConsignanteControllerException {
        final OrgaoTransferObject criterio = new OrgaoTransferObject(orgCodigo);
        return findOrgao(criterio, responsavel);
    }

    public OrgaoTransferObject findOrgaoByIdn(String orgIdentificador, String estCodigo, AcessoSistema responsavel) throws ConsignanteControllerException {
        final OrgaoTransferObject criterio = new OrgaoTransferObject();
        criterio.setOrgIdentificador(orgIdentificador);
        criterio.setEstCodigo(estCodigo);
        return findOrgao(criterio, responsavel);
    }

    public String createOrgao(OrgaoTransferObject orgao, AcessoSistema responsavel) throws ConsignanteControllerException {

        return getConsignanteController().createOrgao(orgao, responsavel);

    }

    public String createOrgao(OrgaoTransferObject orgao, boolean criarConvenio, String orgCodigoACopiar, AcessoSistema responsavel) throws ConsignanteControllerException {
        return getConsignanteController().createOrgao(orgao, criarConvenio, orgCodigoACopiar, responsavel);
    }

    public List<TransferObject> lstOrgaos(TransferObject criterio, AcessoSistema responsavel) throws ConsignanteControllerException {
        return getConsignanteController().lstOrgaos(criterio, responsavel);
    }

    public List<TransferObject> lstOrgaos(TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws ConsignanteControllerException {
        return getConsignanteController().lstOrgaos(criterio, offset, count, responsavel);
    }

    public Map<String, Integer> getOrgDiaRepasse(String orgCodigo, AcessoSistema responsavel) throws ConsignanteControllerException {
        return getConsignanteController().getOrgDiaRepasse(orgCodigo, responsavel);
    }

    public List<OrgaoTransferObject> listarOrgaosDirf(AcessoSistema responsavel) throws ConsignanteControllerException {
        return getConsignanteController().listarOrgaosDirf(responsavel);
    }

    // Estabelecimento
    private EstabelecimentoTransferObject findEstabelecimento(EstabelecimentoTransferObject criterio, AcessoSistema responsavel) throws ConsignanteControllerException {
        return getConsignanteController().findEstabelecimento(criterio, responsavel);
    }

    public EstabelecimentoTransferObject findEstabelecimento(String estCodigo, AcessoSistema responsavel) throws ConsignanteControllerException {
        final EstabelecimentoTransferObject criterio = new EstabelecimentoTransferObject(estCodigo);
        return findEstabelecimento(criterio, responsavel);
    }

    public EstabelecimentoTransferObject findEstabelecimentoByIdn(String estIdentificador, AcessoSistema responsavel) throws ConsignanteControllerException {
        final EstabelecimentoTransferObject criterio = new EstabelecimentoTransferObject();
        criterio.setEstIdentificador(estIdentificador);
        return findEstabelecimento(criterio, responsavel);
    }

    public String createEstabelecimento(EstabelecimentoTransferObject estabelecimento, AcessoSistema responsavel) throws ConsignanteControllerException {
        return getConsignanteController().createEstabelecimento(estabelecimento, responsavel);
    }

    public List<TransferObject> lstEstabelecimentos(TransferObject criterio, AcessoSistema responsavel) throws ConsignanteControllerException {
        return getConsignanteController().lstEstabelecimentos(criterio, responsavel);
    }

    public List<TransferObject> lstEstabelecimentos(TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws ConsignanteControllerException {
        return getConsignanteController().lstEstabelecimentos(criterio, offset, count, responsavel);
    }

    public String dataUltimaAtualizacaoSistema() throws ConsignanteControllerException {
        return getConsignanteController().dataUltimaAtualizacaoSistema();
    }

    // Consignante
    private ConsignanteTransferObject findConsignante(ConsignanteTransferObject criterio, AcessoSistema responsavel) throws ConsignanteControllerException {
        return getConsignanteController().findConsignante(criterio, responsavel);
    }

    public ConsignanteTransferObject findConsignante(String cseCodigo, AcessoSistema responsavel) throws ConsignanteControllerException {
        final ConsignanteTransferObject criterio = new ConsignanteTransferObject(cseCodigo);
        return findConsignante(criterio, responsavel);
    }

    public ConsignanteTransferObject findConsignanteByIdn(String cseIdentificador, AcessoSistema responsavel) throws ConsignanteControllerException {
        final ConsignanteTransferObject criterio = new ConsignanteTransferObject();
        criterio.setCseIdentificador(cseIdentificador);
        return findConsignante(criterio, responsavel);
    }

    public void updateConsignante(ConsignanteTransferObject consignante, AcessoSistema responsavel) throws ConsignanteControllerException {
        getConsignanteController().updateConsignante(consignante, responsavel);
    }

    public void updateConsignante(ConsignanteTransferObject consignante, String msg, AcessoSistema responsavel) throws ConsignanteControllerException {
        getConsignanteController().updateConsignante(consignante, msg, responsavel);
    }

    public String findDadoAdicionalConsignante(String cseCodigo, String tdaCodigo, AcessoSistema responsavel) throws ConsignanteControllerException {
        return getConsignanteController().findDadoAdicionalConsignante(cseCodigo, tdaCodigo, responsavel);
    }

    /**
     * Obtém o nome do consignante
     * @param responsavel
     * @return
     * @throws ViewHelperException
     */
    public String getCseNome(AcessoSistema responsavel) throws ConsignanteControllerException {
        final ConsignanteTransferObject cse = findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
        return cse.getCseNome();
    }

    public Short verificaBloqueioSistema(String cseCodigo, AcessoSistema responsavel) throws ConsignanteControllerException {
        return getSistemaController().verificaBloqueioSistema(cseCodigo, responsavel);
    }

    public void alteraStatusSistema(String cseCodigo, Short status, String msg, AcessoSistema responsavel) throws ConsignanteControllerException {
        getSistemaController().alteraStatusSistema(cseCodigo, status, msg, responsavel);
    }

    public void alteraStatusSistema(String cseCodigo, Short status, String msg, boolean alteraMsgSistema, AcessoSistema responsavel) throws ConsignanteControllerException {
        getSistemaController().alteraStatusSistema(cseCodigo, status, msg, alteraMsgSistema, responsavel);
    }

    public void createOcorrenciaCse(String tocCodigo, AcessoSistema responsavel) throws ConsignanteControllerException {
        getConsignanteController().createOcorrenciaCse(tocCodigo, responsavel);
    }

    public void createOcorrenciaCse(String tocCodigo, String msg, AcessoSistema responsavel) throws ConsignanteControllerException {
        getConsignanteController().createOcorrenciaCse(tocCodigo, msg, responsavel);
    }

    public void enviaNotificacaoEnvioArquivosFolha(AcessoSistema responsavel) throws ConsignanteControllerException {
        getConsignanteController().enviaNotificacaoEnvioArquivosFolha(responsavel);
    }

    public void updateBancosCse(List<String> bcoCodigos, AcessoSistema responsavel) throws ConsignanteControllerException {
        getConsignanteController().setBancosCse(bcoCodigos);
    }

    public String getEmailCseNotificacaoOperacao(String funCodigo, String papCodigoOperador, String cseCodigo, AcessoSistema responsavel) throws ConsignanteControllerException {
        return getConsignanteController().getEmailCseNotificacaoOperacao(funCodigo, papCodigoOperador, cseCodigo, responsavel);
    }
}
