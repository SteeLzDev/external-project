package com.zetra.econsig.service.convenio;


import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConvenioTransferObject;
import com.zetra.econsig.dto.entidade.GrupoServicoTransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcCseTO;
import com.zetra.econsig.dto.entidade.ParamTarifCseTO;
import com.zetra.econsig.dto.entidade.ServicoTransferObject;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ConvenioController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ConvenioController  {
    // Convenio
    public List<String> createConvenio(String svcCodigo, String csaCodigo, String orgCodigo, String vrbConvenio, String vrbConvenioRef, String vrbConvenioFerias, String vrbConvenioDirf, AcessoSistema responsavel) throws ConvenioControllerException ;

    public List<String> createConvenio(String svcCodigo, String csaCodigo, String orgCodigo, String vrbConvenio, String vrbConvenioRef, String vrbConvenioFerias, AcessoSistema responsavel) throws ConvenioControllerException;

    public void setParamQuantidadeDefault(List<String> convenios) throws ConvenioControllerException;

    public List<TransferObject> getCnvConsolidaDescontos(String svcCodigo, String orgCodigo, AcessoSistema responsavel) throws ConvenioControllerException;

    public void setCnvConsolidaDescontos(String svcCodigo, String orgCodigo, String cnvConsolidaDescontos, AcessoSistema responsavel) throws ConvenioControllerException;

    public List<TransferObject> getSvcByCodVerbaSvcIdentificador(String svcIdentificador, String cnvCodVerba, String orgCodigo, String csaCodigo, boolean ativo, AcessoSistema responsavel) throws ConvenioControllerException;

    public List<TransferObject> getCnvCodVerba(String svcCodigo, String csaCodigo, AcessoSistema responsavel) throws ConvenioControllerException;

    public List<TransferObject> getCnvCodVerbaInativo(String svcCodigo, String csaCodigo, AcessoSistema responsavel) throws ConvenioControllerException;

    public List<TransferObject> recuperaCsaCodVerba(String csaCodigo, boolean incluiCnvBloqueados, AcessoSistema responsavel) throws ConvenioControllerException;

    public List<TransferObject> getCsaCodVerbaReajuste(String csaCodigo, AcessoSistema responsavel) throws ConvenioControllerException;

    public void setCnvPrioridade(String cnvCodVerba, String cnvPrioridade, AcessoSistema responsavel) throws ConvenioControllerException;

    public List<TransferObject> getCsaCnvAtivo(String svcCodigo, String orgCodigo, AcessoSistema responsavel) throws ConvenioControllerException;

    public List<TransferObject> getCsaCnvAtivo(String svcCodigo, String orgCodigo, boolean csaDeveSerAtiva, AcessoSistema responsavel) throws ConvenioControllerException;

    public List<TransferObject> getCsaCnvAtivo(String svcCodigo, String orgCodigo, boolean csaDeveSerAtiva, boolean listagemReserva, AcessoSistema responsavel) throws ConvenioControllerException;

    public List<TransferObject> getOrgCnvAtivo(String csaCodigo, String corCodigo, AcessoSistema responsavel) throws ConvenioControllerException;

    public List<TransferObject> getCnvScvCodigo(String svcCodigo, String csaCodigo, String orgCodigo, AcessoSistema responsavel) throws ConvenioControllerException;

    public void setCnvScvCodigo(String svcCodigo, String csaCodigo, String orgCodigo, String scvCodigo, boolean limpaVerba, List<String> codigos, AcessoSistema responsavel) throws ConvenioControllerException;

    public List<TransferObject> listCnvScvCodigo(TransferObject criterio, AcessoSistema responsavel) throws ConvenioControllerException;

    public List<TransferObject> listCnvScvCodigo(TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws ConvenioControllerException;

    public int countCnvScvCodigo(TransferObject criterio, AcessoSistema responsavel) throws ConvenioControllerException;

    public CustomTransferObject getParamCnv(String cnvCodigo, AcessoSistema responsavel) throws ConvenioControllerException;

    public CustomTransferObject getParamCnv(String cnvCodigo, boolean cnvAtivo, boolean svcAtivo, AcessoSistema responsavel) throws ConvenioControllerException;

    public CustomTransferObject getParamCnv(String csaCodigo, String orgCodigo, String svcCodigo, AcessoSistema responsavel) throws ConvenioControllerException;

    public CustomTransferObject getParamCnv(String csaCodigo, String orgCodigo, String svcCodigo, boolean cnvAtivo, boolean svcAtivo, AcessoSistema responsavel) throws ConvenioControllerException;

    public List<TransferObject> lstConvenios(String cnvCodVerba, String csaCodigo, String svcCodigo, String orgCodigo, boolean ativo, AcessoSistema responsavel) throws ConvenioControllerException;

    public List<TransferObject> getCsaCodVerba(String csaCodigo, AcessoSistema responsavel) throws ConvenioControllerException;

    public List<TransferObject> lstCnvBySvcCodigo(String svcCodigo, String cnvCodVerba, int offset, int size, AcessoSistema responsavel) throws ConvenioControllerException;

    public int countCnvBySvcCodigo(String svcCodigo, AcessoSistema responsavel) throws ConvenioControllerException;

    public List<Map<String, Object>> lstConvenioPorNseOrgServidor(String csaCodigo, String nseCodigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCpf, AcessoSistema responsavel) throws ConvenioControllerException;

    // Serviço
    public ServicoTransferObject findServico(String svcCodigo, AcessoSistema responsavel) throws ConvenioControllerException;

    public ServicoTransferObject findServicoByIdn(String svcIdentificador, AcessoSistema responsavel) throws ConvenioControllerException;

    public ServicoTransferObject findServico(ServicoTransferObject criterio, AcessoSistema responsavel) throws ConvenioControllerException;

    public ServicoTransferObject findServicoByAdeCodigo(String adeCodigo, AcessoSistema responsavel) throws ConvenioControllerException;

    public String createServico(ServicoTransferObject servico, AcessoSistema responsavel) throws ConvenioControllerException;

    public String createServico(ServicoTransferObject servico, String svcCopia, String cnvCopia, String paramSvcCsaCopia, String bloqueioCnvCopia, String bloqueioSvcCopia, AcessoSistema responsavel) throws ConvenioControllerException;

    public void updateServico(ServicoTransferObject servico, AcessoSistema responsavel) throws ConvenioControllerException;

    public void updateServico(ServicoTransferObject servico, List<ParamSvcCseTO> listaParamSvcCse, List<ParamTarifCseTO> listaParamTarifCse, Map<String, List<String>> relacionamentos, AcessoSistema responsavel) throws ConvenioControllerException;

    public void removeServico(ServicoTransferObject criterio, AcessoSistema responsavel) throws ConvenioControllerException;

    public void copiaServico(String svcCopia, ServicoTransferObject servico, AcessoSistema responsavel) throws ConvenioControllerException;

    public List<TransferObject> lstServicos(TransferObject criterio, AcessoSistema responsavel) throws ConvenioControllerException;

    public List<TransferObject> lstServicos(TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws ConvenioControllerException;
    
    public List<TransferObject> lstServicos(TransferObject criterio, int offset, int count, boolean orderByList, AcessoSistema responsavel) throws ConvenioControllerException;

    public int countServicos(TransferObject criterio, AcessoSistema responsavel) throws ConvenioControllerException;

    //Consignatárias
    public List<TransferObject> lstConsignatarias(TransferObject criterio, AcessoSistema responsavel) throws ConvenioControllerException;

    public List<TransferObject> lstConsignatarias(TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws ConvenioControllerException;

    //Grupos de Serviços
    public GrupoServicoTransferObject findGrupoServico(String tgsCodigo, AcessoSistema responsavel) throws ConvenioControllerException;

    public GrupoServicoTransferObject findGrupoServico(GrupoServicoTransferObject grupoServico, AcessoSistema responsavel) throws ConvenioControllerException;

    public String createGrupoServico(GrupoServicoTransferObject grupoServico, AcessoSistema responsavel) throws ConvenioControllerException;

    public void updateGrupoServico(GrupoServicoTransferObject grupoServico, AcessoSistema responsavel) throws ConvenioControllerException;

    public void removeGrupoServico(GrupoServicoTransferObject grupoServico, AcessoSistema responsavel) throws ConvenioControllerException;

    public List<TransferObject> lstGrupoServicos(boolean orderById, AcessoSistema responsavel) throws ConvenioControllerException;

    // Convênio Correspondente
    public void criaConvenioCorrespondente(String corCodigo, String svcCodigo, List<String> cnvCodigos, AcessoSistema responsavel) throws ConvenioControllerException;

    public void criaConvenioCorrespondente(List<String> corCodigos, String cnvCodigo, AcessoSistema responsavel) throws ConvenioControllerException;

    public List<TransferObject> listCnvCorrespondente(String corCodigo, AcessoSistema responsavel) throws ConvenioControllerException;

    public List<TransferObject> listCnvCorrespondenteByCsa(String csaCodigo, String orgCodigo, AcessoSistema responsavel) throws ConvenioControllerException;

    public Map<String, List<String>> getCorrespondentePorCnvCodVerba(String csaCodigo, boolean filtraPorCnvCodVerbaRef, boolean filtraPorCnvCodVerbaFerias, AcessoSistema responsavel) throws ConvenioControllerException;

    public List<TransferObject> listOrgCnvCorrespondente(String corCodigo, String svcCodigo, AcessoSistema responsavel) throws ConvenioControllerException;

    public List<TransferObject> listCorCnvOrgao(String cnvCodigo, AcessoSistema responsavel) throws ConvenioControllerException;

    public List<TransferObject> lstCnvEntidade(String codEntidade, String tipoEntidade, String tipo, AcessoSistema responsavel) throws ConvenioControllerException;

    public List<String> csaPorCodVerba(String codVerba, String csaCodigo) throws ConvenioControllerException;

    public void bloqueiaCnvCor(String corCodigo, AcessoSistema responsavel)throws ConvenioControllerException;

    public void desBloqueiaCnvCor(String corCodigo, AcessoSistema responsavel)throws ConvenioControllerException;

    public List<List<String>> createConvenios(String svcCodigo, String csaCodigo, String orgCodigo, List<TransferObject> cnvACriar, String tmoCodigo, String ocoObs, AcessoSistema responsavel)throws ConvenioControllerException;

    public void criaConveniosParaNovoOrgao(String orgCodigo, String estCodigo, String orgCopiado, AcessoSistema responsavel)throws ConvenioControllerException;

    public List<TransferObject> lstCodEntidadesCnvNotInList(String csaCodigo, String orgCodigo, String svcCodigo, List<String> codigosAtivos, AcessoSistema responsavel) throws ConvenioControllerException;

    public Map<String, String> getMapCnvCodVerbaRef() throws ConvenioControllerException;

    public List<TransferObject> getCnvByIdentificadores(String csaIdentificador, String estIdentificador, String orgIdentificador, String svcIdentificador) throws ConvenioControllerException;

    public List<TransferObject> getCnvByIdentificadores(String csaIdentificador, String estIdentificador, String orgIdentificador, String svcIdentificador, String cnvCodVerba, AcessoSistema responsavel) throws ConvenioControllerException;

    public void updateCnvCodVerba() throws ConvenioControllerException;

    public Map<String, String> getMapCnvCodVerbaFerias() throws ConvenioControllerException;

    public ConvenioTransferObject findByUniqueKey(String csaCodigo, String svcCodigo, String orgCodigo, AcessoSistema responsavel) throws ConvenioControllerException;

    public ConvenioTransferObject findByPrimaryKey(String cnvCodigo, AcessoSistema responsavel) throws ConvenioControllerException;

    public List<String> lstConvenioRelIntegracao(String csaCodigo, AcessoSistema responsavel) throws ConvenioControllerException;

    public List<TransferObject> lstOcorrenciaConvenio(String svcCodigo, String csaCodigo, String orgCodigo, List<String> tocCodigos, int offset, int count, AcessoSistema responsavel) throws ConvenioControllerException;

    public int countOcorrenciaConvenio(String svcCodigo, String csaCodigo, String orgCodigo, List<String> tocCodigos, AcessoSistema responsavel) throws ConvenioControllerException;

    public void bloquearConveniosExpirados(AcessoSistema responsavel) throws ConvenioControllerException;

    public List<TransferObject> lstSvcCnvAtivos(String nseCodigo, String csaCodigo, boolean ativo, AcessoSistema responsavel)throws ConvenioControllerException;

    public List<TransferObject> ListaConveniosIncMargemCartaoReservaLancamento(Short marCodigo, boolean buscaCnvReservaCartao, AcessoSistema responsavel) throws ConvenioControllerException;
}