-- DESENV-19633
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = RFI_SEQUENCIA + 1 WHERE REL_CODIGO = 'market_share_csa' AND RFI_SEQUENCIA >= 3;

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_EXIBE_SUP)
VALUES ('market_share_csa', 'campo_csa_multiplo', '1', '0', '0', '0', '0', 3, '1');

