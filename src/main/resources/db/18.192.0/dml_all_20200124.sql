-- DESENV-9268
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/alongarConsignacao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'detalhar' WHERE ACR_CODIGO IN ('10325', '10326', '10327', '10328', '12056');
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/alongarConsignacao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'incluirReserva' WHERE ACR_CODIGO IN ('10509', '10510', '10511', '10512', '12108');  
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/alongarConsignacao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'confirmarDuplicidade' WHERE ACR_CODIGO IN ('15062', '15063', '15064', '15065', '15066'); 
