package AnnCoding.Utils;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * @author chenjiena
 * @version 1.0
 * @created 2020/6/20.
 */
public final class ReflectUtil {

    private static final Pattern pattern = Pattern.compile("[0-9]*");

    public static final Character MARK = '1';

    public ReflectUtil() {
    }

    /**
     * \n 回车(\u000a)
     * \t 水平制表符(\u0009)
     * \s 空格(\u0008)
     * \r 换行(\u000d)
     */
    private static String replaceAllBlank(String str) {
        String s = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            s = m.replaceAll("");
        }
        return s;
    }

    public static String getSeq(String url) {
        url = replaceAllBlank(url);
        String moneyText = "";
        Pattern p = Pattern.compile("seq=" + "[0-9]*");
        Matcher m = p.matcher(url);
        //如果字符串中存在这种类型的字符串就把这个字符串截取出来
        if (m.find()) {
            moneyText = m.group();
            int indexOf = moneyText.indexOf("seq=");
            //截取出字符串后面的数字
            moneyText = moneyText.substring(indexOf + 4, moneyText.length());
        }
        return moneyText;
    }

    public static void main(String[] args) {
        String s = "RmbField(seq = 22, title = \"ecifNo\", remark = \"ecif号\")";
        System.out.println(s);
        System.out.println(getSeq(s));
    }

    public static boolean isNumeric(String str) {
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }
}
