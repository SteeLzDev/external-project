package com.zetra.econsig.service.ocorrencia;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.HistoricoOcorrenciaAde;

import java.util.List;

public interface HistoricoOcorrenciaAdeController {

    public List<HistoricoOcorrenciaAde> buscarHistoricoOcorrenciaAdeByOcaCodigo(String ocaCodigo, AcessoSistema responsavel) throws ZetraException;
}
