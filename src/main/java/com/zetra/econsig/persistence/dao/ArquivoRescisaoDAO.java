package com.zetra.econsig.persistence.dao;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ArquivoRescisaoDAO</p>
 * <p>Description: Interface DAO para a rotinha de exportação do arquivo de rescisão.</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ArquivoRescisaoDAO {

    public void gerarArquivoRescisao(String nomeArqSaida, String nomeArqConfEntrada, String nomeArqConfTradutor, String nomeArqConfSaida, AcessoSistema responsavel) throws DAOException;

}