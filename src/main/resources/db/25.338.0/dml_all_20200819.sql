-- DESENV-14275
-- Relatório Consignações

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP) VALUES ('consignacoes', 'campo_cor_multiplo', 1, 0, 0, 1, 0, 3, NULL, 1);
UPDATE tb_relatorio_filtro SET RFI_EXIBE_CSE = '0', RFI_EXIBE_ORG = '0', RFI_EXIBE_SUP = '0' WHERE REL_CODIGO = 'consignacoes' and TFR_CODIGO = 'campo_cor';

-- Relatório Sintético de Consignações

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP) VALUES ('sintetico', 'campo_cor_multiplo', 1, 0, 0, 1, 0, 3, NULL, 1);
UPDATE tb_relatorio_filtro SET RFI_EXIBE_CSE = '0', RFI_EXIBE_ORG = '0', RFI_EXIBE_SUP = '0' WHERE REL_CODIGO = 'sintetico' and TFR_CODIGO = 'campo_cor';

-- Relatório Movimento Financeiro

UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = RFI_SEQUENCIA + 1 WHERE REL_CODIGO = 'mov_mes' AND RFI_SEQUENCIA >= 3;
INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP) VALUES ('mov_mes', 'campo_cor_multiplo', 1, 0, 0, 1, 0, 3, NULL, 1);

-- Relatório Sintético Movimento Financeiro

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP) VALUES ('sintetico_mov_fin', 'campo_cor_multiplo', 1, 0, 0, 1, 0, 4, NULL, 1);
UPDATE tb_relatorio_filtro SET RFI_EXIBE_CSE = '0', RFI_EXIBE_ORG = '0', RFI_EXIBE_SUP = '0' WHERE REL_CODIGO = 'sintetico_mov_fin' and TFR_CODIGO = 'campo_cor';
