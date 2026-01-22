package com.zetra.econsig.service.folha;


import java.util.Date;
import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ImpArqContrachequeController</p>
 * <p>Description: Session Bean para a rotina de importação de arquivo de contracheques.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ImpArqContrachequeController {

    public void importaArquivoContracheques(String nomeArquivo, Date periodo, String tipoEntidade, String codigoEntidade, Boolean sobrepoe, AcessoSistema responsavel) throws ServidorControllerException;

    public void importaArquivoContracheques(String nomeArquivo, Date periodo, String tipoEntidade, String codigoEntidade, Boolean sobrepoe, Boolean ativo, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> listarContrachequeRse(String rseCodigo, Date ccqPeriodo, boolean obtemUltimo, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> listarContrachequeRse(String rseCodigo, Date ccqPeriodo, boolean obtemUltimo, int count, boolean ordemDesc, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> listarContrachequeRse(String rseCodigo, Date ccqPeriodo, boolean obtemUltimo, int count, boolean ordemDesc, Date dataInicio, Date dataFim, AcessoSistema responsavel) throws ServidorControllerException;
}
