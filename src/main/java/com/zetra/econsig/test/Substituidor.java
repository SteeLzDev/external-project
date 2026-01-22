package com.zetra.econsig.test;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class Substituidor {
    private static final String ENCODING = "ISO-8859-1";

    public static void main(String[] args) throws IOException {
        String path = "/home/igor/WorkDir/Zetra/Desenvolvimento_eConsig/src/main/java/com/zetra/";
        substituir(path);
    }

    public static void substituir(String path) throws IOException {
        File dir = new File(path);
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                substituir(file.getAbsolutePath());
                continue;
            }

            System.out.println(file);
            String content = FileUtils.readFileToString(file, ENCODING);

            /*
            // private Set comunicacaoCseSet = new HashSet(0);
            content = content.replaceAll("private Set ([a-zA-Z]+)Set = new HashSet\\(0\\);", "private Set<$1> $1Set = new HashSet<$1>(0);");
            for(char c = 'a'; c <='z'; c++) {
                content = content.replaceAll("Set<" + c, "Set<" + String.valueOf(c).toUpperCase());
            }

            // public Set getComunicacaoCseSet() {
            content = content.replaceAll("public Set get([a-zA-Z]+)Set\\(\\) \\{", "public Set<$1> get$1Set() {");

            // public void setComunicacaoCseSet(Set comunicacaoCseSet) {
            content = content.replaceAll("public void set([a-zA-Z]+)Set\\(Set ([a-zA-Z]+)Set\\) \\{", "public void set$1Set(Set<$1> $2Set) {");
            */

            content = content.replaceAll("List ([a-zA-Z]+) = ([a-zA-Z]+)\\.executarDTO\\(\\);", "List<TransferObject> $1 = $2.executarDTO();");

            FileUtils.writeStringToFile(file, content, ENCODING);
        }
    }
}
