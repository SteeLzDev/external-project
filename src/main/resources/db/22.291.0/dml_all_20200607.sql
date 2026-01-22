-- DESENV-9281
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/reimplantarCapitalDevido', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciarReimplantacao' WHERE ACR_CODIGO in ('14067', '14068', '14069', '14070', '14071');
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/reimplantarCapitalDevido', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'confirmarReimplantacao' WHERE ACR_CODIGO in ('14072', '14073', '14074', '14075', '14076');
