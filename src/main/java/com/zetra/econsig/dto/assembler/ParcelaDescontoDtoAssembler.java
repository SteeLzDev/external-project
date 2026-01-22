package com.zetra.econsig.dto.assembler;

import com.zetra.econsig.dto.entidade.ParcelaDescontoTO;
import com.zetra.econsig.persistence.entity.ParcelaDesconto;
import com.zetra.econsig.persistence.entity.ParcelaDescontoPeriodo;

/**
 * <p>Title: ParcelaDescontoDtoAssembler</p>
 * <p>Description: Transfer Object Assembler do Parcela Desconto</p>
 * <p>Copyright: Copyright (c) 2002-2021</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ParcelaDescontoDtoAssembler {

    public static ParcelaDescontoTO createDto(ParcelaDesconto bean) {
        ParcelaDescontoTO dto = null;

        if (bean != null) {
            dto = new ParcelaDescontoTO(false);
            dto.setPrdCodigo(bean.getPrdCodigo());
            dto.setPrdNumero(bean.getPrdNumero());
            dto.setAdeCodigo(bean.getAutDesconto().getAdeCodigo());
            dto.setSpdCodigo(bean.getStatusParcelaDesconto().getSpdCodigo());
            dto.setTdeCodigo(bean.getTipoDesconto() != null ? bean.getTipoDesconto().getTdeCodigo() : null);
            dto.setMneCodigo(bean.getTipoMotivoNaoExportacao() != null ? bean.getTipoMotivoNaoExportacao().getMneCodigo() : null);
            dto.setPrdDataDesconto(bean.getPrdDataDesconto());
            dto.setPrdDataRealizado(bean.getPrdDataRealizado());
            dto.setPrdVlrPrevisto(bean.getPrdVlrPrevisto());
            dto.setPrdVlrRealizado(bean.getPrdVlrRealizado());
        }

        return dto;
    }

    public static ParcelaDescontoTO createDto(ParcelaDescontoPeriodo bean) {
        ParcelaDescontoTO dto = null;

        if (bean != null) {
            dto = new ParcelaDescontoTO(true);
            dto.setPrdCodigo(bean.getPrdCodigo());
            dto.setPrdNumero(bean.getPrdNumero());
            dto.setAdeCodigo(bean.getAutDesconto().getAdeCodigo());
            dto.setSpdCodigo(bean.getStatusParcelaDesconto().getSpdCodigo());
            dto.setTdeCodigo(bean.getTipoDesconto() != null ? bean.getTipoDesconto().getTdeCodigo() : null);
            dto.setMneCodigo(bean.getTipoMotivoNaoExportacao() != null ? bean.getTipoMotivoNaoExportacao().getMneCodigo() : null);
            dto.setPrdDataDesconto(bean.getPrdDataDesconto());
            dto.setPrdDataRealizado(bean.getPrdDataRealizado());
            dto.setPrdVlrPrevisto(bean.getPrdVlrPrevisto());
            dto.setPrdVlrRealizado(bean.getPrdVlrRealizado());
        }

        return dto;
    }
}
