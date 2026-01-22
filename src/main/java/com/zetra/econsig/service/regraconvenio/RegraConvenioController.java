package com.zetra.econsig.service.regraconvenio;

import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.RegraConvenioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.report.jasper.dto.RegrasConvenioParametrosBean;

public interface RegraConvenioController {
    
   public void salvarRegrasConvenio(List<RegrasConvenioParametrosBean> listParametros, String csaCodigo, AcessoSistema responsavel) throws RegraConvenioControllerException;

    public List<TransferObject> listaRegrasConvenioByCsa(String csaCodigo, AcessoSistema responsavel) throws RegraConvenioControllerException;

	public void removeRegrasConvenioByCsa(String csaCodigo, AcessoSistema responsavel) throws RegraConvenioControllerException;
}
