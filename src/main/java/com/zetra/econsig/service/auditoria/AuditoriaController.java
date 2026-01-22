package com.zetra.econsig.service.auditoria;


import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AuditoriaControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: AuditoriaController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface AuditoriaController {

    public int qtdeLogAuditoriaQuery(String codigoEntidade, String tipoEntidade, boolean naoAuditado, TransferObject criterios, AcessoSistema responsavel) throws AuditoriaControllerException;

    public List<TransferObject> lstLogAuditoriaQuery(String codigoEntidade, String tipoEntidade, boolean naoAuditado, TransferObject criterios, AcessoSistema responsavel) throws AuditoriaControllerException;

    public List<TransferObject> lstLogAuditoriaQuery(String codigoEntidade, String tipoEntidade, boolean naoAuditado, TransferObject criterios, int offset, int count, AcessoSistema responsavel) throws AuditoriaControllerException;

    public void auditarTodosLogs(String codigoEntidade, String tipoEntidade, TransferObject criterios, AcessoSistema responsavel) throws AuditoriaControllerException;

    public void auditarLog(List<Integer> audCodigos, String codigoEntidade, String tipoEntidade, AcessoSistema responsavel) throws AuditoriaControllerException;

    public void auditarLog(Integer audCodigo, String codigoEntidade, String tipoEntidade, AcessoSistema responsavel) throws AuditoriaControllerException;
}
