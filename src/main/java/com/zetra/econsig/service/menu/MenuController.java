package com.zetra.econsig.service.menu;


import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MenuTO;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MenuControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: MenuController</p>
 * <p>Description: Interface remota para os métodos de negócio de Menu.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface MenuController   {

    public List<MenuTO> obterMenu(AcessoSistema responsavel) throws MenuControllerException;

    public String obterMenu (String usuCodigo, String tipoEntidade, AcessoSistema responsavel) throws MenuControllerException;

    public List<TransferObject> lstMenu (CustomTransferObject criterio, AcessoSistema responsavel) throws MenuControllerException;

    public List<TransferObject> lstItemMenu (CustomTransferObject criterio, AcessoSistema responsavel) throws MenuControllerException;

    public void updateItemMenu(CustomTransferObject criterio, AcessoSistema responsavel) throws MenuControllerException;

    public String createItemMenu (CustomTransferObject criterio, AcessoSistema responsavel) throws MenuControllerException;

    public void removeItemMenu (String itmCodigo, AcessoSistema responsavel) throws MenuControllerException;

    public void favoritarMenu(String itmCodigo, AcessoSistema responsavel) throws MenuControllerException, FindException, UpdateException;

    public void updateFavoritosDashBoard(String usuCodigo, String itmCodigo, Short imfSequencia, AcessoSistema responsavel) throws MenuControllerException, FindException, UpdateException;
}
