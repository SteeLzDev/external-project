package com.zetra.econsig.service.dirf;

import java.util.List;

import com.zetra.econsig.exception.ArquivoDirfControllerException;
import com.zetra.econsig.folha.dirf.ImportaArquivoDirfDTO;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ArquivoDirfControllerBean</p>
 * <p>Description: Interface Remota para o Session Bean para operações sobre arquivo de DIRF</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ArquivoDirfController {
    public void importarArquivoDirf(ImportaArquivoDirfDTO dto, AcessoSistema responsavel) throws ArquivoDirfControllerException;

    public List<Short> listarAnoCalendarioDirf(String serCodigo, AcessoSistema responsavel) throws ArquivoDirfControllerException;

    public String obterConteudoArquivoDirf(String serCodigo, Short disAnoCalendario, AcessoSistema responsavel) throws ArquivoDirfControllerException;
}
