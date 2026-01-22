package com.zetra.econsig.delegate;

import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.Servico;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ServicoDelegate</p>
 * <p>Description: Delegate para manipulação de serviços</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ServicoDelegate extends AbstractDelegate {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ServicoDelegate.class);

    private ServicoController controller = null;

    public ServicoDelegate() throws ServicoControllerException {
        try {
            controller = ApplicationContextProvider.getApplicationContext().getBean(ServicoController.class);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServicoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    public List<TransferObject> selectServicosComParametro(String tpsCodigo, String orgCodigo, String csaCodigo, String pseVlr, boolean selectNull, AcessoSistema responsavel) throws ServicoControllerException {
        return this.selectServicosComParametro(tpsCodigo, null, orgCodigo, csaCodigo, pseVlr, selectNull, responsavel);
    }

    public List<TransferObject> selectServicosComParametro(String tpsCodigo, String svcCodigo, String orgCodigo, String csaCodigo, String pseVlr, boolean selectNull, AcessoSistema responsavel) throws ServicoControllerException {
        return controller.selectServicosComParametro(tpsCodigo, svcCodigo, orgCodigo, csaCodigo, pseVlr, selectNull, null, responsavel);
    }

    public List<TransferObject> selectServicosComParametro(String tpsCodigo, String svcCodigo, String orgCodigo, String csaCodigo, String pseVlr, boolean selectNull, String nseCodigo, AcessoSistema responsavel) throws ServicoControllerException {
        return controller.selectServicosComParametro(tpsCodigo, svcCodigo, orgCodigo, csaCodigo, pseVlr, selectNull, nseCodigo, responsavel);
    }

    public List<TransferObject> selectServicosComParametro(String tpsCodigo, String orgCodigo, String csaCodigo, String pseVlr, boolean selectNull, String nseCodigo, AcessoSistema responsavel) throws ServicoControllerException {
        return controller.selectServicosComParametro(tpsCodigo, null, orgCodigo, csaCodigo, pseVlr, selectNull, nseCodigo, responsavel);
    }

    public List<TransferObject> selectServicosComParametro(String tpsCodigo, String orgCodigo, String csaCodigo, List<String> pseVlrs, boolean selectNull, boolean ativos, AcessoSistema responsavel) throws ServicoControllerException {
        return controller.selectServicosComParametro(tpsCodigo, orgCodigo, AcessoSistema.ENTIDADE_CSE, csaCodigo, pseVlrs, selectNull, ativos, responsavel);
    }

    public List<TransferObject> selectServicosComParametro(String tpsCodigo, String svcCodigo, String orgCodigo, String csaCodigo, List<String> pseVlrs, boolean selectNull, String nseCodigo, AcessoSistema responsavel) throws ServicoControllerException {
        return controller.selectServicosComParametro(tpsCodigo, svcCodigo, orgCodigo, csaCodigo, pseVlrs, selectNull, nseCodigo, responsavel);
    }

    public List<String> lstTipoNaturezasRelSvc() throws ServicoControllerException {
        return controller.lstTipoNaturezasRelSvc();
    }

    public CustomTransferObject findServico(String svcCodigo) throws ServicoControllerException {
        return controller.findServico(svcCodigo);
    }

    public List<Servico> findByNseCodigo(String nseCodigo, AcessoSistema responsavel) throws ServicoControllerException {
        return controller.findByNseCodigo(nseCodigo, responsavel);
    }

    public List<TransferObject> selectServicosComParametroCorrespondente(String tpsCodigo, String svcCodigo, String orgCodigo, String corCodigo, String pseVlr, boolean selectNull, String nseCodigo, AcessoSistema responsavel) throws ServicoControllerException {
        return controller.selectServicosComParametroCorrespondente(tpsCodigo, svcCodigo, orgCodigo, corCodigo, pseVlr, selectNull, nseCodigo, responsavel);
    }

}
