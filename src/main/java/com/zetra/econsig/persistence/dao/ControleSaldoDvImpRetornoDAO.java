package com.zetra.econsig.persistence.dao;

import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;

/**
 * <p>Title: ControleSaldoDvImpRetornoDAO</p>
 * <p>Description: DAO para rotinas de controle de saldo devedor
 * efetuadas na importação do retorno da folha.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * @author Igor Lucas
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ControleSaldoDvImpRetornoDAO {

    public void abaterSaldoDevedor(List<String> orgCodigos, List<String> estCodigos) throws DAOException;

    public void concluirContratosSaldoDevedorZerado(List<String> orgCodigos, List<String> estCodigos) throws DAOException;

    public void concluirContratosNaoPagosNoExercicio(List<String> orgCodigos, List<String> estCodigos) throws DAOException;

    public void estenderPrazo(List<String> orgCodigos, List<String> estCodigos) throws DAOException;

    public void reimplantarContratosNaoPagos(List<String> orgCodigos, List<String> estCodigos) throws DAOException;

    public List<TransferObject> listarContratosCorrecaoOutroServico(List<String> orgCodigos, List<String> estCodigos) throws DAOException;
}
