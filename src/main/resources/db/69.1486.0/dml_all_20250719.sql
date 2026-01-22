-- DESENV-23291
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = RFI_SEQUENCIA + 1 WHERE REL_CODIGO = 'mov_mes_csa' AND RFI_SEQUENCIA >= 8;

UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = RFI_SEQUENCIA + 1 WHERE REL_CODIGO = 'sintetico_mov_fin' AND RFI_SEQUENCIA >= 8;

INSERT INTO tb_relatorio_filtro  (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA) 
VALUES ('mov_mes_csa', 'campo_natureza_svc', '0', '1', '0', '0', '0', '8');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA) 
VALUES ('sintetico_mov_fin', 'campo_natureza_svc', '0', '1', '0', '0', '0', '8');

