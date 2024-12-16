package io.github.evanmi.distribute.lock.db.util;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class NetUtils {
    private final NetProperties netProperties;

    public NetUtils(NetProperties netProperties) {
        this.netProperties = netProperties;
    }

    public InetAddress findFirstNonLoopbackAddress() {
        InetAddress result = null;
        try {
            for (Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
                 networkInterfaces.hasMoreElements(); ) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if (networkInterface.isUp()) {
                    if (!ignoreInterface(networkInterface.getDisplayName())) {
                        for (Enumeration<InetAddress> addresses = networkInterface
                                .getInetAddresses(); addresses.hasMoreElements(); ) {
                            InetAddress address = addresses.nextElement();
                            if (address instanceof Inet4Address
                                    && !address.isLoopbackAddress()
                                    && isPreferredAddress(address)) {
                                result = address;
                            }
                        }
                    }
                }
            }
        } catch (IOException exception) {
            //ignored
        }
        if (result != null) {
            return result;
        }
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException("Unable to retrieve localhost");
        }
    }

    boolean isPreferredAddress(InetAddress address) {
        final List<String> preferredNetworks = this.netProperties.getPreferredNetworks();
        if (preferredNetworks.isEmpty()) {
            return true;
        }
        for (String regex : preferredNetworks) {
            final String hostAddress = address.getHostAddress();
            if (hostAddress.matches(regex) || hostAddress.startsWith(regex)) {
                return true;
            }
        }
        return false;
    }

    boolean ignoreInterface(String interfaceName) {
        List<String> ignoredInterfaces = this.netProperties.getIgnoredInterfaces();
        for (String regex : ignoredInterfaces) {
            if (interfaceName.matches(regex)) {
                return true;
            }
        }
        return false;
    }

    public static class NetProperties {
        public NetProperties() {
        }

        public NetProperties(List<String> preferredNetworks, List<String> ignoredInterfaces) {
            this.preferredNetworks = preferredNetworks;
            this.ignoredInterfaces = ignoredInterfaces;
        }

        private List<String> preferredNetworks = new ArrayList<>();

        private List<String> ignoredInterfaces = new ArrayList<>();


        public List<String> getPreferredNetworks() {
            return preferredNetworks;
        }

        public void setPreferredNetworks(List<String> preferredNetworks) {
            this.preferredNetworks = preferredNetworks;
        }

        public List<String> getIgnoredInterfaces() {
            return ignoredInterfaces;
        }

        public void setIgnoredInterfaces(List<String> ignoredInterfaces) {
            this.ignoredInterfaces = ignoredInterfaces;
        }
    }
}
