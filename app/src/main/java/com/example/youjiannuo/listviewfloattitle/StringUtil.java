package com.example.youjiannuo.listviewfloattitle;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    /**
     * <p/>
     * <p/>
     * 例如：StringUtil.substring("richer",4) 得到的结果为：rich
     * <p/>
     * 得到字符串指定长度字符
     *
     * @param str  字符串
     * @param len： 要得到的字符个数
     * @return 截取后得到的字符
     */
    public static String substring(String str, int len) {
        if (str == null || len < 1) {
            return null;
        } else if (str.length() < len) {
            return str;
        } else {
            return str.substring(0, len);
        }
    }

    public static String getStrSeparated(List<String> set, String sep) {
        if (set == null || set.size() == 0) return "";
        Iterator<String> iterator = set.iterator();
        String ids = "";
        while (iterator.hasNext()) {
            ids += iterator.next() + sep;
        }
        return ids.substring(0, ids.length() - sep.length());
    }

    //获取到过来一下需要放入sql差选的语句
    public static String filterSqlString(String sql) {
        String filters[] = {"'", "\""};
        for (String filter : filters) {
            sql = sql.replaceAll(filter, "");
        }
        return sql;
    }

    public static String getStrSeparated(String[] args, String sep) {
        return getStrSeparated(args, sep, null);
    }

    public static String getStrSeparated(String[] args, String sep, String moveItem) {
        if (args == null || args.length == 0) return "";
        String result = "";
        boolean isMove = !StringUtil.isEmpty(moveItem);
        for (String s : args) {
            if (isMove) {
                if (moveItem.contains(s)) {
                    continue;
                }
            }
            result += s + sep;
        }

        return result.substring(0, result.length() - sep.length());
    }


    public static String changeENComma2CHComma(String s) {
        if (isEmpty(s)) return "";
        String arr[] = s.split(",");
        String name = "";
        for (String a : arr) {
            name += a + "，";
        }
        if (!StringUtil.isEmpty(name)) {
            return name.substring(0, name.length() - 1);
        }
        return name;
    }

    public static String getArraySeparate(String sep, String... args) {
        if (args == null || args.length == 0) return "";
        String result = "";
        for (String s : args) {
            if (!isEmpty(s)) {
                result += s + sep;
            }
        }
        return StringUtil.isEmpty(result) ? "" : result.substring(0, result.length() - sep.length());
    }

    public static String getListSeparated(List<String> args, String sep) {
        if (args == null || args.size() == 0) return "";
        String result = "";
        for (String s : args) {
            result += s + sep;
        }

        return result.substring(0, result.length() - sep.length());
    }


    public static String getStrings(String s) {
        return isEmpty(s) ? "" : s;
    }

    public static String getString(String s) {
        return isEmpty(s) ? "" : filterEmoji(s);
    }


    private static String filterEmoji1(String source) {
        Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]", Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        Matcher emojiMatcher = emoji.matcher(source);
        if (emojiMatcher.find()) {
            source = emojiMatcher.replaceAll("");
            return source;
        }
        return source;
    }

    /**
     * 是否包含表情
     *
     * @param codePoint
     * @return 如果不包含 返回false,包含 则返回true
     */

    private static boolean isEmojiCharacter(char codePoint) {
        return !((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)
                || (codePoint == 0xD)
                || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)))
                || (codePoint >= 0xD913 && codePoint <= 0xDD24);
    }

    /**
     * 过滤emoji 或者 其他非文字类型的字符
     *
     * @param source
     * @return
     */
    public static String filterEmoji(String source) {
        if (isEmpty(source)) {
            return "";
        }
        boolean is = false;
        StringBuilder buf = new StringBuilder();
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) {// 如果不包含 则将字符append

                buf.append(codePoint);
            }
        }
        if (buf != null && buf.length() != len) {// 这里的意义在于尽可能少的toString，因为会重新生成字符串
            source = buf.toString();
        }
        return filterEmoji1(source);
    }


    public static String getEncode(String str) {
        //System.out.println(str);
        String strc = null;
        try {
            strc = new String(str.getBytes("ISO-8859-1"), "gb2312");
        } catch (UnsupportedEncodingException c) {

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return strc;
    }

    /**
     * 判断是否为数字
     *
     * @param str
     * @return
     */

    public static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }


    /**
     * 根据时间用于生成自定义文件
     * 例如：StringUtil.formatDateHtml("2006-10-10 23:22:09",null)结果为：20061010232209
     * StringUtil.formatDateHtml("2006-10-10 23:22:09",".html")结果为：20061010232209.html
     * <p/>
     * 用于生成随机的HTML文件
     *
     * @param str 日期字符串
     * @param s   文件的后缀名,注意前面加“.”
     * @return 返回格式化完成后的字符串
     */
    public static String formatDateHtml(String str, String s) {
        StringBuffer re = new StringBuffer();
        java.util.StringTokenizer analysis = new java.util.StringTokenizer(str, "- :");
        while (analysis.hasMoreTokens()) {
            re.append(analysis.nextToken());
        }
        if (!(s == null)) {
            re.append(s);
        }
        return re.toString();
    }

    /**
     * <p/>
     * 把字符打散转换成UTF-8的格式，用于解决网页上汉字乱码
     *
     * @param str 字符串
     * @return 截取后得到的字符
     */

    public static String getEncodeStr(String str) {
        String strUtf8 = null;
        try {
            strUtf8 = new String(str.getBytes("ISO-8859-1"), "utf-8");
        } catch (UnsupportedEncodingException e) {

        }
        return strUtf8;
    }

    /**
     * <p/>
     * 把字符打散转换成自定义的格式，用于解决网页上汉字乱码
     *
     * @param str  字符串
     * @param from 字符集名
     * @param to   字符集名
     * @return 截取后得到的字符
     */

    public static String getEncodeStr(String str, String from, String to) {
        String strUtf8 = null;
        try {
            strUtf8 = new String(str.getBytes(from), to);
        } catch (UnsupportedEncodingException e) {

        }
        return strUtf8;
    }

    /**
     * <p/>
     * 类似ORACLE中的nvl函数，如果str为空，就把d的值赋给str
     *
     * @param str 字符串
     * @param d   字符串
     * @return
     */
    public static String nvl(String str, String d) {
        return str == null ? d : str;
    }

    /**
     * <p/>
     * 检查字符串是否为空或空白
     *
     * @param param 字符串
     * @return
     */
    public static boolean isEmpty(Object param) {
        if (param == null) return true;
        if (param instanceof Collection) {
            Collection collection = (Collection) param;
            return collection.size() == 0;
        }
        return param.equals("");
    }

    public static String removeLastChar(String s) {
        return !isEmpty(s) ? s.substring(0, s.length() - 1) : "";
    }


    public static String getRightPhone(String number) {
        if (!StringUtil.isEmpty(number) &&
                number.length() == 13 &&
                number.indexOf("86") == 0) {
            number = "+" + number;
        }
        return number;
    }


    /**
     * 把String转换成double，JDK自带的有时候转换比较麻烦
     */
    public static double parseDouble(String param) {
        double d = 0;
        try {
            d = Double.parseDouble(param);
        } catch (Exception e) {
            //
        }
        return d;
    }

    /**
     * 把String转换成float，JDK自带的有时候转换比较麻烦
     */
    public static float parseFloat(String param) {
        float f = 0f;
        try {
            f = Float.parseFloat(param);
        } catch (Exception e) {
            //
        }
        return f;
    }

    /**
     * 把String转换成int，JDK自带的有时候转换比较麻烦
     */
    public static int parseInt(String param) {
        int i;
        try {
            i = Integer.parseInt(param);
        } catch (Exception e) {
            i = (int) parseFloat(param);
        }
        return i;
    }

    public static String setAddress(String str) {
        String st = "";
        char[] cha = str.toCharArray();
        for (int i = 0; i < cha.length; i++) {
            if (cha[i] == '?') {
                st = st + " ";
            } else {
                st = st + st.valueOf(cha[i]);
            }

        }
        return st;
    }

    /**
     * 把String转换成long，JDK自带的有时候转换比较麻烦
     */
    public static long parseLong(String param) {
        long l = 0;
        try {
            l = Long.parseLong(param);
        } catch (Exception e) {
            l = (long) parseDouble(param);
        }
        return l;
    }

    private static char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz"
            + "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();

    private static Random randGen = new Random();

    /**
     * 例如：StringUtil.randomStr(5) 生成：Ja251
     * <p/>
     * 生成一个指定长度的随机字符串，使用于做文件名
     *
     * @param length 生成的长度
     * @return
     */
    public static final String randomStr(int length) {
        if (length < 1) {
            return null;
        }
        char[] randBuffer = new char[length];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
        }
        return new String(randBuffer);
    }

    /**
     * 例如：StringUtil.randomNum(5) 生成：06512
     * <p/>
     * 生成一个指定长度的随机数，使用于做文件名
     *
     * @param length 生成的长度
     * @return
     */
    public static final String randomNum(int length) {
        if (length < 1) {
            return null;
        }
        StringBuffer randBuffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            randBuffer.append(Integer.toString(randGen.nextInt(9)));
        }
        return randBuffer.toString();
    }

    /**
     * 将半角的符号转换成全角符号
     */
    public static String changeToFull(String str) {
        String source = "1234567890!@#$%^&*()abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_=+\\|[];:'\",<.>/?";
        String[] decode = {"１", "２", "３", "４", "５", "６", "７", "８", "９", "０",
                "！", "＠", "＃", "＄", "％", "︿", "＆", "＊", "（", "）", "ａ", "ｂ",
                "ｃ", "ｄ", "ｅ", "ｆ", "ｇ", "ｈ", "ｉ", "ｊ", "ｋ", "ｌ", "ｍ", "ｎ",
                "ｏ", "ｐ", "ｑ", "ｒ", "ｓ", "ｔ", "ｕ", "ｖ", "ｗ", "ｘ", "ｙ", "ｚ",
                "Ａ", "Ｂ", "Ｃ", "Ｄ", "Ｅ", "Ｆ", "Ｇ", "Ｈ", "Ｉ", "Ｊ", "Ｋ", "Ｌ",
                "Ｍ", "Ｎ", "Ｏ", "Ｐ", "Ｑ", "Ｒ", "Ｓ", "Ｔ", "Ｕ", "Ｖ", "Ｗ", "Ｘ",
                "Ｙ", "Ｚ", "－", "＿", "＝", "＋", "＼", "｜", "【", "】", "；", "：",
                "'", "\"", "，", "〈", "。", "〉", "／", "？"};

        String result = "";

        for (int i = 0; i < str.length(); i++) {
            int pos = source.indexOf(str.charAt(i));
            if (pos != -1) {
                result += decode[pos];
            } else {
                result += str.charAt(i);
            }
        }
        return result;
    }

    private final static String[] hexDigits = {
            "0", "1", "2", "3", "4", "5", "6", "7",
            "8", "9", "a", "b", "c", "d", "e", "f"};

    private static String byteArrayToHexString(byte[] b) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n = 256 + n;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    /**
     * 例如：StringUtil.MD5("richer") 生成：43091a5955ffeb31300b99c81bf90094
     * <p/>
     * 把字符串转换成MD5，使用于加密
     *
     * @param origin 字符串
     * @return
     */
    public static String md5(String origin) {
        String resultString = null;

        try {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteArrayToHexString(md.digest(resultString
                    .getBytes()));
        } catch (Exception ex) {

        }
        return resultString;
    }

    private static final char[] zeroArray = "0000000000000000".toCharArray();

    /**
     * 例如：StringUtil.zeroPadStr("1234",10) 生成：0000001234
     * <p/>
     * 把字符串转换成指定长度，不够在前面补0，适用于数据库ID补位
     *
     * @return
     */
    public static final String zeroPadStr(String string, int length) {
        if (string == null || string.length() > length) {
            return string;
        }
        StringBuffer buf = new StringBuffer(length);
        buf.append(zeroArray, 0, length - string.length()).append(string);
        return buf.toString();
    }

    private static final char[] GT_ENCODE = "&gt;".toCharArray();

    private static final char[] LT_ENCODE = "&lt;".toCharArray();

    /**
     * 例如：StringUtil.escapeHTMLTags("<P>richer</P>") 生成：&lt;P&gt;richer&lt;/P&gt;
     * <p/>
     * 把HTML中的字符进行转换，适用于网页
     *
     * @param in 字符串
     * @return
     */
    public static final String escapeHTMLTags(String in) {
        if (in == null) {
            return null;
        }
        char ch;
        int i = 0;
        int last = 0;
        char[] input = in.toCharArray();
        int len = input.length;
        StringBuffer out = new StringBuffer((int) (len * 1.3));
        for (; i < len; i++) {
            ch = input[i];
            if (ch > '>') {
                continue;
            } else if (ch == '<') {
                if (i > last) {
                    out.append(input, last, i - last);
                }
                last = i + 1;
                out.append(LT_ENCODE);
            } else if (ch == '>') {
                if (i > last) {
                    out.append(input, last, i - last);
                }
                last = i + 1;
                out.append(GT_ENCODE);
            }
        }
        if (last == 0) {
            return in;
        }
        if (i > last) {
            out.append(input, last, i - last);
        }
        return out.toString();
    }

    private static final String cvt =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                    + "abcdefghijklmnopqrstuvwxyz"
                    + "0123456789+/";
    private static final int fillchar = '=';

    private static String encodeBase64(byte[] data) {
        int c;
        int len = data.length;
        StringBuffer ret = new StringBuffer(((len / 3) + 1) * 4);
        for (int i = 0; i < len; ++i) {
            c = (data[i] >> 2) & 0x3f;
            ret.append(cvt.charAt(c));
            c = (data[i] << 4) & 0x3f;
            if (++i < len)
                c |= (data[i] >> 4) & 0x0f;

            ret.append(cvt.charAt(c));
            if (i < len) {
                c = (data[i] << 2) & 0x3f;
                if (++i < len)
                    c |= (data[i] >> 6) & 0x03;

                ret.append(cvt.charAt(c));
            } else {
                ++i;
                ret.append((char) fillchar);
            }

            if (i < len) {
                c = data[i] & 0x3f;
                ret.append(cvt.charAt(c));
            } else {
                ret.append((char) fillchar);
            }
        }
        return ret.toString();
    }

    /**
     * 把一个字符串转换成64位编码
     *
     * @param data 字符
     * @return
     */
    public static String encodeBase64(String data) {
        return encodeBase64(data.getBytes());
    }

    public static final String replace(String line, String oldString, String newString) {
        if (line == null) {
            return null;
        }
        int i = 0;
        if ((i = line.indexOf(oldString, i)) >= 0) {
            char[] line2 = line.toCharArray();
            char[] newString2 = newString.toCharArray();
            int oLength = oldString.length();
            StringBuffer buf = new StringBuffer(line2.length);
            buf.append(line2, 0, i).append(newString2);
            i += oLength;
            int j = i;
            while ((i = line.indexOf(oldString, i)) > 0) {
                buf.append(line2, j, i - j).append(newString2);
                i += oLength;
                j = i;
            }
            buf.append(line2, j, line2.length - j);
            return buf.toString();
        }
        return line;
    }

    /**
     * 去重复
     *
     * @param args
     * @return
     */
    public static List<String> toRepeatString(String[] args) {
        if (args.length == 1) {
            List<String> result = new ArrayList<>();
            result.add(args[0]);
            return result;
        }
        HashSet<String> set = new HashSet<>();
        Collections.addAll(set, args);
        List<String> result = new ArrayList<>();
        for (String aSet : set) {
            result.add(aSet);
        }
        return result;
    }

    public static String getEncodeStrl(String str) {
        String strUtf8 = null;
        try {
            strUtf8 = new String(str.getBytes("ISO-8859-1"), "gb2312");
        } catch (UnsupportedEncodingException e) {

        }
        return strUtf8;
    }

    public static String getSystemTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return formatter.format(curDate);
    }


    /**
     * 判断用户名是否为手机号码
     * <p/>
     * ^首字母 [1]必须是1，方格可以去掉。 [3-8]第二个数字为3-8之间 +加表示至少一个[3-8] \\d表示数字
     * {9}表示9个，就是9个数字
     *
     * @param phoneNum
     * @return
     */
    public static boolean isPhoneNum(String phoneNum) {
        String regExp = "^[1][3-9][0-9]{9}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(phoneNum);
        return m.find();
    }

    public static boolean isPhoneNum86(String phoneNum) {
        if (StringUtil.isEmpty(phoneNum)) {
            return false;
        }
        if (phoneNum.length() == 14 && phoneNum.indexOf("+86") == 0) {
            return isPhoneNum(phoneNum.substring(3, phoneNum.length()));
        } else if (phoneNum.length() == 13 && phoneNum.indexOf("86") == 0) {
            return isPhoneNum(phoneNum.substring(2, phoneNum.length()));
        }
        return false;
    }


    /**
     * 过滤手机号码
     *
     * @return
     */
    public static String getFilterPhoneNum(String num) {
        StringBuilder sb = new StringBuilder();
        for (char c : num.toCharArray()) {
            if (c >= '0' && c <= '9') {
                sb.append(c);
            }
        }
        return sb.toString();
    }


    /**
     * 判断用户名格式是否正确
     *
     * @param userName
     * @return
     */
    public static boolean isUserName(String userName) {
        String regExp = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$";// 身份证正则表达式(18位)...
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(userName);
        return m.find();
    }

    /**
     * 判断验证码格式否是正确
     *
     * @param
     * @return
     */
    public static boolean isYzmNum(String yzmNum) {
        String regExp = "[0-9]+";
        if (yzmNum.matches(regExp) && yzmNum.toString().length() >= 6) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断邮箱是否正确
     */

    public static boolean isEmail(String email) {

        String regExp = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        Pattern p = Pattern.compile(regExp);
        Matcher matcher = p.matcher(email);
        return matcher.matches();
    }

    /**
     * 判断是否是6位纯数字
     */

    public static boolean isSixNumber(String yzmNum) {
        String regExp = "[0-9]+";
        if (yzmNum.matches(regExp) && yzmNum.toString().length() == 6) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isLocalFile(String file) {
        return !isEmpty(file) && !isURL(file) && (file.indexOf("/storage") == 0 || file.contains("/system"));
    }

    public static boolean isURL(String url) {
        return !(url == null || url.length() == 0) && (url.trim().toLowerCase().startsWith("http://") || url.trim().toLowerCase().startsWith("https://"));
    }

}
