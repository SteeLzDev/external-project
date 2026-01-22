package com.zetra.econsig.service.sdp;

import java.util.Date;
import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.PermissionarioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: PermissionarioController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface PermissionarioController {

    public String createPermissionario(TransferObject permissionario, AcessoSistema responsavel) throws PermissionarioControllerException;

    public TransferObject findPermissionario(String prmCodigo, AcessoSistema responsavel) throws PermissionarioControllerException;

    public TransferObject findPermissionarioAtivoByRseCodigo(String rseCodigo, AcessoSistema responsavel) throws PermissionarioControllerException;

    public void updatePermissionario(TransferObject permissionario, AcessoSistema responsavel) throws PermissionarioControllerException;

    public void updatePermissionario(TransferObject permissionario, boolean reativacao, AcessoSistema responsavel) throws PermissionarioControllerException;

    public void removePermissionario(String prmCodigo, Date prmDataDesocupacao, AcessoSistema responsavel) throws PermissionarioControllerException;

    public void movePermissionario(String rseCodigoOrigem, String rseCodigoDestino, AcessoSistema responsavel) throws PermissionarioControllerException;

    public int countPermissionarios(TransferObject criterio, AcessoSistema responsavel) throws PermissionarioControllerException;

    public List<TransferObject> lstPermissionarios(TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws PermissionarioControllerException;

    public List<TransferObject> lstHistoricoPermissionario(TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws PermissionarioControllerException;

    public int countOcorrenciaPermisionario(TransferObject criterio, AcessoSistema responsavel) throws PermissionarioControllerException;

    public TransferObject findPermissionarioPorEndereco(TransferObject criterio, AcessoSistema responsavel) throws PermissionarioControllerException;
}
