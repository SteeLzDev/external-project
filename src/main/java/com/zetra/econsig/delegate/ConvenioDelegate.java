package com.zetra.econsig.delegate;

import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConvenioTransferObject;
import com.zetra.econsig.dto.entidade.ServicoTransferObject;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ConvenioDelegate</p>
 * <p>Description: Delegate para manipulação de Convênios</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConvenioDelegate extends AbstractDelegate {
    private ConvenioController convenioController = null;

    public ConvenioDelegate() {
    }

    private ConvenioController getConvenioController() throws ConvenioControllerException {
        try {
            if (convenioController == null) {
                convenioController = ApplicationContextProvider.getApplicationContext().getBean(ConvenioController.class);
            }
            return convenioController;
        } catch (Exception ex) {
            throw new ConvenioControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    public List<String> createConvenio(String svcCodigo, String csaCodigo, String orgCodigo, String vrbConvenio, String vrbConvenioRef, String vrbConvenioFerias, AcessoSistema responsavel) throws ConvenioControllerException {
        return getConvenioController().createConvenio(svcCodigo, csaCodigo, orgCodigo, vrbConvenio, vrbConvenioRef, vrbConvenioFerias, null, responsavel);
    }

    public List<String> createConvenio(String svcCodigo, String csaCodigo, String orgCodigo, String vrbConvenio, String vrbConvenioRef, String vrbConvenioFerias, String vrbConvenioDirf, AcessoSistema responsavel) throws ConvenioControllerException {
        return getConvenioController().createConvenio(svcCodigo, csaCodigo, orgCodigo, vrbConvenio, vrbConvenioRef, vrbConvenioFerias, vrbConvenioDirf, responsavel);
    }

    public List<TransferObject> getSvcByCodVerbaSvcIdentificador(String svcIdentificador, String cnvCodVerba, String orgCodigo, String csaCodigo, boolean ativo, AcessoSistema responsavel) throws ConvenioControllerException {
        return getConvenioController().getSvcByCodVerbaSvcIdentificador(svcIdentificador, cnvCodVerba, orgCodigo, csaCodigo, ativo, responsavel);
    }

    public List<TransferObject> getCsaCnvAtivo(String svcCodigo, String orgCodigo, AcessoSistema responsavel) throws ConvenioControllerException {
        return getConvenioController().getCsaCnvAtivo(svcCodigo, orgCodigo, responsavel);
    }

    public List<TransferObject> getCsaCnvAtivo(String svcCodigo, String orgCodigo, boolean csaDeveSerAtiva, AcessoSistema responsavel) throws ConvenioControllerException {
        return getConvenioController().getCsaCnvAtivo(svcCodigo, orgCodigo, csaDeveSerAtiva, responsavel);
    }

    public List<TransferObject> getCsaCnvAtivo(String svcCodigo, String orgCodigo, boolean csaDeveSerAtiva, boolean listagemReserva, AcessoSistema responsavel) throws ConvenioControllerException {
        return getConvenioController().getCsaCnvAtivo(svcCodigo, orgCodigo, csaDeveSerAtiva, listagemReserva, responsavel);
    }

    public CustomTransferObject getParamCnv(String cnvCodigo, AcessoSistema responsavel) throws ConvenioControllerException {
        return getParamCnv(cnvCodigo, true, true, responsavel);
    }

    public CustomTransferObject getParamCnv(String cnvCodigo, boolean cnvAtivo, boolean svcAtivo, AcessoSistema responsavel) throws ConvenioControllerException {
        return getConvenioController().getParamCnv(cnvCodigo, cnvAtivo, svcAtivo, responsavel);
    }

    public CustomTransferObject getParamCnv(String csaCodigo, String orgCodigo, String svcCodigo, AcessoSistema responsavel) throws ConvenioControllerException {
        return getParamCnv(csaCodigo, orgCodigo, svcCodigo, true, true, responsavel);
    }

    public CustomTransferObject getParamCnv(String csaCodigo, String orgCodigo, String svcCodigo, boolean cnvAtivo, boolean svcAtivo, AcessoSistema responsavel) throws ConvenioControllerException {
        return getConvenioController().getParamCnv(csaCodigo, orgCodigo, svcCodigo, cnvAtivo, svcAtivo, responsavel);
    }

    public List<TransferObject> lstConvenios(String cnvCodVerba, String csaCodigo, String svcCodigo, String orgCodigo, boolean ativo, AcessoSistema responsavel) throws ConvenioControllerException {
        return getConvenioController().lstConvenios(cnvCodVerba, csaCodigo, svcCodigo, orgCodigo, ativo, responsavel);
    }

    public List<TransferObject> recuperaCsaCodVerba(String csaCodigo, boolean incluiCnvBloqueados, AcessoSistema responsavel) throws ConvenioControllerException {
        return getConvenioController().recuperaCsaCodVerba(csaCodigo, incluiCnvBloqueados, responsavel);
    }

    // Serviços
    private ServicoTransferObject findServico(ServicoTransferObject criterio, AcessoSistema responsavel) throws ConvenioControllerException {
        return getConvenioController().findServico(criterio, responsavel);
    }

    public ServicoTransferObject findServico(String svcCodigo, AcessoSistema responsavel) throws ConvenioControllerException {
        ServicoTransferObject criterio = new ServicoTransferObject(svcCodigo);
        return findServico(criterio, responsavel);
    }

    public ServicoTransferObject findServicoByIdn(String svcIdentificador, AcessoSistema responsavel) throws ConvenioControllerException {
        ServicoTransferObject criterio = new ServicoTransferObject();
        criterio.setSvcIdentificador(svcIdentificador);
        return findServico(criterio, responsavel);
    }

    public ServicoTransferObject findServicoByAdeCodigo(String adeCodigo, AcessoSistema responsavel) throws ConvenioControllerException {
        return getConvenioController().findServicoByAdeCodigo(adeCodigo, responsavel);
    }

    public List<TransferObject> lstServicos(TransferObject criterio, AcessoSistema responsavel) throws ConvenioControllerException {
        return getConvenioController().lstServicos(criterio, responsavel);
    }
    
    public List<TransferObject> lstServicos(TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws ConvenioControllerException {
        return getConvenioController().lstServicos(criterio, offset, count, responsavel);
    }

    public List<TransferObject> lstServicos(TransferObject criterio, int offset, int count, boolean orderByList, AcessoSistema responsavel) throws ConvenioControllerException {
        return getConvenioController().lstServicos(criterio, offset, count, orderByList, responsavel);
    }

    public List<TransferObject> lstCnvEntidade(String codEntidade, String tipoEntidade, String tipo, AcessoSistema responsavel) throws ConvenioControllerException {
        return getConvenioController().lstCnvEntidade(codEntidade, tipoEntidade, tipo, responsavel);
    }

    public String createServico(ServicoTransferObject servico, AcessoSistema responsavel) throws ConvenioControllerException {
        return getConvenioController().createServico(servico, responsavel);
    }

    public List<String> csaPorCodVerba(String codVerba, String csaCodigo) throws ConvenioControllerException {
        return getConvenioController().csaPorCodVerba(codVerba, csaCodigo);
    }

    public void criaConveniosParaNovoOrgao(String orgCodigo, String estCodigo, String orgCopiado, AcessoSistema responsavel) throws ConvenioControllerException {
        getConvenioController().criaConveniosParaNovoOrgao(orgCodigo, estCodigo, orgCopiado, responsavel);
    }

    public List<TransferObject> getCnvByIdentificadores(String csaIdentificador, String estIdentificador, String orgIdentificador, String svcIdentificador, String cnvCodVerba, AcessoSistema responsavel) throws ConvenioControllerException {
        return getConvenioController().getCnvByIdentificadores(csaIdentificador, estIdentificador, orgIdentificador, svcIdentificador, cnvCodVerba, responsavel);
    }

    public ConvenioTransferObject findByUniqueKey(String csaCodigo, String svcCodigo, String orgCodigo, AcessoSistema responsavel) throws ConvenioControllerException {
        return getConvenioController().findByUniqueKey(csaCodigo, svcCodigo, orgCodigo, responsavel);
    }

    public ConvenioTransferObject findByPrimaryKey(String cnvCodigo, AcessoSistema responsavel) throws ConvenioControllerException {
        return getConvenioController().findByPrimaryKey(cnvCodigo, responsavel);
    }

    public void bloquearConveniosExpirados(AcessoSistema responsavel) throws ConvenioControllerException {
        getConvenioController().bloquearConveniosExpirados(responsavel);
    }
}
