package com.zetra.econsig.service.consignacao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ParametrosException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;


/**
 * <p>Title: DownloadAnexoContratoController</p>
 * <p>Description: Session Bean para operação de download de anexo de contrato.</p>
 * <p>Copyright: Copyright (c) 2020</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author: ricardo.magno $
 * $Revision: $
 * $Date: $
 */
public interface DownloadAnexoContratoController {

	public String compactarAnexosAdePeriodo(List<String> csaCodigos, List<String> svcCodigos, List<String> sadCodigos, Date aadDataIni, Date aadDataFim, String zipFileNameOutPut, AcessoSistema responsavel) throws ConsignanteControllerException;
    public String geraNomeAnexosPeriodo(HashMap<String, String> campos, String extensao, Integer sufixo, boolean validarDocumentos, AcessoSistema responsavel) throws ParametrosException;
}
