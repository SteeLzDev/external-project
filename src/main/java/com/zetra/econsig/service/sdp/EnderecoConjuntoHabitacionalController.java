package com.zetra.econsig.service.sdp;

import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.EnderecoTransferObject;
import com.zetra.econsig.exception.EnderecoConjuntoHabitacionalControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: EnderecoConjuntoHabitacionalController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author: junio.goncalves $
 * $Revision:  $
 * $Date: 2012-11-28 17:46:00 -0300 (qua, 28 nov 2012) $
 */
public interface EnderecoConjuntoHabitacionalController {

    public List<TransferObject> listaEndereco(TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws EnderecoConjuntoHabitacionalControllerException;

    public int countEndereco(TransferObject criterio, AcessoSistema responsavel) throws EnderecoConjuntoHabitacionalControllerException;

    public void removeEndereco(EnderecoTransferObject endereco, AcessoSistema responsavel) throws EnderecoConjuntoHabitacionalControllerException;

    public String createEndereco(EnderecoTransferObject endereco, AcessoSistema responsavel) throws EnderecoConjuntoHabitacionalControllerException;

    public String updateEndereco(EnderecoTransferObject endereco, AcessoSistema responsavel) throws EnderecoConjuntoHabitacionalControllerException;

    public EnderecoTransferObject buscaEnderecoByPK(EnderecoTransferObject endereco, AcessoSistema responsavel) throws EnderecoConjuntoHabitacionalControllerException;
}
