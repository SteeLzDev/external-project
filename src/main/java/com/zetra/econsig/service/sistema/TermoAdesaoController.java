package com.zetra.econsig.service.sistema;

import java.util.List;


import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.TermoAdesaoTO;
import com.zetra.econsig.exception.TermoAdesaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: TermoAdesaoController</p>
 * <p>Description: Intercafe EJB de controller do Termo Adesao.</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface TermoAdesaoController {
    public TermoAdesaoTO findTermoAdesao(TermoAdesaoTO termoAdesaoServico, AcessoSistema responsavel) throws TermoAdesaoControllerException;

    public void createTermoAdesao(TermoAdesaoTO termoAdesaoServico, AcessoSistema responsavel) throws TermoAdesaoControllerException;

    public void removeTermoAdesao(TermoAdesaoTO termoAdesaoServico, AcessoSistema responsavel) throws TermoAdesaoControllerException;

    public void updateTermoAdesao(TermoAdesaoTO termoAdesaoServico, AcessoSistema responsavel) throws TermoAdesaoControllerException;

    public List<TransferObject> findTermoAdesaoComLeituraByTadCodigo(String tadCodigo, AcessoSistema responsavel) throws TermoAdesaoControllerException;

    public List<TermoAdesaoTO> listTermoAdesaoByFuncao(String funCodigo, AcessoSistema responsavel) throws TermoAdesaoControllerException;

    public List<TermoAdesaoTO> listTermoAdesaoSemLeitura(String funCodigo, List<String> termoAdesaoLerDepois, AcessoSistema responsavel) throws TermoAdesaoControllerException;

    public List<TermoAdesaoTO> listTermoAdesaoSemFunCodigoExibeServidor() throws TermoAdesaoControllerException;

    public List<TransferObject> listTermoAdesaoByUsuCodigo(AcessoSistema responsavel) throws TermoAdesaoControllerException;

    public void createLeituraTermoAdesaoUsuario(String tadCodigo, boolean ltuTermoAceito, String ltuObs, AcessoSistema responsavel) throws TermoAdesaoControllerException;

    public List<TransferObject> findTermoAdesaoAceite(AcessoSistema responsavel ) throws TermoAdesaoControllerException;

    public List<TransferObject> findTermoAdesaoGestaoFinanceira( String usuCpf, String tadCodigo, AcessoSistema responsavel) throws TermoAdesaoControllerException;

    }
