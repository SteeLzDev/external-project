package com.zetra.econsig.service.totem;

import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.TotemParametroConsignanteControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: TotemParametroConsignanteController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface TotemParametroConsignanteController {
    public List<TransferObject> selectParametroConsignanteTotem(List<Integer> tpaCodigos, Integer cseCodigo, AcessoSistema responsavel) throws TotemParametroConsignanteControllerException;

    public void updateParametroConsignante(Integer tpaCodigo, Integer cseCodigo, Integer rcsCodigo, String pceValor, AcessoSistema responsavel) throws TotemParametroConsignanteControllerException;
}
