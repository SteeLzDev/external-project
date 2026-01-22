package com.zetra.econsig.service.sdp;


import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.PostoRegistroServidorControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.ParamPostoCsaSvc;

/**
 * <p>Title: PostoRegistroServidorController</p>
 * <p>Description: Interface remota do Session Façade para operações de manutenção do posto.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface PostoRegistroServidorController  {

    public long countPostoRegistroServidor(TransferObject criterio, AcessoSistema responsavel) throws PostoRegistroServidorControllerException;

    public List<TransferObject> lstPostoRegistroServidor(TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws PostoRegistroServidorControllerException;

    public TransferObject buscaPosto(String posCodigo, AcessoSistema responsavel) throws PostoRegistroServidorControllerException;

    public void updatePosto(TransferObject posto, AcessoSistema responsavel) throws PostoRegistroServidorControllerException;

    public List<TransferObject> lstBloqueioPostoPorCsaSvc(String csaCodigo, String svcCodigo, AcessoSistema responsavel) throws PostoRegistroServidorControllerException;

    public void salvarBloqueioPostoPorCsaSvc(String csaCodigo, String svcCodigo, Map<String, Boolean> bloqueiosSolicitacao, Map<String, Boolean> bloqueiosReserva, AcessoSistema responsavel) throws PostoRegistroServidorControllerException;

    public TransferObject findValorFixoByCsaSvcPos(String svcCodigo, String csaCodigo, String posCodigo, AcessoSistema responsavel)throws PostoRegistroServidorControllerException;

    public List<TransferObject> findValorFixoByCsaSvc(String svcCodigo, String csaCodigo, String posCodigo, AcessoSistema responsavel)throws PostoRegistroServidorControllerException;
    public void saveUpdateVlrFixoPosto(List<ParamPostoCsaSvc> postoCsaSvcList, String svcCodigo, String csaCodigo, AcessoSistema responsavel) throws PostoRegistroServidorControllerException;
}
