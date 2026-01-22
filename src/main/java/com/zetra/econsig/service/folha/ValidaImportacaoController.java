package com.zetra.econsig.service.folha;


import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.ValidaImportacaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ValidaImportacaoController</p>
 * <p>Description: Intercafe EJB de controller da Valida Importação.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ValidaImportacaoController {

    public Map<String, String> lstParamValidacaoArq(String tipoEntidade, String codigoEntidade, List<String> tvaCodigos, List<String> tvaChaves, AcessoSistema responsavel) throws ValidaImportacaoControllerException;

}