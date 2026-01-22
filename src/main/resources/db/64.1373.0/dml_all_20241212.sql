-- DESENV-22630
INSERT INTO tb_tipo_filtro_relatorio (TFR_CODIGO, TFR_DESCRICAO, TFR_RECURSO, TFR_EXIBE_EDICAO)
VALUES ('campo_ade_sem_lancamento', 'Reservas de cartão sem lançamento', '/relatorios/campos_relatorio/campo_ade_sem_lancamento.jsp', 'N');

INSERT INTO tb_tipo_filtro_relatorio (TFR_CODIGO, TFR_DESCRICAO, TFR_RECURSO, TFR_EXIBE_EDICAO)
VALUES ('campo_ade_portabilidade_cartao', 'Reservas aptas à portabilidade', '/relatorios/campos_relatorio/campo_ade_portabilidade_cartao.jsp', 'N');

UPDATE tb_relatorio_filtro
SET RFI_SEQUENCIA = RFI_SEQUENCIA + 2
WHERE TFR_CODIGO IN ('campo_agendado', 'campo_data_execucao', 'campo_tipo_agendamento', 'campo_periodicidade', 'campo_envio_email', 'campo_formato_relatorio')
AND REL_CODIGO = 'provisionamento_margem';

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('provisionamento_margem', 'campo_ade_sem_lancamento', '1', '1', '0', '0', '0', 6, NULL, '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('provisionamento_margem', 'campo_ade_portabilidade_cartao', '1', '1', '0', '0', '0', 7, NULL, '1');

