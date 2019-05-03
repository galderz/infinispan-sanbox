package org.infinispan.sandbox;

import org.infinispan.commons.marshall.WrappedByteArray;

import java.nio.charset.Charset;

public class ShowKeys {

    public static final Charset CHARSET = Charset.forName("UTF-8");

    public static void main(String[] args) {
        System.out.println(wrappedUtf8("game"));
        System.out.println(wrappedUtf8("OptaPlannerConfig"));
        System.out.println(wrappedUtf8("leaderboard"));
    }

    private static WrappedByteArray wrappedUtf8(String s) {
        final byte[] gameBytes = s.getBytes(CHARSET);
        return new WrappedByteArray(gameBytes);
    }

}
