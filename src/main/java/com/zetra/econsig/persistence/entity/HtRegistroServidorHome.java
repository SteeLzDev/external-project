package com.zetra.econsig.persistence.entity;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: RegistroServidorHome</p>
 * <p>Description: Classe Home para a entidade RegistroServidor</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class HtRegistroServidorHome extends AbstractEntityHome {

    public static HtRegistroServidor findByPrimaryKey(String rseCodigo) throws FindException {
        final HtRegistroServidor registroServidor = new HtRegistroServidor();
        registroServidor.setRseCodigo(rseCodigo);
        return find(registroServidor, rseCodigo);
    }

    public static HtRegistroServidor findByPrimaryKeyForUpdate(String rseCodigo) throws FindException {
        final HtRegistroServidor registroServidor = new HtRegistroServidor();
        registroServidor.setRseCodigo(rseCodigo);
        return find(registroServidor, rseCodigo, true);
    }

    public static HtRegistroServidor create(RegistroServidor registroServidor) throws CreateException {
        final Session session = SessionUtil.getSession();
        final HtRegistroServidor bean = new HtRegistroServidor();

        try {
            bean.setRseCodigo(registroServidor.getRseCodigo());
            bean.setRseAuditoriaTotal(registroServidor.getRseAuditoriaTotal());
            bean.setServidor(session.getReference(HtServidor.class, registroServidor.getServidor().getSerCodigo()));
            bean.setOrgao(session.getReference(Orgao.class, registroServidor.getOrgao().getOrgCodigo()));
            bean.setStatusRegistroServidor(session.getReference(StatusRegistroServidor.class, registroServidor.getStatusRegistroServidor().getSrsCodigo()));
            bean.setRseMatricula(registroServidor.getRseMatricula());
            bean.setRseMargem(registroServidor.getRseMargem());
            bean.setRseMargemRest(registroServidor.getRseMargemRest());
            bean.setRseMargemUsada(registroServidor.getRseMargemUsada());
            bean.setRseMargem2(registroServidor.getRseMargem2());
            bean.setRseMargemRest2(registroServidor.getRseMargemRest2());
            bean.setRseMargemUsada2(registroServidor.getRseMargemUsada2());
            bean.setRseMargem3(registroServidor.getRseMargem3());
            bean.setRseMargemRest3(registroServidor.getRseMargemRest3());
            bean.setRseMargemUsada3(registroServidor.getRseMargemUsada3());
            bean.setRseTipo(registroServidor.getRseTipo());
            bean.setRsePrazo(registroServidor.getRsePrazo());
            bean.setRseDataAdmissao(registroServidor.getRseDataAdmissao());
            bean.setRseClt(registroServidor.getRseClt());
            bean.setRseParamQtdAdeDefault(registroServidor.getRseParamQtdAdeDefault());
            bean.setRseObs(registroServidor.getRseObs());
            bean.setRseAssociado(registroServidor.getRseAssociado());
            bean.setRseEstabilizado(registroServidor.getRseEstabilizado());
            bean.setRseDataCarga(registroServidor.getRseDataCarga());
            bean.setRseDataFimEngajamento(registroServidor.getRseDataFimEngajamento());
            bean.setRseDataLimitePermanencia(registroServidor.getRseDataLimitePermanencia());
            bean.setRseBancoSal(registroServidor.getRseBancoSal());
            bean.setRseAgenciaSal(registroServidor.getRseAgenciaSal());
            bean.setRseAgenciaDvSal(registroServidor.getRseAgenciaDvSal());
            bean.setRseContaSal(registroServidor.getRseContaSal());
            bean.setRseContaDvSal(registroServidor.getRseContaDvSal());
            bean.setRseBancoSal2(registroServidor.getRseBancoSal2());
            bean.setRseAgenciaSal2(registroServidor.getRseAgenciaSal2());
            bean.setRseAgenciaDvSal2(registroServidor.getRseAgenciaDvSal2());
            bean.setRseContaSal2(registroServidor.getRseContaSal2());
            bean.setRseContaDvSal2(registroServidor.getRseContaDvSal2());
            bean.setRseSalario(registroServidor.getRseSalario());
            bean.setRseProventos(registroServidor.getRseProventos());
            bean.setRseDescontosComp(registroServidor.getRseDescontosComp());
            bean.setRseDescontosFacu(registroServidor.getRseDescontosFacu());
            bean.setRseOutrosDescontos(registroServidor.getRseOutrosDescontos());
            bean.setRsePraca(registroServidor.getRsePraca());
            bean.setRseBeneficiarioFinanDvCart(registroServidor.getRseBeneficiarioFinanDvCart());
            bean.setRseMunicipioLotacao(registroServidor.getRseMunicipioLotacao());
            bean.setRseMatriculaInst(registroServidor.getRseMatriculaInst());
            bean.setRseDataCtc(registroServidor.getRseDataCtc());
            bean.setRseBaseCalculo(registroServidor.getRseBaseCalculo());
            bean.setRsePedidoDemissao(registroServidor.getRsePedidoDemissao());
            bean.setRseDataSaida(registroServidor.getRseDataSaida());
            bean.setRseDataUltSalario(registroServidor.getRseDataUltSalario());
            bean.setRseDataRetorno(registroServidor.getRseDataRetorno());
            bean.setRseMotivoFaltaMargem(registroServidor.getRseMotivoFaltaMargem());

            if (registroServidor.getBanco() != null) {
                bean.setBanco(session.getReference(Banco.class, registroServidor.getBanco().getBcoCodigo()));
            }
            if (registroServidor.getCargoRegistroServidor() != null) {
                bean.setCargoRegistroServidor(session.getReference(CargoRegistroServidor.class, registroServidor.getCargoRegistroServidor().getCrsCodigo()));
            }
            if (registroServidor.getPadraoRegistroServidor() != null) {
                bean.setPadraoRegistroServidor(session.getReference(PadraoRegistroServidor.class, registroServidor.getPadraoRegistroServidor().getPrsCodigo()));
            }
            if (registroServidor.getSubOrgao() != null) {
                bean.setSubOrgao(session.getReference(SubOrgao.class, registroServidor.getSubOrgao().getSboCodigo()));
            }
            if (registroServidor.getUnidade() != null) {
                bean.setUnidade(session.getReference(Unidade.class, registroServidor.getUnidade().getUniCodigo()));
            }
            if (registroServidor.getVinculoRegistroServidor() != null) {
                bean.setVinculoRegistroServidor(session.getReference(VinculoRegistroServidor.class, registroServidor.getVinculoRegistroServidor().getVrsCodigo()));
            }
            if (registroServidor.getPostoRegistroServidor() != null) {
                bean.setPostoRegistroServidor(session.getReference(PostoRegistroServidor.class, registroServidor.getPostoRegistroServidor().getPosCodigo()));
            }
            if (registroServidor.getTipoRegistroServidor() != null) {
                bean.setTipoRegistroServidor(session.getReference(TipoRegistroServidor.class, registroServidor.getTipoRegistroServidor().getTrsCodigo()));
            }
            if (registroServidor.getCapacidadeRegistroSer() != null) {
                bean.setCapacidadeRegistroSer(session.getReference(CapacidadeRegistroSer.class, registroServidor.getCapacidadeRegistroSer().getCapCodigo()));
            }
            create(bean, session);

        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

}
