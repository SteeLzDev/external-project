package com.zetra.econsig.service.indice;


import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.IndiceControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: IndiceController</p>
 * <p>Description: Session Façade para manipulação de indices.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface IndiceController {

    public List<TransferObject> selectIndices(int size, int offset, CustomTransferObject criterio, AcessoSistema responsavel) throws IndiceControllerException;

    public int countIndices(CustomTransferObject criterio, AcessoSistema responsavel) throws IndiceControllerException;

    public void removeIndice(CustomTransferObject criterio, AcessoSistema responsavel) throws IndiceControllerException;

    public void createIndice(CustomTransferObject novoIndice, AcessoSistema responsavel) throws IndiceControllerException;

    public void updateIndice(CustomTransferObject novoIndice, CustomTransferObject anteriorIndice, AcessoSistema responsavel) throws IndiceControllerException;

}
