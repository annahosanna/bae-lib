package org.gtri.gfipm.bae.util

import gtri.logging.Logger
import gtri.logging.LoggerFactory

import javax.net.ssl.X509KeyManager
import java.security.Principal
import java.security.PrivateKey
import java.security.cert.X509Certificate

/**
 * Created with IntelliJ IDEA.
 * User: brad
 * Date: 5/28/13
 * Time: 11:18 AM
 * To change this template use File | Settings | File Templates.
 */
class ClientKeyManager implements X509KeyManager {

    static final Logger logger = LoggerFactory.get(ClientKeyManager)

    private static final String clientAlias = "client-alias_"+UUID.randomUUID().toString();

    private PrivateKey privateKey;
    private X509Certificate cert;

    public ClientKeyManager(PrivateKey newPrivateKey, X509Certificate newCert) {
        privateKey = newPrivateKey;
        cert = newCert;
    }

    @Override
    String[] getClientAliases(String keyType, Principal[] issuers) {
        System.out.println("getClientAliases");
        return [clientAlias] as String[];
    }

    @Override
    String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
        logger.debug("Choosing client alias: [keytype=$keyType, socket: @|green ${socket.getInetAddress()}|@, issuers count=${issuers?.length}]")
        issuers?.each{ issuer ->
            logger.debug("    Issuer: @|green ${issuer.getName()}|@")
        }
        logger.info("Returning client alias[@|cyan $clientAlias|@] (which is @|green ${cert.subjectDN}|@)...")
        return clientAlias;
    }

    @Override
    String[] getServerAliases(String keyType, Principal[] issuers) {
        System.out.println("getServerAliases");
        return null;
    }

    @Override
    String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
        System.out.println("chooseServerAlias");
        return null;
    }

    @Override
    X509Certificate[] getCertificateChain(String alias) {
        logger.debug("Getting certificate chain for alias[@|cyan $alias|@]...");
        return [cert] as X509Certificate[];
    }

    @Override
    PrivateKey getPrivateKey(String alias) {
        logger.debug("Returning private key for alias[@|cyan $alias|@]...")
        return privateKey;
    }
}
