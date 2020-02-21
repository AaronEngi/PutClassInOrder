package wang.tyrael.putclassinorder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class PutClassInOrder {
    private static final String SOURCE_ROOT = "java";

    private final String rootDir;

    public PutClassInOrder(String rootDir) {
        this.rootDir = rootDir;
    }

    public static void main(String[] args) {
        String rootDir = "E:\\talkbackProject";
        new PutClassInOrder(rootDir).put();
    }

    private void put() {
        putDir(new File(rootDir), null);
    }

    private void putDir(File dir, File sourceRootDir) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                System.out.println("dir name:" + file.getName());
                if (sourceRootDir == null && file.getName().equals(SOURCE_ROOT)) {
                    sourceRootDir = file;
                }
                putDir(file, sourceRootDir);
            } else if (file.getName().endsWith(".java")) {
                putFile(file, sourceRootDir);
            }
        }
    }

    private void putFile(File file, File sourceRootDir) {
        System.out.println("putFile file = " + file + ", sourceRootDir = " + sourceRootDir);
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (scanner == null) {
            return;
        }
        String packageLine = null;
        while (scanner.hasNext()) {
            String line = scanner.nextLine().trim();
            if (line.startsWith("package ")) {
                packageLine = line;
                scanner.close();
                System.out.println(line);
                break;
            }
        }
        String packagePath = packageLine.substring(packageLine.indexOf(' '), packageLine.indexOf(';')).trim();
        String subPath = packagePath.replace('.', '/');
        File destDir = new File(sourceRootDir, subPath);
        File destFile = new File(sourceRootDir, subPath + "/" + file.getName());
        if (file.equals(destFile)){
            return;
        }
        boolean success = false;
        if (destFile.exists()) {
            //for repeat run this program.
            boolean result = file.delete();
            if (result){
                System.out.println("delete success:" + file.getAbsolutePath());
            }else{
                System.out.println("delete failed:" + file.getAbsolutePath());
            }
            System.out.println();
        } else {
            try {
                System.out.println(String.format("try to move %s to %s", file.getCanonicalPath(), destFile.getCanonicalPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            destDir.mkdirs();
            try {
                Files.copy(Path.of(file.getAbsolutePath()), Path.of(destFile.getAbsolutePath()));
                System.out.println("succeeded to rename.\n");
                file.delete();
                System.out.println("delete success:" + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("failed to rename.\n");
            }

//                    success = file.renameTo(destFile);
//                    if (success) {
//                        System.out.println("succeeded to rename.\n");
//                    } else {
//                        System.out.println("failed to rename.\n");
//                    }
    }
}
}
