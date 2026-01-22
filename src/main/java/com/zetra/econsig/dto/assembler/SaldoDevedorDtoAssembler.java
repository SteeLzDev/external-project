package com.zetra.econsig.dto.assembler;

import com.zetra.econsig.dto.entidade.SaldoDevedorTransferObject;
import com.zetra.econsig.persistence.entity.SaldoDevedor;

/**
 * <p>Title: SaldoDevedorDtoAssembler</p>
 * <p>Description: Transfer Object Assembler do Saldo Devedor</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class SaldoDevedorDtoAssembler {

    public static SaldoDevedorTransferObject createDto(SaldoDevedor sdvBean) {
        SaldoDevedorTransferObject dto = null;

        if (sdvBean != null) {
            dto = new SaldoDevedorTransferObject();
            dto.setAdeCodigo(sdvBean.getAdeCodigo());
            dto.setBcoCodigo(sdvBean.getBanco() != null ? sdvBean.getBanco().getBcoCodigo() : null);
            dto.setUsuCodigo(sdvBean.getUsuario().getUsuCodigo());
            dto.setSdvValor(sdvBean.getSdvValor());
            dto.setSdvValorComDesconto(sdvBean.getSdvValorComDesconto());
            dto.setSdvAgencia(sdvBean.getSdvAgencia());
            dto.setSdvConta(sdvBean.getSdvConta());
            dto.setSdvDataMod(sdvBean.getSdvDataMod());
            dto.setSdvNomeFavorecido(sdvBean.getSdvNomeFavorecido());
            dto.setSdvCnpj(sdvBean.getSdvCnpj());
            dto.setSdvNumeroContrato(sdvBean.getSdvNumeroContrato());
            dto.setSdvLinkBoletoQuitacao(sdvBean.getSdvLinkBoletoQuitacao());
            dto.setSdvDataValidade(sdvBean.getSdvDataValidade());
        }

        return dto;
    }
}
