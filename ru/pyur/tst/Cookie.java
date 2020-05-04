package ru.pyur.tst;


// https://en.wikipedia.org/wiki/HTTP_cookie
// https://tools.ietf.org/html/rfc6265

// java.time - from Java 8
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;


public class Cookie {

    public String name;
    public String value;

    // ---- set-cookie ---- //

    public String path;
    public String domain;
    public int expires;
    //max_age - not required

    public boolean secure;
    public boolean http_only;

    // Set-Cookie: SID=31d4d96e407aad42; Path=/; Domain=example.com
    // Set-Cookie: SID=31d4d96e407aad42; Path=/; Secure; HttpOnly
    // Set-Cookie: t=NjU1MDUsMSwxNTg4NTA3MDY5.UR5RTRA6PTeUgzqXdqKsmjyq6bfGMepGaFJAuhXEcws%3D; expires=Mon, 07-Apr-2025 11:57:49 GMT; Max-Age=155520000; path=/
    // Set-Cookie: lang=en-US; Expires=Wed, 09 Jun 2021 10:18:14 GMT

    // remove (expiration date in the past)
    // Set-Cookie: lang=; Expires=Sun, 06 Nov 1994 08:49:37 GMT

    //Set-Cookie: t=;expires=Чт, 25 дек 1969 07:35:07 MSK;path=/
    //Set-Cookie: t=;expires=Thu, 25 Dec 1969 07:43:57 MSK;path=/
    //Set-Cookie: t=NjU1MDUsMzYsMTU4ODU5NzIxOQ==.jVX/uxlk8fGKxA6frikL+X+cjb1YXw04wSKT38L9KVE=;expires=Mon, 05 Jan 1970 04:08:16 GMT;path=/


    public Cookie(String name, String value) {
        //System.out.println("new cookie: [" + name + "], [" + value + "]");
        this.name = name;
        this.value = value;
    }


    public Cookie(String name, String value, int expires, String path) {
        this.name = name;
        this.value = value;
        this.expires = expires;
        this.path = path;
    }


    public String getName() { return name; }

    public String getValue() { return value; }



    public String getStringExpires() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis((long)expires * 1000L);
        //Assign(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));

        //String COOKIE_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS zzz";
        // Set-Cookie: lang=en-US; Expires=Wed, 09 Jun 2021 10:18:14 GMT
        //String COOKIE_DATE_FORMAT = "EEE, dd MMM yyyy kk:mm:ss zzz a";
        String COOKIE_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
        SimpleDateFormat sdf = new SimpleDateFormat(COOKIE_DATE_FORMAT, Locale.US);  // "en-US"
        //System.out.println("Locale: " + Locale.US.toString());

        //sdf.setTimeZone(cal.getTimeZone());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        //System.out.println(sdf.format(cal.getTime()));

        String formatted = sdf.format(cal.getTime());
        System.out.println(expires + " : " + formatted);
        return formatted;
    }



    // ---- utilities ---- //
    // or do it automatically on parse/stringify

    public static String replacePlus(String src) {
        //todo: replace '+' > ' '
        return src;
    }
}