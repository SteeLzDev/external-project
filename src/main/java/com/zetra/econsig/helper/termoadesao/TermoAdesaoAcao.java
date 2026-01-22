package com.zetra.econsig.helper.termoadesao;

import java.io.Serializable;

import com.zetra.econsig.exception.TermoAdesaoAcaoException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: TermoAdesaoAcao</p>
 * <p>Description: Interface de definição dos métodos de ação para execução após aceite, recusa ou pós leitura
 * <p>Copyright: Copyright (c) 2025</p>
 * <p>Company: ZetraSoft</p>
 */
public interface TermoAdesaoAcao extends Serializable {

    public abstract void preAceiteTermo (String tadCodigo, AcessoSistema responsavel) throws TermoAdesaoAcaoException;

    public abstract void posAceiteTermo (String tadCodigo, AcessoSistema responsavel) throws TermoAdesaoAcaoException;

    public abstract void preRecusaTermo (String tadCodigo, AcessoSistema responsavel) throws TermoAdesaoAcaoException;

    public abstract void posRecusaTermo (String tadCodigo, AcessoSistema responsavel) throws TermoAdesaoAcaoException;

    public abstract void preLerDepoisTermo (String tadCodigo, AcessoSistema responsavel) throws TermoAdesaoAcaoException;

    public abstract void posLerDepoisTermo (String tadCodigo, AcessoSistema responsavel) throws TermoAdesaoAcaoException;

}
