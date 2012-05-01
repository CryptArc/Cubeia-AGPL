package com.cubeia.games.poker.debugger;

import java.net.InetAddress;

public class IpChecker {

    public static void main(String args[]) {
        try {
            InetAddress ownIP = InetAddress.getLocalHost();
            System.out.println("IP of my system is := " + ownIP.getHostAddress());
        } catch (Exception e) {
            System.out.println("Exception caught =" + e.getMessage());
        }
    }
}
