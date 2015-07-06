package de.brod.translate;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Andreas_2 on 04.07.2015.
 */
public class Translate {

    public static void main(String[] args) {
        // read a file
        try {

            if (!checkTranslator())
                return;
            HashSet<String> hsLangs = new HashSet<>();
            Hashtable<String, Hashtable<String, String>> htValues = new Hashtable<>();
            // list folders
            File rootFolder = new File("./CardManiac/res");
            for (File folder : rootFolder.listFiles()) {
                String folderName = folder.getName();
                if (folder.isDirectory() && folderName.matches("(values\\-.{2}|values)")) {

                    File file = new File(folder, "strings.xml");
                    if (!file.exists())
                        continue;
                    BufferedReader reader = new BufferedReader(new StringReader(read(file)));

                    String sLine;
                    Pattern compile = Pattern.compile("<string name=\"(.*?)\">(.*?)</string>");
                    while ((sLine = reader.readLine()) != null) {
                        Matcher matcher = compile.matcher(sLine);
                        if (matcher.find()) {
                            String name = matcher.group(1);
                            String value = matcher.group(2);
                            Hashtable<String, String> stringHashtable = htValues.get(folderName);
                            if (stringHashtable == null) {
                                stringHashtable = new Hashtable<>();
                                htValues.put(folderName, stringHashtable);
                            }
                            hsLangs.add(name);
                            stringHashtable.put(name, value);
                        }
                    }
                    reader.close();
                }
            }
            for (String name : htValues.keySet()) {
                if (name.contains("-")) {
                    Hashtable<String, String> hashtable = htValues.get(name);

                    StringBuilder sball = new StringBuilder();
                    System.out.println(">" + name + "<");
                    ArrayList<String> lstIds = new ArrayList<>();
                    for (String lang : hsLangs) {
                        if (lang.startsWith("la_"))
                            continue;
                        String sValue = hashtable.get(lang);
                        String slang = htValues.get("values").get(lang);
                        if (sValue != null) {
                            System.out.print(slang + ":");
                            System.out.println(sValue);
                        } else if (lang.length() > 0) {
                            lstIds.add(lang);
                            sball.append(slang + ".\n");
                        }
                    }
                    System.out.println("----------");
                    if (sball.length() > 0) {
                        System.out.println(sball.toString());
                        copyToClipboard(sball.toString());
                        System.out.print("wait " +
                                name + " > ");
                        System.out.println((char) System.in.read());
                        String s = readFromClipBoard();
                        String[] split = s.split("[\n\r]");
                        if (split.length == lstIds.size()) {

                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < lstIds.size(); i++) {
                                String sItem = split[i];
                                sItem = sItem.substring(0, sItem.length() - 1);
                                sb.append("    <string name=\"" + lstIds.get(i) + "\">"
                                        + sItem + "</string>\n");
                            }
                            File file = new File(rootFolder, name + "/strings.xml");
                            String readOld = read(file);
                            readOld = readOld.substring(0, readOld.lastIndexOf("<")) + sb.toString() + readOld.substring(readOld.lastIndexOf("<"));
                            write(readOld, file);
                            System.out.println(readOld);
                        } else {
                            System.err.println(split.length + "!=" + lstIds.size());
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readFromClipBoard() {
        try {
            return (String) Toolkit.getDefaultToolkit()
                    .getSystemClipboard().getData(DataFlavor.stringFlavor);
        } catch (Exception e) {
            return "";
        }
    }

    private static boolean checkTranslator() {
/*
        try {
            URL url = new URL("https://translate.google.de/");
            String sWsdl = read(url);
            System.out.println(sWsdl);

        } catch (Exception e) {
            e.printStackTrace();
        }
 */
        return true;
    }

    private static void copyToClipboard(String s) {
        StringSelection stringSelection = new StringSelection(s);
        Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
        clpbrd.setContents(stringSelection, null);
    }

    private static void write(String sWsdl, File file) throws IOException {
        System.out.println("written file " + file.getAbsolutePath());
        copy(new ByteArrayInputStream(sWsdl.getBytes("UTF-8")), new FileOutputStream(file));
    }

    private static String read(File file) throws IOException {
        return new String(read(new FileInputStream(file)), "UTF-8");
    }

    private static String read(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            return new String(read(urlConnection.getInputStream()), "UTF-8");
        } catch (IOException e) {

            return new String(read(urlConnection.getErrorStream()), "UTF-8");
        }
    }

    private static byte[] read(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        copy(in, out);
        return out.toByteArray();
    }

    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] b = new byte[4096];

        int iCount;
        while ((iCount = in.read(b)) > 0) {
            out.write(b, 0, iCount);
        }
        in.close();
    }
}
