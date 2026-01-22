-- DESENV-18581
update tb_item_menu set itm_descricao = substr(itm_descricao, 14) where mnu_codigo = '2' and itm_descricao like 'Relatorio de %';
update tb_item_menu set itm_descricao = substr(itm_descricao, 11) where mnu_codigo = '2' and itm_descricao like 'Relatorio %';