-- DESENV-22338
-- @@delimiter = !

UPDATE tb_tipo_param_sist_consignante
SET TPC_DOMINIO='ESCOLHA[0=Não;1=SMS;2=Email;3=SMS/Email;4=Push notif;5=SMS/Email/Push notif;6=Email/Push notif]' WHERE tpc_codigo='961'
!