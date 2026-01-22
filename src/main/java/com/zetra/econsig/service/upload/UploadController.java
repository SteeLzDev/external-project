package com.zetra.econsig.service.upload;

import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.UploadControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.TipoArquivo;

/**
 * <p>Title: UploadController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface UploadController {

    public TipoArquivo buscaTipoArquivoByPrimaryKey(String codigoTipoArquivo, AcessoSistema responsavel) throws UploadControllerException;

    public List<String> listarPapeisEnvioEmailUpload(AcessoSistema responsavel) throws UploadControllerException;

    public List<TransferObject> buscaTipoArquivoSer(AcessoSistema responsavel) throws UploadControllerException;

    }
