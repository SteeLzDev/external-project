-- DESENV-14680 
-- Correção da ordem no relatorio de mov_mes:

UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = '6' WHERE REL_CODIGO = 'mov_mes' AND TFR_CODIGO = 'campo_org';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = '7' WHERE REL_CODIGO = 'mov_mes' AND TFR_CODIGO = 'campo_sub_orgao';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = '8' WHERE REL_CODIGO = 'mov_mes' AND TFR_CODIGO = 'campo_unidade';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = RFI_SEQUENCIA -1 WHERE REL_CODIGO = 'mov_mes' AND RFI_SEQUENCIA >= 4;  

-- mov_mes_csa:
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = RFI_SEQUENCIA + 2 WHERE REL_CODIGO = 'mov_mes_csa' AND RFI_SEQUENCIA >= 6; 

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES('mov_mes_csa', 'campo_sub_orgao', '0', '1', '0', '0', '0', 6, NULL, '0'); 

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES('mov_mes_csa', 'campo_unidade', '0', '1', '0', '0', '0', 7, NULL, '0'); 

-- UPDATE para dar acesso aos campos novos para CSA no relatório de Consignações.
UPDATE tb_relatorio_filtro SET RFI_EXIBE_CSA = '1' WHERE REL_CODIGO = 'consignacoes' and TFR_CODIGO IN ('campo_sub_orgao','campo_unidade');

-- UPDATE para dar acesso aos campos novos para CSA no relatório Sintético de consignações.
UPDATE tb_relatorio_filtro SET RFI_EXIBE_CSA = '1' WHERE REL_CODIGO = 'sintetico' and TFR_CODIGO IN ('campo_sub_orgao','campo_unidade');

-- UPDATE para dar acesso aos campos novos para CSA no relatório Sintético de Movimento financeiro CSA.
UPDATE tb_relatorio_filtro SET RFI_EXIBE_CSA = '1' WHERE REL_CODIGO = 'sintetico_mov_fin' and TFR_CODIGO IN ('campo_sub_orgao','campo_unidade');