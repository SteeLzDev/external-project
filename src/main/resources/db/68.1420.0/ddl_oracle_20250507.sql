/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     01/04/2025 10:36:29                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_perfil_consignado_csa                              */
/*==============================================================*/
create table tb_perfil_consignado_csa  (
   pcc_codigo           varchar2(32)                    not null,
   csa_codigo           varchar2(32)                    not null,
   pcc_pontuacao_inferior integer                         not null,
   pcc_pontuacao_superior integer                         not null,
   pcc_perfil           varchar2(100)                   not null,
   constraint pk_tb_perfil_consignado_csa primary key (pcc_codigo)
);

/*==============================================================*/
/* Index: r_984_fk                                              */
/*==============================================================*/
create index r_984_fk on tb_perfil_consignado_csa (
   csa_codigo asc
);

alter table tb_perfil_consignado_csa
   add constraint fk_tb_perfi_r_984_tb_consi foreign key (csa_codigo)
      references tb_consignataria (csa_codigo);

