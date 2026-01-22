package com.zetra.econsig.service.totem;

import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.EventosTotemControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: EventosTotemController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author: rodrigo.rosa $
 * $Revision: 30365 $
 * $Date: 2020-09-16 10:25:15 -0300 (Qua, 16 set 2020) $
 */
public interface EventosTotemController {

    public List<TransferObject> listarEventosTotem(TransferObject criterio, AcessoSistema responsavel) throws EventosTotemControllerException;

    public int countEventosTotem(TransferObject criterio, AcessoSistema responsavel) throws EventosTotemControllerException;

    public CustomTransferObject buscaDetalheEvento(String evnCodigo, String evnCodigoBiometria, AcessoSistema responsavel) throws EventosTotemControllerException;

}
