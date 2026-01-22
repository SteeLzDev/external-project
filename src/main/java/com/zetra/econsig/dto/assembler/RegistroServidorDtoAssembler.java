package com.zetra.econsig.dto.assembler;

import java.sql.Timestamp;

import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.persistence.entity.RegistroServidor;

/**
 * <p>Title: RegistroServidorDtoAssembler</p>
 * <p>Description: Transfer Object Assembler do Registro Servidor</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RegistroServidorDtoAssembler {

    public static RegistroServidorTO createDto(RegistroServidor rseBean, boolean retornaMargem) {
        RegistroServidorTO rse = null;

        if (rseBean != null) {
            rse = new RegistroServidorTO(rseBean.getRseCodigo());

            rse.setSrsCodigo(rseBean.getStatusRegistroServidor().getSrsCodigo());
            rse.setSerCodigo(rseBean.getServidor().getSerCodigo());
            rse.setOrgCodigo(rseBean.getOrgao().getOrgCodigo());
            if (rseBean.getVinculoRegistroServidor() != null) {
                rse.setVrsCodigo(rseBean.getVinculoRegistroServidor().getVrsCodigo());
            }
            if (rseBean.getCargoRegistroServidor() != null) {
                rse.setCrsCodigo(rseBean.getCargoRegistroServidor().getCrsCodigo());
            }
            if (rseBean.getSubOrgao() != null) {
                rse.setSboCodigo(rseBean.getSubOrgao().getSboCodigo());
            }
            if (rseBean.getUnidade() != null) {
                rse.setUniCodigo(rseBean.getUnidade().getUniCodigo());
            }
            if (rseBean.getPadraoRegistroServidor() != null) {
                rse.setPrsCodigo(rseBean.getPadraoRegistroServidor().getPrsCodigo());
            }
            if (rseBean.getPostoRegistroServidor() != null) {
                rse.setPosCodigo(rseBean.getPostoRegistroServidor().getPosCodigo());
            }
            if (rseBean.getTipoRegistroServidor() != null) {
                rse.setTrsCodigo(rseBean.getTipoRegistroServidor().getTrsCodigo());
            }
            if (rseBean.getCapacidadeRegistroSer() != null) {
                rse.setCapCodigo(rseBean.getCapacidadeRegistroSer().getCapCodigo());
            }
            if (rseBean.getMargem() != null) {
                rse.setMarCodigo(rseBean.getMargem().getMarCodigo());
            }
            if (rseBean.getUsuario() != null) {
                rse.setUsuCodigo(rseBean.getUsuario().getUsuCodigo());
            }
            if (rseBean.getBanco() != null) {
                rse.setBcoCodigo(rseBean.getBanco().getBcoCodigo());
            }
            if (retornaMargem) {
                rse.setRseMargem(rseBean.getRseMargem());
                rse.setRseMargemRest(rseBean.getRseMargemRest());
                rse.setRseMargemUsada(rseBean.getRseMargemUsada());
                rse.setRseMediaMargem(rseBean.getRseMediaMargem());
                rse.setRseMargem2(rseBean.getRseMargem2());
                rse.setRseMargemRest2(rseBean.getRseMargemRest2());
                rse.setRseMargemUsada2(rseBean.getRseMargemUsada2());
                rse.setRseMediaMargem2(rseBean.getRseMediaMargem2());
                rse.setRseMargem3(rseBean.getRseMargem3());
                rse.setRseMargemRest3(rseBean.getRseMargemRest3());
                rse.setRseMargemUsada3(rseBean.getRseMargemUsada3());
                rse.setRseMediaMargem3(rseBean.getRseMediaMargem3());
            }
            rse.setRseMatricula(rseBean.getRseMatricula());
            rse.setRsePrazo(rseBean.getRsePrazo());
            rse.setRseTipo(rseBean.getRseTipo());
            rse.setRseCLT(rseBean.getRseClt());
            rse.setRseBancoSal(rseBean.getRseBancoSal());
            rse.setRseAgenciaDvSal(rseBean.getRseAgenciaDvSal());
            rse.setRseAgenciaSal(rseBean.getRseAgenciaSal());
            rse.setRseContaDvSal(rseBean.getRseContaDvSal());
            rse.setRseContaSal(rseBean.getRseContaSal());
            rse.setRseBancoSalAlternativo(rseBean.getRseBancoSal2());
            rse.setRseAgenciaDvSalAlternativa(rseBean.getRseAgenciaDvSal2());
            rse.setRseAgenciaSalAlternativa(rseBean.getRseAgenciaSal2());
            rse.setRseContaDvSalAlternativa(rseBean.getRseContaDvSal2());
            rse.setRseContaSalAlternativa(rseBean.getRseContaSal2());
            rse.setRseSalario(rseBean.getRseSalario());
            rse.setRseProventos(rseBean.getRseProventos());
            rse.setRseDescontosComp(rseBean.getRseDescontosComp());
            rse.setRseDescontosFacu(rseBean.getRseDescontosFacu());
            rse.setRseOutrosDescontos(rseBean.getRseOutrosDescontos());
            rse.setRseAssociado(rseBean.getRseAssociado());
            rse.setRseObs(rseBean.getRseObs());
            rse.setRseEstabilizado(rseBean.getRseEstabilizado());
            rse.setRseParamQtdAdeDefault(rseBean.getRseParamQtdAdeDefault());
            rse.setRseAuditoriaTotal(rseBean.getRseAuditoriaTotal());
            rse.setRseBeneficiarioFinanDvCart(rseBean.getRseBeneficiarioFinanDvCart());
            rse.setRsePraca(rseBean.getRsePraca());
            rse.setRsePedidoDemissao(rseBean.getRsePedidoDemissao());
            rse.setRsePontuacao(rseBean.getRsePontuacao());
            rse.setRseMotivoBloqueio(rseBean.getRseMotivoBloqueio());

            if (rseBean.getRseMunicipioLotacao() != null) {
                rse.setRseMunicipioLotacao(rseBean.getRseMunicipioLotacao());
            }
            if (rseBean.getRseDataCarga() != null) {
                rse.setRseDataCarga(new Timestamp(rseBean.getRseDataCarga().getTime()));
            }
            if (rseBean.getRseDataAlteracao() != null) {
                rse.setRseDataAlteracao(new Timestamp(rseBean.getRseDataAlteracao().getTime()));
            }
            if (rseBean.getRseDataAdmissao() != null) {
                rse.setRseDataAdmissao(new Timestamp(rseBean.getRseDataAdmissao().getTime()));
            }
            if (rseBean.getRseDataFimEngajamento() != null) {
                rse.setRseDataFimEngajamento(new Timestamp(rseBean.getRseDataFimEngajamento().getTime()));
            }
            if (rseBean.getRseDataLimitePermanencia() != null) {
                rse.setRseDataLimitePermanencia(new Timestamp(rseBean.getRseDataLimitePermanencia().getTime()));
            }
            if (rseBean.getRseBaseCalculo() != null) {
                rse.setRseBaseCalculo(rseBean.getRseBaseCalculo());
            }
            if (rseBean.getRseDataSaida() != null) {
                rse.setRseDataSaida(rseBean.getRseDataSaida());
            }
            if (rseBean.getRseDataUltSalario() != null) {
                rse.setRseDataUltSalario(rseBean.getRseDataUltSalario());
            }
            if (rseBean.getRseDataRetorno() != null) {
                rse.setRseDataRetorno(rseBean.getRseDataRetorno());
            }
            if (rseBean.getRseDataCtc() != null) {
                rse.setRseDataContracheque(rseBean.getRseDataCtc());
            }
            if (rseBean.getRseMatriculaInst() != null) {
                rse.setRseMatriculaInst(rseBean.getRseMatriculaInst());
            }
            if (rseBean.getCargoRegistroServidor() != null) {
                rse.setCrsCodigo(rseBean.getCargoRegistroServidor().getCrsCodigo());
            }

            rse.setRseAssociado(rseBean.getRseAssociado());
            rse.setRseMotivoFaltaMargem(rseBean.getRseMotivoFaltaMargem());
        }

        return rse;
    }
}
