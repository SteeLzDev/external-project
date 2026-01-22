UPDATE tb_relatorio_filtro SET rfi_sequencia = rfi_sequencia + 1 WHERE rfi_sequencia >= 17 AND rel_codigo = 'consignacoes'; 

INSERT INTO tb_relatorio_filtro
(REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES('consignacoes', 'campo_vinculo_servidor', '1', '0', '0', '1', '0', 17, NULL, '1');