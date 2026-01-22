package com.zetra.econsig.service.consignacao;

import java.util.Date;
import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.AnexoAutorizacaoDesconto;
import com.zetra.econsig.values.TipoArquivoEnum;

/**
 * <p>Title: EditarAnexoConsignacaoController</p>
 * <p>Description: Session Bean para operação de edição de anexo de consignação.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface EditarAnexoConsignacaoController {

    public AnexoAutorizacaoDesconto findAnexoAutorizacaoDesconto(String adeCodigo, String aadNome, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public int countAnexoAutorizacaoDesconto(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public int countAnexoAutorizacaoDesconto(CustomTransferObject cto, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> lstAnexoAutorizacaoDesconto(CustomTransferObject cto, int offset, int rows, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void createAnexoAutorizacaoDesconto(String adeCodigo, String aadNome, String aadDescricao, java.sql.Date aadPeriodo, TipoArquivoEnum tipoArquivo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void createAnexoAutorizacaoDesconto(String adeCodigo, String aadNome, String aadDescricao, java.sql.Date aadPeriodo, TipoArquivoEnum tipoArquivo, String aadExibeSup, String aadExibeCse, String aadExibeOrg, String aadExibeCsa, String aadExibeCor, String aadExibeSer, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void updateAnexoAutorizacaoDesconto(CustomTransferObject cto, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void removeAnexoAutorizacaoDesconto(CustomTransferObject cto, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void removeAnexoAutorizacaoDescontoTemp(CustomTransferObject cto, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<AnexoAutorizacaoDesconto> lstAnexoTipoArquivoPeriodo(String adeCodigo, List<String> tarCodigos, Date periodo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> lstAnexoMaxPeriodo(List<String> tarCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<AnexoAutorizacaoDesconto> lstAnexoTipoArquivoMaxPeriodo(String adeCodigo, List<String> tarCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException;
}
