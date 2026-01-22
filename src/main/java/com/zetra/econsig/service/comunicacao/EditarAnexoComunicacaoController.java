package com.zetra.econsig.service.comunicacao;

import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.TipoArquivoEnum;

/**
 * <p>Title: EditarAnexoComunicacaoController</p>
 * <p>Description: Session Bean para operação de edição de anexo de comunicação.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public interface EditarAnexoComunicacaoController {

    public void createAnexoComunicacao(String cmnCodigo, String acmNome, String acmDescricao, TipoArquivoEnum tipoArquivo, String codePath, AcessoSistema responsavel) throws ZetraException;

    public void removeAnexoComunicacao(CustomTransferObject cto, AcessoSistema responsavel) throws ZetraException;

    public int countAnexoComunicacao(String cmnCodigo, AcessoSistema responsavel) throws ZetraException;

    public int countAnexoComunicacao(CustomTransferObject cto, AcessoSistema responsavel) throws ZetraException;

    public List<TransferObject> lstAnexoComunicacao(CustomTransferObject cto, int offset, int rows, AcessoSistema responsavel) throws ZetraException;

}
