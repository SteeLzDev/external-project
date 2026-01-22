-- DESENV-17053
UPDATE tb_tipo_param_sist_consignante SET tpc_dominio = CONCAT('ESCOLHA[N=Não', 0x3b, 'S=Opcional', 0x3b, 'O=Obrigatório]'), tpc_vlr_default='N' where tpc_codigo='609';

UPDATE tb_modelo_email SET mem_texto='Prezado(a),<br><br> Os seguintes contratos foram rejeitados pela folha.<br><br><@tabela_noescape><br><b>Parcela rejeitada folha:</b>São parcelas que não foram descontadas no contracheque pela Folha de Pagamento.' WHERE mem_codigo='emailServidorContratosRejeitados';