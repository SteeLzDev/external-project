/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     26/04/2022 09:05:50                          */
/*==============================================================*/


alter table tb_consignante add bco_codigo smallint;
alter table tb_consignante add cse_sistema_folha varchar2(100);

/*==============================================================*/
/* Index: r_866_fk                                              */
/*==============================================================*/
create index r_866_fk on tb_consignante (
   bco_codigo asc
);

alter table tb_consignante
   add constraint fk_tb_consi_r_866_tb_banco foreign key (bco_codigo)
      references tb_banco (bco_codigo);

