package com.zetra.econsig.service.servico;


import java.util.List;

import com.zetra.econsig.exception.NaturezaServicoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.NaturezaServico;

/**
 * <p>Title: NaturezaServicoController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface NaturezaServicoController {

    public List<NaturezaServico> listaNaturezas(AcessoSistema responsavel) throws NaturezaServicoControllerException;

    public List<NaturezaServico> listaNaturezasByNseCodigo(List<String> nseCodigos, AcessoSistema responsavel) throws NaturezaServicoControllerException;

    public List<NaturezaServico> listaNaturezasByNseCodigo(List<String> nseCodigos, boolean naturezaBeneficio, AcessoSistema responsavel) throws NaturezaServicoControllerException;

    public List<NaturezaServico> listaNaturezasByNseCodigoPai(String nseCodigoPai, AcessoSistema responsavel) throws NaturezaServicoControllerException;

    public List<NaturezaServico> listaNaturezasByNseCodigoPai(String nseCodigoPai, boolean naturezaBeneficio, AcessoSistema responsavel) throws NaturezaServicoControllerException;

    public List<NaturezaServico> listaNaturezasByNseCodigoPai(String orgCodigo, String nseCodigoPai, boolean naturezaBeneficio, AcessoSistema responsavel) throws NaturezaServicoControllerException;

    public NaturezaServico buscaNaturezaServico(String nseCodigo, AcessoSistema responsavel) throws NaturezaServicoControllerException;
}
