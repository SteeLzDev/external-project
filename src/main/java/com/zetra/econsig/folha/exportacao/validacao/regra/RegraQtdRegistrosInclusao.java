package com.zetra.econsig.folha.exportacao.validacao.regra;

import java.util.List;

import com.zetra.econsig.dto.entidade.RegraValidacaoMovimentoTO;
import com.zetra.econsig.dto.entidade.ResultadoValidacaoMovimentoTO;

/**
 * <p>Title: RegraQtdRegistrosInclusao</p>
 * <p>Description: Classe abstrata com a implementação MYSQL da regra com a da quantidade de registros de inclusão gerados.</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RegraQtdRegistrosInclusao extends AbstractRegraQtdRegistros {
    public RegraQtdRegistrosInclusao() {
        super("I");
    }

    @Override
    public void executar(List<String> estCodigos, List<String> orgCodigos, ResultadoValidacaoMovimentoTO rva, RegraValidacaoMovimentoTO regra) {
        // TODO implementar...
    }

    @Override
    protected long buscaQtdBase() {
        // TODO implementar...
        return -1;
    }
}
