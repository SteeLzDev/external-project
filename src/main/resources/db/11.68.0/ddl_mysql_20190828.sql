/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     26/08/2019 14:49:15                          */
/*==============================================================*/

alter table tb_menu
   add MNU_IMAGEM varchar(50);

alter table tb_item_menu
   add ITM_IMAGEM varchar(50);

alter table tb_item_menu
   add TEX_CHAVE varchar(200);

alter table tb_item_menu add constraint FK_R_771 foreign key (TEX_CHAVE)
      references tb_texto_sistema (TEX_CHAVE) on delete restrict on update restrict;
