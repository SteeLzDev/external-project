-- DESENV-14127
INSERT INTO tb_tipo_filtro_relatorio (TFR_CODIGO, TFR_DESCRICAO, TFR_RECURSO, TFR_EXIBE_EDICAO) 
VALUES ('campo_tarifacao_por_cor', 'Detalhar por Correspondente', '/relatorios/campos_relatorio/campo_tarifacao_por_correspondente.jsp', 'N');

-- Atualiza posição dos campos antigos
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = RFI_SEQUENCIA + 1 WHERE REL_CODIGO = 'vlr_recebimento' AND RFI_SEQUENCIA >= 6;

-- Cria o novo filtro para o relatório
INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP) 
VALUES ('vlr_recebimento', 'campo_tarifacao_por_cor', '0', '0', '0', '0', '0', '6', NULL, '1');
