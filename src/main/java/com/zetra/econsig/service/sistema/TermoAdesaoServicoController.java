package com.zetra.econsig.service.sistema;

import com.zetra.econsig.dto.entidade.TermoAdesaoServicoTO;
import com.zetra.econsig.exception.TermoAdesaoServicoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: TermoAdesaoServicoController</p>
 * <p>Description: Intercafe EJB de controller do Termo Adesao Serviço.</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface TermoAdesaoServicoController {
    public TermoAdesaoServicoTO findTermoAdesaoServico(TermoAdesaoServicoTO termoAdesaoServico, AcessoSistema responsavel) throws TermoAdesaoServicoControllerException;

    public void createTermoAdesaoServico(TermoAdesaoServicoTO termoAdesaoServico, AcessoSistema responsavel) throws TermoAdesaoServicoControllerException;

    public void removeTermoAdesaoServico(TermoAdesaoServicoTO termoAdesaoServico, AcessoSistema responsavel) throws TermoAdesaoServicoControllerException;

    public void updateTermoAdesaoServico(TermoAdesaoServicoTO termoAdesaoServico, AcessoSistema responsavel) throws TermoAdesaoServicoControllerException;
}
