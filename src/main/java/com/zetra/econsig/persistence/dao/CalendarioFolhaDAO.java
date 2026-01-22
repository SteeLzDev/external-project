package com.zetra.econsig.persistence.dao;

import java.util.List;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: CalendarioFolhaDAO</p>
 * <p>Description: DAO para rotinas auxiliares de calendário folha
 * usada tanto para exportação de movimento, quanto retorno.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface CalendarioFolhaDAO {

    /**
     * Cria tabela com calendário folha consolidando as informações presentes
     * nas três tabelas possíveis de calendário: tb_calendario_folha_cse,
     * tb_calendario_folha_est e tb_calendario_folha_org, sendo que os
     * registros podem estar presente em uma, duas ou nas três tabelas.
     * @param orgCodigos  : os códigos dos órgãos, nulo para todos
     * @param estCodigos  : os códigos dos estabelecimentos, nulo para todos
     * @param responsavel : responsável pelo processamento
     * @throws DAOException
     */
    public void consolidarCalendarioFolha(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws DAOException;

    public void criarTabelaCalendarioQuinzenal(AcessoSistema responsavel) throws DAOException;
}
