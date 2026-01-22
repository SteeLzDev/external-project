package com.zetra.econsig.service.banner;

import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.BannerPublicidadeControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

public interface BannerPublicidadeController {

    public int countBannerPublicidade(CustomTransferObject criterio, AcessoSistema responsavel) throws BannerPublicidadeControllerException;

    public List<TransferObject> listarBannerPublicidade(CustomTransferObject criterio, int offset, int count, AcessoSistema responsavel) throws BannerPublicidadeControllerException;
}
