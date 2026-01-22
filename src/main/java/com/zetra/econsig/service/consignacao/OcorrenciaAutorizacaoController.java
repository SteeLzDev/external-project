package com.zetra.econsig.service.consignacao;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

public interface OcorrenciaAutorizacaoController {

    public void updateOcorrenciaAutorizacaoAde(String ocaCodigo, String ocaObs, AcessoSistema responsavel) throws ZetraException;
}
