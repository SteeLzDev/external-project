package com.zetra.econsig.service.servico;


import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.Servico;

/**
 * <p>Title: ServicoController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ServicoController  {

    public List<TransferObject> selectServicosOrgao(String orgCodigo, AcessoSistema responsavel) throws ServicoControllerException;

    public List<TransferObject> selectServicosCorrespondente(String corCodigo, AcessoSistema responsavel) throws ServicoControllerException;

    public List<TransferObject> selectServicosCsa(String csaCodigo, AcessoSistema responsavel) throws ServicoControllerException;

    public List<TransferObject> selectServicosComParametro(String tpsCodigo, String orgCodigo, String csaCodigo, String pseVlr, boolean selectNull, AcessoSistema responsavel) throws ServicoControllerException;

    public List<TransferObject> selectServicosComParametro(String tpsCodigo, String svcCodigo, String orgCodigo, String csaCodigo, String pseVlr, boolean selectNull, AcessoSistema responsavel) throws ServicoControllerException;

    public List<TransferObject> selectServicosComParametro(String tpsCodigo, String svcCodigo, String orgCodigo, String csaCodigo, String pseVlr, boolean selectNull, String nseCodigo, AcessoSistema responsavel) throws ServicoControllerException;

    public List<TransferObject> selectServicosComParametro(String tpsCodigo, String orgCodigo, String csaCodigo, String pseVlr, boolean selectNull, String nseCodigo, AcessoSistema responsavel) throws ServicoControllerException;

    public List<TransferObject> selectServicosComParametro(String tpsCodigo, String orgCodigo, String entidade, String csaCodigo, List<String> pseVlrs, boolean selectNull, boolean ativos, AcessoSistema responsavel) throws ServicoControllerException;

    public List<TransferObject> selectServicosComParametro(String tpsCodigo, String svcCodigo, String orgCodigo, String csaCodigo, List<String> pseVlrs, boolean selectNull, String nseCodigo, AcessoSistema responsavel) throws ServicoControllerException;

    public List<TransferObject> lstServicosRenegociaveisServidor(String svcCodigo, String orgCodigo, String csaCodigo, String nseCodigo, AcessoSistema responsavel) throws ServicoControllerException;

    public List<TransferObject> selectServicosModuloAvancadoCompras(String orgCodigo, String csaCodigo, String nseCodigo) throws ServicoControllerException;

    public List<TransferObject> selectServicosParametroCompra(String orgCodigo, String csaCodigo, String nseCodigo) throws ServicoControllerException;

    public List<TransferObject> selectServicosParametroRenegociacao(String orgCodigo, String csaCodigo, String nseCodigo) throws ServicoControllerException;

    public List<TransferObject> selectServicosCancelamentoAutomatico(String orgCodigo, String csaCodigo, String nseCodigo) throws ServicoControllerException;

    public List<String> obtemServicoRelacionadoComConvenioAtivo(String svcCodigoOrigem, String csaCodigo, String orgCodigo, String tntCodigo, AcessoSistema responsavel) throws ServicoControllerException;

    public String selectServicoMaxParametro(String tpsCodigo, String nseCodigo, boolean ativos) throws ServicoControllerException;

    public List<TransferObject> lstNaturezasServicos(boolean orderById) throws ServicoControllerException;

    public List<TransferObject> lstNaturezasServicos(boolean orderById, boolean naturezaBeneficio) throws ServicoControllerException;

    public List<TransferObject> lstNaturezasServicos(String orgCodigo, boolean orderById, boolean naturezaBeneficio) throws ServicoControllerException;

    public List<TransferObject> lstNaturezasServicosBeneficios(boolean orderById) throws ServicoControllerException;

    public List<String> lstTipoNaturezasRelSvc() throws ServicoControllerException;

    public CustomTransferObject findServico(String svcCodigo) throws ServicoControllerException;

    public CustomTransferObject findNaturezaServico(String svcCodigo, AcessoSistema responsavel) throws ServicoControllerException;

    public List<TransferObject> lstOcorrenciaServico(String svcCodigo, List<String> tocCodigos, int offset, int count, AcessoSistema responsavel) throws ServicoControllerException;

    public int countOcorrenciaServico(String svcCodigo, List<String> tocCodigos, AcessoSistema responsavel) throws ServicoControllerException;

    public List<Servico> findByNseCodigo(String nseCodigo, AcessoSistema responsavel) throws ServicoControllerException;

    public List<TransferObject> lstServicoByNaturezas(List<String> nseCodigos, AcessoSistema responsavel) throws ServicoControllerException;

    public List<TransferObject> listaRelacionamentoServicosPorTipoNatureza(String servicoOrigem, String servicoDestinho, String tipoNatureza, AcessoSistema responsavel) throws ServicoControllerException;

    public List<TransferObject> selectServicosComParametroCorrespondente(String tpsCodigo, String svcCodigo, String orgCodigo, String corCodigo, String pseVlr, boolean selectNull, String nseCodigo, AcessoSistema responsavel) throws ServicoControllerException;

    public List<TransferObject> lstServicosByNseCsa(String nseCodigo, String csaCodigo, AcessoSistema responsavel) throws ServicoControllerException;
}
