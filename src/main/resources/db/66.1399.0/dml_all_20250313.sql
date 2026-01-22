-- DESENV-22762
UPDATE tb_relatorio 
SET REL_TITULO = 'Relatório Termo de Uso, Adesão e Privacidade', REL_TEMPLATE_SUBRELATORIO = 'CHART-GRID.jasper, CHART-GRID-LINE.jasper, TermoAdesaoNaoAutorizado.jasper, TermoUsoPrivacidadeAdesaoAutorizado.jasper' 
WHERE REL_CODIGO = 'termo_uso_privacidade';

UPDATE tb_item_menu SET ITM_DESCRICAO = 'Termo de Uso, Adesão e Privacidade' WHERE ITM_CODIGO = '229';

