package com.zetra.econsig.webservice.rest.request;
 
import java.math.BigDecimal;
import java.util.List;
 
public class ConsultarCPFResponse {
    public String cpf;
    public List<ItemMatricula> matriculas;
    public static class ItemMatricula {
        public String matricula;             // ex.: RSE_MATRICULA
        public String orgao;                 // ex.: RSE_ORGAO / RSE_ORGAO_LOTACAO
        public String cargo;                 // CRS_DESCRICAO
        public String municipioLotacao;      // RSE_MUNICIPIO_LOTACAO
        public SalarioStatus salario;        // FOLHA, CALCULAVEL, NAO_CALCULAVEL
        public String dataReferencia;        // yyyy-MM-dd (RSE_DATA_ULT_SALARIO) se houver
        public String dataAdmissao;          // yyyy-MM-dd
        public String dataPagamento;         // yyyy-MM-dd (igual RSE_DATA_ULT_SALARIO)
        public String anoMes;                // yyyy-MM
        // Se não calculável, informar motivo
        public MotivoNaoCalculavel motivoNaoCalculavel; // SEM_MARGEM, PERCENTUAL_NAO_CADASTRADO
        // (Opcional) devolver números quando fizer sentido:
        public BigDecimal salarioFolha;      // RSE_SALARIO, se houver
        public BigDecimal proventos;         // RSE_PROVENTOS
        public BigDecimal compulsorios;      // RSE_DESCONTOS_COMP
        public BigDecimal facultativos;      // RSE_DESCONTOS_FACU
        public BigDecimal outros;            // RSE_OUTROS_DESCONTOS
        // (Opcional) prévia de cálculo quando CALCULAVEL
        public BigDecimal salarioCalculado;  // estimado pela margem, se você quiser expor
    }
    public enum SalarioStatus { FOLHA, CALCULAVEL, NAO_CALCULAVEL }
    public enum MotivoNaoCalculavel { SEM_MARGEM, PERCENTUAL_NAO_CADASTRADO }
}
 