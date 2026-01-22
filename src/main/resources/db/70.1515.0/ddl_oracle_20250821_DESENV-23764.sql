/*==============================================================*/
/* Table: tb_registro_ser_oculto_csa                            */
/*==============================================================*/
create table tb_registro_ser_oculto_csa  (
   rse_codigo           varchar2(32)                    not null,
   csa_codigo           varchar2(32)                    not null,
   constraint pk_registro_ser_oculto_csa primary key (rse_codigo, csa_codigo)
);

/*==============================================================*/
/* Index: idx_rse_roc_1                                         */
/*==============================================================*/
create index idx_rse_roc_1 on tb_registro_ser_oculto_csa (
   rse_codigo asc
);

/*==============================================================*/
/* Index: idx_csa_roc_1                                         */
/*==============================================================*/
create index idx_csa_roc_1 on tb_registro_ser_oculto_csa (
   csa_codigo asc
);

alter table tb_registro_ser_oculto_csa
   add constraint fk_rse_roc_1 foreign key (rse_codigo)
      references tb_registro_servidor (rse_codigo);

alter table tb_registro_ser_oculto_csa
   add constraint fk_csa_roc_1 foreign key (csa_codigo)
      references tb_consignataria (csa_codigo);

