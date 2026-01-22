package com.zetra.econsig.service.folha;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.parametros.ReservarMargemParametros;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ImportaHistoricoControllerBean</p>
 * <p>Description: Session Façade Remote para Rotina de Importação de Arquivo de Histórico</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ImportaHistoricoController {

    public void importaLinha(Map<String, Object> entrada, List<Map<String, Object>> adeInsereParcelas, List<String> critica, String linha,
            String cseCodigo, String hoje,
            boolean validaReserva, boolean permitirValidacaoTaxa,
            boolean serAtivo, boolean cnvAtivo, boolean svcAtivo, boolean serCnvAtivo,
            boolean csaAtivo, boolean orgAtivo, boolean estAtivo, boolean cseAtivo,
            boolean importaDadosAde, boolean retornaAdeNum,
            boolean importacaoSemProcessamento, boolean selecionaPrimeiroCnvDisponivel, Map<String, Map<String, Object>> cacheConvenio,
            ReservarMargemParametros paramAvancados, HashMap<String, TransferObject> cachePlanos, AcessoSistema responsavel);

    public void gerarHistoricoTeste(int qtdRse, int qtdAde, int matriculaInicial, boolean criarParcelas, String nseCodigo, AcessoSistema responsavel) throws ConsignanteControllerException;

    public void gerarHistoricoTesteOrientado(int pctRse, int qtdAdePorRse, boolean criarParcelas, String nseCodigo, AcessoSistema responsavel) throws ConsignanteControllerException;

    public void apagarHistoricoTesteOrientado(AcessoSistema responsavel) throws ConsignanteControllerException;
}
