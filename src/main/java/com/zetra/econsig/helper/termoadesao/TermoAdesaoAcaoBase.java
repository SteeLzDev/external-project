package com.zetra.econsig.helper.termoadesao;

import com.zetra.econsig.exception.TermoAdesaoAcaoException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: TermoAdesaoAcaoBase</p>
 * <p>Description: Classe base para execução após aceite, recusa ou pós leitura
 * <p>Copyright: Copyright (c) 2025</p>
 * <p>Company: ZetraSoft</p>
 */
public abstract class TermoAdesaoAcaoBase implements TermoAdesaoAcao {

    private static final long serialVersionUID = 1L;

	@Override
	public void preAceiteTermo (String tadCodigo, AcessoSistema responsavel) throws TermoAdesaoAcaoException {
    }

    @Override
	public void posAceiteTermo (String tadCodigo, AcessoSistema responsavel) throws TermoAdesaoAcaoException {
    }

    @Override
	public void preRecusaTermo (String tadCodigo, AcessoSistema responsavel) throws TermoAdesaoAcaoException {
    }

    @Override
	public void posRecusaTermo (String tadCodigo, AcessoSistema responsavel) throws TermoAdesaoAcaoException {
    }

    @Override
	public void preLerDepoisTermo (String tadCodigo, AcessoSistema responsavel) throws TermoAdesaoAcaoException {
    }

    @Override
	public void posLerDepoisTermo (String tadCodigo, AcessoSistema responsavel) throws TermoAdesaoAcaoException {
    }

}
